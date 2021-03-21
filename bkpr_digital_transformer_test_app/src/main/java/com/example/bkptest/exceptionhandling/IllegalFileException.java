package com.example.bkptest.exceptionhandling;

/**
 * This is an exception that is thrown when the type of uploaded file is invalid.
 * @author yinjueqian
 *
 */
public class IllegalFileException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public IllegalFileException(String message) {
		super(message);
	}

	public IllegalFileException() {
		super();
	}

	@Override
    public String getMessage() {
        return "the file type is not invalid.";
    }

}
