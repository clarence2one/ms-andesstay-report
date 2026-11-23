package com.andesstay.ms_andesstay_report.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Mismo contrato que consume ms-andesstay-audit desde reservations.events, publicado por
 * ms-andesstay-reservations en cada cambio de estado de una reserva.
 */
public record ReservationEvent(
        @NotBlank String type,
        @NotNull String eventId,
        @NotNull Instant timestamp,
        @NotBlank String traceId,
        @NotBlank String correlationId,
        @NotBlank String reservationId,
        String previousStatus,
        @NotBlank String newStatus,
        String actorId,
        String actorRole,
        @NotNull Long unitId
) {
}
