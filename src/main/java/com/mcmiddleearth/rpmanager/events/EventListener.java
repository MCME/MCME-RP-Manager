package com.mcmiddleearth.rpmanager.events;

@FunctionalInterface
public interface EventListener<T extends Event> extends java.util.EventListener {
    void onEvent(T event);
}
