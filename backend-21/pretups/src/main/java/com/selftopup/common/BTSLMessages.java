package com.selftopup.common;

import java.util.List;
import java.util.Map;

/**
 * @(#)BTSLMessages.java
 *                       Copyright(c) 2000, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Mohit Goel 19/06/2005 Initial Creation
 * 
 *                       This is the Messages Class, used where when show an
 *                       success message
 * 
 */
public class BTSLMessages {

    private String[] _args;
    private Map _messageMap;
    private String _messageKey;
    private boolean _isKey = true;
    private String _forwardPath;
    private List _messageList;

    /**
     * Constructor
     */

    public BTSLMessages(String messageKey) {
        _messageKey = messageKey;
    }

    public BTSLMessages(String messageKey, boolean isKey) {
        _messageKey = messageKey;
        _isKey = isKey;
    }

    public BTSLMessages(String messageKey, String forwardPath) {
        _messageKey = messageKey;
        _forwardPath = forwardPath;
    }

    public BTSLMessages(String messageKey, String[] args) {
        _messageKey = messageKey;
        _args = args;
    }

    public BTSLMessages(String messageKey, String[] args, String forwardPath) {
        _messageKey = messageKey;
        _forwardPath = forwardPath;
        _args = args;
    }

    public BTSLMessages(Map messageMap) {
        _messageMap = messageMap;
    }

    public BTSLMessages(Map messageMap, String forwardPath) {
        _messageMap = messageMap;
        _forwardPath = forwardPath;
    }

    public BTSLMessages(String messageKey, List messageList) {
        _messageKey = messageKey;
        _messageList = messageList;
    }

    public BTSLMessages(List messageList) {
        _messageList = messageList;
    }

    public BTSLMessages(List messageList, String forwardPath) {
        _messageList = messageList;
        _forwardPath = forwardPath;
    }

    public String print() {
        StringBuffer sb = new StringBuffer();
        sb.append("Message Key: " + _messageKey + "\n");
        if (_args != null && _args.length > 0) {
            for (int i = 0; i < _args.length; i++)
                sb.append("Args [" + i + "] = " + _args[i]);
        }

        return sb.toString();
    }
@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("message key=").append(_messageKey).append(",");

        return sb.toString();
    }

    public String[] getArgs() {
        return _args;
    }

    public String getMessageKey() {
        return _messageKey;
    }

    public Map getMessageMap() {
        return _messageMap;
    }

    public List getMessageList() {
        return _messageList;
    }

    /**
     * @return Returns the forwardPath.
     */
    public String getForwardPath() {
        return _forwardPath;
    }

    /**
     * @return Returns the isKey.
     */
    public boolean isKey() {
        return _isKey;
    }

    /**
     * @param isKey
     *            The isKey to set.
     */
    public void setKey(boolean isKey) {
        _isKey = isKey;
    }
}
