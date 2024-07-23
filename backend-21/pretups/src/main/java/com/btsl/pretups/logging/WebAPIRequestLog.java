package com.btsl.pretups.logging;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class WebAPIRequestLog {
    private static Log _log = LogFactory.getLog(WebAPIRequestLog.class.getName());
    private WebAPIRequestLog(){

    }

    public static void log( String message) {
        _log.info("", message);
    }

}
