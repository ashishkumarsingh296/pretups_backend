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
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.geographicaldomain.AddGeographicalDomainConfirmPage;
import com.pageobjects.networkadminpages.geographicaldomain.AddGeographicalDomainPage;
import com.pageobjects.networkadminpages.geographicaldomain.GeograpichalDomainManagement;
import com.pageobjects.networkadminpages.geographicaldomain.ModifyGeograpichalDomainManagement;
import com.pageobjects.networkadminpages.geographicaldomain.ViewGeographicalDomainPage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;


public class SIT_GeograpichalDomainManagement extends BaseTest{
	
	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;
	Map<String,String> dataMap;
	
	
	@DataProvider(name = "Domain&CategoryProvider_validations")
	public Object[][] DomainCategoryProvider_validations() {
		
		GeograpichalDomainManagementMap geograpichalDomainManagementMap = new GeograpichalDomainManagementMap();
	
		String[] description=new String[16];
		description[0]="To verify that operator is unable to add Geographical Domain if Type is not selected";
		description[1]="To verify that operator is unable to add Geographical Domain if domainCode is null";
		description[2]="To verify that operator is unable to add Geographical Domain if domainName is null";
		description[3]="To verify that operator is unable to add Geographical Domain if domainShortName is null";
		description[4]="To verify that operator is able to add default Geographical Domain";
		description[5]="To verify that operator is unable to modify default Geographical Domain when status is not Selected";
		description[6]="To verify that operator is able to modify status for Geographical Domain";
		description[7]="To verify that operator is unable to delete Default Geographical Domain";
		description[8]="To verify that operator is able to modify Geographical Domain";
		description[9]="To verify that operator is able to delete Geographical Domain";
		
		Object[][] geoDomainData = {{0,description[0], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("domainType","")},
				                                 {1,description[1], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("domainCode", " ")},
												 {2,description[2], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("domainName", "")},
										         {3,description[3], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("domainShortName", "")},
											     {4,description[4],geograpichalDomainManagementMap.defaultMap()},
												 {5,description[5], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("status","")},
											  {6,description[6], geograpichalDomainManagementMap.getGeographicalDomainManagementMap("status",PretupsI.STATUS_SUSPENDED_LOOKUPS)},
												 {7,description[7],geograpichalDomainManagementMap.defaultMap()},
				                                {8,description[8],geograpichalDomainManagementMap.defaultMap()},
												{9,description[9],geograpichalDomainManagementMap.defaultMap()},
												};
		
		return geoDomainData;
	}
	
	@Test(dataProvider = "Domain&CategoryProvider_validations")
	public void testCycleSIT(int CaseNum,String Description, HashMap<String, String> mapParam) throws IOException{
		
     Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Geographical Domain Management");
			TestCaseCounter = true;
		}
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
		switch(CaseNum){
		
		case 0://To verify that operator is unable to add Geographical Domain if Type is not selected
			try{
				currentNode = test.createNode("To verify that operator is unable to add Geographical Domain if Type is not selected");
				currentNode.assignCategory("SIT");
				geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
			String actualMsg = geograpichalDomainManagement.getActualMsg();
			String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.selectgrphdomain.label.domaintype"));
			Validator.messageCompare(actualMsg, expectedMsg);
			}
			break;
			
		case 1://To verify that operator is unable to add Geographical Domain if domainCode is null
				currentNode = test.createNode("To verify that operator is unable to add Geographical Domain if domainCode is null");
				currentNode.assignCategory("SIT");
				geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			    actual = addGeographicalDomainPage.getActualMsg();
			    expected = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.selectgrphdomain.label.domaincode"));
			Validator.messageCompare(actual, expected);
			
			break;
			
		case 2://To verify that operator is unable to add Geographical Domain if domainName is null
				currentNode = test.createNode("To verify that operator is unable to add Geographical Domain if domainName is null");
				currentNode.assignCategory("SIT");
				geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			    actual = addGeographicalDomainPage.getActualMsg();
			    expected = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.viewgrphdomains.label.domainname"));
			Validator.messageCompare(actual, expected);
			
			break;
			
		case 3://To verify that operator is unable to add Geographical Domain if domainShortName is null
				currentNode = test.createNode("To verify that operator is unable to add Geographical Domain if domainShortName is null");
				currentNode.assignCategory("SIT");
				geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
                actual = addGeographicalDomainPage.getActualMsg();
			    expected = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.viewgrphdomains.label.domainshortname"));
			    Validator.messageCompare(actual, expected);
			break;
			
		case 4://To verify that operator is able to add default Geographical Domain

				currentNode = test.createNode("To verify that operator is able to add default Geographical Domain");
				currentNode.assignCategory("SIT");
				geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
				if(geograpichalDomainManagement.isDefault(mapParam))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Message Validation Failed");
					ExtentI.attachScreenShot();
					}
				

			break;
			
		case 5://To verify that operator is unable to modify Geographical Domain when status is not Selected
			try{
			currentNode = test.createNode("To verify that operator is unable to modify Geographical Domain when status is not Selected");
			currentNode.assignCategory("SIT");
			geogaphicalDomainManagement.modifyGeographicalDomain_SIT(mapParam);
			}
			catch(Exception e){
				Log.writeStackTrace(e);
			String actualMsg = modifyGeograpichalDomainManagement.getActualMsg();
			String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("grphdomain.viewgrphdomains.label.status"));
			Validator.messageCompare(actualMsg, expectedMsg);
			}

		break;
			
		case 6://To verify that operator is unable to modify Default Geographical Domain

			currentNode = test.createNode("To verify that operator is able to modify status for Geographical Domain");
			currentNode.assignCategory("SIT");
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			geogaphicalDomainManagement.modifyGeographicalDomain_SIT(mapParam);
			if(geograpichalDomainManagement.notModified(mapParam))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Message Validation Failed");
				ExtentI.attachScreenShot();
				}
			

		break;
		
		case 7://To verify that operator is unable to delete Default Geographical Domain
        
			currentNode = test.createNode("To verify that operator is unable to delete Default Geographical Domain");
			currentNode.assignCategory("SIT");
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			geogaphicalDomainManagement.deleteGeographicalDomain_SIT(mapParam);
            String actualMsg = viewGeographicalDomainPage.getActualMsg();
			String expectedMsg = MessagesDAO.prepareMessageByKey("grphdomain.operation.msg.deletedefaultProfile");
			Validator.messageCompare(actualMsg, expectedMsg);
			
			

		break;
		
		case 8://To verify that operator is able to modify Geographical Domain
	        
			currentNode = test.createNode("To verify that operator is able to modify Geographical Domain");
			currentNode.assignCategory("SIT");
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			domainNameCode = DBHandler.AccessHandler.fetchDomainNameCode(mapParam.get("domainType"));
			domainName = DBHandler.AccessHandler.fetchDomainName(domainNameCode);
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
			
			

		break;
		
          case 9://To verify that operator is able to delete Geographical Domain
	        
			currentNode = test.createNode("To verify that operator is able to delete Geographical Domain");
			currentNode.assignCategory("SIT");
			geogaphicalDomainManagement.addGeographicalDomain_SIT(mapParam);
			domainNameCode = DBHandler.AccessHandler.fetchDomainNameCode(mapParam.get("domainType"));
			domainName = DBHandler.AccessHandler.fetchDomainName(domainNameCode);
			//mapParam.put("domainName", domainName);
			viewGeographicalDomainPage.clickOnRadioButton(domainName);
			viewGeographicalDomainPage.clickModifyButton();
			modifyGeograpichalDomainManagement.selectIsDefault();
			modifyGeograpichalDomainManagement.clickModifyButton();
			addGeoDomainConfirmPage.clickConfirmModifyButton();
			
			geogaphicalDomainManagement.deleteGeographicalDomain_SIT(mapParam);
			actualMsg = viewGeographicalDomainPage.getActualMsg();
			expectedMsg = MessagesDAO.prepareMessageByKey("grphdomain.deletegrphdomain.msg.deletesuccess");
			Validator.messageCompare(actualMsg, expectedMsg);
			
			

		break;
		
		default: Log.info("No valid data found."); 
		}
	}
	
	//@Test(dataProvider="Domain&CategoryProvider_validations")
		public void testCycle1(int CaseNum, String Description, HashMap<String, String> mapParam){
			
			Log.startTestCase(this.getClass().getName());
		
			Log.info("" + CaseNum+" ,"+Description+" ,"+mapParam);
			
		}
	}


