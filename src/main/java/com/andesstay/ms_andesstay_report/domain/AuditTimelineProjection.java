package com.andesstay.ms_andesstay_report.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Proyeccion de lectura del historial normalizado publicado en audit.timeline.
 * Complementa los KPIs calculados desde reservations.events con la dimensión
 * "quien / que / cuando" del evento de auditoria.
 */
@Entity
@Table(name = "AUDIT_TIMELINE_PROJECTIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditTimelineProjection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EVENT_ID", nullable = false, unique = true, length = 100)
    private String eventId;

    @Column(name = "RESERVATION_ID", nullable = false, length = 60)
    private String reservationId;

    @Column(name = "EVENT_TYPE", nullable = false, length = 60)
    private String eventType;

    @Column(name = "PREVIOUS_STATUS", length = 30)
    private String previousStatus;

    @Column(name = "NEW_STATUS", nullable = false, length = 30)
    private String newStatus;

    @Column(name = "ACTOR_ID", length = 60)
    private String actorId;

    @Column(name = "ACTOR_ROLE", length = 60)
    private String actorRole;

    @Column(name = "OCCURRED_AT", nullable = false)
    private Instant occurredAt;
}
