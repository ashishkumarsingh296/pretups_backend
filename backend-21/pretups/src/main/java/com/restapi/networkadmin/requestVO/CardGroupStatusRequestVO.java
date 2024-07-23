package com.restapi.networkadmin.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardGroupStatusRequestVO {

	private String cardGroupSetName;
	private String serviceType;
	private String subServiceType;
	private String setType;
	private String version;
}
