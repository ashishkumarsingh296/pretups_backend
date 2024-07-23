package com.apicontrollers.extgw.C2SConsentreversal;

import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.*;
import io.restassured.path.xml.XmlPath;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

public class EXTGW_C2SConsentDP extends  BaseTest {

    static String masterSheetPath;
    static int sheetRowCounter;
    private static String extentCategory = "API";
    public static HashMap<String, String> c2sMap = new HashMap<>();
    public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
    public static final String MESSAGE = "COMMAND.MESSAGE";
    private static CaseMaster CaseMaster = null;
    public static boolean TestCaseCounter = false;
    public static String CUCategory = null;

    public static HashMap<String, String> getAPIdata() throws SQLException, ParseException {
        final String methodname = "getAPIdata";
        int userCounter = 0;
        HashMap<String, String> apiData = new HashMap<String, String>();
        C2SConsentAPI C2SConsentAPI = new C2SConsentAPI();
        masterSheetPath = _masterVO.getProperty("DataProvider");
        apiData.put(C2SConsentAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
        ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        sheetRowCounter = ExcelUtility.getRowCount();
        for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
            String login_id = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter);

            // if (ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter) != null)
            if (login_id != null && !login_id.equalsIgnoreCase("")) {
                apiData.put(C2SConsentAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter));
                apiData.put(C2SConsentAPI.PASSWORD, ExcelUtility.getCellData(0, ExcelI.PASSWORD, userDetailsCounter));
                apiData.put(C2SConsentAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
                CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
                apiData.put(C2SConsentAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, userDetailsCounter)));
                apiData.put(C2SConsentAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(apiData.get(C2SConsentAPI.MSISDN), "EXTERNAL_CODE")[0]);
                break;
            } else {
                apiData.put(C2SConsentAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
                apiData.put(C2SConsentAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, userDetailsCounter)));
                apiData.put(C2SConsentAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(apiData.get(C2SConsentAPI.MSISDN), "EXTERNAL_CODE")[0]);
                CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
                break;
            }


        }


        return apiData;
    }
    public static String[] C2Stransfer() throws ParseException, SQLException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S04");
        ExtentI.Markup(ExtentColor.YELLOW,CaseMaster.getExtentCase());
        EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

       // if (TestCaseCounter == false) {
         //   test = extent.createTest(CaseMaster.getModuleCode());
           // TestCaseCounter = true;
        //}
        HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();
       // currentNode = test.createNode(CaseMaster.getExtentCase());
      //  currentNode.assignCategory(extentCategory);
        String API = C2STransferAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String[] data = new String[2];
        data[0] = xmlPath.get(EXTGWC2SAPI.TXNID);
        data[1] = apiData.get(C2STransferAPI.MSISDN2);
        return (data);
        }
    }


