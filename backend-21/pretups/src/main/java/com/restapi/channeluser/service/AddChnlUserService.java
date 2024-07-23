package com.restapi.channeluser.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/*import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;*/
import org.springframework.scheduling.annotation.Async;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channeluser.businesslogic.ChannelUserApprovalReqVO;
import com.btsl.pretups.channeluser.businesslogic.Msisdn;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryReqGtwTypeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.logging.OperatorUserLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
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
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.NumberConstants;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.DateUtils;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.txn.pretups.master.businesslogic.DivisionDeptTxnDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

import oracle.jdbc.proxy.annotation.Pre;


//@Component("AddChnlUserService")
//@RequestScope
public class AddChnlUserService {
	public static final Log LOG = LogFactory.getLog(AddChnlUserService.class.getName());

	private String primaryMsisdn;
    private String senderPin;
    private OperatorUtilI operatorUtili;
    private UserDAO userDAO = new UserDAO();
    private AddChannelOperatorService addoptService = new AddChannelOperatorService();
    private ChannelUserDAO channelUserDao = new ChannelUserDAO();
    private ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
    private UserRolesDAO userRolesDAO = new UserRolesDAO();
    private ChannelUserVO channelUserVO = new ChannelUserVO();
    private ChannelUserVO existingDBchannelUserVO = new ChannelUserVO();
	private List userPhoneList=null;
	private ArrayList requestuserPhoneList=new ArrayList<String>();  // from UI
	private ArrayList userOldPhoneList=null;
    private UserPhoneVO phoneVO = new UserPhoneVO();
    private UserWebDAO userWebDAO = new UserWebDAO();
    private Locale locale = null;
    private  String randomPin;    private  String  randomPwd;
    private  String webPassword = null;
    private BaseResponse response = new BaseResponse();
    private CommonUtil commonUtil = new CommonUtil();
    private  String modifedLoginId; 
    private String existingLoginId;
    private boolean isWebPasswordChanged;
    private HashMap<String,String> requestGroupHashMap;
    
    
    public ChannelUserVO getChannelUserVO() {
		return channelUserVO;
	}

	public void setChannelUserVO(ChannelUserVO channelUserVO) {
		this.channelUserVO = channelUserVO;
	}

    
	public String getSenderPin() {
		return senderPin;
	}

	public void setSenderPin(String senderPin) {
		this.senderPin = senderPin;
	}

	public String getPrimaryMsisdn() {
		return primaryMsisdn;
	}

	public void setPrimaryMsisdn(String primaryMsisdn) {
		this.primaryMsisdn = primaryMsisdn;
	}

	public BaseResponse execute(ChannelUserApprovalReqVO requestVO, ChannelUserVO senderVO, Connection con)
			throws BTSLBaseException, SQLException {
		final String methodName = "basicValidations";
		convertReqGroupRoleToMap(requestVO); // Calling in Async;
		String[] arrArray = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		this.setPrimaryMsisdn(requestVO.getData().getMobileNumber());
		 locale = new Locale(lang, country);
		String webLoginID =requestVO.getData().getWebloginid();
//			UserVO userVO  = 	userDAO.loadUserDetailsByLoginId(con, webLoginID);
		UserVO userVO = userDAO.loadUsersDetailsfromLoginID(con,webLoginID);
		if (!senderVO.getCategoryCode().equals (PretupsI.PWD_USER_SUADM) && !senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM)) {
			if (!BTSLUtil.isNullString(requestVO.getData().getContactNumber())) {
				requestVO.getData().setMobileNumber(requestVO.getData().getContactNumber());
			}
		}
		if(requestVO.getApprovalLevel().equals(PretupsI.USER_ACTION_REJECT)) {

			 if(userVO==null) {
				 throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.INVALID_USER_LOGINID);
			 }
			
		
			
			boolean rejectSuccess=handleRejectApprovals(con,requestVO,userVO);
			arrArray=new String[]  {userVO.getUserName()};
			if(rejectSuccess) {
				response.setStatus(200);
				response.setMessageCode(PretupsErrorCodesI.USER_REJECTED_SUCCESSFULLY);
				response.setMessage(RestAPIStringParser.getMessage(locale,response.getMessageCode(),arrArray));
				try {
					con.commit();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
			        throw new BTSLBaseException(this, "addUserInfo", "error.general.processing");
				}
				
			}else {
				response.setStatus(400);
				response.setMessageCode(PretupsErrorCodesI.USER_REJECTION_FAILED);
				response.setMessage(RestAPIStringParser.getMessage(locale,response.getMessageCode(),arrArray));
			
			}
			   
			return response;
		}
		
				
		try {
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					methodName, "", "", "", "Exception while loading the class at the call:" + e.getMessage());
		}
		
		
		
		CategoryVO categoryVO = null;
		
		OwnerParentInfoVO ownerParentInfoVO = null;
		if(!"EDIT".equalsIgnoreCase(requestVO.getUserAction())) {
			if (!requestVO.getData().getWebpassword().equals(requestVO.getData().getConfirmwebpassword())) {
				throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.PASSWORD_CONFRIMPASS_NOT_SAME);
			}
		}
		
	
		if (BTSLUtil.isNullString(requestVO.getData().getDomain())) {
			throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.NO_DOMAIN_FOUND);
		}
		if (senderVO.getUserType().equals(PretupsI.USER_TYPE_CHANNEL)) {
			requestVO.getData().setDomain(senderVO.getDomainID());
		}
		
		if(requestVO.getData().getUserCatCode()!=null) { // ex :if user category is DIST, then parent category validation not required
			
			 List catList =null;
			
		if (senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM) || senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM) ) {
			
				catList = new CategoryDAO().loadCategoryDetailsOPTCategoryCode(con,
						requestVO.getData().getUserCatCode());
			}else {
			 catList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con,
					requestVO.getData().getUserCatCode());
			}
			
		
		if (requestVO.getData().getShortName()!=null && requestVO.getData().getShortName().trim().length()>15)  {
			throw new BTSLBaseException("AddChannelUser", methodName,
					PretupsErrorCodesI.SHORT_NAME_EXCEEED_15CHARS);
		}
			
			if (catList == null || catList.isEmpty()) {
				throw new BTSLBaseException("AddChannelUser", methodName,
						PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
			} else {
					categoryVO = (CategoryVO) catList.get(0);
					if(categoryVO.getSequenceNumber()>NumberConstants.ONE.getIntValue()  && !PretupsI.OPERATOR_USER_TYPE.equalsIgnoreCase(categoryVO.getDomainTypeCode())) {
						ownerParentInfoVO = checkParentCategoryValidation(requestVO, categoryVO, senderVO, con);
					}
		    	}
		
			
			if(!requestVO.getApprovalLevel().equals(PretupsI.NEW) ) {
			if(categoryVO.getModifyAllowed()!=null && categoryVO.getModifyAllowed().equals(PretupsI.NO)) {
				//existingDBchannelUserVO = 	channelUserDao.loadAPPRChnlUserDetailsByLoginID(con, requestVO.getData().getWebloginid());
				existingDBchannelUserVO =  (ChannelUserVO) userDAO.loadUsersDetailsfromLoginID(con,requestVO.getData().getWebloginid());
				checkApprovalReqValidation(requestVO,existingDBchannelUserVO);
				checkApprovalLevel(requestVO, categoryVO, senderVO, con); //set channeluserVO.
				existingDBchannelUserVO.setStatus(channelUserVO.getStatus());
				existingDBchannelUserVO.setPreviousStatus(channelUserVO.getPreviousStatus());
				existingDBchannelUserVO.setLevel1ApprovedBy(channelUserVO.getLevel1ApprovedBy());
				existingDBchannelUserVO.setLevel1ApprovedOn(channelUserVO.getLevel1ApprovedOn());
				existingDBchannelUserVO.setLevel2ApprovedBy(channelUserVO.getLevel2ApprovedBy());
				existingDBchannelUserVO.setLevel2ApprovedOn(channelUserVO.getLevel2ApprovedOn());
				channelUserVO.setUserName(existingDBchannelUserVO.getUserName()); // for mail usrname...
				existingDBchannelUserVO.setAuthTypeAllowed(requestVO.getData().getAuthTypeAllowed());
				modifyUser(con,existingDBchannelUserVO);
				  try {
						con.commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
				        throw new BTSLBaseException(this, "addUserInfo", "error.general.processing");
					}
				 response.setStatus(200);
				 mailSendandFinalResponseMessage(con, requestVO ,senderVO,categoryVO);
				    return response;
			}
			}
			
			
			
		
			int index = 1;
			 if(!(requestVO.getData().getUserCatCode().equals(PretupsI.CUSTOMER_CARE))) {
				if(categoryVO.getSmsInterfaceAllowed()!=null && !categoryVO.getSmsInterfaceAllowed().equalsIgnoreCase(PretupsI.YES) &&  
						(requestVO.getData().getMsisdn()!=null && requestVO.getData().getMsisdn().length>0)
						) {
				    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MSISDN_NOT_ALLOWED_FOR_CAT);
				}
			 }
			channelUserVO.setMsisdn(BTSLUtil.NullToString(requestVO.getData().getMobileNumber()));
			if (PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())) {	
				if(categoryVO.getSmsInterfaceAllowed()!=null && categoryVO.getSmsInterfaceAllowed().equalsIgnoreCase(PretupsI.YES)) {
					if(requestVO.getData().getMsisdn()!=null && requestVO.getData().getMsisdn().length==0) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ASSIGN_MSISDN_MANDATAORY);
					}
				}else  {
					if(BTSLUtil.isNullString(requestVO.getData().getMobileNumber())) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MOBILE_NUMB_REQUIRED);	
					}
					channelUserVO.setMsisdn(BTSLUtil.NullToString(requestVO.getData().getMobileNumber()));
				}
				}
				
			channelUserVO.setMsisdn(BTSLUtil.NullToString(requestVO.getData().getMobileNumber()));
			if(!(requestVO.getData().getUserCatCode().equals(PretupsI.CUSTOMER_CARE))) {
				for (Msisdn obj : requestVO.getData().getMsisdn()) {
					 if(obj.getIsprimary()!=null && obj.getIsprimary().equalsIgnoreCase("Y"))
					 {
						 channelUserVO.setMsisdn(obj.getPhoneNo());
					 }
					if (!BTSLUtil.isNullObject(obj.getPin()))
						if (!obj.getPin().equals(obj.getConfirmPin())) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PIN_NOT_SAME,
									new String[]{index + ""});
						}
					index++;
				}
			}
			
		}	
		
		

			channelUserVO.setCategoryVO(categoryVO);
			channelUserVO.setCategoryCode(categoryVO.getCategoryCode());
			channelUserVO.setCategoryName(categoryVO.getCategoryName());
			channelUserVO.setDomainTypeCode(categoryVO.getDomainTypeCode());
		    existingDBchannelUserVO.setAuthTypeAllowed(requestVO.getData().getAuthTypeAllowed());
			channelUserVO.setAuthTypeAllowed(requestVO.getData().getAuthTypeAllowed());


		checkOutletsAllowed(requestVO, categoryVO, senderVO, con);
		checkPersonalDetailsValidation( requestVO,categoryVO,
				senderVO, con);
		
			checkCredentials(requestVO, categoryVO, senderVO, con);
		
		checkNetwork(requestVO, categoryVO, senderVO, con);
		checkEmail(requestVO, categoryVO, senderVO, con);
		setparentOwnerIDs(requestVO, categoryVO, senderVO, con, ownerParentInfoVO);
	if (senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM) || senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM) ) {    
		addoptService.addOperatorGeographies(requestVO, categoryVO, senderVO, con, channelUserVO);
		}else {
		     // Not operator  			
			checkGeographyValidation(requestVO, categoryVO, senderVO, con,  ownerParentInfoVO,userVO);
			
					}

		checkApprovalLevel(requestVO, categoryVO, senderVO, con);
		setaddress(requestVO, categoryVO, senderVO, con, ownerParentInfoVO);
		checkDocumentData(requestVO, categoryVO, senderVO, con,  ownerParentInfoVO);
	    if (!senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM)|| !senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM))  {		
			checkPaymentData(requestVO, categoryVO, senderVO, con,  ownerParentInfoVO);
			checkLowBalAlertAllow(requestVO, categoryVO, senderVO, con,  ownerParentInfoVO);
		}
		checkAllowedLoginTimes(requestVO, categoryVO, senderVO, con,  ownerParentInfoVO);
		
		if(senderVO.getDomainID().equals(PretupsI.DOMAIN_TYPE_OPT) &&  !PretupsI.OPERATOR_CATEGORY.equals(senderVO.getCategoryCode()) ) {
			channelUserVO.setUserType(PretupsI.OPERATOR_USER_TYPE);
			channelUserVO.setDepartmentCode(requestVO.getData().getDepartmentCode());
			channelUserVO.setDivisionCode(requestVO.getData().getDivisionCode());
			validateDivisionCode(con,requestVO.getData().getDivisionCode());
			validateDepartmentCode(con, requestVO.getData().getDepartmentCode(), requestVO.getData().getDivisionCode());
			Date appointmentDate=null;
			if((requestVO.getData().getUserCatCode().equals(PretupsI.CUSTOMER_CARE))) {
				channelUserVO.setMsisdn(requestVO.getData().getMobileNumber());
			}
			if(requestVO.getData().getAppointmentdate()!=null && requestVO.getData().getAppointmentdate().trim().length()>0) {
			try {
				
				channelUserVO.setAppointmentDate(BTSLUtil.getDateFromDateString(requestVO.getData().getAppointmentdate()));
				appointmentDate=BTSLUtil.getDateFromDateString(requestVO.getData().getAppointmentdate());
				//isBeforeDay
	              final Date currentDate = new Date();
	              if(appointmentDate!=null){
		              if (DateUtils.isBeforeDay(appointmentDate,currentDate)) {
		            	  throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.APPOINTMENTDATE_LESS_CURRDATE);
		              }   
	              }
			} catch (ParseException e) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_APPOINTMENT_DATE);
			}
			}
			
		}
		
		if(requestVO.getApprovalLevel().equals(PretupsI.NEW) && requestVO.getUserAction().equals(PretupsI.OPERATION_ADD)) {
			if(BTSLUtil.isNullString(channelUserVO.getMsisdn()) ){
				channelUserVO.setMsisdn(requestVO.getData().getMobileNumber());
			}
	 		addUser(con);
	      }else {
	       modifyUser(con); // if modification during approval level 1/2/3. 	  
	      }
        /*final ArrayList phoneList = new ArrayList();*/
        //

		
        
		if(categoryVO.getSmsInterfaceAllowed()!=null && categoryVO.getSmsInterfaceAllowed().equalsIgnoreCase(PretupsI.YES)   && (requestVO.getData().getMsisdn()!=null && requestVO.getData().getMsisdn().length>0) ) {
			   // BCU category also
	        if(requestVO.getApprovalLevel().equals(PretupsI.NEW) && requestVO.getUserAction().equals(PretupsI.OPERATION_ADD)) {
	        	addUserPhoneList(requestVO,con,senderVO);
	        }else { //  APPRV1/APPRV2/APPRV3/EDIT
	        	userOldPhoneList = userDAO.loadUserPhoneList(con, existingDBchannelUserVO.getUserID());
	        	userPhoneList = prepareUserModifiedPhoneVOList(con, requestVO, channelUserVO,senderVO, userOldPhoneList, senderPin);
	        	addmodifiedPhoneList(con,userPhoneList);
	        }
			channelUserVO.setMsisdn(BTSLUtil.NullToString(primaryMsisdn));
		}
//		if( (channelUserVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY) || channelUserVO.getCategoryCode().equals(PretupsI.SUPER_CHANNEL_ADMIN) )&& requestVO.getUserAction().equals(PretupsI.OPERATION_ADD)) {
//			if(requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
//	        	addUserPhoneList(requestVO,con,senderVO);
//	        }
//		}
//		else if(channelUserVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY) && requestVO.getUserAction().equals(PretupsI.OPERATION_EDIT))
//		
//		{
//			userOldPhoneList = userDAO.loadUserPhoneList(con, existingDBchannelUserVO.getUserID());
//        	userPhoneList = prepareUserModifiedPhoneVOList(con, requestVO, channelUserVO,senderVO, userOldPhoneList, senderPin);
//        	addmodifiedPhoneList(con,userPhoneList);
//		}
	        
        
        
        
        
        
        final Date currentDate = new Date();
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
        checkUserSuspend(requestVO,con,senderVO);
        
		int sysPrefapprvLevel = ((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,
				senderVO.getNetworkID(), requestVO.getData().getUserCatCode()));
		if (!senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM)&& !senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM))  {
			if (sysPrefapprvLevel == 0 && requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
				checkCommissionProfile(requestVO, con, senderVO);
			} else if (sysPrefapprvLevel > 0) {
				if (!requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
					checkCommissionProfile(requestVO, con, senderVO);
				}
			}
		} 
        
        channelUserVO.setMcommerceServiceAllow(PretupsI.NO);
        channelUserVO.setMpayProfileID("");
        if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
        	channelUserVO.setControlGroup(PretupsI.NO);
        }
	  //Single Association with control
	    if (LOG.isDebugEnabled()){
	 		LOG.debug(methodName,"theForm.getLmsProfileId() = "+requestVO.getData().getLmsProfileId()+", channelUserVO.getLmsProfile() = "+channelUserVO.getLmsProfile());
	 	}
	    
	    if  (!(senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM)|| senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM)))  {
		    checkLMSProfile(con);
		    if(categoryVO.getServiceAllowed()!=null && categoryVO.getServiceAllowed().equalsIgnoreCase(PretupsI.YES)) {
		    	checkServiceList(requestVO,con);
		    }
	    }
	    
	    if  (senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM) || senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM) ){
		    if(requestVO.getData().getUserCatCode()!=null  ) {
		    	if(categoryVO.getServiceAllowed()!=null && categoryVO.getServiceAllowed().equalsIgnoreCase(PretupsI.YES)) {
		    		checkSuperadminServiceList(requestVO,con,senderVO);
		    	}
		    }
	    }
	    
	    
	    
	    checkGroupRole(requestVO,con,categoryVO);
		
		///////
		
		// ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
        ArrayList commissionProfileList=userWebDAO.loadCommisionProfileListByCategoryIDandGeography(con, channelUserVO.getCategoryCode(), senderVO.getNetworkID(),null);

        final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
        // load the User Grade dropdown
        ArrayList gradelist=categoryGradeDAO.loadGradeList(con, channelUserVO.getCategoryCode());

        // load the Transfer Profile dropdown
        final TransferProfileDAO profileDAO = new TransferProfileDAO();
        ArrayList transferprofilelist=profileDAO.loadTransferProfileByCategoryID(con, senderVO.getNetworkID(), channelUserVO.getCategoryCode(),
                        PretupsI.PARENT_PROFILE_ID_USER);
        // load the Transfer Rule Type at User level
        ArrayList transferRuleTypeList =null;
        final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, senderVO.getNetworkID(), channelUserVO.getCategoryCode())).booleanValue();
        if (isTrfRuleTypeAllow) {
        	transferRuleTypeList=(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
        }
        
        ArrayList LmsProfileList=null;
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
            LmsProfileList=channelUserWebDAO.getLmsProfileList(con, senderVO.getNetworkID());
        }
        UserRolesDAO userRolesDAO1 = new UserRolesDAO();
        boolean flag = true;
        String userCatCode = channelUserVO.getCategoryCode();
        userCatCode = userCatCode.trim();
		int userLevel = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_APPROVAL_LEVEL);
		Boolean approverCanEdit = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT);
		boolean checkUserGrade;
		if (!approverCanEdit && userLevel != 0) {
			checkUserGrade = true;
		} else {
			checkUserGrade = false;
		}


        	 String userGrade = requestVO.getData().getUsergrade();
		if (checkUserGrade) {
			if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_NEW)) {
				if (!BTSLUtil.isNullString(userGrade)) {
					String userGradeCode = null;
					GradeVO gradeVO = new GradeVO();
					List userGradeList = new CategoryGradeDAO().loadGradeList(con, userCatCode);
					List<String> gradeCodeList = new ArrayList<String>();
					int userGradeLists = userGradeList.size();
					for (int i = 0; i < userGradeLists; i++) {
						gradeVO = (GradeVO) userGradeList.get(i);
						gradeCodeList.add(gradeVO.getGradeCode());
					}

					if (gradeCodeList.contains(userGrade)) {
						userGradeCode = userGrade.trim();
						channelUserVO.setUserGrade(userGradeCode);
						senderVO.setUserGrade(userGradeCode);
					} else if (!gradeCodeList.contains(userGrade)) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GRADE_INVALID);
					}
				} else {

					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GRADE_INVALID);
				}

				String commissionProfileID = requestVO.getData().getCommissionProfileID();
				if (!BTSLUtil.isNullString(commissionProfileID)) {

					flag = true;
					for (int i = 0; i < commissionProfileList.size(); i++) {
						if (((CommissionProfileSetVO) commissionProfileList.get(i)).getCommProfileSetId().equals(commissionProfileID)) {
							flag = false;
						}
					}
					if (flag == true) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMMISSION_SET_ID_INVALID);
					}

					channelUserVO.setCommissionProfileSetID(commissionProfileID);
					senderVO.setCommissionProfileSetID(commissionProfileID);
				} else {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMMISSION_SET_ID_INVALID);
				}

				String transferProfile = requestVO.getData().getTransferProfile();
				if (!BTSLUtil.isNullString(transferProfile)) {
					flag = true;
					for (int i = 0; i < transferprofilelist.size(); i++) {
						if (((ListValueVO) transferprofilelist.get(i)).getValue().equals(transferProfile)) {
							flag = false;
						}
					}
					if (flag == true) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_TRANSFER_PROFILE_NOT_EXIST, new String[]{channelUserVO.getUserName()});
					}

					channelUserVO.setTransferProfileID(transferProfile);
				} else {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_TRANSFER_PROFILE_NOT_EXIST, new String[]{channelUserVO.getUserName()});
				}

				String transferRuleType = requestVO.getData().getTransferRuleType();
				if (isTrfRuleTypeAllow) {
					if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
						if (!BTSLUtil.isNullString(transferRuleType)) {
							flag = true;
							for (int i = 0; i < transferRuleTypeList.size(); i++) {
								if (((ListValueVO) transferRuleTypeList.get(i)).getValue().equals(transferRuleType)) {
									flag = false;
								}
							}
							if (flag == true) {
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_TYPE_NOT_EXIST);
							}

							channelUserVO.setTrannferRuleTypeId(transferRuleType);
							senderVO.setTrannferRuleTypeId(transferRuleType);
						} else {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_TYPE_NOT_EXIST);
						}
					}
				}
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {

					String controlGroupRequired = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
					if (controlGroupRequired == null || controlGroupRequired == "") {
						controlGroupRequired = "Y";
					}
					if ("Y".equals(controlGroupRequired)) {
						channelUserVO.setControlGroup(requestVO.getData().getControlGroup());
						senderVO.setControlGroup(requestVO.getData().getControlGroup());
					}
					if (BTSLUtil.isNullString(requestVO.getData().getLmsProfileId())) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LMS_SETID_NOT_FOUND);
					} else {

						flag = true;
						for (int i = 0; i < LmsProfileList.size(); i++) {
							if (((ListValueVO) LmsProfileList.get(i)).getValue().equals(requestVO.getData().getLmsProfileId())) {
								flag = false;
							}
						}
						if (flag == true) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LMS_SETID_NOT_FOUND);
						}

					}
					channelUserVO.setLmsProfile(requestVO.getData().getLmsProfileId());
					senderVO.setLmsProfile(requestVO.getData().getLmsProfileId());
				}

			} else {
				String transferRuleType = requestVO.getData().getTransferRuleType();
				channelUserVO.setTrannferRuleTypeId(transferRuleType);
				senderVO.setTrannferRuleTypeId(transferRuleType);
			}
		}

	    

		
		
		
		
		
		
		
		///////
	    
	    if (!senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM) &&  !senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM)) {
	     if(PretupsI.NEW.equals(requestVO.getApprovalLevel()) && requestVO.getUserAction().equals(PretupsI.OPERATION_ADD)) {
	    	// insert data into channel users table
	    		 addChannelUser(con,senderVO);
	    	 
	     } else {
	            if (((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(), requestVO.getData().getUserCatCode()))
	                    .intValue() == 0 || 
	                   !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()||(!(((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(), requestVO.getData().getUserCatCode())).intValue() == 0 || 
	                   !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) && userRolesDAO.isUserRoleCodeAssociated(con,senderVO.getUserID(),"ASSCUSR"))) {
	            	int updateChannelCount = channelUserWebDAO.updateChannelUserApprovalInfo(con, channelUserVO);

	        if (updateChannelCount <= 0) {
	            con.rollback();
	            LOG.error(methodName, "Error: while Updating Channel User For Approval One");
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }
	        }else{
	           // final int userChannelCount = channelUserDao.updateChannelUserInfo(con, channelUserVO);
	        	if(!(requestVO.getData().getUserCatCode().equals(PretupsI.CUSTOMER_CARE) || requestVO.getData().getUserCatCode().equals(PretupsI.BCU_USER)) ) {
	        	 final int userChannelCount = channelUserWebDAO.updateChannelUserApprovalInfo(con, channelUserVO);
	            if (userChannelCount <= 0) {
	                con.rollback();
	                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
	                    "Exception:Update count <=0 ");
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
	            }
	    }}
	    	 
	     }
	    }
	    
	    
	    	if(categoryVO.getProductTypeAllowed()!=null && categoryVO.getProductTypeAllowed().equalsIgnoreCase(PretupsI.YES)) {
	    		checkUserProducts(con, requestVO);
	    	}
	    	if(categoryVO.getDomainAllowed()!=null && categoryVO.getDomainAllowed().equalsIgnoreCase(PretupsI.YES)   && PretupsI.DOMAINS_ASSIGNED.equals(categoryVO.getFixedDomains()) ) {
	    		checkUserDomainCodes(con, requestVO);
	    	}
		
	    
	    if(requestVO.getData().getUserCatCode().equalsIgnoreCase(PretupsI.CATEGORY_CODE_NETWORK_ADMIN) ||
	    		requestVO.getData().getUserCatCode().equalsIgnoreCase(TypesI.SUPER_NETWORK_ADMIN) ||
	    		requestVO.getData().getUserCatCode().equalsIgnoreCase(TypesI.SUB_SUPER_ADMIN)
	    		
	    		 ) {
	    checkVoucherSegments(con, requestVO);
	    }
	    
	    checkVoucherType(con, requestVO);
	    
	    
	    
	    if (!senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM) || !senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM) ) {
	    	checkSMSorEmailMessaging(con, requestVO,categoryVO,senderVO);	
	    }else {
	    	checkSMSorEmailMessagingForOPTS(con, requestVO,categoryVO,senderVO);
	    }
	    
	    if(requestVO.getApprovalLevel().equals(PretupsI.NEW) && requestVO.getUserAction().equals(PretupsI.OPERATION_EDIT)) {
	    	String arr[] = { channelUserVO.getUserName() };
	    	response.setMessageCode(PretupsErrorCodesI.OPERATOR_SUCCESSFULY_MODIFIED);
	 		response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),arr));
	    }
	    else {
	    if (PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())  &&  !PretupsI.OPERATOR_CATEGORY.equals(senderVO.getCategoryCode()) ) {
	    
	    String arr[] = { channelUserVO.getUserName() };
        // added for operator user approval

        if (((Integer) PreferenceCache.getControlPreference(PreferenceI.OPT_USR_APRL_LEVEL, channelUserVO.getNetworkID(), channelUserVO.getCategoryCode())).intValue() > 0) {
          // legacy MESSAGE  btslMessage = new BTSLMessages("user.addoperatoruser.addsuccessmessageforrequest", arr, "AddSuccess");
        	response.setMessageCode(PretupsErrorCodesI.OPERATOR_SUCCESSFULY_ADDED);
	 		response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),arr));
            
        } else {
         // legacy message           btslMessage = new BTSLMessages("user.addoperatoruser.addsuccessmessage", arr, "AddSuccess");
            response.setMessageCode(PretupsErrorCodesI.USER_SUCCESSFULY_ADDED);
	 		response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),arr));
        }
	    
	    }else {
	    
	    
	    int maxApprovalLevel =((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(),
				requestVO.getData().getUserCatCode())).intValue();
	    String[] messageArr = null;
	    if(maxApprovalLevel==0){
	    	messageArr = new String[] { channelUserVO.getUserName()};
	    	response.setMessageCode(PretupsErrorCodesI.USER_SUCCESSFULLY_ADDED);
	 		response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));
	     }else {
	    	 if(requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
	    		 CommonUtil commonUtil = new CommonUtil();
	 	    	messageArr = new String[] { channelUserVO.getUserName(),commonUtil.getNumberWord(maxApprovalLevel) };
		    	 response.setMessageCode(PretupsErrorCodesI.USER_ADDED_APPR_PENDING);
			 	 response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));
	    	 } 
	    	 
	     }
	    
	    }
	}
	    
	    
	    
	    
	    
	    
	    
	    
	     
	    
	     response.setStatus(200);
	    return response;

	}
	
	
	
	private void mailSendandFinalResponseMessage(Connection con, ChannelUserApprovalReqVO requestVO ,ChannelUserVO senderVO,CategoryVO categoryVO) throws BTSLBaseException {
		
		if (!senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM) || !senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM) ) {
	    	checkSMSorEmailMessaging(con, requestVO,categoryVO,senderVO);	
	    }else {
	    	checkSMSorEmailMessagingForOPTS(con, requestVO,categoryVO,senderVO);
	    }
	    
	    if(requestVO.getApprovalLevel().equals(PretupsI.NEW) && requestVO.getUserAction().equals(PretupsI.OPERATION_EDIT)) {
	    	String arr[] = { channelUserVO.getUserName() };
	    	response.setMessageCode(PretupsErrorCodesI.OPERATOR_SUCCESSFULY_MODIFIED);
	 		response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),arr));
	    }
	    else {
	    if (PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())  &&  !PretupsI.OPERATOR_CATEGORY.equals(senderVO.getCategoryCode()) ) {
	    
	    String arr[] = { channelUserVO.getUserName() };
        // added for operator user approval

        if (((Integer) PreferenceCache.getControlPreference(PreferenceI.OPT_USR_APRL_LEVEL, channelUserVO.getNetworkID(), channelUserVO.getCategoryCode())).intValue() > 0) {
          // legacy MESSAGE  btslMessage = new BTSLMessages("user.addoperatoruser.addsuccessmessageforrequest", arr, "AddSuccess");
        	response.setMessageCode(PretupsErrorCodesI.OPERATOR_SUCCESSFULY_ADDED);
	 		response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),arr));
            
        } else {
         // legacy message           btslMessage = new BTSLMessages("user.addoperatoruser.addsuccessmessage", arr, "AddSuccess");
            response.setMessageCode(PretupsErrorCodesI.USER_SUCCESSFULY_ADDED);
	 		response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),arr));
        }
	    
	    }else {
	    int maxApprovalLevel =((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(),
				requestVO.getData().getUserCatCode())).intValue();
	    String[] messageArr = null;
	    if(maxApprovalLevel==0){
	    	messageArr = new String[] { channelUserVO.getUserName()};
	    	response.setMessageCode(PretupsErrorCodesI.USER_SUCCESSFULLY_ADDED);
	 		response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));
	     }else {
	    	 if(requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
	    		 CommonUtil commonUtil = new CommonUtil();
	 	    	messageArr = new String[] { channelUserVO.getUserName(),commonUtil.getNumberWord(maxApprovalLevel) };
		    	 response.setMessageCode(PretupsErrorCodesI.USER_ADDED_APPR_PENDING);
			 	 response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));
	    	 } 
	    	 
	     }
	    }
	    }

		
	}
	
	
	
	
	
	
	private boolean handleRejectApprovals(Connection con, ChannelUserApprovalReqVO requestVO,UserVO userVO ) throws BTSLBaseException {
		boolean rejectSuccess=false;
		final String methodName ="handleRejectApprovals";
		
		final Date currentDate = new Date();
		
		 if(userVO.getStatus().equals(PretupsI.USER_STATUS_CANCELED)) {
			 throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.USER_IN_CANCELLED_STATUS);
		 }
//		 if(!userVO.getStatus().equals(PretupsI.USER_STATUS_NEW)) {
//			 throw new BTSLBaseException("AddChannelUser", methodName,
//						PretupsErrorCodesI.APPROVAL_NOT_ALLOWED);
//		 }
		 userVO.setStatus(PretupsI.USER_STATUS_CANCELED);
		 userVO.setLoginID(userVO.getUserID());
		 userVO.setModifiedOn(currentDate);
		 final int userCount = userDAO.updateUser(con, userVO);
		 
		 if(userCount>0) {
			 rejectSuccess=true;
		 }
		 
		 return rejectSuccess;
	}
	
	private void checkSMSorEmailMessaging(Connection con, ChannelUserApprovalReqVO requestVO,CategoryVO categoryVO,ChannelUserVO senderVO ) throws BTSLBaseException {
		if(requestVO.getUserAction().equals(PretupsI.OPERATION_EDIT)) {
			smsOrEmailMessageforEdit(con, requestVO, categoryVO, channelUserVO); //modify user
		}else if(!requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
			smsEmailMessageforApproval(con, requestVO, categoryVO, senderVO); // ALL approval screnarios 
		}else if (requestVO.getApprovalLevel().equalsIgnoreCase(PretupsI.NEW)) {
			smsEmailMessageProcessingforCUs(con, requestVO, categoryVO, senderVO); //Add user
		}
		    
		
	}
	
	private void addmodifiedPhoneList(Connection con,List userPhoneList) throws BTSLBaseException {
		final String methodName ="addmodifiedPhoneList";
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
	}

	private OwnerParentInfoVO checkParentCategoryValidation(ChannelUserApprovalReqVO requestVO,
			CategoryVO categoryVO, ChannelUserVO senderVO, Connection con) throws BTSLBaseException {

		final String methodName = "checkParentCategoryValidation";
		final List parentList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con,
				requestVO.getData().getParentCategory());
		OwnerParentInfoVO ownerParentInfoVO = new OwnerParentInfoVO();
		CategoryVO parentcategoryVO = null;
		if (parentList == null || parentList.isEmpty()) {
			throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.PARENT_CATEGORY_NOT_GIVEN);
		} else {
			// checking parent category wrt user category
			parentcategoryVO = (CategoryVO) parentList.get(0);
			List<CategoryVO> checkCatList = checkTransferRule(con, senderVO, categoryVO,
					requestVO.getData().getDomain());

			boolean parentTrfCategoryNotFound = false;
			for (CategoryVO catgVO : checkCatList) {
				if (catgVO.getCategoryCode().equals(parentcategoryVO.getCategoryCode())) {
					parentTrfCategoryNotFound = true;
					break;
				}
			}

			if (!parentTrfCategoryNotFound) {
				throw new BTSLBaseException("AddChannelUser", methodName,
						PretupsErrorCodesI.LOWER_HIERARCHY_TO_HIGHER_HIERARCHY_NOT_POSSIBLE);
			}

		}

		if (PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())){
			ownerParentInfoVO = checkOperatorValidation(requestVO, categoryVO, senderVO, con, parentcategoryVO);
		}
		if (senderVO.getUserType().equals(PretupsI.USER_TYPE_CHANNEL)) {
			ownerParentInfoVO = checkChannelUserValidation(requestVO, categoryVO, senderVO, con, parentcategoryVO);
		}
		return ownerParentInfoVO;
	}

	private OwnerParentInfoVO checkOperatorValidation(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO,
			ChannelUserVO senderVO, Connection con, CategoryVO parentcategoryVO) throws BTSLBaseException {
		final String methodName = "checkOperatorValidation";
		UserVO ownwerVO = null;
		UserVO parentVO = null;
		OwnerParentInfoVO ownerParentInfoVO = new OwnerParentInfoVO();
		UserDAO userDao = new UserDAO();
		boolean flag1 = false;
		StringBuffer userStat = new StringBuffer();
		ArrayList userList = new ArrayList<>();
		GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
		// load the geographies info from the user_geographies
		
		List<UserGeographiesVO> geolist = _geographyDAO.loadUserGeographyList(con, senderVO.getUserID(),senderVO.getNetworkID());
		

		for (int i = 0; i < geolist.size(); i++) {
			if (((UserGeographiesVO) geolist.get(i)).getGraphDomainCode()
					.equals(requestVO.getData().getGeographicalDomain())) {
				flag1 = true;
				break;
			}

		}

		if (flag1 == false) {
			throw new BTSLBaseException("AddChannelUser", methodName,
					PretupsErrorCodesI.GEOGRAPHY_DOMAIN_CODE_DOES_NOT_EXIST);
		}
		flag1 = false; // to continue next validation check
		if (categoryVO.getSequenceNumber() > 1 && requestVO.getData().getParentCategory() != null
				&& parentcategoryVO.getSequenceNumber() == 1) {
			if (requestVO.getData().getOwnerUser() != null) {
				{
					userStat.delete(0, userStat.length());
					userStat.append("'");
					userStat.append(PretupsI.USER_STATUS_PREACTIVE);
					userStat.append("','");
					userStat.append(PretupsI.USER_STATUS_ACTIVE);
					userStat.append("','");
					userStat.append(PretupsI.USER_STATUS_CHURN);
					userStat.append("'");
					try {
					ownwerVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getData().getOwnerUser());
					} catch (SQLException sqe) {
						throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing", sqe);
						}
					
					userList = userWebDAO.checkOwnerUserList(con, requestVO.getData().getGeographicalDomain(), "%%%",
							requestVO.getData().getDomain(), "IN",userStat.toString(),ownwerVO.getLoginID());  // "Y,CH,PA"
					
					if (userList == null || userList.isEmpty()) {
						throw new BTSLBaseException("AddChannelUser", methodName,
								PretupsErrorCodesI.OWNER_USER_DOES_NOT_EXIST);
					}
					// adding for Pretups-22088
					parentVO=ownwerVO;
//					userList = userWebDAO.loadOwnerUserList(con, requestVO.getData().getGeographicalDomain(), "%%%",
//							requestVO.getData().getDomain(), "IN", userStat.toString());  //"Y,CH,PA"
//					try {
//						ownwerVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getData().getOwnerUser());
//					} catch (SQLException sqe) {
//						throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing", sqe);
//					}
//					if (userList == null || userList.isEmpty()) {
//						throw new BTSLBaseException("AddChannelUser", methodName,
//								PretupsErrorCodesI.OWNER_USER_DOES_NOT_EXIST);
//					}
//					for (int i = 0; i < userList.size(); i++) {
//						if (((UserVO) userList.get(i)).getLoginID().equals(ownwerVO.getLoginID())) {
//							flag1 = true;
//							break;
//						}
//					}
//					if (flag1 == false) {
//						throw new BTSLBaseException("AddChannelUser", methodName,
//								PretupsErrorCodesI.OWNER_USER_DOES_NOT_EXIST);
//					}
				}

			} else {
				throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.OWNER_USER_REQUIRED);
			}
		} else if (categoryVO.getSequenceNumber() > 1 && requestVO.getData().getParentCategory() != null
				&& parentcategoryVO.getSequenceNumber() < categoryVO.getSequenceNumber()) {
			if (requestVO.getData().getOwnerUser() != null) {
				{	userStat.delete(0, userStat.length());
					userStat.append("'");
					userStat.append(PretupsI.USER_STATUS_PREACTIVE);
					userStat.append("','");
					userStat.append(PretupsI.USER_STATUS_ACTIVE);
					userStat.append("','");
					userStat.append(PretupsI.USER_STATUS_CHURN);
					userStat.append("'");
					
					try {
						ownwerVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getData().getOwnerUser());
					} catch (SQLException sqe) {
						throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing", sqe);
					}
					
					userList = userWebDAO.checkOwnerUserList(con, requestVO.getData().getGeographicalDomain(), "%%%",
							requestVO.getData().getDomain(), "IN",userStat.toString(),ownwerVO.getLoginID());  // "Y,CH,PA"
					
					if (userList == null || userList.isEmpty()) {
						throw new BTSLBaseException("AddChannelUser", methodName,
								PretupsErrorCodesI.OWNER_USER_DOES_NOT_EXIST);
					}
//					for (int i = 0; i < userList.size(); i++) {
//						if (((UserVO) userList.get(i)).getLoginID().equals(ownwerVO.getLoginID())) {
//							flag1 = true;
//							break;
//						}
//					}
//					if (flag1 == false) {
//						throw new BTSLBaseException("AddChannelUser", methodName,
//								PretupsErrorCodesI.OWNER_USER_DOES_NOT_EXIST);
//					}
				}

			} else {
				throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.OWNER_USER_REQUIRED);
			}

			if (requestVO.getData().getParentUser() != null) {
				try {
					parentVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getData().getParentUser());
				} catch (SQLException sqe) {
					throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing", sqe);
				}
				
				userStat.delete(0, userStat.length());
				userStat.append("'");
				userStat.append(PretupsI.USER_STATUS_PREACTIVE);
				userStat.append("','");
				userStat.append(PretupsI.USER_STATUS_ACTIVE);
				userStat.append("','");
				userStat.append(PretupsI.USER_STATUS_CHURN);
				userStat.append("'");
				
				userList = userWebDAO.loadUsersListByNameAndOwnerId(con, requestVO.getData().getParentCategory(), "%%%",
						ownwerVO.getUserID(),ownwerVO.getUserID(), "IN", userStat.toString(), "CHANNEL");
				if (userList == null || userList.isEmpty()) {
					throw new BTSLBaseException("AddChannelUser", methodName,
							PretupsErrorCodesI.PARENT_USERS_NOT_EXIST);
				}
				for (int i = 0; i < userList.size(); i++) {
					if (((UserVO) userList.get(i)).getLoginID().equals(parentVO.getLoginID())) {
						flag1 = true;
						break;
					}
				}
				if (flag1 == false) {
					throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.PARENT_USER_NOT_GIVEN);
				}
			} else {
				throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.PARENT_USER_NOT_GIVEN);
			}

		} else if (categoryVO.getSequenceNumber() > 1 && requestVO.getData().getParentCategory() != null) {
			throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.PARENT_CATEGORY_NOT_GIVEN);
		}

		ownerParentInfoVO.setOwnwerVO(ownwerVO);
		ownerParentInfoVO.setParentVO(parentVO);
		return ownerParentInfoVO;
	}

	private OwnerParentInfoVO checkChannelUserValidation(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO,
			ChannelUserVO senderVO, Connection con, CategoryVO parentcategoryVO) throws BTSLBaseException {
		final String methodName = "checkChannelUserValidation";
		OwnerParentInfoVO ownerParentInfoVO = new OwnerParentInfoVO();
		UserVO ownwerVO = null;
		UserVO parentVO = null;
		UserWebDAO userWebDAO = new UserWebDAO();
		UserDAO userDao = new UserDAO();
		boolean flag1 = false;
		ArrayList userList = new ArrayList<>();

		final List catList1 = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con, senderVO.getCategoryCode());
		CategoryVO categoryVO1 = (CategoryVO) catList1.get(0);
		CategoryVO categoryVO2 = categoryVO;
		if (categoryVO1.getSequenceNumber() >= categoryVO2.getSequenceNumber()) {
			throw new BTSLBaseException("AddChannelUser", methodName,
					PretupsErrorCodesI.LOWER_HIERARCHY_TO_HIGHER_HIERARCHY_NOT_POSSIBLE);
		}

		if (!(senderVO.getCategoryCode().equals(requestVO.getData().getParentCategory()))) {
			if (requestVO.getData().getParentCategory() != null
					&& parentcategoryVO.getSequenceNumber() < categoryVO.getSequenceNumber()) {

				ownwerVO = userDao.loadUserDetailsFormUserID(con, senderVO.getOwnerID());

				if (requestVO.getData().getParentUser() != null) {
					try {
						parentVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getData().getParentUser());
					} catch (SQLException sqe) {
						throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing", sqe);
					}
					userList = userWebDAO.loadUsersListByNameAndOwnerId(con, requestVO.getData().getParentCategory(),
							"%%%", ownwerVO.getUserID(), senderVO.getUserID(), "IN", "'Y','CH','PA'", "CHANNEL");
					if (userList == null || userList.isEmpty()) {
						throw new BTSLBaseException("AddChannelUser", methodName,
								PretupsErrorCodesI.PARENT_USERS_NOT_EXIST);
					}
					for (int i = 0; i < userList.size(); i++) {
						if (((UserVO) userList.get(i)).getLoginID().equals(parentVO.getLoginID())) {
							flag1 = true;
							break;
						}
					}

				} else {
					throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.PARENT_USER_NOT_GIVEN);
				}
				if (flag1 == false) {
					throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.PARENT_USER_INVALID);
				}
			}
		}

		ownerParentInfoVO.setOwnwerVO(ownwerVO);
		ownerParentInfoVO.setParentVO(parentVO);
		return ownerParentInfoVO;

	}

	private void checkOutletsAllowed(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO,
			ChannelUserVO senderVO, Connection con) throws BTSLBaseException {
		final String methodName = "checkOutletsAllowed";
		if (TypesI.YES.equals(categoryVO.getOutletsAllowed())) {
			// load the outlet dropdown
			// load the outlet dropdown

			ArrayList outLetList = LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true);
			boolean flag5 = true;
			for (int k = 0; k < outLetList.size(); k++) {
				if (((ListValueVO) outLetList.get(k)).getValue().equals(requestVO.getData().getOutletCode())) {
					flag5 = false;
				}
			}
			if (flag5 == true) {
				throw new BTSLBaseException("AddChannelUser", methodName,
						PretupsErrorCodesI.OUTLET_CODE_DOES_NOT_EXIST);
			}
			channelUserVO.setOutletCode(requestVO.getData().getOutletCode());
			final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
			ArrayList suboutLetList = sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE);
			boolean flag6 = true;
			for (int k = 0; k < suboutLetList.size(); k++) {
				String suboutletCode = ((ListValueVO) suboutLetList.get(k)).getValue();
				String[] split = suboutletCode.split(":");
				if ((split[1].equals(requestVO.getData().getOutletCode()))) {
					if (split[0].equals(requestVO.getData().getSubOutletCode())) {
						flag6 = false;
					}
				}
			}
			if (flag6 == true) {
				throw new BTSLBaseException("AddChannelUser", methodName,
						PretupsErrorCodesI.SUB_OUTLET_CODE_DOES_NOT_EXIST);
			}
			channelUserVO.setSubOutletCode(requestVO.getData().getSubOutletCode());
		}

	}

	private void checkPersonalDetailsValidation(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO,
			ChannelUserVO senderVO, Connection con) throws BTSLBaseException {
		final String methodName = "checkPersonalDetailsValidation";
		String defaultGeoCode = "";
		RequestVO requestVO1 = new RequestVO();
		ExtUserDAO extUserDao = new ExtUserDAO();
		requestVO1.setUserCategory(categoryVO.getCategoryCode());
		// User name set
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
			if (BTSLUtil.isNullString(requestVO.getData().getFirstName())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FIRST_NAME_BLANK);
			}
			channelUserVO.setFirstName(requestVO.getData().getFirstName());
			channelUserVO.setLastName(requestVO.getData().getLastName());
			if (!BTSLUtil.isNullString(requestVO.getData().getLastName())) {
				channelUserVO.setUserName(requestVO.getData().getFirstName() + " " + requestVO.getData().getLastName());
			} else {
				channelUserVO.setUserName(requestVO.getData().getFirstName());
			}
		} else {
			channelUserVO.setFirstName(requestVO.getData().getFirstName());
			channelUserVO.setUserName(requestVO.getData().getUserName());
		}
		if (!BTSLUtil.isNullString(requestVO.getData().getDesignation())
				&& !BTSLUtil.isValidInputField(requestVO.getData().getDesignation())) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DESIGNATION_NOT_VALID);
		}
		channelUserVO.setDesignation(requestVO.getData().getDesignation());
		channelUserVO.setAuthTypeAllowed(requestVO.getData().getAuthTypeAllowed());
		// User
		// Name
		// Mandatory
		// Value
		// short name set
//commenting for pretups-23098
//		if(!senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM)) {
//		if (BTSLUtil.isNullString(requestVO.getData().getShortName())) {
//			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SHORT_NAME_BLANK);
//		}
//		}
		if (!BTSLUtil.isNullString(requestVO.getData().getShortName())
				&& !BTSLUtil.isValidName(requestVO.getData().getShortName())) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SHORT_NAME_INVALID);
		}
		channelUserVO.setShortName(requestVO.getData().getShortName());

		final String SubcriberCode = requestVO.getData().getSubscriberCode();
		// channelUserVO.setShortName(SubcriberCode);
		/*
		 * If IS_DEFAULT_PROFILE is true in system then default geography assigned to
		 * user
		 */
		if ((boolean) ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE)))
				.booleanValue()) {
			// Web Login of User
			// Set default geographical code
			defaultGeoCode = extUserDao.getDefaultGeoCodeDtlBasedOnNetwork(con, requestVO1,
					requestVO.getData().getExtnwcode());
			LogFactory.printLog(methodName, "defaultGeoCode = " + defaultGeoCode, LOG);
			// 11-MAR-2014
			if (!BTSLUtil.isNullString(defaultGeoCode)) {
				channelUserVO.setGeographicalCode(defaultGeoCode);
			} else {
				channelUserVO.setGeographicalCode("COMMON1");
				// Ended Here
			}
		} else {
			channelUserVO.setGeographicalCode(senderVO.getgeographicalCodeforNewuser());

		}

	}

	private void checkCredentials(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO,
			Connection con) throws BTSLBaseException {
		final String methodName = "checkCredentials";
		UserDAO userDAO = new UserDAO();
		String webLoginId = BTSLUtil.NullToString(requestVO.getData().getWebloginid());
		String oldLoginId = BTSLUtil.NullToString(requestVO.getData().getOldLogin());
		
		final HashMap mp = new HashMap();
		Msisdn[] msisdns = requestVO.getData().getMsisdn();
		Msisdn m = new Msisdn();
		
		if(requestVO.getData().getUserCatCode()!=null) {
			if( !PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())  && msisdns.length==0) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_MSISDN_BLANK);
			}
		}
		
		if((requestVO.getData().getUserCatCode().equals(PretupsI.CUSTOMER_CARE))) {
			channelUserVO.setMsisdn(requestVO.getData().getMobileNumber());
		}
		if(!(requestVO.getData().getUserCatCode().equals(PretupsI.CUSTOMER_CARE))) {
			for (int i = 0; i < msisdns.length; i++) {
				m = msisdns[i];
				if (m.getIsprimary().equals("Y")) {
					primaryMsisdn = m.getPhoneNo();
					senderPin = m.getPin();
				}
				if(m.getPhoneNo()!=null) {
					requestuserPhoneList.add(m.getPhoneNo());  // Phone list from UI request.
			    }
				if (mp.containsKey(PretupsBL.getFilteredMSISDN(m.getPhoneNo()))) {
					LOG.error(methodName, "Error: Duplicate entry of the MSISDN Number in the list");
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DUPLICATE_MSISDN_IN_LIST);
				} else {
					mp.put(PretupsBL.getFilteredMSISDN(m.getPhoneNo()), PretupsBL.getFilteredMSISDN(m.getPhoneNo()));
				}
				if (!(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW)))
						.booleanValue())) {
					if (m.getPin() == null || m.getPin() == "") {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PIN_REQUIRED);
					}
					operatorUtili.validatePIN(AESEncryptionUtil.aesDecryptor(m.getPin(), Constants.A_KEY));
				}
			}
		}
		webPassword = requestVO.getData().getWebpassword();
		if (PretupsI.YES.equalsIgnoreCase(categoryVO.getWebInterfaceAllowed())) {
			// Set MSISDN as login id if Login Id is blank in request
			if (BTSLUtil.isNullString(webLoginId)) {
				channelUserVO.setLoginID(primaryMsisdn);
			} else {
				
				if(requestVO.getApprovalLevel().equals(PretupsI.NEW) && requestVO.getUserAction().equals(PretupsI.OPERATION_ADD)) {
				if (userDAO.isUserLoginExist(con, webLoginId, null)) {
					final String[] argsArray = { webLoginId };
					throw new BTSLBaseException("AddChannelUser", methodName,
							PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
				}
				}else { //APPRV1/APPRV2/APPRV3
					
					if(PretupsI.SUPER_ADMIN.equalsIgnoreCase(senderVO.getCategoryCode()) || PretupsI.PWD_CAT_CODE_NWADM.equalsIgnoreCase(senderVO.getCategoryCode() )){  // if superadmin logs in .
						existingDBchannelUserVO = channelUserDao.loadUsersDetailsByLoginId(con, oldLoginId, null, PretupsI.STATUS_NOTIN, PretupsBL.userStatusNotIn());
					}else {
						existingDBchannelUserVO = 	channelUserDao.loadChnlUserDetailsByLoginID(con, webLoginId);
					}
					checkApprovalReqValidation(requestVO, existingDBchannelUserVO);
						if (userDAO.isUserLoginExist(con, webLoginId, existingDBchannelUserVO.getUserID())) {
							final String[] argsArray = { webLoginId };
							throw new BTSLBaseException("AddChannelUser Approval", methodName,
									PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
						}
						webPassword=requestVO.getData().getWebpassword();
					
				}

				channelUserVO.setLoginID(webLoginId);
			}

			// webPassword = BTSLUtil.decryptText(webPassword);

			if (!BTSLUtil.isNullString(webPassword)) {
				webPassword = AESEncryptionUtil.aesDecryptor(webPassword, Constants.A_KEY);
				final Map errorMessageMap = operatorUtili.validatePassword(webLoginId, webPassword);
				if (null != errorMessageMap && errorMessageMap.size() > 0) {
					Integer minLoginPwdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH);
					Integer maxLoginPwdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH);
					final String[] argsArray = { minLoginPwdLength.toString(), maxLoginPwdLength.toString()};
					throw new BTSLBaseException("AddChannelUser", methodName,
							PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID_MODIFIED,argsArray);
				}
			}
			
			
			// for edit request
			if("EDIT".equalsIgnoreCase(requestVO.getUserAction()) && BTSLUtil.isNullString(webPassword)) {
				webPassword = userDAO.getPinPassword(con, oldLoginId).getPassword();
				webPassword = BTSLUtil.decryptText(webPassword);
			}
			
			// Assign Primary MSISDN as web password for null password value
			// in request.
			if (!senderVO.getCategoryCode().equals(PretupsI.PWD_USER_SUADM) && !senderVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_NWADM)) {
				if (BTSLUtil.isNullString(webPassword)) {
					randomPwd = operatorUtili.generateRandomPassword();
					channelUserVO.setPassword(BTSLUtil.encryptText(randomPwd));
				} else {
					channelUserVO.setPassword(BTSLUtil.encryptText(webPassword));
				}
			}
			
			
			
			
			
			
			
		} else {
			// 01-APR-2104 if category do not have web access and
			// XMLGW/EXTGW is allowed then WEBLOGINID is required else
			// create user without loginid
			final List<String> gwAccessTypeList = new CategoryReqGtwTypeDAO().loadCategoryRequestGwType(con,
					requestVO.getData().getUserCatCode());
			final String webLoginID = webLoginId;
			if (gwAccessTypeList != null && !gwAccessTypeList.isEmpty()) {
				if (gwAccessTypeList.contains(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)
						|| gwAccessTypeList.contains(PretupsI.REQUEST_SOURCE_TYPE_XMLGW)
						|| gwAccessTypeList.contains(PretupsI.REQUEST_SOURCE_TYPE_WEB)) {
					if (BTSLUtil.isNullString(webLoginID)) {
						throw new BTSLBaseException("AddChannelUser", methodName,
								PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_BLANK);
					} else {
						if (userDAO.isUserLoginExist(con, webLoginID, null)) {
							final String[] argsArray = { webLoginID };
							throw new BTSLBaseException("AddChannelUser", methodName,
									PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST, argsArray);
						}

					}

				}

			} else if (!BTSLUtil.isNullString(webLoginID)) {

				if (userDAO.isUserLoginExist(con, webLoginID, null)) {
					final String[] argsArray = { webLoginID };
					throw new BTSLBaseException("AddChannelUser", methodName,
							PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_BLANK, argsArray);
				}
			}
			channelUserVO.setLoginID(webLoginID);
			webPassword = requestVO.getData().getWebpassword();
			if (!BTSLUtil.isNullString(webPassword)) {
				final Map errorMessageMap = operatorUtili.validatePassword(webLoginID, webPassword);
				if (null != errorMessageMap && errorMessageMap.size() > 0) {
					throw new BTSLBaseException("AddChannelUser", methodName,
							PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID);
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
		
		if((requestVO.getUserAction()!=null && requestVO.getUserAction().equals(PretupsI.OPERATION_EDIT)) || !requestVO.getApprovalLevel().equals(PretupsI.NEW) ) {
		modifedLoginId = requestVO.getData().getWebloginid();
	    existingLoginId = existingDBchannelUserVO.getLoginID();
	    channelUserVO.setUserID(existingDBchannelUserVO.getUserID());
	   }

	}
	
private void checkModifiedPassword(ChannelUserApprovalReqVO requestVO) throws BTSLBaseException{
	final String methodName ="checkModifiedPassword";
    // Web Password
    String modifedPassword = requestVO.getData().getWebpassword();
    modifedPassword = BTSLUtil.decryptText(modifedPassword);
    if (!BTSLUtil.isNullString(modifedPassword)) {
        final Map errorMessageMap = operatorUtili.validatePassword(channelUserVO.getLoginID(), modifedPassword);
        if (null != errorMessageMap && errorMessageMap.size() > 0) {
            throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID_MODIFIED);
        }
    }
   
    if (!BTSLUtil.isNullString(modifedPassword) && !BTSLUtil.encryptText(modifedPassword).equalsIgnoreCase(requestVO.getData().getWebpassword())) {
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

}
	private void checkNetwork(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO,
			Connection con) throws BTSLBaseException {
		final String methodName = "checkNetwork";
		ChannelUserWebDAO channelUserWebDao = new ChannelUserWebDAO();
		// Ended Here
		// User name prefix.
		final String userPrifix = requestVO.getData().getUserNamePrefix().toUpperCase();
		ArrayList userNameList = LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true);
		boolean flag4 = true;
		for (int k = 0; k < userNameList.size(); k++) {
			if (((ListValueVO) userNameList.get(k)).getValue().equals(requestVO.getData().getUserNamePrefix())) {
				flag4 = false;
			}
		}
		if (flag4 == true) {
			throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.USERNAME_TYPE_DOES_NOT_EXIST);
		}
		channelUserVO.setUserNamePrefix(userPrifix);

		final String extCode = requestVO.getData().getExternalCode();

		if(!BTSLUtil.isNullString(extCode) && BTSLUtil.isContainsSpecialCharacters(extCode)) {
			throw new BTSLBaseException(this, methodName,
					PretupsErrorCodesI.EXTERNAL_CODE_SPCL_CHAR_NA);
		}
		
		if(requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
		if (!BTSLUtil.isNullString(extCode)) {
			final boolean isExtCodeExist = channelUserWebDao.isExternalCodeExist(con, extCode, null);
			if (isExtCodeExist) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTERNAL_CODE_ALREARY_EXIST);
			}
		}
		}else { //APPRV1 APPRV2 ,APPRV3 ...
		
			if (!BTSLUtil.isNullString(extCode)) {
				final boolean isExtCodeExist = channelUserWebDao.isExternalCodeExist(con, extCode, existingDBchannelUserVO.getUserID());
				if (isExtCodeExist) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTERNAL_CODE_ALREARY_EXIST);
				}
			}
		}
		channelUserVO.setExternalCode(extCode);

		// Set actual network of the user for
		final String actualNetworkCode = requestVO.getData().getExtnwcode();
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
				throw new BTSLBaseException("AddChannelUser", methodName,
						PretupsErrorCodesI.EXTSYS_REQ_ACTUAL_NW_CODE_INVALID);
			}
		}

	}

	private void checkEmail(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO,
			Connection con) throws BTSLBaseException {
		final String methodName = "checkEmail";
		// EmailId
		boolean blank;
		final String modifedEmail = requestVO.getData().getEmailid();
		blank = BTSLUtil.isNullString(modifedEmail);
		boolean validEmail = false;
		if (!blank) {
			validEmail = BTSLUtil.validateEmailID(modifedEmail);
			if (validEmail) {
				channelUserVO.setEmail(modifedEmail.toString().trim());
			} else {
				throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.INVALID_EMAIL_MAPP);
			}
		}
		
		//commenting for PRETUPS-23098
//		else {
//			throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.EXTSYS_REQ_EMAILID_NOTFOUND);
//		}
		// other email check
		final String otherEmail = requestVO.getData().getOtherEmail();
		blank = BTSLUtil.isNullString(otherEmail);
		boolean validOEmail = false;
		if (!blank) {
			validOEmail = BTSLUtil.validateEmailID(otherEmail);
			if (validOEmail) {
				channelUserVO.setAlertEmail(otherEmail.toString().trim());
			} else {
				throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.INVALID_EMAIL_MAPP);
			}
		}

	}

	private void setparentOwnerIDs(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO,
			Connection con, OwnerParentInfoVO ownerParentInfoVO) throws BTSLBaseException, SQLException {

		boolean isownerIDNew = false;
		UserDAO userDao = new UserDAO();
		channelUserVO.setNetworkID(requestVO.getData().getExtnwcode());
		if(requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
		channelUserVO.setUserID(generateUserId(channelUserVO.getNetworkID(), categoryVO.getUserIdPrefix()));
		} else {
			channelUserVO.setUserID(existingDBchannelUserVO.getUserID());
		}
		final String categoryID = String.valueOf(categoryVO.getSequenceNumber());
		if ("1".equals(categoryID)) {
			isownerIDNew = true;
			channelUserVO.setParentID(PretupsI.ROOT_PARENT_ID);
			channelUserVO.setOwnerID(channelUserVO.getUserID());
		} else {
			if(ownerParentInfoVO!=null) {
			if (ownerParentInfoVO.getOwnwerVO() != null) {
				channelUserVO.setOwnerID(ownerParentInfoVO.getOwnwerVO().getUserID());
				channelUserVO.setParentID(ownerParentInfoVO.getOwnwerVO().getParentID());
			}
			if (ownerParentInfoVO.getParentVO() != null) {
				channelUserVO.setParentID(ownerParentInfoVO.getParentVO().getUserID());
			}
			}
			/*
			 * This is the case when no search has performed, Like DIST add SE at that time
			 * no search performed so we explicitly set the ownerid=ownerid of the session
			 * user
			 */
			if (BTSLUtil.isNullString(channelUserVO.getOwnerID())) {
				channelUserVO.setOwnerID(senderVO.getOwnerID());
			}

			/*
			 * This is the case when no search has performed, Like 1)BUC add SE ownerId and
			 * parentId both are same 2)DIST add SE parentID=userid of the session user
			 */
			if (PretupsI.PWD_CAT_CODE_SUADM.equals(senderVO.getCategoryCode()) || PretupsI.PWD_CAT_CODE_NWADM.equals(senderVO.getCategoryCode())) {
					channelUserVO.setParentID(senderVO.getUserID());
					channelUserVO.setOwnerID(senderVO.getUserID());
			} else if( PretupsI.OPERATOR_CATEGORY.equals(senderVO.getCategoryCode()) ) {
				if(!requestVO.getData().getParentCategory().equals(PretupsI.CATEGORY_CODE_DIST) ) {
				if(channelUserVO.getCategoryCode().equals(PretupsI.CATEGORY_CODE_RETAILER) || channelUserVO.getCategoryCode().equals(PretupsI.CATEGORY_CODE_AGENT)) {
					ChannelUserVO	parentVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getData().getParentUser());
					channelUserVO.setParentID(parentVO.getUserID());
			  } else if (channelUserVO.getCategoryCode().equals(PretupsI.CATEGORY_CODE_DEALER) ) {
					channelUserVO.setParentID(channelUserVO.getOwnerID());
			  }
			}
			}else {
				  channelUserVO.setParentID(channelUserVO.getOwnerID());
			  }
			
		}

		if (isownerIDNew) {
			channelUserVO.setOwnerID(channelUserVO.getUserID());
		}
		LOG.debug("AddChannelUser", "process : channelUserVO.getUserID = " + channelUserVO.getUserID());
	}

	private void checkGeographyValidation(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO,
			ChannelUserVO senderVO, Connection con, OwnerParentInfoVO ownerParentInfoVO,UserVO userVO)
			throws BTSLBaseException, SQLException {
		final String methodName = "checkGeographyValidation";
		// code for geography is moved here after parent details fetched as parent
		// geocode is required
		UserDAO userDao = new UserDAO();
		UserVO parentVO =null;
		
		String geocode = requestVO.getData().getGeographyCode();
		GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
		LogFactory.printLog(methodName, "geocode value = " + geocode, LOG);
		String defaultGeoCode = "";

		if (!BTSLUtil.isNullString(geocode)) {
			// logic to validate the passed geocode
			GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
			boolean isValidGeoCode = false;
			String parentID = null;
			if (PretupsI.ROOT_PARENT_ID.equals(channelUserVO.getParentID())) {
				if (ownerParentInfoVO == null) {
					if (userVO.isStaffUser())
						parentID = senderVO.getUserID();
					else {
						parentID = channelUserVO.getUserID();
					}
				}
			else if(ownerParentInfoVO.getParentVO()==null) {
				parentID =ownerParentInfoVO.getOwnwerVO().getUserID();
				}else {
					parentID =ownerParentInfoVO.getParentVO().getUserID();	
				}
						
			} else {
				
				if(!BTSLUtil.isNullObject(requestVO.getData().getParentUser())) {
					parentVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getData().getParentUser());
					parentID =parentVO.getUserID();
				}else {
					
					parentVO = userDao.loadAllUserDetailsByLoginID(con, requestVO.getData().getOwnerUser());
					parentID = parentVO.getUserID();
				}
				
			}
			ChannelUserVO loginIDVO1 = userDao.loadUserDetailsFormUserID(con, parentID);
			String msisdn1 = null;
			if (loginIDVO1 == null) {
				msisdn1 = senderVO.getMsisdn();
			} else {
				msisdn1 = loginIDVO1.getMsisdn();
			}
			ChannelUserVO parentCHannelUserVO1 =null;
			if (PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())) {
				parentCHannelUserVO1= channelUserDao.loadUsersDetails(con, null, parentID,
						PretupsI.STATUS_NOTIN, "'N','C'");
			}else {
				parentCHannelUserVO1= channelUserDao.loadUsersDetails(con, msisdn1, null,
					PretupsI.STATUS_NOTIN, "'N','C'");
			}
			ArrayList geographyList1=null;
			if(parentCHannelUserVO1!=null) {
			// load the geographies info from the user_geographies
			 geographyList1 = geographyDAO.loadUserGeographyList(con, parentID,
					parentCHannelUserVO1.getNetworkID());
			}
			if (parentCHannelUserVO1!=null &&  parentCHannelUserVO1.getCategoryVO().getGrphDomainType().equals(categoryVO.getGrphDomainType())) {

				List geoList = new ArrayList<>();
				for (int i = 0; i < geographyList1.size(); i++) {
					geoList.add(((UserGeographiesVO) geographyList1.get(i)).getGraphDomainCode());
				}
				if (!(geoList.contains(geocode))) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
				}
				isValidGeoCode = true;
				defaultGeoCode = geocode;
			}
			// check for other level (SE and Retailer)
			else if (categoryVO.getSequenceNumber() > 1) {
				LogFactory.printLog(methodName, "low level hirearchy = " + geocode, LOG);
				ChannelUserVO loginIDVO=null;
				if (!PretupsI.OPERATOR_CATEGORY.equals(senderVO.getCategoryCode())) {
					 loginIDVO = userDao.loadUserDetailsFormUserID(con, channelUserVO.getParentID());
				}else {
					 loginIDVO = userDao.loadUserDetailsFormUserID(con, parentID);
				}
				String msisdn = loginIDVO.getMsisdn();
				ChannelUserVO parentCHannelUserVO = channelUserDao.loadUsersDetails(con, msisdn, null,
						PretupsI.STATUS_NOTIN, "'N','C'");
				String parentGeoCode = parentCHannelUserVO.getGeographicalCode();
				List<String> geoDomainListUnderParent = geographyDAO.loadGeographyHierarchyUnderParent(con,
						parentGeoCode, parentCHannelUserVO.getNetworkID(), categoryVO.getGrphDomainType());
				geoDomainListUnderParent.add(parentGeoCode);
				if (geoDomainListUnderParent.contains(geocode)) {
					LogFactory.printLog(methodName, "low level hirearchy 1= " + geocode, LOG);

					isValidGeoCode = true;
					defaultGeoCode = geocode;
				}
			} else {
				List geoList = new ArrayList<>();
				LogFactory.printLog(methodName, "top level hirearchy = " + geocode, LOG);
				List geographyList = geographicalDomainWebDAO.loadGeographyList(con, senderVO.getNetworkID(),
						requestVO.getData().getGeographicalDomain(), "%");
				for (int i = 0; i < geographyList.size(); i++) {
					geoList.add(((UserGeographiesVO) geographyList.get(i)).getGraphDomainCode());
				}
				if (categoryVO.getSequenceNumber() > 1 && !(geoList.contains(geocode))) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
				}
				if (!geographyDAO.isGeographicalDomainExist(con, geocode, true)) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
				}

				defaultGeoCode = geocode;
				isValidGeoCode = true;
			}

			if (!isValidGeoCode) {
				 response.setMessageCode(PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
				throw new BTSLBaseException("AddChannelUser", "loadGeographyUnderParent",
						PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
			}
			channelUserVO.setGeographicalCode(defaultGeoCode);
			LogFactory.printLog(methodName, "Passed GeoCode = " + defaultGeoCode, LOG);
		}

	}

	private void checkApprovalLevel(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO,
			ChannelUserVO senderVO, Connection con)
			throws BTSLBaseException {
		final String methodName = "checkGeographyValidation";
	    String selectSysPrefCode=null;
		/*
		 * If USR_APPROVAL_LEVEL = 0 no approval required, if USR_APPROVAL_LEVEL = 1
		 * level 1 approval required, if USR_APPROVAL_LEVEL = 2 level 2 approval
		 * required' While adding user check whether the approval is required or not if
		 * USR_APPROVAL_LEVEL > 0 set status = N(New)//approval required else set status
		 * = Y(Active)
		 */

		// Set the approval level
		
		
	    if (!PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID()) || PretupsI.OPERATOR_CATEGORY.equals(senderVO.getCategoryCode()) ) {
	    	selectSysPrefCode=PreferenceI.USER_APPROVAL_LEVEL;
	    }else {
	    	selectSysPrefCode=PreferenceI.OPT_USR_APRL_LEVEL;
	    }
		
	    
		int maxApprovalLevel =((Integer) PreferenceCache.getControlPreference(selectSysPrefCode, senderVO.getNetworkID(),
				requestVO.getData().getUserCatCode())).intValue();
		if (maxApprovalLevel > 0    && requestVO.getApprovalLevel().equals(PretupsI.NEW) && requestVO.getUserAction().equals(PretupsI.OPERATION_EDIT)  ) {
			channelUserVO.setStatus(existingDBchannelUserVO.getStatus());// W
			// New
			channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// W
			// New

		}
		
		if (maxApprovalLevel > 0    && requestVO.getApprovalLevel().equals(PretupsI.NEW) && requestVO.getUserAction().equals(PretupsI.OPERATION_ADD)  ) {
			channelUserVO.setStatus(PretupsI.USER_STATUS_NEW);// W
			// New
			channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// W
			// New

		}
		else { //Approval scenario - APPRV1 ,APPRV2
			
			if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
			    
			   	
				 if(commonUtil.getCurrentApprovalLevel(requestVO.getApprovalLevel())==maxApprovalLevel ) {
					 if (selectSysPrefCode.equals(PreferenceI.USER_APPROVAL_LEVEL)) {
						channelUserVO.setStatus(PretupsI.USER_STATUS_PREACTIVE);// PA
						channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// W
					 }
					 else {
						 channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// A
						 channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_PREACTIVE);// PA
					 }

				 }else {
							channelUserVO.setPreviousStatus(channelUserVO.getStatus());//
							channelUserVO.setStatus(PretupsI.USER_STATUS_PREACTIVE);// PA
				 }
				 setApprovalLevels(commonUtil.getCurrentApprovalLevel(requestVO.getApprovalLevel()),senderVO);
				// Active
			} else {
				channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// A
				// Active
				channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);// A
				// Active
				setApprovalLevels(commonUtil.getCurrentApprovalLevel(requestVO.getApprovalLevel()),senderVO);
			}
		}

	}
	
	// should be called only in APPRV1, APPRV2 state
	private void checkApprovalReqValidation(ChannelUserApprovalReqVO requestVO, ChannelUserVO existingchannelUserVO) throws BTSLBaseException {
		final String  methodName="checkApprovalReqValidation";
		  if(requestVO.getApprovalLevel().equals(PretupsI.CHANNEL_USER_APPROVE1)) {
			   if (existingchannelUserVO.getLevel1ApprovedBy()!=null && existingchannelUserVO.getLevel1ApprovedOn()!=null) {
				   throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.USER_APPRV1_ALREADY_COMPLETED);	   
			   }
		  } else if (requestVO.getApprovalLevel().equals(PretupsI.CHANNEL_USER_APPROVE2)) {
			  if (existingchannelUserVO.getLevel2ApprovedBy()!=null && existingchannelUserVO.getLevel2ApprovedOn()!=null) {
			   throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.USER_APPRV2_ALREADY_COMPLETED);
			  }
		  }
		
		
	}
	
	
	private void setApprovalLevels(int currentApprovalLevel,ChannelUserVO senderVO) {
		switch(currentApprovalLevel) {
		case 1 : channelUserVO.setLevel1ApprovedBy(senderVO.getUserID());channelUserVO.setLevel1ApprovedOn(new Date()); break;
		case 2 : channelUserVO.setLevel1ApprovedBy(existingDBchannelUserVO.getLevel1ApprovedBy());
			channelUserVO.setLevel1ApprovedOn( existingDBchannelUserVO.getLevel1ApprovedOn());
			channelUserVO.setLevel2ApprovedBy(senderVO.getUserID());channelUserVO.setLevel2ApprovedOn(new Date()); break;
		default:channelUserVO.setLevel1ApprovedBy(null);channelUserVO.setLevel2ApprovedBy(null);break;
		}
		
	}

	private void setaddress(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO,
			Connection con, OwnerParentInfoVO ownerParentInfoVO) throws BTSLBaseException {
		// set some use full parameter
		final String methodName = "setaddress";
		final Date currentDate = new Date();
		channelUserVO.setPasswordModifiedOn(currentDate);
		channelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);
		channelUserVO.setCreationType(senderVO.getCreationType());
		channelUserVO.setExternalCode(BTSLUtil.NullToString(requestVO.getData().getExternalCode()));// External
		// Code
		
		if(senderVO.getDomainID().equals(PretupsI.DOMAIN_TYPE_OPT) && !senderVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY)  ){	
			if (BTSLUtil.isNullString(requestVO.getData().getSubscriberCode())){
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPCODE_MANDATORY);
			}
		}
		
		if (!BTSLUtil.isNullString(requestVO.getData().getSubscriberCode())
				&& !BTSLUtil.isAlphaNumeric(requestVO.getData().getSubscriberCode())) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPCODE_INVALID);
		}
		
		if ( !BTSLUtil.isNullString(requestVO.getData().getSubscriberCode()) 
				&& requestVO.getData().getSubscriberCode().trim().length() > NumberConstants.N12.getIntValue() ) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SUBSCRIBER_CODE_MAX_LEN_EXCEED);
		}
		
		channelUserVO.setEmpCode(BTSLUtil.NullToString(requestVO.getData().getSubscriberCode()));
		
		boolean isUserCode = (Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.USER_CODE_REQUIRED);
		if (isUserCode) {
			channelUserVO.setUserCode(requestVO.getData().getUserCode());
		} else {
			channelUserVO.setUserCode(this.primaryMsisdn);
		}
		
		
		if( requestVO.getData().getCountry()!=null && requestVO.getData().getCountry().length()>NumberConstants.N20.getIntValue()) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_COUNTRY_LENGTH_EXCEEDS);
		}
		
		if (!BTSLUtil.isNullString(requestVO.getData().getContactNumber())
				&& !BTSLUtil.isValidNumber(requestVO.getData().getContactNumber())) {
			throw new BTSLBaseException(this, methodName,
					PretupsErrorCodesI.EXTSYS_REQ_CONTACTNUMBER_NON_NUMERIC,
					new String[] { requestVO.getData().getContactNumber() });
		}
		channelUserVO.setContactNo(BTSLUtil.NullToString(requestVO.getData().getContactNumber()));
		channelUserVO.setContactPerson(BTSLUtil.NullToString(requestVO.getData().getContactPerson()));
		channelUserVO.setSsn(BTSLUtil.NullToString(requestVO.getData().getSsn()));
		channelUserVO.setAddress1(BTSLUtil.NullToString(requestVO.getData().getAddress1()));
		channelUserVO.setAddress2(BTSLUtil.NullToString(requestVO.getData().getAddress2()));
		channelUserVO.setCity(BTSLUtil.NullToString(requestVO.getData().getCity()));
		channelUserVO.setState(BTSLUtil.NullToString(requestVO.getData().getState()));
		channelUserVO.setCountry(BTSLUtil.NullToString(requestVO.getData().getCountry()));
		channelUserVO.setCreatedBy(senderVO.getUserID());

		final Date curDate = new Date();
		channelUserVO.setCreatedOn(curDate);

		String mobileNumber = primaryMsisdn;
		if (!PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())) {
			if (BTSLUtil.isNullString(mobileNumber)) {
				throw new BTSLBaseException("AddChannelUser", methodName,
						PretupsErrorCodesI.EXTSYS_REQ_PRIMARY_MSISDN_BLANK);
			}
		}
		channelUserVO.setMsisdn(BTSLUtil.NullToString(mobileNumber));
		channelUserVO.setModifiedBy(senderVO.getUserID());
		channelUserVO.setModifiedOn(currentDate);
		channelUserVO.setUserProfileID(channelUserVO.getUserID());
		channelUserVO.setPasswordModifiedOn(currentDate);
		channelUserVO.setPasswordCountUpdatedOn(currentDate);

		channelUserVO.setCompany(BTSLUtil.NullToString(requestVO.getData().getCompany()));
		String fax = requestVO.getData().getFax();
		if (!BTSLUtil.isNullString(fax)) {
			if (BTSLUtil.isValidNumber(fax)) {
				channelUserVO.setFax(fax);
			} else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_FAX_INVALID);
			}
		}

		channelUserVO
				.setLongitude(requestVO.getData().getLongitude() != null ? requestVO.getData().getLongitude() : "");
		channelUserVO.setLatitude(requestVO.getData().getLatitude() != null ? requestVO.getData().getLatitude() : "");

	}

	private void checkDocumentData(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO,
			Connection con, OwnerParentInfoVO ownerParentInfoVO) throws BTSLBaseException {
		final String methodName = "checkDocumentData";
		ArrayList documentList = LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true);
		if (!(BTSLUtil.isNullOrEmptyList(documentList))
				&& !BTSLUtil.isNullString(requestVO.getData().getDocumentType())) {
			boolean flag2 = true;
			for (int k = 0; k < documentList.size(); k++) {
				if (((ListValueVO) documentList.get(k)).getValue().equals(requestVO.getData().getDocumentType())) {
					flag2 = false;
				}
			}
			if (flag2 == true) {
				throw new BTSLBaseException("AddChannelUser", methodName,
						PretupsErrorCodesI.DOCUMENT_TYPE_DOES_NOT_EXIST);
			}
		}

		channelUserVO.setDocumentType(
				requestVO.getData().getDocumentType() != null ? requestVO.getData().getDocumentType() : "");
		channelUserVO
				.setDocumentNo(requestVO.getData().getDocumentNo() != null ? requestVO.getData().getDocumentNo() : "");
	}
	
	private void checkLowBalAlertAllow(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO,
			Connection con, OwnerParentInfoVO ownerParentInfoVO) throws BTSLBaseException {
	if (TypesI.YES.equals(categoryVO.getLowBalAlertAllow())) {
        final String delimiter = ";";
        final String allowforself =  requestVO.getData().getLowbalalertself();
        final String allowforparent =  requestVO.getData().getLowbalalertparent();
        final String allowforOther =  requestVO.getData().getLowbalalertother();
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
	}

	private void checkPaymentData(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO,
			Connection con, OwnerParentInfoVO ownerParentInfoVO) throws BTSLBaseException {
		final String methodName = "checkPaymentData";
		ArrayList paymentList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
		if (!BTSLUtil.isNullOrEmptyList(paymentList) && !BTSLUtil.isNullString(requestVO.getData().getPaymentType())) {
			ArrayList<String> payList = new ArrayList<>();
			for (int l = 0; l < paymentList.size(); l++) {
				payList.add(((ListValueVO) paymentList.get(l)).getValue());
			}
			boolean flag3 = false;
			String paymentTypes = requestVO.getData().getPaymentType();
			if (paymentTypes != null) {
				String[] payTypes = paymentTypes.split(",");
				for (int k = 0; k < payTypes.length; k++) {
					if (!(payList.contains(payTypes[k]))) {
						flag3 = true;
						break;
					}
				}
				if (flag3 == true) {
					throw new BTSLBaseException("AddChannelUser", methodName,
							PretupsErrorCodesI.PAYMENT_TYPE_DOES_NOT_EXIST);
				}
			}
		}
		channelUserVO.setPaymentTypes(
				requestVO.getData().getPaymentType() != null ? requestVO.getData().getPaymentType() : "");
	}
	
	private void checkAllowedLoginTimes(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO,
			ChannelUserVO senderVO, Connection con, OwnerParentInfoVO ownerParentInfoVO)
			throws BTSLBaseException {
		final String methodName = "checkAllowedLoginTimes";
		Date appointmentDate = null;
		if (!BTSLUtil.isNullString(requestVO.getData().getAppointmentdate())) {
			try {
				if (!BTSLUtil.isValidDatePattern(requestVO.getData().getAppointmentdate())) {
					throw new BTSLBaseException(this, methodName,
							PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
				}
				appointmentDate = BTSLUtil.getDateFromDateString(requestVO.getData().getAppointmentdate());
			} catch (Exception e) {
				LOG.error(methodName, "Exception " + e);
				LOG.errorTrace(methodName, e);
				throw new BTSLBaseException(this, methodName,
						PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
			}
			channelUserVO.setAppointmentDate(appointmentDate);
		}

		final String allowedIPs = requestVO.getData().getAllowedip();
		if (!BTSLUtil.isNullString(allowedIPs)) {
			String[] allowedIPAddress = allowedIPs.split(",");
			for (int i = 0; i < allowedIPAddress.length; i++) {
				String splitAllowedIP = allowedIPAddress[i];
				if (!BTSLUtil.isValidateIpAddress(splitAllowedIP)) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_IP_INVALID);
				}
			}
			channelUserVO.setAllowedIps(allowedIPs);
		}

		final String allowedDays = requestVO.getData().getAlloweddays();
		if (!BTSLUtil.isNullString(allowedDays)) {
			if (!BTSLUtil.isValidateAllowedDays(allowedDays)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_ALLOWEDDAYS_INVALID);
			}
			channelUserVO.setAllowedDays(allowedDays);
		}

		final String fromTime = requestVO.getData().getAllowedTimeFrom();
		final String toTime = requestVO.getData().getAllowedTimeTo();

		if (!BTSLUtil.isNullString(fromTime)) {
			if (!BTSLUtil.isValidateAllowedTime(fromTime)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_FROMTIME_INVALID);
			}
			channelUserVO.setFromTime(fromTime);
		}

		if (!BTSLUtil.isNullString(toTime)) {
			if (!BTSLUtil.isValidateAllowedTime(toTime)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_TOTIME_INVALID);
			}
			channelUserVO.setToTime(toTime);
		}
		
		if(commonUtil.timeDifference(fromTime, toTime)<0) {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FROMTIME_GRT_TOTIME);
		}
		
		if (!BTSLUtil.isNullString(channelUserVO.getFromTime()) && !BTSLUtil.isNullString(channelUserVO.getToTime())) {
			if (channelUserVO.getFromTime().equals(channelUserVO.getToTime())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TIME_RANGE);
			}
		}

	}
	
	private void addUser(Connection con) throws BTSLBaseException, SQLException {
		final String methodName = "addUser";
		UserDAO userDAO = new UserDAO();
		// insert in to user table
		final int userCount = userDAO.addUser(con, channelUserVO);
		if (userCount <= 0) {
			con.rollback();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
					methodName, "", "", "", "Exception:userCount <=0 ");
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
		}
	}
	
	private void modifyUser(Connection con) throws BTSLBaseException,SQLException {
		final String methodName = "addUser";
		// insert in to user table
		channelUserVO.setUserID(existingDBchannelUserVO.getUserID());
		channelUserVO.setAllowedUserTypeCreation(existingDBchannelUserVO.getAllowedUserTypeCreation());
		channelUserVO.setPassword(existingDBchannelUserVO.getPassword());
		channelUserVO.setRsaFlag(existingDBchannelUserVO.getRsaFlag());
		final int userCount = userDAO.updateUser(con, channelUserVO);
		if (userCount <= 0) {
			con.rollback();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
					methodName, "", "", "", "Exception:userCount <=0 ");
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
		}
	}
	
	
	private void modifyUser(Connection con,ChannelUserVO channelUserVO) throws BTSLBaseException,SQLException {
		final String methodName = "addUser";
		// insert in to user table
		
		final int userCount = userDAO.updateUser(con, channelUserVO);
		if (userCount <= 0) {
			con.rollback();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
					methodName, "", "", "", "Exception:userCount <=0 ");
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
		}
	}


	
	private void addUserPhoneList(ChannelUserApprovalReqVO requestVO,Connection con,ChannelUserVO senderVO) throws BTSLBaseException, SQLException {
		final String methodName = "addUserPhoneList";
	
		 
		final Date currentDate = new Date();
		try {
		 userPhoneList = (ArrayList) prepareUserPhoneVOList(con, requestVO, currentDate, senderPin,senderVO);
		}catch(Exception sqex) {
			throw new BTSLBaseException(this, methodName, sqex.getMessage(), sqex);
		}
	final int phoneCount = userDAO.addUserPhoneList(con, userPhoneList);
    if (phoneCount <= 0) {
        con.rollback();
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
            "ChannelUserRegZainNigeriaRequestHandler[process]", "", "", "", "Exception:Update count <=0 for user phones");
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
    }
	}
	
	private void checkUserSuspend(ChannelUserApprovalReqVO requestVO,Connection con, ChannelUserVO senderVO) throws BTSLBaseException, SQLException {	
		final String methodName = "checkUserSuspend";
		String inSuspend = requestVO.getData().getInsuspend();
		if (!BTSLUtil.isNullString(inSuspend)) {
			if (inSuspend.equals(PretupsI.YES) || inSuspend.equals(PretupsI.NO)) {
				channelUserVO.setInSuspend(inSuspend);
			} else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_INSUSPEND_INVALID);
			}
		} else {
			channelUserVO.setInSuspend("N");
		}
		String outSuspend = requestVO.getData().getOutsuspend();
		if (!BTSLUtil.isNullString(outSuspend)) {
			if (outSuspend.equals(PretupsI.YES) || outSuspend.equals(PretupsI.NO)) {
				channelUserVO.setOutSuspened(outSuspend);
			} else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_OUTSUSPEND_INVALID);
			}
		} else {
			channelUserVO.setOutSuspened("N");
		}   
	}
	
	private void  checkCommissionProfile(ChannelUserApprovalReqVO requestVO,Connection con,ChannelUserVO senderVO)throws BTSLBaseException{
		final String methodName = "checkCommissionProfile";
		String userCategeryCode = requestVO.getData().getUserCatCode();
		UserWebDAO userWebDAO = new UserWebDAO();
		ChannelUserWebDAO  channelUserWebDAO = new ChannelUserWebDAO();
		ArrayList commissionProfileList = userWebDAO.loadCommisionProfileListByCategoryIDandGeography(con,
				userCategeryCode, senderVO.getNetworkID(), null);

		final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
		// load the User Grade dropdown

		ArrayList gradelist = categoryGradeDAO.loadGradeList(con, userCategeryCode);

		// load the Transfer Profile dropdown
		final TransferProfileDAO profileDAO = new TransferProfileDAO();
		ArrayList transferprofilelist = profileDAO.loadTransferProfileByCategoryID(con, senderVO.getNetworkID(),
				userCategeryCode, PretupsI.PARENT_PROFILE_ID_USER);
		// load the Transfer Rule Type at User level
		ArrayList transferRuleTypeList = null;
		final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache
				.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, senderVO.getNetworkID(), userCategeryCode))
						.booleanValue();
		if (isTrfRuleTypeAllow) {
			transferRuleTypeList = (LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
		}
		
		  ArrayList LmsProfileList=null;
          if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
              LmsProfileList=channelUserWebDAO.getLmsProfileList(con, senderVO.getNetworkID());
          }
      
          boolean flag=true;
          String userGrade =null;
          String lmsProfileID = null,defaultCommissionProfileSetID=null,defaultTransferProfileID=null,transferTypeRuleId=null;

		boolean approverCanEdit = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT)).booleanValue();
		int userApprovalLevel = ((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(), userCategeryCode)).intValue();

		boolean profileFlag;
		if (channelUserVO.getStatus().equals(PretupsI.NEW)) {
			profileFlag = !approverCanEdit || userApprovalLevel == 0;
		} else {
			profileFlag = approverCanEdit && userApprovalLevel > 0;
		}

		if(profileFlag) {
			userGrade = requestVO.getData().getUsergrade();
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
          	defaultCommissionProfileSetID = requestVO.getData().getCommissionProfileID();
          	if(BTSLUtil.isNullString(defaultCommissionProfileSetID))
          	{
          		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMMISSION_SET_ID_INVALID);
          	}
          	else
          	{
          		flag=true;
          		for(int i=0;i<commissionProfileList.size();i++)
          		{
          			if(((CommissionProfileSetVO)commissionProfileList.get(i)).getCommProfileSetId().equals(defaultCommissionProfileSetID))
          			{
          				flag=false;
          			}
          		}
          		if(flag==true)
          		{
          			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMMISSION_SET_ID_INVALID);
          		}
          	}
          	channelUserVO.setCommissionProfileSetID(defaultCommissionProfileSetID);
          	defaultTransferProfileID=requestVO.getData().getTransferProfile();
          	if(BTSLUtil.isNullString(defaultTransferProfileID))
          	{
          		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_TRANSFER_PROFILE_NOT_EXIST,new String[]{channelUserVO.getUserName()});
          	}

          	else{
          		flag=true;
      		for(int i=0;i<transferprofilelist.size();i++)
      		{
      			if(((ListValueVO)transferprofilelist.get(i)).getValue().equals(defaultTransferProfileID))
      			{
      				flag=false;
      			}
      		}
      		if(flag==true)
      		{
      			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_TRANSFER_PROFILE_NOT_EXIST,new String[]{channelUserVO.getUserName()});
      		}
          	}
          
          	channelUserVO.setTransferProfileID(defaultTransferProfileID);
          	transferTypeRuleId=requestVO.getData().getTransferRuleType();
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
          			if(((ListValueVO)transferRuleTypeList.get(i)).getValue().equals(transferTypeRuleId))
          			{
          				flag=false;
          			}
          		}
          		if(flag==true)
          		{
          			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_TYPE_NOT_EXIST);
          		}
              	}
          		channelUserVO.setTrannferRuleTypeId(transferTypeRuleId);
          	}
               }
          	lmsProfileID=requestVO.getData().getLmsProfileId();
          	if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
          		
          		String controlGroupRequired = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
      			if(controlGroupRequired == null || controlGroupRequired == ""){
      				controlGroupRequired="Y";
      			}
      			if("Y".equals(controlGroupRequired)) {
      				channelUserVO.setControlGroup(requestVO.getData().getControlGroup());
      			}
          		if(BTSLUtil.isNullString(lmsProfileID))
              	{
              		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LMS_SETID_NOT_FOUND);
              	}
          		else{

              		flag=true;
          		for(int i=0;i<LmsProfileList.size();i++)
          		{
          			if(((ListValueVO)LmsProfileList.get(i)).getValue().equals(requestVO.getData().getLmsProfileId()))
          			{
          				flag=false;
          			}
          		}
          		if(flag==true)
          		{
          			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LMS_SETID_NOT_FOUND);
          		}
              	
          		}
                  channelUserVO.setLmsProfile(requestVO.getData().getLmsProfileId());
              }
          	
          }

	}
	

	private void checkLMSProfile(Connection con) throws BTSLBaseException {
		final String methodName ="checkLMSProfile";
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
	}
	
	
	private void  checkServiceList(ChannelUserApprovalReqVO requestVO,Connection con) throws BTSLBaseException {
		final String methodName ="checkServiceList";
        String services =  requestVO.getData().getServices();
        final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
        ListValueVO listValueVO = null;
        List serviceList = null;
        try {
        	
        	 if(services==null || (services!=null && services.trim().length()==0)) {
        		   throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICES_MANDATORY);
        	 }
        	
            serviceList = servicesTypeDAO.loadServicesList(con,  requestVO.getData().getExtnwcode(), PretupsI.C2S_MODULE, requestVO.getData().getUserCatCode(), false);
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
                	 if(!requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
                		 servicesTypeDAO.deleteUserServices(con, channelUserVO.getUserID());
                      }
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
                	if(!requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
               		 servicesTypeDAO.deleteUserServices(con, channelUserVO.getUserID());
                     }
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

	}

	
	private void  checkSuperadminServiceList(ChannelUserApprovalReqVO requestVO,Connection con,ChannelUserVO senderVO) throws BTSLBaseException {
		final String methodName ="checkSuperadminServiceList";
        String services =  requestVO.getData().getServices();
        final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
        ListValueVO listValueVO = null;
        List serviceList = null;
        try {
        	
        	 if(services==null || (services!=null && services.trim().length()==0)) {
        		   throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICES_MANDATORY);
        	 }

        	 if (PretupsI.OPERATOR_CATEGORY.equalsIgnoreCase(requestVO.getData().getUserCatCode())|| PretupsI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(requestVO.getData().getUserCatCode())) {
        		 serviceList  =servicesTypeDAO.assignServicesToChlAdmin(con, senderVO.getNetworkID());
             } else {
            	 serviceList =servicesTypeDAO.loadServicesList(con, senderVO.getNetworkID());
             }
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
                	if(requestVO.getUserAction().equals(PretupsI.OPERATION_EDIT) && requestVO.getData().getUserCatCode().equals(PretupsI.BCU_USER))
                	{
                		 servicesTypeDAO.deleteUserServices(con, channelUserVO.getUserID());
                	}
                	 if(!requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
                		 servicesTypeDAO.deleteUserServices(con, channelUserVO.getUserID());
                      }
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
                	if(!requestVO.getApprovalLevel().equals(PretupsI.NEW)) {
               		 servicesTypeDAO.deleteUserServices(con, channelUserVO.getUserID());
                     }
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

	}

	
	@Async
	private void  convertReqGroupRoleToMap(ChannelUserApprovalReqVO requestVO) {
		requestGroupHashMap= new HashMap<String,String>();
		String groupRole =  requestVO.getData().getGrouprole();
        String [] groupRoles = groupRole.split(",");
        if(groupRoles!=null && groupRoles.length>0) {
	        for(String str:groupRoles) {
	        	requestGroupHashMap.put(str.toUpperCase(),str.toUpperCase());
	        }
        }
   }
	
	private void checkGroupRole(ChannelUserApprovalReqVO requestVO,Connection con,CategoryVO categoryVO) throws BTSLBaseException  {
		final String methodName ="checkGroupRole";
        String groupRole =  requestVO.getData().getGrouprole();
        String [] groupRoles = groupRole.split(",");
    
        
        if (PretupsI.YES.equalsIgnoreCase(categoryVO.getWebInterfaceAllowed())) {
            final UserRolesDAO userRolesDAO = new UserRolesDAO();
                if(!BTSLUtil.isNullString(groupRole)){
                	 Map rolesMap =userRolesDAO.loadRolesListByGroupRole(con, requestVO.getData().getUserCatCode(), requestVO.getData().getRoleType());
                     Map rolesMapNew=new HashMap();
                     Set rolesKeys = rolesMap.keySet();
                     ArrayList rolesListNew=new ArrayList();
                     ArrayList grouproles = new ArrayList<>();
                     Iterator keyiter = rolesKeys.iterator();
                    while(keyiter.hasNext()){
                        String rolename=(String)keyiter.next();
                        ArrayList rolesVOList=(ArrayList)rolesMap.get(rolename);
                        rolesListNew=new ArrayList();
                        Iterator i=rolesVOList.iterator();
                        while(i.hasNext()){
                              UserRolesVO rolesVO=(UserRolesVO)i.next();
                              if(rolesVO.getStatus().equalsIgnoreCase("Y"))
                              {
                                    rolesListNew.add(rolesVO);
                                    grouproles.add(rolesVO.getRoleCode());
                              }
                        }
                        if(rolesListNew.size()>0){
                      	  rolesMapNew.put(rolename, rolesListNew);
                        }
                  }
                    if (rolesMapNew != null && rolesMapNew.size() > 0) {
                        // this method populate the selected roles
                    	final HashMap newSelectedMap = new HashMap();
                        final Iterator it = rolesMapNew.entrySet().iterator();
                        String key = null;
                        ArrayList list = null;
                        ArrayList listNew = null;
                        UserRolesVO roleVO = null;
                        Map.Entry pairs = null;
                        boolean foundFlag = false;

                        while (it.hasNext()) {
                            pairs = (Map.Entry) it.next();
                            key = (String) pairs.getKey();
                            list = new ArrayList((ArrayList) pairs.getValue());
                            listNew = new ArrayList();
                            foundFlag = false;
                            if (list != null) {
                                for (int i = 0, j = list.size(); i < j; i++) {
                                    roleVO = (UserRolesVO) list.get(i);
//                                    if (groupRoles != null && groupRoles.length > 0) {
//                                        for (int k = 0; k < groupRoles.length; k++) {
//                                            if (roleVO.getRoleCode().equals(groupRoles[k])) {
//                                                listNew.add(roleVO);
//                                                foundFlag = true;
//                                                channelUserVO.setRoleType(roleVO.getGroupRole());
//                                                break;
//                                            }
//                                        }
//                                    }
                                    if(requestGroupHashMap!=null && requestGroupHashMap.containsKey(roleVO.getRoleCode().toUpperCase())){
                                    	  listNew.add(roleVO);
                                    	  	foundFlag = true;
                                    	  	channelUserVO.setRoleType(roleVO.getGroupRole());
                                    }
                                    
                                    
                                    
                                }
                            }
                            if (foundFlag) {
                                newSelectedMap.put(key, listNew);
                            }
                        }
                        if (newSelectedMap.size() > 0) {
                        	channelUserVO.setRolesMapSelected(newSelectedMap);
                        } else {
                            // by default set Role Type = N(means System Role radio button will
                            // be checked in edit mode if no role assigned yet)
                            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.SYSTEM)) {
                            	channelUserVO.setRoleType("N");
                            } else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.GROUP)) {
                            	channelUserVO.setRoleType("Y");

                            } else {
                            	channelUserVO.setRoleType("N");
                            }
                            channelUserVO.setRolesMapSelected(null);
                        }

                    }
                    boolean isGroupRoleValid=true;
                     String requestGroupRoleValue=null;
                    for (int i = 0; i < groupRoles.length; i++) {
                    	if(groupRoles[i]!=null) {
                    		requestGroupRoleValue=groupRoles[i].trim();	
                    	}
                        if(!grouproles.contains(requestGroupRoleValue)){
                        	LOG.debug("AddChannelUserController process : ", " Role = " + groupRoles[i] + "Not found");
                        	isGroupRoleValid = false;
                        	break;
                        }
                    }
                    if(isGroupRoleValid){
                    	
                    	    userRolesDAO.deleteUserRoles(con, channelUserVO.getUserID());
                    	
                            int userRoles=userRolesDAO.addUserRolesList(con, channelUserVO.getUserID(), groupRoles);
                            if (userRoles <= 0) {
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                            }
                    } else
                    {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                    }
                }
            
        }

	}

	private List checkTransferRule(Connection con, ChannelUserVO senderVO, CategoryVO categoryVO1, String domain)
			throws BTSLBaseException {

		C2STransferDAO c2sTransferDAO = new C2STransferDAO();
		CategoryDAO categoryDAO = new CategoryDAO();
		ArrayList list = new ArrayList();
		final ChannelUserVO channelUserSessionVO = senderVO;
		ArrayList OrigininalCatList = new ArrayList<>();
		if (!PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID()))
			OrigininalCatList = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
		else
			OrigininalCatList = categoryDAO.loadOtherCategorList(con, senderVO.getDomainID());
		if (BTSLUtil.isNullOrEmptyList(OrigininalCatList)) {
			throw new BTSLBaseException("AddChannelUser", "checkTransferRule", PretupsErrorCodesI.NO_DOMAIN_FOUND,
					senderVO.getDomainID());
		}
		ArrayList list1 = new ArrayList();
		CategoryVO categoryVOc = null;
		if (PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())) {
			if (OrigininalCatList != null && !BTSLUtil.isNullString(domain)) {
				for (int i = 0, j = OrigininalCatList.size(); i < j; i++) {
					categoryVOc = (CategoryVO) OrigininalCatList.get(i);
					// here value is the combination of categoryCode,domain_code
					// and sequenceNo so we split the value
					if (categoryVOc.getDomainCodeforCategory().equals(domain)) {
						list1.add(categoryVOc);
					}
				}
			}
		} else
			for (int i = 0, j = OrigininalCatList.size(); i < j; i++) {
				categoryVOc = (CategoryVO) OrigininalCatList.get(i);
				if (categoryVO1.getSequenceNumber() > channelUserSessionVO.getCategoryVO().getSequenceNumber()) {
					list1.add(categoryVOc);
				}
			}
		boolean flag12 = false;
		for (int i = 0; i < list1.size(); i++) {
			if (((CategoryVO) list1.get(i)).getCategoryCode().equals(categoryVO1.getCategoryCode())) {
				flag12 = true;
				break;
			}
		}
		if (flag12 == false) {
			throw new BTSLBaseException("AddChannelUser", "checkTransferRule",
					PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
		}
		List originalParentCategoryList = c2sTransferDAO.loadC2SRulesListForChannelUserAssociation(con,
				channelUserSessionVO.getNetworkID());
		final String[] categoryID = new String[3];
		categoryID[2] = String.valueOf(categoryVO1.getSequenceNumber());
		categoryID[0] = categoryVO1.getCategoryCode();
		/*
		 * OrigParentCategory List contains all(Associated C2S Transfer Rules category)
		 * FromCategory and ToCategory information like
		 * 
		 * Dist -> Ret(Disttributor can transfer to retailer and parentAssociationFlag =
		 * Y) The above rule state while adding Retailer parent category can be
		 * Distributor
		 */
		if (originalParentCategoryList != null && !BTSLUtil.isNullString(categoryVO1.getCategoryCode())) {
			CategoryVO categoryVO = null;
			ChannelTransferRuleVO channelTransferRuleVO = null;
			for (int i = 0, j = OrigininalCatList.size(); i < j; i++) {
				categoryVO = (CategoryVO) OrigininalCatList.get(i);
				/*
				 * If Sequence No == 1 means root owner is adding(suppose Distributor) at this
				 * time pagentCategory and category both will be same, just add the categoryVO
				 * into the parentCategoryList
				 */
				if ("1".equals(categoryID[2]) && categoryID[0].equals(categoryVO.getCategoryCode())) {
					list = new ArrayList();
					list.add(categoryVO);
					break;
				}
				/*
				 * In Case of channel admin No need to check the sequence number In Case of
				 * channel user we need to check the sequence number
				 */
				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
					for (int m = 0, n = originalParentCategoryList.size(); m < n; m++) {
						channelTransferRuleVO = (ChannelTransferRuleVO) originalParentCategoryList.get(m);
						/*
						 * Here three checks are checking Add those category into the list where
						 * a)FormCategory(origPatentList) = categoryCode(origcategoryList)
						 * b)selectedCategory(categoryID[0] = ToCategory(origParentCategoryList)
						 * c)selectedCategory(categoryID[0] != FromCategory(origParentCategoryList)
						 */
						if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory())
								&& categoryID[0].equals(channelTransferRuleVO.getToCategory())
								&& !categoryID[0].equals(channelTransferRuleVO.getFromCategory())) {
							list.add(categoryVO);
						}
					}
				} else {
					for (int m = 0, n = originalParentCategoryList.size(); m < n; m++) {
						channelTransferRuleVO = (ChannelTransferRuleVO) originalParentCategoryList.get(m);
						/*
						 * Here three checks are checking Add those category into the list where
						 * a)FormCategory(origPatentList) = categoryCode(origcategoryList)
						 * b)selectedCategory(categoryID[0] = ToCategory(origParentCategoryList)
						 * c)selectedCategory(categoryID[0] != FromCategory(origParentCategoryList)
						 */

						if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory())
								&& categoryID[0].equals(channelTransferRuleVO.getToCategory())
								&& !categoryID[0].equals(channelTransferRuleVO.getFromCategory())) {
							if (categoryVO.getSequenceNumber() >= channelUserSessionVO.getCategoryVO()
									.getSequenceNumber()) {
								list.add(categoryVO);
							}
						}
					}
				}
			}
			if (list.isEmpty()) {
				throw new BTSLBaseException("AddChannelUser", "checkTransferRule",
						PretupsErrorCodesI.EXTSYS_REQ_TRF_RULE_NOT_ALLOWED);
			}
		}

		return list;

	}

	private String generateUserId(String networkCode, String prefix) throws BTSLBaseException {
		StringBuilder loggerValue = new StringBuilder();
		loggerValue.setLength(0);
		loggerValue.append("Entered p_networkCode=");
		loggerValue.append(networkCode);
		loggerValue.append(networkCode);
		loggerValue.append(", p_prefix=");
		loggerValue.append(prefix);
		LogFactory.printLog("generateUserId", loggerValue.toString(), LOG);

		final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
		String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, networkCode)) + "",
				length);
		id = networkCode + prefix + id;

		loggerValue.setLength(0);
		loggerValue.append("Exiting id=");
		loggerValue.append(id);
		LogFactory.printLog("generateUserId", loggerValue.toString(), LOG);

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
    private List prepareUserPhoneVOList(Connection con, ChannelUserApprovalReqVO requestVO, Date currentDate, String senderPin,ChannelUserVO senderVO) throws Exception {
        final String methodName = "prepareUserPhoneVOList";
        LogFactory.printLog(methodName, "Entered channelUserVO.getCategoryCode()=" + channelUserVO.getCategoryCode(), LOG);
    	
        final List <UserPhoneVO>phoneList = new ArrayList<UserPhoneVO>();
        List <Msisdn>msisdnList=new ArrayList();
        NetworkPrefixVO networkPrefixVO = null;
        Msisdn [] msisdns=requestVO.getData().getMsisdn();
        Msisdn m = new Msisdn();
        for(int i=0;i<msisdns.length;i++)
        {
        	m=msisdns[i];
        	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW))).booleanValue())
       	 {
                m.setPin(operatorUtili.generateRandomPin());
       	 }
        	msisdnList.add(m);
        }
        String stkProfile = null;
        final List stkProfileList = userDAO.loadPhoneProfileList(con, channelUserVO.getCategoryCode());
        if (stkProfileList != null && !stkProfileList.isEmpty()) {
            final ListValueVO listValueVO = (ListValueVO) stkProfileList.get(0);
            stkProfile = listValueVO.getValue();
        }
        Msisdn msisdn= null;
        if (msisdnList != null && !msisdnList.isEmpty()) {
            UserPhoneVO phoneVO = null;
            int primaryMobileNumCounter=0;
            for (int i = 0, j = msisdnList.size(); i < j; i++) {
                msisdn = (Msisdn) msisdnList.get(i);
                if (!BTSLUtil.isNullString(msisdn.getPhoneNo())) {
                    phoneVO = new UserPhoneVO();
                    phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(msisdn.getPhoneNo()));
                    phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                    phoneVO.setUserId(channelUserVO.getUserID());
                    //decrypt for ui request,encrypt for DB 
                    phoneVO.setSmsPin(BTSLUtil.encryptText(AESEncryptionUtil.aesDecryptor(msisdn.getPin(), Constants.A_KEY)));
                    phoneVO.setPinRequired(PretupsI.YES);
                    // set the default values
                    phoneVO.setCreatedBy(senderVO.getActiveUserID());
                    phoneVO.setModifiedBy(senderVO.getActiveUserID());
                    phoneVO.setCreatedOn(currentDate);
                    phoneVO.setModifiedOn(currentDate);
                    phoneVO.setPinModifiedOn(currentDate);
                    ArrayList languageList=LocaleMasterDAO.loadLocaleMasterData();
                    if (!BTSLUtil.isNullString(requestVO.getData().getLanguage())) {
                    boolean flag1=true;
                    for(int k=0;k<languageList.size();k++)
                    {
                    	if(((ListValueVO)languageList.get(k)).getValue().equalsIgnoreCase(requestVO.getData().getLanguage()))
                    	{
                    		flag1=false;
                    	}
                    }
                    if(flag1==true)
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.LANGUAGE_DOES_NOT_EXIST);
                    }
                    }
                    if (!BTSLUtil.isNullString(requestVO.getData().getLanguage())) {
                        final String lang_country[] = (requestVO.getData().getLanguage()).split("_");
                        phoneVO.setPhoneLanguage(lang_country[0]);
                        phoneVO.setCountry(lang_country[1]);
                    } else {
                        phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                        phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    }
                    
                    if(channelUserVO.getCategoryCode().equals(PretupsI.BCU_USER) ||channelUserVO.getCategoryCode().equals(PretupsI.CUSTOMER_CARE)) {
                    	msisdn.setStkProfile(channelUserVO.getCategoryCode());	
                    }
                    
                    if(msisdn.getStkProfile()==null)
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_FOUND);
                    }
                    else if(!(msisdn.getStkProfile().equals(stkProfile)))
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_VALID);
                    }
                    phoneVO.setPhoneProfile(msisdn.getStkProfile());
                    phoneVO.setDescription(msisdn.getDescription());
                    if(!(BTSLUtil.isValidMSISDN(phoneVO.getMsisdn())))
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
                    }
                    if(!(BTSLUtil.isValidMSISDNDigit(phoneVO.getMsisdn())))
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
                    }
                    if(!(BTSLUtil.isValidMSISDNLength(phoneVO.getMsisdn())))
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.EXTSYS_MSISDN_MAP_LENGTH_EXCEEDS);
                    }
                    final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL
                        .getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
                    if((prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserVO.getNetworkCode()))) {
                        throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
                    }
                    phoneVO.setPrefixID(prefixVO.getPrefixID());
                    if (msisdn.getIsprimary().equals("Y")){
                        channelUserVO.setMsisdn(msisdn.getPhoneNo());
                        phoneVO.setPrimaryNumber(PretupsI.YES);
                        primaryMobileNumCounter=primaryMobileNumCounter+1;
                        
                         if(primaryMobileNumCounter>1) {
                        	 throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.MULTIPLE_PRIMARY_MOBILENUM);
                         }
                        
                    } else {
                        phoneVO.setPrimaryNumber(PretupsI.NO);
                    }

                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(msisdn.getPhoneNo()));
                        boolean numberAllowed = false;
                        if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn.getPhoneNo(), "", PretupsI.PORTED_IN);
                            if (!numberAllowed) {
                                throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_IN);
                            }
                        } else {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn.getPhoneNo(), "", PretupsI.PORTED_OUT);
                            if (numberAllowed) {
                                throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_OUT);
                            }
                        }
                    }
                    // MNP Code End
                    
                    phoneVO.setPinModifyFlag(true);
                    if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                        final String[] arr = { phoneVO.getMsisdn() };
                        throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                    }
                    phoneList.add(phoneVO);
                }
            }
            
            
            if( primaryMobileNumCounter == 0 ) {
            	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.ATLEAST_ONE_PRIMARYMSISDN);
            }
        }
        channelUserVO.setMsisdnList((ArrayList)phoneList);
        return phoneList;
    }
    
    
    private void addChannelUser(Connection con, ChannelUserVO senderVO) throws BTSLBaseException {
        final String methodName="addChannelUser";
        boolean rsaRequired = false;
        try {
			rsaRequired = BTSLUtil.isRsaRequired(senderVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        channelUserVO.setRsaRequired(rsaRequired);
        // Low Balance Alert Allow

        // Insert data into channel users table
        final int userChannelCount = channelUserDao.addChannelUser(con, channelUserVO);
        if (userChannelCount <= 0) {
            try {
                con.rollback();
            	}catch(Exception ex) {
            		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);	
            	}
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
            if(!(channelUserVO.getCategoryCode().equals(PretupsI.CUSTOMER_CARE) ||channelUserVO.getCategoryCode().equals(PretupsI.BCU_USER))) {
            geoList.add(userGeographiesVO);
            final int addUserGeo = new UserGeographiesDAO().addUserGeographyList(con, geoList);
            if (addUserGeo <= 0) {
            	try {
                con.rollback();
            	}catch(Exception ex) {
            		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);	
            	}
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                    "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
            }
            }
        }

    }
    
    
   private void checkVoucherType(Connection con, ChannelUserApprovalReqVO requestVO) throws BTSLBaseException {
	   String methodName="checkVoucherType";
	   String voucherType = requestVO.getData().getVoucherTypes();
       if(!(BTSLUtil.isNullString(voucherType)))
       { 
       	String []voucherTypes = voucherType.split(",");
       if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue() && ((Boolean)PreferenceCache.getControlPreference(PreferenceI.CHNLUSR_VOUCHER_CATGRY_ALLWD, channelUserVO.getNetworkID(), requestVO.getData().getUserCatCode())).booleanValue())
       {
       	if (voucherTypes != null && voucherTypes.length > 0) {
           
           VomsProductDAO voucherDAO = new VomsProductDAO();
           ArrayList <ListValueVO>voucherList = new ArrayList();
           voucherList = voucherDAO.loadVoucherTypeList(con);
           ArrayList voucTypes = new ArrayList<>();
           for(int i=0;i<voucherList.size();i++)
           {
           	voucTypes.add(voucherList.get(i).getValue());
           	
           }
           for(int i=0;i<voucherTypes.length;i++)
       	{
       		if(!(voucTypes.contains(voucherTypes[i])))
       		{
       			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_TYPE_INVALID);
       		}
       		
       	}
           if(!(PretupsI.NEW.equals(requestVO.getApprovalLevel()) && requestVO.getUserAction().equals(PretupsI.OPERATION_ADD))){
        	   userWebDAO.deleteUserVoucherTypes(con, channelUserVO.getUserID());
           }
           
           int userVoucherTypeCount = voucherDAO.addUserVoucherTypeList(con, channelUserVO.getUserID(), voucherTypes, PretupsI.YES);
           if (userVoucherTypeCount <= 0) {
               try {
                   con.rollback();
               } catch (SQLException e) {
                   LOG.errorTrace(methodName, e);
               }
               LOG.error("addUserInfo", "Error: while Inserting User voucher type Info");
               throw new BTSLBaseException(this, "addUserInfo", "error.general.processing");
           }
       	}
       }
       }
       try {
		con.commit();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
        throw new BTSLBaseException(this, "addUserInfo", "error.general.processing");
	}
   }
   
   
   private void checkVoucherSegments(Connection con, ChannelUserApprovalReqVO requestVO) throws BTSLBaseException {
	   
	   String methodName="checkVoucherType";
	   String voucherSegments = requestVO.getData().getVoucherSegments();
       if(!(BTSLUtil.isNullString(voucherSegments)))
       {	 
       	String []voucherSegmentArr = voucherSegments.split(",");
   
   VomsProductDAO voucherDAO = new VomsProductDAO();
   if(!PretupsI.NEW.equals(requestVO.getApprovalLevel())) {
	   voucherDAO.deleteUserVoucherSegments(con, channelUserVO.getUserID());
   }

   int userSegmentCount = voucherDAO.addUserVoucherSegmentList(con, channelUserVO.getUserID(), voucherSegmentArr, PretupsI.YES);
   if (userSegmentCount <= 0) {
       try {
    	   con.rollback();
       } catch (SQLException e) {
           LOG.errorTrace(methodName, e);
       }
       LOG.error("checkVoucherSegments", "Error: while Inserting User voucher segment Info");
       throw new BTSLBaseException(this, "checkVoucherSegments", "error.general.processing");
   }
   
       }
   
   }
   
   
   
	private void checkUserProducts(Connection con, ChannelUserApprovalReqVO requestVO) throws BTSLBaseException {
		final String methodName = "checkUserProducts";
		ProductTypeDAO productTypeDAO = new ProductTypeDAO();
		
		if(null==requestVO.getData().getUserProducts() || (requestVO.getData().getUserProducts()!=null && requestVO.getData().getUserProducts().trim().length()==0 )) {
			LOG.error(methodName, "Error: Product information is mandatory for Super channel admin");
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PRODUCT_INFO_MANDATORY_SUBCU);
		}
		
		if(requestVO.getUserAction().equals(PretupsI.OPERATION_EDIT) && requestVO.getData().getUserCatCode().equals(PretupsI.BCU_USER))
		{
			productTypeDAO.deleteUserProducts(con, channelUserVO.getUserID());
		}
		

		if (!PretupsI.NEW.equals(requestVO.getApprovalLevel())) {
			productTypeDAO.deleteUserProducts(con, channelUserVO.getUserID());
		}

		if (requestVO.getData().getUserProducts() != null && requestVO.getData().getUserProducts().length() > 0) {

			String[] userProductsArr = requestVO.getData().getUserProducts().split(",");
			for(int i=0;i<userProductsArr.length;i++) {
				 if(!commonUtil.validLookupCodeByLookupType(userProductsArr[i], PretupsI.PRODUCT_TYPE)) {
					 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING);
				 }
				
			}
			
			
			
			
			int productCount = productTypeDAO.addUserProductsList(con, channelUserVO.getUserID(), userProductsArr);
			if (productCount <= 0) {
				try {
					con.rollback();
				} catch (SQLException e) {
					LOG.errorTrace(methodName, e);
				}
				LOG.error("checkUserProducts", "Error: while Inserting User Product Info");
				throw new BTSLBaseException(this, "checkUserProducts", "error.general.processing");
			}
		}
	}
	
	
	private void checkUserDomainCodes(Connection con, ChannelUserApprovalReqVO requestVO) throws BTSLBaseException {
	// insert domains info
	final String methodName ="checkUserDomainCodes";
	DomainDAO domainDAO = new DomainDAO();
	 
    if (requestVO.getData().getUserDomainCodes() != null && requestVO.getData().getUserDomainCodes().length() > 0) {
		String[] userDomainArr = requestVO.getData().getUserDomainCodes().split(",");
		 ArrayList<ListValueVO> listArrayList = domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE);
			if(listArrayList==null) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_LIST_NOT_FOUND);
			}
		 for(int i=0;i<userDomainArr.length;i++) {
			 commonUtil.checkDomainValues(userDomainArr[i], listArrayList);
		 }
		 
		 if(requestVO.getUserAction().equals(PretupsI.OPERATION_EDIT) && requestVO.getData().getUserCatCode().equals(PretupsI.BCU_USER))
		 {
			 domainDAO.deleteUserDomains(con, channelUserVO.getUserID());
		 }
		 
		 	if(!PretupsI.NEW.equals(requestVO.getApprovalLevel())) {
			 	domainDAO.deleteUserDomains(con, channelUserVO.getUserID());
			   }
		 
        int domainCount = domainDAO.addUserDomainList(con, channelUserVO.getUserID(), userDomainArr);
        if (domainCount <= 0) {
            try {
            	con.rollback();
            } catch (SQLException e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error("addUserInfo", "Error: while Inserting User Domain Info");
            throw new BTSLBaseException(this, "addUserInfo", "error.general.processing");
        }
        //p_userVO.setDomainCodes(theForm.getDomainCodes());
    }else {
    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BLANK_DOMAIN);
    }
	
	}
	
   
	// sms message processing for Channel users.
   private void smsEmailMessageProcessingforCUs(Connection con,ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO) throws BTSLBaseException {
	   /*requestMap.put("CHNUSERVO", channelUserVO);*/
       // Push Messages
       phoneVO = null;
       if(userPhoneList!=null ) {
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
       }
       LOG.debug("AddChannelUserController process : ", " channelUserVO.getStatus() = " + channelUserVO.getStatus());
       BTSLMessages btslPushMessage = null;
       String paymentNumber = "";
       if (PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
           paymentNumber = null;

       }
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
                                   "Email has ben delivered recently", channelUserVO, senderVO);
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
                                   "Email will be delivered shortly", channelUserVO, senderVO);
                   emailSendToUser.sendMail();
                   response.setMessageCode(PretupsErrorCodesI.USER_WEB_ACTIVATE);
                   messageaArguments=arrArray;
               }
               
           }
           
           	else {
                   // send message for sms pin
              
                   final String[] arrArray1 = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };
                   
                   // End Zebra and Tango

                   btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                   final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                   pushMessage.push();
                   // Email for pin & password- code for email details
                   if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                       final String arrOne[] = { channelUserVO.getMsisdn() };
                       final String subject = BTSLUtil.getMessage(locale, "subject.user.regmsidn.massage", arrOne);
                       final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
                                       "Email will be delivered shortly", channelUserVO, senderVO);
                       emailSendToUser.sendMail();
                       response.setMessageCode(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE);
                       messageaArguments=arrArray1;
                   }
               }
           
           // pusing individual sms
           if (channelUserVO.getMsisdnList() != null && channelUserVO.getMsisdnList().size() > 1) {
               // it mean that it has secondary number and now push
               // message to individual secondary no

               final ArrayList newMsisdnList = channelUserVO.getMsisdnList();
               UserPhoneVO newUserPhoneVO = null;
               // Email for pin & password
               String subject = null;
               EmailSendToUser emailSendToUser = null;
               final String tmpMsisdn = channelUserVO.getMsisdn();
               for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
                   btslPushMessage = null;
                   newUserPhoneVO = (UserPhoneVO) newMsisdnList.get(i);
                   if (TypesI.NO.equals(newUserPhoneVO.getPrimaryNumber())) {
                       final String[] arrArray1 = { newUserPhoneVO.getMsisdn(), "", BTSLUtil.decryptText(newUserPhoneVO.getSmsPin()) };
                       locale = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());
                       if (locale == null) {
                           locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                       }
                       btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                       final PushMessage pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID());
                       pushMessage.push();
                       // Email for pin & password- code for email
                       // details
                       if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                           subject = BTSLUtil.getMessage(locale, "subject.user.regmsidn.massage",
                                           new String[] { newUserPhoneVO.getMsisdn() });
                           channelUserVO.setMsisdn(newUserPhoneVO.getMsisdn());
                           emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
                                           "Email will be delivered shortly", channelUserVO, senderVO);
                           emailSendToUser.sendMail();
                           channelUserVO.setMsisdn(tmpMsisdn);
                           response.setMessageCode(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE);
                           messageaArguments=arrArray1;
                       }
                   }
               }
           }


       } else {

           btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
           response.setMessageCode(PretupsErrorCodesI.USER_WITH_APPROVAL_REQUIRED_ADDED);
       }
       if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID()))){
       	if(((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),requestVO.getData().getUserCatCode())).intValue()==0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()){
       	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
       	tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
       	}
       }
       
       if((requestVO.getData().getUserCatCode().equals(PretupsI.CUSTOMER_CARE) || requestVO.getData().getUserCatCode().equals(PretupsI.BCU_USER))) {
    	   OperatorUserLog.log("ADDOPTUSR", channelUserVO, senderVO, null);
       }else {
    	   ChannelUserLog.log("ADDCHNLUSR", channelUserVO, senderVO, true, null);
       }
       
       
       //response.setStatus(201);
		response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageaArguments));

   }
   
   private List prepareUserModifiedPhoneVOList(Connection con, ChannelUserApprovalReqVO requestVO, ChannelUserVO channelUserVO, ChannelUserVO senderVO,
   		ArrayList oldPhoneList, String senderPin) throws BTSLBaseException {
	   Date currentDate= new Date();
       final String methodName = "prepareUserPhoneVOList";
       LogFactory.printLog(methodName, "Entered oldPhoneList.size()=" + oldPhoneList.size() 
               , LOG);

       final List <UserPhoneVO>phoneList = new ArrayList<UserPhoneVO>();
       final List <Msisdn>newMsisdnList=new ArrayList<Msisdn>();
       Msisdn [] msisdns=requestVO.getData().getMsisdn();
       Msisdn m = new Msisdn();
       for(int i=0;i<msisdns.length;i++)
       {
       	m=msisdns[i];
       	newMsisdnList.add(m);
       }
       NetworkPrefixVO networkPrefixVO = null;
       Msisdn msisdn = null;
       String stkProfile = null;
       final String oldUserPhoneID = null;
       final List stkProfileList = userDAO.loadPhoneProfileList(con, channelUserVO.getCategoryCode());
       if (stkProfileList != null) {
           final ListValueVO listValueVO = (ListValueVO) stkProfileList.get(0);
           stkProfile = listValueVO.getValue();
       }
       if (newMsisdnList != null && !newMsisdnList.isEmpty()) {
       	
       	boolean[] msisdnDelete = new boolean[oldPhoneList.size()]; 
           UserPhoneVO phoneVO = null;
           UserPhoneVO oldPhoneVO = null;
           int i=0;
           int j=0;
           int primayNumberMultipleCheck=0;
           for (i = 0, j = newMsisdnList.size(); i < j; i++) {
               phoneVO = new UserPhoneVO();
               oldPhoneVO= null;
               msisdn = (Msisdn) newMsisdnList.get(i);
               phoneVO.setMsisdn(msisdn.getPhoneNo());
               phoneVO.setPinModifyFlag(true);
               phoneVO.setPhoneProfile(stkProfile);
               phoneVO.setShowSmsPin(newMsisdnList.get(i).getPin());
               
               
                 
               
               if (!BTSLUtil.isNullString(phoneVO.getMsisdn())) {

                   if (oldPhoneList != null && !oldPhoneList.isEmpty()) {
                   	for(int k=0;k<oldPhoneList.size();k++) {
                   		UserPhoneVO tempOldPhoneVO=(UserPhoneVO) oldPhoneList.get(k);
                   		if (!BTSLUtil.isNullString(tempOldPhoneVO.getMsisdn()) && phoneVO.getMsisdn().equals(tempOldPhoneVO.getMsisdn())) {
                   			msisdnDelete[k]=true;
                   			oldPhoneVO=(UserPhoneVO) oldPhoneList.get(k);
                   			if (!(phoneVO.getShowSmsPin().equals(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(oldPhoneVO.getSmsPin()))))) {
                                   phoneVO.setPinModifyFlag(true);
                               } else {
                                   phoneVO.setPinModifyFlag(false);
                               }
                               break;
                   		}
                   	}
//                       if (i < oldPhoneList.size()) {
//                           oldPhoneVO = (UserPhoneVO) oldPhoneList.get(oldPhoneList.size() - (i + 1));
//                       }
                   }
                   phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()));
                   if (oldPhoneVO != null) {
                       phoneVO.setUserPhonesId(oldPhoneVO.getUserPhonesId());
//                       phoneVO.setPinModifyFlag(false);
                       phoneVO.setOperationType("U");
                       
                       if (phoneVO.isPinModifyFlag() || phoneVO.getPinModifiedOn() == null) {
                           phoneVO.setPinModifiedOn(currentDate);
                       }
//                       phoneVO.setPinModifiedOn(oldPhoneVO.getPinModifiedOn());
                       phoneVO.setPinRequired(oldPhoneVO.getPinRequired());
                       phoneVO.setSmsPin(BTSLUtil.encryptText(msisdn.getPin()));
                       phoneVO.setDescription(oldPhoneVO.getDescription());
                       if(msisdn.getStkProfile()==null)
                       {
                       	throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_FOUND);
                       }
                       else if(!(msisdn.getStkProfile().equals(stkProfile)))
                       {
                       	throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_VALID);
                       }
                       phoneVO.setPhoneProfile(msisdn.getStkProfile());
                       phoneVO.setIdGenerate(false);
                   } else if (oldPhoneVO==null) {
                       phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                       phoneVO.setOperationType("I");
                       phoneVO.setPinModifyFlag(true);
                       phoneVO.setPinModifiedOn(currentDate);
                       phoneVO.setSmsPin(BTSLUtil.encryptText(msisdn.getPin()));
                       phoneVO.setIdGenerate(true);
                       phoneVO.setPinRequired(PretupsI.YES);
                       phoneVO.setDescription(msisdn.getDescription());
                       if(msisdn.getStkProfile()==null)
                       {
                       	throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_FOUND);
                       }
                       else if(!(msisdn.getStkProfile().equals(stkProfile)))
                       {
                       	throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_VALID);
                       }
                       phoneVO.setPhoneProfile(msisdn.getStkProfile());
                   }
                   if (!BTSLUtil.isNullString(phoneVO.getSmsPin())) {
                       /*
                        * modified by ashishT
                        * to set default **** as the pin on jsp.
                        */
                       if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                           phoneVO.setShowSmsPin("****");
                           phoneVO.setConfirmSmsPin("****");
                       }
                       // set the default value *****
                       else {
                           phoneVO.setShowSmsPin(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(phoneVO.getSmsPin())));
                           phoneVO.setConfirmSmsPin(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(phoneVO.getSmsPin())));
                       }
                   }
                   phoneVO.setUserId(channelUserVO.getUserID());
                   // set the default values
                   phoneVO.setCreatedBy(senderVO.getUserID());
                   phoneVO.setModifiedBy(senderVO.getUserID());
                   phoneVO.setCreatedOn(currentDate);
                   phoneVO.setModifiedOn(currentDate);
                   ArrayList languageList=null;
				try {
					languageList = LocaleMasterDAO.loadLocaleMasterData();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                   boolean flag1=true;
                   if (!BTSLUtil.isNullString(requestVO.getData().getLanguage())) {
                   for(int k=0;k<languageList.size();k++)
                   {
                   	if(((ListValueVO)languageList.get(k)).getValue().equalsIgnoreCase(requestVO.getData().getLanguage()))
                   	{
                   		flag1=false;
                   	}
                   }
                   if(flag1==true)
                   {
                   	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LANGUAGE_DOES_NOT_EXIST);
                   }
                   }
                   if (!BTSLUtil.isNullString(requestVO.getData().getLanguage())) {
                       final String lang_country[] = (requestVO.getData().getLanguage()).split("_");
                       phoneVO.setPhoneLanguage(lang_country[0]);
                       phoneVO.setCountry(lang_country[1]);
                   } else {
                       phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                       phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                   }
                   if(!(BTSLUtil.isValidMSISDN(phoneVO.getMsisdn())))
                   {
                   	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
                   }
                   if(!(BTSLUtil.isValidMSISDNDigit(phoneVO.getMsisdn())))
                   {
                   	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
                   }
                   if(!(BTSLUtil.isValidMSISDNLength(phoneVO.getMsisdn())))
                   {
                   	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_MSISDN_MAP_LENGTH_EXCEEDS);
                   }
                   phoneVO.setPinModifiedOn(currentDate);
                   final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL
                       .getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
                   phoneVO.setPrefixID(prefixVO.getPrefixID());
                   if (msisdn.getIsprimary().equals("Y")) {
                       channelUserVO.setMsisdn(msisdn.getPhoneNo());
                       phoneVO.setPrimaryNumber(PretupsI.YES);
                        primayNumberMultipleCheck=primayNumberMultipleCheck+1;
                       if( primayNumberMultipleCheck > 1 ) {
                          	 throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.MULTIPLE_PRIMARY_MOBILENUM);
                       }
                       
                   } else {
                       phoneVO.setPrimaryNumber(PretupsI.NO);
                   }
                   if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                       networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(msisdn.getPhoneNo()));
                       boolean numberAllowed = false;
                       if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                           numberAllowed = new NumberPortDAO().isExists(con, msisdn.getPhoneNo(), "", PretupsI.PORTED_IN);
                           if (!numberAllowed) {
                               throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_IN);
                           }
                       } else {
                           numberAllowed = new NumberPortDAO().isExists(con, msisdn.getPhoneNo(), "", PretupsI.PORTED_OUT);
                           if (numberAllowed) {
                               throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_OUT);
                           }
                       }
                   }
                   if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                       final String[] arr = { phoneVO.getMsisdn() };
                       throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                   }
                   if ((!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW))).booleanValue()) && phoneVO.isPinModifyFlag()) {

                       if (userDAO.checkPasswordHistory(con, PretupsI.USER_PIN_MANAGEMENT, phoneVO.getUserId(), PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()),
                                       BTSLUtil.encryptText(phoneVO.getShowSmsPin()))) {
                           LOG.error(methodName, "Error: Pin exist in password_history table");
                           throw new BTSLBaseException(this, methodName, "channeluser.changepin.error.pinhistory", 0, new String[] { String
                                           .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()), phoneVO.getMsisdn() }, "Detail");
                       }
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
           
           if( primayNumberMultipleCheck == 0 ) {
            	 throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.ATLEAST_ONE_PRIMARYMSISDN);
           }
           
           for(int k=0;k<msisdnDelete.length;k++) {
           	if(!msisdnDelete[k]) {
           	 oldPhoneVO = (UserPhoneVO) oldPhoneList.get(k);
           	 phoneVO = new UserPhoneVO();
                phoneVO.setUserPhonesId(oldPhoneVO.getUserPhonesId());
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
   
   
   private void smsEmailMessageforApproval(Connection con,ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO) throws BTSLBaseException {
	   boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
	   boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
       boolean autoPaymentMethod = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD);
       int maxApprovalLevel =((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(),
				requestVO.getData().getUserCatCode())).intValue();
       String subjectKey ="";
	   final String methodName ="smsEmailMessageforApprovalOne";
	   final String arr[] = { channelUserVO.getUserName(), "" };
           if (PretupsI.USER_STATUS_CANCELED.equals(channelUserVO.getStatus()))// reject
           // the
           // request
           {
        	   response.setMessageCode(PretupsErrorCodesI.USER_REJECTED_SUCCESSFULLY);
           } else// approve the request
           {
        		   
        		   if( maxApprovalLevel==commonUtil.getCurrentApprovalLevel(requestVO.getApprovalLevel())) {
        			   response.setMessageCode(PretupsErrorCodesI.USER_SUCCESSFULLY_ACTIVATED);
            	   }else {
            		   
            		   response.setMessageCode(PretupsErrorCodesI.USER_2NDAPPROVE_REQUIRED);
            	   }
        		   
        		   
                   // send a message to the user abt their
                   // activation
            	   if (locale == null) {
                       locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                   }
                   BTSLMessages btslPushMessage = null;

                   
                	   if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                       // send message for both login id and sms
                       // pin
    
                       // edited by hitesh ghanghas
                       if (!BTSLUtil.isNullString(channelUserVO.getLoginID()))
                       // abhilasha commented for sending email &&
                       // loginPasswordAllowed)
                       {
                           final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", channelUserVO.getShowPassword(), BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };
                           btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                           final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
                                           .getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_ADD);
                           try {
								pushMessage.push();
							} catch (Exception e) {
								LOG.errorTrace(methodName, e);
							}
                           // Email for pin & password- code for
                           // email details
                           if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                        	   if(requestVO.getApprovalLevel().equals("APPRV1")) {
                        		   subjectKey= "user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval" ; 
                        	   }else {
                        		   subjectKey= "user.addchanneluser.level2approvemessage";   
                        	   }
                               final String subject = BTSLUtil.getMessage(locale,subjectKey, arr);
                               
                               final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
                                               "Email will be delivered shortly", channelUserVO, senderVO);
                               emailSendToUser.sendMail();
                           }
                       } else {
                           if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                               final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", channelUserVO.getShowPassword(), BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                               // for Zebra and Tango by Sanjeew
                               // date 11/07/07
                               if (ptupsMobqutyMergd) {
                                   if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                       arrArray[2] =  requestVO.getData().getPaymentType();//paymentnumber
                                   }
                               }
                               // End Zebra and Tango

                               btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                               final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
                                               .getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_ADD);
                               pushMessage.push();
                           } else {
                               final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                               // for Zebra and Tango by Sanjeew
                               // date 11/07/07
                               if (ptupsMobqutyMergd) {
                                   if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                       arrArray[2] = requestVO.getData().getPaymentType(); //paymentnumber 
                                   }
                               }
                               // End Zebra and Tango

                               btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                               final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
                                               .getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_ADD);
                               pushMessage.push();
                           }
                       } // change finished by hitesh ghanghas
                   } else if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && TypesI.NO.equals(categoryVO
                                   .getSmsInterfaceAllowed()) && !BTSLUtil.isNullString(channelUserVO.getLoginID()))
                   // commented for sending email &&
                   // loginPasswordAllowed)
                   {
                       // send message for login id
               
                       final String[] arrArray = { channelUserVO.getLoginID(), "", BTSLUtil.decryptText(channelUserVO.getPassword()) };
                       btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                       final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_ADD);
                       try {
							pushMessage.push();
						} catch (Exception e) {
							LOG.errorTrace(methodName, e);
						}
                       // Email for pin & password- code for email
                       // details
                       if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                    	   if(requestVO.getApprovalLevel().equals("APPRV1")) {
                    		   subjectKey="user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval";
                    	   }else {
                    		   subjectKey="user.addchanneluser.level2approvemessage"; 
                    	   }
                    	   
                           final String subject =BTSLUtil.getMessage(locale,"user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", arr);
                           
                           final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
                                           "Email will be delivered shortly", channelUserVO, senderVO);
                           emailSendToUser.sendMail();
                       }
                   } else {
                       // send message for sms pin
                    
                       final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPrimaryMsisdnPin()) };
                       btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                       final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_ADD);
                       try {
							pushMessage.push();
						} catch (Exception e) {
							LOG.errorTrace(methodName, e);
						}
                       // Email for pin & password
                       if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                           final String arrOne[] = { channelUserVO.getMsisdn() };
                           final String subject =BTSLUtil.getMessage(locale,"subject.user.regmsidn.massage", arrOne);
                           final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
                                           "Email will be delivered shortly", channelUserVO, senderVO);
                           emailSendToUser.sendMail();
                       }
                   }

                   // pusing message to individual msisdn
                   if (channelUserVO.getMsisdnList() != null && channelUserVO.getMsisdnList().size() > 1) {
                       // it mean that it has secondary number and
                       // now push message to individual secondary
                       // no

                       final ArrayList newMsisdnList = channelUserVO.getMsisdnList();
                       UserPhoneVO newUserPhoneVO = null;
                       // Email for pin & password
                       EmailSendToUser emailSendToUser = null;
                       String subject = null;
                       final String tmpMsisdn = channelUserVO.getMsisdn();
                       ;
                       for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
                           btslPushMessage = null;
                           newUserPhoneVO = (UserPhoneVO) newMsisdnList.get(i);
                           if (TypesI.NO.equals(newUserPhoneVO.getPrimaryNumber())) {
                               final String[] arrArray = { newUserPhoneVO.getMsisdn(), "", BTSLUtil.decryptText(newUserPhoneVO.getSmsPin()) };
                               if (ptupsMobqutyMergd) {
                                   if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                       arrArray[1] = requestVO.getData().getPaymentType(); // paymentNumber;
                                   }
                               }
                               locale = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());
                               if (locale == null) {
                                   //locale = BTSLUtil.getBTSLLocale();
                               }
                               btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                               final PushMessage pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), btslPushMessage, "", "", locale, channelUserVO
                                               .getNetworkID(), null, PretupsI.SERVICE_TYPE_EXT_USER_ADD);
                               try {
   								pushMessage.push();
   							} catch (Exception e) {
   								LOG.errorTrace(methodName, e);
   							}
                               // Email for pin & password
                               if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                                   subject =BTSLUtil.getMessage(locale,"subject.user.regmsidn.massage", new String[] { newUserPhoneVO.getMsisdn() });
                                   channelUserVO.setMsisdn(newUserPhoneVO.getMsisdn());
                                   emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(),
                                                   "Email will be delivered shortly", channelUserVO, senderVO);
                                   emailSendToUser.sendMail();
                                   channelUserVO.setMsisdn(tmpMsisdn);
                               }
                           }
                       }
                   }

                   final BTSLMessages btslMessage = new BTSLMessages("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", arr,
                                   "ApprovalOneSuccess");
                   
               
           }
           if(requestVO.getApprovalLevel().equals(PretupsI.CHANNEL_USER_APPROVE1)) {
           ChannelUserLog.log("APP1CHNLUSR", channelUserVO, senderVO, true, null);
           }else {
        	   ChannelUserLog.log("APP2CHNLUSR", channelUserVO, senderVO, true, null);
           }
           boolean approverCanEdit = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT);
           boolean realtimeOtfMsgs = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS);
           //OTF Message function  at approval 1
           if(realtimeOtfMsgs &&(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID())) && (((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),requestVO.getData().getUserCatCode())).intValue()==1 || !approverCanEdit))){
           	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
           	tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
           }
           
       	if(BTSLUtil.isNullString(response.getMessageCode()))
		{
			response.setMessageCode(PretupsErrorCodesI.CHANNEL_USER_UPDATE);
		}
		response.setMessage(RestAPIStringParser.getMessage(locale,response.getMessageCode(),arr));

   }
   

   private void smsOrEmailMessageforEdit(Connection con,ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO) throws BTSLBaseException {
	   String []messageArguments=null;
       final String arr[] = { channelUserVO.getUserName(), null,senderPin };
           UserPhoneVO oldUserPhoneVO = null;
           UserPhoneVO newUserPhoneVO = null;
           String primaryNoPin = null;
           BTSLMessages sendbtslMessage = null;
           final boolean pinFlag = false;
           boolean primaryNoPinFlag = false;
           Locale localeMsisdn = null;
           PushMessage pushMessage = null;
           // Email for pin & password
           String subject = null;
           EmailSendToUser emailSendToUser = null;
           final String tmpMsisdn = channelUserVO.getMsisdn();
           ;
           messageArguments= arr;
           if (userOldPhoneList != null) {
               for (int i = 0, j = userOldPhoneList.size(); i < j; i++) {
                   sendbtslMessage = null;
                   oldUserPhoneVO = (UserPhoneVO) userOldPhoneList.get(i);
                   for (int k = 0, l = userPhoneList.size(); k < l; k++) {
                       newUserPhoneVO = (UserPhoneVO) userPhoneList.get(k);
                       if (!BTSLUtil.isNullString(newUserPhoneVO.getMsisdn()) && newUserPhoneVO.getMsisdn().equals(oldUserPhoneVO.getMsisdn())) {
                           if (TypesI.YES.equals(newUserPhoneVO.getPrimaryNumber())) {
                               if (!(newUserPhoneVO.getShowSmsPin().equals(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(oldUserPhoneVO.getSmsPin()))))) {// primary
                                   // no
                                   // pin
                                   // change
                                   primaryNoPin = newUserPhoneVO.getShowSmsPin();// primary
                                   // no
                                   // pin
                                   // change
                                   primaryNoPinFlag = true;
                               }
                           } else if (!(newUserPhoneVO.getShowSmsPin().equals(BTSLUtil
                                           .getDefaultPasswordNumeric(BTSLUtil.decryptText(oldUserPhoneVO.getSmsPin()))))) {// if
                               // pin
                               // change
                               localeMsisdn = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());

                               // for Zebra and Tango by Sanjeew
                               // date 11/07/07
                               if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX
                                               .equals(channelUserVO.getMcommerceServiceAllow())) {
                                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY,
                                                   new String[] { newUserPhoneVO.getShowSmsPin(), "" });
                               } else {
                                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, new String[] { newUserPhoneVO.getShowSmsPin() });
                               }
                              pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), sendbtslMessage, "", "", localeMsisdn, channelUserVO.getNetworkID(),
                                               "SMS will be delivered shortly thankyou");
                               pushMessage.push();
                               // Email for pin & password- email
                               // send
                               if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                                   channelUserVO.setMsisdn(newUserPhoneVO.getMsisdn());
                                   subject = BTSLUtil.getMessage(locale, "subject.user.regmsidn.massage.modify",
                                                   new String[] { newUserPhoneVO.getMsisdn() });
                                   emailSendToUser = new EmailSendToUser(subject, sendbtslMessage, locale, channelUserVO.getNetworkID(),
                                                   "Email will be delivered shortly", channelUserVO, senderVO);
                                   emailSendToUser.sendMail();
                                   channelUserVO.setMsisdn(tmpMsisdn);
                               }
                           }
                           // comment break for sending messages to
                           // all msisdn
                           // break;
                       }// end of
                        // if(newUserPhoneVO.getMsisdn().equals(oldUserPhoneVO.getMsisdn()))
                   }// end of for(int k=0, l=newMsisdnList.size();
                    // k<l;k++)
               }// end of for(int i=0, j=oldMsisdnList.size(); i<j;
                // i++)
           }
          final String msg[] = new String[3];
           sendbtslMessage = null;
           if (!BTSLUtil.isNullString(channelUserVO.getLoginID()) && !BTSLUtil.isNullString(existingLoginId)) {
               // only web password change
               if (isWebPasswordChanged && !primaryNoPinFlag && channelUserVO.getLoginID().equals(existingLoginId)) {
                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_MODIFY, new String[] { channelUserVO.getShowPassword() });
                   response.setMessageCode(PretupsErrorCodesI.CHNL_USER_PWD_MODIFY);
                   messageArguments=new String[] { channelUserVO.getShowPassword() };
               } else if (isWebPasswordChanged && primaryNoPinFlag && modifedLoginId.equals(existingLoginId)) {
                   msg[0] = channelUserVO.getShowPassword();
                   msg[1] = primaryNoPin;
                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY, msg);
                   response.setMessageCode(PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY);
                   messageArguments=msg;
               }
               // web loginid and web password and primary no pin
               // change
               else if (isWebPasswordChanged && primaryNoPinFlag && !channelUserVO.getLoginID().equals(existingLoginId)) {
                   msg[0] = channelUserVO.getLoginID();
                   msg[1] = channelUserVO.getShowPassword();
                   msg[2] = primaryNoPin;
                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY, msg);
                   response.setMessageCode(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY);
                   messageArguments=msg;
               }
               // only login id change
               else if (!isWebPasswordChanged && !primaryNoPinFlag && !channelUserVO.getLoginID().equals(existingLoginId)) {
                   msg[0] = channelUserVO.getLoginID();
                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY, msg);
                   response.setMessageCode(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY);
                   messageArguments=msg;
               }
               // only login id and web password change
               else if (isWebPasswordChanged && !primaryNoPinFlag && !channelUserVO.getLoginID().equals(existingLoginId)) {
                   msg[0] = channelUserVO.getLoginID();
                   msg[1] = channelUserVO.getShowPassword();
                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY, msg);
                   response.setMessageCode(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY);
                   messageArguments=msg;
               }
               // only login id and pin change
               else if (!isWebPasswordChanged && primaryNoPinFlag && !channelUserVO.getLoginID().equals(existingLoginId)) {
                   msg[0] = channelUserVO.getLoginID();
                   msg[1] = primaryNoPin;
                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY, msg);
                   response.setMessageCode(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY);
                   messageArguments=msg;
               }
               // only primary no pin change.
               else if (!isWebPasswordChanged && primaryNoPinFlag &&channelUserVO.getLoginID().equals(existingLoginId)) {
                   msg[0] = primaryNoPin;
                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, msg);
                   response.setMessageCode(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY);
                   messageArguments=msg;
               }
           }// if(!BTSLUtil.isNullString(theForm.getWebLoginID())
            // &&
            // !BTSLUtil.isNullString(theForm.getOldWebLoginID()))
           else {
               if (primaryNoPinFlag) {
                   sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, new String[] { primaryNoPin });
                   response.setMessageCode(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY);
                   messageArguments=new String[] { primaryNoPin };
               }
           }
           // Send SMS
      
           if (sendbtslMessage != null) {
               
               PushMessage pushMessage1 = new PushMessage(channelUserVO.getMsisdn(), sendbtslMessage, "", "", locale, channelUserVO.getNetworkID(),
                               "SMS will be delivered shortly thanks");
               pushMessage1.push();
               // Email for pin & password
               if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                   String subject1 = BTSLUtil.getMessage(locale, "user.addchanneluser.updatesuccessmessage", arr);
                   EmailSendToUser emailSendToUser1 = new EmailSendToUser(subject1, sendbtslMessage, locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
                                   channelUserVO, senderVO);
                   emailSendToUser1.sendMail();
               }
           }
       
       if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID())) && (((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),requestVO.getData().getUserCatCode())).intValue()==0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue())  )){
       	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
       	tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
       }
       
       if((requestVO.getData().getUserCatCode().equals(PretupsI.CUSTOMER_CARE) || requestVO.getData().getUserCatCode().equals(PretupsI.BCU_USER))) {
    	   OperatorUserLog.log("MODOPTUSR", channelUserVO, senderVO, null);
       }else {
    	   ChannelUserLog.log("MODCHNLUSR", channelUserVO, senderVO, true, null);
       }
       
       response.setStatus(200);
    
		if(BTSLUtil.isNullString(response.getMessageCode()))
		{
			if(requestVO.getApprovalLevel()!=null) {
			    if(requestVO.getApprovalLevel().equals(PretupsI.CHANNEL_USER_APPROVE1)) {
			    	response.setMessageCode(PretupsErrorCodesI.USER_2NDAPPROVE_REQUIRED);	
			  }else {	
				response.setMessageCode(PretupsErrorCodesI.USER_SUCCESSFULLY_UPDATED);
				}
			}
		}
		response.setMessage(RestAPIStringParser.getMessage(locale,response.getMessageCode(),messageArguments));

   }
   
   
   private void checkSMSorEmailMessagingForOPTS(Connection con,ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO, ChannelUserVO senderVO) throws BTSLBaseException {
	   final String methodName ="methodName";
	   boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
		try
		{
		if (!BTSLUtil.isNullString(channelUserVO.getMsisdn())
				&& PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {

			// Add for auto generating PIN/password
			String[] arr = { channelUserVO.getLoginID(), "", requestVO.getData().getWebpassword()  };
			BTSLMessages btslMessage =null;
			
			   if(TypesI.CUSTOMER_CARE.equals(categoryVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equals(categoryVO.getCategoryCode())){
              	 
              	btslMessage = new BTSLMessages(PretupsErrorCodesI.USER_CCE_WEB_ACTIVE, arr);
              }else{
              
              btslMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arr);
              }
			
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			 locale = new Locale(lang, country);
			PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslMessage, "", "", locale,
					channelUserVO.getNetworkID());
			pushMessage.push();
			// Email for pin & password-email push
			if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
				String arrOne[] = { channelUserVO.getUserName() };
				String subject = BTSLUtil.getMessage(locale, "user.addoperatoruser.addsuccessmessage", arr);
				
				EmailSendToUser emailSendToUser1 = new EmailSendToUser(subject, btslMessage, locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
                        channelUserVO, senderVO);
				
				emailSendToUser1.sendMail();
			}
		}
		
		
		            OperatorUserLog.log("ADDOPTUSR", channelUserVO, senderVO, null);
		
		
		
		}
		catch (Exception e) {
     LOG.error(methodName, "Exceptin:e=" + e);
      LOG.errorTrace(methodName, e);
		}

   }
   
 public void  validateDivisionCode(Connection con,String divisionCode) throws BTSLBaseException {
	 final String methodName ="validateDivisionCode";
	 DivisionDeptTxnDAO divisionDeptTxnDAO = new DivisionDeptTxnDAO();
	 if(BTSLUtil.isNullString(divisionCode)) {
		 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DIVISION_CODE_MANDATORY);	 
	 }
	 if(!divisionDeptTxnDAO.isDivisionExists(con, divisionCode)) {
		 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_DIVISION_CODE);	 
	 }
	 
	 
 }
 
 
 
 public void  validateDepartmentCode(Connection con,String departmentCode,String divisionID) throws BTSLBaseException {
	 final String methodName ="validateDepartmentCode";
	 DivisionDeptTxnDAO divisionDeptTxnDAO = new DivisionDeptTxnDAO();
	 if(BTSLUtil.isNullString(departmentCode)) {
		 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DEPARTMENT_CODE_MANDATORY);	 
	 }
	 if(!divisionDeptTxnDAO.isDepartmentExitsUnderDivision(con, divisionID,departmentCode)) {
		 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_DEPARTMENT_CODE);	 
	 }
	 
	 
 }


public HashMap<String, String> getRequestGroupHashMap() {
	return requestGroupHashMap;
}

public void setRequestGroupHashMap(HashMap<String, String> requestGroupHashMap) {
	this.requestGroupHashMap = requestGroupHashMap;
}

public ChannelUserVO getExistingDBchannelUserVO() {
	return existingDBchannelUserVO;
}

public void setExistingDBchannelUserVO(ChannelUserVO existingDBchannelUserVO) {
	this.existingDBchannelUserVO = existingDBchannelUserVO;
}


}
