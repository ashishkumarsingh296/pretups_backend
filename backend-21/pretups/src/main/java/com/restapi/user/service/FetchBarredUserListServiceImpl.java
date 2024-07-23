package com.restapi.user.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.pretups.master.businesslogic.SubLookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

/**
 * @author deepa.shyam
 *
 */
@Service
public class FetchBarredUserListServiceImpl implements FetchBaredUserListService{
	protected final Log log = LogFactory.getLog(getClass().getName());
	@Override
	public FetchBarredListResponseVO viewBarredList(FetchBarredListRequestVO fetcBarredListRequestVO,UserVO userVO) throws BTSLBaseException{
		FetchBarredListResponseVO fetchBarredListResponseVO = new FetchBarredListResponseVO();

        final String METHOD_NAME = "viewBarredList";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered FetchBarredListRequestVO" + fetcBarredListRequestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        ArrayList<BarredVo> barredUserList = null;
        BarredVo barredUserVO = null;
        UserWebDAO userwebDao = null;
        ChannelUserVO channelUserVO = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        try {
            channelUserWebDAO = new ChannelUserWebDAO();
            userwebDao = new UserWebDAO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            BarredUserDAO barreduserDAO = new BarredUserDAO();
            barredUserVO = new BarredVo();
            barredUserVO.setModule(fetcBarredListRequestVO.getModule());
            barredUserVO.setNetworkCode(userVO.getNetworkID());
			Locale locale = null;
	        if (userVO.getUserPhoneVO()!= null) {
	            locale = new Locale(userVO.getUserPhoneVO().getPhoneLanguage(), userVO.getUserPhoneVO().getCountry());
	        } else {
	            locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
	        }
	        
	        ErrorMap errorMap = new ErrorMap();
	        //validate request
            validateFetchUserListRequest(con, fetcBarredListRequestVO, errorMap, userVO,barredUserVO,locale);
            if(!BTSLUtil.isNullObject(errorMap.getMasterErrorList()) || !BTSLUtil.isNullObject(errorMap.getRowErrorMsgLists()))
			{
				fetchBarredListResponseVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				fetchBarredListResponseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REQ_NOT_PROCESS,null));
				fetchBarredListResponseVO.setStatus("400");
				fetchBarredListResponseVO.setErrorMap(errorMap);
				return fetchBarredListResponseVO;
			}

            HashMap<String,ArrayList<BarredVo>> filterMap= new HashMap<String,ArrayList<BarredVo>>();
            ArrayList<BarredVo> list = null;
            if(!userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
            //get barred user list
            barredUserList = barreduserDAO.fetchBarredUserList(con, barredUserVO);
            
            String userID = null;
            if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(userVO.getCategoryVO().getHierarchyAllowed())) {
                userID = userVO.getParentID();
            } else {
                userID = userVO.getUserID();
            }
            ArrayList<ListValueVO> childUserList = null;
         // for getting all staff user list
            childUserList = userwebDao.loadUserListByLogin(con, userVO.getUserID(), PretupsI.STAFF_USER_TYPE, "%"); 
         
         // for getting all channel user list
            ArrayList<ChannelUserVO> hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, userID, false);
            for (int i =0;i<barredUserList.size();i++) {
            	BarredVo barUserVO = barredUserList.get(i);
                // added for the viewing details for channel user
                if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE) && !(userVO.getMsisdn().equals(fetcBarredListRequestVO.getMsisdn()))) {
                    boolean isExist = false;
                    String filteredMsisdn = null;
                    // checking that current user is authorised to see the list
                    // items
                    if (!BTSLUtil.isNullString(fetcBarredListRequestVO.getMsisdn()) || !BTSLUtil.isNullString(barUserVO.getMsisdn())) {
                        // Change ID=ACCOUNTID
                        // FilteredMSISDN is replaced by
                        // getFilteredIdentificationNumber
                        // This is done because this field can contains msisdn
                        // or account id
                        if (BTSLUtil.isNullString(fetcBarredListRequestVO.getMsisdn())) {
                            filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(barUserVO.getMsisdn());
                        } else {
                            filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(fetcBarredListRequestVO.getMsisdn());
                        }
                        if (hierarchyList.size() > 0) {
                            // check in the child user list
                        	int hierarchyListSize = hierarchyList.size();
                            for (int j = 0; j < hierarchyListSize; j++) {
                                channelUserVO = (ChannelUserVO) hierarchyList.get(j);
                                if (channelUserVO.getMsisdn().equals(filteredMsisdn)) {
                                    isExist = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!isExist) {
                        // check in the staff user list
                    	int childUserListSize= childUserList.size();
                        for (int j = 0; j < childUserListSize; j++) {
                            ListValueVO childUser = (ListValueVO) childUserList.get(j);
                            // checking whether mobile no or login id of child
                            // user is same with form data
                            if (childUser.getLabel().equals(filteredMsisdn) || filteredMsisdn.equals(childUser.getOtherInfo2())) {
                                isExist = true;
                                break;
                            }
                        }
                    }
                    if(!isExist){
                    	barredUserList.remove(i);
                    	i--;
                    }
                }
            } 
            for (BarredVo barredVO: barredUserList) {
            	list= new ArrayList<>();
            	if(filterMap.containsKey(barredVO.getLoginId())){
            		list= filterMap.get(barredVO.getLoginId());
            	}
            	list.add(barredVO);
            	filterMap.put(barredVO.getLoginId(), list);
            }
            }else {
            	barredUserList = barreduserDAO.fetchBarredUserListAdmin(con, barredUserVO);
            	 for (BarredVo barredVO: barredUserList) {
                 	list= new ArrayList<>();
                 	if(filterMap.containsKey(barredVO.getMsisdn())){
                 		list= filterMap.get(barredVO.getMsisdn());
                 	}
                 	list.add(barredVO);
                 	filterMap.put(barredVO.getMsisdn(), list);
                 }
            }
        
            if (barredUserList != null && barredUserList.size() > 0) {
            	fetchBarredListResponseVO.setBarredList(filterMap);
            	fetchBarredListResponseVO.setMessageCode( PretupsErrorCodesI.FETCH_BAR_USER_LIST_SUCC);
            	fetchBarredListResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
      			String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.FETCH_BAR_USER_LIST_SUCC, null);
      			fetchBarredListResponseVO.setMessage(resmsg);
            } else {
            	fetchBarredListResponseVO.setMessageCode( PretupsErrorCodesI.INVALID_BARRED_USER);
            	fetchBarredListResponseVO.setStatus("400");
    			String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.INVALID_BARRED_USER, null);
    			fetchBarredListResponseVO.setMessage(resmsg);
             }
        } catch (BTSLBaseException be) {
	        log.errorTrace(METHOD_NAME, be);
	        log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
	        throw be;
	    } catch (Exception e) {
	        log.error(METHOD_NAME, "Exception " + e.getMessage());
	        log.errorTrace(METHOD_NAME, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UnBarUserHandler[unBarredUser]", "", "", "", "Exception:" + e.getMessage());
	        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.REQ_NOT_PROCESS);
	    }
        finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("FetchBarredUserServiceImpl#viewBarredList");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(METHOD_NAME, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(METHOD_NAME, e);
			}
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting");
            }
        }
    
		return fetchBarredListResponseVO;
	}

	
	public void validateFetchUserListRequest(Connection con,FetchBarredListRequestVO fetBarredListRequestVO,ErrorMap errorMap,UserVO userVO,BarredVo barredUserVO,Locale locale) throws BTSLBaseException{
        final String METHOD_NAME = "vaidateFetchUserListRequest";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered FetchBarredListRequestVO" + fetBarredListRequestVO);
        }
        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
		ArrayList<RowErrorMsgLists> rowErrorMsgLists = new ArrayList<RowErrorMsgLists>();
        String arr[] = null;
        Boolean error = false;

        try {
            ListValueVO listValueVO = null;
            //do validation based on 
            //1.MSISDN OR USERNAME
            //2.filter
            
            if(!BTSLUtil.isNullString(fetBarredListRequestVO.getMsisdn()) || !BTSLUtil.isNullString(fetBarredListRequestVO.getUserName())){
            	 // getting the msisdn of the channel user
	            String msisdn = fetBarredListRequestVO.getMsisdn();
	            if (!BTSLUtil.isNullString(msisdn)) {
	            	// filtering the msisdn for country independent dial format
		            String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
		            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
		            	error = true;
		    			MasterErrorList masterErrorList = new MasterErrorList();
		    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_BAR_INVALID_MSISDN, new String[]{ msisdn});
		    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_BAR_INVALID_MSISDN);
		    			masterErrorList.setErrorMsg(msg);
		    			masterErrorListMain.add(masterErrorList);
		            }else{
			            String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
			            // checking whether the msisdn prefix is valid in the network
			            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			            if (networkPrefixVO == null) {
			                arr = new String[] { filteredMsisdn };
			                error = true;
			    			MasterErrorList masterErrorList = new MasterErrorList();
			    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_UNSUPPORTED_NETWORK, arr);
			    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_UNSUPPORTED_NETWORK);
			    			masterErrorList.setErrorMsg(msg);
			    			masterErrorListMain.add(masterErrorList);
			            }else{
				            String networkCode = networkPrefixVO.getNetworkCode();
				            if (networkCode != null && !networkCode.equals(userVO.getNetworkID())) {
				            	error = true;
				    			MasterErrorList masterErrorList = new MasterErrorList();
				    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_NETWORK_NOT_MATCHING_REQUEST, null);
				    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_NETWORK_NOT_MATCHING_REQUEST);
				    			masterErrorList.setErrorMsg(msg);
				    			masterErrorListMain.add(masterErrorList);
				            }else
				            	barredUserVO.setMsisdn(filteredMsisdn);
			            }
		            }
	            }else{
	            	String userName= fetBarredListRequestVO.getUserName();
	    			if(!new ChannelUserDAO().isUserExistByUserName(con, userName, userVO.getNetworkID())){
	    				error = true;
		    			MasterErrorList masterErrorList = new MasterErrorList();
		    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_NOT_FOUND_BARREDLIST, null);
		    			masterErrorList.setErrorCode(PretupsErrorCodesI.USER_NOT_FOUND_BARREDLIST);
		    			masterErrorList.setErrorMsg(msg);
		    			masterErrorListMain.add(masterErrorList);
	    			}else
	    				barredUserVO.setName(userName);
	            }
            }else{
            	 if (BTSLUtil.isNullString(fetBarredListRequestVO.getModule())) {
 	            	error = true;
 	    			MasterErrorList masterErrorList = new MasterErrorList();
 	    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, new String[]{ "module"});
 	    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
 	    			masterErrorList.setErrorMsg(msg);
 	    			masterErrorListMain.add(masterErrorList);
 	            }
 	            else{
 	            // getting the module ie whether the request is from C2S or P2P
 	               // module.the request will be handled accordingly
 	           	   if (BTSLUtil.isNullString(fetBarredListRequestVO.getModule())) {
 	   	            	error = true;
 	   	    			MasterErrorList masterErrorList = new MasterErrorList();
 	   	    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, new String[]{ "module"});
 	   	    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
 	   	    			masterErrorList.setErrorMsg(msg);
 	   	    			masterErrorListMain.add(masterErrorList);
 	   	            }
 	   	            else{
 	   	            	// the module value should be either C2S or P2P else throw error
 	   	            	ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
 	   	            	if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
 	   	                    if (moduleList != null && moduleList.size() > 0) {
 	   	                        for (int i = 0, j = moduleList.size(); i < j; i++) {
 	   	                            listValueVO = (ListValueVO) moduleList.get(i);
 	   	                            if (!listValueVO.getValue().equals(PretupsI.C2S_MODULE)) {
 	   	                                moduleList.remove(i);
 	   	                                i--;
 	   	                                j--;
 	   	                            }
 	   	                        }
 	   	                    }
 	   	            	}
 	   		            if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(fetBarredListRequestVO.getModule(), moduleList).getValue())) {
 	   		            	error = true;
 	   		    			MasterErrorList masterErrorList = new MasterErrorList();
 	   		    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_INVALID_MODULE_VALUE, new String[]{fetBarredListRequestVO.getModule()});
 	   		    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_INVALID_MODULE_VALUE);
 	   		    			masterErrorList.setErrorMsg(msg);
 	   		    			masterErrorListMain.add(masterErrorList);
 	   		            }else
 	   	            	 barredUserVO.setModule(fetBarredListRequestVO.getModule());
 	   	            }
 	   	        
 	           	if (BTSLUtil.isNullString(fetBarredListRequestVO.getBarredAs())) {
	                arr = new String[] { "Barrred User As" };
	            	error = true;
	    			MasterErrorList masterErrorList = new MasterErrorList();
	    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, arr);
	    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
	    			masterErrorList.setErrorMsg(msg);
	    			masterErrorListMain.add(masterErrorList);
	            } else {
	                // checkin bar user type
	                ArrayList barredUserTypeList = LookupsCache.loadLookupDropDown(PretupsI.BARRED_USER_TYPE, true);
	                if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
	                	if (barredUserTypeList != null && barredUserTypeList.size() > 0) {
	                        for (int i = 0, j = barredUserTypeList.size(); i < j; i++) {
	                            listValueVO = (ListValueVO) barredUserTypeList.get(i);
	                            if (!listValueVO.getValue().equals(PretupsI.USER_TYPE_SENDER)) {
	                                barredUserTypeList.remove(i);
	                                i--;
	                                j--;
	                            }
	                        }
	                    }
	                }
	                if (!fetBarredListRequestVO.getBarredAs().equalsIgnoreCase(PretupsI.ALL) && BTSLUtil.isNullString(BTSLUtil.getOptionDesc(fetBarredListRequestVO.getBarredAs(), barredUserTypeList).getValue())) {
	                	error = true;
		    			MasterErrorList masterErrorList = new MasterErrorList();
		    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_INVALID_BARUSERTYPE_VALUE, null);
		    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_INVALID_BARUSERTYPE_VALUE);
		    			masterErrorList.setErrorMsg(msg);
		    			masterErrorListMain.add(masterErrorList);
	                }else
	                	barredUserVO.setBarredAs(fetBarredListRequestVO.getBarredAs());
	            }
	            
            	 if(BTSLUtil.isNullString(fetBarredListRequestVO.getUserType())){
            		arr = new String[] { "User Type" };
 	            	error = true;
 	    			MasterErrorList masterErrorList = new MasterErrorList();
 	    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, arr);
 	    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
 	    			masterErrorList.setErrorMsg(msg);
 	    			masterErrorListMain.add(masterErrorList);
            	 }else
            		 barredUserVO.setUserType(fetBarredListRequestVO.getUserType());
            	 
            	 
            	 if (!BTSLUtil.isNullString(fetBarredListRequestVO.getBarredtype())) {
                     String barType[] = fetBarredListRequestVO.getBarredtype().split(":");
                     barredUserVO.setBarredType(barType[1]);
                 }	
	            if(!barredUserVO.getBarredType().equals(PretupsI.ALL)){
	                SubLookUpVO subLookUpVO = null;
	                ArrayList<SubLookUpVO> barredTypeList = null;
	                if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
	                	barredTypeList= SubLookupsCache.getsubLookUpList(PretupsI.BARRING_TYPE, PretupsI.CHANNLE_USER_BARTYPE_LOOKUP_CODE);
	                }else{
	                	barredTypeList= SubLookupsCache.getsubLookUpList(PretupsI.BARRING_TYPE, PretupsI.ALL);
	                }
	                for (int i = 0, j = barredTypeList.size(); i < j; i++) {
	                    subLookUpVO = (SubLookUpVO) barredTypeList.get(i);
	                    if (subLookUpVO.getLookupCode().equals(PretupsI.P2P_BARTYPE_LOOKUP_CODE)) {
	                        subLookUpVO.setSubLookupCode(PretupsI.P2P_MODULE + ":" + subLookUpVO.getSubCode());
	                    } else {
	                        subLookUpVO.setSubLookupCode(PretupsI.C2S_MODULE + ":" + subLookUpVO.getSubCode());
	                    }
	                }
	                if (barredTypeList == null || barredTypeList.isEmpty()) {
	                	error = true;
	        			MasterErrorList masterErrorList = new MasterErrorList();
	        			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_INVALID_BARTYPELIST_EMPTY, null);
	        			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_INVALID_BARTYPELIST_EMPTY);
	        			masterErrorList.setErrorMsg(msg);
	        			masterErrorListMain.add(masterErrorList);
	                }
	                
	                if (BTSLUtil.isNullString(fetBarredListRequestVO.getBarredtype())) {
		            	error = true;
		    			MasterErrorList masterErrorList = new MasterErrorList();
		    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, new String[]{"Barring Type"});
		    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
		    			masterErrorList.setErrorMsg(msg);
		    			masterErrorListMain.add(masterErrorList);
		            }
		            else {
			                boolean flag = true;
			                for (int i = 0, j = barredTypeList.size(); i < j; i++) {
			                    subLookUpVO = (SubLookUpVO) barredTypeList.get(i);
			                    if (subLookUpVO.getSubLookupCode().equals(fetBarredListRequestVO.getBarredtype())) {
			                    	 flag = false;
			                    	 break;
			                    }
			                }
		                if (flag) {
		                    error = true;
			    			MasterErrorList masterErrorList = new MasterErrorList();
			    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_INVALID_BARTYPE_VALUE, new String[]{ fetBarredListRequestVO.getBarredtype()});
			    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_INVALID_BARTYPE_VALUE);
			    			masterErrorList.setErrorMsg(msg);
			    			masterErrorListMain.add(masterErrorList);
		                }
		          	}
	            }
                if (BTSLUtil.isNullString(fetBarredListRequestVO.getFromDate())) {
	            	error = true;
	    			MasterErrorList masterErrorList = new MasterErrorList();
	    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, new String[]{"from Date"});
	    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
	    			masterErrorList.setErrorMsg(msg);
	    			masterErrorListMain.add(masterErrorList);
	            }else if (BTSLUtil.isNullString(fetBarredListRequestVO.getTodate())) {
	            	error = true;
	    			MasterErrorList masterErrorList = new MasterErrorList();
	    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, new String[]{"From Date"});
	    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
	    			masterErrorList.setErrorMsg(msg);
	    			masterErrorListMain.add(masterErrorList);
	            }else{
                 barredUserVO.setToDate(fetBarredListRequestVO.getTodate());
                 barredUserVO.setFromDate(fetBarredListRequestVO.getFromDate());
	            }
                
                if (BTSLUtil.isNullString(fetBarredListRequestVO.getCategory())) {
	            	error = true;
	    			MasterErrorList masterErrorList = new MasterErrorList();
	    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, new String[]{"from Date"});
	    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
	    			masterErrorList.setErrorMsg(msg);
	    			masterErrorListMain.add(masterErrorList);
	            }else
	            	barredUserVO.setCategoryCode(fetBarredListRequestVO.getCategory());
                
            }
            }
            
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "barredUserVO:" + barredUserVO);
        }

        if(error){
			  errorMap.setMasterErrorList(masterErrorListMain);
          }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error(METHOD_NAME, "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarUnbarUserServiceImpl[vaidateFetchUserListRequest]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("FetchBarredUserListServiceImpl", METHOD_NAME, PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting ");
        }
    }

}
