package com.restapi.channeluser.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.ProfileThresholdResponseVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.user.businesslogic.UserWebDAO;

@Service
public class UserThresholdServiceImpl implements UserThresholdServiceI {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	
	@Override
	public PretupsResponse<ProfileThresholdResponseVO> userThresholdProcess(String identifierType,
			 String loggedInUser, Connection con, String statusUsed, String status, PretupsResponse<ProfileThresholdResponseVO> response, HttpServletResponse responseSwag) {
		
		final String methodName = "userThresholdProcess";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		ChannelUserVO channelUserSessionVO = null;
		try {
			channelUserSessionVO = returnChannelUser(loggedInUser,response, con, statusUsed, status);
		
			if(BTSLUtil.isNullString(identifierType) == false)
				return getThresholdResponseByLoginID(identifierType, channelUserSessionVO, con, statusUsed, status, response,responseSwag);
	
			else
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "No valid input type(login id) is provided");
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
				return response;
			}
	}
	catch (Exception e) {
		response.setStatus(false);
	 	response.setStatusCode(PretupsI.RESPONSE_FAIL);
		response.setMessageCode(e.getMessage());
		response.setMessageKey(e.getMessage());
		responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
		return response;
	}
    finally {
        if (_log.isDebugEnabled()) {
           _log.debug(methodName, "Exiting");
        	}
    	}	
	}
	
	private ChannelUserVO returnChannelUser(String identifierType, PretupsResponse<?> response, Connection con, String statusUsed, String status) throws BTSLBaseException
	{	
		ChannelUserVO channelUserSessionVO = new ChannelUserVO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		
		channelUserSessionVO = channelUserDAO.loadUsersDetailsByLoginId(con, identifierType, null, statusUsed, status);    
        return channelUserSessionVO;
	}
	
	private PretupsResponse<ProfileThresholdResponseVO> getThresholdResponseByLoginID(String loginId,
			ChannelUserVO channelUserSessionVO, Connection con, String statusUsed, String status,
			PretupsResponse<ProfileThresholdResponseVO> response, HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {
		
		final String methodName = "getThresholdResponseByLoginID";
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Entered");
        }
        
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        ChannelUserVO channelUserVO = null;      

        /*
         * If operator user pass userId = null
         * but in case of channel user pass userId = session user Id
         * 
         * In case of channel user we need to perform a Connect By Prior
         * becs load only the child user
         */
        if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getCategoryVO().getDomainCodeforCategory())) {
            channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, loginId, null, statusUsed, status);
        } else {
                String userID = channelUserSessionVO.getUserID();
                if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserSessionVO.getCategoryVO().getCategoryType())) {
                    userID = channelUserSessionVO.getParentID();
                }
                channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, loginId, userID, statusUsed, status);
        }

        if (channelUserVO != null) {
            if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
                // throw exception no user exist with this Login Id
				return getThresholdResponseStaff(channelUserVO, channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().
						getGrphDomainType(), con, response,responseSwag);

			}
//                if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode())) {
//                    // check the user in the same domain or not
//                    _log.error(methodName, "Error: User are at the same level");
//                    response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectchanneluserforview.error.userloginidatsamelevel");
//                    return response;
//                }
            
         return getThresholdResponse(channelUserVO, channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().
        		 getGrphDomainType(), con, response,responseSwag);
            	
        }
        else 
        {
        // throw exception no user exist with this Login Id
        	_log.error(methodName, "Error: User not exist");
        	response.setResponse(PretupsI.RESPONSE_FAIL, false, "No user exists with this login ID("+loginId+")");
        	responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
        	return response;
        }    
	}
	
	private PretupsResponse<ProfileThresholdResponseVO> getThresholdResponse(ChannelUserVO channelUserVO, String sessionUserID, String sessionUserDomainType,
			Connection con, PretupsResponse<ProfileThresholdResponseVO> response, HttpServletResponse responseSwag) 
			throws BTSLBaseException {
		
		ProfileThresholdResponseVO dataObject = new ProfileThresholdResponseVO();
		final String methodName =  "getThresholdResponse";
		UserWebDAO userwebDAO = new UserWebDAO();
		final boolean isDomainFlag;
		
		response.setDataObject(dataObject);
          
          if(channelUserVO!= null)
		{
			if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
				// throw exception no user exist with this Login Id
				_log.error(methodName, "Error: User not exist");
				response.setResponse(PretupsI.RESPONSE_FAIL, false,
						"No user exists with this login ID");
				return response;
			}
			
			isDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(),
					channelUserVO.getCategoryVO().getGrphDomainType(), sessionUserID, sessionUserDomainType);
			
			if (isDomainFlag == true) {
				this.loadUserCounters(response, channelUserVO, con,responseSwag);
			}
			else 
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false,"No user exists with this login ID");
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
				return response;
			}
		}
          else 
          {
          	response.setResponse(PretupsI.RESPONSE_FAIL, false, "No user exists with this login ID");
          	responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
          	return response;
          }
          
		return response;
	}
	
	private void loadUserCounters(PretupsResponse<ProfileThresholdResponseVO> response, UserVO p_userVO, Connection p_con, HttpServletResponse responseSwag) 
	{		
		
        final String methodName = "loadUserCounters";
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "Entered");
        }
        
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
        final TransferProfileDAO transferProfileDAO = new TransferProfileDAO();
        try {
        	
            // the method below is used to load the balance of user
			ArrayList<UserBalancesVO> userBalanceList = new ArrayList<UserBalancesVO>();
			if (PretupsI.STAFF_USER_TYPE.equals(p_userVO.getUserType())) {
				userBalanceList = channelUserDAO.loadUserBalances(p_con, p_userVO.getNetworkID(), p_userVO.getNetworkID(), p_userVO.getParentID());
			}
			else {
				userBalanceList = channelUserDAO.loadUserBalances(p_con, p_userVO.getNetworkID(), p_userVO.getNetworkID(), p_userVO.getUserID());
			}
			// the method below is used to load the current counters of the user
			UserTransferCountsVO userTransferCountsVO = new UserTransferCountsVO();
			if (PretupsI.STAFF_USER_TYPE.equals(p_userVO.getUserType())) {
				userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(p_con, p_userVO.getParentID(), false);
			}
			else {
				userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(p_con, p_userVO.getUserID(), false);
			}

			if (userTransferCountsVO == null) {
                userTransferCountsVO = new UserTransferCountsVO();
            }
            
            final Date p_CurrentDate = new Date(System.currentTimeMillis());
            ChannelTransferBL.checkResetCountersAfterPeriodChange(userTransferCountsVO, p_CurrentDate);
            response.getDataObject().setUserTransferCountsVO(userTransferCountsVO);

            // load the profile details of the user
            final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUser(p_con, p_userVO.getUserID());
           
            if (BTSLUtil.isNullString(channelUserVO.getTransferProfileID())) {
            	response.setResponse(PretupsI.RESPONSE_FAIL, false, "No profile associated with this user");
            	responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
            	return ;
            }
            // load the profile counters of the user
            final TransferProfileVO transferProfileVO = transferProfileDAO.loadTransferProfile(p_con, channelUserVO.getTransferProfileID(), p_userVO.getNetworkID(), true);
            if (transferProfileVO != null) {
       
                transferProfileVO.setStatus((BTSLUtil.getOptionDesc(transferProfileVO.getStatus(), LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true)).getLabel()));
                //response.getDataObject().setTransferProfileVO(transferProfileVO);
                // map the balance with product
                if (userBalanceList != null && userBalanceList.size() > 0) {
                    for (int index1 = 0; index1 < transferProfileVO.getProfileProductList().size(); index1++) {
                        for (int index = 0; index < userBalanceList.size(); index++) {
                            if (((UserBalancesVO) userBalanceList.get(index)).getProductCode().equals(
                                            ((TransferProfileProductVO) transferProfileVO.getProfileProductList().get(index1)).getProductCode())) {
                                ((TransferProfileProductVO) transferProfileVO.getProfileProductList().get(index1)).setCurrentBalance(PretupsBL
                                                .getDisplayAmount(((UserBalancesVO) userBalanceList.get(index)).getBalance()));
                                break;
                            } else {
                                ((TransferProfileProductVO) transferProfileVO.getProfileProductList().get(index1)).setCurrentBalance("0");
                            }
                        }
                    }
                } else {
                    for (int index1 = 0; index1 < transferProfileVO.getProfileProductList().size(); index1++) {
                        ((TransferProfileProductVO) transferProfileVO.getProfileProductList().get(index1)).setCurrentBalance("0");
                    }
                }
                response.getDataObject().setTransferProfileVO(transferProfileVO);
            } else {
            	response.setResponse(PretupsI.RESPONSE_FAIL, false, "Transfer profile is suspended");
            	responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
            	return ;
            }
            // SubscriberOutCountFlag keep tracks of either subscriber out count
            // is allowed or not
            final boolean subscriberOutcount = ((Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.SUBSCRIBER_TRANSFER_OUTCOUNT)).booleanValue();
            
            response.getDataObject().setSubscriberOutCountFlag(subscriberOutcount);
            response.getDataObject().setUserVO(p_userVO);
            
            if (PretupsI.YES.equals(p_userVO.getCategoryVO().getUnctrlTransferAllowed())) {
            	response.getDataObject().setUnctrlTransferFlag(true);
            } else {
            	response.getDataObject().setUnctrlTransferFlag(false);
            }
            response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "");
        }
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            response.setResponse(PretupsI.RESPONSE_FAIL, false, "");
            responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
            return ;
        }

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
    
	}

	private PretupsResponse<ProfileThresholdResponseVO> getThresholdResponseStaff(ChannelUserVO channelUserVO, String sessionUserID, String sessionUserDomainType,
																				  Connection con, PretupsResponse<ProfileThresholdResponseVO> response, HttpServletResponse responseSwag)
			throws BTSLBaseException {

		ProfileThresholdResponseVO dataObject = new ProfileThresholdResponseVO();
		final String methodName =  "getThresholdResponse";
		UserWebDAO userwebDAO = new UserWebDAO();
		final boolean isDomainFlag;

		response.setDataObject(dataObject);

		if(channelUserVO!= null)
		{

			isDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(),
					channelUserVO.getCategoryVO().getGrphDomainType(), sessionUserID, sessionUserDomainType);

			if (isDomainFlag == true) {
				this.loadUserCounters(response, channelUserVO, con,responseSwag);
			}
			else
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false,"No user exists with this login ID");
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
				return response;
			}
		}
		else
		{
			response.setResponse(PretupsI.RESPONSE_FAIL, false, "No user exists with this login ID");
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			return response;
		}

		return response;
	}


}
