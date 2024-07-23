package com.btsl.pretups.p2p.query.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
/**
 * ReceiverTransferQry
 */
public interface ReceiverTransferQry {
	
	Log LOG = LogFactory.getLog(ReceiverTransferQry.class.getName());
	
	public PreparedStatement loadReceiverDetails(Connection pCon, TransferVO preceiverVO)throws SQLException,ParseException;
	

}
