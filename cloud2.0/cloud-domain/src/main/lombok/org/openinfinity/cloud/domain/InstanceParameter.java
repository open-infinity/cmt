package org.openinfinity.cloud.domain;

import lombok.Data;

/**
 * InstanceParameter domain object
 * @author Ari Simanainen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
@Data
public class InstanceParameter {
	private int id;
	private int instanceId;
	private String key;
	private String value;

	// Constructors
	public InstanceParameter (){};
	
	public InstanceParameter (String aKey, String aValue){
		key = aKey;
		value = aValue;
	}
}
