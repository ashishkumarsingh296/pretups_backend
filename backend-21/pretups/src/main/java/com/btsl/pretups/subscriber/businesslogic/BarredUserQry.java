package com.btsl.pretups.subscriber.businesslogic;

public interface BarredUserQry {
	
	String isExistsQry(String barredType);
	
	String addBarredUserBulkFromBarredMsisdns();
	
	String addBarredUserBulkRecordExistGeography();
	
	String loadBarredUserListForXMLAPIQry(String module, String msisdn);

}
