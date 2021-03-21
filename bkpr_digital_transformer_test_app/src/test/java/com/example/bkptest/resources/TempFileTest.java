package com.example.bkptest.resources;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.example.bkptest.ShouldBeExcutedFile;
import com.example.bkptest.ServiceExcuteFile;

public class TempFileTest {
	
	File file;
	ServiceExcuteFile tf;

	@Before
	public void setUp() throws Exception {
		 tf = new ServiceExcuteFile();
		 
	}

	@Test
	public void findthelastmodifyfile_in_tempfolder() throws FileNotFoundException {
		String [] a = {"jpg", "png"};
		File file = tf.findFile("upload-", a);
		System.out.println(file.getAbsolutePath());
		Assert.assertTrue(file.getName().startsWith("upload"));
		
	}
	
	@Test
	public void createAJsonFile() throws IOException {
		String result = "sssssss";
		String [] a = {"jpg", "png"};
		tf.createTempFile("text-", "json", tf.findFile("upload-", a), result);
		System.out.println(result);
		
		String [] b = {"json"};
		String abc = tf.findFile("text-", b).getName();
		Assert.assertTrue(abc.contains("text"));
		
	}

}
