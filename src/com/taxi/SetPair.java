package com.taxi;

public class SetPair {
    private String name;
    private Integer admin;
    
    public SetPair(String n, Integer a){
        this.name = n;
        this.admin = a;
    }
    public String getName () {
    	return this.name; 
    }
    
    public Integer getAdmin () { 
    	return this.admin; 
    }
    
    public void setName (String newName) { 
    	this.name = newName; 
    }
    
    public void setAdmin (Integer newAdmin) { 
    	this.admin = newAdmin;
    }
};