package com.inter.comversetr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import com.btsl.pretups.inter.comversetr.comversetrstub.BalanceCreditAccount;
import com.btsl.pretups.inter.comversetr.comversetrstub.BalanceEntity;
import com.btsl.pretups.inter.comversetr.comversetrstub.ServiceSoap;
import com.btsl.pretups.inter.comversetr.comversetrstub.SubscriberRetrieve;

public class TestCStub {
    static Properties _properties = new Properties();
    static ServiceSoap _clientStub = null;
    static HashMap _requestMap = new HashMap();
    static String _msisdn = "";
    static String _ReconID = "";
    static String _testComment = "";
    static Double _transferAmount = 0.0;
    static int _validity = 0;
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String args[]) throws IOException {
        int choice = 5;
        try {
            System.out.println("Comverse Test Program:");
            System.out.println("1:Validate");
            System.out.println("2:Crdeit");
            System.out.println("3:CreditAdjust");
            System.out.println("4:DebitAdjust");
            System.out.println("5:LMBDebitAdjust");
            System.out.println("Enter your choice:");
            choice = Integer.parseInt(br.readLine());
            if (choice > 5 || choice < 1)
                throw new Exception("Error in input");
            // choice=Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("Error while giving input");
            choice = 7;
        }
        try {
            File file = new File("/pretupshome/pretups/tomcat6_smsp_lmbcr/webapps/pretups/WEB-INF/src/com/btsl/pretups/inter/comversetr/Config.txt");
            _properties.load(new FileInputStream(file));
            String reqStr = _properties.getProperty("REQUEST");
            // _msisdn=_properties.getProperty("MSISDN");
            _ReconID = _properties.getProperty("RECONID");
            _testComment = _properties.getProperty("TESTCOMMENT");
            // _transferAmount=Double.parseDouble(_properties.getProperty("TRANSFER_AMOUNT"));
            // _validity=Integer.parseInt(_properties.getProperty("VALIDITY_DAYS"));
            _requestMap.put("WSDD_LOCATION", _properties.getProperty("WSDD_LOCATION"));
            _requestMap.put("END_URL", _properties.getProperty("END_URL"));
            _requestMap.put("READ_TIME_OUT", _properties.getProperty("READ_TIME_OUT"));
            _requestMap.put("USER_NAME", _properties.getProperty("USER_NAME"));
            _requestMap.put("SOAP_ACTION_URI", _properties.getProperty("SOAP_ACTION_URI"));
            ComverseTRConnector serviceConnection = new ComverseTRConnector(_requestMap);
            _clientStub = serviceConnection.getService();
            if (_clientStub == null) {
                System.out.println("sendRequestToIN " + "Unable to get Client Object");
                // throw new
                // BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            switch (choice) {
            case 1: {
                validateRequest();
                break;
            }
            case 2: {
                creditRequest();
                break;
            }
            case 3: {
                creditAdjustRequest();
                break;
            }
            case 4: {
                debitAdjustRequest();
                break;
            }
            case 5: {
                lmbdebitAdjustRequest();
                break;
            }
            default:
                System.out.println("Your choice parameter is wrong.Please choice out of 1,2,3,4");
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            _properties = null;
            _clientStub = null;
            _requestMap = null;
            _msisdn = null;
            _ReconID = null;
            _testComment = null;
            _transferAmount = null;
            if (br != null)
                br.close();
        }
    }

    /**
     * 
     * @throws IOException
     */
    private static void validateRequest() throws IOException {
        try {
            System.out.println("Entered validateRequest");
            System.out.println("Enter test msisdn with std code");
            _msisdn = br.readLine();
            SubscriberRetrieve subscriberRetrieve = _clientStub.retrieveSubscriberWithIdentityNoHistory(_msisdn, "", 1);
            _requestMap.put("ACCINFO_RESP_OBJ", subscriberRetrieve);
            BalanceEntity be[] = subscriberRetrieve.getSubscriberData().getBalances();
            _requestMap.put("IN_LANG", subscriberRetrieve.getSubscriberData().getNotificationLanguage());
            _requestMap.put("IN_CURRENCY", subscriberRetrieve.getSubscriberData().getCurrencyCode());
            _requestMap.put("ACCOUNT_STATUS", subscriberRetrieve.getSubscriberData().getCurrentState());
            _requestMap.put("SERVICE_CLASS", subscriberRetrieve.getSubscriberData().getCOSName());
            _requestMap.put("AON", Long.toString(subscriberRetrieve.getSubscriberData().getDateEnterActive().getTimeInMillis()));
            System.out.println("Date formed:" + new Date(Long.parseLong((String) _requestMap.get("AON"))));
            for (int i = 0, j = be.length; i < j; i++) {
                try {
                    _requestMap.put("OLD_EXPIRY_DATE" + i, be[i].getAccountExpiration());
                    _requestMap.put("CAL_OLD_EXPIRY_DATE" + i, be[i].getAccountExpiration());
                    _requestMap.put("RESP_BALANCE" + i, Double.valueOf(be[i].getBalance()));
                    _requestMap.put("BALANCE_NAME" + i, be[i].getBalanceName());
                    System.out.println("BALANCE_NAME=" + be[i].getBalanceName());
                    System.out.println("OLD_EXPIRY_DATE =" + be[i].getAccountExpiration().getTime());
                    System.out.println("CAL_OLD_EXPIRY_DATE=" + be[i].getAccountExpiration().getTime());
                    System.out.println("RESP_BALANCE=" + Double.valueOf(be[i].getBalance()));
                } catch (Exception bc) {
                    bc.printStackTrace();
                    // TODO: handle exception
                }
            }
            System.out.println("Exited validateRequest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @throws IOException
     */
    private static void creditRequest() throws IOException {
        try {
            System.out.println("Entered creditRequest");
            System.out.println("Enter test msisdn with std code");
            _msisdn = br.readLine();
            System.out.println("Enter test transfer amount");
            _transferAmount = Double.parseDouble(br.readLine());
            System.out.println("Enter test validity");
            _validity = Integer.parseInt(br.readLine());
            boolean rechargeStatus = _clientStub.nonVoucherRecharge(_msisdn, "", _transferAmount, _validity, _ReconID + _testComment);
            System.out.println("Recharge Status of MSISDN=" + _msisdn + ",for Transferred Amount=" + _transferAmount + "is " + rechargeStatus);
            System.out.println("Exited creditRequest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @throws Exception
     */
    private static void creditAdjustRequest() throws Exception {
        try {
            System.out.println("Entered creditAdjustRequest");
            try {
                System.out.println("Enter test msisdn with std code");
                _msisdn = br.readLine();
                System.out.println("Enter test transfer amount");
                _transferAmount = Double.parseDouble(br.readLine());
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(new Date(_properties.getProperty("CAL_OLD_EXPIRY_DATE")).getTime());
                BalanceCreditAccount bc = new BalanceCreditAccount();
                bc.setCreditValue(_transferAmount);
                bc.setBalanceName(_properties.getProperty("CORE_BAL_NAME"));
                bc.setExpirationDate(cal);
                BalanceCreditAccount bcarr[] = new BalanceCreditAccount[] { bc };
                _requestMap.put("CR_ADJ_REQ_OBJ", bcarr);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            boolean creditStatus = _clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("CR_ADJ_REQ_OBJ"), "", _ReconID + _testComment);
            System.out.println("Credit Adjust Status of MSISDN=" + _msisdn + ",for Transferred Amount=" + _transferAmount + "is " + creditStatus);
            System.out.println("Exited creditAdjustRequest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @throws Exception
     */
    private static void debitAdjustRequest() throws Exception {
        try {
            System.out.println("Entered debitAdjustRequest");
            try {
                System.out.println("Enter test msisdn with std code");
                _msisdn = br.readLine();
                System.out.println("Enter test transfer amount");
                _transferAmount = Double.parseDouble(br.readLine());
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(new Date(_properties.getProperty("CAL_OLD_EXPIRY_DATE")).getTime());
                BalanceCreditAccount bc = new BalanceCreditAccount();
                bc.setCreditValue(-(_transferAmount));
                bc.setBalanceName(_properties.getProperty("CORE_BAL_NAME"));
                bc.setExpirationDate(cal);
                BalanceCreditAccount bcarr[] = new BalanceCreditAccount[] { bc };
                _requestMap.put("DR_ADJ_REQ_OBJ", bcarr);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            boolean creditStatus = _clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("DR_ADJ_REQ_OBJ"), "", _ReconID + _testComment);
            System.out.println("Debit Adjust Status of MSISDN=" + _msisdn + ",for Transferred Amount=" + _transferAmount + "is " + creditStatus);
            System.out.println("Exited debitAdjustRequest");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 
     * @throws Exception
     */
    private static void lmbdebitAdjustRequest() throws Exception {
        try {
            System.out.println("Entered lmbdebitAdjustRequest");
            try {
                System.out.println("Enter test msisdn with std code");
                _msisdn = br.readLine();
                System.out.println("Enter test transfer amount");
                _transferAmount = Double.parseDouble(br.readLine());
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(new Date(_properties.getProperty("CAL_OLD_EXPIRY_DATE")).getTime());
                BalanceCreditAccount bc = new BalanceCreditAccount();
                bc.setCreditValue(-(_transferAmount));
                bc.setBalanceName(_properties.getProperty("LMB_BAL_NAME"));
                bc.setExpirationDate(cal);
                BalanceCreditAccount bcarr[] = new BalanceCreditAccount[] { bc };
                _requestMap.put("DR_ADJ_REQ_OBJ", bcarr);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            boolean creditStatus = _clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("DR_ADJ_REQ_OBJ"), "", _ReconID + _testComment);
            System.out.println("Debit Adjust Status of MSISDN=" + _msisdn + ",for Transferred Amount=" + _transferAmount + "is " + creditStatus);
            System.out.println("Exited lmbdebitAdjustRequest");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
