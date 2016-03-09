package com.qualiycode.remote_cli_connection;

import java.util.regex.Pattern;


/**
 * This class represents a prompt to look after executing a command on a remote machine and it's corresponding action 
 * 
 * @author Eli Rozenfeld
 *
 */
public class Prompt {
	
	/**
	 * prompt - String that defines the prompt content.
	 */
	private String prompt;

	/**
	 * defines if the prompt string should be searched as written or as a regular expression.
	 */
	private boolean regularExpression;
	
	/**
	 * the pattern for the regular expression (optional) if this is a regularExpression prompt 
	 */
	private Pattern pattern;
	
	/**
	 * mark if the regular expression is case sensitive or not (default is not case sensitive) 
	 */
	private boolean regexCaseInsensitive;
	
	/**
	 * indicate the end of the terminal output, default is false
	 */
	private boolean markEndOfOutput;

	/**
	 * the String to send to the terminal if the prompt was found (for example when doing sudo we want to send the password)
	 */
	private String responseString;

	/**
	 * marks if an enter String will be added to the stringToSend, default is true 
	 */
	private boolean addEnter;

	public Prompt(){
		prompt = null;
		regularExpression = false;
		pattern = null;
		regexCaseInsensitive = false;
		markEndOfOutput = false;
		responseString = null;
		addEnter = true;
	}
	
	/**
	 * @param prompt - the prompt string (in case you use RegEx you should also use the setRegularExpression() function)
	 */
	public void setPrompt(String prompt){
		this.prompt = prompt;
	}
	
	/**
	 * @param prompt - the prompt string (in case you use RegEx you should also use the setRegularExpression() function)
	 * @param addEnter - if true will execute "enter" if this prompt found
	 */
	public void setPrompt(String prompt, boolean addEnter){
		this.prompt = prompt;
		this.addEnter = addEnter;
	}
	
	/**
	 * @return the prompt string
	 */
	public String getPrompt(){
		return prompt;
	}
	
	/**
	 * Use this function to mark the prompt string as regular expression
	 * @param isRegularExpression - set to true to mark the prompt as regular expression
	 * @param isRegexCaseSensitive - set to true to mark the regular expression as case sensitive
	 */
	public void setRegularExpression(boolean isRegularExpression, boolean isRegexCaseSensitive){
		regularExpression = isRegularExpression;
		if(regularExpression){
			regexCaseInsensitive = isRegexCaseSensitive;
			pattern =  Pattern.compile(prompt, Pattern.DOTALL | (regexCaseInsensitive ? Pattern.CASE_INSENSITIVE : 0));
		}
	}
	
	/**
	 * @return true if this prompt will be used as regular expression
	 */
	public boolean isRegularExpression(){
		return regularExpression;
	}
	
	/**
	 * @return the prompt as regular expression compiled pattern
	 */
	public Pattern getPattern() throws Exception{
		if (!regularExpression){
			throw new Exception("Prompt is not regular expression... therfor asking for pattern is illegal"); 
		} 
		return pattern;
	}

	/**
	 * @return true if the prompt regular expression is case sentitive
	 */
	public boolean isRegexCaseInsensitive() throws Exception{
		if (!regularExpression){
			throw new Exception("Prompt is not regular expression... therfor asking about case sensitivity is illegal"); 
		} 
		return regexCaseInsensitive;
	}

	/**
	 * @return true if this prompt indicate the end of the terminal output
	 */
	public boolean isMarkEndOfOutput() {
		return markEndOfOutput;
	}

	/**
	 * @param markEndOfOutput - set to true for indicating this prompt as the end of the terminal output
	 */
	public void setMarkEndOfOutput(boolean markEndOfOutput) {
		this.markEndOfOutput = markEndOfOutput;
	}

	/**
	 * @return the String to send in case this prompt was found in the terminal output
	 */
	public String getResponseString() {
		return responseString;
	}

	/**
	 * @param responseString - the String to send as a response in case this prompt was found in the terminal output
	 * Note: the response string will be sent with "Enter" trailing it
	 */
	public void setResponseString(String responseString) {
		setResponseString(responseString, true);
	}

	/**
	 * @param responseString - the String to send as a response in case this prompt was found in the terminal output
	 * @param addEnter set to true if you wish to add "Enter" after sending this response string
	 */
	public void setResponseString(String responseString, boolean addEnter) {
		this.responseString = responseString;
		this.addEnter = addEnter;
	}
	
	/**
	 * @return true if response string is needed to be send after this prompt
	 */
	public boolean isSendResponseString(){
		return responseString != null;
	}

	/**
	 * @return true if "Enter" is added after this prompt has found in terminal output
	 */
	public boolean isAddEnter() {
		return addEnter;
	}

	/**
	 * @param addEnter set to true if you wish to add "Enter" after this prompt has found in terminal output
	 */
	public void setAddEnter(boolean addEnter) {
		this.addEnter = addEnter;
	}

}
