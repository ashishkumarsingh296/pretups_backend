package com.restapi.superadmin.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.channelAdmin.ChannelUserListI;
import com.restapi.superadmin.OperatorApprovalController;
import com.restapi.superadmin.requestVO.ApprovalOperatorUsersRequestVO;
import com.restapi.superadmin.responseVO.ApprovalOperatorUsersListResponseVO;
import com.restapi.superadmin.serviceI.OperatorApprovalServiceI;
import com.web.user.businesslogic.UserWebDAO;

@Service("OperatorApprovalServiceI")
public class OperatorApprovalServiceImpl  implements OperatorApprovalServiceI{
	
	public static final Log log = LogFactory.getLog(OperatorApprovalController.class.getName());
	public static final String classname = "OperatorApprovalServiceImpl";

	@Override
	public ApprovalOperatorUsersListResponseVO loadApprovalOperatorUsersInList(Connection con, UserVO channelUserVO,
			HttpServletResponse response1, ApprovalOperatorUsersRequestVO requestVO, String searchType) {
		
		final String METHOD_NAME = "OperatorApprovalServiceImpl";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		
		String status = null;
		ArrayList userList = null;
		ApprovalOperatorUsersListResponseVO response = new ApprovalOperatorUsersListResponseVO();
		status = "'" + PretupsI.USER_STATUS_NEW + "'";
		
		boolean alreadyApproved=false;
		boolean noUserFound =false;
		
		try {
			UserWebDAO userwebDAO = new UserWebDAO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			UserDAO userDAO = new UserDAO();
			
			if (searchType.equalsIgnoreCase("LoginId"))// load user
	            // deatils
	            // by
	            // LoginId
			    {
				
				UserVO userVOCheck = userDAO.loadUsersDetailsByLoginID(con, requestVO.getLoginID());
				 if(null==userVOCheck) {
					 noUserFound=true;
				 }
				if(userVOCheck!=null &&  PretupsI.YES.equals(userVOCheck.getStatus()) ) {
					alreadyApproved=true;
				}
			        /*
	                 * Search Criteria = L (means user search through Login Id)
	                 */
	                //theForm.setSearchCriteria("L");
	                // con = OracleUtil.getConnection();
	                // load the user info on the basis of LoginId number
	                
	                UserVO userVO = null;
	                userVO = channelUserDAO.loadUsersDetailsByLoginId(con, requestVO.getLoginID(), null, PretupsI.STATUS_IN, status);

	                if (userVO != null) {
	                	
	                	if(!userVO.getNetworkID().equalsIgnoreCase(channelUserVO.getNetworkID())){
//	                		BTSLMessages btslMessage = new BTSLMessages("user.viewapprovalusers.label.nouserlistexistlevel1", "SelectCategoryForAprl");
//	                        forward = super.handleMessage(btslMessage, request, mapping);
//	                        return forward;
	                		throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_USERS_EXIST_LEVEL_APPROVAL,
									0, null);
	                	}
	                    // check to see if the users are at same level or not
	                    // if they are at the same level then their category code
	                    // will be same
	                    if (channelUserVO.getCategoryVO().getCategoryCode().equals(userVO.getCategoryVO().getCategoryCode())) {
	                        // check the user in the same domain or not
	                        String arr2[] = { requestVO.getLoginID() };
//	                        LOG.error(methodName, "Error: User are at the same level");
//	                        throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidatsamelevel", 0, arr2, forwardJsp);
	                    	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LOGINID_SAME_LEVEL,
									0,arr2, null);
	                    }
	                    // ////////////////////////
	                    ListValueVO listValueVO = null;
//	                    if (theForm.getSelectDomainList() != null) {
//	                        /*
//	                         * boolean isDomainFlag =
//	                         * this.isExistDomain(theForm.getSelectDomainList(),
//	                         * userVO); if(!isDomainFlag) { //check the user in the
//	                         * same domain or not String arr2[] =
//	                         * {theForm.getSearchLoginId()};
//	                         * LOG.error("loadApprovalUsersList","Error: User not
//	                         * in the same domain"); throw new
//	                         * BTSLBaseException(this,"loadApprovalUsersList",
//	                         * "user.selectchanneluserforview.error.userloginidnotinsamedomain"
//	                         * ,0,arr2,forwardJsp); }
//	                         * else
//	                         */
//	                        {
//	                            listValueVO = BTSLUtil.getOptionDesc(userVO.getCategoryVO().getDomainCodeforCategory(), theForm.getSelectDomainList());
//	                            theForm.setDomainCodeDesc(listValueVO.getLabel());
//	                        }
//	                    }

	                    // check for searched user is exist in the same domain or
	                    // not
	                    boolean isDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, userVO.getUserID(), userVO.getCategoryVO().getGrphDomainType(), channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType());
	                    if (isDomainFlag) {
//	                        theForm.setCategoryVO(userVO.getCategoryVO());
//	                        theForm.setCategoryCode(theForm.getCategoryVO().getCategoryCode());
//	                        theForm.setChannelCategoryCode(theForm.getCategoryVO().getCategoryCode());
//	                        theForm.setCategoryCodeDesc(theForm.getCategoryVO().getCategoryName());
//	                        theForm.setChannelCategoryDesc(theForm.getCategoryVO().getCategoryName());
	                        userList = new ArrayList();
	                        userList.add(userVO);
	                    } else {
	                        // check the user in the same domain or not
	                    	String arr2[] = { requestVO.getLoginID() };
//	                        LOG.error(methodName, "Error: User not in the same domain");
//	                        throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidnotinsamegeodomain", 0, arr2, forwardJsp);
	                    	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LOGINID_SAME_GEO_DOMAIN,
									0,arr2, null);
	                    }
	                    // ////////////////////////
	                }

	                else {
	                	
	                	if(alreadyApproved) {
	                		
	                		String arr2[] = { requestVO.getLoginID() };
		                    //LOG.error(methodName, "Error: User not exist");
		                    //throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidnotexist", 0, arr2, forwardJsp);
		                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_ALREADY_APPROVED, arr2);
	                	}
	                	
	                	
	                	if(noUserFound) {
	                    // throw exception no user exist with this Login Id
		                	String arr2[] = { requestVO.getLoginID() };
		                    //LOG.error(methodName, "Error: User not exist");
		                    //throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidnotexist", 0, arr2, forwardJsp);
		                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_USER_EXIST_LOGINID,
									0,arr2, null);
	                	}
	                }
	            } else {
//	                if (theForm.getCategoryList() != null) {
//	                    CategoryVO vo = null;
//	                    for (int i = 0, j = theForm.getCategoryList().size(); i < j; i++) {
//	                        vo = (CategoryVO) theForm.getCategoryList().get(i);
//
//	                        if (vo.getCategoryCode().equalsIgnoreCase(theForm.getCategoryCode())) {
//	                            theForm.setCategoryVO(vo);
//	                            theForm.setCategoryCodeDesc(vo.getCategoryName());
//	                            break;
//	                        }
//	                    }
//	                }
	                /*
	                 * if(TypesI.NETWORK_ADMIN.equals(sessionUserVO.getCategoryVO().
	                 * getCategoryCode())) {
	                 * userList =
	                 * userDAO.loadApprovalOPTUsersList(con,theForm.getCategoryCode()
	                 * ,"URTYP",sessionUserVO.getNetworkID(),
	                 * status); //userList =
	                 * userDAO.loadApprovalOperatorUsersList(con,sessionUserVO.
	                 * getNetworkID
	                 * (),theForm.getCategoryCode(),"%"+theForm.getUserName
	                 * ()+"%",null
	                 * ,null,sessionUserVO.getUserID(),statusUsed,status); }
	                 * else
	                 */
	                
	                    userList = userwebDAO.loadApprovalOPTUsersList(con, requestVO.getCategory(), "URTYP", channelUserVO.getNetworkID(), status);
	                    // userList =
	                    // userDAO.loadApprovalOperatorUsersList(con,sessionUserVO.getNetworkID(),theForm.getCategoryCode(),"%"+theForm.getUserName()+"%",null,null,null,statusUsed,status);
	                
	                // userList =
	                // userDAO.loadApprovalUsersList(con,theForm.getCategoryVO().getCategoryCode(),PretupsI.USER_STATUS_TYPE,theForm.getCategoryVO().getSequenceNumber(),theForm.getCategoryVO().getGrphDomainType(),sessionUserVO.getNetworkID(),theForm.getParentDomainCode(),status);
	                //theForm.setSearchCriteria("D");
	                    if(userList.size()==0) {
	                    	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_USER_EXIST_CATEGORY,
									0, null);
	                    }
	                
	            }
			
			response.setApprovalOperatorUsersList(userList);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		}
		catch(BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.NO_OPERATOR_USER_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.NO_OPERATOR_USER_FOUND);
		}
		
		return response;
	}
	
	
	
}
