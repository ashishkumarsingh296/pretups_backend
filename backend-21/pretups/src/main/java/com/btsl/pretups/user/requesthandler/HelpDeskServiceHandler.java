/**
 * @author arvinder.singh
 *         This class is used to validate channel user on the basis of pin
 *         If pin is validated then service help desk number is returned to the
 *         channel user
 */
package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class HelpDeskServiceHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(HelpDeskServiceHandler.class.getName());

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;

        UserPhoneVO userPhoneVO = null;
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (messageArr.length == 3 || messageArr.length == 4) {
                mcomCon = new MComConnection();con=mcomCon.getConnection();
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[2]);
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                          mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                final String senderMSISDN = PretupsBL.getFilteredMSISDN(messageArr[1]);
                final String pin = messageArr[2];
                if (!BTSLUtil.isNullString(senderMSISDN)) {
                    if (!BTSLUtil.isValidMSISDN(senderMSISDN)) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "[process]", p_requestVO.getRequestIDStr(),
                                        senderMSISDN, "", "Channel User MSISDN Not valid");
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_SNDR_INVALID_MSISDN);
                    }
                }

                if (!(BTSLUtil.isNullString(senderMSISDN)) && !BTSLUtil.isNullString(pin)) {
                    HashMap map = p_requestVO.getRequestMap();
                    if (map == null) {
                        map = new HashMap();
                    }
                    map.put("HLPDSKNUM", Constants.getProperty("HLPDESK_NUM"));

                    final String arrmsg[] = { map.get("HLPDSKNUM").toString() };
                    p_requestVO.setMessageArguments(arrmsg);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.HLPDESK_SERVICE_SUCCESS);
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.HLPDESK_SERVICE_NUM_NOT_FOUND);
                }
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.HLPDESK_SERVICE_INVALIDMESSAGEFORMAT, 0,
                                new String[] { p_requestVO.getActualMessageFormat() }, null);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HelpDeskServiceHandler[process]", "", "", "",
                            "BTSL Exception:" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HelpDeskServiceHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("HelpDeskServiceHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

}
