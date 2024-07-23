package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.MastersSubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.TransferRulesSubCategories;
import com.pageobjects.networkadminpages.p2ptransferrule.AddP2PTransferRulePage1;
import com.pageobjects.networkadminpages.p2ptransferrule.AddP2PTransferRulePage2;
import com.pageobjects.networkadminpages.p2ptransferrule.ModifyP2PTransferRulesConfirmPage;
import com.pageobjects.networkadminpages.p2ptransferrule.ModifyP2PTransferRulesPage;
import com.pageobjects.networkadminpages.p2ptransferrule.SelectP2PTransferRulesPage;
import com.pageobjects.networkadminpages.p2ptransferrule.ViewP2PTransferRule;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.CacheUpdate;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class P2PTransferRulesNegative {
	WebDriver driver;
	String MasterSheetPath;
	NetworkAdminHomePage homePage;
	TransferRulesSubCategories transferRuleSubLinks;
	Login login;
	AddP2PTransferRulePage1 addP2PTransferRulePage1;
	AddP2PTransferRulePage2 addP2PTransferRulePage2;
	MastersSubCategories mastersSubCategories;
	CacheUpdate updateCache;
	Map<String, String> userAccessMap;
	String[] result;
	SelectNetworkPage selectNetworkPage;
	SelectP2PTransferRulesPage selectP2PTransferRulesPage;
	ModifyP2PTransferRulesPage modifyP2PTransferRulesPage;
	ModifyP2PTransferRulesConfirmPage modifyP2PTransferRulesConfirmPage;
	ViewP2PTransferRule viewP2PTransferRule;
	int P2P_TRANSFER_RULE;

	public P2PTransferRulesNegative(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		transferRuleSubLinks = new TransferRulesSubCategories(driver);
		login = new Login();
		addP2PTransferRulePage1 = new AddP2PTransferRulePage1(driver);
		addP2PTransferRulePage2 = new AddP2PTransferRulePage2(driver);
		mastersSubCategories = new MastersSubCategories(driver);
		updateCache = new CacheUpdate(driver);
		userAccessMap = new HashMap<String, String>();
		selectNetworkPage = new SelectNetworkPage(driver);
		selectP2PTransferRulesPage = new SelectP2PTransferRulesPage(driver);
		modifyP2PTransferRulesPage = new ModifyP2PTransferRulesPage(driver);
		modifyP2PTransferRulesConfirmPage = new ModifyP2PTransferRulesConfirmPage(driver);
		viewP2PTransferRule = new ViewP2PTransferRule(driver);
		P2P_TRANSFER_RULE = Integer.parseInt(_masterVO.getClientDetail("P2PTRANSFERRULE_VER"));
	}

	public String allNull() {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddP2PTransferRule();
		addP2PTransferRulePage1.add();
		String msg = addP2PTransferRulePage1.getActualMsg();
		return msg;
	}

	public String addP2PReciverTypeNotSelected() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddP2PTransferRule();
		addP2PTransferRulePage1.requestGatewayCode("EXTGW");
		addP2PTransferRulePage1.senderSubscriberType(PretupsI.POSTPAID_SUB_LOOKUPS);
		addP2PTransferRulePage1.senderServiceClassID("ALL(ALL)");
		addP2PTransferRulePage1.add();
		System.out.println(addP2PTransferRulePage1.getActualMsg());
		return addP2PTransferRulePage1.getActualMsg();

	}

	public String addP2PServiceTypeNotSelected() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddP2PTransferRule();
		addP2PTransferRulePage1.requestGatewayCode("EXTGW");
		addP2PTransferRulePage1.senderSubscriberType(PretupsI.POSTPAID_SUB_LOOKUPS);
		addP2PTransferRulePage1.senderServiceClassID("ALL(ALL)");
		addP2PTransferRulePage1.receiverSubscriberType(PretupsI.POSTPAID_SUB_LOOKUPS);
		addP2PTransferRulePage1.receiverServiceClassID("ALL(ALL)");
		addP2PTransferRulePage1.add();

		return addP2PTransferRulePage1.getActualMsg();

	}

	public String addP2PEXTGWAndServiceTypeSelected() {
		
		String serviceType = _masterVO.getProperty("CreditTransferCode");
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();
		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			if((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(serviceType)))
			break;
		}
		System.out.println(i);
		String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddP2PTransferRule();
		addP2PTransferRulePage1.requestGatewayCode("EXTGW");
		addP2PTransferRulePage1.serviceType(service);
		addP2PTransferRulePage1.add();
		return addP2PTransferRulePage1.getActualMsg();
	}

	public String modifyP2PNothingSelected() {
		if (P2P_TRANSFER_RULE == 1) {
			userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_TRANSFER_RULE_ROLECODE);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			selectNetworkPage.selectNetwork();
			homePage.clickTransferRules();
			transferRuleSubLinks.clickP2PTransferRuleModification();
			selectP2PTransferRulesPage.clickOnModifyButton();
			return addP2PTransferRulePage1.getActualMsg();
		} else {
			return "P2P_Transfer Rules for this version of Pretups not exists";
		}

	}

	public String modifyP2PStatusNotSelected(String service, String subService, String cardGroup,
			String requestBearer) {
		if (P2P_TRANSFER_RULE == 1) {
			result = new String[4];
			// String requestBearer="ALL";
			String senderType = null;
			String receiverType = null;
			String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClass");
			String senderServiceClass = _masterVO.getProperty("SenderServiceClass");
			userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_P2P_TRANSFER_RULE_ROLECODE);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			selectNetworkPage.selectNetwork();
			homePage.clickTransferRules();
			transferRuleSubLinks.clickP2PTransferRuleModification();
			// if
			// (service.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET,
			// ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			// senderType = receiverType =
			// DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS,
			// PretupsI.SUBTP_LOOKUP);
			// } else {
			senderType = receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS,
					PretupsI.SUBTP_LOOKUP);
			// }
			// selectP2PTransferRulesPage.clickoncheckbox(requestBearer, senderType,
			// senderServiceClass, receiverType, receiverServiceClass, service, subService,
			// cardGroup);
			selectP2PTransferRulesPage.clickoncheckbox(requestBearer, senderType, senderServiceClass, receiverType,
					receiverServiceClass, service, subService, cardGroup);
			selectP2PTransferRulesPage.clickOnModifyButton();
			String updatedStatus = "";
			modifyP2PTransferRulesPage.changeStatus(updatedStatus);
			modifyP2PTransferRulesPage.clickOnModifyButton();
			return addP2PTransferRulePage1.getActualMsg();
		}
		else {
			return "P2P_Transfer Rules for this version of Pretups not exists";
		}

	}

}
