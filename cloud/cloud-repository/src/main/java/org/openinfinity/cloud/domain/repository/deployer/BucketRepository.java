/*
 * Copyright (c) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openinfinity.cloud.domain.repository.deployer;

import java.io.InputStream;
import java.util.Map;

/**
 * Standardized interface for creating buckets.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0 
 * @since 1.0.0
 */
public interface BucketRepository {
	
	/**
	 * Unique exception message for connection failure. Can be used with error message localization. 
	 */
	public static final String EXCEPTION_MESSAGE_CONNECTION_FAILURE = "system.exception.bucket.repository.connection.failure";

	/**
	 * Creates bucket based on file input stream.
	 * 
	 * @param inputStream Represents the file input stream.
	 * @param bucketName Represents the file name of the deployment. 
	 * @param key Represents the key under which to store the new object.
	 * @param metadataMap Represents the key value based metadata to object.
	 * @return URI of the created resource.
	 */
	String createBucket(InputStream inputStream, String bucketName, String key, Map<String, String> metadataMap);
	
	/**
	 * Retrieves bucket based on bucket name.
	 * 
	 * @param bucketName Defines the name of the bucket.
	 * @param key Defines the name of the object stored inside the bucket.
	 * @return Inputstream represents the stream of the bucket.
	 */
	InputStream load(String bucketName, String key);

	/**
	 * Retrieves bucked meta data based on bucket name.
	 * 
	 * @param bucketName Defines the name of the bucket.
	 * @param key Defines the name of the object stored inside the bucket.
	 * @return Meta data as a string map.
	 * @author Timo Saarinen
	 */
	Map<String, String> readMetadata(String bucketName, String key);
	
	/**
	 * Deletes object in a bucket bucket.
	 * 
	 * @param bucketName Defines the name of the bucket.
	 * @param key Defines the name of the object stored inside the bucket.
	 */
	void deleteObject(String bucketName, String key);
	

	/**
	 * Deletes all object in a bucket and the bucket itself.
	 * 
	 * @param bucketName
	 */
	public void deleteBucketAndObjects(String bucketName);
	
}
