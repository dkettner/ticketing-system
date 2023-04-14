package com.kett.TicketSystem.util;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class EventCatcher {
    private DomainEvent event = null;
    private Class<? extends DomainEvent> typeOfClass = null;

    @Getter
    private Boolean isListening = false;

    public void catchEventOfType(Class<? extends DomainEvent> eventClass) {
        typeOfClass = eventClass;
        event = null;
        isListening = true;
    }

    public Boolean isOfSpecifiedType(Object newEvent) {
        return typeOfClass.isInstance(newEvent);
    }

    public Boolean hasCaughtEvent() {
        return event != null;
    }

    public DomainEvent getEvent() {
        DomainEvent tempEvent = event;
        event = null;
        return tempEvent;
    }

    @EventListener(condition = "@eventCatcher.isListening && @eventCatcher.isOfSpecifiedType(#newEvent)")
    public void onApplicationEvent(DomainEvent newEvent) {
        isListening = false;
        event = newEvent;
    }
}
