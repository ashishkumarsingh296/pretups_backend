package com.pretupsControllers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.dbrepository.DBHandler;
import com.google.common.base.Joiner;

import bsh.ParseException;

public class BTSLUtil {

	  /**
     * Validates the name of the file being uploaded
     * 
     * @param String
     *            and Date
     * @date : 14-08-2014
     * @return boolean
     */
    /**
     * @param value
     * @param date
     * @return
     */
    public static boolean timeRangeValidation(String value, Date date) {
        boolean validate = false;
        if (value == null || value.length() == 0) {
            return true;
        }

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false);
            final String[] dateString = sdf.format(date).split(":");
            final int dm = Integer.parseInt(dateString[0]) * 60 + Integer.parseInt(dateString[1]);
            final String[] commaSepatated = value.split(","); // String []
            if (commaSepatated.length > 0) {
                for (int i = 0; i < commaSepatated.length; i++) {
                    final String[] hyphenSeparated = commaSepatated[i].split("-");
                    if (hyphenSeparated.length == 2) {
                        final String[] current1 = hyphenSeparated[0].split(":");
                        final String[] current2 = hyphenSeparated[1].split(":");
                        if (Integer.parseInt(current1[0]) * 60 + Integer.parseInt(current1[1]) <= dm && dm < Integer.parseInt(current2[0]) * 60 + Integer
                            .parseInt(current2[1])) {
                            validate = true;
                            break;
                        }
                    }
                }
            } else {
                validate = true;
            }
        } catch (Exception e) {
        }
        return validate;
    }
    
    /**
     * Get DateTime String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateTimeStringFromDate(Date date) {
        String format = DBHandler.AccessHandler.getSystemPreference("SYSTEM_DATETIME_FORMAT");
        if (isNullString(format)) {
            format = "dd/MM/yy HH:mm";
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }
    
    /**
     * Is Null String
     * 
     * @param str
     * @return
     */
    public static boolean isNullString(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public static String formatDouble(Double balance)
    {
    	if(balance == 0d)
    		return "0";
    	
    	DecimalFormat decim = new DecimalFormat("0.00");
        return (decim.format(balance)).toString();
    }
    
    public static Map<String, String> getQueryMap(String query)  {  
	    String[] params = query.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params)  
	    {  
	    	String[] paramMap = param.split("=");
	        String name = paramMap[0];
	        String value = "";
	        if(paramMap.length > 1) {
	        	value = paramMap[1];	        	
	        }
	        map.put(name, value);  
	    }  
	    return map;  
    }
    
    public static Map<String, String> getSMSCQueryMap(String query)  {  
	    String[] params = query.split(":");  
	    Map<String, String> map = new HashMap<String, String>(); 
	    map.put("TXNSTATUS", params[1]);
//	    map.put("TXNMESSAGE", params[2]);
	    return map;  
    }
    
    public static String getQueryString(String query, HashMap<String, String> apiDate)  {  
    	String mapJoined = Joiner.on("&").withKeyValueSeparator("=")
                .join(apiDate);
        return query + "&"+ mapJoined;
    }
    
    public static String getQueryString(HashMap<String, String> apiDate)  {  
    	String mapJoined = Joiner.on("&").withKeyValueSeparator("=")
                .join(apiDate);
        return mapJoined;
    }
    public static double getDisplayAmount(double p_amount) {
        final int multiplicationFactor = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("AMOUNT_MULT_FACTOR"));
        double amount = 0d;
        try {
            amount = p_amount / multiplicationFactor;
        } catch (Exception e) { }
        return amount;
    }
    
    
    public static String getRandomNumber(int digCount) {
    	Random rnd = new Random();
        StringBuilder sb = new StringBuilder(digCount);
        for(int i=0; i < digCount; i++)
            sb.append((char)('0' + rnd.nextInt(10)));
        return sb.toString();
    }
    
    
}
