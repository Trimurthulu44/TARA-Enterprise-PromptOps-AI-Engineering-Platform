package com.tara.auth.service;

import com.tara.auth.dto.*;
import com.tara.auth.entity.RefreshToken;
import com.tara.auth.entity.Role;
import com.tara.auth.entity.User;
import com.tara.auth.entity.UserWorkspaceRole;
import com.tara.auth.repository.RefreshTokenRepository;
import com.tara.auth.repository.RoleRepository;
import com.tara.auth.repository.UserRepository;
import com.tara.auth.repository.UserWorkspaceRoleRepository;
import com.tara.common.context.TenantContextHolder;
import com.tara.common.exception.ResourceNotFoundException;
import com.tara.common.exception.UnauthorizedException;
import com.tara.common.exception.ValidationException;
import com.tara.common.security.JwtTokenProvider;
import com.tara.org.entity.Organization;
import com.tara.org.entity.Workspace;
import com.tara.org.repository.OrganizationRepository;
import com.tara.org.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserWorkspaceRoleRepository userWorkspaceRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OrganizationRepository organizationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final long accessTokenValidityMs;

    public AuthService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        UserWorkspaceRoleRepository userWorkspaceRoleRepository,
        RefreshTokenRepository refreshTokenRepository,
        OrganizationRepository organizationRepository,
        WorkspaceRepository workspaceRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider jwtTokenProvider,
        @Value("${tara.jwt.access-token-expiration-ms:86400000}") long accessTokenValidityMs
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userWorkspaceRoleRepository = userWorkspaceRoleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.organizationRepository = organizationRepository;
        this.workspaceRepository = workspaceRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.accessTokenValidityMs = accessTokenValidityMs;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("User with email " + email + " already exists");
        }

        User user = User.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName().trim())
            .isActive(true)
            .isEmailVerified(true)
            .build();
        user = userRepository.save(user);

        // Create Tenant Organization
        String orgSlug = generateSlug(request.getOrganizationName());
        Organization organization = Organization.builder()
            .name(request.getOrganizationName().trim())
            .slug(orgSlug)
            .tier("ENTERPRISE")
            .status("ACTIVE")
            .build();
        organization = organizationRepository.save(organization);

        // Create Default Workspace
        Workspace workspace = Workspace.builder()
            .name("Default Workspace")
            .slug("default")
            .isDefault(true)
            .build();
        workspace.setOrganizationId(organization.getId());
        workspace = workspaceRepository.save(workspace);

        // Assign Admin Role
        Role adminRole = roleRepository.findByName("ADMIN")
            .orElseGet(() -> roleRepository.save(Role.builder()
                .name("ADMIN")
                .description("Tenant Administrator")
                .isSystemRole(true)
                .build()));

        UserWorkspaceRole uwr = UserWorkspaceRole.builder()
            .userId(user.getId())
            .workspaceId(workspace.getId())
            .organizationId(organization.getId())
            .role(adminRole)
            .build();
        userWorkspaceRoleRepository.save(uwr);

        return buildAuthResponse(user, organization, workspace, adminRole.getName());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("Account has been deactivated. Please contact your enterprise administrator.");
        }

        UserWorkspaceRole uwr = userWorkspaceRoleRepository.findFirstByUserId(user.getId())
            .orElseThrow(() -> new UnauthorizedException("User has no active organization workspaces assigned"));

        Organization organization = organizationRepository.findById(uwr.getOrganizationId())
            .orElseThrow(() -> new ResourceNotFoundException("Organization", uwr.getOrganizationId()));

        Workspace workspace = workspaceRepository.findById(uwr.getWorkspaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Workspace", uwr.getWorkspaceId()));

        return buildAuthResponse(user, organization, workspace, uwr.getRole().getName());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findByTokenHashAndIsRevokedFalse(request.getRefreshToken())
            .orElseThrow(() -> new UnauthorizedException("Invalid or expired refresh token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            token.setIsRevoked(true);
            refreshTokenRepository.save(token);
            throw new UnauthorizedException("Refresh token has expired");
        }

        // Revoke current token for rotation
        token.setIsRevoked(true);
        refreshTokenRepository.save(token);

        User user = userRepository.findById(token.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", token.getUserId()));

        UserWorkspaceRole uwr = userWorkspaceRoleRepository.findFirstByUserId(user.getId())
            .orElseThrow(() -> new UnauthorizedException("User has no active workspace assigned"));

        Organization organization = organizationRepository.findById(uwr.getOrganizationId())
            .orElseThrow(() -> new ResourceNotFoundException("Organization", uwr.getOrganizationId()));

        Workspace workspace = workspaceRepository.findById(uwr.getWorkspaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Workspace", uwr.getWorkspaceId()));

        return buildAuthResponse(user, organization, workspace, uwr.getRole().getName());
    }

    public UserDto getCurrentUser() {
        UUID userId = TenantContextHolder.getRequiredUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        UserWorkspaceRole uwr = userWorkspaceRoleRepository.findFirstByUserId(user.getId()).orElse(null);
        String orgName = null;
        String wsName = null;
        String roleName = "VIEWER";
        UUID orgId = null;
        UUID wsId = null;

        if (uwr != null) {
            orgId = uwr.getOrganizationId();
            wsId = uwr.getWorkspaceId();
            roleName = uwr.getRole().getName();
            orgName = organizationRepository.findById(orgId).map(Organization::getName).orElse(null);
            wsName = workspaceRepository.findById(wsId).map(Workspace::getName).orElse(null);
        }

        return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .avatarUrl(user.getAvatarUrl())
            .role(roleName)
            .organizationId(orgId)
            .organizationName(orgName)
            .workspaceId(wsId)
            .workspaceName(wsName)
            .build();
    }

    private AuthResponse buildAuthResponse(User user, Organization org, Workspace ws, String role) {
        String accessToken = jwtTokenProvider.generateAccessToken(
            user.getId(), user.getEmail(), org.getId(), ws.getId(), role
        );

        String rawRefreshToken = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        RefreshToken rt = RefreshToken.builder()
            .userId(user.getId())
            .tokenHash(rawRefreshToken)
            .expiresAt(Instant.now().plusSeconds(7 * 24 * 3600))
            .isRevoked(false)
            .build();
        refreshTokenRepository.save(rt);

        UserDto userDto = UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .avatarUrl(user.getAvatarUrl())
            .role(role)
            .organizationId(org.getId())
            .organizationName(org.getName())
            .workspaceId(ws.getId())
            .workspaceName(ws.getName())
            .build();

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(rawRefreshToken)
            .tokenType("Bearer")
            .expiresIn(accessTokenValidityMs / 1000)
            .user(userDto)
            .build();
    }

    private String generateSlug(String input) {
        String base = input.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");
        if (base.isEmpty()) base = "org";
        String candidate = base;
        int counter = 1;
        while (organizationRepository.existsBySlug(candidate)) {
            candidate = base + "-" + counter++;
        }
        return candidate;
    }
}
