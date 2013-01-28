package org.openinfinity.cloud.autoscaler.application;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AutoscalerApplication {

	public static void main( String[] args ){    	
		//new ClassPathXmlApplicationContext("/application-context.xml", App.class);

    	new ClassPathXmlApplicationContext("/autoscaler-context.xml", AutoscalerApplication.class);
    	//classpath*:META-INF/spring/test-scheduled-scaler-context.xml"
    }
    
}