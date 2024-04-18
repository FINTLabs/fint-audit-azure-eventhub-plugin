package no.fintlabs.audit.eventhub;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.audit.model.AuditEvent;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditEventhub implements FintAuditService {

    @Autowired
    private AuditEventhubWorker auditEventhubWorker;

    @Override
    public void audit(Event event, Status... statuses) {
        for (Status status : statuses) {
            Event copy = new Event();
            BeanUtils.copyProperties(event, copy);
            copy.setStatus(status);
            auditEventhubWorker.audit(new AuditEvent(copy));
        }
        event.setStatus(statuses[statuses.length - 1]);
    }

    @Override
    public void audit(Event event, boolean clearData) {
        Event copy = new Event();
        BeanUtils.copyProperties(event, copy);
        auditEventhubWorker.audit(new AuditEvent(copy, clearData));
    }
}
