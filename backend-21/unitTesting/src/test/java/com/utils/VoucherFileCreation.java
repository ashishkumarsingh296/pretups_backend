package com.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.classes.BaseTest;
import com.classes.UniqueChecker;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.sshmanager.ConnectionManager;

/*
 * Specific to VHA client
 */

public class VoucherFileCreation extends BaseTest{

	public String[] createFileforProvisioning(String fileName, String records, String voucherType, 
			String pinExpiryDate,String groupID) {
		
		long noOfRecordsInFile = Long.parseLong(records);
		String batchNo = UniqueChecker.UC_SerialNumber();
		String startSeq = "000001";
		String endSeq = null;
		String serialNo = batchNo+startSeq;
		long startSerialNo  = Long.parseLong(serialNo);
		
		String pinNo = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		
		long startPinNo = Long.parseLong(pinNo);
		
		String distributor = "Coles";
		String directory = ".\\Output\\VHA\\PROVISION";
		String absolutePath = directory + File.separator + fileName;
		
		FileOutputStream fileOutputStream = null;
		String fileHeader = null; String fileContent[] = new String[Integer.parseInt(records)];
		Log.info("fileName: "+fileName);
		// write the content in file 
		try {  
			fileOutputStream = new FileOutputStream(absolutePath);
			fileHeader ="SERVRET,PROFILE,SERIALNUM,EXPIRYDATE,PINNUMBER,DISTRIBUTOR," + noOfRecordsInFile + "\n";
		    fileOutputStream.write(fileHeader.getBytes());
			for(int i=0;i<noOfRecordsInFile;i++) {
				fileContent[i] = "VODAFONE"+"," + groupID + "," + startSerialNo + "," + pinExpiryDate + "," + startPinNo + "," + distributor + "\n";
				
			    fileOutputStream.write(fileContent[i].getBytes());
			    startPinNo++;
				startSerialNo++;
			}
			fileOutputStream.close();
			Log.info("<pre>"+Arrays.toString(fileContent)+"</pre>");
			
			endSeq = String.valueOf(--startSerialNo).substring(5, 11);
		    Log.info("File Created");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		Session session;

		try {
			session = ConnectionManager.getInstance();
		} catch (JSchException ex) {
			Log.error("Error while getting SSH Server Instance : " + ex);
			return null;
		}
		
		String source = absolutePath;
		String destination = _masterVO.getClientDetail("VOMS_VOUCHER_FILE_PATH")+fileName;
		
		putFiletoSFTP(session, source, destination);

		return new String[]{batchNo,startSeq,endSeq,distributor,pinNo};
	}
	
	
	public void createFileforActivation(String fileName, String[] data, String records) {
		
		long noOfRecordsInFile = Long.parseLong(records);
		
		String directory = ".\\Output\\VHA\\ACTIVATION"; 
		String absolutePath = directory + File.separator + fileName;
		
		FileOutputStream fileOutputStream = null;
		String fileContent = null;
		// write the content in file 
		try {  
			fileOutputStream = new FileOutputStream(absolutePath);
			fileContent ="BATCHNUM,STARTSEQ,ENDSEQ,DISTRIBUTOR," + noOfRecordsInFile + "\n";
		    fileOutputStream.write(fileContent.getBytes());
			for(int i=0;i<noOfRecordsInFile;i++) {
				fileContent = data[0] + "," + data[1] + "," + data[2] + "," + data[3] +"\n";
				
			    fileOutputStream.write(fileContent.getBytes());
			    Log.info("File Content:"+fileContent);
			}
		    Log.info("File Created:"+fileName);
		    fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		Session session;

		try {
			session = ConnectionManager.getInstance();
		} catch (JSchException ex) {
			Log.error("Error while getting SSH Server Instance : " + ex);
			return;
		}
		
		String source = absolutePath;
		String destination = _masterVO.getClientDetail("VOMS_VOUCHER_FILE_PATH_ACTIVATION")+fileName;
		
		putFiletoSFTP(session, source, destination);

	}
	
	public static void putFiletoSFTP(Session session, String sourcepath, String destinationpath) {
		try {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.put(sourcepath, destinationpath);
			sftpChannel.exit();
			channel.disconnect();
		} catch (JSchException JSchEx) {
			Log.error("Error while opening SFTP Channel : " + JSchEx);
		} catch (SftpException SftpEx) {
			Log.error("Error while pushing " + sourcepath + " file to SSH Server : " + SftpEx +" at destination:"+destinationpath);
		}
	}
	
	
}
