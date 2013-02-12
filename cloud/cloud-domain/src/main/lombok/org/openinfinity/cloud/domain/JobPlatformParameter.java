package org.openinfinity.cloud.domain;

import lombok.Data;

/**
 * JobPlatformParameter domain object
 * @author Pasi Kilponen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
@Data
public class JobPlatformParameter {
	private int id;
	private int jobId;
	private String key;
	private String value;

	// Constructors
	public JobPlatformParameter (){};
	
	public JobPlatformParameter (String aKey, String aValue){
		key = aKey;
		value = aValue;
	}
}
