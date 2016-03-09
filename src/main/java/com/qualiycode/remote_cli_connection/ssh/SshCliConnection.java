package com.qualiycode.remote_cli_connection.ssh;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.qualiycode.remote_cli_connection.CliCommand;
import com.qualiycode.remote_cli_connection.CliConnection;

/**
 * This class implements a remote CLI connection using SSH
 * 
 * @author Eli Rozenfeld
 *
 */
public class SshCliConnection extends CliConnection{
	
	/**
	 * The default shell type to be used for this connection
	 */
	protected static final ShellType DEFAULT_SHELL_TYPE = ShellType.SHELL;
	
	/**
	 * Hold the shell type to be used for this connection
	 */
	private ShellType shellType;
	
	/**
	 * Holds the SSH session object 
	 */
	protected Session session;
	
	/**
	 * Holds the SSH channel object 
	 */
	protected Channel channel;
	
	/**
	 * Hold the connect timeout which is the max time we allow for connect function 
	 */
	protected int connectTimeout = 30000;
	
	/**
	 * Holds the SSH Terminal type
	 * 
	 * Note: this works only if we use Shell type = shell 
	 */
	protected SshTerminalType sshTerminalType = SshTerminalType.VT220;
	
	/**
	 * Holds the last known output of a CLI command  
	 */
	protected String lastKnownOutput = "";
	
	/**
	 * @param username - connection user name
	 * @param password - connection password
	 * @param ip - host IP
	 * @param port - connection port
	 * @param shellType = server shell type
	 * @param endLineStr = end line marker (usually #)
	 */
	public SshCliConnection(String username, String password, String ip, int port, ShellType shellType, String endLineStr){
		super(username, password, ip, port, endLineStr);
		this.shellType = shellType;
	}

	/**
	 * @param username - connection user name
	 * @param password - connection password
	 * @param ip - host IP
	 * @param endLineStr = end line marker (usually #)
	 */
	public SshCliConnection(String username, String password, String ip, String endLineStr){
		this(username, password, ip, 22, DEFAULT_SHELL_TYPE, endLineStr);
	}
	
	/**
	 * This method opens a SSH connection to a host
	 * @param connectionHandelr
	 * @return true if connection successful, false otherwise
	 */
	@Override
	protected boolean doConnect(){
		JSch shell = new JSch();
		boolean result = false;
		try {
			session = shell.getSession(username, ip, port);
			session.setUserInfo(new SshUserInfo(password));
			//we set the retry to 1 because retries are managed by the CliConnection object we extends
			session.setConfig("MaxAuthTries","1");
			session.connect(connectTimeout);  
			channel = session.openChannel(shellType.toString());  
			if(shellType.equals(ShellType.SHELL)){
				setTerminalType();
			}
			channel.connect();  
			dataIn = new BufferedReader(new InputStreamReader(channel.getInputStream()));  
			dataOut = new DataOutputStream(channel.getOutputStream());
			result = true;
		} catch (Exception e) {
			log.error("unable to open SSH connection to " + ip, e);
		}
		return result;
	}
	
	/**
	 * Sets the terminal type 
	 */
	protected void setTerminalType(){
		((ChannelShell)channel).setPtyType(sshTerminalType.toString());
	}
	
	/**
	 * This method closes the SSH connection
	 * @param connectionHandelr
	 * @return true if connection closed successfuly, false otherwise
	 */
	@Override
	public boolean doDisconnect(){
		if(channel != null){
			channel.disconnect();  
		}

		if(session != null){
			session.disconnect();
		}
		return true;
	}
	
	/**
	 * This method run command on a SSH channel
	 * @param command - the command to execute
	 * @return the command output
	 */
	@Override
	public String handleCliCommand(String command) throws Exception{
        return handleCliCommand(new CliCommand(command));
	}
	
	/**
	 * This method run command on a SSH channel
	 * @param command - the command to execute
	 * @return the command output
	 */
	@Override
	public String handleCliCommand(CliCommand command) throws Exception{
		String output = "";
		try {
			dataOut.writeBytes(command.getCommand() + LINUX_CRLF);  
			dataOut.flush();
			output = waitForTerminal(command);
		} catch (Exception e) {
			lastKnownOutput = command.getCommandOutput();
			log.error("unable to excecute command");
			throw e;
		}  
        return output;
	}

	@Override
	public boolean isConnected() throws Exception {
		return session != null && channel != null && session.isConnected() && !channel.isClosed();
	}

	/**
	 * @return the last known output a CLI command had
	 */
	public String getLastKnownOutput(){
		return lastKnownOutput;
	}

}
