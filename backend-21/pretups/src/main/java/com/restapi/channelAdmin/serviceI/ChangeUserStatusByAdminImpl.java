package com.restapi.channelAdmin.serviceI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.EmailSendToUser;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.ChannelUserListImpl;
import com.restapi.channelAdmin.requestVO.BulkCUStatusChangeRequestVO;
import com.restapi.channelAdmin.responseVO.BulkCUStatusChangeResponseVO;
import com.restapi.channelAdmin.responseVO.ErrorLog;
import com.restapi.channelAdmin.service.ChangeUserStatusByAdminService;
import com.web.user.businesslogic.UserWebDAO;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

@Service("ChangeUserStatusByAdminService")
public class ChangeUserStatusByAdminImpl implements ChangeUserStatusByAdminService {

	public static final Log LOG = LogFactory.getLog(ChannelUserListImpl.class.getName());
	public static final String classname = "ChannelUserListImpl";

	@Override
	public BaseResponse changeUserStatusAdmin(Connection con, String loginId, String msisdn,
			HttpServletResponse responseSwag, String status, String remarks) throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "ChangeUserStatusByAdminImpl";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		UserWebDAO userwebDAO = null;
		int updateCount = 0;
		
		final ChannelUserVO userVO = new ChannelUserVO();
		UserDAO userDAO = new UserDAO();
		UserVO user = new UserVO();
		UserVO loggedinUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
		Date currentDate = new Date(System.currentTimeMillis());

		try {

			user = userDAO.loadUserDetailsByMsisdn(con, msisdn);
			userwebDAO = new UserWebDAO();
			ArrayList<UserEventRemarksVO> deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
			UserEventRemarksVO userRemarksVO = null;
			if (PretupsI.USER_STATUS_ACTIVE.equals(user.getStatus()) && status.equals(PretupsI.USER_STATUS_SUSPEND)) {

				int deleteCount = 0;

				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_SUSPENSION_APPROVAL))
						.booleanValue()) {
					userVO.setStatus(PretupsI.USER_STATUS_SUSPEND_REQUEST);
				} else {
					userVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
				}

				userVO.setUserName(user.getUserName());
				userVO.setUserID(user.getUserID());
				userVO.setLoginID(user.getLoginID());
				userVO.setMsisdn(user.getMsisdn());
				userVO.setLastModified(user.getLastModified());
				userVO.setModifiedBy(loggedinUserVO.getUserID());
				userVO.setModifiedOn(currentDate);
				userVO.setPreviousStatus(user.getStatus());
				final ArrayList list = new ArrayList();
				list.add(userVO);
				deleteCount = userDAO.deleteSuspendUser(con, list);
				if (deleteCount <= 0) {
					con.rollback();
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_SUSPEND_FAILED, 0,
							null);
				}

				if (deleteCount > 0) {
					int suspendRemarkCount = 0;
					deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
					userRemarksVO = new UserEventRemarksVO();
					userRemarksVO.setCreatedBy(loggedinUserVO.getUserID());
					userRemarksVO.setCreatedOn(currentDate);
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_SUSPENSION_APPROVAL))
						.booleanValue()) {
					userRemarksVO.setEventType(PretupsI.SUSPEND_REQUEST_EVENT);
					}
					else {
					userRemarksVO.setEventType(PretupsI.SUSPEND_EVENT_APPROVAL);
					}
					userRemarksVO.setMsisdn(msisdn);
					userRemarksVO.setRemarks(remarks);
					userRemarksVO.setUserID(user.getUserID());
					userRemarksVO.setUserType(user.getUserType());
					userRemarksVO.setModule(PretupsI.TRANSFER_TYPE_C2S);
					deleteSuspendRemarkList.add(userRemarksVO);
					suspendRemarkCount = userwebDAO.insertEventRemark(con, deleteSuspendRemarkList);
					if (suspendRemarkCount <= 0) {
						con.rollback();
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_SUSPEND_FAILED, 0,
								null);
					}
				}

				con.commit();
				ChannelUserLog.log("SUSPCHNLUSR", userVO, loggedinUserVO, true, null);
				response.setStatus((HttpStatus.SC_OK));
				if (userVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
					String args[] = { user.getUserName() };
					String resmsg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUSPEND_REQUEST,args);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.SUSPEND_REQUEST);
				} else if (userVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND)) {
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.USERS_SUSPENDED_SUCCESSFULLY, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.USERS_SUSPENDED_SUCCESSFULLY);
				}
			}

			else if (PretupsI.USER_STATUS_SUSPEND.equals(user.getStatus())
					&& status.equals(PretupsI.USER_STATUS_RESUME)) {
				ChannelUserVO newUserVO = new ChannelUserVO();
				final ArrayList newUserList = new ArrayList();
				newUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
				newUserVO.setPreviousStatus(user.getStatus());
				newUserVO.setUserID(user.getUserID());
				newUserVO.setLastModified(user.getLastModified());
				newUserVO.setModifiedBy(loggedinUserVO.getUserID());
				newUserVO.setModifiedOn(currentDate);
				newUserVO.setUserName(user.getUserName());
				newUserVO.setLoginID(user.getLoginID());
				newUserVO.setMsisdn(user.getMsisdn());
				newUserList.add(newUserVO);

				int resumeCount = 0;
				if (newUserList.size() > 0) {
					resumeCount = userDAO.deleteSuspendUser(con, newUserList);
					if (resumeCount <= 0) {
						con.rollback();
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_RESUME_FAILED, 0,
								null);
					}
					if (resumeCount > 0) {
						int suspendRemarkCount = 0;
						deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
						userRemarksVO = new UserEventRemarksVO();
						userRemarksVO.setCreatedBy(loggedinUserVO.getUserID());
						userRemarksVO.setCreatedOn(currentDate);
						userRemarksVO.setEventType(PretupsI.RESUME_EVENT_REMARKS);
						userRemarksVO.setMsisdn(user.getMsisdn());
						userRemarksVO.setRemarks(remarks);
						userRemarksVO.setUserID(user.getUserID());
						userRemarksVO.setUserType(user.getUserType());
						userRemarksVO.setModule(PretupsI.C2S_MODULE);
						deleteSuspendRemarkList.add(userRemarksVO);
						suspendRemarkCount = userwebDAO.insertEventRemark(con, deleteSuspendRemarkList);
						if (suspendRemarkCount <= 0) {
							con.rollback();
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_RESUME_FAILED,
									0, null);
						}
					}
				}

				con.commit();
				ChannelUserLog.log("RESCHNLUSR", newUserVO, loggedinUserVO, true, null);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_RESUMED_SUCCESSFULLY,
						null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.USERS_RESUMED_SUCCESSFULLY);

			}

			else if (PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST.equals(status)) {
				boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
				boolean channelSosEnable = (boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
				boolean isEmailServiceAllow = (boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
				int reqCuserBarApproval = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REQ_CUSER_BAR_APPROVAL))).intValue();

				ArrayList<UserEventRemarksVO> barRemarkList = null;
				boolean isO2CPendingFlag = false;
				boolean isSOSPendingFlag = false;
				boolean isLRPendingFlag = false;
				final boolean isChildFlag = userDAO.isChildUserActive(con, user.getUserID());
				if (isChildFlag) {

					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CHILD_USER_EXIST1, 0, null);
				} else {
					if (channelSosEnable) {
						// Checking SOS Pending transactions
						ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
						isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, user.getUserID());
					}
				}
				if (isSOSPendingFlag) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SOS_TRANSACTION_PENDING1, 0,
							null);

				} else {

					// Checking for pending LR transactions
					if (lrEnabled) {
						UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
						UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
						userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(user.getUserID(), con, false, null);
						if (userTrfCntVO != null)
							isLRPendingFlag = true;
					}
				}
				if (isLRPendingFlag) {

					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LR_TRANSACTION_PENDING1, 0,
							null);
				} else {

					// Checking O2C Pending transactions
					final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
					isO2CPendingFlag = transferDAO.isPendingTransactionExist(con, user.getUserID());
				}
				int barCount = 0;
				boolean isbatchFocPendingTxn = false;
				if (isO2CPendingFlag) {

					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.O2C_TRANSACTION_PENDING1, 0,
							null);
				} else {
					final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
					isbatchFocPendingTxn = batchTransferDAO.isPendingTransactionExist(con, user.getUserID());
				}
				if (isbatchFocPendingTxn) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FOC_TRANSACTION_PENDING1, 0,
							null);
				}
				userVO.setUserID(user.getUserID());

				if (reqCuserBarApproval == 0) {
					if (userwebDAO.checkBarLimit(con) <= 0) {

						throw new BTSLBaseException(classname, METHOD_NAME,
								PretupsErrorCodesI.USER_CANNOT_BE_BARRED_DELETED, 0, null);
					}

					userVO.setStatus(PretupsI.USER_STATUS_BARRED);
//					userwebDAO.editRoles(con, user.getUserID());
				} else {
					userVO.setStatus(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST);
				}
				userVO.setLastModified(user.getLastModified());
				userVO.setModifiedBy(loggedinUserVO.getUserID());
				userVO.setModifiedOn(currentDate);
				userVO.setBatchID(null);
				userVO.setNetworkID(user.getNetworkID());
				userVO.setEmail(user.getEmail());
				userVO.setLoginID(user.getLoginID());
				userVO.setMsisdn(user.getMsisdn());
				userVO.setUserName(user.getUserName());
				userVO.setPreviousStatus(user.getStatus());
//				final ArrayList list = new ArrayList();
//				list.add(userVO);
//				barCount = userDAO.deleteSuspendUser(con, list);
				barCount = userwebDAO.barForDelUser(con, userVO);
				if (barCount <= 0) {
					con.rollback();
					throw new BTSLBaseException(classname, METHOD_NAME,
							PretupsErrorCodesI.USER_CANNOT_BE_BARRED_DELETED, 0, null);
				}
				if ((barCount > 0)) {
					int barRemarkCount = 0;
					barRemarkList = new ArrayList<UserEventRemarksVO>();
					userRemarksVO = new UserEventRemarksVO();
					userRemarksVO.setCreatedBy(loggedinUserVO.getUserID());
					userRemarksVO.setCreatedOn(currentDate);
					if (reqCuserBarApproval == 0)
					{
					userRemarksVO.setEventType(PretupsI.USER_STATUS_BARRED);
					}
					else {
						userRemarksVO.setEventType(PretupsI.BARRED_REQUEST_EVENT);
					}
					userRemarksVO.setMsisdn(user.getMsisdn());
					userRemarksVO.setRemarks(remarks);
					userRemarksVO.setUserID(user.getUserID());
					userRemarksVO.setUserType(user.getUserType());
					userRemarksVO.setModule(PretupsI.C2S_MODULE);
					barRemarkList.add(userRemarksVO);
					barRemarkCount = userwebDAO.insertEventRemark(con, barRemarkList);
					if (barRemarkCount <= 0) {
						con.rollback();
						throw new BTSLBaseException(classname, METHOD_NAME,
								PretupsErrorCodesI.USER_CANNOT_BE_BARRED_DELETED, 0, null);
					}
				}
				con.commit();
				userVO.setLoginID(user.getLoginID());
				userVO.setUserName(user.getUserName());
				userVO.setMsisdn(user.getMsisdn());
				ChannelUserLog.log("BARDELUSR", userVO, loggedinUserVO, true, null);
				response.setStatus((HttpStatus.SC_OK));
				if (userVO.getStatus().equals(PretupsI.USER_STATUS_BARRED)) {
					UserPhoneVO userPhoneVO = userDAO.loadUserPhoneVO(con, userVO.getUserID());
					if (userPhoneVO != null) {
						locale = new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry());
					} else {
						locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
					}
					final BTSLMessages sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
					final PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), sendBtslMessage, "", "", locale,
							userVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_CHNL_BARRED);
					try {
						pushMessage.push();
					} catch (Exception e) {
						LOG.errorTrace(METHOD_NAME, e);
					}
					// Email for pin & password
					if (isEmailServiceAllow && !BTSLUtil.isNullString(userVO.getEmail())) {
						final String subject = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.CHNL_USER_DEREGISTER, null);
						final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, sendBtslMessage, locale,
								userVO.getNetworkID(), "Email has ben delivered recently", userVO, loggedinUserVO);
						emailSendToUser.sendMail();
					}
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BARRED_DELETE, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.BARRED_DELETE_REQUEST);
				} else if (userVO.getStatus().equals(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) {
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BARRED_DELETE_REQUEST,
							null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.BARRED_DELETE_REQUEST);
				}

			}

			else {
				userVO.setMsisdn(msisdn);
				userVO.setLoginID(user.getLoginID());
				userVO.setUserName(user.getUserName());
				userVO.setStatus(status);
				userVO.setUserID(user.getUserID());
				userVO.setLastModified(user.getLastModified());
				userVO.setModifiedBy(loggedinUserVO.getUserID());
				userVO.setModifiedOn(currentDate);
				userVO.setPreviousStatus(user.getStatus());
				final ArrayList list = new ArrayList();
				list.add(userVO);
				final StringBuilder str = new StringBuilder(remarks);
				str.setLength(99);
				userVO.setRemarks(str.toString());
				int barRemarkCount = 0;
				ArrayList<UserEventRemarksVO> barRemarkList1 = new ArrayList<UserEventRemarksVO>();
				userRemarksVO = new UserEventRemarksVO();
				userRemarksVO.setCreatedBy(loggedinUserVO.getUserID());
				userRemarksVO.setCreatedOn(currentDate);
				userRemarksVO.setEventType(PretupsI.CHANGE_STATUS);
				userRemarksVO.setMsisdn(user.getMsisdn());
				userRemarksVO.setRemarks(remarks);
				userRemarksVO.setUserID(user.getUserID());
				userRemarksVO.setUserType(user.getUserType());
				userRemarksVO.setModule(PretupsI.C2S_MODULE);
				barRemarkList1.add(userRemarksVO);
				barRemarkCount = userwebDAO.insertEventRemark(con, barRemarkList1);
				if (barRemarkCount <= 0) {
					con.rollback();
					throw new BTSLBaseException(classname, METHOD_NAME,
							PretupsErrorCodesI.USER_CANNOT_BE_BARRED_DELETED, 0, null);
				}
			
				updateCount = userDAO.deleteSuspendUser(con, list);
				if (updateCount > 0) {
					con.commit();
					ChannelUserLog.log("CHANGESTAT", userVO, loggedinUserVO, true, null);
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHANGE_USER_SUCCESS,
							null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.CHANGE_USER_SUCCESS);
				} else {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CANNOT_CHANGE_USER_STATUS, 0,
							null);
				}
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.CANNOT_CHANGE_USER_STATUS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CANNOT_CHANGE_USER_STATUS);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}
public void channelUserBulkStatusChangeImpl(BulkCUStatusChangeRequestVO requestVO,String sessionMsisdn,BulkCUStatusChangeResponseVO responseVO,HttpServletResponse response1) 
			throws BTSLBaseException, IOException, BiffException, SQLException{
		final String methodName="channelUserBulkStatusChangeImpl";
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered:=" + methodName);
		}
        if( BTSLUtil.isNullorEmpty(requestVO.getFile())) {
        	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"File"}, null);
        }
        if(BTSLUtil.isNullorEmpty(requestVO.getFileName())) {
        	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"FileName"}, null);
        }
        if(BTSLUtil.isNullorEmpty(requestVO.getFileType())) {
        	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"FileType"}, null);
        }
        if(!requestVO.getFileType().equalsIgnoreCase(PretupsI.FILE_CONTENT_TYPE_XLS))
        {
        	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"FileType"}, null);
        }
       
		Workbook workbook = null;
	    Sheet excelsheet = null;
	    String strArr[][] = null;
	    String indexStr = null;
        Cell cell = null;
        String content = null;
        String key = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        byte[] base64Bytes=null;
    	String file=requestVO.getFile();
		String fileName=requestVO.getFileName();
		String fileType=requestVO.getFileType();
		ReadGenericFileUtil util=new ReadGenericFileUtil();
		String fileStr = Constants.getProperty("UploadBatchChangeStatusFilePath");
		String fullpathAndFileName=fileStr+fileName+"."+fileType;
	final String contentType = (PretupsI.FILE_CONTENT_TYPE_XLS);
	HashMap<String, String> fileDetailsMap = null;
	fileDetailsMap = new HashMap<String, String>();
	ReadGenericFileUtil fileUtil = null;
	boolean isFileUploaded = false;
	fileUtil = new ReadGenericFileUtil();
	fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
	fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFile());
	fileDetailsMap.put(PretupsI.FILE_TYPE, contentType);
	final byte[] data = fileUtil.decodeFile(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT));
	String fileSize = Constants.getProperty("OTHER_FILE_SIZE");
	isFileUploaded = BTSLUtil.uploadCsvFileToServerWithHashMapForXLS(fileDetailsMap, fileStr,contentType, "bulkChangeStatus", data, Long.parseLong(fileSize));
	    base64Bytes=util.decodeFile(file);
		LOG.debug("filepathtemp:", fullpathAndFileName);
		LOG.debug("base64Bytes:", base64Bytes);
		util.writeByteArrayToFile2New(fullpathAndFileName, base64Bytes);
		try {
		workbook = Workbook.getWorkbook(new File(fullpathAndFileName));
		}catch (Exception e) {
			 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_CONTENT, "");
		}
		 List<UserVO> requiredList = null;
		 excelsheet = workbook.getSheet(0);
         final int noOfRows = excelsheet.getRows();
         final int noOfcols = excelsheet.getColumns();
         strArr = new String[noOfRows][noOfcols];
         final int[] indexMapArray = new int[noOfcols];
         List<LookupsVO> lookUpCodeList = new ArrayList();
         lookUpCodeList = LookupsCache.getLookupList(PretupsI.ALLOWED_USER_STATUS);
         mcomCon = new MComConnection();
         con=mcomCon.getConnection();
         requiredList = new ArrayList();
         List<ErrorLog> errorlogs=new ArrayList<ErrorLog>();
         UserDAO userDao=new UserDAO();
         final UserVO userSessionVO = userDao.loadUsersDetails(con,sessionMsisdn);
         int i = 0;
         i =7;
         if (noOfcols != 3) {
        	 util.deleteUploadedFile(fullpathAndFileName);
             throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_CONTENT, "");
         }
         if (noOfRows == i) {
        	 util.deleteUploadedFile(fullpathAndFileName);
        	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE, "");
         }

         for (int col = 0; col < noOfcols; col++) {
             indexStr = null;
             key = String.valueOf(col);
             indexStr = String.valueOf(col);
             indexMapArray[col] = Integer.parseInt(indexStr);
             strArr[0][indexMapArray[col]] = key;

         }
         for (int row = i; row < noOfRows; row++) {
             for (int col = 0; col < noOfcols; col++) {
                 cell = excelsheet.getCell(col, row);
                 content = cell.getContents();
                 strArr[row][indexMapArray[col]] = content;
             }
         }
        

         LookupsVO lvo = null;
         lvo = lookUpCodeList.get(0);
         // For storing the lookup codes present in different VOs in a single

         final StringBuffer code = new StringBuffer(lvo.getLookupCode());
         int lookUpListSize = lookUpCodeList.size();
         for (int j = 1; j < lookUpListSize; j++) {
             lvo = lookUpCodeList.get(j);
             code.append(",");
             code.append(lvo.getLookupCode());
         }
         if (LOG.isDebugEnabled()) {
             LOG.debug(methodName, code);
         }
         final String codeList = code.toString();
         errorlogs=this.validateFileContent(con, i, strArr, codeList, requiredList,userSessionVO.getUserID());
       
         if (errorlogs.size()!=0&&requiredList.size()==0) {
        	 responseVO.setErrorLogs(errorlogs);
             util.deleteUploadedFile(fullpathAndFileName);
             this.writeFileForResponse(responseVO,errorlogs);
             String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_CHANGE_STATUS_FAIL , null);
             responseVO.setMessage(msg);
      	     responseVO.setMessageCode(PretupsErrorCodesI.BULK_CHANGE_STATUS_FAIL);
      	     responseVO.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
      	     response1.setStatus(HttpStatus.SC_BAD_REQUEST);
      	     return;
         }
         if(requiredList.size()>0&&errorlogs.size()>0) {
        	 responseVO.setErrorLogs(errorlogs);
             this.writeFileForResponse(responseVO,errorlogs);
             userDao=new UserDAO();
     		 int updateCount=userDao.changeUserStatusForBatchAll(con,requiredList);
     		 if(updateCount==requiredList.size()) {
          	   con.commit();
          	   String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_CHANGE_STATUS_PARTIAL_SUCCESS , null);
          	   responseVO.setMessage(msg);
          	   responseVO.setMessageCode(PretupsErrorCodesI.BULK_CHANGE_STATUS_PARTIAL_SUCCESS);
          	   responseVO.setNumberOfRecords(updateCount);
          	   responseVO.setStatus(String.valueOf(HttpStatus.SC_OK));
          	   response1.setStatus(HttpStatus.SC_OK);
          	   this.sendSms(con, requiredList);
             }else {
          	   con.rollback();
          	   util.deleteUploadedFile(fullpathAndFileName);
          	   throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CANNOT_BE_PROCESSED, PretupsI.RESPONSE_FAIL,null, null);
             } 
         }
         if(requiredList.size()>0&&errorlogs.size()==0) { 
           userDao=new UserDAO();
   		   int updateCount=userDao.changeUserStatusForBatchAll(con,requiredList);
           if(updateCount==requiredList.size()) {
        	   con.commit();
        	   String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_CHANGE_STATUS_SUCCESS , null);
        	   responseVO.setMessage(msg);
        	   responseVO.setMessageCode(PretupsErrorCodesI.BULK_CHANGE_STATUS_SUCCESS);
        	   responseVO.setNumberOfRecords(updateCount);
        	   responseVO.setStatus(String.valueOf(HttpStatus.SC_OK));
        	   response1.setStatus(HttpStatus.SC_OK);
        	   this.sendSms(con, requiredList);
           }else {
        	   con.rollback();
          	   util.deleteUploadedFile(fullpathAndFileName);
          	   throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CANNOT_BE_PROCESSED, PretupsI.RESPONSE_FAIL,null, null);
           }
         }
       
         if (LOG.isDebugEnabled()) {
 			LOG.debug(methodName, "Exit:=" + methodName);
 		}
		return;
	}
	
	
	private List<ErrorLog> validateFileContent(Connection con, int i,String[][] strArr,
		String codeList ,List requiredList,String sessionUserId)throws BTSLBaseException{
	    final String methodName="validateFileContent";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered:=" + methodName);
		}
		final String status = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ADMINISTRBLY_USER_STATUS_CHANG));
        final String[] stat = status.split(",");
		ChannelUserVO chVO = null;
		UserVO userVO = null;
		int strArrLength = strArr.length;
		List<ErrorLog> errorlogs=new ArrayList();
		 Date currentDate = new Date(System.currentTimeMillis());
        
		for (int j = i; j < strArrLength; j++) {
			boolean isInvalid=false;
            String msisdn=strArr[j][0].trim();
            String reqStatus=strArr[j][1].trim();
            String remarks=strArr[j][2].trim();
            String lineNo="Line No "+String.valueOf(j + 1);
            
            if ("".equals(msisdn)) {
                ErrorLog elog=new ErrorLog(lineNo, msisdn, "Mobile No can't be empty");
                errorlogs.add(elog);
                isInvalid=true;
               
            }
            int count=0;
            for(int k = i; k < strArrLength; k++) {
            	if(msisdn.equals(strArr[k][0].trim())){
            		count++;
            	}
            }
            if (count>1) {
                ErrorLog elog=new ErrorLog(lineNo, msisdn, "Mobile No is duplicate decleared "+count+" times");
                errorlogs.add(elog);
                isInvalid=true;
               
            }
            
            
            if("".equals(reqStatus)) {
            	ErrorLog elog=new ErrorLog(lineNo, msisdn, "Status can't be empty");
                errorlogs.add(elog);
                isInvalid=true;
            }
            if("".equals(remarks)) {
	             ErrorLog elog=new ErrorLog(lineNo, msisdn, "Remarks can't be empty");
	             errorlogs.add(elog);
	             isInvalid=true;
            }
            if(remarks.length()>100) {
            	 ErrorLog elog=new ErrorLog(lineNo, msisdn, "Maximum length for remarks is 100. Actual length is "+remarks.length());
	             errorlogs.add(elog);
	             isInvalid=true;
            }
           if (!codeList.contains(reqStatus)) {
           	    ErrorLog elog=new ErrorLog(lineNo, msisdn, "Invalid change status "+reqStatus);
                errorlogs.add(elog);
                isInvalid=true;
            }
            String filteredMsisdn = null;
            filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
            	 ErrorLog elog=new ErrorLog(lineNo, msisdn, "Invalid Mobile No");
                 errorlogs.add(elog);
                 isInvalid=true;
                 continue;
            }
            if(isInvalid) {
	           	continue;
	        }
            else if(!isInvalid){
	            final UserDAO userDao = new UserDAO();
	            chVO = userDao.loadUserDetailsByMsisdn(con, msisdn);
	
	            if (chVO == null) {
	            	 ErrorLog elog=new ErrorLog(lineNo, msisdn, "Mobile No not existed");
	                 errorlogs.add(elog);
	                continue;
	            }else {
	            	 final String check = chVO.getStatus() + ":" + reqStatus;
	                 if (Arrays.asList(stat).contains(check)) {
	                     userVO = new UserVO();
	                     userVO.setMsisdn(msisdn);
	                     userVO.setStatus(reqStatus);
	                     userVO.setPreviousStatus(chVO.getStatus());
	                     userVO.setModifiedBy(sessionUserId);
	                     userVO.setModifiedOn(currentDate);
	                     final StringBuilder str = new StringBuilder(remarks);
	                     str.setLength(99);
	                     userVO.setRemarks(str.toString());
	                     requiredList.add(userVO);
	                 } else {
	                 	ErrorLog elog=new ErrorLog(lineNo, msisdn, "Change status from "+chVO.getStatus()+" to "+reqStatus+" is not allowed");
	                     errorlogs.add(elog);
	                     continue;
	                 }
	            }
            }

           

        }
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exit :=" + methodName);
		}
		return errorlogs;
	}
	private void writeFileForResponse(BulkCUStatusChangeResponseVO response, List<ErrorLog> errorlogs)throws BTSLBaseException, IOException{
		  final String methodName="writeFileForResponse";
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Entered:=" + methodName);
			}	
		if(errorlogs == null || errorlogs.size() == 0 ) {
	    		return ;
	    	}
	    	List<List<String>> rows = new ArrayList<>();
			for(ErrorLog elog:errorlogs)
			{
				List<String> listlog=new ArrayList<String>();
				listlog.add(elog.getLineNo());
				listlog.add(elog.getLabelName());
				listlog.add(elog.getReason());
				rows.add(listlog);		
				
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
	   		if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exit:=" + methodName);
			}
	    }	 
    private void writeFileCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
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
    private void sendSms(Connection con,List<UserVO> list) {
    	
    	final String methodName="sendSms";
    	UserDAO userDAO=new UserDAO();
    	String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
    	for(UserVO vo:list) {
    		try {
	    		ChannelUserVO channelUser = userDAO.loadUserDetailsCompletelyByMsisdn(con,vo.getMsisdn());
	    		String arr[] = {channelUser.getUserName(),channelUser.getStatusDesc()};
	    		BTSLMessages sendBtslMessage=null;
	    		sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.MSG_SUCCESSFUL_OF_OPERACTION,arr);
	            Locale locale =null;
	            if(channelUser.getUserPhoneVO()!=null){
	            	locale = new Locale(channelUser.getUserPhoneVO().getPhoneLanguage(),channelUser.getUserPhoneVO().getCountry());
	            } else {
	            	locale = new Locale(defaultLanguage,defaultCountry);
	            }
				try {
	                PushMessage pushMessage=new PushMessage(channelUser.getMsisdn(),sendBtslMessage,"","", locale,channelUser.getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_SUSPEND_RESUME);
					pushMessage.push();
				}catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
			}catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
    	}
    	
    	return;
    }
}