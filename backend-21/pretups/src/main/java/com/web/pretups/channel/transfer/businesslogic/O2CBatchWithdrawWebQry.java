package com.web.pretups.channel.transfer.businesslogic;

public interface O2CBatchWithdrawWebQry {
	public String loadBatchO2CMasterDetailsQry(String pgoeDomain, String pdomain, String pproductCode, String pbatchid, String pmsisdn);
	public String loadBatchDetailsListQry();
	public String validateUsersForBatchO2CWithdrawQry( String pcategoryCode,String receiverStatusAllowed,String pgeographicalDomainCode);
}
