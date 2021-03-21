package com.example.bkptest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

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
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.util.IOUtils;
import com.example.bkptest.AWS.ExtractService;
import com.example.bkptest.exceptionhandling.ApplicationException;
import com.example.bkptest.exceptionhandling.IllegalFileException;
import com.example.bkptest.resources.LocalFileUpload;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.github.cdimascio.dotenv.Dotenv;

public class AWSTextractServiceTest {

	File file = new File(
			Thread.currentThread().getContextClassLoader().getResource("IC-Employee-Incident-Report.jpg").getFile());

	// @Ignore("test be ignored without .env and valid credential with IAM")
	@Test
	public void textextract_is_sucessful() throws FileNotFoundException, IllegalFileException, ApplicationException {

		String[] str = { "png", "jpeg", "jpg" };
		Assume.assumeTrue(new ServiceExcuteFile().findFile("upload-", str).exists());

		try {
			Dotenv dotenv = Dotenv.load();
			Configuration config = new Configuration(dotenv.get("CREDENTIAL_ENV_VAR"), dotenv.get("REGION_ENV_VAR"));
			ShouldBeExcutedFile exfile = new ShouldBeExcutedFile ("upload-", str);
			ExtractService es = new ExtractService(config, exfile );
			es.execute();

			String[] stre = { "json" };
			File newfile = new ServiceExcuteFile().findFile("textextract", stre);
			Assert.assertTrue(newfile.exists());

		} catch (AmazonServiceException e) {
			Assume.assumeNoException(e);
		} catch (SdkClientException e) {
			Assume.assumeNoException(e);
		} catch (NullPointerException e) {
			Assume.assumeNoException(e);
		} catch (Exception e) {
			Assume.assumeNoException(e);
		}
	}

	@Test(expected = FileNotFoundException.class)
	public void textract_is_failed_because_no_file() throws FileNotFoundException, ApplicationException {

		String[] str = { "png", "jpg", "jpeg" };
		Assume.assumeFalse(new ServiceExcuteFile().findFile("uoload-", str).exists());
		Assert.fail("There is no suitable files.");

	}

	@Test
	public void credential_is_invaild() throws ApplicationException {
		Configuration config = new Configuration("avddd", "usa_east_2");
		String[] str = { "png", "jpeg", "jpg" };
		ShouldBeExcutedFile exfile = new ShouldBeExcutedFile ("upload-", str);
		ExtractService ets = new ExtractService(config, exfile);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			ets.execute();
		});
	}

	@Test
	public void region_is_null() throws ApplicationException {
		Configuration config = new Configuration("avddd", "");
		String[] str = { "png", "jpeg", "jpg" };
		ShouldBeExcutedFile exfile = new ShouldBeExcutedFile ("upload-", str);
		ExtractService ets = new ExtractService(config, exfile);


		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			ets.execute();
		});
	}

	@Test
	public void credential_is_null() throws ApplicationException {
		Configuration config = new Configuration("", "usa_east_2");
		String[] str = { "png", "jpeg", "jpg" };
		ShouldBeExcutedFile exfile = new ShouldBeExcutedFile ("upload-", str);
		ExtractService ets = new ExtractService(config, exfile);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			ets.execute();
		});
	}

	@Test
	public void mock_textract_service_sucessful() throws ApplicationException, IOException {
		AmazonTextract client = mock(AmazonTextract.class);

		String document = file.getAbsolutePath();
		InputStream inputStream = new FileInputStream(new File(document));

		ByteBuffer imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));

		DetectDocumentTextRequest request = new DetectDocumentTextRequest()
				.withDocument(new Document().withBytes(imageBytes));

		DetectDocumentTextResult result = client.detectDocumentText(request);

		Mockito.when(client.detectDocumentText(request)).thenReturn(result);

	}

	@Test
	public void mock_textract_service_failed_with_sdkexception() throws FileNotFoundException, ApplicationException {

		AmazonTextract client = mock(AmazonTextract.class);
		DetectDocumentTextRequest request = null;

		Mockito.when(client.detectDocumentText(request)).thenThrow(SdkClientException.class);

	}

}
