package com.btsl.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class UDPClient {
    private DatagramSocket theSocket = null;
    private int serverPort = 8001;
    private static final Log _log = LogFactory.getLog(UDPClient.class.getName());

    public UDPClient(String ip, String port) {
        final String methodName = "UDPClient";
        try {
            theSocket = new DatagramSocket();
            String[] ipStrArr = ip.split("\\.");
            serverPort = Integer.parseInt(port);
            // but if you want to connect to your remote server, then alter the
            // theServer address below
            // InetAddress theServer = InetAddress.getLocalHost();
            // InetAddress theServer = InetAddress.getByAddress(new
            // byte[]{(byte)172,16,1,(byte)155});
            
            // cast fixes
            // InetAddress theServer = InetAddress.getByAddress(new byte[] { (byte) Integer.parseInt(ipStrArr[0]), (byte) Integer.parseInt(ipStrArr[1]), (byte) Integer.parseInt(ipStrArr[2]), (byte) Integer.parseInt(ipStrArr[3]) });
            InetAddress theServer = InetAddress.getByAddress(new byte[] { Byte.parseByte(ipStrArr[0]), Byte.parseByte(ipStrArr[1]), Byte.parseByte(ipStrArr[2]), Byte.parseByte(ipStrArr[3]) });
            _log.debug(methodName, "Socket creation error  : " + theServer.getHostAddress());
            theSocket.connect(theServer, serverPort);
            _log.info(methodName, "Client socket created");
        } catch (SocketException ExceSocket) {
            _log.errorTrace(methodName, ExceSocket);
        } catch (UnknownHostException ExceHost) {
            _log.errorTrace(methodName, ExceHost);
        }
    }

    public String sendMessage(String message) {
        final String methodName = "sendMessage";
        DatagramPacket theSendPacket;
        DatagramPacket theReceivedPacket;
        InetAddress theServerAddress;
        byte[] outBuffer;
        byte[] inBuffer;
        String response = null;

        // the place to store the sending and receiving data
        inBuffer = new byte[500];
        outBuffer = new byte[50];
        try {
            // String message =
            // "92#N#22511112#1#75#22511111 has requested a balance transfer of  1 L\n1 to Confirm\n2 to Reject";
            // String message =
            // "100:OTE5NjExOTA5MDkx#125:TUVOVV9QVVNI#126:MTIx#129:YWRtaW4=#130:YWRtaW4=#124:MTIyMjIyMjIyMg==#127:UHJpY2VzIEF2YWlsYWJsZTo=#128:MjA=";
            outBuffer = message.getBytes();
            // String s = new sun.misc.BASE64Encoder().encode(outBuffer);
            // String [] msg=message.split("#");
            // StringBuffer msgBuff=new StringBuffer();
            // for(int i=0;i<msg.length;i++)
            // msgBuff.append(new
            // sun.misc.BASE64Encoder().encode(msg[i].getBytes()) + "#");
            // System.out.println("Message sending is msgBuff : " + msgBuff);
            _log.debug(methodName, "Message sending is : " + message);
            _log.debug(methodName, "ADDR: " + theSocket.getInetAddress());

            // the server details
            theServerAddress = theSocket.getInetAddress();

            // build up a packet to send to the server
            theSendPacket = new DatagramPacket(outBuffer, outBuffer.length, theServerAddress, serverPort);
            // send the data
            theSocket.send(theSendPacket);
            _log.info(methodName, "packet sent");
            // get the servers response within this packet
            theReceivedPacket = new DatagramPacket(inBuffer, inBuffer.length);
            // for(int i=0;i<5;i++){
            theSocket.receive(theReceivedPacket);
            // the server response is...
            response = new String(theReceivedPacket.getData(), 0, theReceivedPacket.getLength());
            _log.debug(methodName, "Client - server response : " + response);
            // }
            theSocket.close();

        } catch (IOException ExceIO) {
            _log.errorTrace(methodName, ExceIO);
        }

        return response;
    }

    /*
     * public static void main(String[] args)
     * {
     * UDPClient theClient = new UDPClient("","");
     * theClient.sendMessage("");
     * }
     */
}