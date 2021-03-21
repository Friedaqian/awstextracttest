package com.example.bkptest;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileNotFoundException;

import javax.ws.rs.client.Client;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import org.mockito.Mockito;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.bkptest.AWS.UploadService;
import com.example.bkptest.exceptionhandling.ApplicationException;
import com.example.bkptest.resources.LocalFileUpload;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.findify.s3mock.S3Mock;
import io.findify.s3mock.route.PutObject;
import io.github.cdimascio.dotenv.Dotenv;

public class AWSS3ServiceTest {

	S3Mock s3Mock = new S3Mock.Builder().withPort(8012).withInMemoryBackend().build();

	@Test
	public void s3_upload() throws Exception {
		s3Mock.start();
		final String awsRegion = "us-east-2";
		final String s3BucketName = "ddddd-us-djdj";

		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withPathStyleAccessEnabled(true)
				.withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
				.withEndpointConfiguration(new EndpointConfiguration("http://localhost:8012", awsRegion)).build();
		s3client.createBucket(s3BucketName);
		s3Mock.stop();
	}

	//@Ignore("test be ignored without .env and valid credential with IAM")
	@Test
	public void upload_is_sucessful() throws FileNotFoundException, ApplicationException {

		String[] str = { "json" };
		Assume.assumeTrue(new ServiceExcuteFile().findFile("textextract-", str).exists());
		String[] strr = { "png", "jpeg", "jpg" };
		Assume.assumeTrue(new ServiceExcuteFile().findFile("upload-", strr).exists());
		
		ShouldBeExcutedFile exfile = new ShouldBeExcutedFile("upload-", strr);
		
		try {
		Dotenv env = Dotenv.load();
		
		Configuration config = new Configuration();
		config.setAwsCredential(env.get("CREDENTIAL_ENV_VAR"));
		config.setAwsRegion(env.get("REGION_ENV_VAR"));
		config.setS3BucketName(env.get("BUCKET_NAME_ENV_VAR"));


		UploadService ues = new UploadService(config,exfile);
		ues.execute();
		}
		catch (AmazonServiceException e) {
			Assume.assumeNoException(e);
		}catch (SdkClientException e) {
			Assume.assumeNoException(e);
		}catch (NullPointerException e) {
			Assume.assumeNoException(e);
		}

	}
	
	
	@Test
	public void upload_is_failed_because_no_file() throws FileNotFoundException, ApplicationException {

		String[] str = { "json" };
		Assume.assumeFalse(new ServiceExcuteFile().findFile("textextract-", str).exists());
		String[] strr = { "png", "jpeg", "jpg" };
		Assume.assumeTrue(new ServiceExcuteFile().findFile("upload-", strr).exists());
		
		Assert.fail("There is no suitable files.");

	}
	

	@Test
	public void region_is_null() throws ApplicationException {
		Configuration config = new Configuration();
		ShouldBeExcutedFile exfile = new ShouldBeExcutedFile("upload-", new String [] {"jpg","jpeg","png"});

		config.setAwsCredential("");
		config.setAwsRegion("");
		config.setS3BucketName("");

		UploadService es = new UploadService(config, exfile);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			es.execute();
		});

	}

	@Test
	public void without_credential() throws ApplicationException {
		Configuration config = new Configuration();
		ShouldBeExcutedFile exfile = new ShouldBeExcutedFile("upload-", new String [] {"jpg","jpeg","png"});

		config.setAwsRegion("");
		config.setS3BucketName("");

		UploadService es = new UploadService(config, exfile);
		Assertions.assertThrows(NullPointerException.class, () -> {
			es.execute();
		});

	}


	@Test
	public void mock_upload_service_with_sdkexception () throws FileNotFoundException, ApplicationException {
		AmazonS3 client = mock(AmazonS3.class);

		Mockito.when(client.putObject(null)).thenThrow(SdkClientException.class);
		

	}

}
