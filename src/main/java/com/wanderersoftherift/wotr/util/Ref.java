package com.wanderersoftherift.wotr.util;

public class Ref<T> {

    private T value;

    public Ref(T value) {
        this.value = value;
    }

    public Ref() {
        this.value = null;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
