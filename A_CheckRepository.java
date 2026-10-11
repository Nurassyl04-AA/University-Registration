package com.university.domain.subsystemA;

import java.nio.file.*;
import java.io.*;
import java.util.*;

public class A_CheckRepository {
    private final Path file = Path.of("data/a_checks.csv");
    public A_CheckRepository(){ init(); }
    private void init(){ try { Files.createDirectories(file.getParent()); if(!Files.exists(file)) Files.createFile(file); } catch(IOException e){throw new RuntimeException(e);} }

    public void save(A_StudentCheck x) {
        List<String> lines = readAll();
        lines.removeIf(l -> l.startsWith(x.getId()+"|"));
        lines.add(String.join("|", x.getId(), x.getStudentId(), x.getCourse(), x.getStatus().name()));
        write(lines);
    }
    public A_StudentCheck find(String id) {
        for(String l: readAll()) { String[] p=l.split("\\|",-1); if(p.length==4 && p[0].equals(id)) return new A_StudentCheck(p[0],p[1],p[2],A_StudentCheck.Status.valueOf(p[3])); }
        return null;
    }
    public List<A_StudentCheck> findAll(){ List<A_StudentCheck> r=new ArrayList<>(); for(String l:readAll()){String[]p=l.split("\\|",-1);if(p.length==4)r.add(new A_StudentCheck(p[0],p[1],p[2],A_StudentCheck.Status.valueOf(p[3])));} return r; }
    private List<String> readAll(){ try{return new ArrayList<>(Files.readAllLines(file));}catch(IOException e){throw new RuntimeException(e);} }
    private void write(List<String> x){try{Files.write(file,x);}catch(IOException e){throw new RuntimeException(e);}}
}