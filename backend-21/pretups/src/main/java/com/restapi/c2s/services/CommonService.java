package com.restapi.c2s.services;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.BaseRequestdata;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.PBDownloadReqdata;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookDownloadResp;
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OfflineReportDAO;

/**
 * 
 * @author Subesh KCV
 *
 */


public abstract class CommonService  {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	
	protected BaseRequestdata baseRequestdata;
	protected BaseResponseMultiple baseResponseMultiple;

	
		
	
	public void execute(BaseRequestdata baseRequestData,
			BaseResponseMultiple baseResponseData) throws BTSLBaseException {
		final String methodName="execute";
		_log.info(methodName, "Inside Common Service class");
		
	}
	
	
	/**
	 * 
	 * method precheckOfflineValidations
	 * @param con
	 * @param loggedInUserID
	 * @param reportID
	 * @param response 
	 * @throws BTSLBaseException
	 * **/		
	public void precheckOfflineValidations(Connection con ,String loggedInUserID,String reportID) throws BTSLBaseException {
		final String methodName ="precheckOfflineValidations";
		OfflineReportDAO offlineReportDAO= new OfflineReportDAO(); 
		
		boolean allowSameReportExecution = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_SAME_REPORT_EXEC);
		Integer totalReporExecInParallelPerUser = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.TOT_RPT_EXEC_PERUSER);
		
		 if(!allowSameReportExecution  && offlineReportDAO.checkSameReportAlreadyExecuting(con, reportID,loggedInUserID)) {
			 throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.SAME_REPORT_ALREADY_EXECUTING, 0, null); 
		 }
		
		 int totCount =offlineReportDAO.getTotCountRprtRunningParallelByUser(con, loggedInUserID); 
		  if(totCount>totalReporExecInParallelPerUser.intValue()) {
				throw new BTSLBaseException(PretupsUIReportsServiceImpl.class.getName(), methodName,
	        			PretupsErrorCodesI.TOTAL_ALLOWED_RPT_EXEC, new String[] {String.valueOf(totalReporExecInParallelPerUser.intValue())});
		  }
		
	}

	
	public  void  validateEditColumns(List<DispHeaderColumn> requestEditColumns ,Map<String,String> validEditColumns) throws BTSLBaseException {
		final String METHOD_NAME="validateEditColumns";
		String invalidColumnSeq=null;
		StringBuilder invalidDisplaymapcoloumns = new StringBuilder();
		
		for(DispHeaderColumn dispheaderColumn : requestEditColumns) {
			if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,  "->" + dispheaderColumn.getColumnName() + " -> " + dispheaderColumn.getDisplayName());
			}
			 if (!validEditColumns.containsKey(dispheaderColumn.getColumnName()) ) {
				 invalidDisplaymapcoloumns.append(dispheaderColumn.getColumnName()).append(",");
			 }
			 
			 
		}
		 
		if(invalidDisplaymapcoloumns!=null && invalidDisplaymapcoloumns.toString().trim().length()>0) {
			invalidColumnSeq = invalidDisplaymapcoloumns.toString().substring(0, invalidDisplaymapcoloumns.toString().length() - 1);
		throw new BTSLBaseException(CommonService.class.getName(), METHOD_NAME,
				PretupsErrorCodesI.INVALID_REPORT_DISPLAY_COLS, new String[] {invalidColumnSeq});
		}
	 }








}