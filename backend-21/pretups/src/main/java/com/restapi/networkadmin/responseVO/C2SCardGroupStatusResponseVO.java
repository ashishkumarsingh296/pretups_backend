package com.restapi.networkadmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SCardGroupStatusResponseVO extends BaseResponse {
	List<CardGroupSetVO> cardGroupStatusList;

}
