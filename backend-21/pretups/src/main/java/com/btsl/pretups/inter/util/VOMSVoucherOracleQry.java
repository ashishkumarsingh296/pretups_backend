package com.btsl.pretups.inter.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class VOMSVoucherOracleQry implements VOMSVoucherQry{
	 private static Log _log = LogFactory.getLog(VOMSVoucherDAO.class);

	@Override
	public String loadActiveProfilesQry( boolean p_isTimeStamp) {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append(" SELECT vap.active_product_id,vap.applicable_from,");
		sqlSelectBuf.append("vi.product_id,vop.category_id,vop.product_name,vop.short_name,vop.mrp, ");
        sqlSelectBuf.append("vop.status,vop.description,vop.product_code,vop.min_req_quantity,vop.max_req_quantity, ");
        sqlSelectBuf.append("vop.multiple_factor,vop.expiry_period,vop.individual_entity,vop.attribute1, ");
        sqlSelectBuf.append("vop.service_code,vop.no_of_arguments,vop.talktime,vop.validity,vc.VOUCHER_TYPE,vop.VOUCHER_SEGMENT FROM ");
        sqlSelectBuf.append("voms_active_products vap,voms_active_product_items vi,voms_products vop,voms_categories vc,VOMS_VTYPE_SERVICE_MAPPING D ");
        sqlSelectBuf.append(" WHERE vap.active_product_id=vi.active_product_id AND vi.product_id=vop.product_id ");
        sqlSelectBuf.append("AND vop.category_id=vc.category_id AND vc.status<>'N' AND vop.status<>'N' AND vop.status<>'S' ");
        sqlSelectBuf.append("AND vap.network_code=? AND vap.applicable_from=(SELECT MAX(applicable_from) ");
        sqlSelectBuf.append(" FROM voms_active_products vap2 WHERE ");
        if (p_isTimeStamp)
            sqlSelectBuf.append(" applicable_from <=? ");
        else
            sqlSelectBuf.append(" trunc(applicable_from) <=? ");
        sqlSelectBuf.append(" AND vap2.network_code=? AND vap2.status<>'N') ");
        sqlSelectBuf.append(" AND D.SERVICE_TYPE= ? AND D.status <> 'N' AND D.VOUCHER_TYPE=VC.VOUCHER_TYPE ");
		return sqlSelectBuf.toString();
		
	}
	
	@Override
	public String insertDetailsInVoucherAuditQry() {
		StringBuilder sqlInsertBuf = new StringBuilder();
		sqlInsertBuf
				.append("INSERT INTO voms_voucher_audit (row_id,serial_no,");
		sqlInsertBuf.append("current_status,previous_status,modified_by,modified_on,status_change_source, ");
        sqlInsertBuf.append("status_change_partner_id,batch_no,message,process_status) VALUES (VOUCHER_AUDIT_ID.nextval,?,?,?,?,?,?,?,?,?,? )");
		
		return sqlInsertBuf.toString();
		
	}
	
	@Override
	public String insertDetailsInVoucherAuditListQry() {
		StringBuilder sqlInsertBuf = new StringBuilder();
		sqlInsertBuf
				.append("INSERT INTO voms_voucher_audit (row_id,serial_no,");
		sqlInsertBuf.append("current_status,previous_status,modified_by,modified_on,status_change_source, ");
        sqlInsertBuf.append("status_change_partner_id,batch_no,message,process_status) VALUES (VOUCHER_AUDIT_ID.nextval,?,?,?,?,?,?,?,?,?,? )");
		
		return sqlInsertBuf.toString();
		
	}
	
	@Override
	public String loadDownloadedVouchersForEnquiryQry(boolean p_IsBatchIdEneterd) {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append("SELECT vv.SERIAL_NO, vv.PIN_NO, vv.SALE_BATCH_NO, vp.VALIDITY, vv.CURRENT_STATUS, vv.EXPIRY_DATE, vv.STATUS, vv.LAST_TRANSACTION_ID, vp.MRP, vv.GENERATION_BATCH_NO FROM VOMS_VOUCHERS vv, VOMS_PRODUCTS vp WHERE ");
		if (p_IsBatchIdEneterd) {
            sqlSelectBuf.append("vv.SALE_BATCH_NO=? AND ");
        } else {
            sqlSelectBuf.append("vp.MRP=? AND ");
            sqlSelectBuf.append("trunc(vv.LAST_CONSUMED_ON)>=? AND ");
            sqlSelectBuf.append("trunc(vv.LAST_CONSUMED_ON)<=? AND ");
        }
        sqlSelectBuf.append("vp.PRODUCT_ID=vv.PRODUCT_ID AND ");
        sqlSelectBuf.append("vv.CURRENT_STATUS=? AND ");
        sqlSelectBuf.append("vv.LAST_CONSUMED_BY=?");
		
		return sqlSelectBuf.toString();
		
	}
	
	@Override
	public String loadActiveProfilesForPrivateRechargeQry(boolean p_isTimeStamp) {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append("SELECT vap.active_product_id,vap.applicable_from, ");
		sqlSelectBuf.append("vi.product_id,vop.category_id,vop.product_name,vop.short_name,vop.mrp, ");
        sqlSelectBuf.append("vop.status,vop.description,vop.product_code,vop.min_req_quantity,vop.max_req_quantity, ");
        sqlSelectBuf.append("vop.multiple_factor,vop.expiry_period,vop.individual_entity,vop.attribute1, ");
		sqlSelectBuf.append("vop.service_code,vop.no_of_arguments,vop.talktime,vop.validity,vc.type,vc.VOUCHER_TYPE FROM ");
		sqlSelectBuf.append("voms_active_products vap,voms_active_product_items vi,voms_products vop,voms_categories vc ,VOMS_VTYPE_SERVICE_MAPPING D ");
        sqlSelectBuf.append(" WHERE vap.active_product_id=vi.active_product_id AND vi.product_id=vop.product_id ");
        sqlSelectBuf.append("AND vop.category_id=vc.category_id AND vc.status<>'N' AND vop.status<>'N' ");
        sqlSelectBuf.append("AND vap.network_code=? AND vap.applicable_from=(SELECT MAX(applicable_from) ");
        sqlSelectBuf.append(" FROM voms_active_products vap2 WHERE ");
        if (p_isTimeStamp)
            sqlSelectBuf.append(" applicable_from <=? ");
        else
            sqlSelectBuf.append(" trunc(applicable_from) <=? ");
        sqlSelectBuf.append(" AND vap2.network_code=? AND vap2.status<>'N') ");
		sqlSelectBuf.append(" AND D.SERVICE_TYPE= ? AND D.status <> 'N' AND D.VOUCHER_TYPE=VC.VOUCHER_TYPE ");
		
		return sqlSelectBuf.toString();
		
	}
	

	@Override
	public String loadActiveProfilesSelectQry(boolean p_isTimeStamp) {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append("SELECT vap.active_product_id,vap.applicable_from, ");
		sqlSelectBuf.append("vi.product_id,vop.category_id,vop.product_name,vop.short_name,vop.mrp, ");
        sqlSelectBuf.append("vop.status,vop.description,vop.product_code,vop.min_req_quantity,vop.max_req_quantity, ");
        sqlSelectBuf.append("vop.multiple_factor,vop.expiry_period,vop.individual_entity,vop.attribute1, ");
        sqlSelectBuf.append("vop.service_code,vop.no_of_arguments,vop.talktime,vop.validity FROM ");
        sqlSelectBuf.append("voms_active_products vap,voms_active_product_items vi,voms_products vop,voms_categories vc ");
        sqlSelectBuf.append(" WHERE vap.active_product_id=vi.active_product_id AND vi.product_id=vop.product_id ");
        sqlSelectBuf.append("AND vop.category_id=vc.category_id AND vc.status<>'N' AND vop.status<>'N' ");
        sqlSelectBuf.append("AND vap.network_code=? AND vap.applicable_from=(SELECT MAX(applicable_from) ");
        sqlSelectBuf.append(" FROM voms_active_products vap2 WHERE ");
        if (p_isTimeStamp)
            sqlSelectBuf.append(" applicable_from <=? ");
        else
            sqlSelectBuf.append(" trunc(applicable_from) <=? ");
        sqlSelectBuf.append(" AND vap2.network_code=? AND vap2.status<>'N') ");
		
		return sqlSelectBuf.toString();
		
	}
	
	@Override
	public String loadPINAndSerialNumberQry(Connection p_con, VOMSProductVO p_productVO,int p_quantityRequested) throws SQLException {
		
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append(" SELECT VV.serial_no,VV.pin_no,VV.expiry_date,VV.previous_status,VV.seq_no,VV.created_date,VV.product_id  ");
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
            sqlSelectBuf.append(" FROM VOMS_VOUCHERS VV, ENABLE_SUMMARY_DATES ESD WHERE ");
        else {
            sqlSelectBuf.append(",VP.talktime,VC.PAYABLE_AMOUNT FROM VOMS_VOUCHERS VV, VOMS_CATEGORIES VC,VOMS_PRODUCTS VP, ENABLE_SUMMARY_DATES ESD  ");
            sqlSelectBuf.append(" WHERE VV.PRODUCT_ID=VP.PRODUCT_ID AND VP.CATEGORY_ID=VC.CATEGORY_ID AND ");
        }
        sqlSelectBuf.append("  VV.CURRENT_STATUS=? AND VV.PRODUCT_ID=? AND ESD.PRODUCT_ID = ? ");
        sqlSelectBuf.append("AND VV.CREATED_DATE = ESD.CRE_DATE ");

        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
            sqlSelectBuf.append("AND ROWNUM<=? FOR UPDATE OF VV.CURRENT_STATUS WITH RS ");
        else
            sqlSelectBuf.append("AND ROWNUM<=? FOR UPDATE OF VV.CURRENT_STATUS ");
		
		if (_log.isDebugEnabled())
			_log.debug("loadPINAndSerialNumber", "Select Query=" + sqlSelectBuf.toString());
       
        
        return sqlSelectBuf.toString();
		
	}

	@Override
	public StringBuilder loadVomsVoucherVObyUserId(String productId,String orderBy) {
		StringBuilder sqlSelectBuf = new StringBuilder("Select * from (");
		 sqlSelectBuf.append("Select SERIAL_NO, current_status,  MRP, PIN_NO,");
          sqlSelectBuf.append(" product_id , VOUCHER_TYPE, VOUCHER_SEGMENT, EXPIRY_DATE ");
          sqlSelectBuf.append(" FROM voms_vouchers WHERE current_status= ? and USER_ID= ? AND VOUCHER_TYPE = ? AND VOUCHER_SEGMENT = ?");
          if(!BTSLUtil.isNullString(productId))
              sqlSelectBuf.append(" AND PRODUCT_ID = ? ");
          sqlSelectBuf.append(" AND MRP = ? AND USER_NETWORK_CODE = ? AND SUBSCRIBER_ID IS NULL ORDER BY ");
          sqlSelectBuf.append(orderBy);
          sqlSelectBuf.append(" ) WHERE  ROWNUM < =?");
		return sqlSelectBuf;
	}

	@Override
	public String loadVomsVoucherByMasterSerialNumber(String masterSerialNumber) {
		
		StringBuffer sqlSelectBuf =new StringBuffer(" select SERIAL_NO,PRODUCT_ID,CURRENT_STATUS,PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,GENERATION_BATCH_NO,MRP,LAST_BATCH_NO,ENABLE_BATCH_NO,EXPIRY_DATE,MODIFIED_ON from VOMS_VOUCHERS   "); 
		sqlSelectBuf.append(" where MASTER_SERIAL_NO=? and PRODUCTION_NETWORK_CODE=?");
		
		return sqlSelectBuf.toString();
	}
	@Override
	public String loadPINAndSerialNumberQryBulk(Connection p_con, VOMSProductVO p_productVO,int p_quantityRequested) throws SQLException {
		
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append(" SELECT VV.serial_no,VV.pin_no,VV.expiry_date,VV.previous_status,VV.seq_no,VV.created_date,VV.product_id  ");
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
            sqlSelectBuf.append(" FROM VOMS_VOUCHERS VV WHERE ");
        else {
            sqlSelectBuf.append(",VP.talktime,VC.PAYABLE_AMOUNT FROM VOMS_VOUCHERS VV, VOMS_CATEGORIES VC,VOMS_PRODUCTS VP  ");
            sqlSelectBuf.append(" WHERE VV.PRODUCT_ID=VP.PRODUCT_ID AND VP.CATEGORY_ID=VC.CATEGORY_ID AND ");
        }
        sqlSelectBuf.append("  VV.CURRENT_STATUS=? AND VV.PRODUCT_ID=? ");        
	sqlSelectBuf.append("  AND VV.expiry_date >=(SYSDATE + (SELECT DEFAULT_VALUE  FROM system_preferences WHERE preference_code = 'VOMS_MIN_EXPIRY_DAYS')) ");

        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
            sqlSelectBuf.append("AND ROWNUM<=? FOR UPDATE OF VV.CURRENT_STATUS WITH RS ");
        else
            sqlSelectBuf.append("AND ROWNUM<=? FOR UPDATE OF VV.CURRENT_STATUS ");
		
	
        
        return sqlSelectBuf.toString();
		
	}
}
