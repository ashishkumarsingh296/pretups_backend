package com.btsl.common;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class UDPServer {
	
    private static final Log _log = LogFactory.getLog(UDPServer.class.getName());
    private static final boolean _running = true;
    
    /**
	 * ensures no instantiation
	 */
    private UDPServer(){
    	
    }
    
    public static void main(String args[]){
    	try{
        DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
       
          while(_running) {

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            if (_log.isDebugEnabled()) {
                _log.debug("RECEIVED :", sentence);
                }
           

            HashMap reqMap = BTSLUtil.getStringToHash(sentence, "#", ":");
            if (_log.isDebugEnabled()) {
                _log.debug("SKIP string: " ,reqMap.get("128"));
                }
            byte[] skip = BTSLUtil.decodeBuffer((String) reqMap.get("128"));
            String skipStr = new String(skip);
            if (_log.isDebugEnabled()) {
                _log.debug("decoded SKIP string: ",skipStr);
                }
            String[] params = skipStr.split("\\*");

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String notification = "200#" + params[0] + "# Submitted successfully";
            sendData = notification.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);

            StringBuffer reqStr = new StringBuffer();
            String context_type = null;
            if (args[2] != null) {
                context_type = args[2];
                if (context_type.equals("XML")) {
                    reqStr.append("<MSISDN1>" + new String(BTSLUtil.decodeBuffer((String) reqMap.get("100"))) + "</MSISDN1>");
                    reqStr.append("<MSISDN2>" + params[0] + "</MSISDN2>");
                    reqStr.append("<AMOUNT>" + params[1] + "</AMOUNT>");
                    reqStr.append("</COMMAND>");
                } else {
                    reqStr.append("&MSISDN1=" + new String(BTSLUtil.decodeBuffer((String) reqMap.get("100"))));
                    reqStr.append("&MSISDN2=" + params[0]);
                    reqStr.append("&AMOUNT=" + params[1]);
                }
            }
       }
       }
    	catch(Exception e){
		 _log.errorTrace("main", e);
		}
    
    }
}