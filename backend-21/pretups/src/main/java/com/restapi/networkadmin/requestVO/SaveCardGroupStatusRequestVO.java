package com.restapi.networkadmin.requestVO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaveCardGroupStatusRequestVO {

	private String cardGroupSetName;
	private String serviceType;
	private String subServiceType;
	private String setType;
	private String version;
	private String status;
	private String cardGroupSetID;
	private Long lastModifiedOn;
	private String language1Message;
	private String language2Message;
	private String setTypeName;
	
}
