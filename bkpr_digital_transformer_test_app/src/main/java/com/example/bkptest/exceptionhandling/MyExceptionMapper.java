package com.example.bkptest.exceptionhandling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import io.dropwizard.jersey.errors.ErrorMessage;

/**
 * 
 * MyExceptionMapper for {@link Exception}.
 * @author yinjueqian
 *
 */
public class MyExceptionMapper implements ExceptionMapper<Exception>{

	@Override
	public Response toResponse(Exception exception) {
		Response response = null;
		
		if (exception instanceof FileNotFoundException || exception instanceof IllegalFileException) {
			response = Response.status(Status.BAD_REQUEST.getStatusCode())
			.entity(new ErrorMessage("Application cannot be used because there is no file. Please upload the file again."))
			.entity(exception.getMessage())
			.build();
		}
		else if (exception instanceof IllegalArgumentException 
		    || exception instanceof ApplicationException 
			|| exception instanceof UncheckedIOException
			|| exception instanceof RuntimeException) {
			response = Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode())
			.entity(new ErrorMessage("There is an Exception. Service cannot be used."))
			.entity(exception.getMessage())
			.build();
		}
		
		return response;
	}

}
