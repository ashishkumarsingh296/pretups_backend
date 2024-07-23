package com.apicontrollers.extgw.ViewChannelUser;

import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

import java.util.HashMap;

public class XMLGW_VIEWCUSERDP extends BaseTest {



    public static HashMap<String, String> getAPIdata() throws Exception {

        RandomGeneration randomGeneration = new RandomGeneration();
        HashMap<String, String> apiData = new HashMap<String, String>();

        apiData.put(XMLGW_VIEWCUSERAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCounter = ExcelUtility.getRowCount();
        for (int k = 0; k <= rowCounter; k++) {
            String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k);
            String UserCategory = "BCU";

            if (excelCategory.equals(UserCategory)) {
                apiData.put(XMLGW_VIEWCUSERAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k));
                apiData.put(XMLGW_VIEWCUSERAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, k)));
                apiData.put(XMLGW_VIEWCUSERAPI.CATCODE, ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k));
                apiData.put(XMLGW_VIEWCUSERAPI.EMPCODE, DBHandler.AccessHandler.getEmpCode(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k)));
                break;
            }
        }
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        apiData.put(XMLGW_VIEWCUSERAPI.USERLOGINID, ExcelUtility.getCellData(0,ExcelI.LOGIN_ID,1));
        apiData.put(XMLGW_VIEWCUSERAPI.MSISDN, ExcelUtility.getCellData(0,ExcelI.MSISDN,1));
        apiData.put(XMLGW_VIEWCUSERAPI.EXTREFNUM,randomGeneration.randomNumeric(9) );
        apiData.put(XMLGW_VIEWCUSERAPI.DATE, _APIUtil.getCurrentTimeStamp());
        return apiData;
    }


    }
