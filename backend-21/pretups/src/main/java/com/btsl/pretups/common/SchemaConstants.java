package com.btsl.pretups.common;

import java.io.Serializable;
import java.lang.annotation.Native;

public class SchemaConstants implements Serializable {
    //Patterns
    public static final String STRING_INPUT_PATTERN = "^[a-zA-Z0-9]+$";
    public static final String STATUS_INPUT_PATTERN = "^[A-Z]$";

    //Description
    public static final String STATUS_DESC = "Success: 200, Bad Request: 400, Unauthorized: 401, Not Found: 400";
    public static final String ERROR_CODE_DESC = "Error Code";
    public static final String ERROR_MSG_DESC = "Error Message";
    //Example
    public static final String STATUS_EXAMPLE = "200";
    public static final String MESSAGE_EXAMPLE = "Success";
    public static final String TRANSACTION_ID_EXAMPLE = "R0120.12.123322";
    public static final String ERROR_CODE_EXAMPLE = "9010";
    public static final String ERROR_MSG_EXAMPLE = "File not found.";

    //Maximum
    public static final int ARRAY_MAX_SIZE = 1000;
    public static final int STRING_MAX_SIZE = 50;
    public static final int FILE_ATTACHMENT_MAX_SIZE = 100000;
}
