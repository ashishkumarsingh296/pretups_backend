package com.restapi.channelAdmin.restrictedlistmgmt.serviceI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.*;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.*;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.ApprovalRestrictedListResponseVO;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;

public interface RestrictedListMgmtServiceI {

	LoadDropdownsResponseVO loadDropdowns(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			LoadDropdownsResponseVO response) throws BTSLBaseException;

	SearchUserListBasedOnkeywordResponseVO searchUserListBasedOnkeyword(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, SearchUserListBasedOnkeywordResponseVO response,
			String userName, String categoryCode,String geoDomain) throws BTSLBaseException;

	LoadSubscriberListForDeleteResponseVO loadSubscriberListForDelete(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, LoadSubscriberListForDeleteResponseVO response,
			String msisdnStr, String ownerID) throws BTSLBaseException;

	BaseResponse deleteRestrictedSubscriber(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
			DeleteRestrictedSubscriberRequestVO requestVO) throws SQLException, BTSLBaseException;


	public ArrayList<ViewRestrictedResponseVO> loadRestrictedSubs(Connection con, UserVO userVO, String userName, String categoryCode, String geoDomain,String fromDateStr, String toDateStr ) throws BTSLBaseException, Exception;
	public UploadFileResponseVO uploadRestrictedList( Connection con,UserVO userVO, String userName, String categoryCode, String geoDomain, String domain,String subscriberTypet,UploadFileRequestVO uploadFileRequestVO, HttpServletRequest httpRequest, UploadFileResponseVO response,  HttpServletResponse response1  )throws BTSLBaseException, Exception;
	public ApprovalRestrictedListResponseVO approvalRestrictedList(Connection con, UserVO userVO,String userName, String categoryCode,String geoDomain, ApprovalRestrictedListResponseVO responseVO)throws BTSLBaseException, Exception;;
	public Integer updateApprovalSubscriberList( Connection con, UserVO userVO, List<SubscriberDetailsRequestVO> request)throws BTSLBaseException, Exception ;

		
	//Unblack apis code starts 
	LoadSubscriberListForUnBlackResponseVO loadSubscriberListForUnBlack(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, LoadSubscriberListForUnBlackResponseVO response,
			String msisdnStr, String ownerID, String cp2pPayer,String cp2pPayee,String c2sPayee) throws BTSLBaseException;

	BaseResponse unBlackListAllSubscriber(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			BaseResponse response, UnBlackListAllSubscriberRequestVO requestVO) throws BTSLBaseException, SQLException;

	BaseResponse unBlackListSelectedSubscriber(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
			UnBlackListSelectedSubscriberRequestVO requestVO) throws SQLException, BTSLBaseException;
	//Unblack apis code ends
	
	
	//blacklist api's starts here
	LoadSubscriberListForBlackListSingleResponseVO loadSubscriberListForBlackListSingle(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, LoadSubscriberListForBlackListSingleResponseVO response, String msisdnStr,
			String ownerID, String cp2pPayer,String cp2pPayee,String c2sPayee) throws BTSLBaseException;

	BaseResponse blackListSingleSubscriber(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			BaseResponse response, BlackListSingleSubscriberRequestVO requestVO) throws BTSLBaseException, SQLException;

	BaseResponse blackListAllSubscriber(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			BaseResponse response, BlackListAllSubscriberRequestVO requestVO) throws SQLException, BTSLBaseException;

	BlackListMultipleSubscriberResponseVO uploadAndProcessBlackListMultipleSubscriberFile(MultiValueMap<String, String> headers,
																						  HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
																						  MComConnectionI mcomCon, Locale locale, UserVO userVO,
																						  BlacklistMultipleSubscribersRequestVO requestVO) throws Exception;

	//blacklist api's ends here
}
