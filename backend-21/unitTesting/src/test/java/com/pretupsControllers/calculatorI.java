package com.pretupsControllers;

import com.classes.BaseTest;
import com.commons.PretupsI;
import com.utils.Log;
import com.utils._parser;

public class calculatorI extends BaseTest{

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
     * @see com.btsl.pretups.util.OperatorUtilI#calculateCommission(String,
     *      double, long)
     */
    public static long calculateCommission(String p_commissionType, double p_commissionRate, long p_productCost) {
    	
    	Log.info("Entered calculateCommission() :: p_commissionType: " + p_commissionType + " p_rate: " + p_commissionRate + " p_cost: " + p_productCost);

        double commValue = 0;
        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(p_commissionType)) {
            commValue = (p_commissionRate / 100) * (p_productCost);
            commValue = Math.round(commValue);
        } else if (PretupsI.SYSTEM_AMOUNT.equals(p_commissionType)) {
            commValue = (p_commissionRate);
        } else {
        	Log.info(p_commissionType + " is not defined in the system");
        }

        return (long) commValue;
    }
    
    
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
     * @see com.btsl.pretups.util.OperatorUtilI#calculateDiscount(String,
     *      double, long)
     */
    public static long calculateDiscount(String p_discountType, double p_discountRate, long p_productCost) {

        double discountValue = 0;
        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(p_discountType)) {
            discountValue = (p_discountRate / 100) * (p_productCost);
        } else if (PretupsI.SYSTEM_AMOUNT.equals(p_discountType)) {
            discountValue = (long) (p_discountRate);
        } else {
        	Log.info(p_discountType + " is not defined in the system");
        }

        return (long) discountValue;
    }
    
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
     * @see com.btsl.pretups.util.OperatorUtilI#calculateTax1(String, double,
     *      long)
     */
    public static long calculateTax1(String p_type, double p_rate, long p_productCost) {
        double taxCalculatedValue = 0;
        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(p_type)) {
            taxCalculatedValue = (p_rate / (100 + p_rate)) * (p_productCost);
            taxCalculatedValue = Math.round(taxCalculatedValue);
        } else if (PretupsI.SYSTEM_AMOUNT.equals(p_type)) {
            taxCalculatedValue = (long) (p_rate);
        } else {

        }

        return (long) taxCalculatedValue;
    }
    
    
    /**
     * Method calculateTax2.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_value
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#calculateTax2(String, double,
     *      long)
     */
    public static long calculateTax2(String p_type, double p_rate, long p_value) {

        double taxCalculatedValue = 0;
        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(p_type)) {
            taxCalculatedValue = (p_rate / 100) * p_value;
            taxCalculatedValue = Math.round(taxCalculatedValue);
        } else if (PretupsI.SYSTEM_AMOUNT.equals(p_type)) {
            taxCalculatedValue = (long) (p_rate);
        } else {
        }

        return (long) taxCalculatedValue;
    }
    
    
    /**
     * Method calculateTax3.
     * 
     * @param p_type
     *            String
     * @param p_rate
     * @param p_rate
     *            double
     * @param p_value
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#calculateTax3(String, double,
     *      long)
     */
    public static long calculateTax3(String p_type, double p_rate, long p_value) {
        double taxCalculatedValue = 0;
        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(p_type)) {
            taxCalculatedValue = (p_rate / 100) * (p_value);
            taxCalculatedValue = Math.round(taxCalculatedValue);
        } else if (PretupsI.SYSTEM_AMOUNT.equals(p_type)) {
            taxCalculatedValue = (long) (p_rate);
        } else {

        }

        return (long) taxCalculatedValue;
    }
    
    
    /**
     * Method calculateCommissionQuantity.
     * 
     * @param p_commissionType
     *            String
     * @param p_commissionRate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#calculateCommission(long, long)
     */
    public static long calculateCommissionQuantity(long p_commisionValue, long p_unitValue, long p_tax3Value) {

        double commissionQuantity = 0;
        commissionQuantity = p_commisionValue - p_tax3Value;
        commissionQuantity = _parser.getSystemAmount((commissionQuantity / p_unitValue));
        Log.info("comm Quantity:"+commissionQuantity);
        return (long) commissionQuantity;
    }
    
    
    /**
     * Method calculateSenderDebitQuantity.
     * 
     * @param p_commissionType
     *            String
     * @param p_commissionRate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#calculateCommission(long, long)
     */
    public static long calculateReceiverCreditQuantity(String p_requestedQty, long p_unitValue, long p_commisionQty) {
    	final String methodname = "calculateReceiverCreditQuantity";
    	Log.debug("Entered " + methodname + "(" + p_requestedQty + ", " +  p_unitValue + ", " + p_commisionQty + ")");
        long receiverCreditQuantity = 0, requestedQuantity = 0;

        requestedQuantity = _parser.getSystemAmount(p_requestedQty);
        receiverCreditQuantity = requestedQuantity + p_commisionQty;

        Log.debug(methodname + " Returns: receiverCreditQuantity(" + receiverCreditQuantity + ")");
        Log.debug("Exiting " + methodname + "()");
        return receiverCreditQuantity;
    }
    
    
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
     * @see com.btsl.pretups.util.OperatorUtilI#calculatePayableAmount(long,
     *      double, long, long)
     */
    public static long calculatePayableAmount(long p_unitValue, double p_requestedQuantity, long p_commissionValue, long p_discountValue) {
    	final String methodname = "calculatePayableAmount";
    	Log.debug("Enetered " + methodname + "(" + p_unitValue + ", " + p_requestedQuantity + ", " + p_commissionValue + ", " + p_discountValue + ")");

        final long payableAmount = (long) (p_unitValue * p_requestedQuantity - p_commissionValue - p_discountValue);

        Log.debug(methodname + " Returns: payableAmount(" + payableAmount + ")");
        Log.debug("Exiting " + methodname + "()");
        return payableAmount;
    }
    
    /**
     * Method calculateNetPayableAmount.
     * 
     * @param p_payableAmount
     *            long
     * @param p_tax3Value
     *            long
     * @return long
     * @see com.btsl.pretups.util.OperatorUtilI#calculateNetPayableAmount(long,
     *      long)
     */
    public static long calculateNetPayableAmount(long p_payableAmount, long p_tax3Value) {
    	final String methodname = "calculateNetPayableAmount";
    	Log.debug("Enetered " + methodname + "(" + p_payableAmount + ", " + p_tax3Value + ")");
    	
        final long netPayableAmount = p_payableAmount + p_tax3Value;

        Log.debug(methodname + " Returns: netPayableAmount(" + netPayableAmount + ")");
        Log.debug("Exiting " + methodname + "()");
        return netPayableAmount;
    }
    
    public static long calculateOTFComm(String p_type, double p_rate, long p_requestValue) {
        long calculatedOTFValue = 0;
        if(!BTSLUtil.isNullString(p_type)){
	        if (PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(p_type)) {
	        	calculatedOTFValue = (long) p_rate;
	        } else if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(p_type)) {
	        	calculatedOTFValue = (long) ((p_requestValue * ((p_rate) / 100)));
	        } else { }
        }
        return calculatedOTFValue;
    }
    
    public static long calculateOtherCommission(String p_type, double p_rate, long p_requestValue) {
    	long otherCommValue = 0;
    	Log.info("calculateOtherCommission :: p_type-" + p_type + " p_rate-" + p_rate + " p_requestValue-" + p_requestValue);
        if(!BTSLUtil.isNullString(p_type)){
	        if (PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(p_type)) {
	        	otherCommValue = (long) p_rate;
	        } else if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(p_type)) {
	        	otherCommValue = (long) ((p_requestValue *p_rate) / 100);
	        	otherCommValue = Math.round(otherCommValue);
	        } else { }
        }
        Log.info("Calculated other commission value: " + otherCommValue);
        return otherCommValue;
    }
}
