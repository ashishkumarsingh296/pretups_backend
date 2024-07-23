package com.restapi.networkadmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class SelectorServiceProductAmountDetailsResponseVO extends BaseResponse{
	List<ServiceProductAmountDetailsVO> serviceProductAmountDetailsList;
	
	
	
}
