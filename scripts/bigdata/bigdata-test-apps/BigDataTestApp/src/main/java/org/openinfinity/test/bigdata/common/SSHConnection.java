package org.openinfinity.test.bigdata.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * A convenience class to handle SSH connections.
 * 
 * @author Timo Saarinen
 * 
 */
public class SSHConnection {
	private Session session;

	public SSHConnection(String username, String hostname,
			String privateKeyFilename) throws JSchException {
		// Prepare HDFS directories and files for the Map/Reduce
		JSch jsch = new JSch();
		jsch.addIdentity(privateKeyFilename);
		session = jsch.getSession(username, hostname, 22);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		// session.setPassword(password);
		session.connect();
	}

	/**
	 * Execute command in the remote host
	 * 
	 * @param cmd
	 *            Unix shell command to be executed
	 * @return return value
	 * @throws JSchException
	 * @throws IOException
	 */
	public int execute(String cmd) throws JSchException, IOException {
		ChannelExec channel = (ChannelExec) session.openChannel("exec");

		((ChannelExec) channel).setCommand(cmd);

		channel.setInputStream(null);
		((ChannelExec) channel).setOutputStream(System.out);
		((ChannelExec) channel).setErrStream(System.err);

		InputStream in = channel.getInputStream();

		channel.connect();

		int exit_status = -2;

		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				System.out.print(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				exit_status = channel.getExitStatus();
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee) {
			}
		}

		if (!session.isConnected())
			throw new JSchException("SSH session not connected");

		if (exit_status < 0)
			throw new JSchException(
					"SSH command not executed and exit status is not available ("
							+ exit_status + ")");

		channel.disconnect();

		return exit_status;
	}

	/**
	 * Disconnect the SSH connection.
	 */
	public void disconnect() {
		session.disconnect();
		session = null;
	}
}
