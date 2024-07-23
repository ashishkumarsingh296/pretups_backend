/**
 * @(#)ByteCodeGeneratorI.java
 *                             Copyright(c) 2003, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 *                             Controller for initating an Order.
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 *                             Gaurav Garg 05/11/2003 Initial Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 */
package com.btsl.ota.generator;

public interface ByteCodeGeneratorI {

    // Values imported from SimServicesUtil
    public static String ADD = "ADD";
    public static String DELETE = "DELETE";
    public static String ACTIVATE = "ACTIVATE";
    public static String DEACTIVATE = "DEACTIVATE";
    public static String CHANGE_TITLE = "CHANGE_TITLE";
    public static String UPDATE_PARAMETERS = "UPDATE_PARAMETERS";
    public static String UPDATE_PARAMETER_PIN = "UPDATE_PARAMETER_PIN";
    public static String UPDATE_PARAMETER_PRODUCT = "UPDATE_PARAMETER_PRODUCT";
    public static String UPDATE_PARAMETER_TID = "UPDATE_PARAMETER_TID";
    public static String VALIDITY_PERIOD = "VALIDITY_PERIOD";
    public static String UPDATE_SMSC = "UPDATE_SMSC";
    public static String UPDATE_SHORTCODE = "UPDATE_SHORTCODE";
    public static String UPDATE_TID = "UPDATE_TID";
    public static String SIM_ENQUIRY = "SIM_ENQUIRY";
    public static String UPDATE_LANG_FILE = "UPDATE_LANG_FILE";
    public static String SENT_TEST_CARD = "SENT_TEST_CARD";
    public static String SENT = "SENT";
    public static int LANGUAGEMENU_REQUIRED_FLAG = 5;
    public static int DELIVERYRECIEPT_REQUIRED_FLAG = 6;
    public static int PIN_REQUIRED_FLAG = 4;
    public static int PRODUCT_REQUIRED_FLAG = 3;
    public static int TID_REQUIRED_FLAG = 2;
    public static int TID_LENGTH = 9;
    public static int SMS_ADM = 10;
    //
    public static final String CREATEDBY = "SYSTEM";
    public static final String MODIFIEDBY = "SYSTEM";

    // Project Related Variables that needs to be changed in future
    // public static final int MENUSIZELANG1 = 15;
    // public static final int MENUSIZELANG2 = 21;
    public static final int BYTECODESIZE = 800;
    // public static final int MENUSIZELIMIT = 16;
    // General Declaration
    public static final String FILE_PATH = "c:\\abc.log";
    public static final String CONCAT_TAG = "0006";
    public static final String ADMIN_TAG = "50";
    public static final String MENU_TAG = "51";
    public static final String DELETE_TAG = "52";
    public static final String ACT_DEACT_TAG = "53";
    public static final String BYTECODETAG = "55";
    public static final String CHAGEMENUNAMETAG = "69";
    public static final String UPDATEPARAMETETSTAG = "65";
    public static final String UPDATESMSCPORTVPTAG = "66";
    public static final String UPDATETIDTAG = "67";

    public static final String ENCRYPT_TAG = "70";
    public static final String MENU_NAME_TAG = "0A";
    public static final String MULTILANGTAG = "FF";
    public static final String fixedLenth02 = "02";
    public static final String fixedLenth01 = "01";
    public static final String fixedLenth09 = "09";
    public static final String SIM_ENQUIRYTAG = "57";

    public static final String ALLSERVICESTAG = "58";
    public static final String SPECIFICSERVICESTAG = "59";
    public static final String SETTINGSTAG = "60";
    public static final String ALLBYTECODEINFOTAG = "61";

    /*
     * Record Values:
     * Language specific texts
     * 1: Applet Title(prepaid Refill) : 10 81 0D 12 AA CD B0 C0 AA C7 A1 20 B0
     * BF DE BF B2
     * 2: Lang Menu (Language/Bhasa): 09 80 09 2D 09 3E 09 37 09 3E
     * 3: SMS Display Message(Sending SMS/SMS jaa raha hai): 12 81 0F 12 B8 82
     * A6 C7 B6 20 9C BE 20 B0 B9 BE 20 B9 C8
     * 4: Not Used
     * 5: Language Name(English/Hindi): 0D 80 09 39 09 3F 09 28 09 4d 09 26 09
     * 40
     */
    // Hindi Menu Update File
    public static final String UPDATELANGFILETAG = "68";

    public static final String APPLETTITLE = "10810D12AACDB0C0AAC7A120B0C0ABBFB2FFFFFFFF";// preparid
                                                                                          // refill
    public static final String LANGMENU = "0980092D093E0937093EFFFFFFFFFFFFFFFFFFFFFF"; // Bhasha
    public static final String SMSDISPLAY = "12810F12B882A6C7B6209CBE20B0B9BE20B9C8FFFF";// sandash
                                                                                         // ja
                                                                                         // raha
                                                                                         // hai
    public static final String NOTUSED = "";
    public static final String LANGUAGENAME = "0D800939093F0928094d09260940FFFFFFFFFFFFFF"; // Hindi

    /*
     * public static final String APPLETTITLE =
     * "0F800050005200450050004100490044FFFFFFFFFF";//PREPAID
     * public static final String LANGMENU =
     * "0B8000420041005300480041FFFFFFFFFFFFFFFFFF";//BASHA
     * public static final String SMSDISPLAY =
     * "0F800047004F004E00450053004D0053FFFFFFFFFF";//GONESMS
     * public static final String NOTUSED = "";
     * public static final String LANGUAGENAME =
     * "11800045004E004700480049004E00440049FFFFFF";//ENGHINDI
     */

    public static final int MAXTIME = 720;
    public static final int MINTIME = 1;

    public static final int APPLETTITLE01 = 1;
    public static final int LANGMENU02 = 2;
    public static final int SMSDISPLAY03 = 3;
    // public static final int NOTUSED04 = "";
    public static final int LANGUAGENAME05 = 5;

    // Description Declaration
    public static final String DESC_MENU_TAG = "Menu Tag";
    public static final String DESC_LENGTH = "Length";
    public static final String DESC_MENU_POSITION = "Menu Position";
    public static final String DESC_ACTIVATION_STATE = "Activation State";
    public static final String DESC_SERVICEID = "Service Id";
    public static final String DESC_MENU_NAME = "Menu Name";
    public static final String DESC_BYTECODE_TAG = "ByteCode Tag";
    public static final String DESC_G = "-";
    // Exception Declaration
    public static final String EXP_LANGSIZELIMIT = "Lang size should be less than";
    public static final String EXP_BYTESIZELIMIT = "ByteCode size should be less than";
    public static final String EXP_MENUOPTIONNOTVALID = "Menu Option is not Valid(1-16)";
    public static final String EXP_NOLESSTHANZERO = "No. less than Zero not allowed";
    public static final String EXP_ACTIVATIONSTATUS = "Invalid Activation Status((Y-N)or(1,2,3))";
    public static final String EXP_SERVICEID = "Service ID should be a number";
    public static final String EXP_SERVICEIDSIZE = "Service ID should less than 255";
    public static final String EXP_SIZE = "SIZE limit(0-255)";
    public static final String EXP_VERSION = "Version(Major or Minor) should be a number";
    public static final String EXP_LANG = "Lang should be either English or English+Unicode Plz check Your options";
    public static final String EXP_MENUSIXELIMIT = "Maximum support size of activation and deactivation of menus is ";
    public static final String EXP_ONLYINTEGERVALUES = "Only Integer values are supported in MenuList(Act/Deact)";
    public static final String EXP_OPTIONREPEATED = "Menu(Act/Deact) Option is repeated";
    public static final String EXP_OPERATIONNOTSUPPORTED = "This Operation is not supported";
    public static final String EXP_SHORTLIMITEXCEEDS = "Limit is violated(0-65535)";
    public static final String EXP_UPDATEFLAGLIMITEXCEED = "Update Parameters limit is violated(1-10)";
    public static final String EXP_POSITIVENOSUPPORT = "Only Positive Numbers are supported";

    public static final String E77 = "Menu Position greater than no of available records on menu file";
    public static final String E78 = "Menu Level Missing";
    public static final String E79 = "Byte Code TLV Missing";
    public static final String E80 = "Service Id and Version No not Matching with actual value in the specified menu position";
    public static final String E81 = "Wrong Byte Code Length";
    public static final String E82 = "Error while updating bytecode file";
    public static final String E85 = "Wrong Status Value in ByteCode";
    public static final String E86 = "Unknown Parameter Identifier";
    public static final String E87 = "Wrong Type/Position Value";
    public static final String E88 = "Wrong TransactionID (Should be 9)";
    public static final String E89 = "Wrong length or record No field";
    public static final String E90 = "Wrong length / Menu position";

    // Added By gurjeet on 22/12/2003
    public static final String ADDSERVICE = "Add Service";
    public static final String MODIFYSERVICE = "Modify Service";
    public static final String ACTDEACT = "Activate/Deactivate/Delete";
    public static final String PARAMETERS = "Update Parameters";
    public static final String UPDATELANG = "Update Second Language";
    public static final String SIMENQ = "SIM Enquiry";
    public static final String BULKPUSHOPSER = "SERVICE";
    public static final String BULKPUSHOPPARAM = "PARAMS";
    public static final String BULKPUSHOPSIMENQ = "SIMENQ";
    public static final String BULKPUSHOPTYPES = "S";
    public static final String BULKPUSHOPTYPEP = "P";
    public static final String NOTSENTSTAT = "NOTSENT";
    public static final String NEWSTATUS = "NEW";
    public static final String JOB = "JOB";
    public static final String BATCH = "BATCH";
    public static final String ALLFLAG = "ALL";

    // Added by gaurav garg on 27 jan 2004
    public static final String oldCard = "old";
    public static final String newCard = "new";
    public static final String keyFileToRead = "key";
    public static final String iccidFileToRead = "iccid";

}
