package com.revolut.mts.dto;

public class EmptyBody extends Body<Void> {
    public EmptyBody() {
        super(null);
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass());
    }
}
