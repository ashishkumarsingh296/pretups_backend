package com.apicontrollers.ussd.P2PAccInfo;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_AccInfo extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    /**
     * @throws Exception
     * @testid USSDC2S01
     * Positive Test Case For TRFCATEGORY: PRC
     */
    @Test
    public void _01_TC_A_PositiveUSSD_ACCINFO_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDACC01");
        USSD_AccInfoAPI AccInfoAPI = new USSD_AccInfoAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSD_AccInfo_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = AccInfoAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(AccInfoAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
		
	
		
		
	/*	@Test(priority=2)
		public void TC_B_PositiveUSSD_ACCINFO_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDACC02");
			USSD_AccInfoAPI AccInfoAPI = new USSD_AccInfoAPI();
			String MSISDN = null;

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = USSD_AccInfo_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			MSISDN = DBHandler.AccessHandler.getP2PSubscriberMSISDN("POST","Y" );
			

			dataMap.put(AccInfoAPI.MSISDN1,MSISDN);
			String API = AccInfoAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(AccInfoAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			
		}*/


    @Test
    public void _03_TC_A_NegativeUSSD_ACCINFO_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDACC03");
        USSD_AccInfoAPI AccInfoAPI = new USSD_AccInfoAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSD_AccInfo_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(AccInfoAPI.MSISDN1, "");
        String API = AccInfoAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(AccInfoAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void _04_TC_D_Negative_USSD_ACCINFO_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDACC04");
        USSD_AccInfoAPI AccInfoAPI = new USSD_AccInfoAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSD_AccInfo_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(AccInfoAPI.SELECTOR, "");
        String API = AccInfoAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(AccInfoAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
}
