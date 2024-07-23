package com.restapi.networkadmin;

import java.util.ArrayList;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelTransferModifyRequestVO {
	private ArrayList<C2STransferRuleRequest1> transferList;
}
