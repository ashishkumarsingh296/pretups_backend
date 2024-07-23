package com.pretupsControllers.commissionprofile;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.commons.PretupsI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.pretupsControllers.calculatorI;
import com.utils._masterVO;
import com.utils.Log;
import com.utils._parser;

public class ChannelTransferBL extends BaseTest{
	
	public static long productCost;
	public static long value = 0;
	static String POSITIVE_COMM_APPLY;
	

    /**
     * APPLICABLE FOR 6.8 AND BELOW
     * 
     * Method calculateMRPWithTaxAndDiscount()
     * To calculate the taxes on the selected product list
     * 
     * @param p_transferItemsList
     * @param p_txnType
     *            String
     * @throws Exception
     */
    public static void calculateMRPWithTaxAndDiscount(String ReceiverMSISDN, String p_txnType, String ProductCode, String requestedQuantity) throws Exception {
        ChannelTransfersVO ChannelTransfersVO = new ChannelTransfersVO();
        ChannelTransfersVO.loadChannelTransfersDAO(ReceiverMSISDN, ProductCode, requestedQuantity);
    	long productCost = 0, value = 0;

            productCost = (long) (Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()) * ChannelTransfersVO.getUnitValue());
            // In case of FOC Commission will not be calculated
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                value = calculatorI.calculateCommission(ChannelTransfersVO.getCommType(), ChannelTransfersVO.getCommRate(), productCost);
            }
            ChannelTransfersVO.setCommValue(value);
            value = 0;
            value = calculatorI.calculateDiscount(ChannelTransfersVO.getDiscountType(), ChannelTransfersVO.getDiscountRate(), productCost);
            ChannelTransfersVO.setDiscountValue(value);
            value = 0;
            /*
             * To check whether tax is applicable for transaction or not.We have
             * the following three type of the
             * transaction and following corresponding check condition.
             * 1. In case of O2C transfer Tax calculation is mandatory.
             * 2. In case of C2C transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_C2C flag of the
             * commission profile.
             * 3. In case of FOC transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_FOC flag of the
             * commission profile.
             */
            if (p_txnType.equals(_masterVO.getProperty("O2CTransferCode")) || p_txnType.equals(_masterVO.getProperty("O2CWithdrawCode")) || 
            	p_txnType.equals(_masterVO.getProperty("O2CReturnCode")) || ((p_txnType.equals(_masterVO.getProperty("C2CTransferCode")) || 
            	p_txnType.equals(_masterVO.getProperty("C2CWithdrawCode")) || p_txnType.equals(_masterVO.getProperty("C2CReturnCode"))) && PretupsI.YES.equals(ChannelTransfersVO
                .getTaxOnChannelTransfer())) || (p_txnType.equals(_masterVO.getProperty("FOCCode")) && PretupsI.YES.equals(ChannelTransfersVO.getTaxOnFOCTransfer()))) {
                value = calculatorI.calculateTax1(ChannelTransfersVO.getTax1Type(), ChannelTransfersVO.getTax1Rate(), productCost);
                ChannelTransfersVO.setTax1Value(value);
                value = 0;
                value = calculatorI.calculateTax2(ChannelTransfersVO.getTax2Type(), ChannelTransfersVO.getTax2Rate(), ChannelTransfersVO.getTax1Value());
                ChannelTransfersVO.setTax2Value(value);
                value = 0;
                // set commision value as 0 , becoz commision not calculated on
                // the commision in case pf withdraw and return.
                POSITIVE_COMM_APPLY = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY");
                if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && (!_masterVO.getProperty("O2CTransferCode").equalsIgnoreCase(p_txnType) || !_masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))/*!PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(p_channelTransferVO.getTransferSubType())*/) {
                	ChannelTransfersVO.setCommValue(0);
                }
                // In case of FOC Tax3 will not be calculated
                if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                    value = calculatorI.calculateTax3(ChannelTransfersVO.getTax3Type(), ChannelTransfersVO.getTax3Rate(), ChannelTransfersVO.getCommValue());
                }
                ChannelTransfersVO.setTax3Value(value);
                value = 0;
            }
            value = calculatorI.calculateCommissionQuantity(ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getUnitValue(), ChannelTransfersVO
                .getTax3Value());
            ChannelTransfersVO.setCommQuantity(value);
            long payableAmount = 0;
            long netPayableAmount = 0;
            // In case of FOC payableAmount will not be calculated
            /*
             * if(!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC))
             * {
             * payableAmount =
             * calculatorI.calculatePayableAmount(channelTransferItemsVO
             * .getUnitValue
             * (),Double.parseDouble(channelTransferItemsVO.getRequestedQuantity
             * ()),channelTransferItemsVO.getCommValue(),channelTransferItemsVO.
             * getDiscountValue());
             * netPayableAmount =
             * calculatorI.calculateNetPayableAmount(payableAmount
             * ,channelTransferItemsVO.getTax3Value());
             * }
             */
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode")) && POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !_masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType) /*!PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
                .equalsIgnoreCase(p_channelTransferVO.getTransferSubType())*/ ) {
            	System.out.println("Entered First Segment");
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	ChannelTransfersVO.setPayableAmount(productCost);
            	ChannelTransfersVO.setNetPayableAmount(productCost);
            }// this is executed in case of Transfer and POSITIVE_COMM_APPLY is
             // true
            else if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode")) && _masterVO.getProperty("C2CTransferCode")
                .equalsIgnoreCase(p_txnType)) {
            	System.out.println("Entered Second Segment");
            	value = 0;
                value = calculatorI.calculateReceiverCreditQuantity(ChannelTransfersVO.getRequestedQuantity(), ChannelTransfersVO.getUnitValue(),
                		ChannelTransfersVO.getCommQuantity());
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setReceiverCreditQty(value);
                ChannelTransfersVO.setPayableAmount(productCost);
                ChannelTransfersVO.setNetPayableAmount(productCost);
                ChannelTransfersVO
                    .setProductTotalMRP(ChannelTransfersVO.getReceiverCreditQty() * ChannelTransfersVO.getUnitValue() / (SystemPreferences.MULTIPLICATIONFACTOR));
            }// executed in case of FOC or in case of POSITIVE_COMM_APPLY value
             // is false
            else if (!POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
            	System.out.println("Entered Third Segment");
            	payableAmount = calculatorI.calculatePayableAmount(ChannelTransfersVO.getUnitValue(), Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()),
                		ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getDiscountValue());
                netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, ChannelTransfersVO.getTax3Value());
                ChannelTransfersVO.setPayableAmount(payableAmount);
                ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
                ChannelTransfersVO.setProductTotalMRP(productCost);
            } else {
            	System.out.println("Entered Else Segment");
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	ChannelTransfersVO.setPayableAmount(payableAmount);
            	ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
            }
            //ChannelTransfersVO.setApprovedQuantity(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));

    }
    
    

    /**
     * Method calculateMRPWithTaxAndDiscount()
     * To calculate the taxes on the selected product list
     * 
     * @param p_transferItemsList
     * @param p_txnType
     *            String
     * @throws Exception
     */
    public static void calculateAmount(String ReceiverMSISDN, String p_txnType, String ProductCode, String requestedQuantity) throws Exception {
        ChannelTransfersVO ChannelTransfersVO = new ChannelTransfersVO();
        ChannelTransfersVO.loadChannelTransfersDAO(ReceiverMSISDN, ProductCode, requestedQuantity);
    	long productCost = 0, value = 0;
    	
            productCost = (long) (Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()) * ChannelTransfersVO.getUnitValue());
            // In case of FOC Commission will not be calculated
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                value = calculatorI.calculateCommission(ChannelTransfersVO.getCommType(), ChannelTransfersVO.getCommRate(), productCost);
            }
            ChannelTransfersVO.setCommValue(value);
            value = 0;
            value = calculatorI.calculateDiscount(ChannelTransfersVO.getDiscountType(), ChannelTransfersVO.getDiscountRate(), productCost);
            ChannelTransfersVO.setDiscountValue(value);
            value = 0;
            /*
             * To check whether tax is applicable for transaction or not.We have
             * the following three type of the
             * transaction and following corresponding check condition.
             * 1. In case of O2C transfer Tax calculation is mandatory.
             * 2. In case of C2C transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_C2C flag of the
             * commission profile.
             * 3. In case of FOC transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_FOC flag of the
             * commission profile.
             */
            if (p_txnType.equals(_masterVO.getProperty("O2CTransferCode")) || p_txnType.equals(_masterVO.getProperty("O2CWithdrawCode")) || p_txnType.equals(_masterVO.getProperty("O2CReturnCode")) || ( (p_txnType.equals(_masterVO.getProperty("C2CTransferCode")) || p_txnType.equals(_masterVO.getProperty("C2CWithdrawCode")) || p_txnType.equals(_masterVO.getProperty("C2CReturnCode"))) && PretupsI.YES.equals(ChannelTransfersVO
                .getTaxOnChannelTransfer())) || (p_txnType.equals(_masterVO.getProperty("FOCCode")) && PretupsI.YES.equals(ChannelTransfersVO.getTaxOnFOCTransfer()))) {
                value = calculatorI.calculateTax1(ChannelTransfersVO.getTax1Type(), ChannelTransfersVO.getTax1Rate(), productCost);
                ChannelTransfersVO.setTax1Value(value);
                value = 0;
                value = calculatorI.calculateTax2(ChannelTransfersVO.getTax2Type(), ChannelTransfersVO.getTax2Rate(), ChannelTransfersVO.getTax1Value());
                ChannelTransfersVO.setTax2Value(value);
                value = 0;
                // set commision value as 0 , becoz commision not calculated on
                // the commision in case pf withdraw and return.
                POSITIVE_COMM_APPLY = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY");
                if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !(_masterVO.getProperty("O2CTransferCode").equalsIgnoreCase(p_txnType) || _masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))) {
                	ChannelTransfersVO.setCommValue(0);
                }
                // In case of FOC Tax3 will not be calculated
                if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                    value = calculatorI.calculateTax3(ChannelTransfersVO.getTax3Type(), ChannelTransfersVO.getTax3Rate(), ChannelTransfersVO.getCommValue());
                }
                ChannelTransfersVO.setTax3Value(value);
                value = 0;
            }
            value = calculatorI.calculateCommissionQuantity(ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getUnitValue(), ChannelTransfersVO
                .getTax3Value());
            ChannelTransfersVO.setCommQuantity(value);
            long payableAmount = 0;
            long netPayableAmount = 0;
            // In case of FOC payableAmount will not be calculated
            /*
             * if(!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC))
             * {
             * payableAmount =
             * calculatorI.calculatePayableAmount(channelTransferItemsVO
             * .getUnitValue
             * (),Double.parseDouble(channelTransferItemsVO.getRequestedQuantity
             * ()),channelTransferItemsVO.getCommValue(),channelTransferItemsVO.
             * getDiscountValue());
             * netPayableAmount =
             * calculatorI.calculateNetPayableAmount(payableAmount
             * ,channelTransferItemsVO.getTax3Value());
             * }
             */
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode")) && POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !(_masterVO.getProperty("O2CTransferCode")
                .equalsIgnoreCase(p_txnType) || _masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))) {
            	System.out.println("Section 1 Entered");
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	ChannelTransfersVO.setPayableAmount(productCost);
            	ChannelTransfersVO.setNetPayableAmount(productCost);
            }// this is executed in case of Transfer and POSITIVE_COMM_APPLY is
             // true
            else if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode")) && (_masterVO.getProperty("O2CTransferCode")
                .equalsIgnoreCase(p_txnType) || _masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))) {
            	System.out.println("Section 2 Entered");
            	value = 0;
                value = calculatorI.calculateReceiverCreditQuantity(ChannelTransfersVO.getRequestedQuantity(), ChannelTransfersVO.getUnitValue(),
                		ChannelTransfersVO.getCommQuantity());
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setReceiverCreditQty(value);
                ChannelTransfersVO.setPayableAmount(productCost);
                ChannelTransfersVO.setNetPayableAmount(productCost);
                ChannelTransfersVO
                    .setProductTotalMRP(ChannelTransfersVO.getReceiverCreditQty() * ChannelTransfersVO.getUnitValue() / (SystemPreferences.MULTIPLICATIONFACTOR));
            }// executed in case of FOC or in case of POSITIVE_COMM_APPLY value
             // is false
            else if (!POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
            	System.out.println("Section 3 Entered");
            	payableAmount = calculatorI.calculatePayableAmount(ChannelTransfersVO.getUnitValue(), Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()),
                		ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getDiscountValue());
                netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, ChannelTransfersVO.getTax3Value());
                ChannelTransfersVO.setPayableAmount(payableAmount);
                ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
                ChannelTransfersVO.setProductTotalMRP(productCost);
            } else {
            	System.out.println("Section Else Entered");
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	ChannelTransfersVO.setPayableAmount(payableAmount);
            	ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
            }
            //channelTransferItemsVO.setApprovedQuantity(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
    }
    
    
    
	
	@Test
	public void testMain() throws Exception {
		calculateAmount("728555919865259", "C2CT", "POSTETOPUP", "4186.34");
		ChannelTransfersVO ChannelTransfersVO = new ChannelTransfersVO();
		System.out.println("Sender Debit Quantity: " + _parser.getDisplayAmount(ChannelTransfersVO.getSenderDebitQty()));
		System.out.println("Receiver Credit Quantity: " + _parser.getDisplayAmount(ChannelTransfersVO.getReceiverCreditQty()));
		System.out.println("Payable Amount: " + _parser.getDisplayAmount(ChannelTransfersVO.getPayableAmount()));
		System.out.println("Net Payable Amount: " + _parser.getDisplayAmount(ChannelTransfersVO.getNetPayableAmount()));
	}
}