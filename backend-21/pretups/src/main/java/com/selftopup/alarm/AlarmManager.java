/*
 * Created on 2005-04-18
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.selftopup.alarm;

/**
 * @author amit.ruwali
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.Date;
import java.io.*;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.Constants;

public class AlarmManager extends Thread {

    DatagramPacket packet = null;
    DatagramSocket socket = null;
    static InetAddress address = null;
    char separator = '|';
    int port;
    int timeOut;
    int ackFlag = 1;// "On"
    int MAX_COMPONENT_NAME_LEN = 64;
    String _componentName;
    int _alarmID;
    String _alarmState;
    String _alarmMessage;

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * constructer AlarmManager
     * 
     * @param p_componentName
     * @param p_alarmID
     * @param p_alarmState
     * @param p_alarmMessage
     */
    public AlarmManager(String p_componentName, int p_alarmID, String p_alarmState, String p_alarmMessage) {
        _componentName = p_componentName;
        _alarmID = p_alarmID;
        _alarmState = p_alarmState;
        _alarmMessage = p_alarmMessage;
    }// end of AlarmManager

    /**
     * This method is used to generate new thread,
     */
    public void run() {
        try {
            // String flag = Constants.getProperty("NEW_OAM");
            // if(PretupsI.YES.equalsIgnoreCase(flag))
            // {
            // // OAMSimulator oam = new OAMSimulator();
            // oam.sendTrap(_componentName,_alarmID,_alarmState,_alarmMessage);
            // }
            // else
            {
                sendAlarm(_componentName, _alarmID, _alarmState, _alarmMessage);
            }
        }// end of try
        catch (Exception e) {
            _log.errorTrace("run: Exception print stack trace: ", e);
        }
    }// end of run

    // Error Message list
    int componentLenghtTooLarge = 1;
    int componentLenghtTooSmall = 2;
    int alarmLenghtTooLarge = 3;
    int alarmLenghtTooSmall = 4;

    public static void main(String[] args) throws IOException {

    }

    /**
     * This method is used to send alarm,
     * 
     * @param p_componentName
     *            - Name of the component
     * @param p_alarmState
     *            - Alarm state
     * @param p_alarmMessage
     *            - Alarm message
     * @param p_alarmID
     *            - DatagramSocket
     */
    public void sendAlarm(String p_componentName, int p_alarmID, String p_alarmState, String p_alarmMessage) throws IOException {
        if (_log.isDebugEnabled())
            _log.debug("sendAlarm", "Entered p_componentName: " + p_componentName + " p_alarmID: " + p_alarmID + " p_alarmState: " + p_alarmState + " p_alarmMessage: " + p_alarmMessage);

        address = InetAddress.getByName(Constants.getProperty("ONM_HOST_IP"));
        port = 24444;
        timeOut = 5000;
        StringBuffer messagePacket = null;
        byte[] buf = new byte[256];
        boolean status = false;
        long time = (new Date().getTime()) / 1000L;
        messagePacket = new StringBuffer();
        messagePacket.append(p_componentName);
        messagePacket.append(separator);
        messagePacket.append(p_alarmState);
        messagePacket.append(separator);
        messagePacket.append(p_alarmID);
        messagePacket.append(separator);
        messagePacket.append(time);
        messagePacket.append(separator);
        messagePacket.append(p_alarmMessage);
        int packetLength = messagePacket.length();
        String packetLengthStr = String.valueOf(packetLength);
        int length = packetLengthStr.length();
        while (length < 5) {
            packetLengthStr = "0" + packetLengthStr;
            length = packetLengthStr.length();
        }// end of while
        messagePacket.insert(0, packetLengthStr);

        try {
            socket = new DatagramSocket();
            if (_log.isDebugEnabled())
                _log.debug("sendAlarm", "messagePacket: " + messagePacket);
            packet = new DatagramPacket(messagePacket.toString().getBytes(), messagePacket.length(), address, port);

            if (_log.isDebugEnabled())
                _log.debug("sendAlarm", "packet.getLength(): " + packet.getLength() + " messagePacket.length(): " + messagePacket.length());
            socket.send(packet); // Send the message packet to Alarm Manager

            if (_log.isDebugEnabled())
                _log.debug("sendAlarm", "Successfully Sent Message Packet");

            if (ackFlag == 1) {
                // if(_log.isDebugEnabled())
                // _log.debug("sendAlarm","Waiting For Acknowledgement...");
                status = waitForAcknowledgment(socket);

                if (!status) {
                    if (_log.isDebugEnabled())
                        _log.debug("sendAlarm", "Request Time Out :: Closing The Connection status: " + status);
                } else if (_log.isDebugEnabled())
                    _log.debug("sendAlarm", "Closing The Connection status: " + status);
            }// end of if
        }// end of try
        catch (Exception ex) {
            _log.errorTrace("sendAlarm : Exception print stack trace:e=", ex);
        }

        finally {
            try {
                if (!socket.isClosed())
                    socket.close();
            } catch (Exception ex) {
                _log.errorTrace("sendAlarm: Exception print stack trace:ex=", ex);
            }
            messagePacket = null;
            packet = null;
            socket = null;
            address = null;
        }// end of finally
    }// end of sendAlarm

    /**
     * This is method is used to wait for the acknowledgement,
     * 
     * @param ds
     *            - DatagramSocket
     */
    public boolean waitForAcknowledgment(DatagramSocket ds) {
        byte[] buf = new byte[256];
        DatagramPacket dp = null;
        try {
            // creating new datagram packet
            dp = new DatagramPacket(buf, buf.length);
            ds.setSoTimeout(timeOut);
            ds.receive(dp);

            if (_log.isDebugEnabled())
                _log.debug("waitForAcknowledgment", "Acknowledgement: " + new String(dp.getData()));
            ds.close();
            return true;
        }// end of try
        catch (Exception ex) {
            _log.errorTrace("waitForAcknowledgment Exception print stack trace:e=", ex);
        } finally {
            try {
                if (!ds.isClosed())
                    ds.close();
            } catch (Exception ex) {
            }
            dp = null;
            buf = null;
            ds = null;
        }// end of finally
        return false;
    }// end of waitForAcknowledgment

    /**
     * This method is used to validate the Alam for component length, alarm id,
     * 
     * @param p_componentName
     *            - Name of the component
     * @param p_alarmID
     *            - Alarm id
     * @param p_alarmState
     *            - Alarm state
     * @param p_alarmMessage
     *            - Alarm message
     */
    public int validateAlarm(String p_componentName, int p_alarmID, int p_alarmState, String p_alarmMessage) {
        int result = 0;
        if (p_componentName.length() > 64) {
            return (componentLenghtTooLarge);
        }

        if (p_componentName.length() <= 64) {
            return (componentLenghtTooSmall);
        }

        if (p_alarmID > 99999) {
            return (alarmLenghtTooLarge);
        }

        if (p_alarmID <= 0) {
            return (alarmLenghtTooSmall);
        }
        return result;
    }// end of validateAlarm
}// end of class