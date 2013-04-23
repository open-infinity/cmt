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

import org.jets3t.service.Constants;
import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

import org.openinfinity.cloud.util.credentials.ProviderCredentialsImpl;
import org.openinfinity.core.exception.ExceptionLevel;
import org.openinfinity.core.util.ExceptionUtil;
import org.openinfinity.core.util.IOUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Standardized Jets3t interface for creating buckets with simple storage services.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0 
 * @since 1.0.0
 */
@Repository("jetS3Repository")
public class BucketRepositoryJets3tImpl implements BucketRepository {
	private S3Service simpleStorageService;
	
	@Value("${accesskeyid}") 
	private String accesskeyid;
	
	@Value("${secretkey}") 
	private String secretkey;

	static {
		Constants.JETS3T_PROPERTIES_FILENAME = "META-INF/properties/jets3.properties";
	}
	
	/**
	 * Puts file input stream to a bucket belonging to a cluster. Creates or uses existing bucket for the cluster.
	 * 
	 * @param inputStream represents the file input stream.
	 * @param bucketName represents a cluster id of a cluster that will use the bucket where input stream will be stores.  
	 * @param key represents the key under which to store the new object.
	 * @param metadataMap Represents the key value based metadata to object.
	 * @return String Represents the bucket key value of the created resource.
	 */
	public String createBucket(InputStream inputStream, String bucketName, String key, Map<String, String> metadataMap) {
		String resource = "";
		try {
			simpleStorageService = new RestS3Service(new ProviderCredentialsImpl(accesskeyid, secretkey));		
			S3Bucket bucket = simpleStorageService.getOrCreateBucket(bucketName, S3Bucket.LOCATION_EUROPE);
			S3Object object = new S3Object(key);
			for (Map.Entry<String, String> entry : metadataMap.entrySet()) object.addMetadata(entry.getKey(), entry.getValue());	
			object.setDataInputStream(inputStream);
			object = simpleStorageService.putObject(bucket, object);		
			resource = key;
		} catch (Throwable throwable) {
			ExceptionUtil.throwSystemException(throwable.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);
		} finally {
			IOUtil.closeStream(inputStream);                      
		}
		return resource;
	}
	
	/**
	 * Loads stream based on bucket name and key.
	 */
	public InputStream load(String bucketName, String key) {
		InputStream inputStream = null;
		try {
			simpleStorageService = new RestS3Service(new ProviderCredentialsImpl(accesskeyid, secretkey));		
			inputStream = simpleStorageService.getObject(bucketName, key).getDataInputStream();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			ExceptionUtil.throwSystemException(throwable.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);
		}
		return inputStream;
	}
	
}