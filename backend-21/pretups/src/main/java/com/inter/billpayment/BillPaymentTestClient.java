package com.inter.billpayment;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;

public class BillPaymentTestClient {

    private String _endPoint = null;
    private Call call = null;
    private String _action = null;
    private String _msisdn = null;
    private String _amount = null;

    private String _propertiesFilePath = null;

    public static void main(String[] args) {
        BillPaymentTestClient billPayobj = new BillPaymentTestClient();
        try {
            billPayobj._propertiesFilePath = args[0].trim();
            billPayobj._action = args[1];
            billPayobj.loadInputs();
            billPayobj.setUp();
            billPayobj.testRequest();
        } catch (Exception ex) {
            System.out.println("Exception " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    public void loadInputs() {
        try {
            Properties properties = new Properties();
            File file = new File(_propertiesFilePath);// Absolute path
            properties.load(new FileInputStream(file));
            _msisdn = properties.getProperty("MSISDN");
            _endPoint = properties.getProperty("END_POINT");
            _amount = properties.getProperty("AMOUNT");

            // System.out.println("\n_msisdn:"+_msisdn+", _validity:"+_validity+", _amount:"+_amount+",_endPoint:"+_endPoint+",_userName:"+_userName+", _password:"+_password+"\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void setUp() throws Exception {
        Service service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(_endPoint);
        // set soap action
        call.setUseSOAPAction(true);
        call.setSOAPActionURI("");
        // set operation
        OperationDesc oper = new OperationDesc();
        oper.setName("payBill");
        // set input param description
        oper.addParameter(new ParameterDesc(new javax.xml.namespace.QName("", "parameters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://172.16.11.87:8080/PBTLBillPayService/PayBillService?xsd=1", "payBill"), java.lang.String.class, false, false));
        call.setOperation(oper);
        call.setOperationName(new javax.xml.namespace.QName("http://172.16.11.87:8080", "payBill"));
        call.setEncodingStyle(null);
    }

    public void testRequest() {
        try {
            String reqXml = null;
            reqXml = generateBillRequest();

            System.out.println("Request XML:" + reqXml);
            long startTime = System.currentTimeMillis();
            // Call the webservcie interface.The result is in XML format.
            String response = call.invoke(new Object[] { reqXml }).toString();
            long endTime = System.currentTimeMillis();
            double INtime = (endTime - startTime) / 1000;
            System.out.println("\nTime taken at IN for processing in seconds:" + String.valueOf(INtime));
            System.out.println("Response XML:" + response);
            HashMap map = null;
            map = parseGetAccountInfoResponse(response);
            System.out.println("Response Map: " + map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateBillRequest() {

        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            if (_action.equals("1"))
                sbf.append("<request user = 'payBill'>");
            else if (_action.equals("2"))
                sbf.append("<request user = 'retryPayment'>");
            else if (_action.equals("3"))
                sbf.append("<request user = 'rollBackPayment'>");
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
        return requestStr;
    }

    private static String getRequestID() {
        String reqId = "";
        String counter = "";
        String dateStrReqId = null;
        try {
            java.util.Date mydate = new java.util.Date();
            SimpleDateFormat sdfReqId = new SimpleDateFormat("yyyyMMddHHmmss");
            dateStrReqId = sdfReqId.format(mydate);
            reqId = "003" + dateStrReqId + counter;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reqId;
    }

    public HashMap parseGetAccountInfoResponse(String responseStr) {
        HashMap map = null;
        try {
            map = new HashMap();
            String _str = "0";
            int index = responseStr.indexOf("<response>");
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

}
