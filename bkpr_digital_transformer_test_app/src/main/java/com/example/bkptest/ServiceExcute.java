package com.example.bkptest;

/**
 * ServiceExcute is an interface with method execute,
 * which should be implemented by all service classes.
 * @author yinjueqian
 *
 */
public interface ServiceExcute {

	/**
	 * this method to work on extracting
	 * @throws Exception, if credential is invalid or file could not be found.
	 */
	void execute() throws Exception;


}
