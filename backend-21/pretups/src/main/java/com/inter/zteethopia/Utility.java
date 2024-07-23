// ***********************************************************************
// *
// * Pre To up System: Copyright(c) 2002 Cellcloud, Inc
// * All Rights Reserved
// * This program is an unpublished copyrighted work which is proprietary
// * to Cellcloud, Inc. and contains confidential information that is not
// * to be reproduced or disclosed to any other person or entity without
// * prior written consent from Cellcloud, Inc. in each and every instance.
// *
// * Utility.java
// ***********************************************************************
/*
 * History
 * 
 * Date Author Changes Modified
 * ======== ==================== =================== ====================
 * 25/04/02 Supratim,Nikhil Created
 */
package com.inter.zteethopia;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

public class Utility {
    private Vector retVect = new Vector();
    private StringTokenizer st;
    private Hashtable ht;
    final boolean SUCCESS = true;
    final boolean FAILURE = false;

    static private final String hex = "0123456789abcdef";

    /**
     * Get the value for specific string dilimiter.
     */
    public Vector getStringToken(String parseStr, String dim) {
        retVect.clear();

        // Condition for check the delimiter at end.
        // If condition is satisfied then neglect end delimiter
        if (parseStr.endsWith(dim)) {
            parseStr = parseStr.substring(0, parseStr.lastIndexOf(dim));
            st = new StringTokenizer(parseStr, dim);
            while (st.hasMoreTokens()) {
                retVect.addElement(st.nextToken());
            }// close while

        }// close if
        else {
            st = new StringTokenizer(parseStr, dim);
            while (st.hasMoreTokens()) {
                retVect.addElement(st.nextToken());
            }// close while
        }
        return retVect;
    }// closing getStringToken method

    /**
     * Convert Vector object into Hashtable object.
     */
    public Hashtable getCoversionOfVectToHash(Vector vect) {
        ht = new Hashtable();
        for (int i = 0; i < vect.size(); i++) {
            if (i % 2 == 1) {
                ht.put(vect.elementAt(i - 1), vect.elementAt(i));
            } // End of if Statement
        } // End of for loop
        return ht;
    }// End of getCoversionOfVectToHash method

    /**
     * Convert Hash to String
     */
    public String getHashToString(Hashtable hash) {
        Vector keyVect = new Vector();
        String msg = "";

        for (Enumeration enumKey = hash.keys(); enumKey.hasMoreElements();) {
            keyVect.addElement(enumKey.nextElement());
        }
        for (int i = 0; i < keyVect.size(); i++) {
            msg += keyVect.elementAt(i).toString() + "=";
            msg += hash.get(keyVect.elementAt(i).toString()) + "&";
        }
        // msg = msg.substring(0,msg.lastIndexOf("&"));
        return msg;
    }

    /**
     * This method converts a string in the format "a=1&b=2&c=3" into a
     * hashtable in the format "{a=1, b=2, c=3}"
     * 
     * @param str
     *            The string to be converted to hash
     * @param token1
     *            The tokenizer : "&"
     * @param token2
     *            The tokenizer : "="
     * @return Returns Hashtable representation of the String passed
     */
    public Hashtable getStringToHash(String str, String token1, String token2) {
        Hashtable ht = new Hashtable();
        StringTokenizer stToken1 = null;
        StringTokenizer stToken2 = null;
        String newString = "";
        Vector vectData = new Vector();
        stToken1 = new StringTokenizer(str, token1);
        while (stToken1.hasMoreTokens()) {
            newString = stToken1.nextToken();
            stToken2 = new StringTokenizer(newString, token2);
            while (stToken2.hasMoreTokens()) {
                vectData.addElement(stToken2.nextToken());
            }
        }

        for (int i = 0; i < vectData.size(); i++) {
            if (i % 2 == 1) {
                ht.put(vectData.elementAt(i - 1), vectData.elementAt(i));
            } // End of if Statement
        } // End of for loop
        return ht;
    }

    /**
     * Conversion of CCYYMMDDHHMISS fromat of string to YYYY/MM/DD HH24:MI:SS
     * format
     */
    public String convStringDate(String DateStr) {
        String yyStr = DateStr.substring(0, 4); // This for YYYY
        String mmStr = DateStr.substring(4, 6); // This for mm
        String ddStr = DateStr.substring(6, 8); // This for dd
        String hhStr = DateStr.substring(8, 10); // This for HH24
        String miStr = DateStr.substring(10, 12); // This for mi
        String ssStr = DateStr.substring(12, 14); // This for ss
        String dateForamt = yyStr + "-" + mmStr + "-" + ddStr + " " + hhStr + ":" + miStr + ":" + ssStr;

        return dateForamt;
    }

    /**
     * Compare new and old Hashtable and insert not existing record in
     * hashtable.
     */
    public Hashtable compareHash(Hashtable oldHash, Hashtable newHash) {
        String keyStr = "";

        for (Enumeration enumKey = newHash.keys(); enumKey.hasMoreElements();) {
            keyStr = enumKey.nextElement().toString();
            // System.out.println("KEY--> "+keyStr);
            // if (! newHash.containsKey(keyStr)) {
            oldHash.put(keyStr, newHash.get(keyStr));
            // }
        }
        return oldHash;
    }

    /**
     * This method is use to get Current Date.
     */
    public String getCurrentDate() {
        Date dt = (Calendar.getInstance()).getTime();

        String dateStr = "";

        int year = dt.getYear();
        int month = dt.getMonth();
        int date = dt.getDate();
        int hour = dt.getHours();
        int min = dt.getMinutes();
        int sec = dt.getSeconds();

        String monthStr = "";
        String dStr = "";
        String hourStr = "";
        String minStr = "";
        String secStr = "";
        String yearStr = "";

        year = year - 100 + 2000;
        month++;

        yearStr = "" + year;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = "" + month;
        }

        if (date < 10) {
            dStr = "0" + date;
        } else {
            dStr = "" + date;
        }

        if (hour < 10) {
            hourStr = "0" + hour;
        } else {
            hourStr = "" + hour;
        }

        if (min < 10) {
            minStr = "0" + min;
        } else {
            minStr = "" + min;
        }

        if (sec < 10) {
            secStr = "0" + sec;
        } else {
            secStr = "" + sec;
        }

        // dateStr = year+"-"+month+"-"+date+" "+hour+":"+min+":"+sec;
        dateStr = yearStr + "-" + monthStr + "-" + dStr + " " + hourStr + ":" + minStr + ":" + secStr;
        return dateStr;
    }

    /**
     * This method is useful to get current time and date as YYYYMMDDTHH24MISS
     */
    public String currentDateTime() {
        // String s;
        Calendar calender = Calendar.getInstance();
        String str = "";
        String s = "";
        int t = 0;
        t = calender.get(Calendar.YEAR);
        s = s + t;

        t = calender.get(Calendar.MONTH);
        t++;
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }

        t = calender.get(Calendar.DATE);
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }

        s += "T";

        t = calender.get(Calendar.HOUR_OF_DAY);
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }

        t = calender.get(Calendar.MINUTE);
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }

        t = calender.get(Calendar.SECOND);
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }
        return s;
    }

    /**
     * Get the time in seconds from 01/01/1970 to current date
     */
    public long getTime() {
        Date dt = new Date();
        long milli = 0;

        milli = dt.getTime();
        milli = milli / 1000;

        return milli;
    }

    /**
     * This method is use to convert normal message to MD5 type encrypted
     * fromat.
     */

    public String getMD5(String passStr) {
        // get byte from string
        byte buf[] = passStr.getBytes();

        // Set up the java.security MD5 & it's digest result
        MessageDigest smd = null;
        int bcount = buf.length;
        try {
            smd = MessageDigest.getInstance("MD5"); // java.security MD5
            byte sdg[] = new byte[16];
        } catch (NoSuchAlgorithmException nsae) {
            // System.out.println("Can't find MD5");
            // System.exit(1);
        }
        byte sdg[];
        smd.update(buf, 0, bcount);

        sdg = smd.digest();
        String ret = hexString(sdg);
        ret = ret.replaceAll(" ", "");
        return ret;

    }

    // Convert bytes into a hex string.
    static private String hexString(byte[] vb) {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < vb.length; j++) {
            sb.append(hex.charAt((int) (vb[j] >> 4) & 0xf));
            sb.append(hex.charAt((int) (vb[j]) & 0xf));
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Padding zeros to the number.
     */
    public String zeroPad(String msg, int pad) {
        if (msg.length() < pad) {
            int paddingLength = pad - msg.length();
            for (int i = 0; i < paddingLength; i++) {
                // //System.out.println("LENGTH--->"+i+" adding zeros "+(pad-msg.length()));
                msg = "0" + msg;
            }
        }
        return msg;
    }

    public static String get(Hashtable hash, String key) {
        if (hash.get(key).toString() != null) {
            return (hash.get(key).toString().trim());
        } else {
            return ("");
        }
    }

    /**
     * Executes a given command by spawning a new shell and returns back the
     * output
     * 
     * @param cmd
     *            This is the Command to be executed in the shell
     * @return boolean Return true if the command executed successfully else
     *         return false
     * @Author Parth Suthar
     */
    public boolean runCommand(String subcmd) {
        Runtime rt = null;
        Process p = null;

        try {
            rt = Runtime.getRuntime();

            String subCmd = subcmd;
            String cmd[] = { "/bin/sh", "-c", subCmd };
            p = rt.exec(cmd);
            p.waitFor();

            if (p.exitValue() == 0)
                return SUCCESS;
        } catch (Exception e) {
        }

        return FAILURE;
    }

    /**
     * Tokenize a string on given token and Returns an array of String
     * 
     * @param stringToTokenize
     *            The string to be tokenized
     * @param token
     *            The token
     * @return Returns an array of String if successful else null if parameters
     *         are null or empty strings
     * @Author Parth Suthar
     */
    public String[] returnStringArray(String stringToTokenize, String token) {
        int tokenCount = 0;
        StringTokenizer strToken = null;
        String temp = null;
        String tokens[] = { "" };
        int count = 0;

        if (stringToTokenize == null || token == null)
            return tokens;
        else if (stringToTokenize.equals("") || token.equals(""))
            return tokens;

        strToken = new StringTokenizer(stringToTokenize, token);
        tokenCount = strToken.countTokens();

        tokens = new String[tokenCount];

        while (strToken.hasMoreTokens()) {
            temp = strToken.nextToken();
            tokens[count++] = temp;
        }

        return tokens;
    }

    /**
     * Generates Random number based on the length and digits passed as
     * parameters
     * 
     * @param maxLength
     *            The Maximum lenght of the Random number
     * @param maxDigits
     *            The Maximum digits in the Random number, rest of the length is
     *            padded by 0
     * @return Returns a random number as string
     * @Author Parth Suthar
     */
    public String getRandVal(int maxLength, int maxDigits) {
        int temp = 0;
        String rand_numb = "";
        double rand_val = Math.random();
        int multiplier = 0;

        if (maxDigits == 0)
            multiplier = (int) (Math.random() * 10);
        else
            multiplier = maxDigits;

        if (multiplier == 0)
            multiplier = 1;
        else if (multiplier > maxLength)
            multiplier = maxLength;

        for (int i = 0; i < multiplier; i++)
            rand_val = rand_val * 10;

        temp = (int) rand_val;
        rand_numb = "" + temp;

        int val = rand_numb.length();
        if (val < maxLength)
            rand_numb = Utility.getPaddedString(rand_numb, '0', maxLength - val, false);

        return rand_numb;
    }

    /**
     * Method for Converting seconds to hh24:mi:ss format and Addition of two
     * different date
     * Input 1: Date in yyyy-mm-dd hh24:mi:ss
     * Output : Date in yyyymmddhh24:mi:ss
     */
    public long ConvrtDates(String dt) {
        long dateLong = 0;

        dt = dt.replaceAll("-", "");
        dt = dt.replaceAll(" ", "");
        dt = dt.replaceAll(":", "");

        try {
            dateLong = Long.parseLong(dt);
            return dateLong;
        } catch (NumberFormatException num) {
            return 0;
        }

    }// End of AddDate Method

    /**
     * Get Current Date Time in format of CCYYMMDDHH24MISS
     */

    public String getCurrentDateTime() {

        String curr = "";
        // Calling method currentDateTime () to get Current Date Time with
        // Having "T"
        curr = currentDateTime();
        curr = curr.replaceAll("T", "");
        return curr;
    }// End of getCurrentDateTime

    /**
     * Returns the String Padded with the given character
     * direction of padding specified by parameter dir, true appends at end,
     * false at beginning
     */
    public static String getPaddedString(String str, char padVal, int times, boolean dir) {
        StringBuffer temp = new StringBuffer(str);

        if (dir) {
            for (int i = 0; i < times; i++)
                temp.append(padVal);
        } else {
            temp.reverse();
            for (int i = 0; i < times; i++)
                temp.append(padVal);
            temp.reverse();
        }
        return temp.toString();
    }

    /**
     * This method is used to add or subtract date parameters.
     * 
     * @param strDateTime
     *            : The date string to be manipulated. Valid format is:
     *            "yyyymmddhh24miss".
     * @param strParam
     *            : The date string parameter that is to be manipulated. Valid
     *            parameters are
     *            "dd" - Number of days to add or subtract,
     *            "hh" - The number of hours to add or subtract,
     *            "mi" - The number of minutes to add or subtract,
     *            "ss" - The number of seconds to add or subtract.
     * @param strValue
     *            : The value for the corresponding strParam parameter.
     * @return Returns manipulated date in the string format of
     *         "yyyy-mm-dd hh24:mi:ss".
     */
    public static String addDate(String strDateTime, String strValue, String strParam) {

        strDateTime = strDateTime.trim();

        int intYear = Integer.parseInt(strDateTime.substring(0, 4)); // This for
                                                                     // YYYY
        int intMonth = Integer.parseInt(strDateTime.substring(4, 6)); // This
                                                                      // for mm
        int intDay = Integer.parseInt(strDateTime.substring(6, 8)); // This for
                                                                    // dd
        int intHour = Integer.parseInt(strDateTime.substring(8, 10)); // This
                                                                      // for
                                                                      // HH24
        int intMin = Integer.parseInt(strDateTime.substring(10, 12)); // This
                                                                      // for mi
        int intSec = Integer.parseInt(strDateTime.substring(12, 14)); // This
                                                                      // for ss

        GregorianCalendar gregCal = new GregorianCalendar(intYear, intMonth - 1, intDay, intHour, intMin, intSec);

        if (strParam.equalsIgnoreCase("dd")) {
            // //System.out.println("In dd");
            gregCal.add(Calendar.DATE, Integer.parseInt(strValue));
        }

        if (strParam.equalsIgnoreCase("hh")) {
            // //System.out.println("In hh");
            gregCal.add(Calendar.HOUR, Integer.parseInt(strValue));
        }

        if (strParam.equalsIgnoreCase("mi")) {
            // //System.out.println("In mi");
            gregCal.add(Calendar.MINUTE, Integer.parseInt(strValue));
        }

        if (strParam.equalsIgnoreCase("ss")) {
            // //System.out.println("In ss");
            gregCal.add(Calendar.SECOND, Integer.parseInt(strValue));
        }

        String strDay = String.valueOf(gregCal.get(Calendar.DATE));
        if (strDay.length() < 2) {
            strDay = "0" + strDay;
        }

        String strMonth = String.valueOf(gregCal.get(Calendar.MONTH) + 1);
        if (strMonth.length() < 2) {
            strMonth = "0" + strMonth;
        }

        String strYear = String.valueOf(gregCal.get(Calendar.YEAR));

        String strSec = String.valueOf(gregCal.get(Calendar.SECOND));
        if (strSec.length() < 2) {
            strSec = "0" + strSec;
        }

        String strMin = String.valueOf(gregCal.get(Calendar.MINUTE));
        if (strMin.length() < 2) {
            strMin = "0" + strMin;
        }

        String strHour = String.valueOf(gregCal.get(Calendar.HOUR_OF_DAY));
        if (strHour.length() < 2) {
            strHour = "0" + strHour;
        }

        String strNewDateTime = strYear + "-" + strMonth + "-" + strDay + " " + strHour + ":" + strMin + ":" + strSec;

        return strNewDateTime;
    }

    /**
     * Get the time in milli sec from 01/01/1970 to specific date
     */
    public long getTime(String days) {

        long daysLong = 0;
        long miDateLong = 0;
        long milliLong = 0;

        daysLong = Long.parseLong(days);
        miDateLong = daysLong * 24 * 60 * 60 * 1000;
        Date dtLong = new Date(miDateLong);
        milliLong = dtLong.getTime();
        milliLong = milliLong / 1000;

        return milliLong;
    }

    /**
     * Get the new unix timestamp value after adding the number of days
     */
    public long getAddedTime(String days) {
        long milli = 0;
        long miDateLong = 0;
        long totMilli = 0;

        Date dt = new Date();
        milli = dt.getTime();
        milli = milli / 1000;

        miDateLong = getTime(days);

        totMilli = milli + miDateLong;

        return totMilli;
    }

    /**
     * Get the unix timestamp value by providing the date is YYYYMMDD.
     */
    public long getUnixTime(String dateStr) {
        String yyStr = dateStr.substring(0, 4);
        String mmStr = dateStr.substring(4, 6);
        String ssStr = dateStr.substring(6, 8);

        // //System.out.println(yyStr+"---"+mmStr+"---"+ssStr);

        long unixTime = 0;

        int yyInt = 0;
        int mmInt = 0;
        int ssInt = 0;

        yyInt = Integer.parseInt(yyStr);
        mmInt = Integer.parseInt(mmStr) - 1;
        ssInt = Integer.parseInt(ssStr);

        Calendar calendar = new GregorianCalendar(yyInt, mmInt, ssInt);

        Date dt = calendar.getTime();
        // //System.out.println("DATE---------->"+dt.toString());

        unixTime = dt.getTime();

        unixTime = unixTime / 1000;

        return unixTime;
    }

    public static void main(String args[]) {
        Utility utilObj = new Utility();
        try {
            Date dt = new Date();
            String strDt = convertDateToDateString(dt);
            // System.out.println("val of dt string="+strDt);

        } catch (Exception ex) {
        }

        /*
         * Hashtable ht = new Hashtable ();
         * Hashtable newHash = new Hashtable ();
         * 
         * newHash.put("Nikhil","Rajaram");
         * newHash.put("Bhakti","Rutali");
         * 
         * ht.put("Nikhil","Chavan");
         * ht.put("kavita","Ghag");
         * ht.put("Parth","Sutar");
         * ht.put("Supratim","Chandra");
         * 
         * ht = utilObj.compareHash(ht,newHash);
         * 
         * System.out.println("HASHTABLE----------->"+ht.toString());
         * 
         * 
         * /*Hashtable newHash = new Hashtable();
         * newHash.put("Nikhil","Rajaram");
         * newHash.put("Bhakti","Rutali");
         * 
         * String d = "20020416121212";
         * //System.out.println("Date Format: "+utilObj.convStringDate(d));
         * 
         * Vector vect = new Vector();
         * vect.addElement("Nikhil");
         * vect.addElement("Chavan");
         * vect.addElement("Vikas");
         * vect.addElement("BV");
         * 
         * Hashtable ht = utilObj.getCoversionOfVectToHash(vect);
         * 
         * //System.out.println("Conversion of Vector: "+ht.get("Nikhil"));
         * //System.out.println("Conversion of Vector: "+ht.get("Vikas"));
         * 
         * d = utilObj.getHashToString(ht);
         * 
         * ////System.out.println("Converted String from Hash: "+d);
         * Hashtable htt = utilObj.compareHash(ht,newHash);
         * 
         * ////System.out.println("---- "+ht.get("Nikhil"));
         * ////System.out.println("---- "+ht.get("Vikas"));
         * ////System.out.println("---- "+ht.get("Bhakti"));
         */
        // System.out.println(utilObj.getCurrentDate());

        // System.out.println(utilObj.currentDateTime());

        // System.out.println("cellcloud-------------->"+utilObj.getMD5("cellcloud"));
        // System.out.println("pretupsh_live-------------->"+utilObj.getMD5("pretupsh_live"));
        // //System.out.println("superuser------------------->"+utilObj.getMD5("superuser"));
        // //System.out.println(utilObj.zeroPad("100",6));

        Hashtable stHash = utilObj.getStringToHash("a=1&b=2&c=3", "&", "=");
        // //System.out.println("String To Hash : " + stHash);

        // //System.out.println("STRING WITHOUT CHARACTER-------->"+utilObj.ConvrtDates("2002-12-09 23:44:55"));
        String asdf = utilObj.getCurrentDateTime();
        // System.out.println("Get Current date without char -----------------> "+asdf);

        // System.out.println("UNIX------------->"+utilObj.getUnixTime("20030710"));
    }

    /**
     * converts date to a string in yyyymmdd format
     * 
     * @param Date
     *            to be converted
     * @return String in Indian Format
     */
    public static String convertDateToYYYYMMDDString(java.util.Date d) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false); // this is required else it will convert
        String dateString = sdf.format(d);
        return dateString;
    }

    /**
     * converts date to a string in yyyymmdd format
     * 
     * @param Date
     *            to be converted
     * @return String in Indian Format
     */
    public static String convertDateToDateString(java.util.Date d) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd'/'MM'/'yyyy");
        sdf.setLenient(false); // this is required else it will convert
        String dateString = sdf.format(d);
        return dateString;
    }

    // added by avinandan on 07/09/07
    public HashMap<String, String> getHashtableFromString(String str) {
        HashMap<String, String> ht = new HashMap<String, String>();
        String[] row = str.split("&");
        for (int i = 0; i < row.length; i++) {
            String[] column = row[i].split("=");
            if (column.length == 2) {
                ht.put(column[0], column[1]);
            } else {
                ht.put(row[i], "null");
            }
        }
        return ht;
    }

    public String getStringFromHashtable(Hashtable<String, String> ht) {
        String result = new String();

        Set<String> key = ht.keySet();

        for (String str : key) {
            result += str + "=" + ht.get(str) + "&";
        }
        if (result.endsWith("&"))
            result = result.substring(0, result.length() - 1);
        return result;
    }

    /**
     * Compare new and old Hashtable and insert not existing record in
     * hashtable.
     */
    public HashMap<String, String> compareHashtable(HashMap<String, String> oldHash, HashMap<String, String> newHash) {

        Set<String> keys = newHash.keySet();
        for (String keyStr : keys)
            oldHash.put(keyStr, newHash.get(keyStr));
        return oldHash;
    }

    // end mark

}// closing class Utility

