package com.btsl.ota.util;

/**
 * @(#)TagUtil.java
 *                  Copyright(c) 2003, Bharti Telesoft Ltd.
 *                  All Rights Reserved
 *                  This class provides basic support for WebMultiParser
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Gaurav Garg 10/15/2003 12:46:08 PM Initial Creation
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 */
import java.io.IOException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.generator.TagLibary;

public class TagUtil {
    private static Log _log = LogFactory.getLog(TagUtil.class.getName());
    private static final String CLASS_NAME = "TagUtil";
    
    public TagUtil() throws IOException {
        super();
    }

    /**
     * This method is used to construct alphaId Tag
     * 
     * @param alphaIdString
     *            String(i.e Sending SMS)
     * @return String
     * @throws Exception
     */
    /*
     * public static String alphaIdTag(String alphaIdString) throws Exception
     * {
     * _log.debug("alphaIdTag ","Entering ........"+alphaIdString);
     * StringBuffer alphaIdTagBuf = new StringBuffer();
     * try
     * {
     * int length = alphaIdString.trim().length();
     * String hexlength = lengthConverter(length);
     * alphaIdTagBuf.append(TagLibary.alphaTag);
     * alphaIdTagBuf.append(hexlength);
     * alphaIdTagBuf.append(stringToByteConverter(alphaIdString.trim()));
     * }
     * catch(Exception e)
     * {
     * _log.error("alphaIdTag ","Exception ................ "+e);
     * throw e;
     * }
     * _log.debug("alphaIdTag ","Exiting ........"+alphaIdTagBuf);
     * return alphaIdTagBuf.toString();
     * }
     */
    /**
     * This method is used Swap Bytes
     * 
     * @param stringToSwapped
     *            String
     * @return String
     * @throws Exception
     */
    public static String byteSwapper(String stringToSwapped) throws BTSLBaseException {
    	String methodName = "byteSwapper";
        _log.debug(methodName, "Entering........" + stringToSwapped);
        StringBuffer byteSwapperBuf = new StringBuffer();
        try {
            String buffer = new String();
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
            _log.error(methodName, " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "byteSwapper","",e);
        }
        _log.debug(methodName, "Exiting ........" + byteSwapperBuf);
        return byteSwapperBuf.toString();
    }

    /**
     * This method is used to construct go Tag(ref to next Card)
     * 
     * @param cardRef
     *            String (example #gg)
     * @return String
     * @throws Exception
     */
    public static String goTag(String cardRef) throws BTSLBaseException {
    	String methodName = "goTag";
        _log.debug(methodName, "Entering ........" + cardRef);
        StringBuffer goTagBuf = new StringBuffer();
        try {
            goTagBuf.append(TagLibary.goTag);// Go Tag
            goTagBuf.append(TagLibary.refLength);// Length
            goTagBuf.append(urlTag(cardRef));
            String buffer = goTagBuf.toString();
            int indexOf = buffer.indexOf(TagLibary.refLength);
            String hexlength = lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            goTagBuf.replace(indexOf, indexOf + 2, hexlength);
        } catch (Exception e) {
            _log.error(methodName, "Exception ................ " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, "Exiting ........" + goTagBuf);
        return goTagBuf.toString();
    }

    /**
     * This method is used to construct smsc Tag
     * 
     * @param smscNumber
     *            String (ex. 9868116977)
     * @return String
     * @throws Exception
     */
    public static String smscTag(String smscNumber) throws BTSLBaseException {
    	String methodName = "smscTag";
        _log.debug(methodName, "Entering ........" + smscNumber);
        StringBuffer smscTagBuf = new StringBuffer();
        try {
            smscTagBuf.append(TagLibary.smscTag);// 06
            int caseValue = 0;
            if (smscNumber.equalsIgnoreCase(TagLibary.smsc1)) {
                caseValue = 1;
            } else if (smscNumber.equalsIgnoreCase(TagLibary.smsc2)) {
                caseValue = 2;
            } else if (smscNumber.equalsIgnoreCase(TagLibary.smsc3)) {
                caseValue = 3;
            } else {
                caseValue = 4;
            }
            switch (caseValue) {
            case 1:
                smscTagBuf.append(TagLibary.fixedLength02);// Length fixed in
                                                           // case of smsc1
                                                           // smsc2 and smsc3
                smscTagBuf.append(TagLibary.varRefTag);// to show that it is
                                                       // variable ref
                smscTagBuf.append(TagLibary.smsc1Value);// for smsc1
                break;
            case 2:
                smscTagBuf.append(TagLibary.fixedLength02);// Length fixed in
                                                           // case of smsc1
                                                           // smsc2 and smsc3
                smscTagBuf.append(TagLibary.varRefTag);// to show that it is
                                                       // variable ref
                smscTagBuf.append(TagLibary.smsc2Value);// for smsc2
                break;
            case 3:
                smscTagBuf.append(TagLibary.fixedLength02);// Length fixed in
                                                           // case of smsc1
                                                           // smsc2 and smsc3
                smscTagBuf.append(TagLibary.varRefTag);// to show that it is
                                                       // variable ref
                smscTagBuf.append(TagLibary.smsc3Value);// for smsc3
                break;
            case 4:
                int length = Integer.parseInt("" + (smscNumber.length()));// Same
                                                                          // as
                                                                          // destination
                                                                          // address
                                                                          // no
                                                                          // append
                                                                          // to
                                                                          // tone
                                                                          // API
                // 91 in all the cases
                smscTagBuf.append(lengthConverter(length));//
                smscTagBuf.append(TagLibary.toneNPI);// ToneNPI hardCorded
                smscTagBuf.append(byteSwapper(smscNumber));// Number
                break;
            default:
              	 if(_log.isDebugEnabled()){
              		_log.debug("Default Value " , caseValue);
              	 }
            }
        } catch (Exception e) {
            _log.error(methodName, "Exception ................ " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, "Exiting ........" + smscTagBuf);
        return smscTagBuf.toString();
    }

    /**
     * This method is used to construct inline Tag(This tag is used for
     * displaying direct Value)
     * 
     * @param inlineValue
     *            String (i.e Retailer Id)
     * @return String
     * @throws Exception
     */
    public static String inlineTag(String inlineValue) throws BTSLBaseException {
    	String methodName = "inlineTag";
        _log.debug(methodName, "Entering ........" + inlineValue);
        StringBuffer inlineTagBuf = new StringBuffer();
        try {
            String hexlength = lengthConverter(inlineValue.length());
            inlineTagBuf.append(TagLibary.inlineTag);
            inlineTagBuf.append(hexlength);
            inlineTagBuf.append(stringToByteConverter(inlineValue));
        } catch (Exception e) {
            _log.error(methodName, "Exception ................ " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, "Exiting ........" + inlineTagBuf);
        return inlineTagBuf.toString();
    }

    /**
     * This method is used to construct item Tag (This is used with Select Tag)
     * 
     * @param item
     *            String (i.e daily report , sales report etc)
     * @param position
     *            int
     * @return String
     * @throws Exception
     */
    /*
     * public static String itemTag(String item,int position)throws Exception
     * {
     * //Length (+1 has been added as to add position)
     * _log.debug("itemTag ","Entering ........"+item+"...."+position);
     * StringBuffer itemTagBuf = new StringBuffer();
     * try
     * {
     * String hexlength = lengthConverter((item.length()+1));
     * itemTagBuf.append(TagLibary.itemTagValue);//Item Tag 0f
     * itemTagBuf.append(hexlength);
     * itemTagBuf.append(lengthConverter(position));
     * itemTagBuf.append(stringToByteConverter(item));
     * }
     * catch(Exception e)
     * {
     * _log.error("itemTag ","Exception ................ "+e);
     * throw e;
     * }
     * _log.debug("itemTag ","Exiting ........"+itemTagBuf);
     * return itemTagBuf.toString();
     * }
     */
    /**
     * This method is used to construct itemTagUniCode Tag (This is used with
     * Select Tag)
     * 
     * @param lang1
     *            String (Item to be displayed in English)
     * @param lang2
     *            String (Item to be displayed in other Language)
     * @param type
     *            int (Is this tag for multilang support or not)
     * @parma position int
     * @return String
     * @throws Exception
     */
    public static String itemTagUniCode(String lang1, String lang2, int type, int position) throws BTSLBaseException {
    	String methodName = "itemTagUniCode";
        _log.debug(methodName, "Entering ", " " + lang1 + " " + lang2 + " " + type + " " + position);
        StringBuffer itemTagBuf = new StringBuffer();
        int length = 0;
        String hexlength = null;
        try {
            switch (type) {
            case TagLibary.english:
                // In case of English
                length = lang1.trim().length();
                hexlength = lengthConverter(length + 1);
                itemTagBuf.append(TagLibary.itemTagValue);// 0F
                itemTagBuf.append(hexlength);
                itemTagBuf.append(lengthConverter(position));
                itemTagBuf.append(stringToByteConverter(lang1.trim()));
                break;
            case TagLibary.unicode:
                // In case of Unicode
                length = unicodeLength(lang2.trim());
                hexlength = lengthConverter(length + 1);
                itemTagBuf.append(TagLibary.itemTagValue);// 0F
                itemTagBuf.append(hexlength);
                itemTagBuf.append(lengthConverter(position));
                itemTagBuf.append(unicodeToByteConverter(lang2.trim()));
                break;
            case TagLibary.both:
                // In case of both the languages
                length = lang1.trim().length();
                hexlength = lengthConverter(length);
                int length1 = unicodeLength(lang2.trim());
                String hexlength1 = lengthConverter(length1);
                int finalLength = length + length1 + 2 + 2 + 1;
                // finalLength +2 is for two FF tags separating the two
                // languages
                // and another +2 is for there lengths
                // +1 for position
                String finalHexlength = lengthConverter((finalLength));
                itemTagBuf.append(TagLibary.itemTagValue);// 0F
                itemTagBuf.append(finalHexlength);
                itemTagBuf.append(lengthConverter(position));
                itemTagBuf.append(TagLibary.conLength);// FF
                itemTagBuf.append(hexlength);
                itemTagBuf.append(stringToByteConverter(lang1.trim()));
                itemTagBuf.append(TagLibary.conLength);// FF
                itemTagBuf.append(hexlength1);
                itemTagBuf.append(unicodeToByteConverter(lang2.trim()));
                break;
            default:
             	 if(_log.isDebugEnabled()){
             		_log.debug("Default Value " , type);
             	 }
            }
        } catch (Exception e) {
            _log.error(methodName, " Exception   :: " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, "Exiting .........." + itemTagBuf.toString().toUpperCase());
        return itemTagBuf.toString().toUpperCase();
    }

    public static void main(String args[]) {
    	try{
    		if (_log.isDebugEnabled()) {
                _log.debug("main: ",stringToByteConverterTest("ENGHINDI"));
                }
    		
    	}
    	catch(Exception e){
   		 _log.errorTrace("main", e);
   		}
     }

    /**
     * This method is used to get the Next Variable
     * 
     * @return String
     * @throws Exception
     */
    /*
     * public static String nextVariable() throws Exception
     * {
     * _log.debug("nextVariable ","Entering ........");
     * int size = new WebMultiLangParser().userVariablePair.size();
     * String hexlength = lengthConverter((size+1));
     * _log.debug("nextVariable ","Exiting ........"+hexlength);
     * return hexlength;
     * }
     */
    /**
     * This method is used to construct responseLengthTag Tag (This tag is used
     * with input tag and is used for calculating Max and Min Length)
     * 
     * @param min
     *            int (i.e. 10)
     * @param max
     *            int (i.e. 0 )
     * @return String
     * @throws Exception
     */
    public static String responseLengthTag(int min, int max) throws Exception {
        _log.debug("responseLengthTag ", " Entering........" + min + " " + max);
        String minlength = lengthConverter(min);
        String maxlength = lengthConverter(max);
        StringBuffer responseLengthBuf = new StringBuffer();
        responseLengthBuf.append(TagLibary.responseTag);// Tag Value 11
        responseLengthBuf.append(TagLibary.fixedLength02);// 02
        responseLengthBuf.append(minlength);
        responseLengthBuf.append(maxlength);
        if (_log.isDebugEnabled()) {
        _log.debug("responseLengthTag ", " Exiting ........" + responseLengthBuf);
        }
        return responseLengthBuf.toString();
    }

    /**
     * This method is used to construct textString Tag
     * 
     * @param str
     *            String
     * @param dcs
     *            String
     * @return String
     * @throws Exception
     */
    /*
     * public static String textStringTag(String line,String dcs)throws
     * Exception {
     * _log.debug("textStringTag Entering .........."+line+" "+dcs);
     * StringBuffer textTagBuf = new StringBuffer();
     * try
     * {
     * int length = line.trim().length();
     * String hexlength = lengthConverter(length+1);
     * textTagBuf.append(TagLibary.textTag);//0D
     * textTagBuf.append(hexlength);
     * textTagBuf.append(dcs);
     * textTagBuf.append(stringToByteConverter(line.trim()));
     * }
     * catch(Exception e)
     * {
     * _log.error("textStringTag ","Exception ................ "+e);
     * throw e;
     * }
     * _log.debug("textStringTag ","Exiting ........"+textTagBuf);
     * return textTagBuf.toString();
     * }
     */
    /**
     * This method is used to construct defaultValue Tag(This is used with input
     * tag and is used to display some default value to user like Enter Id)
     * 
     * @param defaultValue
     *            String(ex. RetailerId )
     * @return String
     * @throws Exception
     */
    public static String defaultValueTag(String defaultValue) throws BTSLBaseException {
    	String methodName = "defaultValueTag";
        _log.debug(methodName, " Entering .........." + defaultValue);
        StringBuffer defaultValueTagBuf = new StringBuffer();
        try {
            int length = defaultValue.trim().length();
            String hexlength = lengthConverter(length + 1);
            defaultValueTagBuf.append(TagLibary.defaultValueTag);// 17
            defaultValueTagBuf.append(hexlength);
            defaultValueTagBuf.append(TagLibary.defaultDCS);
            defaultValueTagBuf.append(stringToByteConverter(defaultValue.trim()));

        } catch (Exception e) {
            _log.error(methodName, " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        if (_log.isDebugEnabled()) {
         _log.debug(methodName, " Exiting .........." + defaultValueTagBuf.toString());
        }
        return defaultValueTagBuf.toString();
    }

    // This method is used to generate TextString Tag
    /*
     * Text String English: OD 03 04 31 31
     * Text String Unicode: OD 05 08 09 20 09 20
     * Text String Both Lang: 0D 0A FF 02 31 31 FF 04 09 20 09 20
     */
    /**
     * This method is used to construct textString in case of Unicode (This is
     * used for displaying some string)
     * 
     * @param lang1
     *            String (String to be displayed in Eng like(Enter Pin))
     * @param lang2
     *            String (String to be displayed in other Language)
     * @parma type int
     * @return String
     * @throws Exception
     */
    public static String textStringTagUniCode(String lang1, String lang2, int type) throws BTSLBaseException {
    	String methodName = "textStringTagUniCode";
        _log.debug(methodName, " Entering .........." + lang1 + " " + lang2 + " " + type);
        StringBuffer textTagBuf = new StringBuffer();
        int length = 0;
        String hexlength = null;
        try {
            switch (type) {
            case TagLibary.english:
                // In case of English
                length = lang1.trim().length();
                hexlength = lengthConverter(length + 1);
                textTagBuf.append(TagLibary.textTag);// 0D
                textTagBuf.append(hexlength);
                textTagBuf.append(TagLibary.defaultDCS);// 04
                textTagBuf.append(stringToByteConverter(lang1.trim()));
                break;
            case TagLibary.unicode:
                // In case of Unicode
                length = unicodeLength(lang2.trim());
                hexlength = lengthConverter(length + 1);
                textTagBuf.append(TagLibary.textTag);// 0D
                textTagBuf.append(hexlength);
                textTagBuf.append(TagLibary.unicodeDCS);// 08
                textTagBuf.append(unicodeToByteConverter(lang2.trim()));
                break;
            case TagLibary.both:
                // In case of both the languages
                length = lang1.trim().length();
                hexlength = lengthConverter(length);
                int length1 = unicodeLength(lang2.trim());
                String hexlength1 = lengthConverter(length1);
                int finalLength = length + length1 + 2 + 2;
                // finalLength +2 is for two FF tags separating the two
                // languages
                // and another +2 is for there lengths
                String finalHexlength = lengthConverter((finalLength));
                textTagBuf.append(TagLibary.textTag);// 0D
                textTagBuf.append(finalHexlength);
                textTagBuf.append(TagLibary.conLength);// FF
                textTagBuf.append(hexlength);
                textTagBuf.append(stringToByteConverter(lang1.trim()));
                textTagBuf.append(TagLibary.conLength);// FF
                textTagBuf.append(hexlength1);
                textTagBuf.append(unicodeToByteConverter(lang2.trim()));
                break;
            default:
             	 if(_log.isDebugEnabled()){
             		_log.debug("Default Value " , type);
             	 }
            }
        } catch (Exception e) {
            _log.error(methodName, " Exception :: " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, " Exiting .........." + textTagBuf.toString().toUpperCase());
        return textTagBuf.toString().toUpperCase();
    }

    /**
     * This method is used to construct url Tag (i.e. #gg)
     * 
     * @param urlString
     *            String
     * @return String
     * @throws Exception
     */
    public static String urlTag(String urlString) throws BTSLBaseException {
    	String methodName = "urlTag";
        _log.debug(methodName, "Entering ........" + urlString);
        StringBuffer urlTagBuf = new StringBuffer();
        try {
            String hexlength = lengthConverter(urlString.length());
            urlTagBuf.append(TagLibary.urlTag);// Url Tag//0D
            urlTagBuf.append(hexlength);// Length
            urlTagBuf.append(stringToByteConverter(urlString));
        } catch (Exception e) {
            _log.error(methodName, " Exception ................ " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, " Exiting ........" + urlTagBuf);
        return urlTagBuf.toString();
    }

    /**
     * This method is used to take the length of unicode text (means the length
     * of Hex Values entered)
     * 
     * @param unicodeStr
     *            String (i.e 80 09 45 09 56)
     * @return String
     * @throws Exception
     */
    public static int unicodeLength(String unicodeStr) throws BTSLBaseException {
    	String methodName = "unicodeLength";
        _log.debug(methodName, " Entering ..........." + unicodeStr);
        int count = 0;
        StringBuffer actualStringBuf = new StringBuffer();
        try {
            java.util.StringTokenizer st = new java.util.StringTokenizer(unicodeStr, " ");
            count = st.countTokens();
            for (int i = 0; i < count; i++) {
                actualStringBuf.append(st.nextToken());
            }
            count = actualStringBuf.toString().trim().length() / 2;
        } catch (Exception e) {
            _log.error(methodName, " Exception ................ " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, " Exiting........" + count);
        return count;
    }

    /**
     * This method is used to convert unicode into bytes
     * 
     * @param unicodeStr
     *            String (i.e 09 22 09 54)
     * @return String
     * @throws Exception
     */
    public static String unicodeToByteConverter(String unicodeStr) throws BTSLBaseException {
    	String methodName = "unicodeToByteConverter";
        _log.debug(methodName, " Entering..........." + unicodeStr);
        String sb = "";
        try {
            java.util.StringTokenizer st = new java.util.StringTokenizer(unicodeStr, " ");
            while (st.hasMoreTokens()) {
                sb = sb + (String) st.nextElement();
            }
        } catch (Exception e) {
            _log.error(methodName, " Exception................ " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, " Exiting  ..........." + sb);
        return sb;
    }

    /**
     * This function finds option opted by user English , Unicode lang or both
     * (0,1,2) resp
     * 
     * @param textToDisplay1
     *            String
     * @param textToDisplay2
     *            String
     * @return int
     * @throws Exception
     */
    public static int langFinder(String textToDisplay1, String textToDisplay2) throws Exception {
        _log.debug("langFinder", " Entering ........" + textToDisplay1 + "  " + textToDisplay2);
        int option = 0;
        if (!isNullString(textToDisplay1) && isNullString(textToDisplay2)) {
            option = TagLibary.english;
        } else if (isNullString(textToDisplay1) && !isNullString(textToDisplay2)) {
            option = TagLibary.unicode;
        } else if (!isNullString(textToDisplay1) && !isNullString(textToDisplay2)) {
            option = TagLibary.both;
        } else {
            option = -1;
        }
        _log.debug("langFinder", " Exiting ........" + option);
        return option;
    }

    // This method is used to generate Inline Tag
    /*
     * Text String English: OA 02 31 31
     * Text String Unicode: OA 03 08 09 20
     * Text String Both Lang: 0A 0A FF 02 31 31 FF 04 09 20 09 20
     */
    /**
     * This method is used to construct inline Tag(This tag is used for
     * displaying direct Value)
     * 
     * @param lang1
     *            String (i.e Retailer Id)
     * @param lang2
     *            String (In some other Language)
     * @param type
     *            int
     * @return String
     * @throws Exception
     */
    public static String inlineTagUniCode(String lang1, String lang2, int type) throws BTSLBaseException {
    	String methodName = "inlineTagUniCode";
        _log.debug(methodName, " Entering .........." + lang1 + " " + lang2 + " " + type);
        StringBuffer inlineTagBuf = new StringBuffer();
        int length = 0;
        String hexlength = null;
        try {
            switch (type) {
            case TagLibary.english:
                // In case of English
                length = lang1.length();
                hexlength = lengthConverter(length);
                inlineTagBuf.append(TagLibary.inlineTag);// 0A
                inlineTagBuf.append(hexlength);
                inlineTagBuf.append(stringToByteConverter(lang1));
                break;
            case TagLibary.unicode:
                // In case of Unicode
                length = unicodeLength(lang2);
                hexlength = lengthConverter(length);
                inlineTagBuf.append(TagLibary.inlineTag);
                inlineTagBuf.append(hexlength);
                inlineTagBuf.append(unicodeToByteConverter(lang2));
                break;
            case TagLibary.both:
                // In case of both the languages
                length = lang1.length();
                hexlength = lengthConverter(length);
                int length1 = unicodeLength(lang2);
                String hexlength1 = lengthConverter(length1);
                int finalLength = length + length1 + 2 + 2;
                // finalLength +2 is for two FF tags separating the two
                // languages
                // and another +2 is for there lengths
                String finalHexlength = lengthConverter((finalLength));
                inlineTagBuf.append(TagLibary.inlineTag);
                inlineTagBuf.append(finalHexlength);
                inlineTagBuf.append(TagLibary.conLength);// FF
                inlineTagBuf.append(hexlength);
                inlineTagBuf.append(stringToByteConverter(lang1));
                inlineTagBuf.append(TagLibary.conLength);// FF
                inlineTagBuf.append(hexlength1);
                inlineTagBuf.append(unicodeToByteConverter(lang2));
                break;
               default:
            	  if(_log.isDebugEnabled()){
            		  _log.debug("Default Value " , type);
              	 }
            }
        } catch (Exception e) {
            _log.error(methodName, " Exception :: " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, " Exiting .........." + inlineTagBuf.toString().toUpperCase());
        return inlineTagBuf.toString().toUpperCase();
    }

    // This method is used to generate alpha Tag
    /*
     * Text String English: OA 02 31 31
     * Text String Unicode: OA 03 08 09 20
     * Text String Both Lang: 0A 0A FF 02 31 31 FF 04 09 20 09 20
     */
    /**
     * This method is used to construct alphaTag that supports Unicode (i.e
     * Sending SMS)
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
    public static String alphaTagUniCode(String lang1, String lang2, int type)  throws BTSLBaseException {
    	String methodName = "alphaTagUniCode";
        _log.debug(methodName, "Entering .........." + lang1 + " " + lang2 + " " + type);
        StringBuffer alphaTagBuf = new StringBuffer();
        int length = 0;
        String hexlength = null;
        try {
            switch (type) {
            case TagLibary.english:
                // In case of English
                length = lang1.trim().length();
                hexlength = lengthConverter(length);
                alphaTagBuf.append(TagLibary.alphaTag);// 05
                alphaTagBuf.append(hexlength);
                alphaTagBuf.append(stringToByteConverter(lang1.trim()));
                break;
            case TagLibary.unicode:
                // In case of Unicode
                length = unicodeLength(lang2.trim());
                hexlength = lengthConverter(length);
                alphaTagBuf.append(TagLibary.alphaTag);// 05
                alphaTagBuf.append(hexlength);
                alphaTagBuf.append(unicodeToByteConverter(lang2.trim()));
                break;
            case TagLibary.both:
                // In case of both the languages
                length = lang1.trim().length();
                hexlength = lengthConverter(length);
                int length1 = unicodeLength(lang2.trim());
                String hexlength1 = lengthConverter(length1);
                int finalLength = length + length1 + 2 + 2;
                // finalLength +2 is for two FF tags separating the two
                // languages
                // and another +2 is for there lengths
                String finalHexlength = lengthConverter((finalLength));
                alphaTagBuf.append(TagLibary.alphaTag);// 05
                alphaTagBuf.append(finalHexlength);
                alphaTagBuf.append(TagLibary.conLength);// FF
                alphaTagBuf.append(hexlength);
                alphaTagBuf.append(stringToByteConverter(lang1.trim()));
                alphaTagBuf.append(TagLibary.conLength);// FF
                alphaTagBuf.append(hexlength1);
                alphaTagBuf.append(unicodeToByteConverter(lang2.trim()));
                break;
            default:
            	 if(_log.isDebugEnabled()){
            		_log.debug("Default Value " , type);
            	 }
            }

        } catch (Exception e) {
            _log.debug(methodName, " Exception :: " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, "Exiting .........." + alphaTagBuf.toString().toUpperCase());
        return alphaTagBuf.toString().toUpperCase();
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
        	throw new BTSLBaseException(CLASS_NAME, "lengthConverter", "",e);
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
        	throw new BTSLBaseException(CLASS_NAME, "lengthConverter", "",e);
        }
        return sbBuf.toString().toUpperCase();
    }

    /**
     * This method is used to convert String into Byte Values
     * 
     * @param stringToConvert
     *            String(i.e RetailerId)
     * @return String
     * @throws Exception
     */
    public static String stringToByteConverter(String stringToConvert) throws BTSLBaseException {
    	String methodName = "stringToByteConverter";
        _log.debug(methodName, "Entering ........." + stringToConvert);
        StringBuffer sbBuf = new StringBuffer();
        try {
            char buffer[] = stringToConvert.toCharArray();
            for (int i = 0; i < buffer.length; i++) {
                sbBuf.append(Integer.toHexString((int) buffer[i]));
            }
        } catch (Exception e) {
            _log.error(methodName, " Exception ................ " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, "Exiting ........" + sbBuf);
        return sbBuf.toString().toUpperCase();
    }

    /**
     * This method is used to convert String into Byte Values
     * 
     * @param stringToConvert
     *            String(i.e RetailerId)
     * @return String
     * @throws Exception
     */
    public static String stringToByteConverterTest(String stringToConvert) throws BTSLBaseException {
    	String methodName = "stringToByteConverterTest";
        _log.debug(methodName, "Entering ........." + stringToConvert);
        StringBuffer sbBuf = new StringBuffer();
        try {
            char buffer[] = stringToConvert.toCharArray();
            for (int i = 0; i < buffer.length; i++) {
                sbBuf.append("00" + Integer.toHexString((int) buffer[i]));
            }
        } catch (Exception e) {
            _log.error(methodName, " Exception ................ " + e);
            throw new BTSLBaseException(e);
        }
        _log.debug(methodName, "Exiting ........" + sbBuf);
        if (sbBuf.length() > 42) {
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Length is greater than 42");
        } else {

            int y = 42 - sbBuf.length();
            for (int i = 0; i < y; i++) {
                sbBuf.append("F");
            }

        }
        return sbBuf.toString().toUpperCase();
    }

    /**
     * This method is used to check whether node is a variable or a string value
     * 
     * @param variableConstant
     *            String
     * @return String
     * @throws Exception
     */
    public static boolean isVariable(String variableConstant) throws BTSLBaseException {
    	String methodName = "isVariable";
        _log.debug(methodName, "Entering ........" + variableConstant);
        boolean isVariable = false;
        try {
            if (variableConstant.startsWith("$")) {
                isVariable = true;
            }
        } catch (Exception e) {
            _log.error(methodName, " Exception ................ " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        }
        _log.debug(methodName, "Exiting ........" + isVariable);
        return isVariable;
    }

    /**
     * This method is used to display final Byte Code String
     * 
     * @param str
     *            String
     * @return String
     * @throws Exception
     */
    public static void display(String str) throws BTSLBaseException {
        int i = 0;
        char a[] = null;
        try {
            a = str.toUpperCase().toCharArray();
            int length = a.length;
            for (i = 0; i < length; i++) {
                if (i % 2 == 0) {
                    _log.debug("", "" + a[i]);
                }

            }
        } catch (Exception e) {
            _log.debug("display ", " Exception " + e);
            throw new BTSLBaseException(CLASS_NAME, "display", "",e);
        }
    }

    /**
     * This method checks whether the string is null or not
     * 
     * @param text
     *            String
     * @return boolean
     * @throws Exception
     */
    public static boolean isNullString(String text) throws Exception {
        if (text != null) {
            if (text.trim().length() == 0) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method check the final byte code that no error exits in that
     * bytecode
     * 
     * @param stringToCheck
     *            String
     * @throws BaseException
     *             , Exception
     */
    public static void finalCheck(String stringToCheck) throws BaseException, BTSLBaseException {
    	String methodName = "finalCheck ";
        _log.debug(methodName, "Entering ........" + stringToCheck);
        try {
            char arr[] = stringToCheck.trim().toCharArray();
            int length = arr.length;
            for (int i = 0; i < length; i++) {
                if ((arr[i] >= (char) 48 && arr[i] <= (char) 57) || (arr[i] >= (char) 65 && arr[i] <= (char) 70) || (arr[i] >= (char) 97 && arr[i] <= (char) 102)) {
                    continue;
                } else {
                    _log.error(methodName, "Exception ................Invalid character position" + (i / 2) + " value " + arr[i] + "String Value=" + stringToCheck);
                    throw new BaseException("ota.services.error.invalidcharacter");
                }
            }
            if (length % 2 != 0) {
                _log.error(methodName, "Exception ................ Size is not a multiple of two. Size  = " + length);
                throw new BaseException("ota.services.error.general");
            }
        } catch (com.btsl.common.BaseException be) {
            _log.error(methodName, " BaseException " + be);
            throw be;
        } catch (Exception e) {
            _log.error(methodName, " Exception ................ " + e);
            throw new BTSLBaseException(CLASS_NAME, methodName, "");
        }
        _log.debug(methodName, " Exiting ........");
    }

}
