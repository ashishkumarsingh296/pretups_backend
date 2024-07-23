package com.restapi.networkadmin.cardgroup.responseVO;

import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVersionVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionDetailsAndDeleteStatusVO {

	private CardGroupSetVersionVO cardGroupSetVersionVO;
	private String status;
	private boolean deleteStatus;
	
}
