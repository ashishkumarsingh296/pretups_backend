package com.restapi.superadmin.serviceI;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*//import org.apache.struts.action.ActionForm;*/

import com.btsl.common.BTSLBaseException;
import com.restapi.superadmin.responseVO.OperatorUserListResponse;

public interface ViewUserServiceI {

	public OperatorUserListResponse viewOperatorUser(Connection con, String loginID,String category,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	

	

}
