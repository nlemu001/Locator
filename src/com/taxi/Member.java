package com.taxi;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.PushService;

@SuppressLint("UseSparseArrays") 
public class Member extends Application {
	public Integer ID;
	public String nickname;
	public String name;
	public String password;
	public String email;
	public String phone_num;
	private Double mLat = 33.52;
	private Double mLng = -117.82;
	private boolean locSet = true;
	public HashMap<Integer, HashMap<String, Object>> display = new HashMap<Integer, HashMap<String, Object>>();
	public ArrayList <Circle> circles = new ArrayList <Circle> ();
	private ArrayList<Place> userPlaces = new ArrayList<Place>();
	private HashMap<Integer, HashMap<String,Boolean>> notify = new HashMap<Integer, HashMap<String, Boolean>>();
	
	public ArrayList<com.google.android.gms.maps.model.Circle> shapes = new ArrayList<com.google.android.gms.maps.model.Circle>();
	public int circle = -1;
	public int user = -1;
	public String userNN = "";
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "QjBCQwxoQdR6VtYp2tyrGvQLlf7eKEBzPjAZVcGm", "IbgUMSFPZubtrtj7rJ1wxDAce6lcUuLv4N4GCDCW");
        PushService.setDefaultPushCallback (this, MainActivity.class);
    }
	
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
	
	public boolean setLoc(){
		return locSet;
	}
	
	public void toggleSet(){
		locSet = false;
	}
	
	public Double getLat(){
		return mLat;
	}
	
	public Double getLng(){
		return mLng;
	}
	
	public void setLat(Double lat){
		mLat = lat;
	}
	
	public void setLng(Double lng){
		mLng = lng;
	}
	
	public boolean containsNotification (Integer usr, String name) {
		if(notify.containsKey(usr)){
			HashMap<String, Boolean> map = notify.get(usr);
			return (map.containsKey(name)) ? true : false;
		}
		return false;
	}
	
	public void setNotification (Integer usr, String name, boolean val) {
		if(notify.containsKey(usr)){
			notify.get(usr).put(name, val);
		}else{
			HashMap<String, Boolean> map = new HashMap<String, Boolean>();
			map.put(name, val);
			notify.put(usr, map);
		}
	}
	
	public void toggleNotification (Integer usr, String name) {
		boolean current = notify.get(usr).get(name);
		notify.get(usr).put(name, !current);
	}
	
	public boolean checkNotification (Integer usr, String name) {
		return notify.get(usr).get(name);
	}
	
	public HashMap<String, Object> CurrentData () {
		return display.get (this.ID);
	}
	
	public String IDfromNickname (String nname) {
		for (Integer key : display.keySet()){
			HashMap <String, Object> map = display.get(key);
			if (((String) map.get("nickname")).equals(nname)) {
				return String.valueOf((Integer) map.get ("uid"));
			}
		}
		return  String.valueOf(ID);
	}
	
	public HashMap<Integer, HashMap<String, Object>> getDisplay() {
		return display;
	}
	
	public void updateDisplay (Integer key, Double lat, Double lng) {
		display.get(key).put("lat", String.valueOf(lat));
		display.get(key).put("lng", String.valueOf(lng));
	}
	
	public void addDisplay (Integer key, HashMap<String, Object> value) {
		display.put(key, value);
	}
	
	public void clearDisplay () {
		display.clear();
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
		return this.email;
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
	
	public void addPlace(String n, String s, String c, String la, String ln){
		userPlaces.add(new Place(n, s, c, this.ID, la, ln));
	}
	
	public void addPlace(Place p){
		userPlaces.add(p);
	}
	
	public void clearPlaces(){
		userPlaces.clear();
	}
	
	public void addShape(com.google.android.gms.maps.model.Circle c){
		shapes.add(c);
	}
	
	public void clearShape(){
		for (com.google.android.gms.maps.model.Circle c : shapes){
			if (c != null){
				c.remove();
				c = null;
			}
			shapes.remove(c);
		}
	}
};