package com.btsl.pretups.inter.util;

import java.sql.Connection;
import java.sql.SQLException;

public interface VOMSVoucherQry {
	
	public String loadActiveProfilesQry(boolean p_isTimeStamp);
	public String insertDetailsInVoucherAuditQry();
	public String insertDetailsInVoucherAuditListQry();
	public String loadDownloadedVouchersForEnquiryQry(boolean p_IsBatchIdEneterd);
	public String loadActiveProfilesForPrivateRechargeQry(boolean p_isTimeStamp);
	public String loadActiveProfilesSelectQry(boolean p_isTimeStamp);
	public String loadPINAndSerialNumberQry(Connection p_con, VOMSProductVO p_productVO,int p_quantityRequested)throws SQLException;
	public StringBuilder loadVomsVoucherVObyUserId(String productId,String orderBy);
	public String loadVomsVoucherByMasterSerialNumber(String masterSerialNumber);
	public String loadPINAndSerialNumberQryBulk(Connection p_con, VOMSProductVO p_productVO,int p_quantityRequested)throws SQLException;		
}

