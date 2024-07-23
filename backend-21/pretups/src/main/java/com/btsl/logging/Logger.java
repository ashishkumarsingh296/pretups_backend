/*
 * Created on Mar 1, 2005
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.logging;

/**
 * @author abhijit.chauhan
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Logger {

    private Logger _logger = null;

    /**
     * @param arg0
     */
    public Logger(String arg0) {
        _logger = Logger.getLogger(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     * @param arg0
     * @param p_logID
     */
    public void debug(Object arg0, String p_logID) {
        // TODO Auto-generated method stub
        _logger.debug(p_logID + "::" + arg0);
    }

    /**
     * 
     * @param arg0
     */
    public void debug(Object arg0) {
        // TODO Auto-generated method stub
        _logger.debug(arg0);
    }

    /**
     * 
     * @param arg0
     * @param p_logID
     */
    public void info(Object arg0, String p_logID) {
        // TODO Auto-generated method stub
        _logger.info(p_logID + "::" + arg0);
    }

    /**
     * 
     * @param arg0
     */
    public void info(Object arg0) {
        // TODO Auto-generated method stub
        _logger.info(arg0);
    }

    /**
     * 
     * @param arg0
     * @param p_logID
     */
    public void error(Object arg0, String p_logID) {
        // TODO Auto-generated method stub
        _logger.error(p_logID + "::" + arg0);
    }

    /**
     * 
     * @param arg0
     */
    public void error(Object arg0) {
        // TODO Auto-generated method stub
        _logger.error(arg0);
    }

    /**
     * @param arg0
     * @return
     */
    public static Logger getLogger(String arg0) {
        // TODO Auto-generated method stub
        Logger log = new Logger(arg0);
        return log;
    }

    /**
     * 
     * @return
     */
    public boolean isDebugEnabled() {
        return _logger.isDebugEnabled();
    }
}
