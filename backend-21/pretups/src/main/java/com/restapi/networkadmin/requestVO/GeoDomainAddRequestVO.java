package com.restapi.networkadmin.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoDomainAddRequestVO {
	private String description;
	private String grphDomainCode;
	private String grphDomainName;
	private String grphDomainShortName;
	private String grphDomainType;
	private String parentDomainCode;
	private String status;
	private String isDefault;
	
}
