package org.openinfinity.cloud.application.backup;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.openinfinity.cloud.application.backup.job.StorageCommand;
import org.openinfinity.cloud.application.backup.job.Tools;

/**
 * This is a really simple test of ByteBuffer and actually not related to the backup directly. 
 * 
 * @author Timo Saarinen
 */
public class ByteBufferTest {
	private Logger logger = Logger.getLogger(ByteBufferTest.class);
	
	@Test
	public void testByteBuffer() {
/*		
		String src = "abcdefghijklmnopqrstuvwxyzåäö";

		Charset charset = Charset.forName("UTF-8");
		CharsetEncoder encoder = charset.newEncoder();
		CharsetDecoder decoder = charset.newDecoder();
		ByteBuffer bbi = encoder.encode(CharBuffer.wrap(src));
*/		
		
		ByteBuffer bbi = ByteBuffer.allocate(4);
		ByteBuffer bbo = ByteBuffer.allocate(4);
		
		logger.debug(bbo);
		bbi.put((byte)'a'); 
		bbi.put((byte)'b');
		bbi.put((byte)'c');
		bbi.put((byte)'d');
		logger.debug(bbo);
		bbi.flip();
		Tools.copyByteBuffer(bbi, bbo);
		bbo.flip();
		logger.debug(bbo);
		Assert.assertTrue('a' == (char)bbo.get());
		Assert.assertTrue('b' == (char)bbo.get());
		Assert.assertTrue('c' == (char)bbo.get());
		Assert.assertTrue('d' == (char)bbo.get());
		logger.debug(bbo);
	}
}
