package com.university.facade;

public class AdminFacade {
    private final RegistrationFacade registrationFacade;
    public AdminFacade(RegistrationFacade registrationFacade){this.registrationFacade=registrationFacade;}
    public String manualCompensate(String id){
        registrationFacade.manualCompensate(id);
        return "Administrative compensation completed for " + id;
    }
}