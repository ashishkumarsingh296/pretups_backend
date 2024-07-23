package com.restapi.superadmin.serviceI;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.responseVO.PasswordManagementResponseVO;

public interface PasswordManagementServiceI {

	PasswordManagementResponseVO getUserDetails(Connection con, MComConnectionI mcomCon, Locale locale, String loginId,
			String msisdn, String remarks, UserVO userVO, HttpServletResponse responseSwag);

}
