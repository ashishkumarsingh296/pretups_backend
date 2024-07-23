package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoadCardGroupTransferValuesResponseVO extends BaseResponse{

	private String moduleTypeId;
	private String moduleTypeIdDesc;
    private ArrayList cardGroupSubServiceList;
    private String cardGroupSubServiceID;
    private String cardGroupSubServiceName;
    private ArrayList serviceTypeList;
    private String serviceTypeId;
    private String serviceTypedesc;
    private ArrayList setTypeList;
    private String setType;
    private String setTypeName;
    private ArrayList domainList;
    private ArrayList subscriberTypeList;
    private ArrayList subscriberServiceTypeList;
    private List categoryList;
    private List gradeList;
    private List gatewayList;
    
}
