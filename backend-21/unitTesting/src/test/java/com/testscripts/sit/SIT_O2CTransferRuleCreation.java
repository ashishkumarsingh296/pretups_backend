package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransferRule;
import com.Features.mapclasses.O2CTransferRuleCreationMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.networkadminpages.o2ctransferrule.AssociateO2CTransferRulePage1;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_O2C_Transfer_Rule_Creation)
public class SIT_O2CTransferRuleCreation  extends BaseTest{
	
	String MasterSheetPath;
	Object[][] TransferRuleCategories;
	String FirstApprovalLimit;
	String SecondApprovalLimit;
	static boolean TestCaseCounter = false;

	@DataProvider(name = "O2CTransferRule_validations")
	public Object[][] O2CTransferRule_validations() {
		
		O2CTransferRuleCreationMap o2CTransferRuleCreationMap = new O2CTransferRuleCreationMap();
		String[] description=new String[9];
		description[0]=_masterVO.getCaseMasterByID("SITO2CTRFRULE1").getExtentCase();
		description[1]=_masterVO.getCaseMasterByID("SITO2CTRFRULE2").getExtentCase();
		description[2]=_masterVO.getCaseMasterByID("SITO2CTRFRULE3").getExtentCase();
		description[3]=_masterVO.getCaseMasterByID("SITO2CTRFRULE4").getExtentCase();
		description[4]=_masterVO.getCaseMasterByID("SITO2CTRFRULE5").getExtentCase();
		description[5]=_masterVO.getCaseMasterByID("SITO2CTRFRULE6").getExtentCase();
		description[6]=_masterVO.getCaseMasterByID("SITO2CTRFRULE7").getExtentCase();
		description[7]=_masterVO.getCaseMasterByID("SITO2CTRFRULE8").getExtentCase();
		description[8]=_masterVO.getCaseMasterByID("SITO2CTRFRULE9").getExtentCase();
		
		Object[][] o2CTransferRuleData = {{0,description[0], o2CTransferRuleCreationMap.getO2CTransferMap("toDomain","")},
				//{1,description[1], o2CTransferRuleCreationMap.getO2CTransferMap("toCategory","")},
				{2,description[2], o2CTransferRuleCreationMap.getO2CTransferMap("FirstApprovalLimit","")},
				{3,description[3], o2CTransferRuleCreationMap.getO2CTransferMap("SecondApprovalLimit","")},
				{4,description[4], o2CTransferRuleCreationMap.getO2CTransferMap("isProductAvailable","No")},
				{5,description[5], o2CTransferRuleCreationMap.getO2CTransferMap("FirstApprovalLimit","abc")},
				{6,description[6], o2CTransferRuleCreationMap.getO2CTransferMap("SecondApprovalLimit","abc")},
				{7,description[7], o2CTransferRuleCreationMap.getO2CTransferMap("SecondApprovalLimit","1000")},
				{8,description[8], o2CTransferRuleCreationMap.defaultMap()},
				};
		
		return o2CTransferRuleData;
		
	}
	
	@Test(dataProvider = "O2CTransferRule_validations")
	@TestManager(TestKey = "PRETUPS-370") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void testCycleSIT(int CaseNum,String Description, HashMap<String, String> mapParam) throws IOException{
		final String methodName = "Test_O2CTransferRuleCreation";
        Log.startTestCase(methodName);
		String assignCategory="SIT";
		
		O2CTransferRule o2CTransferRule = new O2CTransferRule(driver);
		AssociateO2CTransferRulePage1 associateTransferRulePage_1 = new AssociateO2CTransferRulePage1(driver);
		
		currentNode = test.createNode(Description);
		currentNode.assignCategory(assignCategory);
		
		switch(CaseNum){

		case 0://To verify that operator is unable to add O2C Transfer Rule if Domain is not selected
			try{
				o2CTransferRule.createTransferRule_SIT(mapParam, FirstApprovalLimit, SecondApprovalLimit);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = associateTransferRulePage_1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("o2ctrfrule.selectcategorydomain.label.domain"));
				Assertion.assertEquals(actualMsg, expectedMsg);
			}
			Assertion.completeAssertions();
			break;

		case 1://To verify that operator is unable to add O2C Transfer Rule if Category is null
			try{
				o2CTransferRule.createTransferRule_SIT(mapParam, FirstApprovalLimit, SecondApprovalLimit);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = associateTransferRulePage_1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("channeltrfrule.addtrfrule.msg.addtocategory"));
				Assertion.assertEquals(actualMsg, expectedMsg);
			}
			Assertion.completeAssertions();
			break;

		case 2://To verify that operator is unable to add O2C Transfer Rule if firstApprovalLimit is null
			try{
				o2CTransferRule.createTransferRule_SIT(mapParam, FirstApprovalLimit , SecondApprovalLimit);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = associateTransferRulePage_1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("channeltrfrule.firstapplimit.required");
				Assertion.assertEquals(actualMsg, expectedMsg);
			}
			Assertion.completeAssertions();
			break;

		case 3://To verify that operator is unable to add O2C Transfer Rule if SecondApprovalLimit is null
			try{
				o2CTransferRule.createTransferRule_SIT(mapParam, FirstApprovalLimit , SecondApprovalLimit);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = associateTransferRulePage_1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("channeltrfrule.secondapplimit.required");
				Assertion.assertEquals(actualMsg, expectedMsg);
			}
			Assertion.completeAssertions();
			break;

		case 4://To verify that operator is unable to add O2C Transfer Rule if no product is selected
			try{
				o2CTransferRule.createTransferRule_SIT(mapParam, FirstApprovalLimit , SecondApprovalLimit);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = associateTransferRulePage_1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("operatortrfrule.includeassociatetrfrule.error.productrequired");
				Assertion.assertEquals(actualMsg, expectedMsg);
			}
			Assertion.completeAssertions();
			break;

		case 5://To verify that operator is unable to add O2C Transfer Rule if firstApprovalLimit is not numeric
			try{
				o2CTransferRule.createTransferRule_SIT(mapParam, FirstApprovalLimit , SecondApprovalLimit);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = associateTransferRulePage_1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("operatortrfrule.includeassociatetrfrule.error.numeric", MessagesDAO.getLabelByKey("operatortrfrule.includeassociatetrfrule.label.firstapplimit"));
				Assertion.assertEquals(actualMsg, expectedMsg);
			}
			Assertion.completeAssertions();
			break;

		case 6://To verify that operator is unable to add O2C Transfer Rule if SecondApprovalLimit is not numeric
			try{
				o2CTransferRule.createTransferRule_SIT(mapParam, FirstApprovalLimit , SecondApprovalLimit);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = associateTransferRulePage_1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("operatortrfrule.includeassociatetrfrule.error.numeric", MessagesDAO.getLabelByKey("operatortrfrule.associatetrfrule.label.secondapplimit"));
				Assertion.assertEquals(actualMsg, expectedMsg);
			}
			Assertion.completeAssertions();
			break;


		case 7://To verify that operator is unable to add O2C Transfer Rule if SecondApprovalLimit is less than FirstApprovalLimit
			try{
				o2CTransferRule.createTransferRule_SIT(mapParam, FirstApprovalLimit , SecondApprovalLimit);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = associateTransferRulePage_1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("operatortrfrule.includeassociatetrfrule.error.secondgtfirst");
				Assertion.assertEquals(actualMsg, expectedMsg);
			}
			Assertion.completeAssertions();
			break;


		case 8://To verify that operator is able to view successfully added O2C Transfer Rule
			try{
				o2CTransferRule.createTransferRule_SIT(mapParam, FirstApprovalLimit , SecondApprovalLimit);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = associateTransferRulePage_1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("operatortrfrule.includeassociatetrfrule.error.secondgtfirst");
				Assertion.assertEquals(actualMsg, expectedMsg);
			}
			Assertion.completeAssertions();
			break;
		}
		
		Log.endTestCase(methodName);
		}

}
