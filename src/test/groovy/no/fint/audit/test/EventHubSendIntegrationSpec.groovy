import no.fintlabs.audit.FintAuditConfig
import no.fint.audit.FintAuditService
import no.fintlabs.audit.eventhub.AuditEventhubWorker
import no.fint.event.model.Event
import no.fint.event.model.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = FintAuditConfig)
@ActiveProfiles('test')
class EventHubSendIntegrationSpec extends Specification {
    @Autowired
    private FintAuditService fintAuditService
    @Autowired
    private AuditEventhubWorker auditEventhubWorker

    def 'Send some events'() {
        given:
        def events = [
                new Event<>(corrId: UUID.randomUUID().toString(), orgId: 'fintlabs.no', source: 'Spock', action: 'GET_SOMETHING', client: 'Captain Picard'),
                new Event<>(corrId: UUID.randomUUID().toString(), orgId: 'fintlabs.no', source: 'Frank', action: 'UPDATE_SOMETHING', client: 'Somebody to love', data: [['foo': 'bar', 'baz': 'knot']])
        ]
        expect:
        events.each {
            fintAuditService.audit(it, Status.NEW, Status.DOWNSTREAM_QUEUE, Status.ADAPTER_ACCEPTED, Status.UPSTREAM_QUEUE)
        }
        auditEventhubWorker.save()
    }
}