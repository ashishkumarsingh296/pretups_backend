package com.restapi.networkadmin.operatorUser.serviceI;

import com.btsl.common.BaseResponse;
import com.btsl.common.*;
import com.btsl.event.*;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.GroupedUserRolesVO;
import com.btsl.pretups.channel.transfer.requesthandler.ViewUserRolesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OperatorUserLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.*;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.restapi.networkadmin.operatorUser.requestVO.AddOperatorUserRequestVO;
import com.restapi.networkadmin.operatorUser.requestVO.OperatorUserPhoneVO;
import com.restapi.networkadmin.operatorUser.responseVO.*;
import com.restapi.networkadmin.operatorUser.service.OperatorUserService;
import com.restapi.networkadmin.service.ModifyBatchC2SCardGroupServiceImpl;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.roles.businesslogic.UserRolesWebDAO;
import com.web.user.businesslogic.UserWebDAO;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service("OperatorUserService")
public class OperatorUserServiceI implements OperatorUserService {
    public static final Log LOG = LogFactory.getLog(ModifyBatchC2SCardGroupServiceImpl.class.getName());
    public static final String classname = "ModifyBatchC2SCardGroupServiceImpl";

    @Override
    public OperatorUserCategoryResponseVO getCategoryList(Connection con, Locale locale, String categoryCode, UserVO userVO) throws BTSLBaseException {
        final String METHOD_NAME = "getCategoryList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }

        OperatorUserCategoryResponseVO responseVO = new OperatorUserCategoryResponseVO();
        ArrayList categoryList = new CategoryDAO().loadCategoryList(con, PretupsI.OPERATOR_TYPE_OPT, categoryCode);
        for (int i = 0; i < categoryList.size(); i++) {
            CategoryVO categoryVO = (CategoryVO) categoryList.get(i);
            // check the uniqueness of the external txn number
            if (userVO.getAllowedUserTypeCreation() != null && !(userVO.getAllowedUserTypeCreation().equals("ALL"))) {
                if (!userVO.getAllowedUserTypeCreation().equals(categoryVO.getCategoryCode())) categoryList.remove(i);
            }
        }
        responseVO.setCategoryList(categoryList);
        responseVO.setStatus(HttpStatus.SC_OK);
        responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
        return responseVO;
    }

    @Override
    public BaseResponse addOperatorUser(Connection con, Locale locale, UserVO userVO, HttpServletResponse responseSwag, AddOperatorUserRequestVO requestVO) throws Exception {
        final String methodName = "addOperatorUser";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:=" + methodName);
        }
        BaseResponse response = new BaseResponse();
        boolean changePwdFlag = false;
        OperatorUtilI operatorUtili = null;
        boolean autoPasswordGenerateAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PWD_GENERATE_ALLOW);
        String allowdUsrTypCreation = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION);
        boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
        int prevPinNotAllow = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue();

        UserDAO userDAO = new UserDAO();
        UserVO newUserVO = new UserVO();
        try {
            String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        if (userDAO.isUserLoginExist(con, requestVO.getWebloginid(), null)) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_LOGIN_EXISTS, methodName);
        }
        Date currentDate = new Date();
        CategoryVO categoryVO = userVO.getCategoryVO();
        ArrayList categoryList = new CategoryDAO().loadCategoryList(con, PretupsI.OPERATOR_TYPE_OPT, userVO.getCategoryCode());
        if (categoryList != null) {
            CategoryVO categoryVO1 = null;
            for (int i = 0, j = categoryList.size(); i < j; i++) {
                categoryVO1 = (CategoryVO) categoryList.get(i);
                if (categoryVO1.getCategoryCode().equalsIgnoreCase(requestVO.getCategoryCode())) {
                    categoryVO = categoryVO1;
                    break;
                }
            }
        }
        newUserVO.setUserID(generateUserId(userVO.getNetworkID(), categoryVO.getUserIdPrefix()));
        LOG.info(methodName, "UserId:" + newUserVO.getUserID());

        if (TypesI.NETWORK_ADMIN.equals(categoryVO.getCategoryCode())) {
            if (TypesI.YES.equals(categoryVO.getMultipleGrphDomains())) {
                newUserVO.setNetworkID(requestVO.getGeographicalCodeList().get(0).toString());
            } else {
                newUserVO.setNetworkID(requestVO.getGeographyCode());
            }
        } else {
            newUserVO.setNetworkID(userVO.getNetworkID());
        }
        newUserVO.setLoginID(requestVO.getWebloginid());
        newUserVO.setCategoryCode(categoryVO.getCategoryCode());
        newUserVO.setCategoryVO(categoryVO);
        if (autoPasswordGenerateAllow) {
            newUserVO.setPassword(BTSLUtil.encryptText(operatorUtili.generateRandomPassword()));
            newUserVO.setConfirmPassword(newUserVO.getPassword());
        } else {
            newUserVO.setPassword( BTSLUtil.encryptText(AESEncryptionUtil.aesDecryptor(requestVO.getWebpassword(),Constants.A_KEY)));
            newUserVO.setConfirmPassword(newUserVO.getPassword());
        }
        newUserVO.setParentID(userVO.getUserID());
        newUserVO.setOwnerID(userVO.getOwnerID());
        newUserVO.setAllowedIps(requestVO.getAllowedip());
//        newUserVO.setAllowedDays(requestVO.getAlloweddays().stream().collect(Collectors.joining(",")).toString());
        newUserVO.setAllowedDays((String) requestVO.getAlloweddays().stream().map(Object::toString).collect(Collectors.joining(",")));
        newUserVO.setFromTime(requestVO.getAllowedTimeFrom());
        newUserVO.setToTime(requestVO.getAllowedTimeTo());
        newUserVO.setContactPerson(requestVO.getContactPerson());
        newUserVO.setLastLoginOn(currentDate);
        newUserVO.setEmpCode(requestVO.getSubscriberCode());
        newUserVO.setContactNo(requestVO.getContactNumber());
        if (((Integer) PreferenceCache.getControlPreference(PreferenceI.OPT_USR_APRL_LEVEL, userVO.getNetworkID(), requestVO.getCategoryCode())).intValue() > 0) {
            newUserVO.setStatus(PretupsI.USER_STATUS_NEW);
            newUserVO.setStatusDesc("New");
        } else {
            newUserVO.setStatus(requestVO.getStatus());
            newUserVO.setStatusDesc(BTSLUtil.getOptionDesc(requestVO.getStatus(), LookupsCache.loadLookupDropDown((PretupsI.STATUS_TYPE), true)).getLabel());
        }
//        newUserVO.setPreviousStatus(userVO.getStatus());
        newUserVO.setEmail(requestVO.getEmailid());
        if (!BTSLUtil.isNullString(requestVO.getCompany())) {
            newUserVO.setCompany(requestVO.getCompany());
        }
        if (!BTSLUtil.isNullString(requestVO.getFax())) {
            newUserVO.setFax(requestVO.getFax());
        }

        newUserVO.setFax(userVO.getFax());
        if (isFnameLnameAllowed) {
            newUserVO.setFirstName(requestVO.getFirstName());
            newUserVO.setLastName(requestVO.getLastName());
            if (!BTSLUtil.isNullString(requestVO.getLastName())) {
                newUserVO.setUserName(requestVO.getFirstName() + " " + requestVO.getLastName());
            } else {
                newUserVO.setUserName(requestVO.getFirstName());
            }
        } else {
            newUserVO.setUserName(requestVO.getUserName());
        }
        newUserVO.setPasswordModifiedOn(currentDate);
        newUserVO.setContactNo(requestVO.getContactNumber());
        newUserVO.setDesignation(requestVO.getDesignation());
        newUserVO.setDivisionCode(requestVO.getDivisionCode());
        newUserVO.setDepartmentCode(requestVO.getDepartmentCode());
        newUserVO.setUserType(PretupsI.OPERATOR_USER_TYPE);
        if (categoryVO.getSmsInterfaceAllowed().equalsIgnoreCase("Y"))
            newUserVO.setMsisdn(PretupsBL.getFilteredMSISDN(requestVO.getMsisdnList().get(0).getPhoneNo()));
        else //For Customer Care
            newUserVO.setMsisdn(PretupsBL.getFilteredMSISDN(requestVO.getMsisdn()));
        newUserVO.setCreatedBy(userVO.getUserID());
        newUserVO.setCreatedOn(currentDate);
        newUserVO.setModifiedBy(userVO.getUserID());
        newUserVO.setModifiedOn(currentDate);
        newUserVO.setAddress1(requestVO.getAddress1());
        newUserVO.setAddress2(requestVO.getAddress2());
        newUserVO.setCity(requestVO.getCity());
        newUserVO.setState(requestVO.getState());
        newUserVO.setCountry(requestVO.getCountry());
        newUserVO.setRsaFlag(requestVO.getRsaAuthentication());
        if ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ))
            newUserVO.setAuthTypeAllowed(requestVO.getAuthTypeAllowed());
        else newUserVO.setAuthTypeAllowed("N");
        newUserVO.setSsn(requestVO.getSsn());
        newUserVO.setUserNamePrefix(requestVO.getUserNamePrefix());
        newUserVO.setExternalCode(requestVO.getExternalCode());
        newUserVO.setShortName(requestVO.getShortName());
        if ((Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.USER_CODE_REQUIRED)) {
            newUserVO.setUserCode(requestVO.getUserCode());
        } else {
            newUserVO.setUserCode(newUserVO.getMsisdn());
        }
        newUserVO.setPasswordModifyFlag(true);
        if ((PretupsI.YES).equals(allowdUsrTypCreation)) {
            newUserVO.setAllowedUserTypeCreation(requestVO.getAllowedUserTypeCreation());
        }
        if (!BTSLUtil.isNullString(requestVO.getAppointmentdate())) {
            newUserVO.setAppointmentDate(BTSLUtil.getDateFromDateString(requestVO.getAppointmentdate()));
        }
        int userCount = userDAO.addUser(con, newUserVO);

        if (userCount <= 0) {
            con.rollback();
            LOG.error(methodName, "Error: while Inserting User");
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_ADD_FAIL);
        }
        // this method other info for user
        addUserInfo(con, requestVO, userDAO, newUserVO, currentDate);
        con.commit();
        //Assumed  the approval of operator user is successful.
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "After Inserting User details Status = " + userVO.getStatus());
        }
        OperatorUserLog.log(methodName, newUserVO, userVO, null);

        if (!BTSLUtil.isNullString(newUserVO.getMsisdn()) && PretupsI.USER_STATUS_ACTIVE.equals(newUserVO.getStatus())) {
            BTSLMessages btslMessages;
            String[] arr = {newUserVO.getLoginID(), "", BTSLUtil.getDefaultPasswordText(BTSLUtil.decryptText(requestVO.getWebpassword()))};
            if (TypesI.CUSTOMER_CARE.equals(categoryVO.getCategoryCode())) {
                btslMessages = new BTSLMessages(PretupsErrorCodesI.USER_CCE_WEB_ACTIVE, arr);
            } else {
                btslMessages = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arr);
            }
            PushMessage pushMessage = new PushMessage(newUserVO.getMsisdn(), btslMessages, "", "", locale, newUserVO.getNetworkID());
            pushMessage.push();
            if (isEmailServiceAllow && !BTSLUtil.isNullString(newUserVO.getEmail())) {
                String[] arrOne = {userVO.getUserName()};
                String subject = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ADD_OPERATOR_SUCCESS, arrOne);
                EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessages, locale, userVO.getNetworkID(), "Email has ben delivered recently", newUserVO, userVO);
                emailSendToUser.sendMail();
            }
        }
        String[] arr = {newUserVO.getUserName()};
        String msg;
        if (((Integer) PreferenceCache.getControlPreference(PreferenceI.OPT_USR_APRL_LEVEL, newUserVO.getNetworkID(), requestVO.getCategoryCode())).intValue() > 0) {
            msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ADD_OPERATOR_SUCCSSS_APPRV_REQ, arr);
            response.setStatus(HttpStatus.SC_OK);
            response.setMessage(msg);
        } else {
            msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ADD_OPERATOR_SUCCESS, arr);
            response.setStatus(HttpStatus.SC_OK);
            response.setMessage(msg);
        }
        return response;
    }

    @Override
    public OperatorUserRolesResponseVO getRoleList(Connection con, Locale locale, String userId, String categoryCode) throws BTSLBaseException {
        final String METHOD_NAME = "getRoleList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        OperatorUserRolesResponseVO responseVO = new OperatorUserRolesResponseVO();
        ArrayList rolesList = new UserRolesWebDAO().loadUserRolesList(con, userId);
        responseVO.setGroupedUserRoles(populateSelectedRoles(con, rolesList, categoryCode));
        responseVO.setStatus(HttpStatus.SC_OK);
        responseVO.setMessageCode(PretupsErrorCodesI.SUCCESS);
        responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
        return responseVO;
    }

    @Override
    public OperatorUserGeographyResponseVO getGeographyList(Connection con, Locale locale, String userId, String categoryCode, String networkId) throws BTSLBaseException {
        final String METHOD_NAME = "getGeographyList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        OperatorUserGeographyResponseVO responseVO = new OperatorUserGeographyResponseVO();
        ArrayList geographyList = new ArrayList();
        GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
        if (!((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryCode))) || (TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryCode))) {
            if ((TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(categoryCode))) {
                geographyList = geographicalDomainDAO.loadUserGeographyListForSuperChannelAdmin(con, userId);
            } else {
                geographyList = geographicalDomainDAO.loadUserGeographyList(con, userId, networkId);
            }
            if (geographyList != null && geographyList.size() > 0) {
                responseVO.setGeographyList(geographyList);
                responseVO.setStatus(HttpStatus.SC_OK);
                responseVO.setMessageCode(PretupsI.SUCCESS);
                responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
                return responseVO;
            }
        } else {
            ArrayList<UserGeographiesVO> networkList = geographicalDomainDAO.loadUserNetworkList(con, userId);
            responseVO.setGeographyList(networkList);
            responseVO.setStatus(HttpStatus.SC_OK);
            responseVO.setMessageCode(PretupsI.SUCCESS);
            responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
            return responseVO;
        }
        responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
        responseVO.setMessageCode(PretupsI.FAIL);
        responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FAILED, null));

        return responseVO;
    }

    @Override
    public OperatorUserMsisdnListResponseVO getMsisdnList(Connection con, Locale locale, String userId, String categoryCode, UserVO userVO, String loginid) throws BTSLBaseException {
        final String METHOD_NAME = "getMsisdnList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        OperatorUserMsisdnListResponseVO responseVO = new OperatorUserMsisdnListResponseVO();
        ArrayList msisdnList = new UserDAO().loadUserPhoneList(con, userId);
        CategoryVO categoryVO = (CategoryVO) new CategoryDAO().loadCategoryDetailsOPTCategoryCode(con,categoryCode).get(0);

        if (msisdnList != null && msisdnList.size() > 0) {
            for (Object obj : msisdnList) {
                UserPhoneVO i = (UserPhoneVO) obj;
                i.setSmsPin(AESEncryptionUtil.aesEncryptor(BTSLUtil.decryptText(i.getSmsPin()), Constants.A_KEY));
            }
            responseVO.setMsisdnList(msisdnList);
            responseVO.setStatus(HttpStatus.SC_OK);
            responseVO.setMessageCode(PretupsI.SUCCESS);
            responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
            return responseVO;
        } else if (categoryVO.getSmsInterfaceAllowed().equalsIgnoreCase("Y")) {
            UserPhoneVO phoneVO = new UserPhoneVO();
            ListValueVO listVO = BTSLUtil.getOptionDesc(categoryCode, new UserDAO().loadPhoneProfileList(con, categoryCode));
            userVO = new UserDAO().loadUsersDetailsByLoginID(con,loginid);
//            AESEncryptionUtil.aesDecryptor(loginid, Constants.A_KEY);
            phoneVO.setMsisdn(userVO.getMsisdn());
            phoneVO.setUserId(userId);
            phoneVO.setPrimaryNumber(PretupsI.YES);
            String c2sDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
            phoneVO.setSmsPin(AESEncryptionUtil.aesEncryptor(c2sDefaultSmsPin,Constants.A_KEY));
            phoneVO.setDescription("");
            phoneVO.setConfirmSmsPin(phoneVO.getSmsPin());
            phoneVO.setPhoneProfile(listVO.getValue());
            phoneVO.setPhoneProfileDesc(listVO.getLabel());
            responseVO.setMsisdnList(new ArrayList<>(Collections.singletonList(phoneVO)));
            responseVO.setStatus(HttpStatus.SC_OK);
            responseVO.setMessageCode(PretupsI.SUCCESS);
            responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
            return responseVO;
//            responseVO.setMsisdnList(new ArrayList().add(phoneVO));
        }
        responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
        responseVO.setMessageCode(PretupsI.FAIL);
        responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FAILED, null));

        return responseVO;
    }

    @Override
    public OperatorUserDomainListResponseVO getDomainList(Connection con, Locale locale, String userId) throws BTSLBaseException {
        final String METHOD_NAME = "getDomainList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        OperatorUserDomainListResponseVO responseVO = new OperatorUserDomainListResponseVO();
        responseVO.setDomainList(new DomainWebDAO().loadUserDomainList(con, userId));
        responseVO.setStatus(HttpStatus.SC_OK);
        responseVO.setMessageCode(PretupsI.SUCCESS);
        responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
        return responseVO;
    }

    @Override
    public OperatorUserServiceListResponseVO getServiceList(Connection con, Locale locale, String userId, String categoryCode) throws BTSLBaseException {
        final String METHOD_NAME = "getDomainList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        OperatorUserServiceListResponseVO responseVO = new OperatorUserServiceListResponseVO();
        responseVO.setServiceList(new ServicesTypeDAO().loadUserServicesList(con, userId));
        responseVO.setStatus(HttpStatus.SC_OK);
        responseVO.setMessageCode(PretupsI.SUCCESS);
        responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
        return responseVO;
    }

    @Override
    public OperatorUserProductListResponseVO getProductList(Connection con, Locale locale, String userId) throws BTSLBaseException {
        final String METHOD_NAME = "getProductList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        OperatorUserProductListResponseVO responseVO = new OperatorUserProductListResponseVO();
        responseVO.setProductList(new ProductTypeDAO().loadUserProductsList(con, userId));
        responseVO.setStatus(HttpStatus.SC_OK);
        responseVO.setMessageCode(PretupsI.SUCCESS);
        responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
        return responseVO;
    }

    @Override
    public OperatorUesrVoucherResponseVO getVoucherList(Connection con, Locale locale, String userId) throws BTSLBaseException {
        final String METHOD_NAME = "getVoucherList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        OperatorUesrVoucherResponseVO responseVO = new OperatorUesrVoucherResponseVO();
        responseVO.setVoucherType(new VomsProductDAO().loadUserVoucherTypeList(con, userId));
        responseVO.setVoucherSegment(new VomsProductDAO().loadUserVoucherSegmentList(con,userId));
        responseVO.setStatus(HttpStatus.SC_OK);
        responseVO.setMessageCode(PretupsI.SUCCESS);
        responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
        return responseVO;
    }

    @Override
    public BaseResponse checkMsisdn(Connection con, Locale locale, String userId, String msisdn) throws BTSLBaseException {
        final String METHOD_NAME = "getVoucherList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        BaseResponse response = new BaseResponse();
        if (new UserDAO().isMSISDNExist(con, PretupsBL.getFilteredMSISDN(msisdn), userId)) {
            String[] arr = {msisdn};
            LOG.error(METHOD_NAME, "Error: MSISDN Number is already assigned to another user");
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.OPERATOR_USER_MSISDN_EXIST, 0, arr, METHOD_NAME);
        }
        response.setStatus(HttpStatus.SC_OK);
        response.setMessage(PretupsErrorCodesI.SUCCESS);
        return response;
    }

    private GroupedUserRolesVO populateSelectedRoles(Connection con, ArrayList rolesList, String categoryCode) throws BTSLBaseException {
        final String methodName = "populateSelectedRoles";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        final HashMap mp = new UserRolesWebDAO().loadRolesList(con, categoryCode);
        final HashMap newSelectedMap = new HashMap();
        final Iterator it = mp.entrySet().iterator();
        String key = null;
        GroupedUserRolesVO groupedUserRolesVO = new GroupedUserRolesVO();
        ArrayList list = null;
        ArrayList listNew = null;
        UserRolesVO roleVO = null;
        String roleType;
        Map.Entry pairs = null;
        String[] roleFlag = new String[rolesList.size()];
        boolean foundFlag = false;
        if (rolesList != null && rolesList.size() > 0) {
            final String[] arr = new String[rolesList.size()];
            rolesList.toArray(arr);
            roleFlag = arr;
        }
        ViewUserRolesVO viewUserRolesVO = null;
        HashMap groupRolesMap = new HashMap();
        HashMap systemRolesMap = new HashMap();
        while (it.hasNext()) {
            pairs = (Map.Entry) it.next();
            key = (String) pairs.getKey();
            list = new ArrayList((ArrayList) pairs.getValue());
            listNew = new ArrayList();
            foundFlag = false;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    roleVO = (UserRolesVO) list.get(i);
                    if (roleFlag != null && roleFlag.length > 0) {
                        for (int k = 0; k < roleFlag.length; k++) {
                            if (roleVO.getRoleCode().equals(roleFlag[k])) {
                                // listNew.add(roleVO);
                                viewUserRolesVO = new ViewUserRolesVO();
                                viewUserRolesVO.setGroupRole(roleVO.getGroupRole());
                                viewUserRolesVO.setRoleCode(roleVO.getRoleCode());
                                viewUserRolesVO.setRoleName(roleVO.getRoleName());
                                viewUserRolesVO.setGroupName(roleVO.getGroupName());
                                viewUserRolesVO.setRoleType(roleVO.getRoleType());
                                listNew.add(viewUserRolesVO);


                                foundFlag = true;
                                groupedUserRolesVO.setRoleType(roleVO.getGroupRole());
                                if (groupedUserRolesVO.getRoleType().equals("N"))
                                    groupedUserRolesVO.setRoleTypeDesc("System Roles");
                                else groupedUserRolesVO.setRoleTypeDesc("Group Role");
                            }
                        }
                    }
                }
            }
            if (foundFlag) {
                newSelectedMap.put(key, listNew);
                if ("Y".equals(roleVO.getGroupRole())) {
                    groupRolesMap.put(key, listNew);

                } else if ("N".equals(roleVO.getGroupRole())) {
                    systemRolesMap.put(key, listNew);
                }
            }
        }
        if (newSelectedMap.size() > 0) {
            if (groupedUserRolesVO.getRoleType().equals("N")) {
                groupedUserRolesVO.setSystemRolesMap(newSelectedMap);
            } else {
                groupedUserRolesVO.setGroupRolesMap(newSelectedMap);
            }

        } else {
            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.SYSTEM)) {
                groupedUserRolesVO.setRoleType("N");
                groupedUserRolesVO.setRoleTypeDesc("System Roles");

            } else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.GROUP)) {
                groupedUserRolesVO.setRoleType("Y");
                groupedUserRolesVO.setRoleTypeDesc("Group Role");


            } else {
                groupedUserRolesVO.setRoleType("N");
                groupedUserRolesVO.setRoleTypeDesc("System Roles");

            }
        }

        return groupedUserRolesVO;

    }

    private void addUserInfo(Connection con, AddOperatorUserRequestVO requestVO, UserDAO userDAO, UserVO userVO, Date currentDate) throws BTSLBaseException {
        final String methodName = "addUserInfo";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale = new Locale(defaultLanguage, defaultCountry);
        ArrayList phoneList = new ArrayList();
        ProductTypeDAO productTypeDAO = new ProductTypeDAO();
        UserWebDAO userWebDAO = new UserWebDAO();
        boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
        if (requestVO.getMsisdnList() != null) {
            UserPhoneVO phoneVO = null;
            OperatorUserPhoneVO phoneRequestVO = null;
            ArrayList phonesList = new ArrayList();
//            for(requestVO.getMsisdnList().iterator())
            for (Iterator<OperatorUserPhoneVO> it = requestVO.getMsisdnList().iterator(); it.hasNext(); ) {
                phoneRequestVO = it.next();
                phoneVO = new UserPhoneVO();
                if(phonesList.contains(PretupsBL.getFilteredMSISDN(phoneRequestVO.getPhoneNo()))){
                    throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.DUPLICATED_MSISDN, new String []{phoneRequestVO.getPhoneNo()});
                }else{
                    phonesList.add(PretupsBL.getFilteredMSISDN(phoneRequestVO.getPhoneNo()));
                }
                if (!BTSLUtil.isNullString(phoneRequestVO.getPhoneNo())) {
                    phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(phoneRequestVO.getPhoneNo()));
                    if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                        LOG.error(methodName, "Error: MSISDN Number is already assigned to another user");
                        String[] arr = {PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())), phoneVO.getMsisdn()};
                        throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.OPERATOR_USER_MSISDN_EXIST, arr);
                    }
                    phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                    phoneVO.setUserId(userVO.getUserID());
                    phoneVO.setPinRequired(PretupsI.YES);
                    phoneVO.setCreatedBy(userVO.getUserID());
                    phoneVO.setModifiedBy(userVO.getUserID());
                    phoneVO.setCreatedOn(currentDate);
                    phoneVO.setModifiedOn(currentDate);
                    phoneVO.setPinModifiedOn(currentDate);
                    phoneVO.setDescription(phoneRequestVO.getDescription());
                    phoneVO.setCountry(defaultCountry);

                    phoneVO.setPhoneProfile(phoneRequestVO.getStkProfile());
                    ListValueVO listVO = BTSLUtil.getOptionDesc(phoneVO.getPhoneProfile(), userDAO.loadPhoneProfileList(con, phoneVO.getPhoneProfile()));
                    phoneVO.setPhoneProfileDesc(listVO.getLabel());

//                    if (requestVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY)) {
//                        phoneVO.setPhoneProfile(PretupsI.OPERATOR_CATEGORY);
//                        phoneVO.setPhoneProfileDesc(userVO.getCategoryVO().getCategoryName());
//                    } else {
//                        phoneVO.setPhoneProfile(PretupsI.PWD_CAT_CODE_CCE);
//                        phoneVO.setPhoneProfileDesc(userVO.getCategoryVO().getCategoryName());
//
//                    }
                    phoneVO.setPrimaryNumber(phoneRequestVO.getIsPrimary());
                    phoneVO.setPhoneLanguage(defaultLanguage);
                    NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
                    if (prefixVO == null) {
                        String[] arr = {PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())), userVO.getNetworkID()};
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_NETWORK_PREFIX, arr);
//                        throw new BTSLBaseException(methodName,PretupsErrorCodesI.INVALID_NETWORK_PREFIX, Arrays.asList(arr));
                    }
                    phoneVO.setPrefixID(prefixVO.getPrefixID());
                    phoneVO.setOperationType(PretupsI.DB_FLAG_UPDATE);
                    phoneVO.setPinReset(PretupsI.YES);
                    boolean autoPinGenerateAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW);
                    String c2sDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
                    if (autoPinGenerateAllow) {
                        phoneVO.setSmsPin(BTSLUtil.encryptText(c2sDefaultSmsPin));
                        phoneVO.setConfirmSmsPin(phoneVO.getSmsPin());
                    } else {
                        phoneVO.setSmsPin( BTSLUtil.encryptText(AESEncryptionUtil.aesDecryptor(phoneRequestVO.getPin(),Constants.A_KEY)));
                        phoneVO.setConfirmSmsPin(BTSLUtil.encryptText(AESEncryptionUtil.aesDecryptor(phoneRequestVO.getConfirmPin(),Constants.A_KEY)));
                    }
                    phoneList.add(phoneVO);

                }
            }
            userVO.setMsisdnList(phoneList);
        }

        if (requestVO.getServiceTypeList().size() > 0) {
            if (userVO.getMsisdnList() != null && userVO.getMsisdnList().size() > 0) {
                userWebDAO.deleteOptUserPhoneInfo(con, userVO.getUserID());
                int phoneCount = userDAO.addUserPhoneList(con, userVO.getMsisdnList());
                if (phoneCount <= 0) {
                    try {
                        con.rollback();
                    } catch (SQLException e) {
                        LOG.errorTrace(methodName, e);
                    }
                    LOG.error(methodName, RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_PHONE, null));
                    throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_PHONE);
                }
            }
        }
        ArrayList geographyList = new ArrayList();
        ArrayList networkList = new ArrayList();
        UserGeographiesVO geoVO = null;

        if (!(TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(requestVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(requestVO.getCategoryCode()))) {

            //if user belong to multiple graph domains
            if (TypesI.YES.equalsIgnoreCase(userVO.getCategoryVO().getMultipleGrphDomains())) {
                if (requestVO.getGeographicalCodeList() != null && !requestVO.getGeographicalCodeList().isEmpty()) {
                    List<String> geographicalCodeList = requestVO.getGeographicalCodeList();
                    for (String code : geographicalCodeList) {
                        geoVO = new UserGeographiesVO();
                        geoVO.setUserId(userVO.getUserID());
                        geoVO.setGraphDomainCode(code);
                        geographyList.add(geoVO);
                    }
                }
                } else { //single zones
                    if (requestVO.getGeographyCode() != null && requestVO.getGeographyCode().trim().length() > 0) {
                        geoVO = new UserGeographiesVO();
                        geoVO.setUserId(userVO.getUserID());
                        geoVO.setGraphDomainCode(requestVO.getGeographyCode());
                        geographyList.add(geoVO);
                    }
                }
                //Insert Geography Info
                if (geographyList != null && geographyList.size() > 0) {
                    UserGeographiesDAO userGeographiesDAO = new UserGeographiesDAO();
                    int geographyCount = userGeographiesDAO.addUserGeographyList(con, geographyList);
                    if (geographyCount <= 0) {
                        try {
                            con.rollback();
                        } catch (SQLException e) {
                            LOG.errorTrace(methodName, e);
                        }
                        LOG.error(methodName, "Error: While inserting User Geography Info");
                        throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_GEOGRAPHY);
                    }
                    userVO.setGeographicalAreaList(geographyList);
                }

        } else {
            //if user belong to multiple graph domains
            if (requestVO.getGeographicalCodeList() != null && requestVO.getGeographicalCodeList().size() > 0) {
                for (int i = 0, j = requestVO.getGeographicalCodeList().size(); i < j; i++) {
                    geoVO = new UserGeographiesVO();
                    geoVO.setUserId(userVO.getUserID());
                    geoVO.setGraphDomainCode(requestVO.getGeographicalCodeList().get(i).toString());
                    networkList.add(geoVO);
                }
            }

            //Inserting Geography info
            if (networkList != null && networkList.size() > 0) {
                UserGeographiesDAO userGeographiesDAO = new UserGeographiesDAO();
                int geographyCount = userGeographiesDAO.addUserGeographyList(con, networkList);
                if (geographyCount <= 0) {
                    try {
                        con.rollback();
                    } catch (SQLException e) {
                        LOG.errorTrace(methodName, e);
                    }
                    LOG.error(methodName, "Error: while Inseting User Geography Info");
                    throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_GEOGRAPHY);
                }
                userVO.setGeographicalAreaList(networkList);
            }
        }

        //Insert Roles Info
        if (requestVO.getRoleType() != null && requestVO.getRoleType().length() > 0) {
            UserRolesDAO rolesDAO = new UserRolesDAO();
            String[] roles = (String[]) requestVO.getRoleList().toArray(new String[requestVO.getRoleList().size()]);
            int roleCount = rolesDAO.addUserRolesList(con, userVO.getUserID(), roles);
            if (roleCount <= 0) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Error: while inserting User Roles Info");
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_ROLES);
            }
        }

        //Insert domains info
        if (requestVO.getDomainCodeList() != null && requestVO.getDomainCodeList().size() > 0) {
            DomainDAO domainDAO = new DomainDAO();
            int domainCount = domainDAO.addUserDomainList(con, userVO.getUserID(), (String[]) requestVO.getDomainCodeList().toArray(new String[requestVO.getDomainCodeList().size()]));
            if (domainCount <= 0) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Error: while inserting Domain Codes");
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_DOMAIN);
            }
        }
        userVO.setDomainCodes((String[]) requestVO.getDomainCodeList().toArray(new String[requestVO.getDomainCodeList().size()]));

        //Insert Services Info
        if (requestVO.getServiceTypeList() != null && requestVO.getServiceTypeList().size() > 0) {
            ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
            int servicesCount = servicesTypeDAO.addUserServicesList(con, userVO.getUserID(), (String[]) requestVO.getServiceTypeList().toArray(new String[requestVO.getServiceTypeList().size()]), PretupsI.YES);
            if (servicesCount <= 0) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Error : while inserting Service Types : SQL Error");
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_SERVICE_TYPE);
            }
        }

        //Insert Products Info
        if (requestVO.getProductList() != null && requestVO.getProductList().size() > 0) {
            int productCount = productTypeDAO.addUserProductsList(con, userVO.getUserID(), (String[]) requestVO.getProductList().toArray(new String[requestVO.getProductList().size()]));
            if (productCount <= 0) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Error: while inserting Products");
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_PRODUCTS);
            }
        }
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        if (requestVO.getVoucherTypeList() != null && requestVO.getVoucherTypeList().size() > 0) {
            VomsProductDAO vomsProductDAO = new VomsProductDAO();
            int voucherCount = vomsProductDAO.addUserVoucherTypeList(con, userVO.getUserID(), (String[]) requestVO.getVoucherTypeList().toArray(new String[requestVO.getVoucherTypeList().size()]), PretupsI.YES);
            if (voucherCount <= 0) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Error: while inserting Voucher Types");
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_VOUCHER_TYPES);
            }
        }
        //Check Segments
        if(requestVO.getVoucherSegmentList() != null && requestVO.getVoucherSegmentList().size() > 0){
            VomsProductDAO segmentsDAO = new VomsProductDAO();
            int segmentCount = segmentsDAO.addUserVoucherSegmentList(con, userVO.getUserID(), (String[]) requestVO.getVoucherSegmentList().toArray(new String[requestVO.getVoucherSegmentList().size()]),PretupsI.YES);
            if (segmentCount <= 0) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Error: while inserting user voucher Segments");
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.SQL_ERROR_INSERT_USER_VOUCHER_SEGMENTS);
            }
        }


        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting");
        }

    }

    private String generateUserId(String p_networkCode, String p_prefix) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateUserId", "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix);
        }
        int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft(String.valueOf(IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, p_networkCode)), length);

        // id =
        // p_networkCode+Constants.getProperty("SEPARATOR_FORWARD_SLASH")+p_prefix+id;
        // discuss with sanjay sir remove the / while generating ID
        id = p_networkCode + p_prefix + id;

        if (LOG.isDebugEnabled()) {
            LOG.debug("generateUserId", "Exiting id=" + id);
        }
        return id;
    }

}
