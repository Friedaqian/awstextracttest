package com.example.bkptest.Demo;

import com.example.bkptest.Configuration;
import com.example.bkptest.ServiceExcute;
import com.example.bkptest.exceptionhandling.ApplicationException;

/**
 * demo is only to test the application
 * @author yinjueqian
 *
 */
public class DemoExtractService implements ServiceExcute{
	
	final private Configuration config;

	public DemoExtractService(Configuration config) throws ApplicationException {
		super();
		if (config == null) {
			throw new ApplicationException("there is an exception because of invalid configuration.");
		}
		this.config = config;
	}

	@Override
	public void execute() {
		final String credential = config.getAwsCredential();
		System.out.println("Textextract service is start with " + credential);	
	}


}
