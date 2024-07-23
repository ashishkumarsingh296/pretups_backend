package com.btsl.pretups.privaterecharge.requesthandler;

/**
 * @(#)PrivateRechargeRegController.java
 *                                       Copyright(c) 2011, Comviva technologies
 *                                       Ltd.
 *                                       All Rights Reserved
 * 
 *                                       <description>
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Author Date History
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Harpreet Kaur SEP 9, 2011 Initital
 *                                       Creation
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Private Recharge controller for
 *                                       registration and modification he sends
 *                                       a registration request and a
 *                                       modification request, following cases
 *                                       are being considered:
 *                                       a)User will be registered by auto
 *                                       generated SID(i.e system generated) or
 *                                       gives manual SID .
 *                                       b) User will be modified SID by auto
 *                                       generated SID(i.e system generated) or
 *                                       gives manual SID .
 */
import java.sql.Connection;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgDAO;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class PrivateRechargeRegController implements ServiceKeywordControllerI

{

    private final Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * process method
     * 
     * @param request
     *            VO
     *            main method for registration
     *            three cases are there:
     *            first case sender gives keyword and system generate SID
     *            automatically;
     *            second case sender gives keyword for registration or
     *            modification,first check SID validation then further proceed,
     *            Third case sender request for modification only
     * @throws BTSLBaseException
     */
    @Override
	public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO.toString());
        }
        final String METHOD_NAME = "process";
        String requestID = null;
        String msisdn = null;
        String systemSid = null;
        requestID = p_requestVO.getRequestIDStr();
        msisdn = p_requestVO.getFilteredMSISDN();
		if(BTSLUtil.isNullString(msisdn)) {
			try {
				msisdn=PretupsBL.getFilteredMSISDN(p_requestVO.getRequestMSISDN());
				p_requestVO.setFilteredMSISDN(msisdn);
			} catch (BTSLBaseException e2) {
				_log.errorTrace(METHOD_NAME,e2);
			}
		}
        PrivateRchrgVO PrivateRchrgVO = null;
        PrivateRchrgDAO PrivateRchrgDAO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
        boolean isSIDUnique = false;
        OperatorUtilI operatorUtil = null;
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeRegController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
            if (_log.isDebugEnabled()) {
                _log.debug("process", requestID, " Entered " + p_requestVO);
            }
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            PrivateRchrgDAO = new PrivateRchrgDAO();
            String messageArr[] = p_requestVO.getRequestMessageArray();
            int messageLength = messageArr.length;
            int insertCount = 0;
            int isdelete = 0;

            if (messageLength > 3) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.INVALID_SID_REG_MSG_FORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Message Length= " + messageLength);
            }
            PrivateRchrgVO = PrivateRchrgDAO.loadSubscriberSIDDetails(con, msisdn);
            BTSLMessages btslMessage =null;
            Locale locale=null;
            PushMessage pushMessage=null;
            /*
             * CASE 1: Auto Registration Mode
             * CASE 2: Manual Registration Mode
             * CASE 3: Modification Request
             */
            switch (messageLength) {
            case 1: {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Entered Case 1= " + messageLength);
                }
                systemSid = operatorUtil.generateRandomSID();
                isSIDUnique = PrivateRchrgDAO.checkIfSIDAlreadyExsist(con, systemSid);
                while (isSIDUnique) {
                    systemSid = operatorUtil.generateRandomSID();
                    isSIDUnique = PrivateRchrgDAO.checkIfSIDAlreadyExsist(con, systemSid);
                }
                if (PrivateRchrgVO == null) {
                    p_requestVO.setSid(systemSid);
                    PrivateRchrgVO = pvtRechargefillup(p_requestVO);
                    insertCount = PrivateRchrgDAO.saveSubscriberSID(con, PrivateRchrgVO);
                    if (insertCount > 0) {
                    	mcomCon.finalCommit();
                    } else {
                    	mcomCon.finalRollback();
                    }
                    String arrmsg[] = { p_requestVO.getSid() };
                    p_requestVO.setMessageArguments(arrmsg);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.SUCCESS_SID);
                   
                    
                } else {
                	String sidArr = null;
                    if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
                  	  sidArr = BTSLUtil.decrypt3DesAesText(PrivateRchrgVO.getUserSID());
                    else
                  	  sidArr = PrivateRchrgVO.getUserSID();
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ALREADY_REGISTERED, 0, new String[] { sidArr }, null);
                }
                break;
            }
            case 2: {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Entered Case 2= " + messageLength);
                }
                String enteredSID = messageArr[1];
                String subscriberSID = null;
                if (_log.isDebugEnabled()) {
                    _log.debug("process", requestID, " Entered SID" + enteredSID);
                }
                if (PrivateRchrgVO == null) {
                	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue()){
                		subscriberSID = BTSLUtil.encrypt3DesAesText(enteredSID);
                	}
                	else
                		subscriberSID = enteredSID; 
                    operatorUtil.validateSIDRules(enteredSID);
                    if (!PrivateRchrgDAO.checkIfSIDAlreadyExsist(con, subscriberSID)) {
                        p_requestVO.setSid(enteredSID);
                        PrivateRchrgVO = pvtRechargefillup(p_requestVO);
                        insertCount = PrivateRchrgDAO.saveSubscriberSID(con, PrivateRchrgVO);
                        if (insertCount > 0) {
                        	mcomCon.finalCommit();
                        } else {
                        	mcomCon.finalRollback();
                        }
                        String arrmsg[] = { enteredSID };
                        p_requestVO.setMessageArguments(arrmsg);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SUCCESS_SID);
                       
                        
                    } else {
                    	 
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.SID_ALREADY_EXISTING);
                    }
                } else {
                	String sidArr = null;
                    if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
                  	  sidArr = BTSLUtil.decrypt3DesAesText(PrivateRchrgVO.getUserSID());
                    else
                  	  sidArr = p_requestVO.getSid();
                    
                    
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ALREADY_REGISTERED, 0, new String[] { enteredSID }, null);
                }
                break;
            }
            case 3: {
                String existingSID = messageArr[1];
                String modifySID = messageArr[2];
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Entered Case 3= " + messageLength + " New SID " + modifySID + "Existing SID " + existingSID);
                }
                if (existingSID.equals(modifySID)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.OLDSID_NEWSID_SAME);
                } else {
                    if (PrivateRchrgVO == null) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.NOT_VAILD_SID_MODIFICATION);
                    } else {
                        String sidFromVo;
                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
                        	sidFromVo= BTSLUtil.decrypt3DesAesText(PrivateRchrgVO.getUserSID());
                        else
                        	sidFromVo= PrivateRchrgVO.getUserSID();
                        if (_log.isDebugEnabled()) {
                            _log.debug("process", "Entered Case= " + messageLength + " SID from VO " + sidFromVo + "Existing SID " + existingSID);
                        }
                        if (existingSID.equals(sidFromVo)) {
                            operatorUtil.validateSIDRules(modifySID);
                            if (!PrivateRchrgDAO.checkIfSIDAlreadyExsist(con, modifySID)) {
                                PrivateRchrgVO.setCreatedOn(PrivateRchrgVO.getCreatedOn());
                                isdelete = PrivateRchrgDAO.deleteUserDetails(con, existingSID);
                                if (isdelete > 0) {
                                    p_requestVO.setSid(modifySID);
                                    PrivateRchrgVO = pvtRechargefillup(p_requestVO);
                                    insertCount = PrivateRchrgDAO.saveSubscriberSID(con, PrivateRchrgVO);
                                } else {
                                    con.rollback();
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeRegController[deactivateSubscriberSID]", existingSID, "", "", "No row delete Exception:");
                                    throw new BTSLBaseException(this, "deleteUserDetails", "No row delete ");
                                }
                                if (insertCount > 0) {
                                	mcomCon.finalCommit();
                                } else {
                                	mcomCon.finalRollback();
                                }
                                p_requestVO.setSid(modifySID);
                                String arrmsg[] = { p_requestVO.getSid() };
                                p_requestVO.setMessageArguments(arrmsg);
                                p_requestVO.setMessageCode(PretupsErrorCodesI.SUCCESS_SID);
                               
                            } else {
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.SID_ALREADY_EXISTING);
                            }
                        } else {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.OLD_SID_NOT_MATCHED);
                        }
                    }
                }
                break;
            }
            default :
            {
            	LogFactory.printLog(METHOD_NAME, "Default Case: No case found. Kindly check condition.", _log);
            }            	
            }
			p_requestVO.setSuccessTxn(true);
        } catch (BTSLBaseException be) {
        		p_requestVO.setSuccessTxn(false);
        	if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            p_requestVO.setMessageArguments(be.getArgs());
            _log.error("process", requestID, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.errorTrace(METHOD_NAME, e);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }

            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            _log.error("process", requestID, "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrivateRechargeRegController[process]", "", "", "", "Exception:" + e.getMessage());
        } finally {
			if (mcomCon != null) {
				mcomCon.close("PrivateRechargeRegController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", requestID, " Exited ");
            }
        }
    }

    public PrivateRchrgVO pvtRechargefillup(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("pvtRechargefillup", " Entered " + p_requestVO.toString());
        }
        PrivateRchrgVO PrivateRchrgVO = new PrivateRchrgVO();
        PrivateRchrgVO.setMsisdn(p_requestVO.getFilteredMSISDN());
        if(BTSLUtil.isNullString(PrivateRchrgVO.getMsisdn())){
        	try {
    			PrivateRchrgVO.setMsisdn(PretupsBL.getFilteredMSISDN(p_requestVO.getRequestMSISDN()));
    		} catch (BTSLBaseException e) {
    			_log.errorTrace("pvtRechargefillup",e);
    		}
        }
        PrivateRchrgVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        PrivateRchrgVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        PrivateRchrgVO.setCreatedOn(p_requestVO.getCreatedOn());
        PrivateRchrgVO.setModifyOn(p_requestVO.getCreatedOn());
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
        	PrivateRchrgVO.setUserSID(BTSLUtil.encrypt3DesAesText(p_requestVO.getSid()));
        else
        	PrivateRchrgVO.setUserSID(p_requestVO.getSid());

        if (_log.isDebugEnabled()) {
            _log.debug("pvtRechargefillup", " Exited ", PrivateRchrgVO.toString());
        }
        return PrivateRchrgVO;

    }
}
