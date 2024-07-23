/**

 */
package com.client.pretups.channel.user.service;

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
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;


@Service("userTransferService")
public class UserTransferServiceImpl implements UserTransferService{

       public static final Log _log = LogFactory.getLog(UserTransferServiceImpl.class.getName());



@Autowired
private PretupsRestClient pretupsRestClient;
/**
*       * Load Domain for Channel User
*               * @return List The list of lookup filtered from DB
*                       * @throws IOException, Exception
*                               */

/**
 * UserTransferServiceImpl.java
 * @return
 * @throws BTSLBaseException
 * @throws Exception
 * List<ListValueVO>
 * akanksha.gupta
 * 01-Sep-2016 3:27:54 pm
 */
@Override
public List<ListValueVO> loadDomain() throws BTSLBaseException, IOException  {
        final String methodName = "loadDomains";
        if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered ");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("excludeUserType", PretupsI.DOMAIN_TYPE_CODE);

        Map<String, Object> object = new HashMap<>();
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
 * UserTransferService.java
 * @return
 * @throws BTSLBaseException
 * @throws Exception
 * List<ListValueVO>
 * akanksha.gupta
 * 01-Sep-2016 3:27:57 pm
 */
@Override
public List<ListValueVO> loadCategory() throws BTSLBaseException,IOException  {
        final String methodName = "loadCategory";

        if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered ");
        }

                        Map<String, Object> data = new HashMap<>();


        Map<String, Object> object = new HashMap<>();
        object.put("data", data);
        String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.USERTRFCAT);

                        PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {});

                        List<ListValueVO> list =  response.getDataObject();
                        processListValueVOValue(list , "OPT");


        if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
        }
        return list;
}

/**
 * UserTransferServiceImpl.java
 * @param p_domain
 * @param p_category
 * @param p_geography
 * @param p_loggedInUserID
 * @return
 * @throws BTSLBaseException
 * @throws Exception
 * List<UserVO>
 * akanksha.gupta
 * 01-Sep-2016 3:44:22 pm
 */
@Override
public List<ListValueVO> loadUserData(String domain , String category , String geography,String user,String loggedInUserID) throws IOException   {
	final String methodName = "loadUserData";
    	String responseString ;
		if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered : domain : "+domain+" category : "+category+" geography : "+geography+"p_user :"+user+"loggedInUserID:"+loggedInUserID);
        }
		Map<String, Object> data = new HashMap<>();
		data.put("domainCode", domain);
		data.put("userCat" , category);
        data.put("zoneCode", geography);
		data.put("ownerName","%"+user+"%");
		data.put("loggedInUserID", loggedInUserID);
		

        Map<String, Object> object = new HashMap<>();
        object.put("data", data);

        
		responseString = pretupsRestClient.postJSONRequest(object,"LOADUSRLISTTRF");
		PretupsResponse<List<UserVO>> response = (PretupsResponse<List<UserVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<UserVO>>>() {});

        List<UserVO> list =  response.getDataObject();
        List<ListValueVO> dropDown = new ArrayList<>();
         if(list != null && !list.isEmpty())
         {
        	 /*list.forEach(userVO -> {
			ListValueVO listVO = new ListValueVO(userVO.getUserName(),userVO.getUserID()+":"+userVO.getUserName());
			dropDown.add(listVO);
		});*/
        	 UserVO userVO = null;	
        	 ListValueVO listVO = null;
        	 for (int i = 0, j = list.size(); i < j; i++) {
        		 userVO = (UserVO) list.get(i);
        		 listVO = new ListValueVO(userVO.getUserName(),userVO.getUserID()+":"+userVO.getUserName());
        		 dropDown.add(listVO);
        	 } 	 
         }
        	 
        	 
        if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with list:"+dropDown);
        }
        return dropDown;	     
}


        /**
         * UserTransferServiceImpl.java
         * @param listObject
         * @param type
         * @throws Exception
         * void
         * akanksha.gupta
         * 01-Sep-2016 3:44:32 pm
         */
@Override
        public <T> void processListValueVOValue(List<T> listObject, String type) {
                ListValueVO listValueVO ;
                if (listObject != null && !listObject.isEmpty()) {
                        for (int i = 0, j = listObject.size(); i < j; i++) {
                                listValueVO = (ListValueVO) listObject.get(i);
                                if ((listValueVO.getValue().split(":")[0]).equals(type)) {
                                        listObject.remove(i);
                                        i--;
                                        j--;
                                }
                        }
                }
        }



/**
 * UserTransferServiceImpl.java
 * @param userIDorMsisdn
 * @param isUserID
 * @param userVO
 * @return
 * @throws BTSLBaseException
 * @throws Exception
 * PretupsResponse<ChannelUserVO>
 * akanksha.gupta
 * 01-Sep-2016 4:31:43 pm
 */
public PretupsResponse<ChannelUserVO> confirmUserDetails(String userIDorMsisdn,Boolean isUserCode,UserVO userVO) throws BTSLBaseException, IOException  {
final String methodName = "confirmUserDetails";
String responseString ;
		if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered userIDorMsisdn :"+userIDorMsisdn+"isUserID:"+isUserCode+"userVO:"+userVO);
        }
		Map<String, Object> data = new HashMap<>();
		if(!isUserCode)
			data.put("userID",userIDorMsisdn);
		 else
			 data.put("msisdn",userIDorMsisdn);
		
		
		data.put("isUserCode",isUserCode);
		data.put("loggedInUserID",userVO.getUserID());
		data.put("domainID",userVO.getDomainID());
		data.put("loggedInUserMsisdn",userVO.getMsisdn());
		data.put("loggedInUserName",userVO.getUserName());
		Map<String, Object> object = new HashMap<>();
       	 object.put("data", data);
		responseString = pretupsRestClient.postJSONRequest(object,"LOADUSRDETAILSTRF");
		PretupsResponse<ChannelUserVO> response = (PretupsResponse<ChannelUserVO>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<ChannelUserVO>>() {});

		
		


        if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
        }
        
        return response;
         
}
			

/**
 * UserTransferServiceImpl.java
 * @param p_category
 * @param ownerId
 * @return
 * @throws BTSLBaseException
 * @throws Exception
 * List<UserVO>
 * akanksha.gupta
 * 01-Sep-2016 3:44:45 pm
 */
public List<ListValueVO> loadChannelUserData(String category , String ownerId,String user) throws  IOException   {
	final String methodName = "loadChannelUserData";

	String responseString ;
	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Entered : category : "+category+" ownerId : "+ownerId+"p_user"+user);
	}
	Map<String, Object> data = new HashMap<>();
	data.put("userCat" , category);
	data.put("userName","%"+user+"%");
	data.put("ownerId",ownerId);
	Map<String, Object> object = new HashMap<>();
	object.put("data", data);


	responseString = pretupsRestClient.postJSONRequest(object,"LOADCHNNLUSRLISTTRF");
	PretupsResponse<List<UserVO>> response = (PretupsResponse<List<UserVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<UserVO>>>() {});

	List<UserVO> list = (List<UserVO>) response.getDataObject();
	  List<ListValueVO> dropDown = new ArrayList<>();
	  if(list != null && !list.isEmpty())
	  {
      /*list.forEach(userVO -> {
			ListValueVO listVO = new ListValueVO(userVO.getUserName(),userVO.getUserID()+":"+userVO.getUserName());
			dropDown.add(listVO);
		});*/
		  UserVO userVO = null;	
     	 ListValueVO listVO = null;
     	 for (int i = 0, j = list.size(); i < j; i++) {
     		 userVO = (UserVO) list.get(i);
     		 listVO = new ListValueVO(userVO.getUserName(),userVO.getUserID()+":"+userVO.getUserName());
     		 dropDown.add(listVO);
     	 } 	 
	  }
	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Exiting with list:"+list);
	}
	return dropDown;
}




/**
 * UserTransferServiceImpl.java
 * @param p_domain
 * @param p_category
 * @param p_geography
 * @param p_loggedInUserID
 * @return
 * @throws BTSLBaseException
 * @throws Exception
 * List<ChannelUserTransferVO>
 * akanksha.gupta
 * 01-Sep-2016 3:44:58 pm
 */
@Override
public List<ChannelUserTransferVO> loadInitiatedUserTransfererList(String domain, String category, String geography,
		String loggedInUserID) throws BTSLBaseException, IOException {

	final String methodName = "loadInitiatedUserTransfererList";
		String responseString ;
		if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered : domain : "+domain+" category : "+category+" geography : "+geography+"p_loggedInUserID:"+loggedInUserID);
        }
		Map<String, Object> data = new HashMap<>();
		data.put("domainCode", domain);
		data.put("userCat" , category);
        data.put("zoneCode", geography);
		data.put("loggedInUserID", loggedInUserID);
		

        Map<String, Object> object = new HashMap<>();
        object.put("data", data);

        
		responseString = pretupsRestClient.postJSONRequest(object,"LOADINITUSRTRFLIST");
		PretupsResponse<List<ChannelUserTransferVO>> response = (PretupsResponse<List<ChannelUserTransferVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ChannelUserTransferVO>>>() {});

        List<ChannelUserTransferVO> list = (List<ChannelUserTransferVO>) response.getDataObject();


        if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with list:"+list);
        }
        return list;	     

}



/**
 * UserTransferServiceImpl.java
 * @param p_msisdn
 * @param p_loggedInUserID
 * @return
 * @throws BTSLBaseException
 * @throws Exception
 * List<ChannelUserTransferVO>
 * akanksha.gupta
 * 01-Sep-2016 3:45:05 pm
 */
@Override
public List<ChannelUserTransferVO> loadInitiatedUserTransferDetailMsisdn(String msisdn,String loggedInUserID) throws BTSLBaseException, IOException {

	final String methodName = "loadInitiatedUserTransfererList";
	String responseString;
	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Entered : p_msisdn : "+msisdn+"p_loggedInUserID:"+loggedInUserID);
	}
	Map<String, Object> data = new HashMap<>();
	data.put("msisdn", msisdn);
	data.put("loggedInUserID", loggedInUserID);


	Map<String, Object> object = new HashMap<>();
	object.put("data", data);


	responseString = pretupsRestClient.postJSONRequest(object,"LOADINITUSRBYMSISDN");
	PretupsResponse<List<ChannelUserTransferVO>> response = (PretupsResponse<List<ChannelUserTransferVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ChannelUserTransferVO>>>() {});

	List<ChannelUserTransferVO> list = (List<ChannelUserTransferVO>) response.getDataObject();



	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Exiting with list:"+list);
	}
	return list;	     

}


/**
 * UserTransferServiceImpl.java
 * @param userID
 * @param loggedinUserID
 * @param otp
 * @param _categoryCode
 * @return
 * @throws BTSLBaseException
 * @throws Exception
 * PretupsResponse<Object>
 * akanksha.gupta
 * 01-Sep-2016 3:45:16 pm
 */
@Override
public PretupsResponse<Object> confirmUserTransfer(String userID, String loggedinUserID,String otp,String categoryCode)
		throws BTSLBaseException,IOException {
	final String methodName = "confirmUserTransfer";
	String responseString;
	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Entered");
	}
	Map<String, Object> data = new HashMap<>();
	data.put("userID",userID);
	data.put("otp",otp);
	data.put("loggedInUserID",loggedinUserID);
	data.put("categorycode",categoryCode);
	Map<String, Object> object = new HashMap<>();
	object.put("data", data);
	responseString = pretupsRestClient.postJSONRequest(object,"CONFIRMUSRTRANSFER");
	PretupsResponse<Object> response = (PretupsResponse<Object>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<Object>>() {});


	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Exiting ");
	}

	return response;

}

	
}
