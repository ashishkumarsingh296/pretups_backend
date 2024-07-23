package com.restapi.superadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
/*//import org.apache.struts.action.ActionForm;*/
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.channelAdmin.ChannelUserListResponseVO;
import com.restapi.superadmin.responseVO.OperatorUserListResponse;
import com.restapi.superadmin.serviceI.ViewUserServiceI;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserForm;


@Service("viewUserService")
public class ViewUserServiceImpl implements ViewUserServiceI {
	public static final Log LOG = LogFactory.getLog(ViewUserServiceImpl.class.getName());
	public static final String classname = "ViewUserServiceImpl";

	@Override
	public OperatorUserListResponse viewOperatorUser(Connection con,String loginID,String category,
	HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {
		
		final String METHOD_NAME = "viewOperatorUser";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		OperatorUserListResponse response = new OperatorUserListResponse();
		UserDAO userDAO = new UserDAO();
		UserVO user = null;
		UserVO loggedinUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
		CategoryDAO categoryDAO = new CategoryDAO();
		boolean nwAdminCrossAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.NWADM_CROSS_ALLOW);
		MComConnectionI mcomCon = null;
		
		try {
            String userName = "%";
			UserWebDAO userwebDAO = new UserWebDAO();
			
			List <CategoryVO>categoryList = new ArrayList<CategoryVO>();
			categoryList=(ArrayList<CategoryVO>) categoryDAO.loadCategoryList(con, PretupsI.OPERATOR_TYPE_OPT, loggedinUserVO
					.getCategoryCode());
			
			if (categoryList != null) {
                CategoryVO vo = null;
                for (int i = 0, j = categoryList.size(); i < j; i++) {
                    vo = (CategoryVO) categoryList.get(i);

                    if (vo.getCategoryCode().equalsIgnoreCase(category)) {
                        response.setCategoryVO(vo);
                        break;
                    }
                }
            }
			 
			
			if (!BTSLUtil.isNullString(userName)) {
            	mcomCon = new MComConnection();con=mcomCon.getConnection();
				//UserVO userVO = new UserVO();;
                // load the user list
                String status = "'" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_CANCELED + "'";
                String statusUsed = PretupsI.STATUS_NOTIN;
                ArrayList userList = null;

                // pass p_sessionUserID if Network Admin loggedIn else null(if
                // superadmin)
                // added for operator user approval
                if (((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, loggedinUserVO.getNetworkID(), category)) != null) {
                    if (TypesI.NETWORK_ADMIN.equals(loggedinUserVO.getCategoryVO().getCategoryCode())) {
                        if (nwAdminCrossAllow) {
                            userList = userwebDAO.loadOperatorUsersList(con, loggedinUserVO.getNetworkID(), category, "%" + userName + "%", null, null, null, statusUsed, status);
                        } else {
                            userList = userwebDAO.loadOperatorUsersList(con, loggedinUserVO.getNetworkID(), category, "%" + userName + "%", null, null, loggedinUserVO.getUserID(), statusUsed, status);
                        }
                    } else {
                        userList = userwebDAO.loadOperatorUsersList(con, loggedinUserVO.getNetworkID(), category, "%" + userName + "%", null, null, null, statusUsed, status);
                    }
                } else {
                    if (TypesI.NETWORK_ADMIN.equals(loggedinUserVO.getCategoryVO().getCategoryCode())) {
                        if (nwAdminCrossAllow) {
                            userList = userwebDAO.loadUsersList(con, loggedinUserVO.getNetworkID(), category, "%" + userName + "%", null, null, null, statusUsed, status);
                        } else {
                            userList = userwebDAO.loadUsersList(con, loggedinUserVO.getNetworkID(), category, "%" + category + "%", null, null, loggedinUserVO.getUserID(), statusUsed, status);
                        }
                    } else {
                        userList = userwebDAO.loadUsersList(con, loggedinUserVO.getNetworkID(), category, "%" + userName + "%", null, null, null, statusUsed, status);
                    }
                }

				Map<String, String> categoryCodeMap = categoryList.stream()
						.collect(Collectors.toMap(CategoryVO::getCategoryCode, CategoryVO::getCategoryName));

				for (Object x : userList) {
					UserVO vo = (UserVO) x;
					vo.setCategoryCodeDesc(categoryCodeMap.getOrDefault(vo.getCategoryCode(), ""));
				}
//                if (userList == null || userList.size() <= 0) {
//                    theForm.setUserList(null);
//                    BTSLMessages btslMessage = new BTSLMessages("user.searchuser.error.usernotexist", "SearchUser");
//                    //forward = super.handleMessage(btslMessage, request, mapping);
//                } else if (userList.size() > 0) {
//                    theForm.setUserList(userList);
//                }
                
                if (userList == null || userList.size() <= 0) {
                	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NO_OPT_USER_FOUND
        	   				);
                }
                
                
                response.setViewOperatorUser(userList);
                response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				
			
			}
		
		}catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.NO_CHNL_USER_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.NO_CHNL_USER_FOUND);
		}
		
		finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
		}
		
		return response;
}
}



