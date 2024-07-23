package com.apicontrollers.extgw.AlternateNumber;


import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_AddAlternateNumber extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/*
	 * @throws Exception 
	 * @test Id EXTGWADDALTNMBR01
	 * ADD ALTERNATE SUCCESSFULL WITH ALL CORRECT DATA
	 */
	
	@Test
	public void _01_AddAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWADDALTNMBR01");
		EXTGWADDALTNUMERAPI addAlternateNumberAPI = new EXTGWADDALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWADDALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
			
		// get alternate msisdn
				String [] arr= DBHandler.AccessHandler.getdetailsfromUsersTable(apiData.get(EXTGWADDALTNUMERAPI.MSISDN), "alternate_msisdn");
								
				// if the alter msisdn is not null then null it.
				if(!BTSLUtil.isNullString(arr[0]))
				DBHandler.AccessHandler.updateAnyColumnValue("users", "alternate_msisdn", "", "msisdn", apiData.get(EXTGWADDALTNUMERAPI.MSISDN));
		
		String API = addAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWADDALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	/*
	 * @throws Exception 
	 * @test Id EXTGWADDALTNMBR01
	 * ADD ALTERNATE SUCCESSFULL LOGIN ID AND PASSWORD ONLY
	 */
	
	@Test
	public void _02_AddAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWADDALTNMBR02");
		EXTGWADDALTNUMERAPI addAlternateNumberAPI = new EXTGWADDALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
	
	
		
		HashMap<String, String> apiData = EXTGWADDALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		// get alternate msisdn
		String [] arr= DBHandler.AccessHandler.getdetailsfromUsersTable(apiData.get(EXTGWADDALTNUMERAPI.MSISDN), "alternate_msisdn");
						
		// if the alter msisdn is not null then null it.
		if(!BTSLUtil.isNullString(arr[0]))
		DBHandler.AccessHandler.updateAnyColumnValue("users", "alternate_msisdn", "", "msisdn", apiData.get(EXTGWADDALTNUMERAPI.MSISDN));
		
		
		
		apiData.put(EXTGWADDALTNUMERAPI.MSISDN, "");
		apiData.put(EXTGWADDALTNUMERAPI.PIN,"");
		
		apiData.put(EXTGWADDALTNUMERAPI.EXTCODE, "");
		
			
		String API = addAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWADDALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}

	
	/*
	 * @throws Exception 
	 * @test Id EXTGWADDALTNMBR01
	 * ADD ALTERNATE SUCCESSFULL MSISDN and PIN
	 */
	
	@Test
	public void _03_AddAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWADDALTNMBR03");
		EXTGWADDALTNUMERAPI addAlternateNumberAPI = new EXTGWADDALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		HashMap<String, String> apiData = EXTGWADDALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		// get alternate msisdn
		String [] arr= DBHandler.AccessHandler.getdetailsfromUsersTable(apiData.get(EXTGWADDALTNUMERAPI.MSISDN), "alternate_msisdn");
				
		// if the alter msisdn is not null then null it.
		if(!BTSLUtil.isNullString(arr[0]))
		DBHandler.AccessHandler.updateAnyColumnValue("users", "alternate_msisdn", "", "msisdn", apiData.get(EXTGWADDALTNUMERAPI.MSISDN));
				
		
		
		
		apiData.put(EXTGWADDALTNUMERAPI.LOGINID, "");
		apiData.put(EXTGWADDALTNUMERAPI.PASSWORD,"");
		
		apiData.put(EXTGWADDALTNUMERAPI.EXTCODE, "");
		
			
		String API = addAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWADDALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	

	/*
	 * @throws Exception 
	 * @test Id EXTGWADDALTNMBR01
	 * ADD ALTERNATE SUCCESSFULL EXTCODE
	 */
	
	@Test
	public void _04_AddAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWADDALTNMBR04");
		EXTGWADDALTNUMERAPI addAlternateNumberAPI = new EXTGWADDALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		HashMap<String, String> apiData = EXTGWADDALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		// get alternate msisdn
		String [] arr= DBHandler.AccessHandler.getdetailsfromUsersTable(apiData.get(EXTGWADDALTNUMERAPI.MSISDN), "alternate_msisdn");
				
		// if the alter msisdn is not null then null it.
		if(!BTSLUtil.isNullString(arr[0]))
		DBHandler.AccessHandler.updateAnyColumnValue("users", "alternate_msisdn","", "msisdn", apiData.get(EXTGWADDALTNUMERAPI.MSISDN));
				
				
		apiData.put(EXTGWADDALTNUMERAPI.LOGINID, "");
		apiData.put(EXTGWADDALTNUMERAPI.PASSWORD,"");
		
		apiData.put(EXTGWADDALTNUMERAPI.MSISDN, "");
		apiData.put(EXTGWADDALTNUMERAPI.PIN, "");
			
		String API = addAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWADDALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void _05_AddAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWADDALTNMBR05");
		EXTGWADDALTNUMERAPI addAlternateNumberAPI = new EXTGWADDALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		HashMap<String, String> apiData = EXTGWADDALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		// get alternate msisdn
		String [] arr= DBHandler.AccessHandler.getdetailsfromUsersTable(apiData.get(EXTGWADDALTNUMERAPI.MSISDN), "alternate_msisdn");
		apiData.put(EXTGWADDALTNUMERAPI.ALTMSISDN, arr[0]);
		
		String API = addAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWADDALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	
	/*
	 * @throws Exception 
	 * @test Id EXTGWADDALTNMBR01
	 * ADD ALTERNATE SUCCESSFULL MSISDN and PIN
	 */
	
	@Test
	public void _06_AddAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWADDALTNMBR06");
		EXTGWADDALTNUMERAPI addAlternateNumberAPI = new EXTGWADDALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		HashMap<String, String> apiData = EXTGWADDALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		/*
		 * // get alternate msisdn String [] arr=
		 * DBHandler.AccessHandler.getdetailsfromUsersTable(apiData.get(
		 * EXTGWADDALTNUMERAPI.MSISDN), "alternate_msisdn");
		 * 
		 * // if the alter msisdn is not null then null it.
		 * if(!BTSLUtil.isNullString(arr[0]))
		 * DBHandler.AccessHandler.updateAnyColumnValue("users", "alternate_msisdn", "",
		 * "msisdn", apiData.get(EXTGWADDALTNUMERAPI.MSISDN));
		 */
				
		
		
		
		apiData.put(EXTGWADDALTNUMERAPI.EXTNWCODE, "");
		
		String API = addAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWADDALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	
	@Test
	public void _07_AddAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWADDALTNMBR07");
		EXTGWADDALTNUMERAPI addAlternateNumberAPI = new EXTGWADDALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		HashMap<String, String> apiData = EXTGWADDALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		/*
		 * // get alternate msisdn String [] arr=
		 * DBHandler.AccessHandler.getdetailsfromUsersTable(apiData.get(
		 * EXTGWADDALTNUMERAPI.MSISDN), "alternate_msisdn");
		 * 
		 * // if the alter msisdn is not null then null it.
		 * if(!BTSLUtil.isNullString(arr[0]))
		 * DBHandler.AccessHandler.updateAnyColumnValue("users", "alternate_msisdn", "",
		 * "msisdn", apiData.get(EXTGWADDALTNUMERAPI.MSISDN));
		 */
		
		apiData.put(EXTGWADDALTNUMERAPI.LOGINID, "");
		apiData.put(EXTGWADDALTNUMERAPI.PASSWORD,"");
		
		apiData.put(EXTGWADDALTNUMERAPI.MSISDN, "");
		apiData.put(EXTGWADDALTNUMERAPI.PIN, "");
		apiData.put(EXTGWADDALTNUMERAPI.EXTCODE, "");
		
				
		String API = addAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWADDALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
		@Test
	public void _08_AddAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWADDALTNMBR08");
		EXTGWADDALTNUMERAPI addAlternateNumberAPI = new EXTGWADDALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		HashMap<String, String> apiData = EXTGWADDALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		/*
		 * // get alternate msisdn String [] arr=
		 * DBHandler.AccessHandler.getdetailsfromUsersTable(apiData.get(
		 * EXTGWADDALTNUMERAPI.MSISDN), "alternate_msisdn");
		 * 
		 * // if the alter msisdn is not null then null it.
		 * if(!BTSLUtil.isNullString(arr[0]))
		 * DBHandler.AccessHandler.updateAnyColumnValue("users", "alternate_msisdn", "",
		 * "msisdn", apiData.get(EXTGWADDALTNUMERAPI.MSISDN));
		 */
				
		
		apiData.put(EXTGWADDALTNUMERAPI.ALTMSISDN, "");
		
		String API = addAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWADDALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	

 }
 