package com.restapi.user.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.pretups.master.businesslogic.SubLookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

/**
 * @author deepa.shyam
 *
 */
/**
 * @author deepa.shyam
 *
 */
@Service
public class BarUnbarUserServiceImpl implements BarUnbarUserService {
	protected final Log log = LogFactory.getLog(getClass().getName());
	@Override
	 public BarUnbarResponseVO addBarredUser(BarUnbarRequestVO barUnbarRequestVO,UserVO userVO) throws BTSLBaseException{
        final String METHOD_NAME = "addBarredUser";
        if (log.isDebugEnabled()) {
            log.debug("addBarredUser", "Entered BarUnbarRequestVO" + barUnbarRequestVO);
        }
        Connection con = null;
        MComConnection mcomCon = null;
        String serviceType = PretupsI.CHANNLE_USER_BAR;
        BarUnbarResponseVO barUnbarResponseVO = new BarUnbarResponseVO();
        try {
        	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
            BarredUserDAO barredUserDAO = new BarredUserDAO();
            String networkCode = userVO.getNetworkID();
            Date currentDate = new Date(System.currentTimeMillis());
            Bar barUnbarRequestData = barUnbarRequestVO.getBar().get(0);
            BarredUserVO barredUserVO = new BarredUserVO();
            barredUserVO.setCreatedOn(currentDate);
            barredUserVO.setModifiedOn(currentDate);
            barredUserVO.setCreatedBy(userVO.getUserID());
            barredUserVO.setModifiedBy(userVO.getUserID());
            barredUserVO.setModule(barUnbarRequestVO.getModule());
            barredUserVO.setNetworkCode(networkCode);
            barredUserVO.setMsisdn(barUnbarRequestVO.getMsisdn());
            barredUserVO.setBarredReason(barUnbarRequestData.getBarringReason());
            barredUserVO.setUserType(barUnbarRequestVO.getUserType());
           
            userVO.setUserPhoneVO(new UserDAO().loadUserPhoneVO(con, userVO.getUserID()));
			Locale locale = null;
			if (userVO.getUserPhoneVO()!=null && userVO.getUserPhoneVO().getLocale() == null) {
	            locale = new Locale(userVO.getUserPhoneVO().getPhoneLanguage(), userVO.getUserPhoneVO().getCountry());
	        } 
	        else {
	            locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
	        }
			
			
			String msisdnPrefix = PretupsBL.getMSISDNPrefix(barredUserVO.getMsisdn());
			if (((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR))).booleanValue()) {
				NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
				if (networkPrefixVO != null) {
					if (userVO.getNetworkID().equalsIgnoreCase(networkPrefixVO.getNetworkCode())) {
						barredUserVO.setNetworkCode(networkPrefixVO.getNetworkCode());

					} else {
						throw new BTSLBaseException(this, "BarredUser", PretupsErrorCodesI.NOT_AUTHORIZED_TO_BAR);
					}
				}
			}

            ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            ErrorMap errorMap = new ErrorMap();
	        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();

            //validate request
            vaidateBarUserRequest(con, barUnbarRequestVO, errorMap, userVO);
            if(!BTSLUtil.isNullObject(errorMap.getMasterErrorList()) || !BTSLUtil.isNullObject(errorMap.getRowErrorMsgLists()))
			{
				barUnbarResponseVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				barUnbarResponseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REQ_NOT_PROCESS,null));
				barUnbarResponseVO.setStatus("400");
				//response.setStatus(HttpStatus.SC_BAD_REQUEST);
				barUnbarResponseVO.setService(serviceType + "RESP");
				barUnbarResponseVO.setErrorMap(errorMap);
				return barUnbarResponseVO;
			}
            String barType[] = barUnbarRequestData.getBarringType().split(":");
            barredUserVO.setBarredType(barType[1]);
            
             boolean staffUser = false;
			/* if(userVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)){
       		  channeluser= channelUserDAO.loadChannelUserDetails(con,userVO.getParentMsisdn());
       		 channeluser.setActiveUserID(userVO.getUserID());
       	 }*/
   		 if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE) && !(userVO.getMsisdn().equals(barredUserVO.getMsisdn()))) {
                boolean isExist = false;
                String userID = null;
                String filteredMsisdn = null;
                if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(userVO.getCategoryVO().getHierarchyAllowed())) {
                    userID = userVO.getParentID();
                } else {
                    userID = userVO.getUserID();
                }
                ArrayList childUserList = null;
                childUserList = new UserWebDAO().loadUserListByLogin(con, userVO.getUserID(), PretupsI.STAFF_USER_TYPE, "%"); 
                // for getting all staff user  list
                if (!(BTSLUtil.isNullString(userVO.getMsisdn()))) {
                    ArrayList hierarchyList = new ChannelUserWebDAO().loadChannelUserHierarchy(con, userID, false);
                    /*
                     * if(hierarchyList==null || hierarchyList.isEmpty())
                     * {
                     * if(_log.isDebugEnabled())
                     * _log.debug("confirm",
                     * "Logged in user has no child user so it can't bar any body"
                     * );
                     * throw new BTSLBaseException(this,"confirm",
                     * "subscriber.barreduser.msg.nohierarchy"
                     * ,"barredUser");
                     * }
                     */
                    ChannelUserVO channelUserVO = null;
                    filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(barredUserVO.getMsisdn());
                    if (hierarchyList.size() > 0) {
                        for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                            channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                            if (channelUserVO.getMsisdn().equals(filteredMsisdn)) {
                                isExist = true;
                                break;
                            }
                        }
                    }
                }
                if (!isExist) {
                    for (int i = 0; i < childUserList.size(); i++) {
                        // checking the msisdn of staff with the value
                        // entered on the form
                        ListValueVO childUser = (ListValueVO) childUserList.get(i);
                        if (barredUserVO.getMsisdn().equals(childUser.getOtherInfo2())) {
                            isExist = true;
                            staffUser=true;
                            break;
                        }
                    }
                }
                if (!isExist) {
                   barUnbarResponseVO.setMessageCode( PretupsErrorCodesI.USER_NOT_AUTH_TO_BAR);
                	barUnbarResponseVO.setStatus(String.valueOf(PretupsI.UNAUTHORIZED_ACCESS));
        			String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.USER_NOT_AUTH_TO_BAR, new String[] { barredUserVO.getMsisdn() });
        			barUnbarResponseVO.setMessage(resmsg);
        			return barUnbarResponseVO;
                }
   		 }

   		 //if user is already barred in system
   		 if (barredUserDAO.isExists(con, barredUserVO.getModule(), networkCode, barredUserVO.getMsisdn(), barredUserVO.getUserType(), barredUserVO.getBarredType())) {
    			MasterErrorList masterErrorList = new MasterErrorList();
    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHANNEL_USER_ALREADY_BARRRED, new String[]{barredUserVO.getBarredType()});
    			masterErrorList.setErrorCode(PretupsErrorCodesI.CHANNEL_USER_ALREADY_BARRRED);
    			masterErrorList.setErrorMsg(msg);
    			masterErrorListMain.add(masterErrorList);
    			errorMap.setMasterErrorList(masterErrorListMain);
    			barUnbarResponseVO.setErrorMap(errorMap);
    			barUnbarResponseVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				barUnbarResponseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REQ_NOT_PROCESS,null));
				barUnbarResponseVO.setStatus("400");
    			return barUnbarResponseVO;
            }
   		 
            
            int addCount = barredUserDAO.addBarredUser(con, barredUserVO);
            if (addCount > 0) {
            	ChannelUserVO channelUserVO = null;  
            	if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue())
            	{  
            		 if( barUnbarRequestVO.getModule().equalsIgnoreCase("C2S") && ( barUnbarRequestVO.getUserType().equalsIgnoreCase("SENDER") ||
            				 barUnbarRequestVO.getUserType().equalsIgnoreCase("RECEIVER")))
            	{
        		if(staffUser){
        			  channelUserVO= channelUserDAO.loadChannelUserDetails(con,userVO.getMsisdn());
        	       	  channelUserVO.setActiveUserID(userVO.getUserID());
        		}else{
        			channelUserVO = channelUserDAO.loadChannelUserDetails(con, barUnbarRequestVO.getMsisdn());
        		}
                if (channelUserVO != null) {
                    int insertBarCount = 0;
                    ArrayList<UserEventRemarksVO> barUnbarRemarks = null;
                    UserEventRemarksVO userRemarskVO = null;
                    barUnbarRemarks = new ArrayList<UserEventRemarksVO>();
                    userRemarskVO = new UserEventRemarksVO();
                    userRemarskVO.setCreatedBy(barredUserVO.getCreatedBy());
                    userRemarskVO.setCreatedOn(new Date());
                    userRemarskVO.setEventType(PretupsI.BARRING_USER_REMARKS);
                    userRemarskVO.setRemarks(barUnbarRequestData.getBarringReason());
                    userRemarskVO.setMsisdn(barUnbarRequestVO.getMsisdn());
                    userRemarskVO.setUserID(channelUserVO.getUserID());
                    userRemarskVO.setUserType(barUnbarRequestVO.getUserType());
                    userRemarskVO.setModule(PretupsI.C2S_MODULE);
                    barUnbarRemarks.add(userRemarskVO);
                    insertBarCount = new UserWebDAO().insertEventRemark(con, barUnbarRemarks);
                    if (insertBarCount <= 0) {
                    	mcomCon.finalRollback();
                        log.error(METHOD_NAME, "Error: while inserting into userEventRemarks Table");
                        //barUnbarResponseVO.setMessageCode("error.general.processing");
                     	barUnbarResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
             			//String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.CCE_XML_ERROR_BARRED_USER_NOTUPDATE, null);
             			barUnbarResponseVO.setMessage("error.general.processing");
                    }
                }
        	 }
            }
            mcomCon.finalCommit();

            
//            if ((PretupsI.USER_TYPE_SENDER.equals(barredUserVO.getUserType()) && PretupsI.C2S_MODULE.equals(barredUserVO.getModule())) || PretupsI.MSISDN_VALIDATION.equals(SystemPreferences.IDENTIFICATION_NUMBER_VAL_TYPE)) {
                try {
                	//Added for sending the notification language as per user assigned
                	BTSLMessages sendBtslMessage = null;
                	sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.BARRED);
                	PushMessage pushMessage = new PushMessage(barredUserVO.getMsisdn(), sendBtslMessage, "", "", locale, barredUserVO.getNetworkCode());
//                    PushMessage pushMessage = new PushMessage(barredUserVO.getMsisdn(), new BTSLMessages(PretupsErrorCodesI.CHANNEL_USER_BARRED), null, null, locale, barredUserVO.getNetworkCode(),"Related SMS will be delivered shortly",PretupsI.SERVICE_TYPE_EXT_CHNL_BARRED);
                    pushMessage.push();
                } catch (Exception e) {
                    log.error(METHOD_NAME, "Exception SENDING SMS: " + e.getMessage());
                    log.errorTrace(METHOD_NAME, e);
                    //throw new BTSLBaseException(this, "confirmDeleteBarredUser", "error.sendingsms", "startpage");
                }
//            }
            barUnbarResponseVO.setMessageCode( PretupsErrorCodesI.CHANNEL_USER_BAR_SUCC);
         	barUnbarResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
 			String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.CHANNEL_USER_BAR_SUCC, new String[]{ barredUserVO.getMsisdn(),barredUserVO.getBarredType()});
 			barUnbarResponseVO.setMessage(resmsg);
            } else {
                log.error(METHOD_NAME, "Error: while inserting into barred_msisdn Table");
     			mcomCon.finalRollback();
                barUnbarResponseVO.setMessageCode( PretupsErrorCodesI.ERROR_BARRED_USER_NOTUPDATE);
               	barUnbarResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
       			String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.ERROR_BARRED_USER_NOTUPDATE, null);
       			barUnbarResponseVO.setMessage(resmsg);
            }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("addBarredUser", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("addBarredUser", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarUserHandler[addBarredUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addBarredUser", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }finally{
        	try {
				if (mcomCon != null) {
					mcomCon.close("BarUnbarUserServiceImpl#addBarredUser");
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
                log.debug("addBarredUser", "Exiting  method=" + METHOD_NAME);
            }
        }
    	return barUnbarResponseVO;
        }
        

	@Override
	public BarUnbarResponseVO unBarredUser(BarUnbarRequestVO barUnbarRequestVO,UserVO userVO) throws BTSLBaseException {
        final String METHOD_NAME = "unBarredUser";
        if (log.isDebugEnabled()) {
            log.debug("unBarredUser", "Entered BarUnbarRequestVO" + barUnbarRequestVO);
        }
        String serviceType = PretupsI.CHANNLE_USER_BAR;
        Connection con = null;
        MComConnection mcomCon = null;
        BarUnbarResponseVO barUnbarResponseVO = new BarUnbarResponseVO();
        try {
        	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
            BarredUserDAO barredUserDAO = new BarredUserDAO();
            String networkCode = userVO.getNetworkID();
            Date currentDate = new Date(System.currentTimeMillis());
            BarredUserVO barredUserVO = new BarredUserVO();
            barredUserVO.setModifiedOn(currentDate);
            barredUserVO.setModifiedBy(userVO.getUserID());
            barredUserVO.setModule(barUnbarRequestVO.getModule());
            barredUserVO.setNetworkCode(networkCode);
            barredUserVO.setMsisdn(barUnbarRequestVO.getMsisdn());
            barredUserVO.setUserType(barUnbarRequestVO.getUserType());
            Locale locale = null;
            ErrorMap errorMap = new ErrorMap();
	        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
			ArrayList<RowErrorMsgLists> rowErrorMsgLists = new ArrayList<RowErrorMsgLists>();
			if (userVO.getUserPhoneVO()!=null && userVO.getUserPhoneVO().getLocale() == null) {
	            locale = new Locale(userVO.getUserPhoneVO().getPhoneLanguage(), userVO.getUserPhoneVO().getCountry());
	        } 
	        else {
	            locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
	        }
			

			String msisdnPrefix = PretupsBL.getMSISDNPrefix(barredUserVO.getMsisdn());
			if (((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR))).booleanValue()) {
				NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
				if (networkPrefixVO != null) {
					if (userVO.getNetworkID().equalsIgnoreCase(networkPrefixVO.getNetworkCode())) {
						barredUserVO.setNetworkCode(networkPrefixVO.getNetworkCode());

					} else {
						throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.NOT_AUTHORIZED_TO_BAR);
					}
				}
			}

	        //validate request
            vaidateBarUserRequest(con, barUnbarRequestVO, errorMap, userVO);
            if(!BTSLUtil.isNullObject(errorMap.getMasterErrorList()) || !BTSLUtil.isNullObject(errorMap.getRowErrorMsgLists()))
			{
				barUnbarResponseVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				barUnbarResponseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REQ_NOT_PROCESS,null));
				barUnbarResponseVO.setStatus("400");
				//response.setStatus(HttpStatus.SC_BAD_REQUEST);
				barUnbarResponseVO.setService(serviceType + "RESP");
				barUnbarResponseVO.setErrorMap(errorMap);
				return barUnbarResponseVO;
			}
            boolean staffUser= false;
            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE) && !(userVO.getMsisdn().equals(barredUserVO.getMsisdn()))) {
                boolean isExist = false;
                String userID = null;
                String filteredMsisdn = null;
                if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(userVO.getCategoryVO().getHierarchyAllowed())) {
                    userID = userVO.getParentID();
                } else {
                    userID = userVO.getUserID();
                }
                ArrayList childUserList = null;
                childUserList = new UserWebDAO().loadUserListByLogin(con, userVO.getUserID(), PretupsI.STAFF_USER_TYPE, "%"); 
                // for getting all staff user  list
                if (!(BTSLUtil.isNullString(userVO.getMsisdn()))) {
                    ArrayList hierarchyList = new ChannelUserWebDAO().loadChannelUserHierarchy(con, userID, false);
                    /*
                     * if(hierarchyList==null || hierarchyList.isEmpty())
                     * {
                     * if(_log.isDebugEnabled())
                     * _log.debug("confirm",
                     * "Logged in user has no child user so it can't bar any body"
                     * );
                     * throw new BTSLBaseException(this,"confirm",
                     * "subscriber.barreduser.msg.nohierarchy"
                     * ,"barredUser");
                     * }
                     */
                    ChannelUserVO channelUserVO = null;
                    filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(barredUserVO.getMsisdn());
                    if (hierarchyList.size() > 0) {
                        for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                            channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                            if (channelUserVO.getMsisdn().equals(filteredMsisdn)) {
                                isExist = true;
                                break;
                            }
                        }
                    }
                }
                if (!isExist) {
                    for (int i = 0; i < childUserList.size(); i++) {
                        // checking the msisdn of staff with the value
                        // entered on the form
                        ListValueVO childUser = (ListValueVO) childUserList.get(i);
                        if (barredUserVO.getMsisdn().equals(childUser.getOtherInfo2())) {
                            isExist = true;
                            staffUser=true;
                            break;
                        }
                    }
                }
                if (!isExist) {
                   barUnbarResponseVO.setMessageCode( PretupsErrorCodesI.USER_NOT_AUTH_TO_UNBAR);
                	barUnbarResponseVO.setStatus(String.valueOf(PretupsI.UNAUTHORIZED_ACCESS));
        			String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.USER_NOT_AUTH_TO_UNBAR, new String[] { barredUserVO.getMsisdn() });
        			barUnbarResponseVO.setMessage(resmsg);
        			return barUnbarResponseVO;
                }
   		    }
            if (!barredUserDAO.isExists(con, barUnbarRequestVO.getModule(), networkCode, barUnbarRequestVO.getMsisdn(), barUnbarRequestVO.getUserType(), null)) {
     			MasterErrorList masterErrorList = new MasterErrorList();
    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_BARRED_USER_NOTEXISTINLIST, null);
    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_BARRED_USER_NOTEXISTINLIST);
    			masterErrorList.setErrorMsg(msg);
    			masterErrorListMain.add(masterErrorList);
    			errorMap.setMasterErrorList(masterErrorListMain);
    			barUnbarResponseVO.setErrorMap(errorMap);
    			barUnbarResponseVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				barUnbarResponseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REQ_NOT_PROCESS,null));
				barUnbarResponseVO.setStatus("400");
    			return barUnbarResponseVO;
            }
            ArrayList<BarredUserVO> barredUserList = barredUserDAO.loadInfoOfBarredUser(con, barredUserVO);
            Boolean isPresent = false;
            int row =0;
            String barTypedefault[] = barUnbarRequestVO.getBar().get(0).getBarringType().split(":");
            barredUserVO.setBarredType(barTypedefault[1]);
            for(Bar bar : barUnbarRequestVO.getBar()){
                RowErrorMsgLists rowErrorMsgListss= new RowErrorMsgLists();
        		rowErrorMsgListss.setRowValue(String.valueOf(row));
        		rowErrorMsgListss.setRowName("Bar "+row);
        		row++;
        	    for(BarredUserVO barUserVo :barredUserList){
        	    	String barType[] = bar.getBarringType().split(":");
		       		if(barType[1].equals(barUserVo.getBarredType())){
		       			barUserVo.setMultiBox(PretupsI.YES);
		       			isPresent=true;
		       	}else
		       		barUserVo.setMultiBox(PretupsI.NO);
            }
    	  if (!isPresent) {
   			MasterErrorList masterErrorList = new MasterErrorList();
  			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_NOT_BARRED_USER_NOTEXISTINLIST, new String[]{barredUserVO.getBarredType()});
  			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_NOT_BARRED_USER_NOTEXISTINLIST);
  			masterErrorList.setErrorMsg(msg);
  			masterErrorListMain.add(masterErrorList);
  			rowErrorMsgListss.setMasterErrorList(masterErrorListMain);
			rowErrorMsgLists.add(rowErrorMsgListss);
          }else
        	  isPresent= false;
       	  }
            
            if(rowErrorMsgLists.size()!=0){
            	errorMap.setRowErrorMsgLists(rowErrorMsgLists);
    			barUnbarResponseVO.setErrorMap(errorMap);
    			barUnbarResponseVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				barUnbarResponseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REQ_NOT_PROCESS,null));
				barUnbarResponseVO.setStatus("400");
    			return barUnbarResponseVO;
            }
            
            
            int deleteCount = barredUserDAO.deleteFromBarredMsisdn(con, barredUserList);
            if (deleteCount > 0) {
            	//Added for sending the notification language as per user assigned
            	ChannelUserVO channelUserVO = null;
            	ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            	   UserEventRemarksVO userRemarskVO = null;
                   ArrayList<UserEventRemarksVO> barUnbarRemarks = null;
                   UserWebDAO userwebDAO = null;
            	if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue())
			    { 
				   if((barredUserVO.getUserType().equalsIgnoreCase("SENDER")|| barredUserVO.getUserType().equalsIgnoreCase("RECEIVER"))
						   && barredUserVO.getModule().equalsIgnoreCase("C2S"))
				   {
				   if(staffUser){
	        			channelUserVO= channelUserDAO.loadChannelUserDetails(con,userVO.getMsisdn());
	        	       	channelUserVO.setActiveUserID(userVO.getUserID());
	        		}else{
	        			channelUserVO = channelUserDAO.loadChannelUserDetails(con, barredUserVO.getMsisdn());
		        	}
                    barredUserVO.setCreatedBy(userVO.getActiveUserID());
                    barredUserVO.setCreatedOn(userVO.getCreatedOn());
                    if (channelUserVO != null) {
                        int insertCount = 0;
                        barUnbarRemarks = new ArrayList<UserEventRemarksVO>();
                        userRemarskVO = new UserEventRemarksVO();
                        userRemarskVO.setCreatedBy(barredUserVO.getCreatedBy());
                        userRemarskVO.setCreatedOn(new Date());
                        userRemarskVO.setEventType(PretupsI.UNBARRING_USER_REMARKS);
                        userRemarskVO.setRemarks(barredUserVO.getBarredReason());
                        userRemarskVO.setMsisdn(barredUserVO.getMsisdn());
                        userRemarskVO.setUserID(channelUserVO.getUserID());
                        userRemarskVO.setUserType(barredUserVO.getUserType());
                        userRemarskVO.setModule(PretupsI.C2S_MODULE);
                        barUnbarRemarks.add(userRemarskVO);
                        insertCount = new UserWebDAO().insertEventRemark(con, barUnbarRemarks);
                        if (insertCount <= 0) {
                            mcomCon.finalRollback();
                            log.error(METHOD_NAME, "Error: while inserting into userEventRemarks Table");
                            //barUnbarResponseVO.setMessageCode("error.general.processing");
                         	barUnbarResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
                 			//String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.CCE_XML_ERROR_BARRED_USER_NOTUPDATE, null);
                 			barUnbarResponseVO.setMessage("error.general.processing");
                 			return barUnbarResponseVO;
                        }
                    }
				   }
                }
            	mcomCon.finalCommit();
                try {
                    if ((PretupsI.USER_TYPE_SENDER.equals(barredUserVO.getUserType()) && PretupsI.C2S_MODULE.equals(barredUserVO.getModule())) || PretupsI.MSISDN_VALIDATION.equals(SystemPreferences.IDENTIFICATION_NUMBER_VAL_TYPE)) {
                    	//Added for sending the notification language as per user assigned
                    	UserPhoneVO userPhoneVO = null;
                    	if(channelUserVO == null && PretupsI.USER_TYPE_SENDER.equals(barredUserVO.getUserType())) {
                    		channelUserVO = channelUserDAO.loadChannelUserDetails(con, barredUserVO.getMsisdn());
                    		userPhoneVO = channelUserVO.getUserPhoneVO();                                
                    	}
                        PushMessage pushMessage = new PushMessage(barredUserVO.getMsisdn(), new BTSLMessages(PretupsErrorCodesI.CHANNEL_USER_UNBARRED), null, null, locale, barredUserVO.getNetworkCode(), "Related SMS will be delivered shortly", PretupsI.UNBARUSER);
                        pushMessage.push();
                    }
                } catch (Exception e) {
                    log.error(METHOD_NAME, "Exception SENDING SMS: " + e.getMessage());
                    log.errorTrace(METHOD_NAME, e);
                    //throw new BTSLBaseException(this, "confirmDeleteBarredUser", "error.sendingsms", "startpage");
                }
                barUnbarResponseVO.setMessageCode( PretupsErrorCodesI.CHANNEL_USER_UNBARRED_SUCC);
               	barUnbarResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
     			String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.CHANNEL_USER_UNBARRED_SUCC, new String[]{ barUnbarRequestVO.getMsisdn(),barredUserVO.getBarredType()});
       			barUnbarResponseVO.setMessage(resmsg);
            } else {
          		log.error(METHOD_NAME, "Error: while deleting from barred_msisdn Table");
            	mcomCon.finalRollback();
                barUnbarResponseVO.setMessageCode( PretupsErrorCodesI.ERROR_UNBARRED_USER_NOTUPDATE);
               	barUnbarResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
       			String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.ERROR_UNBARRED_USER_NOTUPDATE, null);
       			barUnbarResponseVO.setMessage(resmsg);
            }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("unBarredUser", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("unBarredUser", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UnBarUserHandler[unBarredUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }finally{
        	try {
				if (mcomCon != null) {
					mcomCon.close("BarUnbarUserServiceImpl#unBarredUser");
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
	            log.debug("unBarredUser", "Exiting  method=" + METHOD_NAME);
	        }
        }
        return barUnbarResponseVO;
    }
	
	
	public void vaidateBarUserRequest(Connection con,BarUnbarRequestVO barUnbarRequestVO,ErrorMap errorMap,UserVO userVO) throws BTSLBaseException{
	        final String METHOD_NAME = "vaidateBarUserRequest";
	        if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, "Entered BarUnbarRequestVO =" + barUnbarRequestVO);
	        }
	        ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
			ArrayList<RowErrorMsgLists> rowErrorMsgLists = new ArrayList<RowErrorMsgLists>();
	        String arr[] = null;
	        Boolean error = false;
	        Locale locale =null;
	        if (userVO.getUserPhoneVO()!=null && userVO.getUserPhoneVO().getLocale() == null) {
	            locale = new Locale(userVO.getUserPhoneVO().getPhoneLanguage(), userVO.getUserPhoneVO().getCountry());
	        } 
	        else {
	            locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
	        }
	        try {
	        	int row =1;
                ListValueVO listValueVO = null;
	        	// getting the module ie whether the request is from C2S or P2P
	            // module.the request will be handled accordingly
	        	   if (BTSLUtil.isNullString(barUnbarRequestVO.getModule())) {
		            	error = true;
		    			MasterErrorList masterErrorList = new MasterErrorList();
		    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, new String[]{ "Module"});
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
			            if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(barUnbarRequestVO.getModule(), moduleList).getValue())) {
			            	error = true;
			    			MasterErrorList masterErrorList = new MasterErrorList();
			    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_INVALID_MODULE_VALUE, new String[]{barUnbarRequestVO.getModule()});
			    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_INVALID_MODULE_VALUE);
			    			masterErrorList.setErrorMsg(msg);
			    			masterErrorListMain.add(masterErrorList);
			            }
		            }
		            
		            // getting the msisdn of the retailer
		            String msisdn = barUnbarRequestVO.getMsisdn();
		            if (BTSLUtil.isNullString(msisdn)) {
		                error = true;
		    			MasterErrorList masterErrorList = new MasterErrorList();
		    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, new String[]{ "Msisdn"});
		    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
		    			masterErrorList.setErrorMsg(msg);
		    			masterErrorListMain.add(masterErrorList);
		            }
			        else{// filtering the msisdn for country independent dial format
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
					            }
				            }
			            }
		            }
		            if (BTSLUtil.isNullString(barUnbarRequestVO.getUserType())) {
		                arr = new String[] { "Barrred User Type" };
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
		                if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(barUnbarRequestVO.getUserType(), barredUserTypeList).getValue())) {
		                	error = true;
			    			MasterErrorList masterErrorList = new MasterErrorList();
			    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_INVALID_BARUSERTYPE_VALUE, null);
			    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_INVALID_BARUSERTYPE_VALUE);
			    			masterErrorList.setErrorMsg(msg);
			    			masterErrorListMain.add(masterErrorList);
		                }
		            }
		            
		        	// for the C2S module ie the request is from C2S
		            if(barUnbarRequestVO.getModule()!=null && barUnbarRequestVO.getUserType() != null && barUnbarRequestVO.getMsisdn() != null){
		            if (barUnbarRequestVO.getModule().equals(PretupsI.C2S_MODULE) && PretupsI.USER_TYPE_SENDER.equals(barUnbarRequestVO.getUserType())) {
						  if (!new ChannelUserDAO().isPhoneExists(con, barUnbarRequestVO.getMsisdn())) {
						    MasterErrorList masterErrorList = new MasterErrorList();
		        			String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.USER_NOT_FOUND_BAR, new String[]{ barUnbarRequestVO.getMsisdn()});
		        			masterErrorList.setErrorCode(PretupsErrorCodesI.USER_NOT_FOUND_BAR);
		        			masterErrorList.setErrorMsg(msg);
		        			masterErrorListMain.add(masterErrorList);
		        			errorMap.setMasterErrorList(masterErrorListMain);
		        			}
		             }
	            }
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
                
                if(error){
					  errorMap.setMasterErrorList(masterErrorListMain);
					  masterErrorListMain = new ArrayList<MasterErrorList>();
		          }
	        
              //validation for barring type and reason	 
                List<Bar> barUnbarRequestDatas = barUnbarRequestVO.getBar();
	        	for(Bar barUnbarRequestData : barUnbarRequestDatas){
	        		error=false;
	        		RowErrorMsgLists rowErrorMsgListss= new RowErrorMsgLists();
	        		rowErrorMsgListss.setRowValue(String.valueOf(row));
	        		rowErrorMsgListss.setRowName("Bar "+row);
	        		row++;
	        		if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue()){
	        			  if (BTSLUtil.isNullString(barUnbarRequestData.getBarringReason())) {
	  		            	error = true;
	  		    			MasterErrorList masterErrorList = new MasterErrorList();
	  		    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_IS_MAND, new String[]{"Barring Reason"});
	  		    			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_IS_MAND);
	  		    			masterErrorList.setErrorMsg(msg);
	  		    			masterErrorListMain.add(masterErrorList);
	  		            }
	        		}
	        		// to avoid more than 150 character in remarks field
		            if (!BTSLUtil.isNullString(barUnbarRequestData.getBarringReason()) && barUnbarRequestData.getBarringReason().length() > 150) {
		            	error = true;
		    			MasterErrorList masterErrorList = new MasterErrorList();
		    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_REMARKS_LEN_MORE_THAN_ALLOWED, null);
		    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_REMARKS_LEN_MORE_THAN_ALLOWED);
		    			masterErrorList.setErrorMsg(msg);
		    			masterErrorListMain.add(masterErrorList);
		            }
		             if (BTSLUtil.isNullString(barUnbarRequestData.getBarringType())) {
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
			                    if (subLookUpVO.getSubLookupCode().equals(barUnbarRequestData.getBarringType())) {
			                    	 flag = false;
			                    	 break;
			                    }
			                }
		                if (flag) {
		                    error = true;
			    			MasterErrorList masterErrorList = new MasterErrorList();
			    			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_INVALID_BARTYPE_VALUE, new String[]{ barUnbarRequestData.getBarringType()});
			    			masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_INVALID_BARTYPE_VALUE);
			    			masterErrorList.setErrorMsg(msg);
			    			masterErrorListMain.add(masterErrorList);
		                }
		          	}
		          if(error){
		        	  rowErrorMsgListss.setMasterErrorList(masterErrorListMain);
					  rowErrorMsgLists.add(rowErrorMsgListss);
		          }
	        	}
	        	if(error)
				  errorMap.setRowErrorMsgLists(rowErrorMsgLists);
	        	
	        	if(errorMap.getMasterErrorList() != null && errorMap.getRowErrorMsgLists()!=null){
	        	 	// for the C2S module ie the request is from C2S
		            if (barUnbarRequestVO.getModule().equals(PretupsI.C2S_MODULE) && PretupsI.USER_TYPE_SENDER.equals(barUnbarRequestVO.getUserType())) {
						/*
						 * if (!channelUserDAO.isPhoneExists(con, barUnbarRequestVO.getMsisn())) {
						 * barUnbarResponseVO.setMessageCode(
						 * PretupsErrorCodesI.CCE_XML_ERROR_CHNL_USER_NOTEXIST);
						 * barUnbarResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
						 * String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.
						 * CCE_XML_ERROR_CHNL_USER_NOTEXIST, null);
						 * barUnbarResponseVO.setMessage(resmsg); }
						 */
		                ChannelUserVO channelUserVO = new ChannelUserDAO().loadChannelUserDetails(con,barUnbarRequestVO.getMsisdn());
		                if (channelUserVO == null) {
		        			MasterErrorList masterErrorList = new MasterErrorList();
		        			String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.USER_NOT_FOUND_BAR, new String[]{ barUnbarRequestVO.getMsisdn()});
		        			masterErrorList.setErrorCode(PretupsErrorCodesI.USER_NOT_FOUND_BAR);
		        			masterErrorList.setErrorMsg(msg);
		        			masterErrorListMain.add(masterErrorList);
		        			errorMap.setMasterErrorList(masterErrorListMain);
		                }
		            }	
	        	}
	        	
	        } catch (BTSLBaseException be) {
	            log.errorTrace(METHOD_NAME, be);
	            log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
	            throw be;
	        } catch (Exception e) {
	            log.error(METHOD_NAME, "Exception " + e.getMessage());
	            log.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarUnbarUserServiceImpl[vaidateBarUserRequest]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException("BarUnbarUserServiceImpl", METHOD_NAME, PretupsErrorCodesI.REQ_NOT_PROCESS);
	        }
	        if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, "Exiting ");
	        }
	    }
	
	
		@Override
		public void processGetUserInfoForBarring(Connection p_con, String p_msisdn, UserVO userVO,
				BarUserInfoResponseVO barUserInfoResponseVO) throws BTSLBaseException {
			final String METHOD_NAME = "processGetUserInfoForBarring";
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Entered");
			}
			if (BTSLUtil.isNullString(p_msisdn)) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.IS_REQUIRED,
						new String[] { "Msisdn" });
			}
			if (!BTSLUtil.isValidMSISDN(p_msisdn)) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_MSISDN,
						new String[] { p_msisdn });
			}
			BarredUserVO barreduserVO = new BarredUserVO();
			barreduserVO.setMsisdn(p_msisdn);
			barUserInfoResponseVO.setMsisdn(p_msisdn);
			
			if(!PretupsI.CATEGORY_USER_TYPE.equalsIgnoreCase(userVO.getUserType())){
				Boolean isExist = new ChannelUserDAO().isUserInHierarchy(p_con, userVO.getUserID(), "MSISDN", p_msisdn);
				if (!isExist)
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_SESSION_HIERARCHY);
			
			}
		
			String msisdnPrefix = PretupsBL.getMSISDNPrefix(barreduserVO.getMsisdn());
			if (((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR))).booleanValue()) {
				NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
				if (networkPrefixVO != null) {
					if (userVO.getNetworkID().equalsIgnoreCase(networkPrefixVO.getNetworkCode())) {
						barreduserVO.setNetworkCode(networkPrefixVO.getNetworkCode());
					} else {
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NOT_AUTHORIZED_TO_BAR,
								"barredUser");
					}
				} else {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UNSUPPORTED_NETWORK,
							"barredUser");
				}
			} else {
				barreduserVO.setNetworkCode(userVO.getNetworkID());
			}

			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			if (channelUserDAO.isPhoneExists(p_con, barreduserVO.getMsisdn())) {
				UserDAO userDAO = new UserDAO();
				ChannelUserVO chnlUserVO = userDAO.loadUserDetailsByMsisdn(p_con, p_msisdn);
				if (chnlUserVO != null) {
					ListValueVO domainTemp = null;
		            final ArrayList domainListAll = new DomainDAO().loadCategoryDomainList(p_con);
		            for (int i = 0, j = domainListAll.size(); i < j; i++) {
		            	domainTemp = (ListValueVO) domainListAll.get(i);
		            	if(chnlUserVO.getDomainID().equals(domainTemp.getValue())) {
		            		barUserInfoResponseVO.setDomain(domainTemp.getLabel());
		            		break;
		            	}
		            }
					barUserInfoResponseVO.setUserName(chnlUserVO.getUserName());
					barUserInfoResponseVO.setSenderAllowed(true);
					barUserInfoResponseVO.setCategory(chnlUserVO.getCategoryVO().getCategoryName());
					barUserInfoResponseVO.setUserType(chnlUserVO.getUserType());
		            
				}
			}

			barUserInfoResponseVO.setStatus(PretupsI.RESPONSE_SUCCESS);
			barUserInfoResponseVO.setMessage(PretupsI.SUCCESS);
			barUserInfoResponseVO.setMessageCode(PretupsErrorCodesI.SUCCESS);
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exit");
			}
		}
	}
