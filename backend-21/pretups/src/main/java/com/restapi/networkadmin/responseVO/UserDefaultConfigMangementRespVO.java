package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDefaultConfigMangementRespVO extends BaseResponse {

	private String fileAttachment;
	private String fileName;
	private String fileType;	
	
}
