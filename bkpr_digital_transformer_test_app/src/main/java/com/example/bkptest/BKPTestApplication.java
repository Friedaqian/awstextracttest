package com.example.bkptest;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.example.bkptest.exceptionhandling.ApplicationException;
import com.example.bkptest.exceptionhandling.MyExceptionMapper;
import com.example.bkptest.resources.LocalFileUpload;
import com.example.bkptest.resources.TempFileTextextract;
import com.example.bkptest.resources.TempFileUpload;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.cdimascio.dotenv.Dotenv;
import io.dropwizard.forms.MultiPartBundle;

public class BKPTestApplication extends Application<BKPTestConfiguration> {

	public static void main(final String[] args) throws Exception {
		new BKPTestApplication().run(args);
	}

	@Override
	public String getName() {
		return "BKPTest";
	}

	@Override
	public void initialize(final Bootstrap<BKPTestConfiguration> bootstrap) {

		bootstrap.addBundle(new MultiPartBundle());

	}

	/**
	 * set .env in project root folder
	 * serviceCredential to set the aws credential
	 * serviceRegion to set the aws region
	 * bucket to set the name of aws s3 bucket to upload
	 */
	@Override
	public void run(final BKPTestConfiguration configuration, final Environment environment)
			throws ApplicationException {

		try {
			Dotenv dotenv = Dotenv.load();
			String servieceCredential = dotenv.get("CREDENTIAL_ENV_VAR");
			String serviceRegion = dotenv.get("REGION_ENV_VAR");
			String bucket = dotenv.get("BUCKET_NAME_ENV_VAR");

			Configuration config = new Configuration(servieceCredential, serviceRegion, bucket);
			Configuration s3config = new Configuration(servieceCredential, serviceRegion);
			String[] fileType = { "jpg", "jpeg", "png" };
			ShouldBeExcutedFile excutedfile = new ShouldBeExcutedFile("upload-", fileType);

			ServiceExcute textextract = ServiceExcuteFactory.getServiceExcute(ServiceExcuteType.TEXTEXTRACT, config,
					excutedfile);
			ServiceExcute s3service = ServiceExcuteFactory.getServiceExcute(ServiceExcuteType.UPLOAD, s3config,
					excutedfile);

			environment.jersey().register(MultiPartFeature.class);
			environment.jersey().register(new LocalFileUpload());
			environment.jersey().register(new MyExceptionMapper());
			environment.jersey().register(new TempFileUpload(s3service));
			environment.jersey().register(new TempFileTextextract(textextract));
		
		} catch (NullPointerException ex) {
			throw new ApplicationException("configuration is invaild." + ex.getMessage());
		} catch (Exception ex) {
			throw new ApplicationException("Application cannot be started: " + ex.getMessage());
		}
	}

}
