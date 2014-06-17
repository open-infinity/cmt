package org.openinfinity.cloud.ssp.billing.invoicecreator;

import org.openinfinity.cloud.domain.ssp.Invoice;
import org.openinfinity.cloud.domain.ssp.InvoiceItem;

import java.util.Collection;

public class InvoiceAggregator {
	
	private Invoice invoice;
	
	private Collection<InvoiceItem> invoiceItems;

    public InvoiceAggregator(Invoice invoice, Collection<InvoiceItem> invoiceItems) {
        this.invoice = invoice;
        this.invoiceItems = invoiceItems;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Collection<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(Collection<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }
}
