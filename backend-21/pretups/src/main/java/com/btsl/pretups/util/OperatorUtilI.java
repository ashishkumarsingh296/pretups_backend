/**
 * @(#)OperatorUtilI.java
 *                        Copyright(c) 2005, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        <description>
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        avinash.kamthan Aug 5, 2005 Initital Creation
 *                        Gurjeet Singh Bedi Sep 23,2005 Modified
 *                        Abhijit Jul 21 2006 Modified By
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 * 
 */

package com.btsl.pretups.util;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.profile.businesslogic.ProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.p2p.transfer.businesslogic.MCDListVO;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.processes.businesslogic.ActivationBonusVO;
import com.btsl.pretups.processes.businesslogic.UserTransactionVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.restapi.networkadmin.networkStock.NetworkStockTxnVO1;

/**
 * @author avinash.kamthan
 * 
 */
public interface OperatorUtilI {
    /**
     * Method calculateTax1.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateTax1(String p_type, double p_rate, long p_productCost) throws BTSLBaseException;
    
   
//Code merging idea to 6.6
    public String generateC2SCommonTransferID(TransferVO p_transferVO) throws BTSLBaseException; 
    
    /**
     * Method calculateTax2.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateTax2(String p_type, double p_rate, long p_productCost) throws BTSLBaseException;
    
    

    /**
     * Method calculateTax3.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateTax3(String p_type, double p_rate, long p_productCost) throws BTSLBaseException;
    
    

    /**
     * Method calculateDiscount.
     * 
     * @param p_discountType
     *            String
     * @param p_discountRate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateDiscount(String p_discountType, double p_discountRate, long p_productCost) throws BTSLBaseException;
    
    

    /**
     * Method calculateCommission.
     * 
     * @param p_commissionType
     *            String
     * @param p_commissionRate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateCommission(String p_commissionType, double p_commissionRate, long p_productCost) throws BTSLBaseException;
    
    

    /**
     * Method calculatePayableAmount.
     * 
     * @param p_unitValue
     *            long
     * @param p_requestedQuantity
     *            double
     * @param p_commissionValue
     *            long
     * @param p_discountValue
     *            long
     * @return long
     */
    public long calculatePayableAmount(long p_unitValue, double p_requestedQuantity, long p_commissionValue, long p_discountValue);
    
    

    /**
     * validateCreditRequestSms
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateCreditRequestSms(Connection p_con, P2PTransferVO p_p2pTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * validateC2SRechargeRequestSms
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateC2SRechargeRequestSms(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method calculateNetPayableAmount.
     * 
     * @param p_payableAmount
     *            long
     * @param p_tax3Value
     *            long
     * @return long
     */

    public long calculateNetPayableAmount(long p_payableAmount, long p_tax3Value);
    
    

    /**
     * Method to calculate the card group access fee for sender and receiver
     * 
     * @param p_accessFeeValue
     * @param p_accessFeeType
     * @param p_requestedValue
     * @param p_minAccessFee
     * @param p_maxAccessFee
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateAccessFee(double p_accessFeeValue, String p_accessFeeType, long p_requestedValue, long p_minAccessFee, long p_maxAccessFee) throws BTSLBaseException;

    /**
     * Method to calculate the card group tax1
     * 
     * @param p_type
     * @param p_rate
     * @param p_requestValue
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateCardGroupTax1(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException;

    /**
     * Method to calculate the card group tax2
     * 
     * @param p_type
     * @param p_rate
     * @param p_requestValue
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateCardGroupTax2(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException;

    /**
     * Method to calculate the bonus value
     * 
     * @param p_type
     * @param p_rate
     * @param p_requestValue
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateCardGroupBonus(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException;

    /**
     * method to calculate the validity based on the parameters
     * 
     * @param p_transferVO
     * @param p_transferDateTime
     * @param p_previousExpiry
     * @param p_valPeriodType
     * @param p_validityPeriod
     * @param p_bonusValidity
     * @throws BTSLBaseException
     */
    public void calculateValidity(TransferVO p_transferVO, Date p_transferDateTime, Date p_previousExpiry, String p_valPeriodType, int p_validityPeriod, int p_bonusValidity) throws BTSLBaseException;

    /**
     * Method to check whether recharge is allowed in grace or not
     * 
     * @param p_msisdn
     * @param p_graceValue
     * @param p_previousExpiry
     * @param p_currentDate
     * @throws BTSLBaseException
     */
    public void checkRechargeInGraceAllowed(String p_msisdn, long p_graceValue, Date p_previousExpiry, Date p_currentDate) throws BTSLBaseException;

    /**
     * Method to calculate the sender transfer value based on the requested
     * amount and taxes
     * 
     * @param p_requestedValue
     * @param p_calculatedTax1Value
     * @param p_calculatedTax2Value
     * @param p_calculatedAccessFee
     * @return long
     */
    public long calculateSenderTransferValue(long p_requestedValue, long p_calculatedTax1Value, long p_calculatedTax2Value, long p_calculatedAccessFee);

    /**
     * Method to calculate the reciever transfer value based on the requested
     * amount and taxes
     * 
     * @param p_requestedValue
     * @param p_calculatedAccessFee
     * @param p_calculatedTax1Value
     * @param p_calculatedTax2Value
     * @param p_calculatedBonusTalkTimeValue
     * @return long
     */
    public long calculateReceiverTransferValue(long p_requestedValue, long p_calculatedAccessFee, long p_calculatedTax1Value, long p_calculatedTax2Value, long p_calculatedBonusTalkTimeValue);

    /**
     * Method to calculate the Differential tax1
     * 
     * @param p_type
     * @param p_rate
     * @param p_commValue
     * @param p_requestValue
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateDifferentialTax1(String p_type, double p_rate, long p_commValue, long p_requestValue) throws BTSLBaseException;

    /**
     * Method to calculate the Differential tax2
     * 
     * @param p_type
     * @param p_rate
     * @param p_requestValue
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateDifferentialTax2(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException;

    /**
     * Method to calculate the Differential margin commission
     * 
     * @param p_type
     * @param p_rate
     * @param p_requestValue
     * @return long
     * @throws BTSLBaseException
     */
    public long calculateDifferentialComm(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException;

    /**
     * Method to calculate the Differential transfer value
     * 
     * @param p_requestAmount
     * @param p_afterMultipleFact
     * @param p_calculatedTax1Value
     * @param p_calculatedTax2Value
     * @return long
     */
    public long calculateDifferentialTransferValue(long p_requestAmount, long p_afterMultipleFact, long p_calculatedTax1Value, long p_calculatedTax2Value);

    /**
     * Method to format C2S Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatC2STransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method to format the P2P Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatP2PTransferID(TransferVO p_transferVO, long p_tempTransferID);
    /**
     * Method to format the Voucher ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatVoucherTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method to format the P2P + validity extension Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatP2PTransferIDWithValidityExt(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method formatChannelTransferID to format the O2C/C2C Transfer ID
     * 
     * @param p_channelTransferVO
     * @param p_tempTransferStr
     * @param p_tempTransferID
     * @return String
     */
    public String formatChannelTransferID(ChannelTransferVO p_channelTransferVO, String p_tempTransferStr, long p_tempTransferID);

    /**
     * Method to format the Network Stock Transaction ID
     * 
     * @param p_networkStockTxnVO
     *            NetworkStockTxnVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatNetworkStockTxnID(NetworkStockTxnVO p_networkStockTxnVO, long p_tempTransferID);
    public String formatNetworkStockTxnID1(NetworkStockTxnVO1 p_networkStockTxnVO, long p_tempTransferID);
    /**
     * Method to format the Schedule Batch ID
     * 
     * @param p_scheduleMasterVO
     *            ScheduleBatchMasterVO
     * @param p_tempTransferStr
     *            String
     * @param p_tempTransferID
     * @return String
     */
    public String formatScheduleBatchID(ScheduleBatchMasterVO p_scheduleMasterVO, String p_tempTransferStr, long p_tempTransferID, String networkId);

    /**
     * Method to perform validation of start and end range for card group.
     * added for CRE_INT_CR00029 by ankit Zindal
     * 
     * @param p_startRange
     *            String
     * @param p_endRange
     *            String
     * @param p_subService
     *            String
     * 
     * @throws Exception
     */
    public void validateCardGroupDetails(String p_startRange, String p_endRange, String p_subService) throws Exception;

    /**
     * Method to set the value for transfer.
     * This methos is called from CardGroupBL
     * added for CRE_INT_CR00029 by ankit Zindal
     * 
     * @param p_subService
     *            String
     * @param p_cardGroupDetailVO
     *            CardGroupDetailsVO
     * @param p_transferVO
     *            TransferVO
     * 
     * @throws Exception
     */
    public void setCalculatedCardGroupValues(String p_subService, CardGroupDetailsVO p_cardGroupDetailVO, TransferVO p_transferVO) throws Exception;

    /**
     * validateC2SRechargeRequest
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateC2SRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * validateC2SBillPmtRequest
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateC2SBillPmtRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method to format the FOC Batch master transaction ID
     * 
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_tempTransferID
     *            long
     * @return String
     */
    public String formatFOCBatchMasterTxnID(FOCBatchMasterVO p_batchMasterVO, long p_tempTransferID);

    /**
     * Method to format the FOC Batch details transaction ID
     * 
     * @param p_batchMasterID
     *            String
     * @param p_tempTransferID
     * @return String
     */
    public String formatFOCBatchDetailsTxnID(String p_batchMasterID, long p_tempTransferID);

    /**
     * Method formatAdjustmentTxnID.
     * 
     * @param p_networkCode
     *            String
     * @param p_currenDate
     *            Date
     * @param p_tempTransferID
     *            long
     * @return String
     */
    public String formatAdjustmentTxnID(String p_networkCode, Date p_currenDate, long p_tempTransferID);

    /**
     * This method is used to append the Netwok code into Subscriber id and
     * refferenced in the
     * RestrictedSubscriberDAO to make the subscriber id as unique for the
     * different networks
     * while processing the restricted subscriber file.
     * 
     * @param String
     *            p_subsId
     * @param String
     *            p_networkCode
     * @return String
     */
    public String getRestrictedSubscriberID(String p_subsId, String p_networkCode) throws BTSLBaseException;

    /**
     * This method will convert operator specific msisdn to system specific
     * msisdn.
     * 
     * @param p_msisdn
     * @return
     */
    public String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException;

    /**
     * This method will convert system specific msisdn to operater specific
     * msisdn
     * 
     * @param p_msisdn
     * @return
     */
    public String getOperatorFilteredMSISDN(String p_msisdn);

    /**
     * this method is used to validate the bill payment request
     * validateC2SbillPaymentRequest
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateUtilityBillPaymentRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * This method is called from the tata sky recharge process for the
     * validation of receiverNotification Number
     * returned from the IN.
     * 
     * @param p_notificationMSISDN
     * @return
     * @throws BTSLBaseException
     */
    public String validateReceiverNotificationNumber(String p_notificationMSISDN) throws BTSLBaseException;

    /**
     * method used to check whether decimal amount is validate in the system
     * also validate decimal amount
     * 
     * @author sourabh.gupta
     */
    public void validateDecimalAmount(String p_serviceType, long p_amount) throws Exception;

    public String formatBatchesID(String p_networkCode, String p_prefix, Date p_currenDate, long p_tempTransferID);

    /**
     * validateEVDRequestFormat
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */

    public void validateEVDRequestFormat(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * This method will be used to handle transfer message format
     * 
     * @param p_con
     * @param requestMessageArray
     * @param p_transferVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void handleTransferMessageFormat(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception;

    /**
     * validateMVDRequestFormat
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public int validateMVDRequestFormat(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method to format EVD Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatEVDTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * @author sanjeew.kumar
     * @created on 12/07/07
     * @param p_loginID
     * @param p_password
     * @return HashMap
     */
    public HashMap validatePassword(String p_loginID, String p_password);

    /**
     * Method to validate the PIN that is sent by user and that stored in
     * database
     * 
     * @param p_con
     * @param p_channelUserVO
     * @param p_requestPin
     * @throws BTSLBaseException
     */
    public void validatePIN(Connection p_con, ChannelUserVO p_channelUserVO, String p_requestPin) throws BTSLBaseException;

    /**
     * Method validatePIN
     * 
     * @author sanjeew.kumar
     * @created on 19/07/07
     * @param p_pin
     *            String
     * @return HashMap
     */
    public HashMap pinValidate(String p_pin);

    /**
     * Date : Jul 23, 2007
     * Discription :
     * Method : validateTransactionPassword
     * 
     * @param p_channelUserVO
     * @param p_password
     * @throws BTSLBaseException
     * @return void
     * @author ved.sharma
     */
    public boolean validateTransactionPassword(ChannelUserVO p_channelUserVO, String p_password) throws BTSLBaseException;

    /**
     * Date : Aug 16, 2007
     * Discription :
     * Method : getReceiverConversionRate
     * 
     * @param p_senderInterfaceID
     * @param p_receiverInterfaceID
     * @throws BTSLBaseException
     * @return double
     * @author ved.sharma
     */
    /*
     * This code was commented on 01/04/08 to eliminate fetch conversion rate
     * step.
     * Now Moldova will support single currency. Previously conversion rate was
     * required to
     * support multiple currency for moldova.
     * 
     * public double getReceiverConversionRate(String p_senderInterfaceID,String
     * p_receiverInterfaceID) throws BTSLBaseException;
     */
    /**
     * Date : Aug 23, 2007
     * Discription :
     * Method : getReceiverConversionRate
     * 
     * @param p_receiverInterfaceID
     * @throws BTSLBaseException
     * @return double
     * @author ved.sharma
     */
    /*
     * This code was commented on 01/04/08 to eliminate fetch conversion rate
     * step.
     * Now Moldova will support single currency. Previously conversion rate was
     * required to
     * support multiple currency for moldova.
     * 
     * public double getReceiverConversionRate(String p_receiverInterfaceID)
     * throws BTSLBaseException;
     */

    /**
     * Method to Add or remove string at starting position of msisdn
     * 
     * @param msisdn
     * @return String
     */
    public String addRemoveDigitsFromMSISDN(String msisdn);

    /**
     * Date : Sep 28, 2007
     * Discription :
     * Method : validatePIN
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_requestPin
     * @throws BTSLBaseException
     * @return void
     * @author ved.sharma
     */
    public void validatePIN(Connection p_con, SenderVO p_senderVO, String p_requestPin) throws BTSLBaseException;

    /**
     * Date : Oct 01, 2007
     * Discription :
     * Method : checkMsisdndServiceClassMapping
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_serviceType
     * @param p_serviceClassCode
     * @param p_module
     * @param p_userType
     * @throws BTSLBaseException
     * @return boolean
     * @author ankit.singhal
     */
    public boolean checkMsisdnServiceClassMapping(Connection p_con, String p_msisdn, String p_serviceType, String p_serviceClassCode, String p_module, String p_userType) throws BTSLBaseException;

    /**
     * validateC2SEnquiryRequest
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @author ranjana.chouhan
     */
    public void validateC2SEnquiryRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * isC2SPayeeAllowed
     * 
     * @param p_con
     * @param p_msisdn
     * @throws BTSLBaseException
     * @author Sanjeew
     */
    public boolean isRestrictedSubscriberAllowed(Connection p_con, String p_msisdn, String p_blacklist_type) throws BTSLBaseException;

    /**
     * Method to validate the PIN rules that is sent by user
     * 
     * @param p_requestPin
     * @throws BTSLBaseException
     */
    public void validatePINRules(String p_requestPin) throws BTSLBaseException;

    /**
     * validateC2SGiftRechargeRequest
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateC2SGiftRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * handleConfirmTransferMessageFormat
     * 
     * @param p_requestVO
     * @param p_transferVO
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void handleConfirmTransferMessageFormat(RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception;

    /**
     * Method to format C2SBillPayment Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatBillPayTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method to format PostpaidBillPayment Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatPostpaidBillPayTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method to format GiftRecharge Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatGiftRechargeTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method to format MVD Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatMVDTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method processPostBillPayment.
     * 
     * @param p_requestedAmt
     *            long
     * @param p_prevBal
     *            long
     * @return boolean
     */
    public boolean processPostBillPayment(long p_requestedAmt, long p_prevBal);

    /**
     * Method to format the C2C Batch master transaction ID
     * 
     * @param p_batchMasterVO
     *            C2CBatchMasterVO
     * @param p_tempTransferID
     *            long
     * @return String
     */
    public String formatC2CBatchMasterTxnID(C2CBatchMasterVO p_batchMasterVO, long p_tempTransferID);

    /**
     * Method to format the C2C Batch details transaction ID
     * 
     * @param p_batchMasterID
     *            String
     * @param p_tempTransferID
     * @return String
     */
    public String formatC2CBatchDetailsTxnID(String p_batchMasterID, long p_tempTransferID);

    /**
     * Method to validate C2S fix line recharge request
     * 
     * @param p_batchMasterID
     *            String
     * @param p_tempTransferID
     * @return String
     */
    public void validateC2SFixLineRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method to format Transfer ID for FixLine Recharge
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatFixLineRCTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method to validate C2S Broadband recharge request
     * 
     * @param p_batchMasterID
     *            String
     * @param p_tempTransferID
     * @return String
     */
    public void validateC2SBroadbandRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method to format Transfer ID for Broadband Recharge
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatBroadbandRCTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method to generate randomPin.
     * 
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#randomPinGenerate()
     */
    public String randomPinGenerate();

    /**
     * Method to generate random password.
     * 
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#randomPwdGenerate()
     */
    public String randomPwdGenerate();

    /**
     * Method for calculating transaction based activation bonus
     * 
     * @param c2sTransferVO
     * @param profileSetVO
     * @param p_subscriberType
     * @return
     * @throws BTSLBaseException
     */
    public ActivationBonusVO calculateActivationTxnBonus(C2STransferVO c2sTransferVO, ProfileSetVO profileSetVO, String p_subscriberType) throws BTSLBaseException;

    /**
     * Method for calculating volume based activation bonus
     * 
     * @param userTransactionVO
     * @param profileSetVO
     * @param p_serviceType
     * @return
     * @throws BTSLBaseException
     */
    public ActivationBonusVO calculateActivationVolumeBonus(UserTransactionVO userTransactionVO, ProfileSetVO profileSetVO, String p_subscriberType) throws BTSLBaseException;

    /**
     * Method for calculating volume based activation bonus for unlimted period
     * 
     * @param userTransactionVO
     * @param profileSetVO
     * @param p_serviceType
     * @return
     * @throws BTSLBaseException
     */
    public ActivationBonusVO calculateActivationVolumeBonusUnlimitedPeriod(UserTransactionVO userTransactionVO, ProfileSetVO profileSetVO, String p_subscriberType) throws BTSLBaseException;

    /**
     * validateVASSellingRequest
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateVASSellingRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method to format VAS TransferID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatVASTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /*
     * @throws BTSLBaseException
     * 
     * @see com.btsl.pretups.util.OperatorUtilI#calculateCommission(long, long)
     */
    public long calculateCommissionQuantity(long p_commissionValue, long p_unitValue, long p_tax3Value) throws BTSLBaseException;
    
    

    public long calculateReceiverCreditQuantity(String p_requestedQty, long p_unitValue, long p_commisionQty) throws BTSLBaseException;
    
    

    /**
     * Method to to generate Random password
     * 
     * @return String
     */
    public String generateRandomPassword();

    /**
     * Method to to generate Random pin
     * 
     * @return String
     */
    public String generateRandomPin();

    /**
     * checkPasswordPeriodToResetAfterCreation
     * 
     * @param p_modifiedOn
     * @param p_channelUserVO
     */
    public boolean checkPasswordPeriodToResetAfterCreation(Date p_modifiedOn, ChannelUserVO p_channelUserVO);

    /**
     * validateRRServiceRequest
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateRRServiceRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method formatRRTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatRoamRechargeTransferID(TransferVO,
     *      long)
     */
    public String formatRoamRechargeTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * validateIRServiceRequest
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateIRServiceRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method formatIntlRechargeTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatIntlRechargeTransferID(TransferVO,
     *      long)
     */
    public String formatIntlRechargeTransferID(TransferVO p_transferVO, long p_tempTransferID);

    public String currentDateTimeFormatString(Date p_date) throws ParseException;

    public String currentTimeFormatString(Date p_date) throws ParseException;

    /**
     * Method to format the Multiple bonus account string to insert in
     * 
     * @param p_bonusVOList
     *            ArrayList
     * @return String
     */
    public String formatBonusSummaryString(ArrayList p_bonusVOList) throws Exception;

    public void populateBonusListAfterValidation(HashMap p_map, C2STransferVO p_c2stransferVO);

    public void updateBonusListAfterTopup(HashMap p_map, C2STransferVO p_c2stransferVO);

    // added by Lohit for Direct Payout
    public String formatDPBatchMasterTxnID(FOCBatchMasterVO p_batchMasterVO, long p_tempTransferID);

    public String formatDPBatchDetailsTxnID(String p_batchMasterID, long p_tempTransferID);

    /**
     * Method isPinUserId
     * 
     * @author vikram.kumar
     * @created on 21/01/2010
     * @param p_pin
     *            String
     * @param p_userId
     *            String
     * @return boolean
     */
    public boolean isPinUserId(String p_pin, String p_userId);

    public boolean handleLDCCRequest();

    public void validatePIN(String p_pin) throws BTSLBaseException;

    /**
     * @param p_con
     *            Connection
     * @param p_requestVO
     *            RequestVO
     * @param p_transferVO
     *            TransferVO
     * @throws BTSLBaseException
     * @throws Exception
     */
   // public void validateP2PMeassgeFormat(Connection p_con, RequestVO p_requestVO, SenderVO p_senderVO, TransferVO p_transferVO) throws BTSLBaseException, Exception;

    public String formatSaleBatchNumber(String p_tempTransferStr, long p_tempTransferID);

    /* for customization of zain airtel as they dont have confirm pin in STK */
    public String[] getP2PChangePinMessageArray(String message[]);

    public String[] getC2SChangePinMessageArray(String message[]);

    public void validateC2STransEnqRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;// added
    // for
    // c2s
    // transaction
    // enquiry
    // by
    // retailer
    // date
    // basis
    // by addded by jasmine

    public void validateSIDRules(String p_requestPin) throws BTSLBaseException;

    public String generateRandomSID() throws BTSLBaseException;

    public int getTxnEnqryMessageArray(String[] p_message, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method isValidSidLength
     * 
     * @author anu.garg
     * @created on 17/01/2011
     * @param p_sid
     *            String
     * @return boolean
     */
    public boolean isValidSidLength(String p_sid);

    public boolean isValidSIDAlpha(String regEx);

    public boolean isValidSIDNumeric(String sid);

    public int getSIDDeletionMessageArray(Connection con, String message[], RequestVO requestVO) throws Exception;// added
    // by
    // ankuj
    // for
    // private
    // recharge

    public int getSIDEnquiryMessageArray(Connection con, String message[], RequestVO requestVO) throws Exception;

    /**
     * This method used to validate the SID exist or not in the database
     */
    public PrivateRchrgVO getPrivateRechargeDetails(Connection p_con, String p_sid) throws BTSLBaseException;

    // added by shashank for CRBT

    /**
     * Method to validate the user message sent
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateCRBTRegistrationRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateCRBTSongSelectionRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return
     */
    public String formatCRBTRegistrationTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return
     */
    public String formatCRBTSongSelectionTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method to format the P2P Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatMultP2PTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method to format the O2C Batch master transaction ID
     * 
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_tempTransferID
     *            long
     * @return String
     */
    public String formatO2CBatchMasterTxnID(FOCBatchMasterVO p_batchMasterVO, long p_tempTransferID);

    // Method added for the OJO requirement, send the encrypted message during
    // the private recharge only
    public String DES3Encryption(String p_message, RequestVO p_requestvo) throws BTSLBaseException, Exception;

    public void checkSOSMessageFormat(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception;

    /**
     * Method to format the SOS Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatSOSTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * @param p_batchMasterVO
     * @param p_tempTransferID
     * @return
     */

    public String formatMCDLP2PTransferID(TransferVO p_transferVO, long p_tempTransferID);

    public MCDListVO validateMCDListAMDRequest(Connection p_con, String[] p_requestArr, RequestVO p_requestVO) throws BTSLBaseException;

    public void handleMCDTransferMessageFormat(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception;

    // VASTRIX ADDED BY HITESH
    /**
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateVasRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validatePVasRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * @param p_transferVO
     * @param p_tempTransferID
     * @param p_prefix
     * @return
     */
    public String formatVASPVASTransferID(TransferVO p_transferVO, long p_tempTransferID, String p_prefix);

    // for c2s table merging
    public boolean getNewDataAftrTbleMerging(Date p_fromDate, Date p_toDate) throws BTSLBaseException;

    // for DMS Module
    /**
     * Method to to validtae LOGIN_ID check
     * 
     * @return String
     */
    public HashMap validateLoginId(String p_loginID);

    // added for user level transfer rule type
    /**
     * @author gaurav.pandey
     * @param p_con
     * @param p_channelUserVO
     * @param p_toCatCode
     *            TODO
     * @param p_txnType
     *            TODO
     * @param p_userName
     *            TODO
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserListForTrfRuleTypeByUserLevel(Connection p_con, ChannelUserVO p_channelUserVO, String p_toCatCode, String p_txnType, String p_userName) throws BTSLBaseException;

    /**
     * @author gaurav.pandey
     * @param p_con
     * @param p_senderVO
     *            ChannelUserVO
     * @param p_receiverVO
     *            ChannelUserVO
     * @return isValidUserForXfr boolean
     * @throws BTSLBaseException
     */

    public boolean validateUserForTrfRuleTypeByUserLevel(Connection p_con, ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO, boolean p_isFromWeb) throws BTSLBaseException;

    
    /**
     * @param p_con
     * @param p_channelUserVO
     * @param p_toCategoryCode
     * @param p_txnType
     * @param p_userName
     * @return
     */
    public LinkedHashMap loadBatchUserListForTrfRuleTypeByUserLevel(Connection p_con, ChannelUserVO p_channelUserVO, String p_toCategoryCode, String p_txnType, String p_userName) throws BTSLBaseException;

    /**
     * Method to format the Multiple bonus account string to insert in
     * 
     * @param p_c2sTransferVO
     *            C2STransferVO
     */
    public void formatBonusPrevoiusAndPostString(C2STransferVO p_c2sTransferVO) throws Exception;

    /**
     * @author gaurav.pandey
     * @param p_INPromo
     *            double
     * @param p_BonusTalkTime
     *            long
     * @return INPromo double
     */
    public double calculateINPromo(double p_INPromo, long p_BonusTalkTime);

    /**
     * @author gaurav.pandey
     * @param p_RequestedAmount
     *            long
     * @param p_BonusTalkTime
     *            long
     * @return rechargeComment String
     */
    public String getRechargeComment(long p_RequestedAmount, long p_BonusTalkTime);

    /**
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateSIMACTRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method to format SIM Activation Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatSIMACTTransferID(TransferVO p_transferVO, int p_tempTransferID);

    /**
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public void validateSubscriberEnquiryRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    // added by harsh for Scheduled Credit List (Add/Modify/Delete) API on 23
    // Apr 13
    public MCDListVO validateSMCDListAMDRequest(Connection p_con, String[] p_requestArr, RequestVO p_requestVO) throws BTSLBaseException;

    public void validateDTHRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method to Generate OTP
     * 
     * @return String
     */
    public String generateOTP() throws Exception;

    public void validateC2SRequestWithoutAmount(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * Method to format Channel User Creation Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatChnlUserTransferID(TransferVO p_transferVO, long p_tempTransferID);

    /**
     * Method that will validate the user message sent
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#validateCollectionBillpaymentRequest(Connection,
     *      C2STransferVO, RequestVO)
     */
    public void validateCollectionBillpaymentRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    public String formatTransferID(TransferVO p_transferVO, long p_tempTransferID, String requestedId);

    public void validateC2SReverrsalRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    public void validateC2SPrepaidReverrsalRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    public void validateReversalOldTxnId(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    public String c2sTransferTDRLog(C2STransferVO p_c2sTransferVO, TransferItemVO p_senderTransferItemVO, TransferItemVO p_receiverTransferItemVO);

    /**
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validatePPBEnquiryRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    /**
     * @param p_con
     * @param p_requestVO
     * @param p_transferVO
     * @param p_senderVO
     * @return
     */

    public LoyaltyPointsRedemptionVO CalculateSumOfTrasaction(Connection p_con, LoyaltyPointsRedemptionVO p_redemptionVO);

    /**
     * Method to format the O2C Batch master transaction ID
     * 
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_tempTransferID
     *            long
     * @return String
     */
    public String formatO2CBatchMasterTxnID(O2CBatchMasterVO p_batchMasterVO, long p_tempTransferID);

    /**
     * @param p_batchMasterID
     * @param p_tempTransferID
     * @return
     */
    public String formatO2CBatchDetailsTxnID(String p_batchMasterID, long p_tempTransferID);

    /**
     * validateC2SRoamRechargeRequest
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateC2SRoamRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

    public String formatC2SRoamTransferID(TransferVO p_transferVO, long p_tempTransferID);

    public int changeUserStatusToActive(Connection p_con, String p_activeUserID, String p_currentStatus, String p_newStatus) throws BTSLBaseException;

    /**
     * @param p_activeUserID
     * @param p_currentStatus
     * @return int
     */
    public void validateEVDO2CRequestFormat(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException, Exception;

    public void validateVoucherPin(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception;

    public String decryptPINPassword(String p_pinpassword) throws BTSLBaseException;

    public String encryptPINPassword(String p_pinpassword) throws BTSLBaseException;

    /**
     * Method to format the LPT Batch master transaction ID
     * 
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_tempTransferID
     *            long
     * @return String
     */
    public String formatLPTBatchMasterTxnID(FOCBatchMasterVO p_batchMasterVO, long p_tempTransferID);

    /**
     * Method to format the LPT Batch details transaction ID
     * 
     * @param p_batchMasterID
     *            String
     * @param p_tempTransferID
     * @return String
     */
    public String formatLPTBatchDetailsTxnID(String p_batchMasterID, long p_tempTransferID);

		public void validateCP2PIRServiceRequest(Connection p_con,P2PTransferVO p2pTransferVO,RequestVO p_requestVO)  throws BTSLBaseException;
		public String formatIATP2PTransferID(TransferVO p_transferVO,long p_tempTransferID);
		
		/**
		 * Method calculateTax1.
		 * @param p_type String
		 * @param p_rate double
		 * @param p_value long
		 * @return long
		 * @throws BTSLBaseException
		 * @see com.btsl.pretups.util.OperatorUtilI#calculateTax2(String, double, long)
		 */
		public long calculatePenaltyTax1(String p_type,double p_rate,long p_value) throws BTSLBaseException;
		
	    
	    /**
		 * Method to calculate the card group tax1
		 * @param p_type
		 * @param p_rate
		 * @param p_requestValue
		 * @return long
		 * @throws BTSLBaseException
		 */
		public long calculatePenaltyTax2(String p_type,double p_rate,long p_value) throws BTSLBaseException;





		public C2STransferVO getC2STransferVOFromTxnID(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException;

		public ListValueVO getClientMNPInfo(Connection p_con,String p_msisdn,String p_type) throws BTSLBaseException;
		
		


		public void validateC2SLiteRechargeRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException;
		
		public ListValueVO validateMSISDNForMNP(String p_msisdn) throws BTSLBaseException;
		
		/**
		 * validateCardGroupEnquiryRequest
		 * @param p_con
		 * @param p_c2sTransferVO
		 * @param p_requestVO
		 * @throws BTSLBaseException
		 * @author harsh.dixit
		 */
		public void validateCardGroupEnquiryRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException;
		
		/**
		 * Method to calculate the card group tax3
		 * @param p_cardGroupDetailsVO
		 * @param p_c2sTransferVO
		 * @return long
		 * @throws BTSLBaseException
		 */
		   public long calculateCardGroupTax3(CardGroupDetailsVO p_cardGroupDetailsVO,C2STransferVO p_c2sTransferVO) throws BTSLBaseException; 
	    /**
	     * Method to calculate the card group tax4
	     * @param p_cardGroupDetailsVO
	     * @param p_c2sTransferVO
	     * @return long
	     * @throws BTSLBaseException
	     */
		public long calculateCardGroupTax4(CardGroupDetailsVO p_cardGroupDetailsVO,C2STransferVO p_c2sTransferVO) throws BTSLBaseException;
		
		  /**
	     * validateMultiCurrencyRechargeRequest
	     * 
	     * @param p_con
	     * @param p_c2sTransferVO
	     * @param p_requestVO
	     * @throws BTSLBaseException
	     */
	    public void validateMultiCurrencyRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

	    
	    /**
	     * getConvertedAmount
	     * @param requestAmtStr
	      * @param Currency
	     * @throws BTSLBaseException
	     */
		 public double getConvertedAmount(String requestAmtStr, double Currency) throws BTSLBaseException ;
		 
		 /**
		     * Method to format requestXML for self Closing Tags
		     * @param p_requestXML
		     * @return StringXML 
		     * @throws BTSLBaseException
		     */
		 public String formatRequestXMLString(String p_requestXML) throws BTSLBaseException;     
		 
		    /**
		     * Method to calculate the OTF commission
		     * 
		     * @param p_type
		     * @param p_rate
		     * @param p_requestValue
		     * @return long
		     * @throws BTSLBaseException
		     */
		    public long calculateOTFComm(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException;
		    public String formatVoucherTransferID(TransferVO p_transferVO,long p_tempTransferID,String p_type);
			public boolean isSubscriberPrefixMappingExist(Connection con,String receiverMsisdn,String senderMsisdn,String subscriberType);
			 public String formatVomsSerialnum(long p_counter, String p_activeproductid, String segment, String nwCode);
			 public ArrayList generatePin(String location_code, String product_code, long totalCount,int seq) throws IOException ;
			 
			 /**
			  * 	Method to format C2S Transfer ID
			  * @param p_transferVO
			  * @param p_tempTransferID
			  * @return String
			 */
			 public String formatOLORCID(TransferVO p_transferVO,long p_tempTransferID);

			 public void validateOLORequestWithoutAmount(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException;

			public Map getInitMap(Connection con, ChannelTransferVO channelTransferVO, ChannelUserVO sessionUserVo, String paymentGatewayType);
			public ArrayList loadProductCodeList();

			
			/**
		     * Method to format Product Recharge Transfer ID
		     * 
		     * @param p_transferVO
		     * @param p_tempTransferID
		     * @return String
		     */
		    public String formatDBRCTransferID(TransferVO p_transferVO,long p_tempTransferID);
		    /**
			 * Method that will validate the validateProductRechargeRequest user message sent
			 * @param p_con
			 * @param p_c2sTransferVO
			 * @param p_requestVO
			 * @throws BTSLBaseException
			 * @see com.btsl.pretups.util.OperatorUtilI#validateProductRechargeRequest(Connection, C2STransferVO, RequestVO)
			 */
		    public void validateProductRechargeRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException;

			public ArrayList removeVMSProductCodeList(ArrayList productList);
			public RequestVO parsePaymentMessage(RequestVO p_requestVO,String message);
			public Map validatePaymentRefId(ChannelTransferVO channelTransferVO) ;
			/**
			 * @param p_filteredMSISDN
			 * @param p_subscriberType
			 * @return
			 * @throws BTSLBaseException
			 * @author ashish.gupta
			 * For VIL
			 */
			public NetworkPrefixVO getNetworkDetails(String p_filteredMSISDN, String p_subscriberType) throws BTSLBaseException; 
			public long calculateAmount(long p_requestValue1,long p_requestValue2);
			public  double calculateCardGroupTaxDouble(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException;
			public double calculateAmount(double p_requestValue1,double p_requestValue2);
			public long calculateCardGroupBonusForDouble(String p_type, double p_rate, double p_requestValue) throws BTSLBaseException;
			
		    public int validateDVDRequestFormat(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException;

		    public String formatDVDTransferID(TransferVO p_transferVO, long p_tempTransferID);
			
			public String formatVoucherExpiryChangeID();
			/**
			 * Method to format the Operator C2C Batch master transaction ID
			 * @param p_batchMasterVO C2CBatchMasterVO
			 * @param p_tempTransferID long
			 * @return String
			 */
			public String formatOPTC2CBatchMasterTxnID(C2CBatchMasterVO p_batchMasterVO, long p_tempTransferID);
			
			public long calculatePremium(UserLoanVO p_userLoanVO, ArrayList<LoanProfileDetailsVO> p_profileSlabList) throws BTSLBaseException ;

			public ArrayList loadUserListForTrfRuleTypeByUserLevelForLoginID(Connection p_con,
					ChannelUserVO p_channelUserVO, String p_toCategoryCode, String p_txnType, String p_loginID) throws BTSLBaseException;
	}
		

