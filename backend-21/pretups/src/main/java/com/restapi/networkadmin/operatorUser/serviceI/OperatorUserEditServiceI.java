package com.restapi.networkadmin.operatorUser.serviceI;

import com.btsl.common.*;
import com.btsl.common.BaseResponse;
import com.btsl.event.*;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OperatorUserLog;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
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
import com.restapi.networkadmin.operatorUser.service.OperatorUserEditService;
import com.restapi.networkadmin.service.ModifyBatchC2SCardGroupServiceImpl;
import com.web.user.businesslogic.UserWebDAO;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service("OperatorUserEditService")
public class OperatorUserEditServiceI implements OperatorUserEditService {
    public static final Log LOG = LogFactory.getLog(ModifyBatchC2SCardGroupServiceImpl.class.getName());
    public static final String classname = "ModifyBatchC2SCardGroupServiceImpl";

    @Override
    public BaseResponse modifyOperatorUser(Connection con, Locale locale, UserVO userVO, HttpServletResponse responseSwag, AddOperatorUserRequestVO requestVO) throws BTSLBaseException, SQLException, ParseException {
        final String methodName = "modifyOperatorUser";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:=" + methodName);
        }
        BaseResponse response = new BaseResponse();
        boolean changePwdFlag = false;
        OperatorUtilI operatorUtili = null;
        boolean autoPasswordGenerateAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PWD_GENERATE_ALLOW);
        String allowdUsrTypCreation = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
        int prevPinNotAllow = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue();

        UserDAO userDAO = new UserDAO();
        UserVO newUserVO = new UserVO();
        UserWebDAO userWebDAO = new UserWebDAO();
        UserVO prevUserVO = userDAO.loadAllUserDetailsByLoginID(con, requestVO.getPrevLoginId());

        try {
            String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        if (userDAO.isUserLoginExist(con, requestVO.getWebloginid(), requestVO.getUserId())) {
            throw new BTSLBaseException(this, methodName, "user.addoperatoruser.error.loginallreadyexist", "Detail");
        }
        setDetails(con, newUserVO, userVO, requestVO, userDAO, userWebDAO, prevUserVO);
        int updateCount = userDAO.updateUser(con, newUserVO);

        if (updateCount <= 0) {
            con.rollback();
            LOG.error(methodName, "Error: while Updating User");
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OPERATOR_USER_MODIFY_FAIL);
        }
        if (requestVO.getCategoryCode().equalsIgnoreCase(PretupsI.OPERATOR_CATEGORY)) {
            userWebDAO.deleteOptUserPhoneInfo(con, newUserVO.getUserID());

        }
        userWebDAO.deleteUserInfo(con, newUserVO.getUserID());
        addUserInfo(con, requestVO, userDAO, newUserVO, new Date(), false);
        con.commit();
        String arr[] = {newUserVO.getUserName()};
        response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OPERATOR_USER_MODIFY_SUCCESS, arr));
        response.setMessageCode(PretupsErrorCodesI.OPERATOR_USER_MODIFY_SUCCESS);
        response.setStatus(HttpStatus.SC_OK);

        String msg[] = new String[2];
        msg[0] = newUserVO.getLoginID();
        if (changePwdFlag) {
            msg[1] = newUserVO.getPassword();
        } else {
            msg[1] = BTSLUtil.decryptText(newUserVO.getPassword());
        }
        BTSLMessages btslMessage = null;

        if (changePwdFlag && !newUserVO.getLoginID().equals(prevUserVO.getLoginID())) {
            btslMessage = new BTSLMessages(PretupsErrorCodesI.OPT_USER_LOGIN_AND_PWD_MODIFY, msg);
        } else if (!newUserVO.getLoginID().equals(prevUserVO.getLoginID())) {
            btslMessage = new BTSLMessages(PretupsErrorCodesI.OPT_USER_LOGIN_MODIFY, msg);
        } else if (changePwdFlag) {
            btslMessage = new BTSLMessages(PretupsErrorCodesI.OPT_USER_PWD_MODIFY, msg);
        }
        if (btslMessage != null) {
            locale = new Locale(defaultLanguage, defaultCountry);

            PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), btslMessage, "", "", locale, userVO.getNetworkID(), "SMS will be delivered shortly");
            pushMessage.push();
            // Email for pin & password-email send
            if (isEmailServiceAllow && !BTSLUtil.isNullString(userVO.getEmail())) {

                String subject = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.OPERATOR_USER_MODIFY_SUCCESS,arr);//this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "user.addoperatoruser.updatesuccessmessage", arr);
                EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage, locale, userVO.getNetworkID(), "Email has ben delivered recently", newUserVO, userVO);
                emailSendToUser.sendMail();
            }
        }
        OperatorUserLog.log(methodName, newUserVO, userVO, null);
        return response;
    }

    private void addUserInfo(Connection con, AddOperatorUserRequestVO requestVO, UserDAO userDAO, UserVO userVO, Date currentDate, Boolean flag) throws BTSLBaseException {
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
//            for(requestVO.getMsisdnList().iterator())
            for (Iterator<OperatorUserPhoneVO> it = requestVO.getMsisdnList().iterator(); it.hasNext(); ) {
                phoneRequestVO = it.next();
                phoneVO = new UserPhoneVO();
                if (!BTSLUtil.isNullString(phoneRequestVO.getPhoneNo())) {
                    phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(phoneRequestVO.getPhoneNo()));
//                    if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
//                        LOG.error(methodName, "Error: MSISDN Number is already assigned to another user");
//                        String[] arr = {PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())), phoneVO.getMsisdn()};
//                        throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.OPERATOR_USER_MSISDN_EXIST, arr);
//                    }
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
                    }
                    phoneVO.setPrefixID(prefixVO.getPrefixID());

                    phoneVO.setOperationType(PretupsI.DB_FLAG_INSERT);

                    phoneVO.setPinReset(PretupsI.NO);

                    boolean autoPinGenerateAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW);
                    String c2sDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
                    if (autoPinGenerateAllow) {
                        phoneVO.setSmsPin(BTSLUtil.encryptText(c2sDefaultSmsPin));
                        phoneVO.setConfirmSmsPin(phoneVO.getSmsPin());
                    } else {
                        phoneVO.setSmsPin(BTSLUtil.encryptText(AESEncryptionUtil.aesDecryptor(phoneRequestVO.getPin(), Constants.A_KEY)));
                        phoneVO.setConfirmSmsPin(BTSLUtil.encryptText(AESEncryptionUtil.aesDecryptor(phoneRequestVO.getConfirmPin(), Constants.A_KEY)));

//                        phoneVO.setConfirmSmsPin(phoneRequestVO.getConfirmPin());
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
        } else {
            userWebDAO.deleteOptUserPhoneInfo(con, userVO.getUserID());
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
        if(requestVO.getVoucherSegmentList() != null && !requestVO.getVoucherSegmentList().isEmpty()){
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


    /**
     * @param con
     * @param newUserVO
     * @param userVO
     * @param requestVO
     * @param userDAO
     * @param userWebDAO
     * @param prevUserVO newUserVO -> user to be modified this does not create new entry in users table, but new entries are created for all the other values i.e., vouchers, geographu, roles, etc.
     *                   prevUserVO -> details of user loaded, which are not provided from frontend, non-modifiable fields
     *                   userVO -> user details of logged in user.
     */
    private void setDetails(Connection con, UserVO newUserVO, UserVO userVO, AddOperatorUserRequestVO requestVO, UserDAO userDAO, UserWebDAO userWebDAO, UserVO prevUserVO) throws BTSLBaseException, ParseException, SQLException {
        final String methodName = "setDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:=" + methodName);
        }
        boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
        int prevPinNotAllow = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue();
        Date currentDate = new Date();
        String allowdUsrTypCreation = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION);
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
        newUserVO.setUserID(requestVO.getUserId());
        newUserVO.setLastModified(prevUserVO.getLastModified());
        if (!BTSLUtil.isNullString(requestVO.getCompany())) {
            newUserVO.setCompany(requestVO.getCompany());
        }
        if (!BTSLUtil.isNullString(requestVO.getFax())) {
            newUserVO.setFax(requestVO.getFax());
        }
        if (isFnameLnameAllowed) {
            newUserVO.setFirstName(requestVO.getFirstName());
            newUserVO.setLastName(requestVO.getLastName());
            if (!BTSLUtil.isNullString(requestVO.getLastName())) {
                newUserVO.setUserName(requestVO.getFirstName() + " " + requestVO.getLastName());
            } else
                newUserVO.setUserName(requestVO.getFirstName());
        } else
            newUserVO.setUserName(requestVO.getUserName());
        newUserVO.setNetworkID(userVO.getNetworkID());
        newUserVO.setLoginID(requestVO.getWebloginid());
        newUserVO.setCategoryVO(categoryVO);
        //check if new password is equal to old
        if (!BTSLUtil.isNullString(requestVO.getWebpassword()) && requestVO.getWebpassword().equals(prevUserVO.getPassword())) {
            newUserVO.setPasswordModifiedOn(currentDate);
            newUserVO.setPassword(requestVO.getWebpassword());
            newUserVO.setPasswordModifyFlag(false);
        } else {
            boolean passwordExist = false;
            passwordExist = userDAO.checkPasswordHistory(con, PretupsI.USER_PASSWORD_MANAGEMENT, requestVO.getUserId(), userVO.getMsisdn(), BTSLUtil.encryptText(requestVO.getWebpassword()));
            if (passwordExist) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANGE_PASSWORD_NEWPASSWORD_EXIST_HIST, 0, new String[]{String.valueOf(prevPinNotAllow)}, methodName);
            }
            String password = BTSLUtil.encryptText(requestVO.getWebpassword());
            newUserVO.setPassword(password);
            newUserVO.setPasswordModifyFlag(true);
        }
        newUserVO.setAllowedIps(requestVO.getAllowedip());
        newUserVO.setAllowedDays((String) requestVO.getAlloweddays().stream().map(Object::toString).collect(Collectors.joining(",")));
        newUserVO.setFromTime(requestVO.getAllowedTimeFrom());
        newUserVO.setToTime(requestVO.getAllowedTimeTo());
        newUserVO.setEmpCode(requestVO.getSubscriberCode());
        newUserVO.setStatus(requestVO.getStatus());
        newUserVO.setPreviousStatus(prevUserVO.getStatus());
        newUserVO.setEmail(requestVO.getEmailid());
        newUserVO.setContactNo(requestVO.getContactNumber());
        newUserVO.setContactPerson(requestVO.getContactPerson());
        newUserVO.setDesignation(requestVO.getDesignation());
        newUserVO.setDivisionCode(requestVO.getDivisionCode());
        newUserVO.setDepartmentCode(requestVO.getDepartmentCode());
        if (categoryVO.getSmsInterfaceAllowed().equalsIgnoreCase("Y"))
            newUserVO.setMsisdn(PretupsBL.getFilteredMSISDN(requestVO.getMsisdnList().get(0).getPhoneNo()));
        else //For Customer Care
            newUserVO.setMsisdn(PretupsBL.getFilteredMSISDN(requestVO.getMsisdn()));
        newUserVO.setLevel1ApprovedBy(prevUserVO.getLevel1ApprovedBy());
        newUserVO.setLevel1ApprovedOn(prevUserVO.getLevel1ApprovedOn());
        newUserVO.setLevel2ApprovedBy(prevUserVO.getLevel2ApprovedBy());
        newUserVO.setLevel2ApprovedOn(prevUserVO.getLevel2ApprovedOn());
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
        else
            newUserVO.setAuthTypeAllowed("N");
        newUserVO.setSsn(requestVO.getSsn());
        newUserVO.setUserNamePrefix(requestVO.getUserNamePrefix());
        newUserVO.setExternalCode(requestVO.getExternalCode());
        newUserVO.setShortName(requestVO.getShortName());
        if ((Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.USER_CODE_REQUIRED)) {
            newUserVO.setUserCode(requestVO.getUserCode());
        } else {
            newUserVO.setUserCode(newUserVO.getMsisdn());
        }
        if ((PretupsI.YES).equals(allowdUsrTypCreation)) {
            newUserVO.setAllowedUserTypeCreation(requestVO.getAllowedUserTypeCreation());
        }

        if (!BTSLUtil.isNullString(requestVO.getAppointmentdate())) {
            newUserVO.setAppointmentDate(BTSLUtil.getDateFromDateString(requestVO.getAppointmentdate()));
        }
    }
}
