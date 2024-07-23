package com.testscripts.sit;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.NetworkServices;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.Log;
import com.utils._masterVO;

public class SIT_NetworkService extends BaseTest{
	
	static boolean testCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;
	Map<String,String> dataMap;
	
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
	public void a_NetworkService(String networkName, String module, String description){
		Log.startTestCase(this.getClass().getName());
		
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]Network Services");
			testCaseCounter = true;
		}
		
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "";
		String lang2Desc = "Automated lang2 Desc";
		currentNode = test.createNode(
				"To verify that error message is displayed if Language 1 field is blank");
		currentNode.assignCategory("SIT");
		String message = networkServices.modifyNetworkService(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode("To verify that proper message is displayed on keeping the language 1 description field empty");
		currentNode.assignCategory("SIT");
		String languageErrorMsg = MessagesDAO.prepareMessageByKey("master.updatenetworkservices.error.message1required","",networkName);
		if (languageErrorMsg.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + languageErrorMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
		
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	public void b_NetworkService(String networkName, String module, String description){
		Log.startTestCase(this.getClass().getName());
if (testCaseCounter == false) {
			test = extent.createTest("[SIT]Network Services");
			testCaseCounter = true;
		}
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "";
		currentNode = test.createNode(
				"To verify that error message is displayed if Language 2 field is blank");
		currentNode.assignCategory("SIT");
		String message = networkServices.modifyNetworkService(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode("To verify that proper message is displayed on keeping the language 2 description field empty");
		currentNode.assignCategory("SIT");
		String languageErrorMsg = MessagesDAO.prepareMessageByKey("master.updatenetworkservices.error.message2required","",networkName);
		if (languageErrorMsg.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + languageErrorMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
		
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	public void c_NetworkService(String networkName, String module, String description){
		Log.startTestCase(this.getClass().getName());
if (testCaseCounter == false) {
			test = extent.createTest("[SIT]Network Services");
			testCaseCounter = true;
		}
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "Automated lang2 Desc";
		currentNode = test.createNode(
				"To verify that service status is modified successfully");
		currentNode.assignCategory("SIT");
		String message = networkServices.modifyNetworkService_active(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode("To verify that proper message is displayed on successful service status modification");
		currentNode.assignCategory("SIT");
		String successMsg = MessagesDAO.prepareMessageByKey("master.confirmnetworkservices.msg.success",networkName);
		if (successMsg.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + successMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
		
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	public void d_NetworkService(String networkName, String module, String description){
		Log.startTestCase(this.getClass().getName());
if (testCaseCounter == false) {
			test = extent.createTest("[SIT]Network Services");
			testCaseCounter = true;
		}
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "Automated lang2 Desc";
		currentNode = test.createNode(
				"To verify that service status is modified successfully");
		currentNode.assignCategory("SIT");
		String message = networkServices.modifyNetworkService_suspend(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode("To verify that proper message is displayed on successful service status modification");
		currentNode.assignCategory("SIT");
		String successMsg = MessagesDAO.prepareMessageByKey("master.confirmnetworkservices.msg.success",networkName);
		if (successMsg.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + successMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
		
	}
	
	@Test(dataProvider = "allModulesAndServiceType")
	public void e_NetworkService(String networkName, String module, String description){
		Log.startTestCase(this.getClass().getName());
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]Network Services");
			testCaseCounter = true;
		}
		NetworkServices networkServices = new NetworkServices(driver);
		String lang1Desc = "Automated lang1 Desc";
		String lang2Desc = "Automated lang2 Desc";
		module = "";
		currentNode = test.createNode(
				"To verify that error message is displayed if module is not selected");
		currentNode.assignCategory("SIT");
		String message = networkServices.modifyNetworkService(networkName, module, description, lang1Desc, lang2Desc);
		currentNode = test.createNode("To verify that proper message is displayed on unsuccessful service status modification");
		currentNode.assignCategory("SIT");
		//String successMsg = MessagesDAO.prepareMessageByKey("master.confirmnetworkservices.msg.success",networkName);
		String successMsg = "Module is Required.";
		if (successMsg.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + successMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
		
	}
	


}
