package com.andesstay.ms_andesstay_report.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Contrato tipado del segundo topico de negocio (audit.timeline), publicado por
 * ms-andesstay-audit. El eventId es el mismo del evento crudo de reservations.events,
 * por lo que sirve como clave de idempotencia al proyectar el historial.
 */
public record AuditTimelineEntry(
        @NotBlank String eventId,
        @NotBlank String reservationId,
        @NotBlank String eventType,
        String previousStatus,
        @NotBlank String newStatus,
        String actorId,
        String actorRole,
        @NotNull Instant occurredAt
) {
}
