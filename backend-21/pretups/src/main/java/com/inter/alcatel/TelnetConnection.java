package com.inter.alcatel;

/**
 * @(#)TelnetConnection.java
 *                           Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Gurjeet Singh Bedi Oct 19,2005 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 *                           Connection class for the interface
 */

import java.io.BufferedInputStream;

import com.btsl.pretups.inter.cache.FileCache;
import com.inter.connection.BTSLConnection;
import com.inter.telnet.TelnetWrapper;

public class TelnetConnection extends BTSLConnection {

    private String _LOGIN_PROMPT = null;
    private String _PASSWORD_PROMPT = null;
    private String _TELNET_PROMPT = null;
    private int TELNET_LOGIN_TIMEOUT = 180000;
    private TelnetWrapper _telnetWrapper = null;

    public TelnetConnection(String p_poolID, String p_hostIP, String p_loginID, String p_password) throws Exception {
        _LOGIN_PROMPT = FileCache.getValue(p_poolID, "LOGIN_PROMPT");
        _PASSWORD_PROMPT = FileCache.getValue(p_poolID, "PASSWORD_PROMPT");
        _TELNET_PROMPT = FileCache.getValue(p_poolID, "TELNET_PROMPT");
        try {
            TELNET_LOGIN_TIMEOUT = Integer.parseInt(FileCache.getValue(p_poolID, "TELNET_LOGIN_TIMEOUT"));
        } catch (Exception e) {
            TELNET_LOGIN_TIMEOUT = 180000;
        }
        try {
            _telnetWrapper = new TelnetWrapper(p_hostIP);
            String output = "";
            output = _telnetWrapper.receiveUntil(_LOGIN_PROMPT, TELNET_LOGIN_TIMEOUT);
            _telnetWrapper.send(p_loginID + "\r");

            output = _telnetWrapper.receiveUntil(_PASSWORD_PROMPT, TELNET_LOGIN_TIMEOUT);
            _telnetWrapper.send(p_password + "\r");

            output = _telnetWrapper.receiveUntil(_TELNET_PROMPT, TELNET_LOGIN_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int flush() throws Exception {
        int count = 0;
        try {
            BufferedInputStream buffIS = this._telnetWrapper.getTelnetInputStream();
            count = buffIS.available();
            buffIS.skip(count);
            System.out.println("TelnetConnection flush() after skipping count=" + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    protected void setPrintWriter() throws Exception {
        // TODO Auto-generated method stub

    }

    protected void setBufferedReader() throws Exception {
    }

    public void setTimeout(int p_timeout) throws Exception {
        // TODO Auto-generated method stub

    }

    public void close() throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public TelnetWrapper getTelnetWrapper() {
        return _telnetWrapper;
    }

    public void setTelnetWrapper(TelnetWrapper telnetWrapper) {
        _telnetWrapper = telnetWrapper;
    }

    public String getTelnetPrompt() {
        return _TELNET_PROMPT;
    }

    public void setTelnetPrompt(String telnet_prompt) {
        _TELNET_PROMPT = telnet_prompt;
    }

    public int getTelnetLoginTimeout() {
        return TELNET_LOGIN_TIMEOUT;
    }

    public void setTelnetLoginTimeout(int telnet_login_timeout) {
        TELNET_LOGIN_TIMEOUT = telnet_login_timeout;
    }

}
