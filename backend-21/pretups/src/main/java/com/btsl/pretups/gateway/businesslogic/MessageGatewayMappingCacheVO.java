/**
 * @(#)MessageGatewayMappingCacheVO.java
 *                                       Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                       All Rights Reserved
 * 
 *                                       <description>
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Author Date History
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       avinash.kamthan Jul 12, 2005 Initital
 *                                       Creation
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 * 
 */

package com.btsl.pretups.gateway.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.btsl.util.Constants;

/**
 * @author avinash.kamthan
 * 
 */
public class MessageGatewayMappingCacheVO implements Serializable {

    private String _requestCode;
    private String _responseCode;
    private String _alternateCode;
    private Date _modifiedOn;
    private Timestamp _modifiedOnTimestamp;

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getAlternateCode() {
        return _alternateCode;
    }

    public void setAlternateCode(String alternateCode) {
        _alternateCode = alternateCode;
    }

    public String getRequestCode() {
        return _requestCode;
    }

    public void setRequestCode(String requestCode) {
        _requestCode = requestCode;
    }

    public String getResponseCode() {
        return _responseCode;
    }

    public void setResponseCode(String responseCode) {
        _responseCode = responseCode;
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(100);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Request Code");
        sbf.append(middleSeperator);
        sbf.append(this.getRequestCode());

        sbf.append(startSeperator);
        sbf.append("Response Code");
        sbf.append(middleSeperator);
        sbf.append(this.getResponseCode());

        sbf.append(startSeperator);
        sbf.append("Alternate Code");
        sbf.append(middleSeperator);
        sbf.append(this.getAlternateCode());

        return sbf.toString();

    }

    public String differences(MessageGatewayMappingCacheVO cacheVO) {

        StringBuffer sbf = new StringBuffer(100);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!cacheVO.getRequestCode().equals(this.getRequestCode())) {
            sbf.append(startSeperator);
            sbf.append("Request Code");
            sbf.append(middleSeperator);
            sbf.append(cacheVO.getRequestCode());
            sbf.append(middleSeperator);
            sbf.append(this.getRequestCode());
        }

        if (!cacheVO.getResponseCode().equals(this.getResponseCode())) {
            sbf.append(startSeperator);
            sbf.append("Response Code");
            sbf.append(middleSeperator);
            sbf.append(cacheVO.getResponseCode());
            sbf.append(middleSeperator);
            sbf.append(this.getResponseCode());
        }

        if (!cacheVO.getAlternateCode().equals(this.getAlternateCode())) {
            sbf.append(startSeperator);
            sbf.append("Alternate Code");
            sbf.append(middleSeperator);
            sbf.append(cacheVO.getAlternateCode());
            sbf.append(middleSeperator);
            sbf.append(this.getAlternateCode());
        }

        return sbf.toString();
    }

    public Timestamp getModifiedOnTimestamp() {
        return _modifiedOnTimestamp;
    }

    public void setModifiedOnTimestamp(Timestamp modifiedOnTimestamp) {
        _modifiedOnTimestamp = modifiedOnTimestamp;
    }

    public boolean equalsMsgGatewayMappingCacheVO(MessageGatewayMappingCacheVO cacheVO) {
        boolean flag = false;

        if (this.getModifiedOnTimestamp().equals(cacheVO.getModifiedOnTimestamp())) {
            flag = true;
        }
        return flag;
    }

}
