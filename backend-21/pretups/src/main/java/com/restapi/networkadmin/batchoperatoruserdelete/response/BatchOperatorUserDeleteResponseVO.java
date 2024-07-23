package com.restapi.networkadmin.batchoperatoruserdelete.response;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchOperatorUserDeleteResponseVO extends BaseResponse{
	private ArrayList errorList;
	private String fileAttachment;
	private String fileName;
	private String fileType;
}
