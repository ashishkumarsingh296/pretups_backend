package com.restapi.superadmin.domainmanagement.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainmanagementRolesResponseVO extends BaseResponse{
	private List rolesList;
}
