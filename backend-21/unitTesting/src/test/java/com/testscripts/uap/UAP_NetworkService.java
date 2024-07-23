package com.testscripts.uap;

import java.sql.SQLException;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.NetworkServices;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name =Module.UAP_NETWORK_SERVICES)
public class UAP_NetworkService extends BaseTest {
	
	public static boolean testCaseCounter = false;
	String assignCategory="UAP";
	
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
	@TestManager(TestKey = "PRETUPS-388") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_language1Missing(String networkName, String module, String description){
		final String methodName = "Test_language1Missing";
        Log.startTestCase(methodName);
        
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "";
		String lang2Desc = "Automated lang2 Desc";
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETSERVICES1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = networkServices.modifyNetworkService(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETSERVICES2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String languageErrorMsg = MessagesDAO.prepareMessageByKey("master.updatenetworkservices.error.message1required","",networkName);
		Assertion.assertEquals(message, languageErrorMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	@TestManager(TestKey = "PRETUPS-389") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_language2Missing(String networkName, String module, String description){
		final String methodName = "Test_language2Missing";
        Log.startTestCase(methodName);

		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "";
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETSERVICES3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = networkServices.modifyNetworkService(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETSERVICES4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String languageErrorMsg = MessagesDAO.prepareMessageByKey("master.updatenetworkservices.error.message2required","",networkName);
		Assertion.assertEquals(message, languageErrorMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	@TestManager(TestKey = "PRETUPS-390") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_serviceModificationSuccess(String networkName, String module, String description){
		final String methodName = "Test_serviceModificationSuccess";
        Log.startTestCase(methodName);

		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "Automated lang2 Desc";
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETSERVICES5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = networkServices.modifyNetworkService(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETSERVICES6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String successMsg = MessagesDAO.prepareMessageByKey("master.confirmnetworkservices.msg.success",networkName);
		Assertion.assertEquals(message, successMsg);
		networkServices.modifyNetworkService_active(networkName, module, description, lang1Desc, lang2Desc);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
}
