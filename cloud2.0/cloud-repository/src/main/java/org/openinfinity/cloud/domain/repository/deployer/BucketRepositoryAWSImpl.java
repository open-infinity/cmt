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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
//import org.jets3t.service.impl.rest.httpclient.RestS3Service;
//import org.jets3t.service.model.S3Object;
import org.openinfinity.cloud.util.credentials.ProviderCredentialsImpl;
import org.openinfinity.core.exception.ExceptionLevel;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * Standardized interface for creating and retrieving buckets and it's objects.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0 
 * @since 1.0.0
 */
@Repository
public class BucketRepositoryAWSImpl implements BucketRepository {
	private static final Logger LOGGER = Logger.getLogger(BucketRepositoryAWSImpl.class.getName());
	
	private AmazonS3 simpleStorageService;
	
	@Value("${awsbucketendpoint}")
	private String endpoint = "http://localhost:8080";
	
	@Autowired
	@Qualifier("cloudCredentials")
	private AWSCredentials eucaCredentials;
	
	public BucketRepositoryAWSImpl() throws Throwable {
		simpleStorageService = new AmazonS3Client(eucaCredentials);
		simpleStorageService.setEndpoint(endpoint);
	}
	
	/**
	 * Creates bucket based on file input stream.
	 * 
	 * @param inputStream Represents the file input stream.
	 * @param bucketName Represents the file name of the deployment. 
	 * @param key Represents the key under which to store the new object.
	 * @return key Defines the created resource key.
	 */
	public String createBucket(InputStream inputStream, String bucketName, String key, Map<String, String> metadataMap) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setUserMetadata(metadataMap);
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
		simpleStorageService.createBucket(bucketName);
		simpleStorageService.putObject(putObjectRequest);
		return key;
	}
	
	/**
	 * Loads bucket content based on bucket's name and key.
	 */
	public InputStream load(String bucketName, String key) {
		return simpleStorageService.getObject(bucketName, key).getObjectContent();
	}

	/**
	 * Tests whether the given bucket name and key exist in the repository.
	 */
	public boolean has(String bucketName, String key) {
		if (simpleStorageService.doesBucketExist(bucketName)) {
			try {
				simpleStorageService.getObjectMetadata(bucketName, key);
				return true;
			} catch (AmazonServiceException e) {
				LOGGER.debug(e.getMessage());
			}
		}
		return false;
	}
	

	/**
	 * Retrieves bucked meta data based on bucket name.
	 * @author Timo Saarinen
	 */
	public Map<String, String> readMetadata(String bucketName, String key) {
		ObjectMetadata omd = simpleStorageService.getObjectMetadata(bucketName, key);
		return (omd != null) ? omd.getUserMetadata() : null;
	}
	
	/**
	 * Delete object
	 */
	public void deleteObject(String bucketName, String key) {		
		simpleStorageService.deleteObject(bucketName, key);
	}
	
	public void deleteBucketAndObjects(String bucketName) {
		try {
			// list and delete objects in a bucket 
			LOGGER.debug("deleteBucketAndObjects called for bucket: <"+bucketName+">.");
			ObjectListing objects = simpleStorageService.listObjects(bucketName);
			
			do {
		        for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
					LOGGER.debug("Trying to delete object <"+objectSummary.getKey()+"> from bucket <: "+objectSummary.getBucketName()+">.");
					simpleStorageService.deleteObject(bucketName, objectSummary.getKey());
		        }
		        objects = simpleStorageService.listNextBatchOfObjects(objects);
			} while (objects.isTruncated());
						
				
			// Now that the bucket is empty, you can delete it. If you try to delete your bucket before it is empty, it will fail.
			LOGGER.debug("Trying to delete bucket <: "+bucketName+">.");
			simpleStorageService.deleteBucket(bucketName);
			//System.out.println("Deleted bucket " + testBucket.getName());		
			
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error in deleting objects and bucket: "+e+" -- "+e.getStackTrace().toString());
			e.printStackTrace();
			ExceptionUtil.throwSystemException(e.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);						
		}			
	}
		
}