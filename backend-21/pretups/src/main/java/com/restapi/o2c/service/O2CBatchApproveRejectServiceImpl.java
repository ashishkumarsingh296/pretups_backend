package com.restapi.o2c.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpStatus;
import org.spring.custom.action.Globals;
//import org.apache.struts.action.ActionForward;
import com.btsl.util.MessageResources;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListService;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListServiceImpl;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchFocFileProcessLog;
import com.btsl.pretups.logging.BatchO2CProcessLog;
import com.btsl.pretups.logging.DirectPayOutErrorLog;
import com.btsl.pretups.logging.DirectPayOutSuccessLog;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.xl.ExcelRW;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.FOCBatchTransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebDAO;


@Service("O2CBatchApproveRejectServiceI")
public class O2CBatchApproveRejectServiceImpl implements O2CBatchApproveRejectServiceI {

	
	protected final Log log = LogFactory.getLog(getClass().getName());
	ArrayList<MasterErrorList> inputValidations=null;
	
	public String getExtensionByApacheCommonLib(String filename) {
  	    return FilenameUtils.getExtension(filename);
  	}
	
	private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
	
	@Override
	public O2CBatchApRejTransferResponse processO2CApproveOrReject(
			O2CBulkApprovalOrRejectRequestVO o2cBulkApprovalOrRejectRequestVO, String msisdn, OperatorUtilI calculator,
			Locale locale, Connection con, String serviceType, String requestIDStr, HttpServletRequest httprequest,
			MultiValueMap<String, String> headers, HttpServletResponse responseSwag) throws BTSLBaseException {
		O2CBulkApprovalOrRejectRequestData data =	o2cBulkApprovalOrRejectRequestVO.getO2CBulkApprovalOrRejectRequestData();
		ChannelUserVO senderVO = null;
		O2CBatchApRejTransferResponse response = new O2CBatchApRejTransferResponse();
		final String methodName = "processO2CApproveOrReject";
        String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORO2C;
        // check the external txn id is mandatory or not at this level
        if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("1") != -1) {
        	externalTxnMandatory = PretupsI.YES;
        }
   
        String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
        final BatchO2CTransferWebDAO batchO2CTransferwebDAO = new BatchO2CTransferWebDAO();
        
        ArrayList o2cBatchMasterVOList = null;
    	try {
		 senderVO = (ChannelUserVO)new UserDAO().loadUsersDetails(con, msisdn);
		 senderVO.setUserPhoneVO(new UserDAO().loadUserPhoneVO(con, senderVO.getUserID()));
		 inputValidations = new ArrayList<MasterErrorList>();
		 //if(PretupsI.YES.equals(senderVO.getCategoryVO().getSmsInterfaceAllowed()) && PretupsI.YES.equals(senderVO.getUserPhoneVO().getPinRequired()))
		 // Removing pin validation
			
				 if(BTSLUtil.isNullString(data.getProduct())){
					 MasterErrorList masterErrorList = new MasterErrorList();
						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_PRODUCTCODE,null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_PRODUCTCODE);
						masterErrorList.setErrorMsg(msg);
						inputValidations.add(masterErrorList);
					
					} else {		
					 	boolean isProd=false;
						ArrayList <C2sBalanceQueryVO>prodList1 =new ProductTypeDAO().getProductsDetails(con);
						for(C2sBalanceQueryVO prod1:prodList1) {
					    	  if(data.getProduct().equalsIgnoreCase(prod1.getProductCode())) {
					              data.setProduct(prod1.getProductCode());
					              isProd=true;
					    		  break;
					    	  }
					       }
						if(!isProd)
						{
							 MasterErrorList masterErrorList = new MasterErrorList();
								String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PRODUCTS_NOT_FOUND,null);
								masterErrorList.setErrorCode(PretupsErrorCodesI.PRODUCTS_NOT_FOUND);
								masterErrorList.setErrorMsg(msg);
								inputValidations.add(masterErrorList);
						}
				 }
				 int MAX_APPLEVEL = (((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.O2C_ORDER_APPROVAL_LVL)).intValue());
			        
				 
				 String str = data.getRequestType();
			        if(BTSLUtil.isNullString(str)) {
			        	MasterErrorList masterErrorList = new MasterErrorList();
			        	//String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PRODUCTS_NOT_FOUND,null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.PRODUCTS_NOT_FOUND);
						masterErrorList.setErrorMsg("Approval level can not be blank");
						inputValidations.add(masterErrorList);
			        }else {
			        	if(MAX_APPLEVEL <= 1 && "approval2".equals(str)) {
			        		MasterErrorList masterErrorList = new MasterErrorList();
			        		//String msg=RestAPIStringParser.getMessage(locale, ,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.PRODUCTS_NOT_FOUND);
							masterErrorList.setErrorMsg("Can not approve. Max approval level is 1");
							inputValidations.add(masterErrorList);
			        		
			        	}
			        	
			        	if(MAX_APPLEVEL <= 2 && "approval3".equals(str)) {
			        		MasterErrorList masterErrorList = new MasterErrorList();
			        		//String msg=RestAPIStringParser.getMessage(locale, ,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.PRODUCTS_NOT_FOUND);
							masterErrorList.setErrorMsg("Can not approve. Max approval level is 2");
							inputValidations.add(masterErrorList);
			        		
			        	}
			        	
			        }
				 
				 if(!BTSLUtil.isNullOrEmptyList(inputValidations)) {
					  response.setStatus("400");
					  response.setService(serviceType + "RESP");
					  response.setErrorMap(new ErrorMap());
					  response.getErrorMap().setMasterErrorList(inputValidations);
					  response.setMessageCode(PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS);;
					  response.setReferenceId(0);
					  response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS, null)); 
					  responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					  return response;
					 }
		
		            final O2CBatchWithdrawWebDAO o2cBatchTransferWebDAO = new O2CBatchWithdrawWebDAO();

		            
		            
		           
		 if(!data.getService().equalsIgnoreCase("T")) {
		        if(data.getRequestType().equals("approval1")) {
		        	o2cBatchMasterVOList  =  o2cBatchTransferWebDAO.loadBatchO2CMasterDetails(con, senderVO.getUserID(), statusUsed,
			                PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
		        	
		        }else  if(data.getRequestType().equals("approval2")) { 
		        	statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
		        	o2cBatchMasterVOList = o2cBatchTransferWebDAO.loadBatchO2CMasterDetails(con, senderVO.getUserID(), statusUsed,
		                    PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
		        }else {
		        	statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
		        	o2cBatchMasterVOList = o2cBatchTransferWebDAO.loadBatchO2CMasterDetails(con, senderVO.getUserID(), statusUsed,
		                    PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
		        } 
		 } else {
        if(data.getRequestType().equals("approval1")) {
        	o2cBatchMasterVOList  = batchO2CTransferwebDAO.loadO2CBatchMasterDetails(con, senderVO.getUserID(), statusUsed,
            PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, PretupsI.TRANSFER_TYPE_O2C, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        }else  if(data.getRequestType().equals("approval2")) { 
        	statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
        	o2cBatchMasterVOList = batchO2CTransferwebDAO.loadO2CBatchMasterDetails(con, senderVO.getUserID(), statusUsed,
                    PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2, PretupsI.TRANSFER_TYPE_O2C, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        }else {
        	statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
        	
        	o2cBatchMasterVOList = batchO2CTransferwebDAO.loadO2CBatchMasterDetails(con, senderVO.getUserID(), statusUsed,
                    PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3, PretupsI.TRANSFER_TYPE_O2C, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        }
       }
        
		response = processBatchApprove(response,senderVO,data,httprequest,responseSwag,serviceType,MAX_APPLEVEL,o2cBatchMasterVOList);
	   
    	
    	}catch(BTSLBaseException be) {
	        log.error("processRequest", "Exceptin:e=" + be);
	        log.errorTrace(methodName, be);
	        
	   	    String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
	   	    if(BTSLUtil.isNullString(msg)) {
	   	      msg =	PretupsRestUtil.getMessageString(be.getMessageKey());
	   	    }
	        response.setMessageCode(be.getMessageKey());
	        response.setMessage(msg);
			response.setService(serviceType + "RESP");
			response.setReferenceId(0);

	    	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	    		responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
		         response.setStatus("401");
	        }
	       else{
	    	   responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	       		response.setStatus("400");
	       }
	    }catch (Exception e) {
	        log.debug("processRequest", e);
	        response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
					null);
	       
	        response.setMessage(resmsg);
			response.setService(serviceType + "RESP");
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	    	response.setStatus("400");
	    	response.setReferenceId(0);
	        log.error(methodName, "Exceptin:e=" + e);
		}finally {
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Exiting:=" + methodName);
	        }
	    
	    }
		return response;
	}
	
	
	
	 /**
     * This method will be used to approve the whole batch.
     * This method will be called on click of approve button on the third
     * screen.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
	 * @throws BTSLBaseException 
     */
    private O2CBatchApRejTransferResponse processBatchApprove(O2CBatchApRejTransferResponse response, ChannelUserVO senderVO,O2CBulkApprovalOrRejectRequestData data,HttpServletRequest httprequest,HttpServletResponse responseSwag,String serviceType,int MAX_APP_LVL,ArrayList batchAppList) throws BTSLBaseException  {
        final String METHOD_NAME = "processBatchApprove";
        if (log.isDebugEnabled()) {
            log.debug("processBatchApprove", "Entered");
        }
        //ActionForward forward = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
        ArrayList errorList = null;
        O2CBatchMasterVO o2cBatchMasterVO = null;
        FOCBatchMasterVO widrawBatchMasterVO = null;
        String currentLevel = null;
        final String arr[] = new String[1];
        boolean showLogs = true;
        ProcessStatusVO processVO = null;
        boolean processRunning = true;
        O2CBatchMasterVO o2CBatchMasterVO = null;
        try {
            // as to check the status of the batch o2c process into the table so
            // that only
            // one instance should be executed for batch o2c
            final ProcessBL processBL = new ProcessBL();
            final UserVO userVO =senderVO;
            try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
                processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.O2C_BATCH_PROCESS_ID,userVO.getNetworkID());
            } catch (BTSLBaseException e) {
                log.error("processBatchApprove", "Exception:e=" + e);
                log.errorTrace(METHOD_NAME, e);
                processRunning = false;
                throw new BTSLBaseException(this, "processBatchApprove", "batcho2c.processfile.error.alreadyexecution");
            }
            if (processVO != null && !processVO.isStatusOkBool()) {
                processRunning = false;
                throw new BTSLBaseException(this, "processBatchApprove", "batcho2c.processfile.error.alreadyexecution");
            }
            mcomCon.partialCommit();
            processVO.setNetworkCode(userVO.getNetworkID());
            boolean sendOrderToApproval = false;
            boolean isRejectReq =false;

            if(data.getRequest().equalsIgnoreCase(("reject"))){
            	isRejectReq = true;
            }
            String statusUsed = null;
            String remark = null;
            // Set the status to be used and also set if order is closed at this
            // level
            
            ErrorMap errorMap = new ErrorMap();
            if ("approval1".equals(data.getRequestType())) {
                remark = data.getRemarks();
                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
                if (MAX_APP_LVL <= 1 && !isRejectReq) {
                    sendOrderToApproval = true;
                }
            }
            
            if ("approval2".equals(data.getRequestType())) {
                remark = data.getRemarks();
                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
                if (MAX_APP_LVL == 2 && !isRejectReq) {
                    sendOrderToApproval = true;
                    currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE;
                }else {
                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
                }
            }
            if ("approval3".equals(data.getRequestType())) {
                remark = data.getRemarks();
                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "'";
                if(!isRejectReq) {
                    sendOrderToApproval = true;
                }
                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE;
            }
            if (!BTSLUtil.isNullString(remark) && remark.length() > 100) {
                showLogs = false;
                throw new BTSLBaseException("batcho2c.batcho2capprove.msg.invalidlengthremark", "batchApproveO2CTransfer");
            }
            // get the login user information from session
            final ChannelUserVO channelUserVO = senderVO;
            final BatchO2CTransferWebDAO batchO2CTransferwebDAO = new BatchO2CTransferWebDAO();
            final O2CBatchWithdrawWebDAO o2cBatchTransferWebDAO = new O2CBatchWithdrawWebDAO();
            LinkedHashMap downloadDataMap = null;
            HashMap downloadDataMap1 = null;
            Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
            
            
            
            if(data.getService().equalsIgnoreCase("T")) {
            	o2cBatchMasterVO =	getBatchDetails(batchAppList,data.getBatchId());
            	if(o2cBatchMasterVO == null) {
              	   throw new BTSLBaseException(this, "processBatchApprove", "batcho2c.batcho2capprove.msg.bachprocessfailed");
                 }
            	
            }else {
            	widrawBatchMasterVO = getBatchDetailsComm(batchAppList,data.getBatchId());
            	if(widrawBatchMasterVO == null) {
               	   throw new BTSLBaseException(this, "processBatchApprove", "batcho2c.batcho2capprove.msg.bachprocessfailed");
                  }
            }
         
            
            if(o2cBatchMasterVO !=  null) {
            downloadDataMap = batchO2CTransferwebDAO.loadBatchO2CItemsMap(con, data.getBatchId(), statusUsed, PretupsI.TRANSFER_TYPE_O2C,
                    PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            } else {
            	downloadDataMap = o2cBatchTransferWebDAO.loadBatchItemsMaprest(con, data.getBatchId(), statusUsed);
            }
            
            
            if (downloadDataMap != null && !downloadDataMap.isEmpty()) {
                BatchO2CItemsVO batchO2CItemsVO = null;
                final Iterator iterator = downloadDataMap.keySet().iterator();
                String key = null;
                int i = 1;
                boolean isNotApprovalLevel1 = true;
	            String exelFileID = null;
                while (iterator.hasNext()) {
                    key = (String) iterator.next();
                    batchO2CItemsVO = (BatchO2CItemsVO) downloadDataMap.get(key);
                    batchO2CItemsVO.setRecordNumber(i + 1);
                    if ("approval1".equals(data.getRequestType())) {
                        batchO2CItemsVO.setFirstApproverRemarks(remark);
                        batchO2CItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        exelFileID = ExcelFileIDI.BATCH_O2C_APPRV1;
		                isNotApprovalLevel1 = false;
		                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
                    } else if ("approval2".equals(data.getRequestType())) {
                        batchO2CItemsVO.setSecondApproverRemarks(remark);
                        batchO2CItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                        exelFileID = ExcelFileIDI.BATCH_O2C_APPRV2;
		                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
                    } else if ("approval3".equals(data.getRequestType())) {
                        batchO2CItemsVO.setThirdApproverRemarks(remark);
                        exelFileID = ExcelFileIDI.BATCH_O2C_APPRV3;
                        batchO2CItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                    }
                       
                    
                if(isRejectReq) {
               	 batchO2CItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                   } else if (sendOrderToApproval) {
                  batchO2CItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                  }
                    
                    i++;
                }
                String name = "";
                if(o2cBatchMasterVO !=  null) {
		           name= o2cBatchMasterVO.getBatchFileName();
                } else {
                   name= widrawBatchMasterVO.getBatchFileName();
                }
		          String extension ="xls";
		           String filePath = Constants.getProperty("DownloadFilePathForO2CApproval");
		           try {
		                final File fileDir = new File(filePath);
		                if (!fileDir.isDirectory()) {
		                    fileDir.mkdirs();
		                }
		            } catch (Exception e) {
		                log.errorTrace("loadDownloadFile", e);
		                log.error("loadDownloadFile", "Exception" + e.getMessage());
		                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "error");
		            }
		           String fileType= null;
		           String fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime();
		           if(extension.equals("csv")) {
		        	    fileType= "."+extension;
		        	    fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
			        	writeToCsv(downloadDataMap, filePath+fileName,isNotApprovalLevel1);
			          }
		            else if (extension.equals("xlsx")) {
		            	String fileArr[][] = constructFileArrForDownload(downloadDataMap, isNotApprovalLevel1);
		            	fileType= "."+extension;
				        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

		            	 ExcelRW excelRW = new ExcelRW();
				         excelRW
				             .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
		            }
		            else {
		            	 String fileArr[][] = constructFileArrForDownload(downloadDataMap, isNotApprovalLevel1);
		            	fileType= ".xls";
		            	fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

		            	 ExcelRW excelRW = new ExcelRW();
				            // Write the excel file for download
				            excelRW
				                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
				           
		            }
		            File download =new File(filePath+fileName);
					byte[] fileContent = FileUtils.readFileToByteArray(download);
			   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
				
             
                // Check if batch is modified or not.
                boolean isModified = false;
                if(o2cBatchMasterVO != null) {
                	isModified = batchO2CTransferwebDAO.isBatchO2CModified(con, o2cBatchMasterVO.getModifiedOn().getTime(), data.getBatchId());
                } else {
                	isModified = o2cBatchTransferWebDAO.isBatchModified(con, widrawBatchMasterVO.getModifiedOn().getTime(), data.getBatchId());
                	
                }
                if (isModified) {
                    throw new BTSLBaseException(this, "processBatchApprove", "batcho2c.batcho2capprovereject.msg.batchmodify", "firstPage");
                } else {
                	 int updateCount = 0 ;
                	  if(o2cBatchMasterVO != null) {
                      updateCount = batchO2CTransferwebDAO.updateBatchO2CStatus(con, data.getBatchId(),
                        PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN);
                	  } else {
                		  updateCount = o2cBatchTransferWebDAO.updateBatchStatus(con, data.getBatchId(),
                                  PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
                	  }
                    if (updateCount <= 0) {
                        mcomCon.partialRollback();
                        if(isRejectReq)
                        throw new BTSLBaseException(this, "processBatchApprove", "error.general.processing", "firstPage");
                        else
                        throw new BTSLBaseException(this, "cancelOrder", "error.general.processing", "firstPage");	
                    }
                    mcomCon.partialCommit();
                    // If order is not closed at this level then call
                    // processOrderByBatch (1st level apprvl)
                    if (!sendOrderToApproval) {
                    if(o2cBatchMasterVO != null) {
                    	errorList = batchO2CTransferwebDAO.processOrderByBatch(con, downloadDataMap, currentLevel, channelUserVO.getUserID(), locale, data.getLanguage1(), data.getLanguage2());
                    	}else {
                        	errorList = o2cBatchTransferWebDAO.processOrderByBatch(con, downloadDataMap, currentLevel, channelUserVO.getUserID(), locale, data.getLanguage1(), data.getLanguage2());	
                    	}
                                            	
                        BTSLMessages btslMessage = null;

                        // success message
                        if (errorList == null || errorList.isEmpty()) {
                        	String msg = null;
                        	if(isRejectReq) {
                 				 msg=PretupsRestUtil.getMessageString( "batcho2c.batcho2creject.msg.bachprocesssuccessfully", new String[]{ String.valueOf(downloadDataMap.size())});
                 				response.setMessageCode("batcho2c.batcho2creject.msg.bachprocesssuccessfully");
                        	}else {
                				 msg=PretupsRestUtil.getMessageString( "batcho2c.batcho2capprove.msg.bachprocessfailedsuccess", new String[]{ String.valueOf(downloadDataMap.size())});
                				 response.setMessageCode("batcho2c.batcho2capprove.msg.bachprocessfailedsuccess");
                        	}
             				response.setStatus("200");
             				response.setBatchID(String.valueOf( data.getBatchId()));
             				response.setMessage(msg);
             				response.setProcessedRecs((String.valueOf(downloadDataMap.size())));
                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
                       	    response.setReferenceId(downloadDataMap.size());
             				responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
                        } else if (errorList.size() == downloadDataMap.size()) {
                            response.setProcessedRecs((String.valueOf(errorList.size())));
                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
                            if (errorList != null && !errorList.isEmpty()) {
                                int errorListSize = errorList.size();
                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
                                    {
                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                  		            	MasterErrorList masterErrorList = new MasterErrorList();
                  						masterErrorList.setErrorCode(errorVO.getIDValue());
                  						String msg = errorVO.getOtherInfo2();
                  						masterErrorList.setErrorMsg(msg);
                  						masterErrorLists.add(masterErrorList);
                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
                  						if(errorMap.getRowErrorMsgLists() == null)
                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                  						writeFileForResponseO2c( response,  errorMap,  responseSwag, encodedString, extension,false);
                                    }
                                }
                            }
                            
                        	response.setStatus("400");
            	            response.setErrorMap(errorMap);
            				response.setBatchID(String.valueOf(o2cBatchMasterVO.getBatchId()));
            				response.setMessageCode(PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS);
                      	    response.setReferenceId(0);
                      	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
            				
                         
                            if(!isRejectReq) {
                            	response.setMessage(PretupsRestUtil.getMessageString("batcho2c.batcho2capprove.msg.bachprocessfailed"));
                            } else {
                            	response.setMessage(PretupsRestUtil.getMessageString("batcho2c.batcho2creject.msg.bachprocessfailed"));
                            }
                            return response;
                        }
                        // success message with showing invalid MSISDN list
                        else {
                            arr[0] = String.valueOf(downloadDataMap.size() - errorList.size());
                            response.setProcessedRecs((String.valueOf(errorList.size())));
                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
                            
                            
                            if (errorList != null && !errorList.isEmpty()) {
                                int errorListSize = errorList.size();
                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
                                    {
                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                  		            	MasterErrorList masterErrorList = new MasterErrorList();
                  						masterErrorList.setErrorCode(errorVO.getIDValue());
                  						String msg = errorVO.getOtherInfo2();
                  						masterErrorList.setErrorMsg(msg);
                  						masterErrorLists.add(masterErrorList);
                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
                  						if(errorMap.getRowErrorMsgLists() == null)
                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                  						writeFileForResponseO2c( response,  errorMap,  responseSwag, encodedString, extension,true);
                                    }
                                }
                            }
                            
                        	response.setStatus("400");
            	            response.setErrorMap(errorMap);
            				response.setBatchID(String.valueOf(o2cBatchMasterVO.getBatchId()));
            				response.setMessageCode(PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS);
                      	    response.setReferenceId(0);
                      	    responseSwag.setStatus(HttpStatus.SC_OK);
            				
                         
                      	  if(!isRejectReq) {
                          	response.setMessage(PretupsRestUtil.getMessageString("batcho2c.batcho2capprove.msg.bachprocessfailedsuccess", arr));
                          }else {
                          	response.setMessage(PretupsRestUtil.getMessageString("batcho2c.batcho2creject.msg.bachprocessfailedsuccess", arr));
                          }
                            return response;
                        }
                    } else {
                    	if(o2cBatchMasterVO != null) {
                        errorList = batchO2CTransferwebDAO.closeOrderByBatch(con, downloadDataMap, currentLevel, channelUserVO.getUserID(), o2cBatchMasterVO,locale, data.getLanguage1(), data.getLanguage2());
                    	} else {
                    		 errorList = o2cBatchTransferWebDAO.o2cBatchCloseRest(con, widrawBatchMasterVO, downloadDataMap, widrawBatchMasterVO.getBatchId(),locale, currentLevel, channelUserVO.getUserID());
                    	}
                        if (errorList == null || errorList.isEmpty()) {
                        	String msg=PretupsRestUtil.getMessageString( "batcho2c.batcho2capprove.msg.bachprocesssuccessfullyclose", new String[]{ String.valueOf(downloadDataMap.size())});
             				response.setStatus("200");
             				response.setBatchID(String.valueOf( data.getBatchId()));
             				response.setProcessedRecs((String.valueOf(downloadDataMap.size())));
                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
             				response.setMessage(msg);
             				response.setMessageCode(PretupsErrorCodesI.BATCH_O2C_TRF_SUCCESS);
                       	    response.setReferenceId(downloadDataMap.size());
             				responseSwag.setStatus(HttpStatus.SC_OK);
                        } else if (errorList.size() == downloadDataMap.size()) {
                        	 response.setProcessedRecs((String.valueOf(errorList.size())));
                             response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
                             
                             if (errorList != null && !errorList.isEmpty()) {
                                 int errorListSize = errorList.size();
                                 for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
                                 	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
                   		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
                                     {
                   		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                   		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                   		            	MasterErrorList masterErrorList = new MasterErrorList();
                   						masterErrorList.setErrorCode(errorVO.getIDValue());
                   						String msg = errorVO.getOtherInfo2();
                   						masterErrorList.setErrorMsg(msg);
                   						masterErrorLists.add(masterErrorList);
                   						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
                   						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
                   						rowErrorMsgLists.setRowName(errorVO.getCodeName());
                   						if(errorMap.getRowErrorMsgLists() == null)
                   							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
                   						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                   						writeFileForResponseO2c( response,  errorMap,  responseSwag, encodedString, extension,false);
                                     }
                                 }
                             }
                             
                         	response.setStatus("400");
             	            response.setErrorMap(errorMap);
							if (o2cBatchMasterVO != null) {
								response.setBatchID(String.valueOf(o2cBatchMasterVO.getBatchId()));
							}else{
								response.setBatchID(String.valueOf(widrawBatchMasterVO.getBatchId()));
							}
             				response.setMessageCode(PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS);
                       	    response.setReferenceId(0);
                       	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                       		response.setMessage(PretupsRestUtil.getMessageString("batcho2c.batcho2capprove.msg.bachprocessfailed"));
                            return response;
                         
                        }
                        // success message with showing invalid MSISDN list
                        else {
                            arr[0] = String.valueOf(downloadDataMap.size() - errorList.size());
                            response.setProcessedRecs((String.valueOf(errorList.size())));
                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
                            if (errorList != null && !errorList.isEmpty()) {
                                int errorListSize = errorList.size();
                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
                                    {
                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                  		            	MasterErrorList masterErrorList = new MasterErrorList();
                  						masterErrorList.setErrorCode(errorVO.getIDValue());
                  						String msg = errorVO.getOtherInfo2();
                  						masterErrorList.setErrorMsg(msg);
                  						masterErrorLists.add(masterErrorList);
                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
                  						if(errorMap.getRowErrorMsgLists() == null)
                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                  						writeFileForResponseO2c( response,  errorMap,  responseSwag, encodedString, "xls",true);
                                    }
                                }
                            }
                            
                        	response.setStatus("400");
            	            response.setErrorMap(errorMap);
            				response.setBatchID(String.valueOf(o2cBatchMasterVO.getBatchId()));
            				response.setMessageCode(PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS);
                      	    response.setReferenceId(0);
                      	    responseSwag.setStatus(HttpStatus.SC_OK);
                      		response.setMessage(PretupsRestUtil.getMessageString("batcho2c.batcho2capprove.msg.bachprocessfailedsuccessclose"));
                            return response;
                           
                        }
                    }// end of else
                }// end of else
            }// end of if
            else {
            	
            	  throw new BTSLBaseException("batcho2c.batcho2capprove.msg.nochildrecordfound", "batchApproveO2CTransfer");
            }
        } catch(BTSLBaseException be){
	  		if(log.isDebugEnabled())
	  		    log.debug(METHOD_NAME, "Exceptin:be=" + be);
	            log.error(METHOD_NAME, "Exceptin:be=" + be);
     	        throw be;
         }catch (Exception e) {
      	    log.error("processBatchApprove", "Exception: " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
        if (showLogs) {
            BatchO2CProcessLog.o2cBatchMasterLog("processBatchApprove", o2cBatchMasterVO, "FAIL : Error message is =" + e.getMessage(), "CURRENT LEVEL=" + currentLevel);
        }
      	throw new BTSLBaseException(PretupsErrorCodesI.REQ_NOT_PROCESS);
  	} finally {
            // as to make the status of the batch o2c process as complete into
            // the table so that only
            // one instance should be executed for batch o2c

            if (processRunning) {
                try {
                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                        mcomCon.finalCommit();
                    } else {
                        mcomCon.finalRollback();
                    }
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.error("processBatchApprove", " Exception in update process detail for batch o2c approval process " + e.getMessage());
                    }
                    log.errorTrace(METHOD_NAME, e);
                }
            }
            // ends here
            // if connection not null then close the connection
			if (mcomCon != null) {
				mcomCon.close("O2CBatchTrfApprovalAction#processBatchApprove");
				mcomCon = null;
			}
            if (showLogs) {
                BatchO2CProcessLog.o2cBatchMasterLog("processBatchApprove", o2cBatchMasterVO, "FINALLY BLOCK : Batch processed",
                    "CURRENT LEVEL=" + currentLevel + ", PROCESSED RECORD=" + arr[0]);
            }
            /*if (log.isDebugEnabled()) {
                log.debug("processBatchApprove", "Exited forward=" + forward);
            }*/
        }
     //   return forward;
		return response;
    }

	
	
	
    O2CBatchMasterVO getBatchDetails(ArrayList<O2CBatchMasterVO> batchAppList,String batchID){
    	
    	
    	for(O2CBatchMasterVO  obj : batchAppList) {
    		String str = obj.getBatchId();
    		if(batchID.equals(str)) {
    			return obj;
    		}
    	}
    	
    	
		return null;
    	
    }
    
    
    @SuppressWarnings("rawtypes")
	@Override
	public O2CBatchApRejTransferResponse processBulkCommApproveOrReject(CommisionBulkApprovalOrRejectRequestVO commisionBulkApprovalOrRejectRequestVO
				,String msisdn,Locale locale,Connection con, HttpServletRequest httprequest,
				MultiValueMap<String, String> headers,
				HttpServletResponse responseSwag) throws BTSLBaseException {
		
		 CommisionBulkApprovalOrRejectRequestData data =	commisionBulkApprovalOrRejectRequestVO.getCommisionBulkApprovalOrRejectRequestData();
		ChannelUserVO senderVO = null;
		O2CBatchApRejTransferResponse response = new O2CBatchApRejTransferResponse();
		final String methodName = "processBulkCommApproveOrReject";
        String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORO2C;
        // check the external txn id is mandatory or not at this level
        if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("1") != -1) {
        	externalTxnMandatory = PretupsI.YES;
        }
   
        final BatchO2CTransferWebDAO batchO2CTransferwebDAO = new BatchO2CTransferWebDAO();
        
        ArrayList focBatchMasterVOList = null;
        final FOCBatchTransferDAO focBatchTransferDAO = new FOCBatchTransferDAO();
        final FOCBatchTransferWebDAO focBatchTransferWebDAO = new FOCBatchTransferWebDAO();
    	try {
		 senderVO = (ChannelUserVO)new UserDAO().loadUsersDetails(con, msisdn);
		 senderVO.setUserPhoneVO(new UserDAO().loadUserPhoneVO(con, senderVO.getUserID()));
		 inputValidations = new ArrayList<MasterErrorList>();
		 //if(PretupsI.YES.equals(senderVO.getCategoryVO().getSmsInterfaceAllowed()) && PretupsI.YES.equals(senderVO.getUserPhoneVO().getPinRequired()))
		 // Removing pin validation
		 
		  if(false)	
		 {
             if(BTSLUtil.isNullString(data.getPin()))
             {
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN,null);
					  response.setStatus("400");
					  response.setService("COMMBATCHAPPROVALRESP");
					  response.setMessage(msg);
	                  response.setReferenceId(0);
					  response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
					  responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					 return response;
             }
             else{
	                try {
						ChannelUserBL.validatePIN(con, senderVO, data.getPin());
					} catch (BTSLBaseException be) {
						if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
								|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
								OracleUtil.commit(con);
						}
						String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
						response.setStatus("400");
						  response.setService("COMMBATCHAPPROVALRESP");
						  response.setMessage(msg);
						  response.setMessageCode(be.getMessageKey());
						  responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		                  response.setReferenceId(0);
						  return response;
					}
               }
			}
			
		  
		   ArrayList<FOCBatchMasterVO> listFocBatchVO =  focBatchTransferDAO.getComissionWalletType(con,data.getBatchId());
		   String commwalletType =null; 
		   String focOrCommPayout =null;
		   if (listFocBatchVO.size()>0) {
			   FOCBatchMasterVO fOCBatchMasterVO =  (FOCBatchMasterVO) listFocBatchVO.get(0);
			   commwalletType =fOCBatchMasterVO.getWallet_type();
			   focOrCommPayout= fOCBatchMasterVO.getFocOrCommPayout();
			   
		   }
		   
		   
				// int MAX_APPLEVEL = (((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.FOC_ORDER_APPROVAL_LVL)).intValue());
		  			int MAX_APPLEVEL;
				  
		  			//using prefrence for direct payout or foc transfer
		  			//if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue()) {
		  			
		  		if(PretupsI.DP_TRANSFER.equals(focOrCommPayout)) {	
		  				
					  MAX_APPLEVEL = SystemPreferences.DP_ORDER_APPROVAL_LVL;
				  }else {
					  MAX_APPLEVEL  =((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.FOC_ORDER_APPROVAL_LVL)).intValue();
				  }
				  
				 
				 String str = data.getRequestType();
			        if(BTSLUtil.isNullString(str)) {
			        	MasterErrorList masterErrorList = new MasterErrorList();
			        	//String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PRODUCTS_NOT_FOUND,null);
			        	String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_APPROVAL,null);
			        	masterErrorList.setErrorCode(PretupsErrorCodesI.NO_APPROVAL);
						masterErrorList.setErrorMsg(msg);
						inputValidations.add(masterErrorList);
			        }else {
			        	if(MAX_APPLEVEL <= 1 && "approval2".equals(str)) {
			        		MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MAX_LVL1,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.MAX_LVL1);
							masterErrorList.setErrorMsg(msg);
							inputValidations.add(masterErrorList);
			        		
			        	}
			        	
			        	if(MAX_APPLEVEL <= 2 && "approval3".equals(str)) {
			        		MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MAX_LVL2,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.MAX_LVL2);
							masterErrorList.setErrorMsg(msg);
							inputValidations.add(masterErrorList);
			        		
			        	}
			        	
			        }
			        
			        if (!BTSLUtil.isNullString(data.getLanguage1()) && data.getLanguage1().length() > 30) {
			        	MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_LENGTH_NOTLANG,null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_LENGTH_NOTLANG);
						masterErrorList.setErrorMsg(msg);
						inputValidations.add(masterErrorList);
	                }
	                if (!BTSLUtil.isNullString( data.getLanguage2()) && data.getLanguage2().length() > 30) {
	                	MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_LENGTH_NOTLANG,null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_LENGTH_NOTLANG);
						masterErrorList.setErrorMsg(msg);
						inputValidations.add(masterErrorList);
	                }


	                  if (!BTSLUtil.isNullString(data.getRemarks()) && data.getRemarks().length() > 100) {
		                MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_LENGTH_REMARKS,null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_LENGTH_REMARKS);
						masterErrorList.setErrorMsg(msg);
						inputValidations.add(masterErrorList);
		            }
	                  
	                  
	                  if (BTSLUtil.isNullString(data.getRequest()) ) {
			                MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
							masterErrorList.setErrorMsg(msg);
							inputValidations.add(masterErrorList);
			            }else if(!data.getRequest().equalsIgnoreCase("approve") && !data.getRequest().equalsIgnoreCase("reject")) {
			            	
			            	
			                MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
							masterErrorList.setErrorMsg(msg);
							inputValidations.add(masterErrorList);
			            	
			            	
			            }
	                  
	                  
	                  if (BTSLUtil.isNullString(data.getRequestType())) {
			                MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
							masterErrorList.setErrorMsg(msg);
							inputValidations.add(masterErrorList);
			            }
	                  else
	                  {
	                	  String Valid="N";
		                 if(data.getRequestType().equalsIgnoreCase("approval1")) {
		                	 Valid="Y";
		                 }else if(data.getRequestType().equalsIgnoreCase("approval2"))
		                 {
		                	 Valid="Y";
		                 }
		                 else if(data.getRequestType().equalsIgnoreCase("approval3"))
		                 {
		                	 Valid="Y";
		                 }
		                 
		                 if(Valid.equalsIgnoreCase("N")) {
		                	 MasterErrorList masterErrorList = new MasterErrorList();
								String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,null);
								masterErrorList.setErrorCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
								masterErrorList.setErrorMsg(msg);
								inputValidations.add(masterErrorList);
		                 }

	                  }
	                  
	                  
	                  if (BTSLUtil.isNullString(data.getBatchId())) {
			                MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
							masterErrorList.setErrorMsg(msg);
							inputValidations.add(masterErrorList);
			            }
	                  
	                  
	                  if (BTSLUtil.isNullString(data.getBatchName())) {
			                MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
							masterErrorList.setErrorMsg(msg);
							inputValidations.add(masterErrorList);
			            }
	                  
	                  
	               


				 
				 if(!BTSLUtil.isNullOrEmptyList(inputValidations)) {
					  response.setStatus("400");
					  response.setService("COMMBATCHAPPROVALRESP");
					  response.setErrorMap(new ErrorMap());
					  response.getErrorMap().setMasterErrorList(inputValidations);
					  response.setMessageCode(PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS);;
					  response.setReferenceId(0);
					  response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS, null)); 
					  responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					  return response;
					 }
		 
				 
			

       
        
        
        String serviceType="COMMBATCHAPPROVALRESP";
        
        //Which approval to call is system pref driven by default COM_PAY_OUT is set to direct payout commission approval
        //if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue()) {
        if(PretupsI.DP_TRANSFER.equals(focOrCommPayout)) {	  // During  Bulk payout initiation , Channel admin which commission wallet selects 
            
            if(data.getRequestType().equals("approval1")) {
            	
                final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
            	focBatchMasterVOList = focBatchTransferDAO.loadBatchDPMasterDetails(con, senderVO.getUserID(), statusUsed,
                    PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);}
            else  if(data.getRequestType().equals("approval2")) {
                final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";

            	focBatchMasterVOList = focBatchTransferDAO.loadBatchDPMasterDetails(con, senderVO.getUserID(), statusUsed,
                    PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);}
            else {
                final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";

            	focBatchMasterVOList = focBatchTransferDAO.loadBatchDPMasterDetails(con, senderVO.getUserID(), statusUsed,
                    PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
            }
            
            
            //calling approve or reject
        	if(data.getRequest().equalsIgnoreCase("approve")){
                processBatcDirectPayouthApprove(response,senderVO,data,httprequest,responseSwag,serviceType,MAX_APPLEVEL,focBatchMasterVOList);

            }else if(data.getRequest().equalsIgnoreCase("reject")) {
            	
            	cancelOrderDirectPayout(response,senderVO,data,httprequest,responseSwag,serviceType,MAX_APPLEVEL,focBatchMasterVOList);
            	
            }
         
        }
        else
        {
        	 if(data.getRequestType().equals("approval1")) {
             	
                 final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
                 focBatchMasterVOList = focBatchTransferWebDAO.loadBatchFOCMasterDetails(con, senderVO.getUserID(), statusUsed,
                         PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                 
        	 
        	 }
             else  if(data.getRequestType().equals("approval2")) {
            	 
            	 
                 final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";

                 focBatchMasterVOList = focBatchTransferWebDAO.loadBatchFOCMasterDetails(con, senderVO.getUserID(), statusUsed,PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
             
             }
             else {
            	 
            	 
                 final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";

                 focBatchMasterVOList = focBatchTransferWebDAO.loadBatchFOCMasterDetails(con, senderVO.getUserID(), statusUsed,
                         PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
             }
        	 
        	 
        	 
        	 //calling approve or reject
         	if(data.getRequest().equalsIgnoreCase("approve")){
         		processFocBatchApprove(response,senderVO,data,httprequest,responseSwag,serviceType,MAX_APPLEVEL,focBatchMasterVOList);

             }else if(data.getRequest().equalsIgnoreCase("reject")) {
             	
            	 cancelFOCOrder(response,senderVO,data,httprequest,responseSwag,serviceType,MAX_APPLEVEL,focBatchMasterVOList);
             	
             }
        	
        }
        
        	   
    	
    	}catch(BTSLBaseException be) {
    		log.error("processRequest", "Exceptin:e=" + be);
    		log.errorTrace(methodName, be);
	        
	   	    String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
	   	    if(BTSLUtil.isNullString(msg)) {
	   	      msg =	PretupsRestUtil.getMessageString(be.getMessageKey());
	   	    }
	        response.setMessageCode(be.getMessageKey());
	        response.setMessage(msg);
			response.setService("COMMBATCHAPPROVALRESP");
			response.setReferenceId(0);

	    	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	    		responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
		         response.setStatus("401");
	        }
	       else{
	    	   responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	       		response.setStatus("400");
	       }
	    }catch (Exception e) {
	    	log.debug("processRequest", e);
	        response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
					null);
	       
	        response.setMessage(resmsg);
			response.setService("COMMBATCHAPPROVALRESP");
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	    	response.setStatus("400");
	    	response.setReferenceId(0);
	    	log.error(methodName, "Exceptin:e=" + e);
		}finally {
	        if (log.isDebugEnabled()) {
	        	log.debug(methodName, "Exiting:=" + methodName);
	        }
	    
	    }
		return response;
	}
	
	
	 /**
     * 
     * TThis method will be used to approve the whole batch.
     * This method will be called on click of approve button 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
	 * @throws BTSLBaseException 
     */
	  private O2CBatchApRejTransferResponse processBatcDirectPayouthApprove(O2CBatchApRejTransferResponse response,
			  ChannelUserVO senderVO,CommisionBulkApprovalOrRejectRequestData data,HttpServletRequest httprequest,
			  HttpServletResponse responseSwag,String serviceType,int MAX_APP_LVL, ArrayList focBatchMasterVOList) throws BTSLBaseException {
	        final String methodName = "processBatcDirectPayouthApprove";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
	        //ActionForward forward = null;
			Connection con = null;
			MComConnectionI mcomCon = null;
	        ArrayList errorList = null;
	        FOCBatchMasterVO focBatchMasterVO = null;
	        String currentLevel = null;
	        final String arr[] = new String[1];
	        boolean showLogs = true;
	        ProcessStatusVO processVO = null;
	        boolean processRunning = true;
	        Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
	        O2CBatchApprovalDetailsResponse o2CBatchApprovalDetailsResponse=new O2CBatchApprovalDetailsResponse();
	        BatchO2CTransferDAO batchO2CTransferDAO =null;

	        try {
	           
	            // as to check the status of the batch foc process into the table so
	            // that only
	            // one instance should be executed for batch foc
	            final ProcessBL processBL = new ProcessBL();
	            batchO2CTransferDAO=new BatchO2CTransferDAO();
	           // final UserVO userVO1 = this.getUserFormSession(request);
	            try {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
	                processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.DP_BATCH_PROCESS_ID,senderVO.getNetworkID());
	            } catch (BTSLBaseException e) {
	                log.error(methodName, "Exception:e=" + e);
	                log.errorTrace(methodName, e);
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, "batchdirectpayout.processfile.error.alreadyexecution");
	            }
	            if (processVO != null && !processVO.isStatusOkBool()) {
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, "batchdirectpayout.processfile.error.alreadyexecution");
	            }
	            mcomCon.partialCommit();
	            processVO.setNetworkCode(senderVO.getNetworkID());
	            // ends here

	          //  final FOCBatchForm theForm = (FOCBatchForm) form;
	            boolean sendOrderToApproval = false;
	            String statusUsed = null;
	            String remark = null;
	            // Set the status to be used and also set if order is closed at this
	            // level
	            ErrorMap errorMap = new ErrorMap();
	            if ("approval1".equals(data.getRequestType())) {

	                remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
	                if (MAX_APP_LVL <= 1) {
	                    sendOrderToApproval = true;
	                }
	            }
	            if ("approval2".equals(data.getRequestType())) {
	                remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
	                if (MAX_APP_LVL <= 2) {
	                    sendOrderToApproval = true;
	                }
	            }
	            if ("approval3".equals(data.getRequestType())) {
	                remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
	                sendOrderToApproval = true;
	            }
	            if (!BTSLUtil.isNullString(remark) && remark.length() > 100) {
	                showLogs = false;
	                throw new BTSLBaseException("batchdirectpayout.batchfocapprove.msg.invalidlengthremark", "batchApproveDirectPayoutTransfer");
	            }
	            // If external txn number is mandatory at this level the process
	            // will not be permitted
	            // error will be thrown
//	            if (PretupsI.YES.equals(theForm.getExternalTxnMandatory())) {
//	                showLogs = false;
//	                DirectPayOutErrorLog.dpBatchMasterLog(methodName, focBatchMasterVO, "FAIL : Operation is not permited", "External Txn is required, CURRENT LEVEL=" +"REST");
//	                throw new BTSLBaseException("batchdirectpayout.batchfocapprove.msg.operationnotpermitted", "batchApproveDirectPayoutTransfer");
//	            }

	            // get the login user information from session
	           // final ChannelUserVO channelUserVO = (ChannelUserVO) getUserFormSession(request);
	            final FOCBatchTransferDAO focBatchTransferDAO = new FOCBatchTransferDAO();
	            LinkedHashMap downloadDataMap = null;

	           // load from the database
	            
	                downloadDataMap = focBatchTransferDAO.loadBatchItemsMap(con, data.getBatchId(), statusUsed);
	            
	            if (downloadDataMap != null && !downloadDataMap.isEmpty()) {
	                FOCBatchItemsVO focBatchItemVO = null;
	                final Iterator iterator = downloadDataMap.keySet().iterator();
	                String key = null;
	                boolean isNotApprovalLevel1 = true;
		            String exelFileID = null;
	                int i = 1;
	                while (iterator.hasNext()) {
	                    key = (String) iterator.next();
	                    focBatchItemVO = (FOCBatchItemsVO) downloadDataMap.get(key);
	                    focBatchItemVO.setRecordNumber(i + 1);
	                    
	                    if ("approval1".equals(data.getRequestType())) {
	                        focBatchItemVO.setFirstApproverRemarks(remark);
	                        focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
	                        exelFileID = ExcelFileIDI.BATCH_DP_APPRV1;
			                isNotApprovalLevel1 = false;
	                       
	                    } else if ("approval2".equals(data.getRequestType())) {
	                        focBatchItemVO.setSecondApproverRemarks(remark);
	                        focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
	                        exelFileID = ExcelFileIDI.BATCH_DP_APPRV2;
	                    } else if ("approval3".equals(data.getRequestType())) {
	                        focBatchItemVO.setThirdApproverRemarks(remark);
	                        focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
	                        exelFileID = ExcelFileIDI.BATCH_DP_APPRV3;
	                    }
	                    if (sendOrderToApproval) {
	                        focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	                    }
	                    i++;
	                }
	                
	                
	                String approvalLevelf ="";
	                
	                if ("approval1".equals(data.getRequestType())) {
                        
	                	approvalLevelf="1";
                    } else if ("approval2".equals(data.getRequestType())) {
                    	approvalLevelf="2";
                    } else if ("approval3".equals(data.getRequestType())) {
                    	
                    	approvalLevelf="3";
                        
                    }
	                
	                focBatchMasterVO = (FOCBatchMasterVO) getBatchDetailsComm(focBatchMasterVOList,data.getBatchId());
	                if(focBatchMasterVO == null) {
	             	  // throw new BTSLBaseException(this, "processBatcDirectPayouthApprove", "batchdirectpayout.batchfocapprove.msg.bachprocessfailed");
	             	  throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DET_NOT_FOUND_BULK);
	                }
	                

					String commWalletType =focBatchMasterVO.getWallet_type();

	               
	               
	                // Check if batch is modified or not.
	                final boolean isModified = focBatchTransferDAO.isBatchModified(con, focBatchMasterVO.getModifiedOn().getTime(), data.getBatchId());
	                if (isModified) {
	                    throw new BTSLBaseException(this, "processBatcDirectPayouthApprove", "batchdirectpayout.batchfocapprovereject.msg.batchmodify", "firstPage");

	                    
	                    // forward = super.handleMessage(btslMessage, request, mapping);
	                } else {
	                    final int updateCount = focBatchTransferDAO.updateBatchStatus(con, focBatchItemVO.getBatchId(), PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS,
	                        PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);
	                    if (updateCount <= 0) {
	                    	mcomCon.partialRollback();
	                        throw new BTSLBaseException(this, methodName, "error.general.processing", "firstPage");
	                    }
	                    mcomCon.partialCommit();
	                    // If order is not closed at this level then call
	                    // processOrderByDPBatch otehwise CloseOrderByBatch method
	                    if (!sendOrderToApproval) {
	                        errorList = focBatchTransferDAO.processOrderByDPBatchRest(con, downloadDataMap, currentLevel,
	                        		senderVO.getUserID(),locale, data.getLanguage1(), data.getLanguage2());
	                       // theForm.setErrorList(errorList);
	                        BTSLMessages btslMessage = null;
	                        
	                        // success message
	                        if (errorList == null || errorList.isEmpty()) {
	                        	String msg = null;
	                        	 arr[0] = String.valueOf(downloadDataMap.size());
	                        	 msg=PretupsRestUtil.getMessageString("batchdirectpayout.batchfocapprove.msg.bachprocesssuccessfully",arr);
	                        	 response.setMessageCode("batchdirectpayout.batchfocapprove.msg.bachprocesssuccessfully");
	                        	 response.setStatus("200");
	                        	 response.setService(serviceType);
	              				response.setBatchID(String.valueOf( focBatchMasterVO.getBatchId()));
	              				response.setMessage(msg);
	              				response.setProcessedRecs((String.valueOf(downloadDataMap.size())));
	                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
	                        	response.setReferenceId(downloadDataMap.size());
	              				responseSwag.setStatus(HttpStatus.SC_OK);
	                           
	                        }
	                        else {
	                        	//boolean bulkCommissionPayout=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue();
	                        	if( PretupsI.INCENTIVE_WALLET_TYPE.equalsIgnoreCase(commWalletType )) {
	        						 o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.DP_BATCH_TRANSACTION_ID);
	        					 }
	        					 else {
	        						 o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.FOC_BATCH_TRANSACTION_ID);
	        					 }
	                        	
	        	              String name= o2CBatchApprovalDetailsResponse.getApprovalDetails().getBatchFileName();
	      			          String extension =getExtensionByApacheCommonLib(name);
	      			           String filePath = Constants.getProperty("DownloadFilePathForO2CApproval");
	      			           try {
	      			                final File fileDir = new File(filePath);
	      			                if (!fileDir.isDirectory()) {
	      			                    fileDir.mkdirs();
	      			                }
	      			            } catch (Exception e) {
	      			                log.errorTrace("loadDownloadFile", e);
	      			                log.error("loadDownloadFile", "Exception" + e.getMessage());
	      			                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "error");
	      			            }
	      			           String fileType= null;
	      			           String fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime();
	      			           if(extension.equals("csv")) {
	      			        	    fileType= "."+extension;
	      			        	    fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	      			        	    writeToCsvforWithdrawalandFOC(downloadDataMap, filePath+fileName,isNotApprovalLevel1,locale,approvalLevelf);
	      				          }
	      			            else if (extension.equals("xlsx")) {
	      			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	      			            	fileType= "."+extension;
	      					        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

	      			            	 ExcelRW excelRW = new ExcelRW();
	      					         excelRW
	      					             .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	      			            }
	      			            else {
	      			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	      			            	fileType= ".xls";
	      			            	extension="xls";
	      			            	fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

	      			            	 ExcelRW excelRW = new ExcelRW();
	      					            // Write the excel file for download
	      					            excelRW
	      					                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	      					           
	      			            }
	      			            File download =new File(filePath+fileName);
	      						byte[] fileContent = FileUtils.readFileToByteArray(download);
	      				   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
	                        	
	                        	
	                        	
	                        	
	                        	
	      				   		
	      				   		
	                        		if (errorList.size() == downloadDataMap.size()) {
		                        	 response.setProcessedRecs((String.valueOf(errorList.size())));
		                             response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
		                           // theForm.setViewErrorLog("Y");
			                       	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

		                             if (errorList != null && !errorList.isEmpty()) {
		                                 int errorListSize = errorList.size();
		                                 for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
		                                 	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
		                   		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
		                                     {
		                   		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		                   		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		                   		            	MasterErrorList masterErrorList = new MasterErrorList();
		                   						masterErrorList.setErrorCode(errorVO.getIDValue());
		                   						String msg = errorVO.getOtherInfo2();
		                   						masterErrorList.setErrorMsg(msg);
		                   						masterErrorLists.add(masterErrorList);
		                   						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
		                   						//rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                   						rowErrorMsgLists.setRowValue(String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                   						rowErrorMsgLists.setRowName(errorVO.getCodeName());
		                   						if(errorMap.getRowErrorMsgLists() == null)
		                   							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
		                   						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,false);
		                   					    
		                                     }
		                                 }
		                             }
		                             
		                             response.setStatus("400");
		             	            response.setErrorMap(errorMap);
		             	           response.setService(serviceType);
		             				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
		             				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
		                       	    response.setReferenceId(0);
		             				response.setMessage(PretupsRestUtil.getMessageString("batchdirectpayout.batchfocapprove.msg.bachprocessfailed"));
		                             
		                             return response;
		                             
		                           
		                        }
		                        // success message with showing invalid MSISDN list
		                        else {
		                        	
		                        	
		                      	    responseSwag.setStatus(HttpStatus.SC_OK);

		                            arr[0] = String.valueOf(downloadDataMap.size() - errorList.size());
		                           // theForm.setProcessedRecs(String.valueOf(errorList.size()));
		                            //theForm.setNoOfRecords(String.valueOf(downloadDataMap.size()));
		                            response.setProcessedRecs((String.valueOf(errorList.size())));
		                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
		                            
		                            if (errorList != null && !errorList.isEmpty()) {
		                                int errorListSize = errorList.size();
		                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
		                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
		                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
		                                    {
		                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		                  		            	MasterErrorList masterErrorList = new MasterErrorList();
		                  						masterErrorList.setErrorCode(errorVO.getIDValue());
		                  						String msg = errorVO.getOtherInfo2();
		                  						masterErrorList.setErrorMsg(msg);
		                  						masterErrorLists.add(masterErrorList);
		                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
		                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
		                  						if(errorMap.getRowErrorMsgLists() == null)
		                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
		                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,true);
		                                    }
		                                }
		                            }
		                            
		                        	response.setStatus("400");
		            	            response.setErrorMap(errorMap);
		            	            response.setService(serviceType);
		            				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
		            				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
		                      	    response.setReferenceId(0);
		                      	    response.setMessage(PretupsRestUtil.getMessageString("batchdirectpayout.batchfocapprove.msg.bachprocessfailedsuccess", arr));
		                          
		                            return response;
		                     
		                            
		                          //  btslMessage = new BTSLMessages("batchdirectpayout.batchfocapprove.msg.bachprocessfailedsuccess", arr, "batchApproveDirectPayoutTransfer");
		                            //theForm.setViewErrorLog("Y");
		                        }
	                        }
	                    
	                       
	                    } else {
	                        errorList = focBatchTransferDAO.closeOrderByBatchForDirectPayoutRest(con, downloadDataMap, currentLevel, senderVO.getUserID(),
	                        		focBatchMasterVO, locale, data.getLanguage1(), data.getLanguage2());
	                       // theForm.setErrorList(errorList);
	                        BTSLMessages btslMessage = null;
	                        // success message
	                        if (errorList == null || errorList.isEmpty()) {
	                            arr[0] = String.valueOf(downloadDataMap.size());
	                            String msg=PretupsRestUtil.getMessageString("batchdirectpayout.batchfocapprove.msg.bachprocesssuccessfullyclose", arr);
	                            response.setStatus("200");
	                            response.setService(serviceType);
	             				response.setBatchID(String.valueOf( data.getBatchId()));
	             				response.setProcessedRecs((String.valueOf(downloadDataMap.size())));
	                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
	             				response.setMessage(msg);
	             				response.setMessageCode(PretupsErrorCodesI.BATCH_O2C_TRF_SUCCESS);
	                       	    response.setReferenceId(downloadDataMap.size());
	             				responseSwag.setStatus(HttpStatus.SC_OK);
	                            
	                        }
	                        else {
	                        	
	                        	boolean bulkCommissionPayout=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue();	
	        					 if(!bulkCommissionPayout)
	        					 {
	        						 o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.FOC_BATCH_TRANSACTION_ID);
	        					 }
	        					 else {
	        						 o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.DP_BATCH_TRANSACTION_ID);

	        					 }
	                        	
	        	                String name= o2CBatchApprovalDetailsResponse.getApprovalDetails().getBatchFileName();

	      			          String extension =getExtensionByApacheCommonLib(name);
	      			           String filePath = Constants.getProperty("DownloadFilePathForO2CApproval");
	      			           try {
	      			                final File fileDir = new File(filePath);
	      			                if (!fileDir.isDirectory()) {
	      			                    fileDir.mkdirs();
	      			                }
	      			            } catch (Exception e) {
	      			                log.errorTrace("loadDownloadFile", e);
	      			                log.error("loadDownloadFile", "Exception" + e.getMessage());
	      			                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "error");
	      			            }
	      			           String fileType= null;
	      			           String fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime();
	      			           if(extension.equals("csv")) {
	      			        	    fileType= "."+extension;
	      			        	    fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	      			        	    writeToCsvforWithdrawalandFOC(downloadDataMap, filePath+fileName,isNotApprovalLevel1,locale,approvalLevelf);
	      				          }
	      			            else if (extension.equals("xlsx")) {
	      			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	      			            	fileType= "."+extension;
	      					        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

	      			            	 ExcelRW excelRW = new ExcelRW();
	      					         excelRW
	      					             .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	      			            }
	      			            else {
	      			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	      			            	fileType= ".xls";
	      			            	fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	      			            	 extension="xls";
	      			            	 ExcelRW excelRW = new ExcelRW();
	      					            // Write the excel file for download
	      					            excelRW
	      					                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	      					           
	      			            }
	      			            File download =new File(filePath+fileName);
	      						byte[] fileContent = FileUtils.readFileToByteArray(download);
	      				   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
	                        	

		                      if (errorList.size() == downloadDataMap.size()) {
		                    	  responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		                        	 response.setProcessedRecs((String.valueOf(errorList.size())));
		                             response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
		                           
		                             if (errorList != null && !errorList.isEmpty()) {
		                                 int errorListSize = errorList.size();
		                                 for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
		                                 	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
		                   		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
		                                     {
		                   		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		                   		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		                   		            	MasterErrorList masterErrorList = new MasterErrorList();
		                   						masterErrorList.setErrorCode(errorVO.getIDValue());
		                   						String msg = errorVO.getOtherInfo2();
		                   						masterErrorList.setErrorMsg(msg);
		                   						masterErrorLists.add(masterErrorList);
		                   						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
		                   						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                   						rowErrorMsgLists.setRowName(errorVO.getCodeName());
		                   						if(errorMap.getRowErrorMsgLists() == null)
		                   							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
		                   						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,false);
		                                     }
		                                 }
		                             }
		                             
		                         	response.setStatus("400");
		                         	 response.setService(serviceType);
		             	            response.setErrorMap(errorMap);
		             				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
		             				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
		                       	    response.setReferenceId(0);
		                       	   
		                       		response.setMessage(PretupsRestUtil.getMessageString("batchdirectpayout.batchfocapprove.msg.bachprocessfailed"));
		                            return response;
		                         
		                             
		                             
		     	                        }
		                        // success message with showing invalid MSISDN list
		                        else {
		                        	
		                      	    responseSwag.setStatus(HttpStatus.SC_OK);

		                            arr[0] = String.valueOf(downloadDataMap.size() - errorList.size());
		                            response.setProcessedRecs((String.valueOf(errorList.size())));
		                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
		                            if (errorList != null && !errorList.isEmpty()) {
		                                int errorListSize = errorList.size();
		                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
		                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
		                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
		                                    {
		                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		                  		            	MasterErrorList masterErrorList = new MasterErrorList();
		                  						masterErrorList.setErrorCode(errorVO.getIDValue());
		                  						String msg = errorVO.getOtherInfo2();
		                  						masterErrorList.setErrorMsg(msg);
		                  						masterErrorLists.add(masterErrorList);
		                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
		                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
		                  						if(errorMap.getRowErrorMsgLists() == null)
		                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
		                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,true);
		                                    }
		                                }
		                            }
		                            
		                        	response.setStatus("400");
		            	            response.setErrorMap(errorMap);
		            	            response.setService(serviceType);
		            				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
		            				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
		                      	    response.setReferenceId(0);
		                      		response.setMessage(PretupsRestUtil.getMessageString("batchdirectpayout.batchfocapprove.msg.bachprocessfailedsuccessclose", arr));
		                            return response;
		                         
		                           
		                        }
	                        }
	                        
	                       

	                    }// end of else
	                }// end of else
	            }// end of if
	            else {
	            	
	            	  throw new BTSLBaseException("batchdirectpayout.batchfocapprove.msg.nochildrecordfound", "processBatcDirectPayouthApprove()");

	            }
	        }
	        catch(BTSLBaseException be){
		  		if(log.isDebugEnabled())
		  		    log.debug( "processBatcDirectPayouthApprove()", "Exceptin:be=" + be);
		            log.error( "processBatcDirectPayouthApprove()", "Exceptin:be=" + be);
	     	        throw be;
	         }
	 catch (Exception e) {
	            log.error(methodName, "Exception: " + e.getMessage());
	            log.errorTrace(methodName, e);
	            if (showLogs) {
	                DirectPayOutErrorLog.dpBatchMasterLog(methodName, focBatchMasterVO, "FAIL : Error message is =" + e.getMessage(), "CURRENT LEVEL=" + currentLevel);
	            }
	            throw new BTSLBaseException(PretupsErrorCodesI.REQ_NOT_PROCESS);
	            
	            
	        } finally {
	            // as to make the status of the batch foc process as complete into
	            // the table so that only
	            // one instance should be executed for batch direct payout

	            if (processRunning) {
	                try {
	                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
	                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
	                    if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
	                       mcomCon.finalCommit();
	                    } else {
	                       mcomCon.finalRollback();
	                    }
	                } catch (Exception e) {
	                    if (log.isDebugEnabled()) {
	                        log.error(methodName, " Exception in update process detail for batch foc approval process " + e.getMessage());
	                    }
	                    log.errorTrace(methodName, e);
	                }
	            }
	            // ends here
	            // if connection not null then close the connection
				if (mcomCon != null) {
					mcomCon.close("FOCBatchTransferApprovalAction#processBatcDirectPayouthApprove");
					mcomCon = null;
				}
	            if (showLogs) {
	                DirectPayOutSuccessLog.dpBatchMasterLog(methodName, focBatchMasterVO, "FINALLY BLOCK : Batch processed",
	                    "CURRENT LEVEL=" + currentLevel + ", PROCESSED RECORD=" + arr[0]);
	            }
	            /*if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exited forward=" + forward);
	            }*/
	        }
	        return response;
	    }
	  
	  /**
	   * Cancel the order in batch
	   * This method handles all level of approval and also cancel the order
	   * request
	   * @param response
	   * @param senderVO
	   * @param data
	   * @param httprequest
	   * @param responseSwag
	   * @param serviceType
	   * @param MAX_APP_LVL
	   * @param focBatchMasterVOList
	   * @return
	   * @throws BTSLBaseException
	   */
	  
	  private O2CBatchApRejTransferResponse cancelOrderDirectPayout(O2CBatchApRejTransferResponse response,
			  ChannelUserVO senderVO,CommisionBulkApprovalOrRejectRequestData data,HttpServletRequest httprequest,
			  HttpServletResponse responseSwag,String serviceType,int MAX_APP_LVL, ArrayList focBatchMasterVOList) throws BTSLBaseException {
		  
		  

	        final String methodName = "cancelOrderDirectPayout";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
	        //ActionForward forward = null;
			Connection con = null;
			MComConnectionI mcomCon = null;
	        ArrayList errorList = null;
	        FOCBatchMasterVO focBatchMasterVO = null;
	        String currentLevel = null;
	        final String arr[] = new String[1];
	        boolean showLogs = true;
	        ProcessStatusVO processVO = null;
	        boolean processRunning = true;
	        Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
	        O2CBatchApprovalDetailsResponse o2CBatchApprovalDetailsResponse=null;
	        BatchO2CTransferDAO batchO2CTransferDAO =null;
	        try {
	        	
	        	o2CBatchApprovalDetailsResponse=new O2CBatchApprovalDetailsResponse();
	        	 batchO2CTransferDAO=new BatchO2CTransferDAO();
	            // as to check the status of the batch foc process into the table so
	            // that only
	            // one instance should be executed for batch foc

	            final ProcessBL processBL = new ProcessBL();
	           // final UserVO userVO1 = this.getUserFormSession(request);
	            try {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
	                processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.DP_BATCH_PROCESS_ID,senderVO.getNetworkID());
	            } catch (BTSLBaseException e) {
	                log.error(methodName, "Exception:e=" + e);
	                log.errorTrace(methodName, e);
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, "batchdirectpayout.processfile.error.alreadyexecution");
	            }
	            if (processVO != null && !processVO.isStatusOkBool()) {
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, "batchdirectpayout.processfile.error.alreadyexecution");
	            }
	            mcomCon.partialCommit();
	            processVO.setNetworkCode(senderVO.getNetworkID());
	            // ends here

	          //  final FOCBatchForm theForm = (FOCBatchForm) form;
	            String statusUsed = null;
	            String remark = null;
	            
	            // Based on the levl of approval set the status to be used.
	            // That status is used to load the data for batch rejection
	            ErrorMap errorMap = new ErrorMap();
	            if ("approval1".equals(data.getRequestType())) {
	                remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
	            }
	            if ("approval2".equals(data.getRequestType())) {
	            	remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
	            }
	            if ("approval3".equals(data.getRequestType())) {
	            	remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
	            }
	            if (!BTSLUtil.isNullString(remark) && remark.length() > 100) {
	                showLogs = false;
	                throw new BTSLBaseException("batchdirectpayout.batchfocapprove.msg.invalidlengthremark", "batchRejectDirectPayoutTransfer");
	            }
	            final FOCBatchTransferDAO focBatchTransferDAO = new FOCBatchTransferDAO();
	            LinkedHashMap downloadDataMap = null;

	            // get the data of items from the form.
	            // If it is null the load from the database
	          
	                downloadDataMap = focBatchTransferDAO.loadBatchItemsMap(con, data.getBatchId(), statusUsed);
	            
	            if (downloadDataMap != null && !downloadDataMap.isEmpty()) {

	                FOCBatchItemsVO focBatchItemVO = null;
	                final Iterator iterator = downloadDataMap.keySet().iterator();
	                String key = null;
	                int i = 1;
	                boolean isNotApprovalLevel1 = true;
		            String exelFileID = null;
	                while (iterator.hasNext()) {
	                    key = (String) iterator.next();
	                    focBatchItemVO = (FOCBatchItemsVO) downloadDataMap.get(key);
	                    focBatchItemVO.setRecordNumber(i + 1);
	                    if ("approval1".equals(data.getRequestType())) {
	                        focBatchItemVO.setFirstApproverRemarks(remark);
	                        exelFileID = ExcelFileIDI.BATCH_DP_APPRV1;
			                isNotApprovalLevel1 = false;
	                    } else if ("approval2".equals(data.getRequestType())) {
	                        focBatchItemVO.setSecondApproverRemarks(remark);
	                        exelFileID = ExcelFileIDI.BATCH_DP_APPRV2;
	 			           
	                    } else if ("approval3".equals(data.getRequestType())) {
	                        focBatchItemVO.setThirdApproverRemarks(remark);
	                        exelFileID = ExcelFileIDI.BATCH_DP_APPRV2;
	                    }
	                    focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
	                    i++;
	                }
	                
	                
	                String approvalLevelf ="";
	                
	                if ("approval1".equals(data.getRequestType())) {
                        
	                	approvalLevelf="1";
                    } else if ("approval2".equals(data.getRequestType())) {
                    	approvalLevelf="2";
                    } else if ("approval3".equals(data.getRequestType())) {
                    	
                    	approvalLevelf="3";
                        
                    }
	                focBatchMasterVO = (FOCBatchMasterVO) getBatchDetailsComm(focBatchMasterVOList,data.getBatchId());
	                if(focBatchMasterVO == null) {
		             	  throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DET_NOT_FOUND_BULK);
		                }
	                
	               String walletTypeTransfer = focBatchMasterVO.getFocOrCommPayout();

	
	                
	                // Check if batch is modified or not
	                final boolean isModified = focBatchTransferDAO.isBatchModified(con, focBatchMasterVO.getModifiedOn().getTime(), data.getBatchId());
	                if (isModified) {
	                
		             	   throw new BTSLBaseException(this, "cancelOrderDirectPayout", "batchdirectpayout.batchfocapprovereject.msg.batchmodify");

	                } else {
	                    final int updateCount = focBatchTransferDAO.updateBatchStatus(con, focBatchItemVO.getBatchId(), PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS,
	                        PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
	                    if (updateCount <= 0) {
	                    	mcomCon.partialRollback();
	                        throw new BTSLBaseException(this, methodName, "error.general.processing", "firstPage");
	                    }
	                    mcomCon.partialCommit();
	                    // cancel the batch
	                    errorList =focBatchTransferDAO.processOrderByDPBatchRest(con, downloadDataMap, currentLevel,
                        		senderVO.getUserID(),locale, data.getLanguage1(), data.getLanguage2());
	                    
	                    
	                    BTSLMessages btslMessage = null;

	                    // success message
	                    if (errorList == null || errorList.isEmpty()) {
	                        arr[0] = String.valueOf(downloadDataMap.size());
	                       
	                        String msg =PretupsRestUtil.getMessageString("batchdirectpayout.batchfocreject.msg.bachprocesssuccessfully", new String[]{ String.valueOf(downloadDataMap.size())});
	                        response.setMessageCode("batchdirectpayout.batchfocreject.msg.bachprocesssuccessfully");
	                        response.setStatus("200");
	                        response.setService(serviceType);
             				response.setBatchID(String.valueOf( data.getBatchId()));
             				response.setMessage(msg);
             				response.setProcessedRecs((String.valueOf(downloadDataMap.size())));
                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
                       	    response.setReferenceId(downloadDataMap.size());
             				responseSwag.setStatus(HttpStatus.SC_OK);
	                    
	                    } else
	                    {
	                    	
	                    	//boolean bulkCommissionPayout=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue();
	                    	if( PretupsI.DP_TRANSFER.equalsIgnoreCase(walletTypeTransfer) )	{
	                    		o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.DP_BATCH_TRANSACTION_ID);
	                    	}else {
	                    			o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.FOC_BATCH_TRANSACTION_ID);
	                    		}
 					    String name= o2CBatchApprovalDetailsResponse.getApprovalDetails().getBatchFileName();

	  			          String extension =getExtensionByApacheCommonLib(name);
	  			           String filePath = Constants.getProperty("DownloadFilePathForO2CApproval");
	  			           try {
	  			                final File fileDir = new File(filePath);
	  			                if (!fileDir.isDirectory()) {
	  			                    fileDir.mkdirs();
	  			                }
	  			            } catch (Exception e) {
	  			                log.errorTrace("loadDownloadFile", e);
	  			                log.error("loadDownloadFile", "Exception" + e.getMessage());
	  			                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "error");
	  			            }
	  			           String fileType= null;
	  			           String fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime();
	  			           if(extension.equals("csv")) {
	  			        	    fileType= "."+extension;
	  			        	    fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	  			        	    writeToCsvforWithdrawalandFOC(downloadDataMap, filePath+fileName,isNotApprovalLevel1,locale,approvalLevelf);
	  				          }
	  			            else if (extension.equals("xlsx")) {
	  			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	  			            	fileType= "."+extension;
	  					        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

	  			            	 ExcelRW excelRW = new ExcelRW();
	  					         excelRW
	  					             .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	  			            }
	  			            else {
	  			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	  			            	fileType= ".xls";
	  			            	fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	  			            	extension="xls";
	  			            	 ExcelRW excelRW = new ExcelRW();
	  					            // Write the excel file for download
	  					            excelRW
	  					                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	  					           
	  			            }
	  			            File download =new File(filePath+fileName);
	  						byte[] fileContent = FileUtils.readFileToByteArray(download);
	  				   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
	                    	
	                    	
	                    	
	                        if (errorList.size() == downloadDataMap.size()) {
	 	                       
		                        
		                        response.setProcessedRecs((String.valueOf(errorList.size())));
	                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
	                            if (errorList != null && !errorList.isEmpty()) {
	                                int errorListSize = errorList.size();
	                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
	                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
	                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
	                                    {
	                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
	                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
	                  		            	MasterErrorList masterErrorList = new MasterErrorList();
	                  						masterErrorList.setErrorCode(errorVO.getIDValue());
	                  						String msg = errorVO.getOtherInfo2();
	                  						masterErrorList.setErrorMsg(msg);
	                  						masterErrorLists.add(masterErrorList);
	                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
	                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
	                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
	                  						if(errorMap.getRowErrorMsgLists() == null)
	                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
	                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
	                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,false);
	                                    }
	                                }
	                            }
	                            
	                        	response.setStatus("400");
	            	            response.setErrorMap(errorMap);
	            	            response.setService(serviceType);
	            				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
	            				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
	                      	    response.setReferenceId(0);
	                      	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	            
	                            response.setMessage(PretupsRestUtil.getMessageString("batchdirectpayout.batchfocreject.msg.bachprocessfailed"));
	                            
	                            return response;
		                    
		                    }
		                    // success message with showing invalid MSISDN list
		                    else {
		                        
		                    	responseSwag.setStatus(HttpStatus.SC_OK);
		                        arr[0] = String.valueOf(downloadDataMap.size() - errorList.size());
	                            response.setProcessedRecs((String.valueOf(errorList.size())));
	                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
	                            if (errorList != null && !errorList.isEmpty()) {
	                                int errorListSize = errorList.size();
	                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
	                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
	                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
	                                    {
	                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
	                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
	                  		            	MasterErrorList masterErrorList = new MasterErrorList();
	                  						masterErrorList.setErrorCode(errorVO.getIDValue());
	                  						String msg = errorVO.getOtherInfo2();
	                  						masterErrorList.setErrorMsg(msg);
	                  						masterErrorLists.add(masterErrorList);
	                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
	                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
	                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
	                  						if(errorMap.getRowErrorMsgLists() == null)
	                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
	                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
	                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,true);
	                                    }
	                                }
	                            }
	                            
	                        	response.setStatus("400");
	            	            response.setErrorMap(errorMap);
	            	            response.setService(serviceType);
	            				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
	            				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
	                      	    response.setReferenceId(0);
	                      	    
	                      		response.setMessage(PretupsRestUtil.getMessageString("batchdirectpayout.batchfocreject.msg.bachprocessfailedsuccess", arr));
	                            return response;
	                         
	                         
		                    }
	                    }
	                    
	        
	                   
	                }
	            } else {
	            	  throw new BTSLBaseException("batchdirectpayout.batchfocapprove.msg.nochildrecordfound", "cancelBatchDirect");

	            
	            }
	        } 
	        catch(BTSLBaseException be){
		  		if(log.isDebugEnabled())
		  		    log.debug( "processBatcDirectPayouthApprove()", "Exceptin:be=" + be);
		            log.error( "processBatcDirectPayouthApprove()", "Exceptin:be=" + be);
	     	        throw be;
	         }
	        catch (Exception e) {

	            log.error(methodName, "Exception:e=" + e);
	            log.errorTrace(methodName, e);
	            if (showLogs) {
	                DirectPayOutErrorLog.dpBatchMasterLog(methodName, focBatchMasterVO, "FAIL : Error message is =" + e.getMessage(), "CURRENT LEVEL=" + currentLevel);
	            }
	            throw new BTSLBaseException(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        } finally {
	            // as to make the status of the batch foc process as complete into
	            // the table so that only
	            // one instance should be executed for batch foc

	            if (processRunning) {
	                try {
	                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
	                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
	                    if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
	                        mcomCon.finalCommit();
	                    } else {
	                        mcomCon.finalRollback();
	                    }
	                } catch (Exception e) {
	                    if (log.isDebugEnabled()) {
	                        log.error(methodName, " Exception in update process detail for batch foc cancel order " + e.getMessage());
	                    }
	                    log.errorTrace(methodName, e);
	                }
	            }
	            // ends here
	            // if connection not null then close the connection
				if (mcomCon != null) {
					mcomCon.close("FOCBatchTransferApprovalAction#cancelOrderDirectPayout");
					mcomCon = null;
				}
	          /*  if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting forward:=" + forward);
	            }*/
	            if (showLogs) {
	                DirectPayOutErrorLog.dpBatchMasterLog(methodName, focBatchMasterVO, "FINALLY BLOCK : Process completed",
	                    "CURRENT LEVEL=" + currentLevel + ", PROCESSED RECORD=" + arr[0]);
	            }
	        }
	        return response;
	    
	  }
	  
	  
	  FOCBatchMasterVO getBatchDetailsComm(ArrayList<FOCBatchMasterVO> batchAppList,String batchID){
	    	
	    	
	    	for(FOCBatchMasterVO  obj : batchAppList) {
	    		String str = obj.getBatchId();
	    		if(batchID.equals(str)) {
	    			return obj;
	    		}
	    	}
	    	
	    	
			return null;
	    	
	    }
	  
	  
	  
	  private O2CBatchApRejTransferResponse processFocBatchApprove(O2CBatchApRejTransferResponse response,
			  ChannelUserVO senderVO,CommisionBulkApprovalOrRejectRequestData data,HttpServletRequest httprequest,
			  HttpServletResponse responseSwag,String serviceType,int MAX_APP_LVL, ArrayList focBatchMasterVOList) throws BTSLBaseException {
		 
		  

	        final String methodName = "processBatchApprove";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
	        //ActionForward forward = null;
			Connection con = null;
			MComConnectionI mcomCon = null;
	        ArrayList errorList = null;
	        FOCBatchMasterVO focBatchMasterVO = null;
	        String currentLevel = null;
	        final String arr[] = new String[1];
	        boolean showLogs = true;
	        ProcessStatusVO processVO = null;
	        boolean processRunning = true;
	        Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
	        ErrorMap errorMap = new ErrorMap();
	        O2CBatchApprovalDetailsResponse o2CBatchApprovalDetailsResponse=null;
	        BatchO2CTransferDAO batchO2CTransferDAO =null;


	        try {
	        	
	        	o2CBatchApprovalDetailsResponse=new O2CBatchApprovalDetailsResponse();
	        	batchO2CTransferDAO=new BatchO2CTransferDAO();
	            
	            // as to check the status of the batch foc process into the table so
	            // that only
	            // one instance should be executed for batch foc
	            final ProcessBL processBL = new ProcessBL();
	            final UserVO userVO = senderVO;
	            try {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
	                processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.FOC_BATCH_PROCESS_ID,userVO.getNetworkID());
	            } catch (BTSLBaseException e) {
	                log.error(methodName, "Exception:e=" + e);
	                log.errorTrace(methodName, e);
	                processRunning = false;

	                throw new BTSLBaseException(this, methodName, "batchfoc.processfile.error.alreadyexecution");
	            }
	            if (processVO != null && !processVO.isStatusOkBool()) {
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, "batchfoc.processfile.error.alreadyexecution");
	            }
	            mcomCon.partialCommit();
	            processVO.setNetworkCode(userVO.getNetworkID());
	            // ends here

	            boolean sendOrderToApproval = false;
	            String statusUsed = null;
	            String remark = null;
	            // Set the status to be used and also set if order is closed at this
	            // level
	            if ("approval1".equals(data.getRequestType())) {
	                remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
	                if (MAX_APP_LVL <= 1) {
	                    sendOrderToApproval = true;
	                }
	            }
	            if ("approval2".equals(data.getRequestType())) {
	                remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
	                if (MAX_APP_LVL <= 2) {
	                    sendOrderToApproval = true;
	                }
	            }
	            if ("approval3".equals(data.getRequestType())) {
	                remark =data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
	                sendOrderToApproval = true;
	            }
	            if (!BTSLUtil.isNullString(remark) && remark.length() > 100) {
	                showLogs = false;
	                throw new BTSLBaseException("batchfoc.batchfocapprove.msg.invalidlengthremark", "batchApproveFOCTransfer");
	            }
	            // If external txn number is mandatory at this level the process
	            // will not be permitted
	            // error will be thrown
//	            if (PretupsI.YES.equals(theForm.getExternalTxnMandatory())) {
//	                showLogs = false;
//	                BatchFocFileProcessLog.focBatchMasterLog(methodName, focBatchMasterVO, "FAIL : Operation is not permited",
//	                    "External Txn is required, CURRENT LEVEL=" + theForm.getRequestType());
//	                throw new BTSLBaseException("batchfoc.batchfocapprove.msg.operationnotpermitted", "batchApproveFOCTransfer");
//	            }

	            // get the login user information from session
	           
	            final FOCBatchTransferDAO focBatchTransferDAO = new FOCBatchTransferDAO();
	            final FOCBatchTransferWebDAO focBatchTransferWebDAO = new FOCBatchTransferWebDAO();
	            LinkedHashMap downloadDataMap = null;

	            // get the data of items from the form.
	            // If it is null the load from the database
	         
	                downloadDataMap = focBatchTransferDAO.loadBatchItemsMap(con, data.getBatchId(), statusUsed);
	            
	            if (downloadDataMap != null && !downloadDataMap.isEmpty()) {
	                FOCBatchItemsVO focBatchItemVO = null;
	                final Iterator iterator = downloadDataMap.keySet().iterator();
	                String key = null;
	                int i = 1;
	                boolean isNotApprovalLevel1 = true;
		            String exelFileID = null;
	                while (iterator.hasNext()) {
	                    key = (String) iterator.next();
	                    focBatchItemVO = (FOCBatchItemsVO) downloadDataMap.get(key);
	                    focBatchItemVO.setRecordNumber(i + 1);
	                    if ("approval1".equals(data.getRequestType())) {
	                        focBatchItemVO.setFirstApproverRemarks(remark);
	                        focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
	                        exelFileID = ExcelFileIDI.BATCH_FOC_APPRV1;
			                isNotApprovalLevel1 = false;
	                    } else if ("approval2".equals(data.getRequestType())) {
	                        focBatchItemVO.setSecondApproverRemarks(remark);
	                        focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
	                        exelFileID = ExcelFileIDI.BATCH_FOC_APPRV2;
	                    } else if ("approval3".equals(data.getRequestType())) {
	                        focBatchItemVO.setThirdApproverRemarks(remark);
	                        focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
	                        exelFileID = ExcelFileIDI.BATCH_FOC_APPRV3;
	                    }
	                    if (sendOrderToApproval) {
	                        focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	                    }
	                    i++;
	                }
	                
	                String approvalLevelf ="";
	                
	                if ("approval1".equals(data.getRequestType())) {
                        
	                	approvalLevelf="1";
                    } else if ("approval2".equals(data.getRequestType())) {
                    	approvalLevelf="2";
                    } else if ("approval3".equals(data.getRequestType())) {
                    	
                    	approvalLevelf="3";
                        
                    }
	                focBatchMasterVO = (FOCBatchMasterVO)  getBatchDetailsComm(focBatchMasterVOList,data.getBatchId());
	                if(focBatchMasterVO == null) {
		             	  throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DET_NOT_FOUND_BULK);
		                }
	                
	                String walletType =focBatchMasterVO.getWallet_type();
	              	                
	                // Check if batch is modified or not.
	                final boolean isModified = focBatchTransferDAO.isBatchModified(con, focBatchMasterVO.getModifiedOn().getTime(), data.getBatchId());
	                if (isModified) {

		             	   throw new BTSLBaseException(this, methodName, "batchfoc.batchfocapprove.msg.bachprocessfailed");
	                
	                } else {
	                    final int updateCount = focBatchTransferDAO.updateBatchStatus(con, focBatchItemVO.getBatchId(), PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS,
	                        PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
	                    if (updateCount <= 0) {
	                        mcomCon.partialRollback();
	                        throw new BTSLBaseException(this, methodName, "error.general.processing", "firstPage");
	                    }
	                    mcomCon.partialCommit();
	                    // If order is not closed at this level then call
	                    // processOrderByBatch otehwise CloseOrderByBatch method
	                    if (!sendOrderToApproval) {
	                        errorList = focBatchTransferWebDAO.processOrderByBatchRest(con, downloadDataMap, currentLevel, senderVO.getUserID(),locale, data.getLanguage1(), data.getLanguage2());
	                        BTSLMessages btslMessage = null;

	                        // success message
	                        if (errorList == null || errorList.isEmpty()) {
	                        	String msg = null;
	                            arr[0] = String.valueOf(downloadDataMap.size());
	                            msg=PretupsRestUtil.getMessageString("batchfoc.batchfocapprove.msg.bachprocesssuccessfully",arr);
	                        	 response.setMessageCode("batchdirectpayout.batchfocapprove.msg.bachprocesssuccessfully");
	                        	 response.setStatus("200");
	              				response.setBatchID(String.valueOf( focBatchMasterVO.getBatchId()));
	              				response.setMessage(msg);
	              				 response.setService(serviceType);
	              				response.setProcessedRecs((String.valueOf(downloadDataMap.size())));
	                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
	                        	response.setReferenceId(downloadDataMap.size());
	              				responseSwag.setStatus(HttpStatus.SC_OK);
	  
	                        
	                        
	                        } else
	                        	
	                        {
	                        	
	                        	//boolean bulkCommissionPayout=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue();
	                        	if(PretupsI.INCENTIVE_WALLET_TYPE.equalsIgnoreCase(walletType) )
	        					 //if(!bulkCommissionPayout)
	        					 {
	                        		o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.DP_BATCH_TRANSACTION_ID);
	        					 }
	        					 else {
	        						 o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.FOC_BATCH_TRANSACTION_ID);
	        					 }
	        					 
	        					 String name= o2CBatchApprovalDetailsResponse.getApprovalDetails().getBatchFileName();
	          	                String extension =getExtensionByApacheCommonLib(name);
	          			           String filePath = Constants.getProperty("DownloadFilePathForO2CApproval");
	          			           try {
	          			                final File fileDir = new File(filePath);
	          			                if (!fileDir.isDirectory()) {
	          			                    fileDir.mkdirs();
	          			                }
	          			            } catch (Exception e) {
	          			                log.errorTrace("loadDownloadFile", e);
	          			                log.error("loadDownloadFile", "Exception" + e.getMessage());
	          			                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "error");
	          			            }
	          			           String fileType= null;
	          			           String fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime();
	          			           if(extension.equals("csv")) {
	          			        	    fileType= "."+extension;
	          			        	    fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	          			        	    writeToCsvforWithdrawalandFOC(downloadDataMap, filePath+fileName,isNotApprovalLevel1,locale,approvalLevelf);
	          				          }
	          			            else if (extension.equals("xlsx")) {
	          			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	          			            	fileType= "."+extension;
	          					        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

	          			            	 ExcelRW excelRW = new ExcelRW();
	          					         excelRW
	          					             .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	          			            }
	          			            else {
	          			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	          			            	fileType= ".xls";
	          			            	fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	          			            	extension="xls";
	          			            	 ExcelRW excelRW = new ExcelRW();
	          					            // Write the excel file for download
	          					            excelRW
	          					                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	          					           
	          			            }
	          			            File download =new File(filePath+fileName);
	          						byte[] fileContent = FileUtils.readFileToByteArray(download);
	          				   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
	          	
	          	                

	                        	
	                           if (errorList.size() == downloadDataMap.size()) {
		                       	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

		                        	 response.setProcessedRecs((String.valueOf(errorList.size())));
		                             response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
		                            
		                            if (errorList != null && !errorList.isEmpty()) {
		                                 int errorListSize = errorList.size();
		                                 for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
		                                 	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
		                   		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
		                                     {
		                   		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		                   		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		                   		            	MasterErrorList masterErrorList = new MasterErrorList();
		                   						masterErrorList.setErrorCode(errorVO.getIDValue());
		                   						String msg = errorVO.getOtherInfo2();
		                   						masterErrorList.setErrorMsg(msg);
		                   						masterErrorLists.add(masterErrorList);
		                   						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
		                   						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                   						rowErrorMsgLists.setRowName(errorVO.getCodeName());
		                   						if(errorMap.getRowErrorMsgLists() == null)
		                   							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
		                   						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,false);
;
		                                     }
		                                 }
		                             }
		                             
		                             response.setStatus("400");
		             	            response.setErrorMap(errorMap);
		             				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
		             				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
		                       	    response.setReferenceId(0);
		                       	 response.setService(serviceType);
		                       	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		             				response.setMessage(PretupsRestUtil.getMessageString("batchfoc.batchfocapprove.msg.bachprocessfailed"));
		                             
		                             return response;
		                             
		                           
		                        }
		                        // success message with showing invalid MSISDN list
		                        else {
		                            arr[0] = String.valueOf(downloadDataMap.size() - errorList.size());
		                       	    responseSwag.setStatus(HttpStatus.SC_OK);

		                            response.setProcessedRecs((String.valueOf(errorList.size())));
		                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
		                            
		                            if (errorList != null && !errorList.isEmpty()) {
		                                int errorListSize = errorList.size();
		                                for (int i2 = 0, j = errorListSize; i2 < j; i++) {
		                                	ListValueVO errorVO = (ListValueVO) errorList.get(i);
		                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
		                                    {
		                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		                  		            	MasterErrorList masterErrorList = new MasterErrorList();
		                  						masterErrorList.setErrorCode(errorVO.getIDValue());
		                  						String msg = errorVO.getOtherInfo2();
		                  						masterErrorList.setErrorMsg(msg);
		                  						masterErrorLists.add(masterErrorList);
		                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
		                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
		                  						if(errorMap.getRowErrorMsgLists() == null)
		                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
		                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,true);
		                                    }
		                                }
		                            }
		                            
		                        	response.setStatus("400");
		            	            response.setErrorMap(errorMap);
		            				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
		            				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
		                      	    response.setReferenceId(0);
		                      	  response.setService(serviceType);
		                      	    responseSwag.setStatus(HttpStatus.SC_OK);
		                      	    response.setMessage(PretupsRestUtil.getMessageString("batchfoc.batchfocapprove.msg.bachprocessfailedsuccess", arr));
		                          
		                            return response;
		                     
		                        }
	                        }
	                        
	                    
	                       
	                    } else {
	                        errorList = focBatchTransferWebDAO.closeOrderByBatchRest(con, downloadDataMap, currentLevel, senderVO.getUserID(),
	                        		focBatchMasterVO, locale, data.getLanguage1(), data.getLanguage2());
	                       
	                        // success message
	                        if (errorList == null || errorList.isEmpty()) {
	                            
	                            
	                            arr[0] = String.valueOf(downloadDataMap.size());
	                            String msg=PretupsRestUtil.getMessageString("batchfoc.batchfocapprove.msg.bachprocesssuccessfullyclose", arr);
	                            response.setStatus("200");
	             				response.setBatchID(String.valueOf( data.getBatchId()));
	             				response.setProcessedRecs((String.valueOf(downloadDataMap.size())));
	                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
	             				response.setMessage(msg);
	             				 response.setService(serviceType);
	             				response.setMessageCode(PretupsErrorCodesI.BATCH_O2C_TRF_SUCCESS);
	                       	    response.setReferenceId(downloadDataMap.size());
	             				responseSwag.setStatus(HttpStatus.SC_OK);
	             				
	                        }
	                        else
	                        {
	                        	
	                        	if(PretupsI.FOC_WALLET_TYPE.equalsIgnoreCase(walletType) ||  PretupsI.SALE_WALLET_TYPE.equalsIgnoreCase(walletType) )
	        					 {
	        						 o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.FOC_BATCH_TRANSACTION_ID);
	        					 }
	        					 else {
	        						 o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.DP_BATCH_TRANSACTION_ID);

	        					 }
	        					 	String name= o2CBatchApprovalDetailsResponse.getApprovalDetails().getBatchFileName();
	                        	
	          	                String extension =getExtensionByApacheCommonLib(name);
	          			           String filePath = Constants.getProperty("DownloadFilePathForO2CApproval");
	          			           try {
	          			                final File fileDir = new File(filePath);
	          			                if (!fileDir.isDirectory()) {
	          			                    fileDir.mkdirs();
	          			                }
	          			            } catch (Exception e) {
	          			                log.errorTrace("loadDownloadFile", e);
	          			                log.error("loadDownloadFile", "Exception" + e.getMessage());
	          			                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "error");
	          			            }
	          			           String fileType= null;
	          			           String fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime();
	          			           if(extension.equals("csv")) {
	          			        	    fileType= "."+extension;
	          			        	    fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	          			        	    writeToCsvforWithdrawalandFOC(downloadDataMap, filePath+fileName,isNotApprovalLevel1,locale,approvalLevelf);
	          				          }
	          			            else if (extension.equals("xlsx")) {
	          			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	          			            	fileType= "."+extension;
	          					        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

	          			            	 ExcelRW excelRW = new ExcelRW();
	          					         excelRW
	          					             .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	          			            }
	          			            else {
	          			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	          			            	fileType= ".xls";
	          			            	fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	          			            	 extension="xls";
	          			            	 ExcelRW excelRW = new ExcelRW();
	          					            // Write the excel file for download
	          					            excelRW
	          					                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	          					           
	          			            }
	          			            File download =new File(filePath+fileName);
	          						byte[] fileContent = FileUtils.readFileToByteArray(download);
	          				   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
	          	
	                        	
	                        	
	                        if (errorList.size() == downloadDataMap.size()) {
		                        	
	     	                       
		                            response.setProcessedRecs((String.valueOf(errorList.size())));
		                             response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
		                           
		                             if (errorList != null && !errorList.isEmpty()) {
		                                 int errorListSize = errorList.size();
		                                 for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
		                                 	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
		                   		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
		                                     {
		                   		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		                   		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		                   		            	MasterErrorList masterErrorList = new MasterErrorList();
		                   						masterErrorList.setErrorCode(errorVO.getIDValue());
		                   						String msg = errorVO.getOtherInfo2();
		                   						masterErrorList.setErrorMsg(msg);
		                   						masterErrorLists.add(masterErrorList);
		                   						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
		                   						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                   						rowErrorMsgLists.setRowName(errorVO.getCodeName());
		                   						if(errorMap.getRowErrorMsgLists() == null)
		                   							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
		                   						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,false);
		                                     }
		                                 }
		                             }
		                             
		                         	response.setStatus("400");
		             	            response.setErrorMap(errorMap);
		             	           response.setService(serviceType);
		             				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
		             				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
		                       	    response.setReferenceId(0);
		                       	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		                       		response.setMessage(PretupsRestUtil.getMessageString("batchfoc.batchfocapprove.msg.bachprocessfailed"));
		                            return response;
		                         
		                             
		                            
		                        
		                        
		                        
		                        }
		                        // success message with showing invalid MSISDN list
		                        else {
		                            arr[0] = String.valueOf(downloadDataMap.size() - errorList.size());
		                         
		                            
		                            response.setProcessedRecs((String.valueOf(errorList.size())));
		                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
		                            if (errorList != null && !errorList.isEmpty()) {
		                                int errorListSize = errorList.size();
		                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
		                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
		                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
		                                    {
		                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		                  		            	MasterErrorList masterErrorList = new MasterErrorList();
		                  						masterErrorList.setErrorCode(errorVO.getIDValue());
		                  						String msg = errorVO.getOtherInfo2();
		                  						masterErrorList.setErrorMsg(msg);
		                  						masterErrorLists.add(masterErrorList);
		                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
		                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
		                  						if(errorMap.getRowErrorMsgLists() == null)
		                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
		                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,true);
		                                    }
		                                }
		                            }
		                            
		                        	response.setStatus("400");
		            	            response.setErrorMap(errorMap);
		            				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
		            				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
		                      	    response.setReferenceId(0);
		                      	  response.setService(serviceType);
		                      	    responseSwag.setStatus(HttpStatus.SC_OK);
		                      		response.setMessage(PretupsRestUtil.getMessageString("batchfoc.batchfocapprove.msg.bachprocessfailedsuccessclose", arr));
		                            return response;
		                         
		                        }
	                        }
	                        
	                       
	                    }// end of else
	                }// end of else
	            }// end of if
	            else {
	            	  throw new BTSLBaseException("batchfoc.batchfocapprove.msg.nochildrecordfound", methodName);

	            
	            }
	        }  catch(BTSLBaseException be){
		  		if(log.isDebugEnabled())
		  		    log.debug( "processBatcDirectPayouthApprove()", "Exceptin:be=" + be);
		            log.error( "processBatcDirectPayouthApprove()", "Exceptin:be=" + be);
	     	        throw be;
	         }
	        
	        catch (Exception e) {
	            log.error(methodName, "Exception: " + e.getMessage());
	            log.errorTrace(methodName, e);
	            if (showLogs) {
	                BatchFocFileProcessLog.focBatchMasterLog(methodName, focBatchMasterVO, "FAIL : Error message is =" + e.getMessage(), "CURRENT LEVEL=" + currentLevel);
	            }
 	            throw new BTSLBaseException(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        } finally {
	            // added by sandeep goel
	            // as to make the status of the batch foc process as complete into
	            // the table so that only
	            // one instance should be executed for batch foc

	            if (processRunning) {
	                try {
	                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
	                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
	                    if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
	                        mcomCon.finalCommit();
	                    } else {
	                        mcomCon.finalRollback();
	                    }
	                } catch (Exception e) {
	                    if (log.isDebugEnabled()) {
	                        log.error(methodName, " Exception in update process detail for batch foc approval process " + e.getMessage());
	                    }
	                    log.errorTrace(methodName, e);
	                }
	            }
	            // ends here
	            // if connection not null then close the connection
				if (mcomCon != null) {
					mcomCon.close("FOCBatchTransferApprovalAction#processBatchApprove");
					mcomCon = null;
				}
	            if (showLogs) {
	                BatchFocFileProcessLog.focBatchMasterLog(methodName, focBatchMasterVO, "FINALLY BLOCK : Batch processed",
	                    "CURRENT LEVEL=" + currentLevel + ", PROCESSED RECORD=" + arr[0]);
	            }
	           /* if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exited forward=" + forward);
	            }*/
	        }
	        return response;
	    
		  
		  
		  
	  }
	  
	  
	  
	  

	    /**
	     * Cancel the order in batch
	     * This method handles all level of approval and also cancel the order
	     * request
	     * This method will call from rejact button of third jsp
	     * 
	     * @param mapping
	     * @param form
	     * @param request
	     * @param response
	     * @return
	     * @throws BTSLBaseException 
	     */
	  private O2CBatchApRejTransferResponse cancelFOCOrder(O2CBatchApRejTransferResponse response,
				  ChannelUserVO senderVO,CommisionBulkApprovalOrRejectRequestData data,HttpServletRequest httprequest,
				  HttpServletResponse responseSwag,String serviceType,int MAX_APP_LVL, ArrayList focBatchMasterVOList) throws BTSLBaseException {
	        final String methodName = "cancelOrder";

	       
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
	        //ActionForward forward = null;
			Connection con = null;
			MComConnectionI mcomCon = null;
	        ArrayList errorList = null;
	        FOCBatchMasterVO focBatchMasterVO = null;
	        String currentLevel = null;
	        final String arr[] = new String[1];
	        boolean showLogs = true;
	        ProcessStatusVO processVO = null;
	        boolean processRunning = true;
	        Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
	        ErrorMap errorMap = new ErrorMap();
	        O2CBatchApprovalDetailsResponse o2CBatchApprovalDetailsResponse=null;
	        BatchO2CTransferDAO batchO2CTransferDAO =null;


	        try {
	        	
	        	
	        	o2CBatchApprovalDetailsResponse=new O2CBatchApprovalDetailsResponse();
	        	batchO2CTransferDAO=new BatchO2CTransferDAO();

	            
	            // as to check the status of the batch foc process into the table so
	            // that only
	            // one instance should be executed for batch foc

	            final ProcessBL processBL = new ProcessBL();
	            try {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
	                processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.FOC_BATCH_PROCESS_ID,senderVO.getNetworkID());
	            } catch (BTSLBaseException e) {
	                log.error(methodName, "Exception:e=" + e);
	                log.errorTrace(methodName, e);
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, "batchfoc.processfile.error.alreadyexecution");
	            }
	            if (processVO != null && !processVO.isStatusOkBool()) {
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, "batchfoc.processfile.error.alreadyexecution");
	            }
	            mcomCon.partialCommit();
	            processVO.setNetworkCode(senderVO.getNetworkID());
	            // ends here

	            //final FOCBatchForm theForm = (FOCBatchForm) form;
	            String statusUsed = null;
	            String remark = null;
	            // load the login user information from the session
	            // Based on the levl of approval set the status to be used.
	            // That status is used to load the data for batch rejection
	            if ("approval1".equals(data.getRequestType())) {
	                remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
	            }
	            if ("approval2".equals(data.getRequestType())) {
	                remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
	            }
	            if ("approval3".equals(data.getRequestType())) {
	                remark = data.getRemarks();
	                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
	                currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
	            }
	            if (!BTSLUtil.isNullString(remark) && remark.length() > 100) {
	                showLogs = false;
	                throw new BTSLBaseException("batchfoc.batchfocapprove.msg.invalidlengthremark", "batchRejectFOCTransfer");
	            }
	            final FOCBatchTransferDAO focBatchTransferDAO = new FOCBatchTransferDAO();
	            final FOCBatchTransferWebDAO focBatchTransferWebDAO = new FOCBatchTransferWebDAO();
	            LinkedHashMap downloadDataMap = null;

	            // get the data of items from the form.
	            // If it is null the load from the database
	       
	            downloadDataMap = focBatchTransferDAO.loadBatchItemsMap(con, data.getBatchId(), statusUsed);
	            
	            if (downloadDataMap != null && !downloadDataMap.isEmpty()) {

	                FOCBatchItemsVO focBatchItemVO = null;
	                final Iterator iterator = downloadDataMap.keySet().iterator();
	                String key = null;
	                int i = 1;
	                boolean isNotApprovalLevel1 = true;
		            String exelFileID = null;

	                while (iterator.hasNext()) {
	                    key = (String) iterator.next();
	                    focBatchItemVO = (FOCBatchItemsVO) downloadDataMap.get(key);
	                    focBatchItemVO.setRecordNumber(i + 1);
	                    if ("approval1".equals(data.getRequestType())) {
	                        focBatchItemVO.setFirstApproverRemarks(remark);
	                        exelFileID = ExcelFileIDI.BATCH_FOC_APPRV1;
			                isNotApprovalLevel1 = false;
	                    } else if ("approval2".equals(data.getRequestType())) {
	                        focBatchItemVO.setSecondApproverRemarks(remark);
	                        exelFileID = ExcelFileIDI.BATCH_FOC_APPRV2;

	                    } else if ("approval3".equals(data.getRequestType())) {
	                        focBatchItemVO.setThirdApproverRemarks(remark);
	                        exelFileID = ExcelFileIDI.BATCH_FOC_APPRV3;

	                    }
	                    focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
	                    i++;
	                }
	                String approvalLevelf ="";
	                
	                if ("approval1".equals(data.getRequestType())) {
                        
	                	approvalLevelf="1";
                    } else if ("approval2".equals(data.getRequestType())) {
                    	approvalLevelf="2";
                    } else if ("approval3".equals(data.getRequestType())) {
                    	
                    	approvalLevelf="3";
                        
                    }
	                focBatchMasterVO = (FOCBatchMasterVO) getBatchDetailsComm(focBatchMasterVOList,data.getBatchId());
	                if(focBatchMasterVO == null)
	                {
		             	  throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DET_NOT_FOUND_BULK);
		               
	                }



	                
	                // Check if batch is modified or not
	                final boolean isModified = focBatchTransferDAO.isBatchModified(con, focBatchMasterVO.getModifiedOn().getTime(), data.getBatchId());
	                if (isModified) {
		             	   throw new BTSLBaseException(this, methodName, "batchfoc.batchfocapprovereject.msg.batchmodify");
	                } else {
	                    final int updateCount = focBatchTransferDAO.updateBatchStatus(con, focBatchItemVO.getBatchId(), PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS,
	                        PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
	                    if (updateCount <= 0) {
	                    	mcomCon.partialRollback();
	                        throw new BTSLBaseException(this, methodName, "error.general.processing", "firstPage");
	                    }
	                    mcomCon.partialCommit();
	                    // cancel the batch
	                    errorList = focBatchTransferWebDAO.processOrderByBatchRest(con, downloadDataMap, currentLevel,
                        		senderVO.getUserID(),locale, data.getLanguage1(), data.getLanguage2());
	                    

	                    // success message
	                    if (errorList == null || errorList.isEmpty()) {
	                       
	                      String msg = null;
                       	 arr[0] = String.valueOf(downloadDataMap.size());
                       	 msg=PretupsRestUtil.getMessageString("batchfoc.batchfocreject.msg.bachprocesssuccessfully",arr);
                       	 response.setMessageCode("batchfoc.batchfocreject.msg.bachprocesssuccessfully");
                       	 response.setStatus("200");
             				response.setBatchID(String.valueOf( focBatchMasterVO.getBatchId()));
             				response.setMessage(msg);
             				response.setProcessedRecs((String.valueOf(downloadDataMap.size())));
                           response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
                       	response.setReferenceId(downloadDataMap.size());
                        response.setService(serviceType);
             				responseSwag.setStatus(HttpStatus.SC_OK);
	                    
	                    
	                    } 
	                    else
	                    {
	                    	
	    	                
	                    	 boolean bulkCommissionPayout=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue();	
        					 if(!bulkCommissionPayout)
        					 {
        						 o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.FOC_BATCH_TRANSACTION_ID);
        					 }
        					 else {
        						 o2CBatchApprovalDetailsResponse = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,data.getBatchId(), senderVO.getUserID(), focBatchItemVO.getStatus(),PretupsI.DP_BATCH_TRANSACTION_ID);

        					 }
        					 String name= o2CBatchApprovalDetailsResponse.getApprovalDetails().getBatchFileName();
	    			          String extension =getExtensionByApacheCommonLib(name);
	    			           String filePath = Constants.getProperty("DownloadFilePathForO2CApproval");
	    			           try {
	    			                final File fileDir = new File(filePath);
	    			                if (!fileDir.isDirectory()) {
	    			                    fileDir.mkdirs();
	    			                }
	    			            } catch (Exception e) {
	    			                log.errorTrace("loadDownloadFile", e);
	    			                log.error("loadDownloadFile", "Exception" + e.getMessage());
	    			                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "error");
	    			            }
	    			           String fileType= null;
	    			           String fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime();
	    			           if(extension.equals("csv")) {
	    			        	    fileType= "."+extension;
	    			        	    fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	    			        	    writeToCsvforWithdrawalandFOC(downloadDataMap, filePath+fileName,isNotApprovalLevel1,locale,approvalLevelf);
	    				          }
	    			            else if (extension.equals("xlsx")) {
	    			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	    			            	fileType= "."+extension;
	    					        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

	    			            	 ExcelRW excelRW = new ExcelRW();
	    					         excelRW
	    					             .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	    			            }
	    			            else {
	    			            	String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevelf);
	    			            	fileType= ".xls";
	    			            	fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
	    			            	 extension="xls";
	    			            	 ExcelRW excelRW = new ExcelRW();
	    					            // Write the excel file for download
	    					            excelRW
	    					                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
	    					           
	    			            }
	    			            File download =new File(filePath+fileName);
	    						byte[] fileContent = FileUtils.readFileToByteArray(download);
	    				   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
	    					
	                    	
	                    	
	                    	 if (errorList.size() == downloadDataMap.size()) {
	 	                       
		                      	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

		                        response.setProcessedRecs((String.valueOf(errorList.size())));
	                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
	                          // theForm.setViewErrorLog("Y");
	                            if (errorList != null && !errorList.isEmpty()) {
	                                int errorListSize = errorList.size();
	                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
	                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
	                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
	                                    {
	                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
	                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
	                  		            	MasterErrorList masterErrorList = new MasterErrorList();
	                  						masterErrorList.setErrorCode(errorVO.getIDValue());
	                  						String msg = errorVO.getOtherInfo2();
	                  						masterErrorList.setErrorMsg(msg);
	                  						masterErrorLists.add(masterErrorList);
	                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
	                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
	                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
	                  						if(errorMap.getRowErrorMsgLists() == null)
	                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
	                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
	                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,false);
	                                    }
	                                }
	                            }
	                            
	                            response.setStatus("400");
	                            response.setService(serviceType);
	            	            response.setErrorMap(errorMap);
	            				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
	            				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
	                      	    response.setReferenceId(0);
	                      	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	            				response.setMessage(PretupsRestUtil.getMessageString("batchfoc.batchfocreject.msg.bachprocessfailed"));
	                            
	                            return response;
	                            
	                          
		                    
		                    
		                    }
		                    // success message with showing invalid MSISDN list
		                    else {
		                      
	                      	    responseSwag.setStatus(HttpStatus.SC_OK);

		                        arr[0] = String.valueOf(downloadDataMap.size() - errorList.size());
		                           // theForm.setProcessedRecs(String.valueOf(errorList.size()));
		                            //theForm.setNoOfRecords(String.valueOf(downloadDataMap.size()));
		                            response.setProcessedRecs((String.valueOf(errorList.size())));
		                            response.setNoOfRecords((String.valueOf(downloadDataMap.size())));
		                            
		                            if (errorList != null && !errorList.isEmpty()) {
		                                int errorListSize = errorList.size();
		                                for (int i2 = 0, j = errorListSize; i2 < j; i2++) {
		                                	ListValueVO errorVO = (ListValueVO) errorList.get(i2);
		                  		            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
		                                    {
		                  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		                  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		                  		            	MasterErrorList masterErrorList = new MasterErrorList();
		                  						masterErrorList.setErrorCode(errorVO.getIDValue());
		                  						String msg = errorVO.getOtherInfo2();
		                  						masterErrorList.setErrorMsg(msg);
		                  						masterErrorLists.add(masterErrorList);
		                  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
		                  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
		                  						rowErrorMsgLists.setRowName(errorVO.getCodeName());
		                  						if(errorMap.getRowErrorMsgLists() == null)
		                  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
		                  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		                  						writeFileForResponseComm( response,  errorMap,  responseSwag, encodedString, extension,true);
		                                    }
		                                }
		                            }
		                            
		                        	response.setStatus("400");
		            	            response.setErrorMap(errorMap);
		            	            response.setService(serviceType);
		            				response.setBatchID(String.valueOf(focBatchMasterVO.getBatchId()));
		            				response.setMessageCode(PretupsErrorCodesI.BULK_APPRV_FAILED);
		                      	    response.setReferenceId(0);
		                      	    responseSwag.setStatus(HttpStatus.SC_OK);
		                      	    response.setMessage(PretupsRestUtil.getMessageString("batchfoc.batchfocreject.msg.bachprocessfailedsuccess", arr));
		                          
		                            return response;
		                     
		                    }
	                    }
	                    
	                    
	                }
	            } else {
	                
	            	
	            	  throw new BTSLBaseException("batchfoc.batchfocapprove.msg.nochildrecordfound", methodName);

	            	
	            }
	        } catch(BTSLBaseException be){
		  		if(log.isDebugEnabled())
		  		    log.debug( "processBatcDirectPayouthApprove()", "Exceptin:be=" + be);
		            log.error( "processBatcDirectPayouthApprove()", "Exceptin:be=" + be);
	     	        throw be;
	         } 
	        catch (Exception e) {

	            log.error(methodName, "Exception:e=" + e);
	            log.errorTrace(methodName, e);
	            if (showLogs) {
	                BatchFocFileProcessLog.focBatchMasterLog(methodName, focBatchMasterVO, "FAIL : Error message is =" + e.getMessage(), "CURRENT LEVEL=" + currentLevel);
	            }
 	            throw new BTSLBaseException(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        } finally {
	            // added by sandeep goel
	            // as to make the status of the batch foc process as complete into
	            // the table so that only
	            // one instance should be executed for batch foc

	            if (processRunning) {
	                try {
	                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
	                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
	                    if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
	                        mcomCon.finalCommit();
	                    } else {
	                        mcomCon.finalRollback();
	                    }
	                } catch (Exception e) {
	                    if (log.isDebugEnabled()) {
	                        log.error(methodName, " Exception in update process detail for batch foc cancel order " + e.getMessage());
	                    }
	                    log.errorTrace(methodName, e);
	                }
	            }
	            // ends here
	            // if connection not null then close the connection
				if (mcomCon != null) {
					mcomCon.close("FOCBatchTransferApprovalAction#cancelOrder");
					mcomCon = null;
				}
	            /*if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting forward:=" + forward);
	            }*/
	            if (showLogs) {
	                BatchFocFileProcessLog.focBatchMasterLog(methodName, focBatchMasterVO, "FINALLY BLOCK : Process completed",
	                    "CURRENT LEVEL=" + currentLevel + ", PROCESSED RECORD=" + arr[0]);
	            }
	        }
	        return response;
		

	    }
	  
	  private void writeToCsv(LinkedHashMap<String, Object> meterMap,String filename,boolean p_level2DetailsShown) {
			 String METHOD_NAME = "writeToCsv";
			 String FILE_HEADER = "batcho2c.downloadfileforo2cbatch.label.batchdetailno"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.msisdn"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.loginid"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.batchno"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.usercategory"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.usergrade"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.esternaltxnno"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.paymenttype"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.initiatorby"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.initiatoron"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.externaltxndate"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.externalcode"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.reqquantity"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.payableamount"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.netpayableamount"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.commissionprofilesetid"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.commissionprofileversion"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.commissionprofiledetail"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.tax1type"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.tax1rate"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.tax1value"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.tax2type"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.tax2rate"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.tax2value"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.tax3type"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.tax3rate"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.tax3value"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.commissiontype"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.commisionrate"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.commissionvalue"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforapproval.label.apprv1by"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforapproval.label.apprv1on"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.reqquantity"+",";
			  	FILE_HEADER += "batcho2c.downloadfileforapproval.label.apprv1qty"+",";
		        if (p_level2DetailsShown) {
		        	FILE_HEADER += "batcho2c.downloadfileforapproval.label.currentstatus"+",";
		        	FILE_HEADER += "batcho2c.downloadfileforapproval.label.requiredaction"+",";
		        	FILE_HEADER += "batcho2c.downloadfileforapproval.label.remarks";
		        } else {
		        	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.currentstatus"+",";
		        	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.requiredaction"+",";
		        	FILE_HEADER += "batcho2c.downloadfileforo2cbatch.label.remarks";
		        }
		        
		        
		        
		        
		        try {
		        	PrintWriter writer = new PrintWriter(new File(filename));
		        	BatchO2CItemsVO batchO2CItemsVO = null;
		        	StringBuilder fw = new StringBuilder();
		            fw.append(FILE_HEADER);
		            fw.append(NEW_LINE_SEPARATOR);
		        	Iterator<String> iterator = meterMap.keySet().iterator();
		        	String key =null;
		        	String value=null;
		        	  while (iterator.hasNext()) {
		        		
		        		  key = (String) iterator.next();
		        		  batchO2CItemsVO = (BatchO2CItemsVO) meterMap.get(key);
		        		 
		        		  value = batchO2CItemsVO.getMsisdn()+",";
		        		  value +=  batchO2CItemsVO.getLoginID()+",";
		        		  value +=  batchO2CItemsVO.getBatchId()+",";
		        		  value +=  batchO2CItemsVO.getCategoryName()+",";
		        		  value +=  batchO2CItemsVO.getGradeName()+",";
		        		  value +=  batchO2CItemsVO.getExtTxnNo()+",";
		        		  value +=  batchO2CItemsVO.getPaymentType()+",";
		        		  value +=  batchO2CItemsVO.getInitiaterName()+",";
		        		  try {
		        			  value += BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getInitiatedOn()))+",";
		                  } catch (Exception e) {
		                	  value+= ""+",";
		                      log.errorTrace(METHOD_NAME, e);
		                  }
		                  try {
		                	  value += BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getExtTxnDate()))+",";	
		                  } catch (Exception e) {
		                	  value+= ""+",";
		                      log.errorTrace(METHOD_NAME, e);
		                  }
		                  value += batchO2CItemsVO.getExternalCode()+",";
		                  try {
		                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity())+",";
		                  } catch (Exception e) {
		                	  value+= ""+",";
		                      log.errorTrace(METHOD_NAME, e);
		                  }
		                  try {
		                	  
		                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getPayableAmount())+",";
		                  } catch (Exception e) {
		                	  value+= ""+",";
		                      log.errorTrace(METHOD_NAME, e);
		                  }
		                  try {
		                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getNetPayableAmount())+",";
		                  } catch (Exception e) {
		                	  value+= ""+",";
		                      log.errorTrace(METHOD_NAME, e);
		                  }
		                  value += batchO2CItemsVO.getCommissionProfileSetId()+",";
		                  value += batchO2CItemsVO.getCommissionProfileVer()+",";
		                  value += batchO2CItemsVO.getCommissionProfileDetailId()+",";
		                  value += batchO2CItemsVO.getTax1Type()+",";
		                  if(batchO2CItemsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
		                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax1Rate())+",";	
		                  else
		                	  value += String.valueOf(batchO2CItemsVO.getTax1Rate())+",";
		                  
		                  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax1Value())+",";
		                  value += batchO2CItemsVO.getTax2Type()+",";
		                  if(batchO2CItemsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
		                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax2Rate())+",";	
		                  else
		                	  value += String.valueOf(batchO2CItemsVO.getTax2Rate())+",";
		                  
		                  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax2Value())+",";
		                  value += batchO2CItemsVO.getTax3Type()+",";
		                  if(batchO2CItemsVO.getTax3Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
		                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax3Rate())+",";	
		                  else
		                	  value += String.valueOf(batchO2CItemsVO.getTax3Rate())+",";
		                  
		                  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax3Value())+",";
		                  value += batchO2CItemsVO.getCommissionType()+",";
		      			 if(batchO2CItemsVO.getCommissionType().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
		                  	value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getCommissionRate())+",";	
		                  else
		                	  value += String.valueOf(batchO2CItemsVO.getCommissionRate())+",";
		      			 	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getCommissionValue())+",";
		      			 	  value += batchO2CItemsVO.getFirstApproverName()+",";
//		                  try {
//		                      fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getFirstApprovedOn()));
//		                  } catch (Exception e) {
//		                      log.errorTrace(METHOD_NAME, e);
//		                  }
		                  try {
		                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity())+",";
		                  } catch (Exception e) {
		                	  value+= ""+",";
		                      log.errorTrace(METHOD_NAME, e);
		                  }
		                  try {
		                      if(0 == batchO2CItemsVO.getFirstApprovedQuantity())
		                    	  value += ""+",";
		                  	else
		                  		value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getFirstApprovedQuantity())+",";
		                  	
		                  } catch (Exception e) {
		                	  value+= ""+",";
		                      log.errorTrace(METHOD_NAME, e);
		                  }
		                  
		                  value += ((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, batchO2CItemsVO.getStatus())).getLookupName();
		                 
		             
		        		  try {
			                    fw.append(key);
			                    fw.append(COMMA_DELIMITER);
			                    fw.append(value);
			                    fw.append(NEW_LINE_SEPARATOR);
			                    
			                } catch (Exception e) {
			                    e.printStackTrace();
			                } finally {
			                    iterator.remove();
			                }
		        		  
		        	  }
		        	
		           writer.write(fw.toString());
		         writer.close();
		        }
		        catch (Exception e) {
		            e.printStackTrace();
		         	log.error("ne", "Exception:e=" + e);
		            log.errorTrace("new", e);
		        }
		     }
		
		
		
		private String[][] constructFileArrForDownload(LinkedHashMap p_dataMap, boolean p_level2DetailsShown) throws Exception {
	        final String METHOD_NAME = "constructFileArrForDownload";
	        if (log.isDebugEnabled()) {
	            log.debug("constructFileArrForDownload", "Entered p_dataMap=" + p_dataMap + "p_level2DetailsShown=" + p_level2DetailsShown);
	        }
	        final int rows = p_dataMap.size();
	        String fileArr[][];
	            fileArr = new String[rows + 1][37];
	        fileArr[0][0] = "batcho2c.downloadfileforo2cbatch.label.batchdetailno";
	        fileArr[0][1] = "batcho2c.downloadfileforo2cbatch.label.msisdn";
	        fileArr[0][2] = "batcho2c.downloadfileforo2cbatch.label.loginid";
	        fileArr[0][3] = "batcho2c.downloadfileforo2cbatch.label.batchno";
	        fileArr[0][4] = "batcho2c.downloadfileforo2cbatch.label.usercategory";
	        fileArr[0][5] = "batcho2c.downloadfileforo2cbatch.label.usergrade";
	        fileArr[0][6] = "batcho2c.downloadfileforo2cbatch.label.esternaltxnno";
	        fileArr[0][7] = "batcho2c.downloadfileforo2cbatch.label.paymenttype";
	        fileArr[0][8] = "batcho2c.downloadfileforo2cbatch.label.initiatorby";
	        fileArr[0][9] = "batcho2c.downloadfileforo2cbatch.label.initiatoron";
	        fileArr[0][10] = "batcho2c.downloadfileforo2cbatch.label.externaltxndate";
	        fileArr[0][11] = "batcho2c.downloadfileforo2cbatch.label.externalcode";
	        fileArr[0][12] = "batcho2c.downloadfileforo2cbatch.label.reqquantity";
	        fileArr[0][13] = "batcho2c.downloadfileforo2cbatch.label.payableamount";
	        fileArr[0][14] = "batcho2c.downloadfileforo2cbatch.label.netpayableamount";
	        fileArr[0][15] = "batcho2c.downloadfileforo2cbatch.label.commissionprofilesetid";
	        fileArr[0][16] = "batcho2c.downloadfileforo2cbatch.label.commissionprofileversion";
	        fileArr[0][17] = "batcho2c.downloadfileforo2cbatch.label.commissionprofiledetail";
	        fileArr[0][18] = "batcho2c.downloadfileforo2cbatch.label.tax1type";
	        fileArr[0][19] = "batcho2c.downloadfileforo2cbatch.label.tax1rate";
	        fileArr[0][20] = "batcho2c.downloadfileforo2cbatch.label.tax1value";
	        fileArr[0][21] = "batcho2c.downloadfileforo2cbatch.label.tax2type";
	        fileArr[0][22] = "batcho2c.downloadfileforo2cbatch.label.tax2rate";
	        fileArr[0][23] = "batcho2c.downloadfileforo2cbatch.label.tax2value";
	        fileArr[0][24] = "batcho2c.downloadfileforo2cbatch.label.tax3type";
	        fileArr[0][25] = "batcho2c.downloadfileforo2cbatch.label.tax3rate";
	        fileArr[0][26] = "batcho2c.downloadfileforo2cbatch.label.tax3value";
	        fileArr[0][27] = "batcho2c.downloadfileforo2cbatch.label.commissiontype";
	        fileArr[0][28] = "batcho2c.downloadfileforo2cbatch.label.commisionrate";
	        fileArr[0][29] = "batcho2c.downloadfileforo2cbatch.label.commissionvalue";
	        fileArr[0][30] = "batcho2c.downloadfileforapproval.label.apprv1by";
	        fileArr[0][31] = "batcho2c.downloadfileforapproval.label.apprv1on";
	        fileArr[0][32] = "batcho2c.downloadfileforo2cbatch.label.reqquantity";
	        fileArr[0][33] = "batcho2c.downloadfileforapproval.label.apprv1qty";
	        if (p_level2DetailsShown) {
	            fileArr[0][34] = "batcho2c.downloadfileforapproval.label.currentstatus";
	            fileArr[0][35] = "batcho2c.downloadfileforapproval.label.requiredaction";
	            fileArr[0][36] = "batcho2c.downloadfileforapproval.label.remarks";
	        } else {
	            fileArr[0][34] = "batcho2c.downloadfileforo2cbatch.label.currentstatus";
	            fileArr[0][35] = "batcho2c.downloadfileforo2cbatch.label.requiredaction";
	            fileArr[0][36] = "batcho2c.downloadfileforo2cbatch.label.remarks";
	        }
	        BatchO2CItemsVO batchO2CItemsVO = null;
	        final Iterator iterator = p_dataMap.keySet().iterator();
	        String key = null;
	        int i = 0;
	        while (iterator.hasNext()) {
	            key = (String) iterator.next();
	            batchO2CItemsVO = (BatchO2CItemsVO) p_dataMap.get(key);
	            int col = 0;
	            fileArr[i + 1][col++] = batchO2CItemsVO.getBatchDetailId();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getMsisdn();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getLoginID();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getBatchId();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getCategoryName();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getGradeName();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getExtTxnNo();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getPaymentType();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getInitiaterName();
	            try {
	                fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getInitiatedOn()));
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getExtTxnDate()));
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            fileArr[i + 1][col++] = batchO2CItemsVO.getExternalCode();
	            try {
	                fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity());
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getPayableAmount());
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getNetPayableAmount());
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            fileArr[i + 1][col++] = batchO2CItemsVO.getCommissionProfileSetId();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getCommissionProfileVer();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getCommissionProfileDetailId();
	            fileArr[i + 1][col++] = batchO2CItemsVO.getTax1Type();
	            if(batchO2CItemsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
	            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax1Rate());	
	            else
	            fileArr[i + 1][col++] = String.valueOf(batchO2CItemsVO.getTax1Rate());
	            
	            fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax1Value());
	            fileArr[i + 1][col++] = batchO2CItemsVO.getTax2Type();
	            if(batchO2CItemsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
	            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax2Rate());	
	            else
	            fileArr[i + 1][col++] = String.valueOf(batchO2CItemsVO.getTax2Rate());
	            
	            fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax2Value());
	            fileArr[i + 1][col++] = batchO2CItemsVO.getTax3Type();
	            if(batchO2CItemsVO.getTax3Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
	            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax3Rate());	
	            else
	            fileArr[i + 1][col++] = String.valueOf(batchO2CItemsVO.getTax3Rate());
	            
	            fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax3Value());
	            fileArr[i + 1][col++] = batchO2CItemsVO.getCommissionType();
				 if(batchO2CItemsVO.getCommissionType().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
	            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getCommissionRate());	
	            else
	            fileArr[i + 1][col++] = String.valueOf(batchO2CItemsVO.getCommissionRate());
	            fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getCommissionValue());
	            fileArr[i + 1][col++] = batchO2CItemsVO.getFirstApproverName();
//	            try {
//	                fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getFirstApprovedOn()));
//	            } catch (Exception e) {
//	                log.errorTrace(METHOD_NAME, e);
//	            }
	            try {
	            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity());
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if(0 == batchO2CItemsVO.getFirstApprovedQuantity())
	            		fileArr[i + 1][col++] = "";
	            	else
	            		fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getFirstApprovedQuantity());
	            	
	            } catch (Exception e) {
	                log.errorTrace(METHOD_NAME, e);
	            }
	            
	            fileArr[i + 1][col++] = ((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, batchO2CItemsVO.getStatus())).getLookupName();
	            i++;
	        }
	        if (log.isDebugEnabled()) {
	            log.debug("constructFileArrForDownload", "Exiting fileArr:=" + fileArr);
	        }
	        return fileArr;
	    }
	
		
		
		@SuppressWarnings("unlikely-arg-type")
		private void writeFileForResponseO2c(O2CBatchApRejTransferResponse response, ErrorMap errorMap, HttpServletResponse responseSwag,String fileAttachment,String filetype,boolean partialProcess)throws BTSLBaseException, IOException {
			if(errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
	    		return ;
			
			
			ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
			errorFileRequestVO.setRowErrorMsgLists(errorMap.getRowErrorMsgLists());
			errorFileRequestVO.setFile(fileAttachment);
			errorFileRequestVO.setFiletype(filetype);
			if(partialProcess) {
				errorFileRequestVO.setPartialFailure(true);
			}
			
			ErrorFileResponse errorFileResponse = new ErrorFileResponse();
			DownloadUserListService downloadUserListService = new DownloadUserListServiceImpl();
			downloadUserListService.downloadErrorFile(errorFileRequestVO,  errorFileResponse,  responseSwag);
			response.setFileAttachment(errorFileResponse.getFileAttachment());
			response.setFileName(errorFileResponse.getFileName());
			response.setFileType(filetype);
			
	
		}
		
		

private String[][] constructFileArrForDownloadForFOCandWithdrawal(LinkedHashMap p_dataMap, boolean p_level2DetailsShown,Locale p_locale,String approvedlevel) throws Exception {
		
        final String METHOD_NAME = "constructFileArrForDownloadForFOCandWithdrawal";
        if (log.isDebugEnabled()) {
            log.debug("constructFileArrForDownload", "Entered p_dataMap Size=" + p_dataMap.size() + "p_level2DetailsShown=" + p_level2DetailsShown);
        }
        int approvedLevel=Integer.parseInt(approvedlevel);
        int cols = 15;
        if (approvedLevel == 1) {
            cols = cols + 2;
        } else if (approvedLevel == 2) {
            cols = cols + 4;
        }
        if (approvedLevel == 3) {
            cols = cols + 6;
        }

        final int rows = p_dataMap.size();
        String fileArr[][];
        fileArr = new String[rows+1][cols];
        int i=0;
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchdetailno", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.msisdn", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usercategory", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usergrade", null);
        fileArr[0][i++] =BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.loginid", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchno", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.reqquantity", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.esternaltxnno", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externaltxndate", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externalcode", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatorby", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatoron", null);
       
    
        if (approvedLevel >= 1)// for first approval required then add
            // column in string array
            {
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.apprv1on", null);
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.apprv1by", null);
            }
            if (approvedLevel >= 2)// for second approval required then add
            // column in string array
            {
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.secondappron", null);
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.secondapprby", null);
            }
            if (approvedLevel >= 3)// for third approval required then add
            // column in string array
            {
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.thirdappron", null);
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.thirdapprby", null);
            }
        
        if (p_level2DetailsShown) {
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null);
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null);
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
        } else {
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null);
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null);
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
        }
           
        FOCBatchItemsVO batchO2CItemsVO = null;
        final Iterator iterator = p_dataMap.keySet().iterator();
        String key = null;
         i = 0;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            batchO2CItemsVO = (FOCBatchItemsVO) p_dataMap.get(key);
            int col = 0;
            fileArr[i + 1][col++] = batchO2CItemsVO.getBatchDetailId();
            fileArr[i + 1][col++] = batchO2CItemsVO.getMsisdn();
            fileArr[i + 1][col++] = batchO2CItemsVO.getCategoryName();
            fileArr[i + 1][col++] = batchO2CItemsVO.getGradeName();
            fileArr[i + 1][col++] = batchO2CItemsVO.getLoginID();
            fileArr[i + 1][col++] = batchO2CItemsVO.getBatchId();
            try {
                fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity());
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            fileArr[i + 1][col++] = batchO2CItemsVO.getExtTxnNo();
            fileArr[i + 1][col++] = batchO2CItemsVO.getExtTxnDateStr();
            fileArr[i + 1][col++] = batchO2CItemsVO.getExternalCode();
            fileArr[i + 1][col++] = batchO2CItemsVO.getInitiaterName();
            try {
                fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getInitiatedOn()));
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
           
            if (approvedLevel >= 1)// for first approval required then add
                // column in string array
                {
                    if (batchO2CItemsVO.getFirstApprovedOn() != null) {
                    	fileArr[i+1][col++] = BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getFirstApprovedOn()));
                    } else {
                    	fileArr[i+1][col++] = null;
                    }
                    fileArr[i+1][col++] = batchO2CItemsVO.getFirstApprovedBy();
                   
                }
                if (approvedLevel >= 2)// for second approval required then add
                // column in string array
                {
                    if (batchO2CItemsVO.getSecondApprovedOn() != null) {
                    	fileArr[i+1][col++] = BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getSecondApprovedOn()));
                    } else {
                    	fileArr[i+1][col++] = null;
                    }
                    fileArr[i+1][col++] = batchO2CItemsVO.getSecondApprovedBy();
                    
                }
                if (approvedLevel >= 3)// for third approval required then add
                // column in string array
                {
                    if (batchO2CItemsVO.getThirdApprovedOn() != null) {
                    	fileArr[i+1][col++] = BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getThirdApprovedOn()));
                    } else {
                    	fileArr[i+1][col++] = null;
                    }
                    fileArr[i+1][col++] = batchO2CItemsVO.getThirdApprovedBy();
                  
                }
                fileArr[i + 1][col++] = ((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, batchO2CItemsVO.getStatus())).getLookupName();
                
                i++;
            
        }
        if (log.isDebugEnabled()) {
            log.debug("constructFileArrForDownload", "Exiting fileArr:=" + fileArr);
        }
        return fileArr;
    }
private void writeToCsvforWithdrawalandFOC(LinkedHashMap<String, Object> meterMap,String filename,boolean p_level2DetailsShown,Locale p_locale,String approvedlevel) {
	 String METHOD_NAME = "writeToCsvforWithdrawalandFOC";
	 int approvedLevel=Integer.parseInt(approvedlevel);
	 String FILE_HEADER =  BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchdetailno", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.msisdn", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usercategory", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usergrade", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.loginid", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchno", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.reqquantity", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.esternaltxnno", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externaltxndate", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externalcode", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatorby", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatoron", null)+",";
	 	
	 	
	 	 if (approvedLevel >= 1)// for first approval required then add
	            // column in string array
	            {
	 		FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.apprv1on", null)+",";
	 		FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.apprv1by", null)+",";
	            }
	            if (approvedLevel >= 2)// for second approval required then add
	            // column in string array
	            {
	            	FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.secondappron", null)+",";
	            	FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.secondapprby", null)+",";
	            }
	            if (approvedLevel >= 3)// for third approval required then add
	            // column in string array
	            {
	            	FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.thirdappron", null)+",";
	            	FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.thirdapprby", null)+",";
	            }
	        
       if (p_level2DetailsShown) {
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null)+",";
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null)+",";
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
       } else {
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null)+",";
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null)+",";
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
       }
       
       
       
       
       try {
       	PrintWriter writer = new PrintWriter(new File(filename));
       	FOCBatchItemsVO batchO2CItemsVO = null;
       	StringBuilder fw = new StringBuilder();
           fw.append(FILE_HEADER);
           fw.append(NEW_LINE_SEPARATOR);
       	Iterator<String> iterator = meterMap.keySet().iterator();
       	String key =null;
       	String value=null;
       	  while (iterator.hasNext()) {
       		
       		  key = (String) iterator.next();
       		  batchO2CItemsVO = (FOCBatchItemsVO) meterMap.get(key);
       		 
       		  value = batchO2CItemsVO.getMsisdn()+",";
       		  value +=  batchO2CItemsVO.getCategoryName()+",";
       		  value +=  batchO2CItemsVO.getGradeName()+",";
       		  value +=  batchO2CItemsVO.getLoginID()+",";
       		  value +=  batchO2CItemsVO.getBatchId()+",";
       		 try {
              	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity())+",";
                } catch (Exception e) {
              	  value+= ""+",";
                    log.errorTrace(METHOD_NAME, e);
                }
       		  value +=  batchO2CItemsVO.getExtTxnNo()+",";
       		 try {
              	  value += BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getExtTxnDate()))+",";	
                } catch (Exception e) {
              	  value+= ""+",";
                    log.errorTrace(METHOD_NAME, e);
                }
       		 value += batchO2CItemsVO.getExternalCode()+",";
       		 value +=  batchO2CItemsVO.getInitiaterName()+",";
       		  try {
       			  value += BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getInitiatedOn()))+",";
                 } catch (Exception e) {
               	  value+= ""+",";
                     log.errorTrace(METHOD_NAME, e);
                 }
                
       		  if (approvedLevel >= 1)// for first approval required then add
                  // column in string array
                  {
                      if (batchO2CItemsVO.getFirstApprovedOn() != null) {
                    	  value += BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getFirstApprovedOn()))+",";
                      } else {
                    	  value += " "+",";
                      }
                      value += batchO2CItemsVO.getFirstApprovedBy()+",";
                     
                  }
                  if (approvedLevel >= 2)// for second approval required then add
                  // column in string array
                  {
                      if (batchO2CItemsVO.getSecondApprovedOn() != null) {
                    	  value += BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getSecondApprovedOn()))+",";
                      } else {
                      	 value += ""+",";
                      }
                      value += batchO2CItemsVO.getSecondApprovedBy()+",";
                      
                  }
                  if (approvedLevel >= 3)// for third approval required then add
                  // column in string array
                  {
                      if (batchO2CItemsVO.getThirdApprovedOn() != null) {
                    	  value += BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getThirdApprovedOn()))+",";
                      } else {
                    	  value += ""+",";
                      }
                      value += batchO2CItemsVO.getThirdApprovedBy()+",";
                    
                  }
                
                
                 
                
                 
                 value += ((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, batchO2CItemsVO.getStatus())).getLookupName();
                
            
       		  try {
	                    fw.append(key);
	                    fw.append(COMMA_DELIMITER);
	                    fw.append(value);
	                    fw.append(NEW_LINE_SEPARATOR);
	                    
	                } catch (Exception e) {
	                    e.printStackTrace();
	                } finally {
	                    iterator.remove();
	                }
       		  
       	  }
       	
          writer.write(fw.toString());
        writer.close();
       }
       catch (Exception e) {
           e.printStackTrace();
        	log.error("ne", "Exception:e=" + e);
           log.errorTrace("new", e);
       }
    }



@SuppressWarnings("unlikely-arg-type")
private void writeFileForResponseComm(O2CBatchApRejTransferResponse response, ErrorMap errorMap, HttpServletResponse responseSwag,String fileAttachment,String filetype,boolean partialProcess)throws BTSLBaseException, IOException {
	if(errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
		return ;
	
	
	ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
	errorFileRequestVO.setRowErrorMsgLists(errorMap.getRowErrorMsgLists());
	errorFileRequestVO.setFile(fileAttachment);
	errorFileRequestVO.setFiletype(filetype);
	
	if(partialProcess) {
		errorFileRequestVO.setPartialFailure(true);
	}
	
	ErrorFileResponse errorFileResponse = new ErrorFileResponse();
	DownloadUserListService downloadUserListService = new DownloadUserListServiceImpl();
	downloadUserListService.downloadErrorFile(errorFileRequestVO,  errorFileResponse,  responseSwag);
	response.setFileAttachment(errorFileResponse.getFileAttachment());
	response.setFileName(errorFileResponse.getFileName());
	response.setFileType(filetype);
	
	
}





	
}
