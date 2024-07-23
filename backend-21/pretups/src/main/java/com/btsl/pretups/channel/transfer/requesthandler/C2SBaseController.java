package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.Locale;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @author Akanksha
 * This class is to maintain all the common method for C2S controller to remove duplicate code.
 */
public class C2SBaseController {

	/**
	 * Commons Logging instance.
	 */
	public static final Log _log = LogFactory.getLog(C2SBaseController.class.getName());

	/**Method to get the fail message to be sent to receiver
	 * @param _transferID
	 * @param amount
	 * @param senderPushMsgMsisdn
	 * @param userName
	 * @param PostUSerMsisdn
	 * @param receiverLocale
	 * @param errorCode
	 * @param gatewayType
	 * @return String
	 */
	public String getReceiverFailMessage(String transferID, String amount,
			String senderPushMsgMsisdn, String userName, String postUSerMsisdn,
			Locale receiverLocale, String errorCode, String gatewayType) {
		final String methodName = "getReceiverFailMessage";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: _transferID=");
			loggerValue.append(transferID);
			loggerValue.append(" amount:");
			loggerValue.append(amount);
			loggerValue.append(" senderPushMsgMsisdn:");
			loggerValue.append(senderPushMsgMsisdn);
			loggerValue.append(" userName:" );
			loggerValue.append(userName);
			loggerValue.append(" PostUSerMsisdn:");
			loggerValue.append(postUSerMsisdn);
			loggerValue.append(" receiverLocale:");
			loggerValue.append(receiverLocale);
			loggerValue.append(" errorCode:");
			loggerValue.append(errorCode);
			loggerValue.append(" gatewayType:" );
			loggerValue.append(gatewayType);
			_log.debug(methodName,  loggerValue);
		}
		final String[] messageArgArray = { transferID, amount,
				senderPushMsgMsisdn, userName, postUSerMsisdn };
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Exited");
		}
		return BTSLUtil.getMessage(receiverLocale, errorCode, messageArgArray,
				gatewayType);

	}

}
