package com.btsl.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserOtpVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


public class UserOtpDAO {
	private static OperatorUtilI utilClass = null;
	private static Log _log = LogFactory.getLog(UserOtpDAO.class.getName());
	static {
		String utilClassName = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			utilClass = (OperatorUtilI) Class.forName(utilClassName).newInstance();
		} catch (Exception e) {
			_log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	public int generateSendOTP(java.sql.Connection p_con, ChannelUserVO p_userVO, String p_serviceType)
			throws SQLException, Exception {
		if (_log.isDebugEnabled()) {
			StringBuffer msg = new StringBuffer("");
			msg.append(" Entered with user_id=");
			msg.append(p_userVO.getUserID());
			msg.append(" MSISDN=");
			msg.append(p_userVO.getMsisdn());
			msg.append("p_serviceType:");
			msg.append(p_serviceType);

			String message = msg.toString();
			_log.debug("generateOTP() ::", message);
		}

		final String METHOD_NAME = "generateSendOTP";
		PreparedStatement pstmtS = null;
		PreparedStatement pstmtS1 = null;
		PreparedStatement pstmtS2 = null;
		int count = 0;
		ResultSet rs = null;
		String otp = null;
		String decryptedOtp = null;
		try {
			decryptedOtp = utilClass.generateOTP();
			otp = BTSLUtil.encryptText(decryptedOtp);
			// otp="456456";
			StringBuilder sqlSelect = new StringBuilder(
					"Select count(1) count from user_otp where user_id=? and msisdn=? and status in(?,?) ");
			if (!BTSLUtil.isNullString(p_serviceType))
				sqlSelect.append(" and  service_types = ? ");

			String selectOTP = sqlSelect.toString();
			if (_log.isDebugEnabled())
				_log.info("generateSendOTP ::", " Query selectOTP : " + selectOTP);
			pstmtS = p_con.prepareStatement(selectOTP);
			// pstmtU.setDate(1,BTSLUtil.getSQLDateFromUtilDate(p_userVO.getLastLoginOn()));
			pstmtS.setString(1, p_userVO.getUserID());
			pstmtS.setString(2, p_userVO.getMsisdn());
			pstmtS.setString(3, TypesI.YES);
			pstmtS.setString(4, TypesI.NO);
			if (!BTSLUtil.isNullString(p_serviceType))
				pstmtS.setString(5, p_serviceType);
			rs = pstmtS.executeQuery();

			if (rs.next() && rs.getInt("count") > 0) {
				count = 1;
			}
			if (count > 0) {
				int i = 1;
				StringBuilder sqlUpdate = new StringBuilder(
						"update user_otp set otp_pin=? , generated_on=? ,status=?,modified_by=? ,modified_on=? where user_id=? and msisdn=?");

				if (!BTSLUtil.isNullString(p_serviceType))
					sqlUpdate.append(" and service_types = ? ");
				String updateOTP = sqlUpdate.toString();
				if (_log.isDebugEnabled())
					_log.debug("generateOTP :: ", " Query updateOTP :" + updateOTP);
				pstmtS1 = p_con.prepareStatement(updateOTP);
				pstmtS1.setString(i++, otp);
				pstmtS1.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				pstmtS1.setString(i++, TypesI.YES);
				pstmtS1.setString(i++, p_userVO.getUserID());
				pstmtS1.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				pstmtS1.setString(i++, p_userVO.getUserID());
				pstmtS1.setString(i++, p_userVO.getMsisdn());
				if (!BTSLUtil.isNullString(p_serviceType))
					pstmtS1.setString(i++, p_serviceType);
				int updateCount = pstmtS1.executeUpdate();
				if (updateCount > 0) {
					count = updateCount;
					p_con.commit();
					p_userVO.setOTP(decryptedOtp);
					p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					sendOTP(p_con, p_userVO, p_serviceType);
				}
			} else {
				int i = 1;
				StringBuilder sqlInsert = new StringBuilder(
						"insert into user_otp (USER_ID,MSISDN,OTP_PIN,STATUS,GENERATED_ON,CREATED_BY,CREATED_ON ");
				if (!BTSLUtil.isNullString(p_serviceType))
					sqlInsert.append(",service_types");
				sqlInsert.append(")values (?,?,?,?,?,?,?");

				if (!BTSLUtil.isNullString(p_serviceType))
					sqlInsert.append(",?");

				sqlInsert.append(")");
				String insertOTP = sqlInsert.toString();
				if (_log.isDebugEnabled())
					_log.debug("generateSendOTP :: ", " Query insertOTP :" + insertOTP);
				pstmtS2 = p_con.prepareStatement(insertOTP);
				pstmtS2.setString(i++, p_userVO.getUserID());
				pstmtS2.setString(i++, p_userVO.getMsisdn());
				pstmtS2.setString(i++, otp);
				pstmtS2.setString(i++, TypesI.YES);
				pstmtS2.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				pstmtS2.setString(i++, p_userVO.getUserID());
				pstmtS2.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				if (!BTSLUtil.isNullString(p_serviceType))
					pstmtS2.setString(i++, p_serviceType);
				int insertCount = pstmtS2.executeUpdate();
				if (insertCount > 0) {
					count = insertCount;
					p_con.commit();
					p_userVO.setOTP(decryptedOtp);
					p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					sendOTP(p_con, p_userVO, p_serviceType);
				}
			}

			if (_log.isDebugEnabled())
				_log.debug("generateSendOTP() ::", " update user_otp for user id=" + p_userVO.getUserID());
		} catch (SQLException sqe) {
			_log.error("generateOTP() ::", " Exception : " + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} catch (Exception ex) {
			_log.error("updateUserLoginDetails() ::", " Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing Rssultset" + ex);
			}
			try {
				if (pstmtS != null)
					pstmtS.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
			try {
				if (pstmtS1 != null)
					pstmtS1.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
			try {
				if (pstmtS2 != null)
					pstmtS2.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
		}
		if (_log.isDebugEnabled())
			_log.debug("generateSendOTP() ::", " Exiting count=" + count);
		return count;
	}

	private void sendOTP(Connection p_con, ChannelUserVO p_channelUserVO, String p_serviceType)
			throws BTSLBaseException, Exception {
		if (_log.isDebugEnabled())
			_log.debug("sendOTP", " Entered with ChannelUserVO:" + p_channelUserVO.toString());
		final String METHOD_NAME = "sendOTP";
		MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
		try {

			Locale locale = null;

			if (p_channelUserVO.getLanguage() == null) {
				String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
				p_channelUserVO.setLanguage(language);
			}
			if (p_channelUserVO.getCountry() == null) {
				String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
				p_channelUserVO.setCountry(country);
			}

			if (p_channelUserVO != null)
				locale = new Locale(p_channelUserVO.getLanguage(), p_channelUserVO.getCountry());
			else
				throw new BTSLBaseException(this, "sendOTP", PretupsErrorCodesI.CCE_ERROR_USER_DETAIL_NOT_FOUND);

			if (!PretupsI.CHANNEL_USER_TRANSFER.equals(p_serviceType)) {
				String[] messageArgArray = { p_channelUserVO.getOTP() };

				String btslMessage = null;
				if (!BTSLUtil.isNullString(p_serviceType))
					btslMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.OTP_MESSAGE + "_" + p_serviceType,
							messageArgArray);
				else
					btslMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.OTP_MESSAGE, messageArgArray);

				if (p_channelUserVO.getStaffUserDetails() != null) {
					if (BTSLUtil.isNullString(p_channelUserVO.getStaffUserDetails().getMsisdn())) {
						PushMessage pushParentMessages = (new PushMessage(p_channelUserVO.getMsisdn(), btslMessage,
								null, messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale));
						pushParentMessages.push();
					} else {
						PushMessage pushParentMessages = (new PushMessage(
								p_channelUserVO.getStaffUserDetails().getMsisdn(), btslMessage, null,
								messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale));
						pushParentMessages.push();
					}
				} else {
					PushMessage pushParentMessages = (new PushMessage(p_channelUserVO.getMsisdn(), btslMessage, null,
							messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale));
					pushParentMessages.push();
				}

				/* send mail start */
				if ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TWO_FA_REQ)) {
					final String to = p_channelUserVO.getEmail();
					final String from = Constants.getProperty("mail_from_admin");
					final String subject = Constants.getProperty("LOGIN_OTP_SUBJECT");
					final String message = "Dear " + p_channelUserVO.getUserName() + ","
							+ Constants.getProperty("LOGIN_OTP_MESSAGE") + "    " + (String) p_channelUserVO.getOTP();
					EMailSender.sendMail(to, from, "", "", subject, message, false, "", "");
				}
			}

			/* send mail ends */

		} catch (BTSLBaseException be) {
			_log.error("sendOTP", "BTSLBaseException " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("sendOTP", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[sendOTP]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("UserOtpDAO", "sendPassword", PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
		if (_log.isDebugEnabled())
			_log.debug("sendOTP", " Exit:");
	}

	public String getOTPStatus(java.sql.Connection p_con, ChannelUserVO p_userVO) throws SQLException, Exception {
		if (_log.isDebugEnabled())
			_log.debug("getOTPStatus ::",
					" Entered..............with Userid" + p_userVO.getUserID() + "===MSISDN" + p_userVO.getMsisdn());
		final String METHOD_NAME = "getOTPStatus";
		PreparedStatement pstmtS = null;
		ResultSet rs = null;
		String status = null;
		try {

			String selectOTPStatus = "Select status from user_otp where user_id=? and msisdn=? ";
			if (_log.isDebugEnabled())
				_log.info("getOTPStatus ::", " Query selectOTPStatus : " + selectOTPStatus);
			pstmtS = p_con.prepareStatement(selectOTPStatus);
			pstmtS.setString(1, p_userVO.getUserID());
			pstmtS.setString(2, p_userVO.getMsisdn());
			rs = pstmtS.executeQuery();
			if (rs.next()) {
				status = rs.getString("status");
			}

		} catch (SQLException sqe) {
			_log.error("generateOTP() ::", " Exception : " + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[getOTPStatus]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "getOTPStatus", "error.general.processing");
		} catch (Exception ex) {
			_log.error("getOTPStatus() ::", " Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[getOTPStatus]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "getOTPStatus", "error.general.processing");
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("getOTPStatus ::", " Exception : in closing Rssultset" + ex);
			}
			try {
				if (pstmtS != null)
					pstmtS.close();
			} catch (Exception ex) {
				_log.error("getOTPStatus ::", " Exception : in closing preparedstatement for Update" + ex);
			}
		}
		if (_log.isDebugEnabled())
			_log.debug("getOTPStatus() ::", " Exiting with status=" + status);
		return status;
	}

	public int updateOTPStatus(java.sql.Connection p_con, ChannelUserVO p_userVO) throws SQLException, Exception {
		if (_log.isDebugEnabled())
			_log.debug("updateOTPStatus ::", " Entered..............");
		final String METHOD_NAME = "updateOTPStatus";
		PreparedStatement pstmtS = null;
		int count = 0;
		int i = 1;
		try {
			String updateOTPStatus = "update user_otp set status=? ,consumed_on=? where user_id=? and msisdn=?";
			if (_log.isDebugEnabled())
				_log.info("updateOTPStatus ::", " Query updateOTPStatus : " + updateOTPStatus);
			pstmtS = p_con.prepareStatement(updateOTPStatus);
			pstmtS.setString(i++, TypesI.NO);
			pstmtS.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
			pstmtS.setString(i++, p_userVO.getUserID());
			pstmtS.setString(i++, p_userVO.getMsisdn());
			count = pstmtS.executeUpdate();
		} catch (SQLException sqe) {
			_log.error("updateOTPStatus() ::", " Exception : " + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[updateOTPStatus]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "updateOTPStatus", "error.general.processing");
		} catch (Exception ex) {
			_log.error("updateUserLoginDetails() ::", " Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[updateOTPStatus]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "updateOTPStatus", "error.general.processing");
		} finally {
			try {
				if (pstmtS != null)
					pstmtS.close();
			} catch (Exception ex) {
				_log.error("updateOTPStatus ::", " Exception : in closing preparedstatement for Update" + ex);
			}
		}
		if (_log.isDebugEnabled())
			_log.debug("updateOTPStatus() ::", " Exiting count=" + count);
		return count;
	}

	public void generateAndReSendOTP(java.sql.Connection p_con, ChannelUserVO p_userVO, String _serviceType)
			throws SQLException, Exception {
		final String METHOD_NAME = "generateReSendOTP";
		if (_log.isDebugEnabled()) {
			StringBuffer msg = new StringBuffer("");
			msg.append(" Entered with user_id=");
			msg.append(p_userVO.getUserID());
			msg.append(" MSISDN=");
			msg.append(p_userVO.getMsisdn());
			msg.append("_serviceType");
			msg.append(_serviceType);

			String message = msg.toString();
			_log.debug(METHOD_NAME, message);
		}

		PreparedStatement pstmtS = null;
		int updateCount = 0;
		String otp = null;
		try {
			otp = BTSLUtil.encryptText(utilClass.generateOTP());

			StringBuilder sqlUpdate = new StringBuilder(
					"update user_otp set otp_pin=? ,status=?,modified_by=? ,modified_on=? where user_id=? and msisdn=? ");
			if (!BTSLUtil.isNullString(_serviceType))
				sqlUpdate.append("and service_types = ?");
			String updateOTP = sqlUpdate.toString();
			if (_log.isDebugEnabled())
				_log.debug("generateOTP :: ", " Query updateOTP :" + updateOTP);
			int i = 1;
			pstmtS = p_con.prepareStatement(updateOTP);
			pstmtS.setString(i++, otp);
			pstmtS.setString(i++, TypesI.YES);
			pstmtS.setString(i++, p_userVO.getUserID());
			pstmtS.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
			pstmtS.setString(i++, p_userVO.getUserID());
			pstmtS.setString(i++, p_userVO.getMsisdn());
			if (!BTSLUtil.isNullString(_serviceType))
				pstmtS.setString(i++, _serviceType);

			updateCount = pstmtS.executeUpdate();
			if (updateCount > 0) {
				p_con.commit();
				p_userVO.setOTP(BTSLUtil.decryptText(otp));

				sendOTP(p_con, p_userVO, _serviceType);
			}
		} catch (SQLException sqe) {
			_log.error("generateOTP() ::", " Exception : " + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} catch (Exception ex) {
			_log.error("updateUserLoginDetails() ::", " Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} finally {
			try {
				if (pstmtS != null)
					pstmtS.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
		}
		if (_log.isDebugEnabled())
			_log.debug("generateSendOTP() ::", " Exiting count=" + updateCount);

	}

	/**
	 * added by Sparsh Karla, Md Sohail
	 * @param p_con
	 * @param p_userVO
	 * @param p_serviceType
	 * @return resendDuration || count
	 *  @throws BTSLBaseException
	 **/
	public int generateSendOTPForForgotPin(java.sql.Connection p_con, ChannelUserVO p_userVO, String p_serviceType,  int validity, int duration, int times)
			throws SQLException, Exception {
		if (_log.isDebugEnabled()) {
			StringBuffer msg = new StringBuffer("");
			msg.append(" Entered with user_id=");
			msg.append(p_userVO.getUserID());
			msg.append(" MSISDN=");
			msg.append(p_userVO.getMsisdn());
			msg.append("p_serviceType:");
			msg.append(p_serviceType);

			String message = msg.toString();
			_log.debug("generateOTP() ::", message);
		}

		final String METHOD_NAME = "generateSendOTPForForgotPin";
		PreparedStatement pstmtS = null;
		PreparedStatement pstmtS1 = null;
		PreparedStatement pstmtS2 = null;
		PreparedStatement pstmtS3 = null;
		int count = 0;
		ResultSet rs = null;
		String otp = null;
		try {
			Date currentDate = new Date();
			otp = BTSLUtil.encryptText(utilClass.generateOTP());
			
			long otpResendDurationInPreferenceInMs = duration* 1000;   //duration in seconds
			
			int otpResendCount = 0;
			// otp="456456";
			StringBuilder sqlSelect = new StringBuilder(
					"Select generated_on,otp_count from user_otp where user_id=? and msisdn=? and status in(?,?) ");

			String selectOTP = sqlSelect.toString();
			if (_log.isDebugEnabled())
				_log.info("generateSendOTP ::", " Query selectOTP : " + selectOTP);
			pstmtS = p_con.prepareStatement(selectOTP);
			pstmtS.setString(1, p_userVO.getUserID());
			pstmtS.setString(2, p_userVO.getMsisdn());
			pstmtS.setString(3, TypesI.YES);
			pstmtS.setString(4, TypesI.NO);

			rs = pstmtS.executeQuery();
			Date generatedDate = null;

			if (rs.next()) {
				generatedDate = rs.getTimestamp("generated_on");
				otpResendCount = rs.getInt("otp_count");
			}
			if (generatedDate != null) {
				

				DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
				String time1 = df.format(currentDate);
				String time2 = df.format(generatedDate);
				Date date1 = df.parse(time1);
				Date date2 = df.parse(time2);

				long otpResendDuration = (date1.getTime() - date2.getTime());// in ms

				if (otpResendDuration > otpResendDurationInPreferenceInMs) {
					StringBuilder sqlUpdate = new StringBuilder(
							"update user_otp set otp_pin=? , generated_on=? ,status=?,modified_by=? ,modified_on=?, otp_count =1, consumed_on=NULL where user_id=? and msisdn=?");
					String updateOTP = sqlUpdate.toString();
					
					pstmtS1 = p_con.prepareStatement(updateOTP);
					pstmtS1.setString(1, otp);
					pstmtS1.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					pstmtS1.setString(3, TypesI.YES);
					pstmtS1.setString(4, p_userVO.getUserID());
					pstmtS1.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					pstmtS1.setString(6, p_userVO.getUserID());
					pstmtS1.setString(7, p_userVO.getMsisdn());

					int updateCount = pstmtS1.executeUpdate();
					if (updateCount > 0) {
						count = updateCount;
						p_con.commit();
						p_userVO.setOTP(BTSLUtil.decryptText(otp));
						p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
						sendOTPForForgotPin(p_con, p_userVO, p_serviceType, validity);
					}

				} else {

					if (otpResendCount >= times) {
						count = otpResendCount;
						long otpResendDurationDiff = BTSLUtil.getDifferenceInUtilDatesinSeconds(generatedDate,currentDate);  //in minutes
						
						//return (int) (duration- otpResendDurationDiff);   //return in seconds
						return BTSLUtil.parseLongToInt(duration- otpResendDurationDiff);   //return in seconds
					} else {
						otpResendCount += 1;
						StringBuilder sqlUpdate = new StringBuilder(
								"update user_otp set otp_pin=? ,status=?,modified_by=? ,modified_on=?, otp_count =?, consumed_on=NULL where user_id=? and msisdn=?");
						String updateOTP = sqlUpdate.toString();
						pstmtS2 = p_con.prepareStatement(updateOTP);
						pstmtS2.setString(1, otp);
						pstmtS2.setString(2, TypesI.YES);
						pstmtS2.setString(3, p_userVO.getUserID());
						pstmtS2.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
						pstmtS2.setInt(5, otpResendCount);
						pstmtS2.setString(6, p_userVO.getUserID());
						pstmtS2.setString(7, p_userVO.getMsisdn());
						int updateCount = pstmtS2.executeUpdate();
						if (updateCount > 0) {
							count = updateCount;
							p_con.commit();
							p_userVO.setOTP(BTSLUtil.decryptText(otp));
							p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
							sendOTPForForgotPin(p_con, p_userVO, p_serviceType, validity);
						}
					}

				}
			}

			else {
				otpResendCount += 1;
				int i = 1;
				StringBuilder sqlInsert = new StringBuilder(
						"insert into user_otp (USER_ID,MSISDN,OTP_PIN,STATUS,GENERATED_ON, MODIFIED_ON, CREATED_BY,CREATED_ON,OTP_COUNT, INVALID_COUNTS ");
				if (!BTSLUtil.isNullString(p_serviceType))
					sqlInsert.append(",service_types");
				sqlInsert.append(")values (?,?,?,?,?,?,?,?,1,0");

				if (!BTSLUtil.isNullString(p_serviceType))
					sqlInsert.append(",?");

				sqlInsert.append(")");
				String insertOTP = sqlInsert.toString();
				if (_log.isDebugEnabled())
					_log.debug("generateSendOTP :: ", " Query insertOTP :" + insertOTP);
				pstmtS3 = p_con.prepareStatement(insertOTP);
				pstmtS3.setString(i++, p_userVO.getUserID());
				pstmtS3.setString(i++, p_userVO.getMsisdn());
				pstmtS3.setString(i++, otp);
				pstmtS3.setString(i++, TypesI.YES);
				pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				pstmtS3.setString(i++, p_userVO.getUserID());
				pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				if (!BTSLUtil.isNullString(p_serviceType))
					pstmtS3.setString(i++, p_serviceType);
				int insertCount = pstmtS3.executeUpdate();
				if (insertCount > 0) {
					count = insertCount;
					p_con.commit();
					p_userVO.setOTP(BTSLUtil.decryptText(otp));
					p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					sendOTPForForgotPin(p_con, p_userVO, p_serviceType, validity);
				}
			}
		} catch (SQLException sqe) {
			_log.error("generateOTP() ::", " Exception : " + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} catch (Exception ex) {
			_log.error("updateUserLoginDetails() ::", " Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing Rssultset" + ex);
			}
			try {
				if (pstmtS != null)
					pstmtS.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Select" + ex);
			}
			try {
				if (pstmtS1 != null)
					pstmtS1.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
			try {
				if (pstmtS2 != null)
					pstmtS2.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
			try {
				if (pstmtS3 != null)
					pstmtS3.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Insert" + ex);
			}
		}
		if (_log.isDebugEnabled())
			_log.debug("generateSendOTP() ::", " Exiting count=" + count);
		return count;
	}
	
	public Boolean generateSendOTPForForgotPassword(java.sql.Connection p_con, ChannelUserVO p_userVO, String p_serviceType,  int validity, int duration, int times)
			throws SQLException, Exception {
		if (_log.isDebugEnabled()) {
			StringBuffer msg = new StringBuffer("");
			msg.append(" Entered with user_id=");
			msg.append(p_userVO.getUserID());
			msg.append(" MSISDN=");
			msg.append(p_userVO.getMsisdn());
			msg.append("p_serviceType:");
			msg.append(p_serviceType);

			String message = msg.toString();
			_log.debug("generateSendOTPForForgotPassword() ::", message);
		}

		final String METHOD_NAME = "generateSendOTPForForgotPassword";
		PreparedStatement pstmtS = null;
		PreparedStatement pstmtS1 = null;
		PreparedStatement pstmtS2 = null;
		PreparedStatement pstmtS3 = null;
		Boolean sendOtp = false;
		ResultSet rs = null;
		String otp = null, decryptedOtp = null;
		try {
			_log.debug(METHOD_NAME,PretupsI.ENTERED);
			Date currentDate = new Date();
			decryptedOtp = utilClass.generateOTP();
			otp = BTSLUtil.encryptText(decryptedOtp);
			
			long otpResendDurationInPreferenceInMs = duration* 1000;   //duration in seconds
			
			int otpResendCount = 0;
			// otp="456456";
			StringBuilder sqlSelect = new StringBuilder(
					"Select generated_on,otp_count from user_otp where user_id=? and msisdn=? and status in(?,?) ");

			String selectOTP = sqlSelect.toString();
			if (_log.isDebugEnabled())
				_log.info("generateSendOTP ::", " Query selectOTP : " + selectOTP);
			pstmtS = p_con.prepareStatement(selectOTP);
			pstmtS.setString(1, p_userVO.getUserID());
			pstmtS.setString(2, p_userVO.getMsisdn());
			pstmtS.setString(3, TypesI.YES);
			pstmtS.setString(4, TypesI.NO);

			rs = pstmtS.executeQuery();
			Date generatedDate = null;

			if (rs.next()) {
				generatedDate = rs.getTimestamp("generated_on");
				otpResendCount = rs.getInt("otp_count");
			}
			if (generatedDate != null) {
				

				DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
				String time1 = df.format(currentDate);
				String time2 = df.format(generatedDate);
				Date date1 = df.parse(time1);
				Date date2 = df.parse(time2);

				long otpResendDuration = (date1.getTime() - date2.getTime());// in ms

				if (otpResendDuration > otpResendDurationInPreferenceInMs) {
					StringBuilder sqlUpdate = new StringBuilder(
							"update user_otp set otp_pin=? , generated_on=? ,status=?,modified_by=? ,modified_on=?, otp_count =1, consumed_on=NULL where user_id=? and msisdn=?");
					String updateOTP = sqlUpdate.toString();
					_log.info("updateSendOTP ::", " Query selectOTP : " + updateOTP);

					pstmtS1 = p_con.prepareStatement(updateOTP);
					pstmtS1.setString(1, otp);
					pstmtS1.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					pstmtS1.setString(3, TypesI.YES);
					pstmtS1.setString(4, p_userVO.getUserID());
					pstmtS1.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					pstmtS1.setString(6, p_userVO.getUserID());
					pstmtS1.setString(7, p_userVO.getMsisdn());

					int updateCount = pstmtS1.executeUpdate();
					if (updateCount > 0) {
						p_con.commit();
						p_userVO.setOTP(decryptedOtp);
						p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
						sendOtp = true;
						//sendOTPForForgotPassword(p_con, p_userVO, p_serviceType, validity);
					}

				} else {

					if (otpResendCount >= times) {
						sendOtp = false;
//						count = otpResendCount;
//						long otpResendDurationDiff = BTSLUtil.getDifferenceInUtilDatesinSeconds(generatedDate,currentDate);  //in minutes
//						
//						//return (int) (duration- otpResendDurationDiff);   //return in seconds
//						return BTSLUtil.parseLongToInt(duration- otpResendDurationDiff);   //return in seconds
					} else {
						otpResendCount += 1;
						StringBuilder sqlUpdate = new StringBuilder(
								"update user_otp set otp_pin=? ,status=?,modified_by=? ,modified_on=?, otp_count =?, consumed_on=NULL where user_id=? and msisdn=?");
						String updateOTP = sqlUpdate.toString();
						pstmtS2 = p_con.prepareStatement(updateOTP);
						pstmtS2.setString(1, otp);
						pstmtS2.setString(2, TypesI.YES);
						pstmtS2.setString(3, p_userVO.getUserID());
						pstmtS2.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
						pstmtS2.setInt(5, otpResendCount);
						pstmtS2.setString(6, p_userVO.getUserID());
						pstmtS2.setString(7, p_userVO.getMsisdn());
						int updateCount = pstmtS2.executeUpdate();
						if (updateCount > 0) {
							p_con.commit();
							p_userVO.setOTP(decryptedOtp);
							p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
							sendOtp = true;
							//sendOTPForForgotPassword(p_con, p_userVO, p_serviceType, validity);
						}
					}

				}
			}

			else {
				otpResendCount += 1;
				int i = 1;
				StringBuilder sqlInsert = new StringBuilder(
						"insert into user_otp (USER_ID,MSISDN,OTP_PIN,STATUS,GENERATED_ON, MODIFIED_ON, CREATED_BY,CREATED_ON,OTP_COUNT, INVALID_COUNTS ");
				if (!BTSLUtil.isNullString(p_serviceType))
					sqlInsert.append(",service_types");
				sqlInsert.append(")values (?,?,?,?,?,?,?,?,1,0");

				if (!BTSLUtil.isNullString(p_serviceType))
					sqlInsert.append(",?");

				sqlInsert.append(")");
				String insertOTP = sqlInsert.toString();
				if (_log.isDebugEnabled())
					_log.debug("generateSendOTP :: ", " Query insertOTP :" + insertOTP);
				pstmtS3 = p_con.prepareStatement(insertOTP);
				pstmtS3.setString(i++, p_userVO.getUserID());
				pstmtS3.setString(i++, p_userVO.getMsisdn());
				pstmtS3.setString(i++, otp);
				pstmtS3.setString(i++, TypesI.YES);
				pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				pstmtS3.setString(i++, p_userVO.getUserID());
				pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				if (!BTSLUtil.isNullString(p_serviceType))
					pstmtS3.setString(i++, p_serviceType);
				int insertCount = pstmtS3.executeUpdate();
				if (insertCount > 0) {
					p_con.commit();
					p_userVO.setOTP(decryptedOtp);
					p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					//sendOTPForForgotPassword(p_con, p_userVO, p_serviceType, validity);
					sendOtp = true;
				}
			}
		} catch (SQLException sqe) {
			_log.error("generateOTP() ::", " Exception : " + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} catch (Exception ex) {
			_log.error("updateUserLoginDetails() ::", " Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing Rssultset" + ex);
			}
			try {
				if (pstmtS != null)
					pstmtS.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Select" + ex);
			}
			try {
				if (pstmtS1 != null)
					pstmtS1.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
			try {
				if (pstmtS2 != null)
					pstmtS2.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
			try {
				if (pstmtS3 != null)
					pstmtS3.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Insert" + ex);
			}
		}
		if (_log.isDebugEnabled())
			_log.debug("generateSendOTP() ::", " Send Otp=" + sendOtp);
		return sendOtp;
	}

	/**
	 * Get details of the otp of a user
	 * @param p_con
	 * @param msisdn
	 * @return
	 * @throws BTSLBaseException
	 */
	 public UserOtpVO getDetailsOfUser(Connection p_con, String msisdn) throws BTSLBaseException{
  	   if (_log.isDebugEnabled())
             _log.debug("getDetailsOfUser ::", " Entered with msisdn" +msisdn);
         final String METHOD_NAME = "getDetailsOfUser";
         UserOtpVO userOtpVO = new UserOtpVO();
         PreparedStatement pstmt = null;
         ResultSet rs = null;
         try {

             String selectQuery = "Select * from user_otp where msisdn = ? ORDER BY GENERATED_ON DESC ";
             if (_log.isDebugEnabled())
                 _log.info("getOTPStatus ::", " Query selectQuery : " + selectQuery);
             pstmt = p_con.prepareStatement(selectQuery);
             int  i =1;
             pstmt.setString(i, msisdn);
             rs = pstmt.executeQuery();
             if (rs.next()) {
          	   userOtpVO.setBarredDate(rs.getTimestamp("BARRED_DATE"));
          	   userOtpVO.setGeneratedOn(rs.getTimestamp("MODIFIED_ON"));
          	   userOtpVO.setInvalidCount(rs.getString("INVALID_COUNTS"));
          	   userOtpVO.setOtppin(rs.getString("OTP_PIN"));
          	   userOtpVO.setUserId(rs.getString("USER_ID"));
          	  userOtpVO.setConsumedOn(rs.getTimestamp("CONSUMED_ON"));
             }

         } catch (SQLException sqe) {
             _log.error("getDetailsOfUser() ::", " Exception : " + sqe);
             _log.errorTrace(METHOD_NAME, sqe);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserOtpDAO[getOTPStatus]", "", "", "", "SQL Exception:" + sqe.getMessage());
             throw new BTSLBaseException(this, "getDetailsOfUser", "error.general.processing");
         } catch (Exception ex) {
             _log.error("getDetailsOfUser() ::", " Exception : " + ex);
             _log.errorTrace(METHOD_NAME, ex);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserOtpDAO[getOTPStatus]", "", "", "", "Exception:" + ex.getMessage());
             throw new BTSLBaseException(this, "getDetailsOfUser", "error.general.processing");
         } finally {
         	try {
                 if (rs != null)
                     rs.close();
             } catch (Exception ex) {
                 _log.error("getDetailsOfUser ::", " Exception : in closing Rssultset" + ex);
             }
             try {
                 if (pstmt != null)
                     pstmt.close();
             } catch (Exception ex) {
                 _log.error("getDetailsOfUser ::", " Exception : in closing preparedstatement for Update" + ex);
             }
         }
         if (_log.isDebugEnabled())
             _log.debug("getDetailsOfUser() ::", " Exiting with userOtpVO=" + userOtpVO.toString());
        
  	   return userOtpVO;
     }
  /**
   * Update the invalidCount and date of the incorrect otp
   * @param p_con
   * @param msisdn
   * @param invalidCount
   * @param barredDate
   * @return
   * @throws BTSLBaseException
   */
  public int updateInvalidCountOfOtp(Connection p_con, String msisdn, int invalidCount , Date barredDate) throws BTSLBaseException{
	   if (_log.isDebugEnabled())
          _log.debug("updateInvalidCountOfOtp ::", " Entered..............with msisdn" +msisdn);
      final String METHOD_NAME = "updateInvalidCountOfOtp";
      int updateCount =0;
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {

          String selectQuery = "Update user_otp set invalid_counts = ?, barred_date = ? where msisdn = ?";
          if (_log.isDebugEnabled())
              _log.info("updateInvalidCountOfOtp ::", " Query selectQuery : " + selectQuery);
          pstmt = p_con.prepareStatement(selectQuery);
          int i =1;
          pstmt.setInt(i, invalidCount);
          pstmt.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(barredDate));
          pstmt.setString(++i, msisdn);
          updateCount = pstmt.executeUpdate();
          if (updateCount <= 0) {
              throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
          }
         if(updateCount>0){
      	   p_con.commit();
         }

      }  catch (BTSLBaseException be) {
          throw be;
      } catch (SQLException sqle) {
          _log.error("updateInvalidCountOfOtp() ::", " Exception : " + sqle);
          _log.errorTrace(METHOD_NAME, sqle);
          EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserOtpDAO[getOTPStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
          throw new BTSLBaseException(this, "getDetailsOfUser", "error.general.processing");
      } // end of catch
      catch (Exception ex) {
          _log.error("getDetailsOfUser() ::", " Exception : " + ex);
          _log.errorTrace(METHOD_NAME, ex);
          EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserOtpDAO[getOTPStatus]", "", "", "", "Exception:" + ex.getMessage());
          throw new BTSLBaseException(this, "getDetailsOfUser", "error.general.processing");
      } // end of catch
      finally {
      	if(pstmt != null) {
      		try {
				pstmt.close();
			} catch (SQLException e) {
				LogFactory.printLog(METHOD_NAME, "Exception occured" + e, _log);
			}
      	}
          LogFactory.printLog(METHOD_NAME, "Exiting: insertCount=" + updateCount, _log);
        
      } // end of finally

      return updateCount;
  }
/**
 * Update in user_oyp for successful validation
 * @param p_con
 * @param msisdn
 * @return
 * @throws BTSLBaseException
 */
	public int updatePinSuccess(Connection p_con, String msisdn) throws BTSLBaseException {

		if (_log.isDebugEnabled())
			_log.debug("updatePinSuccess ::",
					" Entered with msisdn" + msisdn);
		final String METHOD_NAME = "updatePinSuccess";
		int updateCount = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Date currDate = new Date();
		try {

			String selectQuery = "Update user_otp set consumed_on = ?,invalid_counts = ? where msisdn = ?";
			if (_log.isDebugEnabled())
				_log.info("updatePinSuccess ::", " Query selectQuery : "
						+ selectQuery);
			pstmt = p_con.prepareStatement(selectQuery);
			int i = 0;
			pstmt.setTimestamp(++i,
					BTSLUtil.getTimestampFromUtilDate(currDate));
			pstmt.setInt(++i,0);
			pstmt.setString(++i, msisdn);
			updateCount = pstmt.executeUpdate();
			if (updateCount <= 0) {
				throw new BTSLBaseException(this, METHOD_NAME,
						"error.general.sql.processing");
			}
			if (updateCount > 0) {
				p_con.commit();
			}

		} catch (BTSLBaseException be) {
			throw be;
		} catch (SQLException sqle) {
			_log.error("updateInvalidCountOfOtp() ::", " Exception : " + sqle);
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[getOTPStatus]", "", "", "", "SQL Exception:"
							+ sqle.getMessage());
			throw new BTSLBaseException(this, "getDetailsOfUser",
					"error.general.processing");
		} // end of catch
		catch (Exception ex) {
			_log.error("getDetailsOfUser() ::", " Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[getOTPStatus]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "getDetailsOfUser",
					"error.general.processing");
		} // end of catch
		finally {

			if(pstmt != null) {
	      		try {
					pstmt.close();
				} catch (SQLException e) {
					LogFactory.printLog(METHOD_NAME, "Exception occured" + e, _log);
				}
	      	}
			LogFactory.printLog(METHOD_NAME, "Exiting: insertCount="
					+ updateCount, _log);

		} // end of finally

		return updateCount;

	}
	/*
	 * Added by Md. SOhail, Sparsh Karla
	 * Update in user_oyp for successful validation
	 *
	 * @param p_con
	 * @param p_channelUserVO
	 * @param p_serviceType
	 * @return
	 * @throws BTSLBaseException
	*/
	private void sendOTPForForgotPin(Connection p_con, ChannelUserVO p_channelUserVO, String p_serviceType, int validityPeriod)
			throws BTSLBaseException, Exception {
		if (_log.isDebugEnabled())
			_log.debug("sendOTPForForgotPin", " Entered with ChannelUserVO:" + p_channelUserVO.toString());
		final String METHOD_NAME = "sendOTPForForgotPin";
		MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
		try {
			
			String validityPeriodMessage= BTSLDateUtil.getTimeFromSeconds((int)validityPeriod);
			Locale locale = null;

			if (p_channelUserVO.getLanguage() == null) {
				String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
				p_channelUserVO.setLanguage(language);
			}
			if (p_channelUserVO.getCountry() == null) {
				String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
				p_channelUserVO.setCountry(country);
			}

			if (p_channelUserVO != null)
				locale = new Locale(p_channelUserVO.getLanguage(), p_channelUserVO.getCountry());
			else
				throw new BTSLBaseException(this, "sendOTPForForgotPin", PretupsErrorCodesI.CCE_ERROR_USER_DETAIL_NOT_FOUND);

			if ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_ON_SMS)) {
				String[] messageArgArray = { p_channelUserVO.getOTP(), validityPeriodMessage  };

				String btslMessage = null;
				if (!BTSLUtil.isNullString(p_serviceType))
					btslMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.OTP_MESSAGE_FOR_FORGOT_PIN + "_" + p_serviceType,
							messageArgArray);
				else
					btslMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.OTP_MESSAGE_FOR_FORGOT_PIN, messageArgArray) ;

				if (p_channelUserVO.getStaffUserDetails() != null) {
					if (BTSLUtil.isNullString(p_channelUserVO.getStaffUserDetails().getMsisdn())) {
						PushMessage pushParentMessages = (new PushMessage(p_channelUserVO.getMsisdn(), btslMessage,
								null, messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale));
						pushParentMessages.push();
					} else {
						PushMessage pushParentMessages = (new PushMessage(
								p_channelUserVO.getStaffUserDetails().getMsisdn(), btslMessage, null,
								messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale));
						pushParentMessages.push();
					}
				} else {
					PushMessage pushParentMessages = (new PushMessage(p_channelUserVO.getMsisdn(), btslMessage, null,
							messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale));
					pushParentMessages.push();
				}
			}
 
				/* send mail start */
				if ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TWO_FA_REQ_FOR_PIN)) {
					final String to = p_channelUserVO.getEmail();
					final String from = Constants.getProperty("mail_from_admin");
					final String subject = Constants.getProperty("\r\n" + 
							"FORGOT_PIN_OTP_SUBJECT");
					final String message = "Dear " + p_channelUserVO.getUserName() + ","
							+ Constants.getProperty("FORGOT_PIN_OTP_MESSAGE") + " " + (String) p_channelUserVO.getOTP()
							 + ". This OTP is valid for " + validityPeriodMessage + ". Do not disclose OTP to anyone.";
					EMailSender.sendMail(to, from, "", "", subject, message, false, "", "");
				}
			

			/* send mail ends */

		} catch (BTSLBaseException be) {
			_log.error("sendOTPForForgotPin", "BTSLBaseException " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("sendOTPForForgotPin", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[sendOTPForForgotPin]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("UserOtpDAO", "sendPassword", PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
		if (_log.isDebugEnabled())
			_log.debug("sendOTPForForgotPin", " Exit:");
	}

	public void sendOTPForForgotPassword(Connection p_con, ChannelUserVO p_channelUserVO, String p_serviceType, int validityPeriod)
			throws BTSLBaseException, Exception {
		final String methodName = "sendOTPForForgotPaasowd";
		if (_log.isDebugEnabled())
			_log.debug(methodName, " Entered with ChannelUserVO:" + p_channelUserVO.toString());
		final String METHOD_NAME = "sendOTPForForgotPin";
		MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
		try {
			
			String validityPeriodMessage= BTSLDateUtil.getTimeFromSeconds((int)validityPeriod);
			Locale locale = null;

			if (p_channelUserVO.getLanguage() == null) {
				String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
				p_channelUserVO.setLanguage(language);
			}
			if (p_channelUserVO.getCountry() == null) {
				String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
				p_channelUserVO.setCountry(country);
			}

			if (p_channelUserVO != null)
				locale = new Locale(p_channelUserVO.getLanguage(), p_channelUserVO.getCountry());
			else
				throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.CCE_ERROR_USER_DETAIL_NOT_FOUND);

			if ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_ON_SMS)) {
				String[] messageArgArray = { p_channelUserVO.getOTP(), validityPeriodMessage  };

				String btslMessage = null;
				if (!BTSLUtil.isNullString(p_serviceType))
					btslMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.OTP_MESSAGE_FOR_FORGOT_PIN + "_" + p_serviceType,
							messageArgArray);
				else
					btslMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.OTP_MESSAGE_FOR_FORGOT_PIN, messageArgArray) ;

				if (p_channelUserVO.getStaffUserDetails() != null) {
					if (BTSLUtil.isNullString(p_channelUserVO.getStaffUserDetails().getMsisdn())) {
						PushMessage pushParentMessages = (new PushMessage(p_channelUserVO.getMsisdn(), btslMessage,
								null, messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale));
						pushParentMessages.push();
					} else {
						PushMessage pushParentMessages = (new PushMessage(
								p_channelUserVO.getStaffUserDetails().getMsisdn(), btslMessage, null,
								messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale));
						pushParentMessages.push();
					}
				} else {
					PushMessage pushParentMessages = (new PushMessage(p_channelUserVO.getMsisdn(), btslMessage, null,
							messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale));
					pushParentMessages.push();
				}
			}
 
				/* send mail start */
				if ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TWO_FA_REQ_FOR_PIN)) {
					final String to = p_channelUserVO.getEmail();
					final String from = Constants.getProperty("mail_from_admin");
					final String subject = Constants.getProperty("FORGOT_PASSWORD_SUBJECT");
					final String message = "Dear " + p_channelUserVO.getUserName() + ","
							+ Constants.getProperty("FORGOT_PASSWORD_MESSAGE") + " " + (String) p_channelUserVO.getOTP()
							 + ". This OTP is valid for " + validityPeriodMessage + ". Do not disclose OTP to anyone.";
					EMailSender.sendMail(to, from, "", "", subject, message, false, "", "");
				}
			

			/* send mail ends */

		} catch (BTSLBaseException be) {
			_log.error("sendOTPForForgotPin", "BTSLBaseException " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("sendOTPForForgotPin", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[sendOTPForForgotPin]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("UserOtpDAO", "sendPassword", PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
		if (_log.isDebugEnabled())
			_log.debug("sendOTPForForgotPin", " Exit:");
	}
	/**
	 * added by vamshikrishna.v
	 * @param p_con
	 * @param p_userVO
	 * @param p_serviceType
	 * @return dto
	 *  @throws BTSLBaseException
	 **/
	public GenerateOtpDto generateOTP(java.sql.Connection p_con, ChannelUserVO p_userVO, String p_serviceType,  int validity, int duration, int times)
			throws SQLException, Exception {
		if (_log.isDebugEnabled()) {
			StringBuffer msg = new StringBuffer("");
			msg.append(" Entered with user_id=");
			msg.append(p_userVO.getUserID());
			msg.append(" MSISDN=");
			msg.append(p_userVO.getMsisdn());
			msg.append("p_serviceType:");
			msg.append(p_serviceType);

			String message = msg.toString();
			_log.debug("generateOTP() ::", message);
		}

		_log.info("generateOTP() ::","## generate otp decrypted");

		final String METHOD_NAME = "generateOTP";
		PreparedStatement pstmtS = null;
		PreparedStatement pstmtS1 = null;
		PreparedStatement pstmtS2 = null;
		PreparedStatement pstmtS3 = null;
		int count = 0;
		ResultSet rs = null;
		String otp = null;
		String decryptedOtp = null;
		GenerateOtpDto dto=new GenerateOtpDto();
		try {
			Date currentDate = new Date();
			dto.setOtp(utilClass.generateOTP());
			decryptedOtp = dto.getOtp();
			_log.info("generateOTP() ::","## generate otp decrypted:" + decryptedOtp);
			otp = BTSLUtil.encryptText(dto.getOtp());
			long otpResendDurationInPreferenceInMs = duration* 1000;  
			int otpResendCount = 0;
			StringBuilder sqlSelect = new StringBuilder(
					"Select generated_on,otp_count from user_otp where user_id=? and msisdn=? and status in(?,?) ");

			String selectOTP = sqlSelect.toString();
			if (_log.isDebugEnabled())
				_log.info("generateSendOTP ::", " Query selectOTP : " + selectOTP);
			pstmtS = p_con.prepareStatement(selectOTP);
			pstmtS.setString(1, p_userVO.getUserID());
			pstmtS.setString(2, p_userVO.getMsisdn());
			pstmtS.setString(3, TypesI.YES);
			pstmtS.setString(4, TypesI.NO);

			rs = pstmtS.executeQuery();
			Date generatedDate = null;

			if (rs.next()) {
				generatedDate = rs.getTimestamp("generated_on");
				otpResendCount = rs.getInt("otp_count");
			}
			if (generatedDate != null) {
				

				DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
				String time1 = df.format(currentDate);
				String time2 = df.format(generatedDate);
				Date date1 = df.parse(time1);
				Date date2 = df.parse(time2);

				long otpResendDuration = (date1.getTime() - date2.getTime());// in ms

				if (otpResendDuration > otpResendDurationInPreferenceInMs) {
					StringBuilder sqlUpdate = new StringBuilder(
							"update user_otp set otp_pin=? , generated_on=? ,status=?,modified_by=? ,modified_on=?, otp_count =1, consumed_on=NULL where user_id=? and msisdn=?");
					String updateOTP = sqlUpdate.toString();
					
					pstmtS1 = p_con.prepareStatement(updateOTP);
					pstmtS1.setString(1, otp);
					pstmtS1.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					pstmtS1.setString(3, TypesI.YES);
					pstmtS1.setString(4, p_userVO.getUserID());
					pstmtS1.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					pstmtS1.setString(6, p_userVO.getUserID());
					pstmtS1.setString(7, p_userVO.getMsisdn());

					int updateCount = pstmtS1.executeUpdate();
					if (updateCount > 0) {
						count = updateCount;
						p_con.commit();
						p_userVO.setOTP(decryptedOtp);
						p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					}

				} else {

					if (otpResendCount >= times) {
						count = otpResendCount;
						long otpResendDurationDiff = BTSLUtil.getDifferenceInUtilDatesinSeconds(generatedDate,currentDate);  //in minutes
						
						  //dto.setResendDuration((int) (duration- otpResendDurationDiff));
						dto.setResendDuration(BTSLUtil.parseLongToInt(duration- otpResendDurationDiff));
						  dto.setResentCount(count);
						  return dto;
						  //return in seconds
					} else {
						otpResendCount += 1;
						StringBuilder sqlUpdate = new StringBuilder(
								"update user_otp set otp_pin=? ,status=?,modified_by=? ,modified_on=?, otp_count =?, consumed_on=NULL where user_id=? and msisdn=?");
						String updateOTP = sqlUpdate.toString();
						pstmtS2 = p_con.prepareStatement(updateOTP);
						pstmtS2.setString(1, otp);
						pstmtS2.setString(2, TypesI.YES);
						pstmtS2.setString(3, p_userVO.getUserID());
						pstmtS2.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
						pstmtS2.setInt(5, otpResendCount);
						pstmtS2.setString(6, p_userVO.getUserID());
						pstmtS2.setString(7, p_userVO.getMsisdn());
						int updateCount = pstmtS2.executeUpdate();
						if (updateCount > 0) {
							count = updateCount;
							p_con.commit();
							p_userVO.setOTP(decryptedOtp);
							p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
						}
					}

				}
			}

			else {
				otpResendCount += 1;
				int i = 1;
				StringBuilder sqlInsert = new StringBuilder(
						"insert into user_otp (USER_ID,MSISDN,OTP_PIN,STATUS,GENERATED_ON, MODIFIED_ON, CREATED_BY,CREATED_ON,OTP_COUNT, INVALID_COUNTS ");
				if (!BTSLUtil.isNullString(p_serviceType))
					sqlInsert.append(",service_types");
				sqlInsert.append(")values (?,?,?,?,?,?,?,?,1,0");

				if (!BTSLUtil.isNullString(p_serviceType))
					sqlInsert.append(",?");

				sqlInsert.append(")");
				String insertOTP = sqlInsert.toString();
				if (_log.isDebugEnabled())
					_log.debug("generateSendOTP :: ", " Query insertOTP :" + insertOTP);
				pstmtS3 = p_con.prepareStatement(insertOTP);
				pstmtS3.setString(i++, p_userVO.getUserID());
				pstmtS3.setString(i++, p_userVO.getMsisdn());
				pstmtS3.setString(i++, otp);
				pstmtS3.setString(i++, TypesI.YES);
				pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				pstmtS3.setString(i++, p_userVO.getUserID());
				pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
				if (!BTSLUtil.isNullString(p_serviceType))
					pstmtS3.setString(i++, p_serviceType);
				int insertCount = pstmtS3.executeUpdate();
				if (insertCount > 0) {
					count = insertCount;
					p_con.commit();
					p_userVO.setOTP(decryptedOtp);
					p_userVO.setOtpModifiedOn(BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
					
				}
			}
		} catch (SQLException sqe) {
			_log.error("generateOTP() ::", " Exception : " + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} catch (Exception ex) {
			_log.error("updateUserLoginDetails() ::", " Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserOtpDAO[generateSendOTP]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "generateSendOTP", "error.general.processing");
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing Rssultset" + ex);
			}
			try {
				if (pstmtS != null)
					pstmtS.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Select" + ex);
			}
			try {
				if (pstmtS1 != null)
					pstmtS1.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
			try {
				if (pstmtS2 != null)
					pstmtS2.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Update" + ex);
			}
			try {
				if (pstmtS3 != null)
					pstmtS3.close();
			} catch (Exception ex) {
				_log.error("generateSendOTP ::", " Exception : in closing preparedstatement for Insert" + ex);
			}
		}
		if (_log.isDebugEnabled())
			_log.debug("generateSendOTP() ::", " Exiting count=" + count);
		  dto.setResentCount(count);
		return dto;
	}
	
  
      }
     

