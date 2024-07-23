package com.inter.huawei84;

/**
 * @(#)Huawei84TestServer.java
 *                             Copyright(c) 2007, Bharti Telesoft Int. Public
 *                             Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Vinay Kumar Singh December 26, 2007 Initial
 *                             Creation
 *                             ------------------------------------------------
 *                             ------------------------------------------------
 *                             This is a testing purpose class for
 *                             HuaweiEVRINHandler
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class Huawei84TestServer {
    public static void main(String args[]) {
        ServerSocket ss = null;
        BufferedReader buffReader = null;
        PrintWriter printWriter = null;
        try {
            // int port = 4878;
            int port = 5050;
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
    InputStream buffReader = null;
    OutputStream out = null;
    PrintWriter printWriter = null;
    String data = null;
    int writeTimer = 0;
    String status;

    // Constructor
    public RecieverSocketHandler(Socket p_socket, String p_status) {
        socket = p_socket;
        status = p_status;
    }

    public void run() {
        try {
            buffReader = socket.getInputStream();
            printWriter = new PrintWriter(socket.getOutputStream());
            while (true) {
                try {
                    // data=buffReader.readLine();
                    StringBuffer strBuff = new StringBuffer(1028);
                    int d = 0;
                    while ((d = buffReader.read()) != 59)
                        strBuff.append((char) d);
                    data = strBuff.toString();
                    System.out.println((new Date()) + " REQUEST:" + data);
                    String response = generateResponse(data, status);
                    // Thread.sleep(60000);
                    System.out.println((new Date()) + " RESPONSE:" + response);
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
        String finalStrOut = null;
        try {
            /*
             * int index=strData.indexOf("TBD");
             * txnid =strData.substring(index+3,strData.indexOf("TXEND"));
             */
            if (!strData.contains("HBHB"))
                txnId = getRequestTransactionId(strData);

            String requestRespFilePath = "/home/pretups512_dev/tomcat5/webapps/pretups/WEB-INF/classes/configfiles/INFiles/Huawei84RequestResponse.props";
            Properties properties = new Properties();
            File file = new File(requestRespFilePath);
            properties.load(new FileInputStream(file));

            if (strData.contains("LOGIN")) {
                strOut = properties.getProperty("LOGIN") + ";";
            } else if (strData.contains("LOGOUT")) {
                strOut = properties.getProperty("LOGOUT") + ";";
            } else if (strData.contains("ACNTINFO")) {// (strData.contains("DISP PPS ACNTINFO"))
                                                      // {
                strOut = properties.getProperty("ACCOUNT_INFO") + ";";
            } else if (strData.contains("CHRG")) {// (strData.contains("CHGTRIG CHRG ACNT"))
                                                  // {
                strOut = properties.getProperty("RECHARGE") + ";";
            } else if (strData.contains("BALANCE")) {// (strData.contains("MODI PPS BALANCE"))
                                                     // {
                strOut = properties.getProperty("IMMEDIATE_DEBIT") + ";";
            } else if (strData.contains("VALIDITY")) {// (strData.contains("MODI PPS VALIDITY"))
                                                      // {
                strOut = properties.getProperty("ACTIVE_STOP") + ";";
            }

            System.out.println("\nstrOut :" + strOut);
            finalStrOut = strOut.replaceAll("MMMMM", txnId);
            System.out.println("\finalStrOut :" + finalStrOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalStrOut;
    }

    private static String getRequestTransactionId(String p_responseStr) throws Exception {
        int index = 0;
        String transIdStr = null;
        try {
            if (p_responseStr.contains("LOGIN")) {
                index = p_responseStr.indexOf("DLGLGN");
                transIdStr = p_responseStr.substring(index + 10, p_responseStr.indexOf("TXBEG")).trim();
            }

            else if (p_responseStr.contains("LOGOUT") || p_responseStr.contains("ACNTINFO") || p_responseStr.contains("CHRG") || p_responseStr.contains("BALANCE") || p_responseStr.contains("VALIDITY")) {
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
