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

	public static final String SQL_SCALE_OUT = ("META-INF/sql/dataset-init-scale-out.xml");
	public static final String SQL_SCALE_IN = ("META-INF/sql/dataset-init-scale-in.xml");

	public static void updateTestDatabase(IDataSet dataSet, DataSource dataSource){
		try {
			IDatabaseConnection dbConn = new DatabaseDataSourceConnection(dataSource);
			DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static IDataSet initDataSet(Object obj, String sqlScript, Timestamp from, Timestamp to) throws Exception
    {
		ReplacementDataSet dataSet = null;
		
		try{		
			URL resourceLocation = obj.getClass().getClassLoader().getResource(sqlScript);
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
