package com.Features;

import java.text.ParseException;
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
import com.utils.ExtentI;
import com.utils._masterVO;

public class P2PTransferRules {

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
	
	public P2PTransferRules(WebDriver driver) {
		
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

	}

	public String[] addP2PTransferRules(String service, String subService, String cardGroup, boolean uap, String requestBearer) {

		result = new String[4];
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClass");
		String senderServiceClass = _masterVO.getProperty("SenderServiceClass");
		//String csvSplit = ",";
		//String[] requestBearerArray = requestBearer.split(csvSplit);
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddP2PTransferRule();
		if (_masterVO.getClientDetail("P2PTRANSFERRULE_VER").equalsIgnoreCase("1"))
		addP2PTransferRulePage1.requestGatewayCode(requestBearer);

		if (service.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addP2PTransferRulePage1.senderSubscriberType(PretupsI.POSTPAID_SUB_LOOKUPS);
			addP2PTransferRulePage1.receiverSubscriberType(PretupsI.POSTPAID_SUB_LOOKUPS);
		} else {
			addP2PTransferRulePage1.senderSubscriberType(PretupsI.PREPAID_SUB_LOOKUPS);
			addP2PTransferRulePage1.receiverSubscriberType(PretupsI.PREPAID_SUB_LOOKUPS);
		}
		addP2PTransferRulePage1.senderServiceClassID(senderServiceClass);
		addP2PTransferRulePage1.receiverServiceClassID(receiverServiceClass);
		addP2PTransferRulePage1.serviceType(service);
		addP2PTransferRulePage1.subServiceTypeId(subService);
		addP2PTransferRulePage1.cardGroupSetID(cardGroup);
		addP2PTransferRulePage1.add();
		addP2PTransferRulePage2.confirm();
		result[0] = addP2PTransferRulePage1.getActualMsg();
		String msg = addP2PTransferRulePage1.getActualMsg();
		result[1] = service;
		result[2] = subService;
		result[3] = cardGroup;
		updateCache.updateCache();
		/*if(uap == true && msg.equals(MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist",
				"1")))
		{
			activateP2PTransferRules(requestBearer,service, subService, cardGroup,
					PretupsI.STATUS_ACTIVE_LOOKUPS, cardGroup);
		}*/
		return result;
	}
	
	
	public String[] addVoucherTransferRules(String service, String subService, String cardGroup, boolean uap, String requestBearer) {

		result = new String[4];
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClass");
		String senderServiceClass = _masterVO.getProperty("SenderServiceClass");
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddP2PTransferRule();
		if (_masterVO.getClientDetail("P2PTRANSFERRULE_VER").equalsIgnoreCase("1"))
		addP2PTransferRulePage1.requestGatewayCode(requestBearer);
		addP2PTransferRulePage1.receiverSubscriberType(PretupsI.PREPAID_SUB_LOOKUPS);
		addP2PTransferRulePage1.senderSubscriberType(PretupsI.PREPAID_SUB_LOOKUPS);
		addP2PTransferRulePage1.senderServiceClassID(senderServiceClass);
		addP2PTransferRulePage1.receiverServiceClassID(receiverServiceClass);
		addP2PTransferRulePage1.serviceType(service);
		addP2PTransferRulePage1.subServiceTypeId(subService);
		addP2PTransferRulePage1.cardGroupSetID(cardGroup);
		addP2PTransferRulePage1.add();
		addP2PTransferRulePage2.confirm();
		result[0] = addP2PTransferRulePage1.getActualMsg();
		String msg = addP2PTransferRulePage1.getActualMsg();
		result[1] = service;
		result[2] = subService;
		result[3] = cardGroup;
		updateCache.updateCache();
		/*if(uap == true && msg.equals(MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist",
				"1")))
		{
			activateP2PTransferRules(requestBearer,service, subService, cardGroup,
					PretupsI.STATUS_ACTIVE_LOOKUPS, cardGroup);
		}*/
		return result;
	}

	

	public String[] modifyP2PTransferRules(String requestBearer,String service, String subService, String cardGroup,
			String updatedStatus, String updatedCardGroup) {

		result = new String[4];
		String senderType = null;
		String receiverType = null;
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClass");
		String senderServiceClass = _masterVO.getProperty("SenderServiceClass");
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickP2PTransferRuleModification();

		if (service.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			senderType = receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		} else {
			senderType = receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		}
		selectP2PTransferRulesPage.clickoncheckbox(requestBearer,senderType, senderServiceClass, receiverType, receiverServiceClass,
				service, subService, cardGroup);
		selectP2PTransferRulesPage.clickOnModifyButton();
		modifyP2PTransferRulesPage.changeStatus(updatedStatus);
		modifyP2PTransferRulesPage.clickOnModifyButton();
		modifyP2PTransferRulesConfirmPage.clickOnSubmitButton();
		result[0] = addP2PTransferRulePage1.getActualMsg();
		result[1] = service;
		result[2] = subService;
		result[3] = cardGroup;
		return result;
	}
	
	public String[] modifyP2PTransferRulesVoucher(String requestBearer,String service, String subService,
			String updatedStatus, String updatedCardGroup, String datetime) {

		result = new String[4];
		String senderType = null;
		String receiverType = null;
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClass");
		String senderServiceClass = _masterVO.getProperty("SenderServiceClass");
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickP2PTransferRuleModification();

		if (service.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			senderType = receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		} else {
			senderType = receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		}
		selectP2PTransferRulesPage.clickoncheckboxVoucher(requestBearer,senderType, senderServiceClass, receiverType, receiverServiceClass,
				service, subService);
		selectP2PTransferRulesPage.clickOnModifyButton();
		modifyP2PTransferRulesPage.changeStatus(updatedStatus);
		modifyP2PTransferRulesPage.changeCardGroup(updatedCardGroup);
		modifyP2PTransferRulesPage.clickOnModifyButton();
		modifyP2PTransferRulesConfirmPage.clickOnSubmitButton();
		String time2 = datetime+":00";
		long requiredtime = 0;
		try {
			requiredtime = homePage.getTimeDifferenceInSeconds(time2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result[0] = addP2PTransferRulePage1.getActualMsg();
		result[1] = service;
		result[2] = subService;
		updateCache.updateCache();
		try {
			Thread.sleep(requiredtime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String[] activateP2PTransferRules(String requestBearer,String service, String subService, String cardGroup,
			String updatedStatus, String updatedCardGroup) {

		result = new String[4];
		String senderType = null;
		String receiverType = null;
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClass");
		String senderServiceClass = _masterVO.getProperty("SenderServiceClass");
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickP2PTransferRuleModification();

		if (service.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			senderType = receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		} else {
			senderType = receiverType = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		}
		selectP2PTransferRulesPage.clickoncheckbox(requestBearer,senderType, senderServiceClass, receiverType, receiverServiceClass,
				service, subService, cardGroup);
		selectP2PTransferRulesPage.clickOnModifyButton();
		modifyP2PTransferRulesPage.changeStatus(updatedStatus);
		modifyP2PTransferRulesPage.changeCardGroup(updatedCardGroup);
		modifyP2PTransferRulesPage.clickOnModifyButton();
		modifyP2PTransferRulesConfirmPage.clickOnSubmitButton();
		result[0] = addP2PTransferRulePage1.getActualMsg();
		result[1] = service;
		result[2] = subService;
		result[3] = cardGroup;
		return result;
	}

	public boolean viewP2PTransferRule(String service, String subService, String cardGroupSet, String requestBearer) {
		String senderTypeData = null;
		String receiverTypeData = null;
		String receiverServiceClassData = _masterVO.getProperty("ReceiverServiceClass");
		String senderServiceClassData = _masterVO.getProperty("SenderServiceClass");
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_P2P_TRANSFER_RULE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickViewP2PTransferRule();
		String lastWord = viewP2PTransferRule.getTotalEnteries();
		int enteries = Integer.parseInt(lastWord);
		int enteriesPerPage = Integer.parseInt(_masterVO.getProperty("enteriesPerPage"));
		int totalPages = (int) Math.ceil(enteries/enteriesPerPage);
		boolean isNextExists=viewP2PTransferRule.isNextDisplayed();
		
		while(isNextExists == true)
		{
			viewP2PTransferRule.getLastPage(enteriesPerPage);
			isNextExists = viewP2PTransferRule.isNextDisplayed();
		}
		
		if (service.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			senderTypeData = receiverTypeData = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		} else {
			senderTypeData = receiverTypeData = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		}
		boolean elementStatus = viewP2PTransferRule.checkTransferRule(senderTypeData, senderServiceClassData,
				receiverTypeData, receiverServiceClassData, service, subService, cardGroupSet, requestBearer);
		return elementStatus;
	}

	public void writeCardGroup(String[] result) {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "P2P Service Sheet");
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
			String serviceName = ExcelUtility.getCellData(0, ExcelI.NAME, i);
			String subServiceName = ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i);
			if (serviceName.equals(result[0]) && subServiceName.equals(result[1]))
				ExcelUtility.setCellData(0, ExcelI.CARDGROUP_NAME, i, result[2]);
		}
	}

	public String[] serviceValue(String services) {
		String csvSplit = ",";
		String serviceArray[] = services.split(csvSplit);
		int size = serviceArray.length;
		String result[] = new String[size];
		for (int j = 0; j < size; j++)
			result[j] = _masterVO.p2pServicesMap.get(serviceArray[j].trim());

		return result;
	}

	public String gradeData(String category) {

		String result;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		Map<String, String> gradeMap = new HashMap<String, String>();
		for (int i = 0; i < totalRow; i++)
			gradeMap.put(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i), ExcelUtility.getCellData(0, ExcelI.GRADE, i));

		result = gradeMap.get(category);

		return result;
	}

}
