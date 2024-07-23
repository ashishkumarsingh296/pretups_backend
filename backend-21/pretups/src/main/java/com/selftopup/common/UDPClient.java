package com.selftopup.common;

import java.io.*;
import java.net.*;

public class UDPClient {
    DatagramSocket theSocket = null;
    int serverPort = 8001;

    public UDPClient(String ip, String port) {
        try {
            theSocket = new DatagramSocket();
            String[] ipStrArr = ip.split("\\.");
            serverPort = Integer.parseInt(port);
            // but if you want to connect to your remote server, then alter the
            // theServer address below
            // InetAddress theServer = InetAddress.getLocalHost();
            // InetAddress theServer = InetAddress.getByAddress(new
            // byte[]{(byte)172,16,1,(byte)155});
            InetAddress theServer = InetAddress.getByAddress(new byte[] { (byte) Integer.parseInt(ipStrArr[0]), (byte) Integer.parseInt(ipStrArr[1]), (byte) Integer.parseInt(ipStrArr[2]), (byte) Integer.parseInt(ipStrArr[3]) });
            System.out.println("Socket creation error  : " + theServer.getHostAddress());
            theSocket.connect(theServer, serverPort);
            System.out.println("Client socket created");
        } catch (SocketException ExceSocket) {
            System.out.println("Socket creation error  : " + ExceSocket.getMessage());
        } catch (UnknownHostException ExceHost) {
            System.out.println("Socket host unknown : " + ExceHost.getMessage());
        }
    }

    public String sendMessage(String message) {
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
            System.out.println("Message sending is : " + message);
            System.out.println("ADDR: " + theSocket.getInetAddress());

            // the server details
            theServerAddress = theSocket.getInetAddress();

            // build up a packet to send to the server
            theSendPacket = new DatagramPacket(outBuffer, outBuffer.length, theServerAddress, serverPort);
            // send the data
            theSocket.send(theSendPacket);
            System.out.println("packet sent");
            // get the servers response within this packet
            theReceivedPacket = new DatagramPacket(inBuffer, inBuffer.length);
            // for(int i=0;i<5;i++){
            theSocket.receive(theReceivedPacket);
            // the server response is...
            response = new String(theReceivedPacket.getData(), 0, theReceivedPacket.getLength());
            System.out.println("Client - server response : " + response);
            // }
            theSocket.close();

        } catch (IOException ExceIO) {
            System.out.println("Client getting data error : " + ExceIO.getMessage());
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