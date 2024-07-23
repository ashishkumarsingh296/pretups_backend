package com.client.pretups.channel.user.businesslogic;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferDAO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;



/*
 * This class implements UserTransferRestService and provides basic method to transfer channel user using OTP
 */
/**
 * @author akanksha.gupta
 *
 */
public class UserTransferRestServiceImpl implements UserTransferRestService {

	
	public static final Log log = LogFactory.getLog(UserTransferRestServiceImpl.class.getName());
	private static final  String CLASSNAME = "UserTransferRestServiceImpl";

	
	
	/**
	 * This method load CategoryList data from DB 
	 * @param requestData Json string of Of User Type
	 * @return  PretupsResponse<List<ListValueVO>>
	 * @throws BTSLBaseException, Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> loadCategoryData(String requestData) throws BTSLBaseException  {
		final String methodName = "loadCategoryData";
		
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+"#"+methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
		
		try{
			 CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			 mcomCon = new MComConnection();
			 try{con=mcomCon.getConnection();}catch(SQLException e){
				 log.error(methodName, "SQLException " + e.getMessage());
     			log.errorTrace(methodName, e);
			 }
			 List<ListValueVO> list = categoryWebDAO.loadCategoryVOListForUserTransfer(con);
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
			
		}catch (BTSLBaseException  e) {
			throw new BTSLBaseException(e);
		}
		finally {
           
			if (mcomCon != null) {
				mcomCon.close("UserTransferRestServiceImpl#loadCategoryData");
				mcomCon = null;
			}

       		if (log.isDebugEnabled()) {
       			log.debug(CLASSNAME+"#"+methodName, PretupsI.EXITED);
       		}
           
        }
		
		
		return response;
	}


	 /**
     * UserTransferRestServiceImpl.java
     * @param requestData
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     * PretupsResponse<List<UserVO>>
     * akanksha.gupta
     * 01-Sep-2016 3:05:11 pm
     */
	@Override
	public PretupsResponse<List<UserVO>> loadUserListData(String requestData) throws BTSLBaseException  {
		final String methodName = "loadUserListData";

		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+"#"+methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<UserVO>> response = new PretupsResponse<>();
		try{
			Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			
			UserWebDAO userWebDAO = new UserWebDAO();
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				 log.error(methodName, "SQLException " + e.getMessage());
	     			log.errorTrace(methodName, e);
			}
			final String status = PretupsBL.userStatusIn() + ", '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
			List<UserVO> list = userWebDAO.loadOwnerUserListForUserTransfer(con,map.get("zoneCode").toString(),map.get("ownerName").toString(),map.get("domainCode").toString(),PretupsI.STATUS_IN,status,map.get("loggedInUserID").toString());
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
		}
		catch (BTSLBaseException |IOException  e) {
			throw new BTSLBaseException(e);
		}
		finally {

			if (mcomCon != null) {
				mcomCon.close("UserTransferRestServiceImpl#loadUserListData");
				mcomCon = null;
			}
			 if (log.isDebugEnabled()) {
					log.debug(CLASSNAME+"#"+methodName, PretupsI.EXITED);
				}
		}
		return response;
	}

    /**
     * UserTransferRestServiceImpl.java
     * @param requestData
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     * PretupsResponse<ChannelUserVO>
     * akanksha.gupta
     * 01-Sep-2016 3:05:08 pm
     */
	@Override
	public PretupsResponse<ChannelUserVO> confirmUserDetail(String requestData) throws BTSLBaseException  {
		final String methodName = "confirmUserDetail";
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+"#"+methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserVO channelUserVO= null;
		ChannelUserTransferDAO userTransferDAO =null;
		final Date curDate = new Date();
		PretupsResponse<ChannelUserVO> response = new PretupsResponse<>();
		try{
			Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();

			Boolean isUserCode = (Boolean) map.get("isUserCode");
			String userID ="";
			if(map.get("userID") != null)
			 userID = map.get("userID").toString();
			
			 String  msisdn ="";
			
			if(map.get("msisdn") != null){
				
			 msisdn = map.get("msisdn").toString();
			 
			 if (log.isDebugEnabled()) {
					log.debug(CLASSNAME+"#"+methodName, msisdn+"--"+map.get("msisdn").toString());
				}
			}
			if(!BTSLUtil.isNullString(userID))
				channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, userID,isUserCode,curDate,true);
			else if(!BTSLUtil.isNullString(msisdn))
				channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, msisdn,isUserCode,curDate,true);

			String loggedInUserName = map.get("loggedInUserName").toString();
			String	loggedInUserMsisdn= map.get("loggedInUserMsisdn").toString();
			if(channelUserVO == null)
			{
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.CHANNEL_USER_TRANSFER_CANNOT_INITIATED);
			}

			 if(!BTSLUtil.isNullString(msisdn))
			 {
				
				
				 CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
				 List<ListValueVO> list = categoryWebDAO.loadCategoryVOListForUserTransfer(con);

				  boolean validCategory= false;
				  if(list!= null)
				  {
					  for(ListValueVO userVO :list)
					  {

						  if (log.isDebugEnabled()) 
							  log.debug(CLASSNAME+"#"+methodName, channelUserVO.getCategoryCode()+"--"+userVO.getValue().split(":")[1]);

						  if(channelUserVO.getCategoryCode().equals(userVO.getValue().split(":")[1]) && (map.get("domainID").toString()).equals(userVO.getValue().split(":")[0]) )
						  {
							  validCategory= true;
							  break;
						  }
					  }
				  }
				  	if(!validCategory)
				  	{
				  		throw new BTSLBaseException(this,methodName,"usermovement.validateotp.error.failed.invalid.category");
				  	}
				  	else if( channelUserVO.getOwnerID().equals( map.get("loggedInUserID").toString())||channelUserVO.getParentID().equals( map.get("loggedInUserID").toString()))
					 {
						 throw new BTSLBaseException(this,methodName,"usermovement.validateotp.error.failed.within.hierarchy");
					 }

			 }
				
		    	if (log.isDebugEnabled()) {
			    	log.debug(CLASSNAME+"#"+methodName, "Exiting with VO:" + channelUserVO);
			}

			userTransferDAO = new ChannelUserTransferDAO();

			int updateCount = userTransferDAO.addUserTransferInitiate(con, channelUserVO, map.get("loggedInUserID").toString());
				if(updateCount>0)
				{
					   MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
					    
				    Locale locale = null;

		            if (channelUserVO.getLanguage() == null) {
		                String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		                channelUserVO.setLanguage(language);
		            }
		            if (channelUserVO.getCountry() == null) {
		                String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		                channelUserVO.setCountry(country);
		            }

		               locale = new Locale(channelUserVO.getLanguage(), channelUserVO.getCountry());
		                String[] messageArgArray = { channelUserVO.getOTP(),loggedInUserMsisdn,loggedInUserName };

		        	   String  btslMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.OTP_MESSAGE+"_"+PretupsI.CHANNEL_USER_TRANSFER, messageArgArray);
		        
		        	   if (channelUserVO.getStaffUserDetails() != null) {
		        		   if (BTSLUtil.isNullString(channelUserVO.getStaffUserDetails().getMsisdn())) {
		        			   PushMessage pushParentMessages = new PushMessage(channelUserVO.getMsisdn(), btslMessage, null, messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale);
		        			   pushParentMessages.push();
		        		   } else {
		        			   PushMessage pushParentMessages = new PushMessage(channelUserVO.getStaffUserDetails().getMsisdn(), btslMessage, null, messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale);
		        			   pushParentMessages.push();
		        		   }
		        	   } else {
		        		   PushMessage pushParentMessages = new PushMessage(channelUserVO.getMsisdn(), btslMessage, null, messageGatewayVO.getRequestGatewayVO().getGatewayCode(), locale);
		        		   pushParentMessages.push();
		        	   }

					
					
					response.setParameters(new String[] { channelUserVO.getMsisdn() });
					response.setResponse(PretupsI.RESPONSE_SUCCESS, true,PretupsErrorCodesI.CHANNEL_USER_TRANSFER_INITIATED);
					response.setMessageCode(PretupsErrorCodesI.CHANNEL_USER_TRANSFER_INITIATED);
					response.setParameters(new String[]{channelUserVO.getMsisdn()});

					UserPhoneVO userPhoneVO = null;
					final UserDAO userDAO = new UserDAO();
					userPhoneVO = channelUserVO.getUserPhoneVO();
					 userPhoneVO = userDAO.loadUserPhoneVO(con, map.get("loggedInUserID").toString());
					this.sendPushMessage(userPhoneVO, PretupsErrorCodesI.CHANNEL_USER_TRANSFER_INITIATED,channelUserVO.getNetworkCode(),new String[]{channelUserVO.getMsisdn(),channelUserVO.getUserName(),loggedInUserMsisdn,loggedInUserName});
					
					if(PretupsI.YES.equals(Constants.getProperty("MESSGAE_ALLOWED_TO_PARENT_FOR_USER_MOVE")))
					{
						 userPhoneVO = userDAO.loadUserPhoneVO(con, channelUserVO.getParentID());
						this.sendPushMessage(userPhoneVO, PretupsErrorCodesI.CHANNEL_USER_TRANSFER_INITIATED_PARENT,channelUserVO.getNetworkCode(),new String[]{channelUserVO.getMsisdn(),channelUserVO.getUserName(),loggedInUserMsisdn,loggedInUserName});
					
					}
					if(PretupsI.YES.equals(Constants.getProperty("MESSGAE_ALLOWED_TO_OWNER_FOR_USER_MOVE")))
					{
						 userPhoneVO = userDAO.loadUserPhoneVO(con, channelUserVO.getOwnerID());
						
						this.sendPushMessage(userPhoneVO, PretupsErrorCodesI.CHANNEL_USER_TRANSFER_INITIATED_OWNER,channelUserVO.getNetworkCode(),new String[]{channelUserVO.getMsisdn(),channelUserVO.getUserName(),loggedInUserMsisdn,loggedInUserName});
					}
						
				}
				 
				
		}
			catch (BTSLBaseException bex) {
			log.error(methodName, PretupsI.BTSLEXCEPTION + bex +"channelUserVO :"+channelUserVO );
			log.errorTrace(methodName, bex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+"#"+methodName, "", "", "", PretupsI.EXCEPTION  + bex.getMessage());
			if(channelUserVO!=null)
			{
				log.error(methodName, PretupsI.BTSLEXCEPTION + bex +"inside if ");
				response.setResponse(bex.getMessage(),new String[] { channelUserVO.getMsisdn() });
			}
			else
			{
				response.setResponse(bex.getMessage(),null);
				response.setMessageCode(bex.getMessage());
			}

		} 
		catch (Exception ex) {
			log.error(methodName, PretupsI.EXCEPTION  + ex+"channelUserVO :"+channelUserVO);
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+"#"+methodName, "", "", "", PretupsI.EXCEPTION + ex.getMessage());
			if(channelUserVO!=null)
			{
				log.error(methodName, PretupsI.EXCEPTION  + ex +"inside if ");
				response.setResponse(ex.getMessage(),new String[] { channelUserVO.getMsisdn() });
			}	
			else
				response.setResponse(ex.getMessage(),null);
			response.setMessageCode(ex.getMessage());

			log.error(methodName, PretupsI.EXCEPTION  + ex +"response.getMessageCode() :"+response.getMessageCode()+"response.getParameters():"+response.getParameters() );

		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("UserTransferRestServiceImpl#confirmUserDetail");
				mcomCon = null;
			}
			 if (log.isDebugEnabled()) {
					log.debug(CLASSNAME+"#"+methodName, PretupsI.EXITED );
				}

			 
		}

		
		return response;
	}
	  /**
     * UserTransferRestServiceImpl.java
     * @param requestData
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     * PretupsResponse<List<UserVO>>
     * akanksha.gupta
     * 01-Sep-2016 3:05:06 pm
     */
	@Override
	public PretupsResponse<List<UserVO>> loadChannelUserListData(String requestData) throws BTSLBaseException  {
		final String methodName = "loadChannelUserListData";
		
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+"#"+methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<UserVO>> response = new PretupsResponse<>();
		try{
			Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			
			UserWebDAO userWebDAO = new UserWebDAO();
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				 log.error(methodName, "SQLException " + e.getMessage());
	     	     log.errorTrace(methodName, e);
			}
			final String status = PretupsBL.userStatusNotIn();
			if (log.isDebugEnabled()) {
				log.debug(CLASSNAME+"#"+methodName, "userCat : "+map.get("userCat").toString()+"userName: "+map.get("userName").toString()+"ownerId: "+ map.get("ownerId").toString());
			}
			List<UserVO> list = userWebDAO.loadUsersListByNameAndOwnerId(con,map.get("userCat").toString(),map.get("userName").toString(),map.get("ownerId").toString(),null,PretupsI.STATUS_NOTIN,status,"CHANNEL");
	    	if(list !=null && !list.isEmpty())
	    		response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
			else
				response.setResponse("channeluser.viewuserhierarchy.msg.trfunsuccess",null);
		}catch (BTSLBaseException | IOException e) {
			throw new BTSLBaseException(e);
		}
		finally {

			if (mcomCon != null) {
				mcomCon.close("UserTransferRestServiceImpl#loadChannelUserListData");
				mcomCon = null;
			}
			 if (log.isDebugEnabled()) {
					log.debug(CLASSNAME+"#"+methodName, PretupsI.EXITED);
				}
		}
		return response;
	}



	/**
	 * UserTransferRestServiceImpl.java
	 * @param userPhoneVO
	 * @param message
	 * @param networkCode
	 * @param param
	 * void
	 * akanksha.gupta
	 * 21-Sep-2016 3:17:38 pm
	 */
	 void sendPushMessage(UserPhoneVO userPhoneVO, String message,String networkCode,String[] param) {
		
		if (userPhoneVO != null) {
			 Locale locale = new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry());
			final PushMessage pushMessage = new PushMessage(userPhoneVO.getMsisdn(), new BTSLMessages(message,param), null, null, locale, networkCode);
			pushMessage.push();
		}
	}

	



    /**
     * UserTransferRestServiceImpl.java
     * @param requestData
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     * PretupsResponse<List<ChannelUserTransferVO>>
     * akanksha.gupta
     * 01-Sep-2016 3:05:02 pm
     */
	@Override
	public PretupsResponse<List<ChannelUserTransferVO>> loadInitiatedUserTransferListData(String requestData) throws BTSLBaseException  {
		final String methodName = "loadInitiatedUserTransferListData";

		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+"#"+methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<ChannelUserTransferVO>> response = new PretupsResponse<>();
		try{
			Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			ChannelUserTransferDAO userTransferDAO = new ChannelUserTransferDAO();
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				 log.error(methodName, "SQLException " + e.getMessage());
	     			log.errorTrace(methodName, e);
			}
			final String status = PretupsBL.userStatusIn() + ", '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
			List<ChannelUserTransferVO> list = userTransferDAO .loadInitiatedUserListForUserTransfer(con,map.get("zoneCode").toString(),map.get("userCat").toString(),map.get("domainCode").toString(),PretupsI.STATUS_IN,status,map.get("loggedInUserID").toString());
			 if(list != null && !list.isEmpty())
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
			 else 
				response.setResponse("channeluser.viewuserhierarchy.msg.trfunsuccess",null);
				
		}catch (BTSLBaseException | IOException e) {
			throw new BTSLBaseException(e);
		}
		finally {

			if (mcomCon != null) {
				mcomCon.close("UserTransferRestServiceImpl#loadInitiatedUserTransferListData");
				mcomCon = null;
			}
			 if (log.isDebugEnabled()) {
					log.debug(CLASSNAME+"#"+methodName, PretupsI.EXITED);
				}
		}

		

		return response;
	}
	  /**
     * UserTransferRestServiceImpl.java
     * @param requestData
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     * PretupsResponse<List<ChannelUserTransferVO>>
     * akanksha.gupta
     * 01-Sep-2016 3:04:55 pm
     */
	@Override
	public PretupsResponse<List<ChannelUserTransferVO>> loadInitiatedUserTransferWithMsisdn(String requestData) throws BTSLBaseException {
		final String methodName = "#loadInitiatedUserTransferWithMsisdn";

		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+"#"+methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<ChannelUserTransferVO>> response = new PretupsResponse<>();
		try{
				Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
				Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			
			ChannelUserTransferDAO userTransferDAO = new ChannelUserTransferDAO();

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final String status = PretupsBL.userStatusIn() + ", '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
			List<ChannelUserTransferVO> list = userTransferDAO.loadInitiatedUserbyMsisdnForUserTransfer(con,map.get("msisdn").toString(),PretupsI.STATUS_IN,status,map.get("loggedInUserID").toString());
			if(list != null  && !list.isEmpty())
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
			else 
				response.setResponse("channeluser.viewuserhierarchy.msg.trfunsuccess",null);
			

		}
		catch (BTSLBaseException | IOException bex) {
			log.error(methodName, PretupsI.BTSLEXCEPTION + bex);
			log.errorTrace(methodName, bex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+"#"+methodName, "", "", "", "Exception:" + bex.getMessage());
			response.setMessageCode(bex.getMessage());
			response.setResponse(bex.getMessage(),null);


		} 
		catch (Exception ex) {
			log.error(methodName, PretupsI.EXCEPTION  + ex);
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+"#"+methodName, "", "", "", "Exception:" + ex.getMessage());
			response.setMessageCode(ex.getMessage());
			response.setResponse(ex.getMessage(),null);
		}
		finally {

			if (mcomCon != null) {
				mcomCon.close("UserTransferRestServiceImpl#loadInitiatedUserTransferWithMsisdn");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(CLASSNAME+"#"+methodName, PretupsI.EXITED);
			}
		}
		return response;
	}



    /**
     * UserTransferRestServiceImpl.java
     * @param requestData
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     * PretupsResponse<Object>
     * akanksha.gupta
     * 01-Sep-2016 3:04:59 pm
     */
	@Override
	public PretupsResponse<Object> confirmUserTransfer(String requestData) throws BTSLBaseException {
			final String methodName = "#confirmUserTransfer";
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+"#"+methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserTransferDAO userTransferDAO =null;
		ChannelUserTransferVO channelUserTransferVO  = null;
		ChannelUserVO channelUserVO  = null;
    	PretupsResponse<Object> response = new PretupsResponse<>();
    try{
    	Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
		Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
		String userID = map.get("userID").toString();
		String loggedInUserID = map.get("loggedInUserID").toString();
		String categorycode = map.get("categorycode").toString();

			final Date currentDate = new Date();
			userTransferDAO = new ChannelUserTransferDAO();

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final String status = PretupsBL.userStatusIn() + ", '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
			channelUserTransferVO = userTransferDAO .loadInitiatedUserListForSelectedRecord(con,map.get("userID").toString(),map.get("loggedInUserID").toString(),map.get("otp").toString(),PretupsI.STATUS_IN,status);

			if(channelUserTransferVO != null)
			{
				channelUserTransferVO.setCreatedBy(loggedInUserID);
				channelUserTransferVO.setCreatedOn(currentDate);
				channelUserTransferVO.setModifiedBy(loggedInUserID);
				channelUserTransferVO.setModifiedOn(currentDate);
				channelUserTransferVO.setToParentID(loggedInUserID);
				
				mcomCon = new MComConnection();
				con=mcomCon.getConnection();
				String[] arr = new String[1];
				arr[0] = userID;
				
				ArrayList<ChannelUserVO> userHierarchyList = new ChannelUserWebDAO().loadUserHierarchyListForTransfer(con, arr, PretupsI.SINGLE, PretupsI.STATUS_NOTIN, PretupsBL.userStatusNotIn(), categorycode);
				channelUserTransferVO.setUserHierarchyList(userHierarchyList);
			
               boolean isPendingTxnFound = false;
                final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
		        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
    			UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
    			UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
    			int usersHierarchyLists=userHierarchyList.size();
                for (int i = 0; i <usersHierarchyLists ; i++) {
                	channelUserVO = userHierarchyList.get(i);
                	if(userID.equals( channelUserVO.getUserID()))
                	{
                		// Checking SOS Pending transactions
                		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
					        boolean isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, channelUserVO.getUserID());
					        if (isSOSPendingFlag) {
					        	throw new BTSLBaseException(this, methodName, "channeluser.userhierarchyview.msg.pending.SOS.exist", methodName);
					        }
						}
                		// Checking for pending LR transactions
                		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue()){
                			userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(channelUserVO.getUserID(), con, false, null);
                			if (userTrfCntVO!=null){ 
                				throw new BTSLBaseException(this, methodName, "channeluser.userhierarchyview.msg.pending.LR.exist", methodName);
                			}
                		}
                		if (channelTransferDAO.isPendingTransactionExist(con, channelUserVO.getUserID())){
                			isPendingTxnFound = true;
                			break;
                		}
                		if ( batchTransferDAO.isPendingTransactionExist(con, channelUserVO.getUserID())){
                			isPendingTxnFound = true;
                			break;
                		}
                	}
                }
                    
                if (isPendingTxnFound) {
                    throw new BTSLBaseException(this, methodName, "channeluser.userhierarchyview.msg.pendingtxnexist", methodName);
                }
				
				UserVO userVO = new UserDAO().loadUserDetailsFormUserID(con, loggedInUserID);
				channelUserTransferVO.setToOwnerID(userVO.getOwnerID());
				channelUserTransferVO.setToParentUserName(userVO.getUserName());
				getGeoDomainCode(con, channelUserTransferVO);
				int updateCount =0;
					updateCount = userTransferDAO .updateTransferedUserData(con,map.get("userID").toString(),map.get("loggedInUserID").toString(),map.get("otp").toString(),channelUserTransferVO);
					if (  updateCount > 0 ) {
						if(con != null)
							con.commit();
						response.setParameters(new String[] { channelUserTransferVO.getMsisdn() });
						response.setResponse(PretupsI.RESPONSE_SUCCESS, true,"channeluser.viewuserhierarchy.msg.trfsuccess");
						response.setMessageCode("channeluser.viewuserhierarchy.msg.trfsuccess");
						response.setParameters(new String[]{channelUserTransferVO.getMsisdn()});


						// sending the SMS to all of the users of their transfer
						// from one parent to other parent.
						final String[] arr1 = new String[2];
						arr1[0] = channelUserTransferVO.getParentUserName();
						arr1[1] = channelUserTransferVO.getToParentUserName();
						final StringBuilder sbf = new StringBuilder();
						final UserDAO userDAO = new UserDAO();
						for (int i = 0, j = usersHierarchyLists; i < j; i++) {
							channelUserVO = userHierarchyList.get(i);
							
							if(channelUserVO!= null && userVO !=null )
							{
								channelUserVO.setUserPhoneVO(userDAO.loadUserPhoneVO(con, channelUserVO.getUserID()));
								this.sendPushMessage(channelUserVO.getUserPhoneVO(), PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_MSG_SUCCESS_WITHOUTPRODUCT,channelUserVO.getNetworkCode(),arr1);

								ChannelUserLog.log("USERTRANSFER", channelUserVO, userVO, true, "Channel user hierarchy transfer new user ID=" + channelUserVO.getFxedInfoStr());
								if ("1".equals(channelUserVO.getUserlevel())) {
									sbf.append(channelUserVO.getUserName());
									sbf.append(",");

							
								}
							}
						}

						// load user Phone Vo of parent users

						
						UserPhoneVO userPhoneVO = null;
						userPhoneVO = userDAO.loadUserPhoneVO(con, channelUserVO.getParentID());
						this.sendPushMessage(userPhoneVO, PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_SENDER_PARENT,channelUserVO.getNetworkCode(),new String[] { sbf.substring(0,sbf.length() - 1) });
						userPhoneVO = userDAO.loadUserPhoneVO(con, channelUserTransferVO.getToParentID());
						this.sendPushMessage(userPhoneVO, PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_RECEIVER_PARENT,channelUserVO.getNetworkCode(),new String[] { sbf.substring(0,sbf.length() - 1) });
						
					} else {
						con.rollback();
						response.setMessageCode("channeluser.viewuserhierarchy.msg.trfunsuccess");
						response.setResponse("channeluser.viewuserhierarchy.msg.trfunsuccess",null);
						
					}
					
			}
			
			else
			{
				response.setMessageCode("channeluser.viewuserhierarchy.msg.trfunsuccess");
				response.setResponse("channeluser.viewuserhierarchy.msg.trfunsuccess",null);
				
			}
			

		}
		catch (BTSLBaseException | IOException bex) {
			log.error(methodName, PretupsI.BTSLEXCEPTION + bex);
			log.errorTrace(methodName, bex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+"#"+methodName, "", "", "", "Exception:" + bex.getMessage());
			response.setMessageCode(bex.getMessage());
			response.setResponse(bex.getMessage(),null);


		} 
		catch (Exception ex) {
			log.error(methodName, PretupsI.EXCEPTION  + ex);
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+"#"+methodName, "", "", "", "Exception:" + ex.getMessage());
			response.setMessageCode(ex.getMessage());
			response.setResponse(ex.getMessage(),null);


		}
		finally {
			
			if (mcomCon != null) {
				mcomCon.close("UserTransferRestServiceImpl#confirmUserTransfer");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(CLASSNAME+"#"+methodName, PretupsI.EXITED );
			}
			
		}
    	return response;
		}

	/**
	 * Methodget NewGeographicalDomainCode
	 * method to load the geographical domain list of to parent user, for
	 * updating the user geography info.
	 * 
	 * @param Connection
	 *            p_con
	 * @param ChannelUserTransferForm
	 *            p_channelUserTransferForm
	 * @param ChannelUserTransferVO
	 *            p_channelUserTransferVO
	 * @return void
	 */
	private void getGeoDomainCode(Connection con, ChannelUserTransferVO channelUserTransferVO) throws BTSLBaseException {
		final String methodName = "#getGeoDomainCode";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED +   ",channelUserTransferVO=" + channelUserTransferVO);
		}
		String geoGraphicalDomainType = null;
		final ChannelUserTransferWebDAO chnlUserTransferwebDAO = new ChannelUserTransferWebDAO();
		try {
			final List<GeographicalDomainVO> geoList = chnlUserTransferwebDAO.loadGeogphicalHierarchyListByToParentId(con, channelUserTransferVO.getToParentID());

			final List<ChannelUserVO> userList = channelUserTransferVO.getUserHierarchyList();
		
			if(userList !=null)
			{
				for (ChannelUserVO  chnlUserVO :userList) {

					geoGraphicalDomainType = chnlUserVO.getCategoryVO().getGrphDomainType();

					if(geoList!= null){
						for (GeographicalDomainVO geoDomainVO : geoList) {

							if (geoGraphicalDomainType.equals(geoDomainVO.getGrphDomainType())) {
								chnlUserVO.setGeographicalCode(geoDomainVO.getGrphDomainCode());
								break;
							}
						}

					}
				}
			}

		} catch (BTSLBaseException be) {
			log.error(methodName,PretupsI.BTSLEXCEPTION  + be);
			log.errorTrace(methodName, be);
			throw be;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}

	}
}


