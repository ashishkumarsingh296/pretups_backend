package com.btsl.loadcontroller;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/*
 * @# InitializeAllCounters.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Feb 22, 2006 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
public class InitializeAllCounters {
    private static Log _log = LogFactory.getLog(InitializeAllCounters.class.getName());

    /**
	 * ensures no instantiation
	 */
    private InitializeAllCounters(){
    	
    }
    
    public static void main(String[] args) {
        if (_log.isDebugEnabled()) {
            _log.debug("main", "Entered");
        }
        LoadControllerCache.initializeInstanceLoad("ALL");
        LoadControllerCache.initializeNetworkLoad("ALL", "ALL");
        LoadControllerCache.initializeInterfaceLoad("ALL", "ALL", "ALL");
        LoadControllerCache.initializeTransactionLoad("ALL", "ALL", "ALL", "ALL");
        if (_log.isDebugEnabled()) {
            _log.debug("main", "Exited");
        }
    }

}
