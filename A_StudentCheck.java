package com.university.domain.subsystemA;

public class A_StudentCheck {
    public enum Status { NEW, CONFIRMED, CANCELLED }
    private final String id;
    private final String studentId;
    private final String course;
    private Status status;

    public A_StudentCheck(String id, String studentId, String course, Status status) {
        this.id=id; this.studentId=studentId; this.course=course; this.status=status;
    }
    public void confirm() { if(status != Status.NEW) throw new IllegalStateException("A: illegal transition"); status=Status.CONFIRMED; }
    public void cancel() { if(status == Status.CANCELLED) throw new IllegalStateException("A: already cancelled"); status=Status.CANCELLED; }
    public String getId(){return id;} public String getStudentId(){return studentId;} public String getCourse(){return course;} public Status getStatus(){return status;}
}