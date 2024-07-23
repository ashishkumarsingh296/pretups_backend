package com.Features;

import java.util.ArrayList;
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
import com.pageobjects.networkadminpages.homepage.CardGroupSubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.p2pcardgroup.AddP2PCardGroupDetailsPage;
import com.pageobjects.networkadminpages.p2pcardgroup.AddP2PCardGroupDetailsPage2;
import com.pageobjects.networkadminpages.p2pcardgroup.CalculateP2Ptransfervaluepage1;
import com.pageobjects.networkadminpages.p2pcardgroup.DefaultP2PCardGroupPage;
import com.pageobjects.networkadminpages.p2pcardgroup.ModifyP2PCardGroupPage1;
import com.pageobjects.networkadminpages.p2pcardgroup.ModifyP2PCardGroupPage2;
import com.pageobjects.networkadminpages.p2pcardgroup.ModifyP2PCardGroupPage3;
import com.pageobjects.networkadminpages.p2pcardgroup.P2PCardGroupDetailsPage;
import com.pageobjects.networkadminpages.p2pcardgroup.P2PCardGroupStatusConfirmPage;
import com.pageobjects.networkadminpages.p2pcardgroup.P2PCardGroupStatusPage;
import com.pageobjects.networkadminpages.p2pcardgroup.P2Pcardgroupstatuspage1;
import com.pageobjects.networkadminpages.p2pcardgroup.ViewP2PCardGroupPage;
import com.pageobjects.networkadminpages.p2pcardgroup.ViewP2PCardGroupPage2;
import com.pageobjects.networkadminpages.p2pcardgroup.ViewP2PCardGroupPage3;
import com.pageobjects.superadminpages.VMS.AddVoucherDenomination;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;

public class P2PCardGroup extends BaseTest{

	public String cardGroupName;
	public WebDriver driver;
	public static int Client_CardGroupVer;
	public int P2P_CARDGROUP_Type;
	public static ArrayList<String> CardGroup_SetID = new ArrayList<String>();
	int Nation_Voucher;
	NetworkAdminHomePage homePage;
	CardGroupSubCategories cardGrpCategories;
	AddP2PCardGroupDetailsPage addP2PCardGrp;
	AddP2PCardGroupDetailsPage2 addP2PCardGrp2;
	P2PCardGroupDetailsPage P2PCardGroupDetailsPage;
	Login login;
	RandomGeneration randmGeneration;
	//QueryRepository queryRepo;
	ModifyP2PCardGroupPage1 modifyCardGrpPage1;
	ModifyP2PCardGroupPage2 modifyCardGrpPage2;
	ModifyP2PCardGroupPage3 modifyCardGrpPage3;
	//ModifyP2PCardGroupPage4 modifyCardGrpPage4;
	ViewP2PCardGroupPage viewP2PCardGroupPage;
	ViewP2PCardGroupPage2 viewP2PCardGroupPage2;
	P2PCardGroupStatusPage P2PCradGroupStatusPage;
	UserAccess userAccess;	
	SelectNetworkPage networkPage;
	ViewP2PCardGroupPage3 viewP2PCardGroupPage3;
	P2PCardGroupStatusPage P2PCardGroupStatusPage;
	P2Pcardgroupstatuspage1 P2Pcardgroupstatuspage1;
	P2PCardGroupStatusConfirmPage P2PCardGroupStatusConfirmPage;
	DefaultP2PCardGroupPage DefaultP2PCardGroupPage;
	RandomGeneration RandomGenerator;
	CacheUpdate CacheUpdate;
	AddVoucherDenomination addVoucherDenomination;
	CalculateP2Ptransfervaluepage1 calculateP2Ptransfervaluepage1;
	public P2PCardGroup(WebDriver driver) {
		this.driver = driver;
		// Initializing the Pages.
		homePage = new NetworkAdminHomePage(driver);
		cardGrpCategories = new CardGroupSubCategories(driver);
		addP2PCardGrp = new AddP2PCardGroupDetailsPage(driver);
		addP2PCardGrp2 = new AddP2PCardGroupDetailsPage2(driver);
		P2PCardGroupDetailsPage = new P2PCardGroupDetailsPage(driver);
		//modifyCardGrpPage4= new ModifyP2PCardGroupPage4(driver);
		login = new Login();
		randmGeneration = new RandomGeneration();	
		//QueryRepository queryRepo= new QueryRepository();

		modifyCardGrpPage1 = new ModifyP2PCardGroupPage1(driver);
		modifyCardGrpPage2 = new ModifyP2PCardGroupPage2(driver);
		modifyCardGrpPage3 = new ModifyP2PCardGroupPage3(driver);
		P2PCradGroupStatusPage = new P2PCardGroupStatusPage(driver);
		userAccess = new UserAccess();
		viewP2PCardGroupPage = new ViewP2PCardGroupPage(driver);
		viewP2PCardGroupPage2 = new ViewP2PCardGroupPage2(driver);
		viewP2PCardGroupPage3 = new ViewP2PCardGroupPage3(driver);
		calculateP2Ptransfervaluepage1=new CalculateP2Ptransfervaluepage1(driver);
		networkPage = new SelectNetworkPage(driver);
		P2PCardGroupStatusPage = new P2PCardGroupStatusPage(driver);
		P2Pcardgroupstatuspage1 = new P2Pcardgroupstatuspage1(driver);
		P2PCardGroupStatusConfirmPage = new P2PCardGroupStatusConfirmPage(driver);
		CacheUpdate = new CacheUpdate(driver);
		DefaultP2PCardGroupPage = new DefaultP2PCardGroupPage(driver);
		RandomGenerator = new RandomGeneration();

		Client_CardGroupVer = Integer.parseInt(_masterVO.getClientDetail("C2SCARDGROUP_VER"));
		P2P_CARDGROUP_Type = Integer.parseInt(_masterVO.getClientDetail("P2P_CARDGROUP_Type"));
		addVoucherDenomination = new AddVoucherDenomination(driver);
		Nation_Voucher = Integer.parseInt(_masterVO.getClientDetail("Nation_Voucher"));
	}

	public void enterCardGroupDetails(int array[], int m, int n, String cardGroupName) throws InterruptedException{
		P2PCardGroupDetailsPage P2PCardGroupDetailsPage = new P2PCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);

		if (Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.enterCardGroupCode(_masterVO.getProperty("CardGroupCode"));
		else {
			CardGroup_SetID = UniqueChecker.UC_CARDGROUPID(CardGroup_SetID);
			P2PCardGroupDetailsPage.enterCardGroupCode(CardGroup_SetID.get(CardGroup_SetID.size() - 1));
		}

		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.entercardName(cardGroupName);

		P2PCardGroupDetailsPage.enterStartRange(++array[m] + "");
		P2PCardGroupDetailsPage.enterEndRange(array[n] + "");

		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				P2PCardGroupDetailsPage.checkCOSRequired();
		}

		P2PCardGroupDetailsPage.selectValidityType(_masterVO.getProperty("ValidityType"));
		P2PCardGroupDetailsPage.enterValidityDays(_masterVO.getProperty("ValidityDays"));
		P2PCardGroupDetailsPage.enterGracePeriod(_masterVO.getProperty("GracePeriod"));
		P2PCardGroupDetailsPage.enterMultipleOf(_masterVO.getProperty("MultipleOf"));
		P2PCardGroupDetailsPage.checkOnline();
		P2PCardGroupDetailsPage.checkBoth();
		P2PCardGroupDetailsPage.selectSenderTax1Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterSenderTax1Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectSenderTax2Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterSenderTax2Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectSenderProcessingFeeType(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterSenderProcessingFeeRate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.enterSenderProcessingFeeMinAmount("1");
		P2PCardGroupDetailsPage.enterSenderProcessingFeeMaxAmount("2");
		P2PCardGroupDetailsPage.enterSenderConversionFactor(_masterVO.getProperty("ReceiverConversionFactor"));
		P2PCardGroupDetailsPage.selectReceiverTax1Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterReceiverTax1Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectReceiverTax2Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterReceiverTax2Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectReceiverProcessingFeeType(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterReceiverProcessingFeeRate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.enterReceiverProcessingFeeMinAmount("1");
		P2PCardGroupDetailsPage.enterReceiverProcessingFeeMaxAmount("2");
		P2PCardGroupDetailsPage.enterReceiverConversionFactor(_masterVO.getProperty("ReceiverConversionFactor"));
		P2PCardGroupDetailsPage.selectBonusType(_masterVO.getProperty("BonusType"));
		P2PCardGroupDetailsPage.enterBonusValue(_masterVO.getProperty("BonusValue"));
		P2PCardGroupDetailsPage.enterBonusValidity(_masterVO.getProperty("BonusValidity"));
		P2PCardGroupDetailsPage.enterBonusConversionFactor(_masterVO.getProperty("BonusConversionFactor"));
		P2PCardGroupDetailsPage.enterBonusValidityDays(_masterVO.getProperty("BonusValidityDays"));

		P2PCardGroupDetailsPage.clickAddButton();

		SwitchWindow.backwindow(driver); // BackWindow Handler

	}
	
	public void enterCardGroupDetailsVoucher(int array[], int m, int n, String cardGroupName,HashMap<String, String> initiateMap) throws InterruptedException{
		P2PCardGroupDetailsPage P2PCardGroupDetailsPage = new P2PCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);

		if (Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.enterCardGroupCode(_masterVO.getProperty("CardGroupCode"));
		else {
			CardGroup_SetID = UniqueChecker.UC_CARDGROUPID(CardGroup_SetID);
			P2PCardGroupDetailsPage.enterCardGroupCode(CardGroup_SetID.get(CardGroup_SetID.size() - 1));
		}

		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.entercardName(cardGroupName);

		/*P2PCardGroupDetailsPage.enterStartRange(++array[m] + "");
		P2PCardGroupDetailsPage.enterEndRange(array[n] + "");*/
		addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment("LC");
		}

		addVoucherDenomination.SelectMRP(initiateMap.get("denomination"));
		boolean flag = addVoucherDenomination.visibilityProfile();
		if(flag)
		addVoucherDenomination.SelectProductId(initiateMap.get("activeProfile"));
		
		
		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				P2PCardGroupDetailsPage.checkCOSRequired();
		}

		P2PCardGroupDetailsPage.selectValidityType(_masterVO.getProperty("ValidityType"));
		P2PCardGroupDetailsPage.enterValidityDays(_masterVO.getProperty("ValidityDays"));
		P2PCardGroupDetailsPage.enterGracePeriod(_masterVO.getProperty("GracePeriod"));
		//P2PCardGroupDetailsPage.enterMultipleOf(_masterVO.getProperty("MultipleOf"));
		P2PCardGroupDetailsPage.checkOnline();
		P2PCardGroupDetailsPage.checkBoth();
	/*	P2PCardGroupDetailsPage.selectSenderTax1Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterSenderTax1Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectSenderTax2Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterSenderTax2Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectSenderProcessingFeeType(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterSenderProcessingFeeRate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.enterSenderProcessingFeeMinAmount("1");
		P2PCardGroupDetailsPage.enterSenderProcessingFeeMaxAmount("2");
		P2PCardGroupDetailsPage.enterSenderConversionFactor(_masterVO.getProperty("ReceiverConversionFactor"));*/
		P2PCardGroupDetailsPage.selectReceiverTax1Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterReceiverTax1Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectReceiverTax2Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterReceiverTax2Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectReceiverProcessingFeeType(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterReceiverProcessingFeeRate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.enterReceiverProcessingFeeMinAmount("1");
		P2PCardGroupDetailsPage.enterReceiverProcessingFeeMaxAmount("2");
	//	P2PCardGroupDetailsPage.enterReceiverConversionFactor(_masterVO.getProperty("ReceiverConversionFactor"));
		P2PCardGroupDetailsPage.selectBonusType(_masterVO.getProperty("BonusType"));
		P2PCardGroupDetailsPage.enterBonusValue(_masterVO.getProperty("BonusValue"));
		P2PCardGroupDetailsPage.enterBonusValidity(_masterVO.getProperty("BonusValidity"));
		P2PCardGroupDetailsPage.enterBonusConversionFactor(_masterVO.getProperty("BonusConversionFactor"));
		//P2PCardGroupDetailsPage.enterBonusValidityDays(_masterVO.getProperty("BonusValidityDays"));

		P2PCardGroupDetailsPage.clickAddButton();

		SwitchWindow.backwindow(driver); // BackWindow Handler

	}
	
	
	public void enterCardGroupDetails(int array[], int m, int n, String cardGroupName, int cardgroupType) throws InterruptedException{
		P2PCardGroupDetailsPage P2PCardGroupDetailsPage = new P2PCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);

		if (Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.enterCardGroupCode(_masterVO.getProperty("CardGroupCode"));
		else {
			CardGroup_SetID = UniqueChecker.UC_CARDGROUPID(CardGroup_SetID);
			P2PCardGroupDetailsPage.enterCardGroupCode(CardGroup_SetID.get(CardGroup_SetID.size() - 1));
		}

		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.entercardName(cardGroupName);

		P2PCardGroupDetailsPage.enterStartRange(++array[m] + "");
		P2PCardGroupDetailsPage.enterEndRange(array[n] + "");

		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				P2PCardGroupDetailsPage.checkCOSRequired();
		}

		P2PCardGroupDetailsPage.selectValidityType(_masterVO.getProperty("ValidityType"));
		P2PCardGroupDetailsPage.enterValidityDays(_masterVO.getProperty("ValidityDays"));
		P2PCardGroupDetailsPage.enterGracePeriod(_masterVO.getProperty("GracePeriod"));
		P2PCardGroupDetailsPage.enterMultipleOf(_masterVO.getProperty("MultipleOf"));
		P2PCardGroupDetailsPage.checkOnline();
		P2PCardGroupDetailsPage.checkBoth();
		
		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			P2PCardGroupDetailsPage.selectCardGroupType(cardgroupType);
		
		P2PCardGroupDetailsPage.selectSenderTax1Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterSenderTax1Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectSenderTax2Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterSenderTax2Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectSenderProcessingFeeType(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterSenderProcessingFeeRate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.enterSenderProcessingFeeMinAmount("1");
		P2PCardGroupDetailsPage.enterSenderProcessingFeeMaxAmount("2");
		P2PCardGroupDetailsPage.enterSenderConversionFactor(_masterVO.getProperty("ReceiverConversionFactor"));
		P2PCardGroupDetailsPage.selectReceiverTax1Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterReceiverTax1Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectReceiverTax2Type(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterReceiverTax2Rate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.selectReceiverProcessingFeeType(_masterVO.getProperty("TaxType"));
		P2PCardGroupDetailsPage.enterReceiverProcessingFeeRate(_masterVO.getProperty("TaxRate"));
		P2PCardGroupDetailsPage.enterReceiverProcessingFeeMinAmount("1");
		P2PCardGroupDetailsPage.enterReceiverProcessingFeeMaxAmount("2");
		P2PCardGroupDetailsPage.enterReceiverConversionFactor(_masterVO.getProperty("ReceiverConversionFactor"));
		P2PCardGroupDetailsPage.selectBonusType(_masterVO.getProperty("BonusType"));
		P2PCardGroupDetailsPage.enterBonusValue(_masterVO.getProperty("BonusValue"));
		P2PCardGroupDetailsPage.enterBonusValidity(_masterVO.getProperty("BonusValidity"));
		P2PCardGroupDetailsPage.enterBonusConversionFactor(_masterVO.getProperty("BonusConversionFactor"));
		P2PCardGroupDetailsPage.enterBonusValidityDays(_masterVO.getProperty("BonusValidityDays"));

		P2PCardGroupDetailsPage.clickAddButton();

		SwitchWindow.backwindow(driver); // BackWindow Handler

	}

	
	public void enterCardGroupDetails_usingMap(Map<String,String> datamap,int array[], int m, int n, String cardGroupName) throws InterruptedException{
		P2PCardGroupDetailsPage p2pCardGroupDetailsPage = new P2PCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);
		Map<String, String> P2PCardGroup_Map = datamap;

		if (Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.enterCardGroupCode(_masterVO.getProperty("CardGroupCode"));
		else {
			CardGroup_SetID = UniqueChecker.UC_CARDGROUPID(CardGroup_SetID);
			P2PCardGroupDetailsPage.enterCardGroupCode(CardGroup_SetID.get(CardGroup_SetID.size() - 1));
		}

		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.entercardName(cardGroupName);

		P2PCardGroupDetailsPage.enterStartRange(++array[m] + "");
		P2PCardGroupDetailsPage.enterEndRange(array[n] + "");

		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				P2PCardGroupDetailsPage.checkCOSRequired();
		}
		
		//p2pCardGroupDetailsPage.selectValidityType(P2PCardGroup_Map.get("ValidityType"));
		p2pCardGroupDetailsPage.enterValidityDays(P2PCardGroup_Map.get("ValidityDays"));
		p2pCardGroupDetailsPage.enterGracePeriod(P2PCardGroup_Map.get("GracePeriod"));
		p2pCardGroupDetailsPage.enterMultipleOf(P2PCardGroup_Map.get("MultipleOf"));
		p2pCardGroupDetailsPage.checkOnline();
		p2pCardGroupDetailsPage.checkBoth();
		p2pCardGroupDetailsPage.selectSenderTax1Type(P2PCardGroup_Map.get("Tax1Type"));
		p2pCardGroupDetailsPage.enterSenderTax1Rate(P2PCardGroup_Map.get("Tax1Rate"));
		p2pCardGroupDetailsPage.selectSenderTax2Type(P2PCardGroup_Map.get("Tax2Type"));
		p2pCardGroupDetailsPage.enterSenderTax2Rate(P2PCardGroup_Map.get("Tax2Rate"));
		p2pCardGroupDetailsPage.selectSenderProcessingFeeType(P2PCardGroup_Map.get("ProcessingFeeType"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeRate(P2PCardGroup_Map.get("ProcessingFeeRate"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeMinAmount(P2PCardGroup_Map.get("ProcessingFeeMinAmount"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeMaxAmount(P2PCardGroup_Map.get("ProcessingFeeMaxAmount"));
		p2pCardGroupDetailsPage.enterSenderConversionFactor(P2PCardGroup_Map.get("SenderConversionFactor"));
		p2pCardGroupDetailsPage.selectReceiverTax1Type(P2PCardGroup_Map.get("RecTax1Type"));
		p2pCardGroupDetailsPage.enterReceiverTax1Rate(P2PCardGroup_Map.get("RecTax1Rate"));
		p2pCardGroupDetailsPage.selectReceiverTax2Type(P2PCardGroup_Map.get("RecTax2Type"));
		p2pCardGroupDetailsPage.enterReceiverTax2Rate(P2PCardGroup_Map.get("RecTax2Rate"));
		p2pCardGroupDetailsPage.selectReceiverProcessingFeeType(P2PCardGroup_Map.get("ProcessingFeeType"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeRate(P2PCardGroup_Map.get("ProcessingFeeRate"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeMinAmount(P2PCardGroup_Map.get("ReceiverProcessingFeeMinAmount"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeMaxAmount(P2PCardGroup_Map.get("ReceiverProcessingFeeMaxAmount"));
		p2pCardGroupDetailsPage.enterReceiverConversionFactor(P2PCardGroup_Map.get("ReceiverConversionFactor"));
		p2pCardGroupDetailsPage.selectBonusType(P2PCardGroup_Map.get("BonusType"));
		p2pCardGroupDetailsPage.enterBonusValue(P2PCardGroup_Map.get("BonusValue"));
		p2pCardGroupDetailsPage.enterBonusValidity(P2PCardGroup_Map.get("BonusValidity"));
		p2pCardGroupDetailsPage.enterBonusConversionFactor(P2PCardGroup_Map.get("BonusConversionFactor"));
		p2pCardGroupDetailsPage.enterBonusValidityDays(P2PCardGroup_Map.get("BonusValidityDays"));

		p2pCardGroupDetailsPage.clickAddButton();
		P2PCardGroup_Map.put("ACTUAL",CONSTANT.CARDGROUP_SLAB_ERR);
		
		SwitchWindow.backwindow(driver); // BackWindow Handler

	}

	
	public void enterCardGroupDetails_usingMapVoucher(Map<String,String> datamap,int array[], int m, int n, String cardGroupName,HashMap<String, String> initiateMap) throws InterruptedException{
		P2PCardGroupDetailsPage p2pCardGroupDetailsPage = new P2PCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);
		Map<String, String> P2PCardGroup_Map = datamap;

		if (Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.enterCardGroupCode(_masterVO.getProperty("CardGroupCode"));
		else {
			CardGroup_SetID = UniqueChecker.UC_CARDGROUPID(CardGroup_SetID);
			P2PCardGroupDetailsPage.enterCardGroupCode(CardGroup_SetID.get(CardGroup_SetID.size() - 1));
		}

		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.entercardName(cardGroupName);

		/*P2PCardGroupDetailsPage.enterStartRange(++array[m] + "");
		P2PCardGroupDetailsPage.enterEndRange(array[n] + "");*/
		addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment("LC");
		}

		addVoucherDenomination.SelectMRP(initiateMap.get("denomination"));
		boolean flag = addVoucherDenomination.visibilityProfile();
		if(flag)
		addVoucherDenomination.SelectProductId(initiateMap.get("activeProfile"));
		

		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				P2PCardGroupDetailsPage.checkCOSRequired();
		}
		
		//p2pCardGroupDetailsPage.selectValidityType(P2PCardGroup_Map.get("ValidityType"));
		p2pCardGroupDetailsPage.enterValidityDays(P2PCardGroup_Map.get("ValidityDays"));
		p2pCardGroupDetailsPage.enterGracePeriod(P2PCardGroup_Map.get("GracePeriod"));
		//p2pCardGroupDetailsPage.enterMultipleOf(P2PCardGroup_Map.get("MultipleOf"));
		p2pCardGroupDetailsPage.checkOnline();
		p2pCardGroupDetailsPage.checkBoth();
		/*p2pCardGroupDetailsPage.selectSenderTax1Type(P2PCardGroup_Map.get("Tax1Type"));
		p2pCardGroupDetailsPage.enterSenderTax1Rate(P2PCardGroup_Map.get("Tax1Rate"));
		p2pCardGroupDetailsPage.selectSenderTax2Type(P2PCardGroup_Map.get("Tax2Type"));
		p2pCardGroupDetailsPage.enterSenderTax2Rate(P2PCardGroup_Map.get("Tax2Rate"));
		p2pCardGroupDetailsPage.selectSenderProcessingFeeType(P2PCardGroup_Map.get("ProcessingFeeType"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeRate(P2PCardGroup_Map.get("ProcessingFeeRate"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeMinAmount(P2PCardGroup_Map.get("ProcessingFeeMinAmount"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeMaxAmount(P2PCardGroup_Map.get("ProcessingFeeMaxAmount"));
		p2pCardGroupDetailsPage.enterSenderConversionFactor(P2PCardGroup_Map.get("SenderConversionFactor"));*/
		p2pCardGroupDetailsPage.selectReceiverTax1Type(P2PCardGroup_Map.get("RecTax1Type"));
		p2pCardGroupDetailsPage.enterReceiverTax1Rate(P2PCardGroup_Map.get("RecTax1Rate"));
		p2pCardGroupDetailsPage.selectReceiverTax2Type(P2PCardGroup_Map.get("RecTax2Type"));
		p2pCardGroupDetailsPage.enterReceiverTax2Rate(P2PCardGroup_Map.get("RecTax2Rate"));
		p2pCardGroupDetailsPage.selectReceiverProcessingFeeType(P2PCardGroup_Map.get("ProcessingFeeType"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeRate(P2PCardGroup_Map.get("ProcessingFeeRate"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeMinAmount(P2PCardGroup_Map.get("ReceiverProcessingFeeMinAmount"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeMaxAmount(P2PCardGroup_Map.get("ReceiverProcessingFeeMaxAmount"));
		//p2pCardGroupDetailsPage.enterReceiverConversionFactor(P2PCardGroup_Map.get("ReceiverConversionFactor"));
		p2pCardGroupDetailsPage.selectBonusType(P2PCardGroup_Map.get("BonusType"));
		p2pCardGroupDetailsPage.enterBonusValue(P2PCardGroup_Map.get("BonusValue"));
		p2pCardGroupDetailsPage.enterBonusValidity(P2PCardGroup_Map.get("BonusValidity"));
		p2pCardGroupDetailsPage.enterBonusConversionFactor(P2PCardGroup_Map.get("BonusConversionFactor"));
		//p2pCardGroupDetailsPage.enterBonusValidityDays(P2PCardGroup_Map.get("BonusValidityDays"));

		p2pCardGroupDetailsPage.clickAddButton();
		P2PCardGroup_Map.put("ACTUAL",CONSTANT.CARDGROUP_SLAB_ERR);
		
		SwitchWindow.backwindow(driver); // BackWindow Handler

	}
	
	
	public void enterCardGroupDetails_Overlap(Map<String,String> datamap,int array[], int m, int n, String cardGroupName) throws InterruptedException{
		P2PCardGroupDetailsPage p2pCardGroupDetailsPage = new P2PCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);
		Map<String, String> P2PCardGroup_Map = datamap;

		if (Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.enterCardGroupCode(_masterVO.getProperty("CardGroupCode"));
		else {
			CardGroup_SetID = UniqueChecker.UC_CARDGROUPID(CardGroup_SetID);
			P2PCardGroupDetailsPage.enterCardGroupCode(CardGroup_SetID.get(CardGroup_SetID.size() - 1));
		}

		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.entercardName(cardGroupName);

		P2PCardGroupDetailsPage.enterStartRange(array[m] + "");
		P2PCardGroupDetailsPage.enterEndRange(array[n] + "");

		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				P2PCardGroupDetailsPage.checkCOSRequired();
		}
		
		//p2pCardGroupDetailsPage.selectValidityType(P2PCardGroup_Map.get("ValidityType"));
		p2pCardGroupDetailsPage.enterValidityDays(P2PCardGroup_Map.get("ValidityDays"));
		p2pCardGroupDetailsPage.enterGracePeriod(P2PCardGroup_Map.get("GracePeriod"));
		p2pCardGroupDetailsPage.enterMultipleOf(P2PCardGroup_Map.get("MultipleOf"));
		p2pCardGroupDetailsPage.checkOnline();
		p2pCardGroupDetailsPage.checkBoth();
		p2pCardGroupDetailsPage.selectSenderTax1Type(P2PCardGroup_Map.get("Tax1Type"));
		p2pCardGroupDetailsPage.enterSenderTax1Rate(P2PCardGroup_Map.get("Tax1Rate"));
		p2pCardGroupDetailsPage.selectSenderTax2Type(P2PCardGroup_Map.get("Tax2Type"));
		p2pCardGroupDetailsPage.enterSenderTax2Rate(P2PCardGroup_Map.get("Tax2Rate"));
		p2pCardGroupDetailsPage.selectSenderProcessingFeeType(P2PCardGroup_Map.get("ProcessingFeeType"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeRate(P2PCardGroup_Map.get("ProcessingFeeRate"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeMinAmount(P2PCardGroup_Map.get("ProcessingFeeMinAmount"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeMaxAmount(P2PCardGroup_Map.get("ProcessingFeeMaxAmount"));
		p2pCardGroupDetailsPage.enterSenderConversionFactor(P2PCardGroup_Map.get("SenderConversionFactor"));
		p2pCardGroupDetailsPage.selectReceiverTax1Type(P2PCardGroup_Map.get("RecTax1Type"));
		p2pCardGroupDetailsPage.enterReceiverTax1Rate(P2PCardGroup_Map.get("RecTax1Rate"));
		p2pCardGroupDetailsPage.selectReceiverTax2Type(P2PCardGroup_Map.get("RecTax2Type"));
		p2pCardGroupDetailsPage.enterReceiverTax2Rate(P2PCardGroup_Map.get("RecTax2Rate"));
		p2pCardGroupDetailsPage.selectReceiverProcessingFeeType(P2PCardGroup_Map.get("ProcessingFeeType"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeRate(P2PCardGroup_Map.get("ProcessingFeeRate"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeMinAmount(P2PCardGroup_Map.get("ReceiverProcessingFeeMinAmount"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeMaxAmount(P2PCardGroup_Map.get("ReceiverProcessingFeeMaxAmount"));
		p2pCardGroupDetailsPage.enterReceiverConversionFactor(P2PCardGroup_Map.get("ReceiverConversionFactor"));
		p2pCardGroupDetailsPage.selectBonusType(P2PCardGroup_Map.get("BonusType"));
		p2pCardGroupDetailsPage.enterBonusValue(P2PCardGroup_Map.get("BonusValue"));
		p2pCardGroupDetailsPage.enterBonusValidity(P2PCardGroup_Map.get("BonusValidity"));
		p2pCardGroupDetailsPage.enterBonusConversionFactor(P2PCardGroup_Map.get("BonusConversionFactor"));
		p2pCardGroupDetailsPage.enterBonusValidityDays(P2PCardGroup_Map.get("BonusValidityDays"));

		p2pCardGroupDetailsPage.clickAddButton();
		P2PCardGroup_Map.put("ACTUAL",CONSTANT.CARDGROUP_SLAB_ERR);
		
		SwitchWindow.backwindow(driver); // BackWindow Handler

	}

	
	public void enterCardGroupDetails_OverlapVoucher(Map<String,String> datamap,int array[], int m, int n, String cardGroupName,HashMap<String, String> initiateMap) throws InterruptedException{
		P2PCardGroupDetailsPage p2pCardGroupDetailsPage = new P2PCardGroupDetailsPage(driver);
		SwitchWindow.switchwindow(driver);
		Map<String, String> P2PCardGroup_Map = datamap;

		if (Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.enterCardGroupCode(_masterVO.getProperty("CardGroupCode"));
		else {
			CardGroup_SetID = UniqueChecker.UC_CARDGROUPID(CardGroup_SetID);
			P2PCardGroupDetailsPage.enterCardGroupCode(CardGroup_SetID.get(CardGroup_SetID.size() - 1));
		}

		if (Client_CardGroupVer == 1 || Client_CardGroupVer == 2)
			P2PCardGroupDetailsPage.entercardName(cardGroupName);

		/*P2PCardGroupDetailsPage.enterStartRange(++array[m] + "");
		P2PCardGroupDetailsPage.enterEndRange(array[n] + "");*/
		addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment("LC");
		}

		addVoucherDenomination.SelectMRP(initiateMap.get("denomination"));
		boolean flag = addVoucherDenomination.visibilityProfile();
		if(flag)
		addVoucherDenomination.SelectProductId(initiateMap.get("activeProfile"));

		if (Client_CardGroupVer == 2) {
			String COS_REQUIRED = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "COS_REQUIRED");
			if (!COS_REQUIRED.isEmpty() && COS_REQUIRED.equalsIgnoreCase("true"))
				P2PCardGroupDetailsPage.checkCOSRequired();
		}
		
		//p2pCardGroupDetailsPage.selectValidityType(P2PCardGroup_Map.get("ValidityType"));
		p2pCardGroupDetailsPage.enterValidityDays(P2PCardGroup_Map.get("ValidityDays"));
		p2pCardGroupDetailsPage.enterGracePeriod(P2PCardGroup_Map.get("GracePeriod"));
		//p2pCardGroupDetailsPage.enterMultipleOf(P2PCardGroup_Map.get("MultipleOf"));
		p2pCardGroupDetailsPage.checkOnline();
		p2pCardGroupDetailsPage.checkBoth();
		/*p2pCardGroupDetailsPage.selectSenderTax1Type(P2PCardGroup_Map.get("Tax1Type"));
		p2pCardGroupDetailsPage.enterSenderTax1Rate(P2PCardGroup_Map.get("Tax1Rate"));
		p2pCardGroupDetailsPage.selectSenderTax2Type(P2PCardGroup_Map.get("Tax2Type"));
		p2pCardGroupDetailsPage.enterSenderTax2Rate(P2PCardGroup_Map.get("Tax2Rate"));
		p2pCardGroupDetailsPage.selectSenderProcessingFeeType(P2PCardGroup_Map.get("ProcessingFeeType"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeRate(P2PCardGroup_Map.get("ProcessingFeeRate"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeMinAmount(P2PCardGroup_Map.get("ProcessingFeeMinAmount"));
		p2pCardGroupDetailsPage.enterSenderProcessingFeeMaxAmount(P2PCardGroup_Map.get("ProcessingFeeMaxAmount"));
		p2pCardGroupDetailsPage.enterSenderConversionFactor(P2PCardGroup_Map.get("SenderConversionFactor"));*/
		p2pCardGroupDetailsPage.selectReceiverTax1Type(P2PCardGroup_Map.get("RecTax1Type"));
		p2pCardGroupDetailsPage.enterReceiverTax1Rate(P2PCardGroup_Map.get("RecTax1Rate"));
		p2pCardGroupDetailsPage.selectReceiverTax2Type(P2PCardGroup_Map.get("RecTax2Type"));
		p2pCardGroupDetailsPage.enterReceiverTax2Rate(P2PCardGroup_Map.get("RecTax2Rate"));
		p2pCardGroupDetailsPage.selectReceiverProcessingFeeType(P2PCardGroup_Map.get("ProcessingFeeType"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeRate(P2PCardGroup_Map.get("ProcessingFeeRate"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeMinAmount(P2PCardGroup_Map.get("ReceiverProcessingFeeMinAmount"));
		p2pCardGroupDetailsPage.enterReceiverProcessingFeeMaxAmount(P2PCardGroup_Map.get("ReceiverProcessingFeeMaxAmount"));
		//p2pCardGroupDetailsPage.enterReceiverConversionFactor(P2PCardGroup_Map.get("ReceiverConversionFactor"));
		p2pCardGroupDetailsPage.selectBonusType(P2PCardGroup_Map.get("BonusType"));
		p2pCardGroupDetailsPage.enterBonusValue(P2PCardGroup_Map.get("BonusValue"));
		p2pCardGroupDetailsPage.enterBonusValidity(P2PCardGroup_Map.get("BonusValidity"));
		p2pCardGroupDetailsPage.enterBonusConversionFactor(P2PCardGroup_Map.get("BonusConversionFactor"));
		//p2pCardGroupDetailsPage.enterBonusValidityDays(P2PCardGroup_Map.get("BonusValidityDays"));

		p2pCardGroupDetailsPage.clickAddButton();
		P2PCardGroup_Map.put("ACTUAL",CONSTANT.CARDGROUP_SLAB_ERR);
		
		SwitchWindow.backwindow(driver); // BackWindow Handler

	}


	public Map<String, String> P2PCardGroupCreation(String serviceName, String subService) throws InterruptedException {
		final String methodname = "P2PCardGroupCreation";
		Log.methodEntry(methodname, serviceName, subService);

		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		if (serviceName!=null &&subService !=null){

			//ArrayList<String> userInfo= UserAccess.getUserWithAccess("ADDP2PCARDGRP");
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_CREATION_ROLECODE);
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
			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			cardGroupName = UniqueChecker.UC_CardGroupName();
			//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			addP2PCardGrp.selectSubService(subService);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);
			System.out.println(cardGroupName);


			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 3; m++) {
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails(array, m, n, cardGroupName);
			}
			addP2PCardGrp.enterDateFromDatePicker();
			addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
			if(P2P_CARDGROUP_Type == 1)
				addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
			addP2PCardGrp.clickSaveButton();
			addP2PCardGrp.clickSaveButton();
			addP2PCardGrp2.clickConfirmbutton();

			// Asserting message on confirmation.
			//String expected = "Card group details successfully added";
			String expected= _masterVO.getMessage("cardgroup.cardgroupdetailsview.successaddmessage");
			String actual = addP2PCardGrp.getMessage();

			//Assert.assertEquals(expected, actual);
			arrayList.add(cardGroupName);


			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);
			dataMap.put("CARDGROUP_SETID", DBHandler.AccessHandler.getCardGroupSetID(cardGroupName));

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());
		} else {
			//Log.info("The service and Sub service recieved as null");
			currentNode.skip("The service and Sub service recieved as null");
		}

		Log.methodExit(methodname);
		return dataMap;
	}

	public Map<String, String> P2PCardGroupCreationVoucher(String serviceName, String subService,HashMap<String, String> initiateMap) throws InterruptedException {
		final String methodname = "P2PCardGroupCreation";
		Log.methodEntry(methodname, serviceName, subService);

		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		if (serviceName!=null &&subService !=null){

			//ArrayList<String> userInfo= UserAccess.getUserWithAccess("ADDP2PCARDGRP");
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
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
			cardGrpCategories.clickAddP2PVoucherCardGroup();
			addP2PCardGrp.selectServiceTypeVoucher(serviceName);
			cardGroupName = UniqueChecker.UC_CardGroupName();
			//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
			/*cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);*/
			addP2PCardGrp.selectSubService(subService);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);
			System.out.println(cardGroupName);


			// Filling multiple slabs for different ranges.
			//for (int m = 0; m < 3; m++) {
				
				int m = 0;
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetailsVoucher(array, m, n, cardGroupName,initiateMap);
		//	}
				dataMap.put("datetime", homePage.getApplicableFromTime());
			addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
			addP2PCardGrp.enterApplicableFromHour(dataMap.get("datetime"));
			if(P2P_CARDGROUP_Type == 1)
				addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
			addP2PCardGrp.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();

			// Asserting message on confirmation.
			//String expected = "Card group details successfully added";
			String expected= _masterVO.getMessage("cardgroup.cardgroupdetailsview.successaddmessage");
			String actual = addP2PCardGrp.getMessage();

			//Assert.assertEquals(expected, actual);
			arrayList.add(cardGroupName);


			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);
			dataMap.put("CARDGROUP_SETID", DBHandler.AccessHandler.getCardGroupSetID(cardGroupName));

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());
		} else {
			//Log.info("The service and Sub service recieved as null");
			currentNode.skip("The service and Sub service recieved as null");
		}

		Log.methodExit(methodname);
		return dataMap;
	}
	
	
	public Map<String, String> P2PCardGroupFutureDate(String serviceName, String subService) throws InterruptedException {
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();


		if (serviceName!=null &&subService !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_CREATION_ROLECODE);
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
			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			cardGroupName = UniqueChecker.UC_CardGroupName();
			//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			addP2PCardGrp.selectSubService(subService);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);


			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 3; m++) {
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails(array, m, n, cardGroupName);
			}
			addP2PCardGrp.enterApplicableFromDate(homePage.addDaysToCurrentDate(homePage.getDate(), 1));
			System.out.println(homePage.addDaysToCurrentDate(homePage.getDate(), 1));
			addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
			if(P2P_CARDGROUP_Type == 1)
				addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
			addP2PCardGrp.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();

			// Asserting message on confirmation.
			//String expected = "Card group details successfully added";

			String actual = addP2PCardGrp.getMessage();

			//Assert.assertEquals(expected, actual);
			arrayList.add(cardGroupName);


			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}

		else{
			currentNode.skip("The service and Sub service recieved as null");	
		}

		return dataMap;
	}
	
	public Map<String, String> P2PCardGroupFutureDateVoucher(String serviceName, String subService,HashMap<String, String> initiateMap) throws InterruptedException {
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();


		if (serviceName!=null &&subService !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
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
			cardGrpCategories.clickAddP2PVoucherCardGroup();
			addP2PCardGrp.selectServiceTypeVoucher(serviceName);
			cardGroupName = UniqueChecker.UC_CardGroupName();
			//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
			cardGrpCategories.clickAddP2PVoucherCardGroup();
			addP2PCardGrp.selectServiceTypeVoucher(serviceName);
			addP2PCardGrp.selectSubService(subService);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);


			// Filling multiple slabs for different ranges.
		//for (int m = 0; m < 3; m++) {
			int m=0;
			int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetailsVoucher(array, m, n, cardGroupName,initiateMap);
	//		}
			addP2PCardGrp.enterApplicableFromDate(homePage.addDaysToCurrentDate(homePage.getDate(), 1));
			System.out.println(homePage.addDaysToCurrentDate(homePage.getDate(), 1));
			addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
			if(P2P_CARDGROUP_Type == 1)
				addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
			addP2PCardGrp.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();

			// Asserting message on confirmation.
			//String expected = "Card group details successfully added";

			String actual = addP2PCardGrp.getMessage();

			//Assert.assertEquals(expected, actual);
			arrayList.add(cardGroupName);


			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}

		else{
			currentNode.skip("The service and Sub service recieved as null");	
		}

		return dataMap;
	}


	public String P2PCardGroupDeletion(String serviceName, String subservice, String cardGroupName) throws InterruptedException {

		//ArrayList<String> userInfo= UserAccess.getUserWithAccess("EDITP2PCARDGRP");
		//login.UserLogin(driver, "Operator", userInfo.get(0), userInfo.get(1));

		String actual = null;


		if (serviceName!=null &&subservice !=null){


			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_MODIFY_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			networkPage.selectNetwork();

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);

			homePage.clickCardGroup();
			cardGrpCategories.clickModifyP2PCardGroup();

			modifyCardGrpPage1.selectServiceType(serviceName);
			modifyCardGrpPage1.selectSubService(subservice);
			modifyCardGrpPage1.selectSetName(cardGroupName);
			modifyCardGrpPage1.selectSetVersion(0);
			modifyCardGrpPage1.clickDeleteButton();

			Alert alert = driver.switchTo().alert();
			alert.accept();

			//String expected="Card group details successfully deleted";
			//String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupdetailsview.successdeletemessage");
			
			if(modifyCardGrpPage1.getMessagestatus())
				actual=modifyCardGrpPage1.getMessage();
			else
				actual=modifyCardGrpPage1.getErrorMessage();
			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());
		}

		else{
			currentNode.skip("The service and Sub service recieved as null");
		}

		return actual;
		//Assert.assertEquals(actual, expected);
	}
	
	public String P2PCardGroupDeletionVoucher(String serviceName, String subservice, String cardGroupName) throws InterruptedException {

		//ArrayList<String> userInfo= UserAccess.getUserWithAccess("EDITP2PCARDGRP");
		//login.UserLogin(driver, "Operator", userInfo.get(0), userInfo.get(1));

		String actual = null;


		if (serviceName!=null &&subservice !=null){


			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_MODIFY_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			networkPage.selectNetwork();

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);

			homePage.clickCardGroup();
			cardGrpCategories.clickModifyVoucherCardGroup();

		//	modifyCardGrpPage1.selectServiceType(serviceName);
			modifyCardGrpPage1.selectSubService(subservice);
			modifyCardGrpPage1.selectSetName(cardGroupName);
			modifyCardGrpPage1.selectSetVersion(0);
			modifyCardGrpPage1.clickDeleteButton();

			Alert alert = driver.switchTo().alert();
			alert.accept();

			//String expected="Card group details successfully deleted";
			//String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupdetailsview.successdeletemessage");

			actual=modifyCardGrpPage1.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());
		}

		else{
			currentNode.skip("The service and Sub service recieved as null");
		}

		return actual;
		//Assert.assertEquals(actual, expected);
	}

	public String P2PCardGroupModification_AddNewSlab(String serviceName, String subservice, String cardGroupName) throws InterruptedException{

		String actual=null;

		if (serviceName!=null &&subservice !=null){

			// Initializing Slab Definition
			ArrayList<String> arrayList = new ArrayList<String>();
			//int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
			//int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
			//int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
			int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
			int Slab5 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab5"));

			int array[] = { Slab4 , Slab5 };

			// Login as a Network Admin. Click Card Group.
			//ArrayList<String> userInfo= UserAccess.getUserWithAccess("EDITP2PCARDGRP");
			//login.UserLogin(driver, "Operator", userInfo.get(0), userInfo.get(1));
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_MODIFY_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);

			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickModifyP2PCardGroup();

			modifyCardGrpPage1.selectServiceType(serviceName);
			modifyCardGrpPage1.selectSubService(subservice);
			modifyCardGrpPage1.selectSetName(cardGroupName);
			System.out.println(cardGroupName);
			modifyCardGrpPage1.selectSetVersion(0);
			modifyCardGrpPage1.clickModifyButton();

			//modifyCardGrpPage2.clickAddCardGroupList();

			// Filling new slab for different ranges.
			for (int m = 0; m < 1; m++) {
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails(array, m, n, cardGroupName);
			}

			Thread.sleep(2000);
			addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
			addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());

			addP2PCardGrp.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();

			// Asserting message on confirmation.

			String expected= _masterVO.getMessage("cardgroup.cardgroupP2Pdetailsview.successeditmessage");
			actual = modifyCardGrpPage1.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}

		else{
			currentNode.skip("The service and Sub service recieved as null");
		}


		return actual;
	}
	
	public String P2PCardGroupModification_AddNewSlabVoucher(String serviceName, String subservice, String cardGroupName,HashMap<String, String> initiateMap) throws InterruptedException{

		String actual=null;

		if (serviceName!=null &&subservice !=null){

			// Initializing Slab Definition
			ArrayList<String> arrayList = new ArrayList<String>();
			//int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
			//int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
			//int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
			int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
			int Slab5 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab5"));

			int array[] = { Slab4 , Slab5 };

			// Login as a Network Admin. Click Card Group.
			//ArrayList<String> userInfo= UserAccess.getUserWithAccess("EDITP2PCARDGRP");
			//login.UserLogin(driver, "Operator", userInfo.get(0), userInfo.get(1));
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_MODIFY_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);

			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickModifyVoucherCardGroup();

			//modifyCardGrpPage1.selectServiceType(serviceName);
			modifyCardGrpPage1.selectSubService(subservice);
			modifyCardGrpPage1.selectSetName(cardGroupName);
			System.out.println(cardGroupName);
			modifyCardGrpPage1.selectSetVersion(0);
			modifyCardGrpPage1.clickModifyButton();

			modifyCardGrpPage2.clickAddCardGroupList();

			// Filling new slab for different ranges.
		//	for (int m = 0; m < 1; m++) {
			int m=0;
			int n = m + 1;
				enterCardGroupDetailsVoucher(array, m, n, cardGroupName,initiateMap);
		//	}

			Thread.sleep(2000);
			addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
			addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());

			addP2PCardGrp.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();

			// Asserting message on confirmation.

			String expected= _masterVO.getMessage("cardgroup.cardgroupP2Pdetailsview.successeditmessage");
			actual = modifyCardGrpPage1.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}

		else{
			currentNode.skip("The service and Sub service recieved as null");
		}


		return actual;
	}

	public void writeCardGroupToExcel(String CardGroupName, String CardGroupSetID, int rowNum){

		if (CardGroupName!=null ){
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
			ExcelUtility.setCellData(0, ExcelI.CARDGROUP_NAME, rowNum, CardGroupName);
			ExcelUtility.setCellData(0, ExcelI.CARDGROUP_SETID, rowNum, CardGroupSetID);
		}

		else{
			currentNode.skip("The cardGroupName recieved as null");
		}

	}
	
	public void writeVoucherCardGroupToExcel(String CardGroupName, String CardGroupSetID, int rowNum){

		if (CardGroupName!=null ){
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);
			ExcelUtility.setCellData(0, ExcelI.CARDGROUP_NAME, rowNum, CardGroupName);
			ExcelUtility.setCellData(0, ExcelI.CARDGROUP_SETID, rowNum, CardGroupSetID);
		}

		else{
			currentNode.skip("The cardGroupName recieved as null");
		}

	}
	
	public String fetchCardGroup(int rownum)
	{
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		String cardGroup = ExcelUtility.getCellData(0, ExcelI.PROMO_CARDGROUP_NAME, rownum);
		return cardGroup;
	}
	
	public String P2PCardGroupModification_EditCardGroup(String serviceName, String subservice, String cardGroupName) throws InterruptedException{


		String actual=null;

		if (serviceName!=null &&subservice !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_MODIFY_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickModifyP2PCardGroup();

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

			addP2PCardGrp.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();

			// Asserting message on confirmation.

			String expected= _masterVO.getMessage("cardgroup.cardgroupP2Pdetailsview.successeditmessage");
			actual = modifyCardGrpPage1.getMessage();

			//Assert.assertEquals(expected, actual);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());
		}
		else{
			currentNode.skip("The service and Sub service recieved as null");
		}
		return actual;
	}
	
	public String P2PCardGroupModification_EditCardGroupVoucher(String serviceName, String subservice, String cardGroupName) throws InterruptedException{


		String actual=null;

		if (serviceName!=null &&subservice !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_MODIFY_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickModifyVoucherCardGroup();

		//	modifyCardGrpPage1.selectServiceType(serviceName);
			modifyCardGrpPage1.selectSubService(subservice);
			modifyCardGrpPage1.selectSetName(cardGroupName);
			modifyCardGrpPage1.selectSetVersion(0);
			modifyCardGrpPage1.clickModifyButton();
			modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());
			modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());
			//modifyCardGrpPage2.clickEditCardGroup();
			//SwitchWindow.switchwindow(driver);

			//modifyCardGrpPage3.clickAddBtn();

			addP2PCardGrp.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();

			// Asserting message on confirmation.

			String expected= _masterVO.getMessage("cardgroup.cardgroupP2Pdetailsview.successeditmessage");
			actual = modifyCardGrpPage1.getMessage();

			//Assert.assertEquals(expected, actual);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());
		}
		else{
			currentNode.skip("The service and Sub service recieved as null");
		}
		return actual;
	}



	public String P2PCardGroupModification_SuspendCardGroupSlab(String serviceName, String subservice, String cardGroupName) throws InterruptedException{

		// Login as a Network Admin. Click Card Group.

		String actual=null;

		if (serviceName!=null &&subservice !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_MODIFY_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickModifyP2PCardGroup();

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
			modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());


			modifyCardGrpPage2.clickSaveButton();

			Thread.sleep(5000);


			addP2PCardGrp2.clickConfirmbutton();






			//String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupP2Pdetailsview.successeditmessage");
			actual = modifyCardGrpPage1.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());



			String actualStatus= DBHandler.AccessHandler.getCardGroupStatus(cardGroupName, startRng, endRng);
			String expectedStatus="S";
			Assert.assertEquals(actualStatus, expectedStatus);

			Map<String, String> dataMap=new HashMap<String, String>();
			dataMap.put("ACTUAL_MESSAGE", actual);
			dataMap.put("STATUS", actualStatus);

		}
		else{
			currentNode.skip("The service and Sub service recieved as null");
		}

		return actual;
	}






	public String P2PCardGroupModification_ResumeCardGroupSlab(String serviceName, String subservice, String cardGroupName) throws InterruptedException{

		// Login as a Network Admin. Click Card Group.
		String actual=null;

		if (serviceName!=null &&subservice !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_MODIFY_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();

			cardGrpCategories.clickModifyP2PCardGroup();

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

			int startRng= (int)_parser.getSystemAmount(startRange);

			int endRng=(int)_parser.getSystemAmount(endRange);

			modifyCardGrpPage3.clickResumeBtn();	

			SwitchWindow.backwindow(driver);
			modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());
			modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());
			/*if(P2P_CARDGROUP_Type == 1)
			modifyCardGrpPage2.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));*/
			modifyCardGrpPage2.clickSaveButton();

			Thread.sleep(5000);

			addP2PCardGrp2.clickConfirmbutton();

			actual = modifyCardGrpPage1.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

			String actualStatus= DBHandler.AccessHandler.getCardGroupStatus(cardGroupName, startRng, endRng);
			String expectedStatus="Y";
			Assert.assertEquals(actualStatus, expectedStatus);

			Map<String, String> dataMap=new HashMap<String, String>();
			dataMap.put("ACTUAL_MESSAGE", actual);
			dataMap.put("STATUS", actualStatus);

		}
		else{
			currentNode.skip("The service and Sub service recieved as null");
		}

		return actual;

	}

	
	public String P2PCardGroupModification_ResumeCardGroupSlabVoucher(String serviceName, String subservice, String cardGroupName,HashMap<String, String> initiateMap) throws InterruptedException{

		// Login as a Network Admin. Click Card Group.
		String actual=null;

		if (serviceName!=null &&subservice !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_MODIFY_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();

			cardGrpCategories.clickModifyVoucherCardGroup();

			//modifyCardGrpPage1.selectServiceType(serviceName);
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
			/*String startRange= driver.findElement(By.name("startRange")).getAttribute("value");
			String endRange= driver.findElement(By.name("endRange")).getAttribute("value");

			int startRng= (int)_parser.getSystemAmount(startRange);

			int endRng=(int)_parser.getSystemAmount(endRange);*/

			String startRng = initiateMap.get("mrp");
			startRng += "00";
			int strtRange = Integer.parseInt(startRng);
			
			String endRng = initiateMap.get("mrp");
			endRng += "00";
			int endRange = Integer.parseInt(endRng);
			
			modifyCardGrpPage3.clickResumeBtn();	

			SwitchWindow.backwindow(driver);
			modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());
			modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());
			/*if(P2P_CARDGROUP_Type == 1)
			modifyCardGrpPage2.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));*/
			modifyCardGrpPage2.clickSaveButton();

			Thread.sleep(5000);

			addP2PCardGrp2.clickConfirmbutton();

			actual = modifyCardGrpPage1.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

			String actualStatus= DBHandler.AccessHandler.getCardGroupStatus(cardGroupName, strtRange, endRange);
			String expectedStatus="Y";
			Assert.assertEquals(actualStatus, expectedStatus);

			Map<String, String> dataMap=new HashMap<String, String>();
			dataMap.put("ACTUAL_MESSAGE", actual);
			dataMap.put("STATUS", actualStatus);

		}
		else{
			currentNode.skip("The service and Sub service recieved as null");
		}

		return actual;

	}


	public String viewP2PCardGroup(String serviceName, String subservice, String cardGroupName) throws InterruptedException {
		//ArrayList<String> userInfo= UserAccess.getUserWithAccess("VIEWP2PCARDGRP");
		//login.UserLogin(driver, "Operator", userInfo.get(0), userInfo.get(1));

		String actual=null;

		if (serviceName!=null &&subservice !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_VIEW_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickViewP2PCardGroup();
			viewP2PCardGroupPage.selectServiceType(serviceName);
			viewP2PCardGroupPage.selectP2PCardGroupSubService(subservice);
			viewP2PCardGroupPage.selectP2PCardGroupSetName(cardGroupName);
			viewP2PCardGroupPage.clickSubmitButton();

			viewP2PCardGroupPage2.clickSubmitButton();


			String expected= _masterVO.getMessage("cardgroup.cardgroupdetailsview.view.heading");
			actual = viewP2PCardGroupPage3.getMessage();	

		}
		else{
			currentNode.skip("The service and Sub service recieved as null");
		}

		return actual;
	}
	
	public String viewP2PCardGroupVoucher(String serviceName, String subservice, String cardGroupName) throws InterruptedException {
		//ArrayList<String> userInfo= UserAccess.getUserWithAccess("VIEWP2PCARDGRP");
		//login.UserLogin(driver, "Operator", userInfo.get(0), userInfo.get(1));

		String actual=null;

		if (serviceName!=null &&subservice !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_VIEW_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickViewVoucherCardGroup();
			//viewP2PCardGroupPage.selectServiceType(serviceName);
			viewP2PCardGroupPage.selectP2PCardGroupSubService(subservice);
			viewP2PCardGroupPage.selectP2PCardGroupSetName(cardGroupName);
			viewP2PCardGroupPage.clickSubmitButton();

			viewP2PCardGroupPage2.clickSubmitButton();


			String expected= _masterVO.getMessage("cardgroup.cardgroupdetailsview.view.heading");
			actual = viewP2PCardGroupPage3.getMessage();	

		}
		else{
			currentNode.skip("The service and Sub service recieved as null");
		}

		return actual;
	}


	public String P2PCardGroupStatus(String cardGroupName){

		String actual=null;

		if (cardGroupName!=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_STATUS_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickP2PCardGroupStatus();
			P2PCardGroupStatusPage.checkP2PCardGroup(cardGroupName);

			//String expected = "Checked";

			//Assert.assertEquals(expected, actual);

			P2Pcardgroupstatuspage1.ClickOnsave();
			P2Pcardgroupstatuspage1.ClickOnconfirm();


			actual = P2PCardGroupStatusConfirmPage.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}
		else{
			currentNode.skip("The CardGroupName recieved as null");
		}

		return actual;



	}

	
	public String P2PCardGroupStatusVoucher(String cardGroupName){

		String actual=null;

		if (cardGroupName!=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_STATUS_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickVoucherCardGroupStatus();
			P2PCardGroupStatusPage.checkP2PCardGroup(cardGroupName);

			//String expected = "Checked";

			//Assert.assertEquals(expected, actual);

			P2Pcardgroupstatuspage1.ClickOnsave();
			P2Pcardgroupstatuspage1.ClickOnconfirm();


			actual = P2PCardGroupStatusConfirmPage.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}
		else{
			currentNode.skip("The CardGroupName recieved as null");
		}

		return actual;



	}





	public boolean P2PCardGroupModification_EditCardGroupNewVersion(String serviceName, String subservice, String cardGroupName) throws InterruptedException{


		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_MODIFY_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickModifyP2PCardGroup();

		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);


		String currentVersion=driver.findElement(By.name("selectCardGroupSetVersionId")).getText().trim();


		System.out.println("Version1 is :" +currentVersion);
		modifyCardGrpPage1.clickModifyButton();

		modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());
		//modifyCardGrpPage2.clickEditCardGroup();
		//SwitchWindow.switchwindow(driver);

		//modifyCardGrpPage3.clickAddBtn();

		addP2PCardGrp.clickSaveButton();

		addP2PCardGrp2.clickConfirmbutton();
		Thread.sleep(1000);
		modifyCardGrpPage1.selectServiceType(serviceName);
		modifyCardGrpPage1.selectSubService(subservice);
		modifyCardGrpPage1.selectSetName(cardGroupName);
		modifyCardGrpPage1.selectSetVersion(0);

		String newVersion=driver.findElement(By.name("selectCardGroupSetVersionId")).getText().trim();


		System.out.println("Version2 is :" +newVersion);

		// Asserting message on confirmation.

		int a= Integer.parseInt(currentVersion);
		int expected=a+1;

		int actual = Integer.parseInt(newVersion);

		boolean result =false;

		if (actual==expected){

			return result = true;
		}


		CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		return result;

	}









	public Map<String, String> setDefaultP2PCardGroup(String serviceName, String subService) throws InterruptedException {



		Map<String, String> dataMap = new HashMap<String, String>();

		if (serviceName!=null &&subService !=null){
			String MasterSheetPath = _masterVO.getProperty("DataProvider");

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_DEFAULT_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();
			homePage.clickCardGroup();
			cardGrpCategories.clickDefaultP2PCardGroup();
			DefaultP2PCardGroupPage.selectServiceType(serviceName);
			DefaultP2PCardGroupPage.selectSubService(subService);

			ExcelUtility.setExcelFile( MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.NAME, i).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i).matches(subService)))

				break;
			}

			System.out.println(i);

			DefaultP2PCardGroupPage.selectSetName(ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i));
			//DefaultP2PCardGroupPage.selectSetName(cardGroupName);
			cardGroupName= ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
			System.out.println(cardGroupName);
			DefaultP2PCardGroupPage.clickDefault();
			driver.switchTo().alert().accept();

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);




			String actual= DefaultP2PCardGroupPage.getMessage();
			System.out.println("The message is:" +actual);

			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}
		else{
			currentNode.skip("The service and Sub service recieved as null");
		}
		return dataMap;
	}
	
	public Map<String, String> setDefaultP2PCardGroupVoucher(String serviceName, String subService) throws InterruptedException {



		Map<String, String> dataMap = new HashMap<String, String>();

		if (serviceName!=null &&subService !=null){
			String MasterSheetPath = _masterVO.getProperty("DataProvider");

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_DEFAULT_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));


			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();
			homePage.clickCardGroup();
			cardGrpCategories.clickDefaultVoucherCardGroup();
			//DefaultP2PCardGroupPage.selectServiceType(serviceName);
			DefaultP2PCardGroupPage.selectSubService(subService);

			ExcelUtility.setExcelFile( MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.NAME, i).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i).matches(subService)))

				break;
			}

			System.out.println(i);

			DefaultP2PCardGroupPage.selectSetName(ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i));
			//DefaultP2PCardGroupPage.selectSetName(cardGroupName);
			cardGroupName= ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
			System.out.println(cardGroupName);
			DefaultP2PCardGroupPage.clickDefault();
			driver.switchTo().alert().accept();

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);




			String actual= DefaultP2PCardGroupPage.getMessage();
			System.out.println("The message is:" +actual);

			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}
		else{
			currentNode.skip("The service and Sub service recieved as null");
		}
		return dataMap;
	}









	public Map<String, String> P2PCardGroupCreationWithSuspendedSlab(String serviceName, String subService) throws InterruptedException {
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		if (serviceName!=null &&subService !=null){

			//ArrayList<String> userInfo= UserAccess.getUserWithAccess("ADDP2PCARDGRP");
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_CREATION_ROLECODE);
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
			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			String cardGroupName = UniqueChecker.UC_CardGroupName();
			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			addP2PCardGrp.selectSubService(subService);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);
			System.out.println(cardGroupName);


			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 3; m++) {
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails(array, m, n, cardGroupName);
			}

			modifyCardGrpPage2.clickEditCardGroup();
			SwitchWindow.switchwindow(driver);
			String startRange= driver.findElement(By.name("startRange")).getAttribute("value");
			String endRange= driver.findElement(By.name("endRange")).getAttribute("value");


			int startRng = (int) _parser.getSystemAmount(startRange);
			//int startRng=Integer.parseInt(startRange)*100;
			int endRng=(int) _parser.getSystemAmount(endRange);

			modifyCardGrpPage3.clickSuspendBtn();	

			SwitchWindow.backwindow(driver);
			modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());
			modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());
			if(P2P_CARDGROUP_Type == 1)
				addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));

			modifyCardGrpPage2.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();

			String expected= _masterVO.getMessage("cardgroup.cardgroupdetailsview.successaddmessage");
			String actual = addP2PCardGrp.getMessage();

			//Assert.assertEquals(expected, actual);
			arrayList.add(cardGroupName);


			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

			String actualStatus= DBHandler.AccessHandler.getCardGroupStatus(cardGroupName, startRng, endRng);
			String expectedStatus="S";
			Assert.assertEquals(actualStatus, expectedStatus);

			dataMap.put("STATUS", actualStatus);

		}
		else
		{

			//Log.info("The service and Sub service recieved as null");
			currentNode.skip("The service and Sub service recieved as null");
		}

		return dataMap;
	}
	
	public Map<String, String> P2PCardGroupCreationWithSuspendedSlabVoucher(String serviceName, String subService,HashMap<String, String> initiateMap) throws InterruptedException {
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();

		if (serviceName!=null &&subService !=null){

			//ArrayList<String> userInfo= UserAccess.getUserWithAccess("ADDP2PCARDGRP");
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
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
			cardGrpCategories.clickAddP2PVoucherCardGroup();
			addP2PCardGrp.selectServiceTypeVoucher(serviceName);
			String cardGroupName = UniqueChecker.UC_CardGroupName();
			cardGrpCategories.clickAddP2PVoucherCardGroup();
			addP2PCardGrp.selectServiceTypeVoucher(serviceName);
			addP2PCardGrp.selectSubService(subService);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);
			System.out.println(cardGroupName);


			// Filling multiple slabs for different ranges.
		//	for (int m = 0; m < 3; m++) {
			int m=0;
			int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetailsVoucher(array, m, n, cardGroupName,initiateMap);
		//	}

			modifyCardGrpPage2.clickEditCardGroup();
			SwitchWindow.switchwindow(driver);
			// startRange= driver.findElement(By.name("startRange")).getAttribute("value");
			//String endRange= driver.findElement(By.name("endRange")).getAttribute("value");


			//int startRng = (int) _parser.getSystemAmount(startRange);
			//int startRng=Integer.parseInt(startRange)*100;
			//int endRng=(int) _parser.getSystemAmount(endRange);
			
			String startRng = initiateMap.get("mrp");
			startRng += "00";
			int strtRange = Integer.parseInt(startRng);
			
			String endRng = initiateMap.get("mrp");
			endRng += "00";
			int endRange = Integer.parseInt(endRng);
			//int startRng=Integer.parseInt(startRange)*100;
			//int endRng=(int) _parser.getSystemAmount(endRange);

			modifyCardGrpPage3.clickSuspendBtn();	

			SwitchWindow.backwindow(driver);
			modifyCardGrpPage2.enterApplicableFromDate(homePage.getDate());
			modifyCardGrpPage2.enterApplicableFromHour(homePage.getApplicableFromTime());
			if(P2P_CARDGROUP_Type == 1)
				addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));

			modifyCardGrpPage2.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();

			String expected= _masterVO.getMessage("cardgroup.cardgroupdetailsview.successaddmessage");
			String actual = addP2PCardGrp.getMessage();

			//Assert.assertEquals(expected, actual);
			arrayList.add(cardGroupName);


			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

			String actualStatus= DBHandler.AccessHandler.getCardGroupStatus(cardGroupName, strtRange, endRange);
			String expectedStatus="S";
			Assert.assertEquals(actualStatus, expectedStatus);

			dataMap.put("STATUS", actualStatus);

		}
		else
		{

			//Log.info("The service and Sub service recieved as null");
			currentNode.skip("The service and Sub service recieved as null");
		}

		return dataMap;
	}




	public String p2pCardGroupErrorValidation_BlankCardGroupSetName(String serviceName, String subservice) throws InterruptedException {


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

		homePage.clickCardGroup();
		cardGrpCategories.clickAddP2PCardGroup();
		addP2PCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addP2PCardGrp.selectSubService(subservice);
		addP2PCardGrp.enterP2PCardGroupSetName("");

		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling

		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addP2PCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName);
		}
		addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
		addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		if(P2P_CARDGROUP_Type == 1)
			addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
		addP2PCardGrp.clickSaveButton();

		String actual= addP2PCardGrp.getErrorMessage();

		return actual;

	}
	
	public String p2pCardGroupErrorValidation_BlankCardGroupSetNameVoucher(String serviceName, String subservice,HashMap<String, String> initiateMap) throws InterruptedException {


		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickAddP2PVoucherCardGroup();
		addP2PCardGrp.selectServiceTypeVoucher(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();
		addP2PCardGrp.selectSubService(subservice);
		addP2PCardGrp.enterP2PCardGroupSetName("");

		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling

		if (_masterVO.getClientDetail("C2SCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
			CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addP2PCardGrp.clickCardGroupListIcon();
			enterCardGroupDetailsVoucher(array, m, n, cardGroupName,initiateMap);
		}
		addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
		addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		if(P2P_CARDGROUP_Type == 1)
			addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
		addP2PCardGrp.clickSaveButton();

		String actual= addP2PCardGrp.getErrorMessage();

		return actual;

	}

	public String p2pCardGroupErrorValidation_BlankSubservice(String serviceName, String subservice) throws InterruptedException {


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

		homePage.clickCardGroup();
		cardGrpCategories.clickAddP2PCardGroup();
		addP2PCardGrp.selectServiceType(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();

		//addP2PCardGrp.selectSubService("");

		addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);



		addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
		addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		if(P2P_CARDGROUP_Type == 1)
			addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
		addP2PCardGrp.clickSaveButton();

		String actual= addP2PCardGrp.getErrorMessage();

		return actual;

	}
	
	public String p2pCardGroupErrorValidation_BlankSubserviceVoucher(String serviceName, String subservice,HashMap<String, String>initiateMap) throws InterruptedException {


		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();

		homePage.clickCardGroup();
		cardGrpCategories.clickAddP2PVoucherCardGroup();
		addP2PCardGrp.selectServiceTypeVoucher(serviceName);
		String cardGroupName = UniqueChecker.UC_CardGroupName();

		//addP2PCardGrp.selectSubService("");

		addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);



		addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
		addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		if(P2P_CARDGROUP_Type == 1)
			addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
		addP2PCardGrp.clickSaveButton();

		String actual= addP2PCardGrp.getErrorMessage();

		return actual;

	}




	public String P2PCardGroupStatusDeativate(String cardGroupName){

		String actual=null;

		if (cardGroupName!=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_STATUS_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickP2PCardGroupStatus();
			P2PCardGroupStatusPage.CardGroupDeactivateNegative(cardGroupName,"deactivated","deactivated");
			

			P2Pcardgroupstatuspage1.ClickOnsave();
			P2Pcardgroupstatuspage1.ClickOnconfirm();


			actual = P2PCardGroupStatusConfirmPage.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}
		else{
			currentNode.skip("The CardGroupName recieved as null");
		}

		return actual;



	}
	
	public String P2PCardGroupStatusDeativateVoucher(String cardGroupName){

		String actual=null;

		if (cardGroupName!=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_STATUS_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			driver.switchTo().defaultContent();
			driver.switchTo().frame(0);
			networkPage.selectNetwork();

			homePage.clickCardGroup();
			cardGrpCategories.clickVoucherCardGroupStatus();
			P2PCardGroupStatusPage.CardGroupDeactivateNegative(cardGroupName,"deactivated","deactivated");
			

			P2Pcardgroupstatuspage1.ClickOnsave();
			P2Pcardgroupstatuspage1.ClickOnconfirm();


			actual = P2PCardGroupStatusConfirmPage.getMessage();

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}
		else{
			currentNode.skip("The CardGroupName recieved as null");
		}

		return actual;



	}
	
	
	
	
	public String p2pCardGroupErrorValidation_UniqueCardGroupSetName(String serviceName, String subservice,String cardGroupName) throws InterruptedException {

		
		
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
		homePage.clickCardGroup();
		cardGrpCategories.clickAddP2PCardGroup();
		addP2PCardGrp.selectServiceType(serviceName);
		addP2PCardGrp.selectSubService(subservice);
		addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);
		

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addP2PCardGrp.clickCardGroupListIcon();
			enterCardGroupDetails(array, m, n, cardGroupName);
		}

		addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
		addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		if(P2P_CARDGROUP_Type == 1)
			addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
		addP2PCardGrp.clickSaveButton();
        addP2PCardGrp2.clickConfirmbutton();
		
		
		String actual= addP2PCardGrp.getErrorMessage();
		
		return actual;

	}

public String p2pCardGroupErrorValidation_UniqueCardGroupSetNameVoucher(String serviceName, String subservice,String cardGroupName,HashMap<String, String>initiateMap) throws InterruptedException {

		
		
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int CardGroupType_Slab_Count = 1;
		int crdTypIndex = 1;
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		/*
		 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
		 * name from Excel sheet. 3. Iterate the sub service dropdown and create
		 * card grops.
		 */
		networkPage.selectNetwork();
		homePage.clickCardGroup();
		cardGrpCategories.clickAddP2PVoucherCardGroup();
		addP2PCardGrp.selectServiceTypeVoucher(serviceName);
		addP2PCardGrp.selectSubService(subservice);
		addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);
		

		// Filling multiple slabs for different ranges.
		for (int m = 0; m < 3; m++) {
			int n = m + 1;
			addP2PCardGrp.clickCardGroupListIcon();
			enterCardGroupDetailsVoucher(array, m, n, cardGroupName,initiateMap);
		}

		addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
		addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
		if(P2P_CARDGROUP_Type == 1)
			addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
		addP2PCardGrp.clickSaveButton();
        addP2PCardGrp2.clickConfirmbutton();
		
		
		String actual= addP2PCardGrp.getErrorMessage();
		
		return actual;

	}

	public Map<String, String> P2PCardGroupApplicableDateVerification(String serviceName, String subService) throws InterruptedException {
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();


		if (serviceName!=null &&subService !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_CREATION_ROLECODE);
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
			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			cardGroupName = UniqueChecker.UC_CardGroupName();
			//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			addP2PCardGrp.selectSubService(subService);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);


			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 3; m++) {
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails(array, m, n, cardGroupName);
			}
			addP2PCardGrp.enterApplicableFromDate(homePage.addDaysToCurrentDate(homePage.getDate(), -1));
			System.out.println(homePage.addDaysToCurrentDate(homePage.getDate(), -1));
			addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
			if(P2P_CARDGROUP_Type == 1)
				addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
			addP2PCardGrp.clickSaveButton();

			String actual = addP2PCardGrp.getErrorMessage();

			arrayList.add(cardGroupName);


			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}

		else{
			currentNode.skip("The service and Sub service recieved as null");	
		}

		return dataMap;
	}


	public Map<String, String> P2PCardGroupApplicableDateVerificationVoucher(String serviceName, String subService,HashMap<String, String>initiateMap) throws InterruptedException {
		// Initializing Slab Definition
		ArrayList<String> arrayList = new ArrayList<String>();
		int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
		int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
		int array[] = { Slab1, Slab2, Slab3, Slab4 };
		Map<String, String> dataMap = new HashMap<String, String>();


		if (serviceName!=null &&subService !=null){

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
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
			cardGrpCategories.clickAddP2PVoucherCardGroup();
			addP2PCardGrp.selectServiceTypeVoucher(serviceName);
			cardGroupName = UniqueChecker.UC_CardGroupName();
			//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
			cardGrpCategories.clickAddP2PVoucherCardGroup();
			addP2PCardGrp.selectServiceTypeVoucher(serviceName);
			addP2PCardGrp.selectSubService(subService);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);


			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 3; m++) {
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetailsVoucher(array, m, n, cardGroupName,initiateMap);
			}
			addP2PCardGrp.enterApplicableFromDate(homePage.addDaysToCurrentDate(homePage.getDate(), -1));
			System.out.println(homePage.addDaysToCurrentDate(homePage.getDate(), -1));
			addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
			if(P2P_CARDGROUP_Type == 1)
				addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
			addP2PCardGrp.clickSaveButton();

			String actual = addP2PCardGrp.getErrorMessage();

			arrayList.add(cardGroupName);


			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);

			CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());

		}

		else{
			currentNode.skip("The service and Sub service recieved as null");	
		}

		return dataMap;
	}
	
	
	
	
	
	// C2S Slab Field Validation 	
		public Map<String, String> p2pCardGroupSlabErrorValidation(Map<String, String> dataMap, String serviceName, String subservice) throws InterruptedException {

			Map<String, String> CardGroup_Map = dataMap;
			Map<String, String> dataMap1 = new HashMap<String, String>();
			
			
			
			ArrayList<String> arrayList = new ArrayList<String>();
			int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
			int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
			int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
			int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
			int CardGroupType_Slab_Count = 1;
			int crdTypIndex = 1;
			int array[] = { Slab1, Slab2, Slab3, Slab4 };
			
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_CREATION_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			/*
			 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
			 * name from Excel sheet. 3. Iterate the sub service dropdown and create
			 * card grops.
			 */
			networkPage.selectNetwork();

			homePage.clickCardGroup();

			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			String cardGroupName = UniqueChecker.UC_CardGroupName();
			
			addP2PCardGrp.selectSubService(subservice);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);

			
			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 1; m++) {
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails_usingMap(CardGroup_Map, array, m, n, cardGroupName);
			}
			
	
			return dataMap;

		}
		
		public Map<String, String> p2pCardGroupSlabErrorValidationVoucher(Map<String, String> dataMap, String serviceName, String subservice,HashMap<String, String>initiateMap) throws InterruptedException {

			Map<String, String> CardGroup_Map = dataMap;
			Map<String, String> dataMap1 = new HashMap<String, String>();
			
			
			
			ArrayList<String> arrayList = new ArrayList<String>();
			int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
			int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
			int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
			int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
			int CardGroupType_Slab_Count = 1;
			int crdTypIndex = 1;
			int array[] = { Slab1, Slab2, Slab3, Slab4 };
			
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			/*
			 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
			 * name from Excel sheet. 3. Iterate the sub service dropdown and create
			 * card grops.
			 */
			networkPage.selectNetwork();

			homePage.clickCardGroup();

			cardGrpCategories.clickAddP2PVoucherCardGroup();
			addP2PCardGrp.selectServiceTypeVoucher(serviceName);
			String cardGroupName = UniqueChecker.UC_CardGroupName();
			
			addP2PCardGrp.selectSubService(subservice);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);

			
			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 1; m++) {
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails_usingMapVoucher(CardGroup_Map, array, m, n, cardGroupName,initiateMap);
			}
			
	
			return dataMap;

		}
	
	
	
		
		public Map<String, String> FixValueP2PCardGroupCreation(String serviceName, String subService) throws InterruptedException {
			final String methodname = "P2PCardGroupCreation";
			Log.methodEntry(methodname, serviceName, subService);

			// Initializing Slab Definition
			ArrayList<String> arrayList = new ArrayList<String>();
			int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
			int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
			int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
			int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
			int array[] = { Slab1, Slab2, Slab3, Slab4 };
			Map<String, String> dataMap = new HashMap<String, String>();

			if (serviceName!=null &&subService !=null){

				//ArrayList<String> userInfo= UserAccess.getUserWithAccess("ADDP2PCARDGRP");
				Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_CREATION_ROLECODE);
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
				cardGrpCategories.clickAddP2PCardGroup();
				addP2PCardGrp.selectServiceType(serviceName);
				cardGroupName = UniqueChecker.UC_CardGroupName();
				//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
				cardGrpCategories.clickAddP2PCardGroup();
				addP2PCardGrp.selectServiceType(serviceName);
				addP2PCardGrp.selectSubService(subService);
				addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);
				System.out.println(cardGroupName);


				// Filling multiple slabs for different ranges.
				for (int m = 0; m < 3; m++) {
					int n = m;
					addP2PCardGrp.clickCardGroupListIcon();
					enterCardGroupDetails(array, m, n, cardGroupName);
				}
				addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
				addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
				if(P2P_CARDGROUP_Type == 1)
					addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
				addP2PCardGrp.clickSaveButton();

				addP2PCardGrp2.clickConfirmbutton();

				// Asserting message on confirmation.
				//String expected = "Card group details successfully added";
				String expected= _masterVO.getMessage("cardgroup.cardgroupdetailsview.successaddmessage");
				String actual = addP2PCardGrp.getMessage();

				//Assert.assertEquals(expected, actual);
				arrayList.add(cardGroupName);


				dataMap.put("ACTUALMESSAGE", actual);
				dataMap.put("CARDGROUPNAME", cardGroupName);
				dataMap.put("CARDGROUP_SETID", DBHandler.AccessHandler.getCardGroupSetID(cardGroupName));

				CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());
			} else {
				//Log.info("The service and Sub service recieved as null");
				currentNode.skip("The service and Sub service recieved as null");
			}

			Log.methodExit(methodname);
			return dataMap;
		}

		
		public Map<String, String> FixValueP2PCardGroupCreationVoucher(String serviceName, String subService,HashMap<String, String>initiateMap) throws InterruptedException {
			final String methodname = "P2PCardGroupCreation";
			Log.methodEntry(methodname, serviceName, subService);

			// Initializing Slab Definition
			ArrayList<String> arrayList = new ArrayList<String>();
			int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
			int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
			int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
			int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
			int array[] = { Slab1, Slab2, Slab3, Slab4 };
			Map<String, String> dataMap = new HashMap<String, String>();

			if (serviceName!=null &&subService !=null){

				//ArrayList<String> userInfo= UserAccess.getUserWithAccess("ADDP2PCARDGRP");
				Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
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
				cardGrpCategories.clickAddP2PVoucherCardGroup();
				addP2PCardGrp.selectServiceTypeVoucher(serviceName);
				cardGroupName = UniqueChecker.UC_CardGroupName();
				//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
				cardGrpCategories.clickAddP2PVoucherCardGroup();
				addP2PCardGrp.selectServiceTypeVoucher(serviceName);
				addP2PCardGrp.selectSubService(subService);
				addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);
				System.out.println(cardGroupName);


				// Filling multiple slabs for different ranges.
				for (int m = 0; m < 3; m++) {
					int n = m;
					addP2PCardGrp.clickCardGroupListIcon();
					enterCardGroupDetailsVoucher(array, m, n, cardGroupName,initiateMap);
				}
				addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
				addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
				if(P2P_CARDGROUP_Type == 1)
					addP2PCardGrp.selectCardGroupSetType(_masterVO.getProperty("cardGroupSetType"));
				addP2PCardGrp.clickSaveButton();

				addP2PCardGrp2.clickConfirmbutton();

				// Asserting message on confirmation.
				//String expected = "Card group details successfully added";
				String expected= _masterVO.getMessage("cardgroup.cardgroupdetailsview.successaddmessage");
				String actual = addP2PCardGrp.getMessage();

				//Assert.assertEquals(expected, actual);
				arrayList.add(cardGroupName);


				dataMap.put("ACTUALMESSAGE", actual);
				dataMap.put("CARDGROUPNAME", cardGroupName);
				dataMap.put("CARDGROUP_SETID", DBHandler.AccessHandler.getCardGroupSetID(cardGroupName));

				CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP());
			} else {
				//Log.info("The service and Sub service recieved as null");
				currentNode.skip("The service and Sub service recieved as null");
			}

			Log.methodExit(methodname);
			return dataMap;
		}
		
		
		
		
		// C2S Slab Field Validation 	
		public Map<String, String> p2pCardGroupOverLappingRangeValueValidation(Map<String, String> dataMap, String serviceName, String subservice) throws InterruptedException {

			//Map<String, String> CardGroup_Map = dataMap;
			
			
			
			
			ArrayList<String> arrayList = new ArrayList<String>();
			int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
			int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
			int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
			int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
			int CardGroupType_Slab_Count = 1;
			int crdTypIndex = 1;
			int array[] = { Slab1, Slab2, Slab3, Slab4 };
			
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_CREATION_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			/*
			 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
			 * name from Excel sheet. 3. Iterate the sub service dropdown and create
			 * card grops.
			 */
			networkPage.selectNetwork();

			homePage.clickCardGroup();

			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			String cardGroupName = UniqueChecker.UC_CardGroupName();
			
			addP2PCardGrp.selectSubService(subservice);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);

			
			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 2; m++) {
				int n = m+1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails_Overlap(dataMap, array, m, n, cardGroupName);
			}
			
	
			return dataMap;

		}
		
		public Map<String, String> p2pCardGroupOverLappingRangeValueValidationVoucher(Map<String, String> dataMap, String serviceName, String subservice,HashMap<String, String>initiateMap) throws InterruptedException {

			//Map<String, String> CardGroup_Map = dataMap;
			
			
			
			
			ArrayList<String> arrayList = new ArrayList<String>();
			int Slab1 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
			int Slab2 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"));
			int Slab3 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
			int Slab4 = Integer.parseInt(_masterVO.getProperty("CardGroupSlab4"));
			int CardGroupType_Slab_Count = 1;
			int crdTypIndex = 1;
			int array[] = { Slab1, Slab2, Slab3, Slab4 };
			
			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			/*
			 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
			 * name from Excel sheet. 3. Iterate the sub service dropdown and create
			 * card grops.
			 */
			networkPage.selectNetwork();

			homePage.clickCardGroup();

			cardGrpCategories.clickAddP2PVoucherCardGroup();
			addP2PCardGrp.selectServiceTypeVoucher(serviceName);
			String cardGroupName = UniqueChecker.UC_CardGroupName();
			
			addP2PCardGrp.selectSubService(subservice);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);

			
			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 2; m++) {
				int n = m+1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails_OverlapVoucher(dataMap, array, m, n, cardGroupName,initiateMap);
			}
			
	
			return dataMap;

		}
		
		// Added by Krishan for Vodafone Specific Card Group Type Handling
		public int cardgroupTypeCounterUpdater(int CardGroupType_Slab_Count, int crdTypIndex) {
			if (crdTypIndex > CardGroupType_Slab_Count) 
				return 1;
			else
				return crdTypIndex;
		}

		
		
		
		public Map<String, String> p2pPromoCardGroupCreation(String serviceName, String subService) throws InterruptedException {

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

			Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.P2P_CARD_GROUP_CREATION_ROLECODE);
			login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

			/*
			 * 1. Clicking Add Card group under Add Card Group. 2. Fetch Service
			 * name from Excel sheet. 3. Iterate the sub service dropdown and create
			 * card grops.
			 */
			networkPage.selectNetwork();
	
			homePage.clickCardGroup();

			cardGrpCategories.clickAddP2PCardGroup();
			addP2PCardGrp.selectServiceType(serviceName);
			//cardGroupName = "AUT" + serviceName.substring(0, 3).toUpperCase() + randmGeneration.randomNumeric(4);
			String cardGroupName = UniqueChecker.UC_CardGroupName();
			addP2PCardGrp.selectSubService(subService);
			addP2PCardGrp.enterP2PCardGroupSetName(cardGroupName);

	/*		// Added By Krishan for Vodafone Specific Client Changes. Card Group Dropdown Handling
			if (_masterVO.getClientDetail("P2PCARDGROUP_CARDGROUPTYPE").equalsIgnoreCase("true"))
				CardGroupType_Slab_Count = DBHandler.AccessHandler.getLookUpSize("CGTYP");
*/

			// Filling multiple slabs for different ranges.
			for (int m = 0; m < 3; m++) {
				int n = m + 1;
				addP2PCardGrp.clickCardGroupListIcon();
				enterCardGroupDetails(array, m, n, cardGroupName, crdTypIndex);
				crdTypIndex = cardgroupTypeCounterUpdater(CardGroupType_Slab_Count, ++crdTypIndex);
			}
			addP2PCardGrp.enterApplicableFromDate(homePage.getDate());
			addP2PCardGrp.enterApplicableFromHour(homePage.getApplicableFromTime());
			
			boolean setTypeVisibity = addP2PCardGrp.cardGrpTypeVisibility();
			if (setTypeVisibity == true){
			addP2PCardGrp.selectCardGroupSetTypePromo(PretupsI.CARDGRP_PROMO_LOOKUPS);
			addP2PCardGrp.clickSaveButton();

			addP2PCardGrp2.clickConfirmbutton();
			String actual = addP2PCardGrp.getMessage();

			dataMap.put("ACTUALMESSAGE", actual);
			dataMap.put("CARDGROUPNAME", cardGroupName);

			String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");
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
					addP2PCardGrp.clickSaveButton();
					addP2PCardGrp2.clickConfirmbutton();
					String actual = addP2PCardGrp.getMessage();
					dataMap.put("ACTUALMESSAGE", actual);
					dataMap.put("CARDGROUPNAME", cardGroupName);
					
					CacheUpdate.updateCache(CacheController.CacheI.CARD_GROUP(), CacheController.CacheI.TransferRulesCache());
				}
			}
			return dataMap;
		}
	
		
		public Map<String, String> calculateVoucherCardGroup(String serviceName, String subService,HashMap<String, String> initiateMap) throws InterruptedException {
			final String methodname = "calCulateVoucherCardGroup";
			Log.methodEntry(methodname, serviceName, subService);

			Map<String, String> dataMap = new HashMap<String, String>();

			if (serviceName!=null &&subService !=null){
				Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
				login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
				driver.switchTo().defaultContent();
				driver.switchTo().frame(0);
				networkPage.selectNetwork();

				homePage.clickCardGroup();
				cardGrpCategories.clickCalculateVoucherCardGroup();
				
				calculateP2Ptransfervaluepage1.SelectGatewayId(PretupsI.GATEWAY_TYPE_ALL);
				calculateP2Ptransfervaluepage1.SelectcardGroupSubServiceID(subService);
				calculateP2Ptransfervaluepage1.SelectreceiverTypeIdValue(PretupsI.PREPAID_SUB_LOOKUPS);
				calculateP2Ptransfervaluepage1.SelectreceiverClassId(_masterVO.getProperty("ReceiverServiceClass"));
				if(!initiateMap.get("voucherType").equals(""))
				{
				addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
				}
				if (Nation_Voucher == 1) {
					if(addVoucherDenomination.isSegmentAvailable())
						addVoucherDenomination.SelectVoucherSegment(initiateMap.get("segment"));
				}
				if(!initiateMap.get("denomination").equals(""))
				{
				addVoucherDenomination.SelectMRP(initiateMap.get("denomination"));
				}
				if(!initiateMap.get("activeProfile").equals(""))
				{
					if(addVoucherDenomination.visibilityProfile())
						addVoucherDenomination.SelectProductId(initiateMap.get("activeProfile"));
				}
				calculateP2Ptransfervaluepage1.EnteroldValidityDate(homePage.getDate());
				calculateP2Ptransfervaluepage1.EnterapplicableFromDate(homePage.getDate());
				calculateP2Ptransfervaluepage1.EnterapplicableFromHour(homePage.getApplicableFromTime());
				calculateP2Ptransfervaluepage1.ClickOncalculate();
//				boolean flag=calculateP2Ptransfervaluepage1.getErrorMessageVisible();
//				if(flag) {
				String actual = calculateP2Ptransfervaluepage1.getErrorMessage();
				if(actual != null)
		   		{
				dataMap.put("MESSAGE", actual);
				dataMap.put("ACTUALMESSAGE", "N");
				}
				else {
					dataMap.put("ACTUALMESSAGE", "Y");
					dataMap.put("MESSAGE", actual);
					} 
			}else {
				//Log.info("The service and Sub service recieved as null");
					currentNode.skip("The service and Sub service recieved as null");
			}

			Log.methodExit(methodname);
			return dataMap;
		}

		
		public Map<String, String> calculateVoucherCardGroupValidation(String serviceName, String subService,HashMap<String, String> initiateMap) throws InterruptedException {
			final String methodname = "calCulateVoucherCardGroup";
			Log.methodEntry(methodname, serviceName, subService);

			Map<String, String> dataMap = new HashMap<String, String>();

			if (serviceName!=null &&subService !=null){
				Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VOUCHER_CARD_GROUP_CREATION_ROLECODE);
				login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
				driver.switchTo().defaultContent();
				driver.switchTo().frame(0);
				networkPage.selectNetwork();

				homePage.clickCardGroup();
				cardGrpCategories.clickCalculateVoucherCardGroup();
				if(!initiateMap.get("GatewayCode").equals(""))
				{
				calculateP2Ptransfervaluepage1.SelectGatewayId(PretupsI.GATEWAY_TYPE_ALL);
				}
				if(!subService.equals(""))
				{
					calculateP2Ptransfervaluepage1.SelectcardGroupSubServiceID(subService);
				}
				if(!initiateMap.get("ReceiverType").equals("")) {
				calculateP2Ptransfervaluepage1.SelectreceiverTypeIdValue(PretupsI.PREPAID_SUB_LOOKUPS);
				}
				if(!initiateMap.get("ReceiverClass").equals("")) {
				calculateP2Ptransfervaluepage1.SelectreceiverClassId(_masterVO.getProperty("ReceiverServiceClass"));
				}
				if(!initiateMap.get("voucherType").equals(""))
				{
				addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
				}
				if (Nation_Voucher == 1) {
					if(addVoucherDenomination.isSegmentAvailable())
						addVoucherDenomination.SelectVoucherSegment(initiateMap.get("segment"));
				}
				if(!initiateMap.get("denomination").equals(""))
				{
				addVoucherDenomination.SelectMRP(initiateMap.get("denomination"));
				}
				if(!initiateMap.get("activeProfile").equals(""))
				{
					if(addVoucherDenomination.visibilityProfile())
						addVoucherDenomination.SelectProductId(initiateMap.get("activeProfile"));
				}
				calculateP2Ptransfervaluepage1.EnteroldValidityDate(homePage.getDate());
				calculateP2Ptransfervaluepage1.EnterapplicableFromDate(homePage.getDate());
				calculateP2Ptransfervaluepage1.EnterapplicableFromHour(homePage.getApplicableFromTime());
				calculateP2Ptransfervaluepage1.ClickOncalculate();
			
				String actual = calculateP2Ptransfervaluepage1.getErrorMessage();
				dataMap.put("ACTUALMESSAGE", actual);
				
			} else {
				//Log.info("The service and Sub service recieved as null");
				currentNode.skip("The service and Sub service recieved as null");
			}

			Log.methodExit(methodname);
			return dataMap;
		}
		
		
		
		public void writePromoCardGroupToExcel(String CardGroupName, int rowNum){
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
			ExcelUtility.setCellData(0, ExcelI.PROMO_CARDGROUP_NAME, rowNum, CardGroupName);

		}
		
		
		
}
