package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Tools {
	/**
	 * Execute command in local host. Quote marks are not permitted as part of
	 * the command.
	 * 
	 * @return exit value of the command
	 */
	public static int runLocalCommand(String cmd, Logger logger, Level stderr_logging_level) throws IOException {
		logger.debug("Running local command: " + cmd);
		try {
			// Run and wait for completion of the command
			Process p = Runtime.getRuntime().exec(cmd);
			int ret = p.waitFor();
			if (ret != 0) {
				StringWriter writer = new StringWriter();
				IOUtils.copy(p.getErrorStream(), writer);
				logger.log(stderr_logging_level, "Command ended with error: " + writer.toString());
			}
			return ret;
		} catch (InterruptedException exception) {
			logger.warn("Local command waiting was interrupted unexpectedly");
		}
		return -1;
	}

	/**
	 * Execute command in local host. Quote marks are not permitted as part of
	 * the command.
	 * 
	 * @return exit value of the command
	 */
	public static int runLocalCommand(String cmd, Logger logger) throws IOException {
		return runLocalCommand(cmd, logger, Level.WARN);
	}

	/**
	 * Replaces the current extension of the file with a new one.
	 * 
	 * @param f   File
	 * @param ext New Extension without a dot. If the extension is null, 
	 *            the old extension will be just removed.
	 * @return File name with the new extension
	 */
	public static File replaceExtension(File f, String ext) {
		String fname = f.toString();
		if (ext != null) {
			return new File(fname.substring(0, fname.lastIndexOf('.')), "." + ext);
		} else {
			return new File(fname.substring(0, fname.lastIndexOf('.')));
		}
	}

	/**
	 * Returns length of the given file.
	 * @param f Filename
	 * @return size in bytes
	 * @throws IOException 
	 */
	public static long readFileSize(File f) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			return fis.getChannel().size();
		} finally {
			if (fis != null)
				fis.close();
		}
	}
	
	/**
	 * Copy all data from input stream to output stream.
	 * @param is
	 * @param os
	 * @throws IOException 
	 */
	public static void copyStreams(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[8192];
		int len;
		while ((len = is.read(buffer)) != -1) {
		    os.write(buffer, 0, len);
		}
	}
	
	/**
	 * Copies content from ByteBuffer to another. The implementation is not efficient,
	 * but meant for debugging only.  
	 */
	public static void copyByteBuffer(ByteBuffer b1, ByteBuffer b2) {
		for (int i = b1.position(); i < b1.limit(); i++) {
			b2.put(b1.get());
		}
	}
}
