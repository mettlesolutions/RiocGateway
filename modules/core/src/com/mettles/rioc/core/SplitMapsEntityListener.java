package com.mettles.rioc.core;

import com.mettles.rioc.BeanNotificationEvent;
import com.mettles.rioc.UiNotificationEvent;
import com.mettles.rioc.entity.Document;
import com.mettles.rioc.entity.SplitMaps;
import com.mettles.rioc.entity.Submission;
import com.mettles.rioc.service.SubmissionService;
import com.google.common.base.Strings;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.AttributeChanges;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.Events;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.*;

@Component(SplitMapsEntityListener.NAME)
public class SplitMapsEntityListener {
    public static final String NAME = "rioc_SplitMapsEntityListener";

    @Inject
    Events events;



    @Inject
    private SubmissionService submissionService;



    @EventListener
    public void beforeCommit(EntityChangedEvent<SplitMaps, UUID> event) {
        Id<SplitMaps, UUID> entityId = event.getEntityId();
        EntityChangedEvent.Type changeType = event.getType();

        AttributeChanges changes = event.getChanges();
        System.out.println("before commit event"+changes.getAttributes().iterator().next().toString());



    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    private void afterCommit(EntityChangedEvent<SplitMaps, UUID> event) {

        System.out.println("after commit event"+event.getEntityId().getValue());
        String message = event.getEntityId().getValue().toString();
        //  globaleventservice.sendEvent(new UiNotificationEvent(this, Strings.isNullOrEmpty(message) ? "test" : message));

        SplitMaps splitmaps;
        System.out.println("Change type is "+ event.getType());

        if (event.getType() == EntityChangedEvent.Type.UPDATED) {
            System.out.println("Sending BeanNotificationEvent");
            events.publish(new BeanNotificationEvent(this, Strings.isNullOrEmpty(message) ? "test" : message));


        }
        //  globaleventservice.sendEvent(new UiNotificationEvent(this, Strings.isNullOrEmpty(message) ? "test" : message));
      //  events.publish(new UiNotificationEvent(this, Strings.isNullOrEmpty(message) ? "test" : message));

    }
}