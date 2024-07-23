package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @author sanjay.bind1
 *
 */
public class BulkUserSuspensionProcessLog {
	
	private static Log _Filelogger = LogFactory.getLog(BulkUserSuspensionProcessLog.class.getName());
	/**
	 * 
	 */
	private BulkUserSuspensionProcessLog() {
		super();
	}
	
	/**
	 * @param p_action
	 * @param p_userID
	 * @param p_msisdn
	 * @param p_mobileCount
	 * @param p_message
	 * @param p_result
	 * @param p_otherInfo
	 */
	public static void log(String p_action,String p_userID,String p_msisdn,long p_mobileCount,String p_message,String p_result,String p_otherInfo)
	{
	    StringBuffer message=new StringBuffer();
		message.append("[ACTION = "+BTSLUtil.NullToString(p_action)+"] ");
		message.append("[LOGGED IN USER'S ID = "+BTSLUtil.NullToString(p_userID)+"] ");
		message.append("[PROCESSED STRING = "+BTSLUtil.NullToString(p_msisdn)+"] ");
		message.append("[PROCESSED STRING POSITION = "+p_mobileCount+"] ");
		message.append("[MESSAGE = "+BTSLUtil.NullToString(p_message)+"] ");
		message.append("[RESULT = "+BTSLUtil.NullToString(p_result)+"] ");
		message.append("[OTHER INFO = "+BTSLUtil.NullToString(p_otherInfo)+"] ");
		_Filelogger.info(" ",message);
	}
	
	/**
	 * @param method
	 */
	public static void logPrint(String method)
	{
		
		StringBuilder strBuild = new StringBuilder();
		 strBuild.append("").append(method).append("-----------------");
         //strBuild.append("[value:").append(value).append("]");
        _Filelogger.info("",strBuild.toString());
	}

	
	/**
	 * @param method
	 * @param filename
	 * @param totalCount
	 * @param successCount
	 * @param failureCount
	 */
	public static void summaryLog(String method,String filename,Integer totalCount,Integer successCount,Integer failureCount)
	{
		
		StringBuilder strBuild = new StringBuilder();
		 strBuild.append("").append(method).append("-------");
		 strBuild.append("File Name is: ").append(filename).append("");
         strBuild.append(",Total count is : ").append(totalCount).append("");
         strBuild.append(",Success count is : ").append(successCount).append("");
         strBuild.append(",Failure count is : ").append(failureCount).append("");
        _Filelogger.info("",strBuild.toString());
	}
}
