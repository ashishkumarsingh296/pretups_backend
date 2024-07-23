package com.inter.huaweievr;

/**
 * @(#)HuaweiEVRTestServer.java
 *                              Copyright(c) 2007, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Vinay Kumar Singh December 10, 2007 Initial
 *                              Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 *                              This is a testing purpose class for
 *                              HuaweiEVRINHandler
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class HuaweiEVRTestServer {
    public static void main(String args[]) {
        ServerSocket ss = null;
        BufferedReader buffReader = null;
        PrintWriter printWriter = null;
        try {
            int port = 4050;
            System.out.println("Port : " + port);
            ss = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Exception e=" + e);
            return;
        }
        ArrayList statusList = new ArrayList();
        try {
            /*
             * file=new File("c://status.txt");
             * fileBuffReader=new BufferedReader(new InputStreamReader(new
             * FileInputStream(file)));
             * while((dataStr=fileBuffReader.readLine())!=null)
             * {
             * statusList.add(dataStr.trim());
             * }
             */
            statusList.add("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        while (true) {
            try {
                System.out.println("waiting");
                new RecieverSocketHandler(ss.accept(), (String) statusList.get(i)).start();

            } catch (Exception e) {
                System.out.println("Exception e=" + e);
                try {
                    if (buffReader != null)
                        buffReader.close();
                    if (printWriter != null)
                        printWriter.close();
                } catch (Exception ex) {
                    System.out.println("Inner Exception e=" + ex);
                }
            }
        }
    }
}

class RecieverSocketHandler extends Thread {
    Socket socket = null;
    // BufferedReader buffReader=null;
    InputStream buffReader = null;
    // InputStream buffReader=null;
    OutputStream out = null;
    PrintWriter printWriter = null;
    String data = null;
    int writeTimer = 0;
    String status;

    public RecieverSocketHandler(Socket p_socket, String p_status) {
        socket = p_socket;
        status = p_status;
    }

    public void run() {
        try {
            // buffReader=new BufferedReader(new
            // InputStreamReader(socket.getInputStream()));
            buffReader = socket.getInputStream();
            printWriter = new PrintWriter(socket.getOutputStream());
            while (true) {
                try {
                    System.out.println("waiting");
                    // data=buffReader.readLine();
                    StringBuffer strBuff = new StringBuffer(1028);
                    int d = 0;
                    while ((d = buffReader.read()) != 59)
                        strBuff.append((char) d);
                    data = strBuff.toString();
                    System.out.println((new Date()) + " REQUEST:" + data);
                    String response = generateResponse(data, status);
                    // Thread.sleep(60000);
                    printWriter.flush();
                    System.out.println((new Date()) + " RESPONSE: ASDSDF:A" + response);
                    printWriter.println(response);
                    // out.write(response.getBytes());
                    printWriter.flush();
                    // out.flush();
                } catch (Exception e) {
                    System.out.println("Exception e=" + e);
                    try {
                        if (buffReader != null)
                            buffReader.close();
                        if (printWriter != null)
                            printWriter.close();
                    } catch (Exception ex) {
                        System.out.println("Inner Exception e=" + ex);
                        return;
                    }
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printWriter != null)
                    printWriter.close();
                if (buffReader != null)
                    buffReader.close();
                if (socket != null)
                    socket.close();
            } catch (Exception e) {
            }
        }
    }

    private static String generateResponse(String strData, String status) {
        System.out.println("generateResponse Entered  strData:" + strData);
        String strOut = "";
        String txnId = null;

        try {
            // String txnid = null;
            /*
             * int index=strData.indexOf("TBD");
             * txnid =strData.substring(index+3,strData.indexOf("TXEND"));
             */
            if (!strData.contains("HBHB"))
                txnId = getRequestTransactionId(strData);

            if (strData.contains("LOGIN")) {
                strOut = "ACK00000522DLGCON0000" + txnId + " TXEND0000ACK: LOGIN: RETN=0, DESC=LOGIN Description;";
            } else if (strData.contains("LOGOUT")) {
                strOut = "ACK00000522DLGEND0000" + txnId + " TXEND0000ACK: LOGOUT: RETN=0, DESC=LOGOUT Description;";
            } else if (strData.contains("ACNTINFO")) {// (strData.contains("DISP PPS ACNTINFO"))
                                                      // {
                strOut = "89C9A2A4`SC`0004HBHBB7BDB7BD`SC`012C1.01internal     ACK English10000001DLGCON0000" + txnId + " TXEND0000ACK:DISP PPS ACNTINFO: RETN=0, DESC=\"Querying subscriber's information succeeded.\", ATTR=MSISDN&SUBCOSID&SERVICESTOP&ACTIVESTOP&BALANCE&LANGUAGETYPE&FRAUDLOCK&SUSPENDSTOP, RESULT=\"1815050020|1|20161214|20070216|32154100|2|0|20070517|\";";
            } else if (strData.contains("CHRG")) {// (strData.contains("CHGTRIG CHRG ACNT"))
                                                  // {
                strOut = "BAB6F9F9`SC`01641.01ijternal     ACK English10000001DLGCON0000" + txnId + " TXEND0000ACK:CHGTRIG CHRG ACNT: RETN=0, DESC=\"Manual recharging succeeded.\", ATTR=FACEVALUE&EXTRAVALUE&VALIDITYPERIOD&EXTRAVALIDITY&BALANCE&ACTIVESTOP&EXTRAFREEMINUTE&EXTRAFREESM&EXTRAFREEMINUTE2&EXTRAFREEMINUTE3&EXTRAFREESM2&EXTRAFREESM3, RESULT=5000078|0|220|0|607896368|20080216|0|0|0|0|0|0|;";
            } else if (strData.contains("BALANCE")) {// (strData.contains("MODI PPS BALANCE"))
                                                     // {
                strOut = "BAB6F9F9`SC`0004HBHBB7BDB7BD`SC`0004HBHBB7BDB7BD`SC`0004HBHBB7BDB7BD`SC`0004HBHBB7BDB7BD`SC`0004HBHBB7BDB7BD`SC`0004HBHBB7BDB7BD`SC`0004HBHBB7BDB7BD`SC`0004HBHBB7BDB7BD`SC`00F41.01internal     ACK English10000001DLGCON0000" + txnId + " TXEND0000ACK:MODI PPS BALANCE: RETN=0, DESC=\"Modifying subscriber's balance succeeded.\", ORGBALANCE=602896290, LASTBALANCE=602846212, SUCCNUM=1, SUCCLIST=1815050020, FAILNUM=0, FAILLIST=;";
            }
            System.out.println("\nstrOut :" + strOut);
        } catch (Exception e) {
            // e.printStackTrace();
            // System.out.println(e.getMessage());
        }
        return strOut;
    }

    private static String getRequestTransactionId(String p_responseStr) throws Exception {
        int index = 0;
        String transIdStr = null;
        try {
            if (p_responseStr.contains("LOGIN")) {
                index = p_responseStr.indexOf("DLGLGN");
                transIdStr = p_responseStr.substring(index + 10, p_responseStr.indexOf("TXBEG")).trim();
            }

            else if (p_responseStr.contains("LOGOUT") || p_responseStr.contains("ACNTINFO") || p_responseStr.contains("CHRG") || p_responseStr.contains("BALANCE")) {
                index = p_responseStr.indexOf("DLGCON");
                transIdStr = p_responseStr.substring(index + 10, p_responseStr.indexOf("TXBEG")).trim();
            }
        } catch (Exception e) {
            System.out.println("getRequestTransactionId Exception e=" + e);
        } finally {
            System.out.println("getRequestTransactionId Exited transIdStr:" + transIdStr);
        }
        return transIdStr;
    }
}
