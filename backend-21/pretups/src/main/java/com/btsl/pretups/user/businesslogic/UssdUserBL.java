package com.btsl.pretups.user.businesslogic;

/**
 * @(#)UssdUserBL.java
 *                     Copyright(c) 2010, Comviva Technologies Ltd.
 *                     All Rights Reserved
 * 
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Ashish Kumar Todia 24Sept10 Creation.
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     This class is used for Ussd User Creation Bussiness
 *                     logics.
 * 
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class UssdUserBL {

    private static Log _log = LogFactory.getLog(UssdUserBL.class.getName());
    private static UssdUserDAO _ussdUserDao = new UssdUserDAO();
    private static UserDAO _userdao = new UserDAO();
    private static ServicesTypeDAO _servicedao = new ServicesTypeDAO();
    private static CategoryDAO _categorydao = new CategoryDAO();
    private static ChannelUserDAO _channelUserdao = new ChannelUserDAO();
    private static GeographicalDomainDAO _geographichalDoamindao = new GeographicalDomainDAO();
    private static UserGeographiesDAO _userGeographiesDAO = new UserGeographiesDAO();
    private static UserRolesDAO _userroledao = new UserRolesDAO();

    /**
	 * to ensure no class instantiation 
	 */
    private UssdUserBL() {
        
    }
    
    /**
     * Method to validate the creation of users in system.
     * 
     * @param p_con
     * @param p_categoryCode
     * @return
     */
    public static boolean validateUserCreationByCategoryCode(Connection p_con, String p_fromCategoryCode, String p_toCategoryCode, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateCategoryCode", "Entered p_fromCategoryCode:" + p_fromCategoryCode + "p_toCategoryCode " + p_toCategoryCode + "p_networkCode " + p_networkCode);
        }
        final String METHOD_NAME = "validateUserCreationByCategoryCode";
        boolean isValid = false;
        try {
            isValid = _ussdUserDao.checkValidUserCreation(p_con, p_fromCategoryCode, p_toCategoryCode, p_networkCode);

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("UssdUserBL", "validateCategoryCode", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateCategoryCode", "Exiting Status:" + isValid);
        }
        return isValid;
    }

    /**
     * To load the users details
     * 
     * @param p_con
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     */
    public static ChannelUserVO loadUserDetail(Connection p_con, String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserDetail", "Entered p_msisdn:" + p_msisdn);
        }
        ChannelUserVO _channelUserVO = null;
        final String METHOD_NAME = "loadUserDetail";
        try {
            _channelUserVO = (ChannelUserVO) _ussdUserDao.loadUsersDetails(p_con, p_msisdn);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("UssdUserBL", "loadUserDetail", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
        }

        return _channelUserVO;
    }

    /**
     * To Check whether the category exists in the parent users domain.
     * 
     * @param p_con
     * @param p_categoryCode
     * @param p_domainCode
     * @return
     * @throws BTSLBaseException
     */
    public static boolean validateCategoryCode(Connection p_con, String p_categoryCode, String p_domainCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateCategoryCode", "Entered p_categoryCode:" + p_categoryCode + "p_domainCode " + p_domainCode);
        }
        boolean isValid = false;
        final String METHOD_NAME = "validateCategoryCode";
        try {
            isValid = _ussdUserDao.checkCategoryCode(p_con, p_categoryCode, p_domainCode);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("UssdUserBL", "validateCategoryCode", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateCategoryCode", "Exiting Status:" + isValid);
        }
        return isValid;
    }

    /**
     * Method to add the users details in the users table.
     * 
     * @param p_con
     * @param p_channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    public static int addUserDetails(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addUserDetails", "Entered p_channelUserVO:" + p_channelUserVO.getMsisdn());
        }
        final String METHOD_NAME = "addUserDetails";
        int count = 0;
        CategoryVO _categoryVO = null;
        try {
            // to generate userId here.
            _categoryVO = _categorydao.loadCategoryDetailsByCategoryCode(p_con, p_channelUserVO.getCategoryCode());
            p_channelUserVO.setCategoryVO(_categoryVO);
            if (p_channelUserVO.getCategoryVO().getWebInterfaceAllowed().equalsIgnoreCase(PretupsI.NO)) {
                p_channelUserVO.setLoginID("");
                p_channelUserVO.setPassword("");
            }
            p_channelUserVO.setUserID(UssdUserBL.generateUserId(p_channelUserVO.getNetworkID(), p_channelUserVO.getCategoryVO().getUserIdPrefix()));
            count = _userdao.addUser(p_con, p_channelUserVO);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("UssdUserBL", "addDetailUsers", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addUserDetails", "Exiting count:" + count);
        }
        return count;
    }

    /**
     * Method to add channel User entries.
     * 
     * @param p_con
     * @param p_channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    public static int addChannelUserDetaile(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addChannelUserDetaile", "Entered p_channelUserVO:" + p_channelUserVO.getMsisdn());
        }
        final String METHOD_NAME = "addChannelUserDetaile";
        int count = 0;
        try {
            p_channelUserVO.setInSuspend(PretupsI.NO);
            p_channelUserVO.setOutSuspened(PretupsI.NO);
            p_channelUserVO.setApplicationID("2");
            p_channelUserVO.setUserProfileID(p_channelUserVO.getUserID());
            p_channelUserVO.setOutletCode("");
            p_channelUserVO.setSubOutletCode("");
            p_channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
            p_channelUserVO.setMpayProfileID("");
            p_channelUserVO.setLowBalAlertAllow(PretupsI.NO);

            count = _channelUserdao.addChannelUser(p_con, p_channelUserVO);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addChannelUserDetaile", "Exiting count:" + count);
        }
        return count;
    }

    /**
     * This method will add user geography details in database.
     * 
     * @param p_con
     * @param p_geographyCode
     * @return
     * @throws BTSLBaseException
     */
    public static int addUserGeograpicalDomain(Connection p_con, ChannelUserVO p_ChannelUserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addUserGeograpicalDomain", "Entered p_ChannelUserVO:" + p_ChannelUserVO.getMsisdn());
        }
        int count = 0;
        final String METHOD_NAME = "addUserGeograpicalDomain";
        final ArrayList geographyList = new ArrayList();
        final UserGeographiesVO _usergeographyVo = new UserGeographiesVO();
        _usergeographyVo.setUserId(p_ChannelUserVO.getUserID());
        _usergeographyVo.setGraphDomainCode(p_ChannelUserVO.getGeographicalCode());
        geographyList.add(_usergeographyVo);
        try {
            count = _userGeographiesDAO.addUserGeographyList(p_con, geographyList);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("UssdUserBL", "addUserGeograpicalDomain", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addUserGeograpicalDomain", "Exiting count:" + count);
        }
        return count;
    }

    /**
     * This method will add the user serivces category based.
     * 
     * @param p_con
     * @param p_userId
     * @param p_categoryCode
     * @return
     * @throws BTSLBaseException
     */
    public static int addUserServices(Connection p_con, ChannelUserVO p_channelUserVO, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addUserServices", "Entered p_channelUserVO:" + p_channelUserVO + " p_module= " + p_module);
        }
        int count = 0;
        final String METHOD_NAME = "addUserServices";
        String[] _serivceArray = null;
        final String p_userId = p_channelUserVO.getUserID();
        final String p_categoryCode = p_channelUserVO.getCategoryCode();
        final String p_networkCode = p_channelUserVO.getNetworkID();
        if (_log.isDebugEnabled()) {
            _log.debug("addUserServices ", "Entered p_userId: " + p_userId + "p_categoryCode " + p_categoryCode + "p_module " + p_module);
        }
        ArrayList<ListValueVO> _serviceList = null;
        try {
            // to load the default serices for a category.
            _serviceList = (ArrayList<ListValueVO>) _servicedao.loadServicesList(p_con, p_networkCode, p_module, p_categoryCode, false);
            if (_log.isDebugEnabled()) {
                _log.debug("addUserServices", "_serviceList= " + _serviceList);
            }
            if (_serviceList != null && _serviceList.size() > 0) {
                _serivceArray = new String[_serviceList.size()];
                for (int i = 0, j = _serviceList.size(); i < j; i++) {
                    final ListValueVO listVO = (ListValueVO) _serviceList.get(i);
                    _serivceArray[i] = listVO.getValue();
                }
            }
            if (_serivceArray != null && _serivceArray.length > 0) {
                count = _servicedao.addUserServicesList(p_con, p_userId, _serivceArray, PretupsI.YES);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("UssdUserBL", "addUserServices", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addUserServices", "Exiting count:" + count);
        }
        return count;
    }

    /**
     * This method will add the users details in the user_roles table.
     * 
     * @param p_con
     * @param p_channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    public static int addUserRolesDetails(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addUserRolesDetails ", "Entered p_channelUserVO(MSISDN) : " + p_channelUserVO.getMsisdn() + " defaultRole : " + p_channelUserVO.getGroupRoleCode());
        }
        int count = 0;
        final String METHOD_NAME = "addUserRolesDetails";

        String[] _defaultroleList = null;
        try {
            _defaultroleList = new String[1];
            _defaultroleList[0] = p_channelUserVO.getGroupRoleCode();
            count = _userroledao.addUserRolesList(p_con, p_channelUserVO.getUserID(), _defaultroleList);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("UssdUserBL", "addUserRolesDetails", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
        }
        return count;
    }

    /**
     * Method to add the entries in the user_phones table.
     * 
     * @param p_con
     * @param p_userId
     * @param p_categoryCode
     * @param p_networkCode
     * @param p_module
     * @return
     * @throws BTSLBaseException
     */
    public static int addUserPhoneDetails(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addUserPhoneDetails ", "Entered p_channelUserVO(MSISDN) : " + p_channelUserVO.getMsisdn());
        }
        int count = 0;
        final String METHOD_NAME = "addUserPhoneDetails";
        final UserPhoneVO _userPhone = new UserPhoneVO();
        final ArrayList phoneList = new ArrayList();
        try {
            _userPhone.setMsisdn(PretupsBL.getFilteredMSISDN(p_channelUserVO.getMsisdn()));
            _userPhone.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
            _userPhone.setUserId(p_channelUserVO.getUserID());
            _userPhone.setCreatedBy(p_channelUserVO.getCreatedBy());
            _userPhone.setModifiedBy(p_channelUserVO.getCreatedBy());
            _userPhone.setCreatedOn(p_channelUserVO.getCreatedOn());
            _userPhone.setModifiedOn(p_channelUserVO.getCreatedOn());
            _userPhone.setPinModifiedOn(p_channelUserVO.getCreatedOn());
            _userPhone.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            _userPhone.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
            final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL
                            .getMSISDNPrefix(PretupsBL.getFilteredMSISDN(p_channelUserVO.getMsisdn())));
            _userPhone.setPrefixID(prefixVO.getPrefixID());
            _userPhone.setPrimaryNumber(PretupsI.YES);
            _userPhone.setSmsPin(p_channelUserVO.getSmsPin()); // sms pin from
            // channelUserVO.
            _userPhone.setPinRequired(PretupsI.YES);
            final ArrayList<ListValueVO> _userPhoneProfile = _userdao.loadPhoneProfileList(p_con, p_channelUserVO.getCategoryCode());
            _userPhone.setPhoneProfile(_userPhoneProfile.get(0).getValue());
            p_channelUserVO.setUserPhoneVO(_userPhone);
            phoneList.add(_userPhone);
            p_channelUserVO.setMsisdnList(phoneList);
            count = _userdao.addUserPhoneList(p_con, phoneList);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("UssdUserBL", "addUserPhoneDetails", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("addUserPhoneDetails", "Exiting count:" + count);
        }
        return count;
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
    private static String generateUserId(String p_networkCode, String p_prefix) throws Exception {
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

    /**
     * Method to set all(grades/commission/transferprofile/geography/roles) the
     * default values.
     * 
     * @param p_con
     * @param p_channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    public static HashMap<String, String> setDefaultProfiles(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "setDefaultProfiles";
        if (_log.isDebugEnabled()) {
            _log.debug("setDefaultProfiles ", "Entered p_channelUserVO(MSISDN) : " + p_channelUserVO.getMsisdn());
        }

        final HashMap<String, String> defaultValueMap = new HashMap<String, String>();
        try {
            final String categoryCode = p_channelUserVO.getCategoryCode();
            final String defaultGrade = _ussdUserDao.loadDefaultGrade(p_con, categoryCode);
            defaultValueMap.put("defaultGrade", defaultGrade);
            final String defaultCommission = _ussdUserDao.loadDefaultCommissionProfile(p_con, categoryCode);
            defaultValueMap.put("defaultCommission", defaultCommission);
            final String defaultTrasfPrf = _ussdUserDao.loadDetaultTransferProfile(p_con, categoryCode);
            defaultValueMap.put("defaultTrasfPrf", defaultTrasfPrf);
            final String defaultGeo = _ussdUserDao.loadDefaultGeography(p_con, p_channelUserVO.getParentMsisdn(), categoryCode);
            defaultValueMap.put("defaultGeo", defaultGeo);
            final String defaultRoleCode = _ussdUserDao.loadDefaultRole(p_con, categoryCode);
            defaultValueMap.put("defaultRoleCode", defaultRoleCode);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("setDefaultProfiles", "Exiting defaultValueMap:" + defaultValueMap);
        }
        return defaultValueMap;
    }

    /**
     * Method to check whether the parent user is in the hierarchy of the sender
     * user.
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_parentUserVO
     * @return
     * @throws BTSLBaseException
     */
    public static boolean checkParentInSenderHierarchy(Connection p_con, ChannelUserVO p_senderVO, ChannelUserVO p_parentUserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("checkParentInSenderHierarchy ", "Entered p_senderVO : " + p_senderVO + " p_parentUserVO: " + p_parentUserVO);
        }
        boolean isValid = false;
        final String METHOD_NAME = "checkParentInSenderHierarchy";
        ChannelUserVO listValueVO = null;
        final String parentUserId = p_parentUserVO.getUserID();
        final String parentMsisdn = p_parentUserVO.getMsisdn();
        final String[] arr = new String[1];
        arr[0] = p_senderVO.getUserID();
        try {
            final ArrayList<ChannelUserVO> hierarchyList = _channelUserdao.loadUserHierarchyList(p_con, arr, PretupsI.ALL, PretupsI.STATUS_EQUAL, PretupsI.YES, p_senderVO
                            .getCategoryCode());
            final Iterator<ChannelUserVO> it = hierarchyList.iterator();
            while (it.hasNext()) {
                listValueVO = (ChannelUserVO) it.next();
                if (_log.isDebugEnabled()) {
                    _log.debug("checkParentInSenderHierarchy", "listValueVO " + listValueVO);
                }
                if ((parentMsisdn.equalsIgnoreCase(listValueVO.getMsisdn())) && (parentUserId.equalsIgnoreCase(listValueVO.getUserID()))) {
                    isValid = true;
                }
            }
            // to check the sender user is iteslf the parent user.
            if (!isValid) {
                if ((p_parentUserVO.getUserID().equalsIgnoreCase(p_senderVO.getUserID())) && (p_parentUserVO.getCategoryCode().equalsIgnoreCase(p_senderVO.getCategoryCode()))) {
                    isValid = true;
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("checkParentInSenderHierarchy", "Exiting isValid:" + isValid);
        }
        return isValid;
    }

}
