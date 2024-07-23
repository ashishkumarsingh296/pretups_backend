package com.client.pretups.channel.user.businesslogic;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserDeletionBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.logging.UnregisterChUsersFileProcessLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OracleUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
/**
 * ApprovalUserDeleteSuspendRestServiceImpl Implements ApprovalUserDeleteSuspendRestService and use for RestService to Approve Batch Suspend/Delete User Request
 * @author MOHD SUHEL
 * @since 15/10/2016
 */
public class ApprovalUserDeleteSuspendRestServiceImpl implements ApprovalUserDeleteSuspendRestService{
	public static final Log _log = LogFactory.getLog(ApprovalUserDeleteSuspendRestServiceImpl.class.getName());
	private static final String PSMTDELETE= "psmtDelete";
	private static final String PSMTRESUMEEXIST= "psmtResumeExist";
	private static final String PSMTUSERID= "psmtUserID";
	private static final String PSMTISEXIST= "psmtIsExist";
	private static final String ERRORGENERAL = "error.general.processing";
	/** 
	 * this method provide the list of user which are under approval state for suspend/delete request
	 * @param requestData
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @return
	 * @author mohd.suhel1
	 * @since 10/17/2016
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse< byte[]> downloadUserList(String requestData) throws BTSLBaseException{
		final String methodName = "downloadUserList";
		if (_log.isDebugEnabled())
			_log.debug(methodName, PretupsI.ENTERED+" : requestData :"+requestData);
		PretupsResponse< byte[]> response = new PretupsResponse<>();
		byte[] excelFilebytes;
		try{
			List<ChannelUserVO> userList =new ArrayList<>();
			StringBuilder strBuff = new StringBuilder();
			strBuff.append("SELECT u.user_name,c.category_name, u.status, u.msisdn, u.external_code,u.previous_status");
			strBuff.append(" FROM users u , categories c where u.category_code = c.category_code and u.status = ?");
			Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			if(!map.containsKey("userStatus")){
				_log.debug(methodName, "Missing Tag in Json Request : userStatus");
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, null);
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "tag.is.missing");
				return response;
			}
			if (_log.isDebugEnabled())
				_log.debug(methodName, "QUERY sqlSelect=" + strBuff.toString());
			loadUserList(userList , map.get("userStatus").toString(),strBuff);
			if(userList.isEmpty()){
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "no.user.approval.state");
				return response;
			}
			excelFilebytes = writeUserDataToXLS(userList);
		
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, excelFilebytes);
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "file.download.successfully");
			return response;
		}
		catch(BTSLBaseException | IOException | SQLException e){
		
			throw new BTSLBaseException(e);
			
		}
		finally{
			
			printLog(methodName, PretupsI.EXITED);
		}
		
		
	}
	/** 
	 * this method take the file and check user for approval and do the rest process for approval the suspend/delete request
	 * @param requestData
	 * @throws IOException
	 * @throws BTSLBaseException
	 * @return 
	 * @author mohd.suhel1
	 * @since 10/17/2016
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> approveDeleteSuspendUser(String requestData) throws BTSLBaseException{
		final String methodName = "approveDeleteSuspendUser";
		if (_log.isDebugEnabled())
			_log.debug(methodName, PretupsI.ENTERED+" with : requestData : "+requestData);
		try{
			PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
			Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			if(!map.containsKey("filePath") || !map.containsKey("fileName") || !map.containsKey("userID") || !map.containsKey("userNetworkID") ){
				printLog(methodName ,  " mendatory tags are  : MISSING : should Contains = filePath , fileName , userID , userNetworkID ");
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, null);
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "tag.is.missing");
				return response;
			}
			final String filePath = map.get("filePath").toString();
			final String fileName = map.get("fileName").toString();
			final String fileDirPath = filePath+"/"+fileName; // Full File Path 
			
			UserVO userVO = new UserVO();
			userVO.setUserID(map.get("userID").toString());
			userVO.setNetworkID(map.get("userNetworkID").toString());
			response = approveOrRejectRequest(userVO ,fileDirPath,fileName );
			
			return response;
		
		}catch(IOException e){
			throw new BTSLBaseException(e);
		}finally{
			
			printLog(methodName, PretupsI.EXITED);
		}
	}
	
	
	
	/**this methods use to call methods of file parsing , approving request
	 * @param pUserVO
	 * @param fileDirPath
	 * @param fileName
	 * @return
	 * @throws BTSLBaseException
	 */
	private PretupsResponse<List<ListValueVO>> approveOrRejectRequest(UserVO pUserVO, String fileDirPath , String fileName) throws BTSLBaseException {
		final String methodName = "approveOrRejectRequest";
		if (_log.isDebugEnabled()) 
			_log.debug(methodName, PretupsI.ENTERED+" : fileDirPath : "+fileDirPath+" , fileName : "+fileName);
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
		Connection con = OracleUtil.getConnection();
		List<ChannelUserVO> userList = new ArrayList<>();
		List<ListValueVO> errorFileList = new ArrayList<>();
		List<ChannelUserVO> rejectUserList = new ArrayList<>();
		HashMap<String, String> realUserList = new HashMap<>();
		String suspendOrDeleteReq;
		// File Parsing and storing all records data into List
		try{
			int totalNoRecords = ApprovalUserDeleteSuspendUtils.fileParsingForRecords( fileDirPath , fileName ,userList,rejectUserList,errorFileList);
			if(totalNoRecords==0){
				printLog(methodName, "NO Records Found in File");
				response.setStatus(true);
				response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
				response.setParameters(new String[] {});
				response.setFormError("no.action.given.for.approval.or.rejection");
				return response;
			}
			suspendOrDeleteReq = ApprovalUserDeleteSuspendUtils.getRequestType(userList, rejectUserList);
			verifyFileRecordsForApproval(realUserList , errorFileList , userList, rejectUserList ,suspendOrDeleteReq,con);
			approvalForValidRecords(userList , errorFileList , pUserVO, suspendOrDeleteReq ,rejectUserList , realUserList ,  con);
			this.rejectValidRecords(rejectUserList , errorFileList , con);
			if(!errorFileList.isEmpty()){
				response.setFormError("approve.user.suspenddelete.message.error.report");
				response.setStatus(true);
				response.setParameters(new String[] { String.valueOf(totalNoRecords-errorFileList.size()) , String.valueOf(totalNoRecords)});
				response.setDataObject(errorFileList);
				response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
				return response;
			}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,errorFileList);
			response.setParameters(new String[] { String.valueOf(totalNoRecords)});
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "approve.user.suspenddelete.message.success.report");
			return response;
		}
		catch(BTSLBaseException be){
			if (_log.isDebugEnabled()) {
				_log.debug(methodName , be);
			}
			throw new BTSLBaseException(be);
		
		}finally {
			OracleUtil.closeQuietly(con);
			printLog(methodName, PretupsI.EXITED);
		}
	}


	/**Move balance in case of delete user
	 * @param pCon
	 * @param pUserID
	 * @param userVO
	 * @param currentDate
	 * @param userDAO
	 * @throws BTSLBaseException
	 */
	private void balanceMoveForDel(Connection pCon, String pUserID, UserVO userVO, final Date currentDate, final UserDAO userDAO) throws BTSLBaseException{
		final String methodName = "balanceMoveForDel";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED+" UserID -"+pUserID);
		}
		try{
			boolean isBalanceFlag;
			isBalanceFlag = userDAO.isUserBalanceExist(pCon, pUserID);
			if (isBalanceFlag) {
				updateBalChnlTrnsfr(pCon , pUserID , currentDate,  userVO);
			}
		}
		catch(Exception e)
		{
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, e);
			}
			throw new BTSLBaseException(ERRORGENERAL);
		}finally{
			printLog(methodName, PretupsI.EXITED);
		}
		
	}
	
	
	
	
	/**Update Balance Channel Transfer in case of User Delete
	 * @param pCon
	 * @param pUserID
	 * @param currentDate
	 * @param userVO
	 * @throws Exception
	 */
	private void updateBalChnlTrnsfr( Connection pCon , String pUserID ,final Date currentDate , UserVO userVO) throws Exception {
		final String methodName = "updateBalChnlTrnsfr";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		boolean sendMsgToOwner =false;
		long totBalance = 0;
		ArrayList<UserBalancesVO> userBal ;
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		final ChannelUserVO fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(pCon, pUserID, false, currentDate,false);
		final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
		fromChannelUserVO.setGateway(PretupsI.REQUEST_SOURCE_TYPE_WEB);
		final ChannelUserVO toChannelUserVO = channelUserDAO
				.loadChannelUserDetailsForTransfer(pCon, fromChannelUserVO.getOwnerID(), false, currentDate,false);
		userBal = userBalancesDAO.loadUserBalanceForDelete(pCon, fromChannelUserVO.getUserID());// user
		Iterator<UserBalancesVO> itr ;
		itr = userBal.iterator();
		UserBalancesVO userBalancesVO ;
		while (itr.hasNext()) {
			userBalancesVO = itr.next();
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RETURN_TO_OPERATOR_STOCK)).booleanValue() || fromChannelUserVO.getOwnerID().equals(fromChannelUserVO.getUserID())) {
				UserDeletionBL.updateBalNChnlTransfersNItemsO2C(pCon, fromChannelUserVO, toChannelUserVO, PretupsI.REQUEST_SOURCE_TYPE_WEB,
						PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
			} else if(!PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(toChannelUserVO.getStatus())){
				UserDeletionBL.updateBalNChnlTransfersNItemsC2C(pCon, fromChannelUserVO, toChannelUserVO, userVO.getUserID(),
						PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
				sendMsgToOwner = true; 
				totBalance += userBalancesVO.getBalance();
			}
			else
				throw new BTSLBaseException(this, "balanceMoveForDel", "user.channeluser.deletion.parentsuspended");	
		}
		if(sendMsgToOwner) {
			ChannelUserVO prntChnlUserVO = new ChannelUserDAO().loadChannelUserByUserID(pCon, fromChannelUserVO.getParentID());
			String[] msgArr = {fromChannelUserVO.getMsisdn(),PretupsBL.getDisplayAmount(totBalance)};
			final BTSLMessages sendBtslMessageToOwner = new BTSLMessages(PretupsErrorCodesI.OWNER_USR_BALCREDIT,msgArr);
			final PushMessage pushMessageToOwner = new PushMessage(prntChnlUserVO.getParentMsisdn(), sendBtslMessageToOwner, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
					(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), fromChannelUserVO.getNetworkID());
			pushMessageToOwner.push();    
		}  
		
		printLog(methodName, PretupsI.EXITED);
		
	}
	/**
	 * THIS METHOD CHECKS THAT USER FOR DELETE REQUEST HAS CHILD OR NOT
	 * @param pCon
	 * @param pUserID
	 * @param pDeleteOrSuspend
	 * @param pModifiedBy
	 * @param pPreStatus
	 * @param pMsisdn
	 * @param pLoginID
	 * @param pCountStr
	 * @param userVO
	 * @param errorFileList 
	 * @return
	 * @throws BTSLBaseException 
	 */
	private boolean deleteRetry(Connection pCon, String pUserID, String pDeleteOrSuspend, String pModifiedBy , ChannelUserVO childUser ,  UserVO userVO, List<ListValueVO> errorFileList) throws BTSLBaseException {
		final String methodName = "deleteRetry";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED +" pModifiedBy-"+pModifiedBy);
		}
		
		boolean deleteError = true;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			deleteError = checkChildUserDetails(pCon , pUserID , childUser , pDeleteOrSuspend , userVO , pModifiedBy , errorFileList);
			return deleteError;
		} catch (BTSLBaseException be) {
			throw new BTSLBaseException(be);
		}finally{
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, PretupsI.EXITED);
			}
		}
	}
	/**this methods check User's Child user details
	 * @param pCon
	 * @param pUserID
	 * @param childUser
	 * @param pDeleteOrSuspend
	 * @param userVO
	 * @param pModifiedBy
	 * @param errorFileList
	 * @return
	 * @throws BTSLBaseException
	 */
	private boolean checkChildUserDetails(Connection pCon, String pUserID, ChannelUserVO childUser , String pDeleteOrSuspend  , UserVO userVO , String pModifiedBy, List<ListValueVO> errorFileList ) throws BTSLBaseException {
		final String methodName = "checkChildUserDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		ListValueVO errorVO ;
		final UserDAO userDAO = new UserDAO();
		ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
		boolean isChildFlag;
		final Date currentDate = new Date();
		boolean deletedError = true;
		boolean isO2CPendingFlag ;
		boolean isBatchFOCTxnPendingFlag;
		String childPreStatus = childUser.getStatus();
		String childMsisdn = childUser.getMsisdn();
		int childSeqNo = childUser.getCategoryVO().getSequenceNumber();
		String msisdnForLog = childMsisdn;
		isChildFlag = userDAO.isChildUserActive(pCon, pUserID);
		if (isChildFlag) {
			errorVO = new ListValueVO(msisdnForLog , "approve.user.suspenddelete.message.error.childexist");
			errorFileList.add(errorVO);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "This user has childs down the hierarchy, so can't be deleted " + pUserID);
			}
			UnregisterChUsersFileProcessLog.log("CHILD EXISTS", pUserID, msisdnForLog, childSeqNo, "Child exists for this user", "Fail", "");
		}
		else {
			boolean isSOSPendingFlag = false;
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())  {
		        // Checking SOS Pending transactions
		        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
		        isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(pCon, pUserID);
		     }
			if (isSOSPendingFlag){
				errorVO = new ListValueVO(msisdnForLog , "approve.user.suspenddelete.message.error.pending.sos.transaction");
				errorFileList.add(errorVO);
				LogFactory.printLog(methodName, "This user has pending SOS transactions, so can't be deleted " + pUserID, _log);
				UnregisterChUsersFileProcessLog
				.log("IS PENDING SOS TRANSACTION EXISTS", pUserID, msisdnForLog, childSeqNo, "Pending User's transaction", "Fail", "");
			}
			else{
				//cheking pending Last recharge transaction
				boolean isLRPendingFlag = false;
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue())  {
					UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
                    UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
                    userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(pUserID, pCon, false, null);
                    if (userTrfCntVO!=null) 
                    	isLRPendingFlag=true;
			     }
				if(isLRPendingFlag){
					errorVO = new ListValueVO(msisdnForLog , "approve.user.suspenddelete.message.error.pending.lr.transaction");
					errorFileList.add(errorVO);
					LogFactory.printLog(methodName, "This user has pending Last Recharge transactions, so can't be deleted " + pUserID, _log);
					UnregisterChUsersFileProcessLog
					.log("IS PENDING LR TRANSACTION EXISTS", pUserID, msisdnForLog, childSeqNo, "Pending User's transaction", "Fail", "");
				}
				else{
					// Checking O2C Pending transactions
					final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
					isO2CPendingFlag = transferDAO.isPendingTransactionExist(pCon, pUserID);
					if (isO2CPendingFlag) {
						errorVO = new ListValueVO(msisdnForLog , "approve.user.suspenddelete.message.error.pending.transaction");
						errorFileList.add(errorVO);
						if (_log.isDebugEnabled()) {
							_log.debug(methodName, "This user has pending transactions, so can't be deleted " + pUserID);
						}
						UnregisterChUsersFileProcessLog
						.log("IS PENDING TRANSACTION EXISTS", pUserID, msisdnForLog, childSeqNo, "Pending User's transaction", "Fail", "");
					} else {
						// Checking Batch FOC Pending transactions - Ved
						// 07/08/06
						final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
						isBatchFOCTxnPendingFlag = batchTransferDAO.isPendingTransactionExist(pCon, pUserID);
						if (isBatchFOCTxnPendingFlag) {
							errorVO = new ListValueVO(msisdnForLog , "approve.user.suspenddelete.message.error.pending.batch.transaction");
							errorFileList.add(errorVO);
							printLog(methodName, "This user has pending batch foc transactions, so can't be deleted " + pUserID);
							UnregisterChUsersFileProcessLog.log("IS PENDING BATCH FOC TRANSACTION EXISTS", pUserID, msisdnForLog, childSeqNo,
									"Pending User's batch foc transaction", "Fail", "");
						} else {
		
							balanceMoveForDel(pCon, pUserID, userVO, currentDate, userDAO);
		
							deletedError = channelUserWebDAO.deleteOrSuspendChnlUsers(pCon, pUserID, pDeleteOrSuspend, pModifiedBy, childPreStatus);
						}
					}
				}
			}

		}
		
		printLog(methodName, PretupsI.EXITED);
		
		return deletedError;
	}

	/**
	 * USE FOR WRITING DATA TO XLS FILE AND RETUNR BYTE ARRAY
	 * @param dataObject
	 * @return
	 * @throws BTSLBaseException
	 */
	private byte[] writeUserDataToXLS(List<ChannelUserVO> dataObject) throws BTSLBaseException
	{
		final String methodName = "writeUserDataToXLS";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED +" DataObject : "+dataObject);
		}
		byte[] excelFilebytes = null;
		try(HSSFWorkbook workbook = new HSSFWorkbook();
				ByteArrayOutputStream bos = new ByteArrayOutputStream())
				{
			String keyName ;
			HSSFSheet sheet = workbook.createSheet("sheet 1");
			sheet.protectSheet("protected");
			int rowNum = 0;
			int headerCell = 0;
			//Header Style
			HSSFCellStyle headerStyle = workbook.createCellStyle(); 
			HSSFFont font = workbook.createFont();
			font.setFontName(HSSFFont.FONT_ARIAL);
			font.setBold(true);
			headerStyle.setFont(font);
			HSSFCellStyle lockedNumericStyle = workbook.createCellStyle(); 			// Locking Style for xls file 
			lockedNumericStyle.setLocked(false);
			// Creating Header for the xls file 
			HSSFRow row = sheet.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("approve.batch.suspnd.dlt.header.username");
			HSSFCell cell =  row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("approve.batch.suspnd.dlt.header.categoryname");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("approve.batch.suspnd.dlt.header.msisdn");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("approve.batch.suspnd.dlt.header.status");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("approve.batch.suspnd.dlt.header.externalcode");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("approve.batch.suspnd.dlt.header.previousstatus");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("approve.batch.suspnd.dlt.header.action");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			for(ChannelUserVO user : dataObject)
			{
				int cellNum = 0;
				row = sheet.createRow(rowNum++);
				cell = row.createCell(cellNum++);
				cell.setCellValue((String)user.getUserName());
				sheet.autoSizeColumn(cellNum-1);
				cell = row.createCell(cellNum++);
				cell.setCellValue((String)user.getCategoryName());
				sheet.autoSizeColumn(cellNum-1);
				cell = row.createCell(cellNum++);
				cell.setCellValue((String)user.getMsisdn());
				sheet.autoSizeColumn(cellNum-1);
				cell = row.createCell(cellNum++);
				cell.setCellValue((String)user.getStatus());
				sheet.autoSizeColumn(cellNum-1);
				cell = row.createCell(cellNum++);
				cell.setCellValue((String)user.getExternalCode());
				sheet.autoSizeColumn(cellNum-1);
				cell = row.createCell(cellNum++);
				cell.setCellValue((String)user.getPreviousStatus());
				sheet.autoSizeColumn(cellNum-1);
				cell = row.createCell(cellNum++);
				cell.setCellValue("");
				sheet.autoSizeColumn(cellNum);
				cell.setCellStyle(lockedNumericStyle);
			}
			for(int column = 0; column<7; column++)
			{
				sheet.autoSizeColumn(column);
			}
			workbook.write(bos);
			excelFilebytes = bos.toByteArray();
				}
		catch (IOException ioe) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ioe);
			}
			throw new BTSLBaseException("approve.user.suspenddelete.error.file.writing");
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, e);
			}
			throw new BTSLBaseException("approve.user.suspenddelete.error.file.writing");
		}
		finally{
			printLog(methodName, PretupsI.EXITED);
		}
		return excelFilebytes;
	}

	/**
	 * CHECK EACH RECORD FROM FILE IS VALID FOR APPROVAL OR NOT
	 * @param realUserList
	 * @param errorFileList
	 * @param userList
	 * @param rejectUserList
	 * @param suspendOrDeleteReq
	 * @param con
	 * @throws BTSLBaseException
	 */
	private void verifyFileRecordsForApproval(HashMap<String, String> realUserList , List<ListValueVO> errorFileList , List<ChannelUserVO> userList, List<ChannelUserVO> rejectUserList , String suspendOrDeleteReq , Connection con) throws BTSLBaseException
	{
		final String methodName = "verifyFileRecordsForApproval";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED +" :with : requestType :"+suspendOrDeleteReq);
		}
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT u.msisdn , u.previous_status");
		strBuff.append(" FROM users u , categories c where u.category_code = c.category_code and u.status = '");
		strBuff.append(suspendOrDeleteReq);
		strBuff.append("'");
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "SELECT QUERY : "+strBuff.toString());
		}
		try(PreparedStatement prstmt = con.prepareStatement(strBuff.toString())){
			ResultSet rs = null;
			rs = prstmt.executeQuery();
			while (rs.next())
				realUserList.put(rs.getString("MSISDN"), rs.getString("PREVIOUS_STATUS"));
			// Removing Invalid User From List which is not in Approval State
			ApprovalUserDeleteSuspendUtils.processListValueVOValue(userList, realUserList,errorFileList);
			ApprovalUserDeleteSuspendUtils.processListValueVOValue(rejectUserList, realUserList,errorFileList);
		}
		catch (SQLException sqe) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, sqe);
			}
			throw new BTSLBaseException(ERRORGENERAL);
		}
		finally{
			printLog(methodName, PretupsI.EXITED);
		}
		return;
	}
	/**
	 * THIS METHOD IS TO PROCESS REQUEST FOR APPROVAL REJECTION
	 * @param rejectUserList
	 * @param errorFileList
	 * @param con
	 * @throws BTSLBaseException
	 */
	private void rejectValidRecords(List<ChannelUserVO> rejectUserList , List<ListValueVO> errorFileList ,  Connection con) throws BTSLBaseException
	{
		final String methodName = "rejectValidRecords";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED+" rejectUserList -"+rejectUserList);
		}
		ListValueVO errorVO;
		StringBuilder updateQuery = new StringBuilder();
		updateQuery.append("update users set status = ? , previous_status = ? where msisdn = ? and status = ? ");
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Update QUery: " + updateQuery.toString());
		}
		try(PreparedStatement pstm = con.prepareStatement(updateQuery.toString())){
			for(ChannelUserVO user : rejectUserList)
			{
				pstm.setString(1, user.getPreviousStatus());
				pstm.setString(2, user.getStatus());
				pstm.setString(3, user.getMsisdn());
				pstm.setString(4, user.getStatus());
				int updateCount = pstm.executeUpdate();
				if(updateCount<=0)
				{
					errorVO = new ListValueVO(user.getMsisdn(),"approve.user.suspenddelete.message.reject.failed");
					errorFileList.add(errorVO);
					printLog(methodName, "Could Not Update this User Rejection Failed ");
				}else
				{
					con.commit();
				}
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, " Update count " + updateCount + " for MSISDN : "+user.getMsisdn());
				}
			}
		}
		catch (SQLException sqe) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,sqe);
			}
			throw new BTSLBaseException(ERRORGENERAL);
		}finally{
			printLog(methodName, PretupsI.EXITED);
		}
		return;
	}
	/**
	 * THIS METHOD IS TO APPROVE ALL RECORDS GIVEN FOR APPROVAL
	 * 
	 * @param userList
	 * @param errorFileList
	 * @param userDAO
	 * @param userVO
	 * @param childExistList
	 * @param prepareStatementMap
	 * @param defaultLocale
	 * @param suspendOrDeleteReq
	 * @param rejectUserList
	 * @param realUserList
	 * @param con
	 * @throws BTSLBaseException
	 */
	private void approvalForValidRecords(List<ChannelUserVO> userList , List<ListValueVO> errorFileList ,UserVO userVO ,String suspendOrDeleteReq , List<ChannelUserVO> rejectUserList , HashMap<String, String> realUserList ,Connection con) throws BTSLBaseException
	{
		final String methodName = "approvalForValidRecords";
		printLog(methodName, PretupsI.ENTERED);
		int countRow = 0;
		final UserDAO userDAO = new UserDAO();
		List<String> deleteError = new ArrayList<>();
		List<ChannelUserVO> childExistList = new ArrayList<>();
		try{
			for(ChannelUserVO user : userList)
			{
				countRow++;
				suspendOrDeleteAllRequest(con , user , childExistList , deleteError , userVO , userDAO ,countRow);
			}
		}catch(SQLException sqe)
		{
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, sqe);
			}
			throw new BTSLBaseException(ERRORGENERAL);
		}
		try{
			if (!childExistList.isEmpty()) {
				retryforChildExist(childExistList , errorFileList, rejectUserList , realUserList, deleteError , userVO , suspendOrDeleteReq);
			}
		}catch(SQLException sqe)
		{
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, sqe);
			}
			throw new BTSLBaseException(ERRORGENERAL);
		}finally{
			printLog(methodName, PretupsI.EXITED);
		}
		return;
	}
	/**Do The Do retry to delete the user for which delete request
	 * @param childExistList
	 * @param errorFileList
	 * @param rejectUserList
	 * @param realUserList
	 * @param deleteError
	 * @param userVO
	 * @param suspendOrDeleteReq
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	private void retryforChildExist(List<ChannelUserVO> childExistList,
			List<ListValueVO> errorFileList,
			List<ChannelUserVO> rejectUserList, HashMap<String, String> realUserList, List<String> deleteError, UserVO userVO, String suspendOrDeleteReq) throws BTSLBaseException, SQLException {
		Connection con = OracleUtil.getConnection();
		final String methodName = "retryforChildExist";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		ChannelUserVO channelUserVO;
		ChannelUserVO chnlUserVO;
		String filteredMsisdn;
		
		boolean invalidStringFromDao;
		final int length = childExistList.size();
		for (int i = 0; i < length; i++) {
			for (int j = i + 1; j < length; j++) {
				if (( childExistList.get(i)).getCategoryVO().getCategorySequenceNumber() > ( childExistList.get(j)).getCategoryVO()
						.getCategorySequenceNumber()) {
					chnlUserVO = childExistList.get(i);
					childExistList.set(i, (ChannelUserVO)childExistList.get(j));
					childExistList.set(j, chnlUserVO);
				}
			}
		}
		for (int i = childExistList.size() - 1; i >= 0; i--) {
			channelUserVO =  childExistList.get(i);
			invalidStringFromDao = deleteRetry(con, channelUserVO.getUserID(),suspendOrDeleteReq, userVO.getUserID(),
					(ChannelUserVO) childExistList.get(i),userVO , errorFileList);
			filteredMsisdn=( childExistList.get(i)).getMsisdn();
			if(invalidStringFromDao)
			{    
				
				channelUserVO.setPreviousStatus(realUserList.get(channelUserVO.getMsisdn()));
				rejectUserList.add(channelUserVO);
				if(_log.isDebugEnabled())
					_log.debug(methodName,"Rollback the transaction for : "+(childExistList.get(i)).getMsisdn());
				con.rollback();
			}
			else {
				if(deleteError.contains(filteredMsisdn)){
					printLog(methodName, "Commit the transaction");
					con.commit();
				}
			}
		}
		
		printLog(methodName, PretupsI.EXITED);
		
		
	}
	/**this methods to approve Susepnd or delete Request
	 * @param con
	 * @param user
	 * @param childExistList
	 * @param deleteError
	 * @param userVO
	 * @param userDAO
	 * @param countRow
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	private void suspendOrDeleteAllRequest(Connection con, ChannelUserVO user, List<ChannelUserVO> childExistList,List<String> deleteError, UserVO userVO, UserDAO userDAO, int countRow) throws BTSLBaseException, SQLException {
		final String methodName = "suspendOrDeleteAllRequest";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		final HashMap prepareStatementMap = new HashMap();
		final Date currentDate = new Date();
		ChannelUserVO channelUserVO = null;
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		String filteredMsisdn;
		String suspendDeleteResumeReq = user.getStatus();
		final ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
		filteredMsisdn = PretupsBL.getFilteredMSISDN(user.getMsisdn());
		// On user deletion action only balance should go owner/operator
		if (PretupsI.USER_STATUS_DELETE_REQUEST.equals(suspendDeleteResumeReq)) {
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, filteredMsisdn);
			balanceMoveForDel(con, channelUserVO.getUserID(), channelUserVO, currentDate, userDAO);
		}
		try{
			if (channelUserWebDAO.deleteOrSuspendChnlUsersInBulkForMsisdn(con, filteredMsisdn, suspendDeleteResumeReq , (ArrayList)childExistList, userVO.getUserID(), countRow, prepareStatementMap)){
				deleteError.add(user.getMsisdn());
				printLog(methodName, "Rollback the transaction for : " + user.getMsisdn());
				con.rollback();
			}
			else {
				printLog(methodName, "Commit the transaction for : " + user.getMsisdn());
				con.commit();
				ApprovalUserDeleteSuspendUtils.sendBtslMessageSuccess(suspendDeleteResumeReq , filteredMsisdn , userVO);
				if (channelUserVO == null) {
					channelUserVO = new ChannelUserVO();
				}
				channelUserVO.setModifiedOn(currentDate);
				channelUserVO.setMsisdn(filteredMsisdn);
				if (suspendDeleteResumeReq.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
					channelUserVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
					ChannelUserLog.log("BLKSUSPCHNLUSR", channelUserVO, userVO, true, null);
				} else if (suspendDeleteResumeReq.equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
					channelUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
					ChannelUserLog.log("BLKDELCHNLUSR", channelUserVO, userVO, true, null);
				}
			}
			clearAllParepareStmtMap(prepareStatementMap);
		}
		finally{
			closeAllPrepareStmtMap(prepareStatementMap);
			printLog(methodName, PretupsI.EXITED);
			
		}
	}

	/**Clear all PreparedStmt Map Parameter
	 * @param prepareStatementMap
	 * @throws SQLException
	 */
	private void clearAllParepareStmtMap(HashMap prepareStatementMap) throws SQLException {
		final String methodName = "clearAllParepareStmtMap";
		if (prepareStatementMap.get(PSMTISEXIST) != null) {
			((PreparedStatement) prepareStatementMap.get(PSMTISEXIST)).clearParameters();
		}
		if (prepareStatementMap.get(PSMTUSERID) != null) {
			((PreparedStatement) prepareStatementMap.get(PSMTUSERID)).clearParameters();
		}
		if (prepareStatementMap.get(PSMTDELETE) != null) {
			((PreparedStatement) prepareStatementMap.get(PSMTDELETE)).clearParameters();
		}
		if (prepareStatementMap.get(PSMTRESUMEEXIST) != null) {
			((PreparedStatement) prepareStatementMap.get(PSMTRESUMEEXIST)).clearParameters();
		}
		
		printLog(methodName, PretupsI.EXITED);
		
	}
	/**Close all PreparedStmt in Map
	 * @param prepareStatementMap
	 */
	private void closeAllPrepareStmtMap(HashMap prepareStatementMap) {
		final String methodName = "closeAllPrepareStmtMap";
		try {
			((PreparedStatement) prepareStatementMap.get(PSMTISEXIST)).close();
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			((PreparedStatement) prepareStatementMap.get(PSMTUSERID)).close();

		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			((PreparedStatement) prepareStatementMap.get(PSMTDELETE)).close();
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			((PreparedStatement) prepareStatementMap.get(PSMTRESUMEEXIST)).close();
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			((PreparedStatement) prepareStatementMap.get("psmtChildExist")).close();
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			((PreparedStatement) prepareStatementMap.get("psmtUserBalanceExist")).close();
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			((PreparedStatement) prepareStatementMap.get("psmtChnlTrnsfrPendingTransactionExist")).close();
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			((PreparedStatement) prepareStatementMap.get("psmtfocPendingTransactionExist")).close();
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		
		printLog(methodName, PretupsI.EXITED);
		
	}
	/**Load User List those are in approval state
	 * @param userList
	 * @param userStatusType
	 * @param strBuff
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	private void loadUserList(List<ChannelUserVO> userList , String userStatusType , StringBuilder strBuff) throws BTSLBaseException, SQLException 
	{
		final String methodName = "loadUserList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		ChannelUserVO channelUserVO = new ChannelUserVO();
		try(Connection con = OracleUtil.getConnection();
				PreparedStatement prstmt = con.prepareStatement(strBuff.toString())){
			ResultSet rs = null;
			prstmt.setString(1,userStatusType ); 
			rs = prstmt.executeQuery();
			while (rs.next()) {
				channelUserVO = new ChannelUserVO();
				channelUserVO.setUserName(rs.getString("USER_NAME"));
				channelUserVO.setCategoryName(rs.getString("CATEGORY_NAME"));
				channelUserVO.setStatus(rs.getString("STATUS"));
				channelUserVO.setMsisdn(rs.getString("MSISDN"));
				channelUserVO.setPreviousStatus(rs.getString("PREVIOUS_STATUS"));
				channelUserVO.setExternalCode(rs.getString("EXTERNAL_CODE"));
				userList.add(channelUserVO);
			}
		}
		finally{
			printLog(methodName, PretupsI.EXITED);
		}
		return;
	}
	
	/**this method Print Log
	 * @param methodName
	 * @param log
	 */
	private void printLog(String methodName , String log)
	{
		if (_log.isDebugEnabled())
			_log.debug(methodName, log);
	}


}
