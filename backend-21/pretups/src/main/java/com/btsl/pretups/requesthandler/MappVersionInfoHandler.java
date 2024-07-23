package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;

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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.Constants;

public class MappVersionInfoHandler implements ServiceKeywordControllerI {
    private static OperatorUtilI _operatorUtil = null;
    private ChannelUserVO channelUserVO = null;
    private static Log _log = LogFactory.getLog(MappVersionInfoHandler.class.getName());
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CRBTRegistrationController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("MappVersionInfoHandler process", "Entered p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        final Date date = new Date();
        try {
        	/*Only Skeleton created to function as simulator*/
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            HashMap requestMap = p_requestVO.getRequestMap();
            
            p_requestVO.setInfo1(Constants.getProperty("MAPP_APPTYPE"));
            p_requestVO.setInfo2(Constants.getProperty("MAPP_VERSION"));
            p_requestVO.setInfo3(Constants.getProperty("MAPP_PLATFORM"));
            p_requestVO.setInfo4(Constants.getProperty("MAPP_UPDTYPE"));
            p_requestVO.setInfo5(Constants.getProperty("MAPP_UPDURL"));
            
            p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
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
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.error(METHOD_NAME, "Exception:" + ee.getMessage());
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MappVersionInfoHandler[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("MappVersionInfoHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exited ");
            }
        }
    }
}

