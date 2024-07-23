package com.restapi.loggers;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.logging.ActivityLog;

@Service("LoggerServiceI")
public class LoggerServiceImpl implements LoggerServiceI{

	public static final Log log = LogFactory.getLog(LoggerServiceImpl.class.getName());
    public static final String  classname = "LoggerServiceImpl";
    
    @Override
    public void printLog(ArrayList<LogVO> p_logs , HttpServletRequest p_request) throws BTSLBaseException{
    	final String methodName = "getElementCodeDetailsMap";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     ArrayList<LogVO> logs = p_logs;
		 String remoteAddress = p_request.getRemoteAddr();
		 String remoteHost = p_request.getRemoteHost();
		 if(logs != null) {
			 logs.stream().forEach(logObj -> ActivityLog.webUILog(logObj, remoteAddress, remoteHost));
		 }else {
			 throw new BTSLBaseException(this , methodName , PretupsErrorCodesI.NO_DATA_FOUND_TO_LOG);
		 }
	     
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Exiting");
	     }
	}
	
}
