package com.restapi.networkadmin.commissionprofile.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import lombok.Data;

@Data
public class BatchAddCommProfRespVO extends BaseResponse{

	private String fileAttachment;
	private String fileName;
	private String fileType;
	public String sequenceNo;
	
	

	
}
