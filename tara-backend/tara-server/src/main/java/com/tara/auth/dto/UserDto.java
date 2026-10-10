package com.tara.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String role;
    private UUID organizationId;
    private String organizationName;
    private UUID workspaceId;
    private String workspaceName;
}
