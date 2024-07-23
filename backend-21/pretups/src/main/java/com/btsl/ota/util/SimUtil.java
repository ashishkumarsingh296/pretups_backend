/**
 * @(#)SimUtil.java
 *                  Copyright(c) 2003, Bharti Telesoft Ltd.
 *                  All Rights Reserved
 *                  This Class provides basic methods to support
 *                  ByteCodeGenerator and Ota Package Class
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 * 
 *                  Gaurav Garg 05/11/2003 Initial Creation
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 */
package com.btsl.ota.util;

import java.util.ArrayList;
// import java.util.Calendar;
// import java.util.Date;
// import java.util.GregorianCalendar;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.generator.ByteCodeGeneratorI;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.ota.services.businesslogic.SimVO;
import com.btsl.ota.services.businesslogic.SmsVO;
import com.btsl.ota.services.businesslogic.UserServicesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class SimUtil {
    private static final Log _logger = LogFactory.getLog(SimUtil.class.getName());
    private static final String CLASS_NAME = "SimUtil";
    
    public SimUtil() {
        super();
    }

    public static void mains(String[] args) throws Exception {
     //   org.apache.log4j.PropertyConfigurator.configure(ByteCodeGeneratorI.FILE_PATH);
        // _logger=Logger.getLogger(ByteCodeGeneratorUtil.class.getName());

        // ByteCodeGeneratorUtil bcgu = new ByteCodeGeneratorUtil();
        ArrayList test = new ArrayList();
        ServicesVO sVO1 = new ServicesVO();
        // String benquiry
        // ="41444d204601002042200a010c00000f280090ff0b5265742042616c616e6365ff0f810c12b0bf9fc7b2b020ac95beafbeffffffffffffffffffff0b010e00000fd20059ff0c4461696c79205265706f7274ff12810f12b0cb9cbea8be20b0bfaacbb0cd9fb8ffffffffffff2054581f0c0d0f2000000000";
        // String benquiry
        // ="41444d204601002042200effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff2054581f0c170b2000000000";
        /*
         * String denquiry =
         * "41444d204601002042200102ffffffffffffffffffffffffffffffffffffff696d6974ff11810e12b2bfaebf9f209fcdb0be82b8abb0ffffffffff0202ffffffffffffffffffffffffffffffffffffff696d6974ff0d810a12b2bfaebf9f20b5beaab8ffffffffffffffffff2054580c0d102e20"
         * ;
         * String typeofEnquiry ="B";
         * test= servicesInfoForQueryBD(denquiry,typeofEnquiry);
         */
        // test = new ArrayList();
        /*
         * sVO1 = new ServicesVO();
         * sVO1.setPosition(59);
         * sVO1.setStatus("N");
         * sVO1.setPosition(3);
         * sVO1.setSmscGatewayNo("9868116977");
         * sVO1.setPositionList("5,7,9");
         * sVO1.setDescription("660");
         * sVO1.setServiceID("25");
         * sVO1.setMajorVersion("25");
         * sVO1.setMinorVersion("100");
         * sVO1.setLabel1("auuuuuuuuuuuua");
         * sVO1.setLabel2("0909");
         * sVO1.setLength(100);
         * sVO1.setOffSet(100);
         * sVO1.setValidityPeriod(100);
         * sVO1.setOperation(ByteCodeGeneratorI.ADD);
         * sVO1.setTypeOfEnquiry("60");
         * test.add(sVO1);
         */
        sVO1 = new ServicesVO();
        sVO1.setPosition(1);
        sVO1.setStatus("N");
        sVO1.setSmscGatewayNo("986811698720");
        sVO1.setPositionList("5,7,9");
        sVO1.setDescription("111111111");
        sVO1.setServiceID("25");
        sVO1.setMajorVersion("25");
        sVO1.setMinorVersion("100");
        sVO1.setLabel1("auuuuuuuuuuuua");
        sVO1.setLabel2("0909");
        sVO1.setLength(100);
        sVO1.setOffSet(100);
        sVO1.setValidityPeriod(100);
        sVO1.setOperation(ByteCodeGeneratorI.VALIDITY_PERIOD);
        sVO1.setTypeOfEnquiry("60");
        test.add(sVO1);
        SimUtil su = new SimUtil();


        /*
         * Boolean a = new Boolean(false);
         * _logger.info("pph"+a);
         */
        // String updateTId = returnOperationByteCode(test);
        // _logger.info("-->"+updateTId);

        // returnOperationByteCodeDesc("510503191964005204031919645304010507095304000507096905031919640065022200660C0103090DA1898611967802F9660B0203080CA189861196780266030303136709313131313131313131680103");
        // compareServerRequestListWithSIMResponseList("510503191964005204031919645304010507095304000507096905031919640065022200660C0103090DA1898611967802F9660B0203080CA189861196780266030303136709313131313131313131680103","5103191964002052031919640020530020530020690020650020660020660020660020670020680020");

        // compareServerRequestListWithSIMResponseList("5302000169050206000000","530020690020",);
        // _logger.info("-->"+stringToByteConverter("asAS61734153"));
        // _logger.info("-->"+byteToStringConverter("61734153"));

        // int a = bcgu.hexToDec("FF");
        // _logger.info("value is "+a);
        // bcgu.returnOperationByteCodeDescTest(updateTId);
        // bcgu.returnOperationByteCodeDescTest("510503191964006608010398681169872");*/
        // compareServerRequestListWithSIMResponseList("510503191964005204031919645304010507095304000507096905031919640065022200660C0103090DA1898611967802F9660B0203080CA189861196780266030303136709313131313131313131680103","510319196477520319196477537753776977657766776677667767776877");
        /*
         * String str = "080c911989020045440404b07698ffffffffff01";
         * _logger.info(""+smscPortVPHandle(str));
         */
        /*
         * _logger.info(" "+new Date());
         * ArrayList a = new ArrayList();
         * a.add("pp");
         * a.add("pp");a.add("pp");a.add("pp");a.add("pp");
         * for(int i=0;i<a.size();i++)
         * {
         * _logger.info(i+" "+a.get(i));
         * }
         */
        /*
         * Date lockDate = new Date();
         * int lockTime = 5;
         * GregorianCalendar k1 = (GregorianCalendar)Calendar.getInstance();
         * GregorianCalendar k = (GregorianCalendar)Calendar.getInstance();
         * k.setTime(lockDate);
         * k.set(Calendar.MINUTE,k.get(Calendar.MINUTE)+lockTime);
         * // _logger.info(" = "+k.after(k1));
         * //boolean a =
         * isValid2Lang("14800D12AACDB0C0AAC7A12ff0C0ABBFB21234567F");
         * StringBuffer ak = new StringBuffer();
         * boolean kgh = false;
         * /* Boolean a = new Boolean("false");
         * _logger.info("pph"+kgh);
         * test(kgh);
         * _logger.info("pph"+kgh);
         */
        _logger.info("", lengthConverter(16));

        String bytecodeDesc = "6603030113";
        su.returnOperationByteCodeDesc(bytecodeDesc);

    }

    /*
     * static void test(boolean kgh)
     * {
     * kgh = true;
     * //_logger.info(" Inside Mehod "+kgh);
     * 
     * }
     */
    /**
     * This method is gives u the smsc no , port no and vp | separated used in
     * SIM Enquiry3
     * 
     * @param smscGatewayNoRev
     *            String (has tone NPI and length included in it)
     * @return String
     * @throws Exception
     */
    public static String smscPortVPHandle(String smscPortVPstring) throws BTSLBaseException {
        try {
            int length = smscPortVPstring.length();
            int offsetact = 0;
            int actualLength = Integer.parseInt(smscPortVPstring.substring(0, 2), 16);
            int phoneNoLength = Integer.parseInt(smscPortVPstring.substring(2, 4), 16);
            String phoneNo = byteSwapper(smscPortVPstring.substring(6));
            offsetact = actualLength * 2 + 2;
            String buf = "" + phoneNo.substring(0, phoneNoLength) + "|";
            actualLength = Integer.parseInt(smscPortVPstring.substring(offsetact, offsetact + 2), 16);
            phoneNoLength = Integer.parseInt(smscPortVPstring.substring(offsetact + 2, offsetact + 4), 16);
            phoneNo = byteSwapper(smscPortVPstring.substring(offsetact + 6));
            buf += phoneNo.substring(0, phoneNoLength) + "|" + smscPortVPstring.substring(length - 2);

            return buf;

        } catch (Exception e) {
            _logger.error("smscGatewayNoRev ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "smscGatewayNoRev", "");
        }

    }

    /**
     * This method validates Menu Position List
     * 
     * @param menulist
     *            String(i.e 2,3,4,8)
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    public static String menuPositionList(String menuList, SimProfileVO simProfileVO) throws BTSLBaseException {
        StringBuffer menuPositionListBuf = new StringBuffer();
        try {
            java.util.StringTokenizer str = new java.util.StringTokenizer(menuList, ",");
            int count = str.countTokens();
            Integer[] scratch = new Integer[count];
            if (!(count > 0 && count <= simProfileVO.getNoOfmenus())) {
                throw new BTSLBaseException("ota.util.error.menusizeexceeds");
            }
            // throw new
            // Exception(ByteCodeGeneratorI.EXP_MENUSIXELIMIT+ByteCodeGeneratorI.MENUSIZELIMIT);
            int check = 0;
            int j = 0;
            for (int i = 0; i < count; i++) {
                check = Integer.parseInt(str.nextToken().trim());
                if (check < 1 || check > simProfileVO.getNoOfmenus()) {
                    throw new BTSLBaseException("ota.util.error.menusizeexceeds");
                }
                // throw new
                // Exception(ByteCodeGeneratorI.EXP_MENUOPTIONNOTVALID);
                scratch[i] = new Integer(check);
                for (j = 0; j < i; j++) {
                    if (check == scratch[j].intValue()) {
                        throw new BTSLBaseException("ota.util.error.optionrepeated");
                        // throw new
                        // Exception(ByteCodeGeneratorI.EXP_OPTIONREPEATED);
                    }
                }
                menuPositionListBuf.append(lengthConverter(check));
            }
        } catch (Exception e) {
            _logger.error("menuPositionList ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "menuPositionList", "");
        }

        return menuPositionListBuf.toString();
    }

    /**
     * This method validates Menu Position
     * 
     * @param menuOption
     *            int (i.e 3)
     * @return String
     * @throws Exception
     */
    public static String menuPosition(int menuOption, SimProfileVO simProfileVO) throws BTSLBaseException {
        String menuPosition = null;
        try {
            if (!(menuOption > 0 && menuOption <= simProfileVO.getNoOfmenus())) {
                // throw new
                // Exception(ByteCodeGeneratorI.EXP_MENUOPTIONNOTVALID);
                throw new BTSLBaseException("ota.util.error.menusizeexceeds");
            }
            menuPosition = hexValue(menuOption);
        } catch (Exception e) {
            _logger.error("menuPosition ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "menuPosition", "");
        }
        return menuPosition;
    }

    /**
     * This method validates List for Sim Enquiry
     * 
     * @param simEnquiry
     *            String (i.e 2,3 or 5,10 at Max value of this can't be more
     *            that 2)
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    public static String simEnquiryList(String simEnquiry, SimProfileVO simProfileVO) throws BTSLBaseException {
        StringBuffer simEnquiryListBuf = new StringBuffer();
        try {
            java.util.StringTokenizer str = new java.util.StringTokenizer(simEnquiry, ",");
            int count = str.countTokens();
            Integer[] scratch = new Integer[count];
            int j = 0;
            if (count < 1 || count > 2) {
                throw new BTSLBaseException("ota.util.error.simquerylistsize");
            }
            int check = 0;
            // int indexOf= 0;
            for (int i = 0; i < count; i++) {
                check = Integer.parseInt(str.nextToken().trim());
                if (check < 1 || check > simProfileVO.getNoOfmenus()) {
                    throw new BTSLBaseException("ota.util.error.simqueryvariable");
                }
                scratch[i] = new Integer(check);
                for (j = 0; j < i; j++) {
                    if (check == scratch[j].intValue()) {
                        // throw new
                        // Exception("ota.util.error.simqueryvariableoptionrepeated");
                        throw new BTSLBaseException(SimUtil.class, "otaMessageSenderArr", "ota.util.error.simqueryvariableoptionrepeated");
                    }

                }
                simEnquiryListBuf.append(lengthConverter(check));
            }
        } catch (Exception e) {
            _logger.error("simEnquiryList ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "simEnquiryList", "");
        }
        return simEnquiryListBuf.toString();
    }

    /**
     * This method Converts decimal values to hex Values
     * 
     * @param value
     *            int
     * @return String
     * @throws Exception
     */
    public static String hexValue(int value) throws BTSLBaseException {
        String hexValue = null;
        try {
            if (value < 0) {
                throw new BTSLBaseException("ota.util.error.lessthanzero");
            }
            if (value < 16) {
                hexValue = "0" + Integer.toHexString(value);
            } else {
                hexValue = Integer.toHexString(value);
            }
        } catch (Exception e) {
            _logger.error("hexValue ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "hexValue", "");
        }
        return hexValue;
    }

    /**
     * This method validates Activation Status
     * 
     * @param value
     *            String
     * @return String
     * @throws Exception
     */
    public static String activationStatus(String value) throws BTSLBaseException {
        String activationStatus = null;
        try {
            if (isNullString(value)) {
                throw new BTSLBaseException("ota.util.error.activationstatus");
            }
            if ("Y".equalsIgnoreCase(value)) {
                activationStatus = "01";
            } else if ("N".equalsIgnoreCase(value)) {
                activationStatus = "00";
            } else if ("1".equalsIgnoreCase(value) || "01".equalsIgnoreCase(value)) {
                activationStatus = "01";
            } else if ("2".equalsIgnoreCase(value) || "02".equalsIgnoreCase(value)) {
                activationStatus = "02";
            } else if ("3".equalsIgnoreCase(value) || "03".equalsIgnoreCase(value)) {
                activationStatus = "03";
            } else {
                throw new BTSLBaseException("ota.util.error.activationstatus");
            }
        } catch (Exception e) {
            _logger.error("activationStatus ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "activationStatus", "");
        }

        return activationStatus;
    }

    /**
     * This method validates ServiceID
     * 
     * @param serviceId
     *            String
     * @return String
     * @throws Exception
     */
    public static String serviceID(String serviceId) throws BTSLBaseException {
        final String METHOD_NAME = "serviceID";
        try {
            if (isNullString(serviceId)) {
                throw new BTSLBaseException("ota.util.error.serviceidnull");
            }
            String serviceID = null;
            int check;
            try {
                check = Integer.parseInt(serviceId);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("ota.uti.serviceidnumberformate");
            }
            if (check > 255 || check < 0) {
                throw new BTSLBaseException("ota.uti.serviceidrange");
            }
            serviceID = hexValue(check);
            return serviceID;
        } catch (Exception e) {
            _logger.error("serviceID ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "serviceID", "");
        }

    }

    // Uptil Now all has been done
    /**
     * This method validates Version
     * 
     * @param p_version
     *            String
     * @return String
     * @throws Exception
     */
    public static String version(String p_version) throws BTSLBaseException {
        final String METHOD_NAME = "version";
        try {
            if (isNullString(p_version)) {
                throw new BTSLBaseException("Version can't be Null ");
            }
            String version = null;
            int check;
            try {
                check = Integer.parseInt(p_version);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(ByteCodeGeneratorI.EXP_VERSION);
            }
            if (check > 255 || check < 0) {
                throw new BTSLBaseException("Version " + ByteCodeGeneratorI.EXP_SIZE);
            }
            version = hexValue(check);
            return version;
        } catch (Exception e) {
            _logger.error("version ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "version", "");
        }

    }

    /**
     * This method Converts String To Byte
     * 
     * @param stringToConvert
     *            String (i.e Retailer Id , Label of the Service etc)
     * @return String
     * @throws Exception
     */
    public static String stringToByteConverter(String stringToConvert) throws BTSLBaseException {
        try {
            if (isNullString(stringToConvert)) {
                throw new BTSLBaseException("stringToConvert can't be Null ");
            }
            StringBuffer sbBuf = new StringBuffer();
            char buffer[] = stringToConvert.toCharArray();
            for (int i = 0; i < buffer.length; i++) {
                sbBuf.append(Integer.toHexString((int) buffer[i]));
            }
            return sbBuf.toString().toUpperCase();
        } catch (Exception e) {
            _logger.error("stringToByteConverter ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "stringToByteConverter", "");
        }
    }

    /**
     * This method Converts ByteArray To String
     * 
     * @param byteArrToConvert
     *            String (like 22566456)
     * @return String
     * @throws Exception
     */
    public static String byteToStringConverter(String byteArrToConvert) throws BTSLBaseException {
        try {
            if (isNullString(byteArrToConvert)) {
                throw new BTSLBaseException("byteToStringConverter::byteArrToConvert can't be Null ");
            }
            if (byteArrToConvert.length() % 2 != 0) {
                throw new BTSLBaseException("byteToStringConverter::byteArrToConvert isn't multiple of 2");
            }

            StringBuffer sbBuf = new StringBuffer();
            char buffer[] = byteArrToConvert.toCharArray();
            for (int i = 0; i < buffer.length; i++) {
                sbBuf.append((char) Integer.parseInt("" + buffer[i] + buffer[++i], 16));
            }
            return sbBuf.toString().toUpperCase();
        } catch (Exception e) {
            _logger.error("byteToStringConverter ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "byteToStringConverter", "");
        }
    }

    /**
     * This method check for Null String
     * 
     * @param text
     *            String
     * @return String
     * @throws Exception
     */
    public static boolean isNullString(String text) throws BTSLBaseException {
        try {
            if (text != null) {
                if (text.trim().length() == 0) {
                    return true;
                }
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            _logger.error("stringToByteConverter ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "isNullString", "");
        }

    }

    /**
     * This method is used to find the lang option the user is opting
     * 
     * @param textToDisplay1
     *            String
     * @parma textToDisplay2 String
     * @return int
     * @throws Exception
     */
    public static int langFinder(String textToDisplay1, String textToDisplay2) throws BTSLBaseException {
        try {
            int option = 0;
            if (!isNullString(textToDisplay1) && isNullString(textToDisplay2)) {
                option = 0;
            } else if (!isNullString(textToDisplay1) && !isNullString(textToDisplay2)) {
                option = 1;
            } else {
                throw new BTSLBaseException(ByteCodeGeneratorI.EXP_LANG);
            }
            return option;
        } catch (Exception e) {
            _logger.error("langFinder ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "langFinder", "");
        }

    }

    /**
     * This method is used to construct BER format Length
     * 
     * @param length
     *            int
     * @return String
     * @throws Exception
     */

    public static String lengthConverter(int length) throws BTSLBaseException {

        StringBuffer sbBuf = new StringBuffer();
        try {
            if ((length >= 0) && (length <= 127)) {
                if (length < 16) {
                    sbBuf.append("0");
                    sbBuf.append(Integer.toHexString(length));
                    return sbBuf.toString().toUpperCase();
                } else {
                    return Integer.toHexString(length);
                }
            } else if ((length > 127) && (length < 256)) {
                sbBuf.append("81");
                sbBuf.append(Integer.toHexString(length));
                return sbBuf.toString().toUpperCase();
            } else if ((length >= 256) && (length <= 65535)) {
                if (length < 4096) {
                    sbBuf.append("82");
                    sbBuf.append("0");
                    sbBuf.append(Integer.toHexString(length));
                    return sbBuf.toString().toUpperCase();
                } else {
                    sbBuf.append("82");
                    sbBuf.append(Integer.toHexString(length));
                    return sbBuf.toString().toUpperCase();
                }
            }
        } catch (Exception e) {
            _logger.error("lengthConverter ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "lengthConverter", "");
        }
        return sbBuf.toString().toUpperCase();
    }

    /**
     * This method is used to construct BER format Length
     * 
     * @param strlength
     *            String
     * @return String
     * @throws Exception
     */
    public static String lengthConverter(String strlength) throws BTSLBaseException {

        StringBuffer sbBuf = new StringBuffer();
        int length = Integer.parseInt(strlength);
        try {
            if ((length >= 0) && (length <= 127)) {
                if (length < 16) {
                    sbBuf.append("0");
                    sbBuf.append(Integer.toHexString(length));
                    return sbBuf.toString().toUpperCase();
                } else {
                    return Integer.toHexString(length);
                }
            } else if ((length > 127) && (length < 256)) {
                sbBuf.append("81");
                sbBuf.append(Integer.toHexString(length));
                return sbBuf.toString().toUpperCase();
            } else if ((length >= 256) && (length <= 65535)) {
                if (length < 4096) {
                    sbBuf.append("82");
                    sbBuf.append("0");
                    sbBuf.append(Integer.toHexString(length));
                    return sbBuf.toString().toUpperCase();
                } else {
                    sbBuf.append("82");
                    sbBuf.append(Integer.toHexString(length));
                    return sbBuf.toString().toUpperCase();
                }
            }
        } catch (Exception e) {
            _logger.error("lengthConverter ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "lengthConverter", "");
        }
        return sbBuf.toString().toUpperCase();
    }

    /**
     * This method finally check that no error should be in bytecode
     * 
     * @param stringToCheck
     *            String
     * @throws Exception
     */
    public static void finalCheck(String stringToCheck) throws BTSLBaseException {
        try {

            char arr[] = stringToCheck.trim().toCharArray();
            int length = arr.length;
            for (int i = 0; i < length; i++) {
                if ((arr[i] >= (char) 48 && arr[i] <= (char) 57) || (arr[i] >= (char) 65 && arr[i] <= (char) 70) || (arr[i] >= (char) 97 && arr[i] <= (char) 102)) {
                    continue;
                } else {
                    throw new BTSLBaseException("finalCheck ::  Exception :: Invalid character position" + (i / 2) + " value " + arr[i] + "String Value=" + stringToCheck);
                }
            }
            if (length % 2 != 0) {
                throw new BTSLBaseException(" finalCheck ::  Exception :: Size is not a multiple of two. Size  = " + length + " ByteCode is  = " + stringToCheck);
            }
        } catch (Exception e) {
            _logger.error("finalCheck ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "finalCheck", "");
        }
    }

    /**
     * This method is used to display the ByteCode
     * 
     * @param str
     *            String
     */
    public static void display(String str) {
        int i = 0;
        char a[] = null;
        try {
            a = str.toUpperCase().toCharArray();
            int length = a.length;
            for (i = 0; i < length; i++) {
                if (i % 2 == 0) {
                    _logger.debug("display", a[i]);
                }
            }
        } catch (Exception e) {
            _logger.error("", "Exception " + e);
        }
    }

    // This method is used to generate Inline Tag
    /*
     * Text String English: OA 02 31 31
     * Text String Unicode: OA 03 08 09 20(Not supported)
     * Text String Both Lang: 0A 0A FF 02 31 31 FF 04 09 20 09 20
     */
    /**
     * This method is used to construct inline Tag(This tag is used for
     * displaying direct Value)
     * 
     * @param lang1
     *            String
     * @param lang2
     *            String
     * @param type
     *            int
     * @return String
     * @throws Exception
     */
    public static String inlineTagUniCode(String lang1, String lang2, int type) throws BTSLBaseException {
        // In this method it is assumed that the lang1 and lang2 are coming in
        // HexForms
        StringBuffer inlineTagBuf = new StringBuffer();
        try {
            int length = 0;
            String hexlength = null;
            inlineTagBuf.append(ByteCodeGeneratorI.MENU_NAME_TAG);
            switch (type) {
            case 0:
                // In case of English
                length = lang1.trim().length() / 2;// because here hex values
                                                   // are coming
                hexlength = lengthConverter(length);
                inlineTagBuf.append(hexlength);
                inlineTagBuf.append(lang1.trim());
                break;
            case 1:
                // In case of both the languages
                /*
                 * if(lang1.length()/2>ByteCodeGeneratorI.MENUSIZELANG1)
                 * throw new
                 * Exception(ByteCodeGeneratorI.EXP_LANGSIZELIMIT+ByteCodeGeneratorI
                 * .MENUSIZELANG1);
                 * if(lang2.length()/2>ByteCodeGeneratorI.MENUSIZELANG2)
                 * throw new
                 * Exception(ByteCodeGeneratorI.EXP_LANGSIZELIMIT+ByteCodeGeneratorI
                 * .MENUSIZELANG2);
                 */
                String buffer = lengthConverter(((lang1.length() / 2) + (lang2.length() / 2) + 2 + 2));// "2
                                                                                                       // is
                                                                                                       // included
                                                                                                       // because
                                                                                                       // each
                                                                                                       // byte
                                                                                                       // takes
                                                                                                       // two
                                                                                                       // characters
                                                                                                       // to
                                                                                                       // store
                String buffer2 = lengthConverter((lang1.length() / 2));// "2 is
                                                                       // included
                                                                       // because
                                                                       // each
                                                                       // byte
                                                                       // takes
                                                                       // two
                                                                       // characters
                                                                       // to
                                                                       // store
                String buffer3 = lengthConverter((lang2.length() / 2));// "2 is
                                                                       // included
                                                                       // because
                                                                       // each
                                                                       // byte
                                                                       // takes
                                                                       // two
                                                                       // characters
                                                                       // to
                                                                       // store
                inlineTagBuf.append(buffer);// whole length of the tag
                inlineTagBuf.append(ByteCodeGeneratorI.MULTILANGTAG);
                inlineTagBuf.append(buffer2);// length of lang1
                inlineTagBuf.append(lang1);
                inlineTagBuf.append(ByteCodeGeneratorI.MULTILANGTAG);
                inlineTagBuf.append(buffer3);// length of lang1
                inlineTagBuf.append(lang2);
                break;
            default:
              	 if(_logger.isDebugEnabled()){
              		_logger.debug("Default Value " , type);
              	 }
            }
            return inlineTagBuf.toString().toUpperCase();
        } catch (Exception e) {
            _logger.error("inlineTagUniCode ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "inlineTagUniCode", "");
        }

    }

    /**
     * This method validates Menu Titles
     * 
     * @param lang1
     *            String
     * @param lang2
     *            String
     * @param type
     *            int
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    public static String menuTitleLang(String lang1, String lang2, int type, SimProfileVO simProfileVO) throws BTSLBaseException {
        try {
            // In this method it is assumed that the lang1 and lang2 are coming
            // in HexForms
            // The Maximum length of both the languages can't exceed 40 bytes
            // 40*2
            StringBuffer menuTitleLangBuf = new StringBuffer();
            int length = 0;
            String hexlength = null;
            switch (type) {
            case 0:
                // In case of English
                length = lang1.trim().length() / 2;// because here hex values
                                                   // are coming
                hexlength = lengthConverter(length);
                // if(length>78)//1 byte is reserved for length
                if (length > (simProfileVO.getMenuSize() - 2)) {
                    // throw new
                    // Exception("Menu Title Length can't be greater 40 Bytes");
                    throw new BTSLBaseException("ota.util.error.menutitlelength");
                }
                menuTitleLangBuf.append(hexlength);
                menuTitleLangBuf.append(lang1.trim());
                break;
            case 1:
                // In case of both the languages
                /*
                 * if(lang1.length()/2>ByteCodeGeneratorI.MENUSIZELANG1)
                 * throw new
                 * Exception(ByteCodeGeneratorI.EXP_LANGSIZELIMIT+ByteCodeGeneratorI
                 * .MENUSIZELANG1);
                 * if(lang2.length()/2>ByteCodeGeneratorI.MENUSIZELANG2)
                 * throw new
                 * Exception(ByteCodeGeneratorI.EXP_LANGSIZELIMIT+ByteCodeGeneratorI
                 * .MENUSIZELANG2);
                 */
                String buffer2 = lengthConverter((lang1.length() / 2));// "2 is
                                                                       // included
                                                                       // because
                                                                       // each
                                                                       // byte
                                                                       // takes
                                                                       // two
                                                                       // characters
                                                                       // to
                                                                       // store
                String buffer3 = lengthConverter((lang2.length() / 2));// "2 is
                                                                       // included
                                                                       // because
                                                                       // each
                                                                       // byte
                                                                       // takes
                                                                       // two
                                                                       // characters
                                                                       // to
                                                                       // store
                menuTitleLangBuf.append(ByteCodeGeneratorI.MULTILANGTAG);
                menuTitleLangBuf.append(buffer2);// length of lang1
                menuTitleLangBuf.append(lang1);
                menuTitleLangBuf.append(ByteCodeGeneratorI.MULTILANGTAG);
                menuTitleLangBuf.append(buffer3);// length of lang1
                menuTitleLangBuf.append(lang2);
                break;
            }
            // int padLength = 80 - menuTitleLangBuf.toString().length();
            int padLength = simProfileVO.getMenuSize() - menuTitleLangBuf.toString().length();
            for (int i = 0; i < padLength; i++) {
                menuTitleLangBuf.append("F");
            }
            return menuTitleLangBuf.toString().toUpperCase();
        } catch (Exception e) {
            _logger.error("menuTitleLang ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "menuTitleLang", "");
        }

    }

    /**
     * This method converts int value to short
     * 
     * @param number
     *            long
     * @return String
     * @throws Exception
     */
    public static String intToShort(long number) throws BTSLBaseException {
        try {
            String intToShort = null;
            if (number >= 0 && number <= 15) {
                intToShort = "000" + Long.toHexString(number);
            } else if (number >= 16 && number <= 255) {
                intToShort = "00" + Long.toHexString(number);
            } else if (number >= 256 && number <= 4095) {
                intToShort = "0" + Long.toHexString(number);
            } else if (number >= 4096 && number <= 65535) {
                intToShort = Long.toHexString(number);
            } else {
                throw new BTSLBaseException(ByteCodeGeneratorI.EXP_SHORTLIMITEXCEEDS);
            }
            return intToShort;
        } catch (Exception e) {
            _logger.error("intToShort ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "intToShort", "");
        }

    }

    /**
     * This method Checks the 2nd Lang Parameters
     * 
     * @param langString
     *            String
     * @param simProfileVO
     *            SimProfileVO
     * @return boolean
     */
    public static boolean isValid2Lang(String p_langString, SimProfileVO simProfileVO) {
        String langString = p_langString;
        boolean isValid = false;
        try {
            if (BTSLUtil.isNullString(langString)) {
                _logger.error("isValid2Lang ", " String is NULL");
                return isValid;
            }
            if (langString.trim().length() != simProfileVO.getUniCodeFileSize()) {
                _logger.error("isValid2Lang ", " String Length isn't " + simProfileVO.getUniCodeFileSize());
                return isValid;
            }
            langString = langString.trim();
            if (Integer.parseInt(langString.substring(0, 2), 16) < 2) {
                _logger.error("isValid2Lang ", " Length should be greater than 2");
                return isValid;
            }
            if ("80".equalsIgnoreCase(langString.substring(2, 4)) || "81".equalsIgnoreCase(langString.substring(2, 4))) {
                StringBuffer buf = new StringBuffer();
                buf.append(langString);
                buf.reverse();
                int counter = 0;
                for (int i = 0; i <= simProfileVO.getUniCodeFileSize(); i = i + 2) {
                    if ("FF".equalsIgnoreCase(buf.substring(i, i + 2))) {
                        counter = counter + 1;
                    } else {
                        break;
                    }
                }
                // if((20-counter)!=Integer.parseInt(langString.substring(0,2),16))
                if ((((simProfileVO.getUniCodeFileSize() - 2) / 2) - counter) != Integer.parseInt(langString.substring(0, 2), 16))// Here
                                                                                                                                  // length
                                                                                                                                  // comes
                                                                                                                                  // out
                                                                                                                                  // to
                                                                                                                                  // be
                                                                                                                                  // double
                                                                                                                                  // like
                                                                                                                                  // 42
                                                                                                                                  // now
                                                                                                                                  // 2
                                                                                                                                  // is
                                                                                                                                  // subtracted
                                                                                                                                  // as
                                                                                                                                  // first
                                                                                                                                  // two
                                                                                                                                  // bytes
                                                                                                                                  // are
                                                                                                                                  // tag
                                                                                                                                  // no
                                                                                                                                  // and
                                                                                                                                  // actual
                                                                                                                                  // length
                                                                                                                                  // is
                                                                                                                                  // tag
                                                                                                                                  // +
                                                                                                                                  // data
                                                                                                                                  // =
                                                                                                                                  // 42
                                                                                                                                  // after
                                                                                                                                  // that
                                                                                                                                  // divide
                                                                                                                                  // by
                                                                                                                                  // 2
                                                                                                                                  // as
                                                                                                                                  // to
                                                                                                                                  // treat
                                                                                                                                  // them
                                                                                                                                  // as
                                                                                                                                  // bytes
                {
                    _logger.error("isValid2Lang ", " Given Length doesn't match with data length " + (((simProfileVO.getUniCodeFileSize() - 2) / 2) - counter) + " " + Integer.parseInt(langString.substring(0, 2), 16));
                    return isValid;
                } else {
                    return true;
                }
            } else {
                _logger.error("isValid2Lang ", " Second Byte Should be 80 or 81 ");
                return isValid;
            }
        } catch (Exception e) {
            _logger.error("intToShort ", " Exception " + e);
            return false;
        }
    }

    /**
     * This function is used to check the update parameters as they have to lie
     * inbetween
     * 20 to 29 (Mapping 1 to 10 mapped as 20 to 29)
     * 
     * @param number
     *            int
     * @return String
     * @throws Exception
     */
    public static String updateParameters(int number) throws BTSLBaseException {
        try {
            String updateParameters = null;
            if (number >= 1 && number <= 10) {
                updateParameters = "" + (19 + number);// 20 to 29 Flags are
                                                      // reserved
            } else {
                throw new BTSLBaseException(ByteCodeGeneratorI.EXP_UPDATEFLAGLIMITEXCEED);
            }
            return updateParameters;
        } catch (Exception e) {
            _logger.error("updateParameters ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "updateParameters", "");
        }

    }

    /**
     * This method is used to check SMSC or Gateway Number and append Tone NPI
     * and length of number
     * 
     * @param number
     *            String
     * @return String
     * @throws Exception
     */
    public static String smscGatewayNo(String smscGatewayNo) throws BTSLBaseException {
        final String METHOD_NAME = "smscGatewayNo";
        try {
            StringBuffer smscGatewayNoBuf = new StringBuffer();
            String toneNPI = null;
            String length = lengthConverter(smscGatewayNo.length());
            if (!isValidMobileNo(smscGatewayNo)) {
                throw new BTSLBaseException(ByteCodeGeneratorI.EXP_POSITIVENOSUPPORT);
            }
            /*
             * if(smscGatewayNo.startsWith("9198"))
             * toneNPI = "91";
             * else if(smscGatewayNo.startsWith("98"))
             * toneNPI = "A1";
             * else
             * toneNPI ="B0";
             */
            // For hutch the last part is toneNPI=BO and for idea it is
            // toneNPI=00
            try {
                if (smscGatewayNo.startsWith("9198")) {
                    // toneNPI = "91"; International ISDN
                    toneNPI = Constants.getProperty("TON_NPI_INT_ISDN");
                } else if (smscGatewayNo.startsWith("98")) {
                    // toneNPI = "A1"; National ISDN
                    toneNPI = Constants.getProperty("TON_NPI_NATIONAL_ISDN");
                } else {
                    // toneNPI ="B0"; UNKNOWN
                    toneNPI = Constants.getProperty("TON_NPI_UNKNOWN");
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                _logger.error(" smscGatewayNo ", " Values of TON/NPI not found in Constants.props");
                toneNPI = "00";
            }
            smscGatewayNoBuf.append(length);
            smscGatewayNoBuf.append(toneNPI);
            smscGatewayNoBuf.append(byteSwapper(smscGatewayNo));
            smscGatewayNoBuf.insert(0, lengthConverter(smscGatewayNoBuf.toString().trim().length() / 2));
            return smscGatewayNoBuf.toString();
        } catch (Exception e) {
            _logger.error("smscGatewayNo ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "smscGatewayNo", "");
        }

    }

    /**
     * This method is extract number from the given string which has tone NPI
     * and length associted with it (just rev of smscGatewayNo method)
     * 
     * @param smscGatewayNoRev
     *            String (has tone NPI and length included in it)
     * @return String
     * @throws Exception
     */
    public static String smscGatewayNoRev(String smscGatewayNoRev) throws BTSLBaseException {
        try {
            /*
             * Structure of smscGatewayNoRev 09 0D A1 89 86 11 96 78 02 F9
             * 09 Hex Total Length
             * 0D Length of phone No
             * A1 Tone NPI
             * 89 86 11 96 78 02 F9 Phone No. Swapped
             */
            // int actualLength =
            // Integer.parseInt(smscGatewayNoRev.substring(0,2),16);
            int phoneNoLength = Integer.parseInt(smscGatewayNoRev.substring(2, 4), 16);
            String phoneNo = byteSwapper(smscGatewayNoRev.substring(6));
            return phoneNo.substring(0, phoneNoLength);

        } catch (Exception e) {
            _logger.error("smscGatewayNoRev ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "smscGatewayNoRev", "");
        }

    }

    /**
     * This method is used to check Validity of Mobile No
     * 
     * @param mobileno
     *            String
     * @return boolean
     * @throws Exception
     */
    public static boolean isValidMobileNo(String mobileno) throws BTSLBaseException {
        try {
            mobileno = mobileno.trim();
            int strLength = mobileno.length();
            for (int i = 0; i < strLength; i++) {
                if (!(Character.isDigit(mobileno.charAt(i)))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            _logger.error("isValidMobileNo ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "isValidMobileNo", "");
        }

    }

    /**
     * This method is used Swap Bytes
     * 
     * @param stringToSwapped
     *            String
     * @return String
     * @throws Exception
     */
    public static String byteSwapper(String stringToSwapped) throws BTSLBaseException {
        StringBuffer byteSwapperBuf = new StringBuffer();
        try {
            // String buffer = new String();
            int length = stringToSwapped.length();
            if (length % 2 != 0) {
                // stringSwapBuf.append("F");
                stringToSwapped += "F";
                length = length + 1;
            }
            for (int i = 0; i < length; i = i + 2) {

                byteSwapperBuf.append(stringToSwapped.charAt(i + 1));
                byteSwapperBuf.append(stringToSwapped.charAt(i));
            }
        } catch (Exception e) {
            _logger.error("byteSwapper ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "byteSwapper", "");
        }
        return byteSwapperBuf.toString();
    }

    /**
     * This method is used to convert length into 2 bytes (ex. 00 34)
     * 
     * @param intValue
     *            int
     * @return String
     * @throws Exception
     */
    public static String convertTo2DigitLength(int intValue) throws BTSLBaseException {
        try {
            StringBuffer sbBuf = new StringBuffer();
            if ((intValue < 0) || (intValue > 65535)) {
                throw new BTSLBaseException("Len in TLV should be in the range 0-65535");
            } else {
                sbBuf.append(Integer.toHexString(intValue));
                if (sbBuf.length() == 1) {
                    sbBuf.insert(0, "000");
                } else if (sbBuf.length() == 2) {
                    sbBuf.insert(0, "00");
                } else if (sbBuf.length() == 3) {
                    sbBuf.insert(0, "0");
                } else if (sbBuf.length() == 4) {
                    ;
                } else {
                    throw new BTSLBaseException("convertTo2DigitLength :: Wrong Length");
                }

            }
            return sbBuf.toString();
        } catch (Exception e) {
            _logger.error("convertTo2DigitLength  ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "convertTo2DigitLength", "");
        }

    }

    /**
     * This method is used to convert into 2 bytes (ex. 00 34)
     * 
     * @param value
     *            String
     * @return String
     * @throws Exception
     */
    public static String convertTo2DigitLength(String value) throws BTSLBaseException {
        try {
            int intValue = Integer.parseInt(value);
            StringBuffer sbBuf = new StringBuffer();
            if ((intValue < 0) || (intValue > 65535)) {
                throw new BTSLBaseException("Len in TLV should be in the range 0-65535");
            } else {
                sbBuf.append(Integer.toHexString(intValue));
                if (sbBuf.length() == 1) {
                    sbBuf.insert(0, "000");
                } else if (sbBuf.length() == 2) {
                    sbBuf.insert(0, "00");
                } else if (sbBuf.length() == 3) {
                    sbBuf.insert(0, "0");
                } else if (sbBuf.length() == 4) {
                    ;
                } else {
                    throw new BTSLBaseException("Wrong Length");
                }
            }
            return sbBuf.toString();
        } catch (Exception e) {
            _logger.error("convertTo2DigitLength ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "convertTo2DigitLength", "");
        }

    }

    /**
     * This method is used to convert ServiceId , Major Version , Minor Verison
     * and Status to corrosponding Hex Values and return the combined Hex Code
     * 
     * @param serviceID
     *            String
     * @param majorVersion
     *            String
     * @param minorVersion
     *            String
     * @param status
     *            String
     * @return String
     * @throws BaseException
     * @throws BTSLBaseException
     */
    public static String convertServiceIDMVStatusHex(String serviceID, String majorVersion, String minorVersion, String status) throws BaseException, BTSLBaseException {
        try {
            StringBuffer sbBuf = new StringBuffer();
            sbBuf.append(serviceID(serviceID));
            sbBuf.append(version(majorVersion));
            sbBuf.append(version(minorVersion));
            sbBuf.append(activationStatus(status));
            return sbBuf.toString();
        } catch (Exception e) {
            _logger.error("convertServiceIDMVStatusHex ", " Exception " + e);
            // throw new BTSLBaseException("common.csms.internalerror",new
            // Exception());
            throw new BTSLBaseException("common.csms.internalerror");
        }

    }

    /**
     * This method is used to check Validity Period and convert min format into
     * VP Sending format
     * 
     * @param timeInMin
     *            int
     * @return String
     * @throws Exception
     */
    public static String vP(int timeInMin) throws BTSLBaseException {
        try {
            String time = null;
            if (timeInMin > ByteCodeGeneratorI.MAXTIME || timeInMin < ByteCodeGeneratorI.MINTIME) {
                throw new BTSLBaseException(" VP  should range 5-720 Min");
            } else {
                if ((timeInMin / 5) - 1 < 0) {
                    throw new BTSLBaseException(" VP  should range 5-720 Min");
                }
                time = lengthConverter((timeInMin / 5) - 1);
            }
            return time;
        } catch (Exception e) {
            _logger.error("vp ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "vp", "");
        }

    }

    /**
     * This method is used to convert VP sending format into VP min
     * format(reverse of vP method)
     * 
     * @param timeInMin
     *            String
     * @return int
     * @throws Exception
     */
    public static int vPRev(String time) throws BTSLBaseException {
        try {
            _logger.info("-->", time);
            int timeInMin = (Integer.parseInt(time, 16) + 1) * 5;
            return timeInMin;
        } catch (Exception e) {
            _logger.error("vPRev ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "vPRev", "");
        }

    }

    /**
     * This method is used to check Transaction Id
     * 
     * @param numberList
     *            String
     * @return String
     * @throws Exception
     */
    public static String updateTID(String numberList) throws BTSLBaseException {
        try {
            StringBuffer listTOReturnBuf = new StringBuffer();
            // _logger.info("nu"+numberList);
            // char arr[]= numberList.trim().toCharArray();
            int i = 0;
            if (isNullString(numberList)) {
                throw new BTSLBaseException("TID List Can't be Null");
            }
            if (numberList.length() != 9) {
                throw new BTSLBaseException("Update TID Size should be 9");
            }
            try {
                while (i < 9) {
                    Integer.parseInt("" + numberList.charAt(i));
                    listTOReturnBuf.append(stringToByteConverter("" + numberList.charAt(i)));
                    i++;
                }
            } catch (Exception e) {
                _logger.error("updateTID", " Exception " + e);
                throw new BTSLBaseException(e);
            }
            return listTOReturnBuf.toString();
        } catch (Exception e) {
            _logger.error("updateTID ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "updateTID", "");
        }

    }

    /**
     * This method is used to Generate Byte Code that represents various
     * operations that are performed on the SIM
     * 
     * @param listOfServicesVO
     *            ArrayList
     * @param simProfileVO
     *            SimProfileVO
     * @return String
     * @throws Exception
     */
    public String returnOperationByteCode(ArrayList listOfServicesVO, SimProfileVO simProfileVO) throws BTSLBaseException {
        try {
            final int ADD = 51, DELETE = 52, ACTDEACT = 53, UPDATEMENUFILE = 69, UPDATEPARAM = 65, UPDATESMSPARAM = 66, UPDATETID = 67, UPDATEUNICODEFILE = 68, SIMENQUIRY = 57, SENT_TEST_CARD = 99;
            StringBuffer returnOperationByteCodeBuf = new StringBuffer();
            // String value =null;
            // int selectedCase = -1;
            if (listOfServicesVO == null || listOfServicesVO.isEmpty()) {

                _logger.debug("returnOperationByteCode ", " No Element Found in the list " + listOfServicesVO);
                return null;
            }
            int length = listOfServicesVO.size();
            TLV tlv = null;
            ServicesVO sVO = null;
            String operation = null;
            for (int i = 0; i < length; i++) {
                sVO = (ServicesVO) listOfServicesVO.get(i);
                operation = sVO.getOperation();
                if (operation.equalsIgnoreCase(ByteCodeGeneratorI.ADD)) {
                    tlv = new TLV();
                    tlv.setTag("" + ADD);
                    tlv.setData(menuPosition(sVO.getPosition(), simProfileVO));
                    tlv.setData(serviceID(sVO.getServiceID()));
                    tlv.setData(version(sVO.getMajorVersion()));
                    tlv.setData(version(sVO.getMinorVersion()));
                    tlv.setData(activationStatus(sVO.getStatus()));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.DELETE)) {
                    tlv = new TLV();
                    tlv.setTag("" + DELETE);
                    tlv.setData(menuPosition(sVO.getPosition(), simProfileVO));
                    tlv.setData(serviceID(sVO.getServiceID()));
                    tlv.setData(version(sVO.getMajorVersion()));
                    tlv.setData(version(sVO.getMinorVersion()));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.ACTIVATE)) {
                    tlv = new TLV();
                    tlv.setTag("" + ACTDEACT);
                    tlv.setData("01");
                    tlv.setData(menuPositionList(sVO.getPositionList(), simProfileVO));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.DEACTIVATE)) {
                    tlv = new TLV();
                    tlv.setTag("" + ACTDEACT);
                    tlv.setData("00");
                    tlv.setData(menuPositionList(sVO.getPositionList(), simProfileVO));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.CHANGE_TITLE)) {
                    tlv = new TLV();
                    tlv.setTag("" + UPDATEMENUFILE);
                    tlv.setData(menuPosition(sVO.getPosition(), simProfileVO));
                    tlv.setData(serviceID(sVO.getServiceID()));
                    tlv.setData(version(sVO.getMajorVersion()));
                    tlv.setData(version(sVO.getMinorVersion()));
                    tlv.setData(activationStatus(sVO.getStatus()));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_PARAMETERS)) {
                    tlv = new TLV();
                    tlv.setTag("" + UPDATEPARAM);
                    tlv.setData(updateParameters(sVO.getPosition()));
                    tlv.setData(activationStatus(sVO.getStatus()));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_SMSC)) {
                    if (sVO.getPosition() > 3 || sVO.getPosition() < 1) {
                        throw new BTSLBaseException("returnOperationByteCode :: Position for" + sVO.getOperation() + " should range 1-3");
                    }
                    tlv = new TLV();
                    tlv.setTag("" + UPDATESMSPARAM);
                    tlv.setData("01");
                    tlv.setData("0" + sVO.getPosition());
                    tlv.setData((smscGatewayNo(sVO.getSmscGatewayNo())));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_SHORTCODE)) {
                    if (sVO.getPosition() > 3 || sVO.getPosition() < 1) {
                        throw new BTSLBaseException("returnOperationByteCode :: Position for" + sVO.getOperation() + " should range 1-3");
                    }
                    tlv = new TLV();
                    tlv.setTag("" + UPDATESMSPARAM);
                    tlv.setData("02");
                    tlv.setData("0" + sVO.getPosition());
                    tlv.setData((smscGatewayNo(sVO.getSmscGatewayNo())));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.VALIDITY_PERIOD)) {
                    if (sVO.getPosition() > 3 || sVO.getPosition() < 1) {
                        throw new BTSLBaseException("returnOperationByteCode :: Position for" + sVO.getOperation() + " should range 1-3");
                    }
                    tlv = new TLV();
                    tlv.setTag("" + UPDATESMSPARAM);
                    tlv.setData("03");
                    tlv.setData("0" + sVO.getPosition());
                    tlv.setData(vP(sVO.getValidityPeriod()));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_TID)) {
                    tlv = new TLV();
                    tlv.setTag("" + UPDATETID);
                    tlv.setData(updateTID(sVO.getDescription()));
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.SIM_ENQUIRY)) {
                    tlv = new TLV();
                    tlv.setTag("" + SIMENQUIRY);
                    tlv.setData(sVO.getTypeOfEnquiry());
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.UPDATE_LANG_FILE)) {
                    if (sVO.getPosition() < 1 || sVO.getPosition() > 5 || sVO.getPosition() == 4) {
                        throw new BTSLBaseException("returnOperationByteCode :: This Option is not valid for updateLangFile (0-5) except 4");
                    }
                    tlv = new TLV();
                    tlv.setTag("" + UPDATEUNICODEFILE);
                    tlv.setData("0" + sVO.getPosition());
                    tlv.setLength();
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else if (operation.equalsIgnoreCase(ByteCodeGeneratorI.SENT_TEST_CARD)) {
                    tlv = new TLV();
                    tlv.setTag("" + SENT_TEST_CARD);
                    returnOperationByteCodeBuf.append(tlv.getTLV());
                } else {
                    throw new BTSLBaseException(ByteCodeGeneratorI.EXP_OPERATIONNOTSUPPORTED + " " + operation);
                }
            }

            return returnOperationByteCodeBuf.toString();
        } catch (Exception e) {
            _logger.error("returnOperationByteCode ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "returnOperationByteCode", "");
        }
    }

    public void returnOperationByteCodeDesc(String byteCode) throws Exception {

        try {
            OTALogger.logMessage(" returnOperationByteCodeDesc :: Byte Code for Desc :: " + byteCode);
            // final int ADD = 51 , DELETE = 52 , ACTDEACT = 53, UPDATEMENUFILE
            // = 69 , UPDATEPARAM = 65 , UPDATESMSPARAM = 66, UPDATETID= 67 ,
            // UPDATEUNICODEFILE=68, SIMENQUIRY = 57;
            int offset = 0;
            int length = Integer.parseInt(byteCode.substring(2, 4), 16);
            int serviceByteInfo = offset + 4;
            int value;
            do {
                if ("66".equalsIgnoreCase(byteCode.substring(offset, offset + 2))) {

                    int type = hexToDec(byteCode.substring(serviceByteInfo, serviceByteInfo + 2));
                    if (type == 1) {
                        OTALogger.logMessage(" SMSC Update   = " + hexToDec(byteCode.substring(serviceByteInfo + 2, serviceByteInfo + 4)));
                        OTALogger.logMessage(" Value         = " + smscGatewayNoRev((byteCode.substring(serviceByteInfo + 4, serviceByteInfo + (length * 2)))));
                    } else if (type == 2) {
                        OTALogger.logMessage(" Port Update   = " + hexToDec(byteCode.substring(serviceByteInfo + 2, serviceByteInfo + 4)));
                        OTALogger.logMessage(" Value         = " + smscGatewayNoRev((byteCode.substring(serviceByteInfo + 4, serviceByteInfo + (length * 2)))));
                    } else if (type == 3) {
                        OTALogger.logMessage(" VP Update     = " + hexToDec(byteCode.substring(serviceByteInfo + 2, serviceByteInfo + 4)));
                        OTALogger.logMessage(" Value         = " + vPRev((byteCode.substring(serviceByteInfo + 4, serviceByteInfo + (length * 2)))));
                    }

                } else if ("51".equalsIgnoreCase(byteCode.substring(offset, offset + 2))) {
                    OTALogger.logMessage(" Add Operation :: ");
                    OTALogger.logMessage(" Menu Positin   = " + hexToDec(byteCode.substring(serviceByteInfo, serviceByteInfo + 2)));
                    OTALogger.logMessage(" Service Id     = " + hexToDec(byteCode.substring(serviceByteInfo + 2, serviceByteInfo + 4)));
                    OTALogger.logMessage(" MajorVersion   = " + hexToDec(byteCode.substring(serviceByteInfo + 4, serviceByteInfo + 6)));
                    OTALogger.logMessage(" MinorVersion   = " + hexToDec(byteCode.substring(serviceByteInfo + 6, serviceByteInfo + 8)));
                    OTALogger.logMessage(" Status         = " + hexToDec(byteCode.substring(serviceByteInfo + 8, serviceByteInfo + 10)));
                } else if ("52".equalsIgnoreCase(byteCode.substring(offset, offset + 2))) {
                    OTALogger.logMessage(" Delete Operation :: ");
                    OTALogger.logMessage(" Menu Positin   = " + hexToDec(byteCode.substring(serviceByteInfo, serviceByteInfo + 2)));
                    OTALogger.logMessage(" Service Id     = " + hexToDec(byteCode.substring(serviceByteInfo + 2, serviceByteInfo + 4)));
                    OTALogger.logMessage(" MajorVersion   = " + hexToDec(byteCode.substring(serviceByteInfo + 4, serviceByteInfo + 6)));
                    OTALogger.logMessage(" MinorVersion   = " + hexToDec(byteCode.substring(serviceByteInfo + 6, serviceByteInfo + 8)));
                } else if ("53".equalsIgnoreCase(byteCode.substring(offset, offset + 2))) {
                    OTALogger.logMessage(" Activation Or Deactivation Operation :: ");
                    String status = null;
                    if (hexToDec(byteCode.substring(serviceByteInfo, serviceByteInfo + 2)) == 1) {
                        status = "activated";
                    } else {
                        status = "deactivated";
                    }
                    OTALogger.logMessage(" Status         = " + status);
                    OTALogger.logMessage(" Menu Position  = " + separateValues(byteCode.substring(serviceByteInfo + 2, serviceByteInfo + (length * 2))));
                } else if ("69".equalsIgnoreCase(byteCode.substring(offset, offset + 2))) {
                    OTALogger.logMessage(" Update Menu File (Change Menu Name) Operation :: ");
                    OTALogger.logMessage(" Menu Positin   = " + hexToDec(byteCode.substring(serviceByteInfo, serviceByteInfo + 2)));
                    OTALogger.logMessage(" Service Id     = " + hexToDec(byteCode.substring(serviceByteInfo + 2, serviceByteInfo + 4)));
                    OTALogger.logMessage(" MajorVersion   = " + hexToDec(byteCode.substring(serviceByteInfo + 4, serviceByteInfo + 6)));
                    OTALogger.logMessage(" MinorVersion   = " + hexToDec(byteCode.substring(serviceByteInfo + 6, serviceByteInfo + 8)));
                    OTALogger.logMessage(" Status         = " + hexToDec(byteCode.substring(serviceByteInfo + 8, serviceByteInfo + 10)));
                } else if ("65".equalsIgnoreCase(byteCode.substring(offset, offset + 2))) {
                    OTALogger.logMessage(" Update Parmatar Operation :: ");
                    value = Integer.parseInt(byteCode.substring(serviceByteInfo, serviceByteInfo + 2)) - 19;
                    OTALogger.logMessage(" Paramter No    = " + value);
                    OTALogger.logMessage(" Status         = " + hexToDec(byteCode.substring(serviceByteInfo + 2, serviceByteInfo + 4)));
                } else if ("67".equalsIgnoreCase(byteCode.substring(offset, offset + 2))) {
                    OTALogger.logMessage(" Update Transaction ID Operation :: " + byteToStringConverter(byteCode.substring(serviceByteInfo, serviceByteInfo + (length * 2))));
                } else if ("68".equalsIgnoreCase(byteCode.substring(offset, offset + 2))) {
                    OTALogger.logMessage(" Update UniCode File Operation :: ");
                    OTALogger.logMessage(" Param Updated  = " + (byteCode.substring(serviceByteInfo, serviceByteInfo + 2)));
                } else if ("57".equalsIgnoreCase(byteCode.substring(offset, offset + 2))) {
                    OTALogger.logMessage(" SIM Enquiry Operation :: ");
                    value = Integer.parseInt(byteCode.substring(serviceByteInfo, serviceByteInfo + 2));
                    if (value == 58) {
                        OTALogger.logMessage(" Enquiry For Service Details   ");
                    } else if (value == 59) {
                        OTALogger.logMessage(" Enquiry For Menu Details ");
                    } else if (value == 60) {
                        OTALogger.logMessage(" Enquiry For Parameters  ");
                    } else if (value == 61) {
                        OTALogger.logMessage(" Enquiry For Offset & Length ");
                    }
                }
                offset = offset + 4 + (length * 2);
                if (offset < byteCode.length()) {
                    length = Integer.parseInt(byteCode.substring(offset + 2, offset + 4), 16);
                    serviceByteInfo = offset + 4;
                }

            } while (offset < byteCode.length());
        } catch (Exception e) {
            _logger.error("returnOperationByteCodeDesc ", " Exception " + e);
            // throw e; //Here exception is omitted because this method is only
            // for desc and if some error occur
            // through logs it can be corrected
        }
    }

    /**
     * This method is used to Convert Hex Value to Decimal Value
     * 
     * @param hexValue
     *            String
     * @return int
     * @throws Exception
     */
    public static int hexToDec(String hexValue) throws BTSLBaseException {
        try {
            int decValue = Integer.parseInt(hexValue, 16);
            return decValue;
        } catch (Exception e) {
            _logger.error("hexToDec ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "hexToDec", "");
        }
    }

    /**
     * This method is used to Convert Hex Value to Decimal Value with Zero
     * padding we also have length conveter for the same but this method
     * is written as length conveter forms BER TLV this is not used in this case
     * and the above method doesn't
     * pad zero if required
     * 
     * @param hexValue
     *            String
     * @return int
     * @throws Exception
     */
    public static String hexToDecZeroPad(String hexValue) throws BTSLBaseException {
        try {
            int value = Integer.parseInt(hexValue);
            String decValue = null;
            if (value < 16) {
                decValue = "0" + Integer.parseInt(hexValue, 16);
            } else {
                decValue = "" + Integer.parseInt(hexValue, 16);
            }
            return decValue;
        } catch (Exception e) {
            _logger.error("hexToDecZeroPad ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "hexToDecZeroPad", "");
        }
    }

    /**
     * This method is used to insert comma after two chars
     * 
     * @param formatString
     *            String
     * @return formattedStringBuf String
     * @throws Exception
     */

    public static String separateValues(String formatString) {
        StringBuffer formattedStringBuf = new StringBuffer();
        try {
            int decimalValue = -1;
            for (int i = 0; i < formatString.length(); i += 2) {
                decimalValue = hexToDec("" + formatString.charAt(i) + formatString.charAt(i + 1));
                formattedStringBuf.append(decimalValue + ",");

            }
        } catch (Exception e) {
            _logger.error("addSpace ", " Exception " + e);
        }
        return formattedStringBuf.toString().trim();
    }

    /**
     * This method is used to give error message for a particular error code
     * 
     * @param number
     *            int
     * @return String
     */
    public static String errorCodes(int number) {
        if (number == 77) {
            return ByteCodeGeneratorI.E77;
        } else if (number == 78) {
            return ByteCodeGeneratorI.E78;
        } else if (number == 79) {
            return ByteCodeGeneratorI.E79;
        } else if (number == 80) {
            return ByteCodeGeneratorI.E80;
        } else if (number == 81) {
            return ByteCodeGeneratorI.E81;
        } else if (number == 82) {
            return ByteCodeGeneratorI.E82;
        } else if (number == 89) {
            return ByteCodeGeneratorI.E89;
        } else if (number == 90) {
            return ByteCodeGeneratorI.E90;
        } else if (number == 85) {
            return ByteCodeGeneratorI.E85;
        } else if (number == 86) {
            return ByteCodeGeneratorI.E86;
        } else if (number == 87) {
            return ByteCodeGeneratorI.E87;
        } else if (number == 88) {
            return ByteCodeGeneratorI.E88;
        } else {
            return "unknow exception type";
        }
    }

    /**
     * This method is used to make a String Length even in length by appending
     * space
     * 
     * @param makeEvenLengthStr
     *            String
     * @return evenString String
     */
    public static String evenStringAddSpace(String makeEvenLengthStr) {
        if (makeEvenLengthStr.length() % 2 == 0) {
            return makeEvenLengthStr;
        } else {
            return makeEvenLengthStr + " ";
        }
    }

    /**
     * This method is used to Comapare the Server Operation List Sent with the
     * response from the SIM and constuct a final list of operations that
     * updates the SIM Image
     * 
     * @param serverList
     *            String
     * @param simList
     *            String
     * @param isActDeactTagincluded
     *            StringBuffer (This variable is used to represent prensence of
     *            act/deact tag in final list true show it contains fails shows
     *            it doesn't contails)
     * @return ArrayList
     */
    public static ArrayList compareServerRequestListWithSIMResponseList(String serverlist, String simlist, StringBuffer actDeactTagincluded) {
        _logger.debug("compareServerRequestListWithSIMResponseList", " Server List" + serverlist + ":: SIM List::" + simlist);
        int serverOffSet = 0;
        int simOffSet = 0;
        int serverService = Integer.parseInt(serverlist.substring(0, 2));
        int serverLength = Integer.parseInt(serverlist.substring(2, 4), 16);
        int simService = Integer.parseInt(simlist.substring(0, 2));
 
        int serviceByteInfo = serverOffSet + 4;
        // StringBuffer testBuf = new StringBuffer();
        ArrayList simImageUpdateOperationList = new ArrayList();
        ArrayList smsOperation = new ArrayList();
        ArrayList langOperation = new ArrayList();
        boolean isSMSOperationFailed = false;// true in case it really fails
        boolean isLangOperationFailed = false;// true in case it really fails
        int count = 0;
        do {
            _logger.debug("", "count         " + (count++));
            if (serverService == simService && (serverService == 51 || serverService == 52)) {
                _logger.debug("", serverService + " " + simService + " " + serverOffSet + " " + simOffSet + " " + serverlist.substring(serverOffSet + 4, serverOffSet + 12) + " " + simlist.substring(simOffSet + 2, simOffSet + 10) + "** **" + simlist.substring(simOffSet + 10, simOffSet + 12));
                if (serverlist.substring(serverOffSet + 4, serverOffSet + 12).equalsIgnoreCase(simlist.substring(simOffSet + 2, simOffSet + 10))) {
                    if ("00".equalsIgnoreCase(simlist.substring(simOffSet + 10, simOffSet + 12))) {
                        simImageUpdateOperationList.add(serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                        // testBuf.append(serverlist.substring(serverOffSet,serverOffSet+(serverLength*2)+4));
                        _logger.debug("", returnTagName(serverService) + " " + serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    } else {
                        _logger.debug("compareServerRequestListWithSIMResponseList", returnTagName(serverService) + "::" + errorCodes(Integer.parseInt(simlist.substring(simOffSet + 10, simOffSet + 12))));
                    }
                }
                simOffSet = simOffSet + 14;
                if (simOffSet >= simlist.length()) {
                    break;
                } else {
                    simService = Integer.parseInt(simlist.substring(simOffSet, simOffSet + 2));
                }
            } else if (serverService == simService && (serverService == 69)) {
                _logger.debug("", serverService + " " + simService + " " + serverOffSet + " " + simOffSet);
                if ("00".equalsIgnoreCase(simlist.substring(simOffSet + 2, simOffSet + 4))) {
                    simImageUpdateOperationList.add(serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    _logger.debug("", returnTagName(serverService) + " " + serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    // testBuf.append(serverlist.substring(serverOffSet,serverOffSet+(serverLength*2)+4));
                } else {
                    _logger.debug("compareServerRequestListWithSIMResponseList ", "" + returnTagName(serverService) + "::" + errorCodes(Integer.parseInt(simlist.substring(simOffSet + 2, simOffSet + 4))));
                }
                simOffSet = simOffSet + 6;// 6 because 20 is also included
                if (simOffSet >= simlist.length()) {
                    break;
                } else {
                    simService = Integer.parseInt(simlist.substring(simOffSet, simOffSet + 2));
                }

            } else if (serverService == simService && (serverService == 53)) {
                _logger.debug("", serverService + " " + simService + " " + serverOffSet + " " + simOffSet);
                if ("00".equalsIgnoreCase(simlist.substring(simOffSet + 2, simOffSet + 4))) {
                    simImageUpdateOperationList.add(serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    _logger.debug("compareServerRequestListWithSIMResponseList*********", actDeactTagincluded);
                    actDeactTagincluded.append("true");
                    _logger.debug("compareServerRequestListWithSIMResponseList*********", actDeactTagincluded);
                    _logger.debug("", returnTagName(serverService) + " " + serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    // testBuf.append(serverlist.substring(serverOffSet,serverOffSet+(serverLength*2)+4));
                } else {
                    _logger.debug("compareServerRequestListWithSIMResponseList ", "" + returnTagName(serverService) + "::" + errorCodes(Integer.parseInt(simlist.substring(simOffSet + 2, simOffSet + 4))));
                }
                simOffSet = simOffSet + 6;// 6 because 20 is also included
                if (simOffSet >= simlist.length()) {
                    break;
                } else {
                    simService = Integer.parseInt(simlist.substring(simOffSet, simOffSet + 2));
                }
            } else if (serverService == simService && (serverService == 65)) {
                _logger.debug("", serverService + " " + simService + " " + serverOffSet + " " + simOffSet);
                if ("00".equalsIgnoreCase(simlist.substring(simOffSet + 2, simOffSet + 4))) {
                    simImageUpdateOperationList.add(serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    _logger.debug("", returnTagName(serverService) + " " + serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    // testBuf.append(serverlist.substring(serverOffSet,serverOffSet+(serverLength*2)+4));
                } else {
                    _logger.debug("compareServerRequestListWithSIMResponseList ", "" + returnTagName(serverService) + "::" + errorCodes(Integer.parseInt(simlist.substring(simOffSet + 2, simOffSet + 4))));
                }
                simOffSet = simOffSet + 6;// 6 because 20 is also included
                if (simOffSet >= simlist.length()) {
                    break;
                } else {
                    simService = Integer.parseInt(simlist.substring(simOffSet, simOffSet + 2));
                }
            } else if (serverService == simService && (serverService == 66)) {
                _logger.debug("", serverService + " " + simService + " " + serverOffSet + " " + simOffSet);
                if ("00".equalsIgnoreCase(simlist.substring(simOffSet + 2, simOffSet + 4))) {
                    smsOperation.add(serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    _logger.debug("", returnTagName(serverService) + " " + serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    // testBuf.append(serverlist.substring(serverOffSet,serverOffSet+(serverLength*2)+4));
                } else {
                    isSMSOperationFailed = true;
                    _logger.debug("compareServerRequestListWithSIMResponseList ::", returnTagName(serverService) + "::" + errorCodes(Integer.parseInt(simlist.substring(simOffSet + 2, simOffSet + 4))));
                }
                simOffSet = simOffSet + 6;// 6 because 20 is also included
                if (simOffSet >= simlist.length()) {
                    break;
                } else {
                    simService = Integer.parseInt(simlist.substring(simOffSet, simOffSet + 2));
                }
            } else if (serverService == simService && (serverService == 67))// Update
                                                                            // TId
                                                                            // in
                                                                            // this
                                                                            // case
                                                                            // no
                                                                            // updation
                                                                            // of
                                                                            // sim
                                                                            // image
                                                                            // takes
                                                                            // place
                                                                            // thats
                                                                            // why
                                                                            // it
                                                                            // is
                                                                            // not
                                                                            // added
                                                                            // in
                                                                            // the
                                                                            // list
            {
                _logger.debug("", serverService + " " + simService + " " + serverOffSet + " " + simOffSet);
                if ("00".equalsIgnoreCase(simlist.substring(simOffSet + 2, simOffSet + 4))) {
                    _logger.debug("", returnTagName(serverService) + " " + serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                } else {
                    _logger.debug("compareServerRequestListWithSIMResponseList ::", returnTagName(serverService) + "::" + errorCodes(Integer.parseInt(simlist.substring(simOffSet + 2, simOffSet + 4))));
                }
                simOffSet = simOffSet + 6;// 6 because 20 is also included
                if (simOffSet >= simlist.length()) {
                    break;
                } else {
                    simService = Integer.parseInt(simlist.substring(simOffSet, simOffSet + 2));
                }
            } else if (serverService == simService && (serverService == 68)) {
                _logger.debug("", serverService + " " + simService + " " + serverOffSet + " " + simOffSet);
                if ("00".equalsIgnoreCase(simlist.substring(simOffSet + 2, simOffSet + 4))) {
                    langOperation.add(serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    _logger.debug("", returnTagName(serverService) + " " + serverlist.substring(serverOffSet, serverOffSet + (serverLength * 2) + 4));
                    // testBuf.append(serverlist.substring(serverOffSet,serverOffSet+(serverLength*2)+4));
                } else {
                    isSMSOperationFailed = true;
                    _logger.debug("compareServerRequestListWithSIMResponseList ::", returnTagName(serverService) + "::" + errorCodes(Integer.parseInt(simlist.substring(simOffSet + 2, simOffSet + 4))));
                }
                simOffSet = simOffSet + 6;// 6 because 20 is also included
                if (simOffSet >= simlist.length()) {
                    break;
                } else {
                    simService = Integer.parseInt(simlist.substring(simOffSet, simOffSet + 2));
                }
            } else {
                _logger.debug("compareServerRequestListWithSIMResponseList::", "Request Not Match Server Request No=" + serverService + " SIM Response =" + simService);
                break;
            }
            serverOffSet = serverOffSet + 4 + (serverLength * 2);
            if (serverOffSet < serverlist.length()) {
                serverService = Integer.parseInt(serverlist.substring(serverOffSet, serverOffSet + 2));
                serverLength = Integer.parseInt(serverlist.substring(serverOffSet + 2, serverOffSet + 4), 16);
                serviceByteInfo = serverOffSet + 4;
            }

        } while (serverOffSet < serverlist.length() && simOffSet < simlist.length());
        if (!isLangOperationFailed && langOperation != null && !langOperation.isEmpty()) {
            simImageUpdateOperationList.add(langOperation.get(0));
        }
        if (!isSMSOperationFailed && smsOperation != null && !smsOperation.isEmpty()) {
            simImageUpdateOperationList.add(smsOperation.get(0));
        }
        return simImageUpdateOperationList;
    }

    /**
     * This method is return Name corrosponding to Tag Value
     * 
     * @param noValue
     *            int
     * @return tagName String
     */
    public static String returnTagName(int noValue) {
        switch (noValue) {
        case 51:
            return "Add Tag : ";
        case 52:
            return "Delete Tag : ";
        case 53:
            return "Activation/Deactivation Tag : ";
        case 69:
            return "Update Menu File Tag : ";
        case 65:
            return "Update Parameters Tag : ";
        case 66:
            return "Update SMS Parameters Tag : ";
        case 67:
            return "Update TID Tag : ";
        case 68:
            return "Update Unicode File Tag : ";
        case 57:
            return "Sim Enquiry Tag : ";
        default:
            return "Unknow Tag : ";
        }

    }

    /**
     * This method is used in flat file generation its main aim is to provide
     * some gap between two sms to a single mobile no.
     * 
     * @param buf1
     *            StringBuffer
     * @param buf2
     *            StringBuffer
     * @param buf3
     *            StringBuffer
     * @param buf4
     *            StringBuffer
     * @param buf5
     *            StringBuffer
     * @param buf6
     *            StringBuffer
     * @param buf7
     *            StringBuffer
     * @param mobileNo
     *            String
     * @param consValue
     *            String
     * @param separator
     *            String
     * @param smsList
     *            ArrayList
     * @throws Exception
     */
    public void flatFileSmsSeparator(StringBuffer buf1, StringBuffer buf2, StringBuffer buf3, StringBuffer buf4, StringBuffer buf5, StringBuffer buf6, StringBuffer buf7, String mobileNo, String consValue, String separator, ArrayList smsList) throws BTSLBaseException {
        try {
            int size = smsList.size();
            if (size <= 0 || size > 7) {
                throw new BTSLBaseException("Size Exceeds 7 SMS");
            }
            switch (size) {
            case 1:
                buf1.append(mobileNo + " " + (String) smsList.get(0) + " " + consValue + separator);
                break;
            case 2:
                buf1.append(mobileNo + " " + (String) smsList.get(0) + " " + consValue + separator);
                buf2.append(mobileNo + " " + (String) smsList.get(1) + " " + consValue + separator);
                break;
            case 3:
                buf1.append(mobileNo + " " + (String) smsList.get(0) + " " + consValue + separator);
                buf2.append(mobileNo + " " + (String) smsList.get(1) + " " + consValue + separator);
                buf3.append(mobileNo + " " + (String) smsList.get(2) + " " + consValue + separator);
                break;
            case 4:
                buf1.append(mobileNo + " " + (String) smsList.get(0) + " " + consValue + separator);
                buf2.append(mobileNo + " " + (String) smsList.get(1) + " " + consValue + separator);
                buf3.append(mobileNo + " " + (String) smsList.get(2) + " " + consValue + separator);
                buf4.append(mobileNo + " " + (String) smsList.get(3) + " " + consValue + separator);
                break;
            case 5:
                buf1.append(mobileNo + " " + (String) smsList.get(0) + " " + consValue + separator);
                buf2.append(mobileNo + " " + (String) smsList.get(1) + " " + consValue + separator);
                buf3.append(mobileNo + " " + (String) smsList.get(2) + " " + consValue + separator);
                buf4.append(mobileNo + " " + (String) smsList.get(3) + " " + consValue + separator);
                buf5.append(mobileNo + " " + (String) smsList.get(4) + " " + consValue + separator);
                break;
            case 6:
                buf1.append(mobileNo + " " + (String) smsList.get(0) + " " + consValue + separator);
                buf2.append(mobileNo + " " + (String) smsList.get(1) + " " + consValue + separator);
                buf3.append(mobileNo + " " + (String) smsList.get(2) + " " + consValue + separator);
                buf4.append(mobileNo + " " + (String) smsList.get(3) + " " + consValue + separator);
                buf5.append(mobileNo + " " + (String) smsList.get(4) + " " + consValue + separator);
                buf6.append(mobileNo + " " + (String) smsList.get(5) + " " + consValue + separator);
                break;
            case 7:
                buf1.append(mobileNo + " " + (String) smsList.get(0) + " " + consValue + separator);
                buf2.append(mobileNo + " " + (String) smsList.get(1) + " " + consValue + separator);
                buf3.append(mobileNo + " " + (String) smsList.get(2) + " " + consValue + separator);
                buf4.append(mobileNo + " " + (String) smsList.get(3) + " " + consValue + separator);
                buf5.append(mobileNo + " " + (String) smsList.get(4) + " " + consValue + separator);
                buf6.append(mobileNo + " " + (String) smsList.get(5) + " " + consValue + separator);
                buf7.append(mobileNo + " " + (String) smsList.get(6) + " " + consValue + separator);
                break;
            default:
              	 if(_logger.isDebugEnabled()){
              		_logger.debug("Default Value " , size);
              	 }
            }
        } catch (Exception e) {
            _logger.error("flatFileSmsSeparator :: ", e);
            throw new BTSLBaseException(CLASS_NAME, "flatFileSmsSeparator", "");
        }
    }

    /**
     * This method is used interprate infomation for sim enquiry C
     * 
     * @param byteStr
     *            String (infomation to be interpret)
     * @param simVO
     *            SimVO
     * @param smsVO
     *            SmsVO
     * @throws Exception
     */
    public void interpretSimEnquiryC(String byteStr, SimVO simVO, SmsVO smsVO) throws BTSLBaseException {
        _logger.debug("interpretSimEnquiryC()", " Entered");
        try {
            String flags = null;
            String SMSSetting1 = null;
            String SMSSetting2 = null;
            String SMSSetting3 = null;
            flags = byteStr.substring(0, 20);
            String data = null;
            int caseValue = 0;
            int i = 0;
            for (i = 0, caseValue = 0; i < flags.length(); i = i + 2, caseValue++) {
                if ("00".equalsIgnoreCase(flags.substring(i, i + 2))) {
                    data = "N";
                } else if ("01".equalsIgnoreCase(flags.substring(i, i + 2))) {
                    data = "Y";
                } else {
                    data = flags.substring(i + 1, i + 2);
                }

                setDataSimValue(21 + caseValue, data, simVO);
                /*
                 * switch(i)
                 * {
                 * case 0:
                 * simVO.setParam1(data);
                 * break;
                 * case 2:
                 * simVO.setParam2(data);
                 * break;
                 * case 4:
                 * simVO.setParam3(data);
                 * break;
                 * case 6:
                 * simVO.setParam4(data);
                 * break;
                 * case 8:
                 * simVO.setParam5(data);
                 * break;
                 * case 10:
                 * simVO.setParam6(data);
                 * break;
                 * case 12:
                 * simVO.setParam7(data);
                 * break;
                 * case 14:
                 * simVO.setParam8(data);
                 * break;
                 * case 16:
                 * simVO.setParam9(data);
                 * break;
                 * case 18:
                 * simVO.setParam10(data);
                 * break;
                 * }
                 */
            }
            smsVO.setLocation(simVO.getLocationCode());
            SMSSetting1 = byteStr.substring(22, 62);
            SMSSetting2 = byteStr.substring(64, 104);
            SMSSetting3 = byteStr.substring(106, 146);
            _logger.debug("flags=", flags);
            _logger.debug("SMSSetting1=", SMSSetting1);
            _logger.debug("SMSSetting2=", SMSSetting2);
            _logger.debug("SMSSetting3=", SMSSetting3);
            String smscPortVP = smscPortVPHandle(SMSSetting1);
            int vp = 0;
            smsVO.setSmsc1(smscPortVP.substring(0, smscPortVP.indexOf("|")));
            smsVO.setPort1(smscPortVP.substring(smscPortVP.indexOf("|") + 1, smscPortVP.lastIndexOf("|")));
            vp = (Integer.parseInt(smscPortVP.substring(smscPortVP.lastIndexOf("|") + 1)) + 1) * 5;
            smsVO.setVp1(vp);

            smscPortVP = smscPortVPHandle(SMSSetting2);
            smsVO.setSmsc2(smscPortVP.substring(0, smscPortVP.indexOf("|")));
            smsVO.setPort2(smscPortVP.substring(smscPortVP.indexOf("|") + 1, smscPortVP.lastIndexOf("|")));
            vp = (Integer.parseInt(smscPortVP.substring(smscPortVP.lastIndexOf("|") + 1)) + 1) * 5;
            // smsVO.setVp2(Integer.parseInt(smscPortVP.substring(smscPortVP.lastIndexOf("|")+1)));
            smsVO.setVp2(vp);
            smscPortVP = smscPortVPHandle(SMSSetting3);
            smsVO.setSmsc3(smscPortVP.substring(0, smscPortVP.indexOf("|")));
            smsVO.setPort3(smscPortVP.substring(smscPortVP.indexOf("|") + 1, smscPortVP.lastIndexOf("|")));
            vp = (Integer.parseInt(smscPortVP.substring(smscPortVP.lastIndexOf("|") + 1)) + 1) * 5;
            // smsVO.setVp3(Integer.parseInt(smscPortVP.substring(smscPortVP.lastIndexOf("|")+1)));
            smsVO.setVp3(vp);
        } catch (Exception e) {
            _logger.error("interpretSimEnquiryC()", " Exception e=" + e);
            throw new BTSLBaseException(CLASS_NAME, "interpretSimEnquiryC", "");
        } finally {
            _logger.debug("interpretSimEnquiryC() ", "Exiting");
        }
    }

    /**
     * This method is used for setting the service information in SimVO(used in
     * simEnquiryA and Reg time)
     * 
     * @param byteStr
     *            String (infomation to be interpret)
     * @param simVO
     *            SimVO
     * @return ArrayList
     * @throws Exception
     */
    public ArrayList constructArrayListFromEnquiry(String smsStr, SimVO simVO) throws BTSLBaseException {
        final String METHOD_NAME = "constructArrayListFromEnquiry";
        _logger.debug("constructArrayListFromEnquiry()", " Entered");
        try {
            String byteStr = smsStr;
            String serviceStr = null;
            ServicesVO servicesVO = null;
            String status = null;// 1 byte
            String serviceID = null;// 1 byte
            String majorVersion = null;// 1 byte
            String minorVersion = null;// 1 byte
            ArrayList servicesList = new ArrayList();
            int totalByteLength = 0;
            try {
                totalByteLength = Integer.parseInt(Constants.getProperty("byteLength"));
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                totalByteLength = 128;
            }
            _logger.debug("constructArrayListFromEnquiry() ", "totalByteLength=" + totalByteLength);
            int j = 0;
            String data = null;
            for (int i = 0; i < totalByteLength; i = i + 8) {
                ++j;
                servicesVO = new ServicesVO();
                serviceStr = byteStr.substring(i, i + 8);
                status = serviceStr.substring(0, 2);
                if ("00".equals(status) || "01".equals(status)) {
                    serviceID = serviceStr.substring(2, 4);
                    majorVersion = serviceStr.substring(4, 6);
                    minorVersion = serviceStr.substring(6, 8);
                    servicesVO.setPosition(j);
                    data = serviceID + majorVersion + minorVersion + status;
                    servicesVO.setStatus(Integer.parseInt(status, 16) == 0 ? "N" : "Y");
                    servicesVO.setServiceID("" + Integer.parseInt(serviceID, 16));
                    servicesVO.setMajorVersion("" + Integer.parseInt(majorVersion, 16));
                    servicesVO.setMinorVersion("" + Integer.parseInt(minorVersion, 16));
                    servicesList.add(servicesVO);
                    _logger.debug("constructArrayListFromEnquiry() ", " Position:" + (j) + "   serviceID:" + servicesVO.getServiceID() + " majorVersion:" + servicesVO.getMajorVersion() + " minorVersion:" + servicesVO.getMinorVersion() + " status=" + (("01").equals(status) ? "Activated" : "Deactivated"));
                } else {
                    data = "FFFFFFFF";
                }

                setDataSimValue(j, data, simVO);// This is the subtitute of the
                                                // below code and on testing the
                                                // below code should be removed
                /*
                 * switch(j)
                 * {
                 * case 1:
                 * 
                 * simVO.setService1(data);
                 * break ;
                 * case 2:
                 * simVO.setService2(data);
                 * break ;
                 * case 3:
                 * simVO.setService3(data);
                 * break ;
                 * case 4:
                 * simVO.setService4(data);
                 * break ;
                 * case 5:
                 * simVO.setService5(data);
                 * break ;
                 * case 6:
                 * simVO.setService6(data);
                 * break ;
                 * case 7:
                 * simVO.setService7(data);
                 * break ;
                 * case 8:
                 * simVO.setService8(data);
                 * break ;
                 * case 9:
                 * simVO.setService9(data);
                 * break ;
                 * case 10:
                 * simVO.setService10(data);
                 * break ;
                 * case 11:
                 * simVO.setService11(data);
                 * break ;
                 * case 12:
                 * simVO.setService12(data);
                 * break ;
                 * case 13:
                 * simVO.setService13(data);
                 * break ;
                 * case 14:
                 * simVO.setService14(data);
                 * break ;
                 * case 15:
                 * simVO.setService15(data);
                 * break ;
                 * case 16:
                 * simVO.setService16(data);
                 * break ;
                 * }
                 */
            }
            return servicesList;
        } catch (Exception e) {
            _logger.error("constructArrayListFromEnquiry()", " Exception e=" + e);
            throw new BTSLBaseException(CLASS_NAME, "constructArrayListFromEnquiry", "");
        } finally {
            _logger.debug("constructArrayListFromEnquiry()", " Entered");
        }
    }

    /**
     * This method compares the list from the sim and from the service and
     * creates an list for updation
     * 
     * @param simList
     *            ArrayList
     * @param serverList
     *            ArrayList(This list is brought from user sim services)
     * @param simServicesListHp
     *            ArrayList(This list is bought from sim services)
     * @param userType
     *            String (user type can be dist , ret or SE)
     * @return ArrayList
     * @throws Exception
     */
    public static ArrayList getUpdatedSIMServicesList(ArrayList simList, ArrayList serverList, HashMap simServicesListHp, String userType) throws BTSLBaseException {
        _logger.debug("getUpdatedSIMServicesList()", " Entered");
        try {
            ServicesVO simServicesVO = null;
            UserServicesVO serverServicesVO = null;
            ArrayList list = new ArrayList();
            boolean isAdd = true;
            boolean isDelete = true;
            String deactivatePositions = null;
            String activatePositions = null;
            for (int i = 0; i < serverList.size(); i++) {
                isAdd = true;
                serverServicesVO = (UserServicesVO) serverList.get(i);
                for (int j = 0; j < simList.size(); j++) {
                    simServicesVO = (ServicesVO) simList.get(j);
                    if ((serverServicesVO.getPosition() == simServicesVO.getPosition()) && serverServicesVO.getServiceID().equals(simServicesVO.getServiceID()) && serverServicesVO.getMajorVersion().equals(simServicesVO.getMajorVersion()) && (serverServicesVO.getMinorVersion().equals(simServicesVO.getMinorVersion())) && (serverServicesVO.getStatus().equals(simServicesVO.getStatus()))) {
                        _logger.debug("", "Equal service ID=" + simServicesVO.getServiceID());
                        isAdd = false;
                    } else if ((serverServicesVO.getPosition() == simServicesVO.getPosition()) && serverServicesVO.getServiceID().equals(simServicesVO.getServiceID()) && serverServicesVO.getMajorVersion().equals(simServicesVO.getMajorVersion()) && serverServicesVO.getMinorVersion().equals(simServicesVO.getMinorVersion()) && (!serverServicesVO.getStatus().equals(simServicesVO.getStatus()))) {
                        // Status Change
                        _logger.debug("", "status change service ID=" + simServicesVO.getServiceID());
                        if ("N".equals(serverServicesVO.getStatus())) {
                            if (deactivatePositions == null) {
                                deactivatePositions = "" + simServicesVO.getPosition();
                            } else {
                                deactivatePositions = deactivatePositions + "," + simServicesVO.getPosition();
                            }
                        } else {
                            if (activatePositions == null) {
                                activatePositions = "" + simServicesVO.getPosition();
                            } else {
                                activatePositions = activatePositions + "," + simServicesVO.getPosition();
                            }
                        }
                        isAdd = false;
                        break;
                    } else if ((serverServicesVO.getPosition() == simServicesVO.getPosition()) && serverServicesVO.getServiceID().equals(simServicesVO.getServiceID()) && serverServicesVO.getMajorVersion().equals(simServicesVO.getMajorVersion()) && (!serverServicesVO.getMinorVersion().equals(simServicesVO.getMinorVersion())) && serverServicesVO.getStatus().equals(simServicesVO.getStatus())) {
                        // Menu Title Change
                        _logger.debug("", "menu title change service ID=" + simServicesVO.getServiceID());
                        simServicesVO.setOperation(ByteCodeGeneratorI.CHANGE_TITLE);
                        simServicesVO.setLabel1(serverServicesVO.getLabel1());
                        simServicesVO.setLabel2(serverServicesVO.getLabel2());
                        simServicesVO.setStatus(serverServicesVO.getStatus());
                        simServicesVO.setOffSet(serverServicesVO.getOffset());
                        simServicesVO.setLength(serverServicesVO.getLength());
                        list.add(simServicesVO);
                        isAdd = false;
                        break;
                    } else if ((serverServicesVO.getPosition() == simServicesVO.getPosition()) && serverServicesVO.getServiceID().equals(simServicesVO.getServiceID()) && serverServicesVO.getMajorVersion().equals(simServicesVO.getMajorVersion()) && (!serverServicesVO.getMinorVersion().equals(simServicesVO.getMinorVersion())) && (!serverServicesVO.getStatus().equals(simServicesVO.getStatus()))) {
                        _logger.debug("", "both change service ID=" + simServicesVO.getServiceID());
                        // Menu Title Change and Status Change
                        simServicesVO.setLabel1(serverServicesVO.getLabel1());
                        simServicesVO.setLabel2(serverServicesVO.getLabel2());
                        simServicesVO.setStatus(serverServicesVO.getStatus());
                        simServicesVO.setOffSet(serverServicesVO.getOffset());
                        simServicesVO.setLength(serverServicesVO.getLength());
                        if ("N".equals(serverServicesVO.getStatus())) {
                            if (deactivatePositions == null) {
                                deactivatePositions = "" + simServicesVO.getPosition();
                            } else {
                                deactivatePositions = deactivatePositions + "," + simServicesVO.getPosition();
                            }
                        } else {
                            if (activatePositions == null) {
                                activatePositions = "" + simServicesVO.getPosition();
                            } else {
                                activatePositions = activatePositions + "," + simServicesVO.getPosition();
                            }
                        }
                        simServicesVO.setOperation(ByteCodeGeneratorI.CHANGE_TITLE);
                        list.add(simServicesVO);
                        isAdd = false;
                        break;
                    }

                }
                // This code is commented for some time (after discussion with
                // sanjay and dharmendra sir
                // and finally after the new desigh (having one more master)
                // this code will again be uncommented
                /*
                 * if(isAdd)
                 * {
                 * _logger.debug("add service ID="+serverServicesVO.getServiceID(
                 * ));
                 * simServicesVO=new ServicesVO();
                 * simServicesVO.setPosition(serverServicesVO.getPosition());
                 * if(serverServicesVO.getStatus()==null)
                 * simServicesVO.setStatus("N");
                 * else
                 * simServicesVO.setStatus(serverServicesVO.getStatus());
                 * simServicesVO.setServiceID(serverServicesVO.getServiceID());
                 * simServicesVO.setMajorVersion(serverServicesVO.getMajorVersion
                 * ());
                 * simServicesVO.setMinorVersion(serverServicesVO.getMinorVersion
                 * ());
                 * simServicesVO.setOffSet(serverServicesVO.getOffset());
                 * simServicesVO.setLength(serverServicesVO.getLength());
                 * simServicesVO.setLabel1(serverServicesVO.getLabel1());
                 * simServicesVO.setLabel2(serverServicesVO.getLabel2());
                 * simServicesVO.setByteCode(serverServicesVO.getByteCode());
                 * simServicesVO.setOperation(ByteCodeGeneratorI.ADD);
                 * key = hexToDecZeroPad(serverServicesVO.getServiceID())+
                 * hexToDecZeroPad
                 * (serverServicesVO.getMajorVersion())+hexToDecZeroPad
                 * (serverServicesVO.getMinorVersion());
                 * value = (String)simServicesListHp.get(key.toUpperCase());
                 * if(!BTSLUtil.isNullString(value))
                 * {
                 * 
                 * if(value.indexOf(userType.substring(0,1))!=-1) //means if
                 * value = D or S or R then ok otherwise not
                 * list.add(simServicesVO);
                 * }
                 * //Add Server VO
                 * }
                 */
            }
            for (int i = 0; i < simList.size(); i++) {
                simServicesVO = (ServicesVO) simList.get(i);
                isDelete = true;
                for (int j = 0; j < serverList.size(); j++) {
                    serverServicesVO = (UserServicesVO) serverList.get(j);
                    if ((serverServicesVO.getPosition() == simServicesVO.getPosition()) && serverServicesVO.getServiceID().equals(simServicesVO.getServiceID()) && serverServicesVO.getMajorVersion().equals(simServicesVO.getMajorVersion())) {
                        isDelete = false;
                        break;
                    }
                }
                if (isDelete) {
                    // simServicesVO.setOperation(DELETE);
                    // st.add(simServicesVO);
                    // Delete SIM VO
                    if (deactivatePositions == null) {
                        deactivatePositions = "" + simServicesVO.getPosition();
                    } else {
                        deactivatePositions = deactivatePositions + "," + simServicesVO.getPosition();
                    }
                }
            }
            if (!BTSLUtil.isNullString(deactivatePositions)) {
                _logger.debug("", "deactivatePositions=" + deactivatePositions);
                simServicesVO = new ServicesVO();
                simServicesVO.setPositionList(deactivatePositions);
                simServicesVO.setOperation(ByteCodeGeneratorI.DEACTIVATE);
                simServicesVO.setStatus("N");
                list.add(simServicesVO);
            }
            if (!BTSLUtil.isNullString(activatePositions)) {
                _logger.debug("", "activatePositions=" + activatePositions);
                simServicesVO = new ServicesVO();
                simServicesVO.setPositionList(activatePositions);
                simServicesVO.setOperation(ByteCodeGeneratorI.ACTIVATE);
                simServicesVO.setStatus("Y");
                list.add(simServicesVO);
            }
            return list;
        } catch (Exception e) {
            _logger.error("getUpdatedSIMServicesList", " Exception e=" + e);
            throw new BTSLBaseException(CLASS_NAME, "getUpdatedSIMServicesList", "");
        } finally {
            _logger.debug("getUpdatedSIMServicesList() ", "Exiting");

        }
    }

    /**
     * This Method is used for Inserting those details in the SimVO that are
     * left empty
     * 
     * @param simVO
     *            SimVO
     * @exception Exception
     */
    public void insertDetailsInSimVO(SimVO simVO) throws Exception {
        _logger.debug("insertDetailsInSimVO()", " Entered ..................");
        if (BTSLUtil.isNullString(simVO.getService1())) {
            simVO.setService1("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService2())) {
            simVO.setService2("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService3())) {
            simVO.setService3("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService4())) {
            simVO.setService4("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService5())) {
            simVO.setService5("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService6())) {
            simVO.setService6("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService7())) {
            simVO.setService7("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService8())) {
            simVO.setService8("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService9())) {
            simVO.setService9("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService10())) {
            simVO.setService10("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService11())) {
            simVO.setService11("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService12())) {
            simVO.setService12("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService13())) {
            simVO.setService13("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService14())) {
            simVO.setService14("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService15())) {
            simVO.setService15("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService16())) {
            simVO.setService16("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService17())) {
            simVO.setService17("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService18())) {
            simVO.setService18("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService19())) {
            simVO.setService19("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getService20())) {
            simVO.setService20("ALL");
        }

        if (BTSLUtil.isNullString(simVO.getParam1())) {
            simVO.setParam1("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getParam2())) {
            simVO.setParam2("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getParam3())) {
            simVO.setParam3("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getParam4())) {
            simVO.setParam4("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getParam5())) {
            simVO.setParam5("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getParam6())) {
            simVO.setParam6("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getParam7())) {
            simVO.setParam7("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getParam8())) {
            simVO.setParam8("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getParam9())) {
            simVO.setParam9("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getParam10())) {
            simVO.setParam10("ALL");
        }

        if (BTSLUtil.isNullString(simVO.getLangRef())) {
            simVO.setLangRef("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getSmsRef())) {
            simVO.setSmsRef("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getSimEnquiryRes())) {
            simVO.setSimEnquiryRes("ALL");
        }
        if (BTSLUtil.isNullString(simVO.getStatus())) {
            simVO.setStatus("ALL");
        }

        _logger.debug("insertDetailsInSimVO()", " Existing ..................");
    }

    /**
     * This method is to set data for the SIM VO
     * 
     * @param caseValue
     *            int(case value is used to set different data items of the Sim
     *            VO)
     * @param data
     *            String (data is the actual value that is set in that item )
     * @param simVO
     *            SimVO
     */
    public void setDataSimValue(int caseValue, String data, SimVO simVO) {
        switch (caseValue) {
        case 1:
            simVO.setService1(data);
            break;
        case 2:
            simVO.setService2(data);
            break;
        case 3:
            simVO.setService3(data);
            break;
        case 4:
            simVO.setService4(data);
            break;
        case 5:
            simVO.setService5(data);
            break;
        case 6:
            simVO.setService6(data);
            break;
        case 7:
            simVO.setService7(data);
            break;
        case 8:
            simVO.setService8(data);
            break;
        case 9:
            simVO.setService9(data);
            break;
        case 10:
            simVO.setService10(data);
            break;
        case 11:
            simVO.setService11(data);
            break;
        case 12:
            simVO.setService12(data);
            break;
        case 13:
            simVO.setService13(data);
            break;
        case 14:
            simVO.setService14(data);
            break;
        case 15:
            simVO.setService15(data);
            break;
        case 16:
            simVO.setService16(data);
            break;
        case 17:
            simVO.setService17(data);
            break;
        case 18:
            simVO.setService18(data);
            break;
        case 19:
            simVO.setService19(data);
            break;
        case 20:
            simVO.setService20(data);
            break;
        case 21:
            simVO.setParam1(data);
            break;
        case 22:
            simVO.setParam2(data);
            break;
        case 23:
            simVO.setParam3(data);
            break;
        case 24:
            simVO.setParam4(data);
            break;
        case 25:
            simVO.setParam5(data);
            break;
        case 26:
            simVO.setParam6(data);
            break;
        case 27:
            simVO.setParam7(data);
            break;
        case 28:
            simVO.setParam8(data);
            break;
        case 29:
            simVO.setParam9(data);
            break;
        case 30:
            simVO.setParam10(data);
            break;

        case 66:
            simVO.setSmsRef(data);
            break;
        case 68:
            simVO.setLangRef(data);
            break;

        default:

        }

    }

    /**
     * This method is to get data for the SIM VO(imp in case this method is used
     * to get data for any param one should be added in the calling function)
     * 
     * @param caseValue
     *            int(case value is used to set different data items of the Sim
     *            VO)
     * @param data
     *            String (data is the actual value that is set in that item )
     * @param simVO
     *            SimVO
     */

    public String getDataSimValue(int caseValue, SimVO simVO) {
        switch (caseValue) {
        case 1:
            return simVO.getService1();
        case 2:
            return simVO.getService2();
        case 3:
            return simVO.getService3();
        case 4:
            return simVO.getService4();
        case 5:
            return simVO.getService5();
        case 6:
            return simVO.getService6();
        case 7:
            return simVO.getService7();
        case 8:
            return simVO.getService8();
        case 9:
            return simVO.getService9();
        case 10:
            return simVO.getService10();
        case 11:
            return simVO.getService11();
        case 12:
            return simVO.getService12();
        case 13:
            return simVO.getService13();
        case 14:
            return simVO.getService14();
        case 15:
            return simVO.getService15();
        case 16:
            return simVO.getService16();
        case 17:
            return simVO.getService17();
        case 18:
            return simVO.getService18();
        case 19:
            return simVO.getService19();
        case 20:
            return simVO.getService20();
        case 21:
            return simVO.getParam1();
        case 22:
            return simVO.getParam2();
        case 23:
            return simVO.getParam3();
        case 24:
            return simVO.getParam4();
        case 25:
            return simVO.getParam5();
        case 26:
            return simVO.getParam6();
        case 27:
            return simVO.getParam7();
        case 28:
            return simVO.getParam8();
        case 29:
            return simVO.getParam9();
        case 66:
            return simVO.getSmsRef();
        case 68:
            return simVO.getLangRef();
        default:
            return null;

        }
    }

    /**
     * This method check whether of not Mobile entry exists in the SIM Image
     * table or not if exists then Ok other wise create a entry
     * 
     * @param wholeSMSInfo
     *            String
     * @return String
     * @throws Exception
     */
    public static String getTID(String wholeSMSInfo) throws Exception {
        String tid = null;
        int indexOfTX = wholeSMSInfo.indexOf("205458");
        if (indexOfTX == -1)// not found
        {
            _logger.error("getTID", " No TID Found Returning Null");
            return null;
        } else {
            tid = wholeSMSInfo.substring(indexOfTX + 6, indexOfTX + 14);
        }
        _logger.debug("getTID :", " Transaction = " + wholeSMSInfo.substring(indexOfTX, indexOfTX + 2) + " Value is =" + tid);
        return tid;

    }

    /**
     * This method gives the response bytecode from the sim (means the
     * operations that are performed by the sim are successfull or not and what
     * were they)
     * 
     * @param wholeSMSInfo
     *            String
     * @return String
     * @throws Exception
     */
    public static String getResponse(String wholeSMSInfo) throws Exception {
        String response = null;
        int startingIndex = 20;// First 10 Bytes are fixed data
        int indexOfTX = wholeSMSInfo.indexOf("205458");
        if (indexOfTX == -1)// not found
        {
            _logger.error("getResponse", " Response not found");
            return null;
        } else {
            if (startingIndex > indexOfTX) {
                _logger.error("getResponse", "Wrong Response String Starting Index = " + startingIndex + " End Index " + indexOfTX);
                return null;
            } else {
                response = wholeSMSInfo.substring(startingIndex, indexOfTX + 2);// +2
                                                                                // is
                                                                                // done
                                                                                // as
                                                                                // to
                                                                                // include
                                                                                // 20
                                                                                // from
                                                                                // it
            }
        }
        _logger.debug("getResponse ", " Response is = " + wholeSMSInfo.substring(startingIndex, indexOfTX + 2));
        return response;
    }

    /**
     * This method is used to set information related with all the services in
     * the ServiceVO means conversion of inforamtion from Hex to understandable
     * form
     * 
     * @param listOfServiceVO
     *            ArrayList
     * @return String
     */
    public static String servicesInfoForQueryA(ArrayList listOfServiceVO) {
        StringBuffer serviceIDBuf = new StringBuffer();
        try {
            ServicesVO servicesVO = null;
            int size = listOfServiceVO.size();
            if (size == 0 || listOfServiceVO.isEmpty()) {
                _logger.debug("servicesInfoForQueryA ", " listOfServiceVO empty or size 0 ");
                return null;
            }
            for (int i = 0; i < size; i++) {
                servicesVO = (ServicesVO) listOfServiceVO.get(i);
                if (BTSLUtil.isNullString(servicesVO.getCompareHexString())) {
                    servicesVO.setServiceID("FF");
                } else if ("FFFFFFFF".equalsIgnoreCase(servicesVO.getCompareHexString())) {
                    servicesVO.setServiceID("FF");
                } else {
                    servicesVO.setServiceID("" + hexToDec(servicesVO.getCompareHexString().substring(0, 2)));
                    servicesVO.setMajorVersion("" + hexToDec(servicesVO.getCompareHexString().substring(2, 4)));
                    servicesVO.setMinorVersion("" + hexToDec(servicesVO.getCompareHexString().substring(4, 6)));
                    servicesVO.setStatus(("00").equalsIgnoreCase(servicesVO.getCompareHexString().substring(6, 8)) ? "N" : "Y");
                    serviceIDBuf.append("'" + servicesVO.getServiceID() + "',");
                }
            }
            if (!BTSLUtil.isNullString(serviceIDBuf.toString())) {
                return serviceIDBuf.toString().substring(0, serviceIDBuf.toString().lastIndexOf(','));
            }
        } catch (Exception e) {
            _logger.error("", "servicesInfoForQueryA :: " + e);
            return null;
        }
        return serviceIDBuf.toString().substring(0, serviceIDBuf.toString().lastIndexOf(','));
    }

    /**
     * This method is used interprate infomation for sim enquiry B or D
     * 
     * @param hexCode
     *            String (infomation to be interprete)
     * @param typeOfEnquiry
     *            (B or D)
     * @return ArrayList
     * @throws Exception
     */
    public static ArrayList servicesInfoForQueryBD(String hexCode, String typeOfEnquiry) throws BTSLBaseException {
        final String METHOD_NAME = "servicesInfoForQueryBD";
        ArrayList serviceInfoForQueryBD = null;
        try {
            String menuPosition = null;
            String byteCodeOffset = null;
            String status = null;// 1 byte
            String serviceID = null;// 1 byte
            String majorVersion = null;// 1 byte
            String menuTitle = null;// 1 byte
            String minorVersion = null;// 1 byte
            String byteCodeLength = null;
            String enquiry = null;
            int offset = 0;
            int length = 0;

            if (BTSLUtil.isNullString(hexCode) || BTSLUtil.isNullString(typeOfEnquiry)) {
                // throw new
                // Exception("Either Info part is null or type of enquiry is null");
                throw new BTSLBaseException(SimUtil.class, "servicesInfoForQueryBD", "services.simenquiry.error.wrongenq");
            }
            if ("B".equalsIgnoreCase(typeOfEnquiry) || "D".equalsIgnoreCase(typeOfEnquiry)) {
                enquiry = typeOfEnquiry.toUpperCase();
            } else {
                // throw new Exception("Type of Enquiry should be B or D");
                throw new BTSLBaseException(SimUtil.class, "servicesInfoForQueryBD", "services.simenquiry.error.typeofenq");
            }

            if (!enquiry.equalsIgnoreCase(byteToStringConverter(hexCode.substring(16, 18)))) {
                // throw new
                // Exception("Type of Enquiry("+enquiry+") from request doesn't match with database("+byteToStringConverter(hexCode.substring(16,18))
                // +")");
                throw new BTSLBaseException(SimUtil.class, "servicesInfoForQueryBD", "services.simenquiry.error.typeofenq");
            }

            String response = SimUtil.getResponse(hexCode);
            if (BTSLUtil.isNullString(response)) {
                // throw new Exception("Response String is NULL");
                throw new BTSLBaseException(SimUtil.class, "servicesInfoForQueryBD", "services.simenquiry.error.nullresponse");
            } else {
                _logger.debug("servicesInfoForQueryBD::Response ", " " + response);
            }
            serviceInfoForQueryBD = new ArrayList();
            ServicesVO sVO = null;
            if ("B".equalsIgnoreCase(enquiry)) {
                sVO = new ServicesVO();
                int j = 0;
                menuPosition = "" + Integer.parseInt(response.substring(0, 2), 16);
                sVO.setPosition(Integer.parseInt(response.substring(0, 2), 16));
                _logger.info("", "menuPosition=" + menuPosition);
                status = response.substring(2, 4);
                if (!"00".equalsIgnoreCase(status) && !"01".equalsIgnoreCase(status)) {
                    sVO.setStatus("deleted");
                } else {
                    _logger.info("", "status=" + ("01".equals(status) ? "Activated" : "Deactivated"));
                    sVO.setStatus("01".equals(status) ? "Activated" : "Deactivated");
                    serviceID = "" + Integer.parseInt(response.substring(4, 6), 16);
                    sVO.setServiceID(serviceID);
                    _logger.info("", "serviceID=" + serviceID);
                    majorVersion = "" + Integer.parseInt(response.substring(6, 8), 16);
                    sVO.setMajorVersion(majorVersion);
                    _logger.info("", "majorVersion=" + majorVersion);
                    minorVersion = "" + Integer.parseInt(response.substring(8, 10), 16);
                    sVO.setMinorVersion(minorVersion);
                    _logger.info("", "minorVersion=" + minorVersion);
                    byteCodeOffset = "" + Integer.parseInt(response.substring(10, 14), 16);
                    sVO.setOffSet(Integer.parseInt(response.substring(10, 14), 16));
                    _logger.info("", "byteCodeOffset=" + byteCodeOffset);
                    byteCodeLength = "" + Integer.parseInt(response.substring(14, 18), 16);
                    sVO.setLength(Integer.parseInt(response.substring(14, 18), 16));
                    _logger.info("", "byteCodeLength=" + byteCodeLength);
                    menuTitle = response.substring(18, 98);
                    length = Integer.parseInt(menuTitle.substring(offset + 2, offset + 4), 16);
                    sVO.setLabel1(byteToStringConverter(menuTitle.substring(offset + 4, offset + 4 + length * 2)));
                    _logger.info("", "Label1 =" + sVO.getLabel1());
                    offset = 4 + length * 2;
                    length = Integer.parseInt(menuTitle.substring(offset + 2, offset + 4), 16);
                    sVO.setLabel2(menuTitle.substring(offset + 4, offset + 4 + length * 2));
                    _logger.info("", "Label2 =" + sVO.getLabel2());
                    _logger.info("", "menuTitle=" + menuTitle);
                }
                serviceInfoForQueryBD.add(sVO);
                j = 98;
                if (response.length() > 150) {
                    sVO = new ServicesVO();
                    menuPosition = "" + Integer.parseInt(response.substring(j + 0, j + 2), 16);
                    sVO.setPosition(Integer.parseInt(response.substring(j + 0, j + 2), 16));
                    _logger.info("", "menuPosition=" + menuPosition);
                    status = response.substring(j + 2, j + 4);
                    if (!"00".equalsIgnoreCase(status) && !"01".equalsIgnoreCase(status)) {
                        sVO.setStatus("deleted");
                    } else {
                        // _logger.info("status="+(status.equals("01")?"Activated":"Deactivated"));
                        sVO.setStatus("01".equalsIgnoreCase(status) ? "Activated" : "Deactivated");
                        serviceID = "" + Integer.parseInt(response.substring(j + 4, j + 6), 16);
                        sVO.setServiceID(serviceID);
                        _logger.info("", "serviceID=" + serviceID);
                        majorVersion = "" + Integer.parseInt(response.substring(j + 6, j + 8), 16);
                        sVO.setMajorVersion(majorVersion);
                        _logger.info("", "majorVersion=" + majorVersion);
                        minorVersion = "" + Integer.parseInt(response.substring(j + 8, j + 10), 16);
                        sVO.setMinorVersion(minorVersion);
                        _logger.info("", "minorVersion=" + minorVersion);
                        byteCodeOffset = "" + Integer.parseInt(response.substring(j + 10, j + 14), 16);
                        sVO.setOffSet(Integer.parseInt(response.substring(j + 10, j + 14), 16));
                        _logger.info("", "byteCodeOffset=" + byteCodeOffset);
                        byteCodeLength = "" + Integer.parseInt(response.substring(j + 14, j + 18), 16);
                        sVO.setLength(Integer.parseInt(response.substring(j + 14, j + 18), 16));
                        _logger.info("", "byteCodeLength=" + byteCodeLength);
                        offset = 0;
                        length = 0;
                        menuTitle = response.substring(j + 18, j + 98);
                        length = Integer.parseInt(menuTitle.substring(offset + 2, offset + 4), 16);
                        sVO.setLabel1(byteToStringConverter(menuTitle.substring(offset + 4, offset + 4 + length * 2)));
                        _logger.info("", "Label1 =" + sVO.getLabel1());
                        offset = 4 + length * 2;
                        length = Integer.parseInt(menuTitle.substring(offset + 2, offset + 4), 16);
                        sVO.setLabel2(menuTitle.substring(offset + 4, offset + 4 + length * 2));
                        _logger.info("", "Label2 =" + sVO.getLabel2());
                        _logger.info("", "menuTitle=" + menuTitle);
                    }
                    serviceInfoForQueryBD.add(sVO);
                }
            } else {
                int totalByteLength = 0;
                try {
                    totalByteLength = Integer.parseInt(Constants.getProperty("byteLength"));
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                    totalByteLength = 128;
                }
                _logger.debug("servicesInfoForQueryBD() ", "totalByteLength=" + totalByteLength);
                int i = 0;
                for (i = 0; i < totalByteLength; i = i + 8) {
                    sVO = new ServicesVO();
                    offset = Integer.parseInt(response.substring(i, i + 4), 16);
                    length = Integer.parseInt(response.substring(i + 4, i + 8), 16);
                    _logger.info(" ", "offset=" + offset + "    length=" + length);
                    sVO.setOffSet(offset);
                    sVO.setLength(length);
                    serviceInfoForQueryBD.add(sVO);
                }

            }
        } catch (Exception e) {
            _logger.error("servicesInfoForQueryBD ", " " + e);
            throw new BTSLBaseException(CLASS_NAME, "servicesInfoForQueryBD", "");
        }
        return serviceInfoForQueryBD;

    }

    // Can be used in future
    /*
     * Menu Position(1),Activation Status(1),Service ID(1),Version
     * No(2),Bytecode Offset(2),
     * Bytecode Len(2),Menu Title(40)
     */
    /*
     * public static void interpretSimEnquiryB(String byteStr) throws
     * Exception//
     * {
     * _logger.debug("interpretSimEnquiryB() Entered "+byteStr);
     * try
     * {
     * String menuPosition=null;
     * String byteCodeOffset=null;
     * String status=null;//1 byte
     * String serviceID=null;//1 byte
     * String majorVersion=null;//1 byte
     * String menuTitle=null;//1 byte
     * String minorVersion=null;//1 byte
     * String byteCodeLength=null;
     * int j=0;
     * menuPosition=""+Integer.parseInt(byteStr.substring(0,2),16);
     * _logger.info("menuPosition="+menuPosition);
     * status=byteStr.substring(2,4);
     * _logger.info("status="+(status.equals("01")?"Activated":"Deactivated"));
     * serviceID=""+Integer.parseInt(byteStr.substring(4,6),16);
     * _logger.info("serviceID="+serviceID);
     * majorVersion=""+Integer.parseInt(byteStr.substring(6,8),16);
     * _logger.info("majorVersion="+majorVersion);
     * minorVersion=""+Integer.parseInt(byteStr.substring(8,10),16);
     * _logger.info("minorVersion="+minorVersion);
     * byteCodeOffset=""+Integer.parseInt(byteStr.substring(10,14),16);
     * _logger.info("byteCodeOffset="+byteCodeOffset);
     * byteCodeLength=""+Integer.parseInt(byteStr.substring(14,18),16);
     * _logger.info("byteCodeLength="+byteCodeLength);
     * menuTitle=byteStr.substring(18,98);
     * _logger.info("menuTitle="+menuTitle);
     * j=98;
     * if(byteStr.length()>150)
     * {
     * menuPosition=""+Integer.parseInt(byteStr.substring(j+0,j+2),16);
     * _logger.info("menuPosition="+menuPosition);
     * status=byteStr.substring(j+2,j+4);
     * _logger.info("status="+(status.equals("01")?"Activated":"Deactivated"));
     * serviceID=""+Integer.parseInt(byteStr.substring(j+4,j+6),16);
     * _logger.info("serviceID="+serviceID);
     * majorVersion=""+Integer.parseInt(byteStr.substring(j+6,j+8),16);
     * _logger.info("majorVersion="+majorVersion);
     * minorVersion=""+Integer.parseInt(byteStr.substring(j+8,j+10),16);
     * _logger.info("minorVersion="+minorVersion);
     * byteCodeOffset=""+Integer.parseInt(byteStr.substring(j+10,j+14),16);
     * _logger.info("byteCodeOffset="+byteCodeOffset);
     * byteCodeLength=""+Integer.parseInt(byteStr.substring(j+14,j+18),16);
     * _logger.info("byteCodeLength="+byteCodeLength);
     * menuTitle=byteStr.substring(j+18,j+98);
     * _logger.info("menuTitle="+menuTitle);
     * }
     * }
     * catch(Exception e)
     * {
     * _logger.error("interpretSimEnquiryB() Exception e="+e);
     * throw e;
     * }
     * finally
     * {
     * _logger.debug("interpretSimEnquiryB() Exiting");
     * }
     * }
     * public static void interpretSimEnquiryD(String smsStr) throws Exception//
     * {
     * _logger.debug("interpretSimEnquiryD() Entered");
     * try
     * {
     * String byteStr=smsStr;
     * String offset=null;
     * ServicesVO servicesVO=null;
     * String length=null;//1 byte
     * String transactionID=null;//1 byte
     * ArrayList servicesList=new ArrayList();
     * int j=0;
     * int totalByteLength=0;
     * try
     * {
     * totalByteLength=Integer.parseInt(Constants.getProperty("byteLength"));
     * }
     * catch(Exception e)
     * {
     * totalByteLength=128;
     * }
     * _logger.debug("interpretSimEnquiryD() totalByteLength="+totalByteLength);
     * int i=0;
     * for(i=0;i<totalByteLength;i=i+8)
     * {
     * offset="";
     * length="";
     * servicesVO=new ServicesVO();
     * offset=byteStr.substring(i,i+4);
     * length=byteStr.substring(i+4,i+8);
     * _logger.info(" offset="+Integer.parseInt(offset,16)+"    length="+Integer.
     * parseInt(length,16));
     * }
     * _logger.info("i="+i);
     * transactionID=byteStr.substring(i);
     * _logger.info("transactionID="+transactionID);
     * }
     * catch(Exception e)
     * {
     * _logger.error("interpretSimEnquiryD() Exception e="+e);
     * throw e;
     * }
     * finally
     * {
     * _logger.debug("interpretSimEnquiryD() Entered");
     * }
     * }
     */

    /**
     * Method for updating parameters.
     * Creation date: (6/24/03 9:22:48 AM)
     * 
     * @return java.util.ArrayList
     * @param p_locationCode
     *            String
     * @param msisdn
     *            String
     * @exception com.btsl.common.BaseException
     */
    public boolean updateParametersAndSendSMS(String locationCode, String msisdn, String parameter, String value, String key, String createdBy) throws BTSLBaseException {
        _logger.debug("updateParametersAndSendSMS() ", "Entered locationCode=" + locationCode + "   msisdn=" + msisdn + "  parameter=" + parameter + "  value=" + value + "  key=" + key + " createdBy =" + createdBy);
        boolean send = false;
        java.util.ArrayList updatedParametersList = new ArrayList();
        try {
            ServicesVO servicesVO = new ServicesVO();
            if (parameter.equals(ByteCodeGeneratorI.UPDATE_PARAMETER_PIN)) {
                servicesVO.setPosition(ByteCodeGeneratorI.PIN_REQUIRED_FLAG);
                servicesVO.setStatus(value);
                servicesVO.setOperation(ByteCodeGeneratorI.UPDATE_PARAMETERS);
                updatedParametersList.add(servicesVO);
            }
            // product required
            if (parameter.equals(ByteCodeGeneratorI.UPDATE_PARAMETER_PRODUCT)) {
                servicesVO = new ServicesVO();
                servicesVO.setPosition(ByteCodeGeneratorI.PRODUCT_REQUIRED_FLAG);
                servicesVO.setStatus(value);
                servicesVO.setOperation(ByteCodeGeneratorI.UPDATE_PARAMETERS);
                updatedParametersList.add(servicesVO);
            }
            // transaction id required
            if (parameter.equals(ByteCodeGeneratorI.UPDATE_PARAMETER_TID)) {
                servicesVO = new ServicesVO();
                servicesVO.setPosition(ByteCodeGeneratorI.TID_REQUIRED_FLAG);
                servicesVO.setStatus(value);
                servicesVO.setOperation(ByteCodeGeneratorI.UPDATE_PARAMETERS);
                updatedParametersList.add(servicesVO);
            }
            send = new OtaMessage().OtaMessageSender(updatedParametersList, msisdn, key, createdBy);
        } catch (Exception e) {
            _logger.error("updateParametersAndSendSMS() ", "Exception e=" + e);
            throw new BTSLBaseException(CLASS_NAME, "updateParametersAndSendSMS", "");
        } finally {
            _logger.debug("updateParametersAndSendSMS()", " Exiting send=" + send);
        }
        return send;
    }

    /**
     * This method is used to deactivate all services
     * 
     * @param mobileNo
     * @param key
     * @param simProfileVO
     *            SimProfileVO
     * @boolean
     * @throws Exception
     */
    public static boolean deactivateAllServices(String msisdn, String key, SimProfileVO simProfileVO) {
        _logger.debug("deactivateAllServices() ", "Entered msisdn = " + msisdn + " simprofileVO = " + simProfileVO);
        boolean flag = false;
        try {
            ServicesVO simServicesVO = null;
            ArrayList list = new ArrayList();
            String menu = "";
            for (int i = 1; i <= simProfileVO.getNoOfmenus(); i++) {
                menu = menu + i + ",";
            }
            menu = menu.substring(0, menu.length() - 1);
            simServicesVO = new ServicesVO();
            simServicesVO.setPositionList(menu);
            simServicesVO.setOperation(ByteCodeGeneratorI.DEACTIVATE);
            simServicesVO.setStatus("N");
            list.add(simServicesVO);
            OtaMessage otaM = new OtaMessage();
            flag = otaM.deactivateAllServices(list, msisdn, key, simProfileVO);
            return flag;
        } catch (Exception e) {
            flag = false;
            _logger.error("deactivateAllServices ", "Exception e=" + e);
        } finally {

            _logger.debug("deactivateAllServices() ", "Exiting ");

        }
        return flag;
    }

    public static void main(String args[]) {
        SimProfileVO simProfileVO = new SimProfileVO();
        simProfileVO.setMenuSize(16);
        try {
            String menu = "";
            for (int i = 1; i <= simProfileVO.getMenuSize(); i++) {
                menu = menu + i + ",";
            }
            menu = menu.substring(0, menu.length() - 1);

        } catch (Exception e) {

            _logger.error("deactivateAllServices ", "Exception e=" + e);
        } finally {
            _logger.debug("deactivateAllServices()", " Exiting ");

        }
    }
}
