package com.testscripts.sit;

import org.testng.annotations.Test;

import com.Features.NetworkManagement;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CacheUpdate;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_NetworkManagement)
public class SIT_NetworkManagement extends BaseTest{
	String NetworkCode = null;
	String assignCategory="SIT";

	@TestManager(TestKey = "PRETUPS-994")
	@Test
	public void a_AddNetwork() throws InterruptedException{

		final String methodName="a_AddNetwork";Log.startTestCase(methodName);


		NetworkManagement NetworkManagement = new NetworkManagement(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKMGMT1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		boolean networkMngmntLinkExists = ExcelUtility.isRoleExists(RolesI.VIEWNETWORK);

		if (!networkMngmntLinkExists){
			
			Assertion.assertSkip("Network Management through Web is not available");
		}
		else{
		String [] result = NetworkManagement.addNetwork();
		NetworkCode = result[1];
		Log.info("Network Code created as:" +result[1]);

		String expected = MessagesDAO.prepareMessageByKey("network.networkdetail.successaddmessage");
         Assertion.assertEquals(result[2], expected);
		
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}


	@TestManager(TestKey = "PRETUPS-995")
	@Test
	public void b_ModifyNetwork() throws InterruptedException{

		final String methodName="b_ModifyNetwork";
		Log.startTestCase(methodName);

	

		NetworkManagement NetworkManagement = new NetworkManagement(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKMGMT2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		boolean networkMngmntLinkExists = ExcelUtility.isRoleExists(RolesI.VIEWNETWORK);

		if (!networkMngmntLinkExists){
			currentNode.log(Status.SKIP, "Network Management through Web is not available");
		}
		else{
		String actual = NetworkManagement.modifyNetwork(NetworkCode);

		String expected = MessagesDAO.prepareMessageByKey("network.networkdetail.successeditmessage");
Assertion.assertEquals(actual, expected);
	
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@TestManager(TestKey = "PRETUPS-996")@Test
	public void c_DeleteNetwork(){
		final String methodName="c_DeleteNetwork";
		Log.startTestCase(methodName);
		
		
		String actual=null;
		CacheUpdate CacheUpdate = new CacheUpdate(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKMGMT3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		boolean networkMngmntLinkExists = ExcelUtility.isRoleExists(RolesI.VIEWNETWORK);

		if (!networkMngmntLinkExists){
			Assertion.assertSkip("Network Management through Web is not available");
			
		}
		else{
			
			if(NetworkCode.equals(null)){
				Assertion.assertSkip("Issue in adding a new network Code");
				
			}	
			else{	
		Log.info("Deleting network code via DB query.");
		ExtentI.Markup(ExtentColor.ORANGE,"Delete functionality does not exists in base product Hence Deleting above created Network from Database");
		String result = DBHandler.AccessHandler.DeleteNetwork(NetworkCode);
		if(result.equalsIgnoreCase("N")){
			actual = "The Created Network is deleted from database successfully";
		}
		else{
			actual = "Error with delete Query or commit Query";
		}
		
		String expected = "The Created Network is deleted from database successfully";
		CacheUpdate.updateCache();
	       Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();    
		Log.endTestCase(methodName);
		}
		}
	}

}
