package com.btsl.pretups.requesthandler;

/**
 * * @(#)UnBarUserHandler.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Ved prakash Sharma Dec 12, 2006 Initial Creation
 * 
 * This class parses the request received on the basis of format for the Un Bar
 * User.
 * 
 * 
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

/**
 * @author ved.sharma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class UnBarUserHandler implements ServiceKeywordControllerI {

    private Log log = LogFactory.getLog(UnBarUserHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private String _module = null;
    private String _userType = null;
    private String _barType = null;
    private static final String XML_TAG_MODULE = "MODULE";
    private static final String XML_TAG_MSISDN = "MSISDN";
    private static final String XML_TAG_USERTYPE = "USERTYPE";
    private static final String XML_TAG_BARTYPE = "BARTYPE";

    
    //for child user unbar
	private String _service_type = null;
	private String _childMsisdn = null;
	private String _parentId = null;
	private String _networkCode = null;
	private String _parentMsisdn = null;
	private String _parentLoginId= null;
	private String _parentPassword = null;
	private String _parentPin = null;
	private ChannelUserVO _parentVO = null;
	private ChannelUserVO _childVO = null;
    
    /**
     * This method is the entry point into the class.
     * method process
     * 
     * @param RequestVO
     *            p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (log.isDebugEnabled()) {
            log.debug("process", "Entered....: p_requestVO= " + p_requestVO.toString());
        }
        _requestVO = p_requestVO;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
            _requestMap = _requestVO.getRequestMap();
            _module = (String) _requestMap.get(XML_TAG_MODULE);
            _userType = (String) _requestMap.get(XML_TAG_USERTYPE);
            _barType = (String) _requestMap.get(XML_TAG_BARTYPE);
            
            
            
            //for child user unbar
            _service_type = p_requestVO.getServiceType();
			_childMsisdn = (String) _requestMap.get("MSISDN2");
			_parentId = p_requestVO.getActiverUserId();
			_networkCode = p_requestVO.getRequestNetworkCode();
	        _parentMsisdn = p_requestVO.getFilteredMSISDN();
	        _parentLoginId = p_requestVO.getSenderLoginID();
	        _parentPassword = p_requestVO.getPassword();
	        _parentPin = (String) _requestMap.get("PIN");

            // this method validates whether the msisdn, module and action have
            // valid values in the request
            validate(con);
			if(log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Service type=" + _service_type);
			}
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			BarredUserDAO barredUserDAO = new BarredUserDAO();
			ChannelUserVO channelUserVO = new ChannelUserVO();
			if(PretupsI.SERVICE_TYPE_CHILDUNBAR.equalsIgnoreCase(_service_type) && !BTSLUtil.isNullString(_childMsisdn)) {

				
	        	//validate pin if sender authentication is done by msisdn and pin
	        	if( BTSLUtil.isNullString(_parentLoginId) || BTSLUtil.isNullString(_parentPassword)) {
	        		
	        		
		        			
		            try {
		                ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), _parentPin);
		            } catch (BTSLBaseException be) {
		                log.errorTrace(METHOD_NAME, be);
		                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
		                                .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
		                    con.commit();
		                }
		                log.error(METHOD_NAME, "BTSLBaseException " + be);
		                throw be;
		            }
		        	
	        	
	        	}
				
    			// check if user is child or not
    			
	        	_parentVO = (ChannelUserVO)p_requestVO.getSenderVO();
            	ChannelUserTxnDAO channelUserTxnDAO = null;
            	channelUserTxnDAO = new ChannelUserTxnDAO();
            	_childVO = channelUserTxnDAO.loadOtherUserBalanceVO(con, _childMsisdn, _parentVO);
            	
            	
    			if(_childVO.getUserName() == null) {
    				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.USER_NOT_CHILD);
    			}
    			
    			
    			

    			
    			boolean isUserBarred = barredUserDAO.isExists(con, _module, _networkCode, _childMsisdn, _userType, null);

                Locale locale = null;
                
				// unbar child user
				if(isUserBarred){
					// unbar 
					int status = barredUserDAO.deleteSingleBarredMsisdn(con, _module, _networkCode, _childMsisdn, _userType, _barType);
					if(status!=-1) {
						con.commit();
						channelUserVO = (ChannelUserVO) channelUserDAO.loadChannelUserDetails(con, _childMsisdn);
						UserPhoneVO phoneVO = channelUserDAO.loadUserPhoneDetails(con, channelUserVO.getUserID());
						locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
		                PushMessage pushMessage = new PushMessage(_childMsisdn, new BTSLMessages(PretupsErrorCodesI.CHANNEL_USER_UNBARRED), null, null, locale, _networkCode);
		                pushMessage.push();
						p_requestVO.setMessageCode(PretupsErrorCodesI.USER_UNBAR_SUCCESS);
												
					}else {
						throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.USER_UNBAR_FAILED);
					}
					

				}else {
					// throw error that user is not barred
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHANNEL_USER_ALREADY_UNBARRED);
				}
                
            }
            else {
                unBarredUser(con);       	
            }


            // setting the transaction status to true
            _requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException be) {
            log.error("process", "BTSLBaseException " + be.getMessage());
            _requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            try {
                if (con != null) {
                   mcomCon.finalRollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.errorTrace(METHOD_NAME, be);
        } catch (Exception ex) {
            _requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", "Exception " + ex.getMessage());
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UnBarUserHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("UnBarUserHandler#process");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("process", " Exited ");
            }
        }// end of finally
    }

    /**
     * This method is called to validate the values present in the requestMap of
     * the requestVO
     * The purpose of this method is to validate the values of the msisdn,
     * module, userType, barType
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void validate(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (log.isDebugEnabled()) {
            log.debug("validate", "Entered.....");
        }

        String arr[] = null;
        try {
            // getting the module ie whether the request is from C2S or P2P
            // module.the request will be handled accordingly
            if (BTSLUtil.isNullString(_module)) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_MODULE);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            // the module value should be either C2S or P2P else throw error
            ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(_module, moduleList).getValue())) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_MODULE);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_MODULE_VALUE);
            }
            //if service is childunbar and msisdn2 is not null we need to validate the msisdn2 and not msisdn as it is optional parameter
            if(PretupsI.SERVICE_TYPE_CHILDUNBAR.equalsIgnoreCase(_service_type) && !BTSLUtil.isNullString(_childMsisdn)) {

            	
            	//validate msisdn of child user
                String filteredChildMsisdn = PretupsBL.getFilteredMSISDN(_childMsisdn);
                if (!BTSLUtil.isValidMSISDN(filteredChildMsisdn)) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }
                

                
                String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredChildMsisdn);
                _requestVO.setFilteredMSISDN(filteredChildMsisdn);
                // checking whether the msisdn prefix is valid in the network
                NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    arr = new String[] { filteredChildMsisdn };
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
                }
                String networkCode = networkPrefixVO.getNetworkCode();
                if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                
                
            }
            
            else {
                // getting the msisdn of the retailer
                String msisdn = (String) _requestMap.get(XML_TAG_MSISDN);
                if (BTSLUtil.isNullString(msisdn)) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
                }
                // filtering the msisdn for country independent dial format
                String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }
                String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                _requestVO.setFilteredMSISDN(filteredMsisdn);
                // checking whether the msisdn prefix is valid in the network
                NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    arr = new String[] { filteredMsisdn };
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
                }
                String networkCode = networkPrefixVO.getNetworkCode();
                if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
            }
            // checkinh bar user type
            if (BTSLUtil.isNullString(_userType)) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_USERTYPE);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            } else {
                // checkin bar user type
                ArrayList barredUserTypeList = LookupsCache.loadLookupDropDown(PretupsI.BARRED_USER_TYPE, true);
                if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(_userType, barredUserTypeList).getValue())) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_USERTYPE);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_BARUSERTYPE_VALUE);
                }
            }
            if (BTSLUtil.isNullString(_barType)) {
                _barType = PretupsI.ALL;
            }

            if (!PretupsI.ALL.equals(_barType)) {
                SubLookUpDAO subLookUpDAO = new SubLookUpDAO();
                ArrayList barredTypeList = subLookUpDAO.loadSublookupVOList(p_con, PretupsI.BARRING_TYPE);

                if (barredTypeList == null || barredTypeList.isEmpty()) {
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_BARTYPELIST_EMPTY);
                }
                SubLookUpVO subLookUpVO = null;
                boolean flag = true;
                for (int i = 0, j = barredTypeList.size(); i < j; i++) {
                    subLookUpVO = (SubLookUpVO) barredTypeList.get(i);
                    if (PretupsI.P2P_MODULE.equals(_module)) {
                        if (subLookUpVO.getLookupCode().equals(PretupsI.P2P_BARTYPE_LOOKUP_CODE)) {
                            if (_barType.equals(subLookUpVO.getSubLookupCode())) {
                                flag = false;
                                break;
                            }
                        }
                    } else {
                        if (subLookUpVO.getLookupCode().equals(PretupsI.C2S_BARTYPE_LOOKUP_CODE)) {
                            if (_barType.equals(subLookUpVO.getSubLookupCode())) {
                                flag = false;
                                break;
                            }
                        }
                    }
                }
                if (flag) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_BARTYPE);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_BARTYPE_VALUE);
                }
            }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("validate", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UnBarUserHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("UnBarUserHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("validate", "Exiting ");
        }
    }

    /**
     * This method is called when a request is received for bar user.
     * method unBarredUser
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void unBarredUser(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "unBarredUser";
        if (log.isDebugEnabled()) {
            log.debug("unBarredUser", "Entered .....");
        }
        try {
            BarredUserDAO barredUserDAO = new BarredUserDAO();
            UserVO userVO = (UserVO) _requestVO.getSenderVO();
            String networkCode = userVO.getNetworkID();
            Date currentDate = new Date(System.currentTimeMillis());

            BarredUserVO barredUserVO = new BarredUserVO();
            barredUserVO.setModifiedOn(currentDate);
            barredUserVO.setModifiedBy(userVO.getUserID());
            barredUserVO.setModule(_module);
            barredUserVO.setNetworkCode(networkCode);
            barredUserVO.setMsisdn(_requestVO.getFilteredMSISDN());
            barredUserVO.setUserType(_userType);
            Locale locale = null;
            ArrayList barredUserList = barredUserDAO.loadInfoOfBarredUser(p_con, barredUserVO);
            boolean checkBarType = true;
            if (barredUserList != null && barredUserList.size() > 0) {
                for (int i = 0, j = barredUserList.size(); i < j; i++) {
                    barredUserVO = (BarredUserVO) barredUserList.get(i);
                    barredUserVO.setMultiBox(PretupsI.NO);
                    if (PretupsI.ALL.equals(_barType) || _barType.equals(barredUserVO.getBarredType())) {
                        barredUserVO.setMultiBox(PretupsI.YES);
                        checkBarType = false;
                    }
                    barredUserList.set(i, barredUserVO);
                }
            }
            if (checkBarType) {
                throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_BARRED_USER_NOTEXISTINLIST);
            }

            if (!barredUserDAO.isExists(p_con, _module, networkCode, _requestVO.getFilteredMSISDN(), _userType, null)) {
                throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_BARRED_USER_NOTEXISTINLIST);
            }
            // for the C2S module ie the request is from C2S
            if (_module.equals(PretupsI.C2S_MODULE) && PretupsI.USER_TYPE_SENDER.equals(barredUserVO.getUserType())) {
                ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                if (!channelUserDAO.isPhoneExists(p_con, _requestVO.getFilteredMSISDN())) {
                    // in
                    // user
                    // phones
                    throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_CHNL_USER_NOTEXIST);
                }

                ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, _requestVO.getFilteredMSISDN());
                if (channelUserVO == null) {
                    throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_CHNL_USER_NOTEXIST);
                }

                boolean isChDomainCheckRequired = false;
                boolean isGeoDomainCheckRequired = false;
                ArrayList domainList = null;
                if (PretupsI.BCU_USER.equals(userVO.getCategoryVO().getCategoryCode())) {
                    isGeoDomainCheckRequired = true;
                }
                if (PretupsI.DOMAINS_ASSIGNED.equals(userVO.getCategoryVO().getFixedDomains())) {
                    domainList = userVO.getDomainList();
                    if (domainList == null || domainList.isEmpty()) {
                        throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_DOMAIN_NOTASSIGNED);
                    }
                    isChDomainCheckRequired = true;
                }
                if (isChDomainCheckRequired || isGeoDomainCheckRequired) {
                    if (isChDomainCheckRequired)// check domain
                    {
                        if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(channelUserVO.getDomainID(), domainList).getValue())) {
                            throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_DOMAIN_NOTMATCH);
                        }
                    }
                    if (isGeoDomainCheckRequired)// check geographical domain
                                                 // hierarchy
                    {
                        GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
                        if (!geographicalDomainDAO.isUserExistsInGeoDomainExistHierarchy(p_con, channelUserVO.getUserID(), userVO.getUserID())) {
                            throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_GEOGRAPHYDOMAIN_NOTIN_HIERARCHY);
                        }
                    }
                }
                // locale=new
                // Locale((channelUserVO.getUserPhoneVO()).getPhoneLanguage(),(channelUserVO.getUserPhoneVO()).getCountry());
            }
            // for the P2P module ie the request is from P2P module
            /*
             * else if(_module.equals(PretupsI.P2P_MODULE))
             * {
             * SubscriberDAO subscriberDAO = new SubscriberDAO();
             * ArrayList userList =
             * subscriberDAO.loadSubscriberDetails(p_con,barredUserVO
             * .getMsisdn(),null,null,PretupsI.ALL);
             * if(userList==null || userList.isEmpty())
             * locale=new
             * Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),SystemPreferences
             * .DEFAULT_COUNTRY);
             * else
             * {
             * SenderVO senderVO = (SenderVO)userList.get(0);
             * locale=new Locale(senderVO.getLanguage(),senderVO.getCountry());
             * }
             * }
             */
            int deleteCount = barredUserDAO.deleteFromBarredMsisdn(p_con, barredUserList);
            if (deleteCount > 0) {
                p_con.commit();
                // if(locale==null)
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                PushMessage pushMessage = new PushMessage(barredUserVO.getMsisdn(), new BTSLMessages(PretupsErrorCodesI.CHANNEL_USER_UNBARRED), null, null, locale, barredUserVO.getNetworkCode());
                pushMessage.push();
            } else {
                throw new BTSLBaseException(this, "unBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_BARRED_USER_NOTUPDATE);
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
        }
        if (log.isDebugEnabled()) {
            log.debug("unBarredUser", "Exiting  module=" + _module);
        }
    }
}
