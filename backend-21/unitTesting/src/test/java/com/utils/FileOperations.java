package com.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class FileOperations {

	public static void createFile(List<String> data, String path) throws IOException {

		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		for (int i = 0; i < data.size(); i++) {
			if (i == data.size() - 1) {
				bw.write(data.get(i));
			} else {
				bw.write(data.get(i) + ",");
			}
		}
		bw.close();

	}

	public static void deleteFile(String path) {
		File file = new File(path);
		if (file.delete()) {
			Log.info(file.getName() + " deleted successfully");
		} else {
			Log.info("File not found to delete");
		}
	}

	public static String getFileName(String path) {
		File file = new File(path);
		return file.list()[0].toString();
	}

	public static File file(String path) {
		File file = new File(path);
		return file;
	}

	public static void deleteFilesFromFolderIfExists(String folderPath) {
		File file = new File(folderPath);

		File[] fileList = file.listFiles();
		for (File f : fileList) {
			if(f.exists()) {
				f.delete();
			}else {
				System.out.println("No files in " + folderPath);
			}
		}
	}
}
