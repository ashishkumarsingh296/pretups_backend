package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.c2stransferrule.AddC2STransferRulePage1;
import com.pageobjects.networkadminpages.c2stransferrule.AddC2STransferRulePage2;
import com.pageobjects.networkadminpages.c2stransferrule.ModifyC2STransferRulePage1;
import com.pageobjects.networkadminpages.c2stransferrule.ModifyC2STransferRulePage2;
import com.pageobjects.networkadminpages.c2stransferrule.ModifyC2STransferRulePage3;
import com.pageobjects.networkadminpages.c2stransferrule.SelectC2STransferRulePage;
import com.pageobjects.networkadminpages.c2stransferrule.ViewC2STransferRulePage;
import com.pageobjects.networkadminpages.homepage.MastersSubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.TransferRulesSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.CacheUpdate;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;

public class C2STransferRules {

	WebDriver driver = null;
	String MasterSheetPath = null;
	NetworkAdminHomePage homePage;
	TransferRulesSubCategories transferRuleSubLinks;
	Login login;
	AddC2STransferRulePage1 addC2STransferRulePage1;
	AddC2STransferRulePage2 addC2STransferRulePage2;
	MastersSubCategories mastersSubCategories;
	CacheUpdate updateCache;
	ModifyC2STransferRulePage1 modifyC2STransferRulePage1;
	ModifyC2STransferRulePage2 modifyC2STransferRulePage2;
	ModifyC2STransferRulePage3 modifyC2STransferRulePage3;
	ViewC2STransferRulePage viewC2STransferRulePage;
	Map<String, String> cardGroupDataMap;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	Object[][] c2SDataObject;
	SelectNetworkPage selectNetworkPage;
	SelectC2STransferRulePage selectC2STransferRulePage;

	public C2STransferRules(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		transferRuleSubLinks = new TransferRulesSubCategories(driver);
		login = new Login();
		addC2STransferRulePage1 = new AddC2STransferRulePage1(driver);
		addC2STransferRulePage2 = new AddC2STransferRulePage2(driver);
		mastersSubCategories = new MastersSubCategories(driver);
		updateCache = new CacheUpdate(driver);
		modifyC2STransferRulePage1 = new ModifyC2STransferRulePage1(driver);
		modifyC2STransferRulePage2 = new ModifyC2STransferRulePage2(driver);
		modifyC2STransferRulePage3 = new ModifyC2STransferRulePage3(driver);
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		selectNetworkPage = new SelectNetworkPage(driver);
		viewC2STransferRulePage = new ViewC2STransferRulePage(driver);
		selectC2STransferRulePage = new SelectC2STransferRulePage(driver);
	}

	public Object[][] addC2STransferRule(String fromDomain, String fromCategory, String services, String requestBearer, int rownum, boolean preRequisite) {
		final String methodname = "addC2STransferRule";
		Log.methodEntry(methodname, fromDomain, fromCategory, services, requestBearer, rownum, preRequisite);
		
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClassC2S");
		cardGroupDataMap = new HashMap<String, String>();

		// Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_C2S_TRANSFER_RULE_ROLECODE); // Getting User with Access to Add C2S Transfer Rule
		String ErrorMessageString = null;
		String status = null;
		String csvSplit = ",";
		String[] result = addC2STransferRulePage1.serviceValue(services);
		Object[][] serviceSheetDataObject = addC2STransferRulePage1.serviceSheetData(result);
		int size = serviceSheetDataObject.length;
		String[] requestBearerArray = requestBearer.split(csvSplit);
		c2SDataObject = new Object[size][5];
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
        String subService;
		for (int i = 0; i < size; i++) {
			try {
				transferRuleSubLinks.clickAddC2STransferRule();
				if (!_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("2"))
					addC2STransferRulePage1.reqGatewayCode(requestBearerArray[0]);
				status = null;
				addC2STransferRulePage1.domain(fromDomain);
				if (_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("1")) {
					addC2STransferRulePage1.category(fromCategory);
					String gradeValue = addC2STransferRulePage1.gradeData(fromCategory);
					addC2STransferRulePage1.Grade(gradeValue);
				}

				if (serviceSheetDataObject[i][0].equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
					addC2STransferRulePage1.recieverType(PretupsI.POSTPAID_SUB_LOOKUPS);
				} else
					addC2STransferRulePage1.recieverType(PretupsI.PREPAID_SUB_LOOKUPS);

				addC2STransferRulePage1.recieverServiceClass(receiverServiceClass);
				addC2STransferRulePage1.serviceType((String) serviceSheetDataObject[i][0]);
				addC2STransferRulePage1.subService((String) serviceSheetDataObject[i][1]);
				addC2STransferRulePage1.cardGroupSet((String) serviceSheetDataObject[i][2]);
				addC2STransferRulePage1.add();
				addC2STransferRulePage2.confirm();
				
				subService = (String) serviceSheetDataObject[i][1];
				c2SDataObject[i][0] = addC2STransferRulePage1.getActualMsg();
				String msg = addC2STransferRulePage1.getActualMsg();
				if(preRequisite == true && msg.equals(MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist",
						"1")))
				{
					activateC2STransferRules(fromDomain,fromCategory,requestBearer,(String) serviceSheetDataObject[i][0],(String) serviceSheetDataObject[i][1]);
				}
				c2SDataObject[i][1] = fromCategory;
				c2SDataObject[i][2] = (String) serviceSheetDataObject[i][2];
				c2SDataObject[i][3] = (String) serviceSheetDataObject[i][0];
				c2SDataObject[i][4] = (String) serviceSheetDataObject[i][1];
				ErrorMessageString = addC2STransferRulePage1.checkForError();
				// cardGroupDataMap.put(fromCategory, (String)
				// serviceSheetDataObject[i][2]);
			} catch (Exception e) {
				ExtentI.Markup(ExtentColor.RED, "Unable to locate element" + e);
				Log.failNode("Unable to locate element:" + e);
				c2SDataObject[i][0] = "Data Issue";
				c2SDataObject[i][1] = fromCategory;
				c2SDataObject[i][2] = (String) serviceSheetDataObject[i][2];
				c2SDataObject[i][3] = (String) serviceSheetDataObject[i][0];
				c2SDataObject[i][4] = (String) serviceSheetDataObject[i][1];
				continue;
			}

			if (ErrorMessageString != null) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				status = serviceSheetDataObject[i][0] + ":Already Exists\n";
				ExcelUtility.setCellData(0, ExcelI.STATUS, rownum, status);
			}

		}
		
		Log.methodExit(methodname);
		return c2SDataObject;
	}

	public void writeC2SData(Object[][] c2STransferDataObject) {
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int j = 0; j < c2STransferDataObject.length; j++) {
			for (int i = 1; i <= rowCount; i++) {
				String catName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				if (catName.equals((String) c2STransferDataObject[j][1]))
					ExcelUtility.setCellData(0, ExcelI.CARDGROUP_NAME, i, (String) c2STransferDataObject[j][2]);
			}
		}
	}

	public String modifyC2STransferRules(String fromDomain, String fromCategory, String services,
			String requestBearer, String subService) {

		String csvSplit = ",";
		String orderByValue = "Request Gateway";
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClassC2S");
		//String subService = _masterVO.getProperty("SubService");
		
		String status = PretupsI.STATUS_SUSPENDED_LOOKUPS;
		String type = null;
		/*String[] serviceArray = modifyC2STransferRulePage1.serviceValue(services);
		String service = serviceArray[0];*/
		if (services.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {

			type = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
			Log.info("type is " +type);;
		} else
			type = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		Log.info("type is " +type);

		String[] requestBearerArray = requestBearer.split(csvSplit);
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MOD_C2S_TRF_RULES);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		if(_masterVO.getClientDetail("MODIFYC2STRANSFERRULE_VER").equalsIgnoreCase("1")){
			transferRuleSubLinks.clickC2CTransferRuleModificationOrderBy();
			selectC2STransferRulePage.selectOrderBy(orderByValue);
			selectC2STransferRulePage.clickOnSubmitButton();
		}
		else{
			transferRuleSubLinks.clickC2CTransferRuleModification();
		}
		String cardGroup = modifyC2STransferRulePage1.cardGroupData(services, subService);
		String grade = modifyC2STransferRulePage1.gradeData(fromCategory);
		modifyC2STransferRulePage1.clickoncheckbox(requestBearerArray[0], fromDomain, fromCategory, grade, type,
				receiverServiceClass, services, subService, cardGroup);
		modifyC2STransferRulePage1.clickOnbtnMod();
		modifyC2STransferRulePage2.changedStatus(status);
		modifyC2STransferRulePage2.changedCardGroup(cardGroup);
		modifyC2STransferRulePage2.clickOnbtnMod();
		modifyC2STransferRulePage3.clickOnbtnSubmit();
		String uiMsg = modifyC2STransferRulePage1.getActualMsg();
		return uiMsg;
	}

	public void activateC2STransferRules(String fromDomain, String fromCategory,
			String requestBearer, String service, String subServiceNew) {
		Log.methodEntry("activateC2STransferRules", fromDomain, fromCategory,requestBearer, service, subServiceNew);
		String csvSplit = ",";
		String orderByValue = "Request Gateway";
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClassC2S");
		//String subService = _masterVO.getProperty("SubService");
		String status = PretupsI.STATUS_ACTIVE_LOOKUPS;
		String type = null;
		/*String[] serviceArray = modifyC2STransferRulePage1.serviceValue(services);
		String service = serviceArray[0];*/
		if (service.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {

			type = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
			Log.info("type is " +type);;
		} else
			type = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		Log.info("type is " +type);

		String[] requestBearerArray = requestBearer.split(csvSplit);
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MOD_C2S_TRF_RULES);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		if(_masterVO.getClientDetail("MODIFYC2STRANSFERRULE_VER").equalsIgnoreCase("1")){
			transferRuleSubLinks.clickC2CTransferRuleModificationOrderBy();
			selectC2STransferRulePage.selectOrderBy(orderByValue);
			selectC2STransferRulePage.clickOnSubmitButton();
		}
		else{
			transferRuleSubLinks.clickC2CTransferRuleModification();
		}
		String cardGroup = modifyC2STransferRulePage1.cardGroupData(service, subServiceNew);
		String grade = modifyC2STransferRulePage1.gradeData(fromCategory);
		modifyC2STransferRulePage1.clickoncheckbox(requestBearerArray[0], fromDomain, fromCategory, grade, type,
				receiverServiceClass, service, subServiceNew, cardGroup);
		modifyC2STransferRulePage1.clickOnbtnMod();
		modifyC2STransferRulePage2.changedStatus(status);
		modifyC2STransferRulePage2.changedCardGroup(cardGroup);
		modifyC2STransferRulePage2.clickOnbtnMod();
		modifyC2STransferRulePage3.clickOnbtnSubmit();
		}
	
	public boolean viewC2STransferRules(String fromDomain, String fromCategory, String service, String requestBearer, String subService) {

		String csvSplit = ",";
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClassC2S");
		//String subService = _masterVO.getProperty("SubService");
		String type = null;
		if (service.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			type = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
		} else
			type = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);;
		String[] requestBearerArray = requestBearer.split(csvSplit);
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_C2S_TRF_RULES);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickViewC2STransferRule();
		String lastWord = viewC2STransferRulePage.getTotalEnteries();
		int enteries = Integer.parseInt(lastWord);
		int enteriesPerPage = Integer.parseInt(_masterVO.getProperty("enteriesPerPage"));
		int totalPages = (int) Math.ceil(enteries/enteriesPerPage);
		boolean isNextExists=viewC2STransferRulePage.isNextDisplayed();
		
		while(isNextExists == true)
		{
			viewC2STransferRulePage.getLastPage(enteriesPerPage);
			isNextExists = viewC2STransferRulePage.isNextDisplayed();
		}
		
		String cardGroup = modifyC2STransferRulePage1.cardGroupData(service, subService);
		String grade = modifyC2STransferRulePage1.gradeData(fromCategory);
		boolean elementStatus = viewC2STransferRulePage.checkTransferRule(requestBearerArray[0], fromDomain,
				fromCategory, grade, type, receiverServiceClass, service, subService, cardGroup);
		return elementStatus;
	}

	// SIT Flow Starts

	public void addC2STransferRule_SIT(Map<String, String> dataMap) {

		String receiverServiceClass = "ALL(ALL)";
		cardGroupDataMap = new HashMap<String, String>();

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_C2S_TRANSFER_RULE_ROLECODE);
		String csvSplit = ",";
		String[] result = addC2STransferRulePage1.serviceValue(dataMap.get("services"));
		Object[][] serviceSheetDataObject = addC2STransferRulePage1.serviceSheetData(result);
		int size = serviceSheetDataObject.length;
		String[] requestBearerArray = dataMap.get("requestBearer").split(csvSplit);
		c2SDataObject = new Object[size][5];
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddC2STransferRule();

		for (int i = 0; i < size; i++) {

			if (!_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("2"))
				addC2STransferRulePage1.reqGatewayCode(requestBearerArray[0]);
			if (!dataMap.get("fromDomain").equals(""))
				addC2STransferRulePage1.domain(dataMap.get("fromDomain"));
			if (_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("1")){
				if (!dataMap.get("fromCategory").equals(""))
					addC2STransferRulePage1.category(dataMap.get("fromCategory"));
				String gradeValue;
				if(!dataMap.get("fromCategory").equals("ALL") && !dataMap.get("fromCategory").equals(""))
				{
					gradeValue = addC2STransferRulePage1.gradeData(dataMap.get("fromCategory"));
				}
				else{
					gradeValue =  dataMap.get("fromGrade");
				}
				if(!dataMap.get("fromGrade").equals(""))
				{
					addC2STransferRulePage1.Grade(gradeValue);
				}
			}
			if (serviceSheetDataObject[i][0].equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
				addC2STransferRulePage1.recieverType(PretupsI.POSTPAID_SUB_LOOKUPS);
			} else
				addC2STransferRulePage1.recieverType(PretupsI.PREPAID_SUB_LOOKUPS);

			addC2STransferRulePage1.recieverServiceClass(receiverServiceClass);
			addC2STransferRulePage1.serviceType((String) serviceSheetDataObject[i][0]);
			addC2STransferRulePage1.subService((String) serviceSheetDataObject[i][1]);
			//addC2STransferRulePage1.cardGroupSet((String) serviceSheetDataObject[i][2]);
			addC2STransferRulePage1.add();
			if (!dataMap.get("fromDomain").equals("") && !dataMap.get("fromCategory").equals("") && !dataMap.get("fromGrade").equals("")){
				addC2STransferRulePage2.confirm();
			}
			else{
				dataMap.put("message", addC2STransferRulePage1.checkForError());
				break;
			}
		}

	}

	public String getErrorMessage() {	
		return addC2STransferRulePage1.checkForError();
	}

	public String addNull(Map<String, String> dataMap) {
		cardGroupDataMap = new HashMap<String, String>();

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_C2S_TRANSFER_RULE_ROLECODE);
		String[] result = addC2STransferRulePage1.serviceValue(dataMap.get("services"));
		Object[][] serviceSheetDataObject = addC2STransferRulePage1.serviceSheetData(result);
		int size = serviceSheetDataObject.length;
		c2SDataObject = new Object[size][5];
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddC2STransferRule();
		addC2STransferRulePage1.add();
		String uiMsg = addC2STransferRulePage1.getActualMsg();
		return uiMsg;
	}

	public String receiverTypeNotSelected(Map<String, String> dataMap) {
		cardGroupDataMap = new HashMap<String, String>();
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_C2S_TRANSFER_RULE_ROLECODE);
		String[] result = addC2STransferRulePage1.serviceValue(dataMap.get("services"));
		Object[][] serviceSheetDataObject = addC2STransferRulePage1.serviceSheetData(result);
		int size = serviceSheetDataObject.length;
		c2SDataObject = new Object[size][5];
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddC2STransferRule();
		addC2STransferRulePage1.domain(dataMap.get("fromDomain"));
		if (_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("1"))
		{	addC2STransferRulePage1.category(dataMap.get("fromCategory"));
			String gradeValue = addC2STransferRulePage1.gradeData(dataMap.get("fromCategory"));
			addC2STransferRulePage1.Grade(gradeValue);
		}
		addC2STransferRulePage1.add();
		String uiMsg = addC2STransferRulePage1.getActualMsg();
		return uiMsg;
	}

	public String serviceTypeNotSelected(Map<String, String> dataMap) {
		String receiverServiceClass = "ALL(ALL)";
		cardGroupDataMap = new HashMap<String, String>();
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_C2S_TRANSFER_RULE_ROLECODE);
		String[] result = addC2STransferRulePage1.serviceValue(dataMap.get("services"));
		Object[][] serviceSheetDataObject = addC2STransferRulePage1.serviceSheetData(result);
		int size = serviceSheetDataObject.length;
		c2SDataObject = new Object[size][5];
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		transferRuleSubLinks.clickAddC2STransferRule();
		addC2STransferRulePage1.domain(dataMap.get("fromDomain"));
		if (_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("1")){
			addC2STransferRulePage1.category(dataMap.get("fromCategory"));
			String gradeValue = addC2STransferRulePage1.gradeData(dataMap.get("fromCategory"));
			addC2STransferRulePage1.Grade(gradeValue);
		}
		if (serviceSheetDataObject[0][0].equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addC2STransferRulePage1.recieverType(PretupsI.POSTPAID_SUB_LOOKUPS);
		} else
			addC2STransferRulePage1.recieverType(PretupsI.PREPAID_SUB_LOOKUPS);

		addC2STransferRulePage1.recieverServiceClass(receiverServiceClass);
		addC2STransferRulePage1.add();
		String uiMsg = addC2STransferRulePage1.getActualMsg();
		return uiMsg;
	}

	public String modifyC2STransferRules_null(String fromDomain, String fromCategory, String services,
			String requestBearer) {
		String orderByValue = "Request Gateway";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MOD_C2S_TRF_RULES);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		if(_masterVO.getClientDetail("MODIFYC2STRANSFERRULE_VER").equalsIgnoreCase("1")){
			transferRuleSubLinks.clickC2CTransferRuleModificationOrderBy();
			selectC2STransferRulePage.selectOrderBy(orderByValue);
			selectC2STransferRulePage.clickOnSubmitButton();
		}
		else{
			transferRuleSubLinks.clickC2CTransferRuleModification();
		}
		modifyC2STransferRulePage1.clickOnbtnMod();
		String uiMsg = modifyC2STransferRulePage1.getActualMsg();
		return uiMsg;
	}

	public String modifyC2STransferRules_SIT(String fromDomain, String fromCategory, String services,
			String requestBearer, String status, String buttonType, String subService) {

		String csvSplit = ",";
		String orderByValue = "Request Gateway";
		String receiverServiceClass = _masterVO.getProperty("ReceiverServiceClassC2S");
		//String subService = _masterVO.getProperty("SubService");
		/*String[] serviceArray = modifyC2STransferRulePage1.serviceValue(services);
		String service = serviceArray[0];*/
		String type;
		if (services.equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			type = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
			Log.info("type is " +type);;
		} else {
			type = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_SUB_LOOKUPS, PretupsI.SUBTP_LOOKUP);
			Log.info("type is " +type);
		}
		String[] requestBearerArray = requestBearer.split(csvSplit);
		String uiMsg = null;
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MOD_C2S_TRF_RULES);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		selectNetworkPage.selectNetwork();
		homePage.clickTransferRules();
		if(_masterVO.getClientDetail("MODIFYC2STRANSFERRULE_VER").equalsIgnoreCase("1")){
			transferRuleSubLinks.clickC2CTransferRuleModificationOrderBy();
			selectC2STransferRulePage.selectOrderBy(orderByValue);
			selectC2STransferRulePage.clickOnSubmitButton();
		}
		else{
			transferRuleSubLinks.clickC2CTransferRuleModification();
		}
		String cardGroup = modifyC2STransferRulePage1.cardGroupData(services, subService);
		String grade = modifyC2STransferRulePage1.gradeData(fromCategory);
		modifyC2STransferRulePage1.clickoncheckbox(requestBearerArray[0], fromDomain, fromCategory, grade, type,
				receiverServiceClass, services, subService, cardGroup);
		modifyC2STransferRulePage1.clickOnbtnMod();
		if(!status.equals("")){
			if(buttonType.equals("Modify")){
				modifyC2STransferRulePage2.changedStatus(status);
				modifyC2STransferRulePage2.clickOnbtnMod();
				boolean result = modifyC2STransferRulePage3.viewTransferRule(requestBearerArray, fromDomain, fromCategory, grade, services, requestBearer, type, receiverServiceClass, status);
				if(result){
					modifyC2STransferRulePage3.clickOnbtnSubmit();
					uiMsg = modifyC2STransferRulePage1.getActualMsg();
				}
				else{
					uiMsg = "Status not verified";
				}
			}
			else if(buttonType.equals("reset")){

				String afterstatus = PretupsI.STATUS_ACTIVE_LOOKUPS;
				modifyC2STransferRulePage2.changedStatus(afterstatus);
				modifyC2STransferRulePage2.clickOnbtnReset();
				modifyC2STransferRulePage2.clickOnbtnMod();
				if(modifyC2STransferRulePage3.viewTransferRule(requestBearerArray, fromDomain, fromCategory, grade, services, requestBearer, type, receiverServiceClass, status))
					uiMsg = "true";
				else
					uiMsg = "false";

			}
			else if(buttonType.equals("back")){
				modifyC2STransferRulePage2.clickOnbtnBack();
				if(modifyC2STransferRulePage1.clickOnselectAll())
					uiMsg = "true";
				else
					uiMsg = "false";

			}

			else if(buttonType.equals("delete")){
				modifyC2STransferRulePage2.clickOnbtnDelete();
				modifyC2STransferRulePage2.clickOK();
				uiMsg = modifyC2STransferRulePage1.getActualMsg();
			}
		}
		else{
			modifyC2STransferRulePage2.changedStatus(status);
			modifyC2STransferRulePage2.clickOnbtnMod();
			uiMsg =	modifyC2STransferRulePage2.getActualMsg();
		}
		return uiMsg;
	}

}
