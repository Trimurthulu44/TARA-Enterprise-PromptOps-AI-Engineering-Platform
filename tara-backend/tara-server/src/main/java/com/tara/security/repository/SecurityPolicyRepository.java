package com.tara.security.repository;

import com.tara.security.entity.CanaryToken;
import com.tara.security.entity.SecurityPolicy;
import com.tara.security.entity.SecurityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecurityPolicyRepository extends JpaRepository<SecurityPolicy, UUID> {
    List<SecurityPolicy> findByOrganizationId(UUID organizationId);
    Optional<SecurityPolicy> findFirstByOrganizationIdAndIsDefaultTrue(UUID organizationId);
}
