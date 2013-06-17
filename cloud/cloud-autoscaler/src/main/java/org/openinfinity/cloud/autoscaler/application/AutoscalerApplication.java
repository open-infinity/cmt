package org.openinfinity.cloud.autoscaler.application;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AutoscalerApplication {

	public static void main( String[] args ){    	
    	new ClassPathXmlApplicationContext("/autoscaler-context.xml", AutoscalerApplication.class);
    	
    }
    
}