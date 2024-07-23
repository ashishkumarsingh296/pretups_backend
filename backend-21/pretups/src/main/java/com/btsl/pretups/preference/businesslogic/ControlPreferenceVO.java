/**
 * @(#)ControlPreferenceVO.java
 *                              Copyright(c) 2005, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 * 
 *                              <description>
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              shishupal.singh Mar 09, 2007 Initital Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 * 
 */

package com.btsl.pretups.preference.businesslogic;

/**
 * @author shishupal.singh
 * 
 */
public class ControlPreferenceVO extends PreferenceCacheVO {

    private String _categoryCode;
    private String _categoryName;

    public String toString() {

        StringBuffer sbf = new StringBuffer();
        sbf.append("categoryCode=" + _categoryCode);
        sbf.append("categoryName=" + _categoryName);
        return sbf.toString();
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }
}
