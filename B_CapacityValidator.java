package com.university.domain.subsystemB;

public class B_CapacityValidator {
    public void validate(boolean seatsAvailable, boolean forceFailure){
        if(!seatsAvailable || forceFailure) throw new IllegalStateException("B: no available seat");
    }
}