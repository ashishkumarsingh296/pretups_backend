package com.btsl.pretups.p2p.query.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

/**
 * P2PQueryHistoryPostgresQry
 */
public class P2PQueryHistoryPostgresQry implements P2PQueryHistoryQry {

	@Override
	public PreparedStatement loadSubscriberDetails(Connection pCon,
			P2pQueryHistoryVO pqueryHistoryVO) throws SQLException, ParseException{
		
		
		PreparedStatement pstmtSelect;		
		 
		StringBuilder  strBuff = new StringBuilder("SELECT activated_on, billing_cycle_date, billing_type, buddy_seq_number,");
        strBuff.append(" consecutive_failures, PSUB.created_by, PSUB.created_on, credit_limit,");
        strBuff.append("  last_success_transfer_date, last_transfer_amount, last_transfer_id,");
        strBuff.append(" last_transfer_msisdn, last_transfer_on, last_transfer_status, last_transfer_type,");
        strBuff.append(" PSUB.modified_by, PSUB.modified_on, msisdn,");
        strBuff.append(" network_code, pin, pin_block_count, prefix_id, registered_on, ");
        strBuff.append(" service_class_code, skey_required,  subscriber_type, total_transfer_amount,");
        strBuff.append(" total_transfers, user_id, user_name, ");
        strBuff.append(" LOOK.lookup_name status");
        strBuff.append(" FROM lookups LOOK right outer join p2p_subscribers_history PSUB on (LOOK.lookup_code = PSUB.status AND LOOK.lookup_type = ?)");
        strBuff.append(" WHERE msisdn = ?");
        
        
        int i = 0;
       
        boolean flag= false;
      
        if ((pqueryHistoryVO.getFromDate().trim().length() > 0) && (pqueryHistoryVO.getSubscriberMsisdn().length() > 0)) {
          
            strBuff.append(" AND (date_trunc('day',PSUB.modified_on::timestamp)>=? AND date_trunc('day',PSUB.modified_on::timestamp)<=?)");
            strBuff.append(" ORDER BY PSUB.modified_on");
           
            flag =true;
            
        }
        
        LogFactory.printLog("P2PQueryHistoryPostgresQry", strBuff.toString(), LOG);		
        
        pstmtSelect = pCon.prepareStatement(strBuff.toString());        
        pstmtSelect.setString(++i, PretupsI.USER_STATUS_TYPE);
        pstmtSelect.setString(++i, pqueryHistoryVO.getSubscriberMsisdn());
        
        if(flag){
        	pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pqueryHistoryVO.getFromDate())));
            pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pqueryHistoryVO.getToDate())));
        }
        
        
		
		return pstmtSelect;
		
	
	}

}
