package com.example.bkptest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.bkptest.AWS.ExtractService;
import com.example.bkptest.Demo.DemoExtractService;
import com.example.bkptest.exceptionhandling.ApplicationException;

import io.dropwizard.logback.shaded.checkerframework.checker.nullness.qual.NonNull;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * 
 * Create a .env file in the root of your project to configure the feature
 * 
 * @see <a href = "https://github.com/cdimascio/dotenv-java">github guide</a>
 * @param type be used to define which service you want to use according
 *             {@link ServiceExcuteType}
 * @author yinjueqian
 *
 */
public class ServiceExcuteFactory {

	static Logger logger = LoggerFactory.getLogger(ServiceExcuteFactory.class);

	/**
	 * 
	 * @param type is from {@link ServiceExcuteType}
	 * @return a concrete service
	 * @throws ApplicationException
	 */
	public static ServiceExcute getServiceExcute(@NonNull ServiceExcuteType type, Configuration config, ShouldBeExcutedFile excutedfile) 
			throws ApplicationException {
		ServiceExcute serviceexcute = null;
	
		try {
			Dotenv dotenv = Dotenv.load();
			String serviceProvider = dotenv.get("SERVICE_PROVIDER_ENV_VAR");
		
			if (type == ServiceExcuteType.TEXTEXTRACT) {
				
				serviceexcute = (ServiceExcute) Class
						.forName(String.format("com.example.bkptest.%s.ExtractService", serviceProvider))
						.getConstructor(Configuration.class, ShouldBeExcutedFile.class ).newInstance(config, excutedfile);
			}
			if (type == ServiceExcuteType.UPLOAD) {
				serviceexcute = (ServiceExcute) Class
						.forName(String.format("com.example.bkptest.%s.UploadService", serviceProvider))
						.getConstructor(Configuration.class, ShouldBeExcutedFile.class ).newInstance(config, excutedfile);
			}

			if (type == ServiceExcuteType.DEMO) {
				serviceexcute = (ServiceExcute) Class
						.forName("com.example.bkptest.Demo.DemoExtractService")
						.getConstructor(Configuration.class).newInstance(config);
			}
		} catch (ClassNotFoundException ex) {
			logger.error("There is an exception:" + ex.getMessage());
			throw new ApplicationException("There is an exception: " + ex.getMessage());
		} catch (Exception ex) {
			logger.error("There is an exception: " + ex.getMessage() + " " + ex.getCause());
			throw new ApplicationException("There is an exception: " + ex.getMessage());
		}
		return serviceexcute;
	}

}
