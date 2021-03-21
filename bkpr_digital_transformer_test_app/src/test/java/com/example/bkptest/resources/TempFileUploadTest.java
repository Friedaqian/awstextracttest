package com.example.bkptest.resources;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileNotFoundException;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import com.example.bkptest.BKPTestApplication;
import com.example.bkptest.BKPTestConfiguration;
import com.example.bkptest.Configuration;
import com.example.bkptest.ServiceExcute;
import com.example.bkptest.ServiceExcuteFactory;
import com.example.bkptest.ServiceExcuteFile;
import com.example.bkptest.ServiceExcuteType;
import com.example.bkptest.ShouldBeExcutedFile;
import com.example.bkptest.AWS.ExtractService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.github.cdimascio.dotenv.Dotenv;


public class TempFileUploadTest {
	
	ServiceExcuteFile temp = new ServiceExcuteFile();

	TempFileUpload tfu;
	ServiceExcute se;
	Configuration config;
	ShouldBeExcutedFile exfile;

	@SuppressWarnings("deprecation")
	@ClassRule
	public static final DropwizardAppRule<BKPTestConfiguration> RULE = new DropwizardAppRule<BKPTestConfiguration>(BKPTestApplication.class,
			ResourceHelpers.resourceFilePath("testconfig.yml"));
	
	Client client = RULE.client().register(TempFileUpload.class);
	
	 private static WireMockServer mockServer =
		      new WireMockServer(new WireMockConfiguration().dynamicPort());
	
	@Before
	public void setUp() throws Exception {
		

		exfile = new ShouldBeExcutedFile("upload-", new String [] {"jpg","jpeg","png"});
		
		Dotenv dotenv = Dotenv.load();
		String servieceCredential = dotenv.get("CREDENTIAL_ENV_VAR");
		String serviceRegion = dotenv.get("REGION_ENV_VAR");
        config = new Configuration(servieceCredential, serviceRegion);
		
		se = ServiceExcuteFactory.getServiceExcute(ServiceExcuteType.TEXTEXTRACT, config, exfile);
		tfu = new TempFileUpload(se);
		 
	}
	 @BeforeClass
	 public static void startServer() {
		    mockServer.start();
		  }

	  @AfterClass
	  public static void stopServer() {
	    mockServer.stop();
	  }

	  @Test
	  public void return_status_200 () throws Exception {
		  String[] suffixList = {"json"};
		Assume.assumeTrue(new ServiceExcuteFile().findFile("textextract-", suffixList).exists());
		Assert.assertEquals(200, tfu.tempfileupload().getStatus());
	  }
	  
	  @Test
	  public void return_status_400 () throws Exception {
		String[] suffixList = {"json"};
		Assume.assumeFalse(new ServiceExcuteFile().findFile("textextract-", suffixList).exists());
		Assert.assertEquals(400, tfu.tempfileupload().getStatus());
	  }
	  
	  
	  @Test
	    public void is_500 () throws Exception {
	    	
		  mockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/upload")).willReturn(WireMock.aResponse().withStatus(500)));
	    	
	    	Response rp = client.target(			
	                String.format("http://localhost:%d/upload", mockServer.port()))
	               .request()
	               .get();
	    	
	    	Assert.assertEquals(response().getStatus(), rp.getStatus());
	    	
	    }
	    
	    @Test
	    public void is_200 () throws Exception {
	    		
	    	mockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/upload"))
	    			.willReturn(WireMock.aResponse().withStatus(200)));
	    	
	    	Response rp = client.target(			
	                String.format("http://localhost:%d/upload", mockServer.port()))
	               .request()
	               .get();
	    	
	    	Assert.assertEquals(200, rp.getStatus());
	    	
	    }
	    
	    private Response response() {
	    	Response rp = null;
	    	Configuration config = new Configuration("profile2","anb");
	    	ShouldBeExcutedFile exfile = new ShouldBeExcutedFile("upload-", new String [] {"jpg","jpeg","png"});
	     	
	    	try{
	    	 	
	        	ExtractService es = new ExtractService(config, exfile);
	        	es.execute();
	        	rp =Response.status(200).build();
	    	
	    	}catch (Exception ex) {
	    		ex.getLocalizedMessage();
	    		rp= Response.status(500).build();
	        	
	    	}
			return rp;
			
	    }
    
    
}
