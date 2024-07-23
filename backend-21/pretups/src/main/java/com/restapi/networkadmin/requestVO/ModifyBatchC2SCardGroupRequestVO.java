package com.restapi.networkadmin.requestVO;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyBatchC2SCardGroupRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "RC", required = true)
	@JsonProperty("serviceType")
	String serviceType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "dd/mm/yy", required = true)
	@JsonProperty("Date")
	String Date;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "C2S", required = true)
	@JsonProperty("module")
	String module;
	
	@JsonProperty("fileName")
	String fileName;
	
	@JsonProperty("fileAttachment")
	String fileAttachment;
	
	@JsonProperty("fileType")
	String fileType;
	
	@SuppressWarnings("rawtypes")
	@JsonProperty("cardGroupList")
	ArrayList cardGroupList;
}
