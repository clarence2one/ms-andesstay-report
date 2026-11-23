package com.andesstay.ms_andesstay_report.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "andesstay.kafka")
public record KafkaProperties(
        String topicReservationsEvents,
        String topicAuditTimeline,
        int partitions,
        int replicationFactor,
        String cleanupPolicy,
        int minInsyncReplicas,
        long reservationsRetentionMs,
        long reservationsRetentionBytes,
        long dltRetentionMs,
        long dltRetentionBytes,
        long auditRetentionMs,
        long auditRetentionBytes
) {
}
