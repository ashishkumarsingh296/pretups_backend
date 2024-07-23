/*
 * IntervalTimeVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Ruwali 24/05/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * This class is used to generate interval time VO.
 */
package com.btsl.pretups.processes.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;

public class IntervalTimeVO implements Serializable {
    private Timestamp _startTime;
    private Timestamp _endTime;
    private static final long serialVersionUID = 1L;
    @Override
    public String toString() {
        StringBuilder sbf = new StringBuilder();
        sbf.append("_startTime  =" + _startTime);
        sbf.append(",_endTime =" + _endTime);
        return sbf.toString();
    }

    /**
     * @return Returns the endTime.
     */
    public Timestamp getEndTime() {
        return _endTime;
    }

    /**
     * @param endTime
     *            The endTime to set.
     */
    public void setEndTime(Timestamp endTime) {
        _endTime = endTime;
    }

    /**
     * @return Returns the startTime.
     */
    public Timestamp getStartTime() {
        return _startTime;
    }

    /**
     * @param startTime
     *            The startTime to set.
     */
    public void setStartTime(Timestamp startTime) {
        _startTime = startTime;
    }
}
