package com.taxi;

import java.util.ArrayList;
import java.util.HashMap;

public class Circle {
	private String cname;
	private Integer adminID;
	private ArrayList<Integer> members = new ArrayList<Integer> ();
	private ArrayList<String> nnames = new ArrayList<String> ();
	ArrayList<HashMap<String, String>> all_users = new ArrayList<HashMap<String, String>>();
	
	public Circle (String name, Integer admin){
		this.cname = name;
		this.adminID = admin;
	}
	
	public String getCircleName () {
		return this.cname;
	}
	
	public HashMap<String, String> getMember (Integer index) {
		return this.all_users.get(index);
	}
	
	public HashMap<String, String> getMember (String nname) {
		int index = nnames.indexOf (nname);
		return this.all_users.get(index);
	}
	
	public Integer getCircleAdmin () {
		return this.adminID;
	}
	
	public ArrayList <String> getNicknames () {
		return this.nnames;
	}
	
	public ArrayList <Integer> getCircleMembers () {
		return this.members;
	}
	
	public boolean equals(Circle aThat) {
	    if ( this == aThat ) 
	    	return true;
	    if (!(aThat instanceof Circle)) 
	    	return false;
	    Circle that = (Circle) aThat;
	    return (this.cname.equals(that.cname) && (this.adminID == that.adminID));
	}
	
	public boolean equals(String cn, Integer aid) {
	    if (this == null) 
	    	return false;
	    return this.cname.equals(cn) && this.adminID.equals(aid);
	}
	
	public void addMember (Integer member, String nickname, HashMap <String, String> map) {
		if (!(this.nnames.contains(nickname))){
			this.members.add(member);
			this.nnames.add(nickname);
			this.all_users.add(map);
		}
	}
	
	public void deleteMember (Integer member, String nickname) {
		if (this.members.contains(member)) {
			this.members.remove(member);
			this.nnames.remove(nickname);
		}
	}
}