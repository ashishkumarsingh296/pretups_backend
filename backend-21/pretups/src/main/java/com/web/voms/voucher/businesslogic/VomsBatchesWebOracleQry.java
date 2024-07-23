package com.web.voms.voucher.businesslogic;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;

public class VomsBatchesWebOracleQry implements VomsBatchesWebQry {

	@Override
	public String loadBatchListWithBatchNoNewQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.reference_no REFERENCENO,B.product_id PRODUCTID,B.signed_doc SIGNEDDOC, P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, B.status STATUS, nvl(B.message,'') message,");
		strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO ,U.user_name ,U.msisdn, B.FIRST_APPROVER_REMARKS, B.SECOND_APPROVER_REMARKS, B.THIRD_APPROVER_REMARKS, ");
		strBuff.append(" (SELECT USER_NAME FROM USERS WHERE USER_ID = B.FIRST_APPROVED_BY) FIRST_APPROVED_BY, B.FIRST_APPROVED_ON, ");
		strBuff.append(" (SELECT USER_NAME FROM USERS WHERE USER_ID = B.SECOND_APPROVED_BY) SECOND_APPROVED_BY, B.SECOND_APPROVED_ON, ");
		strBuff.append(" (SELECT USER_NAME FROM USERS WHERE USER_ID = B.THIRD_APPROVED_BY) THIRD_APPROVED_BY, B.THIRD_APPROVED_ON ");
		strBuff.append(" FROM voms_batches B, voms_products P,USERS U " );
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
         {
			strBuff.append(" , voms_categories vc ");
	     }
		strBuff.append("WHERE B.product_id=P.product_id  AND B.USER_ID=U.USER_ID(+) AND");
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        {
			strBuff.append(" p.category_id = vc.category_id AND ");
	    }
		strBuff.append(" B.network_code=decode(?,?,B.network_code,?) ");
		strBuff.append(" AND B.batch_no=? ");
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        {
			strBuff.append(" AND vc.voucher_type in ( SELECT uv.voucher_type FROM user_vouchertypes uv, voms_types vt, users u WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id  ) ");
	    }
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue()) {
			strBuff.append(" AND vc.voucher_segment in (SELECT us.VOUCHER_SEGMENT FROM USER_VOUCHER_SEGMENTS us, LOOKUPS lu, users u WHERE us.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE  AND u.user_id = us.user_id)");
		}
		return strBuff.toString();
	}

	@Override
	public String loadBatchListNewQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.product_id PRODUCTID,B.signed_doc SIGNEDDOC, B.reference_no REFERENCENO, P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, B.status STATUS, nvl(B.message,'') message,");
		strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO,B.network_code,P.expiry_period,P.talktime,P.validity,U.user_name ,U.msisdn ");
		strBuff.append(" FROM voms_batches B, voms_products P ,users U " );
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
			strBuff.append(" , voms_categories vc ");
		strBuff.append(" WHERE B.product_id=P.product_id AND ");
		
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
			strBuff.append(" p.category_id = vc.category_id AND ");
	    
		
		strBuff.append("B.network_code=DECODE(?,'ALL',B.network_code,?) AND B.status=DECODE(?,'ALL',B.status,?)");
		strBuff.append(" AND B.batch_type=DECODE(?,'ALL',B.batch_type,?) AND  B.user_id=U.user_id(+) AND");
		strBuff.append(" TRUNC(B.created_date)>=? AND TRUNC(B.created_date)<=? " );
		
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        {
			strBuff.append(" AND vc.voucher_type in ( SELECT uv.voucher_type FROM user_vouchertypes uv, voms_types vt, users u WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id  ) ");
	    }
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue()) {
			strBuff.append(" AND vc.voucher_segment in (SELECT us.VOUCHER_SEGMENT FROM USER_VOUCHER_SEGMENTS us, LOOKUPS lu, users u WHERE us.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE  AND u.user_id = us.user_id) ");
		}
		strBuff.append(" order by B.created_on desc");
		return strBuff.toString();
	}

	@Override
	public String loadBatchListOnDaysNewQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.product_id PRODUCTID, B.signed_doc SIGNEDDOC, B.reference_no REFERENCENO,P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, B.status STATUS, nvl(B.message,'') message,");
		strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO,U.USER_NAME,U.msisdn ");
		strBuff.append(" FROM voms_batches B, voms_products P,Users U " );
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
        {
			strBuff.append(" , voms_categories vc ");
	    }
		strBuff.append(" WHERE B.product_id=P.product_id ");
		strBuff.append(" AND B.network_code=DECODE(?,'ALL',B.network_code,?) ");
		strBuff.append(" AND TRUNC(B.created_date)>=DECODE(?,'ALL',B.created_date,?) AND B.USER_ID=U.USER_ID(+) ");
		
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        {
			strBuff.append(" and p.category_id = vc.category_id ");
			strBuff.append(" AND vc.voucher_type in ( SELECT uv.voucher_type FROM user_vouchertypes uv, voms_types vt, users u WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id  ) ");
	    }
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue()) {
			strBuff.append(" AND vc.voucher_segment in (SELECT us.VOUCHER_SEGMENT FROM USER_VOUCHER_SEGMENTS us, LOOKUPS lu, users u WHERE us.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE  AND u.user_id = us.user_id)");
		}
		
		strBuff.append(" ORDER BY B.created_on DESC");
		return strBuff.toString();
	}

	@Override
	public String loadBatchListOnStatusQry(String p_productid) {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.reference_no REFERENCENO,B.product_id PRODUCTID,B.signed_doc SIGNEDDOC, P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, l.LOOKUP_NAME STATUS, nvl(B.message,'') message,");
		strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO,B.remarks, B.SEQUENCE_ID, P.EXPIRY_PERIOD, P.EXPIRY_DATE, B.FIRST_APPROVED_BY, B.FIRST_APPROVER_REMARKS, B.SECOND_APPROVED_BY, B.SECOND_APPROVER_REMARKS, B.THIRD_APPROVED_BY, B.THIRD_APPROVER_REMARKS, P.VOUCHER_SEGMENT, B.NETWORK_CODE ");
		strBuff.append(" FROM voms_batches B, voms_products P,LOOKUPS l,LOOKUP_TYPES lt WHERE B.product_id=P.product_id AND l.LOOKUP_TYPE=lt.LOOKUP_TYPE and B.status=l.LOOKUP_CODE and  ");
		strBuff.append("B.network_code=decode(?,?,B.network_code,?) AND B.status=? and l.LOOKUP_TYPE=? and P.voucher_segment = ? ");
		
		if (!BTSLUtil.isNullString(p_productid)) {
			strBuff.append("AND B.product_id=? ");
		}
		return strBuff.toString();
	}

	@Override
	public String loadBatchListOnStatusNewQry(String p_productid) {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.reference_no REFERENCENO,B.product_id PRODUCTID,B.signed_doc SIGNEDDOC, P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, B.status STATUS, nvl(B.message,'') message,");
		strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO,B.remarks,U.USER_NAME,U.MSISDN ");
		strBuff.append(" FROM voms_batches B, voms_products P,USERS U " );
		
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
        strBuff.append(" , voms_categories vc ");
	     
		strBuff.append(" WHERE B.product_id=P.product_id AND ");
		strBuff.append("B.network_code=decode(?,?,B.network_code,?) AND B.status=? AND B.USER_ID=U.USER_ID(+)");
		if (!BTSLUtil.isNullString(p_productid)) {
			strBuff.append("AND B.product_id=? ");
			
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
	        {
				strBuff.append(" AND vc.voucher_type in ( SELECT uv.voucher_type FROM user_vouchertypes uv, voms_types vt, users u WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id  ) ");
		    }
			
		}
		else
		{
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
	        {
				strBuff.append(" AND vc.voucher_type in ( SELECT uv.voucher_type FROM user_vouchertypes uv, voms_types vt, users u WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id  ) ");
		    }
			
		}
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue()) {
			strBuff.append(" AND vc.voucher_segment in (SELECT us.VOUCHER_SEGMENT FROM USER_VOUCHER_SEGMENTS us, LOOKUPS lu, users u WHERE us.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE  AND u.user_id = us.user_id) ");
		}
		return strBuff.toString();
	}

	@Override
	public String getVomsPrinterBatchQry(String p_batchType,UserVO userVO) {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append(" SELECT distinct P.printer_batch_id,P.start_serial_no,P.end_serial_no,P.user_id,P.downloaded,");
		sqlSelectBuf
				.append("P.product_id,P.voms_decryp_key,P.total_no_of_vouchers,P.created_on,P.created_by,");
		sqlSelectBuf
				.append("P.modified_on,P.modified_by,PR.product_name,PR.mrp,VC.voucher_type,VC.voucher_segment FROM VOMS_PRINT_BATCHES P,VOMS_VOUCHERS vomss, ");
		sqlSelectBuf.append("VOMS_PRODUCTS PR,voms_categories VC ");
		if (("Y".equals(p_batchType) || "N".equals(p_batchType))) {
			sqlSelectBuf
					.append("WHERE PR.product_id=P.product_id and P.downloaded=? and ");

		} else {
			sqlSelectBuf.append("WHERE PR.product_id=P.product_id and ");
		}
		if((PretupsI.USER_TYPE_CHANNEL).equals(userVO.getUserType()))
		{
			sqlSelectBuf.append("P.product_id = vomss.PRODUCT_ID and ");
		}
		sqlSelectBuf
				.append("PR.category_id=VC.category_id and trunc(P.created_on)>=? and trunc(P.created_on)<=? and VC.network_code=?");
		if((PretupsI.USER_TYPE_CHANNEL).equals(userVO.getUserType()))
		{
			sqlSelectBuf.append("AND vomss.user_id = ? AND vomss.CURRENT_STATUS = 'EN' ");
		}
		sqlSelectBuf.append("order by P.created_on ");
		return sqlSelectBuf.toString();
	}

	@Override
	public String updateVomsPrintBatchstatusQry() {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append(" UPDATE voms_batches SET download_count=download_count+1,last_download_on=?");
		sqlSelectBuf
				.append(" WHERE(to_number(from_serial_no) <=to_number(?) AND to_number(to_serial_no)>=to_number(?)) ");
		return sqlSelectBuf.toString();
	}

	@Override
	public String getBatchInfoForUserInputsQry() {
		StringBuilder sqlLoadBuf = new StringBuilder();
		sqlLoadBuf
				.append(" SELECT b.batch_no batchno,b.from_serial_no,b.to_serial_no,b.batch_type bType,b.total_no_of_vouchers,nvl(b.download_count,0) DOWNCOUNT,b.sequence_id,p.voucher_segment ");
		sqlLoadBuf
				.append(" from voms_batches b,voms_products p WHERE p.product_id=? AND p.product_id=b.product_id ");
		sqlLoadBuf.append(" AND p.mrp=? AND b.from_serial_no <=? ");
		sqlLoadBuf.append(" AND b.to_serial_no >=? AND b.status=? AND p.network_code = ? ");
		sqlLoadBuf.append(" AND b.BATCH_TYPE= '" + VOMSI.BATCH_GENERATED + "'");
		return sqlLoadBuf.toString();
	}
	

	@Override
	public String getBatchInfoForUserInputsSelectQry() {
		StringBuilder sqlBatchBuf = new StringBuilder();
		sqlBatchBuf
				.append(" SELECT b.batch_no batchno,b.from_serial_no fserial,b.to_serial_no tserial,b.batch_type bType,b.total_no_of_vouchers totalvouch, p.voucher_segment ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
        	sqlBatchBuf.append(" ,b.sequence_id ");
        }
		sqlBatchBuf
				.append(" from voms_batches b,voms_products p WHERE p.product_id=? AND p.product_id=b.product_id AND p.mrp=? ");
		sqlBatchBuf
				.append(" AND ((b.from_serial_no <=? AND b.to_serial_no>=?) ");
		sqlBatchBuf
				.append(" OR (b.from_serial_no >=? AND b.from_serial_no <=?) ");
		sqlBatchBuf
				.append(" OR (b.to_serial_no >= ? AND b.to_serial_no <=?) ) ");
		sqlBatchBuf.append(" AND b.status=? ");
		// sqlBatchBuf.append(" AND b.location_code=? " );
		sqlBatchBuf
				.append(" AND b.BATCH_TYPE= '" + VOMSI.BATCH_GENERATED + "'");
		return sqlBatchBuf.toString();
	}

	@Override
	public String loadBatchListForMsisdnQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT B.batch_no BATCHNO,B.batch_type BATCHTYPE,B.product_id PRODUCTID,B.signed_doc SIGNEDDOC, B.reference_no REFERENCENO, P.product_name PRODUCTNAME, P.mrp MRP, B.created_by CREATEDBY, B.total_no_of_vouchers TOTALVOUCHER, B.created_on CREATEDON, B.status STATUS, nvl(B.message,'') message,");
		strBuff.append(" B.created_date CREATEDATE, B.download_count COUNT, B.last_download_on DOWNLOADON,B.total_no_of_failure FAILCOUNT, B.total_no_of_success SUCCCOUNT, B.from_serial_no FROMSERIALNO, B.to_serial_no TOSERIALNO,B.network_code,P.expiry_period,P.talktime,P.validity,U.user_name ,U.msisdn ");
		strBuff.append(" FROM voms_batches B, voms_products P ,users U " );
		
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
			strBuff.append(" , voms_categories vc ");
		strBuff.append(" WHERE B.product_id=P.product_id AND ");
		strBuff.append("B.network_code=DECODE(?,'ALL',B.network_code,?) ");
		strBuff.append(" AND  B.user_id=U.user_id(+)  AND ");
		
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
			strBuff.append("  p.category_id = vc.category_id AND " );
		
		strBuff.append(" U.msisdn= ?  " );
		
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        {
			strBuff.append(" AND vc.voucher_type in ( SELECT uv.voucher_type FROM user_vouchertypes uv, voms_types vt, users u WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id  ) ");
	    }
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue()) {
			strBuff.append(" AND vc.voucher_segment in (SELECT us.VOUCHER_SEGMENT FROM USER_VOUCHER_SEGMENTS us, LOOKUPS lu, users u WHERE us.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE  AND u.user_id = us.user_id) ");
		}
		strBuff.append(" order by B.batch_no desc");
		return strBuff.toString();
	}

	@Override
	public String getBatchInfoForSelectUserInputsQry() {
		StringBuilder sqlLoadBuf = new StringBuilder();
		sqlLoadBuf
				.append("SELECT b.batch_no batchno,b.from_serial_no,b.to_serial_no,b.batch_type bType,b.total_no_of_vouchers,nvl(b.download_count,0) DOWNCOUNT, b.sequence_id");
		sqlLoadBuf
				.append(" from voms_batches b,voms_products p WHERE p.product_id=? AND p.product_id=b.product_id ");
		sqlLoadBuf.append(" AND p.mrp=? AND to_number(b.from_serial_no) <=? ");
		sqlLoadBuf.append(" AND to_number(b.to_serial_no) >=? AND b.status=? ");
		sqlLoadBuf.append(" AND b.BATCH_TYPE IN ('" + VOMSI.BATCH_GENERATED
				+ "','" + VOMSI.BATCH_INTIATED + "')");
		return sqlLoadBuf.toString();
	}

	@Override
	public String getBatchInfoForSelectUserInputsBatchBuffQry() {
		StringBuilder sqlBatchBuf = new StringBuilder();
		sqlBatchBuf
				.append("SELECT b.batch_no batchno,b.from_serial_no fserial,b.to_serial_no tserial,b.batch_type bType,b.total_no_of_vouchers totalvouch");
		sqlBatchBuf
				.append(" from voms_batches b,voms_products p WHERE p.product_id=? AND p.product_id=b.product_id AND p.mrp=? ");
		sqlBatchBuf
				.append(" AND ((to_number(b.from_serial_no) <=? AND to_number(b.to_serial_no)>=?) ");
		sqlBatchBuf
				.append(" OR (to_number(b.from_serial_no) >=? AND to_number(b.from_serial_no) <=?) ");
		sqlBatchBuf
				.append(" OR (to_number(b.to_serial_no) >= ? AND to_number(b.to_serial_no) <=?) ) ");
		sqlBatchBuf.append(" AND b.status=? ");
		sqlBatchBuf.append(" AND b.BATCH_TYPE IN ('" + VOMSI.BATCH_GENERATED
				+ "','" + VOMSI.BATCH_INTIATED + "')");
		return sqlBatchBuf.toString();
	}

	@Override
	public String getVomsPrinterBatchForUserQry(String p_batchType,UserVO userVO) {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append(" SELECT distinct P.printer_batch_id,P.start_serial_no,P.end_serial_no,P.user_id,P.downloaded,");
		sqlSelectBuf
				.append("P.product_id,P.voms_decryp_key,P.total_no_of_vouchers,P.created_on,P.created_by,");
		sqlSelectBuf
				.append("P.modified_on,P.modified_by,PR.product_name,PR.mrp,VC.voucher_type,VC.voucher_segment FROM VOMS_PRINT_BATCHES P,VOMS_VOUCHERS vomss, ");
		sqlSelectBuf.append("VOMS_PRODUCTS PR,voms_categories VC , users u,user_vouchertypes uv ");
		if (("Y".equals(p_batchType) || "N".equals(p_batchType))) {
			sqlSelectBuf
					.append("WHERE PR.product_id=P.product_id and P.downloaded=? and ");

		} else {
			sqlSelectBuf.append("WHERE PR.product_id=P.product_id and ");
		}
		if((PretupsI.USER_TYPE_CHANNEL).equals(userVO.getUserType()))
		{
			sqlSelectBuf.append("P.product_id = vomss.PRODUCT_ID and ");
		}
		sqlSelectBuf
				.append("PR.category_id=VC.category_id and trunc(P.created_on)>=? and trunc(P.created_on)<=?  AND u.USER_ID = ?  and u.USER_ID =uv.USER_ID and vc.VOUCHER_TYPE = uv.VOUCHER_TYPE and VC.network_code = ? "); 
		if((PretupsI.USER_TYPE_CHANNEL).equals(userVO.getUserType()))
		{
			sqlSelectBuf.append("AND vomss.user_id = ? AND vomss.CURRENT_STATUS = 'EN' ");
		}
		sqlSelectBuf.append("order by P.created_on ");
		return sqlSelectBuf.toString();
	}
	
	@Override
	public String getVomsPrinterBatchByBatchIDQry(String p_batchType,UserVO userVO) {
		StringBuilder sqlSelectBuf = new StringBuilder();
		sqlSelectBuf
				.append(" SELECT distinct P.printer_batch_id,P.start_serial_no,P.end_serial_no,P.user_id,P.downloaded,");
		sqlSelectBuf
				.append("P.product_id,P.voms_decryp_key,P.total_no_of_vouchers,P.created_on,P.created_by,");
		sqlSelectBuf
				.append("P.modified_on,P.modified_by,PR.product_name,PR.mrp,VC.voucher_type,VC.voucher_segment FROM VOMS_PRINT_BATCHES P,VOMS_VOUCHERS vomss, ");
		sqlSelectBuf.append("VOMS_PRODUCTS PR,voms_categories VC , users u,user_vouchertypes uv ");
		if (("Y".equals(p_batchType) || "N".equals(p_batchType))) {
			sqlSelectBuf
					.append("WHERE PR.product_id=P.product_id and P.downloaded=? and ");

		} else {
			sqlSelectBuf.append("WHERE PR.product_id=P.product_id and ");
		}
		if((PretupsI.USER_TYPE_CHANNEL).equals(userVO.getUserType()))
		{
			sqlSelectBuf.append("P.product_id = vomss.PRODUCT_ID and ");
		}
		sqlSelectBuf
				.append("PR.category_id=VC.category_id AND u.USER_ID = ?  and u.USER_ID =uv.USER_ID and vc.VOUCHER_TYPE = uv.VOUCHER_TYPE and VC.network_code = ? "); 
		if((PretupsI.USER_TYPE_CHANNEL).equals(userVO.getUserType()))
		{
			sqlSelectBuf.append("AND vomss.user_id = ? AND vomss.CURRENT_STATUS = 'EN' ");
		}
		sqlSelectBuf.append("AND P.PRINTER_BATCH_ID = ? ");
		return sqlSelectBuf.toString();
	}

}
