/* * Copyright (c) 2013 the original author or authors. * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package org.openinfinity.cloud.util.filesystem;import java.io.ByteArrayOutputStream;import java.io.File;import java.io.FileInputStream;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.OutputStream;import java.util.ArrayList;import java.util.Arrays;import java.util.Collection;import java.util.Collections;import java.util.Comparator;import java.util.List;import org.openinfinity.core.exception.SystemException;import org.openinfinity.core.util.ExceptionUtil;import org.openinfinity.core.util.IOUtil;import org.slf4j.Logger;import org.slf4j.LoggerFactory;/** * File utility to handle directory structure. *  * @author Ilkka Leinonen * @version 1.0.0 * @since 1.2.0 */public class FileUtil {		private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);		private static final String 	EXCEPTION_CAUSE_FILE_DOES_NOT_EXIST = "File does not exist",	EXCEPTION_CAUSE_IO_ERROR = "IO Error occurred";		private static final int 	STREAM_ENDING_FLAG = -1;		public static Collection<File> load(String filePath, boolean isRecursive) {		Collection<File> fileCollection = Collections.checkedCollection(new ArrayList<File>(), File.class);		File file = new File(filePath);		checkAvailability(EXCEPTION_CAUSE_FILE_DOES_NOT_EXIST, file);		doRecursionOnFileSystem(isRecursive, fileCollection, file);		return Collections.unmodifiableCollection(fileCollection);	}		public static File loadByFileName(String filePath) {		File file = new File(filePath);		checkAvailability(EXCEPTION_CAUSE_FILE_DOES_NOT_EXIST, file);		return file;	}		public static InputStream loadInputStreamByFileName(String filePath) {		InputStream inputStream = null;		try {			File file = new File(filePath);			checkAvailability(EXCEPTION_CAUSE_FILE_DOES_NOT_EXIST, file);			inputStream = new FileInputStream(file);		} catch (FileNotFoundException fileNotFoundException) {			ExceptionUtil.throwSystemException(EXCEPTION_CAUSE_IO_ERROR + ": " + fileNotFoundException.toString(), fileNotFoundException);				}		return inputStream;	}		public static byte[] loadBytesByFileName(String filePath) {		InputStream inputStream = null;		ByteArrayOutputStream byteArrayOutputStream = null;		try {			int data;			inputStream = loadInputStreamByFileName(filePath);			byteArrayOutputStream = new ByteArrayOutputStream();			while ((data=inputStream.read()) != STREAM_ENDING_FLAG) {				byteArrayOutputStream.write(data);			}			return byteArrayOutputStream.toByteArray();		} catch (Throwable throwable) {			throw new SystemException(EXCEPTION_CAUSE_IO_ERROR, throwable);		} finally {			IOUtil.closeStream(byteArrayOutputStream);			IOUtil.closeStream(inputStream);		}			}	public static void store(String filePath, String content) {		OutputStream outputStream = null;		try {				int index = filePath.lastIndexOf(File.separator);			String directoryPath = filePath.substring(0, index + 1);			File directory = new File(directoryPath);			if (!directory.exists()) {				boolean success = directory.mkdir();				if(!success)					throw new RuntimeException("Error creating directory: " + directory, new IOException("File allready exists:"+filePath));			} 			directory = null;			File file = new File(filePath);			if (file.exists()){				file.delete();			}			outputStream = new FileOutputStream(file);			outputStream.write(content.getBytes());			outputStream.flush();		} catch (Throwable throwable) {			LOGGER.debug("Exception: " + throwable.getMessage() + " : " + filePath);			ExceptionUtil.throwSystemException(EXCEPTION_CAUSE_FILE_DOES_NOT_EXIST, throwable);		} finally {			IOUtil.closeStream(outputStream);		}	}		public static void remove(String filePath) {		File file = new File(filePath);		checkAvailability(EXCEPTION_CAUSE_FILE_DOES_NOT_EXIST, file);		boolean isDeleted = file.delete();		if (!isDeleted) {			ExceptionUtil.throwSystemException(EXCEPTION_CAUSE_IO_ERROR + " could not remove file :" + file.getAbsolutePath(), new IOException("Could not remove file."));		}	}	public static void checkAvailability(String cause, File file) {		if (!file.exists()){			ExceptionUtil.throwSystemException(cause + file.getAbsolutePath(), new IOException("Absolute path:"+file.getAbsolutePath()));		}	}		private static void doRecursionOnFileSystem(boolean isRecursive, Collection<File> fileCollection, File file) {		if (file.isDirectory()) {			if (isRecursive) {				findFilesRecursively(file, fileCollection);			}		} else {			fileCollection.add(file);		}	}		public static void findFilesRecursively(File file, Collection<File> fileCollection) {		if (file.isFile()) {			fileCollection.add(file);		} else {			File[] files = file.listFiles();			for (File f : files) {				findFilesRecursively(f, fileCollection);			}		}	}	public static void removeAllRecursively(String filePath) {		File dir = new File(filePath);		if (dir.isDirectory()) {			Collection<File> files = load(filePath, Boolean.TRUE);			for(File file : files) {				remove(file.getAbsolutePath());			}		}		remove(dir.getAbsolutePath());	}		public static List<File> sortByLastModifiedTimestamp(List<File> files) {		File[] temp = new File[]{};		File[] fileArrays = files.toArray(temp);		Arrays.sort(fileArrays, new Comparator<File>() {		    public int compare(File f1, File f2) {		        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());		    } 		});		return Arrays.asList(fileArrays);	}}