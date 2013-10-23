package org.openinfinity.cloud.ssp.billing.invoicecreator;

import org.openinfinity.cloud.domain.UsagePeriod;
import org.openinfinity.cloud.domain.ssp.Account;

public class InvoiceCreatorDataContainer {
	
	private UsagePeriod usagePeriod;
	
	private Account account;
	
    public InvoiceCreatorDataContainer(UsagePeriod usagePeriod, Account account)   {
        this.usagePeriod = usagePeriod;
        this.account = account;
    }

    public UsagePeriod getUsagePeriod() {
        return usagePeriod;
    }

    public void setUsagePeriod(UsagePeriod usagePeriod) {
        this.usagePeriod = usagePeriod;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
