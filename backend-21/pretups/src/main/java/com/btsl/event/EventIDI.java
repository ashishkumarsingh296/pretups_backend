/*
 * Created on 2005-04-18
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.event;

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
	final static int ADMIN_OPT_NW_STATUS = 9;
    final static int ADMIN_OPT_PD_STATUS = 10;
    final static int CARD_GROUP_SUSPENDED=11;
    final static int CARD_GROUP_SLAB_NOT_FOUND=12;
    final static int CARD_GROUP_EXCEPTION=13;
    final static int CARD_GROUP_NOT_LOADED=14;
    final static int REQ_BLOCKTIME=15;
    final static int REQFAIL_DAYMAXLIMIT=16;
    final static int REQFAIL_DAYMAX_AMTLIMIT=17;
    final static int REQFAIL_WEEKMAXLIMIT=18;
    final static int REQFAIL_WEEKMAX_AMTLIMIT=19;
    final static int REQFAIL_MONTHMAXLIMIT=20;
    final static int REQFAIL_MONTHMAX_AMTLIMIT=21;
    final static int REQ_AMT_NOT_IN_RANGE=22;
    final static int INVALID_PIN=23;
    final static int PIN_BLOCKED=24;
    final static int MAXLOCATION_LOAD_REACHED=25;
    final static int LOCATION_RECHARGESYSTEM_OVERLOADED=26;

}
