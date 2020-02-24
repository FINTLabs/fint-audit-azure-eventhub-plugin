package no.fint.audit;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import lombok.extern.slf4j.Slf4j;
import no.fint.audit.plugin.eventhub.AuditEventhub;
import no.fint.audit.plugin.eventhub.AuditEventhubWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FintAuditConfig {

    @Bean
    @ConditionalOnProperty(value = "fint.audit.test-mode", havingValue = "false", matchIfMissing = true)
    public EventHubProducerClient eventHubProducerClient(
            @Value("${fint.audit.azure.eventhub.connection-string}") String connectionString,
            @Value("${fint.audit.azure.eventhub.name}") String eventHubName) {
        return new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();
    }

    @Bean
    public FintAuditService fintAuditService() {
        return new AuditEventhub();
    }

    @Bean
    public AuditEventhubWorker auditEventhubWorker() {
        return new AuditEventhubWorker();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().setDateFormat(new ISO8601DateFormat()).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
