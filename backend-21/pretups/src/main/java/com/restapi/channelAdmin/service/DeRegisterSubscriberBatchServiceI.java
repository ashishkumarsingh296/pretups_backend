package com.restapi.channelAdmin.service;

import java.sql.Connection;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.user.businesslogic.UserVO;
import com.restapi.channelAdmin.DeRegisterSubscriberBatchVO;
import com.restapi.channelAdmin.requestVO.DeRegisterSubscriberBatchRequestVO;
import com.restapi.channelAdmin.responseVO.DeRegisterSubscriberBatchResponseVO;

public interface DeRegisterSubscriberBatchServiceI {

	public DeRegisterSubscriberBatchResponseVO processUploadedFileForUnRegSubscriber(HashMap<String, String> fileDetailsMap,
			DeRegisterSubscriberBatchRequestVO request, HttpServletRequest requestSwag, UserVO userVO, Connection con,
			DeRegisterSubscriberBatchVO deRegisterSubscriberBatchVO,DeRegisterSubscriberBatchResponseVO response, HttpServletResponse responseSwag);

}
