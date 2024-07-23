package com.selftopup.pretups.logging;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

public class CacheOperationLog {
    private static Log _log = LogFactory.getFactory().getInstance(CacheOperationLog.class.getName());

    public static void log(String p_className, String p_logMessage) {
        _log.info(p_className, p_logMessage);
    }
}
