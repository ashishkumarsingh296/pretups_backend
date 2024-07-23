package com.inter.huaweiknfix;

/**
 * @(#)HuaweiKnFixTestServer.java
 *                                Copyright(c) 2009, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Abhay January 28, 2009 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 *                                This is a testing purpose class for
 *                                HuaweiKnFixINHandler
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

public class HuaweiKnFixTestServer {
    public static void main(String args[]) {
        ServerSocket ss = null;
        BufferedReader buffReader = null;
        PrintWriter printWriter = null;
        try {
            int port = 5050;
            ss = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("HuaweiKnFixTestServer.main() Exception e=" + e);
            e.printStackTrace();
            return;
        }
        ArrayList statusList = new ArrayList();
        try {
            statusList.add("0");
        } catch (Exception e) {
            e.printStackTrace();
        }

        int i = 0;
        while (true) {
            try {
                System.out.println("HuaweiKnFixTestServer waiting for request...");
                new RecieverSocketHandler(ss.accept(), (String) statusList.get(i)).start();
            } catch (Exception e) {
                System.out.println("HuaweiKnFixTestServer outer Exception e=" + e);
                try {
                    if (buffReader != null)
                        buffReader.close();
                    if (printWriter != null)
                        printWriter.close();
                } catch (Exception ex) {
                    System.out.println("HuaweiKnFixTestServer.main() Inner Exception ex=" + ex);
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
                    StringBuffer strBuff = new StringBuffer(1028);
                    int d = 0;
                    while ((d = buffReader.read()) != 59)
                        strBuff.append((char) d);
                    data = strBuff.toString();
                    System.out.println((new Date()) + " HuaweiKnFixTestServer REQUEST = " + data);

                    String response = generateResponse(data, status);

                    // Thread.sleep(60000);
                    System.out.println((new Date()) + " HuaweiKnFixTestServer RESPONSE = " + response);
                    printWriter.println(response);
                    // out.write(response.getBytes());
                    printWriter.flush();
                    // out.flush();
                } catch (Exception e) {
                    System.out.println("RecieverSocketHandler.run() Exception e = " + e);
                    try {
                        if (buffReader != null)
                            buffReader.close();
                        if (printWriter != null)
                            printWriter.close();
                    } catch (Exception ex) {
                        System.out.println("RecieverSocketHandler.run() Inner Exception ex = " + ex);
                        ex.printStackTrace();
                        return;
                    }
                    e.printStackTrace();
                    return;
                }
            }// end of while loop
        } catch (Exception excep) {
            System.out.println("RecieverSocketHandler.run() Exception excep = " + excep);
            excep.printStackTrace();
        } finally {
            try {
                if (printWriter != null)
                    printWriter.close();
                if (buffReader != null)
                    buffReader.close();
                if (socket != null)
                    socket.close();
            } catch (Exception exc) {
                System.out.println("RecieverSocketHandler.run() Exception exc = " + exc);
                exc.printStackTrace();
            }
        }
    }

    private static String generateResponse(String strData, String status) {
        // System.out.println("RecieverSocketHandler.generateResponse() Entered  strData="+strData+", status="+status);
        String strOut = "";
        String txnId = null;
        String finalStrOut = null;
        try {
            if (!strData.contains("HBHBMSMSCHCK"))
                txnId = getRequestTransactionId(strData);

            String requestRespFilePath = "/home/pretups_kenya/tomcat5/webapps/pretups/WEB-INF/classes/configfiles/INFiles/HuaweiFixLineSimulatorResponse.props";
            // String
            // requestRespFilePath="C:\\Documents and Settings\\abhay.singh\\Desktop\\Orange_Kenya\\Changes\\HuaweiSimulatorResponse.props";

            Properties responseProp = new Properties();
            File file = new File(requestRespFilePath);
            responseProp.load(new FileInputStream(file));

            if (strData.contains("LOGIN")) {
                strOut = responseProp.getProperty("LOGIN") + ";";
            } else if (strData.contains("LOGOUT")) {
                strOut = responseProp.getProperty("LOGOUT") + ";";
            } else if (strData.contains("INFO")) {
                strOut = responseProp.getProperty("ACCOUNT_INFO") + ";";
            } else if (strData.contains("RECHG")) {
                strOut = responseProp.getProperty("RECHARGE") + ";";
            } else if (strData.contains("BALANCE")) {
                strOut = responseProp.getProperty("IMMEDIATE_DEBIT") + ";";
            } else if (strData.contains("VALIDITY")) {
                strOut = responseProp.getProperty("ACTIVE_STOP") + ";";
            }

            // System.out.println("\nstrOut = " + strOut);
            finalStrOut = strOut.replaceAll("MMMMM", txnId);
            // System.out.println("\nfinalStrOut = " + finalStrOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalStrOut;
    }

    private static String getRequestTransactionId(String p_responseStr) throws Exception {
        int index = 0;
        String transIdStr = null;
        try {
            if (p_responseStr.contains("LOGOUT")) {
                index = p_responseStr.indexOf("DLGEND");
                transIdStr = p_responseStr.substring(index + 10, p_responseStr.indexOf("TXEND")).trim();
            }

            else if (p_responseStr.contains("LOGIN") || p_responseStr.contains("INFO") || p_responseStr.contains("RECHG") || p_responseStr.contains("BALANCE") || p_responseStr.contains("VALIDITY")) {
                index = p_responseStr.indexOf("DLGCON");
                transIdStr = p_responseStr.substring(index + 10, p_responseStr.indexOf("TXEND")).trim();
            }
        } catch (Exception e) {
            System.out.println("RecieverSocketHandler.getRequestTransactionId() Exception e=" + e);
        } finally {
            // System.out.println("RecieverSocketHandler.getRequestTransactionId() Exited transIdStr="+transIdStr);
        }
        return transIdStr;
    }
}