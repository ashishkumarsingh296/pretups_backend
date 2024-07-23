package com.inter.cs3ccapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class CS3CCAPISimulator {
    public static void main(String args[]) {
        ServerSocket ss = null;
        BufferedReader buffReader = null;
        PrintWriter printWriter = null;

        try {
            int port = 5555;
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
                System.out.println("Waiting for request to handle");
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
    BufferedReader buffReader = null;
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

            buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        String strOut = "";
        if (strData.contains("LOGIN"))
            strOut = "RESP:" + status + ";";
        else if (strData.contains("SET:REFILL"))
            strOut = "RESP:" + status + ":OriginTransactionID,1000012:CurrencyLabel,Euro:CurrencySymbol,E$:AccountValueAfter,20000000:ServiceClassCurrent,ALL:ServiceFeeDateAfter,20070102:SupervisionPeriodExpiryDateAfter,20070102:Status,Negative Bal Barred:;";
        else if (strData.contains("SET:ACCOUNTADJUSTMENT"))
            strOut = "RESP:" + status + ":AccountBalance,5000000:CurrencyLabel,Euro:AccountFlagsAfter,ACTIVE:CurrencySymbol,E$:Status,Negative Bal Barred;";
        else if (strData.contains("GET:ACCOUNTINFORMATION"))
            strOut = "RESP:" + status + ":AccountBalance,5000000:Status,NegativeBalBarred:ServiceClass,101:ServiceFeePeriodExpiryDate,20070102:SupervisionPeriodExpiryDate,20070102:;";
        System.out.println("EXITED :: From RESPONSE String=" + strOut);
        return strOut;
    }
}
