package com.btsl.pretups.channel.userreturn.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChnlToChnlTransferTransactionCntrl;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.userreturn.web.C2CWithdrawVO;
import com.btsl.pretups.channel.userreturn.web.C2CWithdrawViaAdmValidator;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.user.businesslogic.UserWebDAO;


/*
 * This class implements LookupsRestService and provides basic method to load Look and sublookups
 */
public class C2CWithdrawRestServiceImpl implements C2CWithdrawRestService {

	public static final Log _log = LogFactory
			.getLog(C2CWithdrawRestServiceImpl.class.getName());
	/**
	 * This method load CategoryList data from DB
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * 
	 * @return PretupsResponse<List<ListValueVO>>
	 * 
	 * @throws BTSLBaseException
	 *             , Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List> loadCatListByByDomain(
			String requestData) throws Exception {
		final String methodName = "C2CWithdrawRestServiceImpl#loadCatListByTransferRuleByDomain";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "entered: ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List> response = new PretupsResponse<>();
		Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil
				.convertJSONToObject(requestData,
						new TypeReference<Map<String, Object>>() {
						});
		Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
		try {
			if (_log.isDebugEnabled()) {
			_log.debug(methodName, "ENTERED IN DATA :" + map);
			}
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			CategoryDAO categoryDAO = new CategoryDAO();
			List list = categoryDAO.loadOtherCategorList(con,PretupsI.OPERATOR_TYPE_OPT);
			CategoryVO categoryVO = null;
			List list2 = new ArrayList();
    		for(int i=0,j=list.size(); i<j ;i++)
    		{
    			categoryVO = (CategoryVO)list.get(i);
    			if(categoryVO.getSequenceNumber()==1 && (categoryVO.getDomainCodeforCategory().equalsIgnoreCase(map.get("domain").toString())))	 
    						list2.add(categoryVO);
    		}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, list2);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CWithdrawRestServiceImpl#loadCatListByByDomain");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) 
				_log.debug(methodName, "Exiting :");
			}
		return response;
	}

	/**
	 * This method load Owner User data from DB
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * @return PretupsResponse<List<UserVO>>
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<UserVO>> loadUserListData(String requestData)
			throws BTSLBaseException,IOException {
		final String methodName = "C2CWithdrawRestServiceImpl#loadUserListData";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered:");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<UserVO>> response = new PretupsResponse<>();
		Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil
				.convertJSONToObject(requestData,
						new TypeReference<Map<String, Object>>() {
						});
		Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
		try {
			if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered in data:");
			}
			UserWebDAO userWebDAO = new UserWebDAO();
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				  _log.error(methodName, "SQLException " + e);
			      _log.errorTrace(methodName, e);
			}
			final String status = PretupsBL.userStatusNotIn();
			List<UserVO> list = userWebDAO.loadOwnerUserList(con,
					map.get("zoneCode").toString(), map.get("ownerName")
							.toString(), map.get("domainCode").toString(),
					PretupsI.STATUS_NOTIN, status);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: with list:"+list);
			}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, list);
		} finally {

			if (mcomCon != null) {
				mcomCon.close("C2CWithdrawRestServiceImpl#loadUserListData");
				mcomCon = null;
			}
		if (_log.isDebugEnabled()) 
			_log.debug(methodName, "Exiting: with list:");
		}

		return response;
	}

	/**
	 * This method load Channel User details from DB
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * @return PretupsResponse<ChannelUserVO>
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<ChannelUserVO> loadUserListDetail(String requestData)
			throws Exception {
		final String methodName = "C2CWithdrawRestServiceImpl#loadUserListDetail";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered :");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		final Date curDate = new Date();
		PretupsResponse<ChannelUserVO> response = new PretupsResponse<>();
		C2CWithdrawViaAdmValidator validator=new C2CWithdrawViaAdmValidator();
		JsonNode dataObject = (JsonNode) PretupsRestUtil
				.convertJSONToObject(requestData,
						new TypeReference<JsonNode>() {
						});
		
		if (_log.isDebugEnabled()) {
			_log.debug(methodName+"Entered Data Object is ", dataObject);
		}
		
		C2CWithdrawVO c2cWithdrawVO=(C2CWithdrawVO)PretupsRestUtil
				.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<C2CWithdrawVO>() {});
		Boolean isUserCode = (Boolean)PretupsRestUtil
				.convertJSONToObject(dataObject.get("isUserId").toString(), new TypeReference<Boolean>() {});
		try {
			String msisdn=c2cWithdrawVO.getToMsisdn();
			String userName=c2cWithdrawVO.getToUserName();
			String geography=c2cWithdrawVO.getGeography();
			String domain=c2cWithdrawVO.getDomainCode();
			String toCategory=c2cWithdrawVO.getToCategory();
			if ((BTSLUtil.isNullString(msisdn) || "".equalsIgnoreCase(msisdn)) && (BTSLUtil.isNullString(userName) || "".equalsIgnoreCase(userName)
					|| BTSLUtil.isNullString(geography) || "".equalsIgnoreCase(geography) || BTSLUtil.isNullString(domain) || "".equalsIgnoreCase(domain)
					|| BTSLUtil.isNullString(toCategory) || "".equalsIgnoreCase(toCategory))){
				validator.validateRequestData("LOADUSRDETAILS",response, c2cWithdrawVO, "validateReceiverDetails");
			}
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			ChannelUserVO channeluserVO = null;
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			if (!BTSLUtil.isNullString(msisdn) || !("".equalsIgnoreCase(msisdn))){
				channeluserVO = channelUserDAO
						.loadChannelUserDetailsForTransfer(con,
								msisdn, isUserCode,
								curDate, false);
			}
			else{
				channeluserVO = channelUserDAO
						.loadChannelUserDetailsForTransfer(con,
								c2cWithdrawVO.getToUserId(), isUserCode,
								curDate, false);
			}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
					channeluserVO);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,
						"Exiting with VO:" + response.getDataObject());
			}
		}

		finally {
			if (mcomCon != null) {
				mcomCon.close("C2CWithdrawRestServiceImpl#loadUserListDetail");
				mcomCon = null;
			}
		if (_log.isDebugEnabled()) 
			_log.debug(methodName, "Exiting : ");
		}

		return response;
	}

	/**
	 * This method load Channel User data from DB
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * @return PretupsResponse<List<UserVO>>
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<UserVO>> loadChannelUserListData(
			String requestData) throws BTSLBaseException,IOException{
		final String methodName = "C2CWithdrawRestServiceImpl#loadChannelUserListData";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered : ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<UserVO>> response = new PretupsResponse();
		Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil
				.convertJSONToObject(requestData,
						new TypeReference<Map<String, Object>>() {
						});
		Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
		try {
			if(_log.isDebugEnabled()){
			_log.debug(methodName, "ENTERED IN DATA");
			}
			UserWebDAO userWebDAO = new UserWebDAO();
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				_log.error(methodName, "SQLException " + e);
			    _log.errorTrace(methodName, e);
			}
			final String status = PretupsBL.userStatusNotIn();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "userCat : "
						+ map.get("userCat").toString() + "userName: "
						+ map.get("userName").toString() + "ownerId: "
						+ map.get("ownerId").toString());
			}
			List<UserVO> list = userWebDAO.loadUsersListByNameAndOwnerId(con,
					map.get("userCat").toString(), map.get("userName")
							.toString(), map.get("ownerId").toString(), null,
					PretupsI.STATUS_NOTIN, status, "CHANNEL");
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, list);
		}finally {

			if (mcomCon != null) {
				mcomCon.close("C2CWithdrawRestServiceImpl#loadChannelUserListData");
				mcomCon = null;
			}
		if (_log.isDebugEnabled()) 
			_log.debug(methodName, "Exiting :");
		}

		return response;
	}

	/**
	 * This method load CategoryList ont he basis of
	 * Transfer Rule data from DB
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * @return PretupsResponse<List<ChannelTransferRuleVO>>
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List> loadCatListByTransferRule(
			String requestData) throws BTSLBaseException,IOException {
		final String methodName = "C2CWithdrawRestServiceImpl#loadCatListByTransferRule";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List> response = new PretupsResponse<>();
		Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData,
						new TypeReference<Map<String, Object>>() {
						});
		Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
		try {
			if (_log.isDebugEnabled()) {
			_log.debug(methodName, "ENTERED IN DATA: "+map);
			}
			mcomCon = new MComConnection();
			try {
				con = mcomCon.getConnection();
			} 
			
			catch (SQLException e) {
				_log.error(methodName,"SQLException : ", e.getMessage());
			}
			CategoryWebDAO categoryDAO = new CategoryWebDAO();
			CategoryVO categoryVO = null;
			ArrayList listFinal=new ArrayList<>();
			ArrayList categoryList = categoryDAO.loadCategorListByDomainCode(con,map.get("domain").toString());
			for(int i=0,j=categoryList.size(); i<j ; i++)
            {
                categoryVO = (CategoryVO)categoryList.get(i);
                
          	  if(categoryVO.getSequenceNumber()>(int)map.get("seqNo"))
          		  listFinal.add(categoryVO);
          	  
            }
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, listFinal);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "List:" + listFinal);
			}
		} finally {

			if (mcomCon != null) {
				mcomCon.close("C2CWithdrawRestServiceImpl#loadCatListByTransferRule");
				mcomCon = null;
			}
		if (_log.isDebugEnabled()) 
			_log.debug(methodName, "Exiting :  ");
		}

		return response;
	}

	/**
	 * This method load Channel User list(sender) from DB
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * @return PretupsResponse<List<UserVO>>
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	@SuppressWarnings("unchecked")
@Override
	public PretupsResponse<List<ListValueVO>> loadChannelUserListSender(
			String requestData) throws BTSLBaseException,IOException{
		final String methodName = "C2CWithdrawRestServiceImpl#loadChannelUserListSender";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered : ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
		Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil
				.convertJSONToObject(requestData,
						new TypeReference<Map<String, Object>>() {
						});
		Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
		try {
			_log.debug(methodName,
					"ENTERED IN DATA with from_cat: "
							+ map.get("fromCat").toString() + " toCat: "
							+ map.get("toCat").toString() + " domain: "
							+ map.get("domain").toString() + " userName: "
							+ map.get("user").toString());
			mcomCon = new MComConnection();
			try {
				con = mcomCon.getConnection();
			} 
			
			catch (SQLException e) {
				_log.error(methodName, "SQLException " + e);
			    _log.errorTrace(methodName,e);
			}
			String status = "'"+PretupsI.USER_STATUS_ACTIVE+"'";
			String statusUsed = PretupsI.STATUS_IN;
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			List list = null;
			String[] arr=null;
        	arr = new String[1];
			arr[0]=map.get("userID").toString();
			list = channelUserDAO.loadUserHierarchyListForTransferByCatergory(con,arr,PretupsI.SINGLE,statusUsed,
					status,map.get("fromCat").toString(),map.get("toCat").toString(),map.get("user").toString());
			List<ListValueVO> dropDown = new ArrayList<>();
      	  if(list != null && !list.isEmpty())
      	  {
            list.forEach(channeluserVO -> {
      			ListValueVO listVO = new ListValueVO(((UserVO) channeluserVO).getUserName(),((UserVO) channeluserVO).getUserID()+":"+((UserVO) channeluserVO).getUserName());
      			dropDown.add(listVO);
      		});
      	  }
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, dropDown);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting with userList: " + dropDown);
			}
		}
		finally{
			if (mcomCon != null) {
				mcomCon.close("C2CWithdrawRestServiceImpl#loadChannelUserListSender");
				mcomCon = null;
			}
		if (_log.isDebugEnabled()) 
			_log.debug(methodName, "Exiting:");
		}

		return response;
	}

	/**
	 * This method validates sender User 
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * @return PretupsResponse<Map<String, List>>
	 * @throws BTSLBaseException
	 *             , Exception
	 */
	
	@Override
	public PretupsResponse<C2CWithdrawVO> validateChannelUser(
			String requestData) throws Exception {
		final String methodName = "C2CWithdrawRestServiceImpl#validateChannelUser";
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<C2CWithdrawVO> response = new PretupsResponse<>();
		C2CWithdrawVO c2cWithdrawVO=null;
		Map<String, List> mp = new HashMap<>();
		try {
			JsonNode dataObject = (JsonNode) PretupsRestUtil
					.convertJSONToObject(requestData,
							new TypeReference<JsonNode>() {
							});
			C2CWithdrawViaAdmValidator validator=new C2CWithdrawViaAdmValidator();
			c2cWithdrawVO=(C2CWithdrawVO)PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<C2CWithdrawVO>() {});
			final Date curDate = new Date();
			List productList = null;
			ChannelUserVO receiverVO = null;
			ChannelUserVO senderVO = null;
			final Locale locale;
			String msisdn=c2cWithdrawVO.getFromMsisdn();
			String fromCategory=c2cWithdrawVO.getFromCategory();
			String fromUserName=c2cWithdrawVO.getFromUserName();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			Boolean isUserCode = new ObjectMapper().readValue(
					dataObject.get("isUserId").toString(), Boolean.class);
			if ((BTSLUtil.isNullString(msisdn) || "".equalsIgnoreCase(msisdn)) && (BTSLUtil.isNullString(fromCategory) || "".equalsIgnoreCase(fromCategory)
					|| BTSLUtil.isNullString(fromUserName) || "".equalsIgnoreCase(fromUserName))){
				validator.validateRequestData("VALIDATECHNNLUSER",response, c2cWithdrawVO, "validateSenderDetails");
			}
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
				String admUserID=dataObject.get("admUserID").textValue();
				receiverVO = channelUserDAO.loadChannelUserDetailsForTransfer(
						con, c2cWithdrawVO.getToUserId(),
						false, curDate, false);
				if (!BTSLUtil.isNullString(msisdn) || !("".equalsIgnoreCase(msisdn)))
				{	senderVO = channelUserDAO
						.loadChannelUserDetailsForTransfer(con,
								msisdn,
								isUserCode, curDate, false);
				}
				else
				{
					senderVO = channelUserDAO
						.loadChannelUserDetailsForTransfer(con,
								c2cWithdrawVO.getFromUserId(),
								isUserCode, curDate, false);
				}
				c2cWithdrawVO.setReceiverVO(receiverVO);
				c2cWithdrawVO.setSenderVO(senderVO);
				//user life cycle
				
				UserDAO userDAO = new UserDAO();
	            UserPhoneVO phoneVO = null;
				String userStatusAllowed1=null;
	         	boolean statusAllowed = false; 
	         	boolean returnFlag=false;
	         	String argument = null;
			    UserStatusVO userStatusVO1 = (UserStatusVO)UserStatusCache.getObject(receiverVO.getNetworkID(),receiverVO.getCategoryCode(), PretupsI.USER_TYPE_CHANNEL,PretupsI.REQUEST_SOURCE_TYPE_WEB );
			    if(userStatusVO1!=null){
				      userStatusAllowed1 = userStatusVO1.getUserReceiverAllowed();	
				String[] status = userStatusAllowed1.split(",");
				for(int k=0;k<status.length;k++){
					if(status[k].equals(receiverVO.getStatus()))
						statusAllowed=true;
					}
				}else{
					response.setParameters(new String[]{receiverVO.getMsisdn()});
					response.setFormError("message.channeltransfer.usernotallowed.msg");
					return response;
				}
				
			if(!statusAllowed){
				response.setParameters(new String[]{receiverVO.getMsisdn()});
				response.setFormError("message.channeltransfer.usersuspended.msg");
				return response;
			}
			
			//end user life cycle
			if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue())
            {
				if(BTSLUtil.isNullString(msisdn))
	            {
					senderVO  = channelUserDAO.loadChannelUserDetailsForTransfer(con,c2cWithdrawVO.getFromUserId(),false,curDate,false);
	                argument = senderVO.getUserName();
	            }
	            else
	            {
					senderVO  = channelUserDAO.loadChannelUserDetailsForTransfer(con,msisdn,true,curDate,false);
	                argument = msisdn;
	            }
            }
			else
            {
                if(!BTSLUtil.isNullString(msisdn))
                {
                    phoneVO = userDAO.loadUserAnyPhoneVO(con,msisdn);
                    if(phoneVO == null)
                    {
                    	response.setParameters(new String[]{senderVO.getMsisdn()});
        				response.setFormError("message.channeltransfer.userdetailnotfound.msg");
                    }
                    argument = msisdn;
                }else
                    argument = senderVO.getUserName();
                senderVO  = channelUserDAO.loadChannelUserDetailsForTransfer(con,senderVO.getUserID(),false,curDate,false);
                
                	if(senderVO==null || (!senderVO.getOwnerID().equals(receiverVO.getUserID()) || senderVO.getCategoryVO().getSequenceNumber() == 1))
                	{
                		response.setParameters(new String[]{senderVO.getMsisdn()});
    					response.setFormError("message.channeltransfer.userdetailnotfound.msg");
                	}
                
				if(phoneVO !=null && !("Y").equalsIgnoreCase(phoneVO.getPrimaryNumber()))
    			{
                    senderVO.setPrimaryMsisdn(senderVO.getMsisdn());
                    senderVO.setMsisdn(phoneVO.getMsisdn());
    			}
    			else if(senderVO!=null)
    			    senderVO.setPrimaryMsisdn(senderVO.getMsisdn());
            }
			//checking user profile information
			if(senderVO == null)
            {
				response.setParameters(new String[]{argument});
				response.setFormError("message.channeltransfer.userdetailnotfound.msg");
				return response;
            }
		    if(!senderVO.getOwnerID().equals(receiverVO.getUserID()) || senderVO.getCategoryVO().getSequenceNumber() == 1)
            {
		   	    response.setParameters(new String[]{senderVO.getMsisdn()});
			    response.setFormError("message.channeltransfer.userdetailnotfound.msg");
				return response;
            }
		    else {
            	//user life cycle
				statusAllowed=false;
            	String userStatusAllowed=null;
            	UserStatusVO userStatusVO = (UserStatusVO)UserStatusCache.getObject(senderVO.getNetworkID(), senderVO.getCategoryCode(), senderVO.getUserType(),PretupsI.REQUEST_SOURCE_TYPE_WEB );
            	if(userStatusVO!=null){
				       userStatusAllowed = userStatusVO.getUserSenderAllowed();  
				String[] status = userStatusAllowed.split(",");
				for(int i=0;i<status.length;i++){
					if(status[i].equals(senderVO.getStatus()))
						statusAllowed=true;
					}
				}else{
					response.setParameters(new String[]{senderVO.getUserName()});
				    response.setFormError("message.channeltransfer.usernotallowed.msg");
					return response;
				}
            }
		    if(!statusAllowed)
            {
		    	response.setParameters(new String[]{argument});
			    response.setFormError("message.channeltransfer.usersuspended.msg");
				return response;
            }
			else if(senderVO .getCommissionProfileApplicableFrom().after(curDate))
            {
				response.setParameters(new String[]{argument});
			    response.setFormError("message.channeltransfer.usernocommprofileapplicable.msg");
				return response;
            }
		    locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
					(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		    senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang2Msg());
			receiverVO.setCommissionProfileSuspendMsg(receiverVO.getCommissionProfileLang2Msg());
			//commission profile suspend message has to be set in VO
	        //Check which language message to be set from the locale master table for the perticuler locale.
			LocaleMasterVO localeVO=LocaleMasterCache.getLocaleDetailsFromlocale(locale);
            if(PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
			{
				senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang1Msg());
				receiverVO.setCommissionProfileSuspendMsg(receiverVO.getCommissionProfileLang1Msg());
			}
            if(!returnFlag)
			{
				if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue())
				    receiverVO.setPrimaryMsisdn(senderVO.getPrimaryMsisdn());
				productList = ChannelTransferBL.loadC2CXfrProducts(con,senderVO.getUserID(),dataObject.get("networkId").textValue(),senderVO.getCommissionProfileSetID(),curDate,"usersearch",true,argument,locale, null,admUserID);
			}
			if(PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(senderVO.getInSuspend()))
			{
				if(_log.isDebugEnabled())
					_log.debug("channelUserReturned","USER IS IN SUSPENDED IN THE SYSTEM");
				response.setParameters(new String[]{senderVO.getUserName()});
			    response.setFormError("message.channeltransfer.return.errormsg.userinsuspend");
				return response;
			}
		
			if (productList != null) {
				c2cWithdrawVO.setProductList((ChannelTransferItemsVO)productList.get(0));
				mp.put("productList", productList);
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
						c2cWithdrawVO);
			}
		}
		catch (BTSLBaseException be) {
			if (_log.isDebugEnabled()) 
				_log.debug(methodName,"Exception: " +be);
			response.setDataObject(PretupsI.RESPONSE_FAIL, true, c2cWithdrawVO);
			response.setParameters(be.getArgs());
			response.setFormError(be.getMessageKey());
			return response;
		}finally {
			if (mcomCon != null) {
				mcomCon.close("C2CWithdrawRestServiceImpl#validateChannelUser");
				mcomCon = null;
			}

		if (_log.isDebugEnabled()) 
			_log.debug(methodName, "exiting:");
		}
		return response;
	}
	
	/**
	 * This method calculates the tax and other things
	 *
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * 
	 * @return PretupsResponse<Map<String, String>>
	 * 
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<ChannelTransferItemsVO> confirmC2CWithdraw(
			String requestData) throws Exception {
		final String methodName = "C2CWithdrawRestServiceImpl#confirmC2CWithdraw";
		PretupsResponse<ChannelTransferItemsVO> response = new PretupsResponse<>();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered:");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil
					.convertJSONToObject(requestData,
							new TypeReference<JsonNode>() {
							});
			C2CWithdrawVO c2cWithdrawVO;
			C2CWithdrawViaAdmValidator validator=new C2CWithdrawViaAdmValidator();
			c2cWithdrawVO=(C2CWithdrawVO)PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<C2CWithdrawVO>() {});
			if ((BTSLUtil.isNullString(c2cWithdrawVO.getAmount()) || "".equalsIgnoreCase(c2cWithdrawVO.getAmount())) && 
					(BTSLUtil.isNullString(c2cWithdrawVO.getRemarks()) || "".equalsIgnoreCase(c2cWithdrawVO.getRemarks()))){
				validator.validateRequestData("CONFIRMWITHDRAW",response, c2cWithdrawVO, "validateAmount");
			}
			ChannelUserVO senderVO = null;
			final Date curDate = new Date();
			List productList = null;
			String argument=null;
			String admUserID=dataObject.get("admUserID").textValue();
			String amount = c2cWithdrawVO.getAmount();
			
			senderVO=c2cWithdrawVO.getSenderVO();

				final Locale locale = new Locale(
						(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
						(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
				productList = ChannelTransferBL.loadC2CXfrProducts(con,senderVO.getUserID(),dataObject.get("networkId").textValue(),senderVO.getCommissionProfileSetID(),curDate,"usersearch",true,argument,locale, null,admUserID);

				ChannelTransferItemsVO channelTransferItemsVO = null;
				final ArrayList itemsList = new ArrayList();
				ChannelTransferItemsVO channelTransferItemsOldVO = null;
				for (int i = 0, k = productList.size(); i < k; i++) {
					channelTransferItemsOldVO = (ChannelTransferItemsVO) productList
							.get(i);

					channelTransferItemsOldVO.setRequestedQuantity(amount);

					channelTransferItemsVO = new ChannelTransferItemsVO();
					populateTransferItemsVO(channelTransferItemsVO,
							channelTransferItemsOldVO);
					itemsList.add(channelTransferItemsVO);
				}

				if (Long.parseLong(channelTransferItemsVO.getRequestedQuantity()) <= 0) {

					response.setParameters(new String[] { channelTransferItemsVO
							.getShortName() });
					response.setFormError("userreturn.withdrawreturn.error.qtygtzero");
				}
				// ends here
				else if (PretupsBL
						.getSystemAmount(Long.parseLong(channelTransferItemsVO
						.getRequestedQuantity())) > channelTransferItemsVO.getBalance()) {
					response.setParameters(new String[] { channelTransferItemsVO
							.getShortName() });
					response.setFormError("userreturn.withdrawreturn.error.qtymorenbalance");
				} 
				else {
					// make a new channel TransferVO to transfer into the method
					// during tax calculataion
					final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
					channelTransferVO.setChannelTransferitemsVOList(itemsList);
					channelTransferVO
							.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
		            channelTransferVO.setOtfFlag(false);

					ChannelTransferBL.loadAndCalculateTaxOnProducts(con,
							senderVO.getCommissionProfileSetID(),
							senderVO.getCommissionProfileSetVersion(),
							channelTransferVO, true, "viewproduct",
							PretupsI.TRANSFER_TYPE_C2C);
					channelTransferItemsVO.setProductMrpStr(PretupsBL
							.getDisplayAmount(channelTransferItemsVO
									.getProductTotalMRP()));
						response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,channelTransferItemsVO);
					}
			 
		} catch (BTSLBaseException be) {
			if (_log.isDebugEnabled()) 
				_log.debug(methodName,"Exiting Exception: " +be);
			response.setParameters(be.getArgs());
			response.setFormError(be.getMessageKey());
			return response;
		}finally {
			if (mcomCon != null) {
				mcomCon.close("C2CWithdrawRestServiceImpl#confirmC2CWithdraw");
				mcomCon = null;
			}
		}
		return response;
	}

	/**
	 * Method populateTransferItemsVO 
	 * FORMBEAN
	 * 
	 * @param transferItemsVO
	 * @param channelTransferItemOldVO
	 * @param receiverVO
	 * @throws BTSLBaseException
	 */
	
	private void populateTransferItemsVO(
			ChannelTransferItemsVO transferItemsVO,
			ChannelTransferItemsVO channelTransferItemOldVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("populateTransferItemsVO", "Entered transferItemsVO "
					+ transferItemsVO + " UserBalancesVO "
					+ channelTransferItemOldVO);
		}

		transferItemsVO.setProductType(channelTransferItemOldVO
				.getProductType());
		transferItemsVO.setProductCode(channelTransferItemOldVO
				.getProductCode());
		transferItemsVO.setShortName(channelTransferItemOldVO
				.getShortName());
		transferItemsVO.setProductName(channelTransferItemOldVO
				.getShortName());
		transferItemsVO.setCommProfileDetailID(channelTransferItemOldVO
				.getCommProfileDetailID());

		transferItemsVO.setRequestedQuantity(channelTransferItemOldVO
				.getRequestedQuantity());
		if (channelTransferItemOldVO.getRequestedQuantity() != null) {
			transferItemsVO.setRequiredQuantity(PretupsBL
					.getSystemAmount(channelTransferItemOldVO
							.getRequestedQuantity()));
		}
		transferItemsVO.setUnitValue(channelTransferItemOldVO
				.getUnitValue());
		transferItemsVO.setBalance(channelTransferItemOldVO.getBalance());
		transferItemsVO.setProductShortCode(channelTransferItemOldVO
				.getProductShortCode());
		transferItemsVO.setTaxOnChannelTransfer(channelTransferItemOldVO
				.getTaxOnChannelTransfer());
		transferItemsVO.setTaxOnFOCTransfer(channelTransferItemOldVO
				.getTaxOnFOCTransfer());
		if (_log.isDebugEnabled()) {
			_log.debug("populateTransferItemsVO", "Exiting transferItemsVO "
					+ transferItemsVO + " UserBalancesVO "
					+ channelTransferItemOldVO);
		}
	}

	/**
	 * This method confirms the C2C withdraw
	 *
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * 
	 * @return PretupsResponse<ChannelTransferVO>
	 * 
	 * @throws BTSLBaseException
	 *             , Exception
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<ChannelTransferVO> confirmTransaction(
			String requestData) throws Exception{
		final String methodName = "C2CWithdrawRestServiceImpl#confirmTransaction";
		PretupsResponse<ChannelTransferVO> response = new PretupsResponse<>();

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			final Date curDate = new Date();
			ChannelUserVO senderVO = null;
			final String errorDispalyPath = "approveerror";
			
			C2CWithdrawVO c2cWithdrawVO;
			final ArrayList itemsList = new ArrayList();
			JsonNode dataObject = (JsonNode) PretupsRestUtil
					.convertJSONToObject(requestData,
							new TypeReference<JsonNode>() {
							});
			c2cWithdrawVO=(C2CWithdrawVO)PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<C2CWithdrawVO>() {});
			ChannelTransferItemsVO itemsVO=(ChannelTransferItemsVO)PretupsRestUtil
					.convertJSONToObject(dataObject.get("map").toString(), new TypeReference<ChannelTransferItemsVO>() {});
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			String admUserID=dataObject.get("admUserID").textValue();
			ChannelUserVO receiverVO = c2cWithdrawVO.getReceiverVO();
			senderVO=c2cWithdrawVO.getSenderVO();
				
				
				itemsList.add(itemsVO);
				final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
				this.constructVofromForm(itemsVO, c2cWithdrawVO,
						channelTransferVO, curDate,admUserID);
				channelTransferVO.setChannelTransferitemsVOList(itemsList);
				channelTransferVO.setActiveUserId(admUserID);
				channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
				channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
				channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
	            channelTransferVO.setOtfFlag(false);
	            mcomCon = new MComConnection();
	            con=mcomCon.getConnection();
				ChannelTransferBL.loadAndCalculateTaxOnProducts(con,senderVO.getCommissionProfileSetID(),senderVO.getCommissionProfileSetVersion(),
						channelTransferVO, true, "viewproduct",
						PretupsI.TRANSFER_TYPE_C2C);
				final int count = ChnlToChnlTransferTransactionCntrl
						.withdrawAndReturnChannelToChannel(con,
								channelTransferVO, false, true,
								errorDispalyPath, curDate);

				if (count > 0) {
					if (((Boolean) PreferenceCache
							.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))
							.booleanValue()) {
						UserEventRemarksVO userRemarskVO = null;
						ArrayList<UserEventRemarksVO> c2cRemarks = null;
						if (channelTransferVO != null) {
							int insertCount = 0;
							c2cRemarks = new ArrayList<>();
							userRemarskVO = new UserEventRemarksVO();
							userRemarskVO.setCreatedBy(channelTransferVO
									.getCreatedBy());
							userRemarskVO.setCreatedOn(new Date());
							userRemarskVO
									.setEventType(PretupsI.TRANSFER_TYPE_C2C);
							userRemarskVO.setRemarks(channelTransferVO
									.getChannelRemarks());
							userRemarskVO.setMsisdn(channelTransferVO
									.getFromUserCode());
							userRemarskVO.setUserID(channelTransferVO
									.getFromUserID());
							userRemarskVO.setUserType("SENDER");
							userRemarskVO.setModule(PretupsI.C2C_MODULE);
							c2cRemarks.add(userRemarskVO);
							insertCount = new UserWebDAO().insertEventRemark(
									con, c2cRemarks);
							if (insertCount <= 0) {
								con.rollback();
								_log.error("process",
										"Error: while inserting into userEventRemarks Table");
								throw new BTSLBaseException(this, "save",
										"error.general.processing");
							}

						}
					}
					con.commit();
					List balanceList = channelUserDAO.loadUserBalances(
							con, channelTransferVO.getNetworkCode(),
							channelTransferVO.getNetworkCode(),
							channelTransferVO.getToUserID());
					
					List balanceList1 = channelUserDAO.loadUserBalances(
							con, channelTransferVO.getNetworkCode(),
							channelTransferVO.getNetworkCode(),
							channelTransferVO.getFromUserID());

					String finalBalance = null;
					String senderPostBalance=null;
					int balanceLists=balanceList.size();
					for (int i = 0; i < balanceLists; i++) {
						UserBalancesVO ub = (UserBalancesVO) balanceList.get(i);

						finalBalance = PretupsBL.getDisplayAmount(ub
								.getBalance());
					}
					
					channelTransferVO.setReceiverPostStock(finalBalance);
					int balanceLists1=balanceList1.size();
					for (int i = 0; i < balanceLists1; i++) {
						UserBalancesVO ub = (UserBalancesVO) balanceList1.get(i);

						senderPostBalance = PretupsBL.getDisplayAmount(ub
								.getBalance());
					}
					channelTransferVO.setSenderPostStock(senderPostBalance);
					ChannelTransferBL
							.prepareUserBalancesListForLogger(channelTransferVO);
					sendMessages(con,channelTransferVO,PretupsBL.getDisplayAmount(itemsVO.getNetPayableAmount()),senderVO,receiverVO,itemsList);
					response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
							channelTransferVO);
					
				} else {
					con.rollback();
					if (_log.isDebugEnabled()) {
						_log.debug(methodName, "Exiting with error: ");
					}
				}
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CWithdrawRestServiceImpl#confirmTransaction");
				mcomCon = null;
			}
		if (_log.isDebugEnabled()) 
			_log.debug(methodName, "Exiting:");
		}
		return response;
	}

	/**
	 * Method constructVofromForm This method is to construct VO from the
	 * FORMBEAN
	 * 
	 * @param request
	 * @param p_theForm
	 * @param p_channelTransferVO
	 * @param p_curDate
	 * @throws BTSLBaseException
	 */
	private void constructVofromForm(ChannelTransferItemsVO itemsVO,
			C2CWithdrawVO withdrawVO,
			ChannelTransferVO channelTransferVO, Date curDate,String admUserID)
			throws BTSLBaseException {
		ChannelUserVO receiverVO=withdrawVO.getReceiverVO();
		ChannelUserVO senderVO=withdrawVO.getSenderVO();
		if (_log.isDebugEnabled()) {
			_log.debug("constructVofromForm", "Entered ReceiverVO: "
					+ receiverVO + " SenderVO: " + senderVO
					+ " ChannelTransferVO: " + channelTransferVO
					+ " CurDate " + curDate);
		}
		int transferMRP=0;
		if(PretupsI.COMM_TYPE_POSITIVE.equals(senderVO.getDualCommissionType()))
            transferMRP += (itemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(itemsVO.getUnitValue()));
        else
            transferMRP += itemsVO.getUnitValue() * Double.parseDouble(itemsVO.getRequestedQuantity());
		channelTransferVO.setNetworkCode(receiverVO.getNetworkID());
		channelTransferVO.setNetworkCodeFor(receiverVO.getNetworkID());
		channelTransferVO.setCategoryCode(senderVO.getCategoryVO()
				.getCategoryCode());
		channelTransferVO.setSenderGradeCode(senderVO.getUserGrade());
		channelTransferVO.setReceiverGradeCode(receiverVO.getUserGrade());
		channelTransferVO.setDomainCode(senderVO.getDomainID());
		channelTransferVO.setFromUserID(senderVO.getUserID());
		channelTransferVO.setFromUserName(senderVO.getUserName());
		channelTransferVO.setToUserID(receiverVO.getUserID());
		channelTransferVO.setToUserName(receiverVO.getUserName());
		channelTransferVO.setTransferDate(curDate);
		channelTransferVO.setGraphicalDomainCode(senderVO
				.getGeographicalCode());
		channelTransferVO.setCommProfileSetId(senderVO
				.getCommissionProfileSetID());
		channelTransferVO.setCommProfileVersion(senderVO
				.getCommissionProfileSetVersion());
		channelTransferVO.setDualCommissionType(senderVO
				.getDualCommissionType());
		channelTransferVO.setChannelRemarks(withdrawVO.getRemarks());
		channelTransferVO.setCreatedOn(curDate);
		channelTransferVO.setCreatedBy(admUserID);
		channelTransferVO.setModifiedOn(curDate);
		channelTransferVO.setModifiedBy(admUserID);
		channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
		channelTransferVO
				.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
		channelTransferVO.setTransferInitatedBy(receiverVO.getUserID());
		channelTransferVO
				.setSenderTxnProfile(senderVO.getTransferProfileID());
		channelTransferVO.setReceiverTxnProfile(receiverVO
				.getTransferProfileID());
		channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
		channelTransferVO.setReceiverCategoryCode(receiverVO
				.getCategoryCode());
		channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
		channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
		channelTransferVO.setRequestedQuantity(itemsVO.getRequiredQuantity());
		channelTransferVO.setTransferMRP(transferMRP);
		channelTransferVO.setPayableAmount(itemsVO.getPayableAmount());
		channelTransferVO.setNetPayableAmount(itemsVO.getNetPayableAmount());
		channelTransferVO.setTotalTax1(itemsVO.getTax1Value());
		channelTransferVO.setTotalTax2(itemsVO.getTax2Value());
		channelTransferVO.setTotalTax3(itemsVO.getTax3Value());
		channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);

		channelTransferVO.setControlTransfer(PretupsI.YES);

		channelTransferVO
				.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
		channelTransferVO.setRequestGatewayCode(PretupsI.GATEWAY_TYPE_WEB);
		channelTransferVO.setRequestGatewayType(PretupsI.GATEWAY_TYPE_WEB);
		// adding the some additional information for sender/reciever
		channelTransferVO.setReceiverGgraphicalDomainCode(receiverVO
				.getGeographicalCode());
		channelTransferVO.setReceiverDomainCode(receiverVO.getDomainID());
		channelTransferVO.setToUserCode(PretupsBL
				.getFilteredMSISDN(receiverVO.getMsisdn()));
		channelTransferVO.setFromUserCode(PretupsBL
				.getFilteredMSISDN(senderVO.getMsisdn()));

		channelTransferVO.setToChannelUserStatus(receiverVO.getStatus());
		channelTransferVO.setFromChannelUserStatus(senderVO.getStatus());

		if (_log.isDebugEnabled()) {
			_log.debug("constructVofromForm", "Exited : "
					+ " ChannelTransferVO: " + channelTransferVO
					+ " CurDate " + curDate);
		}
	}

	/**
	 * Prepare the SMS message which we have to send the user as SMS
	 * 
	 * @param p_returnedProductList
	 *            ArrayList
	 * @param p_smsKey
	 * @return ArrayList
	 */
	private Object[] prepareSMSMessageList(ArrayList returnedProductList,
			String txnKey, String balKey) {
		if (_log.isDebugEnabled()) {
			_log.debug("prepareSMSMessageList",
					"Entered returnedProductList size =  : "
							+ returnedProductList.size() + " txnKey : "
							+ txnKey + " balKey : " + balKey);
		}
		final ArrayList txnSmsMessageList = new ArrayList();
		final ArrayList balSmsMessageList = new ArrayList();
		KeyArgumentVO keyArgumentVO ;
		String[] argsArr;
		ChannelTransferItemsVO channelTransferItemsVO;
		for (int i = 0, k = returnedProductList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) returnedProductList
					.get(i);
			keyArgumentVO = new KeyArgumentVO();
			argsArr = new String[3];
			argsArr[1] = channelTransferItemsVO.getRequestedQuantity();
			argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
			keyArgumentVO.setKey(txnKey);
			keyArgumentVO.setArguments(argsArr);
			txnSmsMessageList.add(keyArgumentVO);

			keyArgumentVO = new KeyArgumentVO();
			argsArr = new String[3];
			argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemsVO
					.getAfterTransSenderPreviousStock()
					- channelTransferItemsVO.getApprovedQuantity());
			argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
			keyArgumentVO.setKey(balKey);
			keyArgumentVO.setArguments(argsArr);
			balSmsMessageList.add(keyArgumentVO);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(
					"prepareSMSMessageList",
					"Exited  txnSmsMessageList.size() = "
							+ txnSmsMessageList.size()
							+ ", balSmsMessageList.size()"
							+ balSmsMessageList.size());
		}
		return new Object[] { txnSmsMessageList, balSmsMessageList };
	}

	/*
	 * function to sendMessage
	 * @param connection
	 * @param channeltransferVO
	 * @param netPayableAmount
	 * @param ChannelUserVO
	 * @param itemsList
	 */
	
	
	private void sendMessages(Connection connection,ChannelTransferVO channelTransferVO,String netPayableAmount,
			ChannelUserVO senderVO,ChannelUserVO receiverVO,ArrayList itemsList) throws BTSLBaseException
	{
		String senderTxnSubKey;
		String senderBalSubKey;
		String senderSMSKey;
		String receiverTxnSubKey;
		String receiverBalSubKey;
		String receiverSMSKey;
		String country; 
		String language ;
		// Else withdraw
		receiverTxnSubKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_TXNSUBKEY;
		receiverBalSubKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_BALSUBKEY;
		receiverSMSKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS;

		senderTxnSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_WITHDRAW_RECEIVER_TXNSUBKEY;
		senderBalSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_WITHDRAW_RECEIVER_BALSUBKEY;
		senderSMSKey = PretupsErrorCodesI.C2S_CHNL_CHNL_WITHDRAW_RECEIVER;

		if (PretupsI.TRANSFER_CATEGORY_TRANSFER
				.equals(channelTransferVO.getTransferCategory())) {
			senderSMSKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_RECEIVER_AGENT;
			receiverSMSKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_SENDER_AGENT;
		}

		final UserDAO userDAO = new UserDAO();
		if (((Boolean) PreferenceCache
				.getSystemPreferenceValue(PreferenceI.SMS_TO_LOGIN_USER))
				.booleanValue()) {
			// added by vikram
			final String userID = channelTransferVO
					.getActiveUserId();
			UserPhoneVO phoneVO;
			phoneVO = userDAO.loadUserPhoneVO(connection, userID);
			if (phoneVO != null) {
				country = phoneVO.getCountry();
				language = phoneVO.getPhoneLanguage();
				final Object[] smsListArr = ChannelTransferBL
						.prepareSMSMessageListForReceiverForC2C(
								connection, channelTransferVO,
								receiverTxnSubKey,
								receiverBalSubKey);
				Locale locale = new Locale(language, country);
				final String[] array = {
						BTSLUtil.getMessage(locale,
								(ArrayList) smsListArr[0]),
						BTSLUtil.getMessage(locale,
								(ArrayList) smsListArr[1]),
						channelTransferVO.getTransferID(),
						netPayableAmount,
						senderVO.getMsisdn() };
				final BTSLMessages messages = new BTSLMessages(
						receiverSMSKey, array);
				final PushMessage pushMessage = new PushMessage(
						phoneVO.getMsisdn(), messages,
						channelTransferVO.getTransferID(), null,
						locale, channelTransferVO.getNetworkCode());
				pushMessage.push();
			}
		}

		if (receiverVO.isStaffUser()) { // then send the sms to the
			// parent user
			receiverSMSKey = PretupsErrorCodesI.CHNL_WITHDRAW_SUCCESS_STAFF;
			// ChannelUserVO channelUserVO= userDAO.load
			UserPhoneVO phoneVO;
			phoneVO = userDAO.loadUserPhoneVO(connection,
					channelTransferVO.getToUserID());
			if (phoneVO != null) {
				country = phoneVO.getCountry();
				language = phoneVO.getPhoneLanguage();
				Locale locale = new Locale(language, country);
				final Object[] smsListArr = prepareSMSMessageList(
						itemsList,
						PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY,
						PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
				final String[] array = {
						BTSLUtil.getMessage(locale,
								(ArrayList) smsListArr[0]),
						BTSLUtil.getMessage(locale,
								(ArrayList) smsListArr[1]),
						channelTransferVO.getTransferID(),
						netPayableAmount,
						receiverVO.getMsisdn(),
						receiverVO.getUserName() };
				final BTSLMessages messages = new BTSLMessages(
						receiverSMSKey, array);
				final PushMessage pushMessage = new PushMessage(
						phoneVO.getMsisdn(), messages,
						channelTransferVO.getTransferID(), null,
						locale, channelTransferVO.getNetworkCode());
				pushMessage.push();
			} 
		}

		final String prefUserID = channelTransferVO.getFromUserID();
		UserPhoneVO primaryPhoneVOS = null;
		UserPhoneVO phoneVO;
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()
					&& !((channelTransferVO.getFromUserCode())
							.equalsIgnoreCase(senderVO
									.getMsisdn()))) {
				primaryPhoneVOS = userDAO.loadUserAnyPhoneVO(
						connection, senderVO.getMsisdn());
			}
			phoneVO = userDAO.loadUserAnyPhoneVO(connection,
					channelTransferVO.getFromUserCode());
		} else {
			phoneVO = userDAO.loadUserPhoneVO(connection,
					prefUserID);
		}
		Object[] smsListArr = null;
		Locale locale;
		if (phoneVO != null) {
			country = phoneVO.getCountry();
			language = phoneVO.getPhoneLanguage();
			smsListArr = prepareSMSMessageList(itemsList,
					senderTxnSubKey, senderBalSubKey);
			locale = new Locale(language, country);
			final String[] array = {
					BTSLUtil.getMessage(locale,
							(ArrayList) smsListArr[0]),
					BTSLUtil.getMessage(locale,
							(ArrayList) smsListArr[1]),
					channelTransferVO.getTransferID(),
					netPayableAmount,
					receiverVO.getMsisdn() };
			final BTSLMessages messages = new BTSLMessages(
					senderSMSKey, array);
			final PushMessage pushMessage = new PushMessage(
					phoneVO.getMsisdn(), messages,
					channelTransferVO.getTransferID(), null,
					locale, channelTransferVO.getNetworkCode());
			pushMessage.push();
		}
		if (primaryPhoneVOS != null) {
			country = primaryPhoneVOS.getCountry();
			language = primaryPhoneVOS.getPhoneLanguage();
			locale = new Locale(language, country);
			final String[] array = {
					BTSLUtil.getMessage(locale,
							(ArrayList) smsListArr[0]),
					BTSLUtil.getMessage(locale,
							(ArrayList) smsListArr[1]),
					channelTransferVO.getTransferID(),
					netPayableAmount,
					receiverVO.getMsisdn() };
			final BTSLMessages messages = new BTSLMessages(
					senderSMSKey, array);
			final PushMessage pushMessage = new PushMessage(
					senderVO.getMsisdn(), messages,
					channelTransferVO.getTransferID(), null,
					locale, channelTransferVO.getNetworkCode());
			pushMessage.push();
		}

	}
}
