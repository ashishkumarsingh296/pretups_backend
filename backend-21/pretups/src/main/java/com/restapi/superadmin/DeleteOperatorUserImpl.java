package com.restapi.superadmin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.EmailSendToUser;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OperatorUserLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

@Service("DeleteOperatorUserI")
public class DeleteOperatorUserImpl implements DeleteOperatorUserI {

	public static final Log LOG = LogFactory.getLog(DeleteOperatorUserImpl.class.getName());
	public static final String classname = "DeleteOperatorUserImpl";

	@Override
	public BaseResponse deleteOperatorUser(Connection con, String loginId,DeleteOperatorRequestVO requestVO,HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "deleteOperatorUser";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		BaseResponse response = new BaseResponse();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		UserDAO userDAO = new UserDAO();
		UserVO user1 = new UserVO();
		UserVO userVO2 = new UserVO();
		Date currentDate = new Date(System.currentTimeMillis());

		try {
			UserVO userVO = new UserVO();
			user1 = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			boolean isBalanceFlag = false;
			boolean isO2CPendingFlag = false;
			boolean isSOSPendingFlag = false;
			boolean isLRPendingFlag = false;

			boolean isChildFlag = userDAO.isChildUserActive(con, requestVO.getUserId());
			if (isChildFlag) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CHILD_USER_EXIST1, 0, null);
			} else {
				isBalanceFlag = userDAO.isUserBalanceExist(con, requestVO.getUserId());
			}

			if (isBalanceFlag) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ERROR_ERP_BALANCE_EXISTS, 0,
						null);
			} else {
				boolean channelSosEnable = (boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
				if (channelSosEnable) {
					// Checking SOS Pending transactions
					ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
					isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, requestVO.getUserId());
				}
			}
			if (isSOSPendingFlag) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SOS_TRANSACTION_PENDING1, 0,
						null);
			} else {
				boolean lrEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED);
				// checking Pending Last recharge transaction
				if (lrEnabled) {
					UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
					UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
					userTrfCntVO = userTrfCntDAO.selectLastLRTxnID( requestVO.getUserId(), con, false, null);
					if (userTrfCntVO != null)
						isLRPendingFlag = true;
				}
			}
			if (isLRPendingFlag) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LR_TRANSACTION_PENDING1, 0,
						null);
			} else {
				// Checking O2C Pending transactions
				ChannelTransferDAO transferDAO = new ChannelTransferDAO();
				isO2CPendingFlag = transferDAO.isPendingTransactionExist(con,  requestVO.getUserId());
			}

			int deleteCount = 0;
			boolean isbatchFocPendingTxn = false;
			if (isO2CPendingFlag) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.O2C_TRANSACTION_PENDING1, 0,
						null);
			} else {
				// Checking Batch FOC pending transactions
				FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
				isbatchFocPendingTxn = batchTransferDAO.isPendingTransactionExist(con, requestVO.getUserId());
			}
			if (isbatchFocPendingTxn) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FOC_TRANSACTION_PENDING1, 0,
						null);
			}

			else {
				userVO.setUserID(requestVO.getUserId());
				userVO.setLastModified(Long.parseLong( requestVO.getLastModified()));
				userVO.setModifiedBy(user1.getUserID());
				userVO.setModifiedOn(currentDate);
				userVO.setStatus(PretupsI.USER_STATUS_DELETED);
				userVO.setPreviousStatus(requestVO.getStatus());
				ArrayList userList = new ArrayList();
				userList.add(userVO);
				userVO2 = userDAO.loadAllUserDetailsByLoginID(con,requestVO.getId());
				userVO.setUserName(userVO2.getUserName());
				userVO.setLoginID(userVO2.getLoginID());
				userVO.setMsisdn(userVO2.getMsisdn());
				deleteCount = userDAO.deleteSuspendUser(con, userList);
			}

			if (deleteCount <= 0) {
				con.rollback();
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DELETION_FAIL, 0, null);
			}
			
			
			con.commit();
			OperatorUserLog.log("DELOPTUSR", userVO, user1, null);
			final String arr[] = { requestVO.getUserName() };
			BTSLMessages btslMessage = null;
            btslMessage = new BTSLMessages(PretupsErrorCodesI.DEL_SUCCESS, arr, "DeleteSuccess");

            final BTSLMessages sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
            final PushMessage pushMessage = new PushMessage(userVO2.getMsisdn(), sendBtslMessage, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
                            (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), user1.getNetworkID());
            pushMessage.push();
            // Email for pin & password
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(userVO.getEmail())) {
            	final String subject =  RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), PretupsErrorCodesI.SUB_MAIL_DELETE_USER, null);
                final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, sendBtslMessage,new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), userVO.getNetworkID(),
                                "Email has ben delivered recently", userVO2, user1);
                emailSendToUser.sendMail();
            }
			userVO.setLoginID(requestVO.getId());
			userVO.setUserName(requestVO.getUserName());
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DELETION_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DELETION_SUCCESS);

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
					PretupsErrorCodesI.DELETION_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DELETION_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

}
