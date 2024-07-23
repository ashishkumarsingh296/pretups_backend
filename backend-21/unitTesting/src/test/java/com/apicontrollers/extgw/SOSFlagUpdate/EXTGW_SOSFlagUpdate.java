package com.apicontrollers.extgw.SOSFlagUpdate;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_SOSFlagUpdate extends BaseTest{


	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	public String TxnId;
	public String extRefNo;

	/**
	 * @throws Exception 
	 * 
	 * @testid EXTGWC2STransfer01
	 * Positive Test Case For TRFCATEGORY: C2STransferStatus
	 */
	

	@Test
	public void TC_C2S01_PositiveSOSFlagUpdateAPIwithALL() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS01");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);

		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}



	@Test
	public void TC_C2S02_PositiveSOSFlagUpdateAPIwithMSISDNPIN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS02");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(extgwC2CAPI.LOGINID, "");
		apiC2CData.put(extgwC2CAPI.PASSWORD, "");
		apiC2CData.put(extgwC2CAPI.EXTCODE, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}

	@Test
	public void TC_C2S03_PositiveSOSFlagUpdateAPIwithLOGINIDPWD() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS03");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, "");
		apiC2CData.put(sosFlagUpdateAPI.PIN, "");
		apiC2CData.put(sosFlagUpdateAPI.EXTCODE, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}

	
	@Test
	public void TC_C2S04_PositiveSOSFlagUpdateAPIwithEXTCODE() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS04");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, "");
		apiC2CData.put(sosFlagUpdateAPI.PIN, "");
		apiC2CData.put(sosFlagUpdateAPI.LOGINID, "");
		apiC2CData.put(sosFlagUpdateAPI.PASSWORD, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	
	@Test
	public void TC_C2S05_PositiveSOSFlagUpdateAPIwithMSISDNandLOGIN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS05");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.EXTCODE, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	
	@Test
	public void TC_C2S06_PositiveSOSFlagUpdateAPIwithEXTCODEandLOGINID() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS06");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, "");
		apiC2CData.put(sosFlagUpdateAPI.PIN, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}

	@Test
	public void TC_C2S07_PositiveSOSFlagUpdateAPIwithEXTCODEandMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS07");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.LOGINID, "");
		apiC2CData.put(sosFlagUpdateAPI.PASSWORD, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}

	
	@Test
	public void TC_C2S08_NegativeSOSFlagUpdateAPIwithoutEXTNWCODE() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS08");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.EXTNWCODE, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}

	@Test
	public void TC_C2S09_NegativeSOSFlagUpdateAPIwithoutLOGINIDMSISDNEXTCODE() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS09");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.EXTCODE, "");
		apiC2CData.put(sosFlagUpdateAPI.LOGINID, "");
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}

	@Test
	public void TC_C2S10_NegativeSOSFlagUpdateAPIwithoutSOSALLOWED() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS10");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	
	@Test
	public void TC_C2S11_NegativeSOSFlagUpdateAPIwithoutSOSALLOWEDAMOUNT() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS11");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	
	@Test
	public void TC_C2S12_NegativeSOSFlagUpdateAPIwithoutSOSALLOWEDAMOUNT() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS12");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	
	@Test
	public void TC_C2S13_NegativeSOSFlagUpdateAPIwithoutSOSALLOWEDAMOUNT() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS13");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID, "");
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	
	@Test
	public void TC_C2S14_NegativeSOSFlagUpdateAPIinvalidEXTNWCODE() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS14");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		String CorrectNWCode = apiC2CData.get(sosFlagUpdateAPI.EXTNWCODE);

		String InValNWCode;

		do
		{
			InValNWCode = randomGeneration.randomAlphaNumeric(4);

		} while(CorrectNWCode==InValNWCode);
		
		apiC2CData.put(sosFlagUpdateAPI.EXTNWCODE,InValNWCode);
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	
	@Test
	public void TC_C2S15_NegativeSOSFlagUpdateAPIinvalidPIN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS15");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		String CorrectPin = apiC2CData.get(sosFlagUpdateAPI.PIN);

		String InValPin;

		do
		{
			InValPin = randomGeneration.randomAlphaNumeric(4);

		} while(CorrectPin==InValPin);
		
		apiC2CData.put(sosFlagUpdateAPI.PIN,InValPin);
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	

	@Test
	public void TC_C2S16_NegativeSOSFlagUpdateAPIinvalidLOGINID() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS16");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		String CorrectLoginID = apiC2CData.get(sosFlagUpdateAPI.EXTCODE);

		String InValLoginID;

		do
		{
			InValLoginID = randomGeneration.randomAlphaNumeric(4);

		} while(CorrectLoginID==InValLoginID);
		
		apiC2CData.put(sosFlagUpdateAPI.LOGINID,InValLoginID);
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}

	@Test
	public void TC_C2S17_NegativeSOSFlagUpdateAPIinvalidEXTCODE() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS17");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		String CorrectExtCode = apiC2CData.get(sosFlagUpdateAPI.EXTCODE);

		String InValExtCode;

		do
		{
			InValExtCode = randomGeneration.randomAlphaNumeric(4);

		} while(CorrectExtCode==InValExtCode);
		
		apiC2CData.put(sosFlagUpdateAPI.EXTCODE,InValExtCode);
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	
	@Test
	public void TC_C2S18_NegativeSOSFlagUpdateAPIinvalidMsisdn() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSOS18");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> C2CData = SOSFlagUpdateDP.getC2CAPIdata();

		HashMap<String, String> apiC2CData = C2CData;
		
		String C2CAPI = extgwC2CAPI.prepareAPI(apiC2CData);
		
		String[] C2CAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, C2CAPI);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, C2CAPIResponse[1]);
		String txnID = xmlPath.get(extgwC2CAPI.TXNID).toString();
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN, apiC2CData.get(extgwC2CAPI.MSISDN1));
		apiC2CData.put(sosFlagUpdateAPI.SOSTXNID,txnID);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWED,PretupsI.YES);
		apiC2CData.put(sosFlagUpdateAPI.SOSALLOWEDAMOUNT,apiC2CData.get(extgwC2CAPI.QTY));
		apiC2CData.put(sosFlagUpdateAPI.SOSTHRESHOLDLIMIT,apiC2CData.get(extgwC2CAPI.QTY));
		String CorrectMsisdn = apiC2CData.get(sosFlagUpdateAPI.MSISDN);

		String InValMsisdn;

		do
		{
			InValMsisdn = randomGeneration.randomAlphaNumeric(4);

		} while(CorrectMsisdn==InValMsisdn);
		
		apiC2CData.put(sosFlagUpdateAPI.MSISDN,InValMsisdn);
		String sosAPI = sosFlagUpdateAPI.prepareAPI(apiC2CData);
		
		String[] sosAPIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, sosAPI);
		_APIUtil.addExecutionRecord(CaseMaster, C2CAPIResponse);
		XmlPath sosxmlPath = new XmlPath(CompatibilityMode.HTML, sosAPIResponse[1]);
		Validator.messageCompare(sosxmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 
	}
	
/*

	@Test
	public void TC_C2S02_PositiveSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER02");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.LOGINID, "");
		Map.put(sosFlagUpdateAPI.PASSWORD,"");
		Map.put(sosFlagUpdateAPI.EXTCODE,"");

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S03_PositiveSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER03");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);sosFlagUpdateAPIferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.MSISDN, "");
		Map.put(sosFlagUpdateAPI.PIN,"");
		Map.put(sosFlagUpdateAPI.EXTCODE,"");
		
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_C2S04_PositiveSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER04");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.LOGINID, "");
		Map.put(sosFlagUpdateAPI.PASSWORD,"");
		Map.put(sosFlagUpdateAPI.MSISDN, "");
		Map.put(sosFlagUpdateAPI.PIN,"");

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S05_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER05");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		
		String CorrectNWCode = Map.get(sosFlagUpdateAPI.EXTNWCODE);

		String InValNWCode;

		do
		{
			InValNWCode = RandomGeneration.randomAlphaNumeric(4);

		} while(CorrectNWCode==InValNWCode);
		
		Map.put(sosFlagUpdateAPI.EXTNWCODE,InValNWCode);
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_C2S06_PositiveSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER06");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.MSISDN,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_C2S07_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER07");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.PIN,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	@Test
	public void TC_C2S08_PositiveSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER08");
	
		SOSFlagUpdateAPI c2stransferStatusAPI = new SOSFlagUpdateAPI();
	
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.LOGINID,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_C2S09_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER09");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.PASSWORD,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_C2S10_PositiveSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER10");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.EXTCODE,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	
	
	@Test
	public void TC_C2S11_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER11");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		
		String EXTCODE = Map.get(sosFlagUpdateAPI.EXTCODE);

		String InValExtCode;

		do
		{
			InValExtCode = RandomGeneration.randomNumeric(6);

		}while(EXTCODE==InValExtCode);

		
		
		Map.put(sosFlagUpdateAPI.EXTCODE,InValExtCode);
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	

	
	@Test
	public void TC_C2S12_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER12");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		
		String CorrectMSISDN = Map.get(sosFlagUpdateAPI.MSISDN);

		String InValMSISDN;

		do
		{
			InValMSISDN = RandomGeneration.randomNumeric(9);

		} while(CorrectMSISDN==InValMSISDN);

		
		
		Map.put(sosFlagUpdateAPI.MSISDN, InValMSISDN);
		Map.put(sosFlagUpdateAPI.LOGINID, "");
		Map.put(sosFlagUpdateAPI.EXTCODE,"");

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	


	
	@Test
	public void TC_C2S13_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER13");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.PASSWORD,RandomGeneration.randomNumeric(9));
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_C2S14_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER14");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		String CorrectPin = Map.get(sosFlagUpdateAPI.PIN);

		String InValPin;

		do
		{
			InValPin = RandomGeneration.randomNumeric(4);

		}while(CorrectPin==InValPin);

		Map.put(sosFlagUpdateAPI.PIN,InValPin);	

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	
	@Test
	public void TC_C2S15_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER15");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	
	
	@Test
	public void TC_C2S16_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER16");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

        TxnId= null;		
		Map.put(sosFlagUpdateAPI.EXTREFNUM, "" );
		Map.put(sosFlagUpdateAPI.MSISDN, "");
		Map.put(sosFlagUpdateAPI.PIN, "" );
		Map.put(sosFlagUpdateAPI.LOGINID, "");
		Map.put(sosFlagUpdateAPI.PASSWORD, "" );
		Map.put(sosFlagUpdateAPI.TXNID, RandomGeneration.randomNumeric(14));
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_C2S17_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER17");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
				
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		String API0 = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		
		TxnId= xmlPath0.get(EXTGWO2CAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = xmlPath0.get(EXTGWO2CAPI.EXTTXNNO);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}

	
	
	@Test
	public void TC_R_NegativeSOSFlagUpdateAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER18");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
				
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = SOSFlagUpdateDP.getAPIdata();
		
		
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		String API0 = C2CTransferAPI.prepareAPI(apiData);
		long preBalance = Long
				.parseLong(DBHandler.AccessHandler.getUserBalance(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.MSISDN2)));

		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
	
		TxnId= xmlPath0.get(C2CTransferAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = xmlPath0.get(C2CTransferAPI.EXTTXNNO);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}

	
	

 * PostPaid Bill Payment Transfer Status Test Cases	
 
	
	
	@Test
	public void TC_PPB01_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER19");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = PPBTransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGW_PPB_API.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(PPBTransferAPI.EXTREFNUM);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_PPB02_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER20");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.LOGINID, "");
		Map.put(sosFlagUpdateAPI.PASSWORD,"");
		Map.put(sosFlagUpdateAPI.EXTCODE,"");

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_PPB03_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER21");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.MSISDN, "");
		Map.put(sosFlagUpdateAPI.PIN,"");
		Map.put(sosFlagUpdateAPI.EXTCODE,"");
		
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_PPB04_PositivePPBTransferStatusAPI() throws Exception {
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER22");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.LOGINID, "");
		Map.put(sosFlagUpdateAPI.PASSWORD,"");
		Map.put(sosFlagUpdateAPI.MSISDN, "");
		Map.put(sosFlagUpdateAPI.PIN,"");

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_PPB05_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER23");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		
		String CorrectNWCode = Map.get(sosFlagUpdateAPI.EXTNWCODE);

		String InValNWCode;

		do
		{
			InValNWCode = RandomGeneration.randomAlphaNumeric(4);

		} while(CorrectNWCode==InValNWCode);
		
		Map.put(sosFlagUpdateAPI.EXTNWCODE,InValNWCode);
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_PPB06_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER24");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.MSISDN,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_PPB07_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER25");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.PIN,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	@Test
	public void TC_PPB08_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER26");
	
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
	
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();
		
		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.LOGINID,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_PPB09_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER27");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();
		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.PASSWORD,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_PPB10_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER28");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();
		
		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.EXTCODE,"");
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	
	
	@Test
	public void TC_PPB11_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER29");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		
		String EXTCODE = Map.get(sosFlagUpdateAPI.EXTCODE);

		String InValExtCode;

		do
		{
			InValExtCode = RandomGeneration.randomNumeric(6);

		}while(EXTCODE==InValExtCode);

		
		
		Map.put(sosFlagUpdateAPI.EXTCODE,InValExtCode);
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	

	
	@Test
	public void TC_PPB12_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER30");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		
		String CorrectMSISDN = Map.get(sosFlagUpdateAPI.MSISDN);

		String InValMSISDN;

		do
		{
			InValMSISDN = RandomGeneration.randomNumeric(9);

		} while(CorrectMSISDN==InValMSISDN);

		
		
		Map.put(sosFlagUpdateAPI.MSISDN, InValMSISDN);
		Map.put(sosFlagUpdateAPI.LOGINID, "");
		Map.put(sosFlagUpdateAPI.EXTCODE,"");

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	


	
	@Test
	public void TC_PPB13_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER31");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();
		
		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.PASSWORD,RandomGeneration.randomNumeric(9));
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_PPB14_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER32");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();
		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		String CorrectPin = Map.get(sosFlagUpdateAPI.PIN);

		String InValPin;

		do
		{
			InValPin = RandomGeneration.randomNumeric(4);

		}while(CorrectPin==InValPin);

		Map.put(sosFlagUpdateAPI.PIN,InValPin);	
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	
	@Test
	public void TC_PPB15_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER33");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();
		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );
		Map.put(sosFlagUpdateAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	
	
	@Test
	public void TC_PPB16_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER34");
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		TxnId= null;
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();
		Map.put(sosFlagUpdateAPI.MSISDN, "");
		Map.put(sosFlagUpdateAPI.PIN, "" );
		Map.put(sosFlagUpdateAPI.LOGINID, "");
		Map.put(sosFlagUpdateAPI.PASSWORD, "" );
		Map.put(sosFlagUpdateAPI.EXTREFNUM, "" );
		Map.put(sosFlagUpdateAPI.TXNID, RandomGeneration.randomNumeric(14));
		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_PPB17_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER35");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
				
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = SOSFlagUpdateDP.getPPBAPIdata();
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		String API0 = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		
		TxnId= xmlPath0.get(EXTGWO2CAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = xmlPath0.get(EXTGWO2CAPI.EXTTXNNO);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}


	
	
	
	
	 * GIFT RECHARGE
	 
	
	
	
	//@Test
	public void TC_GRC01_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER37");
		EXTGW_GRCAPI GRCTransferAPI = new EXTGW_GRCAPI();
		SOSFlagUpdateAPI sosFlagUpdateAPI = new SOSFlagUpdateAPI();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_GRCDP.getAPIdata();
		HashMap<String, String> Map = SOSFlagUpdateDP.getGRCAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = GRCTransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGW_GRCAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(GRCTransferAPI.EXTREFNUM);

		Map.put(sosFlagUpdateAPI.TXNID, TxnId);
		Map.put(sosFlagUpdateAPI.EXTREFNUM, extRefNo );

		String API = sosFlagUpdateAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(sosFlagUpdateAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}*/

}
