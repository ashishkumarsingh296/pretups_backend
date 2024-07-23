package com.btsl.loadcontroller;

import com.btsl.util.Constants;

/*
 * LoadControllerI.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Interface class for storing all the values
 */

public interface LoadControllerI {

    public String STATUS_Y = "Y";// for status set to Y
    public String STATUS_N = "N";// for status set to N

    public String DEFAULT_INSTANCE_ID = Constants.getProperty("DEF_INSTANCE_ID"); // Not
                                                                                  // Required
                                                                                  // Default
                                                                                  // Instance
                                                                                  // ID
                                                                                  // to
                                                                                  // be
                                                                                  // used
                                                                                  // will
                                                                                  // be
                                                                                  // changed
                                                                                  // as
                                                                                  // per
                                                                                  // the
                                                                                  // installations
                                                                                  // on
                                                                                  // the
                                                                                  // server
    public String LOAD_CONTROLLER_DEF_TYPE = "TXN";// Default load controller
                                                   // type i.e Transaction
                                                   // counts
    public String LOAD_CONTROLLER_TXN_TYPE = "TXN";// Transaction counts load
                                                   // controller type
    public String LOAD_CONTROLLER_TPS_TYPE = "TPS";// TPS Based load controller
                                                   // type

    public int INSTANCE_NEW_REQUEST = 100;// Instance New Request Recieved
    public int NETWORK_NEW_REQUEST = 101;// Network New Request Recieved
    public int PROCESSED_FROM_QUEUE = 102;// Instance New Request Recieved
    public int REFUSED_FROM_QUEUE = 103;
    public int TIMEOUT_FROM_QUEUE = 104;
    public int REQUEST_TIMEDOUT = 105;
    public int DEC_LAST_TRANS_COUNT = 200;// Instance New Request Recieved
    public int DEC_SAME_SEC_RES_COUNT = 201;

    public int SENDER_UNDER_VAL = 202;
    public int RECEIVER_UNDER_VAL = 203;
    public int SENDER_UNDER_TOP = 204;
    public int RECEIVER_UNDER_TOP = 205;

    public int SENDER_VAL_SUCCESS = 206;
    public int RECEIVER_VAL_SUCCESS = 207;
    public int SENDER_TOP_SUCCESS = 208;
    public int RECEIVER_TOP_SUCCESS = 209;
    public int INTERNAL_FAIL_COUNT = 210;

    public int SENDER_VAL_FAILED = 211;
    public int RECEIVER_VAL_FAILED = 212;
    public int SENDER_TOP_FAILED = 213;
    public int RECEIVER_TOP_FAILED = 214;

    public int SENDER_VAL_RESPONSE = 220;
    public int RECEIVER_VAL_RESPONSE = 221;
    public int SENDER_TOP_RESPONSE = 222;
    public int RECEIVER_TOP_RESPONSE = 223;

    public String USERTYPE_SENDER = "SENDER";
    public String USERTYPE_RECEIVER = "RECEIVER";

    public int COUNTER_NEW_REQUEST = 900;
    public int COUNTER_SUCCESS_REQUEST = 901;
    public int COUNTER_FAIL_REQUEST = 902;
    public int COUNTER_UNDERPROCESS_REQUEST = 903;
    public int COUNTER_ROAM_REQUEST = 904;
    public int COUNTER_BEF_GTW_FAIL_REQUEST = 905;
    public int COUNTER_BEF_NET_FAIL_REQUEST = 906;
    public int COUNTER_BEF_SER_FAIL_REQUEST = 907;
    public int COUNTER_OTHER_FAIL_REQUEST = 908;

    public int ENTRY_IN_QUEUE = 909;
    public int FAIL_BEF_PROCESSED_QUEUE = 910;

}
