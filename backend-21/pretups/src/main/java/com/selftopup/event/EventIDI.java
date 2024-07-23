/*
 * Created on 2005-04-18
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.selftopup.event;

/**
 * @author abhijit.chauhan
 * 
 *         (This interface will hold unique Event ID(Alarm IDs) for the Events)
 * 
 */
public interface EventIDI {

    final static int DATABASE_CONECTION_PROBLEM = 1;
    final static int SYSTEM_ERROR = 2;
    final static int SYSTEM_INFO = 3;
    // final static int SYSTEM_ERROR= 2;
    final static int INTERFACE_INVALID_RESPONSE = 4;
    final static int RETRY_ATTEMPT_FAILED = 5;
    final static int INTERFACE_REQUEST_EXCEPTION = 6;
    final static int INTERFACE_RESPONSE_EXCEPTION = 7;
    final static int ADMIN_OPT = 8;
}
