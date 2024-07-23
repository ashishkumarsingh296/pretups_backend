package com.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.commons.SystemPreferences;

public class _parser {

	private static long _longValue;

	public _parser convertStringToLong(String value) {
		_parser t = new _parser();
		BigInteger BIntVal= new BigInteger(value); 
        BigDecimal BDecimalVal = new BigDecimal(BIntVal);
		setValue(BDecimalVal.longValue());
		return t;
	}
	
	public void changeDenomation() {
		setValue(getValue()/100);
	}
	
	public long getValue() {
		return _longValue;
	}

	public static void setValue(long testValue) {
		_parser._longValue = testValue;
	}
	
    /**
     * Get Display Amount
     * 
     * @param p_amount
     * @return
     * @throws BTSLBaseException
     */
    public static String getDisplayAmount(long p_amount) {
        final double amount = (double) p_amount / (double) SystemPreferences.MULTIPLICATIONFACTOR;
        String amountStr = new DecimalFormat("#############.#####").format(amount);
        try {
            final long l = Long.parseLong(amountStr);
            amountStr = String.valueOf(l);
        } catch (Exception e) {
            amountStr = new DecimalFormat("############0.00#").format(amount);
        }
        return amountStr;
    }
    
    /**
     * Get System Amount
     * 
     * @param p_amountStr
     * @return
     * @throws BTSLBaseException
     */
    public static long getSystemAmount(double p_validAmount){
        long amount = 0;
        amount = (long) (Round((p_validAmount * SystemPreferences.MULTIPLICATIONFACTOR), 2));
        return amount;
    }
    
    
    /**
     * Get System Amount
     * 
     * @param p_amountStr
     * @return
     * @throws BTSLBaseException
     */
    public static long getSystemAmount(String p_amountStr) {
        long amount = 0;
        try {
            final double p_validAmount = Double.parseDouble(p_amountStr);
            amount = getSystemAmount(p_validAmount);
        } catch (Exception e) {
        	Log.writeStackTrace(e);
        }
        return amount;
    }
    
    
    /**
     * Method to round values till precision
     * 
     * @param Rval
     * @param Rpl
     * @return
     */
    public static double Round(double Rval, int Rpl) {
        final double p = Math.pow(10, Rpl);
        Rval = Rval * p;
        final double tmp = Math.round(Rval);
        return tmp / p;
    }
    
    
    public static BigDecimal currencyHandler(String amount, Locale locale) {
		try {
	    final NumberFormat format = NumberFormat.getNumberInstance(locale);
	    if (format instanceof DecimalFormat) {
	        ((DecimalFormat) format).setParseBigDecimal(true);
	    }
	    return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]",""));
		}
		catch(Exception e) {
			Log.writeStackTrace(e);
			return null;
		}
	}

}
