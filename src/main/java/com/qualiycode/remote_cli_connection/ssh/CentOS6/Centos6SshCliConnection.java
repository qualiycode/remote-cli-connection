package com.qualiycode.remote_cli_connection.ssh.CentOS6;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;

import com.qualiycode.remote_cli_connection.ssh.ShellType;
import com.qualiycode.remote_cli_connection.ssh.SshCliConnection;

/**
 * This class extends the regular SSH CLI by adding the option to ignore terminal colors when using terminal other than "dumb"
 * 
 * @author Eli Rozenfeld
 *
 */
public class Centos6SshCliConnection extends SshCliConnection {

	/**
	 * The following variables are used then ignoring terminal colors:
	 * - Once we see the TERMINAL_COLORS_START_INDICATOR we set the skipChars to true
	 * - When skipChars is true we do not add characters to the line we read
	 * - If skipChars is true and we see TERMINAL_COLORS_END_INDICATOR we set the skipChars to false
	 * 
	 * The colored output looks like this: \\ubbbKYOUR_TEXT\\ubbbK so we remove the \\ubbbK from both sides
	 */
	protected boolean skipChars = false;
	protected int terminalColorsStartIndicator = 27;
	protected List<Integer> terminalColorsEndIndicators = Arrays.asList(new Integer[]{74, 75, 109});

	/**
	 * if true we remove the ANSI colors indicators from the terminal output 
	 */
	protected boolean ignoreTerminalColors = true;

	
	public Centos6SshCliConnection(String username, String password, String ip, String endLineStr) {
		super(username, password, ip, endLineStr);
	}

	public Centos6SshCliConnection(String username, String password, String ip, int port, ShellType shellType, String endLineStr) {
		super(username, password, ip, port, shellType, endLineStr);
	}
	
	@Override
	protected void readOutputAndAddLine(BufferedReader dataIn, StringBuilder line) throws Exception{
		if(ignoreTerminalColors){
			int character = dataIn.read();
			
			if(character == terminalColorsStartIndicator){
				skipChars = true;
				return;
			}
			
			if(skipChars && terminalColorsEndIndicators.contains(character)){
				skipChars = false;
				return;
			}
			
			if(!skipChars){
				line.append((char)character);
			}
		}else{
			super.readOutputAndAddLine(dataIn, line);
		}
	}	

}
