package com.btsl.user.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


import lombok.Getter;
import lombok.Setter;

/**
 * Entity of MessageGatewayMappingCacheVO.
 *
 * @author Venkatesans
 */
@Setter
@Getter
public class MessageGatewayMappingCacheVO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String requestCode;
    private String responseCode;
    private String alternateCode;
    private Date modifiedOn;
    private Timestamp modifiedOnTimestamp;

    public String logInfo() {
        StringBuilder sbf = new StringBuilder(NumberConstants.N100.getIntValue());
        String startSeperator = com.btsl.util.Constants.getProperty("startSeperatpr");
        String middleSeperator = com.btsl.util.Constants.getProperty("middleSeperator");
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

        StringBuilder sbf = new StringBuilder(NumberConstants.N100.getIntValue());

        String startSeperator = com.btsl.util.Constants.getProperty("startSeperatpr");
        String middleSeperator = com.btsl.util.Constants.getProperty("middleSeperator");
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

    public boolean equalsMsgGatewayMappingCacheVO(MessageGatewayMappingCacheVO cacheVO) {
        boolean flag = false;
        if (this.getModifiedOnTimestamp().equals(cacheVO.getModifiedOnTimestamp())) {
            flag = true;
        }
        return flag;
    }

	public String getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(String requestCode) {
		this.requestCode = requestCode;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getAlternateCode() {
		return alternateCode;
	}

	public void setAlternateCode(String alternateCode) {
		this.alternateCode = alternateCode;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Timestamp getModifiedOnTimestamp() {
		return modifiedOnTimestamp;
	}

	public void setModifiedOnTimestamp(Timestamp modifiedOnTimestamp) {
		this.modifiedOnTimestamp = modifiedOnTimestamp;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
    
    

}
