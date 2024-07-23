package com.inter.billpayment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.billpayment.stub.PayBill;

public class BillPaymentTest {

    private PayBill _stub = null;
    private String _action = null;
    private String _msisdn = null;
    private String _amount = null;
    private Stub _stubSuper = null;

    public BillPaymentTest() {
        super();
    }

    public static void main(String[] args) {
        BillPaymentTest billPaymentTest = new BillPaymentTest();
        // billPaymentTest._propertiesFilePath = args[0].trim();
        billPaymentTest._action = args[0];

        try {
            billPaymentTest.loadInputs();
            // PayBillServiceLocator payBillServiceLocator = new
            // PayBillServiceLocator();
            // billPaymentTest._stub=payBillServiceLocator.getPayBillPort(new
            // java.net.URL("http://172.16.11.169:8080/PBTLBillPayService1/PayBill"));
            BillPaymentConnectionManager serviceConnection = new BillPaymentConnectionManager("http://172.16.11.169:8080/PBTLBillPayService1/PayBillService", 5000, "COMTEST", "COMTEST");
            billPaymentTest._stub = serviceConnection.getBillPaymentClient();
            System.out.println("_stub = " + billPaymentTest._stub.toString());
            billPaymentTest._stubSuper = (Stub) billPaymentTest._stub;
            billPaymentTest._stub = (PayBill) billPaymentTest._stubSuper;
            String request = null;
            HashMap _requestMap = new HashMap();
            _requestMap.put("SERVICE_USER", "COMTEST");
            // _requestMap.put("MSISDN", "01190546115");
            _requestMap.put("MSISDN", "01199700587");
            _requestMap.put("INTERFACE_AMOUNT", "10");
            _requestMap.put("IN_TXN_ID", getRequestID());

            long startTime = System.currentTimeMillis();
            String response = "";
            System.out.println("startTime = " + Time());
            if (billPaymentTest._action.equals("1")) {

                request = new BillPaymentRequestResponseFormatter().generatePayBillRequest(_requestMap);
                System.out.println(Time() + " :: BillPay :: Request = " + request);
                response = billPaymentTest._stub.payBill(request);
            } else if (billPaymentTest._action.equals("2")) {
                request = new BillPaymentRequestResponseFormatter().generatePayBillRequest(_requestMap);
                response = billPaymentTest._stub.retryPayment(request);
            } else if (billPaymentTest._action.equals("3")) {
                request = new BillPaymentRequestResponseFormatter().generateCreditAdjustRequest(_requestMap);
                System.out.println(Time() + " :: generateValidateRequest :: Request = " + request);
                response = billPaymentTest._stub.rollBackPayment(request);
            } else if (billPaymentTest._action.equals("4")) {
                request = new BillPaymentRequestResponseFormatter().generateValidateRequest(_requestMap);
                System.out.println(Time() + " :: generateValidateRequest :: Request = " + request);
                response = billPaymentTest._stub.subInfo(request);
            } else if (billPaymentTest._action.equals("5")) {
                request = new BillPaymentRequestResponseFormatter().generateDebitAdjustRequest(_requestMap);
                System.out.println(Time() + " :: generateDebitAdjustRequest :: Request = " + request);
                response = billPaymentTest._stub.debitAdjustment(request);
            } else if (billPaymentTest._action.equals("6")) {
                request = new BillPaymentRequestResponseFormatter().generateCreditAdjustRequest(_requestMap);
                System.out.println(Time() + " :: generateCreditAdjustRequest :: Request = " + request);
                response = billPaymentTest._stub.creditAdjustment(request);
            } else if (billPaymentTest._action.equals("7")) {
                request = new BillPaymentRequestResponseFormatter().generateDepositRequest(_requestMap);
                System.out.println(Time() + " :: generateDepositRequest :: Request = " + request);
                response = billPaymentTest._stub.depositBill(request);
            }

            long endTime = System.currentTimeMillis();

            System.out.println(Time() + " ::  Response = " + response);
            // HashMap map=null;
            // map=billPaymentTest.parseGetAccountInfoResponse(response);
            // System.out.println("Response Map: "+map);
        } catch (SOAPFaultException se) {
            System.out.println("SOAPFaultException getFaultString=" + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            System.out.println("testAlepo Exception=" + e.getMessage());
            e.printStackTrace();
        }
    }

    public HashMap parseGetAccountInfoResponse(String responseStr) {
        HashMap map = null;
        try {
            map = new HashMap();
            String _str = "1";
            int index = responseStr.indexOf("<returnCode>");
            String returnCode = responseStr.substring(index + "<returnCode>".length(), responseStr.indexOf("</returnCode>", index));
            map.put("resp_returnCode", returnCode);
            if (_str.equals(returnCode)) {
                index = responseStr.indexOf("<transactionid>");
                if (index != -1) {
                    String resp_transactionSN = responseStr.substring(index + "<transactionid>".length(), responseStr.indexOf("</transactionid>", index));
                    map.put("resp_transactionSN", resp_transactionSN);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private String generateBillRequest() {

        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<request user = 'COMTEST'>");
            sbf.append("<serviceno>");
            sbf.append(_msisdn);
            sbf.append("</serviceno>");
            sbf.append("<amount>");
            sbf.append(_amount);
            sbf.append("</amount>");
            sbf.append("<transactionid>");
            sbf.append(getRequestID());
            sbf.append("</transactionid>");
            sbf.append("</request>");
            requestStr = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("XML request = " + requestStr);
        return requestStr;
    }

    private static String getRequestID() {
        String reqId = "";
        String counter = "";
        String dateStrReqId = null;
        try {
            java.util.Date mydate = new java.util.Date();
            SimpleDateFormat sdfReqId = new SimpleDateFormat("yyMMddHHss");
            dateStrReqId = sdfReqId.format(mydate);
            reqId = "003" + dateStrReqId + counter;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reqId;
    }

    public void loadInputs() throws Exception {
        try {
            // Properties properties = new Properties();
            // File file= new File(_propertiesFilePath);
            // properties.load(new FileInputStream(file));
            _msisdn = "01199700587";

            _amount = "1000";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String Time() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());

    }

}
