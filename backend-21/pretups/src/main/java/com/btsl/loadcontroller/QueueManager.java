package com.btsl.loadcontroller;

import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.transfer.businesslogic.TransferVO;

public class QueueManager {

    public static final Log _log = LogFactory.getLog(QueueManager.class.getName());

    /**
	 * ensures no instantiation
	 */
    private QueueManager(){
    	
    }
    
    public static synchronized boolean offer(ArrayList p_list, TransferVO p_transferVO) {
        final String METHOD_NAME = "offer";
        try {
            p_list.add(p_transferVO);
            return true;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            return false;
        }
    }

}
