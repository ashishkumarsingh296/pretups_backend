package com.restapi.networkadmin.interfacePrefix;

import java.util.ArrayList;

import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveInterfaceNetworkMappingPrefixRequestVO {	
	private ArrayList<InterfaceNetworkMappingVO> interfaceList;
}
