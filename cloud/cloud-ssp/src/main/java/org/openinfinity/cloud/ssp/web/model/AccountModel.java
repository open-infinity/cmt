package org.openinfinity.cloud.ssp.web.model;

import org.openinfinity.cloud.domain.ssp.Account;
import org.openinfinity.cloud.domain.ssp.User;

public class AccountModel {

	private User user;
	
	private Account account;
	
	public AccountModel(final User user, final Account account){
		this.user = user;
		this.account = account;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUSer(final User user){
		this.user = user;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(final Account account){
		this.account = account;
	}
}
