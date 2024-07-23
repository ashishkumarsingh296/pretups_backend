package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;

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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;

/**
 * @(#)C2STransEnqController.java
 *                                Name Date History
 *                                ----------------------------------------------
 *                                --------------------------
 *                                Rahul Dutt 15/12/2010 Initial Creation
 *                                ----------------------------------------------
 *                                --------------------------
 *                                Copyright (c) 2010 Comviva Technologies Ltd.
 *                                Controller class for handling the Enquiry of
 *                                Channel to subscriber transfers requests from
 *                                retailers
 */
public class C2STransEnqController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(C2STransEnqController.class.getName());
    public static OperatorUtilI _operatorUtil = null;
    private C2STransferVO _c2sTransferVO = null;
    private ChannelUserVO _channelUserVO;
    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public C2STransEnqController() {
        _c2sTransferVO = new C2STransferVO();
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        C2STransferTxnDAO c2STransfertxnDAO = null;
        ArrayList c2sTransfersList = null;
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            {
            	mcomCon = new MComConnection();con=mcomCon.getConnection();
             
                c2STransfertxnDAO = new C2STransferTxnDAO();
                try {
                    _operatorUtil.validateC2STransEnqRequest(con, _c2sTransferVO, p_requestVO);
                } catch (BTSLBaseException be) {
                    _log.error("process", "BTSLBaseException " + be);
                    throw be;
                }
                if (!BTSLUtil.isNullString(_c2sTransferVO.getTransferValueStr())) {
                    c2sTransfersList = c2STransfertxnDAO.getChanneltransAmtDatewise(con, ((UserVO) p_requestVO.getSenderVO()).getNetworkID(),
                        _c2sTransferVO.getTransferDate(), _c2sTransferVO.getTransferDate(), channelUserVO.getUserPhoneVO().getMsisdn(), _c2sTransferVO.getReceiverMsisdn(),
                        Long.toString(_c2sTransferVO.getTransferValue()));
                } else {
                    c2sTransfersList = c2STransfertxnDAO.getChanneltransAmtDatewise(con, ((UserVO) p_requestVO.getSenderVO()).getNetworkID(),
                        _c2sTransferVO.getTransferDate(), _c2sTransferVO.getTransferDate(), channelUserVO.getUserPhoneVO().getMsisdn(), _c2sTransferVO.getReceiverMsisdn(),
                        PretupsI.ALL);
                }
                if (c2sTransfersList != null && !c2sTransfersList.isEmpty()) {
                    this.formatC2STransferEnqListForSMS(c2sTransfersList, p_requestVO);
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_NO_TXN_FOUND_IN_DATE_AMT_RANGE);
                }

            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransEnqController[process]","","","","BTSL Exception:"+be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransEnqController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null){mcomCon.close("C2STransEnqController#process");mcomCon=null;}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * this method use for preparing and formating SMS Message for C2S transfer
     * enquiry based on amount and transfer date of the user
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @author rahul.dutt
     */
    private void formatC2STransferEnqListForSMS(ArrayList p_c2sTransfersList, RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("formatC2STransferEnqListForSMS", "Entered: p_c2sTransfersList:" + p_c2sTransfersList + ", p_requestVO=" + p_requestVO.toString());
        }
        final String METHOD_NAME = "formatC2STransferEnqListForSMS";
        try {
            C2STransferVO c2sTransferVO = null;
            KeyArgumentVO argumentVO = null;
            final ArrayList argumentVOList = new ArrayList();
            final String[] arr = new String[1];
            int c2sTransfersLists=p_c2sTransfersList.size();
            for (int i = 0; i < c2sTransfersLists; i++) {
                c2sTransferVO = (C2STransferVO) p_c2sTransfersList.get(i);
                final String[] transferStatusArr = new String[5];
                argumentVO = new KeyArgumentVO();
                transferStatusArr[0] = c2sTransferVO.getTransferID();
                transferStatusArr[1] = c2sTransferVO.getServiceName();
                transferStatusArr[2] = c2sTransferVO.getTransferStatus();
                transferStatusArr[3] = PretupsBL.getDisplayAmount(c2sTransferVO.getQuantity());
                transferStatusArr[4] = c2sTransferVO.getTransferDateStr();
                argumentVO.setKey(PretupsErrorCodesI.LAST_TRANSFER_ENQ_MSG);
                argumentVO.setArguments(transferStatusArr);
                argumentVOList.add(argumentVO);
            }
            arr[0] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOList);
            p_requestVO.setMessageArguments(arr);
            p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_TRANSFER_ENQ_LIST_SUCCESS);
        } catch (Exception e) {
            _log.error("formatC2STransferEnqListForSMS", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "C2STransEnqController[formatC2STransferEnqListForSMS]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("C2STransEnqController", "formatC2STransferEnqListForSMS", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatC2STransferEnqListForSMS", "Exited: size =");
            }

        }

    }
}
