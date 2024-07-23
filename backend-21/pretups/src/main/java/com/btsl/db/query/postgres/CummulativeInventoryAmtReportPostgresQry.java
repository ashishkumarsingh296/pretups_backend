package com.btsl.db.query.postgres;

import com.btsl.voms.vomsprocesses.businesslogic.CummulativeInventoryAmtReportQry;

public class CummulativeInventoryAmtReportPostgresQry implements CummulativeInventoryAmtReportQry{
	
	@Override
	public String getVoucherSummInfoLocQry(int noOfdays) {
		  StringBuffer strBuff = new StringBuffer("SELECT P.product_id PRODUCTID,P.product_name PRODUCTNAME,P.mrp, P.min_req_quantity,");
	        strBuff.append(" P.max_req_quantity, SUM(V.total_generated)-SUM(V.total_enabled)-SUM(V.total_stolen_dmg) total_generated,");
	        strBuff.append(" SUM(V.total_enabled)- SUM(V.total_recharged)-SUM(V.total_stolen_dmg_after_en)-SUM(V.total_reconciled)+");
	        strBuff.append(" SUM(V.total_reconciled_changed) total_enabled, SUM(V.total_on_hold) total_hold,");
	        strBuff.append(" SUM(V.total_recharged) total_recharged,SUM(V.total_stolen_dmg) total_st_da_before, ");
	        strBuff.append(" SUM(V.total_stolen_dmg_after_en) total_st_da_after, SUM(V.total_reconciled) total_reconciled ");
	        strBuff.append(" FROM voms_voucher_summary V right join voms_products P on V.product_id=P.product_id AND V.summary_date<=?-" + noOfdays );
	        strBuff.append("  GROUP BY P.product_id,P.product_name,P.mrp, ");
	        strBuff.append(" P.min_req_quantity, P.max_req_quantity ORDER BY P.mrp,P.product_id");
		return strBuff.toString();
	}


	@Override
	public String getVoucherArchiveInfoLocQry() {
		 StringBuffer strBuff = new StringBuffer("SELECT COALESCE(P.product_id,' ') PRODUCTID,COALESCE(P.product_name,' ') PRODUCTNAME,P.mrp, P.min_req_quantity, ");
	        strBuff.append(" P.max_req_quantity, COALESCE(SUM(V.total_generated),0) TOTAL_GENERATED ,");
	        strBuff.append(" COALESCE(SUM(V.total_enabled),0) TOTAL_ENABLED, COALESCE(SUM(V.total_on_hold),0) TOTAL_HOLD,");
	        strBuff.append(" COALESCE(SUM(V.total_recharged),0) TOTAL_RECHARGED,COALESCE(SUM(V.total_stolen_dmg),0) TOTAL_ST_DA_BEFORE, ");
	        strBuff.append(" COALESCE(SUM(V.total_stolen_dmg_after_en),0) TOTAL_ST_DA_AFTER,COALESCE(SUM(V.total_reconciled),0) TOTAL_RECONCILED ");
	        strBuff.append(" FROM voms_archive_summary V right join voms_products P on V.product_id=P.product_id AND summary_date=? ");
	        strBuff.append(" GROUP BY P.product_id,P.product_name,P.MRP, P.min_req_quantity, ");
	        strBuff.append(" P.max_req_quantity ORDER BY P.mrp,P.product_id");
		return strBuff.toString();
	}

	@Override
	public String getVoucherCountQry() {
		  StringBuilder strBuff = new StringBuilder("SELECT COALESCE(COUNT(VA.serial_no),0) VOUCHCOUNT FROM voms_voucher_audit VA,voms_vouchers V WHERE");
	        strBuff.append(" V.SERIAL_NO=VA.SERIAL_NO AND V.PRODUCT_ID=? AND ");
	        strBuff.append(" VA.current_status=? AND VA.previous_status=? AND date_trunc('day',VA.modified_on::timestamp)= ? ");
		return strBuff.toString();
	}
	
	@Override
	public String getVoucherSummaryInfoLocQry() {
		StringBuilder strBuff = new StringBuilder("SELECT P.product_id PRODUCTID,P.product_name PRODUCTNAME,P.MRP, P.min_req_quantity , ");
	        strBuff.append(" P.max_req_quantity, SUM(V.total_generated) total_generated ,SUM(V.total_enabled) total_enabled, SUM(V.total_on_hold) total_hold,");
	        strBuff.append(" SUM(V.total_recharged) total_recharged,SUM(V.total_stolen_dmg) total_st_da_before, ");
	        strBuff.append(" SUM(V.total_stolen_dmg_after_en) total_st_da_after,SUM(V.total_reconciled) total_reconciled ");
	        strBuff.append(" FROM voms_voucher_summary V right join  voms_products P on V.product_id=P.product_id AND summary_date=? ");
	        strBuff.append(" GROUP BY P.product_id,P.product_name ,");
	        strBuff.append(" P.mrp, P.min_req_quantity, P.max_req_quantity ORDER BY P.mrp,P.product_id");
		return strBuff.toString();
	}
	
	@Override
	public String getBatchListForLocQry() {
		   StringBuffer strBuff = new StringBuffer("SELECT B.product_id PRODUCTID,P.product_name PRODUCTNAME, P.mrp MRP, ");
	        strBuff.append(" B.batch_type BATCHTYPE, count(B.batch_no) NOOFBATCH, SUM(B.total_no_of_vouchers) TOTALVOUCHER, ");
	        strBuff.append(" SUM(B.download_count) COUNT, SUM(B.total_no_of_failure) FAILCOUNT, SUM(B.total_no_of_success) SUCCCOUNT, ");
	        strBuff.append(" sum(case B.status when 'EX' then 1 else 0 end ) EXCOUNT, sum(case B.status when 'FA' then 1 else 0 end ) FACOUNT,sum(case B.status when 'SC' then 1 else 0 end ) SCHCOUNT ");
	        strBuff.append(" FROM voms_batches B, voms_products P WHERE B.product_id=P.product_id AND created_date=? ");
	        strBuff.append(" GROUP BY B.product_id,P.product_name,P.mrp,B.batch_type ORDER BY P.mrp,B.product_id");
		return strBuff.toString();
	}
}
