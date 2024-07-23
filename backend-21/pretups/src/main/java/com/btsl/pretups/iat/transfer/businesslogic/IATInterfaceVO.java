/*
 * Created on Jul 9, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.iat.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class IATInterfaceVO implements Serializable {
    private String _iatAction; // validate, credit
    private String _iatServiceType; // RR, IR
    private String _iatReceiverMSISDN;
    private int _iatReceiverCountryCode;
    private String _iatReceiverCountryShortName; // IND
    private String _iatInterfaceId;// INTID00001
    private String _iatSenderNWTRXID; // SENDER ZEBRA TXNID
    private String _iatSenderNWID; // MO FOR mOBINIL
    private String _iatRcvrNWID; // MO FOR mOBINIL
    private String _iatSenderNWTYPE; // OPERATOR, BANK, atm
    private int _iatSenderCountryCode;
    private int _iatType; // ///////////// C2S=1,P2P=2
    private String _iatRetailerMsisdn;
    private String _iatRetailerID;
    private String _iatDeviceID; // atm,pos
    private Date _iatSendingNWTimestamp; // sender zebra date time
    private String _option1;
    private String _option2;
    private String _option3;
    private String _iatTRXID; // rcvd from IAT
    private Date _iatTimeStamp; // rcvd from IAT
    private String _iatINTransactionStatus; // 200,250,205
    private String _iatResponseCodeVal; // validation response from IAT (0 OR
                                        // ERROR CODES)
    private String _iatResponseCodeCredit; // CREDIT RESPONSE CODE. status filed
                                           // of credit response
    private String _iatResponseMsgCredit; // CREDIT RESP MSG. message field of
                                          // credit response
    private String _iatResponseCodeChkStatus; // CKECK STATUS RESPONSE CODE.
                                              // THIS IS FINAL STATUS OF TXN.
                                              // STATUS FILED OF CHK STATUS RESP

    private String _iatFailedAt;
    private String _iatReasonCode;
    private String _iatReasonMessage;
    private String _receiverNWReasonCode;
    private String _receiverNWReasonMessage;

    private double _iatFees;
    private double _iatProvRatio;
    private double _iatExchangeRate;
    private double _iatReceiverZebraBonus;

    private String _iatInterfaceHandlerClass;
    private String _iatModule;
    private String _iatUserType;
    private String _iatCardGrpSelector;

    private long _iatInterfaceAmt;// /////// amount sent by controller to IN
                                  // (INTERface_amount), our interface amount
    private long _iatRequestedAmount;// /////// amount sent by controller to IN
                                     // (REQUESTED_amount), //our requested amt
    private double _iatAmountSentToIAT;// sender zebra to iat by by pretups
                                       // after multiplying by interface multi
                                       // factor
    private double _iatReceivedAmount;// sent_amount ..provided by iat )
                                      // IAT_RECEIVED_AMOUNT
    private double _iatSentAmtByIAT;// iat to receiver zebra
                                    // R_PF_RECEIVED_AMOUNT
    private double _iatRcvrRcvdAmount; // //final topup amount
                                       // RECIPIENT_RECEIVED_AMOUNT, Amount
                                       // received by rcvr in rcvr currency and
                                       // country

    private String _iatSourceType; // WEB, USD, SMS
    private String _iatINAccessType;// CONTROLLER, PROCESS
    private String _iatGraceDays;
    private int _iatValidityDays;
    private String _iatCardGroupCode;
    private int _iatBonusValidityDays;
    private String _iatStartTime;
    private String _iatEndTime;
    private String _iatProtocolStatus;
    private String _iatUpdateStatus;
    private String _iatNotifyMSISDN;
    private String _iatGatewayCode;

    private String _iatServiceClass;
    private String _iatAccountStatus;
    private String _reconId;
    private String _senderId;
	private String _receiverCurrency;
	private static final long serialVersionUID = 1L;
@Override
    public String toString() {
        StringBuilder sbf = new StringBuilder();
        sbf.append("_iatAction  =" + _iatAction);
        sbf.append(",_iatServiceType  =" + _iatServiceType);
        sbf.append(",_iatReceiverMSISDN =" + _iatReceiverMSISDN);
        sbf.append(",_iatReceiverCountryCode =" + _iatReceiverCountryCode);
        sbf.append(",_iatReceiverCountryShortName =" + _iatReceiverCountryShortName);
        sbf.append(",_iatInterfaceId =" + _iatInterfaceId);
        sbf.append(",_iatSenderNWTRXID =" + _iatSenderNWTRXID);
        sbf.append(",_iatSenderNWID =" + _iatSenderNWID);
        sbf.append(",_iatRcvrNWID =" + _iatRcvrNWID);
        sbf.append(",_iatSenderNWTYPE =" + _iatSenderNWTYPE);
        sbf.append(",_iatSenderCountryCode =" + _iatSenderCountryCode);
        sbf.append(", _iatType =" + _iatType);
        sbf.append(",_iatRetailerMsisdn =" + _iatRetailerMsisdn);
        sbf.append(",_iatRetailerID =" + _iatRetailerID);
        sbf.append(",_iatDeviceID =" + _iatDeviceID);
        sbf.append(",_iatSendingNWTimestamp =" + _iatSendingNWTimestamp);
        sbf.append(",_iatTRXID =" + _iatTRXID);
        sbf.append(",_iatTimeStamp =" + _iatTimeStamp);
        sbf.append(",_iatINTransactionStatus =" + _iatINTransactionStatus);
        sbf.append(",_iatResponseCodeVal =" + _iatResponseCodeVal);
        sbf.append(",_iatResponseCodeCredit =" + _iatResponseCodeCredit);
        sbf.append(",_iatResponseMsgCredit =" + _iatResponseMsgCredit);
        sbf.append(",_iatResponseCodeChkStatus =" + _iatResponseCodeChkStatus);
        sbf.append(", _iatFailedAt =" + _iatFailedAt);
        sbf.append(",_iatReasonCode =" + _iatReasonCode);
        sbf.append(",_iatReasonMessage =" + _iatReasonMessage);
        sbf.append(",_receiverNWReasonCode =" + _receiverNWReasonCode);
        sbf.append(", _receiverNWReasonMessage =" + _receiverNWReasonMessage);
        sbf.append(",_iatFees =" + _iatFees);
        sbf.append(",_iatProvRatio =" + _iatProvRatio);
        sbf.append(",_iatExchangeRate =" + _iatExchangeRate);
        // added by vikas k for updation of card group 08/12/08
        sbf.append(", _iatInterfaceHandlerClass =" + _iatInterfaceHandlerClass);
        sbf.append(", _iatModule =" + _iatModule);
        sbf.append(", _iatCardGrpSelector =" + _iatCardGrpSelector);
        sbf.append(", _iatUserType =" + _iatUserType);
        sbf.append(", _iatInterfaceAmt =" + _iatInterfaceAmt);// our interface
                                                              // amount
        sbf.append(", _iatRequestedAmount =" + _iatRequestedAmount);// our
                                                                    // requested
                                                                    // amt
        sbf.append(", _iatAmountSentToIAT =" + _iatAmountSentToIAT);// sender
                                                                    // zebra to
                                                                    // iat by by
                                                                    // pretups
                                                                    // after
                                                                    // multiplying
                                                                    // by
                                                                    // interface
                                                                    // mul;t
                                                                    // factor
        sbf.append(", _iatReceivedAmount = " + _iatReceivedAmount);// sent_amount
                                                                   // ..provided
                                                                   // by iat )
                                                                   // IAT_RECEIVED_AMOUNT
        sbf.append(", _iatSentAmtByIAT =" + _iatSentAmtByIAT);// iat to receiver
                                                              // zebra
                                                              // R_PF_RECEIVED_AMOUNT
        sbf.append(", _iatRcvrRcvdAmount =" + _iatRcvrRcvdAmount);// final topup
                                                                  // amount
                                                                  // RECIPIENT_RECEIVED_AMOUNT
        sbf.append(", _iatReceiverZebraBonus =" + _iatReceiverZebraBonus);
        sbf.append(", _iatSourceType =" + _iatSourceType);
        sbf.append(", _iatINAccessType =" + _iatINAccessType);
        sbf.append(", _iatGraceDays =" + _iatGraceDays);
        sbf.append(", _iatUserType =" + _iatUserType);
        sbf.append(", _iatValidityDays =" + _iatValidityDays);
        sbf.append(", _iatCardGroupCode =" + _iatCardGroupCode);
        sbf.append(", _iatBonusValidityDays =" + _iatBonusValidityDays);
        sbf.append(", _iatStartTime =" + _iatStartTime);
        sbf.append(", _iatEndTime =" + _iatEndTime);
        sbf.append(", _iatProtocolStatus =" + _iatProtocolStatus);
        sbf.append(", _iatNotifyMSISDN =" + _iatNotifyMSISDN);
        sbf.append(", _iatGatewayCode =" + _iatGatewayCode);
        sbf.append(", _iatServiceClass =" + _iatServiceClass);
        sbf.append(", _iatAccountStatus =" + _iatAccountStatus);
        sbf.append(", _option1 =" + _option1);
        sbf.append(", _option2 =" + _option2);
        sbf.append(", _option3 =" + _option3);
	sbf.append(", _receiverCurrency =" + _receiverCurrency);
        return sbf.toString();
    }

    /**
     * @return Returns the iatAction.
     */
    public String getIatAction() {
        return _iatAction;
    }

    /**
     * @param iatAction
     *            The iatAction to set.
     */
    public void setIatAction(String iatAction) {
        _iatAction = iatAction;
    }

    /**
     * @return Returns the iatBonusValidityDays.
     */
    public int getIatBonusValidityDays() {
        return _iatBonusValidityDays;
    }

    /**
     * @param iatBonusValidityDays
     *            The iatBonusValidityDays to set.
     */
    public void setIatBonusValidityDays(int iatBonusValidityDays) {
        _iatBonusValidityDays = iatBonusValidityDays;
    }

    /**
     * @return Returns the iatCardGroupCode.
     */
    public String getIatCardGroupCode() {
        return _iatCardGroupCode;
    }

    /**
     * @param iatCardGroupCode
     *            The iatCardGroupCode to set.
     */
    public void setIatCardGroupCode(String iatCardGroupCode) {
        _iatCardGroupCode = iatCardGroupCode;
    }

    /**
     * @return Returns the iatCardGrpSelector.
     */
    public String getIatCardGrpSelector() {
        return _iatCardGrpSelector;
    }

    /**
     * @param iatCardGrpSelector
     *            The iatCardGrpSelector to set.
     */
    public void setIatCardGrpSelector(String iatCardGrpSelector) {
        _iatCardGrpSelector = iatCardGrpSelector;
    }

    /**
     * @return Returns the iatDeviceID.
     */
    public String getIatDeviceID() {
        return _iatDeviceID;
    }

    /**
     * @param iatDeviceID
     *            The iatDeviceID to set.
     */
    public void setIatDeviceID(String iatDeviceID) {
        _iatDeviceID = iatDeviceID;
    }

    /**
     * @return Returns the iatEndTime.
     */
    public String getIatEndTime() {
        return _iatEndTime;
    }

    /**
     * @param iatEndTime
     *            The iatEndTime to set.
     */
    public void setIatEndTime(String iatEndTime) {
        _iatEndTime = iatEndTime;
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
     * @return Returns the iatGatewayCode.
     */
    public String getIatGatewayCode() {
        return _iatGatewayCode;
    }

    /**
     * @param iatGatewayCode
     *            The iatGatewayCode to set.
     */
    public void setIatGatewayCode(String iatGatewayCode) {
        _iatGatewayCode = iatGatewayCode;
    }

    /**
     * @return Returns the iatGraceDays.
     */
    public String getIatGraceDays() {
        return _iatGraceDays;
    }

    /**
     * @param iatGraceDays
     *            The iatGraceDays to set.
     */
    public void setIatGraceDays(String iatGraceDays) {
        _iatGraceDays = iatGraceDays;
    }

    /**
     * @return Returns the iatINAccessType.
     */
    public String getIatINAccessType() {
        return _iatINAccessType;
    }

    /**
     * @param iatINAccessType
     *            The iatINAccessType to set.
     */
    public void setIatINAccessType(String iatINAccessType) {
        _iatINAccessType = iatINAccessType;
    }

    /**
     * @return Returns the iatInterfaceAmt.
     */
    public long getIatInterfaceAmt() {
        return _iatInterfaceAmt;
    }

    /**
     * @param iatInterfaceAmt
     *            The iatInterfaceAmt to set.
     */
    public void setIatInterfaceAmt(long iatInterfaceAmt) {
        _iatInterfaceAmt = iatInterfaceAmt;
    }

    /**
     * @return Returns the iatInterfaceHandlerClass.
     */
    public String getIatInterfaceHandlerClass() {
        return _iatInterfaceHandlerClass;
    }

    /**
     * @param iatInterfaceHandlerClass
     *            The iatInterfaceHandlerClass to set.
     */
    public void setIatInterfaceHandlerClass(String iatInterfaceHandlerClass) {
        _iatInterfaceHandlerClass = iatInterfaceHandlerClass;
    }

    /**
     * @return Returns the iatInterfaceId.
     */
    public String getIatInterfaceId() {
        return _iatInterfaceId;
    }

    /**
     * @param iatInterfaceId
     *            The iatInterfaceId to set.
     */
    public void setIatInterfaceId(String iatInterfaceId) {
        _iatInterfaceId = iatInterfaceId;
    }

    /**
     * @return Returns the iatINTransactionStatus.
     */
    public String getIatINTransactionStatus() {
        return _iatINTransactionStatus;
    }

    /**
     * @param iatINTransactionStatus
     *            The iatINTransactionStatus to set.
     */
    public void setIatINTransactionStatus(String iatINTransactionStatus) {
        _iatINTransactionStatus = iatINTransactionStatus;
    }

    /**
     * @return Returns the iatModule.
     */
    public String getIatModule() {
        return _iatModule;
    }

    /**
     * @param iatModule
     *            The iatModule to set.
     */
    public void setIatModule(String iatModule) {
        _iatModule = iatModule;
    }

    /**
     * @return Returns the iatNotifyMSISDN.
     */
    public String getIatNotifyMSISDN() {
        return _iatNotifyMSISDN;
    }

    /**
     * @param iatNotifyMSISDN
     *            The iatNotifyMSISDN to set.
     */
    public void setIatNotifyMSISDN(String iatNotifyMSISDN) {
        _iatNotifyMSISDN = iatNotifyMSISDN;
    }

    /**
     * @return Returns the iatProtocolStatus.
     */
    public String getIatProtocolStatus() {
        return _iatProtocolStatus;
    }

    /**
     * @param iatProtocolStatus
     *            The iatProtocolStatus to set.
     */
    public void setIatProtocolStatus(String iatProtocolStatus) {
        _iatProtocolStatus = iatProtocolStatus;
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
     * @return Returns the iatRcvrNWID.
     */
    public String getIatRcvrNWID() {
        return _iatRcvrNWID;
    }

    /**
     * @param iatRcvrNWID
     *            The iatRcvrNWID to set.
     */
    public void setIatRcvrNWID(String iatRcvrNWID) {
        _iatRcvrNWID = iatRcvrNWID;
    }

    /**
     * @return Returns the iatRcvrRcvdAmount.
     */
    public double getIatRcvrRcvdAmount() {
        return _iatRcvrRcvdAmount;
    }

    /**
     * @param iatRcvrRcvdAmount
     *            The iatRcvrRcvdAmount to set.
     */
    public void setIatRcvrRcvdAmount(double iatRcvrRcvdAmount) {
        _iatRcvrRcvdAmount = iatRcvrRcvdAmount;
    }

    /**
     * @return Returns the iatReasonCode.
     */
    public String getIatReasonCode() {
        return _iatReasonCode;
    }

    /**
     * @param iatReasonCode
     *            The iatReasonCode to set.
     */
    public void setIatReasonCode(String iatReasonCode) {
        _iatReasonCode = iatReasonCode;
    }

    /**
     * @return Returns the iatReasonMessage.
     */
    public String getIatReasonMessage() {
        return _iatReasonMessage;
    }

    /**
     * @param iatReasonMessage
     *            The iatReasonMessage to set.
     */
    public void setIatReasonMessage(String iatReasonMessage) {
        _iatReasonMessage = iatReasonMessage;
    }

    /**
     * @return Returns the iatReceiverCountryCode.
     */
    public int getIatReceiverCountryCode() {
        return _iatReceiverCountryCode;
    }

    /**
     * @param iatReceiverCountryCode
     *            The iatReceiverCountryCode to set.
     */
    public void setIatReceiverCountryCode(int iatReceiverCountryCode) {
        _iatReceiverCountryCode = iatReceiverCountryCode;
    }

    /**
     * @return Returns the iatReceiverCountryShortName.
     */
    public String getIatReceiverCountryShortName() {
        return _iatReceiverCountryShortName;
    }

    /**
     * @param iatReceiverCountryShortName
     *            The iatReceiverCountryShortName to set.
     */
    public void setIatReceiverCountryShortName(String iatReceiverCountryShortName) {
        _iatReceiverCountryShortName = iatReceiverCountryShortName;
    }

    /**
     * @return Returns the iatReceiverMSISDN.
     */
    public String getIatReceiverMSISDN() {
        return _iatReceiverMSISDN;
    }

    /**
     * @param iatReceiverMSISDN
     *            The iatReceiverMSISDN to set.
     */
    public void setIatReceiverMSISDN(String iatReceiverMSISDN) {
        _iatReceiverMSISDN = iatReceiverMSISDN;
    }

    /**
     * @return Returns the iatReceiverZebraBonus.
     */
    public double getIatReceiverZebraBonus() {
        return _iatReceiverZebraBonus;
    }

    /**
     * @param iatReceiverZebraBonus
     *            The iatReceiverZebraBonus to set.
     */
    public void setIatReceiverZebraBonus(double iatReceiverZebraBonus) {
        _iatReceiverZebraBonus = iatReceiverZebraBonus;
    }

    /**
     * @return Returns the iatRequestedAmount.
     */
    public long getIatRequestedAmount() {
        return _iatRequestedAmount;
    }

    /**
     * @param iatRequestedAmount
     *            The iatRequestedAmount to set.
     */
    public void setIatRequestedAmount(long iatRequestedAmount) {
        _iatRequestedAmount = iatRequestedAmount;
    }

    /**
     * @return Returns the iatResponseCodeChkStatus.
     */
    public String getIatResponseCodeChkStatus() {
        return _iatResponseCodeChkStatus;
    }

    /**
     * @param iatResponseCodeChkStatus
     *            The iatResponseCodeChkStatus to set.
     */
    public void setIatResponseCodeChkStatus(String iatResponseCodeChkStatus) {
        _iatResponseCodeChkStatus = iatResponseCodeChkStatus;
    }

    /**
     * @return Returns the iatResponseCodeCredit.
     */
    public String getIatResponseCodeCredit() {
        return _iatResponseCodeCredit;
    }

    /**
     * @param iatResponseCodeCredit
     *            The iatResponseCodeCredit to set.
     */
    public void setIatResponseCodeCredit(String iatResponseCodeCredit) {
        _iatResponseCodeCredit = iatResponseCodeCredit;
    }

    /**
     * @return Returns the iatResponseCodeVal.
     */
    public String getIatResponseCodeVal() {
        return _iatResponseCodeVal;
    }

    /**
     * @param iatResponseCodeVal
     *            The iatResponseCodeVal to set.
     */
    public void setIatResponseCodeVal(String iatResponseCodeVal) {
        _iatResponseCodeVal = iatResponseCodeVal;
    }

    /**
     * @return Returns the iatResponseMsgCredit.
     */
    public String getIatResponseMsgCredit() {
        return _iatResponseMsgCredit;
    }

    /**
     * @param iatResponseMsgCredit
     *            The iatResponseMsgCredit to set.
     */
    public void setIatResponseMsgCredit(String iatResponseMsgCredit) {
        _iatResponseMsgCredit = iatResponseMsgCredit;
    }

    /**
     * @return Returns the iatRetailerID.
     */
    public String getIatRetailerID() {
        return _iatRetailerID;
    }

    /**
     * @param iatRetailerID
     *            The iatRetailerID to set.
     */
    public void setIatRetailerID(String iatRetailerID) {
        _iatRetailerID = iatRetailerID;
    }

    /**
     * @return Returns the iatRetailerMsisdn.
     */
    public String getIatRetailerMsisdn() {
        return _iatRetailerMsisdn;
    }

    /**
     * @param iatRetailerMsisdn
     *            The iatRetailerMsisdn to set.
     */
    public void setIatRetailerMsisdn(String iatRetailerMsisdn) {
        _iatRetailerMsisdn = iatRetailerMsisdn;
    }

    /**
     * @return Returns the iatSenderCountryCode.
     */
    public int getIatSenderCountryCode() {
        return _iatSenderCountryCode;
    }

    /**
     * @param iatSenderCountryCode
     *            The iatSenderCountryCode to set.
     */
    public void setIatSenderCountryCode(int iatSenderCountryCode) {
        _iatSenderCountryCode = iatSenderCountryCode;
    }

    /**
     * @return Returns the iatSenderNWID.
     */
    public String getIatSenderNWID() {
        return _iatSenderNWID;
    }

    /**
     * @param iatSenderNWID
     *            The iatSenderNWID to set.
     */
    public void setIatSenderNWID(String iatSenderNWID) {
        _iatSenderNWID = iatSenderNWID;
    }

    /**
     * @return Returns the iatSenderNWTRXID.
     */
    public String getIatSenderNWTRXID() {
        return _iatSenderNWTRXID;
    }

    /**
     * @param iatSenderNWTRXID
     *            The iatSenderNWTRXID to set.
     */
    public void setIatSenderNWTRXID(String iatSenderNWTRXID) {
        _iatSenderNWTRXID = iatSenderNWTRXID;
    }

    /**
     * @return Returns the iatSenderNWTYPE.
     */
    public String getIatSenderNWTYPE() {
        return _iatSenderNWTYPE;
    }

    /**
     * @param iatSenderNWTYPE
     *            The iatSenderNWTYPE to set.
     */
    public void setIatSenderNWTYPE(String iatSenderNWTYPE) {
        _iatSenderNWTYPE = iatSenderNWTYPE;
    }

    /**
     * @return Returns the iatSendingNWTimestamp.
     */
    public Date getIatSendingNWTimestamp() {
        return _iatSendingNWTimestamp;
    }

    /**
     * @param iatSendingNWTimestamp
     *            The iatSendingNWTimestamp to set.
     */
    public void setIatSendingNWTimestamp(Date iatSendingNWTimestamp) {
        _iatSendingNWTimestamp = iatSendingNWTimestamp;
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
     * @return Returns the iatServiceType.
     */
    public String getIatServiceType() {
        return _iatServiceType;
    }

    /**
     * @param iatServiceType
     *            The iatServiceType to set.
     */
    public void setIatServiceType(String iatServiceType) {
        _iatServiceType = iatServiceType;
    }

    /**
     * @return Returns the iatSourceType.
     */
    public String getIatSourceType() {
        return _iatSourceType;
    }

    /**
     * @param iatSourceType
     *            The iatSourceType to set.
     */
    public void setIatSourceType(String iatSourceType) {
        _iatSourceType = iatSourceType;
    }

    /**
     * @return Returns the iatStartTime.
     */
    public String getIatStartTime() {
        return _iatStartTime;
    }

    /**
     * @param iatStartTime
     *            The iatStartTime to set.
     */
    public void setIatStartTime(String iatStartTime) {
        _iatStartTime = iatStartTime;
    }

    /**
     * @return Returns the iatTimeStamp.
     */
    public Date getIatTimeStamp() {
        return _iatTimeStamp;
    }

    /**
     * @param iatTimeStamp
     *            The iatTimeStamp to set.
     */
    public void setIatTimeStamp(Date iatTimeStamp) {
        _iatTimeStamp = iatTimeStamp;
    }

    /**
     * @return Returns the iatTRXID.
     */
    public String getIatTRXID() {
        return _iatTRXID;
    }

    /**
     * @param iatTRXID
     *            The iatTRXID to set.
     */
    public void setIatTRXID(String iatTRXID) {
        _iatTRXID = iatTRXID;
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

    /**
     * @return Returns the iatUpdateStatus.
     */
    public String getIatUpdateStatus() {
        return _iatUpdateStatus;
    }

    /**
     * @param iatUpdateStatus
     *            The iatUpdateStatus to set.
     */
    public void setIatUpdateStatus(String iatUpdateStatus) {
        _iatUpdateStatus = iatUpdateStatus;
    }

    /**
     * @return Returns the iatUserType.
     */
    public String getIatUserType() {
        return _iatUserType;
    }

    /**
     * @param iatUserType
     *            The iatUserType to set.
     */
    public void setIatUserType(String iatUserType) {
        _iatUserType = iatUserType;
    }

    /**
     * @return Returns the iatValidityDays.
     */
    public int getIatValidityDays() {
        return _iatValidityDays;
    }

    /**
     * @param iatValidityDays
     *            The iatValidityDays to set.
     */
    public void setIatValidityDays(int iatValidityDays) {
        _iatValidityDays = iatValidityDays;
    }

    /**
     * @return Returns the option1.
     */
    public String getOption1() {
        return _option1;
    }

    /**
     * @param option1
     *            The option1 to set.
     */
    public void setOption1(String option1) {
        _option1 = option1;
    }

    /**
     * @return Returns the option2.
     */
    public String getOption2() {
        return _option2;
    }

    /**
     * @param option2
     *            The option2 to set.
     */
    public void setOption2(String option2) {
        _option2 = option2;
    }

    /**
     * @return Returns the option3.
     */
    public String getOption3() {
        return _option3;
    }

    /**
     * @param option3
     *            The option3 to set.
     */
    public void setOption3(String option3) {
        _option3 = option3;
    }

    /**
     * @return Returns the receiverNWReasonCode.
     */
    public String getReceiverNWReasonCode() {
        return _receiverNWReasonCode;
    }

    /**
     * @param receiverNWReasonCode
     *            The receiverNWReasonCode to set.
     */
    public void setReceiverNWReasonCode(String receiverNWReasonCode) {
        _receiverNWReasonCode = receiverNWReasonCode;
    }

    /**
     * @return Returns the receiverNWReasonMessage.
     */
    public String getReceiverNWReasonMessage() {
        return _receiverNWReasonMessage;
    }

    /**
     * @param receiverNWReasonMessage
     *            The receiverNWReasonMessage to set.
     */
    public void setReceiverNWReasonMessage(String receiverNWReasonMessage) {
        _receiverNWReasonMessage = receiverNWReasonMessage;
    }

    /**
     * @return Returns the iatAccountStatus.
     */
    public String getIatAccountStatus() {
        return _iatAccountStatus;
    }

    /**
     * @param iatAccountStatus
     *            The iatAccountStatus to set.
     */
    public void setIatAccountStatus(String iatAccountStatus) {
        _iatAccountStatus = iatAccountStatus;
    }

    /**
     * @return Returns the iatServiceClass.
     */
    public String getIatServiceClass() {
        return _iatServiceClass;
    }

    /**
     * @param iatServiceClass
     *            The iatServiceClass to set.
     */
    public void setIatServiceClass(String iatServiceClass) {
        _iatServiceClass = iatServiceClass;
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

    public double getIatAmountSentToIAT() {
        return _iatAmountSentToIAT;
    }

    public void setIatAmountSentToIAT(double iatAmountSentToIAT) {
        _iatAmountSentToIAT = iatAmountSentToIAT;
    }

    public double getIatReceivedAmount() {
        return _iatReceivedAmount;
    }

    public void setIatReceivedAmount(double iatReceivedAmount) {
        _iatReceivedAmount = iatReceivedAmount;
    }
	public String getReceiverCurrency() {
		return _receiverCurrency;
	}
	public void setReceiverCurrency(String currency) {
		_receiverCurrency = currency;
	}
}
