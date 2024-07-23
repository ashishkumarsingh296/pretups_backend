package com.restapi.channelAdmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.restapi.channelAdmin.requestVO.SuspendResumeStaffRequestVO;
import com.restapi.channelAdmin.responseVO.SuspendResumeStaffResponseVO;

public interface SuspendResumeStaffService {
	
	public SuspendResumeStaffResponseVO suspendResumeStaffUser(Connection con, MComConnectionI mcomCon, Locale locale, String operationType,
			ChannelUserVO sessionUserVO, SuspendResumeStaffRequestVO request, SuspendResumeStaffResponseVO response,
			HttpServletResponse responseSwag) throws SQLException;

}
