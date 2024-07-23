package com.btsl.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author deepa.shyam
 *
 */
public class BaseResponseRedoclyCommon {
    public static final String BAD_REQUEST = "[ { \"status\": 400, \"messageCode\": \"string\", \"message\": \"Success\", \"errorMap\": { \"masterErrorList\": [ { \"errorCode\": \"string\", \"errorMsg\": \"string\" } ], \"rowErrorMsgLists\": [ { \"rowValue\": \"string\", \"rowName\": \"string\", \"masterErrorList\": [ { \"errorCode\": \"string\", \"errorMsg\": \"string\" } ], \"rowErrorMsgList\": [ {} ] } ] }, \"transactionId\": \"transactionId\" } ]" ;
    public static final String NOT_FOUND = "[ { \"status\": 404, \"messageCode\": \"string\", \"message\": \"Success\", \"errorMap\": { \"masterErrorList\": [ { \"errorCode\": \"string\", \"errorMsg\": \"string\" } ], \"rowErrorMsgLists\": [ { \"rowValue\": \"string\", \"rowName\": \"string\", \"masterErrorList\": [ { \"errorCode\": \"string\", \"errorMsg\": \"string\" } ], \"rowErrorMsgList\": [ {} ] } ] }, \"transactionId\": \"transactionId\" } ]" ;
    public static final String INTERNAL_SERVER_ERROR = "[ { \"status\": 500, \"messageCode\": \"string\", \"message\": \"Success\", \"errorMap\": { \"masterErrorList\": [ { \"errorCode\": \"string\", \"errorMsg\": \"string\" } ], \"rowErrorMsgLists\": [ { \"rowValue\": \"string\", \"rowName\": \"string\", \"masterErrorList\": [ { \"errorCode\": \"string\", \"errorMsg\": \"string\" } ], \"rowErrorMsgList\": [ {} ] } ] }, \"transactionId\": \"transactionId\" } ]" ;

    public static final String UNAUTH = "[ { \"status\": 401, \"messageCode\": \"string\", \"message\": \"Success\", \"errorMap\": { \"masterErrorList\": [ { \"errorCode\": \"string\", \"errorMsg\": \"string\" } ], \"rowErrorMsgLists\": [ { \"rowValue\": \"string\", \"rowName\": \"string\", \"masterErrorList\": [ { \"errorCode\": \"string\", \"errorMsg\": \"string\" } ], \"rowErrorMsgList\": [ {} ] } ] }, \"transactionId\": \"transactionId\" } ]" ;

}
