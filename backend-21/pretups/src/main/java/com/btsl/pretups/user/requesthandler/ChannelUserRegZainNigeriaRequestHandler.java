/*
 * Created on Sep 18, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.user.requesthandler;

/**
 * # ChannelUserRegZainNigeriaRequestHandler.java
 * Copyright(c) 2010, Comviva Technologies
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Chetan Kothari Nov 11, 2010 initialcreation
 * ----------------------------------------------------------------------------
 * ----------------------
 * This class is used for the creation of Channel user through STK
 * 
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author chetan.kothari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ChannelUserRegZainNigeriaRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(ChannelUserRegZainNigeriaRequestHandler.class.getName());
    private ChannelUserVO _channelUserVO = null;
    private CategoryVO _categoryVO = null;
    private ChannelUserVO _senderVO = null;
    private ReceiverVO _recVO = null;

    /**
     * Method Process
     * 
     * @param p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process::::ChannelUserRegZainNigeriaRequestHandler", "Entered p_requestVO: " + p_requestVO);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserDAO channelUserDAO = null;
        _channelUserVO = new ChannelUserVO();
        _senderVO = new ChannelUserVO();

        final String msg[] = new String[5];
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserRegZainNigeriaRequestHandler[process]", "",
                            "", "", "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            _senderVO = (ChannelUserVO) p_requestVO.getSenderVO();

            String userCategeryCode = _senderVO.getCategoryCode();
            final CategoryDAO categoryDAO = new CategoryDAO();
            final DomainDAO domainDAO = new DomainDAO();
            ArrayList catList = null;
            final String[] requestArr = p_requestVO.getRequestMessageArray();
            final UserPhoneVO userPhoneVO = _senderVO.getUserPhoneVO();
            if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                try {
                    ChannelUserBL.validatePIN(con, _senderVO, requestArr[3]);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                      mcomCon.finalCommit();
                    }
                    throw be;
                }
            }
            if (requestArr.length != 4) {
                throw new BTSLBaseException("ChannelUserRegZainNigeriaRequestHandler", "process", PretupsErrorCodesI.CHN_USR_REG_INVALID_REQUEST_FORMAT);
            }
            _recVO = new ReceiverVO();
            PretupsBL.validateMsisdn(con, _recVO, "STK_USER_CREATION", requestArr[1]);
            final DomainVO domainVO = domainDAO.loadDomainVO(con, _senderVO.getCategoryVO().getDomainCodeforCategory());
            if (_senderVO.getCategoryVO().getSequenceNumber() < Integer.parseInt(domainVO.getNumberOfCategories())) {
                catList = categoryDAO.loadCategoryHierarchyList(con, _senderVO.getCategoryVO().getDomainCodeforCategory(), _senderVO.getCategoryVO().getSequenceNumber() + 1);
            } else {
                throw new BTSLBaseException("ChannelUserRegZainNigeriaRequestHandler", "process", PretupsErrorCodesI.CHN_USR_REG_LEAF_CATEGORY);
            }
            // categorycode mandatoty check
            userCategeryCode = userCategeryCode.trim();
            // load category
            // if null category does not exist

            _categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con, ((ListValueVO) catList.get(0)).getValue());
            _channelUserVO.setCategoryVO(_categoryVO);
            _channelUserVO.setCategoryCode(_categoryVO.getCategoryCode());
            _channelUserVO.setCategoryName(_categoryVO.getCategoryName());
            _channelUserVO.setDomainTypeCode(_categoryVO.getDomainTypeCode());

            // username set
            _channelUserVO.setUserName(requestArr[2].trim());

            // short name set
            if (requestArr[2].length() > 15) {
                _channelUserVO.setShortName(requestArr[2].substring(0, 15));
            } else {
                _channelUserVO.setShortName(requestArr[2].trim());
            }

            _channelUserVO.setMsisdn(_recVO.getMsisdn());

            // user prefix check
            _channelUserVO.setUserNamePrefix("MR");
            channelUserDAO = new ChannelUserDAO();
            // _channelUserVO.setExternalCode(newUserExtCode.trim());

            // login id unique check
            String newUserLoginId = _recVO.getMsisdn();
            newUserLoginId = newUserLoginId.trim();
            if (new UserDAO().isUserLoginExist(con, newUserLoginId, null)) {
                throw new BTSLBaseException("ChannelUserRegZainNigeriaRequestHandler", "process", PretupsErrorCodesI.ERROR_CHNL_USER_LOGINID_ALREADY_EXIST);
            }
            if (new UserDAO().isMSISDNExist(con, _channelUserVO.getMsisdn(), null)) {
                throw new BTSLBaseException("ChannelUserRegZainNigeriaRequestHandler", "process", PretupsErrorCodesI.ERROR_CHNL_USER_MSISDN_ALREADY_EXIST);
            }
            if (_categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                _channelUserVO.setLoginID(newUserLoginId);
                _channelUserVO.setPassword(BTSLUtil.encryptText(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD))));
            }
            // _channelUserVO.setEmail(emailID);
            _channelUserVO.setMsisdn(_recVO.getMsisdn());
            _channelUserVO.setNetworkID(p_requestVO.getExternalNetworkCode());
            boolean isownerIDNew = false;
            isownerIDNew = true;
            _channelUserVO.setParentID(_senderVO.getUserID());
            _channelUserVO.setOwnerID(_senderVO.getOwnerID());
            ChannelTransferRuleVO channelTransferRuleVO = null;
            boolean isTrfRuleExist = false;
            final ArrayList trfRule = new C2STransferDAO().loadC2SRulesListForChannelUserAssociation(con, _senderVO.getNetworkID());
            if (trfRule != null && trfRule.size() > 0) {
                for (int m = 0, n = trfRule.size(); m < n; m++) {
                    channelTransferRuleVO = (ChannelTransferRuleVO) trfRule.get(m);
                    if (_senderVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory()) && _channelUserVO.getCategoryCode().equals(
                                    channelTransferRuleVO.getToCategory())) {
                        isTrfRuleExist = true;
                        break;
                    }
                }
            } else {
                msg[0] = _channelUserVO.getCategoryName();
                // throw exception transfer rule not exist.
                throw new BTSLBaseException("ChannelUserRegZainNigeriaRequestHandler", "process", PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, msg);
            }
            if (!isTrfRuleExist) {
                // msg[0]=parentChannelUserVO.getCategoryVO().getCategoryName();
                msg[1] = _channelUserVO.getCategoryName();
                // throw exception transfer rule not exist.
                throw new BTSLBaseException("ChannelUserRegZainNigeriaRequestHandler", "process", PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST_BTWEEN_CATEGORIES, msg);
            }

            // geography type validation

            _channelUserVO.setAppointmentDate(new Date());

            _channelUserVO.setUserID(generateUserId(_senderVO.getNetworkID(), _categoryVO.getUserIdPrefix()));
            if (isownerIDNew) {
                _channelUserVO.setOwnerID(_channelUserVO.getUserID());
            }
            final GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
            final ArrayList geoList = geoDAO.loadGeoDomainCodeHeirarchy(con, _categoryVO.getGrphDomainType(), _senderVO.getGeographicalCode(), true);
            GeographicalDomainVO geographicalDomainVO = null;
            // for(int i=0;i<geoList.size();i++)
            // {
            geographicalDomainVO = (GeographicalDomainVO) geoList.get(0);
            // if(geographicalDomainVO.getGrphDomainType().equals(_categoryVO.getGrphDomainType()))
            // break;
            // }
            final ArrayList geographyList = new ArrayList();
            final UserGeographiesVO myVO = new UserGeographiesVO();
            myVO.setGraphDomainCode(geographicalDomainVO.getGrphDomainCode());
            myVO.setUserId(_channelUserVO.getUserID());
            geographyList.add(myVO);

            // others check
            /*
             * If USR_APPROVAL_LEVEL = 0 no approval required, if
             * USR_APPROVAL_LEVEL = 1 level 1 approval required,
             * if USR_APPROVAL_LEVEL = 2 level 2 approval required'
             * While adding user check whether the approval is required or not
             * if(USR_APPROVAL_LEVEL > 0 )
             * set status = N(New)//approval required
             * else
             * set status = Y(Active)
             */
            if (((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.USR_APPROVAL_LEVEL)).intValue() > 0) {
                _channelUserVO.setStatus(PretupsI.USER_STATUS_NEW);// N New
                _channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// N
                // New

            } else {
                _channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// Y
                // Active
                _channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);// Y
                // Active
            }

            // set some usefull parameter
            final Date currentDate = new Date();
            _channelUserVO.setPasswordModifiedOn(currentDate);
            _channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);
            _channelUserVO.setCreationType(PretupsI.STK_SYSTEM_USR_CREATION_TYPE);
            // _channelUserVO.setEmpCode(BTSLUtil.NullToString((String)requestMap.get("SUBSCRIBERCODE")));
            // _channelUserVO.setContactNo(BTSLUtil.NullToString((String)requestMap.get("CONTACTNUMBER")));
            // _channelUserVO.setDesignation(BTSLUtil.NullToString((String)requestMap.get("DESIGNATION")));
            // _channelUserVO.setContactPerson(BTSLUtil.NullToString((String)requestMap.get("CONTACTPERSON")));
            // _channelUserVO.setSsn(BTSLUtil.NullToString((String)requestMap.get("SSN")));
            // _channelUserVO.setAddress1(BTSLUtil.NullToString((String)requestMap.get("ADDRESS1")));
            // _channelUserVO.setAddress2(BTSLUtil.NullToString((String)requestMap.get("ADDRESS2")));
            // _channelUserVO.setCity(BTSLUtil.NullToString((String)requestMap.get("CITY")));
            // _channelUserVO.setState(BTSLUtil.NullToString((String)requestMap.get("STATE")));
            // _channelUserVO.setCountry(BTSLUtil.NullToString((String)requestMap.get("COUNTRY")));

            _channelUserVO.setCreatedBy(PretupsI.SYSTEM_USER);
            _channelUserVO.setCreatedOn(currentDate);
            _channelUserVO.setModifiedBy(PretupsI.SYSTEM_USER);
            _channelUserVO.setModifiedOn(currentDate);
            _channelUserVO.setUserProfileID(_channelUserVO.getUserID());
            _channelUserVO.setPasswordModifiedOn(currentDate);
            _channelUserVO.setPasswordCountUpdatedOn(currentDate);

            // insert in to user table
            _channelUserVO.setNetworkID(_senderVO.getNetworkID());
            _channelUserVO.setNetworkCode(_senderVO.getNetworkCode());
            final int userCount = new UserDAO().addUser(con, _channelUserVO);
            if (userCount <= 0) {
               mcomCon.partialRollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChannelUserRegZainNigeriaRequestHandler[process]",
                                "", "", "", "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_REGISTRATION_FAILED);
            }
            final UserPhoneVO phoneVO = new UserPhoneVO();
            final ArrayList phoneList = new ArrayList();
            phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(_channelUserVO.getMsisdn()));
            phoneVO.setPrimaryNumber(PretupsI.YES);
            phoneVO.setSmsPin(BTSLUtil.encryptText(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN))));
            phoneVO.setPinRequired(PretupsI.YES);
            phoneVO.setPhoneProfile(_categoryVO.getCategoryCode());
            phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
            phoneVO.setUserId(_channelUserVO.getUserID());
            // set the default values
            phoneVO.setCreatedBy(_senderVO.getUserID());
            phoneVO.setModifiedBy(_senderVO.getUserID());
            phoneVO.setCreatedOn(currentDate);
            phoneVO.setModifiedOn(currentDate);
            phoneVO.setPinModifiedOn(currentDate);
            phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
            final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
            phoneVO.setPrefixID(prefixVO.getPrefixID());
            phoneList.add(phoneVO);

            final int phoneCount = new UserDAO().addUserPhoneList(con, phoneList);
            if (phoneCount <= 0) {
                mcomCon.partialRollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChannelUserRegZainNigeriaRequestHandler[process]",
                                "", "", "", "Exception:Update count <=0 for user phones");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_REGISTRATION_FAILED);
            }
            /*
             * If user status is Y(active) means user is activaed at the
             * creation time, so we are setting the
             * activated_on = currentDate. This indicate user is actived on the
             * same date
             */
            if (PretupsI.USER_STATUS_ACTIVE.equals(_channelUserVO.getStatus())) {
                _channelUserVO.setActivatedOn(currentDate);
            } else {
                _channelUserVO.setActivatedOn(null);
            }
            _channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
            _channelUserVO.setMpayProfileID("");
            _channelUserVO.setLowBalAlertAllow("N");
            _channelUserVO.setInSuspend("N");
            _channelUserVO.setOutSuspened("N");

            // insert data into channelusers table
            final int userChannelCount = channelUserDAO.addChannelUser(con, _channelUserVO);

            if (userChannelCount <= 0) {
                mcomCon.partialRollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChannelUserRegZainNigeriaRequestHandler[process]",
                                "", "", "", "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_REGISTRATION_FAILED);
            }

            if (geographyList != null && geographyList.size() > 0) {
                final int geographyCount = new UserGeographiesDAO().addUserGeographyList(con, geographyList);
                if (geographyCount <= 0) {
                    try {
                        mcomCon.partialRollback();
                    } catch (SQLException e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                                    "ChannelUserRegZainNigeriaRequestHandler[process]", "", "", "", "Exception:Update count <=0 ");
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_REGISTRATION_FAILED);
                }
            }

            mcomCon.finalCommit();
            final String senderReturnMessage = BTSLUtil.getMessage(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()),
                            PretupsErrorCodesI.USER_REGISTRATION_SUCCESS, new String[0]);
            p_requestVO.setSenderReturnMessage(senderReturnMessage);

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserRegZainNigeriaRequestHandler[process]", "",
                            "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChannelUserRegZainNigeriaRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * Method to generate the userId while inserting new record
     * 
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return String
     */
    private String generateUserId(String p_networkCode, String p_prefix) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateUserId", "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix);
        }
        final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, p_networkCode)) + "", length);
        id = p_networkCode + p_prefix + id;
        if (_log.isDebugEnabled()) {
            _log.debug("generateUserId", "Exiting id=" + id);
        }
        return id;
    }
}
