package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ICCIDMSISDNHistoryDetailsVO {

	private String mobileNumber;
	private String iccid;
	private String modifiedBy;
	private String modifiedOn;
	private String lastTransactionID;
	private String newIccID;
	
	 
}
