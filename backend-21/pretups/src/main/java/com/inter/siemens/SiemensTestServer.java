package com.inter.siemens;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @(#)SiemensTestServer.java
 *                            Copyright(c) 2006, Bharti Telesoft Int. Public
 *                            Ltd.
 *                            All Rights Reserved
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Ashish Kumar Jun 16,2006 Initial Creation
 *                            --------------------------------------------------
 *                            ----------------------------------------------
 *                            This class is used to Simulate the Siemens Server.
 *                            Server give the response based on the request.
 */
public class SiemensTestServer extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String _siemensResponseDirPath;

    public void init(ServletConfig conf) throws ServletException {
        System.out.println("SiemensTestServer init() Entered ");
        super.init(conf);
        _siemensResponseDirPath = getServletContext().getRealPath(getInitParameter("siemensresponsedirpath"));
        System.out.println("Siemens Response File Path=" + _siemensResponseDirPath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("SiemensTestServer", "Entered ");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        BufferedReader in = null;
        StringBuffer strBuff = null;
        // StringBuffer message = null;
        // HashMap map = null;
        try {
            // SiemensTestRequestParser parser = new SiemensTestRequestParser();
            // String strReq="";
            // message = new StringBuffer();
            // Reading the request using buffered reader and getting the
            // InputStream from request object.
            /*
             * BufferedReader bufferedReader = new BufferedReader(new
             * InputStreamReader(request.getInputStream()));
             * while ((strReq = bufferedReader.readLine()) != null)
             * message.append(strReq);
             */
            // int c = 0;
            String purpose = request.getParameter("Purpose");
            String action = purpose.substring(0, purpose.indexOf(";"));
            if (_log.isDebugEnabled())
                _log.debug("doPost", "GET PARAMETER ::" + action);

            /*
             * String message = "";
             * BufferedReader bufferedReader = new BufferedReader(new
             * InputStreamReader(request.getInputStream()));
             * while ((c = bufferedReader.read()) != -1)
             * {
             * 
             * message += (char) c;
             * }
             * if
             * (_log.isDebugEnabled())_log.debug("doPost","message = "+message);
             */
            // Getting the index of &Purpose in request string and determines
            // which action(accountInfo,credit or debit)
            // to be performed for parsing.
            // int index1 = message.indexOf("&Purpose=");
            // String action =
            // (message.substring(index1+"&Purpose=".length(),message.indexOf(";",index1))).trim();
            if (_log.isDebugEnabled())
                _log.debug("doPost", "action::" + action);
            // Create an instance of appropriate file,File is selected based on
            // the action has been taken.
            // Directory name is provided by the init parameter.
            // If the request is for Account information then action = PT01 and
            // response file will be PT01.txt
            // If the request is for Debit then action =PT02 and response file
            // will be PT02.txt
            // If the request if for Credit then action=PT03 and response file
            // will be PT03.txt
            try {
                File file = new File(_siemensResponseDirPath + File.separator + action.trim() + ".txt");
                if (_log.isDebugEnabled())
                    _log.debug("doPost", "file = " + file);
                in = new BufferedReader(new FileReader(file));
                String str = "";
                strBuff = new StringBuffer();
                while ((str = in.readLine()) != null)
                    strBuff.append(str);
            }// end of try block.
            catch (IOException ioe) {
                ioe.printStackTrace();
                _log.error("doPost", "IOException ioe = " + ioe.getMessage());
            }// end of catch
            responseStr = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("doPost", "Before replacing the transaction id from resposnse responseStr = " + responseStr);
            int index = responseStr.indexOf("TransactionID=");
            // TransactionId
            String transactionID = responseStr.substring(index + "TransactionID=".length(), responseStr.indexOf("ExecutionStatus=", index));
            System.out.println("transactionID from Responose = " + transactionID.trim());
            /*
             * if("PT01".equals(action))
             * map=parser.parseGetAccountInfoRequest(message.toString());
             * else if("PT02".equals(action))
             * map=parser.parseImmediateDebitRequest(message.toString());
             * else
             * map=parser.parseRechargeCreditRequest(message.toString());
             * if(_log.isDebugEnabled())_log.debug("doPost",
             * "After parsing request string map = "+map);
             * //AccountType
             */
            // if(_log.isDebugEnabled())
            // _log.debug("doPost"," TransactionID (String)map.get(\"AccountType\") = "+(String)map.get("AccountType"));
            // if(_log.isDebugEnabled())
            // _log.debug("doPost"," TransactionID (String)map.get(\"TransactionId\") = "+(String)map.get("TransactionId"));

            // String string =
            // responseStr.replaceAll(transactionID.trim(),(String)map.get("TransactionId"));
            String string = responseStr.replaceAll(transactionID.trim(), request.getParameter("TransactionId"));
            Thread.sleep(200);
            if (_log.isDebugEnabled())
                _log.debug("doPost", "After replacing the transaction id from resposnse string = " + string);
            out.print(string);
        }// end of try-block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("doPost", "Exception e = " + e.getMessage());
        }// end of catch-Exception
        finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
            }
        }// end of finally
    }// end of doPost
}
