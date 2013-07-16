package org.openinfinity.cloud.service.usage;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.MachineUsage;
import org.openinfinity.cloud.domain.UsageHour;
import org.openinfinity.cloud.domain.UsagePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath*:META-INF/spring/service-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class UsageServiceImplTest {

	@Autowired
	UsageService usageService;

	/*
	@Before
	public void before() {
		// You can enable this to investigate the data in the H2. Opens a GUI.
		org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url",
				"jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
				"--user", "sa", "--password", "sa", "--noexit"});
	}
	*/

	@Test
	public void testUpTimeHoursPerMachine() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startTime = null;
		try {
			startTime = sdf.parse("01/06/2013");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date endTime = new Date(); // Current time

		UsagePeriod usagePeriod = usageService.loadUsagePeriod(10495,
				startTime, endTime);
		
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		System.out.println("*************************");
		System.out.println("Map: " + actual.toString());
		System.out.println("*************************");
		
		assertEquals(1, actual.get(1).getMachineId());
		assertEquals(1, actual.get(1).getInstanceId());
		assertEquals(525000, actual.get(1).getUptime());
		assertEquals(525, actual.get(1).getUptimeInSeconds());
		assertEquals(8, actual.get(1).getUptimeInMinutes());
		assertEquals(MachineUsage.State.USAGE_DATA_VALID, actual.get(1).getState());
		assertEquals(0, actual.get(1).getErrorCount());
		assertEquals(0, actual.get(1).getErrorMessage().length());
		
	}
	
	// TODO: missing asserts.
	@Test
	public void testLoadUsagePeriod() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startTime = null;
		try {
			startTime = sdf.parse("01/06/2013");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date endTime = new Date(); // Current time

		UsagePeriod usagePeriod = usageService.loadUsagePeriod(10495,
				startTime, endTime);
		Collection<UsageHour> usageHours = usagePeriod.getUsageHours();

		System.out.println(usagePeriod);

		System.out.println("Row count: " + usageHours.size());

		System.out.println("Print usage hours:");
		for (UsageHour usage : usageHours) {
			System.out.println(usage);
		}
		System.out.println("==================");

		System.out.println("Uptime: " + usagePeriod.getUptime());
		System.out.println("Downtime: " + usagePeriod.getDowntime());

	}

	@Test
	public void testStartVirtualMachineUsageMonitoring() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startTime = null;
		try {
			startTime = sdf.parse("01/06/2013");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		usageService.startVirtualMachineUsageMonitoring(10495, 10, 1106, 38);

		// Case 1
		Date endTime = new Date(System.currentTimeMillis() + 100);	// Current
																	// time, and
																	// add some
																	// buffer..
		UsagePeriod usagePeriod1 = usageService.loadUsagePeriod(10495,
				startTime, endTime);

		Collection<UsageHour> usageHours1 = usagePeriod1.getUsageHours();
		List<UsageHour> list1 = new ArrayList<UsageHour>(usageHours1);

		UsageHour usage1 = list1.get(list1.size() - 1);

		assertEquals(720, usage1.getInstanceId());
		assertEquals(1106, usage1.getClusterId());
		assertEquals("Service platform", usage1.getClusterTypeTitle());
		assertEquals(1, usage1.getMachineTypeId());
		assertEquals("Medium", usage1.getMachineTypeName());
		assertEquals("Cores: 2, RAM: 2GB, Disk: 10GB",
				usage1.getMachineTypeSpec());
		assertEquals(0, usage1.getClusterEbsImageUsed());
		assertEquals(0, usage1.getClusterEbsVolumesUsed());

		// Case 2
		endTime = new Date(System.currentTimeMillis() + 100);	// Current time,
																// and add some
																// buffer..
		usageService.startVirtualMachineUsageMonitoring(10495, 10, 1107, 48);

		UsagePeriod usagePeriod2 = usageService.loadUsagePeriod(10495,
				startTime, endTime);

		Collection<UsageHour> usageHours2 = usagePeriod2.getUsageHours();
		List<UsageHour> list2 = new ArrayList<UsageHour>(usageHours2);

		UsageHour usage2 = list2.get(list2.size() - 1);

		assertEquals(720, usage1.getInstanceId());
		assertEquals(1107, usage2.getClusterId());
		assertEquals("Portal platform", usage2.getClusterTypeTitle());
		assertEquals(2, usage2.getMachineTypeId());
		assertEquals("Large", usage2.getMachineTypeName());
		assertEquals("Cores: 4, RAM: 4GB, Disk: 10GB",
				usage2.getMachineTypeSpec());
		assertEquals(1, usage2.getClusterEbsImageUsed());
		assertEquals(99, usage2.getClusterEbsVolumesUsed());

	}
}
