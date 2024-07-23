package com.restapi.networkadmin.commissionprofile.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommissionProfileViewDetailsRequestVO {

	private String categoryCode;
	private String gradeCode;
	private String grphDomainCode;
	private String commProfileSetId;
	private String networkCode;
	private String commissionType;
	private String domainCode;
	private String commProfileSetVersionId;

}
