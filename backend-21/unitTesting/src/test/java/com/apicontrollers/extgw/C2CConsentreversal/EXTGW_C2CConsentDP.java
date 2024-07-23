package com.apicontrollers.extgw.C2CConsentreversal;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CDP;
import com.apicontrollers.extgw.c2ctransfer.EXTGW_C2CTransfer;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import io.restassured.path.xml.XmlPath;
import org.apache.commons.lang3.ObjectUtils;
import com.utils.ExtentI;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.commons.PretupsI;


public class EXTGW_C2CConsentDP extends BaseTest {
    static String masterSheetPath;
    static int sheetRowCounter;
    private static String extentCategory = "API";
    public static HashMap<String, String> c2cMap = new HashMap<>();

    public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
    public static final String MESSAGE = "COMMAND.MESSAGE";
    private static CaseMaster CaseMaster = null;
    public static boolean TestCaseCounter = false;

    public static HashMap<String, String> getAPIdata() throws SQLException, ParseException {
        final String methodname = "getAPIdata";
        int userCounter = 0;
        HashMap<String, String> apiData = new HashMap<String, String>();
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        masterSheetPath = _masterVO.getProperty("DataProvider");
        apiData.put(C2CConsentAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
        ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        sheetRowCounter = ExcelUtility.getRowCount();


        for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
            String login_id = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter);

            // if (ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter) != null)
            if (login_id != null && !login_id.equalsIgnoreCase("")) {
                apiData.put(C2CConsentAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter));
                apiData.put(C2CConsentAPI.PASSWORD, ExcelUtility.getCellData(0, ExcelI.PASSWORD, userDetailsCounter));
                apiData.put(C2CConsentAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
                apiData.put(C2CConsentAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, userDetailsCounter)));
                apiData.put(C2CConsentAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(apiData.get(C2CConsentAPI.MSISDN), "EXTERNAL_CODE")[0]);
                break;
            } else {
                apiData.put(C2CConsentAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
                apiData.put(C2CConsentAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, userDetailsCounter)));
                apiData.put(C2CConsentAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(apiData.get(C2CConsentAPI.MSISDN), "EXTERNAL_CODE")[0]);
                break;
            }


        }


        return apiData;
    }
        public static String[] C2Ctransfer() throws ParseException, SQLException {


            CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C01");
            ExtentI.Markup(ExtentColor.YELLOW,CaseMaster.getExtentCase());
            EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
            HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
            businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), apiData.get(C2CTransferAPI.MSISDN1), apiData.get(C2CTransferAPI.MSISDN2));
            TransactionVO TransactionVO = businessController.preparePreTransactionVO();
            TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
            HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
            initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

            String API = C2CTransferAPI.prepareAPI(apiData);
            apiData.put(C2CTransferAPI.LOGINID, "");
            apiData.put(C2CTransferAPI.PASSWORD, "");

            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
          _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
            Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            String [] data = new String[2];
            data [0] = xmlPath.get(EXTGWC2CAPI.TXNID);
            data [1] = apiData.get(C2CTransferAPI.MSISDN2);



            /*
             * Test Case to validate Network Stocks after successful O2C Transfer
             */
          //  currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
           // currentNode.assignCategory("Smoke");
            TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
            BusinessValidator.validateStocks(TransactionVO);

            /*
             * Test Case to validate Channel User balance after successful O2C Transfer
             */
          //  currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
          //  currentNode.assignCategory("Smoke");
            BusinessValidator.validateUserBalances(TransactionVO);
            return (data);

        }


    }



