package com.restapi.networkadmin.networkservices.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ServiceTypeListResponseVO extends BaseResponse{
	private ArrayList moduleList;
    private ArrayList serviceTypeList;
}
