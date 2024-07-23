package com.restapi.networkadmin.requestVO;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyC2SCardGroupFileRequestVO {
	@SuppressWarnings("rawtypes")
	@JsonProperty("cardGroupList")
	ArrayList cardGroupList;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "RC", required = true)
	@JsonProperty("serviceType")
	String serviceType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "dd/mm/yy", required = true)
	@JsonProperty("Date")
	String Date;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "C2S", required = true)
	@JsonProperty("module")
	String module;
}
