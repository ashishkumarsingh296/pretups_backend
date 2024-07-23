package com.testscripts.sit;

import org.testng.annotations.Test;

import com.Features.O2CReconciliation;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_O2C_Reconsilation)
public class SIT_O2CReconsilation extends BaseTest {

	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;
	String assignCategory = "SIT";

	// Success
	@Test
	@TestManager(TestKey = "PRETUPS-295") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_O2CReconciliation() {
		final String methodName = "Test_O2CReconsilation";
        Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		O2CReconciliation o2CReconciliation = new O2CReconciliation(driver);

		String[] result = o2CReconciliation.o2CReconciliationlink_Success();

		String message = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success",result[1]);
		String message1 = MessagesDAO.getLabelByKey("channeltransfer.list.msg.zero");
		String message2 = MessagesDAO.getLabelByKey("channeltransfer.approval.msg.userdetailnotfound");
		
		if (result[0].equals(message1)) 
			Assertion.assertSkip("No record found for the input values.");
		else if (result[0].equals(message2)) 
			Assertion.assertSkip("No User Found for Ambigeous transaction.");  
		else
		Assertion.assertEquals(result[0], message);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	// Fail
		@Test
		@TestManager(TestKey = "PRETUPS-297") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void b_O2CReconciliation() {
			final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE2").getExtentCase());
			currentNode.assignCategory(assignCategory);
			O2CReconciliation o2CReconciliation = new O2CReconciliation(driver);

			String[] result = o2CReconciliation.o2CReconciliationlink_Failure();

			String message = MessagesDAO.prepareMessageByKey("channeltransfer.foctransferapprovaldetailview.msg.level1cancel",result[1]);
			String message1 = MessagesDAO.getLabelByKey("channeltransfer.list.msg.zero");
			String message2 = MessagesDAO.getLabelByKey("channeltransfer.approval.msg.userdetailnotfound");
			
			if (result[0].equals(message1)) 
				Assertion.assertSkip("No record found for the input values.");
			else if (result[0].equals(message2))
				Assertion.assertSkip("No User Found for Ambigeous transaction.");
			else
			Assertion.assertEquals(result[0], message);
						
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		} 
		// NEGATIVE FROM DATE IS NULL 
		@Test
		@TestManager(TestKey = "PRETUPS-298") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void c_O2CReconcilation()
		{
			final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE3").getExtentCase());
			currentNode.assignCategory(assignCategory);
			O2CReconciliation o2CReconciliation = new O2CReconciliation(driver);
            String result = o2CReconciliation.o2CReconciliationFromDateNull();
            currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE4").getExtentCase());
			currentNode.assignCategory(assignCategory);
            String message = MessagesDAO.prepareMessageByKey("c2s.reports.c2sbonusreport.label.fromDate");
			Assertion.assertEquals(result, message);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
	// negative- to date is null 
		@Test
		@TestManager(TestKey = "PRETUPS-302") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void d_O2CReconcilation()
		{
			final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE5").getExtentCase());
			currentNode.assignCategory(assignCategory);
			O2CReconciliation o2CReconciliation = new O2CReconciliation(driver);
			String result = o2CReconciliation.o2CReconciliationToDateNull();	
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE6").getExtentCase());
			currentNode.assignCategory(assignCategory);
			String message = MessagesDAO.prepareMessageByKey("c2s.reports.c2sbonusreport.label.toDate");
			Assertion.assertEquals(result,message);	
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		//o2CReconciliation_dateDiffMoreThan30Days - neagtive 
		@Test
		@TestManager(TestKey = "PRETUPS-304") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void e_O2CReconcilation()
		{
			final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
	        currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE7").getExtentCase());
	        currentNode.assignCategory(assignCategory);
			O2CReconciliation o2CReconciliation = new O2CReconciliation(driver);
			String result = o2CReconciliation.o2CReconciliationlink_dateDiffMoreThan30Days();	
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE8").getExtentCase());
			currentNode.assignCategory(assignCategory);
			String message = MessagesDAO.prepareMessageByKey("btsl.date.error.datecompare", "30");
			Assertion.assertEquals(result,message);		
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		// negative-o2CReconciliation_fromDateGreaterThanCurrDate
		@Test
		@TestManager(TestKey = "PRETUPS-308") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void f_O2CReconcilation()
		{
			final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE9").getExtentCase());
			currentNode.assignCategory(assignCategory);
			O2CReconciliation o2CReconciliation = new O2CReconciliation(driver);
			String result = o2CReconciliation.o2CReconciliationlink_fromDateGreaterThanCurrDate();	
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE10").getExtentCase());
			currentNode.assignCategory(assignCategory);
			String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.fromdatebeforecurrentdate");
			Assertion.assertEquals(result,message);		
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		//o2CReconciliationlink_toDateGreaterThanCurrDate
		@Test
		@TestManager(TestKey = "PRETUPS-310") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void g_O2CReconcilation()
		{
			final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE11").getExtentCase());
			currentNode.assignCategory(assignCategory);
			O2CReconciliation o2CReconciliation = new O2CReconciliation(driver);
			String result = o2CReconciliation.o2CReconciliationlink_toDateGreaterThanCurrDate();
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE12").getExtentCase());
			currentNode.assignCategory(assignCategory);
			String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.todatebeforecurrentdate");
			Assertion.assertEquals(result,message);		
			Assertion.completeAssertions();
			Log.endTestCase(this.getClass().getName());
		}
		//o2CReconciliationlink_toDateLessThanForDate
		@Test
		@TestManager(TestKey = "PRETUPS-312") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void h_O2CReconcilation()
		{
			final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE13").getExtentCase());
			currentNode.assignCategory(assignCategory);
			O2CReconciliation o2CReconciliation = new O2CReconciliation(driver);
			String result = o2CReconciliation.o2CReconciliationlink_toDateLessThanForDate();
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE14").getExtentCase());
			currentNode.assignCategory(assignCategory);
			String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.fromdatebeforetodate");
			Assertion.assertEquals(result,message);	
			Assertion.completeAssertions();
			Log.endTestCase(this.getClass().getName());
		}
	    @Test
	    @TestManager(TestKey = "PRETUPS-315") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void i_O2CReconciliation() {
	    	final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE16").getExtentCase());
			currentNode.assignCategory(assignCategory);
			O2CReconciliation O2CReconciliation = new O2CReconciliation(driver);
			String validationValue = MessagesDAO.getLabelByKey("channeltransfer.list.msg.zero");
			boolean[] result = O2CReconciliation.o2CReconciliationlink_checkAmbigious(validationValue);
			Assertion.assertEquals(String.valueOf(result[1]), String.valueOf(result[0]));
			Assertion.completeAssertions();
			Log.endTestCase(this.getClass().getName());
		}
	    
	    @Test
	    @TestManager(TestKey = "PRETUPS-316") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void j_O2CReconciliation() {
	    	final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE17").getExtentCase());
			currentNode.assignCategory(assignCategory);
			O2CReconciliation O2CReconciliation = new O2CReconciliation(driver);
			String result = O2CReconciliation.o2CReconciliationlink_dbVerification();
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE18").getExtentCase());
			currentNode.assignCategory(assignCategory);
			String message1 = MessagesDAO.getLabelByKey("channeltransfer.list.msg.zero");
			String message2 = MessagesDAO.getLabelByKey("channeltransfer.approval.msg.userdetailnotfound");
			if (result.equals(message1)) 
				Assertion.assertSkip("No transfer found for the input values.");
			else if (result.equals(message2)) 
				Assertion.assertSkip("No user found.");  
			else
			Assertion.assertEquals(result, "CLOSE");
			Assertion.completeAssertions();
			Log.endTestCase(this.getClass().getName());
		}
	    //failure case 
	    @Test
	    @TestManager(TestKey = "PRETUPS-319") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void k_O2CReconciliation() {
	    	final String methodName = "Test_O2CReconsilation";
	        Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE19").getExtentCase());
			currentNode.assignCategory(assignCategory);
			O2CReconciliation O2CReconciliation = new O2CReconciliation(driver);
			String result = O2CReconciliation.o2CReconciliationlink_dbVerification1();
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITO2CRECONCILE20").getExtentCase());
			currentNode.assignCategory(assignCategory);
			String message1 = MessagesDAO.getLabelByKey("channeltransfer.list.msg.zero");
			String message2 = MessagesDAO.getLabelByKey("channeltransfer.approval.msg.userdetailnotfound");
			if (result.equals(message1)) 
				Assertion.assertSkip("No transfer found for the input values.");
			else if (result.equals(message2)) 
				Assertion.assertSkip("No user found.");  
			else
			Assertion.assertEquals(result, "CNCL");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	    
		
		
		
}
