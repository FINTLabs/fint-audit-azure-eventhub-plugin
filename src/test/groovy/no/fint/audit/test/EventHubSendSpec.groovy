import no.fint.audit.FintAuditConfig
import no.fint.audit.FintAuditService
import no.fint.audit.plugin.eventhub.AuditEventhubWorker
import no.fint.event.model.Event
import no.fint.event.model.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = FintAuditConfig)
@ActiveProfiles('test')
class EventHubSendSpec extends Specification {
    @Autowired
    FintAuditService fintAuditService
    @Autowired
    AuditEventhubWorker auditEventhubWorker

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