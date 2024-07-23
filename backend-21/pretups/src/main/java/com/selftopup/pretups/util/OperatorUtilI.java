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

package com.selftopup.pretups.util;

import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.selftopup.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;

/**
 * @author avinash.kamthan
 * 
 */
public interface OperatorUtilI {
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
     * Method to format the P2P Transfer ID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */
    public String formatP2PTransferID(TransferVO p_transferVO, long p_tempTransferID);

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
     * @author sanjeew.kumar
     * @created on 12/07/07
     * @param p_loginID
     * @param p_password
     * @return HashMap
     */
    public HashMap validatePassword(String p_loginID, String p_password);

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
     * Method to generate randomPin.
     * 
     * @return String
     * @see com.selftopup.pretups.util.OperatorUtilI#randomPinGenerate()
     */
    public String randomPinGenerate();

    /**
     * Method to generate random password.
     * 
     * @return String
     * @see com.selftopup.pretups.util.OperatorUtilI#randomPwdGenerate()
     */
    public String randomPwdGenerate();

    /**
     * Method to format VAS TransferID
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return String
     */

    public long calculateCommissionQuantity(long p_commissionValue, long p_unitValue, long p_tax3Value) throws BTSLBaseException;

    public String currentDateTimeFormatString(Date p_date) throws ParseException;

    public String currentTimeFormatString(Date p_date) throws ParseException;

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
    public void validateP2PMeassgeFormat(Connection p_con, RequestVO p_requestVO, SenderVO p_senderVO, TransferVO p_transferVO) throws BTSLBaseException, Exception;

    /**
     * Method that will validate the user message sent
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @see com.selftopup.pretups.util.OperatorUtilI#validateCollectionBillpaymentRequest(Connection,
     *      C2STransferVO, RequestVO)
     */

    public void validateP2PAdhocRechargeMessageFormat(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO, SenderVO p_senderVO) throws BTSLBaseException, Exception;

    /**
     * Method to Add or remove string at starting position of msisdn
     * 
     * @param msisdn
     * @return String
     */
    public String addRemoveDigitsFromMSISDN(String msisdn);

    public String[] getP2PChangePinMessageArray(String message[]);

}
