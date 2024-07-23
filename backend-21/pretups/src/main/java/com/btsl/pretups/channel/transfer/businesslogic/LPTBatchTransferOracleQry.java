package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class LPTBatchTransferOracleQry implements LPTBatchTransferQry{
	@Override
	public PreparedStatement loadBatchLPTMasterDetailsQry(Connection p_con, String p_goeDomain, String p_domain, String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String p_loginID, String p_type)throws SQLException{
		PreparedStatement pstmt = null;
		
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name, p.unit_value, fb.domain_code, fb.batch_date, ");
        strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) new,");
        strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr1,SUM(DECODE(fbi.status,?,1,0)) cncl, ");
        strBuff.append(" SUM(DECODE(fbi.status,?,1,0)) appr2,SUM(DECODE(fbi.status,?,1,0)) closed, fb.created_on ");
        strBuff
            .append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud,  user_product_types upt, geographical_domains GRPD ");
        strBuff.append(" WHERE ug.grph_domain_code=fbg.geography_code  AND ud.user_id=ug.user_id  AND ud.domain_code= fb.domain_code AND upt.user_id=ud.user_id ");
        strBuff.append(" AND upt.product_type=p.product_type AND fbg.batch_id=fb.batch_id AND fb.product_code=p.product_code AND fb.batch_id=fbi.batch_id ");
        strBuff.append(" AND GRPD.grph_domain_code=UG.grph_domain_code AND fb.CREATED_BY = ug.USER_ID ");
        strBuff.append(" AND GRPD.grph_domain_code IN (SELECT GD1.grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S') ");
        if (p_type != null) {
            strBuff.append(" AND fb.type=? ");
        }
        strBuff.append(" CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code ");
        strBuff.append(" START WITH GD1.grph_domain_code IN (SELECT UG2.grph_domain_code FROM user_geographies UG2 WHERE UG2.user_id=? ");
        if (p_batchid == null && p_msisdn == null) {
            strBuff.append(" AND UG2.grph_domain_code IN(" + p_goeDomain + ")");
        }
        strBuff.append("))");

        if (p_batchid != null) {
            strBuff.append(" AND fb.batch_id = ? ");
        } else if (p_msisdn != null) {
            strBuff.append(" AND fbi.msisdn = ? AND TRUNC(fbi.transfer_date) >= ? AND TRUNC(fbi.transfer_date) <= ? ");
        } else {

            strBuff.append(" AND fb.domain_code IN(" + p_domain + ")");
            strBuff.append(" AND fb.product_code=p.product_code ");
            strBuff.append(" AND p.product_code IN(" + p_productCode + ") ");
            strBuff.append(" AND TRUNC(fbi.transfer_date) >= ? AND TRUNC(fbi.transfer_date) <= ? ");
        }
        strBuff
            .append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 WHERE ug1.user_id=ug.user_id) ");
        strBuff
            .append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code, fb.batch_date, fb.created_on ");
        strBuff.append(" ORDER BY fb.created_on DESC ");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadBatchLPTMasterDetailsQry", "QUERY sqlSelect=" + sqlSelect);
        }
		
        {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            if (p_type != null) {
                ++i;
                pstmt.setString(i, p_type);
            }
            ++i;
            pstmt.setString(i, p_loginID);
            if (p_batchid != null) {
                ++i;
                pstmt.setString(i, p_batchid);
            } else if (p_msisdn != null) {
                ++i;
                pstmt.setString(i, p_msisdn);
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "loadBatchLPTMasterDetailsQry",
                        "QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil.getSQLDateFromUtilDate(p_toDate) + " BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil
                            .getSQLDateFromUtilDate(p_toDate));
                }
            } else {
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "loadBatchLPTMasterDetailsQry",
                        "QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil.getSQLDateFromUtilDate(p_toDate) + " BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil
                            .getSQLDateFromUtilDate(p_toDate));
                }
            }
            
	}
        return pstmt;
	}

	@Override
	public String loadBatchDetailsListQry() {
        final StringBuilder strBuff = new StringBuilder(" SELECT FB.batch_id, FB.network_code, FB.network_code_for,  ");
        strBuff.append(" FB.batch_name, FB.status, L.lookup_name status_desc, FB.domain_code, FB.product_code, FB.batch_file_name, ");
        strBuff.append(" FB.batch_total_record, FB.batch_date, INTU.user_name initated_by, FB.created_on, P.product_name, D.domain_name, ");
        strBuff.append(" FBI.batch_detail_id, FBI.category_code, FBI.msisdn, FBI.user_id, FBI.status status_item,  FBI.user_grade_code, FBI.reference_no, ");
        strBuff.append(" FBI.ext_txn_no, FBI.ext_txn_date, FBI.transfer_date, FBI.txn_profile, FBI.commission_profile_set_id,  ");
        strBuff.append(" FBI.commission_profile_ver, FBI.commission_profile_detail_id, FBI.commission_type, FBI.commission_rate, ");
        strBuff.append(" FBI.commission_value, FBI.tax1_type, FBI.tax1_rate, FBI.tax1_value, FBI.tax2_type, FBI.tax2_rate, FBI.tax2_value, ");
        strBuff.append(" FBI.tax3_type, FBI.tax3_rate, FBI.tax3_value, FBI.requested_quantity, FBI.transfer_mrp, FBI.initiator_remarks, FBI.first_approver_remarks,");
        strBuff.append(" FBI.second_approver_remarks, FBI.third_approver_remarks, ");
        strBuff.append(" NVL(FAPP.user_name,CNCL_USR.user_name) first_approved_by, NVL(FBI.first_approved_on,FBI.cancelled_on) first_approved_on,");
        strBuff
            .append(" DECODE(FBI.first_approved_by, NULL, SAPP.user_name, NVL(SAPP.user_name,CNCL_USR.user_name)) second_approved_by, DECODE(FBI.first_approved_on, NULL, FBI.second_approved_on, NVL(FBI.second_approved_on,FBI.cancelled_on) )second_approved_on,");
        strBuff
            .append(" DECODE(FBI.second_approved_by, NULL, TAPP.user_name, NVL(TAPP.user_name,CNCL_USR.user_name)) third_approved_by,  DECODE(FBI.second_approved_on, NULL, FBI.second_approved_on, NVL(FBI.third_approved_on, FBI.cancelled_on))third_approved_on,");
        strBuff.append(" CNCL_USR.user_name cancelled_by, FBI.cancelled_on, FBI.rcrd_status, FBI.external_code, ");
        strBuff.append(" U.user_name, C.category_name, CG.grade_name, OWNU.user_name ownername, OWNU.msisdn ownermsisdn ");
        strBuff.append(" FROM foc_batches FB, products P, domains D, foc_batch_items FBI, categories C, users U, ");
        strBuff.append(" users INTU, users FAPP, users SAPP, users TAPP, channel_grades CG, lookups L, users CNCL_USR, users OWNU ");
        strBuff.append(" WHERE FB.batch_id=?  ");
        strBuff.append(" AND FBI.batch_id = FB.batch_id AND FBI.category_code = C.category_code AND FBI.user_id = U.user_id ");
        strBuff.append(" AND P.product_code = FB.product_code AND D.domain_code = FB.domain_code ");
        strBuff.append(" AND FB.created_by = INTU.user_id(+) ");
        strBuff.append(" AND FBI.first_approved_by = FAPP.user_id(+) ");
        strBuff.append(" AND FBI.second_approved_by = SAPP.user_id(+) ");
        strBuff.append(" AND FBI.third_approved_by = TAPP.user_id(+) ");
        strBuff.append(" AND FBI.cancelled_by = CNCL_USR.user_id(+) ");
        strBuff.append(" AND U.owner_id = OWNU.user_id(+) ");
        strBuff.append(" AND CG.grade_code = FBI.user_grade_code ");
        strBuff.append(" AND L.lookup_type = ? ");
        strBuff.append(" AND L.lookup_code = FB.status ");

        strBuff.append(" ORDER BY FBI.batch_detail_id DESC, FBI.category_code, FBI.status ");
		return strBuff.toString();
	}
	
}
