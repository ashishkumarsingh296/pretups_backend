package com.btsl.common;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @(#)Networkform.java Copyright(c) 2000, Bharti Telesoft Ltd. All Rights
 *                      Reserved
 * 
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Mohit Goel 26/05/2005 Initial Creation
 * 
 *                      This is the Base Exception Class, we will always throw
 *                      BTSLBaseExcetion
 * @author ayush.abhijeet
 */
public class BTSLBaseException extends Exception {
    private String className;
    private String method;
    private Date date;
    private int errorCode;

    private transient BTSLMessages _btslMessages = null;

    public BTSLBaseException(String message) {
        super(message);
        _btslMessages = new BTSLMessages(message);
    }

    public BTSLBaseException(String message, boolean isKey) {
        super(message);
        _btslMessages = new BTSLMessages(message, isKey);
    }

    public BTSLBaseException(String message, String forwardPath) {
        super(message);
        _btslMessages = new BTSLMessages(message, forwardPath);
    }

    public BTSLBaseException(Object classObj, String method, List errorList, String forwardPath) {
        this(classObj.getClass().getName(), method);
        _btslMessages = new BTSLMessages(errorList, forwardPath);

    }

    public BTSLBaseException(Object classObj, String method, List errorList) {
        this(classObj.getClass().getName(), method);
        _btslMessages = new BTSLMessages(errorList);
    }

    public BTSLBaseException(Object classObj, String method, String message, String forwardPath) {
        this(classObj.getClass().getName(), method, message, forwardPath);

    }

    public BTSLBaseException(Object classObj, String method, String message, int errorCode, String[] args, String forwardPath) {
        this(classObj.getClass().getName(), method, message, errorCode, args, forwardPath);

    }

    public BTSLBaseException(Object classObj, String method, String message, int errorCode, String forwardPath) {
        this(classObj.getClass().getName(), method, message, errorCode, forwardPath);
    }

    public BTSLBaseException(Object classObj, String method, String message) {
        this(classObj.getClass().getName(), method, message);
    }
    public BTSLBaseException(Object classObj, String method, String message,Throwable cause ) {
        this(classObj.getClass().getName(), method, message , cause);
    }
    public BTSLBaseException(String className, String method, String message, String forwardPath) {
        super(message);
        _btslMessages = new BTSLMessages(message, forwardPath);
        this.className = className;
        this.method = method;
        date = new Date(System.currentTimeMillis());
    }

    public BTSLBaseException(String className, String method, String message,Throwable cause) {
        super(message,cause);
        _btslMessages = new BTSLMessages(message);
        this.className = className;
        this.method = method;
        date = new Date(System.currentTimeMillis());
    }
    public BTSLBaseException(String className, String method, String message) {
        super(message);
        _btslMessages = new BTSLMessages(message);
        this.className = className;
        this.method = method;
        date = new Date(System.currentTimeMillis());
    }

    public BTSLBaseException(String className, String method, String message, ArrayList errorList) {
        super(message);
        _btslMessages = new BTSLMessages(message, errorList);
        this.className = className;
        this.method = method;
        date = new Date(System.currentTimeMillis());
    }

    public BTSLBaseException(String className, String method, String message, int errorCode, String forwardPath) {
        super(message);
        _btslMessages = new BTSLMessages(message, forwardPath);
        this.className = className;
        this.method = method;
        this.errorCode = errorCode;
        date = new Date(System.currentTimeMillis());
    }

    public BTSLBaseException(String className, String method, String message, int errorCode, String[] args, String forwardPath) {
        super(message);
        _btslMessages = new BTSLMessages(message, args, forwardPath);
        this.className = className;
        this.method = method;
        this.errorCode = errorCode;
        date = new Date(System.currentTimeMillis());
    }

    public BTSLBaseException(Object className, String method, String message, String[] args) {
        super(message);
        _btslMessages = new BTSLMessages(message, args);
        this.className = className.getClass().getName();
        this.method = method;
        this.errorCode = 0;
        date = new Date(System.currentTimeMillis());
    }

    public BTSLBaseException(String message, String[] args) {
        super(message);

        date = new Date(System.currentTimeMillis());
        _btslMessages = new BTSLMessages(message, args);
    }

    public BTSLBaseException(String message, String[] args, String forwardPath) {
        super(message);

        date = new Date(System.currentTimeMillis());
        _btslMessages = new BTSLMessages(message, args, forwardPath);
    }

    public BTSLBaseException(Object classObj, String method, Map errorList) {
        this.className = classObj.getClass().getName();
        this.method = method;
        date = new Date(System.currentTimeMillis());
        _btslMessages = new BTSLMessages(errorList);
    }

    public BTSLBaseException(Object classObj, String method, Map errorList, String forwardPath) {
        this.className = classObj.getClass().getName();
        this.method = method;
        date = new Date(System.currentTimeMillis());
        _btslMessages = new BTSLMessages(errorList, forwardPath);
    }
    
    public BTSLBaseException(Throwable cause){
    	super(cause);
    }

    /**
     * @return
     */
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Class: " + className);
        sb.append("\n");
        sb.append("Method: " + method );
        sb.append("\n");
        sb.append("Date: " + date);
        sb.append("Error Code = " + errorCode);
        sb.append("_btslMessages: " + _btslMessages.print());
        sb.append("\n");
        return sb.toString();
    }
@Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }
@Override
    public void printStackTrace(PrintStream out) {
        
        try(PrintWriter pw=new PrintWriter(out);)
        {
	printStackTrace(pw);
        }
    }
@Override
    public void printStackTrace(PrintWriter out) {
        super.printStackTrace(out);
        out.flush();
    }
@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class=" + className);
        sb.append(",");
        sb.append("method=" + method );
        sb.append(",");
        sb.append("message=" + getMessage());
        sb.append(",");
        sb.append("date=" + date);
        sb.append("errorCode=" + errorCode);

        return sb.toString();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String[] getArgs() {
        return _btslMessages.getArgs();
    }

    public Map getMessageMap() {
        return _btslMessages.getMessageMap();
    }

    public List getMessageList() {
        return _btslMessages.getMessageList();
    }

    public String getMessageKey() {
        return _btslMessages.getMessageKey();
    }

    public String getForwardPath() {
        return _btslMessages.getForwardPath();
    }

    /**
     * @return Returns the btslMessages.
     */
    public BTSLMessages getBtslMessages() {
        return _btslMessages;
    }

    /**
     * @param btslMessages
     *            The btslMessages to set.
     */
    public void setBtslMessages(BTSLMessages btslMessages) {
        _btslMessages = btslMessages;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.btsl.common.BTSLMessages#isKey()
     */
    public boolean isKey() {

        return _btslMessages.isKey();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.btsl.common.BTSLMessages#setKey(boolean)
     */
    public void setKey(boolean isKey) {

        _btslMessages.setKey(isKey);
    }

}