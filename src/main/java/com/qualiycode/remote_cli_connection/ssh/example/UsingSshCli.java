package com.qualiycode.remote_cli_connection.ssh.example;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.qualiycode.remote_cli_connection.CliCommand;
import com.qualiycode.remote_cli_connection.ssh.SshCliConnection;

/**
 * This class demonstrate the use of the SSH CLI connection
 * 
 * You can open an account in many free shell servers on line (http://shells.red-pill.eu/) to test it
 * 
 * @author Eli Rozenfeld
 *
 */
public class UsingSshCli {

	public static void main(String[] args) throws Exception{
		//Setting Log4J to output the logs to the console
		Logger logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		logger.setLevel(Level.ALL);
		logger.addAppender(new ConsoleAppender(new PatternLayout("%d{ISO8601} - %m%n")));
		
		//Creating a remote SSH CLI connection
		String user = "user";
		String password = "password";
		String remoteDeviceIp = "1.1.1.1";
		String remoteDevicePromptSign = "]# "; //Setting the remote device prompt sign (will help us identify prompt wait)
		SshCliConnection cli = new SshCliConnection(user, password, remoteDeviceIp, remoteDevicePromptSign);
		
		//Connect to the remote device
		cli.connect();

		//Creating a command object with desiered parameters
		CliCommand command = new CliCommand("ll");
		
		//Execute the command on the remote device
		String output = cli.handleCliCommand(command);

		//Disconnect the connection to the remote device
		cli.disconnect();

		//printing the output to the console
		System.out.println("==================================================\nOutput is:\n" + output + "\n==================================================");
	}
}
