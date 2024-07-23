package com.restapi.channeluser.service;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.PretupsResponse;
import com.btsl.pretups.channel.profile.businesslogic.ProfileThresholdResponseVO;

public interface UserThresholdServiceI {
	

	public PretupsResponse<ProfileThresholdResponseVO> userThresholdProcess(String identifierType,
			 String loggedInUser, Connection con, String statusUsed, String status, PretupsResponse<ProfileThresholdResponseVO> response, HttpServletResponse responseSwag );
}
