package com.andesstay.ms_andesstay_report.repository;

import com.andesstay.ms_andesstay_report.domain.AuditTimelineProjection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditTimelineProjectionRepository extends JpaRepository<AuditTimelineProjection, Long> {

    boolean existsByEventId(String eventId);
}
