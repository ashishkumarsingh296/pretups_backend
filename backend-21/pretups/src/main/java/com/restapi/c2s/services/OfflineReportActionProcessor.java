package com.restapi.c2s.services;

import java.util.Locale;

import com.btsl.pretups.channel.transfer.businesslogic.OfflineReportActionResp;
import com.btsl.pretups.common.PretupsI;

public class OfflineReportActionProcessor extends CommonService {
	
	
	
	public void processReportAction(String reportAction,String rptTaskID,OfflineReportActionResp offlineReportActionResp ,Locale locale) {
		 if(reportAction.equals(PretupsI.OFFLINE_REPORTACTION_DELETE)) {
			 processReportDelete(reportAction,offlineReportActionResp ,locale);
		 }
		
		
	}
	
	
   public boolean processReportDelete(String reportAction,OfflineReportActionResp offlineReportActionResp ,Locale locale) {
	   
	return false;
	}
	
	

}
