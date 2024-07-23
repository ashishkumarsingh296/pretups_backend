package com.restapi.channelAdmin.serviceI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.channelAdmin.SuspendResumeStaffVO;
import com.restapi.channelAdmin.requestVO.SuspendResumeStaffRequestVO;
import com.restapi.channelAdmin.responseVO.SuspendResumeStaffResponseVO;
import com.restapi.channelAdmin.service.SuspendResumeStaffService;
import com.restapi.superadmin.service.BatchOperatorUserInitiateServiceImpl;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

@Service ("SuspendResumeStaffService")
public class SuspendResumeStaffServiceImpl implements SuspendResumeStaffService{
	
	public static final Log log = LogFactory.getLog(BatchOperatorUserInitiateServiceImpl.class.getName());

	public SuspendResumeStaffResponseVO suspendResumeStaffUser(Connection con, MComConnectionI mcomCon, Locale locale, String operationType,
			ChannelUserVO sessionUserVO, SuspendResumeStaffRequestVO request, SuspendResumeStaffResponseVO response,
			HttpServletResponse responseSwag) throws SQLException {

		final String methodName = "suspendResumeStaffUser";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
		
        SuspendResumeStaffVO suspendResumeStaffVO = new SuspendResumeStaffVO();
        String staffMsisdn = null;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        String staffUserId = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        UserWebDAO userwebDAO = null;
        
        try {
        	suspendResumeStaffVO.setCategoryCode(sessionUserVO.getCategoryCode());
        	suspendResumeStaffVO.setLoginUserID(sessionUserVO.getUserID());
        	suspendResumeStaffVO.setLoginUserDomainCode(sessionUserVO.getDomainTypeCode());
        	suspendResumeStaffVO.setLoginUserCategoryCode(sessionUserVO.getCategoryCode());
        	
        	if (operationType.equals("S")) {
        		suspendResumeStaffVO.setRequestType("suspend");
        	}
        	else {
        		suspendResumeStaffVO.setRequestType("resume");
        	}

        	final CategoryVO catVO = sessionUserVO.getCategoryVO();
            catVO.setOutletsAllowed("N");
        	
            if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_REQ_MSISDN_FOR_STAFF)).booleanValue()) {
                catVO.setSmsInterfaceAllowed("N");
            }
            
            suspendResumeStaffVO.setCategoryVO(catVO);
            
            final ArrayList<UserGeographiesVO> geoList = sessionUserVO.getGeographicalAreaList();
            UserGeographiesVO userGeoVO = null;
            if (catVO.getMultipleGrphDomains().equals(PretupsI.YES)) {
                String[] geoStr = null;
                if (geoList != null && geoList.size() > 0) {
                    geoStr = new String[geoList.size()];
                    for (int i = 0, j = geoList.size(); i < j; i++) {
                        userGeoVO = (UserGeographiesVO) geoList.get(i);
                        geoStr[i] = userGeoVO.getGraphDomainCode();
                    }
                }
                suspendResumeStaffVO.setGeographicalCodeArray(geoStr);
            } else if (geoList != null && geoList.size() > 0) {
                userGeoVO = (UserGeographiesVO) geoList.get(0);
                suspendResumeStaffVO.setGeographicalCode(userGeoVO.getGraphDomainCode());
            }
            
            channelUserWebDAO = new ChannelUserWebDAO();
            
            final String statustoBeSuspend = "'" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','" + PretupsI.USER_STATUS_CANCELED + "','" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_DELETE_REQUEST + "'";
            final String statusToResume = "'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_CANCELED + "','" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_DELETE_REQUEST + "'";
            
            String userLoginId = null;
            if (BTSLUtil.isNullString(request.getLoginID())) {
                userLoginId = "%%%"; // for selecting all the child users
            } else {
                userLoginId = request.getLoginID();
            }
            if (!BTSLUtil.isNullString(request.getLoginID())) {
                // for suspending active user
                if ("suspend".equals(suspendResumeStaffVO.getRequestType())) {
                    if ("OPERATOR".equals(sessionUserVO.getUserType())) {
                        staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginID(con, userLoginId, null, statustoBeSuspend);
                    } else {
                        staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginIDforSuspend(con, userLoginId, sessionUserVO.getUserID(), statustoBeSuspend);
                        // for resuming suspended user
                    }
                } else if ("OPERATOR".equals(sessionUserVO.getUserType())) {
                    staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginID(con, userLoginId, null, statusToResume);
                } else {
                    staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginIDforSuspend(con, userLoginId, sessionUserVO.getUserID(), statusToResume);
                }

                /*
                 * Search Criteria = L(means user search through Login id)
                 */
                suspendResumeStaffVO.setSearchCriteria("L");

                if (BTSLUtil.isNullString(staffUserId)) {
                    if ("suspend".equals(suspendResumeStaffVO.getRequestType())) {
                        if ("OPERATOR".equals(sessionUserVO.getUserType())) {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginID(con, userLoginId, null, statusToResume);
                        } else {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginIDforSuspend(con, userLoginId, sessionUserVO.getUserID(), statusToResume);
                        }
                        if (BTSLUtil.isNullString(staffUserId)) {
                			String msg = RestAPIStringParser.getMessage(locale, "channeluser.suspendstaffuser.msg.nostaffexist", new String[] { request.getMsisdn() });
                			response.setMessage(msg);
                            throw new BTSLBaseException(this, methodName, "channeluser.suspendstaffuser.msg.loginid.nostaffexist", 0, new String[] { request.getLoginID() }, "search");
                        } else {
                			String msg = RestAPIStringParser.getMessage(locale, "channeluser.suspendstaffuser.msg.msisdn.alreadysuspended", new String[] { request.getMsisdn() });
                			response.setMessage(msg);
                            throw new BTSLBaseException(this, methodName, "channeluser.suspendstaffuser.msg.alreadysuspended", 0,
                                            new String[] { request.getLoginID() }, "search");
                        }
                    } else {
                        if ("OPERATOR".equals(sessionUserVO.getUserType())) {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginID(con, userLoginId, null, statustoBeSuspend);
                        } else {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetailsbyLoginIDforSuspend(con, userLoginId, sessionUserVO.getUserID(), statustoBeSuspend);
                        }
                        if (BTSLUtil.isNullString(staffUserId)) {
                			String msg = RestAPIStringParser.getMessage(locale, "channeluser.suspendstaffuser.msg.nostaffexist", new String[] { request.getMsisdn() });
                			response.setMessage(msg);
                            throw new BTSLBaseException(this, methodName, "channeluser.suspendstaffuser.msg.loginid.nostaffexist", 0, new String[] { request.getLoginID() }, "search");
                        } else {
                			String msg = RestAPIStringParser.getMessage(locale, "channeluser.suspendstaffuser.msg.msisdn.alreadyresumed", new String[] { request.getMsisdn() });
                			response.setMessage(msg);
                            throw new BTSLBaseException(this, methodName, "channeluser.suspendstaffuser.msg.alreadyresumed", 0, new String[] { request.getLoginID() },
                                            "search");
                        }
                    }

                }
            }
            
            if (!BTSLUtil.isNullString(request.getMsisdn())) {
                staffMsisdn = request.getMsisdn().trim();
                /*
                 * Search Criteria = M (means user search through Mobile
                 * Number)
                 */
                suspendResumeStaffVO.setSearchCriteria("M");
                // check for msisdn belongs to same network or not
                msisdnPrefix = PretupsBL.getMSISDNPrefix(staffMsisdn);
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                if (networkPrefixVO == null) {
        			String msg = RestAPIStringParser.getMessage(locale, "c2senquiry.viewc2stransfers.msg.notsupportnetwork", new String[] { request.getMsisdn() });
        			response.setMessage(msg);
                    throw new BTSLBaseException(this, methodName, "c2senquiry.viewc2stransfers.msg.notsupportnetwork", 0, new String[] { request.getMsisdn() }, "search");
                    // for getting id of user from msisdn
                }

                // for suspending active user
                if ("suspend".equals(suspendResumeStaffVO.getRequestType())) {
                    if ("OPERATOR".equals(sessionUserVO.getUserType())) {
                        staffUserId = channelUserWebDAO.loadStaffUsersDetails(con, staffMsisdn, null, statustoBeSuspend);
                    } else {
                        staffUserId = channelUserWebDAO.loadStaffUsersDetailsForSuspend(con, staffMsisdn, sessionUserVO.getUserID(), statustoBeSuspend);
                        // for resuming suspended user
                    }
                } else if ("OPERATOR".equals(sessionUserVO.getUserType())) {
                    staffUserId = channelUserWebDAO.loadStaffUsersDetails(con, staffMsisdn, null, statusToResume);
                } else {
                    staffUserId = channelUserWebDAO.loadStaffUsersDetailsForSuspend(con, staffMsisdn, sessionUserVO.getUserID(), statusToResume);
                }

                if (BTSLUtil.isNullString(staffUserId)) {
                    if ("suspend".equals(suspendResumeStaffVO.getRequestType())) {
                        if ("OPERATOR".equals(sessionUserVO.getUserType())) {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetails(con, staffMsisdn, null, statusToResume);
                        } else {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetailsForSuspend(con, staffMsisdn, sessionUserVO.getUserID(), statusToResume);
                        }

                        if (BTSLUtil.isNullString(staffUserId)) {
                			String msg = RestAPIStringParser.getMessage(locale, "channeluser.suspendstaffuser.msg.nostaffexist", new String[] { request.getMsisdn() });
                			response.setMessage(msg);
                            throw new BTSLBaseException(this, methodName, "channeluser.suspendstaffuser.msg.nostaffexist", 0, new String[] { request.getMsisdn() },
                                            "search");
                        } else {
                			String msg = RestAPIStringParser.getMessage(locale, "channeluser.suspendstaffuser.msg.msisdn.alreadysuspended", new String[] { request.getMsisdn() });
                			response.setMessage(msg);
                            throw new BTSLBaseException(this, methodName, "channeluser.suspendstaffuser.msg.msisdn.alreadysuspended", 0, new String[] { staffMsisdn },
                                            "search");
                        }
                    } else {
                        if ("OPERATOR".equals(sessionUserVO.getUserType())) {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetails(con, staffMsisdn, null, statustoBeSuspend);
                        } else {
                            staffUserId = channelUserWebDAO.loadStaffUsersDetailsForSuspend(con, staffMsisdn, sessionUserVO.getUserID(), statustoBeSuspend);
                        }
                        if (BTSLUtil.isNullString(staffUserId)) {
                			String msg = RestAPIStringParser.getMessage(locale, "channeluser.suspendstaffuser.msg.nostaffexist", new String[] { request.getMsisdn() });
                			response.setMessage(msg);
                            throw new BTSLBaseException(this, methodName, "channeluser.suspendstaffuser.msg.nostaffexist", 0, new String[] { request.getMsisdn() },
                                            null);
                        } else {
                			String msg = RestAPIStringParser.getMessage(locale, "channeluser.suspendstaffuser.msg.msisdn.alreadyresumed", new String[] { request.getMsisdn() });
                			response.setMessage(msg);
                            throw new BTSLBaseException(this, methodName, "channeluser.suspendstaffuser.msg.msisdn.alreadyresumed", 0, new String[] { staffMsisdn },
                                            null);
                        }
                    }

                }
            }
            
            ChannelUserVO staffDetails = new ChannelUserVO();
            final UserDAO userDao = new UserDAO();
            staffDetails = userDao.loadUserDetailsFormUserID(con, staffUserId);

            this.setUserDetails(suspendResumeStaffVO, staffDetails); 
            
            final UserVO userVO = new UserVO();
            final Date currentDate = new Date();
            
            int deleteCount = 0;
            if ("suspend".equals(suspendResumeStaffVO.getRequestType())) {
                userVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
            } else {
                userVO.setStatus(PretupsI.USER_STATUS_ACTIVE); // for
                // resuming
                // the
                // staff
                // user
            }
            
            userVO.setUserID(suspendResumeStaffVO.getUserId()); // who is to be
            // suspended
            userVO.setModifiedBy(sessionUserVO.getActiveUserID());
            userVO.setModifiedOn(currentDate);
            userVO.setPreviousStatus(suspendResumeStaffVO.getStatus()); // set the
            // old status
            // value into
            // the
            // previous
            // status
            userwebDAO = new UserWebDAO();
            deleteCount = userwebDAO.suspendResumeStaffUser(con, userVO);
            
            if (deleteCount <= 0) {
            	mcomCon.finalRollback();
                log.error(methodName, "Error: while Suspending User");
                throw new BTSLBaseException(this, "save", "error.general.processing");
            }
            mcomCon.finalCommit();
            final String arr[] = { suspendResumeStaffVO.getChannelUserName() };
            if (PretupsI.USER_STATUS_SUSPEND.equals(userVO.getStatus())) {
                String resmsg = RestAPIStringParser.getMessage(locale, "user.deletesuspendchanneluser.suspendsuccessmessage", arr);
                response.setStatus(200);
                response.setMessage(resmsg);
                response.setMessageCode("user.deletesuspendchanneluser.suspendsuccessmessage");
                responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
                
            } else if (PretupsI.USER_STATUS_ACTIVE.equals(userVO.getStatus())) {
                String resmsg = RestAPIStringParser.getMessage(locale, "user.deletesuspendchanneluser.resumesuccessmessage", arr);
                response.setStatus(200);
                response.setMessage(resmsg);
                response.setMessageCode("user.deletesuspendchanneluser.resumesuccessmessage");
                responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            }
            
            userVO.setLoginID(suspendResumeStaffVO.getWebLoginID());
            userVO.setUserName(suspendResumeStaffVO.getChannelUserName());
            userVO.setMsisdn(suspendResumeStaffVO.getMsisdn());
            if (userVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
                ChannelUserLog.log("SUSPREQCHNLUSR", userVO, sessionUserVO, false, null);
            } else if (userVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND)) {
                ChannelUserLog.log("SUSPCHNLUSR", userVO, sessionUserVO, false, null);
            }
        } catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			response.setMessageCode(be.getMessageKey());
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
        } finally{
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}
        }
		
		
		return response;
		
	}
	
    private void setUserDetails(SuspendResumeStaffVO suspendResumeStaffVO, ChannelUserVO staffDetails) {
    	suspendResumeStaffVO.setChannelCategoryDesc(staffDetails.getCategoryVO().getCategoryName());
    	suspendResumeStaffVO.setChannelUserName(staffDetails.getUserName());
    	suspendResumeStaffVO.setMsisdn(staffDetails.getMsisdn());
    	suspendResumeStaffVO.setOwnerName(staffDetails.getOwnerName());
    	suspendResumeStaffVO.setParentName(staffDetails.getParentName());
    	suspendResumeStaffVO.setWebLoginID(staffDetails.getLoginID());
    	suspendResumeStaffVO.setUserType(staffDetails.getUserType());
    	suspendResumeStaffVO.setEmail(staffDetails.getEmail());
    	suspendResumeStaffVO.setAddress1(staffDetails.getAddress1());
    	suspendResumeStaffVO.setAddress2(staffDetails.getAddress2());
    	suspendResumeStaffVO.setUserId(staffDetails.getUserID());
    	suspendResumeStaffVO.setStatus(staffDetails.getStatus());
    	suspendResumeStaffVO.setLastModified(staffDetails.getLastModified());
    }

}