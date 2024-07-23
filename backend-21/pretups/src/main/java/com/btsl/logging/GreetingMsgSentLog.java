package com.btsl.logging;

public class GreetingMsgSentLog {
    private static final Log _log = LogFactory.getFactory().getInstance(GreetingMsgSentLog.class.getName());

    /**
	 * ensures no instantiation
	 */
    private GreetingMsgSentLog(){
    	
    }
    /**
     * Method to log the info in the file
     * 
     * @param p_msisdn
     * @param p_locale
     * @param p_gatewayType
     * @param p_gatewayCode
     * @param p_message
     * @param p_status
     * @param p_otherInfo
     */
    public static void log(String p_msisdn, String p_message , String p_status ) throws Exception {
        final String METHOD_NAME = "log";
        try {
            StringBuilder strBuild = new StringBuilder();
			strBuild.append(" [Status:").append(p_status).append("]");
            strBuild.append(" [MSISDN:").append(p_msisdn).append("]");
           
			if (_log.isDebugEnabled()) {
                strBuild.append(" [Message:").append(p_message).append("]");
            } else {
                strBuild.append(" [Message: Message has been sent to user ]");
            }

            _log.info("", strBuild.toString());
        }
		finally{
			  if (_log.isDebugEnabled())
				_log.debug(METHOD_NAME , "Exiting");
			
		}
    }
}
