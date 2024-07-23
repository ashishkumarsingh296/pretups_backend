package com.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    
    public static String getTransactionID(String message, String transactionIDKey) {
    	String pattern = null;
    	int clientTransactionParam = Integer.parseInt(_masterVO.getClientDetail("TRANSACTIONIDFORMAT"));
    	String NetworkCode = _masterVO.getMasterValue("Network Code");
    	if (clientTransactionParam == 0)
    		pattern = transactionIDKey + "[A-Z]*[.[0-9]*[a-zA-Z]*]*[^ .]";
    	else if (clientTransactionParam == 1)
    		pattern = NetworkCode + "[A-Z]*[.[0-9]*[a-zA-Z]*]*[^ .]";
    	else
    		Log.error("Invalid Client Library Parameter Configuration For: TRANSACTIONIDFORMAT - [Expected: 0 / 1 but Found" + clientTransactionParam + "]");
    	try {
	    	Pattern r = Pattern.compile(pattern);
	    	Matcher m = r.matcher(message);
	    	m.find();
	    	Log.info("Trasaction ID fetched as: " + m.group(0));
	    	return m.group(0);
    	} catch (Exception e) {
    		Log.info("Error while fetching TransactionID from: " + message);
    		return null;
    	}
    }
    
 

        /**
         * Convert a result set into a JSON Array
         * @param resultSet
         * @return a JSONArray
         * @throws Exception
         */
        public static JSONArray convertToJSON(ResultSet resultSet) {
            JSONArray jsonArray = new JSONArray();
            try {
				while (resultSet.next()) {
				    int total_rows = resultSet.getMetaData().getColumnCount();
				    for (int i = 0; i < total_rows; i++) {
				        JSONObject obj = new JSONObject();
				        obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
				                .toLowerCase(), resultSet.getObject(i + 1));
				        jsonArray.put(obj);
				    }
				}
			} catch (JSONException e) {
				Log.debug("JSONException while converting resultSet to JSON");
			} catch (SQLException e) {
				Log.debug("SQLException while converting resultSet to JSON");
			}
            return jsonArray;
        }
        
        /**
         * Convert a result set into a XML List
         * @param resultSet
         * @return a XML String with list elements
         * @throws Exception if something happens
         */
        public static String convertToXML(ResultSet resultSet)
                throws Exception {
            StringBuffer xmlArray = new StringBuffer("<results>");
            while (resultSet.next()) {
                int total_rows = resultSet.getMetaData().getColumnCount();
                xmlArray.append("<result ");
                for (int i = 0; i < total_rows; i++) {
                    xmlArray.append(" " + resultSet.getMetaData().getColumnLabel(i + 1)
                    .toLowerCase() + "='" + resultSet.getObject(i + 1) + "'"); }
                xmlArray.append(" />");
            }
            xmlArray.append("</results>");
            return xmlArray.toString();
        }
        
        public static HashMap<String, String> convertToHashMap(Object[][] obj, int key, int value) {
        	final String methodname = "convertToHashMap";
        	Log.debug("Entered " + methodname + "("+ obj +", "+ key +", "+ value +")");
        	HashMap<String, String> returnObj = new HashMap<String, String>();
        	for (int i=0; i < obj.length; i++) {
        		returnObj.put(obj[i][key].toString(), obj[i][value].toString());
        	}
        	Log.debug("Exiting " + methodname + "(" + Arrays.asList(returnObj) + ")");
        	return returnObj;
        }
		
		
		public static String getBatchID(String message, String transactionIDKey) {
		String pattern = null;
		int clientTransactionParam = Integer.parseInt(_masterVO.getClientDetail("TRANSACTIONIDFORMAT"));
		String NetworkCode = _masterVO.getMasterValue("Network Code");
		if (clientTransactionParam == 0)
			pattern = transactionIDKey + "[A-Z]*[.[0-9]*[a-zA-Z]*]*[^ .)]";
		else if (clientTransactionParam == 1)
			pattern = NetworkCode + "[A-Z]*[.[0-9]*[a-zA-Z]*]*[^ .]";
		else
			Log.error("Invalid Client Library Parameter Configuration For: TRANSACTIONIDFORMAT - [Expected: 0 / 1 but Found" + clientTransactionParam + "]");
		try {
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(message);
			m.find();
			Log.info("Batch ID fetched as: " + m.group(0));
			return m.group(0);
		} catch (Exception e) {
			Log.info("Error while fetching Batch ID from: " + message);
			return null;
		}
	}

}
