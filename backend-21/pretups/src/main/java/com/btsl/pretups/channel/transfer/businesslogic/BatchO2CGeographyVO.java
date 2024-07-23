package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class BatchO2CGeographyVO implements Serializable {
   
	private static final long serialVersionUID = 1L;
	private String _batchId = null;
    private String _geographyCode = null;
    private Date _dateTime = null;
@Override
    public String toString() {
        final StringBuilder sbf = new StringBuilder();
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
