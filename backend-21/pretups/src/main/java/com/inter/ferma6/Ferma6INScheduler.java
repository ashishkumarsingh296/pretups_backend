package com.inter.ferma6;

/**
 * @(#)Ferma6INScheduler.java
 *                            Copyright(c) 2005, Bharti Telesoft Int. Public
 *                            Ltd.
 *                            All Rights Reserved
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Abhijit Chauhan Dec 26,2005 Initial Creation
 *                            --------------------------------------------------
 *                            ----------------------------------------------
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;

public class Ferma6INScheduler {
    private static Log _log = LogFactory.getLog(Ferma6INScheduler.class.getName());
    private String _interfaceID = null;
    private String _urlStr = null;
    private int _urlID = 0;
    private long _resetTime = 0;
    private long _minHoldDuration = 0;
    private long _maxHoldDuration = 0;

    /**
     * Replace FERMA URL
     * 
     */
    public synchronized void replaceUrl() {
        if (_log.isDebugEnabled())
            _log.debug("replaceUrl", "Entered _urlID=" + _urlID + " URL: " + FileCache.getValue(_interfaceID, "URL" + _urlID));
        long currentTime = System.currentTimeMillis();
        if (currentTime - _resetTime > _minHoldDuration)// if minimum hold time
                                                        // reaches then only
                                                        // divert URL
        {
            _log.info("replaceUrl", "Replacing URL currentTime: " + currentTime + " _resetTime" + _resetTime + " _minHoldDuration=" + _minHoldDuration);
            if (_urlID == 1) {
                _urlID = 2;
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "Ferma6INScheduler[replaceUrl]", "", "", "", "Ferma Switchover from Node 1 to Node 2");
            } else {
                _urlID = 1;
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "Ferma6INScheduler[replaceUrl]", "", "", "", "Ferma Switchover from Node 2 to Node 1");
            }
            _urlStr = FileCache.getValue(_interfaceID, "URL" + _urlID);
            _resetTime = currentTime;
        } else
            // if minimum hold time not reaches then ignore replacing
            _log.info("replaceUrl", "Ignoring replace since less than minimum hold time");

        if (_log.isDebugEnabled())
            _log.debug("replaceUrl", "Exiting _urlID=" + _urlID + " URL: " + FileCache.getValue(_interfaceID, "URL" + _urlID));
    }

    public synchronized void forceReplaceUrl() {
        if (_log.isDebugEnabled())
            _log.debug("forceReplaceUrl", "Entered _urlID=" + _urlID);
        long currentTime = System.currentTimeMillis();
        _log.info("forceReplaceUrl", "Replacing URL currentTime: " + currentTime + " _resetTime" + _resetTime + " _minHoldDuration=" + _minHoldDuration);
        if (_urlID == 1)
            _urlID = 2;
        else
            _urlID = 1;
        _urlStr = FileCache.getValue(_interfaceID, "URL" + _urlID);
        _resetTime = currentTime;
        if (_log.isDebugEnabled())
            _log.debug("forceReplaceUrl", "Exiting _urlID=" + _urlID);
    }

    public long getResetTime() {
        return _resetTime;
    }

    public void setResetTime(long resetTime) {
        _resetTime = resetTime;
    }

    /**
     * Getter for Url String
     * 
     * @return
     */
    public String getUrlStr() {
        try {
            if (_urlID == 1)
                return _urlStr;
            else {
                long currentTime = System.currentTimeMillis();
                if (_log.isDebugEnabled())
                    _log.debug("getUrlStr", "currentTime: " + currentTime + " _resetTime: " + _resetTime + " _maxHoldDuration: " + _maxHoldDuration);
                if (currentTime - _resetTime > _maxHoldDuration) {
                    _urlID = 1;
                    _resetTime = currentTime;
                    _urlStr = FileCache.getValue(_interfaceID, "URL" + _urlID);
                    _log.info("getUrlStr", "Switching to IN 1");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "Ferma6INScheduler[getUrlStr]", "", "", "", "Switching to IN 1");
                }
                return _urlStr;
            }
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getUrlStr", "Exiting _urlID=" + _urlID);
        }
    }

    public void setUrlStr(String urlStr) {
        _urlStr = urlStr;
    }

    public int getUrlID() {
        return _urlID;
    }

    public void setUrlID(int urlID) {
        _urlID = urlID;
    }

    public long getMaxHoldDuration() {
        return _maxHoldDuration;
    }

    public void setMaxHoldDuration(long maxHoldDuration) {
        _maxHoldDuration = maxHoldDuration;
    }

    public long getMinHoldDuration() {
        return _minHoldDuration;
    }

    public void setMinHoldDuration(long minHoldDuration) {
        _minHoldDuration = minHoldDuration;
    }

    public String getInterfaceID() {
        return _interfaceID;
    }

    public void setInterfaceID(String interfaceID) {
        _interfaceID = interfaceID;
    }
}
