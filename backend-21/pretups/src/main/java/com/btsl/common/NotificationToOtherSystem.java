package com.btsl.common;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;

public class NotificationToOtherSystem implements Runnable {
	private static Log log = LogFactory.getLog(NotificationToOtherSystem.class.getName());
	String type;
	String msisdn;
	String reason;
	BarredUserVO barredUserVO = null;

	public NotificationToOtherSystem(String type, BarredUserVO barredUserVO) {
		final String methodName = "NotificationToOtherUrl";
		if (log.isDebugEnabled())
			log.debug(methodName, "Entered with type: " + type + "BarredUserVO " + barredUserVO);
		this.type = type;
		this.msisdn = barredUserVO.getMsisdn();
		this.reason = barredUserVO.getBarredReason();
		this.barredUserVO = barredUserVO;
	}

	public void run() {

		sendNotification();

	}

	public void sendNotification() {
		final String methodName = "sendNotification";
		if (log.isDebugEnabled())
			log.debug(methodName, "Entered...");
		try {
			final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache
					.getObject(barredUserVO.getModule(), barredUserVO.getNetworkCode(),
							PretupsI.INTERFACE_CATEGORY_PRE);
			String intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
			String intModClassNameS = networkInterfaceModuleVOS.getClassName();
			final CommonClient commonClient = new CommonClient();
			final String requestStr = getSenderValidateStr();
			final String senderValResponse = commonClient.process(requestStr, null,intModCommunicationTypeS, null, 0,intModClassNameS);

		} catch (Exception e) {
			log.error(methodName, "Exception : " + e.getMessage());
			log.errorTrace(methodName, e);
		} finally {
			if (log.isDebugEnabled())
				log.debug(methodName, "Exited...");
		}
		
	}

	private String getSenderValidateStr() {
		StringBuilder sb= new StringBuilder();
		sb.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
		sb.append("&TYPE=" + type);
		sb.append("&MSISDN=" + msisdn);
		sb.append("&REASON=" + reason);
		sb.append("&CRM_NOTIFICATION=" + PretupsI.YES);
		return sb.toString();
	}

	

}
