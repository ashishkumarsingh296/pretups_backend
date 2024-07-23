package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class TransferEnquiryPostgresQry implements TransferEnquiryQry{
	private Log log = LogFactory.getLog(TransferEnquiryPostgresQry.class.getName());

	@Override
	public PreparedStatement loadLastXTransfersOldQryIfC2S(Connection con,
			String recMsisdn, String useId,int noLastTxn,int noDays, java.sql.Date txnDate) throws SQLException {
		String methodName = "loadLastXTransfersOldQry";
		final Date differenceDate = BTSLUtil.getDifferenceDate(new Date(), -noDays); 
		StringBuilder strBuff = new StringBuilder(
				" SELECT transfer_id, transfer_date_time, transfer_status,net_payable_amount, post_balance, receiver_msisdn, created_on, service, name, type, statusname,SHORT_NAME,SUBS_SID from ( ");
		strBuff.append("  select CS.transfer_id, CS.transfer_date_time,CS.SUBS_SID, CS.transfer_status, CS.transfer_value net_payable_amount, CTI.post_balance, CS.receiver_msisdn, ");
		strBuff.append("  CS.transfer_date_time created_on, CS.service_type service, ST.name, 'C2S' AS type, KV.value statusname,CTI.sno,p.SHORT_NAME ");
		strBuff.append("  FROM C2S_TRANSFERS_OLD CS left outer join key_values KV on (CS.transfer_status=KV.key AND KV.type=?) , C2S_TRANSFER_ITEMS CTI,SERVICE_TYPE ST,products p where ");
		if (isRecentDate(noDays,txnDate)) {
			strBuff.append(" CS.transfer_date >= ? AND ");
		}
		strBuff.append(" CS.active_user_id = ? AND CS.transfer_id= CTI.transfer_id ");
		strBuff.append("  AND CTI.sno = (select max(cti1.sno) m from C2S_TRANSFER_ITEMS cti1 where cti1.user_type= ? and CS.transfer_id=CTI1.TRANSFER_ID) ");
		strBuff.append("   AND CS.service_type=ST.service_type  AND cs.PRODUCT_CODE=p.PRODUCT_CODE ");
		if(!BTSLUtil.isNullString(recMsisdn)){
			strBuff.append("  AND CS.receiver_msisdn=? ");
		}
		if (txnDate != null){
			strBuff.append("  AND CS.transfer_date=? ");
		}
		strBuff.append(" ORDER BY created_on desc)  X  limit ? ");
		final String sqlSelect = strBuff.toString();
		LogFactory.printLog(methodName, sqlSelect, log);
		int i = 0;
		PreparedStatement pstmt = con.prepareStatement(sqlSelect);
		pstmt.setString(++i, PretupsI.KEY_VALUE_C2C_STATUS);
		if (isRecentDate(noDays,txnDate)) {
			pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(differenceDate));
		}
		pstmt.setString(++i, useId);
		pstmt.setString(++i, PretupsI.USER_TYPE_SENDER);
		if(!BTSLUtil.isNullString(recMsisdn)){
			pstmt.setString(++i, recMsisdn);
		}
		if (txnDate != null){
			pstmt.setDate(++i, txnDate);
		}
		pstmt.setInt(++i, noLastTxn);

		return pstmt;
	}


	@Override
	public PreparedStatement loadLastXTransfersOldQryIfC2C(Connection con,
			String recMsisdn, String userId, int noLastTxn, int noDays, String service , java.sql.Date txnDate)
					throws SQLException {
		String methodName = "loadLastXTransfersOldQryIfC2C";
		final Date differenceDate = BTSLUtil.getDifferenceDate(new Date(), -noDays); 
		final String []aa = service.split(":");
		StringBuilder strBuff = new StringBuilder(
				" SELECT transfer_id,CLOSE_DATE,status, net_payable_amount, to_msisdn, msisdn, created_on, service, name, type, transfer_sub_type, sender_previous_stock, receiver_previous_stock, approved_quantity, statusname FROM ( ");
		strBuff.append(" SELECT CT.transfer_id, CT.CLOSE_DATE, CT.status , CT.net_payable_amount, CT.to_msisdn, CT.msisdn, CT.created_on, CT.transfer_type service, LK.lookup_name as  name, ");
		strBuff.append(" CT.type, CT.transfer_sub_type, CTI.sender_previous_stock, CTI.receiver_previous_stock , CTI.approved_quantity, KV2.value statusname ");
		strBuff.append( "FROM LOOKUPS LK,CHANNEL_TRANSFERS CT left outer join KEY_VALUES KV2 on (CT.status=KV2.key AND KV2.type =?) , CHANNEL_TRANSFERS_ITEMS CTI WHERE CT.ACTIVE_USER_ID=? ");
		if (isRecentDate(noDays,txnDate)) {
			strBuff.append(" AND CT.transfer_date >= ? ");
		}
		if (aa.length == 2) {
			strBuff.append(" AND CT.transfer_sub_type  = ? ");
		} else if (aa.length == 3) {
			strBuff.append(" AND CT.transfer_sub_type IN (?,?) ");
		}
		strBuff.append("  AND CTI.transfer_id = CT.transfer_id AND LK.lookup_type=? AND CT.type = ? ");
		strBuff.append("  AND LK.lookup_code=CT.TRANSFER_SUB_TYPE  ");
		if (txnDate != null){
			strBuff.append("  AND CT.transfer_date=? ");
		}
		if (!BTSLUtil.isNullString(recMsisdn)){
			strBuff.append("  AND CT.to_msisdn=? ");
		}
		strBuff.append(" ORDER BY created_on desc) X   limit ? ");
		final String sqlSelect1 = strBuff.toString();
		LogFactory.printLog(methodName, sqlSelect1, log);
		int i = 0;
		PreparedStatement pstmt1 = con.prepareStatement(sqlSelect1);
		pstmt1.setString(++i, PretupsI.CHANNEL_TRANSFER_STATUS);
		pstmt1.setString(++i, userId);
		if (isRecentDate(noDays,txnDate)) {
			pstmt1.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(differenceDate));
		}
		if (aa.length == 2) {
			pstmt1.setString(++i, aa[1]);
		} else if (aa.length == 3) {
			pstmt1.setString(++i, aa[1]);
			pstmt1.setString(++i, aa[2]);
		}
		pstmt1.setString(++i, PretupsI.TRANSFER_TYPE);
		pstmt1.setString(++i, PretupsI.TRANSFER_TYPE_C2C);
		if (txnDate != null){
			pstmt1.setDate(++i, txnDate);
		}
		if (!BTSLUtil.isNullString(recMsisdn)){
			pstmt1.setString(++i, recMsisdn);
		}
		pstmt1.setInt(++i, noLastTxn);
		return pstmt1;
	}


	@Override
	public PreparedStatement loadLastXTransfersOldQryIfO2C(Connection con,
			String recMsisdn, String userId, int noLastTxn, int noDays,
			String service, java.sql.Date txnDate) throws SQLException {
		String methodName = "loadLastXTransfersOldQryIfO2C";
		final String aa[] = service.split(":");
		final Date differenceDate = BTSLUtil.getDifferenceDate(new Date(), -noDays); 
		StringBuilder strBuff = new StringBuilder(
				" SELECT transfer_id,CLOSE_DATE, status, approved_quantity, msisdn, to_msisdn, created_on, service, name, type, transfer_sub_type, sender_previous_stock, receiver_previous_stock, statusname FROM ( ");
		strBuff
		.append(" SELECT CT.transfer_id, CT.CLOSE_DATE, CT.status, CT.transfer_sub_type,CTI.approved_quantity, CTI.sender_previous_stock, CTI.receiver_previous_stock, CT.msisdn, CT.to_msisdn, CT.created_on, CT.transfer_type service, LK.lookup_name as  name, ");
		strBuff
		.append(" CT.type, KV2.value statusname FROM LOOKUPS LK,CHANNEL_TRANSFERS CT left outer join KEY_VALUES KV2 on (CT.status=KV2.key AND KV2.type =? ) , CHANNEL_TRANSFERS_ITEMS CTI WHERE (CT.to_user_id = ? OR CT.from_user_id = ? ) ");
		if (isRecentDate(noDays,txnDate)) {
			strBuff.append(" AND CT.transfer_date >= ? ");
		}
		if (aa.length == 2) {
			strBuff.append(" AND CT.transfer_sub_type  = ? ");
		} else if (aa.length == 3) {
			strBuff.append(" AND CT.transfer_sub_type IN (?,?) ");
		}
		strBuff.append("  AND CTI.transfer_id = CT.transfer_id AND LK.lookup_type=? AND CT.type = ? ");
		strBuff.append("  AND LK.lookup_code=CT.TRANSFER_SUB_TYPE  ");
		if (txnDate != null){
			strBuff.append("  AND CT.transfer_date=? ");
		}
		if (!BTSLUtil.isNullString(recMsisdn)){
			strBuff.append("  AND CT.to_msisdn=? ");
		}
		strBuff.append(" ORDER BY created_on desc)   X limit ? ");
		final String sqlSelect2 = strBuff.toString();
		LogFactory.printLog(methodName, sqlSelect2, log);
		int  i = 0;
		PreparedStatement pstmt2 = con.prepareStatement(sqlSelect2);
		pstmt2.setString(++i, PretupsI.CHANNEL_TRANSFER_STATUS);
		pstmt2.setString(++i, userId);
		pstmt2.setString(++i, userId);
		if (isRecentDate(noDays,txnDate)) {
			pstmt2.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(differenceDate));
		}
		if (aa.length == 2) {
			pstmt2.setString(++i, aa[1]);
		} else if (aa.length == 3) {
			pstmt2.setString(++i, aa[1]);
			pstmt2.setString(++i, aa[2]);
		}
		pstmt2.setString(++i, PretupsI.TRANSFER_TYPE);
		pstmt2.setString(++i, PretupsI.TRANSFER_TYPE_O2C);
		if (txnDate != null){
			pstmt2.setDate(++i, txnDate);
		}
		if (!BTSLUtil.isNullString(recMsisdn)){
       	 pstmt2.setString(++i, recMsisdn);
		}
		pstmt2.setInt(++i, noLastTxn);
		return pstmt2;
	}
	
	@Override
	public PreparedStatement loadLastXTransfersNewQryIfC2S(Connection p_con,
			String recMsisdn, String userId, int noLastTxn, int noDays, java.sql.Date txnDate)
			throws SQLException {
		//local_index_implemented
		   final Date differenceDate = BTSLUtil.getDifferenceDate(new Date(), -noDays); // for
		   String lastxC2STxnstatusAllowed = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_C2S_TXNSTATUS_ALLOWED);
		StringBuilder strBuff = new StringBuilder(
                " SELECT transfer_id, transfer_date_time, transfer_status,net_payable_amount, sender_post_balance, receiver_msisdn, created_on, service, name, type, statusname,SHORT_NAME,SUBS_SID from ( ");
            strBuff.append("  select CS.transfer_id, CS.transfer_date_time,CS.SUBS_SID, CS.transfer_status, CS.transfer_value net_payable_amount, CS.sender_post_balance , CS.receiver_msisdn, ");
            strBuff.append("  CS.transfer_date_time created_on, CS.service_type service, ST.name, 'C2S' AS type, KV.value statusname,p.SHORT_NAME ");
            strBuff.append("  from C2S_TRANSFERS CS left outer join key_values KV on (CS.transfer_status=KV.key AND KV.type=? ) ,SERVICE_TYPE ST ,products p where ");
            if (isRecentDate(noDays,txnDate)) {
                strBuff.append(" CS.transfer_date >= ? AND ");
            }
            strBuff.append(" CS.active_user_id = ? ");
            strBuff.append("  AND CS.service_type=ST.service_type AND cs.PRODUCT_CODE=p.PRODUCT_CODE ");
            if(!BTSLUtil.isNullString(lastxC2STxnstatusAllowed))
    			strBuff.append("  	AND CS.transfer_status in (?)");
            if(!BTSLUtil.isNullString(recMsisdn)){
            	strBuff.append("  AND CS.receiver_msisdn=? ");
            }
            if (txnDate != null){
    			strBuff.append("  AND CS.transfer_date=? ");
    		}
            strBuff.append(" ORDER BY created_on desc) X  limit ? ");
            final String sqlSelect = strBuff.toString();
            
            int i = 0;
            PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(++i, PretupsI.KEY_VALUE_C2C_STATUS);
    		if (isRecentDate(noDays,txnDate)) {
                pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(differenceDate));
            }
            pstmt.setString(++i, userId);
            if(!BTSLUtil.isNullString(lastxC2STxnstatusAllowed))
            	pstmt.setString(++i, lastxC2STxnstatusAllowed);
			if(!BTSLUtil.isNullString(recMsisdn)){
            	pstmt.setString(++i, recMsisdn);
            }
			if (txnDate != null){
				pstmt.setDate(++i, txnDate);
			}
            pstmt.setInt(++i, noLastTxn);
		return pstmt;
	}
	
	@Override
	public PreparedStatement loadLastXTransfersNewQryIfC2C(Connection con,
			String recMsisdn, String userId, int noLastTxn, int noDays,
			String service, java.sql.Date txnDate) throws SQLException {
		final Date differenceDate = BTSLUtil.getDifferenceDate(new Date(), -noDays); 
		final String aa[] = service.split(":");
		String lastxChnlTxnstatusAllowed = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_CHNL_TXNSTATUS_ALLOWED);
		StringBuilder strBuff = new StringBuilder(
				" SELECT transfer_id,CLOSE_DATE,status, net_payable_amount, to_msisdn, msisdn, created_on, service, name, type, transfer_sub_type, sender_previous_stock, receiver_previous_stock, approved_quantity, statusname FROM ( ");
		strBuff.append(" SELECT CT.transfer_id, CT.CLOSE_DATE, CT.status , CT.net_payable_amount, CT.to_msisdn, CT.msisdn, CT.created_on, CT.transfer_type service, LK.lookup_name as name, ");
		strBuff.append(" CT.type, CT.transfer_sub_type, CTI.sender_previous_stock, CTI.receiver_previous_stock , CTI.approved_quantity, KV2.value statusname " );
		strBuff.append( "FROM LOOKUPS LK,CHANNEL_TRANSFERS CT left outer join KEY_VALUES KV2 on (CT.status=KV2.key AND KV2.type =?) , CHANNEL_TRANSFERS_ITEMS CTI WHERE (CT.ACTIVE_USER_ID=? OR CT.TO_USER_ID=?) ");
		if (isRecentDate(noDays,txnDate)) {
			strBuff.append(" AND CT.transfer_date >= ? ");
		}
		if (aa.length == 2) {
			strBuff.append(" AND CT.transfer_sub_type  = ? ");
		} else if (aa.length == 3) {
			strBuff.append(" AND CT.transfer_sub_type IN (?,?) ");
		}
		strBuff.append("  AND CTI.transfer_id = CT.transfer_id AND LK.lookup_type=? AND CT.type = ? ");
		strBuff.append("  AND LK.lookup_code=CT.TRANSFER_SUB_TYPE  ");
		if(!BTSLUtil.isNullString(lastxChnlTxnstatusAllowed))
        	strBuff.append("  AND CT.status in (?) ");
		if (txnDate != null){
			strBuff.append("  AND CT.transfer_date=? ");
		}
		if (!BTSLUtil.isNullString(recMsisdn)){
			strBuff.append("  AND CT.to_msisdn=? ");
		}
		strBuff.append(" ORDER BY created_on desc) X  limit ? ");
		final String sqlSelect1 = strBuff.toString();

		int i = 0;
		PreparedStatement pstmt1 = con.prepareStatement(sqlSelect1);
		pstmt1.setString(++i, PretupsI.CHANNEL_TRANSFER_STATUS);
		pstmt1.setString(++i, userId);
	pstmt1.setString(++i, userId);
		if (isRecentDate(noDays,txnDate)) {
			pstmt1.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(differenceDate));
		}
		if (aa.length == 2) {
			pstmt1.setString(++i, aa[1]);
		} else if (aa.length == 3) {
			pstmt1.setString(++i, aa[1]);
			pstmt1.setString(++i, aa[2]);
		}
		pstmt1.setString(++i, PretupsI.TRANSFER_TYPE);
		pstmt1.setString(++i, PretupsI.TRANSFER_TYPE_C2C);
		if(!BTSLUtil.isNullString(lastxChnlTxnstatusAllowed))
        	pstmt1.setString(++i, lastxChnlTxnstatusAllowed);
		if (txnDate != null){
			pstmt1.setDate(++i, txnDate);
		}
		if (!BTSLUtil.isNullString(recMsisdn)){
			pstmt1.setString(++i, recMsisdn);
		}
		pstmt1.setInt(++i, noLastTxn);
		return pstmt1;
	}

	@Override
	public PreparedStatement loadLastXTransfersNewQryIfO2C(Connection con,
			String recMsisdn, String userId, int noLastTxn, int noDays,
			String service, java.sql.Date txnDate) throws SQLException {
		String methodName = "loadLastXTransfersNewQryIfO2C";
		final String []aa = service.split(":");
		final Date differenceDate = BTSLUtil.getDifferenceDate(new Date(), -noDays); 
		String lastxChnlTxnstatusAllowed = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_CHNL_TXNSTATUS_ALLOWED);
		 StringBuilder strBuff = new StringBuilder(
             " SELECT transfer_id,CLOSE_DATE, status, approved_quantity, msisdn, to_msisdn, created_on, service, name, type, transfer_sub_type, sender_previous_stock, receiver_previous_stock, statusname FROM ( ");
         strBuff.append(" SELECT CT.transfer_id, CT.CLOSE_DATE, CT.status, CT.transfer_sub_type,CTI.approved_quantity, CTI.sender_previous_stock, CTI.receiver_previous_stock, CT.msisdn, CT.to_msisdn, CT.created_on, CT.transfer_type service, LK.lookup_name as name, ");
         strBuff.append(" CT.type, KV2.value statusname FROM LOOKUPS LK,CHANNEL_TRANSFERS CT left outer join KEY_VALUES KV2 on (CT.status=KV2.key AND KV2.type =?) , CHANNEL_TRANSFERS_ITEMS CTI WHERE (CT.to_user_id = ? OR CT.from_user_id = ? ) ");
         if (isRecentDate(noDays,txnDate)) {
             strBuff.append(" AND CT.transfer_date >= ? ");
         }
         if (aa.length == 2) {
             strBuff.append(" AND CT.transfer_sub_type  = ? ");
         } else if (aa.length == 3) {
             strBuff.append(" AND CT.transfer_sub_type IN (?,?) ");
         }
         strBuff.append("  AND CTI.transfer_id = CT.transfer_id AND LK.lookup_type=? AND CT.type = ? ");
         strBuff.append("  AND LK.lookup_code=CT.TRANSFER_SUB_TYPE ");
         if(!BTSLUtil.isNullString(lastxChnlTxnstatusAllowed))
          	strBuff.append("  AND CT.status in (?) ");
		 if (txnDate != null){
  			strBuff.append("  AND CT.transfer_date=? ");
  		}
  		if (!BTSLUtil.isNullString(recMsisdn)){
 			strBuff.append("  AND CT.to_msisdn=? ");
 		}
         strBuff.append(" ORDER BY created_on desc)  X  limit ? ");
         final String sqlSelect2 = strBuff.toString();
         LogFactory.printLog(methodName, sqlSelect2, log);
         int i = 0;
         PreparedStatement pstmt2 = con.prepareStatement(sqlSelect2);
         pstmt2.setString(++i, PretupsI.CHANNEL_TRANSFER_STATUS);
         pstmt2.setString(++i, userId);
         pstmt2.setString(++i, userId);
 		if (isRecentDate(noDays,txnDate)) {
             pstmt2.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(differenceDate));
         }
         if (aa.length == 2) {
             pstmt2.setString(++i, aa[1]);
         } else if (aa.length == 3) {
             pstmt2.setString(++i, aa[1]);
             pstmt2.setString(++i, aa[2]);
         }
         pstmt2.setString(++i, PretupsI.TRANSFER_TYPE);
         pstmt2.setString(++i, PretupsI.TRANSFER_TYPE_O2C);
         if(!BTSLUtil.isNullString(lastxChnlTxnstatusAllowed))
        	 pstmt2.setString(++i, lastxChnlTxnstatusAllowed);
		 if (txnDate != null){
        	 pstmt2.setDate(++i, txnDate);
 		}
         if (!BTSLUtil.isNullString(recMsisdn)){
        	 pstmt2.setString(++i, recMsisdn);
 		}
         pstmt2.setInt(++i, noLastTxn);
		return pstmt2;
	}



	private boolean isRecentDate(int noDays, java.sql.Date txnDate) {
		if(noDays != 0 && txnDate == null)
			return true;
		else
			return false;
	}

	public PreparedStatement loadLastNDaysEVDTrfDetailsQry(Connection p_con, int noDays, String serviceType,
			String senderMsisdn, String serialNumber, String denomination, LocalDate transfer_date) throws BTSLBaseException, SQLException {
		final String methodName = "loadLastNDaysEVDTrfDetailsQry";
		final StringBuffer sbr = new StringBuffer();
		sbr.append(
				"SELECT TO_CHAR(TRANSFER_DATE_TIME, ?) TRANSFER_DATE, SERIAL_NUMBER, (TRANSFER_VALUE) TRANSFER_VALUE ");
		sbr.append("FROM C2S_TRANSFERS WHERE SERVICE_TYPE = ? AND SENDER_MSISDN = ? ");
		sbr.append("AND TRANSFER_DATE >= ? ");
		if (!BTSLUtil.isNullString(serialNumber)) {
			sbr.append("AND SERIAL_NUMBER = ? ");
		}
		if (!BTSLUtil.isNullString(denomination)) {
			sbr.append("AND TRANSFER_VALUE = ? ");
		}
		sbr.append("ORDER BY TRANSFER_DATE DESC");
		String sqlSelect = sbr.toString();
		if (log.isDebugEnabled()) {
			log.debug(methodName, "sql query = " + sqlSelect);
		}

		int i = 1;
		PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
		pstmt.setString(i++, Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++, serviceType);
		pstmt.setString(i++, senderMsisdn);
		pstmt.setDate(i++, java.sql.Date.valueOf(transfer_date.toString()));
		if (!BTSLUtil.isNullString(serialNumber)) {
			pstmt.setString(i++, serialNumber);
		}
		if (!BTSLUtil.isNullString(denomination)) {
			pstmt.setDouble(i++, (Double.parseDouble(denomination) * ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()));
		}
		
		return pstmt;
	}
}
