package com.Features;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.CacheController;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.networkadminpages.c2scardgroup.AddC2SCardGroupDetailsPage;
import com.pageobjects.networkadminpages.c2scardgroup.AddC2SCardGroupDetailsPage2;
import com.pageobjects.networkadminpages.c2scardgroup.C2SCardGroupDetailsPage;
import com.pageobjects.networkadminpages.c2scardgroup.C2SCardGroupStatusConfirmPage;
import com.pageobjects.networkadminpages.c2scardgroup.C2SCardGroupStatusPage;
import com.pageobjects.networkadminpages.c2scardgroup.C2Scardgroupstatuspage1;
import com.pageobjects.networkadminpages.c2scardgroup.DefaultC2SCardGroupPage;
import com.pageobjects.networkadminpages.c2scardgroup.ModifyC2SCardGroupPage1;
import com.pageobjects.networkadminpages.c2scardgroup.ModifyC2SCardGroupPage2;
import com.pageobjects.networkadminpages.c2scardgroup.ModifyC2SCardGroupPage3;
import com.pageobjects.networkadminpages.c2scardgroup.ViewC2SCardGroupPage;
import com.pageobjects.networkadminpages.c2scardgroup.ViewC2SCardGroupPage2;
import com.pageobjects.networkadminpages.c2scardgroup.ViewC2SCardGroupPage3;
import com.pageobjects.networkadminpages.homepage.CardGroupSubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;
import com.utils.Validator;
import com.utils._masterVO;

public class C2SCardGroup extends BaseTest{

	public String cardGroupName;
	public String ChoiceRCPreferenceValue;
	public WebDriver driver;
	public static int Client_CardGroupVer;
	public static ArrayList<String> CardGroup_SetID = new ArrayList<String>();

	NetworkAdminHomePage homePage;
	CardGroupSubCategories cardGrpCategories;
	AddC2SCardGroupDetailsPage addC2SCardGrp;
	AddC2SCardGroupDetailsPage2 addC2SCardGrp2;
	C2SCardGroupDetailsPage c2sCardGroupDetailsPage;
	Login login;
	RandomGeneration randmGeneration;
	//QueryRepository queryRepo;
	ModifyC2SCardGroupPage1 modifyCardGrpPage1;
	ModifyC2SCardGroupPage2 modifyCardGrpPage2;
	ModifyC2SCardGroupPage3 modifyCardGrpPage3;
	//ModifyC2SCardGroupPage4 modifyCardGrpPage4;
	ViewC2SCardGroupPage viewC2SCardGroupPage;
	ViewC2SCardGroupPage2 viewC2SCardGroupPage2;
	C2SCardGroupStatusPage c2sCradGroupStatusPage;
	UserAccess userAccess;	
	SelectNetworkPage networkPage;
	ViewC2SCardGroupPage3 viewC2SCardGroupPage3;
	C2SCardGroupStatusPage C2SCardGroupStatusPage;
	C2Scardgroupstatuspage1 C2Scardgroupstatuspage1;
	C2SCardGroupStatusConfirmPage C2SCardGroupStatusConfirmPage;
	DefaultC2SCardGroupPage DefaultC2SCardGroupPage;
	RandomGeneration RandomGenerator;
	CacheUpdate CacheUpdate;

	public C2SCardGroup(WebDriver driver) {
		this.driver = driver;
		// Initializing the Pages.
		homePage = new NetworkAdminHomePage(driver);
		cardGrpCategories = new CardGroupSubCategories(driver);
		addC2SCardGrp = new AddC2SCardGroupDetailsPage(driver);
		addC2SCardGrp2 = new AddC2SCardGroupDetailsPage2(driver);
		c2sCardGroupDetailsPage = new C2SCardGroupDetailsPage(driver);
		login = new Login();
		randmGeneration = new RandomGeneration();	

		modifyCardGrpPage1 = new ModifyC2SCardGroupPage1(driver);
		modifyCardGrpPage2 = new ModifyC2SCardGroupPage2(driver);
		modifyCardGrpPage3 = new ModifyC2SCardGroupPage3(driver);
		c2sCradGroupStatusPage = new C2SCardGroupStatusPage(driver);
		userAccess = new UserAccess();
		viewC2SCardGroupPage = new ViewC2SCardGroupPage(driver);
		viewC2SCardGroupPage2 = new ViewC2SCardGroupPage2(driver);
		viewC2SCardGroupPage3 = new ViewC2SCardGroupPage3(driver);

		networkPage = new SelectNetworkPage(driver);
		C2SCardGroupStatusPage = new C2SCardGroupStatusPage(driver);
		C2Scardgroupstatuspage1 = new C2Scardgroupstatuspage1(driver);
		C2SCardGroupStatusConfirmPage = new C2SCardGroupStatusConfirmPage(driver);
		CacheUpdate = new CacheUpdate(driver);
		DefaultC2SCardGroupPage = new DefaultC2SCardGroupPage(driver);
		RandomGenerator = new RandomGeneration();
		
		Client_CardGroupVer = Integer.parseInt(_masterVO.getClientDetail("C2SCARDGROUP_VER"));

	}

	public void enterCardGroupDetails(int array[], int m, int n, String cardGroupName, int CardgroupType) throws InterruptedException{
		C2SCardGroupDetailsPage c2sCardGroupDetailsPage = new C2SCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);
		
		if (Client_CardGroupVer == 2)
			c2sCardGroupDetailsPage.enterCardGroupCode(_masterVO.getProperty("CardGroupCode"));
		else {
			CardGroup_SetID = UniqueChecker.UC_CARDGROUPID(CardGroup_SetID);
			c2sCardGroupDetailsPage.enterCardGroupCode(CardGroup_SetID.get(CardGroup_SetID.size() - 1));
		}
		
		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			c2sCardGroupDetailsPage.entercardName(cardGroupName);

		c2sCardGroupDetailsPage.enterStartRange(++array[m] + "");
		c2sCardGroupDetailsPage.enterEndRange(array[n] + "");
		
		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			c2sCardGroupDetailsPage.checkReversalPermitted();
		
		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				c2sCardGroupDetailsPage.checkCOSRequired();
		}
		
		c2sCardGroupDetailsPage.selectValidityType(PretupsI.VLTYP_LOOKUP);
		c2sCardGroupDetailsPage.enterValidityDays(_masterVO.getProperty("ValidityDays"));
		c2sCardGroupDetailsPage.enterGracePeriod(_masterVO.getProperty("GracePeriod"));
		c2sCardGroupDetailsPage.enterMultipleOf(_masterVO.getProperty("MultipleOf"));
		c2sCardGroupDetailsPage.checkOnline();
		c2sCardGroupDetailsPage.checkBoth();
		
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			c2sCardGroupDetailsPage.selectCardGroupType(CardgroupType);
		
		c2sCardGroupDetailsPage.selectTax1Type(_masterVO.getProperty("Tax1Type"));
		c2sCardGroupDetailsPage.enterTax1Rate(_masterVO.getProperty("Tax1Rate"));
		c2sCardGroupDetailsPage.selectTax2Type(_masterVO.getProperty("Tax2Type"));
		c2sCardGroupDetailsPage.enterTax2Rate(_masterVO.getProperty("Tax2Rate"));
		
		if (_masterVO.getClientDetail("TAX3TAX4_APPLICABLE").equalsIgnoreCase("true")) {
			c2sCardGroupDetailsPage.selectTax3Type(_masterVO.getProperty("Tax3Type"));
			c2sCardGroupDetailsPage.enterTax3Rate(_masterVO.getProperty("Tax3Rate"));
			c2sCardGroupDetailsPage.selectTax4Type(_masterVO.getProperty("Tax4Type"));
			c2sCardGroupDetailsPage.enterTax4Rate(_masterVO.getProperty("Tax4Rate"));
		}
		
		c2sCardGroupDetailsPage.selectProcessingFeeType(_masterVO.getProperty("TaxType"));
		c2sCardGroupDetailsPage.enterProcessingFeeRate(_masterVO.getProperty("TaxRate"));
		c2sCardGroupDetailsPage.enterProcessingFeeMinAmount("1");
		c2sCardGroupDetailsPage.enterProcessingFeeMaxAmount("2");
		c2sCardGroupDetailsPage.enterReceiverConversionFactor(_masterVO.getProperty("ReceiverConversionFactor"));
		c2sCardGroupDetailsPage.selectBonusType(_masterVO.getProperty("BonusType"));
		c2sCardGroupDetailsPage.enterBonusValue(_masterVO.getProperty("BonusValue"));
		c2sCardGroupDetailsPage.enterBonusValidity(_masterVO.getProperty("BonusValidity"));
		c2sCardGroupDetailsPage.enterBonusConversionFactor(_masterVO.getProperty("BonusConversionFactor"));
		c2sCardGroupDetailsPage.enterBonusValidityDays(_masterVO.getProperty("BonusValidityDays"));
		c2sCardGroupDetailsPage.clickAddButton();

		SwitchWindow.backwindow(driver); // BackWindow Handler

	}

	
	public void enterCardGroupDetails_usingMap(Map<String,String> datamap,int array[], int m, int n, String cardGroupName) throws InterruptedException{
		C2SCardGroupDetailsPage c2sCardGroupDetailsPage = new C2SCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);
		Map<String, String> C2SCardGroup_Map = datamap;
		if (Client_CardGroupVer == 2)
		c2sCardGroupDetailsPage.enterCardGroupCode(C2SCardGroup_Map.get("CardGroupCode"));
		else {
			CardGroup_SetID = UniqueChecker.UC_CARDGROUPID(CardGroup_SetID);
			c2sCardGroupDetailsPage.enterCardGroupCode(CardGroup_SetID.get(CardGroup_SetID.size() - 1));
		}
		
		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			c2sCardGroupDetailsPage.entercardName(cardGroupName);

		c2sCardGroupDetailsPage.enterStartRange(++array[m] + "");
		c2sCardGroupDetailsPage.enterEndRange(array[n] + "");
		
		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				c2sCardGroupDetailsPage.checkCOSRequired();
		}
		
		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			c2sCardGroupDetailsPage.checkReversalPermitted();
		
		c2sCardGroupDetailsPage.selectValidityType(C2SCardGroup_Map.get("ValidityType"));
		c2sCardGroupDetailsPage.enterValidityDays(C2SCardGroup_Map.get("ValidityDays"));
		c2sCardGroupDetailsPage.enterGracePeriod(C2SCardGroup_Map.get("GracePeriod"));
		c2sCardGroupDetailsPage.enterMultipleOf(C2SCardGroup_Map.get("MultipleOf"));
		c2sCardGroupDetailsPage.checkOnline();
		c2sCardGroupDetailsPage.checkBoth();
		c2sCardGroupDetailsPage.selectTax1Type(C2SCardGroup_Map.get("Tax1Type"));
		c2sCardGroupDetailsPage.enterTax1Rate(C2SCardGroup_Map.get("Tax1Rate"));
		c2sCardGroupDetailsPage.selectTax2Type(C2SCardGroup_Map.get("Tax2Type"));
		c2sCardGroupDetailsPage.enterTax2Rate(C2SCardGroup_Map.get("Tax2Rate"));
		c2sCardGroupDetailsPage.selectProcessingFeeType(C2SCardGroup_Map.get("ProcessingFeeType"));
		c2sCardGroupDetailsPage.enterProcessingFeeRate(C2SCardGroup_Map.get("ProcessingFeeRate"));
		c2sCardGroupDetailsPage.enterProcessingFeeMinAmount(C2SCardGroup_Map.get("ProcessingFeeMinAmount"));
		c2sCardGroupDetailsPage.enterProcessingFeeMaxAmount(C2SCardGroup_Map.get("ProcessingFeeMaxAmount"));
		c2sCardGroupDetailsPage.enterReceiverConversionFactor(C2SCardGroup_Map.get("ReceiverConversionFactor"));
		c2sCardGroupDetailsPage.selectBonusType(C2SCardGroup_Map.get("BonusType"));
		c2sCardGroupDetailsPage.enterBonusValue(C2SCardGroup_Map.get("BonusValue"));
		c2sCardGroupDetailsPage.enterBonusValidity(C2SCardGroup_Map.get("BonusValidity"));
		c2sCardGroupDetailsPage.enterBonusConversionFactor(C2SCardGroup_Map.get("BonusConversionFactor"));
		c2sCardGroupDetailsPage.enterBonusValidityDays(C2SCardGroup_Map.get("BonusValidityDays"));

		c2sCardGroupDetailsPage.clickAddButton();
		C2SCardGroup_Map.put("ACTUAL",CONSTANT.CARDGROUP_SLAB_ERR);
		
		SwitchWindow.backwindow(driver); // BackWindow Handler

	}

	
	
	
	public void enterCardGroupDetails_OverlapRange(Map<String,String> datamap,int array[], int m, int n, String cardGroupName) throws InterruptedException{
		C2SCardGroupDetailsPage c2sCardGroupDetailsPage = new C2SCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);
		Map<String, String> C2SCardGroup_Map = datamap;

		c2sCardGroupDetailsPage.enterCardGroupCode(C2SCardGroup_Map.get("CardGroupCode"));
		
		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			c2sCardGroupDetailsPage.entercardName(cardGroupName);

		c2sCardGroupDetailsPage.enterStartRange(array[m] + "");
		c2sCardGroupDetailsPage.enterEndRange(array[n] + "");
		
		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				c2sCardGroupDetailsPage.checkCOSRequired();
		}
		
		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			c2sCardGroupDetailsPage.checkReversalPermitted();
		
		c2sCardGroupDetailsPage.selectValidityType(C2SCardGroup_Map.get("ValidityType"));
		c2sCardGroupDetailsPage.enterValidityDays(C2SCardGroup_Map.get("ValidityDays"));
		c2sCardGroupDetailsPage.enterGracePeriod(C2SCardGroup_Map.get("GracePeriod"));
		c2sCardGroupDetailsPage.enterMultipleOf(C2SCardGroup_Map.get("MultipleOf"));
		c2sCardGroupDetailsPage.checkOnline();
		c2sCardGroupDetailsPage.checkBoth();
		c2sCardGroupDetailsPage.selectTax1Type(C2SCardGroup_Map.get("Tax1Type"));
		c2sCardGroupDetailsPage.enterTax1Rate(C2SCardGroup_Map.get("Tax1Rate"));
		c2sCardGroupDetailsPage.selectTax2Type(C2SCardGroup_Map.get("Tax2Type"));
		c2sCardGroupDetailsPage.enterTax2Rate(C2SCardGroup_Map.get("Tax2Rate"));
		c2sCardGroupDetailsPage.selectProcessingFeeType(C2SCardGroup_Map.get("ProcessingFeeType"));
		c2sCardGroupDetailsPage.enterProcessingFeeRate(C2SCardGroup_Map.get("ProcessingFeeRate"));
		c2sCardGroupDetailsPage.enterProcessingFeeMinAmount(C2SCardGroup_Map.get("ProcessingFeeMinAmount"));
		c2sCardGroupDetailsPage.enterProcessingFeeMaxAmount(C2SCardGroup_Map.get("ProcessingFeeMaxAmount"));
		c2sCardGroupDetailsPage.enterReceiverConversionFactor(C2SCardGroup_Map.get("ReceiverConversionFactor"));
		c2sCardGroupDetailsPage.selectBonusType(C2SCardGroup_Map.get("BonusType"));
		c2sCardGroupDetailsPage.enterBonusValue(C2SCardGroup_Map.get("BonusValue"));
		c2sCardGroupDetailsPage.enterBonusValidity(C2SCardGroup_Map.get("BonusValidity"));
		c2sCardGroupDetailsPage.enterBonusConversionFactor(C2SCardGroup_Map.get("BonusConversionFactor"));
		c2sCardGroupDetailsPage.enterBonusValidityDays(C2SCardGroup_Map.get("BonusValidityDays"));

		c2sCardGroupDetailsPage.clickAddButton();
		C2SCardGroup_Map.put("ACTUAL",CONSTANT.CARDGROUP_SLAB_ERR);
		
		SwitchWindow.backwindow(driver); // BackWindow Handler

	}
	
	private String getTodayDate() {
		Log.info("Trying to select Date");
		String date = "null" ;
		SimpleDateFormat s = new SimpleDateFormat("dd/MM/yy");
		Date d = new Date();
		date = s.format(d);
		return date;
	}

	public Map<String, String> c2SCardGroupCreation(String serviceName, String subService) throws InterruptedException {
		final String methodname = "c2SCardGroupCreation";
		Log.methodEntry(methodname, serviceName, subService);
		
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();
		
		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		Log.info("Choice Recharge status is " +ChoiceRCPreferenceValue);

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();

		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subService);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);
		
		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
			crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
		}
		//Thread.sleep(2000);
		addC2SCardGrp.enterApplicableFromDate(getTodayDate());
		//addC2SCardGrp.enterDateFromDatePicker();
		//Thread.sleep(3000);
		//addC2SCardGrp.enterApplicableFromDate(homePage.getDate());
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		//Thread.sleep(3000);
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		//Thread.sleep(3000);
		addC2SCardGrp.clickSaveButton();
		//Thread.sleep(3000);
		addC2SCardGrp.clickSaveButton();
		addC2SCardGrp2.clickConfirmbutton();
		
		String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		String actual = addC2SCardGrp.getMessage();

		Assert.assertEquals(expected, actual);
		arrayList.add(cardGroupName);

		dataMap.put("ACTUALMESSAGE", actual);
		dataMap.put("CARDGROUPNAME", cardGroupName);
		dataMap.put("CARDGROUP_SETID", DBHandler.AccessHandler.getCardGroupSetID(cardGroupName));
		
		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());
		
		Log.methodExit(methodname);
		return dataMap;
	}
	


	public Map<String, String> c2SPromoCardGroupCreation(String serviceName, String subService) throws InterruptedException {

		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();
		
		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		Log.info("Choice Recharge status is " +ChoiceRCPreferenceValue);

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else			
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();

		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subService);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);

		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");


		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
			crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
		}
		addC2SCardGrp.enterApplicableFromDate(homePage.getTodayDate());
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		
		boolean setTypeVisibity = addC2SCardGrp.cardGrpTypeVisibility();
		if (setTypeVisibity == true){
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_PROMO_LOOKUPS);
		addC2SCardGrp.clickSaveButton();

		addC2SCardGrp2.clickConfirmbutton();
		String actual = addC2SCardGrp.getMessage();

		dataMap.put("ACTUALMESSAGE", actual);
		dataMap.put("CARDGROUPNAME", cardGroupName);

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		Validator.messageCompare(actual, expected);
		
		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());
		}
		else{

			String setTypeValue = driver.findElement(By.xpath("//form/table//table//tr[7]/td/following-sibling::td[1]")).getText();			
			String LookUPCode = DBHandler.AccessHandler.getLookUpCode(setTypeValue);
			Log.info("LookUPCode is " +LookUPCode);
			
			if(!LookUPCode.equals(PretupsI.CARDGRP_PROMO_LOOKUPS)){				
				currentNode.skip("Card Group Set Type Value is not Promotional");
				dataMap.put("ACTUALMESSAGE", "");
				dataMap.put("CARDGROUPNAME", "");
			} else {
				addC2SCardGrp.clickSaveButton();
				addC2SCardGrp2.clickConfirmbutton();
				String actual = addC2SCardGrp.getMessage();
				dataMap.put("ACTUALMESSAGE", actual);
				dataMap.put("CARDGROUPNAME", cardGroupName);
				String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");
				Validator.messageCompare(actual, expected);
				CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());
			}
		}
		return dataMap;
	}

	public Map<String, String> C2SCardGroupFutureDate(String serviceName, String subService) throws InterruptedException {
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service drop-down and create
		 * card groups.
		 */
		networkPage.selectNetwork();

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();
		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subService);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);
		
		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
			crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
		}
		
		addC2SCardGrp.enterApplicableFromDate(homePage.addDaysToCurrentDate(homePage.getDate(), 1));
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		addC2SCardGrp.clickSaveButton();

		addC2SCardGrp2.clickConfirmbutton();

		// Asserting message on confirmation.
		String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		String actual = addC2SCardGrp.getMessage();

		Assert.assertEquals(expected, actual);
		arrayList.add(cardGroupName);

		dataMap.put("ACTUALMESSAGE", actual);
		dataMap.put("CARDGROUPNAME", cardGroupName);

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		return dataMap;
	}

	public String c2SCardGroupDeletion(String serviceName, String subservice, String cardGroupName) throws InterruptedException {

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_MODIFY_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		networkPage.selectNetwork();
		homePage.clickCardGroup();
		cardGrpCategories.clickModifyC2SCardGroup();

		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);
		modifyCardGrpPage1.clickDeleteButton();

		Alert alert = driver.switchTo().alert();
		alert.accept();
		
		String actual=new AddChannelUserDetailsPage(driver).getActualMessage();//modifyCardGrpPage1.getMessage();
		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		return actual;
	}

	public String c2sCardGroupModification_AddNewSlab(String serviceName, String subservice, String cardGroupName) throws InterruptedException{

		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		//int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		//int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		//int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int Slab5 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab5"));
		int crdTypIndex = 1;

		int array[] = { Slab4 , Slab5 };

		// Login as a Network Admin. Click Card Group.
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_MODIFY_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);

		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickModifyC2SCardGroup();

		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);
		modifyCardGrpPage1.clickModifyButton();

		modifyCardGrpPage2.clickAddCardGroupList();

		// Filling new slab for different ranges.
		for (int m = 0; m < 1; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
		}

		Thread.sleep(2000);
		addC2SCardGrp.enterApplicableFromDate(homePage.getDate());
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());

		addC2SCardGrp.clickSaveButton();

		addC2SCardGrp2.clickConfirmbutton();

		// Asserting message on confirmation.

		String actual = modifyCardGrpPage1.getMessage();
		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		return actual;
	}

	public void writeCardGroupToExcel(String CardGroupName, String CardGroupSetID, int rowNum){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		ExcelUtility.setCellData(0, ExcelI.CARDGROUP_NAME, rowNum, CardGroupName);
		ExcelUtility.setCellData(0, ExcelI.CARDGROUP_SETID, rowNum, CardGroupSetID);
	}

	
	
	public void writePromoCardGroupToExcel(String CardGroupName, int rowNum){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		ExcelUtility.setCellData(0, ExcelI.PROMO_CARDGROUP_NAME, rowNum, CardGroupName);

	}
	
	public String fetchCardGroup(int rownum)
	{
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		String cardGroup = ExcelUtility.getCellData(0, ExcelI.PROMO_CARDGROUP_NAME, rownum);
		return cardGroup;
	}
	public String c2sCardGroupModification_EditCardGroup(String serviceName, String subservice, String cardGroupName) throws InterruptedException{

		//ArrayList<String> userInfo= UserAccess.getUserWithAccess("EDITC2SCARDGRP");
		//login.UserLogin(driver, "Operator", userInfo.get(0), userInfo.get(1));
		//login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_MODIFY_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickModifyC2SCardGroup();

		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);
		modifyCardGrpPage1.clickModifyButton();
		modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());
		modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());
		//modifyCardGrpPage2.clickEditCardGroup();
		//SwitchWindow.switchwindow(driver);

		//modifyCardGrpPage3.clickAddBtn();

		addC2SCardGrp.clickSaveButton();

		addC2SCardGrp2.clickConfirmbutton();

		// Asserting message on confirmation.

		//String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		String actual = modifyCardGrpPage1.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		//Assert.assertEquals(expected, actual);
		return actual;
	}


	public String c2sCardGroupModification_EditCardGroupForFutureDate(String serviceName, String subservice, String cardGroupName) throws InterruptedException{

		//ArrayList<String> userInfo= UserAccess.getUserWithAccess("EDITC2SCARDGRP");
		//login.UserLogin(driver, "Operator", userInfo.get(0), userInfo.get(1));
		//login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_MODIFY_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickModifyC2SCardGroup();

		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);
		modifyCardGrpPage1.clickModifyButton();
		modifyCardGrpPage2.enterApplicableFromDate(homePage.addDaysToCurrentDate(homePage.getDate(), 1));
		modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());
		//modifyCardGrpPage2.clickEditCardGroup();
		//SwitchWindow.switchwindow(driver);

		//modifyCardGrpPage3.clickAddBtn();

		addC2SCardGrp.clickSaveButton();

		addC2SCardGrp2.clickConfirmbutton();

		// Asserting message on confirmation.

		//String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		String actual = modifyCardGrpPage1.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		//Assert.assertEquals(expected, actual);
		return actual;
	}

	public String c2sCardGroupModification_SuspendCardGroupSlab(String serviceName, String subservice, String cardGroupName) throws InterruptedException{

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_MODIFY_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickModifyC2SCardGroup();

		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);
		modifyCardGrpPage1.clickModifyButton();

		modifyCardGrpPage2.clickEditCardGroup();
		SwitchWindow.switchwindow(driver);
		String startRange= driver.findElement(By.name("startRange")).getAttribute("value");
		String endRange= driver.findElement(By.name("endRange")).getAttribute("value");

		int startRng=Integer.parseInt(startRange)*100;
		int endRng=Integer.parseInt(endRange)*100;

		modifyCardGrpPage3.clickSuspendBtn();	

		SwitchWindow.backwindow(driver);

		modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());

		modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime_1min());


		modifyCardGrpPage2.clicksaveBtn();

		Thread.sleep(5000);


		addC2SCardGrp2.clickConfirmbutton();

		//String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		String actual = modifyCardGrpPage1.getMessage();

		//Thread.sleep(arg0);

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		//Assert.assertEquals(expected, actual);

		String actualStatus= DBHandler.AccessHandler.getCardGroupStatus(cardGroupName, startRng, endRng);
		String expectedStatus="S";
		Assert.assertEquals(actualStatus, expectedStatus);

		Map<String, String> dataMap=new HashMap<String, String>();

	    dataMap.put("ACTUAL_MESSAGE", actual);
		dataMap.put("STATUS", actualStatus);

		return actual;
	}






	public String c2sCardGroupModification_ResumeCardGroupSlab(String serviceName, String subservice, String cardGroupName) throws InterruptedException{

		// Login as a Network Admin. Click Card Group.

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_MODIFY_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();

		homePage.clickCardGroup();

		cardGrpCategories.clickModifyC2SCardGroup();

		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);
		modifyCardGrpPage1.clickModifyButton();

		boolean verifySuspended = modifyCardGrpPage2.suspendedTextVisibility();

		if (verifySuspended ==true){
			boolean result = modifyCardGrpPage2.verifySuspended();
			if (result == true){

				modifyCardGrpPage2.clickEditCardGroup();

			}
		}


		if (verifySuspended==false){
			System.out.println("The card group is not suspended");
		}

		SwitchWindow.switchwindow(driver);
		String startRange= driver.findElement(By.name("startRange")).getAttribute("value");
		String endRange= driver.findElement(By.name("endRange")).getAttribute("value");

		int startRng=Integer.parseInt(startRange)*100;
		int endRng=Integer.parseInt(endRange)*100;

		modifyCardGrpPage3.clickResumeBtn();	

		SwitchWindow.backwindow(driver);

		//Thread.sleep(60000);

		modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());
		modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());

		modifyCardGrpPage2.clicksaveBtn();

		Thread.sleep(5000);

		addC2SCardGrp2.clickConfirmbutton();

		String actual = modifyCardGrpPage1.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		String actualStatus= DBHandler.AccessHandler.getCardGroupStatus(cardGroupName, startRng, endRng);
		String expectedStatus="Y";
		Assert.assertEquals(actualStatus, expectedStatus);

		Map<String, String> dataMap=new HashMap<String, String>();
		dataMap.put("ACTUAL_MESSAGE", actual);
		dataMap.put("STATUS", actualStatus);

		return actual;

	}


	public String viewC2SCardGroup(String serviceName, String subservice, String cardGroupName) throws InterruptedException {

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_VIEW_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickViewC2SCardGroup();
		viewC2SCardGroupPage.selectServiceType(serviceName);
		viewC2SCardGroupPage.selectC2SCardGroupSubService(subservice);
		viewC2SCardGroupPage.selectC2SCardGroupSetName(cardGroupName);
		viewC2SCardGroupPage.clickSubmitButton();

		viewC2SCardGroupPage2.clickSubmitButton();


		//String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.view.heading");
		String actual = viewC2SCardGroupPage3.getMessage();	


		return actual;
	}


	public String c2SCardGroupStatus(String cardGroupName){
		//ArrayList<String> userInfo= UserAccess.getUserWithAccess("VIEWC2SCARDGRP");
		//login.UserLogin(driver, "Operator", userInfo.get(0), userInfo.get(1));
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_STATUS_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickC2SCardGroupStatus();
		C2SCardGroupStatusPage.checkC2SCardGroup(cardGroupName);

		//String expected = "Checked";

		//Assert.assertEquals(expected, actual);

		C2Scardgroupstatuspage1.ClickOnsave();
		C2Scardgroupstatuspage1.ClickOnconfirm();

		String actual = C2SCardGroupStatusConfirmPage.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.view.heading");


		return actual;



	}





	public boolean c2sCardGroupModification_EditCardGroupNewVersion(String serviceName, String subservice, String cardGroupName) throws InterruptedException{


		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_MODIFY_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickModifyC2SCardGroup();

		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);

		
		String currentVersion=driver.findElement(By.name("selectCardGroupSetVersionId")).getText().trim();
		
		System.out.println(" Current Version is :" +currentVersion);
		char ss=currentVersion.charAt(0);
		
		System.out.println("my Value===="+ss);
		
		String curr= Character.toString(ss);
		
		int currentVersion1=Integer.parseInt(curr);
		
		
		System.out.println(" Current Version1 is :" +currentVersion1);
		modifyCardGrpPage1.clickModifyButton();
		modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());
		modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());
		//modifyCardGrpPage2.clickEditCardGroup();
		//SwitchWindow.switchwindow(driver);

		//modifyCardGrpPage3.clickAddBtn();

		addC2SCardGrp.clickSaveButton();
		
		int newVersion = addC2SCardGrp2.getVersionIdFromMessage();

		addC2SCardGrp2.clickConfirmbutton();
		Thread.sleep(1000);

	System.out.println("UpdatedVersionId is :" +newVersion);

		// Asserting message on confirmation.

		
		//int expected=currentVersion1+1;

		boolean result =false;

		if (newVersion>currentVersion1){

			return result = true;
		}

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		//Assert.assertEquals(expected, actual);
		return result;

	}


	public Map<String, String> setDefaultC2SCardGroup(String serviceName, String subService, String cardGroupName) throws InterruptedException {

		Map<String, String> dataMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_DEFAULT_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();
		homePage.clickCardGroup();
		cardGrpCategories.clickDefaultC2SCardGroup();
		DefaultC2SCardGroupPage.selectServiceType(serviceName);
		DefaultC2SCardGroupPage.selectSubService(subService);
		
		DefaultC2SCardGroupPage.selectSetName(cardGroupName);
		System.out.println(cardGroupName);
		DefaultC2SCardGroupPage.clickDefault();
		driver.switchTo().alert().accept();

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		String actual= DefaultC2SCardGroupPage.getMessage();


		dataMap.put("ACTUALMESSAGE", actual);
		dataMap.put("CARDGROUPNAME", cardGroupName);
		return dataMap;
	}

	

	public Map<String, String> c2SCardGroupCreation_withSuspendedSlab(String serviceName, String subService) throws InterruptedException {

		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		//ArrayList<String> userInfo= UserAccess.getUserWithAccess("ADDC2SCARDGRP");
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();
		homePage.clickCardGroup();
		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subService);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);

		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
			crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
		}

		modifyCardGrpPage2.clickEditCardGroup();
		SwitchWindow.switchwindow(driver);
		String startRange= driver.findElement(By.name("startRange")).getAttribute("value");
		String endRange= driver.findElement(By.name("endRange")).getAttribute("value");

		int startRng=Integer.parseInt(startRange)*100;
		int endRng=Integer.parseInt(endRange)*100;

		modifyCardGrpPage3.clickSuspendBtn();	

		SwitchWindow.backwindow(driver);

		addC2SCardGrp.enterApplicableFromDate(homePage.getDate());
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		addC2SCardGrp.clickSaveButton();

		addC2SCardGrp2.clickConfirmbutton();

		// Asserting message on confirmation.
		String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		String actual = addC2SCardGrp.getMessage();

		Assert.assertEquals(expected, actual);
		arrayList.add(cardGroupName);

		dataMap.put("ACTUALMESSAGE", actual);
		dataMap.put("CARDGROUPNAME", cardGroupName);

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		String actualStatus= DBHandler.AccessHandler.getCardGroupStatus(cardGroupName, startRng, endRng);
		String expectedStatus="S";
		Assert.assertEquals(actualStatus, expectedStatus);

		dataMap.put("STATUS", actualStatus);
		return dataMap;
	}

	public String c2SCardGroupDeletionForDefaultCardGroup(String serviceName, String subservice, String cardGroupName) throws InterruptedException {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_MODIFY_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		networkPage.selectNetwork();
		homePage.clickCardGroup();
		cardGrpCategories.clickModifyC2SCardGroup();

		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		
		ExcelUtility.setExcelFile( MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.NAME, i).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i).matches(subservice)))

			break;
		}

		cardGroupName= ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
		System.out.println(cardGroupName);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);
		modifyCardGrpPage1.clickDeleteButton();

		Alert alert = driver.switchTo().alert();
		alert.accept();
		String actual=modifyCardGrpPage1.getErrorMessage();

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		return actual;
		//Assert.assertEquals(actual, expected);
	}
	
	public String c2SCardGroupSuspend(String cardGroupName){
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_STATUS_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickC2SCardGroupStatus();
		C2SCardGroupStatusPage.suspendC2SCardGroup(cardGroupName);

		C2Scardgroupstatuspage1.ClickOnsave();
		C2Scardgroupstatuspage1.ClickOnconfirm();

		String actual = C2SCardGroupStatusConfirmPage.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		//String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.view.heading");

		return actual;

	}
	
	public String c2SCardGroupActivateCardGroup(String cardGroupName){
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_STATUS_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickC2SCardGroupStatus();
		C2SCardGroupStatusPage.resumeC2SCardGroup(cardGroupName);

		C2Scardgroupstatuspage1.ClickOnsave();
		C2Scardgroupstatuspage1.ClickOnconfirm();
		String actual = C2SCardGroupStatusConfirmPage.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		return actual;
	}

	//Validation Test cases Logic	
	public String c2SCardGroupErrorValidation_BlankCardGroupSetName(String serviceName, String subservice,String cardGroupName) throws InterruptedException {

		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();
		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();
		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		addC2SCardGrp.selectSubService(subservice);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);
		
		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");
		
		// Filling multiple slabs for different ranges.
			for (int m = 0; m < 3; m++) {
				int n = m + 1;
				addC2SCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
				crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
			}

		addC2SCardGrp.enterApplicableFromDate(homePage.getDate());
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		addC2SCardGrp.clickSaveButton();
		
		String actual= addC2SCardGrp.getErrorMessage();
		
		return actual;

	}

	
	
	public String c2SCardGroupErrorValidation_UniqueCardGroupSetName(String serviceName, String subservice,String cardGroupName) throws InterruptedException {

		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();
		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();
		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		addC2SCardGrp.selectSubService(subservice);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);
		
		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");
		
		// Filling multiple slabs for different ranges.
			for (int m = 0; m < 3; m++) {
				int n = m + 1;
				addC2SCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
				crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
			}

		addC2SCardGrp.enterApplicableFromDate(homePage.getDate());
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		addC2SCardGrp.clickSaveButton();
        addC2SCardGrp2.clickConfirmbutton();
		
		
		String actual= addC2SCardGrp.getErrorMessage();
		
		return actual;

	}

	
	public String c2SCardGroupErrorValidation_AppTimeFormat(String serviceName, String subservice) throws InterruptedException {

		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();

		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subservice);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);

		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");
		
		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 1; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
			crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
		}
				
		addC2SCardGrp.enterApplicableFromDate(homePage.getDate());
		addC2SCardGrp.enterApplicableFromHour("");
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		addC2SCardGrp.clickSaveButton();
		String actual= addC2SCardGrp.getErrorMessage();
		
		return actual;

	}
	
	public String c2SCardGroupErrorValidation_AppDate(String serviceName, String subservice) throws InterruptedException {

		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();

		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subservice);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);

		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");
		
		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 1; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
			crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
		}
	
		addC2SCardGrp.enterApplicableFromDate(homePage.addDaysToCurrentDate(homePage.getDate(), -1));
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		addC2SCardGrp.clickSaveButton();
		String actual= addC2SCardGrp.getErrorMessage();
		
		return actual;
	}
	
	// C2S Slab Field Validation 	
	public Map<String, String> c2SCardGroupSlabErrorValidation(Map<String, String> dataMap, String serviceName, String subservice) throws InterruptedException {

		Map<String, String> C2SCardGroup_Map = dataMap;
		Map<String, String> dataMap1 = new HashMap<String, String>();
		
		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		System.out.println("Choice Recharge status is " +ChoiceRCPreferenceValue);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();

		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subservice);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);

		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");
		
		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 1; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails_usingMap(C2SCardGroup_Map, array, m, n, cardGroupName);
		}
		
		/*
		 addC2SCardGrp.enterApplicableFromDate(homePage.getDate());
		 
		addC2SCardGrp.enterApplicableFromHour("");
		addC2SCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
		addC2SCardGrp.clickSaveButton();
		String actual= addC2SCardGrp.getErrorMessage();
		
		*/
		return dataMap;

	}
	
	// Added by Krishan for Vodafone Specific Card Group Type Handling
	public int cardgroupTypeCounterUpdater(int CardGroupType_Slab_Count, int crdTypIndex) {
		if (crdTypIndex > CardGroupType_Slab_Count) 
			return 1;
		else
			return crdTypIndex;
	}
	
	
	
	public Map<String, String> c2SCardGroupCreationForDefault(String serviceName, String subService) throws InterruptedException, ParseException {
		final String methodname = "c2SCardGroupCreation";
		Log.methodEntry(methodname, serviceName, subService);
		
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();
		
		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		Log.info("Choice Recharge status is " +ChoiceRCPreferenceValue);

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();

		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subService);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);
		
		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
			crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
		}
		addC2SCardGrp.enterApplicableFromDate(homePage.getDate());
		String time = addC2SCardGrp.enterApplicableFromHour1(homePage.getApplicableFromTime_1min());
		System.out.println(time);
		String time2 = time+":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		addC2SCardGrp.clickSaveButton();
		addC2SCardGrp2.clickConfirmbutton();
		
		String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		String actual = addC2SCardGrp.getMessage();

		Assert.assertEquals(expected, actual);
		arrayList.add(cardGroupName);

		dataMap.put("ACTUALMESSAGE", actual);
		dataMap.put("CARDGROUPNAME", cardGroupName);
		dataMap.put("CARDGROUP_SETID", DBHandler.AccessHandler.getCardGroupSetID(cardGroupName));
		dataMap.put("Requiredtime",String.valueOf(requiredtime));
		
		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());
		
		Log.methodExit(methodname);
		return dataMap;
	}


	
	
	public String C2SCardGroupStatusDeativate(String cardGroupName){

		String actual=null;

		if (cardGroupName!=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_STATUS_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickC2SCardGroupStatus();
			C2SCardGroupStatusPage.CardGroupDeactivateNegative(cardGroupName,"deactivated","deactivated");
			

			C2Scardgroupstatuspage1.ClickOnsave();
			C2Scardgroupstatuspage1.ClickOnconfirm();


			actual = C2SCardGroupStatusConfirmPage.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}
		else{
			currentNode.skip("The CardGroupName recieved as null");
		}

		return actual;



	}
	
	
	
	public Map<String, String> C2SCardGroupApplicableDateVerification(String serviceName, String subService) throws InterruptedException {
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service drop-down and create
		 * card groups.
		 */
		networkPage.selectNetwork();

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();
		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subService);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);
		
		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
			crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
		}
		
		addC2SCardGrp.enterApplicableFromDate(homePage.addDaysToCurrentDate(homePage.getDate(), -1));
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		addC2SCardGrp.clickSaveButton();

		String actual = addC2SCardGrp.getErrorMessage();

	
		arrayList.add(cardGroupName);

		dataMap.put("ACTUALMESSAGE", actual);
		dataMap.put("CARDGROUPNAME", cardGroupName);

		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

		return dataMap;
	}



	public Map<String, String> FixValueC2SCardGroupCreation(String serviceName, String subService) throws InterruptedException {
		final String methodname = "c2SCardGroupCreation";
		Log.methodEntry(methodname, serviceName, subService);
		
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();
		
		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		Log.info("Choice Recharge status is " +ChoiceRCPreferenceValue);

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();

		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subService);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);
		
		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m ;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
			crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
		}
		addC2SCardGrp.enterApplicableFromDate(homePage.getDate());
		addC2SCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		
		addC2SCardGrp.selectCardGroupSetType(PretupsI.CARDGRP_NORMAL_LOOKUPS);
		addC2SCardGrp.clickSaveButton();
		addC2SCardGrp2.clickConfirmbutton();
		
		String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		String actual = addC2SCardGrp.getMessage();

		Assert.assertEquals(expected, actual);
		arrayList.add(cardGroupName);

		dataMap.put("ACTUALMESSAGE", actual);
		dataMap.put("CARDGROUPNAME", cardGroupName);
		dataMap.put("CARDGROUP_SETID", DBHandler.AccessHandler.getCardGroupSetID(cardGroupName));
		
		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());
		
		Log.methodExit(methodname);
		return dataMap;
	}

	

	public Map<String, String> c2sCardGroupOverLappingRangeValueValidation(Map<String, String> dataMap, String serviceName, String subservice) throws InterruptedException {

		Map<String, String> C2SCardGroup_Map = dataMap;
		Map<String, String> dataMap1 = new HashMap<String, String>();
		
		ChoiceRCPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHOICE_RECHARGE_STATUS);
		System.out.println("Choice Recharge status is " +ChoiceRCPreferenceValue);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();

		if(ChoiceRCPreferenceValue != null && ChoiceRCPreferenceValue.equalsIgnoreCase("True"))
			Log.info("Card Groups may not be added due to Choice Recharge Functionality of no duplicate slabs for different Sub-service");
		else
			Log.info("Choice Recharge Status is False, Hence Card Group Creation will begin with similar slabs for different Sub-services");

		homePage.clickCardGroup();

		cardGrpCategories.clickAddC2SCardGroup();
		addC2SCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addC2SCardGrp.selectSubService(subservice);
		addC2SCardGrp.enterC2SCardGroupSetName(cardGroupName);

		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");
		
		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 2; m++) {
			int n = m + 1;
			addC2SCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails_OverlapRange(C2SCardGroup_Map, array, m, n, cardGroupName);
		}
		
	
		return dataMap;

	}

	public String viewC2SCardGroup_Promo(String serviceName, String subservice, String cardGroupName) throws InterruptedException {

        Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_VIEW_ROLECODE);
        login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

        driver.switchTo().defaultContent();
        driver.switchTo().frame(0);
        networkPage.selectNetwork();

        homePage.clickCardGroup();
        cardGrpCategories.clickViewC2SCardGroup();
        viewC2SCardGroupPage.selectServiceType(serviceName);
        viewC2SCardGroupPage.selectC2SCardGroupSubService(subservice);
        viewC2SCardGroupPage.selectC2SCardGroupSetName(cardGroupName);
        viewC2SCardGroupPage.clickSubmitButton();

        viewC2SCardGroupPage2.clickSubmitButton();


        //String expected= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.view.heading");
        String actual = viewC2SCardGroupPage3.getMessage();    


        return actual;
 }

 public String c2sCardGroupModification_Promo(String serviceName, String subservice, String cardGroupName) throws InterruptedException{

        Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.C2S_CARD_GROUP_MODIFY_ROLECODE);
        login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


        driver.switchTo().defaultContent();
        driver.switchTo().frame(0);
        networkPage.selectNetwork();

        homePage.clickCardGroup();
        cardGrpCategories.clickModifyC2SCardGroup();

        modifyCardGrpPage1.selectServiceType(serviceName);
        modifyCardGrpPage1.selectSubService(subservice);
        modifyCardGrpPage1.selectSetName(cardGroupName);
        modifyCardGrpPage1.selectSetVersion(0);
        modifyCardGrpPage1.clickModifyButton();

        modifyCardGrpPage2.clickEditCardGroup();
        SwitchWindow.switchwindow(driver);
        String startRange= driver.findElement(By.name("startRange")).getAttribute("value");
        String endRange= driver.findElement(By.name("endRange")).getAttribute("value");

        int startRng=Integer.parseInt(startRange)*100;
        int endRng=Integer.parseInt(endRange)*100;

        modifyCardGrpPage3.clickSuspendBtn();    

        SwitchWindow.backwindow(driver);

        modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());

        modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime_1min());


        modifyCardGrpPage2.clicksaveBtn();

        Thread.sleep(5000);


        addC2SCardGrp2.clickConfirmbutton();

        //String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupc2sdetailsview.successeditmessage");
        String actual = modifyCardGrpPage1.getMessage();

        //Thread.sleep(arg0);

        CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());

        //Assert.assertEquals(expected, actual);

        String actualStatus= DBHandler.AccessHandler.getCardGroupStatus(cardGroupName, startRng, endRng);
        String expectedStatus="S";
        Assert.assertEquals(actualStatus, expectedStatus);

        Map<String, String> dataMap=new HashMap<String, String>();

     dataMap.put("ACTUAL_MESSAGE", actual);
        dataMap.put("STATUS", actualStatus);

        return actual;
 }

	

	
	
	
}
