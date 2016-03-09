package com.qualiycode.remote_cli_connection.ssh;

/**
 * This enum holds the list of supported shells by JSCH
 * 
 * @author Eli Rozenfeld
 *
 */
public enum ShellType {

	SESSION ("session"),
	SHELL ("shell"),
	EXEC ("exec"),
	X11 ("x11"),
	AUTH_AGENT ("auth-agent@openssh.com"),
	DIRECT ("direct-tcpip"),
	FORWARDED ("forwarded-tcpip"),
	SFTP ("sftp"),
	SUBSYSTEM ("subsystem");
	
	private final String name;
	private ShellType(String s) {
	        name = s;
	}
	
	@Override
	public String toString(){
	       return name;
	}
}
