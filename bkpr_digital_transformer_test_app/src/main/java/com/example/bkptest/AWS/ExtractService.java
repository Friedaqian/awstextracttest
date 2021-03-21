package com.example.bkptest.AWS;

import com.example.bkptest.Configuration;
import com.example.bkptest.ShouldBeExcutedFile;
import com.example.bkptest.ServiceExcute;
import com.example.bkptest.ServiceExcuteFile;
import com.example.bkptest.exceptionhandling.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

import com.amazonaws.services.textract.AmazonTextract;

import com.amazonaws.services.textract.AmazonTextractClientBuilder;

import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;

import com.amazonaws.util.IOUtils;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

/**
 * ExtractService implement the interface ServiceExcute,
 * {@link com.example.bkptest.ServiceExcute} which use the AWS Texttract to extract the files
 * 
 * @author yinjueqian
 *
 */
public class ExtractService implements ServiceExcute {

	static Logger logger = LoggerFactory.getLogger(ExtractService.class);

	private File file;
	private ServiceExcuteFile tf;
	private ByteBuffer imageBytes;

	final private Configuration config;
	final private ShouldBeExcutedFile excutedfile;

	public ExtractService(Configuration config, ShouldBeExcutedFile excutedfile) throws ApplicationException {
		super();
		if (config == null) {
			throw new ApplicationException("the configuration for running the applicaiton is invalid.");
		}
		this.config = config;
		this.excutedfile = excutedfile;
	}

	/**
	 * this method to work on extracting
	 * @throws ApplicationException 
	 * @throws FileNotFoundException 
	 * @throws Exception, if credential is invalid or file could not be found.Or
	 *                    there is an IOException
	 */
	@Override
	public void execute() throws FileNotFoundException, ApplicationException {

		findAndReadFile();

		logger.info("textract service is starting....");

		createResultAsJsonFile();

		logger.info("textract service ended.");

	}

	/**
	 * to find a file and read it
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 */
	private void findAndReadFile() throws ApplicationException, FileNotFoundException {
		tf = new ServiceExcuteFile();
		file = tf.findFile(excutedfile.getPrefix(), excutedfile.getSuffixList());
		try (InputStream is = Files.newInputStream(Paths.get(file.getAbsolutePath()))) {
		
			imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(is));
		
		} catch (FileNotFoundException e) {
			throw  e;
		}catch (IOException e) {
			throw new ApplicationException("There is an Exception: "+ e.getMessage());
		}
	}				

	/**
	 * the found file be extracted and result as a json file
	 */
	private void createResultAsJsonFile() {

		try {
		    final String awsCredential = config.getAwsCredential();
			final String awsRegion = config.getAwsRegion();

			if (awsCredential.isEmpty() || awsRegion.isEmpty()) {
				logger.error("aws credential or aws region isn't concret configured to start the service.");
				throw new IllegalArgumentException("there is no credential or region to be found.");
			}
			
			AmazonTextract client = AmazonTextractClientBuilder
					 .standard()
					 .withEndpointConfiguration(new EndpointConfiguration(
						String.format("https://textract.%s.amazonaws.com", awsRegion), awsRegion))
					.withCredentials(new ProfileCredentialsProvider(awsCredential))
					.build();
			DetectDocumentTextRequest request = new DetectDocumentTextRequest()
					.withDocument(new Document().withBytes(imageBytes));
			DetectDocumentTextResult result = client.detectDocumentText(request);

			String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result);
			new ServiceExcuteFile().createTempFile("textextract-", "json", file, json);
		
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}catch (SdkClientException e) {
			throw new IllegalArgumentException("There is no credential: " + config.getAwsCredential() + " in .aws/credentials.");
		}

	}

}
