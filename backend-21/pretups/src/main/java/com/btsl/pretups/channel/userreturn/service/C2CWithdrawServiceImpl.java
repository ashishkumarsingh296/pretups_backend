package com.btsl.pretups.channel.userreturn.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
/*
 *  * This class implements C2CWithdrawService and define method for performing
 *   * C2C Withdraw related operations
 *    */
import com.btsl.pretups.channel.userreturn.web.C2CWithdrawVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;


 @Service("c2cWithdrawService")
 public class C2CWithdrawServiceImpl implements C2CWithdrawService {

        public static final Log _log = LogFactory.getLog(C2CWithdrawServiceImpl.class.getName());


        @Autowired
        private PretupsRestClient pretupsRestClient;
        /**
 *       * Load Domain for Channel User
 *               * @return List The list of lookup filtered from DB
 *                       * @throws IOException, Exception
 *                               */

        @SuppressWarnings("unchecked")
   
        public List<ListValueVO> loadDomain() throws Exception  {
                final String methodName = "loadDomains";
                if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Entered ");
                }
                Map<String, Object> data = new HashMap();
                data.put("excludeUserType", PretupsI.DOMAIN_TYPE_CODE);

                Map<String, Object> object = new HashMap();
                object.put("data", data);
                String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.GREETMSGDOMAIN);

                PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {});
                List<ListValueVO> list =  response.getDataObject();
                if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Exiting");
                }
                return list;
        }

		/**
 *       * Load Category for Channel User(Receiver)
 *               * @return List The list of lookup filtered from DB
 *                       * @throws IOException, Exception
 *                               */

        @SuppressWarnings("unchecked")
        public List loadCategory(String domain,String networkId) throws Exception   {
                final String methodName = "C2CWithdrawServiceImpl#loadCategory";
                String responseString;
                if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Entered with domain: "+domain+" and networkId: "+networkId);
                }
                Map<String, Object> data = new HashMap();
				data.put("domain",domain);
				data.put("networkId", networkId);
				Map<String, Object> object = new HashMap();
                object.put("data", data);
                 responseString = pretupsRestClient.postJSONRequest(object, "LOADCATBYDOMAIN");

                   PretupsResponse<ArrayList> response = (PretupsResponse<ArrayList>) 
                		   PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<ArrayList>>() {});

                   ArrayList list = response.getDataObject();
   				if (_log.isDebugEnabled()) {
                           _log.debug(methodName, "Exiting with list:"+list);
                   }     
                                


                
                return list;
        }
		
		/**
 *       * Load Owner User List for Channel User(Receiver)
 *               * @return List The list of lookup filtered from DB
 *                       * @throws IOException, Exception
 *                               */
		
		@SuppressWarnings("unchecked")
		public List<ListValueVO> loadUserData(String domain , String category , String geography,String user) throws BTSLBaseException,IOException   {
			final String methodName = "loadUserData";
				String responseString ;
				if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Entered : domain : "+domain+" category : "+category+" geography : "+geography);
                }
				Map<String, Object> data = new HashMap();
				data.put("domainCode", domain);
				data.put("userCat" , category);
                data.put("zoneCode", geography);
				data.put("ownerName","%"+user+"%");
                Map<String, Object> object = new HashMap();
                object.put("data", data);

                
				responseString = pretupsRestClient.postJSONRequest(object,"LOADUSRLIST");
				PretupsResponse<List<UserVO>> response = (PretupsResponse<List<UserVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<UserVO>>>() {});

                List<UserVO> list = (List<UserVO>)response.getDataObject();
                
          	  List<ListValueVO> dropDown = new ArrayList<>();
        	  if(list != null && !list.isEmpty())
        	  {
              list.forEach(userVO -> {
        			ListValueVO listVO = new ListValueVO(userVO.getUserName(),userVO.getUserID()+":"+userVO.getUserName());
        			dropDown.add(listVO);
        		});
        	  }
        	
        	
                if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Exiting with list:"+dropDown);
                }
                return dropDown;	     
		}

		/**
 *       * Load Channel User details for Channel User(Receiver)
 *               * @return List The list of lookup filtered from DB
 *                       * @throws IOException, Exception
 *                               */
		
		@SuppressWarnings("unchecked")
		public ChannelUserVO loadUserDetails(C2CWithdrawVO withdrawVO,Boolean isUserId) throws BTSLBaseException ,IOException  {
		final String methodName = "loadUserDetails";
		String responseString;
				if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Entered with toMsisdn: " + withdrawVO.getToMsisdn()+" UserId: "+withdrawVO.getToUserId());
                }
				/*Map<String, Object> data = new HashMap();
				if(isUserId)
				data.put("userID",userID);
				else
					data.put("msisdn", userID);
				
				data.put("isUserId", isUserId);*/
				Map<String, Object> object = new HashMap();
                object.put("data", withdrawVO);
                object.put("isUserId", isUserId);
				responseString = pretupsRestClient.postJSONRequest(object,"LOADUSRDETAILS");
				PretupsResponse<ChannelUserVO> response = (PretupsResponse<ChannelUserVO>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<ChannelUserVO>>() {});

                ChannelUserVO channelUserVO =  response.getDataObject();

                if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Exiting with list:"+channelUserVO);
                }
                return channelUserVO;	     
		}
		
		/**
 *       * Load Channel User List for Channel User(Receiver)
 *               * @return List The list of lookup filtered from DB
 *                       * @throws IOException, Exception
 *                               */
		
		public List<UserVO> loadChannelUserData(String category , String ownerId,String user) throws BTSLBaseException,IOException {
			final String methodName = "loadChannelUserData";
				String responseString;
				if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Entered : category : "+category+" ownerId : "+ownerId+" user: "+user);
                }
				Map<String, Object> data = new HashMap();
				data.put("userCat" , category);
				data.put("userName","%"+user+"%");
				data.put("ownerId",ownerId);
                Map<String, Object> object = new HashMap();
                object.put("data", data);

                
				responseString = pretupsRestClient.postJSONRequest(object,"LOADCHNNLUSRLIST");
				PretupsResponse<List<UserVO>> response = (PretupsResponse<List<UserVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<UserVO>>>() {});

                List<UserVO> list = response.getDataObject();


                if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Exiting with list:"+list);
                }
                return list;	     
		}
		
		/**
 *       * Load Category List for Channel User(Sender) by Transfer Rule
 *               * @return List The list of lookup filtered from DB
 *                       * @throws IOException, Exception
 *                               */
				
		public List loadCatListByTrfRule(String domain,int seqNo) throws BTSLBaseException,IOException{
				final String methodName = "loadCatListByTrfRule";
				String responseString;
				if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Entered : domain"+domain+" seqNo: "+seqNo);
                }
				Map<String, Object> data = new HashMap();
				data.put("domain",domain);
				data.put("seqNo", seqNo);
				Map<String, Object> object = new HashMap();
                object.put("data", data);
				responseString = pretupsRestClient.postJSONRequest(object,"LOADCATLISTBTTRF");
				PretupsResponse<ArrayList> response = (PretupsResponse<ArrayList>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<ArrayList>>() {});
				ArrayList list = response.getDataObject();
				if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Exiting with list:"+list);
                }
                return list;
		}
		
		/**
 *       * Load Channel User List for Channel User(Sender)
 *               * @return List The list of lookup filtered from DB
 *                       * @throws BTSLBaseException
		 * @throws IOException 
		 * @throws JsonMappingException 
		 * @throws JsonParseException 
 *                               */
				public List<ListValueVO> loadUserListSender(String toCat,String fromCat,String domain,String networkId,String userID,UserVO userVO,String user) throws BTSLBaseException, JsonParseException, JsonMappingException, IOException{
				final String methodName = "loadUserListSender";
				String responseString;
				if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Entered : From_category : "+fromCat+" To_category : "+toCat+" Domain: "+domain+" Network ID: "+networkId+" userID= "+userID+" user:"+user);
                }
				Map<String, Object> data = new HashMap();
				data.put("fromCat",fromCat);
				data.put("toCat",toCat);
				data.put("domain",domain);
				data.put("networkId",networkId);
				data.put("userID",userID);
				data.put("user","%"+user+"%");
				Map<String, Object> object = new HashMap();
                object.put("data", data);
				responseString = pretupsRestClient.postJSONRequest(object,"LOADUSRLISTSNDR");
				@SuppressWarnings("unchecked")
				PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {});
				List<ListValueVO> list = response.getDataObject();
				if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Exiting with list:"+list);
                }
                return list;
		}
				

				/**
				 *       * Method validateUser to validate the sender user
				 *               * @return Map<String,List> from DB
				 *                       * @throws IOException
				 *                               */
		
		@SuppressWarnings("unchecked")
		@Override
	public PretupsResponse<C2CWithdrawVO> validateUser(C2CWithdrawVO c2cWithdrawVO,UserVO userVO,Boolean isUserId) throws IOException {
			final String methodName = "C2CWithdrawServiceImpl#validateUser";
			if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered :  From_category : "+c2cWithdrawVO.getToCategory()+" Domain: "+c2cWithdrawVO.getDomainCode()
			+" Network ID: "+userVO.getNetworkID()+" fromUserID= "+c2cWithdrawVO.getToUserId()+" toUserID= "+c2cWithdrawVO.getFromMsisdn());
		}
	

		Map<String, Object> requestObject = new HashMap();
		requestObject.put("data", c2cWithdrawVO);
		requestObject.put("networkId",userVO.getNetworkID());
		requestObject.put("admUserID", userVO.getUserID());
		if(isUserId)
		requestObject.put("msisdn",c2cWithdrawVO.getFromMsisdn());
		else
			requestObject.put("fromUserID", c2cWithdrawVO.getFromUserId());
		requestObject.put("isUserId", isUserId);

		String responseString = pretupsRestClient.postJSONRequest(requestObject, "VALIDATECHNNLUSER");

		PretupsResponse<C2CWithdrawVO> pretupsResponse = (PretupsResponse<C2CWithdrawVO>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<C2CWithdrawVO>>() {
				});
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting with pretupsResponse: " + pretupsResponse);
		}
				if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Exiting with list:");
                }
                return pretupsResponse;

	}
		
		/**
		 *       * Method confirmWithdraw to calculate tax
		 *               * @return Map<String,String> from DB
		 *                       * @throws IOException
		 *                               */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<ChannelTransferItemsVO> confirmWithdraw(C2CWithdrawVO withdrawVO,UserVO userVO) throws IOException {	
		final String methodName = "C2CWithdrawServiceImpl#confirmWithdraw";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered :  networkId : "+userVO.getNetworkID()+" fromUserID : "+withdrawVO.getSenderVO().getUserID()
					+" toUserID : "+withdrawVO.getReceiverVO().getUserID()+" AdmUserID: "+userVO.getUserID());
		}
		Map<String, Object> requestObject = new HashMap();
		requestObject.put("data",withdrawVO );
		requestObject.put("networkId",userVO.getNetworkID());
		requestObject.put("admUserID", userVO.getUserID());
		String responseString = pretupsRestClient.postJSONRequest(requestObject, "CONFIRMUSER");
		PretupsResponse<ChannelTransferItemsVO> pretupsResponse = (PretupsResponse<ChannelTransferItemsVO>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<ChannelTransferItemsVO>>() {
				});
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting with pretupsResponse: " + pretupsResponse);
		}
		if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with list:"+pretupsResponse);
        }
        return pretupsResponse;

	}
	
	/**
	 *       * Perform C2CWithdraw
	 *               * @return ChannelTransferVO from DB
	 *                       * @throws BTSLBaseException
	 *                               */
	
	@SuppressWarnings("unchecked")
	public ChannelTransferVO withdraw(C2CWithdrawVO withdrawVO,UserVO userVO,ChannelTransferItemsVO itemsVO) throws Exception {
		
		final String methodName = "C2CWithdrawServiceImpl#Withdraw";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered with toUserID: " + withdrawVO.getReceiverVO().getUserID()+" admUserID: "+userVO.getUserID()+
					" fromUserId: "+withdrawVO.getSenderVO().getUserID());
			_log.debug(methodName, "Entered Map:" + itemsVO);
		}
		Map<String, Object> requestObject = new HashMap();
		requestObject.put("networkId", userVO.getNetworkID());
		requestObject.put("admUserID", userVO.getUserID());
		requestObject.put("data", withdrawVO);
		requestObject.put("map", itemsVO);
		String responseString = pretupsRestClient.postJSONRequest(requestObject, "CONFIRMWITHDRAW");
		PretupsResponse<ChannelTransferVO> pretupsResponse = (PretupsResponse<ChannelTransferVO>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<ChannelTransferVO>>() {
				});
		ChannelTransferVO channelTransferVO = pretupsResponse.getDataObject();

        if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with list:"+channelTransferVO);
        }
        return channelTransferVO;	
		
	}
	
}