package com.btsl.pretups.batch.businesslogic;

/*
 * @# BatchGeographyVO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Ved prakash July 21, 2006 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Bharti Telesoft Ltd.
 * This class use for batch user creation.
 */
import java.io.Serializable;

public class BatchGeographyVO implements Serializable {
    private String batchID = null;
    private String geographyCode = null;

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sbf = new StringBuilder();
        sbf.append("batchID =" + batchID);
        sbf.append(",geographyCode =" + geographyCode);
        return sbf.toString();
    }

    /**
     * @return Returns the batchId.
     */
    public String getBatchID() {
        return batchID;
    }

    /**
     * @param batchId
     *            The batchId to set.
     */
    public void setBatchID(String batchId) {
    	this.batchID = batchId;
    }

    /**
     * @return Returns the geographyCode.
     */
    public String getGeographyCode() {
        return geographyCode;
    }

    /**
     * @param geographyCode
     *            The geographyCode to set.
     */
    public void setGeographyCode(String geographyCode) {
    	this.geographyCode = geographyCode;
    }
}
