package com.apicontrollers.extgw.selfcarebundle;

import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGW_C2SDAO;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EXTGW_SELFCAREBUNDLE_DP extends CaseMaster {

    public static String CUCategory = null;
    public static String TCPName = null;
    public static String TCPID = null;
    public static String Domain = null;
    public static String ProductCode = null;
    public static String ProductName = null;
    public static String LoginID = null;
    public static String CPName = null;
    public static String parentCategory = null;
    public static String grade = null;


    public static HashMap<String, String> getAPIdata() {

        /*
         * Variable Declaration
         */
        HashMap<String, String> apiData = new HashMap<String, String>();
        EXGTW_SELFCAREBUNDLEAPI exgtwSelfcareBundleApi = new EXGTW_SELFCAREBUNDLEAPI();
        GenerateMSISDN gnMsisdn = new GenerateMSISDN();
        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        String Product_Type = _masterVO.getProperty("PrepaidProductType");
        String channelUserCategory = null;
        int dataRowCounter = 0;

        /*
         * Object Declaration
         */
        RandomGeneration RandomGeneration = new RandomGeneration();

        /*
         * Variable initializations
         */
        apiData.put(exgtwSelfcareBundleApi.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
        dataRowCounter = ExcelUtility.getRowCount();
        for (int i = 0; i <= dataRowCounter; i++) {
            String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> alist = new ArrayList<String>(Arrays.asList(categoryServices.split("[ ]*,[ ]*")));
            String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
            ArrayList<String> alist1 = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
            if (alist.contains(ProductRechargeCode) && alist1.contains("EXTGW")) {
                channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
                break;
            }
        }

        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        dataRowCounter = ExcelUtility.getRowCount();
        for (int i = 0; i <= dataRowCounter; i++) {
            String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);

            if (excelCategory.equals(channelUserCategory)) {
                apiData.put(exgtwSelfcareBundleApi.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
                apiData.put(exgtwSelfcareBundleApi.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
                LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
                TCPID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
                Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
                CPName = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
                parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                grade = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
                apiData.put(exgtwSelfcareBundleApi.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
                apiData.put(exgtwSelfcareBundleApi.PASSWORD, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
                apiData.put(exgtwSelfcareBundleApi.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
                break;
            }
        }

        apiData.put(exgtwSelfcareBundleApi.EXTREFNUM, RandomGeneration.randomNumeric(10));
        apiData.put(exgtwSelfcareBundleApi.DATE, _APIUtil.getCurrentTimeStamp());

        apiData.put(exgtwSelfcareBundleApi.AMOUNT, "10");
        apiData.put(exgtwSelfcareBundleApi.PAYMENTTYPE, "Pre-charged");
        apiData.put(exgtwSelfcareBundleApi.BONUSAMOUNT,"10");

        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        for (int rownum = 1; rownum <= rowCount; rownum++) {
            String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                apiData.put(exgtwSelfcareBundleApi.PAYMENTMODE, ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));
                break;
            }
        }

        apiData.put(exgtwSelfcareBundleApi.PAYMENTINFO, "Payment Info Self care");
        apiData.put(exgtwSelfcareBundleApi.PROMOCODE, "42271");
        apiData.put(exgtwSelfcareBundleApi.FLAG, "False");
        apiData.put(exgtwSelfcareBundleApi.BONUSREFILLID, RandomGeneration.randomAlphaNumeric(5));
        String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);

        String subscriberNumber = prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
        apiData.put(exgtwSelfcareBundleApi.MSISDN2, subscriberNumber);
        apiData.put(exgtwSelfcareBundleApi.BENEFICIARYMSIDN, subscriberNumber);
        apiData.put(exgtwSelfcareBundleApi.PAYEEMSISDN, subscriberNumber);


        return apiData;
    }



    public static Object[] getAPIdataWithAllUsers() {

        // Variable Declaration


        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        String Product_Type = _masterVO.getProperty("PrepaidProductType");
        int dataRowCounter = 0;
        int objSize=0;
        //Object Declaration

        RandomGeneration RandomGeneration = new RandomGeneration();
        GenerateMSISDN gnMsisdn = new GenerateMSISDN();
        EXGTW_SELFCAREBUNDLEAPI exgtwSelfcareBundleApi = new EXGTW_SELFCAREBUNDLEAPI();
        //Variable initializations


        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
        dataRowCounter = ExcelUtility.getRowCount();
        for (int i=0; i <= dataRowCounter; i++) {
            String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
            if (categoryServices.contains(ProductRechargeCode) && gatewayType.contains("EXTGW") && !ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i).equalsIgnoreCase("Subscriber")) {
                objSize++;
            }
        }

        Object[] apiDataObj = new Object[objSize];
        int objCounter = 0;


        for (int counter = 0; counter <= dataRowCounter; counter++) {
            HashMap<String, String> apiData = new HashMap<String, String>();
            apiData.put(exgtwSelfcareBundleApi.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
            ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
            String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, counter);
            String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, counter);

            if (categoryServices.contains(ProductRechargeCode) && gatewayType.contains("EXTGW")&& !ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, counter).equalsIgnoreCase("Subscriber")) {

                EXTGW_SELFCAREBUNDLEDAO APIDataDAO = new EXTGW_SELFCAREBUNDLEDAO();
                String channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, counter);

                ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
                int CURowCounter = ExcelUtility.getRowCount();
                for (int i = 0; i<=CURowCounter;i++) {
                    String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                    if (excelCategory.equals(channelUserCategory)) {
                        apiData.put(exgtwSelfcareBundleApi.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
                        apiData.put(exgtwSelfcareBundleApi.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
                        apiData.put(exgtwSelfcareBundleApi.PASSWORD, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
                        LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                        apiData.put(exgtwSelfcareBundleApi.LOGINID,ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
                        APIDataDAO.setLoginID(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
                        APIDataDAO.setCategory(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
                        APIDataDAO.setTCPName(ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i));
                        APIDataDAO.setDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i));
                        apiData.put(exgtwSelfcareBundleApi.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
                        break;
                    }
                }


                ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
                int rowCount1 = ExcelUtility.getRowCount();
                System.out.println(Product_Type);
                for (int k = 1; k<=rowCount1;k++) {
                    String ExcelProductType = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, k);

                    if(ExcelProductType.equals(Product_Type)){

                        APIDataDAO.setProductCode(ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, k));
                        APIDataDAO.setProductName(ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, k));

                        break;
                    }
                }

                apiData.put(exgtwSelfcareBundleApi.EXTREFNUM, RandomGeneration.randomNumeric(10));
                apiData.put(exgtwSelfcareBundleApi.DATE, _APIUtil.getCurrentTimeStamp());

                apiData.put(exgtwSelfcareBundleApi.AMOUNT, "100");
                apiData.put(exgtwSelfcareBundleApi.PAYMENTTYPE, "Pre-charged");
                apiData.put(exgtwSelfcareBundleApi.BONUSAMOUNT,"10");

                apiData.put(exgtwSelfcareBundleApi.PAYMENTINFO, "Payment Info Self care");
                apiData.put(exgtwSelfcareBundleApi.PROMOCODE, "42271");
                apiData.put(exgtwSelfcareBundleApi.FLAG, "False");
                apiData.put(exgtwSelfcareBundleApi.BONUSREFILLID, RandomGeneration.randomAlphaNumeric(5));
                String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);

                String subscriberNumber = prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
                apiData.put(exgtwSelfcareBundleApi.MSISDN2, subscriberNumber);
                apiData.put(exgtwSelfcareBundleApi.BENEFICIARYMSIDN, subscriberNumber);
                apiData.put(exgtwSelfcareBundleApi.PAYEEMSISDN, subscriberNumber);

                /*
                ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
                int rowCount = ExcelUtility.getRowCount();
                for (int rownum = 1; rownum <= rowCount; rownum++) {
                    String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
                    String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
                    if (service.equals(CustomerRechargeCode)&& !cardGroupName.isEmpty()) {

                        apiData.put(C2STransferAPI.SELECTOR,DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum),service));
                        break;
                    }
                }
                */

                APIDataDAO.setApiData(apiData);
                apiDataObj[objCounter] = (EXTGW_SELFCAREBUNDLEDAO) APIDataDAO;
                objCounter++;
            }
        }

        return apiDataObj;
    }



}
