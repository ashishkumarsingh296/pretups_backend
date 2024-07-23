package com.inter.socket;

/**
 * @(#)SocketConnection.java
 *                           Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Abhijit Chauhan June 22,2005 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.inter.connection.BTSLConnection;

public class SocketConnection extends BTSLConnection {
    private Socket _socket = null;

    public SocketConnection(String p_ip, int p_port) throws Exception {
        _socket = new Socket(p_ip, p_port);
        setPrintWriter();
        setBufferedReader();
    }

    protected void setPrintWriter() {
        try {
            _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream())), true);
            ;
        } catch (Exception e) {
            // Do nothing
        }
    }

    protected void setBufferedReader() {
        try {
            _in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        } catch (Exception e) {
            // Do nothing
        }
    }

    public void setTimeout(int p_timeout) {
        try {
            _socket.setSoTimeout(p_timeout);
        } catch (Exception e) {
        }
    }

    public void close() throws Exception {
        _socket.close();

    }

    /**
     * This method is used to return the client port of the socket connection
     * 
     * @return
     * @throws Exception
     */
    public int getLocalPort() throws Exception {
        return _socket.getLocalPort();
    }
}
