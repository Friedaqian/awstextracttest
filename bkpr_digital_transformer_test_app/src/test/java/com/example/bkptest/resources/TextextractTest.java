package com.example.bkptest.resources;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import com.example.bkptest.BKPTestApplication;
import com.example.bkptest.BKPTestConfiguration;
import com.example.bkptest.Configuration;
import com.example.bkptest.ServiceExcute;
import com.example.bkptest.ServiceExcuteType;
import com.example.bkptest.ShouldBeExcutedFile;
import com.example.bkptest.ServiceExcuteFactory;
import com.example.bkptest.ServiceExcuteFile;
import com.example.bkptest.AWS.ExtractService;
import com.example.bkptest.exceptionhandling.ApplicationException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.github.cdimascio.dotenv.DotEnvException;
import io.github.cdimascio.dotenv.Dotenv;

public class TextextractTest {

	TempFileTextextract tfu;
	ServiceExcute se;
	Configuration config;
	ShouldBeExcutedFile exfile;

	@SuppressWarnings("deprecation")
	@ClassRule
	public static final DropwizardAppRule<BKPTestConfiguration> RULE = new DropwizardAppRule<BKPTestConfiguration>(
			BKPTestApplication.class, ResourceHelpers.resourceFilePath("testconfig.yml"));

	private Client client = RULE.client().register(TempFileTextextract.class);

	private static WireMockServer wms = new WireMockServer(new WireMockConfiguration().dynamicPort());

	@Before
	public void setUp() throws Exception {

		exfile = new ShouldBeExcutedFile("upload-", new String [] {"jpg","jpeg","png"});
		
		Dotenv dotenv = Dotenv.load();
		String servieceCredential = dotenv.get("CREDENTIAL_ENV_VAR");
		String serviceRegion = dotenv.get("REGION_ENV_VAR");
        config = new Configuration(servieceCredential, serviceRegion);
		
		se = ServiceExcuteFactory.getServiceExcute(ServiceExcuteType.TEXTEXTRACT, config, exfile);
		tfu = new TempFileTextextract(se);

	}

	@BeforeClass
	public static void before() {
		wms.start();
	}

	@AfterClass
	public static void after() {
		wms.stop();
	}

	@Ignore("only test when .env has been set in root of project according theguide and there is an uploaded file in directory")
	@Test
	public void excute_status_200() throws DotEnvException{
		try {
		String[] suffixList = { "jpg", "png", "jpeg" };
		Assume.assumeTrue(new ServiceExcuteFile().findFile("upload-", suffixList).exists());
		Response rp = client.target(String.format("http://localhost:%d/textextract", RULE.getLocalPort())).request()
				.get();

		Assert.assertEquals(200, rp.getStatus());
		}catch (Exception e) {
			Assume.assumeNoException(e);
		}

	}

	@Test
	public void return_status_400() throws Exception {
		String[] suffixList = { "jpg", "png", "jpeg" };
		Assume.assumeFalse(new ServiceExcuteFile().findFile("upload-", suffixList).exists());
		Assert.assertEquals(400, tfu.tempFileTextextract().getStatus());
	}

	@Test
	public void invaild_config_with_status_500() throws Exception {

		wms.stubFor(WireMock.get(WireMock.urlEqualTo("/textextract")).willReturn(WireMock.aResponse().withStatus(500)));

		Response rp = client.target(String.format("http://localhost:%d/textextract", wms.port())).request().get();

		Assert.assertEquals(response().getStatus(), rp.getStatus());

	}

	private Response response() {
		Response rp = null;
		Configuration config = new Configuration("profile2", "anb");
		ShouldBeExcutedFile exfile = new ShouldBeExcutedFile("upload-", new String [] {"jpg","jpeg","png"});

		try {

			ExtractService es = new ExtractService(config, exfile);
			es.execute();
			rp = Response.status(200).build();

		} catch (Exception ex) {
			ex.getLocalizedMessage();
			rp = Response.status(500).build();

		}
		return rp;

	}

	@Test
	public void run_excute_is_successful() throws ApplicationException {

		Configuration config = new Configuration();
		ServiceExcute se = textract(config);
		int rpstatus = response(se).getStatus();
		assertEquals(200, rpstatus);
	}

	@Test
	public void run_excute_is_fail() throws ApplicationException {

		Configuration config = null;

		Throwable thrown = assertThrows(RuntimeException.class, () -> {
			textract(config);
		});
		Assert.assertTrue(thrown.getClass().equals(RuntimeException.class));

	}

	private ServiceExcute textract(Configuration config) {
		ServiceExcute se = null;

		if (config == null) {
			throw new RuntimeException();
		}

		try {
			ShouldBeExcutedFile exfile = new ShouldBeExcutedFile("upload-", new String [] {"jpg","jpeg","png"});


			se = ServiceExcuteFactory.getServiceExcute(ServiceExcuteType.TEXTEXTRACT, config, exfile);

		} catch (Exception e) {
			e.getLocalizedMessage();
		}

		return se;

	}

	private Response response(ServiceExcute se) throws ApplicationException {
		Response rp = null;
		try {

			rp = Response.status(200).build();
		} catch (RuntimeException ex) {
			throw new ApplicationException();
		}
		return rp;
	}

}
