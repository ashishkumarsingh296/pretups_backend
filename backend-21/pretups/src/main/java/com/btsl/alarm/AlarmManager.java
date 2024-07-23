/*
 * Created on 2005-04-18
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.alarm;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;
/**
 * @author amit.ruwali
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class AlarmManager extends Thread {
    private DatagramPacket packet = null;
    private DatagramSocket socket = null;
    private static InetAddress address = null;
    private char separator = '|';
    private int port;
    private int timeOut;
    private int ackFlag = 1;// "On"
   
    private String _componentName;
    private int _alarmID;
    private String _alarmState;
    private String _alarmMessage;

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
    @Override
    public void run() {
        final String METHOD_NAME = "run";
        try {
            String flag = Constants.getProperty("NEW_OAM");
           if (PretupsI.YES.equalsIgnoreCase(flag)) {
               OAMSimulator oam = new OAMSimulator();
               oam.sendTrap(_componentName, _alarmID, _alarmState, _alarmMessage);
            } else {
                sendAlarm(_componentName, _alarmID, _alarmState, _alarmMessage);
            }
        }// end of try
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }// end of run

    // Error Message list
    private int componentLenghtTooLarge = 1;
    private int componentLenghtTooSmall = 2;
    private int alarmLenghtTooLarge = 3;
    private int alarmLenghtTooSmall = 4;

    public static void main(String[] args) {

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
        final String METHOD_NAME = "sendAlarm";
        if (_log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_componentName: ");
        	msg.append(p_componentName);
        	msg.append(" p_alarmID: ");
        	msg.append(p_alarmID);
        	msg.append(" p_alarmState: ");
        	msg.append(p_alarmState);
        	msg.append(" p_alarmMessage: ");
        	msg.append(p_alarmMessage);
        	String message=msg.toString();
            _log.debug("sendAlarm", message);
        }
        address = InetAddress.getByName(Constants.getProperty("ONM_HOST_IP"));
        port = 24444;
        timeOut = 5000;
        StringBuffer messagePacket = null;
       
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
            sendingMessagePacket(messagePacket);

            if (ackFlag == 1) {
                
                status = waitForAcknowledgment(socket);

                if (!status) {
                    if (_log.isDebugEnabled())
                        _log.debug("sendAlarm", "Request Time Out :: Closing The Connection status: " + status);
                } else if (_log.isDebugEnabled())
                    _log.debug("sendAlarm", "Closing The Connection status: " + status);
            }// end of if
        }// end of try
        catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
        }

        finally {
            try {
                if (!socket.isClosed())
                    socket.close();
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            messagePacket = null;
            packet = null;
            socket = null;
            address = null;
        }// end of finally
    }// end of sendAlarm

	private void sendingMessagePacket(StringBuffer messagePacket)
			throws IOException {
		if (_log.isDebugEnabled())
		    _log.debug("sendAlarm", "messagePacket: " + messagePacket);
		packet = new DatagramPacket(messagePacket.toString().getBytes(), messagePacket.length(), address, port);

		if (_log.isDebugEnabled())
		{
			StringBuffer msg=new StringBuffer("");
        	msg.append("packet.getLength(): ");
        	msg.append(packet.getLength());
        	msg.append(" messagePacket.length(): ");
        	msg.append(messagePacket.length());
        	String message=msg.toString();
		    _log.debug("sendAlarm", message);
		}
		
		socket.send(packet); // Send the message packet to Alarm Manager

		if (_log.isDebugEnabled())
		    _log.debug("sendAlarm", "Successfully Sent Message Packet");
	}

    /**
     * This is method is used to wait for the acknowledgement,
     * 
     * @param ds
     *            - DatagramSocket
     */
    public boolean waitForAcknowledgment(DatagramSocket ds) {
        final String METHOD_NAME = "waitForAcknowledgment";
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
            _log.errorTrace(METHOD_NAME, ex);
        } finally {
            try {
                if (!ds.isClosed())
                    ds.close();
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
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
            return componentLenghtTooLarge;
        }

        if (p_componentName.length() <= 64) {
            return componentLenghtTooSmall;
        }

        if (p_alarmID > 99999) {
            return alarmLenghtTooLarge;
        }

        if (p_alarmID <= 0) {
            return alarmLenghtTooSmall;
        }
        return result;
    }// end of validateAlarm
}// end of class