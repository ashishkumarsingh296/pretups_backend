package com.restapi.superadmin;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GatewayMappingVO {
	
	    private String _requestGatewayCode;
	    private String _responseGatewayCode;
	    private String _altresponseGatewayCode;
	    private String modifyFlag;
}
