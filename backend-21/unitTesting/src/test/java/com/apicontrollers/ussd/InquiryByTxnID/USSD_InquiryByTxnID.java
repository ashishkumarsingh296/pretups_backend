package com.apicontrollers.ussd.InquiryByTxnID;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.C2CTransfer;
import com.Features.C2STransfer;
import com.Features.O2CTransfer;
import com.Features.mapclasses.OperatorToChannelMap;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CDP;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGW_C2SDAO;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CDP;
import com.apicontrollers.extgw.o2ctransfer.EXTGW_O2CDAO;
import com.apicontrollers.ussd.Transfer.USSD_TRANSFER_API;
import com.apicontrollers.ussd.Transfer.USSD_TRANSFER_DP;
import com.apicontrollers.ussd.c2stransfer.USSDC2SAPI;
import com.apicontrollers.ussd.c2stransfer.USSDC2SDP;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_InquiryByTxnID extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";
    static String directO2CPreference;
    private String Exisiting_TXN_No = null;

    @Test//C2S WEB
    public void TCA1_PositiveINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ01");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDC2SDP.getAPIdata();
        C2STransfer c2STransfer = new C2STransfer(driver);
        String txnID = c2STransfer.performC2STransfer(dataMap.get("parentCategory"), dataMap.get("category"), dataMap.get("PIN"), dataMap.get("service"), dataMap.get("AMOUNT"), dataMap.get("MSISDN2"));

        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", txnID);
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCB2_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ02");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", "");
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCC3_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ03");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("MSISDN1", "");
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCD4_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ04");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("PIN", "");
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCE5_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ05");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("PIN", randomGeneration.randomNumeric(4));
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCF6_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ06");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("MSISDN1", randomGeneration.randomNumeric(9));
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCG7_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ07");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", randomGeneration.randomNumeric(9));
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test// O2C WEB
    public void TCH8_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ08");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
        HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
        O2CTransfer o2CTransfer = new O2CTransfer(driver);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"), _masterVO.getMasterValue(MasterI.NETWORK_CODE));
        directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        transferMap = o2CTransfer.initiateO2CTransfer(transferMap);
        if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
            o2CTransfer.performingLevel1Approval(transferMap);
        long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov)
            o2CTransfer.performingLevel2Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));
        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > secondApprov)
            o2CTransfer.performingLevel3Approval(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"));

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", transferMap.get("TRANSACTION_ID"));
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test//O2C EXTGW
    public void TCI9_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ09");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        Object[] dataObject = EXTGWO2CDP.getAPIdataWithAllUsers();
        EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) dataObject[0];
        HashMap<String, String> apiDataO2C = APIDAO.getApiData();
        Exisiting_TXN_No = apiDataO2C.get(O2CTransferAPI.EXTTXNNUMBER);
        String API_o2c = O2CTransferAPI.prepareAPI(apiDataO2C);
        String[] APIResponse_o2c = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API_o2c);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse_o2c);

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", Exisiting_TXN_No);
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test//C2S EXTGW
    public void TCJ10_PositiveINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ10");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        Object[] dataObject = EXTGWC2SDP.getAPIdataWithAllUsers();
        EXTGW_C2SDAO APIDAO = (EXTGW_C2SDAO) dataObject[0];
        HashMap<String, String> apiDataC2S = APIDAO.getApiData();
        String API_C2S = C2STransferAPI.prepareAPI(apiDataC2S);
        String[] APIResponse_C2S = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API_C2S);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse_C2S);
        XmlPath xmlPath_c2s = new XmlPath(CompatibilityMode.HTML, APIResponse_C2S[1]);
        String txnID = xmlPath_c2s.get(EXTGWC2SAPI.TXNID).toString();
        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", txnID);
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test//C2S USSD
    public void TCK11_PositiveINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ11");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        USSDC2SAPI C2STransferAPI = new USSDC2SAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> apiDataC2S = USSDC2SDP.getAPIdata();
        String API_C2S = C2STransferAPI.prepareAPI(apiDataC2S);
        String[] APIResponse_C2S = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API_C2S);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse_C2S);
        XmlPath xmlPath_c2s = new XmlPath(CompatibilityMode.HTML, APIResponse_C2S[1]);
        String txnID = xmlPath_c2s.get(USSDC2SAPI.TXNID).toString();
        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", txnID);
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test//C2C USSD
    public void TCL12_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ12");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        USSD_TRANSFER_API transferAPI = new USSD_TRANSFER_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> apiDataC2C = USSD_TRANSFER_DP.getAPIdata();
        String API_C2C = transferAPI.prepareAPI(apiDataC2C);
        String[] APIResponse_C2C = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API_C2C);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse_C2C);
        XmlPath xmlPath_c2c = new XmlPath(CompatibilityMode.HTML, APIResponse_C2C[1]);
        String txnID = xmlPath_c2c.get(USSD_TRANSFER_API.TXNID).toString();
        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", txnID);
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test//C2C EXTGW
    public void TCL13_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ13");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> apiDataC2C = EXTGWC2CDP.getAPIdata();
        String API_C2C = C2CTransferAPI.prepareAPI(apiDataC2C);
        String[] APIResponse_C2C = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API_C2C);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse_C2C);
        XmlPath xmlPath_c2c = new XmlPath(CompatibilityMode.HTML, APIResponse_C2C[1]);
        String txnID = xmlPath_c2c.get(EXTGWC2CAPI.TXNID).toString();
        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", txnID);
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test//C2C WEB
    public void TCL14_NegativeINQAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINQ14");
        USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
        C2CTransfer c2CTransfer = new C2CTransfer(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> apiDataC2C = USSD_TRANSFER_DP.getAPIdata();
        apiDataC2C = c2CTransfer.channel2channelTransfer(apiDataC2C.get("FROM_CATEGORY"), apiDataC2C.get("TO_CATEGORY"), apiDataC2C.get("MSISDN2"), apiDataC2C.get("PIN"));
        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_Inquiry_DP.getAPIdata();
        apiData.put("TXNID", apiDataC2C.get("TransactionID"));
        String API = inquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(inquiryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

}
