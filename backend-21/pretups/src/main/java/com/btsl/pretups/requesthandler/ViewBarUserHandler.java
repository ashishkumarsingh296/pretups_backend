package com.btsl.pretups.requesthandler;

/**
 * * @(#)ViewBarUserHandler.java
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
 * This class parses the request received on the basis of format for the view
 * Bar User.
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

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
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * @author ved.sharma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewBarUserHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(ViewBarUserHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private String _module = null;
    private String _userType = null;
    private String _barType = null;
    private String _fromDate = null;
    private String _toDate = null;

    private final String XML_TAG_MSISDN = "MSISDN";
    private final String XML_TAG_MODULE = "MODULE";
    private final String XML_TAG_USERTYPE = "USERTYPE";
    private final String XML_TAG_BARTYPE = "BARTYPE";
    private final String XML_TAG_FROM_DATE = "FROMDATE";
    private final String XML_TAG_TO_DATE = "TODATE";

    /**
     * This method is the entry point into the class.
     * method process
     * 
     * @param RequestVO
     *            p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered....: p_requestVO= " + p_requestVO.toString());
        }
        _requestVO = p_requestVO;
        Connection con = null;MComConnectionI mcomCon = null;
        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
            _requestMap = _requestVO.getRequestMap();
            _module = (String) _requestMap.get(XML_TAG_MODULE);
            _userType = (String) _requestMap.get(XML_TAG_USERTYPE);
            _barType = (String) _requestMap.get(XML_TAG_BARTYPE);

            // this method validates whether the msisdn, module and action have
            // valid values in the request
            validate(con);
            viewBarredUser(con);

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
            try {
                if (con != null) {
                    con.rollback();
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
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UnBarUserHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("ViewBarUserHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
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
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered.....");
        }

        String arr[] = null;
        try {
            boolean mondatoryMsisdnFlag = false;
            boolean mondatoryModuleFlag = false;
            String mobileNo = (String) _requestMap.get(XML_TAG_MSISDN);
            if (!BTSLUtil.isNullString(mobileNo)) {
                // filtering the msisdn for country independent dial format
                String filteredMsisdn = PretupsBL.getFilteredMSISDN(mobileNo);
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }
                String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                _requestVO.setFilteredMSISDN(filteredMsisdn);
                // checking whether the msisdn prefix is valid in the network
                NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    arr = new String[] { filteredMsisdn };
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
                }
                String networkCode = networkPrefixVO.getNetworkCode();
                if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
            } else {
                mondatoryMsisdnFlag = true;
            }

            // getting the module ie whether the request is from C2S or P2P
            // module.the request will be handled accordingly
            if (BTSLUtil.isNullString(_module)) {
                mondatoryModuleFlag = true;
            } else {
                // the module value should be either C2S or P2P else throw error
                ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
                if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(_module, moduleList).getValue())) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_MODULE);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_MODULE_VALUE);
                }
            }
            if (BTSLUtil.isNullString(_userType)) {
                _userType = PretupsI.ALL;
            } else {
                // checkinh bar user type
                ArrayList barredUserTypeList = LookupsCache.loadLookupDropDown(PretupsI.BARRED_USER_TYPE, true);
                if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(_userType, barredUserTypeList).getValue())) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_USERTYPE);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_BARUSERTYPE_VALUE);
                }
            }
            if (BTSLUtil.isNullString(_barType)) {
                _barType = PretupsI.ALL;
            } else {
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
            if (mondatoryModuleFlag && mondatoryMsisdnFlag) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN + "," + XML_TAG_MODULE);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            HashMap dateMap = HandlerUtil.dateValidation((String) _requestMap.get(XML_TAG_FROM_DATE), (String) _requestMap.get(XML_TAG_TO_DATE));
            _fromDate = (String) dateMap.get(HandlerUtil.FROM_DATE_STR);
            _toDate = (String) dateMap.get(HandlerUtil.TO_DATE_STR);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UnBarUserHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("UnBarUserHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exiting ");
        }
    }

    /**
     * This method is called when a request is received for bar user.
     * method viewBarredUser
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void viewBarredUser(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "viewBarredUser";
        if (_log.isDebugEnabled()) {
            _log.debug("viewBarredUser", "Entered .....");
        }
        try {
            BarredUserDAO barredUserDAO = new BarredUserDAO();
            UserVO userVO = (UserVO) _requestVO.getSenderVO();
            String networkCode = userVO.getNetworkID();
            BarredUserVO barredUserVO = new BarredUserVO();
            barredUserVO.setModule(BTSLUtil.NullToString(_module).toUpperCase());
            barredUserVO.setMsisdn(_requestVO.getFilteredMSISDN());
            barredUserVO.setNetworkCode(networkCode);
            barredUserVO.setUserType(_userType);
            barredUserVO.setBarredType(_barType);
            barredUserVO.setFromDate(_fromDate);
            barredUserVO.setToDate(_toDate);
            ArrayList barredUserList = barredUserDAO.loadBarredUserListForXMLAPI(p_con, barredUserVO);

            if (barredUserList != null && barredUserList.size() > 0) {
                _requestMap.put("RES_TYPE", "SUMMARY");
                _requestVO.setValueObject(barredUserList);
            } else {
                throw new BTSLBaseException(this, "viewBarredUser", PretupsErrorCodesI.CCE_XML_ERROR_BARRED_USER_NOTEXISTINLIST);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("viewBarredUser", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("viewBarredUser", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UnBarUserHandler[viewBarredUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "viewBarredUser", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("viewBarredUser", "Exiting ");
        }
    }
}
