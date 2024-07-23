package com.restapi.superadmin.serviceI;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.requestVO.ApprovalOperatorUsersRequestVO;
import com.restapi.superadmin.responseVO.ApprovalOperatorUsersListResponseVO;


@Service
public interface OperatorApprovalServiceI {

	public ApprovalOperatorUsersListResponseVO loadApprovalOperatorUsersInList(Connection con, UserVO userVO,
			HttpServletResponse response1, ApprovalOperatorUsersRequestVO requestVO, String type);
	

}
