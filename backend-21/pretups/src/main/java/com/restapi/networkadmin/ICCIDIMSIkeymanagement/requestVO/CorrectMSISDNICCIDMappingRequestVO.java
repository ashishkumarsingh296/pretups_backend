package com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorrectMSISDNICCIDMappingRequestVO {

	private String firstMSISDN;
	private String firstICCID;
	private String secondMSISDN;
	private String secondICCID;
	
}
