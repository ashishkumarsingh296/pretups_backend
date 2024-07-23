package com.web.pretups.restrictedsubs.businesslogic;

import java.util.Date;


public interface RestrictedSubscriberWebQry {
	public String loadResSubsDetails(boolean isOwnerID,String msisdn,Date fromDate,Date toDate);
	
}
