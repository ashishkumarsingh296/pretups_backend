package com.txn.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class C2STransferTxnPostgresQry implements C2STransferTxnQry {
	private Log _log = LogFactory.getLog(this.getClass().getName());
	private String className = "C2STransferTxnPostgresQry";
	private static OperatorUtilI _operatorUtilI = null;

	@Override
	public String loadLastTransfersStatusVOForC2SWithExtRefNumQry() {
		//local_index_implemented
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT C2S.transfer_id,C2S.sender_id, C2S.service_type,C2S.sender_msisdn,C2S.receiver_msisdn,C2S.transfer_value,C2S.transfer_status,C2S.receiver_transfer_value,C2S.receiver_access_fee,C2S.sender_post_balance,  ");
		strBuff.append(" KV.value,C2S.transfer_date_time,P.short_name,P.product_short_code,ST.name ");
		strBuff.append(" FROM c2s_transfers C2S,products P, key_values KV,service_type ST WHERE ");
		//changes fro Performance issue
		strBuff.append(" TRANSFER_DATE >= date_trunc('day',?::TIMESTAMP) AND TRANSFER_DATE <= date_trunc('day',?::TIMESTAMP) AND ");
		strBuff.append(" C2S.REFERENCE_ID=?  ");
		strBuff.append(" AND C2S.product_code=P.product_code AND C2S.transfer_status=KV.key AND KV.type=?  ");
		strBuff.append(" AND C2S.service_type=ST.service_type AND ST.module=? ");

		return strBuff.toString();

	}

	@Override
	public PreparedStatement loadLastXCustTransfersQry(Connection p_con,
			String p_user_id, int p_noLastTxn, String receiverMsisdn, Date p_date)
			throws SQLException {
		//local_index_implemented
		PreparedStatement pstmt = null;
		final String methodName = className + "#loadLastXCustTransfers";
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT transfer_id, transfer_date_time, net_payable_amount, receiver_msisdn, created_on, service, name, 'C2S' AS type, KV.value statusname   ");
		strBuff.append(" FROM (SELECT  CS.transfer_status,CS.transfer_id, CS.transfer_date_time, CS.transfer_value net_payable_amount, CS.receiver_msisdn,");
		strBuff.append(" CS.transfer_date_time created_on, CS.service_type service, ST.name FROM  ");
		strBuff.append(" SERVICE_TYPE ST,C2S_TRANSFERS CS WHERE CS.transfer_date >= ? and CS.ACTIVE_USER_ID=? ");
		if (!BTSLUtil.isNullString(receiverMsisdn)) {
			strBuff.append(" AND CS.receiver_msisdn=? ");
		}
		strBuff.append(" AND CS.service_type=ST.service_type) tmp LEFT JOIN key_values KV ON (tmp.transfer_status=KV.key AND KV.type = ? ) ");
		strBuff.append(" ORDER BY created_on desc LIMIT ? ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		pstmt = p_con.prepareStatement(sqlSelect);
		int i = 0;
		pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_date));
		pstmt.setString(++i, p_user_id);
		if (!BTSLUtil.isNullString(receiverMsisdn)) {
			pstmt.setString(++i, receiverMsisdn);
		}
		pstmt.setString(++i, PretupsI.KEY_VALUE_C2C_STATUS);
		pstmt.setInt(++i, p_noLastTxn - 1);
		return pstmt;

	}

	@Override
	public PreparedStatement getChanneltransAmtDatewiseQry(Connection p_con,
			String p_networkCode, Date p_fromDate, Date p_toDate,
			String p_senderMsisdn, String p_receiverMsisdn, String p_amount)
			throws SQLException {
		//local_index_missing
		PreparedStatement pstmtSelect = null;
		final String methodName = className + "#getChanneltransAmtDatewise";
		String tbl_name = "c2s_transfers";
		try {
			if (!_operatorUtilI.getNewDataAftrTbleMerging(p_fromDate, p_toDate)) {
				tbl_name = "c2s_transfers_old";
			}
		} catch (BTSLBaseException e) {
			_log.errorTrace(methodName, e);
		}
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff
				.append("SELECT  CTRF.transfer_id,CTRF.transfer_date,CTRF.network_code,CTRF.sender_msisdn,CTRF.receiver_msisdn,CTRF.transfer_date_time  ");
		selectQueryBuff
				.append(",CTRF.error_code,CTRF.transfer_status,CTRF.SERVICE_TYPE,CTRF.quantity,CTRF.transfer_value ");
		selectQueryBuff.append(",KV1.VALUE transtatus,ST.NAME servicename ");
		selectQueryBuff
				.append("FROM "
						+ tbl_name
						+ " CTRF RIGHT JOIN KEY_VALUES KV1 ON (KV1.KEY = CTRF.transfer_status AND KV1.TYPE=?), SERVICE_TYPE ST");
		selectQueryBuff.append("WHERE CTRF.transfer_date>=? AND CTRF.transfer_date<=? AND CTRF.network_code=? ");
		selectQueryBuff.append("AND CTRF.sender_msisdn=? ");
		selectQueryBuff.append("AND CTRF.receiver_msisdn=? ");
		// if quantity is all then bypass join on amount
		if (!PretupsI.ALL.equals(p_amount)) {
			selectQueryBuff.append("AND CTRF.quantity=? ");
		}
		selectQueryBuff.append("AND CTRF.SERVICE_TYPE=ST.SERVICE_TYPE ");
		selectQueryBuff.append("ORDER BY CTRF.service_type ");
		String selectQuery = selectQueryBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "select query:" + selectQuery);
		}
		pstmtSelect = p_con.prepareStatement(selectQuery);
		int i = 1;
		pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
		pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
		pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
		pstmtSelect.setString(i++, p_networkCode);
		pstmtSelect.setString(i++, p_senderMsisdn);
		pstmtSelect.setString(i++, p_receiverMsisdn);
		if (!PretupsI.ALL.equals(p_amount)) {
			pstmtSelect.setString(i++, p_amount);
		}

		return pstmtSelect;

	}
	
	@Override
	public PreparedStatement loadLastC2STransfersBySubscriberMSISDNQry(Connection con,String senderMsisdn,String receiverMsisdn,Date fromDate)throws SQLException {
        final String methodName = className + "#loadLastC2STransfersBySubscriberMSISDN";
        PreparedStatement pstmt = null;    
        String statusCheckRequired = null;
        try {
            statusCheckRequired = Constants.getProperty("ENQUIRY_BY_SUBSCRIBER_STATUS_CHECK_REQUIRED");
		} catch (Exception e) {
            if(_log.isDebugEnabled()){
                _log.debug(methodName, "ENQUIRY_BY_SUBSCRIBER_STATUS_CHECK_REQUIRED not defined in Constants.props");
            }
		}
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT transfer_id, sender_id, service_type, sender_msisdn, receiver_msisdn,transfer_value, transfer_status, receiver_transfer_value, VALUE, ");
        strBuff.append("transfer_date_time, short_name, product_short_code, NAME FROM (SELECT   c2s.transfer_id, c2s.sender_id, c2s.service_type, ");
        strBuff.append("c2s.sender_msisdn, c2s.receiver_msisdn, c2s.transfer_value, c2s.transfer_status, c2s.receiver_transfer_value, kv.VALUE, ");
        strBuff.append("c2s.transfer_date_time created_on, p.short_name, p.product_short_code, st.NAME FROM c2s_transfers c2s, products p, key_values kv, service_type st ");
        strBuff.append("WHERE c2s.TRANSFER_DATE >= ? AND c2s.receiver_msisdn = ? AND c2s.sender_msisdn = ? AND c2s.product_code = p.product_code AND c2s.transfer_status = kv.KEY ");
        strBuff.append("AND kv.TYPE = ? AND c2s.service_type = st.service_type AND st.module = ? ");
        if(statusCheckRequired.equalsIgnoreCase(PretupsI.YES)){
            strBuff.append("AND c2s.transfer_status = ?" );
        }
        strBuff.append(" ORDER BY created_on DESC) as result LIMIT 1 ");
        String sqlSelect = strBuff.toString();

        if(_log.isDebugEnabled()){
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        pstmt = con.prepareStatement(sqlSelect);
        int i = 1;
        pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(fromDate));
        pstmt.setString(i++, receiverMsisdn);
        pstmt.setString(i++, senderMsisdn);
        pstmt.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
        pstmt.setString(i++, PretupsI.C2S_MODULE);
        if(statusCheckRequired.equalsIgnoreCase(PretupsI.YES)){
            pstmt.setString(i++, PretupsI.TXN_STATUS_SUCCESS);
        }
        return pstmt;

	}

	@Override
	public PreparedStatement getC2SnProdTxnDetails(Connection con,
			String userId, Date fromDate, Date toDate, String ServiceType,
			String value) throws SQLException {
    	final String methodName="getC2STxnDetailsAll";
		 if (_log.isDebugEnabled())
			 _log.debug(methodName, "Entered  with userId" + userId);
    	 PreparedStatement pstmt = null;
         StringBuilder strBuff = new StringBuilder("SELECT COUNT( TRANSFER_VALUE )transferCount,SUM( TRANSFER_VALUE ) transferValue,TRANSFER_VALUE amount");
         strBuff.append(" FROM C2S_TRANSFERS ct,users u, SERVICE_TYPE ST WHERE ");
         strBuff.append(" u.USER_ID = CT.SENDER_ID AND u.USER_ID = ? AND ct.TRANSFER_DATE BETWEEN ? AND ? ");
         strBuff.append(" AND ST.SERVICE_TYPE = ? AND ct.SERVICE_TYPE = ST.SERVICE_TYPE  AND ct.TRANSFER_STATUS = '200'");
         strBuff.append(" GROUP BY TRANSFER_VALUE ORDER BY transferValue DESC limit ? ");
         if (_log.isDebugEnabled())
        	 _log.debug(methodName, "select Query=" +strBuff);
         pstmt = con.prepareStatement(strBuff.toString());	            
    	 int i = 1;
    	 pstmt.setString(i++, userId);
    	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(fromDate));
    	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(toDate));
    	 pstmt.setString(i++, ServiceType);
    	 pstmt.setInt(i++, Integer.parseInt(value));
    	 return pstmt;	 	    
 	
 }

	@Override
	public String getC2STransferCommissiondetails(C2STransferCommReqDTO c2sTransferCommReqDTO) {
		 final String methodName ="getC2STransferCommissiondetails";		
			StringBuilder c2sTranscommqry = new StringBuilder();
			c2sTranscommqry.append( " SELECT  ct.transfer_date_time transfer_date_time,L3.LOOKUP_NAME AS MOBILE_TYPE, ");
			c2sTranscommqry.append( " adj.commission_type as BONUS_COMMISSION_TYPE , ");
			c2sTranscommqry.append( "  L4.lookup_name  as BONUS_TYPE, ");
			c2sTranscommqry.append( "  L5.lookup_name  as MARGIN_TYPE_DESC, ");
			c2sTranscommqry.append( "  ADJ.REFERENCE_ID, ADJ.adjustment_id, ADJ.MARGIN_AMOUNT as differentialCommission,ADJ.MARGIN_RATE as MARGIN_RATE,ADJ.MARGIN_TYPE as MARGIN_TYPE, adj.USER_CATEGORY ,adj.USER_ID,adj.tax1_value,adj.tax2_value,");
			c2sTranscommqry.append( " adj.otf_rate as cac_rate ,adj.otf_type as cac_type , adj.otf_amount as cac_amount,   ");
			c2sTranscommqry.append(	" ct.transfer_id AS TRANSACTION_ID,	ct.sender_msisdn AS SENDER_MSISDN, u.user_name  AS USER_NAME, cat.CATEGORY_NAME  AS SENDER_CATEGORY_NAME,GD.grph_domain_name as SENDER_GEOGRAPHY,  ");
			c2sTranscommqry.append(	" ( CASE  WHEN ct.SUBS_SID IS NULL THEN ct.receiver_msisdn  ELSE ct.SUBS_SID  END) RECEIVER_MOBILENUM, ");
			c2sTranscommqry.append(	"	st.NAME  AS  SERVICE_NAME, ");
			c2sTranscommqry.append(	"	ct.SENDER_TRANSFER_VALUE AS TRANSFER_AMOUNT, ");
			c2sTranscommqry.append(	"    KV.value as status, ");
			c2sTranscommqry.append(	"    u.network_CODE AS SENDER_NETWORKCODE, ");
			c2sTranscommqry.append(	"  userNetw.network_name as SENDER_NETWORK_NAME, ");
			c2sTranscommqry.append(	"    ( CASE U.parent_id WHEN 'ROOT' THEN ''  ELSE PU.msisdn END )   parent_msisdn, ");
			c2sTranscommqry.append(	"    ( CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END )  parent_name, ");
			c2sTranscommqry.append(	"       ( CASE U.parent_id WHEN  'ROOT'  THEN  N''  ELSE  PC.category_name END ) Parent_category_name, ");
			c2sTranscommqry.append( " ( CASE U.parent_id WHEN 'ROOT' THEN  N'' ELSE  PGD.grph_domain_name  END  ) PARENT_GEOGRAPHY, "); 
			c2sTranscommqry.append( " ( CASE  WHEN U.parent_id = 'ROOT' THEN '' WHEN U.user_id = OU.user_id  THEN  '' ELSE   OU.msisdn  END ) owner_msisdn, "); 
			c2sTranscommqry.append( " ( CASE WHEN U.parent_id = 'ROOT' THEN  N''  WHEN  U.user_id = OU.user_id  THEN   N''  ELSE    OU.user_name END ) owner_name, ");
			c2sTranscommqry.append( " ( CASE  WHEN  U.parent_id = 'ROOT' THEN  N'' WHEN  U.user_id = OU.user_id THEN  N''  ELSE   OC.category_name   END ) Owner_Category, ");
			c2sTranscommqry.append( " (CASE WHEN U.parent_id = 'ROOT' THEN  N'' WHEN   U.user_id = OU.user_id  THEN N''  ELSE     OGD.grph_domain_name  END  ) owner_geography, ");   
			c2sTranscommqry.append( " ( CASE WHEN PU.parent_id = 'ROOT'  or U.parent_id = 'ROOT' THEN   N''  ELSE    GU.user_name   END )   grand_name, "); 
			c2sTranscommqry.append( " ( CASE WHEN PU.parent_id = 'ROOT'  or U.parent_id = 'ROOT' THEN  ''    ELSE  GU.msisdn  END  ) grand_msisdn, "); 
			c2sTranscommqry.append( " ( CASE WHEN PU.parent_id = 'ROOT'  or U.parent_id = 'ROOT' THEN  N''  ELSE   GGD.grph_domain_name   END  )  grand_geo_domain, "); 
			c2sTranscommqry.append( " ( CASE WHEN PU.parent_id = 'ROOT'  or U.parent_id = 'ROOT' THEN   N''  ELSE  GC.category_name END  ) grantParent_Category, ");
			c2sTranscommqry.append( " ( CASE WHEN PU.parent_id = 'ROOT'  or U.parent_id = 'ROOT' THEN    N''    ELSE    GGD.grph_domain_name    END   )   grand_geo_domain, ");
			c2sTranscommqry.append( " ct.receiver_msisdn AS RECEIVER_MOBILENUM, ");
			c2sTranscommqry.append( " sc.service_class_name AS RECEIVER_SERVICECLASS, ");
			c2sTranscommqry.append( "    p.product_name AS PRODUCT_NAME, ");
			c2sTranscommqry.append( "    SC.SERVICE_CLASS_NAME  AS SERVICES, ");
			c2sTranscommqry.append( "    l.selector_name subservice_name, ");
			c2sTranscommqry.append( "     ct.request_gateway_type AS REQUEST_SOURCE, ");
			c2sTranscommqry.append( "      ct.transfer_value AS REQUESTED_AMOUNT, ");
			c2sTranscommqry.append( "        ct.receiver_transfer_value AS credited_Amount, "); 
			c2sTranscommqry.append( "      ct.serial_number AS SERIAL_NUMBER , ");
			c2sTranscommqry.append( "     ct.bonus_amount AS bonus_amount, ");
			c2sTranscommqry.append( "       ct.pin_sent_to_msisdn  AS pinsentTOmsisdn, ");
			c2sTranscommqry.append( "     ct.penalty AS penalty, ");
			//c2sTranscommqry.append( "     ct.transfer_value AS TRANSFER_AMOUNT, ");
			c2sTranscommqry.append( "     u.login_id AS LOGIN_ID, ");
			c2sTranscommqry.append( "     ct.differential_Applicable AS differential_applicable, ");
			c2sTranscommqry.append( "     ct.differential_given AS differential_given, ");
			c2sTranscommqry.append( "      To_char(ct.transfer_date, 'dd-mm-yyyy')      transfer_date, ");
			c2sTranscommqry.append( "         n.network_name as network_Name, ");
			c2sTranscommqry.append( "         kv.value transfer_status, ");
			c2sTranscommqry.append( "   ct.receiver_access_fee, ");
			c2sTranscommqry.append( "      ct.receiver_bonus_value, ");
			c2sTranscommqry.append( "       ct.request_gateway_type, ");
			c2sTranscommqry.append( "        ct.error_code, ");
			c2sTranscommqry.append( "     ct.service_class_id,  ");
			c2sTranscommqry.append( "  up.primary_number msisdn_type, ");
			c2sTranscommqry.append( "      ct.reversal_id, ");
			c2sTranscommqry.append( "      ct.multicurrency_detail, ");
			c2sTranscommqry.append( "      u.external_code , ");
			c2sTranscommqry.append( "     L2.lookup_name     AS request_Gateway_Desc, ");
			c2sTranscommqry.append( "     ct.SENDER_PREVIOUS_BALANCE,ct.SENDER_POST_BALANCE, ct.interface_reference_id   ");
			
			//c2sTranscommqry.append( "     commprof.dual_comm_type  ");
			
			
			//oraclequery changed to postgres
//			c2sTranscommqry.append( " FROM  ( SELECT user_id,parent_id,owner_id FROM USERS CONNECT BY PRIOR user_id = parent_id START WITH user_id=? ) X, ");
//			c2sTranscommqry.append( "        c2s_transfers CT  ");
			
			
			c2sTranscommqry.append(" from   ( with recursive q as( ");
			c2sTranscommqry.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID  "); 
			c2sTranscommqry.append("		 FROM users USR  "); 
			c2sTranscommqry.append("		 where USR.user_id=? "); 
			c2sTranscommqry.append("		    union all  ");
			c2sTranscommqry.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID "); 
			c2sTranscommqry.append("		 FROM users USR join q on q.user_id = USR.parent_id  ");
			c2sTranscommqry.append("		    ) select user_id, parent_id, OWNER_ID from q ) X ,");
			c2sTranscommqry.append( "        c2s_transfers CT  ");
			c2sTranscommqry.append( "  LEFT OUTER JOIN ADJUSTMENTS adj ON  adj.REFERENCE_ID = ct.TRANSFER_ID ");
			c2sTranscommqry.append( " 	LEFT OUTER JOIN Lookups L2 ON l2.lookup_type = 'SRTYP'AND L2.lookup_code = ct.request_gateway_code ");
			c2sTranscommqry.append( "		 LEFT OUTER JOIN Lookups L4 ON l4.lookup_type = 'COTYP'AND L4.lookup_code = adj.commission_type ");
			c2sTranscommqry.append( "			 LEFT OUTER JOIN Lookups L5 ON l5.lookup_type = 'AMTYP' AND L5.lookup_code = adj.margin_type, ");     
			c2sTranscommqry.append( "       networks N, networks userNetw, ");
			c2sTranscommqry.append( "         key_values KV,");
			c2sTranscommqry.append( "       service_type ST, ");
			c2sTranscommqry.append( "       service_type_selector_mapping L, ");
			c2sTranscommqry.append( "        users U, ");
			c2sTranscommqry.append( "           users PU, ");
			c2sTranscommqry.append( "       users OU, users IU , ");  // Intitiator User
			c2sTranscommqry.append( "      categories CAT, ");
			c2sTranscommqry.append( "     user_geographies UG, ");
			c2sTranscommqry.append( "      geographical_domains GD ,  service_classes SC,  ");
			c2sTranscommqry.append( "       user_phones UP,  ");
			c2sTranscommqry.append( "         products p,  ");
			c2sTranscommqry.append( "     CATEGORIES PC,  ");
			c2sTranscommqry.append( "      CATEGORIES OC, "); 
			c2sTranscommqry.append( "     USER_GEOGRAPHIES PUG,  ");
			c2sTranscommqry.append( " 	 GEOGRAPHICAL_DOMAINS PGD ,  ");
			c2sTranscommqry.append( "	 USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,   ");
			c2sTranscommqry.append( "	 USERS GU, USER_GEOGRAPHIES GUG, CATEGORIES GC, GEOGRAPHICAL_DOMAINS GGD ,LOOKUPS L3  ");
			/*
			c2sTranscommqry.append( "	 (SELECT cv.comm_profile_set_id AS comm_profile_set_id,cv.comm_profile_set_version,cv.applicable_from   ");
			c2sTranscommqry.append( "	 ,cv.oth_comm_prf_set_id,ocpf.oth_comm_prf_type,ocpf.oth_comm_prf_type_value,ocpf.OTH_COMM_PRF_SET_NAME ,cv.dual_comm_type AS dual_comm_type ");
			c2sTranscommqry.append( "     FROM commission_profile_set cs,commission_profile_set_version cv  "); 
			c2sTranscommqry.append( "   ,other_comm_prf_set ocpf  ");
			c2sTranscommqry.append( "     WHERE cs.network_code = ?   "); 
			c2sTranscommqry.append( "    AND cs.category_code =? AND cs.comm_profile_set_id = cv.comm_profile_set_id AND "); 
			c2sTranscommqry.append( "  cv.oth_comm_prf_set_id = ocpf.oth_comm_prf_set_id(+) AND   "); 
			c2sTranscommqry.append( "  (cv.applicable_from >=  ? OR cv.applicable_from =(SELECT MAX(cv2.applicable_from)  ");
			c2sTranscommqry.append( " from commission_profile_set_version cv2 WHERE cs.comm_profile_set_id = cv2.comm_profile_set_id and cv2.applicable_from <=  ? )) ");
			c2sTranscommqry.append( " AND  cs.status <> 'N' ORDER BY comm_profile_set_version ) commprof ");
			*/
			c2sTranscommqry.append( "    WHERE    ");
			c2sTranscommqry.append( "      ( ct.transfer_date_time >= ?  AND      ct.transfer_date_time <=  ? )  ");
			c2sTranscommqry.append( "  AND      ct.network_code = ? ");
			c2sTranscommqry.append( " AND  X.USER_ID = ct.sender_id  ");  
			c2sTranscommqry.append( "  AND    sc.service_class_id=ct.service_class_id   ");
			//c2sTranscommqry.append( "  AND commprof.comm_profile_set_id = ct.commission_profile_id "); 
			c2sTranscommqry.append( "  AND      ct.receiver_network_code = n.network_code ");
			c2sTranscommqry.append( "  AND      userNetw.network_code = u.network_code  ");
			c2sTranscommqry.append( " AND PUG.grph_domain_code = PGD.grph_domain_code "); 
			c2sTranscommqry.append( " AND IU.user_id = CT.ACTIVE_USER_ID  ");
			 if(!BTSLUtil.isNullString(c2sTransferCommReqDTO.getUserType())  && c2sTransferCommReqDTO.getUserType().trim().equals(PretupsI.STAFF_USER_TYPE) ) {
				 c2sTranscommqry.append( " AND IU.user_type ='STAFF'  ");
				 c2sTranscommqry.append( " AND  CT.ACTIVE_USER_ID= CASE ? WHEN 'ALL' THEN CT.ACTIVE_USER_ID  ELSE ? END    ");	// here input channel userID. 
			 }else  if(!BTSLUtil.isNullString(c2sTransferCommReqDTO.getUserType())  && c2sTransferCommReqDTO.getUserType().trim().equals(PretupsI.CHANNEL_USER_TYPE) ) { 
				 c2sTranscommqry.append( " AND IU.user_type = 'CHANNEL'  ");
				//  if (c2sTransferCommReqDTO.getChannelUserID()!=null && !c2sTransferCommReqDTO.getChannelUserID().equals(PretupsI.ALL)) {
					  c2sTranscommqry.append( " AND  CT.ACTIVE_USER_ID= CASE ? WHEN 'ALL' THEN CT.ACTIVE_USER_ID  ELSE ? END    ");   // here input channel userID.	 
				 // }
			 }else {
				 c2sTranscommqry.append( " AND IU.user_type IN('STAFF','CHANNEL')"); // ALL OPTION SELECTED
			 }
		    
			c2sTranscommqry.append( " AND PUG.user_id = PU.user_id  "); 
			c2sTranscommqry.append( "  AND PU.user_id = CASE  U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END AND PU.category_code = PC.category_code  "); 
			c2sTranscommqry.append( "  AND OUG.grph_domain_code = OGD.grph_domain_code "); 
			c2sTranscommqry.append( " AND OUG.user_id = OU.user_id "); 
			c2sTranscommqry.append( "  AND OU.user_id = U.owner_id "); 
			c2sTranscommqry.append( " AND OU.category_code = OC.category_code "); 
			c2sTranscommqry.append( "  AND GUG.grph_domain_code = GGD.grph_domain_code "); 
			c2sTranscommqry.append( " AND GUG.user_id = GU.user_id  "); 
			c2sTranscommqry.append( " AND GU.user_id = CASE PU.parent_id  WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END ");
			c2sTranscommqry.append( " AND GU.category_code = GC.category_code "); 
			c2sTranscommqry.append( "   AND ct.transfer_status = KV.KEY   ");
			c2sTranscommqry.append( "  AND      KV.type = 'C2S_STATUS' ");
			c2sTranscommqry.append( "  AND      ct.transfer_status=  CASE ? WHEN 'ALL' THEN ct.transfer_status  ELSE ? END ");
			c2sTranscommqry.append( "  AND      ct.service_type = st.service_type  ");
			c2sTranscommqry.append( " AND      ct.service_type = CASE ? WHEN 'ALL' THEN ct.service_type ELSE ?  END ");
			c2sTranscommqry.append( " AND      ct.sub_service = l.selector_code ");
			c2sTranscommqry.append( " AND      ct.service_type = l.service_type ");
			c2sTranscommqry.append( "  AND      ct.sender_id =   CASE 'ALL'    WHEN 'ALL' THEN ct.sender_id   ELSE 'ALL' END ");
			c2sTranscommqry.append( " AND      u.user_id = ct.sender_id   ");
			c2sTranscommqry.append( " AND      cat.category_code = CASE  ?   WHEN 'ALL' THEN cat.category_code  ELSE ? END ");
			c2sTranscommqry.append( " AND      cat.category_code = u.category_code ");
			c2sTranscommqry.append( " AND      cat.domain_code = ? ");
			c2sTranscommqry.append( " AND      u.user_id = ug.user_id ");
			c2sTranscommqry.append( " AND      u.user_id=up.user_id ");
			c2sTranscommqry.append( " AND      ct.sender_msisdn=up.msisdn ");
			c2sTranscommqry.append( " AND L3.lookup_type = 'MOBTYP' ");
			c2sTranscommqry.append( " AND L3.lookup_code= up.PRIMARY_NUMBER ");
//			c2sTranscommqry.append( " AND L2.lookup_type = 'SRTYP' ");
//			c2sTranscommqry.append( " AND L2.lookup_code= ct.REQUEST_GATEWAY_CODE ");
//			c2sTranscommqry.append( " AND L4.lookup_type = 'COTYP' ");
//			c2sTranscommqry.append( " AND L4.lookup_code= adj.commission_type ");
//			c2sTranscommqry.append( " AND L5.lookup_type = 'AMTYP' ");
//			c2sTranscommqry.append( " AND L5.lookup_code= ADJ.MARGIN_TYPE ");
			c2sTranscommqry.append( " AND PU.user_id = ( CASE u.parent_id  WHEN 'ROOT' THEN u.user_id ELSE u.parent_id  END ) ");
			c2sTranscommqry.append( " AND OU.user_id = U.owner_id ");
			c2sTranscommqry.append( " AND ct.product_code = p.product_code "); 
			c2sTranscommqry.append( " AND p.module_code='C2S' ");
			c2sTranscommqry.append( " AND      ug.grph_domain_code = gd.grph_domain_code ");
			c2sTranscommqry.append( "  AND      ug.grph_domain_code IN  ") ;
			/*
			c2sTranscommqry.append( "  ( SELECT grph_domain_code  FROM   geographical_domains gd1 ");
			c2sTranscommqry.append( "             WHERE  status  IN('Y','S') connect BY prior grph_domain_code = parent_grph_domain_code start WITH grph_domain_code IN  ");
		    c2sTranscommqry.append( "  ( SELECT grph_domain_code FROM   user_geographies ug1  WHERE  ug1.grph_domain_code =  CASE ? WHEN 'ALL' THEN ug1.grph_domain_code ELSE ?   END ");
		    c2sTranscommqry.append( "  AND    ug1.user_id=?)) " */
			
			c2sTranscommqry.append ( "  (   with recursive q as "); 
			c2sTranscommqry.append ( "     ( SELECT grph_domain_code, status from  geographical_domains  WHERE grph_domain_code IN "); 
			c2sTranscommqry.append ( " ( SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code = "); 
			c2sTranscommqry.append ( "  ( case ? when 'ALL'   then    UG1.grph_domain_code else ?  end  ) "); 
			c2sTranscommqry.append ( "     AND UG1.user_id = ?  ) "); 
			c2sTranscommqry.append ( "         union all  ");  
			c2sTranscommqry.append ( "           select m.grph_domain_code, m.status  from geographical_domains m ");   
			c2sTranscommqry.append ( "               join  q   on q.grph_domain_code = m.parent_grph_domain_code  ) "); 
			c2sTranscommqry.append ( "  select  q.grph_domain_code from   q  where   status IN     ('Y','S')  ) "); 
			c2sTranscommqry.append ( " AND ( adj.USER_ID IS  NULL OR (adj.USER_ID IS NOT NULL AND  adj.USER_ID <> 'OPERATOR')) " );
		    if(!BTSLUtil.isEmpty(c2sTransferCommReqDTO.getMobileNumber())  &&  !c2sTransferCommReqDTO.getMobileNumber().trim().toUpperCase().equals(PretupsI.ALL) ) {
			   c2sTranscommqry.append( "  AND     ct.sender_msisdn = ?    ");
			 }
		//    
//		    if(!BTSLUtil.isEmpty(c2sTransferCommReqDTO.getSenderUserID())  &&  !c2sTransferCommReqDTO.getSenderUserID().trim().toUpperCase().equals(PretupsI.ALL) ) {
//		 	   c2sTranscommqry.append( "  AND     ct.sender_id = ?    ");
//		 	    }
		     
		    		c2sTranscommqry.append( " 		 ORDER BY ct.transfer_date_time DESC ");		
			
			  String query = c2sTranscommqry.toString();
			    if(_log.isDebugEnabled()) {
			    	_log.debug(methodName, query);
			    }
					return query;

	}

	@Override
	public String getAddtnlCommSummaryDets(AddtnlCommSummryReqDTO addtnlCommSummryReqDTO) {
		final String methodName ="getAddtnlCommSummaryDets";		
		StringBuilder addtnlCommQuery = new StringBuilder();	
		if(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()!=null && addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_DAILY)) {

			addtnlCommQuery.append( " SELECT SUM(DCTD.transaction_count) transaction_count, SUM(DCTD.differential_amount) differential_amount, ");  
			addtnlCommQuery.append( " DCTD.trans_date trans_date, u.user_id,U.user_name, U.login_id, U.msisdn, C.category_name, "); 
			addtnlCommQuery.append( " GD.grph_domain_name, ");
			addtnlCommQuery.append( " PU.user_id,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name,(CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PC.category_name END) parent_cat, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PGD.grph_domain_name END) parent_geo, ");
			addtnlCommQuery.append( "  (CASE WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name,(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OC.category_name END) owner_category,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OGD.grph_domain_name END)  owner_geo,C.sequence_no, ST.NAME service_type_name, "); 
			addtnlCommQuery.append( "    GU.user_id,GU.user_name grand_name, ");
			addtnlCommQuery.append( "  GU.msisdn grand_msisdn,GGD.grph_domain_name grand_geo_domain,GC.category_name grand_category,stsm.selector_name ");
			addtnlCommQuery.append( "  FROM DAILY_C2S_TRANS_DETAILS DCTD, USERS U, SERVICE_TYPE ST, CATEGORIES C, GEOGRAPHICAL_DOMAINS GD,USER_GEOGRAPHIES UG, ");
			addtnlCommQuery.append( "  USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, ");
			addtnlCommQuery.append( " USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,USERS GU, ");
			addtnlCommQuery.append( " USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD,SERVICE_TYPE_SELECTOR_MAPPING stsm ");
			addtnlCommQuery.append( "  WHERE DCTD.user_id=U.user_id ");
			addtnlCommQuery.append( " AND U.category_code=C.category_code ");
			addtnlCommQuery.append( " AND DCTD.SERVICE_TYPE=ST.SERVICE_TYPE ");
			addtnlCommQuery.append( " AND UG.user_id=U.user_id ");
			addtnlCommQuery.append( " AND UG.grph_domain_code = GD.grph_domain_code ");
			addtnlCommQuery.append( " AND PUG.grph_domain_code = PGD.grph_domain_code ");
			addtnlCommQuery.append( " AND PUG.user_id=PU.user_id ");
			addtnlCommQuery.append( " AND OUG.grph_domain_code = OGD.grph_domain_code ");
			addtnlCommQuery.append( " AND OUG.user_id=OU.user_id ");
			addtnlCommQuery.append( " AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END ");
			addtnlCommQuery.append( " AND PU.category_code=PC.category_code ");
			addtnlCommQuery.append( "  AND OU.user_id=U.owner_id ");
			addtnlCommQuery.append( " AND OU.category_code=OC.category_code ");
			addtnlCommQuery.append( " AND GUG.grph_domain_code = GGD.grph_domain_code ");
			addtnlCommQuery.append( " AND GUG.user_id=GU.user_id ");
			addtnlCommQuery.append( " AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END ");
			addtnlCommQuery.append( "    AND GU.category_code=GC.category_code  ");
			addtnlCommQuery.append( "  AND C.domain_code=? ");
			addtnlCommQuery.append( " AND U.network_code=? ");
			addtnlCommQuery.append( " AND U.category_code =CASE ? WHEN  'ALL' THEN U.category_code ELSE ? END ");
			addtnlCommQuery.append( " AND DCTD.SERVICE_TYPE=CASE ? WHEN  'ALL' THEN DCTD.SERVICE_TYPE ELSE ? END ");
			addtnlCommQuery.append( "  AND DCTD.trans_date>= ? ");
			addtnlCommQuery.append( " AND DCTD.trans_date<= ? ");
			addtnlCommQuery.append( " AND DCTD.service_type=stsm.service_type ");
			addtnlCommQuery.append( " AND DCTD.SUB_SERVICE=stsm.selector_code ");
			addtnlCommQuery.append( "  AND differential_amount <>0 ");
			addtnlCommQuery.append( " AND UG.grph_domain_code IN ( ");
			addtnlCommQuery.append( "  with recursive q as( ");
			addtnlCommQuery.append( "  SELECT grph_domain_code, status "); 
			addtnlCommQuery.append( " FROM GEOGRAPHICAL_DOMAINS "); 
			addtnlCommQuery.append( " where "); 
			addtnlCommQuery.append( " grph_domain_code IN ");
			addtnlCommQuery.append( " (SELECT grph_domain_code ");
			addtnlCommQuery.append( " FROM USER_GEOGRAPHIES ug1 ");
			addtnlCommQuery.append( " WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END ");
			addtnlCommQuery.append( " AND UG1.user_id=?) ");
			addtnlCommQuery.append( " union all ");
			addtnlCommQuery.append( " SELECT gd.grph_domain_code, gd.status "); 
			addtnlCommQuery.append( " FROM GEOGRAPHICAL_DOMAINS gd join q on q.grph_domain_code = gd.parent_grph_domain_code ) ");
			addtnlCommQuery.append( " SELECT grph_domain_code "); 
			addtnlCommQuery.append( " FROM q "); 
			addtnlCommQuery.append( "  WHERE status IN('Y', 'S') ) ");
			addtnlCommQuery.append( " group by DCTD.trans_date , u.user_id,U.user_name, U.login_id, U.msisdn, C.category_name, "); 
			addtnlCommQuery.append( " GD.grph_domain_name,PU.user_id,PU.user_name ,PU.msisdn ,PC.category_name , PGD.grph_domain_name , ");
			addtnlCommQuery.append( " OU.user_name ,OU.msisdn , OC.category_name , OGD.grph_domain_name ,C.sequence_no, ST.NAME , GU.user_id,GU.user_name , ");
			addtnlCommQuery.append( " GU.msisdn ,GGD.grph_domain_name ,GC.category_name,stsm.selector_name,U.parent_id,OU.user_id ");
		
		}else {
			addtnlCommQuery.append( " SELECT sum(MCTD.transaction_count)transaction_count, sum(MCTD.differential_amount) differential_amount, ");
			addtnlCommQuery.append( "   MCTD.trans_date trans_date, u.user_id, U.user_name, U.login_id, U.msisdn, C.category_name, GD.grph_domain_name, ");
			addtnlCommQuery.append( "    PU.user_id,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name,(CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PC.category_name END) parent_cat, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PGD.grph_domain_name END) parent_geo, ");
			addtnlCommQuery.append( "  (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name,(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END)  owner_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OC.category_name END) owner_category,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OGD.grph_domain_name END)  owner_geo , ");
			addtnlCommQuery.append( " C.sequence_no, ST.NAME service_type_name, GU.user_id,GU.user_name grand_name, ");
			addtnlCommQuery.append( "   GU.msisdn grand_msisdn,GGD.grph_domain_name grand_geo_domain,GC.category_name grand_category,stsm.selector_name ");
			addtnlCommQuery.append( "  FROM MONTHLY_C2S_TRANS_DETAILS MCTD, USERS U, SERVICE_TYPE ST, CATEGORIES C, GEOGRAPHICAL_DOMAINS GD,USER_GEOGRAPHIES UG, ");
			addtnlCommQuery.append( "    USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, ");
			addtnlCommQuery.append( "    USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,USERS GU, ");
			addtnlCommQuery.append( "     USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD,SERVICE_TYPE_SELECTOR_MAPPING stsm ");
			addtnlCommQuery.append( "  WHERE MCTD.user_id=U.user_id ");
			addtnlCommQuery.append( "   AND U.category_code=C.category_code ");
			addtnlCommQuery.append( "     AND MCTD.SERVICE_TYPE=ST.SERVICE_TYPE ");
			addtnlCommQuery.append( "    AND UG.user_id=U.user_id ");
			addtnlCommQuery.append( "    AND UG.grph_domain_code = GD.grph_domain_code ");
			addtnlCommQuery.append( "    AND PUG.grph_domain_code = PGD.grph_domain_code ");
			addtnlCommQuery.append( "     AND PUG.user_id=PU.user_id ");
			addtnlCommQuery.append( "   AND OUG.grph_domain_code = OGD.grph_domain_code ");
			addtnlCommQuery.append( "    AND OUG.user_id=OU.user_id ");
			addtnlCommQuery.append( "    AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END ");
			addtnlCommQuery.append( "    AND PU.category_code=PC.category_code ");
			addtnlCommQuery.append( "    AND OU.user_id=U.owner_id ");
			addtnlCommQuery.append( "      AND OU.category_code=OC.category_code ");
			addtnlCommQuery.append( "     AND GUG.grph_domain_code = GGD.grph_domain_code ");
			addtnlCommQuery.append( "      AND GUG.user_id=GU.user_id ");
			addtnlCommQuery.append( "     AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END ");
			addtnlCommQuery.append( "      AND GU.category_code=GC.category_code ");
			addtnlCommQuery.append( "      AND C.domain_code=? "); 
			addtnlCommQuery.append( "     AND U.network_code=? ");
			addtnlCommQuery.append( "     AND U.category_code =CASE ? WHEN  'ALL' THEN U.category_code ELSE ? END ");
			addtnlCommQuery.append( "    AND MCTD.SERVICE_TYPE=CASE ? WHEN  'ALL' THEN MCTD.SERVICE_TYPE ELSE ? END ");
			addtnlCommQuery.append( "    AND MCTD.trans_date>=? ");
			addtnlCommQuery.append( "    AND MCTD.trans_date<=? ");
			addtnlCommQuery.append( "    AND MCTD.service_type=stsm.service_type ");
			addtnlCommQuery.append( "   AND MCTD.sub_service=stsm.selector_code ");
			addtnlCommQuery.append( "    AND MCTD.TRANSACTION_COUNT >0 ");
			addtnlCommQuery.append( "   AND differential_amount <>0 ");
			addtnlCommQuery.append( "   AND UG.grph_domain_code IN ( ");
			addtnlCommQuery.append( "     with recursive q as( ");
			addtnlCommQuery.append( " SELECT grph_domain_code, status "); 
			addtnlCommQuery.append( " FROM GEOGRAPHICAL_DOMAINS "); 
			addtnlCommQuery.append( " where "); 
			addtnlCommQuery.append( "   grph_domain_code IN ");
			addtnlCommQuery.append( "    (SELECT grph_domain_code ");
			addtnlCommQuery.append( "  FROM USER_GEOGRAPHIES ug1 ");
			addtnlCommQuery.append( "  WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END ");
			addtnlCommQuery.append( "   AND UG1.user_id=?) ");
			addtnlCommQuery.append( "   union all ");
			addtnlCommQuery.append( " SELECT gd.grph_domain_code, gd.status "); 
			addtnlCommQuery.append( " FROM GEOGRAPHICAL_DOMAINS gd join q on q.grph_domain_code = gd.parent_grph_domain_code ) ");
			addtnlCommQuery.append( "		SELECT grph_domain_code  "); 
			addtnlCommQuery.append( "	 FROM q "); 
			addtnlCommQuery.append( "  WHERE status IN('Y', 'S') ) ");
			addtnlCommQuery.append( " group by "); 
			addtnlCommQuery.append( "    MCTD.trans_date , u.user_id, U.user_name, U.login_id, U.msisdn, C.category_name, GD.grph_domain_name, ");
			addtnlCommQuery.append( "   PU.user_id,PU.user_name ,PU.msisdn ,PC.category_name , PGD.grph_domain_name , ");
			addtnlCommQuery.append( "     OU.user_name ,OU.msisdn , OC.category_name , OGD.grph_domain_name , ");
			addtnlCommQuery.append( "    C.sequence_no, ST.NAME , GU.user_id,GU.user_name , ");
			addtnlCommQuery.append( "    GU.msisdn ,GGD.grph_domain_name ,GC.category_name,stsm.selector_name,U.parent_id,OU.user_id ");
			   
		}
       return addtnlCommQuery.toString();
	}

	@Override
	public String getPassbookOthersQuery(PassbookOthersReqDTO PassbookOthersReqDTO) {
		final String methodName ="getPassbookOthersQuery";		
		StringBuilder pbquery = new StringBuilder();
		
		pbquery.append("   SELECT CT.trans_date transfer_date,P.product_name product_name, U.user_name,U.msisdn,U.external_code, ");
		pbquery.append("   UP.user_name parent_name,UP.msisdn parent_msisdn, PC.CATEGORY_NAME parentcategoryName,OU.user_name ownerName,CAT.CATEGORY_NAME USERCATEGORY,");
		pbquery.append("   GD.grph_domain_name USEGEOGRPHY ,GP.user_name grand_name,GP.msisdn grand_msisdn,GD1.GRPH_DOMAIN_NAME ParentGeography,GD2.GRPH_DOMAIN_NAME owner_geo,CT.opening_balance,CT.closing_balance,OC.category_name ownercategoryName,ou.msisdn ownermsisdn, ");
		pbquery.append("   CT.O2C_TRANSFER_IN_COUNT   o2cTransferCount, ");
		pbquery.append("   CT.o2c_transfer_in_amount o2cTransferAmount, ");
		pbquery.append("   CT.O2C_RETURN_OUT_COUNT   o2cReturnCount, ");
		pbquery.append("   CT.O2C_RETURN_OUT_AMOUNT  o2cReturnAmount, ");
		pbquery.append("   CT.O2C_WITHDRAW_OUT_COUNT   o2cWithdrawCount, ");
		pbquery.append("   CT.O2C_WITHDRAW_OUT_AMOUNT     o2cWithdrawAmount, ");
		pbquery.append("   CT.C2C_TRANSFER_IN_COUNT    c2cTransfer_InCount, ");
		pbquery.append("   CT.C2C_TRANSFER_IN_AMOUNT c2cTransfer_InAmount, ");
		pbquery.append("   CT.C2C_TRANSFER_OUT_COUNT c2cTransfer_OutCount, ");
		pbquery.append("   CT.C2C_TRANSFER_OUT_AMOUNT c2cTransfer_OutAmount, ");
		pbquery.append("   CT.C2C_RETURN_IN_COUNT c2cTransferRet_InCount, ");
		pbquery.append("   CT.C2C_RETURN_IN_AMOUNT c2cTransferRet_InAmount, ");
		pbquery.append("   CT.C2C_RETURN_OUT_COUNT c2cTransferRet_OutCount, ");
		pbquery.append("   CT.C2C_RETURN_OUT_AMOUNT c2cTransferRet_OutAmount, ");
		pbquery.append("   CT.C2C_WITHDRAW_IN_COUNT c2cTransferWithdraw_InCount, ");
		pbquery.append("   CT.C2C_WITHDRAW_IN_AMOUNT c2cTransferWithdraw_InAmount, ");
		pbquery.append("   CT.C2C_WITHDRAW_OUT_COUNT c2cTransferWithdraw_OutCount, ");
		pbquery.append("   CT.c2s_transfer_out_COUNT ,CT.c2s_transfer_IN_COUNT ,CT.c2s_transfer_IN_AMOUNT ,  ");
		pbquery.append("   CT.c2s_transfer_out_amount,CT.o2c_return_out_amount,CT.o2c_withdraw_out_amount, "); 
		pbquery.append("   CT.c2c_transfer_out_amount,CT.c2c_withdraw_out_amount  ,CT.c2c_return_out_amount, ");
		pbquery.append("   CT.c2c_return_in_amount,CT.c2c_transfer_in_amount,CT.trans_date,OC.CATEGORY_NAME,GC.CATEGORY_NAME,CT.DIFFERENTIAL AS COMMISSION ");
		pbquery.append("  FROM ( with recursive q as( ");
		pbquery.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID  "); 
		pbquery.append("		 FROM users USR  "); 
		pbquery.append("		 where USR.user_id=? "); 
		pbquery.append("		    union all  ");
		pbquery.append("		 SELECT USR.user_id, USR.parent_id, USR.OWNER_ID "); 
		pbquery.append("		 FROM users USR join q on q.user_id = USR.parent_id  ");
		pbquery.append("		    ) select user_id, parent_id, OWNER_ID from q ) X, "); 
		pbquery.append("   DAILY_CHNL_TRANS_MAIN CT, USERS U,CATEGORIES CAT, USER_GEOGRAPHIES UG, "); 
		pbquery.append("   GEOGRAPHICAL_DOMAINS GD,PRODUCTS P,USERS UP,USERS GP,USERS OU,USER_GEOGRAPHIES UGG,USER_GEOGRAPHIES UGW,GEOGRAPHICAL_DOMAINS GD1, ");
		pbquery.append("   GEOGRAPHICAL_DOMAINS GD2,categories PC,categories OC, CATEGORIES GC ");
		pbquery.append("   WHERE  X.user_id = CT.user_id ");
		pbquery.append("   AND CT.user_id = U.user_id ");
		pbquery.append("   AND P.product_code=CT.product_code ");
		pbquery.append("   AND CAT.category_code = U.category_code  ");
		pbquery.append("   AND U.user_id = UG.user_id  ");
		pbquery.append("   AND UG.grph_domain_code = GD.grph_domain_code ");
		pbquery.append("   AND UP.USER_ID=CASE X.parent_id WHEN 'ROOT' THEN X.user_id ELSE X.parent_id END ");
		pbquery.append("   AND GP.USER_ID=CASE UP.parent_id WHEN 'ROOT' THEN UP.user_id ELSE UP.parent_id END ");
		pbquery.append("   AND OU.USER_ID=X.OWNER_ID  ");
		pbquery.append("   AND UGG.user_id=GP.USER_ID  ");
		pbquery.append("   AND UGG.GRPH_DOMAIN_CODE=GD1.GRPH_DOMAIN_CODE ");
		pbquery.append("   AND UGW.USER_ID=OU.USER_ID ");
		pbquery.append("   AND UGW.GRPH_DOMAIN_CODE=GD2.GRPH_DOMAIN_CODE ");
		pbquery.append("   AND PC.CATEGORY_CODE =UP.CATEGORY_CODE ");
		pbquery.append("   AND GC.CATEGORY_CODE =GP.CATEGORY_CODE ");
		pbquery.append("   AND OC.CATEGORY_CODE =OU.CATEGORY_CODE ");
		pbquery.append("   AND CT.network_code = ?  ");
		pbquery.append("   AND CAT.domain_code = ?  "); 
		pbquery.append("   AND CAT.category_code = CASE  ?   WHEN 'ALL' THEN CAT.category_code ELSE   ?  END  "); 
		pbquery.append("   AND CT.user_id =  CASE ? WHEN 'ALL' THEN CT.user_id ELSE ?  END ");   // Input user
		pbquery.append("   AND CT.user_id <> ?   ");    // Logged in userid
		pbquery.append("   AND CT.trans_date >= ? ");
		pbquery.append("   AND CT.trans_date <= ? ");
		pbquery.append("   AND p.product_code = CASE ? WHEN 'ALL' THEN p.product_code ELSE ?  END ");
		pbquery.append("   AND UG.grph_domain_code IN ( ");
		pbquery.append("   with recursive q as( ");
		pbquery.append("		  SELECT gd1.grph_domain_code, gd1.status  "); 
		pbquery.append("		  FROM geographical_domains GD1 ");  
		pbquery.append("		  where grph_domain_code IN  (SELECT grph_domain_code  "); 
		pbquery.append("		  FROM user_geographies UG1  "); 
		pbquery.append("		  WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end)  ");
		pbquery.append("		     AND UG1.user_id=?)  ");
		pbquery.append("		     union all  ");
		pbquery.append("		  SELECT gd1.grph_domain_code, gd1.status  "); 
		pbquery.append("			  FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code    ) ");
		pbquery.append("		  SELECT grph_domain_code  FROM q   WHERE status IN('Y','S')   ) ");
		
	    String query = pbquery.toString();
	    
	    if(_log.isDebugEnabled()) {
	    	_log.debug(methodName, query);
	    }
			return query;

	}

	
			
			

		
}
