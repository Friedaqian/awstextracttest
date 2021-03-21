package com.example.bkptest.exceptionhandling;

/**
 * This is an exception when application cannot start.
 * @author yinjueqian
 *
 */
public class ApplicationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException() {
		super();
	}

	@Override
    public String getMessage() {
        return "There is an exception. Application couldn't start.";
    }

}
