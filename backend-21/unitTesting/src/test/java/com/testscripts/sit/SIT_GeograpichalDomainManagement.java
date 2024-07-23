package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.GeogaphicalDomainManagement;
import com.Features.mapclasses.GeograpichalDomainManagementMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.geographicaldomain.AddGeographicalDomainConfirmPage;
import com.pageobjects.networkadminpages.geographicaldomain.AddGeographicalDomainPage;
import com.pageobjects.networkadminpages.geographicaldomain.GeograpichalDomainManagement;
import com.pageobjects.networkadminpages.geographicaldomain.ModifyGeograpichalDomainManagement;
import com.pageobjects.networkadminpages.geographicaldomain.ViewGeographicalDomainPage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_GEOGRAPHICAL_DOMAIN_MANAGEMENT)
public class SIT_GeograpichalDomainManagement extends BaseTest{
	NetworkAdminHomePage networkAdminHomePage;
	Map<String,String> dataMap;
	String assignCategory="SIT";
	
	
	@DataProvider(name = "Domain&CategoryProvider_validations")
	public Object[][] DomainCategoryProvider_validations() {
		
		GeograpichalDomainManagementMap geograpichalDomainManagementMap = new GeograpichalDomainManagementMap();
	
		String[] description=new String[16];
		description[0]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT1").getExtentCase();
		description[1]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT2").getExtentCase();
		description[2]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT3").getExtentCase();
		description[3]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT4").getExtentCase();
		description[4]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT5").getExtentCase();
		/*description[5]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT6").getExtentCase();;*/
		description[6]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT7").getExtentCase();;
		description[7]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT8").getExtentCase();;
		description[8]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT9").getExtentCase();
		description[9]=_masterVO.getCaseMasterByID("SITGEODOMAINMGMT10").getExtentCase();;
		
		Object[][] geoDomainData = {{0,description[0], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("domainType","")},
				{1,description[1], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("domainCode", " ")},
				{2,description[2], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("domainName", "")},
				{3,description[3], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("domainShortName", "")},
				{4,description[4],geograpichalDomainManagementMap.defaultMap()},
				/*{5,description[5], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("status","")},*/
				{6,description[6], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("status",PretupsI.STATUS_SUSPENDED_LOOKUPS)},
				{7,description[7],geograpichalDomainManagementMap.defaultMap()},
				{8,description[8],geograpichalDomainManagementMap.defaultMap()},
				{9,description[9],geograpichalDomainManagementMap.defaultMap()},
		};
		
		return geoDomainData;
	}
	
	@Test(dataProvider = "Domain&CategoryProvider_validations")
	@TestManager(TestKey = "PRETUPS-879") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void testCycleSIT(int CaseNum,String Description, HashMap<String, String> mapParam) throws IOException{
		
		final String methodName = "Test_Geographical_Domain_Management";
		Log.startTestCase(methodName);
		GeogaphicalDomainManagement geogaphicalDomainManagement = new GeogaphicalDomainManagement(driver);
		GeograpichalDomainManagement geograpichalDomainManagement = new GeograpichalDomainManagement(driver);
		AddGeographicalDomainPage addGeographicalDomainPage = new AddGeographicalDomainPage(driver);
		ModifyGeograpichalDomainManagement modifyGeograpichalDomainManagement = new ModifyGeograpichalDomainManagement(driver);
		ViewGeographicalDomainPage viewGeographicalDomainPage = new ViewGeographicalDomainPage(driver);
		AddGeographicalDomainConfirmPage addGeoDomainConfirmPage = new AddGeographicalDomainConfirmPage(driver);
		
		String actual = null;
		String expected = null;
		String domainNameCode;
		String domainName;
		
		currentNode = test.createNode(Description);
		currentNode.assignCategory(assignCategory);
		
		switch(CaseNum){

		case 0://To verify that operator is unable to add Geographical Domain if Type is not selected
			try{
				geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = geograpichalDomainManagement.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.selectgrphdomain.label.domaintype"));
				Assertion.assertEquals(actualMsg, expectedMsg);
				
			}
			Assertion.completeAssertions();
			break;

		case 1://To verify that operator is unable to add Geographical Domain if domainCode is null
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			actual = addGeographicalDomainPage.getActualMsg();
			expected = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.selectgrphdomain.label.domaincode"));
			Assertion.assertEquals(actual, expected);
			
			Assertion.completeAssertions();
			break;

		case 2://To verify that operator is unable to add Geographical Domain if domainName is null
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			actual = addGeographicalDomainPage.getActualMsg();
			expected = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.viewgrphdomains.label.domainname"));
			Assertion.assertEquals(actual, expected);
			
			Assertion.completeAssertions();
			break;

		case 3://To verify that operator is unable to add Geographical Domain if domainShortName is null
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			actual = addGeographicalDomainPage.getActualMsg();
			expected = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.viewgrphdomains.label.domainshortname"));
			Assertion.assertEquals(actual, expected);
			Assertion.completeAssertions();
			break;

		case 4://To verify that operator is able to add default Geographical Domain
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			if(geograpichalDomainManagement.isDefault(mapParam))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Message Validation Failed");
				ExtentI.attachScreenShot();
			}
			Assertion.completeAssertions();
			break;

		case 5://To verify that operator is unable to modify Geographical Domain when status is not Selected
			try{
				geogaphicalDomainManagement.modifyGeographicalDomain_SIT(mapParam);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
				String actualMsg = modifyGeograpichalDomainManagement.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.viewgrphdomains.label.status"));
				Assertion.assertEquals(actualMsg, expectedMsg);
				
			}
			Assertion.completeAssertions();
			break;

		case 6://To verify that operator is unable to modify Default Geographical Domain
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			geogaphicalDomainManagement.modifyGeographicalDomain_SIT(mapParam);
			if(geograpichalDomainManagement.notModified(mapParam))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Message Validation Failed");
				ExtentI.attachScreenShot();
			}
			Assertion.completeAssertions();
			break;

		case 7://To verify that operator is unable to delete Default Geographical Domain
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			geogaphicalDomainManagement.deleteGeographicalDomain_SIT(mapParam);
			String actualMsg = viewGeographicalDomainPage.getActualMsg();
			String expectedMsg = MessagesDAO.prepareMessageByKey("grphdomain.operation.msg.deletedefaultProfile");
			Assertion.assertEquals(actualMsg, expectedMsg);
			Assertion.completeAssertions();
			break;

		case 8://To verify that operator is able to modify Geographical Domain
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			String parentGeography = mapParam.get("parent");
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			domainNameCode = DBHandler.AccessHandler.fetchDomainNameCode(mapParam.get("domainType"));
			domainName = DBHandler.AccessHandler.fetchDomainName(domainNameCode, networkCode, parentGeography);
			//mapParam.put("domainName", domainName);
			viewGeographicalDomainPage.clickOnRadioButton(mapParam.get("domainName"));
			viewGeographicalDomainPage.clickModifyButton();
			modifyGeograpichalDomainManagement.selectIsDefault();
			modifyGeograpichalDomainManagement.clickModifyButton();
			addGeoDomainConfirmPage.clickConfirmModifyButton();

			geogaphicalDomainManagement.modifyGeographicalDomain_SIT(mapParam);
			if(geograpichalDomainManagement.isModified(mapParam))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Message Validation Failed");
				ExtentI.attachScreenShot();
			}
			Assertion.completeAssertions();
			break;

		case 9://To verify that operator is able to delete Geographical Domain
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			String parentGeography1 = mapParam.get("parent");
			String networkCode1 = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			domainNameCode = DBHandler.AccessHandler.fetchDomainNameCode(mapParam.get("domainType"));
			domainName = DBHandler.AccessHandler.fetchDomainName(domainNameCode, networkCode1, parentGeography1);
			//mapParam.put("domainName", domainName);
			//viewGeographicalDomainPage.clickOnRadioButton(mapParam.get("domainName"));
			viewGeographicalDomainPage.clickOnRadioButton(domainName);
			viewGeographicalDomainPage.clickModifyButton();
			modifyGeograpichalDomainManagement.selectIsDefault();
			modifyGeograpichalDomainManagement.clickModifyButton();
			addGeoDomainConfirmPage.clickConfirmModifyButton();

			geogaphicalDomainManagement.deleteGeographicalDomain_SIT(mapParam);
			actualMsg = viewGeographicalDomainPage.getActualMsg();
			expectedMsg = MessagesDAO.prepareMessageByKey("grphdomain.deletegrphdomain.msg.deletesuccess");
			Assertion.assertEquals(actualMsg, expectedMsg);
			Assertion.completeAssertions();
			break;

		default: Log.info("No valid data found."); 
		}
		
		Log.endTestCase(methodName);
	}
	
	//@Test(dataProvider="Domain&CategoryProvider_validations")
		public void testCycle1(int CaseNum, String Description, HashMap<String, String> mapParam){
			
			Log.startTestCase(this.getClass().getName());
		
			Log.info("" + CaseNum+" ,"+Description+" ,"+mapParam);
			
		}
	}


