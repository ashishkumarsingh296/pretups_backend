package com.Features;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.commons.PretupsI;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.TransferRulesSubCategories;
import com.pageobjects.networkadminpages.o2ctransferrule.AssociateO2CTransferRulePage1;
import com.pageobjects.networkadminpages.o2ctransferrule.AssociateO2CTransferRulePage2;
import com.pageobjects.networkadminpages.o2ctransferrule.AssociateO2CTransferRulePage3;
import com.pageobjects.networkadminpages.o2ctransferrule.AssociateO2CTransferRulePage4;
import com.pageobjects.networkadminpages.o2ctransferrule.InitiateO2CTransferRulePage1;
import com.pageobjects.networkadminpages.o2ctransferrule.InitiateO2CTransferRulePage2;
import com.pageobjects.networkadminpages.o2ctransferrule.InitiateO2CTransferRulePage3;
import com.pageobjects.networkadminpages.o2ctransferrule.InitiateO2CTransferRulePage4;
import com.pageobjects.networkadminpages.o2ctransferrule.O2CTransferRulesApprovalPage1;
import com.pageobjects.networkadminpages.o2ctransferrule.O2CTransferRulesApprovalPage2;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class O2CTransferRule extends BaseTest {

	WebDriver driver = null;

	Login login;
	NetworkAdminHomePage homePage;
	TransferRulesSubCategories TransferRuleSubLinks;

	AssociateO2CTransferRulePage1 AssociateTransferRulePage_1;
	AssociateO2CTransferRulePage2 AssociateTransferRulePage_2;
	AssociateO2CTransferRulePage3 AssociateTransferRulePage_3;
	AssociateO2CTransferRulePage4 AssociateTransferRulePage_4;

	InitiateO2CTransferRulePage1 InitiateTransferRulePage_1;
	InitiateO2CTransferRulePage2 InitiateTransferRulePage_2;
	InitiateO2CTransferRulePage3 InitiateTransferRulePage_3;
	InitiateO2CTransferRulePage4 InitiateTransferRulePage_4;
	O2CTransferRulesApprovalPage1 TransferRuleApprovalPage_1;
	O2CTransferRulesApprovalPage2 TransferRuleApprovalPage_2;
	
	SelectNetworkPage networkPage;

	public O2CTransferRule(WebDriver driver) {
		this.driver = driver;

		login = new Login();
		homePage = new NetworkAdminHomePage(driver);
		TransferRuleSubLinks = new TransferRulesSubCategories(driver);

		// Associate O2C Transfer Rule Link Pages
		AssociateTransferRulePage_1 = new AssociateO2CTransferRulePage1(driver);
		AssociateTransferRulePage_2 = new AssociateO2CTransferRulePage2(driver);
		AssociateTransferRulePage_3 = new AssociateO2CTransferRulePage3(driver);
		AssociateTransferRulePage_4 = new AssociateO2CTransferRulePage4(driver);

		// Initiate O2C Transfer Rule Link Pages
		InitiateTransferRulePage_1 = new InitiateO2CTransferRulePage1(driver);
		InitiateTransferRulePage_2 = new InitiateO2CTransferRulePage2(driver);
		InitiateTransferRulePage_3 = new InitiateO2CTransferRulePage3(driver);
		InitiateTransferRulePage_4 = new InitiateO2CTransferRulePage4(driver);
		TransferRuleApprovalPage_1 = new O2CTransferRulesApprovalPage1(driver);
		TransferRuleApprovalPage_2 = new O2CTransferRulesApprovalPage2(driver);
		
		networkPage = new SelectNetworkPage(driver);
	}

	// Create O2C Transfer Rule
	public Object[] createTransferRule(String ToDomain, String ToCategory, String Services, String FirstApprovalLimit, String SecondApprovalLimit) {
		final String methodname = "createTransferRule";
		Log.methodEntry(methodname, ToDomain, ToCategory, Services, FirstApprovalLimit, SecondApprovalLimit);
		
		Object resultObj[] = new Object[3];
		login.UserLogin(driver, "Operator", PretupsI.SUPERADMIN_CATCODE, PretupsI.NETWORKADMIN_CATCODE);
		networkPage.selectNetwork();
		homePage.clickTransferRules();
		resultObj[0] = TransferRuleSubLinks.checkIfAssociateO2CTransferRuleLinkExists();

		if (resultObj[0].equals(true)) {

			TransferRuleSubLinks.clickAssociateO2CTransferRule();
			AssociateTransferRulePage_1.selectDomainName(ToDomain);
			AssociateTransferRulePage_1.clickSubmit();
			resultObj[1] = AssociateTransferRulePage_2.checkIfTransferRuleExists(ToCategory);
			if (resultObj[1].equals(false)) {
				AssociateTransferRulePage_2.clickAdd();
				AssociateTransferRulePage_3.selectToCategory(ToCategory);
				AssociateTransferRulePage_3.selectServices(Services, FirstApprovalLimit, SecondApprovalLimit);
				AssociateTransferRulePage_3.selectAllProducts();
				AssociateTransferRulePage_3.clickAddButton();
				AssociateTransferRulePage_4.clickConfirmButton();
				resultObj[2] = AssociateTransferRulePage_4.getMessage();
				homePage.clickLogout();
			} else {
				currentNode.log(Status.PASS, "O2C Transfer Rule for " + ToCategory + " category already exists.");
			}

		} else {
			TransferRuleSubLinks.clickO2CInitiateTR();
			InitiateTransferRulePage_1.selectDomainName(ToDomain);
			InitiateTransferRulePage_1.clickSubmit();
			resultObj[1] = InitiateTransferRulePage_2.checkIfTransferRuleExists(ToCategory);
			if (resultObj[1].equals(false)) {
				InitiateTransferRulePage_2.clickAdd();
				InitiateTransferRulePage_3.selectToCategory(ToCategory);
				InitiateTransferRulePage_3.selectServices(Services, FirstApprovalLimit, SecondApprovalLimit);
				InitiateTransferRulePage_3.selectAllProducts();
				InitiateTransferRulePage_3.clickAddButton();
				InitiateTransferRulePage_4.clickConfirmButton();
				resultObj[2] = InitiateTransferRulePage_4.getMessage();
				homePage.clickLogout();
			} else {
				currentNode.log(Status.PASS, "O2C Transfer Rule for " + ToCategory + " category already exists.");
			}
		}
		
		Log.methodExit(methodname);
		return resultObj;
	}
	
	//O2C Transfer Rule Approval
	public void approveTransferRule(String ToCategory) {
		final String methodname = "approveTransferRule";
		Log.methodEntry(methodname, ToCategory);
		
		login.UserLogin(driver, "Operator", PretupsI.SUPERADMIN_CATCODE, PretupsI.NETWORKADMIN_CATCODE);
		networkPage.selectNetwork();
		homePage.clickTransferRules();
		TransferRuleSubLinks.clickO2CTransferRuleApproval();
		TransferRuleApprovalPage_1.selectTransferRuleForApproval(ToCategory);
		TransferRuleApprovalPage_1.clickSubmitButton();
		TransferRuleApprovalPage_2.clickApproveButton();
		TransferRuleApprovalPage_2.PressOkOnConfirmDialog();
		
		Log.methodExit(methodname);
	}
	
	
	// Modify O2C Transfer Rule
	public Object[] modifyTransferRule(String ToDomain, String ToCategory, String FirstApprovalLimit, String SecondApprovalLimit) {
		Object resultObj[] = new Object[3];
		login.UserLogin(driver, "Operator", PretupsI.SUPERADMIN_CATCODE, PretupsI.NETWORKADMIN_CATCODE);
		networkPage.selectNetwork();
		homePage.clickTransferRules();
		resultObj[0] = TransferRuleSubLinks.checkIfAssociateO2CTransferRuleLinkExists();

		if (resultObj[0].equals(true)) {

			TransferRuleSubLinks.clickAssociateO2CTransferRule();
			AssociateTransferRulePage_1.selectDomainName(ToDomain);
			AssociateTransferRulePage_1.clickSubmit();
			resultObj[1] = AssociateTransferRulePage_2.checkIfTransferRuleExists(ToCategory);
			if (resultObj[1].equals(true)) {
				AssociateTransferRulePage_2.selectTransferRule(ToCategory);
				AssociateTransferRulePage_2.clickModify();
				AssociateTransferRulePage_3.inputFirstApprovalLimit(FirstApprovalLimit);
				AssociateTransferRulePage_3.inputSecondApprovalLimit(SecondApprovalLimit);
				AssociateTransferRulePage_3.clickModifyButton();
				AssociateTransferRulePage_4.clickConfirmModifyButton();
			} else {
				currentNode.skip("O2C Transfer Rule for " + ToCategory + " category does not exist");
			}

		} else {
			TransferRuleSubLinks.clickO2CInitiateTR();
			InitiateTransferRulePage_1.selectDomainName(ToDomain);
			InitiateTransferRulePage_1.clickSubmit();
			resultObj[1] = InitiateTransferRulePage_2.checkIfTransferRuleExists(ToCategory);
			if (resultObj[1].equals(true)) {
				InitiateTransferRulePage_2.selectTransferRule(ToCategory);
				InitiateTransferRulePage_2.clickModify();
				InitiateTransferRulePage_3.inputFirstApprovalLimit(FirstApprovalLimit);
				InitiateTransferRulePage_3.inputSecondApprovalLimit(SecondApprovalLimit);
				InitiateTransferRulePage_3.clickModifyButton();
				InitiateTransferRulePage_4.clickConfirmModifyButton();
				resultObj[2] = InitiateTransferRulePage_4.getMessage();
				homePage.clickLogout();
			} else {
				currentNode.skip("O2C Transfer Rule for " + ToCategory + " category already exists.");
			}
		}
		
		return resultObj;
	}
	
	// Create O2C Transfer Rule
		public Object[] createTransferRule_SIT(HashMap<String, String> mapParam, String FirstApprovalLimit, String SecondApprovalLimit) {
			final String methodname = "createTransferRule";
			Log.methodEntry(methodname, mapParam.get("toDomain"), mapParam.get("toCategory"), mapParam.get("services"), FirstApprovalLimit, SecondApprovalLimit);
			
			Object resultObj[] = new Object[3];
			login.UserLogin(driver, "Operator", PretupsI.SUPERADMIN_CATCODE, PretupsI.NETWORKADMIN_CATCODE);
			networkPage.selectNetwork();
			homePage.clickTransferRules();
			resultObj[0] = TransferRuleSubLinks.checkIfAssociateO2CTransferRuleLinkExists();

			if (resultObj[0].equals(true)) {

				TransferRuleSubLinks.clickAssociateO2CTransferRule();
				if(!mapParam.get("toDomain").equalsIgnoreCase("")){
				AssociateTransferRulePage_1.selectDomainName(mapParam.get("toDomain"));
			}
				AssociateTransferRulePage_1.clickSubmit();
				resultObj[1] = AssociateTransferRulePage_2.checkIfTransferRuleExists(mapParam.get("toCategory"));
				if (resultObj[1].equals(false)) {
					AssociateTransferRulePage_2.clickAdd();
					if(!mapParam.get("toCategory").equalsIgnoreCase(""))
					AssociateTransferRulePage_3.selectToCategory(mapParam.get("toCategory"));
					if(FirstApprovalLimit != null || SecondApprovalLimit != null)
					AssociateTransferRulePage_3.selectServices(mapParam.get("services"), FirstApprovalLimit, SecondApprovalLimit);
					AssociateTransferRulePage_3.selectAllProducts();
					AssociateTransferRulePage_3.clickAddButton();
					AssociateTransferRulePage_4.clickConfirmButton();
					resultObj[2] = AssociateTransferRulePage_4.getMessage();
					homePage.clickLogout();
				} else {
					currentNode.log(Status.PASS, "O2C Transfer Rule for " + mapParam.get("toCategory") + " category already exists.");
				}

			} else {
				TransferRuleSubLinks.clickO2CInitiateTR();
				InitiateTransferRulePage_1.selectDomainName(mapParam.get("toDomain"));
				InitiateTransferRulePage_1.clickSubmit();
				resultObj[1] = InitiateTransferRulePage_2.checkIfTransferRuleExists(mapParam.get("toCategory"));
				if (resultObj[1].equals(false)) {
					InitiateTransferRulePage_2.clickAdd();
					InitiateTransferRulePage_3.selectToCategory(mapParam.get("toCategory"));
					InitiateTransferRulePage_3.selectServices(mapParam.get("services"), FirstApprovalLimit, SecondApprovalLimit);
					InitiateTransferRulePage_3.selectAllProducts();
					InitiateTransferRulePage_3.clickAddButton();
					InitiateTransferRulePage_4.clickConfirmButton();
					resultObj[2] = InitiateTransferRulePage_4.getMessage();
					homePage.clickLogout();
				} else {
					currentNode.log(Status.PASS, "O2C Transfer Rule for " + mapParam.get("toCategory") + " category already exists.");
				}
			}
			
			Log.methodExit(methodname);
			return resultObj;
		}
		
}
