package com.mcmiddleearth.rpmanager.events;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EventDispatcher {
    private final Map<Class<?>, List<EventListener<?>>> listeners = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Event> void dispatchEvent(T event) {
        for (Map.Entry<Class<?>, List<EventListener<?>>> entry : listeners.entrySet()) {
            if (entry.getKey().isAssignableFrom(event.getClass())) {
                for (EventListener<?> listener : entry.getValue()) {
                    ((EventListener<T>) listener).onEvent(event);
                }
            }
        }
    }

    public <T extends Event> void addEventListener(EventListener<T> listener, Class<T> eventClass) {
        listeners.computeIfAbsent(eventClass, ignored -> new LinkedList<>()).add(listener);
    }
}
