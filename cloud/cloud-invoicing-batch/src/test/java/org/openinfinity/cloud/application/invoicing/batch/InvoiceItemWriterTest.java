package org.openinfinity.cloud.application.invoicing.batch;

import org.openinfinity.cloud.application.invoicing.batch.InvoiceItemWriter;

import junit.framework.TestCase;

public class InvoiceItemWriterTest extends TestCase {

	private InvoiceItemWriter writer = new InvoiceItemWriter();
	
	public void testWrite() throws Exception {
		writer.write(null); // nothing bad happens
	}

}
