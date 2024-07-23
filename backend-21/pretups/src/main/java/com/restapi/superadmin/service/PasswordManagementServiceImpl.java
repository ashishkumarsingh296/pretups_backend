package com.restapi.superadmin.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.responseVO.PasswordManagementResponseVO;
import com.restapi.superadmin.serviceI.PasswordManagementServiceI;
import com.restapi.superadminVO.PasswordManagementVO;

@Service("PasswordManagementServiceI")
public class PasswordManagementServiceImpl implements PasswordManagementServiceI{
	
	public static final Log log = LogFactory.getLog(PasswordManagementServiceImpl.class.getName());
	public static final String classname = "PasswordManagementServiceImpl";

	@Override
	public PasswordManagementResponseVO getUserDetails(Connection con, MComConnectionI mcomCon, Locale locale,
			String loginId, String msisdn, String remarks, UserVO userVO, HttpServletResponse responseSwag) {
		
		
		final String methodName =  "getUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		PasswordManagementResponseVO response = new PasswordManagementResponseVO();
		
		PasswordManagementVO passwordManagementVO = new PasswordManagementVO();
        LoginDAO loginDAO = null;
		try {
			
            if (loginId != null) {
				passwordManagementVO.setLoginID(loginId.trim());
            }
            final String loginID = passwordManagementVO.getLoginID();
            
            if (msisdn != null) {
            	passwordManagementVO.setMsisdn(msisdn.trim());
            }
            msisdn = passwordManagementVO.getMsisdn();
            
            if (loginID.equals(userVO.getLoginID())) {
                throw new BTSLBaseException(PretupsErrorCodesI.SAME_LOGIN_ID);
            } 
            else {
                loginDAO = new LoginDAO();
                final ChannelUserVO channelUserVo = loginDAO.loadUserDetailsByMsisdnOrLoginId(con, msisdn, loginID, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)), locale);
                
                if (!PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
                    ChannelUserVO channelUserVOHryCheck = null;
                    final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                    final String userID = userVO.getUserID();
                    final String status = "'" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_CANCELED + "','" + PretupsI.USER_STATUS_DELETE_REQUEST + "'";
                    final String statusUsed = PretupsI.STATUS_NOTIN;

                    if (!BTSLUtil.isNullString(msisdn))
                    {
                        final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(msisdn)));
                        if (prefixVO == null || !prefixVO.getNetworkCode().equals(userVO.getNetworkID())) {
                            final String[] arr1 = { msisdn, userVO.getNetworkName() };
                            throw new BTSLBaseException(PretupsErrorCodesI.MSISDN_NOT_IN_NETWORK, arr1);
                        }
                        final String filteredMSISDN = PretupsBL.getFilteredMSISDN(msisdn);
                        channelUserVOHryCheck = channelUserDAO.loadUsersDetails(con, filteredMSISDN, userID, statusUsed, status);
                        if (channelUserVOHryCheck == null) {
                            final String arr2[] = { msisdn };
                            throw new BTSLBaseException(PretupsErrorCodesI.MSISDN_DOESNT_EXIST, arr2);
                        }
                    } 
                    else if (!BTSLUtil.isNullString(loginID)) 
                    {
                        channelUserVOHryCheck = channelUserDAO.loadUsersDetailsByLoginId(con, loginID, userID, statusUsed, status);
                        if (channelUserVOHryCheck == null) {
                            final String arr2[] = { loginID };
                            throw new BTSLBaseException(PretupsErrorCodesI.NO_USER_EXIST_LOGINID, arr2);
                        }
                    }
                }
                
                passwordManagementVO.setChannelUserVO(channelUserVo); 
                
                if (passwordManagementVO.getChannelUserVO() == null) {
                    final String[] arr = new String[1];
                    arr[0] = passwordManagementVO.getLoginID();
                    if (!BTSLUtil.isNullString(arr[0])) {
                        throw new BTSLBaseException(PretupsErrorCodesI.USER_DETAILS_NOT_FOUND_LOGINID, arr);
                    } else {
                        arr[0] = passwordManagementVO.getMsisdn();
                        throw new BTSLBaseException(PretupsErrorCodesI.USER_DETAILS_NOT_FOUND_MSISDN, arr);
                    }
                }
                
                channelUserVo.setRsaRequired(BTSLUtil.isRsaRequired(channelUserVo));
                passwordManagementVO.setChannelUserVO(channelUserVo);
                
                if (PretupsI.USER_STATUS_NEW.equals(passwordManagementVO.getChannelUserVO().getStatus())) {
                    final String[] arr = new String[1];
                    arr[0] = passwordManagementVO.getLoginID();
                    throw new BTSLBaseException(PretupsErrorCodesI.USER_NOT_YET_APPROVED, arr);
                }
                
                if (!userVO.getNetworkID().equals(passwordManagementVO.getChannelUserVO().getNetworkID())) {
                    if (!BTSLUtil.isNullString(passwordManagementVO.getLoginID())) {
                        throw new BTSLBaseException(PretupsErrorCodesI.LOGINID_NETWORK_NOT_SUPPORTED);
                    } else if(!BTSLUtil.isNullString(passwordManagementVO.getMsisdn())){
                        throw new BTSLBaseException(PretupsErrorCodesI.MSISDN_NETWORK_NOT_SUPPORTED);
                    }
                }
                
                final ChannelUserVO channelUserVO = passwordManagementVO.getChannelUserVO();
                channelUserVO.setRemarks(passwordManagementVO.getRemarks());
                
                if (userVO.getCategoryVO().getGrphDomainSequenceNo() > channelUserVO.getCategoryVO().getGrphDomainSequenceNo()) {
                    throw new BTSLBaseException(PretupsErrorCodesI.NOT_AUTHORIZED);
                } else if (userVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode())) {
                    throw new BTSLBaseException(PretupsErrorCodesI.NOT_AUTHORIZED);
                } else {
                    if (userVO.getCategoryVO().getDomainCodeforCategory().equals(channelUserVO.getCategoryVO().getDomainCodeforCategory())) {
                        if (userVO.getCategoryVO().getGrphDomainType().equals(channelUserVO.getCategoryVO().getGrphDomainType())) {
                            if (userVO.getCategoryVO().getSequenceNumber() >= channelUserVO.getCategoryVO().getSequenceNumber()) {
                                throw new BTSLBaseException(PretupsErrorCodesI.NOT_AUTHORIZED);
                            }
                        }
                    }
                    final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
                    if (!geographicalDomainDAO.isUserExistsInGeoDomainExistHierarchy(con, channelUserVO.getUserID(), userVO.getUserID())) {
                        throw new BTSLBaseException(PretupsErrorCodesI.NOT_AUTHORIZED);
                    }
                }
                
                if (!PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(channelUserVO.getDomainID())) {
                    if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(userVO.getDomainID())) {
                        ArrayList domList = null;
                        domList = userVO.getDomainList();

                        if ((domList == null || domList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
                            .getCategoryVO().getFixedDomains())) {
                            domList = new DomainDAO().loadCategoryDomainList(con);
                        }
                        domList = BTSLUtil.displayDomainList(domList);
                        
                        ListValueVO listValueVO = null;
                        boolean domainfound = false;
                        for (int i = 0, j = domList.size(); i < j; i++) {
                            listValueVO = (ListValueVO) domList.get(i);
                            if (channelUserVO.getDomainID().equals(listValueVO.getValue())) {
                                domainfound = true;
                                break;
                            }
                        }
                        if (!domainfound) {
                            throw new BTSLBaseException(PretupsErrorCodesI.NOT_AUTHORIZED);
                        }
                    } else {
                        if (!channelUserVO.getDomainID().equals(userVO.getDomainID())) {
                            throw new BTSLBaseException(PretupsErrorCodesI.NOT_AUTHORIZED);
                        }
                    }
                }
                
    			if(! (channelUserVO.getCategoryVO().getWebInterfaceAllowed().equalsIgnoreCase(PretupsI.STATUS_ACTIVE))){ 
                    throw new BTSLBaseException(PretupsErrorCodesI.WEB_INTERFACE_NOT_ALLOWED);
    			}
    			
                this.setReponse(response, channelUserVO, remarks);
            }
            

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_DETAILS_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.USER_DETAILS_FOUND);
            
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e){
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.USER_DETAILS_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.USER_DETAILS_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
	}
	
	
	private void setReponse(PasswordManagementResponseVO response, ChannelUserVO channelUserVO, String remarks) {
		response.setLoginID(channelUserVO.getLoginID());
		response.setUserName(channelUserVO.getUserName());
		response.setAddress(channelUserVO.getFullAddress());
		response.setCity(channelUserVO.getCity());
		response.setState(channelUserVO.getState());
		response.setCountry(channelUserVO.getCountry());
		response.setUserID(channelUserVO.getUserID());
		response.setMobileNumber(channelUserVO.getMsisdn());
		response.setInvalidPasswordCount(channelUserVO.getInvalidPasswordCount());
		response.setSsn(channelUserVO.getSsn());
		response.setContactPerson(channelUserVO.getContactPerson());
		response.setNetworkCode(channelUserVO.getNetworkID());
		response.setShortName(channelUserVO.getShortName());
		response.setUserGrade(channelUserVO.getUserGrade());
		response.setParentID(channelUserVO.getParentID());
		response.setOwnerName(channelUserVO.getOwnerName());
		response.setOwnerID(channelUserVO.getOwnerID());
		response.setNetworkName(channelUserVO.getNetworkName());
		response.setCategory(channelUserVO.getCategoryVO().getCategoryName());
		response.setDesignation(channelUserVO.getDesignation());
		response.setRemarks(remarks);
        response.setParentName(channelUserVO.getParentName());
		if (channelUserVO.getInvalidPasswordCount() < ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, channelUserVO.getNetworkID(),
		channelUserVO.getCategoryCode())).intValue()) {
			response.setPasswordStatus(PretupsI.PWD_UNBLOCKED);
		}
		else {
			response.setPasswordStatus(PretupsI.PWD_BLOCKED);
		}
	}
	
}