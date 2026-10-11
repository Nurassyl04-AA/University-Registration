package com.university.domain.subsystemA;

public class A_EligibilityRule {
    public void validate(String studentId, String course) {
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("Student ID is required");
        if (course == null || course.isBlank()) throw new IllegalArgumentException("Course is required");
    }
}