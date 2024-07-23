package com.restapi.superadmin.serviceI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.restapi.superadmin.responseVO.GradeTypeListResponseVO;

public interface GradeManagementServiceI {

	GradeTypeListResponseVO getGradeTypeList(Connection con, MComConnectionI mcomCon, Locale locale, HttpServletResponse responseSwag,String reqType) throws BTSLBaseException, SQLException;

	GradeTypeListResponseVO viewGradeList(Connection con, MComConnectionI mcomCon, Locale locale, String domainCode, String categoryCode, HttpServletResponse responseSwag);

	GradeTypeListResponseVO addGrade(Connection con, MComConnectionI mcomCon, Locale locale, ChannelUserVO userVO, String categoryCode, String gradeCode, String gradeName, String defaultGrade, HttpServletResponse responseSwag);

	GradeTypeListResponseVO modifyGrade(Connection con, MComConnectionI mcomCon, Locale locale, ChannelUserVO userVO, String gradeCode, String gradeName, String defaultGrade, HttpServletResponse responseSwag);

	GradeTypeListResponseVO deleteGrade(Connection con, MComConnectionI mcomCon, Locale locale, ChannelUserVO userVO, String gradeCode, HttpServletResponse responseSwag);
	
	

}
