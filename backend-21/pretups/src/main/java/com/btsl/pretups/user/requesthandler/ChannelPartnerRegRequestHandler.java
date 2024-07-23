/**
 * @(#)ChannelPartnerRegRequestHandler.java
 *                                          This controller is used for creation
 *                                          of Channel user by the STK or SMS of
 *                                          Channel Admin
 *                                          ------------------------------------
 *                                          ------------------------------------
 *                                          -------------------------
 *                                          Author Date History
 *                                          ------------------------------------
 *                                          ------------------------------------
 *                                          -------------------------
 *                                          Vikas Kumar 13-May-2012 Initital
 *                                          Creation
 *                                          ------------------------------------
 *                                          ------------------------------------
 *                                          -------------------------
 *                                          Copyright(c) 2012, Comviva
 *                                          Technologies Limited
 *                                          All Rights Reserved
 */

package com.btsl.pretups.user.requesthandler;

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
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;
import com.txn.pretups.channel.profile.businesslogic.TransferProfileTxnDAO;
import com.txn.pretups.master.businesslogic.GeographicalDomainTxnDAO;
import com.txn.pretups.roles.businesslogic.UserRolesTxnDAO;
import com.txn.user.businesslogic.UserTxnDAO;

public class ChannelPartnerRegRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(ChannelPartnerRegRequestHandler.class.getName());
    private ChannelUserVO _senderUserVO = null;
    private ChannelUserVO _parentUserVO = null;
    private ChannelUserVO _channelUserVO = null;
    private CategoryVO _categoryVO = null;
    private ReceiverVO _recVO = null;
    private String _parentFilteredMSISDN = null;
    private String _channelUserFilteredMSISDN = null;

    /**
     * Business rules for Channel Partner registration
     * //KEYWORD<SPACE>PARENTMSISDN<SPACE>USERMSISDN<SPACE>USERNAME
     * //KEYWORD<SPACE>PARENTMSISDN<SPACE>USERMSISDN<SPACE>USERNAME<SPACE>RET
     * //CPREG 900001001 900001101 testDealer
     * //CPREG 900001001 900001002 testRetailer RET
     * 
     * //Message Format Validation
     * //Parent MSISDN validation
     * //if valid ,load user details from the database
     * 
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process::::ChannelPartnerRegRequestHandler", "Entered p_requestVO: " + p_requestVO);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserDAO channelUserDAO = null;
        CategoryDAO categoryDAO = null;
        DomainDAO domainDAO = null;
        _channelUserVO = new ChannelUserVO();
        final String msg[] = new String[8];
        _recVO = new ReceiverVO();
        UserDAO userDAO = null;
        final UserTxnDAO usertxnDAO = new UserTxnDAO();

        // String parentFilteredMSISDN=null;
        // CPREG+90343437+testdealer+2468

        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final String[] requestArr = p_requestVO.getRequestMessageArray();
            // Message length validation
            if ((requestArr.length != 4) && (requestArr.length != 5)) {
                throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.CHN_USR_REG_INVALID_REQUEST_FORMAT);
            }
            try {
                _parentFilteredMSISDN = PretupsBL.getFilteredMSISDN(requestArr[1].trim());
                PretupsBL.validateMsisdn(con, _recVO, p_requestVO.getRequestIDStr(), _parentFilteredMSISDN);
            } catch (BTSLBaseException ex) {
                _log.errorTrace(METHOD_NAME, ex);
                throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.CHN_USR_REG_INVALID_PARENT_MSISDN);
            }
            // Parent MSISDN and details load
            channelUserDAO = new ChannelUserDAO();
            _parentUserVO = channelUserDAO.loadChannelUserDetails(con, _parentFilteredMSISDN);
            msg[0] = _parentFilteredMSISDN;
            if (_parentUserVO == null) {
                throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.CHN_USR_REG_PARENT_DETAILS_NOT_FOUND);
            }

            _senderUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            msg[1] = _senderUserVO.getMsisdn();
            /*
             * //Check the sender allowed for this service
             * 
             * if(!BTSLUtil.isNullString(_senderUserVO.getServiceTypes()))
             * flag =new CategoryServiceDAO().isSenderServiceAllowed(con,
             * _senderUserVO.getCategoryCode(),
             * _senderUserVO.getServiceTypes());
             * 
             * if(!flag){
             * throw new
             * BTSLBaseException("ChannelPartnerRegRequestHandler","process"
             * ,PretupsErrorCodesI.CHN_USR_REG_SENDER_SERVICE_NOT_ALLOWED);
             * }
             */

            // Channel User MSISDN validation
            try {
                PretupsBL.validateMsisdn(con, _recVO, p_requestVO.getRequestIDStr(), requestArr[2]);
            } catch (BTSLBaseException e) {
                _log.errorTrace(METHOD_NAME, e);
                throw e;
            }
            categoryDAO = new CategoryDAO();
            domainDAO = new DomainDAO();
            ArrayList catList = null;
            final UserPhoneVO userPhoneVO = (UserPhoneVO) _parentUserVO.getUserPhoneVO();
            boolean categoryFoundFlag = false;
            ListValueVO categoryValueVO = null;

            final DomainVO domainVO = domainDAO.loadDomainVO(con, _parentUserVO.getCategoryVO().getDomainCodeforCategory());
            if (_parentUserVO.getCategoryVO().getSequenceNumber() < Integer.parseInt(domainVO.getNumberOfCategories())) {
                catList = categoryDAO.loadCategoryHierarchyList(con, _parentUserVO.getCategoryVO().getDomainCodeforCategory(), _parentUserVO.getCategoryVO()
                                .getSequenceNumber() + 1);
            } else {
                throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.CHN_USR_REG_LEAF_CATEGORY);
            }

            String categoryCode = null;

            if (requestArr.length == 5) {
                if (!BTSLUtil.isNullString(requestArr[4])) {
                    categoryCode = requestArr[4].trim();
                    if (catList != null) {
                    	int catListSize = catList.size();
                        for (int m = 0; m < catListSize; m++) {

                            categoryValueVO = (ListValueVO) catList.get(m);
                            if (categoryValueVO.getValue().equalsIgnoreCase(categoryCode)) {
                                categoryFoundFlag = true;
                                break;
                            }
                        }
                    }

                    if (!categoryFoundFlag) {
                        throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.CHN_USR_REG_INVALID_CATEGORY);
                    }
                }
            }
            // load category
            final String categoryAllowed = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CATEGORY_ALLOWED_FOR_CREATION));
            final String[] categoryAllowedArray = categoryAllowed.split(",");
            boolean preferenceAllowedFlag = false;
            if (_log.isDebugEnabled()) {
                _log.debug("process::::ChannelPartnerRegRequestHandler", "Entered categoryCode: " + categoryCode);
            }
            for (final String category : categoryAllowedArray) {
                if (!BTSLUtil.isNullString(categoryCode)) {
                    if (category.equalsIgnoreCase(categoryCode)) {
                        preferenceAllowedFlag = true;
                        break;
                    }
                } else {
                    if (category.equalsIgnoreCase(((ListValueVO) catList.get(0)).getValue())) {
                        preferenceAllowedFlag = true;
                        break;
                    }
                }
            }
            if (!preferenceAllowedFlag) {
                throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.CHN_USR_REG_NOT_ALLOWED_CATEGORY);
            }
            if (BTSLUtil.isNullString(categoryCode)) {
                _categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con, ((ListValueVO) catList.get(0)).getValue());
            } else {
                _categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con, categoryCode);
            }

            _channelUserVO.setCategoryVO(_categoryVO);
            _channelUserVO.setCategoryCode(_categoryVO.getCategoryCode());
            _channelUserVO.setCategoryName(_categoryVO.getCategoryName());
            _channelUserVO.setDomainTypeCode(_categoryVO.getDomainTypeCode());
            // username set
            _channelUserVO.setUserName(requestArr[3].trim());
            // short name set
            if (requestArr[3].length() > 15) {
                _channelUserVO.setShortName(requestArr[3].substring(0, 15));
            } else {
                _channelUserVO.setShortName(requestArr[3].trim());
            }

            _channelUserVO.setMsisdn(_recVO.getMsisdn());

            // user prefix check
            _channelUserVO.setUserNamePrefix("MR");
            // login id unique check
            String newUserLoginId = _recVO.getMsisdn();
            newUserLoginId = newUserLoginId.trim();
            msg[2] = newUserLoginId.trim();

            userDAO = new UserDAO();
            if (userDAO.isMSISDNExist(con, _channelUserVO.getMsisdn(), null)) {
                throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.ERROR_CHNL_USER_MSISDN_ALREADY_EXIST);
            }
            if (userDAO.isUserLoginExist(con, newUserLoginId, null)) {
                throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.ERROR_CHNL_USER_LOGINID_ALREADY_EXIST);
            }

            if (_categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                _channelUserVO.setLoginID(newUserLoginId);
                _channelUserVO.setPassword(BTSLUtil.encryptText(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD))));
            }

            _channelUserVO.setNetworkID(p_requestVO.getExternalNetworkCode());
            _channelUserVO.setParentID(_parentUserVO.getUserID());
            _channelUserVO.setOwnerID(_parentUserVO.getOwnerID());
            ChannelTransferRuleVO channelTransferRuleVO = null;
            boolean isTrfRuleExist = false;
            final ArrayList trfRule = new C2STransferDAO().loadC2SRulesListForChannelUserAssociation(con, _parentUserVO.getNetworkID());
            msg[3] = _channelUserVO.getCategoryName();
            if (trfRule != null && trfRule.size() > 0) {
            	int trfRuleSize = trfRule.size();
                for (int m = 0, n = trfRuleSize; m < n; m++) {
                    channelTransferRuleVO = (ChannelTransferRuleVO) trfRule.get(m);
                    if (_parentUserVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory()) && _channelUserVO.getCategoryCode().equals(
                                    channelTransferRuleVO.getToCategory())) {
                        isTrfRuleExist = true;
                        break;
                    }
                }
            } else {
                // throw exception transfer rule not exist.
                throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, msg);
            }
            if (!isTrfRuleExist) {
                // throw exception transfer rule not exist.
                throw new BTSLBaseException("ChannelPartnerRegRequestHandler", "process", PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST_BTWEEN_CATEGORIES, msg);
            }
            // geography type validation
            final Date currentDate = new Date();
            _channelUserVO.setAppointmentDate(currentDate);
            _channelUserVO.setUserID(generateUserId(_parentUserVO.getNetworkID(), _categoryVO.getUserIdPrefix()));
            _channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// Y Active
            _channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);// Y
            // Active
            // set some usefull parameter

            _channelUserVO.setPasswordModifiedOn(currentDate);
            _channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);
            _channelUserVO.setCreationType(PretupsI.STK_SYSTEM_USR_CREATION_TYPE);
            _channelUserVO.setCreatedBy(_senderUserVO.getUserID());
            _channelUserVO.setCreatedOn(currentDate);
            _channelUserVO.setModifiedBy(_senderUserVO.getUserID());
            _channelUserVO.setModifiedOn(currentDate);
            _channelUserVO.setUserProfileID(_channelUserVO.getUserID());
            _channelUserVO.setPasswordModifiedOn(currentDate);
            _channelUserVO.setPasswordCountUpdatedOn(currentDate);
            _channelUserFilteredMSISDN = PretupsBL.getFilteredMSISDN(_channelUserVO.getMsisdn());
            // insert in to user table
            _channelUserVO.setNetworkID(_parentUserVO.getNetworkID());
            _channelUserVO.setNetworkCode(_recVO.getNetworkCode());
            _channelUserVO.setUserCode(_channelUserFilteredMSISDN);
            final int userCount = userDAO.addUser(con, _channelUserVO);
            if (userCount <= 0) {
                mcomCon.partialRollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChannelPartnerRegRequestHandler[process]", "", "",
                                "", "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_FAILED);
            }
            msg[4] = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD));
            msg[5] = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN));
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
            phoneVO.setCreatedBy(_senderUserVO.getUserID());
            phoneVO.setModifiedBy(_senderUserVO.getUserID());
            phoneVO.setCreatedOn(currentDate);
            phoneVO.setModifiedOn(currentDate);
            phoneVO.setPinModifiedOn(currentDate);
            phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
            final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
            phoneVO.setPrefixID(prefixVO.getPrefixID());
            phoneList.add(phoneVO);

            final int phoneCount = userDAO.addUserPhoneList(con, phoneList);
            if (phoneCount <= 0) {
                mcomCon.partialRollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChannelPartnerRegRequestHandler[process]", "", "",
                                "", "Exception:Update count <=0 for user phones");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_FAILED);
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

            // transfercontrolprofile
            final TransferProfileVO transferProfileVO = new TransferProfileTxnDAO().loadDefaultTrfProfileForCategoryCode(con, _channelUserVO.getCategoryCode(), _channelUserVO
                            .getNetworkCode());
            // transferProfileVO.getProfileId();
            if (transferProfileVO == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_DEF_TC_PRF_NOT_FOUND);
            } else {
                _channelUserVO.setTransferProfileID(transferProfileVO.getProfileId());
            }
            // CommissionProfileDAO commissionProfileDAO =new
            // CommissionProfileDAO();
            final CommissionProfileTxnDAO CommissionProfileTxnDAO = new CommissionProfileTxnDAO();
            // Commission profile
            final CommissionProfileSetVO commissionProfileSetVO = CommissionProfileTxnDAO.loadDefaultCommissionProfileSetForCategory(con, _channelUserVO.getNetworkCode(),
                            _channelUserVO.getCategoryCode());
            if (commissionProfileSetVO == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_DEF_COMM_PRF_NOT_FOUND);
            } else {
                _channelUserVO.setCommissionProfileSetID(commissionProfileSetVO.getCommProfileSetId());
            }
            // channel grade
            final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
            final GradeVO gradeVO = categoryGradeDAO.loadDefaultGradeListForCategory(con, _channelUserVO.getCategoryCode());
            if (gradeVO == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_DEF_GRADE_NOT_FOUND);
            } else {
                _channelUserVO.setUserGrade(gradeVO.getGradeCode());
            }

            // insert data into channelusers table
            final int userChannelCount = channelUserDAO.addChannelUser(con, _channelUserVO);

            if (userChannelCount <= 0) {
              mcomCon.partialRollback();;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChannelPartnerRegRequestHandler[process]", "", "",
                                "", "Exception:Update userChannelCount <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_FAILED);
            }
            /*
             * load the geography of Parent channel user
             * Load the geography hierarchy list from geographies
             * According to category of created user ,select the default
             * geography of hierarchy
             * then assign the geography to channel user
             */UserGeographiesVO geographyVO = null;
            String geographycode = "";
            final ArrayList senderGeographyList = _senderUserVO.getGeographicalAreaList();
            if (senderGeographyList == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_SENDER_GEOGRAPHY_NOT_FOUND);
            } else {
                for (int m = 0; m < senderGeographyList.size(); m++) {

                    geographyVO = (UserGeographiesVO) senderGeographyList.get(m);
                    geographycode = geographycode + geographyVO.getGraphDomainCode() + "','";
                }
                geographycode = geographycode.substring(0, geographycode.length() - 3);
            }
            // senderGeographyList.
            final GeographicalDomainTxnDAO geographicalDomainTxnDAO = new GeographicalDomainTxnDAO();
            final ArrayList geoList = geographicalDomainTxnDAO.loadGeoDomainCodeHeirarchyForOpt(con, _categoryVO.getGrphDomainType(), geographycode, true);
            GeographicalDomainVO geographicalDomainVO = null;
            final ArrayList geographyList = new ArrayList();
            final UserGeographiesVO myVO = new UserGeographiesVO();
            boolean defGeoFlag = false;
            if (geoList == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_USER_GEOGRAPHY_NOT_FOUND);
            } else {
            	int geoListSize = geoList.size();
                for (int gd = 0; gd < geoListSize; gd++) {
                    geographicalDomainVO = (GeographicalDomainVO) geoList.get(gd);

                    if (geographicalDomainVO.getParentDomainCode().equalsIgnoreCase(_parentUserVO.getGeographicalCode()) && geographicalDomainVO.getIsDefault()
                                    .equalsIgnoreCase(PretupsI.YES)) {
                        myVO.setGraphDomainCode(geographicalDomainVO.getGrphDomainCode());
                        defGeoFlag = true;
                        break;
                    } else if (_parentUserVO.getCategoryVO().getGrphDomainType().equalsIgnoreCase(_categoryVO.getGrphDomainType()) && geographicalDomainVO.getIsDefault()
                                    .equalsIgnoreCase(PretupsI.YES)) {
                        myVO.setGraphDomainCode(geographicalDomainVO.getGrphDomainCode());
                        defGeoFlag = true;
                        break;
                    }
                }
            }
            if (!defGeoFlag) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_DEF_USER_GEOGRAPHY_NOT_FOUND);
            }
            myVO.setGraphDomainCode(geographicalDomainVO.getGrphDomainCode());
            myVO.setUserId(_channelUserVO.getUserID());
            geographyList.add(myVO);
            if (geographyList != null && geographyList.size() > 0) {
                final int geographyCount = new UserGeographiesDAO().addUserGeographyList(con, geographyList);
                if (geographyCount <= 0) {
                    try {
                        mcomCon.partialRollback();
                    } catch (SQLException e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChannelPartnerRegRequestHandler[process]", "",
                                    "", "", "Exception:Update count <=0 ");
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_FAILED);
                }
            }
            // end of geography

            // assign group roles
            final UserRolesDAO userRolesDAO = new UserRolesDAO();
            final UserRolesTxnDAO userRolesTxnDAO = new UserRolesTxnDAO();
            final String rolesString = userRolesTxnDAO.loadDefaultGroupRoleForCategory(con, _channelUserVO.getCategoryCode());
            if (rolesString == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_DEF_GROUP_ROLES_NOT_FOUND);
            } else {
                final String roles[] = rolesString.split(",");

                final int userRolesCount = userRolesDAO.addUserRolesList(con, _channelUserVO.getUserID(), roles);
                if (userRolesCount <= 0) {
                    mcomCon.partialRollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChannelPartnerRegRequestHandler[process]", "",
                                    "", "", "Exception:userRolesCount <=0 ");
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_FAILED);
                }

            }
            // assign services

            final ArrayList servicesList = usertxnDAO.loadCategoryServices(con, _channelUserVO.getCategoryCode(), _channelUserVO.getNetworkID());

            if (servicesList != null && servicesList.size() > 0) {

                final int userServicesCount = usertxnDAO.addChannelUserServices(con, servicesList, _channelUserVO.getUserID());
                if (userServicesCount <= 0) {
                    mcomCon.partialRollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChannelPartnerRegRequestHandler[process]", "",
                                    "", "", "Exception:userServicesCount <=0 ");
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHN_USR_REG_FAILED);
                }

            }
            // end of services
            mcomCon.finalCommit();
            PushMessage pushMessages = null;

            // pushMessages=(new
            // PushMessage(_senderPushMessageMsisdn,_c2sTransferVO.getSenderReturnMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_senderLocale));
            final String senderReturnMessage = BTSLUtil.getMessage(new Locale(_senderUserVO.getUserPhoneVO().getPhoneLanguage(), _senderUserVO.getUserPhoneVO().getCountry()),
                            PretupsErrorCodesI.CHN_USR_REG_SUCESS, msg);
            final String parentReturnMessage = BTSLUtil.getMessage(new Locale(_parentUserVO.getUserPhoneVO().getPhoneLanguage(), userPhoneVO.getCountry()),
                            PretupsErrorCodesI.CHN_USR_REG_SUCESS_P, msg);
            final String userReturnMessage = BTSLUtil.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
                            PretupsErrorCodesI.CHN_USR_REG_SUCESS_R, msg);

            // PushMessage pushMessage=new
            // PushMessage(requestVO.getMessageSentMsisdn(),senderMessage,requestVO.getRequestIDStr(),requestVO.getRequestGatewayCode(),requestVO.getLocale());
            p_requestVO.setSenderReturnMessage(senderReturnMessage);
            p_requestVO.setSuccessTxn(true);
            p_requestVO.setMessageCode(PretupsErrorCodesI.CHN_USR_REG_SUCESS);

            // Parent message sent
            pushMessages = (new PushMessage(_parentFilteredMSISDN, parentReturnMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO
                            .getLocale()));
            pushMessages.push();
            // channel user message sent
            pushMessages = (new PushMessage(_channelUserFilteredMSISDN, userReturnMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO
                            .getLocale()));
            pushMessages.push();

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelPartnerRegRequestHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChannelPartnerRegRequestHandler#process");
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
