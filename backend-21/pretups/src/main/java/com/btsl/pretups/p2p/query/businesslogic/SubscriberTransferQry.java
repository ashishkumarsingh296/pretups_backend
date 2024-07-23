package com.btsl.pretups.p2p.query.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.transfer.businesslogic.TransferVO;

/**
 * SubscriberTransferQry
 */
public interface SubscriberTransferQry {

	Log LOG = LogFactory.getLog(SubscriberTransferQry.class.getName());
	
	public PreparedStatement loadSubscriberDetails(Connection pCon, TransferVO subscriberVO)throws SQLException,ParseException;
	
	public PreparedStatement loadSubscriberItemList(Connection pCon, TransferVO transferVO)throws SQLException,ParseException;
	
}
