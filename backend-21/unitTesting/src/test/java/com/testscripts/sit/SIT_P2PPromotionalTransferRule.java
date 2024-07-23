package com.testscripts.sit;

import java.util.ArrayList;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PPromotionalTransferRule;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.entity.P2PPromotionalTrfRuleVO;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_P2PPromotionalTransferRule extends BaseTest{

	static boolean TestCaseCounter = false;
	static String moduleCode;
	String assignCategory="SIT";
	
	
	@Test(priority=1)
	public void P2P_PROMO_TRF_APPLICABLE_PreferenceVerification() throws InterruptedException {
     moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE1").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that  Promotional Transfer rule can be enabled via Preference at system level by superadmin");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
        String scenario = "modifySystemPreference";
        
		initiateMap = new P2PPromotionalTransferRule(driver).p2pPromoTransferApplicablePreference(scenario);
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Log.info(" System preference P2P_PROMO_TRF_APP getting applicable on UI");
			
			//Message Validation Here
		} else 
			Log.failNode("System preference P2P_PROMO_TRF_APP not getting applicable on UI");

		Log.endTestCase(this.getClass().getName());
	}
	
	
	
	@Test(priority=2)
	public void P2P_PROMO_APPLICABLE_Order_PreferenceVerification() throws InterruptedException {
     moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE2").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Promotional Rule applicable order can be set at system level by superadmin");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
        String scenario = "modifySystemPreference";
        
		initiateMap = new P2PPromotionalTransferRule(driver).p2pPromoApplicableOrderPreference(scenario);
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Log.info(" System preference P2P_PROMO_TRF_APP Order getting applicable on UI");
			
			//Message Validation Here
		} else 
			Log.failNode("System preference P2P_PROMO_TRF_APP Order not getting applicable on UI");

		Log.endTestCase(this.getClass().getName());
	}
	
	

	
	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=3)
	public void addP2PPromotionalTrfRule(P2PPromotionalTrfRuleVO p2ppromodata){
		 moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE3").getModuleCode();
		
			if (TestCaseCounter == false) {
				test=extent.createTest(moduleCode);
				TestCaseCounter = true;
			}
			
			Log.startTestCase("To verify that networkadmin can create promotional card group");
			
			
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE3").getExtentCase());
			currentNode.assignCategory(assignCategory);
		
		Log.info(""+p2ppromodata.toString());
		String actualMessage = new P2PPromotionalTransferRule(driver).addP2PPromotionalTransferRule(p2ppromodata,"SUB","DATE","SINGLE");
		
		String expModMsg = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		String expAddMsg = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
		
		if (actualMessage.equals(expAddMsg)){
			Validator.messageCompare(actualMessage, expAddMsg);
		}
		else{
			Validator.messageCompare(actualMessage, expModMsg);
		}
		
	}
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=4)
	public void modifyP2PPromotionalTrfRule(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE4").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin can modify promotional transfer rule at subscriber level for date range");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE4").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).modifyP2PPromotionalTransferRule(p2ppromodata,"SUB","DATE");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage= MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		Validator.messageCompare(actualMessage, expectedMessage);
	}
	
	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=5)
	public void viewP2PPromotionalTrfRule(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE5").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin can view promotional transfer rule at subscriber level for date range");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE5").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).viewP2PPromotionalTransferRule(p2ppromodata,"SUB","DATE");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("promotrfrule.viewc2stransferrules.heading");
		Validator.messageCompare(actualMessage, expectedMessage);
	}	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=6)
	public void deleteP2PPromotionalTrfRule(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE6").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin can delete promotional transfer rule at subscriber level for date range");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE6").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).deleteP2PPromotionalTransferRule(p2ppromodata,"SUB","DATE");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("trfrule.deltrfrule.msg.success");
		Validator.messageCompare(actualMessage, expectedMessage);
	}

	
	

	 
	@Test(dataProvider="p2pPromotionalTransferData", priority=7)
	public void addP2PPromotionalTrfRuleSubscriberLevelSingleRange(P2PPromotionalTrfRuleVO p2ppromodata){
		 moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE7").getModuleCode();
			
			if (TestCaseCounter == false) {
				test=extent.createTest(moduleCode);
				TestCaseCounter = true;
			}
			
			Log.startTestCase("To verify that Networkadmin can create promotional transfer rule at subscriber level for Single time range");
		
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE7").getExtentCase());
			currentNode.assignCategory(assignCategory);
		
		Log.info(""+p2ppromodata.toString());
		String actualMessage = new P2PPromotionalTransferRule(driver).addP2PPromotionalTransferRuleTimeRange(p2ppromodata,"SUB","TIME","SINGLE");
		
		String expModMsg = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		String expAddMsg = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
		
		if (actualMessage.equals(expAddMsg)){
			Validator.messageCompare(actualMessage, expAddMsg);
		}
		else{
			Validator.messageCompare(actualMessage, expModMsg);
		}
	}
	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=8)
	public void modifyP2PPromotionalTrfRuleSubLevelTimeRange(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE8").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin  can modify promotional transfer rule at subscriber level for time range (Single/Multiple)");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE8").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).modifyP2PPromotionalTransferRule(p2ppromodata,"SUB","TIME");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		Validator.messageCompare(actualMessage, expectedMessage);
	}

	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=9)
	public void viewP2PPromotionalTrfRuleSubLevelTimeRange(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE9").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin  can view promotional transfer rule at subscriber level for time range (Single/Multiple)");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE9").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).viewP2PPromotionalTransferRule(p2ppromodata,"SUB","TIME");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("promotrfrule.viewc2stransferrules.heading");
		Validator.messageCompare(actualMessage, expectedMessage);
	}

	
	
		
	

	
	@Test(dataProvider="p2pPromotionalTransferData", priority=10)
	public void deleteP2PPromotionalTrfRuleSubLevelTimeRange(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE10").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin  can delete promotional transfer rule at subscriber level for time range (Single/Multiple)");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE10").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).deleteP2PPromotionalTransferRule(p2ppromodata,"SUB","TIME");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("trfrule.deltrfrule.msg.success");
		Validator.messageCompare(actualMessage, expectedMessage);
	}
	
	
	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=11)
	public void addP2PPromotionalTrfRuleSubscriberLevelMultipleRange(P2PPromotionalTrfRuleVO p2ppromodata){
		 moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE11").getModuleCode();
			
			if (TestCaseCounter == false) {
				test=extent.createTest(moduleCode);
				TestCaseCounter = true;
			}
			
			Log.startTestCase("To verify that Networkadmin can create promotional transfer rule at subscriber level for Multiple time range");
			
			
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE11").getExtentCase());
			currentNode.assignCategory(assignCategory);
		
		Log.info(""+p2ppromodata.toString());

		String subscriberType = PretupsI.PREPAID_LOOKUP;
		String mobilenumber = DBHandler.AccessHandler.getP2PSubscriberMSISDNSeq(subscriberType, "Y","2");
		
		String actualMessage = new P2PPromotionalTransferRule(driver).addP2PPromotionalTransferRule(p2ppromodata,"SUB","TIME","MULTIPLE");
		String expModMsg = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		String expAddMsg = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
		
		String act = new P2PPromotionalTransferRule(driver).deleteP2PPromotionalTransferRule(p2ppromodata, "SUB", "TIME");
		
		
		if (actualMessage.equals(expAddMsg)){
			Validator.messageCompare(actualMessage, expAddMsg);
		}
		else{
			Validator.messageCompare(actualMessage, expModMsg);
		}
		
		String exp = MessagesDAO.prepareMessageByKey("trfrule.deltrfrule.msg.success");
		if (act.equals(exp)){
			ExtentI.Markup(ExtentColor.TEAL, "Transfer rule deleted successfully");
		}
	}	
	
	
	

	
	
	
	
	
	
	
	
	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=12)
	public void addP2PPromotionalTrfRuleCellGroup(P2PPromotionalTrfRuleVO p2ppromodata){
		 moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE12").getModuleCode();
			
			if (TestCaseCounter == false) {
				test=extent.createTest(moduleCode);
				TestCaseCounter = true;
			}
			
			Log.startTestCase("To verify that networkadmin can create promotional card group for Cell Group at date Range");
			
			
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE12").getExtentCase());
			currentNode.assignCategory(assignCategory);
		
		Log.info(""+p2ppromodata.toString());
		String actualMessage = new P2PPromotionalTransferRule(driver).addP2PPromotionalTransferRule(p2ppromodata,"CEL","DATE","SINGLE");
		String expModMsg = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		String expAddMsg = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
		
		if (actualMessage.equals(expAddMsg)){
			Validator.messageCompare(actualMessage, expAddMsg);
		}
		else{
			Validator.messageCompare(actualMessage, expModMsg);
		}
	}
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=13)
	public void modifyP2PPromotionalTrfRuleCellGroup(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE13").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin can modify promotional transfer rule at Cell Group level for date range");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE13").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).modifyP2PPromotionalTransferRule(p2ppromodata,"CEL","DATE");
		Log.info(""+p2ppromodata.toString());
		
		String expectedMessage = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		Validator.messageCompare(actualMessage, expectedMessage);
	}
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=14)
	public void viewP2PPromotionalTrfRuleCellGroup(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE14").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin can view promotional transfer rule at cell group level for date range");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE14").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).viewP2PPromotionalTransferRule(p2ppromodata,"CEL","DATE");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("promotrfrule.viewc2stransferrules.heading");
		Validator.messageCompare(actualMessage, expectedMessage);
	}
	
	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=15)
	public void deleteP2PPromotionalTrfRuleCellGroup(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE15").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin can delete promotional transfer rule at cell group level for date range");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE15").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).deleteP2PPromotionalTransferRule(p2ppromodata,"CEL","DATE");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("trfrule.deltrfrule.msg.success");
		Validator.messageCompare(actualMessage, expectedMessage);
	}
	
	
	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=16)
	public void addP2PPromotionalTrfRuleCellGroupLevelSingleRange(P2PPromotionalTrfRuleVO p2ppromodata){
		 moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE16").getModuleCode();
			
			if (TestCaseCounter == false) {
				test=extent.createTest(moduleCode);
				TestCaseCounter = true;
			}
			
			Log.startTestCase("To verify that Networkadmin can create promotional transfer rule at cell group level for Single time range");
			
			
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE16").getExtentCase());
			currentNode.assignCategory(assignCategory);
		
		Log.info(""+p2ppromodata.toString());
		String actualMessage = new P2PPromotionalTransferRule(driver).addP2PPromotionalTransferRule(p2ppromodata,"CEL","TIME","SINGLE");
			String expModMsg = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		String expAddMsg = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
		
		if (actualMessage.equals(expAddMsg)){
			Validator.messageCompare(actualMessage, expAddMsg);
		}
		else{
			Validator.messageCompare(actualMessage, expModMsg);
		}
	}
	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=17)
	public void modifyP2PPromotionalTrfRuleCellGroupLevelTimeRange(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE17").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin  can modify promotional transfer rule at cell group level for time range (Single/Multiple)");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE16").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).modifyP2PPromotionalTransferRule(p2ppromodata,"CEL","TIME");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		Validator.messageCompare(actualMessage, expectedMessage);
	}
	
	
	
	@Test(dataProvider="p2pPromotionalTransferData", priority=18)
	public void viewP2PPromotionalTrfRulecellgroupLevelTimeRange(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE18").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin  can view promotional transfer rule at cell group level for time range (Single/Multiple)");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE18").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).viewP2PPromotionalTransferRule(p2ppromodata,"CEL","TIME");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("promotrfrule.viewc2stransferrules.heading");
		Validator.messageCompare(actualMessage, expectedMessage);
	}

	@Test(dataProvider="p2pPromotionalTransferData", priority=19)
	public void deleteP2PPromotionalTrfRuleCellGroupLevelTimeRange(P2PPromotionalTrfRuleVO p2ppromodata){
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE19").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Log.startTestCase("To verify that Networkadmin  can delete promotional transfer rule at cell group level for time range (Single/Multiple)");
		
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE19").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
	    Log.info(""+p2ppromodata.toString());
	
		String actualMessage = new P2PPromotionalTransferRule(driver).deleteP2PPromotionalTransferRule(p2ppromodata,"CEL","TIME");
		Log.info(""+p2ppromodata.toString());
		String expectedMessage = MessagesDAO.prepareMessageByKey("trfrule.deltrfrule.msg.success");
		Validator.messageCompare(actualMessage, expectedMessage);
	}
	

	
	@Test(dataProvider="p2pPromotionalTransferData", priority=20)
	public void addP2PPromotionalTrfRuleCellGroupLevelMultipleRange(P2PPromotionalTrfRuleVO p2ppromodata){
		 moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE20").getModuleCode();
			
			if (TestCaseCounter == false) {
				test=extent.createTest(moduleCode);
				TestCaseCounter = true;
			}
			
			Log.startTestCase("To verify that Networkadmin can create promotional transfer rule at Cell Group level for Multiple time range");
			
			
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPROMOTRFRULE20").getExtentCase());
			currentNode.assignCategory(assignCategory);
		
		Log.info(""+p2ppromodata.toString());
		String actualMessage = new P2PPromotionalTransferRule(driver).addP2PPromotionalTransferRule(p2ppromodata,"CEL","TIME","MULTIPLE");
			String expModMsg = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
		String expAddMsg = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
		if (actualMessage.equals(expAddMsg)){
			Validator.messageCompare(actualMessage, expAddMsg);
		}
		else{
			Validator.messageCompare(actualMessage, expModMsg);
		}
		
		String act = new P2PPromotionalTransferRule(driver).deleteP2PPromotionalTransferRule(p2ppromodata, "CEL", "TIME");
		String exp = MessagesDAO.prepareMessageByKey("trfrule.deltrfrule.msg.success");
		if (act.equals(exp)){
			ExtentI.Markup(ExtentColor.TEAL, "Transfer rule deleted successfully");
		}
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="p2pPromotionalTransferData")
	public Object[] p2pPromotionalTrfRuleData(){

		String serviceType = _masterVO.getProperty("CreditTransferCode");
		String subscriberType = PretupsI.PREPAID_LOOKUP;
		String mobilenumber = DBHandler.AccessHandler.getP2PSubscriberMSISDN(subscriberType, "Y");
		
		ArrayList<String> subservicelist = ExtentI.columnbasedfilter(_masterVO.getProperty("DataProvider"), ExcelI.P2P_SERVICES_SHEET, ExcelI.SERVICE_TYPE, serviceType, ExcelI.SELECTOR_NAME);
		Object[] p2pPromoData = new Object[subservicelist.size()];
		
		for(int itr=0;itr<subservicelist.size();itr++){
			P2PPromotionalTrfRuleVO p2ptrfruleVO = new P2PPromotionalTrfRuleVO(driver);
			p2ptrfruleVO.setServicetype(_masterVO.getProperty("CreditTransferCode"));
			p2ptrfruleVO.setMobilenumber(mobilenumber);
			p2ptrfruleVO.setSubscribertype(subscriberType);
			
			p2ptrfruleVO.setSubservice(subservicelist.get(itr));

			String[] columns = new String[]{ExcelI.SERVICE_TYPE,ExcelI.SELECTOR_NAME};
			String[] values = new String[]{p2ptrfruleVO.getServicetype(),p2ptrfruleVO.getSubservice()};
			int row = ExtentI.combinationExistAtRow(columns, values, ExcelI.P2P_SERVICES_SHEET);
			String serviceName = ExtentI.fetchValuefromDataProviderSheet(ExcelI.P2P_SERVICES_SHEET, ExcelI.NAME, row);
			String cardgroupset = ExtentI.fetchValuefromDataProviderSheet(ExcelI.P2P_SERVICES_SHEET, ExcelI.PROMO_CARDGROUP_NAME, row);
			p2ptrfruleVO.setCardgroupset(cardgroupset);
			p2ptrfruleVO.setServiceName(serviceName);
			p2pPromoData[itr] = p2ptrfruleVO;
		}
		
		return p2pPromoData;
	}
	
}
