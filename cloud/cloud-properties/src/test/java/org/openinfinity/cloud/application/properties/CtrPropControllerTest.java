package org.openinfinity.cloud.application.properties;

import static org.junit.Assert.*;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.application.properties.controller.SharedPropertiesController;
import org.openinfinity.cloud.domain.SharedProperty;
import org.openinfinity.cloud.service.properties.CentralizedPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test the basic functionality of the service needed by Properties Controller.
 * 
 * @author Timo Saarinen
 */
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/cloud-properties-portlet.xml", "file:src/test/resources/cloud-properties-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class CtrPropControllerTest {
	private static final Logger logger = Logger.getLogger(CtrPropControllerTest.class.getName());

	@Autowired
	@Qualifier("centralizedPropertiesService")
	private CentralizedPropertiesService service;

	public static final String ORGANIZATION_ID = "-1";
	public static final String INSTANCE_ID = "-2";
	public static final String CLUSTER_ID = "-3";
	//public static final String PROP_ID = "-4";
	public static final String PROP_KEY1 = "Key 1";
	public static final String PROP_KEY2 = "Key 2";
	public static final String PROP_VALUE1 = "Value 1";
	public static final String PROP_VALUE2 = "Value 2";

	private SharedProperty currentSharedProperty(String key) {
		SharedProperty p = new SharedProperty();
		/*p.setOrganizationId(ORGANIZATION_ID);
		p.setInstanceId(INSTANCE_ID);
		p.setClusterId(CLUSTER_ID);
		p.setKey(key);*/
		return p;
	}
	
	@Test 
	public void testStoreAndRead() {
		assertNotNull("The service can't be null!", service);
		
		SharedProperty prop_written = currentSharedProperty(PROP_KEY1);
		prop_written.setValue(PROP_VALUE1);
		service.store(prop_written);
		
		SharedProperty prop_read = service.load(currentSharedProperty(PROP_KEY1));
		boolean deleted = service.delete(currentSharedProperty(PROP_KEY1));
		
		assertEquals(prop_written, prop_read);
		assertEquals(deleted, true);
	}

	/*
	public SharedProperty store(SharedProperty prop);
	public Collection<SharedProperty> loadAll(SharedProperty sample);
	public SharedProperty load(SharedProperty sample);
	public boolean delete(SharedProperty prop);
	public boolean rename(SharedProperty prop, String newkey);
	*/

	@Test 
	public void testLoadAll() {
		SharedProperty p1 = currentSharedProperty(PROP_KEY1);
		p1.setValue(PROP_VALUE1);
		service.store(p1);
		
		SharedProperty p2 = currentSharedProperty(PROP_KEY2);
		p1.setValue(PROP_VALUE2);
		service.store(p2);

		SharedProperty p = currentSharedProperty(null);
		Collection<SharedProperty> ps;

		ps = service.loadAll(p);
		assertEquals(ps.size(), 2);

		//p.setInstanceId(null);
		ps = service.loadAll(p);
		assertEquals(ps.size(), 2);

		//p.setClusterId(null);
		ps = service.loadAll(p);
		assertEquals(ps.size(), 2);

		assertEquals(service.delete(p1), true);
		assertEquals(service.delete(p2), true);
		assertEquals(service.loadAll(p).size(), 0);
	}


	@Test 
	public void testRename() {
		SharedProperty p1 = currentSharedProperty(PROP_KEY1);
		p1.setValue(PROP_VALUE1);
		service.store(p1);
		
		assertEquals(service.rename(p1, PROP_KEY2), true);
		assertEquals(service.rename(p1, PROP_KEY2), false);
		
		SharedProperty p2 = currentSharedProperty(PROP_KEY2);
		
		assertEquals(service.delete(p1), false);
		assertEquals(service.delete(p2), true);
	}


/*
	@Test
	public void testChangeOrganization() {
		try {
			controller.changeOrganization(null, null, ORGANIZATION_ID);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testSaveProperty() {
		try {
			controller.changeOrganization(null, null, ORGANIZATION_ID);
			controller.saveProperty(null, null, PROP_ID, PROP_KEY1, PROP_VALUE1);
			controller.deleteProperty(null, null, PROP_ID, PROP_KEY1);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testChangeKey() {
		try {
			controller.changeOrganization(null, null, ORGANIZATION_ID);
			controller.saveProperty(null, null, PROP_ID, PROP_KEY1, PROP_VALUE1);
			controller.changeKey(null, null, PROP_ID, PROP_KEY2, PROP_KEY1);
			controller.deleteProperty(null, null, PROP_ID, PROP_KEY2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
*/	
}
