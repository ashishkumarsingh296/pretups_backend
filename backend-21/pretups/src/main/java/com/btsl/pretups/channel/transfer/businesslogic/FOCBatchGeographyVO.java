/*
 * @# FOCBatchGeographyVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * June 22, 2006 Amit Ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class FOCBatchGeographyVO implements Serializable {
    private String _batchId = null;
    private String _geographyCode = null;
    // Added on 07/02/08 for addition of new date_time column in the table
    // FOC_BATCH_GEOGRAPHIES.
    private Date _dateTime = null;

    public String toString() {
        final StringBuffer sbf = new StringBuffer();
        sbf.append("_batchId =" + _batchId);
        sbf.append(",_geographyCode =" + _geographyCode);
        sbf.append(",_dateTime =" + _dateTime);
        return sbf.toString();
    }

    /**
     * @return Returns the batchId.
     */
    public String getBatchId() {
        return _batchId;
    }

    /**
     * @param batchId
     *            The batchId to set.
     */
    public void setBatchId(String batchId) {
        _batchId = batchId;
    }

    /**
     * @return Returns the geographyCode.
     */
    public String getGeographyCode() {
        return _geographyCode;
    }

    /**
     * @param geographyCode
     *            The geographyCode to set.
     */
    public void setGeographyCode(String geographyCode) {
        _geographyCode = geographyCode;
    }

    /**
     * @return Returns the dateTime.
     */
    public Date getDateTime() {
        return _dateTime;
    }

    /**
     * @param dateTime
     *            The dateTime to set.
     */
    public void setDateTime(Date dateTime) {
        _dateTime = dateTime;
    }
}
