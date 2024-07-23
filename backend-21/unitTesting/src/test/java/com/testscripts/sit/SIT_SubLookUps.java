package com.testscripts.sit;

import org.testng.annotations.Test;

import com.Features.subLookUps;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_SubLookUps)
public class SIT_SubLookUps extends BaseTest{
	String subLName = null;
	String NewName = null;
	String LookUpName = null;
	
	
	@TestManager(TestKey = "PRETUPS-997")@Test
	public void a_AddSubLookUp() {

		
	   final String methodName="";Log.startTestCase(methodName);
		
	   CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITSUBLOOKUP1");

		subLookUps subLookUps = new subLookUps(driver);

		//currentNode=test.createNode("To verify that Super Admin is able to perform sub look up addition");
		currentNode=test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		String [] result = subLookUps.addSubLookUp();
		subLName= result[0];
		LookUpName = result[1];
		Log.info("The SubLookUp for " +result[1] + "is:" +result[0]);
		
		String expected = MessagesDAO.prepareMessageByKey("master.addsublookup.success");
		Assertion.assertEquals(result[2], expected);
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	
	
	@TestManager(TestKey = "PRETUPS-998")@Test
	public void b_ModifySubLookUp() {

		final String methodName="";Log.startTestCase(methodName);

		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITSUBLOOKUP2");
		
		subLookUps subLookUps = new subLookUps(driver);

		currentNode=test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory("SIT");
		String [] resultSet = subLookUps.modifySubLookUp(subLName,LookUpName);
		NewName = resultSet[0];
		
		String expected = MessagesDAO.prepareMessageByKey("master.modifysublookup.success");
		Assertion.assertEquals(resultSet[1], expected);
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	
	@TestManager(TestKey = "PRETUPS-999")@Test
	public void c_DeleteSubLookUp() {

		final String methodName="";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITSUBLOOKUP3");
		subLookUps subLookUps = new subLookUps(driver);

		currentNode=test.createNode(CaseMaster3.getExtentCase());
		currentNode.assignCategory("SIT");
		String actual = subLookUps.deleteSubLookUp(NewName,LookUpName);
		
		String expected = MessagesDAO.prepareMessageByKey("master.modifysublookup.delete.success");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	

}
