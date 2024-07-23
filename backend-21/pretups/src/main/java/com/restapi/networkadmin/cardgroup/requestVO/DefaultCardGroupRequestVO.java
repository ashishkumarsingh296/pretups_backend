package com.restapi.networkadmin.cardgroup.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultCardGroupRequestVO {
	
	private String serviceTypeId;
	private String cardGroupSubServiceID;
    private String selectCardGroupSetId;
	private String previousDefaultCardGroup;
}
