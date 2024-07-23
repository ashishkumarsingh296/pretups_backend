package com.restapi.loggers;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;

@Service
public interface LoggerServiceI {

	/**
	 * 
	 * @param p_logs
	 * @param p_request
	 * @throws BTSLBaseException
	 */
	public void printLog(ArrayList<LogVO> p_logs , HttpServletRequest p_request) throws BTSLBaseException;
	
}
