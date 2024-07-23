package com.restapi.c2cbulk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.spring.custom.action.Globals;
import com.btsl.util.MessageResources;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListServiceImpl;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.xl.ExcelRW;
import com.web.pretups.channel.transfer.businesslogic.C2CBatchTransferWebDAO;

@Service("C2CBulkServiceI")
public class C2CBulkServiceImpl implements C2CBulkServiceI {
	private final Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	public C2CProcessBulkApprovalResponseVO processC2cBulkTrfAppProcess(MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, C2CProcessBulkRequestVO req, HttpServletRequest httprequest) {
		final String methodName = "processC2cBulkTrfAppProcess";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		boolean processRunning = true;
		String filePath = null;
		// Name of file to be uploaded on server
		String fileName = null;
		C2CProcessBulkApprovalResponseVO response = new C2CProcessBulkApprovalResponseVO();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		ErrorMap errorMap = new ErrorMap();
		ProcessStatusVO processVO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
			ErrorFileResponse errorFileResponse =  new ErrorFileResponse();
			final C2CBatchTransferWebDAO c2cBatchTransferWebDAO = new C2CBatchTransferWebDAO();
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());
			String loginId = oAuthUserData.getData().getLoginid();
			ChannelUserVO channelUserVO1 = null;
			ArrayList errorList = new ArrayList<>();
			C2CBatchMasterVO c2cBatchMasterVO = null;
			String currentLevel = null;
			boolean showLogs = true;
			final ProcessBL processBL = new ProcessBL();
			ChannelUserVO channelUserVO = new ChannelUserVO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			

			channelUserVO = channelUserDAO.loadActiveUserId(con, oAuthUserData.getData().getLoginid(), "LOGINID");
			if(channelUserVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)) {
				UserDAO _userDAO = new UserDAO();
            	channelUserVO = channelUserDAO.loadStaffUserDetailsByLoginId(con, oAuthUserData.getData().getLoginid());
            	settingStaffDetails(channelUserVO);
            	if(channelUserVO.getUserPhoneVO().getMsisdn()==null) {//means staff has no msisdn
            		UserPhoneVO parentPhoneVO = _userDAO.loadUserPhoneVO(con, channelUserVO.getUserID());//getting parent User phoneVO
            		channelUserVO.setUserPhoneVO(parentPhoneVO);
            	}
			}else	channelUserVO = channelUserDAO.loadChannelUserDetails(con,oAuthUserData.getData().getMsisdn());
			
			final UserVO userVO = (UserVO)channelUserVO;
			
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.C2C_BATCH_PROCESS_ID,
						userVO.getNetworkID());
			} catch (BTSLBaseException e) {
				log.error(methodName, "Exception:e=" + e);
				log.errorTrace(methodName, e);
				processRunning = false;
				throw new BTSLBaseException(this, methodName, "batchc2c.processfile.error.alreadyexecution");
			}
			if (processVO != null && !processVO.isStatusOkBool()) {
				processRunning = false;
				throw new BTSLBaseException(this, methodName, "batchc2c.processfile.error.alreadyexecution");
			}

			mcomCon.partialCommit();
			processVO.setNetworkCode(userVO.getNetworkID());
			// ends here

			
			
			vaildateBasicRequest(req);
			// max file size that can be uploaded ion server(Read from
			// constant.props)
			String contentsSize = null;
			try {
				// max content size that file can hold
				contentsSize = Constants.getProperty("C2C_BATCH_APPROVAL_FILE_SIZE");
			} catch (Exception e) {
				log.error(methodName, "C2C_BATCH_APPROVAL_FILE_SIZE not defined in Constant Property file");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "C2CBatchTransferApprovalAction[processFile]", "", "", "",
						"C2C_BATCH_APPROVAL_FILE_SIZE not defined in Constant Property file");
				log.errorTrace(methodName, e);
				throw new BTSLBaseException(this, methodName, "batchc2c.batchc2cprocessfile.msg.filesizemissing",
						"uploadFileForC2CApproval");

			}
			// content type of file
			final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLS);
			try {
				filePath = Constants.getProperty("C2C_BATCH_APPROVAL_FILE_PATH");
			} catch (Exception e) {
				log.error(methodName, "C2C_BATCH_APPROVAL_FILE_PATH not defined in Constant Property file");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "C2CBatchTransferApprovalAction[processFile]", "", "", "",
						"C2C_BATCH_APPROVAL_FILE_PATH not defined in Constant Property file");
				log.errorTrace(methodName, e);
				throw new BTSLBaseException(this, methodName, "batchc2c.batchc2cprocessfile.msg.filepathmissing",
						"uploadFileForC2CApproval");
			}
			fileName = req.getFileName() + "." + req.getFileType();
			// Upload the file on server
			String base64Bytes = req.getFile();
			try {
				log.debug("writeByteArrayToFile: ", filePath);
				log.debug("writeByteArrayToFile: ", base64Bytes);
				/*
				 * if (new File(filepathtemp).exists()) { fileExist=true; throw new
				 * BTSLBaseException("OAuthenticationUtil", "writeByteArrayToFile",
				 * PretupsErrorCodesI.BATCH_UPLOAD_FILE_EXISTS, PretupsI.RESPONSE_FAIL, null); }
				 */
				FileUtils.writeByteArrayToFile(new File(filePath+""+fileName), decodeFile(base64Bytes));
				// isFileWritten = true ;
			} catch (BTSLBaseException be) {
				throw be;
			} catch (Exception e) {
				log.debug("writeByteArrayToFile: ", e.getMessage());
				log.error("writeByteArrayToFile", "Exceptin:e=" + e);
				log.errorTrace("writeByteArrayToFile", e);

			}
			// final boolean isFileUploaded = BTSLUtil.uploadFileToServer(req.getFile(),
			// filePath, contentType, "uploadFileForC2CApproval",
			// Long.parseLong(contentsSize));
			// object of ExcelRW class, that will be used to read the exl file
			final ExcelRW excelRW = new ExcelRW();
			String[][] arr = null;
			// To keep track of index of fields after level 1 approved on.
			// Because in case of level 1 approval we will not show level 2
			// approval details.
			int indexToBeReduced = 0, indexToBeReducedForRemarks = 0;
			final HashMap<String, String> map = new HashMap<String, String>();
			// added by akanksha for tigo gautemala CR
            ListValueVO errorVO = null;
			final int rowOffset = 1;
			arr = excelRW.readMultipleExcel(ExcelFileIDI.BATCH_C2C_APPRV, filePath + fileName, true, rowOffset, map);
			indexToBeReduced = 2;
			indexToBeReducedForRemarks = 1;
			// get the datamap from the form
			LinkedHashMap hashMap = null;
			String statusUsed = null;
			statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "'";
			final C2CBatchTransferDAO c2cBatchTransferDAO = new C2CBatchTransferDAO();
			hashMap = c2cBatchTransferDAO.loadBatchItemsMap(con, req.getBatchId(), statusUsed);
			C2CBatchItemsVO c2cBatchItemVO = null;
			// contains the error list in file validation occur
			final ArrayList fileErrorList = new ArrayList();
			// contains error list when error is found when approve or reject
			// the record
			ArrayList approveRejectErrorList = null;
			// contains error list when record is closed
			ArrayList closeErrorList = null;
			// true if file validation error exists
			boolean fileValidationErrorExists = false;
			// holds c2cBatchMasterVO for approve/reject
			final LinkedHashMap approveRejectMap = new LinkedHashMap();
			// holds c2cBatchMasterVO for order close
			final LinkedHashMap closedOrderMap = new LinkedHashMap();
			// counts the discard count
			int discardCounts = 0;
			// counts the blank line
			int blankCount = 0;
			int arrs = arr.length;
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			
			for (int i = 1, j =arrs ; i < j; i++) {
                // Check if detail id is null then data is invalid
                if (BTSLUtil.isNullString(arr[i][0])) {
                    if (BTSLUtil.isNullArray(arr[i])) {
                        blankCount++;
                        continue;
                    }
                    
                    RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
	            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
	            	MasterErrorList masterErrorList = new MasterErrorList();
					masterErrorList.setErrorCode( "batchc2c.batchapprovereject.msg.error.invaliddata");
					String msg = PretupsRestUtil.getMessageString( "batchc2c.batchapprovereject.msg.error.invaliddata");
					masterErrorList.setErrorMsg(msg);
					masterErrorLists.add(masterErrorList);
					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
					rowErrorMsgLists.setRowName(arr[i][1]);
					rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                    fileValidationErrorExists = true;
                    errorVO = new ListValueVO(arr[i][1], String.valueOf(i + 1), msg,
                    		"batchc2c.batchapprovereject.msg.error.invaliddata");
                    fileErrorList.add(errorVO);
                    continue;
                }
                // load the data from the map for the batch detail id of array.
                // If no record found file is rejected
                c2cBatchItemVO = (C2CBatchItemsVO) hashMap.get(arr[i][0]);
                if (c2cBatchItemVO == null) {
                    MasterErrorList masterErrorList = new MasterErrorList();
                    RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					String msg = PretupsRestUtil.getMessageString( "batchc2c.batchapprovereject.msg.error.invalidbatchdetailsid");
					masterErrorList.setErrorCode("batchc2c.batchapprovereject.msg.error.invalidbatchdetailsid");
					masterErrorList.setErrorMsg(msg);
					if(rowErrorMsgLists.getMasterErrorList() == null)
						rowErrorMsgLists.setMasterErrorList(new ArrayList<MasterErrorList>());
					rowErrorMsgLists.getMasterErrorList().add(masterErrorList);
					rowErrorMsgLists.setRowName(arr[i][1]);
					rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                    fileValidationErrorExists = true;
                    errorVO = new ListValueVO(arr[i][1], String.valueOf(i + 1), msg,
                    		"batchc2c.batchapprovereject.msg.error.invalidbatchdetailsid");
                    fileErrorList.add(errorVO);
                    continue;
                }
                // set the record number for showing on error log
                c2cBatchItemVO.setRecordNumber(i + 1);
                // check msisdn is updated or not.
                // If msisdn in file is null then check for login id
                if (BTSLUtil.isNullString(arr[i][1]) && (!c2cBatchItemVO.getLoginID().equals(arr[i][4]))) {
                    MasterErrorList masterErrorList = new MasterErrorList();
                    RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					masterErrorList.setErrorCode("batchc2c.batchapprovereject.msg.error.validmdnorlidreqd");
					String msg = PretupsRestUtil.getMessageString("batchc2c.batchapprovereject.msg.error.validmdnorlidreqd");
					masterErrorList.setErrorMsg(msg);
					if(rowErrorMsgLists.getMasterErrorList() == null)
						rowErrorMsgLists.setMasterErrorList(new ArrayList<MasterErrorList>());
					rowErrorMsgLists.getMasterErrorList().add(masterErrorList);
					rowErrorMsgLists.setRowName(arr[i][1]);
					rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
					 errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(i + 1), msg,
							 "batchc2c.batchapprovereject.msg.error.validmdnorlidreqd");
	                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else if (!BTSLUtil.isNullString(arr[i][1]) && !c2cBatchItemVO.getMsisdn().equals(arr[i][1])) {
                    
                    MasterErrorList masterErrorList = new MasterErrorList();
                    RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					masterErrorList.setErrorCode( "batchc2c.batchapprovereject.msg.error.invalidmsisdn");
					String msg = PretupsRestUtil.getMessageString("batchc2c.batchapprovereject.msg.error.invalidmsisdn");
					masterErrorList.setErrorMsg(msg);
					if(rowErrorMsgLists.getMasterErrorList() == null)
						rowErrorMsgLists.setMasterErrorList(new ArrayList<MasterErrorList>());
					rowErrorMsgLists.getMasterErrorList().add(masterErrorList);
					rowErrorMsgLists.setRowName(arr[i][1]);
					rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                    fileValidationErrorExists = true;
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(i + 1), msg,
                    		"batchc2c.batchapprovereject.msg.error.invalidmsisdn");
	                    fileErrorList.add(errorVO);
                    continue;
                }
                // Check the required action is valid or not(Only valid values
                // are "Y","N","D" or fill nothing)
                if (!BTSLUtil.isNullString(arr[i][13 - indexToBeReduced]) && !("Y".equalsIgnoreCase(arr[i][13 - indexToBeReduced])) && !("N"
                    .equalsIgnoreCase(arr[i][13 - indexToBeReduced])) && !("D".equalsIgnoreCase(arr[i][13 - indexToBeReduced]))) {
                    MasterErrorList masterErrorList = new MasterErrorList();
                    RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					masterErrorList.setErrorCode( "batchc2c.batchapprovereject.msg.error.invalidreqaction");
					String msg = PretupsRestUtil.getMessageString("batchc2c.batchapprovereject.msg.error.invalidreqaction");
					masterErrorList.setErrorMsg(msg);
					if(rowErrorMsgLists.getMasterErrorList() == null)
						rowErrorMsgLists.setMasterErrorList(new ArrayList<MasterErrorList>());
					rowErrorMsgLists.getMasterErrorList().add(masterErrorList);
					rowErrorMsgLists.setRowName(arr[i][1]);
					rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
					 errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(i + 1), msg,
							 "batchc2c.batchapprovereject.msg.error.invalidreqaction");
		                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                if (BTSLUtil.isNullString(arr[i][13 - indexToBeReduced]) || "D".equalsIgnoreCase(arr[i][13 - indexToBeReduced])) {
                    discardCounts++;
                    continue;
                }
                // Check batch id is same or not
                if (!c2cBatchItemVO.getBatchId().equals(arr[i][5])) {
                    MasterErrorList masterErrorList = new MasterErrorList();
                    RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					masterErrorList.setErrorCode("batchc2c.batchapprovereject.msg.error.invalidbatchid");
					String msg = PretupsRestUtil.getMessageString("batchc2c.batchapprovereject.msg.error.invalidbatchid");
					masterErrorList.setErrorMsg(msg);
					if(rowErrorMsgLists.getMasterErrorList() == null)
						rowErrorMsgLists.setMasterErrorList(new ArrayList<MasterErrorList>());
					rowErrorMsgLists.getMasterErrorList().add(masterErrorList);
					rowErrorMsgLists.setRowName(arr[i][1]);
					rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                    fileValidationErrorExists = true;
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(i + 1), msg,
                    		"batchc2c.batchapprovereject.msg.error.invalidbatchid");
		                    fileErrorList.add(errorVO);
                    continue;
                }
                long reqQuantity;
                try {
                    // check required quantity is numeric or not
                    reqQuantity = PretupsBL.getSystemAmount(arr[i][6]);
                } catch (Exception e) {
                    MasterErrorList masterErrorList = new MasterErrorList();
                    RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					masterErrorList.setErrorCode("batchfoc.batchapprovereject.msg.error.qtynotnumeric");
					String msg = PretupsRestUtil.getMessageString("batchfoc.batchapprovereject.msg.error.qtynotnumeric");
					masterErrorList.setErrorMsg(msg);
					if(rowErrorMsgLists.getMasterErrorList() == null)
						rowErrorMsgLists.setMasterErrorList(new ArrayList<MasterErrorList>());
					rowErrorMsgLists.getMasterErrorList().add(masterErrorList);
					rowErrorMsgLists.setRowName(arr[i][1]);
					rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                    fileValidationErrorExists = true;
                    log.errorTrace(methodName, e);
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(i + 1), msg,
                    		"batchfoc.batchapprovereject.msg.error.qtynotnumeric");
		                    fileErrorList.add(errorVO);
                    continue;
                }
                //ASHU check for quantity multiple-off in commision prof product
                FOCBatchTransferDAO focDAO = new FOCBatchTransferDAO();
                CommissionProfileProductsVO cpVO = focDAO.getMultipleOff(con,c2cBatchItemVO.getMsisdn(),c2cBatchItemVO.getLoginID());
                if(cpVO != null) {
                	 if(cpVO.getTransferMultipleOff() > 0) {
                     	if (reqQuantity % cpVO.getTransferMultipleOff() != 0){
                            MasterErrorList masterErrorList = new MasterErrorList();
                            RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
        					masterErrorList.setErrorCode("batchfoc.batchapprovereject.msg.error.qtynotmult");
        					String msg = PretupsRestUtil.getMessageString("batchfoc.batchapprovereject.msg.error.qtynotmult");
        					masterErrorList.setErrorMsg(msg);
        					if(rowErrorMsgLists.getMasterErrorList() == null)
        						rowErrorMsgLists.setMasterErrorList(new ArrayList<MasterErrorList>());
        					rowErrorMsgLists.getMasterErrorList().add(masterErrorList);
        					rowErrorMsgLists.setRowName(arr[i][1]);
        					rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
        					if(errorMap.getRowErrorMsgLists() == null)
        						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
        					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
        					  errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(i + 1), msg,
        							  "batchfoc.batchapprovereject.msg.error.qtynotmult");
        			                    fileErrorList.add(errorVO);
                     		fileValidationErrorExists = true;
                     		continue;
                     	}
                     }
                	 if (cpVO.getMinTransferValue() > reqQuantity || cpVO.getMaxTransferValue() < reqQuantity) { 
                  		String msg =  "Quantity "+PretupsBL.getDisplayAmount(reqQuantity)+" is not between "+PretupsBL.getDisplayAmount(cpVO.getMinTransferValue())+" and "+PretupsBL.getDisplayAmount(cpVO.getMaxTransferValue());
                  	  MasterErrorList masterErrorList = new MasterErrorList();
                      RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
  					  masterErrorList.setErrorCode("batchfoc.batchapprovereject.msg.error.qtynotmult");
  					  masterErrorList.setErrorMsg(msg);
  					  if(rowErrorMsgLists.getMasterErrorList() == null)
						rowErrorMsgLists.setMasterErrorList(new ArrayList<MasterErrorList>());
					  rowErrorMsgLists.getMasterErrorList().add(masterErrorList);
  					  rowErrorMsgLists.setRowName(arr[i][1]);
  					  rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
  					if(errorMap.getRowErrorMsgLists() == null)
  						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
  					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
  					 errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(i + 1), msg,
  							"batchfoc.batchapprovereject.msg.error.qtynotmult");
			                    fileErrorList.add(errorVO);
            		fileValidationErrorExists = true;
                  		fileValidationErrorExists = true;
                  		continue;
                	 }
                }
                // check required qty is same or not
                if (!(c2cBatchItemVO.getRequestedQuantity() == reqQuantity)) {
                    MasterErrorList masterErrorList = new MasterErrorList();
                    RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					masterErrorList.setErrorCode("batchc2c.batchapprovereject.msg.error.incorrectqty");
					String msg = PretupsRestUtil.getMessageString("batchc2c.batchapprovereject.msg.error.incorrectqty");
					masterErrorList.setErrorMsg(msg);
					if(rowErrorMsgLists.getMasterErrorList() == null)
						rowErrorMsgLists.setMasterErrorList(new ArrayList<MasterErrorList>());
					rowErrorMsgLists.getMasterErrorList().add(masterErrorList);
					rowErrorMsgLists.setRowName(arr[i][1]);
					rowErrorMsgLists.setRowValue("Line" + String.valueOf(i + 1));
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
					 errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(i + 1), msg,
	  							"batchfoc.batchapprovereject.msg.error.qtynotmult");
				                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }

                // If file validation not exists then only construct the
                // c2cBatchItemVO and add map for processing
                if (!fileValidationErrorExists) {
                        if (arr[i][13 - indexToBeReducedForRemarks] != null) {
                            if (arr[i][13 - indexToBeReducedForRemarks].length() > 100) {
                                c2cBatchItemVO.setApproverRemarks(arr[i][13 - indexToBeReducedForRemarks].substring(0, 100));
                            } else {
                                c2cBatchItemVO.setApproverRemarks(arr[i][13 - indexToBeReducedForRemarks]);
                            }
                        }
                        if ("N".equalsIgnoreCase(arr[i][13 - indexToBeReduced])) {
                            c2cBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                        } else if ("Y".equalsIgnoreCase(arr[i][13 - indexToBeReduced])) {
                            // If request is to approve the order
                            c2cBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                        }
                    // If status is closed then contruct map for closing
                    // otherwise one map will be there
                    if (c2cBatchItemVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                        closedOrderMap.put(c2cBatchItemVO.getBatchDetailId(), c2cBatchItemVO);
                    } else {
                        approveRejectMap.put(c2cBatchItemVO.getBatchDetailId(), c2cBatchItemVO);
                    }
                }
                
               
                
            }
            if (fileValidationErrorExists) {
              response.setErrorMap(errorMap);
              String[] msgArr = new String[1];
              msgArr[0] = String.valueOf(discardCounts);
              response.setNumberOfRecords(arr.length - blankCount-1);
              String msg = PretupsRestUtil.getMessageString("batchc2c.batchc2capprove.msg.batchprocessfilefailed",msgArr);
			  response.setStatus(400+"");
			  response.setMessage(msg);
			  response.setScheduleBatchId(req.getBatchId());
			  response.setMessageCode("241198");
            }else {
            	 statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
            	  ArrayList<C2CBatchMasterVO> c2cBatchMasterVOList = c2cBatchTransferWebDAO.loadBatchC2CMasterDetailsForTxr(con, channelUserVO.getUserID(), statusUsed,
                          PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            	   c2cBatchMasterVOList =  (ArrayList) c2cBatchMasterVOList.stream().filter((obj)->{
            		  return  obj.getBatchId().equals(req.getBatchId());
            	  }).collect(Collectors.toList());
            	   if(c2cBatchMasterVOList == null || c2cBatchMasterVOList.size() <= 0) {
            		    c2cBatchMasterVOList = c2cBatchTransferWebDAO.loadBatchC2CMasterDetailsForWdr(con, channelUserVO.getUserID(), statusUsed,
            	                PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            		    c2cBatchMasterVOList =  (ArrayList) c2cBatchMasterVOList.stream().filter((obj)->{
                  		  return  obj.getBatchId().equals(req.getBatchId());
                  	  }).collect(Collectors.toList());    
            		    
            	   }
            	   
            	   if(c2cBatchMasterVOList == null ||c2cBatchMasterVOList.size() <=  0) {
            		 
                       	throw new BTSLBaseException(PretupsErrorCodesI.INVALID_BATCH_ID);
                       
            	   }
                c2cBatchMasterVO = (C2CBatchMasterVO) c2cBatchMasterVOList.get(0);
                boolean isModified = false;
                if ((closedOrderMap != null && !closedOrderMap.isEmpty()) || (approveRejectMap != null && !approveRejectMap.isEmpty())) { 
                    isModified = c2cBatchTransferDAO.isBatchModified(con, c2cBatchMasterVO.getModifiedOn().getTime(), req.getBatchId());
                    if (isModified) {
                    	 response.setErrorMap(errorMap);
                         String[] msgArr = new String[1];
                         msgArr[0] = String.valueOf(discardCounts);
                         response.setNumberOfRecords(arr.length - blankCount);
                         String msg = PretupsRestUtil.getMessageString("batchc2c.batchc2capprovereject.msg.batchmodify");
           			     response.setStatus(400+"");
           			     response.setMessage(msg);
           			     response.setScheduleBatchId(req.getBatchId());
           			     response.setMessageCode("batchc2c.batchc2capprovereject.msg.batchmodify");
                    } else {
                        UserGeographiesVO userGeoVO = null;
                        final ArrayList userGeographyList = channelUserVO.getGeographicalAreaList();
                        if (userGeographyList != null && !userGeographyList.isEmpty()) {
                            userGeoVO = (UserGeographiesVO) userGeographyList.get(0);
                            channelUserVO.setGeographicalCode(userGeoVO.getGraphDomainCode());
                        }
                         currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE;
                        if (closedOrderMap != null && !closedOrderMap.isEmpty()) {
                            // If closedOrderMap is not null then closed the
                            // orders in closedOrderMap
                            final int updateCount = c2cBatchTransferDAO.updateBatchStatus(con, c2cBatchItemVO.getBatchId(),
                                PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
                            if (updateCount <= 0) {
                            	mcomCon.partialRollback();
                                throw new BTSLBaseException(this, methodName, "error.general.processing", "firstPage");
                            }
                            mcomCon.partialCommit();
                            if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
           	                  channelUserVO1 = (ChannelUserVO)channelUserVO;
                             }
                            closeErrorList = c2cBatchTransferDAO.closeOrderByBatch(con, closedOrderMap, currentLevel, channelUserVO, (C2CBatchMasterVO)c2cBatchMasterVO , ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)),locale, req.getLanguage1(), req.getLanguage2(),channelUserVO1);
                        }// If approveRejectMap is not null then approve or
                         // reject the order
                        if (approveRejectMap != null && !approveRejectMap.isEmpty()) {
                            // If closedOrderMap is not null then closed the
                            // orders in closedOrderMap
                            final int updateCount = c2cBatchTransferDAO.updateBatchStatus(con, c2cBatchItemVO.getBatchId(),
                                PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
                            if (updateCount <= 0) {
                            	mcomCon.partialRollback();
                                throw new BTSLBaseException(this, methodName, "error.general.processing", "firstPage");
                            }
                        
                            mcomCon.partialCommit();
                            
                            approveRejectErrorList = c2cBatchTransferWebDAO.processOrderByBatch(con, approveRejectMap, currentLevel, channelUserVO.getUserID(),
                                ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)),locale, req.getLanguage1(), req.getLanguage2());
                        }
                       
                        if (closeErrorList != null && !closeErrorList.isEmpty()) {
                            if (approveRejectErrorList != null && !approveRejectErrorList.isEmpty()) {
                                approveRejectErrorList.addAll(closeErrorList);
                               
                                }
                            errorList.addAll(approveRejectErrorList);
                           // Collections.sort(errorList);
                        }
                        String[]   msgArr = new String[2];
                      if (errorList== null || errorList.isEmpty()) {
                          msgArr[0] = String.valueOf(closedOrderMap.size() + approveRejectMap.size());
                          msgArr[1] = String.valueOf(discardCounts);
        				  String msg = PretupsRestUtil.getMessageString("batchc2c.batchc2cprocessfile.msg.bachprocessfilesuccessfully",msgArr);
      					  response.setStatus(200+"");
      					  response.setMessage(msg);
      					  response.setScheduleBatchId(req.getBatchId());
      					  response.setErrorMap(errorMap);
      					  response.setMessageCode("batchc2c.batchc2cprocessfile.msg.bachprocessfilesuccessfully");
                    	  response.setNumberOfRecords(arr.length - discardCounts - 1 - blankCount);
                      } else if (errorList.size() + discardCounts == arr.length - 1) {
                    	  msgArr[0] = String.valueOf(discardCounts);
                    	  response.setNumberOfRecords(arr.length - discardCounts - 1 - blankCount);
        				  String msg = PretupsRestUtil.getMessageString("batchc2c.batchc2capprove.msg.bachprocessfilefailedall",msgArr);
        				  response.setMessage(msg);
      					  response.setScheduleBatchId(req.getBatchId());
      					  response.setMessageCode("batchc2c.batchc2capprove.msg.bachprocessfilefailedall");
      					  response.setErrorMap(errorMap);
                       }else {
                   	      response.setNumberOfRecords(arr.length - discardCounts - 1 - blankCount);
                          msgArr[0] = String.valueOf(closedOrderMap.size() + approveRejectMap.size() - errorList.size());
                          msgArr[1] = String.valueOf(discardCounts);
                          String msg = PretupsRestUtil.getMessageString("batchc2c.batchc2capprove.msg.bachprocessfilefailedsuccess",msgArr);
       				      response.setMessage(msg);
     					  response.setScheduleBatchId(req.getBatchId());
     					  response.setMessageCode("batchc2c.batchc2capprove.msg.bachprocessfilefailedall");
     					  response.setErrorMap(errorMap);
                       }
                       
                    }
                } else {
                    String[]  msgArr = new String[] { String.valueOf(closedOrderMap.size() + approveRejectMap.size()), String.valueOf(discardCounts) };
					String msg = PretupsRestUtil.getMessageString("batchc2c.batchc2cprocessfile.msg.bachprocessfilesuccessfully",msgArr);
					response.setStatus(200+"");
					response.setMessage(msg);
					response.setScheduleBatchId(req.getBatchId());
					response.setMessageCode("batchc2c.batchc2cprocessfile.msg.bachprocessfilesuccessfully");
                }
                
                
                
            }
           // errorList.addAll(fileErrorList);
            if(errorList.size() > 0 || fileErrorList.size() > 0) {
            for (int i = 0, j = errorList.size(); i < j; i++) {
            	 errorVO = (ListValueVO) fileErrorList.get(i);
	            if(!BTSLUtil.isNullString(errorVO.getOtherInfo()))
                {
	            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
	            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
	            	MasterErrorList masterErrorList = new MasterErrorList();
					masterErrorList.setErrorCode(errorVO.getOtherInfo());
					String msg = errorVO.getOtherInfo2();
					masterErrorList.setErrorMsg(msg);
					masterErrorLists.add(masterErrorList);
					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
					rowErrorMsgLists.setRowName(errorVO.getCodeName());
					rowErrorMsgLists.setRowValue( "Line" + String.valueOf(Long.parseLong(errorVO.getIDValue()) + 1));
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                }
            }
            
             errorFileRequestVO.setRowErrorMsgLists(errorMap.getRowErrorMsgLists());
			 errorFileRequestVO.setFile(req.getFile());
			 errorFileRequestVO.setFiletype(req.getFileType());
			 errorFileRequestVO.setPartialFailure(false);
			 new DownloadUserListServiceImpl().downloadErrorFile(errorFileRequestVO, errorFileResponse, responseSwag);
			 if (fileValidationErrorExists) {
				 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			 }
			 response.setFileAttachment(errorFileResponse.getFileAttachment());
			 response.setFileName(errorFileResponse.getFileName()); 
       // writeFileForResponse(response, errorMap);
         }
		} catch (BTSLBaseException be) {
			log.error("processC2cBulkTrfAppProcess", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			if(BTSLUtil.isNullString(msg)) {
				response.setMessage(be.getMessage());
			}else {
			response.setMessage(msg);
			}
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(401+"");
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(400+"");
			}

			response.setErrorMap(errorMap);
		} catch (Exception e) {
			log.debug("processC2cBulkTrfAppProcess", e);
			response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.REQ_NOT_PROCESS, null);
			response.setMessage(resmsg);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(400+"");

		} finally {
			
			log.debug("processFile", "Exit");
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
	                        log.error(methodName, " Exception in update process detail for batch c2c approval file processing  " + e.getMessage());
	                    }
	                    log.errorTrace(methodName, e);
	                }
	            }
	                if (filePath != null && fileName != null) {
	                    final File fileToDelete = new File(filePath, fileName);
	                    if (fileToDelete.exists()) {
	                    	boolean isDeleted = fileToDelete.delete();
	        	            if(isDeleted){
	        	            	log.debug(methodName, "File deleted successfully");
	        	            }
	                    }
	                }
	            
			 
			 if (mcomCon != null) {
					mcomCon.close("C2CBulkServiceImpl#processC2cBulkTrfAppProcess");
					mcomCon = null;
				}

		}

		return response;

	}

	private void vaildateBasicRequest(C2CProcessBulkRequestVO req)  throws BTSLBaseException{
		
		String METHOD_NAME = "vaildateBasicRequest";
		if(BTSLUtil.isNullString(req.getBatchId())) {
            throw new BTSLBaseException("BatchId can not be blank");                   
		}else if(BTSLUtil.isNullString(req.getFile())) {
			throw new BTSLBaseException("file attachment can not be blank"); 
		}else if(BTSLUtil.isNullString(req.getFileName())) {
			throw new BTSLBaseException("filename can not be blank"); 
		}else if(BTSLUtil.isNullString(req.getFileType())) {
			throw new BTSLBaseException("filetype can not be blank"); 
		}
		
		if(BTSLUtil.isNullString(req.getLanguage1())) {
			req.setLanguage1((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
		}
		
		if(BTSLUtil.isNullString(req.getLanguage2())) {
			req.setLanguage1((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
		}
	}

	/**
	 * 
	 * @param base64value
	 * @return
	 * @throws BTSLBaseException
	 */
	public byte[] decodeFile(String base64value) throws BTSLBaseException {
		byte[] base64Bytes = null;
		try {
			log.debug("decodeFile: ", base64value);
			base64Bytes = Base64.getMimeDecoder().decode(base64value);
			log.debug("base64Bytes: ", base64Bytes);
		} catch (IllegalArgumentException il) {
			log.debug("Invalid file format", il);
			log.error("Invalid file format", il);
			log.errorTrace("Invalid file format", il);
			throw new BTSLBaseException(this, "decodeFile", PretupsErrorCodesI.INVALID_FILE_FORMAT,
					PretupsI.RESPONSE_FAIL, null);
		}
		return base64Bytes;
	}

	
	
	private void writeFileForResponse(C2CProcessBulkApprovalResponseVO response, ErrorMap errorMap)throws BTSLBaseException, IOException{
    	if(errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
    		return ;
    	List<List<String>> rows = new ArrayList<>();
		for(int i=0;i<errorMap.getRowErrorMsgLists().size();i++)
		{
			RowErrorMsgLists rowErrorMsgList = errorMap.getRowErrorMsgLists().get(i);
			for(int col= 0; col< rowErrorMsgList.getMasterErrorList().size(); col++)
			{
				MasterErrorList masterErrorList=rowErrorMsgList.getMasterErrorList().get(col);
			    rows.add(( Arrays.asList(rowErrorMsgList.getRowValue(),rowErrorMsgList.getRowName(), masterErrorList.getErrorMsg())));
			}
			
		}
		String filePathCons = Constants.getProperty("C2C_BATCH_APPROVAL_FILE_PATH");
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
	
	
	public void writeFileCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
		try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
			csvWriter.append("Line number");
			csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
			csvWriter.append("Mobile number/login ID");
			csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
			csvWriter.append("Reason");
			csvWriter.append("\n");

			for (List<String> rowData : listBook) {
				csvWriter.append(String.join(Constants.getProperty("FILE_SEPARATOR_O2C"), rowData));
				csvWriter.append("\n");
			}


		}
    }
	
	private void settingStaffDetails(ChannelUserVO channelUserVO) {

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			UserDAO userDao = new UserDAO();
            UserPhoneVO phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
            if (phoneVO != null) {
                channelUserVO.setActiveUserMsisdn(phoneVO.getMsisdn());
                channelUserVO.setActiveUserPin(phoneVO.getSmsPin());
               }
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
				
		}catch(Exception e) {
			
		}finally {
			if(mcomCon != null)
			{
				mcomCon.close("C2CTransferController#checkAndSetStaffVO");
				mcomCon=null;
			}
		}
		
	}
	
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

}
