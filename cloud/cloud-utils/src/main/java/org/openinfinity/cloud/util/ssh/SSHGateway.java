/*
 * Copyright (c) 2013 the original author or authors.
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
package org.openinfinity.cloud.util.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.openinfinity.core.exception.SystemException;
import org.openinfinity.core.util.ExceptionUtil;
import org.openinfinity.core.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Utility for accessing SSH server.
 * 
 * @author Ilkka Leinonen
 * @version 1.1.0
 * @since 1.2.0
 */
public class SSHGateway {
		
	private static final Logger LOGGER = LoggerFactory.getLogger(SSHGateway.class);
	
	/**
	 * Pushes file to SSH server.
	 * 
	 * @param inputStream Represents the inputstream of the media.
	 * @param fileName Represents the name of the media.
	 * @param host Represents the 
	 * @param port
	 * @param username
	 * @param password
	 */
	public static void pushToServer(byte[] privateKey, byte[] publicKey, InputStream inputStream, String fileName, String host, int port, String username, String password, String deploymentDirectory) {
		Session session = null;
		Channel channel = null;
		try {
			JSch jsch = new JSch();
			if (privateKey != null) {
				jsch.addIdentity(username, privateKey, publicKey, password.getBytes());
			}
			
			session = jsch.getSession(username, host, port);
			LOGGER.trace("Starting session with username [" + username + "] to host ["+host+"] at port ["+port+"]");
			if (password != null && password.length() > 0)
				session.setPassword(password);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			LOGGER.trace("Starting session.");
			session.connect();
			LOGGER.trace("Connected to session.");
			
			channel = session.openChannel("sftp");
			LOGGER.trace("Opening channel.");
			
			channel.connect();
			LOGGER.trace("Connected to channel.");
			
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			LOGGER.trace("Pushing to " + deploymentDirectory);
			sftpChannel.put(inputStream, deploymentDirectory);
	        sftpChannel.exit();
		} catch (Throwable throwable) {
			ExceptionUtil.throwSystemException(throwable);
		} finally {
			IOUtil.closeStream(inputStream);
			if (channel != null)
				channel.disconnect();
			if (session != null)
				session.disconnect();
		}
	}

	public static void executeRemoteCommands(byte[] privateKey, byte[] publicKey, String host, int port, String username, String password, Collection<String> commands) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Session session = null;
		Channel channel = null;
		try {
			JSch jsch = new JSch();
			if (privateKey != null) {
				jsch.addIdentity(username, privateKey, publicKey, password != null ? password.getBytes() : null);
			}
			session = jsch.getSession(username, host, port);
			if (password != null && password.length() > 0)
				session.setPassword(password);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			LOGGER.trace("Starting session.");
			session.connect();
			LOGGER.trace("Connected to session.");
			channel = session.openChannel("exec");
			LOGGER.trace("Starting executing commands to session.");
			for (String command : commands) {
				LOGGER.trace("Executing command: " + command);
				((ChannelExec)channel).setCommand(command);
				outputStream = channel.getOutputStream();
				inputStream = channel.getInputStream();
				channel.connect();
				IOUtil.copyStream(inputStream, outputStream); // what should this do??
				outputStream.flush();
			}
			LOGGER.trace("Execution of commands has been finalized.");
		} catch (Throwable throwable) {
			ExceptionUtil.throwSystemException(throwable);
		} finally {
			IOUtil.closeStream(inputStream);
			IOUtil.closeStream(outputStream);
			if (channel != null)
				channel.disconnect();
			if (session != null)
				session.disconnect();
		}
	}

	/**
	 * Execute a command in remote host and stream its output to local file system.
	 * The SSH key can be read with KeyService and then converted to an expected form using
	 * <code>Key.getSecret_key().getBytes()</code>.
	 * 
	 * @param privateKey 	SSH key to be used.
	 * @param publicKey		Can be null.
	 * @param host     		Remote host ip address or hostname  
	 * @param port			Remote SSH port
	 * @param username		Remote host username
	 * @param password		Remote host password (can be null)
	 * @param command		Single shell command to be execute in the remote host
	 * @param filename		Filename in local file system
	 * @return Exit status of the process or -1 if exit status is not available.
	 * @author Timo Saarinen
	 */
	public static int executeRemoteCommandAndStreamOutputToFile(byte[] privateKey, byte[] publicKey, String host, int port, String username, String password, String command, String filename) {
		int exit_status = -1;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Session session = null;
		Channel channel = null;
		try {
			// JSCH initialization
			JSch jsch = new JSch();
			if (privateKey != null) {
				jsch.addIdentity(username, privateKey, publicKey, password != null ? password.getBytes() : null);
			}
			session = jsch.getSession(username, host, port);
			if (password != null && password.length() > 0)
				session.setPassword(password);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			LOGGER.trace("Starting session.");
			session.connect();
			LOGGER.trace("Connected to session.");
			channel = session.openChannel("exec");
			LOGGER.trace("Starting executing commands to session.");

			// Create the output file
			FileOutputStream fos = null;
			if (filename != null) {
				fos = new FileOutputStream(filename);
			}
			
			// Run the command
			LOGGER.trace("Executing command: " + command);
			((ChannelExec)channel).setCommand(command);
			outputStream = channel.getOutputStream();
			inputStream = channel.getInputStream();
			channel.connect();
			if (fos != null) {
				IOUtil.copyStream(inputStream, fos);
			}
			
			// Close local file
			if (fos != null) {
				fos.flush();
				fos.close();
			}
			
			// Exit code
			exit_status = channel.getExitStatus();
			LOGGER.trace("Execution of commands was finished with exit status " + exit_status + ".");
		} catch (Throwable throwable) {
			ExceptionUtil.throwSystemException(throwable);
		} finally {
			// Cleanup
			IOUtil.closeStream(inputStream);
			IOUtil.closeStream(outputStream);
			if (channel != null)
				channel.disconnect();
			if (session != null)
				session.disconnect();
		}
		return exit_status;
	}
	
	public static int checkAck(InputStream inputStream) throws SystemException {
		try {
			int bit = inputStream.read();
		    if (bit == 0 || bit == -1) return bit;
		    if (bit == 1 || bit == 2) {
				StringBuffer sb = new StringBuffer();
				int c;
				do {
					c = inputStream.read();
					sb.append((char)c);
				} while (c != '\n');
				if (bit == 1 || bit == 2) { 
					ExceptionUtil.throwSystemException("Error occurred while handling SSH transfer: " + sb.toString());
				}
			}
		    return bit;
		} catch(Throwable throwable) {
			throw new SystemException(throwable);
		}
    }
	
}
