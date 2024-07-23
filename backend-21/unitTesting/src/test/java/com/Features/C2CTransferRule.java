/**
 * 
 */
package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.commons.PretupsI;
import com.pageobjects.networkadminpages.c2ctransferrule.AssociateC2CTransferRulePage1;
import com.pageobjects.networkadminpages.c2ctransferrule.AssociateC2CTransferRulePage2;
import com.pageobjects.networkadminpages.c2ctransferrule.AssociateC2CTransferRulePage3;
import com.pageobjects.networkadminpages.c2ctransferrule.AssociateC2CTransferRulePage4;
import com.pageobjects.networkadminpages.c2ctransferrule.C2CTransferRulesApprovalPage1;
import com.pageobjects.networkadminpages.c2ctransferrule.C2CTransferRulesApprovalPage2;
import com.pageobjects.networkadminpages.c2ctransferrule.InitiateC2CTransferRulePage1;
import com.pageobjects.networkadminpages.c2ctransferrule.InitiateC2CTransferRulePage2;
import com.pageobjects.networkadminpages.c2ctransferrule.InitiateC2CTransferRulePage3;
import com.pageobjects.networkadminpages.c2ctransferrule.InitiateC2CTransferRulePage4;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.TransferRulesSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Assertion;
import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class C2CTransferRule extends BaseTest {

	WebDriver driver = null;
	public static String RuleType;

	NetworkAdminHomePage homePage;
	TransferRulesSubCategories TransferRuleSubLinks;
	Login userlogin;
	AssociateC2CTransferRulePage1 TrfRulePg_1;
	AssociateC2CTransferRulePage2 TrfRulePg_2;
	AssociateC2CTransferRulePage3 TrfRulePg_3;
	AssociateC2CTransferRulePage4 TrfRulePg_4;
	InitiateC2CTransferRulePage1 C2CTransferRulePage_1;
	InitiateC2CTransferRulePage2 C2CTransferRulePage_2;
	InitiateC2CTransferRulePage3 C2CTransferRulePage_3;
	InitiateC2CTransferRulePage4 C2CTransferRulePage_4;
	C2CTransferRulesApprovalPage1 C2CTransferRuleAppPage_1;
	C2CTransferRulesApprovalPage2 C2CTransferRuleAppPage_2;
	Map<String, String> userAccessMap;
	SelectNetworkPage selectNetworkPage;

	public C2CTransferRule(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		TransferRuleSubLinks = new TransferRulesSubCategories(driver);
		userlogin = new Login();
		TrfRulePg_1 = new AssociateC2CTransferRulePage1(driver);
		TrfRulePg_2 = new AssociateC2CTransferRulePage2(driver);
		TrfRulePg_3 = new AssociateC2CTransferRulePage3(driver);
		TrfRulePg_4 = new AssociateC2CTransferRulePage4(driver);
		C2CTransferRulePage_1 = new InitiateC2CTransferRulePage1(driver);
		C2CTransferRulePage_2 = new InitiateC2CTransferRulePage2(driver);
		C2CTransferRulePage_3 = new InitiateC2CTransferRulePage3(driver);
		C2CTransferRulePage_4 = new InitiateC2CTransferRulePage4(driver);
		C2CTransferRuleAppPage_1 = new C2CTransferRulesApprovalPage1(driver);
		C2CTransferRuleAppPage_2 = new C2CTransferRulesApprovalPage2(driver);
		userAccessMap = new HashMap<String, String>();
		selectNetworkPage = new SelectNetworkPage(driver);
	}

	public void channeltochannelTrfRule(String ToDomain, String ToCategory, String Services, String FromCategory, String FromDomain) {
		final String methodname = "channeltochannelTrfRule";
		Log.methodEntry(methodname, ToDomain, ToCategory, Services, FromCategory, FromDomain);

		userlogin.UserLogin(driver, "Operator", PretupsI.SUPERADMIN_CATCODE, PretupsI.NETWORKADMIN_CATCODE);
		selectNetworkPage.selectNetwork();

		homePage.clickTransferRules();
		boolean AssociateC2CTrfLink = TransferRuleSubLinks.checkIfAssociateC2CTransferRuleLinkExists();
		RuleType = Services;
		if (AssociateC2CTrfLink == true) {
			TransferRuleSubLinks.clickAssociateC2CTransferRule();
			TrfRulePg_1.selectFromDomainName(FromDomain);
			TrfRulePg_1.selectToDomainName(ToDomain);
			TrfRulePg_1.clickSubmit();

			boolean TransferRuleStatus = TrfRulePg_2.checkIfTransferRuleExists(FromCategory, ToCategory);
			if (TransferRuleStatus == false) {
				TrfRulePg_2.clickAdd();

				TrfRulePg_3.selectFromCategory(FromCategory);
				TrfRulePg_3.selectToCategory(ToCategory);
				TrfRulePg_3.clickParentAssociationAllowed_Yes();
				TrfRulePg_3.selectTransferType();

				TrfRulePg_3.clickTransferAllowed_Yes();
				TrfRulePg_3.chnlbypassTransferAllowed_Yes();
				TrfRulePg_3.controlTrfLevel();

				TrfRulePg_3.clickWithdrawlAllowed_Yes();
				TrfRulePg_3.chnlbypassWithdrawAllowed_Yes();
				TrfRulePg_3.controlWithDrawLevel();

				TrfRulePg_3.clickReturnAllowed_Yes();
				TrfRulePg_3.chnlbypassReturnAllowed_Yes();
				TrfRulePg_3.controlRtrLevel();

				TrfRulePg_3.selectAllProducts();
				TrfRulePg_3.clickAddButton();

				TrfRulePg_4.clickConfirmButton();

			} else {
				Assertion.assertSkip("C2C Transfer Rule from " + FromCategory + " to " + ToCategory + " category already exists.");
			}
		} else {
			TransferRuleSubLinks.clickC2CInitiateTR();
			C2CTransferRulePage_1.selectDomainName(ToDomain);
			C2CTransferRulePage_1.selectRuleType(RuleType);
			C2CTransferRulePage_1.clickSubmit();

			boolean TransferRuleStatus = C2CTransferRulePage_2.checkIfTransferRuleExists(FromCategory, ToCategory);

			if (TransferRuleStatus == false) {
				C2CTransferRulePage_2.clickAdd();
				C2CTransferRulePage_3.selectFromCategory(FromCategory);
				C2CTransferRulePage_3.selectToCategory(ToCategory);
				C2CTransferRulePage_3.parentAssociationAllowed_Yes();
				C2CTransferRulePage_3.clickTransferAllowed_Yes();
				C2CTransferRulePage_3.channelByPassTransfer_Yes();
				C2CTransferRulePage_3.controlTrfLevel();
				C2CTransferRulePage_3.clickWithdrawlAllowed_Yes();
				C2CTransferRulePage_3.channelByPassWithdraw_Yes();
				C2CTransferRulePage_3.controlWithdrawLevel();
				C2CTransferRulePage_3.selectAllProducts();
				C2CTransferRulePage_3.clickAddButton();
				C2CTransferRulePage_4.clickConfirmButton();
				TransferRuleSubLinks.clickC2CTransferRuleApproval();
				C2CTransferRuleAppPage_1.selectTransferRuleForApproval(FromCategory, ToCategory);
				C2CTransferRuleAppPage_1.clickSubmitButton();
				C2CTransferRuleAppPage_2.clickApproveButton();
				C2CTransferRuleAppPage_2.PressOkOnConfirmDialog();
			} else {
				Assertion.assertSkip("C2C Transfer Rule from " + FromCategory + " to " + ToCategory + " category already exists.");
			}
		}
		Assertion.completeAssertions();
		Log.methodExit(methodname);
	}
}
