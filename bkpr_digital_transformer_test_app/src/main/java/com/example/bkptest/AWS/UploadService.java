package com.example.bkptest.AWS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.bkptest.Configuration;
import com.example.bkptest.ShouldBeExcutedFile;
import com.example.bkptest.ServiceExcute;
import com.example.bkptest.ServiceExcuteFile;
import com.example.bkptest.exceptionhandling.ApplicationException;

/**
 * UploadService implement the interface ServiceExcute,
 * {@link com.example.bkptest.ServiceExcute} which use the AWS S3 Put to upload
 * the extracted files
 * 
 * @author yinjueqian
 *
 */
public class UploadService implements ServiceExcute {

	static Logger logger = LoggerFactory.getLogger(UploadService.class);

	/**
	 * This a local variable to configure the aws service:
	 * {@link Configuration.class}
	 */
	final private Configuration config;
	final private ShouldBeExcutedFile excutedfile;

	/**
	 * 
	 * @param config the config parameter not null
	 * @throws ApplicationException when configuration is invalid
	 */
	public UploadService(Configuration config, ShouldBeExcutedFile excutedfile) throws ApplicationException {
		super();
		if (config == null) {
			throw new ApplicationException("the configuration for running the applicaiton is invalid.");
		}
		this.config = config;
		this.excutedfile = excutedfile;

	}

	/**
	 * this method to work on uploading
	 * 
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 * 
	 * @throws Exception,            if credential is invalid or file could not be
	 *                               found.
	 */
	@Override
	public void execute() throws ApplicationException, FileNotFoundException {

		/**
		 * excuteFileSuffix is used to define the valid file-types jsonFileSuffix is
		 * used to define in order to find the json data
		 */
		final String awsCredential = config.getAwsCredential();
		final String awsRegion = config.getAwsRegion();

		if (awsCredential.isEmpty() || awsRegion.isEmpty()) {
			logger.error("aws credential or aws region isn't concret configured to start the service.");
			throw new IllegalArgumentException("there is no credential or region to be found.");
		}

		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider(awsCredential)).withRegion(awsRegion).build();

		try {
			List<File> uploadtos3filelist = new ArrayList<>();

			File uploadedfile = new ServiceExcuteFile().findFile(excutedfile.getPrefix(), excutedfile.getSuffixList());
			File tempjsonfile = new ServiceExcuteFile().findFile("textextract-", new String[] { "json" });
			uploadtos3filelist.add(uploadedfile);
			uploadtos3filelist.add(tempjsonfile);

			for (File file : uploadtos3filelist) {

				String fileName = file.getName();
				String filePath = file.getAbsolutePath();
				logger.info("service is starting....");

				s3client.putObject(config.getS3BucketName(), fileName, new File(filePath));

				logger.info("service ended.");
			}
		} catch (FileNotFoundException e) {
			logger.error("No file to be found" + e.getMessage() + e.getCause());
			throw e;
		} catch (AmazonServiceException e) {
			logger.error("The call was transmitted successfully, but Amazon S3 couldn't process it. "
					+ e.getErrorMessage() + e.getCause());
			throw new ApplicationException(
					"There is an amazon service exception: " + e.getErrorMessage() + e.getCause());
		} catch (SdkClientException e) {
			logger.error("Amazon S3 couldn't be contacted for a response." + e.getMessage() + e.getCause());
			throw new ApplicationException("There is an Exception: " + e.getMessage() + e.getCause());
		} catch (NullPointerException e) {
			logger.error("There is an Exception." + e.getMessage() + e.getCause());
			throw new ApplicationException("There is an Exception." + e.getMessage() + e.getCause());
		}

	}
}
