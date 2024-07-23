package com.restapi.networkadmin.vouchercardgroup.response;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DenominationListResponseVO extends BaseResponse{
	private ArrayList<DenominationDetails> denominationList;
	
}
