package com.qualiycode.remote_cli_connection.ssh;

/**
 * Holds the terminal types when using the SSH CLI connection
 * 
 * @author Eli Rozenfeld
 *
 */
public enum SshTerminalType {

	/**
	 * - Allows colors (using non-printable chars) 
	 * - Output behaviour when output reaches the terminal max chars per line =  seperate the txt using space+\r 
	 */
	VT100("vt100"),
	
	/**
	 * - Allows colors (using non-printable chars) 
	 * - Output behaviour when output reaches the terminal max chars per line =  seperate the txt using space+\r 
	 */
	VT220("vt220"),
	
	/**
	 * - Ignore colors 
	 * - Output behaviour when output reaches the terminal max chars per line =  cut the txt and doe not return the rest
	 */
	VT320("vt320"),
	
	/**
	 * - Ignore colors 
	 * - Output behaviour when output reaches the terminal max chars per line =  cut the txt and doe not return the rest
	 */
	DUMB("dumb");
	
	private final String name;
	
	private SshTerminalType(String s) {
	        name = s;
	}
	
	@Override
	public String toString(){
	       return name;
	}
}
