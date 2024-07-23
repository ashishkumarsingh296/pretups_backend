package com.restapi.networkadmin.vouchercardgroup.response;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class VoucherCardGroupStatusResponseVO extends BaseResponse {
	List<CardGroupSetVO> cardGroupStatusList;

}
