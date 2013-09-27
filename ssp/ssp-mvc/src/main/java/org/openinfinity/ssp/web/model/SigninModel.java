package org.openinfinity.ssp.web.model;

public class SigninModel {

	private String username;
	
	private String password;
	
	public SigninModel(final String username, final String password){
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(final String username){
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(final String password){
		this.password = password;
	}
}
