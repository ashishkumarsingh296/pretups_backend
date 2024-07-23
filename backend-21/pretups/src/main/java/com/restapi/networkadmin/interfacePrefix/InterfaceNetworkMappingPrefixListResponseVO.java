package com.restapi.networkadmin.interfacePrefix;

import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class InterfaceNetworkMappingPrefixListResponseVO extends BaseResponse{
	
	private String prepaidSeries;
	private String postpaidSeries;
	
	private HashMap seriesMap;// this map contains all series, used during save
    						// to fetch the prefix_id
	
	private ArrayList interfaceList;
}
