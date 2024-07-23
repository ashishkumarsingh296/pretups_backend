package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
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
import com.btsl.common.EmailSendToUser;
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
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OperatorUserLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.domain.businesslogic.CategoryTxnDAO;
import com.txn.pretups.master.businesslogic.DivisionDeptTxnDAO;
import com.txn.user.businesslogic.UserTxnDAO;
/**
 * @description : This controller class will be used to process the update
 *              request for operator user through external system via operator
 *              receiver.
 * @author : Vipan
 */

public class OPTUserModController implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(OPTUserModController.class.getName());
    private static final String PROCESS = "OPTUserModController[process]";
    private ChannelUserVO _channelUserVO = null;
    private UserDAO _userDAO = null;
    private CategoryVO _categoryVO = null;
    private ChannelUserVO _senderVO = null;
    private ChannelUserVO parentChannelUserVO = null;

    /**
     * Method Process
     * Process Method , Processes Operator user update request
     * 
     * @param p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        final String methodName = "process";
        StringBuilder loggerValue= new StringBuilder(); 
		       
        if (_log.isDebugEnabled()) {
            _log.debug(PROCESS, "Entered p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        final HashMap requestMap = p_requestVO.getRequestMap();
        _channelUserVO = new ChannelUserVO();
        _userDAO = new UserDAO();
        UserVO requestUserVO = null;
        OperatorUtilI operatorUtili = null;
        Locale locale = null;
        String defaultGeoCode = "";
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception while loading the class at the call:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OPTUserAddController[process]", "", "", "",
            		loggerValue.toString() );
        }
        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();

            _senderVO = (ChannelUserVO) p_requestVO.getSenderVO();

            final String userExistingLoginId = (String) requestMap.get("USRLOGINID");

            if (BTSLUtil.isNullString(userExistingLoginId)) {
                final String[] argsArray = { userExistingLoginId };
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_EXT_OPT_USER_LOGINID_BLANK, argsArray);
            }

            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            // Load user details for modification.
            requestUserVO = new UserTxnDAO().loadUsersDetailsByLoginId(con, userExistingLoginId);

            // If no user details found for the user, throw an exception.
            if (requestUserVO == null) {
                final String[] argsArray = { userExistingLoginId };
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, argsArray);
            }
            // Get category code
            String catCode = requestUserVO.getCategoryCode();
            loggerValue.setLength(0);
        	loggerValue.append("process  User Found : _channelUserVO.getUserID = " );
        	loggerValue.append(_channelUserVO.getUserID());
            _log.debug(this, loggerValue);

            // get User Id
            String userId = requestUserVO.getUserID();
            userId = userId.trim();
            _channelUserVO.setUserID(userId);
            catCode = catCode.trim();

            final CategoryDAO categoryDAO = new CategoryDAO();
            final ArrayList catagorynList = categoryDAO.loadCategoryList(con, PretupsI.OPERATOR_TYPE_OPT, (String) requestMap.get("CATCODE"));

            if (catagorynList != null && !catagorynList.isEmpty()) {
                final Iterator catagoryIte = catagorynList.iterator();
                boolean flag = false;

                while (catagoryIte.hasNext()) {
                    final CategoryVO categoryVO = (CategoryVO) catagoryIte.next();
                    if (categoryVO.getCategoryCode().equalsIgnoreCase(catCode)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    final String[] argsArray = { requestUserVO.getCategoryVO().getCategoryName() };
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_CATAGORY_NOT_ALOOWED, argsArray);
                }
            } else {
                final String[] argsArray = { requestUserVO.getCategoryVO().getCategoryName() };
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_CATAGORY_NOT_ALOOWED, argsArray);
            }
            loggerValue.setLength(0);
        	loggerValue.append("process  Before setting values : _channelUserVO.getUserID = ");
        	loggerValue.append(_channelUserVO.getUserID());
            _log.debug(this,  loggerValue);

            final List catList = new CategoryTxnDAO().loadOptCategoryDetailsUsingCategoryCode(con, catCode);

            // if null category does not exist
            if (catList == null || catList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
            }

            // If channel user is not owner of it's category, then it's parent
            // details should not be blank.
            _categoryVO = (CategoryVO) catList.get(0);

            _channelUserVO.setCategoryVO(_categoryVO);
            
            // User External Code set
            boolean blank = true;
            final String extCode = (String) requestMap.get("EXTERNALCODE");
            blank = BTSLUtil.isNullString(extCode);
            if (!blank) {
                _channelUserVO.setExternalCode(extCode.toString().trim());
            }

            final String actualNetworkCode = (String) p_requestVO.getExternalNetworkCode();
            if (!BTSLUtil.isNullString(actualNetworkCode)) {
                final String status = "'" + PretupsI.STATUS_DELETE + "','" + PretupsI.STATUS_CANCELED + "'";
                final List networkList = new NetworkDAO().loadNetworkList(con, status);
                NetworkVO networkVO = null;
                boolean actualNetworkExist = false;
               
                if (networkList != null && !networkList.isEmpty()) {
                	 int networkListSize = networkList.size();
                    for (int i = 0, j = networkListSize; i < j; i++) {
                        networkVO = (NetworkVO) networkList.get(i);
                        if (networkVO.getNetworkCode().equalsIgnoreCase(actualNetworkCode)) {
                            actualNetworkExist = true;
                        }
                    }
                }
                if (actualNetworkExist) {
                    _channelUserVO.setNetworkCode(actualNetworkCode.trim());
                } else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_ACTUAL_NW_CODE_INVALID);
                }
            }
            
            String loginID = ((String) requestMap.get("LOGINID")).trim();
            if (!BTSLUtil.isNullString(loginID)) {
                loginID = loginID.trim();
                final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";
                parentChannelUserVO = new ChannelUserDAO().loadUsersDetailsByLoginId(con, loginID, null, PretupsI.STATUS_NOTIN, status);
            }
            
            // User Name set
            final String modifiedUsername = (String) requestMap.get("USERNAME");
            blank = BTSLUtil.isNullString(modifiedUsername);
            if (!blank) {
                _channelUserVO.setUserName(modifiedUsername.toString().trim());
            }

            // short name set
            final String modifiedUserShortname = (String) requestMap.get("SHORTNAME");
            blank = BTSLUtil.isNullString(modifiedUserShortname);
            if (!blank) {
                _channelUserVO.setShortName(modifiedUserShortname.toString().trim());
            }

            // User prefix check
            final String userPrifix = (String) requestMap.get("USERNAMEPREFIX");
            blank = BTSLUtil.isNullString(userPrifix);
            if (!blank) {
                _channelUserVO.setUserNamePrefix(userPrifix.toUpperCase());
            }

            // set External Code
            final String modifiedEmpCode = (String) requestMap.get("SUBSCRIBERCODE");
            blank = BTSLUtil.isNullString(modifiedEmpCode);
            if (!blank) {
                _channelUserVO.setEmpCode(modifiedEmpCode);
            }

            // Set Contact Person , contact number , ssn , address1, address2,
            // city , state, country ,email id
            final String modifiedContactPerson = (String) requestMap.get("CONTACTPERSON");
            blank = BTSLUtil.isNullString(modifiedContactPerson);
            if (!blank) {
                _channelUserVO.setContactPerson(modifiedContactPerson.toString().trim());
            }

            final String modifiedContactNumber = (String) requestMap.get("CONTACTNUMBER");
            blank = BTSLUtil.isNullString(modifiedContactNumber);
            if (!blank) {
                _channelUserVO.setContactNo(modifiedContactNumber.toString().trim());
            }

            final String modifiedSSN = (String) requestMap.get("SSN");
            blank = BTSLUtil.isNullString(modifiedSSN);
            if (!blank) {
                _channelUserVO.setSsn(modifiedSSN.toString().trim());
            }

            // Address1
            final String modifedAddress1 = (String) requestMap.get("ADDRESS1");
            blank = BTSLUtil.isNullString(modifedAddress1);
            if (!blank) {
                _channelUserVO.setAddress1(modifedAddress1.toString().trim());
            }

            // Mobile Number
            final String mobileNumber = (String) requestMap.get("MOBILENUMBER");
            blank = BTSLUtil.isNullString(mobileNumber);
            if (!blank) {
                if(TypesI.YES.equals(_channelUserVO.getCategoryVO().getSmsInterfaceAllowed())){
                    NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(mobileNumber)));
                    if (prefixVO == null || !prefixVO.getNetworkCode().equals(_channelUserVO.getNetworkCode())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
                    }
                    
                    if(new UserDAO().isMSISDNExist(con, mobileNumber, "")){
                        final String[] arr = { mobileNumber };
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                    }

                }
                _channelUserVO.setMsisdn(mobileNumber.toString().trim());
                _channelUserVO.setUserCode(mobileNumber.toString().trim());
            }

            // Division Number
            final String division = (String) requestMap.get("DIVISION");
            blank = BTSLUtil.isNullString(division);
            if (!blank) {
                final DivisionDeptTxnDAO depttxnDAO = new DivisionDeptTxnDAO();
                final boolean flag = depttxnDAO.isDivisionExists(con, (String) requestMap.get("DIVISION"));
                if (!flag) {
                    final String[] argsArray = { (String) requestMap.get("DIVISION") };
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_DIVISION_INVALID, argsArray);
                }
                _channelUserVO.setDivisionCode(BTSLUtil.NullToString((String) requestMap.get("DIVISION")));

            }

            // Department Number
            final String department = (String) requestMap.get("DEPARTMENT");

            blank = BTSLUtil.isNullString(department);
            if (!blank) {
                final DivisionDeptTxnDAO depttxnDAO = new DivisionDeptTxnDAO();
                boolean flag = false;
                if (!BTSLUtil.isNullString(_channelUserVO.getDivisionCode())) {
                    flag = depttxnDAO.isDepartmentExitsUnderDivision(con, _channelUserVO.getDivisionCode(), (String) requestMap.get("DEPARTMENT"));
                } else {
                    flag = depttxnDAO.isDepartmentExitsUnderDivision(con, requestUserVO.getDivisionCode(), (String) requestMap.get("DEPARTMENT"));
                }

                if (!flag) {
                    final String[] argsArray = { (String) requestMap.get("DEPARTMENT") };
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_DEPARTMENT_INVALID, argsArray);
                }
                _channelUserVO.setDepartmentCode(BTSLUtil.NullToString((String) requestMap.get("DEPARTMENT")));

            }

            final String designation = (String) requestMap.get("DESIGNATION");
            blank = BTSLUtil.isNullString(designation);
            if (!blank) {
                _channelUserVO.setDesignation(designation.toString().trim());
            }

            // Address2
            final String modifedAddress2 = (String) requestMap.get("ADDRESS2");
            blank = BTSLUtil.isNullString(modifedAddress2);
            if (!blank) {
                _channelUserVO.setAddress2(modifedAddress2.toString().trim());
            }

            final String modifedCity = (String) requestMap.get("CITY");
            blank = BTSLUtil.isNullString(modifedCity);
            if (!blank) {
                _channelUserVO.setCity(modifedCity.toString().trim());
            }

            // State
            final String modifedState = (String) requestMap.get("STATE");
            blank = BTSLUtil.isNullString(modifedState);
            if (!blank) {
                _channelUserVO.setState(modifedState.toString().trim());
            }

            // Country
            final String modifedCountry = (String) requestMap.get("COUNTRY");
            blank = BTSLUtil.isNullString(modifedCountry);
            if (!blank) {
                _channelUserVO.setCountry(modifedCountry.toString().trim());
            }

            // EmailId
            final String modifedEmail = (String) requestMap.get("EMAILID");
            blank = BTSLUtil.isNullString(modifedEmail);
            boolean validEmail = false;
            if (!blank) {
                validEmail = BTSLUtil.validateEmailID(modifedEmail);
                if (validEmail) {
                    _channelUserVO.setEmail(modifedEmail.toString().trim());
                }
            }

            // WebLoginId
            final String modifedLoginId = (String) requestMap.get("WEBLOGINID");
            final String existingLoginId = (String) requestUserVO.getLoginID();
            if (!BTSLUtil.isNullString(modifedLoginId) && !modifedLoginId.equals(existingLoginId)) {
                if (new UserDAO().isUserLoginExist(con, modifedLoginId, null)) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_LOGINID_ALREADY_EXIST);
                    // 11-MAR-2014
                } else {
                    _channelUserVO.setLoginID(modifedLoginId.toString().trim());
                    // Ended Here
                }
            } else {
                _channelUserVO.setLoginID(existingLoginId.toString().trim());
            }

            // Web Password
            final String modifedPassword = (String) requestMap.get("WEBPASSWORD");

            // 11-MAR-2104 for setFlagForSMS
            boolean isWebLoginIdChanged = false;
            boolean isWebPasswordChanged = false;
            boolean isWebLoginIdPassswordChanged = false;

            if (!BTSLUtil.isNullString(modifedLoginId) && !modifedLoginId.equalsIgnoreCase(existingLoginId) && !BTSLUtil.isNullString(modifedPassword) && !modifedPassword
                .equalsIgnoreCase(BTSLUtil.decryptText(requestUserVO.getPassword()))) {
                isWebLoginIdPassswordChanged = true;
            } else if (!BTSLUtil.isNullString(modifedLoginId) && !modifedLoginId.equalsIgnoreCase(existingLoginId)) {
                isWebLoginIdChanged = true;
            } else if (!BTSLUtil.isNullString(modifedPassword) && !modifedPassword.equalsIgnoreCase(BTSLUtil.decryptText(requestUserVO.getPassword()))) {
                isWebPasswordChanged = true;
            }

            // Ended Here

            // If in modify request, web password is not mentioned, then set the
            // previous one.

            if (!BTSLUtil.isNullString(modifedPassword)) {
                final HashMap errorMessageMap = operatorUtili.validatePassword(_channelUserVO.getLoginID(), modifedPassword);
                if (null != errorMessageMap && errorMessageMap.size() > 0) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID);
                }
            }
            // Assign Primary MSISDN as web password for null password value in
            // request.
            if (BTSLUtil.isNullString(modifedPassword)) {
                final String randomPwd = operatorUtili.generateRandomPassword();
                _channelUserVO.setPassword(BTSLUtil.encryptText(randomPwd));
            } else {
                _channelUserVO.setPassword(BTSLUtil.encryptText(modifedPassword));
            }

            String status = (String) p_requestVO.getRequestMap().get("STATUS");
            blank = BTSLUtil.isNullString(status);
            if(!blank){
                if(status.equals(PretupsI.USER_STATUS_ACTIVE) || status.equals(PretupsI.USER_STATUS_SUSPEND)){
                    _channelUserVO.setStatus(status);
                }else{
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_STATUS_INVALID);
                }
            }
            
            // Set some use full parameter
            final Date currentDate = new Date();
            // prepare user phone list
            _channelUserVO.setModifiedBy(_senderVO.getUserID());
            _channelUserVO.setModifiedOn(currentDate);
            if (_channelUserVO.getMsisdn() != null) {
                _channelUserVO.setUserCode(_channelUserVO.getMsisdn());
            }

            final String userProfileId = _channelUserVO.getUserProfileID();
            _channelUserVO.setUserProfileID(userProfileId);
            _channelUserVO.setLowBalAlertAllow(_channelUserVO.getLowBalAlertAllow());

            Date appointmentDate = null;
            if(!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("APPOINTMENTDATE"))){
                try {
                    appointmentDate = BTSLUtil.getDateFromDateString((String) p_requestVO.getRequestMap().get("APPOINTMENTDATE"));
                } catch (Exception e) {
                	loggerValue.setLength(0);
                	loggerValue.append("Exception ");
                	loggerValue.append(e);
                	_log.error(methodName, loggerValue );
    			   _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
                }
                _channelUserVO.setAppointmentDate(appointmentDate);
            }
            
            final String allowedIPs = (String) p_requestVO.getRequestMap().get("ALLOWEDIP");
            if(!BTSLUtil.isNullString(allowedIPs)){
                String[] allowedIPAddress = allowedIPs.split(",");
                for(int i=0; i<allowedIPAddress.length; i++){
                    String splitAllowedIP = allowedIPAddress[i];
                    if(!BTSLUtil.isValidateIpAddress(splitAllowedIP)){
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_IP_INVALID);
                    }
                }
                _channelUserVO.setAllowedIps(allowedIPs);
            }
            
            final String allowedDays = (String) p_requestVO.getRequestMap().get("ALLOWEDDAYS");
            if(!BTSLUtil.isNullString(allowedDays)){
                if(!BTSLUtil.isValidateAllowedDays(allowedDays)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_ALLOWEDDAYS_INVALID);
                }
                _channelUserVO.setAllowedDays(allowedDays);
            }
            
            final String fromTime = (String) p_requestVO.getRequestMap().get("ALLOWEDTIMEFROM");
            final String toTime = (String) p_requestVO.getRequestMap().get("ALLOWEDTIMETO");

            if(!BTSLUtil.isNullString(fromTime)){
                if(!BTSLUtil.isValidateAllowedTime(fromTime)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_FROMTIME_INVALID);
                }
                _channelUserVO.setFromTime(fromTime);
            }
            
            if(!BTSLUtil.isNullString(fromTime)){
                if(!BTSLUtil.isValidateAllowedTime(fromTime)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_TOTIME_INVALID);
                }
                _channelUserVO.setToTime(toTime);
            }
            
            String groupRole = (String) p_requestVO.getRequestMap().get("GROUPROLE");
            // Assign Roles
            if (PretupsI.YES.equalsIgnoreCase(_categoryVO.getWebInterfaceAllowed())) {
                final UserRolesDAO userRolesDAO = new UserRolesDAO();
                String[] roles = null;

                if (!BTSLUtil.isNullString(Constants.getProperty("EXT_USER_REG_GROUP_ROLE_REQ"))) {
                    if(!BTSLUtil.isNullString(groupRole)){
                        Map rolesMap = userRolesDAO.loadRolesListByGroupRole(con, catCode, PretupsI.YES);
                        Set rolesKeys = rolesMap.keySet();
                        List<String> rolesListNew=new ArrayList<String>();
                        Iterator keyiter = rolesKeys.iterator();
                        while(keyiter.hasNext()){
                            String rolename=(String)keyiter.next();
                            List rolesVOList=(List)rolesMap.get(rolename);
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
                                    userRolesDAO.deleteUserRoles(con, _channelUserVO.getUserID());
                                    userRolesDAO.addUserRolesList(con, _channelUserVO.getUserID(), roles);
                                    break;
                                }else{
                                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                                  }
                              }
                        }
                    }
                }
                }

            String geocode=(String) requestMap.get("GEOGRAPHYCODE");
            
            if (!BTSLUtil.isNullString(geocode)){    
                // logic to validate the passed geocode
                GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
                boolean isValidGeoCode=false;
                // check for other level (SE and Retailer)
                if (_categoryVO.getSequenceNumber()>1){
                    LogFactory.printLog(methodName, "low level hirearchy = "+geocode, _log);
                             
                    String parentGeoCode=parentChannelUserVO.getGeographicalCode();
                             
                    List<String> geoDomainListUnderParent= geographyDAO.loadGeographyHierarchyUnderParent(con,parentGeoCode,
                    		parentChannelUserVO.getNetworkID(),_categoryVO.getGrphDomainType());
                             
                    if(geoDomainListUnderParent.contains(geocode)){
                        LogFactory.printLog(methodName, "low level hirearchy 1= "+geocode, _log);
                                 
                                 
                        isValidGeoCode=true;
                        defaultGeoCode=geocode;
                    }
                }else{
                    LogFactory.printLog(methodName, "top level hirearchy = "+geocode, _log);
                             
                    if(!geographyDAO.isGeographicalDomainExist(con, geocode, true))
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 

                    defaultGeoCode=geocode;
                    isValidGeoCode=true;
                }

                if(!isValidGeoCode){
                    p_requestVO.setMessageCode(PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
                    throw new BTSLBaseException(this, "loadGeographyUnderParent", PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
                }
                if (_log.isDebugEnabled()){
                    _log.debug(PROCESS,"Passed GeoCode = "+defaultGeoCode);
                }
            }
            
            if(!BTSLUtil.isNullString(defaultGeoCode)){
                _channelUserVO.setGeographicalCode(defaultGeoCode);
                final ArrayList geoList = new ArrayList();
                final UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
                userGeographiesVO.setUserId(_channelUserVO.getUserID());
                userGeographiesVO.setGraphDomainCode(_channelUserVO.getGeographicalCode());
                LogFactory.printLog(methodName, "_channelUserVO.getGeographicalCode() >> " + _channelUserVO.getGeographicalCode(), _log);
                geoList.add(userGeographiesVO);
                int deleteUserGeo=new UserGeographiesDAO().deleteUserGeographies(con, _channelUserVO.getUserID());
                if (deleteUserGeo <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, PROCESS, "", "", "",
                        "Exception:deleteUserGeo <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
                final int addUserGeo = new UserGeographiesDAO().addUserGeographyList(con, geoList);
                if (addUserGeo <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, PROCESS, "", "", "",
                        "Exception:addUserGeo <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
            }
                
            
         // Assign Services
            String services = (String) p_requestVO.getRequestMap().get("SERVICES");
            if(TypesI.YES.equals(_channelUserVO.getCategoryVO().getServiceAllowed())){
                final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
                ListValueVO listValueVO = null;
                List serviceList = null;
                try {
                    serviceList = servicesTypeDAO.assignServicesToChlAdmin(con, _channelUserVO.getNetworkCode());
                    if(!BTSLUtil.isNullString(services)){
                        List<String> serviceTypeList = new ArrayList<String>();
                        for (int i = 0; i < serviceList.size(); i++) {
                            listValueVO = (ListValueVO) serviceList.get(i);
                            serviceTypeList.add(listValueVO.getValue());
                        }
                        boolean isServiceValid = true;
                        final String[] givenService = services.split(",");
                        for (int i = 0; i < givenService.length; i++) {
                            if(!serviceTypeList.contains(givenService[i])){
                                isServiceValid = false;
                            }
                        }
                        if(isServiceValid){
                            servicesTypeDAO.deleteUserServices(con, _channelUserVO.getUserID());
                            servicesTypeDAO.addUserServicesList(con, _channelUserVO.getUserID(), givenService, PretupsI.YES);
                        }else{
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_INVALID);
                        }
                    }
                
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(e);
                }
            }
            
            copyNewDateToOldVO(_channelUserVO, requestUserVO);

            // insert in to user table
            loggerValue.setLength(0);
        	loggerValue.append("process  Before user Update : _channelUserVO.getUserID = " );
        	loggerValue.append( _channelUserVO.getUserID());
            _log.debug(this, loggerValue);
            final int userCount = new UserDAO().updateUser(con, requestUserVO);
            if (userCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OPTUserModController[process]", "", "", "",
                    "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
            }

            con.commit();
            OperatorUserLog.apiLog("MODOPTUSR", requestUserVO, _senderVO, p_requestVO);
            _log.debug(this, "process  DB Connection Commited: _channelUserVO.getUserID = " + _channelUserVO.getUserID());
            // send a message to the user about there activation
            /*
             * if(locale==null)
             * locale = p_requestVO.getLocale();
             */// code commented for sonar issue Dodgy - Redundant nullcheck of
               // value known to be non-null
            BTSLMessages btslPushMessage = null;

            // 11-MAR-2014
            String[] arrArray = null;
            String messageCode = null;
            if (isWebLoginIdPassswordChanged) {
                arrArray = new String[] { requestUserVO.getLoginID(), BTSLUtil.decryptText(requestUserVO.getPassword()) };
                messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY;
            } else if (isWebLoginIdChanged) {
                arrArray = new String[] { requestUserVO.getLoginID() };
                messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY;
            } else if (isWebPasswordChanged) {
                arrArray = new String[] { BTSLUtil.decryptText(requestUserVO.getPassword()) };
                messageCode = PretupsErrorCodesI.CHNL_USER_PWD_MODIFY;
            } else {
                messageCode = PretupsErrorCodesI.USER_MODIFY_SUCCESS;
            }
            // Ended Here
            if (!BTSLUtil.isNullString(messageCode) && arrArray != null && arrArray.length > 0) {
                btslPushMessage = new BTSLMessages(messageCode, arrArray);
                p_requestVO.setMessageArguments(arrArray);
                p_requestVO.setMessageCode(messageCode);
                new PushMessage(requestUserVO.getMsisdn(), btslPushMessage, null, null, locale, p_requestVO.getExternalNetworkCode()).push();
            } else if (!BTSLUtil.isNullString(messageCode)) {
                btslPushMessage = new BTSLMessages(messageCode);
                p_requestVO.setMessageCode(messageCode);
                new PushMessage(requestUserVO.getMsisdn(), btslPushMessage, null, null, locale, p_requestVO.getExternalNetworkCode()).push();
            }

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(requestUserVO.getEmail())) {
                final String arr[] = { requestUserVO.getUserName() };
                final String subject = BTSLUtil.getMessage(locale, "user.addoperatoruser.updatesuccessmessage", arr);
                final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, requestUserVO.getNetworkID(), "Email will be delivered shortly",
                    requestUserVO, requestUserVO);
                emailSendToUser.sendMail();
            }

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(be.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, be);
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
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OPTUserModController[process]", "", "", "",
            		loggerValue.toString() );
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {

            _userDAO = null;
            _channelUserVO = null;
            _categoryVO = null;
            requestMap.put("CHNUSERVO", requestUserVO);

            p_requestVO.setRequestMap(requestMap);

			if (mcomCon != null) {
				mcomCon.close("OPTUserModController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exited ");
            }
        }
    }

    private void copyNewDateToOldVO(ChannelUserVO _channelUserVO, UserVO requestUserVO) {
        final String METHOD_NAME = "copyNewDateToOldVO";
        StringBuilder loggerValue= new StringBuilder(); 
		       
        try {

            if (_log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
             	loggerValue.append("Entered New _channelUserVO=" );
             	loggerValue.append(_channelUserVO);
             	loggerValue.append(",Old requestUserVO " );
             	loggerValue.append(requestUserVO);
                _log.debug("copyNewDateToOldVO", loggerValue);
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getExternalCode())) {
                requestUserVO.setExternalCode(_channelUserVO.getExternalCode());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getUserName())) {
                requestUserVO.setUserName(_channelUserVO.getUserName());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getShortName())) {
                requestUserVO.setShortName(_channelUserVO.getShortName());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getExternalCode())) {
                requestUserVO.setExternalCode(_channelUserVO.getExternalCode());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getUserNamePrefix())) {
                requestUserVO.setUserNamePrefix(_channelUserVO.getUserNamePrefix());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getEmpCode())) {
                requestUserVO.setEmpCode(_channelUserVO.getEmpCode());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getContactPerson())) {
                requestUserVO.setContactPerson(_channelUserVO.getContactPerson());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getContactNo())) {
                requestUserVO.setContactNo(_channelUserVO.getContactNo());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getSsn())) {
                requestUserVO.setSsn(_channelUserVO.getSsn());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getAddress1())) {
                requestUserVO.setAddress1(_channelUserVO.getAddress1());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getMsisdn())) {
                requestUserVO.setMsisdn(_channelUserVO.getMsisdn());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getUserCode())) {
                requestUserVO.setUserCode(_channelUserVO.getUserCode());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getDivisionCode())) {
                requestUserVO.setDivisionCode(_channelUserVO.getDivisionCode());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getDepartmentCode())) {
                requestUserVO.setDepartmentCode(_channelUserVO.getDepartmentCode());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getDesignation())) {
                requestUserVO.setDesignation(_channelUserVO.getDesignation());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getAddress2())) {
                requestUserVO.setAddress2(_channelUserVO.getAddress2());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getCity())) {
                requestUserVO.setCity(_channelUserVO.getCity());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getState())) {
                requestUserVO.setState(_channelUserVO.getState());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getCountry())) {
                requestUserVO.setCountry(_channelUserVO.getCountry());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getEmail())) {
                requestUserVO.setEmail(_channelUserVO.getEmail());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getLoginID())) {
                requestUserVO.setLoginID(_channelUserVO.getLoginID());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getPassword())) {
                requestUserVO.setPassword(_channelUserVO.getPassword());
            }

            if (!BTSLUtil.isNullString(_channelUserVO.getModifiedBy())) {
                requestUserVO.setModifiedBy(_channelUserVO.getModifiedBy());
            }

            if (_channelUserVO.getModifiedOn() != null) {
                requestUserVO.setModifiedOn(_channelUserVO.getModifiedOn());
            }
            
            if (_channelUserVO.getStatus() != null) {
                requestUserVO.setStatus(_channelUserVO.getStatus());
            }
            
            if (_channelUserVO.getAppointmentDate() != null) {
                requestUserVO.setAppointmentDate(_channelUserVO.getAppointmentDate());
            }
            
            if (_channelUserVO.getAllowedDays() != null) {
                requestUserVO.setAllowedDays(_channelUserVO.getAllowedDays());
            }
            
            if (_channelUserVO.getAllowedIps() != null) {
                requestUserVO.setAllowedIps(_channelUserVO.getAllowedIps());
            }
            
            if (_channelUserVO.getFromTime() != null) {
                requestUserVO.setFromTime(_channelUserVO.getFromTime());
            }
            
            if (_channelUserVO.getToTime() != null) {
                requestUserVO.setToTime(_channelUserVO.getToTime());
            }

        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(e.getMessage());
            _log.error("copyNewDateToOldVO",  loggerValue );
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exit New _channelUserVO=");
        	loggerValue.append(_channelUserVO);
        	loggerValue.append(",Old requestUserVO " );
        	loggerValue.append(requestUserVO);
            _log.debug("copyNewDateToOldVO",  loggerValue);
        }

    }

}
