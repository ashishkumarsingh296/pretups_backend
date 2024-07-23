package com.restapi.networkadmin.requestVO;

import java.util.ArrayList;

import com.restapi.networkadmin.C2STransferRuleRequest1;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ChannelTransferDeleteRequestVO {
	private ArrayList<C2STransferRuleRequest1> transferList;
	
}
