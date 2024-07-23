package com.web.voms.voucher.businesslogic;

public interface VomsVoucherWebQry {
	String loadBatchLogListQry();
	String getVomsVoucherListQry(String tablename);
	
	String selectVouchersOnSaleNo(String tablename);
}
