package com.txn.voms.voucher.businesslogic;

public interface VomsVoucherTxnQry {
	
	public StringBuilder loadDatafromVoms(String tablename, String sno);
	
	public StringBuilder insertVomsVoucherAudit();
	
	public StringBuilder loadDatafromVoms1(String tablename);
	
	public StringBuilder insertVomsVoucherAudit1();
	
	
	public StringBuilder getVoucherForMrp();

	public int getLimitOrRownumValue();
}
