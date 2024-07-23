package com.btsl.pretups.requesthandler;

/**
 * * @(#)ChangeLanguageHandler.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Siddhartha Srivastava Dec 11, 2006 Initial Creation
 * 
 * This class handles the request for the change of language for a subscriber.
 * The subscriber's language will be changed on the basis of language code
 * provided with the request.
 * 
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ChangeLocaleVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class ChangeLanguageHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(ChangeLanguageHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private String _module = null;
    private ChannelUserDAO _channelUserDAO = null;
    private SubscriberDAO _subscriberDAO = null;

    /**
     * This is the entry point for the class and the only public method.
     * All the supporting methods are called from within this method.
     * 
     * @param p_requestVO
     */

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered....: p_requestVO= " + p_requestVO);
        }

        _requestVO = p_requestVO;
        Connection con = null;
        MComConnectionI mcomCon = null;

        try {
            // retreiving the Map which has all the values in the request.These
            // values can then be retreived by
            // using the tag name as key value
            _requestMap = _requestVO.getRequestMap();
            _module = (String) _requestMap.get("MODULE");

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            // validates the parameter passed in the request eg. msisdn, module
            // and language code
            validate(con);

            _subscriberDAO = new SubscriberDAO();

            changeLanguage(con, _module);
            // setting the transaction status to true
            _requestVO.setSuccessTxn(true);
        } catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            // Rollbacking the transaction
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception ex) {
            _requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangeLanguageHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            _requestMap = null;
            _requestVO = null;
            _module = null;
            _channelUserDAO = null;
            _subscriberDAO = null;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("ChangeLanguageHandler#process");
				mcomCon = null;
			}
        }// end of finally
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Exiting.... ");
        }
    }

    /**
     * This method validates the passed parameter values in the request. The
     * passed values are checked for not being
     * null. If they are not null then further checks on them are applied
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void validate(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered .....");
        }

        String msisdnPrefix = null;
        String filteredMsisdn = null;
        String langCode = null;
        try {
            // langCode will be combination of language code and country code eg
            // en_us
            langCode = (String) _requestMap.get("LANGCODE");
            // getting the language code and checking it for not being null. The
            // language code should be in the list of language code
            // The langCode will be like 0 - English, 1- others
            if (BTSLUtil.isNullString(langCode)) {
                _requestMap.put("RES_ERR_KEY", "LANGCODE");
                throw new BTSLBaseException("ChangeLanguageHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }

            // getting the module and checking it for not being null. The
            // request can be from C2S or P2P module.The request will be handled
            // accordingly
            // if the module is null throw exception
            if (BTSLUtil.isNullString(_module)) {
                _requestMap.put("RES_ERR_KEY", "MODULE");
                throw new BTSLBaseException("ChangeLanguageHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }

            // getting the msisdn of the retailer
            String msisdn = (String) _requestMap.get("MSISDN");

            // checking whether the msisdn is not blank.If blank throw error
            if (BTSLUtil.isNullString(msisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN");
                throw new BTSLBaseException("ChangeLanguageHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }

            // the module value should be either C2S or P2P else throw error
            ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            // if the module value passed as parameter in the request has no
            // description then throw exception.
            // No description implies that the value is invalid
            if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(_module, moduleList).getValue())) {
                _requestMap.put("RES_ERR_KEY", "MODULE");
                throw new BTSLBaseException("ChangeLanguageHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_MODULE_VALUE);
            }

            // filtering the msisdn for country independent dial format
            filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN");
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
            }
            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            _requestVO.setFilteredMSISDN(filteredMsisdn);
            // checking whether the msisdn prefix is valid, defined in the
            // network as prefix
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            // the msisdn prefix is not defined in the network
            if (networkPrefixVO == null) {
                String[] arr = new String[] { filteredMsisdn };
                throw new BTSLBaseException("ChangeLanguageHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
            }
            String networkCode = networkPrefixVO.getNetworkCode();
            // this check conforms if the customer care user and the customer
            // care belong to the same netowrk
            if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                throw new BTSLBaseException("ChangeLanguageHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
            }

            // creates an instance of channelUserDAO which will be used in the
            // remainder of the code
            _channelUserDAO = new ChannelUserDAO();
            // laoding the locale based on the passed value of language code tag
            // in the request
            Locale locale = LocaleMasterCache.getLocaleFromCodeDetails(langCode);
            // the langCode is undefined in the system.
            if (locale == null) {
                throw new BTSLBaseException("ChangeLanguageHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_LANGUAGE_CODE_VALUE);
            }

            ArrayList langCodeList = _channelUserDAO.loadLanguageListForUser(p_con);
            String language = locale.getLanguage();
            // checking whether the language exists or the locale has invalid
            // language for the given langCode
            int size = langCodeList.size();
            ChangeLocaleVO localeVO = null;
            boolean matchFound = false;
            for (int x = 0; x < size; x++) {
                localeVO = (ChangeLocaleVO) langCodeList.get(x);
                String languageCode = localeVO.getLanguageCode();
                if (languageCode.equals(language)) {
                    matchFound = true;
                    break;
                }
            }
            // means no match found for the entered languageCode
            if (!matchFound) {
                throw new BTSLBaseException("ChangeLanguageHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_LANGUAGE_CODE_VALUE);
            }
            // setting the locale in the requestVO to be used later in
            // changeLanguage method
            _requestVO.setLocale(locale);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangeLanguageHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ChangeLanguageHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exiting filteredMsisdn= " + filteredMsisdn + " _module:= " + _module + " _langCode:= " + langCode);
        }
    }

    /**
     * This method is the main method for validation of the existence of the
     * msisdn in case of request from C2S.
     * This method updates the user language code and the country code based on
     * the passed value of langaugeCode
     * 
     * @param p_con
     * @param p_module
     * @throws BTSLBaseException
     */
    private void changeLanguage(Connection p_con, String p_module) throws BTSLBaseException {
        final String METHOD_NAME = "changeLanguage";
        if (_log.isDebugEnabled()) {
            _log.debug("changeLanguage", "Entered   p_module:= " + p_module);
        }

        // getting the locale from the _requestVO
        Locale locale = _requestVO.getLocale();
        String language = locale.getLanguage();
        String country = locale.getCountry();

        try {
            int updateCount = 0;
            if (PretupsI.C2S_MODULE.equalsIgnoreCase(p_module)) {
                // Check isPhoneExists
                if (!_channelUserDAO.isPhoneExists(p_con, _requestVO.getFilteredMSISDN())) {
                    throw new BTSLBaseException("ChangeLanguageHandler", "changeLanguage", PretupsErrorCodesI.CCE_ERROR_USER_DETAIL_NOT_FOUND);
                }

                /*
                 * ChannelUserVO tempChannelUserVO =
                 * _channelUserDAO.loadChannelUserDetails
                 * (p_con,_requestVO.getFilteredMSISDN());
                 * if(tempChannelUserVO == null)
                 * throw new BTSLBaseException("ChangeLanguageHandler",
                 * "changeLanguage",
                 * PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK);
                 */

                // Update user phones table & set language and country
                updateCount = _channelUserDAO.updateLanguageAndCountry(p_con, language, country, _requestVO.getFilteredMSISDN());
                if (updateCount > 0) {
                    p_con.commit();
                } else {
                    throw new BTSLBaseException("ChangeLanguageHandler", "changeLanguage", PretupsErrorCodesI.REQ_NOT_PROCESS);
                }
            } else if (PretupsI.P2P_MODULE.equalsIgnoreCase(p_module)) {
                ArrayList subscriberList = null;
                subscriberList = _subscriberDAO.loadSubscriberDetails(p_con, _requestVO.getFilteredMSISDN(), null, null, PretupsI.ALL,null);
                if (!(subscriberList != null && subscriberList.size() > 0)) {
                    throw new BTSLBaseException("ChangeLanguageHandler", "changeLanguage", PretupsErrorCodesI.CCE_ERROR_SUBSCRIBER_DETAIL_NOT_FOUND);
                }

                // Update p2p subscriber table & set language and country
                updateCount = _subscriberDAO.updateLanguageAndCountry(p_con, language, country, _requestVO.getFilteredMSISDN());
                if (updateCount > 0) {
                    p_con.commit();
                } else {
                    throw new BTSLBaseException("ChangeLanguageHandler", "changeLanguage", PretupsErrorCodesI.REQ_NOT_PROCESS);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("ChangeLanguageHandler", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("ChangeLanguageHandler", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangeLanguageHandler[changeLanguage]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ChangeLanguageHandler", "changeLanguage", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("changeLanguage", "Exiting   p_module:= " + p_module + " country:= " + country + " language:= " + language);
        }
    }
}
