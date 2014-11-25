package com.taxi;

public class Place {

	private String name;
	private String street;
	private String city;
	private Integer uid;
	
	public Place(){
		name = "home";
		street = "123 Fake St.";
		city = "Springfield, MI";
		uid = 0;
	}
	
	public Place(String n, String s, String c, Integer u){
		name = n;
		street = s;
		city = c;
		uid = u;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public void setStreet(String n){
		street = n;
	}
	
	public void setCity(String n){
		city = n;
	}
	
	public void setID(Integer n){
		uid = n;
	}
	
	public String getName(){
		return name;
	}
	
	public String getStreet(){
		return street;
	}
	
	public String getCity(){
		return city;
	}
	
	public String getAddress(){
		return street + " " + city;
	}
	
	public Integer getID(){
		return uid;
	}
}
