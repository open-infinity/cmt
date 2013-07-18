package org.openinfinity.cloud.application.invoicing.batch;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareInvoice;
import org.openinfinity.cloud.service.invoicing.InstanceShareInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


/**
 * Dummy {@link ItemWriter} which only logs data it receives.
 */
@Component("writer")
public class InvoiceItemWriter implements ItemWriter<Map<Integer, InstanceInvoice>>, StepExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceItemWriter.class);
	
	@Value("${output.file}")
	private String outputFileName;
	
	@Value("${line.separator}")
	private String LF;
	
	@Value("${batch.save_receipt}")
	private boolean saveReceipt;
	
	@Value("${batch.send_email}")
	private boolean sendEmail;
	
	@Value("${invoice_batch.userid}")
	private int batchUserId;
	
	@Value("${mail.to}")
	private String[] mailTo;
	
	@Value("${mail.from}")
	private String mailFrom;
	
	@Value("${mail.host}")
	private String host;
	
	@Value("${mail.port}")
	private int port;
	
	private File outputFile;
	
	@Autowired
	private InstanceShareInvoiceService instanceShareInvoiceService;
	
	/**
	 * @see ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends Map<Integer, InstanceInvoice>> instanceInvoicesList) throws Exception {
	
		if (instanceInvoicesList == null) {
			return;
		}
		
		List<InstanceShareInvoice> instanceShareInvoices = new ArrayList<InstanceShareInvoice>();
		Timestamp ts = new Timestamp(new Date().getTime());
		
		Map<Integer, InstanceInvoice> instanceInvoices = instanceInvoicesList.get(0);
		for (InstanceInvoice instanceInvoice : instanceInvoices.values()) {
			// Write to CSV file
			FileUtils.writeStringToFile(outputFile, instanceInvoice.toCSV(LF, ";"), true);
			
			// Construct a invoice object if share exists
			InstanceShare instanceShare = instanceInvoice.getInstanceShare();
			if (instanceShare != null) {
				InstanceShareInvoice instanceShareInvoice = new InstanceShareInvoice();
				instanceShareInvoice.setInstanceShare(instanceInvoice.getInstanceShare());
				instanceShareInvoice.setCreated(ts);
				instanceShareInvoice.setCreatedBy(batchUserId);
				instanceShareInvoice.setModified(ts);
				instanceShareInvoice.setModifiedBy(batchUserId);
				instanceShareInvoice.setPeriodStart(instanceInvoice.getPeriodStart());
				instanceShareInvoice.setPeriodEnd(instanceInvoice.getPeriodEnd());
				instanceShareInvoice.setTotalUsage(instanceInvoice.sumUptimeInMinutes());
				
				instanceShareInvoices.add(instanceShareInvoice);
			} else {
				LOGGER.warn("Share was not found for instance id : {}. Can not write to instance_share_invoice_tbl",
						instanceInvoice.getInstance().getInstanceId());
			}
		}
		
		if (saveReceipt == true) {
			// Write receipt to DB
			instanceShareInvoiceService.save(instanceShareInvoices);
		} else {
			LOGGER.info("Property batch.save_receipt was set to false. Not saving receipt.");
		}
		
		sendEmail();
	}
	
	public void sendEmail() throws Exception {
		if (sendEmail == false) {
			LOGGER.info("Property batch.send_email was set to false. Not sending email.");
			return;
		}
		
		for (int i = 0; i < mailTo.length; i++) {
			LOGGER.info("Mail to: {}", mailTo[i]);
		}
		
		// Send mail
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(host);
		sender.setPort(port);
		
		MimeMessage message = sender.createMimeMessage();
		// use the true flag to indicate you need a multipart message
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(mailTo);
		helper.setFrom(mailFrom);
		helper.setSubject("Invoice period from TODO to TODO");
		helper.setText("Invoice CSV file attached.");
		FileSystemResource file = new FileSystemResource(outputFile);
		helper.addAttachment(outputFile.getName(), file);
		sender.send(message);
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		LOGGER.debug("Creating a file");
		outputFile = new File(outputFileName);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// Nothing to do.
		return null;
	}

}
