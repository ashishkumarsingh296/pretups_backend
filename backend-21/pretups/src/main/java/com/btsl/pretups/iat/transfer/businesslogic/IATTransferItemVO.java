/*
 * Created on Jul 9, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.iat.transfer.businesslogic;

import java.util.Date;
import java.util.Locale;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class IATTransferItemVO {

    private String _iatSenderTxnId;
    private String _iatRecNWCode;
    private String _iatRecMsisdn;
    private String _iatNotifyMsisdn;
    private String _iatFailedAt;
    private double _iatExchangeRate;
    private double _iatProvRatio;
    private double _iatReceiverSystemBonus;
    private Date _iatTimestamp;
    private String _iatTimestampString;
    private String _iatCreditMessage;
    private String _iatCreditRespCode;
    private String _iatCheckStatusRespCode;
    private String _iatRcvrNWErrorCode;
    private String _iatRcvrNWErrorMessage;
    private String _iatErrorMessage;
    private String _iatErrorCode;
    private double _iatFees;
    private double _iatRcvrRcvdAmt;// final topup value at Receiver IN
    private String _iatTxnId;
    private int _iatRcvrCountryCode;
    private String _iatRcvrCntryIATStatus;
    private int _iatRcvrPrfxLength;
    private String _iatRcvrPrfx;
    private String _iatRecCountryShortName;
    private String _iatCode;
    private String _iatHandlerClass;
    private int _iatType;
    private double _iatSentAmtByIAT;// iat to receiver zebra
                                    // R_PF_RECEIVED_AMOUNT
    private String _iatRcvrCurrency;
    private Locale _iatNotifyMsisdnLocale;
    private String _iatInterfaceID;
    private String _iatRcvrCountryName;
    private String _iatTxnStatus;
    private Date _sendingNWTimestamp;
    private String _transferStatus;
    private String _senderId;
    private String _serviceType;
    private String _iatInterfaceType;
    private String _reconId;
    private String _senderCountryCode;
    private String _txnStatusFromIAT;
    private String _errorCodeFromIAT;
    private String _errorMsgFromIAT;
    private int _bonusValidityDaysFromIAT;
    private long _transferValue;
    private long _quantity;
    private double _iatReceivedAmount; // sent_amount ..provided by iat )
                                       // IAT_RECEIVED_AMOUNT

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_iatSenderTxnId  =" + _iatSenderTxnId);
        sbf.append(",_iatRecNWCode  =" + _iatRecNWCode);
        sbf.append(",_iatRecMsisdn =" + _iatRecMsisdn);
        sbf.append("_iatNotifyMsisdn  =" + _iatNotifyMsisdn);
        sbf.append(",_iatFailedAt  =" + _iatFailedAt);
        sbf.append(",_iatExchangeRate =" + _iatExchangeRate);
        sbf.append("_iatProvRatio  =" + _iatProvRatio);
        sbf.append(",_iatReceiverSystemBonus  =" + _iatReceiverSystemBonus);
        sbf.append(",_iatTimestamp =" + _iatTimestamp);
        sbf.append("_iatTimestampString  =" + _iatTimestampString);
        sbf.append(",_iatCreditMessage  =" + _iatCreditMessage);
        sbf.append(",_iatCreditRespCode =" + _iatCreditRespCode);
        sbf.append("_iatCheckStatusRespCode  =" + _iatCheckStatusRespCode);
        sbf.append(",_iatRcvrNWErrorCode  =" + _iatRcvrNWErrorCode);
        sbf.append(",_iatRcvrNWErrorMessage =" + _iatRcvrNWErrorMessage);
        sbf.append("_iatErrorMessage  =" + _iatErrorMessage);
        sbf.append(",_iatErrorCode  =" + _iatErrorCode);
        sbf.append(",_iatFees =" + _iatFees);
        sbf.append("_iatRcvrRcvdAmt(final topup at IN)  =" + _iatRcvrRcvdAmt);
        sbf.append(",_iatTxnId  =" + _iatTxnId);
        sbf.append(",_iatRcvrCountryCode =" + _iatRcvrCountryCode);
        sbf.append("_iatRcvrCntryIATStatus  =" + _iatRcvrCntryIATStatus);
        sbf.append(",_iatRcvrPrfxLength  =" + _iatRcvrPrfxLength);
        sbf.append(",_iatRcvrPrfx =" + _iatRcvrPrfx);
        sbf.append("_iatRecCountryShortName  =" + _iatRecCountryShortName);
        sbf.append(",_iatCode  =" + _iatCode);
        sbf.append(",_iatHandlerClass =" + _iatHandlerClass);
        sbf.append("_iatType  =" + _iatType);
        sbf.append(",_iatSentAmtByIAT (iat to receiver zebra) =" + _iatSentAmtByIAT);
        sbf.append(",_iatRcvrCurrency =" + _iatRcvrCurrency);
        sbf.append("_iatNotifyMsisdnLocale  =" + _iatNotifyMsisdnLocale);
        sbf.append(",_iatInterfaceID  =" + _iatInterfaceID);
        sbf.append(",_iatRcvrCountryName =" + _iatRcvrCountryName);
        sbf.append("_iatTxnStatus  =" + _iatTxnStatus);
        sbf.append(",_sendingNWTimestamp  =" + _sendingNWTimestamp);
        sbf.append(",_transferStatus =" + _transferStatus);
        sbf.append("_senderId  =" + _senderId);
        sbf.append(",_serviceType  =" + _serviceType);
        sbf.append(",_iatInterfaceType =" + _iatInterfaceType);
        sbf.append("_reconId  =" + _reconId);
        sbf.append(",_senderCountryCode  =" + _senderCountryCode);
        sbf.append(",_txnStatusFromIAT =" + _txnStatusFromIAT);
        sbf.append("_errorCodeFromIAT  =" + _errorCodeFromIAT);
        sbf.append(",_errorMsgFromIAT  =" + _errorMsgFromIAT);
        sbf.append(",_bonusValidityDaysFromIAT  =" + _bonusValidityDaysFromIAT);
        sbf.append("_transferValue = " + _transferValue);
        sbf.append("_quantity = " + _quantity);
        sbf.append("_iatReceivedAmount (iat to sender zebra) = " + _iatReceivedAmount);

        return sbf.toString();
    }

    /**
     * @return Returns the iatCheckStatusRespCode.
     */
    public String getIatCheckStatusRespCode() {
        return _iatCheckStatusRespCode;
    }

    /**
     * @param iatCheckStatusRespCode
     *            The iatCheckStatusRespCode to set.
     */
    public void setIatCheckStatusRespCode(String iatCheckStatusRespCode) {
        _iatCheckStatusRespCode = iatCheckStatusRespCode;
    }

    /**
     * @return Returns the iatCode.
     */
    public String getIatCode() {
        return _iatCode;
    }

    /**
     * @param iatCode
     *            The iatCode to set.
     */
    public void setIatCode(String iatCode) {
        _iatCode = iatCode;
    }

    /**
     * @return Returns the iatCreditMessage.
     */
    public String getIatCreditMessage() {
        return _iatCreditMessage;
    }

    /**
     * @param iatCreditMessage
     *            The iatCreditMessage to set.
     */
    public void setIatCreditMessage(String iatCreditMessage) {
        _iatCreditMessage = iatCreditMessage;
    }

    /**
     * @return Returns the iatCreditRespCode.
     */
    public String getIatCreditRespCode() {
        return _iatCreditRespCode;
    }

    /**
     * @param iatCreditRespCode
     *            The iatCreditRespCode to set.
     */
    public void setIatCreditRespCode(String iatCreditRespCode) {
        _iatCreditRespCode = iatCreditRespCode;
    }

    /**
     * @return Returns the iatErrorCode.
     */
    public String getIatErrorCode() {
        return _iatErrorCode;
    }

    /**
     * @param iatErrorCode
     *            The iatErrorCode to set.
     */
    public void setIatErrorCode(String iatErrorCode) {
        _iatErrorCode = iatErrorCode;
    }

    /**
     * @return Returns the iatErrorMessage.
     */
    public String getIatErrorMessage() {
        return _iatErrorMessage;
    }

    /**
     * @param iatErrorMessage
     *            The iatErrorMessage to set.
     */
    public void setIatErrorMessage(String iatErrorMessage) {
        _iatErrorMessage = iatErrorMessage;
    }

    /**
     * @return Returns the iatExchangeRate.
     */
    public double getIatExchangeRate() {
        return _iatExchangeRate;
    }

    /**
     * @param iatExchangeRate
     *            The iatExchangeRate to set.
     */
    public void setIatExchangeRate(double iatExchangeRate) {
        _iatExchangeRate = iatExchangeRate;
    }

    /**
     * @return Returns the iatFailedAt.
     */
    public String getIatFailedAt() {
        return _iatFailedAt;
    }

    /**
     * @param iatFailedAt
     *            The iatFailedAt to set.
     */
    public void setIatFailedAt(String iatFailedAt) {
        _iatFailedAt = iatFailedAt;
    }

    /**
     * @return Returns the iatFees.
     */
    public double getIatFees() {
        return _iatFees;
    }

    /**
     * @param iatFees
     *            The iatFees to set.
     */
    public void setIatFees(double iatFees) {
        _iatFees = iatFees;
    }

    /**
     * @return Returns the iatHandlerClass.
     */
    public String getIatHandlerClass() {
        return _iatHandlerClass;
    }

    /**
     * @param iatHandlerClass
     *            The iatHandlerClass to set.
     */
    public void setIatHandlerClass(String iatHandlerClass) {
        _iatHandlerClass = iatHandlerClass;
    }

    /**
     * @return Returns the iatNotifyMsisdn.
     */
    public String getIatNotifyMsisdn() {
        return _iatNotifyMsisdn;
    }

    /**
     * @param iatNotifyMsisdn
     *            The iatNotifyMsisdn to set.
     */
    public void setIatNotifyMsisdn(String iatNotifyMsisdn) {
        _iatNotifyMsisdn = iatNotifyMsisdn;
    }

    /**
     * @return Returns the iatNotifyMsisdnLocale.
     */
    public Locale getIatNotifyMsisdnLocale() {
        return _iatNotifyMsisdnLocale;
    }

    /**
     * @param iatNotifyMsisdnLocale
     *            The iatNotifyMsisdnLocale to set.
     */
    public void setIatNotifyMsisdnLocale(Locale iatNotifyMsisdnLocale) {
        _iatNotifyMsisdnLocale = iatNotifyMsisdnLocale;
    }

    /**
     * @return Returns the iatProvRatio.
     */
    public double getIatProvRatio() {
        return _iatProvRatio;
    }

    /**
     * @param iatProvRatio
     *            The iatProvRatio to set.
     */
    public void setIatProvRatio(double iatProvRatio) {
        _iatProvRatio = iatProvRatio;
    }

    /**
     * @return Returns the iatRcvrCntryIATStatus.
     */
    public String getIatRcvrCntryIATStatus() {
        return _iatRcvrCntryIATStatus;
    }

    /**
     * @param iatRcvrCntryIATStatus
     *            The iatRcvrCntryIATStatus to set.
     */
    public void setIatRcvrCntryIATStatus(String iatRcvrCntryIATStatus) {
        _iatRcvrCntryIATStatus = iatRcvrCntryIATStatus;
    }

    /**
     * @return Returns the iatRcvrCountryCode.
     */
    public int getIatRcvrCountryCode() {
        return _iatRcvrCountryCode;
    }

    /**
     * @param iatRcvrCountryCode
     *            The iatRcvrCountryCode to set.
     */
    public void setIatRcvrCountryCode(int iatRcvrCountryCode) {
        _iatRcvrCountryCode = iatRcvrCountryCode;
    }

    /**
     * @return Returns the iatRcvrCurrency.
     */
    public String getIatRcvrCurrency() {
        return _iatRcvrCurrency;
    }

    /**
     * @param iatRcvrCurrency
     *            The iatRcvrCurrency to set.
     */
    public void setIatRcvrCurrency(String iatRcvrCurrency) {
        _iatRcvrCurrency = iatRcvrCurrency;
    }

    /**
     * @return Returns the iatRcvrNWErrorCode.
     */
    public String getIatRcvrNWErrorCode() {
        return _iatRcvrNWErrorCode;
    }

    /**
     * @param iatRcvrNWErrorCode
     *            The iatRcvrNWErrorCode to set.
     */
    public void setIatRcvrNWErrorCode(String iatRcvrNWErrorCode) {
        _iatRcvrNWErrorCode = iatRcvrNWErrorCode;
    }

    /**
     * @return Returns the iatRcvrNWErrorMessage.
     */
    public String getIatRcvrNWErrorMessage() {
        return _iatRcvrNWErrorMessage;
    }

    /**
     * @param iatRcvrNWErrorMessage
     *            The iatRcvrNWErrorMessage to set.
     */
    public void setIatRcvrNWErrorMessage(String iatRcvrNWErrorMessage) {
        _iatRcvrNWErrorMessage = iatRcvrNWErrorMessage;
    }

    /**
     * @return Returns the iatRcvrPrfx.
     */
    public String getIatRcvrPrfx() {
        return _iatRcvrPrfx;
    }

    /**
     * @param iatRcvrPrfx
     *            The iatRcvrPrfx to set.
     */
    public void setIatRcvrPrfx(String iatRcvrPrfx) {
        _iatRcvrPrfx = iatRcvrPrfx;
    }

    /**
     * @return Returns the iatRcvrPrfxLength.
     */
    public int getIatRcvrPrfxLength() {
        return _iatRcvrPrfxLength;
    }

    /**
     * @param iatRcvrPrfxLength
     *            The iatRcvrPrfxLength to set.
     */
    public void setIatRcvrPrfxLength(int iatRcvrPrfxLength) {
        _iatRcvrPrfxLength = iatRcvrPrfxLength;
    }

    /**
     * @return Returns the iatRcvrRcvdAmt.
     */
    public double getIatRcvrRcvdAmt() {
        return _iatRcvrRcvdAmt;
    }

    /**
     * @param iatRcvrRcvdAmt
     *            The iatRcvrRcvdAmt to set.
     */
    public void setIatRcvrRcvdAmt(double iatRcvrRcvdAmt) {
        _iatRcvrRcvdAmt = iatRcvrRcvdAmt;
    }

    /**
     * @return Returns the iatRecCountryShortName.
     */
    public String getIatRecCountryShortName() {
        return _iatRecCountryShortName;
    }

    /**
     * @param iatRecCountryShortName
     *            The iatRecCountryShortName to set.
     */
    public void setIatRecCountryShortName(String iatRecCountryShortName) {
        _iatRecCountryShortName = iatRecCountryShortName;
    }

    /**
     * @return Returns the iatReceiverSystemBonus.
     */
    public double getIatReceiverSystemBonus() {
        return _iatReceiverSystemBonus;
    }

    /**
     * @param iatReceiverSystemBonus
     *            The iatReceiverSystemBonus to set.
     */
    public void setIatReceiverSystemBonus(double iatReceiverSystemBonus) {
        _iatReceiverSystemBonus = iatReceiverSystemBonus;
    }

    /**
     * @return Returns the iatRecMsisdn.
     */
    public String getIatRecMsisdn() {
        return _iatRecMsisdn;
    }

    /**
     * @param iatRecMsisdn
     *            The iatRecMsisdn to set.
     */
    public void setIatRecMsisdn(String iatRecMsisdn) {
        _iatRecMsisdn = iatRecMsisdn;
    }

    /**
     * @return Returns the iatRecNWCode.
     */
    public String getIatRecNWCode() {
        return _iatRecNWCode;
    }

    /**
     * @param iatRecNWCode
     *            The iatRecNWCode to set.
     */
    public void setIatRecNWCode(String iatRecNWCode) {
        _iatRecNWCode = iatRecNWCode;
    }

    /**
     * @return Returns the iatSenderTxnId.
     */
    public String getIatSenderTxnId() {
        return _iatSenderTxnId;
    }

    /**
     * @param iatSenderTxnId
     *            The iatSenderTxnId to set.
     */
    public void setIatSenderTxnId(String iatSenderTxnId) {
        _iatSenderTxnId = iatSenderTxnId;
    }

    /**
     * @return Returns the iatSentAmtByIAT.
     */
    public double getIatSentAmtByIAT() {
        return _iatSentAmtByIAT;
    }

    /**
     * @param iatSentAmtByIAT
     *            The iatSentAmtByIAT to set.
     */
    public void setIatSentAmtByIAT(double iatSentAmtByIAT) {
        _iatSentAmtByIAT = iatSentAmtByIAT;
    }

    /**
     * @return Returns the iatTimestamp.
     */
    public Date getIatTimestamp() {
        return _iatTimestamp;
    }

    /**
     * @param iatTimestamp
     *            The iatTimestamp to set.
     */
    public void setIatTimestamp(Date iatTimestamp) {
        _iatTimestamp = iatTimestamp;
    }

    /**
     * @return Returns the iatTxnId.
     */
    public String getIatTxnId() {
        return _iatTxnId;
    }

    /**
     * @param iatTxnId
     *            The iatTxnId to set.
     */
    public void setIatTxnId(String iatTxnId) {
        _iatTxnId = iatTxnId;
    }

    /**
     * @return Returns the iatType.
     */
    public int getIatType() {
        return _iatType;
    }

    /**
     * @param iatType
     *            The iatType to set.
     */
    public void setIatType(int iatType) {
        _iatType = iatType;
    }

    public String getIatInterfaceID() {
        return _iatInterfaceID;
    }

    public void setIatInterfaceID(String iatInterfaceID) {
        _iatInterfaceID = iatInterfaceID;
    }

    public String getIatRcvrCountryName() {
        return _iatRcvrCountryName;
    }

    public void setIatRcvrCountryName(String iatRcvrCountryName) {
        _iatRcvrCountryName = iatRcvrCountryName;
    }

    public String getIatTxnStatus() {
        return _iatTxnStatus;
    }

    public void setIatTxnStatus(String iatTxnStatus) {
        _iatTxnStatus = iatTxnStatus;
    }

    /**
     * @return Returns the sendingNWTimestamp.
     */
    public Date getSendingNWTimestamp() {
        return _sendingNWTimestamp;
    }

    /**
     * @param sendingNWTimestamp
     *            The sendingNWTimestamp to set.
     */
    public void setSendingNWTimestamp(Date sendingNWTimestamp) {
        _sendingNWTimestamp = sendingNWTimestamp;
    }

    /**
     * @return Returns the transferStatus.
     */
    public String getTransferStatus() {
        return _transferStatus;
    }

    /**
     * @param transferStatus
     *            The transferStatus to set.
     */
    public void setTransferStatus(String transferStatus) {
        _transferStatus = transferStatus;
    }

    /**
     * @return Returns the senderId.
     */
    public String getSenderId() {
        return _senderId;
    }

    /**
     * @param senderId
     *            The senderId to set.
     */
    public void setSenderId(String senderId) {
        _senderId = senderId;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the iatInterfaceType.
     */
    public String getIatInterfaceType() {
        return _iatInterfaceType;
    }

    /**
     * @param iatInterfaceType
     *            The iatInterfaceType to set.
     */
    public void setIatInterfaceType(String iatInterfaceType) {
        _iatInterfaceType = iatInterfaceType;
    }

    /**
     * @return Returns the reconId.
     */
    public String getReconId() {
        return _reconId;
    }

    /**
     * @param reconId
     *            The reconId to set.
     */
    public void setReconId(String reconId) {
        _reconId = reconId;
    }

    public String getIatTimestampString() {
        return _iatTimestampString;
    }

    public void setIatTimestampString(String iatTimestampString) {
        _iatTimestampString = iatTimestampString;
    }

    /**
     * @return Returns the senderCountryCode.
     */
    public String getSenderCountryCode() {
        return _senderCountryCode;
    }

    /**
     * @param senderCountryCode
     *            The senderCountryCode to set.
     */
    public void setSenderCountryCode(String senderCountryCode) {
        _senderCountryCode = senderCountryCode;
    }

    public String getTxnStatusFromIAT() {
        return _txnStatusFromIAT;
    }

    public void setTxnStatusFromIAT(String txnStatusFromIAT) {
        _txnStatusFromIAT = txnStatusFromIAT;
    }

    public String getErrorCodeFromIAT() {
        return _errorCodeFromIAT;
    }

    public void setErrorCodeFromIAT(String errorCodeFromIAT) {
        _errorCodeFromIAT = errorCodeFromIAT;
    }

    public String getErrorMsgFromIAT() {
        return _errorMsgFromIAT;
    }

    public void setErrorMsgFromIAT(String errorMsgFromIAT) {
        _errorMsgFromIAT = errorMsgFromIAT;
    }

    public int getBonusValidityDaysFromIAT() {
        return _bonusValidityDaysFromIAT;
    }

    public void setBonusValidityDaysFromIAT(int bonusValidityDaysFromIAT) {
        _bonusValidityDaysFromIAT = bonusValidityDaysFromIAT;
    }

    /**
     * @return Returns the quantity.
     */
    public long getQuantity() {
        return _quantity;
    }

    /**
     * @param quantity
     *            The quantity to set.
     */
    public void setQuantity(long quantity) {
        _quantity = quantity;
    }

    /**
     * @return Returns the transferValue.
     */
    public long getTransferValue() {
        return _transferValue;
    }

    /**
     * @param transferValue
     *            The transferValue to set.
     */
    public void setTransferValue(long transferValue) {
        _transferValue = transferValue;
    }

    public double getIatReceivedAmount() {
        return _iatReceivedAmount;
    }

    public void setIatReceivedAmount(double iatReceivedAmount) {
        _iatReceivedAmount = iatReceivedAmount;
    }

}
