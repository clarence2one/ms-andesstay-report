package com.andesstay.ms_andesstay_report.messaging;

import com.andesstay.ms_andesstay_report.domain.AuditTimelineProjection;
import com.andesstay.ms_andesstay_report.repository.AuditTimelineProjectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditTimelineEventListenerTest {

    private static final Instant OCURRIDO = Instant.parse("2026-11-22T10:15:30Z");

    @Mock
    private AuditTimelineProjectionRepository repository;

    @InjectMocks
    private AuditTimelineEventListener listener;

    private AuditTimelineEntry entry() {
        return new AuditTimelineEntry("evt-1", "res-1", "RESERVATION_CONFIRMED",
                "CREADA", "CONFIRMADA", "admin-1", "Admin", OCURRIDO);
    }

    @Test
    void proyectaElHistorialNormalizado() {
        when(repository.existsByEventId("evt-1")).thenReturn(false);

        listener.onAuditTimelineEntry(entry());

        ArgumentCaptor<AuditTimelineProjection> captor = ArgumentCaptor.forClass(AuditTimelineProjection.class);
        verify(repository).save(captor.capture());
        AuditTimelineProjection projection = captor.getValue();
        assertEquals("evt-1", projection.getEventId());
        assertEquals("res-1", projection.getReservationId());
        assertEquals("RESERVATION_CONFIRMED", projection.getEventType());
        assertEquals("admin-1", projection.getActorId());
        assertEquals(OCURRIDO, projection.getOccurredAt());
    }

    @Test
    void ignoraRedeliveryDelMismoEvento() {
        when(repository.existsByEventId("evt-1")).thenReturn(true);

        listener.onAuditTimelineEntry(entry());

        verify(repository, never()).save(any());
    }
}
