package com.ctmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.commons.ExcelI;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.utils.Log;
import com.utils._masterVO;

public class CTHelper {
	
	private static Session session = null;
	private static Channel channel = null;
	private static ChannelSftp sftpChannel = null;

	public static void cleanOutputDirectory(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				cleanOutputDirectory(file);
			} else {
				file.delete();
			}
		}
	}
	
	public static void cleanDataProvider() throws IOException {
		List<String> sheetsToPreserve = Arrays.asList(ExcelI.MASTER_SHEET_NAME, ExcelI.TRANSFER_MATRIX_SHEET, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		String NEWFILE_NAME = _masterVO.getProperty("ConfigPath") + "DataProvider_" + System.getProperty("current.date") + ".xlsx";
		File ExecutedDataProviderFile = new File(_masterVO.getProperty("DataProvider"));
		File ExecutedDataProviderFile_Rename = new File(NEWFILE_NAME);
		ExecutedDataProviderFile.renameTo(ExecutedDataProviderFile_Rename);
		
		File ExecutedDataProvider_NEW = new File(NEWFILE_NAME);
		File CleanDataProvider = new File(_masterVO.getProperty("ConfigPath") + "DataProvider.xlsx");
		
		FileUtils.copyFile(ExecutedDataProvider_NEW, CleanDataProvider);
		
		try {
			FileInputStream ExcelFile = new FileInputStream(NEWFILE_NAME);
			XSSFWorkbook ExcelWBook = new XSSFWorkbook(ExcelFile);
			int sheetsToBeRemoved = ExcelWBook.getNumberOfSheets() - sheetsToPreserve.size();
			for (int i = 0; i < sheetsToBeRemoved; i++) {
				for (int j = 0; j < ExcelWBook.getNumberOfSheets(); j++) {
					String sheetname = ExcelWBook.getSheetName(j);
					if (!sheetsToPreserve.contains(sheetname)) {
						ExcelWBook.removeSheetAt(j);
					}
				}
			}
			
			FileOutputStream fileOut = new FileOutputStream(CleanDataProvider);
			ExcelWBook.write(fileOut);
			ExcelWBook.close();
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			Log.info("Error performing setExcelFile():");
			Log.writeStackTrace(e);
		}
	}
	
	public static void uploadOutput(File outputDirectoryPath, String actionid) {
		
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(_masterVO.getProperty("CTMode.automation.server.username"),_masterVO.getProperty("CTMode.automation.server.ip"), 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(_masterVO.getProperty("CTMode.automation.server.password"));
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;
		
			sftpChannel.cd("reports");
			sftpChannel.mkdir(actionid);
			sftpChannel.cd(actionid);
			sftpChannel.mkdir("Output");
			sftpChannel.cd("Output");
			fileUploadProcess(outputDirectoryPath);
			
			sftpChannel.disconnect();
			channel.disconnect();
			session.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	private static void fileUploadProcess(File outputDirectoryPath) throws SftpException, IOException {
		File[] files = outputDirectoryPath.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				sftpChannel.mkdir(file.getName());
				sftpChannel.cd(file.getName());
				fileUploadProcess(file);
				sftpChannel.cd("..");
			} else {
				FileInputStream fis = new FileInputStream(file);
				sftpChannel.put(fis, file.getName());
				fis.close();
			}
		}
	}
}
