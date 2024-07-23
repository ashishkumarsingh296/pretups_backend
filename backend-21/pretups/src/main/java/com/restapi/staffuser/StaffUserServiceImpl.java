package com.restapi.staffuser;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.pretups.channeluser.businesslogic.ChannelUserApprovalReqVO;
import com.btsl.user.businesslogic.*;
import com.restapi.channeluser.service.OwnerParentInfoVO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

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
import com.btsl.pretups.channel.transfer.requesthandler.ChannelUserUnderParentVO;
import com.btsl.pretups.channel.transfer.requesthandler.FetchUserDetailsResponseVO;
import com.btsl.pretups.channel.transfer.requesthandler.GroupedUserRolesVO;
import com.btsl.pretups.channel.transfer.requesthandler.LoginDetailsVO;
import com.btsl.pretups.channel.transfer.requesthandler.PaymentAndServiceDetailsVO;
import com.btsl.pretups.channel.transfer.requesthandler.PersonalDetailsVO;
import com.btsl.pretups.channel.transfer.requesthandler.ProfileDetailsVO;
import com.btsl.pretups.channel.transfer.requesthandler.ViewUserRolesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.DateUtils;
import com.web.pretups.roles.businesslogic.UserRolesWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.pretups.user.web.ChannelUserAction;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserForm;


@Service("StaffUserServiceI")
public class StaffUserServiceImpl implements StaffUserServiceI{

	public static final Log log = LogFactory.getLog(StaffUserServiceImpl.class.getName());
    public static final String  classname = "StaffUserServiceImpl";
    
    private UserDAO userDAO = null;
	private CategoryVO 	categoryVO = null;
	private ChannelUserDAO channelUserDao = null;
	private ChannelUserWebDAO channelUserWebDao = null;
	private ExtUserDAO extUserDao = null;
	private ChannelUserVO senderVO = null;
	private CommonUtil commonUtil=new CommonUtil();
	private OperatorUtilI operatorUtili;
	
	
	@Override	public ServiceListResponse getServiceList(String loginId, Connection con, ServiceListResponse response, HttpServletResponse responseSwag) {
		
		// TODO Auto-generated method stub
		
		 final String methodName = "getServiceList";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     
		ChannelUserWebDAO channelUserWebDAO = null;
	    UserDAO userDAO=null;
	    ServicesTypeDAO servicesDAO = null;
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    try {
	    	servicesDAO = new ServicesTypeDAO();
	    	userDAO = new UserDAO();
	    	channelUserWebDAO = new ChannelUserWebDAO();
	    	
	    	final ChannelUserVO channelUserSessionVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginId);
	         
	         // modified for staff user approval
	         if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(channelUserSessionVO.getDomainID())) {
	             ChannelUserVO parentVO = null;
	             parentVO = channelUserWebDAO.loadParentUserDetailsByUserID(con, channelUserSessionVO.getUserID());
	             parentVO.setUserID(channelUserSessionVO.getUserID());
	             if (!BTSLUtil.isNullString(parentVO.getUserID())) {
	            	 response.setServiceList(servicesDAO.loadUserServicesList(con, parentVO.getUserID()));
	              }
	         } 
	         else {
	        	 response.setServiceList(servicesDAO.loadUserServicesList(con, channelUserSessionVO.getUserID()));
	         }

	     
			 response.setStatus(PretupsI.RESPONSE_SUCCESS);
			 response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			 String resmsg = RestAPIStringParser.getMessage(new Locale(lang,country),
						PretupsErrorCodesI.SUCCESS, null);
			 response.setMessage(resmsg);
	     
	         
	    }catch (Exception e) {
		 	response.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(e.getMessage());
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			return response;
		}
	    finally {
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Exiting");
	        	}
	    	}	
		
	    return response;
	}
	
	@Override
	public FetchStaffUserResponse getRoleList(String loginId, Connection con, FetchStaffUserResponse response, HttpServletResponse responseSwag) {
	
		 final String methodName = "getRoleList";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     	ChannelUserWebDAO channelUserWebDAO = null;
		    UserDAO userDAO=null;
		    UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
		    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		   try {
			   userDAO = new UserDAO();
		    	channelUserWebDAO = new ChannelUserWebDAO();
		    	rolesWebDAO=new UserRolesWebDAO();
			   ChannelUserVO channelUserVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginId);
		         
			 if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(channelUserVO.getDomainID())) {
	                ChannelUserVO parentVO = null;
	                parentVO = channelUserWebDAO.loadParentUserDetailsByUserID(con, channelUserVO.getUserID());
	                parentVO.setUserID((channelUserVO.getUserID()));
	              
	                if (!BTSLUtil.isNullString(parentVO.getUserID()) && !BTSLUtil.isNullString(parentVO.getCategoryVO().getDomainTypeCode())) {
	                    final boolean isGroupRole = rolesWebDAO.isGroupRole(con, parentVO.getUserID(), parentVO.getCategoryVO().getDomainTypeCode());
	                    if (isGroupRole) {
	                    	response.setRolesList(rolesWebDAO.loadGroupRoleRolesListByUserID(con, parentVO.getUserID(), parentVO.getCategoryCode()));
	                    } else {
	                    	
	                    	HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> rolesMap = (new UserRolesDAO()).loadRolesListByUserId_new(con, parentVO.getUserID(), parentVO.getCategoryCode(), "N");
	                    	response.setRolesList(rolesMap);
//	                    	response.setRolesList(rolesWebDAO.loadRolesListByUserID(con, parentVO.getUserID(), parentVO.getCategoryCode(), "N"));
	                    }
	                }
	            } else {
	                final boolean isGroupRole = rolesWebDAO.isGroupRole(con, channelUserVO.getUserID(), channelUserVO.getDomainTypeCode());

	                if (isGroupRole) {
//	                    response.setRolesList(rolesWebDAO.loadGroupRoleRolesListByUserID(con, channelUserVO.getUserID(), channelUserVO.getCategoryCode()));
	                    
	                    HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> rolesMap = (new UserRolesDAO()).loadGroupRolesListByUserId_new(con, channelUserVO.getUserID(), channelUserVO.getCategoryCode(), channelUserVO.getDomainTypeCode());
                    	response.setRolesList(rolesMap);
	                } else {
	                	
	                	HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> rolesMap = (new UserRolesDAO()).loadRolesListByUserId_new(con, channelUserVO.getUserID(), channelUserVO.getCategoryCode(), "N");
                    	response.setRolesList(rolesMap);
//	                	response.setRolesList(rolesWebDAO.loadRolesListByUserID(con, channelUserVO.getUserID(), channelUserVO.getCategoryCode(), "N"));
	                }
	            }
			 response.setStatus(PretupsI.RESPONSE_SUCCESS);
			 response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			 String resmsg = RestAPIStringParser.getMessage(new Locale(lang,country),
						PretupsErrorCodesI.SUCCESS, null);
			 response.setMessage(resmsg);
			 
		   }catch (Exception e) {
			 	response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(e.getMessage());
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
				return response;
			}finally {  if (log.isDebugEnabled()) {
	            log.debug(methodName, "Exiting");
	        	}
	    }
		   
		return response;
	}
	
	
	
	
	@Override
	public BaseResponse addStaffUserDetails(String loginId, Connection con,StaffUserRequestVO  requestVO,
			HttpServletResponse responseSwag) {
		 final String METHOD_NAME = "addStaffUserDetails";
	     if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Entered");
	     }
	    NetworkPrefixVO networkPrefixVO = null;
	    ServicesTypeDAO servicesDAO = null;
	    CategoryDAO categoryDao=null;
	    
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
    	Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
        UserPhoneVO phoneVO = null;
        channelUserDao = new ChannelUserDAO();
        channelUserWebDao = new ChannelUserWebDAO();
        userDAO = new UserDAO();
        extUserDao = new ExtUserDAO();
        
        categoryDao= new CategoryDAO();
        BaseResponse response = new BaseResponse();
    	OperatorUtilI operatorUtili = new OperatorUtil();
        String senderPin = "";
        String webPassword = null;
        String randomPwd = null;
        String defaultGeoCode = "";
        String parentId = "";
        String staffCategoryCode=null;
        final String fromTime = "00:00";
        final String toTime = "23:59";
        RetValue retValue =null;
	    try {
	    	servicesDAO = new ServicesTypeDAO();
	    	userDAO = new UserDAO();
	    	
	    	channelUserWebDao = new ChannelUserWebDAO();
	    	// This is when batch admin user logs in , Input channel user loginid  is considered  , else logged user login id is considered
	    	    	if(requestVO.getBatchInputParams()!=null) {   
	    	    		retValue=validateBatchUserInputDetails(con,requestVO);
	    	    		  
	    	    			  loginId=requestVO.getBatchInputParams().getChannelUser();
	    	    	}
	    	
	    	
	    	final ChannelUserVO channelUserSessionVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginId);
	    	final String userID = channelUserSessionVO.getUserID();
	        UserWebDAO userwebDAO = new UserWebDAO();
	        final int staffUserCount = userwebDAO.staffUserCount(con, userID, TypesI.STAFF_USER_TYPE);
	        final int allowedStaffUserCount = ((Integer) PreferenceCache.getControlPreference(PreferenceI.STAFF_USER_COUNT, channelUserSessionVO.getNetworkID(), channelUserSessionVO
	                            .getCategoryCode())).intValue();
	            if (allowedStaffUserCount <= staffUserCount) {
	                final String arr[] = { String.valueOf(allowedStaffUserCount) };
	                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.STAFF_USER_LIMIT, arr);
	            }
	            
	           if(BTSLUtil.isNullString(requestVO.getStaffUserDetailsdata().getUserName()) &&  BTSLUtil.isNullString(requestVO.getStaffUserDetailsdata().getFirstName()) ){
                   throw new BTSLBaseException( this,METHOD_NAME, PretupsErrorCodesI.USERNAME_CANT_BLANK);
	           }
	            
	            final ChannelUserVO staffUserVO = new ChannelUserVO();
	            // load the userPrefixName dropdown
	            ArrayList userNamePrefix = LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true);
	            parentId = channelUserSessionVO.getParentID();
	             if (parentId.equalsIgnoreCase(PretupsI.ROOT_PARENT_ID)) {
	                 parentId = channelUserSessionVO.getUserID();
	             }
	            final CategoryVO catVO = channelUserSessionVO.getCategoryVO();
	            catVO.setOutletsAllowed("N");
	            if (!SystemPreferences.IS_REQ_MSISDN_FOR_STAFF) {
	                catVO.setSmsInterfaceAllowed("N");	            }
	            final ArrayList<UserGeographiesVO> geoList = channelUserSessionVO.getGeographicalAreaList();
	            if (catVO.getMultipleGrphDomains().equals(PretupsI.YES)) {
	                String[] geoStr = null;
	                if (geoList != null && geoList.size() > 0) {
	                    geoStr = new String[geoList.size()];
	                    int geoListSize = geoList.size();
	                    for (int i = 0, j = geoListSize; i < j; i++) {
	                    	UserGeographiesVO  userGeoVO = (UserGeographiesVO) geoList.get(i);
	                        geoStr[i] = userGeoVO.getGraphDomainCode();
	                    }
	                }
	                staffUserVO.setGeographicalCodeArray(geoStr);
	            } else if (geoList != null && geoList.size() > 0) {
	            	UserGeographiesVO  userGeoVO = (UserGeographiesVO) geoList.get(0);
	                staffUserVO.setGeographicalCode(userGeoVO.getGraphDomainCode());
	            }
	            
	            Date currentDate = new Date();
	            if(requestVO.getStaffUserDetailsdata().getAppointmentdate()!=null && requestVO.getStaffUserDetailsdata().getAppointmentdate().trim().length()>0) {
	                Date appointmentDate=BTSLUtil.getDateFromDateString(requestVO.getStaffUserDetailsdata().getAppointmentdate());				//isBeforeDay
	   	              if(appointmentDate!=null){
	   		              if (DateUtils.isBeforeDay(appointmentDate,currentDate)) {
	   		            	  throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.APPOINTMENTDATE_LESS_CURRDATE);
	   		              }   
	   	              }
	                }
	          
	            
	            if(BTSLUtil.isNullString(requestVO.getStaffUserDetailsdata().getUserNamePrefix())) {
		        	   throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.USERNAME_PREFIX_NULL);
		           }
	            
	            
	            
	            //final String[] categoryCode = channelUserSessionVO.getChannelCategoryCode().split(":");
	            if (SystemPreferences.AUTO_PWD_GENERATE_ALLOW) {
                    randomPwd = operatorUtili.generateRandomPassword();
                    requestVO.getStaffUserDetailsdata().setWebpassword(randomPwd);
                    requestVO.getStaffUserDetailsdata().setConfirmwebpassword(randomPwd);
               }else if(BTSLUtil.isNullString(requestVO.getStaffUserDetailsdata().getWebpassword())) {
                   throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PASSWORD_CANT_BLANK);
               }
	            
	            
	            
	           if(!requestVO.getStaffUserDetailsdata().getWebpassword().equals(requestVO.getStaffUserDetailsdata().getConfirmwebpassword())) {
	        	   throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.PASSWORD_CONFRIMPASS_NOT_SAME);
	           }
               int index = 0;
	           for(Msisdn obj :requestVO.getStaffUserDetailsdata().getMsisdn()) {
	        	      if(BTSLUtil.isNullorEmpty(obj.getPhoneNo())){
	        	    	  throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_FOUND,new String[] {index+1+""});
	        	      }
	        	      
	        	      if(!BTSLUtil.isNumericDouble(obj.getPhoneNo())){
	        	    	  throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.ONLY_NUMBER_IN_MSISDN,new String[] {index+1+""});
	        	      }
	        	   
	            	 if(!obj.getPin().equals(obj.getConfirmpin())) {
	  	        	   throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.PIN_NOT_SAME,new String[] {index+1+""});
	  	        }      
	            	 index++;
	           }
	           
	           
	           final String allowedIPs = requestVO.getStaffUserDetailsdata().getAllowedip();
	   		if (!BTSLUtil.isNullString(allowedIPs)) {
	   			commonUtil.ipAddressValidation(allowedIPs);
	   			staffUserVO.setAllowedIps(allowedIPs);
	   		}
	            if(BTSLUtil.isNullString(requestVO.getStaffUserDetailsdata().getAllowedTimeFrom())) {
	            	requestVO.getStaffUserDetailsdata().setAllowedTimeFrom(fromTime);
	               }
	            if(BTSLUtil.isNullString(requestVO.getStaffUserDetailsdata().getAllowedTimeTo())) {
	            	requestVO.getStaffUserDetailsdata().setAllowedTimeTo(toTime);
	               }
	            String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
	            Pattern pattern = Pattern.compile(regex);
	            Matcher matcher = pattern.matcher(requestVO.getStaffUserDetailsdata().getEmailid());
	             if(!matcher.matches()) {
	            	 throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.INVALID_EMAIL); 
	             }
	            
	            final CategoryVO categoryVO = channelUserSessionVO.getCategoryVO();
                final ChannelUserAction channelUserAction = new ChannelUserAction();
                staffUserVO.setNetworkID(channelUserSessionVO.getNetworkID());
                staffUserVO.setLastLoginOn(currentDate);
                staffUserVO.setPasswordModifiedOn(currentDate);
                staffUserVO.setCreatedOn(currentDate);
                staffUserVO.setCreatedBy(channelUserSessionVO.getActiveUserID());
                staffUserVO.setModifiedOn(currentDate);
                
                if (SystemPreferences.STAFF_USER_APRL_LEVEL > 0) {
                    staffUserVO.setStatus(PretupsI.USER_STATUS_NEW);// N New
                    staffUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// N
                    // New
                } else {
                    staffUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// Y
                    // Active
                    staffUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);// Y
                    // Active
                }

                this.constructFormToVO(requestVO.getStaffUserDetailsdata() ,staffUserVO, channelUserSessionVO);
                
    	    	if(requestVO.getBatchInputParams()!=null) {  // If batchadm logs in ....
    	    		staffUserVO.setOwnerID(retValue.getOwnerUser());
    	    		staffUserVO.setParentID(retValue.getChannelUser());
    	    	}
                
                if (categoryVO.getUserIdPrefix() == null) {
                    final ArrayList categoryList = geoList;
                    int categoryListSize = categoryList.size();
                    for (int i = 0; i < categoryListSize; i++) {
                        final CategoryVO _categoryVO = (CategoryVO) categoryList.get(i);
                        if (_categoryVO.getCategoryCode().equalsIgnoreCase(channelUserSessionVO.getCategoryCode())) {
                            categoryVO.setUserIdPrefix(_categoryVO.getUserIdPrefix());
                        }
                    }
                }
                staffUserVO.setUserID(channelUserAction.generateUserId(channelUserSessionVO.getNetworkID(), categoryVO.getUserIdPrefix()));
                staffUserVO.setUserProfileID(staffUserVO.getUserID());
                staffUserVO.setMcommerceServiceAllow(PretupsI.NO);
                staffUserVO.setMpayProfileID("");
                staffUserVO.setLowBalAlertAllow("N");
               if(requestVO.getStaffUserDetailsdata().getMsisdn() != null ) {
               for(Msisdn msisdn:requestVO.getStaffUserDetailsdata().getMsisdn()) {
            	   if("Y".equals(msisdn.getIsprimary())) {
            		   staffUserVO.setMsisdn(msisdn.getPhoneNo());
            	   }
               }
               }
                String filterMsisdn = null;
                final ArrayList phoneList = new ArrayList();
                boolean msisdnCheck = false;

              final ArrayList msisdnList  = (ArrayList) prepareUserPhoneVOList(con, requestVO, channelUserSessionVO, currentDate, senderPin,operatorUtili,staffUserVO);
              
              if(requestVO.getBatchInputParams()!=null) {
            	  staffCategoryCode=requestVO.getBatchInputParams().getCategoryCode();
              }else {
            	  staffCategoryCode=channelUserSessionVO.getCategoryCode();
              }
              CategoryVO catgoryVO =	categoryDao.loadCategoryDetailsByCategoryCode(con,staffCategoryCode);
              
              
              
               if(catgoryVO.getWebInterfaceAllowed()!= null && catgoryVO.getWebInterfaceAllowed().equalsIgnoreCase(PretupsI.YES)) {
            	    if(BTSLUtil.isNullString(requestVO.getStaffUserDetailsdata().getWebloginid())){
            	    	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.STAFF_USER_LOGINID_MANDATORY);   	
            	    }
            	     
               }
            
              
             if( userDAO.isUserLoginExist(con,requestVO.getStaffUserDetailsdata().getWebloginid(),staffUserVO.getUserID())) {
            	 final String[] arr = {requestVO.getStaffUserDetailsdata().getWebloginid()};
                 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.STAFF_USER_LOGINID_ALREADY_EXIST, arr);
             }
                                
                // insert data into users table
                final int userCount = userDAO.addUser(con, staffUserVO);

                if (userCount <= 0) {
                    con.rollback();
                    log.error(METHOD_NAME, "Error: while Inserting Staff User");
                    throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                }
                
                
                if (PretupsI.USER_STATUS_ACTIVE.equals(staffUserVO.getStatus())) {
                    staffUserVO.setActivatedOn(currentDate);
                } else {
                    staffUserVO.setActivatedOn(null);
                }

                // insert data into channelusers table
                final int userChannelCount = channelUserDao.addChannelUser(con, staffUserVO);

                if (userChannelCount <= 0) {
                    con.rollback();
                    log.error(METHOD_NAME, "Error: while Inserting User");
                    throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                }else {
                

                    if (geoList != null && geoList.size() > 0) {
                        final UserGeographiesDAO userGeographiesDAO = new UserGeographiesDAO();
                        for(UserGeographiesVO vo: geoList) {
                        	vo.setUserId(staffUserVO.getUserID());
                        }
                        
                        final int geographyCount = userGeographiesDAO.addUserGeographyList(con, geoList);

                        if (geographyCount <= 0) {
                            try {
                                con.rollback();
                            } catch (SQLException e) {
                                log.errorTrace(METHOD_NAME, e);
                            }
                            log.error(METHOD_NAME, "Error: while Inserting User Geography Info");
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                        }
                        staffUserVO.setGeographicalAreaList(geoList);
                    }	
                	           
                }
  
                if (msisdnList != null && msisdnList.size() > 0) {
                    final int phoneCount = userDAO.addUserPhoneList(con,msisdnList);
                    if (phoneCount <= 0) {
                        try {
                            con.rollback();
                        } catch (SQLException e) {
                            log.errorTrace(METHOD_NAME, e);
                        }
                        log.error(METHOD_NAME, "Error: while Inserting User Phone Info");
                        throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                    }
                }
                // Assign Services
                String services =  requestVO.getStaffUserDetailsdata().getServices();
                final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
                ListValueVO listValueVO = null;
                List serviceList = null;
                try {
                    //serviceList = servicesTypeDAO.loadServicesList(con, channelUserSessionVO.getNetworkID(), PretupsI.C2S_MODULE, channelUserSessionVO.getCategoryCode(), false);
                	serviceList = servicesTypeDAO.loadUserServicesList(con, channelUserSessionVO.getUserID());  // Get Parent SErvices
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
                            servicesTypeDAO.addUserServicesList(con, staffUserVO.getUserID(), givenService, PretupsI.YES);
                        }else{
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_INVALID);
                        }
                    }
                } catch (Exception e) {
                    log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
                }
                // Assign Roles
                UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
                String role =  requestVO.getStaffUserDetailsdata().getRoles();
                if(role!=null && role.trim().length()==0) {
                	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ASSIGN_STAFF_ROLES_MANDATORY);
                }
                
                String [] rolesArray = role.split(",");
                HashMap rolesMap = null;
                
                if (PretupsI.YES.equalsIgnoreCase(categoryVO.getWebInterfaceAllowed())) {
                    final UserRolesDAO userRolesDAO = new UserRolesDAO();
                        if(!BTSLUtil.isNullString(role)){
                        	final boolean isGroupRole = rolesWebDAO.isGroupRole(con, channelUserSessionVO.getUserID(), channelUserSessionVO.getDomainTypeCode());

        	                if (isGroupRole) {
        	                	rolesMap = rolesWebDAO.loadGroupRoleRolesListByUserID(con, channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryCode());
        	                } else {
       	                	rolesMap = rolesWebDAO.loadRolesListByUserID(con, channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryCode(), "N");
        	                }
                            boolean isGroupRoleValid=true;
                            
                            HashSet<String> roleCodeSet = new HashSet<String>();
                            
                            for(Object key : rolesMap.keySet()) {
                            	ArrayList<UserRolesVO> roleslist = (ArrayList<UserRolesVO>) rolesMap.get(key);
                            	for(UserRolesVO roles : roleslist) {
                            		roleCodeSet.add(roles.getRoleCode());
                            	}
                             }
                            
                            
                            for (int i = 0; i < rolesArray.length; i++) {
                                if(!roleCodeSet.contains(rolesArray[i])){
                                	isGroupRoleValid = false;
                                }
                            }
                            if(isGroupRoleValid){
                                    int userRoles=userRolesDAO.addUserRolesList(con, staffUserVO.getUserID(), rolesArray);
                                    if (userRoles <= 0) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                                    }
                                
                              }
                            else
                            {
                                throw new BTSLBaseException(this,  METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                            }
                        }
                    
                }
                con.commit();
                response.setStatus(200);
                response.setMessageCode(PretupsErrorCodesI.STAFF_USER_SUSSESS);
                String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STAFF_USER_SUSSESS,new String[] {staffUserVO.getUserName()});
                response.setMessage(msg);
                // push message
                if (PretupsI.USER_STATUS_ACTIVE.equals(staffUserVO.getStatus())) {
                    PushMessage pushMessage = null;
                    BTSLMessages btslMessage = null;
             
                    if (!BTSLUtil.isNullString(staffUserVO.getMsisdn()) && !"".equals(staffUserVO.getMsisdn()) && !PretupsI.NOT_AVAILABLE.equals(staffUserVO.getMsisdn())) {
                        final String[] arr = { staffUserVO.getLoginID(), staffUserVO.getMsisdn(), "", BTSLUtil.decryptText(staffUserVO.getPassword()), BTSLUtil
                                        .decryptText(staffUserVO.getPrimaryMsisdnPin()), staffUserVO.getUserName() };
                        btslMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arr);
                        pushMessage = new PushMessage(staffUserVO.getMsisdn(), btslMessage, "", "", locale, staffUserVO.getNetworkID(),
                                        "SMS will be delivered shortly thanks");
                        pushMessage.push();
                        // Email for pin & password
                        if (SystemPreferences.IS_EMAIL_SERVICE_ALLOW && !BTSLUtil.isNullString(staffUserVO.getEmail())) {
                            final String arrTmp[] = { staffUserVO.getUserName() };
                            //final String subject = this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "user.addStaffuser.addsuccessmessage", arrTmp);
                            String subject=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STAFF_USER_ADDED_SUCCESS,new String[] {staffUserVO.getUserName()});
                            final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage, locale, staffUserVO.getNetworkID(),
                                            "Email will be delivered shortly", staffUserVO, channelUserSessionVO);
                            emailSendToUser.sendMail();
                        }
                    } else {
                        // Changed for hiding PIN and PWD that are written
                        // in MessageSentLog
                        // if pin is assigned to staff user
                        UserPhoneVO phoneVO1 = null;
                        if (BTSLUtil.isNullString(parentId)) {
                            parentId = channelUserSessionVO.getUserID();
                        }
                        phoneVO1 = new ChannelUserDAO().loadUserPhoneDetails(con, parentId);
                        if (BTSLUtil.isNullString(staffUserVO.getSmsPin())) {
                            final String[] arr = { staffUserVO.getLoginID(), "", BTSLUtil.decryptText(staffUserVO.getPassword()), staffUserVO.getUserName() };
                            btslMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE_STAFF, arr);
                        } else {
                            final String[] arr = { staffUserVO.getLoginID(), "", BTSLUtil.decryptText(staffUserVO.getPassword()), BTSLUtil.decryptText(staffUserVO
                                            .getSmsPin()), staffUserVO.getUserName() };
                            btslMessage = new BTSLMessages(PretupsErrorCodesI.STAFF_WEB_SMSPIN_ACTIVATE, arr);
                        }
                  
                        pushMessage = new PushMessage(phoneVO1.getMsisdn(), btslMessage, "", "", locale, staffUserVO.getNetworkID(),
                                        "SMS will be delivered shortly thanks");
                        pushMessage.push();
                        // Email for pin & password
                        if (SystemPreferences.IS_EMAIL_SERVICE_ALLOW && !BTSLUtil.isNullString(channelUserSessionVO.getEmail())) {
                            final String arrUserName[] = { staffUserVO.getUserName() };
                            /*final String subject = this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "user.addStaffuser.addsuccessmessage",
                                            arrUserName);*/
                            final ChannelUserVO tmpStaffUserVO = new ChannelUserVO();
                            BeanUtils.copyProperties(tmpStaffUserVO, staffUserVO);
                            tmpStaffUserVO.setEmail(channelUserSessionVO.getEmail());
                            String subject=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STAFF_USER_ADDED_SUCCESS,new String[] {staffUserVO.getUserName()});
                            final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage, locale, staffUserVO.getNetworkID(),
                                            "Email will be delivered shortly", tmpStaffUserVO, channelUserSessionVO);
                            emailSendToUser.sendMail();
                        }
                    }
                 

                } 

                ChannelUserLog.log("ADDSTAFFUSR", staffUserVO, channelUserSessionVO, true, null);
               
                
	    } catch (BTSLBaseException be) {
	        log.error("processFile", "Exceptin:e=" + be);
	        log.errorTrace(METHOD_NAME, be);
       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),be.getArgs());
	        response.setMessageCode(be.getMessageKey());
	        response.setMessage(msg);
        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
    	         response.setStatus(401);
            }
           else{
        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
           		response.setStatus(400);
           }
        }catch (Exception e) {
            log.debug("processFile", e);
            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            String resmsg = RestAPIStringParser.getMessage(
    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
    				null);
            response.setMessage(resmsg);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        	response.setStatus(400);
        	
    	}
		return response;
	}
	@Override
	@SuppressWarnings({"rawtypes" , "unchecked"})
	public BaseResponse editStaffUserDetails(String loginId, Connection con, StaffUserEditRequestVO requestVO, HttpServletResponse responseSwag) {
		
		final String METHOD_NAME = "editStaffUserDetails";
	     if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Entered");
	     }
	     
	     NetworkPrefixVO networkPrefixVO = null;
		 ServicesTypeDAO servicesDAO = null;
		 DomainDAO domainDAO = null;
		 String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		 String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		 Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		 UserPhoneVO phoneVO = null;
		 channelUserDao = new ChannelUserDAO();
		 channelUserWebDao = new ChannelUserWebDAO();
		 userDAO = new UserDAO();
		 extUserDao = new ExtUserDAO();
		 BaseResponse response = new BaseResponse();
		 OperatorUtilI operatorUtili = new OperatorUtil();
		 String senderPin = "";
	     String webPassword = null;
	     String randomPwd = null;
	     String defaultGeoCode = "";
	     String parentId = "";
	      String fromTime = "00:00";
	      String toTime = "23:59";
	        
	     try {
	    	     
	    	 servicesDAO = new ServicesTypeDAO();
		     userDAO = new UserDAO();
		    	
		     //fetching userId by login id
	    	 final String oldLoginId = requestVO.getStaffUserEditDetailsdata().getOldWebloginid();
	    	 ChannelUserVO userVO  = userDAO.loadAllUserDetailsByLoginID(con, oldLoginId);// if oldLoginId is null or not correct then this will take care of it;
	    	 ChannelUserVO parentuserVO  = userDAO.loadUserDetailsFormUserID(con, userVO.getParentID()) ;
	    	 
	    	 final String userId = userVO.getUserID();
	    	 
	    	 //decrypting pin
	    	 for(EditMsisdn msisdn:requestVO.getStaffUserEditDetailsdata().getMsisdn() ) {
	    		 String oldPin = msisdn.getOldPin();
	    		 String newPin = msisdn.getPin();
	    		 if(oldPin!=null && oldPin.trim().length()>0) {
	    			 oldPin = AESEncryptionUtil.aesDecryptor(oldPin, Constants.A_KEY).replace("\"", "");
		    		 msisdn.setOldPin(oldPin);
	    		 }
	    		 if(newPin!=null && newPin.trim().length()>0) {
	    			 newPin = AESEncryptionUtil.aesDecryptor(newPin, Constants.A_KEY).replace("\"", "");
	    			 msisdn.setPin(newPin);
	    		 }

	    		 
	    	 }
	    	 
	    	 if(BTSLUtil.isNullString(requestVO.getStaffUserEditDetailsdata().getUserName()) &&  BTSLUtil.isNullString(requestVO.getStaffUserEditDetailsdata().getFirstName()) ){
                 throw new BTSLBaseException( this,METHOD_NAME, PretupsErrorCodesI.USERNAME_CANT_BLANK);
	           }
	    	 
	    	 if(BTSLUtil.isNullString(requestVO.getStaffUserEditDetailsdata().getUserNamePrefix())) {
	        	   throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.USERNAME_PREFIX_NULL);
	           }
	  		  fromTime = requestVO.getStaffUserEditDetailsdata().getAllowedTimeFrom();
			  toTime = requestVO.getStaffUserEditDetailsdata().getAllowedTimeTo();

			if (!BTSLUtil.isNullString(fromTime)) {
				if (!BTSLUtil.isValidateAllowedTime(fromTime)) {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_FROMTIME_INVALID);
				}
				requestVO.getStaffUserEditDetailsdata().setAllowedTimeFrom(fromTime);
			}

			if (!BTSLUtil.isNullString(toTime)) {
				if (!BTSLUtil.isValidateAllowedTime(toTime)) {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_TOTIME_INVALID);
				}
				requestVO.getStaffUserEditDetailsdata().setAllowedTimeTo(toTime);
			}
			if (!BTSLUtil.isNullString(requestVO.getStaffUserEditDetailsdata().getAllowedTimeFrom()) && !BTSLUtil.isNullString(requestVO.getStaffUserEditDetailsdata().getAllowedTimeTo())) {
				if (requestVO.getStaffUserEditDetailsdata().getAllowedTimeFrom().equals(requestVO.getStaffUserEditDetailsdata().getAllowedTimeTo())) {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.TIME_RANGE);
				}
			}

			
			if(commonUtil.timeDifference(fromTime, toTime)<0) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.FROMTIME_GRT_TOTIME);
			}
			 
	    	 
	    	 
	    	 
	    	 
	    	 
	    	 String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
	            Pattern pattern = Pattern.compile(regex);
	            Matcher matcher = pattern.matcher(requestVO.getStaffUserEditDetailsdata().getEmailid());
	             if(!matcher.matches()) {
	            	 throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.INVALID_EMAIL); 
	             }
	            
	         final Date currentDate = new Date();
	             
	    	 //create staffUserVO 
	    	 final ChannelUserVO staffUserVO = new ChannelUserVO();
	    	 
	    	 
	    	 final String allowedIPs = requestVO.getStaffUserEditDetailsdata().getAllowedip();
	    	  if (!BTSLUtil.isNullString(allowedIPs)) {
		   		commonUtil.ipAddressValidation(allowedIPs);
		   		staffUserVO.setAllowedIps(allowedIPs);
		   	  }
	    	  staffUserVO.setFromTime(requestVO.getStaffUserEditDetailsdata().getAllowedTimeFrom());
	    	  staffUserVO.setToTime(requestVO.getStaffUserEditDetailsdata().getAllowedTimeTo());
	    	 staffUserVO.setUserID(userId);// all updates will be made in db where this userId exists
	    	 staffUserVO.setPassword(userVO.getPassword());//password can't be changed while editing staff details
	    	 
	    	 staffUserVO.setNetworkID(userVO.getNetworkID());
             staffUserVO.setLastLoginOn(currentDate);
             staffUserVO.setPasswordModifiedOn(userVO.getPasswordModifiedOn());
             staffUserVO.setCreatedOn(userVO.getCreatedOn());
             staffUserVO.setCreatedBy(userVO.getActiveUserID());
             staffUserVO.setModifiedOn(currentDate);
             
             
             staffUserVO.setPreviousStatus(userVO.getPreviousStatus());
             staffUserVO.setStatus(userVO.getStatus());
             
             
              ChannelUserVO channelUserSessionVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginId);//data of user who is editing staff user details
             
              if(channelUserSessionVO.getCategoryCode()!=null && channelUserSessionVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY)) {
            	   channelUserSessionVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, requestVO.getStaffUserEditDetailsdata().getOldWebloginid());//data of user who is editing staff user details
              }
             
               
             
             this.constructFormToVO(requestVO.getStaffUserEditDetailsdata() ,staffUserVO, channelUserSessionVO);// filling staffUserVO
             
             if(channelUserSessionVO.getCategoryCode()!=null && channelUserSessionVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY)) { 
            	 staffUserVO.setParentID(channelUserSessionVO.getParentID());
             }else {
            	 staffUserVO.setParentID(parentuserVO.getUserID());	 
             }
             
             staffUserVO.setUserProfileID(staffUserVO.getUserID());
             staffUserVO.setMcommerceServiceAllow(PretupsI.NO);
             staffUserVO.setMpayProfileID("");
             staffUserVO.setLowBalAlertAllow("N");
             
             if(requestVO.getStaffUserEditDetailsdata().getMsisdn() != null ) {
                 for(EditMsisdn msisdn:requestVO.getStaffUserEditDetailsdata().getMsisdn()) {
              	   if("Y".equals(msisdn.getIsprimary())) {
              		   staffUserVO.setMsisdn(msisdn.getPhoneNo());
              	   }
                 }
                 
             }
             
             
             if(requestVO.getStaffUserEditDetailsdata().getSubscriberCode() != null && requestVO.getStaffUserEditDetailsdata().getSubscriberCode().length()>PretupsI.SUBSCRIBER_MAX_LENGTH) {
            		String[] strAr1=new String[] {String.valueOf(PretupsI.SUBSCRIBER_MAX_LENGTH)};
        	   		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SUBSCRIBERCODE_LENGTH_EXCEEDING,
        	   				strAr1);
             }	 
              
              if (PretupsI.USER_STATUS_ACTIVE.equals(staffUserVO.getStatus())) {
                  staffUserVO.setActivatedOn(currentDate);
              } else {
                  staffUserVO.setActivatedOn(null);
              }

              // insert data into channelusers table
              final int userChannelCount = channelUserDao.updateChannelUserInfo(con, staffUserVO);

              if (userChannelCount <= 0) {
                  con.rollback();
                  log.error(METHOD_NAME, "Error: while modifying User");
                  throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
              }
              
              
              // delete old services            
              String services =  requestVO.getStaffUserEditDetailsdata().getServices();
              final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
              ListValueVO listValueVO = null;
              List serviceList = null;
              int delCount = servicesTypeDAO.deleteUserServices(con, userId);
//              if (delCount <= 0) {	
//                  con.rollback();
//                  log.error(METHOD_NAME, "Error: while deleting Services");
//                  throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
//              }
              // will add  services
              try {
                  //serviceList = servicesTypeDAO.loadServicesList(con, channelUserSessionVO.getNetworkID(), PretupsI.C2S_MODULE, channelUserSessionVO.getCategoryCode(), false);
            	  serviceList = servicesTypeDAO.loadUserServicesList(con, staffUserVO.getParentID());
            	  
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
                          servicesTypeDAO.addUserServicesList(con, staffUserVO.getUserID(), givenService, PretupsI.YES);
                      }else{
                          throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_INVALID);
                      }
                  }else{
//                      final String[] service = new String[serviceList.size()];
//                      int serviceListSize=serviceList.size();
//                      for (int i = 0; i < serviceListSize; i++) {
//                          listValueVO = (ListValueVO) serviceList.get(i);
//                          service[i] = listValueVO.getValue();
//
//                      }
//                      if(!serviceList.isEmpty()){
//                          final int servicesCount = servicesTypeDAO.addUserServicesList(con, staffUserVO.getUserID(), service, PretupsI.YES);
//                          if (servicesCount <= 0) {
//                              throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
//                          }
//                      }
                  }
              } catch (Exception e) {
                  log.errorTrace(METHOD_NAME, e);
                  throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
              }
              
                          
              //delete old roles
              final CategoryVO categoryVO = channelUserSessionVO.getCategoryVO();
              UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
              String role =  requestVO.getStaffUserEditDetailsdata().getRoles();
              if(role!=null && role.trim().length()==0) {
              	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ASSIGN_STAFF_ROLES_MANDATORY);
              }
              String [] rolesArray = role.split(",");
              HashMap rolesMap = null;
              
              
              //will add roles
              if (PretupsI.YES.equalsIgnoreCase(categoryVO.getWebInterfaceAllowed())) {
                      if(!BTSLUtil.isNullString(role)){
                    	  domainDAO = new DomainDAO();
                    	  DomainVO domainVO =    domainDAO.loadDomainVO(con,parentuserVO.getDomainID());
                      	final boolean isGroupRole = rolesWebDAO.isGroupRole(con, parentuserVO.getUserID(), domainVO.getDomainTypeCode());

      	                if (isGroupRole) {
      	                	rolesMap = rolesWebDAO.loadGroupRoleRolesListByUserID(con, parentuserVO.getUserID(), parentuserVO.getCategoryCode());
      	                } else {
      	           	                	rolesMap = rolesWebDAO.loadRolesListByUserID(con, parentuserVO.getUserID(), parentuserVO.getCategoryCode(), "N");
      	                }
      	              //code moved  
      	              final UserRolesDAO userRolesDAO = new UserRolesDAO();
      	              delCount = userRolesDAO.deleteUserRoles(con, userId);
      	              if (delCount < 0) {// < 0 because it might so happen that no roles were assigned while adding staff user 
      	                  con.rollback();
      	                  log.error(METHOD_NAME, "Error: while deleting Services");
      	                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
      	              }
      	          //code moved
      	                
                          boolean isGroupRoleValid=true;
                          
                          HashSet<String> roleCodeSet = new HashSet<String>();
                          
                          for(Object key : rolesMap.keySet()) {
                          	ArrayList<UserRolesVO> roleslist = (ArrayList<UserRolesVO>) rolesMap.get(key);
                          	for(UserRolesVO roles : roleslist) {
                          		roleCodeSet.add(roles.getRoleCode());
                          	}
                           }
                          
                          
                          for (int i = 0; i < rolesArray.length; i++) {
                              if(!roleCodeSet.contains(rolesArray[i])){
                              	isGroupRoleValid = false;
                              }
                          }
                          if(isGroupRoleValid){
                                  int userRoles=userRolesDAO.addUserRolesList(con, staffUserVO.getUserID(), rolesArray);
                                  if (userRoles <= 0) {
                                      throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                                  }
                              
                            }
                          else
                          {
                        throw new BTSLBaseException(this,  METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                          }
                      }
                  
              }  
              
              //Handling msisdn list
              //checking if primary no pin is modified
              boolean isPinModified = false;
              boolean isPrimaryExist = false;
              String primarySmsPin="";
              UserPhoneVO primaryPhoneVO = null;
              for(EditMsisdn msisdn: requestVO.staffUserEditDetailsdata.getMsisdn()) {
            	  if(BTSLUtil.isNullString(msisdn.getPhoneNo()) || !BTSLUtil.isValidMSISDN(msisdn.getPhoneNo())) {
            		  log.error(METHOD_NAME, "Error: while Inserting User Phone Info");
             		  throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.MSISDN_INVALID);
            	  }
            	  BTSLUtil.validatePIN(msisdn.getPin());
            	  if(msisdn.getIsprimary().equals("Y") && isPrimaryExist) { 
            		  log.error(METHOD_NAME, "Error: while Inserting User Phone Info");
             		  throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.MORE_THAN_ONE_PRIMARY_MSISDN);
            	  }
             	  if(msisdn.getIsprimary().equals("Y")) {
             		 isPrimaryExist=true;
             		if(!BTSLUtil.isNullString(msisdn.getPin()) && !msisdn.getPin().equals(msisdn.getOldPin())) {isPinModified=true; primarySmsPin=msisdn.getPin();}
             	  } 
              }
              
              if(!isPrimaryExist && requestVO.staffUserEditDetailsdata.getMsisdn().length>0) {
            	  log.error(METHOD_NAME, "Error: while Inserting User Phone Info");
         		  throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_PRIMARY_MSISDN_BLANK);
              }
              
	          ArrayList<Integer> insertIndexes=new ArrayList<>(), updateIndexes=new ArrayList<>();
	          EditMsisdn[] msisdnList = requestVO.getStaffUserEditDetailsdata().getMsisdn();
	          if(msisdnList.length>0) {
              for(int i=0; i<msisdnList.length; i++) {
            	  if(BTSLUtil.isNullString(msisdnList[i].getPhoneNo())) continue;
            	  //if(msisdnList[i].getOpType().equals("U")) updateIndexes.add(i);
            	  insertIndexes.add(i);
              }
              
             EditMsisdn insertMsisdnList[] = new EditMsisdn[insertIndexes.size()] ;
             
             for(int i=0; i<insertIndexes.size(); i++) insertMsisdnList[i]=msisdnList[insertIndexes.get(i)]; 
             //for(int i=0; i<updateIndexes.size(); i++) updateMsisdnList[i]=msisdnList[updateIndexes.get(i)]; 
             
             //handling new msisdn first
             if(insertIndexes.size()>0) {
            	 final ArrayList insertPhoneList  = (ArrayList) prepareUserPhoneVOList(con, insertMsisdnList, requestVO, channelUserSessionVO, currentDate, senderPin,operatorUtili,staffUserVO);
            	 for(int i=0; i<insertPhoneList.size(); i++) {
            		 UserPhoneVO obj = (UserPhoneVO)insertPhoneList.get(i);
            		 obj.setOperationType("I"); 
            		 if(!BTSLUtil.isNullString(obj.getPrimaryNumber())) primaryPhoneVO=obj;
            	 	}
            	 userDAO.deleteUserPhoneList(con, userId);
            	 final int phoneCount = userDAO.addStaffUserPhoneList(con,insertPhoneList);
            	 if (phoneCount <= 0) {
            		 try {
            			 	con.rollback();
                      	} catch (SQLException e) {
                           log.errorTrace(METHOD_NAME, e);
                      	}
            		 log.error(METHOD_NAME, "Error: while Inserting User Phone Info");
            		 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
                   	}
            	 }
             
	          } else {
	        	  userDAO.deleteUserPhoneList(con, userId); 
	          }
             /*
             //handling msisdn update 
             if(updateIndexes.size()>0) {
            	 //load userPhoneVo list from db
            	 ArrayList<UserPhoneVO> dbPhoneList = userDAO.loadUserPhoneList(con, userId);
            	 
            	 //filter those userPhoneVO which will update
            	 ArrayList<UserPhoneVO> toUpdateUserPhoneVO = new ArrayList<>();
            	 for(EditMsisdn obj1 : updateMsisdnList) {
            		 for(UserPhoneVO obj2 : dbPhoneList) {
            			 if(obj1.getOldPhoneNo().equals(obj2.getMsisdn())) {toUpdateUserPhoneVO.add(obj2); break;}
            		 }
            	 }
            	 
            	 //change userPhoneVO according to changes in updateMsisdnList
            	 for(int i=0; i<updateMsisdnList.length; i++) {
            		 UserPhoneVO obj = (UserPhoneVO) toUpdateUserPhoneVO.get(i);
            		 updateUserPhoneVO(con, updateMsisdnList[i], obj, operatorUtili, staffUserVO, channelUserSessionVO);
            		 if(!BTSLUtil.isNullString(obj.getPrimaryNumber())) primaryPhoneVO=obj;
            	 }
            		 
            	 //updating
            	 final int phoneCount = userDAO.updateInsertDeleteUserPhoneList(con,toUpdateUserPhoneVO);
            	 if (phoneCount <= 0) {
            		 try {
            			 	con.rollback();
                      	} catch (SQLException e) {
                           log.errorTrace(METHOD_NAME, e);
                      	}
            		 log.error(METHOD_NAME, "Error: while Inserting User Phone Info");
            		 throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                   	}
            	 
             } */

             
             // insert data into users table
             if (!staffUserVO.getLoginID().equals(requestVO.getStaffUserEditDetailsdata().getOldWebloginid()) && userDAO.isUserLoginExist(con, staffUserVO.getLoginID(),"")) {
            	 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LOGINID_EXIST_ALREADY);
             }
             final int userCount = userDAO.updateUser(con, staffUserVO);
             if (userCount <= 0) {
                 con.rollback();
                 log.error(METHOD_NAME, "Error: while modifying Staff User");
                 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
             }
             
             
             con.commit();
             response.setStatus(200);
             response.setMessageCode(PretupsErrorCodesI.STAFF_USER_UPDATED_SUCCESS);
             String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STAFF_USER_UPDATED_SUCCESS,new String[] {staffUserVO.getUserName()});
             response.setMessage(msg);
      
             // push message
          // push message here to dealer
             UserVO channelUserDetail = userDAO.loadAllUserDetailsByLoginID(con, staffUserVO.getParentID()); 
             PushMessage pushMessage = null;
             BTSLMessages btslMessage1 = null;
             if (!BTSLUtil.isNullString(staffUserVO.getMsisdn())) {
            	 
            	 							// only password change
//                 						if (staffUserVO.isPasswordModifyFlag() && theForm.getOldWebLoginID().equals(theForm.getWebLoginID()) && !phoneVO.isPinModifyFlag()) {
//                 								btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_MODIFY, new String[] { BTSLUtil.decryptText(staffUserVO.getPassword()) });
//                 						} else if (staffUserVO.isPasswordModifyFlag() && phoneVO.isPinModifyFlag() && theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
//                 								btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY, new String[] { BTSLUtil
//                 										.decryptText(staffUserVO.getPassword()), BTSLUtil.decryptText(phoneVO.getSmsPin()) });
//                 						} else if (staffUserVO.isPasswordModifyFlag() && phoneVO.isPinModifyFlag() && !theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
//                 								btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY, new String[] { staffUserVO.getLoginID(), BTSLUtil
//                 										.decryptText(staffUserVO.getPassword()), BTSLUtil.decryptText(phoneVO.getSmsPin()) });
//                 						}

                                         if (!BTSLUtil.isNullString(requestVO.staffUserEditDetailsdata.getOldWebloginid()) ) {
                                             // web loginid change
                                             if (!requestVO.staffUserEditDetailsdata.getOldWebloginid().equals(requestVO.staffUserEditDetailsdata.getWebloginid())) {
                                                 btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY, new String[] { staffUserVO.getLoginID() });
                                             }

//                                             // web login id and password modified
//                                             if (!theForm.getOldWebLoginID().equals(theForm.getWebLoginID()) && staffUserVO.isPasswordModifyFlag()) {
//                                                 btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY, new String[] { staffUserVO.getLoginID(), BTSLUtil
//                                                                 .decryptText(staffUserVO.getPassword()) });
//                                             }
                                         }
                                         // only login id and pin change
                                         else if (isPinModified && !requestVO.staffUserEditDetailsdata.getOldWebloginid().equals(requestVO.staffUserEditDetailsdata.getWebloginid())) {
                                             btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY, new String[] { staffUserVO.getLoginID(), primarySmsPin });
//                                         } else if (!staffUserVO.isPasswordModifyFlag() && phoneVO.isPinModifyFlag() && theForm.getWebLoginID().equals(theForm.getOldWebLoginID()) && !PretupsI.NOT_AVAILABLE
//                                                         .equals(staffUserVO.getSmsPin())) {
//                                
//                                             btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, new String[] { BTSLUtil.decryptText(staffUserVO.getSmsPin()) });
                                         }
                                     } else if (!BTSLUtil.isNullString(requestVO.staffUserEditDetailsdata.getContactNumber()) || !BTSLUtil.isNullString(channelUserSessionVO.getMsisdn())) {
                                         // only password change
//                                         if (staffUserVO.isPasswordModifyFlag() && theForm.getOldWebLoginID().equals(theForm.getWebLoginID()) && !phoneVO.isPinModifyFlag()) {
//                                             btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PSWD_MODIFY_STAFF, new String[] { staffUserVO.getUserName(), BTSLUtil
//                                                             .decryptText(staffUserVO.getPassword()) });
//                                         } else if (staffUserVO.isPasswordModifyFlag() && phoneVO.isPinModifyFlag() && theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
//                                             btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PSWD_AND_PIN_MODIFY_STAFF, new String[] { BTSLUtil.decryptText(staffUserVO
//                                                             .getPassword()), BTSLUtil.decryptText(phoneVO.getSmsPin()), staffUserVO.getUserName() });
//                                         } else if (staffUserVO.isPasswordModifyFlag() && phoneVO.isPinModifyFlag() && !theForm.getWebLoginID().equals(theForm.getOldWebLoginID())) {
//                                             btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PSWD_AND_PIN_MODIFY_STAFF,
//                                                             new String[] { staffUserVO.getLoginID(), BTSLUtil.decryptText(staffUserVO.getPassword()), BTSLUtil.decryptText(phoneVO
//                                                                             .getSmsPin()), staffUserVO.getUserName() });
//                                         } else if (!theForm.getOldWebLoginID().equals(theForm.getWebLoginID()) && !staffUserVO.isPasswordModifyFlag() && !phoneVO.isPinModifyFlag()) {
//                                             btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY_STAFF, new String[] { staffUserVO.getLoginID(), staffUserVO
//                                                             .getUserName(), sessionUserVO.getUserName() });
//                                         }
                                         if (!BTSLUtil.isNullString(requestVO.staffUserEditDetailsdata.getOldWebloginid())) {
                                             // web loginid change
                                      
//                                             if (!theForm.getOldWebLoginID().equals(theForm.getWebLoginID()) && !staffUserVO.isPasswordModifyFlag()) {
//                                                 btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY_STAFF, new String[] { staffUserVO.getLoginID(), staffUserVO
//                                                                 .getUserName(), sessionUserVO.getUserName() });
//                                             }
                                             // web login id and password modified.
//                                             if (!theForm.getOldWebLoginID().equals(theForm.getWebLoginID()) && staffUserVO.isPasswordModifyFlag()) {
//                                                 btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PSWD_MODIFY_STAFF, new String[] { staffUserVO.getLoginID(), BTSLUtil
//                                                                 .decryptText(staffUserVO.getPassword()), staffUserVO.getUserName() });
//                                             }
                                         }
                                         // only login id and pin change
                                         else if (!requestVO.staffUserEditDetailsdata.getOldWebloginid().equals(requestVO.staffUserEditDetailsdata.getWebloginid())) {
                                             btslMessage1 = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY_STAFF, new String[] { staffUserVO.getLoginID(), primarySmsPin, staffUserVO.getUserName() });
                                         } 
//                                         else if (!staffUserVO.isPasswordModifyFlag() && phoneVO.isPinModifyFlag() && theForm.getWebLoginID().equals(theForm.getOldWebLoginID()) && !PretupsI.NOT_AVAILABLE
//                                                         .equals(staffUserVO.getSmsPin())) {
//                       
//                                             btslMessage1 = new BTSLMessages(PretupsErrorCodesI.PIN_MODIFY_STAFF, new String[] { BTSLUtil.decryptText(staffUserVO.getSmsPin()), staffUserVO
//                                                             .getUserName() });
//                                         }
                                     }
             			    if (btslMessage1 != null) {
                 

                                         if (!BTSLUtil.isNullString(staffUserVO.getMsisdn()) && !"".equals(staffUserVO.getMsisdn()) && !PretupsI.NOT_AVAILABLE.equals(staffUserVO
                                                         .getMsisdn())) {
                                             final UserPhoneVO phoneVO1 = primaryPhoneVO;
                                             locale = new Locale(phoneVO1.getPhoneLanguage(), phoneVO1.getCountry());
                                             // Changed for hiding PIN and PWD that are
                                             // written in MessageSentLog
                                             pushMessage = new PushMessage(staffUserVO.getMsisdn(), btslMessage1, "", "", locale, staffUserVO.getNetworkID(),
                                                             "SMS will be delivered shortly thanks");
                                             pushMessage.push();
                                             // Email for pin & password
                                             if (SystemPreferences.IS_EMAIL_SERVICE_ALLOW && !BTSLUtil.isNullString(staffUserVO.getEmail())) {
                                            	 final String arrTmp[] = { staffUserVO.getUserName() };
                                            	 String subject=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STAFF_USER_UPDATED_SUCCESS,arrTmp);
                                                 final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage1, locale, staffUserVO.getNetworkID(),
                                                                 "Email will be delivered shortly", staffUserVO, channelUserSessionVO);
                                                 emailSendToUser.sendMail();
                                             }
                                         }
                      
                                         else {
                                             UserPhoneVO phoneVO1 = null;
                                             if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
                                                 if (BTSLUtil.isNullString(parentId)) {
                                                     parentId = channelUserSessionVO.getParentID();
                                                 }
                                             } else {
                                                 if (BTSLUtil.isNullString(parentId)) {
                                                     parentId = channelUserSessionVO.getUserID();
                                                 }
                                             }
                                             phoneVO1 = new ChannelUserDAO().loadUserPhoneDetails(con, parentId);
                                             locale = new Locale(phoneVO1.getPhoneLanguage(), phoneVO1.getCountry());
                                             // Changed for hiding PIN and PWD that are
                                             // written in MessageSentLog
                                 
                                             pushMessage = new PushMessage(phoneVO1.getMsisdn(), btslMessage1, "", "", locale, staffUserVO.getNetworkID(),
                                                             "SMS will be delivered shortly thanks");
                                             pushMessage.push();
                                             // Email for pin & password
                                             if (SystemPreferences.IS_EMAIL_SERVICE_ALLOW && !BTSLUtil.isNullString(channelUserSessionVO.getEmail())) {
                                                 final String arrTmp[] = { staffUserVO.getUserName() };
                                                 String subject=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STAFF_USER_UPDATED_SUCCESS,arrTmp);
                                                 final ChannelUserVO tmpStaffUserVO = new ChannelUserVO();
                                                 BeanUtils.copyProperties(tmpStaffUserVO, staffUserVO);
                                                 tmpStaffUserVO.setEmail(channelUserSessionVO.getEmail());
                                                 final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage1, locale, staffUserVO.getNetworkID(),
                                                                 "Email will be delivered shortly", tmpStaffUserVO, channelUserSessionVO);
                                                 emailSendToUser.sendMail();
                                             }
                                         }
                                     }
                                     ChannelUserLog.log("MODIFYSTAFFUSR", staffUserVO, channelUserSessionVO, true, null);


	     }catch(BTSLBaseException be) {
		        log.error("processFile", "Exceptin:e=" + be);
		        log.errorTrace(METHOD_NAME, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),be.getArgs());
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);
	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus(401);
	            }
	           else{
	        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus(400);
	           }
	        }catch (Exception e) {
	            log.debug("processFile", e);
	            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	            String resmsg = RestAPIStringParser.getMessage(
	    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
	    				null);
	            response.setMessage(resmsg);
	            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(400);
	        	
	    	}
	    
	     return response;

	}

	private void constructFormToVO(StaffUserDetails staffUserDetails, ChannelUserVO p_channelUserVO, ChannelUserVO sessionUserVO) throws Exception {
        final String methodName = "staffUserDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_channelUserVO=" + p_channelUserVO);
        }
        
        try {
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					methodName, "", "", "", "Exception while loading the class at the call:" + e.getMessage());
		}
        
        try {
            String password = null;
           // p_channelUserVO.setUserID(staffUserDetails.getUserId());
            p_channelUserVO.setNetworkID(sessionUserVO.getNetworkID());
            if(BTSLUtil.isNullString(staffUserDetails.getUserName())) {
            	if(BTSLUtil.isNullString(staffUserDetails.getLastName())){
            	 p_channelUserVO.setUserName(staffUserDetails.getFirstName());
                 staffUserDetails.setUserName(staffUserDetails.getFirstName());
                 p_channelUserVO.setFirstName(staffUserDetails.getFirstName());
            	 }else {
                  p_channelUserVO.setUserName(staffUserDetails.getFirstName()+" "+staffUserDetails.getLastName());
                  staffUserDetails.setUserName(staffUserDetails.getFirstName()+" "+staffUserDetails.getLastName());
                  p_channelUserVO.setFirstName(staffUserDetails.getFirstName());
                  p_channelUserVO.setLastName(staffUserDetails.getLastName());
            	 }
            }else {
               p_channelUserVO.setUserName(staffUserDetails.getUserName());
               p_channelUserVO.setFirstName(staffUserDetails.getUserName());
            }
           
            p_channelUserVO.setLoginID(staffUserDetails.getWebloginid());
            
            // while inserting encrypt the password
            if (!BTSLUtil.isNullString(staffUserDetails.getWebpassword())) {
            		//decrypt request
            		password = AESEncryptionUtil.aesDecryptor(staffUserDetails.getWebpassword(), Constants.A_KEY);
            		final Map errorMessageMap = operatorUtili.validatePassword(staffUserDetails.getWebloginid(), password);
    				if (!SystemPreferences.AUTO_PWD_GENERATE_ALLOW && null != errorMessageMap && errorMessageMap.size() > 0) {
						Integer minLoginPwdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH);
						Integer maxLoginPwdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH);
						final String[] argsArray = { minLoginPwdLength.toString(), maxLoginPwdLength.toString()};
						throw new BTSLBaseException("StaffUserServiceImpl", methodName,
								PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID_MODIFIED, argsArray);
    				}
            		//encrypt for DB
                    password = BTSLUtil.encryptText(password);
               }
            p_channelUserVO.setPassword(password);
            p_channelUserVO.setPasswordModifyFlag(true);
            
            p_channelUserVO.setCategoryCode(sessionUserVO.getCategoryCode());
			if(sessionUserVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)){
				p_channelUserVO.setParentID(sessionUserVO.getParentID());
			}else{
				p_channelUserVO.setParentID(sessionUserVO.getUserID());
			}


			p_channelUserVO.setOwnerID(sessionUserVO.getOwnerID());
            p_channelUserVO.setAllowedIps(staffUserDetails.getAllowedip());
            final StringBuffer str = new StringBuffer();
            /*
             * theForm.getAllowedDays returns an string array but in DB we
             * insert a single
             * string value of the allowed days like 1,4,7, for this prupose
             * convert the string
             * array into string
             */
            if (staffUserDetails.getAlloweddays() != null && !BTSLUtil.isNullString(staffUserDetails.getAlloweddays()) && staffUserDetails.getAlloweddays().split(",").length > 0) {
                str.append(staffUserDetails.getAlloweddays().split(",")[0]);
                for (int i = 1, j = staffUserDetails.getAlloweddays().split(",").length; i < j; i++) {
                    str.append("," + staffUserDetails.getAlloweddays().split(",")[i]);
                }
                p_channelUserVO.setAllowedDays(str.toString());
            }else {
            	 //p_channelUserVO.setAllowedDays("1,2,3,4,5,6,7");
            	 p_channelUserVO.setAllowedDays(null);
            }
           
            p_channelUserVO.setFromTime(staffUserDetails.getAllowedTimeFrom());
            p_channelUserVO.setToTime(staffUserDetails.getAllowedTimeTo());
            p_channelUserVO.setEmpCode(staffUserDetails.getSubscriberCode());
            p_channelUserVO.setContactNo(staffUserDetails.getContactNumber());
            p_channelUserVO.setEmail(staffUserDetails.getEmailid());
            p_channelUserVO.setDesignation(staffUserDetails.getDesignation());
            // while adding Staff user userType value will be STAFF
            p_channelUserVO.setUserType(PretupsI.STAFF_USER_TYPE);
       
            p_channelUserVO.setContactPerson(sessionUserVO.getContactPerson());
            p_channelUserVO.setUserGrade(sessionUserVO.getUserGrade());
            p_channelUserVO.setTransferProfileID(sessionUserVO.getTransferProfileID());
            p_channelUserVO.setCommissionProfileSetID(sessionUserVO.getCommissionProfileSetID());
            p_channelUserVO.setInSuspend(sessionUserVO.getInSuspend());
            p_channelUserVO.setOutSuspened(sessionUserVO.getOutSuspened());
            p_channelUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
            p_channelUserVO.setAddress1(staffUserDetails.getAddress1());
            p_channelUserVO.setAddress2(staffUserDetails.getAddress2());
            p_channelUserVO.setCity(staffUserDetails.getCity());
            p_channelUserVO.setState(staffUserDetails.getState());
            p_channelUserVO.setCountry(staffUserDetails.getCountry());
             p_channelUserVO.setSsn(sessionUserVO.getSsn());
            // Added for RSA Authentication
            p_channelUserVO.setRsaFlag(sessionUserVO.getRsaFlag());
            p_channelUserVO.setUserNamePrefix(staffUserDetails.getUserNamePrefix());
//            p_channelUserVO.setExternalCode(sessionUserVO.getExternalCode());
            p_channelUserVO.setShortName(staffUserDetails.getShortName());
            if (!BTSLUtil.isNullString(staffUserDetails.getAppointmentdate())) {
                p_channelUserVO.setAppointmentDate(BTSLUtil.getDateFromDateString(staffUserDetails.getAppointmentdate()));
            }
            p_channelUserVO.setOutletCode(sessionUserVO.getOutletCode());
            p_channelUserVO.setSubOutletCode(sessionUserVO.getSubOutletCode());
            // modified for staff user approval
            if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(sessionUserVO.getDomainID())) {
         /*
                final ChannelUserVO parentVO = staffUserDetails.getStaffParentVO();
                p_channelUserVO.setNetworkID(parentVO.getNetworkID());
                p_channelUserVO.setParentID(parentVO.getUserID());
           
                p_channelUserVO.setOwnerID(parentVO.getOwnerID());
                p_channelUserVO.setContactPerson(parentVO.getContactPerson());
                p_channelUserVO.setUserGrade(parentVO.getUserGrade());
                p_channelUserVO.setTransferProfileID(parentVO.getTransferProfileID());
                p_channelUserVO.setCommissionProfileSetID(parentVO.getCommissionProfileSetID());
                p_channelUserVO.setInSuspend(parentVO.getInSuspend());
                p_channelUserVO.setOutSuspened(parentVO.getOutSuspened());
                p_channelUserVO.setModifiedBy(parentVO.getUserID());
                p_channelUserVO.setOutletCode(parentVO.getOutletCode());
                p_channelUserVO.setSubOutletCode(parentVO.getSubOutletCode());*/
            } else {
                p_channelUserVO.setNetworkID(sessionUserVO.getNetworkID());
				if(sessionUserVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)){
					p_channelUserVO.setParentID(sessionUserVO.getParentID());
				}else{
					p_channelUserVO.setParentID(sessionUserVO.getUserID());
				}
				p_channelUserVO.setOwnerID(sessionUserVO.getOwnerID());
                p_channelUserVO.setContactPerson(sessionUserVO.getContactPerson());
                p_channelUserVO.setUserGrade(sessionUserVO.getUserGrade());
                p_channelUserVO.setTransferProfileID(sessionUserVO.getTransferProfileID());
                p_channelUserVO.setCommissionProfileSetID(sessionUserVO.getCommissionProfileSetID());
                p_channelUserVO.setInSuspend(sessionUserVO.getInSuspend());
                p_channelUserVO.setOutSuspened(sessionUserVO.getOutSuspened());
                p_channelUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
                p_channelUserVO.setOutletCode(sessionUserVO.getOutletCode());
                p_channelUserVO.setSubOutletCode(sessionUserVO.getSubOutletCode());
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            throw e;
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting  p_channelUserVO=" + p_channelUserVO);
        }
    }
	
	 /**
     * This is the private method which insert User Info into the DB
     * 
     * @param form
     *            ActionForm
     * @param p_con
     *            Connection
     * @param p_userDAO
     *            UserDAO
     * @param p_userVO
     *            UserVO
     * @param p_curentdate
     *            java.util.Date
     */
    public void addUserInfo(Connection p_con, UserDAO p_userDAO, UserWebDAO userwebDAO, UserVO p_userVO, Date p_currentDate,StaffUserDetails staffUserDetails ) throws Exception {
        final String methodName = "addUserInfo";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

      
            if (p_userVO.getMsisdnList() != null && p_userVO.getMsisdnList().size() > 0) {
                final int phoneCount = p_userDAO.addUserPhoneList(p_con, p_userVO.getMsisdnList());
                if (phoneCount <= 0) {
                    try {
                        p_con.rollback();
                    } catch (SQLException e) {
                        log.errorTrace(methodName, e);
                    }
                    log.error(methodName, "Error: while Inserting User Phone Info");
                    throw new BTSLBaseException(this, methodName, "error.general.processing");
                }
            }
        final ArrayList geographyList = new ArrayList();
        UserGeographiesVO geoVO = null;

       /* if (TypesI.YES.equals(theForm.getCategoryVO().getMultipleGrphDomains())) {
            if (theForm.getGeographicalCodeArray() != null && theForm.getGeographicalCodeArray().length > 0) {
                for (int i = 0, j = theForm.getGeographicalCodeArray().length; i < j; i++) {
                    geoVO = new UserGeographiesVO();
                    geoVO.setUserId(p_userVO.getUserID());
                    geoVO.setGraphDomainCode(theForm.getGeographicalCodeArray()[i]);
                    geographyList.add(geoVO);
                }
            }
        } else// if user belongs to single zones
        {
            if (theForm.getGeographicalCode() != null && theForm.getGeographicalCode().trim().length() > 0) {
                geoVO = new UserGeographiesVO();
                geoVO.setUserId(p_userVO.getUserID());
                geoVO.setGraphDomainCode(theForm.getGeographicalCode());
                geographyList.add(geoVO);
            }
        }*/
        // insert geography info
        if (geographyList != null && geographyList.size() > 0) {
            final UserGeographiesDAO userGeographiesDAO = new UserGeographiesDAO();
            final int geographyCount = userGeographiesDAO.addUserGeographyList(p_con, geographyList);

            if (geographyCount <= 0) {
                try {
                    p_con.rollback();
                } catch (SQLException e) {
                    log.errorTrace(methodName, e);
                }
                log.error(methodName, "Error: while Inserting User Geography Info");
                throw new BTSLBaseException(this, methodName, "error.general.processing");
            }
            p_userVO.setGeographicalAreaList(geographyList);
        }

        // insert roles info
        if (p_userVO.getRoleFlag() != null && p_userVO.getRoleFlag().length > 0) {
            final UserRolesDAO rolesDAO = new UserRolesDAO();
            final int roleCount = rolesDAO.addUserRolesList(p_con, p_userVO.getUserID(), p_userVO.getRoleFlag());
            if (roleCount <= 0) {
                try {
                    p_con.rollback();
                } catch (SQLException e) {
                    log.errorTrace(methodName, e);
                }
                log.error(methodName, "Error: while Inserting User Roles Info");
                throw new BTSLBaseException(this, methodName, "error.general.processing");
            }
        }

        // insert services info
        if (p_userVO.getServicesTypes() != null && p_userVO.getServicesTypes().length > 0) {
            final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
            final int servicesCount = servicesDAO.addUserServicesList(p_con, p_userVO.getUserID(), p_userVO.getServicesTypes(), PretupsI.YES);
            if (servicesCount <= 0) {
                try {
                    p_con.rollback();
                } catch (SQLException e) {
                    log.errorTrace(methodName, e);
                }
                log.error(methodName, "Error: while Inserting User Services Info");
                throw new BTSLBaseException(this, methodName, "error.general.processing");
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        }
    }

    /**
     * @param staffUserVO 
     * @description : Method to prepareUserPhoneVOList
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return : List
     */
    private List prepareUserPhoneVOList(Connection con, StaffUserRequestVO requestVO, ChannelUserVO channelUserVO, Date currentDate, String senderPin,OperatorUtilI operatorUtili, ChannelUserVO staffUserVO) throws Exception {
        final String methodName = "prepareUserPhoneVOList";
        LogFactory.printLog(methodName, "Entered channelUserVO.getCategoryCode()=" + channelUserVO.getCategoryCode(), log);
    	
        final List <UserPhoneVO>phoneList = new ArrayList<UserPhoneVO>();
        List <Msisdn>msisdnList=new ArrayList();
        NetworkPrefixVO networkPrefixVO = null;
        Msisdn[] msisdns=requestVO.getStaffUserDetailsdata().getMsisdn();
        Msisdn m = new Msisdn();
        for(int i=0;i<msisdns.length;i++)
        {
        	m=msisdns[i];
        	if(SystemPreferences.AUTO_PIN_GENERATE_ALLOW)
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
            for (int i = 0, j = msisdnList.size(); i < j; i++) {
                msisdn = (Msisdn) msisdnList.get(i);
                if (!BTSLUtil.isNullString(msisdn.getPhoneNo())) {
                    phoneVO = new UserPhoneVO();
                    phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(msisdn.getPhoneNo()));
                    phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                    phoneVO.setUserId(staffUserVO.getUserID());
                    phoneVO.setSmsPin(BTSLUtil.encryptText(AESEncryptionUtil.aesDecryptor(msisdn.getPin(),Constants.A_KEY)));
                    phoneVO.setPinRequired(PretupsI.YES);
                    // set the default values
                    phoneVO.setCreatedBy(channelUserVO.getUserID());
                    phoneVO.setModifiedBy(channelUserVO.getUserID());
                    phoneVO.setCreatedOn(currentDate);
                    phoneVO.setModifiedOn(currentDate);
                    phoneVO.setPinModifiedOn(currentDate);
                    ArrayList languageList=LocaleMasterDAO.loadLocaleMasterData();
                    if (!BTSLUtil.isNullString(requestVO.getStaffUserDetailsdata().getLanguage())) {
                    boolean flag1=true;
                    for(int k=0;k<languageList.size();k++)
                    {
                    	if(((ListValueVO)languageList.get(k)).getValue().equals(requestVO.getStaffUserDetailsdata().getLanguage()))
                    	{
                    		flag1=false;
                    	}
                    }
                    if(flag1==true)
                    {
                    	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LANGUAGE_DOES_NOT_EXIST);
                    }
                    }
                    if (!BTSLUtil.isNullString(requestVO.getStaffUserDetailsdata().getLanguage())) {
                        final String lang_country[] = (requestVO.getStaffUserDetailsdata().getLanguage()).split("_");
                        phoneVO.setPhoneLanguage(lang_country[0]);
                        phoneVO.setCountry(lang_country[1]);
                    } else {
                        phoneVO.setPhoneLanguage((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
                        phoneVO.setCountry((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
                    }
                   /* if(msisdn.getStkProfile()==null)
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_FOUND);
                    }
                    else if(!(msisdn.getStkProfile().equals(stkProfile)))
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_VALID);
                    }*/
                    phoneVO.setPhoneProfile(stkProfile);
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
                    if((prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserVO.getNetworkID()))) {
                        throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
                    }
                    phoneVO.setPrefixID(prefixVO.getPrefixID());
                    if (msisdn.getIsprimary().equals("Y")){
                        channelUserVO.setMsisdn(msisdn.getPhoneNo());
                        phoneVO.setPrimaryNumber(PretupsI.YES);
                    } else {
                        phoneVO.setPrimaryNumber(PretupsI.NO);
                    }

                    if (SystemPreferences.MNP_ALLOWED) {
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
        }
        else {
        	UserPhoneVO phoneVO = new UserPhoneVO();
            phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
            phoneVO.setMsisdn(PretupsI.NOT_APPLICABLE);
            phoneVO.setUserId(staffUserVO.getUserID());
            phoneVO.setDescription(null);
            phoneVO.setPrimaryNumber(PretupsI.YES);
            phoneVO.setSmsPin(PretupsI.NOT_APPLICABLE);
            phoneVO.setPinRequired(channelUserVO.getPinRequired());
            phoneVO.setPhoneProfile(stkProfile);
            phoneVO.setPhoneLanguage((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
            phoneVO.setCountry((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));     
            phoneVO.setInvalidPinCount(0);
            phoneVO.setCreatedBy(channelUserVO.getUserID());
            phoneVO.setModifiedBy(channelUserVO.getUserID());
            phoneVO.setCreatedOn(currentDate);
            phoneVO.setModifiedOn(currentDate);
            phoneVO.setPinModifiedOn(currentDate);
            final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL
                    .getMSISDNPrefix(PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn())));
                if(prefixVO == null) {
                    throw new BTSLBaseException(methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
                }
            phoneVO.setPrefixID(prefixVO.getPrefixID());
            phoneList.add(phoneVO);
        }
        channelUserVO.setMsisdnList((ArrayList)phoneList);
        return phoneList;
    }
	

    /***
   	 * @param p_con
   	 * @param p_id
   	 * @param p_response
   	 * @param p_sessionUserVO
   	 */
   	@Override
   	public void processStaffUserDetailsDownload(Connection p_con , String p_id , FetchUserDetailsResponseVO p_response , UserVO p_sessionUserVO) throws BTSLBaseException , Exception{
  		final String methodName = "processStaffUserDetailsDownload";
   		if (log.isDebugEnabled()) {
   			log.debug(methodName, "Entered ");
   		}
   		
   		PersonalDetailsVO personalDetails = new PersonalDetailsVO();
   		LoginDetailsVO loginDetails = new LoginDetailsVO();
   		PaymentAndServiceDetailsVO paymentAndServiceDetails = new PaymentAndServiceDetailsVO();
   		GroupedUserRolesVO userRolesByGroup = new GroupedUserRolesVO();
   		ProfileDetailsVO profileDetails = new ProfileDetailsVO();
   		
   		UserForm userForm = new UserForm();
   		
   		//validate request
   		this.validateRequestDetails(p_id, userForm);
   		
   		//getting search user details from login id and setting username and userId in form for input to loadStaffUserDetails method 
   		UserDAO userDao = new UserDAO();
		UserVO searchUserVO = userDao.loadAllUserDetailsByLoginID( p_con, userForm.getSearchLoginId().trim() );
		if(searchUserVO == null ) {
			String[] arr = new String[1];
            arr[0] = userForm.getSearchLoginId();
            throw new BTSLBaseException(this , methodName , PretupsErrorCodesI.INVALID_LOGIN_ID , 0,arr, null);
		} 
//		if(!searchUserVO.getParentID().equals(p_sessionUserVO.getUserID())) {
//			String[] arr = new String[1];
//            arr[0] = userForm.getSearchLoginId();
//            throw new BTSLBaseException(this , methodName , PretupsErrorCodesI.INVALID_LOGIN_ID , 0,arr, null);
//		}
		userForm.setUserId(searchUserVO.getUserID());
		userForm.setUserName(searchUserVO.getUserName());
		userForm.setSearchLoginId(null);
   		
   		userForm.setLoginUserCategoryCode(p_sessionUserVO.getCategoryCode());
   		userForm.setLoginUserDomainCode(p_sessionUserVO.getDomainTypeCode());
   		userForm.setLoginUserDomainID(p_sessionUserVO.getDomainID());
   		userForm.setLoginUserID(p_sessionUserVO.getActiveUserID());
   		
   		// load staff user details
   		this.loadStaffUserDetails( p_con, userForm, p_sessionUserVO, loginDetails);
   			        
   		// set user details
   		this.setUserDetailsResponse( userForm, personalDetails, loginDetails, paymentAndServiceDetails, profileDetails, userRolesByGroup );

   		
   		//fetching roles(group & subgroup)
		if(userRolesByGroup.getRoleType().equals("N")) {//system role
			
		HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> rolesMap = (new UserRolesDAO()).loadRolesListByUserId_new(p_con, userForm.getUserId(), userForm.getCategoryCode(), "N");
			userRolesByGroup.setSystemRolesMap(rolesMap);
		}
		
		 final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
         final ArrayList serviceList = servicesDAO.loadUserServicesList(p_con, searchUserVO.getUserID());
         final ArrayList parentServiceList = servicesDAO.loadUserServicesList(p_con, searchUserVO.getParentID());
         removeServices(serviceList, parentServiceList); 
         p_response.setServicesList(serviceList);
         p_response.setParentservicesList(parentServiceList);
   		// final response
   		p_response.setPersonalDetails(personalDetails);
   		p_response.setLoginDetails(loginDetails);
   		p_response.setPaymentAndServiceDetails(paymentAndServiceDetails);
   		p_response.setProfileDetails(profileDetails);
   		p_response.setGroupedUserRoles(userRolesByGroup);
   	}
   	
   	
   	/**
   	 * 
   	 * @param id
   	 * @param userForm
   	 * @throws BTSLBaseException
   	 */
   	private void validateRequestDetails(String id, UserForm userForm) throws BTSLBaseException {
   		final String methodName = "validateRequestDetails";
   		if(log.isDebugEnabled()) {
   			log.debug(methodName, "Entered login id: "  + BTSLUtil.maskParam(id));
   		}
   		if (BTSLUtil.isNullString(id)) 
   		{
   			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOGINID_BLANK, 0, null, null);
   		} else 
   		{
   			userForm.setSearchLoginId(id);
   		} 
   	}
   	
   	/**
   	 * 
   	 * @param con
   	 * @param userForm
   	 * @param sessionUserVO	
   	 * @throws BTSLBaseException
   	 * @throws Exception
   	 */
   	@SuppressWarnings({"rawtypes" , "unchecked"})
       public void loadStaffUserDetails(Connection con,  UserForm userForm, UserVO sessionUserVO , LoginDetailsVO p_loginDetailsVO ) throws BTSLBaseException, Exception {
           final String methodName = "loadStaffUserDetails";
           if (log.isDebugEnabled()) {
               log.debug(methodName, "Entered");
           }
           UserWebDAO userwebDAO = null;
           ChannelUserWebDAO channelUserWebDAO = null;
           ChannelUserVO channelUserVO = null;
           try {
               channelUserWebDAO = new ChannelUserWebDAO();
               //final UserForm theForm = userForm;
               final HashMap map = new HashMap();
               String[] arr = null;
               UserPhoneVO phoneVO = null;
               UserPhoneVO oldPhoneVO = null;
               userForm.setPwdGenerateAllow("N");
               final String JSPForward = "userDetails";
               final String errorJSPForward = "search";
               if (BTSLUtil.isNullString(userForm.getSearchLoginId()) && BTSLUtil.isNullString(userForm.getUserName())) {
                   map.put("user.staffuser.loadstaffuserdetails.error.search.required", arr);
               }
               if (map.size() > 0) {
                   throw new BTSLBaseException(this, methodName, map, errorJSPForward);
               }
//               mcomCon = new MComConnection();
//               con=mcomCon.getConnection();
//               final UserVO userVO = this.getUserFormSession(request);
               final UserVO userVO = sessionUserVO;
               

                   final String userName = userForm.getUserName();
                   ArrayList userList;
                   if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
                       userList = new UserWebDAO().loadStaffUserList(con, PretupsI.STAFF_USER_TYPE, userName);
                   } else {
                       userList = new UserWebDAO().loadChildUserList(con, userVO.getUserID(), PretupsI.STAFF_USER_TYPE, userName);
                   }
                   if ("edit".equals(userForm.getRequestType()) && userList != null) {
                   	
                       final Iterator iterator = userList.iterator();
                       while (iterator.hasNext()) {
                           if (((ListValueVO) iterator.next()).getValue().equals(userVO.getActiveUserID())) {
//                               final BTSLMessages btslMessage = new BTSLMessages("user.staffuser.loadstaffuserdetails.error.search.selfdetailmodify", arr, errorJSPForward);
//                               return forward = super.handleMessage(btslMessage, request, mapping);
                               throw new BTSLBaseException(this , methodName , "user.staffuser.loadstaffuserdetails.error.search.selfdetailmodify", 0 ,arr, null);

                           }
                       }
                   }
                   if (userList == null || userList.size() <= 0) {
                       arr = new String[1];
                       arr[0] = userName;
//                       final BTSLMessages btslMessage = new BTSLMessages("user.selectcategoryforedit.error.usernotexist", arr, errorJSPForward);
//                       return forward = super.handleMessage(btslMessage, request, mapping);
                       throw new BTSLBaseException(this , methodName , PretupsErrorCodesI.NO_USER_EXIST_BY_NAME, 0 ,arr, null);

                   }/* else if (userList.size() == 1) {
                       final ListValueVO listValueVO = (ListValueVO) userList.get(0);
                       theForm.setUserName(listValueVO.getLabel());
                       theForm.setUserId(listValueVO.getValue());
                   } else if (userList.size() > 1) {*/
                	   else if (userList.size() >= 1) {
                       if (!BTSLUtil.isNullString(userForm.getUserId())) {
                           final ListValueVO listValueVO = BTSLUtil.getOptionDesc(userForm.getUserId(), userList);
                           if(BTSLUtil.isNullString(listValueVO.getLabel())) {
                        	   arr = new String[1];
                               arr[0] = userName;
                        	   throw new BTSLBaseException(this , methodName , PretupsErrorCodesI.NO_USER_EXIST_BY_NAME, 0 ,arr, null);
                           }
                           if (!listValueVO.getLabel().equalsIgnoreCase(userName)) {
//                               final BTSLMessages btslMessage = new BTSLMessages("user.selectcategoryforedit.error.usermorethanone", errorJSPForward);
//                               return forward = super.handleMessage(btslMessage, request, mapping);
                               throw new BTSLBaseException(this , methodName , PretupsErrorCodesI.USER_MORE_THAN_ONE, 0 ,arr, null);

                           }
                       } else {
//                           final BTSLMessages btslMessage = new BTSLMessages("user.selectcategoryforedit.error.usermorethanone", errorJSPForward);
//                           return forward = super.handleMessage(btslMessage, request, mapping);
                           throw new BTSLBaseException(this , methodName , PretupsErrorCodesI.USER_MORE_THAN_ONE, 0 ,arr, null);

                       }
                   }
                   channelUserVO = new UserDAO().loadUserDetailsFormUserID(con, userForm.getUserId());
                   phoneVO = new ChannelUserDAO().loadUserPhoneDetails(con, channelUserVO.getUserID());
                   userForm.setAuthTypeAllowed(channelUserVO.getAuthTypeAllowed());
                   if (phoneVO == null && (!SystemPreferences.STAFF_AS_USER)) {
                       phoneVO = new ChannelUserDAO().loadUserPhoneDetails(con, channelUserVO.getParentID());
                   }
                   // oldPhoneVO=new UserPhoneVO(phoneVO);
                   if (phoneVO != null) {
                       oldPhoneVO = new UserPhoneVO(phoneVO);
                       if (PretupsI.NOT_AVAILABLE.equals(phoneVO.getMsisdn())) {
                           phoneVO.setMsisdn("");
                       }
                       phoneVO.setConfirmSmsPin(phoneVO.getShowSmsPin());
                       final ArrayList msisdnList = new ArrayList();
                       final ArrayList oldMsisdnList = new ArrayList();
                       msisdnList.add(phoneVO);
                       oldMsisdnList.add(oldPhoneVO);
                       userForm.setMsisdnList(msisdnList);
                       userForm.setOldMsisdnList(oldMsisdnList);
                   }
                   /*
                    * else
                    * theForm.setMsisdnAssigned("N");
                    */
                   // theForm.setMsisdn(channelUserVO.getSmsMSisdn());//change for
                   // send msg
//               } // Added for Rsa Authentication
               boolean rsaRequired = false;
               final String networkCode = channelUserVO.getNetworkID();
               final String categoryCode = channelUserVO.getCategoryCode();
               try {
                   rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, networkCode, categoryCode)).booleanValue();
                   if (!(PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(channelUserVO.getDomainID())) && rsaRequired == true) {
                       final String webLoginAllowedOrNot = channelUserVO.getCategoryVO().getWebInterfaceAllowed();
                       if ("Y".equalsIgnoreCase(webLoginAllowedOrNot)) {
                           rsaRequired = true;
                       } else {
                           rsaRequired = false;
                       }
                   }
               } catch (Exception e) {
//                   _log.errorTrace(methodName, e);
               	log.errorTrace(methodName, e);
               }
               userForm.setRsaRequired(rsaRequired);
               if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(userVO.getDomainID())) {
                   ChannelUserVO parentVO = null;
                   parentVO = channelUserWebDAO.loadParentUserDetailsByUserID(con, channelUserVO.getUserID());
                   userForm.setStaffParentVO(parentVO);
                   userForm.setCategoryCode(parentVO.getCategoryCode());
                   final CategoryVO catVO = parentVO.getCategoryVO();
                   catVO.setOutletsAllowed("N");
                   if (!SystemPreferences.IS_REQ_MSISDN_FOR_STAFF) {
                       catVO.setSmsInterfaceAllowed("N");
                   }
                   userForm.setCategoryVO(catVO);
                   final GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
                   // load the geographies info from the user_geographies
                   final ArrayList geographyList = _geographyDAO.loadUserGeographyList(con, parentVO.getUserID(), parentVO.getNetworkID());
                   UserGeographiesVO userGeoVO = null;
                   if (catVO.getMultipleGrphDomains().equals(PretupsI.YES)) {
                       String[] geoStr = null;
                       if (geographyList != null && geographyList.size() > 0) {
                           geoStr = new String[geographyList.size()];
                           for (int i = 0, j = geographyList.size(); i < j; i++) {
                               userGeoVO = (UserGeographiesVO) geographyList.get(i);
                               geoStr[i] = userGeoVO.getGraphDomainCode();
                           }
                       }
                       userForm.setGeographicalCodeArray(geoStr);
                   } else if (geographyList != null && geographyList.size() > 0) {
                       userGeoVO = (UserGeographiesVO) geographyList.get(0);
                       userForm.setGeographicalCode(userGeoVO.getGraphDomainCode());
                   }
               }
               p_loginDetailsVO.setInvalidPasswordCount(channelUserVO.getInvalidPasswordCount());
               this.constructVoToForm(userForm, channelUserVO, con);
//               if("edit".equals(theForm.getRequestType()))
//               {
//               	setDatesToDisplayInForm(form);
//               }
//               forward = mapping.findForward(JSPForward);
//               if ("view".equals(theForm.getRequestType())) {
//                   forward = this.detailView(mapping, theForm, request, response);
//               }
           } catch(BTSLBaseException be) {
       	throw be;
       }  catch (Exception e) {
           throw e;
       } finally {
           if (log.isDebugEnabled()) {
               log.debug(methodName, "Exiting");
           }
       }
   }
       
       /**
        * 
        * @param userForm
        * @param personalDetails
        * @param loginDetails
        * @param paymentAndServiceDetails
        * @param profileDetails
        * @param groupedUserRoles
        */
   	@SuppressWarnings("rawtypes")
       private void setUserDetailsResponse(UserForm userForm, PersonalDetailsVO personalDetails, LoginDetailsVO loginDetails, PaymentAndServiceDetailsVO paymentAndServiceDetails,
       		ProfileDetailsVO profileDetails, GroupedUserRolesVO groupedUserRoles) {
       	
       	if(log.isDebugEnabled()) {
   			log.debug("setUserDetailsResponse", "Entered... ");
   		}
       	/*
   		 * Setting User Personal Details
   		 */
   		personalDetails.setNamePrefix(userForm.getUserNamePrefixCode());
       	personalDetails.setFirstName(userForm.getFirstName());
       	personalDetails.setLastName(userForm.getLastName());
   		personalDetails.setShortName(userForm.getShortName());
   		personalDetails.setMsisdn(userForm.getMsisdn());
   		personalDetails.setEmailId(userForm.getEmail());
   		personalDetails.setDesignation(userForm.getDesignation());
   		personalDetails.setAddressLine1(userForm.getAddress1());
   		personalDetails.setAddressLine2(userForm.getAddress2());
   		personalDetails.setAppointmentDate(userForm.getAppointmentDate());
   		personalDetails.setCity(userForm.getCity());
   		personalDetails.setState(userForm.getState());
   		personalDetails.setCountry(userForm.getCountry());
   		personalDetails.setStatus(userForm.getStatus());
   		personalDetails.setStatusDesc(userForm.getStatusDesc());
   		personalDetails.setSubscriberCode(userForm.getEmpCode()); // subscriber code = employee code
   		personalDetails.setUserId(userForm.getUserId());
   		personalDetails.setContactNumber(userForm.getContactNo());
   		personalDetails.setUserName(userForm.getChannelUserName());
   		/*
   		 * Setting login details
   		 */
   		loginDetails.setAllowedDays(userForm.getAllowedDays());
   		loginDetails.setAllowedIp(userForm.getAllowedIPs());
   		loginDetails.setAllowedFromTime(userForm.getAllowedFormTime());
   		loginDetails.setAllowedToTime(userForm.getAllowedToTime());
   		loginDetails.setLoginId(userForm.getWebLoginID());
   		loginDetails.setUserCode(userForm.getUserCode());
   		loginDetails.setNetworkCode(userForm.getNetworkCode());
   		/*
   		 * Setting Login Details
   		 */
   		ArrayList<String> secMsisdnList = new ArrayList<>();
   		ArrayList phoneVOList = userForm.getMsisdnList();
   		
   		if ( !BTSLUtil.isNullOrEmptyList(phoneVOList) ) {
   			
   				for(int listIndex = 0; listIndex < phoneVOList.size(); listIndex++ ) {
   					UserPhoneVO phoneVO = (UserPhoneVO) phoneVOList.get(listIndex);
   					
   					if("N".equalsIgnoreCase(phoneVO.getPrimaryNumber())) 
   					{
   						secMsisdnList.add(phoneVO.getMsisdn());
   					} else 
   					{ // only one primary number at a time
   						loginDetails.setPrimaryMsisdn(phoneVO.getMsisdn());
   						loginDetails.setProfileName(phoneVO.getPhoneProfileDesc());
   						loginDetails.setIsPrimary(phoneVO.getPrimaryNumber());
   						loginDetails.setDescription(phoneVO.getDescription());
   						loginDetails.setInvalidPinCount(phoneVO.getInvalidPinCount());
   						loginDetails.setUserPhoneId(phoneVO.getUserPhonesId());
   						String encryptedPin = null;
   						if(!BTSLUtil.isNullString(phoneVO.getShowSmsPin())) {
   							encryptedPin = AESEncryptionUtil.aesEncryptor(phoneVO.getShowSmsPin(), Constants.A_KEY);
   						}
   						loginDetails.setPin(encryptedPin);
   					}
   			}
   				loginDetails.setSecMsisdn(secMsisdnList);
   			
   		}	
   		
   		/*
   		 * Setting Service List
   		 */
   				paymentAndServiceDetails.setServiceInformation(userForm.getServicesTypes());
   				paymentAndServiceDetails.setServiceTypes(userForm.getServicesTypes());
   		
   		/*
   		 * Setting user roles and role type: system role or group role
   		 */
    
   		HashMap systemRolesMap = new HashMap(); 
   		HashMap groupRolesMap = new HashMap();

   		if (userForm.getRolesMap() != null && userForm.getRolesMap().size() > 0) {
   			this.populateSelectedRoles(userForm , systemRolesMap , groupRolesMap);
   		}
   		groupedUserRoles.setRoleType(userForm.getRoleType());
   		 if ("N".equals(userForm.getRoleType())) {
              groupedUserRoles.setRoleTypeDesc("System Roles");
          } else {
              groupedUserRoles.setRoleTypeDesc("Group Role");

          }
   		groupedUserRoles.setGroupRolesMap(groupRolesMap);
   		groupedUserRoles.setSystemRolesMap(systemRolesMap);
   		    	
       }
       
       /**
        * 
        * @param theForm
        * @param p_channelUserVO
        * @param p_con
        * @throws ParseException
        * @throws BTSLBaseException
        */
   	@SuppressWarnings({"rawtypes" , "unchecked"})
       private void constructVoToForm(UserForm theForm, ChannelUserVO p_channelUserVO, Connection p_con) throws ParseException, BTSLBaseException {
           final String methodName = "constructVoToForm";
           if (log.isDebugEnabled()) {
               log.debug(methodName, "Entered  p_channelUserVO=" + BTSLUtil.maskParam(p_channelUserVO.toString()));
           }
           ChannelUserWebDAO channelUserWebDAO = null;
           try {
               channelUserWebDAO = new ChannelUserWebDAO();
               theForm.setUserId(p_channelUserVO.getUserID());
               theForm.setChannelUserName(p_channelUserVO.getUserName());
               theForm.setUserNamePrefixCode(p_channelUserVO.getUserNamePrefix());
               theForm.setUserNamePrefixList(LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
               theForm.setShortName(p_channelUserVO.getShortName());
               theForm.setCategoryCode(p_channelUserVO.getCategoryCode());
               theForm.setEmpCode(p_channelUserVO.getEmpCode());
               theForm.setContactNo(p_channelUserVO.getContactNo());
               theForm.setContactPerson(p_channelUserVO.getContactPerson());
               // Added for RSA Authentication
               theForm.setRsaAuthentication(p_channelUserVO.getRsaFlag());
               theForm.setSsn(p_channelUserVO.getSsn());
               theForm.setDesignation(p_channelUserVO.getDesignation());
               theForm.setAddress1(p_channelUserVO.getAddress1());
               theForm.setAddress2(p_channelUserVO.getAddress2());
               theForm.setCity(p_channelUserVO.getCity());
               theForm.setState(p_channelUserVO.getState());
               theForm.setCountry(p_channelUserVO.getCountry());
               theForm.setEmail(p_channelUserVO.getEmail());
               theForm.setStatusDesc(p_channelUserVO.getStatusDesc());
               //added by deepanshu
               theForm.setFirstName(p_channelUserVO.getFirstName());
               theForm.setLastName(p_channelUserVO.getLastName());
               theForm.setMsisdn(p_channelUserVO.getMsisdn());
               // added by shashank
               theForm.setStatus(p_channelUserVO.getStatus());
               theForm.setPreviousStatus(p_channelUserVO.getPreviousStatus());
               // end
               if (p_channelUserVO.getAppointmentDate() != null) {
                   theForm.setAppointmentDate(BTSLUtil.getDateStringFromDate(p_channelUserVO.getAppointmentDate()));
               }
               theForm.setWebLoginID(p_channelUserVO.getLoginID());
               theForm.setOldWebLoginID(p_channelUserVO.getLoginID());
               String password = null;
               if (!BTSLUtil.isNullString(p_channelUserVO.getPassword())) {
                   password = BTSLUtil.getDefaultPasswordText(BTSLUtil.decryptText(p_channelUserVO.getPassword()));

               }
               theForm.setWebPassword(p_channelUserVO.getPassword());
               theForm.setShowPassword(password);
               theForm.setConfirmPassword(password);

               theForm.setAllowedIPs(p_channelUserVO.getAllowedIps());
               if (p_channelUserVO.getAllowedDays() != null && p_channelUserVO.getAllowedDays().trim().length() > 0) {
                   theForm.setAllowedDays(p_channelUserVO.getAllowedDays().split(","));
               }
               theForm.setAllowedFormTime(p_channelUserVO.getFromTime());
               theForm.setAllowedToTime(p_channelUserVO.getToTime());
               // load the roles info from the user_roles table that are assigned
               // with the user
               final UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();

               // To load all the roles of staff user.
               final ArrayList assignedRoleList = rolesWebDAO.loadUserRolesList(p_con, p_channelUserVO.getUserID());

               // modified for level 1 staff user approval
               HashMap hashMap = null;
               if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(theForm.getLoginUserDomainID())) {
                   ChannelUserVO parentVO = null;
                   parentVO = channelUserWebDAO.loadParentUserDetailsByUserID(p_con, p_channelUserVO.getUserID());
                   theForm.setStaffParentVO(parentVO);
                   // ChannelUserVO parentVO = theForm.getStaffParentVO();
                   if (!BTSLUtil.isNullString(parentVO.getUserID()) && !BTSLUtil.isNullString(parentVO.getCategoryVO().getDomainTypeCode())) {
                       final boolean isGroupRole = rolesWebDAO.isGroupRole(p_con, parentVO.getUserID(), parentVO.getCategoryVO().getDomainTypeCode());
                       if (isGroupRole) {
                           hashMap = rolesWebDAO.loadGroupRoleRolesListByUserID(p_con, parentVO.getUserID(), parentVO.getCategoryCode());
                       } else {
                           hashMap = rolesWebDAO.loadRolesListByUserID(p_con, parentVO.getUserID(), parentVO.getCategoryCode(), "N");
                       }
                   }
               } else {
                   // To check whether role associated with login channel user is a
                   // group role or not and load those roles.
                   final boolean isGroupRole = rolesWebDAO.isGroupRole(p_con, theForm.getLoginUserID(), theForm.getLoginUserDomainCode());
                   if (isGroupRole) {
                       hashMap = rolesWebDAO.loadGroupRoleRolesListByUserID(p_con, theForm.getLoginUserID(), theForm.getLoginUserCategoryCode());
                   } else {
                       hashMap = rolesWebDAO.loadRolesListByUserID(p_con, theForm.getLoginUserID(), theForm.getLoginUserCategoryCode(), "N");
                   }
               }
               /**
                * There are different associted roles with channel users which are
                * avalibale in map.
                * The map key is Group Name and value as UserRolesVO arraylist.
                * There would we different key,
                * and each key have arralist. in the below code we populate the one
                * arraylist
                * which have all smalll arraylist. The purpose is to make easy
                * filteration of
                * role of staff user and roles associated with login channel user.
                */
               final ArrayList temList = new ArrayList();
               if (hashMap != null) {

                   final Iterator iterator = hashMap.keySet().iterator();
                   while (iterator.hasNext()) {
                       temList.addAll((ArrayList) hashMap.get(iterator.next()));
                   }
               }

               final ArrayList finalList = new ArrayList();
               for (int m = 0, n = temList.size(); m < n; m++) {
                   final UserRolesVO userRolesVO = (UserRolesVO) temList.get(m);
                   for (int i = 0, k = assignedRoleList.size(); i < k; i++) {
                       if (userRolesVO.getRoleCode().equals(assignedRoleList.get(i))) {
                           finalList.add(assignedRoleList.get(i));
                       }
                   }
               }

               theForm.setRolesMap(hashMap);
               if (finalList != null && finalList.size() > 0) {
                   final String[] arr = new String[finalList.size()];
                   finalList.toArray(arr);
                   theForm.setRoleFlag(arr);
               }

               if (theForm.getRolesMap() != null && theForm.getRolesMap().size() > 0) {
                   // this method populate the selected roles
//                   new ChannelUserAction().populateSelectedRoles(theForm); // removed and new method created in this class with same name & functionality , called from #setUserDetailsResponse deepanshu
               } else {
                   // by default set Role Type = N(means System Role radio button
                   // will be checked in edit mode if no role assigned yet)
                   theForm.setRoleType("N");
               }
              /* 
               
               final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
               final ArrayList serviceList = servicesDAO.loadUserServicesList(p_con, p_channelUserVO.getUserID());
               final ArrayList parentServiceList = servicesDAO.loadUserServicesList(p_con, p_channelUserVO.getParentID());
               removeServices(serviceList, parentServiceList); 
               if (serviceList != null && serviceList.size() > 0) {
                   final String[] arr = new String[serviceList.size()];
                   for (int i = 0, j = serviceList.size(); i < j; i++) {
                       final ListValueVO listVO = (ListValueVO) serviceList.get(i);
//                       arr[i] = listVO.getValue();
                       arr[i] = listVO.getLabel();
                   }
                   theForm.setServicesTypes(arr);
               }
               theForm.setServicesList(parentServiceList); */
               
               
           } catch (ParseException | BTSLBaseException e) {
               log.errorTrace(methodName, e);
               throw e;
           } 
           if (log.isDebugEnabled()) {
               log.debug(methodName, "Exiting  p_channeleUserVO=" + p_channelUserVO);
           }
       }
       
       /**
        * 
        * @param p_servicesList
        * @param p_servicesListParent
        */
   	@SuppressWarnings("rawtypes")
       private void removeServices(ArrayList p_servicesList, ArrayList p_servicesListParent) {
           final String methodName = "removeServices";
           if (log.isDebugEnabled()) {
               log.debug(methodName, "Entered p_servicesList.size()=" + p_servicesList.size() + "p_servicesListParent.size()=" + p_servicesListParent.size());
           }
           ListValueVO itemParent = null;
           ListValueVO item = null;
           boolean isExist = false;
           for (int i = 0, j = p_servicesList.size(); i < j; i++) {
               isExist = false;
               item = (ListValueVO) p_servicesList.get(i);
               for (int k = 0, l = p_servicesListParent.size(); k < l; k++) {
                   itemParent = (ListValueVO) p_servicesListParent.get(k);
                   if (itemParent.getValue().equals(item.getValue())) {
                       isExist = true;
                       break;
                   }
               }
               if (!isExist) {
                   p_servicesList.remove(i);
                   j--;
                   i--;
               }
           }
           if (log.isDebugEnabled()) {
               log.debug(methodName, "Exited p_servicesList.size()=" + p_servicesList.size() + "p_servicesListParent.size()=" + p_servicesListParent.size());
           }
       }
       
       /*
        * This method populate the selected roles from the map, which contains
        * all list of the roles
        */
       /**
        * 
        * @param userForm
        * @param systemRolesMap
        * @param groupRolesMap
        */
   	@SuppressWarnings({"rawtypes" , "unchecked"})
       public void populateSelectedRoles(UserForm userForm , HashMap systemRolesMap , HashMap groupRolesMap) {
           final String methodName = "populateSelectedRoles";
           if (log.isDebugEnabled()) {
               log.debug(methodName, "Entered");
           }
           
           final UserForm theForm = userForm;
           final HashMap mp = theForm.getRolesMap();
           final HashMap newSelectedMap = new HashMap();
           final Iterator it = mp.entrySet().iterator();
           String key = null;
           ArrayList list = null;
           ArrayList listNew = null;
           UserRolesVO roleVO = null;
           Map.Entry pairs = null;
           boolean foundFlag = false;
           ViewUserRolesVO viewUserRolesVO = null;
//           groupRolesMap = new HashMap();
//           systemRolesMap = new HashMap();

           while (it.hasNext()) {
               pairs = (Map.Entry) it.next();
               key = (String) pairs.getKey();
               list = new ArrayList((ArrayList) pairs.getValue());
               listNew = new ArrayList();
               foundFlag = false;
               if (list != null) {
                   for (int i = 0, j = list.size(); i < j; i++) {
                       roleVO = (UserRolesVO) list.get(i);
                       if (theForm.getRoleFlag() != null && theForm.getRoleFlag().length > 0) { 
                           for (int k = 0; k < theForm.getRoleFlag().length; k++) {
                               if (roleVO.getRoleCode().equals(theForm.getRoleFlag()[k])) {
                                    // listNew.add(roleVO);
                               	viewUserRolesVO = new ViewUserRolesVO();
                               	viewUserRolesVO.setGroupRole(roleVO.getGroupRole());
                               	viewUserRolesVO.setRoleCode(roleVO.getRoleCode());
                               	viewUserRolesVO.setRoleName(roleVO.getRoleName());
                               	viewUserRolesVO.setGroupName(roleVO.getGroupName());
                               	viewUserRolesVO.setRoleType(roleVO.getRoleType());
                               	listNew.add(viewUserRolesVO);
                               	
                               	
                                   foundFlag = true;
                                   theForm.setRoleType(roleVO.getGroupRole());
                               }
                           }
                       }
                   }
               }
               if (foundFlag) {
                   newSelectedMap.put(key, listNew);
                   if("Y".equals(roleVO.getGroupRole())) {
                   	groupRolesMap.put(key, listNew);
                   	
                   } else if("N".equals(roleVO.getGroupRole())) {
                   	systemRolesMap.put(key, listNew);
                   }
               }
           }
           if (newSelectedMap.size() > 0) {
               theForm.setRolesMapSelected(newSelectedMap);
           } else {
               // by default set Role Type = N(means System Role radio button will
               // be checked in edit mode if no role assigned yet)
               if (SystemPreferences.CHANNEL_USER_ROLE_TYPE_DISPLAY.equalsIgnoreCase(PretupsI.SYSTEM)) {
                   theForm.setRoleType("N");
               } else if (SystemPreferences.CHANNEL_USER_ROLE_TYPE_DISPLAY.equalsIgnoreCase(PretupsI.GROUP)) {
                   theForm.setRoleType("Y");

               } else {
                   theForm.setRoleType("N");
               }
               theForm.setRolesMapSelected(null);
           }

           if (log.isDebugEnabled()) {
               log.debug(methodName, "Exiting");
           }
       }
   	
   	private void updateUserPhoneVO(Connection con,EditMsisdn editMsisdn, UserPhoneVO userPhoneVO, OperatorUtilI operatorUtili, ChannelUserVO staffUserVO, ChannelUserVO channelUserVO ) throws Exception {
   		final String METHOD_NAME = "updateUserPhoneVO";
   		if (log.isDebugEnabled()) {
	         log.debug(METHOD_NAME, "Entered");
	     }
		
   		NetworkPrefixVO networkPrefixVO = null;
   		boolean msisdnCheck=false;
   		final Date currentDate = new Date();
   		String filterMsisdn="";
   		
   		//handle phone no
   		if(!editMsisdn.getOldPhoneNo().equals(editMsisdn.getPhoneNo())) {
   			filterMsisdn = PretupsBL.getFilteredMSISDN(editMsisdn.getPhoneNo());
   			userPhoneVO.setMsisdn(filterMsisdn);
   			NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(userPhoneVO.getMsisdn())));
   	   		userPhoneVO.setPrefixID(prefixVO.getPrefixID());
   	   		msisdnCheck=true;
   		}
   		
   		//handled pin
   		if(editMsisdn.getIsprimary().equals("Y")) staffUserVO.setMsisdn(userPhoneVO.getMsisdn());
   		if(SystemPreferences.AUTO_PIN_GENERATE_ALLOW) {	editMsisdn.setPin(operatorUtili.generateRandomPin()); userPhoneVO.setPinModifyFlag(true);  userPhoneVO.setPinModifiedOn(currentDate);}
   		else if(!BTSLUtil.isNullString(editMsisdn.getPin()) && !editMsisdn.getOldPin().equals(editMsisdn.getPin())) { userPhoneVO.setPinModifyFlag(true);  userPhoneVO.setPinModifiedOn(currentDate);}
   		if(!BTSLUtil.isNullString(editMsisdn.getPin())) userPhoneVO.setSmsPin(BTSLUtil.encryptText(editMsisdn.getPin()));
   		
   		//handling is primary no
   		if (editMsisdn.getIsprimary().equals("Y")){
            userPhoneVO.setPrimaryNumber(PretupsI.YES);
        } else {
            userPhoneVO.setPrimaryNumber(PretupsI.NO);
        }
   		
   		userPhoneVO.setModifiedBy(channelUserVO.getActiveUserID());
   		userPhoneVO.setDescription(editMsisdn.getDescription());
   		
   		if(!(BTSLUtil.isValidMSISDN(userPhoneVO.getMsisdn())))
        {
        	throw new BTSLBaseException("AddChannelUser", METHOD_NAME, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
        }
        if(!(BTSLUtil.isValidMSISDNDigit(userPhoneVO.getMsisdn())))
        {
        	throw new BTSLBaseException("AddChannelUser", METHOD_NAME, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
        }
        if(!(BTSLUtil.isValidMSISDNLength(userPhoneVO.getMsisdn())))
        {
        	throw new BTSLBaseException("AddChannelUser", METHOD_NAME, PretupsErrorCodesI.EXTSYS_MSISDN_MAP_LENGTH_EXCEEDS);
        }
        final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(userPhoneVO.getMsisdn())));
        if((prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserVO.getNetworkID()))) {
            throw new BTSLBaseException("AddChannelUser", METHOD_NAME, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
        }
        
        
        if (msisdnCheck) {
            if (SystemPreferences.MNP_ALLOWED) {
                boolean numberAllowed = false;
                if (prefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_IN);
                    if (!numberAllowed) {
                        throw new BTSLBaseException(this, "save", "user.assignphone.error.msisdnnotinsamenetwork", 0,
                                        new String[] { filterMsisdn, prefixVO.getNetworkName() }, "Detail");
                    }
                } else {
                    numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_OUT);
                    if (numberAllowed) {
                        throw new BTSLBaseException(this, "save", "user.assignphone.error.msisdnnotinsamenetwork", 0,
                                        new String[] { filterMsisdn, prefixVO.getNetworkName() }, "Detail");
                    }
                }
            }
            if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(userPhoneVO.getMsisdn()), userPhoneVO.getUserId())) {
                final String[] arr = { userPhoneVO.getMsisdn() };
                throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.MSISDN_EXIST,arr ) ;
            }

            // check if pin exist in password history table
            // when change
            // if(phoneVO.isPinModifyFlag()&&
            // ("edit".equals(theForm.getRequestType()))){
            if ((!SystemPreferences.AUTO_PIN_GENERATE_ALLOW) && userPhoneVO.isPinModifyFlag()) {

                if (userDAO.checkPasswordHistory(con, PretupsI.USER_PIN_MANAGEMENT, userPhoneVO.getUserId(), PretupsBL.getFilteredMSISDN(userPhoneVO.getMsisdn()),
                                BTSLUtil.encryptText(userPhoneVO.getShowSmsPin()))) {
                    throw new BTSLBaseException(this, "save", "channeluser.changepin.error.pinhistory", 0, new String[] { String
                                    .valueOf(SystemPreferences.PREV_PIN_NOT_ALLOW), userPhoneVO.getMsisdn() }, "Detail");
                }
            }
        }
        
   		userPhoneVO.setOperationType("U");
   		if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting");
        }
   	}
	
	
   	private List prepareUserPhoneVOList(Connection con, EditMsisdn[] msisdns, StaffUserEditRequestVO requestVO, ChannelUserVO channelUserVO, Date currentDate, String senderPin,OperatorUtilI operatorUtili, ChannelUserVO staffUserVO) throws Exception {
        final String methodName = "prepareUserPhoneVOList";
        LogFactory.printLog(methodName, "Entered channelUserVO.getCategoryCode()=" + channelUserVO.getCategoryCode(), log);
    	
        final List <UserPhoneVO>phoneList = new ArrayList<UserPhoneVO>();
        List <EditMsisdn>msisdnList=new ArrayList();
        NetworkPrefixVO networkPrefixVO = null;
        EditMsisdn m = new EditMsisdn();
        for(int i=0;i<msisdns.length;i++)
        {
        	m=msisdns[i];
        	if(SystemPreferences.AUTO_PIN_GENERATE_ALLOW)
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
        EditMsisdn msisdn= null;
        if (msisdnList != null && !msisdnList.isEmpty()) {
            UserPhoneVO phoneVO = null;
            for (int i = 0, j = msisdnList.size(); i < j; i++) {
                msisdn = (EditMsisdn) msisdnList.get(i);
                if (!BTSLUtil.isNullString(msisdn.getPhoneNo())) {
                    phoneVO = new UserPhoneVO();
                    phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(msisdn.getPhoneNo()));
                    phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                    phoneVO.setUserId(staffUserVO.getUserID());
                    phoneVO.setSmsPin(BTSLUtil.encryptText(msisdn.getPin()));
                    phoneVO.setPinRequired(PretupsI.YES);
                    // set the default values
                    phoneVO.setCreatedBy(channelUserVO.getUserID());
                    phoneVO.setModifiedBy(channelUserVO.getUserID());
                    phoneVO.setCreatedOn(currentDate);
                    phoneVO.setModifiedOn(currentDate);
                    phoneVO.setPinModifiedOn(currentDate);
                    ArrayList languageList=LocaleMasterDAO.loadLocaleMasterData();
                    if (!BTSLUtil.isNullString(requestVO.getStaffUserEditDetailsdata().getLanguage())) {
                    boolean flag1=true;
                    for(int k=0;k<languageList.size();k++)
                    {
                    	if(((ListValueVO)languageList.get(k)).getValue().equals(requestVO.getStaffUserEditDetailsdata().getLanguage()))
                    	{
                    		flag1=false;
                    	}
                    }
                    if(flag1==true)
                    {
                    	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LANGUAGE_DOES_NOT_EXIST);
                    }
                    }
                    if (!BTSLUtil.isNullString(requestVO.getStaffUserEditDetailsdata().getLanguage())) {
                        final String lang_country[] = (requestVO.getStaffUserEditDetailsdata().getLanguage()).split("_");
                        phoneVO.setPhoneLanguage(lang_country[0]);
                        phoneVO.setCountry(lang_country[1]);
                    } else {
                        phoneVO.setPhoneLanguage((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
                        phoneVO.setCountry((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
                    }
                   /* if(msisdn.getStkProfile()==null)
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_FOUND);
                    }
                    else if(!(msisdn.getStkProfile().equals(stkProfile)))
                    {
                    	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_VALID);
                    }*/
                    phoneVO.setPhoneProfile(stkProfile);
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
                    if((prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserVO.getNetworkID()))) {
                        throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH);
                    }
                    phoneVO.setPrefixID(prefixVO.getPrefixID());
                    if (msisdn.getIsprimary().equals("Y")){
                        channelUserVO.setMsisdn(msisdn.getPhoneNo());
                        phoneVO.setPrimaryNumber(PretupsI.YES);
                    } else {
                        phoneVO.setPrimaryNumber(PretupsI.NO);
                    }

                    if (SystemPreferences.MNP_ALLOWED) {
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
        }
        channelUserVO.setMsisdnList((ArrayList)phoneList);
        return phoneList;
    }

   	private void constructFormToVO(StaffUserEditDetails staffUserDetails, ChannelUserVO p_channelUserVO, ChannelUserVO sessionUserVO) throws Exception {
        final String methodName = "staffUserDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_channelUserVO=" + p_channelUserVO);
        }
        try {
            String password = null;
           // p_channelUserVO.setUserID(staffUserDetails.getUserId());
            p_channelUserVO.setNetworkID(sessionUserVO.getNetworkID());
            if(BTSLUtil.isNullString(staffUserDetails.getUserName())) {
            	if(BTSLUtil.isNullString(staffUserDetails.getLastName())){
            	 p_channelUserVO.setUserName(staffUserDetails.getFirstName());
                 staffUserDetails.setUserName(staffUserDetails.getFirstName());
                 p_channelUserVO.setFirstName(staffUserDetails.getFirstName());
            	 }else {
                  p_channelUserVO.setUserName(staffUserDetails.getFirstName()+" "+staffUserDetails.getLastName());
                  staffUserDetails.setUserName(staffUserDetails.getFirstName()+" "+staffUserDetails.getLastName());
                  p_channelUserVO.setFirstName(staffUserDetails.getFirstName());
                  p_channelUserVO.setLastName(staffUserDetails.getLastName());
            	 }
            }else {
               p_channelUserVO.setUserName(staffUserDetails.getUserName());
               p_channelUserVO.setFirstName(staffUserDetails.getUserName());
            }

            Object isFnameAllowed =  PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
            if(isFnameAllowed.equals("true") && BTSLUtil.isNullString(staffUserDetails.getFirstName())) {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FIRST_NAME_BLANK);
            }
            if(!isFnameAllowed.equals("true")  && BTSLUtil.isNullString(staffUserDetails.getUserName())) {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.XML_ERROR_USER_NAME_BLANK);
            }
          
           
            if(BTSLUtil.isNullString(staffUserDetails.getWebloginid())) {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOGINID_BLANK);
            }
            p_channelUserVO.setLoginID(staffUserDetails.getWebloginid());
            
            p_channelUserVO.setPasswordModifyFlag(false);
            
            p_channelUserVO.setCategoryCode(sessionUserVO.getCategoryCode());
            p_channelUserVO.setParentID(sessionUserVO.getUserID());
            
            p_channelUserVO.setOwnerID(sessionUserVO.getOwnerID());
            p_channelUserVO.setAllowedIps(staffUserDetails.getAllowedip());
            final StringBuffer str = new StringBuffer();
            /*
             * theForm.getAllowedDays returns an string array but in DB we
             * insert a single
             * string value of the allowed days like 1,4,7, for this prupose
             * convert the string
             * array into string
             */
            if (staffUserDetails.getAlloweddays() != null && !BTSLUtil.isNullString(staffUserDetails.getAlloweddays()) && staffUserDetails.getAlloweddays().split(",").length > 0) {
                str.append(staffUserDetails.getAlloweddays().split(",")[0]);
                for (int i = 1, j = staffUserDetails.getAlloweddays().split(",").length; i < j; i++) {
                    str.append("," + staffUserDetails.getAlloweddays().split(",")[i]);
                }
                p_channelUserVO.setAllowedDays(str.toString());
            }else {
            	 //p_channelUserVO.setAllowedDays("1,2,3,4,5,6,7");
            	p_channelUserVO.setAllowedDays(null);
            	 
            }
           
            p_channelUserVO.setFromTime(staffUserDetails.getAllowedTimeFrom());
            p_channelUserVO.setToTime(staffUserDetails.getAllowedTimeTo());
            p_channelUserVO.setEmpCode(staffUserDetails.getSubscriberCode());
            p_channelUserVO.setContactNo(staffUserDetails.getContactNumber());
            p_channelUserVO.setEmail(staffUserDetails.getEmailid());
            p_channelUserVO.setDesignation(staffUserDetails.getDesignation());
            // while adding Staff user userType value will be STAFF
            p_channelUserVO.setUserType(PretupsI.STAFF_USER_TYPE);
       
            p_channelUserVO.setContactPerson(sessionUserVO.getContactPerson());
            p_channelUserVO.setUserGrade(sessionUserVO.getUserGrade());
            p_channelUserVO.setTransferProfileID(sessionUserVO.getTransferProfileID());
            p_channelUserVO.setCommissionProfileSetID(sessionUserVO.getCommissionProfileSetID());
            p_channelUserVO.setInSuspend(sessionUserVO.getInSuspend());
            p_channelUserVO.setOutSuspened(sessionUserVO.getOutSuspened());
            p_channelUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
            p_channelUserVO.setAddress1(staffUserDetails.getAddress1());
            p_channelUserVO.setAddress2(staffUserDetails.getAddress2());
            p_channelUserVO.setCity(staffUserDetails.getCity());
            p_channelUserVO.setState(staffUserDetails.getState());
            p_channelUserVO.setCountry(staffUserDetails.getCountry());
             p_channelUserVO.setSsn(sessionUserVO.getSsn());
            // Added for RSA Authentication
            p_channelUserVO.setRsaFlag(sessionUserVO.getRsaFlag());
            p_channelUserVO.setUserNamePrefix(staffUserDetails.getUserNamePrefix());
            p_channelUserVO.setExternalCode(sessionUserVO.getExternalCode());
            p_channelUserVO.setShortName(staffUserDetails.getShortName());
            if (!BTSLUtil.isNullString(staffUserDetails.getAppointmentdate())) {
                p_channelUserVO.setAppointmentDate(BTSLUtil.getDateFromDateString(staffUserDetails.getAppointmentdate()));
            }
            p_channelUserVO.setOutletCode(sessionUserVO.getOutletCode());
            p_channelUserVO.setSubOutletCode(sessionUserVO.getSubOutletCode());
            // modified for staff user approval
            if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(sessionUserVO.getDomainID())) {
         /*
                final ChannelUserVO parentVO = staffUserDetails.getStaffParentVO();
                p_channelUserVO.setNetworkID(parentVO.getNetworkID());
                p_channelUserVO.setParentID(parentVO.getUserID());
           
                p_channelUserVO.setOwnerID(parentVO.getOwnerID());
                p_channelUserVO.setContactPerson(parentVO.getContactPerson());
                p_channelUserVO.setUserGrade(parentVO.getUserGrade());
                p_channelUserVO.setTransferProfileID(parentVO.getTransferProfileID());
                p_channelUserVO.setCommissionProfileSetID(parentVO.getCommissionProfileSetID());
                p_channelUserVO.setInSuspend(parentVO.getInSuspend());
                p_channelUserVO.setOutSuspened(parentVO.getOutSuspened());
                p_channelUserVO.setModifiedBy(parentVO.getUserID());
                p_channelUserVO.setOutletCode(parentVO.getOutletCode());
                p_channelUserVO.setSubOutletCode(parentVO.getSubOutletCode());*/
            } else {
                p_channelUserVO.setNetworkID(sessionUserVO.getNetworkID());
                p_channelUserVO.setParentID(sessionUserVO.getUserID());
                p_channelUserVO.setOwnerID(sessionUserVO.getOwnerID());
                p_channelUserVO.setContactPerson(sessionUserVO.getContactPerson());
                p_channelUserVO.setUserGrade(sessionUserVO.getUserGrade());
                p_channelUserVO.setTransferProfileID(sessionUserVO.getTransferProfileID());
                p_channelUserVO.setCommissionProfileSetID(sessionUserVO.getCommissionProfileSetID());
                p_channelUserVO.setInSuspend(sessionUserVO.getInSuspend());
                p_channelUserVO.setOutSuspened(sessionUserVO.getOutSuspened());
                p_channelUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
                p_channelUserVO.setOutletCode(sessionUserVO.getOutletCode());
                p_channelUserVO.setSubOutletCode(sessionUserVO.getSubOutletCode());
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            throw e;
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting  p_channelUserVO=" + p_channelUserVO);
        }
    }
   	
   	
   	private RetValue validateBatchUserInputDetails(Connection con,StaffUserRequestVO  requestVO) throws BTSLBaseException  {
   		final String methodName ="validateBatchUserInputDetails";
   		String inputcategoryCode=  requestVO.getBatchInputParams().getCategoryCode();
   		RetValue retValue = new RetValue();
   		CategoryDAO categoryDao = new CategoryDAO();
   		String userID =null;
   		String parentUserID=null;
   		String staffCategoryCode =null;
   		CategoryVO staffcategoryVO=null;
   		CategoryVO parentcategoryVO=null;
   		if(requestVO.getBatchInputParams()!=null){
   			
   			if(BTSLUtil.isNullString(requestVO.getBatchInputParams().getCategoryCode()) && !BTSLUtil.isNullString(requestVO.getBatchInputParams().getParentCategory())){
   				 staffcategoryVO =	categoryDao.loadCategoryDetailsByCategoryCode(con,requestVO.getBatchInputParams().getCategoryCode());
   				 parentcategoryVO =	categoryDao.loadCategoryDetailsByCategoryCode(con,requestVO.getBatchInputParams().getParentCategory());
   				 if (staffcategoryVO.getSequenceNumber() > parentcategoryVO.getSequenceNumber()) {
   					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.STAFFCAT_GRTRTHAN_PARENTCAT);
   				 }
   				 
   			}
   			
   			if( !requestVO.getBatchInputParams().getCategoryCode().equals(PretupsI.CATEGORY_CODE_DIST)  &&  BTSLUtil.isNullorEmpty(requestVO.getBatchInputParams().getParentCategory())  ){
   				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.STAFFCAT_PARENTCAT_MANDATORY);
   			}
   			
   			
   			if( requestVO.getBatchInputParams().getCategoryCode().equals(PretupsI.CATEGORY_CODE_DIST)  &&  BTSLUtil.isNullorEmpty(requestVO.getBatchInputParams().getParentCategory())  ){
   				requestVO.getBatchInputParams().setParentCategory(PretupsI.CATEGORY_CODE_DIST);
   			}

   			
   			
   			if( requestVO.getBatchInputParams().getCategoryCode().equals(PretupsI.CATEGORY_CODE_DIST) &&  requestVO.getBatchInputParams().getParentCategory().equals(PretupsI.CATEGORY_CODE_DIST)) {
   				userID =channelUserdataValidation( con, requestVO.getBatchInputParams().getChannelUser(),requestVO.getBatchInputParams().getCategoryCode(),null);
   				retValue.setChannelUser(userID);
   				retValue.setOwnerUser(userID);
   		   }else if(( inputcategoryCode.equals(PretupsI.CATEGORY_CODE_DEALER) || inputcategoryCode.equals(PretupsI.CATEGORY_CODE_AGENT)  ) &&  requestVO.getBatchInputParams().getParentCategory().equals(PretupsI.CATEGORY_CODE_DIST)) {
   			   userID= channelUserdataValidation( con, requestVO.getBatchInputParams().getChannelUser(),requestVO.getBatchInputParams().getCategoryCode(),null);
	   			retValue.setOwnerUser( ownerUserdataValidation( con, requestVO.getBatchInputParams().getOwnerUser()));
	   			retValue.setChannelUser(userID);
	   			retValue.setParentUser(userID);
   		   }else if((inputcategoryCode.equals(PretupsI.CATEGORY_CODE_AGENT) || inputcategoryCode.equals(PretupsI.CATEGORY_CODE_RETAILER)) && requestVO.getBatchInputParams().getParentCategory().equals(PretupsI.CATEGORY_CODE_DEALER) ) {
	   			retValue.setOwnerUser(ownerUserdataValidation( con, requestVO.getBatchInputParams().getOwnerUser()));
	   			parentUserID=parentUserdataValidation(con,requestVO.getBatchInputParams().getParentUser(),requestVO.getBatchInputParams().getParentCategory());
	   			retValue.setParentUser(parentUserID);
	   			retValue.setChannelUser(channelUserdataValidation( con, requestVO.getBatchInputParams().getChannelUser(),requestVO.getBatchInputParams().getCategoryCode(),parentUserID));
   		   }else if( inputcategoryCode.equals(PretupsI.CATEGORY_CODE_RETAILER) && requestVO.getBatchInputParams().getParentCategory().equals(PretupsI.CATEGORY_CODE_DIST) ) {
   			userID= channelUserdataValidation( con, requestVO.getBatchInputParams().getChannelUser(),requestVO.getBatchInputParams().getCategoryCode(),null);
	   			retValue.setOwnerUser(ownerUserdataValidation( con, requestVO.getBatchInputParams().getOwnerUser()));
	   			retValue.setChannelUser(userID);
	   			retValue.setParentUser(userID);
   		   }else if( inputcategoryCode.equals(PretupsI.CATEGORY_CODE_RETAILER) && requestVO.getBatchInputParams().getParentCategory().equals(PretupsI.CATEGORY_CODE_AGENT) ) {
	   			retValue.setOwnerUser(ownerUserdataValidation( con, requestVO.getBatchInputParams().getOwnerUser()));
	   			parentUserID=parentUserdataValidation(con,requestVO.getBatchInputParams().getParentUser(),requestVO.getBatchInputParams().getParentCategory());
	   			retValue.setParentUser(parentUserID);
	   			retValue.setChannelUser(channelUserdataValidation( con, requestVO.getBatchInputParams().getChannelUser(),requestVO.getBatchInputParams().getCategoryCode(),parentUserID));
    		}else{
				ArrayList categoryList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con,
						requestVO.getBatchInputParams().getCategoryCode());
				CategoryVO categoryVO = (CategoryVO) categoryList.get(0);
				final String categoryID = String.valueOf(categoryVO.getSequenceNumber());
				if ("1".equals(categoryID)) {
					userID =channelUserdataValidation( con, requestVO.getBatchInputParams().getChannelUser(),requestVO.getBatchInputParams().getCategoryCode(),null);
					retValue.setChannelUser(userID);
					retValue.setOwnerUser(userID);
				}else if((requestVO.getBatchInputParams().getOwnerUser() != null && requestVO.getBatchInputParams().getChannelUser() !=null) && requestVO.getBatchInputParams().getParentCategory().equals(PretupsI.CATEGORY_CODE_DIST) ){
					userID= channelUserdataValidation( con, requestVO.getBatchInputParams().getChannelUser(),requestVO.getBatchInputParams().getCategoryCode(),null);
					retValue.setOwnerUser( ownerUserdataValidation( con, requestVO.getBatchInputParams().getOwnerUser()));
					retValue.setChannelUser(userID);
					retValue.setParentUser(userID);
				}else if((requestVO.getBatchInputParams().getOwnerUser() != null && requestVO.getBatchInputParams().getChannelUser() !=null && requestVO.batchInputParams.getParentUser() != null) && !requestVO.getBatchInputParams().getParentCategory().equals(PretupsI.CATEGORY_CODE_DIST)){
					retValue.setOwnerUser(ownerUserdataValidation( con, requestVO.getBatchInputParams().getOwnerUser()));
					parentUserID=parentUserdataValidation(con,requestVO.getBatchInputParams().getParentUser(),requestVO.getBatchInputParams().getParentCategory());
					retValue.setParentUser(parentUserID);
					retValue.setChannelUser(channelUserdataValidation( con, requestVO.getBatchInputParams().getChannelUser(),requestVO.getBatchInputParams().getCategoryCode(),parentUserID));
				}
			}
   		}
   		return retValue;
   	}
   	
  private String channelUserdataValidation(Connection con,String channeluserLoginID,String categoryCode,String parentUserID) throws  BTSLBaseException{
	  final String methodName ="channelUserdataValidation";
	   userDAO = new UserDAO();
      CategoryDAO categoryDAO = new CategoryDAO();
	   String channelUserID=null;
	   ChannelUserVO channelUserVO =null;
	  if(BTSLUtil.isNullString(channeluserLoginID)) {
		  throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_USER_BLANK);
	  }
	   try {
		    channelUserVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, channeluserLoginID);
		    if(channelUserVO!=null) {
		     	CategoryVO categoryVO = 	categoryDAO.loadCategoryDetailsByCategoryCode(con, categoryCode);
		     	 if(!categoryVO.getCategoryCode().equalsIgnoreCase(channelUserVO.getCategoryCode())) {
		     		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_USER_SHOULD_BE_CAT,new String[] {categoryVO.getCategoryName()});   		 
		     	  }
		    	channelUserID= channelUserVO.getUserID();
		    	if(parentUserID!=null) {
		    List<ChannelUserUnderParentVO> list =	userDAO.checkChannelUnderParentHierarchy(con, channeluserLoginID, parentUserID);
		        if(list!=null && list.size()==0) {
		        	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_USER_NOT_UNDER_PARENT);
		        }
		    
		    
		    
		    	}
		    	
		    	
		    }else {
		    	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_USER_NOT_FOUND);
		    }
		    
		    
		    
	   }catch(SQLException se) {
		   	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_USER_NOT_FOUND);
	   }
	   
	   return channelUserID;
   	}

  private String 	parentUserdataValidation(Connection con,String parentUserLoginID,String parentcategoryCode) throws  BTSLBaseException{
	  final String methodName ="parentUserdataValidation";
	   userDAO = new UserDAO();
	   CategoryDAO categoryDAO = new CategoryDAO();
	   ChannelUserVO channelUserVO = null;
	   String userid=null;
	  if(BTSLUtil.isNullString(parentUserLoginID)) {
		  throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_USER_PARENT_MANDATORY);
	  }
	   try {
		   channelUserVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, parentUserLoginID);
		   if(channelUserVO!=null) {
			   CategoryVO categoryVO = 	categoryDAO.loadCategoryDetailsByCategoryCode(con, parentcategoryCode);
			   if(!categoryVO.getCategoryCode().equalsIgnoreCase(channelUserVO.getCategoryCode())) {
		     		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PARENT_USER_SHOULD_BE_CAT,new String[] {categoryVO.getCategoryName()});   		 
		     	  }
			   userid= channelUserVO.getUserID();
		   }else {
			   throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_EXIST);
		   }
	   }catch(SQLException se) {
		   	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_EXIST);
	   }
	   return userid;
   	}


  private String ownerUserdataValidation(Connection con,String owneruserLoginID) throws  BTSLBaseException{
	  final String methodName ="ownerUserdataValidation";
	   userDAO = new UserDAO();
	   ChannelUserVO channelUserVO =null;
	   String ownerUserId =null;
	  if(BTSLUtil.isNullString(owneruserLoginID)) {
		  throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_USER_OWNER_MANDATORY);
	  }
	   try {
		   channelUserVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, owneruserLoginID);
		   if(channelUserVO!=null) {
			   ownerUserId= channelUserVO.getUserID();
		   }else {
			   throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OWNER_USER_DOES_NOT_EXIST);
		   }
	   }catch(SQLException se) {
		   	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OWNER_USER_DOES_NOT_EXIST);
	   }
	   return ownerUserId;
   	}

  
}


class RetValue {
	private String ownerUser;
	private String parentUser;
	private String channelUser;
	public String getOwnerUser() {
		return ownerUser;
	}
	public void setOwnerUser(String ownerUser) {
		this.ownerUser = ownerUser;
	}
	public String getParentUser() {
		return parentUser;
	}
	public void setParentUser(String parentUser) {
		this.parentUser = parentUser;
	}
	public String getChannelUser() {
		return channelUser;
	}
	public void setChannelUser(String channelUser) {
		this.channelUser = channelUser;
	}
	
}