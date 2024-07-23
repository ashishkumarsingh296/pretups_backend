package com.testscripts.sit;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.NetworkServices;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_NETWORK_SERVICES)
public class SIT_NetworkService extends BaseTest{
	
	NetworkAdminHomePage networkAdminHomePage;
	Map<String,String> dataMap;
	String assignCategory="SIT";
	
	@DataProvider(name = "allModulesAndServiceType")
	public Object[][] getDataForNetworkService() throws SQLException {

		String network = _masterVO.getMasterValue(MasterI.NETWORK_CODE);		
		String networkName = DBHandler.AccessHandler.getNetworkName(network);
		List<String> moduleList = DBHandler.AccessHandler.getModuleList();
		int moduleListSize = moduleList.size();
		Object[][] dataObject = new Object[moduleListSize][3];
		for (int i=0;i<moduleListSize;i++){
			List<String> descriptionList = DBHandler.AccessHandler.getModuleDescription(moduleList.get(i));
			dataObject[i][0] = networkName;
			dataObject[i][1] = moduleList.get(i);
			dataObject[i][2] = descriptionList.get(0);
		}
		return dataObject;
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	@TestManager(TestKey = "PRETUPS-1131") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_NetworkService(String networkName, String module, String description){
		final String methodName = "Test_Network_Services";
		Log.startTestCase(methodName, networkName, module, description);
		
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "";
		String lang2Desc = "Automated lang2 Desc";
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = networkServices.modifyNetworkService(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String languageErrorMsg = MessagesDAO.prepareMessageByKey("master.updatenetworkservices.error.message1required","",networkName);
		Assertion.assertEquals(message, languageErrorMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	@TestManager(TestKey = "PRETUPS-1133") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_NetworkService(String networkName, String module, String description){
		final String methodName = "Test_Network_Services";
		Log.startTestCase(methodName, networkName, module, description);

		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "";
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = networkServices.modifyNetworkService(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String languageErrorMsg = MessagesDAO.prepareMessageByKey("master.updatenetworkservices.error.message2required","",networkName);
		Assertion.assertEquals(languageErrorMsg, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider = "allModulesAndServiceType")
	@TestManager(TestKey = "PRETUPS-1137") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_NetworkService(String networkName, String module, String description){
		final String methodName = "Test_Network_Services";
		Log.startTestCase(methodName, networkName, module, description);
		
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "Automated lang2 Desc";
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = networkServices.modifyNetworkService_suspend(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String successMsg = MessagesDAO.prepareMessageByKey("master.confirmnetworkservices.msg.success",networkName);
		Assertion.assertEquals(successMsg, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	@TestManager(TestKey = "PRETUPS-1139") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void d_NetworkService(String networkName, String module, String description){
		final String methodName = "Test_Network_Services";
		Log.startTestCase(methodName, networkName, module, description);
		
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "Automated lang2 Desc";
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = networkServices.modifyNetworkService_active(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String successMsg = MessagesDAO.prepareMessageByKey("master.confirmnetworkservices.msg.success",networkName);
		Assertion.assertEquals(successMsg, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	@TestManager(TestKey = "PRETUPS-1143") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void e_NetworkService(String networkName, String module, String description){
		final String methodName = "Test_Network_Services";
		Log.startTestCase(methodName, networkName, module, description);
		
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "Automated lang2 Desc";
		module = "";
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = networkServices.modifyNetworkService(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETSERVICES10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		//String expectedMsg = MessagesDAO.prepareMessageByKey("master.selectservicetypefornetworkservices.label.module.required");
		String expectedMsg = "Module is required.";
		Assertion.assertEquals(message,expectedMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
