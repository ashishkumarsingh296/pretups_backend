package com.restapi.networkadmin.servicetypeselectormapping.responseVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ViewServiceTypeSelectorMappingResponseVO extends ServiceTypeSelectorMappingDetailsVO{
	private int status;
    private String messageCode;
    private String message;
    
}
