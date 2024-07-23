/*
 * @# MessagesVO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Chhaya Sikheria Sep 29, 2011 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2011 Comviva.
 */
package com.selftopup.pretups.messages.businesslogic;

import java.io.Serializable;
import java.util.List;

public class MessagesVO implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String _messageType;
    private String _messageCode;
    private String _defaultMessage;
    private String _networkCode;
    private String _message1;
    private String _message2;
    private String _message3;
    private String _message4;
    private String _message5;
    private String _mclass;
    private String _description;
    private List<MessageArgumentVO> _argumentList = null;

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(" MessageType =" + _messageType);
        sbf.append(",MessageCode =" + _messageCode);
        sbf.append(",DefaultMessage =" + _defaultMessage);
        sbf.append(",NetworkCode =" + _networkCode);
        sbf.append(",Message1 =" + _message1);
        sbf.append(",Message2 =" + _message2);
        sbf.append(",Message3 =" + _message3);
        sbf.append(",Message4 =" + _message4);
        sbf.append(",Message5 =" + _message5);

        sbf.append(",Mclass =" + _mclass);
        sbf.append(",Description =" + _description);

        return sbf.toString();
    }

    /**
     * @return the _messageType
     */
    public String getMessageType() {
        return _messageType;
    }

    /**
     * @param type
     *            the _messageType to set
     */
    public void setMessageType(String type) {
        _messageType = type;
    }

    /**
     * @return the _messageCode
     */
    public String getMessageCode() {
        return _messageCode;
    }

    /**
     * @param code
     *            the _messageCode to set
     */
    public void setMessageCode(String code) {
        _messageCode = code;
    }

    /**
     * @return the _defaultMessage
     */
    public String getDefaultMessage() {
        return _defaultMessage;
    }

    /**
     * @param message
     *            the _defaultMessage to set
     */
    public void setDefaultMessage(String message) {
        _defaultMessage = message;
    }

    /**
     * @return the _networkCode
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param code
     *            the _networkCode to set
     */
    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    /**
     * @return the _message1
     */
    public String getMessage1() {
        return _message1;
    }

    /**
     * @param _message1
     *            the _message1 to set
     */
    public void setMessage1(String _message1) {
        this._message1 = _message1;
    }

    /**
     * @return the _message2
     */
    public String getMessage2() {
        return _message2;
    }

    /**
     * @param _message2
     *            the _message2 to set
     */
    public void setMessage2(String _message2) {
        this._message2 = _message2;
    }

    /**
     * @return the _message3
     */
    public String getMessage3() {
        return _message3;
    }

    /**
     * @param _message3
     *            the _message3 to set
     */
    public void setMessage3(String _message3) {
        this._message3 = _message3;
    }

    /**
     * @return the _message4
     */
    public String getMessage4() {
        return _message4;
    }

    /**
     * @param _message4
     *            the _message4 to set
     */
    public void setMessage4(String _message4) {
        this._message4 = _message4;
    }

    /**
     * @return the _message5
     */
    public String getMessage5() {
        return _message5;
    }

    /**
     * @param _message5
     *            the _message5 to set
     */
    public void setMessage5(String _message5) {
        this._message5 = _message5;
    }

    /**
     * @return the _mclass
     */
    public String getMclass() {
        return _mclass;
    }

    /**
     * @param _mclass
     *            the _mclass to set
     */
    public void setMclass(String _mclass) {
        this._mclass = _mclass;
    }

    /**
     * @return the _description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * @param _description
     *            the _description to set
     */
    public void setDescription(String _description) {
        this._description = _description;
    }

    /**
     * @return the _argumentList
     */
    public List<MessageArgumentVO> getArgumentList() {
        return _argumentList;
    }

    /**
     * @param list
     *            the _argumentList to set
     */
    public void setArgumentList(List<MessageArgumentVO> list) {
        _argumentList = list;
    }

}
