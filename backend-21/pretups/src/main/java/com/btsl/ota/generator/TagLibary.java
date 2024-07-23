package com.btsl.ota.generator;

/**
 * @(#)TagLibary.java
 *                    Copyright(c) 2003, Bharti Telesoft Ltd.
 *                    All Rights Reserved
 *                    This Interface provides constant values to be used in
 *                    other Classes
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 * 
 *                    Gaurav Garg 10/14/2003 12:57:47 PM) Initial Creation
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 */

public interface TagLibary {

    public static final String refLength = "0L";// nearly all the lengths are
                                                // first defined as constant so
                                                // this is reference length
    public static final String name = "NAME";
    public static final String deckTag = "01WL02";
    public static final String deckIdTag = "0203414141";

    public static final String cardTag = "05";
    public static final String cardIdTag = "06";// Tag
    // Input Specific Variable
    public static final String input_type = "TYPE";
    public static final String input_name = "NAME";
    public static final String input_display = "BTSL-DISPLAY";
    public static final String input_maxlength = "MAXLENGTH";
    public static final String input_minength = "BTSL-MINLENGTH";
    public static final String input_format = "FORMAT";
    public static final String input_value = "VALUE";
    public static final String defaultInputType = "text";
    public static final String lang1 = "BTSL-LANG1";
    public static final String lang2 = "BTSL-LANG2";
    public static final String inputTypeValueText = "TEXT";
    public static final String inputTypeValuePassword = "PASSWORD";
    public static final String formatC = "C";
    public static final String formatN = "N";
    public static final String commandQualifierValTC = "01";// text char
    public static final String commandQualifierValTN = "00";// text number
    public static final String commandQualifierValPN = "04";// password number
    public static final String commandQualifierValPC = "05";// password char
    public static final String updateMenuOptionTag = "4700";
    public static final String destinationDevice = "02";

    public static final String encryptTag = "26";
    public static final String encryptVariable = "50";

    public static final String href = "HREF";
    // card Attributes
    public static final String card_id = "ID";
    public static final String cardLength = "CL";
    public static final String commandQLength = "CQ";

    public static final String textTag = "0D";
    public static final String displayTextTag = "2D0L218102";
    // public static final String displayTextTag = "2D0L2181020D0A04";
    public static final String inputTag = "2D0L23";
    public static final String compareTag = "40";
    public static final String deleteTag = "48";
    public static final String textToDisplay = "TEXTTODISPLAY";
    public static final String type = "TYPE=";

    public static final String conLength = "FF";
    public static final String defaultDCS = "04";
    public static final String unicodeDCS = "08";

    public static final String sendSMSTag = "2D0L13CQ83";
    public static final String sendSMS_title = "TITLE";
    public static final String sendSMS_dest = "DEST";
    public static final String sendSMS_pid = "PID";
    public static final String sendSMS_dcs = "DCS";
    public static final String sendSMS_vp = "VP";// Validity period
    public static final String sendSMSDefaultTitle = "Sending SMS";
    public static final String sendSMSDefaultPID = "00"; // "7F";Modified on
                                                         // 08/march/2003
    public static final String sendSMSDefaultDCS = "binary";
    public static final String sendSMSDefaultDCSValue = "F6";
    public static final String sendSMSDCSValue = "00";

    public static final String sendSMSDefaultVP = "01";
    public static final String alphaTag = "05";
    public static final String smsTPDUTag = "0B";
    public static final String TON_NPI = "B0";

    public static final String concat_name = "NAME";
    public static final String conCatTag = "240L";

    public static final String ifdoTag = "4104";
    public static final String ifdo_value = "VALUE";
    public static final String ifdo_var = "VAR";
    public static final String ifdo_defaultValue = "01";

    public static final String ifelseTag = "420L";
    public static final String ifelse_card1 = "CARD1";
    public static final String ifelse_var = "VAR";
    public static final String ifelse_card2 = "CARD2";

    public static final String counterName = "COUNTER";
    public static final String counterTag = "4304080121";// here 43 is tag 04 is
                                                         // length after that
                                                         // one byte will be
    // reserved for Var Ref and then variable refTag
    public static final String counterReadTag = "4401";
    public static final String pinFlag = "pinFlag";
    public static final String pinFlagValue = "23";
    public static final String tidFlag = "tidFlag";
    public static final String tidFlagValue = "21";
    public static final String productFlag = "productFlag";
    public static final String productFlagValue = "22";

    public static final String fixedData = "fixedData";
    public static final String fixedDataValue = "60";
    // Language Support
    public static final int english = 1;
    public static final int unicode = 2;
    public static final int both = 3;
    public static final String defaultValueTag = "17";

    public static final String selectTag = "2D0L240082";
    public static final String onpick = "ONPICK";
    public static final String value = "VALUE";
    public static final String coupleTag = "110L";
    public static final String itemTag = "100101";

    public static final String binary = "BINARY";
    public static final String var = "VAR";

    public static final String wmlLength = "WL";
    public static final String text = "TEXT";
    public static final String smsc = "SMSC";
    public static final String smscTag = "06";
    public static final String smsc1 = "SMSC1";
    public static final String smsc2 = "SMSC2";
    public static final String smsc3 = "SMSC3";

    public static final String port1 = "PORT1";
    public static final String port2 = "PORT2";
    public static final String port3 = "PORT3";

    public static final String vp1 = "VP1";
    public static final String vp2 = "VP2";
    public static final String vp3 = "VP3";

    public static final String vp1Value = "01";
    public static final String vp2Value = "02";
    public static final String vp3Value = "03";

    public static final String goTag = "29";
    public static final String varRef = "08";// this is the value of the actual
                                             // varRefTag
    public static final String varRefTag = "FF";// This is used to show that a
                                                // variable is coming

    public static final String fixedLength01 = "01";
    public static final String fixedLength02 = "02";
    public static final String fixedLength03 = "03";
    public static final String fixedLength07 = "07";
    public static final String fixedValue11 = "11";
    public static final String fixedValue51 = "51";
    public static final String port1Value = "01";
    public static final String port2Value = "02";
    public static final String port3Value = "03";

    public static final String smsc1Value = "01";
    public static final String smsc2Value = "02";
    public static final String smsc3Value = "03";

    public static final String toneNPI = "91";
    public static final String inlineTag = "0A";
    public static final String itemTagValue = "0F";
    public static final String responseTag = "11";

    public static final String urlTag = "0D";
    public static final String setTag = "45";
    public static final String data = "DATA";
    public static final String mobileNo = "MOBILENO";
    public static final String amount = "AMOUNT";
    public static final String userId = "USERID";
    public static final String getTag = "46";

    public static final String changeLangTag = "49";
    public static final String changelang_value = "VALUE";

    // START :WMLVALIDATOR TAG AND ATTRIBUTE CONSTANTS.
    public static final String TAG_BTSL_COMPARE = "btsl-compare";
    public static final String TAG_BTSL_IF_ELSE = "btsl-if-else";
    public static final String TAG_BTSL_GET = "btsl-get";
    public static final String TAG_WML = "wml";
    public static final String TAG_CARD = "card";
    public static final String TAG_INPUT = "input";
    public static final String TAG_BTSL_CONCAT = "btsl-concat";
    public static final String TAG_VAR = "var";
    public static final String TAG_TEXT = "text";
    public static final String TAG_SELECT = "select";
    public static final String TAG_BTSL_SEND_SMS = "btsl-send-sms";
    public static final String TAG_OPTION = "option";
    public static final String TAG_GO = "go";
    public static final String TAG_BTSL_ENCRYPT = "btsl-Encrypt";
    public static final String TAG_BTSL_DECRYPT = "btsl-decrypt";
    public static final String TAG_BTSL_IF_DO = "btsl-if-do";
    public static final String TAG_BTSL_SETCOUNTER = "btsl-setCounter";
    public static final String TAG_BTSL_READCOUNTER = "btsl-readCounter";
    public static final String TAG_BTSL_DISPLAYTEXT = "btsl-displayText";
    public static final String TAG_BTSL_LANGCHANGE = "btsl-lang-Change";
    public static final String TAG_SET = "btsl-set";
    public static final String TAG_GET = "btsl-get";
    public static final String TAG_BTSL_UPDATEMENUOPTION = "btsl-updateMenuOption";
    public static final String TAG_BTSL_DELETE = "btsl-delete";
    public static final String ATT_NAME = "name";
    public static final String ATT_ID = "id";
    public static final String ATT_TYPE = "type";
    public static final String ATT_MAXLENGTH = "maxlength";
    public static final String ATT_BTSL_MINLENGTH = "btsl-minlength";
    public static final String ATT_BTSL_LANG1 = "btsl-lang1";
    public static final String ATT_BTSL_LANG2 = "btsl-lang2";
    public static final String ATT_VALUE = "value";
    public static final String ATT_FORMAT = "format";
    public static final String ATT_DEST = "dest";
    public static final String ATT_PID = "pid";
    public static final String ATT_DCS = "dcs";
    public static final String ATT_VP = "vp";
    public static final String ATT_SMSC = "smsc";
    public static final String ATT_ONPICK = "onpick";
    public static final String ATT_HREF = "href";
    public static final String ATT_DATA = "data";
    public static final String ATT_VAR = "var";
    public static final String ATT_VAR1 = "var1";
    public static final String ATT_VAR2 = "var2";
    public static final String ATTVAL_SMSC1 = "smsc1";
    public static final String ATTVAL_SMSC2 = "smsc2";
    public static final String ATTVAL_SMSC3 = "smsc3";
    public static final String ATTVAL_dest1 = "port1";
    public static final String ATTVAL_dest2 = "port2";
    public static final String ATTVAL_dest3 = "port3";
    public static final int VAL_BTSL_SEND_SMS121 = 121;
    public static final int VAL_BTSL_SEND_SMS255 = 255;
    public static final String VAL_DOL = "$";
    public static final int VAL_IFDO_VALUE_LEN = 5;
    public static final String VAL_HASH = "#";
    public static final int VAL_GO_HREF_LEN = 2;
    public static final int VAL_OPTION_VALUE_LEN = 20;
    public static final int VAL_OPTION_ONPICK_LEN = 2;
    public static final int VAL_SendSMS_VP_LEN = 2;
    public static final int VAL_SendSMS_PID_LEN = 2;
    public static final int VAL_SendSMS_SMSC_LEN = 13;
    public static final int VAL_SendSMS_DEST_LEN = 13;
    public static final int VAL_INPUT_MINLEN = 0;
    public static final int VAL_INPUT_MAXLEN_L = 1;
    public static final int VAL_INPUT_MAXLEN_U = 100;
    public static final int VAL_CARD_ID_LEN = 2;
    public static final int VAL_WML_LEN = 10;
    public static final int VAL_COMPARE_LANG1 = 50;
    public static final int VAL_COMPARE_LANG2 = 100;
    // END :WMLVALIDATOR TAG AND ATTRIBUTE CONSTANTS.
}
