package com.restapi.user.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;
@Service("SuspendResumeServiceI")
public class SuspendResumeServiceImpl implements SuspendResumeServiceI {

	
	public static final Log log = LogFactory.getLog(SuspendResumeRestController.class.getName());
	@Override
	public SuspendResumeResponse processRequestStaff(SuspendResumeUserVo requestVO, HttpServletRequest httprequest,
			MultiValueMap<String, String> headers, HttpServletResponse responseSwag) throws BTSLBaseException {
		final String METHOD_NAME = "processRequest";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
		Connection con = null;
		MComConnectionI mcomCon = null;
		SuspendResumeResponse response = new SuspendResumeResponse();
        String staffUserId = null;
        String staffMsisdn = null;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
		Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		 try {
	        	mcomCon = new MComConnection();
	        	ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
	        	con=mcomCon.getConnection();
	        	final UserWebDAO userwebDAO = new UserWebDAO();
	            UserVO userVO = new UserVO();
	            final UserDAO userDAO = new UserDAO();
	            final Date currentDate = new Date();
	 	        OAuthenticationUtil.validateTokenApi(requestVO, headers,responseSwag);
		        userVO = userDAO.loadUsersDetails(con,requestVO.getData().getMsisdn());
                mcomCon = new MComConnection();
                con=mcomCon.getConnection();
                final String statustoBeSuspend = "'" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','" + PretupsI.USER_STATUS_CANCELED + "','" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_DELETE_REQUEST + "'";
                final String statusToResume = "'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_CANCELED + "','" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_DELETE_REQUEST + "'";
                String userLoginId = null;
                if (BTSLUtil.isNullString(requestVO.getSuspendResumeUserDetailsData().getLoginid())) {
                    userLoginId = "%%%"; // for selecting all the child users
                } else {
                    userLoginId = requestVO.getSuspendResumeUserDetailsData().getLoginid();
                }
                if (!BTSLUtil.isNullString(requestVO.getSuspendResumeUserDetailsData().getLoginid())) {
                    // for suspending active user
                    if ("suspend".equals(requestVO.getSuspendResumeUserDetailsData().getReqType())) {
                        if ("OPERATOR".equals(userVO.getUserType())) {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginID(con, userLoginId, null, statustoBeSuspend);
                        } else {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginIDforSuspend(con, userLoginId, userVO.getUserID(), statustoBeSuspend);
                            // for resuming suspended user
                        }
                    } else if ("OPERATOR".equals(userVO.getUserType())) {
                        staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginID(con, userLoginId, null, statusToResume);
                    } else {
                        staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginIDforSuspend(con, userLoginId, userVO.getUserID(), statusToResume);
                    }


                    if (BTSLUtil.isNullString(staffUserId)) {
                        if ("suspend".equals(requestVO.getSuspendResumeUserDetailsData().getReqType())) {
                            if ("OPERATOR".equals(userVO.getUserType())) {
                                staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginID(con, userLoginId, null, statusToResume);
                            } else {
                                staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginIDforSuspend(con, userLoginId, userVO.getUserID(), statusToResume);
                            }
                            if (BTSLUtil.isNullString(staffUserId)) {
                            	
        	                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NO_STAFF_LOGINID,new String[] { requestVO.getSuspendResumeUserDetailsData().getLoginid() });
                            } else {
        	                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.STAFF_LOGINID_ALREADY_SUSPENDED,new String[] { requestVO.getSuspendResumeUserDetailsData().getLoginid() });

                            }
                        } else {
                            if ("OPERATOR".equals(userVO.getUserType())) {
                                staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginID(con, userLoginId, null, statustoBeSuspend);
                            } else {
                                staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginIDforSuspend(con, userLoginId, userVO.getUserID(), statustoBeSuspend);
                            }
                            if (BTSLUtil.isNullString(staffUserId)) {
        	                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NO_STAFF_LOGINID,new String[] { requestVO.getSuspendResumeUserDetailsData().getLoginid() });
                            } else {
        	                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.STAFF_LOGINID_ALREADY_ACTIVE,new String[] { requestVO.getSuspendResumeUserDetailsData().getLoginid() });

                            }
                        }

                    }
                }

                if (!BTSLUtil.isNullString(requestVO.getSuspendResumeUserDetailsData().getMsisdn())) {
                    staffMsisdn = requestVO.getSuspendResumeUserDetailsData().getMsisdn().trim();
                  
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(staffMsisdn);
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                    if (networkPrefixVO == null) {
                    	
	                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_SUPPORTED,new String[] { requestVO.getSuspendResumeUserDetailsData().getMsisdn() });

                    }

                    // for suspending active user
                    if ("suspend".equals(requestVO.getSuspendResumeUserDetailsData().getReqType())) {
                        if ("OPERATOR".equals(userVO.getUserType())) {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetails(con, staffMsisdn, null, statustoBeSuspend);
                        } else {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetailsForSuspend(con, staffMsisdn, userVO.getUserID(), statustoBeSuspend);
                            // for resuming suspended user
                        }
                    } else if ("OPERATOR".equals(userVO.getUserType())) {
                        staffUserId = channelUserWebDAO.loadStaffUsersDetails(con, staffMsisdn, null, statusToResume);
                    } else {
                        staffUserId = channelUserWebDAO.loadStaffUsersDetailsForSuspend(con, staffMsisdn, userVO.getUserID(), statusToResume);
                    }

                    if (BTSLUtil.isNullString(staffUserId)) {
                        if ("suspend".equals(requestVO.getSuspendResumeUserDetailsData().getReqType())) {
                            if ("OPERATOR".equals(userVO.getUserType())) {
                                staffUserId = channelUserWebDAO.loadStaffUsersDetails(con, staffMsisdn, null, statusToResume);
                            } else {
                                staffUserId = channelUserWebDAO.loadStaffUsersDetailsForSuspend(con, staffMsisdn, userVO.getUserID(), statusToResume);
                            }

                            if (BTSLUtil.isNullString(staffUserId)) {
                               
		                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NO_STAFF_EIXST,new String[] { requestVO.getSuspendResumeUserDetailsData().getMsisdn() });

                            } else {
		                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.STAFF_ALREADY_SUS,new String[] { requestVO.getSuspendResumeUserDetailsData().getMsisdn() });

                            }
                        } else {
                            if ("OPERATOR".equals(userVO.getUserType())) {
                                staffUserId = channelUserWebDAO.loadStaffUsersDetails(con, staffMsisdn, null, statustoBeSuspend);
                            } else {
                                staffUserId = channelUserWebDAO.loadStaffUsersDetailsForSuspend(con, staffMsisdn, userVO.getUserID(), statustoBeSuspend);
                            }
                            if (BTSLUtil.isNullString(staffUserId)) {
    		                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NO_STAFF_MSISDN,new String[] { requestVO.getSuspendResumeUserDetailsData().getMsisdn() });

                            } else {
    		                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.STAFF_ALREADY_EXIST,new String[] { requestVO.getSuspendResumeUserDetailsData().getMsisdn() });

                            }
                        }

                    }
                }
               

                ChannelUserVO staffDetails = new ChannelUserVO();
                final UserDAO userDao = new UserDAO();
                staffDetails = userDao.loadUserDetailsFormUserID(con, staffUserId);
                final UserVO userVOtemp = new UserVO();
                int deleteCount = 0;
                if ("suspend".equals(requestVO.getSuspendResumeUserDetailsData().getReqType())) {
                	userVOtemp.setStatus(PretupsI.USER_STATUS_SUSPEND);
                } else {
                	userVOtemp.setStatus(PretupsI.USER_STATUS_ACTIVE); 
                }
                userVOtemp.setUserID(staffDetails.getUserID()); // who is to be
                // suspended
                userVOtemp.setModifiedBy(userVO.getUserID());
                userVOtemp.setModifiedOn(currentDate);
                userVOtemp.setPreviousStatus(staffDetails.getStatus()); 

                deleteCount = userwebDAO.suspendResumeStaffUser(con, userVOtemp);

                if (deleteCount <= 0) {
                	mcomCon.finalRollback();
                    log.error(METHOD_NAME, "Error: while Suspending User");
                    throw new BTSLBaseException(this, "save", "error.general.processing");
                }
                mcomCon.finalCommit();
                final String arr[] = { staffDetails.getUserName() };
                
                String msg = "";
                if (PretupsI.USER_STATUS_SUSPEND.equals(userVOtemp.getStatus())) {
                   msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_SUCCESS_SUSPEND, arr);
                   response.setMessage(msg);
                   response.setMessageCode(PretupsErrorCodesI.USER_SUCCESS_SUSPEND);
                   response.setStatus(200);
                } else if (PretupsI.USER_STATUS_ACTIVE.equals(userVOtemp.getStatus())) { 
                	msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_RESUMED, arr);
                	response.setMessage(msg);
                	response.setMessageCode(PretupsErrorCodesI.USER_RESUMED);
                	response.setStatus(200);
                }
                
	    		
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
	        	
	    	}finally {
				if (mcomCon != null) {
					mcomCon.close("RestrictedTopUpAction#processFile");
					mcomCon = null;
				}
	                log.debug("processFile", "Exit");
	        }
			return response;
	}
	
	
	
	  private boolean isExistDomain(ArrayList p_domainList, ChannelUserVO p_channelUserVO) throws Exception {
	        final String methodName = "isExistDomain";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered p_domainList.size()=" + p_domainList.size() + ", p_channelUserVO=" + p_channelUserVO);
	        }
	        if (p_domainList == null || p_domainList.isEmpty()) {
	            return true;
	        }
	        boolean isDomainExist = false;
	        try {
	            ListValueVO listValueVO = null;
	            for (int i = 0, j = p_domainList.size(); i < j; i++) {
	                listValueVO = (ListValueVO) p_domainList.get(i);
	                if (listValueVO.getValue().equals(p_channelUserVO.getCategoryVO().getDomainCodeforCategory())) {
	                    isDomainExist = true;
	                    break;
	                }
	            }
	        } catch (Exception e) {
	            log.errorTrace(methodName, e);
	            throw new BTSLBaseException(e);
	        }
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Exiting isDomainExist=" + isDomainExist);
	        }
	        return isDomainExist;
	    }



	@Override
	public SuspendResumeResponse processRequest(SuspendResumeUserVo requestVO, HttpServletRequest httprequest,
			MultiValueMap<String, String> headers, HttpServletResponse responseSwag) throws BTSLBaseException {

		final String METHOD_NAME = "processRequest";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
		Connection con = null;
		MComConnectionI mcomCon = null;
		SuspendResumeResponse response = new SuspendResumeResponse();
		Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		 try {
	        	mcomCon = new MComConnection();
	        	con=mcomCon.getConnection();
	        	final UserWebDAO userwebDAO = new UserWebDAO();
	            UserVO sessionUserVO = null;
	            final UserVO userVO = new UserVO();
	            final UserDAO userDAO = new UserDAO();
	            final Date currentDate = new Date();
	            ArrayList<UserEventRemarksVO> deleteSuspendRemarkList = null;
	            UserEventRemarksVO userRemarksVO = null;
	        	String userpin = requestVO.getData().getPin();		 
	 	        OAuthenticationUtil.validateTokenApi(requestVO, headers,responseSwag);
	 	        ChannelUserVO channelUserVO=new ChannelUserVO();
				ChannelTransferVO channelTransferVO =new ChannelTransferVO();
		        ChannelUserDAO channelUserDAO = null;
		        String filteredMsisdn = null;
		        UserVO senderVO = null;
		       
		            String status = "'" + PretupsI.USER_STATUS_ACTIVE + "'" + "," + "'" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
		            String statusUsed = PretupsI.STATUS_IN;
		            channelUserDAO = new ChannelUserDAO();
		            
		            senderVO = userDAO.loadUsersDetails(con,requestVO.getData().getMsisdn());
		            
		            String action ="";
		            
		            if(BTSLUtil.isNullString(requestVO.getSuspendResumeUserDetailsData().getRemarks())) {
	                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.O2C_REMARKS_REQUIRED);
		            }
		            
		            if("suspend".equalsIgnoreCase(requestVO.getSuspendResumeUserDetailsData().getReqType())) {
		            	  if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_SUSPENSION_APPROVAL)).booleanValue()) {
		                     action = PretupsI.USER_STATUS_SUSPEND_REQUEST;
		                  } else {
		                	  action =  PretupsI.USER_STATUS_SUSPEND;
		                  }
		            	
		            }else if("resume".equalsIgnoreCase(requestVO.getSuspendResumeUserDetailsData().getReqType())) {
		            	action = PretupsI.USER_STATUS_ACTIVE;
		            }else {
	                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ,new String[] {"requestType"});
		            }
		            if (!BTSLUtil.isNullString(requestVO.getSuspendResumeUserDetailsData().getMsisdn())) {
		                // filtering the msisdn for country independent dial format
		                filteredMsisdn = PretupsBL.getFilteredMSISDN(requestVO.getSuspendResumeUserDetailsData().getMsisdn());
		                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
		                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
		                }
		                // load user details by msisdn
		                channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMsisdn, null, statusUsed, status);
		                if (channelUserVO == null) {
		                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
		                }
		                if (!BTSLUtil.isNullString(requestVO.getSuspendResumeUserDetailsData().getLoginid()) && !channelUserVO.getLoginID().equalsIgnoreCase(requestVO.getSuspendResumeUserDetailsData().getLoginid())) {
		                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
		                }
		            } else if (!BTSLUtil.isNullString(requestVO.getSuspendResumeUserDetailsData().getLoginid())) {
		                // load user details by login id
		                channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, requestVO.getSuspendResumeUserDetailsData().getLoginid(), null, statusUsed, status);
		                if (channelUserVO == null) {
		                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4LOGINID);
		                }
		                filteredMsisdn = PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn());

		            } else {
		                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
		            }

		            
		            
		    if (senderVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode()) && !PretupsI.USER_TYPE_STAFF
                            .equals(channelUserVO.getUserType())) {
                // check the user in the same domain or not
                final String arr2[] = { channelUserVO.getLoginID() };
                log.error(METHOD_NAME, "Error: User are at the same level");
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SUSPEND_LOGINID_SAME_LEVEL,arr2);
            }
		    
		    if (PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())) {
              ArrayList domainList =  BTSLUtil.displayDomainList(senderVO.getDomainList());
              if (domainList != null) {
                  final boolean isDomainFlag = this.isExistDomain(domainList, channelUserVO);
                  final ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserVO.getCategoryVO().getDomainCodeforCategory(),domainList);
                 
                  if (!isDomainFlag) {
                      // check the user in the same domain or not
                      final String arr2[] = { channelUserVO.getLoginID() };
                      log.error(METHOD_NAME, "Error: User not in the same domain");
                      
                      throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SUSPEND_LOGINID_DOMAIN_DIFF,arr2);
                  }
              }
            }
		    
		    
		    final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
		    		senderVO.getUserID(), senderVO.getCategoryVO().getGrphDomainType());
					 if (!isGeoDomainFlag) {
					        // check the user in the same domain or not
					        final String arr2[] = { channelUserVO.getMsisdn() };
					        log.error(METHOD_NAME, "Error: User not in the same Geo");
					        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SUSPEND_LOGINID_GEO_DIFF,arr2);
					    }
		            
		            String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
		            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		            if (networkPrefixVO == null) {
		                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, new String[] { filteredMsisdn });
		            }
		            String networkCode = networkPrefixVO.getNetworkCode();
		            if (networkCode != null && !networkCode.equalsIgnoreCase(channelUserVO.getNetworkID())) {
		                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
		            }
		            
		            if (channelUserVO.getStatus().equalsIgnoreCase(action)) {
		                if (PretupsI.USER_STATUS_ACTIVE.equalsIgnoreCase(action)) {
		                    throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.CCE_ERROR_USER_ALREADY_ACTIVE);
		                } else if (PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(action)) {
		                    throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.CCE_ERROR_USER_ALREADY_SUSPENDED);
		                }
		                if (action.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
			                throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.CCE_USER_SUSPENDED_APPROVAL_PENDING);
			            }
		            }
		            	if(channelUserVO.getStatus().equals("S")) {
		            		
		            		if( action.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST) || action.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)){
				                throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.CCE_ERROR_USER_ALREADY_SUSPENDED);
		            		}
		            	}

		            if(requestVO.getSuspendResumeUserDetailsData().getReqType().equals("resume")) {
		            	if(!channelUserVO.getStatus().equalsIgnoreCase("S")) {
		            		  throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.CANNOT_RESUME);
		            	}
		            }
		           
		            channelUserVO.setPreviousStatus(channelUserVO.getStatus());

                int deleteCount = 0;
                userVO.setStatus(action);
                userVO.setUserID(channelUserVO.getUserID());
                userVO.setLastModified(channelUserVO.getLastModified());
                senderVO.setActiveUserID(senderVO.getUserID());
                userVO.setModifiedBy(senderVO.getActiveUserID());
                userVO.setModifiedOn(currentDate);
                /*
                 * set the old status value into the previous status
                 */
                userVO.setPreviousStatus(channelUserVO.getStatus());
                final ArrayList list = new ArrayList();
                list.add(userVO);
                deleteCount = userDAO.deleteSuspendUser(con, list);

                if (deleteCount <= 0) {
                    con.rollback();
                    log.error(METHOD_NAME, "Error: while Suspending User");
                    throw new BTSLBaseException(this, "save", PretupsErrorCodesI.CCE_XML_ERROR_CHANNEL_USER_NOTUPDATE);
                }
              
                if(SystemPreferences.USER_EVENT_REMARKS)
                {
                    if (deleteCount > 0) {
                        int suspendRemarkCount = 0;
                        deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
                        userRemarksVO = new UserEventRemarksVO();
                        userRemarksVO.setCreatedBy(senderVO.getCreatedBy());
                        userRemarksVO.setCreatedOn(currentDate);
                    
                        userRemarksVO.setEventType(PretupsI.SUSPEND_REQUEST_EVENT);
                        userRemarksVO.setMsisdn(channelUserVO.getMsisdn());
                        userRemarksVO.setRemarks(requestVO.getSuspendResumeUserDetailsData().getRemarks());
                        userRemarksVO.setUserID(channelUserVO.getUserID());
                        userRemarksVO.setUserType(channelUserVO.getUserType());
                        userRemarksVO.setModule("C2S");
                        deleteSuspendRemarkList.add(userRemarksVO);
                        suspendRemarkCount = userwebDAO.insertEventRemark(con, deleteSuspendRemarkList);
                        if (suspendRemarkCount <= 0) {
                            con.rollback();
                            log.error(METHOD_NAME, "Error: while inserting into userEventRemarks Table");
                            throw new BTSLBaseException(this, "save", PretupsErrorCodesI.CCE_XML_ERROR_CHANNEL_USER_NOTUPDATE);
                        }
                    }
                }
                con.commit();
                
                final String arr[] = { channelUserVO.getUserName() };
                String msg = "";
                if (PretupsI.USER_STATUS_SUSPEND.equals(userVO.getStatus())) {
                   msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_SUCCESS_SUSPEND, arr);
                   response.setMessage(msg);
                   response.setMessageCode(PretupsErrorCodesI.USER_SUCCESS_SUSPEND);
                   response.setStatus(200);
                } else if(PretupsI.USER_STATUS_SUSPEND_REQUEST.equals(userVO.getStatus())) {
                	msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_SUCCESS_APP_SUSPEND, arr);
                	response.setMessage(msg);
                	response.setMessageCode(PretupsErrorCodesI.USER_SUCCESS_APP_SUSPEND);
                	response.setStatus(200);
                }else if(PretupsI.USER_STATUS_ACTIVE.equals(userVO.getStatus())) {
                	msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_SUCCESS_RESUMED, arr);
                	response.setMessage(msg);
                	response.setMessageCode(PretupsErrorCodesI.USER_SUCCESS_RESUMED);
                	response.setStatus(200);
                }
                
               
               
               // forward = super.handleMessage(btslMessage, request, mapping);

                userVO.setLoginID(channelUserVO.getWebLoginID());
                userVO.setUserName(channelUserVO.getUserName());
                userVO.setMsisdn(channelUserVO.getMsisdn());
                if (userVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
                    ChannelUserLog.log("SUSPREQCHNLUSR", channelUserVO, userVO, false, null);
                } else if (userVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND)) {
                    ChannelUserLog.log("SUSPCHNLUSR", channelUserVO, userVO, false, null);
                }
				
	    		
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
	        	
	    	}finally {
				if (mcomCon != null) {
					mcomCon.close("RestrictedTopUpAction#processFile");
					mcomCon = null;
				}
	                log.debug("processFile", "Exit");
	        }
			return response;
	
	}


}
