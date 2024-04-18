package no.fintlabs.audit.eventhub;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fint.audit.model.AuditEvent;
import no.twingine.CircularBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.nio.BufferOverflowException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class AuditEventhubWorker {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private EventHubProducerClient eventHubProducerClient;

    @Value("${fint.audit.azure.eventhub.buffer-size:200000}")
    private int bufferSize;

    @Value("${fint.audit.azure.eventhub.rate:2500}")
    private long rate;

    @Value("${fint.audit.test-mode:false}")
    private boolean testMode;

    private CircularBuffer<byte[]> buffer;

    private AtomicLong index;

    private ScheduledExecutorService executorService;

    @PostConstruct
    public void init() {
        buffer = new CircularBuffer<>(bufferSize);
        index = buffer.index();
        if (!testMode) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleWithFixedDelay(this::save, rate, rate, TimeUnit.MILLISECONDS);
        }
    }

    public void save() {
        int count = 0;
        try {
            byte[] message = buffer.take(index);
            while (message != null) {
                EventDataBatch batch = eventHubProducerClient.createBatch();
                while (message != null && batch.tryAdd(new EventData(message))) {
                    message = buffer.take(index);
                }
                count = batch.getCount();
                eventHubProducerClient.send(batch);
                log.debug("Sent a batch of {} events", count);
            }
        } catch (BufferOverflowException e) {
            log.warn("Audit event buffer overflow, losing at least {} events!", bufferSize);
        } catch (Exception e) {
            log.trace("Stopping due to unknown error", e);
            index.addAndGet(-count);
        }
    }

    public void audit(AuditEvent auditEvent) {
        try {
            buffer.add(objectMapper.writeValueAsBytes(auditEvent));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
