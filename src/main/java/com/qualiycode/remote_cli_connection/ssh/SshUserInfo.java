package com.qualiycode.remote_cli_connection.ssh;

import com.jcraft.jsch.UserInfo;

/**
 * This class holds the user information to be used by JSCH when initiating SSH connection
 * 
 * @author Eli Rozenfeld
 *
 */
public class SshUserInfo implements UserInfo{
	
    private String password;  
    
    SshUserInfo(String password) {  
        this.password = password;  
    }  

    @Override
	public String getPassphrase() {  
        return null;  
    }  

    @Override
	public String getPassword() {  
        return password;  
    }  

    @Override
	public boolean promptPassword(String arg0) {  
        return true;  
    }  

    @Override
	public boolean promptPassphrase(String arg0) {  
        return true;  
    }  

    @Override
	public boolean promptYesNo(String arg0) {  
        return true;  
    }  

    @Override
	public void showMessage(String arg0) {  
        System.out.println(arg0);  
    }  

}
