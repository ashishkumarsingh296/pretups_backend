package com.btsl.pretups.privaterecharge.requesthandler;

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
import com.btsl.util.BTSLUtil;

/**
 * @(#)SidDeletionController.java
 *                                Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 *                                this class is used for returning the SID of a
 *                                pre-registered user
 *                                <description>
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                ankuj arora Dec 27,2010 Initital Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 * 
 */
public class SidEnquiryController implements ServiceKeywordControllerI {
    private final Log _log = LogFactory.getLog(this.getClass().getName());

    @Override
	public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        int flag = 0;
        PrivateRchrgDAO privateRchrgDAO = null;
        PrivateRchrgVO privateRchrgVO = null;
        OperatorUtilI operatorUtil = null;
        String msisdn = null;
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SidEnquiryController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            flag = operatorUtil.getSIDEnquiryMessageArray(con, p_requestVO.getRequestMessageArray(), p_requestVO);
            if (flag == 1) {
                msisdn = p_requestVO.getFilteredMSISDN();
                privateRchrgDAO = new PrivateRchrgDAO();
                privateRchrgVO = privateRchrgDAO.loadSubscriberSIDDetails(con, msisdn);
            }
            BTSLMessages btslMessage = null;
            Locale locale=null;
            PushMessage pushMessage = null;
            if (privateRchrgVO != null) {	
            	p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_SUCCESS_RESPONSE_MESSAGE);	
				String[] args= new String[1];
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
					args[0]=BTSLUtil.decrypt3DesAesText(privateRchrgVO.getUserSID());
				else
					args[0]=privateRchrgVO.getUserSID();
				p_requestVO.setMessageArguments(args);
                p_requestVO.setSid(privateRchrgVO.getUserSID());
                p_requestVO.setSuccessTxn(true);
                
                mcomCon.finalCommit();
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_DELETE_MSISDN_NORECORD);
			}
            p_requestVO.setSuccessTxn(true);
            
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("@@@@ process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
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
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SidEnquiryController[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("SidEnquiryController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }

    }
}
