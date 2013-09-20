package org.openinfinity.cloud.autoscaler.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

/**
 * Cloud-autoscaler periodic scaler, functional tests
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

public class DatabaseUtils {

	public static void updateTestDatabase(IDataSet dataSet, DataSource dataSource){
		try {
			IDatabaseConnection dbConn = new DatabaseDataSourceConnection(dataSource);
			DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static IDataSet initDataSet(Object obj) throws Exception
    {
        long now = System.currentTimeMillis();
	    Timestamp from = new Timestamp(now + 2100);
	    Timestamp to = new Timestamp(now + 7200 );
		ReplacementDataSet dataSet = null;
		
		try{		
			URL resourceLocation = obj.getClass().getClassLoader().getResource("META-INF/sql/dataset-init-scale-out.xml");
	        dataSet = new ReplacementDataSet(new FlatXmlDataSetBuilder().
	            build(new FileInputStream(new File(resourceLocation.toURI())))); 
	        dataSet.addReplacementObject("[from]", from);
	        dataSet.addReplacementObject("[to]", to);
		}
		catch (Exception e){
		    e.printStackTrace();
		}
        return dataSet;
    }
}
