package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;

public class SelfTopUpRegUsingTwoReqProducer implements ServiceKeywordControllerI {

    public static Map<String, String> _imeiUniqueCodeMap = null;
    private static Log _log = LogFactory.getLog(SelfTopUpRegUsingTwoReqProducer.class.getName());
    private final static ReentrantLock lock = new ReentrantLock();
    static {

        try {
            _imeiUniqueCodeMap = new HashMap<String, String>();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " SelfTopUpRegUsingTwoReqProducer [initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }
        String msisdn = null;
        String imei = null;
        String uniqueCode = null;
        try {

            String[] args = p_requestVO.getRequestMessageArray();
            uniqueCode = args[1];
            msisdn = p_requestVO.getRequestMSISDN();
            imei = args[2];
            lock.lock();
            try {
                _imeiUniqueCodeMap.put(imei + "_" + uniqueCode, msisdn);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("process", " Exited ");
        }
    }
}
