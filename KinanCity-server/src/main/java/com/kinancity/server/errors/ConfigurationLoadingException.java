package com.kinancity.server.errors;

/**
 * Fired when an error happend when loading configuration
 * 
 * @author drallieiv
 *
 */
public class ConfigurationLoadingException extends Exception {

	private static final long serialVersionUID = 5032010119111791410L;

	public ConfigurationLoadingException() {
	}

	public ConfigurationLoadingException(String arg0) {
		super(arg0);
	}

	public ConfigurationLoadingException(Throwable arg0) {
		super(arg0);
	}

	public ConfigurationLoadingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ConfigurationLoadingException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
