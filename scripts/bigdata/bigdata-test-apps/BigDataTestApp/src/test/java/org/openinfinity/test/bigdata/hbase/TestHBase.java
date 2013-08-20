package org.openinfinity.test.bigdata.hbase;

import org.junit.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This application tests HBase cluster.
 *
 */
public class TestHBase
{
	private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
            "/spring-application-context.xml");
	
    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * Ensure, that connection to HBase is available and that 
     * required structures exist. 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    	getApplicationContext();
    }
    
    @Test
    public void testNormalWriteAndRead() {
    }
    
    @Test
    public void testNormalMapReduce() {
    }
    
}
