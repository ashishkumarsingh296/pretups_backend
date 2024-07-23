package com.btsl.voms.vomsreport.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.btsl.voms.vomsreport.web.VomsReportForm;

public interface VomsRptQry {

	
	public PreparedStatement getVCDetailQuery(Connection con,VomsReportForm vomsReportForm);
	public PreparedStatement getSoldVCCardQuery(Connection con,VomsReportForm vomsReportForm); 
	public PreparedStatement getVCDeliveryHistoryQuery(Connection con,VomsReportForm vomsReportForm);
	public PreparedStatement getVCSoldSummaryCityQuery(Connection con,VomsReportForm vomsReportForm);
	public PreparedStatement getVCSoldSummaryBankQuery(Connection con,VomsReportForm vomsReportForm);
	public PreparedStatement getVCSoldSummaryChannelQuery(Connection con,VomsReportForm vomsReportForm);
	public PreparedStatement getVCSoldSummarySoldDateQuery(Connection con,VomsReportForm vomsReportForm);
	public PreparedStatement getStatisticsRechargeQuery(Connection con,VomsReportForm vomsReportForm);
	public PreparedStatement getStatisticsServiceQuery(Connection con,VomsReportForm vomsReportForm);
	public PreparedStatement getVCStatisticsQuery(Connection con,VomsReportForm vomsReportForm);
}
