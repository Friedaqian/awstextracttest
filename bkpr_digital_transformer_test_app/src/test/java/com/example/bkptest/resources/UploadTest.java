package com.example.bkptest.resources;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.ws.rs.client.Client;

import javax.ws.rs.client.Entity;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;


import com.example.bkptest.BKPTestApplication;
import com.example.bkptest.BKPTestConfiguration;
import com.example.bkptest.exceptionhandling.ApplicationException;
import com.example.bkptest.exceptionhandling.IllegalFileException;


import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.testing.junit.ResourceTestRule;

public class UploadTest {

	@SuppressWarnings("deprecation")
	@ClassRule
	public static final DropwizardAppRule<BKPTestConfiguration> RULE = new DropwizardAppRule<BKPTestConfiguration>(BKPTestApplication.class,
			ResourceHelpers.resourceFilePath("testconfig.yml"));
	
	@SuppressWarnings("deprecation")
	final Client client = RULE.client().register(MultiPartFeature.class).register(LocalFileUpload.class);


	final File testfile = new File(Thread.currentThread().getContextClassLoader()
			.getResource("IC-Employee-Incident-Report.jpg").getFile());
	final File testtxt = new File(Thread.currentThread().getContextClassLoader().getResource("test.txt").getFile());

	final FormDataContentDisposition fileDetail = FormDataContentDisposition.name("test").fileName("IC-Employee-Incident-Report.jpg").build();
	final FormDataContentDisposition fileDetailtxt = FormDataContentDisposition.name("test").fileName("test.txt")
			.build();

	private LocalFileUpload localfileupload;

	@Before
	public void setUp() throws FileNotFoundException, ParseException {

		localfileupload = new LocalFileUpload();

	}

	@SuppressWarnings("deprecation")
	@Test
	public void failed_because_exclude_content() {

		Response response = client.target(String.format("http://localhost:%d/upload", RULE.getLocalPort()))
				.request().post(null);

		Assert.assertEquals(500, response.getStatus());
	}


	@Test
	public void uploadfile_is_sucessed() throws IOException {

		@SuppressWarnings("resource")
		FormDataMultiPart multipart = (FormDataMultiPart) new FormDataMultiPart().field("foo", "bar")
				.bodyPart(new FileDataBodyPart("localfile", testfile));
		final Response response = client.target(String.format("http://localhost:%d/upload", RULE.getLocalPort())).request()
				.post(Entity.entity(multipart, multipart.getMediaType()));
		System.out.println(multipart.contentDisposition(fileDetail).getContentDisposition().getFileName());
		System.out.println(response.getStatus());
		
		Assert.assertEquals(200, response.getStatus());
	}

	@Test
	public void failed_because_filetype_illegal(){
		@SuppressWarnings("resource")
		FormDataMultiPart multipart = (FormDataMultiPart) new FormDataMultiPart().field("foo", "bar")
				.bodyPart(new FileDataBodyPart("localfile", testtxt));
		
		final Response response = client.target(String.format("http://localhost:%d/upload", RULE.getLocalPort()))
				.request().post(Entity.entity(multipart, multipart.getMediaType()));

		Assert.assertEquals(400,
				response.getStatus());
	}
	
	public class FailingInputStream extends InputStream {
	    @Override
	    public int read() throws IOException {
	        throw new IOException();
	    }
	}
	
	@Test (expected = IOException.class)
	public void test() throws IOException, ApplicationException, IllegalFileException {
		LocalFileUpload ufl = new LocalFileUpload();
		int in = new FailingInputStream().read();
		
		if(in ==0) {
			throw new ApplicationException();
		}

		ufl.uploadFile(new FailingInputStream(), fileDetail);
		
	}
	
	
	
	@Test
	public void throwIllegalFileException() {
		
		Throwable thrown = assertThrows(
				IllegalFileException.class,
				() -> {
					localfileupload.uploadFile(new FileInputStream(testtxt), fileDetailtxt);
					throw new IllegalFileException();
				}
				);
		Assert.assertEquals("the file type is not invalid.", thrown.getMessage());
	}

	@Test
	public void upload_is_sucessed() throws IllegalFileException, ApplicationException {
		try {
			Assert.assertEquals(200,
					localfileupload.uploadFile(new FileInputStream(testfile), fileDetail).getStatus());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(testfile.getName());
	}


}
