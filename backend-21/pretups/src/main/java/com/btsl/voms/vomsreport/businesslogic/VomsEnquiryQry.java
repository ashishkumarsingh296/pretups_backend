package com.btsl.voms.vomsreport.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface VomsEnquiryQry {
	
	public PreparedStatement getVoucherEnquiry_oldQry(Connection p_con, String p_voucherType, String p_serialNo) throws SQLException;
	public String getVoucherEnquiry_oldSelectQry();
	public String getVoucherEnquiry_newQry(String p_voucherType);
	public String getVoucherEnquiry_newSelectQry();
	public String loadReconcillationReportListQry();
	public String loadUserCategoryList();
	public String loadMRPofVomsProducts();
}
