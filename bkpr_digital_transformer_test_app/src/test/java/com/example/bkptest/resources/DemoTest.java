package com.example.bkptest.resources;

import org.junit.Assert;
import org.junit.Test;

import com.example.bkptest.Configuration;
import com.example.bkptest.ServiceExcute;
import com.example.bkptest.ServiceExcuteType;
import com.example.bkptest.ServiceExcuteFactory;

import com.example.bkptest.Demo.DemoExtractService;
import com.example.bkptest.exceptionhandling.ApplicationException;


public class DemoTest {
	
	@Test
	public void demo_can_excute () throws ApplicationException {
		
		Configuration config = new Configuration("abd", "def");
		DemoExtractService se = new DemoExtractService(config);
		se.execute();
		
	}
	

	@Test (expected = ApplicationException.class)
	public void configuration_is_null() throws Exception {

		Configuration config = null;
		DemoExtractService se = new DemoExtractService(config);
		se.execute();
		
	}

	@Test
	public void with_config_get_demoexcute_and_run() throws Exception {

		Configuration config = new Configuration("abd", "def");
		demoexcute(config).execute();
		
		Assert.assertEquals(DemoExtractService.class,demoexcute(config).getClass());
		

	}

	private ServiceExcute demoexcute(Configuration config) throws Exception {
		ServiceExcute se = null;
		try {

			se = ServiceExcuteFactory.getServiceExcute(ServiceExcuteType.DEMO, config, null);

		} catch (Exception e) {
			e.getLocalizedMessage();
		}
		return se;
	}
	





}
