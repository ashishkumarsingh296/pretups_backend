package com.testscripts.sit;

import org.testng.annotations.Test;

import com.Features.InterfaceManagement;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_InterfaceManagement)
public class SIT_InterfaceManagement extends BaseTest{

	String interfaceID = null;
	String assignCategory="SIT";
	
	@TestManager(TestKey = "PRETUPS-1007")@Test
	public void a_AddInterface() throws InterruptedException{
		
		final String methodName="a_AddInterface";Log.startTestCase(methodName);

		InterfaceManagement InterfaceManagement = new InterfaceManagement(driver);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITINTERFACEMGMT1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String [] result = InterfaceManagement.addInterface();
		interfaceID = result[2];
		Log.info("Interface ID created as:" +result[2]);
		
        String expected = MessagesDAO.prepareMessageByKey("interfaces.addinterface.add.success",interfaceID);
		Assertion.assertEquals(result[1], expected);
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	
	
	@TestManager(TestKey = "PRETUPS-1008")@Test
	public void b_ModifyInterface() throws InterruptedException{
		
		final String methodName="b_ModifyInterface";Log.startTestCase(methodName);

		
		
		InterfaceManagement InterfaceManagement = new InterfaceManagement(driver);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITINTERFACEMGMT2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String actual = InterfaceManagement.modifyInterface(interfaceID);
		
        String expected = MessagesDAO.prepareMessageByKey("interfaces.addinterface.modify.success",interfaceID);
        Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	
	
	@TestManager(TestKey = "PRETUPS-1009")@Test
	public void c_DeleteInterface() throws InterruptedException{
		
		final String methodName="c_DeleteInterface";Log.startTestCase(methodName);

		
		
		InterfaceManagement InterfaceManagement = new InterfaceManagement(driver);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITINTERFACEMGMT3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String actual = InterfaceManagement.DeleteInterface(interfaceID);
		
        String expected = MessagesDAO.prepareMessageByKey("interfaces.addmodify.delete.success");
		
        Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	

}
