package com.inter.hcpt.logger;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class ExternalGatewayRequestLog {
    private static Log _log = LogFactory.getLog(ExternalGatewayRequestLog.class.getName());


    /**
	 * ensures no instantiation
	 */
    private ExternalGatewayRequestLog(){
    	
    }
    /**
     * Method that prepares the the string to be written in log file
     * 
     * @param p_requestVO
     * @return
     */
//    private static String generateMessageString(RequestVO p_requestVO) {}// end of generateMessageString

    /**
     * Method to log the details in Request Log
     * 
     * @param p_requestVO
     */
    public static void outLog(String p_requestID,String p_response,long p_requestStartTime) {
    	final StringBuilder strBuild = new StringBuilder();
        StringBuilder loggerValue= new StringBuilder(); 
        strBuild.append("[ReqOut]");
	strBuild.append("[RQID:").append(p_requestID).append("]");
	strBuild.append("[RESMSG:" ).append( p_response ).append( "]");
	strBuild.append("[TT:" ).append(System.currentTimeMillis()-p_requestStartTime ).append( " ms]");
        _log.info("", strBuild.toString());
                 }

    /**
     * Method to log the details of input stage,Request Log
     * 
     * @param p_requestVO
     */
    public static void inLog(String p_requestID,String p_request) {
        final StringBuilder strBuild = new StringBuilder();
        StringBuilder loggerValue= new StringBuilder(); 
        strBuild.append("[ReqIn]");
	strBuild.append("[RQID:").append(p_requestID).append("]");
		strBuild.append("[REQMSG:" ).append( p_request ).append( "]");
        _log.info("", strBuild.toString());
    }
}
