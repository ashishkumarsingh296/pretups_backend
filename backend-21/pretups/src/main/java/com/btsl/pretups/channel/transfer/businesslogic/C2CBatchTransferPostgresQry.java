package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;


public class C2CBatchTransferPostgresQry implements C2CBatchTransferQry{
	private final Log LOG = LogFactory.getLog(C2CBatchTransferPostgresQry.class.getName());
	@Override
	public String closeOrderByBatchselectUserBalancesQry() {
		StringBuilder sqlBuffer  = new StringBuilder(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
	        sqlBuffer.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
	        sqlBuffer.append("FROM user_balances ");
	        sqlBuffer.append("WHERE user_id = ? AND date_trunc('day',daily_balance_updated_on::TIMESTAMP) <> date_trunc('day',?::TIMESTAMP) FOR UPDATE  ");
	        
		return sqlBuffer.toString();
	}
	@Override
	public String closeOrderByBatchselectBalanceQry() {
		StringBuilder sqlBuffer = new StringBuilder("  SELECT ");
        sqlBuffer.append(" balance ");
        sqlBuffer.append(" FROM user_balances ");
        sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE  ");
      
		return sqlBuffer.toString();
	}

	@Override
	public PreparedStatement loadUsersForHierarchyFixedCatQry(Connection p_con,String statusAllowed,
			String p_networkCode, String p_toCategoryCode,
			String p_parentUserID, String p_userName, String p_userID,
			String p_fixedCat, int p_ctrlLvl, String p_txnType)
			throws SQLException {
		
		final StringBuilder strBuff = new StringBuilder();
		
		strBuff.append(" with recursive q as ( ");
		strBuff.append(" SELECT u.user_id, u.network_code ,CU.user_id CU_user_id,CU.user_grade CU_user_grade, cat.CATEGORY_CODE cat_CATEGORY_CODE , cg.CATEGORY_CODE cg_CATEGORY_CODE,");
	    strBuff.append( " cg.STATUS cg_STATUS, CPS.comm_profile_set_id CPS_comm_profile_set_id, CPSV.comm_profile_set_id CPSV_comm_profile_set_id, TP.profile_id TP_profile_id,cat.status cat_status, ");
		strBuff.append( "u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE,  ");
		strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ,ub.product_code");
        strBuff.append(" FROM users u left join USER_BALANCES ub on u.user_id=ub.user_id ,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" where ");
        strBuff.append(" u.parent_id IN ");
            strBuff.append("(");
        		strBuff.append(" with recursive q1 as( ");
        		 strBuff.append("SELECT u1.user_id ,u1.category_code FROM users u1    ");
        		  if (p_ctrlLvl == 1) {
  	                strBuff.append(" where parent_id = ? ");
  	            } else if (p_ctrlLvl == 2) {
  	                strBuff.append(" where  owner_id = ? ");
  	            }
        		 strBuff.append("union all SELECT u1.user_id ,u1.category_code  FROM users u1 join q1 on q1.user_id=u1.parent_id  ");
        				 strBuff.append(" )SELECT user_id FROM q1  where category_code  IN (" + p_fixedCat + ") ");
        
        strBuff.append(" )  ");
        strBuff.append(" AND u.user_id=CU.user_id AND CU.user_grade=cg.grade_code ");
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = cu.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = cu.transfer_profile_id AND cat.status='Y' ");

        strBuff.append(" union all ");
       
        strBuff.append(" SELECT u.user_id, u.network_code ,CU.user_id CU_user_id,CU.user_grade CU_user_grade, cat.CATEGORY_CODE cat_CATEGORY_CODE , cg.CATEGORY_CODE cg_CATEGORY_CODE,");
        strBuff.append( " cg.STATUS cg_STATUS, CPS.comm_profile_set_id CPS_comm_profile_set_id, CPSV.comm_profile_set_id CPSV_comm_profile_set_id, TP.profile_id TP_profile_id,cat.status cat_status , ");
       
        strBuff.append( "  u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE,  ");
		strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
        strBuff.append(" , CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append(" CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append(" CPS.language_2_message  comprf_lang_2_msg,ub.product_code ");
        strBuff.append(" FROM users u join q on  q.user_id=u.parent_id left join USER_BALANCES ub on u.user_id=ub.user_id  ,");
        strBuff.append(" channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(" , commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        
        strBuff.append(" WHERE u.user_id=CU.user_id AND CU.user_grade=cg.grade_code ");
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = cu.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = cu.transfer_profile_id AND cat.status='Y' ");
        
        
	    strBuff.append(" )");
	    strBuff.append(" SELECT user_id,user_name,LOGIN_ID,msisdn,EXTERNAL_CODE,  ");
		strBuff.append(" CATEGORY_CODE,BALANCE,CATEGORY_NAME,GRADE_CODE, GRADE_NAME,STATUS ");
        strBuff.append(" ,transfer_profile_id,comm_profile_set_id,in_suspend");
        strBuff.append(" , applicable_from,comm_profile_set_name ,comm_profile_set_version, profile_name, ");
        strBuff.append(" commprofilestatus, profile_status, comprf_lang_1_msg, ");
        strBuff.append("  comprf_lang_2_msg,product_code ");
        strBuff.append(" from q ");
        strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_id != ?");
        strBuff.append(" AND user_id=CU_user_id AND CU_user_grade=grade_code ");
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        strBuff.append(" AND CATEGORY_CODE=cat_CATEGORY_CODE AND CATEGORY_CODE=cg_CATEGORY_CODE AND cg_STATUS='Y' ");
        strBuff.append(" AND CPS_comm_profile_set_id = comm_profile_set_id AND CPSV_comm_profile_set_id = CPS_comm_profile_set_id ");
        strBuff.append(" AND applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS_comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP_profile_id = transfer_profile_id AND cat_status='Y' ");
        strBuff.append(" ORDER BY user_name ");
		
		
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUsersForHierarchyFixedCatQry", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
        int i = 0;
        if (p_ctrlLvl > 0) {
            
            pstmt.setString(++i, p_parentUserID);
        }
        final Date currentDate = new Date();
        pstmt.setString(++i, p_userName);
        pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
        pstmt.setString(++i, p_userName);
        pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
        pstmt.setString(++i, p_networkCode);
        pstmt.setString(++i, p_toCategoryCode);
        pstmt.setString(++i, p_userID);
        pstmt.setString(++i, p_userName);
        pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
        return pstmt;
	}

	

	@Override
	public PreparedStatement loadUsersForHierarchyFixedCatTcpQry(Connection p_con,String statusAllowed,
			String p_networkCode, String p_toCategoryCode,
			String p_parentUserID, String p_userName, String p_userID,
			String p_fixedCat, int p_ctrlLvl, String p_txnType)
			throws SQLException {
		
		final StringBuilder strBuff = new StringBuilder();
		
		strBuff.append(" with recursive q as ( ");
		strBuff.append(" SELECT u.user_id, u.network_code ,CU.user_id CU_user_id,CU.user_grade CU_user_grade, cat.CATEGORY_CODE cat_CATEGORY_CODE , cg.CATEGORY_CODE cg_CATEGORY_CODE,");
	    strBuff.append( " cg.STATUS cg_STATUS, CPS.comm_profile_set_id CPS_comm_profile_set_id, CPSV.comm_profile_set_id CPSV_comm_profile_set_id, cat.status cat_status, ");
		strBuff.append( "u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE,  ");
		strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,  ");
        strBuff.append("CPS.status commprofilestatus,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ,ub.product_code");
        strBuff.append(" FROM users u left join USER_BALANCES ub on u.user_id=ub.user_id ,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV ");
        strBuff.append(" where ");
        strBuff.append(" u.parent_id IN ");
            strBuff.append("(");
        		strBuff.append(" with recursive q1 as( ");
        		 strBuff.append("SELECT u1.user_id ,u1.category_code FROM users u1    ");
        		  if (p_ctrlLvl == 1) {
  	                strBuff.append(" where parent_id = ? ");
  	            } else if (p_ctrlLvl == 2) {
  	                strBuff.append(" where  owner_id = ? ");
  	            }
        		 strBuff.append("union all SELECT u1.user_id ,u1.category_code  FROM users u1 join q1 on q1.user_id=u1.parent_id  ");
        				 strBuff.append(" )SELECT user_id FROM q1  where category_code  IN (" + p_fixedCat + ") ");
        
        strBuff.append(" )  ");
        strBuff.append(" AND u.user_id=CU.user_id AND CU.user_grade=cg.grade_code ");
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = cu.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append("  AND cat.status='Y' ");

        strBuff.append(" union all ");
       
        strBuff.append(" SELECT u.user_id, u.network_code ,CU.user_id CU_user_id,CU.user_grade CU_user_grade, cat.CATEGORY_CODE cat_CATEGORY_CODE , cg.CATEGORY_CODE cg_CATEGORY_CODE,");
        strBuff.append( " cg.STATUS cg_STATUS, CPS.comm_profile_set_id CPS_comm_profile_set_id, CPSV.comm_profile_set_id CPSV_comm_profile_set_id, cat.status cat_status , ");
       
        strBuff.append( "  u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE,  ");
		strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
        strBuff.append(" , CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,  ");
        strBuff.append(" CPS.status commprofilestatus,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append(" CPS.language_2_message  comprf_lang_2_msg,ub.product_code ");
        strBuff.append(" FROM users u join q on  q.user_id=u.parent_id left join USER_BALANCES ub on u.user_id=ub.user_id  ,");
        strBuff.append(" channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(" , commission_profile_set CPS, commission_profile_set_version CPSV ");
        
        strBuff.append(" WHERE u.user_id=CU.user_id AND CU.user_grade=cg.grade_code ");
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = cu.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append("  AND cat.status='Y' ");
        
        
	    strBuff.append(" )");
	    strBuff.append(" SELECT user_id,user_name,LOGIN_ID,msisdn,EXTERNAL_CODE,  ");
		strBuff.append(" CATEGORY_CODE,BALANCE,CATEGORY_NAME,GRADE_CODE, GRADE_NAME,STATUS ");
        strBuff.append(" ,transfer_profile_id,comm_profile_set_id,in_suspend");
        strBuff.append(" , applicable_from,comm_profile_set_name ,comm_profile_set_version, profile_name, ");
        strBuff.append(" commprofilestatus, profile_status, comprf_lang_1_msg, ");
        strBuff.append("  comprf_lang_2_msg,product_code ");
        strBuff.append(" from q ");
        strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ") AND category_code = ? AND user_id != ?");
        strBuff.append(" AND user_id=CU_user_id AND CU_user_grade=grade_code ");
        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?) ");
        strBuff.append(" AND CATEGORY_CODE=cat_CATEGORY_CODE AND CATEGORY_CODE=cg_CATEGORY_CODE AND cg_STATUS='Y' ");
        strBuff.append(" AND CPS_comm_profile_set_id = comm_profile_set_id AND CPSV_comm_profile_set_id = CPS_comm_profile_set_id ");
        strBuff.append(" AND applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS_comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND cat_status='Y' ");
        strBuff.append(" ORDER BY user_name ");
		
		
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUsersForHierarchyFixedCatQry", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
        int i = 0;
        if (p_ctrlLvl > 0) {
            
            pstmt.setString(++i, p_parentUserID);
        }
        final Date currentDate = new Date();
        pstmt.setString(++i, p_userName);
        pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
        pstmt.setString(++i, p_userName);
        pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
        pstmt.setString(++i, p_networkCode);
        pstmt.setString(++i, p_toCategoryCode);
        pstmt.setString(++i, p_userID);
        pstmt.setString(++i, p_userName);
        pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
        return pstmt;
	}

	
	@Override
	public PreparedStatement loadUsersByParentIDRecursiveQry(Connection p_con,
			String p_networkCode, String p_toCategoryCode, String p_parentID,
			String p_userName, String p_userID, String p_txnType,String statusAllowed)
			throws SQLException {
			PreparedStatement pstmt = null;
		 	final StringBuilder strBuff = new StringBuilder(" with recursive q as ( SELECT u.user_id,");
		 	 strBuff.append(" u.network_code, CU.user_id CU_user_id,CU.user_grade CU_user_grade, cat.CATEGORY_CODE cat_CATEGORY_CODE,cg.CATEGORY_CODE cg_CATEGORY_CODE, cg.STATUS cg_STATUS ");
		     strBuff.append("  , CPS.comm_profile_set_id CPS_comm_profile_set_id, CPSV.comm_profile_set_id CPSV_comm_profile_set_id,TP.profile_id TP_profile_id, cat.status cat_status , u.user_type u_user_type ");
		     strBuff.append(",u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
	        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS  ");
	        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
	        strBuff.append(" , CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
	        strBuff.append(" CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message , ");
	        strBuff.append(" CPS.language_2_message   ,ub.product_code");
	        strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id  ,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
	        strBuff.append(" , commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
	       	        
	        strBuff.append(" where u.parent_id = ? union all SELECT m.user_id,");
	        strBuff.append(" m.network_code, CU1.user_id CU_user_id,CU1.user_grade CU_user_grade, cat1.CATEGORY_CODE cat_CATEGORY_CODE,cg1.CATEGORY_CODE cg_CATEGORY_CODE, cg1.STATUS cg_STATUS ");
	        strBuff.append("  , CPS1.comm_profile_set_id CPS_comm_profile_set_id, CPSV1.comm_profile_set_id CPSV_comm_profile_set_id,TP1.profile_id TP_profile_id, cat1.status cat_status , m.user_type u_user_type  ");
	        strBuff.append(" ,m.user_name,m.LOGIN_ID,m.msisdn,m.EXTERNAL_CODE, ");
	        strBuff.append(" m.CATEGORY_CODE,ub1.BALANCE,cat1.CATEGORY_NAME,cg1.GRADE_CODE, cg1.GRADE_NAME,m.STATUS  ");
	        strBuff.append(" ,CU1.transfer_profile_id,CU1.comm_profile_set_id,CU1.in_suspend");
	        strBuff.append(" , CPSV1.applicable_from,CPS1.comm_profile_set_name ,CPSV1.comm_profile_set_version, TP1.profile_name, ");
	        strBuff.append(" CPS1.status commprofilestatus ,TP1.status profile_status ,CPS1.language_1_message , ");
	        strBuff.append(" CPS1.language_2_message ,ub1.product_code  ");
	        strBuff.append(" FROM channel_users CU1,CHANNEL_GRADES cg1 ,CATEGORIES cat1 ");
	        strBuff.append(" , commission_profile_set CPS1, commission_profile_set_version CPSV1,transfer_profile TP1 ,users m join q on q.user_id=m.parent_id left join USER_BALANCES ub1 on m.user_id=ub1.user_id  ");
	       
	        // here user_id != ? check is for not to load the sender user in the
	        // query for the same level transactions
	        strBuff.append("  ) SELECT q.user_id,q.user_name,q.LOGIN_ID,q.msisdn,q.EXTERNAL_CODE, ");
	        strBuff.append(" q.CATEGORY_CODE,q.BALANCE,q.CATEGORY_NAME,q.GRADE_CODE, q.GRADE_NAME,q.STATUS ");
	        strBuff.append(" ,q.transfer_profile_id,q.comm_profile_set_id,q.in_suspend");
	        strBuff.append(" , q.applicable_from,q.comm_profile_set_name ,q.comm_profile_set_version, q.profile_name, ");
	        strBuff.append(" q.commprofilestatus,q.profile_status,q.language_1_message comprf_lang_1_msg, ");
	        strBuff.append(" q.language_2_message  comprf_lang_2_msg,q.product_code ");
	        strBuff.append(" FROM q");
	        
	        strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ")  AND category_code = ? AND user_id != ?");
	        strBuff.append(" AND user_id=CU_user_id AND  CU_user_grade=grade_code ");
	        strBuff.append(" AND CATEGORY_CODE=cat_CATEGORY_CODE AND CATEGORY_CODE=cg_CATEGORY_CODE AND cg_STATUS='Y' ");
	        strBuff.append(" AND CPS_comm_profile_set_id = comm_profile_set_id AND CPSV_comm_profile_set_id = CPS_comm_profile_set_id ");
	        strBuff.append(" AND applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
	        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
	        strBuff.append(" AND CPS_comm_profile_set_id = CPSV1.comm_profile_set_id )");
	        strBuff.append(" AND TP_profile_id = transfer_profile_id AND cat_status='Y' ");
	        strBuff.append(" AND u_user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
	        // here user_id != ? check is for not to load the sender user in the
	        // query for the same level transactions
	        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?)  ");
	        
	        strBuff.append(" ORDER BY user_name ");
	        final String sqlSelect = strBuff.toString();
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("loadUsersByParentIDRecursiveQry", "QUERY sqlSelect=" + sqlSelect);
	        }
	        pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            final Date currentDate = new Date();
            ++i;
            pstmt.setString(i, p_parentID);
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_userID);
            ++i;
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));

            ++i;
            pstmt.setString(i, p_userName);
           
		return pstmt;
	}

	
	@Override
	public PreparedStatement loadUsersByParentIDRecursiveTcpQry(Connection p_con,
			String p_networkCode, String p_toCategoryCode, String p_parentID,
			String p_userName, String p_userID, String p_txnType,String statusAllowed)
			throws SQLException {
			PreparedStatement pstmt = null;
		 	final StringBuilder strBuff = new StringBuilder(" with recursive q as ( SELECT u.user_id,");
		 	 strBuff.append(" u.network_code, CU.user_id CU_user_id,CU.user_grade CU_user_grade, cat.CATEGORY_CODE cat_CATEGORY_CODE,cg.CATEGORY_CODE cg_CATEGORY_CODE, cg.STATUS cg_STATUS ");
		     strBuff.append("  , CPS.comm_profile_set_id CPS_comm_profile_set_id, CPSV.comm_profile_set_id CPSV_comm_profile_set_id, cat.status cat_status , u.user_type u_user_type ");
		     strBuff.append(",u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
	        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS  ");
	        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
	        strBuff.append(" , CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,  ");
	        strBuff.append(" CPS.status commprofilestatus,CPS.language_1_message , ");
	        strBuff.append(" CPS.language_2_message   ,ub.product_code");
	        strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id  ,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
	        strBuff.append(" , commission_profile_set CPS, commission_profile_set_version CPSV  ");
	       	        
	        strBuff.append(" where u.parent_id = ? union all SELECT m.user_id,");
	        strBuff.append(" m.network_code, CU1.user_id CU_user_id,CU1.user_grade CU_user_grade, cat1.CATEGORY_CODE cat_CATEGORY_CODE,cg1.CATEGORY_CODE cg_CATEGORY_CODE, cg1.STATUS cg_STATUS ");
	        strBuff.append("  , CPS1.comm_profile_set_id CPS_comm_profile_set_id, CPSV1.comm_profile_set_id CPSV_comm_profile_set_id, cat1.status cat_status , m.user_type u_user_type  ");
	        strBuff.append(" ,m.user_name,m.LOGIN_ID,m.msisdn,m.EXTERNAL_CODE, ");
	        strBuff.append(" m.CATEGORY_CODE,ub1.BALANCE,cat1.CATEGORY_NAME,cg1.GRADE_CODE, cg1.GRADE_NAME,m.STATUS  ");
	        strBuff.append(" ,CU1.transfer_profile_id,CU1.comm_profile_set_id,CU1.in_suspend");
	        strBuff.append(" , CPSV1.applicable_from,CPS1.comm_profile_set_name ,CPSV1.comm_profile_set_version,  ");
	        strBuff.append(" CPS1.status commprofilestatus ,CPS1.language_1_message , ");
	        strBuff.append(" CPS1.language_2_message ,ub1.product_code  ");
	        strBuff.append(" FROM channel_users CU1,CHANNEL_GRADES cg1 ,CATEGORIES cat1 ");
	        strBuff.append(" , commission_profile_set CPS1, commission_profile_set_version CPSV1 ,users m join q on q.user_id=m.parent_id left join USER_BALANCES ub1 on m.user_id=ub1.user_id  ");
	       
	        // here user_id != ? check is for not to load the sender user in the
	        // query for the same level transactions
	        strBuff.append("  ) SELECT q.user_id,q.user_name,q.LOGIN_ID,q.msisdn,q.EXTERNAL_CODE, ");
	        strBuff.append(" q.CATEGORY_CODE,q.BALANCE,q.CATEGORY_NAME,q.GRADE_CODE, q.GRADE_NAME,q.STATUS ");
	        strBuff.append(" ,q.transfer_profile_id,q.comm_profile_set_id,q.in_suspend");
	        strBuff.append(" , q.applicable_from,q.comm_profile_set_name ,q.comm_profile_set_version, q.profile_name, ");
	        strBuff.append(" q.commprofilestatus,q.profile_status,q.language_1_message comprf_lang_1_msg, ");
	        strBuff.append(" q.language_2_message  comprf_lang_2_msg,q.product_code ");
	        strBuff.append(" FROM q");
	        
	        strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ")  AND category_code = ? AND user_id != ?");
	        strBuff.append(" AND user_id=CU_user_id AND  CU_user_grade=grade_code ");
	        strBuff.append(" AND CATEGORY_CODE=cat_CATEGORY_CODE AND CATEGORY_CODE=cg_CATEGORY_CODE AND cg_STATUS='Y' ");
	        strBuff.append(" AND CPS_comm_profile_set_id = comm_profile_set_id AND CPSV_comm_profile_set_id = CPS_comm_profile_set_id ");
	        strBuff.append(" AND applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
	        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
	        strBuff.append(" AND CPS_comm_profile_set_id = CPSV1.comm_profile_set_id )");
	        strBuff.append(" AND cat_status='Y' ");
	        strBuff.append(" AND u_user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
	        // here user_id != ? check is for not to load the sender user in the
	        // query for the same level transactions
	        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?)  ");
	        
	        strBuff.append(" ORDER BY user_name ");
	        final String sqlSelect = strBuff.toString();
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("loadUsersByParentIDRecursiveQry", "QUERY sqlSelect=" + sqlSelect);
	        }
	        pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            final Date currentDate = new Date();
            ++i;
            pstmt.setString(i, p_parentID);
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_userID);
            ++i;
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));

            ++i;
            pstmt.setString(i, p_userName);
           
		return pstmt;
	}

	
	@Override
	public PreparedStatement loadUserForChannelByPassQry(String statusAllowed,Connection p_con,
			String p_networkCode, String p_toCategoryCode, String p_parentID,
			String p_userName, String p_userID, String p_txnType)
			throws SQLException {
		final StringBuilder strBuff = new StringBuilder(" with recursive q as ( SELECT u.user_id ");
		 strBuff.append(" , u.network_code,u.parent_id, CU.user_id CU_user_id,CU.user_grade CU_user_grade, cat.CATEGORY_CODE cat_CATEGORY_CODE,cg.CATEGORY_CODE cg_CATEGORY_CODE, cg.STATUS cg_STATUS ");
	     strBuff.append(" , CPS.comm_profile_set_id CPS_comm_profile_set_id, CPSV.comm_profile_set_id CPSV_comm_profile_set_id,TP.profile_id TP_profile_id, cat.status cat_status , u.user_type u_user_type ");
		strBuff.append( ",u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE,  ");
		strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
		strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
		strBuff.append(" , CPSV.applicable_from ,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
		strBuff.append(" CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message , ");
		strBuff.append(" CPS.language_2_message,ub.product_code");
		strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
		strBuff.append(" , commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
		
		strBuff.append(" where ");
		strBuff.append("  u.parent_id=?  union all");
		strBuff.append(" SELECT m.user_id");
		strBuff.append(" , m.network_code,m.parent_id, CU1.user_id CU_user_id,CU1.user_grade CU_user_grade, cat1.CATEGORY_CODE cat_CATEGORY_CODE,cg1.CATEGORY_CODE cg_CATEGORY_CODE, cg1.STATUS cg_STATUS ");
	    strBuff.append("  , CPS1.comm_profile_set_id CPS_comm_profile_set_id, CPSV1.comm_profile_set_id CPSV_comm_profile_set_id,TP1.profile_id TP_profile_id, cat1.status cat_status , m.user_type u_user_type  ");
	    strBuff.append(" ,m.user_name,m.LOGIN_ID,m.msisdn,m.EXTERNAL_CODE, ");
		strBuff.append("  m.CATEGORY_CODE,ub1.BALANCE,cat1.CATEGORY_NAME,cg1.GRADE_CODE, cg1.GRADE_NAME,m.STATUS ");
		strBuff.append(" ,CU1.transfer_profile_id,CU1.comm_profile_set_id,CU1.in_suspend");
		strBuff.append(" , CPSV1.applicable_from,CPS1.comm_profile_set_name ,CPSV1.comm_profile_set_version, TP1.profile_name, ");
		strBuff.append(" CPS1.status commprofilestatus,TP1.status profile_status,CPS1.language_1_message ,");
		strBuff.append(" CPS1.language_2_message,ub1.product_code FROM channel_users CU1,CHANNEL_GRADES cg1 ,CATEGORIES cat1 ");
		strBuff.append(" , commission_profile_set CPS1, commission_profile_set_version CPSV1,transfer_profile TP1 ,users m join q on q.user_id=m.parent_id left join USER_BALANCES ub1 on m.user_id=ub1.user_id ");

		strBuff.append(" ) SELECT q.user_id,");
		strBuff.append( " q.user_name,q.LOGIN_ID,q.msisdn,q.EXTERNAL_CODE, ");
		strBuff.append(" q.CATEGORY_CODE,q.BALANCE,q.CATEGORY_NAME,q.GRADE_CODE, q.GRADE_NAME,q.status ,q.transfer_profile_id,q.comm_profile_set_id,q.in_suspend");
		strBuff.append(" , q.applicable_from,q.comm_profile_set_name ,q.comm_profile_set_version, q.profile_name, ");
		strBuff.append(" q.commprofilestatus,q.profile_status,q.language_1_message comprf_lang_1_msg,  ");
		strBuff.append(" q.language_2_message  comprf_lang_2_msg,q.product_code from q ");
		
		 strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ")  AND category_code = ? AND user_id != ?");
	        strBuff.append(" AND user_id=CU_user_id AND  CU_user_grade=grade_code ");
	        strBuff.append(" AND CATEGORY_CODE=cat_CATEGORY_CODE AND CATEGORY_CODE=cg_CATEGORY_CODE AND cg_STATUS='Y' ");
	        strBuff.append(" AND CPS_comm_profile_set_id = comm_profile_set_id AND CPSV_comm_profile_set_id = CPS_comm_profile_set_id ");
	        strBuff.append(" AND applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
	        strBuff.append(" WHERE applicable_from <= ? ");
	        strBuff.append(" AND CPS_comm_profile_set_id = CPSV_comm_profile_set_id )");
	        strBuff.append(" AND TP_profile_id = transfer_profile_id AND cat_status='Y' ");
	        strBuff.append(" AND u_user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
	        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?)  ");
        strBuff.append(" ORDER BY user_name ");
        
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUserForChannelByPassQry", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
        int i = 0;
        final Date currentDate = new Date();
        ++i;
        pstmt.setString(i, p_parentID);
        ++i;
        pstmt.setString(i, p_networkCode);
        ++i;
        pstmt.setString(i, p_toCategoryCode);
        ++i;
        pstmt.setString(i, p_userID);
        ++i;
        pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
        ++i;
        pstmt.setString(i, p_userName);
        
		return pstmt;
	}

	
	@Override
	public PreparedStatement loadUserForChannelByPassTcpQry(String statusAllowed,Connection p_con,
			String p_networkCode, String p_toCategoryCode, String p_parentID,
			String p_userName, String p_userID, String p_txnType)
			throws SQLException {
		final StringBuilder strBuff = new StringBuilder(" with recursive q as ( SELECT u.user_id ");
		 strBuff.append(" , u.network_code,u.parent_id, CU.user_id CU_user_id,CU.user_grade CU_user_grade, cat.CATEGORY_CODE cat_CATEGORY_CODE,cg.CATEGORY_CODE cg_CATEGORY_CODE, cg.STATUS cg_STATUS ");
	     strBuff.append(" , CPS.comm_profile_set_id CPS_comm_profile_set_id, CPSV.comm_profile_set_id CPSV_comm_profile_set_id, cat.status cat_status , u.user_type u_user_type ");
		strBuff.append( ",u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE,  ");
		strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
		strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
		strBuff.append(" , CPSV.applicable_from ,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,  ");
		strBuff.append(" CPS.status commprofilestatus,CPS.language_1_message , ");
		strBuff.append(" CPS.language_2_message,ub.product_code");
		strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
		strBuff.append(" , commission_profile_set CPS, commission_profile_set_version CPSV ");
		
		strBuff.append(" where ");
		strBuff.append("  u.parent_id=?  union all");
		strBuff.append(" SELECT m.user_id");
		strBuff.append(" , m.network_code,m.parent_id, CU1.user_id CU_user_id,CU1.user_grade CU_user_grade, cat1.CATEGORY_CODE cat_CATEGORY_CODE,cg1.CATEGORY_CODE cg_CATEGORY_CODE, cg1.STATUS cg_STATUS ");
	    strBuff.append("  , CPS1.comm_profile_set_id CPS_comm_profile_set_id, CPSV1.comm_profile_set_id CPSV_comm_profile_set_id, cat1.status cat_status , m.user_type u_user_type  ");
	    strBuff.append(" ,m.user_name,m.LOGIN_ID,m.msisdn,m.EXTERNAL_CODE, ");
		strBuff.append("  m.CATEGORY_CODE,ub1.BALANCE,cat1.CATEGORY_NAME,cg1.GRADE_CODE, cg1.GRADE_NAME,m.STATUS ");
		strBuff.append(" ,CU1.transfer_profile_id,CU1.comm_profile_set_id,CU1.in_suspend");
		strBuff.append(" , CPSV1.applicable_from,CPS1.comm_profile_set_name ,CPSV1.comm_profile_set_version,  ");
		strBuff.append(" CPS1.status commprofilestatus,CPS1.language_1_message ,");
		strBuff.append(" CPS1.language_2_message,ub1.product_code FROM channel_users CU1,CHANNEL_GRADES cg1 ,CATEGORIES cat1 ");
		strBuff.append(" , commission_profile_set CPS1, commission_profile_set_version CPSV1 ,users m join q on q.user_id=m.parent_id left join USER_BALANCES ub1 on m.user_id=ub1.user_id ");

		strBuff.append(" ) SELECT q.user_id,");
		strBuff.append( " q.user_name,q.LOGIN_ID,q.msisdn,q.EXTERNAL_CODE, ");
		strBuff.append(" q.CATEGORY_CODE,q.BALANCE,q.CATEGORY_NAME,q.GRADE_CODE, q.GRADE_NAME,q.status ,q.transfer_profile_id,q.comm_profile_set_id,q.in_suspend");
		strBuff.append(" , q.applicable_from,q.comm_profile_set_name ,q.comm_profile_set_version, q.profile_name, ");
		strBuff.append(" q.commprofilestatus,q.profile_status,q.language_1_message comprf_lang_1_msg,  ");
		strBuff.append(" q.language_2_message  comprf_lang_2_msg,q.product_code from q ");
		
		 strBuff.append(" WHERE network_code = ? AND status IN (" + statusAllowed + ")  AND category_code = ? AND user_id != ?");
	        strBuff.append(" AND user_id=CU_user_id AND  CU_user_grade=grade_code ");
	        strBuff.append(" AND CATEGORY_CODE=cat_CATEGORY_CODE AND CATEGORY_CODE=cg_CATEGORY_CODE AND cg_STATUS='Y' ");
	        strBuff.append(" AND CPS_comm_profile_set_id = comm_profile_set_id AND CPSV_comm_profile_set_id = CPS_comm_profile_set_id ");
	        strBuff.append(" AND applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
	        strBuff.append(" WHERE applicable_from <= ? ");
	        strBuff.append(" AND CPS_comm_profile_set_id = CPSV_comm_profile_set_id )");
	        strBuff.append("  AND cat_status='Y' ");
	        strBuff.append(" AND u_user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
	        strBuff.append(" AND UPPER(user_name) LIKE UPPER(?)  ");
        strBuff.append(" ORDER BY user_name ");
        
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUserForChannelByPassQry", "QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);
        int i = 0;
        final Date currentDate = new Date();
        ++i;
        pstmt.setString(i, p_parentID);
        ++i;
        pstmt.setString(i, p_networkCode);
        ++i;
        pstmt.setString(i, p_toCategoryCode);
        ++i;
        pstmt.setString(i, p_userID);
        ++i;
        pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
        ++i;
        pstmt.setString(i, p_userName);
        
		return pstmt;
	}

	@Override
	public String loadBatchDetailsListQry(){
		StringBuffer strBuff = new StringBuffer(" SELECT distinct c2cb.batch_id, c2cb.opt_batch_id, c2cb.network_code, c2cb.network_code_for,  ");
		strBuff.append(" c2cb.batch_name, c2cb.status, L.lookup_name status_desc, c2cb.domain_code, c2cb.product_code, c2cb.batch_file_name, "); 
		strBuff.append(" c2cb.batch_total_record, c2cb.batch_date, INTU.user_name initated_by, c2cb.created_on, P.product_name, D.domain_name, "); 
		strBuff.append(" cbi.batch_detail_id, cbi.category_code, cbi.msisdn, cbi.user_id, cbi.status status_item,  cbi.user_grade_code, cbi.reference_no, "); 
		strBuff.append(" cbi.transfer_date, cbi.txn_profile, cbi.commission_profile_set_id,  ");
		strBuff.append(" cbi.commission_profile_ver, cbi.commission_profile_detail_id, cbi.commission_type, cbi.commission_rate, ");
		strBuff.append(" cbi.commission_value, cbi.tax1_type, cbi.tax1_rate, cbi.tax1_value, cbi.tax2_type, cbi.tax2_rate, cbi.tax2_value, "); 
		strBuff.append(" cbi.tax3_type, cbi.tax3_rate, cbi.tax3_value, cbi.requested_quantity, cbi.transfer_mrp, cbi.initiator_remarks, cbi.approver_remarks,");
		strBuff.append(" COALESCE(FAPP.user_name,CNCL_USR.user_name) approved_by, COALESCE(cbi.approved_on,cbi.cancelled_on) approved_on,");
		strBuff.append(" CNCL_USR.user_name cancelled_by, cbi.cancelled_on, cbi.rcrd_status, cbi.external_code, ");
		strBuff.append(" U.user_name, C.category_name, CG.grade_name ");
		strBuff.append(" FROM c2c_batches c2cb Left OUTER JOIN users INTU ON c2cb.created_by = INTU.user_id, products P, domains D, ");
		strBuff.append(" c2c_batch_items cbi Left OUTER JOIN users FAPP ON (cbi.approved_by = FAPP.user_id) Left OUTER JOIN users CNCL_USR ON cbi.cancelled_by = CNCL_USR.user_id  ");
		strBuff.append(" , categories C, users U,channel_grades CG, lookups L ");
		strBuff.append(" WHERE ( c2cb.batch_id=? or c2cb.opt_batch_id=? ) "); 
		strBuff.append(" AND cbi.batch_id = c2cb.batch_id AND cbi.category_code = C.category_code AND cbi.user_id = U.user_id ");
		strBuff.append(" AND P.product_code = c2cb.product_code AND D.domain_code = c2cb.domain_code ");
		strBuff.append(" AND CG.grade_code = cbi.user_grade_code ");
		strBuff.append(" AND L.lookup_type = ? ");
		strBuff.append(" AND L.lookup_code = c2cb.status ");
		strBuff.append(" ORDER BY cbi.batch_detail_id DESC, cbi.category_code, cbi.status ");
		return strBuff.toString();
	}
	@Override
	public String loadBatchC2CMasterDetailsQry(String p_batchid, String pLOGinCatCode, String p_categoryCode,String p_userName, String p_domain) {
		StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT DISTINCT cb.batch_id,cb.opt_batch_id,cb.batch_name,cb.batch_total_record, p.product_name, p.unit_value, cb.domain_code, cb.batch_date,");
        strBuff.append(" SUM(case cbi.status when ? then 1 else 0 end ) as NEW, SUM(case cbi.status when ? then 1 else 0 end ) as CNCL, SUM(case cbi.status when ? then 1 else 0 end ) as CLOSE,cb.created_on ");
        
        strBuff.append(" FROM C2C_BATCHES cb,C2C_BATCH_ITEMS cbi,PRODUCTS p,USERS U");
        strBuff.append(" WHERE cb.product_code=p.product_code AND cb.batch_id=cbi.batch_id AND cb.opt_batch_id=cbi.opt_batch_id AND cb.created_by =u.user_id");
        if(p_batchid !=null)
        {
        	strBuff.append(" AND ( cb.batch_id = ? OR cb.opt_batch_id = ?) ");                        
        	strBuff.append(" AND cb.created_by =? ");
        }
        else
        {
        	strBuff.append(" AND DATE_TRUNC('day',cbi.transfer_date::TIMESTAMP) >= ? AND DATE_TRUNC('day',cbi.transfer_date::TIMESTAMP) <= ? ");
        	
            if(p_categoryCode.equals(pLOGinCatCode)){
            	strBuff.append(" AND cb.created_by =? ");
            } else {
            	strBuff.append(" AND cbi.CATEGORY_CODE="+p_categoryCode);
            	strBuff.append(" AND u.CATEGORY_CODE="+pLOGinCatCode); 	
                strBuff.append(" AND cb.created_by="+p_userName);
                strBuff.append(" AND cb.DOMAIN_CODE="+p_domain);
            }
            
        }                                
        strBuff.append(" GROUP BY cb.batch_id,cb.opt_batch_id,cb.batch_name,cb.batch_total_record, p.product_name,p.unit_value,cb.network_code,cb.network_code_for,cb.product_code, cb.modified_by, cb.modified_on,p.product_type,p.short_name ,cb.domain_code, cb.batch_date, cb.created_on ");
        strBuff.append(" ORDER BY cb.created_on DESC  ");
        return strBuff.toString();
	}
	@Override
	public String loadBatchC2CMasterDetailsForTxrQry(String p_currentLevel, String p_itemStatus) {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(" SELECT * FROM ( SELECT DISTINCT c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record,c2cb.user_id, p.product_name,p.short_name, ");
		strBuff.append(" p.unit_value, SUM(case cbi.status when ? then 1 else 0 end ) as NEW,SUM(case cbi.status when ? then 1 else 0 end ) as cncl, SUM(case cbi.status when ? then 1 else 0 end ) as CLOSE, ");
		strBuff.append(" c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on ,p.product_type, ");
		strBuff.append(" c2cb.domain_code,c2cb.batch_date,c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type,c2cb.created_by  ");
		strBuff.append(" FROM C2C_BATCHES c2cb,C2C_BATCH_ITEMS cbi,PRODUCTS p ");
		strBuff.append(" WHERE c2cb.user_id=? AND c2cb.product_code=p.product_code");
		strBuff.append(" AND c2cb.status=?  AND c2cb.batch_id=cbi.batch_id AND c2cb.opt_batch_id is null AND cbi.opt_batch_id is null ");
		strBuff.append(" AND cbi.rcrd_status=? AND cbi.status IN ("+p_itemStatus+")AND cbi.transfer_sub_type=? GROUP BY c2cb.batch_id,c2cb.batch_name,c2cb.batch_total_record, ");
		strBuff.append(" c2cb.user_id,p.product_name,p.unit_value,c2cb.network_code,c2cb.network_code_for,c2cb.product_code, c2cb.modified_by, c2cb.modified_on, ");
		strBuff.append(" c2cb.sms_default_lang,c2cb.sms_second_lang,cbi.transfer_type,cbi.transfer_sub_type ,p.product_type,p.short_name ,c2cb.domain_code,c2cb.batch_date,c2cb.created_by  ");
		strBuff.append(" ORDER BY c2cb.batch_date DESC ) ");
		strBuff.append(" as x ");
		if(PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_currentLevel))
			strBuff.append(" WHERE new>0 ");
		return strBuff.toString();
	}
	@Override
	public String closeBatchC2CTransferBalanceQry() {
		StringBuffer sqlBuffer=new StringBuffer("  SELECT ");
		sqlBuffer.append(" balance "); 
		sqlBuffer.append(" FROM user_balances ");
		sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE ");
		return sqlBuffer.toString();
	}
	@Override
	public String closeBatchC2CTransferUserBalanceQry() {
		StringBuffer sqlBuffer=new StringBuffer(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
		sqlBuffer.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
		sqlBuffer.append("FROM user_balances ");
		sqlBuffer.append("WHERE user_id = ? AND DATE_TRUNC('day',daily_balance_updated_on::TIMESTAMP)<> DATE_TRUNC('day',?::TIMESTAMP) FOR UPDATE");
		return sqlBuffer.toString();
	}
}

