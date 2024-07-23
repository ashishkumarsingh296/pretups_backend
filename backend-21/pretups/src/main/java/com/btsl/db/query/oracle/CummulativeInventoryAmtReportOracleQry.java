package com.btsl.db.query.oracle;

import com.btsl.voms.vomsprocesses.businesslogic.CummulativeInventoryAmtReportQry;

public class CummulativeInventoryAmtReportOracleQry implements CummulativeInventoryAmtReportQry{

	@Override
	public String getVoucherSummInfoLocQry(int noOfdays) {
		  StringBuilder strBuff = new StringBuilder("SELECT P.product_id PRODUCTID,P.product_name PRODUCTNAME,P.mrp, P.min_req_quantity,");
	        strBuff.append(" P.max_req_quantity, SUM(V.total_generated)-SUM(V.total_enabled)-SUM(V.total_stolen_dmg) total_generated,");
	        strBuff.append(" SUM(V.total_enabled)- SUM(V.total_recharged)-SUM(V.total_stolen_dmg_after_en)-SUM(V.total_reconciled)+");
	        strBuff.append(" SUM(V.total_reconciled_changed) total_enabled, SUM(V.total_on_hold) total_hold,");
	        strBuff.append(" SUM(V.total_recharged) total_recharged,SUM(V.total_stolen_dmg) total_st_da_before, ");
	        strBuff.append(" SUM(V.total_stolen_dmg_after_en) total_st_da_after, SUM(V.total_reconciled) total_reconciled ");
	        strBuff.append(" FROM voms_voucher_summary V,voms_products P where V.summary_date(+)<=?-" + noOfdays + " AND ");
	        strBuff.append(" V.product_id(+)=P.product_id GROUP BY P.product_id,P.product_name,P.mrp, ");
	        strBuff.append(" P.min_req_quantity, P.max_req_quantity ORDER BY P.mrp,P.product_id");
		return strBuff.toString();
	}

	@Override
	public String getVoucherArchiveInfoLocQry() {
		 StringBuilder strBuff = new StringBuilder("SELECT nvl(P.product_id,' ') PRODUCTID,nvl(P.product_name,' ') PRODUCTNAME,P.mrp, P.min_req_quantity, ");
	        strBuff.append(" P.max_req_quantity, nvl(SUM(V.total_generated),0) TOTAL_GENERATED ,");
	        strBuff.append(" nvl(SUM(V.total_enabled),0) TOTAL_ENABLED, nvl(SUM(V.total_on_hold),0) TOTAL_HOLD,");
	        strBuff.append(" nvl(SUM(V.total_recharged),0) TOTAL_RECHARGED,nvl(SUM(V.total_stolen_dmg),0) TOTAL_ST_DA_BEFORE, ");
	        strBuff.append(" nvl(SUM(V.total_stolen_dmg_after_en),0) TOTAL_ST_DA_AFTER,nvl(SUM(V.total_reconciled),0) TOTAL_RECONCILED ");
	        strBuff.append(" FROM voms_archive_summary V,voms_products P where summary_date(+)=? ");
	        strBuff.append(" AND V.product_id(+)=P.product_id GROUP BY P.product_id,P.product_name,P.MRP, P.min_req_quantity, ");
	        strBuff.append(" P.max_req_quantity ORDER BY P.mrp,P.product_id");
		return strBuff.toString();
	}

	@Override
	public String getVoucherCountQry() {
		  StringBuilder strBuff = new StringBuilder("SELECT nvl(COUNT(VA.serial_no),0) VOUCHCOUNT FROM voms_voucher_audit VA,voms_vouchers V WHERE");
	        strBuff.append(" V.SERIAL_NO=VA.SERIAL_NO AND V.PRODUCT_ID=? AND ");
	        strBuff.append(" VA.current_status=? AND VA.previous_status=? AND trunc(VA.modified_on)= ? ");
		return strBuff.toString();
	}

	@Override
	public String getVoucherSummaryInfoLocQry() {
		StringBuilder strBuff = new StringBuilder("SELECT P.product_id PRODUCTID,P.product_name PRODUCTNAME,P.MRP, P.min_req_quantity , ");
	        strBuff.append(" P.max_req_quantity, SUM(V.total_generated) total_generated ,SUM(V.total_enabled) total_enabled, SUM(V.total_on_hold) total_hold,");
	        strBuff.append(" SUM(V.total_recharged) total_recharged,SUM(V.total_stolen_dmg) total_st_da_before, ");
	        strBuff.append(" SUM(V.total_stolen_dmg_after_en) total_st_da_after,SUM(V.total_reconciled) total_reconciled ");
	        strBuff.append(" FROM voms_voucher_summary V,voms_products P WHERE summary_date(+)=? ");
	        strBuff.append(" AND V.product_id(+)=P.product_id GROUP BY P.product_id,P.product_name ,");
	        strBuff.append(" P.mrp, P.min_req_quantity, P.max_req_quantity ORDER BY P.mrp,P.product_id");
		return strBuff.toString();
	}

	@Override
	public String getBatchListForLocQry() {
		StringBuilder strBuff = new StringBuilder("SELECT B.product_id PRODUCTID,P.product_name PRODUCTNAME, P.mrp MRP, ");
	        strBuff.append(" B.batch_type BATCHTYPE, count(B.batch_no) NOOFBATCH, SUM(B.total_no_of_vouchers) TOTALVOUCHER, ");
	        strBuff.append(" SUM(B.download_count) COUNT, SUM(B.total_no_of_failure) FAILCOUNT, SUM(B.total_no_of_success) SUCCCOUNT, ");
	        strBuff.append(" sum(decode(B.status,'EX',1,0)) EXCOUNT, sum(decode(B.status,'FA',1,0)) FACOUNT,sum(decode(B.status,'SC',1,0)) SCHCOUNT ");
	        strBuff.append(" FROM voms_batches B, voms_products P WHERE B.product_id=P.product_id AND created_date=? ");
	        strBuff.append(" GROUP BY B.product_id,P.product_name,P.mrp,B.batch_type ORDER BY P.mrp,B.product_id");
		return strBuff.toString();
	}

}
