package com.qualiycode.remote_cli_connection;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.qualiycode.remote_cli_connection.ssh.SshCliConnection;

/**
 * This is a unit test class that test the SSH connection
 * 
 * This class is connecting to sdf.org using SSH and test that the connection is successful
 * 
 * @author Eli Rozenfeld
 *
 */
@RunWith(JUnit4.class)
public class SshTest {

	
	@Test
	public void connectionTest(){
		Logger logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		logger.setLevel(Level.ALL);
		logger.addAppender(new ConsoleAppender(new PatternLayout("%d{ISO8601} - %m%n")));

		//Creating a remote SSH CLI connection
		String user = "new";
		String password = "";
		String remoteDeviceIp = "sdf.org";
		String remoteDevicePromptSign = "[RETURN] ";
		SshCliConnection cli = new SshCliConnection(user, password, remoteDeviceIp, remoteDevicePromptSign);

		try {
			//Connect to the remote device
			cli.connect();
			//Check that connection is OK
			Assert.assertTrue(cli.isConnected());
			//Disconnect session
			cli.disconnect();
			//Check that connection disconnected
			Assert.assertTrue(!cli.isConnected());
		} catch (Exception e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}

		
	}
}
