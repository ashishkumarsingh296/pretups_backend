package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.OracleUtil;

public class SelfTopUpRegUsingTwoReqConsumer implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(SelfTopUpRegUsingTwoReqConsumer.class.getName());
    private static SubscriberDAO _subscriberDAO = new SubscriberDAO();
    private final static ReentrantLock lock = new ReentrantLock();
    public static OperatorUtilI operatorUtil = null;
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " DeleteBuddyController [initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }
        String msisdn = null;
        String imei = null;
        String uniqueCode = null;
        String[] args = p_requestVO.getRequestMessageArray();
        String pin = null;
        // Connection con =null;
        try {
            // SenderVO senderVO=new SenderVO();
            uniqueCode = args[1];
            imei = args[2];
            p_requestVO.setImei(imei);
            pin = PretupsBL.genratePin();
            // ((SenderVO)p_requestVO.getSenderVO())
            p_requestVO.setPin(pin);
            boolean isRegistered = false;
            for (int i = 0; i < 10; i++) {
                lock.lock();
                try {
                    if (SelfTopUpRegUsingTwoReqProducer._imeiUniqueCodeMap.containsKey(imei + "_" + uniqueCode)) {
                        // con =OracleUtil.getSingleConnection();
                        msisdn = SelfTopUpRegUsingTwoReqProducer._imeiUniqueCodeMap.get(imei + "_" + uniqueCode);
                        // senderVO=new
                        // SubscriberDAO().loadSubscriberDetailsByMsisdn(con,
                        // msisdn);
                        p_requestVO.setRequestMSISDN(msisdn);
                        p_requestVO.setFilteredMSISDN(msisdn);

                        try {
                            new SelfTopUpRegistrationController().process(p_requestVO);

                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                        }
                        isRegistered = true;
                        SelfTopUpRegUsingTwoReqProducer._imeiUniqueCodeMap.remove(imei + "_" + uniqueCode);
                        break;
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                } finally {
                    lock.unlock();
                }
            }

            if (!isRegistered) {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
            } else {
                if (SystemPreferences.PIN_REQUIRED) {
                    _log.debug("SelfTopUpRegUsingTwoReqConsumer process", "Successful ");
                    String msgArr[] = { msisdn };
                    p_requestVO.setMessageArguments(msgArr);
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_SUCCESS);
                } else {
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_SUCCESS);
                    String args1[] = { msisdn };
                    p_requestVO.setMessageArguments(args1);
                }
            }
        } catch (Exception e) {
            _log.errorTrace("process", e);
            try {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
            } catch (BTSLBaseException e1) {
                _log.errorTrace("process", e1);
            }
        } finally {
            /*
             * try {
             * if (con != null)
             * con.close();
             * } catch (Exception e){
             * _log.errorTrace("process", e);
             * }
             */
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }

    }
}
