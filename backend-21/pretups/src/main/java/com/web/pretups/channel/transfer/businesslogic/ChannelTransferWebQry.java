package com.web.pretups.channel.transfer.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.loyalitystock.businesslogic.LoyalityStockQry;

public interface ChannelTransferWebQry {
	Log LOG = LogFactory.getLog(LoyalityStockQry.class.getName());
	String QUERY = "Query : ";
	String loadO2CChannelTransfersListQry(String isPrimary,String p_transferID,String p_userCode, String p_transferTypeCode, String p_transferCategory );
}
