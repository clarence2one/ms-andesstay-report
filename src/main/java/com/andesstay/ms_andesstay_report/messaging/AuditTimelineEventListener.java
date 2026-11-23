package com.andesstay.ms_andesstay_report.messaging;

import com.andesstay.ms_andesstay_report.domain.AuditTimelineProjection;
import com.andesstay.ms_andesstay_report.repository.AuditTimelineProjectionRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Segundo consumidor del sistema: lee audit.timeline con su propio consumer group,
 * asi que no compite con los grupos de reservations.events y ve el historial completo.
 */
@Component
public class AuditTimelineEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuditTimelineEventListener.class);

    private final AuditTimelineProjectionRepository repository;

    public AuditTimelineEventListener(AuditTimelineProjectionRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "${andesstay.kafka.topic-audit-timeline}",
            groupId = "ms-andesstay-report-audit-timeline",
            containerFactory = "auditTimelineKafkaListenerContainerFactory"
    )
    public void onAuditTimelineEntry(@Valid @Payload AuditTimelineEntry entry) {
        if (repository.existsByEventId(entry.eventId())) {
            log.info("Entrada {} ya proyectada en el timeline, se ignora el redelivery", entry.eventId());
            return;
        }

        repository.save(AuditTimelineProjection.builder()
                .eventId(entry.eventId())
                .reservationId(entry.reservationId())
                .eventType(entry.eventType())
                .previousStatus(entry.previousStatus())
                .newStatus(entry.newStatus())
                .actorId(entry.actorId())
                .actorRole(entry.actorRole())
                .occurredAt(entry.occurredAt())
                .build());
        log.info("Timeline proyectado: reserva {} -> {}", entry.reservationId(), entry.newStatus());
    }
}
