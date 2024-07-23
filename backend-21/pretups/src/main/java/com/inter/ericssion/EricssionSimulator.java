package com.inter.ericssion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class EricssionSimulator {
    private BufferedReader ois = null;

    public static void main(String args[]) {
        Socket clientSocket = null;
        ServerSocket ss = null;
        boolean listening = true;
        BufferedReader buffReader = null;
        PrintWriter printWriter = null;

        try {
            // int port = 4878;
            int port = 3000;
            System.out.println("Port : " + port);
            ss = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Exception e=" + e);
            return;
        }
        File file = null;
        BufferedReader fileBuffReader = null;
        String dataStr = null;
        ArrayList statusList = new ArrayList();
        String msisdn = null;
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
            statusList.add("200");
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
                    data = buffReader.readLine();
                    String response = generateResponse(data, status);
                    System.out.println((new Date()) + " REQUEST:" + data);
                    Thread.sleep(50);
                    // Thread.sleep(60000);
                    System.out.println((new Date()) + " RESPONSE:" + response);
                    printWriter.println(response);
                    printWriter.flush();
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
        System.out.println("generateResponse Entered");
        String strOut = "";
        try {
            Utility utility = new Utility();
            StringParser spData = null;
            String strFormat = "";

            String strDate = "";

            strFormat = strData.substring(strData.indexOf('?') + 1, strData.length());
            System.out.println("IN Request: " + strFormat);
            spData = new StringParser(strFormat);
            strDate = spData.get("TransDateTime").trim();
            strDate = strDate.replaceAll("T", "");
            strDate = utility.addDate(strDate, "30", "dd");
            strDate = strDate.replaceAll("-", "");
            strDate = strDate.substring(0, 8);

            if (strData.startsWith("GET PPSubsData")) {
                strOut = "TransId=" + spData.get("TransId") + "&TransDateTime=" + spData.get("TransDateTime") + "&MSISDN=" + spData.get("MSISDN") + "&FirstCall=Y&Class=0001&BarInd=N&Language=EN&Syntax=ENGLISH&SysOffset=0500&AppOffset=2500&ServiceExpiry=" + strDate + "&AirtimeExpiry=" + strDate + "&SCPId=01&EndUserNoti=00&Status=" + status + "&Dest=" + spData.get("Dest") + "&Origin=" + spData.get("Origin");
            } else if (strData.startsWith("GET PPExpiryDates")) {
                strOut = "TransId=" + spData.get("TransId") + "&TransDateTime=" + spData.get("TransDateTime") + "&MSISDN=" + spData.get("MSISDN") + "&ServiceExpiry=" + strDate + "&AirtimeExpiry=" + strDate + "&ServiceRemoval=" + strDate + "&CreditClearance=" + strDate + "&ServPeriod=+000&AirPeriod=+000&RemovalPeriod=+090&ClearancePeriod=+090&Status=" + status + "&Dest=" + spData.get("Dest") + "&Origin=" + spData.get("Origin");
            } else if (strData.startsWith("GET PPBalance")) {
                System.out.println("Response should contain negative transaction Amount::TransAmt=-00000000120");
                strOut = "TransId=" + spData.get("TransId") + "&TransDateTime=" + spData.get("TransDateTime") + "&MSISDN=" + spData.get("MSISDN") + "&TransAmt=-00000000120&TransCurrency=RS_&ServiceExpiry=" + strDate + "&AirtimeExpiry=" + strDate + "&Status=" + status + "&Dest=" + spData.get("Dest") + "&Origin=" + spData.get("Origin");
            } else if (strData.startsWith("GET PPPayment")) {
                System.out.println("Inside PPPayment");
                try {
                    // //Thread.sleep(20000);
                } catch (Exception e) {
                    // Do nothing
                }
                strOut = "TransId=" + spData.get("TransId") + "&TransDateTime=" + spData.get("TransDateTime") + "&MSISDN=" + spData.get("MSISDN") + "&PayAmt=" + spData.get("TransAmt") + "&TransAmt=" + spData.get("TransAmt") + "&PromValue=000000000000&TransCurrency=RS_&ServiceDays=030&AirtimeDays=030&PromServiceDays=000&PromAirtimeDays=000&ServiceExpiry=20040909&AirtimeExpiry=20040909&Status=" + status + "&Origin=" + spData.get("Origin");
            } else if (strData.startsWith("GET PPAdjust")) {
                System.out.println("Inside PPAdjust");
                try {
                    // //Thread.sleep(20000);
                } catch (Exception e) {
                    // Do nothing
                }
                strOut = "TransId=" + spData.get("TransId") + "&TransDateTime=" + spData.get("TransDateTime") + "&MSISDN=" + spData.get("MSISDN") + "&PayAmt=" + spData.get("TransAmt") + "&TransAmt=" + spData.get("TransAmt") + "&PromValue=000000000000&TransCurrency=RS_&ServiceDays=030&AirtimeDays=030&PromServiceDays=000&PromAirtimeDays=000&ServiceExpiry=20040909&AirtimeExpiry=20040909&Status=" + status + "&Origin=" + spData.get("Origin");
            }
            System.out.println("strOut = " + strOut);
        } catch (Exception e) {
            // e.printStackTrace();
            // System.out.println(e.getMessage());
        }
        return strOut;
    }
}
