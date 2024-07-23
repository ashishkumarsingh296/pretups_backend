package com.txn.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;

public class ChannelUserTxnPostgresQry implements ChannelUserTxnQry {
	@Override
	public String loadChannelUserDetailsForTransferIfReqExtgwQry(String extCode){
		Boolean isTrfRuleUserlevelAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		 StringBuilder sqlBuffer = new StringBuilder(" SELECT cpsv.applicable_from, u.user_id, u.user_name, u.network_code,l.network_name, ");
	        sqlBuffer.append("u.login_id, u.password, u.category_code, u.parent_id, u.owner_id, u.msisdn, u.allowed_ip, ");
	        sqlBuffer.append("u.allowed_days,u.from_time,u.to_time,u.last_login_on,u.employee_code,u.status userstatus, ");
	        sqlBuffer.append("u.email,u.created_by,u.created_on,u.modified_by,u.modified_on,u.pswd_modified_on,u.company,u.fax,u.firstname,u.lastname, ");// firstname,lastname,company,fax
	        sqlBuffer.append("cusers.contact_person,u.contact_no,u.designation,u.division,u.department, ");
	        sqlBuffer.append("u.user_type,cusers.in_suspend,cusers.out_suspend, u.address1,u.address2,u.city,u.state, ");
	        sqlBuffer.append("u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id, ");
	        sqlBuffer.append("l.status networkstatus,l.language_1_message,cat.hierarchy_allowed, cat.category_type, ");
	        sqlBuffer.append("cat.category_code,cat.category_name,cat.domain_code,cat.sequence_no, ");
	        sqlBuffer.append("cat.multiple_login_allowed, cat.max_login_count,cat.view_on_network_block, ");
	        sqlBuffer.append("cat.grph_domain_type, cat.multiple_grph_domains, cat.fixed_roles, cat.user_id_prefix, ");
	        sqlBuffer.append("cusers.comm_profile_set_id,cusers.transfer_profile_id,cusers.user_grade, ");
	        sqlBuffer.append("cps.comm_profile_set_name , cg.grade_name , cpsv.comm_profile_set_version, ");
	        sqlBuffer.append("tp.profile_name , ug.grph_domain_code, dm.domain_name , GD.grph_domain_name, ");
	        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
	        sqlBuffer.append("cps.language_2_message  comprf_lang_2_msg,dm.domain_type_code ,cusers.sos_allowed,cusers.sos_allowed_amount,cusers.sos_threshold_limit  ");
	        if (isTrfRuleUserlevelAllow) {
	            sqlBuffer.append(", cusers.trf_rule_type  ");
	        }
	        sqlBuffer.append(" ,cpsv.dual_comm_type ");
	        sqlBuffer.append("FROM users u,networks l,categories cat,channel_users cusers,commission_profile_set cps, ");
	        sqlBuffer.append("channel_grades cg, commission_profile_set_version cpsv , ");
	        sqlBuffer.append("transfer_profile tp, user_geographies ug , domains dm, geographical_domains GD ");
	        sqlBuffer.append("WHERE ");
	        if (!BTSLUtil.isNullString(extCode)) {
	            sqlBuffer.append("u.external_code= ?");
	        } else {
	            sqlBuffer.append("u.login_id = ?");
	        }
	        sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' AND cat.status='Y' AND  U.network_code=L.network_code  ");
	        sqlBuffer.append(" AND u.user_id=cusers.user_id AND cat.category_code=U.category_code AND ");
	        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND cg.grade_code = cusers.user_grade AND ");
	        sqlBuffer.append(" cpsv.comm_profile_set_id = cps.comm_profile_set_id AND tp.profile_id = cusers.transfer_profile_id  ");
	        sqlBuffer.append(" AND ug.user_id = u.user_id AND dm.domain_code = cat.domain_code AND ug.grph_domain_code = GD.grph_domain_code  ");
	        sqlBuffer.append(" AND cpsv.applicable_from =COALESCE ( (SELECT MAX(applicable_from) ");
	        sqlBuffer.append(" FROM commission_profile_set_version  ");
	        sqlBuffer.append(" WHERE applicable_from<=? AND comm_profile_set_id=cusers.comm_profile_set_id),cpsv.applicable_from) ");
	        return sqlBuffer.toString();
	}
	@Override
	public PreparedStatement loadOtherUserBalanceVOQry(Connection con, String userCode, ChannelUserVO channelUserVO) throws SQLException{
		StringBuilder strBuff = new StringBuilder();

		strBuff.append("WITH RECURSIVE Q AS ( ");
		strBuff.append(" SELECT U.user_id as user_id , U.user_code , U.status,  U.Category_code  U_Category_code ,U.user_name as user_name,U.owner_id as owner_id,U.Parent_id as Parent_id,C.sequence_no as sequence_no , C.category_code as category_code, C.category_type as category_type ");
		strBuff.append(" FROM users U,categories C ");
		strBuff.append(" WHERE ");
		strBuff.append("   user_id=? ");
		strBuff.append(" UNION ALL SELECT U.user_id, U.user_code ,  U.status,  U.Category_code  U_Category_code  ,U.user_name ,U.owner_id ,U.Parent_id,C.sequence_no, C.category_code, C.category_type ");
		strBuff.append(" from users U join Q on (U.user_id=Q.Parent_id),categories C");
		strBuff.append(" ) SELECT  user_id , user_name, owner_id,Parent_id,sequence_no , category_code, category_type  FROM Q ");
		strBuff.append(" where user_code=? AND U_Category_code=category_code ");
		strBuff.append(" AND status <> 'N' AND status <> 'C'  ");
		if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserVO.getCategoryVO().getCategoryType())) {
	            strBuff.append(" AND user_id<> ? ");
	    }

        String selectQuery = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadOtherUserBalanceVOQry", "Select Query= " + selectQuery);
        }
        PreparedStatement pstmt = con.prepareStatement(selectQuery);
        pstmt.setString(1, channelUserVO.getParentID());
        pstmt.setString(2, userCode);
        if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserVO.getCategoryVO().getCategoryType())) 
            pstmt.setString(3, channelUserVO.getParentID());
       
        
        return pstmt;
	}
	
	@Override
	public String loadUserChannelInTransferListQry(ChannelUserVO channelUserVO){
	 	StringBuilder strBuff = new StringBuilder("SELECT product_short_code,short_name,product_code,sum(transfers)transfers,sum(returns1)returns1 ");
        strBuff.append("FROM");
        strBuff.append("(SELECT /*+ INDEX(CT) */ P.product_short_code product_short_code, P.short_name short_name,CTI.product_code product_code,");
        strBuff.append(" sum(CASE WHEN CT.transfer_type=? THEN approved_quantity ELSE 0 END)transfers,");
        strBuff.append(" sum(CASE WHEN CT.transfer_type=? THEN approved_quantity ELSE 0 END)returns1");
        strBuff.append(" FROM channel_transfers CT,channel_transfers_items CTI,products P ");
        strBuff.append(" WHERE CT.transfer_date=? AND CT.status=? AND CT.to_user_id=?");
        if (channelUserVO.isStaffUser()) {
            strBuff.append(" AND CT.active_user_id=? ");
        }
        strBuff.append(" AND  CT.transfer_id=CTI.transfer_id AND CTI.product_code=P.product_code");
        strBuff.append(" GROUP BY CTI.product_code,P.product_short_code,P.short_name ");
        strBuff.append(" UNION all ");
        strBuff.append(" SELECT P.product_short_code product_short_code,P.short_name short_name,P.product_code product_code,0 transfers,0 returns1 ");
        strBuff.append(" FROM products P ,network_product_mapping NPM ");
        strBuff.append(" WHERE NPM.network_code=? AND P.product_code=NPM.product_code AND module_code=?) as q ");
        strBuff.append(" GROUP BY  product_short_code,short_name,product_code ");
       return strBuff.toString();
	}
	
	@Override
	public String loadUserChannelOutTransferListQry(ChannelUserVO channelUserVO){
		StringBuilder strBuff = new StringBuilder("SELECT product_short_code,short_name,product_code,sum(transfers)transfers,sum(returns1)returns1 ");
        strBuff.append("FROM");
        strBuff.append("(SELECT /*+ INDEX(CT) */ P.product_short_code product_short_code,P.short_name short_name,CTI.product_code product_code,");
        strBuff.append(" sum(CASE WHEN CT.transfer_type=? THEN approved_quantity ELSE 0 END)transfers,");
        strBuff.append(" sum(CASE WHEN CT.transfer_type=? THEN approved_quantity ELSE 0 END)returns1");
        strBuff.append(" FROM channel_transfers CT,channel_transfers_items CTI,products P ");
        strBuff.append(" WHERE CT.transfer_date=? AND CT.status=? AND ");

        strBuff.append(" CT.from_user_id=? ");
        if (channelUserVO.isStaffUser()) {
            strBuff.append(" AND CT.active_user_id=? ");
        }
        strBuff.append(" AND  CT.transfer_id=CTI.transfer_id AND CTI.product_code=P.product_code");
        strBuff.append(" GROUP BY CTI.product_code,P.product_short_code,P.short_name ");
        strBuff.append(" UNION all ");
        strBuff.append(" SELECT P.product_short_code product_short_code,P.short_name short_name,P.product_code product_code,0 transfers,0 returns1 ");
        strBuff.append(" FROM products P ,network_product_mapping NPM ");
        strBuff.append(" WHERE NPM.network_code=? AND P.product_code=NPM.product_code AND module_code=?) as q");
        strBuff.append(" GROUP BY  product_short_code,short_name,product_code ");
        return strBuff.toString();
	}
	
	@Override
	public PreparedStatement loadUserSubscriberOutTransferListQry(Connection con,OperatorUtilI operatorUtilI,ChannelUserVO channelUserVO,java.util.Date date) throws SQLException, BTSLBaseException{
		//local_index_missing
		StringBuilder strBuff = new StringBuilder("Select m.*,ST.NAME");
	        strBuff.append(" From service_type ST,");
	        strBuff.append(" (SELECT  P.product_short_code,P.product_code,P.short_name,sum(CT.transfer_value)");
	        strBuff.append(" transfers,ct.service_type ");
	        strBuff.append(" FROM ");
	        if (operatorUtilI.getNewDataAftrTbleMerging(date, new Date())) {
	            strBuff.append(" c2s_transfers CT right outer join products P on (CT.product_code = P.product_code  ");
	        } else {
	            strBuff.append(" c2s_transfers_old CT right outer join products P on (CT.product_code = P.product_code");
	        }
	        strBuff.append(" AND CT.transfer_date =? AND CT.transfer_status =?");
	        if (!channelUserVO.isStaffUser()) {
	            strBuff.append(" AND  CT.sender_id =? )");
	        } else {
	            strBuff.append(" AND  CT.active_user_id =?)");
	        }
	        strBuff.append("  ,network_product_mapping NPM");
	        strBuff.append(" WHERE NPM.network_code=?");
	        strBuff.append(" AND P.product_code=NPM.product_code");
	        strBuff.append(" AND P.module_code=?");
	        strBuff.append(" Group by P.product_short_code,P.product_code,P.short_name,ct.service_type) M ");
	        strBuff.append(" where COALESCE(m.service_type,?)=ST.service_type ");
	        
	        String sqlSelect = strBuff.toString();
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("loadUserSubscriberOutTransferListQry", "QUERY sqlSelect=" + sqlSelect);
	        }
	        
	        PreparedStatement pstmt = con.prepareStatement(sqlSelect);
	        pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(date));
            pstmt.setString(2, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            if (!channelUserVO.isStaffUser()) {
                pstmt.setString(3, channelUserVO.getUserID());
            } else {
                pstmt.setString(3, channelUserVO.getActiveUserID());
            }
            pstmt.setString(4, channelUserVO.getNetworkID());
            pstmt.setString(5, PretupsI.C2S_MODULE);
            pstmt.setString(6, PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
            
            return pstmt;
	}
	@Override
	public PreparedStatement loadERPChnlUserDetailsByExtCodeQry(Connection con,String extCode) throws SQLException{
		 StringBuilder selectQueryBuff = new StringBuilder();
		 
		 selectQueryBuff.append("SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, ");
		 selectQueryBuff.append(" u.msisdn, u.employee_code,u.status userstatus,u.created_by,u.created_on,u.modified_by,u.modified_on,");
		 selectQueryBuff.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,");
		 selectQueryBuff.append(" cusers.out_suspend,cusers.suboutlet_code,cusers.outlet_code,cusers.mpay_profile_id,cusers.user_profile_id,");
		 selectQueryBuff.append(" cusers.mcommerce_service_allow,cusers.low_bal_alert_allow,");
		 selectQueryBuff.append(" u.previous_status,u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,");
		 selectQueryBuff.append(" u.user_code,u.short_name,u.reference_id,u.creation_type,");
		 selectQueryBuff.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,");
		 selectQueryBuff.append(" gdomains.status geostatus, ");
		 selectQueryBuff.append(" uphones.msisdn prmsisdn, uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required,");
		 selectQueryBuff.append(" uphones.phone_profile, uphones.phone_language phlang,");
		 selectQueryBuff.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
       	 selectQueryBuff.append("  uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time, ");
		 selectQueryBuff.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id,");
		selectQueryBuff.append(" tp.status tpstatus,cusers.user_grade,cset.status csetstatus, ");
		selectQueryBuff.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,");
		selectQueryBuff.append(" gdt.sequence_no grphSeq, cat.transfertolistonly, uphones.created_on uphones_created ");
		selectQueryBuff.append(" FROM channel_users cusers right outer join ( users u left outer join user_phones uphones (u.user_id=uphones.user_id  AND  uphones.primary_number=?)) on cusers.user_id=u.user_id ");
		selectQueryBuff.append(" left outer join transfer_profile tp on cusers.transfer_profile_id=tp.profile_id");
		selectQueryBuff.append(" left outer join commission_profile_set cset on cusers.comm_profile_set_id=cset.comm_profile_set_id,");
		selectQueryBuff.append(" geographical_domains gdomains right outer join user_geographies geo  on gdomains.grph_domain_code= geo.grph_domain_code");
		selectQueryBuff.append(" left outer join geographical_domain_types gdt on gdomains.grph_domain_type=gdt.grph_domain_type,");
		 selectQueryBuff.append(" categories cat,domains dom 	");
	selectQueryBuff.append(" WHERE u.external_code=?  AND u.status <> ? AND u.status <> ?  AND  u.category_code = cat.category_code AND u.user_id=geo.user_id AND  cat.domain_code= dom.domain_code "); 
	
		   String selectQuery = selectQueryBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadERPChnlUserDetailsByExtCodeQry", "select query:" + selectQuery);
        }
        PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);
        pstmtSelect.setString(1, PretupsI.YES);
        pstmtSelect.setString(2, extCode);
        pstmtSelect.setString(3, PretupsI.USER_STATUS_DELETED);
        pstmtSelect.setString(4, PretupsI.USER_STATUS_CANCELED);
        return pstmtSelect;
	}
	
	@Override
	public PreparedStatement validateParentAndOwnerQry(Connection con,String userGeography,ChannelUserVO parentVO) throws SQLException {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("  WITH RECURSIVE Q AS (SELECT (1),grph_domain_code, status  FROM geographical_domains gd  ");
        strBuff.append(" WHERE gd.parent_grph_domain_code=? ");
        strBuff.append("  UNION ALL SELECT (1),m.grph_domain_code, m.status  FROM geographical_domains m join Q on q.grph_domain_code=m.parent_grph_domain_code )" );
        strBuff.append( " SELECT  (1) FROM Q where grph_domain_code=? AND status='Y'");
        String selectGeography = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateParentAndOwnerQry", "Parent Geography Query =" + selectGeography);
        }
        PreparedStatement pstmtSelectGeography = con.prepareStatement(selectGeography);
        pstmtSelectGeography.setString(1, parentVO.getGeographicalCode());
        pstmtSelectGeography.setString(2, userGeography);
        return pstmtSelectGeography;
	}

	
	@Override
	public PreparedStatement loadUsersListForExtApiQry(Connection con, String loginId, String parentMsisdn, String ownerMsisdn, String statusUsed, String status)throws SQLException{
		 Boolean isLoginIdCheckAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_ID_CHECK_ALLOWED);
		 StringBuilder strBuff = new StringBuilder();
			  strBuff.append("SELECT distinct USR.batch_id, USR.creation_type, USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
		      strBuff.append("  USR.login_id,USR.password as  password,USR.category_code usr_category_code,USR.parent_id,USR.reference_id, ");
		      strBuff.append("   USR.owner_id,USR.allowed_ip,USR.allowed_days,");
		      strBuff.append("   USR.from_time,USR.to_time,USR.employee_code,");
		      strBuff.append("   USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
		      strBuff.append("   USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
		      strBuff.append("   USR.created_by,USR_CRBY.user_name created_by_name, USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
		      strBuff.append("   USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
		      strBuff.append("   USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
		      strBuff.append("   USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
		      strBuff.append("   USR.previous_status,USR.rsaflag,USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name, USR_CAT.max_txn_msisdn, ");
		      strBuff.append("   USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, USR_CAT.transfertolistonly, USR_CAT.low_bal_alert_allow, ");
		      strBuff.append("   USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, USR_CAT.services_allowed, ");
		      strBuff.append("   USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed, USR_CAT.agent_allowed,USR_CAT.uncntrl_transfer_allowed,USR_CAT.restricted_msisdns,"); 
		      strBuff.append("   PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn,MOD_USR.user_name request_user_name, ");
		      strBuff.append("   PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, USR_CAT.category_type,");
		      strBuff.append("   ONR_CAT.category_name owner_cat,l.lookup_name, UG.grph_domain_code, GD.grph_domain_name, D.domain_type_code ");
		      strBuff.append("   FROM users USR left outer join (categories  PRNT_CAT  right outer join users PRNT_USR on PRNT_CAT.category_code=PRNT_USR.category_code ) on USR.parent_id=PRNT_USR.user_id ");
			  strBuff.append("	 left outer join users MOD_USR on USR.modified_by=MOD_USR.user_id ");
			  strBuff.append(" left outer join users USR_CRBY on USR.created_by =USR_CRBY.user_id  ");
			  strBuff.append(" ,users ONR_USR,categories USR_CAT,categories ONR_CAT ");
		      strBuff.append("  ,lookups l,user_geographies UG, geographical_domains GD, domains D ");
		      
		     
		      // for LoginID case Sensitive
		        if (isLoginIdCheckAllow) {
		            strBuff.append("WHERE ( USR.login_id=? ");
		        } else {
		            strBuff.append("WHERE ( UPPER(USR.login_id)=UPPER(?) ");
		        }
		      
		       strBuff.append(" OR USR.msisdn=? OR USR.msisdn=? )");
		       if (statusUsed.equals(PretupsI.STATUS_IN)) {
		            strBuff.append(" AND USR.status IN (" + status + ") ");
		        } else if (statusUsed.equals(PretupsI.STATUS_NOTIN)) {
		            strBuff.append(" AND USR.status NOT IN (" + status + ") ");
		        } else if (statusUsed.equals(PretupsI.STATUS_EQUAL)) {
		            strBuff.append(" AND USR.status =? ");
		        } else if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
		            strBuff.append(" AND USR.status <> ? ");
		        }

		       strBuff.append("AND  USR.owner_id=ONR_USR.user_id ");
		       strBuff.append("AND USR.category_code=USR_CAT.category_code ");
		       strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
		      
		       strBuff.append("AND USR.status = l.lookup_code ");
		       strBuff.append("AND l.lookup_type= ? ");
		       strBuff.append("AND USR.user_id = UG.user_id ");
		       strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
		      
		       strBuff.append("AND USR_CAT.domain_code=D.domain_code ");

	        
	        String sqlSelect = strBuff.toString();
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("loadUsersListForExtApiQry", "QUERY sqlSelect=" + sqlSelect);
	        }
	        PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, loginId);
            pstmtSelect.setString(i++, parentMsisdn);
            pstmtSelect.setString(i++, ownerMsisdn);
            if (statusUsed.equals(PretupsI.STATUS_NOTEQUAL) || statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                pstmtSelect.setString(i++, status);
            }
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_TYPE);
            return pstmtSelect;
	}
	@Override
	public String loadChannelUserOutChildTransferListQry(
			ChannelUserVO pChannelUserVO) {
		StringBuilder strBuff = new StringBuilder("SELECT product_short_code,short_name,product_code,sum(transfers)transfers,sum(returns1)returns1 ");
        strBuff.append("FROM");
        strBuff.append("(SELECT /*+ INDEX(CT) */ P.product_short_code product_short_code,P.short_name short_name,CTI.product_code product_code,");
        strBuff.append(" sum(CASE WHEN CT.transfer_type=? THEN approved_quantity ELSE 0 END)transfers,");
        strBuff.append(" sum(CASE WHEN CT.transfer_type=? THEN approved_quantity ELSE 0 END)returns1");
        strBuff.append(" FROM channel_transfers CT,channel_transfers_items CTI,products P ");
        strBuff.append(" WHERE CT.transfer_date=? AND CT.status=? AND ");
        strBuff.append(" CT.from_user_id in (select user_id from users where parent_id=? and status=?) ");
        if (pChannelUserVO.isStaffUser()) {
            strBuff.append(" AND CT.active_user_id in (select user_id from users where parent_id=? and status=?) ");
        }
        strBuff.append(" AND  CT.transfer_id=CTI.transfer_id AND CTI.product_code=P.product_code");
        strBuff.append(" GROUP BY CTI.product_code,P.product_short_code,P.short_name ");
        strBuff.append(" UNION all ");
        strBuff.append(" SELECT P.product_short_code product_short_code,P.short_name short_name,P.product_code product_code,0 transfers,0 returns1 ");
        strBuff.append(" FROM products P ,network_product_mapping NPM ");
        strBuff.append(" WHERE NPM.network_code=? AND P.product_code=NPM.product_code AND module_code=?) as q");
        strBuff.append(" GROUP BY  product_short_code,short_name,product_code ");
        return strBuff.toString();
	}
}
