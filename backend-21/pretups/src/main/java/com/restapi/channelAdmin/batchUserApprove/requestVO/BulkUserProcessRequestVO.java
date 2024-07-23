package com.restapi.channelAdmin.batchUserApprove.requestVO;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class BulkUserProcessRequestVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "xls", required = true, description="File Type(csv, xls, xlsx")
	private String fileType;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Batch Operator User Initiate", required = true, description="File Name")
	private String fileName;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Base64 Encoded data", required = true, description="Base64 Encoded File as String")
	private String fileAttachment;
	
	private String batchID;
	private String domainCode;
	
}
