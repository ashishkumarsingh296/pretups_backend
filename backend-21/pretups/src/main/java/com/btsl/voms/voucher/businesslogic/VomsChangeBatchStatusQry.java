package com.btsl.voms.voucher.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;

public interface VomsChangeBatchStatusQry {
	
	public String[] changeVoucherStatusPlSqlQry(Connection con ,  VomsBatchVO batchVO,long maxErrorAllowed, int processScreen, MessageResources messages) 
			throws BTSLBaseException,SQLException, ParseException;

	public String[] changeVoucherStatusPlSqlQry(Connection con ,  VomsBatchVO batchVO,long maxErrorAllowed, int processScreen) 
			throws BTSLBaseException,SQLException, ParseException;
	
}
