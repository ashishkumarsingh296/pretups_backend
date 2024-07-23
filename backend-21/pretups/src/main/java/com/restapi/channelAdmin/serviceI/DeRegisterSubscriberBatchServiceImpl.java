package com.restapi.channelAdmin.serviceI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
//import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.logging.UnregisterSubscribersFileProcessLog;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.channelAdmin.DeRegisterSubscriberBatchVO;
import com.restapi.channelAdmin.requestVO.DeRegisterSubscriberBatchRequestVO;
import com.restapi.channelAdmin.responseVO.DeRegisterSubscriberBatchResponseVO;
import com.restapi.channelAdmin.service.DeRegisterSubscriberBatchServiceI;

@Service()
public class DeRegisterSubscriberBatchServiceImpl implements DeRegisterSubscriberBatchServiceI{

	public static final Log log = LogFactory.getLog(DeRegisterSubscriberBatchServiceImpl.class.getName());
	
	@Override
	public DeRegisterSubscriberBatchResponseVO processUploadedFileForUnRegSubscriber(HashMap<String, String> p_formFile,DeRegisterSubscriberBatchRequestVO request,HttpServletRequest requestSwag,UserVO userVO,Connection con,DeRegisterSubscriberBatchVO deRegisterSubscriberBatchVO,DeRegisterSubscriberBatchResponseVO response, HttpServletResponse responseSwag) {
		//
		//DeRegisterSubscriberBatchVO deRegisterSubscriberBatchVO = new DeRegisterSubscriberBatchVO();
		//
		final String METHOD_NAME = "processUploadedFileForUnReg";
        //ActionForward forward = null;
        //final UnregisterSubscribersForm unregisterForm = (UnregisterSubscribersForm) form;
        final String delimiter = Constants.getProperty("DelimiterForUploadedFileForUnReg");
        final String filePath = Constants.getProperty("UploadFileForUnRegPath");
        final String contentsSize = Constants.getProperty("NO_OF_CONTENTS");
        final String fileName = deRegisterSubscriberBatchVO.getFileNameStr();
        final String filePathAndFileName = filePath + fileName; // path if the
                                                                // file
        // with file name
        final SubscriberDAO subscriberDAO = new SubscriberDAO();
        final StringBuffer invalidMsisdnStr = new StringBuffer();
        final ArrayList validMsisdnList = new ArrayList();
        NetworkPrefixVO networkPrefixVO = null;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        BTSLMessages btslMessage = null;
        //Connection con = null;MComConnectionI mcomCon = null;
        File file = null;
        String invalidMsisdn;
        String tempStr = null;
        String filteredMsisdn;
        int countMsisdn = 0;
        String msisdnPrefix;
        String networkCode;
        String invalidMSISDNFromDao;
        String msisdn;
        int lineLength;
        boolean fileMoved = false;
        boolean processFile = false;
        final ArrayList fileContents = new ArrayList(); // list to store the
                                                        // contents
        												// of the file
        Locale locale = null;
        
        
        try {
        	locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
            // check the DELIMITER defined in the Constant Property file for
            // Blank
            if (BTSLUtil.isNullString(delimiter)) {
                if (log.isDebugEnabled()) {
                    log.debug("processUploadedFileForUnReg", "Delimiter not defined in Constant Property file");
                }
//                throw new BTSLBaseException(this, "processUploadedFileForUnReg", "p2p.subscriber.uploadsubscriberfileforunreg.error.delimitermissingincons",
//                    "uploadSubscriberFileForUnReg");
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DELIMETER_MISSING, PretupsI.RESPONSE_FAIL, null);
            } else {
                // check the FILEPATH defined in the Constant Property file for
                // Blank
                if (BTSLUtil.isNullString(filePath)) {
                    if (log.isDebugEnabled()) {
                        log.debug("processUploadedFileForUnReg", "File path not defined in Constant Property file");
                    }
//                    throw new BTSLBaseException(this, "processUploadedFileForUnReg", "p2p.subscriber.uploadsubscriberfileforunreg.error.filepathmissingincons",
//                        "uploadSubscriberFileForUnReg");
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.FILE_PATH_MISSING, PretupsI.RESPONSE_FAIL, null);
                } else {
                    // check the NO_OF_CONTENTS defined in the Constant Property
                    // file for Blank
                    if (BTSLUtil.isNullString(contentsSize)) {
                        if (log.isDebugEnabled()) {
                            log.debug("processUploadedFileForUnReg", "Contents size of the file not defined in Constant Property file");
                        }
//                        throw new BTSLBaseException(this, "processUploadedFileForUnReg", "p2p.subscriber.uploadsubscriberfileforunreg.error.contentssizehmissingincons",
//                            "uploadSubscriberFileForUnReg");
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CONTENT_SIZE_MISSING, PretupsI.RESPONSE_FAIL, null);
                    }
                }
            }

            StringTokenizer startparser = null;

            // take out each string from the file & put it in a array list
            if (log.isDebugEnabled()) {
                log.debug("processUploadedFileForUnReg", "Initializing the fileReader, filepath : " + filePathAndFileName);
            }
            fileReader = new FileReader("" + filePathAndFileName);
            bufferReader = new BufferedReader(fileReader);

            if (bufferReader.ready()) // If File Not Blank Read line by line
            {
                while ((tempStr = bufferReader.readLine()) != null) // read the
                // file
                // until it
                // reaches
                // to end
                {
                    tempStr = tempStr.trim();
                    if ((lineLength = tempStr.length()) == 0) // check for the
                    // blank line b/w
                    // the records of
                    // the file
                    {
//                        throw new BTSLBaseException(this, "processUploadedFileForUnReg", "p2p.subscriber.uploadsubscriberfileforunreg.error.blankline",
//                            "uploadSubscriberFileForUnReg");
                    	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.BLANK_LINE, PretupsI.RESPONSE_FAIL, null);
                    }
                    startparser = new StringTokenizer(tempStr, delimiter); // separate
                    // each
                    // string
                    // in
                    // a
                    // line
                    while (startparser.hasMoreTokens()) {
                        msisdn = startparser.nextToken().trim();
                        if (log.isDebugEnabled()) {
                            log.debug("processUploadedFileForUnReg", "Fatching the MSISDN's from the file " + msisdn);
                        }
                        fileContents.add(msisdn); // add each string in the list
                    }

                    // it can not be allowed to process the file if MSISDN's are
                    // more than the defined Limit
                    if (fileContents.size() > Integer.parseInt(contentsSize)) {
                        if (log.isDebugEnabled()) {
                            log.debug("processUploadedFileForUnReg", "File contents size of the file is not valid in constant properties file : " + fileContents.size());
                        }
//                        throw new BTSLBaseException(this, "processUploadedFileForUnReg", "p2p.subscriber.uploadsubscriberfileforunreg.error.novalidcontentssize",
//                            "uploadSubscriberFileForUnReg");
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SIZE_LIMIT_EXCEEDED, PretupsI.RESPONSE_FAIL, null);
                    }

                    startparser = null;
                    tempStr = null;
                }
            }

            //final UserVO userVO = this.getUserFormSession(request);

            // process the MSISDN's from the Array List
            int invalidmsisdnCount =0;
            int totalCount =fileContents.size();
           
            while (fileContents.size() != countMsisdn) {
                msisdn = (String) fileContents.get(countMsisdn);
                if (log.isDebugEnabled()) {
                    log.debug("processUploadedFileForUnReg", "Processing starts for MSISDN's " + msisdn);
                }
                countMsisdn++;                filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn); // before
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    if (log.isDebugEnabled()) {
                        log.debug("processUploadedFileForUnReg", "Not a valid MSISDN " + msisdn);
                    }
                    UnregisterSubscribersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdn, countMsisdn, "Not a valid MSISDN", "Fail",
                        filePathAndFileName + "," + userVO.getNetworkID());
                    invalidMsisdnStr.append(msisdn); // append the invalid
                    invalidMsisdnStr.append(delimiter);
                    
                    continue;
                }
                msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("processUploadedFileForUnReg", "Not Network prefix found " + msisdn);
                    }
                    UnregisterSubscribersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdn, countMsisdn, "Not Network prefix found", "Fail",
                        filePathAndFileName + "," + userVO.getNetworkID());
                    invalidMsisdnStr.append(msisdn);
                    invalidMsisdnStr.append(delimiter);
                    
                    continue;
                }
                networkCode = networkPrefixVO.getNetworkCode();
                if (!networkCode.equals(userVO.getNetworkID())) {
                    if (log.isDebugEnabled()) {
                        log.debug("processUploadedFileForUnReg", "Not supporting Network" + msisdn);
                    }
                    UnregisterSubscribersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdn, countMsisdn, "Not supporting Network", "Fail",
                        filePathAndFileName + "," + userVO.getNetworkID());
                    invalidMsisdnStr.append(msisdn);
                    invalidMsisdnStr.append(delimiter);
                    
                    continue;
                }
                validMsisdnList.add(new ListValueVO(networkPrefixVO.getSeriesType(), filteredMsisdn));
                /** Added to push messages of De-registering - subscriber starts **/
                //final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                final BTSLMessages btslMessageForPush = new BTSLMessages(PretupsErrorCodesI.P2P_DEREGISTERATION_SUCCESS);
                final PushMessage pushMessage = new PushMessage(filteredMsisdn, btslMessageForPush, "","", locale, networkPrefixVO.getNetworkCode());
                pushMessage.push();
            }
            if (validMsisdnList == null || validMsisdnList.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("processUploadedFileForUnReg", "No valid MSISDN in the file, size :" + validMsisdnList.size());
                }
//                throw new BTSLBaseException(this, "processUploadedFileForUnReg", "p2p.subscriber.uploadsubscriberfileforunreg.message.nomsisdn",
//                    "uploadSubscriberFileForUnReg");
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_MOBILE_NUMBER, PretupsI.RESPONSE_FAIL, null);
            }

            //mcomCon = new MComConnection();con=mcomCon.getConnection();
            invalidMSISDNFromDao = subscriberDAO.deleteSubscriberBulk(con, validMsisdnList);// call
            invalidMsisdnStr.append(invalidMSISDNFromDao);// insert the invalid
            bufferReader.close();
            fileReader.close();

            fileMoved = this.moveFileToArchive(filePathAndFileName, fileName);
            if (fileMoved) {
                processFile = true;
            } else {

            	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.FILE_CANNOT_BE_MOVED, PretupsI.RESPONSE_FAIL, null);
            }

            if (invalidMsisdnStr!=null  && invalidMsisdnStr.length() == 0 ) {
                response.setMessageCode(PretupsI.DEREGISTER_SUCCESS);
    			response.setMessage(PretupsRestUtil.getMessageString(PretupsI.DEREGISTER_SUCCESS));
    			responseSwag.setStatus(HttpStatus.SC_OK);
    			response.setStatus(HttpStatus.SC_OK);
            } else {
                invalidMsisdn = invalidMsisdnStr.toString().substring(0, (invalidMsisdnStr.toString().length()) - 1);
                if(invalidMsisdn!=null  && invalidMsisdn.length()>0 ) {
                	String invlidvalidmsisdn[] = invalidMsisdn.split(",");
                	invalidmsisdnCount=invlidvalidmsisdn.length;
                	
                }
                
                String arr[] = new String[1];
                arr[0] = invalidMsisdn.toString();
                String error=null;
                String messageCode =null;
                 if(invalidmsisdnCount ==totalCount) {
                	 error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DEREGISTRATION_SUBSCRIBER_BULK,new String[] { arr[0] });
                	 messageCode=PretupsErrorCodesI.DEREGISTRATION_SUBSCRIBER_BULK;
                	 response.setMessageCode(messageCode);
                	 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
         	   		response.setStatus(HttpStatus.SC_BAD_REQUEST);
                 }else if(invalidmsisdnCount >0 && invalidmsisdnCount<=totalCount) {
                	  error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DEREGISTRATION_SUBSCRIBER_BULK_PARTIAL_MESSAGE,new String[] { arr[0] });
                	  messageCode= PretupsErrorCodesI.DEREGISTRATION_SUBSCRIBER_BULK_PARTIAL_MESSAGE;
                	  response.setMessageCode(messageCode);
                	  responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
          	   		response.setStatus(HttpStatus.SC_ACCEPTED);
                 }
                
                String filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
                C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
                String filePathConstemp = filePathCons + "temp/";        
    			c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
    			
    			String filepathtemp = filePathConstemp ;   

    			String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
    			writeFileTXT(error, filepathtemp + logErrorFilename + ".txt");
                
    			File error1 =new File(filepathtemp+logErrorFilename+ ".txt");
    			byte[] fileContent = FileUtils.readFileToByteArray(error1);
    			String encodedString = Base64.getEncoder().encodeToString(fileContent);
    	   		response.setFileAttachment(encodedString);
    	   		response.setFileName(logErrorFilename+".txt");
    	   	response.setMessage(error);
    	   		
           
            }
            
        } 
        catch(BTSLBaseException be) {
			log.error(METHOD_NAME, "Exceptin:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				//responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
        catch (Exception e) {
//            log.error("processUploadedFileForUnReg", "Exceptin:e=" + e);
//            log.errorTrace(METHOD_NAME, e);
//            file = new File(filePath, fileName);
//            file.delete();
//            log.debug("processUploadedFileForUnReg", "Exit:forward=" + forward);
//            return super.handleError(this, "processUploadedFileForUnReg", e, request, mapping, con);
        	e.printStackTrace();
        } finally {
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e1) {
                bufferReader = null;
                log.errorTrace(METHOD_NAME, e1);
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e1) {
                fileReader = null;
                log.errorTrace(METHOD_NAME, e1);
            }
//			if (mcomCon != null) {
//				mcomCon.close("UnregisterSubscribersAction#processUploadedFileForUnReg");
//				mcomCon = null;
//			}
           // if (log.isDebugEnabled()) {
            //    log.debug("processUploadedFileForUnReg", "Exit:forward=" + forward);
           // }
        }
		
		return response;	
	}
	
	
	
	
	
	
	private boolean moveFileToArchive(String p_filePathAndFileName, String p_fileName) {
        if (log.isDebugEnabled()) {
            log.debug("moveFileToArchive", " Entered ");
        }
        final File fileRead = new File(p_filePathAndFileName);
        File fileArchive = new File("" + Constants.getProperty("ArchiveFilePathForUnReg"));
        if (!fileArchive.isDirectory()) {
            fileArchive.mkdirs();
        }
        fileArchive = new File("" + Constants.getProperty("ArchiveFilePathForUnReg") + p_fileName + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
        // make
        // the
        // new
        // file
        // name
        final boolean flag = fileRead.renameTo(fileArchive);
        if (log.isDebugEnabled()) {
            log.debug("moveFileToArchive", " Exiting File Moved=" + flag);
        }
        return flag;
    }
	
	
	private void writeFileTXT(String error, String excelFilePath) throws IOException {
  	  final String methodName="writeFileTXT";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered:=" + methodName);
			}
        try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
                csvWriter.write(error);
                csvWriter.flush();
                csvWriter.close();
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exit:=" + methodName);
                }
            }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited:=" + methodName);
        }

  }
	
}
