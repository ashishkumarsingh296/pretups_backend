package com.inter.iat.client;

/**
 * @(#) AuthHeader.java
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

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public class AuthHeader {

    protected AuthHeaderParam authHeaderParam;
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public AuthHeader() {

    }

    public String toString() {
        StringBuffer sbfObj = new StringBuffer();
        sbfObj.append("authHeaderParam::=" + authHeaderParam);

        return sbfObj.toString();
    }

    /**
     * Gets the value of the authHeaderParam property.
     * 
     * @return possible object is {@link AuthHeaderParam }
     */
    public AuthHeaderParam getAuthHeaderParam() {
        return authHeaderParam;
    }

    /**
     * Sets the value of the authHeaderParam property.
     * 
     * @param value
     *            allowed object is {@link AuthHeaderParam }
     */
    public void setAuthHeaderParam(AuthHeaderParam value) {
        this.authHeaderParam = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed
     * property on this class.
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    public AuthHeader(AuthHeaderParam authHeaderParam) {
        this.authHeaderParam = authHeaderParam;
    }

}
