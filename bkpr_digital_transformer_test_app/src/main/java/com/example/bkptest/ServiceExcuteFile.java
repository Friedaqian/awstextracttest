package com.example.bkptest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Files;

public class ServiceExcuteFile {
	private File tempfile;
	
	/**
	 * 
	 * @param prefix     define the prefix of file
	 * @param suffixList define the valid file types
	 * @return a tempfile, if upload is successful.
	 * @throws FileNotFoundException
	 */
	public File findFile(String prefix, String[] suffixList) throws FileNotFoundException {

		File folder = new File(System.getProperty("java.io.tmpdir"));
		File[] listOfFiles = folder.listFiles();
		List<File> foundFiles = new ArrayList<>();

		for (File file : listOfFiles) {
			boolean check = checkFileType(suffixList, file);

			if (file.isFile() && check && (file.getName().startsWith(prefix)) && file.length()>0) {
				foundFiles.add(file);

			}
		}
		if (!foundFiles.isEmpty()) {

			Optional<File> opfile = foundFiles.stream()
					.max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
			tempfile = opfile.get();

		} else {
			throw new FileNotFoundException("there is no uploaded file.");
		}
		return tempfile;

	}

	/**
	 * private method to check the valid file types
	 * 
	 * @param suffixList define the valid file types
	 * @param file       uploaded tempfile
	 * @return
	 */
	private boolean checkFileType(String[] suffixList, File file) {

		boolean check = false;

		for (String suffix : suffixList) {
			if (file.getName().endsWith(suffix))
				check = true;
		}
		return check;
	}
	
	/**
	 * 
	 * @param suffix       define the suffix of file
	 * @param prefix       define the prefix of file
	 * @param uploadedFile is a by /upload uploaded file
	 * @throws IOException
	 */
	public File createTempFile (final String prefix, final String suffix, final File uploadedFile, final String inputString) throws IOException {
		final File file = File.createTempFile(prefix,
				uploadedFile.getName().replace(Files.getFileExtension(uploadedFile.getAbsolutePath()), suffix));

		try (BufferedWriter filewriter = Files.newWriter(file, StandardCharsets.UTF_8)) {
			filewriter.write(inputString);
		}

		return file;

	}

}
