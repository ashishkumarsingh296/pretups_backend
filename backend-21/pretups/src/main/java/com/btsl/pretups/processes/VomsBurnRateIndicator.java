package com.btsl.pretups.processes;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.ibm.icu.util.Calendar;

/**
 * @(#)VomsBurnRateIndicator .java
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Shaina Sahni 23/11/2017 Initial creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 */

public class VomsBurnRateIndicator {
	private static String message = "";
	private static ProcessBL _processBL = null;
	private static ProcessStatusVO _processStatusVO;
	private static final Log _logger = LogFactory.getLog(VomsBurnRateIndicator.class.getName());
	private static final String CLASS_NAME = "VomsBurnRateIndicator";

	

    /**
     * to ensure no class instantiation 
     */
    private VomsBurnRateIndicator(){
    	
    }
	public static void main(String[] args) {
		Connection con = null;
		final String methodName = "main";
		try {
			if (args.length != 2) {
				System.out.println("Usage : VomsBurnRateIndicator [Constants file] [LogConfig file]");
				return;
			}
			final File constantsFile = new File(args[0]);
			if (!constantsFile.exists()) {
				System.out.println(" Constants File Not Found .............");
				return;
			}
			final File logconfigFile = new File(args[1]);
			if (!logconfigFile.exists()) {
				System.out.println(" Logconfig File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
		} catch (Exception e) {
			System.out.println("Exception thrown in VomsBurnRateIndicator: Not able to load files" + e);
			ConfigServlet.destroyProcessCache();
			_logger.errorTrace(methodName, e);
			return;
		}

		try {
			// Make Connection
			con = OracleUtil.getSingleConnection();
			if (con == null) {
				if (_logger.isDebugEnabled()) {
					_logger.debug("VomsBurnRateIndicator[main]", "Not able to get Connection for VomsBurnRateIndicator: ");
				}
				throw new SQLException();
			}
			burnRateExecution(con);
		} catch (Exception e) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("VomsBurnRateIndicator[main]", "Exception thrown in VomsBurnRateIndicator: Not able to load files" + e);
			}
			_logger.errorTrace(methodName, e);
			ConfigServlet.destroyProcessCache();
		} finally {
			if (_logger.isDebugEnabled()) {
				_logger.debug("VomsBurnRateIndicator[main]", "Exiting");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(methodName, e);
			}
			ConfigServlet.destroyProcessCache();
		}
	}

	private static void burnRateExecution(Connection con) {
		final String methodName = "burnRateExecution";
		if (_logger.isDebugEnabled()) {
			_logger.debug(methodName, " Entered:");
		}
		CallableStatement cstmt = null;
		Date currentDate = null;
		String reportTo = null;
		String prevDateStr = null;
		Date processedUpto = null;
		String processId = null;
		boolean statusOk = false;
		int beforeInterval = 0;
		ResultSet rs = null;
		try {

			processId = ProcessI.VOMS_BURN_RATE;
			// method call to check status of the process
			_processBL = new ProcessBL();
			_processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
			statusOk = _processStatusVO.isStatusOkBool();
			beforeInterval = BTSLUtil.parseLongToInt(_processStatusVO.getBeforeInterval() / (60 * 24));
			if (statusOk) {
				con.commit();
				// method call to find maximum date till which process has been
				// executed
				processedUpto = _processStatusVO.getExecutedUpto();
				if (processedUpto != null) {
					// adding 1 in processed upto date as we have to start from
					// the next day till which process has been executed
					processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
					final Calendar cal = BTSLDateUtil.getInstance();
					currentDate = cal.getTime(); // Current Date
					currentDate = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);
				} else {
					if (_logger.isDebugEnabled()) {
						_logger.debug(methodName, " Date till which process has been executed is not found.");
					}
					return;
				}
			}
			try {
				final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
				sdf.setLenient(false); // this is required else it will convert
				reportTo = sdf.format(currentDate); // Current Date
				prevDateStr = sdf.format(processedUpto);// Last MIS Done Date +1
				
				/*reportTo = BTSLDateUtil.getSystemLocaleDate(currentDate, PretupsI.DATE_FORMAT); // Current Date
                prevDateStr = BTSLDateUtil.getSystemLocaleDate(processedUpto, PretupsI.DATE_FORMAT);// Last MIS Done Date +1
                */
			} catch (Exception e) {
				reportTo = "";
				prevDateStr = "";
				_logger.errorTrace(methodName, e);
				throw new BTSLBaseException(CLASS_NAME, methodName, "Not able to convert date to String");
			}

			//  Process will be exceuted from the start till to date -1
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName,
						"From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(currentDate)=" + processedUpto.compareTo(currentDate));
			}

			// If process is already ran for the last day then do not run again
			if (processedUpto != null && processedUpto.compareTo(currentDate) > 0) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "",
						"", "", "Vouchers burn rate process already run for the date=" + String.valueOf(currentDate));
				return;
			}
			String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
			if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
				cstmt = con.prepareCall("{call get_voms_data_dtrange(?,?)}");
				cstmt.setString(1, prevDateStr);
				cstmt.setString(2, reportTo);

				if (_logger.isDebugEnabled()) {
					_logger.debug("VomsBurnRateIndicator[burnRateExecution]", "Before Exceuting Procedure");
				}
				 rs=cstmt.executeQuery();
				if (_logger.isDebugEnabled()) {
					_logger.debug("VomsBurnRateIndicator[burnRateExecution]", "After Exceuting Procedure");
				}
				if(rs.next()){
					String status=rs.getString("aov_message");
					String messageforlog=rs.getString("aov_messageforlog");
					String sqlerrmsgforlog=rs.getString("aov_sqlerrmsgforlog");
					if("SUCCESS".equalsIgnoreCase(status))
						con.commit();

					if (_logger.isDebugEnabled()) {
						_logger.debug("VomsBurnRateIndicator[burnRateExecution]",
								"Parameters Returned : Status=" + status + " , Message=" + messageforlog + " ,Exception if any=" + sqlerrmsgforlog);
					}
					if (status == null || !("SUCCESS".equalsIgnoreCase(status))) {
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsBurnRateIndicator[burnRateExecution]",
								"", "", "", messageforlog + " Exception if any:" + sqlerrmsgforlog);
					} else {
						EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsBurnRateIndicator[burnRateExecution]", "",
								"", "", messageforlog + " Exception if any:" + sqlerrmsgforlog);
					}
					message = BTSLUtil.NullToString(messageforlog) + BTSLUtil.NullToString(sqlerrmsgforlog);
				}

			}
			else{

				cstmt = con.prepareCall("{call Voms_burned_rate_Pkg.GET_VOMS_DATA_DTRANGE(?,?,?,?,?)}");
				cstmt.registerOutParameter(3, Types.VARCHAR); // Message
				cstmt.registerOutParameter(4, Types.VARCHAR); // Message for log
				cstmt.registerOutParameter(5, Types.VARCHAR); // Sql Exception
				cstmt.setString(1, prevDateStr);
				cstmt.setString(2, reportTo);



				if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, "Before Exceuting Procedure");
				}
				cstmt.executeUpdate();
				if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, "After Exceuting Procedure");
				}
				if (_logger.isDebugEnabled()) {
					_logger.debug(methodName,
							"Parameters Returned : Status=" + cstmt.getString(3) + " , Message=" + cstmt.getString(4) + " ,Exception if any=" + cstmt.getString(5));
				}

				if (cstmt.getString(3) == null || !("SUCCESS".equalsIgnoreCase(cstmt.getString(3)))) {
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName,
							"", "", "", cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));
				} else {
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "",
							"", "", cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));
				}
				message = BTSLUtil.NullToString(cstmt.getString(4)) + BTSLUtil.NullToString(cstmt.getString(5));

			}
			// send the message as SMS
			final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

			final String msisdnString = Constants.getProperty("adminmobile");
			final String[] msisdn = msisdnString.split(",");

			for (int i = 0; i < msisdn.length; i++) {
				final PushMessage pushMessage = new PushMessage(msisdn[i], message, null, null, locale);
				pushMessage.push();
			}

			sendAlertSMSandEmail(con);
			try {
				Thread.sleep(5);
			} catch (Exception e) {
				_logger.errorTrace(methodName, e);
			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception sqlex) {
				if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, "Exception while roll back" + sqlex);
				}
				_logger.errorTrace(methodName, sqlex);
			}
			message = e.getMessage();
			// send the message as SMS
			_logger.errorTrace(methodName, e);
		} finally {
			try {
				if (statusOk) {
					if (markProcessStatusAsComplete(con, processId) == 1) {
						try {
							con.commit();
						} catch (Exception e) {
							_logger.errorTrace(methodName, e);
						}
					} else {
						try {
							con.rollback();
						} catch (Exception e) {
							_logger.errorTrace(methodName, e);
						}
					}
				}
				if (cstmt != null) {
					cstmt.close();
				}
			} catch (Exception ex) {
				if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, "Exception while closing statement in "+ methodName +"method ");
				}
				_logger.errorTrace(methodName, ex);
			}
			try {
				if(rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(methodName,e);
			}
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				_logger.errorTrace(methodName, e);
			}
		}

	}

	private static int markProcessStatusAsComplete(Connection con, String processId) {
		final String methodName = "markProcessStatusAsComplete";
		if (_logger.isDebugEnabled()) {
			_logger.debug(methodName, " Entered:  p_processId:" + processId);
		}
		int updateCount = 0;
		final Date currentDate = new Date();
		final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
		_processStatusVO.setProcessID(processId);
		_processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
		_processStatusVO.setStartDate(currentDate);
		try {
			updateCount = processStatusDAO.updateProcessDetailForMis(con, _processStatusVO);
		} catch (Exception e) {
			_logger.errorTrace(methodName, e);
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Exception= " + e.getMessage());
			}
		} finally {
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Exiting: updateCount=" + updateCount);
			}
		} // end of finally
		return updateCount;

	}

	private static  void sendAlertSMSandEmail(Connection con) {
		final String methodName = "sendAlertSMSandEmail";
		if (_logger.isDebugEnabled()) {
			_logger.debug(methodName, " Entered");
		}
		ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
		ChannelUserVO userVO = new ChannelUserVO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
		VomsVoucherVO alertVO = new VomsVoucherVO();
		final UserDAO userDAO = new UserDAO();
		BTSLMessages btslMessage = null;
		BTSLMessages btslMessageAdmin = null;
		BTSLMessages btslMessageUser = null;
		String[] arr = null;
		String[] arrArray = null;
		String[] arrArrayString= null;
		Locale locale = null;
		String adminMSISDN = null;
		String adminEmailid = null;
		PushMessage pushMessage = null;
		PushMessage pushMessageAdmin = null;
		String subject = null;
		EmailSendToUser emailSendToUser = null;


		ArrayList<VomsVoucherVO> alertList;
		ArrayList<String> alertMessageList = new ArrayList<>();
		try
		{
			final String emailKeyAdmin = PretupsErrorCodesI.BURN_RATE_ALERT_EMAIL_ADMIN;
			final String emailKeyUser = PretupsErrorCodesI.BURN_RATE_ALERT_EMAIL_USER;


			alertList = vomsVoucherDAO.burnRateAlertUsers(con);
			subject = Constants.getProperty("BURN_RATE_ALERT_EMAIL_SUBJECT");

			if(!alertList.isEmpty())
			{
				for (int i = 0, j = alertList.size(); i < j; i++) {
					alertVO = alertList.get(i);

					channelUserVO = channelUserDAO.loadChannelUserDetails(con, alertVO.getMsisdn());
					try {
						final String smsKey = PretupsErrorCodesI.BURN_RATE_ALERT_MESSAGE;
						arr = new String[5];
						final UserPhoneVO phoneVO = userDAO.loadUserPhoneVO(con, alertVO.getUserID());
						arr[0] = phoneVO.getMsisdn();
						arr[1] = String.valueOf(alertVO.getBurnRate());
						arr[2] = String.valueOf(alertVO.getTotalDistributed());
						arr[3] = String.valueOf(alertVO.getTotalConsumed());
						arr[4] = String.valueOf(alertVO.getMRP());
						locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
						btslMessage = new BTSLMessages(smsKey, arr);
						//Push SMS to channel user
						if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_ALLOWED))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_BURN_RATE_SMS_ALERT))).booleanValue()) {
							pushMessage = new PushMessage(alertVO.getMsisdn(), btslMessage, null, null, locale, alertVO.getUserNetworkCode());
							adminMSISDN = Constants.getProperty("BURN_RATE_ALERT_MSISDNS");

							try {
								pushMessage.push();				
							} catch (RuntimeException e1) {
								_logger.errorTrace(methodName, e1);
							}

						}

						// Push email to channel user
						if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() &&  ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_BURN_RATE_EMAIL_ALERT))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
							arrArray = new String[1];
							arrArray[0] =  '\n' + "MSISDN:" +alertVO.getMsisdn() +"   Burn Rate: " +String.valueOf(alertVO.getBurnRate()) +"   Total Distributed Vouchers: " +String.valueOf(alertVO.getTotalDistributed())+ "   Total Recharged Vouchers: " +String.valueOf(alertVO.getTotalConsumed()) +"   Denomination: " +String.valueOf(alertVO.getMRP()) +  '\n';
							arrArray[0] = arrArray[0].replace("[","");
							arrArray[0] = arrArray[0].replace("]","");
							arrArray[0] = arrArray[0].replace(",","");
							btslMessageUser = new BTSLMessages(emailKeyUser, arrArray);

							emailSendToUser = new EmailSendToUser(subject, btslMessageUser, locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
									channelUserVO, channelUserVO);
							emailSendToUser.sendMail();


						}


					} catch (BTSLBaseException be) {
						_logger.errorTrace(methodName,be);
					} catch (Exception e) {
						_logger.errorTrace(methodName, e);
					}



				}

				//Push email to admin
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_BURN_RATE_EMAIL_ALERT))).booleanValue() ){
					for (int i = 0, j = alertList.size(); i < j; i++) {
						alertVO = alertList.get(i);
						arrArray = new String[j];
						arrArray[i] =  '\n' + "MSISDN:" +alertVO.getMsisdn() +"   Burn Rate: " +String.valueOf(alertVO.getBurnRate()) +"   Total Distributed Vouchers: " +String.valueOf(alertVO.getTotalDistributed())+ "   Total Recharged Vouchers: " +String.valueOf(alertVO.getTotalConsumed()) +"   Denomination: " +String.valueOf(alertVO.getMRP()) +  '\n';
						alertMessageList.add(arrArray[i]);			

					}
					if(!alertMessageList.isEmpty())
					{
						arrArrayString = alertMessageList.toArray(new String[0]);

						String ans1 = Arrays.toString(arrArrayString);
						ans1 = ans1.replace("[","");
						ans1 = ans1.replace("]","");
						ans1 = ans1.replace(",","");

						arrArrayString = new String[] {ans1};

						btslMessageAdmin = new BTSLMessages(emailKeyAdmin, arrArrayString);
					}
					adminEmailid = Constants.getProperty("BURN_RATE_ALERT_EMAILIDS");


					if(!BTSLUtil.isNullString(adminEmailid))
					{

						final String[] adminEmailidArr = adminEmailid.split(",");
						int n=0;
						while(n < adminEmailidArr.length)
						{
							Boolean validEmail = BTSLUtil.validateEmailID(adminEmailidArr[n]);
							if(validEmail)
							{
								userVO.setEmail(adminEmailidArr[n]);
								userVO.setUserName("user");
								emailSendToUser = new EmailSendToUser(subject, btslMessageAdmin, locale, null, "Email will be delivered shortly",
										userVO, userVO);
								if(emailSendToUser!=null)
									emailSendToUser.sendMail();
								n++;
							}
						}

					}
				}
			}

		}
		catch (Exception e) {
			_logger.errorTrace(methodName, e);
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Exception= " + e.getMessage());
			}
		} finally {
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Exiting");
			}
		} // end of finally

	}
}
