package com.example.bkptest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.example.bkptest.ServiceExcute;

@Path(value = "/textextract")
@Produces(MediaType.APPLICATION_JSON)
public class TempFileTextextract {
	
	ServiceExcute textextract;
	
	public TempFileTextextract(ServiceExcute textextract) {
		super();
		this.textextract = textextract;
	}

	/**
	 * 
	 * @return an response with status 200, if aws textract has successfully
	 *         executed.
	 * @throws Exception
	 */
	@GET
	public Response tempFileTextextract() throws Exception {
		Response response = null;
			textextract.execute();
			response = Response.ok("File has been extracted.").build();	
		return response;

	}

}
