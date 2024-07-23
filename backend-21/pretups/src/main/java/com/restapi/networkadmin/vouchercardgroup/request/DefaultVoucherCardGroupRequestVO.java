package com.restapi.networkadmin.vouchercardgroup.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DefaultVoucherCardGroupRequestVO {
	private String serviceTypeId;
	private String cardGroupSubServiceID;
    private String selectCardGroupSetId;
	private String previousDefaultCardGroup;
}
