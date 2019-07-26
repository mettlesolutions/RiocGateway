package com.mettles.rioc;


import com.haulmont.addon.globalevents.GlobalApplicationEvent;

public class BeanNotificationEvent extends GlobalApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */

    private String message;

    public BeanNotificationEvent(Object source,String message) {
        super(source);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    @Override
    public String toString() {
        return "BeanNotificationEvent{" +
                "message='" + message + '\'' +
                ", source=" + source +
                '}';
    }
}
