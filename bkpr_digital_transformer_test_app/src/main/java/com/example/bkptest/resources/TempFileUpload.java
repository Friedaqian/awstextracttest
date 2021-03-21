package com.example.bkptest.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.example.bkptest.ServiceExcute;
import com.example.bkptest.exceptionhandling.ApplicationException;

@Path(value = "/uploadtempfile")
@Produces(MediaType.APPLICATION_JSON)
public class TempFileUpload {

	ServiceExcute serviceExcute;

	public TempFileUpload(ServiceExcute serviceExcute) {
		super();
		this.serviceExcute = serviceExcute;
	}

	/**
	 * 
	 * @return an response with status 200, if aws S3 service has successfully
	 *         executed.
	 * @throws ApplicationException
	 */
	@POST
	public Response tempfileupload() throws Exception {

		Response response = null;
		serviceExcute.execute();
		response = Response.ok("upload is sucessed.").build();

		return response;

	}

}
