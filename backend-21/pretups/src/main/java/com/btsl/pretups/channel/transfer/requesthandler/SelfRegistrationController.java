package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
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
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channeluser.businesslogic.Msisdn;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

public class SelfRegistrationController implements ServiceKeywordControllerI{
    private Log LOG = LogFactory.getLog(SelfRegistrationController.class.getName());
    private ChannelUserVO channelUserVO = null;
    private ChannelUserDAO channelUserDao = null;
    private ChannelUserWebDAO channelUserWebDao = null;
    private UserDAO userDAO = null;

    /**
     * Method Process
     * 
     * @param p_requestVO
     */
    public void process(RequestVO requestVO) {
		final String methodName =  "SelfRegistrationController";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");
		}

        LogFactory.printLog(methodName, "Entered requestVO=" + requestVO, LOG);
        Connection con = null;MComConnectionI mcomCon = null;
        channelUserDao = new ChannelUserDAO();
        channelUserWebDao = new ChannelUserWebDAO();
        channelUserVO = new ChannelUserVO();
        userDAO = new UserDAO();
        OperatorUtilI operatorUtili = null;
        Locale locale = null;
        String senderPin = "";
        String webPassword = null;
        String randomPwd = null;
        String defaultGeoCode = "";
        HashMap responseMap = new HashMap();
        HashMap reqMap = requestVO.getRequestMap();
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
        BaseResponse response = null;
        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
        	response = new BaseResponse();
			LOG.debug(methodName, "Gson Conversion");
			
            final String userCategeryCode = Constants.getProperty("USERCATEGORY");
            // Load category
            final List catList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con, userCategeryCode);
            if (catList == null || catList.isEmpty()) {
                throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
            }
            CategoryVO categoryVO = (CategoryVO) catList.get(0);
            if(!"ZO".equalsIgnoreCase(categoryVO.getGrphDomainType()))
            {
            	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
            }
            if(BTSLUtil.isNullString(Constants.getProperty("GEOGRAPHYCODE")))
            {
            	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.GEO_CODE_BLANK);
            }
            // if null category does not exist
            channelUserVO.setCategoryVO(categoryVO);
            channelUserVO.setCategoryCode(categoryVO.getCategoryCode());
            channelUserVO.setCategoryName(categoryVO.getCategoryName());
            channelUserVO.setDomainTypeCode(categoryVO.getDomainTypeCode());
            if (TypesI.YES.equals(categoryVO.getOutletsAllowed())) {
                // load the outlet dropdown
            	// load the outlet dropdown
                ArrayList outLetList=LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true);
                boolean flag5=true;
                for(int k=0;k<outLetList.size();k++)
                {
                	if(((ListValueVO)outLetList.get(k)).getValue().equals(Constants.getProperty("OUTLETCODE").trim()))
                	{
                		flag5=false;
                	}
                }
                if(flag5==true)
                {
                	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.OUTLET_CODE_DOES_NOT_EXIST);
                }
                channelUserVO.setOutletCode(Constants.getProperty("OUTLETCODE").trim());
                 final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
                 ArrayList suboutLetList=sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE);
                 boolean flag6=true;
                 for(int k=0;k<suboutLetList.size();k++)
                 {
                 	String suboutletCode=((ListValueVO)suboutLetList.get(k)).getValue();
                 	String []split=suboutletCode.split(":");
                 	if((split[1].equals(Constants.getProperty("OUTLETCODE").trim())))
                 	{
                 		if(split[0].equals(Constants.getProperty("SUBOUTLETCODE").trim()))
                 		{
                 			flag6=false;
                 		}
                 	}
                 }
                 if(flag6==true)
                 {
                 	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.SUB_OUTLET_CODE_DOES_NOT_EXIST);
                 }
                 channelUserVO.setSubOutletCode(Constants.getProperty("SUBOUTLETCODE").trim());
            }
            RequestVO requestVO1= new RequestVO();
            requestVO1.setUserCategory(categoryVO.getCategoryCode());
            // User name set
            if(BTSLUtil.isNullObject(reqMap.get("USERNAME")))
            {
            	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.XML_ERROR_USER_NAME_BLANK);
            }
            
            	channelUserVO.setUserName(reqMap.get("USERNAME").toString());
            	channelUserVO.setFirstName(reqMap.get("USERNAME").toString());
            	
            	if(reqMap.get("USERNAME").toString().length()>10)
            	{
            		channelUserVO.setShortName(reqMap.get("USERNAME").toString().substring(0, 10));
            	}
            	else
            	{
            		channelUserVO.setShortName(reqMap.get("USERNAME").toString());
            	}
            String webLoginId = "";
            if(BTSLUtil.isNullObject(reqMap.get("MSISDN")))
            {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_MSISDN);
            }
            if(BTSLUtil.isNullObject(reqMap.get("PIN")))
            {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_PIN_BLANK);
            }
            Msisdn msisdn = new Msisdn();
            msisdn.setIsprimary("Y");
            msisdn.setDescription("REGISTERSELF");
            msisdn.setPhoneNo(reqMap.get("MSISDN").toString());
            msisdn.setPin(reqMap.get("PIN").toString());
            msisdn.setStkProfile(categoryVO.getCategoryCode());
            String primaryMsisdn = "";
         	primaryMsisdn=msisdn.getPhoneNo();
            senderPin=msisdn.getPin();
            	 if(msisdn.getPin()==null)
            	 {
            		 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PIN_REQUIRED);
            	 }
            	 operatorUtili.validatePIN(senderPin);
                if (BTSLUtil.isNullString(webPassword)) {
                    randomPwd = operatorUtili.generateRandomPassword();
                    channelUserVO.setPassword(BTSLUtil.encryptText(randomPwd).trim());
                 }
            // Ended Here
            // User name prefix.
            if(BTSLUtil.isNullString(Constants.getProperty("USERNAMEPREFIX")))
            {
            	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.USERNAME_TYPE_DOES_NOT_EXIST);
            }
            final String userPrifix = Constants.getProperty("USERNAMEPREFIX").toUpperCase();
            ArrayList userNameList=LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true);
            boolean flag4=true;
            for(int k=0;k<userNameList.size();k++)
            {
            	if(((ListValueVO)userNameList.get(k)).getValue().equals(Constants.getProperty("USERNAMEPREFIX").trim()))
            	{
            		flag4=false;
            	}
            }
            if(flag4==true)
            {
            	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.USERNAME_TYPE_DOES_NOT_EXIST);
            }
            channelUserVO.setUserNamePrefix(userPrifix.trim());

            

            // Set actual network of the user for
            final String actualNetworkCode = Constants.getProperty("EXTERNALNWCODE");
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
                    throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.EXTSYS_REQ_ACTUAL_NW_CODE_INVALID);
                }
            }

         // EmailId
            boolean blank;
            if(BTSLUtil.isNullObject(reqMap.get("EMAILID")))
            {
            	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.EXTSYS_REQ_EMAILID_NOTFOUND);
            }
            final String modifedEmail = reqMap.get("EMAILID").toString();
            blank = BTSLUtil.isNullString(modifedEmail);
            boolean validEmail = false;
            if (!blank) {
                validEmail = BTSLUtil.validateEmailID(modifedEmail);
                if (validEmail) {
                    channelUserVO.setEmail(modifedEmail.toString().trim());
                }
                else
                {
                	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.INVALID_EMAIL_MAPP);
                }
            }
            channelUserVO.setNetworkID(Constants.getProperty("EXTERNALNWCODE").trim());
            channelUserVO.setUserID(generateUserId(channelUserVO.getNetworkID(), categoryVO.getUserIdPrefix()));
            boolean isownerIDNew = false;
            final String categoryID = String.valueOf(categoryVO.getSequenceNumber());
            /*if ("1".equals(categoryID)) {*/
            	isownerIDNew=true;
                channelUserVO.setParentID(PretupsI.ROOT_PARENT_ID);
                channelUserVO.setOwnerID(channelUserVO.getUserID());
           /* } */
            // Generate user id for new channel user
            
            // code for geography is moved here after parent details fetched as parent geocode is required
            String geocode=Constants.getProperty("GEOGRAPHYCODE").trim();
            LogFactory.printLog(methodName, "geocode value = "+geocode, LOG);
            
            if (!BTSLUtil.isNullString(geocode)){    
                // logic to validate the passed geocode
                GeographicalDomainWebDAO domainWebDAO = new GeographicalDomainWebDAO();
                LogFactory.printLog(methodName, "top level hirearchy = "+geocode, LOG);
            	List geoList = new ArrayList<>();
                List geographyList = domainWebDAO.loadGeographicalDomainCodebyNetwork(con, "ZO",channelUserVO.getNetworkID());
                for(int i=0;i<geographyList.size();i++)
                {
                	geoList.add(((GeographicalDomainVO)geographyList.get(i)).getGrphDomainCode());
                }
                if(!(geoList.contains(geocode)))
                {
                	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 
                }
                defaultGeoCode=geocode;
                channelUserVO.setGeographicalCode(defaultGeoCode);
                LogFactory.printLog(methodName, "Passed GeoCode = "+defaultGeoCode, LOG);
            }
            else
            {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 
            }
    
            // User hierarchy check
            {
                final String networkCode = Constants.getProperty("EXTERNALNWCODE");

                
                if (isownerIDNew) {
                    channelUserVO.setOwnerID(channelUserVO.getUserID());
                }
                LOG.debug("SelfRegistrationController", "process : channelUserVO.getUserID = " + channelUserVO.getUserID());
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
                    if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
                        channelUserVO.setStatus(PretupsI.USER_STATUS_PREACTIVE);// PA
                        // Active
                        channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_PREACTIVE);// PA
                        // Active
                    } else {
                        channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// A
                        // Active
                        channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);// A
                        // Active
                    }
                
                
                final Date currentDate = new Date();
                
                // prepare UserPhone VO list
                
                final ArrayList userPhoneList = (ArrayList) prepareUserPhoneVOList(con, msisdn, channelUserVO, currentDate, reqMap);
                final String extCode = primaryMsisdn;
                if (!BTSLUtil.isNullString(extCode)) {
                    final boolean isExtCodeExist = channelUserWebDao.isExternalCodeExist(con, extCode, null);
                    if (isExtCodeExist) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST);
                    }
                }
                channelUserVO.setExternalCode(extCode);
                if (BTSLUtil.isNullString(webLoginId)) {
                    channelUserVO.setLoginID(channelUserVO.getUserID());
                } 
                // set some use full parameter
                channelUserVO.setPasswordModifiedOn(currentDate);
                channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);
                channelUserVO.setCreationType("M");
                channelUserVO.setUserCode(primaryMsisdn);
                channelUserVO.setCreatedBy(channelUserVO.getUserID());

                final Date curDate = new Date();
                channelUserVO.setCreatedOn(curDate);
                channelUserVO.setMsisdn(primaryMsisdn);
                channelUserVO.setModifiedBy(channelUserVO.getUserID());
                channelUserVO.setModifiedOn(currentDate);
                channelUserVO.setUserProfileID(channelUserVO.getUserID());
                channelUserVO.setPasswordModifiedOn(currentDate);
                channelUserVO.setPasswordCountUpdatedOn(currentDate);
                ArrayList paymentList=LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
                if(!BTSLUtil.isNullOrEmptyList(paymentList))
                {
                	ArrayList<String> payList=new ArrayList<>();
                for(int l=0;l<paymentList.size();l++)
                {
                	payList.add(((ListValueVO)paymentList.get(l)).getValue());
                }
                boolean flag3=false;
                String paymentTypes=Constants.getProperty("PAYMENTTYPE");
                if(paymentTypes!=null)
                {
                String []payTypes=paymentTypes.trim().split(",");
                for(int k=0;k<payTypes.length;k++)
                {
                	if(!(payList.contains(payTypes[k])))
                	{
                		flag3=true;
                		break;
                	}
                }
                if(flag3==true)
                {
                	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.PAYMENT_TYPE_DOES_NOT_EXIST);
                }
                }
                }
                channelUserVO.setPaymentTypes(Constants.getProperty("PAYMENTTYPE") != null ? Constants.getProperty("PAYMENTTYPE"): "");
                
                if (TypesI.YES.equals(categoryVO.getLowBalAlertAllow())) {
                    final String allowforself =  Constants.getProperty("LOWBALALERTSELF");
                    final StringBuilder alerttype = new StringBuilder("");
                    if (TypesI.YES.equals(allowforself)) {
                        alerttype.append(PretupsI.ALERT_TYPE_SELF);
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
                channelUserVO.setMsisdn(BTSLUtil.NullToString(primaryMsisdn));
                final int phoneCount = userDAO.addUserPhoneList(con, userPhoneList);
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
                String inSuspend =  Constants.getProperty("INSUSPEND");
                if(!BTSLUtil.isNullString(inSuspend)){
                    if(inSuspend.trim().equals(PretupsI.YES) || inSuspend.trim().equals(PretupsI.NO)){
                        channelUserVO.setInSuspend(inSuspend);
                    }else{
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_INSUSPEND_INVALID);
                    }    
                }else{
                    channelUserVO.setInSuspend("N");
                }    
                String outSuspend = Constants.getProperty("OUTSUSPEND");
                if(!BTSLUtil.isNullString(outSuspend)){
                    if(outSuspend.trim().equals(PretupsI.YES) || outSuspend.trim().equals(PretupsI.NO)){
                        channelUserVO.setOutSuspened(outSuspend);
                    }else{
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_OUTSUSPEND_INVALID);
                    }        
                }else{
                    channelUserVO.setOutSuspened("N");
                }   
                UserWebDAO userWebDAO = new UserWebDAO();
                ArrayList commissionProfileList=userWebDAO.loadCommisionProfileListByCategoryIDandGeography(con, userCategeryCode, channelUserVO.getNetworkID(),null);

                final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
                // load the User Grade dropdown
                ArrayList gradelist=categoryGradeDAO.loadGradeList(con, userCategeryCode);

                // load the Transfer Profile dropdown
                final TransferProfileDAO profileDAO = new TransferProfileDAO();
                ArrayList transferprofilelist=profileDAO.loadTransferProfileByCategoryID(con, channelUserVO.getNetworkID(), userCategeryCode,
                                PretupsI.PARENT_PROFILE_ID_USER);
                // load the Transfer Rule Type at User level
                ArrayList transferRuleTypeList =null;
                final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, channelUserVO.getNetworkID(), userCategeryCode)).booleanValue();
                if (isTrfRuleTypeAllow) {
                	transferRuleTypeList=(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
                }

                ArrayList LmsProfileList=null;
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
                    LmsProfileList=channelUserWebDao.getLmsProfileList(con, channelUserVO.getNetworkID());
                }
            
                boolean flag=true;
                String userGrade =null;
                String lmsProfileID = null,defaultCommissionProfileSetID=null,defaultTransferProfileID=null,transferTypeRuleId=null;
                	userGrade =  Constants.getProperty("USERGRADE");
                	if(BTSLUtil.isNullString(userGrade))
                	{
                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GRADE_INVALID);
                	}
                	else
                	{
                		for(int i=0;i<gradelist.size();i++)
                		{
                			if(((GradeVO)gradelist.get(i)).getGradeCode().equals(userGrade))
                			{
                				flag=false;
                			}
                		}
                		if(flag==true)
                		{
                			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GRADE_INVALID);
                		}
                	}
                	channelUserVO.setUserGrade(userGrade);
                	defaultCommissionProfileSetID = Constants.getProperty("COMMISSIONPROFILEID");
                	if(BTSLUtil.isNullString(defaultCommissionProfileSetID))
                	{
                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMMISSION_SET_ID_INVALID);
                	}
                	else
                	{
                		flag=true;
                		for(int i=0;i<commissionProfileList.size();i++)
                		{
                			if(((CommissionProfileSetVO)commissionProfileList.get(i)).getCommProfileSetId().equals(defaultCommissionProfileSetID.trim()))
                			{
                				flag=false;
                			}
                		}
                		if(flag==true)
                		{
                			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMMISSION_SET_ID_INVALID);
                		}
                	}
                	channelUserVO.setCommissionProfileSetID(defaultCommissionProfileSetID.trim());
                	defaultTransferProfileID=Constants.getProperty("TRANSFERPROFILE");
                	if(BTSLUtil.isNullString(defaultTransferProfileID))
                	{
                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_TRANSFER_PROFILE_NOT_EXIST);
                	}

                	else{
                		flag=true;
            		for(int i=0;i<transferprofilelist.size();i++)
            		{
            			if(((ListValueVO)transferprofilelist.get(i)).getValue().equals(defaultTransferProfileID.trim()))
            			{
            				flag=false;
            			}
            		}
            		if(flag==true)
            		{
            			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_TRANSFER_PROFILE_NOT_EXIST,new String[]{channelUserVO.getUserName()});
            		}
                	}
                
                	channelUserVO.setTransferProfileID(defaultTransferProfileID.trim());
                	transferTypeRuleId=Constants.getProperty("TRANSFERRULETYPE");
                	 if(isTrfRuleTypeAllow)
                     {
                	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue())
                	{
                		if(BTSLUtil.isNullString(transferTypeRuleId))
                    	{
                    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_TYPE_NOT_EXIST);
                    	}
                		else
                    	{
                    		flag=true;
                		for(int i=0;i<transferRuleTypeList.size();i++)
                		{
                			if(((ListValueVO)transferRuleTypeList.get(i)).getValue().equals(transferTypeRuleId.trim()))
                			{
                				flag=false;
                			}
                		}
                		if(flag==true)
                		{
                			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_TYPE_NOT_EXIST);
                		}
                    	}
                		channelUserVO.setTrannferRuleTypeId(transferTypeRuleId.trim());
                	}
                     }
                	lmsProfileID=Constants.getProperty("LMSPROFILEID");
                	if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
                		
                		String controlGroupRequired = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
            			if(controlGroupRequired == null || controlGroupRequired == ""){
            				controlGroupRequired="Y";
            			}
            			if("Y".equals(controlGroupRequired)) {
            				channelUserVO.setControlGroup(Constants.getProperty("CONTROLGROUP"));
            			}
                		if(BTSLUtil.isNullString(lmsProfileID))
                    	{
                    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LMS_SETID_NOT_FOUND);
                    	}
                		else{

                    		flag=true;
                		for(int i=0;i<LmsProfileList.size();i++)
                		{
                			if(((ListValueVO)LmsProfileList.get(i)).getValue().equals(Constants.getProperty("LMSPROFILEID").trim()))
                			{
                				flag=false;
                			}
                		}
                		if(flag==true)
                		{
                			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LMS_SETID_NOT_FOUND);
                		}
                    	
                		}
                        channelUserVO.setLmsProfile(Constants.getProperty("LMSPROFILEID").trim());
                    }
                	
                
               
                    channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
                    channelUserVO.setMpayProfileID("");
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
                    	channelUserVO.setControlGroup(PretupsI.NO);
                }
              //Single Association with control
                if (LOG.isDebugEnabled()){
 	        		LOG.debug(methodName,"theForm.getLmsProfileId() = "+Constants.getProperty("LMSPROFILEID")+", channelUserVO.getLmsProfile() = "+channelUserVO.getLmsProfile());
 	        	}
	            double targetCount = 0d;
	 	        double controlCount = 0d; 
	 	        int retval4Association = 0;
	 	        int retval4deAssociation = 0;
	 	        int retvalTargetControl = 0;
	 	        Map<String,Double> countOfUsersInTargetControlGroup = null;
	 	        double targetCountOfassprofile = 0d;
	 	        double controlCountOfassprofile = 0d; 
	 	        int retval4AssociationOfassprofile = 0;
	 	        int retval4deAssociationOfassprofile = 0;
	 	        int retvalTargetControlOfassprofile = 0;
	 	        Map<String,Double> countOfUsersInTargetControlGroupOfassprofile = null;
	 	        ChannelUserVO channelUserLMSVO = null;
	 	        if(!BTSLUtil.isNullString(channelUserVO.getLmsProfile()))
	 	        {
	 	          countOfUsersInTargetControlGroup = channelUserDao.countOfUsersInTargetControlGroup(con,channelUserVO.getLmsProfile());
	 	          if(countOfUsersInTargetControlGroup!=null){
	 	        	 controlCount = countOfUsersInTargetControlGroup.get("control_count");
	 	        	 targetCount = countOfUsersInTargetControlGroup.get("target_count");
	 	        	 retval4Association = Double.compare(targetCount, 0d);
	 	        	 retval4deAssociation = Double.compare(controlCount, 0d);
	 	        	 retvalTargetControl = Double.compare(targetCount,controlCount);
	 	        	if (LOG.isDebugEnabled()){
	 	        		LOG.debug(methodName,"control_count = "+controlCount+", target_count = "+targetCount+", retval4Association = "+retval4Association+", retval4deAssociation = "+retval4deAssociation+", retvalTargetControl = "+retvalTargetControl);
	 	        	}
	 	          }
	 	          //Need to check if channel user already associated with lms profile and wants to associate with another lms profile
	 	          channelUserLMSVO = channelUserDao.loadChannelUser(con, channelUserVO.getUserID());
	 	        	if (LOG.isDebugEnabled()) {
	 	        		LOG.debug(methodName,"Already associated with lms profile :: channelUserLMSVO.getControlGroup() = "+channelUserLMSVO.getControlGroup());
	 	        	}
	 	        	if(!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfile()) && !BTSLUtil.isNullString(channelUserLMSVO.getControlGroup()) && PretupsI.NO.equalsIgnoreCase(channelUserLMSVO.getControlGroup()))
	 	        	{
	 	        		countOfUsersInTargetControlGroupOfassprofile = channelUserDao.countOfUsersInTargetControlGroup(con,channelUserLMSVO.getLmsProfile());
	 		 	          if(countOfUsersInTargetControlGroupOfassprofile!=null){
	 		 	        	 controlCountOfassprofile = countOfUsersInTargetControlGroupOfassprofile.get("control_count");
	 		 	        	 targetCountOfassprofile = countOfUsersInTargetControlGroupOfassprofile.get("target_count");
	 		 	        	 retval4AssociationOfassprofile = Double.compare(targetCountOfassprofile, 1d);
	 		 	        	 retval4deAssociationOfassprofile = Double.compare(controlCountOfassprofile, 1d);
	 		 	        	 retvalTargetControlOfassprofile = Double.compare(targetCountOfassprofile,controlCountOfassprofile);
	 		 	        	 if (LOG.isDebugEnabled()){
	 		 	        		LOG.debug(methodName,"Already associated with lms profile"+channelUserLMSVO.getLmsProfile()+" :: control_count = "+controlCountOfassprofile+", target_count = "+targetCountOfassprofile+", retval4Association = "+retval4AssociationOfassprofile+", retval4deAssociation = "+retval4deAssociationOfassprofile+", retvalTargetControl = "+retvalTargetControlOfassprofile);
	 		 	        	 }
	 		 	        	 if(retvalTargetControlOfassprofile < 0 || (retval4deAssociationOfassprofile == 0 && retval4AssociationOfassprofile==0) ){
								if (LOG.isDebugEnabled()) LOG.debug(methodName,"Already associated with lms profile :: User de-association is not allowded from target group as one user still exists into the control group of already associated profile");
								String arr[] = {channelUserVO.getUserName(),""};
				 	            String key="user.associatechanneluser.alreadyassociated.oneuserstillexistsintocontrolgroup";
				 	           throw new BTSLBaseException(this, methodName, key,arr);
							 }
	 		 	        	 else if(retvalTargetControlOfassprofile > 0 || ( retval4AssociationOfassprofile > 1 && retval4deAssociationOfassprofile < 0))
							 {
								if (LOG.isDebugEnabled()){
									LOG.debug(methodName,"Already associated with lms profile :: User Association is allowded into the target group as no such user into the control group of already associate profile");
								}
								
							 } 
	 		 	          }
	 	        	}
	 	          //
	 	          if(BTSLUtil.isNullString(channelUserVO.getControlGroup())){
	 	        	 if (LOG.isDebugEnabled()) LOG.debug(methodName,"The value of control group is missing");
						String arr[] = {channelUserVO.getUserName(),""};
		 	            String key="user.associatechanneluser.updatecontrollednotfound";
		 	           throw new BTSLBaseException(this, methodName, key,arr);
					}
					else if(PretupsI.YES.equalsIgnoreCase(channelUserVO.getControlGroup()))
					{
						if(retval4Association>=1)
						{
							if(channelUserDao.isProfileActive(channelUserVO.getMsisdn(),channelUserVO.getLmsProfile())){
								if (LOG.isDebugEnabled()) LOG.debug(methodName,"User assocition is not allowded into control group profile as profile is active");
								String arr[] = {channelUserVO.getUserName(),""};
				 	            String key="user.associatechanneluser.updatecontrolledactive";
				 	           throw new BTSLBaseException(this, methodName, key,arr);
							} 
						} else {
							if (LOG.isDebugEnabled()) LOG.debug(methodName,"User association is not allowded into control group as  no user belong to target group of this profile");
							String arr[] = {channelUserVO.getUserName(),""};
			 	            String key="user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup";
			 	           throw new BTSLBaseException(this, methodName, key,arr);
						}
					}
					else if(PretupsI.NO.equalsIgnoreCase(channelUserVO.getControlGroup()))
					{
						// if profile was already associated with lms profile into the control group
		 	        	channelUserLMSVO = channelUserDao.loadChannelUser(con, channelUserVO.getUserID());
		 	        	if(!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfileId()) && !BTSLUtil.isNullString(channelUserLMSVO.getControlGroup()) && PretupsI.YES.equalsIgnoreCase(channelUserLMSVO.getControlGroup()))
		 	        	{
		 	        	  countOfUsersInTargetControlGroup = channelUserDao.countOfUsersInTargetControlGroup(con,channelUserLMSVO.getLmsProfileId());
	 		 	          if(countOfUsersInTargetControlGroup!=null){
	 		 	        	 controlCount = countOfUsersInTargetControlGroup.get("control_count");
	 		 	        	 targetCount = countOfUsersInTargetControlGroup.get("target_count");
	 		 	        	 retval4Association = Double.compare(targetCount, 0d);
	 		 	        	 retval4deAssociation = Double.compare(controlCount, 0d);
	 		 	        	 retvalTargetControl = Double.compare(targetCount,controlCount);
	 		 	        	if (LOG.isDebugEnabled()){
	 		 	        		LOG.debug(methodName,"control_count = "+controlCount+", target_count = "+targetCount+", retval4Association = "+retval4Association+", retval4deAssociation = "+retval4deAssociation+", retvalTargetControl = "+retvalTargetControl);
	 		 	        	}
							if(retvalTargetControl == 0 || retval4Association>=0 )
							{
								if (LOG.isDebugEnabled()){
									LOG.debug(methodName,"User Association is allowded into the target group as no such user into the control group of this profile");
								}
								
							} else if(retval4deAssociation <=1) {
								if (LOG.isDebugEnabled()) LOG.debug(methodName,"User de-association is not allowded from target group as one user still exists into the control group of this profile");
								String arr[] = {channelUserVO.getUserName(),""};
				 	            String key="user.associatechanneluser.targetgroup.oneuserstillexistsintocontrolgroup";
				 	           throw new BTSLBaseException(this, methodName, key,arr);
							}
		 		 	   }
					}
					}
	 	        } else {
	 	        	if (LOG.isDebugEnabled()) {
	 	        		LOG.debug(methodName,"If profile was already associated with lms profile into the control group : channelUserVO.getUserID() = "+channelUserVO.getUserID());
	 	        	}
	 	        	// if profile was already associated with lms profile into the control group
	 	        	channelUserLMSVO = channelUserDao.loadChannelUser(con, channelUserVO.getUserID());
	 	        	if (LOG.isDebugEnabled()) {
	 	        		LOG.debug(methodName," channelUserLMSVO.getControlGroup() = "+channelUserLMSVO.getControlGroup());
	 	        	}
	 	        	if(!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfile()) && !BTSLUtil.isNullString(channelUserLMSVO.getControlGroup()))
	 	        	{
	 	        		countOfUsersInTargetControlGroup = channelUserDao.countOfUsersInTargetControlGroup(con,channelUserLMSVO.getLmsProfile());
	 		 	          if(countOfUsersInTargetControlGroup!=null){
	 		 	        	 controlCount = countOfUsersInTargetControlGroup.get("control_count");
	 		 	        	 targetCount = countOfUsersInTargetControlGroup.get("target_count");
	 		 	        	 retval4Association = Double.compare(targetCount, 1d);
	 		 	        	 retval4deAssociation = Double.compare(controlCount, 1d);
	 		 	        	 retvalTargetControl = Double.compare(targetCount,controlCount);
	 		 	        	if (LOG.isDebugEnabled()){
	 		 	        		LOG.debug(methodName,"control_count = "+controlCount+", target_count = "+targetCount+", retval4Association = "+retval4Association+", retval4deAssociation = "+retval4deAssociation+", retvalTargetControl = "+retvalTargetControl);
	 		 	        	}
	 		 	        	if(retvalTargetControl > 0 || retval4Association > 0 )
							{
								if (LOG.isDebugEnabled()){
									LOG.debug(methodName,"User Association is allowded into the target group as no such user into the control group of this profile");
								}
								
							} else if(retval4deAssociation <=1) {
								if (LOG.isDebugEnabled()) LOG.debug(methodName,"User de-association is not allowded from target group as one user still exists into the control group of this profile");
								String arr[] = {channelUserVO.getUserName(),""};
				 	            String key="user.associatechanneluser.targetgroup.oneuserstillexistsintocontrolgroup";
				 	           throw new BTSLBaseException(this, methodName, key,arr);
							}
	 		 	          }
	 	        	}
					
				}
 
                // Assign Services
                String services =  Constants.getProperty("SERVICES");
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
                String productCodes =  Constants.getProperty("PRODUCTCODE");
                final UserBalancesDAO userBalanceDAO = new UserBalancesDAO();
                try {
                    if(!BTSLUtil.isNullString(productCodes)){
                    	int count = 0;
                        final String[] productList = productCodes.split(",");
                        for(int i=0;i<productList.length;i++)
                        count =count+ userBalanceDAO.insertUserBalancesMappGw(con, channelUserVO.getUserID(), productList[i], channelUserVO.getNetworkCode());
                        if (count <= 0) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PRODUCT_NOT_FOUND);
                        }
                    }else{
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PRODUCT_NOT_FOUND);
                        }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PRODUCT_NOT_FOUND);
                }
	
                boolean rsaRequired = false;
                rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
                channelUserVO.setRsaRequired(rsaRequired);
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
                LOG.debug("SelfRegistrationControllerController process : ", " channelUserVO.getStatus() = " + channelUserVO.getStatus());
                BTSLMessages btslPushMessage = null;
                String[] arrArray = null;
                final String arr[] = { channelUserVO.getUserName(), "",senderPin };
                String []messageaArguments=null;
                if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                    // send a message to the user about there activation
                    if (locale == null) {
                        locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    }
                    if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                        // changes by hitesh ghanghas
                        if (!BTSLUtil.isNullString(channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
                            if (BTSLUtil.isNullString(webPassword)) {
                                arrArray = new String[] { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", randomPwd, senderPin };
                            } else {
                                arrArray = new String[] { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", webPassword, senderPin };
                            }
                            response.setMessageCode(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE);
                            messageaArguments=arrArray;
                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                            final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                            pushMessage.push();

                        }else {
                            if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                            	response.setMessageCode(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE);
                                if (BTSLUtil.isNullString(webPassword)) {
                                    arrArray = new String[] { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", randomPwd, senderPin };
                                } else {
                                    arrArray = new String[] { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", webPassword, senderPin };
                                }
                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                                messageaArguments=arrArray;
                                response.setMessageCode(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE);
                                final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                                pushMessage.push();

                            } else {
                                arrArray = new String[] { channelUserVO.getMsisdn(), "", senderPin };
                                response.setMessageCode(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE);
                                messageaArguments=arrArray;
                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                                final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                                pushMessage.push();
                            }
                        }
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                            final String subject = BTSLUtil.getMessage(locale, "user.addchanneluser.addsuccessmessage", arr);
                            final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
                                            "Email has ben delivered recently", channelUserVO, channelUserVO);
                            emailSendToUser.sendMail();
                            response.setMessageCode(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE);
                            messageaArguments=arrArray;
                        }
                        
                    } else if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && TypesI.NO.equals(categoryVO.getSmsInterfaceAllowed()) && !BTSLUtil
                        .isNullString(channelUserVO.getLoginID()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
                        // send message for login id
                        response.setMessageCode(PretupsErrorCodesI.USER_WEB_ACTIVATE);
                        if (BTSLUtil.isNullString(webPassword)) {
                            arrArray = new String[] { channelUserVO.getLoginID(), randomPwd };
                        } else {
                            arrArray = new String[] { channelUserVO.getLoginID(), webPassword };
                        }
                        messageaArguments=arrArray;
                        response.setMessageCode(PretupsErrorCodesI.USER_WEB_ACTIVATE);
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                        final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                        pushMessage.push();
                        
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                            final String subject =  BTSLUtil.getMessage(locale, "user.addchanneluser.addsuccessmessage", arr);
                            final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
                                            "Email will be delivered shortly", channelUserVO, channelUserVO);
                            emailSendToUser.sendMail();
                            response.setMessageCode(PretupsErrorCodesI.USER_WEB_ACTIVATE);
                            messageaArguments=arrArray;
                        }
                        
                    }
                    	else {
                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arr);
                            final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                            pushMessage.push();
                            // Email for pin & password- code for email details
                            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                                final String arrOne[] = { channelUserVO.getMsisdn() };
                                final String subject = BTSLUtil.getMessage(locale, "subject.user.regmsidn.massage", arrOne);
                                final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
                                                "Email will be delivered shortly", channelUserVO, channelUserVO);
                                emailSendToUser.sendMail();
                                response.setMessageCode(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE);
                                messageaArguments=arr;
                            }
                        }
                }
                
                else {

                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
                    response.setMessageCode(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
                }
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID()))){
                	if(((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),userCategeryCode)).intValue()==0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()){
                	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
                	tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
                	}
                }
                
                response.setStatus(201);
				response.setMessage(RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.USER_SUCCESS,messageaArguments));
				responseMap.put("RESPONSE", response.getMessage());
				 requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				 requestVO.setResponseMap(responseMap);
				 requestVO.setVomsMessage(response.getMessage());
                // Ended Here
            }
        } catch (BTSLBaseException be) {
        	response.setStatus(400);
        	if(locale ==null)
        	locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
            LOG.errorTrace(methodName, be);
            response.setMessageCode(be.getMessageKey());
			response.setMessage(RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs()));
			responseMap.put("RESPONSE", response.getMessage());
			requestVO.setResponseMap(responseMap);
			requestVO.setMessageCode(response.getMessageCode());
			requestVO.setVomsMessage(response.getMessage());
            
        } catch (Exception e) {
        	response.setStatus(400);
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
            response.setMessage(e.getMessage());
            
        } finally {
            channelUserDao = null;
            channelUserWebDao = null;
            userDAO = null;
            channelUserVO = null;
            if(mcomCon != null){mcomCon.close("SelfRegistrationController#process");mcomCon=null;}
            LogFactory.printLog(methodName, " Exited ", LOG);
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
        if (LOG.isDebugEnabled()) {
        	LOG.debug("generateUserId", "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix);
        }
        final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, p_networkCode)) + "", length);
        id = p_networkCode + p_prefix + id;
        if (LOG.isDebugEnabled()) {
        	LOG.debug("generateUserId", "Exiting id=" + id);
        }
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
    private List prepareUserPhoneVOList(Connection con, Msisdn msisdn1, ChannelUserVO channelUserVO, Date currentDate, HashMap reqMap) throws Exception {
        final String methodName = "prepareUserPhoneVOList";
        LogFactory.printLog(methodName, "Entered channelUserVO.getCategoryCode()=" + channelUserVO.getCategoryCode(), LOG);
    	
        final List <UserPhoneVO>phoneList = new ArrayList<UserPhoneVO>();
        List <Msisdn>msisdnList=new ArrayList();
        NetworkPrefixVO networkPrefixVO = null;
        	msisdnList.add(msisdn1);
        String stkProfile = null;
        final List stkProfileList = userDAO.loadPhoneProfileList(con, channelUserVO.getCategoryCode());
        if (stkProfileList != null && !stkProfileList.isEmpty()) {
            final ListValueVO listValueVO = (ListValueVO) stkProfileList.get(0);
            stkProfile = listValueVO.getValue();
        }
        Msisdn msisdn= null;
        if (msisdnList != null && !msisdnList.isEmpty()) {
            UserPhoneVO phoneVO = null;
            for (int i = 0, j = msisdnList.size(); i < j; i++) {
                msisdn = (Msisdn) msisdnList.get(i);
                if (!BTSLUtil.isNullString(msisdn.getPhoneNo())) {
                    phoneVO = new UserPhoneVO();
                    phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(msisdn.getPhoneNo()));
                    phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                    phoneVO.setUserId(channelUserVO.getUserID());
                    phoneVO.setSmsPin(BTSLUtil.encryptText(msisdn.getPin()));
                    phoneVO.setPinRequired(PretupsI.YES);
                    phoneVO.setPinReset(PretupsI.NO);
                    //phoneVO.setImei(String.valueOf(reqMap.get("IMEI")));
                    //phoneVO.setMhash(String.valueOf(reqMap.get("MHASH")));
                    // set the default values
                    phoneVO.setCreatedBy(channelUserVO.getUserID());
                    phoneVO.setModifiedBy(channelUserVO.getUserID());
                    phoneVO.setCreatedOn(currentDate);
                    phoneVO.setModifiedOn(currentDate);
                    phoneVO.setPinModifiedOn(currentDate);
                    ArrayList languageList=LocaleMasterDAO.loadLocaleMasterData();
                    boolean flag1=true;
                    for(int k=0;k<languageList.size();k++)
                    {
                    	if(((ListValueVO)languageList.get(k)).getValue().equals(Constants.getProperty("LANGUAGECODE")))
                    	{
                    		flag1=false;
                    	}
                    }
                    if(flag1==true)
                    {
                    	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.LANGUAGE_DOES_NOT_EXIST);
                    }
                    if (!BTSLUtil.isNullString((Constants.getProperty("LANGUAGECODE")))) {
                        final String lang_country[] = (Constants.getProperty("LANGUAGECODE")).split("_");
                        phoneVO.setPhoneLanguage(lang_country[0]);
                        phoneVO.setCountry(lang_country[1]);
                    } else {
                        phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                        phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    }
                    if(msisdn.getStkProfile()==null)
                    {
                    	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_FOUND);
                    }
                    else if(!(msisdn.getStkProfile().equals(stkProfile)))
                    {
                    	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_VALID);
                    }
                    phoneVO.setPhoneProfile(msisdn.getStkProfile());
                    phoneVO.setDescription(msisdn.getDescription());
                    if(!(BTSLUtil.isValidMSISDN(phoneVO.getMsisdn())))
                    {
                    	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
                    }
                    if(!(BTSLUtil.isValidMSISDNDigit(phoneVO.getMsisdn())))
                    {
                    	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
                    }
                    if(!(BTSLUtil.isValidMSISDNLength(phoneVO.getMsisdn())))
                    {
                    	throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.EXTSYS_MSISDN_MAP_LENGTH_EXCEEDS);
                    }
                    final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL
                        .getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
                    if((prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserVO.getNetworkCode()))) {
                        throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
                    }
                    phoneVO.setPrefixID(prefixVO.getPrefixID());
                    if (msisdn.getIsprimary().equals("Y")){
                        channelUserVO.setMsisdn(msisdn.getPhoneNo());
                        phoneVO.setPrimaryNumber(PretupsI.YES);
                    } else {
                        phoneVO.setPrimaryNumber(PretupsI.NO);
                    }

                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(msisdn.getPhoneNo()));
                        boolean numberAllowed = false;
                        if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn.getPhoneNo(), "", PretupsI.PORTED_IN);
                            if (!numberAllowed) {
                                throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_IN);
                            }
                        } else {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn.getPhoneNo(), "", PretupsI.PORTED_OUT);
                            if (numberAllowed) {
                                throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_OUT);
                            }
                        }
                    }
                    // MNP Code End
                    
                    phoneVO.setPinModifyFlag(true);
                    if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                        final String[] arr = { phoneVO.getMsisdn() };
                        throw new BTSLBaseException("SelfRegistrationController", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                    }
                    phoneList.add(phoneVO);
                }
            }
        }
        channelUserVO.setMsisdnList((ArrayList)phoneList);
        return phoneList;
    }

	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
