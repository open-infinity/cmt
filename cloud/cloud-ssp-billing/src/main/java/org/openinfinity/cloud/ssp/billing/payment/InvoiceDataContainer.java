package org.openinfinity.cloud.ssp.billing.payment;

import org.openinfinity.cloud.domain.UsagePeriod;
import org.openinfinity.cloud.domain.ssp.Account;

public class InvoiceDataContainer {
	
	private UsagePeriod usagePeriod;
	
	private Account account;
	
	InvoiceDataContainer(UsagePeriod usagePeriod, Account account){
		this.account = account;
		this.usagePeriod = usagePeriod;
	}
	
	public Account getAccount(){
		return account;
	}
	
	public UsagePeriod getUsagePeriod(){
		return usagePeriod;
	}

}
