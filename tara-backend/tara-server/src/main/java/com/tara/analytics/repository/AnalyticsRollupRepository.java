package com.tara.analytics.repository;

import com.tara.analytics.entity.AnalyticsRollup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsRollupRepository extends JpaRepository<AnalyticsRollup, UUID> {
    List<AnalyticsRollup> findByOrganizationIdAndBucketTimestampAfterOrderByBucketTimestampAsc(UUID organizationId, Instant after);
    List<AnalyticsRollup> findByPromptIdOrderByBucketTimestampAsc(UUID promptId);
}
