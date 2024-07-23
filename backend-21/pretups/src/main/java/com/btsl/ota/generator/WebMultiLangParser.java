package com.btsl.ota.generator;

/**
 * @(#)WebMultiLangParser.java
 *                             Copyright(c) 2003, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 *                             This class is written for generating Bytecode
 *                             corrosponding to given WML
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Gaurav Garg 10/13/2003 12:57:47 PM Initial
 *                             Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 */
import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.util.TagUtil;

public class WebMultiLangParser {
    private StringBuffer stackByteCodeBuf = new StringBuffer();
    private java.util.HashMap userVariablePair = new java.util.HashMap();
    private java.util.HashMap userVariableMemoryMap = new java.util.HashMap();
    private int cardNo = 0;
    private static final Log logger = LogFactory.getLog(WebMultiLangParser.class.getName());

    /**
     * This method is used to generate byte code for card tag
     * 
     * @param processNode
     *            Node , card
     * @throws Exception
     */
    private void appendCardTag(Node processNode) throws BTSLBaseException {
        logger.debug("appendCardTag ", "Entering ..........");
        try {
            cardNo = cardNo + 1;// To find No of Cards
            java.util.HashMap hp = giveTagAttributes(processNode);
            String id_value = (String) hp.get(TagLibary.card_id);
            String hexLength = TagUtil.lengthConverter(id_value.length());
            stackByteCodeBuf.append(TagLibary.cardTag);// card Tag
            stackByteCodeBuf.append(TagLibary.cardLength);// card Length CL
            stackByteCodeBuf.append(TagLibary.cardIdTag);// card Id Tag
            stackByteCodeBuf.append(hexLength);// Length
            stackByteCodeBuf.append(TagUtil.stringToByteConverter(id_value));// Value
        } catch (Exception e) {
            logger.error("appendCardTag ", "Exception .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendCardTag", "");
        }
        logger.debug("appendCardTag ", "Existing..........");
    }

    /**
     * This method is used to generate byte code for Compare tag
     * 
     * @param processNode
     *            Node , btsl-compare
     * @throws BaseException
     *             , Exception
     * @throws BTSLBaseException 
     */
    /*
     * Compare Values
     * 40 0e 08 01 01 08 01 02 0A 06 FF 01 09 FF 01 31
     * 07 Len(1)
     * 08 Var Ref
     * 01 len(1)
     * 01 Var Reference-1
     * 08 Var Ref Tag (Can also be Inline value)
     * 01 len(1)
     * 02 Var Reference-2
     * 0A Inline Value
     * 06 Len
     * FF 01 09 FF 01 31 Inline Text
     */
    private void appendCompareTag(Node processNode) throws BTSLBaseException {
        logger.debug("appendCompareTag ", "Entering ..........");
        StringBuffer compareTagBuf = new StringBuffer();
        try {

            java.util.HashMap hp = giveTagAttributes(processNode);
            String var1 = (String) hp.get(TagLibary.ATT_VAR1.toUpperCase());
            compareTagBuf.append(varRefTag(var1));
            String var2 = (String) hp.get(TagLibary.ATT_VAR2.toUpperCase());
            compareTagBuf.append(varRefTag(var2));
            String lang1 = (String) hp.get(TagLibary.lang1);
            String lang2 = (String) hp.get(TagLibary.lang2);
            if (TagUtil.langFinder(lang1, lang2) != TagLibary.both) {
                throw new BTSLBaseException("Lang1 and Lang2 are both Mandatory");
            }
            compareTagBuf.append(TagUtil.inlineTagUniCode(lang1, lang2, TagLibary.both));
            compareTagBuf.insert(0, TagUtil.lengthConverter(compareTagBuf.toString().length() / 2));
            compareTagBuf.insert(0, TagLibary.compareTag);
            stackByteCodeBuf.append(compareTagBuf.toString());
        } catch (BTSLBaseException be) {
        	logger.error("appendCompareTag ", "BTSLBaseException .........." + be);
            throw new BTSLBaseException("WebMultiLangParser", "appendCompareTag", "");
        }
        catch (Exception e) {
            logger.error("appendCompareTag ", "Exception .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendCompareTag", "");
        }
        logger.debug("appendCompareTag", " Existing .........." + compareTagBuf.toString());

    }

    /**
     * This method is used to generate byte code for Concat tag
     * 
     * @param processNode
     *            Node , btsl-concat
     * @throws BaseException
     *             , Exception
     */
    /*
     * Concatenate
     * 24 11 02 04 0A 04 54 65 73 74 08 01 01 0A 04 54 65 66 67
     * 24 Concat Tag
     * 10 Length(1 Byte)
     * 02 Variable To store
     * 04 DCS ( 04 for Single Lang, 08 for Multi Lang)
     * 0A Inline Tag
     * 04 Len
     * 54 65 73 74 Value
     * 08 Var Reference Tag
     * 01 (F)len
     * 01 Var Ref (Var Ref between 01x-19x and 60x. If 60x Fixed Data will be
     * added.)
     * 0A 04 54 65 66 67 - Inline
     */
    /*
     * private void appendConcatTag (Node processNode) throws BaseException ,
     * Exception
     * {
     * logger.debug("","Entering appendConcatTag ..........");
     * StringBuffer concatTagBuf = new StringBuffer();
     * try
     * {
     * String nextVariable = TagUtil.nextVariable();//next variable from the
     * userVariable pair is get
     * java.util.HashMap hp = new java.util.HashMap();
     * hp = giveTagAttributes(processNode);
     * String name = (String) hp.get(TagLibary.concat_name);
     * concatTagBuf.append(TagLibary.conCatTag);
     * concatTagBuf.append(nextVariable);//Var Ref has to changes
     * userVariablePair.put(name.toUpperCase(),nextVariable);
     * String strWithVariables =
     * processNode.getFirstChild().getNodeValue().trim()+" ";
     * int indexOf =strWithVariables.indexOf(" ");
     * java.util.StringTokenizer str = new
     * java.util.StringTokenizer(strWithVariables," ");
     * boolean flag = false;
     * String temp= "";
     * int count = str.countTokens();
     * int i =0;
     * while (i<count)
     * {
     * if(!flag)
     * temp = str.nextToken();
     * if(temp.indexOf("$")==-1)
     * {
     * String temp2="";
     * try
     * {
     * temp2 = str.nextToken();
     * }
     * catch(java.util.NoSuchElementException e)
     * {
     * temp2="$";
     * }
     * i++;
     * while(temp2.indexOf("$")==-1)
     * {
     * temp= temp+" "+temp2;
     * temp2 =str.nextToken();
     * i++;
     * }
     * concatTagBuf.append(TagUtil.inlineTag(" "+temp));
     * temp = temp2;
     * flag = true;
     * }
     * else
     * {
     * flag = false;
     * concatTagBuf.append(TagUtil.varRefTag(temp));
     * i++;
     * }
     * }
     * String buffer = concatTagBuf.toString();
     * indexOf = buffer.indexOf(TagLibary.refLength);//0L
     * String hexlength =
     * TagUtil.lengthConverter(buffer.substring(indexOf+2).length()/2);
     * concatTagBuf.replace(indexOf,indexOf+2,hexlength);
     * stackByteCodeBuf.append(concatTagBuf.toString());
     * }
     * catch(com.btsl.common.BaseException be)
     * {
     * logger.error("appendConcatTag "," BaseException "+be);
     * throw be;
     * }
     * catch(Exception e)
     * {
     * logger.error("Exception appendConcatTag  :: "+e);
     * throw e;
     * }
     * logger.debug("Existing appendConcatTag ..........");
     * }
     */
    /**
     * This method is used to generate byte code for DisplayText tag
     * 
     * @param processNode
     *            Node , btsl-displayText
     * @throws BaseException
     *             , Exception
     */

    /*
     * Display Text
     * 2D 0F 21 81 02 0D 0A 04 4F 74 61 20 54 65 73 74 32
     * 2D (F)Generic STK Tag
     * 0F Length(1-3)
     * 21 (F)Command Type- Display Text
     * 81 Command Qualifier- Wait for User Input
     * 02 (F)Dest Device - Display
     * 0D (F)Text String Tag
     * 0A Length (If Len= FF, its a Var Reference e.g 0D FF 04 01 )
     * 04 DCS-8-Bit-Data
     * 4F 74 61 20 54 65 73 74 32 ( Text String)
     */
    private void appendDisplayTextTag(Node processNode) throws BaseException, BTSLBaseException {
        logger.debug("appendDisplayTextTag ", "Entering ..........");
        StringBuffer displayTagBuf = new StringBuffer();
        try {
            java.util.HashMap hp = giveTagAttributes(processNode);
            String textToDisplay1 = (String) hp.get(TagLibary.lang1);
            String textToDisplay2 = (String) hp.get(TagLibary.lang2);
            String var = (String) hp.get(TagLibary.var);
            int choice = TagUtil.langFinder(textToDisplay1, textToDisplay2);
            displayTagBuf.append(TagLibary.displayTextTag);
            if (!TagUtil.isNullString(var)) {
                displayTagBuf.append(TagLibary.textTag);// 0D
                displayTagBuf.append(TagLibary.conLength);// FF for variable
                                                          // ref.
                displayTagBuf.append(TagLibary.defaultDCS);// DCS
                String buffer = var.substring(1).trim().toUpperCase();
                String variable = (String) userVariablePair.get(buffer);
                if (TagUtil.isNullString(variable)) {
                    logger.error("appendDisplayTextTag ", " VariableRef Not found" + buffer);
                    throw new com.btsl.common.BaseException("ota.services.error.variablenotfound," + buffer);
                } else {
                    displayTagBuf.append(variable);// variable Reference
                }
            } else {
                String str = TagUtil.textStringTagUniCode(textToDisplay1, textToDisplay2, choice);
                displayTagBuf.append(str);
            }
            String buffer = displayTagBuf.toString();
            int indexOf = buffer.indexOf(TagLibary.refLength);
            String hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            displayTagBuf.replace(indexOf, indexOf + 2, hexlength);
            stackByteCodeBuf.append(displayTagBuf.toString());
        } catch (com.btsl.common.BaseException be) {
            logger.error("appendDisplayTextTag ", " BaseException " + be);
            throw be;
        } catch (Exception e) {
            logger.error("appendDisplayTextTag", " Exception .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendDisplayTextTag", "");
        }
        logger.debug("appendDisplayTextTag", " Existing .........." + displayTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for encrypt tag
     * 
     * @param processNode
     *            Node, btsl-encrypt
     * @throws BaseException
     *             , Exception
     */

    /*
     * Encrypt
     * 26 03 08 01 01
     * 03 Len(1 Byte)
     * 08 Var Ref ID (can also be inline value)
     * 01 (F)Len
     * 01 Var Ref
     * After encryption the encrypted value is stored in the var '50'
     */
    private void appendEncryptTag(Node processNode) throws BaseException, BTSLBaseException {
        logger.debug("appendEncryptTag  ", "Entering ..........");
        StringBuffer encryptTagBuf = new StringBuffer();
        try {
            String firstValue = null;
            java.util.HashMap hp = giveTagAttributes(processNode);
            String name = (String) hp.get(TagLibary.name);
            userVariablePair.put(name.toUpperCase(), TagLibary.encryptVariable);// 50
            encryptTagBuf.append(TagLibary.encryptTag);// Tag Value 26
            encryptTagBuf.append(TagLibary.refLength);
            String value = processNode.getFirstChild().getNodeValue().trim();
            if (TagUtil.isVariable(value)) {
                firstValue = varRefTag(value);
            } else {
                firstValue = TagUtil.inlineTag(value);
            }
            encryptTagBuf.append(firstValue);// value
            String buffer = encryptTagBuf.toString();
            int indexOf = buffer.indexOf(TagLibary.refLength);// 0L
            String hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            encryptTagBuf.replace(indexOf, indexOf + 2, hexlength);
            stackByteCodeBuf.append(encryptTagBuf.toString());
        } catch (Exception e) {
            logger.error("appendEncryptTag ", " Exception :: " + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendEncryptTag", "");
        }
        logger.debug("appendEncryptTag  ", " Existing .........." + encryptTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for go tag
     * 
     * @param processNode
     *            Node , go
     * @throws Exception
     */
    /*
     * Go- Selected
     * 29 05 0D 03 23 63 63
     * 05 Len(1 Byte)
     * 0D URL Tag
     * 03 Len
     * 23 63 63 URL Value (#cc)
     */
    private void appendGoTag(Node processNode) throws BTSLBaseException {
        logger.debug("appendGoTag ", "Entering ..........");
        String goTag = null;
        try {
            java.util.HashMap hp = giveTagAttributes(processNode);
            String href = (String) hp.get(TagLibary.href);// "HREF"
            goTag = TagUtil.goTag(href);
            stackByteCodeBuf.append(goTag);
        } catch (Exception e) {
            logger.error("appendGoTag ", "Exception .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendGoTag", "");
        }
        logger.debug("appendGoTag ", "Existing .........." + goTag);
    }

    /**
     * This method is used to to generate byte code for UpdateMenuOption tag
     * 
     * @param processNode
     *            Node , btsl-updateMenuOption
     * @throws Exception
     */
    /*
     * Update-btsl
     * <btsl-updateMenuOption/>
     * 47 00
     */
    private void appendUpdateMenuOptionTag(Node processNode) throws BTSLBaseException {
        logger.debug("appendUpdateMenuOption ", "Entering ..........");
        String appendUpdateMenuOption = TagLibary.updateMenuOptionTag; // "4700"
        stackByteCodeBuf.append(appendUpdateMenuOption);
        logger.debug("appendUpdateMenuOption ", " Existing .........." + appendUpdateMenuOption);
    }

    private void appendLangChangeTag(Node processNode) throws BTSLBaseException {
        logger.debug("appendLangChangeTag ", "Entering ..........");
        String appendLangChangeTag = TagLibary.changeLangTag; // "49"
        stackByteCodeBuf.append(appendLangChangeTag);
        stackByteCodeBuf.append("03");
        java.util.HashMap hp = giveTagAttributes(processNode);
        String value = (String) hp.get(TagLibary.changelang_value);
        stackByteCodeBuf.append(varRefTag(value));
        logger.debug("appendLangChangeTag ", " Existing .........." + appendLangChangeTag);
    }

    /**
     * This method is used to generate byte code for btsl-if-do tag
     * 
     * @param processNode
     *            Node , btsl-if-do
     * @throws BaseException
     *             , Exception
     */
    /*
     * btsl-if- do
     * if- do
     * If the var value is 0x01, one/more tags will be executed
     * else if(var == 0x00) the tags will be skipped.
     * 
     * 41 03 08 01 02 01
     * 03 len(1)
     * 08 Var Ref Tag
     * 01 Len(1)
     * 02 Var ID (Var ID can be temporary(00x-19x) or permanenet params(20x-
     * 29x) )
     * 01 (O)No Of Tags to skip- Default value=1
     * 50 4B 51 49 03 0A 03 61 62 63 55 41 01 3F 02 05 44 65 63 6B 32 05 36 06
     * 02 61 61 2D 12 23 01 02 0D 08 04 53 45 27 73 20 49 44 11 02 05 05 01 40
     * 0B 0A 05 43 68 65 63 6B 08 01 01 02 41 03 08 01 02 2D 0A 21 81 02 0D 05
     * 04 4F 74 61 32
     */
    private void appendIfDoTag(Node processNode) throws BaseException, BTSLBaseException {
        logger.debug("appendIfDoTag ", "Entering ..........");
        StringBuffer ifdoTagBuf = new StringBuffer();
        try {
            java.util.HashMap hp = giveTagAttributes(processNode);
            String var = null;
            String value = null;
            ifdoTagBuf.append(TagLibary.ifdoTag);
            if (TagUtil.isNullString((String) hp.get(TagLibary.ifdo_value))) {
                value = "01";
            } else {
                value = TagUtil.lengthConverter((String) hp.get(TagLibary.ifdo_value));
            }
            var = (String) hp.get(TagLibary.ifdo_var);
            if (TagUtil.isVariable(var)) {
                ifdoTagBuf.append(varRefTag(var));
            } else {
                // throw exception this has to be some variable
            	 throw new BTSLBaseException("appendIfDoTag Exception here has to be some  variable" + var);
            }
            ifdoTagBuf.append(value);
            stackByteCodeBuf.append(ifdoTagBuf.toString());
        } catch (com.btsl.common.BaseException be) {
            logger.error("appendIfDOTag ", " BaseException............ " + be);
          
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            logger.error("appendIfDoTag ", " Exception " + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendIfDoTag", "");
        }
        logger.debug("appendIfDoTag ", " Existing .........." + ifdoTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for btsl-if-else tag
     * 
     * @param processNode
     *            Node , btsl-if-else
     * @throws BaseException
     *             , Exception
     */
    /*
     * If-else
     * If( var == 0x01) go to first card
     * else go to second card
     * 
     * 42 11 08 01 02 29 05 0D 03 23 62 62 29 05 0D 03 23 63 63
     * 11 Len(1)
     * 08 Var Ref Tag
     * 01 Len(1)
     * 02 Var ID (Var ID can be temporary(00x-19x) or permanenet params(20x-
     * 29x) )
     * 29 Go-Card Tag
     * 05 Len(1)
     * 0D Text String Tag
     * 03 Len(1)
     * 23 62 62 Text Value
     * 29 Go-Card Tag
     * 05 Len(1)
     * 0D Text String Tag
     * 03 Len(1)
     * 23 63 63 Text Value
     * 
     * 50 71 51 6F 03 0A 03 61 62 63 55 67 01 65 02 05 44 65 63 6B 32 05 38 06
     * 02 61 61 2D 12 23 01 02 0D 08 04 53 45 27 73 20 49 44 11 02 05 05 01 40
     * 0B 0A 05 43 68 65 63 6B 08 01 01 02 42 11 08 01 02 29 05 0D 03 23 62 62
     * 29 05 0D 03 23 63 63 05 10 06 02 62 62 2D 0A 21 81 02 0D 05 04 4F 74 61
     * 32 05 10 06 02 63 63 2D 0A 21 81 02 0D 05 04 4F 74 61 33
     */
    private void appendIfElseTag(Node processNode) throws BaseException, BTSLBaseException {
        logger.debug("appendIfElseTag ", "Entering ..........");
        StringBuffer ifelseTagBuf = new StringBuffer();
        try {
            java.util.HashMap hp = giveTagAttributes(processNode);
            String var = null;
            String card1 = null;
            String card2 = null;
            var = (String) hp.get(TagLibary.ifelse_var);
            ifelseTagBuf.append(TagLibary.ifelseTag);
            if (TagUtil.isVariable(var)) {
                ifelseTagBuf.append(varRefTag(var.toUpperCase()));
            } else {
                // throw exception this has to be some variable
             
            	 throw new BTSLBaseException("appendIfelseTag Exception here has to be some  variable" + var);
            }
            card1 = (String) hp.get(TagLibary.ifelse_card1);
            card2 = (String) hp.get(TagLibary.ifelse_card2);
            ifelseTagBuf.append(TagUtil.goTag(card1));
            ifelseTagBuf.append(TagUtil.goTag(card2));
            String buffer = ifelseTagBuf.toString();
            int indexOf = buffer.indexOf(TagLibary.refLength);// 0L
            String hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            ifelseTagBuf.replace(indexOf, indexOf + 2, hexlength);
            stackByteCodeBuf.append(ifelseTagBuf.toString());
        } catch (Exception e) {
            logger.error("appendIfElseTag", " Exception  " + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendIfElseTag", "");
        }
        logger.debug("appendIfElseTag ", " Existing .........." + ifelseTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for Input tag
     * 
     * @param processNode
     *            Node , input
     * @throws BaseException
     *             , Exception
     */
    /*
     * Get Input
     * 2D 15 23 00 02 0D 0B 04 45 6E 74 65 72 20 44 61 74 61 11 02 04 04 01
     * 2D (F)Generic STK Tag
     * 15 Length(1-3)
     * 23 (F)Command Type ( 11.14 Section 13.4 )
     * 00 Command Qualifier ( 11.14 Section 12.6)
     * 02 (F)Dest Device-Display (11.14 section 12.7)
     * 0D Text String Tag
     * 0B ( length of Text String,1 byte)
     * 04 ( DCS, always 04,8-Bit Data)
     * 45 6E 74 65 72 20 44 61 74 61 ( Text String Value)
     * 11 Response length Tag
     * 02 Length
     * 04 Min Length
     * 04 Max Length
     * 01 Varible ID to store the Data
     * Here also the length field is replace by 0L
     * 2D 0L 23 CQ 02 0D 0L 04 45 6E 74 65 72 20 44 61 74 61 11 02 04 04 01
     */
    private void appendInputTag(Node processNode) throws BaseException, BTSLBaseException {
        logger.debug("appendInputTag ", "Entering ..........");
        StringBuffer inputTagBuf = new StringBuffer();
        try {
            java.util.HashMap hp = giveTagAttributes(processNode);
            String commandQualifier = null;
            String type = null;
            String name = null;
            boolean appendValueTag = false;
            String textToDisplay1 = null;
            String textToDisplay2 = null;
            int maxlength = 0;
            int minlength = 0;
            int option = 0;
            String format = null;
            String value = null;
            String nextVariable = nextVariable();// next variable from the
                                                 // userVariable pair is get
            textToDisplay1 = (String) hp.get(TagLibary.lang1);
            textToDisplay2 = (String) hp.get(TagLibary.lang2);
            if (!TagUtil.isNullString(textToDisplay1) && TagUtil.isNullString(textToDisplay2)) {
                option = TagLibary.english;
            } else if (TagUtil.isNullString(textToDisplay1) && !TagUtil.isNullString(textToDisplay2)) {
                option = TagLibary.unicode;
            } else if (!TagUtil.isNullString(textToDisplay1) && !TagUtil.isNullString(textToDisplay2)) {
                option = TagLibary.both;
            }
            if (TagUtil.isNullString((String) hp.get(TagLibary.input_value))) {
                appendValueTag = false;
            } else {
                value = (String) hp.get(TagLibary.input_value);
                appendValueTag = true;
            }
            if (TagUtil.isNullString((String) hp.get(TagLibary.input_type))) {
                type = TagLibary.defaultInputType;// text
            } else {
                type = (String) hp.get(TagLibary.input_type);
            }
            name = (String) hp.get(TagLibary.input_name);
            userVariablePair.put(name.toUpperCase(), nextVariable);// entry is
                                                                   // put into
                                                                   // the
                                                                   // userVariable
                                                                   // pair
            maxlength = Integer.parseInt((String) hp.get(TagLibary.input_maxlength));
            userVariableMemoryMap.put((name.toUpperCase() + "###" + nextVariable), new Integer(maxlength));
            if (TagUtil.isNullString((String) hp.get(TagLibary.input_minength))) {
                minlength = 0;
            } else {
                minlength = Integer.parseInt((String) hp.get(TagLibary.input_minength));
            }
            if (TagUtil.isNullString((String) hp.get(TagLibary.input_format))) {
                format = TagLibary.formatC;// "C"
            } else {
                format = (String) hp.get(TagLibary.input_format);
            }
            if (type.equalsIgnoreCase(TagLibary.inputTypeValueText) && format.equalsIgnoreCase(TagLibary.formatC)) {
                commandQualifier = TagLibary.commandQualifierValTC;
            } else if (type.equalsIgnoreCase(TagLibary.inputTypeValuePassword) && format.equalsIgnoreCase(TagLibary.formatC)) {
                commandQualifier = TagLibary.commandQualifierValPC;
                throw new BaseException("ota.services.error.combinationnotsupported"); // here
                                                                                       // throw
                                                                                       // BaseException
            } else if (type.equalsIgnoreCase(TagLibary.inputTypeValuePassword) && format.equalsIgnoreCase(TagLibary.formatN)) {
                commandQualifier = TagLibary.commandQualifierValPN;
            } else {
                // (
                // type.compareToIgnoreCase("text")&&format.compareToIgnoreCase("N"))
                commandQualifier = TagLibary.commandQualifierValTN;
            }
            String destinationDevice = TagLibary.destinationDevice;// "02";
            String textStringTag = TagUtil.textStringTagUniCode(textToDisplay1, textToDisplay2, option);// 04
                                                                                                        // is
                                                                                                        // dcs
            String responseLengthTag = TagUtil.responseLengthTag(minlength, maxlength);
            inputTagBuf.append(TagLibary.inputTag);
            inputTagBuf.append(commandQualifier);
            inputTagBuf.append(destinationDevice);
            inputTagBuf.append(textStringTag);
            inputTagBuf.append(responseLengthTag);
            if (appendValueTag) {
                inputTagBuf.append(TagUtil.defaultValueTag(value));
            }
            inputTagBuf.append(nextVariable);
            String buffer = inputTagBuf.toString();
            int indexOf = buffer.indexOf(TagLibary.refLength);// 0L
            String hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            inputTagBuf.replace(indexOf, indexOf + 2, hexlength);
            stackByteCodeBuf.append(inputTagBuf.toString());
        } catch (Exception e) {
            logger.error("appendInputTag ", "Exception " + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendInputTag", "");
        }
        logger.debug("appendInputTag ", "Existing ........." + inputTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for readCounter tag
     * 
     * @param processNode
     *            Node , btsl-readCounter
     * @throws Exception
     */
    /*
     * Read Counter
     * 44 01 02
     * 01 (F)len
     * 02 Var To store counter value
     */
    private void appendReadCounterTag(Node processNode) throws BTSLBaseException {
        logger.debug("appendReadCounterTag ", "Entering ..........");
        StringBuffer counterTagBuf = new StringBuffer();
        try {
            java.util.HashMap hp = giveTagAttributes(processNode);
            String name = (String) hp.get(TagLibary.name);
            String nextVariable = nextVariable();// next variable from the
                                                 // userVariable pair is get
            counterTagBuf.append(TagLibary.counterReadTag);
            counterTagBuf.append(nextVariable);
            stackByteCodeBuf.append(counterTagBuf.toString());
            userVariablePair.put(name.toUpperCase(), nextVariable);
        } catch (Exception e) {
            logger.error("appendReadCounterTag ", "Exception " + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendReadCounterTag", "");
        }
        logger.debug("appendReadCounterTag", " Existing .........." + counterTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for Select tag
     * 
     * @param processNode
     *            Node , select
     * @throws Exception
     */
    /*
     * Select Item with Couple Tag(onpick supported) + Multi Card
     * 01 81 8C 02 05 44 65 63 6B 32 05 55 06 02 61 61 2D 4F 24 00 82 05 06 53
     * 65 6C 65 63 74 11 0E 0F 07 01 4F 70 74 69 6E 31 0D 03 23 65 65 11 0E 0F
     * 07 02 4F 70 74 69 6E 32 0D 03 23 64 64 11 0E 0F 07 03 4F 70 74 69 6E 33
     * 0D 03 23 65 65 11 0E 0F 07 04 4F 70 74 69 6E 34 0D 03 23 64 64 10 01 01
     * 02 05 15 06 02 64 64 2D 0F 21 81 02 8D 0A 04 4F 74 61 20 54 65 73 74 34
     * 05 15 06 02 65 65 2D 0F 21 81 02 8D 0A 04 4F 74 61 20 54 65 73 74 35
     * 01 81 8C 02 05 44 65 63 6B 32
     * 05 55 06 02 61 61
     * 2D 4F 24 00 82 05 06 53 65 6C 65 63 74 11 0E 0F 07 01 4F 70 74 69 6E 31
     * 0D 03 23 65 65 11 0E 0F 07 02 4F 70 74 69 6E 32 0D 03 23 64 64 11 0E 0F
     * 07 03 4F 70 74 69 6E 33 0D 03 23 65 65 11 0E 0F 07 04 4F 70 74 69 6E 34
     * 0D 03 23 64 64 10 01 01 02
     * 2D
     * 4F Len
     * 24 (F)Command Type
     * 00 Cmd Qualifier
     * 82 (F)Dest Device(ME)
     * 05 (O)Alpah Id
     * 06 Len
     * 53 65 6C 65 63 74 Value String
     * 11 (O)Couple Tag (Either couple Tag(11) or Item tag(0F))
     * 14 Len
     * 0F Tag Item
     * 07 Len
     * 01 Position
     * 4F 70 74 69 6E 31 Item String
     * 0D URL Tag
     * 03 Len
     * 23 65 65 URL(#ee)
     * 0A
     * 03
     * 31 32 33
     * 11 0E 0F 07 02 4F 70 74 69 6E 32 0D 03 23 64 64
     * 11 0E 0F 07 03 4F 70 74 69 6E 33 0D 03 23 65 65
     * 11 0E 0F 07 04 4F 70 74 69 6E 34 0D 03 23 64 64
     * 10 Item Identifier Tag
     * 01
     * 01
     * 02 (O)Var ID to store result
     * 2D 0F 21 81 02 8D 0A 04 4F 74 61 20 54 65 73 74 34
     */
    private void appendSelectTag(Node processNode) throws BTSLBaseException {
        logger.debug("appendSelectTag ", "Entering ..........");
        StringBuffer coupleTagBuf = new StringBuffer();
        StringBuffer selectTagBuf = new StringBuffer();
        try {
            java.util.HashMap hp = giveTagAttributes(processNode);
            selectTagBuf.append(TagLibary.selectTag);// 2D0L240082
            String name = (String) hp.get(TagLibary.name);
            String textToDisplay1 = (String) hp.get(TagLibary.lang1);
            String textToDisplay2 = (String) hp.get(TagLibary.lang2);
            int choice = TagUtil.langFinder(textToDisplay1, textToDisplay2);
            if (choice != -1) {
                selectTagBuf.append(TagUtil.alphaTagUniCode(textToDisplay1, textToDisplay2, choice));
            }// Now
            String buffer = null;
            int indexOf = 0;
            String hexlength = null;
            int lengthOfOption = 0;
            int counter = 1; // this counter is maintaied for the position of
                             // option
            for (Node child = processNode.getFirstChild(); child != null; child = child.getNextSibling()) {
                try {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        continue;
                    }
                    hp = giveTagAttributes(child);
                    String onpick = (String) hp.get(TagLibary.onpick);// "ONPICK"
                    String value = (String) hp.get(TagLibary.value);// "VALUE"
                    coupleTagBuf.append(TagLibary.coupleTag);// 110L
                    textToDisplay1 = (String) hp.get(TagLibary.lang1);
                    textToDisplay2 = (String) hp.get(TagLibary.lang2);
                    choice = TagUtil.langFinder(textToDisplay1, textToDisplay2);
                    coupleTagBuf.append(TagUtil.itemTagUniCode(textToDisplay1, textToDisplay2, choice, counter));// value
                                                                                                                 // of
                                                                                                                 // option
                    counter = counter + 1;
                    hp = giveTagAttributes(child);
                    onpick = (String) hp.get(TagLibary.onpick);
                    value = (String) hp.get(TagLibary.value);
                    if (!TagUtil.isNullString(onpick)) {
                        coupleTagBuf.append(TagUtil.urlTag(onpick));
                    }
                    if (!TagUtil.isNullString(value)) {
                        coupleTagBuf.append(TagUtil.inlineTag(value));
                    }
                    buffer = coupleTagBuf.toString();
                    indexOf = buffer.indexOf(TagLibary.refLength);
                    hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
                    coupleTagBuf.replace(indexOf, indexOf + 2, hexlength);
                } catch (Exception e) {
                    logger.error("appendSelectTag ", " Exception :: " + e);
                
                    throw new BTSLBaseException(e);
                }
            }// END OF FOR LOOP
            selectTagBuf.append(coupleTagBuf.toString());
            selectTagBuf.append(TagLibary.itemTag);// Item tag 100101
            String nextVariable = nextVariable();// next variable from the
                                                 // userVariable pair is get
            userVariablePair.put(name.toUpperCase(), nextVariable);
            userVariableMemoryMap.put(name + "###" + nextVariable, new Integer(lengthOfOption));
            selectTagBuf.append(nextVariable);// Var Ref
            buffer = selectTagBuf.toString();
            indexOf = buffer.indexOf(TagLibary.refLength);
            hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            selectTagBuf.replace(indexOf, indexOf + 2, hexlength);
            stackByteCodeBuf.append(selectTagBuf.toString());
            // TagUtil.display(selectTagBuf.toString());
        } catch (Exception ex) {
            logger.error("appendSelectTag  ", " Exception .........." + ex);
            throw new BTSLBaseException("WebMultiLangParser", "appendSelectTag", "");
        }
        logger.debug("appendSelectTag ", " Existing  .........." + selectTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for Select tag
     * 
     * @param processNode
     *            Node , select
     * @throws Exception
     */
    /*
     * Select Item with Couple Tag(onpick supported) + Multi Card
     * 01 81 8C 02 05 44 65 63 6B 32 05 55 06 02 61 61 2D 4F 24 00 82 05 06 53
     * 65 6C 65 63 74 11 0E 0F 07 01 4F 70 74 69 6E 31 0D 03 23 65 65 11 0E 0F
     * 07 02 4F 70 74 69 6E 32 0D 03 23 64 64 11 0E 0F 07 03 4F 70 74 69 6E 33
     * 0D 03 23 65 65 11 0E 0F 07 04 4F 70 74 69 6E 34 0D 03 23 64 64 10 01 01
     * 02 05 15 06 02 64 64 2D 0F 21 81 02 8D 0A 04 4F 74 61 20 54 65 73 74 34
     * 05 15 06 02 65 65 2D 0F 21 81 02 8D 0A 04 4F 74 61 20 54 65 73 74 35
     * 01 81 8C 02 05 44 65 63 6B 32
     * 05 55 06 02 61 61
     * 2D 4F 24 00 82 05 06 53 65 6C 65 63 74 11 0E 0F 07 01 4F 70 74 69 6E 31
     * 0D 03 23 65 65 11 0E 0F 07 02 4F 70 74 69 6E 32 0D 03 23 64 64 11 0E 0F
     * 07 03 4F 70 74 69 6E 33 0D 03 23 65 65 11 0E 0F 07 04 4F 70 74 69 6E 34
     * 0D 03 23 64 64 10 01 01 02
     * 2D
     * 4F Len
     * 24 (F)Command Type
     * 00 Cmd Qualifier
     * 82 (F)Dest Device(ME)
     * 05 (O)Alpah Id
     * 06 Len
     * 53 65 6C 65 63 74 Value String
     * 11 (O)Couple Tag (Either couple Tag(11) or Item tag(0F))
     * 0E Len
     * 0F Tag Item
     * 07 Len
     * 01 Position
     * 4F 70 74 69 6E 31 Item String
     * 0D URL Tag
     * 03 Len
     * 23 65 65 URL(#ee)
     * 11 0E 0F 07 02 4F 70 74 69 6E 32 0D 03 23 64 64
     * 11 0E 0F 07 03 4F 70 74 69 6E 33 0D 03 23 65 65
     * 11 0E 0F 07 04 4F 70 74 69 6E 34 0D 03 23 64 64
     * 10 Item Identifier Tag
     * 01
     * 01
     * 02 Var ID to store result
     * 01
     */
    // This method was previously used now the above method is used because
    // now always couple tag has to be their + name for select tag is mandatory
    /*
     * private void appendSelectTagPrev (Node processNode) throws Exception
     * {
     * logger.debug("Entering appendSelectTag ..........");
     * StringBuffer coupleTag = new StringBuffer();
     * StringBuffer selectTag = new StringBuffer();
     * try
     * {
     * java.util.HashMap hp = new java.util.HashMap();
     * selectTag.append(TagLibary.selectTag);//2D0L240082
     * hp = giveTagAttributes(processNode);
     * boolean flag = false;//This Variable check whether we require
     * // name variable or not in case of <select name=""> in case of value in
     * option we reqire it otherwise not
     * String name = (String)hp.get(TagLibary.name);
     * if(!TagUtil.isNullString(name))
     * flag = true;
     * String textToDisplay1 = (String)hp.get(TagLibary.lang1);
     * String textToDisplay2 = (String)hp.get(TagLibary.lang2);
     * int choice = TagUtil.langFinder(textToDisplay1,textToDisplay2);
     * if(choice!=-1)
     * selectTag.append(TagUtil.alphaTagUniCode(textToDisplay1,textToDisplay2,choice
     * ));
     * //putting the values into the userVariable HashMap
     * boolean isOnPick = false;// if onPick is defined then its Value is true
     * else false
     * //first node is neglected as it is Text Node so next node is taken which
     * is Option Node
     * Node child = processNode.getFirstChild().getNextSibling();
     * hp = giveTagAttributes(child);
     * //String onpick = (String) hp.get("ONPICK");Prev
     * String onpick = "";//Now
     * String value = "";
     * String buffer = "";
     * int indexOf = 0;
     * String hexlength = "";
     * int lengthOfOption = 0;
     * // if(TagUtil.isNullString(onpick))
     * // isOnPick = false;
     * // else
     * // isOnPick = true;
     * int counter = 1; //this counter is maintaied for the position of option
     * for (child = processNode.getFirstChild(); child != null; child =
     * child.getNextSibling())
     * {
     * try
     * {
     * if(child.getNodeType()==Node.TEXT_NODE)
     * continue;
     * hp = giveTagAttributes(child);
     * onpick = (String) hp.get(TagLibary.onpick);//"ONPICK"
     * value =(String) hp.get(TagLibary.value);//"VALUE"
     * if(TagUtil.isNullString(onpick)&&TagUtil.isNullString(value))//New
     * isOnPick = false;
     * else
     * isOnPick = true;
     * if(isOnPick)
     * {
     * coupleTag.append(TagLibary.coupleTag);//110L
     * }
     * textToDisplay1=(String) hp.get(TagLibary.lang1);
     * textToDisplay2=(String) hp.get(TagLibary.lang2);
     * choice = TagUtil.langFinder(textToDisplay1,textToDisplay2);
     * coupleTag.append(TagUtil.itemTagUniCode(textToDisplay1,textToDisplay2,choice
     * ,counter));//value of option
     * counter = counter + 1;
     * //This method is used find the length of Max value of the option value so
     * that it
     * //can be place into the memory Map
     * //Starting
     * // if(lengthOfOption<child.getFirstChild().getNodeValue().length())
     * // lengthOfOption = child.getFirstChild().getNodeValue().length();
     * //Ending
     * if(isOnPick)
     * {
     * hp = giveTagAttributes(child);
     * onpick = (String) hp.get(TagLibary.onpick);
     * value = (String) hp.get(TagLibary.value);
     * if(!TagUtil.isNullString(onpick))
     * coupleTag.append(TagUtil.urlTag(onpick));
     * if(!TagUtil.isNullString(value))
     * {
     * flag = true;
     * coupleTag.append(TagUtil.inlineTag(value));
     * }
     * buffer = coupleTag.toString();
     * indexOf = buffer.indexOf(TagLibary.refLength);
     * hexlength =
     * TagUtil.lengthConverter(buffer.substring(indexOf+2).length()/2);
     * coupleTag.replace(indexOf,indexOf+2,hexlength);
     * }
     * }
     * catch(Exception e)
     * {
     * logger.error("Exception "," appendSelectTag"+e);
     * throw e;
     * }
     * }//END OF FOR LOOP
     * selectTag.append(coupleTag.toString()) ;
     * selectTag.append(TagLibary.itemTag);//Item tag 100101
     * if(flag)
     * {
     * String nextVariable = TagUtil.nextVariable();//next variable from the
     * userVariable pair is get
     * userVariablePair.put(name.toUpperCase(),nextVariable);
     * userVariableMemoryMap.put(name+"###"+nextVariable,new
     * Integer(lengthOfOption));
     * selectTag.append(nextVariable);//Var Ref
     * }
     * buffer = selectTag.toString();
     * indexOf = buffer.indexOf(TagLibary.refLength);
     * hexlength =
     * TagUtil.lengthConverter(buffer.substring(indexOf+2).length()/2);
     * selectTag.replace(indexOf,indexOf+2,hexlength);
     * stackByteCodeBuf.append(selectTag.toString());
     * TagUtil.display(selectTag.toString());
     * }
     * catch(Exception ex)
     * {
     * logger.error("Exception appendSelectTag  .........."+ex);
     * throw ex;
     * }
     * logger.debug("Existing appendSelectTag  .........."+selectTag.toString());
     * }
     */
    /**
     * This method is used to generate byte code for SendSMS tag
     * 
     * @param processNode
     *            Node , btsl-send-sms
     * @throws Exception
     */
    /*
     * Send SMS
     * 2D 1F 13 00 83 05 0B 53 65 6E 64 69 6E 67 20 53 4D 53 0B 0D 11 01 04 B0
     * 11 11 7F F6 01 03 08 01 02
     * 1F Len
     * 13 (F)Cmd Type
     * 00 Cmd Qualifier
     * 83 (F)Dest Device(network)
     * 05 (O)Alpha-Id Tag(Default is "Sending SMS")
     * 0B Len
     * 53 65 6E 64 69 6E 67 20 53 4D 53 Alpha -Id String
     * 06 (0)SMSC Address Tag
     * 12 Len of Address
     * 91 TON/NPI
     * 19 89 02 10 32 54 (Byteswapped Address- actual value 919820012345)
     * 0B SMS TPDU Tag
     * 0D Len
     * 11 1st Byte of TPDU ( 11- UDHI not set-Unformatted SMS, 51- UDHI
     * set-03.48 formatted SMS)
     * 01 2nd Byte of TPDU
     * 04 DA Len
     * BO Dest Add TON/NPI
     * 11 11 Dest add(Byte swapped)
     * 7F PID
     * F6 DCS
     * 01 Validity Period
     * 03 Userdata len
     * 08 01 02 Userdata ( Var Ref, can also be Inline Val, if Var Id is 50 it
     * is encryption buffer )
     */
    private void appendSendSMSTag(Node processNode) throws BTSLBaseException {
        logger.debug("appendsendSMSTag ", "Entering ..........");
        StringBuffer sendSMSTagBuf = new StringBuffer();
        try {
            java.util.HashMap hp = giveTagAttributes(processNode);
            String commandQ = null;
            String dest = null;
            String pid = null;
            String dcs = null;
            String dcsValue = null;
            boolean isVariable = true;
            String vp = null;// Validity period
            String data = processNode.getFirstChild().getNodeValue();
            String textToDisplay1 = null;
            String textToDisplay2 = null;
            if (!TagUtil.isVariable(data)) {
                isVariable = false;
            }
            String smsc = null;
            if (TagUtil.isNullString((String) hp.get(TagLibary.smsc))) {
                smsc = TagLibary.smsc1;// if smsc is not provided then by
                                       // default smsc1
            } else {
                smsc = (String) hp.get(TagLibary.smsc);
            }
            if (TagUtil.isNullString((String) hp.get(TagLibary.lang1))) {
                textToDisplay1 = null;
            } else {
                textToDisplay1 = (String) hp.get(TagLibary.lang1);
            }
            if (TagUtil.isNullString((String) hp.get(TagLibary.lang2))) {
                textToDisplay2 = null;
            } else {
                textToDisplay2 = (String) hp.get(TagLibary.lang2);
            }
            int choice = TagUtil.langFinder(textToDisplay1, textToDisplay2);
            if (TagUtil.isNullString((String) hp.get(TagLibary.sendSMS_pid))) {
                pid = TagLibary.sendSMSDefaultPID;
            } else {
                pid = (String) hp.get(TagLibary.sendSMS_pid);
            }
            if (TagUtil.isNullString((String) hp.get(TagLibary.sendSMS_dcs))) {
                dcs = TagLibary.sendSMSDefaultDCS;
                dcsValue = TagLibary.sendSMSDefaultDCSValue;
            } else {
                dcs = (String) hp.get(TagLibary.sendSMS_dcs);
                if (dcs.equalsIgnoreCase(TagLibary.binary)) {
                    dcsValue = TagLibary.sendSMSDefaultDCSValue;
                } else {
                    dcsValue = TagLibary.sendSMSDCSValue;// 00
                }
            }
            if (TagUtil.isNullString((String) hp.get(TagLibary.sendSMS_vp))) {
                vp = TagLibary.vp1;// if null then default is vp1
            } else {
                vp = (String) hp.get(TagLibary.sendSMS_vp);
            }
            dest = (String) hp.get(TagLibary.sendSMS_dest);
            String alphaTagConstruction = null;

            if (choice != -1) {
                alphaTagConstruction = TagUtil.alphaTagUniCode(textToDisplay1, textToDisplay2, choice);
            }
            String smsTPDUTagConstruction = smsTPDUTag(dcs, pid, dcsValue, vp, isVariable, data, dest);
            sendSMSTagBuf.append(TagLibary.sendSMSTag);
            if (choice != -1) {
                sendSMSTagBuf.append(alphaTagConstruction);
            }
            if (!TagUtil.isNullString(smsc)) {
                sendSMSTagBuf.append(TagUtil.smscTag(smsc));// Appends SMSC Tag
            }
            sendSMSTagBuf.append(smsTPDUTagConstruction);
            String buffer = sendSMSTagBuf.toString();
            int indexOf = buffer.indexOf(TagLibary.refLength);
            String hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            sendSMSTagBuf.replace(indexOf, indexOf + 2, hexlength);
            if (dcs.equalsIgnoreCase(TagLibary.binary) && pid.equals(TagLibary.sendSMSDefaultPID)) {
                commandQ = "00";
            } else {
                commandQ = "01";
            }
            buffer = sendSMSTagBuf.toString();
            indexOf = buffer.indexOf(TagLibary.commandQLength);// Index Of
                                                               // Command
                                                               // Qualifier"CQ"
            sendSMSTagBuf.replace(indexOf, indexOf + 2, commandQ);
            stackByteCodeBuf.append(sendSMSTagBuf.toString());
        } catch (Exception e) {
            logger.error("appendSendSMSTag Exception ", " " + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendSendSMSTag", "");
        }
        logger.debug("appendsendSMSTag  ", " Existing .........." + sendSMSTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for setCounter tag
     * 
     * @param processNode
     *            Node , btsl-setCounter
     * @throws Exception
     */

    /*
     * Set Counter
     * 43 04 01 08 01 21
     * 04 len(1)
     * 01 var Ref to store counter value(counter len 9 bytes)
     * 08 Var Ref Tag
     * 01 (F)Len
     * 21 Ref of Counter ON/OFF var
     */
    private void appendSetCounterTag(Node processNode) throws BTSLBaseException {
        logger.debug("appendSetCounterTag ", "Entering ..........");
        StringBuffer counterTagBuf = new StringBuffer();
        try {
            String nextVariable = nextVariable();// next variable from the
                                                 // userVariable pair is get
            java.util.HashMap hp = giveTagAttributes(processNode);
            String name = (String) hp.get(TagLibary.name);
            // String name =TagLibary.counterName;
            userVariablePair.put(name.toUpperCase(), nextVariable);
            counterTagBuf.append(TagLibary.counterTag);
            counterTagBuf.insert(4, nextVariable);
            stackByteCodeBuf.append(counterTagBuf.toString());
        } catch (Exception e) {
            logger.error("appendSetCounterTag", " Exception .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendSetCounterTag", "");
        }
        // appendReadCounterTag(processNode);
        logger.debug("appendSetCounterTag", " Existing .........." + counterTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for setData tag
     * 
     * @param processNode
     *            Node , btsl-set
     * @throws BaseException
     *             , Exception
     */
    /*
     * Set Data
     * 45 07 01 08 01 21 08 01 02
     * 07 len(1)
     * 01 Type of data 01 for mobile 02 for amount 03 userid
     * 08 Var Ref Tag
     * 01 (F)Len
     * 21 Ref of Counter ON/OFF var
     * 08 Var Ref Tag
     * 01 (F)Len
     * 02 Variable To store
     */
    private void appendSetTag(Node processNode) throws BaseException, BTSLBaseException {
        logger.debug("appendSetTag ", "Entering ..........");
        StringBuffer setTagBuf = new StringBuffer();
        try {
            // String nextVariable = TagUtil.nextVariable();//next variable from
            // the userVariable pair is get
            java.util.HashMap hp = giveTagAttributes(processNode);
            String var = (String) hp.get(TagLibary.var);
            String data = (String) hp.get(TagLibary.data);
            // userVariablePair.put(var.toUpperCase().substring(1).trim(),nextVariable);
            setTagBuf.append(TagLibary.setTag);
            setTagBuf.append(TagLibary.fixedLength07);
            if (data.equalsIgnoreCase(TagLibary.mobileNo)) {
                setTagBuf.append(TagLibary.fixedLength01);
            } else if (data.equalsIgnoreCase(TagLibary.amount)) {
                setTagBuf.append(TagLibary.fixedLength02);
            } else if (data.equalsIgnoreCase(TagLibary.userId)) {
                setTagBuf.append(TagLibary.fixedLength03);
            } else {
                throw new BaseException("ota.services.error.datatypenotsupported");// here
                                                                                   // throw
                                                                                   // BaseException
            }
            /*
             * setTag.append(TagLibary.varRef);
             * setTag.append(TagLibary.fixedLength01);
             * setTag.append(TagLibary.tidFlagValue);
             */
            /*
             * setTag.append(TagLibary.varRef);
             * setTag.append(TagLibary.fixedLength01);
             * setTag.append(nextVariable);
             */
            setTagBuf.append(varRefTag("$" + TagLibary.tidFlag));
            // String variable =(String)
            // WebMultiLangParser.userVariablePair.get(var.toUpperCase().trim());

            setTagBuf.append(varRefTag(var.toUpperCase()));
            stackByteCodeBuf.append(setTagBuf.toString());
        } catch (Exception e) {
            logger.error("appendSetsetTag", " Exception .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendSetsetTag", "");
        }
        // appendReadCounterTag(processNode);
        logger.debug("appendSetsetTag ", "Existing .........." + setTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for Delete tag
     * 
     * @param processNode
     *            Node , btsl-delete
     * @throws Exception
     */
    /*
     * Delete Tag
     * 48 01 03
     * 48 Tag
     * 01 Length
     * 03 Value
     * Up to this value all the buffer will be cleared
     * Syntax:
     * <btsl-delete var="$abc" />
     */
    private void appendDeleteTag(Node processNode) throws BaseException, BTSLBaseException {
        logger.debug("appendDeleteTag ", "Entering ..........");
        StringBuffer deleteTagBuf = new StringBuffer();
        try {
            java.util.HashMap hp = giveTagAttributes(processNode);
            String var = (String) hp.get(TagLibary.var);
            deleteTagBuf.append(TagLibary.deleteTag);
            deleteTagBuf.append(TagLibary.fixedLength01);
            String buffer = var.substring(1).trim().toUpperCase();
            String variable = (String) userVariablePair.get(buffer);
            if (TagUtil.isNullString(variable)) {
                logger.error("appendDeleteTag Exception ", " VariableRef Not found" + buffer);
                throw new com.btsl.common.BaseException("ota.services.error.variablenotfound," + buffer);
            }
            deleteTagBuf.append(variable);
            stackByteCodeBuf.append(deleteTagBuf.toString());
        } catch (Exception e) {
            logger.error("appendDeleteTag ", "Exception .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendDeleteTag", "");
        }
        // appendReadCounterTag(processNode);
        logger.debug("appendDeleteTag", " Existing .........." + deleteTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for get tag
     * 
     * @param processNode
     *            Node , get
     * @throws BaseException
     *             , Exception
     */
    /*
     * Get Data
     * Tag, len, Type(1/2/3), Var where data will be stored
     * 46 02 01 01
     */
    private void appendGetTag(Node processNode) throws BaseException, BTSLBaseException {
        logger.debug("appendGetTag ", "Entering ..........");
        StringBuffer getTagBuf = new StringBuffer();
        try {
            String nextVariable = nextVariable();// next variable from the
                                                 // userVariable pair is get
            java.util.HashMap hp = giveTagAttributes(processNode);
            String name = (String) hp.get(TagLibary.name);
            String data = (String) hp.get(TagLibary.data);
            userVariablePair.put(name.toUpperCase(), nextVariable);
            getTagBuf.append(TagLibary.getTag);
            getTagBuf.append(TagLibary.fixedLength02);
            if (data.equalsIgnoreCase(TagLibary.mobileNo)) {
                getTagBuf.append(TagLibary.fixedLength01);
            } else if (data.equalsIgnoreCase(TagLibary.amount)) {
                getTagBuf.append(TagLibary.fixedLength02);
            } else if (data.equalsIgnoreCase(TagLibary.userId)) {
                getTagBuf.append(TagLibary.fixedLength03);
            } else {
                throw new BaseException("ota.services.error.datatypenotsupported");// here
                                                                                   // throw
                                                                                   // BaseException
            }
            getTagBuf.append(nextVariable);
            stackByteCodeBuf.append(getTagBuf.toString());
        } catch (Exception e) {
            logger.error("appendGetTag ", "Exception .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendGetTag", "");
        }
        logger.debug("appendGetTag", " Existing .........." + getTagBuf.toString());
    }

    /**
     * This method is used to generate byte code for WML tag
     * 
     * @param processNode
     *            Node , wml
     * @throws Exception
     */
    private void appendWMLTag(String name) throws BTSLBaseException {
        logger.debug("appendWMLTag ", "Entering .........." + name);
        try {
            stackByteCodeBuf.append(TagLibary.deckTag);
            // stackByteCodeBuf.append("02");//Tag
            stackByteCodeBuf.append(TagUtil.lengthConverter(name.length())); // length
            stackByteCodeBuf.append(TagUtil.stringToByteConverter(name)); //
        } catch (Exception e) {
            logger.error("appendWMLTagv", " Exception  .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendWMLTagv", "");
        }
        logger.debug("appendWMLTag ", "Exiting ..........");
    }

    /**
     * This method is gives the different attribute values
     * 
     * @param processNode
     *            of Node type
     * @return hp type of HashMap
     * @throws Exception
     */
    private java.util.HashMap giveTagAttributes(Node start) throws BTSLBaseException {
        // logger.debug("giveTagAttributes ","Entering ..........");
        java.util.HashMap hp = new java.util.HashMap();
        try {
            Node attr = null;
            if (start.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.NamedNodeMap startAttr = start.getAttributes();
                int startAttrgetLength=startAttr.getLength();
                for (int i = 0; i < startAttrgetLength; i++) {
                    attr = startAttr.item(i);
                    hp.put(attr.getNodeName().toUpperCase(), attr.getNodeValue());
                    logger.debug("giveTagAttributes", "  Attribute:  " + attr.getNodeName() + " = " + attr.getNodeValue());
                }
            }
        } catch (Exception e) {
            logger.error("giveTagAttributes", " Exception .........." + e);
            throw new BTSLBaseException("WebMultiLangParser", "giveTagAttributes", "");
        }
        // logger.debug("giveTagAttributes ","Exiting .........."+hp);
        return hp;
    }

    public static void main(String args[]){
        try {
            new TagUtil();

       //     org.apache.log4j.PropertyConfigurator.configure("c:\\abc.log");
            // ApplicationResourses.load("F:\\SOURCE CODE\\csmsh\\configFiles\\Application.props");
            logger.debug("", "Entering main");
            File docFile = new File("C:\\text.wml");
            // File docFile = new File("c:\\text.wml");
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(docFile);
            // doc = db.parse(is);
            // STEP 1: Get the root element
            org.w3c.dom.Element root = doc.getDocumentElement();
            // STEP 2: Get the children
            WebMultiLangParser wmlp = new WebMultiLangParser();
            java.util.HashMap hp = wmlp.giveTagAttributes(root);
            String name = null;
            if (!TagUtil.isNullString((String) hp.get(TagLibary.name))) {
                name = (String) hp.get(TagLibary.name);
                wmlp.appendWMLTag(name.trim());
            }
            wmlp.stepThrough(root);
            wmlp.replaceCardTags();
            wmlp.replaceWMLTag();
            // checkNull();
            logger.info("", "Displaying userVariablePair-->   " + wmlp.userVariablePair);
            logger.info("", wmlp.userVariableMemoryMap);
            final String methodName = "main";
            logger.debug(methodName, "Final Byte Code --> " + wmlp.stackByteCodeBuf);
            logger.debug(methodName, "Final Memory Array --> " + wmlp.userVariableMemoryMap);

        } catch (BaseException e) {
            logger.error("Exception ", " Main" + e);
        } catch (Exception e) {
            logger.error("Exception ", " Main" + e);
        }
        logger.info("", "Exiting Main ............");
    }

    /**
     * This method checks that final byte code contains only valid characters
     * 
     * @throws Exception
     */
    /*
     * private void checkNull() throws Exception
     * {
     * int length =stackByteCodeBuf.toString().trim().length();
     * char arr[]=stackByteCodeBuf.toString().trim().toCharArray();
     * for(int i=0;i<length;i++)
     * {
     * if((arr[i] >=(char)48&& arr[i]<=(char)57)||(arr[i] >=(char)65&&
     * arr[i]<=(char)70)||(arr[i] >=(char)97&& arr[i]<=(char)102) )
     * {
     * }
     * else
     * {
     * logger.error("Invalid character position"+(i/2)+" value "+arr[i]);
     * throw new Exception("Invalid character position"+(i/2)+" value "+arr[i]);
     * }
     * }
     * TagUtil.display(stackByteCodeBuf.toString().toUpperCase());
     * //System.out.println(stackByteCodeBuf.toString().toUpperCase());
     * logger.info(userVariablePair);
     * logger.info(userVariableMemoryMap);
     * }
     */
    /**
     * This method is used for processing the various tags and then generating
     * bytecode for corrosponing Tag
     * 
     * @param processNode
     *            of Node type
     * @throws Exception
     */
    private void processChild(Node processNode) throws BTSLBaseException {
        // logger.debug("processChild ","Entering ..........");
        try {
            int caseValue = 0;
            String name = processNode.getNodeName();
            if (name.equalsIgnoreCase(TagLibary.TAG_INPUT)) {
                caseValue = 1;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_COMPARE))// "btsl-compare"
            {
                caseValue = 2;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_SEND_SMS)) {
                caseValue = 3;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_CONCAT)) {
                caseValue = 4;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_IF_DO))// "btsl-if-do"
            {
                caseValue = 5;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_IF_ELSE))// "btsl-if-else"
            {
                caseValue = 6;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_SELECT))// "select"
            {
                caseValue = 7;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_GO))// "go"
            {
                caseValue = 8;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_SETCOUNTER))// "btsl-setCounter"
            {
                caseValue = 9;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_READCOUNTER))// "btsl-readCounter"
            {
                caseValue = 10;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_ENCRYPT))// "btsl-encrypt"
            {
                caseValue = 11;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_CARD))// card
            {
                caseValue = 12;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_SET))// btsl-set
            {
                caseValue = 13;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_GET))// btsl-get
            {
                caseValue = 14;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_UPDATEMENUOPTION))// btsl-updateMenuOption
            {
                caseValue = 15;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_DELETE)) // btsl-delete
            {
                caseValue = 16;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_DISPLAYTEXT)) // btsl-displayText
            {
                caseValue = 100;
            } else if (name.equalsIgnoreCase(TagLibary.TAG_BTSL_LANGCHANGE)) // btsl-displayText
            {
                caseValue = 101;
            } else {
                caseValue = 1000;
            }
            switch (caseValue) {
            case 1:
                appendInputTag(processNode);
                break;
            case 2:
                appendCompareTag(processNode);
                break;
            case 3:
                appendSendSMSTag(processNode);
                break;
            case 4:
                appendConcatTagUnicode(processNode);
                break;
            case 5:
                appendIfDoTag(processNode);
                break;
            case 6:
                appendIfElseTag(processNode);
                break;
            case 7:
                appendSelectTag(processNode);
                break;
            case 8:
                appendGoTag(processNode);
                break;
            case 9:
                appendSetCounterTag(processNode);
                break;
            case 10:
                appendReadCounterTag(processNode);
                break;
            case 11:
                appendEncryptTag(processNode);
                break;
            case 12:
                appendCardTag(processNode);
                break;
            case 13:
                appendSetTag(processNode);
                break;
            case 14:
                appendGetTag(processNode);
                break;
            case 15:
                appendUpdateMenuOptionTag(processNode);
                break;
            case 16:
                appendDeleteTag(processNode);
                break;
            case 100: // Text
                appendDisplayTextTag(processNode);
                break;
            case 101: // Text
                appendLangChangeTag(processNode);
                break;
            default:
           	 if(logger.isDebugEnabled()){
           		 logger.debug("Default Value " , caseValue);
           	 }
            	 
            }
        }catch (BTSLBaseException e) {
            logger.error("processChild ", " BTSLBaseException :: " + e);
            throw new BTSLBaseException("WebMultiLangParser", "processChild", "");
        } catch (Exception e) {
            logger.error("processChild ", " Exception :: " + e);
            throw new BTSLBaseException("WebMultiLangParser", "processChild", "");
        }
        // logger.debug("processChild ","Exiting ..........");
    }

    /**
     * This method is used Replace CL with Actual Card Length
     * 
     * @throws Exception
     */
    private void replaceCardTags() throws BTSLBaseException {
        logger.debug("replaceCardTags ", "Entering .........." + stackByteCodeBuf.toString());
        int firstIndex = 0;
        int secondIndex = 0;
        String buffer = stackByteCodeBuf.toString();
        for (int i = 0; i < cardNo; i++) {
            firstIndex = buffer.indexOf(TagLibary.cardLength);// CL
            secondIndex = buffer.indexOf(TagLibary.cardLength, firstIndex + 1);// CL
            if (i == cardNo - 1) {
                stackByteCodeBuf.replace(firstIndex, firstIndex + 2, TagUtil.lengthConverter(((stackByteCodeBuf.substring(firstIndex).length()) / 2 - 1)));
            } else {
                stackByteCodeBuf.replace(firstIndex, firstIndex + 2, TagUtil.lengthConverter(((stackByteCodeBuf.substring(firstIndex, secondIndex).length()) / 2 - 2)));
            }
            buffer = stackByteCodeBuf.toString();
        }
        logger.debug("", "replaceCardTags Existing ..........");
    }

    /**
     * This method is used Replace WL with Actual WML
     * 
     * @throws Exception
     */
    private void replaceWMLTag() throws BTSLBaseException {
        logger.debug("replaceWMLTag ", "Entering .........." + stackByteCodeBuf.toString());
        try {
            String buffer = stackByteCodeBuf.toString();
            int indexOf = buffer.indexOf(TagLibary.wmlLength);
            String hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            stackByteCodeBuf.replace(indexOf, indexOf + 2, hexlength);
        } catch (Exception e) {
            logger.error("replaceWMLTag ", " Exception :: " + e);
            throw new BTSLBaseException("WebMultiLangParser", "replaceWMLTag", "");
        }
        logger.debug("replaceWMLTag ", "Existing  ..........");
    }

    /**
     * This method is used for processing the various nodes recursively
     * 
     * @param processNode
     *            of Node type
     * @throws Exception
     */
    private void stepThrough(Node start) throws BTSLBaseException {
        // logger.debug("stepThrough "," ","Entering ..........");
        for (Node child = start.getFirstChild(); child != null; child = child.getNextSibling()) {
            processChild(child);
            stepThrough(child);

        }
        // logger.debug("stepThrough "," Exiting ..........");
    }

    /**
     * This method is used to generate byte code for Concat tag
     * 
     * @param processNode
     *            of Node type , btsl-concat
     * @throws BaseException
     *             , Exception
     * @throws BTSLBaseException 
     */
    /*
     * Concatenate
     * 24 10 02 0A 04 54 65 73 74 08 01 01 0A 04 54 65 66 67
     * 24 Concat Tag
     * 10 Length(1 Byte)
     * 02 Variable To store
     * 0A Inline Tag
     * 04 Len
     * 54 65 73 74 Value
     * 08 Var Reference Tag
     * 01 (F)len
     * 01 Var Ref
     * 0A 04 54 65 66 67 - Inline
     */
    private void appendConcatTagUnicode(Node processNode) throws BaseException, BTSLBaseException {
        StringBuffer concatTagBuf = new StringBuffer();
        try {
            logger.debug("appendConcatTagUnicode :: ", "Entering ..........");

            java.util.HashMap hp = giveTagAttributes(processNode);
            String name = (String) hp.get(TagLibary.concat_name);
            concatTagBuf.append(TagLibary.conCatTag);
            // If name=$bigVariable then variable=0x55
            if (name.equalsIgnoreCase("bigVariable")) {
                concatTagBuf.append("55");// Var Ref has to changes
                userVariablePair.put(name.toUpperCase(), "55");
            } else {
                String nextVariable = nextVariable();// next variable from the
                                                     // userVariable pair is get
                concatTagBuf.append(nextVariable);// Var Ref has to changes
                userVariablePair.put(name.toUpperCase(), nextVariable);
            }
            Node child;
            boolean isMultiLang = false;
            int previousValue = 0;
            int flag = 0;
            String textToDisplay1 = null;
            String textToDisplay2 = null;
            String inLineValue = null;
            String varName = null;
            int choice = 0;
            for (child = processNode.getFirstChild(); child != null; child = child.getNextSibling()) {
                hp = giveTagAttributes(child);
                if (child.getNodeName().equalsIgnoreCase(TagLibary.text))// text
                {
                    if (flag == 0) {
                        flag = 1;
                    }
                    textToDisplay1 = (String) hp.get(TagLibary.lang1);
                    textToDisplay2 = (String) hp.get(TagLibary.lang2);
                    choice = TagUtil.langFinder(textToDisplay1, textToDisplay2);
                    if (flag == 1) {
                        previousValue = choice;
                        if (choice == TagLibary.english || choice == TagLibary.unicode) {
                            isMultiLang = false;
                        } else if (choice == TagLibary.both) {
                            isMultiLang = true;
                            // concatTagBuf.append(TagLibary.unicodeDCS);
                        } else {
                            logger.error("appendConcatTagUnicode ", " Invalid Choice For Language");// here
                                                                                                    // throw
                                                                                                    // BaseException
                            throw new BaseException("ota.services.error.invalidchoiceforlang");
                        }
                        flag = -1;
                    }
                    if (previousValue != choice) {
                        logger.error("appendConcatTagUnicode ", " Change in Lang Option Not Supported" + previousValue + " " + choice);// here
                                                                                                                                       // throw
                                                                                                                                       // BaseException
                        throw new BaseException("ota.services.error.langoptionchange");
                    }
                    inLineValue = TagUtil.inlineTagUniCode(" " + textToDisplay1.trim(), " " + textToDisplay2.trim(), choice);
                    concatTagBuf.append(inLineValue);
                } else if (child.getNodeName().equalsIgnoreCase(TagLibary.var))// "var"
                {
                    varName = (String) hp.get(TagLibary.value);
                    concatTagBuf.append(varRefTag(varName));
                } else {
                    continue;
                }
            }
            if (isMultiLang) {
                concatTagBuf.insert(6, TagLibary.unicodeDCS);
            } else {
                concatTagBuf.insert(6, TagLibary.defaultDCS);
            }
            String buffer = concatTagBuf.toString();
            int indexOf = buffer.indexOf(TagLibary.refLength);// 0L
            String hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            concatTagBuf.replace(indexOf, indexOf + 2, hexlength);
            stackByteCodeBuf.append(concatTagBuf.toString());
        }// End of try block
        catch (com.btsl.common.BaseException be) {
            logger.error("appendConcatTagUnicode ", " BaseException " + be);
            throw be;
        } catch (Exception e) {
            logger.error(" appendConcatTagUnicode  ", "Exception:: " + e);
            throw new BTSLBaseException("WebMultiLangParser", "appendConcatTagUnicode", "");
        }
        logger.debug("appendConcatTagUnicode ", " Existing .........." + concatTagBuf.toString());
    }

    /**
     * This method is method from where execution starts
     * 
     * @param is
     *            String
     * @throws BaseException 
     * @throws Exception
     */
    public String webByteGeneratorStr(String inputString) throws BTSLBaseException, BaseException {
        logger.debug("webByteGeneratorStr :: ", "Entering ............" + inputString);
        StringReader stringReader = null;
        try {
            userVariablePair = new java.util.HashMap();
            userVariableMemoryMap = new java.util.HashMap();
            cardNo = 0;
            stackByteCodeBuf = new StringBuffer();
            stringReader = new StringReader(inputString);
            InputSource isa = new InputSource(stringReader);
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(isa);
            org.w3c.dom.Element root = doc.getDocumentElement();
            // NodeList children = root.getChildNodes();
            java.util.HashMap hp = giveTagAttributes(root);
            String name = null;
            if (!TagUtil.isNullString((String) hp.get(TagLibary.name))) {
                name = (String) hp.get(TagLibary.name);
                appendWMLTag(name.trim());
            }
            stepThrough(root);
            replaceCardTags();
            replaceWMLTag();
            // System.out.println("Final Byte Code Starts");
            TagUtil.finalCheck(stackByteCodeBuf.toString());
            // System.out.println("Final Byte Code Ends");
            logger.info("webByteGeneratorStr  ", " Displaying userVariablePair   " + userVariablePair);
        } catch (BaseException ef) {
            logger.error("webByteGeneratorStr ", " Exception :: " + ef);
            throw ef;
        } catch (Exception e) {
            logger.error("webByteGeneratorStr ", "Exception :: " + e);
            throw new BTSLBaseException("WebMultiLangParser", "webByteGeneratorStr", "");
        }finally{
        	try{
        		if(stringReader!=null){
        			stringReader.close();
        		}
        	}catch(Exception e){
        		logger.error("webByteGeneratorStr ", " Exception :: " + e);
        	}
        }
        logger.debug("webByteGeneratorStr ", " Exiting  ............" + stackByteCodeBuf.toString().length() + "ByteCode=" + stackByteCodeBuf.toString());
        return stackByteCodeBuf.toString();
    }

    /**
     * This method is used to get the Next Variable
     * 
     * @return String
     * @throws Exception
     */
    private String nextVariable() throws BTSLBaseException {
        logger.debug("nextVariable ", "Entering ........");
        int size = userVariablePair.size();
        String hexlength = TagUtil.lengthConverter((size + 1));
        logger.debug("nextVariable ", "Exiting ........" + hexlength);
        return hexlength;
    }

    /**
     * This method is used is check if the variable exists previouly in the
     * hashmap is yes then provides its value else throws exception
     * 
     * @param str
     *            String
     * @return String
     * @throws BaseException 
     * @throws Exception
     */
    private String varRefTag(String str) throws BTSLBaseException {

        logger.debug("varRefTag ", "Entering " + str);
        StringBuffer varRefTagBuf = new StringBuffer();
        String buffer = new String();
        try {
            buffer = str.substring(1).trim().toUpperCase();
            if (buffer.equalsIgnoreCase(TagLibary.pinFlag)) {
                varRefTagBuf.append(TagLibary.varRef);// tag//08
                varRefTagBuf.append(TagLibary.fixedLength01);// length
                varRefTagBuf.append(TagLibary.pinFlagValue);// variable
                                                            // Reference
                return varRefTagBuf.toString();
            }
            if (buffer.equalsIgnoreCase(TagLibary.tidFlag)) {
                varRefTagBuf.append(TagLibary.varRef);// tag//08
                varRefTagBuf.append(TagLibary.fixedLength01);// length
                varRefTagBuf.append(TagLibary.tidFlagValue);// variable
                                                            // Reference
                return varRefTagBuf.toString();
            }
            if (buffer.equalsIgnoreCase(TagLibary.productFlag)) {
                varRefTagBuf.append(TagLibary.varRef);// tag//08
                varRefTagBuf.append(TagLibary.fixedLength01);// length
                varRefTagBuf.append(TagLibary.productFlagValue);// variable
                                                                // Reference
                return varRefTagBuf.toString();
            }
            if (buffer.equalsIgnoreCase(TagLibary.fixedData)) {
                varRefTagBuf.append(TagLibary.varRef);// tag//08
                varRefTagBuf.append(TagLibary.fixedLength01);// length
                varRefTagBuf.append(TagLibary.fixedDataValue);// variable
                                                              // Reference
                return varRefTagBuf.toString();
            }
            String variable = (String) userVariablePair.get(buffer);
            if (TagUtil.isNullString(variable)) {
                logger.error("varRefTag ", " VariableRef Not found" + buffer);
                throw new com.btsl.common.BaseException("ota.services.error.variablenotfound," + buffer);
            } else {
                varRefTagBuf.append(TagLibary.varRef);// tag
                varRefTagBuf.append(TagLibary.fixedLength01);// length
                varRefTagBuf.append(variable);// variable Reference
            }
        } catch (Exception e) {
            logger.error("varRefTag ", "Exception :: " + e);
            throw new BTSLBaseException("WebMultiLangParser", "varRefTag", "");
        }
        logger.debug("", "Existing varRefTag " + varRefTagBuf.toString());
        return varRefTagBuf.toString();
    }

    /*
     * 0B SMS TPDU Tag
     * 0D Len
     * 11 1st Byte of TPDU ( 11- UDHI not set-Unformatted SMS, 51- UDHI
     * set-03.48 formatted SMS)
     * 01 2nd Byte of TPDU
     * 04 DA Len
     * BO Dest Add TON/NPI
     * 11 11 Dest add(Byte swapped)
     * 7F PID
     * F6 DCS
     * 01 Validity Period
     * 03 Userdata len
     * 08 01 02 Userdata ( Var Ref, can also be Inline Val, if Var Id is 50 it
     * is encryption buffer )
     */
    /**
     * This method is used to construct smsTPDU Tag
     * 
     * @param typeOfSMS
     *            String
     * @param pid
     *            String
     * @param dcsValue
     *            String
     * @param vp
     *            String
     * @param isVariable
     *            boolean
     * @param varOrConstant
     *            String
     * @param destinationAddress
     *            String
     * @return String
     * @throws Exception
     */
    private String smsTPDUTag(String typeOfSMS, String pid, String dcsValue, String vp, boolean isVariable, String varOrConstant, String destinationAddress) throws BTSLBaseException {
        logger.debug("smsTPDUTag ", "Entering........" + typeOfSMS + " " + pid + " " + dcsValue + " " + vp + " " + isVariable + " " + varOrConstant + " " + destinationAddress);
        StringBuffer smsTPDUTagBuf = new StringBuffer();
        try {
            String firstByte = null;
            String secondByte = "01";
            String userDataLength = null;
            int desLength = 0;
            smsTPDUTagBuf.append(TagLibary.smsTPDUTag);// tag
            smsTPDUTagBuf.append(TagLibary.refLength);// length 0L
            if (dcsValue.equalsIgnoreCase(TagLibary.sendSMSDefaultDCSValue)) {
                firstByte = TagLibary.fixedValue51;// 51
            } else {
                firstByte = TagLibary.fixedValue11;// 11
            }
            smsTPDUTagBuf.append(firstByte);
            smsTPDUTagBuf.append(secondByte);
            if (destinationAddress.equalsIgnoreCase(TagLibary.port1)) {
                smsTPDUTagBuf.append(TagLibary.fixedLength02);// Length is fixed
                                                              // one byte for FF
                                                              // and another for
                                                              // port
                smsTPDUTagBuf.append(TagLibary.varRefTag);
                smsTPDUTagBuf.append(TagLibary.port1Value);// Destination port

            } else if (destinationAddress.equalsIgnoreCase(TagLibary.port2)) {
                smsTPDUTagBuf.append(TagLibary.fixedLength02);// Length is fixed
                                                              // one byte for FF
                                                              // and another for
                                                              // port
                smsTPDUTagBuf.append(TagLibary.varRefTag);
                smsTPDUTagBuf.append(TagLibary.port2Value);// Destination port
            } else if (destinationAddress.equalsIgnoreCase(TagLibary.port3)) {
                smsTPDUTagBuf.append(TagLibary.fixedLength02);// Length is fixed
                                                              // one byte for FF
                                                              // and another for
                                                              // port
                smsTPDUTagBuf.append(TagLibary.varRefTag);
                smsTPDUTagBuf.append(TagLibary.port3Value);// Destination port
            } else {
                desLength = destinationAddress.length();
                smsTPDUTagBuf.append(TagUtil.lengthConverter(desLength));
                smsTPDUTagBuf.append(TagLibary.TON_NPI);
                smsTPDUTagBuf.append(TagUtil.byteSwapper(destinationAddress));
            }
            smsTPDUTagBuf.append(pid);
            smsTPDUTagBuf.append(dcsValue);
            if (vp.equalsIgnoreCase(TagLibary.vp1)) {
                smsTPDUTagBuf.append(TagLibary.varRefTag);// 0F
                smsTPDUTagBuf.append(TagLibary.vp1Value);
            } else if (vp.equalsIgnoreCase(TagLibary.vp2)) {
                smsTPDUTagBuf.append(TagLibary.varRefTag);
                smsTPDUTagBuf.append(TagLibary.vp2Value);
            } else if (vp.equalsIgnoreCase(TagLibary.vp3)) {
                smsTPDUTagBuf.append(TagLibary.varRefTag);
                smsTPDUTagBuf.append(TagLibary.vp3Value);
            } else {
                smsTPDUTagBuf.append(vp);
            }
            if (isVariable) {
                userDataLength = TagLibary.fixedLength03;// 03
                smsTPDUTagBuf.append(userDataLength);
                smsTPDUTagBuf.append(varRefTag(varOrConstant));
            } else {
                userDataLength = "" + TagUtil.lengthConverter(varOrConstant.length() + 2);// Two
                                                                                          // is
                                                                                          // added
                                                                                          // for
                                                                                          // Inline
                                                                                          // Tag
                                                                                          // and
                                                                                          // Length
                smsTPDUTagBuf.append(userDataLength);
                smsTPDUTagBuf.append(TagUtil.inlineTag(varOrConstant));
            }
            String buffer = smsTPDUTagBuf.toString();
            int indexOf = buffer.indexOf(TagLibary.refLength);
            String hexlength = TagUtil.lengthConverter(buffer.substring(indexOf + 2).length() / 2);
            smsTPDUTagBuf.replace(indexOf, indexOf + 2, hexlength);
        } catch (Exception e) {
            logger.error("smsTPDUTag ", "Exception ................ " + e);
            throw new BTSLBaseException("WebMultiLangParser", "smsTPDUTag", "");
        }
        logger.debug("smsTPDUTag ", "", "Exiting ........" + smsTPDUTagBuf);
        return smsTPDUTagBuf.toString();
    }

    /*
     * private String webByteGeneratorLat(InputSource is) throws Exception {
     * logger.debug("","Entering webByteGeneratorLat............"+is);
     * try
     * {
     * 
     * userVariablePair = new java.util.HashMap();
     * userVariableMemoryMap = new java.util.HashMap();
     * stackByteCodeBuf = new StringBuffer();
     * cardNo = 0;
     * 
     * Document doc = null;
     * 
     * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
     * DocumentBuilder db = dbf.newDocumentBuilder();
     * 
     * 
     * //doc = db.parse(docFile);
     * doc = db.parse(is);
     * //STEP 1: Get the root element
     * org.w3c.dom.Element root = doc.getDocumentElement();
     * //STEP 2: Get the children
     * NodeList children = root.getChildNodes();
     * 
     * java.util.HashMap hp = new java.util.HashMap();
     * hp = giveTagAttributes(root);
     * String name =null;
     * if(!TagUtil.isNullString((String) hp.get(TagLibary.name)))
     * name = (String) hp.get(TagLibary.name);
     * appendWMLTag(name.trim());
     * 
     * stepThrough(root);
     * replaceCardTags();
     * replaceWMLTag();
     * System.out.println("Final Byte Code Starts");
     * checkNull();
     * System.out.println("Final Byte Code Ends");
     * 
     * //System.out.println("");
     * logger.info("Displaying userVariablePair-->  hhhhhhhhhhhhhhhhhhhhhh ");
     * logger.info("Displaying userVariablePair-->   "+userVariablePair);
     * logger.info(userVariableMemoryMap);
     * 
     * }
     * catch(BaseException ef)
     * {
     * logger.error("Exception :: webByteGenerator"+ef);
     * throw ef;
     * }
     * catch(Exception e)
     * {
     * logger.error("Exception :: webByteGenerator"+e);
     * throw e;
     * }
     * logger.info("","Exiting webByteGenerator ............");
     * return stackByteCodeBuf.toString();
     * 
     * }
     */
    /*
     * private void processParent (Node processNode) throws Exception
     * {
     * try
     * {
     * System.out.println("","Entering Parent..................");
     * int caseValue = 0;
     * String name = processNode.getNodeName();
     * if(name.equalsIgnoreCase("wml"))
     * caseValue = 1;
     * else
     * if(name.equalsIgnoreCase("card"))
     * caseValue = 2;
     * else
     * if(name.equalsIgnoreCase("select"))
     * caseValue = 3;
     * else
     * caseValue =1000;
     * switch(caseValue)
     * {
     * case 1 :
     * appendWMLTag(name);
     * System.out.println("Entering Parent................WML..");
     * break;
     * 
     * case 2:
     * appendCardTag(processNode);
     * System.out.println("Entering Parent..............Card....");
     * break;
     * 
     * case 3:
     * 
     * break;
     * case 1000:
     * logger.fatal("processParent "," TagNotFound"+processNode);
     * throw new Exception("TagNotFound "+processNode);
     * //break;
     * }
     * }
     * catch(Exception e)
     * {
     * System.out.println("");
     * throw e;
     * }
     * 
     * }
     */

}
