package com.restapi.networkadmin.requestVO;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDefaultConfigurationTemplateFileRequestVO {
	@io.swagger.v3.oas.annotations.media.Schema(example = "xls", required = true, description="File Type(xls")
	private String fileType;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Network Admin Initiate", required = true, description="File Name")
	private String fileName;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Base64 Encoded data", required = true, description="Base64 Encoded File as String")
	private String fileAttachment;
}
