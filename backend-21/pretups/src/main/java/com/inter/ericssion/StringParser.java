package com.inter.ericssion;

import java.net.*;

public class StringParser {
    String listStr = "";
    StringBuffer listBuff = null;
    // static URLEncoder urlEnc;
    // static URLDecoder urlDec;

    static final String paramSep = "&";
    static final String valueSep = "=";
    static final int ERR_CODE = -1;
    static final int SUCCESS = 0;
    static final String encodeScheme = "UTF-8";

    Object guard1 = new Object();
    Object guard2 = new Object();

    // static int keyPtr = 0;
    // static int valPtr = 0;

    public String msisdnStr = null;
    public String ptRefIdStr = null;

    /**
     * Default Constructor
     **/
    public StringParser() {
        this.listBuff = new StringBuffer(80);
    }

    /**
     * Constructor to set the Initial value of the String
     **/
    public StringParser(String str) {
        if (str == null || str.length() == 0) {
            this.listStr = "";
            this.listBuff = new StringBuffer(80);
        } else {
            this.listStr = str;
            this.listBuff = new StringBuffer(str);
        }
    }

    /**
     * Reset the String to null String
     **/
    public void resetString() {
        listStr = "";
        listBuff = new StringBuffer(80);
    }

    /**
     * Appending the key and value to the Current String
     **/
    public synchronized int put(String key, String value) {
        String key1 = null;

        if (key == null || key.length() == 0) {
            return ERR_CODE;
        }
        if (key.equals("MSISDN")) {
            msisdnStr = value;
        } else if (key.equals("PTRefId")) {
            ptRefIdStr = value;
        }

        if (listBuff.length() > 0 && key != null && value != null) {
            key1 = key;
            key = paramSep + key + valueSep;

            if (listBuff.indexOf(key) >= 0) {
                int start = listBuff.indexOf(key) + 1;
                start = listBuff.indexOf(valueSep, start) + 1;
                int end = listBuff.indexOf(paramSep, start);

                if (end < 0)
                    end = listBuff.length();

                try {
                    listBuff = listBuff.replace(start, end, URLEncoder.encode(value, encodeScheme));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listStr = listBuff.toString();

                return SUCCESS;
            } else if (listBuff.indexOf(key) == -1) {
                // System.out.println("Else loop ");
                key = key1;
                key = key + valueSep;
                if (listBuff.indexOf(key) >= 0 && listBuff.indexOf(key) - 1 < 0) {
                    int start = listBuff.indexOf(key) + 1;
                    start = listBuff.indexOf(valueSep, start) + 1;
                    int end = listBuff.indexOf(paramSep, start);

                    if (end < 0)
                        end = listBuff.length();

                    try {
                        listBuff = listBuff.replace(start, end, URLEncoder.encode(value, encodeScheme));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listStr = listBuff.toString();

                    return SUCCESS;
                } else
                    key = key1;
            } else
                key = key1;
        }
        if (listBuff.length() > 0) {
            listBuff = listBuff.append(paramSep);
        }

        listBuff.append(key);
        listBuff.append(valueSep);
        if (value != null) {
            try {

                synchronized (guard1) {
                    listBuff = listBuff.append(URLEncoder.encode(value, encodeScheme));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        listStr = listBuff.toString();
        return SUCCESS;
    }

    /**
     * Returns the String as a Encoded URL
     **/
    public String getStringUrl() {
        if (listBuff != null) {
            return listBuff.toString();
        } else {
            return "";
        }
    }

    /**
     * Get the Value when key is passed
     **/
    public String get(String key) {
        String value = null;
        int equals = 0, ampersand;
        String retVal = null;
        String key1 = null;

        try {
            if (key == null || key.length() == 0) {
                return "";
            }
            key1 = key;
            key = paramSep + key + valueSep;
            int strOccur = listStr.indexOf(key);

            /*
             * Checking for occurence of the key string
             */
            if (strOccur > 0) {
                equals = listStr.indexOf(valueSep, strOccur);
            } else if (strOccur == -1) {
                strOccur = listStr.indexOf(key1 + valueSep);

                if (strOccur == -1)
                    return "";
                else
                    equals = listStr.indexOf(valueSep, strOccur);

            } else {
                return "";
            }

            /*
             * Getting the last index of the key string
             */
            if (equals >= 0) {
                ampersand = listStr.indexOf(paramSep, strOccur + 1);
                if (ampersand == -1) {
                    ampersand = listStr.length();
                }
            } else {
                return "";
            }

            value = listStr.substring(equals + 1, ampersand);

        } catch (Exception e) {
            return "";
        }

        if (!(value == null)) {
            try {
                synchronized (guard2) {
                    retVal = URLDecoder.decode(value, encodeScheme);
                }
            } catch (Exception e) {
                retVal = value;
            }
            return retVal;
        } else {
            return "";
        }
    }

    // ///////////////////////////////////////////////////////////////////////

    public static void main(String args[]) {
        String strData = "Req=VAL&IMSI=0&TASRefId=MU0401825257&TASDateTime=20050206170145&MSISDN=919819989338&TASOrigInstCode=00005200052&Type=SMS&SPEID=52&PEID=52&RET_MSISDN=919820303261&smscName=PostPaid&SMSRechargeType=Amount&key2=CSMS&TOPUP_STAGE=2&Interface=H&NETWORK_Id=1&PAMI_Id=1&Cust_No=56642222&Cust_Name=Orange&DestNo=140&GatewayHost=10.10.1.67&GatewayPort=13013&AltGatewayHost=10.10.1.67&AltGatewayPort=13013&Amount=12000&AmountType=POA&AccountType=3&PAMI_HOST=10.11.128.5&PAMI_PORT=4878&PAMI_INTERFACE=P&First_Flag=Y&RequestExpiryTime=1107689624376&PTRefId=MU0001916860&CardGroup=Z6&PamiAmt=5900&TalktimeAmt=5900&PromoAmt=0&AccessFee=4989&AccessFeeTax=0&Tax1Rate=10.2&Tax1Amount=1111&Tax2Rate=0&Tax2Amount=0&Multiple=1&Validity=0&PromoValidity=0&TalktimeValidity=0&GracePeriod=90&CardGroupDetId=525";
        StringParser sp = new StringParser(strData);
        System.out.println("Operator Name - " + sp.get("Cust_Name"));
    }

    // ///////////////////////////////////////////////////////////////////////

}
