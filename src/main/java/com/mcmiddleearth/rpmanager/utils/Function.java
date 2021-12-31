package com.mcmiddleearth.rpmanager.utils;

@FunctionalInterface
public interface Function<T, R, E extends Throwable> {
    R apply(T arg) throws E;
}
