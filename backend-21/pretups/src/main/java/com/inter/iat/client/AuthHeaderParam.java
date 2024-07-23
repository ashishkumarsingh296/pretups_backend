package com.inter.iat.client;

/**
 * @(#) AuthHeaderParam.java
 *      Copyright(c) 2011, Comviva Technologies Ltd.
 *      All Rights Reserved
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Created By Created On History
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Babu Kunwar 02-DEC-2011 Initial Creation
 *      ------------------------------------------------------------------------
 *      -------------------------
 */

public class AuthHeaderParam {

    protected String userName;
    protected String password;

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *         possible object is {@link String }
     * 
     */

    public String toString() {
        StringBuffer sbfObj = new StringBuffer();
        sbfObj.append("userName::=" + userName);
        sbfObj.append("password::=" + password);

        return sbfObj.toString();
    }

    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *         possible object is {@link String }
     * 
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPassword(String value) {
        this.password = value;
    }

}
