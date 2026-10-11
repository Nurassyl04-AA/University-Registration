package com.university.facade;

import com.university.domain.subsystemA.*;
import com.university.domain.subsystemB.*;
import com.university.domain.subsystemC.*;
import java.util.UUID;

public class RegistrationFacade {
    private final A_CheckRepository aRepo=new A_CheckRepository();
    private final A_EligibilityRule aRule=new A_EligibilityRule();
    private final B_AllocationRepository bRepo=new B_AllocationRepository();
    private final B_CapacityValidator bRule=new B_CapacityValidator();
    private final C_RecordRepository cRepo=new C_RecordRepository();
    private final C_BillingCalculator cRule=new C_BillingCalculator();

    public String register(String studentId,String course,boolean seatsAvailable,boolean failB,boolean failC){
        String id=UUID.randomUUID().toString().substring(0,8);
        aRule.validate(studentId,course);

        A_StudentCheck a=new A_StudentCheck(id,studentId,course,A_StudentCheck.Status.NEW);
        a.confirm(); aRepo.save(a);
        System.out.println("A committed: student eligibility confirmed");

        try {
            bRule.validate(seatsAvailable,failB);
            B_SeatAllocation b=new B_SeatAllocation(id,course,B_SeatAllocation.Status.NEW);
            b.allocate(); bRepo.save(b);
            System.out.println("B committed: seat allocated");

            try {
                cRule.validate(failC);
                C_RegistrationRecord c=new C_RegistrationRecord(id,cRule.calculate(course),C_RegistrationRecord.Status.NEW);
                c.confirm(); cRepo.save(c);
                System.out.println("C committed: registration confirmed");
                return "SUCCESS. Registration ID = "+id;
            } catch (RuntimeException e) {
                System.out.println("C failed -> compensating B then A");
                compensateB(id); compensateA(id);
                return "COMPENSATED C FAILURE. Registration ID = "+id;
            }
        } catch (RuntimeException e) {
            System.out.println("B failed -> compensating A");
            compensateA(id);
            return "COMPENSATED B FAILURE. Registration ID = "+id;
        }
    }

    private void compensateA(String id){A_StudentCheck a=aRepo.find(id);if(a!=null){a.cancel();aRepo.save(a);}}
    private void compensateB(String id){B_SeatAllocation b=bRepo.find(id);if(b!=null){b.cancel();bRepo.save(b);}}

    public void manualCompensate(String id){
        C_RegistrationRecord c=cRepo.find(id);
        B_SeatAllocation b=bRepo.find(id);
        A_StudentCheck a=aRepo.find(id);
        if(c!=null && c.getStatus()!=C_RegistrationRecord.Status.CANCELLED){c.cancel();cRepo.save(c);}
        if(b!=null && b.getStatus()!=B_SeatAllocation.Status.CANCELLED){b.cancel();bRepo.save(b);}
        if(a!=null && a.getStatus()!=A_StudentCheck.Status.CANCELLED){a.cancel();aRepo.save(a);}
    }

    public void printState(){
        System.out.println("\n--- A: Eligibility ---");
        aRepo.findAll().forEach(x->System.out.println(x.getId()+" | "+x.getStudentId()+" | "+x.getCourse()+" | "+x.getStatus()));
        System.out.println("--- B: Seats ---");
        bRepo.findAll().forEach(x->System.out.println(x.getId()+" | "+x.getCourse()+" | "+x.getStatus()));
        System.out.println("--- C: Registration ---");
        cRepo.findAll().forEach(x->System.out.println(x.getId()+" | fee="+x.getFee()+" | "+x.getStatus()));
    }
}