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
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.PromotionalTransferRuleSubCategories;
import com.pageobjects.networkadminpages.promotionaltransferrule.AddMultipleTimeSlabs;
import com.pageobjects.networkadminpages.promotionaltransferrule.AddPromotionalTransferRule;
import com.pageobjects.networkadminpages.promotionaltransferrule.AddPromotionalTransferRulePage2;
import com.pageobjects.networkadminpages.promotionaltransferrule.AddPromotionalTransferRulePage3;
import com.pageobjects.networkadminpages.promotionaltransferrule.ModifyMultipleTimeSlabs;
import com.pageobjects.networkadminpages.promotionaltransferrule.ModifyPromotionalTransferRule;
import com.pageobjects.networkadminpages.promotionaltransferrule.ModifyPromotionalTransferRulePage2;
import com.pageobjects.networkadminpages.promotionaltransferrule.ModifyPromotionalTransferRulePage3;
import com.pageobjects.networkadminpages.promotionaltransferrule.ModifyPromotionalTransferRulePage4;
import com.pageobjects.networkadminpages.promotionaltransferrule.ViewPromotionalTransferRule;
import com.pageobjects.networkadminpages.promotionaltransferrule.ViewPromotionalTransferRule1;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pretupsControllers.BTSLUtil;
import com.utils.ExtentI;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;

public class PromotionalTransferRule {

	public WebDriver driver;
	String MasterSheetPath;
	NetworkAdminHomePage homePage;
	Login login;
	RandomGeneration RandomGenerator;
	AddPromotionalTransferRule addPromotionalTransferRule;
	AddPromotionalTransferRulePage2 addPromotionalTransferRulePage2;
	AddPromotionalTransferRulePage3 addPromotionalTransferRulePage3;
	AddMultipleTimeSlabs addMultipleTimeSlabs;
	ModifyPromotionalTransferRule modifyPromotionalTransferRule;
	ModifyPromotionalTransferRulePage2 modifyPromotionalTransferRulePage2;
	ModifyPromotionalTransferRulePage3 modifyPromotionalTransferRulePage3;
	ModifyPromotionalTransferRulePage4 modifyPromotionalTransferRulePage4;
	ModifyMultipleTimeSlabs modifyMultipleTimeSlabs;
	ViewPromotionalTransferRule viewPromotionalTransferRule;
	ViewPromotionalTransferRule1 viewPromotionalTransferRule1;
	String[] result;
	SelectNetworkPage selectNetworkPage;
	Map<String, String> userAccessMap;
	NetworkAdminHomePage networkAdminHomePage;
	PromotionalTransferRuleSubCategories promotionalTransferRuleSubCategories;
	String User = PretupsI.USER_LOOKUP;
	String Grade = PretupsI.GRADE_LOOKUP;
	String Geography = PretupsI.GEOGRAPHY_LOOKUP;
	String Category = PretupsI.CATEGORY_LOOKUP;
	String PrePaid = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_LOOKUP, PretupsI.SERVICE_LOOUP);
	String PostPaid = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_LOOKUP, PretupsI.SERVICE_LOOUP);
	
	public PromotionalTransferRule(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		RandomGenerator = new RandomGeneration();
		addPromotionalTransferRule = new AddPromotionalTransferRule(driver);
		addPromotionalTransferRulePage2 = new AddPromotionalTransferRulePage2(driver);
		addPromotionalTransferRulePage3 = new AddPromotionalTransferRulePage3(driver);
		addMultipleTimeSlabs = new AddMultipleTimeSlabs(driver);
		modifyPromotionalTransferRule = new ModifyPromotionalTransferRule(driver);
		modifyPromotionalTransferRulePage2 = new ModifyPromotionalTransferRulePage2(driver);
		modifyPromotionalTransferRulePage3 = new ModifyPromotionalTransferRulePage3(driver);
		modifyPromotionalTransferRulePage4 = new ModifyPromotionalTransferRulePage4(driver);
		modifyMultipleTimeSlabs = new ModifyMultipleTimeSlabs(driver);
		viewPromotionalTransferRule = new ViewPromotionalTransferRule(driver);
		viewPromotionalTransferRule1 = new ViewPromotionalTransferRule1(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
		userAccessMap = new HashMap<String, String>();
		networkAdminHomePage = new NetworkAdminHomePage(driver);
		promotionalTransferRuleSubCategories = new PromotionalTransferRuleSubCategories(driver);
	}

	// Promotional Level is null
	public String a_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode("");
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage = addPromotionalTransferRule.getActualMsg();

		return uiMessage;
	}

	// Domain is null
	public String b_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		addPromotionalTransferRulePage2.selectdomainCodeforDomain("Select");
		addPromotionalTransferRulePage2.ClickOnAdd();
		String uiMessage = addPromotionalTransferRulePage2.getActualMsg();

		return uiMessage;
	}

	// Geographical domain is null
	public String c_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.ClickOnAdd();
			uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		}
		else if(promotionalLevel.equals(Geography)){
			addPromotionalTransferRulePage2.ClickOnAdd();
		    uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		}
	     else {
			uiMessage = null;
		}

		return uiMessage;
	}

	// Grade is null
	public String d_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage = null;
		if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.selectGradeCode("Select");
			addPromotionalTransferRulePage2.ClickOnAdd();
			uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		}

		return uiMessage;
	}

	// User is null
	public String e_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.enterUserName("");
			addPromotionalTransferRulePage2.ClickOnAdd();
			uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		} else {
			uiMessage = null;
		}

		return uiMessage;
	}

	// Receiver Type is null
	public String f_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// serviceType is null
	public String g_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		//addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType("Select");
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// fromDate is null
	public String j_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate("");
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// fromTime is null
	public String k_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();

		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		String fromDate = currDate;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if(promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime("");
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// tillDate is null
	public String l_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();

		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		String fromDate = currDate;

		String fromTime = networkAdminHomePage.getApplicableFromTime();
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime(fromTime);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate("");
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// tillTime is null
	public String m_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();

		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		String fromDate = currDate;
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 5);
		String fromTime = networkAdminHomePage.getApplicableFromTime();
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime(fromTime);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillTime("");
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// fromdateformat notValid
	public String n_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");

		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)){
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate("39-39-38");
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// fromTimeformat notValid
	public String o_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();

		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		String fromDate = currDate;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime("32-29");
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// tillDateformat notValid
	public String p_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();

		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		String fromDate = currDate;
		String fromTime = networkAdminHomePage.getApplicableFromTime();
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime(fromTime);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate("32-29-28");

		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// tillTimeformat notValid
	public String q_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();

		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		String fromDate = currDate;
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 5);
		String fromTime = networkAdminHomePage.getApplicableFromTime();
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)){
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime(fromTime);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillTime("32-29-5734");
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// from date time less than current date time
	public String r_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -2);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 5);
		String fromTime = networkAdminHomePage.getApplicableFromTime();
		String tillTime = networkAdminHomePage.getApplicableFromTime();
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime(fromTime);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillTime(tillTime);
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// till date time less than current date time
	public String s_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -2);
		String fromTime = networkAdminHomePage.getApplicableFromTime();
		String tillTime = networkAdminHomePage.getApplicableFromTime();
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime(fromTime);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillTime(tillTime);
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// Positive Test Case for Date Range
	public String t_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
		String fromTime = networkAdminHomePage.getApplicableFromTime();
		String tillTime = networkAdminHomePage.getApplicableFromTime();
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)){
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverServiceClassID("ALL(ALL)");
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subscriberStatus("ALL");
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceGroupCode("ALL");
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime(fromTime);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillTime(tillTime);
		addPromotionalTransferRulePage2.ClickOnAdd();
		addPromotionalTransferRulePage3.ClickOnConfirm();
		uiMessage = addPromotionalTransferRule.getActualMsg();
		return uiMessage;
	}

	// multipleTimeSlab is null
	public String u_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("TimeType"), dataMap.get("MultipleSlabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)){
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.ClickOnAdd();
		uiMessage = addPromotionalTransferRulePage2.getActualMsg();
		return uiMessage;
	}

	// Empty Slabs
	public String v_addPromotionalTransferRule(Map<String, String> dataMap) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("TimeType"), dataMap.get("MultipleSlabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.TimeSlab0();
		SwitchWindow.switchwindow(driver);
		addMultipleTimeSlabs.ClickOnAdd();
		uiMessage = addPromotionalTransferRule.getActualMsg();
		return uiMessage;
	}

	// Same Start and End Time Slab
	public String w_addPromotionalTransferRule(Map<String, String> dataMap) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
		String fromTime = networkAdminHomePage.getApplicableFromTime();
		String tillTime = networkAdminHomePage.getApplicableFromTime();
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("TimeType"), dataMap.get("MultipleSlabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.TimeSlab0();
		SwitchWindow.switchwindow(driver);
		addMultipleTimeSlabs.enterStartTime0(fromTime);
		addMultipleTimeSlabs.enterEndTime0(tillTime);
		addMultipleTimeSlabs.ClickOnAdd();
		uiMessage = addPromotionalTransferRule.getActualMsg();
		return uiMessage;
	}

	// Same Start and End Time Slab for two rows
	public String x_addPromotionalTransferRule(Map<String, String> dataMap) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
		String fromTime = networkAdminHomePage.getApplicableFromTime();
		String tillTime = networkAdminHomePage.getApplicableFromTime_1min();
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("TimeType"), dataMap.get("MultipleSlabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)){
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)){
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.TimeSlab0();
		SwitchWindow.switchwindow(driver);
		addMultipleTimeSlabs.enterStartTime0(fromTime);
		addMultipleTimeSlabs.enterEndTime0(tillTime);
		addMultipleTimeSlabs.enterStartTime1(fromTime);
		addMultipleTimeSlabs.enterEndTime1(tillTime);
		addMultipleTimeSlabs.ClickOnAdd();
		uiMessage = addPromotionalTransferRule.getActualMsg();
		return uiMessage;
	}

	// Start Time is less than End Time for previous slab
	public String y_addPromotionalTransferRule(Map<String, String> dataMap) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
		String fromTime1 = networkAdminHomePage.getApplicableFromTime_min(2);
		String tillTime1 = networkAdminHomePage.getApplicableFromTime_min(6);
		String fromTime2 = networkAdminHomePage.getApplicableFromTime_min(4);
		String tillTime2 = networkAdminHomePage.getApplicableFromTime_min(9);
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("TimeType"), dataMap.get("MultipleSlabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.TimeSlab0();
		SwitchWindow.switchwindow(driver);
		addMultipleTimeSlabs.enterStartTime0(fromTime1);
		addMultipleTimeSlabs.enterEndTime0(tillTime1);
		addMultipleTimeSlabs.enterStartTime1(fromTime2);
		addMultipleTimeSlabs.enterEndTime1(tillTime2);
		addMultipleTimeSlabs.ClickOnAdd();
		uiMessage = addPromotionalTransferRule.getActualMsg();
		return uiMessage;
	}

	// Positive Test Case for Single Time Range
	public String z_addPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
		String fromTime = networkAdminHomePage.getApplicableFromTime();
		String tillTime = networkAdminHomePage.getApplicableFromTime();
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("TimeType"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromTime(fromTime);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillTime(tillTime);
		addPromotionalTransferRulePage2.ClickOnAdd();
		addPromotionalTransferRulePage3.ClickOnConfirm();
		uiMessage = addPromotionalTransferRule.getActualMsg();
		return uiMessage;
	}

	// Positive Test Case for Multiple Time Range
	public String za_addPromotionalTransferRule(Map<String, String> dataMap) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
		String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
		String fromTime = networkAdminHomePage.getApplicableFromTime_min(2);
		String tillTime = networkAdminHomePage.getApplicableFromTime_min(5);
		promotionalTransferRuleSubCategories.clickAddPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("TimeType"), dataMap.get("MultipleSlabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if (promotionalLevel.equals(User)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));
			addPromotionalTransferRulePage2.enterUserName(dataMap.get("userName"));
			// addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType();
		} else if (promotionalLevel.equals(Grade)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));
			addPromotionalTransferRulePage2.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			addPromotionalTransferRulePage2.selectGeoTypeCode(dataMap.get("geoDomainType"));
			addPromotionalTransferRulePage2.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			addPromotionalTransferRulePage2.selectdomainCodeforDomain(dataMap.get("domain"));
			addPromotionalTransferRulePage2.SelectcategoryCode(dataMap.get("category"));

		}
		if (!BTSLUtil.isNullString(dataMap.get("serviceName")) && dataMap.get("serviceName").equals(ExtentI.getValueofCorrespondingColumns(ExcelI.C2S_SERVICES_SHEET, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"PPB"}))) {
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PostPaid);
		} else
			addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0receiverSubscriberType(PrePaid);
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0serviceType(dataMap.get("serviceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0subServiceTypeId(dataMap.get("subServiceName"));
		addPromotionalTransferRulePage2.Selectc2STransferRulesIndexed0cardGroupSetID(dataMap.get("cardGroup"));
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0fromDate(fromDate);
		addPromotionalTransferRulePage2.enterc2STransferRulesIndexed0tillDate(tillDate);
		addPromotionalTransferRulePage2.TimeSlab0();
		SwitchWindow.switchwindow(driver);
		addMultipleTimeSlabs.enterStartTime0(fromTime);
		addMultipleTimeSlabs.enterEndTime0(tillTime);
		addMultipleTimeSlabs.ClickOnAdd();
		SwitchWindow.backwindow(driver);
		// addPromotionalTransferRulePage2.enterIndexed0multipleSlab();
		addPromotionalTransferRulePage2.ClickOnAdd();
		addPromotionalTransferRulePage3.ClickOnConfirm();
		uiMessage = addPromotionalTransferRule.getActualMsg();
		return uiMessage;
	}

	// View Promotional Transfer Rule

	// Promotional Level is null
	public String zb_viewPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		promotionalTransferRuleSubCategories.clickViewPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode("");
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage = addPromotionalTransferRule.getActualMsg();

		return uiMessage;
	}

	// Domain is null
	public String zc_viewPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickViewPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if(!promotionalLevel.equals(Geography))
	       {
			viewPromotionalTransferRule1.selectdomainCodeforDomain("Select");
			viewPromotionalTransferRule1.ClickOnSubmit();
			uiMessage = modifyPromotionalTransferRule.getActualMsg();
	       }
			else{
			uiMessage = "Domain Field for Promotional level Geography does not exist";
			}
		
		return uiMessage;
	}

	// Geographical Domain is null
	public String zd_viewPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String uiMessage = null;
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickViewPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		if(!promotionalLevel.equals(Geography)){
		viewPromotionalTransferRule1.selectdomainCodeforDomain(dataMap.get("domain"));
		}
		viewPromotionalTransferRule1.ClickOnSubmit();
		uiMessage = viewPromotionalTransferRule1.getActualMsg();

		return uiMessage;
	}

	// Grade or User is null
	public String ze_viewPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickViewPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if(promotionalLevel.equals(User) || promotionalLevel.equals(Grade)){
		viewPromotionalTransferRule1.selectdomainCodeforDomain(dataMap.get("domain"));
		viewPromotionalTransferRule1.selectGeoTypeCode(dataMap.get("geoDomainType"));
		viewPromotionalTransferRule1.ClickOnSubmit();
		uiMessage = viewPromotionalTransferRule1.getActualMsg();
		}
		else{
		uiMessage = "Test Case only for Promotional Level User and Grade.";
		}
	
       return uiMessage;
	}

	public boolean zf_viewPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickViewPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();

		if (promotionalLevel.equals(User)) {
			viewPromotionalTransferRule1.selectdomainCodeforDomain(dataMap.get("domain"));
			viewPromotionalTransferRule1.SelectcategoryCode(dataMap.get("category"));
			viewPromotionalTransferRule1.selectGeoTypeCode(dataMap.get("geoDomainType"));
			viewPromotionalTransferRule1.selectGeoDomainCode(dataMap.get("geoDomainName"));
			viewPromotionalTransferRule1.enterUserName(dataMap.get("userName"));

		} else if (promotionalLevel.equals(Grade)) {
			viewPromotionalTransferRule1.selectdomainCodeforDomain(dataMap.get("domain"));
			viewPromotionalTransferRule1.SelectcategoryCode(dataMap.get("category"));
			viewPromotionalTransferRule1.selectGradeCode(dataMap.get("grade"));

		} else if (promotionalLevel.equals(Geography)) {
			viewPromotionalTransferRule1.selectGeoTypeCode(dataMap.get("geoDomainType"));
			viewPromotionalTransferRule1.selectGeoDomainCode(dataMap.get("geoDomainName"));

		} else if (promotionalLevel.equals(Category)) {
			viewPromotionalTransferRule1.selectdomainCodeforDomain(dataMap.get("domain"));
			viewPromotionalTransferRule1.SelectcategoryCode(dataMap.get("category"));

		}

		viewPromotionalTransferRule1.ClickOnSubmit();
		boolean resultValue = viewPromotionalTransferRule.viewTransferRule(dataMap);

		return resultValue;

	}

	// Modify Promotional Transfer Rule
	
	//Promotional Level is null
	public String zg_modifyPromotionalTransferRule(Map<String, String> dataMap) {

	userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_PROMOTIONAL_TRANSFER_RULE);
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	// User Access module ends.
	selectNetworkPage.selectNetwork();
	networkAdminHomePage.clickPromotionalTransferRule();
	promotionalTransferRuleSubCategories.clickModifyPromotionalTransferRule();
	addPromotionalTransferRule.selectPromotionCode("");
	addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
	addPromotionalTransferRule.clickOnSubmit();


	String uiMessage = addPromotionalTransferRule.getActualMsg();
	return uiMessage;

}
	
	//Domain is null
		public String zh_modifyPromotionalTransferRule(Map<String, String> dataMap) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_PROMOTIONAL_TRANSFER_RULE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickPromotionalTransferRule();
		String promotionalLevel = dataMap.get("promotionalLevel");
		promotionalTransferRuleSubCategories.clickModifyPromotionalTransferRule();
		addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
		addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
		addPromotionalTransferRule.clickOnSubmit();
		String uiMessage;
		if(!promotionalLevel.equals(Geography))
       {
			modifyPromotionalTransferRule.selectdomainCodeforDomain("Select");
			modifyPromotionalTransferRule.ClickOnSubmit();
			uiMessage = modifyPromotionalTransferRule.getActualMsg();
       }
		else{
		uiMessage = "Domain Field for Promotional level Geography does not exist";
		}
		
		return uiMessage;

	}
		
		//Geographical Domain is null
				public String zi_modifyPromotionalTransferRule(Map<String, String> dataMap) {

				userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_PROMOTIONAL_TRANSFER_RULE);
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				// User Access module ends.
				selectNetworkPage.selectNetwork();
				networkAdminHomePage.clickPromotionalTransferRule();
				String promotionalLevel = dataMap.get("promotionalLevel");
				promotionalTransferRuleSubCategories.clickModifyPromotionalTransferRule();
				addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
				addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
				addPromotionalTransferRule.clickOnSubmit();
				String uiMessage;
				if(!promotionalLevel.equals(Geography))
		       {
					modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));
		       }

				modifyPromotionalTransferRule.ClickOnSubmit();
				uiMessage = modifyPromotionalTransferRule.getActualMsg();
				return uiMessage;

			}
				
				//User or Grade is null
				public String zj_modifyPromotionalTransferRule(Map<String, String> dataMap) {

				userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_PROMOTIONAL_TRANSFER_RULE);
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				// User Access module ends.
				selectNetworkPage.selectNetwork();
				networkAdminHomePage.clickPromotionalTransferRule();
				String promotionalLevel = dataMap.get("promotionalLevel");
				promotionalTransferRuleSubCategories.clickModifyPromotionalTransferRule();
				addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
				addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
				addPromotionalTransferRule.clickOnSubmit();
				String uiMessage;
				if(promotionalLevel.equals(User) || promotionalLevel.equals(Grade)){
				modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));
				modifyPromotionalTransferRule.selectGeoTypeCode(dataMap.get("geoDomainType"));
				modifyPromotionalTransferRule.ClickOnSubmit();
				uiMessage = modifyPromotionalTransferRule.getActualMsg();
				}
				else{
				uiMessage = "Test Case only for Promotional Level User and Grade.";
				}
			
		       return uiMessage;

			}
				//CheckBox are unchecked
				public String zk_modifyPromotionalTransferRule(Map<String, String> dataMap) {

				userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_PROMOTIONAL_TRANSFER_RULE);
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				// User Access module ends.
				selectNetworkPage.selectNetwork();
				networkAdminHomePage.clickPromotionalTransferRule();
				String promotionalLevel = dataMap.get("promotionalLevel");
				promotionalTransferRuleSubCategories.clickModifyPromotionalTransferRule();
				addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
				addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
				addPromotionalTransferRule.clickOnSubmit();
				String uiMessage;
				if (promotionalLevel.equals(User)) {
					modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));
					modifyPromotionalTransferRule.SelectcategoryCode(dataMap.get("category"));
					modifyPromotionalTransferRule.selectGeoTypeCode(dataMap.get("geoDomainType"));
					modifyPromotionalTransferRule.selectGeoDomainCode(dataMap.get("geoDomainName"));
					modifyPromotionalTransferRule.enterUserName(dataMap.get("userName"));

				} else if (promotionalLevel.equals(Grade)) {
					modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));
					modifyPromotionalTransferRule.selectGradeCode(dataMap.get("grade"));

				} else if (promotionalLevel.equals(Geography)) {
					modifyPromotionalTransferRule.selectGeoTypeCode(dataMap.get("geoDomainType"));
					modifyPromotionalTransferRule.selectGeoDomainCode(dataMap.get("geoDomainName"));

				} else if (promotionalLevel.equals(Category)) {
					modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));

				}
				modifyPromotionalTransferRule.ClickOnSubmit();
				modifyPromotionalTransferRulePage2.ClickOnSubmit();
				uiMessage = modifyPromotionalTransferRulePage2.getActualMsg();
		       return uiMessage;

			}
				
				//Positive Flow for Date Range
				public String zl_modifyPromotionalTransferRule(Map<String, String> dataMap) {

				userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_PROMOTIONAL_TRANSFER_RULE);
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				// User Access module ends.
				selectNetworkPage.selectNetwork();
				networkAdminHomePage.clickPromotionalTransferRule();
				String promotionalLevel = dataMap.get("promotionalLevel");
				String currDate = networkAdminHomePage.getDate();
				String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
				String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
				String fromTime = networkAdminHomePage.getApplicableFromTime_min(2);
				String tillTime = networkAdminHomePage.getApplicableFromTime_min(5);
				promotionalTransferRuleSubCategories.clickModifyPromotionalTransferRule();
				addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
				addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
				addPromotionalTransferRule.clickOnSubmit();
				String uiMessage;
				if (promotionalLevel.equals(User)) {
					modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));
					modifyPromotionalTransferRule.SelectcategoryCode(dataMap.get("category"));
					modifyPromotionalTransferRule.selectGeoTypeCode(dataMap.get("geoDomainType"));
					modifyPromotionalTransferRule.selectGeoDomainCode(dataMap.get("geoDomainName"));
					modifyPromotionalTransferRule.enterUserName(dataMap.get("userName"));

				} else if (promotionalLevel.equals(Grade)) {
					modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));
					modifyPromotionalTransferRule.selectGradeCode(dataMap.get("grade"));

				} else if (promotionalLevel.equals(Geography)) {
					modifyPromotionalTransferRule.selectGeoTypeCode(dataMap.get("geoDomainType"));
					modifyPromotionalTransferRule.selectGeoDomainCode(dataMap.get("geoDomainName"));

				} else if (promotionalLevel.equals(Category)) {
					modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));

				}
				modifyPromotionalTransferRule.ClickOnSubmit();
				modifyPromotionalTransferRulePage2.selectTransferRule(dataMap);
				modifyPromotionalTransferRulePage2.ClickOnSubmit();
				modifyPromotionalTransferRulePage3.enterc2STransferRulesIndexed0fromDate(fromDate);
				modifyPromotionalTransferRulePage3.enterc2STransferRulesIndexed0fromTime(fromTime);
				modifyPromotionalTransferRulePage3.enterc2STransferRulesIndexed0tillDate(tillDate);
				modifyPromotionalTransferRulePage3.enterc2STransferRulesIndexed0tillTime(tillTime);
				modifyPromotionalTransferRulePage3.ClickOnModify();
				modifyPromotionalTransferRulePage4.ClickOnModify();
				uiMessage = modifyPromotionalTransferRulePage2.getActualMsg();
		       return uiMessage;

			}
				public String activate(Map<String, String> dataMap)
				{
					userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_PROMOTIONAL_TRANSFER_RULE);
					login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
					// User Access module ends.
					selectNetworkPage.selectNetwork();
					networkAdminHomePage.clickPromotionalTransferRule();
					String promotionalLevel = dataMap.get("promotionalLevel");
					String currDate = networkAdminHomePage.getDate();
					String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 1);
					String tillDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 6);
					String fromTime = networkAdminHomePage.getApplicableFromTime_min(2);
					String tillTime = networkAdminHomePage.getApplicableFromTime_min(5);
					promotionalTransferRuleSubCategories.clickModifyPromotionalTransferRule();
					addPromotionalTransferRule.selectPromotionCode(promotionalLevel);
					addPromotionalTransferRule.selectType(dataMap.get("type"), dataMap.get("slabType"));
					addPromotionalTransferRule.clickOnSubmit();
					String uiMessage;
					if (promotionalLevel.equals(User)) {
						modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));
						modifyPromotionalTransferRule.SelectcategoryCode(dataMap.get("category"));
						modifyPromotionalTransferRule.selectGeoTypeCode(dataMap.get("geoDomainType"));
						modifyPromotionalTransferRule.selectGeoDomainCode(dataMap.get("geoDomainName"));
						modifyPromotionalTransferRule.enterUserName(dataMap.get("userName"));

					} else if (promotionalLevel.equals(Grade)) {
						modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));
						modifyPromotionalTransferRule.selectGradeCode(dataMap.get("grade"));

					} else if (promotionalLevel.equals(Geography)) {
						modifyPromotionalTransferRule.selectGeoTypeCode(dataMap.get("geoDomainType"));
						modifyPromotionalTransferRule.selectGeoDomainCode(dataMap.get("geoDomainName"));

					} else if (promotionalLevel.equals(Category)) {
						modifyPromotionalTransferRule.selectdomainCodeforDomain(dataMap.get("domain"));

					}
					modifyPromotionalTransferRule.ClickOnSubmit();
					modifyPromotionalTransferRulePage2.selectTransferRule(dataMap);
					modifyPromotionalTransferRulePage2.ClickOnSubmit();
					modifyPromotionalTransferRulePage3.enterc2STransferRulesIndexed0fromDate(fromDate);
					modifyPromotionalTransferRulePage3.enterc2STransferRulesIndexed0fromTime(fromTime);
					modifyPromotionalTransferRulePage3.enterc2STransferRulesIndexed0tillDate(tillDate);
					modifyPromotionalTransferRulePage3.enterc2STransferRulesIndexed0tillTime(tillTime);
					modifyPromotionalTransferRulePage3.selectCardGroupSet0(dataMap.get("cardGroup"));
					modifyPromotionalTransferRulePage3.ClickOnModify();
					modifyPromotionalTransferRulePage4.ClickOnModify();
					uiMessage = modifyPromotionalTransferRulePage2.getActualMsg();
			        return uiMessage;

					
				}
}
