package com.utils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

public class FileToBase64 {

	public static String fileToBase64(String filePath) {
		byte[] fileContent = null;
		try {
			fileContent = FileUtils.readFileToByteArray(new File(filePath));
		} catch (IOException e) {
			Log.info(filePath + " not found in the directory");
		}
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		return encodedString;
	}
}
