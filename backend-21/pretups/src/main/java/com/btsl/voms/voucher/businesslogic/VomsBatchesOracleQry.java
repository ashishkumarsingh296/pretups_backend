package com.btsl.voms.voucher.businesslogic;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class VomsBatchesOracleQry implements VomsBatchesQry{
	@Override
	public String loadBatchListWithBatchNoQry(){
		StringBuilder strBuff = new StringBuilder("SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.reference_no REFERENCENO,B.product_id PRODUCTID, P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, B.status STATUS, nvl(B.message,'') message, ");
        strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO ");
        strBuff.append(" FROM voms_batches B, voms_products P WHERE B.product_id=P.product_id  AND");
        strBuff.append(" B.network_code=decode(?,?,B.network_code,?) ");
        strBuff.append(" AND B.batch_no=? ");
        return strBuff.toString();		
	}
	@Override
	public String loadBatchListQry(){
		StringBuilder strBuff = new StringBuilder("SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.product_id PRODUCTID,B.reference_no REFERENCENO, P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.first_approved_by, B.second_approved_by, B.third_approved_by, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, B.status STATUS, nvl(B.message,'') message, ");
        strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO,B.network_code,P.expiry_period,P.talktime,P.validity,P.expiry_date, B.voucher_segment ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
        	strBuff.append(" ,B.sequence_id ");
        }
        strBuff.append(" FROM voms_batches B, voms_products P WHERE B.product_id=P.product_id AND ");
        strBuff.append("B.network_code=DECODE(?,'ALL',B.network_code,?) AND B.status=DECODE(?,'ALL',B.status,?)");
        strBuff.append(" AND B.batch_type=DECODE(?,'ALL',B.batch_type,?) AND");
        strBuff.append(" trunc( B.modified_on)>=? AND trunc( B.modified_on)<=? order by B.batch_no desc");
        return strBuff.toString();
	}
	@Override
	public String loadBatchListOnDaysQry(){

        StringBuilder strBuff = new StringBuilder("SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.product_id PRODUCTID, B.reference_no REFERENCENO,P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, B.status STATUS, nvl(B.message,'') message, ");
        strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO ");
        strBuff.append(" FROM voms_batches B, voms_products P WHERE B.product_id=P.product_id ");
        strBuff.append(" AND B.network_code=DECODE(?,'ALL',B.network_code,?) ");
        strBuff.append(" AND B.created_date>=DECODE(?,'ALL',B.created_date,?) ");
        strBuff.append(" ORDER BY B.created_on DESC");
        return strBuff.toString();
	}
	public String loadAutoBatchListQry(){
		StringBuilder strBuff = new StringBuilder("SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.product_id PRODUCTID,B.reference_no REFERENCENO, P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, B.status STATUS, nvl(B.message,'') message, ");
        strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO,B.network_code,P.expiry_period,P.talktime,P.validity,P.expiry_date, B.voucher_segment ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
        	strBuff.append(" ,B.sequence_id ");
        }
        strBuff.append(" FROM voms_batches B, voms_products P WHERE B.product_id=P.product_id AND ");
        strBuff.append("B.network_code=DECODE(?,'ALL',B.network_code,?) AND B.status=DECODE(?,'ALL',B.status,?)");
        strBuff.append(" AND B.batch_type=DECODE(?,'ALL',B.batch_type,?) AND");
        strBuff.append(" trunc( B.modified_on)>=? AND trunc( B.modified_on)<=?  AND B.created_by = ? order by B.batch_no desc");
        return strBuff.toString();
	}
}
