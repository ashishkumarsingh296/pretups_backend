package com.client.pretups.channel.transfer.requesthandler;

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
import com.btsl.pretups.domain.businesslogic.CategoryReqGtwTypeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
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
import com.txn.pretups.roles.businesslogic.UserRolesTxnDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class AddChannelUserController implements ServiceKeywordControllerI {
    private static final Log LOG = LogFactory.getLog(AddChannelUserController.class.getName());
    private ChannelUserVO channelUserVO = null;
    private UserDAO userDAO = null;
    private CategoryVO categoryVO = null;
    private ChannelUserDAO channelUserDao = null;
    private ChannelUserWebDAO channelUserWebDao = null;
    private ExtUserDAO extUserDao = null;
    private ChannelUserVO parentChannelUserVO = null;
    private ChannelUserVO senderVO = null;
    private static final String CLASSNAME = "AddChannelUserController";

    /**
     * Method Process
     * Process Method , Processes external channel user registration request
     * 
     * @param requestVO
     */
    @Override
    public void process(RequestVO requestVO) {
        final String methodName = "process";
        LogFactory.printLog(methodName, "Entered requestVO=" + requestVO, LOG);
        Connection con = null;MComConnectionI mcomCon = null;
        final HashMap requestMap = requestVO.getRequestMap();
        channelUserDao = new ChannelUserDAO();
        channelUserWebDao = new ChannelUserWebDAO();
        channelUserVO = new ChannelUserVO();
        userDAO = new UserDAO();
        extUserDao = new ExtUserDAO();
        OperatorUtilI operatorUtili = null;
        Locale locale = null;
        String senderPin = "";
        String webPassword = null;
        String randomPwd = null;
        String defaultGeoCode = "";
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            final String userCategeryCode = ((String) requestMap.get("USERCATCODE")).trim();
            senderVO = (ChannelUserVO) requestVO.getSenderVO();

            // Load category
            final List catList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con, userCategeryCode);

            // if null category does not exist
            if (catList == null || catList.isEmpty()) {
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
            }

            // If channel user is not owner of it's category, then it's parent
            // details should not be blank.
            categoryVO = (CategoryVO) catList.get(0);
            if (categoryVO.getSequenceNumber() > 1) {
                final String parentMsisdn = (String) requestMap.get("PARENTMSISDN");
                final String parentExtCode = (String) requestMap.get("PARENTEXTERNALCODE");
                if ((BTSLUtil.isNullString(parentMsisdn))&&BTSLUtil.isNullString(parentExtCode)) {
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_PARENTMSISDN_BLANK);
                }
            }

            channelUserVO.setCategoryVO(categoryVO);
            channelUserVO.setCategoryCode(categoryVO.getCategoryCode());
            channelUserVO.setCategoryName(categoryVO.getCategoryName());
            channelUserVO.setDomainTypeCode(categoryVO.getDomainTypeCode());
            requestVO.setUserCategory(categoryVO.getCategoryCode());
            // User name set
            channelUserVO.setUserName(requestMap.get("USERNAME").toString().trim());// User
            // Name
            // Mandatory
            // Value
            // short name set

            channelUserVO.setShortName(BTSLUtil.NullToString((String) requestMap.get("SHORTNAME")));
            /* If IS_DEFAULT_PROFILE is true in system then default geography assigned to user*/
            if((boolean)((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()){
                 // Web Login of User
                // Set default geographical code
                defaultGeoCode = extUserDao.getDefaultGeoCodeDtlBasedOnNetwork(con, requestVO, requestVO.getExternalNetworkCode());
                LogFactory.printLog(methodName, "defaultGeoCode = " + defaultGeoCode, LOG);
                // 11-MAR-2014
                if (!BTSLUtil.isNullString(defaultGeoCode)) {
                    channelUserVO.setGeographicalCode(defaultGeoCode);
                } else {
                    channelUserVO.setGeographicalCode("COMMON1");
                    // Ended Here
                }
            }else{
                channelUserVO.setGeographicalCode(senderVO.getgeographicalCodeforNewuser());
                     
            }

            String webLoginId = BTSLUtil.NullToString((String)requestMap.get("WEBLOGINID"));
            String primaryMsisdn = BTSLUtil.NullToString((String)requestMap.get("PRIMARYMSISDN"));
            if (PretupsI.YES.equalsIgnoreCase(categoryVO.getWebInterfaceAllowed())) {
                // Set MSISDN as login id if Login Id is blank in request
                if (BTSLUtil.isNullString(webLoginId)) {
                    channelUserVO.setLoginID(primaryMsisdn);
                } else {
                    if (userDAO.isUserLoginExist(con, webLoginId, null)) {
                        final String[] argsArray = { webLoginId };
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
                    }

                    channelUserVO.setLoginID(webLoginId);
                }

                webPassword = (String) requestVO.getRequestMap().get("WEBPASSWORD");
                if (!BTSLUtil.isNullString(webPassword)) {
                    final Map errorMessageMap = operatorUtili.validatePassword(webLoginId, webPassword);
                    if (null != errorMessageMap && errorMessageMap.size() > 0) {
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID);
                    }
                }
                // Assign Primary MSISDN as web password for null password value
                // in request.
                if (BTSLUtil.isNullString(webPassword)) {
                    randomPwd = operatorUtili.generateRandomPassword();
                    channelUserVO.setPassword(BTSLUtil.encryptText(randomPwd));
                } else {
                    channelUserVO.setPassword(BTSLUtil.encryptText(webPassword));
                }
            }else {
                // 01-APR-2104 if category do not have web access and
                // XMLGW/EXTGW is allowed then WEBLOGINID is required else
                // create user without loginid
                final List<String> gwAccessTypeList = new CategoryReqGtwTypeDAO().loadCategoryRequestGwType(con, userCategeryCode);
                final String webLoginID = webLoginId;
                if (gwAccessTypeList != null && !gwAccessTypeList.isEmpty()) {
                    if (gwAccessTypeList.contains(PretupsI.REQUEST_SOURCE_TYPE_EXTGW) || gwAccessTypeList.contains(PretupsI.REQUEST_SOURCE_TYPE_XMLGW) || gwAccessTypeList
                        .contains(PretupsI.REQUEST_SOURCE_TYPE_WEB)) {
                        if (BTSLUtil.isNullString(webLoginID)) {
                            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_BLANK);
                        } else {
                            if (userDAO.isUserLoginExist(con, webLoginID, null)) {
                                final String[] argsArray = { webLoginID };
                                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
                            }

                        }

                    }

                } else if (!BTSLUtil.isNullString(webLoginID)) {

                    if (userDAO.isUserLoginExist(con, webLoginID, null)) {
                        final String[] argsArray = { webLoginID };
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
                    }
                }
                channelUserVO.setLoginID(webLoginID);
                webPassword = (String) requestVO.getRequestMap().get("WEBPASSWORD");
                if (!BTSLUtil.isNullString(webPassword)) {
                    final Map errorMessageMap = operatorUtili.validatePassword(webLoginID, webPassword);
                    if (null != errorMessageMap && errorMessageMap.size() > 0) {
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID);
                    }
                }
                // Assign Primary MSISDN as web password for null password value
                // in request.
                if (BTSLUtil.isNullString(webPassword)) {
                    randomPwd = operatorUtili.generateRandomPassword();
                    channelUserVO.setPassword(BTSLUtil.encryptText(randomPwd));
                } else {
                    channelUserVO.setPassword(BTSLUtil.encryptText(webPassword));
                }

            }
            // Ended Here
            // User name prefix.
            final String userPrifix = ((String) requestMap.get("USERNAMEPREFIX")).toUpperCase();
            channelUserVO.setUserNamePrefix(userPrifix);

            // External code is not unique for HCPT INDONESIA implementation.
            final String extCode = (String) requestVO.getRequestMap().get("EXTERNALCODE");
            if (!BTSLUtil.isNullString(extCode)) {
                final boolean isExtCodeExist = channelUserWebDao.isExternalCodeExist(con, extCode, null);
                if (isExtCodeExist) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTERNAL_CODE_ALREARY_EXIST);
                }
            }
            channelUserVO.setExternalCode(extCode);

            // Set actual network of the user for
            // DiwakarString
            final String actualNetworkCode = requestVO.getExternalNetworkCode();
            if (!BTSLUtil.isNullString(actualNetworkCode)) {
                final String status = "'" + PretupsI.STATUS_DELETE + "','" + PretupsI.STATUS_CANCELED + "'";
                final List networkList = new NetworkDAO().loadNetworkList(con, status);
                NetworkVO networkVO = null;
                boolean actualNetworkExist = false;
                if (networkList != null && !networkList.isEmpty()) {
                    for (int i = 0, j = networkList.size(); i < j; i++) {
                        networkVO = (NetworkVO) networkList.get(i);
                        if (networkVO.getNetworkCode().equalsIgnoreCase(actualNetworkCode)) {
                            actualNetworkExist = true;
                        }
                    }
                }
                if (actualNetworkExist) {
                    channelUserVO.setNetworkCode(actualNetworkCode.trim());
                } else {
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_ACTUAL_NW_CODE_INVALID);
                }
            }

            // Email ID check
            final String emailID = BTSLUtil.NullToString((String) requestVO.getRequestMap().get("EMAILID"));
            channelUserVO.setEmail(emailID);

            // Generate user id for new channel user
            channelUserVO.setNetworkID(requestVO.getExternalNetworkCode());
            String parentExtCode = ((String) requestMap.get("PARENTEXTERNALCODE")).trim();
            String parentMsisdn = (String) requestMap.get("PARENTMSISDN");

            boolean isownerIDNew = false;
            // Mandatory check
            // parent login id exist check in db and load parent user id details

            // snippet start
            if (!BTSLUtil.isNullString(parentMsisdn) && !BTSLUtil.isNullString(parentExtCode)) {
                parentExtCode = parentExtCode.trim();
                parentMsisdn = parentMsisdn.trim();
                final String filteredParentMsisdn = PretupsBL.getFilteredMSISDN(parentMsisdn);

                if (!BTSLUtil.isValidMSISDN(filteredParentMsisdn)) {
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_INVALID_MSISDN);
                }

                final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";

                parentChannelUserVO = extUserDao.loadUsersDetailsforExtReq(con, filteredParentMsisdn, null, PretupsI.STATUS_NOTIN, status);

                // commented to load the parent channel user details on the
                // basis of parent external code
                ChannelUserVO parentChannelUserVO1 = null;
                parentChannelUserVO1 = extUserDao.loadUsersDetailsforExtCodeReq(con, parentExtCode, null, PretupsI.STATUS_NOTIN, status);

                if (parentChannelUserVO != null && parentChannelUserVO1 != null) {
                    if (!parentChannelUserVO.getUserID().equals(parentChannelUserVO1.getUserID())) {
                        // Throw exception that either MSISDN is wrong or
                        // external code of parent
                        // external code and MSISDN not match for same parent.
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_PARENT_IS_INVALID);
                    } else if (!PretupsI.STATUS_ACTIVE.equals(parentChannelUserVO.getStatus())) {
                        // throw exp parent not active
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    } else {
                        channelUserVO.setParentID(parentChannelUserVO.getUserID());
                        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                    }
                }else if (parentChannelUserVO != null) {
                    if (!PretupsI.STATUS_ACTIVE.equals(parentChannelUserVO.getStatus())) {
                        // throw exp parent not active
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    } else {
                        channelUserVO.setParentID(parentChannelUserVO.getUserID());
                        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                    }
                } else {
                    // Throw exception if parent does not exist.
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_EXIST);
                }
            } else if (!BTSLUtil.isNullString(parentExtCode)) {
                final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";
                parentChannelUserVO = extUserDao.loadUsersDetailsforExtCodeReq(con, parentExtCode, null, PretupsI.STATUS_NOTIN, status);
                if (parentChannelUserVO != null) {
                    if (!PretupsI.STATUS_ACTIVE.equals(parentChannelUserVO.getStatus())) {
                        // Throw exception if parent not active
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    }
                    channelUserVO.setParentID(parentChannelUserVO.getUserID());
                    channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                } else {
                    // Throw exception if parent is not exist
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_EXIST);
                }
            } else if (!BTSLUtil.isNullString(parentMsisdn)) {
                parentMsisdn = parentMsisdn.trim();
                final String filteredParentMsisdn = PretupsBL.getFilteredMSISDN(parentMsisdn);
                if (!BTSLUtil.isValidMSISDN(filteredParentMsisdn)) {
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_INVALID_MSISDN);
                }
                final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";
                parentChannelUserVO = channelUserDao.loadUsersDetails(con, filteredParentMsisdn, null, PretupsI.STATUS_NOTIN, status);
                if (parentChannelUserVO != null) {
                    if (!PretupsI.STATUS_ACTIVE.equals(parentChannelUserVO.getStatus())) {
                        // throw exp parent not active
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    }
                    channelUserVO.setParentID(parentChannelUserVO.getUserID());
                    channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                } else {
                    // throw parent is not exist
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_EXIST);
                }
            } else {
                isownerIDNew = true;
                channelUserVO.setParentID(PretupsI.ROOT_PARENT_ID);
             // it will
                channelUserVO.setOwnerID(channelUserVO.getUserID());
                // set
                // after
                // user id
                // generated
            }
            // code for geography is moved here after parent details fetched as parent geocode is required
            String geocode=(String) requestMap.get("GEOGRAPHYCODE");
            LogFactory.printLog(methodName, "geocode value = "+geocode, LOG);

            if (!BTSLUtil.isNullString(geocode)){    
                // logic to validate the passed geocode
                GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
                boolean isValidGeoCode=false;
                // check for other level (SE and Retailer)
                if (categoryVO.getSequenceNumber()>1){
                    LogFactory.printLog(methodName, "low level hirearchy = "+geocode, LOG);
                    
                    String parentGeoCode=parentChannelUserVO.getGeographicalCode();
                    
                    List<String> geoDomainListUnderParent= geographyDAO.loadGeographyHierarchyUnderParent(con,parentGeoCode,
                    		parentChannelUserVO.getNetworkID(),categoryVO.getGrphDomainType());
                    
                    if(geoDomainListUnderParent.contains(geocode)){
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
                    throw new BTSLBaseException(CLASSNAME, "loadGeographyUnderParent", PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
                }
                channelUserVO.setGeographicalCode(defaultGeoCode);
                LogFactory.printLog(methodName, "Passed GeoCode = "+defaultGeoCode, LOG);
            }
    
            // User hierarchy check
            {
                final String networkCode = (String) requestVO.getRequestMap().get("EXTNWCODE");

                channelUserVO.setUserID(generateUserId(channelUserVO.getNetworkID(), categoryVO.getUserIdPrefix()));
                if (isownerIDNew) {
                    channelUserVO.setOwnerID(channelUserVO.getUserID());
                }
                LOG.debug(CLASSNAME, "process : channelUserVO.getUserID = " + channelUserVO.getUserID());
                /*
                 * If USR_APPROVAL_LEVEL = 0 no approval required, if
                 * USR_APPROVAL_LEVEL = 1 level 1 approval required,
                 * if USR_APPROVAL_LEVEL = 2 level 2 approval required'
                 * While adding user check whether the approval is required or
                 * not
                 * if USR_APPROVAL_LEVEL > 0 
                 * set status = N(New)//approval required
                 * else
                 * set status = Y(Active)
                 */

                // Set the approval level
                LOG.debug(CLASSNAME, "process : ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue() = " + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue());
                if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue() > 0) {
                    channelUserVO.setStatus(PretupsI.USER_STATUS_NEW);// N
                    // Active
                } else {
                    channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// Y
                    // Active
                }
                channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// N
                // New

                final Date currentDate = new Date();
                // prepare UserPhone VO list
                final List userPhoneList = prepareUserPhoneVOList(con, requestMap, channelUserVO, currentDate, senderPin);
                // set some use full parameter
                channelUserVO.setPasswordModifiedOn(currentDate);
                channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);
                channelUserVO.setCreationType(PretupsI.EXTERNAL_SYSTEM_USR_CREATION_TYPE);
                channelUserVO.setExternalCode(BTSLUtil.NullToString((String) requestMap.get("EXTERNALCODE")));// External
                // Code
                channelUserVO.setEmpCode((String) requestMap.get("EMPCODE"));
                channelUserVO.setUserCode(primaryMsisdn);
                channelUserVO.setContactNo(BTSLUtil.NullToString((String) requestMap.get("CONTACTNUMBER")));
                channelUserVO.setContactPerson(BTSLUtil.NullToString((String) requestMap.get("CONTACTPERSON")));
                channelUserVO.setSsn(BTSLUtil.NullToString((String) requestMap.get("SSN")));
                channelUserVO.setAddress1(BTSLUtil.NullToString((String) requestMap.get("ADDRESS1")));
                channelUserVO.setAddress2(BTSLUtil.NullToString((String) requestMap.get("ADDRESS2")));
                channelUserVO.setCity(BTSLUtil.NullToString((String) requestMap.get("CITY")));
                channelUserVO.setState(BTSLUtil.NullToString((String) requestMap.get("STATE")));
                channelUserVO.setCountry(BTSLUtil.NullToString((String) requestMap.get("COUNTRY")));
                channelUserVO.setCreatedBy(senderVO.getUserID());

                final Date curDate = new Date();
                channelUserVO.setCreatedOn(curDate);
                
                String mobileNumber = primaryMsisdn;
                if(!BTSLUtil.isNullString(mobileNumber)){
                    NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(mobileNumber)));
                    if(TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed()) && (prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserVO.getNetworkCode()))) {
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
                    }
                    
                    if(TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed()) && (new UserDAO().isMSISDNExist(con, mobileNumber, ""))){
                        final String[] arr = { mobileNumber };
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                    }
                }else{
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_PRIMARY_MSISDN_BLANK);
                }
                channelUserVO.setMsisdn(BTSLUtil.NullToString(mobileNumber));
                channelUserVO.setModifiedBy(senderVO.getUserID());
                channelUserVO.setModifiedOn(currentDate);
                channelUserVO.setUserProfileID(channelUserVO.getUserID());
                channelUserVO.setPasswordModifiedOn(currentDate);
                channelUserVO.setPasswordCountUpdatedOn(currentDate);

                channelUserVO.setCompany(BTSLUtil.NullToString((String) requestMap.get("COMPANY")));
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
                
                if (TypesI.YES.equals(categoryVO.getLowBalAlertAllow())) {
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
                        channelUserVO.setLowBalAlertAllow("N");
                    }

                }else {
                    channelUserVO.setLowBalAlertAllow("N");
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
                    for(int i=0; i<allowedIPAddress.length; i++){
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
                
                // insert in to user table
                final int userCount = userDAO.addUser(con, channelUserVO);
                if (userCount <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        "Exception:userCount <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
                // Phone VO entries
                UserPhoneVO phoneVO = new UserPhoneVO();
                final ArrayList phoneList = new ArrayList();
                //
                channelUserVO.setMsisdn(BTSLUtil.NullToString(primaryMsisdn));
                phoneVO.setMsisdn(channelUserVO.getMsisdn());
                phoneVO.setPrimaryNumber(PretupsI.YES);

                // get PIN of send if not comming then generate random pin as
                // discussed with Divyakant Sir
                senderPin = (String) requestMap.get("PIN");
                if (BTSLUtil.isNullString(senderPin)) {
                    senderPin = operatorUtili.generateRandomPin();
                }
                phoneVO.setSmsPin(BTSLUtil.encryptText(senderPin));
                phoneVO.setPinRequired(PretupsI.YES);
                phoneVO.setPhoneProfile(categoryVO.getCategoryCode());
                phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                phoneVO.setUserId(channelUserVO.getUserID());
                // set the default values
                phoneVO.setCreatedBy(channelUserVO.getUserID());
                phoneVO.setModifiedBy(channelUserVO.getUserID());
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
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                        "ChannelUserRegZainNigeriaRequestHandler[process]", "", "", "", "Exception:Update count <=0 for user phones");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
                // end
                /*
                 * If user status is Y(active) means user is activated at the
                 * creation time, so we are setting the
                 * activated_on = currentDate. This indicate user is activated
                 * on the same date
                 */
                if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                    channelUserVO.setActivatedOn(currentDate);
                } else {
                    channelUserVO.setActivatedOn(null);
                }
                channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                channelUserVO.setMpayProfileID("");
                String inSuspend = (String) requestVO.getRequestMap().get("INSUSPEND");
                if(!BTSLUtil.isNullString(inSuspend)){
                    if(inSuspend.equals(PretupsI.YES) || inSuspend.equals(PretupsI.NO)){
                        channelUserVO.setInSuspend(inSuspend);
                    }else{
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_INSUSPEND_INVALID);
                    }    
                }else{
                    channelUserVO.setInSuspend("N");
                }    
                String outSuspend = (String) requestVO.getRequestMap().get("OUTSUSPEND");
                if(!BTSLUtil.isNullString(outSuspend)){
                    if(outSuspend.equals(PretupsI.YES) || outSuspend.equals(PretupsI.NO)){
                        channelUserVO.setOutSuspened(outSuspend);
                    }else{
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_OUTSUSPEND_INVALID);
                    }        
                }else{
                    channelUserVO.setOutSuspened("N");
                }   
                // Setting Default grades
                String defaultUserGradeCode = null;
                String userGrade = (String) requestVO.getRequestMap().get("USERGRADE");
               
                GradeVO gradeVO = new GradeVO();
                List userGradeList = new CategoryGradeDAO().loadGradeList(con, userCategeryCode);
                List<String> gradeCodeList = new ArrayList<String>();
                int userGradeListSizes=userGradeList.size();
                for (int i = 0; i <userGradeListSizes ; i++) {
                    gradeVO = (GradeVO) userGradeList.get(i);
                    gradeCodeList.add(gradeVO.getGradeCode());
                }
                     
                if(!BTSLUtil.isNullString(userGrade) && gradeCodeList.contains(userGrade)){
                    defaultUserGradeCode = userGrade.trim();
                    channelUserVO.setUserGrade(defaultUserGradeCode);    
                }else if(!BTSLUtil.isNullString(userGrade) && !gradeCodeList.contains(userGrade)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GRADE_INVALID);
                }else{
                    defaultUserGradeCode = extUserDao.getDefaultGradeCode(con, userCategeryCode);
                    if (BTSLUtil.isNullString(defaultUserGradeCode)) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_DEFAULT_GRADE_NOT_FOUND);
                    }else{
                        defaultUserGradeCode = defaultUserGradeCode.trim();
                        channelUserVO.setUserGrade(defaultUserGradeCode);    
                    }  
                }                    
                

                // Setting Default Commission Profile Set ID
                String defaultCommissionProfileSetID = null;
                try {
                    defaultCommissionProfileSetID = extUserDao.getDefaultCommisionProfileSetIDByCategoryID(con, userCategeryCode, networkCode, defaultUserGradeCode);
                    defaultCommissionProfileSetID = defaultCommissionProfileSetID.trim();
                    channelUserVO.setCommissionProfileSetID(defaultCommissionProfileSetID);
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    if (BTSLUtil.isNullString(defaultCommissionProfileSetID)) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_DEFAULT_COMMISSION_PROFILE_NOT_FOUND);
                    }
                }

                // Setting Default Transfer Profile ID
                String defaultTransferProfileID = null;
                try {
                    defaultTransferProfileID = extUserDao.getDefaultTransferProfileIDByCategoryID(con, networkCode, userCategeryCode, PretupsI.PARENT_PROFILE_ID_USER);
                    defaultTransferProfileID = defaultTransferProfileID.trim();
                    channelUserVO.setTransferProfileID(defaultTransferProfileID);
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    if (BTSLUtil.isNullString(defaultTransferProfileID)) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_DEFAULT_TRANSFER_PROFILE_NOT_FOUND);
                    }
                }

                // Assign Services
                String services = (String) requestVO.getRequestMap().get("SERVICES");
                final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
                ListValueVO listValueVO = null;
                List serviceList = null;
                try {
                    serviceList = servicesTypeDAO.loadServicesList(con, networkCode, PretupsI.C2S_MODULE, userCategeryCode, false);
                    if(!BTSLUtil.isNullString(services)){
                        List<String> serviceTypeList = new ArrayList<String>();
                        int serviceLists=serviceList.size();
                        for (int i = 0; i <serviceLists ; i++) {
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
                            servicesTypeDAO.addUserServicesList(con, channelUserVO.getUserID(), givenService, PretupsI.YES);
                        }else{
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_INVALID);
                        }
                    }else{
                        final String[] service = new String[serviceList.size()];
                        int serviceListSize=serviceList.size();
                        for (int i = 0; i < serviceListSize; i++) {
                            listValueVO = (ListValueVO) serviceList.get(i);
                            service[i] = listValueVO.getValue();

                        }
                        if(!serviceList.isEmpty()){
                            final int servicesCount = servicesTypeDAO.addUserServicesList(con, channelUserVO.getUserID(), service, PretupsI.YES);
                            if (servicesCount <= 0) {
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
                }
                // Assign Roles
                String groupRole = (String) requestVO.getRequestMap().get("GROUPROLE");
                if (PretupsI.YES.equalsIgnoreCase(categoryVO.getWebInterfaceAllowed())) {
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
                                        userRolesDAO.addUserRolesList(con, channelUserVO.getUserID(), roles);
                                    }else{
                                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                                      }
                                  }
                            }
                        }else{
                            final Map defaultRoleCodes = new UserRolesTxnDAO().loadDefaultRolesListByGroupRole(con, userCategeryCode
                                    , Constants.getProperty("EXT_GROUP_ROLE"));
                            if (defaultRoleCodes.size() > 0 || !defaultRoleCodes.isEmpty()) {
                                final Iterator iterator = defaultRoleCodes.keySet().iterator();

                                UserRolesVO rolesVO = null;
                                String groupRoles = null;
                                final List<String> roleCodeList = new ArrayList<String>();
                                while (iterator.hasNext()) {
                                    groupRoles = (String) iterator.next();
                                    final List list = (List) defaultRoleCodes.get(groupRoles);
                                    if (list != null) {
                                    	int listSizes=list.size();
                                        for (int i = 0, j = listSizes; i < j; i++) {
                                            rolesVO = (UserRolesVO) list.get(i);
                                            roleCodeList.add(rolesVO.getRoleCode());
                                        }
                                    }
                                }
                                if (!roleCodeList.isEmpty()) {
                                    roles = new String[roleCodeList.size()];
                                    for (int i = 0, j = roleCodeList.size(); i < j; i++) {
                                        roles[i] = roleCodeList.get(i);
                                    }

                                    final int insertCount = userRolesDAO.addUserRolesList(con, channelUserVO.getUserID(), roles);
                                    if (insertCount <= 0) {
                                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_ROLES_NOT_FOUND);
                                    }
                                }else {
                                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_ROLES_NOT_FOUND);
                                }
                                // Ended Here
                            }
                        }
                    }
                }

                // Low Balance Alert Allow

                // Insert data into channel users table
                final int userChannelCount = channelUserDao.addChannelUser(con, channelUserVO);
                if (userChannelCount <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        "Exception:Update count <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                } else {
                    // USER_ID ,GEOLI
                    final ArrayList geoList = new ArrayList();
                    final UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
                    userGeographiesVO.setUserId(channelUserVO.getUserID());
                    userGeographiesVO.setGraphDomainCode(channelUserVO.getGeographicalCode());
                    LogFactory.printLog(methodName, "channelUserVO.getGeographicalCode() >> " + channelUserVO.getGeographicalCode(), LOG);
                    geoList.add(userGeographiesVO);
                    final int addUserGeo = new UserGeographiesDAO().addUserGeographyList(con, geoList);
                    if (addUserGeo <= 0) {
                        con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                            "Exception:Update count <=0 ");
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                    }
                }
                con.commit();
                requestMap.put("CHNUSERVO", channelUserVO);
                requestVO.setRequestMap(requestMap);
                // Push Messages
                phoneVO = null;
                int usersPhoneLists=userPhoneList.size();
                for (int i = 0, j =usersPhoneLists; i < j; i++) {
                    phoneVO = (UserPhoneVO) userPhoneList.get(i);
                    if (TypesI.YES.equals(phoneVO.getPrimaryNumber())) {
                        break;
                    }
                }
                if (!BTSLUtil.isNullString(phoneVO.getMsisdn())) {
                    final String filterMsisdn = PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn());
                    phoneVO.setMsisdn(filterMsisdn);
                    if (TypesI.YES.equals(phoneVO.getPrimaryNumber())) {
                        channelUserVO.setMsisdn(phoneVO.getMsisdn());
                        locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                    }
                }
                LOG.debug("AddChannelUserController process : ", " channelUserVO.getStatus() = " + channelUserVO.getStatus());
                BTSLMessages btslPushMessage = null;
                
                String[] arrArray = null;
                if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                    // send a message to the user about there activation
                    if (locale == null) {
                        locale = requestVO.getLocale();
                    }
                    if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                        // changes by hitesh ghanghas
                        if (!BTSLUtil.isNullString(channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
                            if (BTSLUtil.isNullString(webPassword)) {
                                arrArray = new String[] { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", randomPwd, senderPin };
                            } else {
                                arrArray = new String[] { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", webPassword, senderPin };
                            }
                            requestVO.setMessageCode(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE);
                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                            final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                            pushMessage.push();

                        }else {
                            if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                                requestVO.setMessageCode(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE);
                                if (BTSLUtil.isNullString(webPassword)) {
                                    arrArray = new String[] { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", randomPwd, senderPin };
                                } else {
                                    arrArray = new String[] { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", webPassword, senderPin };
                                }
                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                                final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                                pushMessage.push();

                            } else {
                                arrArray = new String[] { channelUserVO.getMsisdn(), "", senderPin };
                                requestVO.setMessageCode(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE);
                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                                final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                                pushMessage.push();
                            }
                        }
                        
                    } else if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && TypesI.NO.equals(categoryVO.getSmsInterfaceAllowed()) && !BTSLUtil
                        .isNullString(channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
                        // send message for login id
                        requestVO.setMessageCode(PretupsErrorCodesI.USER_WEB_ACTIVATE);
                        if (BTSLUtil.isNullString(webPassword)) {
                            arrArray = new String[] { channelUserVO.getLoginID(), randomPwd };
                        } else {
                            arrArray = new String[] { channelUserVO.getLoginID(), webPassword };
                        }
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                        final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                        pushMessage.push();
                        
                    }

                    // pusing individual sms
                    if (channelUserVO.getMsisdnList() != null && channelUserVO.getMsisdnList().size() > 1) {
                        // it mean that it has secondary number and now push
                        // message to individual secondary no

                        final List newMsisdnList = channelUserVO.getMsisdnList();
                        UserPhoneVO newUserPhoneVO = null;
                        // Email for pin & password
                        for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
                            btslPushMessage = null;
                            newUserPhoneVO = (UserPhoneVO) newMsisdnList.get(i);
                            if (TypesI.NO.equals(newUserPhoneVO.getPrimaryNumber())) {
                                arrArray = new String[] { newUserPhoneVO.getMsisdn(), "", senderPin };
                                locale = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());
                                
                                requestVO.setMessageCode(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE);
                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                                final PushMessage pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                                pushMessage.push();
                                
                            }
                        }
                    }


                } else {

                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
                    requestVO.setMessageCode(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
                }
                requestVO.setMessageArguments(arrArray);
                // Ended Here
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
            channelUserWebDao = null;
            userDAO = null;
            channelUserVO = null;
            categoryVO = null;

            if(mcomCon != null){mcomCon.close("AddChannelUserController#process");mcomCon=null;}
            LogFactory.printLog(methodName, " Exited ", LOG);
        }
    }

    /**
     * @description : This method to generate the userId while inserting new
     *              record
     * @param networkCode
     *            String
     * @param prefix
     *            String
     * @return String
     */
    private String generateUserId(String networkCode, String prefix) throws Exception {
    	StringBuilder loggerValue= new StringBuilder(); 
    	loggerValue.setLength(0);
    	loggerValue.append("Entered p_networkCode=");
    	loggerValue.append(networkCode);
    	loggerValue.append(networkCode);
    	loggerValue.append(", p_prefix=");
    	loggerValue.append(prefix);
        LogFactory.printLog("generateUserId", loggerValue.toString(), LOG);

        final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, networkCode)) + "", length);
        id = networkCode + prefix + id;
       
        loggerValue.setLength(0);
    	loggerValue.append("Exiting id=");
    	loggerValue.append(id);
        LogFactory.printLog("generateUserId",  loggerValue.toString(), LOG);

        return id;
    }

    /**
     * @description : Method to prepareUserPhoneVOList
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return : List
     */
    private List prepareUserPhoneVOList(Connection con, HashMap requestMap, ChannelUserVO channelUserVO, Date currentDate, String senderPin) throws Exception {
        final String methodName = "prepareUserPhoneVOList";
        LogFactory.printLog(methodName, "Entered channelUserVO.getCategoryCode()=" + channelUserVO.getCategoryCode(), LOG);
    	
        final List phoneList = new ArrayList();
        final List msisdnList = (List) requestMap.get("MSISDNLIST");
        NetworkPrefixVO networkPrefixVO = null;
        String msisdn = null;
        String stkProfile = null;
        final List stkProfileList = userDAO.loadPhoneProfileList(con, channelUserVO.getCategoryCode());
        if (stkProfileList != null && !stkProfileList.isEmpty()) {
            final ListValueVO listValueVO = (ListValueVO) stkProfileList.get(0);
            stkProfile = listValueVO.getValue();
        }
        if (msisdnList != null && !msisdnList.isEmpty()) {
            UserPhoneVO phoneVO = null;
            for (int i = 0, j = msisdnList.size(); i < j; i++) {
                msisdn = (String) msisdnList.get(i);
                if (!BTSLUtil.isNullString(msisdn)) {
                    msisdn = PretupsBL.getFilteredMSISDN(msisdn);
                    phoneVO = new UserPhoneVO();
                    phoneVO.setMsisdn(msisdn);
                    phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                    phoneVO.setUserId(channelUserVO.getUserID());
                    phoneVO.setSmsPin(BTSLUtil.encryptText(senderPin));
                    phoneVO.setPinRequired(PretupsI.YES);
                    // set the default values
                    phoneVO.setCreatedBy(PretupsI.SYSTEM_USER);
                    phoneVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    phoneVO.setCreatedOn(currentDate);
                    phoneVO.setModifiedOn(currentDate);
                    phoneVO.setPinModifiedOn(currentDate);
                    phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                    phoneVO.setPhoneProfile(stkProfile);
                    final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL
                        .getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
                    phoneVO.setPrefixID(prefixVO.getPrefixID());
                    if (msisdn.equals(PretupsBL.getFilteredMSISDN((String) requestMap.get("PRIMARYMSISDN")))){
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
                    // MNP Code End
                    phoneVO.setPinModifyFlag(true);
                    if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                        final String[] arr = { phoneVO.getMsisdn() };
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                    }
                    phoneList.add(phoneVO);
                }
            }
        }
        channelUserVO.setMsisdnList((ArrayList)phoneList);
        return phoneList;
    }
}
