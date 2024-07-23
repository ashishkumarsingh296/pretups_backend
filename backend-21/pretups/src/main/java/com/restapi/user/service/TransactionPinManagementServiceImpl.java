package com.restapi.user.service;

import java.sql.Connection;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;

@Service("TransactionPinManagementService")
public class TransactionPinManagementServiceImpl implements TransactionPinManagementService {
	private static final Log log = LogFactory.getLog(TransactionPinManagementServiceImpl.class.getName());
	private static final String classname = "TransactionPinManagementServiceImpl";

	@Override
	public TransactionPinManagementResponseVO validatePinModifyRequired(String msisdn) throws BTSLBaseException {
		final String methodName = "validatePinModifyRequired";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered msisdn : " + msisdn);
		}
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		TransactionPinManagementResponseVO response = new TransactionPinManagementResponseVO();
		UserDAO userDAO = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO uservo = null;
		Date pwdModifiedOn = null;
		Date lastLoginOn = null;
		Date pinModifiedOn = null;
		String pinReset = null;
		Date currentDate = new Date();
		boolean isPinChangeRequired = false;

		try {
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				uservo = userDAO.loadUserDetailsCompletelyByMsisdn(con, msisdn);
			} catch (Exception e) {
				log.debug(methodName, e.getMessage());
				throw new BTSLBaseException(classname, methodName, "Error occured", "Exception " + e);
			}
			if (uservo == null || uservo.getUserPhoneVO() == null) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.INVALID_MSISDN, 0, null);
			}
			pwdModifiedOn = uservo.getPasswordModifiedOn();
			lastLoginOn = uservo.getLastLoginOn();
			pinModifiedOn = uservo.getUserPhoneVO().getPinModifiedOn();
			pinReset = uservo.getUserPhoneVO().getPinReset();

			if (pwdModifiedOn == null || lastLoginOn == null || pinModifiedOn == null) {
				isPinChangeRequired = true;
			}
			if ("Y".equals(pinReset)) {
				isPinChangeRequired = true;
			}
			final long daysAfterChngPn = ((Integer) PreferenceCache.getControlPreference(
					PreferenceI.C2S_DAYS_AFTER_CHANGE_PIN, uservo.getNetworkID(), uservo.getCategoryCode())).intValue();
			long dt2 = currentDate.getTime();
			long dt1 = uservo.getUserPhoneVO().getPinModifiedOn().getTime();
			final long noOfdays = ((dt2 - dt1) / (1000 * 60 * 60 * 24));
			if (uservo.getUserPhoneVO().getPinModifiedOn() != null && noOfdays > daysAfterChngPn) {
				isPinChangeRequired = true;
			}
			if (isPinChangeRequired) {
				response.setPinChangeRequired(isPinChangeRequired);
				response.setStatus(HttpStatus.SC_OK);
				response.setMessageCode(PretupsErrorCodesI.PIN_CHANGE_REQUIRED);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PIN_CHANGE_REQUIRED, null);
				response.setMessage(msg);
			} else {
				response.setPinChangeRequired(isPinChangeRequired);
				response.setStatus(HttpStatus.SC_OK);
				response.setMessageCode(PretupsErrorCodesI.PIN_CHANGE_NOT_REQUIRED);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PIN_CHANGE_NOT_REQUIRED, null);
				response.setMessage(msg);
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.close(classname + "#" + methodName);
				mcomCon = null;
			}

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}
		}
		return response;
	}
}
