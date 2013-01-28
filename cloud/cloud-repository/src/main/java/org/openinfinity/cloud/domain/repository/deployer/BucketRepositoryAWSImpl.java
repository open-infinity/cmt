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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Standardized interface for creating and retrieving buckets and it's objects.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0 
 * @since 1.0.0
 */
@Repository
public class BucketRepositoryAWSImpl implements BucketRepository {
	
	private AmazonS3 simpleStorageService;
	
	@Value("${awsbucketendpoint}")
	private String endpoint = "http://localhost:8080";
	
	@Autowired
	public BucketRepositoryAWSImpl(AWSCredentials credentials) throws Throwable {
		simpleStorageService = new AmazonS3Client(credentials);
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

}