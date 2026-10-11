package com.university;

import com.university.facade.RegistrationFacade;
import com.university.facade.AdminFacade;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RegistrationFacade facade = new RegistrationFacade();
        AdminFacade admin = new AdminFacade(facade);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== UNIVERSITY REGISTRATION ===");
            System.out.println("1. Register student");
            System.out.println("2. Register and force B failure");
            System.out.println("3. Register and force C failure");
            System.out.println("4. Show persisted state");
            System.out.println("5. Cancel/compensate by registration ID");
            System.out.println("0. Exit");
            System.out.print("> ");
            String choice = sc.nextLine();

            try {
                if (choice.equals("1")) {
                    System.out.print("Student ID: "); String s = sc.nextLine();
                    System.out.print("Course: "); String c = sc.nextLine();
                    System.out.print("Seats available (true/false): "); boolean seat = Boolean.parseBoolean(sc.nextLine());
                    System.out.println(facade.register(s, c, seat, false, false));
                } else if (choice.equals("2")) {
                    System.out.print("Student ID: "); String s = sc.nextLine();
                    System.out.print("Course: "); String c = sc.nextLine();
                    System.out.println(facade.register(s, c, true, true, false));
                } else if (choice.equals("3")) {
                    System.out.print("Student ID: "); String s = sc.nextLine();
                    System.out.print("Course: "); String c = sc.nextLine();
                    System.out.println(facade.register(s, c, true, false, true));
                } else if (choice.equals("4")) {
                    facade.printState();
                } else if (choice.equals("5")) {
                    System.out.print("Registration ID: "); String id = sc.nextLine();
                    System.out.println(admin.manualCompensate(id));
                } else if (choice.equals("0")) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }
}