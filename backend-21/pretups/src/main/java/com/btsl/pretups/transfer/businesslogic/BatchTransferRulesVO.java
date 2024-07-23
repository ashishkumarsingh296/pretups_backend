/**
 * @(#)BatchTransferRulesVO.java
 *                               Copyright(c) 2007, Bharti Telesoft Ltd.
 *                               All Rights Reserved
 * 
 *                               <description>
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               shishupal.singh Apr 16, 2005 Initital Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 * 
 */

package com.btsl.pretups.transfer.businesslogic;

/**
 * @author shishupal.singh
 */
public class BatchTransferRulesVO extends TransferRulesVO {
    private String _recordNumber;
    private String _batchName;

    public String toString() {
        String str = super.toString();
        str = str + ",_recordNumber=" + _recordNumber;
        str = str + ",_batchName=" + _batchName;
        return str;
    }

    /**
     * @return Returns the recordNumber.
     */
    public String getRecordNumber() {
        return _recordNumber;
    }

    /**
     * @param recordNumber
     *            The recordNumber to set.
     */
    public void setRecordNumber(String recordNumber) {
        _recordNumber = recordNumber;
    }

    /**
     * @return Returns the batchName.
     */
    public String getBatchName() {
        return _batchName;
    }

    /**
     * @param batchName
     *            The batchName to set.
     */
    public void setBatchName(String batchName) {
        _batchName = batchName;
    }
}
