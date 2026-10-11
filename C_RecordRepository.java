package com.university.domain.subsystemC;

import java.nio.file.*; import java.io.*; import java.util.*;

public class C_RecordRepository {
    private final Path file=Path.of("data/c_records.csv");
    public C_RecordRepository(){try{Files.createDirectories(file.getParent());if(!Files.exists(file))Files.createFile(file);}catch(IOException e){throw new RuntimeException(e);}}
    public void save(C_RegistrationRecord x){List<String>l=all();l.removeIf(s->s.startsWith(x.getId()+"|"));l.add(x.getId()+"|"+x.getFee()+"|"+x.getStatus());write(l);}
    public C_RegistrationRecord find(String id){for(String s:all()){String[]p=s.split("\\|",-1);if(p.length==3&&p[0].equals(id))return new C_RegistrationRecord(p[0],Double.parseDouble(p[1]),C_RegistrationRecord.Status.valueOf(p[2]));}return null;}
    public List<C_RegistrationRecord> findAll(){List<C_RegistrationRecord>r=new ArrayList<>();for(String s:all()){String[]p=s.split("\\|",-1);if(p.length==3)r.add(new C_RegistrationRecord(p[0],Double.parseDouble(p[1]),C_RegistrationRecord.Status.valueOf(p[2])));}return r;}
    private List<String>all(){try{return new ArrayList<>(Files.readAllLines(file));}catch(IOException e){throw new RuntimeException(e);}}
    private void write(List<String>x){try{Files.write(file,x);}catch(IOException e){throw new RuntimeException(e);}}
}