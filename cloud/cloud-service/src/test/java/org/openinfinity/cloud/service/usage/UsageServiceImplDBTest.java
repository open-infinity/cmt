package org.openinfinity.cloud.service.usage;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.MachineUsage;
import org.openinfinity.cloud.domain.UsagePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath*:META-INF/spring/service-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class UsageServiceImplDBTest {
	
	@Value("${jdbc.driverClassName}")
	private static String jdbcDriverClassName;
	
	@Value("${jdbc.url}")
	private static String jdbcUrl;
	
	@Value("${jdbc.username}")
	private static String jdbcUsername;
	
	@Value("${jdbc.password}")
	private static String jdbcPassword;
	
	@Autowired
	UsageService usageService;

	// Date format
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	// Data sets
	// TODO: 	Modify maven build so that the data set xml file can be in the same package as this
	//			test class is. At the moment it is located in the resource directory.
	private String dataset1 = "/UsageServiceImplDBTest_dataset1.xml";
	
	@Before
	public void before() throws Exception {
		Properties props = new Properties();
		props.load(UsageServiceImplDBTest.class.getClassLoader().getResourceAsStream("system.properties"));
		
		jdbcDriverClassName = props.getProperty("jdbc.driverClassName");
		jdbcUrl = props.getProperty("jdbc.url");
		jdbcUsername = props.getProperty("jdbc.username");
		jdbcPassword = props.getProperty("jdbc.password");
		
		fullDatabaseImport(dataset1);
	}
	
	private void peekToHSQL() {
		// You can enable this to investigate the data in the H2. Opens a GUI.
				org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url",
						jdbcUrl,
						"--user", jdbcUsername, "--password", jdbcPassword, "--noexit"});
	}
	
	/**
	 * Tests that the period start and end are same with machine start and stop events.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event   20.6.2013 12:00:00
	 * 
	 * Period start  5.6.2013  0:00:00
	 * Period end   20.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodStartSameAndEndSameAsMachineStartAndStopEvent() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("5/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("20/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		assertEquals(20880, actual.get(1).getUptimeInMinutes());
		
		//peekToHSQL();
	}
	
	/**
	 * Test that the period start earlier and ends later than the machine start and stop events.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event   20.6.2013 12:00:00
	 * 
	 * Period start  1.6.2013  0:00:00
	 * Period end   30.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodStartEarlierAndEndLaterThanMachineStartAndStopEvents() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("1/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("30/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		assertEquals(21600, actual.get(1).getUptimeInMinutes());
	}
	
	/**
	 * Test that the period start later and ends earlier than the machine start and stop events.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event   20.6.2013 12:00:00
	 * 
	 * Period start 10.6.2013  0:00:00
	 * Period end   15.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodStartLaterAndEndEarlierThanMachineStartAndStopEvents() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("10/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("15/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		assertEquals(7200, actual.get(1).getUptimeInMinutes());
	}
	
	/**
	 * Test that the period start earlier and ends earlier than the machine start and stop events.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event   20.6.2013 12:00:00
	 * 
	 * Period start  1.6.2013  0:00:00
	 * Period end   15.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodStartEarlierAndEndEarlierThanMachineStartAndStopEvents() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("1/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("15/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		assertEquals(13680, actual.get(1).getUptimeInMinutes());
	}
	
	/**
	 * Test that the period start later and ends later than the machine start and stop events.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event   20.6.2013 12:00:00
	 * 
	 * Period start 10.6.2013  0:00:00
	 * Period end   30.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodStartLaterAndEndLaterThanMachineStartAndStopEvents() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("10/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("30/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		assertEquals(15120, actual.get(1).getUptimeInMinutes());
	}

	/**
	 * Test that the period starts later and than the machine stop event.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event   20.6.2013 12:00:00
	 * 
	 * Period start 22.6.2013  0:00:00
	 * Period end   30.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodStartsLaterThanMachineStopEvent() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("22/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("30/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();

		//There should not be any uptime data
		assertEquals(0, actual.get(1).getUptimeInMinutes());
	}

	/**
	 * Test that the period starts after machine stop event.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event   20.6.2013 12:00:00
	 * 
	 * Period start 22.6.2013  0:00:00
	 * Period end   30.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodEndsBeforeMachineStartEvent() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("1/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("4/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();

		//There should not be any uptime data
		assertEquals(0, actual.get(1).getUptimeInMinutes());
	}	
	
	/**
	 * Test that the period start is same as machine start event. The machine has no stop event.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event    No stop event for the machine
	 * 
	 * Period start  5.6.2013  0:00:00
	 * Period end   20.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodStartSameAsMachineStartEvent() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("5/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("20/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		assertEquals(20880, actual.get(21).getUptimeInMinutes());
	}
	
	/**
	 * Test that the period starts earlier than the machine start event. The machine has no stop event.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event    No stop event for the machine
	 * 
	 * Period start  1.6.2013  0:00:00
	 * Period end   30.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodStartEarlierThanMachineStartEvent() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("1/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("30/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		assertEquals(35280, actual.get(21).getUptimeInMinutes());
	}

	/**
	 * Test that the period starts later than the machine start event. The machine has no stop event.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event    No stop event for the machine
	 * 
	 * Period start  10.6.2013  0:00:00
	 * Period end    30.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodStartLaterThanMachineStartEvent() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("10/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("30/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		assertEquals(28800, actual.get(21).getUptimeInMinutes());
	}
	
	/**
	 * Test that the period ends earlier than the machine start event. The machine has no stop event.
	 * 
	 * Start event   5.6.2013 12:00:00
	 * Stop event    No stop event for the machine
	 * 
	 * Period start  1.6.2013  0:00:00
	 * Period end    4.6.2013  0:00:00
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPeriodEndEarlierThanMachineStartEvent() throws Exception {
		int organizationId = 10495;
		
		Date periodStart = returnDate("1/6/2013"); // Note, 0:00 o'clock
		Date periodEnd = returnDate("4/6/2013");
		
		UsagePeriod usagePeriod = usageService.loadUsage(organizationId);
		usagePeriod.setStartTime(periodStart);
		usagePeriod.setEndTime(periodEnd);
		Map<Integer, MachineUsage> actual = usagePeriod.getUptimeHoursPerMachine();
		
		assertEquals(0, actual.get(21).getUptimeInMinutes());
	}
	
	
	/**
	 * Read data set xml file from classpath from the same package where the test class is located.
	 * 
	 * @param fileName to be loaded e.g. "dataset.xml"
	 * @throws DatabaseUnitException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void fullDatabaseImport(String fileName) throws ClassNotFoundException, SQLException, DatabaseUnitException {
		InputStream istream = UsageServiceImplDBTest.class.getResourceAsStream(fileName);
		
		IDatabaseConnection connection = getConnection();
		
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(istream);
		
		DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
	}
	
	/**
	 * Get the database connection for the DBUnit.
	 * 
	 * @return DBUnits IDatabaseConnection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws DatabaseUnitException
	 */
	public static IDatabaseConnection getConnection() throws ClassNotFoundException, SQLException, DatabaseUnitException {
		@SuppressWarnings({ "unused", "rawtypes" })
		Class driverClass = Class.forName(jdbcDriverClassName);
		Connection jdbcConnection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
		return new DatabaseConnection(jdbcConnection);
	}
	
	public Date returnDate(String dateStr) {
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}

}
