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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;

import org.apache.log4j.Logger;
import org.jets3t.service.Constants;
import org.jets3t.service.S3Service;
import org.jets3t.service.ServiceException;
import org.jets3t.service.StorageService;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.openinfinity.cloud.util.credentials.ProviderCredentialsImpl;
import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.openinfinity.core.exception.ExceptionLevel;
import org.openinfinity.core.util.ExceptionUtil;
import org.openinfinity.core.util.IOUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.model.ObjectMetadata;

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
	private static final Logger LOGGER = Logger.getLogger(BucketRepositoryJets3tImpl.class.getName());
	
	private S3Service simpleStorageService;
	
	@Value("${eucaaccesskeyid}") 
	private String accesskeyid;
	
	@Value("${eucasecretkey}") 
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
	@Log
	@AuditTrail		
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
			LOGGER.error("Error in createBucket: "+throwable.getMessage());
			ExceptionUtil.throwSystemException(throwable.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);
		} finally {
			IOUtil.closeStream(inputStream);                      
		}
		LOGGER.debug("Object inserted to bucket <"+bucketName+"> with key <"+key+">. Returning resource: <"+resource+">." );
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

	/**
	 * Tests whether the given bucket name and key exist in the repository.
	 */
	public boolean has(String bucketName, String key) {
		try {
			simpleStorageService = new RestS3Service(new ProviderCredentialsImpl(accesskeyid, secretkey));
			return simpleStorageService.isObjectInBucket(bucketName, key);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			ExceptionUtil.throwSystemException(throwable.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);
			return false;
		}
	}
	
	/**
	 * Retrieves bucked meta data based on bucket name.
	 * @author Timo Saarinen
	 */
	public Map<String, String> readMetadata(String bucketName, String key) {
		Map<String, Object> metadataMap = null;
		try {
			simpleStorageService = new RestS3Service(new ProviderCredentialsImpl(accesskeyid, secretkey));
			S3Object s3o = simpleStorageService.getObject(bucketName, key);
			if (s3o != null) metadataMap = s3o.getMetadataMap();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			ExceptionUtil.throwSystemException(throwable.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);
		}
		
		// Convert Map<String, Object> to Map<String, String>.
		if (metadataMap != null) {
			Map<String, String> md = new HashMap<String, String>();
			for (String metaKey : metadataMap.keySet()) {
				Object metaObject = metadataMap.get(metaKey);
				if (metaObject != null) {
					md.put(metaKey, metaObject.toString());
				}
			}
			return md;
		}
		return null;
	}
	

	/**
	 * Delete object
	 */
	public void deleteObject(String bucketName, String key) {
		
		try {
			simpleStorageService = new RestS3Service(new ProviderCredentialsImpl(accesskeyid, secretkey));		
			simpleStorageService.deleteObject(bucketName, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtil.throwSystemException(e.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);			
		}
	}
	
	/**
	 * Delete all objects and bucket
	 */	
	@Log
	@AuditTrail		
	public void deleteBucketAndObjects(String bucketName) {
		try {
			LOGGER.debug("deleteBucketAndObjects called for bucket: <"+bucketName+">. Listing objects in bucket");
			
			// list and delete objects in a bucket 
			simpleStorageService = new RestS3Service(new ProviderCredentialsImpl(accesskeyid, secretkey));		
			
			// current implementation of jets3t has bug in checkBucketStatus method, cannot be used
			//int bucketStatus = simpleStorageService.checkBucketStatus(bucketName);
			//if (bucketStatus==StorageService.BUCKET_STATUS__DOES_NOT_EXIST) {
			//	LOGGER.warn("Trying to delete nonExisting bucket: "+bucketName);
			//	return;
			//}
			
			S3Object[] objects = simpleStorageService.listObjects(bucketName);
			
			
			for (S3Object s3Object : objects) {
				LOGGER.debug("Trying to delete object <"+s3Object.getKey()+"> from bucket <: "+bucketName+">.");				
				simpleStorageService.deleteObject(bucketName, s3Object.getKey());
			}
				
			// Now that the bucket is empty, you can delete it. If you try to delete your bucket before it is empty, it will fail.
			LOGGER.warn("Trying to delete bucket <: "+bucketName+">.");
			simpleStorageService.deleteBucket(bucketName);
			
		} catch (ServiceException se) {
			// if first deployment cleans all objects and bucket others will be in ERRONOUS	unless response code checked.
			// checkBucketState in jets3t is buggy, must use response code
			if(se.getResponseCode()==404) {
				LOGGER.info("Trying to delete nonExisting bucket: <"+bucketName+">. Just returning.");
				return;					
			} else {
				LOGGER.warn("Error in deleting objects and bucket. ErrorCode: "+se.getErrorCode());				
				LOGGER.warn("Error in deleting objects and bucket: "+se+" -- "+ExceptionUtil.getStackTraceString(se));				
				ExceptionUtil.throwSystemException(se.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LOGGER.warn("Error in deleting objects and bucket: "+e+" -- "+ExceptionUtil.getStackTraceString(e));				
			ExceptionUtil.throwSystemException(e.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);						
		}			
	}
	

	/**
	 * Delete all objects and bucket
	 */	
	public void deleteBucketAndObjectsVersioned(String bucketName, List <String> keyList) {
		try {
			// if versioning is used need to check how to delete all versions
			// Check bucket versioning status for the bucket
			//S3BucketVersioningStatus versioningStatus = simpleStorageService.getBucketVersioningStatus(vBucketName);			
			//simpleStorageService.suspendBucketVersioning(bucketName);			
			
			// Delete all the objects in the bucket	
			for (String key : keyList) {
				simpleStorageService.deleteObject(bucketName,key);			
			}
	
			// Now that the bucket is empty, you can delete it. If you try to delete your bucket before it is empty, it will fail.
			simpleStorageService.deleteBucket(bucketName);
			//System.out.println("Deleted bucket " + testBucket.getName());		
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ExceptionUtil.throwSystemException(e.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);						
		}			
	}
	
	
	
	/**
	 * Update object
	 */
	public void updateObject(String bucketName, String key, String version) {
		try {
			// Enable versioning for a bucket.
			//simpleStorageService.enableBucketVersioning(vBucketName);
			//S3Object versionedObject = new S3Object(key, version);
			//simpleStorageService.putObject(bucketName, versionedObject);
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ExceptionUtil.throwSystemException(e.getMessage(), ExceptionLevel.ERROR, BucketRepository.EXCEPTION_MESSAGE_CONNECTION_FAILURE);						
		}
	}
	
	
}