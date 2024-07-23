package com.Features;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.taskdefs.WaitFor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;

import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.AutomationException;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.VMS.CreateBatchForVoucherDownload;
import com.pageobjects.channeladminpages.VMS.CreateBatchForVoucherDownloadPage2;
import com.pageobjects.channeladminpages.VMS.CreateBatchForVoucherDownloadPage3;
import com.pageobjects.channeladminpages.VMS.InitiateVoucherO2CPage2;
import com.pageobjects.channeladminpages.VMS.InitiateVoucherO2CPage3;
import com.pageobjects.channeladminpages.VMS.VomsDownload;
import com.pageobjects.channeladminpages.VMS.VomsDownloadPage2;
import com.pageobjects.channeladminpages.VMS.VomsDownloadPage3;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.VoucherDownloadSubCategories;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateO2CTransferPage;
import com.pageobjects.channeladminpages.voucherdownload.VoucherDownloadPage1;
import com.pageobjects.channeladminpages.voucherdownload.VoucherDownloadPage2;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.VMS.AddActiveProfile;
import com.pageobjects.superadminpages.VMS.AddActiveProfilePage2;
import com.pageobjects.superadminpages.VMS.AddVoucherBundleConfirmPage;
import com.pageobjects.superadminpages.VMS.AddVoucherBundlePage;
import com.pageobjects.superadminpages.VMS.AddVoucherDenomination;
import com.pageobjects.superadminpages.VMS.AddVoucherDenominationPage2;
import com.pageobjects.superadminpages.VMS.AddVoucherProfile;
import com.pageobjects.superadminpages.VMS.BatchDetails;
import com.pageobjects.superadminpages.VMS.ChangeGeneratedStatusPage;
import com.pageobjects.superadminpages.VMS.ChangeOtherStatus;
import com.pageobjects.superadminpages.VMS.ChangeOtherStatusPage2;
import com.pageobjects.superadminpages.VMS.ModifyActiveProfile;
import com.pageobjects.superadminpages.VMS.ModifyActiveProfile2;
import com.pageobjects.superadminpages.VMS.ModifyActiveProfile3;
import com.pageobjects.superadminpages.VMS.ModifyVoucherBundleConfirmPage;
import com.pageobjects.superadminpages.VMS.ModifyVoucherBundlePage;
import com.pageobjects.superadminpages.VMS.ModifyVoucherBundlePage2;
import com.pageobjects.superadminpages.VMS.ModifyVoucherDenomination;
import com.pageobjects.superadminpages.VMS.ModifyVoucherDenomination2;
import com.pageobjects.superadminpages.VMS.ModifyVoucherDenomination3;
import com.pageobjects.superadminpages.VMS.ModifyVoucherDenomination4;
import com.pageobjects.superadminpages.VMS.ModifyVoucherProfile;
import com.pageobjects.superadminpages.VMS.ModifyVoucherProfilePage2;
import com.pageobjects.superadminpages.VMS.ModifyVoucherProfilePage3;
import com.pageobjects.superadminpages.VMS.ViewActiveProfile;
import com.pageobjects.superadminpages.VMS.ViewActiveProfile2;
import com.pageobjects.superadminpages.VMS.ViewBatchList;
import com.pageobjects.superadminpages.VMS.ViewBatchList2;
import com.pageobjects.superadminpages.VMS.ViewVoucherBundlePage;
import com.pageobjects.superadminpages.VMS.ViewVoucherBundlePage2;
import com.pageobjects.superadminpages.VMS.ViewVoucherDenomination;
import com.pageobjects.superadminpages.VMS.ViewVoucherDenomination2;
import com.pageobjects.superadminpages.VMS.ViewVoucherProfile;
import com.pageobjects.superadminpages.VMS.ViewVoucherProfile2;
import com.pageobjects.superadminpages.VMS.ViewVoucherProfile3;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval1Page1;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval1Page2;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval1Page3;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval1Page4;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval2Page1;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval2Page2;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval2Page3;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval2Page4;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval3Page1;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval3Page2;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval3Page3;
import com.pageobjects.superadminpages.VMS.VomsOrderApproval3Page4;
import com.pageobjects.superadminpages.VMS.VoucherBundleManagement;
import com.pageobjects.superadminpages.VMS.VoucherBurnRateIndicator;
import com.pageobjects.superadminpages.VMS.VoucherGenerationInitiatePage1;
import com.pageobjects.superadminpages.VMS.VoucherGenerationInitiatePage2;
import com.pageobjects.superadminpages.VMS.VoucherGenerationInitiatePage3;
import com.pageobjects.superadminpages.VMS.VoucherGenerationInitiatePage4;
import com.pageobjects.superadminpages.VMS.VoucherExpiry.ChangeVoucherExpiryPage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.homepage.VoucherDenomination;
import com.pageobjects.superadminpages.homepage.VoucherExpiry;
import com.pageobjects.superadminpages.homepage.VoucherGeneration;
import com.pageobjects.superadminpages.homepage.VoucherProfile;
import com.pageobjects.superadminpages.homepage.VoucherReports;
import com.pageobjects.superadminpages.preferences.SystemPreferencePage;
import com.pretupsControllers.BTSLUtil;
import com.sshmanager.SSHService;
import com.testscripts.prerequisites.UpdateCache;
import com.utils.Assertion;
import com.utils.BTSLDateUtil;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class VMS {

	WebDriver driver;
	String voucherSegment =_masterVO.getProperty("segmentType");
	Login login1;
	ChannelAdminHomePage caHomepage;
	SuperAdminHomePage saHomePage;
	VoucherDenomination voucherDenomination;
	VoucherProfile voucherProfile;
	VoucherGeneration voucherGeneration;
	VoucherDownloadSubCategories voucherDownloadSubCategories;
	AddActiveProfile addActiveProfile;
	AddActiveProfilePage2 addActiveProfilePage2;
	AddVoucherProfile addVoucherProfile;
	AddVoucherDenomination addVoucherDenomination;
	AddVoucherDenominationPage2 addVoucherDenominationPage2;
	VoucherGenerationInitiatePage1 voucherGenerationInitiatePage1;
	VoucherGenerationInitiatePage2 voucherGenerationInitiatePage2;
	VoucherGenerationInitiatePage3 voucherGenerationInitiatePage3;
	VoucherGenerationInitiatePage4 voucherGenerationInitiatePage4;
	VomsOrderApproval1Page1 vomsOrderApproval1Page1;
	VomsOrderApproval1Page2 vomsOrderApproval1Page2;
	VomsOrderApproval1Page3 vomsOrderApproval1Page3;
	VomsOrderApproval1Page4 vomsOrderApproval1Page4;
	VomsOrderApproval2Page1 vomsOrderApproval2Page1;
	VomsOrderApproval2Page2 vomsOrderApproval2Page2;
	VomsOrderApproval2Page3 vomsOrderApproval2Page3;
	VomsOrderApproval2Page4 vomsOrderApproval2Page4;
	VomsOrderApproval3Page1 vomsOrderApproval3Page1;
	VomsOrderApproval3Page2 vomsOrderApproval3Page2;
	VomsOrderApproval3Page3 vomsOrderApproval3Page3;
	VomsOrderApproval3Page4 vomsOrderApproval3Page4;
	CreateBatchForVoucherDownload createBatchForVoucherDownload;
	CreateBatchForVoucherDownloadPage2 createBatchForVoucherDownloadPage2;
	CreateBatchForVoucherDownloadPage3 createBatchForVoucherDownloadPage3;
	VomsDownload vomsDownload;
	VomsDownloadPage2 vomsDownloadPage2;
	VomsDownloadPage3 vomsDownloadPage3;
	ChangeOtherStatus changeOtherStatus;
	ChangeOtherStatusPage2 changeOtherStatusPage2;
	VoucherReports voucherReports;
	VoucherBurnRateIndicator voucherBurnRateIndicator;
	ModifyVoucherProfile modifyVoucherProfile;
	ModifyVoucherProfilePage2 modifyVoucherProfilePage2;
	ModifyVoucherProfilePage3 modifyVoucherProfilePage3;
	SystemPreferencePage systemPreferencePage;
	UpdateCache updateCache;
	SelectNetworkPage selectNetworkPage;
	Map<String, String> userAccessMap;
	NetworkAdminHomePage homePage;
	ViewVoucherDenomination viewVoucherDenomination;
	ViewVoucherDenomination2 viewVoucherDenomination2;
	ViewVoucherProfile viewVoucherProfile;
	ViewVoucherProfile2 viewVoucherProfile2;
	ViewVoucherProfile3 viewVoucherProfile3;
	ModifyVoucherDenomination modifyVoucherDenomination;
	ModifyVoucherDenomination2 modifyVoucherDenomination2;
	ModifyVoucherDenomination3 modifyVoucherDenomination3;
	ModifyVoucherDenomination4 modifyVoucherDenomination4;
	ViewActiveProfile viewActiveProfile;
	ViewActiveProfile2 viewActiveProfile2;
	ModifyActiveProfile modifyActiveProfile;
	ModifyActiveProfile2 modifyActiveProfile2;
	ModifyActiveProfile3 modifyActiveProfile3;
	ViewBatchList viewBatchList;
	ViewBatchList2 viewBatchList2;
	BatchDetails batchDetails;
	InitiateO2CTransferPage initiateO2CPage;
	InitiateVoucherO2CPage2 initiateVoucherO2CPage2;
	InitiateVoucherO2CPage3 initiateVoucherO2CPage3;
	VoucherExpiry voucherExpiry;
	VoucherBundleManagement voucherBundleManagementPage;
	AddVoucherBundlePage addVoucherBundlePage;
	AddVoucherBundleConfirmPage addVoucherBundleConfirmPage;
	ViewVoucherBundlePage viewVoucherBundlePage;
	ViewVoucherBundlePage2 viewVoucherBundlePage2;
	ModifyVoucherBundlePage modifyVoucherBundlePage;
	ModifyVoucherBundlePage2 modifyVoucherBundlePage2;
	ModifyVoucherBundleConfirmPage modifyVoucherBundleConfirmPage;
	VoucherDownloadPage1 voucherDownloadPage1;
	VoucherDownloadPage2 voucherDownloadPage2;
	int Nation_Voucher;
	ChangeGeneratedStatusPage changegeneratedStatus;
	static RandomGeneration randStr = new RandomGeneration();
	
	public int getNationVoucher() {
		return Nation_Voucher;
	}

	public VMS(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		saHomePage = new SuperAdminHomePage(driver);
		caHomepage = new ChannelAdminHomePage(driver);
		voucherDenomination = new VoucherDenomination(driver);
		voucherProfile = new VoucherProfile(driver);
		voucherGeneration = new VoucherGeneration(driver);
		voucherDownloadSubCategories = new VoucherDownloadSubCategories(driver);
		addActiveProfile = new AddActiveProfile(driver);
		addActiveProfilePage2 = new AddActiveProfilePage2(driver);
		addVoucherBundlePage = new AddVoucherBundlePage(driver);
		addVoucherProfile = new AddVoucherProfile(driver);
		addVoucherDenomination = new AddVoucherDenomination(driver);
		addVoucherDenominationPage2 = new AddVoucherDenominationPage2(driver);
		voucherGenerationInitiatePage1 = new VoucherGenerationInitiatePage1(driver);
		voucherGenerationInitiatePage2 = new VoucherGenerationInitiatePage2(driver);
		voucherGenerationInitiatePage3 = new VoucherGenerationInitiatePage3(driver);
		voucherGenerationInitiatePage4 = new VoucherGenerationInitiatePage4(driver);
		vomsOrderApproval1Page1 = new VomsOrderApproval1Page1(driver);
		vomsOrderApproval1Page2 = new VomsOrderApproval1Page2(driver);
		vomsOrderApproval1Page3 = new VomsOrderApproval1Page3(driver);
		vomsOrderApproval1Page4 = new VomsOrderApproval1Page4(driver);
		vomsOrderApproval2Page1 = new VomsOrderApproval2Page1(driver);
		vomsOrderApproval2Page2 = new VomsOrderApproval2Page2(driver);
		vomsOrderApproval2Page3 = new VomsOrderApproval2Page3(driver);
		vomsOrderApproval2Page4 = new VomsOrderApproval2Page4(driver);
		vomsOrderApproval3Page1 = new VomsOrderApproval3Page1(driver);
		vomsOrderApproval3Page2 = new VomsOrderApproval3Page2(driver);
		vomsOrderApproval3Page3 = new VomsOrderApproval3Page3(driver);
		vomsOrderApproval3Page4 = new VomsOrderApproval3Page4(driver);
		createBatchForVoucherDownload = new CreateBatchForVoucherDownload(driver);
		createBatchForVoucherDownloadPage2 = new CreateBatchForVoucherDownloadPage2(driver);
		createBatchForVoucherDownloadPage3 = new CreateBatchForVoucherDownloadPage3(driver);
		vomsDownload = new VomsDownload(driver);
		vomsDownloadPage2 = new VomsDownloadPage2(driver);
		vomsDownloadPage3 = new VomsDownloadPage3(driver);
		changeOtherStatus = new ChangeOtherStatus(driver);
		changeOtherStatusPage2 = new ChangeOtherStatusPage2(driver);
		voucherReports = new VoucherReports(driver);
		voucherBurnRateIndicator = new VoucherBurnRateIndicator(driver);
		modifyVoucherProfile = new ModifyVoucherProfile(driver);
		modifyVoucherProfilePage2 = new ModifyVoucherProfilePage2(driver);
		modifyVoucherProfilePage3 = new ModifyVoucherProfilePage3(driver);
		systemPreferencePage = new SystemPreferencePage(driver);
		updateCache = new UpdateCache();
		selectNetworkPage = new SelectNetworkPage(driver);
		homePage = new NetworkAdminHomePage(driver);
		viewVoucherDenomination = new ViewVoucherDenomination(driver);
		viewVoucherDenomination2 = new ViewVoucherDenomination2(driver);
		viewVoucherProfile = new ViewVoucherProfile(driver);
		viewVoucherProfile2 = new ViewVoucherProfile2(driver);
		viewVoucherProfile3 = new ViewVoucherProfile3(driver);
		modifyVoucherDenomination = new ModifyVoucherDenomination(driver);
		modifyVoucherDenomination2 = new ModifyVoucherDenomination2(driver);
		modifyVoucherDenomination3 = new ModifyVoucherDenomination3(driver);
		modifyVoucherDenomination4 = new ModifyVoucherDenomination4(driver);
		viewActiveProfile = new ViewActiveProfile(driver);
		viewActiveProfile2 = new ViewActiveProfile2(driver);
		modifyActiveProfile =  new ModifyActiveProfile(driver);
		modifyActiveProfile2 = new ModifyActiveProfile2(driver);
		modifyActiveProfile3 = new ModifyActiveProfile3(driver);
		viewBatchList = new ViewBatchList(driver);
		viewBatchList2 = new ViewBatchList2(driver);
		batchDetails = new BatchDetails(driver);
		initiateO2CPage = new InitiateO2CTransferPage(driver);
		initiateVoucherO2CPage2 = new InitiateVoucherO2CPage2(driver);
		initiateVoucherO2CPage3 = new InitiateVoucherO2CPage3(driver);
		voucherExpiry  = new VoucherExpiry(driver);
		Nation_Voucher = Integer.parseInt(_masterVO.getClientDetail("Nation_Voucher"));
		changegeneratedStatus = new ChangeGeneratedStatusPage(driver);
		voucherBundleManagementPage = new VoucherBundleManagement(driver);
		viewVoucherBundlePage = new ViewVoucherBundlePage(driver);
		viewVoucherBundlePage2 = new ViewVoucherBundlePage2(driver);
		modifyVoucherBundlePage = new ModifyVoucherBundlePage(driver);
		modifyVoucherBundlePage2 = new ModifyVoucherBundlePage2(driver);
		addVoucherBundleConfirmPage = new AddVoucherBundleConfirmPage(driver);
		modifyVoucherBundleConfirmPage = new ModifyVoucherBundleConfirmPage(driver);
		voucherDownloadPage1 = new VoucherDownloadPage1(driver);
		voucherDownloadPage2 = new VoucherDownloadPage2(driver);	
	}
	
	//jj
//	Add Voucher Bundle and add to ExcelSheet
	public HashMap<String,String> addVoucherBundle(){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VMS_BUNDLE_ADD);
		HashMap<String,String> initiateMap = new HashMap<String,String>();
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
//		int rowCount = ExcelUtility.getRowCount(); //number of vouchers for bundle creation
		int rowCount = Integer.parseInt(_masterVO.getClientDetail("BUNDLE_CREATION_ROWS"));
		Object[] profiles = new Object[rowCount];
		String[] bundleProfiles = new String[rowCount];
		//get existing profile details from excel sheet
		profiles = loadProfile(rowCount);
		int quantity,price,totalExpectedMRP=0; //to calculate total value of bundle
		for(int i = 0 ; i <  rowCount ; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) profiles[i]);
			Log.info(temp.get("activeProfile"));
		}	
		
		login1.LoginAsUser(driver,userAccessMap.get("LOGIN_ID") , userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherBundleManagement();
		voucherBundleManagementPage.clickAddVoucherBundle();
		initiateMap.put("voucherBundleName", UniqueChecker.UC_VBNAME());
		initiateMap.put("voucherBundlePrefix", UniqueChecker.UC_VBPREFIX());
		while(true) {	if(!initiateMap.get("voucherBundleName").equals(null))
						break;	}
		Log.info(initiateMap.get("voucherBundleName") + " " + initiateMap.get("voucherBundlePrefix"));
		addVoucherBundlePage.enterBundleName(initiateMap.get("voucherBundleName"));
		addVoucherBundlePage.enterBundlePrefix(initiateMap.get("voucherBundlePrefix"));
		String isBlankVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(PretupsI.IS_BLANK_VOUCHER_REQUIRED);
		//Enter Vouchers
		for(int i = 0 ; i < rowCount ; i++) {
			if(profiles[i] != null) {
				initiateMap.putAll((Map<? extends String, ? extends String>) profiles[i]);
			Log.info("VoucherType: " + initiateMap.get("voucherType") + " ; " + "ProfileName: " + initiateMap.get("activeProfile") 	+ "BundleName: " + initiateMap.get("voucherBundleName"));
			try {
				if("TRUE".equalsIgnoreCase(isBlankVoucherAllowed) && "BLANK_VOUCHER".equals(initiateMap.get("voucherType")))
					continue;
		addVoucherBundlePage.selectVoucherTypeByIndex(i,initiateMap.get("voucherType"));
		if(Nation_Voucher == 1)
			addVoucherBundlePage.selectVoucherSegmentByIndex(i,"Local");
		addVoucherBundlePage.selectVoucherDenominationByIndex(i,initiateMap.get("denominationName") + "(" + Integer.toString(Integer.parseInt(initiateMap.get("mrp"))) + ".0)");
		addVoucherBundlePage.selectVoucherProfileByIndex(i,initiateMap.get("activeProfile"));
		quantity = Integer.parseInt(randStr.randomNumeric(1))+1;
		addVoucherBundlePage.enterVoucherQuantityByIndex(i,Integer.toString(quantity));
		bundleProfiles[i] = initiateMap.get("activeProfile");
		price = quantity * Integer.parseInt(initiateMap.get("mrp"));
		totalExpectedMRP += price;
			}catch(NoSuchElementException e) {
		}}}
		addVoucherBundlePage.clickSubmit();
		addVoucherBundleConfirmPage.clickSubmit();
		
		String MRP = DBHandler.AccessHandler.fetchMRPforBundle(initiateMap.get("voucherBundleName")); 
		Pattern pattern = Pattern.compile("([0-9]*).*");
		Matcher matcher = pattern.matcher(MRP); 
		if(matcher.find()){
		    MRP = matcher.group(1);		//extract MRP without decimal
		}else{
		    System.out.println("Not Found");
		}
		Log.info("MRP of Bundle is: " + MRP );
		if(totalExpectedMRP == Integer.parseInt(MRP) )
			Assertion.assertPass("Retail Price calculated is correct");
		
		String successMessage = addVoucherBundlePage.getMessage();
		if(successMessage != null){
			initiateMap.put("MessageStatus","Y");
			initiateMap.put("Message",successMessage);
			//Add Bundle profiles data to Excel sheet
			BuilderLogic VBSheet = new BuilderLogic();
			VBSheet.writeVOMSBundleSheet(bundleProfiles,initiateMap.get("voucherBundleName"),initiateMap.get("voucherBundlePrefix"));
		}else{
			initiateMap.put("MessageStatus","N");
			String failureMessage = addVoucherBundlePage.getErrorMessage();
			initiateMap.put("Message",failureMessage);
		}		
		
		return initiateMap;
	}
	
	//load voucher profiles to create voucher bundle
	public Object[] loadProfile(int rowCount) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		Object[] dataObj = new Object[rowCount];
		int objCounter = 0;
		for (int i = 1; i <= rowCount; i++) {
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
			VomsData.put("service", ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i));
			VomsData.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
			VomsData.put("categoryName",ExcelUtility.getCellData(0, ExcelI.VOMS_USER_CATEGORY_NAME, i));
			VomsData.put("denominationName", ExcelUtility.getCellData(0, ExcelI.VOMS_DENOMINATION_NAME, i));
			VomsData.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
			VomsData.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
			VomsData.put("quantity", randStr.randomNumeric(1)+1);
			Log.info("EXCEL :: " + VomsData.get("activeProfile"));
			dataObj[objCounter++] = VomsData.clone();
		}
		return dataObj;
	}
	
	public HashMap<String, String> viewVoucherBundle(HashMap<String, String> initiateMap) throws SQLException {
	userAccessMap = UserAccess.getUserWithAccess(RolesI.VMS_BUNDLE_MODIFY);
		
		login1.LoginAsUser(driver,userAccessMap.get("LOGIN_ID") , userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherBundleManagement();
		voucherBundleManagementPage.clickViewVoucherBundle();
		String VBName = initiateMap.get("voucherBundleName");
		String VBPrefix = initiateMap.get("voucherBundlePrefix");
		initiateMap.put("Message", null);
		initiateMap.put("MessageStatus", "N");
		try{ Log.info("NAME&ID: " + VBName + " " +  VBPrefix);} catch(NullPointerException e) {}

		boolean flag = viewVoucherBundlePage.checkParticularVoucherBundle(VBName, VBPrefix);
		boolean bundleInRow= viewVoucherBundlePage.clickVoucherBundle(VBName, VBPrefix);
		String profileID, profileName;
		if(bundleInRow) {
			ResultSet profiles = DBHandler.AccessHandler.fetchVoucherBundleDetails(VBName);
			profiles.beforeFirst();
			if(profiles.next()) {
				profileID = profiles.getString("PROFILE_ID");
				profileName = profiles.getString("PRODUCT_NAME");
				if(!viewVoucherBundlePage2.checkProfileInBundle(profileID,VBName))
						Log.info("Voucher Profile: " + profileName + " with ID: " + profileID + " found");
			}
		}
		if(!flag){
 		   	initiateMap.put("MessageStatus", "N");
	   		initiateMap.put("Message", "No Such Voucher Bundle Found");
 	   }else{
 		   initiateMap.put("MessageStatus", "Y");
 		   initiateMap.put("Message", "Voucher Bundle Found on View Voucher Bundle Page");
 	   }
 	   
 	   return initiateMap;
	}
	
	public HashMap<String, String> modifyVoucherBundle(HashMap<String, String> initiateMap) {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VMS_BUNDLE_MODIFY);
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = Integer.parseInt(_masterVO.getClientDetail("BUNDLE_CREATION_ROWS")); //number of vouchers for bundle creation
		Object[] profiles = new Object[rowCount];
		String[] bundleProfiles = new String[rowCount];
		boolean bundleFound = false;
		
		//get existing profile details from excel sheet
		profiles = loadProfile(rowCount);
		int quantity,price,totalExpectedMRP=0; //to calculate total value of bundle
		for(int i = 0 ; i <  rowCount ; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) profiles[i]);
			Log.info(temp.get("activeProfile"));
		}
		
		login1.LoginAsUser(driver,userAccessMap.get("LOGIN_ID") , userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherBundleManagement();
		voucherBundleManagementPage.clickModifyVoucherBundle();
		bundleFound = modifyVoucherBundlePage.clickVoucherBundle(initiateMap.get("voucherBundleName"), initiateMap.get("voucherBundlePrefix"));
		if(bundleFound) {
			modifyVoucherBundlePage.clickSubmit();
			String isBlankVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(PretupsI.IS_BLANK_VOUCHER_REQUIRED);
			//Enter Vouchers
			for(int i = 0 ; i < rowCount ; i++) {
				if(profiles[i] != null) {
					initiateMap.putAll((Map<? extends String, ? extends String>) profiles[i]);
				Log.info("VoucherType: " + initiateMap.get("voucherType") + " ; " + "ProfileName: " + initiateMap.get("activeProfile") 	+ "BundleName: " + initiateMap.get("voucherBundleName"));
				try {
					if("TRUE".equalsIgnoreCase(isBlankVoucherAllowed) && "BLANK_VOUCHER".equals(initiateMap.get("voucherType")))
						continue;
			modifyVoucherBundlePage2.selectVoucherTypeByIndex(i,initiateMap.get("voucherType"));
			if(Nation_Voucher == 1)
				modifyVoucherBundlePage2.selectVoucherSegmentByIndex(i,"Local");
			modifyVoucherBundlePage2.selectVoucherDenominationByIndex(i,initiateMap.get("denominationName") + "(" + Integer.toString(Integer.parseInt(initiateMap.get("mrp"))) + ".0)");
			modifyVoucherBundlePage2.selectVoucherProfileByIndex(i,initiateMap.get("activeProfile"));
			quantity = Integer.parseInt(randStr.randomNumeric(1)) + 1;
			modifyVoucherBundlePage2.enterVoucherQuantityByIndex(i,Integer.toString(quantity));
			bundleProfiles[i] = initiateMap.get("activeProfile");
			price = quantity * Integer.parseInt(initiateMap.get("mrp"));
			totalExpectedMRP += price;
				}catch(NoSuchElementException e) {
			}}}
			modifyVoucherBundlePage2.clickSubmit();
			modifyVoucherBundleConfirmPage.clickConfirm();
			
			String MRP = DBHandler.AccessHandler.fetchMRPforBundle(initiateMap.get("voucherBundleName"));
			Pattern pattern = Pattern.compile("([0-9]*).*");
			Matcher matcher = pattern.matcher(MRP); 
			if(matcher.find()){
			    MRP = matcher.group(1);		//extract MRP without decimal
			}else{
			    System.out.println("Not Found");
			}
			Log.info("MRP of Bundle is: " + MRP );
			if(totalExpectedMRP == Integer.parseInt(MRP) )
				Assertion.assertPass("Retail Price calculated is correct");
			
			String successMessage = modifyVoucherBundlePage2.getMessage();
			if(successMessage != null){
				initiateMap.put("MessageStatus","Y");
				initiateMap.put("Message",successMessage);
				//Add Bundle profiles data to Excel sheet
				BuilderLogic VBSheet = new BuilderLogic();
				VBSheet.writeVOMSBundleSheet(bundleProfiles,initiateMap.get("voucherBundleName"),initiateMap.get("voucherBundlePrefix"));
			}else{
				initiateMap.put("MessageStatus","N");
				String failureMessage = modifyVoucherBundlePage2.getErrorMessage();
				initiateMap.put("Message",failureMessage);
			}		
		}else {
			initiateMap.put("MessageStatus","N");
			initiateMap.put("Message","Bundle not found in List");
		}
				
		
		return initiateMap;
	}
	
	public HashMap<String,String> addVoucherBundleNEGATIVE1(HashMap<String,String> initiateMap){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VMS_BUNDLE_ADD);
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = Integer.parseInt(_masterVO.getClientDetail("BUNDLE_CREATION_ROWS"));
		Object[] profiles = new Object[rowCount];

		//get profile details from excel sheet
		profiles = loadProfile(rowCount);
		for(int i = 0 ; i <  rowCount ; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) profiles[i]);
			Log.info(temp.get("activeProfile"));
		}
		
		login1.LoginAsUser(driver,userAccessMap.get("LOGIN_ID") , userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherBundleManagement();
		voucherBundleManagementPage.clickAddVoucherBundle();  Log.info(initiateMap.get("voucherBundleName") + " " + initiateMap.get("voucherBundlePrefix"));
		addVoucherBundlePage.enterBundleName(initiateMap.get("voucherBundleName"));
		addVoucherBundlePage.enterBundlePrefix(initiateMap.get("voucherBundlePrefix"));
		for(int i = 0 ; i < rowCount ; i++) { //To load vouchers in the bundle
			if(profiles[i] != null) { initiateMap.putAll((Map<? extends String, ? extends String>) profiles[i]); Log.info("VoucherType: " + initiateMap.get("voucherType") + " ; " + "ProfileName: " + initiateMap.get("activeProfile") 	+ "BundleName: " + initiateMap.get("voucherBundleName"));
			try {
		addVoucherBundlePage.selectVoucherTypeByIndex(i,initiateMap.get("voucherType"));
			if(Nation_Voucher == 1)
		addVoucherBundlePage.selectVoucherSegmentByIndex(i,"Local");
		addVoucherBundlePage.selectVoucherDenominationByIndex(i,initiateMap.get("denominationName") + "(" + Integer.toString(Integer.parseInt(initiateMap.get("mrp"))) + ".0)");
		addVoucherBundlePage.selectVoucherProfileByIndex(i,initiateMap.get("activeProfile"));
			if(initiateMap.get("quantity") != null) {
		addVoucherBundlePage.enterVoucherQuantityByIndex(i,initiateMap.get("quantity"));
			}else {
		addVoucherBundlePage.enterVoucherQuantityByIndex(i,"10");
			}}catch(NoSuchElementException e) {}
		}}
		String totalBundleValue = addVoucherBundlePage.getTotalValue();
		initiateMap.put("totalBundleValue", totalBundleValue);
		addVoucherBundlePage.clickSubmit();
		String failureMessage = null;
		try {
		failureMessage = addVoucherBundlePage.getErrorMessage();
		}catch(Exception e) {}
		if(failureMessage != null){
			initiateMap.put("MessageStatus","N");
			initiateMap.put("Message",failureMessage);		
		}else{
			String successMessage = addVoucherBundlePage.getMessage();
			initiateMap.put("MessageStatus","Y");
			initiateMap.put("Message",successMessage);		
		}		
		
		return initiateMap;
	}
	
	public HashMap<String,String> addVoucherBundleNEGATIVE2(HashMap<String,String> initiateMap){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VMS_BUNDLE_ADD);
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = Integer.parseInt(_masterVO.getClientDetail("BUNDLE_CREATION_ROWS"));
		Object[] profiles = new Object[rowCount];

		//get profile details from excel sheet
		profiles = loadProfile(rowCount);
		for(int i = 0 ; i <  rowCount ; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) profiles[i]);
			Log.info(temp.get("activeProfile"));
		}
		
		login1.LoginAsUser(driver,userAccessMap.get("LOGIN_ID") , userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherBundleManagement();
		voucherBundleManagementPage.clickAddVoucherBundle();  Log.info(initiateMap.get("voucherBundleName") + " " + initiateMap.get("voucherBundlePrefix"));
		addVoucherBundlePage.enterBundleName(initiateMap.get("voucherBundleName"));
		addVoucherBundlePage.enterBundlePrefix(initiateMap.get("voucherBundlePrefix"));
		String isBlankVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(PretupsI.IS_BLANK_VOUCHER_REQUIRED);
		for(int i = 0 ; i < rowCount ; i++) { //To load vouchers in the bundle
			if(profiles[i] != null) { initiateMap.putAll((Map<? extends String, ? extends String>) profiles[i]); Log.info("VoucherType: " + initiateMap.get("voucherType") + " ; " + "ProfileName: " + initiateMap.get("activeProfile") 	+ "BundleName: " + initiateMap.get("voucherBundleName"));
			try {
				if("TRUE".equalsIgnoreCase(isBlankVoucherAllowed) && "BLANK_VOUCHER".equals(initiateMap.get("voucherType")))
					continue;
		addVoucherBundlePage.selectVoucherTypeByIndex(i,initiateMap.get("voucherType"));
			if(Nation_Voucher == 1)
		addVoucherBundlePage.selectVoucherSegmentByIndex(i,"Local");
		addVoucherBundlePage.selectVoucherDenominationByIndex(i,initiateMap.get("denominationName") + "(" + Integer.toString(Integer.parseInt(initiateMap.get("mrp"))) + ".0)");
		addVoucherBundlePage.selectVoucherProfileByIndex(i,initiateMap.get("activeProfile"));
			if(initiateMap.get("quantity") != null) {
		addVoucherBundlePage.enterVoucherQuantityByIndex(i,initiateMap.get("quantity"));
			}else {
		addVoucherBundlePage.enterVoucherQuantityByIndex(i,"10");
			}}catch(NoSuchElementException e) {}
		}}
		String totalBundleValue = addVoucherBundlePage.getTotalValue();
		initiateMap.put("totalBundleValue", totalBundleValue);
		addVoucherBundlePage.clickSubmit();
		addVoucherBundleConfirmPage.clickSubmit();
		
		String failureMessage = null;
		try {
		failureMessage = addVoucherBundlePage.getErrorMessage();
		}catch(Exception e) {}
		if(failureMessage != null){
			initiateMap.put("MessageStatus","N");
			initiateMap.put("Message",failureMessage);		
		}else{
			String successMessage = addVoucherBundlePage.getMessage();
			initiateMap.put("MessageStatus","Y");
			initiateMap.put("Message",successMessage);		
		}		
		
		return initiateMap;
	}
	
	public HashMap<String,String> addVoucherBundleNEGATIVE3(HashMap<String,String> initiateMap){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VMS_BUNDLE_ADD);
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = Integer.parseInt(_masterVO.getClientDetail("BUNDLE_CREATION_ROWS"));
		Object[] profiles = new Object[rowCount];
		String failureMessage = null;
		String quantity = initiateMap.get("quantity");

		//get profile details from excel sheet
		profiles = loadProfile(rowCount);
		for(int i = 0 ; i <  1 ; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) profiles[i]);
			Log.info(temp.get("activeProfile"));
		}
		
		login1.LoginAsUser(driver,userAccessMap.get("LOGIN_ID") , userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherBundleManagement();
		voucherBundleManagementPage.clickAddVoucherBundle();  Log.info(initiateMap.get("voucherBundleName") + " " + initiateMap.get("voucherBundlePrefix"));
		addVoucherBundlePage.enterBundleName(initiateMap.get("voucherBundleName"));
		addVoucherBundlePage.enterBundlePrefix(initiateMap.get("voucherBundlePrefix"));
		for(int i = 0 ; i < 1 ; i++) { //To load vouchers in the bundle
			if(profiles[i] != null) { initiateMap.putAll((Map<? extends String, ? extends String>) profiles[i]); Log.info("VoucherType: " + initiateMap.get("voucherType") + " ; " + "ProfileName: " + initiateMap.get("activeProfile") 	+ "BundleName: " + initiateMap.get("voucherBundleName"));
			try {
		addVoucherBundlePage.selectVoucherTypeByIndex(i,initiateMap.get("voucherType"));
			if(Nation_Voucher == 1)
		addVoucherBundlePage.selectVoucherSegmentByIndex(i,"Local");
		addVoucherBundlePage.selectVoucherDenominationByIndex(i,initiateMap.get("denominationName") + "(" + Integer.toString(Integer.parseInt(initiateMap.get("mrp"))) + ".0)");
		addVoucherBundlePage.selectVoucherProfileByIndex(i,initiateMap.get("activeProfile"));
		addVoucherBundlePage.enterVoucherQuantityByIndex(i,quantity);
		addVoucherBundlePage.clickVoucherValueByIndex(i);
		failureMessage = addVoucherBundlePage.getAlertText();
			}catch(NoSuchElementException e) {}
		}}
		
		
		
		if(failureMessage != null){
			initiateMap.put("MessageStatus","N");
			initiateMap.put("Message",failureMessage);		
		}else{
			String successMessage = addVoucherBundlePage.getMessage();
			initiateMap.put("MessageStatus","Y");
			initiateMap.put("Message",successMessage);		
		}		
		
		return initiateMap;
	}
	//jj>>
	
	public HashMap<String, String> voucherDenomination(HashMap<String, String> initiateMap, String voucherType) {
		
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
		if(voucherType.equalsIgnoreCase("physical"))
		userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.ADD_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
		else if(voucherType.equalsIgnoreCase("electronic"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.ADD_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
		else
			userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.ADD_VOUCHER_DENOMINATION,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherDenomination();
		voucherDenomination.clickAddVoucherDenomination();
		addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		addVoucherDenomination.SelectServiceType(initiateMap.get("service"));
		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
		addVoucherDenomination.SelectSubServiceType(initiateMap.get("subService"));
		addVoucherDenomination.EnterDenomination(initiateMap.get("denominationName"));
		addVoucherDenomination.EnterShortName(initiateMap.get("shortName"));
		addVoucherDenomination.EnterMRP(initiateMap.get("mrp"));
		addVoucherDenomination.EnterPayable(initiateMap.get("payableAmount"));
		addVoucherDenomination.EnterDescription(initiateMap.get("description"));
		addVoucherDenomination.ClickonSubmit();
		addVoucherDenominationPage2.ClickonConfirm();
		String successMessage = addVoucherDenomination.getSuccessMessage();
		if (successMessage != null) {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
		} else {
			initiateMap.put("MessageStatus", "N");
			String failureMessage = addVoucherDenomination.getErrorMessage();
			initiateMap.put("Message", failureMessage);
		}
		return initiateMap;
	}
	
	
	public void writeVoucherBundle(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
		ExcelUtility.setCellData(0, ExcelI.VOMS_BUNDLE_NAME, dataCounter, dataMap.get("voucherBundleName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter, dataMap.get("activeProfile"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_BUNDLE_QUANTITY, dataCounter, dataMap.get("quantity"));
	}

	public void writeDenomination(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		ExcelUtility.setCellData(0, ExcelI.VOMS_DENOMINATION_NAME, dataCounter, dataMap.get("denominationName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_SHORT_NAME, dataCounter, dataMap.get("shortName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_MRP, dataCounter, dataMap.get("mrp"));
	}
	
	public void writeDenominationRevampAPI(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE_API);
		ExcelUtility.setCellData(0, ExcelI.VOMS_DENOMINATION_NAME, dataCounter, dataMap.get("denominationName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_SHORT_NAME, dataCounter, dataMap.get("shortName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_MRP, dataCounter, dataMap.get("mrp"));
	}
	
	public void writeDenominationC2C(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE_C2C);
		ExcelUtility.setCellData(0, ExcelI.VOMS_DENOMINATION_NAME, dataCounter, dataMap.get("denominationName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_SHORT_NAME, dataCounter, dataMap.get("shortName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_MRP, dataCounter, dataMap.get("mrp"));
	}
	public void writeDenominationElectronics(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		ExcelUtility.setCellData(0, ExcelI.VOMS_DENOMINATION_NAME, dataCounter, dataMap.get("denominationName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_SHORT_NAME, dataCounter, dataMap.get("shortName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_MRP, dataCounter, dataMap.get("mrp"));
	}
	
	public void writeProfile(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		ExcelUtility.setCellData(0, ExcelI.VOMS_PAYABLE_AMOUNT, dataCounter, dataMap.get("payableAmount"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter, dataMap.get("profile_name"));
	}


	public void writeProfileC2C(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE_C2C);
		ExcelUtility.setCellData(0, ExcelI.VOMS_PAYABLE_AMOUNT, dataCounter, dataMap.get("payableAmount"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter, dataMap.get("profile_name"));
	}
	

	public void writeProfileElectronic(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		ExcelUtility.setCellData(0, ExcelI.VOMS_PAYABLE_AMOUNT, dataCounter, dataMap.get("payableAmount"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter, dataMap.get("profile_name"));
	}

	public void writeDenominationForPhysical(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		ExcelUtility.setCellData(0, ExcelI.VOMS_DENOMINATION_NAME, dataCounter, dataMap.get("denominationName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_SHORT_NAME, dataCounter, dataMap.get("shortName"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_MRP, dataCounter, dataMap.get("mrp"));
	}
	
	public void writeProfileForPhysical(HashMap<String, String> dataMap, int dataCounter) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		ExcelUtility.setCellData(0, ExcelI.VOMS_PAYABLE_AMOUNT, dataCounter, dataMap.get("payableAmount"));
		ExcelUtility.setCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter, dataMap.get("profile_name"));
	}
	
	
	public HashMap<String, String> addVoucherProfile(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		initiateMap.put("profile_name", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.ADD_VOUCHER_PROFILE, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.ADD_VOUCHER_PROFILE, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.ADD_VOUCHER_PROFILE,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		//if(addVoucherProfile.isVoucherTypeAvailable())
		addVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		//if(addVoucherProfile.isServiceTypeAvailable())
		addVoucherProfile.SelectService(initiateMap.get("service"));
		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
		//if(addVoucherProfile.isSubServiceTypeAvailable())
		addVoucherProfile.SelectSubService(initiateMap.get("subService"));
		if(addVoucherProfile.VisibleMRP()) {
		addVoucherProfile.SelectMRP(initiateMap.get("mrp"));
		}
		addVoucherProfile.EnterProfileName(initiateMap.get("profile_name"));
		addVoucherProfile.EnterShortName(initiateMap.get("shortName"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("false")) {
		addVoucherProfile.EnterMinQuantity(initiateMap.get("minQuantity"));
		addVoucherProfile.EnterMaxQuantity(initiateMap.get("maxQuantity"));
		}
		addVoucherProfile.EnterTalkTime(initiateMap.get("talkTime"));
		addVoucherProfile.EnterValidity(initiateMap.get("validity"));
		String autoVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("true")) {
		addVoucherProfile.ClickAutoGenerate();
		addVoucherProfile.EnterThreshold(initiateMap.get("threshold"));
		addVoucherProfile.EnterQuantity(initiateMap.get("quantity"));
		}
		addVoucherProfile.EnterExpiryPeriod(initiateMap.get("expiryPeriod"));
		addVoucherProfile.EnterProductDescription(initiateMap.get("description"));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
		if(!BTSLUtil.isNullString(value)) {
			if(!value.equalsIgnoreCase("false")){
				addVoucherProfile.EnterOtherInfo1("Info1");
				addVoucherProfile.EnterOtherInfo2("Info2");
			}
		}
		addVoucherProfile.ClickonSubmit();
		addVoucherProfile.ClickonConfirm();
		String successMessage = addVoucherProfile.getMessage();
		if (successMessage != null) {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
		} else {
			initiateMap.put("MessageStatus", "N");
			String failureMessage = addVoucherDenomination.getErrorMessage();
			initiateMap.put("Message", failureMessage);
		}
		return initiateMap;
	}
	
	public HashMap<String, String> addActiveVoucherProfile(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.ADD_ACTIVE_VOUCHER_PROFILE, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.ADD_ACTIVE_VOUCHER_PROFILE, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.ADD_ACTIVE_VOUCHER_PROFILE,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddActiveProfileDetails();
		addActiveProfile.EnterApplicableFromDate(homePage.getDate());
		addActiveProfile.SelectProfileName(initiateMap.get("voucherType"), initiateMap.get("denominationName"), initiateMap.get("mrp"), initiateMap.get("activeProfile"));
		addActiveProfile.ClickonSubmit();
		String failureMessage = addActiveProfile.getErrorMessage();
		if(failureMessage != null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}else {
			addActiveProfilePage2.ClickonConfirm();
			String successMessage = addActiveProfile.getMessage();
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
		}
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationInitiate(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VOMS_ORDER_INITIATION, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VOMS_ORDER_INITIATION, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.VOMS_ORDER_INITIATION,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderInitiate();
		if(voucherGenerationInitiatePage1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		voucherGenerationInitiatePage1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		voucherGenerationInitiatePage1.ClickonSubmit();
		String errorMessage = voucherGenerationInitiatePage1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
	   if(initiateMap.get("denomination")!="")
		voucherGenerationInitiatePage2.SelectDenomination(initiateMap.get("denomination"));
	   if(initiateMap.get("quantity")!="")
		voucherGenerationInitiatePage2.EnterQuantity(initiateMap.get("quantity"));
		voucherGenerationInitiatePage2.EnterRemarks(initiateMap.get("remarks"));
		voucherGenerationInitiatePage2.ClickonSubmit();
		String failureMessage = voucherGenerationInitiatePage2.getErrorMessage();
		if(failureMessage != null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}else {
			voucherGenerationInitiatePage3.SelectProduct(initiateMap.get("activeProfile"));
			voucherGenerationInitiatePage3.ClickonSubmit();
			voucherGenerationInitiatePage4.ClickonConfirm();
			failureMessage = voucherGenerationInitiatePage2.getErrorMessage();
			if(failureMessage != null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else {
			String successMessage = voucherGenerationInitiatePage1.getMessage();
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
		}
		}
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval1(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VOMS_ORDER_APPROVAL1, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VOMS_ORDER_APPROVAL1, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.VOMS_ORDER_APPROVAL1,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval1();
		if(vomsOrderApproval1Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval1Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval1Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval1Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval1Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
		vomsOrderApproval1Page2.ClickonSubmit();
		vomsOrderApproval1Page3.EnterQuantity(initiateMap.get("quantity"));
		vomsOrderApproval1Page3.EnterRemarks(initiateMap.get("remarks"));
		vomsOrderApproval1Page3.ClickonApprove();
		String failureMessage = vomsOrderApproval1Page3.getErrorMessage();
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else
		{
			vomsOrderApproval1Page4.ClickonConfirm();
			String successMessage = vomsOrderApproval1Page1.getMessage();
			if (successMessage != null) {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
			else {
				initiateMap.put("MessageStatus", "N");
				String failureMessage2 = vomsOrderApproval1Page1.getErrorMessage();
				initiateMap.put("Message", failureMessage2);
			}
		}
		}
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval2(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VOMS_ORDER_APPROVAL2, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VOMS_ORDER_APPROVAL2, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.VOMS_ORDER_APPROVAL2,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval2();
		if(vomsOrderApproval2Page1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval2Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval2Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval2Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			if(vomsOrderApproval2Page2.checkParticularProfileAvailable(initiateMap.get("denomination"), initiateMap.get("quantity")))
		vomsOrderApproval2Page2.ClickonSubmit();
		vomsOrderApproval2Page3.EnterQuantity(initiateMap.get("quantity"));
		vomsOrderApproval2Page3.EnterRemarks(initiateMap.get("remarks"));
		vomsOrderApproval2Page3.ClickonApprove();
		String failureMessage = vomsOrderApproval2Page3.getErrorMessage();
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else
		{
			vomsOrderApproval2Page4.ClickonConfirm();
			String successMessage = vomsOrderApproval2Page1.getMessage();
			if (successMessage != null) {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
			else {
				initiateMap.put("MessageStatus", "N");
				String failureMessage2 = vomsOrderApproval2Page1.getErrorMessage();
				initiateMap.put("Message", failureMessage2);
			}
		}
		}
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval3(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VOMS_ORDER_APPROVAL3, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VOMS_ORDER_APPROVAL3, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.VOMS_ORDER_APPROVAL3,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval3();
		if(vomsOrderApproval3Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval3Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval3Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval3Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			vomsOrderApproval3Page2.ClickonSubmit();
			vomsOrderApproval3Page3.EnterQuantity(initiateMap.get("quantity"));
			vomsOrderApproval3Page3.EnterRemarks(initiateMap.get("remarks"));
			vomsOrderApproval3Page3.ClickonApprove();
			String failureMessage = vomsOrderApproval3Page3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				vomsOrderApproval2Page4.ClickonConfirm();
				String successMessage = vomsOrderApproval3Page1.getMessage();
				if (successMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
				}
				else {
					initiateMap.put("MessageStatus", "N");
					String failureMessage2 = vomsOrderApproval3Page1.getErrorMessage();
					initiateMap.put("Message", failureMessage2);
				}
			}
			}
		
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval3uploadDoc(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL3);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval3();
		if(vomsOrderApproval3Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval3Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")
				!="")
		vomsOrderApproval3Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval3Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			vomsOrderApproval3Page2.ClickonSubmit();
			vomsOrderApproval3Page3.EnterQuantity(initiateMap.get("quantity"));
			vomsOrderApproval3Page3.EnterRemarks(initiateMap.get("remarks"));
			vomsOrderApproval3Page3.ClickonChooseDOC();
			vomsOrderApproval3Page3.ClickonApprove();
			String failureMessage = vomsOrderApproval3Page3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				vomsOrderApproval2Page4.ClickonConfirm();
				String successMessage = vomsOrderApproval3Page1.getMessage();
				if (successMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
				}
				else {
					initiateMap.put("MessageStatus", "N");
					String failureMessage2 = vomsOrderApproval3Page1.getErrorMessage();
					initiateMap.put("Message", failureMessage2);
				}
			}
			}
		
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval1uploadDoc(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL3);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval1();
		if(vomsOrderApproval3Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval3Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval3Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval3Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			vomsOrderApproval3Page2.ClickonSubmit();
			vomsOrderApproval3Page3.EnterQuantity(initiateMap.get("quantity"));
			vomsOrderApproval3Page3.EnterRemarks(initiateMap.get("remarks"));
			vomsOrderApproval3Page3.ClickonChooseDOC();
			vomsOrderApproval3Page3.ClickonApprove();
			String failureMessage = vomsOrderApproval3Page3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				vomsOrderApproval2Page4.ClickonConfirm();
				String successMessage = vomsOrderApproval3Page1.getMessage();
				if (successMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
				}
				else {
					initiateMap.put("MessageStatus", "N");
					String failureMessage2 = vomsOrderApproval3Page1.getErrorMessage();
					initiateMap.put("Message", failureMessage2);
				}
			}
			}
		
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval2uploadDoc(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL3);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval2();
		if(vomsOrderApproval3Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval3Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval3Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval3Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			vomsOrderApproval3Page2.ClickonSubmit();
			vomsOrderApproval3Page3.EnterQuantity(initiateMap.get("quantity"));
			vomsOrderApproval3Page3.EnterRemarks(initiateMap.get("remarks"));
			vomsOrderApproval3Page3.ClickonChooseDOC();
			vomsOrderApproval3Page3.ClickonApprove();
			String failureMessage = vomsOrderApproval3Page3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				vomsOrderApproval2Page4.ClickonConfirm();
				String successMessage = vomsOrderApproval3Page1.getMessage();
				if (successMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
				}
				else {
					initiateMap.put("MessageStatus", "N");
					String failureMessage2 = vomsOrderApproval3Page1.getErrorMessage();
					initiateMap.put("Message", failureMessage2);
				}
			}
			}
		
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval3uploadDocx(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL3);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval3();
		if(vomsOrderApproval3Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval3Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval3Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval3Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			vomsOrderApproval3Page2.ClickonSubmit();
			vomsOrderApproval3Page3.EnterQuantity(initiateMap.get("quantity"));
			vomsOrderApproval3Page3.EnterRemarks(initiateMap.get("remarks"));
			vomsOrderApproval3Page3.ClickonChooseDOCX();
			vomsOrderApproval3Page3.ClickonApprove();
			String failureMessage = vomsOrderApproval3Page3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				vomsOrderApproval2Page4.ClickonConfirm();
				String successMessage = vomsOrderApproval3Page1.getMessage();
				if (successMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
				}
				else {
					initiateMap.put("MessageStatus", "N");
					String failureMessage2 = vomsOrderApproval3Page1.getErrorMessage();
					initiateMap.put("Message", failureMessage2);
				}
			}
			}
		
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval3uploadPdf(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL3);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval3();
		if(vomsOrderApproval3Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval3Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval3Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval3Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			vomsOrderApproval3Page2.ClickonSubmit();
			vomsOrderApproval3Page3.EnterQuantity(initiateMap.get("quantity"));
			vomsOrderApproval3Page3.EnterRemarks(initiateMap.get("remarks"));
			vomsOrderApproval3Page3.ClickonChoosePDF();
			vomsOrderApproval3Page3.ClickonApprove();
			String failureMessage = vomsOrderApproval3Page3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				vomsOrderApproval2Page4.ClickonConfirm();
				String successMessage = vomsOrderApproval3Page1.getMessage();
				if (successMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
				}
				else {
					initiateMap.put("MessageStatus", "N");
					String failureMessage2 = vomsOrderApproval3Page1.getErrorMessage();
					initiateMap.put("Message", failureMessage2);
				}
			}
			}
		
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval3uploadGIF(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL3);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval3();
		if(vomsOrderApproval3Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval3Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}		
		if(initiateMap.get("productID")!="")
		vomsOrderApproval3Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval3Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			vomsOrderApproval3Page2.ClickonSubmit();
			vomsOrderApproval3Page3.EnterQuantity(initiateMap.get("quantity"));
			vomsOrderApproval3Page3.EnterRemarks(initiateMap.get("remarks"));
			vomsOrderApproval3Page3.ClickonChooseGIF();
			vomsOrderApproval3Page3.ClickonApprove();
			String failureMessage = vomsOrderApproval3Page3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				vomsOrderApproval2Page4.ClickonConfirm();
				String successMessage = vomsOrderApproval3Page1.getMessage();
				if (successMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
				}
				else {
					initiateMap.put("MessageStatus", "N");
					String failureMessage2 = vomsOrderApproval3Page1.getErrorMessage();
					initiateMap.put("Message", failureMessage2);
				}
			}
			}
		
		return initiateMap;
	}
	
	
	public void voucherGenerationScriptExecution()
	{
		Log.info("Trying to execute Script");
		SSHService.executeScript("VoucherGenerator.sh");
		Log.info("Script executed successfully");
	}
	
	public HashMap<String, String> createBatchForVoucherDownload(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.CREATE_BATCH_FOR_VOUCHER_DOWNLOAD, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.CREATE_BATCH_FOR_VOUCHER_DOWNLOAD, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.CREATE_BATCH_FOR_VOUCHER_DOWNLOAD);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickVoucherDownload();
		voucherDownloadSubCategories.clickCreateBatchForVoucherDownload();
		if(initiateMap.get("voucherType")!="")
		createBatchForVoucherDownload.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		createBatchForVoucherDownload.SelectCreateBatchFor(initiateMap.get("batchType"));
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("false")) {
		if(initiateMap.get("denomination")!="")
		createBatchForVoucherDownload.SelectDenomination(initiateMap.get("denomination"));
		if(initiateMap.get("quantity")!="")
		createBatchForVoucherDownload.EnterQuantity(initiateMap.get("quantity"));
		if(initiateMap.get("remarks")!="")
		createBatchForVoucherDownload.EnterRemarks(initiateMap.get("remarks"));
		}
		else {
			if(initiateMap.get("denomination")!="")
				createBatchForVoucherDownload.SelectDenomination(initiateMap.get("denomination"));
			String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String batchID = DBHandler.AccessHandler.getBatchNumber(productID, "EX");
			if(initiateMap.get("denomination")!="")
				createBatchForVoucherDownload.SelectBatchID(batchID);
			if(initiateMap.get("remarks")!="")
				createBatchForVoucherDownload.EnterRemarks(initiateMap.get("remarks"));
		}
		createBatchForVoucherDownload.ClickonSubmit();
		String failureMessage = createBatchForVoucherDownload.getErrorMessage();
		if(failureMessage != null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}else {
			if(defaultValue.equalsIgnoreCase("false")) {
			createBatchForVoucherDownloadPage2.SelectProductID(initiateMap.get("activeProfile"));
			}
			createBatchForVoucherDownloadPage2.ClickonSubmit();
			createBatchForVoucherDownloadPage3.ClickonConfirm();
			String successMessage = createBatchForVoucherDownload.getMessage();
			String FailMessage = createBatchForVoucherDownload.getErrorMessage();
			if(FailMessage != null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", successMessage);
			}
			else {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
		}
		return initiateMap;
	}
	
	public HashMap<String, String> vomsVoucherDownload(HashMap<String, String> initiateMap, String voucherType)  {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_VOUCHER_DOWNLOAD);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickVoucherDownload();
		voucherDownloadSubCategories.clickVomsVoucherDownload();
		vomsDownload.SelectBatchType(initiateMap.get("viewBatchFor"));
		String toDate = homePage.getDate();
		String fromDate = homePage.addDaysToCurrentDate(toDate, -30);
		vomsDownload.EnterFromDate(fromDate);
		vomsDownload.EnterToDate(toDate);
		vomsDownload.ClickonSubmit();
		String failureMessage = vomsDownload.getMessage();
		if(failureMessage != null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}else {
			
			vomsDownloadPage2.SelectBatchToDownload(initiateMap.get("activeProfile"));
			vomsDownloadPage3.SelectLink();
			String successMessage = "Voms Batch Downloaded Successfully";
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
		}
		batchDetails.switchwindow();
		batchDetails.popUpCLose();
		batchDetails.firstWindow();
		return initiateMap;
	}
	
	public HashMap<String, String> voucherDownloadView(HashMap<String, String> initiateMap, String voucherType)  {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_VOUCHER_DOWNLOAD);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickVoucherDownload();
		voucherDownloadSubCategories.clickVoucherDownload();
		voucherDownloadPage1.enterUserCode(initiateMap.get("msisdn"));
		voucherDownloadPage1.selectTransferCategoryNew(PretupsI.TRANSFER_CATEGORY_SALE);
		voucherDownloadPage1.clickSubmitButton();
		voucherDownloadPage1.selectTransferNum(initiateMap.get("transactionID"));
		voucherDownloadPage1.clickViewButton();
		if(voucherDownloadPage2.getPageHeading() != null)
			initiateMap.put("MessageStatus", "Y");
		else
			initiateMap.put("MessageStatus", "N");
		return initiateMap;
	}
	
	public HashMap<String, String> voucherDownloadFileNeg(HashMap<String, String> initiateMap, String voucherType)  {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_VOUCHER_DOWNLOAD);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickVoucherDownload();
		voucherDownloadSubCategories.clickVoucherDownload();
		voucherDownloadPage1.enterUserCode(initiateMap.get("msisdn"));
		voucherDownloadPage1.selectTransferCategoryNew(PretupsI.TRANSFER_CATEGORY_SALE);
		voucherDownloadPage1.clickSubmitButton();
		voucherDownloadPage1.selectTransferNum(initiateMap.get("transactionID"));
		voucherDownloadPage1.clickDownloadDetails();
		if(voucherDownloadPage1.getErrorMessage() != null)	{
			initiateMap.put("MessageStatus", "N");
		}
		else
			initiateMap.put("MessageStatus", "Y");
		return initiateMap;
	}
	
	public HashMap<String, String> voucherDownloadFile(HashMap<String, String> initiateMap, String voucherType)  {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_VOUCHER_DOWNLOAD);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickVoucherDownload();
		voucherDownloadSubCategories.clickVoucherDownload();
		voucherDownloadPage1.enterUserCode(initiateMap.get("msisdn"));
		voucherDownloadPage1.selectTransferCategoryNew(PretupsI.TRANSFER_CATEGORY_SALE);
		voucherDownloadPage1.clickSubmitButton();
		voucherDownloadPage1.selectTransferNum(initiateMap.get("transactionID"));
		voucherDownloadPage1.clickDownloadDetails();
		initiateMap.put("MessageStatus", "Y");
		return initiateMap;
	}
	
	public HashMap<String, String> voucherDownloadFTPFile(HashMap<String, String> initiateMap, String voucherType)  {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_VOUCHER_DOWNLOAD);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickVoucherDownload();
		voucherDownloadSubCategories.clickVoucherDownload();
		voucherDownloadPage1.enterUserCode(initiateMap.get("msisdn"));
		voucherDownloadPage1.selectTransferCategoryNew(PretupsI.TRANSFER_CATEGORY_SALE);
		voucherDownloadPage1.clickSubmitButton();
		voucherDownloadPage1.selectTransferNum(initiateMap.get("transactionID"));
		voucherDownloadPage1.clickFtpFile();
		if(voucherDownloadPage1.getMessage() != null){
			String message = voucherDownloadPage1.getMessage();
			initiateMap.put("SuccessMessage", message);
			initiateMap.put("MessageStatus", "Y");
		} else {
			initiateMap.put("MessageStatus", "N");
		}
		
		return initiateMap;
	}
	
	public HashMap<String, String> changeOtherStatus(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.CHANGE_OTHER_STATUS, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.CHANGE_OTHER_STATUS, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.CHANGE_OTHER_STATUS,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherDenomination();
		voucherDenomination.clickChangeOtherStatus();
		if(changeOtherStatus.isVoucherTypeAvailable()) {
		changeOtherStatus.SelectVoucherType(initiateMap.get("voucherType"));
		}
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productID);
		String fromSerialNumber = VOMSData[0];
		String toSerialNumber = VOMSData[1];
		String numberOfVouchers = VOMSData[2];
		changeOtherStatus.EnterFromSerial(fromSerialNumber);
		changeOtherStatus.EnterToSerial(toSerialNumber);
		changeOtherStatus.EnterNumberOfVouchers(numberOfVouchers);
		changeOtherStatus.EnterMRP(initiateMap.get("mrp"));
		changeOtherStatus.SelectProductID(initiateMap.get("activeProfile"));
		changeOtherStatus.SelectVoucherStatus(initiateMap.get("voucherStatus"));
		changeOtherStatus.ClickonSubmit();
		String failureMessage = vomsDownload.getMessage();
		if(failureMessage != null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}else {
			changeOtherStatusPage2.ClickonConfirm();
			String successMessage = changeOtherStatus.getMessage();
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
		}
		return initiateMap;
	}
	
	public HashMap<String, String> checkO2CStatus(HashMap<String, String> initiateMap) throws InterruptedException {
		Log.info("Trying to check Voucher Status");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productID);
		String fromSerialNumber = initiateMap.get("fromSerialNumber");
		String toSerialNumber = initiateMap.get("toSerialNumber");
        String numberOfVouchers = VOMSData[2];
        String currentStatus = DBHandler.AccessHandler.getVoucherStatus(fromSerialNumber);
		initiateMap.put("currentStatus", currentStatus);
		return initiateMap;
		}
	
	public HashMap<String, String> voucherExpiryExtension(HashMap<String, String> initiateMap) throws InterruptedException {
		Log.info("Trying to check Expiry Date After Voucher Expiry Extension API Execution");
		String currDate = homePage.getDate();
		String newExpiryDate = homePage.addDaysToCurrentDate(currDate, 30);
		String oldExpiryDate = homePage.addDaysToCurrentDate(currDate, 90);
		initiateMap.put("currDate", currDate);
		initiateMap.put("newExpiryDate", newExpiryDate);
		initiateMap.put("oldExpiryDate", oldExpiryDate);
		return initiateMap;
		}
	
	public HashMap<String, String> voucherConsumption(HashMap<String, String> initiateMap) throws InterruptedException {
		Log.info("Trying to check Voucher Status After Consumption");
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(initiateMap.get("serialNumber"));
		initiateMap.put("currentStatus", currentStatus);
		return initiateMap;
		}
	
	public void voucherBurnRateScript()
	{
		Log.info("Trying to execute Voucher Burn Rate Script");
		SSHService.executeScript("VoucherBurnRateSummary.sh");
		Log.info("Voucher Burn Rate Script executed successfully");
	}

    public HashMap<String, String> voucherBurnRate(HashMap<String, String> initiateMap) throws InterruptedException, ParseException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_BURN_RATE_INDICATOR);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherReports();
		voucherReports.clickVoucherBurnRateIndicator();
		String currentDate = homePage.getDate();
		String fromDate = homePage.addDaysToCurrentDate(currentDate, -30);
		String toDate = homePage.getDate();
		boolean dataExist = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		voucherBurnRateIndicator.SelectVoucherDenomination(initiateMap.get("denominationName"));
		voucherBurnRateIndicator.SelectVoucherProfile(initiateMap.get("activeProfile"));
		voucherBurnRateIndicator.EnterDistributedFromDate(fromDate);
		voucherBurnRateIndicator.EnterDistributedToDate(toDate);
		voucherBurnRateIndicator.EnterConsumedFromDate(fromDate);
		voucherBurnRateIndicator.EnterConsumedToDate(toDate);
		voucherBurnRateIndicator.ClickonSubmit();
		String summaryDateQuery = DBHandler.AccessHandler.getVoucherSummaryDate();
		Date date_new = null;
		date_new = new SimpleDateFormat("y-M-d").parse(summaryDateQuery);
		String summaryDate = dateFormat.format(date_new);
		summaryDate = BTSLDateUtil.getSystemLocaleDate(summaryDate);
		String prevDate = homePage.addDaysToCurrentDate(currentDate, -1);
		if(prevDate.equalsIgnoreCase(summaryDate))
		{
			dataExist = true;
			String displayedProfile = voucherBurnRateIndicator.fetchDisplayedProfile();
			if(displayedProfile.equalsIgnoreCase(initiateMap.get("activeProfile")))
			{
				initiateMap.put("MessageStatus", "Y");
				String successMessage = "Profile getting displayed";
				initiateMap.put("Message", successMessage);
			}
			else
			{
				initiateMap.put("MessageStatus", "N");
				String failureMessage = "Profile not found";
				initiateMap.put("Message", failureMessage);
			}
		}
		else
		{
			String successMessage = voucherBurnRateIndicator.getSuccessMessage();
			if (successMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
			} else {
				initiateMap.put("MessageStatus", "N");
				String failureMessage = voucherBurnRateIndicator.getErrorMessage();
				initiateMap.put("Message", failureMessage);
			}
		}
		ExtentI.attachScreenShot();
		return initiateMap;
		}
	
    public HashMap<String, String> autoVoucherPreference(String scenario)
	{
    	HashMap<String, String> initiateMap = new HashMap<String, String>();
    	userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
    	login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickPreferences();
		systemPreferencePage.selectModule("C2S");
		systemPreferencePage.selectPreferenceType(PretupsI.SYSTEM_PREFERENCE_TYPE);
		systemPreferencePage.clickSubmitButton();
		String preferenceCode = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		boolean isPreferenceDisplayed = systemPreferencePage.isPreferenceDisplayed(preferenceCode);
		boolean isModifiedAllowed = systemPreferencePage.isModifiedAllowed(preferenceCode);
		if (scenario.equalsIgnoreCase("systemPreferenceVerification") && isPreferenceDisplayed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if (scenario.equalsIgnoreCase("isModifyAllowed") && isModifiedAllowed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if (scenario.equalsIgnoreCase("modifySystemPreference")) {
			systemPreferencePage.setValueofSystemPreference(preferenceCode, "false");
			systemPreferencePage.clickModifyBtn();
			systemPreferencePage.clickConfirmBtn();
			updateCache.updateCache();
			String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
			if(defaultValue.equalsIgnoreCase("false"))
			initiateMap.put("MessageStatus", "Y");
		}
		else if (scenario.equalsIgnoreCase("modifyAlphaNumericValue")) {
			systemPreferencePage.setValueofSystemPreference(preferenceCode, "alpha008");
			systemPreferencePage.clickModifyBtn();
			String failureMessage = systemPreferencePage.getErrorMessage();
			if(failureMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", failureMessage);
			}
			else {
				initiateMap.put("MessageStatus", "N");
			}
		} 
		/*systemPreferencePage.setValueofSystemPreference(preferenceCode1, trueOrfalse);
		systemPreferencePage.clickModifyBtn();
		systemPreferencePage.clickConfirmBtn();*/
		return initiateMap;
	}
    
    public void enableAutoVoucher()
	{
    	userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
    	login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickPreferences();
		systemPreferencePage.selectModule("C2S");
		systemPreferencePage.selectPreferenceType(PretupsI.SYSTEM_PREFERENCE_TYPE);
		systemPreferencePage.clickSubmitButton();
		String preferenceCode = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		systemPreferencePage.setValueofSystemPreference(preferenceCode, "true");
		systemPreferencePage.clickModifyBtn();
		systemPreferencePage.clickConfirmBtn();
		updateCache.updateCache();
	}
    
    public HashMap<String, String> autoVoucher(HashMap<String, String> initiateMap) throws InterruptedException {
    	userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		boolean isAutoVoucherDisplayed = addVoucherProfile.ClickAutoGenerate();
		String autoVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("true") && isAutoVoucherDisplayed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("false") && !isAutoVoucherDisplayed)
		{
			initiateMap.put("MessageStatus", "Y");
		}
		else
			initiateMap.put("MessageStatus", "N");
		
		return initiateMap;
    }
    
    public HashMap<String, String> modifyVoucherProfile(HashMap<String, String> initiateMap, String scenario, String voucherType) throws InterruptedException {
		initiateMap.put("profile_name", UniqueChecker.UC_VOMS_ProfileName());
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.MODIFY_VOUCHER_PROFILE, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.MODIFY_VOUCHER_PROFILE, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickModifyVoucherProfile();
		modifyVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		modifyVoucherProfile.ClickonSubmit();
		modifyVoucherProfilePage2.SelectProfileToModify(initiateMap.get("activeProfile"));
		modifyVoucherProfilePage2.ClickonSubmit();
		boolean isAutoVoucherDisplayed = modifyVoucherProfilePage3.ClickAutoGenerate();
		String autoVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		if(scenario.equalsIgnoreCase("preference")) {
		if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("true") && isAutoVoucherDisplayed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("false") && !isAutoVoucherDisplayed)
		{
			initiateMap.put("MessageStatus", "Y");
		}
		else
			initiateMap.put("MessageStatus", "N");
		}
		else if(scenario.equalsIgnoreCase("fieldValidation") && autoVoucherAllowed.equalsIgnoreCase("true"))
		{
			modifyVoucherProfilePage3.ClickAutoGenerate();
			modifyVoucherProfilePage3.EnterThreshold(initiateMap.get("threshold"));
			modifyVoucherProfilePage3.EnterQuantity(initiateMap.get("quantity"));
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
			if(!BTSLUtil.isNullString(value)) {
				if(!value.equalsIgnoreCase("false")){
					addVoucherProfile.EnterOtherInfo1("Info1");
					addVoucherProfile.EnterOtherInfo2("Info2");
				}
			}
			modifyVoucherProfilePage3.ClickonModify();
			String failureMessage = modifyVoucherProfilePage3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				modifyVoucherProfilePage3.ClickonConfirm();
				String successMessage = modifyVoucherProfilePage3.getMessage();
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
			}
		}
		else if(scenario.equalsIgnoreCase("suspended") && autoVoucherAllowed.equalsIgnoreCase("true"))
		{
			modifyVoucherProfilePage3.selectStatus("S");
			modifyVoucherProfilePage3.ClickAutoGenerate();
			modifyVoucherProfilePage3.EnterThreshold(initiateMap.get("threshold"));
			modifyVoucherProfilePage3.EnterQuantity(initiateMap.get("quantity"));
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
			if(!BTSLUtil.isNullString(value)) {
				if(!value.equalsIgnoreCase("false")){
					addVoucherProfile.EnterOtherInfo1("Info1");
					addVoucherProfile.EnterOtherInfo2("Info2");
				}
			}
			modifyVoucherProfilePage3.ClickonModify();
			String failureMessage = modifyVoucherProfilePage3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				modifyVoucherProfilePage3.ClickonConfirm();
				failureMessage = modifyVoucherProfilePage3.getErrorMessage();
				if(failureMessage!=null)
				{
					initiateMap.put("MessageStatus", "N");
					initiateMap.put("Message", failureMessage);
				}
				else {
					saHomePage.clickVoucherGeneration();
					voucherGeneration.clickVomsOrderInitiate();
					if (voucherGenerationInitiatePage1.isVoucherTypeAvailable()) {
						if (initiateMap.get("voucherType") != "") {
							voucherGenerationInitiatePage1.SelectVoucherType(initiateMap.get("voucherType"));
						}
						if (Nation_Voucher == 1) {
							if(addVoucherDenomination.isSegmentAvailable())
								addVoucherDenomination.SelectVoucherSegment(voucherSegment);
						}
						voucherGenerationInitiatePage1.ClickonSubmit();
					}
					String errorMessage = voucherGenerationInitiatePage1.getErrorMessage();
					if(errorMessage!=null)
					{
						initiateMap.put("MessageStatus", "N");
						initiateMap.put("Message", errorMessage);
					}
					else {

						if (initiateMap.get("denomination") != "")
							voucherGenerationInitiatePage2.SelectDenomination(initiateMap.get("denomination"));
						if (initiateMap.get("quantity") != "")
							voucherGenerationInitiatePage2.EnterQuantity(initiateMap.get("quantity"));
						voucherGenerationInitiatePage2.EnterRemarks(initiateMap.get("remarks"));
						voucherGenerationInitiatePage2.ClickonSubmit();
						failureMessage = voucherGenerationInitiatePage2.getErrorMessage();
						if(failureMessage != null)
						{
							initiateMap.put("MessageStatus", "N");
							initiateMap.put("Message", failureMessage);
						}else {
							String successMessage = voucherGenerationInitiatePage1.getMessage();
							initiateMap.put("MessageStatus", "Y");
							initiateMap.put("Message", successMessage);
						}
					}
				}	
			}	
		}
		return initiateMap;
	}
    
    public HashMap<String, String> modifyVoucherProfileSuspended(HashMap<String, String> initiateMap, String scenario, String voucherType) throws InterruptedException {
		initiateMap.put("profile_name", UniqueChecker.UC_VOMS_ProfileName());
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.MODIFY_VOUCHER_PROFILE, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.MODIFY_VOUCHER_PROFILE, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickModifyVoucherProfile();
		modifyVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		modifyVoucherProfile.ClickonSubmit();
		modifyVoucherProfilePage2.SelectProfileToModify(initiateMap.get("activeProfile"));
		modifyVoucherProfilePage2.ClickonSubmit();
		boolean isAutoVoucherDisplayed = modifyVoucherProfilePage3.ClickAutoGenerate();
		String autoVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		if(scenario.equalsIgnoreCase("preference")) {
		if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("true") && isAutoVoucherDisplayed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("false") && !isAutoVoucherDisplayed)
		{
			initiateMap.put("MessageStatus", "Y");
		}
		else
			initiateMap.put("MessageStatus", "N");
		}
		else if(scenario.equalsIgnoreCase("fieldValidation") && autoVoucherAllowed.equalsIgnoreCase("true"))
		{
			modifyVoucherProfilePage3.ClickAutoGenerate();
			modifyVoucherProfilePage3.EnterThreshold(initiateMap.get("threshold"));
			modifyVoucherProfilePage3.EnterQuantity(initiateMap.get("quantity"));
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
			if(!BTSLUtil.isNullString(value)) {
				if(!value.equalsIgnoreCase("false")){
					addVoucherProfile.EnterOtherInfo1("Info1");
					addVoucherProfile.EnterOtherInfo2("Info2");
				}
			}
			modifyVoucherProfilePage3.ClickonModify();
			String failureMessage = modifyVoucherProfilePage3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				modifyVoucherProfilePage3.ClickonConfirm();
				String successMessage = modifyVoucherProfilePage3.getMessage();
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
			}
		}
		else if(scenario.equalsIgnoreCase("suspended") && autoVoucherAllowed.equalsIgnoreCase("true"))
		{
			modifyVoucherProfilePage3.selectStatus("S");
			modifyVoucherProfilePage3.ClickAutoGenerate();
			modifyVoucherProfilePage3.EnterThreshold(initiateMap.get("threshold"));
			modifyVoucherProfilePage3.EnterQuantity(initiateMap.get("quantity"));
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
			if(!BTSLUtil.isNullString(value)) {
				if(!value.equalsIgnoreCase("false")){
					addVoucherProfile.EnterOtherInfo1("Info1");
					addVoucherProfile.EnterOtherInfo2("Info2");
				}
			}
			modifyVoucherProfilePage3.ClickonModify();
			String failureMessage = modifyVoucherProfilePage3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				modifyVoucherProfilePage3.ClickonConfirm();
				failureMessage = modifyVoucherProfilePage3.getErrorMessage();
				if(failureMessage!=null)
				{
					initiateMap.put("MessageStatus", "N");
					initiateMap.put("Message", failureMessage);
				}
				else {
					saHomePage.clickVoucherGeneration();
					voucherGeneration.clickVomsOrderInitiate();
					if (voucherGenerationInitiatePage1.isVoucherTypeAvailable()) {
						if (initiateMap.get("voucherType") != "") {
							voucherGenerationInitiatePage1.SelectVoucherType(initiateMap.get("voucherType"));
						}
						if (Nation_Voucher == 1) {
							if(addVoucherDenomination.isSegmentAvailable())
								addVoucherDenomination.SelectVoucherSegment(voucherSegment);
						}
						voucherGenerationInitiatePage1.ClickonSubmit();
					}
					String errorMessage = voucherGenerationInitiatePage1.getErrorMessage();
					if(errorMessage!=null)
					{
						initiateMap.put("MessageStatus", "N");
						initiateMap.put("Message", errorMessage);
					}
					else {

						if (initiateMap.get("denomination") != "")
							voucherGenerationInitiatePage2.SelectDenomination(initiateMap.get("denomination"));
						if (initiateMap.get("quantity") != "")
							voucherGenerationInitiatePage2.EnterQuantity(initiateMap.get("quantity"));
						voucherGenerationInitiatePage2.EnterRemarks(initiateMap.get("remarks"));
						voucherGenerationInitiatePage2.ClickonSubmit();
						failureMessage = voucherGenerationInitiatePage2.getErrorMessage();
						if(failureMessage != null)
						{
							initiateMap.put("MessageStatus", "N");
							initiateMap.put("Message", failureMessage);
						}else {
							String successMessage = voucherGenerationInitiatePage1.getMessage();
							initiateMap.put("MessageStatus", "Y");
							initiateMap.put("Message", successMessage);
						}
						
						if(initiateMap.get("productID")!="") {
							boolean flag = voucherGenerationInitiatePage3.availabilityofProduct(initiateMap.get("activeProfile"));
							
							
							if(!flag) {
								String successMessage = "Product ID not found";
								initiateMap.put("MessageStatus", "N");
								initiateMap.put("Message", successMessage);
							}
							else {
								failureMessage = "Product ID found";
								initiateMap.put("MessageStatus", "Y");
								initiateMap.put("Message", failureMessage);
							}
						}
					
					}
				}	
			}	
		}
		return initiateMap;
	}
    
    public HashMap<String, String> addVoucherProfileNegative(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		String currDate = homePage.getDate();
		String previousDate = homePage.addDaysToCurrentDate(currDate, -5);
		if(addVoucherProfile.isVoucherTypeAvailable())
		addVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(addVoucherProfile.isServiceTypeAvailable())
		addVoucherProfile.SelectService(initiateMap.get("service"));
		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
		if(addVoucherProfile.isSubServiceTypeAvailable())
		addVoucherProfile.SelectSubService(initiateMap.get("subService"));
		if(initiateMap.get("mrp")!="")
		addVoucherProfile.SelectMRP(initiateMap.get("mrp"));
		addVoucherProfile.EnterProfileName(initiateMap.get("activeProfile"));
		addVoucherProfile.EnterShortName(initiateMap.get("shortName"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("false")) {
		addVoucherProfile.EnterMinQuantity(initiateMap.get("minQuantity"));
		addVoucherProfile.EnterMaxQuantity(initiateMap.get("maxQuantity"));
		}
		addVoucherProfile.EnterTalkTime(initiateMap.get("talkTime"));
		addVoucherProfile.EnterValidity(initiateMap.get("validity"));
		String autoVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("true")) {
		addVoucherProfile.ClickAutoGenerate();
		addVoucherProfile.EnterThreshold(initiateMap.get("threshold"));
		addVoucherProfile.EnterQuantity(initiateMap.get("quantity"));
		}
		if(initiateMap.get("scenario").equalsIgnoreCase("ExpiryPeriod equal to Current Date"))
			addVoucherProfile.EnterExpiryPeriod(currDate);
		else if(initiateMap.get("scenario").equalsIgnoreCase("ExpiryPeriod less than Current Date"))
			addVoucherProfile.EnterExpiryPeriod(previousDate);
		else
		addVoucherProfile.EnterExpiryPeriod(initiateMap.get("expiryPeriod"));
		addVoucherProfile.EnterProductDescription(initiateMap.get("description"));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
		if(!BTSLUtil.isNullString(value)) {
			if(!value.equalsIgnoreCase("false")){
				addVoucherProfile.EnterOtherInfo1("Info1");
				addVoucherProfile.EnterOtherInfo2("Info2");
			}
		}	
		addVoucherProfile.ClickonSubmit();
		String failureMessage = addVoucherDenomination.getErrorMessage();
		boolean isThresholdDisplayed = addVoucherProfile.isEnteredThresholdValueDisplayed(initiateMap.get("threshold"));
		boolean isQuantityDisplayed = addVoucherProfile.isEnteredQuantityValueDisplayed(initiateMap.get("quantity"));
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else if(isThresholdDisplayed && isQuantityDisplayed)
		{
			addVoucherProfile.ClickonConfirm();
			String successMessage = addVoucherProfile.getMessage();
			if(successMessage==null)
			{
				String errorMessage = addVoucherProfile.getErrorMessage();
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", errorMessage);
			}
			else
			{
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
		}
		else
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", "Threshold and Quantity exceeding maxLimit");
		}
		return initiateMap;
	}
    
    public HashMap<String, String> addVoucherProfileNegativeTalktime(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		String currDate = homePage.getDate();
		String previousDate = homePage.addDaysToCurrentDate(currDate, -5);
		//if(addVoucherProfile.isVoucherTypeAvailable())
		addVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		//if(addVoucherProfile.isServiceTypeAvailable())
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		addVoucherProfile.SelectService(initiateMap.get("service"));
		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
		//if(addVoucherProfile.isSubServiceTypeAvailable())
		addVoucherProfile.SelectSubService(initiateMap.get("subService"));
		if(initiateMap.get("mrp")!="")
		addVoucherProfile.SelectMRP(initiateMap.get("mrp"));
		addVoucherProfile.EnterProfileName(initiateMap.get("activeProfile"));
		addVoucherProfile.EnterShortName(initiateMap.get("shortName"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("false")) {
		addVoucherProfile.EnterMinQuantity(initiateMap.get("minQuantity"));
		addVoucherProfile.EnterMaxQuantity(initiateMap.get("maxQuantity"));
		}
		String vomsProfileTalktime = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROF_TALKTIME_MANDATORY);
		if(vomsProfileTalktime.equalsIgnoreCase("true")) {
		addVoucherProfile.EnterTalkTime(initiateMap.get("talkTime"));
		}
		String vomsProfileValidity = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROF_VALIDITY_MANDATORY);
		if(vomsProfileValidity.equalsIgnoreCase("true")) {
		addVoucherProfile.EnterValidity(initiateMap.get("validity"));
		}
		String autoVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("true")) {
		addVoucherProfile.ClickAutoGenerate();
		addVoucherProfile.EnterThreshold(initiateMap.get("threshold"));
		addVoucherProfile.EnterQuantity(initiateMap.get("quantity"));
		}
		if(initiateMap.get("scenario").equalsIgnoreCase("ExpiryPeriod equal to Current Date"))
			addVoucherProfile.EnterExpiryPeriod(currDate);
		else if(initiateMap.get("scenario").equalsIgnoreCase("ExpiryPeriod less than Current Date"))
			addVoucherProfile.EnterExpiryPeriod(previousDate);
		else
		addVoucherProfile.EnterExpiryPeriod(initiateMap.get("expiryPeriod"));
		addVoucherProfile.EnterProductDescription(initiateMap.get("description"));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
		if(!BTSLUtil.isNullString(value)) {
			if(!value.equalsIgnoreCase("false")){
				addVoucherProfile.EnterOtherInfo1("Info1");
				addVoucherProfile.EnterOtherInfo2("Info2");
			}
		}	
		addVoucherProfile.ClickonSubmit();
		String failureMessage = addVoucherDenomination.getErrorMessage();
		boolean isThresholdDisplayed = addVoucherProfile.isEnteredThresholdValueDisplayed(initiateMap.get("threshold"));
		boolean isQuantityDisplayed = addVoucherProfile.isEnteredQuantityValueDisplayed(initiateMap.get("quantity"));
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else if(isThresholdDisplayed && isQuantityDisplayed)
		{
			addVoucherProfile.ClickonConfirm();
			String successMessage = addVoucherProfile.getMessage();
			if(successMessage==null)
			{
				String errorMessage = addVoucherProfile.getErrorMessage();
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", errorMessage);
			}
			else
			{
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
		}
		else
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", "Threshold and Quantity exceeding maxLimit");
		}
		return initiateMap;
	}
    
    public HashMap<String, String> addVoucherProfileNegativeNA(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.ADD_VOUCHER_PROFILE,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		String currDate = homePage.getDate();
		String previousDate = homePage.addDaysToCurrentDate(currDate, -5);
		//if(addVoucherProfile.isVoucherTypeAvailable())
		addVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		//if(addVoucherProfile.isServiceTypeAvailable())
		addVoucherProfile.SelectService(initiateMap.get("service"));
		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
		//if(addVoucherProfile.isSubServiceTypeAvailable())
		addVoucherProfile.SelectSubService(initiateMap.get("subService"));
		if(initiateMap.get("mrp")!="")
		addVoucherProfile.SelectMRP(initiateMap.get("mrp"));
		addVoucherProfile.EnterProfileName(initiateMap.get("activeProfile"));
		addVoucherProfile.EnterShortName(initiateMap.get("shortName"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("false")) {
		addVoucherProfile.EnterMinQuantity(initiateMap.get("minQuantity"));
		addVoucherProfile.EnterMaxQuantity(initiateMap.get("maxQuantity"));
		}
		addVoucherProfile.EnterTalkTime(initiateMap.get("talkTime"));
		addVoucherProfile.EnterValidity(initiateMap.get("validity"));
		String autoVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("true")) {
		addVoucherProfile.ClickAutoGenerate();
		addVoucherProfile.EnterThreshold(initiateMap.get("threshold"));
		addVoucherProfile.EnterQuantity(initiateMap.get("quantity"));
		}
		if(initiateMap.get("scenario").equalsIgnoreCase("ExpiryPeriod equal to Current Date"))
			addVoucherProfile.EnterExpiryPeriod(currDate);
		else if(initiateMap.get("scenario").equalsIgnoreCase("ExpiryPeriod less than Current Date"))
			addVoucherProfile.EnterExpiryPeriod(previousDate);
		else
		addVoucherProfile.EnterExpiryPeriod(initiateMap.get("expiryPeriod"));
		addVoucherProfile.EnterProductDescription(initiateMap.get("description"));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
		if(!BTSLUtil.isNullString(value)) {
			if(!value.equalsIgnoreCase("false")){
				addVoucherProfile.EnterOtherInfo1("Info1");
				addVoucherProfile.EnterOtherInfo2("Info2");
			}
		}	
		addVoucherProfile.ClickonSubmit();
		String failureMessage = addVoucherDenomination.getErrorMessage();
		boolean isThresholdDisplayed = addVoucherProfile.isEnteredThresholdValueDisplayed(initiateMap.get("threshold"));
		boolean isQuantityDisplayed = addVoucherProfile.isEnteredQuantityValueDisplayed(initiateMap.get("quantity"));
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else if(isThresholdDisplayed && isQuantityDisplayed)
		{
			addVoucherProfile.ClickonConfirm();
			String successMessage = addVoucherProfile.getMessage();
			if(successMessage==null)
			{
				String errorMessage = addVoucherProfile.getErrorMessage();
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", errorMessage);
			}
			else
			{
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
		}
		else
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", "Threshold and Quantity exceeding maxLimit");
		}
		return initiateMap;
	}
    
    
    public HashMap<String, String> addVoucherProfileNegativeNASegment(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.ADD_VOUCHER_PROFILE,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		String currDate = homePage.getDate();
		String previousDate = homePage.addDaysToCurrentDate(currDate, -5);
		//if(addVoucherProfile.isVoucherTypeAvailable())
		addVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		if(initiateMap.get("mrp")!="")
		addVoucherProfile.SelectMRP(initiateMap.get("mrp"));
		addVoucherProfile.EnterProfileName(initiateMap.get("activeProfile"));
		addVoucherProfile.EnterShortName(initiateMap.get("shortName"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("false")) {
		addVoucherProfile.EnterMinQuantity(initiateMap.get("minQuantity"));
		addVoucherProfile.EnterMaxQuantity(initiateMap.get("maxQuantity"));
		}
		addVoucherProfile.EnterTalkTime(initiateMap.get("talkTime"));
		addVoucherProfile.EnterValidity(initiateMap.get("validity"));
		String autoVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("true")) {
		addVoucherProfile.ClickAutoGenerate();
		addVoucherProfile.EnterThreshold(initiateMap.get("threshold"));
		addVoucherProfile.EnterQuantity(initiateMap.get("quantity"));
		}
		if(initiateMap.get("scenario").equalsIgnoreCase("ExpiryPeriod equal to Current Date"))
			addVoucherProfile.EnterExpiryPeriod(currDate);
		else if(initiateMap.get("scenario").equalsIgnoreCase("ExpiryPeriod less than Current Date"))
			addVoucherProfile.EnterExpiryPeriod(previousDate);
		else
		addVoucherProfile.EnterExpiryPeriod(initiateMap.get("expiryPeriod"));
		addVoucherProfile.EnterProductDescription(initiateMap.get("description"));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
		if(!BTSLUtil.isNullString(value)) {
			if(!value.equalsIgnoreCase("false")){
				addVoucherProfile.EnterOtherInfo1("Info1");
				addVoucherProfile.EnterOtherInfo2("Info2");
			}
		}	
		addVoucherProfile.ClickonSubmit();
		String failureMessage = addVoucherDenomination.getErrorMessage();
		boolean isThresholdDisplayed = addVoucherProfile.isEnteredThresholdValueDisplayed(initiateMap.get("threshold"));
		boolean isQuantityDisplayed = addVoucherProfile.isEnteredQuantityValueDisplayed(initiateMap.get("quantity"));
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else if(isThresholdDisplayed && isQuantityDisplayed)
		{
			addVoucherProfile.ClickonConfirm();
			String successMessage = addVoucherProfile.getMessage();
			if(successMessage==null)
			{
				String errorMessage = addVoucherProfile.getErrorMessage();
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", errorMessage);
			}
			else
			{
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
		}
		else
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", "Threshold and Quantity exceeding maxLimit");
		}
		return initiateMap;
	}
    public HashMap<String, String> voucherDenominationNegativeNA(HashMap<String, String> initiateMap, String voucherType) {
 	   if(voucherType.equalsIgnoreCase("physical"))
 			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
 			else if(voucherType.equalsIgnoreCase("electronic"))
 				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
 			else
 				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.ADD_VOUCHER_DENOMINATION,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherDenomination();
		voucherDenomination.clickAddVoucherDenomination();
		//if(addVoucherDenomination.isVoucherTypeAvailable())
		addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		//if(addVoucherDenomination.isServiceTypeAvailable())
		addVoucherDenomination.SelectServiceType(initiateMap.get("service"));
		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
		//if(addVoucherDenomination.isSubServiceTypeAvailable())
			addVoucherDenomination.SelectSubServiceType(initiateMap.get("subService"));
		
		addVoucherDenomination.EnterDenomination(initiateMap.get("denominationName"));
		addVoucherDenomination.EnterShortName(initiateMap.get("shortName"));
		addVoucherDenomination.EnterMRP(initiateMap.get("mrp"));
		addVoucherDenomination.EnterPayable(initiateMap.get("payableAmount"));
		addVoucherDenomination.EnterDescription(initiateMap.get("description"));
		addVoucherDenomination.ClickonSubmit();
		String failureMessage = addVoucherDenomination.getErrorMessage();
		if(failureMessage!=null) {
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else
		{
		addVoucherDenominationPage2.ClickonConfirm();
		String errorMessage = addVoucherDenomination.getErrorMessage();
		if(errorMessage!=null)
			initiateMap.put("Message", errorMessage);
		else
		{
		String successMessage = addVoucherDenomination.getSuccessMessage();
		initiateMap.put("MessageStatus", "Y");
		initiateMap.put("Message", successMessage);
		}
		}
		
		return initiateMap;
	}
    
    public HashMap<String, String> voucherDenominationNegativeNASegment(HashMap<String, String> initiateMap, String voucherType) {
  	   if(voucherType.equalsIgnoreCase("physical"))
  			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
  			else if(voucherType.equalsIgnoreCase("electronic"))
  				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
  			else
  				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.ADD_VOUCHER_DENOMINATION,initiateMap.get("categoryName"));
  	   int size;
  	   login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
 		selectNetworkPage.selectNetwork();
 		saHomePage.clickVoucherDenomination();
 		voucherDenomination.clickAddVoucherDenomination();
 		//if(addVoucherDenomination.isVoucherTypeAvailable())
 		addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
 		if (Nation_Voucher == 1) {
 			if(addVoucherDenomination.isSegmentAvailable()) {
 				size=addVoucherDenomination.VoucherSegmentCount();
 				if(size>0) {
 					if(initiateMap.get("segment").equalsIgnoreCase("National")) {
 						addVoucherDenomination.SelectVoucherSegment("NL");
 					}
 					else {
 						addVoucherDenomination.SelectVoucherSegment(voucherSegment);
 					}
 				}	
 			}
 		}
 		//if(addVoucherDenomination.isServiceTypeAvailable())
 		addVoucherDenomination.SelectServiceType(initiateMap.get("service"));
 		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
 		//if(addVoucherDenomination.isSubServiceTypeAvailable())
 			addVoucherDenomination.SelectSubServiceType(initiateMap.get("subService"));
 		
 		addVoucherDenomination.EnterDenomination(initiateMap.get("denominationName"));
 		addVoucherDenomination.EnterShortName(initiateMap.get("shortName"));
 		addVoucherDenomination.EnterMRP(initiateMap.get("mrp"));
 		addVoucherDenomination.EnterPayable(initiateMap.get("payableAmount"));
 		addVoucherDenomination.EnterDescription(initiateMap.get("description"));
 		addVoucherDenomination.ClickonSubmit();
 		String failureMessage = addVoucherDenomination.getErrorMessage();
 		if(failureMessage!=null) {
 			initiateMap.put("MessageStatus", "N");
 			initiateMap.put("Message", failureMessage);
 		}
 		else
 		{
 		addVoucherDenominationPage2.ClickonConfirm();
 		String errorMessage = addVoucherDenomination.getErrorMessage();
 		if(errorMessage!=null)
 			initiateMap.put("Message", errorMessage);
 		else
 		{
 		String successMessage = addVoucherDenomination.getSuccessMessage();
 		initiateMap.put("MessageStatus", "Y");
 		initiateMap.put("Message", successMessage);
 		}
 		}
 		
 		return initiateMap;
 	}
    
    
    public HashMap<String, String> voucherDenominationNegativeNAwithoutSegment(HashMap<String, String> initiateMap, String voucherType) {
   	   if(voucherType.equalsIgnoreCase("physical"))
   			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
   			else if(voucherType.equalsIgnoreCase("electronic"))
   				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
   			else
   				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.ADD_VOUCHER_DENOMINATION,initiateMap.get("categoryName"));
   	   int size;
   	   login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
  		selectNetworkPage.selectNetwork();
  		saHomePage.clickVoucherDenomination();
  		voucherDenomination.clickAddVoucherDenomination();
  		//if(addVoucherDenomination.isVoucherTypeAvailable())
  		addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
  		//if(addVoucherDenomination.isServiceTypeAvailable())
  		addVoucherDenomination.EnterDenomination(initiateMap.get("denominationName"));
  		addVoucherDenomination.EnterShortName(initiateMap.get("shortName"));
  		addVoucherDenomination.EnterMRP(initiateMap.get("mrp"));
  		addVoucherDenomination.EnterPayable(initiateMap.get("payableAmount"));
  		addVoucherDenomination.EnterDescription(initiateMap.get("description"));
  		addVoucherDenomination.ClickonSubmit();
  		String failureMessage = addVoucherDenomination.getErrorMessage();
  		if(failureMessage!=null) {
  			initiateMap.put("MessageStatus", "N");
  			initiateMap.put("Message", failureMessage);
  		}
  		else
  		{
  		addVoucherDenominationPage2.ClickonConfirm();
  		String errorMessage = addVoucherDenomination.getErrorMessage();
  		if(errorMessage!=null)
  			initiateMap.put("Message", errorMessage);
  		else
  		{
  		String successMessage = addVoucherDenomination.getSuccessMessage();
  		initiateMap.put("MessageStatus", "Y");
  		initiateMap.put("Message", successMessage);
  		}
  		}
  		
  		return initiateMap;
  	}
    
       public HashMap<String, String> voucherDenominationNegative(HashMap<String, String> initiateMap, String voucherType) {
    	   if(voucherType.equalsIgnoreCase("physical"))
    			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
    			else if(voucherType.equalsIgnoreCase("electronic"))
    				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
    			else
    				userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_VOUCHER_DENOMINATION);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherDenomination();
		voucherDenomination.clickAddVoucherDenomination();
		if(addVoucherDenomination.isVoucherTypeAvailable())
		addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(addVoucherDenomination.isServiceTypeAvailable())
		addVoucherDenomination.SelectServiceType(initiateMap.get("service"));
		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
		if(addVoucherDenomination.isSubServiceTypeAvailable())
			addVoucherDenomination.SelectSubServiceType(initiateMap.get("subService"));
		
		addVoucherDenomination.EnterDenomination(initiateMap.get("denominationName"));
		addVoucherDenomination.EnterShortName(initiateMap.get("shortName"));
		addVoucherDenomination.EnterMRP(initiateMap.get("mrp"));
		addVoucherDenomination.EnterPayable(initiateMap.get("payableAmount"));
		addVoucherDenomination.EnterDescription(initiateMap.get("description"));
		addVoucherDenomination.ClickonSubmit();
		String failureMessage = addVoucherDenomination.getErrorMessage();
		if(failureMessage!=null) {
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else
		{
		addVoucherDenominationPage2.ClickonConfirm();
		String errorMessage = addVoucherDenomination.getErrorMessage();
		if(errorMessage!=null)
			initiateMap.put("Message", errorMessage);
		else
		{
		String successMessage = addVoucherDenomination.getSuccessMessage();
		initiateMap.put("MessageStatus", "Y");
		initiateMap.put("Message", successMessage);
		}
		}
		
		return initiateMap;
	}
       
       public HashMap<String, String> voucherDenominationNegativeOtherNeworkAdmin(HashMap<String, String> initiateMap, String voucherType) {
    	   if(voucherType.equalsIgnoreCase("physical"))
    			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
    			else if(voucherType.equalsIgnoreCase("electronic"))
    				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_DENOMINATION, voucherType);
    			else
    				userAccessMap = UserAccess.getUserWithAccesswithCategoryOtherNetworkAdmin(RolesI.ADD_VOUCHER_DENOMINATION,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherDenomination();
		voucherDenomination.clickAddVoucherDenomination();
		//if(addVoucherDenomination.isVoucherTypeAvailable())
		addVoucherDenomination.SelectVoucherType(initiateMap.get("voucherType"));
		//if(addVoucherDenomination.isServiceTypeAvailable())
		addVoucherDenomination.SelectServiceType(initiateMap.get("service"));
		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
		//if(addVoucherDenomination.isSubServiceTypeAvailable())
		addVoucherDenomination.SelectSubServiceType(initiateMap.get("subService"));
		
		addVoucherDenomination.EnterDenomination(initiateMap.get("denominationName"));
		addVoucherDenomination.EnterShortName(initiateMap.get("shortName"));
		addVoucherDenomination.EnterMRP(initiateMap.get("mrp"));
		addVoucherDenomination.EnterPayable(initiateMap.get("payableAmount"));
		addVoucherDenomination.EnterDescription(initiateMap.get("description"));
		addVoucherDenomination.ClickonSubmit();
		String failureMessage = addVoucherDenomination.getErrorMessage();
		if(failureMessage!=null) {
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else
		{
		addVoucherDenominationPage2.ClickonConfirm();
		String errorMessage = addVoucherDenomination.getErrorMessage();
		if(errorMessage!=null)
			initiateMap.put("Message", errorMessage);
		else
		{
		String successMessage = addVoucherDenomination.getSuccessMessage();
		initiateMap.put("MessageStatus", "Y");
		initiateMap.put("Message", successMessage);
		}
		}
		
		return initiateMap;
	}
       
       public HashMap<String, String> addActiveVoucherProfileNegative(HashMap<String, String> initiateMap) throws InterruptedException {
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_ACTIVE_VOUCHER_PROFILE);
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherProfile();
   		voucherProfile.clickAddActiveProfileDetails();
   		String currDate = homePage.getDate();
   		String prevDate = homePage.addDaysToCurrentDate(currDate, -6);
   		String futrDate = homePage.addDaysToCurrentDate(currDate, 1);
   		initiateMap.put("futuredate", futrDate);
   		if(initiateMap.get("addApplicableDate")!="")
   		{
   			if(initiateMap.get("scenario").equalsIgnoreCase("Applicable Date less than Current Date"))
   				addActiveProfile.EnterApplicableFromDate(prevDate);
   			else if(initiateMap.get("scenario").equalsIgnoreCase("Applicable Date greater than Current Date"))
   				addActiveProfile.EnterApplicableFromDate(futrDate);
   			else
   		        addActiveProfile.EnterApplicableFromDate(currDate);
   		}
   		if(initiateMap.get("selectProfile")!="")
   		addActiveProfile.SelectProfileName(initiateMap.get("voucherType"), initiateMap.get("denominationName"), initiateMap.get("mrp"), initiateMap.get("activeProfile"));
   		addActiveProfile.ClickonSubmit();
   		String failureMessage = addActiveProfile.getErrorMessage();
   		if(failureMessage != null)
   		{
   			initiateMap.put("MessageStatus", "N");
   			initiateMap.put("Message", failureMessage);
   		}else {
   			addActiveProfilePage2.ClickonConfirm();
   			String successMessage = addActiveProfile.getMessage();
   			initiateMap.put("MessageStatus", "Y");
   			initiateMap.put("Message", successMessage);
   		}
   		return initiateMap;
   	}
       
       public HashMap<String, String> addActiveVoucherProfileNegativeNA(HashMap<String, String> initiateMap) throws InterruptedException {
      		userAccessMap = UserAccess.getUserWithAccesswithCategoryOtherNetworkAdmin(RolesI.ADD_ACTIVE_VOUCHER_PROFILE,initiateMap.get("categoryName"));
      		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
      		selectNetworkPage.selectNetwork();
      		saHomePage.clickVoucherProfile();
      		voucherProfile.clickAddActiveProfileDetails();
      		String currDate = homePage.getDate();
      		String prevDate = homePage.addDaysToCurrentDate(currDate, -6);
      		String futrDate = homePage.addDaysToCurrentDate(currDate, 1);
      		initiateMap.put("futuredate", futrDate);
      		if(initiateMap.get("addApplicableDate")!="")
      		{
      			if(initiateMap.get("scenario").equalsIgnoreCase("Applicable Date less than Current Date"))
      				addActiveProfile.EnterApplicableFromDate(prevDate);
      			else if(initiateMap.get("scenario").equalsIgnoreCase("Applicable Date greater than Current Date"))
      				addActiveProfile.EnterApplicableFromDate(futrDate);
      			else
      		        addActiveProfile.EnterApplicableFromDate(currDate);
      		}
      		if(initiateMap.get("selectProfile")!="")
      		addActiveProfile.SelectProfileName(initiateMap.get("voucherType"), initiateMap.get("denominationName"), initiateMap.get("mrp"), initiateMap.get("activeProfile"));
      		addActiveProfile.ClickonSubmit();
      		String failureMessage = addActiveProfile.getErrorMessage();
      		if(failureMessage != null)
      		{
      			initiateMap.put("MessageStatus", "N");
      			initiateMap.put("Message", failureMessage);
      		}else {
      			addActiveProfilePage2.ClickonConfirm();
      			String successMessage = addActiveProfile.getMessage();
      			initiateMap.put("MessageStatus", "Y");
      			initiateMap.put("Message", successMessage);
      		}
      		return initiateMap;
      	}
     
       public HashMap<String, String> vomsVoucherDownloadNegative(HashMap<String, String> initiateMap) throws InterruptedException {
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_VOUCHER_DOWNLOAD);
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		caHomepage.clickVoucherDownload();
   		voucherDownloadSubCategories.clickVomsVoucherDownload();
   		String currDate = homePage.getDate();
   		String toDate = null;
   		if(initiateMap.get("scenario").equalsIgnoreCase("To date greater than to Current Date"))
   			toDate = homePage.addDaysToCurrentDate(currDate, 6);
   		else
   			toDate = currDate;
   		String fromDate = null;
   		if(initiateMap.get("scenario").equalsIgnoreCase("From date greater than to Date"))
   			fromDate = homePage.addDaysToCurrentDate(currDate, 6);
   		else
   			fromDate = homePage.addDaysToCurrentDate(currDate, -30);
   		if(initiateMap.get("viewBatchFor")!="")
   		vomsDownload.SelectBatchType(initiateMap.get("viewBatchFor"));
   		if(initiateMap.get("downLoadFromDate")!="")
   		vomsDownload.EnterFromDate(fromDate);
   		if(initiateMap.get("downLoadToDate")!="")
   		vomsDownload.EnterToDate(toDate);
   		vomsDownload.ClickonSubmit();
   		String failureMessage = vomsDownload.getErrorMessage();
   		if(failureMessage != null)
   		{
   			initiateMap.put("MessageStatus", "N");
   			initiateMap.put("Message", failureMessage);
   		}else {
   			vomsDownloadPage2.SelectBatchToDownload(initiateMap.get("activeProfile"));
   			vomsDownloadPage3.SelectLink();
   			String successMessage = "Voms Batch Downloaded Successfully";
   			initiateMap.put("MessageStatus", "Y");
   			initiateMap.put("Message", successMessage);
   		}
   		return initiateMap;
   	}
      
       public HashMap<String, String> changeOtherStatusNegative(HashMap<String, String> initiateMap) throws InterruptedException {
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGE_OTHER_STATUS);
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherDenomination();
   		voucherDenomination.clickChangeOtherStatus();
   		if(initiateMap.get("voucherType")!="")
   		changeOtherStatus.SelectVoucherType(initiateMap.get("voucherType"));
   		
   		if(initiateMap.get("fromSerialNumber")!="") {
   				changeOtherStatus.EnterFromSerial(initiateMap.get("fromSerialNumber"));
   		}
   		if(initiateMap.get("toSerialNumber")!="") {
   			changeOtherStatus.EnterToSerial(initiateMap.get("toSerialNumber"));
   		}
   		if(initiateMap.get("numberOfVouchers")!="")
   		changeOtherStatus.EnterNumberOfVouchers(initiateMap.get("numberOfVouchers"));
   		if(initiateMap.get("mrp")!="")
   		changeOtherStatus.EnterMRP(initiateMap.get("mrp"));
   		if(initiateMap.get("activeProfile")!="")
   		changeOtherStatus.SelectProductID(initiateMap.get("activeProfile"));
   		if(initiateMap.get("voucherStatus")!="")
   		changeOtherStatus.SelectVoucherStatus(initiateMap.get("voucherStatus"));
   		changeOtherStatus.ClickonSubmit();
   		String failureMessage = vomsDownload.getErrorMessage();
   		if(failureMessage != null)
   		{
   			initiateMap.put("MessageStatus", "N");
   			initiateMap.put("Message", failureMessage);
   		}else {
   			changeOtherStatusPage2.ClickonConfirm();
   			String errorMessage = vomsDownload.getErrorMessage();
   			if(errorMessage!=null)
   			{
   				initiateMap.put("Message", errorMessage);
   			}
   			else
   			{
   			String successMessage = changeOtherStatus.getMessage();
   			initiateMap.put("MessageStatus", "Y");
   			initiateMap.put("Message", successMessage);
   			}
   		}
   		return initiateMap;
   	}
       
       public HashMap<String, String> changeGeneratedStatusNegative(HashMap<String, String> initiateMap) throws InterruptedException {
      		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGE_OTHER_STATUS);
      		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
      		selectNetworkPage.selectNetwork();
      		saHomePage.clickVoucherDenomination();
      		voucherDenomination.clickChangeGeneratedStatus();
      		if(initiateMap.get("voucherType")!="")
      		changeOtherStatus.SelectVoucherType(initiateMap.get("voucherType"));
      		
      		if(initiateMap.get("fromSerialNumber")!="") {
      				changeOtherStatus.EnterFromSerial(initiateMap.get("fromSerialNumber"));
      		}
      		if(initiateMap.get("toSerialNumber")!="") {
      			changeOtherStatus.EnterToSerial(initiateMap.get("toSerialNumber"));
      		}
      		if(initiateMap.get("numberOfVouchers")!="")
      		changeOtherStatus.EnterNumberOfVouchers(initiateMap.get("numberOfVouchers"));
      		if(initiateMap.get("mrp")!="")
      		changeOtherStatus.EnterMRP(initiateMap.get("mrp"));
      		if(initiateMap.get("activeProfile")!="")
      		changeOtherStatus.SelectProductID(initiateMap.get("activeProfile"));
      		if(initiateMap.get("voucherStatus")!="")
      		changeOtherStatus.SelectVoucherStatus(initiateMap.get("voucherStatus"));
      		changegeneratedStatus.ClickonSubmit();
      		String failureMessage = vomsDownload.getErrorMessage();
      		if(failureMessage != null)
      		{
      			initiateMap.put("MessageStatus", "N");
      			initiateMap.put("Message", failureMessage);
      		}else {
      			changegeneratedStatus.ClickonConfirm();
      			String errorMessage = vomsDownload.getErrorMessage();
      			if(errorMessage!=null)
      			{
      				initiateMap.put("Message", errorMessage);
      			}
      			else
      			{
      			String successMessage = changegeneratedStatus.getMessage();
      			initiateMap.put("MessageStatus", "Y");
      			initiateMap.put("Message", successMessage);
      			}
      		}
      		return initiateMap;
      	}
       
       public HashMap<String, String> viewVoucherDenomination(HashMap<String, String> initiateMap,String voucherType) throws Exception {
    	   if(voucherType.equalsIgnoreCase("physical"))
    			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
    			else if(voucherType.equalsIgnoreCase("electronic"))
    				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
    			else
    				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.VIEW_VOUCHER_DENOMINATION,initiateMap.get("categoryName"));
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherDenomination();
   		voucherDenomination.clickViewVoucherDenomination();
   		viewVoucherDenomination.selectVoucherType(initiateMap.get("voucherType"));
   		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
   		viewVoucherDenomination.clickSubmit();
   		String failureMessage = viewVoucherDenomination.getErrorMessage();
   		if (failureMessage != null) {
   			initiateMap.put("MessageStatus", "N");
   			initiateMap.put("Message", failureMessage);
   		} else {
   			boolean flag = viewVoucherDenomination2.checkParticularDenominationAvailable(
   					initiateMap.get("denominationName"), initiateMap.get("shortName"), initiateMap.get("mrp"),
   					initiateMap.get("payableAmount"), initiateMap.get("description"));
   			if (!flag) {
   				initiateMap.put("MessageStatus", "N");
   				initiateMap.put("Message", "No Such Voucher Denomination Found");
   			} else {
   				initiateMap.put("MessageStatus", "Y");
   				initiateMap.put("Message", "Added Voucher Denomination Found");
   			}
   		}
   		return initiateMap;

   	}
       
       public HashMap<String, String> viewVoucherDenominationOtherNetworkAdmin(HashMap<String, String> initiateMap,String voucherType) throws Exception {
    	   if(voucherType.equalsIgnoreCase("physical"))
    			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
    			else if(voucherType.equalsIgnoreCase("electronic"))
    				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
    			else
    				userAccessMap = UserAccess.getUserWithAccesswithCategoryOtherNetworkAdmin(RolesI.VIEW_VOUCHER_DENOMINATION,initiateMap.get("categoryName"));
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherDenomination();
   		voucherDenomination.clickViewVoucherDenomination();
   		viewVoucherDenomination.selectVoucherType(initiateMap.get("voucherType"));
   		viewVoucherDenomination.clickSubmit();
   		String failureMessage = viewVoucherDenomination.getErrorMessage();
   		if (failureMessage != null) {
   			initiateMap.put("MessageStatus", "N");
   			initiateMap.put("Message", failureMessage);
   		} else {
   			boolean flag = viewVoucherDenomination2.checkParticularDenominationAvailable(
   					initiateMap.get("denominationName"), initiateMap.get("shortName"), initiateMap.get("mrp"),
   					initiateMap.get("payableAmount"), initiateMap.get("description"));
   			if (!flag) {
   				initiateMap.put("MessageStatus", "N");
   				initiateMap.put("Message", "No Such Voucher Denomination Found");
   			} else {
   				initiateMap.put("MessageStatus", "Y");
   				initiateMap.put("Message", "Added Voucher Denomination Found");
   			}
   		}
   		return initiateMap;

   	}

   	public HashMap<String, String> viewVoucherProfile(HashMap<String, String> initiateMap, String voucherType) throws Exception{
   	 if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.VIEW_VOUCHER_DENOMINATION,initiateMap.get("categoryName"));
       	   login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
       	   selectNetworkPage.selectNetwork();
       	   saHomePage.clickVoucherProfile();
       	   voucherProfile.clickViewVoucherProfile();
       	   viewVoucherProfile.selectVoucherType(initiateMap.get("voucherType"));
       	if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
       	   viewVoucherProfile.clickSubmitButton();
       	   boolean flag = viewVoucherProfile2.checkParticularProfileAvailable(initiateMap.get("activeProfile"), initiateMap.get("mrp"));
       	   if(!flag) {
       		   	initiateMap.put("MessageStatus", "N");
     	   			initiateMap.put("Message", "No Such Voucher Profile Found");
       	   }
       	   else {
       		   viewVoucherProfile2.selectRadioButtonProfile(initiateMap.get("activeProfile"), initiateMap.get("mrp"));
       		   viewVoucherProfile2.clickSubmitButton();
       	   }
       	   flag = viewVoucherProfile3.checkParticularProfileAvailable(initiateMap.get("activeProfile"));
       	   if(!flag) {
       		   	initiateMap.put("MessageStatus", "N");
    	   			initiateMap.put("Message", "No Such Voucher Profile Found");
       	   }
       	   else {
       		   initiateMap.put("MessageStatus", "Y");
    	   			initiateMap.put("Message", "Voucher Profile Found on View Details Page");
       	   }
       	   
       	   return initiateMap;
            }
   	
	public HashMap<String, String> viewVoucherProfileNegativeNA(HashMap<String, String> initiateMap, String voucherType) throws Exception{
	   	 if(voucherType.equalsIgnoreCase("physical"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
				else if(voucherType.equalsIgnoreCase("electronic"))
					userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType,initiateMap.get("categoryName"));
				else
					userAccessMap = UserAccess.getUserWithAccesswithCategoryOtherNetworkAdmin(RolesI.VIEW_VOUCHER_DENOMINATION,initiateMap.get("categoryName"));
	       	   login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	       	   selectNetworkPage.selectNetwork();
	       	   saHomePage.clickVoucherProfile();
	       	   voucherProfile.clickViewVoucherProfile();
	       	   viewVoucherProfile.selectVoucherType(initiateMap.get("voucherType"));
	       	if (Nation_Voucher == 1) {
				if(addVoucherDenomination.isSegmentAvailable())
					addVoucherDenomination.SelectVoucherSegment(voucherSegment);
			}
	       	   viewVoucherProfile.clickSubmitButton();
	       	   boolean flag = viewVoucherProfile2.checkParticularProfileAvailable(initiateMap.get("activeProfile"), initiateMap.get("mrp"));
	       	   if(!flag) {
	       		   	initiateMap.put("MessageStatus", "N");
	     	   			initiateMap.put("Message", "No Such Voucher Profile Found");
	       	   }
	       	   else {
	       		   viewVoucherProfile2.selectRadioButtonProfile(initiateMap.get("activeProfile"), initiateMap.get("mrp"));
	       		   viewVoucherProfile2.clickSubmitButton();
	       	   }
	       	   flag = viewVoucherProfile3.checkParticularProfileAvailable(initiateMap.get("activeProfile"));
	       	   if(!flag) {
	       		   	initiateMap.put("MessageStatus", "N");
	    	   			initiateMap.put("Message", "No Such Voucher Profile Found");
	       	   }
	       	   else {
	       		   initiateMap.put("MessageStatus", "Y");
	    	   			initiateMap.put("Message", "Voucher Profile Found on View Details Page");
	       	   }
	       	   
	       	   return initiateMap;
	            }
   	
   	public HashMap<String, String> modifyVoucherDenomination(HashMap<String, String> initiateMap){
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_VOUCHER_DENOMINATION);
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherDenomination();
   		voucherDenomination.clickModifyVoucherDenomination();
   		modifyVoucherDenomination.selectVoucherType(initiateMap.get("voucherType"));
   		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
   		modifyVoucherDenomination.selectService(initiateMap.get("service"));
   		modifyVoucherDenomination.selectSubService(initiateMap.get("subService"));
   		modifyVoucherDenomination.clickSubmit();
   		modifyVoucherDenomination2.selectDenominationName(initiateMap.get("denominationName"));
   		modifyVoucherDenomination2.clickSubmit();
   		modifyVoucherDenomination3.modifyDenominationName(initiateMap.get("denominationName") + "Modify");
   		modifyVoucherDenomination3.modifyShortName(initiateMap.get("shortName")+"Modify");
   		modifyVoucherDenomination3.modifyDescription(initiateMap.get("description")+"Modify");
   		modifyVoucherDenomination3.clickSubmit();
   		String failureMessage = modifyVoucherDenomination.getErrorMessage();
   		if(failureMessage!=null)
   		{
   		initiateMap.put("MessageStatus", "N");
   		initiateMap.put("Message", failureMessage);
   		}
   		else
   		{
   			modifyVoucherDenomination4.clickConfirm();
   			String successMessage = modifyVoucherDenomination.getSuccessMessage();
   			if(successMessage!=null)
   			{
   				initiateMap.put("MessageStatus", "Y");
   				initiateMap.put("Message", successMessage);
   			}
   			else
   			{
   				String errorMessage = modifyVoucherDenomination.getErrorMessage();
   				initiateMap.put("MessageStatus", "N");
   				initiateMap.put("Message", errorMessage);
   			}
   		
   		}
   		  return initiateMap;		
   	}
   	
   	public HashMap<String, String> modifyVoucherDenominationmrp(HashMap<String, String> initiateMap){
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_VOUCHER_DENOMINATION);
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherDenomination();
   		voucherDenomination.clickModifyVoucherDenomination();
   		modifyVoucherDenomination.selectVoucherType(initiateMap.get("voucherType"));
   		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
   		modifyVoucherDenomination.selectService(initiateMap.get("service"));
   		modifyVoucherDenomination.selectSubService(initiateMap.get("subService"));
   		modifyVoucherDenomination.clickSubmit();
   		modifyVoucherDenomination2.selectDenominationName(initiateMap.get("denominationName"));
   		modifyVoucherDenomination2.clickSubmit();
   		modifyVoucherDenomination3.modifyDenominationName(initiateMap.get("denominationName") + "Modify");
   		modifyVoucherDenomination3.modifyShortName(initiateMap.get("shortName")+"Modify");
   		modifyVoucherDenomination3.modifyDescription(initiateMap.get("description")+"Modify");
   		modifyVoucherDenomination3.modifyMRP("234");
   		modifyVoucherDenomination3.clickSubmit();
   		String failureMessage = modifyVoucherDenomination.getErrorMessage();
   		if(failureMessage!=null)
   		{
   		initiateMap.put("MessageStatus", "N");
   		initiateMap.put("Message", failureMessage);
   		}
   		else
   		{
   			modifyVoucherDenomination4.clickConfirm();
   			String successMessage = modifyVoucherDenomination.getSuccessMessage();
   			if(successMessage!=null)
   			{
   				initiateMap.put("MessageStatus", "Y");
   				initiateMap.put("Message", successMessage);
   			}
   			else
   			{
   				String errorMessage = modifyVoucherDenomination.getErrorMessage();
   				initiateMap.put("MessageStatus", "N");
   				initiateMap.put("Message", errorMessage);
   			}
   		
   		}
   		  return initiateMap;		
   	}
   	
   	public HashMap<String, String> viewActiveProfile(HashMap<String, String> initiateMap, String voucherType){
   		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_ACTIVE_VOUCHER_PROFILEE, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_ACTIVE_VOUCHER_PROFILEE, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.VIEW_ACTIVE_VOUCHER_PROFILEE,initiateMap.get("categoryName"));
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherProfile();
   		voucherProfile.clickViewActiveProfileDetails();
   		String toDate = homePage.getDate();
   		viewActiveProfile.enterApplicableOn(toDate);
   		viewActiveProfile.clickSubmit();
   		boolean flag = viewActiveProfile2.checkParticularActiveProfile(initiateMap.get("mrp"), initiateMap.get("denominationName"), initiateMap.get("activeProfile"), initiateMap.get("talkTime"), initiateMap.get("validity"), initiateMap.get("voucherType"));
   
 	   if(!flag) {
 		   	initiateMap.put("MessageStatus", "N");
	   			initiateMap.put("Message", "No Such Active Profile Found");
 	   }
 	   else {
 		   viewActiveProfile2.clickBack();
 		   initiateMap.put("MessageStatus", "Y");
	   			initiateMap.put("Message", "Active Profile Found on View Active Profile Page");
 	   }
 	   
 	   return initiateMap;
      }
   	
	public HashMap<String, String> viewActiveProfileNA(HashMap<String, String> initiateMap, String voucherType){
   		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_ACTIVE_VOUCHER_PROFILEE, voucherType,initiateMap.get("categoryName"));
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.VIEW_ACTIVE_VOUCHER_PROFILEE, voucherType,initiateMap.get("categoryName"));
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategoryOtherNetworkAdmin(RolesI.VIEW_ACTIVE_VOUCHER_PROFILEE,initiateMap.get("categoryName"));
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherProfile();
   		voucherProfile.clickViewActiveProfileDetails();
   		String toDate = homePage.getDate();
   		viewActiveProfile.enterApplicableOn(toDate);
   		viewActiveProfile.clickSubmit();
   		boolean flag = viewActiveProfile2.checkParticularActiveProfile(initiateMap.get("mrp"), initiateMap.get("denominationName"), initiateMap.get("activeProfile"), initiateMap.get("talkTime"), initiateMap.get("validity"), initiateMap.get("voucherType"));
   
 	   if(!flag) {
 		   	initiateMap.put("MessageStatus", "N");
	   			initiateMap.put("Message", "No Such Active Profile Found");
 	   }
 	   else {
 		   viewActiveProfile2.clickBack();
 		   initiateMap.put("MessageStatus", "Y");
	   			initiateMap.put("Message", "Active Profile Found on View Active Profile Page");
 	   }
 	   
 	   return initiateMap;
      }
   	
   	public HashMap<String, String> modifyActiveProfile(HashMap<String, String> initiateMap){
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_ACTIVE_VOUCHER_PROFILEE);
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherProfile();
   		voucherProfile.clickModifyActiveProfileDetails();
   		String value="";
   		value=initiateMap.get("activeProfile")+"(" + initiateMap.get("futuredate") +")";
   		modifyActiveProfile.selectActiveProfile(value);
   		modifyActiveProfile.clickSubmit();
   		String failureMessage = modifyActiveProfile.getErrorMessage();
   		if(failureMessage!=null)
   		{
   		initiateMap.put("MessageStatus", "N");
   		initiateMap.put("Message", failureMessage);
   		}
   		else {
   			modifyActiveProfile2.selectActiveProfile(initiateMap.get("activeProfile"));
   			modifyActiveProfile2.clickSubmit();
   	   		failureMessage = modifyActiveProfile.getErrorMessage();
   	   		if(failureMessage!=null)
   	   		{
   	   			initiateMap.put("MessageStatus", "N");
   	   			initiateMap.put("Message", failureMessage);
   	   		}
   	   		else {
   	   			modifyActiveProfile3.checkParticularActiveProfileAvailable(initiateMap.get("denominationName"), initiateMap.get("activeProfile"));
   	   			modifyActiveProfile3.clickConfirm();
   	   			String successMessage = modifyVoucherDenomination.getSuccessMessage();
   	   			if(successMessage!=null)
   	   			{
   				initiateMap.put("MessageStatus", "Y");
   				initiateMap.put("Message", successMessage);
   	   			}
   	   			else
   	   			{
   				String errorMessage = modifyVoucherDenomination.getErrorMessage();
   				initiateMap.put("MessageStatus", "N");
   				initiateMap.put("Message", errorMessage);
   	   			}
   	   		}
   	
   		}
   		return initiateMap;
   	}
   	
   	
   	public int voucherTypeCount() {
   		int i=0;
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		i = addVoucherProfile.selectDropDownSize();
		return i;
   		
   	}
   	
   	public List<String> voucherTypeList() {
   		List<String> values = new ArrayList<String>();
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		values= addVoucherProfile.selectDropDownTypeValues();
		return values;
   	}
   	
 	public String voucherType() {
   		String values = null;
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		values= addVoucherProfile.selectDropDownvalue();
		return values;
   	}
   	
   	public HashMap<String, String> deleteVoucherProfile(HashMap<String, String> initiateMap, String scenario) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickModifyVoucherProfile();
		modifyVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		modifyVoucherProfile.ClickonSubmit();
		modifyVoucherProfilePage2.SelectProfileToModify(initiateMap.get("activeProfile"));
		modifyVoucherProfilePage2.ClickonSubmit();
		boolean isAutoVoucherDisplayed = modifyVoucherProfilePage3.ClickAutoGenerate();
		String autoVoucherAllowed = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		if(scenario.equalsIgnoreCase("preference")) {
		if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("true") && isAutoVoucherDisplayed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if(!(autoVoucherAllowed==null) && autoVoucherAllowed.equalsIgnoreCase("false") && !isAutoVoucherDisplayed)
		{
			initiateMap.put("MessageStatus", "Y");
		}
		else
			initiateMap.put("MessageStatus", "N");
		}
		else if(scenario.equalsIgnoreCase("fieldValidation") && autoVoucherAllowed.equalsIgnoreCase("true"))
		{
			modifyVoucherProfilePage3.ClickAutoGenerate();
			modifyVoucherProfilePage3.EnterThreshold(initiateMap.get("threshold"));
			modifyVoucherProfilePage3.EnterQuantity(initiateMap.get("quantity"));
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_PROFILE_OTHER_INFO);
			if(!BTSLUtil.isNullString(value)) {
				if(!value.equalsIgnoreCase("false")){
					addVoucherProfile.EnterOtherInfo1("Info1");
					addVoucherProfile.EnterOtherInfo2("Info2");
				}
			}
			modifyVoucherProfilePage3.ClickonDelete();
			modifyVoucherProfilePage3.sendEnterKey();
			String failureMessage = modifyVoucherProfilePage3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				//modifyVoucherProfilePage3.ClickonConfirm();
				String successMessage = modifyVoucherProfilePage3.getMessage();
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
			}
		}
		return initiateMap;
	}
   	
   	public HashMap<String, String> voucherGenerationInitiateNegative(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_INITIATION);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderInitiate();
		if(voucherGenerationInitiatePage1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		voucherGenerationInitiatePage1.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		voucherGenerationInitiatePage1.ClickonSubmit();
		}
		String errorMessage = voucherGenerationInitiatePage1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
	   if(initiateMap.get("denomination")!="")
		voucherGenerationInitiatePage2.SelectDenomination(initiateMap.get("denomination"));
	   if(initiateMap.get("quantity")!="")
		voucherGenerationInitiatePage2.EnterQuantity(initiateMap.get("quantity"));
		voucherGenerationInitiatePage2.EnterRemarks(initiateMap.get("remarks"));
		voucherGenerationInitiatePage2.ClickonSubmit();
		String failureMessage = voucherGenerationInitiatePage2.getErrorMessage();
		if(failureMessage != null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}else {
			boolean flag = voucherGenerationInitiatePage3.availabilityofProduct(initiateMap.get("activeProfile"));
			if(!flag) {
				String successMessage = "Voucher Profile not found";
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", successMessage);
			}
			else {
				failureMessage = "Voucher Profile found";
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", failureMessage);
			}
		}
		}
		return initiateMap;
	}
   	
   	public HashMap<String, String> voucherGenerationApproval1Negative(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL1);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval1();
		if(vomsOrderApproval1Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval1Page1.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		}
		if(initiateMap.get("productID")!="") {
			boolean flag = vomsOrderApproval1Page1.availabilityofProduct(initiateMap.get("productID"));
			
			if(!flag) {
				String successMessage = "Product ID not found";
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", successMessage);
			}
			else {
				String failureMessage = "Product ID found";
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", failureMessage);
			}
		}
		return initiateMap;
	}
   	
   	public HashMap<String, String> voucherGenerationApproval1NegativeNA(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccesswithCategoryOtherNetworkAdmin(RolesI.VOMS_ORDER_APPROVAL1,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval1();
		if(vomsOrderApproval1Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval1Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="") {
			boolean flag = vomsOrderApproval1Page1.availabilityofProduct(initiateMap.get("productID"));
			
			if(!flag) {
				String successMessage = "Product ID not found";
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
			}
			else {
				String failureMessage = "Product ID found";
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
		}
		return initiateMap;
	}
   	
	public HashMap<String, String> voucherGenerationNegativeNASegment(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccesswithCategoryOtherNetworkAdmin(RolesI.VOMS_ORDER_APPROVAL1,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderInitiate();
		if(vomsOrderApproval1Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval1Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		voucherGenerationInitiatePage1.ClickonSubmit();
		
		String errorMessage = voucherGenerationInitiatePage1.getErrorMessage();
		if(errorMessage!=null)
		{
		initiateMap.put("MessageStatus", "N");
		initiateMap.put("Message", errorMessage);
		}
		else {
		initiateMap.put("MessageStatus", "Y");
		initiateMap.put("Message", "Voms Order Initiate page loads");
		}
		return initiateMap;
		
	}
   	
   	public HashMap<String, String> viewVoucherBatchList(HashMap<String, String> initiateMap, String value) throws InterruptedException	{
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_VOUCHER_BATCH_LIST);
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherDenomination();
		voucherDenomination.clickViewBatchList();
		viewBatchList.selectViewStatus("ALL");
		viewBatchList.selectViewType("ALL");
		viewBatchList.ClickonSubmit();
		boolean flag =viewBatchList2.checkParticularBatchNumberAvailable(value);
		if(flag) {
			String successMessage = "Batch Number is available";
			
			viewBatchList2.selectBatchNumberAvailable(value);
			batchDetails.switchwindow();
			String type = "Initiated";
			int counter=0;
			if(batchDetails.checkParticularBatchType(type))
				++counter;
			if(batchDetails.checkParticularBatchNumber(value))
				++counter;
			
			
			if(counter==2) {
				successMessage = "Batch Number and type is available";
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
			else {
				String failureMessage = "Batch Type is not found";
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
		}
		else {
			String failureMessage = "Batch Number is not found";
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		batchDetails.popUpCLose();
		batchDetails.firstWindow();
		return initiateMap;
   	}
   	
   	public HashMap<String, String> vomsOrderInititate(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_INITIATION);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderInitiate();
		if(voucherGenerationInitiatePage1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		voucherGenerationInitiatePage1.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		voucherGenerationInitiatePage1.ClickonSubmit();
		}
		String errorMessage = voucherGenerationInitiatePage1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", "Voms Order Initiate page loads");
		}
		return initiateMap;
   	}
	public HashMap<String, String> vomsOrderInititateNegativeNA(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.VOMS_ORDER_INITIATION,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderInitiate();
		if(voucherGenerationInitiatePage1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		voucherGenerationInitiatePage1.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		voucherGenerationInitiatePage1.ClickonSubmit();
		}
		String errorMessage = voucherGenerationInitiatePage1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", "Voms Order Initiate page loads");
		}
		return initiateMap;
   	}
   		
   	
   	
   	public HashMap<String, String> voucherGenerationInitiateMultipleDenominationEnter(HashMap<String, String> initiateMap,HashMap<String, String> initiateMap2) throws InterruptedException {
		
	   if(initiateMap.get("denomination")!="") {
		voucherGenerationInitiatePage2.SelectDenomination(initiateMap.get("denomination"));
	    voucherGenerationInitiatePage2.SelectDenomination2(initiateMap2.get("denomination"));
	   }
	   if(initiateMap.get("quantity")!="") {
		voucherGenerationInitiatePage2.EnterQuantity(initiateMap.get("quantity"));
		voucherGenerationInitiatePage2.EnterRemarks(initiateMap.get("remarks"));
		voucherGenerationInitiatePage2.EnterQuantity2(initiateMap2.get("quantity"));
		voucherGenerationInitiatePage2.EnterRemarks2(initiateMap2.get("remarks"));
		voucherGenerationInitiatePage2.ClickonSubmit();
	   }	   
		String failureMessage = voucherGenerationInitiatePage2.getErrorMessage();
		if(failureMessage != null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}else {
			voucherGenerationInitiatePage3.SelectProduct(initiateMap.get("activeProfile"));
			voucherGenerationInitiatePage3.SelectProduct2(initiateMap2.get("activeProfile"));
			voucherGenerationInitiatePage3.ClickonSubmit();
			voucherGenerationInitiatePage4.ClickonConfirm();
			failureMessage = voucherGenerationInitiatePage2.getErrorMessage();
			if(failureMessage != null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else {
			String successMessage = voucherGenerationInitiatePage1.getMessage();
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
		}
		return initiateMap;
	}
   	
   	public HashMap<String, String> viewVoucherBatchListAllAttributes(HashMap<String, String> initiateMap, String value) throws InterruptedException	{
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_VOUCHER_BATCH_LIST);
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherDenomination();
		voucherDenomination.clickViewBatchList();
		viewBatchList.selectViewStatus("ALL");
		viewBatchList.selectViewType("ALL");
		viewBatchList.ClickonSubmit();
		boolean flag =viewBatchList2.checkParticularBatchNumberAvailable(value);
		if(flag) {
			String successMessage = "Batch Number is available";
			
			viewBatchList2.selectBatchNumberAvailable(value);
			batchDetails.switchwindow();
			String type = "Initiated";
			int counter=0;
			if(batchDetails.checkParticularBatchType(type))
				++counter;
			if(batchDetails.checkParticularBatchNumber(value))
				++counter;
			boolean check = batchDetails.verifyAllAttributes();
			
			if(counter==2 && check==true ) {
				successMessage = "Batch Number and type is available";
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
			else {
				String failureMessage = "Batch Type is not found";
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
		}
		else {
			String failureMessage = "Batch Number is not found";
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		batchDetails.popUpCLose();
		batchDetails.firstWindow();
		return initiateMap;
   	}
   	
   	public HashMap<String, String> voucherGenerationApproval2ModiftyQuantity(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL2);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval2();
		if(vomsOrderApproval2Page1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval2Page1.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval2Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval2Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
		vomsOrderApproval2Page2.ClickonSubmit();
		vomsOrderApproval2Page3.EnterQuantity(initiateMap.get("quantity2"));
		vomsOrderApproval2Page3.EnterRemarks(initiateMap.get("remarks"));
		vomsOrderApproval2Page3.ClickonApprove();
		String failureMessage = vomsOrderApproval2Page3.getErrorMessage();
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else
		{
			vomsOrderApproval2Page4.ClickonConfirm();
			String successMessage = vomsOrderApproval2Page1.getMessage();
			if (successMessage != null) {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
			else {
				initiateMap.put("MessageStatus", "N");
				String failureMessage2 = vomsOrderApproval2Page1.getErrorMessage();
				initiateMap.put("Message", failureMessage2);
			}
		}
		}
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL3);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval3();
		if(vomsOrderApproval3Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="") {
		vomsOrderApproval3Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval3Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval3Page1.ClickonSubmit();
		boolean flag = vomsOrderApproval3Page1.UpdatedQuanityavialble(initiateMap.get("quantity2"));
		errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(!flag)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", "Quantity is Not updated");
		}
		else if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			vomsOrderApproval3Page2.ClickonSubmit();
			vomsOrderApproval3Page3.EnterRemarks(initiateMap.get("remarks"));
			vomsOrderApproval3Page3.ClickonApprove();
			String failureMessage = vomsOrderApproval3Page3.getErrorMessage();
			if(failureMessage!=null)
			{
				initiateMap.put("MessageStatus", "N");
				initiateMap.put("Message", failureMessage);
			}
			else
			{
				vomsOrderApproval2Page4.ClickonConfirm();
				String successMessage = vomsOrderApproval3Page1.getMessage();
				if (successMessage != null) {
				initiateMap.put("MessageStatus", "Y");
				initiateMap.put("Message", successMessage);
				}
				else {
					initiateMap.put("MessageStatus", "N");
					String failureMessage2 = vomsOrderApproval3Page1.getErrorMessage();
					initiateMap.put("Message", failureMessage2);
				}
			}
			}
		
		return initiateMap;
	}
  
   	public HashMap<String, String> voucherGenerationrejectApproval1(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL1);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval1();
		if(vomsOrderApproval1Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval1Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval1Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval1Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval1Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
		vomsOrderApproval1Page2.ClickonSubmit();
		vomsOrderApproval1Page3.EnterQuantity(initiateMap.get("quantity"));
		vomsOrderApproval1Page3.EnterRemarks(initiateMap.get("remarks"));
		vomsOrderApproval1Page3.ClickonApprove();
		String failureMessage = vomsOrderApproval1Page3.getErrorMessage();
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else
		{
			vomsOrderApproval1Page4.ClickonReject();
			String successMessage = vomsOrderApproval1Page1.getMessage();
			if (successMessage != null) {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
			else {
				initiateMap.put("MessageStatus", "N");
				String failureMessage2 = vomsOrderApproval1Page1.getErrorMessage();
				initiateMap.put("Message", failureMessage2);
			}
		}
		}
		return initiateMap;
	}
   	
   	public HashMap<String, String> voucherGenerationRejectApproval2(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL2);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval2();
		if(vomsOrderApproval2Page1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval2Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval2Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval2Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			if(vomsOrderApproval2Page2.checkParticularProfileAvailable(initiateMap.get("denomination"), initiateMap.get("quantity")))
		vomsOrderApproval2Page2.ClickonSubmit();
		vomsOrderApproval2Page3.EnterQuantity(initiateMap.get("quantity"));
		vomsOrderApproval2Page3.EnterRemarks(initiateMap.get("remarks"));
		vomsOrderApproval2Page3.ClickonApprove();
		String failureMessage = vomsOrderApproval2Page3.getErrorMessage();
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else
		{
			vomsOrderApproval2Page4.ClickonReject();
			String successMessage = vomsOrderApproval2Page1.getMessage();
			if (successMessage != null) {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
			else {
				initiateMap.put("MessageStatus", "N");
				String failureMessage2 = vomsOrderApproval2Page1.getErrorMessage();
				initiateMap.put("Message", failureMessage2);
			}
		}
		}
		return initiateMap;
	}
   	
    public HashMap<String, String> validatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws AutomationException {
    	if(voucherType.equalsIgnoreCase("physical"))
    		userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_PROFILE, voucherType);
    		else if(voucherType.equalsIgnoreCase("electronic"))
    			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_VOUCHER_PROFILE, voucherType);
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddVoucherProfile();
		String currDate = homePage.getDate();
		String previousDate = homePage.addDaysToCurrentDate(currDate, -5);
		//if(addVoucherProfile.isVoucherTypeAvailable())
		addVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		//if(addVoucherProfile.isServiceTypeAvailable())
		addVoucherProfile.SelectService(initiateMap.get("service"));
		//int subServiceCount = DBHandler.AccessHandler.getSubServiceCount(initiateMap.get("voucherType"), initiateMap.get("service"));
		//if(addVoucherProfile.isSubServiceTypeAvailable())
		addVoucherProfile.SelectSubService(initiateMap.get("subService"));
        if(initiateMap.get("mrp")!="")
		{
				try {
				   addVoucherProfile.SelectMRPWithException(initiateMap.get("mrp"));
				   initiateMap.put("MessageStatus", "N");
				} catch(Exception ex) {
					throw new AutomationException("MRP not found, hence test case succesful.", ex);
				}
				
		}
		return initiateMap;
	}
    
    public HashMap<String, String> modifyVoucherProfileValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String scenario, String voucherType) throws InterruptedException, AutomationException {
		initiateMap.put("profile_name", UniqueChecker.UC_VOMS_ProfileName());
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.MODIFY_VOUCHER_PROFILE, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.MODIFY_VOUCHER_PROFILE, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickModifyVoucherProfile();
		modifyVoucherProfile.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		modifyVoucherProfile.ClickonSubmit();
		 if(initiateMap.get("mrp")!="")
			{
			 try {
		           modifyVoucherProfilePage2.SelectMRPToModify(initiateMap.get("mrp"));
		           initiateMap.put("MessageStatus", "N");
			 } catch(Exception ex) {
					throw new AutomationException("MRP not found, hence test case succesful.", ex);
				}  
			}
		return initiateMap;
	}
    
	public HashMap<String, String> viewVoucherProfileValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws Exception{
	   	 if(voucherType.equalsIgnoreCase("physical"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType);
				else if(voucherType.equalsIgnoreCase("electronic"))
					userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType);
				else
					userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_VOUCHER_DENOMINATION);
	       	   login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	       	   selectNetworkPage.selectNetwork();
	       	   saHomePage.clickVoucherProfile();
	       	   voucherProfile.clickViewVoucherProfile();
	       	   viewVoucherProfile.selectVoucherType(initiateMap.get("voucherType"));
	       	if (Nation_Voucher == 1) {
				if(addVoucherDenomination.isSegmentAvailable())
					addVoucherDenomination.SelectVoucherSegment(voucherSegment);
			}
	       	   viewVoucherProfile.clickSubmitButton();
	       	try {
	       	   boolean flag = viewVoucherProfile2.checkParticularProfileAvailable(initiateMap.get("activeProfile"), initiateMap.get("mrp"));
	       	   if(!flag) {
	       		   	initiateMap.put("MessageStatus", "Y");
	     	   			initiateMap.put("Message", "MRP not found, hence test case succesful.");
	       	   }
	       	   else {
	       		initiateMap.put("MessageStatus", "N");
 	   			initiateMap.put("Message", "MRP found, hence test case failed.");
	       	   }
	       	}catch(Exception ex) {
					throw new AutomationException("MRP not found, hence test case succesful.", ex);
				}  
	       	
	       	   return initiateMap;
	            }
	   	
	public HashMap<String, String> addActiveVoucherProfileValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException, AutomationException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_ACTIVE_VOUCHER_PROFILE, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.ADD_ACTIVE_VOUCHER_PROFILE, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_ACTIVE_VOUCHER_PROFILE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherProfile();
		voucherProfile.clickAddActiveProfileDetails();
		addActiveProfile.EnterApplicableFromDate(homePage.getDate());
		 try {
		addActiveProfile.SelectProfileName(initiateMap.get("voucherType"), initiateMap.get("denominationName"), initiateMap.get("mrp"), initiateMap.get("activeProfile"));
		  initiateMap.put("MessageStatus", "N");
		 } catch(Exception ex) {
				throw new AutomationException("MRP not found, hence test case succesful.", ex);
			}  
		
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationInitiateValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException, AutomationException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_INITIATION, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_INITIATION, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_INITIATION);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderInitiate();
		if(voucherGenerationInitiatePage1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		voucherGenerationInitiatePage1.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		voucherGenerationInitiatePage1.ClickonSubmit();
		}
		String errorMessage = voucherGenerationInitiatePage1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			try {
	   if(initiateMap.get("denomination")!="")
		voucherGenerationInitiatePage2.SelectDenomination(initiateMap.get("denomination"));
	   initiateMap.put("MessageStatus", "N");
			 } catch(Exception ex) {
					throw new AutomationException("Denomination not found, hence test case succesful.", ex);
				}
		}
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval1ValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException, AutomationException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL1, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL1, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL1);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval1();
		if(vomsOrderApproval1Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval1Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		try {
		if(initiateMap.get("productID")!="")
		{
		vomsOrderApproval1Page1.SelectProductId(initiateMap.get("productID"));
		 initiateMap.put("MessageStatus", "N");
		}
		 } catch(Exception ex) {
				throw new AutomationException("Product not found, hence test case succesful.", ex);
			}
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval2ValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException, AutomationException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL2, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL2, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL2);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval2();
		if(vomsOrderApproval2Page1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval2Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		try {
			if(initiateMap.get("productID")!="")
			{
		vomsOrderApproval2Page1.SelectProductId(initiateMap.get("productID"));
		 initiateMap.put("MessageStatus", "N");
			}
			 } catch(Exception ex) {
					throw new AutomationException("Product not found, hence test case succesful.", ex);
				}
	
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval3ValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException, AutomationException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL3, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL3);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval3();
		if(vomsOrderApproval3Page1.isVoucherTypeAvailable()) {
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval3Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		try {
			if(initiateMap.get("productID")!="")
			{
		vomsOrderApproval3Page1.SelectProductId(initiateMap.get("productID"));
		 initiateMap.put("MessageStatus", "N");
			}
			 } catch(Exception ex) {
					throw new AutomationException("Product not found, hence test case succesful.", ex);
				}
		
		return initiateMap;
	}
	
	public HashMap<String, String> viewVoucherDenominationValidatingMRPBasedOnType(HashMap<String, String> initiateMap,String voucherType) throws Exception {
 	   if(voucherType.equalsIgnoreCase("physical"))
 			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType);
 			else if(voucherType.equalsIgnoreCase("electronic"))
 				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType);
 			else
 				userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_VOUCHER_DENOMINATION);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherDenomination();
		voucherDenomination.clickViewVoucherDenomination();
		boolean flag=true;
		viewVoucherDenomination.selectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		viewVoucherDenomination.clickSubmit();
		String failureMessage = viewVoucherDenomination.getErrorMessage();
		if (failureMessage != null) {
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		} else {
			flag = viewVoucherDenomination2.checkParticularDenominationAvailable(
					initiateMap.get("denominationName"), initiateMap.get("shortName"), initiateMap.get("mrp"),
					initiateMap.get("payableAmount"), initiateMap.get("description"));
			if(flag) {
				Log.info("Mrp found, hence test case unsuccesful.");
				initiateMap.put("MessageStatus", "N");
			}
			else{
				Log.info("Mrp not found, hence test case succesful.");
				initiateMap.put("MessageStatus", "Y");
			}
		
		}
		return initiateMap;

	}
    
   	public HashMap<String, String> modifyVoucherDenominationValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws AutomationException{
   		if(voucherType.equalsIgnoreCase("physical"))
 			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.MODIFY_VOUCHER_DENOMINATION, voucherType);
 			else if(voucherType.equalsIgnoreCase("electronic"))
 				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.MODIFY_VOUCHER_DENOMINATION, voucherType);
 			else
   		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_VOUCHER_DENOMINATION);
   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
   		selectNetworkPage.selectNetwork();
   		saHomePage.clickVoucherDenomination();
   		voucherDenomination.clickModifyVoucherDenomination();
   		modifyVoucherDenomination.selectVoucherType(initiateMap.get("voucherType"));
   		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
   		modifyVoucherDenomination.selectService(initiateMap.get("service"));
   		modifyVoucherDenomination.selectSubService(initiateMap.get("subService"));
   		modifyVoucherDenomination.clickSubmit();
   		try {
   		modifyVoucherDenomination2.selectDenominationName(initiateMap.get("denominationName"));
   		}catch(Exception ex) {
			throw new AutomationException("Denomination not found, hence test case succesful.", ex);
		} 
   		  return initiateMap;		
   	}
   	
   	public HashMap<String, String> createBatchForVoucherDownloadValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException, AutomationException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.CREATE_BATCH_FOR_VOUCHER_DOWNLOAD, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.CREATE_BATCH_FOR_VOUCHER_DOWNLOAD, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.CREATE_BATCH_FOR_VOUCHER_DOWNLOAD);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickVoucherDownload();
		voucherDownloadSubCategories.clickCreateBatchForVoucherDownload();
		if(initiateMap.get("voucherType")!="")
		createBatchForVoucherDownload.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		createBatchForVoucherDownload.SelectCreateBatchFor(initiateMap.get("batchType"));
		try
		{
		if(initiateMap.get("denomination")!="")
		createBatchForVoucherDownload.SelectDenomination(initiateMap.get("denomination"));
		}catch(Exception ex) {
			throw new AutomationException("Denomination not found, hence test case succesful.", ex);
		} 

		return initiateMap;
	}
	
	public HashMap<String, String> vomsVoucherDownloadValidatingMRPBasedOnType(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException, AutomationException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_VOUCHER_DOWNLOAD, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_VOUCHER_DOWNLOAD);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickVoucherDownload();
		voucherDownloadSubCategories.clickVomsVoucherDownload();
		vomsDownload.SelectBatchType(initiateMap.get("viewBatchFor"));
		String toDate = homePage.getDate();
		String fromDate = homePage.addDaysToCurrentDate(toDate, -30);
		vomsDownload.EnterFromDate(fromDate);
		vomsDownload.EnterToDate(toDate);
		vomsDownload.ClickonSubmit();
		String failureMessage = vomsDownload.getMessage();
		if(failureMessage != null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}else {
			try
			{
			vomsDownloadPage2.SelectBatchToDownload(initiateMap.get("activeProfile"));
			}catch(Exception ex) {
				throw new AutomationException("Denomination not found, hence test case succesful.", ex);
			} 
	}	
		return initiateMap;
}
	public HashMap<String, String> initiateVoucherO2CTransfer(HashMap<String, String> initiateMap, HashMap<String, String> voucherInitiateMap, String voucherType) throws InterruptedException, AutomationException {
		String PositiveCommissioning = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY").toUpperCase();
		//String O2CApproval_Preference = DBHandler.AccessHandler.getSystemPreference("O2C_APPRV_QTY_LEVEL");
		BigDecimal netPayableAmount = null;
		//int approvalLength = O2CApproval_Preference.split(",").length;
		
/*		ResultMap.put("O2CAPPROVALCOUNT_PREFERENCE", O2CApproval_Preference.split(",")[approvalLength-1]);
		ResultMap.put("FIRSTAPPROVALLIMIT", _masterVO.getProperty("O2CFirstApprovalLimit"));
		ResultMap.put("SECONDAPPROVALLIMIT", _masterVO.getProperty("O2CSecondApprovalLimit"));*/

		login1.LoginAsUser(driver, initiateMap.get("LOGIN_ID"), initiateMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		if (initiateMap.get("TO_STOCK_TYPE") != null) 
			initiateMap.put("STOCK_TYPE_DROPDOWN_STATUS", "" + initiateO2CPage.selectDistributionType(initiateMap.get("TO_STOCK_TYPE")));
		else
			initiateMap.put("STOCK_TYPE_DROPDOWN_STATUS", "" + initiateO2CPage.DistributionTypeIsDisplayed());

		initiateO2CPage.enterMobileNumber(initiateMap.get("TO_MSISDN"));
		
		if (initiateMap.get("PRODUCT_TYPE") != null)
			initiateO2CPage.selectProductType1(initiateMap.get("PRODUCT_TYPE"));
		initiateO2CPage.clickSubmitButton();

		//Entering transfer details
		initiateVoucherO2CPage2.SelectVoucherType(voucherInitiateMap.get("voucherType"));
		try
		{
		initiateVoucherO2CPage2.SelectDenomination(voucherInitiateMap.get("mrp"));
		}catch(Exception ex) {
			throw new AutomationException("Denomination not found, hence test case succesful.", ex);
		} 
		
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval2Negative(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL2, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL2, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccess(RolesI.VOMS_ORDER_APPROVAL2);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval2();
		if(vomsOrderApproval2Page1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval2Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
		if(initiateMap.get("productID")!="")
		vomsOrderApproval2Page1.SelectProductId(initiateMap.get("productID"));
		vomsOrderApproval2Page1.ClickonSubmit();
		String errorMessage = vomsOrderApproval2Page1.getErrorMessage();
		if(errorMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", errorMessage);
		}
		else
		{
			if(vomsOrderApproval2Page2.checkParticularProfileAvailableNegative(initiateMap.get("denomination")))
				vomsOrderApproval2Page2.ClickonSubmit();
		vomsOrderApproval2Page3.EnterQuantity(initiateMap.get("quantity2"));
		vomsOrderApproval2Page3.EnterRemarks(initiateMap.get("remarks"));
		vomsOrderApproval2Page3.ClickonApprove();
		String failureMessage = vomsOrderApproval2Page3.getErrorMessage();
		if(failureMessage!=null)
		{
			initiateMap.put("MessageStatus", "N");
			initiateMap.put("Message", failureMessage);
		}
		else
		{
			vomsOrderApproval2Page4.ClickonConfirm();
			String successMessage = vomsOrderApproval2Page1.getMessage();
			if (successMessage != null) {
			initiateMap.put("MessageStatus", "Y");
			initiateMap.put("Message", successMessage);
			}
			else {
				initiateMap.put("MessageStatus", "N");
				String failureMessage2 = vomsOrderApproval2Page1.getErrorMessage();
				initiateMap.put("Message", failureMessage2);
			}
		}
		}
		return initiateMap;
	}
	
	public HashMap<String, String> voucherGenerationApproval2NegativeNA(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		if(voucherType.equalsIgnoreCase("physical"))
			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL2, voucherType);
			else if(voucherType.equalsIgnoreCase("electronic"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VOMS_ORDER_APPROVAL2, voucherType);
			else
				userAccessMap = UserAccess.getUserWithAccesswithCategoryOtherNetworkAdmin(RolesI.VOMS_ORDER_APPROVAL2,initiateMap.get("categoryName"));
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickVoucherGeneration();
		voucherGeneration.clickVomsOrderApproval2();
		boolean flag=false;
		if(vomsOrderApproval2Page1.isVoucherTypeAvailable())
		{
		if(initiateMap.get("voucherType")!="")
		vomsOrderApproval2Page1.SelectVoucherType(initiateMap.get("voucherType"));
		}
		if(initiateMap.get("productID")!="")
			flag=vomsOrderApproval2Page1.SelectProductIdVisible(initiateMap.get("productID"));
		if(!flag) {
			initiateMap.put("MessageStatus", "N");
			Log.info("Product Id not found");
		}
		else {
			initiateMap.put("MessageStatus", "Y");
			Log.info("Product Id not found");
		}
		return initiateMap;
	}

	 public HashMap<String, String> viewModifyVoucherDenomination(HashMap<String, String> initiateMap,String voucherType) throws Exception {
  	   if(voucherType.equalsIgnoreCase("physical"))
  			userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType);
  			else if(voucherType.equalsIgnoreCase("electronic"))
  				userAccessMap = UserAccess.getUserWithAccessForVoucherType(RolesI.VIEW_VOUCHER_DENOMINATION, voucherType);
  			else
  				userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_VOUCHER_DENOMINATION);
 		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
 		selectNetworkPage.selectNetwork();
 		saHomePage.clickVoucherDenomination();
 		voucherDenomination.clickViewVoucherDenomination();
 		viewVoucherDenomination.selectVoucherType(initiateMap.get("voucherType"));
 		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment(voucherSegment);
		}
 		viewVoucherDenomination.clickSubmit();
 		String failureMessage = viewVoucherDenomination.getErrorMessage();
 		if (failureMessage != null) {
 			initiateMap.put("MessageStatus", "N");
 			initiateMap.put("Message", failureMessage);
 		} else {
 			boolean flag = viewVoucherDenomination2.checkParticularDenominationAvailable(
 					initiateMap.get("denominationName")+"Modify", initiateMap.get("shortName")+"Modify", initiateMap.get("mrp"),
 					initiateMap.get("payableAmount"), initiateMap.get("description")+"Modify");
 			if (!flag) {
 				initiateMap.put("MessageStatus", "N");
 				initiateMap.put("Message", "No Such Voucher Denomination Found");
 			} else {
 				initiateMap.put("MessageStatus", "Y");
 				initiateMap.put("Message", "Added Voucher Denomination Found");
 			}
 		}
 		return initiateMap;

 	}
	  
	 public HashMap<String, String> changeGeneratedStatusElectronic(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
	   	//	userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGE_GENERATED_STATUS);
	   		
	   		if(voucherType.equalsIgnoreCase("physical"))
				userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.CHANGE_GENERATED_STATUS, voucherType,initiateMap.get("categoryName"));
				else if(voucherType.equalsIgnoreCase("electronic"))
					userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.CHANGE_GENERATED_STATUS, voucherType,initiateMap.get("categoryName"));
				else
					userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.CHANGE_GENERATED_STATUS,initiateMap.get("categoryName"));
	   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	   		selectNetworkPage.selectNetwork();
	   		saHomePage.clickVoucherDenomination();
	   		voucherDenomination.clickChangeGeneratedStatus();
	   		if(initiateMap.get("voucherType")!="")
	   			changegeneratedStatus.SelectVoucherType(initiateMap.get("voucherType"));
	   		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productID);
			String fromSerialNumber = VOMSData[0];
			String toSerialNumber = VOMSData[1];
			String numberOfVouchers = VOMSData[2];
			   		
	   		
	   		if(initiateMap.get("fromSerialNumber")!="") {
	   			changegeneratedStatus.EnterFromSerial(fromSerialNumber);
	   		}
	   		if(initiateMap.get("toSerialNumber")!="") {
	   			changegeneratedStatus.EnterToSerial(toSerialNumber);
	   		}
	   		if(initiateMap.get("numberOfVouchers")!="")
	   			changegeneratedStatus.EnterNumberOfVouchers(numberOfVouchers);
	   		if(initiateMap.get("mrp")!="")
	   			changegeneratedStatus.EnterMRP(initiateMap.get("mrp"));
	   		if(initiateMap.get("activeProfile")!="")
	   			changegeneratedStatus.SelectProductID(initiateMap.get("activeProfile"));
	   		if(initiateMap.get("voucherStatus")!="")
	   			changegeneratedStatus.SelectVoucherStatus("EN");
	   		changegeneratedStatus.ClickonSubmit();
	   		String failureMessage = vomsDownload.getErrorMessage();
	   		if(failureMessage != null)
	   		{
	   			initiateMap.put("MessageStatus", "N");
	   			initiateMap.put("Message", failureMessage);
	   		}else {
	   			changegeneratedStatus.ClickonConfirm();
	   			String errorMessage = vomsDownload.getErrorMessage();
	   			if(errorMessage!=null)
	   			{
	   				initiateMap.put("Message", errorMessage);
	   			}
	   			else
	   			{
	   			String successMessage = changegeneratedStatus.getMessage();
	   			initiateMap.put("MessageStatus", "Y");
	   			initiateMap.put("Message", successMessage);
	   			}
	   		}
	   		return initiateMap;
	   	}
	 
	 public HashMap<String, String> changeGeneratedStatus(HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
		   	//	userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGE_GENERATED_STATUS);
		   		
		   		if(voucherType.equalsIgnoreCase("physical"))
					userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.CHANGE_GENERATED_STATUS, voucherType,initiateMap.get("categoryName"));
					else if(voucherType.equalsIgnoreCase("electronic"))
						userAccessMap = UserAccess.getUserWithAccessForVoucherTypeCategory(RolesI.CHANGE_GENERATED_STATUS, voucherType,initiateMap.get("categoryName"));
					else
						userAccessMap = UserAccess.getUserWithAccesswithCategory(RolesI.CHANGE_GENERATED_STATUS,initiateMap.get("categoryName"));
		   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		   		selectNetworkPage.selectNetwork();
		   		saHomePage.clickVoucherDenomination();
		   		voucherDenomination.clickChangeGeneratedStatus();
		   		if(initiateMap.get("voucherType")!="")
		   			changegeneratedStatus.SelectVoucherType(initiateMap.get("voucherType"));
		   		
		   		if(initiateMap.get("fromSerialNumber")!="") {
		   			changegeneratedStatus.EnterFromSerial(initiateMap.get("fromSerialNumber"));
		   		}
		   		if(initiateMap.get("toSerialNumber")!="") {
		   			changegeneratedStatus.EnterToSerial(initiateMap.get("toSerialNumber"));
		   		}
		   		if(initiateMap.get("numberOfVouchers")!="")
		   			changegeneratedStatus.EnterNumberOfVouchers(initiateMap.get("numberOfVouchers"));
		   		if(initiateMap.get("mrp")!="")
		   			changegeneratedStatus.EnterMRP(initiateMap.get("mrp"));
		   		if(initiateMap.get("activeProfile")!="")
		   			changegeneratedStatus.SelectProductID(initiateMap.get("activeProfile"));
		   		if(initiateMap.get("voucherStatus")!="")
		   			changegeneratedStatus.SelectVoucherStatus(initiateMap.get("voucherStatus"));
		   		changegeneratedStatus.ClickonSubmit();
		   		String failureMessage = vomsDownload.getErrorMessage();
		   		if(failureMessage != null)
		   		{
		   			initiateMap.put("MessageStatus", "N");
		   			initiateMap.put("Message", failureMessage);
		   		}else {
		   			changegeneratedStatus.ClickonConfirm();
		   			String errorMessage = vomsDownload.getErrorMessage();
		   			if(errorMessage!=null)
		   			{
		   				initiateMap.put("Message", errorMessage);
		   			}
		   			else
		   			{
		   			String successMessage = changegeneratedStatus.getMessage();
		   			initiateMap.put("MessageStatus", "Y");
		   			initiateMap.put("Message", successMessage);
		   			}
		   		}
		   		return initiateMap;
		   	}

	 public void changeVoucherExpiryDate(String date, String fromSerialno, String toSerialno, String noOfVouchers){
		 userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGE_VOUCHER_EXPIRY);
	   		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	   		selectNetworkPage.selectNetwork();
	   		saHomePage.clickVoucherExpiry();
	   		voucherExpiry.clickChangeVoucherExpiry();
	   		
	   		ChangeVoucherExpiryPage cvep = new ChangeVoucherExpiryPage(driver);
	   		cvep.enterFromSerialNo(fromSerialno);
	   		cvep.enterToSerialNo(toSerialno);
	   		cvep.enterNoOfVouchers(noOfVouchers);
	   		cvep.enterExpiryDate(date);
	   		cvep.clickSubmitbutton();
	   		cvep.clickConfirmbutton();
	   		
	 }

	
	 public void voucherChangeStatusScriptExecution()
		{
			Log.info("Trying to execute Script");
			DBHandler.AccessHandler.rollbackDateForProcess("CHANGEVOMSTAT");
			SSHService.executeScript("VoucherChangeStatus.sh");
			Log.info("Script executed successfully");
		}
	 
	 public HashMap<String,String> addVoucherBundleForO2CTransfer(){
			userAccessMap = UserAccess.getUserWithAccess(RolesI.VMS_BUNDLE_ADD);
			HashMap<String,String> initiateMap = new HashMap<String,String>();
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
//			int rowCount = ExcelUtility.getRowCount(); //number of vouchers for bundle creation
			int rowCount = Integer.parseInt(_masterVO.getClientDetail("BUNDLE_CREATION_ROWS"));
			Object[] profiles = new Object[rowCount];
			String[] bundleProfiles = new String[rowCount];
			//get existing profile details from excel sheet
			profiles = loadProfile(rowCount);
			int quantity,price,totalExpectedMRP=0; //to calculate total value of bundle
			for(int i = 0 ; i <  rowCount ; i++) {
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.putAll((Map<? extends String, ? extends String>) profiles[i]);
				Log.info(temp.get("activeProfile"));
			}	
			
			login1.LoginAsUser(driver,userAccessMap.get("LOGIN_ID") , userAccessMap.get("PASSWORD"));
			selectNetworkPage.selectNetwork();
			saHomePage.clickVoucherBundleManagement();
			voucherBundleManagementPage.clickAddVoucherBundle();
			initiateMap.put("voucherBundleName", UniqueChecker.UC_VBNAME());
			initiateMap.put("voucherBundlePrefix", UniqueChecker.UC_VBPREFIX());
			while(true) {	if(!initiateMap.get("voucherBundleName").equals(null))
							break;	}
			Log.info(initiateMap.get("voucherBundleName") + " " + initiateMap.get("voucherBundlePrefix"));
			addVoucherBundlePage.enterBundleName(initiateMap.get("voucherBundleName"));
			addVoucherBundlePage.enterBundlePrefix(initiateMap.get("voucherBundlePrefix"));
			//Enter Vouchers
			for(int i = 0 ; i < 1 ; i++) {
				if(profiles[i] != null) {
					initiateMap.putAll((Map<? extends String, ? extends String>) profiles[i]);
				Log.info("VoucherType: " + initiateMap.get("voucherType") + " ; " + "ProfileName: " + initiateMap.get("activeProfile") 	+ "BundleName: " + initiateMap.get("voucherBundleName"));
				try {
			addVoucherBundlePage.selectVoucherTypeByIndex(i,initiateMap.get("voucherType"));
			if(Nation_Voucher == 1)
				addVoucherBundlePage.selectVoucherSegmentByIndex(i,"Local");
			addVoucherBundlePage.selectVoucherDenominationByIndex(i,initiateMap.get("denominationName") + "(" + Integer.toString(Integer.parseInt(initiateMap.get("mrp"))) + ".0)");
			addVoucherBundlePage.selectVoucherProfileByIndex(i,initiateMap.get("activeProfile"));
			quantity = Integer.parseInt(randStr.randomNumeric(1))+1;
			addVoucherBundlePage.enterVoucherQuantityByIndex(i,Integer.toString(quantity));
			bundleProfiles[i] = initiateMap.get("activeProfile");
			price = quantity * Integer.parseInt(initiateMap.get("mrp"));
			totalExpectedMRP += price;
				}catch(NoSuchElementException e) {
			}}}
			addVoucherBundlePage.clickSubmit();
			addVoucherBundleConfirmPage.clickSubmit();
			
			String MRP = DBHandler.AccessHandler.fetchMRPforBundle(initiateMap.get("voucherBundleName")); 
			Pattern pattern = Pattern.compile("([0-9]*).*");
			Matcher matcher = pattern.matcher(MRP); 
			if(matcher.find()){
			    MRP = matcher.group(1);		//extract MRP without decimal
			}else{
			    System.out.println("Not Found");
			}
			Log.info("MRP of Bundle is: " + MRP );
			if(totalExpectedMRP == Integer.parseInt(MRP) )
				Assertion.assertPass("Retail Price calculated is correct");
			
			String successMessage = addVoucherBundlePage.getMessage();
			if(successMessage != null){
				initiateMap.put("MessageStatus","Y");
				initiateMap.put("Message",successMessage);
				//Add Bundle profiles data to Excel sheet
				BuilderLogic VBSheet = new BuilderLogic();
				VBSheet.writeVOMSBundleSheetForO2CTransfer(bundleProfiles,initiateMap.get("voucherBundleName"),initiateMap.get("voucherBundlePrefix"),MRP);
			}else{
				initiateMap.put("MessageStatus","N");
				String failureMessage = addVoucherBundlePage.getErrorMessage();
				initiateMap.put("Message",failureMessage);
			}		
			
			return initiateMap;
		}
}
