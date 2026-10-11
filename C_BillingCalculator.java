package com.university.domain.subsystemC;

public class C_BillingCalculator {
    public double calculate(String course){ return course.toLowerCase().contains("advanced") ? 50000.0 : 30000.0; }
    public void validate(boolean forceFailure){if(forceFailure)throw new IllegalStateException("C: billing confirmation failed");}
}