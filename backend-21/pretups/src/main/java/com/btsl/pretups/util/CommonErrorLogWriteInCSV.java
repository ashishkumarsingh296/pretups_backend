package com.btsl.pretups.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.pretups.common.PretupsI;
import com.restapi.networkadmin.geogrpahycellidmapping.requestVO.GegraphicalCellIdFileRequestVO;
import com.restapi.networkadmin.geogrpahycellidmapping.responseVO.UploadFileToAssociateCellIdResponseVO;
import org.apache.commons.io.FileUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.networkadmin.commissionprofile.requestVO.BatchAddCommisionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommisionProfileResponseVO;

/*CommonErrorLogWriteInCSV.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Mohak Dubey           			    23/03/16        Initial Creation
 *------------------------------------------------------------------------
 * 
 * Copyright (c) 2016 Mahindra Comviva.
 */

public class CommonErrorLogWriteInCSV
{
	
private static final Log LOGGER = LogFactory.getLog(CommonErrorLogWriteInCSV.class.getName());


public void writeDataInFile(ArrayList errorList,String _fileName,String filePath,String _networkCode, String uploadedFileNamePath) 

{
	   
        final String methodName = "writeDataInFile";
        String[] splitFileName = uploadedFileNamePath.split("/");
        String uploadedFileName = splitFileName[(splitFileName.length)-1];
    	if (LOGGER.isDebugEnabled()){
        	//_logger.debug(methodName," Entered: p_dirPath := "+p_dirPath+", p_fileName := "+p_fileName+", p_fileLabel := "+p_fileLabel+", p_beingProcessedDate := "+p_beingProcessedDate+", p_fileEXT := "+p_fileEXT+", p_fileNumber := "+p_fileNumber+", p_distinctNetwork := "+p_distinctNetwork);
        }       
        Writer out =null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        String fileName=null;
        try
        {
             
            Date date= new Date();
            filePath=filePath+_networkCode+"/C2S/";
            newFile1=new File(filePath);
            if(! newFile1.isDirectory())
        	 newFile1.mkdirs();
            SimpleDateFormat sdf1= new SimpleDateFormat("ddMMyy");
    		SimpleDateFormat sdf2= new SimpleDateFormat("HHmmss");
            fileName=filePath+_networkCode+"_"+_fileName+"_"+sdf1.format(date)+"_"+sdf2.format(date)+".csv";
            LOGGER.debug(methodName,"fileName := "+fileName);
            fileHeader=Constants.getProperty("ERROR_FILE_HEADER");
            newFile = new File(fileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write("ErrorLogFile for the uploaded file- "+uploadedFileName+"\n\n");
            out.write(fileHeader +"\n");
            for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
				
            	ListValueVO listValueVO = (ListValueVO)iterator.next();
            	out.write(listValueVO.getOtherInfo()+",");
            	out.write(listValueVO.getOtherInfo2()+",");
            	out.write(",");
            	out.write("\n");
            }
			out.write("End");
			
        }
        catch(Exception e)
        {
           
            LOGGER.debug(methodName, "Exception := " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"writeDataInFile[writeDataInFile]","","","","Exception:= "+e.getMessage());
             }
        finally
        {
            if (out!=null)
            	try{out.close();}catch(Exception e){
            		
            		LOGGER.error(methodName,  "Exception"+ e.getMessage());
            		LOGGER.errorTrace(methodName, e);
            	}
            	if (LOGGER.isDebugEnabled()){
            		LOGGER.debug(methodName,"Exiting... ");
            	}
        }
	}
public void writeDataInFileDownload(ArrayList errorList,String _fileName,String filePath,String _networkCode, String uploadedFileNamePath) 

{
	   	final String methodName = "writeDataInFileDownload";
        String[] splitFileName = uploadedFileNamePath.split("/");
        String uploadedFileName = splitFileName[(splitFileName.length)-1];
    	if (LOGGER.isDebugEnabled()){
        	LOGGER.debug(methodName,"Entered: "+methodName);
        }       
        Writer out =null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        String fileName=null;
        try
        {
             
            Date date= new Date();
            newFile1=new File(filePath);
            if(! newFile1.isDirectory())
        	 newFile1.mkdirs();
            fileName=filePath+_fileName;
            LOGGER.debug(methodName,"fileName := "+fileName);
            fileHeader=Constants.getProperty("ERROR_FILE_HEADER");
            newFile = new File(fileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write(fileHeader +"\n");
            for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
				
            	ListValueVO listValueVO = (ListValueVO)iterator.next();
            	out.write(listValueVO.getOtherInfo()+",");
            	out.write(listValueVO.getOtherInfo2()+",");
            	out.write(",");
            	out.write("\n");
            }
			out.write("End");
			
        }
        catch(Exception e)
        {
        	LOGGER.error(methodName,  "Exception"+ e.getMessage());
    		LOGGER.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"writeDataInFile[writeDataInFile]","","","","Exception:= "+e.getMessage());
             }
        finally
        {
            if (out!=null)
            	try{out.close();}catch(Exception e){
            		LOGGER.error(methodName,  "Exception"+ e.getMessage());
            	}
            	if (LOGGER.isDebugEnabled()){
            		LOGGER.debug(methodName,"Exiting... ");
            	}
        }
	}

public void writeDataInFileDownload(ArrayList errorList,String _fileName,String filePath,String _networkCode, String uploadedFileNamePath, String sheetName) 
{
	   	final String methodName = "writeDataInFileDownload";
        String[] splitFileName = uploadedFileNamePath.split("/");
        String uploadedFileName = splitFileName[(splitFileName.length)-1];
    	if (LOGGER.isDebugEnabled()){
        	LOGGER.debug(methodName,"Entered: "+methodName);
        }       
        Writer out =null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        String fileName=null;
        try
        {            
            Date date= new Date();
            newFile1=new File(filePath);
            if(! newFile1.isDirectory())
        	 newFile1.mkdirs();
            fileName=filePath+_fileName;
            LOGGER.debug(methodName,"fileName := "+fileName);
            fileHeader=Constants.getProperty("ERROR_FILE_HEADER");
            newFile = new File(fileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write("Sheet Name,");
            out.write(sheetName + "\n");
            out.write(fileHeader +"\n");
            for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
				
            	ListValueVO listValueVO = (ListValueVO)iterator.next();
            	out.write(listValueVO.getOtherInfo()+",");
            	out.write(listValueVO.getOtherInfo2()+",");
            	out.write(",");
            	out.write("\n");
            }
			out.write("End");
			
        }
        catch(Exception e)
        {
        	LOGGER.error(methodName,  "Exception"+ e.getMessage());
    		LOGGER.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"writeDataInFile[writeDataInFile]","","","","Exception:= "+e.getMessage());
             }
        finally
        {
            if (out!=null)
            	try{out.close();}catch(Exception e){
            		LOGGER.error(methodName,  "Exception"+ e.getMessage());
            	}
            	if (LOGGER.isDebugEnabled()){
            		LOGGER.debug(methodName,"Exiting... ");
            	}
        }
}

public void writeDataMsisdnInFileDownload(ArrayList errorList,String _fileName,String filePath,String _networkCode, String uploadedFileNamePath,Boolean headval) 

{
	   	final String methodName = "writeDataMsisdnInFileDownload";
        String[] splitFileName = uploadedFileNamePath.split("/");
        String uploadedFileName = splitFileName[(splitFileName.length)-1];
    	if (LOGGER.isDebugEnabled()){
        	LOGGER.debug(methodName,"Entered: "+methodName);
        }       
        Writer out =null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        String fileName=null;
        try
        {
             
            Date date= new Date();
            newFile1=new File(filePath);
            if(! newFile1.isDirectory())
        	 newFile1.mkdirs();
            fileName=filePath+_fileName;
            LOGGER.debug(methodName,"fileName := "+fileName);
            if(headval){
            	fileHeader=Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");
            }
            else{
            fileHeader=Constants.getProperty("ERROR_FILE_HEADER_PAYOUT");
            }
            newFile = new File(fileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write(fileHeader +"\n");
            for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
				
            	ListValueVO listValueVO =iterator.next();
            		out.write(listValueVO.getOtherInfo()+",");
            		if(!headval){
            		out.write(listValueVO.getCodeName()+",");
            		}
                	out.write(listValueVO.getOtherInfo2()+",");
            	
            	out.write(",");
            	out.write("\n");
            }
			out.write("End");
			
        }
        catch(Exception e)
        {
           
            LOGGER.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"writeDataInFile[writeDataInFile]","","","","Exception:= "+e.getMessage());
             }
        finally
        {
        	if (LOGGER.isDebugEnabled()){
        		LOGGER.debug(methodName,"Exiting... ");
        	}
            if (out!=null)
            	try{
            		out.close();
            		}
            catch(Exception e){
            	 LOGGER.errorTrace(methodName, e);
            }
            	
        }
	}

public void writeDataInFileForBatchAddCommPro(Locale locale,ArrayList errorList, String _fileName, String filePath,
		String _networkCode, String uploadedFileNamePath, BatchAddCommisionProfileRequestVO request,BatchAddCommisionProfileResponseVO response) throws  Exception

{

	final String METHOD_NAME = "writeDataInFileForBatchAddCommPro";
	Writer out = null;
	File newFile = null;
	File newFile1 = null;
	String fileHeader = null;
	Date date = new Date();
	if (LOGGER.isDebugEnabled())
		LOGGER.debug(METHOD_NAME, "Entered");
	try {
		File fileDir = new File(filePath);
		if (!fileDir.isDirectory())
			fileDir.mkdirs();

		filePath += _networkCode + "/C2S/";
		String _fileName1 = filePath + _networkCode + "_" + _fileName + "_"
				+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
		newFile1 = new File(filePath);
		if (!newFile1.isDirectory())
			newFile1.mkdirs();
		String absolutefileName = _fileName1;
//		fileHeader = Constants.getProperty("ERROR_FILE_HEADER");

		fileHeader= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_LINENO_LABEL, null) + "," + RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_MESSAGE_LABEL, null);
		String sheetNameHeader =RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_SHEETNAME_LABEL, null) + "," + response.getSheetName();
		newFile = new File(absolutefileName);
		out = new OutputStreamWriter(new FileOutputStream(newFile));
		out.write(RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_ERROR_LOG, null) + request.getFileName() + "\n\n");
		out.write(sheetNameHeader + "\n");
		out.write(fileHeader + "\n");
		for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
			ListValueVO listValueVO = iterator.next();
			out.write(listValueVO.getOtherInfo() + ",");
			out.write(listValueVO.getOtherInfo2() + ",");
			out.write(",");
			out.write("\n");
		}
		out.close();
		File error = new File(absolutefileName);
		byte[] fileContent = FileUtils.readFileToByteArray(error);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		response.setFileAttachment(encodedString);
		response.setFileName(
				_networkCode + "_" + _fileName + "_" + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv");
		response.setFileType("csv");

	} catch (Exception e) {
		LOGGER.debug(METHOD_NAME, "Exception := " + e.getMessage());
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"writeDataInFileForBatchAddCommPro[writeDataInFileForBatchAddCommPro]", "", "", "",
				"Exception:= " + e.getMessage());
		throw e;

	} finally {

		try {
			if (out != null) {
				out.close();
			}
		} catch (Exception e) {

			LOGGER.error(METHOD_NAME, "Exception" + e.getMessage());
			LOGGER.errorTrace(METHOD_NAME, e);
			throw e;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(METHOD_NAME, "Exiting... ");
		}
	}
}
	public void writeDataInErrorFileForGeogrphicalCellIdMapping(Locale locale, ArrayList errorList, String _fileName, String filePath,
																String _networkCode, String uploadedFileNamePath, GegraphicalCellIdFileRequestVO request  , UploadFileToAssociateCellIdResponseVO response) throws  Exception

	{

		final String METHOD_NAME = "writeDataInErrorFileForGeogrphicalCellIdMapping";
		Writer out = null;
		File newFile = null;
		File newFile1 = null;
		String fileHeader = null;
		Date date = new Date();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(METHOD_NAME, PretupsI.ENTERED);
		try {
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory())
				fileDir.mkdirs();

			filePath += _networkCode ;
			String _fileName1 = filePath + _networkCode + "_" + _fileName + "_"
					+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
			newFile1 = new File(filePath);
			if (!newFile1.isDirectory())
				newFile1.mkdirs();
			String absolutefileName = _fileName1;

			fileHeader= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_LINENO_LABEL, null) + "," + RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_MESSAGE_LABEL, null);
			newFile = new File(absolutefileName);
			out = new OutputStreamWriter(new FileOutputStream(newFile));
			out.write(RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_ERROR_LOG, null) + request.getFileName() + "\n\n");
			out.write(fileHeader + "\n");
			for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
				ListValueVO listValueVO = iterator.next();
				out.write(listValueVO.getOtherInfo() + ",");
				out.write(listValueVO.getOtherInfo2() + ",");
				out.write(",");
				out.write("\n");
			}
			out.close();
			File error = new File(absolutefileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			response.setFileAttachment(encodedString);
			response.setFileName(
					_networkCode + "_" + _fileName + "_" + BTSLUtil.getFileNameStringFromDate(new Date()) + PretupsI.CSV_EXT);
			response.setFileType(PretupsI.FILE_CONTENT_TYPE_CSV);

		} catch (Exception e) {
			LOGGER.debug(METHOD_NAME, "Exception := " + e.getMessage());
			throw e;

		} finally {

			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {

				LOGGER.error(METHOD_NAME, "Exception" + e.getMessage());
				LOGGER.errorTrace(METHOD_NAME, e);
				throw e;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(METHOD_NAME, PretupsI.EXITED);
			}
		}
	}
}