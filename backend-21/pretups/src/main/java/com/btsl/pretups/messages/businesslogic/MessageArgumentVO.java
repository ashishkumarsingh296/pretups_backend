/*
 * @# MessageArgumentVO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Chhaya Sikheria Sep 29, 2011 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2011 Comviva.
 */
package com.btsl.pretups.messages.businesslogic;

import java.io.Serializable;

public class MessageArgumentVO implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private String _messageCode;
    private String _argument;
    private String _argumentDesc;

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(" Message Code =" + _messageCode);
        sbf.append(",Argument =" + _argument);
        sbf.append(",Argument Description =" + _argumentDesc);

        return sbf.toString();
    }

    /**
     * @return the _messageCode
     */
    public String getMessageCode() {
        return _messageCode;
    }

    /**
     * @param code
     *            the _messageCode to set
     */
    public void setMessageCode(String code) {
        _messageCode = code;
    }

    /**
     * @return the _argument
     */
    public String getArgument() {
        return _argument;
    }

    /**
     * @param _argument
     *            the _argument to set
     */
    public void setArgument(String argument) {
        _argument = argument;
    }

    /**
     * @return the _argumentDesc
     */
    public String getArgumentDesc() {
        return _argumentDesc;
    }

    /**
     * @param desc
     *            the _argumentDesc to set
     */
    public void setArgumentDesc(String desc) {
        _argumentDesc = desc;
    }

    /**
     * @return construct the argument with description e.g {0}=Transaction ID
     */
    public String getArguments() {

        String str = null;

        str = "\n" + _argument + "=" + _argumentDesc + ",";

        return str;
    }
    
    public String getArgumentsWithBraces() {

        String str = null;

        str = "\n" + "{" +_argument +"}"+ "=" + _argumentDesc + ",";

        return str;
    }
}
