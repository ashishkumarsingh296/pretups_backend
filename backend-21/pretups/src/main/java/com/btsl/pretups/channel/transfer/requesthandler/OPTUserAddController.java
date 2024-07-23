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
import com.btsl.pretups.domain.businesslogic.CategoryReqGtwTypeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
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
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.domain.businesslogic.CategoryTxnDAO;
import com.txn.pretups.master.businesslogic.DivisionDeptTxnDAO;
import com.txn.pretups.roles.businesslogic.UserRolesTxnDAO;

/**
 * @description : This controller class will be used to process the add request
 *              for operator user through external system via operator receiver.
 * @author : Vipan
 */

public class OPTUserAddController implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(OPTUserAddController.class.getName());
    private ChannelUserVO _channelUserVO = null;
    private UserDAO _userDAO = null;
    private CategoryVO _categoryVO = null;
    private ChannelUserDAO _channelUserDao = null;
    private ExtUserDAO _extUserDao = null;
    private ChannelUserVO parentChannelUserVO = null;
    private ChannelUserVO _senderVO = null;

    /**
     * Method Process
     * Process Method , Processes external channel user registration request
     * 
     * @param p_requestVO
     */
    public void process(RequestVO p_requestVO) {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
         	loggerValue.append("Entered p_requestVO=" );
         	loggerValue.append(p_requestVO);
            _log.debug("OPTUserAddController process", loggerValue );
        }
        final String methodName = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        final HashMap requestMap = p_requestVO.getRequestMap();
        _channelUserDao = new ChannelUserDAO();
        _channelUserVO = new ChannelUserVO();
        _userDAO = new UserDAO();
        _extUserDao = new ExtUserDAO();
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
            		loggerValue.toString());
        }

        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            final String userCategeryCode = ((String) requestMap.get("USERCATCODE")).trim();

            p_requestVO.setUserCategory(BTSLUtil.NullToString(userCategeryCode));
            
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            _senderVO = (ChannelUserVO) p_requestVO.getSenderVO();

            // Load category
            final ArrayList catList = new CategoryTxnDAO().loadOptCategoryDetailsUsingCategoryCode(con, userCategeryCode);

            // if null category does not exist
            if (catList == null || catList.isEmpty()) {
                throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
            }

            // If channel user is not owner of it's category, then it's parent
            // details should not be blank.
            _categoryVO = (CategoryVO) catList.get(0);

            _channelUserVO.setCategoryVO(_categoryVO);
            _channelUserVO.setCategoryCode(_categoryVO.getCategoryCode());
            _channelUserVO.setCategoryName(_categoryVO.getCategoryName());
            _channelUserVO.setDomainTypeCode(_categoryVO.getDomainTypeCode());
            // User name set
            _channelUserVO.setUserName(requestMap.get("USERNAME").toString().trim());// User
            // Name
            // Mandatory
            // Value
            _channelUserVO.setFirstName(requestMap.get("USERNAME").toString().trim());
            // short name set

            _channelUserVO.setShortName(BTSLUtil.NullToString((String) requestMap.get("SHORTNAME")));
            // Web Login of User

            final CategoryDAO categoryDAO = new CategoryDAO();
            final ArrayList catagorynList = categoryDAO.loadCategoryList(con, PretupsI.OPERATOR_TYPE_OPT, (String) requestMap.get("CATCODE"));

            if (catagorynList != null && !catagorynList.isEmpty()) {
                final Iterator catagoryIte = catagorynList.iterator();
                boolean flag = false;

                while (catagoryIte.hasNext()) {
                    final CategoryVO categoryVO = (CategoryVO) catagoryIte.next();
                    if (categoryVO.getCategoryCode().equalsIgnoreCase(requestMap.get("USERCATCODE").toString().trim())) {
                        flag = true;
                    }
                }
                if (!flag) {
                    final String[] argsArray = { _channelUserVO.getCategoryName() };
                    throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_CATAGORY_NOT_ALOOWED, argsArray);
                }
            } else {
                final String[] argsArray = { _channelUserVO.getCategoryName() };
                throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_CATAGORY_NOT_ALOOWED, argsArray);
            }

            if (PretupsI.YES.equalsIgnoreCase(_categoryVO.getWebInterfaceAllowed())) {
                // Set MSISDN as login id if Login Id is blank in request
                if (BTSLUtil.isNullString((String) requestMap.get("WEBLOGINID"))) {
                    _channelUserVO.setLoginID((String) requestMap.get("USERMSISDN"));
                } else {
                    if (_userDAO.isUserLoginExist(con, (String) requestMap.get("WEBLOGINID"), null)) {
                        final String[] argsArray = { requestMap.get("WEBLOGINID").toString() };
                        throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
                    }

                    _channelUserVO.setLoginID((String) requestMap.get("WEBLOGINID"));
                }

                final String webPassword = (String) p_requestVO.getRequestMap().get("WEBPASSWORD");
                if (!BTSLUtil.isNullString(webPassword)) {
                    final HashMap errorMessageMap = operatorUtili.validatePassword((String) requestMap.get("WEBLOGINID"), webPassword);
                    if (null != errorMessageMap && errorMessageMap.size() > 0) {
                        throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID);
                    }
                }
                // Assign Primary MSISDN as web password for null password value
                // in request.
                if (BTSLUtil.isNullString(webPassword)) {
                    final String randomPwd = operatorUtili.generateRandomPassword();
                    _channelUserVO.setPassword(BTSLUtil.encryptText(randomPwd));
                } else {
                    _channelUserVO.setPassword(BTSLUtil.encryptText(webPassword));
                }
            } else {
                // if category do not have web access and XMLGW/EXTGW is allowed
                // then WEBLOGINID is required else create user without loginid
                final ArrayList<String> gwAccessTypeList = new CategoryReqGtwTypeDAO().loadCategoryRequestGwType(con, userCategeryCode);
                final String webLoginID = (String) requestMap.get("WEBLOGINID");
                if (gwAccessTypeList != null && !gwAccessTypeList.isEmpty()) {
                    if (gwAccessTypeList.contains(PretupsI.REQUEST_SOURCE_TYPE_EXTGW) || gwAccessTypeList.contains(PretupsI.REQUEST_SOURCE_TYPE_XMLGW) || gwAccessTypeList
                        .contains(PretupsI.REQUEST_SOURCE_TYPE_WEB)) {
                        if (BTSLUtil.isNullString(webLoginID)) {
                            throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_BLANK);
                        } else {
                            if (_userDAO.isUserLoginExist(con, webLoginID, null)) {
                                final String[] argsArray = { webLoginID };
                                throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
                            }

                        }

                    }

                } else if (!BTSLUtil.isNullString(webLoginID)) {

                    if (_userDAO.isUserLoginExist(con, webLoginID, null)) {
                        final String[] argsArray = { webLoginID };
                        throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
                    }
                }
                _channelUserVO.setLoginID(webLoginID);
                final String webPassword = (String) p_requestVO.getRequestMap().get("WEBPASSWORD");
                if (!BTSLUtil.isNullString(webPassword)) {
                    final HashMap errorMessageMap = operatorUtili.validatePassword(webLoginID, webPassword);
                    if (null != errorMessageMap && errorMessageMap.size() > 0) {
                        throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID);
                    }
                }
                // Assign Primary MSISDN as web password for null password value
                // in request.
                if (BTSLUtil.isNullString(webPassword)) {
                    final String randomPwd = operatorUtili.generateRandomPassword();
                    _channelUserVO.setPassword(BTSLUtil.encryptText(randomPwd));
                } else {
                    _channelUserVO.setPassword(BTSLUtil.encryptText(webPassword));
                }

            }
            // Ended Here

            // User name prefix.
            final String userPrifix = ((String) requestMap.get("USERNAMEPREFIX")).toUpperCase();
            _channelUserVO.setUserNamePrefix(userPrifix);

            // Division Validation
            if (BTSLUtil.isNullString((String) requestMap.get("DIVISION"))) {
                throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_DIVISION_NOTNULL);
            } else {
                final DivisionDeptTxnDAO depttxnDAO = new DivisionDeptTxnDAO();
                final boolean flag = depttxnDAO.isDivisionExists(con, (String) requestMap.get("DIVISION"));
                if (!flag) {
                    final String[] argsArray = { (String) requestMap.get("DIVISION") };
                    throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_DIVISION_INVALID, argsArray);
                }
                _channelUserVO.setDivisionCode(BTSLUtil.NullToString((String) requestMap.get("DIVISION")));
            }

            // Department Validation
            if (BTSLUtil.isNullString((String) requestMap.get("DEPARTMENT"))) {
                throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_DEPARTMENT_NOTNULL);
            } else {
                final DivisionDeptTxnDAO depttxnDAO = new DivisionDeptTxnDAO();
                final boolean flag = depttxnDAO.isDepartmentExitsUnderDivision(con, BTSLUtil.NullToString((String) requestMap.get("DIVISION")), (String) requestMap
                    .get("DEPARTMENT"));
                if (!flag) {
                    final String[] argsArray = { (String) requestMap.get("DEPARTMENT") };
                    throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_DEPARTMENT_INVALID, argsArray);
                }
                _channelUserVO.setDepartmentCode(BTSLUtil.NullToString((String) requestMap.get("DEPARTMENT")));
            }

            // External code is not unique for HCPT INDONESIA implementation.
            _channelUserVO.setExternalCode((String) p_requestVO.getRequestMap().get("EXTERNALCODE"));

            final String actualNetworkCode = (String) p_requestVO.getExternalNetworkCode();
            if (!BTSLUtil.isNullString(actualNetworkCode)) {
                final String status = "'" + PretupsI.STATUS_DELETE + "','" + PretupsI.STATUS_CANCELED + "'";
                final ArrayList networkList = new NetworkDAO().loadNetworkList(con, status);
                
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
                    throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_ACTUAL_NW_CODE_INVALID);
                }
            }

            // Email ID check
            if (BTSLUtil.isStringContain(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_CREATION_MANDATORY_FIELDS)), "email")) {
                if (BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EMAILID"))) {
                    throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_EMAILID_NOTFOUND);
                }
            }
            
            final String emailID = BTSLUtil.NullToString((String) p_requestVO.getRequestMap().get("EMAILID"));
            _channelUserVO.setEmail(emailID);
            
            //MSISDN check
            String mobileNumber = (String) p_requestVO.getRequestMap().get("MOBILENUMBER");
            if(TypesI.YES.equals(_channelUserVO.getCategoryVO().getSmsInterfaceAllowed())){
                NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(mobileNumber)));
                if (prefixVO == null || !prefixVO.getNetworkCode().equals(_channelUserVO.getNetworkCode())) {
                    throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
                }
                
                if(new UserDAO().isMSISDNExist(con, mobileNumber, "")){
                    final String[] arr = { mobileNumber };
                    throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                }

            }
            // Generate user id for new channel user
            _channelUserVO.setNetworkID(p_requestVO.getExternalNetworkCode());
            String loginID = ((String) requestMap.get("LOGINID")).trim();

            boolean isownerIDNew = false;

            // Mandatory check
            // parent login id exist check in db and load parent user id details
            if (!BTSLUtil.isNullString(loginID)) {
                loginID = loginID.trim();

                final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";
                parentChannelUserVO = _channelUserDao.loadUsersDetailsByLoginId(con, loginID, null, PretupsI.STATUS_NOTIN, status);
                if (parentChannelUserVO != null) {
                    // Throw exception if parent not active
                    if (!PretupsI.STATUS_ACTIVE.equals(parentChannelUserVO.getStatus())) {
                        throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    }

                    _channelUserVO.setParentID(parentChannelUserVO.getUserID());
                    _channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                }
                // Throw exception if parent is not exist
                else {
                    throw new BTSLBaseException("OPTUserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_EXIST);
                }
            } else {
                isownerIDNew = true;
                _channelUserVO.setParentID(PretupsI.ROOT_PARENT_ID);
                _channelUserVO.setOwnerID(_channelUserVO.getUserID());// it will
                // set
                // after
                // user id
                // generated
            }

            // User hierarchy check
            _channelUserVO.setUserID(generateUserId(_channelUserVO.getNetworkID(), _categoryVO.getUserIdPrefix()));
            if (isownerIDNew) {
                _channelUserVO.setOwnerID(_channelUserVO.getUserID());
            }

            _log.debug("OPTUserAddController", "process : _channelUserVO.getUserID = " + _channelUserVO.getUserID());
            /*
             * If EXTSYS_USR_APRL_LEVEL_REQUIRED = 0 no approval required, if
             * EXTSYS_USR_APRL_LEVEL_REQUIRED = 1 level 1 approval required,
             * if EXTSYS_USR_APRL_LEVEL_REQUIRED = 2 level 2 approval required'
             * While adding user check whether the approval is required or not
             * if(EXTSYS_USR_APRL_LEVEL_REQUIRED > 0 )
             * set status = N(New)//approval required
             * else
             * set status = Y(Active)
             */

            // Set the approval level
            String status = (String) p_requestVO.getRequestMap().get("STATUS");
            loggerValue.setLength(0);
        	loggerValue.append("process : ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue() = " );
        	loggerValue.append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue());
            _log.debug("OPTUserAddController",loggerValue);
            if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue() > 0) {
            	// N Active
                _channelUserVO.setStatus(PretupsI.USER_STATUS_NEW);
            } else if(!BTSLUtil.isNullString(status) && (status.equals(PretupsI.USER_STATUS_ACTIVE) || 
            		status.equals(PretupsI.USER_STATUS_SUSPEND))) {
                _channelUserVO.setStatus((String) p_requestVO.getRequestMap().get("STATUS"));// Y
                // Active
            }else{
                _channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// Y
            }
            // N New
            _channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);

            final Date currentDate = new Date();
            // prepare UserPhone VO list
            // ArrayList userPhoneList =
            // prepareUserPhoneVOList(con,requestMap,_channelUserVO,currentDate,senderPin);
            // set some use full parameter
            _channelUserVO.setPasswordModifiedOn(currentDate);
            _channelUserVO.setUserType(PretupsI.OPERATOR_USER_TYPE);
            _channelUserVO.setCreationType(PretupsI.EXTERNAL_SYSTEM_USR_CREATION_TYPE);
            _channelUserVO.setExternalCode(BTSLUtil.NullToString((String) requestMap.get("EXTERNALCODE")));// External
            // Code
            _channelUserVO.setEmpCode((String) requestMap.get("SUBSCRIBERCODE"));
            _channelUserVO.setUserCode(mobileNumber);
            _channelUserVO.setContactNo(BTSLUtil.NullToString((String) requestMap.get("CONTACTNUMBER")));
            _channelUserVO.setContactPerson(BTSLUtil.NullToString((String) requestMap.get("CONTACTPERSON")));
            _channelUserVO.setSsn(BTSLUtil.NullToString((String) requestMap.get("SSN")));
            _channelUserVO.setMsisdn(BTSLUtil.NullToString((String) requestMap.get("MOBILENUMBER")));
            _channelUserVO.setDesignation(BTSLUtil.NullToString((String) requestMap.get("DESIGNATION")));
            _channelUserVO.setDepartmentCode(BTSLUtil.NullToString((String) requestMap.get("DEPARTMENT")));
            _channelUserVO.setDivisionCode(BTSLUtil.NullToString((String) requestMap.get("DIVISION")));
            _channelUserVO.setAddress1(BTSLUtil.NullToString((String) requestMap.get("ADDRESS1")));
            _channelUserVO.setAddress2(BTSLUtil.NullToString((String) requestMap.get("ADDRESS2")));
            _channelUserVO.setCity(BTSLUtil.NullToString((String) requestMap.get("CITY")));
            _channelUserVO.setState(BTSLUtil.NullToString((String) requestMap.get("STATE")));
            _channelUserVO.setCountry(BTSLUtil.NullToString((String) requestMap.get("COUNTRY")));
            _channelUserVO.setCreatedBy(_senderVO.getUserID());

         // code for geography is moved here after parent details fetched as parent geocode is required
            String geocode=(String) requestMap.get("GEOGRAPHYCODE");
            if (_log.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("geocode value = ");
            	loggerValue.append(geocode);
                _log.debug("UserAddController process", loggerValue);
            }

            if (BTSLUtil.isNullString(geocode)){
                defaultGeoCode=_extUserDao.getDefaultGeoCodeDtlBasedOnNetwork(con, p_requestVO,actualNetworkCode);
                if (_log.isDebugEnabled()){
                	loggerValue.setLength(0);
                	loggerValue.append("default GeoCode = ");
                	loggerValue.append(defaultGeoCode);
                    _log.debug("UserAddController process", loggerValue);
                }
            }
            if (!BTSLUtil.isNullString(geocode)){	
                // logic to validate the passed geocode
                GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
                boolean isValidGeoCode=false;
                // check for other level (SE and Retailer)
                if (_categoryVO.getSequenceNumber()>1){
                    LogFactory.printLog(methodName, "low level hirearchy = "+geocode, _log);
         					
                    String parentGeoCode=parentChannelUserVO.getGeographicalCode();
         					
                    List<String> geoDomainListUnderParent= geographyDAO.
                    		loadGeographyHierarchyUnderParent(con,parentGeoCode,parentChannelUserVO.getNetworkID(),_categoryVO.getGrphDomainType());
         					
                    if(geoDomainListUnderParent.contains(geocode)){
                    	loggerValue.setLength(0);
                    	loggerValue.append("low level hirearchy 1= ");
                    	loggerValue.append(geocode);
                        LogFactory.printLog(methodName, loggerValue.toString(), _log);
		
                        isValidGeoCode=true;
                        defaultGeoCode=geocode;
                    }
                }else{
                	loggerValue.setLength(0);
                	loggerValue.append("top level hirearchy = ");
                	loggerValue.append(geocode);
                    LogFactory.printLog(methodName, loggerValue.toString(), _log);
         					
                    if(!geographyDAO.isGeographicalDomainExist(con, geocode, true))
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 

                    defaultGeoCode=geocode;
                    isValidGeoCode=true;
                }

                if(!isValidGeoCode){
                    p_requestVO.setMessageCode(PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
                    throw new BTSLBaseException("UserAddController", "loadGeographyUnderParent", PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
                }
                loggerValue.setLength(0);
            	loggerValue.append("Passed GeoCode = ");
            	loggerValue.append(defaultGeoCode);
                LogFactory.printLog(methodName, loggerValue.toString(), _log);
         }
            
            if(!BTSLUtil.isNullString(defaultGeoCode)){
                _channelUserVO.setGeographicalCode(defaultGeoCode);
			}	
            
            final Date curDate = new Date();
            _channelUserVO.setCreatedOn(curDate);
            
            Date appointmentDate = null;
            if(!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("APPOINTMENTDATE"))){
            	try {
                    appointmentDate = BTSLUtil.getDateFromDateString((String) p_requestVO.getRequestMap().get("APPOINTMENTDATE"));
            	} catch (Exception e) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
            	}
            	 _channelUserVO.setAppointmentDate(appointmentDate);
            }
            
            _channelUserVO.setModifiedBy(_senderVO.getUserID());
            _channelUserVO.setModifiedOn(currentDate);
            _channelUserVO.setUserProfileID(_channelUserVO.getUserID());
            _channelUserVO.setPasswordModifiedOn(currentDate);
            _channelUserVO.setPasswordCountUpdatedOn(currentDate);
            
            final String allowedIPs = (String) p_requestVO.getRequestMap().get("ALLOWEDIP");
            if(!BTSLUtil.isNullString(allowedIPs)){
                String[] allowedIPAddress = allowedIPs.split(",");
                for(int i=0; i<allowedIPAddress.length; i++){
                    String splitAllowedIP = allowedIPAddress[i];
                    if(!BTSLUtil.isValidateIpAddress(splitAllowedIP)){
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_IP_INVALID);
                    }
            	}
                _channelUserVO.setAllowedIps(allowedIPs);
            }
            
            final String allowedDays = (String) p_requestVO.getRequestMap().get("ALLOWEDDAYS");
            if(!BTSLUtil.isNullString(allowedDays)){
                if(!BTSLUtil.isValidateAllowedDays(allowedDays)){
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_ALLOWEDDAYS_INVALID);
                }
                _channelUserVO.setAllowedDays(allowedDays);
            }
            
            final String fromTime = (String) p_requestVO.getRequestMap().get("ALLOWEDTIMEFROM");
            final String toTime = (String) p_requestVO.getRequestMap().get("ALLOWEDTIMETO");

            if(!BTSLUtil.isNullString(fromTime)){
                if(!BTSLUtil.isValidateAllowedTime(fromTime)){
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_FROMTIME_INVALID);
                }
                _channelUserVO.setFromTime(fromTime);
            }
            
            if(!BTSLUtil.isNullString(fromTime)){
                if(!BTSLUtil.isValidateAllowedTime(fromTime)){
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_TOTIME_INVALID);
                }
                _channelUserVO.setToTime(toTime);
            }
            

            // insert in to user table
            final int userCount = _userDAO.addUser(con, _channelUserVO);

            if (userCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OPTUserAddController[process]", "", "", "",
                    "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
            }

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("After Insert in users " );
            	loggerValue.append(_channelUserVO);
                _log.debug("OPTUserAddController process", loggerValue );
            }

            // insert geography info
            
            final ArrayList geoList = new ArrayList();
            final UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
            userGeographiesVO.setUserId(_channelUserVO.getUserID());
            userGeographiesVO.setGraphDomainCode(_channelUserVO.getGeographicalCode());
            LogFactory.printLog(methodName, "_channelUserVO.getGeographicalCode() >> " + _channelUserVO.getGeographicalCode(), _log);
            geoList.add(userGeographiesVO);
            final int addUserGeo = new UserGeographiesDAO().addUserGeographyList(con, geoList);
            if (addUserGeo <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserAddController[process]", "", "", "",
                    "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
            }

            // insert domains info
            if (TypesI.YES.equals(_categoryVO.getDomainAllowed()) && PretupsI.DOMAINS_ASSIGNED.equals(_categoryVO.getFixedDomains())) {
                final DomainDAO domainDAO = new DomainDAO();
                final ArrayList domainList = domainDAO.loadDomainDetails(con);
                if (domainList != null && !domainList.isEmpty()) {
                    final String[] domainListArray = new String[domainList.size()];
                    final Iterator domainIter = domainList.iterator();
                    int i = 0;
                    while (domainIter.hasNext()) {
                        final DomainVO domainVO = (DomainVO) domainIter.next();
                        domainListArray[i] = domainVO.getDomainCode();
                        i++;
                    }
                    final int domainCount = domainDAO.addUserDomainList(con, _channelUserVO.getUserID(), domainListArray);
                    if (domainCount <= 0) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_DEFAULT_DOMAIN_NOT_FOUND);
                    }
                    _channelUserVO.setDomainCodes(domainListArray);
                }
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("After Insert in Domain " );
                	loggerValue.append(domainList.size());
                    _log.debug("OPTUserAddController process", loggerValue );
                }
            }

            // Insert Product Info
            if (TypesI.YES.equals(_categoryVO.getProductTypeAssociationAllowed())) {
                final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
                final ProductTypeDAO productTypeDAO = new ProductTypeDAO();

                final ArrayList productList = networkProductDAO.loadProductList(con, _channelUserVO.getNetworkID());

                if (productList != null && !productList.isEmpty()) {
                    final String[] productListArray = new String[productList.size()];
                    final Iterator productIter = productList.iterator();
                    int i = 0;
                    while (productIter.hasNext()) {
                        final ProductVO transferProfileProductVO = (ProductVO) productIter.next();
                        productListArray[i] = transferProfileProductVO.getProductType();
                        i++;
                    }

                    final int productCount = productTypeDAO.addUserProductsList(con, _channelUserVO.getUserID(), productListArray);
                    if (productCount <= 0) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_DEFAULT_PRODUCT_NOT_FOUND);

                    }

                }
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("After Insert in Product ");
                	loggerValue.append(productList.size());
                    _log.debug("OPTUserAddController process",  loggerValue );
                }
            }

            if (PretupsI.USER_STATUS_ACTIVE.equals(_channelUserVO.getStatus())) {
                _channelUserVO.setActivatedOn(currentDate);
            } else {
                _channelUserVO.setActivatedOn(null);
            }

            _channelUserVO.setLowBalAlertAllow("N");
            _channelUserVO.setInSuspend("N");
            _channelUserVO.setOutSuspened("N");

            String groupRole = (String) p_requestVO.getRequestMap().get("GROUPROLE");
            // Assign Roles
            if (PretupsI.YES.equalsIgnoreCase(_categoryVO.getWebInterfaceAllowed())) {
                final UserRolesDAO userRolesDAO = new UserRolesDAO();
                final UserRolesTxnDAO userRolesTxnDAO = new UserRolesTxnDAO();
                String[] roles = null;

                if (!BTSLUtil.isNullString(Constants.getProperty("EXT_USER_REG_GROUP_ROLE_REQ"))) {
                    if(!BTSLUtil.isNullString(groupRole)){
                        Map rolesMap = userRolesDAO.loadRolesListByGroupRole(con, _channelUserVO.getCategoryCode(), "Y");
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
                                    userRolesDAO.addUserRolesList(con, _channelUserVO.getUserID(), roles);
                                    break;
                                }else{
                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                          		}
                          	}
                		}
                	}else{
                        final Map defaultRoleCodes = userRolesTxnDAO.loadDefaultRolesListByGroupRole(con, userCategeryCode, Constants.getProperty("EXT_GROUP_ROLE"));
                        if (defaultRoleCodes.size() > 0 || !defaultRoleCodes.isEmpty()) {
                            final Iterator iterator = defaultRoleCodes.keySet().iterator();

                            UserRolesVO rolesVO = null;
                            String groupRoles = null;
                            final List<String> roleCodeList = new ArrayList<String>();
                            while (iterator.hasNext()) {
                                groupRoles = (String) iterator.next();
                                final List list = (List) defaultRoleCodes.get(groupRoles);
                                if (list != null) {
                                    for (int i = 0, j = list.size(); i < j; i++) {
                                        rolesVO = (UserRolesVO) list.get(i);
                                        roleCodeList.add(rolesVO.getRoleCode());
                					}
                				}
                			}
                            if (roleCodeList != null && !roleCodeList.isEmpty()) {
                                roles = new String[roleCodeList.size()];
                                for (int i = 0, j = roleCodeList.size(); i < j; i++) {
                                    roles[i] = roleCodeList.get(i);
                				}

                                final int insertCount = userRolesDAO.addUserRolesList(con, _channelUserVO.getUserID(), roles);
                                if (insertCount <= 0) {
                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_ROLES_NOT_FOUND);
                				}
                			} else {
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_ROLES_NOT_FOUND);
                			}
                			// Ended Here
                		}
                	}
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
                            servicesTypeDAO.addUserServicesList(con, _channelUserVO.getUserID(), givenService, PretupsI.YES);
            			}else{
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_INVALID);
            			}
            		}else{
                        final String[] service = new String[serviceList.size()];
                        _log.debug("process", "size of serviceList = " + serviceList.size());
                        for (int i = 0; i < serviceList.size(); i++) {
                            listValueVO = (ListValueVO) serviceList.get(i);
                            service[i] = listValueVO.getValue();

            			}
                        if(!serviceList.isEmpty()){
                            final int servicesCount = servicesTypeDAO.addUserServicesList(con, _channelUserVO.getUserID(), service, PretupsI.YES);
                            if (servicesCount <= 0) {
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
            				}
            			}
            		}
                
            	} catch (Exception e) {
                    _log.errorTrace("process", e);
                    throw new BTSLBaseException(this, methodName, "Exception in process");
            		}
            	}
            con.commit();

            OperatorUserLog.apiLog("ADDOPTUSR", _channelUserVO, _senderVO, p_requestVO);
            // Push Messages
            loggerValue.setLength(0);
        	loggerValue.append( " _channelUserVO.getStatus() = ");
        	loggerValue.append(_channelUserVO.getStatus());
            _log.debug("OPTUserAddController process Connection Commited : ",loggerValue );
            BTSLMessages btslPushMessage = null;

            final String arr[] = { _channelUserVO.getUserName(), "" };
            String[] arrArray = null;

            if (PretupsI.USER_STATUS_ACTIVE.equals(_channelUserVO.getStatus())) {
                if (locale == null) {
                    locale = p_requestVO.getLocale();
                }
                // send message for login id
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_WEB_ACTIVATE);
                arrArray = new String[] { _channelUserVO.getLoginID(), "", BTSLUtil.decryptText(_channelUserVO.getPassword()) };
                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, _channelUserVO.getNetworkID());
                pushMessage.push();
                // Email for pin & password- code for email details
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(_channelUserVO.getEmail())) {
                    final String subject = BTSLUtil.getMessage(locale, "user.addchanneluser.addsuccessmessage", arr);
                    final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, _channelUserVO.getNetworkID(),
                        "Email will be delivered shortly", _channelUserVO, _channelUserVO);
                    emailSendToUser.sendMail();
                }

                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("After Sent Mail or message ");
                	loggerValue.append(_channelUserVO);
                    _log.debug("OPTUserAddController process",  loggerValue);
                }
            } else {
                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
            }
            p_requestVO.setMessageArguments(arrArray);
            // Ended Here
        }

        catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException " );
        	loggerValue.append( be.getMessage());
            _log.error("process", loggerValue);
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
            _log.error("process",  loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OPTUserAddController[process]", "", "", "",
            		loggerValue.toString() );
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            requestMap.put("CHNUSERVO", _channelUserVO);
            p_requestVO.setRequestMap(requestMap);
            _channelUserDao = null;
            _userDAO = null;
            _channelUserVO = null;
            _categoryVO = null;

			if (mcomCon != null) {
				mcomCon.close("OPTUserAddController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * @description : This method to generate the userId while inserting new
     *              record
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return String
     */
    private String generateUserId(String p_networkCode, String p_prefix) throws Exception {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_networkCode=" );
        	loggerValue.append(p_networkCode);
        	loggerValue.append( ", p_prefix=" );
        	loggerValue.append(p_prefix);
            _log.debug("generateUserId", loggerValue);
        }

        final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, p_networkCode)) + "", length);
        id = p_networkCode + p_prefix + id;

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting id=");
        	loggerValue.append(id);
            _log.debug("generateUserId",  loggerValue );
        }

        return id;
    }

}
