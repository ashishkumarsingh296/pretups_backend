package com.btsl.pretups.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.logging.ActivityLog;
import com.google.gson.Gson;
import com.restapi.loggers.PrintLogsRequestVO;

@Component
public class RestApiInterceptor /*implements HandlerInterceptor*/{
	
	public static final Log log = LogFactory.getLog(RestApiInterceptor.class.getName());
	
	
	//@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		final String methodName = "afterCompletion";
		if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	//	HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
		int status = response.getStatus();
		String remoteAddress = request.getRemoteAddr();
		String remoteHost = request.getRemoteHost();
		Gson gson = new Gson();
		//Response status - 403 or 401 signifies token validation failed @controller level 
		if(!(status == 401 || status == 403)) {
			String logs = request.getHeader("LogHeader");
			PrintLogsRequestVO printLogsRequestVO = gson.fromJson(logs, PrintLogsRequestVO.class);
			if(printLogsRequestVO != null && printLogsRequestVO.getLogData() != null) {
				printLogsRequestVO.getLogData().stream().forEach(logVO -> ActivityLog.webUILog(logVO , remoteAddress , remoteHost));
			}
		}
		if (log.isDebugEnabled()) {
	         log.debug(methodName, "Exit");
	     }
	}
	
	/* Not in use - below method cannot handle server errors
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
		int status = response.getStatus();
		//Response status - 403 or 401 signifies token validation failed @controller level 
		if(!(status == 401 || status == 403)) {
			String logs = request.getHeader("LogHeader");
			if(!BTSLUtil.isNullString(logs)) {
				String logsBuffer[] = logs.split(",");
				for(String log : logsBuffer) {
					ActivityLog.webUILog(log);
				}
			}
		}
	}*/
}
