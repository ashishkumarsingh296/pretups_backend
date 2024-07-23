package com.btsl.pretups.channel.transfer.requesthandler;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
/** 
 * @author diwakar
 */
public class UserRoleAddModifyController implements ServiceKeywordControllerI {
	
    private Log log = LogFactory.getLog(UserRoleAddModifyController.class.getName());
    private ChannelUserVO _channelUserVO = null;
    private UserDAO _userDAO = null;
    private ChannelUserVO modifiesChannelUserVO = null;
    private UserRolesDAO userRolesDAO = null;

    /**
     * Method Process
     * Process Method , Processes external channel user registration request
     * 
     * @param p_requestVO
     */
@Override
    public void process(RequestVO p_requestVO) {
    final String methodName = "process";
    final String controllerName="UserRoleAddModifyController";
        LogFactory.printLog(controllerName+methodName, "Entered p_requestVO=" + p_requestVO, log);
       
        Connection con = null;MComConnectionI mcomCon = null;
        HashMap requestMap = p_requestVO.getRequestMap();
        _channelUserVO = new ChannelUserVO();
        _userDAO = new UserDAO();
        userRolesDAO = new UserRolesDAO();

        OperatorUtilI operatorUtili = null;
        final String msg[] = new String[1];
        Locale locale = null;
        final ArrayList oldPhoneList = null;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,controllerName+methodName , "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();

            requestMap = p_requestVO.getRequestMap();

            /*
             * Validation for Channel ADMIN or Channel user who requesting user
             * request.if BCU is the category code i.e. User is Channel admin
             * OPERATOR_CATEGORY and External Code exists, msisdn and sms pin is
             * valid or not.
             */
            final String userMsisdn = (String) requestMap.get("USERMSISDN");// User
            // MSISDN

            // Requester Validation Ends
            // Load details of channel user to be modified
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            final String userLoginId = (String) requestMap.get("USERLOGINID");
            final String roleCode = (String) requestMap.get("ROLECODE");
            String actionId = (String) requestMap.get("ACTION");
            actionId = actionId.trim();

            // Check for role code
            if (!userRolesDAO.isRoleCodeExist(con, roleCode.toUpperCase())) {
                final String arrmsg[] = { roleCode };
                throw new BTSLBaseException(this,controllerName+methodName, PretupsErrorCodesI.XML_ROLE_CODE_INVALID, arrmsg);
            }

            // Load Channel User on basis of either parameter MSISDN, Login Id
            modifiesChannelUserVO = new ExtUserDAO().loadChannelUserDetailsByMsisdnLoginIdExt(con, userMsisdn, userLoginId, null, null, locale);
            if (modifiesChannelUserVO == null) {
                throw new BTSLBaseException(this,controllerName+methodName, PretupsErrorCodesI.EXTSYS_REQ_RECEIVER_DETAILS_INVALID);// 21-02-2014
            }

            boolean roleAvailable = false;
            if ("Y".equalsIgnoreCase(modifiesChannelUserVO.getCategoryVO().getFixedRoles())) {
                roleAvailable = _userDAO.isFixedRoleAndExist(con, modifiesChannelUserVO.getCategoryCode(), roleCode, TypesI.OPERATOR_USER_TYPE);
            } else {
                roleAvailable = _userDAO.isAssignedRoleAndExist(con, modifiesChannelUserVO.getUserID(), roleCode, TypesI.OPERATOR_USER_TYPE);
            }

            if (!roleAvailable) {
                if (actionId.equalsIgnoreCase("D")) {
                    throw new BTSLBaseException(this,controllerName, PretupsErrorCodesI.XML_ERROR_USER_ROLE_UNAVAILABLE);
                }
            }

            final String message = null;
            // Setting some use full parameter
            if (actionId.equalsIgnoreCase("A")) {
                final String[] roles = new String[1];
                roles[0] = roleCode;
                final boolean existFlag = userRolesDAO.isUserRoleCodeAssociated(con, modifiesChannelUserVO.getUserID(), roles[0]);
                if (existFlag) {
                    throw new BTSLBaseException(this,controllerName,PretupsErrorCodesI.XML_ROLE_CODE_ADD_FAIL);
                } else {
                    final int insertCount = userRolesDAO.addUserRolesList(con, modifiesChannelUserVO.getUserID(), roles);
                    if (insertCount <= 0) {
                        throw new BTSLBaseException(this,controllerName, PretupsErrorCodesI.XML_ROLE_CODE_ADD_FAIL);
                    }
                    con.commit();
                }
            } else if (actionId.equalsIgnoreCase("D")) {
                // delete the user roles info
                final int deleteCount = userRolesDAO.deleteUserRoles(con, modifiesChannelUserVO.getUserID());

                if (deleteCount <= 0) {
                    con.rollback();
                    log.error("saveChangeRole", "Error: while Deleting User Roles");
                    throw new BTSLBaseException(this, controllerName, PretupsErrorCodesI.XML_ROLE_CODE_DELETE_FAIL);
                }
                con.commit();
            }

            requestMap.put("CHNUSERVO", _channelUserVO);
            p_requestVO.setRequestMap(requestMap);
            _channelUserVO.setMsisdn(modifiesChannelUserVO.getMsisdn());

            final String arrmsg[] = { roleCode };
            String smsMessage;
            if (actionId.equalsIgnoreCase("A")) {
                smsMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.XML_ROLE_CODE_ADD_SUCC, arrmsg);
                p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ROLE_CODE_ADD_SUCC);
            } else {
                smsMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.XML_ROLE_CODE_DELETE_SUCC, arrmsg);
                p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ROLE_CODE_DELETE_SUCC);
            }
            new PushMessage(_channelUserVO.getMsisdn(), smsMessage, null, null, locale).push();
            p_requestVO.setMessageArguments(arrmsg);

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "BTSLBaseException " + be.getMessage());
            log.errorTrace(methodName, be);
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
                    con.rollback();
                }
            } catch (Exception ee) {
                log.errorTrace(methodName, ee);
            }
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRoleAddModifyController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            _userDAO = null;
            _channelUserVO = null;

			if (mcomCon != null) {
				mcomCon.close("UserRoleAddModifyController#process");
				mcomCon = null;
			}
            LogFactory.printLog(methodName, " Exited ", log);
           
        }
    }

}
