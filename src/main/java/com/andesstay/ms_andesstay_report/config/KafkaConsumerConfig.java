package com.andesstay.ms_andesstay_report.config;

import com.andesstay.ms_andesstay_report.messaging.AuditTimelineEntry;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * 2 reintentos con 2s de espera y luego DLT ("<topico-origen>.DLT"), igual que en
 * ms-andesstay-audit. El resolver de destino se fija explicitamente porque el sufijo
 * por defecto de DeadLetterPublishingRecoverer en esta version es "-dlt", no ".DLT".
 */
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", -1));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 2L));
    }

    /**
     * Fabrica propia para audit.timeline: el deserializador global del servicio esta fijado
     * a ReservationEvent, asi que el segundo topico necesita su propio JsonDeserializer
     * tipado a AuditTimelineEntry. Consumer group y errores son independientes del flujo
     * de reservations.events (materia 3.1.2 y 3.3.3).
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuditTimelineEntry>
            auditTimelineKafkaListenerContainerFactory(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ms-andesstay-report-audit-timeline");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        JacksonJsonDeserializer<AuditTimelineEntry> valueDeserializer =
                new JacksonJsonDeserializer<>(AuditTimelineEntry.class);
        valueDeserializer.setUseTypeHeaders(false);
        valueDeserializer.addTrustedPackages("com.andesstay.ms_andesstay_report.messaging");

        DefaultKafkaConsumerFactory<String, AuditTimelineEntry> consumerFactory =
                new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);

        ConcurrentKafkaListenerContainerFactory<String, AuditTimelineEntry> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(2000L, 2L)));
        return factory;
    }
}
