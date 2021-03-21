package com.example.bkptest.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.Consumes;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.codahale.metrics.annotation.Timed;
import com.example.bkptest.exceptionhandling.ApplicationException;
import com.example.bkptest.exceptionhandling.IllegalFileException;

/**
 * Upload the file to create a tempfile
 * @author yinjueqian
 *
 */
@Path("/upload")
@Produces(MediaType.APPLICATION_JSON)
public class LocalFileUpload {

	/**
	 * 
	 * @param inputStream
	 * @param fileDetail
	 * @return an response with status 200
	 * @throws IllegalFileException if file type is invalid
	 * @throws ApplicationException if other exceptions appear during the uploading.
	 */
	@POST
	@Timed
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("localfile") InputStream inputStream,
			@FormDataParam("localfile") FormDataContentDisposition fileDetail)
			throws IllegalFileException, ApplicationException {
		
		String fileName = fileDetail.getFileName();

		checkFileType(fileName);
		
	    createTempFile(fileName, inputStream);

		return Response.ok().entity("File is sucessfully uploaded.").build();

	}

	private void checkFileType(String fileName) throws IllegalFileException {

		if (!fileName.endsWith("png") && !fileName.endsWith("pdf") && !fileName.endsWith("jpg")
				&& !fileName.endsWith("jpeg")) {
			throw new IllegalFileException();
		}
	}

	private void createTempFile(String fileName, InputStream inputStream) throws ApplicationException {
	
		try {
		File tempFile = File.createTempFile("upload-", fileName);
		String fileUploadPath = tempFile.getAbsolutePath();
		writeAndSaveFile(inputStream, fileUploadPath);
		System.out.println(fileUploadPath);
		
		}catch (IOException e) {
			System.out.println("upload is not sucessfully.");
			throw new ApplicationException();
		}
		
	}

	private void writeAndSaveFile(InputStream inputStream, String fileUploadPath) throws IOException {
		
		try (OutputStream os = Files.newOutputStream(Paths.get(fileUploadPath))) {
		
			int n;
			byte[] buffer = new byte[1024 * 64];

			while ((n = inputStream.read(buffer)) != -1) {
				os.write(buffer, 0, n);
			}
		 
	    }


	}

}
