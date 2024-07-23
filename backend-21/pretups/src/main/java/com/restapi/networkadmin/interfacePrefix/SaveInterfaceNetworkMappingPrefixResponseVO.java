package com.restapi.networkadmin.interfacePrefix;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveInterfaceNetworkMappingPrefixResponseVO extends BaseResponse{
	private ArrayList errors;
}
