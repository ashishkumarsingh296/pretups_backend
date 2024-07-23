package com.restapi.networkadmin.networkservices.requestVO;

import java.util.ArrayList;

import com.btsl.pretups.master.businesslogic.NetworkServiceVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateNetworkServicesRequestVO {
	private ArrayList<NetworkServiceVO> networkServicesVOList;
	
}
