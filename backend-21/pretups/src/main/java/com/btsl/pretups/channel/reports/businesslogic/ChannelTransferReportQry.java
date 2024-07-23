package com.btsl.pretups.channel.reports.businesslogic;

import com.web.pretups.channel.reports.web.UsersReportForm;

public interface ChannelTransferReportQry {
	public String loadVoucherO2CTransferDetailsQry(UsersReportForm thisForm);
	public String queryC2CTransferVoucherDetailsForChannelUser(UsersReportForm thisForm);
	public String queryC2CTransferVoucherDetailsForOperatorUser(UsersReportForm thisForm);
	public String queryC2CNLevelTrackingDetailReport();
	public String queryVoucherAvailabilityDetailsQry(UsersReportForm thisForm);
	public String queryVoucherConsumptionDetailsQry(UsersReportForm thisForm);
	


}
