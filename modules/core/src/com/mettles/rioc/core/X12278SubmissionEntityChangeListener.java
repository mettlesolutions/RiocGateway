package com.mettles.rioc.core;


import com.google.common.base.Strings;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.Events;
import com.mettles.rioc.UiNotificationEvent;
import com.mettles.rioc.entity.X12278Submission;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;

@Component(X12278SubmissionEntityChangeListener.NAME)
public class X12278SubmissionEntityChangeListener {

    public static final String NAME = "rioc_X12278SubmissionEntityChangeListener";

    @Inject
    Events events;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    private void afterCommit(EntityChangedEvent<X12278Submission, Long> event)
    {
        try {
            System.out.println("after commit event" + event.getEntityId().getValue());
            String message = event.getEntityId().getValue().toString();
            System.out.println("Message: "+message);
            //  globaleventservice.sendEvent(new UiNotificationEvent(this, Strings.isNullOrEmpty(message) ? "test" : message));
            events.publish(new UiNotificationEvent(this, Strings.isNullOrEmpty(message) ? "test" : message));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /*
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<X12278Submission, Long> event) {

        System.out.println("before commit event" + event.getEntityId().getValue());
        String message = event.getEntityId().getValue().toString();
        System.out.println("Message: "+message);

        events.publish(new UiNotificationEvent(this, Strings.isNullOrEmpty(message) ? "test" : message));
        //Id<X12278Submission, Long> entityId = event.getEntityId();
        //EntityChangedEvent.Type changeType = event.getType();

        //AttributeChanges changes = event.getChanges();
        // System.out.println("before commit event"+changes.getAttributes().iterator().next().toString());



    }
    */


}
