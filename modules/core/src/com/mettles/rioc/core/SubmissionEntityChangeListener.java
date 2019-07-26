package com.mettles.rioc.core;

import com.mettles.rioc.entity.Submission;
import com.google.common.base.Strings;
import com.haulmont.addon.globalevents.transport.GlobalEventsService;
import com.haulmont.cuba.core.global.Events;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.app.events.AttributeChanges;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.mettles.rioc.UiNotificationEvent;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;

@Component(SubmissionEntityChangeListener.NAME)
public class SubmissionEntityChangeListener {
    public static final String NAME = "rioc_SubmissionEntityChangeListener";

    @Inject
    Events events;



    @EventListener
    public void beforeCommit(EntityChangedEvent<Submission, Long> event) {
        Id<Submission, Long> entityId = event.getEntityId();
        EntityChangedEvent.Type changeType = event.getType();

        AttributeChanges changes = event.getChanges();
       // System.out.println("before commit event"+changes.getAttributes().iterator().next().toString());



    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    private void afterCommit(EntityChangedEvent<Submission, Long> event) {

        System.out.println("after commit event"+event.getEntityId().getValue());
        String message = event.getEntityId().getValue().toString();
        //  globaleventservice.sendEvent(new UiNotificationEvent(this, Strings.isNullOrEmpty(message) ? "test" : message));
        events.publish(new UiNotificationEvent(this, Strings.isNullOrEmpty(message) ? "test" : message));

    }
}