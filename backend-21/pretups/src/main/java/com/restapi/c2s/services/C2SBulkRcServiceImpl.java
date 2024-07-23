package com.restapi.c2s.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.CategoryData;
import com.btsl.common.CategoryRespVO;
import com.btsl.common.CategoryResponseVO;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.channel.transfer.requesthandler.C2CStockTransferMultRequestVO;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListService;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryDomainCodeVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ScheduleFileProcessLog;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.restrictedsubs.businesslogic.ScheduleTopUpNowBL;
import com.btsl.pretups.restrictedsubs.businesslogic.ScheduledBatchDetailDAO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.scheduletopup.process.BatchFileParserI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.restapi.c2sservices.controller.RescheduleBatchRechargeRequestVO;
import com.restapi.c2sservices.controller.RescheduleBatchRechargeResponseVO;
import com.restapi.c2sservices.controller.ViewScheduleDetailsBatchResponseVO;
import com.restapi.c2sservices.controller.ViewScheduleDetailsListResponseVO;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.restrictedsubs.web.RestrictedTopUpForm;
import com.web.pretups.restrictedsubs.web.ViewScheduleForm;


@Service("C2SBulkRcServiceI")
public class C2SBulkRcServiceImpl implements C2SBulkRcServiceI{

	@Autowired
	private DownloadUserListService downloadUserListService;
	@Autowired
	private C2SBulkEVDProcessor c2SBulkEVDProcessor;

	private final Log log = LogFactory.getLog(this.getClass().getName());
	
	private final Log _log = LogFactory.getLog(this.getClass().getName());
	
	private ScheduleBatchMasterVO scheduleMasterVO = null;
    private String batchID = null;
    long failCount = 0;
    long totalCount = 0;
    String scheduleNowContentsSize=null;
    HashMap<String,List<String>> fileContent = new HashMap<String,List<String>>();
    HashMap<String,Object> scheduleInfoMap = new HashMap<String,Object>();
    C2SBulkRechargeResponseVO response = new C2SBulkRechargeResponseVO();
    ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
    LinkedHashMap<String, List<String>> bulkDataMap ;
    int records = 0;
    int errorRecords = 0;
    int totalRecords = 0;
    ArrayList fileContents = null; // contains all the data form the file
    RestrictedSubscriberVO errorVO = null;
    private C2CStockTransferMultRequestVO c2cStockTransferMultRequestVOs;
    //Bulk DVD
    private HttpServletResponse responseSwag;
    private DvdRequestVO dvdRequestVO;
    private DvdBulkResponse dvdResponse;
    private BaseResponseMultiple baseResponseMultiple=  null;
    private DvdDetails dvdDetails;
    private ArrayList<BaseResponse> baseResponseList = null;
    private List<RowErrorMsgLists> rowErrorMsgListsFinal = null;
  //  private ArrayList<MasterErrorList> masterErrorList= null;
    private ArrayList<TxnIDBaseResponse> txnIdDetailsList = null;
    private ErrorMap errorMap;
    private String txnBatchId= null;
    private int failureCount = 0 ;
    private String fileName = null;

	private ErrorFileResponse errorFileResponse;
    
	@Override
	public  C2SBulkRechargeResponseVO processRequest(C2SBulkRechargeRequestVO requestVO,String serviceKeyword,
			String requestIDStr,String requestFor,MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException {
        final String METHOD_NAME = "processRequest";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
		Connection con = null;
		MComConnectionI mcomCon = null;
        String contentsSize = null;
        String batchContentsSize = null;
    	//Schedule Now Recharge
		String scheduleNowContentsSize=null;
        HashMap<String,List<String>> fileContent = new HashMap<String,List<String>>();
        LinkedHashMap<String, List<String>> bulkDataMap ;
        HashMap scheduleInfoMap = new HashMap();
        boolean isErrorFound = false;
        String userpin ;
        String processedRecords = "";
        C2SBulkRechargeResponseVO response = new C2SBulkRechargeResponseVO();
        ArrayList finalList = new ArrayList();
        String arr[] = null;
    	Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
    	ErrorMap errorMap = new ErrorMap();
        try{
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
        	userpin = requestVO.getData().getPin();
            /*
			 * Authentication
			 * @throws BTSLBaseException
			 */			 
 	        OAuthenticationUtil.validateTokenApi(requestVO, headers,responseSwag);
 	       try {
 	            contentsSize = Constants.getProperty("RESTRICTED_MSISDN_LIST_SIZE");
 	            batchContentsSize = Constants.getProperty("BATCH_MSISDN_LIST_SIZE");
 				//Schedule Now Recharge
 				scheduleNowContentsSize=Constants.getProperty("SCHEDULE_NOW_BATCH_RECHARGE_FILE_SIZE").trim();
 				
 	        } catch (Exception e) {
 	            if (log.isDebugEnabled()) {
 	                log.debug(METHOD_NAME, "RESTRICTED_MSISDN_LIST_SIZE not defined in Constant Property file");
 	            }
 	            log.errorTrace(METHOD_NAME, e);
 	            throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.contentsizemissing");
 	        }
 	        ChannelUserVO channelUserVO=new ChannelUserVO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con,requestVO.getData().getMsisdn());			
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			  if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
                //  validateStaffLoginDetails(con, theForm, request, mapping, loginLoggerVO, channelUserVO);
                  UserDAO userDao = new UserDAO();
                  UserPhoneVO phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
                  if (phoneVO != null) {
                   channelUserVO.setActiveUserMsisdn(phoneVO.getMsisdn());
                   channelUserVO.setActiveUserPin(phoneVO.getSmsPin());
                  }
                  // Set Staff User Details
                  ChannelUserVO staffUserVO = new ChannelUserVO();
                  UserPhoneVO staffphoneVO = new UserPhoneVO();
                  BeanUtils.copyProperties(staffUserVO, channelUserVO);
                  if (phoneVO != null) {
                      BeanUtils.copyProperties(staffphoneVO, phoneVO);
                      staffUserVO.setUserPhoneVO(staffphoneVO);
                  }
                  staffUserVO.setPinReset(channelUserVO.getPinReset());
                  channelUserVO.setStaffUserDetails(staffUserVO);
                  ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());

                  staffUserDetails(channelUserVO, parentChannelUserVO);

                  channelUserVO.setPrefixId(parentChannelUserVO.getPrefixId());
              }
			if(PretupsI.YES.equals(channelUserVO.getUserPhoneVO().getPinRequired()))
			{
              if(BTSLUtil.isNullString(userpin))
              {
		          throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN, "");
              }
              else
              	{
              	try {
					ChannelUserBL.validatePIN(con, channelUserVO, userpin);
				} catch (BTSLBaseException be) {
					if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
							|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
						OracleUtil.commit(con);
					}
			        throw new BTSLBaseException(this, METHOD_NAME, be.getMessageKey(), "");
				}
              	}
			}
 	       
			if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO.getOutSuspened())) {
	            if (log.isDebugEnabled()) {
	                log.debug(METHOD_NAME, "USER IS OUT SUSPENDED IN THE SYSTEM");
	            }
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.OUT_SUSPENDED, "");
	        }
			
			if(requestVO.getData().getScheduleNow().equalsIgnoreCase("on")  && !isSameDay(new SimpleDateFormat("dd/MM/yy").parse(requestVO.getData().getScheduleDate()),new Date())) {
				   if (log.isDebugEnabled()) {
		                log.debug(METHOD_NAME, "PLESE SPECIFY TODAYS DATE");
		            }
	                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2C_BULK_NOT_CURRNT_DATE);
			}
			if(BTSLUtil.isDateBeforeToday(new SimpleDateFormat("dd/MM/yy").parse(requestVO.getData().getScheduleDate()))) {
				   if (log.isDebugEnabled()) {
		                log.debug(METHOD_NAME, "PLESE SPECIFY TODAYS DATE");
		            }
	                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2C_BULK_LESSTHAN_CURRNT_DATE);
			}
			
			
			//code for read file content
			ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
			HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_TYPE1, requestVO.getData().getFileType());
			fileDetailsMap.put(PretupsI.FILE_NAME,requestVO.getData().getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getData().getFile());
			fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, serviceKeyword);
			bulkDataMap = fileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);
			
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			
			String dataStr = null;
			fileContents =   getFileContentsList(bulkDataMap);
			records = fileContents.size();
			errorRecords = errorMap.getRowErrorMsgLists().size();
			totalRecords = records + errorRecords;
			response.setNumberOfRecords(totalRecords);
			// /
            // If file does not contain record as entered by the user then
            // show the error message
            // (records excludes blank lines)
            // /
			
            if (records  == 0) {
                if (log.isDebugEnabled()) {
                    log.debug(METHOD_NAME, "Number of records are zero : ");
                }
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2C_BULK_No_RECORDS, "");
            }
           /* if (records != Integer.parseInt(requestVO.getData().get)) {
                if (log.isDebugEnabled()) {
                    log.debug("processFile", "File contents size is not matching with specified size : " + fileContents.size());
                }
                throw new BTSLBaseException(this, "processFile", "restrictedsubs.scheduletopupdetails.msg.invalidnoofrecord", "scheduleDetail");
            }*/
			//Schedule Now Recharge
			// start code added to check if the number of records in file is more that the maximum size define in the constant.properties as value 
	
			if(requestVO.getData().getScheduleNow().equals("on"))
			{	
				try{
					if ((totalRecords) > Integer.parseInt(scheduleNowContentsSize))
					{
						if(log.isDebugEnabled())log.debug(METHOD_NAME,"File contents size of the file is not valid in constant properties file : "+fileContents.size());
						throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.noofrecordexeced",0,new String[]{scheduleNowContentsSize},"scheduleDetail");
					}
				}
				catch(BTSLBaseException be) {
					if(log.isDebugEnabled())log.debug(METHOD_NAME,"File contents size of the file is more than that in constant properties file : "+fileContents.size());
					log.errorTrace(METHOD_NAME,be);
					throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.noofrecordexeced",0,new String[]{scheduleNowContentsSize},"scheduleDetail");
				}
				catch(Exception e)
				{
					if(log.isDebugEnabled())
						log.debug(METHOD_NAME,"SCHEDULE_NOW_BATCH_RECHARGE_FILE_SIZE not defined in Constant Property file");
					log.errorTrace(METHOD_NAME,e);
					throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.contentsizemissing");
				}
				
			}	 
			// code ended 
            // /
            // it can not be allowed to process the file if MSISDN's are
            // more than the defined Limit
            // /
          
			if(requestVO.getData().getScheduleNow().equals("off"))
			{
				if(PretupsI.BATCH_TYPE_CORPORATE.equals(requestVO.getData().getBatchType()))
				{
					if((totalRecords) > Integer.parseInt(contentsSize))
					{    
						if(log.isDebugEnabled())log.debug(METHOD_NAME,"File contents size of the file is not valid in constant properties file : "+fileContents.size());
						throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.noofrecordexeced",0,new String[]{contentsSize},"scheduleDetail");
					}
				}
				else
				{
					if((totalRecords) > Integer.parseInt(batchContentsSize))
					{    
					if(log.isDebugEnabled())log.debug(METHOD_NAME,"File contents size of the file is not valid in constant properties file : "+fileContents.size());
					throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.noofrecordexeced",0,new String[]{batchContentsSize},"scheduleDetail");
					}
			    
				}
			}
			
			 // /
	        // Check for the duplicate mobile numbers in the list
	        // here we add the information in the new list called finalList it
	        // contains informaion about all
	        // the data.
	        // /
			 String duplicateMSISDN = "restrictedsubs.scheduletopupdetails.errorfile.msg.duplicatemsisdn";
	            if (fileContents != null && records > 0 ) {
	              
	                for (int i = 0; i < records; i++) {
	                    dataStr = (String) fileContents.get(i);
	                    arr = dataStr.split(",");
	                    errorVO = new RestrictedSubscriberVO();
	                    if (!isContain(finalList, arr[0])) {
	                        errorVO.setLineNumber((i + 1) + "");
	                        errorVO.setMsisdn(dataStr);
	                        finalList.add(errorVO);
	                    } else {
	                      //  ScheduleFileProcessLog.log("Processing File", requestVO.getCreatedBy(), arr[0], requestVO.getBatchID(), "Mobile number is Duplicate", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestVO.getRequestFor());
	                        errorVO.setLineNumber((i + 1) + "");
	                        errorVO.setMsisdn(arr[0] + "(D)");
	                        errorVO.setErrorCode(duplicateMSISDN);
	                        errorVO.setisErrorFound(true);
	                        errorVO.setAmount(Long.parseLong(arr[3]));
	                        finalList.add(errorVO);
	                    }
	                }
	            }
              int count = 0;
	            count = addBatch(con,requestVO, channelUserVO, serviceKeyword, totalRecords);
			if (count <= 0) {
	            mcomCon.finalRollback();
		        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2C_BULK_FILE_UNSUCCESSFUL, "");
	        }
       
	    ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceTypeObject(serviceKeyword, PretupsI.C2S_MODULE);
	    BatchFileParserI batchFileParserI = (BatchFileParserI) Class.forName(serviceKeywordCacheVO.getFileParser()).newInstance();
	    String[] uploadErrorList = batchFileParserI.getErrorKeys(requestVO.getData().getFileType());
        if (PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equalsIgnoreCase(serviceKeyword)
        		|| PretupsI.SERVICE_TYPE_VAS_RECHARGE.equalsIgnoreCase(serviceKeyword)
        		|| PretupsI.SERVICE_TYPE_GIFT_RECHARGE.equalsIgnoreCase(serviceKeyword)
        		|| PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN.equalsIgnoreCase(serviceKeyword) 
        		|| PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR.equalsIgnoreCase(serviceKeyword)
        		|| PretupsI.SERVICE_TYPE_POSTPAID_BILL_PAYMENT.equalsIgnoreCase(serviceKeyword)
        		|| PretupsI.SERVICE_TYPE_CHNL_DATA_RECHARGE.equalsIgnoreCase(serviceKeyword)
        		|| PretupsI.SERVICE_TYPE_DVD.equalsIgnoreCase(serviceKeyword)
        	    || PretupsI.SERVICE_TYPE_EVD.equalsIgnoreCase(serviceKeyword))
        
        {
            scheduleInfoMap.put(BatchFileParserI.ERROR_KEY, uploadErrorList);
            //scheduleInfoMap.put(BatchFileParserI.DATA_MAP, theForm.getDownLoadDataMap());
            scheduleInfoMap.put(BatchFileParserI.USER_ID, channelUserVO.getUserID());
            scheduleInfoMap.put(BatchFileParserI.OWNER_ID, channelUserVO.getOwnerID());
            scheduleInfoMap.put(BatchFileParserI.BATCH_ID, batchID);
            scheduleInfoMap.put("CREATED_BY", scheduleMasterVO.getCreatedBy());
            scheduleInfoMap.put("FINAL_LIST", finalList);
            scheduleInfoMap.put("FINAL_LIST_SIZE", records);
            scheduleInfoMap.put("FILE_NAME", requestVO.getData().getFileName());
            scheduleInfoMap.put("REQUEST_FOR", requestFor);
            //scheduleInfoMap.put("DOWNLOAD_BATCH_ID", scheduleMasterVO.getDownLoadBatchID());
            scheduleInfoMap.put("USER_VO", channelUserVO);
            scheduleInfoMap.put("SCHEDULED_VO", scheduleMasterVO);
            scheduleInfoMap.put("MODIFIED_ON", scheduleMasterVO.getModifiedOn());
            scheduleInfoMap.put("CREATED_ON", scheduleMasterVO.getCreatedOn());
            scheduleInfoMap.put("RECORDS", totalRecords);
            scheduleInfoMap.put("BATCH_TYPE", scheduleMasterVO.getBatchType());
        }
        BTSLMessages btslMessage = null;
        try {
            batchFileParserI.uploadFile(con, requestVO.getData().getFileType(), scheduleInfoMap, isErrorFound);
        } catch (BTSLBaseException e) {
            log.error(METHOD_NAME, "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
        }
        isErrorFound = ((Boolean) scheduleInfoMap.get("IS_ERROR_FOUND")).booleanValue();
        if (!isErrorFound) {

			if(!requestVO.getData().getScheduleNow().equals("on"))
			{
				
				if("Schedule".equalsIgnoreCase(requestFor))
				{
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BULK_SCH_SUCCESS,new String[]{String.valueOf( scheduleMasterVO.getBatchID())});
					 response.setStatus("200");
					 responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
					  response.setMessage(msg);
					  response.setScheduleBatchId(String.valueOf( scheduleMasterVO.getBatchID()));
					  response.setMessageCode(PretupsErrorCodesI.C2C_BULK_SCH_SUCCESS);
					//btslMessage = new BTSLMessages("restrictedsubs.scheduletopupdetails.msg.success",new String[]{scheduleMasterVO.getBatchID()},"firstPage");
				}
				else if("Reschedule".equals(requestFor))
				{
					//btslMessage = new BTSLMessages("restrictedsubs.rescheduletopupdetails.msg.success",new String[]{scheduleMasterVO.getRefBatchID(),scheduleMasterVO.getBatchID()},"firstPage");
				
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BULK_RESCH_SUCCESS,new String[]{scheduleMasterVO.getRefBatchID(),scheduleMasterVO.getBatchID()});
					response.setMessage(msg);  
					response.setScheduleBatchId(String.valueOf( scheduleMasterVO.getBatchID()));
					  response.setMessageCode(PretupsErrorCodesI.C2C_BULK_RESCH_SUCCESS);
					  responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
				}
				
			}
			 processedRecords = (String) scheduleInfoMap.get("PROCESSED_RECS");
        } else {
            ArrayList errorList = (ArrayList) scheduleInfoMap.get("FINAL_LIST");
            ArrayList list = new ArrayList();
            
            if (errorList != null) {
                int errorListSize = errorList.size();
                for (int i = 0, j = errorListSize; i < j; i++) {
                    errorVO = (RestrictedSubscriberVO) errorList.get(i);
		            if(!BTSLUtil.isNullString(errorVO.getErrorCode()))
                    {
		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		            	MasterErrorList masterErrorList = new MasterErrorList();
						masterErrorList.setErrorCode(errorVO.getErrorCode());
						String msg = RestAPIStringParser.getMessage(locale, errorVO.getErrorCode(), errorVO.getErrorCodeArgs());
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
						rowErrorMsgLists.setRowValue(errorVO.getMsisdn());
						rowErrorMsgLists.setRowName("Line" + String.valueOf(Long.parseLong(errorVO.getLineNumber()) + 2));
						if(errorMap.getRowErrorMsgLists() == null)
							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                    }
                }
            }
            
            processedRecords = (String) scheduleInfoMap.get("PROCESSED_RECS") ;
            if (("0").equals(processedRecords))
            {
            	MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorCode("restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile");
				String msg = RestAPIStringParser.getMessage(locale, "restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile", null);
				masterErrorList.setErrorMsg(msg);
				errorMap.setMasterErrorList(new ArrayList<MasterErrorList> ());
				errorMap.getMasterErrorList().add(masterErrorList);
            }
        }
            // Schedule Now Recharge
    		if (requestVO.getData().getScheduleNow() != null && !requestVO.getData().getScheduleNow().isEmpty())
    			{
    			
    			 if(requestVO.getData().getScheduleNow().equals("on"))
    			 {
    			
    				ScheduleTopUpNowBL upNowBL= new ScheduleTopUpNowBL(); 
    				
    				if (mcomCon != null) {
    					mcomCon.close("C2SBulkRcServiceImpl#processFile");
    					mcomCon = null;
    				}
    				
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
				try{
						if(errorMap == null)
							errorMap = new ErrorMap();
						upNowBL.invokeProcessByStatus(con,PretupsI.SCHEDULE_NOW_TYPE , scheduleMasterVO, errorMap );
						if("Schedule".equalsIgnoreCase(requestFor))
			    		{
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BULK_SUCCESS,new String[]{String.valueOf(scheduleMasterVO.getSuccessfulCount()), String.valueOf(totalRecords), scheduleMasterVO.getBatchID()});
							
							if(scheduleMasterVO.getSuccessfulCount()== totalRecords)
								response.setStatus(PretupsI.TXN_STATUS_SUCCESS);
							else if(scheduleMasterVO.getSuccessfulCount()>0)
								response.setStatus(PretupsErrorCodesI.PARTIAL_SUCCESS);
							else
								response.setStatus(PretupsI.RESPONSE_FAIL.toString());
							log.debug("Counts Checks : " , scheduleMasterVO.getSuccessfulCount() + " " + totalRecords);
							
							if(scheduleMasterVO.getSuccessfulCount()== totalRecords)
								responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
							else
								responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
							
							response.setScheduleBatchId(String.valueOf( scheduleMasterVO.getBatchID()));
							if(scheduleMasterVO.getSuccessfulCount() ==  totalRecords)
								msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BULK_SCH_SUCCESS,new String[]{String.valueOf( scheduleMasterVO.getBatchID())});
							response.setMessage(msg);
							response.setMessageCode(PretupsErrorCodesI.C2C_BULK_SUCCESS);
							response.setErrorMap(errorMap);
							writeFileForResponse(response, errorMap);
			    			//btslMessage = new BTSLMessages("restrictedsubs.scheduletopupdetails.msg.schedulenowsuccess"+"",new String[]{String.valueOf(scheduleMasterVO.getSuccessfulCount()), String.valueOf(scheduleMasterVO.getNoOfRecords()), scheduleMasterVO.getBatchID()},"showMsg");
			    		}		
					
				}
				catch(BTSLBaseException e)
				{	
					log.error(METHOD_NAME,"Exception:e="+e);
					log.errorTrace(METHOD_NAME,e);
					btslMessage = new BTSLMessages("restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile",new String[]{scheduleMasterVO.getBatchID()},"showMsg");
					if( !BTSLUtil.isNullorEmpty(e.getMessageKey() ))
					{
						String message = getErrorMessage(e.getMessageKey());
						response.setMessage(message);
						response.setMessageCode(e.getMessageKey());
					}
					response.setErrorMap(errorMap);
					response.setStatus("400");
					responseSwag.setStatus(400);
					response.setScheduleBatchId(String.valueOf( scheduleMasterVO.getBatchID()));
					writeFileForResponse(response, errorMap);
				
				}
			 }
    			 else
    			 {
    				 try
    				 {
    					 if("Schedule".equalsIgnoreCase(requestFor) && isErrorFound)
    					 {
							response.setMessageCode("");
							String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2S_BULK_SCHEDULE_SUCCESS, new String[]{(String) scheduleInfoMap.get("PROCESSED_RECS"), String.valueOf(records), scheduleMasterVO.getBatchID()});
							response.setStatus("400");
							responseSwag.setStatus(400);
							response.setScheduleBatchId(String.valueOf( scheduleMasterVO.getBatchID()));
							response.setMessage(msg);
							response.setMessageCode(PretupsErrorCodesI.C2S_BULK_SCHEDULE_SUCCESS);
							response.setErrorMap(errorMap);
							writeFileForResponse(response, errorMap);
			    			//btslMessage = new BTSLMessages("restrictedsubs.scheduletopupdetails.msg.schedulenowsuccess"+"",new String[]{String.valueOf(scheduleMasterVO.getSuccessfulCount()), String.valueOf(scheduleMasterVO.getNoOfRecords()), scheduleMasterVO.getBatchID()},"showMsg");
			    		}
    				 }
    				 catch(BTSLBaseException e)
    					{	
    						log.error(METHOD_NAME,"Exception:e="+e);
    						log.errorTrace(METHOD_NAME,e);
    						btslMessage = new BTSLMessages("restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile",new String[]{scheduleMasterVO.getBatchID()},"showMsg");
    					
    						if( !BTSLUtil.isNullorEmpty(e.getMessageKey() ))
    						{
    							String message = getErrorMessage(e.getMessageKey());
    							response.setMessage(message);
    							response.setMessageCode(e.getMessageKey());
    						}
    						response.setErrorMap(errorMap);
    						response.setStatus("400");
    						responseSwag.setStatus(400);
    						response.setScheduleBatchId(String.valueOf( scheduleMasterVO.getBatchID()));
    						writeFileForResponse(response, errorMap);
    					}
    			 }
    			 
			}
    		
	    } catch (BTSLBaseException be) {
	        log.error(METHOD_NAME, "Exception:e=" + be);
	        log.errorTrace(METHOD_NAME, be);
       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
	        response.setMessageCode(be.getMessageKey());
	        response.setMessage(msg);
        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
    	         response.setStatus("401");
            }
           else{
        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
           		response.setStatus("400");
           }
        	
           response.setErrorMap(errorMap);
           try {
              writeFileForResponse(response, errorMap);
             }catch (IOException io) {
            	 log.debug(METHOD_NAME, io);
	    	}
        }catch (Exception e) {
            log.debug(METHOD_NAME, e);
            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            String resmsg = RestAPIStringParser.getMessage(
    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
    				null);
            response.setMessage(resmsg);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        	response.setStatus("400");
        	
    	}finally {
			if (mcomCon != null) {
				mcomCon.close("RestrictedTopUpAction#processFile");
				mcomCon = null;
			}
                log.debug(METHOD_NAME, "Exit");
        }
		return response;
    
	}

	public int addBatch(Connection con, C2SBulkRechargeRequestVO requestVO,ChannelUserVO userVO,String servicekeyword,int record) throws ParseException, BTSLBaseException, SQLException {
		// /
        // Constructing the new BatchVO for the creation of the new
        // batch
        // at this time the batchID of the batch will be the
        // referenceBatchID of the batch
        // /
		scheduleMasterVO = new ScheduleBatchMasterVO();
        Date curDate = new Date();
        scheduleMasterVO.setStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
        scheduleMasterVO.setScheduledDate(BTSLUtil.getDateFromDateString(requestVO.getData().getScheduleDate()));
        scheduleMasterVO.setCreatedBy(userVO.getActiveUserID());//changed from active
        scheduleMasterVO.setCreatedOn(curDate);
        scheduleMasterVO.setModifiedBy(userVO.getActiveUserID());//changed from active
        scheduleMasterVO.setModifiedOn(curDate);
        scheduleMasterVO.setInitiatedBy(userVO.getUserID());
        scheduleMasterVO.setNetworkCode(userVO.getNetworkID());
        scheduleMasterVO.setParentCategory(userVO.getCategoryCode());
        scheduleMasterVO.setParentDomain(userVO.getCategoryVO().getDomainCodeforCategory());
        
        scheduleMasterVO.setParentID(userVO.getUserID());
        scheduleMasterVO.setOwnerID(userVO.getOwnerID());
        scheduleMasterVO.setServiceType(servicekeyword);
        scheduleMasterVO.setTotalCount(record);
        scheduleMasterVO.setActiveUserId(userVO.getActiveUserID());
        scheduleMasterVO.setFrequency(requestVO.getData().getOccurence());
        scheduleMasterVO.setIterations(Integer.parseInt(requestVO.getData().getNoOfDays()));
        scheduleMasterVO.setSuccessfulCount(0);
        if (PretupsI.BATCH_TYPE_NORMAL.equalsIgnoreCase(requestVO.getData().getBatchType())) {
            scheduleMasterVO.setBatchType(PretupsI.BATCH_TYPE_NORMAL);
        } else {
            scheduleMasterVO.setBatchType(PretupsI.BATCH_TYPE_CORPORATE);
        }

        // /
        // Generation of the new BatchID
        // /
        RestrictedSubscriberBL.generateScheduleBatchID(scheduleMasterVO);
        scheduleMasterVO.setRefBatchID(scheduleMasterVO.getBatchID());
        batchID = scheduleMasterVO.getBatchID();

        // creating the single line logger for the indication of the
        // starting of the processing
        // /
        //ScheduleFileProcessLog.log("Schedule File Processing START", theForm.getCreatedBy(), null, theForm.getBatchID(), "FILE = " + theForm.getFileNameStr() + "PROCESSING START", "START", "TYPE=" + theForm.getRequestFor());

        RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
        ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
        // /
        // Adding the new batch in the batch master table.
        // /
       int count = scheduledBatchDetailDAO.addScheduleBatchMaster(con, scheduleMasterVO);
        
        totalCount = scheduleMasterVO.getTotalCount();
        failCount = scheduleMasterVO.getUploadFailedCount();
        return count;
	}
	
	
	/**
     * Method isContain.
     * This method checks that passed list contains passed msisdn or not
     * 
     * @param p_finalList
     *            ArrayList
     * @param p_msisdn
     *            String
     * @return boolean
     */
    private boolean isContain(ArrayList p_finalList, String p_msisdn) {
        if (log.isDebugEnabled()) {
            log.debug("isContain", "Entered p_msisdn=" + p_msisdn + ", p_finalList=" + p_finalList);
        }
        boolean flag = false;
        if (p_finalList != null) {
            RestrictedSubscriberVO resVO;
            int size = p_finalList.size();
            String arr[] = null;
            for (int i = 0; i < size; i++) {
                resVO = (RestrictedSubscriberVO) p_finalList.get(i);
                arr = resVO.getMsisdn().split(",");
                if (arr[0].equals(p_msisdn)) {
                    flag = true;
                    break;
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("isContain", "Exit:flag=" + flag);
        }
        return flag;

    }

  
	private ArrayList getFileContentsList(LinkedHashMap<String, List<String>> bulkDataMap) {
		ArrayList<String> fileDataList = new ArrayList<String>();
		if(bulkDataMap != null && bulkDataMap.size()>0) {
			int size = bulkDataMap.size();
			int count = 0;
			String str;
			for (Entry<String, List<String>> entry : bulkDataMap.entrySet()) {
				count++;
				String key = entry.getKey();
				if(BTSLUtil.isNullString(key)) {
					break;
				}
			    ArrayList<String> list = (ArrayList<String>) entry.getValue();
			    
			    if(size == count ) {
			    for(int i=0;i<list.size();i++) {
			    	str = fileDataList.get(i)+list.get(i);
			    	fileDataList.remove(i);
			    	fileDataList.add(i,str );
			     } 
			   } else if(count == 1 ){
				   for(int i=0;i<list.size();i++) {
					   str =  list.get(i)+",";
				    	//fileDataList.remove(i);
				    	fileDataList.add(i,str );
				    } 
			   }else {
				   for(int i=0;i<list.size();i++) {
				    	str =  fileDataList.get(i)+list.get(i)+",";
				    	fileDataList.remove(i);
				    	fileDataList.add(i,str );
				    } 
			   }
			    
			}
		}
	
		return fileDataList;
	}
	
	
	 /**
     * Common Method used for set the login details for StaffUser
     * 
     * @param channelUserVO
     * @param parentChannelUserVO
     */

    protected void staffUserDetails(ChannelUserVO channelUserVO, ChannelUserVO parentChannelUserVO) {
        channelUserVO.setUserID(channelUserVO.getParentID());
        channelUserVO.setParentID(parentChannelUserVO.getParentID());
        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
        channelUserVO.setStatus(parentChannelUserVO.getStatus());
        channelUserVO.setUserType(parentChannelUserVO.getUserType());
        channelUserVO.setStaffUser(true);
        channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
        channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
        channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
        channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
    }
    
    
    private void writeFileForResponse(C2SBulkRechargeResponseVO response, ErrorMap errorMap)throws BTSLBaseException, IOException{
    	if(errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
    		return ;
    	List<List<String>> rows = new ArrayList<>();
		for(int i=0;i<errorMap.getRowErrorMsgLists().size();i++)
		{
			RowErrorMsgLists rowErrorMsgList = errorMap.getRowErrorMsgLists().get(i);
			for(int col= 0; col< rowErrorMsgList.getMasterErrorList().size(); col++)
			{
				MasterErrorList masterErrorList=rowErrorMsgList.getMasterErrorList().get(col);
			    rows.add(( Arrays.asList(rowErrorMsgList.getRowName(), rowErrorMsgList.getRowValue(), masterErrorList.getErrorMsg())));
			}
			
		}
		String filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
		C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
		c2CFileUploadApiControllerObject.validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        
		c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
		

		String filepathtemp = filePathConstemp ;   

		String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
		writeFileCSV(rows, filepathtemp + logErrorFilename + ".csv");
		File error =new File(filepathtemp+logErrorFilename+ ".csv");
		byte[] fileContent = FileUtils.readFileToByteArray(error);
   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
   		response.setFileAttachment(encodedString);
   		response.setFileName(logErrorFilename+".csv");
    }
	
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
    
    public void writeFileCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
		try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
    	csvWriter.append("Line number");
    	csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
    	csvWriter.append("Mobile number");
    	csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
    	csvWriter.append("Reason");
    	csvWriter.append("\n");

    	for (List<String> rowData : listBook) {
    	    csvWriter.append(String.join(Constants.getProperty("FILE_SEPARATOR_C2C"), rowData));
    	    csvWriter.append("\n");
    	}
    }
	}
    
    
    
    /**
     * Bulk DVD Service
     * Method: processRequestBulkDVD
     */
	@Override
	public DvdBulkResponse processRequestBulkDVD(C2SBulkRechargeRequestVO requestVO, String requestIDStr,MultiValueMap<String, String> headers, 
			HttpServletResponse responseSwag, HttpServletRequest httpServletRequest, String serviceKeyword) throws BTSLBaseException {

	    final String methodName = "processRequestBulkDVD";
	    
	    PretupsResponse<JsonNode> jsonResponse;
	    Set<String> msisdn2Set  = new HashSet<String>(); 
	    errorMap = new ErrorMap();
	    baseResponseList = new ArrayList<BaseResponse>();
	   // masterErrorList = new ArrayList<MasterErrorList>();
	    txnIdDetailsList = new ArrayList<TxnIDBaseResponse>();
	    this.responseSwag = responseSwag;
	    failureCount = 0;
	    //successCount = 0;
	    int rowCount = 0;
        C2SServiceImpl c2sServiceImpl = null;
        HashMap<String, String> responseBasicDetails= null;
        ArrayList<VomsVoucherVO> vomsVoucherList = null;
	        try {
	        	fileName = requestVO.getData().getFileName() + "." + requestVO.getData().getFileType();
	        	c2sServiceImpl = new C2SServiceImpl();
	        	baseResponseMultiple = new BaseResponseMultiple();
	        	dvdResponse = new DvdBulkResponse();
	        	dvdDetails = new DvdDetails();
	        	dvdRequestVO = new DvdRequestVO();
	        	dvdRequestVO.setData(dvdDetails);
	        	
	        	responseBasicDetails= new HashMap<String, String>();
	        	vomsVoucherList = new ArrayList<VomsVoucherVO>();
	        	OAuthenticationUtil.validateTokenApi(dvdRequestVO, headers, baseResponseMultiple);
	        	requestVO.getData().setPin(AESEncryptionUtil.aesDecryptor(requestVO.getData().getPin(), Constants.A_KEY));
	        	dvdResponse = validateSenderForDVD(dvdRequestVO.getData().getMsisdn(), requestVO.getData().getPin());
	        	if(!dvdResponse.getStatus().equals("200")) {
	        		return dvdResponse;
	        	}
	        	
	        	validateNetworkCode(requestVO.getData().getExtnwcode());
	        	
	        	scheduleNowContentsSize=Constants.getProperty("SCHEDULE_NOW_BATCH_RECHARGE_FILE_SIZE").trim();
	        	
	        // For schedule now
	        /*	if(requestVO.getData().getScheduleNow().equalsIgnoreCase("on")  && !isSameDay(new SimpleDateFormat("dd/MM/yy").parse(requestVO.getData().getScheduleDate()),new Date())) {
					   if (log.isDebugEnabled()) {
			                log.debug(methodName, "PLESE SPECIFY TODAYS DATE");
			            }
		                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2C_BULK_NOT_CURRNT_DATE);
				}
				if(BTSLUtil.isDateBeforeToday(new SimpleDateFormat("dd/MM/yy").parse(requestVO.getData().getScheduleDate()))) {
					   if (log.isDebugEnabled()) {
			                log.debug(methodName, "PLESE SPECIFY TODAYS DATE");
			            }
		                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2C_BULK_LESSTHAN_CURRNT_DATE);
				}*/
				
				//code for read file content
				ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
				HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
				fileDetailsMap.put(PretupsI.FILE_TYPE1, requestVO.getData().getFileType());
				fileDetailsMap.put(PretupsI.FILE_NAME,requestVO.getData().getFileName());
				fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getData().getFile());
				fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, serviceKeyword);
				bulkDataMap = fileUtil.uploadAndReadGenericFile(fileDetailsMap, 2, errorMap);
				
				//checking for RowErrorExist
				if(errorMap.getRowErrorMsgLists() == null) {
					errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
				}
				rowErrorMsgListsFinal = errorMap.getRowErrorMsgLists();
				
				if(bulkDataMap != null && bulkDataMap.size()>0 && bulkDataMap.size() == PretupsI.DVD_BULK_FILE_HEADER_SIZE) {
					bulkDataMap = getMapInFileFormat(bulkDataMap);
				}else {
					throw new BTSLBaseException(PretupsErrorCodesI.INVALID_FILE_HEADINGS);
				}
				records = bulkDataMap.size()-1;
				errorRecords = errorMap.getRowErrorMsgLists().size();
				totalRecords = records + errorRecords;
				//validating no of records
				if ((totalRecords) > Integer.parseInt(scheduleNowContentsSize))
				{
					if(log.isDebugEnabled())log.debug(methodName,"File contents size of the file is not valid in constant properties file : "+fileContents.size());
					throw new BTSLBaseException(this, methodName, "restrictedsubs.scheduletopupdetails.msg.noofrecordexeced",0,new String[]{scheduleNowContentsSize},"scheduleDetail");
				}

			   for (Entry<String, List<String>> entry : bulkDataMap.entrySet()) 
			   {
				   rowCount += 1;
					ArrayList<String> voucherDetailsList = (ArrayList<String>) entry.getValue();
//					int listSize = voucherDetailsList.size();

					if (rowCount == 1) {
						validateFileHeader(voucherDetailsList);
					} else {
						setContentInVO(voucherDetailsList, requestVO, rowCount-1);
						/*boolean isValidVoucherDetails = validateVoucherDetails(dvdRequestVO, rowCount-1);
						if(!isValidVoucherDetails) {
							failureCount +=1;
							continue;              //no need to process thes row
						}*/
						
						jsonResponse = c2sServiceImpl.processRequestChannel(dvdRequestVO, responseSwag, responseBasicDetails, httpServletRequest, vomsVoucherList);
						createResponse(jsonResponse, responseBasicDetails.get("transactionID"),
								dvdRequestVO.getData().getVoucherprofile(), rowCount-1);
						waitForNextDVD(msisdn2Set);
					}
			}
			
            int rowsProcessed = rowCount -1;
			createFinalResponse(rowsProcessed);
			response= new C2SBulkRechargeResponseVO();
			try {
				writeFileForResponseForDvd(dvdResponse, errorMap);
			}catch(BTSLBaseException be) {
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
						be.getArgs());
				dvdResponse.setMessage(resmsg);
			}catch(Exception e) {
				dvdResponse.setMessage(e.toString() + " : " + e.getMessage());
			}
			
	        return dvdResponse;
	        
	       } catch (BTSLBaseException be) {
				log.error(methodName, "Exception:e=" + be);
				log.errorTrace(methodName, be);
				
				if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage()) ){
					dvdResponse.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
	            	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	            }else if(Arrays.asList(PretupsI.DELTE_FILE_CODES).contains(be.getMessage()) ) //check for delting file in case of unexpected error.
	            {
	                fileDelete();
	            	dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
	                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	            }
				else{
	            	dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
	                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	             }
				
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
						be.getArgs());
				dvdResponse.setMessageCode(be.getMessage());
				dvdResponse.setMessage(resmsg);

			} catch (Exception e) {
				log.error(methodName, "Exception:e=" + e);
				log.errorTrace(methodName, e);
				
				dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				dvdResponse.setMessageCode(e.toString());
				dvdResponse.setMessage(e.toString() + " : " + e.getMessage());
			} finally {
				if (log.isDebugEnabled()) {
					log.debug(methodName, dvdResponse);
					log.debug(methodName, "Exiting ");
				}
			}
	       return dvdResponse;


	}
	
	
	/**
	 * 
	 * @param msisdn
	 * @param pin
	 * @return
	 */
	public DvdBulkResponse validateSenderForDVD(String msisdn, String pin) {
		final String methodName = "validatePinForDVD";
		
		Connection con = null;
		MComConnectionI mcomCon = null;

		ChannelUserVO senderVO;
		try {
			 mcomCon = new MComConnection();con=mcomCon.getConnection();
			senderVO = new ChannelUserDAO().loadChannelUserDetails(con, msisdn);
			if (senderVO.getUserPhoneVO().getPinRequired().equals(PretupsI.YES)) {
				try {
					if (!BTSLUtil.isNullString(pin)) {
						ChannelUserBL.validatePIN(con, senderVO, pin);
						dvdResponse.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
					} else {
						throw new BTSLBaseException(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
					}
				} catch (BTSLBaseException be) {
					log.error(methodName, "Exception " + be.getMessage());
					log.errorTrace(methodName, be);

					if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
							|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
						OracleUtil.commit(con);
					}
					dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
					dvdResponse.setMessage(PretupsRestUtil.getMessageString(be.getMessageKey(), null));
					dvdResponse.setMessageCode(be.getMessageKey());
					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

					return dvdResponse;
				}
			}
			 // Checking senders out transfer status, it should not be suspended
            if (PretupsI.YES.equalsIgnoreCase(senderVO.getOutSuspened())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND_DVD);
            }

            // Checking senders transfer profile status, it should not be suspended
            if (PretupsI.SUSPEND.equals(senderVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_DVD);
            }

            // Checking senders commission profile status, it should not be suspended
            if (PretupsI.SUSPEND.equals(senderVO.getCommissionProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_DVD);
            }
		} catch (BTSLBaseException be) 
		{
			log.error(methodName, "Exception " + be.getMessage());
			log.errorTrace(methodName, be);
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
						be.getArgs());
			dvdResponse.setMessage(resmsg);
			dvdResponse.setMessageCode(be.getMessageKey());
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		} catch (Exception e) 
		{
			log.error(methodName, "Exception " + e.getMessage());
			log.errorTrace(methodName, e);
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), 
					PretupsErrorCodesI.REQ_NOT_PROCESS, null);
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			dvdResponse.setMessage(resmsg);
			dvdResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}finally 
		{
			try {
				if (mcomCon != null) {
					mcomCon.close("FetchChannelUserDetailsController#" + "validatePinForDVD");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			LogFactory.printLog(methodName, " Exited ", log);
		}
		return dvdResponse;
	}
	
	/**
	 * 
	 * @param bulkDataMap
	 * @return
	 */
	public LinkedHashMap<String, List<String>> getMapInFileFormat(LinkedHashMap<String, List<String>> bulkDataMap){
		
		LinkedHashMap<String, List<String>> fileDetailsMap = new LinkedHashMap<String, List<String>>();
		List<String> fileHeader = new ArrayList(bulkDataMap.keySet());
		fileDetailsMap.put("0", fileHeader);
		List<List<String>> listGroup = bulkDataMap.values().stream().collect(Collectors.toList());
		
		for(int row = 0; row < listGroup.get(0).size(); row++) {
			ArrayList<String> rows = new ArrayList<String>();
			
			for(int col =0;  col< fileHeader.size(); col++) {
				rows.add(listGroup.get(col).get(row));
			}
			String key = String.valueOf(row + 1);
			fileDetailsMap.put(key, rows);
		}
		return fileDetailsMap;
	}
	
	private void validateFileHeader(ArrayList<String> voucherDetailsList) throws BTSLBaseException {
		ArrayList<String> headers = createValidHeaderList();
		if(voucherDetailsList.size() != PretupsI.DVD_BULK_FILE_HEADER_SIZE) {
			throw new BTSLBaseException(PretupsErrorCodesI.INVALID_FILE_HEADINGS);
		}
		if (!headers.equals(voucherDetailsList)) {
			throw new BTSLBaseException(PretupsErrorCodesI.INVALID_FILE_HEADINGS);
		}
	}
	
	private void setContentInVO(ArrayList<String> voucherDetailsList, C2SBulkRechargeRequestVO requestVO,
			int rowCount) throws BTSLBaseException {
		int colCount = 0;
		if(voucherDetailsList.size() == PretupsI.DVD_BULK_FILE_HEADER_SIZE) {
			String misidn2 = voucherDetailsList.get(colCount++);
			String voucherType = voucherDetailsList.get(colCount++);
			String voucherSegment = voucherDetailsList.get(colCount++);
			String voucherDenomiantion = voucherDetailsList.get(colCount++);
			String voucherProfile = voucherDetailsList.get(colCount++);
			String quantity = voucherDetailsList.get(colCount++);
			dvdDetails.setVoucherprofile(voucherProfile);
			dvdDetails.setVouchersegment(voucherSegment);
			dvdDetails.setVouchertype(voucherType);
			dvdDetails.setAmount(voucherDenomiantion);
			dvdDetails.setQuantity(quantity);

			dvdDetails.setDate("");
			dvdDetails.setExtnwcode(requestVO.getData().getExtnwcode()); // must
			// dvdDetails.setExtcode("");
			dvdDetails.setExtrefnum(dvdDetails.getExtcode());
			dvdDetails.setLanguage1("");
			dvdDetails.setLanguage2("");
			dvdDetails.setPin(requestVO.getData().getPin());
			dvdDetails.setMsisdn2(misidn2);
			dvdDetails.setSelector("");
			dvdDetails.setRowCount(rowCount);
			dvdRequestVO.setData(dvdDetails);
		}else {
			throw new BTSLBaseException(PretupsErrorCodesI.INVALID_FILE_HEADINGS);
		}
		
	}
	

	private ArrayList<String> createValidHeaderList(){
		ArrayList<String> headers = new ArrayList<String>();
		headers.addAll(Arrays.asList(PretupsI.DVD_FILE_HEADER));
		return headers;
		
	}
	
	/**
	 * 
	 * @param networkCode
	 * @throws BTSLBaseException
	 */
	private void validateNetworkCode(String networkCode) throws BTSLBaseException{
		final String methodName = "validateNetworkCode";
		if (!BTSLUtil.isNullString(networkCode)) {
			NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(networkCode);
			if (networkVO == null) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID,
						new String[] {networkCode} );
			}
		} else {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0, null,
					null);
		}
	}
	
	
	/**
	 * 
	 * @param dvdRequestVO
	 * @param rowCount
	 * @return
	 */
	private boolean validateVoucherDetails(DvdRequestVO dvdRequestVO, int rowCount) {
		boolean isVomsDetailsNotNull = true;
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		String message = null;
		String errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
		
		if(!BTSLUtil.isNullorEmpty(dvdRequestVO.getData().getMsisdn2())) {
			if(!BTSLUtil.isValidMSISDN(dvdRequestVO.getData().getMsisdn2())) {
				isVomsDetailsNotNull = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						errorCode, null);
				createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode);
			}
		}else {
			isVomsDetailsNotNull = false;
			message = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					errorCode, new String[] { "Subscriber's MSISIDN" });
			createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode);
		}
		
		if(BTSLUtil.isNullorEmpty(dvdRequestVO.getData().getVouchertype())){
			isVomsDetailsNotNull = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					errorCode, new String[] { "VOUCHERTYPE" });
			createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode );
		}
		
         if(BTSLUtil.isNullorEmpty(dvdRequestVO.getData().getVouchersegment())){
        	 isVomsDetailsNotNull = false;
        	 errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
        	 message = RestAPIStringParser.getMessage(
 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
 					errorCode, new String[] { "VOUCHERSEGMENT" });
 			createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode );
		}
         if(BTSLUtil.isNullorEmpty(dvdRequestVO.getData().getAmount())){
        	 isVomsDetailsNotNull = false;
        	 errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
        	 message = RestAPIStringParser.getMessage(
 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
 					errorCode, new String[] { "DENOMINATION" });
 			createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode );
 		}
         if(BTSLUtil.isNullorEmpty(dvdRequestVO.getData().getQuantity())){
        	 isVomsDetailsNotNull = false;
        	 errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
        	 message = RestAPIStringParser.getMessage(
 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
 					errorCode, new String[] { "QUANTITY" });
        	 
 			createFailureResponseForVoucherDetails(masterErrorLists, message, errorCode );
 		} 
         
		if(!isVomsDetailsNotNull) {
			int fileRowNumber = rowCount + 3;
			rowErrorMsgListsObj.setRowName("Line " + fileRowNumber);
			rowErrorMsgListsObj.setRowValue(dvdRequestVO.getData().getMsisdn2());
			rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
			rowErrorMsgListsFinal.add(rowErrorMsgListsObj);
		}
		
		return isVomsDetailsNotNull;
	}
	
	
	/**
	 * 
	 * @param masterErrorLists
	 * @param message
	 * @param errorCode
	 */
	private void createFailureResponseForVoucherDetails(ArrayList<MasterErrorList> masterErrorLists, String message, String errorCode) {
		MasterErrorList masterErrorListObj = new MasterErrorList();
		
		
		masterErrorListObj.setErrorCode(errorCode);
		masterErrorListObj.setErrorMsg(message);
		masterErrorLists.add(masterErrorListObj);
		
		
	}
	
	private void waitForNextDVD(Set<String> msisdn2Set){
		try {
			if(!msisdn2Set.add(dvdRequestVO.getData().getMsisdn2())) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			log.debug("waitForNextDVD", "Sleeping thread get interrupted.");
		}
	}
	
	
	/**
	 * 
	 * @param jsonResponse
	 * @param transactionID
	 * @param voucherProfileID
	 */
	private void createResponse(PretupsResponse<JsonNode>  jsonResponse, String transactionID, String voucherProfileID, int rowCount) {
		
	    if(jsonResponse!=null && jsonResponse.getDataObject()!=null && jsonResponse.getDataObject().get("txnstatus") != null){ 
	    	 String errorCode= null;
	    	 int status =  Integer.parseInt(jsonResponse.getDataObject().get("txnstatus").textValue());
	    	 String message = jsonResponse.getDataObject().get("message").textValue();
	    	 
	    	 if(!BTSLUtil.isNullorEmpty(message)  && !message.endsWith(".")) {
	    		 message = message + ".";
	    	 }
	    	 
	    	 if(transactionID != null) {
	    		 message  = message + " "  + "Transaction ID for this transaction is: " + transactionID;
	    	 }
	    	 
	    	 if(jsonResponse.getDataObject().get("errorcode") != null) {
	    		 errorCode = jsonResponse.getDataObject().get("errorcode").textValue();
	    	 }
	    	
	    	
	    	if(jsonResponse.getDataObject().get("txnstatus").textValue().equals("200")) {
	    		
	    		createSuccessResponse(status, message, errorCode, transactionID, voucherProfileID, rowCount);
	    	}else {
	    		createFailureResponse(message, errorCode, transactionID, voucherProfileID, rowCount);
	    	}
	    	
	    	if(jsonResponse.getDataObject().get("txnbatchid").textValue() != null && BTSLUtil.isNullorEmpty(txnBatchId)) {
	    		txnBatchId = jsonResponse.getDataObject().get("txnbatchid").textValue();
	    	}
	    
	     }
	}
	
	/**
	 * 
	 * @param status
	 * @param message
	 * @param errorCode
	 * @param transactionID
	 * @param voucherProfileID
	 */
	private void createSuccessResponse(int status, String message, String errorCode, String transactionID, String voucherProfileID, int rowCount) {
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(status);
    	baseResponse.setMessage(message);
    	baseResponse.setMessageCode(errorCode);
    	baseResponseList.add(baseResponse);
    	
    	addTransactionDetailsInResponse(rowCount,PretupsI.SUCCESS, transactionID, voucherProfileID);
    	
    	//successCount += 1;
		
	}
	
	/**
	 * 
	 * @param message
	 * @param errorCode
	 * @param transactionID
	 * @param voucherProfileID
	 */
	private void createFailureResponse(String message, String errorCode, String transactionID, String voucherProfileID, int rowCount) {
		MasterErrorList masterErrorListObj = new MasterErrorList();
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		
		failureCount += 1;
		masterErrorListObj.setErrorCode(errorCode);
		masterErrorListObj.setErrorMsg(message);
		masterErrorLists.add(masterErrorListObj);
		
		int fileRowNumber = rowCount + 3;
		rowErrorMsgListsObj.setRowName("Line " + fileRowNumber);
		rowErrorMsgListsObj.setRowValue(dvdRequestVO.getData().getMsisdn2());
		rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
		
		rowErrorMsgListsFinal.add(rowErrorMsgListsObj);
		
		addTransactionDetailsInResponse(rowCount, PretupsI.FAIL, transactionID, voucherProfileID);
	}
	
	
	/**
	 * 
	 * @param rowCount
	 * @param message
	 * @param transactionID
	 * @param voucherProfileID
	 */
	private void addTransactionDetailsInResponse(int rowCount,  String message, String transactionID, String voucherProfileID) {
		TxnIDBaseResponse txnIdDetailsObj = new TxnIDBaseResponse();
		txnIdDetailsObj.setRow(String.valueOf(rowCount));
    	txnIdDetailsObj.setMessage(message);
    	txnIdDetailsObj.setTransactionID(transactionID);
    	txnIdDetailsObj.setProfileID(voucherProfileID);
    	txnIdDetailsList.add(txnIdDetailsObj);
	}
	
	/**
	 * 
	 * @param rowCount
	 */
	private void createFinalResponse(int rowCount){
		String message = null;
		int successCount = rowCount - failureCount;
		if(rowCount == 0) {
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			message = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.NO_RECORD_FOUND_IN_FILE, null);
			dvdResponse.setMessage(message);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDelete();
		}else if(failureCount == rowCount) {
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			message = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.ALL_FAIL, null);
			dvdResponse.setMessage(message);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDelete();
		}else {
			dvdResponse.setStatus(String.valueOf(HttpStatus.SC_OK));
			message = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.PARTIAL_SUCCESS, new String[] {String.valueOf(successCount), String.valueOf(totalRecords), txnBatchId});
			
			dvdResponse.setMessage(message);
			responseSwag.setStatus(HttpStatus.SC_OK);
		}
		dvdResponse.setSuccessList(baseResponseList);
		errorMap.setRowErrorMsgLists(rowErrorMsgListsFinal);
		dvdResponse.setErrorMap(errorMap);
		dvdResponse.setTxnBatchId(txnBatchId);
		dvdResponse.setService(PretupsI.SERVICE_TYPE_DVD);
		dvdResponse.setTxnDetailsList(txnIdDetailsList);
	}
	
	/**
	 * 
	 * @param response
	 * @param errorMap
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	private void writeFileForResponseForDvd(DvdBulkResponse response, ErrorMap errorMap)throws BTSLBaseException, IOException{
    	if(errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
    		return ;
    	List<List<String>> rows = new ArrayList<>();
		for(int i=0;i<errorMap.getRowErrorMsgLists().size();i++)
		{
			RowErrorMsgLists rowErrorMsgList = errorMap.getRowErrorMsgLists().get(i);
			for(int col= 0; col< rowErrorMsgList.getMasterErrorList().size(); col++)
			{
				MasterErrorList masterErrorList=rowErrorMsgList.getMasterErrorList().get(col);
			    rows.add(( Arrays.asList(rowErrorMsgList.getRowName(), rowErrorMsgList.getRowValue(), masterErrorList.getErrorMsg())));
			}
		}
		String filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
		C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
		c2CFileUploadApiControllerObject.validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        
		c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
		

		String filepathtemp = filePathConstemp ;   

		String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
		writeFileCSVForDvd(rows, filepathtemp + logErrorFilename + ".csv");
		File error =new File(filepathtemp+logErrorFilename+ ".csv");
		byte[] fileContent = FileUtils.readFileToByteArray(error);
   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
   		response.setFileAttachment(encodedString);
   		response.setFileName(logErrorFilename+".csv");
    }
	
	public void writeFileCSVForDvd(List<List<String>> listBook, String excelFilePath) throws IOException {
		try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
			csvWriter.append("Line number");
			csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
			csvWriter.append("Subscriber's MSISDN");
			csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
			csvWriter.append("Reason");
			csvWriter.append("\n");

			for (List<String> rowData : listBook) {
				csvWriter.append(String.join(Constants.getProperty("FILE_SEPARATOR_C2C"), rowData));
				csvWriter.append("\n");
			}
		}
	}
	 
	 

	private void fileDelete() {
		String uploadedFilePathCons = Constants.getProperty("UploadBatchC2CUserListFilePath");
		
		String filePathConstemp = uploadedFilePathCons + "temp/" + fileName;       // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/
		if (!BTSLUtil.isNullString(filePathConstemp)) {
			File file = new File(filePathConstemp);
			if (file.delete()) {
				log.debug("filedelete", "******** Method uploadAndProcessFile :: Got exception and deleted the file");
			}
		}
	}
	
	/**
	 * 
	 * @param errorCode
	 * @return
	 */
	public String getErrorMessage(String errorCode) 
	{
		String message = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				errorCode, null);
		return message;
	}
	
	/**
	 * 
	 * @param errorCode
	 * @param args
	 * @return
	 */
	public String getErrorMessage(String errorCode, String[] args) 
	{
		String message = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				errorCode, args);
		return message;
	}

	@Override
	public DvdBulkResponse processCancelBatch(CancelBatchC2SRequestVO requestVO, String requestIDStr,
			MultiValueMap<String, String> headers, HttpServletResponse responseSwag,
			HttpServletRequest httpServletRequest, String serviceKeyword)  {
		String METHOD_NAME = "processCancelBatch";
		if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered requestVO=" + requestVO + ", serviceKeyword=" + serviceKeyword);
        }
		ViewScheduleForm thisForm = new ViewScheduleForm();
		Date fromScheduleDate = null;
        Date toScheduleDate = null;
        dvdResponse = new DvdBulkResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		try {
		mcomCon = new MComConnection();
		 con=mcomCon.getConnection();
		 if(BTSLUtil.isNullOrEmptyList(requestVO.getBatchIDS())){
	    	   String resmsg = RestAPIStringParser.getMessage(
      				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.EMPTY_BATCH_ID,
      				null);
	    	   dvdResponse.setMessage(resmsg);
	    	   dvdResponse.setMessageCode(PretupsErrorCodesI.EMPTY_BATCH_ID);
	    	   dvdResponse.setStatus("400");
	    	   dvdResponse.setService(serviceKeyword);
	           responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	           return dvdResponse;
	       }
		 oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
		    oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
		 
		 ChannelUserVO channelUserVO = new ChannelUserVO();
			channelUserVO = new ChannelUserDAO().loadChannelUserDetails(con,oAuthUser.getData().getMsisdn());
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			thisForm.setUserID(channelUserVO.getUserID());
            String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "'";
        ArrayList<ScheduleBatchMasterVO> listMaster = new ArrayList<ScheduleBatchMasterVO>();
        listMaster = scheduledBatchDetailDAO.loadScheduleBatchMasterList(con, thisForm.getUserID(), PretupsI.STATUS_IN, status, null, fromScheduleDate, toScheduleDate, null, channelUserVO.isStaffUser(), channelUserVO.getActiveUserID());
        thisForm.setViewScheduleList(listMaster);
        
        Map<String, ScheduleBatchMasterVO> batchIds = listMaster.parallelStream().
				collect(Collectors.toMap(ScheduleBatchMasterVO::getBatchID,b->b));
        ArrayList<ScheduleBatchMasterVO> listMaster1 = new ArrayList<ScheduleBatchMasterVO>();
        for(int i=0;i<requestVO.getBatchIDS().size();i++)
        {
        	if(batchIds.containsKey(requestVO.getBatchIDS().get(i)))
        			{
        		batchIds.get(requestVO.getBatchIDS().get(i)).setStatus(PretupsI.SCHEDULE_STATUS_CANCELED);
        		listMaster1.add(batchIds.get(requestVO.getBatchIDS().get(i)));
        			}
        	else
        	{
        		 String resmsg = RestAPIStringParser.getMessage(
           				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.EMPTY_BATCH_ID,
           				null);
     	    	   dvdResponse.setMessage(resmsg);
     	    	   dvdResponse.setMessageCode(PretupsErrorCodesI.BATCH_DETAIL_NO_NOT_FOUND);
     	    	   dvdResponse.setStatus("400");
     	    	   dvdResponse.setService(serviceKeyword);
     	           responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
     	           return dvdResponse;

        	}
        }
        thisForm.setViewScheduleList(listMaster1);
       
         int sizeOfScheduleList = thisForm.getSizeOfViewScheduleList();
        if (sizeOfScheduleList > 0) {
            ArrayList scheduleList = new ArrayList();
            ArrayList cancelScheduleList = new ArrayList();
            scheduleList = thisForm.getViewScheduleList();
            ScheduleBatchMasterVO scheduleMasterVO = null;
            Date currentDate = new Date();
            for (int i = 0; i < sizeOfScheduleList; i++) {
                scheduleMasterVO = (ScheduleBatchMasterVO) scheduleList.get(i);
                if (scheduleMasterVO.getStatus().equals(PretupsI.SCHEDULE_STATUS_CANCELED)) {
                    scheduleMasterVO.setModifiedOn(currentDate);
                    scheduleMasterVO.setModifiedBy(channelUserVO.getActiveUserID());
                    cancelScheduleList.add(scheduleMasterVO);
                }// end of
                 // if(scheduleMasterVO.getStatus().equals(PretupsI.SCHEDULE_STATUS_CANCELED))
            }// end of for(int i=0; i<sizeOfScheduleList; i++)
            thisForm.setListForCancel(cancelScheduleList);
        }
        

        ArrayList cancleList = thisForm.getListForCancel();
        ScheduleBatchMasterVO scheduleMasterVO = null;
        ScheduleBatchDetailVO scheduleDetailVO = new ScheduleBatchDetailVO();
        Date currentdate = new Date();
        String batchid[] = { "" };
        int cancelCount = 0;
        int j = thisForm.getSizeOfListForCancel();
        boolean modified = false;
        if (modified) {
            throw new BTSLBaseException(this, "updateScheduleBatchMaster", "error.modify.true");
        }
        int updateCountDetail = 0;
        int updateCountMaster = 0;
        for (int i = 0; i < j; i++) {
            cancelCount = 0;
            scheduleMasterVO = (ScheduleBatchMasterVO) cancleList.get(i);
            batchid[0] = batchid[0] + scheduleMasterVO.getBatchID() + ", ";
            scheduleDetailVO.setBatchID(scheduleMasterVO.getBatchID());
            scheduleDetailVO.setModifiedBy(channelUserVO.getActiveUserID());
            scheduleDetailVO.setModifiedOn(currentdate);
            scheduleDetailVO.setStatus(PretupsI.SCHEDULE_STATUS_CANCELED);
            // check modify_on updated or not.
            modified = scheduledBatchDetailDAO.isScheduleBatchMasterModified(con, scheduleMasterVO.getLastModifiedTime(), scheduleMasterVO.getBatchID());
            if (modified) {
                throw new BTSLBaseException(this, "updateScheduleBatchMaster", "error.modify.true");
            }
            // cancel the batch details on the basis of batch id in to
            // (SCHEDULED_BATCH_DETAIL) table
            cancelCount = scheduledBatchDetailDAO.updateScheduleStatus(con, scheduleDetailVO);
            updateCountDetail = updateCountDetail + cancelCount;
            // update the canceledcount for the SCHEDULED_BATCH_MASTER
            scheduleMasterVO.setCancelledCount(cancelCount + scheduleMasterVO.getCancelledCount());
            // update the SCHEDULED_BATCH_MASTER table
            updateCountMaster = updateCountMaster + scheduledBatchDetailDAO.updateScheduleBatchMasterStatus(con, scheduleMasterVO);
        }// end of for(int i=0; i<j; i++)
        batchid[0] = batchid[0].substring(0, (batchid[0].length() - 2)); // batch
                                                                         // id
                                                                         // listing
                                                                         // for
                                                                         // message
        if (updateCountMaster > 0)// give the successs message
        {
            
            mcomCon.finalCommit();
            // this code change for logs entry
            if (log.isDebugEnabled()) {
                log.debug("cancelConfirmSchedule", "Schedule has been cancelled successfully");
            }
            
            String msg=RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.BATCH_CANCELLED_SUCCESSFULLY,null);
            dvdResponse.setMessage(msg);
            dvdResponse.setStatus("200");
            dvdResponse.setService(serviceKeyword);
        }// end of if(updateCountMaster >0 )
        else// give the error message
        {
          mcomCon.finalRollback();
            // this code change for logs entry
            if (log.isDebugEnabled()) {
            	log.debug("cancelConfirmSchedule", " Schedule is not being cancelled now this time");
            }
            
            dvdResponse.setMessage(" Schedule can't be cancelled "+batchid[0]);
            dvdResponse.setStatus("400");
            dvdResponse.setService(serviceKeyword);

        }// end of else
    return dvdResponse;
		}
		catch (BTSLBaseException be) {
	        log.error(METHOD_NAME, "Exception:e=" + be);
	        log.errorTrace(METHOD_NAME, be);
       	    String msg=RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),null);
	        response.setMessageCode(be.getMessageKey());
	        response.setMessage(msg);
        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
    	         response.setStatus("401");
            }
           else{
        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
           		response.setStatus("400");
           }
        	
           response.setErrorMap(errorMap);
        }catch (Exception e) {
            log.debug(METHOD_NAME, e);
            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            String resmsg = RestAPIStringParser.getMessage(
    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
    				null);
            response.setMessage(resmsg);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        	response.setStatus("400");
        	
    	}finally {
			if (mcomCon != null) {
				mcomCon.close("C2SBulkRcServiceImpl#processCancelBatch");
				mcomCon = null;
			}
                log.debug(METHOD_NAME, "Exit");
        }
		return dvdResponse;
	}

	@Override
	public CancelSingleMsisdnBatchResponseVO processSingleMsisdnCancelBatch(
			CancelSingleMsisdnBatchC2SRequestVO requestVO, String requestIDStr,
			MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag,
			HttpServletRequest httpServletRequest, String serviceKeyword)
			throws Exception {
		
		
		if (log.isDebugEnabled()) {
            log.debug("processSingleMsisdnCancelBatch", "Entered requestVO=" + requestVO + ", serviceKeyword=" + serviceKeyword);
        }
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		OAuthUserData oAuthdata = null;
		OAuthUser oAuthreqVo = null;
		ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
		CancelSingleMsisdnBatchResponseVO responseVO=null;
		 ErrorMap errorMap=new ErrorMap();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			oAuthdata = new OAuthUserData();
			oAuthreqVo = new OAuthUser();
			oAuthreqVo.setData(oAuthdata);
        	responseVO = new CancelSingleMsisdnBatchResponseVO();
			OAuthenticationUtil.validateTokenApi(oAuthreqVo, headers,responseSwag);
			ChannelUserVO channelUserVO = new ChannelUserVO();
			channelUserVO = new ChannelUserDAO().loadChannelUserDetails(con,oAuthreqVo.getData().getMsisdn());
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			batchID= requestVO.getBatchId();
			
			
			 //validate request
		       if(BTSLUtil.isNullString(batchID)){
		    	   String resmsg = RestAPIStringParser.getMessage(
	        				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.EMPTY_BATCH_ID,
	        				null);
		    	   responseVO.setMessage(resmsg);
		    	   responseVO.setMessageCode(PretupsErrorCodesI.EMPTY_BATCH_ID);
		           responseVO.setStatus("400");
		           responseVO.setService(serviceKeyword);
		           responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		           return responseVO;
		       }else if(requestVO.getMsisdnList().size()==0){
		    	   String resmsg = RestAPIStringParser.getMessage(
	       				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.EXTSYS_BLANK,
	       				new String[]{"Msisdn List"});
		    	   responseVO.setMessage(resmsg);
		    	   responseVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
		           responseVO.setStatus("400");
		           responseVO.setService(serviceKeyword);
	       	       responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	       	       return responseVO;
		       }
		       
			//fetch batch master view
			String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "'";
	        ScheduleBatchMasterVO scheduleBatchMasterVO=new ScheduledBatchDetailDAO().loadScheduleBatchMasterDetails(con, channelUserVO.getUserID(), PretupsI.STATUS_IN, status, null, null, null, null, channelUserVO.isStaffUser(), channelUserVO.getActiveUserID(), requestVO.getBatchId());
	        
	        if(scheduleBatchMasterVO == null){
                 String resmsg = RestAPIStringParser.getMessage(
	        				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.INVALID_BATCH_OR_ACCESS,
	        				null);
		    	   responseVO.setMessage(resmsg);
		    	   responseVO.setMessageCode(PretupsErrorCodesI.INVALID_BATCH_OR_ACCESS);
		           responseVO.setStatus("400");
		           responseVO.setService(serviceKeyword);
		           responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		           return responseVO;
		           
	        }
	        //fetch batch detail view
	        status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','" + PretupsI.SCHEDULE_STATUS_CANCELED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "','" + PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "'";
	        ArrayList<ScheduleBatchDetailVO> batchdetailVOList = new RestrictedSubscriberDAO().loadBatchDetailVOList(con, requestVO.getBatchId(), PretupsI.STATUS_IN, status);
	      
	        //create map with msisdn and ScheduleBatchDetailVO
	        Map<String, ScheduleBatchDetailVO> msisdnMap = batchdetailVOList.parallelStream().collect(Collectors.toMap(ScheduleBatchDetailVO::getMsisdn, b->b));
	        ArrayList<ScheduleBatchDetailVO> deleteList = new ArrayList<>();
	        ArrayList<String> msisdnList = requestVO.getMsisdnList();
	       
	       //create a VO list for deleted msisdn
	        List<MasterErrorList> masterErrorLists =new ArrayList<>();
		    for(int i=0;i<msisdnList.size();i++){
		    	String msisdn = msisdnList.get(i);
		       	if(msisdnMap.containsKey(msisdn))
		   		{  
		       		if(PretupsI.SCHEDULE_STATUS_CANCELED.equals(msisdnMap.get(msisdn).getScheduleStatus())){
		       			MasterErrorList masterError =  new MasterErrorList();
			   			masterError.setErrorCode(PretupsErrorCodesI.SCHEDULED_MSISDN_ALREADY_CANCELLED);
		                 String resmsg = RestAPIStringParser.getMessage(
		         				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.SCHEDULED_MSISDN_ALREADY_CANCELLED,
		         				new String[]{msisdn});
		                 masterError.setErrorMsg(resmsg);
		                 masterErrorLists.add(masterError);
		       		}else{
			       		msisdnMap.get(msisdnList.get(i)).setStatus(PretupsI.SCHEDULE_STATUS_CANCELED);
			       		deleteList.add(msisdnMap.get(msisdn));
		       		}
		   		}else{
		   			MasterErrorList masterError =  new MasterErrorList();
		   			masterError.setErrorCode(PretupsErrorCodesI.INVALID_MSISDN_BATCH);
	                 String resmsg = RestAPIStringParser.getMessage(
	         				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.INVALID_MSISDN_BATCH,
	         				new String[]{msisdn});
	                 masterError.setErrorMsg(resmsg);
	                 masterErrorLists.add(masterError);
			   		}
		       	if(masterErrorLists.size()>0){
		       		errorMap=new ErrorMap();
			        errorMap.setMasterErrorList(masterErrorLists);
			        responseVO.setErrorMap(errorMap);
		       		responseVO.setStatus("400");
			        responseVO.setService(serviceKeyword);	
			        String resmsg = RestAPIStringParser.getMessage(
		       				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.BATCH_CANCEL_FAIL,
		       				null);
		            responseVO.setMessage(resmsg);
		            responseVO.setMessageCode(PretupsErrorCodesI.BATCH_CANCEL_FAIL);
	        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			        return responseVO;
		       	}
		   }
	       
	       
	       // Update the status in details table also update the master table
	       int updateCount = scheduledBatchDetailDAO.updateSchedule(con, deleteList, channelUserVO.getUserID(), new Date(),scheduleBatchMasterVO.getCancelledCount(), scheduleBatchMasterVO.getNoOfRecords());
	       // if updateCount<=0 then gives error.
	       if (updateCount <= 0) {
	        	mcomCon.finalRollback();
	            if (log.isDebugEnabled()) {
	                log.debug("processSingleMsisdnCancelBatch", " Schedule is not being cancelled now this time");
	            }
	            String resmsg = RestAPIStringParser.getMessage(
	       				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.BATCH_CANCEL_FAIL,
	       				null);
	            responseVO.setMessage(resmsg);
	            responseVO.setMessageCode(PretupsErrorCodesI.BATCH_CANCEL_FAIL);
	            responseVO.setStatus("400");
	            responseVO.setService(serviceKeyword);
        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }else{
	        	 mcomCon.finalCommit();	
	             if (log.isDebugEnabled()) {
	                 log.debug("processSingleMsisdnCancelBatch", " Schedule has been cancelled successfully");
	             }
	             String resmsg = RestAPIStringParser.getMessage(
		       				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.BATCH_CANCEL_SUCCESS,
		       				new String[]{String.valueOf(msisdnList.size())});
		         responseVO.setMessage(resmsg);
		         responseVO.setMessageCode(PretupsErrorCodesI.BATCH_CANCEL_SUCCESS);
	             responseVO.setStatus("200");
	             responseVO.setCanceledRecords(msisdnList.size());
	        	 responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
	        	}
	          return responseVO;
		}
		catch (BTSLBaseException be) {
	        log.error("processSingleMsisdnCancelBatch", "Exception:e=" + be);
	        log.errorTrace("processSingleMsisdnCancelBatch", be);
       	    String msg=RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),null);
	        response.setMessageCode(be.getMessageKey());
	        response.setMessage(msg);
        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
    	         response.setStatus("401");
            }
           else{
        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
           		response.setStatus("400");
           }
           response.setErrorMap(errorMap);
        }catch (Exception e) {
            log.debug("processSingleMsisdnCancelBatch", e);
            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            String resmsg = RestAPIStringParser.getMessage(
    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
    				null);
            response.setMessage(resmsg);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        	response.setStatus("400");
        	
    	}finally {
			if (mcomCon != null) {
				mcomCon.close("C2SBulkRcServiceImpl#processSingleMsisdnCancelBatch");
				mcomCon = null;
			}
			if(con != null){
				con.close();
				con=null;
			}
                log.debug("processSingleMsisdnCancelBatch", "Exit");
            responseVO.setService(serviceKeyword);
            responseVO.setScheduleBatchId(batchID);
        }
		return responseVO;
	}
	@Override
	public ViewC2SBulkRechargeDetailsResponseVO processViewRequest(String batchId,
			MultiValueMap<String, String> headers, HttpServletResponse responseSwag,String msisdn) throws BTSLBaseException {

        final String METHOD_NAME = "processViewRequest";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
		Connection con = null;
		File file = null;
		String fileName = "";
		MComConnectionI mcomCon = null;
        ViewC2SBulkRechargeDetailsResponseVO response = new ViewC2SBulkRechargeDetailsResponseVO();
        Writer fileWriter = null;
    	Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
    	ErrorMap errorMap = new ErrorMap();
        try{
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            
            OAuthUser oAuthUserData=new OAuthUser();
            oAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,new BaseResponseMultiple());
 	       
            String loginId =  oAuthUserData.getData().getLoginid();
            
            ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
            String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','" + PretupsI.SCHEDULE_STATUS_CANCELED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "','" + PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "'";
            // load the list of batch details
            // find batch type and load details accordingly
            ScheduleBatchMasterVO scheduleBatchMasterVO = null;
            
            ChannelUserVO channelUserVO=new ChannelUserVO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con,msisdn);
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			  if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
                  UserDAO userDao = new UserDAO();
                  UserPhoneVO phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
                  if (phoneVO != null) {
                   channelUserVO.setActiveUserMsisdn(phoneVO.getMsisdn());
                   channelUserVO.setActiveUserPin(phoneVO.getSmsPin());
                  }
                  // Set Staff User Details
                  ChannelUserVO staffUserVO = new ChannelUserVO();
                  UserPhoneVO staffphoneVO = new UserPhoneVO();
                  BeanUtils.copyProperties(staffUserVO, channelUserVO);
                  if (phoneVO != null) {
                      BeanUtils.copyProperties(staffphoneVO, phoneVO);
                      staffUserVO.setUserPhoneVO(staffphoneVO);
                  }
                  staffUserVO.setPinReset(channelUserVO.getPinReset());
                  channelUserVO.setStaffUserDetails(staffUserVO);
                  ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());
                  staffUserDetails(channelUserVO, parentChannelUserVO);

                  channelUserVO.setPrefixId(parentChannelUserVO.getPrefixId());
              }
			  ArrayList<ScheduleBatchMasterVO> list = null;
			  if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
				  list = scheduledBatchDetailDAO.loadScheduleBatchMasterDetails(con, channelUserVO.getUserID(), true, channelUserVO.getActiveUserID(), batchId);
			  } else {
				  list = scheduledBatchDetailDAO.loadScheduleBatchMasterDetails(con, channelUserVO.getUserID(), false,null, batchId);
			  }
            boolean isRestricted = false;
            scheduleBatchMasterVO = (ScheduleBatchMasterVO) list.get(0);
            String filePath = Constants.getProperty("DownloadRestrictedMSISDNFilePath");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("loadDownloadFileForReschedule", "Exception" + e.getMessage());
                throw new BTSLBaseException(this, "loadDownloadFileForReschedule", "downloadfile.error.dirnotcreated", "error");
            }
             fileName = Constants.getProperty("DownloadRestrictedMSISDNFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".csv";
             file = new File(filePath + "" + fileName);
            try {
                fileWriter = new BufferedWriter(new FileWriter(file));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("loadDownloadFileForReschedule", "Exception" + e.getMessage());

            }
           
                ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceTypeObject(scheduleBatchMasterVO.getServiceType(), PretupsI.C2S_MODULE);
                BatchFileParserI batchFileParserI = (BatchFileParserI) Class.forName(serviceKeywordCacheVO.getFileParser()).newInstance();
                ArrayList fileHeaderList = this.getKeyValues(new String[] { batchFileParserI.getHeaderKey(scheduleBatchMasterVO.getBatchType()) }, scheduleBatchMasterVO.getServiceType());
                ArrayList columnHeaderList = this.getKeyValues(batchFileParserI.getColumnKeys(scheduleBatchMasterVO.getBatchType()), scheduleBatchMasterVO.getServiceType());
                HashMap scheduleInfoMap = new HashMap();
                scheduleInfoMap.put(BatchFileParserI.HEADER_KEY, fileHeaderList);
                scheduleInfoMap.put(BatchFileParserI.COLUMN_HEADER_KEY, columnHeaderList);
                scheduleInfoMap.put(BatchFileParserI.OWNER_ID, scheduleBatchMasterVO.getOwnerID());
                scheduleInfoMap.put(BatchFileParserI.USER_ID, channelUserVO.getUserID());
                scheduleInfoMap.put(BatchFileParserI.SERVICE_TYPE, scheduleBatchMasterVO.getServiceType());
                scheduleInfoMap.put(BatchFileParserI.BATCH_ID, scheduleBatchMasterVO.getBatchID());


                batchFileParserI.downloadFileForResheduleRest(con, fileWriter,scheduleBatchMasterVO.getBatchType(), scheduleInfoMap);
                if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(scheduleBatchMasterVO.getBatchType())) {
                 //   theForm.setDownLoadDataMap((HashMap) scheduleInfoMap.get(BatchFileParserI.DATA_MAP));
                }
        		byte[] fileContent = FileUtils.readFileToByteArray(file);
           		String encodedString = Base64.getEncoder().encodeToString(fileContent);
           		response.setFile(encodedString);
           		response.setFileName(fileName);
                filePath = BTSLUtil.encrypt3DesAesText(filePath);
            if(PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(scheduleBatchMasterVO.getBatchType())) {
                  isRestricted = true;
             }
            LinkedHashMap<String,ScheduleBatchDetailVO> resultRows = null;
            if (isRestricted) {
            	resultRows  = restrictedSubscriberDAO.loadScheduleBatchDetailsList(con,batchId, PretupsI.STATUS_IN, status);
            } else {
            	resultRows = restrictedSubscriberDAO.loadScheduleBatchDetailsMap(con, batchId, PretupsI.STATUS_IN, status);
            }
            ArrayList<ScheduleBatchDetailVO> msisdnList = new ArrayList<>(resultRows.values());
            UserDAO user = new UserDAO();
          //  channelUserVO
           // scheduleBatchMasterVO.setParentCategory(channelUserVO.getCategoryName());
           // scheduleBatchMasterVO.setParentDomain(channelUserVO.getDomainName());
            scheduleBatchMasterVO.setParentCategory(user.getCategoryNameFromCatCode(con,scheduleBatchMasterVO.getParentCategory(),""));
            scheduleBatchMasterVO.setParentDomain(user.getDomainName(con, scheduleBatchMasterVO.getParentDomain()));
            scheduleBatchMasterVO.setUserGeo(channelUserVO.getGeographicalDesc());
            response.setMsisdnList(msisdnList);
            response.setScheduleBatchMasterVO(scheduleBatchMasterVO);
            response.setStatus(200);
            response.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TXN_STATUS_SUCCESS,null));

	    } catch (BTSLBaseException be) {
	        log.error("processViewRequest", "Exceptin:e=" + be);
	        log.errorTrace(METHOD_NAME, be);
       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
	        response.setMessageCode(be.getMessageKey());
	        response.setMessage(msg);
        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
    	         response.setStatus(401);
            }
           else{
        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
           		response.setStatus(400);
           }
        	
           response.setErrorMap(errorMap);
        }catch (Exception e) {
            log.debug("processViewRequest", e);
            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            String resmsg = RestAPIStringParser.getMessage(
    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
    				null);
            response.setMessage(resmsg);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        	response.setStatus(400);
        	
    	}finally {
			if (mcomCon != null) {
				mcomCon.close("C2SBulkRcServiceImpl#processViewRequest");
				mcomCon = null;
			}
                log.debug("processFile", "Exit");
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                        byte[] fileContent = FileUtils.readFileToByteArray(file);
                   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
                   		response.setFile(encodedString);
                   		response.setFileName(fileName);
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                
                
                
        }
        
		return response;
	}

	 private ArrayList getKeyValues(String[] keyArr, String serviceType) {
	        ArrayList arrayList = new ArrayList(10);
	        String message = "";
	        for (int i = 0, j = keyArr.length; i < j; i++) {
	        	message = BTSLUtil.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),keyArr[i], null);
	            if("restrictedsubs.scheduletopupdetails.file.label.subservice".equals(keyArr[i])){
	            	message = message + getSubService(serviceType);
	            }
	            arrayList.add(message);
	            
	        }
	        return arrayList;
	    }
	
	   
	    private String getSubService(String serviceType){
	    	if (_log.isDebugEnabled()) {
	            _log.debug("getSubService", "Entered:" );
	        }
	        final ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
	        String subService = new String();
	        subService = "( ";
	        Connection con = null;MComConnectionI mcomCon = null;
	        try {
				mcomCon = new MComConnection();
			  con=mcomCon.getConnection();
	        ServiceSelectorMappingVO serviceSelectorMappingVO = new ServiceSelectorMappingVO();
	        ArrayList selectorList = new ArrayList<>();
			selectorList = serviceSelectorMappingDAO.loadServiceSelectorMappingDetails(con, serviceType);
			int selectorLists=selectorList.size();
	        for (int i = 0; i <selectorLists ; i++) {
	            serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
	            final ListValueVO listVO = new ListValueVO(serviceSelectorMappingVO.getSelectorName(), serviceSelectorMappingVO.getSelectorCode());
	            subService = subService + listVO.getLabel() + "=" + listVO.getValue()+"|";
	        }
	        subService = subService.substring(0,subService.length() - 1)+  ")";
	        } catch (BTSLBaseException | SQLException e) {
				e.printStackTrace();
			}finally{
				if (mcomCon != null) {
					mcomCon.close("RestrictedTopUpAction#confirm");
					mcomCon = null;
				}
	            if (_log.isDebugEnabled()) {
	                _log.debug("getSubService", "Exiting:" );
	            }
				
			}
			return subService;
	    }
	@Override
	public CategoryDomainCodeVO getDomainCode(CategoryVO requestVO, Connection con, String domainCode,
			HttpServletResponse response1) throws BTSLBaseException {
		CategoryDomainCodeVO response = new CategoryDomainCodeVO();
		List<ListValueVO> domainList = new ArrayList<>();
		ListValueVO listVO = new ListValueVO(null, domainCode);
		domainList.add(listVO);
		CategoryDAO categoryDao = new CategoryDAO();

		try {

			List<CategoryVO> loadAllCategoryOfDomains = categoryDao.loadAllCategoryOfDomains(con, domainList);

			response.setDomainList(loadAllCategoryOfDomains);

			if (loadAllCategoryOfDomains.size() >= 1) {
				response.setStatus(Integer.toString(PretupsI.RESPONSE_SUCCESS));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.CATEGORY_LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.CATEGORY_LIST_FOUND);

			} else {
				throw new BTSLBaseException(C2SBulkRcServiceImpl.class.getName(), PretupsErrorCodesI.DOMAIN_NOT_EXIST);
			}

		} catch (BTSLBaseException be) {
			response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.DOMAIN_NOT_EXIST, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DOMAIN_NOT_EXIST);

		} catch (Exception e) {
			response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.DOMAIN_NOT_EXIST, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DOMAIN_NOT_EXIST);
		}

		return response;
	}
	
	public RescheduleBatchRechargeResponseVO processRescheduleFile(RescheduleBatchRechargeRequestVO rescheduleBatchRechargeRequestVO,OAuthUser oAuthUserData,HttpServletRequest request,HttpServletResponse responseSwag) {
		 final String METHOD_NAME = "processRescheduleFile";
		 if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, "Entered");
	        }
			Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			ErrorFileResponse errorFileResponseVO = new ErrorFileResponse();
			ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
		 RescheduleBatchRechargeResponseVO response =null;
		 RestrictedTopUpForm theForm = null;
			Connection con = null;
			MComConnectionI mcomCon = null;
	        ScheduleBatchMasterVO scheduleMasterVO = null;
	        String status =null;
	        UserVO userVO = null;
	        UserDAO userDAO= null;
	        ScheduledBatchDetailDAO scheduledBatchDetailDAO =null;
	        try {
	        	mcomCon = new MComConnection();
	        	errorMap = new ErrorMap();
	  	        con=mcomCon.getConnection();
	  	        scheduleMasterVO = new ScheduleBatchMasterVO();
	  	        scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
	    		 try {
	 	            //Schedule Now Recharge
	 				scheduleNowContentsSize=Constants.getProperty("SCHEDULE_NOW_BATCH_RECHARGE_FILE_SIZE").trim();
	 				
	 	        } catch (Exception e) {
	 	            if (log.isDebugEnabled()) {
	 	                log.debug(METHOD_NAME, "RESTRICTED_MSISDN_LIST_SIZE not defined in Constant Property file");
	 	            }
	 	            log.errorTrace(METHOD_NAME, e);
	 	            throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.contentsizemissing");
	 	        }
	        	response=new RescheduleBatchRechargeResponseVO();
	        	theForm =new RestrictedTopUpForm();
	        	userVO = new UserVO();
	        	userDAO = new UserDAO();
	             String fileSize = null;
	             fileSize = Constants.getProperty("RESTRICTED_MSISDN_FILE_SIZE");
	             if (BTSLUtil.isNullString(fileSize)) {
	                    fileSize = Constants.getProperty("OTHER_FILE_SIZE");
	               }
	           
	          	ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
	       		HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
	       		fileDetailsMap.put(PretupsI.FILE_TYPE1, rescheduleBatchRechargeRequestVO.getFileType());
	       		fileDetailsMap.put(PretupsI.FILE_NAME,rescheduleBatchRechargeRequestVO.getFileName());
	       		fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, rescheduleBatchRechargeRequestVO.getFileAttachment());
	       		fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, rescheduleBatchRechargeRequestVO.getServiceCode());
	       		bulkDataMap = fileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);
	       			
	             
	             
	                String loginId=  oAuthUserData.getData().getLoginid();
	  	        	 userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
		  	         
	                Date curDate = new Date();
	                scheduleMasterVO.setCreatedBy(userVO.getActiveUserID());
	                scheduleMasterVO.setCreatedOn(curDate);
	                scheduleMasterVO.setModifiedBy(userVO.getActiveUserID());
	                scheduleMasterVO.setModifiedOn(curDate);
	                int numberOfDays = 0;
	                try {
	                    numberOfDays = Integer.parseInt(Constants.getProperty("RESCHEDULE_BATCH_BACK_DAYS"));
	                } catch (Exception e) {
	                    _log.errorTrace(METHOD_NAME, e);
	                    _log.error("loadScheduleList", "RESCHEDULE_BATCH_BACK_DAYS is not defined in constants.props file e=" + e.getMessage());
	                    numberOfDays = 0;
	                }
					
	               
	                status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "'";
	                ArrayList listMaster = new ScheduledBatchDetailDAO().loadScheduleBatchMasterList(con, userVO.getUserID(), PretupsI.STATUS_IN, status, null, null, null, null, userVO.isStaffUser(), userVO.getActiveUserID());
	                
	                for (int i = 0, j = listMaster.size(); i < j; i++) {
	                    scheduleMasterVO = (ScheduleBatchMasterVO) listMaster.get(i);
	                    if (scheduleMasterVO.getBatchID().equals(rescheduleBatchRechargeRequestVO.getBatchId())) {
	                        response.setPreviousDate(scheduleMasterVO.getScheduledDateStr());
	                    }
	                }
	                // /
	                // set the information in the selected batchVO for the canceling
	                // the batch
	                // /
	                
	                scheduleMasterVO = new ScheduleBatchMasterVO();
	                scheduleMasterVO.setBatchID(rescheduleBatchRechargeRequestVO.getBatchId());
	                scheduleMasterVO.setModifiedBy(userVO.getActiveUserID());
	                scheduleMasterVO.setModifiedOn(curDate);
	                scheduleMasterVO.setStatus(PretupsI.SCHEDULE_STATUS_CANCELED);
	                scheduleMasterVO.setActiveUserId(userVO.getActiveUserID());
	                if (PretupsI.BATCH_TYPE_NORMAL.equalsIgnoreCase(rescheduleBatchRechargeRequestVO.getBatchType())) {
	                    scheduleMasterVO.setBatchType(PretupsI.BATCH_TYPE_NORMAL);
	                } else {
	                    scheduleMasterVO.setBatchType(PretupsI.BATCH_TYPE_CORPORATE);
	                }
	                // /
	                // Constructing the new BatchVO for the creation of the new
	                // batch on the canceling of the old batch
	                // here the batchID of the new batch will be the
	                // referenceBatchID of the old batch and the batchID of the
	                // old batch will be the referenceBatchID of the new batch.
	                // /
	                ScheduleBatchMasterVO scheduleMasterVONew = new ScheduleBatchMasterVO();
	                scheduleMasterVONew.setStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
	                scheduleMasterVONew.setScheduledDate(BTSLUtil.getDateFromDateString(rescheduleBatchRechargeRequestVO.getScheduleDate()));
	                scheduleMasterVONew.setCreatedBy(userVO.getActiveUserID());
	                scheduleMasterVONew.setCreatedOn(curDate);
	                scheduleMasterVONew.setModifiedBy(userVO.getActiveUserID());
	                scheduleMasterVONew.setModifiedOn(curDate);
	                scheduleMasterVONew.setInitiatedBy(userVO.getUserID());
	                scheduleMasterVONew.setNetworkCode(userVO.getNetworkID());
	                scheduleMasterVONew.setParentCategory(rescheduleBatchRechargeRequestVO.getCategoryCode());
	                scheduleMasterVONew.setParentDomain(userVO.getDomainID());
	                scheduleMasterVONew.setParentID(userVO.getUserID());
	                scheduleMasterVONew.setOwnerID(userVO.getOwnerID());
	                scheduleMasterVONew.setServiceType(rescheduleBatchRechargeRequestVO.getServiceCode());
	             //   scheduleMasterVONew.setTotalCount(Long.parseLong(rescheduleBatchRechargeRequestVO.getNoOfRecords()));
	                scheduleMasterVONew.setActiveUserId(scheduleMasterVO.getActiveUserId());
	                // /
	                // Generation of the new BatchID
	                // /
	                
	                RestrictedSubscriberBL.generateScheduleBatchID(scheduleMasterVONew);
	                // /
	                // seting the batchID and the ReferenceBatchID
	                // /
	                scheduleMasterVO.setRefBatchID(scheduleMasterVONew.getBatchID());
	                scheduleMasterVONew.setIterations(Integer.parseInt(rescheduleBatchRechargeRequestVO.getIteration()));
	                scheduleMasterVONew.setFrequency(rescheduleBatchRechargeRequestVO.getFrequency());
	                scheduleMasterVONew.setRefBatchID(scheduleMasterVO.getBatchID());
	                scheduleMasterVONew.setBatchType(scheduleMasterVO.getBatchType());

	                // /
	                // Construction of the new detail vo for the caneling of the
	                // deatils of the old batch for this
	                // we are setting the batchID of the old batch
	                // /

	                ScheduleBatchDetailVO scheduleDetailVO = new ScheduleBatchDetailVO();

	                scheduleDetailVO.setModifiedBy(userVO.getActiveUserID());
	                scheduleDetailVO.setModifiedOn(curDate);
	                scheduleDetailVO.setStatus(PretupsI.SCHEDULE_STATUS_CANCELED);
	                scheduleDetailVO.setBatchID(rescheduleBatchRechargeRequestVO.getBatchId());
	                // /
	                // creating the single line logger for the indication of the
	                // starting of the processing
	                // /
	                ScheduleFileProcessLog.log("Re-schedule File Processing START", theForm.getCreatedBy(), null, scheduleMasterVONew.getBatchID(), "FILE = " + theForm.getFileNameStr() + "PROCESSING START", "START", "TYPE=" + "Reschedule");
	                // getting the conneciton for the processing
	               
					 scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
	                // /
	                // Canceling the details of the selected batch form the details
	                // table
	                // /

	                int count = scheduledBatchDetailDAO.updateScheduleStatus(con, scheduleDetailVO);
	                if (count <= 0) {
	                    log.error("confirmReschedule", "Schedule Details can not be updated");
	                    response.setMessage("Schedule Details can not be updated");

	                    mcomCon.finalRollback();
	                    throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.rescheduletopupdetails.msg.unsuccess", "scheduleTopUpAuthorise");
	                }
	                else {
	                	response.setCancelledRecords(count);
	                }
	                // /
	                // Canceling the Batch master information form the master table
	                // here we have to update the cancel count of the batch
	                // /
	                scheduleMasterVO.setCancelledCount(scheduleMasterVO.getCancelledCount() + count);
	                count = scheduledBatchDetailDAO.updateScheduleBatchMasterStatus(con, scheduleMasterVO);
	                
	                if (count <= 0) {
	                    log.error("confirmReschedule", "Schedule Master can not be updated");
	                    mcomCon.finalRollback();
	                    response.setMessage("Schedule Master can not be updated as no record found");
	                    throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.rescheduletopupdetails.msg.unsuccess", "scheduleTopUpAuthorise");
	                }
	               
	                // /
	                // Adding the informaion of the new Batch
	                // /
	                count = scheduledBatchDetailDAO.addScheduleBatchMaster(con, scheduleMasterVONew);
	                if (count <= 0) {
	                    log.error("confirmReschedule", "Schedule Master can not be added");
	                    response.setMessage("Schedule Master can not be added");

	                    mcomCon.finalRollback();
	                    throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.unsuccess", "scheduleTopUpAuthorise");
	                }
	                else {
	                	response.setBatchId( scheduleMasterVONew.getBatchID());
	                	mcomCon.partialCommit();
	                }
	                // Connection will be commited in the processFile method
	              
	                processFile(scheduleMasterVONew, request, con,userVO,rescheduleBatchRechargeRequestVO,response,bulkDataMap,errorMap);
	                totalCount = scheduleMasterVONew.getTotalCount();
	                failCount = scheduleMasterVONew.getUploadFailedCount();
					
					response.setRescheduleDate(rescheduleBatchRechargeRequestVO.getScheduleDate());
					response.setErrorMap(errorMap);
					errorFileRequestVO.setFiletype(rescheduleBatchRechargeRequestVO.getFileType());
					errorFileRequestVO.setFile(rescheduleBatchRechargeRequestVO.getFileAttachment());
					Boolean flag=false;
					if((response.getFailedRecords() != response.getNoOfRecords()) && response.getFailedRecords()>0) {
						flag = true;
						response.setStatus(HttpStatus.SC_BAD_REQUEST);
					}
					errorFileRequestVO.setPartialFailure(flag);
					errorFileRequestVO.setRowErrorMsgLists(errorMap.getRowErrorMsgLists());
					errorFileRequestVO.setAdditionalProperty(PretupsI.SERVICE_KEYWORD, PretupsI.PROCESSRESCHDL);
					errorFileRequestVO.setAdditionalProperty("row",1);	
					downloadUserListService.downloadErrorFile(errorFileRequestVO, errorFileResponseVO, responseSwag);
					response.setErrorFileAttachment(errorFileResponseVO.getFileAttachment());
					response.setErrorFileName(errorFileResponseVO.getFileName());
	        } catch (BTSLBaseException be) {
	            _log.error(METHOD_NAME, "Exceptin:e=" + be);
	            log.errorTrace(METHOD_NAME, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
	            response.setMessageCode(be.getMessageKey());
	            response.setMessage(msg);
	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	            }
	           else{
	        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
	           }
	        	
	           response.setErrorMap(errorMap);
	         
	        }
	        catch(Exception e) {
	            log.error(METHOD_NAME, "Exception:e=" + e);
	            log.errorTrace(METHOD_NAME, e);
	            response.setStatus(PretupsI.RESPONSE_FAIL);
	            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	    		response.setMessageCode("error.general.processing");
	    	//	response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
	        }
	        finally {
	        	try {
					if (mcomCon != null) {
						mcomCon.close("C2SBulkRcServiceImpl#"+METHOD_NAME);
						mcomCon = null;
					}
				} catch (Exception e) {
					log.errorTrace(METHOD_NAME, e);
				}
	
				try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
					log.errorTrace(METHOD_NAME, e);
				}
	        }

		
		
		return response;
	}
   
/**
* 
* @param form
* @param request
* @param p_con
* @throws Exception
*/
	private void processFile(ScheduleBatchMasterVO scheduleMasterVONew, HttpServletRequest request, Connection p_con,UserVO userVO,RescheduleBatchRechargeRequestVO requestVO,RescheduleBatchRechargeResponseVO response, LinkedHashMap<String, List<String>> bulkDataMap,ErrorMap errorMap) throws Exception {
       final String METHOD_NAME = "processFile";
       if (log.isDebugEnabled()) {
           log.debug(METHOD_NAME, "Entered");
       }
		
		MComConnectionI mcomCon = null;
       RestrictedTopUpForm theForm = null;

       // variables for the reading the contents of the file
      
       String filePathAndFileName = null;
       String arr[] = null;
      
       String contentsSize = null;
       String batchContentsSize = null;
       String filePath =null;
   	//Schedule Now Recharge
		
		int records = 0;
       // end here
		Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		String processedRecords = "";
       ArrayList<String> fileContents = null; // contains all the data form the file
       RestrictedSubscriberVO errorVO = null;
       ArrayList<RestrictedSubscriberVO> finalList = new ArrayList<RestrictedSubscriberVO>();
       boolean isErrorFound = false;
       try {
           contentsSize = Constants.getProperty("RESTRICTED_MSISDN_LIST_SIZE");
           batchContentsSize = Constants.getProperty("BATCH_MSISDN_LIST_SIZE");
			//Schedule Now Recharge
			scheduleNowContentsSize=Constants.getProperty("SCHEDULE_NOW_BATCH_RECHARGE_FILE_SIZE").trim();
			
       } catch (Exception e) {
           if (log.isDebugEnabled()) {
               log.debug(METHOD_NAME, "RESTRICTED_MSISDN_LIST_SIZE not defined in Constant Property file");
           }
           log.errorTrace(METHOD_NAME, e);
           throw new BTSLBaseException(this,METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.contentsizemissing");
       }
       try {
           filePath = Constants.getProperty("UploadBatchC2CUserListFilePath");
       } catch (Exception e) {
           if (_log.isDebugEnabled()) {
               _log.debug("processFile", "File path not defined in Constant Property file");
           }
           _log.errorTrace(METHOD_NAME, e);
           throw new BTSLBaseException(this, "processFile", "restrictedsubs.scheduletopupdetails.msg.filepathmissing");
       }
       fileName = requestVO.getFileName();
       filePathAndFileName = filePath + fileName+"."+requestVO.getFileType().toLowerCase();

       try {
          
          
       		
           mcomCon = new MComConnection();
       	p_con=mcomCon.getConnection();
     
    
   		
   		if (errorMap.getRowErrorMsgLists() == null) {
   			errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
   		}
   		
   		fileContents =   getFileContentsList(bulkDataMap);
   		records = fileContents.size();
   		errorRecords = errorMap.getRowErrorMsgLists().size();
   		totalRecords = records + errorRecords;
   		response.setNoOfRecords(totalRecords);
   		response.setFailedRecords(errorRecords);
   		// /
           // If file does not contain record as entered by the user then
           // show the error message
           // (records excludes blank lines)
           // /
   		
           if (records  == 0) {
               if (log.isDebugEnabled()) {
                   log.debug(METHOD_NAME, "Number of records are zero : ");
               }
               throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2C_BULK_No_RECORDS);
           }
					
	              
					
						if(PretupsI.BATCH_TYPE_CORPORATE.equals(requestVO.getFileType()))
						{
							if((totalRecords) > Integer.parseInt(contentsSize))
							{    
								if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"File contents size of the file is not valid in constant properties file : "+fileContents.size());
								throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.noofrecordexeced",0,new String[]{contentsSize},"scheduleDetail");
							}
						}
						else
						{
							if((totalRecords) > Integer.parseInt(batchContentsSize))
							{    
							if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"File contents size of the file is not valid in constant properties file : "+fileContents.size());
							throw new BTSLBaseException(this, METHOD_NAME, "restrictedsubs.scheduletopupdetails.msg.noofrecordexeced",0,new String[]{batchContentsSize},"scheduleDetail");
							}
					    
						}
					
				
       
          
    		
           // /
           // Check for the duplicate mobile numbers in the list
           // here we add the information in the new list called finalList it
           // contains informaion about all
           // the data.
           // /
   		String dataStr = null;

           String duplicateMSISDN = "restrictedsubs.scheduletopupdetails.errorfile.msg.duplicatemsisdn";
           if (fileContents != null && records > 0 && requestVO.getServiceCode().equals("DVD") == false) {
             
               for (int i = 0; i < records; i++) {
                   dataStr = (String) fileContents.get(i);
                   arr = dataStr.split(",");
                   errorVO = new RestrictedSubscriberVO();
                   if (!("NEWLINE".equals(dataStr)) && !isContain(finalList, arr[0])) {
                       errorVO.setLineNumber((i + 1) + "");
                       errorVO.setMsisdn(dataStr);
                       finalList.add(errorVO);
                   } else if (!("NEWLINE".equals(dataStr))) {
                       ScheduleFileProcessLog.log("Processing File", scheduleMasterVONew.getCreatedBy(), arr[0], scheduleMasterVONew.getBatchID(), "Mobile number is Duplicate", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + "Reschedule");
                       errorVO.setLineNumber((i + 1) + "");
                       errorVO.setMsisdn(arr[0] + "(D)");
                       errorVO.setErrorCode(duplicateMSISDN);
                       errorVO.setisErrorFound(true);
                       errorVO.setAmount(Long.parseLong(arr[3]));
                       finalList.add(errorVO);
                   } else if ("NEWLINE".equals(dataStr)) {
                       errorVO.setLineNumber((i + 1) + "");
                       errorVO.setErrorCode("NEWLINE");
                       errorVO.setMsisdn("NEWLINE");
                       finalList.add(errorVO);
                   }
               }
           }
           else if(fileContents != null && records > 0 && requestVO.getServiceCode().equals("DVD") == true)
           {
               for (int i = 0; i < records; i++) {
                   dataStr = (String) fileContents.get(i);
                   arr = dataStr.split(",");
                   errorVO = new RestrictedSubscriberVO();
                   if ("NEWLINE".equals(dataStr)== false && isContainDenominationAndMsisdn(finalList, arr) == false) {
                       errorVO.setLineNumber((i + 1) + "");
                       errorVO.setMsisdn(dataStr);
                       finalList.add(errorVO);
                   } else if (!("NEWLINE".equals(dataStr))) {
                       ScheduleFileProcessLog.log("Processing File", scheduleMasterVONew.getCreatedBy(), arr[0], scheduleMasterVONew.getBatchID(), "Mobile number is Duplicate", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + theForm.getRequestFor());
                       errorVO.setLineNumber((i + 1) + "");
                       errorVO.setErrorCode(duplicateMSISDN);
                       errorVO.setMsisdn(arr[0] + "(D)");
                       errorVO.setvoucherProfile(arr[4]);
                       errorVO.setAmount(Long.parseLong(arr[3]));
                       errorVO.setisErrorFound(true);
		                errorVO.setvoucherType(arr[1]);
		                errorVO.setvoucherSegment(arr[2]);
						errorVO.setVoucherQuantity(arr[5]);
                       isErrorFound = true;
                       finalList.add(errorVO);
                   } else if ("NEWLINE".equals(dataStr)) {
                       errorVO.setLineNumber((i + 1) + "");
                       errorVO.setErrorCode("NEWLINE");
                       errorVO.setMsisdn("NEWLINE");
                       finalList.add(errorVO);
                   }
               }
           
           }
          
           ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceTypeObject(requestVO.getServiceCode(), PretupsI.C2S_MODULE);
           BatchFileParserI batchFileParserI = (BatchFileParserI) Class.forName(serviceKeywordCacheVO.getFileParser()).newInstance();
           String[] uploadErrorList = batchFileParserI.getErrorKeys(requestVO.getFileType());
           HashMap<Object,Object> scheduleInfoMap = new HashMap<Object, Object>();
           if (PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equalsIgnoreCase(requestVO.getServiceCode())
           		|| PretupsI.SERVICE_TYPE_VAS_RECHARGE.equalsIgnoreCase(requestVO.getServiceCode())
           		|| PretupsI.SERVICE_TYPE_GIFT_RECHARGE.equalsIgnoreCase(requestVO.getServiceCode())
           		|| PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN.equalsIgnoreCase(requestVO.getServiceCode()) 
           		|| PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR.equalsIgnoreCase(requestVO.getServiceCode())
           		|| PretupsI.SERVICE_TYPE_POSTPAID_BILL_PAYMENT.equalsIgnoreCase(requestVO.getServiceCode())
           		|| PretupsI.SERVICE_TYPE_CHNL_DATA_RECHARGE.equalsIgnoreCase(requestVO.getServiceCode())
           		|| PretupsI.SERVICE_TYPE_DVD.equalsIgnoreCase(requestVO.getServiceCode())) {
               scheduleInfoMap.put(BatchFileParserI.ERROR_KEY, uploadErrorList);
        //     scheduleInfoMap.put(BatchFileParserI.DATA_MAP, theForm.getDownLoadDataMap());
               scheduleInfoMap.put(BatchFileParserI.USER_ID, userVO.getUserID());
               scheduleInfoMap.put(BatchFileParserI.OWNER_ID, userVO.getOwnerID());
               scheduleInfoMap.put(BatchFileParserI.BATCH_ID, requestVO.getBatchId());
               scheduleInfoMap.put("CREATED_BY", scheduleMasterVONew.getCreatedBy());
               scheduleInfoMap.put("FINAL_LIST", finalList);
               scheduleInfoMap.put("FINAL_LIST_SIZE",records);
               scheduleInfoMap.put("FILE_NAME", requestVO.getFileName());
//             scheduleInfoMap.put("DOWNLOAD_BATCH_ID", theForm.getDownLoadBatchID());
               scheduleInfoMap.put("USER_VO",userVO);
               scheduleInfoMap.put("SCHEDULED_VO", scheduleMasterVONew);
               scheduleInfoMap.put("MODIFIED_ON", scheduleMasterVONew.getModifiedOn());
               scheduleInfoMap.put("CREATED_ON", scheduleMasterVONew.getCreatedOn());
               scheduleInfoMap.put("RECORDS",fileContents.size());
           }
           BTSLMessages btslMessage = null;
           try {
               batchFileParserI.uploadFile(p_con, requestVO.getFileType(), scheduleInfoMap, isErrorFound);
           } catch (BTSLBaseException e) {
               _log.error(METHOD_NAME, "Exception:e=" + e);
               _log.errorTrace(METHOD_NAME, e);
           }
           isErrorFound = ((Boolean) scheduleInfoMap.get("IS_ERROR_FOUND")).booleanValue();
          
           if (!isErrorFound) {

           	  response.setStatus(HttpStatus.SC_OK);
           	  response.setNoOfRecords(records);
           	  response.setFileName(requestVO.getFileName());
           	  String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BULK_RESCH_SUCCESS,new String[]{scheduleMasterVONew.getRefBatchID(),scheduleMasterVONew.getBatchID()});
           	  response.setMessage(msg);  
           	 
			  response.setMessageCode(PretupsErrorCodesI.C2C_BULK_RESCH_SUCCESS);
					
				
				processedRecords = (String) scheduleInfoMap.get("PROCESSED_RECS");
               
               
           } else {
               ArrayList<?> errorList = (ArrayList<?>) scheduleInfoMap.get("FINAL_LIST");
               
               if (errorList != null) {
               	int errorListSize = errorList.size();
                   for (int i = 0, j = errorListSize; i < j; i++) {
                       errorVO = (RestrictedSubscriberVO) errorList.get(i);
       	            if(!BTSLUtil.isNullString(errorVO.getErrorCode()))
                       {
       	            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
       	            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
       	            	MasterErrorList masterErrorList = new MasterErrorList();
       					masterErrorList.setErrorCode(errorVO.getErrorCode());
       					String msg = RestAPIStringParser.getMessage(locale, errorVO.getErrorCode(), errorVO.getErrorCodeArgs());
       					masterErrorList.setErrorMsg(msg);
       					masterErrorLists.add(masterErrorList);
       					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
       					rowErrorMsgLists.setRowValue(errorVO.getMsisdn());
       					rowErrorMsgLists.setRowName("Line" + String.valueOf(Long.parseLong(errorVO.getLineNumber()) + 2));
       					if(errorMap.getRowErrorMsgLists() == null)
       						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
       					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                       }
                   }
               }
               
              
               processedRecords = (String) scheduleInfoMap.get("PROCESSED_RECS") ;
               
               if (!BTSLUtil.isNullString((String) scheduleInfoMap.get("IS_FILE_TYPE_DIFF"))) {
                   btslMessage = new BTSLMessages((String) scheduleInfoMap.get("IS_FILE_TYPE_DIFF"), "firstPage");
               } else if (("0").equals(processedRecords)) {
               	MasterErrorList masterErrorList = new MasterErrorList();
       			masterErrorList.setErrorCode("restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile");
       			String msg = RestAPIStringParser.getMessage(locale, "restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile", null);
       			masterErrorList.setErrorMsg(msg);
       			errorMap.setMasterErrorList(new ArrayList<MasterErrorList> ());
       			errorMap.getMasterErrorList().add(masterErrorList);
            
               } 
			    
			}
           
           
         
		
       } catch (BTSLBaseException be) {
           _log.error(METHOD_NAME, "Exception:e=" + be);
           log.errorTrace(METHOD_NAME, be);
      	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
           response.setMessageCode(be.getMessageKey());
           response.setMessage(msg);
       	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
       		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
   	         response.setStatus(HttpStatus.SC_UNAUTHORIZED);
           }
          else{
       	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
          		response.setStatus(HttpStatus.SC_BAD_REQUEST);
          }
       	
          response.setErrorMap(errorMap);
        
       } catch (Exception e) {
       	log.debug(METHOD_NAME, e);
           response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
           String resmsg = RestAPIStringParser.getMessage(
   				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
   				null);
           response.setMessage(resmsg);
           responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
       	response.setStatus(400);
       } finally {
			if (mcomCon != null) {
				mcomCon.close("C2SBulkRcServiceImpl#"+METHOD_NAME);
				mcomCon = null;
			}
         
           
         
       }
       
       
         
   }

	
	private boolean isContainDenominationAndMsisdn(ArrayList p_finalList, String[] arr2) {
		if (_log.isDebugEnabled()) {
			_log.debug("isContainDenominationAndMsisdn", "Entered p_denomination=" + arr2[3]
					+ ", p_finalList=" + p_finalList);
		}
		boolean flag = false;
		if (p_finalList != null) {
			RestrictedSubscriberVO resVO;
			int size = p_finalList.size();
			String arr[] = null;
			for (int i = 0; i < size; i++) {
				resVO = (RestrictedSubscriberVO) p_finalList.get(i);
				arr = resVO.getMsisdn().split(",");
				if (arr.length >3) {
					if (arr2[3].equals(arr[3]) && arr2[0].equals(arr[0])) {
						flag = true;
						break;
					}

				}
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("isContain", "Exit:flag=" + flag);
		}
		return flag;

	}
	
	@Override
	public ViewScheduleDetailsBatchResponseVO processViewBatchScheduleDetails(Connection con, String sessionUserLoginId,
			HttpServletResponse responseSwag, String loginId, String scheduleStatus, String serviceType,
			String dateRange) throws BTSLBaseException {




		final String METHOD_NAME = "getBatches";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}

		ViewScheduleDetailsBatchResponseVO response = new ViewScheduleDetailsBatchResponseVO();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
		String status = null;
		Date fromScheduleDate = null;
		Date toScheduleDate = null;
		ChannelUserVO userVO = null;

		try {

			if (BTSLUtil.isNullString(loginId))
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PROPERTY_MISSING,
						new String[] { "Login Id" });
			if (BTSLUtil.isNullString(serviceType))
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PROPERTY_MISSING,
						new String[] { "Service type" });
			if (BTSLUtil.isNullString(scheduleStatus))
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PROPERTY_MISSING,
						new String[] { "Schedule status" });

			UserVO sessionUser = new UserDAO().loadUsersDetailsByLoginID(con, sessionUserLoginId);

			Boolean isExist = new ChannelUserDAO().isUserInHierarchy(con, sessionUser.getUserID(), "LOGINID", loginId);

			if (!isExist)
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_SESSION_HIERARCHY);

			if (!scheduleStatus.equals(PretupsI.SCHEDULE_STATUS_SCHEDULED)
					&& !scheduleStatus.equals(PretupsI.SCHEDULE_STATUS_CANCELED)
					&& !scheduleStatus.equals(PretupsI.SCHEDULE_STATUS_EXECUTED)
					&& !scheduleStatus.equals(PretupsI.ALL))
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PROPERTY_INVALID,
						new String[] { "Schedule status" });

			if (scheduleStatus.equals(PretupsI.ALL)) {
				status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','" + PretupsI.SCHEDULE_STATUS_CANCELED + "','"
						+ PretupsI.SCHEDULE_STATUS_EXECUTED + "'";
			} else {
				status = "'" + scheduleStatus + "'";
			}

			if (!BTSLUtil.isNullString(dateRange)) {
				try {
					fromScheduleDate = BTSLUtil.getDateFromDateString(dateRange.split("-")[0].trim());
					toScheduleDate = BTSLUtil.getDateFromDateString(dateRange.split("-")[1].trim());
				} catch (Exception e) {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PROPERTY_INVALID,
							new String[] { "Date range" });
				}

			} else {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PROPERTY_MISSING,
						new String[] { "Date range" });
			}

			if (!BTSLUtil.isNullString(loginId)) {
				userVO = new UserDAO().loadAllUserDetailsByLoginID(con, loginId);
				if (userVO == null)
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LOGIN_ID_DOES_NOT_EXISTS, "");
			} else {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PROPERTY_MISSING,
						new String[] { "Login Id" });
			}

			ArrayList<ScheduleBatchMasterVO> listMaster = new ArrayList<>();
			if (serviceType.equals(PretupsI.ALL))
				listMaster = scheduledBatchDetailDAO.loadScheduleBatchMasterList(con, userVO.getUserID(),
						PretupsI.STATUS_IN, status, null, fromScheduleDate, toScheduleDate, null, userVO.isStaffUser(),
						userVO.getActiveUserID());
			else
				listMaster = scheduledBatchDetailDAO.loadScheduleBatchMasterList(con, userVO.getUserID(),
						PretupsI.STATUS_IN, status, null, fromScheduleDate, toScheduleDate, serviceType,
						userVO.isStaffUser(), userVO.getActiveUserID());
			response.setScheduleDetailList(listMaster);
			response.setStatus(Integer.toString(PretupsI.RESPONSE_SUCCESS));

		} catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(Integer.toString(PretupsI.UNAUTHORIZED_ACCESS));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(Integer.toString(PretupsI.RESPONSE_FAIL));
			}
		} catch (Exception e) {
			log.debug(METHOD_NAME, e);
			response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.REQ_NOT_PROCESS, null);
			response.setMessage(resmsg);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(Integer.toString(PretupsI.RESPONSE_FAIL));

		}

		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Exited");
		}

		return response;
	}
	
	@Override
	public ViewScheduleDetailsListResponseVO processViewScheduleDetails(Connection con ,UserVO sessionUserVO , String loginId ,String msisdn) throws BTSLBaseException, Exception{
		final String methodName = "processViewScheduleDetails";
		
		 if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
		 
		 if(BTSLUtil.isNullString(loginId)) throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_MISSING, new String[] {"Login Id"});
		 if(BTSLUtil.isNullString(msisdn)) throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_MISSING, new String[] {"MSISDN"});
		 
		 Boolean isExist = new ChannelUserDAO().isUserInHierarchy(con, sessionUserVO.getUserID(), "LOGINID", loginId);
		 if(!isExist) throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_IN_SESSION_HIERARCHY);
		 
		 UserDAO userDao = new UserDAO();
		 UserVO searchUserVO = null;
		 
		 ArrayList<ScheduleBatchDetailVO> list = null;
		 ViewScheduleDetailsListResponseVO viewScheduleDetailsListResponseVO = new ViewScheduleDetailsListResponseVO();
		 try {
			 searchUserVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
			 String filteredMsisdn = null;
			 UserVO userVO = sessionUserVO;
			 try {
	                // Change ID=ACCOUNTID
	                // FilteredMSISDN is replaced by getFilteredIdentificationNumber
	                // This is done because this field can contains msisdn or
	                // account id
	                filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn);//changed
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	                final String arr[] = {msisdn};
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_MSISDN , arr);
	            }
			 
			 	// Change ID=ACCOUNTID
	            // isValidMsisdn is replaced by isValidIdentificationNumber
	            // This is done because this field can contains msisdn or account id
	            if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
	            	final String arr[] = {msisdn};
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_MSISDN , arr);
	            }
	            // check prefix of the MSISDN
	            String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
	                                                                             // the
	                                                                             // prefix
	                                                                             // of
	                                                                             // the
	                                                                             // MSISDN
	            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
	            if (networkPrefixVO == null) {
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_NETWORK_PREFIX);
	            }
	            // check network support of the MSISDN
	            String networkCode = networkPrefixVO.getNetworkCode();
	            if (!networkCode.equals(userVO.getNetworkID())) {
	            	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_NETWORK_SUPPORT_MSISDN);
	            	 }

	            // Now load the information of the entered MSISDN form the
	            // restrictedMsisdn table

	            ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
	            /*
	             * ArrayList
	             * restrictedList=restrictedSubscriberDAO.loadResSubsDetails
	             * (con,filteredMsisdn,thisForm.getUserID(),false,null,null);
	             * 
	             * if(restrictedList==null || restrictedList.isEmpty())
	             * {
	             * throw new BTSLBaseException(this,"showScheduleDetails",
	             * "restrictedsubs.viewsingletrfschedule.msg.nouserinfo",0,new
	             * String[]{thisForm.getUserName()},"userSelect");
	             * }
	             */
	            // RestrictedSubscriberVO restrictedSubscriberVO
	            // =(RestrictedSubscriberVO)restrictedList.get(0);
	            // thisForm.setRestrictedSubscriberVO(restrictedSubscriberVO);

	            // Now load the schedule information of the entered MSISDN
	            String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "','" + PretupsI.SCHEDULE_STATUS_CANCELED + "'";
	            list = scheduledBatchDetailDAO.loadScheduleDetailVOList(con, searchUserVO.getOwnerID(), searchUserVO.getUserID(), filteredMsisdn, PretupsI.STATUS_IN, status, userVO.isStaffUser(), userVO.getActiveUserID());
	            viewScheduleDetailsListResponseVO.setScheduleDetailList(list);

		 }catch (Exception e) {
	            log.error("showScheduleDetails", "Exception: " + e.getMessage());
	            log.errorTrace(methodName, e);
	            throw e;
	        } finally {
	            if (log.isDebugEnabled()) {
	                log.debug("showScheduleDetails", "Exit forward = ");
	            }
	        }
		 return viewScheduleDetailsListResponseVO;
	}
	
	@Override
	public ViewScheduleDetailsListResponseVO processScheduleReportRequest(Connection con ,UserVO sessionUserVO , String loginId ,String msisdn, String dateRange, String staffFlag) throws BTSLBaseException, Exception{
		final String methodName = "processViewScheduleDetails";
		
		 if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
		 
		 if(BTSLUtil.isNullString(loginId)) throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_MISSING, new String[] {"Login Id"});
//		 if(BTSLUtil.isNullString(msisdn)) throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_MISSING, new String[] {"MSISDN"});
		 
		 Boolean isExist = new ChannelUserDAO().isUserInHierarchy(con, sessionUserVO.getUserID(), "LOGINID", loginId);
		 if(!isExist) throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_IN_SESSION_HIERARCHY);
		 
		 UserDAO userDao = new UserDAO();
		 UserVO searchUserVO = null;
		 
		 ArrayList<ScheduleBatchDetailVO> list = null;
		 ViewScheduleDetailsListResponseVO viewScheduleDetailsListResponseVO = new ViewScheduleDetailsListResponseVO();
		 try {
			 searchUserVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
			 String filteredMsisdn = null;
			 UserVO userVO = sessionUserVO;
			 Date fromScheduleDate = null;
			 Date toScheduleDate = null;
			 try {
	                // Change ID=ACCOUNTID
	                // FilteredMSISDN is replaced by getFilteredIdentificationNumber
	                // This is done because this field can contains msisdn or
	                // account id
				 	if(!BTSLUtil.isNullString(msisdn))
				 		filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn);//changed
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	                final String arr[] = {msisdn};
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_MSISDN , arr);
	            }
			 
			 	// Change ID=ACCOUNTID
	            // isValidMsisdn is replaced by isValidIdentificationNumber
	            // This is done because this field can contains msisdn or account id
	            if (!BTSLUtil.isNullString(msisdn) && !msisdn.equals("null") && !BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
	            	final String arr[] = {msisdn};
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_MSISDN , arr);
	            }
	            // check prefix of the MSISDN
	            if(!BTSLUtil.isNullString(msisdn) && !msisdn.equals("null")) {
	            	String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); 
	            	NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
	            	if (networkPrefixVO == null) {
	            		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_NETWORK_PREFIX);
	            	}
	            	// check network support of the MSISDN
	            	String networkCode = networkPrefixVO.getNetworkCode();
	            	if (!networkCode.equals(userVO.getNetworkID())) {
	            		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_NETWORK_SUPPORT_MSISDN);
	            	 }
	            }

	            // Now load the information of the entered MSISDN form the
	            // restrictedMsisdn table

	            ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
	            /*
	             * ArrayList
	             * restrictedList=restrictedSubscriberDAO.loadResSubsDetails
	             * (con,filteredMsisdn,thisForm.getUserID(),false,null,null);
	             * 
	             * if(restrictedList==null || restrictedList.isEmpty())
	             * {
	             * throw new BTSLBaseException(this,"showScheduleDetails",
	             * "restrictedsubs.viewsingletrfschedule.msg.nouserinfo",0,new
	             * String[]{thisForm.getUserName()},"userSelect");
	             * }
	             */
	            // RestrictedSubscriberVO restrictedSubscriberVO
	            // =(RestrictedSubscriberVO)restrictedList.get(0);
	            // thisForm.setRestrictedSubscriberVO(restrictedSubscriberVO);

	            // Now load the schedule information of the entered MSISDN
	            if(!BTSLUtil.isNullString(msisdn) && !msisdn.equals("null") && BTSLUtil.isNullString(dateRange)) {
	            	String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "','" + PretupsI.SCHEDULE_STATUS_CANCELED + "'";
		            list = scheduledBatchDetailDAO.loadScheduleDetailVOList(con, searchUserVO.getOwnerID(), searchUserVO.getUserID(), filteredMsisdn, PretupsI.STATUS_IN, status, userVO.isStaffUser(), userVO.getActiveUserID());
		            viewScheduleDetailsListResponseVO.setScheduleDetailList(list);
	            }else if(!BTSLUtil.isNullString(dateRange)) {
	            	
	            	try {
						fromScheduleDate = BTSLUtil.getDateFromDateString(dateRange.split("-")[0].trim());
						toScheduleDate = BTSLUtil.getDateFromDateString(dateRange.split("-")[1].trim());
					} catch (Exception e) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_INVALID,
								new String[] { "Date range" });
					}
	            	
	            	if(toScheduleDate.after(new Date()))	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_INVALID,
							new String[] { "Date range" });
	            	
	            	String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "','" + PretupsI.SCHEDULE_STATUS_CANCELED + "'";
	            	list = scheduledBatchDetailDAO.loadScheduleDetailReportVOList(con, searchUserVO.getOwnerID(), searchUserVO.getUserID(), filteredMsisdn, PretupsI.STATUS_IN, status, userVO.isStaffUser(), userVO.getActiveUserID(), fromScheduleDate, toScheduleDate);
	            	viewScheduleDetailsListResponseVO.setScheduleDetailList(list);
	            }else {
	            	if(BTSLUtil.isNullString(dateRange)) throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_MISSING, new String[] {"Date range"});
	            }
	            
	            
	            

		 }catch (Exception e) {
	            log.error("showScheduleDetails", "Exception: " + e.getMessage());
	            log.errorTrace(methodName, e);
	            throw e;
	        } finally {
	            if (log.isDebugEnabled()) {
	                log.debug("showScheduleDetails", "Exit forward = ");
	            }
	        }
		 return viewScheduleDetailsListResponseVO;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void getCategoryList(UserVO userVO, CategoryResponseVO responseVO, Connection con) throws BTSLBaseException 
	{
		ArrayList<ChannelTransferRuleVO> catList = null;
		 ArrayList<String> domainList = new ArrayList<String>();
        boolean isRestricted = false;
        if(userVO.getDomainCodes()!= null)
        {
        	Collections.addAll( domainList, userVO.getDomainCodes());
        }
        
		if (PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(userVO.getUserType())) {

			if ("Y".equalsIgnoreCase(userVO.getCategoryVO().getTransferToListOnly())
					&& "Y".equalsIgnoreCase(userVO.getCategoryVO().getRestrictedMsisdns())) {
				isRestricted = true;

			} else if ("N".equalsIgnoreCase(userVO.getCategoryVO().getTransferToListOnly())
					&& "Y".equalsIgnoreCase(userVO.getCategoryVO().getRestrictedMsisdns())) {
				isRestricted = true;

			}
		}
        
        if (PretupsI.DOMAINS_NOTFIXED_NOTASSIGNED.equals(userVO.getCategoryVO().getFixedDomains())) {
            ChannelTransferRuleVO rulesVO = new ChannelTransferRuleVO();
            rulesVO.setToCategory(userVO.getCategoryCode());
            rulesVO.setToCategoryDes(userVO.getCategoryVO().getCategoryName());
            catList = new ArrayList<ChannelTransferRuleVO>();
            if (!PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(userVO.getUserType())) {
                catList.add(rulesVO);
            } else if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(userVO.getDomainTypeCode()) || isRestricted) {
                catList = new ChannelTransferRuleWebDAO().loadTrfRuleCatListForRestrictedMsisdn(con, userVO.getNetworkID(), userVO.getCategoryCode(), false, isRestricted);
            }
		
		
	}
        
        responseVO.setCatList(catList);
        responseVO.setMessage(PretupsI.SUCCESS);
        responseVO.setStatus(HttpServletResponse.SC_OK);
        responseVO.setMessageCode(PretupsI.SUCCESS);
        
        
	}
	
	@Override
	public ServiceListResponseVO servicesList(String loginId, Connection con, HttpServletResponse responseSwag)
			throws BTSLBaseException {
		ServiceListResponseVO response = new ServiceListResponseVO();

		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		ChannelUserVO userVO = new ChannelUserVO();
		UserDAO userDAO = new UserDAO();
		userVO = userDAO.loadUserDetailsByLoginId(con, loginId);
		List<ListValueVO> servicesListNew ;
//		if(!userVO.getDomainID().equals(PretupsI.OPERATOR_TYPE_OPT))
			servicesListNew = new ServicesTypeDAO().loadServicesListForC2SReconciliation(con,PretupsI.C2S_MODULE);
//		else{
//		servicesListNew = channelUserDAO.loadUserServicesList1(con, userVO.getUserID());
//		}
		List<ServiceListFilter> filterList = new ArrayList<ServiceListFilter>();
		for (ListValueVO listValueVO : servicesListNew) {
			ServiceListFilter serviceListFilter = new ServiceListFilter();
			serviceListFilter.setName(listValueVO.getLabel());
			serviceListFilter.setStatus(listValueVO.getStatus());
			serviceListFilter.setServiceType(listValueVO.getValue());
			filterList.add(serviceListFilter);
		}

		try {

			response.setServicesList(filterList);

			if (servicesListNew.size() > 1) {
				response.setStatus((PretupsI.RESPONSE_SUCCESS));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.SERVICE_LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SERVICE_LIST_FOUND);

			} else {
				throw new BTSLBaseException(C2SBulkRcServiceImpl.class.getName(),
						PretupsErrorCodesI.SERVICE_LIST_NOT_FOUND);
			}

		} catch (BTSLBaseException be) {
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.SERVICE_LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICE_LIST_NOT_FOUND);

		} catch (Exception e) {
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.SERVICE_LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICE_LIST_NOT_FOUND);
		}

		return response;

	}
	
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void getCategoryListWithoutTransferRules(UserVO userVO, CategoryRespVO responseVO, Connection con,String domainCode,String categoryCode) throws BTSLBaseException 
	{
  try {
        CategoryDAO categoryDao =new CategoryDAO();
       ArrayList<CategoryData> cateogoryList =  categoryDao.loadCategoryHierarchyUnderCategory(con, categoryCode, domainCode);
        responseVO.setCatList(cateogoryList);
        if(!BTSLUtil.isNullOrEmptyList(cateogoryList)) {
        responseVO.setMessage(PretupsI.SUCCESS);
        responseVO.setStatus(HttpServletResponse.SC_OK);
        responseVO.setMessageCode(PretupsI.SUCCESS);
        }else {
        	String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.NO_RECORDS_FOUND, null);
        	responseVO.setMessage(resmsg);
            responseVO.setStatus(HttpServletResponse.SC_OK);
            responseVO.setMessageCode(PretupsErrorCodesI.NO_RECORDS_FOUND);
        	
        }
  } catch (BTSLBaseException be) {
	  responseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
		String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				PretupsErrorCodesI.TECHNICAL_ERROR, null);
		responseVO.setMessage(resmsg);
		responseVO.setMessageCode(PretupsErrorCodesI.TECHNICAL_ERROR);

	} catch (Exception e) {
		responseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
		String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				PretupsErrorCodesI.TECHNICAL_ERROR, null);
		responseVO.setMessage(resmsg);
		responseVO.setMessageCode(PretupsErrorCodesI.TECHNICAL_ERROR);
	}
 
        
	}



	@Override
	public C2SBulkEvdRechargeResponseVO processRequestBulkEVD(C2SBulkEvdRechargeRequestVO requestVO,
			String serviceKeyword, String requestIDStr, String requestFor, MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException {
		final String METHOD_NAME = "processRequestBulkEVD";
		C2SBulkEvdRechargeResponseVO response =c2SBulkEVDProcessor.processRequestBulkEVD(requestVO, serviceKeyword, requestIDStr, requestFor, headers, responseSwag);
		return response;
        
	}

	@Override
	public ReconcileServiceListResponseVO getReconservicesList(String loginId, Connection con,
			HttpServletResponse responseSwag) throws BTSLBaseException {
		
		ReconcileServiceListResponseVO reconcileServiceListResponseVO = new ReconcileServiceListResponseVO();
		List<ServiceListFilter> servicesList=new ArrayList<ServiceListFilter>();
		final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
	    ArrayList list = servicesTypeDAO.loadServicesListForReconciliation(con, PretupsI.C2S_MODULE);
	    
	    if(!BTSLUtil.isNullOrEmptyList(list)){
	    	for (int i=0;i <list.size();i++ ) {
	    		ListValueVO listVO =(ListValueVO) list.get(i);
	    		ServiceListFilter serviceListFilter = new ServiceListFilter();
	    		serviceListFilter.setServiceType(listVO.getValue());
	    		serviceListFilter.setName(listVO.getLabel());
	    		servicesList.add(serviceListFilter);
	    	}
	    	reconcileServiceListResponseVO.setStatus(HttpStatus.SC_OK);
	    	responseSwag.setStatus(HttpStatus.SC_OK);
	    	reconcileServiceListResponseVO.setServicesList(servicesList);
	    	reconcileServiceListResponseVO.setMessageCode(PretupsErrorCodesI.SUCCESS);
	    	reconcileServiceListResponseVO.setMessageCode(PretupsRestUtil.getMessageString(PretupsErrorCodesI.SUCCESS));
	    }else {
	    	reconcileServiceListResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	    	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	    	reconcileServiceListResponseVO.setMessageCode(PretupsI.ADDITIONAL_SUMMARY_NOSERVICEFOUND);
	    	reconcileServiceListResponseVO.setMessageCode(PretupsRestUtil.getMessageString(PretupsI.ADDITIONAL_SUMMARY_NOSERVICEFOUND));
	    }

		return reconcileServiceListResponseVO;
	}

	


}
