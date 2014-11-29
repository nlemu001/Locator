package com.taxi;

public class Place {

	private String name;
	private String street;
	private String city;
	private Integer uid;
	private String lat;
	private String lng;
	
	public Place(){
		name = "home";
		street = "123 Fake St.";
		city = "Springfield, MI";
		uid = 0;
		lat = "0";
		lng = "0";
	}
	
	public Place(String n, String s, String c, Integer u){
		name = n;
		street = s;
		city = c;
		uid = u;
		lat = "0";
		lng = "0";
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
	
	public void setLat(Double Lat){
		lat = Lat.toString();
	}
	
	public void setLng(Double Lng){
		lat = Lng.toString();
	}
	
	public String gettLat(){
		return lat;
	}
	
	public String getLng(){
		return lng;
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
