package com.kett.TicketSystem.util;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class EventCatcher {
    private DomainEvent event = null;
    private Class<? extends DomainEvent> typeOfClass = null;
    private Boolean isListening = false;

    public void catchEventOfType(Class<? extends DomainEvent> eventClass) {
        typeOfClass = eventClass;
        isListening = true;
    }

    public Boolean isOfSpecifiedType(Object newEvent) {
        return isListening && typeOfClass.isInstance(newEvent);
    }

    public Boolean hasCaughtEvent() {
        return event != null;
    }

    public DomainEvent getEvent() {
        DomainEvent tempEvent = event;
        event = null;
        return tempEvent;
    }

    @EventListener(condition = "@eventCatcher.isOfSpecifiedType(#newEvent)")
    public void onApplicationEvent(Object newEvent) {
        isListening = false;
        event = (DomainEvent) newEvent;
    }
}
