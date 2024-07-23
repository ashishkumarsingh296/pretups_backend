package com.btsl.pretups.inter.util;

/**
 * @(#)FermaRequestResponse.java
 *                               Copyright(c) 2005, Bharti Telesoft Int. Public
 *                               Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Abhijit Chauhan Oct 06,2005 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 **/
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory; // JAXP classes for parsing

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node; // W3C DOM classes for traversing the document
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; // SAX classes used for error handling by JAXP

import com.btsl.util.BTSLUtil;
// For reading the input file

public abstract class XMLRequestResponse {
    protected static DocumentBuilderFactory _dbf = DocumentBuilderFactory.newInstance();
    protected static javax.xml.parsers.DocumentBuilder _parser = null;
    static {
        try {
            _dbf.setValidating(true);
            _parser = _dbf.newDocumentBuilder();
            _parser.setErrorHandler(new org.xml.sax.ErrorHandler() {
                public void warning(SAXParseException e) {
                    System.err.println("WARNING: " + e.getMessage());
                }

                public void error(SAXParseException e) {
                    System.err.println("ERROR: " + e.getMessage());
                }

                public void fatalError(SAXParseException e) throws SAXException {
                    System.err.println("FATAL: " + e.getMessage());
                    throw e; // re-throw the error
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract HashMap parseResponse(int action, String responseStr) throws Exception;

    protected abstract String generateRequest(int action, HashMap map) throws Exception;

    /**
     * Output the specified DOM Node object, printing it using the specified
     * indentation string
     **/
    protected String write(Node node, StringBuffer strBuff, String indent) {
        // The output depends on the type of the node
        switch (node.getNodeType()) {
        case Node.DOCUMENT_NODE: { // If its a Document node
            Document doc = (Document) node;
            strBuff.append(indent + "<?xml version='1.0'?>"); // Output header
            Node child = doc.getFirstChild(); // Get the first node
            while (child != null) { // Loop 'till no more nodes
                write(child, strBuff, indent); // Output node
                child = child.getNextSibling(); // Get next node
            }
            break;
        }
        case Node.DOCUMENT_TYPE_NODE: { // It is a <!DOCTYPE> tag
            DocumentType doctype = (DocumentType) node;
            // Note that the DOM Level 1 does not give us information about
            // the the public or system ids of the doctype, so we can't output
            // a complete <!DOCTYPE> tag here. We can do better with Level 2.
            strBuff.append("<!DOCTYPE " + doctype.getName() + ">");
            break;
        }
        case Node.ELEMENT_NODE: { // Most nodes are Elements
            Element elt = (Element) node;
            strBuff.append(indent + "<" + elt.getTagName()); // Begin start tag
            NamedNodeMap attrs = elt.getAttributes(); // Get attributes
            for (int i = 0; i < attrs.getLength(); i++) { // Loop through them
                Node a = attrs.item(i);
                strBuff.append(" " + a.getNodeName() + "='" + // Print attr.
                                                              // name
                fixup(a.getNodeValue()) + "'"); // Print attr. value
            }
            strBuff.append(">"); // Finish start tag

            String newindent = indent; // Increase indent
            Node child = elt.getFirstChild(); // Get child
            while (child != null) { // Loop
                write(child, strBuff, newindent); // Output child
                child = child.getNextSibling(); // Get next child
            }

            strBuff.append(indent + "</" + // Output end tag
            elt.getTagName() + ">");
            break;
        }
        case Node.TEXT_NODE: { // Plain text node
            Text textNode = (Text) node;

            String text = BTSLUtil.NullToString(textNode.getData()).trim();
            if ((text != null) && text.length() > 0) // If non-empty
                strBuff.append(indent + fixup(text)); // print text
            break;
        }
        case Node.PROCESSING_INSTRUCTION_NODE: { // Handle PI nodes
            ProcessingInstruction pi = (ProcessingInstruction) node;
            strBuff.append(indent + "<?" + pi.getTarget() + " " + pi.getData() + "?>");
            break;
        }
        case Node.ENTITY_REFERENCE_NODE: { // Handle entities
            strBuff.append(indent + "&" + node.getNodeName() + ";");
            break;
        }
        case Node.CDATA_SECTION_NODE: { // Output CDATA sections
            CDATASection cdata = (CDATASection) node;
            // Careful! Don't put a CDATA section in the program itself!
            strBuff.append(indent + "<" + "![CDATA[" + cdata.getData() + "]]" + ">");
            break;
        }
        case Node.COMMENT_NODE: { // Comments
            Comment c = (Comment) node;
            strBuff.append(indent + "<!--" + c.getData() + "-->");
            break;
        }
        default: // Hopefully, this won't happen too much!
            System.err.println("Ignoring node: " + node.getClass().getName());
            break;
        }
        return strBuff.toString();
    }

    // This method replaces reserved characters with entities.
    protected String fixup(String s) {
        StringBuffer sb = new StringBuffer();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
            default:
                sb.append(c);
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '&':
                sb.append("&amp;");
                break;
            case '"':
                sb.append("&quot;");
                break;
            case '\'':
                sb.append("&apos;");
                break;
            }
        }
        return sb.toString();
    }
}