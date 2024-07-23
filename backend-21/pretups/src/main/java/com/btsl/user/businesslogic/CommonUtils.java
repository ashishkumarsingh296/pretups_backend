/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.OracleCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.btsl.util.AESEncryptionUtil;

import com.btsl.util.OneWayHashingAlgoUtil;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.PersianCalendar;
import com.ibm.icu.util.ULocale;

/**
 * This final class used for common utilities methods.
 * 
 * @author SubeshKCV
 */
@SuppressWarnings("deprecation")
public final class CommonUtils {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
    private static HashMap< String , String> hsMap = new HashMap();
    private static VMSApplicationProps vMSApplicationProps;
   
    /**
     * Instantiates a new common utils.
     */
    private CommonUtils() {
    }
    
   static {
		 if (vMSApplicationProps == null) {
    		 vMSApplicationProps = (VMSApplicationProps) com.btsl.common.ApplicationContextProvider.getApplicationContext("TEST")
                     .getBean(VMSApplicationProps.class);
         }
		 
		 
       hsMap.put(Constants.LOGIN_URL.getStrValue(), Constants.LOGIN_URL.getStrValue());
       hsMap.put(Constants.RENEWTOKEN_URL.getStrValue(), Constants.RENEWTOKEN_URL.getStrValue());
       hsMap.put(Constants.ENCRYPT_API_URL.getStrValue(), Constants.ENCRYPT_API_URL.getStrValue());
       hsMap.put(Constants.GENERATETOKEN_URL.getStrValue(), Constants.GENERATETOKEN_URL.getStrValue());
    	
    	
    }

    /**
     * Validate the null check.
     *
     * @param collection
     *            - collection
     * @return boolean
     */
    public static boolean isNullorEmpty(Collection<?> collection) {
        return (null == collection || collection.isEmpty());
    }

    /**
     * Validate the null check for Object super class.
     *
     * @param str
     *            - input
     * @return boolean
     */
    public static boolean isNullorEmpty(Object str) {
        return (null == str || "".equals(str) || "null".equals(str) || "undefined".equals(str));
    }

    /**
     * Method currentDateTimeFormatString.
     * 
     * @param date
     *            Date
     * @return String
     */
    public static String currentDateTimeFormatString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        return sdf.format(date);
    }

    /**
     * Method currentTimeFormatString.
     * 
     * @param date
     *            Date
     * @return String
     */
    public static String currentTimeFormatString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        return sdf.format(date);
    }

    /**
     * Get java.sql.Timestamp from java.util.Date
     * 
     * @param date
     *            object
     * 
     * @return Timestamp
     */
    public static Timestamp getTimestampFromUtilDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    /**
     * Encrypts the passed text string using an encryption key
     * 
     * @param text
     *            - string value
     * @param cryptionType
     *            - string value
     * 
     * @return String
     */
    public static String encryptText(String text, String cryptionType) {
        final String METHOD_NAME = "encryptText";
        String encryptStr = null;
        LOGGER.debug( MessageFormat.format(METHOD_NAME, "encryptText in CommonUtils starts...."));
       //Comment to resolve Checkmarx Privacy Violation Issue
       /* LOGGER.debug(
                MessageFormat.format(METHOD_NAME, "Plain Test: " + text + "cryptionType:" + cryptionType));*/
        //Comment to resolve Checkmarx Privacy Violation Issue
        // LOGGER.info(METHOD_NAME + " CommonUtils Plain Test: " + text + "cryptionType:" + cryptionType);
        try {
            if ("SHA".equals(cryptionType)) {
                encryptStr = OneWayHashingAlgoUtil.getInstance().encrypt(text);
                LOGGER.debug( MessageFormat.format("*****SHA Encrypted data :: {0}", encryptStr));
            } else if ("AES".equals(cryptionType)) {
                encryptStr = new AESEncryptionUtil().EncryptAES(text);
                LOGGER.debug( MessageFormat.format("******AES Encrypted data :: {0}", encryptStr));
            } else if ("DES".equals(cryptionType)) {
            	 //Comment to resolve Checkmarx Privacy Violation Issue
                /*LOGGER.debug( MessageFormat.format(METHOD_NAME,
                        "Plain Test: " + text + "cryptionKey:" + ConstantProperties.KEY));*/
            	 //Comment to resolve Checkmarx Privacy Violation Issue
                //LOGGER.info("Plain Test: " + text + "cryptionKey:" + ConstantProperties.KEY);
                encryptStr = CryptoUtil.encrypt(text, "981AFA8CDEB2A0F7E0A011B557BB08CF");
                LOGGER.debug( MessageFormat.format("******DES Encrypted data :: {0}", encryptStr));
            } else {
                return encryptStr;
            }
        } catch (GeneralSecurityException e) {
            LOGGER.error(MessageFormat.format("encryptText, Exception e={0}", e.getMessage()));
            LOGGER.trace(METHOD_NAME, e);
        }

        return encryptStr;
    }

    /**
     * getInstance will return the instance of calendar
     * 
     * @param caltype
     *            is string value
     * 
     * @return Calendar
     */
    public static Calendar getInstance(String caltype) {
        Calendar cal;
        String calendarType = getTrimmedValue(caltype);
        if (Constants.PERSIAN.getStrValue().equalsIgnoreCase(calendarType)) {
            ULocale locale = new ULocale(Constants.LOCALE_PERSIAN.getStrValue());
            cal = PersianCalendar.getInstance(locale);
        } else {
            cal = GregorianCalendar.getInstance();
        }
        return cal;
    }

    /**
     * Get Trimmed Value
     * 
     * @param parameter
     *            is string value
     * 
     * @return String
     */

    public static String getTrimmedValue(String parameter) {
        String retval = null;
        if (isNullorEmpty(parameter)) {
            retval = "";
        } else {
            retval = parameter.trim();
        }

        return retval;
    }

    /**
     * Convert the input string normized
     * 
     * @param str
     *            - input
     * @return String
     */
    public static String inputNormalized(String str) {
        return Normalizer.normalize(str, Form.NFKC);
    }

    /**
     * Get System Amount
     * 
     * @param validAmount
     *            - is double value
     * 
     * @param multiplicationFactor
     *            - is integer value
     * 
     * @return long
     * 
     */
    public static long getSystemAmount(double validAmount, int multiplicationFactor) {
        long amount = 0;
        amount = (long) (Round((validAmount * multiplicationFactor), NumberConstants.TWO.getIntValue()));
        return amount;
    }

    /**
     * Method to round values till precision
     * 
     * @param Rval
     *            - is double value
     * 
     * @param Rpl
     *            - is integer value
     * 
     * @return double
     */
    public static double Round(double Rval, int Rpl) {
        final double p = Math.pow(10, Rpl);
        Rval = Rval * p;
        final double tmp = Math.round(Rval);
        return tmp / p;
    }

    /**
     * Get Display Amount
     * 
     * @param validAmount
     *            is double value
     * 
     * @param multiplicationFactor
     *            is integer value
     * 
     * @return String
     *
     */
    public static String getDisplayAmount(double validAmount, int multiplicationFactor) {
        final double amount = validAmount / (double) multiplicationFactor;
        String amountStr = new DecimalFormat("#############.#####").format(amount);
        final long l = (Double.valueOf(amount)).longValue();
        amountStr = String.valueOf(l);
        return amountStr;
    }
    
    public static String getDisplayAmount1(double validAmount, int multiplicationFactor) {
        final double amount = validAmount / (double) multiplicationFactor;
        String amountStr = new DecimalFormat("#############.#####").format(amount);
        //final long l = (Double.valueOf(amount)).longValue();
        //amountStr = String.valueOf(l);
        return amountStr;
    }


    /**
     * Float Equality Check
     * 
     * @param num1
     *            is double value
     * @param num2
     *            is double value
     * @param ineqality
     *            is String value
     * @param multfactor
     *            is String value
     * @return boolean
     */

    public static boolean floatEqualityCheck(Double num1, Double num2, String ineqality, String multfactor) {
        long multfac = Long.parseLong(multfactor);
        long firstNumber = (long) (num1 * multfac);
        long secondNumber = (long) (num2 * multfac);
        Boolean retval = false;
        if ("==".equals(ineqality))
            retval = (firstNumber == secondNumber);

        else if ("!=".equals(ineqality))
            retval = (firstNumber != secondNumber);

        else if (">=".equals(ineqality))
            retval = (firstNumber >= secondNumber);

        else if ("<=".equals(ineqality))
            retval = (firstNumber <= secondNumber);

        else if (">".equals(ineqality))
            retval = (firstNumber > secondNumber);

        else if ("<".equals(ineqality))
            retval = (firstNumber < secondNumber);

        return retval;
    }

    /**
     * Check Validation
     * 
     * @param input
     *            is a object
     * @param fieldName
     *            is a string
     * @param messageCode
     *            is a string
     * @param logMessage
     *            is a string
     */
    public static void checkValidation(Object input, String fieldName, String messageCode, String logMessage) {
        if (input instanceof String) {
            if (CommonUtils.isNullorEmpty((String) input)) {
                LOGGER.error(logMessage);
                throw new ValidationException(fieldName, messageCode);
            }
        }
    }
    
    
    //MessageCodes.FIELD_MANDATORY.getStrValue(),  languageCode, params
    
    
    /**
     * To check whether the value is proper decimal value
     * 
     * @param str
     * @return
     */
    public static boolean isDecimalValue(String str) {

        boolean flag = false;
        final String METHOD_NAME = "isDecimalValue";
        try {
            Double.parseDouble(str);
            flag = true;
        } catch (Exception e) {
        	LOGGER.error(METHOD_NAME, e);
            flag = false;
        }
        return flag;
    }


    /**
     * isValidDecimal
     * 
     * @param str
     * @param multfactor
     * @return boolean
     */
    public static boolean isValidDecimal(String str, String multfactor) {
        boolean flag = false;
        final String METHOD_NAME = "isDecimalValue";
        try {
            double d = Double.parseDouble(str);
            if (floatEqualityCheck((d - (int) d), 0D, "!=", multfactor))
                flag = true;
            else
                flag = false;

        } catch (RuntimeException e) {
            LOGGER.error(METHOD_NAME, e);
        }
        return flag;
    }

    /**
     * Validate the Numeric or not
     * 
     * @param str
     *            - input
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        return (!isNullorEmpty(str) && str.chars().allMatch(Character::isDigit));
    }

    /**
     * Get Date From Date String
     * 
     * @param dateStr
     *            input
     * @param format
     *            input
     * @return Date
     * @throws ParseException
     */
    public static Date getDateFromDateString(String dateStr, String format) throws ParseException {
        Date date = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error(MessageFormat.format("parsing, Exception e={0}", e.getMessage()));
            throw e;
        }
        return date;
    }

    /**
     * Method: getVomsDateStringFromDate this method is used in class
     * VomsProductAction in method displayDate , Get VOMS Date String From Date
     * 
     * @param date
     *            input
     * @param format
     *            input
     * @return String
     * @throws ParseException
     */
    public static String getVomsDateStringFromDate(Date date, String format) {
        if (isNullorEmpty(format)) {
            format = Constants.DATE_FORMAT.getStrValue();
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        return sdf.format(date);
    }

    /**
     * This function will check whether the type passed in function is Test
     * Voucher Type i.e. DT, ET, PT
     * 
     * @param type
     * @return
     */
    public static boolean isTestVoucherType(String type) {
        final String methodName = "isTestVoucherType";
        boolean isTestVoucherType = Constants.VOUCHER_TYPE_TEST_DIGITAL.getStrValue().equals(type)
                || Constants.VOUCHER_TYPE_TEST_ELECTRONIC.getStrValue().equals(type)
                || Constants.VOUCHER_TYPE_TEST_PHYSICAL.getStrValue().equals(type);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(methodName, "isTestVoucherType =  " + isTestVoucherType);
        }
        return isTestVoucherType;
    }

    /**
     * This function will check whether the type passed in function is Test
     * Voucher Type i.e., D, E, P
     * 
     * @param type
     * @return
     */
    public static boolean isNonTestVoucherType(String type) {
        final String methodName = "isNonTestVoucherType";
        boolean isNonTestVoucherType = Constants.VOUCHER_TYPE_DIGITAL.getStrValue().equals(type)
                || Constants.VOUCHER_TYPE_ELECTRONIC.getStrValue().equals(type)
                || Constants.VOUCHER_TYPE_PHYSICAL.getStrValue().equals(type);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(methodName, "isNonTestVoucherType =  " + isNonTestVoucherType);
        }
        return isNonTestVoucherType;
    }

    /**
     * Converts Util date to Sql Date
     * 
     * @param utilDate
     * @return
     */
    public static java.sql.Date getSQLDateFromUtilDate(java.util.Date utilDate) {
        java.sql.Date sqlDate = null;
        if (utilDate != null) {
            sqlDate = new java.sql.Date(utilDate.getTime());
        }
        return sqlDate;
    }

    /**
     * Validate the Boolean string
     * 
     * @param str
     *            - input
     * @return boolean
     */
    public static boolean isYesOrNo(String str) {
        if (!isNullorEmpty(str)) {
            str = inputNormalized(str);
            if (str.equals(Constants.YES.getStrValue()) || str.equals(Constants.NO.getStrValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * isValidFormat
     * 
     * @param value
     *            - input
     * 
     * @return boolean -boolean
     */
    public static boolean isValidFormat(String value) {
        boolean validate = false;
        String strDateRegEx = "(0[1-9]|[12][0-9]|[3][01])/(0[1-9]|1[012])/\\d{4}";
        if (value.matches(strDateRegEx)) {
            validate = true;
        }
        return validate;
    }

    /**
     * 
     * @param caltype
     * @param vmsdateformat
     * @param expiryDateString
     * @return
     * @throws ParseException 
     */
    public static Date checkExpiryDateString(String caltype, String vmsdateformat, String expiryDateString) throws ParseException {
    	
    	 getDateFromDateString(expiryDateString, vmsdateformat);
        Date expiryDateObj =  BTSLDateUtil.getGregorianDate(expiryDateString);
        Date currentDateObj =  new Date();
        if (!expiryDateObj.after(currentDateObj)) {
            throw new ValidationException(Constants.EXPIRY_DATE.getStrValue(),
                    MessageCodes.PROFILE_EXPIRY_DATE_SHOULDNOT_SAME.getStrValue());
        }
        return expiryDateObj;
    }

    public static boolean isValid(Pattern p, String value) {
        Matcher m = p.matcher(value);
        return m.matches();

    }

    public static Properties getProperties(String propFileName) {
        Properties properties = new Properties();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream in = loader.getResourceAsStream(propFileName);
            properties.load(in);
        } catch (IOException e) {
            LOGGER.info("Unable to laod constantprops file", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return properties;
    }
    
    
    public static Properties getExternalProperties(String propFileName) {
        Properties properties = new Properties();
        try {
            String serverHome =System.getenv(Constants.SERVER_HOME.getStrValue());
             String path=serverHome+"//conf//pretups//"+propFileName;
             LOGGER.debug( "Loading "+propFileName+ " from path " + path);
             properties.load(new FileInputStream(path));
        } catch (IOException e) {
            LOGGER.info("Unable to laod constantprops file", e);
            LOGGER.info("*******************Configure Envoirment variable ->  server.home ", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return properties;
    }

    public static String convertStringObject(Object obj) {
        String data = null;
        if (obj != null) {
            if (obj instanceof String) {
                data = (String) obj;
            } else if (obj instanceof Long) {
                data = String.valueOf(obj);
            }
        }
        return data;

    }

    public static Date convertDateObject(Object obj) {
        Date data = null;
        if (obj != null) {
            if (obj instanceof Date) {
                data = (Date) obj;
            }
        }
        return data;

    }

    public static String convertDateTimeObject(Object obj) {
        String data = null;
        if (obj != null) {
            if (obj instanceof Date) {
                data = BTSLDateUtil.getLocaleDateTimeFromDate((Date) obj);
            }
        }
        return data;

    }

    public static byte[] convertByteArray(Object obj) {
        byte[] data = null;
        if (obj != null) {
            if (obj instanceof byte[]) {
                data = (byte[]) obj;
            }
        }
        return data;

    }

    public static String applyParameterName(String fieldName, String placeHolder, String parameterData) {
        StringBuilder strb = new StringBuilder();
        if (!parameterData.equals(Constants.ALL.getStrValue())) {
            strb.append(fieldName);
            strb.append(" = ");
            strb.append(placeHolder);
        } else {
            strb.append("");
        }
        return strb.toString();
    }

    public static String convertToDefaultData(String inputParam) {
        if (inputParam == null) {
        	return Constants.ALL.getStrValue();
        } else if(inputParam.trim().length() == 0) {
        	return Constants.ALL.getStrValue();
        }
        return inputParam;
    }

    public static Double asDouble(Object o) {
        Double val = null;
        if (o instanceof Number) {
            val = ((Number) o).doubleValue();
        }
        return val;
    }

    public static Double calcBase64SizeInKBytes(String base64String) {
        Double result = -1.0;
        if (!StringUtils.isEmpty(base64String)) {
            Integer padding = 0;
            if (base64String.endsWith("==")) {
                padding = NumberConstants.TWO.getIntValue();
            } else {
                if (base64String.endsWith("="))
                    padding = 1;
            }
            result = (Math.ceil(base64String.length() / Double.valueOf(NumberConstants.FOUR.getIntValue())) * NumberConstants.THREE.getIntValue()) - padding;
        }
        return result / NumberConstants.N1000.getIntValue();
    }

    public static MessageGatewayVONew constructMessageGatewayService(Object[] row) {
        MessageGatewayVONew messageGateway = new MessageGatewayVONew();
        messageGateway.setGatewayCode(((String) row[NumberConstants.ZERO.getIntValue()]));
        messageGateway.setGatewayType(((String) row[NumberConstants.ONE.getIntValue()]));
        messageGateway.setGatewaySubType(((String) row[NumberConstants.TWO.getIntValue()]));
        messageGateway.setProtocol(((String) row[NumberConstants.THREE.getIntValue()]));
        messageGateway.setHandlerClass(((String) row[NumberConstants.FOUR.getIntValue()]));
        messageGateway.setNetworkCode(((String) row[NumberConstants.FIVE.getIntValue()]));
        messageGateway.setHost(((String) row[NumberConstants.SIX.getIntValue()]));
        messageGateway.setModifiedOn(((Date) row[NumberConstants.SEVEN.getIntValue()]));
        messageGateway.setModifiedOnTimestamp(((Timestamp) row[NumberConstants.SEVEN.getIntValue()]));
        messageGateway.setStatus(((String) row[NumberConstants.EIGHT.getIntValue()]));
        messageGateway.setFlowType(((String) row[NumberConstants.NINE.getIntValue()]));
        messageGateway.setResponseType(((String) row[NumberConstants.N10.getIntValue()]));
        messageGateway.setTimeoutValue(((Long) row[NumberConstants.N11.getIntValue()]));
        if (Constants.NO.equals(row[NumberConstants.N12.getIntValue()])) {
            messageGateway.setUserAuthorizationReqd(false);
        }
        messageGateway.setReqpaswrdtype(((String) row[NumberConstants.N13.getIntValue()]));
        messageGateway.setPlainMsgAllowed(((String) row[NumberConstants.N14.getIntValue()]));
        messageGateway.setBinaryMsgAllowed(((String) row[NumberConstants.N15.getIntValue()]));
        messageGateway.setAccessFrom(((String) row[NumberConstants.N16.getIntValue()]));
        messageGateway.setGatewaySubTypeName(((String) row[NumberConstants.N17.getIntValue()]));
        return messageGateway;
    }
    
    
    public  static int calculateOffset(int page, int limit) {
        return ((limit * page) - limit);
    }
    
    
    public static Properties  getRegExSecuriyProperties(String language) {
    	StringBuilder struff = new StringBuilder();
    	Properties props =null;	
    	struff.append(Constants.SECURITYCONSTANTSPROPS.getStrValue());
		struff.append(Constants.UNDERSCORE.getStrValue());
		struff.append(language);
		struff.append(Constants.DOTPROPS.getStrValue());
		String propFileName=struff.toString();
		if(vMSApplicationProps.getExternalizeProperties().trim().toUpperCase().equals(Constants.YES.getStrValue())) {
    		props =getExternalProperties(propFileName);
		}else {
			props =getProperties(propFileName);
		}
    	
        return props;	
    }
    
    
    public static List<String> readExcelForXLSX(String filepathtemp) throws IOException{
    	List<String> fileValueArray = new ArrayList<String>();
		 XSSFWorkbook  workbook = null;
	     XSSFSheet  excelsheet =null;
	     String tempStr = "";
	     /*DataFormatter formatter =null;
	     try(FileInputStream file =  new FileInputStream(filepathtemp)){
	    	 workbook = new XSSFWorkbook(file);
	    	 excelsheet = workbook.getSheetAt(0);
	    	 int rowcount = excelsheet.getLastRowNum();
	    	 int temp = 0;
	    	 while(temp!= (rowcount+1)) {
	    		 tempStr = "";
	    		 for(int i=0;i<5;i++) {
	    			  formatter =getDataFormatter();
		    		 tempStr = tempStr + formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
	                 tempStr = tempStr + ","; 
	    		 }
	    		 temp++;
	    		 fileValueArray.add(tempStr);
	    	 }
	     }*/
		return fileValueArray;
    }
    
    
   /* private  static DataFormatter  getDataFormatter() {
    	DataFormatter formatter  = new DataFormatter();
    	return formatter;
    }*/
    
        /**
     * Get the property value
     * 
     * @param propertyName
     *            - propertyName
     * @return String - encodeStr
     */
    public static String getSecurityProperty(Properties prop ,String propertyName) {
        String encodeStr = null;
        try {
            encodeStr = encodeSecurityParams(prop.getProperty(propertyName));
        } catch (RuntimeException ex) {
            LOGGER.info("Unable to laod constantprops file", ex);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return encodeStr;
    }

    /**
     * encodeParams
     * 
     * @param param
     *            - param
     * @return String
     */
    @SuppressWarnings("rawtypes")
    private static String encodeSecurityParams(String param) {
        String paramNew = null;
        String modifyResult = null;
        if (param != null) {
            Codec codec = new OracleCodec();
            paramNew = ESAPI.encoder().encodeForSQL(codec, param);
            modifyResult = paramNew.replace("''", "'");
        }
        return modifyResult;
    }

    
  public static  boolean urlContains(String requestUrl) {
	  
	  for (Entry<String, String> entry : hsMap.entrySet()) {
	        if(requestUrl.indexOf(entry.getValue())>0) {
	        	return true;
	        }
	        
	    }
        return false;
	}
 
	public static String nullToString(String pstr) {
		if (pstr == null) {
			return "";
		} else {
			return pstr;
		}
	}

	
	public static ArrayList encryptionList(LinkedHashSet p_list,String algoType) {
    	ArrayList cipherList = new ArrayList();
        String plainText = null, cipherText;
        Iterator itr = p_list.iterator();
        int i = 0;
        while (itr.hasNext()) {
            plainText = (String) itr.next();
            LOGGER.debug( MessageFormat.format("Pin encrption algorithm used :: {0}", algoType));
            cipherText = encryptText(plainText,algoType);
            //cipherText = CommonUtils.encryptText(plainText, algoType);
            cipherList.add(i, (String) cipherText);
            i++;

        }
        return cipherList;
    }
}
