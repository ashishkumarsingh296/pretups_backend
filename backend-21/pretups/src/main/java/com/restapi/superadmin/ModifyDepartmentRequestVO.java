package com.restapi.superadmin;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ModifyDepartmentRequestVO {

	private String divDeptName;
	private String divDeptShortCode;
	private String status;
	private String parentId;
	private String divDeptId;
	private String divDeptType;
	
}
