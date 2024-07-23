package com.restapi.channelAdmin.batchUserApprove.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class BulkUserProcessResponseVO extends BaseResponse{
	
	private String errorFlag;
	private ArrayList errorList = null;
	private String noOfRecords;
	private int totalRecords;
	
	private String fileAttachment;
	private String fileType;
	private String fileName;
}
