package com.farissyariati.kuma.select.users;

public class SelectUsers {
	public String username;
	public String userEmail;
	public String phoneNumber;
	public int id;
	
	public SelectUsers(){
		
	}
	
	public SelectUsers(int id, String username, String userEmail, String phoneNumber){
		this.id = id;
		this.username = username;
		this.userEmail = userEmail;
		this.phoneNumber = phoneNumber;
	}

}
