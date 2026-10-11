package com.university.domain.subsystemC;

public class C_RegistrationRecord {
    public enum Status { NEW, CONFIRMED, CANCELLED }
    private final String id; private final double fee; private Status status;
    public C_RegistrationRecord(String id,double fee,Status status){this.id=id;this.fee=fee;this.status=status;}
    public void confirm(){if(status!=Status.NEW)throw new IllegalStateException("C: illegal transition");status=Status.CONFIRMED;}
    public void cancel(){if(status==Status.CANCELLED)throw new IllegalStateException("C: already cancelled");status=Status.CANCELLED;}
    public String getId(){return id;} public double getFee(){return fee;} public Status getStatus(){return status;}
}