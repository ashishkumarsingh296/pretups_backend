/**
 * @(#)PinPasswordAlertVO.java
 *                             Name Date History
 *                             ------------------------------------------------
 *                             ------------------------
 *                             Ankit Singhal 07/03/2007 Initial Creation
 *                             ------------------------------------------------
 *                             ------------------------
 *                             Copyright (c) 2007 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.processes.businesslogic;

import java.sql.Date;
import java.util.Locale;

public class PinPasswordAlertVO {

    private String _msisdn;
    private Date _lastModifiedOn;
    private Locale _locale;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("_msisdn=" + _msisdn);
        sb.append("_lastModifiedOn=" + _lastModifiedOn);
        sb.append(",_locale=" + _locale);
        return sb.toString();
    }

    /**
     * @return Returns the locale.
     */
    public Locale getLocale() {
        return _locale;
    }

    /**
     * @param locale
     *            The locale to set.
     */
    public void setLocale(Locale locale) {
        _locale = locale;
    }

    /**
     * @return Returns the lastModifiedOn.
     */
    public Date getLastModifiedOn() {
        return _lastModifiedOn;
    }

    /**
     * @param lastModifiedOn
     *            The lastModifiedOn to set.
     */
    public void setLastModifiedOn(Date lastModifiedOn) {
        _lastModifiedOn = lastModifiedOn;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }
}
