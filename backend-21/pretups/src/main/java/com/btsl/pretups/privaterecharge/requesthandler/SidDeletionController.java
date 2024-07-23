package com.btsl.pretups.privaterecharge.requesthandler;

/**
 * @(#)SidDeletionController.java
 *                                Copyright(c) 2010, Comviva Technologies.
 *                                All Rights Reserved
 *                                this class use for deleting the SID of a
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
import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgDAO;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class SidDeletionController implements ServiceKeywordControllerI {
    private final Log _log = LogFactory.getLog(this.getClass().getName());

    @Override
	public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        int status = 0;
        int flag = 0;
        String sid = null;
        String msisdn = null;
        PrivateRchrgDAO privaterechargeDAO = new PrivateRchrgDAO();
        OperatorUtilI operatorUtil = null;
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SidDeletionCOntroller[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            flag = operatorUtil.getSIDDeletionMessageArray(con, p_requestVO.getRequestMessageArray(), p_requestVO);
            if (flag == 1) {
                /*
                 * Cheking user Exsist or not
                 */
				msisdn=PretupsBL.getFilteredMSISDN(p_requestVO.getRequestMSISDN());
                PrivateRchrgVO privateRchrgVO = new PrivateRchrgVO();
                privateRchrgVO = privaterechargeDAO.loadSubscriberSIDDetails(con, msisdn);
                if (privateRchrgVO != null) {
                	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
                		sid = BTSLUtil.encrypt3DesAesText(p_requestVO.getSid());
                	else
                		sid = p_requestVO.getSid();
                    status = privaterechargeDAO.deactivateSubscriberSID(con, sid, msisdn);
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.MSISDN_NOT_EXIST);
                }
            }
            if (status == 1) {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.DEACTIVATE_SUCCESS_MESSAGE);
				String[] args= new String[1];
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
					args[0]=BTSLUtil.decrypt3DesAesText(sid);
				else
					args[0]=sid;
				p_requestVO.setMessageArguments(args);

				
				mcomCon.finalCommit();
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_DELETE_SID_NORECORD, 0, null, null);
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
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SidDeletionController[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("SidDeletionController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }

    }
}
