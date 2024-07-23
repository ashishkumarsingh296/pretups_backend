package com.testscripts.sit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.Features.C2STransfer;
import com.Features.PromotionalTransferRule;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_PROMOTIONAL_TRANSFER_RULE)
public class SIT_PromotionalTransferRule extends BaseTest {

	NetworkAdminHomePage networkAdminHomePage;
	Map<String,String> dataMap;
	String assignCategory="SIT";
	
	@BeforeClass
	public void testData() throws SQLException {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		dataMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(MasterSheetPath, "Transfer Rule Sheet");
		int rowCountTransfer = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCountTransfer; i++) {
			String fromCategory = ExcelUtility.getCellData(0, "FROM_CATEGORY", i);
			String toCategory = ExcelUtility.getCellData(0, "TO_CATEGORY", i);
			if (toCategory.equals("Subscriber")) {
				dataMap.put("serviceCode", (ExcelUtility.getCellData(0, "SERVICES", i).split(",")[0]));
				dataMap.put("category", fromCategory);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
		int userCounter = ExcelUtility.getRowCount();
		for (int i = 1; i <= userCounter; i++) {
			String category = ExcelUtility.getCellData(0, "CATEGORY_NAME", i);
			if (category.equals(dataMap.get("category"))) {
				dataMap.put("domain", ExcelUtility.getCellData(0, "DOMAIN_NAME", i));
				dataMap.put("userName", ExcelUtility.getCellData(0, "USER_NAME", i));
				dataMap.put("grade", ExcelUtility.getCellData(0, "GRADE", i));
				dataMap.put("pin", ExcelUtility.getCellData(0, "PIN", i));
				dataMap.put("parentCategoryName", ExcelUtility.getCellData(0, "PARENT_CATEGORY_NAME", i));
				break;
			}
		}

		ExcelUtility.setExcelFile(MasterSheetPath, "C2S Services Sheet");
		int rowCountService = ExcelUtility.getRowCount();
		for (int j = 1; j <= rowCountService; j++) {
			String serviceType = ExcelUtility.getCellData(0, "SERVICE_TYPE", j);
			String serviceName = ExcelUtility.getCellData(0, "NAME", j);
			String subServiceName = ExcelUtility.getCellData(0, "SELECTOR_NAME", j);
			String cardGroupName = ExcelUtility.getCellData(0, "PROMO_CARDGROUP", j);
			if (serviceType.equals(dataMap.get("serviceCode")) && !serviceName.isEmpty() && !subServiceName.isEmpty() && !cardGroupName.isEmpty()) {
				dataMap.put("serviceName", serviceName);
				dataMap.put("subServiceName", subServiceName);
				dataMap.put("cardGroup", cardGroupName);
				break;
			}
		}
		

		ExcelUtility.setExcelFile(MasterSheetPath, "Geographical Domains");
		dataMap.put("geoDomainType", ExcelUtility.getCellData(0, "DOMAIN_TYPE_NAME", 1));
		dataMap.put("geoDomainName", ExcelUtility.getCellData(0, "DOMAIN_NAME", 1));

		dataMap.put("type", "Date range");
		dataMap.put("TimeType", "Time range");
		dataMap.put("slabType", "Single");
		dataMap.put("MultipleSlabType", "Multiple");
		
		dataMap.put("promotionalLevel", PretupsI.USER_LOOKUP);

	}

	// Promotional Level is null
	@Test
	@TestManager(TestKey = "PRETUPS-735") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.a_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.promotionleve");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Domain is null
	@Test
	@TestManager(TestKey = "PRETUPS-738") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.b_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domain");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Geographical domain is null
	@Test
	@TestManager(TestKey = "PRETUPS-741") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.c_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domaintype");
		if(result == null){
			Assertion.assertSkip("Message Validation Skipped");
		}
		else
		{
			Assertion.assertEquals(result, message);
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Grade is null
	@Test
	@TestManager(TestKey = "PRETUPS-744") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void d_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.d_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.grade");
		if (message.equals(result))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else if(result == null){
			currentNode.log(Status.SKIP, "Message Validation Skipped");
		}
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(methodName);
	}

	// User is null
	@Test
	@TestManager(TestKey = "PRETUPS-747") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void e_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.e_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.user");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Receiver Type is null
	@Test
	@TestManager(TestKey = "PRETUPS-751") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void f_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.f_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.error.selectdata");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	 // serviceType is null
	@Test
	@TestManager(TestKey = "PRETUPS-754") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void g_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.g_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addc2stransferrules.error.servicetype","1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// fromDate is null
	@Test
	@TestManager(TestKey = "PRETUPS-756") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void j_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.j_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromdate", "1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// fromTime is null
	@Test
	@TestManager(TestKey = "PRETUPS-758") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void k_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.k_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromtime", "1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// tillDate is null
	@Test
	@TestManager(TestKey = "PRETUPS-761") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void l_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.l_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilldate", "1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// tillTime is null
	@Test
	@TestManager(TestKey = "PRETUPS-764") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void m_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.m_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilltime", "1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}




	// fromdateformat notValid
	/*@Test//Commented for Persian Calendar
	public void n_promotionalTransferRuleCreation(){
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE12").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.n_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromdateformat", "1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}*/

	// fromTimeformat notValid
	@Test
	@TestManager(TestKey = "PRETUPS-769") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void o_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.o_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromtimefromat", "1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// tillDateformat notValid
	/*@Test //Commented for Persian Calendar
	public void p_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.p_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilldateformat", "1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}*/
	
	// tillTimeformat notValid
		@Test
		@TestManager(TestKey = "PRETUPS-770") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		public void q_promotionalTransferRuleCreation() {
			final String methodName = "Test_Promotional_Transfer_Rule";
			Log.startTestCase(methodName);

			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE15").getExtentCase());
			currentNode.assignCategory(assignCategory);
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.q_addPromotionalTransferRule(dataMap);
			String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilltimefromat", "1");
			Assertion.assertEquals(result, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		

	// from date time less than current date time
	@Test
	@TestManager(TestKey = "PRETUPS-773") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void r_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE16").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.r_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO
				.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromdatetimeerror", "1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// till date time less than current date time
	@Test
	@TestManager(TestKey = "PRETUPS-774") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void s_promotionalTransferRuleCreation() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE17").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.s_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilldatetimeerror", "1");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


       // Positive Test Case for Date Range
		@Test
		@TestManager(TestKey = "PRETUPS-779") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		public void t_promotionalTransferRuleCreation() {
			final String methodName = "Test_Promotional_Transfer_Rule";
			Log.startTestCase(methodName);

			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE18").getExtentCase());
			currentNode.assignCategory(assignCategory);
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.t_addPromotionalTransferRule(dataMap);
			String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
			String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.operation.msg.alreadyexist", "1");
			if (result.equals(message2)) {
				
				promotionalTransferRule.activate(dataMap);
				Assertion.assertSkip("Message Validation Successful");
			}

			else
				Assertion.assertEquals(result, message1);
			
			String ParentCategory = dataMap.get("parentCategoryName");
			String FromCategory = dataMap.get("category");
			String PIN = dataMap.get("pin");
			String service = dataMap.get("serviceCode");
			
			if(CommonUtils.roleCodeExistInLinkSheet(RolesI.C2SRECHARGE, FromCategory)) {
				String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
				C2STransfer C2STransfer = new C2STransfer(driver);
				if(webAccessAllowed.equals("Y")) {
					C2STransfer.performC2STransfer(ParentCategory, FromCategory, PIN, service);
				}
			}
            
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		/*// multipleTimeSlab is null
		@Test
		public void u_promotionalTransferRuleCreation() {
			final String methodName = "Test_Promotional_Transfer_Rule";
			Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE19").getExtentCase());
			currentNode.assignCategory(assignCategory);
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.u_addPromotionalTransferRule(dataMap);
			String message = MessagesDAO
					.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.multipleTimeSlabrequired");
			Assertion.assertEquals(result, message);

			Assertion.completeAssertions();

			Log.endTestCase(methodName);
		}
		
		// Empty Slabs 
		@Test
		public void v_promotionalTransferRuleCreation() throws InterruptedException {
			final String methodName = "Test_Promotional_Transfer_Rule";
			Log.startTestCase(methodName);
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE20").getExtentCase());
			currentNode.assignCategory(assignCategory);
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.v_addPromotionalTransferRule(dataMap);
			String message = MessagesDAO
					.prepareMessageByKey("promotrfrule.addmultipletimeslabs.error.slabentryreq");
			Assertion.assertEquals(result, message);

			Assertion.completeAssertions();

			Log.endTestCase(methodName);
		}
		
		// Same Start and End Time Slab
				@Test
				public void w_promotionalTransferRuleCreation() throws InterruptedException {
					final String methodName = "Test_Promotional_Transfer_Rule";
					Log.startTestCase(methodName);
					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE21").getExtentCase());
					currentNode.assignCategory(assignCategory);
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.w_addPromotionalTransferRule(dataMap);
					String message = MessagesDAO
							.prepareMessageByKey("promotrfrule.addmultipleslab.error.endtimefromtimeequal", "1");
					Assertion.assertEquals(result, message);

					Assertion.completeAssertions();

					Log.endTestCase(methodName);
				}
				
			// Same Start and End Time Slab for two rows
				@Test
				public void x_promotionalTransferRuleCreation() throws InterruptedException {
					final String methodName = "Test_Promotional_Transfer_Rule";
					Log.startTestCase(methodName);
					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE22").getExtentCase());
					currentNode.assignCategory(assignCategory);
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.x_addPromotionalTransferRule(dataMap);
					String message = MessagesDAO
							.prepareMessageByKey("promotrfrule.addmultipletimeslabs.error.fromtimeinnextrow", "2", "1");
					Assertion.assertEquals(result, message);

					Assertion.completeAssertions();
					Log.endTestCase(methodName);
				}
				
				//  Start Time is less than  End Time  for previous slab
				@Test
				public void y_promotionalTransferRuleCreation() throws InterruptedException {
					final String methodName = "Test_Promotional_Transfer_Rule";
					Log.startTestCase(methodName);

					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE23").getExtentCase());
					currentNode.assignCategory(assignCategory);
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.y_addPromotionalTransferRule(dataMap);
					String message = MessagesDAO
							.prepareMessageByKey("promotrfrule.addmultipletimeslabs.error.fromtimeinnextrow", "2", "1");
					Assertion.assertEquals(result, message);

					Assertion.completeAssertions();

					Log.endTestCase(methodName);
				}
				
			// Positive flow for Single Time Slab 
				@Test
				public void z_promotionalTransferRuleCreation() {
					final String methodName = "Test_Promotional_Transfer_Rule";
					Log.startTestCase(methodName);
					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE24").getExtentCase());
					currentNode.assignCategory(assignCategory);
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.z_addPromotionalTransferRule(dataMap);
					String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
					String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.operation.msg.alreadyexist", "1");
					if (result.equals(message1))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else if (result.equals(message2))
					currentNode.log(Status.SKIP, "Message Validation Successful");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message1 + "] or [" + message2 + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(methodName);
				}
		
			// Positive flow for Multiple Time Slab 
				@Test
				public void za_promotionalTransferRuleCreation() throws InterruptedException {
					final String methodName = "Test_Promotional_Transfer_Rule";
					Log.startTestCase(methodName);
					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE25").getExtentCase());
					currentNode.assignCategory(assignCategory);
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.za_addPromotionalTransferRule(dataMap);
					String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
					String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.operation.msg.alreadyexist", "1");
					if (result.equals(message1))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else if (result.equals(message2))
					currentNode.log(Status.SKIP, "Message Validation Successful");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message1 + "] or [" + message2 + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(methodName);
				}*/
		
	// View Promotional Transfer Rule Test Cases

	// Promotional Level is null
	@Test
	@TestManager(TestKey = "PRETUPS-783") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void zb_viewPromotionalTransferRule() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE26").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.zb_viewPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.promotionleve");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
// Domain is null
		@Test
		@TestManager(TestKey = "PRETUPS-785") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		public void zc_viewPromotionalTransferRule() {
			final String methodName = "Test_Promotional_Transfer_Rule";
			Log.startTestCase(methodName);

			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE27").getExtentCase());
			currentNode.assignCategory(assignCategory);
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.zc_viewPromotionalTransferRule(dataMap);
			String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domain");
			String message2 = "Domain Field for Promotional level Geography does not exist";
			if (result.equals(message2)){
				Assertion.assertSkip("Message Validation skipped");
			}
			else 
				Assertion.assertEquals(result, message1);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		//Geographical domain is null
				@Test
				@TestManager(TestKey = "PRETUPS-786") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
				public void zd_viewPromotionalTransferRule() {
					final String methodName = "Test_Promotional_Transfer_Rule";
					Log.startTestCase(methodName);

					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE28").getExtentCase());
					currentNode.assignCategory(assignCategory);
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.zd_viewPromotionalTransferRule(dataMap);
					String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domaintype");
					if (result.equals(message) && result != null){
						currentNode.log(Status.PASS, "Message Validation Successful");
					}
					else if(result == null)
						currentNode.log(Status.SKIP, "Message Validation Skipped");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(methodName);
				}
				
				//Grade or User is null
				@Test
				@TestManager(TestKey = "PRETUPS-787") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
				public void ze_viewPromotionalTransferRule() {
					final String methodName = "Test_Promotional_Transfer_Rule";
					Log.startTestCase(methodName);
	
					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE29").getExtentCase());
					currentNode.assignCategory(assignCategory);
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.ze_viewPromotionalTransferRule(dataMap);
					String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.grade");
					String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.user");
					String message3 = "Test Case only for Promotional Level User and Grade.";
					if (result.equals(message1))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else if (result.equals(message2))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else if(result.equals(message3)) 
					currentNode.log(Status.SKIP, "Message Validation Skipped");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message1 + "] or [" + message2 + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(methodName);
				}
				
				//Positive Test Case for View
				@Test
				@TestManager(TestKey = "PRETUPS-788") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
				public void zf_viewPromotionalTransferRule() {
					final String methodName = "Test_Promotional_Transfer_Rule";
					Log.startTestCase(methodName);

					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE30").getExtentCase());
					currentNode.assignCategory(assignCategory);
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					boolean result = promotionalTransferRule.zf_viewPromotionalTransferRule(dataMap);
					if (result)
						currentNode.log(Status.PASS, "Message Validation Successful");
					else {
						currentNode.log(Status.FAIL, "Expected [True] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(methodName);
				}

			// Modify Promotional Transfer Rule Test Cases

	// Promotional Level is null
	@Test
	@TestManager(TestKey = "PRETUPS-800") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void zg_modifyPromotionalTransferRule() {
		final String methodName = "Test_Promotional_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE31").getExtentCase());
		currentNode.assignCategory(assignCategory);
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.zg_modifyPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.promotionleve");
		Assertion.assertEquals(result, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// Domain is null
		@Test
		@TestManager(TestKey = "PRETUPS-802") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		public void zh_modifyPromotionalTransferRule() {
			final String methodName = "Test_Promotional_Transfer_Rule";
			Log.startTestCase(methodName);

			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE32").getExtentCase());
			currentNode.assignCategory(assignCategory);
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.zh_modifyPromotionalTransferRule(dataMap);
			String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domain");
			String message2 = "Domain Field for Promotional level Geography does not exist";
			if (result.equals(message2))
			{
				Assertion.assertSkip("Message Validation Skipped");
			}

			else
				Assertion.assertEquals(result, message1);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		

		// Geographical Domain is null
			@Test
			@TestManager(TestKey = "PRETUPS-804") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
			public void zi_modifyPromotionalTransferRule() {
				final String methodName = "Test_Promotional_Transfer_Rule";
				Log.startTestCase(methodName);

				currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE33").getExtentCase());
				currentNode.assignCategory(assignCategory);
				PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
				String result = promotionalTransferRule.zi_modifyPromotionalTransferRule(dataMap);
				String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domaintype");
				Assertion.assertEquals(result, message);

				Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}
			
			//Grade or User is null
			@Test
			@TestManager(TestKey = "PRETUPS-806") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
			public void zj_modifyPromotionalTransferRule() {
				final String methodName = "Test_Promotional_Transfer_Rule";
				Log.startTestCase(methodName);

				currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE34").getExtentCase());
				currentNode.assignCategory(assignCategory);
				PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
				String result = promotionalTransferRule.zj_modifyPromotionalTransferRule(dataMap);
				String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.grade");
				String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.user");
				String message3 = "Test Case only for Promotional Level User and Grade.";
				if (result.equals(message1))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else if (result.equals(message2))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else if(result.equals(message3)) 
				currentNode.log(Status.SKIP, "Message Validation Skipped");
				else {
					currentNode.log(Status.FAIL, "Expected [" + message1 + "] or [" + message2 + "] but found [" + result + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}

				//Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}

			
			//When checkboxes are unchecked
			@Test
			@TestManager(TestKey = "PRETUPS-808") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
			public void zk_modifyPromotionalTransferRule() {
				final String methodName = "Test_Promotional_Transfer_Rule";
				Log.startTestCase(methodName);

				currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE35").getExtentCase());
				currentNode.assignCategory(assignCategory);
				PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
				String result = promotionalTransferRule.zk_modifyPromotionalTransferRule(dataMap);
				String message = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.selectrow");
				Assertion.assertEquals(result, message);

				Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}
			
			//Positive Flow for Date Range                                               
			@Test
			@TestManager(TestKey = "PRETUPS-810") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
			public void zl_modifyPromotionalTransferRule() {
				final String methodName = "Test_Promotional_Transfer_Rule";
				Log.startTestCase(methodName);
				currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOTRFRULE36").getExtentCase());
				currentNode.assignCategory(assignCategory);
				PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
				String result = promotionalTransferRule.zl_modifyPromotionalTransferRule(dataMap);
				String message = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
				
				Assertion.assertEquals(result, message);
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}
}
