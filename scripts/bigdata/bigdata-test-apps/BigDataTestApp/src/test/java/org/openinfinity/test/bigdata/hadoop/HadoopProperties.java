package org.openinfinity.test.bigdata.hadoop;

/**
 * Properties for HadoopTestApp from hadoop.properties file.
 * @author Timo Saarinen
 */
public class HadoopProperties {
	private String hmasterHost;
	private String hmasterSshKeyFilename;
	
	public String getHmasterHost() {
		return hmasterHost;
	}
	public void setHmasterHost(String hmasterHost) {
		this.hmasterHost = hmasterHost;
	}
	public String getHmasterSshKeyFilename() {
		return hmasterSshKeyFilename;
	}
	public void setHmasterSshKeyFilename(String hmasterSshKeyFilename) {
		this.hmasterSshKeyFilename = hmasterSshKeyFilename;
	}
	
}
