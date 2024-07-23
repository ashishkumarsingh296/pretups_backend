package com.client.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * This class modifies the channel user through API
 * @param 
 */
public class ModifyChannelUserController implements ServiceKeywordControllerI {
    private static final Log LOG = LogFactory.getLog(ModifyChannelUserController.class.getName());
    private ChannelUserVO channelUserVO = null;
    private UserDAO userDAO = null;
    private CategoryVO categoryVO = null;
    private CategoryVO userCategoryVO = null;
    private ChannelUserDAO channelUserDao = null;
    private ExtUserDAO extUserDao = null;
    private ChannelUserVO parentChannelUserVO = null;
    private ChannelUserVO modifiesChannelUserVO = null;
    private ChannelUserVO senderVO = null;
    private static final String CLASSNAME = "ModifyChannelUserController";

    /**
     * Method Process
     * Process Method , Processes external channel user registration request
     * 
     * @param requestVO
     */
    public void process(RequestVO requestVO) {
        final String methodName = "process";
        StringBuilder loggerValue= new StringBuilder(); 
		        loggerValue.setLength(0);
            	loggerValue.append("Entered requestVO=");
            	loggerValue.append(requestVO);
        LogFactory.printLog(methodName,  loggerValue.toString(), LOG);
        Connection con = null;MComConnectionI mcomCon = null;
        final HashMap requestMap = requestVO.getRequestMap();
        channelUserDao = new ChannelUserDAO();
        channelUserVO = new ChannelUserVO();
        userDAO = new UserDAO();
        extUserDao = new ExtUserDAO();
        OperatorUtilI operatorUtili = null;
        Locale locale = null;
        ArrayList oldPhoneList = null;
        String senderPin = "";
        String defaultGeoCode = "";
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception while loading the class at the call:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
            		loggerValue.toString() );
        }

        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();

            senderVO = (ChannelUserVO) requestVO.getSenderVO();

            // Load details of channel user to be modified
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            String userExtCode = (String) requestMap.get("EXTERNALCODE");
            String modifiedUserMsisdn = (String) requestMap.get("USERMSISDN");
            String modifiedUserLoginId = (String) requestMap.get("USERLOGINID");
            modifiedUserMsisdn = PretupsBL.getFilteredMSISDN(modifiedUserMsisdn);
            userExtCode = userExtCode.trim();
            modifiedUserLoginId = modifiedUserLoginId.trim();
            // Load Channel User on basis of Primary MSISDN only:
            if(BTSLUtil.isNullString(modifiedUserMsisdn) && BTSLUtil.isNullString(modifiedUserLoginId) && BTSLUtil.isNullString(userExtCode)){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_RECEIVER_DETAILS_INVALID);
            }
            modifiesChannelUserVO = extUserDao.loadChannelUserDetailsByMsisdnLoginIdExt(con, modifiedUserMsisdn, modifiedUserLoginId, null, userExtCode, locale);
            if (!(modifiesChannelUserVO == null)) {
                oldPhoneList = userDAO.loadUserPhoneList(con, modifiesChannelUserVO.getUserID());
                channelUserVO = modifiesChannelUserVO;
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_RECEIVER_DETAILS_INVALID); // Message
                // Changes
                // on
                // 11-MAR-2014
            }

            String userCatCode = channelUserVO.getCategoryCode();
            userCatCode = userCatCode.trim();
            final List userCatList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con, userCatCode);
            userCategoryVO = (CategoryVO) userCatList.get(0);
            
            final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";
            String parentMsisdn = channelUserVO.getParentMsisdn();
            String filteredParentMsisdn = null;
            if(!BTSLUtil.isNullString(parentMsisdn)){
                filteredParentMsisdn = PretupsBL.getFilteredMSISDN(parentMsisdn);
            }
            parentChannelUserVO = channelUserDao.loadUsersDetails(con, filteredParentMsisdn, null, PretupsI.STATUS_NOTIN, status);
            
            final String newUserExtCode = (String) requestMap.get("NEWEXTERNALCODE");
            if (!BTSLUtil.isNullString(newUserExtCode)) {
                channelUserVO.setExternalCode(newUserExtCode);
            }
            // External Code
            // Check given channel User MSISDN if it is not already existing ,
            // in request
            final String originalMsisdn = channelUserVO.getMsisdn();
            final String modifiedMsisdn = BTSLUtil.NullToString((String) requestMap.get("PRIMARYMSISDN"));
            if(BTSLUtil.isNullString(modifiedMsisdn)) {
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_PRIMARY_MSISDN_BLANK);
            }
            boolean isExists = false;
            isExists = channelUserDao.isPhoneExists(con, modifiedMsisdn);
            NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(modifiedMsisdn)));
            // If primary MSISDN is not again given and any existing MSISDN
            // given in modification request throw error
            if (!originalMsisdn.equals(modifiedMsisdn)) {
                if (!isExists) {
                    if(TypesI.YES.equals(userCategoryVO.getSmsInterfaceAllowed()) && (prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserVO.getNetworkID()))) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
                    }else{
                        channelUserVO.setMsisdn(BTSLUtil.NullToString(originalMsisdn));
                    }                  
                } else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST);
                }
            }

            // User Name set
            boolean blank = true;
            final String modifiedUsername = (String) requestMap.get("USERNAME");
            blank = BTSLUtil.isNullString(modifiedUsername);
            if (!blank) {
                channelUserVO.setUserName(modifiedUsername.toString().trim());
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.XML_ERROR_USER_NAME_BLANK);// Blank
                // User
                // Name
                // Exception
            }

            // short name set
            final String modifiedUserShortname = (String) requestMap.get("SHORTNAME");
            blank = BTSLUtil.isNullString(modifiedUserShortname);
            if (!blank) {
                channelUserVO.setShortName(modifiedUserShortname.toString().trim());
            }

            // User prefix check
            String userPrifix = (String) requestMap.get("USERNAMEPREFIX");
            userPrifix = userPrifix.toUpperCase();

            if (!userPrifix.equals(modifiesChannelUserVO.getUserNamePrefix())) {
                channelUserVO.setUserNamePrefix(userPrifix);
            }

            // set External Code

            // Set Contact Person , contact number , ssn , address1, address2,
            // city , state, country ,email id
            final String modifiedContactPerson = (String) requestMap.get("CONTACTPERSON");
            blank = BTSLUtil.isNullString(modifiedContactPerson);
            if (!blank) {
                channelUserVO.setContactPerson(modifiedContactPerson.toString().trim());
            }

            final String modifiedContactNumber = (String) requestMap.get("CONTACTNUMBER");
            blank = BTSLUtil.isNullString(modifiedContactNumber);
            if (!blank) {
                channelUserVO.setContactNo(modifiedContactNumber.toString().trim());
            }

            final String modifiedSSN = (String) requestMap.get("SSN");
            blank = BTSLUtil.isNullString(modifiedSSN);
            if (!blank) {
                channelUserVO.setSsn(modifiedSSN.toString().trim());
            }

            // Address1
            final String modifedAddress1 = (String) requestMap.get("ADDRESS1");
            blank = BTSLUtil.isNullString(modifedAddress1);
            if (!blank) {
                channelUserVO.setAddress1(modifedAddress1.toString().trim());
            }

            // Address2
            final String modifedAddress2 = (String) requestMap.get("ADDRESS2");
            blank = BTSLUtil.isNullString(modifedAddress2);
            if (!blank) {
                channelUserVO.setAddress2(modifedAddress2.toString().trim());
            }

            final String modifedCity = (String) requestMap.get("CITY");
            blank = BTSLUtil.isNullString(modifedCity);
            if (!blank) {
                channelUserVO.setCity(modifedCity.toString().trim());
            }

            // State
            final String modifedState = (String) requestMap.get("STATE");
            blank = BTSLUtil.isNullString(modifedState);
            if (!blank) {
                channelUserVO.setState(modifedState.toString().trim());
            }

            // Country
            final String modifedCountry = (String) requestMap.get("COUNTRY");
            blank = BTSLUtil.isNullString(modifedCountry);
            if (!blank) {
                channelUserVO.setCountry(modifedCountry.toString().trim());
            }

            // EmailId
            final String modifedEmail = (String) requestMap.get("EMAILID");
            blank = BTSLUtil.isNullString(modifedEmail);
            boolean validEmail = false;
            if (!blank) {
                validEmail = BTSLUtil.validateEmailID(modifedEmail);
                if (validEmail) {
                    channelUserVO.setEmail(modifedEmail.toString().trim());
                }
            }

            // WebLoginId
            final String modifedLoginId = (String) requestMap.get("WEBLOGINID");
            final String existingLoginId = (String) modifiesChannelUserVO.getLoginID();
            if (!BTSLUtil.isNullString(modifedLoginId) && !modifedLoginId.equals(existingLoginId)) {
                if (new UserDAO().isUserLoginExist(con, modifedLoginId, null)) {
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_LOGINID_ALREADY_EXIST);
                    // 11-MAR-2014
                } else {
                    channelUserVO.setLoginID(modifedLoginId.toString().trim());
                    // Ended Here
                }
            } else {
                channelUserVO.setLoginID(existingLoginId.toString().trim());
            }

            // Web Password
            String modifedPassword = (String) requestMap.get("WEBPASSWORD");

            // 11-MAR-2104 for setFlagForSMS
            boolean isWebLoginIdChanged = false;
            boolean isWebPasswordChanged = false;
            boolean isWebLoginIdPassswordChanged = false;
            boolean isPinChanged = false;
            if (!BTSLUtil.isNullString(modifedLoginId) && !modifedLoginId.equalsIgnoreCase(existingLoginId) && !BTSLUtil.isNullString(modifedPassword) && !BTSLUtil
                .encryptText(modifedPassword).equalsIgnoreCase(modifiesChannelUserVO.getPassword())) {
                isWebLoginIdPassswordChanged = true;
            } else if (!BTSLUtil.isNullString(modifedLoginId) && !modifedLoginId.equalsIgnoreCase(existingLoginId)) {
                isWebLoginIdChanged = true;
            } else if (BTSLUtil.isNullString(modifedLoginId) && !"".equalsIgnoreCase(existingLoginId)) {
                isWebLoginIdChanged = true;
            } else if (!BTSLUtil.isNullString(modifedPassword) && !BTSLUtil.encryptText(modifedPassword).equalsIgnoreCase(modifiesChannelUserVO.getPassword())) {
                isWebPasswordChanged = true;
                // Ended Here
            }

            // If in modify request, web password is not mentioned, then set the
            // previous one.
            if (BTSLUtil.isNullString(modifedPassword)) {
                modifedPassword = BTSLUtil.decryptText(channelUserVO.getPassword());
            }
            // while updating encrypt the password
            channelUserVO.setPassword(BTSLUtil.encryptText(modifedPassword));

            // get PIN of send if not comming then generate random pin as
            // discussed with Divyakant Sir
            senderPin = (String) requestMap.get("PIN");
            if (BTSLUtil.isNullString(senderPin)) {
                if (oldPhoneList != null && !oldPhoneList.isEmpty()) {
                    final UserPhoneVO oldPhoneVO = (UserPhoneVO) oldPhoneList.get(0);
                    // 11-MAR-2104 for setFlagForSMS
                    if (!BTSLUtil.isNullString(senderPin) && !oldPhoneVO.getSmsPin().toString().equalsIgnoreCase(BTSLUtil.encryptText(senderPin))) {
                        isPinChanged = true;
                        // Ended Here
                    }
                } else {
                    senderPin = operatorUtili.generateRandomPin();
                }

            }

            // Set some use full parameter
            final Date currentDate = new Date();
            // prepare user phone list
            final List userPhoneList = prepareUserPhoneVOList(con, requestMap, modifiesChannelUserVO, currentDate, oldPhoneList, senderPin);
            channelUserVO.setModifiedBy(senderVO.getUserID());
            channelUserVO.setModifiedOn(currentDate);
            if (channelUserVO.getMsisdn() != null) {
                channelUserVO.setUserCode(channelUserVO.getMsisdn());
            }

            final String userProfileId = channelUserVO.getUserProfileID();
            channelUserVO.setUserProfileID(userProfileId);
            // Employee Code
            final String empCode = (String) requestMap.get("EMPCODE");
            blank = BTSLUtil.isNullString(empCode);
            if (!blank) {
                channelUserVO.setEmpCode(empCode.toString().trim());
            }
            
            String inSuspend = (String) requestVO.getRequestMap().get("INSUSPEND");
            if(!BTSLUtil.isNullString(inSuspend)){
                if(inSuspend.equals(PretupsI.YES) || inSuspend.equals(PretupsI.NO)){
                    channelUserVO.setInSuspend(inSuspend);
                }else{
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_INSUSPEND_INVALID);
                }    
            }else{
                channelUserVO.setInSuspend(modifiesChannelUserVO.getInSuspend());
            }    
            String outSuspend = (String) requestVO.getRequestMap().get("OUTSUSPEND");
            if(!BTSLUtil.isNullString(outSuspend)){
                if(outSuspend.equals(PretupsI.YES) || outSuspend.equals(PretupsI.NO)){
                    channelUserVO.setOutSuspened(outSuspend);
                }else{
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_OUTSUSPEND_INVALID);
                }        
            }else{
                channelUserVO.setOutSuspened(modifiesChannelUserVO.getOutSuspened());
            }   
            
            String company = (String) requestMap.get("COMPANY");
            if(!BTSLUtil.isNullString(company)){
                channelUserVO.setCompany(company);
            }
            
            String fax = (String) requestMap.get("FAX");
            if(!BTSLUtil.isNullString(fax)){
                if(BTSLUtil.isValidNumber(fax)){
                    channelUserVO.setFax(fax);
                }else{
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_FAX_INVALID);
                }
            }
             
            channelUserVO.setLongitude(requestMap.get("LONGITUDE") != null ? (String)requestMap.get("LONGITUDE") : "");
            channelUserVO.setLatitude(requestMap.get("LATITUDE") != null ? (String)requestMap.get("LATITUDE") : "");
            channelUserVO.setDocumentType(requestMap.get("DOCUMENTTYPE") != null ? (String)requestMap.get("DOCUMENTTYPE") : "");
            channelUserVO.setDocumentNo(requestMap.get("DOCUMENTNO") != null ? (String)requestMap.get("DOCUMENTNO") : "");
            channelUserVO.setPaymentType(requestMap.get("PAYMENTTYPE") != null ? (String)requestMap.get("PAYMENTTYPE") : "");
            
            if (TypesI.YES.equals(userCategoryVO.getLowBalAlertAllow())) {
                final String delimiter = ";";
                final String allowforself = BTSLUtil.NullToString((String) requestMap.get("LOWBALALERTSELF"));
                final String allowforparent = BTSLUtil.NullToString((String) requestMap.get("LOWBALALERTPARENT"));
                final String allowforOther = BTSLUtil.NullToString((String) requestMap.get("LOWBALALERTOTHER"));
                final StringBuilder alerttype = new StringBuilder("");
                if (TypesI.YES.equals(allowforself)) {
                    alerttype.append(PretupsI.ALERT_TYPE_SELF);
                }
                if (TypesI.YES.equals(allowforparent)) {
                    alerttype.append("".equals(alerttype.toString()) ? "" : delimiter);
                    alerttype.append(PretupsI.ALERT_TYPE_PARENT);
                }
                if (TypesI.YES.equals(allowforOther)) {
                    alerttype.append("".equals(alerttype.toString()) ? "" : delimiter);
                    alerttype.append(PretupsI.ALERT_TYPE_OTHER);
                }

                if (!"".equals(alerttype.toString())) {
                    channelUserVO.setLowBalAlertAllow(TypesI.YES);
                    channelUserVO.setAlertType(alerttype.toString());
                } else {
                    channelUserVO.setLowBalAlertAllow(modifiesChannelUserVO.getLowBalAlertAllow());
                }

            }else {
                channelUserVO.setLowBalAlertAllow(modifiesChannelUserVO.getLowBalAlertAllow());
            }

            Date appointmentDate = null;
            if(!BTSLUtil.isNullString((String) requestVO.getRequestMap().get("APPOINTMENTDATE"))){
                try {
                    appointmentDate = BTSLUtil.getDateFromDateString((String) requestVO.getRequestMap().get("APPOINTMENTDATE"));
                } catch (Exception e) {
                	LOG.error(methodName, "Exception " + e);
    				LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
                }
                channelUserVO.setAppointmentDate(appointmentDate);
            }
            
            final String allowedIPs = (String) requestVO.getRequestMap().get("ALLOWEDIP");
            if(!BTSLUtil.isNullString(allowedIPs)){
                String[] allowedIPAddress = allowedIPs.split(",");
                int allowIPAddress=allowedIPAddress.length;
                for(int i=0; i<allowIPAddress; i++){
                    String splitAllowedIP = allowedIPAddress[i];
                    if(!BTSLUtil.isValidateIpAddress(splitAllowedIP)){
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_IP_INVALID);
                    }
                }
                channelUserVO.setAllowedIps(allowedIPs);
            }
            
            final String allowedDays = (String) requestVO.getRequestMap().get("ALLOWEDDAYS");
            if(!BTSLUtil.isNullString(allowedDays)){
                if(!BTSLUtil.isValidateAllowedDays(allowedDays)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_ALLOWEDDAYS_INVALID);
                }
                channelUserVO.setAllowedDays(allowedDays);
            }
            
            final String fromTime = (String) requestVO.getRequestMap().get("ALLOWEDTIMEFROM");
            final String toTime = (String) requestVO.getRequestMap().get("ALLOWEDTIMETO");

            if(!BTSLUtil.isNullString(fromTime)){
                if(!BTSLUtil.isValidateAllowedTime(fromTime)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_FROMTIME_INVALID);
                }
                channelUserVO.setFromTime(fromTime);
            }
            
            if(!BTSLUtil.isNullString(fromTime)){
                if(!BTSLUtil.isValidateAllowedTime(fromTime)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_TOTIME_INVALID);
                }
                channelUserVO.setToTime(toTime);
            }
            
            String userGrade = (String) requestVO.getRequestMap().get("USERGRADE");
            if(!BTSLUtil.isNullString(userGrade)){
                String userGradeCode = null;
                GradeVO gradeVO = new GradeVO();
                List userGradeList = new CategoryGradeDAO().loadGradeList(con, userCatCode);
                List<String> gradeCodeList = new ArrayList<String>();
                int userGradeLists=userGradeList.size();
                for (int i = 0; i <userGradeLists ; i++) {
                    gradeVO = (GradeVO) userGradeList.get(i);
                    gradeCodeList.add(gradeVO.getGradeCode());
                }
                     
                if(gradeCodeList.contains(userGrade)){
                    userGradeCode = userGrade.trim();
                    channelUserVO.setUserGrade(userGradeCode);
                }else if(!gradeCodeList.contains(userGrade)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GRADE_INVALID);
                }
            }
            
            String geocode=(String) requestMap.get("GEOGRAPHYCODE");
            
            if (!BTSLUtil.isNullString(geocode)){    
                // logic to validate the passed geocode
                GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
                boolean isValidGeoCode=false;
                // check for other level (SE and Retailer)
                if (userCategoryVO.getSequenceNumber()>1){
                    LogFactory.printLog(methodName, "low level hirearchy = "+geocode, LOG);
                    
                    String parentGeoCode=parentChannelUserVO.getGeographicalCode();
                    
                    List<String> geoDomainListUnderParent= geographyDAO.loadGeographyHierarchyUnderParent(con,
                    		parentGeoCode,parentChannelUserVO.getNetworkID(),userCategoryVO.getGrphDomainType());
                    
                    if(geoDomainListUnderParent.contains(geocode)) {
                        LogFactory.printLog(methodName, "low level hirearchy 1= "+geocode, LOG);
                        
                        
                        isValidGeoCode=true;
                        defaultGeoCode=geocode;
                    }
                }else{
                    LogFactory.printLog(methodName, "top level hirearchy = "+geocode, LOG);
                    
                    if(!geographyDAO.isGeographicalDomainExist(con, geocode, true)){
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 
                    }
                    defaultGeoCode=geocode;
                    isValidGeoCode=true;
                }

                if(!isValidGeoCode){
                    requestVO.setMessageCode(PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
                }
                LogFactory.printLog(methodName, "Passed GeoCode = "+defaultGeoCode, LOG);
            }
            
            if(!BTSLUtil.isNullString(defaultGeoCode)){
                channelUserVO.setGeographicalCode(defaultGeoCode);
                final ArrayList geoList = new ArrayList();
                final UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
                userGeographiesVO.setUserId(channelUserVO.getUserID());
                userGeographiesVO.setGraphDomainCode(channelUserVO.getGeographicalCode());
                LogFactory.printLog(methodName, "channelUserVO.getGeographicalCode() >> " + channelUserVO.getGeographicalCode(), LOG);
                geoList.add(userGeographiesVO);
                int deleteUserGeo=new UserGeographiesDAO().deleteUserGeographies(con, channelUserVO.getUserID());
                if (deleteUserGeo <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        "Exception:deleteUserGeo <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
                final int addUserGeo = new UserGeographiesDAO().addUserGeographyList(con, geoList);
                if (addUserGeo <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        "Exception:addUserGeo <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
            }
            
            String services = (String) requestVO.getRequestMap().get("SERVICES");
            final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
            ListValueVO listValueVO = null;
            List serviceList = null;
            String networkCode = (String) requestVO.getRequestMap().get("EXTNWCODE");
            try {
                serviceList = servicesTypeDAO.loadServicesList(con, networkCode, PretupsI.C2S_MODULE, userCatCode, false);
                if(!BTSLUtil.isNullString(services)){
                    List<String> serviceTypeList = new ArrayList<String>();
                    int serviceListsizes=serviceList.size();
                    for (int i = 0; i < serviceListsizes; i++) {
                        listValueVO = (ListValueVO) serviceList.get(i);
                        serviceTypeList.add(listValueVO.getValue());
                    }
                    boolean isServiceValid = true;
                    final String[] givenService = services.split(",");
                    int givenServices=givenService.length;
                    for (int i = 0; i < givenServices; i++) {
                        if(!serviceTypeList.contains(givenService[i])){
                            isServiceValid = false;
                        }
                    }
                    if(isServiceValid){
                        servicesTypeDAO.deleteUserServices(con, channelUserVO.getUserID());
                        servicesTypeDAO.addUserServicesList(con, channelUserVO.getUserID(), givenService, PretupsI.YES);
                    }else{
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_INVALID);
                    }
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
            }
            
            String groupRole = (String) requestVO.getRequestMap().get("GROUPROLE");
            if (PretupsI.YES.equalsIgnoreCase(userCategoryVO.getWebInterfaceAllowed())) {
                final UserRolesDAO userRolesDAO = new UserRolesDAO();
                String[] roles = null;

                if (!BTSLUtil.isNullString(Constants.getProperty("EXT_USER_REG_GROUP_ROLE_REQ"))) {
                    if(!BTSLUtil.isNullString(groupRole)){
                        Map rolesMap = userRolesDAO.loadRolesListByGroupRole(con, channelUserVO.getCategoryCode(), "Y");
                        Set rolesKeys = rolesMap.keySet();
                        List<String> rolesListNew=new ArrayList<String>();
                        Iterator keyiter = rolesKeys.iterator();
                        while(keyiter.hasNext()){
                            String rolename=(String)keyiter.next();
                            List rolesVOList=(ArrayList)rolesMap.get(rolename);
                            rolesListNew=new ArrayList();
                            Iterator i=rolesVOList.iterator();
                            while(i.hasNext()){
                                UserRolesVO rolesVO=(UserRolesVO)i.next();
                                if("Y".equalsIgnoreCase(rolesVO.getStatus())){
                                    rolesListNew.add(rolesVO.getRoleCode());
                                }
                              }
                            if(!rolesListNew.isEmpty()){
                                if(rolesListNew.contains(groupRole)){
                                    roles = new String[]{ groupRole };
                                    userRolesDAO.deleteUserRoles(con, channelUserVO.getUserID());
                                    userRolesDAO.addUserRolesList(con, channelUserVO.getUserID(), roles);
                                }else{
                                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                                  }
                              }
                        }
                    }
                }
            }

            
            
            
            // insert in to user table
            final int userCount = new UserDAO().updateUser(con, channelUserVO);
            if (userCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                    "Exception:userCount <=0 ");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
            }
            
            // insert data into channel users table
            final int userChannelCount = channelUserDao.updateChannelUserInfo(con, channelUserVO);
            if (userChannelCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                    "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
            }
            if (userPhoneList != null && !userPhoneList.isEmpty()) {
                final int phoneCount = userDAO.updateInsertDeleteUserPhoneList(con, (ArrayList)userPhoneList);
                if (phoneCount <= 0) {
                    try {
                        con.rollback();
                    } catch (SQLException e) {
                        LOG.errorTrace(methodName, e);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        "Exception:Update count <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
                }
            }
            con.commit();
            requestMap.put("CHNUSERVO", channelUserVO);
            requestVO.setRequestMap(requestMap);

            if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                // send a message to the user about there activation
                /*
                 */// code commented for sonar issue Dodgy - Redundant nullcheck
                   // of value known to be non-null
                BTSLMessages btslPushMessage = null;

                // 11-MAR-2014
                String[] arrArray = null;
                String[] arrArray1=null;
                String messageCode = null;
                String star = "*****";
                if (isWebLoginIdPassswordChanged && isPinChanged) {
                    arrArray = new String[] { channelUserVO.getLoginID(), modifedPassword, senderPin };
                    arrArray1 = new String[] { channelUserVO.getLoginID(), star, star};
                    messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY;
                } else if (isWebLoginIdPassswordChanged) {
                    arrArray = new String[] { channelUserVO.getLoginID(), modifedPassword };
                    arrArray1 = new String[] { channelUserVO.getLoginID(), star};
                    messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY;
                } else if (isWebLoginIdChanged && isPinChanged) {
                    arrArray = new String[] { channelUserVO.getLoginID(), senderPin };
                    arrArray1 = new String[] { channelUserVO.getLoginID(), star};
                    messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY;
                } else if (isWebPasswordChanged && isPinChanged) {
                    arrArray = new String[] { modifedPassword, senderPin };
                    arrArray1 = new String[] {star, star};
                    messageCode = PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY;
                } else if (isWebLoginIdChanged) {
                    arrArray = new String[] { channelUserVO.getLoginID() };
                    arrArray1 = new String[] {channelUserVO.getLoginID() };
                    messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY;
                } else if (isWebPasswordChanged) {
                    arrArray = new String[] { modifedPassword };
                    arrArray1 = new String[] {star};
                    messageCode = PretupsErrorCodesI.CHNL_USER_PWD_MODIFY;
                } else if (isPinChanged) {
                    arrArray = new String[] { senderPin };
                    arrArray1 = new String[] {star};
                    messageCode = PretupsErrorCodesI.CHNL_USER_PIN_MODIFY;
                } else {
                    messageCode = PretupsErrorCodesI.USER_MODIFY_SUCCESS;
                }
                // Ended Here
                if (!BTSLUtil.isNullString(messageCode) && arrArray != null && arrArray.length > 0 && arrArray1 != null && arrArray1.length > 0) {
                    btslPushMessage = new BTSLMessages(messageCode, arrArray);
                    requestVO.setMessageArguments(arrArray1);
                    requestVO.setMessageCode(messageCode);
                    new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, null, null, locale, requestVO.getExternalNetworkCode()).push();
                } else if (!BTSLUtil.isNullString(messageCode)) {
                    btslPushMessage = new BTSLMessages(messageCode);
                    requestVO.setMessageCode(messageCode);
                    new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, null, null, locale, requestVO.getExternalNetworkCode()).push();
                }

            }
        } catch (BTSLBaseException be) {
            requestVO.setSuccessTxn(false);

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
            LOG.errorTrace(methodName, be);
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                LOG.errorTrace(methodName, ee);
            }
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                "Exception:" + e.getMessage());
            requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            channelUserDao = null;
            userDAO = null;
            channelUserVO = null;
            categoryVO = null;
            userCategoryVO = null;

            if(mcomCon != null){mcomCon.close("ModifyChannelUserController#process");mcomCon=null;}
            LogFactory.printLog(methodName, " Exited ", LOG);
        }
    }

    /**
     * @description : This method to prepare the userphoneVO to update user
     *              information
     * @author :diwakar
     * @return : ArrayList
     */
    private List prepareUserPhoneVOList(Connection con, HashMap requestMap, ChannelUserVO channelUserVO, Date currentDate,
    		ArrayList oldPhoneList, String senderPin) throws Exception {
        final String methodName = "prepareUserPhoneVOList";
        LogFactory.printLog(methodName, "Entered oldPhoneList.size()=" + oldPhoneList.size() + ",newMsisdnList.size()=" + ((ArrayList) requestMap.get("MSISDNLIST"))
                .size(), LOG);

        final List newMsisdnList = (List) requestMap.get("MSISDNLIST");
        NetworkPrefixVO networkPrefixVO = null;
        String msisdn = null;
        String stkProfile = null;
        final String oldUserPhoneID = null;
        final List phoneList = new ArrayList();
        final List stkProfileList = userDAO.loadPhoneProfileList(con, channelUserVO.getCategoryCode());
        if (stkProfileList != null) {
            final ListValueVO listValueVO = (ListValueVO) stkProfileList.get(0);
            stkProfile = listValueVO.getValue();
        }
        if (newMsisdnList != null && !newMsisdnList.isEmpty()) {
            UserPhoneVO phoneVO = null;
            UserPhoneVO oldPhoneVO = null;
            for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
                phoneVO = new UserPhoneVO();
                msisdn = (String) newMsisdnList.get(i);
                phoneVO.setMsisdn(msisdn);
                phoneVO.setPinModifyFlag(true);
                phoneVO.setPhoneProfile(stkProfile);
                if (!BTSLUtil.isNullString(phoneVO.getMsisdn())) {

                    if (oldPhoneList != null && !oldPhoneList.isEmpty()) {
                        if (i < oldPhoneList.size()) {
                            oldPhoneVO = (UserPhoneVO) oldPhoneList.get(oldPhoneList.size() - (i + 1));
                        }
                    }
                    phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()));
                    if (oldPhoneVO != null) {
                        phoneVO.setUserPhonesId(oldPhoneVO.getUserPhonesId());
                        phoneVO.setPinModifyFlag(false);
                        phoneVO.setOperationType("U");
                        phoneVO.setPinModifiedOn(oldPhoneVO.getPinModifiedOn());
                        phoneVO.setPinRequired(oldPhoneVO.getPinRequired());
                        phoneVO.setSmsPin(oldPhoneVO.getSmsPin());
                        phoneVO.setIdGenerate(false);
                    } else if (BTSLUtil.isNullString(oldUserPhoneID)) {
                        phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                        phoneVO.setOperationType("I");
                        phoneVO.setPinModifyFlag(true);
                        phoneVO.setPinModifiedOn(currentDate);
                        phoneVO.setSmsPin(BTSLUtil.encryptText(senderPin));
                        phoneVO.setIdGenerate(true);
                        phoneVO.setPinRequired(PretupsI.YES);
                    }

                    phoneVO.setUserId(channelUserVO.getUserID());
                    // set the default values
                    phoneVO.setCreatedBy(senderVO.getUserID());
                    phoneVO.setModifiedBy(senderVO.getUserID());
                    phoneVO.setCreatedOn(currentDate);
                    phoneVO.setModifiedOn(currentDate);
                    phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                    phoneVO.setPinModifiedOn(currentDate);
                    final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL
                        .getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
                    phoneVO.setPrefixID(prefixVO.getPrefixID());
                    if (msisdn.equals((String) requestMap.get("PRIMARYMSISDN"))) {
                        channelUserVO.setMsisdn(msisdn);
                        phoneVO.setPrimaryNumber(PretupsI.YES);
                    } else {
                        phoneVO.setPrimaryNumber(PretupsI.NO);
                    }
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(msisdn));
                        boolean numberAllowed = false;
                        if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn, "", PretupsI.PORTED_IN);
                            if (!numberAllowed) {
                                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_IN);
                            }
                        } else {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn, "", PretupsI.PORTED_OUT);
                            if (numberAllowed) {
                                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_OUT);
                            }
                        }
                    }

                    if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                        final String[] arr = { phoneVO.getMsisdn() };
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                    }
                    phoneList.add(phoneVO);
                } else {
                    if (oldPhoneList != null && !oldPhoneList.isEmpty()) {
                        if (i < oldPhoneList.size()) {
                            oldPhoneVO = (UserPhoneVO) oldPhoneList.get(oldPhoneList.size() - (i + 1));
                            phoneVO = new UserPhoneVO();
                            phoneVO.setUserPhonesId(oldPhoneVO.getUserPhonesId());
                        }
                    }
                    if (!BTSLUtil.isNullString(phoneVO.getUserPhonesId())) {
                        if (!phoneVO.isIdGenerate()) {
                            phoneVO.setOperationType("D");
                        }
                        phoneList.add(phoneVO);

                    }
                }
                oldPhoneVO = null;
                phoneVO = null;
            }

        }
        return phoneList;
    }
}
