package com.btsl.cp2p.login.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.iat.enquiry.businesslogic.IATTransferQry;

public interface CP2PLoginQry {
	
	Log LOG = LogFactory.getLog(IATTransferQry.class.getName());
	String QUERY = "Query : ";
	
	public String loadUserDetailsByMsisdnOrLoginIdQry(String p_msisdn,String p_loginId );
	
	public String loadCP2PSubscriberDetailsQry(String servicetype);

}
