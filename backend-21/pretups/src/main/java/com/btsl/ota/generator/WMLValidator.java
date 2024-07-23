package com.btsl.ota.generator;

/**
 * @(#)WMLValidator.java
 *                       Copyright(c) 2003, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 *                       This class is written for validating the wml tag input
 *                       in accordance to the document WMLValidaion Ver 1.0
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 * 
 *                       Kchitij Kumar 11/13/2003 12:57:47 PM Initial Creation
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;

public class WMLValidator {

    
    
    private static Log _log = LogFactory.getLog(WMLValidator.class.getName());
    private HashMap _resourceMap = new HashMap();
    private String _global_cardid = "";
    private Node _prev_elem_node = null;
    private String[] _name_attr_array = new String[10000];
    private String[] _cardid_attr_array = new String[1000];
    private boolean _validationResult;
    private boolean _finalConclusion = true;
    private boolean[] _overAllValidationResult = new boolean[10000];
    private int _count_name_attr_array; 
    private int _count_cardid; 
    private int _count = 4; 
    private int a = 0;

    private File _fileFromMain = null;
    private SAXParserFactory _factory = null;
    private SAXParser _parser = null;
    private DocumentBuilderFactory _builderFactory = null;
    private DefaultHandler _defaultHandler = null;
    private DocumentBuilder _docBuilder = null;
    private Document _doc = null;
    private org.w3c.dom.Element _root = null;
    private static Logger _logger = Logger.getLogger(WMLValidator.class.getName());

    /**
     * WMLValidator : SAXValidation()
     * Starting point validation. Validation is of two parts, first DTD
     * validations
     * thru SAXParser, and second the functional validations described in the
     * WMLValidation document version 1.0. If in any of the validation items the
     * validation fails, it simply throws error to the ServiceWorker with the
     * 
     * @param String
     * @return String
     * @throws Exception
     */
    public String SAXValidation(String input) throws BTSLBaseException, Exception {
        final String methodName = "SAXValidation";
        String str = null;
        BufferedReader br = null;
        _validationResult = true;
        StringReader sr1 = null;
        FileReader fr = null;
        try {
        	if(_logger.isDebugEnabled()){
            _logger.debug("SAXValidation() :: Entering");
        	}
            initialize();
            _factory = SAXParserFactory.newInstance();
            _factory.setValidating(true); 
            _parser = _factory.newSAXParser();
            _defaultHandler = new MyErrorHandler();
            String filepath = Constants.getProperty("xmldtdfilepath");
            File _dtd = new File(filepath);
            fr = new FileReader(_dtd);
            br = new BufferedReader(fr);
            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String input_dtd = sb.toString();
            
            
            
            
            
            input = input.trim();
            a = input.indexOf("<?xml");

            if (a == 0) {
                int b = input.indexOf("<" + TagLibary.TAG_WML);
                int c = input.indexOf("</" + TagLibary.TAG_WML + ">");
                input = input.substring(b, c).trim();
                input += "</" + TagLibary.TAG_WML + ">";
            }
            String validation_input = input_dtd + "\n" + input;
            /*sr1 = new StringReader(validation_input);
            InputSource is1 = new InputSource(sr1);
            _parser.parse(is1, _defaultHandler);*/
            _builderFactory = DocumentBuilderFactory.newInstance();
            _docBuilder = _builderFactory.newDocumentBuilder();
            if(_logger.isDebugEnabled()){
            _logger.debug("SAXValidation() ::  validation_input :" + validation_input);
            }
            sr1 = new StringReader(validation_input);
            InputSource is1 = new InputSource(sr1);
            _doc = _docBuilder.parse(is1);
            _root = _doc.getDocumentElement();
            validateWML(_root);
            while (_count >= 0) {
                if (_overAllValidationResult[_count--] == true) {
                    _finalConclusion = false;
                    if(_logger.isDebugEnabled()){
                    	_logger.debug(" In if : " + _overAllValidationResult[_count]);
                    }
                    _logger.error("SAXValidation() :: Validation Error Caught in WMLValidator");
                    
                    
                    throw new BTSLBaseException(this, methodName, _resourceMap);
                }
            }
            input = "<?xml version=\"1.0\"?>" + input;
            /*sr1 = new StringReader(input);
            is1 = new InputSource(sr1);*/
            WebMultiLangParser wmlp = new WebMultiLangParser();
            str = wmlp.webByteGeneratorStr(input);
            if(_logger.isDebugEnabled()){
            _logger.debug("SAXValidation() :: After calling WebMultiLangParser.webByteGeneratorStr, Bytecode: " + str);
            }
            setParamsToNull();
        } catch (BaseException e) {
            _finalConclusion = false;
            _logger.error("SAXValidation() :: BaseException" + e.toString());
            _log.errorTrace(methodName, e);
            
            throw new BTSLBaseException(e.getMessage());

        } catch (ParserConfigurationException e) {
            _finalConclusion = false;
            _logger.error("SAXValidation() :: " + e.toString());

            
            
            
            throw new BTSLBaseException(e, methodName, _resourceMap);
        } catch (SAXException e) {
            _finalConclusion = false;
            _logger.error("SAXValidation() ::" + e.toString());
            _log.errorTrace(methodName, e);
            
            
            throw new BTSLBaseException(this, methodName, _resourceMap);
        } catch (Exception e) {
            _finalConclusion = false;
            _logger.error("SAXValidation() :: " + e.toString());
            _log.errorTrace(methodName, e);
            
            if (_resourceMap.isEmpty()) {
                
                throw new BTSLBaseException(this, methodName, _resourceMap);
            } else {
                
                
                throw new BTSLBaseException(this, methodName, _resourceMap);
            }
        } finally {
        	try{
        		if(sr1!=null){
        			sr1.close();	
        		}
        	}catch(Exception e){
        		 _log.errorTrace(methodName, e);
        	}
        	
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				_log.errorTrace(methodName, e);
			}
		}
        
        if(_logger.isDebugEnabled()){
        _logger.debug("SAXValidation() :: Exiting Cleanly");
        }
        return str;
    }

    /**
     * WMLValidator : setParamsToNull()
     * For Efficient memory utilization, setting all the
     * global vars to null. Called from SAXValidation().
     * 
     * @return void
     * @throws Exception
     */
    private void setParamsToNull() throws Exception {
        
        _global_cardid = null;
        _prev_elem_node = null;
        _name_attr_array = null;
        _cardid_attr_array = null;
        _overAllValidationResult = null;
    }

    /**
     * WMLValidator : initialize()
     * For Efficient memory utilization, initializing all the global vars first,
     * called from SAXValidation().
     * 
     * @return void
     * @throws Exception
     */
    private void initialize() throws Exception {
        
        _global_cardid = "";
        _name_attr_array = new String[10000];
        _cardid_attr_array = new String[1000];
        _validationResult = true;
        _finalConclusion = true;
        _overAllValidationResult = new boolean[10000];
        _count_name_attr_array = 0;
        _count_cardid = 0;
        _count = 4;
    }

    /**
     * WMLValidator:validateWML()
     * Validation of the wml tag and calling functional validations,
     * distinguishing by Node-Type, i.e. Text node or Element node. Checking the
     * null and duplicacy in a card by calling validate_Name_Null_Attr() here.
     * 
     * @param Node
     * @return void
     * @throws Exception
     */
    public void validateWML(Node start) {
        try {
           if(_logger.isDebugEnabled()){
            _logger.debug("validateWML() :: START = " + start);
           }
            if (start.getNodeName().equals(TagLibary.TAG_WML)) {
                _overAllValidationResult[3] = !(validate_WML_Tag(start));
                if(_logger.isDebugEnabled()){
                _logger.debug("validateWML() :: " + _overAllValidationResult[3] + "");
                }
            }
            for (Node child = start.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                	if(_logger.isDebugEnabled()){
                		_logger.debug("validateWML() :: " + child.getNodeName() + " :Before processing ELEMENT node: " + _overAllValidationResult[_count]);
                	}
                    _overAllValidationResult[_count++] = !(processElementNode(child));
                    _overAllValidationResult[_count++] = !(validate_Name_Null_Attr(child));
                } else if (child.getNodeType() == Node.TEXT_NODE) {
                	if(_logger.isDebugEnabled()){
                		_logger.debug("validateWML() :: " + child.getNodeName() + " :Before processing TEXT node: " + _overAllValidationResult[_count]);
                	}
                    _overAllValidationResult[_count++] = !(processTextNode(child));
                }
                if(_logger.isDebugEnabled()){
                	_logger.debug("validateWML() :: After Processing :" + _count + ": " + child.getNodeName() + " : " + _overAllValidationResult[_count]);
                }

                validateWML(child); 
            }
        } catch (Exception e) {
            _logger.error("validateWML() :: " + e.toString());
        }
    }

    /**
     * WMLValidator : processTextNode()
     * Validating #text values of btsl-send-sms tag. Accepts the Text Nodes from
     * the
     * validateWML() function. If in future more validations are required for
     * #text
     * values, make and call new functions for corresponding tags.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean processTextNode(Node processNode) {
        boolean flag = true;
        final String METHOD_NAME = "processTextNode";
        try { 
        	if(_logger.isDebugEnabled()){
            _logger.debug("processTextNode() :: Prev Node=" + _prev_elem_node.getNodeName() + "   NodeValue=" + processNode.getNodeValue());
        	}
            if ((_prev_elem_node.getNodeName().equals(TagLibary.TAG_BTSL_SEND_SMS) || _prev_elem_node.getNodeName().equals(TagLibary.TAG_BTSL_DECRYPT) || _prev_elem_node.getNodeName().equals(TagLibary.TAG_BTSL_ENCRYPT)) && !(("").equals(processNode.getNodeValue().trim()))) {
                String val = processNode.getNodeValue().trim();
                if (_prev_elem_node.getNodeName().equals(TagLibary.TAG_BTSL_SEND_SMS) && !(val.startsWith(TagLibary.VAL_DOL)) && val.length() > TagLibary.VAL_BTSL_SEND_SMS121) {
                	if(_logger.isDebugEnabled()){
                		_logger.debug("processTextNode() :: " + _prev_elem_node.getNodeName() + ":" + processNode.getNodeValue() + " data length can not be > 121" + processNode.toString());
                	}
                    
                    
                    String strArr[] = { _prev_elem_node.toString(), processNode.getNodeValue(), _global_cardid };
                    _resourceMap.put("ota.services.error.sendsms_gt121", strArr);
                    flag = false;
                }
                if (val.startsWith(TagLibary.VAL_DOL)) {
                    if (val.length() > TagLibary.VAL_BTSL_SEND_SMS255) {
                    	if(_logger.isDebugEnabled()){
                    		_logger.debug("processTextNode() :: " + _prev_elem_node.getNodeName() + ":" + processNode.getNodeValue() + " variable should start with $" + processNode.toString());
                    	}
                        
                        
                        String strArr[] = { _prev_elem_node.toString(), processNode.getNodeValue(), _global_cardid };
                        _resourceMap.put("ota.services.error.lengthexceeds", strArr);
                        flag = false;
                    }
                    if (val.length() < 2) {
                    	if(_logger.isDebugEnabled()){
                    		_logger.debug("processTextNode() :: " + _prev_elem_node.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                    	}
                        
                        
                        String strArr[] = { _prev_elem_node.toString(), _global_cardid };
                        _resourceMap.put("ota.services.error.novarnamegiven_dol_tag", strArr);

                        flag = false;
                    }
                    if (val.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("processTextNode() :: " + _prev_elem_node.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                    	}
                        
                        
                        String strArr[] = { _prev_elem_node.toString(), processNode.getNodeValue(), _global_cardid };
                        _resourceMap.put("ota.services.error.inbtw$_tag", strArr);

                        flag = false;
                    }
                    if (val.indexOf(" ") > 0) {
                    	if(_logger.isDebugEnabled()){
                    		_logger.debug("processTextNode() :: should not contain whitespaces after $" + processNode.toString());
                    	}
                        
                        
                        String strArr[] = { _prev_elem_node.toString(), processNode.getNodeValue(), _global_cardid };
                        _resourceMap.put("ota.services.error.whitespaces_tag", strArr);
                        flag = false;
                    }
                }
            }
        } catch (NullPointerException npe) {
            _log.errorTrace(METHOD_NAME, npe);
        } catch (Exception e) {
            _logger.error("processTextNode() :: " + e.toString());
        }
        return flag;
    }

    /**
     * WMLValidator : processElementNode()
     * Function to call the case specific function corresponding to the tag
     * passed to it.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean processElementNode(Node processNode) {
        String str = processNode.toString();
        final String METHOD_NAME = "processElementNode";
        if(_logger.isDebugEnabled()){
        _logger.debug("processElementNode() :: PROCESSNODE = " + str);
        }
        String name;
        try {
            name = processNode.getNodeName();
            _prev_elem_node = processNode; 
            
            if (name.equals(TagLibary.TAG_CARD)) {
                _validationResult = validate_Card_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_INPUT)) {
                _validationResult = validate_Input_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_VAR)) {
                _validationResult = validate_var_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_TEXT)) {
                _validationResult = validate_text_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_BTSL_SEND_SMS)) {
                _validationResult = validate_SendSMS_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_SELECT)) {
                _validationResult = validate_select_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_OPTION)) {
                _validationResult = validate_option_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_BTSL_IF_DO)) {
                _validationResult = validate_ifdo_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_GO)) {
                _validationResult = validate_go_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_BTSL_DISPLAYTEXT)) {
                _validationResult = validate_displaytext_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_SET)) {
                _validationResult = validate_set_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_BTSL_ENCRYPT)) {
                _validationResult = validate_Encrypt_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_BTSL_DECRYPT)) {
                _validationResult = validate_Decrypt_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_BTSL_DELETE)) {
                _validationResult = validate_Delete_Tag(processNode);
            }
            if (name.equals(TagLibary.TAG_BTSL_COMPARE)) {
                _validationResult = validate_Compare_Tag(processNode);
            }
            if(_logger.isDebugEnabled()){
            _logger.debug("processElementNode() :: " + name + " : " + _validationResult);
            }
        } catch (Exception e) {
            _logger.error("processElementNode() :: " + e.toString());
            _log.errorTrace(METHOD_NAME, e);
            
            
            String strArr[] = { processNode.toString(), _global_cardid, TagLibary.TAG_BTSL_SEND_SMS };
            _resourceMap.put("ota.services.error.varvaluenull", strArr);
            _validationResult = false;
        }
        return _validationResult;
    }

    /**
     * WMLValidator : validate_set_Tag()
     * functional validation for tag 'set'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_set_Tag(Node processNode) throws BTSLBaseException {
        boolean flag = true;
        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttrLength=startAttr.getLength();
            for (int i = 0; i < startAttrLength; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue();
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VAR)) {
                    if (!(attr_val.startsWith(TagLibary.VAL_DOL))) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_set_Tag() :: VAR should start with $" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.startswith$", strArr);
                        flag = false;
                        continue;
                    }
                    if (attr_val.length() < 2 && attr_val.startsWith(TagLibary.VAL_DOL)) {
                    	if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_set_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.novarnamegiven_dol", strArr);
                        flag = false;
                        continue;
                    }
                    if (attr_val.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                    	if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_set_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.inbtw$", strArr);
                        flag = false;
                    }
                    if (attr_val.indexOf(" ") > 0) {
                    	if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_set_Tag() :: should not contain whitespaces after $" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.whitespaces", strArr);
                        flag = false;
                    }
                }
            }
        } catch (Exception e) {
            _logger.error("validate_set_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_set_Tag", "");
        }
        return flag;
    }

    /**
     * WMLValidator : validate_ifdo_Tag()
     * functional validation for tag btsl-if-do.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_ifdo_Tag(Node processNode) throws BTSLBaseException {
        boolean flag = true;
        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttrLength=startAttr.getLength();
            for (int i = 0; i < startAttrLength; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue().trim();
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VAR) && !(attr_val.startsWith(TagLibary.VAL_DOL))) {
                	if(_logger.isDebugEnabled()){
                    _logger.debug("validate_ifdo_Tag() :: VAR should start with $" + processNode.toString());
                	} 
                    
                    String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                    _resourceMap.put("ota.services.error.startswith$", strArr);
                    flag = false;
                    continue;
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VAR) && attr_val.length() < 2 && attr_val.startsWith(TagLibary.VAL_DOL)) {
                	if(_logger.isDebugEnabled()){
                	_logger.debug("validate_ifdo_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                	}
                    
                    String strArr[] = { processNode.toString(), _global_cardid, attr.getNodeName().trim() };
                    _resourceMap.put("ota.services.error.novarnamegiven_dol", strArr);
                    flag = false;
                    continue;
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VAR) && attr_val.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_ifdo_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                	} 
                    
                    String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                    _resourceMap.put("ota.services.error.inbtw$", strArr);
                    flag = false;
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VAR) && attr_val.indexOf(" ") > 0) {
                	if(_logger.isDebugEnabled()){
                	_logger.debug("validate_ifdo_Tag() :: should not contain whitespaces after $" + processNode.toString());
                	}
                    
                    String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                    _resourceMap.put("ota.services.error.whitespaces", strArr);

                    flag = false;
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VALUE)) {
                    int val;
                    try {
                        val = Integer.parseInt(attr_val);
                        if (val > TagLibary.VAL_IFDO_VALUE_LEN) {
                        	if(_logger.isDebugEnabled()){
                        	_logger.debug("validate_ifdo_Tag() :: cardid should start with #" + processNode.toString());
                        	}
                            
                            String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                            _resourceMap.put("ota.services.error.ifdo_gt5", strArr);
                            flag = false;
                        }
                    } catch (NumberFormatException ne) {
                    	if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_ifdo_Tag() :: value" + ne + "" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.numfmtexp", strArr);

                        flag = false;
                    }
                }
            }
        } catch (Exception e) {
            _logger.error("validate_ifdo_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_ifdo_Tag", "");
        }
        return flag;
    }

    /**
     * WMLValidator : validate_go_Tag()
     * functional validation for tag go.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_go_Tag(Node processNode) throws BTSLBaseException {
        boolean flag = true;
        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttLength=startAttr.getLength();
            for (int i = 0; i <startAttLength ; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue();
                if (attr.getNodeName().trim().equals(TagLibary.ATT_HREF) && !("".equals(attr_val))) {
                    if (!(attr.getNodeValue().trim().startsWith(TagLibary.VAL_HASH))) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_go_Tag() :: cardid should start with #" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.startswithhash", strArr);
                        flag = false;
                        continue;
                    }
                    if (attr_val.length() < 2) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_go_Tag() :: Card reference name not given after hash" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.novarnamegiven_hash", strArr);
                        flag = false;
                        continue;
                    }
                    if (attr_val.lastIndexOf(TagLibary.VAL_HASH) > 0) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_go_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.inbtw#", strArr);
                        flag = false;
                        continue;
                    }
                    if (attr_val.indexOf(" ") > 0) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_go_Tag() :: should not contain whitespaces after $" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.whitespaces", strArr);
                        flag = false;
                        continue;
                    }
                    attr_val = attr_val.substring(1);
                    if (attr_val.length() > TagLibary.VAL_GO_HREF_LEN) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_go_Tag() :: href can max of two chars" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.go_gt2", strArr);
                        flag = false;
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            _logger.error("validate_go_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_go_Tag()", "");
        }
        return flag;
    }

    /**
     * WMLValidator : isLang1Lang2Null()
     * Functional validation for attributes btsl-lang1 and btsl-lang2.
     * Both cant be null.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean isLang1Lang2Null(Node processNode) {
        final String METHOD_NAME = "isLang1Lang2Null";
        boolean flag = true;
        try {
            Node attr = null;
            String lang1 = null, lang2 = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttrsLength=startAttr.getLength();
            for (int i = 0; i < startAttrsLength; i++) {
                attr = startAttr.item(i);
                String attr_name = attr.getNodeName().trim();
                if (attr_name.equals(TagLibary.ATT_BTSL_LANG1)) {
                    lang1 = attr.getNodeValue().trim();
                    continue;
                }
                if (attr_name.equals(TagLibary.ATT_BTSL_LANG2)) {
                    lang2 = attr.getNodeValue().trim();
                    continue;
                }
            }
            try {
                if (("").equals(lang1) && ("").equals(lang2)) {
                	if(_logger.isDebugEnabled()){
                    _logger.debug("isLang1Lang2Null() :: Validate_" + processNode.getNodeName() + "_Tag() :: btsl-lang1 & btsl-lang2 both cant be null" + processNode.toString());
                	}
                    
                    
                    String strArr[] = { processNode.toString(), _global_cardid };
                    _resourceMap.put("ota.services.error.lang1lang2null", strArr);
                    flag = false;
                }
            } catch (NullPointerException ne) {
            	if(_logger.isDebugEnabled()){
                _logger.debug("isLang1Lang2Null() :: Validate_" + processNode.getNodeName() + "_Tag():btsl-lang1 & btsl-lang2 not present");
            	}
                _log.errorTrace(METHOD_NAME, ne);
                if (processNode.getNodeName().equals(TagLibary.TAG_BTSL_DISPLAYTEXT)) {
                    
                    
                    String strArr[] = { processNode.toString(), _global_cardid };
                    _resourceMap.put("ota.services.error.lang1lang2notpresent", strArr);

                    flag = false;
                }
            }
        } catch (Exception e) {
            _logger.error("isLang1Lang2Null() :: " + e.toString());
        }
        return flag;
    }

    /**
     * WMLValidator : validate_option_Tag()
     * Functional validation for tag 'option'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_option_Tag(Node processNode) throws BTSLBaseException {
        boolean flag = true;
        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttrLength=startAttr.getLength();
            for (int i = 0; i <startAttrLength ; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue();
                if (attr.getNodeName().trim().equals(TagLibary.ATT_BTSL_LANG1) || attr.getNodeName().trim().equals(TagLibary.ATT_BTSL_LANG2)) {
                    continue;
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VALUE) && attr_val.length() > TagLibary.VAL_OPTION_VALUE_LEN) {
                    if(_logger.isDebugEnabled()){
                	_logger.debug("validate_option_Tag() :: Length Value > 20" + processNode.toString());
                    } 
                    
                    String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                    _resourceMap.put("ota.services.error.option_gt20", strArr);
                    flag = false;
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_ONPICK) && !(("").equals(attr_val))) {
                    if (!(attr.getNodeValue().trim().startsWith(TagLibary.VAL_HASH))) {
                    	 if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_option_Tag() :: cardid should start with #" + processNode.toString());
                    	 }
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.startswithhash", strArr);
                        flag = false;
                        continue;
                    }
                    if (attr_val.length() < 2) {
                    	 if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_option_Tag() :: Card reference name not given after hash" + processNode.toString());
                    	 }
                        
                        String strArr[] = { processNode.toString(), _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.novarnamegiven_hash", strArr);

                        flag = false;
                        continue;
                    }
                    if (attr_val.lastIndexOf(TagLibary.VAL_HASH) > 0) {
                    	 if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_option_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                    	 }
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.inbtw#", strArr);
                        flag = false;
                        continue;
                    }
                    if (attr_val.indexOf(" ") > 0) {
                    	 if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_option_Tag() :: should not contain whitespaces after $" + processNode.toString());
                    	 }
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.whitespaces", strArr);
                        flag = false;
                        continue;
                    }
                    attr_val = attr_val.substring(1);
                    if (attr_val.length() > TagLibary.VAL_OPTION_ONPICK_LEN) {
                    	 if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_option_Tag() :: onpick can be max of two chars" + processNode.toString());
                    	 }
                        
                        String strArr[] = { processNode.toString(), _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.go_gt2", strArr);
                        flag = false;
                        continue;
                    }
                }
            }
            if(_logger.isDebugEnabled()){
            _logger.debug("validate_option_Tag() :: " + processNode.toString());
            }
            if (isLang1Lang2Null(processNode) == false) {
                flag = false;
            }
        } catch (Exception e) {
            _logger.error("validate_option_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_option_Tag", "");
        }
        return flag;
    }

    /**
     * WMLValidator:validate_displaytext_Tag()
     * Functional validation for tag 'btsl-displaytext'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_displaytext_Tag(Node processNode) throws BTSLBaseException {
        boolean flag = true;
        try {
            Node attr = null;
            String var = "", lang1 = "", lang2 = "";
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttrsLength=startAttr.getLength();
            for (int i = 0; i < startAttrsLength; i++) {
                attr = startAttr.item(i);

                if (attr.getNodeName().trim().equals(TagLibary.ATT_VAR)) {
                    var = attr.getNodeValue();
                } else if (attr.getNodeName().trim().equals(TagLibary.ATT_BTSL_LANG1)) {
                    lang1 = attr.getNodeValue();
                } else if (attr.getNodeName().trim().equals(TagLibary.ATT_BTSL_LANG2)) {
                    lang2 = attr.getNodeValue();
                }
            }
            if (("").equals(var)) {
                if (isLang1Lang2Null(processNode) == false) {
                    return flag = false;
                }
                flag = hexValidator(lang2, processNode);
            }
            if (!(("").equals(var))) {
                if (!("".equals(lang1)) || !("".equals(lang2))) {
                	if(_logger.isDebugEnabled()){
                    _logger.debug("validate_displaytext_Tag() :: VAR(Lang1/Lang2)" + processNode.toString());
                	}
                    
                    String strArr[] = { processNode.toString(), var, _global_cardid, TagLibary.ATT_VAR };
                    _resourceMap.put("ota.services.error.varlang1lang2", strArr);
                    return flag = false;
                }
                if (!(var.startsWith(TagLibary.VAL_DOL))) {
                	if(_logger.isDebugEnabled()){
                    _logger.debug("validate_displaytext_Tag() :: VAR should start with $" + processNode.toString());
                	}
                    
                    String strArr[] = { processNode.toString(), var, _global_cardid, TagLibary.ATT_VAR };
                    _resourceMap.put("ota.services.error.startswith$", strArr);
                    return flag = false;
                }
                if (var.length() < 2 && var.startsWith(TagLibary.VAL_DOL)) {
                	if(_logger.isDebugEnabled()){
                    _logger.debug("validate_displaytext_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                	} 
                    
                    String strArr[] = { processNode.toString(), var, _global_cardid, TagLibary.ATT_VAR };
                    _resourceMap.put("ota.services.error.novarnamegiven_dol", strArr);
                    return flag = false;
                }
                if (var.indexOf(" ") > 0) {
                	if(_logger.isDebugEnabled()){
                    _logger.debug("validate_displaytext_Tag() :: should not contain whitespaces after $" + processNode.toString());
                	}
                    
                    String strArr[] = { processNode.toString(), var, _global_cardid, TagLibary.ATT_VAR };
                    _resourceMap.put("ota.services.error.whitespaces", strArr);
                    return flag = false;
                }
                if (var.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                	if(_logger.isDebugEnabled()){
                    _logger.debug("validate_displaytext_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                	}
                    
                    String strArr[] = { processNode.toString(), var, _global_cardid, TagLibary.ATT_VAR };
                    _resourceMap.put("ota.services.error.inbtw$", strArr);
                    return flag = false;
                }
            }
        } catch (Exception e) {
            _logger.error("validate_displaytext_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_displaytext_Tag", "");
        }
        return flag;
    }

    /**
     * WMLValidator:validate_select_Tag()
     * Functional validation for tag 'select'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_select_Tag(Node processNode) throws BTSLBaseException {
        try {
            return isLang1Lang2Null(processNode);
        } catch (Exception e) {
            _logger.error("validate_select_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_select_Tag", "");
        }
    }

    /**
     * WMLValidator:validate_Encrypt_Tag()
     * Functional validation for tag 'btsl-encrypt'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_Encrypt_Tag(Node processNode) throws BTSLBaseException {
        final String METHOD_NAME = "validate_Encrypt_Tag";
        boolean flag = true;
        if(_logger.isDebugEnabled()){
        _logger.debug("validate_Encrypt_Tag :: " + processNode.toString() + "  Value=" + processNode.getNodeValue());
        }try {
            String nodeValue = processNode.getFirstChild().toString().trim();
            if (nodeValue == null || ("").equals(nodeValue)) {
            	 if(_logger.isDebugEnabled()){
                _logger.debug("validate_Encrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " no data " + processNode.toString());
            	 }
                
                String strArr[] = { processNode.toString(), processNode.getNodeValue(), _global_cardid };
                _resourceMap.put("ota.services.error.varvaluenull", strArr);
                flag = false;
            }
            if (nodeValue.startsWith(TagLibary.VAL_DOL)) {
                if (nodeValue.length() > TagLibary.VAL_BTSL_SEND_SMS255) {
                	 if(_logger.isDebugEnabled()){
                    _logger.debug("validate_Encrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " variable should start with $" + processNode.toString());
                	 }
                    
                    String strArr[] = { processNode.toString(), processNode.getNodeValue(), _global_cardid };
                    _resourceMap.put("ota.services.error.lengthexceeds", strArr);
                    flag = false;
                }
                if (nodeValue.length() < 2) {
                	 if(_logger.isDebugEnabled()){
                    _logger.debug("validate_Encrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                	 }
                    
                    String strArr[] = { processNode.toString(), _global_cardid };
                    _resourceMap.put("ota.services.error.novarnamegiven_dol_tag", strArr);
                    flag = false;
                }
                if (nodeValue.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                	 if(_logger.isDebugEnabled()){
                    _logger.debug("validate_Encrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                	 }
                    
                    String strArr[] = { processNode.toString(), _global_cardid };
                    _resourceMap.put("ota.services.error.inbtw$_tag", strArr);
                    flag = false;
                }
                if (nodeValue.indexOf(" ") > 0) {
                	 if(_logger.isDebugEnabled()){
                    _logger.debug("validate_Encrypt_Tag() :: should not contain whitespaces after $" + processNode.toString());
                	 }
                    
                    String strArr[] = { processNode.toString(), _global_cardid };
                    _resourceMap.put("ota.services.error.whitespaces_tag", strArr);
                    flag = false;
                }
            }
        } catch (NullPointerException e) {
            _log.errorTrace(METHOD_NAME, e);
            if(_logger.isDebugEnabled()){
            _logger.debug("validate_Encrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " no data " + processNode.toString());
            }
            
            String strArr[] = { processNode.toString(), _global_cardid };
            _resourceMap.put("ota.services.error.varvaluenull", strArr);

            flag = false;
        } catch (Exception e) {
            _logger.error("validate_Encrypt_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", METHOD_NAME, "");
        }
        return flag;
    } 

    /**
     * WMLValidator: validate_Decrypt_Tag()
     * Functional validation for tag 'btsl-decrypt'.
     * 
     * @param Node
     * @return boolean
     * @throws BTSLBaseException 
     * @throws Exception
     */
    private boolean validate_Decrypt_Tag(Node processNode) throws BTSLBaseException {
        final String METHOD_NAME = "validate_Decrypt_Tag";
        boolean flag = true;
       if(_logger.isDebugEnabled()){
        _logger.debug("validate_Decrypt_Tag :: " + processNode.toString() + "  Value=" + processNode.getNodeValue());
       }  try {
            String nodeValue = processNode.getFirstChild().toString().trim();
            if (nodeValue == null || ("").equals(nodeValue)) {
            	   if(_logger.isDebugEnabled()){
                _logger.debug("validate_Decrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " no data " + processNode.toString());
            	   } 
                
                String strArr[] = { processNode.toString(), _global_cardid };
                _resourceMap.put("ota.services.error.varvaluenull", strArr);
                flag = false;
            }
            if (nodeValue.startsWith(TagLibary.VAL_DOL)) {
                if (nodeValue.length() > TagLibary.VAL_BTSL_SEND_SMS255) {
                	   if(_logger.isDebugEnabled()){
                    _logger.debug("validate_Decrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " variable should start with $" + processNode.toString());
                	   }
                    
                    String strArr[] = { processNode.toString(), _global_cardid };
                    _resourceMap.put("ota.services.error.lengthexceeds", strArr);
                    flag = false;
                }
                if (nodeValue.length() < 2) {
                	   if(_logger.isDebugEnabled()){
                    _logger.debug("validate_Decrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                	   } 
                    
                    String strArr[] = { processNode.toString(), _global_cardid };
                    _resourceMap.put("ota.services.error.novarnamegiven_dol_tag", strArr);
                    flag = false;
                }
                if (nodeValue.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                	   if(_logger.isDebugEnabled()){
                    _logger.debug("validate_Decrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                	   }
                    
                    String strArr[] = { processNode.toString(), _global_cardid };
                    _resourceMap.put("ota.services.error.inbtw$_tag", strArr);
                    flag = false;
                }
                if (nodeValue.indexOf(" ") > 0) {
                	   if(_logger.isDebugEnabled()){
                    _logger.debug("validate_Decrypt_Tag() :: should not contain whitespaces after $" + processNode.toString());
                	   }
                    
                    String strArr[] = { processNode.toString(), _global_cardid };
                    _resourceMap.put("ota.services.error.whitespaces_tag", strArr);
                    flag = false;
                }
            }
        } catch (NullPointerException e) {
            _log.errorTrace(METHOD_NAME, e);
            if(_logger.isDebugEnabled()){
            _logger.debug("validate_Decrypt_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " no data " + processNode.toString());
            }
            
            String strArr[] = { processNode.toString(), _global_cardid };
            _resourceMap.put("ota.services.error.varvaluenull", strArr);
            flag = false;
        } catch (Exception e) {
            _logger.error("validate_Decrypt_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", METHOD_NAME, "");
        }
        return flag;
    }

    /**
     * WMLValidator:validate_SendSMS_Tag()
     * Functional validation for tag 'btsl-send-sms'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_SendSMS_Tag(Node processNode) throws BTSLBaseException {
        final String METHOD_NAME = "validate_SendSMS_Tag";
        boolean flag = true;
        if(_logger.isDebugEnabled()){
        	_logger.debug("validate_SendSMS_Tag() :: " + processNode.toString() + "  Value=" + processNode.getNodeValue() + " CHILD=" + processNode.getFirstChild().getNodeValue());
        }
        try {
            String nodeValue = processNode.getFirstChild().toString().trim();
            if (nodeValue == null || ("").equals(nodeValue.trim())) {
            	if(_logger.isDebugEnabled()){
                _logger.debug("validate_SendSMS_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " no data " + processNode.toString());
            	}
                
                String strArr[] = { processNode.toString(), _global_cardid };
                _resourceMap.put("ota.services.error.varvaluenull", strArr);
                flag = false;
            }
        } catch (NullPointerException e) {
            _log.errorTrace(METHOD_NAME, e);
            if(_logger.isDebugEnabled()){
            _logger.debug("validate_SendSMS_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " no data " + processNode.toString());
            }
            
            String strArr[] = { processNode.toString(), _global_cardid };
            _resourceMap.put("ota.services.error.varvaluenull", strArr);
            flag = false;
        }

        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            if ((flag = isLang1Lang2Null(processNode)) == false) {
                flag = false;
            }
            int startAttrsLength=startAttr.getLength();
            for (int i = 0; i < startAttrsLength; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue().trim();
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VP) && !(("").equals(attr_val))) {
                    if (attr_val.length() > TagLibary.VAL_SendSMS_VP_LEN) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_SendSMS_Tag() :: vp length exceeds" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.sendsms_gt2", strArr);
                        flag = false;
                        continue;
                    }
                    flag = hexValidator(attr_val, processNode);
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_PID) && !(("").equals(attr_val))) {
                    if (attr_val.length() > TagLibary.VAL_SendSMS_PID_LEN) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_SendSMS_Tag() :: pid length exceeds" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.sendsms_gt2", strArr);
                        flag = false;
                        continue;
                    }
                    flag = hexValidator(attr_val, processNode);
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_SMSC) && !("".equals(attr_val))) {
                    if (attr_val.length() > TagLibary.VAL_SendSMS_SMSC_LEN) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_SendSMS_Tag() :: smsc length exceeds" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.sendsms_gt2", strArr);
                        flag = false;
                    }
                    if (!(attr_val.equals(TagLibary.ATTVAL_SMSC1) || attr_val.equals(TagLibary.ATTVAL_SMSC2) || attr_val.equals(TagLibary.ATTVAL_SMSC3))) {
                        try {
                        	_logger.info("Inside try block");
                        } catch (NumberFormatException ne) {
                        	if(_logger.isDebugEnabled()){
                            _logger.debug("validate_SendSMS_Tag() :: smsc " + ne + "" + processNode.toString());
                        	}
                            
                            String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                            _resourceMap.put("ota.services.error.numfmtexp", strArr);
                            flag = false;
                        }
                    } 
                    continue;
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_DEST) && !(("").equals(attr_val))) {
                    if (attr_val.length() > TagLibary.VAL_SendSMS_DEST_LEN) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_SendSMS_Tag() :: dest length exceeds" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.sendsms_gt13", strArr);
                        flag = false;
                        continue;
                    }
                    if (!(attr_val.equals(TagLibary.ATTVAL_dest1) || attr_val.equals(TagLibary.ATTVAL_dest2) || attr_val.equals(TagLibary.ATTVAL_dest3))) {
                        try {
                            double smsc = Double.parseDouble(attr_val);
                        } catch (NumberFormatException ne) {
                        	if(_logger.isDebugEnabled()){
                            _logger.debug("validate_SendSMS_Tag() :: dest " + ne + "" + processNode.toString());
                        	}
                            
                            String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                            _resourceMap.put("ota.services.error.numfmtexp", strArr);
                            flag = false;
                        }
                    }
                    continue;
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VP) && !(("").equals(attr_val))) {
                    try {
                    	 _logger.info("inside try block");
                    } catch (NumberFormatException ne) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_SendSMS_Tag() :: vp " + ne + "" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.numfmtexp", strArr);
                        flag = false;
                    }
                    continue;
                }
            }
        } catch (Exception e) {
            _logger.error("validate_SendSMS_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", METHOD_NAME, "");
        }
        return flag;
    }

    /**
     * WMLValidator:validate_text_Tag()
     * Functional validation for tag 'text'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_text_Tag(Node processNode) throws BTSLBaseException {
        try {
            return isLang1Lang2Null(processNode);
        } catch (Exception e) {
            _logger.error("validate_text_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_text_Tag", "");
        }
    }

    /**
     * WMLValidator:validate_var_Tag()
     * Functional validation for tag 'var'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_var_Tag(Node processNode) throws BTSLBaseException {
        boolean flag = true;
        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttrLength=startAttr.getLength();
            for (int i = 0; i <startAttrLength ; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue().trim();
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VALUE) && !(("").equals(attr_val))) {
                    if (!attr_val.startsWith(TagLibary.VAL_DOL)) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_var_Tag() :: should start with $" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.startswith$", strArr);
                        flag = false;
                        continue;
                    } else if (attr_val.length() < 2 && attr_val.startsWith(TagLibary.VAL_DOL)) {
                    	if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_var_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.novarnamegiven_dol", strArr);
                        flag = false;
                    } else if (attr_val.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                    	if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_var_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.inbtw$", strArr);
                        flag = false;
                    } else if (attr_val.startsWith(TagLibary.VAL_DOL) && attr_val.indexOf(" ") > 0) {
                    	if(_logger.isDebugEnabled()){
                    	_logger.debug("validate_var_Tag() :: should not contain whitespaces after $" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.whitespaces", strArr);
                        flag = false;
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            _logger.error("validate_var_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_var_Tag", "");
        }
        return flag;
    }

    /**
     * WMLValidator:validate_Delete_Tag()
     * Functional validation for tag 'btsl-delete'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_Delete_Tag(Node processNode) throws BTSLBaseException {
        boolean flag = true;
        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
           int startAttrLength= startAttr.getLength();
            for (int i = 0; i < startAttrLength; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue().trim();
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VAR) && !(("").equals(attr_val))) {
                    if (!attr_val.startsWith(TagLibary.VAL_DOL)) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Delete_Tag() :: should start with $" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.startswith$", strArr);
                        flag = false;
                        continue;
                    } else if (attr_val.length() < 2 && attr_val.startsWith(TagLibary.VAL_DOL)) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Delete_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.novarnamegiven_dol", strArr);
                        flag = false;
                    } else if (attr_val.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Delete_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.inbtw$", strArr);
                        flag = false;
                    } else if (attr_val.startsWith(TagLibary.VAL_DOL) && attr_val.indexOf(" ") > 0) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Delete_Tag() :: should not contain whitespaces after $" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.whitespaces", strArr);
                        flag = false;
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            _logger.error("validate_Delete_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_Delete_Tag", "");
        }
        return flag;
    }

    /**
     * WMLValidator:validate_Input_Tag()
     * Functional validation for tag 'input'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_Input_Tag(Node processNode) throws BTSLBaseException {
        boolean flag = true;
        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int maxlength = 0, minlength = 0;
            String lang1 , lang2 ;
            int startAttrLength=startAttr.getLength();
            for (int i = 0; i < startAttrLength; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue().trim();
                String attr_name = attr.getNodeName().trim();
                if (attr_name.equals(TagLibary.ATT_BTSL_MINLENGTH) && !(("").equals(attr_val))) {
                    try {
                        minlength = Integer.parseInt(attr_val);
                        if (minlength < TagLibary.VAL_INPUT_MINLEN) {
                        	if(_logger.isDebugEnabled()){
                            _logger.debug("validate_Input_Tag() :: btsl-minlength out of range" + processNode.toString());
                        	} 
                            
                            String strArr[] = { "<" + processNode.toString() + "/>", attr_val, _global_cardid, attr.getNodeName().trim() };
                            _resourceMap.put("ota.services.error.outofrange", strArr);
                            flag = false;
                        }
                    } catch (NumberFormatException ne) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Input_Tag() :: btsl-minlength" + ne + "" + processNode.toString());
                    	} 
                        
                        String strArr[] = { "<" + processNode.toString() + "/>", attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.numfmtexp", strArr);
                        flag = false;
                    }
                    continue;
                }
                if (attr_name.equals(TagLibary.ATT_MAXLENGTH) && !(("").equals(attr_val))) {
                    try {
                        maxlength = Integer.parseInt(attr_val);
                        if (maxlength < TagLibary.VAL_INPUT_MAXLEN_L || maxlength > TagLibary.VAL_INPUT_MAXLEN_U) {
                        	if(_logger.isDebugEnabled()){
                            _logger.debug("validate_Input_Tag() :: maxlength out of range" + processNode.toString());
                        	}
                            
                            String strArr[] = { "<" + processNode.toString() + "/>", attr_val, _global_cardid, attr.getNodeName().trim() };
                            _resourceMap.put("ota.services.error.outofrange", strArr);
                            flag = false;
                        }
                    } catch (NumberFormatException ne) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Input_Tag() :: maxlength" + ne + "" + processNode.toString());
                    	}
                        
                        String strArr[] = { "<" + processNode.toString() + "/>", attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.numfmtexp", strArr);
                        flag = false;
                    }
                    continue;
                }
            }
            System.out.println("flag =" + flag);
            if (flag == true && ((minlength > maxlength) && maxlength != 0)) {
            	if(_logger.isDebugEnabled()){
                _logger.debug("validate_Input_Tag() :: MAX<=MIN" + processNode.toString());
            	}  
                
                String strArr[] = { "<" + processNode.toString() + "/>", _global_cardid, attr.getNodeName().trim() };
                _resourceMap.put("ota.services.error.maxgtmin", strArr);
                flag = false;
            }
            if (isLang1Lang2Null(processNode) == false) {
                flag = false;
            }
        } catch (Exception e) {
            _logger.error("validate_Input_Tag() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_Input_Tag", "");
        }
        return flag;
    }

    /**
     * WMLValidator:validate_Card_Tag()
     * Functional validation for tag 'card'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_Card_Tag(Node processNode) {
        boolean flag = true;
        try {
            _name_attr_array = new String[10000];
            _count_name_attr_array = 0;
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttrLength=startAttr.getLength();
            for (int i = 0; i <startAttrLength ; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue().trim();
                if(_logger.isDebugEnabled()){
                _logger.debug("validate_Card_Tag() :: isDuplicateCardId : " + isDuplicateCardId(attr_val));
                }
                if (attr.getNodeName().equals(TagLibary.ATT_ID) && isDuplicateCardId(attr_val)) {
                    _logger.error("validate_Card_Tag() :: Duplicate card id<" + processNode.getNodeName() + " />," + attr_val + "," + _global_cardid + "|");
                    
                    
                    String strArr[] = { "<" + processNode.getNodeName() + "/>," + attr_val, _global_cardid, attr.getNodeName().trim() };
                    _resourceMap.put("ota.services.error.dupcardid", strArr);
                    flag = false;
                } else {
                    _cardid_attr_array[_count_cardid] = attr.getNodeValue().trim();
                    _count_cardid++;
                }
                if (attr.getNodeName().equals(TagLibary.ATT_ID)) {
                    _global_cardid = attr_val;
                    if (attr_val.length() > TagLibary.VAL_CARD_ID_LEN && !(("").equals(attr_val))) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Card_Tag() :: id can be max of two chars" + processNode.toString());
                    	}
                        
                        
                        String strArr[] = { "<" + processNode.getNodeName() + "/>", attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.card_gt2", strArr);
                        flag = false;
                    }
                }
                /*
                 * if (attr.getNodeName().equals(TagLibary.ATT_ID) &&
                 * attr_val.equals("") )
                 * {
                 * _logger.debug("validate_Card_Tag() :: id cant be null"+
                 * processNode.toString())
                 * _validationResourceString +=
                 * "ota.services.error.cardidnull,"+
                 * processNode.toString()+","+attr_val+","+_global_cardid+"|"
                 * flag = false
                 * }
                 */
            }
        } catch (Exception e) {
            _logger.error("validate_Card_Tag() :: " + e.toString());
        }
        return flag;
    }

    /**
     * WMLValidator:validate_WML_Tag()
     * Functional validation for tag 'wml'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_WML_Tag(Node processNode) {
        boolean flag = true;
        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttrsLength=startAttr.getLength();
            for (int i = 0; i < startAttrsLength; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue().trim();
                if (attr.getNodeName().equals(TagLibary.ATT_NAME) && ("".equals(attr_val) || attr_val.length() > TagLibary.VAL_WML_LEN)) {
                   if(_logger.isDebugEnabled()){
                	_logger.debug("validate_WML_Tag() :: name cant be null" + processNode.toString());
                   }
                    
                    String strArr[] = { "<" + processNode.getNodeName() + "/>", attr_val, attr.getNodeName().trim() };
                    _resourceMap.put("ota.services.error.wmlnamenull", strArr);
                    flag = false;
                }
            }
        } catch (Exception e) {
            _logger.error("validate_WML_Tag() :: " + e.toString());
        }
        return flag;
    }

    /**
     * WMLValidator:validate_Compare_Tag()
     * Functional validation for tag 'btsl-compare'.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_Compare_Tag(Node processNode) {
        boolean flag = true;
        String var1 = "", var2 = "";
        try {
            Node attr = null;
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            if (isLang1Lang2Null(processNode) == false) {
                flag = false;
            }
            int startAttrLength=startAttr.getLength();
            for (int i = 0; i < startAttrLength; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue().trim();
                /*
                 * if
                 * (attr.getNodeName().trim().equals(TagLibary.ATT_BTSL_LANG1)
                 * && !(attr_val.equals("")))
                 * {
                 * if (attr_val.length() > TagLibary.VAL_COMPARE_LANG1 )
                 * {
                 * _logger.debug("validate_Compare_Tag() :: "+processNode.
                 * getNodeName
                 * ()+":"+processNode.getNodeValue()+" length exceeds "
                 * +processNode.toString());
                 * _validationResourceString +=
                 * "ota.services.error.compare_gt50,"
                 * +processNode.toString()+","+attr_val+","+_global_cardid+"|"
                 * flag = false
                 * }
                 * }
                 */if ((attr.getNodeName().trim().equals(TagLibary.ATT_BTSL_LANG1) || attr.getNodeName().trim().equals(TagLibary.ATT_BTSL_LANG2)) && "".equals(attr_val)) {
                    if(_logger.isDebugEnabled()){
                	 _logger.debug("validate_Compare_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " value null " + processNode.toString());
                    }
                	 
                    
                    String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                    _resourceMap.put("ota.services.error.varvaluenull", strArr);
                    flag = false;
                }

                /*
                 * if
                 * (attr.getNodeName().trim().equals(TagLibary.ATT_BTSL_LANG2)
                 * && !(attr_val.equals("")))
                 * {
                 * if (attr_val.length() > TagLibary.VAL_COMPARE_LANG2 )
                 * {
                 * _logger.debug("validate_Compare_Tag() :: "+processNode.
                 * getNodeName
                 * ()+":"+processNode.getNodeValue()+" length exceeds "
                 * +processNode.toString())
                 * _validationResourceString +=
                 * "ota.services.error.compare_gt100,"
                 * +processNode.toString()+","+attr_val+","+_global_cardid+"|";
                 * flag = false
                 * }
                 * }
                 */if (attr.getNodeName().trim().equals(TagLibary.ATT_VAR1) && !("".equals(attr_val))) {
                    if (!attr_val.startsWith(TagLibary.VAL_DOL)) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Compare_Tag() :: should start with $" + processNode.toString());
                    	}
                        
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.startswith$", strArr);
                        flag = false;
                    } else if (attr_val.length() < 2 && attr_val.startsWith(TagLibary.VAL_DOL)) {
                    	 if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Compare_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                    	 }
                        
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.novarnamegiven_dol", strArr);
                        flag = false;
                    } else if (attr_val.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Compare_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.inbtw$", strArr);
                        flag = false;
                    } else if (attr_val.startsWith(TagLibary.VAL_DOL) && attr_val.indexOf(" ") > 0) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Compare_Tag() :: should not contain whitespaces after $" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.whitespaces", strArr);
                        flag = false;
                    }
                    var1 = attr_val.substring(1);
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_VAR2) && !("".equals(attr_val))) {
                    if (!attr_val.startsWith(TagLibary.VAL_DOL)) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Compare_Tag() :: should start with $" + processNode.toString());
                    	} 
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.startswith$", strArr);
                        flag = false;
                        continue;
                    } else if (attr_val.length() < 2 && attr_val.startsWith(TagLibary.VAL_DOL)) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Compare_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " Variable name not given after dollar" + processNode.toString());
                        
                    	}
                        String strArr[] = { processNode.toString(), _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.novarnamegiven_dol", strArr);
                        flag = false;
                    } else if (attr_val.lastIndexOf(TagLibary.VAL_DOL) > 0) {
                    	if(_logger.isDebugEnabled()){
                        _logger.debug("validate_Compare_Tag() :: " + processNode.getNodeName() + ":" + processNode.getNodeValue() + " should not contain $ in between variable name" + processNode.toString());
                    	}
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.inbtw$", strArr);
                        flag = false;
                    } else if (attr_val.startsWith(TagLibary.VAL_DOL) && attr_val.indexOf(" ") > 0) {
                    	if(_logger.isDebugEnabled()){
                    		_logger.debug("validate_Compare_Tag() :: should not contain whitespaces after $" + processNode.toString());
                    	}
                        
                        
                        String strArr[] = { processNode.toString(), attr_val, _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.whitespaces", strArr);
                        flag = false;
                    }
                    var2 = attr_val.substring(1);
                }
            }
            if (var1.equals(var2)) {
            	if(_logger.isDebugEnabled()){
                _logger.debug("validate_var_Tag() :: var1 can not be equal to var2" + processNode.toString());
            	}
                
                String strArr[] = { processNode.toString(), var1, var2, _global_cardid };
                _resourceMap.put("ota.services.error.var1var2equal", strArr);
                flag = false;
            }
        } catch (Exception e) {
            _logger.error("validate_var_Tag() :: " + e.toString());
        }
        return flag;
    }

    /**
     * WMLValidator:validate_Name_Null_Attr()
     * for validating the uniqueness of name attribute inside a deck of cards.
     * And handling the not-null checks on the attributes.
     * 
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean validate_Name_Null_Attr(Node processNode) throws BTSLBaseException {
        boolean flag = true;
        try {
            Node attr = null;
            if (processNode.getNodeName().equals(TagLibary.TAG_BTSL_DISPLAYTEXT)) {
                return flag;
            }
            org.w3c.dom.NamedNodeMap startAttr = processNode.getAttributes();
            int startAttrLength=startAttr.getLength();
            for (int i = 0; i < startAttrLength; i++) {
                attr = startAttr.item(i);
                String attr_val = attr.getNodeValue().trim();
                if ("".equals(attr_val) && !(attr.getNodeName().equals(TagLibary.ATT_BTSL_LANG1) || attr.getNodeName().equals(TagLibary.ATT_BTSL_LANG2))) {
                   if(_logger.isDebugEnabled()){
                	_logger.debug("validate_Name_Null_Attr() :: Attribute can not be null " + processNode.toString());
                   }
                    if (processNode.getNodeName().equals(TagLibary.TAG_CARD)) {
                        
                        
                        String strArr[] = { "<" + processNode.getNodeName() + ">", _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.attr_null", strArr);
                    } else {
                        
                        
                        String strArr[] = { processNode.toString(), _global_cardid, attr.getNodeName().trim() };
                        _resourceMap.put("ota.services.error.attr_null", strArr);
                    }
                    flag = false;
                    continue;
                }
                if (attr.getNodeName().trim().equals(TagLibary.ATT_BTSL_LANG2) && !("".equals(attr_val))) {
                    if (hexValidator(attr_val, processNode) == false) {
                        flag = false;
                    }
                    continue;
                }
                if (attr.getNodeName().equals(TagLibary.ATT_NAME) && isDuplicateInCard(attr_val)) {
                	 if(_logger.isDebugEnabled()){
                    _logger.debug("validate_Name_Null_Attr() :: Duplicate name attribute in card" + processNode.toString());
                	 } 
                    
                    String strArr[] = { processNode.toString() };
                    _resourceMap.put("ota.services.error.dupnameattr", strArr);
                    flag = false;
                    continue;
                } else if (attr.getNodeName().equals(TagLibary.ATT_NAME)) {
                    _name_attr_array[_count_name_attr_array] = attr.getNodeValue().trim();
                    _count_name_attr_array++;
                }
            }
        } catch (Exception e) {
            _logger.error("validate_Name_Null_Attr() :: " + e.toString());
            throw new BTSLBaseException("WMLValidator", "validate_Name_Null_Attr", "");
        }
        return flag;
    }

    /**
     * WMLValidator:isDuplicateInCard()
     * checking the duplicacy of name attribute from the global array
     * 
     * @param String
     * @return boolean
     * @throws Exception
     */
    private boolean isDuplicateInCard(String attr_val) {
        try {
            int j = 0;
            while (_name_attr_array[j] != null) {
                if (_name_attr_array[j++].equals(attr_val)) {
                    return true;
                }
            }
        } catch (Exception e) {
            _logger.error("isDuplicateInCard() :: " + e.toString());
        }
        return false;
    }

    /**
     * WMLValidator:isDuplicateCardId()
     * checking the duplicacy of card id from the global array
     * 
     * @param String
     * @return boolean
     * @throws Exception
     */
    private boolean isDuplicateCardId(String attr_val){
        try {
            int j = 0;
            if(_logger.isDebugEnabled()){
            _logger.debug("isDuplicateCardId() :: " + attr_val);
            }
            while (_cardid_attr_array[j] != null) {
                if (_cardid_attr_array[j++].equals(attr_val)) {
                    return true;
                }
            }
        } catch (Exception e) {
            _logger.error("isDuplicateCardId() :: " + e.toString());
        }
        return false;
    }

    /**
     * WMLValidator:hexValidator()
     * checking the hex input.
     * 
     * @param String
     * @param Node
     * @return boolean
     * @throws Exception
     */
    private boolean hexValidator(String attr_val, Node processNode) {
        boolean flag = true;
        try {
            int length = attr_val.length();
            char arr[] = attr_val.toCharArray();
            for (int i = 0; i < length; i++) {
                if (arr[i] == 32 || (arr[i] >= (char) 48 && arr[i] <= (char) 57) || (arr[i] >= (char) 65 && arr[i] <= (char) 70) || (arr[i] >= (char) 97 && arr[i] <= (char) 102)) {
                    continue;
                } else {
                    flag = false;
                }
            } 
            if (flag == false) {
            	if(_logger.isDebugEnabled()){
            		_logger.debug("hexValidator() :: " + attr_val);
            	}
                
                
                String strArr[] = { processNode.toString(), attr_val, _global_cardid };
                _resourceMap.put("ota.services.error.nothex", strArr);

            }
        } catch (Exception e) {
            _logger.error("hexValidator() :: " + e.toString());
        }
        return flag;
    }

    /**
     * WMLValidator : MyErrorHandler
     * inner class for SAXValidationExceptions. Extends DefaultHandler.
     * to customize the error strings according to the Base Exception.
     */
    private class MyErrorHandler extends DefaultHandler {

        /**
         * WMLValidator : MyErrorHandler :: warning()
         * Overriding to get error string in _validationResourceString.
         * 
         * @param SAXException
         * @throws SAXException
         */
        public void warning(SAXParseException e) throws SAXException {
            _overAllValidationResult[0] = true;
            _finalConclusion = false;
            int x = e.getLineNumber();
            if (a != 0) {
                x--;
            }
            _logger.warn("warning() :: Line number: " + x + " Message: " + e.getMessage());
            
            
            String strArr[] = { "" + x, e.getMessage() };
            _resourceMap.put("ota.services.error.saxexcp_warn", strArr);
        }

        /**
         * WMLValidator : MyErrorHandler :: error()
         * Overriding to get error string in _validationResourceString.
         * 
         * @param SAXException
         * @throws SAXException
         */
        public void error(SAXParseException e) throws SAXException {
            _overAllValidationResult[1] = true;
            _finalConclusion = false;
            int x = e.getLineNumber();
            if (a != 0) {
                x--;
            }
            _logger.error("error() :: Line number: " + x + " Message: " + e.getMessage());
            
            
            String strArr[] = { "" + x, e.getMessage() };
            _resourceMap.put("ota.services.error.saxexcp_error", strArr);
        }

        /**
         * WMLValidator : MyErrorHandler :: fatalError()
         * Overriding to get error string in _validationResourceString.
         * 
         * @param SAXException
         * @throws SAXException
         */
        public void fatalError(SAXParseException e) throws SAXException {
            _overAllValidationResult[2] = true;
            _finalConclusion = false;
            int x = e.getLineNumber();
            if (a != 0) {
                x--;
            }
            _logger.fatal("fatalError() :: Line number: " + x + " Message: " + e.getMessage());
            
            
            String strArr[] = { "" + x, e.getMessage() };
            _resourceMap.put("ota.services.error.saxexcp_fatal", strArr);

        }
    }
}
