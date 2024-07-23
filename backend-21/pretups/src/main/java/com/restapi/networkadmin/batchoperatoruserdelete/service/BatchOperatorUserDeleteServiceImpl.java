package com.restapi.networkadmin.batchoperatoruserdelete.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.BatchOPTUserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.UploadFileRequestVO;
import com.restapi.networkadmin.batchoperatoruserdelete.response.BatchOperatorUserDeleteResponseVO;
import com.restapi.networkadmin.batchoperatoruserdelete.serviceI.BatchUserOperatorDeleteServiceI;

import jakarta.servlet.http.HttpServletResponse;

@Service("BatchUserOperatorDeleteServiceI")
public class BatchOperatorUserDeleteServiceImpl implements BatchUserOperatorDeleteServiceI{
	
	
	public static final Log LOG = LogFactory.getLog(BatchOperatorUserDeleteServiceImpl.class.getName());
	public static final String CLASS_NAME = "BatchOperatorUserDeleteServiceImpl";
	
	@Override
	public BatchOperatorUserDeleteResponseVO batchOperatorUserDelete(Connection con, UserVO userVO,
			UploadFileRequestVO uploadRequestVO, BatchOperatorUserDeleteResponseVO response,HttpServletResponse responseSwagger) throws BTSLBaseException, Exception{
		if (LOG.isDebugEnabled()) {
	            LOG.debug("confirmDelete", "Entered");
	        }
	        final String METHOD_NAME = "batchOperatorUserDelete";
	        Date currentDate = new Date();	        String fileDelimit = ",";
	        String dir = Constants.getProperty("DownloadBatchOperatorUserPath");// Upload
	        BatchOPTUserDAO batchuserDAO = new BatchOPTUserDAO();
	                // Read and Process file
	        
	        BufferedReader br = null;
	        String line = null;
	        InputStream is = null;
	        InputStreamReader inputStreamReader = null;
	        ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
		      
	        byte[] data  =fileUtil.decodeFile(uploadRequestVO.getFileAttachment());     ;// ak
	       
	        boolean isFileUploaded = false;
	        String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
	        String contentType2 = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_CSV);
	        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	    
	        boolean isValidFileType= false;
	        if (uploadRequestVO.getFileName().endsWith(".txt")||uploadRequestVO.getFileName().endsWith(".csv") ) {
	        	isValidFileType =true; 
	        }
		    if(!isValidFileType) {
		    	throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,PretupsErrorCodesI.FILE_COULD_NOT_BE_UPLOADED_TRY_A_VALID_FILE , "");

		    }
	        
            if (BTSLUtil.isNullString(dir)) {
                throw new BTSLBaseException(CLASS_NAME,METHOD_NAME, PretupsErrorCodesI.DIR_NOT_CREATED);
            }
            File dirtest = new File(dir);
            if (!dirtest.exists()) {
                if (!(dirtest.mkdirs())) {
                	throw new BTSLBaseException(CLASS_NAME,METHOD_NAME, PretupsErrorCodesI.DIR_NOT_CREATED);
                    
                }
            }
            String fileSize = Constants.getProperty("OTHER_FILE_SIZE");
            File requestToFile = new File(uploadRequestVO.getFileName());
            FileUtils.writeByteArrayToFile(requestToFile, data);
            if(uploadRequestVO.getFileName().endsWith(".txt"))
            	isFileUploaded = this.uploadFileToServer(requestToFile, data, dir, contentType,  Long.parseLong(fileSize));
         
            else if(uploadRequestVO.getFileName().endsWith(".csv")) {
                isFileUploaded = this.uploadFileToServer(requestToFile, data, dir, contentType2,  Long.parseLong(fileSize));
            }
            if(!isFileUploaded) {
            	throw new BTSLBaseException(CLASS_NAME,METHOD_NAME, PretupsErrorCodesI.FILE_ALREADY_EXISTS);
                
            }
           
            
	        is = new ByteArrayInputStream(data);
	        inputStreamReader = new InputStreamReader(is);
		    br = new BufferedReader(inputStreamReader);
		    StringBuffer tempStr = new StringBuffer();
	                
	                StringTokenizer startparser = null;
	                do {
	                    line = br.readLine();
	                    if (line != null) {
	                        // added for Aujas Defect Fixing for file content
	                        // validation - Start
	                        boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
	                        if (!isFileContentValid) {
	                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_COULD_NOT_BE_UPLOADED_TRY_A_VALID_FILE, "");
	                        }
	                        // added for Aujas Defect Fixing for file content
	                        // validation - End
	                        startparser = new StringTokenizer(line, fileDelimit); // separate
	                                                                              // each
	                                                                              // string
	                                                                              // in
	                                                                              // a
	                                                                              // line
	                    }
	                    while (startparser.hasMoreTokens()) {
	                        tempStr.append(startparser.nextToken().trim());
	                        tempStr.append(fileDelimit);
	                    }
	                } while (line != null);

	                String[] loginIDsArr = batchuserDAO.confirmBatchDelete(con, tempStr.toString().split(fileDelimit), userVO, currentDate);
	               
	                if (loginIDsArr[0] != null && loginIDsArr[0].length() > 0 && loginIDsArr[1] == null) {
	                    // invalid
	                	con.rollback();
	                	String msg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.OPERATOR_USER_DELETE_FAILED, new String[] { "" });
	                	response.setMessage(msg);
	                	ArrayList errorList = new ArrayList();
	                	errorList.add(loginIDsArr[0]);
	                	response.setErrorList(errorList);
	                	response.setMessageCode(PretupsErrorCodesI.OPERATOR_USER_DELETE_FAILED);
	                	response.setStatus(HttpStatus.SC_BAD_REQUEST);
	                	responseSwagger.setStatus(PretupsI.RESPONSE_FAIL);       
	                	downloadErrorLogFile(userVO, loginIDsArr[0], response);
	                    return response;
	                } else if (loginIDsArr[0] != null && loginIDsArr[0].length() > 0 && loginIDsArr[1] != null && loginIDsArr[1].length() > 0)// both
	                                                                                                                                          // invalid
	                {	con.commit();
	                	String msg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.OPERATOR_USER_DELETE_PARTIALLY_SUCCESS, new String[] { "" });
	                	response.setMessage(msg);
	                	response.setMessageCode(PretupsErrorCodesI.OPERATOR_USER_DELETE_PARTIALLY_SUCCESS);
	                	response.setStatus(HttpStatus.SC_BAD_REQUEST);
	                	responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
	                	
	                    downloadErrorLogFile(userVO, loginIDsArr[0], response);
	                    return response;
	                } else {
	                	con.commit();
	                	response.setStatus((HttpStatus.SC_OK));
	        			String resmsg = RestAPIStringParser.getMessage(locale,
	        				PretupsErrorCodesI.OPERATOR_USER_DELETE_SUCCESSFULLY, null);
	        			response.setMessage(resmsg);
	        			response.setMessageCode(PretupsErrorCodesI.OPERATOR_USER_DELETE_SUCCESSFULLY);	                   return response;
	                }
	            
	}
	
	   public void downloadErrorLogFile(UserVO userVO, String errorString,BatchOperatorUserDeleteResponseVO response ) {
			final String METHOD_NAME = "downloadErrorLogFile";
			if (LOG.isDebugEnabled())
				LOG.debug(METHOD_NAME, "Entered");
			try {
				String [] errorList = errorString.split(",");
				String filePath = Constants.getProperty("DownloadBatchOperatorUserPath");
				try {
					File fileDir = new File(filePath);
					if (!fileDir.isDirectory())
						fileDir.mkdirs();
				} catch (Exception e) {
					LOG.errorTrace(METHOD_NAME, e);
					LOG.error(METHOD_NAME, "Exception" + e.getMessage());
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, "directory not created", 0, null);
				}
				String fileName = "downloadErrorLogFile"
						
						+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
				
				this.writeDataInFileDownload(errorList, fileName, filePath, userVO.getNetworkID(),
						fileName, true);
				 
				File errorFile = new File(filePath+fileName);
				byte[] fileContent = FileUtils.readFileToByteArray(errorFile);
				String encodedString = Base64.getEncoder().encodeToString(fileContent);
				response.setFileAttachment(encodedString);
				response.setFileName(fileName);
				response.setFileType("csv");
			} catch (Exception e) {
				LOG.error(METHOD_NAME, "Exception:e=" + e);
				LOG.errorTrace(METHOD_NAME, e);

			} finally {
				if (LOG.isDebugEnabled())
					LOG.debug(METHOD_NAME, "Exiting:forward=" );
			}

		}
	   public void writeDataInFileDownload(String [] errorList,String _fileName,String filePath,String _networkCode, String uploadedFileNamePath,Boolean headval) 

	    {
	    	   	final String METHOD_NAME = "writeDataInFileDownload";
	            String[] splitFileName = uploadedFileNamePath.split("/");
	            String uploadedFileName = splitFileName[(splitFileName.length)-1];
	        	if (LOG.isDebugEnabled()){
	        		LOG.debug(METHOD_NAME,"Entered: "+METHOD_NAME);
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
	                LOG.debug(METHOD_NAME,"fileName := "+fileName);
	                if(headval){
	                	fileHeader=Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");
	                }
	                else{
	                fileHeader=Constants.getProperty("ERROR_FILE_HEADER_PAYOUT");
	                }
	                newFile = new File(fileName);
	                out = new OutputStreamWriter(new FileOutputStream(newFile));
	                out.write(fileHeader +"\n");
	                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
	                String invalidLoginIdMsg = RestAPIStringParser.getMessage(locale,
	        				PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID, null);
	              for(int i=0;i<errorList.length;i++) {	
	            	  
	                		out.write(errorList[i].concat(","));

	                    	out.write(invalidLoginIdMsg.concat(","));
	                		
	                    	out.write(",");
	                	
	                	 	out.write("\n");
	                }
	    			out.write("End");
	    			
	            }
	            catch(Exception e)
	            {
	               
	            	LOG.errorTrace(METHOD_NAME, e);
	                EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"writeDataInFile[writeDataInFile]","","","","Exception:= "+e.getMessage());
	                 }
	            finally
	            {
	            	if (LOG.isDebugEnabled()){
	            		LOG.debug(METHOD_NAME,"Exiting... ");
	            	}
	                if (out!=null)
	                	try{
	                		out.close();
	                		}
	                catch(Exception e){
	                	LOG.errorTrace(METHOD_NAME, e);
	                }
	                	
	            }
	    	}

	   public static boolean uploadFileToServer(File p_formFile, byte []data, String p_dirPath, String p_contentType,  long p_fileSize) throws BTSLBaseException {
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("uploadFileToServer",
	                "Entered :p_formFile=" + p_formFile + ", p_dirPath=" + p_dirPath + ", p_contentType=" + p_contentType  + ", p_fileSize=" + p_fileSize);
	        }
	        FileOutputStream outputStream = null;
	        boolean returnValue = false;
	        final String METHOD_NAME = "uploadFileToServer";
	        // modified by Manisha(18/01/08) use singal try catch
	        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	        try {
	            final File fileDir = new File(p_dirPath);
	            if (!fileDir.isDirectory()) {
	                fileDir.mkdirs();
	            }
	            if (!fileDir.exists()) {
	                LOG.debug("uploadFileToServer", "Directory does not exist: " + fileDir + " ");
	                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", PretupsErrorCodesI.DIR_NOT_CREATED, "");
	            }

	            final File fileName = new File(p_dirPath, p_formFile.getName());
	            Path path = Paths.get( p_formFile.getName());
	            long fileSize = Files.size(path);
	            File file = new File(fileDir+ p_formFile.getName());
	            String    mimeType = mimeTypesMap.getContentType(file);
	            // if file already exist then show the error message.
	            if (p_formFile != null) {
	                if (fileSize <= 0) {
	                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_DOES_NOT_CONTAIN_DATA, "");
	                } else if (fileSize > p_fileSize) {
	                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FILE_SIZE_LARGE, 0, new String[] { String.valueOf(p_fileSize) }, "");
	                }

	                boolean contentTypeExist = false;
	                if (p_contentType.contains(",")) {
	                    final String temp[] = p_contentType.split(",");
	                    for (int i = 0, j = temp.length; i < j; i++) {
	                        if (mimeType.equalsIgnoreCase(temp[i].trim())) {
	                            contentTypeExist = true;
	                            break;
	                        }
	                    }
	                } else if (mimeType.equalsIgnoreCase(p_contentType)) {
	                    contentTypeExist = true;
	                }

	                if (contentTypeExist) {
	                    if (fileName.exists()) {
	                        throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", PretupsErrorCodesI.FILE_ALREADY_EXISTS, "");
	                    }
	                    outputStream = new FileOutputStream(fileName);
	                    outputStream.write(data);
	                    returnValue = true;
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("uploadFileToServer", "File Uploaded Successfully");
	                    }
	                }
	                // if file is not a text file show error message
	                else {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(
	                            "uploadFileToServer",
	                            "Invalid content type: " + mimeType + " required is p_contentType: " + p_contentType + " p_formFile.getFileName(): " + p_formFile
	                                .getName());
	                    }
	                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT, "");
	                }
	            }
	            // if there is no such file then show the error message
	            else {
	                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_FILE_EXIST, "");
	            }
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (Exception e) {
	            LOG.error("uploadFileToServer", "Exception " + e.getMessage());
	            LOG.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "",
	                "Exception:" + e.getMessage());
	            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, "");
	        } finally {
	        	try{
	                if (outputStream!= null){
	                	outputStream.close();
	                }
	              }
	              catch (IOException e){
	            	  LOG.error("An error occurred closing outputStream.", e);
	              }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);
	            }

	        }
	        return returnValue;
	    }


}
