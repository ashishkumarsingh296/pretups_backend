package com.apicontrollers.extgw.Loan;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.*;

import java.io.IOException;
import java.util.HashMap;

public class OptOutDP {

        public static String CUCategory = null;
        public static String TCPName = null;
        public static String Domain = null;
        public static String ProductCode = null;
        public static String LoginID = null;
        public static String nPIN = null;
        public static int rowNum;
        public static HashMap<String, String> getAPIdata() {

            HashMap<String, String> apiData = new HashMap<String, String>();
            OptOutAPI OptOutAPI = new OptOutAPI();
            RandomGeneration rndgen = new RandomGeneration();
            String masterSheetpath= _masterVO.getProperty("DataProvider");
            ExcelUtility.setExcelFile(masterSheetpath, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
            int rowCount = ExcelUtility.getRowCount();
            int i=0;
            for(i=1;i<=rowCount;i++){
                if(ExcelUtility.getCellData(0, ExcelI.EXTGW, i).equals("Y")&&
                        (ExcelUtility.getCellData(0, ExcelI.EXTGW, i)!=null ||
                                !ExcelUtility.getCellData(0, ExcelI.EXTGW, i).equals("")) && !ExcelUtility.getCellData(0, ExcelI.CATEGORY_USERS, i).equalsIgnoreCase("Operator"))
                {break;
                }
            }
            String category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_USERS, i);

            rowNum=0;
            try {
                rowNum=ExcelUtility.searchStringRowNum(masterSheetpath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, category);
            } catch (IOException e) {
                e.printStackTrace();
            }

            nPIN = new CommonUtils().isSMSPinValid();
            ExcelUtility.setExcelFile(masterSheetpath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            apiData.put(OptOutAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
            apiData.put(OptOutAPI.MSISDN, ExcelUtility.getCellData(0,ExcelI.MSISDN,rowNum));
            apiData.put(OptOutAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0,ExcelI.PIN,rowNum)));
            apiData.put(OptOutAPI.EXTREFNUM, rndgen.randomAlphaNumeric(10));
            apiData.put(OptOutAPI.LOGINID, ExcelUtility.getCellData(0,ExcelI.LOGIN_ID,rowNum));
            apiData.put(OptOutAPI.PASSWORD, ExcelUtility.getCellData(0,ExcelI.PASSWORD,rowNum));
            apiData.put(OptOutAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
            return apiData;
        }

    }


