package com.mcmiddleearth.rpmanager.events;

public class ListItemRemovedEvent implements Event {
    private final Object source;
    private final Object item;
    private final int index;

    public ListItemRemovedEvent(Object source, Object item, int index) {
        this.source = source;
        this.item = item;
        this.index = index;
    }

    @Override
    public Object getSource() {
        return source;
    }

    public Object getItem() {
        return item;
    }

    public int getIndex() {
        return index;
    }
}
