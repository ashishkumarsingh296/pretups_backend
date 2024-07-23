package com.txn.voms.voucher.businesslogic;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;


public class VomsVoucherTxnPostgresQry implements VomsVoucherTxnQry {

	@Override
	public StringBuilder loadDatafromVoms(String tablename, String sno) {

		
		StringBuilder sqlSelectBuf = new StringBuilder("SELECT vv.VOUCHER_TYPE,vv.serial_no,vv.pin_no,vv.current_status,vv.expiry_date,vv.production_network_code,vv.mrp,vv.previous_status,vv.talktime,vv.validity,vv.first_consumed_on,vv.product_id,vv.subscriber_id,vp.product_name,vv.voucher_segment ");
		sqlSelectBuf.append("FROM " + tablename + " vv" + "," + "voms_products" + " vp"  + " WHERE ");
		if(BTSLUtil.isNullString(sno))
			sqlSelectBuf.append("vv.pin_no=? and vp.product_id = vv.product_id");
		else
			sqlSelectBuf.append("vv.pin_no=? and vv.serial_no=? and vp.product_id = vv.product_id");
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
			sqlSelectBuf.append(" and sequence_id=? ");
		}
		sqlSelectBuf.append(" FOR UPDATE ");
		return sqlSelectBuf;

	}

	@Override
	public StringBuilder insertVomsVoucherAudit() {
		StringBuilder sqlSelectBuf = new StringBuilder("INSERT INTO voms_voucher_audit(row_id, serial_no, current_status, previous_status, modified_by, modified_on, status_change_source,message)");
        sqlSelectBuf.append("VALUES (nextval('voucher_audit_id'),?,?,?,?,?,?,?)");
        
        return sqlSelectBuf;
	}

	@Override
	public StringBuilder getVoucherForMrp() {
		
		StringBuilder selectBuff= new StringBuilder(" select  serial_no,pin_no,EXPIRY_DATE,TALKTIME,VALIDITY from VOMS_VOUCHERS_SNIFFER where product_id=? and mrp=? and EXPIRY_DATE>=date_trunc('day',current_timestamp::timestamp) ");
		selectBuff.append("and current_status=? and subscriber_id is null limit ?  FOR UPDATE  ");
        
       
		return selectBuff;
		
		
	}

	@Override
	public int getLimitOrRownumValue() {
		
		return 0;
	}
	
	@Override
	public StringBuilder loadDatafromVoms1(String tablename) {
		StringBuilder sqlSelectBuf = new StringBuilder("SELECT serial_no,current_status,expiry_date,production_network_code,mrp,previous_status ");
        sqlSelectBuf.append("FROM " + tablename);
        sqlSelectBuf.append(" WHERE pin_no=?  and serial_no =? FOR UPDATE ");
        
		return sqlSelectBuf;
	}

	@Override
	public StringBuilder insertVomsVoucherAudit1() {
		StringBuilder sqlSelectBuf = new StringBuilder("INSERT INTO voms_voucher_audit(row_id, serial_no, current_status, previous_status, modified_by, modified_on, status_change_source,message)");
        sqlSelectBuf.append("VALUES (nextval('voucher_audit_id'),?,?,?,?,?,?,?)");
        
        return sqlSelectBuf;
	}


}
