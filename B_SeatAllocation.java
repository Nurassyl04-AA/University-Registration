package com.university.domain.subsystemB;

public class B_SeatAllocation {
    public enum Status { NEW, ALLOCATED, CANCELLED }
    private final String id; private final String course; private Status status;
    public B_SeatAllocation(String id,String course,Status status){this.id=id;this.course=course;this.status=status;}
    public void allocate(){if(status!=Status.NEW)throw new IllegalStateException("B: illegal transition");status=Status.ALLOCATED;}
    public void cancel(){if(status==Status.CANCELLED)throw new IllegalStateException("B: already cancelled");status=Status.CANCELLED;}
    public String getId(){return id;} public String getCourse(){return course;} public Status getStatus(){return status;}
}