package com.restapi.o2c.service;

import java.io.File;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.web.pretups.channel.transfer.businesslogic.FOCBatchTransferWebDAO;
import org.apache.http.HttpStatus;
//import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListServiceImpl;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchO2CFileProcessLog;
import com.btsl.pretups.logging.BatchO2CProcessLog;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebDAO;
import com.web.pretups.channel.transfer.web.FOCBatchForm;
import com.web.pretups.channel.transfer.web.O2CBatchWithdrawForm;
@Service("O2CBatchProcessServiceI")
public class O2CBatchProcessServiceImpl implements O2CBatchProcessServiceI {
	
	protected final Log log = LogFactory.getLog(getClass().getName());
	private String filepathtemp;
	private O2CBatchWithdrawFileRequest request;
	O2CBatchWithdrawFileResponse response = null;
	 boolean processRunning = true;
	ProcessStatusVO processVO = null;
	C2CBatchItemsVO c2cBatchItemVO = null;
	 long batchDetailID = 0;
	 int errorSize=0;
	 ArrayList<MasterErrorList> inputValidations=null;
	 boolean fileExist=false;
	 @Override 
public O2CBatchWithdrawFileResponse processRequest(
		String serviceTypee,O2CBatchWithdrawFileRequest o2CFileUploadApiRequest,String msisdn,OperatorUtilI calculatorI,Locale locale,Connection con,String requestType,String batchID, String serviceType,
		String requestIDStr, HttpServletRequest httprequest,
		MultiValueMap<String, String> headers,
		HttpServletResponse responseSwag) throws BTSLBaseException {

	final String methodName = "processRequest";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered ");
	}

	String errorMessage=null;
	String errorMessageCode=null;
	int status;
	errorSize=0;
	processRunning = true;
    fileExist=false;
	this.request = o2CFileUploadApiRequest;
	LinkedHashMap<String, List<String>> bulkDataMap ;
	
	try {
		
		response = new O2CBatchWithdrawFileResponse();
		O2CBatchWithdrawForm theForm = new O2CBatchWithdrawForm();//tight coupling
		//loose coupling
		
		//basic form validation at api level
		UserVO userVO = new UserDAO().loadUsersDetails(con, msisdn);
    	inputValidations = new ArrayList<>();
    	ChannelUserVO channelUserVO=new ChannelUserVO();
		 
		channelUserVO = (ChannelUserVO) userVO;
			UserPhoneVO userPhoneVO = new UserDAO().loadUserPhoneVO(con,  userVO.getUserID());
			channelUserVO.setUserPhoneVO(userPhoneVO);
		/*if(PretupsI.YES.equals(userVO.getCategoryVO().getSmsInterfaceAllowed())&& PretupsI.YES.equals(userPhoneVO.getPinRequired()))
		{
            if(BTSLUtil.isNullString(request.getPin()))
            {
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN,null);
				response.setStatus("400");
				  response.setService("o2cBatchTransferApprovalResp");
				  response.setMessage(msg);
				  response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
				 return response;
            }
            else
            	{
            	try {
				ChannelUserBL.validatePIN(con, channelUserVO,request.getPin());
			} catch (BTSLBaseException be) {
				if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
						|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
					OracleUtil.commit(con);
				}
				String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
				response.setStatus("400");
				  response.setService("o2cBatchTransferApprovalResp");
				  response.setMessage(msg);
				  response.setMessageCode(be.getMessageKey());
				  return response;
			}
            	}
		
		}
		*/
		 
	
		 requestValidation();
		
		 /*
			 * request input validation
			 */

			//throwing list of basic form errors
			 if(!BTSLUtil.isNullOrEmptyList(inputValidations)) {
			  response.setStatus("400");
			  response.setService("o2cBatchTransferApprovalResp");
			  response.setErrorMap(new ErrorMap());
			  response.getErrorMap().setMasterErrorList(inputValidations);
			  return response;
			 }

	      
	        if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO.getOutSuspened())) {
	            if (log.isDebugEnabled()) {
	                log.debug("userSearch", "USER IS OUT SUSPENDED IN THE SYSTEM");
	            }

				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OUT_SUSPENDED, PretupsI.RESPONSE_FAIL, null);

	        }
	       
		 theForm.setRequestType(requestType);
		 theForm.setBatchId(batchID);
		 theForm.setDefaultLang(o2CFileUploadApiRequest.getLanguage1());
		 theForm.setSecondLang(o2CFileUploadApiRequest.getLanguage2());
		/*
		 * Uploading and validating file
		 */
	   	//code for read file content
			ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
			ErrorMap errorMap = new ErrorMap();
			HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_TYPE1, o2CFileUploadApiRequest.getFileType());
			fileDetailsMap.put(PretupsI.FILE_NAME,o2CFileUploadApiRequest.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, o2CFileUploadApiRequest.getFileAttachment());
			fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, "o2cBatchWithdraw");
			ArrayList batchItemsList = new ArrayList();
			// Check external txn id for domain type
			final String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORO2C;

            if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("3") != -1) {
                theForm.setExternalTxnMandatory(PretupsI.YES);
            }
            O2CBatchMasterVO o2cBatchMasterVO = new O2CBatchMasterVO();
            FOCBatchMasterVO focBatchMasterVO = new FOCBatchMasterVO();
            final HashMap approveRejectMap = new HashMap();
            // holds focBatchMasterVO for order close
            final HashMap closedOrderMap = new HashMap();
            
            final LinkedHashMap approveRejectMap1 = new LinkedHashMap();
            // holds focBatchMasterVO for order close
            final LinkedHashMap closedOrderMap1 = new LinkedHashMap();
            if(serviceTypee.equals("W"))
            {
            	int indexToBeReduced = 0;
            
            if ("approval1".equals(theForm.getRequestType())) {
                indexToBeReduced = 2;
            }
            if ("approval2".equals(theForm.getRequestType())) {
            }
            if ("approval3".equals(theForm.getRequestType())) {
            }
            String statusUsed = null;
            if ("approval1".equals(theForm.getRequestType())) {
                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
            }
            if ("approval2".equals(theForm.getRequestType())) {
                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
            }
            if ("approval3".equals(theForm.getRequestType())) {
                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
            }
            final O2CBatchWithdrawWebDAO o2cBatchTransferWebDAO = new O2CBatchWithdrawWebDAO();
            theForm.setO2cOrderApprovalLevel(((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.FOC_ORDER_APPROVAL_LVL)).intValue());
           // Map hashMap = o2cBatchTransferWebDAO.loadBatchItemsMap(con, theForm.getBatchId(), statusUsed);  // Earlier code  hashmap has O2CBatchItemsVO
          //changed because ,  call at line Linec 739, processOrderbybatch -> sending O2CBatchItemsVO  to   O2cBatchWitdhrawDAO Line 4869   This line expecting  BatchO2CItemsVO ,
            // classcast exception.
            Map hashMap = o2cBatchTransferWebDAO.loadBatchItemsMapBatchO2CItemsVO(con, theForm.getBatchId(), statusUsed);  //Earlier method changed loadBatchItemsMap();
            
            if ("approval1".equals(theForm.getRequestType())) {
                final String statusUsed1 = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";

                // load the batch details from the database
                final ArrayList o2cBatchMasterVOList = o2cBatchTransferWebDAO.loadBatchO2CMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
                    PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                theForm.setO2cBatchMasterVOList(o2cBatchMasterVOList);
                
                for(int i=0;i<theForm.getO2cBatchMasterVOList().size();i++)
                {
                	String id=((FOCBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i)).getBatchId();
                	if(id.equals(batchID))
                	{
                		focBatchMasterVO=(FOCBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i);
                		break;
                	}
                		
                }
                }
                else if("approval2".equals(theForm.getRequestType()))
                {
                	final String statusUsed1 = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";

                    // load the batch details from the database
                    final ArrayList focBatchMasterVOList = o2cBatchTransferWebDAO.loadBatchO2CMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
                        PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    theForm.setO2cBatchMasterVOList(focBatchMasterVOList);
                    for(int i=0;i<theForm.getO2cBatchMasterVOList().size();i++)
                    {
                    	String id=((FOCBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i)).getBatchId();
                    	if(id.equals(batchID))
                    	{
                    		focBatchMasterVO=(FOCBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i);
                    		break;
                    	}
                    		
                    }
                }
                else {
                	final String statusUsed1 = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
                     // load the batch details from the database
                     final ArrayList focBatchMasterVOList = o2cBatchTransferWebDAO.loadBatchO2CMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
                         PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
                     theForm.setO2cBatchMasterVOList(focBatchMasterVOList);
                     for(int i=0;i<theForm.getO2cBatchMasterVOList().size();i++)
                     {
                     	String id=((FOCBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i)).getBatchId();
                     	if(id.equals(batchID))
                     	{
                     		focBatchMasterVO=(FOCBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i);
                     		break;
                     	}
                     		
                     }
                }
			bulkDataMap = fileUtil.uploadAndReadGenericFileO2CBatchApproval(fileDetailsMap, 0, errorMap,closedOrderMap,approveRejectMap,externalTxnMandatory, batchItemsList,hashMap,indexToBeReduced,theForm);
            }
            else
            {
            	int indexToBeReduced = 0;
                if ("approval1".equals(theForm.getRequestType())) {
                	indexToBeReduced = 3;
                }
                if ("approval2".equals(theForm.getRequestType())) {
                	indexToBeReduced = 3;
                }
                /*if ("approval3".equals(theForm.getRequestType())) {
                }*/
                String statusUsed = null;
                if ("approval1".equals(theForm.getRequestType())) {
                    statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
                }
                if ("approval2".equals(theForm.getRequestType())) {
                    statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
                }
                /*if ("approval3".equals(theForm.getRequestType())) {
                    statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
                }*/
                
                final BatchO2CTransferWebDAO batchO2CTransferwebDAO = new BatchO2CTransferWebDAO();
                theForm.setO2cOrderApprovalLevel(((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.O2C_ORDER_APPROVAL_LVL)).intValue());
                LinkedHashMap hashMap = batchO2CTransferwebDAO.loadBatchO2CItemsMap(con, theForm.getBatchId(), statusUsed, PretupsI.TRANSFER_TYPE_O2C,
                        PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                if ("approval1".equals(theForm.getRequestType())) {
                	final String statusUsed1 = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
                    
                    // load the batch details from the database
                    final ArrayList o2cBatchMasterVOList = batchO2CTransferwebDAO.loadO2CBatchMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
                            PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, PretupsI.TRANSFER_TYPE_O2C, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                        
                    theForm.setO2cBatchMasterVOList(o2cBatchMasterVOList);
                    
                    for(int i=0;i<theForm.getO2cBatchMasterVOList().size();i++)
                    {
                    	String id=((O2CBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i)).getBatchId();
                    	if(id.equals(batchID))
                    	{
                    		o2cBatchMasterVO=(O2CBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i);
                    		break;
                    	}
                    		
                    }
                    }
                    else if("approval2".equals(theForm.getRequestType()))
                    {
                    	final String statusUsed1 = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "'";
                        
                        // load the batch details from the database
                    	final ArrayList o2cBatchMasterVOList = batchO2CTransferwebDAO.loadO2CBatchMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
                                PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2, PretupsI.TRANSFER_TYPE_O2C, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                            
                        theForm.setO2cBatchMasterVOList(o2cBatchMasterVOList);
                        for(int i=0;i<theForm.getO2cBatchMasterVOList().size();i++)
                        {
                        	String id=((O2CBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i)).getBatchId();
                        	if(id.equals(batchID))
                        	{
                        		o2cBatchMasterVO=(O2CBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i);
                        		break;
                        	}
                        		
                        }
                    }
                    /*else {
                    	final String statusUsed1 = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
                         // load the batch details from the database
                         final ArrayList focBatchMasterVOList = o2cBatchTransferWebDAO.loadBatchO2CMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
                             PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
                         theForm.setO2cBatchMasterVOList(focBatchMasterVOList);
                         for(int i=0;i<theForm.getO2cBatchMasterVOList().size();i++)
                         {
                         	String id=((FOCBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i)).getBatchId();
                         	if(id.equals(batchID))
                         	{
                         		focBatchMasterVO=(FOCBatchMasterVO)theForm.getO2cBatchMasterVOList().get(i);
                         		break;
                         	}
                         		
                         }
                    }*/
    			bulkDataMap = fileUtil.uploadAndReadGenericFileO2CBatchWithdrawApproval(con,o2cBatchMasterVO,fileDetailsMap, 0, errorMap,closedOrderMap1,approveRejectMap1,externalTxnMandatory, batchItemsList,hashMap,indexToBeReduced,theForm);
    			
            }
			LinkedList<String> res = (LinkedList<String>) bulkDataMap.get("isValidRow");
			
			String error = res.get(0);
			int blankCount = Integer.valueOf(res.get(1));
			int cancelCount = Integer.valueOf(res.get(2));
			int discardCount = Integer.valueOf(res.get(3));
			int size = Integer.valueOf(res.get(4));
			/*
			 * ArrayList fileContents = getFileContentsList(bulkDataMap); int records =
			 * fileContents.size();
			 */
			
			//response.setNumberOfRecords(totalRecords);
			// /
            // If file does not contain record as entered by the user then
            // show the error message
            // (records excludes blank lines)
            // /
			ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
			ErrorFileResponse errorFileResponse =  new ErrorFileResponse();
			 if (error.equals("true")) {
				 errorFileRequestVO.setRowErrorMsgLists(errorMap.getRowErrorMsgLists());
				 errorFileRequestVO.setFile(o2CFileUploadApiRequest.getFileAttachment());
				 errorFileRequestVO.setFiletype(o2CFileUploadApiRequest.getFileType());
				 errorFileRequestVO.setPartialFailure(false);
				 new DownloadUserListServiceImpl().downloadErrorFile(errorFileRequestVO, errorFileResponse, responseSwag);
				 response.setFileAttachment(errorFileResponse.getFileAttachment());
				 response.setFileName(errorFileResponse.getFileName());
				 response.setErrorMap(errorMap);
					//writeToFileError(response);
		             response.setStatus("400");
		             response.setMessage("Some Records contain error.Kindly correct them");
		             response.setService("o2cBatchTransferApprovalResp");
		             responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		             return response;
		         }
			else
			{
				List<String> filePath= bulkDataMap.get("filepathtemp");
				String filePaths=filePath.get(0);
				filepathtemp=filePaths;
				if(serviceTypee.equals("W"))
				processUploadedFile(con,theForm,focBatchMasterVO,userVO,size,blankCount,cancelCount,discardCount,approveRejectMap,closedOrderMap, httprequest, batchItemsList,responseSwag);
				else
				{
					processUploadedFileTransfer(con,theForm,o2cBatchMasterVO,userVO,size,blankCount,cancelCount,discardCount,approveRejectMap1,closedOrderMap1, httprequest, batchItemsList,responseSwag);
					
				}
				if(BTSLUtil.isNullString(response.getMessage()))
				{
				response.setStatus("200");
				response.setMessageCode(errorMessageCode);
				response.setMessage("BATCH "+theForm.getBatchId()+" generated successfully.All Records processed");
				response.setService("o2cBatchTransferApprovalResp");
				}
			}
      
	} catch (BTSLBaseException be) {
		if(!fileExist)
		filedelete();
		log.error(methodName, "Exception:e=" + be);
		log.errorTrace(methodName, be);
		String [] args=null;
		if(!BTSLUtil.isNullorEmpty(be.getArgs())) {
			args = be.getArgs();
		}
		if(!BTSLUtil.isNullorEmpty(be.getMessageKey())) {
			errorMessage = RestAPIStringParser.getMessage(locale,
					be.getMessageKey(), args);
			errorMessageCode = be.getMessageKey();

			
		}
		if(!BTSLUtil.isNullorEmpty(be.getErrorCode()) &&  be.getErrorCode() !=0) {
			status = be.getErrorCode();
			} else {
				status= 400;
			}
	response.setStatus(String.valueOf(status));
	response.setMessageCode(errorMessageCode);
	response.setMessage(errorMessage);
	response.setService("o2cBatchTransferApprovalResp");
	
	} catch (Exception e) {
		log.error(methodName, "Exceptin:e=" + e);
		log.errorTrace(methodName, e);
		response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
		response.setMessageCode("error.general.processing");
		response.setMessage("Check File Type supplied.");
		responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		 
	} finally {
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			log.errorTrace(methodName, e);
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, " Exited ");
		}
	}
	// log.debug(methodName, response);
	return response;

}

private void filedelete() {
	if(!BTSLUtil.isNullString(filepathtemp))
	{File file = new File(filepathtemp);
	if (file.delete()) {
		log.debug("filedelete", "******** Method uploadAndProcessFile :: Got exception and deleted the file");
	}
	}
}

public void validateFilePathCons(String filePathCons) throws BTSLBaseException {
	if (BTSLUtil.isNullorEmpty(filePathCons)) {
		 throw new BTSLBaseException(this, "validateFilePathCons", PretupsErrorCodesI.EMPTY_FILE_PATH_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
	}
}

/*private void writeToFileError(O2CBatchWithdrawFileResponse response) throws BTSLBaseException, IOException {
	List<List<String>> rows = new ArrayList<>();
	for(int i=0;i<response.getErrorMap().getRowErrorMsgLists().size();i++)
	{
		RowErrorMsgLists rowErrorMsgList = response.getErrorMap().getRowErrorMsgLists().get(i);
		for(int i1=0;i1<rowErrorMsgList.getMasterErrorList().size();i1++)
		{
			MasterErrorList masterErrorList=rowErrorMsgList.getMasterErrorList().get(i1);
		    rows.add(( Arrays.asList(rowErrorMsgList.getRowValue(), rowErrorMsgList.getRowName(), masterErrorList.getErrorMsg())));
		    
		}
		rowErrorMsgList.setRowErrorMsgList(null);//done to remove rowerrormsglist deliberately in response
	}
	String filePathCons = Constants.getProperty("ErrorBatchO2CUserListFilePath");
	validateFilePathCons(filePathCons);
	
	String filePathConstemp = filePathCons + "temp/";        
	createDirectory(filePathConstemp);
	

	filepathtemp = filePathConstemp ;   

	String logErrorFilename = "Errorlog_" + (System.currentTimeMillis()); 
	//writeExcel(rows,filepathtemp+logErrorFilename+ ".xls");
	writeCSV(rows,filepathtemp+logErrorFilename+ ".csv");
	File error =new File(filepathtemp+logErrorFilename+ ".csv");
	byte[] fileContent = FileUtils.readFileToByteArray(error);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		response.setFileAttachment(encodedString);
		response.setFileName(logErrorFilename+".csv");
		
}
*/
public void createDirectory(String filePathConstemp) throws BTSLBaseException {

	String methodName = "createDirectory";
	File fileTempDir = new File(filePathConstemp);
	if (!fileTempDir.isDirectory()) {
		fileTempDir.mkdirs();
	}
	if (!fileTempDir.exists()) {
		log.debug("Directory does not exist : ", fileTempDir);
		throw new BTSLBaseException("OAuthenticationUtil", methodName,
				PretupsErrorCodesI.BATCH_UPLOAD_DIRECTORY_DO_NOT_EXISTS, PretupsI.RESPONSE_FAIL, null); // provide
																										// your own
	}
}
public void writeCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
	try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
	csvWriter.append("Line number");
	csvWriter.append(Constants.getProperty("FILE_SEPARATOR_O2C"));
	csvWriter.append("Mobile number/LoginId");
	csvWriter.append(Constants.getProperty("FILE_SEPARATOR_O2C"));
	csvWriter.append("Reason");
	csvWriter.append("\n");

	for (List<String> rowData : listBook) {
	    csvWriter.append(String.join(Constants.getProperty("FILE_SEPARATOR_O2C"), rowData));
	    csvWriter.append("\n");
	}
	}
}

//Validation for request input for file upload
private boolean requestValidation() throws BTSLBaseException {
	boolean isValid = true;

	if (BTSLUtil.isNullorEmpty(request.getFileName())) {
		MasterErrorList masterErrorList = new MasterErrorList();
		masterErrorList.setErrorMsg("File name is empty.");
		masterErrorList.setErrorCode("");
		inputValidations.add(masterErrorList);
		isValid = false ;
	}
	if (BTSLUtil.isNullorEmpty(request.getFileAttachment())) {
		MasterErrorList masterErrorList = new MasterErrorList();
		masterErrorList.setErrorMsg("File attachment is empty.");
		masterErrorList.setErrorCode("");
		inputValidations.add(masterErrorList);
		isValid = false ;
	}
	if (BTSLUtil.isNullorEmpty(request.getFileType())) {
		MasterErrorList masterErrorList = new MasterErrorList();
		masterErrorList.setErrorMsg("File type is empty.");
		masterErrorList.setErrorCode("");
		inputValidations.add(masterErrorList);
		isValid = false ;
	}
	if (BTSLUtil.isNullorEmpty(request.getBatchName())) {
		MasterErrorList masterErrorList = new MasterErrorList();
		masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				PretupsErrorCodesI.BATCH_NAME_EMPTY, null));
		masterErrorList.setErrorCode(PretupsErrorCodesI.BATCH_NAME_EMPTY);
		inputValidations.add(masterErrorList);
		isValid = false ;
	}
	if (!BTSLUtil.isNullorEmpty(request.getLanguage2())&& request.getLanguage2().length()>30) {
		MasterErrorList masterErrorList = new MasterErrorList();
		masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				PretupsErrorCodesI.LANGUAGE2_LENGTH, null));
		masterErrorList.setErrorCode(PretupsErrorCodesI.LANGUAGE2_LENGTH);
		inputValidations.add(masterErrorList);
		isValid = false ;
	}
	if (!BTSLUtil.isNullorEmpty(request.getLanguage1())&& request.getLanguage1().length()>30) {
		MasterErrorList masterErrorList = new MasterErrorList();
		masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				PretupsErrorCodesI.LANGUAGE1_LENGTH, null));
		masterErrorList.setErrorCode(PretupsErrorCodesI.LANGUAGE1_LENGTH);
		inputValidations.add(masterErrorList);
		isValid = false ;
	}
	return isValid;

}

private O2CBatchWithdrawFileResponse processUploadedFile(Connection con,O2CBatchWithdrawForm theForm,FOCBatchMasterVO focBatchMasterVO ,UserVO userVOreq,int size,int blankCount,int cancelCounts,int discardCounts,HashMap approveRejectMap,HashMap closedOrderMap,HttpServletRequest request,ArrayList batchItemsList,HttpServletResponse responseSwag) {
    final String METHOD_NAME = "processUploadedFile";
    if (log.isDebugEnabled()) {
        log.debug("processFile", "Entered");
    }
    // contains forward path
    //ActionForward forward = null;
    // path where file will be uploaded on server
    // Name of file to be uploaded on server
    // object of formbean

    String msgArr[] = null;
    ProcessStatusVO processVO = null;
    boolean processRunning = true;
    try {


        // as to check the status of the batch foc process into the table so
        // that only
        // one instance should be executed for batch foc
        final ProcessBL processBL = new ProcessBL();
        final UserVO userVO = userVOreq;
        final ChannelUserVO channelUserVO = (ChannelUserVO) userVOreq;
        ListValueVO errorVO = null;
        ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
		ErrorFileResponse errorFileResponse =  new ErrorFileResponse();
        ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal = new ArrayList<>();
        try {
            processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.O2C_BATCH_PROCESS_ID,userVO.getNetworkID());
        } catch (BTSLBaseException e) {
            log.error("processFile", "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            processRunning = false;
            throw new BTSLBaseException(this, "processFile", "batcho2c.processfile.error.alreadyexecution");
        }
        if (processVO != null && !processVO.isStatusOkBool()) {
            processRunning = false;
            throw new BTSLBaseException(this, "processFile", "batcho2c.processfile.error.alreadyexecution");
        }
        con.commit();
        processVO.setNetworkCode(userVO.getNetworkID());
        // ends here

        // contains the error list in file validation occur
        // contains error list when error is found when approve or reject
        // the record
        ArrayList approveRejectErrorList = null;
        // contains error list when record is closed
        ArrayList closeErrorList = null;
        // this will hold the information aboput the
        // error()CodeName=MSISDN,otherInfo=Record number,
        // OtherInfo2=message to be dispayed)
        // true if file validation error exists
        // Holds the external txn number(will be used to check the unique at
        // the time of file validation)
        // counts the discard count
        
        // counts the blanck line
        
        // If file validation not exists the processd the records
         {
        	 final HashMap<String, String> map = new HashMap<String, String>();
            final O2CBatchWithdrawWebDAO o2cBatchTransferWebDAO = new O2CBatchWithdrawWebDAO();

            boolean isModified = false;
            if ((closedOrderMap != null && !closedOrderMap.isEmpty()) || (approveRejectMap != null && !approveRejectMap.isEmpty())) { // check
                // the
                // batch
                // is
                // modified
                // or
                // not
                isModified = o2cBatchTransferWebDAO.isBatchModified(con, focBatchMasterVO.getModifiedOn().getTime(), theForm.getBatchId());
                if (isModified) {
                    //BTSLMessages btslMessage = null;
                    //btslMessage = new BTSLMessages("batcho2c.batcho2capprovereject.msg.batchmodify", "firstPage");
                    //forward = super.handleMessage(btslMessage, request, mapping);
                    response.setStatus("206");
    				response.setMessageCode("");
    				response.setMessage("Batch is already modified by someone else, please reload the data");
    				response.setService("o2cBatchTransferApprovalResp");
                } else {
                    String currentLevel = null;
                    if ("approval1".equals(theForm.getRequestType())) {
                        currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
                    } else if ("approval2".equals(theForm.getRequestType())) {
                        currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
                    } else if ("approval3".equals(theForm.getRequestType())) {
                        currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
                    }

                    if (closedOrderMap != null && !closedOrderMap.isEmpty()) {
                        // If closedOrderMap is not null then closed the
                        // orders in closedOrderMap
                        final int updateCount = o2cBatchTransferWebDAO.updateBatchStatus(con, theForm.getBatchId(),
                            PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);

                        if (updateCount <= 0) {
                        	con.rollback();
                            throw new BTSLBaseException(this, "processFile", "error.general.processing", "firstPage");
                        }
                        con.commit();
                        closeErrorList = o2cBatchTransferWebDAO.o2cBatchCloseRest(con, focBatchMasterVO, closedOrderMap, theForm.getBatchId(), 
                        		new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), currentLevel, channelUserVO.getUserID());

                    }// If approveRejectMap is not null then approve or
                     // reject the order
                    if (approveRejectMap != null && !approveRejectMap.isEmpty()) {
                        // If closedOrderMap is not null then closed the
                        // orders in closedOrderMap
                        final int updateCount = o2cBatchTransferWebDAO.updateBatchStatus(con, theForm.getBatchId(),
                            PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);

                        if (updateCount < 0) {
                        	con.rollback();
                            throw new BTSLBaseException(this, "processFile", "error.general.processing", "firstPage");
                        }
                        con.commit();
          
                        approveRejectErrorList = o2cBatchTransferWebDAO.processOrderByBatch(con, approveRejectMap, currentLevel, channelUserVO.getUserID(),
                        		new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), theForm.getDefaultLang(), theForm
                                .getSecondLang());
                    }
                    // two error list may be there. If two are there then
                    // merge and sort them
                    if (approveRejectErrorList != null && !approveRejectErrorList.isEmpty()) {
                        theForm.setErrorList(approveRejectErrorList);
                    }
                    if (closeErrorList != null && !closeErrorList.isEmpty()) {
                        if (approveRejectErrorList != null && !approveRejectErrorList.isEmpty()) {
                            approveRejectErrorList.addAll(closeErrorList);
                            theForm.setErrorList(approveRejectErrorList);
                        } else {
                            theForm.setErrorList(closeErrorList);
                        }
                        Collections.sort(theForm.getErrorList());
                    }
                    msgArr=new String[3];
                    // success message
                    if (theForm.getErrorList() == null || theForm.getErrorList().isEmpty()) {
                    	int totalSize=closedOrderMap.size()+approveRejectMap.size()+discardCounts;
                        int processed = approveRejectMap.size();
                        int approved=approveRejectMap.size()-cancelCounts;
                        int canceled=cancelCounts;
                        response.setStatus("200");
        				response.setMessageCode("");
        				if (closedOrderMap != null && !closedOrderMap.isEmpty()) {
        					approved=closedOrderMap.size();
        				 }
        				response.setMessage(approved+" records approved, "+canceled+" rejected");                        response.setBatchID(theForm.getBatchId());
        				response.setService("o2cBatchTransferApprovalResp");

                    } else if (theForm.getErrorList().size() + discardCounts == size - 1) {
                        theForm.setProcessedRecs(String.valueOf(theForm.getErrorList().size()));
                        msgArr[0] = String.valueOf(discardCounts);
                        theForm.setNoOfRecords(String.valueOf((size - discardCounts - 1 - blankCount)));
                        //btslMessage = new BTSLMessages("batcho2c.batcho2capprove.msg.bachprocessfilefailedall", msgArr, "processFileBatchO2CApprovel");
                        theForm.setViewErrorLog("Y");
                        MasterErrorList masterErrorList = new MasterErrorList();
                    	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                    	
                    	for (int i = 0; i < theForm.getErrorList().size(); i++) {
                        	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                        	errorVO = (ListValueVO) theForm.getErrorList().get(i);
                        	rowErrorMsgLists.setRowName(errorVO.getCodeName());
        					rowErrorMsgLists.setRowValue("Line "+errorVO.getOtherInfo());
        					masterErrorList.setErrorMsg(errorVO.getOtherInfo2());
        					masterErrorLists.add(masterErrorList);
        					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
        					rowErrorMsgListsFinal.add(rowErrorMsgLists);
                            
                            errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                            theForm.getErrorList().set(i, errorVO);
                        }
                        ErrorMap p= new ErrorMap();
                    	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
                    	response.setErrorMap(p);
                    	//writeToFileError(response);
                    	 errorFileRequestVO.setRowErrorMsgLists(p.getRowErrorMsgLists());
        				 errorFileRequestVO.setFile(this.request.getFileAttachment());
        				 errorFileRequestVO.setFiletype(this.request.getFileType());
        				 errorFileRequestVO.setPartialFailure(false);
        				 new DownloadUserListServiceImpl().downloadErrorFile(errorFileRequestVO, errorFileResponse, responseSwag);
        				 response.setFileAttachment(errorFileResponse.getFileAttachment());
        				 response.setFileName(errorFileResponse.getFileName());
        				 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                    	 response.setStatus("400");
        				response.setMessageCode("");
        				response.setMessage("No valid record in file for processing,"+msgArr[0]+" records discarded.");
        				response.setService("o2cBatchTransferApprovalResp");
                    }
                    // success message with showing invalid MSISDN list
                    else {
                        theForm.setProcessedRecs(String.valueOf(theForm.getErrorList().size()));
                        theForm.setNoOfRecords(String.valueOf((size - discardCounts - 1 - blankCount)));
                        msgArr[0] = String.valueOf(closedOrderMap.size() + approveRejectMap.size() - theForm.getErrorList().size());
                        msgArr[1] = String.valueOf(discardCounts);
                        msgArr[2]=String.valueOf(cancelCounts);
                        //btslMessage = new BTSLMessages("batcho2c.batcho2capprove.msg.bachprocessfilefailedsuccess", msgArr, "processFileBatchO2CApprovel");
                        theForm.setViewErrorLog("Y");
                        MasterErrorList masterErrorList = new MasterErrorList();
                    	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                    	
                    	for (int i = 0; i < theForm.getErrorList().size(); i++) {
                        	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                        	errorVO = (ListValueVO) theForm.getErrorList().get(i);
                        	rowErrorMsgLists.setRowName(errorVO.getCodeName());
        					rowErrorMsgLists.setRowValue("Line "+errorVO.getOtherInfo());
        					masterErrorList.setErrorMsg(errorVO.getOtherInfo2());
        					masterErrorLists.add(masterErrorList);
        					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
        					rowErrorMsgListsFinal.add(rowErrorMsgLists);
                            
                            errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                            theForm.getErrorList().set(i, errorVO);
                        }
                        ErrorMap p= new ErrorMap();
                    	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
                    	response.setErrorMap(p);
                    	//writeToFileError(response);
                    	 errorFileRequestVO.setRowErrorMsgLists(p.getRowErrorMsgLists());
        				 errorFileRequestVO.setFile(this.request.getFileAttachment());
        				 errorFileRequestVO.setFiletype(this.request.getFileType());
        				 errorFileRequestVO.setPartialFailure(true);
        				 new DownloadUserListServiceImpl().downloadErrorFile(errorFileRequestVO, errorFileResponse, responseSwag);
        				 response.setFileAttachment(errorFileResponse.getFileAttachment());
        				 response.setFileName(errorFileResponse.getFileName());
        				 responseSwag.setStatus(HttpStatus.SC_OK);
                        response.setStatus("400");
        				response.setMessageCode("");
        				response.setMessage("File is uploaded and processed successfully, "+msgArr[0]+" records processed successfully, "+msgArr[1]+" records discarded.  "+msgArr[2] +"  Records Cancelled");
        				response.setService("o2cBatchTransferApprovalResp");
                    }
                }
            } else {
                msgArr = new String[] { String.valueOf(closedOrderMap.size() + approveRejectMap.size()), String.valueOf(discardCounts) };
                //final BTSLMessages btslMessage = new BTSLMessages("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully",msgArr,"firstPage");
                //forward = super.handleMessage(btslMessage, request, mapping);
                response.setStatus("200");
				response.setMessageCode("");
				response.setMessage("File is uploaded and processed successfully, "+String.valueOf(closedOrderMap.size() + approveRejectMap.size())+" records processed successfully, "+String.valueOf(discardCounts)+" records discarded.");
				response.setService("o2cBatchTransferApprovalResp");
            }
        }
    } catch (Exception e) {
        log.error("processFile", "Exception:e=" + e);
        log.errorTrace(METHOD_NAME, e);
        // Delete file if exception occure
        if(!fileExist)
			filedelete();
        BatchO2CFileProcessLog.o2cBatchMasterLog("processFile", focBatchMasterVO, "FAIL : Error message is =" + e.getMessage(), "CURRENT LEVEL=" + theForm
            .getRequestType());
        
    } finally {
        // as to make the status of the batch o2c process as complete into
        // the table so that only
        // one instance should be executed for batch o2c
        if (processRunning) {
            try {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                   con.commit();
                } else {
                   con.rollback();;
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.error("processFile", " Exception in update process detail for batch foc approval file processing  " + e.getMessage());
                }
                log.errorTrace(METHOD_NAME, e);
            }
        } else // delete the uploaded file
        {
            // Delete file if exception occure
        	if(!fileExist)
    			filedelete();
        }
        // ends here

        // close the connection
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				 if (log.isDebugEnabled()) {
	                    log.error("processFile", " Exception in update process detail for batch foc approval file processing  " + e.getMessage());
	                }
	                log.errorTrace(METHOD_NAME, e);
			}
			con = null;
		}
        /*if (log.isDebugEnabled()) {
            log.debug("processFile", "Exited forward=" + forward);
        }*/
        final StringBuffer OtherInfo = new StringBuffer("CURRENT LEVEL=" + theForm.getRequestType());
        if (msgArr != null) {
            if (msgArr.length < 2) {
                OtherInfo.append(", DISSCARDED RECORD = " + msgArr[0]);
            } else if (msgArr.length > 1) {
                OtherInfo.append(", PROCESSED RECORD = " + msgArr[0]);
                OtherInfo.append(", DISSCARDED RECORD = " + msgArr[1]);
            }
        }
        BatchO2CFileProcessLog.o2cBatchMasterLog("processFile", focBatchMasterVO, "FINALLY BLOCK : Process completed", OtherInfo.toString());
    }
    return response;



}
private O2CBatchWithdrawFileResponse processUploadedFileTransfer(Connection con,O2CBatchWithdrawForm theForm,O2CBatchMasterVO focBatchMasterVO ,UserVO userVOreq,int size,int blankCount,int cancelCounts,int discardCounts,LinkedHashMap approveRejectMap,LinkedHashMap closedOrderMap,HttpServletRequest request,ArrayList batchItemsList,HttpServletResponse responseSwag) {
    final String METHOD_NAME = "processUploadedFile";
    if (log.isDebugEnabled()) {
        log.debug("processFile", "Entered");
    }
    // contains forward path
    //ActionForward forward = null;
    // path where file will be uploaded on server
    // Name of file to be uploaded on server
    // object of formbean

    String msgArr[] = null;
    ProcessStatusVO processVO = null;
    boolean processRunning = true;
    try {

    	ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
		ErrorFileResponse errorFileResponse =  new ErrorFileResponse();
        // as to check the status of the batch foc process into the table so
        // that only
        // one instance should be executed for batch foc
        final ProcessBL processBL = new ProcessBL();
        final UserVO userVO = userVOreq;
        final ChannelUserVO channelUserVO = (ChannelUserVO) userVOreq;
        ListValueVO errorVO = null;
        ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal = new ArrayList<>();
        try {
            processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.O2C_BATCH_PROCESS_ID,userVO.getNetworkID());
        } catch (BTSLBaseException e) {
            log.error("processFile", "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            processRunning = false;
            throw new BTSLBaseException(this, "processFile", "batcho2c.processfile.error.alreadyexecution");
        }
        if (processVO != null && !processVO.isStatusOkBool()) {
            processRunning = false;
            throw new BTSLBaseException(this, "processFile", "batcho2c.processfile.error.alreadyexecution");
        }
        con.commit();
        processVO.setNetworkCode(userVO.getNetworkID());
        // ends here

        // contains the error list in file validation occur
        // contains error list when error is found when approve or reject
        // the record
        ArrayList approveRejectErrorList = null;
        // contains error list when record is closed
        ArrayList closeErrorList = null;
        // this will hold the information aboput the
        // error()CodeName=MSISDN,otherInfo=Record number,
        // OtherInfo2=message to be dispayed)
        // true if file validation error exists
        // Holds the external txn number(will be used to check the unique at
        // the time of file validation)
        // counts the discard count
        
        // counts the blanck line
        
        // If file validation not exists the processd the records
         {
        	 final HashMap<String, String> map = new HashMap<String, String>();
        	 final BatchO2CTransferWebDAO batchO2CTransferwebDAO = new BatchO2CTransferWebDAO();

            boolean isModified = false;
            if ((closedOrderMap != null && !closedOrderMap.isEmpty()) || (approveRejectMap != null && !approveRejectMap.isEmpty())) { // check
                // the
                // batch
                // is
                // modified
                // or
                // not
                isModified = batchO2CTransferwebDAO.isBatchO2CModified(con, focBatchMasterVO.getModifiedOn().getTime(), theForm.getBatchId());
                if (isModified) {
                    //BTSLMessages btslMessage = null;
                    //btslMessage = new BTSLMessages("batcho2c.batcho2capprovereject.msg.batchmodify", "firstPage");
                    //forward = super.handleMessage(btslMessage, request, mapping);
                    response.setStatus("206");
    				response.setMessageCode("");
    				response.setMessage("Batch is already modified by someone else, please reload the data");
    				response.setService("o2cBatchTransferApprovalResp");
                } else {
                    String currentLevel = null;
                    if ("approval1".equals(theForm.getRequestType())) {
                        currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
                    } else if ("approval2".equals(theForm.getRequestType())) {
                        currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE;
					} /*
						 * else if ("approval3".equals(theForm.getRequestType())) { currentLevel =
						 * PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3; }
						 */

                    if (closedOrderMap != null && !closedOrderMap.isEmpty()) {
                        // If closedOrderMap is not null then closed the
                        // orders in closedOrderMap
                    	 final int updateCount = batchO2CTransferwebDAO.updateBatchO2CStatus(con, theForm.getBatchId(),
                                 PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN);
                             if (updateCount <= 0) {
                                 con.rollback();
                                 throw new BTSLBaseException(this, "processFile", "error.general.processing", "firstPage");
                             }
                             con.commit();
                             closeErrorList = batchO2CTransferwebDAO.closeOrderByBatch(con, closedOrderMap, currentLevel, channelUserVO.getUserID(), focBatchMasterVO,new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), theForm.getDefaultLang(), theForm.getSecondLang());
                    }// If approveRejectMap is not null then approve or
                     // reject the order
                    if (approveRejectMap != null && !approveRejectMap.isEmpty()) {
                        // If closedOrderMap is not null then closed the
                        // orders in closedOrderMap
                        final int updateCount = batchO2CTransferwebDAO.updateBatchO2CStatus(con, theForm.getBatchId(),
                            PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN);
                        if (updateCount <= 0) {
                        	con.rollback();
                            throw new BTSLBaseException(this, "processFile", "error.general.processing", "firstPage");
                        }
                        con.commit();
                        approveRejectErrorList = batchO2CTransferwebDAO.processOrderByBatch(con, approveRejectMap, currentLevel, channelUserVO.getUserID(),
                        		new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), theForm.getDefaultLang(), theForm
                                .getSecondLang());
                    }
                    // two error list may be there. If two are there then
                    // merge and sort them
                    if (approveRejectErrorList != null && !approveRejectErrorList.isEmpty()) {
                        theForm.setErrorList(approveRejectErrorList);
                    }
                    if (closeErrorList != null && !closeErrorList.isEmpty()) {
                        if (approveRejectErrorList != null && !approveRejectErrorList.isEmpty()) {
                            approveRejectErrorList.addAll(closeErrorList);
                            theForm.setErrorList(approveRejectErrorList);
                        } else {
                            theForm.setErrorList(closeErrorList);
                        }
                        Collections.sort(theForm.getErrorList());
                    }
                    msgArr=new String[3];
                    // success message
                    if (theForm.getErrorList() == null || theForm.getErrorList().isEmpty()) {
                    	int totalSize=closedOrderMap.size()+approveRejectMap.size()+discardCounts;
                        int processed = approveRejectMap.size();
                        int approved=approveRejectMap.size()-cancelCounts;
                        int canceled=cancelCounts;
                        
                        response.setStatus("200");
        				response.setMessageCode("");
        				if (closedOrderMap != null && !closedOrderMap.isEmpty()) {
        					approved=closedOrderMap.size();
        				 }
        				response.setMessage(approved+" records approved, "+canceled+" rejected");                        response.setBatchID(theForm.getBatchId());
                        response.setService("o2cBatchTransferApprovalResp");
                        

                    } else if (theForm.getErrorList().size() + discardCounts == size - 1) {
                        theForm.setProcessedRecs(String.valueOf(theForm.getErrorList().size()));
                        msgArr[0] = String.valueOf(discardCounts);
                        theForm.setNoOfRecords(String.valueOf((size - discardCounts - 1 - blankCount)));
                        //btslMessage = new BTSLMessages("batcho2c.batcho2capprove.msg.bachprocessfilefailedall", msgArr, "processFileBatchO2CApprovel");
                        theForm.setViewErrorLog("Y");
                        MasterErrorList masterErrorList = new MasterErrorList();
                    	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                    	
                    	for (int i = 0; i < theForm.getErrorList().size(); i++) {
                        	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                        	errorVO = (ListValueVO) theForm.getErrorList().get(i);
                        	rowErrorMsgLists.setRowName(errorVO.getCodeName());
        					rowErrorMsgLists.setRowValue("Line "+errorVO.getOtherInfo());
        					masterErrorList.setErrorMsg(errorVO.getOtherInfo2());
        					masterErrorLists.add(masterErrorList);
        					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
        					rowErrorMsgListsFinal.add(rowErrorMsgLists);
                            
                            errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                            theForm.getErrorList().set(i, errorVO);
                        }
                        ErrorMap p= new ErrorMap();
                    	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
                    	response.setErrorMap(p);
                    	 errorFileRequestVO.setRowErrorMsgLists(p.getRowErrorMsgLists());
        				 errorFileRequestVO.setFile(this.request.getFileAttachment());
        				 errorFileRequestVO.setFiletype(this.request.getFileType());
        				 errorFileRequestVO.setPartialFailure(false);
        				 new DownloadUserListServiceImpl().downloadErrorFile(errorFileRequestVO, errorFileResponse, responseSwag);
        				 response.setFileAttachment(errorFileResponse.getFileAttachment());
        				 response.setFileName(errorFileResponse.getFileName());
        				 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                    	 response.setStatus("400");
        				response.setMessageCode("");
        				response.setMessage("No valid record in file for processing,"+msgArr[0]+" records discarded.");
        				response.setService("o2cBatchTransferApprovalResp");
                    }
                    // success message with showing invalid MSISDN list
                    else {
                        theForm.setProcessedRecs(String.valueOf(theForm.getErrorList().size()));
                        theForm.setNoOfRecords(String.valueOf((size - discardCounts - 1 - blankCount)));
                        msgArr[0] = String.valueOf(closedOrderMap.size() + approveRejectMap.size() - theForm.getErrorList().size());
                        msgArr[1] = String.valueOf(discardCounts);
                        msgArr[2]=String.valueOf(cancelCounts);
                        //btslMessage = new BTSLMessages("batcho2c.batcho2capprove.msg.bachprocessfilefailedsuccess", msgArr, "processFileBatchO2CApprovel");
                        theForm.setViewErrorLog("Y");
                        MasterErrorList masterErrorList = new MasterErrorList();
                    	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                    	
                    	for (int i = 0; i < theForm.getErrorList().size(); i++) {
                        	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                        	errorVO = (ListValueVO) theForm.getErrorList().get(i);
                        	rowErrorMsgLists.setRowName(errorVO.getCodeName());
        					rowErrorMsgLists.setRowValue("Line "+errorVO.getOtherInfo());
        					masterErrorList.setErrorMsg(errorVO.getOtherInfo2());
        					masterErrorLists.add(masterErrorList);
        					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
        					rowErrorMsgListsFinal.add(rowErrorMsgLists);
                            
                            errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                            theForm.getErrorList().set(i, errorVO);
                        }
                        ErrorMap p= new ErrorMap();
                    	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
                    	response.setErrorMap(p);
                    	//writeToFileError(response);
                    	 errorFileRequestVO.setRowErrorMsgLists(p.getRowErrorMsgLists());
        				 errorFileRequestVO.setFile(this.request.getFileAttachment());
        				 errorFileRequestVO.setFiletype(this.request.getFileType());
        				 errorFileRequestVO.setPartialFailure(true);
        				 new DownloadUserListServiceImpl().downloadErrorFile(errorFileRequestVO, errorFileResponse, responseSwag);
        				 response.setFileAttachment(errorFileResponse.getFileAttachment());
        				 response.setFileName(errorFileResponse.getFileName());
        				 responseSwag.setStatus(HttpStatus.SC_OK);
                        response.setStatus("400");
        				response.setMessageCode("");
        				response.setMessage("File is uploaded and processed successfully, "+msgArr[0]+" records processed successfully, "+msgArr[1]+" records discarded.  "+msgArr[2] +"  Records Cancelled");
        				response.setService("o2cBatchTransferApprovalResp");
                    }
                }
            } else {
                msgArr = new String[] { String.valueOf(closedOrderMap.size() + approveRejectMap.size()), String.valueOf(discardCounts) };
                //final BTSLMessages btslMessage = new BTSLMessages("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully",msgArr,"firstPage");
                //forward = super.handleMessage(btslMessage, request, mapping);
                response.setStatus("200");
				response.setMessageCode("");
				response.setMessage("File is uploaded and processed successfully, "+String.valueOf(closedOrderMap.size() + approveRejectMap.size())+" records processed successfully, "+String.valueOf(discardCounts)+" records discarded.");
				response.setService("o2cBatchTransferApprovalResp");
            }
        }
    } catch (Exception e) {
        log.error("processFile", "Exception:e=" + e);
        log.errorTrace(METHOD_NAME, e);
        // Delete file if exception occure
        if(!fileExist)
			filedelete();
        BatchO2CProcessLog.o2cBatchMasterLog("processFile", focBatchMasterVO, "FAIL : Error message is =" + e.getMessage(), "CURRENT LEVEL=" + theForm.getRequestType());
        
        
    } finally {
        // as to make the status of the batch o2c process as complete into
        // the table so that only
        // one instance should be executed for batch o2c
        if (processRunning) {
            try {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                   con.commit();
                } else {
                   con.rollback();;
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.error("processFile", " Exception in update process detail for batch foc approval file processing  " + e.getMessage());
                }
                log.errorTrace(METHOD_NAME, e);
            }
        } else // delete the uploaded file
        {
            // Delete file if exception occure
        	if(!fileExist)
    			filedelete();
        }
        // ends here

        // close the connection
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				 if (log.isDebugEnabled()) {
	                    log.error("processFile", " Exception in update process detail for batch foc approval file processing  " + e.getMessage());
	                }
	                log.errorTrace(METHOD_NAME, e);
			}
			con = null;
		}
       /* if (log.isDebugEnabled()) {
            log.debug("processFile", "Exited forward=" + forward);
        }*/
        final StringBuffer OtherInfo = new StringBuffer("CURRENT LEVEL=" + theForm.getRequestType());
        if (msgArr != null) {
            if (msgArr.length < 2) {
                OtherInfo.append(", DISSCARDED RECORD = " + msgArr[0]);
            } else if (msgArr.length > 1) {
                OtherInfo.append(", PROCESSED RECORD = " + msgArr[0]);
                OtherInfo.append(", DISSCARDED RECORD = " + msgArr[1]);
            }
        }
        BatchO2CProcessLog.o2cBatchMasterLog("processFile", focBatchMasterVO, "FINALLY BLOCK : Process completed", OtherInfo.toString());
    }
    return response;



}

@Override
public O2CBatchWithdrawFileResponse processBulkComBatchProcessRequest(
		O2CBatchWithdrawFileRequest bulkComProcessApiRequest, String msisdn, OperatorUtilI calculatorI,
		Locale locale, Connection con, String requestType, String batchID, String requestIDStr,
		HttpServletRequest httprequest, MultiValueMap<String, String> headers, HttpServletResponse response1) {
	


	final String methodName = "processBulkComBatchProcessRequest";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered ");
	}

	String errorMessage=null;
	String errorMessageCode=null;
	int status;
	errorSize=0;
	processRunning = true;
    fileExist=false;
	this.request = bulkComProcessApiRequest;
	LinkedHashMap<String, List<String>> bulkDataMap ;

	try {
		response = new O2CBatchWithdrawFileResponse();
		FOCBatchForm theForm = new FOCBatchForm();
		//basic form validation at api level
		UserVO userVO = new UserDAO().loadUsersDetails(con, msisdn);
    	inputValidations = new ArrayList<>();
    	ChannelUserVO channelUserVO=new ChannelUserVO();
		 
		channelUserVO = (ChannelUserVO) userVO;
		UserPhoneVO userPhoneVO = new UserDAO().loadUserPhoneVO(con,  userVO.getUserID());
		channelUserVO.setUserPhoneVO(userPhoneVO);
        
        //  request input validation 
		requestValidation();

		//throwing list of basic form errors
		if(!BTSLUtil.isNullOrEmptyList(inputValidations)) {
			  response.setStatus("400");
			  response.setService("batchComProcessResp");
			  response.setErrorMap(new ErrorMap());
			  response.getErrorMap().setMasterErrorList(inputValidations);
			  return response;
		}

	      
	    if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO.getOutSuspened())) {
	           if (log.isDebugEnabled()) {
	                log.debug("userSearch", "USER IS OUT SUSPENDED IN THE SYSTEM");
	            }

			    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OUT_SUSPENDED, PretupsI.RESPONSE_FAIL, null);
	    }
	       
		 theForm.setBatchId(batchID);
		 theForm.setRequestType(requestType);
		 theForm.setFocOrderApprovalLevel(SystemPreferences.DP_ORDER_APPROVAL_LVL);
		 theForm.setDefaultLang(bulkComProcessApiRequest.getLanguage1());
		 theForm.setSecondLang(bulkComProcessApiRequest.getLanguage2());
		 
		 final String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORDP;
         if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf(String.valueOf(theForm.getFocOrderApprovalLevel())) != -1) {
             theForm.setExternalTxnMandatory(PretupsI.YES);
         }
		 
		/*
		 * Uploading and validating file
		 */
	   	//code for read file content
		 
		ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
		ErrorMap errorMap = new ErrorMap();
		HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
		fileDetailsMap.put(PretupsI.FILE_TYPE1, bulkComProcessApiRequest.getFileType());
		fileDetailsMap.put(PretupsI.FILE_NAME,bulkComProcessApiRequest.getFileName());
		fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, bulkComProcessApiRequest.getFileAttachment());
		fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, "batchComProcessResp");
		
		ArrayList batchItemsList = new ArrayList();
		// Check external txn id for domain type
//		final String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORO2C;
//
//      if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("3") != -1) {
//            theForm.setExternalTxnMandatory(PretupsI.YES);
//      }
           
         FOCBatchMasterVO focBatchMasterVO = new FOCBatchMasterVO();
            
         final LinkedHashMap approveRejectMap = new LinkedHashMap();
         // holds focBatchMasterVO for order close
         final LinkedHashMap closedOrderMap = new LinkedHashMap();
       
         // To keep track of index of fields after level 1 approved on.
         // Because in case of level 1 approval we will not show level 2
         // approval details.
         int indexToBeReduced = 0;
            
         if ("approval1".equals(theForm.getRequestType())) {
                indexToBeReduced = 2;
         }
         if ("approval2".equals(theForm.getRequestType())) {
         }
         if ("approval3".equals(theForm.getRequestType())) {
         }
         
         // get the datamap from the form
         LinkedHashMap hashMap = theForm.getDownLoadDataMap();
         
         final FOCBatchTransferDAO focBatchTransferDAO  = new FOCBatchTransferDAO();
		 final FOCBatchTransferWebDAO focBatchTransferWebDAO = new FOCBatchTransferWebDAO();
         // datamap on form is null then load the data from the database.
         if (hashMap == null || hashMap.size() <= 0) {
        	 String statusUsed = null;
        	 if ("approval1".equals(theForm.getRequestType())) {
                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
        	 }
        	 if ("approval2".equals(theForm.getRequestType())) {
                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
        	 }
        	 if ("approval3".equals(theForm.getRequestType())) {
                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
        	 }
        	 
        	 
//           theForm.setO2cOrderApprovalLevel(((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.FOC_ORDER_APPROVAL_LVL)).intValue());
        	 hashMap = focBatchTransferDAO.loadBatchItemsMap(con, theForm.getBatchId(), statusUsed);
        	 theForm.setDownLoadDataMap(hashMap);
         }

		ArrayList<FOCBatchMasterVO> listFocBatchVO =  focBatchTransferDAO.getComissionWalletType(con,theForm.getBatchId());
		String commwalletType =null;
		String focOrCommPayout =null;
		if (listFocBatchVO.size()>0) {
			FOCBatchMasterVO fOCBatchMasterVO =  (FOCBatchMasterVO) listFocBatchVO.get(0);
			commwalletType =fOCBatchMasterVO.getWallet_type();
			focOrCommPayout= fOCBatchMasterVO.getFocOrCommPayout();
		}

		if ("approval1".equals(theForm.getRequestType())) {
                final String statusUsed1 = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
			    ArrayList focBatchMasterVOList = new ArrayList();
                // load the batch details from the database
			if(PretupsI.DP_TRANSFER.equals(focOrCommPayout)) {
				focBatchMasterVOList = focBatchTransferDAO.loadBatchDPMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
						PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
				theForm.setFocBatchMasterVOList(focBatchMasterVOList);
			}else if(PretupsI.FOC_TRANSFER.equals(focOrCommPayout)) {
				focBatchMasterVOList = focBatchTransferWebDAO.loadBatchFOCMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
						PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
				theForm.setFocBatchMasterVOList(focBatchMasterVOList);
			}

			for(int i=0;i<theForm.getFocBatchMasterVOList().size();i++)
                {
                	String id=((FOCBatchMasterVO)theForm.getFocBatchMasterVOList().get(i)).getBatchId();
                	if(id.equals(batchID))
                	{
                		focBatchMasterVO=(FOCBatchMasterVO)theForm.getFocBatchMasterVOList().get(i);
                		break;
                	}
                		
                }
                }
                else if("approval2".equals(theForm.getRequestType()))
                {
                	final String statusUsed1 = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
					ArrayList focBatchMasterVOList = new ArrayList();
					if(PretupsI.DP_TRANSFER.equals(focOrCommPayout)) {
						// load the batch details from the database
						focBatchMasterVOList = focBatchTransferDAO.loadBatchDPMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
								PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
						theForm.setFocBatchMasterVOList(focBatchMasterVOList);
					}else if(PretupsI.FOC_TRANSFER.equals(focOrCommPayout)) {
						focBatchMasterVOList = focBatchTransferWebDAO.loadBatchFOCMasterDetails(con, channelUserVO.getUserID(), statusUsed1,PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
						theForm.setFocBatchMasterVOList(focBatchMasterVOList);
					}
					for(int i=0;i<theForm.getFocBatchMasterVOList().size();i++)
                    {
                    	String id=((FOCBatchMasterVO)theForm.getFocBatchMasterVOList().get(i)).getBatchId();
                    	if(id.equals(batchID))
                    	{
                    		focBatchMasterVO=(FOCBatchMasterVO)theForm.getFocBatchMasterVOList().get(i);
                    		break;
                    	}
                    		
                    }
                }
                else {
                	final String statusUsed1 = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
			ArrayList focBatchMasterVOList = new ArrayList();
			if(PretupsI.DP_TRANSFER.equals(focOrCommPayout)) {
				// load the batch details from the database
				focBatchMasterVOList = focBatchTransferDAO.loadBatchDPMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
						PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
				theForm.setFocBatchMasterVOList(focBatchMasterVOList);
			}else if(PretupsI.FOC_TRANSFER.equals(focOrCommPayout)) {
				focBatchMasterVOList = focBatchTransferWebDAO.loadBatchFOCMasterDetails(con, channelUserVO.getUserID(), statusUsed1,
						PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
				theForm.setFocBatchMasterVOList(focBatchMasterVOList);
			}
			for(int i=0;i<theForm.getFocBatchMasterVOList().size();i++)
                     {
                     	String id=((FOCBatchMasterVO)theForm.getFocBatchMasterVOList().get(i)).getBatchId();
                     	if(id.equals(batchID))
                     	{
                     		focBatchMasterVO=(FOCBatchMasterVO)theForm.getFocBatchMasterVOList().get(i);
                     		break;
                     	}
                     		
                     }
                }
			bulkDataMap = fileUtil.uploadAndReadGenericFileBulkComProcessApproval(fileDetailsMap, 0, errorMap,closedOrderMap,approveRejectMap, batchItemsList,hashMap,indexToBeReduced,theForm);
            
            
			LinkedList<String> res = (LinkedList<String>) bulkDataMap.get("isValidRow");
			
			String error = res.get(0);
			int blankCount = Integer.valueOf(res.get(1));
			int cancelCount = Integer.valueOf(res.get(2));
			int discardCount = Integer.valueOf(res.get(3));
			int size = Integer.valueOf(res.get(4));
			/*
			 * ArrayList fileContents = getFileContentsList(bulkDataMap); int records =
			 * fileContents.size();
			 */
			
			//response.setNumberOfRecords(totalRecords);
			// /
            // If file does not contain record as entered by the user then
            // show the error message
            // (records excludes blank lines)
            // /
			ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
			ErrorFileResponse errorFileResponse =  new ErrorFileResponse();
			 if (error.equals("true")) {
				 errorFileRequestVO.setRowErrorMsgLists(errorMap.getRowErrorMsgLists());
				 errorFileRequestVO.setFile(bulkComProcessApiRequest.getFileAttachment());
				 errorFileRequestVO.setFiletype(bulkComProcessApiRequest.getFileType());
				 errorFileRequestVO.setPartialFailure(false);
				 new DownloadUserListServiceImpl().downloadErrorFile(errorFileRequestVO, errorFileResponse, response1);
				 response.setFileAttachment(errorFileResponse.getFileAttachment());
				 response.setFileName(errorFileResponse.getFileName());
				 response.setErrorMap(errorMap);
		         response.setStatus("400");
		         response.setMessage("Some Records contain error.Kindly correct them");
		         response.setService("o2cBatchTransferApprovalResp");
		         response.setBatchID(theForm.getBatchId());
		         response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		         return response;
		         
		         }
			else
			{
				List<String> filePath= bulkDataMap.get("filepathtemp");
				String filePaths=filePath.get(0);
				filepathtemp=filePaths;
				theForm.setDefaultLang(focBatchMasterVO.getDefaultLang());
				theForm.setSecondLang(focBatchMasterVO.getSecondLang());
				processUploadedFileBulkCom(con,theForm,focBatchMasterVO,userVO,size,blankCount,cancelCount,discardCount,approveRejectMap,closedOrderMap, httprequest, batchItemsList,response1);
				
				if(BTSLUtil.isNullString(response.getMessage()))
				{
				response.setStatus("200");
				response.setMessageCode(errorMessageCode);
				response.setMessage("BATCH "+theForm.getBatchId()+" generated successfully.All Records processed");
				response.setService("o2cBatchTransferApprovalResp");
				response.setBatchID(theForm.getBatchId());
				}
			}
      
	} catch (BTSLBaseException be) {
		if(!fileExist)
		filedelete();
		log.error(methodName, "Exception:e=" + be);
		log.errorTrace(methodName, be);
		String [] args=null;
		if(!BTSLUtil.isNullorEmpty(be.getArgs())) {
			args = be.getArgs();
		}
		if(!BTSLUtil.isNullorEmpty(be.getMessageKey())) {
			errorMessage = RestAPIStringParser.getMessage(locale,
					be.getMessageKey(), args);
			errorMessageCode = be.getMessageKey();

			
		}
		if(!BTSLUtil.isNullorEmpty(be.getErrorCode()) &&  be.getErrorCode() !=0) {
			status = be.getErrorCode();
			} else {
				status= 400;
			}
	response.setStatus(String.valueOf(status));
	response.setMessageCode(errorMessageCode);
	response.setMessage(errorMessage);
	response.setService("o2cBatchTransferApprovalResp");
	
	} catch (Exception e) {
		log.error(methodName, "Exceptin:e=" + e);
		log.errorTrace(methodName, e);
		response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
		response.setMessageCode("error.general.processing");
		response.setMessage("Check File Type supplied.");
		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		 
	} finally {
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			log.errorTrace(methodName, e);
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, " Exited ");
		}
	}
	// log.debug(methodName, response);
	return response;


}

private O2CBatchWithdrawFileResponse processUploadedFileBulkCom(Connection con,FOCBatchForm theForm,FOCBatchMasterVO focBatchMasterVO ,UserVO userVOreq,int size,int blankCount,int cancelCounts,int discardCounts,LinkedHashMap approveRejectMap,LinkedHashMap closedOrderMap,HttpServletRequest request,ArrayList batchItemsList,HttpServletResponse responseSwag) {
    final String METHOD_NAME = "processUploadedFileBulkCom";
    if (log.isDebugEnabled()) {
        log.debug(METHOD_NAME, "Entered");
    }
    // contains forward path
    //ActionForward forward = null;
    // path where file will be uploaded on server
    // Name of file to be uploaded on server
    // object of formbean

    String msgArr[] = null;
    ProcessStatusVO processVO = null;
    boolean processRunning = true;
    try {


        // as to check the status of the batch foc process into the table so
        // that only
        // one instance should be executed for batch foc
        final ProcessBL processBL = new ProcessBL();
        final UserVO userVO = userVOreq;
        final ChannelUserVO channelUserVO = (ChannelUserVO) userVOreq;
        ListValueVO errorVO = null;
        ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
		ErrorFileResponse errorFileResponse =  new ErrorFileResponse();
        ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal = new ArrayList<>();
        try {
            processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.DP_BATCH_PROCESS_ID,userVO.getNetworkID());
        } catch (BTSLBaseException e) {
            log.error("processFile", "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            processRunning = false;
            throw new BTSLBaseException(this, "processFile", "batcho2c.processfile.error.alreadyexecution");
        }
        if (processVO != null && !processVO.isStatusOkBool()) {
            processRunning = false;
            throw new BTSLBaseException(this, "processFile", "batcho2c.processfile.error.alreadyexecution");
        }
        con.commit();
        processVO.setNetworkCode(userVO.getNetworkID());
        // ends here

        // contains the error list in file validation occur
        // contains error list when error is found when approve or reject
        // the record
        ArrayList approveRejectErrorList = null;
        // contains error list when record is closed
        ArrayList closeErrorList = null;
        // this will hold the information aboput the
        // error()CodeName=MSISDN,otherInfo=Record number,
        // OtherInfo2=message to be dispayed)
        // true if file validation error exists
        // Holds the external txn number(will be used to check the unique at
        // the time of file validation)
        // counts the discard count
        
        // counts the blanck line
        
        // If file validation not exists the processd the records
         {
        	 final HashMap<String, String> map = new HashMap<String, String>();
            final FOCBatchTransferDAO focBatchTransferDAO = new FOCBatchTransferDAO();

            boolean isModified = false;
            if ((closedOrderMap != null && !closedOrderMap.isEmpty()) || (approveRejectMap != null && !approveRejectMap.isEmpty())) { // check
                // the
                // batch
                // is
                // modified
                // or
                // not
                isModified = focBatchTransferDAO.isBatchModified(con, focBatchMasterVO.getModifiedOn().getTime(), theForm.getBatchId());
                if (isModified) {
                    //BTSLMessages btslMessage = null;
                    //btslMessage = new BTSLMessages("batcho2c.batcho2capprovereject.msg.batchmodify", "firstPage");
                    //forward = super.handleMessage(btslMessage, request, mapping);
                    response.setStatus("206");
    				response.setMessageCode("");
    				response.setMessage("Batch is already modified by someone else, please reload the data");
    				response.setService("o2cBatchTransferApprovalResp");
                } else {
                    String currentLevel = null;
                    if ("approval1".equals(theForm.getRequestType())) {
                        currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
                    } else if ("approval2".equals(theForm.getRequestType())) {
                        currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
                    } else if ("approval3".equals(theForm.getRequestType())) {
                        currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
                    }

                    if (closedOrderMap != null && !closedOrderMap.isEmpty()) {
                        // If closedOrderMap is not null then closed the
                        // orders in closedOrderMap
                        final int updateCount = focBatchTransferDAO.updateBatchStatus(con, theForm.getBatchId(),
                            PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);

                        if (updateCount <= 0) {
                        	con.rollback();
                            throw new BTSLBaseException(this, "processFile", "error.general.processing", "firstPage");
                        }
                        con.commit();

                        closeErrorList = focBatchTransferDAO.closeOrderByBatchForDirectPayoutRest(con, closedOrderMap, currentLevel, channelUserVO.getUserID(), focBatchMasterVO,
                        		 BTSLUtil.getBTSLLocale(request), theForm.getDefaultLang(), theForm.getSecondLang());

                    }// If approveRejectMap is not null then approve or
                     // reject the order
                    if (approveRejectMap != null && !approveRejectMap.isEmpty()) {
                        // If closedOrderMap is not null then closed the
                        // orders in closedOrderMap
                        final int updateCount = focBatchTransferDAO.updateBatchStatus(con, theForm.getBatchId(),
                        		 PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);

                        if (updateCount < 0) {
                        	con.rollback();
                            throw new BTSLBaseException(this, "processFile", "error.general.processing", "firstPage");
                        }
                        con.commit();

                        approveRejectErrorList = focBatchTransferDAO.processOrderByDPBatchRest(con, approveRejectMap, currentLevel, channelUserVO.getUserID(),
                             BTSLUtil.getBTSLLocale(request), theForm.getDefaultLang(), theForm
                                .getSecondLang());
                    }
                    // two error list may be there. If two are there then
                    // merge and sort them
                    if (approveRejectErrorList != null && !approveRejectErrorList.isEmpty()) {
                        theForm.setErrorList(approveRejectErrorList);
                    }
                    if (closeErrorList != null && !closeErrorList.isEmpty()) {
                        if (approveRejectErrorList != null && !approveRejectErrorList.isEmpty()) {
                            approveRejectErrorList.addAll(closeErrorList);
                            theForm.setErrorList(approveRejectErrorList);
                        } else {
                            theForm.setErrorList(closeErrorList);
                        }
                        Collections.sort(theForm.getErrorList());
                    }
                    msgArr=new String[3];
                    // success message
                    if (theForm.getErrorList() == null || theForm.getErrorList().isEmpty()) {
                        int totalSize=closedOrderMap.size()+approveRejectMap.size()+discardCounts;
                        int processed = approveRejectMap.size();
                        int approved=approveRejectMap.size()-cancelCounts;
                        int canceled=cancelCounts;
                        response.setStatus("200");
        				response.setMessageCode("");
        				
        				if (closedOrderMap != null && !closedOrderMap.isEmpty()) {
        					approved=closedOrderMap.size();
        				 }
        				response.setMessage(approved+" records approved, "+canceled+" rejected");
        				response.setService("bulkComProcessResp");
        				response.setBatchID(theForm.getBatchId());
        				responseSwag.setStatus(HttpStatus.SC_OK);

                    } else if (theForm.getErrorList().size() + discardCounts == size - 1) {
                        theForm.setProcessedRecs(String.valueOf(theForm.getErrorList().size()));
                        msgArr[0] = String.valueOf(discardCounts);
                        theForm.setNoOfRecords(String.valueOf((size - discardCounts - 1 - blankCount)));
                        //btslMessage = new BTSLMessages("batcho2c.batcho2capprove.msg.bachprocessfilefailedall", msgArr, "processFileBatchO2CApprovel");
                        theForm.setViewErrorLog("Y");
                        MasterErrorList masterErrorList = new MasterErrorList();
                    	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                    	
                    	for (int i = 0; i < theForm.getErrorList().size(); i++) {
                        	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                        	errorVO = (ListValueVO) theForm.getErrorList().get(i);
                        	rowErrorMsgLists.setRowName(errorVO.getCodeName());
        					rowErrorMsgLists.setRowValue("Line "+errorVO.getOtherInfo());
        					masterErrorList.setErrorMsg(errorVO.getOtherInfo2());
        					masterErrorLists.add(masterErrorList);
        					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
        					rowErrorMsgListsFinal.add(rowErrorMsgLists);
                            
                            errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                            theForm.getErrorList().set(i, errorVO);
                        }
                        ErrorMap p= new ErrorMap();
                    	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
                    	response.setErrorMap(p);
                    	//writeToFileError(response);
                    	 errorFileRequestVO.setRowErrorMsgLists(p.getRowErrorMsgLists());
        				 errorFileRequestVO.setFile(this.request.getFileAttachment());
        				 errorFileRequestVO.setFiletype(this.request.getFileType());
        				 errorFileRequestVO.setPartialFailure(false);
        				 new DownloadUserListServiceImpl().downloadErrorFile(errorFileRequestVO, errorFileResponse, responseSwag);
        				 response.setFileAttachment(errorFileResponse.getFileAttachment());
        				 response.setFileName(errorFileResponse.getFileName());
                    	 response.setStatus("400");
                    	 response.setMessageCode("");
                    	 response.setMessage("No valid record in file for processing,"+msgArr[0]+" records discarded.");
                    	 response.setService("bulkComProcessResp");
                    	 response.setBatchID(theForm.getBatchId());
                    	 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                    	 
                    }
                    // success message with showing invalid MSISDN list
                    else {
                        theForm.setProcessedRecs(String.valueOf(theForm.getErrorList().size()));
                        theForm.setNoOfRecords(String.valueOf((size - discardCounts - 1 - blankCount)));
                        msgArr[0] = String.valueOf(closedOrderMap.size() + approveRejectMap.size() - theForm.getErrorList().size());
                        msgArr[1] = String.valueOf(discardCounts);
                        msgArr[2]=String.valueOf(cancelCounts);
                        MasterErrorList masterErrorList = new MasterErrorList();
                    	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                    	
                    	for (int i = 0; i < theForm.getErrorList().size(); i++) {
                        	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                        	errorVO = (ListValueVO) theForm.getErrorList().get(i);
                        	rowErrorMsgLists.setRowName(errorVO.getCodeName());
        					rowErrorMsgLists.setRowValue("Line "+errorVO.getOtherInfo());
        					masterErrorList.setErrorMsg(errorVO.getOtherInfo2());
        					masterErrorLists.add(masterErrorList);
        					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
        					rowErrorMsgListsFinal.add(rowErrorMsgLists);
                            
                            errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                            theForm.getErrorList().set(i, errorVO);
                        }
                        ErrorMap p= new ErrorMap();
                    	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
                    	response.setErrorMap(p);
                    	//writeToFileError(response);
                    	 errorFileRequestVO.setRowErrorMsgLists(p.getRowErrorMsgLists());
        				 errorFileRequestVO.setFile(this.request.getFileAttachment());
        				 errorFileRequestVO.setFiletype(this.request.getFileType());
        				 errorFileRequestVO.setPartialFailure(true);
        				 new DownloadUserListServiceImpl().downloadErrorFile(errorFileRequestVO, errorFileResponse, responseSwag);
        				 response.setFileAttachment(errorFileResponse.getFileAttachment());
        				 response.setFileName(errorFileResponse.getFileName());
                        theForm.setViewErrorLog("Y");
                        response.setStatus("400");
        				response.setMessageCode("");
        				response.setMessage("File is uploaded and processed successfully, "+msgArr[0]+" records processed successfully, "+msgArr[1]+" records discarded.  "+msgArr[2] +"  Records Cancelled");
        				response.setService("bulkComProcessResp");
        				response.setBatchID(theForm.getBatchId());
        				responseSwag.setStatus(HttpStatus.SC_OK);
                    }
                }
            } else {
                msgArr = new String[] { String.valueOf(closedOrderMap.size() + approveRejectMap.size()), String.valueOf(discardCounts) };
                //final BTSLMessages btslMessage = new BTSLMessages("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully",msgArr,"firstPage");
                //forward = super.handleMessage(btslMessage, request, mapping);
                response.setStatus("200");
				response.setMessageCode("");
				response.setMessage("File is uploaded and processed successfully, "+String.valueOf(closedOrderMap.size() + approveRejectMap.size())+" records processed successfully, "+String.valueOf(discardCounts)+" records discarded.");
				response.setService("bulkComProcessResp");
				response.setBatchID(theForm.getBatchId());
            }
         }
    } catch (Exception e) {
        log.error("processFile", "Exception:e=" + e);
        log.errorTrace(METHOD_NAME, e);
        // Delete file if exception occure
        if(!fileExist)
			filedelete();
        BatchO2CFileProcessLog.o2cBatchMasterLog(METHOD_NAME, focBatchMasterVO, "FAIL : Error message is =" + e.getMessage(), "CURRENT LEVEL=" + theForm
            .getRequestType());
        
    } finally {
        // as to make the status of the batch o2c process as complete into
        // the table so that only
        // one instance should be executed for batch o2c
        if (processRunning) {
            try {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                   con.commit();
                } else {
                   con.rollback();;
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.error(METHOD_NAME, " Exception in update process detail for batch foc approval file processing  " + e.getMessage());
                }
                log.errorTrace(METHOD_NAME, e);
            }
        } else // delete the uploaded file
        {
            // Delete file if exception occure
        	if(!fileExist)
    			filedelete();
        }
        // ends here

        // close the connection
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				 if (log.isDebugEnabled()) {
	                    log.error(METHOD_NAME, " Exception in update process detail for batch foc approval file processing  " + e.getMessage());
	                }
	                log.errorTrace(METHOD_NAME, e);
			}
			con = null;
		}
       /* if (log.isDebugEnabled()) {
            log.debug("processFile", "Exited forward=" + forward);
        }*/
        final StringBuffer OtherInfo = new StringBuffer("CURRENT LEVEL=" + theForm.getRequestType());
        if (msgArr != null) {
            if (msgArr.length < 2) {
                OtherInfo.append(", DISSCARDED RECORD = " + msgArr[0]);
            } else if (msgArr.length > 1) {
                OtherInfo.append(", PROCESSED RECORD = " + msgArr[0]);
                OtherInfo.append(", DISSCARDED RECORD = " + msgArr[1]);
            }
        }
        BatchO2CFileProcessLog.o2cBatchMasterLog(METHOD_NAME, focBatchMasterVO, "FINALLY BLOCK : Process completed", OtherInfo.toString());
    }
    return response;
    
    }


}
