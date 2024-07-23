package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryReqGtwTypeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
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
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;



/**
 * @description : This controller class will be used to process the add request
 *              for user through external system via operator receiver.
 * @author : diwakar
 */

public class UserAddController implements ServiceKeywordControllerI {
    private final Log _log = LogFactory.getLog(UserAddController.class.getName());
    private ChannelUserVO _channelUserVO = null;
    private UserDAO _userDAO = null;
    private CategoryVO _categoryVO = null;
    private ChannelUserDAO _channelUserDao = null;
    private ChannelUserWebDAO _channelUserWebDao = null;
    private ExtUserDAO _extUserDao = null;
    private ChannelUserVO parentChannelUserVO = null;
    private ChannelUserVO _senderVO = null;

    /**
     * Method Process
     * Process Method , Processes external channel user registration request
     * 
     * @param p_requestVO
     */
    	@Override
		public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("UserAddController process", "Entered p_requestVO=" + p_requestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        final HashMap requestMap = p_requestVO.getRequestMap();
        _channelUserDao = new ChannelUserDAO();
        _channelUserWebDao = new ChannelUserWebDAO();
        ChannelUserTxnDAO channelUsertxnDAO = null;
        _channelUserVO = new ChannelUserVO();
        _userDAO = new UserDAO();
        _extUserDao = new ExtUserDAO();
        OperatorUtilI operatorUtili = null;
        final String[] msg = new String[1];
        Locale locale = null;
        try {
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
        } catch (RuntimeException e) {
            _log.errorTrace(METHOD_NAME, e);
            locale = new Locale("en", "US");
        }
        String senderPin = "";
        String webPassword = null;
        String randomPwd = null;
        String defaultGeoCode = "";
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserAddController[process]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
            channelUsertxnDAO = new ChannelUserTxnDAO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final String userCategeryCode = ((String) requestMap.get("USERCATCODE")).trim();
            _senderVO = (ChannelUserVO) p_requestVO.getSenderVO();

            // Load category
            final ArrayList catList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con, userCategeryCode);

            // if null category does not exist
            if (catList == null || catList.isEmpty()) {
                throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
            }

            // If channel user is not owner of it's category, then it's parent
            // details should not be blank.
            _categoryVO = (CategoryVO) catList.get(0);
            if (_categoryVO.getSequenceNumber() > 1) {
                final String parentMsisdn = (String) requestMap.get("PARENTMSISDN");
                final String parentExtCode = (String) requestMap.get("PARENTEXTERNALCODE");
                if ((BTSLUtil.isNullString(parentMsisdn))&&BTSLUtil.isNullString(parentExtCode)) {
                    throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_PARENTMSISDN_BLANK);
                }
            }

            _channelUserVO.setCategoryVO(_categoryVO);
            _channelUserVO.setCategoryCode(_categoryVO.getCategoryCode());
            _channelUserVO.setCategoryName(_categoryVO.getCategoryName());
            _channelUserVO.setDomainTypeCode(_categoryVO.getDomainTypeCode());
            // User name set
            _channelUserVO.setUserName(requestMap.get("USERNAME").toString().trim());// User
            String []name=_channelUserVO.getUserName().split(" ", 2);
            _channelUserVO.setFirstName(name[0]);
            if(name.length>1){
            	_channelUserVO.setLastName(name[1]);
            } 
            // Name
            // Mandatory
            // Value
            // short name set

            _channelUserVO.setShortName(BTSLUtil.NullToString((String) requestMap.get("SHORTNAME")));
            p_requestVO.setUserCategory(_categoryVO.getCategoryCode());
            String geograpgyCode=BTSLUtil.NullToString((String) requestMap.get("GEOGRAPHYCODE"));
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()){
            	 // Web Login of User
            	if(BTSLUtil.isNullString(geograpgyCode)) {
	                ExtUserDAO extUserDao = new ExtUserDAO();
	                String parentMSISDN = (String) p_requestVO.getRequestMap().get("PARENTMSISDN");
	                String childCatCode = (String) p_requestVO.getRequestMap().get("USERCATCODE");
	                String parentExtCode = (String) p_requestVO.getRequestMap().get("PARENTEXTERNALCODE");
	                ChannelUserVO channelUserVOTemp = new ChannelUserVO();
	                UserDAO userDao = new UserDAO();
	                String extNwCode = (String) p_requestVO.getRequestMap().get("EXTNWCODE");
	                String senderMSISDN = (String) p_requestVO.getRequestMap().get("MSISDN");
	                String networkID = p_requestVO.getRequestNetworkCode();
	                String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
	                String loginID = (String) p_requestVO.getRequestMap().get("LOGINID");
	                String empCode = (String) p_requestVO.getRequestMap().get("EMPCODE");
	                String pin = (String) p_requestVO.getRequestMap().get("PIN");
	                if (BTSLUtil.isNullString(parentMSISDN) && BTSLUtil.isNullString(parentExtCode)) {
	                	channelUserVOTemp = extUserDao.loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode(con,
	                            senderMSISDN, pin, loginID, password, empCode, extNwCode, "", "", locale);
	                } else {
	                    if (!BTSLUtil.isNullString(parentMSISDN)) {
	                    	channelUserVOTemp = userDao.loadUserDetailsByMsisdn(con, parentMSISDN);
	                    } else {
	                        try{
	                        	channelUserVOTemp = userDao.loadAllUserDetailsByExternalCode(con, parentExtCode);
	                        }catch(SQLException sql){
	                            throw new BTSLBaseException("UserAddController process", "process",PretupsErrorCodesI.SQL_ERROR_EXCEPTION);
	                        }
	                    }
	                }
	                if (channelUserVOTemp == null) {
	                    throw new BTSLBaseException("UserAddController process", "process",PretupsErrorCodesI.OPT_ERROR_NO_SUCH_USER);
	                }
	                //Set default geographical code
	                if(BTSLUtil.isNullString(parentMSISDN)) {
		            	defaultGeoCode = _extUserDao.getDefaultGeoCodeDetails(con, p_requestVO);
	                } else {
	                	defaultGeoCode = _extUserDao.getDefaultGeoCodeDetailsForParent(con, p_requestVO,channelUserVOTemp);
	                }
	                
	                GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
	                _channelUserVO.setGeographicalAreaList(geographyDAO.loadUserGeographyList(con,channelUserVOTemp.getUserID(), channelUserVOTemp.getNetworkID()));
	                String newUserGeoCode = defaultGeoCode;
	                if (BTSLUtil.isNullString(newUserGeoCode)) {
	                    throw new BTSLBaseException("UserAddController process", "process", PretupsErrorCodesI.DEFAULT_GEO_NOT_FOUND);
	                }
	                p_requestVO.setNetworkCode(channelUserVOTemp.getNetworkID());
	                List<UserGeographiesVO> childGeographyVOlist = geographyDAO.loadParentGeographyInfo(con,newUserGeoCode, p_requestVO);
	                if (childGeographyVOlist.isEmpty()) {
	                    throw new BTSLBaseException("UserAddController process", "process",PretupsErrorCodesI.EXTSYS_REQ_USR_GEOGRAPHY_NOT_BELONG_TO_PARENT);
	                }
	                int count = _channelUserVO.getGeographicalAreaList().size();
	                boolean graphbelongstoparent = false;

	                for (int i = 0; i < count; i++) {
	                    UserGeographiesVO userGeogVO =  _channelUserVO.getGeographicalAreaList().get(i);
	                    if (userGeogVO.getGraphDomainCode().equals(childGeographyVOlist.get(0).getParentGraphDomainCode())
	                            || userGeogVO.getGraphDomainCode().equals(childGeographyVOlist.get(0).getGraphDomainCode())) {
	                        _channelUserVO.setgeographicalCodeforNewuser(childGeographyVOlist.get(0).getGraphDomainCode());
	                        graphbelongstoparent = true;
	                    }

	                }
	                if (!graphbelongstoparent) {
	                    throw new BTSLBaseException("UserAddController process", "process",PretupsErrorCodesI.EXTSYS_REQ_USR_GEOGRAPHY_NOT_BELONG_TO_PARENT);
	                }
	                if (!BTSLUtil.isNullString(_channelUserVO.getgeographicalCodeforNewuser())) {
	                    _channelUserVO.setGeographicalCode( _channelUserVO.getgeographicalCodeforNewuser());
	                    defaultGeoCode = _channelUserVO.getgeographicalCodeforNewuser();		                
	                } else {
	                	throw new BTSLBaseException(this,"process",PretupsErrorCodesI.DEFAULT_GEO_NOT_FOUND);
	        		}
            	} else {
            		_channelUserVO.setGeographicalCode(_senderVO.getgeographicalCodeforNewuser());
            	}
            }
            else{
            	_channelUserVO.setGeographicalCode(_senderVO.getgeographicalCodeforNewuser());
                     
            }

            if (PretupsI.YES.equalsIgnoreCase(_categoryVO.getWebInterfaceAllowed())) {
                // Set MSISDN as login id if Login Id is blank in request
                if (BTSLUtil.isNullString((String) requestMap.get("WEBLOGINID"))) {
                    _channelUserVO.setLoginID((String) requestMap.get("PRIMARYMSISDN"));
                } else {
                    if (_userDAO.isUserLoginExist(con, (String) requestMap.get("WEBLOGINID"), null)) {
                        final String[] argsArray = { requestMap.get("WEBLOGINID").toString() };
                        throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
                    }

                    _channelUserVO.setLoginID((String) requestMap.get("WEBLOGINID"));
                }

                webPassword = (String) p_requestVO.getRequestMap().get("WEBPASSWORD");
                if (!BTSLUtil.isNullString(webPassword)&&operatorUtili!=null) {
                    final HashMap errorMessageMap = operatorUtili.validatePassword((String) requestMap.get("WEBLOGINID"), webPassword);
                    if (null != errorMessageMap && errorMessageMap.size() > 0) {
                        throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID);
                    }
                }
                // Assign Primary MSISDN as web password for null password value
                // in request.
                if (BTSLUtil.isNullString(webPassword)&&operatorUtili!=null) {
                    randomPwd = operatorUtili.generateRandomPassword();
                    _channelUserVO.setPassword(BTSLUtil.encryptText(randomPwd));
                } else {
                    _channelUserVO.setPassword(BTSLUtil.encryptText(webPassword));
                }
            }
            // 11-MAR-2014
            else {
                // 01-APR-2104 if category do not have web access and
                // XMLGW/EXTGW is allowed then WEBLOGINID is required else
                // create user without loginid
                final ArrayList<String> gwAccessTypeList = new CategoryReqGtwTypeDAO().loadCategoryRequestGwType(con, userCategeryCode);
                final String webLoginID = (String) requestMap.get("WEBLOGINID");
                if (gwAccessTypeList != null && !gwAccessTypeList.isEmpty()) {
                    if (gwAccessTypeList.contains(PretupsI.REQUEST_SOURCE_TYPE_EXTGW) || gwAccessTypeList.contains(PretupsI.REQUEST_SOURCE_TYPE_XMLGW) || gwAccessTypeList
                        .contains(PretupsI.REQUEST_SOURCE_TYPE_WEB)) {
                        if (BTSLUtil.isNullString(webLoginID)) {
                            throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_BLANK);
                        } else {
                            if (_userDAO.isUserLoginExist(con, webLoginID, null)) {
                                final String[] argsArray = { webLoginID };
                                throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
                            }

                        }

                    }

                } else if (!BTSLUtil.isNullString(webLoginID)&&_userDAO.isUserLoginExist(con, webLoginID, null)) {

                    
                        final String[] argsArray = { webLoginID };
                        throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
                    
                }
                _channelUserVO.setLoginID(webLoginID);
                webPassword = (String) p_requestVO.getRequestMap().get("WEBPASSWORD");
                if (!BTSLUtil.isNullString(webPassword)) {
                    final HashMap errorMessageMap = operatorUtili.validatePassword(webLoginID, webPassword);
                    if (null != errorMessageMap && errorMessageMap.size() > 0) {
                        throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID);
                    }
                }
                // Assign Primary MSISDN as web password for null password value
                // in request.
                if (BTSLUtil.isNullString(webPassword)) {
                    randomPwd = operatorUtili.generateRandomPassword();
                    _channelUserVO.setPassword(BTSLUtil.encryptText(randomPwd));
                } else {
                    _channelUserVO.setPassword(BTSLUtil.encryptText(webPassword));
                }

            }
            // Ended Here
            // User name prefix.
            final String userPrifix = ((String) requestMap.get("USERNAMEPREFIX")).toUpperCase();
            List<ListValueVO> userPrefix = new ArrayList<>();
            boolean prefix = false;
                userPrefix= LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true);
                for(ListValueVO value : userPrefix){
                    if(value.getLabel().equalsIgnoreCase(userPrifix)){
                _channelUserVO.setUserNamePrefix(userPrifix);
                prefix=true;
                break;
                }
                }
                if(!prefix){
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INVALID_USERNAMEPRFX);
                }


            // External code is not unique for HCPT INDONESIA implementation.

            final String extCode = (String) p_requestVO.getRequestMap().get("EXTERNALCODE");
            if (!BTSLUtil.isNullString(extCode)) {
                final boolean isExtCodeExist = _channelUserWebDao.isExternalCodeExist(con, extCode, null);
                if (isExtCodeExist) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTERNAL_CODE_ALREARY_EXIST);
                }
            }
            _channelUserVO.setExternalCode(extCode);

            // Set actual network of the user for
            // DiwakarString

            final String actualNetworkCode = p_requestVO.getExternalNetworkCode();
            if (!BTSLUtil.isNullString(actualNetworkCode)) {
                final String status = "'" + PretupsI.STATUS_DELETE + "','" + PretupsI.STATUS_CANCELED + "'";
                final ArrayList networkList = new NetworkDAO().loadNetworkList(con, status);
                NetworkVO networkVO = null;
                boolean actualNetworkExist = false;
                actualNetworkExist = checknetworkList(actualNetworkCode,
						networkList, actualNetworkExist);
                if (actualNetworkExist) {
                    _channelUserVO.setNetworkCode(actualNetworkCode.trim());
                } else {
                    throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_ACTUAL_NW_CODE_INVALID);
                }
            }

            // Email ID check
            final String emailID = BTSLUtil.NullToString((String) p_requestVO.getRequestMap().get("EMAILID"));
            _channelUserVO.setEmail(emailID);

            // Generate user id for new channel user
            _channelUserVO.setNetworkID(p_requestVO.getExternalNetworkCode());
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
                    throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_INVALID_MSISDN);
                }

                final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";

                parentChannelUserVO = _extUserDao.loadUsersDetailsforExtReq(con, filteredParentMsisdn, null, PretupsI.STATUS_NOTIN, status);

                // commented to load the parent channel user details on the
                // basis of parent external code
                // parentChannelUserVO=_channelUserDao.loadERPChnlUserDetailsByExtCode(con,parentExtCode);
                ChannelUserVO parentChannelUserVO1 = null;
                parentChannelUserVO1 = _extUserDao.loadUsersDetailsforExtCodeReq(con, parentExtCode, null, PretupsI.STATUS_NOTIN, status);

                if (parentChannelUserVO != null && parentChannelUserVO1 != null) {
                    if (!parentChannelUserVO.getUserID().equals(parentChannelUserVO1.getUserID())) {
                        // Throw exception that either MSISDN is wrong or
                        // external code of parent
                        // external code and MSISDN not match for same parent.
                        throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_PARENT_IS_INVALID);
                    } else if (!PretupsI.STATUS_ACTIVE.equals(parentChannelUserVO.getStatus())) {
                        // throw exp parent not active
                        throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    } else {
                        _channelUserVO.setParentID(parentChannelUserVO.getUserID());
                        _channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                    }
                }
                // Handling of user if parentExtCode is null
                else if (parentChannelUserVO != null) {
                    if (!PretupsI.STATUS_ACTIVE.equals(parentChannelUserVO.getStatus())) {
                        // throw exp parent not active
                        throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    } else {
                        _channelUserVO.setParentID(parentChannelUserVO.getUserID());
                        _channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                    }
                } else {
                    // Throw exception if parent does not exist.
                    throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_EXIST);
                }
            } else if (!BTSLUtil.isNullString(parentExtCode)) {
            	final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";
                parentChannelUserVO = _extUserDao.loadUsersDetailsforExtCodeReq(con, parentExtCode, null, PretupsI.STATUS_NOTIN, status);
                if (parentChannelUserVO != null) {
                    if (!PretupsI.STATUS_ACTIVE.equals(parentChannelUserVO.getStatus())) {
                        // Throw exception if parent not active
                        throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    }
                    _channelUserVO.setParentID(parentChannelUserVO.getUserID());
                    _channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                } else {
                    // Throw exception if parent is not exist
                    throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_EXIST);
                }
            } else if (!BTSLUtil.isNullString(parentMsisdn)) {
                parentMsisdn = parentMsisdn.trim();
                final String filteredParentMsisdn = PretupsBL.getFilteredMSISDN(parentMsisdn);
                if (!BTSLUtil.isValidMSISDN(filteredParentMsisdn)) {
                    throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_INVALID_MSISDN);
                }
                final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";
                parentChannelUserVO = _channelUserDao.loadUsersDetails(con, filteredParentMsisdn, null, PretupsI.STATUS_NOTIN, status);
                if (parentChannelUserVO != null) {
                	if(!PretupsI.USER_STATUS_PREACTIVE.equals(parentChannelUserVO.getStatus())&&!PretupsI.STATUS_ACTIVE.equals(parentChannelUserVO.getStatus())){
                   
                        // throw exp parent not active
                        throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    
                	}
                    _channelUserVO.setParentID(parentChannelUserVO.getUserID());
                    _channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                } else {
                    // throw parent is not exist
                    throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_EXIST);
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
            // code for geography is moved here after parent details fetched as parent geocode is required
			String geocode=(String) requestMap.get("GEOGRAPHYCODE");
			if (_log.isDebugEnabled())
			{
				_log.debug("UserAddController process","geocode value = "+geocode);
			}

			if (BTSLUtil.isNullString(geocode))
			{
				defaultGeoCode=_extUserDao.getDefaultGeoCodeDtlBasedOnNetwork(con, p_requestVO,actualNetworkCode);
				if (_log.isDebugEnabled())
				{
					_log.debug("UserAddController process","default GeoCode = "+defaultGeoCode);
				}
			}
			if (!BTSLUtil.isNullString(geocode))
			{	
				// logic to validate the passed geocode
				GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
				boolean isValid_GeoCode=false;
				// check for other level (SE and Retailer)
				if (_categoryVO.getSequenceNumber()>1)
				{
					if (_log.isDebugEnabled())
					{
						_log.debug("UserAddController process","low level hirearchy = "+geocode);
					}
					
					String parentGeo_Code=parentChannelUserVO.getGeographicalCode();
					
					ArrayList<String> _geoDomainListUnderParent= _geographyDAO.loadGeographyHierarchyUnderParent(con,parentGeo_Code,parentChannelUserVO.getNetworkID(),_categoryVO.getGrphDomainType());
					
					if(_geoDomainListUnderParent.contains(geocode))
					{
						if (_log.isDebugEnabled())
						{
							_log.debug("UserAddController process","low level hirearchy 1= "+geocode);
						}
						
						
						isValid_GeoCode=true;
						defaultGeoCode=geocode;
					}
				}
				else
				{// check for Top level user (Distributer level)
					
					if (_log.isDebugEnabled())
					{
						_log.debug("UserAddController process","top level hirearchy = "+geocode);
					}
					
					if(!_geographyDAO.isGeographicalDomainExist(con, geocode, true))
						throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 

					defaultGeoCode=geocode;
					isValid_GeoCode=true;
				}

				if(!isValid_GeoCode)
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
					throw new BTSLBaseException("UserAddController", "loadGeographyUnderParent", PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
				}
				if (_log.isDebugEnabled())
				{
				_log.debug("UserAddController process","Passed GeoCode = "+defaultGeoCode);
				}
			}

			//11-MAR-2014
			if(!BTSLUtil.isNullString(defaultGeoCode))
				_channelUserVO.setGeographicalCode(defaultGeoCode);
            // User hierarchy check
            final ArrayList trfRule = new C2STransferDAO().loadC2SRulesListForChannelUserAssociation(con, _channelUserVO.getNetworkID());

            {
                final String networkCode = (String) p_requestVO.getRequestMap().get("EXTNWCODE");

                _channelUserVO.setUserID(generateUserId(_channelUserVO.getNetworkID(), _categoryVO.getUserIdPrefix()));
                if (isownerIDNew) {
                    _channelUserVO.setOwnerID(_channelUserVO.getUserID());
                }
                _log.debug("UserAddController", "process : _channelUserVO.getUserID = " + _channelUserVO.getUserID());
 

                // Set the approval level
                _log.debug("UserAddController", "process : ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue() = " + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue());
                if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue() > 0) {
                    _channelUserVO.setStatus(PretupsI.USER_STATUS_NEW);// N
                    // Active
                }	else {
                        if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
                        	_channelUserVO.setStatus(PretupsI.USER_STATUS_PREACTIVE);// PA
                            // Active
                        } else {
                        	_channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// A
                            // Active
                        }
                    }
                 _channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// N
                // New

                final Date currentDate = new Date();
                // prepare UserPhone VO list
                final ArrayList userPhoneList = prepareUserPhoneVOList(con, requestMap, _channelUserVO, currentDate, senderPin);
                // set some use full parameter
                _channelUserVO.setPasswordModifiedOn(currentDate);
                _channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);
                _channelUserVO.setCreationType(PretupsI.EXTERNAL_SYSTEM_USR_CREATION_TYPE);
                _channelUserVO.setExternalCode(BTSLUtil.NullToString((String) requestMap.get("EXTERNALCODE")));// External
                // Code
                _channelUserVO.setEmpCode((String) requestMap.get("EMPCODE"));
                _channelUserVO.setUserCode((String) requestMap.get("PRIMARYMSISDN"));
                _channelUserVO.setContactNo(BTSLUtil.NullToString((String) requestMap.get("CONTACTNUMBER")));
                _channelUserVO.setContactPerson(BTSLUtil.NullToString((String) requestMap.get("CONTACTPERSON")));
                _channelUserVO.setSsn(BTSLUtil.NullToString((String) requestMap.get("SSN")));
                _channelUserVO.setAddress1(BTSLUtil.NullToString((String) requestMap.get("ADDRESS1")));
                _channelUserVO.setAddress2(BTSLUtil.NullToString((String) requestMap.get("ADDRESS2")));
                _channelUserVO.setCity(BTSLUtil.NullToString((String) requestMap.get("CITY")));
                _channelUserVO.setState(BTSLUtil.NullToString((String) requestMap.get("STATE")));
                _channelUserVO.setCountry(BTSLUtil.NullToString((String) requestMap.get("COUNTRY")));
                _channelUserVO.setCreatedBy(_senderVO.getUserID());

                final Date curDate = new Date();
                _channelUserVO.setCreatedOn(curDate);
                _channelUserVO.setMsisdn(BTSLUtil.NullToString((String) requestMap.get("PRIMARYMSISDN")));
                _channelUserVO.setModifiedBy(_senderVO.getUserID());
                _channelUserVO.setModifiedOn(currentDate);
                _channelUserVO.setUserProfileID(_channelUserVO.getUserID());
                _channelUserVO.setPasswordModifiedOn(currentDate);
                _channelUserVO.setPasswordCountUpdatedOn(currentDate);

                // insert in to user table
                final int userCount = _userDAO.addUser(con, _channelUserVO);
                if (userCount <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserAddController[process]", "", "", "",
                        "Exception:Update count <=0 ");
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
                // Phone VO entries
                UserPhoneVO phoneVO = new UserPhoneVO();
                final ArrayList phoneList = new ArrayList();
                //
                _channelUserVO.setMsisdn(BTSLUtil.NullToString((String) requestMap.get("PRIMARYMSISDN")));
                phoneVO.setMsisdn(_channelUserVO.getMsisdn());
                phoneVO.setPrimaryNumber(PretupsI.YES);

                // get PIN of send if not comming then generate random pin as
                // discussed with Divyakant Sir
                senderPin = (String) requestMap.get("PIN");
                if (BTSLUtil.isNullString(senderPin)&&operatorUtili!=null) {
                    senderPin = operatorUtili.generateRandomPin();
                }
                phoneVO.setSmsPin(BTSLUtil.encryptText(senderPin));
                phoneVO.setPinRequired(PretupsI.YES);
                phoneVO.setPhoneProfile(_categoryVO.getCategoryCode());
                phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                phoneVO.setUserId(_channelUserVO.getUserID());
                // set the default values
                phoneVO.setCreatedBy(_channelUserVO.getUserID());
                phoneVO.setModifiedBy(_channelUserVO.getUserID());
                phoneVO.setCreatedOn(currentDate);
                phoneVO.setModifiedOn(currentDate);
                phoneVO.setPinModifiedOn(currentDate);
                phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
                phoneVO.setPrefixID(prefixVO.getPrefixID());
                if(phoneVO.getPinReset()==null) 
                { 
                        phoneVO.setPinReset(PretupsI.YES); 
                }
                phoneList.add(phoneVO);
                final int phoneCount = _userDAO.addUserPhoneList(con, phoneList);
                if (phoneCount <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                        "ChannelUserRegZainNigeriaRequestHandler[process]", "", "", "", "Exception:Update count <=0 for user phones");
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
                // end
                /*
                 * If user status is Y(active) means user is activated at the
                 * creation time, so we are setting the
                 * activated_on = currentDate. This indicate user is activated
                 * on the same date
                 */
                if (PretupsI.USER_STATUS_ACTIVE.equals(_channelUserVO.getStatus())) {
                    _channelUserVO.setActivatedOn(currentDate);
                } else {
                    _channelUserVO.setActivatedOn(null);
                }
                _channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                _channelUserVO.setMpayProfileID("");

                _channelUserVO.setInSuspend("N");
                _channelUserVO.setOutSuspened("N");
                // Setting Default grades
                String defaultUserGradeCode = null;
                defaultUserGradeCode = usingDefaultUserGradeCode(METHOD_NAME,
						con, userCategeryCode, defaultUserGradeCode);

                // Setting Default Commission Profile Set ID
                String defaultCommissionProfileSetID = null;
                defaultCommissionPrfSetId(METHOD_NAME, con, userCategeryCode,
						networkCode, defaultUserGradeCode,
						defaultCommissionProfileSetID);

                // Setting Default Transfer Profile ID
                String defaultTransferProfileID = null;
                try {
                    defaultTransferProfileID = _extUserDao.getDefaultTransferProfileIDByCategoryID(con, networkCode, userCategeryCode, PretupsI.PARENT_PROFILE_ID_USER);
                    defaultTransferProfileID = defaultTransferProfileID.trim();
                    _channelUserVO.setTransferProfileID(defaultTransferProfileID);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    if (BTSLUtil.isNullString(defaultTransferProfileID)) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_DEFAULT_TRANSFER_PROFILE_NOT_FOUND);
                    }
                }

                // Assign Services
                final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
                ListValueVO listValueVO = null;
                ArrayList serviceList = null;
                try {
                    serviceList = servicesTypeDAO.loadServicesList(con, networkCode, PretupsI.C2S_MODULE, userCategeryCode, false);
                    final String[] service = new String[serviceList.size()];
                    System.out.println("size of serviceList = " + serviceList.size());
                    int serviceLists=serviceList.size();
                    for (int i = 0; i < serviceLists; i++) {
                        listValueVO = (ListValueVO) serviceList.get(i);
                        service[i] = listValueVO.getValue();

                    }
                    if(serviceList != null && !serviceList.isEmpty())   
                    {
                    	final int servicesCount = servicesTypeDAO.addUserServicesList(con, _channelUserVO.getUserID(), service, PretupsI.YES);
                    	if (servicesCount <= 0) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
                        }
                    }
                    
                    
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
                }
                // Assign Roles
                if (PretupsI.YES.equalsIgnoreCase(_categoryVO.getWebInterfaceAllowed())) {
                    final UserRolesDAO userRolesDAO = new UserRolesDAO();
                    String[] roles = null;

                    if (!BTSLUtil.isNullString(Constants.getProperty("EXT_USER_REG_GROUP_ROLE_REQ"))) {
                        final HashMap defaultRoleCodes = userRolesDAO.loadRolesListByGroupRole(con, userCategeryCode, Constants.getProperty("EXT_GROUP_ROLE"));
                        if (defaultRoleCodes.size() > 0 || !defaultRoleCodes.isEmpty()) {
                            final Iterator iterator = defaultRoleCodes.keySet().iterator();

                            UserRolesVO rolesVO = null;
                            String groupRoles = null;
                            final ArrayList<String> roleCodeList = new ArrayList<String>();
                            while (iterator.hasNext()) {
                                groupRoles = (String) iterator.next();
                                final ArrayList list = (ArrayList) defaultRoleCodes.get(groupRoles);
                                if (list != null) {
                                    for (int i = 0, j = list.size(); i < j; i++) {
                                        rolesVO = (UserRolesVO) list.get(i);
                                        if(rolesVO.getDefaultType().equals(PretupsI.YES)){
                                        roleCodeList.add(rolesVO.getRoleCode());
                                        }
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
                            }
                            // 11-MAR-2014
                            else {
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_ROLES_NOT_FOUND);
                            }
                            // Ended Here
                        }
                    }
                }

                // Low Balance Alert Allow
                _channelUserVO.setLowBalAlertAllow(PretupsI.YES);

                // Insert data into channel users table
                final int userChannelCount = _channelUserDao.addChannelUser(con, _channelUserVO);
                if (userChannelCount <= 0) {
                	mcomCon.finalRollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserAddController[process]", "", "", "",
                        "Exception:Update count <=0 ");
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                } else {
                    // USER_ID ,GEOLI
                    final ArrayList geoList = new ArrayList();
                    final UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
                    userGeographiesVO.setUserId(_channelUserVO.getUserID());
                    userGeographiesVO.setGraphDomainCode(_channelUserVO.getGeographicalCode());
                    System.out.println("_channelUserVO.getGeographicalCode() >> " + _channelUserVO.getGeographicalCode());
                    geoList.add(userGeographiesVO);
                    final int addUserGeo = new UserGeographiesDAO().addUserGeographyList(con, geoList);
                    if (addUserGeo <= 0) {
                    	mcomCon.finalRollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserAddController[process]", "", "", "",
                            "Exception:Update count <=0 ");
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                    }
                }
                mcomCon.finalCommit();
                requestMap.put("CHNUSERVO", _channelUserVO);
                p_requestVO.setRequestMap(requestMap);
                // Push Messages
                phoneVO = null;
                for (int i = 0, j = userPhoneList.size(); i < j; i++) {
                    phoneVO = (UserPhoneVO) userPhoneList.get(i);
                    if (TypesI.YES.equals(phoneVO.getPrimaryNumber())) {
                        break;
                    }
                }
                if (!BTSLUtil.isNullString(phoneVO.getMsisdn())) {
                    final String filterMsisdn = PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn());
                    phoneVO.setMsisdn(filterMsisdn);
                    if (TypesI.YES.equals(phoneVO.getPrimaryNumber())) {
                        _channelUserVO.setMsisdn(phoneVO.getMsisdn());
                        locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                    }
                }
                _log.debug("UserAddController process : ", " _channelUserVO.getStatus() = " + _channelUserVO.getStatus());
                BTSLMessages btslPushMessage = null;

                // 11-MAR-2014
                final String[] arr = { _channelUserVO.getUserName(), "" };
                String[] arrArray = null;
                if (PretupsI.USER_STATUS_ACTIVE.equals(_channelUserVO.getStatus())) {
                    // send a message to the user about there activation
                    if (locale == null) {
                        locale = p_requestVO.getLocale();
                    }
                    if (TypesI.YES.equals(_categoryVO.getWebInterfaceAllowed()) && TypesI.YES.equals(_categoryVO.getSmsInterfaceAllowed())) {
                        // send message for both login id and sms pin

                        // changes by hitesh ghanghas
                        if (!BTSLUtil.isNullString(_channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
                            if (BTSLUtil.isNullString(webPassword)) {
                                arrArray = new String[] { _channelUserVO.getLoginID(), _channelUserVO.getMsisdn(), "", randomPwd, senderPin };
                            } else {
                                arrArray = new String[] { _channelUserVO.getLoginID(), _channelUserVO.getMsisdn(), "", webPassword, senderPin };
                            }
                            p_requestVO.setMessageCode(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE);
                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                            final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, _channelUserVO.getNetworkID());
                            pushMessage.push();

                        }

                        else {
                            if (!BTSLUtil.isNullString(_channelUserVO.getLoginID())) {
                                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE);
                                if (BTSLUtil.isNullString(webPassword)) {
                                    arrArray = new String[] { _channelUserVO.getLoginID(), _channelUserVO.getMsisdn(), "", randomPwd, senderPin };
                                } else {
                                    arrArray = new String[] { _channelUserVO.getLoginID(), _channelUserVO.getMsisdn(), "", webPassword, senderPin };
                                }
                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                                final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, _channelUserVO.getNetworkID());
                                pushMessage.push();

                            } else {
                                arrArray = new String[] { _channelUserVO.getMsisdn(), "", senderPin };
                                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE);
                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                                final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, _channelUserVO.getNetworkID());
                                pushMessage.push();
                            }
                        }
                        // Changes end by hitesh ghanghas
                        // Email for pin & password-mail push

                    } else if (TypesI.YES.equals(_categoryVO.getWebInterfaceAllowed()) && TypesI.NO.equals(_categoryVO.getSmsInterfaceAllowed()) && !BTSLUtil
                        .isNullString(_channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
                        // send message for login id

                        p_requestVO.setMessageCode(PretupsErrorCodesI.USER_WEB_ACTIVATE);
                        if (BTSLUtil.isNullString(webPassword)) {
                            arrArray = new String[] { _channelUserVO.getLoginID(), randomPwd };
                        } else {
                            arrArray = new String[] { _channelUserVO.getLoginID(), webPassword };
                        }
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                        final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, _channelUserVO.getNetworkID());
                        pushMessage.push();
                        // Email for pin & password- code for email details

                    } else {
                        // send message for sms pin
 
                        p_requestVO.setMessageCode(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE);
                        arrArray = new String[] { _channelUserVO.getMsisdn(), "", senderPin };
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                        final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, _channelUserVO.getNetworkID());
                        pushMessage.push();
                        // Email for pin & password- code for email details

                    }

                    // pusing individual sms
                    String messageCode = p_requestVO.getMessageCode();
                    if (_channelUserVO.getMsisdnList() != null && _channelUserVO.getMsisdnList().size() > 1) {
                        // it mean that it has secondary number and now push
                        // message to individual secondary no

                        final ArrayList newMsisdnList = _channelUserVO.getMsisdnList();
                        UserPhoneVO newUserPhoneVO = null;
                        // Email for pin & password
                        final String subject = null;
                        final EmailSendToUser emailSendToUser = null;
                        final String tmpMsisdn = _channelUserVO.getMsisdn();
                        String[] newarr = null;
                        for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
                            btslPushMessage = null;
                            newUserPhoneVO = (UserPhoneVO) newMsisdnList.get(i);
                            if (TypesI.NO.equals(newUserPhoneVO.getPrimaryNumber())) {
                                newarr = new String[] { newUserPhoneVO.getMsisdn(), "", senderPin };
                                locale = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());

                                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE);
                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, newarr);
                              
                                final PushMessage pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), btslPushMessage, "", "", locale, _channelUserVO.getNetworkID());
                                pushMessage.push();
                                // Email for pin & password- code for email
                                // details

                            }
                        }
                    }

                   p_requestVO.setMessageCode(messageCode);
                } else {

                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
                }
                p_requestVO.setMessageArguments(arrArray);
                
                //OTF Message function while adding
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,_channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,_channelUserVO.getNetworkID()))){
                	if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue()==0){
                		TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
                		tbcm.loadCommissionProfileDetailsForOTFMessages(con,_channelUserVO);
                	}
                }
                // Ended Here
            }
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserAddController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            _channelUserDao = null;
            _channelUserWebDao = null;
            _userDAO = null;
            _channelUserVO = null;
            _categoryVO = null;

			if (mcomCon != null) {
				mcomCon.close("UserAddController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

	private void defaultCommissionPrfSetId(final String METHOD_NAME,
			Connection con, final String userCategeryCode,
			final String networkCode, String defaultUserGradeCode,
			String defaultCommissionProfileSetID) throws BTSLBaseException {
		try {

		    defaultCommissionProfileSetID = _extUserDao.getDefaultCommisionProfileSetIDByCategoryID(con, userCategeryCode, networkCode, defaultUserGradeCode);
		    defaultCommissionProfileSetID = defaultCommissionProfileSetID.trim();
		    _channelUserVO.setCommissionProfileSetID(defaultCommissionProfileSetID);
		} catch (Exception e) {
		    _log.errorTrace(METHOD_NAME, e);
		    if (BTSLUtil.isNullString(defaultCommissionProfileSetID)) {
		        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_DEFAULT_COMMISSION_PROFILE_NOT_FOUND);
		    }
		}
	}

	private boolean checknetworkList(final String actualNetworkCode,
			final ArrayList networkList, boolean actualNetworkExist) {
		NetworkVO networkVO;
		if (networkList != null && !networkList.isEmpty()) {
		    for (int i = 0, j = networkList.size(); i < j; i++) {
		        networkVO = (NetworkVO) networkList.get(i);
		        if (networkVO.getNetworkCode().equalsIgnoreCase(actualNetworkCode)) {
		            actualNetworkExist = true;
		        }
		    }
		}
		return actualNetworkExist;
	}

	private String usingDefaultUserGradeCode(final String METHOD_NAME,
			Connection con, final String userCategeryCode,
			String defaultUserGradeCode) throws BTSLBaseException {
		try {
		    defaultUserGradeCode = _extUserDao.getDefaultGradeCode(con, userCategeryCode);
		    defaultUserGradeCode = defaultUserGradeCode.trim();
		    _channelUserVO.setUserGrade(defaultUserGradeCode);
		} catch (Exception e) {
		    _log.errorTrace(METHOD_NAME, e);
		    if (BTSLUtil.isNullString(defaultUserGradeCode)) {
		        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_DEFAULT_GRADE_NOT_FOUND);
		    }
		}
		return defaultUserGradeCode;
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
        if (_log.isDebugEnabled()) {
            _log.debug("generateUserId", "Entered p_networkCode=" + p_networkCode + ", p_prefix=" + p_prefix);
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
     * @description : Method to prepareUserPhoneVOList
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return : ArrayList
     */
    private ArrayList prepareUserPhoneVOList(Connection con, HashMap requestMap, ChannelUserVO _channelUserVO, Date currentDate, String senderPin) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareUserPhoneVOList", "Entered _channelUserVO.getCategoryCode()=" + _channelUserVO.getCategoryCode());
        }

        final ArrayList phoneList = new ArrayList();
        final ArrayList msisdnList = (ArrayList) requestMap.get("MSISDNLIST");
        NetworkPrefixVO networkPrefixVO = null;
        String msisdn ;
        String stkProfile = null;
        final ArrayList stkProfileList = _userDAO.loadPhoneProfileList(con, _channelUserVO.getCategoryCode());
        if (stkProfileList != null) {
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
                    phoneVO.setUserId(_channelUserVO.getUserID());
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
                    if (msisdn.equals(PretupsBL.getFilteredMSISDN((String) requestMap.get("PRIMARYMSISDN"))))// made
                    // filtered
                    // msisdn
                    {
                        _channelUserVO.setMsisdn(msisdn);
                        phoneVO.setPrimaryNumber(PretupsI.YES);
                    } else {
                        phoneVO.setPrimaryNumber(PretupsI.NO);
                    }
                    /*
                     * Code Added for MNP
                     * Preference to check whether MNP is allowed in system or
                     * not.
                     * If yes then check whether Number has not been ported out,
                     * If yes then throw error, else continue
                     */
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(msisdn));
                        boolean numberAllowed = false;
                        if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn, "", PretupsI.PORTED_IN);
                            if (!numberAllowed) {
                                throw new BTSLBaseException("UserAddController", "validateMsisdn", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_IN);
                            }
                        } else {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn, "", PretupsI.PORTED_OUT);
                            if (numberAllowed) {
                                throw new BTSLBaseException("UserAddController", "validateMsisdn", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_OUT);
                            }
                        }
                    }
                    // MNP Code End
                    phoneVO.setPinModifyFlag(true);
                    if (_userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                        final String[] arr = { phoneVO.getMsisdn() };
                        throw new BTSLBaseException("UserAddController", "validateMsisdn", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                    }
                    phoneList.add(phoneVO);
                }
            }
        }
        _channelUserVO.setMsisdnList(phoneList);
        return phoneList;
    }
}
