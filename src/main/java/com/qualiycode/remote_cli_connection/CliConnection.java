package com.qualiycode.remote_cli_connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a base class for CLI connections implementation classes.
 * 
 * It holds the logic of reading the terminal using a prompt mechanism to identify specific prompts and react accordingly
 * 
 * Note:
 * When extending this class you must set the dataIn & dataOut variables to allow reading and writing to your connection
 * 
 * Link to ASCII table codes: http://www.bluesock.org/~willg/dev/ascii.html
 *  
 * @author Eli Rozenfeld
 *
 */
public abstract class CliConnection {
	
	protected final static Logger log = LoggerFactory.getLogger(CliConnection.class);
	
	/**
	 * CRLF identifier of (linux), used to mark the "enter" when commands are transmitted to the remote machine 
	 */
	protected final String LINUX_CRLF = "\n";
	
	/**
	 * connection user name 
	 */
	protected String username = null;
	
	/**
	 * connection password 
	 */
	protected String password = null;
	
	/**
	 * connection IP 
	 */
	protected String ip = null;
	
	/**
	 * connection port 
	 */
	protected int port = -1;
	
	/**
	 * Holds the list of prompts that will be used when handling this connection 
	 */
	protected ArrayList<Prompt> prompts = null;

	/**
	 * holds the data-out stream, used for sending the data to the remote connection  
	 */
	protected DataOutputStream dataOut = null;
	
	/**
	 * holds the data-in stream, used for reading the data from the remote connection  
	 */
	protected BufferedReader dataIn = null;

	/**
	 * If true - we keep reading from remote terminal
	 */
	protected boolean keepReadingOutput;
	
	/**
	 * Hold the number of retries we do when trying to connect 
	 */
	protected int numberOfRetries = 3;
	
	/**
	 * @param username - the remote connection user name
	 * @param password - the remote connection password
	 * @param ip - the remote connection IP address
	 * @param port - the remote connection port
	 * @param endLineStr - the identifier to use for identifing the end of line prompt
	 */
	public CliConnection(String username, String password, String ip, int port, String endLineStr){
		this.username = username;
		this.password = password;
		this.ip = ip;
		this.port = port;

		prompts = new ArrayList<>();
		Prompt myPrompt = new Prompt();
		myPrompt.setPrompt(endLineStr, false);
		myPrompt.setMarkEndOfOutput(true);
		prompts.add(myPrompt);
	}

	/**
	 * This function adds prompts to be used when handling this connection
	 * @param promptToAdd - the prompt to add
	 * @throws Exception
	 */
	public void addPrompt(Prompt promptToAdd) throws Exception{
		if(promptToAdd == null || promptToAdd.getPrompt() == null || promptToAdd.getPrompt().length() == 0){
			throw new Exception("Error while trying to add prompt, Prompt is empty");
		}
		prompts.add(promptToAdd);
	}
	
	/**
	 * This method opens a connection to a host
	 * @return true if connection successful, false otherwise
	 */
	public boolean connect() throws Exception{
		
		int retry = 1;
		while(retry <= numberOfRetries && !isConnected()){
			try {
				log.info("Connecting to: " + ip + ":" + port + ", Try #" + retry + ", User: " + username + ", Pass: ******");
				if(doConnect() == true){
					if(dataIn == null || dataOut == null){
						throw new Exception("You must set dataIn & dataOut objects to allow reading and writing to your connection");
					}
					waitForTerminal();
				}else{
					log.error("Unable to connect to: " + ip + ":" + port + ", User: " + username + ", Pass: ******");
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			retry ++;
		}
		
		if(isConnected()){
			log.info("Connestion successfull");
		}else{
			log.error("Unable to connect to: " + ip + ", User = " + username + ", Pass = " + password);
		}
		
		return isConnected();
	}

	/**
	 * This method contains the extender class CLI connection logic
	 * @return true if connection successful, false otherwise
	 */
	protected abstract boolean doConnect();
	
	/**
	 * This method disconnect the connection from the host
	 * @return true if connection closed successfully, false otherwise
	 */
	public boolean disconnect(){
		boolean result = true;
		try {
			if(dataIn != null){
				dataIn.close();  
			}
			if(dataOut != null){
				dataOut.close();
			}
			result = doDisconnect() && result;
			if(result){
				log.info("Disconnected successfuly");
			}else{
				log.error("Error while disconnecting session");
			}
		} catch (Exception e) {
			result = false;
			log.error("Error while disconnecting session",e);
		}

		return result;
	}

	
	/**
	 * This method contains the extender class CLI disconnection logic
	 * @return true if connection successful, false otherwise
	 */
	public abstract boolean doDisconnect();

	/**
	 * This method run command on a connected connection
	 * @param command - the command to run
	 * @return the command output
	 */
	public abstract String handleCliCommand(String command) throws Exception;
	
	/**
	 * This method run command on a connected connection
	 * @param command - the command to run
	 * @return the command output
	 */
	public abstract String handleCliCommand(CliCommand command) throws Exception;

	/**
	 * @return true if the remote connection is connected and alive
	 * @throws Exception
	 */
	public abstract boolean isConnected() throws Exception;
	
	private String waitForTerminal() throws Exception{
		return waitForTerminal(new CliCommand("ConnectTerminalSession"));
	}
	
	/**
	 * This function read characters from the terminal and return the output once the reading is over.
	 * Note:
	 * This function also send responses to the terminal according to the prompts 
	 * 
	 * @return the output from the CLI
	 * @throws Exception
	 */
	protected String waitForTerminal(CliCommand command) throws Exception{
		long startTime = System.currentTimeMillis();
		long elapsTime;
		
		StringBuilder lines = new StringBuilder();
		StringBuilder line = new StringBuilder();
		try {
    	   //read all output after executing command
    	   keepReadingOutput = true;
    	   
    	   elapsTime = System.currentTimeMillis()-startTime;
    	   if(isTerminalReadyForReading(command, elapsTime)){
    		   readOutputAndAddLine(dataIn, line);
    	   }else{
    		   throw new Exception("Unable to read command output, no prompt return");
    	   }
    	   
//    	   while(line.length() > 0 && keepReadingOutput) {
       	   while(keepReadingOutput) {
    		   boolean found = false;
    		   for(int i=0; i<prompts.size() && !found; i++){
    			   Prompt prompt = prompts.get(i);
    			   found = isPromptFound(prompt, line);
    			   if(found){
    				   if(prompt.isMarkEndOfOutput()){
        	    		   lines.append(line);
        	    		   if(!command.isSilent()){
        	    			   log.info("[terminal] " + line);
        	    		   }
    					   keepReadingOutput = false;
    					   continue; //once we know the output is ended we stop processing prompts (and because we set keepReadingOutput to false we will also stop reading outputs)
    				   }
    				   if(prompt.isSendResponseString()){
    			     	   dataOut.writeBytes(prompt.getResponseString());  
    			     	   dataOut.flush();
    				   }
    				   if(prompt.isAddEnter()){
    			     	   dataOut.writeBytes(LINUX_CRLF);  
    			     	   dataOut.flush();
    				   }
    			   }else{
    				   if(line.toString().endsWith(LINUX_CRLF)){
        	    		   lines.append(line);
        	    		   if(!command.isSilent()){
        	    			   log.info("[terminal] " + line);
        	    		   }
        	    		   line.setLength(0);
        	    		   break;
    				   }
    			   }
    		   }
    		   elapsTime = System.currentTimeMillis()-startTime; 
    		   if(elapsTime > (command.getTimeout()*1000)){
    			   throw new Exception("Got timeout (After " + command.getTimeout() + " seconds) while reading command output");
    		   }
    		   
    		   if(keepReadingOutput){
    			   if(isTerminalReadyForReading(command, elapsTime)){
    				   readOutputAndAddLine(dataIn, line);
    	    	   }else{
    	    		   throw new Exception("Got timeout (After " + command.getTimeout() + " seconds) while reading command output");
    	    	   }
    		   }
           }
    	   command.setCommandOutput(lines.toString());
    	   
    	   //This is here because we stop reading from the buffer then we find the correct prompt... 
    	   //In some cases after the prompt we could still find more characters... 
    	   //So we need to pull those out of the buffer to prevent it from showing up next time we interact with the console  
    	   while(dataIn.ready()){
    		   int i = dataIn.read();
    		   log.debug("****************** reading JUNK char " + i + "********************");
    	   }
    			   
		} catch (Exception e) {
			if(line.length() > 0){
				lines.append(line);
			}
			command.setCommandOutput(lines.toString());
			if(!command.ignoreErrors){
				String exceptionString = "Unable to excecute command \"" + command.getCommand() + "\", " + e.getMessage();
				if(command.getCommand().equals("")){
					exceptionString = "Unable to connect to: " + ip;
				}
				throw new Exception(exceptionString);
					
			}
		}  
        return lines.toString();
	}
	
	/**
	 * This function reads a char from the remove machine terminal and add it to our line container 
	 * @param dataIn - the remote terminal reader 
	 * @param line - the line container
	 * @throws Exception
	 */
	protected void readOutputAndAddLine(BufferedReader dataIn, StringBuilder line) throws Exception{
		line.append((char)dataIn.read());
	}
	
	/**
	 * used for checking if the data-in stream is ready for read
	 * @param command - the CLI command that is been used (used for extracting the command timeout) 
	 * @return true if we can read from the data-in stream
	 * @throws Exception
	 */
	protected boolean isTerminalReadyForReading(CliCommand command, long elapsTime) throws Exception{
		long startTime = System.currentTimeMillis() - elapsTime;
		while(!dataIn.ready() && startTime + (command.getTimeout()*1000) > System.currentTimeMillis()){}
		
		return dataIn.ready();
	}
	
	/**
	 * This function checks if a specific prompt is found in the desired line
	 * @param prompt - the prompt to look for
	 * @param line - the line to seek the prompt in
	 * @return true if the prompt is found in the line
	 * @throws Exception
	 */
	protected boolean isPromptFound(Prompt prompt, StringBuilder line) throws Exception{
		boolean found = false;
		if(prompt.isRegularExpression()){
			Matcher matcher = prompt.getPattern().matcher(line);
			found = matcher.find();
		}else{
			found = line.toString().endsWith(prompt.getPrompt());
		}
		
		return found;
	}

	/**
	 * @return the user name used for this connection
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * @param username - the user name to be used for this connection
	 */
	public void setUsername(String username) {
		this.username = username;
	}


	/**
	 * @return the password used for this connection
	 */
	public String getPassword() {
		return password;
	}


	/**
	 * @param password - the password to be used for this connection
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * @return the connection remote IP
	 */
	public String getIp() {
		return ip;
	}


	/**
	 * @param ip - the connection remote IP
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}


	/**
	 * @return the connection TCP port
	 */
	public int getPort() {
		return port;
	}


	/**
	 * @param port - TCP port to be used by this connection
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the number of retries we do when trying to connect
	 */
	public int getNumberOfRetries() {
		return numberOfRetries;
	}

	/**
	 * @param numberOfRetries - the number of retries we do when trying to connect
	 */
	public void setNumberOfRetries(int numberOfRetries) {
		this.numberOfRetries = numberOfRetries;
	}

}
