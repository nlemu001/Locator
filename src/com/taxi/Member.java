package com.taxi;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

public class Member extends Application {
	public Integer ID;
	public String nickname;
	public String name;
	public String password;
	public String email;
	public String phone_num;

	@SuppressLint("UseSparseArrays") 
	public HashMap<Integer, HashMap<String, Object>> display = new HashMap<Integer, HashMap<String, Object>>();
	public ArrayList <Circle> circles = new ArrayList <Circle> ();
	private ArrayList<Place> userPlaces = new ArrayList<Place>();
	public int circle = -1;
	public int user = -1;
	public String userNN = "";

	public Member () {
		this.nickname = "nick";
		this.email = "test@taxi.com";
		this.password = "pass";
		this.phone_num = "9519119111";
	}

	public Member (String username, String password, String nickname, String phone) {
		this.nickname = nickname;
		this.email = username;
		this.password = password;
		this.phone_num = phone;

	}
	
	public String IDfromNickname (String nname) {
		for (Integer key : display.keySet()){
			HashMap <String, Object> map = display.get(key);
			if (((String) map.get("nickname")).equals(nname)) {
				Log.d("IDfromNickname", "found!");
				return String.valueOf((Integer) map.get ("uid"));
			}
		}
		Log.d("IDfromNickname", "currentID");
		return  String.valueOf(ID);
	}
	
	public void addDisplay (Integer key, HashMap<String, Object> value) {
		display.put(key, value);
	}

	public void deleteDisplay (Integer key) {
		display.remove (key);
	}
	
	public HashMap<String, Object> getDisplay (Integer key) {
		return display.get (key);
	}
	
	public boolean inDisplay (Integer key) {
		return display.containsKey (key);
	}

	public int contains (String cn, Integer aid){
		for (Circle c : circles) {
			if (c.equals (cn, aid))
				return circles.indexOf (c);
		}
		return -1;
	}

	public int contains (Circle cir){
		for (Circle c : circles) {
			if (c.equals (cir))
				return circles.indexOf (c);
		}
		return -1;
	}

	public String[] getCircleArray () {
		ArrayList <String> cnames = new ArrayList <String> ();
		for (Circle c : circles){
			cnames.add(c.getCircleName());
		}
		return cnames.toArray (new String [0]);
	}

	public String[] getNicknameArray (int index) {
		ArrayList <String> nnames = circles.get(index).getNicknames();
		Log.d ("Adding - ", String.valueOf(nnames.size()));
		return nnames.toArray (new String [0]);
	}

	public ArrayList <Circle> getCircleList () {
		return this.circles;
	}

	public HashMap<String, String> getUser (Integer id) {
		Circle c = this.circles.get (this.circle);
		return c.getMember (id);
	}
	
	public HashMap<String, String> getUser (String nname) {
		Circle c = this.circles.get (this.circle);
		return c.getMember (nname);
	}
	
	public String getNickname (Integer uid) {
		HashMap<String, String> map = this.getUser(uid);
		if (map != null)
			return map.get("nickname");
		else return "No Nickname";
	}
	
	public String getPhone (Integer uid) {
		HashMap<String, String> map = this.getUser (uid);
		if (map != null)
			return map.get("phone");
		else return "911";
	}

	public void addCircle (Circle c) {
		circles.add(c);
		//CircleNames.add(c.getCircleName());
		Log.d("Circle", "adding");
	}

	public Integer getID () {
		return this.ID;
	}

	public String getPhone (String nickname) {
		HashMap<String, String> map = this.getUser (nickname);
		if (map != null)
			return map.get("phone");
		else return "911";
	}
	
	public String getUsername () {
		return this.nickname;
	}

	public String getNickname () {
		return this.nickname;
	}

	public String getPhoneNumber () {
		return this.phone_num;
	}

	public String getPassword () {
		return this.password;
	}

	public ArrayList <Circle> getCircles (){
		return this.circles;
	}

	public void setID (Integer newID){
		this.ID = newID;
	}

	public void setUsername (String newUsername) {
		this.email = newUsername;
	}

	public void setNickname (String newNickname) {
		this.nickname = newNickname;
	}

	public void setPassword (String newPassword) {
		this.password = newPassword;
	}

	public void setPhoneNumber (String newPhone) {
		this.phone_num = newPhone;
	}
	
	public ArrayList<Place> getPlaces(){
		return this.userPlaces;
	}
	
	public void addPlace(String n, String s, String c){
		userPlaces.add(new Place(n, s, c, this.ID));
	}
	
	public void addPlace(Place p){
		//userPlaces.add(new Place(n, s, c, this.ID));
		userPlaces.add(p);
	}
	
	public void clearPlaces(){
		userPlaces.clear();
	}
};