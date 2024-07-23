package com.restapi.networkadmin.cardgroup.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardGroupCalculateC2STransferValueRequestVO {
	private String categoryId;
	private String gradeId;
	private String moduleTypeId;
	private String serviceTypeId;
	private String receiverTypeId;	
	private String receiverClassId;
	private String cardGroupSubServiceID;
	private String domainCode;
	private String amount;
	private String gatewayId;
	private String applicableFromHour;
	private String applicableFromDate;
	private String oldValidityDate;
			
		
		
	}



