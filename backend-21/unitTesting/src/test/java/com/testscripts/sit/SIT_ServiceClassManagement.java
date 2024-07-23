package com.testscripts.sit;

import org.testng.annotations.Test;

import com.Features.InterfaceManagement;
import com.Features.ServiceClassManagement;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_SERVICE_CLASS_MANAGEMENT)
public class SIT_ServiceClassManagement extends BaseTest{

	String InterfaceName = null;
	String InterfaceID = null;
	String InterfaceCatCode = null;
	String InterfaceType = null;
	String ServiceClassID = null;
	static boolean isRoleCodeExist;

	@Test
	@TestManager(TestKey = "PRETUPS-993") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_AddServiceClass() throws InterruptedException{
		final String methodName = "Test_Service_Class_Management";
		Log.startTestCase(methodName);
		isRoleCodeExist = ExcelUtility.isRoleExists(RolesI.ADDSERVICECLASS);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITSRVCLASS1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITSRVCLASS2");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITSRVCLASS3");
		
		InterfaceManagement InterfaceManagement = new InterfaceManagement(driver);
		ServiceClassManagement ServiceClassManagement = new ServiceClassManagement(driver);

		currentNode=test.createNode(CaseMaster1.getExtentCase());
		//currentNode=test.createNode("To verify that Super Admin is able to add Service Class for specific Interface  ");
		currentNode.assignCategory("SIT");

		//Adding Interface
		if (isRoleCodeExist) {
		String [] result = InterfaceManagement.addInterface();
		InterfaceName = result[0];
		InterfaceID = result[2];
		String interfaceCategory = result[3];
		String interfaceType = result[4];
		InterfaceCatCode = DBHandler.AccessHandler.getLookUpCode(interfaceCategory);
		//InterfaceType = DBHandler.AccessHandler.getLookUpCode(interfaceType);
		Log.info("The Interface Category Code for service class Management is : " +InterfaceCatCode);
		Log.info("The Interface Type for service class Management is : " +InterfaceType);
		Log.info("Interface  created as:" +result[0]);
		String expected = MessagesDAO.prepareMessageByKey("interfaces.addinterface.add.success",InterfaceID);

		Assertion.assertEquals(result[1], expected);

		//Adding Service Class

		ExtentI.Markup(ExtentColor.ORANGE, "Creating service class");

		String [] data = ServiceClassManagement.addServiceClass(InterfaceName,InterfaceCatCode);

		String ServiceClassName = data[0];
		ServiceClassID = data[2];
		Log.info("Service class added as:" +ServiceClassName);

		String expected2 = MessagesDAO.prepareMessageByKey("master.addserviceclass.success");

		Assertion.assertEquals(data[1], expected2);

		//Modifying Service Class
		
		currentNode=test.createNode(CaseMaster2.getExtentCase());
		//currentNode=test.createNode("To verify that Super Admin is able to modify Service Class for Recharge Interface  ");
		currentNode.assignCategory("SIT");

		String Message = ServiceClassManagement.modifyServiceClass(InterfaceName, ServiceClassID ,InterfaceCatCode);

		String expected3 = MessagesDAO.prepareMessageByKey("master.modifyserviceclass.success");
		Assertion.assertEquals(Message, expected3);
		
		//Deleting Service Class
		
		currentNode=test.createNode(CaseMaster3.getExtentCase());
		//currentNode=test.createNode("To verify that Super Admin is able to delete Service Class for Recharge Interface  ");
		currentNode.assignCategory("SIT");

		String Message2 = ServiceClassManagement.deleteServiceClass(InterfaceName, ServiceClassID,InterfaceCatCode);

		String expected4 = MessagesDAO.prepareMessageByKey("master.deleteserviceclass.success");
		Assertion.assertEquals(Message2, expected4);


		// Deleting Interface

		ExtentI.Markup(ExtentColor.ORANGE, "Deleting Interface");

		String actual = InterfaceManagement.DeleteInterface(InterfaceID);

		String expected5 = MessagesDAO.prepareMessageByKey("interfaces.addmodify.delete.success");

		Assertion.assertEquals(actual, expected5);
		}
		else { 
			Assertion.assertSkip("Service Management Module not available in system.") ; 
			}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}