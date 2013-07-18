package org.openinfinity.cloud.application.invoicing.batch;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.MachineUsage;
import org.openinfinity.cloud.domain.UsagePeriod;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.invoicing.InstanceShareService;
import org.openinfinity.cloud.service.usage.UsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * {@link ItemReader} with hard-coded input data.
 */

@Component("reader")
public class InvoiceItemReader implements ItemReader<Map<Integer, InstanceInvoice>>, StepExecutionListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceItemReader.class);
	
	/*
	 * Paramters for the batch job 
	 */
	@Value("${usage.organizationId}")
	private long organizationId;
	
	@Value("${usage.periodStart}")
	private String periodStartStr;
	
	@Value("${usage.periodEnd}")
	private String periodEndStr;
	
	@Value("${usage.dateFormatPattern}")
	private String dateFormatPattern;
	
	@Autowired
	private InstanceService instanceService;
	
	@Autowired
	private UsageService usageService;
	
	@Autowired
	private InstanceShareService instanceShareService;
	
	private Map<Integer, InstanceInvoice> instanceInvoices;
	
	private boolean sent = false;
	
	/**
	 * Reads next record from input
	 */
	public Map<Integer, InstanceInvoice> read() throws Exception {
		
		if (sent) {
			return null;
		}
		
		sent = true;
		return instanceInvoices;
		
	}

	/**
	 * Create new Map where InstanceIvoice object are saved. Key of this map is instance id.
	 * Share data is fetched only once for the instance. Usage data is saved to the InstanceInvoice
	 * object so machine usages are grouped to the instance.
	 * 
	 * This Map is eventually passed to the writer.
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		
		instanceInvoices = new HashMap<Integer, InstanceInvoice>();
		
		DateTimeFormatter dateFormat = DateTimeFormat.forPattern(dateFormatPattern);
		DateTime periodStart = dateFormat.parseDateTime(periodStartStr);
		DateTime periodEnd = dateFormat.parseDateTime(periodEndStr);
		
		LOGGER.info("Starting the invoice batch for period: {} to {}", periodStart.toString(), periodEnd.toString());
		
		UsagePeriod usagePeriod = usageService.loadUsagePeriod(organizationId, periodStart.toDate(), periodEnd.toDate());
		Map<Integer, MachineUsage> machineUsages = usagePeriod.getUptimeHoursPerMachine();
		
		LOGGER.debug("UsagePeriod for organization id : {}", usagePeriod.getOrganizationId());
		LOGGER.debug("UsagePeriods : {}", machineUsages);
		
		for (MachineUsage machineUsage : machineUsages.values()) {
			LOGGER.debug("Machine usage : {}", machineUsage);
			
			int instanceId = machineUsage.getInstanceId();
			
			InstanceInvoice instanceInvoice = instanceInvoices.get(instanceId); 
					
			if (instanceInvoice == null) {
				/*
				 * Can not find from Map by instance id, so create instance invoice and fetch the share data.
				 */
				
				instanceInvoice = new InstanceInvoice();
				instanceInvoices.put(instanceId, instanceInvoice);
				
				instanceInvoice.setInstance(instanceService.getInstanceAlsoPassive(instanceId));
				
				InstanceShare instanceShare = instanceShareService.findLatestByInstanceIdAndPeriodStart(instanceId, periodStart.toDate());
				if (instanceShare == null) {
					LOGGER.warn("No shares for instance id : {}", instanceId);
					instanceInvoice.setInstanceShare(null);
				} else {
					LOGGER.debug("InstanceShare id : {}, period start {}", instanceShare.getId(), instanceShare.getPeriodStart());
					
					// Save the share information
					instanceInvoice.setInstanceShare(instanceShare);
				}
			}
			/*
			 * Instance id was found from from Map or created and added to map, so just save the usage data.
			 */
			instanceInvoice.setPeriodStart(periodStart.toDate());
			instanceInvoice.setPeriodEnd(periodEnd.toDate());
			instanceInvoice.setMachineUsage(machineUsage);
			
		}
		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}

}
