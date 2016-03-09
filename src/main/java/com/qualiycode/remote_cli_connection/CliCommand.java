package com.qualiycode.remote_cli_connection;

/**
 * This object contains a CLI command to execute on remote connection with specific properties
 * 
 * @author Eli Rozenfeld
 *
 */
public class CliCommand {

	/**
	 * The timeout (in seconds) to wait for command execution until throwing error and declaring the command as failed. 
	 */
	protected int timeout = 30;
	
	/**
	 * if set to true and command failes it will not report it and command will be considered OK  
	 */
	protected boolean ignoreErrors = false;
	
	/**
	 * if set to true the command and it's output will not be shown in the terminal 
	 */
	protected boolean silent = false;
	
	/**
	 * The actual command string to execute on the remote connection 
	 */
	protected String command = null;
	
	/**
	 * Holds the command output 
	 */
	protected String commandOutput = null;
	
	/**
	 * @param command - the command string to execute on the remote connection
	 */
	public CliCommand(String command){
		this.command = command;
	}

	/**
	 * @return the command timeout (In seconds)
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout - the command timeout (In Seconds)
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return true if errors are ignored
	 */
	public boolean isIgnoreErrors() {
		return ignoreErrors;
	}

	/**
	 * @param ignoreErrors - set to true if you wish to ignore errors
	 */
	public void setIgnoreErrors(boolean ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}

	/**
	 * @return true if the command and output will not be shown in the terminal
	 */
	public boolean isSilent() {
		return silent;
	}

	/**
	 * @param silent - set to true for hiding the command and output from the terminal
	 */
	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * @return the command output
	 */
	public String getCommandOutput() {
		return commandOutput;
	}

	/**
	 * @param commandOutput - the command output
	 */
	public void setCommandOutput(String commandOutput) {
		this.commandOutput = commandOutput;
	}

}
