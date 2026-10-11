package com.university.domain.subsystemB;

import java.nio.file.*; import java.io.*; import java.util.*;

public class B_AllocationRepository {
    private final Path file=Path.of("data/b_allocations.csv");
    public B_AllocationRepository(){try{Files.createDirectories(file.getParent());if(!Files.exists(file))Files.createFile(file);}catch(IOException e){throw new RuntimeException(e);}}
    public void save(B_SeatAllocation x){List<String> l=all();l.removeIf(s->s.startsWith(x.getId()+"|"));l.add(x.getId()+"|"+x.getCourse()+"|"+x.getStatus());write(l);}
    public B_SeatAllocation find(String id){for(String s:all()){String[]p=s.split("\\|",-1);if(p.length==3&&p[0].equals(id))return new B_SeatAllocation(p[0],p[1],B_SeatAllocation.Status.valueOf(p[2]));}return null;}
    public List<B_SeatAllocation> findAll(){List<B_SeatAllocation>r=new ArrayList<>();for(String s:all()){String[]p=s.split("\\|",-1);if(p.length==3)r.add(new B_SeatAllocation(p[0],p[1],B_SeatAllocation.Status.valueOf(p[2])));}return r;}
    private List<String>all(){try{return new ArrayList<>(Files.readAllLines(file));}catch(IOException e){throw new RuntimeException(e);}}
    private void write(List<String>x){try{Files.write(file,x);}catch(IOException e){throw new RuntimeException(e);}}
}