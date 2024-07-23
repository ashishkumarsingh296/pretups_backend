/*
 * #ChangeLocaleVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Sep 21, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.subscriber.businesslogic;

import java.io.Serializable;

public class ChangeLocaleVO implements Serializable {
    private String _languageCode;
    private String _country;
    private String _languageName;
    private String _languageCountry;

    public String getLanguageCountry() {
        return _languageCode + "-" + _country;
    }

    /**
     * To get the value of country field
     * 
     * @return country.
     */
    public String getCountry() {
        return _country;
    }

    /**
     * To set the value of country field
     */
    public void setCountry(String country) {
        _country = country;
    }

    /**
     * To get the value of languageCode field
     * 
     * @return languageCode.
     */
    public String getLanguageCode() {
        return _languageCode;
    }

    /**
     * To set the value of languageCode field
     */
    public void setLanguageCode(String languageCode) {
        _languageCode = languageCode;
    }

    /**
     * To get the value of languageName field
     * 
     * @return languageName.
     */
    public String getLanguageName() {
        return _languageName;
    }

    /**
     * To set the value of languageName field
     */
    public void setLanguageName(String languageName) {
        _languageName = languageName;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_country = " + _country);
        sbf.append(",_languageCode = " + _languageCode);
        sbf.append(",_languageCountry = " + _languageCountry);
        sbf.append(",_languageName = " + _languageName);
        // sbf.append(", = "+ _);

        return sbf.toString();
    }
}
