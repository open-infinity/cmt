package org.openinfinity.web.support;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SerializerUtil.class);
	
	public static Writer getWriter(HttpServletResponse response) {
		Writer writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return writer;
	}

}
