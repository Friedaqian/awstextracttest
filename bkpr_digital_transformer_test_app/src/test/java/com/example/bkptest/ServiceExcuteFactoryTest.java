package com.example.bkptest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.example.bkptest.AWS.UploadService;



public class ServiceExcuteFactoryTest {
	
	String serviceProvide;
	ServiceExcute serviceexcute; 

	@Before
	public void setUp() throws Exception {
		serviceProvide="AWS";
				
	}

	
	@Test(expected= ClassNotFoundException.class)
	public void serviceProvider_is_null() throws Exception  {
		String serviceProvider = null;
		serviceexcute = (ServiceExcute) Class.forName(String.format("com.example.bkptest.%s.UploadService", serviceProvider)).newInstance();	 
		serviceexcute.execute();	
		
	}
	
	@Test(expected= ClassNotFoundException.class)
	public void serviceProvider_is_not_exist() throws Exception  {
		String serviceProvider = "Demo";
		serviceexcute = (ServiceExcute) Class.forName(String.format("com.example.bkptest.%s.DemoService", serviceProvider)).newInstance();	 
		serviceexcute.execute();	
		
	}
	
	@Test(expected= ClassNotFoundException.class)
	public void serviceProvider_is_Empty() throws Exception  {
		String serviceProvider = " ";
		serviceexcute = (ServiceExcute) Class.forName(String.format("com.example.bkptest.%s.UploadService", serviceProvider)).newInstance();	 
		serviceexcute.execute();	
		
	}
	
	@Test
	public void getServiceInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		serviceexcute =  (ServiceExcute)Class.forName("com.example.bkptest.AWS.UploadService")
			 .getConstructor(Configuration.class, ShouldBeExcutedFile.class )
			 .newInstance(new Configuration(),new ShouldBeExcutedFile());

		Assert.assertEquals(UploadService.class.getName(), serviceexcute.getClass().getName());
	}
	
	@Test
	public void getUploadServiceInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Configuration config = new Configuration();
		ShouldBeExcutedFile excutedfile = new ShouldBeExcutedFile();
		 Class<?> constructor =  Class.forName("com.example.bkptest.AWS.UploadService");
			Constructor<?> cons = constructor.getConstructor(Configuration.class, ShouldBeExcutedFile.class );
					
					UploadService us =	(UploadService) cons.newInstance(config, excutedfile);

		Assert.assertEquals(UploadService.class.getName(), us.getClass().getName());
	}

}
