package com.inter.umniah.voms;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.Constants;

public class VOMSVoucherPostgresDAO implements VOMSVoucherQry {
	@Override
	public String loadActiveProfilesQry(boolean pisTimeStamp){
		StringBuilder sqlSelectBuf = new StringBuilder("SELECT vap.active_product_id,vap.applicable_from, ");
        sqlSelectBuf.append(" vi.product_id,vop.category_id,vop.product_name,vop.short_name,vop.mrp, ");
        sqlSelectBuf.append(" vop.status,vop.description,vop.product_code,vop.min_req_quantity,vop.max_req_quantity, ");
        sqlSelectBuf.append(" vop.multiple_factor,vop.expiry_period,vop.individual_entity,vop.attribute1, ");
        sqlSelectBuf.append(" vop.service_code,vop.no_of_arguments,vop.talktime,vop.validity FROM ");
        sqlSelectBuf.append(" voms_active_products vap,voms_active_product_items vi,voms_products vop,voms_categories vc ");
        sqlSelectBuf.append(" WHERE vap.active_product_id=vi.active_product_id AND vi.product_id=vop.product_id ");
        sqlSelectBuf.append(" AND vop.category_id=vc.category_id AND vc.status<>'N' AND vop.status<>'N' ");
        sqlSelectBuf.append(" AND vap.network_code=? AND vap.applicable_from=(SELECT MAX(applicable_from) ");
        sqlSelectBuf.append(" FROM voms_active_products vap2 WHERE ");
        if (pisTimeStamp)
            sqlSelectBuf.append(" applicable_from <=? ");
        else
            sqlSelectBuf.append(" date_trunc('day',applicable_from::TIMESTAMP) <=? ");
        sqlSelectBuf.append(" AND vap2.network_code=? AND vap2.status<>'N') ");
        return sqlSelectBuf.toString();
	}
	@Override
	public String loadDownloadedVouchersForEnquiryQry(boolean pIsBatchIdEneterd){
	     StringBuilder sqlSelectBuf = new StringBuilder("SELECT vv.SERIAL_NO, vv.PIN_NO, vv.SALE_BATCH_NO, vp.VALIDITY, vv.CURRENT_STATUS, vv.EXPIRY_DATE, vv.STATUS, vv.LAST_TRANSACTION_ID, vp.MRP, vv.GENERATION_BATCH_NO FROM VOMS_VOUCHERS vv, VOMS_PRODUCTS vp WHERE ");
         if (pIsBatchIdEneterd) {
             sqlSelectBuf.append("vv.SALE_BATCH_NO=? AND ");
         } else {
             sqlSelectBuf.append("vp.MRP=? AND ");
             sqlSelectBuf.append("date_trunc('day',vv.LAST_CONSUMED_ON::TIMESTAMP)>=? AND ");
             sqlSelectBuf.append("date_trunc('day',vv.LAST_CONSUMED_ON::TIMESTAMP)<=? AND ");
         }
         sqlSelectBuf.append("vp.PRODUCT_ID=vv.PRODUCT_ID AND ");
         sqlSelectBuf.append("vv.CURRENT_STATUS=? AND ");
         sqlSelectBuf.append("vv.LAST_CONSUMED_BY=?");
         return sqlSelectBuf.toString();
	}
	@Override
	public String loadActiveProfilesForPrivateRechargeQry(boolean pisTimeStamp){
		StringBuilder sqlSelectBuf = new StringBuilder("SELECT vap.active_product_id,vap.applicable_from, ");
        sqlSelectBuf.append("vi.product_id,vop.category_id,vop.product_name,vop.short_name,vop.mrp, ");
        sqlSelectBuf.append("vop.status,vop.description,vop.product_code,vop.min_req_quantity,vop.max_req_quantity, ");
        sqlSelectBuf.append("vop.multiple_factor,vop.expiry_period,vop.individual_entity,vop.attribute1, ");
        sqlSelectBuf.append("vop.service_code,vop.no_of_arguments,vop.talktime,vop.validity,vc.type FROM ");
        sqlSelectBuf.append("voms_active_products vap,voms_active_product_items vi,voms_products vop,voms_categories vc ");
        sqlSelectBuf.append(" WHERE vap.active_product_id=vi.active_product_id AND vi.product_id=vop.product_id ");
        sqlSelectBuf.append("AND vop.category_id=vc.category_id AND vc.status<>'N' AND vop.status<>'N' ");
        sqlSelectBuf.append("AND vap.network_code=? AND vap.applicable_from=(SELECT MAX(applicable_from) ");
        sqlSelectBuf.append(" FROM voms_active_products vap2 WHERE ");
        if (pisTimeStamp)
            sqlSelectBuf.append(" applicable_from <=? ");
        else
            sqlSelectBuf.append(" date_trunc('day',applicable_from::TIMESTAMP) <=? ");
        sqlSelectBuf.append(" AND vap2.network_code=? AND vap2.status<>'N') ");
        return sqlSelectBuf.toString();
	}
	@Override
	public String loadPINAndSerialNumberQry(){
		
		StringBuilder sqlSelectBuf = new StringBuilder("SELECT VV.serial_no,VV.pin_no,VV.expiry_date,VV.previous_status,VV.seq_no,VV.created_date,VV.product_id ");
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
            sqlSelectBuf.append(" FROM VOMS_VOUCHERS VV, ENABLE_SUMMARY_DATES ESD WHERE ");
        else {
            sqlSelectBuf.append(",VP.talktime,VC.PAYABLE_AMOUNT FROM VOMS_VOUCHERS VV, VOMS_CATEGORIES VC,VOMS_PRODUCTS VP, ENABLE_SUMMARY_DATES ESD  ");
            sqlSelectBuf.append(" WHERE VV.PRODUCT_ID=VP.PRODUCT_ID AND VP.CATEGORY_ID=VC.CATEGORY_ID AND ");
        }
        sqlSelectBuf.append("  VV.CURRENT_STATUS=? AND VV.PRODUCT_ID=? AND ESD.PRODUCT_ID = ? ");
        sqlSelectBuf.append("AND VV.CREATED_DATE = ESD.CRE_DATE ");
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
            sqlSelectBuf.append("AND ROWNUM<? FOR UPDATE OF VV.CURRENT_STATUS WITH RS ");
        else
            sqlSelectBuf.append(" FOR UPDATE LIMIT ? ");
        return sqlSelectBuf.toString();
	}
	@Override
	public String loadActiveProfilesForPrivateRechargeQry2(boolean pisTimeStamp){
		StringBuilder sqlSelectBuf = new StringBuilder("SELECT vap.active_product_id,vap.applicable_from, ");	
    sqlSelectBuf.append("vi.product_id,vop.category_id,vop.product_name,vop.short_name,vop.mrp, ");
    sqlSelectBuf.append("vop.status,vop.description,vop.product_code,vop.min_req_quantity,vop.max_req_quantity, ");
    sqlSelectBuf.append("vop.multiple_factor,vop.expiry_period,vop.individual_entity,vop.attribute1, ");
	sqlSelectBuf.append("vop.service_code,vop.no_of_arguments,vop.talktime,vop.validity,vc.type,vc.VOUCHER_TYPE FROM ");
	sqlSelectBuf.append("voms_active_products vap,voms_active_product_items vi,voms_products vop,voms_categories vc ,VOMS_VTYPE_SERVICE_MAPPING D ");
    sqlSelectBuf.append(" WHERE vap.active_product_id=vi.active_product_id AND vi.product_id=vop.product_id ");
    sqlSelectBuf.append("AND vop.category_id=vc.category_id AND vc.status<>'N' AND vop.status<>'N' ");
    sqlSelectBuf.append("AND vap.network_code=? AND vap.applicable_from=(SELECT MAX(applicable_from) ");
    sqlSelectBuf.append(" FROM voms_active_products vap2 WHERE ");
    if (pisTimeStamp)
        sqlSelectBuf.append(" applicable_from <=? ");
    else
        sqlSelectBuf.append(" date_trunc('day',applicable_from::TIMESTAMP) <=? ");
    sqlSelectBuf.append(" AND vap2.network_code=? AND vap2.status<>'N') ");
	//Added for Multi product EVD
	sqlSelectBuf.append(" AND D.SERVICE_TYPE= ? AND D.status <> 'N' AND D.VOUCHER_TYPE=VC.VOUCHER_TYPE ");
	return sqlSelectBuf.toString();
	}
}
