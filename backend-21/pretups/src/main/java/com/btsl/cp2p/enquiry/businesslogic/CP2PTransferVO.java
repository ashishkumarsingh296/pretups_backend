package com.btsl.cp2p.enquiry.businesslogic;

import java.io.Serializable;

public class CP2PTransferVO implements Serializable {
    private String _receiverMsisdn;
    private String _service;
    private int _totalCount;
    private long _totalTransfer;
    private double _senderTotalTax;
    private double _senderTotalDebitAmount;
    private double _senderTotalAccessFees;
    private double _receiverTotalTax;
    private double _receiverTotalCreditAmount;
    private double _receiverTotalAccessFees;
    private double _totalBonusAmount;

    /**
     * @return Returns the _receiverMsisdn.
     */
    public String getReceiverMsisdn() {
        return _receiverMsisdn;
    }

    /**
     * @param msisdn
     *            The _receiverMsisdn to set.
     */
    public void setReceiverMsisdn(String msisdn) {
        _receiverMsisdn = msisdn;
    }

    /**
     * @return Returns the _receiverTotalAccessFees.
     */
    public double getReceiverTotalAccessFees() {
        return _receiverTotalAccessFees;
    }

    /**
     * @param totalAccessFees
     *            The _receiverTotalAccessFees to set.
     */
    public void setReceiverTotalAccessFees(double totalAccessFees) {
        _receiverTotalAccessFees = totalAccessFees;
    }

    /**
     * @return Returns the _receiverTotalCreditAmount.
     */
    public double getReceiverTotalCreditAmount() {
        return _receiverTotalCreditAmount;
    }

    /**
     * @param totalCreditAmount
     *            The _receiverTotalCreditAmount to set.
     */
    public void setReceiverTotalCreditAmount(double totalCreditAmount) {
        _receiverTotalCreditAmount = totalCreditAmount;
    }

    /**
     * @return Returns the _senderTotalAccessFees.
     */
    public double getSenderTotalAccessFees() {
        return _senderTotalAccessFees;
    }

    /**
     * @param totalAccessFees
     *            The _senderTotalAccessFees to set.
     */
    public void setSenderTotalAccessFees(double totalAccessFees) {
        _senderTotalAccessFees = totalAccessFees;
    }

    /**
     * @return Returns the _senderTotalDebitAmount.
     */
    public double getSenderTotalDebitAmount() {
        return _senderTotalDebitAmount;
    }

    /**
     * @param totalDebitAmount
     *            The _senderTotalDebitAmount to set.
     */
    public void setSenderTotalDebitAmount(double totalDebitAmount) {
        _senderTotalDebitAmount = totalDebitAmount;
    }

    /**
     * @return Returns the _service.
     */
    public String getService() {
        return _service;
    }

    /**
     * @param _service
     *            The _service to set.
     */
    public void setService(String _service) {
        this._service = _service;
    }

    /**
     * @return Returns the _totalBonusAmount.
     */
    public double getTotalBonusAmount() {
        return _totalBonusAmount;
    }

    /**
     * @param bonusAmount
     *            The _totalBonusAmount to set.
     */
    public void setTotalBonusAmount(double bonusAmount) {
        _totalBonusAmount = bonusAmount;
    }

    /**
     * @return Returns the _totalTransfer.
     */
    public long getTotalTransfer() {
        return _totalTransfer;
    }

    /**
     * @param transfer
     *            The _totalTransfer to set.
     */
    public void setTotalTransfer(long transfer) {
        _totalTransfer = transfer;
    }

    /**
     * @return Returns the _receiverTotalTax.
     */
    public double getReceiverTotalTax() {
        return _receiverTotalTax;
    }

    /**
     * @param totalTax
     *            The _receiverTotalTax to set.
     */
    public void setReceiverTotalTax(double totalTax) {
        _receiverTotalTax = totalTax;
    }

    /**
     * @return Returns the _senderTotalTax.
     */
    public double getSenderTotalTax() {
        return _senderTotalTax;
    }

    /**
     * @param totalTax
     *            The _senderTotalTax to set.
     */
    public void setSenderTotalTax(double totalTax) {
        _senderTotalTax = totalTax;
    }

    /**
     * @return Returns the _totalCount.
     */
    public int getTotalCount() {
        return _totalCount;
    }

    /**
     * @param count
     *            The _totalCount to set.
     */
    public void setTotalCount(int count) {
        _totalCount = count;
    }
}
