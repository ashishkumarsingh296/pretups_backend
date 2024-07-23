package com.web.voms.voucher.businesslogic;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class VomsVoucherWebOracleQry implements VomsVoucherWebQry{
	@Override
	public String loadBatchLogListQry(){
		final StringBuilder sqlSelectBuf = new StringBuilder(
                " select va.serial_no SERIALNO, va.current_status CURRENTSTAT,  va.previous_status PREVSTAT,  US.user_name MODIFIEDBY, ");
            sqlSelectBuf.append("va.modified_on MODIFIEDON,");
            sqlSelectBuf.append(" va.status_change_source STATCHSRC, va.batch_no, nvl( va.message,'') MESSAGE, ");
            sqlSelectBuf.append(" v.expiry_date EXPDATE ");
            sqlSelectBuf.append(" FROM voms_voucher_audit va,voms_vouchers v,users US WHERE va.batch_no=? ");
            sqlSelectBuf.append(" AND va.modified_by=US.user_id AND va.SERIAL_NO=v.SERIAL_NO AND va.process_status =? order by va.serial_no");
            return sqlSelectBuf.toString();		
	}
	@Override
	public String getVomsVoucherListQry(String tablename){
		   final StringBuilder sqlSelectBuf = new StringBuilder("select serial_no, product_id, pin_no, current_status, ");
           sqlSelectBuf.append("expiry_date, user_network_code, status, talktime, validity, mrp,user_id");
           sqlSelectBuf.append(" FROM " + tablename + " where to_number(serial_no) BETWEEN ? and ? ");
           sqlSelectBuf.append(" and sale_batch_no=? order by to_number(serial_no)");
           return sqlSelectBuf.toString();
	}
	@Override
	public String selectVouchersOnSaleNo(String tablename) {
		final StringBuilder sqlSelectBuf = new StringBuilder("select pin_no || CHR(44) || serial_no || CHR(44) || round(mrp/"+((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()+",2) || CHR(44) || to_char(expiry_date, 'dd/mm/yyyy') as voucher ");
        sqlSelectBuf.append(" FROM " + tablename + " where to_number(serial_no) BETWEEN ? and ? ");
        sqlSelectBuf.append(" and sale_batch_no=? order by to_number(serial_no)");
		return sqlSelectBuf.toString();
	}
	
	
}
