package com.Features;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.CacheController;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.AddAdditionalCommissionDetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.AddCommissionProfileConfirmPage;
import com.pageobjects.networkadminpages.commissionprofile.AddCommissionProfileDetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.AddCommissionProfilePage;
import com.pageobjects.networkadminpages.commissionprofile.CommissionProfilePage;
import com.pageobjects.networkadminpages.commissionprofile.CommissionProfileStatus;
import com.pageobjects.networkadminpages.commissionprofile.CommissionProfileStatusPage;
import com.pageobjects.networkadminpages.commissionprofile.ModifyAdditionalCommProfileDetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.ModifyCommPage_Vodafone;
import com.pageobjects.networkadminpages.commissionprofile.ModifyCommProfileConfirmPage;
import com.pageobjects.networkadminpages.commissionprofile.ModifyCommProfiledetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.ModifyCommissionProfilePage;
import com.pageobjects.networkadminpages.commissionprofile.OtherCommisionProfilePage;
import com.pageobjects.networkadminpages.commissionprofile.ViewCommissionProfileDetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.ViewCommissionProfilePage2;
import com.pageobjects.networkadminpages.commissionprofile.viewCommissionProfilePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.ProfileManagementSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.preferences.SystemPreferencePage;
import com.pretupsControllers.BTSLUtil;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;
import com.utils._masterVO;

public class CommissionProfile extends BaseTest {

	public WebDriver driver;

	String MasterSheetPath = _masterVO.getProperty("DataProvider");
	NetworkAdminHomePage homePage;
	Login login;
	RandomGeneration RandomGenerator;
	CommissionProfilePage commissionProfilePage;
	AddCommissionProfilePage addCommissionProfilePage;
	AddCommissionProfileDetailsPage addProfileDetailsPage;
	AddAdditionalCommissionDetailsPage addAdditionalCommissionDetailsPage;
	ProfileManagementSubCategories profileMgmntSubCats;
	AddCommissionProfileConfirmPage commissionConfirmPage;
	CommissionProfileStatusPage commissionProfileStatusPage;
	ModifyCommissionProfilePage modifyCommissionProfilePage;
	ModifyCommProfiledetailsPage modifyCommProfiledetailsPage;
	ModifyAdditionalCommProfileDetailsPage modifyAdditionalCommProfileDetailsPage;
	ModifyCommProfileConfirmPage modifyCommProfileConfirmPage;
	ModifyCommPage_Vodafone modifyCommPage_Vodafone;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	String[] result;
	SelectNetworkPage selectNetworkPage;
	SystemPreferencePage systemPreferencePage;
	viewCommissionProfilePage viewCommissionProfilePage;
	ViewCommissionProfilePage2 ViewCommissionProfilePage2;
	ViewCommissionProfileDetailsPage ViewCommissionProfileDetailsPage;
	CommissionProfileStatusPage CommissionProfileStatusPage;
	CommissionProfileStatus CommissionProfileStatus;
	Map_CommissionProfile Map_CommissionProfile;
	CacheUpdate CacheUpdate;
	String selectedNetwork;
	int subSlabCount = 0;
	String targetbasedCommission = "";
	String targetbasedaddtnlcommission = "";
	SuperAdminHomePage saHomePage;
	OtherCommisionProfilePage otherCommissionProfilePage;

	int COMM_PROFILE_CLIENTVER;
	int DUAL_COMM_FIELD_AVAILABLE;
	int ADD_COMM_VER;
	int OTF_SLAB_ASSIGN;
	WebDriverWait wait=null;;

	public CommissionProfile(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		RandomGenerator = new RandomGeneration();
		commissionProfilePage = new CommissionProfilePage(driver);
		addCommissionProfilePage = new AddCommissionProfilePage(driver);
		addProfileDetailsPage = new AddCommissionProfileDetailsPage(driver);
		addAdditionalCommissionDetailsPage = new AddAdditionalCommissionDetailsPage(driver);
		profileMgmntSubCats = new ProfileManagementSubCategories(driver);
		commissionConfirmPage = new AddCommissionProfileConfirmPage(driver);
		commissionProfileStatusPage = new CommissionProfileStatusPage(driver);
		modifyCommissionProfilePage = new ModifyCommissionProfilePage(driver);
		modifyCommProfiledetailsPage = new ModifyCommProfiledetailsPage(driver);
		modifyCommPage_Vodafone = new ModifyCommPage_Vodafone(driver);
		modifyCommProfileConfirmPage = new ModifyCommProfileConfirmPage(driver);
		modifyAdditionalCommProfileDetailsPage = new ModifyAdditionalCommProfileDetailsPage(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
		viewCommissionProfilePage = new viewCommissionProfilePage(driver);
		ViewCommissionProfilePage2 = new ViewCommissionProfilePage2(driver);
		ViewCommissionProfileDetailsPage = new ViewCommissionProfileDetailsPage(driver);
		CommissionProfileStatusPage = new CommissionProfileStatusPage(driver);
		CommissionProfileStatus = new CommissionProfileStatus(driver);
		Map_CommissionProfile = new Map_CommissionProfile(driver);
		CacheUpdate = new CacheUpdate(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
		saHomePage = new SuperAdminHomePage(driver);
		systemPreferencePage = new SystemPreferencePage(driver);
		otherCommissionProfilePage = new OtherCommisionProfilePage(driver);
		selectedNetwork = _masterVO.getMasterValue("Network Code");
		if (DBHandler.AccessHandler.getNetworkPreference(selectedNetwork,
				"TARGET_BASED_ADDNL_COMMISSION_SLABS") != null) {
			subSlabCount = Integer.parseInt(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork,
					"TARGET_BASED_ADDNL_COMMISSION_SLABS"));
		}
		if (DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, "TARGET_BASED_BASE_COMMISSION") != null) {
			targetbasedCommission = DBHandler.AccessHandler.getNetworkPreference(selectedNetwork,
					"TARGET_BASED_BASE_COMMISSION");
		}
		if (DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, "TARGET_BASED_ADDNL_COMMISSION") != null) {
			targetbasedaddtnlcommission = DBHandler.AccessHandler.getNetworkPreference(selectedNetwork,
					"TARGET_BASED_ADDNL_COMMISSION");
		}

		COMM_PROFILE_CLIENTVER = Integer.parseInt(_masterVO.getClientDetail("MULTIPAGECOMMISSIONSELECTION"));
		DUAL_COMM_FIELD_AVAILABLE = Integer.parseInt(_masterVO.getClientDetail("DUAL_COMMISSION_FieldType"));
		ADD_COMM_VER = Integer.parseInt(_masterVO.getClientDetail("ADD_COMM_VER"));
		OTF_SLAB_ASSIGN = Integer.parseInt(_masterVO.getClientDetail("OTF_SLAB_ASSIGN"));
		wait = new WebDriverWait(driver,50);
	}

	public String[] addCommissionProfile(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		String currDate = homePage.getDate();
		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(currDate);
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}


		if (OTF_SLAB_ASSIGN == 0) {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
						addProfileDetailsPage.enterCBCTimeSlab(slabIndex, _masterVO.getProperty("BaseTimeSlab"));
						for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
							if (detailIndex != 0) {
								addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex);
							}
							addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
						}
					} else {
						Log.info("Target based commission is not applicable.");
					}

					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAddButton();

				SwitchWindow.backwindow(driver);
			}
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
				{addProfileDetailsPage.selectTransactionType("ALL");
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
					addProfileDetailsPage.selectPaymentMode("ALL");}
				
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAddButton();

				SwitchWindow.backwindow(driver);

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}

				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
					}

				} else {
					Log.info("Target based commission is not applicable.");
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
			}

		}

		// Fill in AddAdditionalCommissionSlabs
		// To Check if Additional Commission slabs exists or not.
		boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
		if (addCommVisibity == true) {
			result[0] = "Y";
			addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();

			SwitchWindow.switchwindow(driver);
			int index1 = addAdditionalCommissionDetailsPage.getServicesIndex();

			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			int i = 1, j = 1;
			while (i <= index1) {
				{
					addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
					SwitchWindow.switchwindow(driver);

					addAdditionalCommissionDetailsPage.selectService(i);

					// Check the existence of Sub Service field. (separate
					// slab for each sub service will be added)
					boolean subservice = addAdditionalCommissionDetailsPage.subServiceVisibility();
					if (subservice == true) {
						int index2 = addAdditionalCommissionDetailsPage.getSubServiceIndex();
						addAdditionalCommissionDetailsPage.selectSubServiceCode(j);
						if (j != index2)
							j++;
						else {
							i++;
							j = 1;
						} // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
							// for subservice
					}

					// Fill in all the details
					if (COMM_PROFILE_CLIENTVER > 0)
						addAdditionalCommissionDetailsPage.selectGatewayCodeAll(_masterVO.getProperty("GatewayCode"));
					addAdditionalCommissionDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
					addAdditionalCommissionDetailsPage
							.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue1"));
					if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
						addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
						String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
						addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
					}

					int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
					boolean roamCommTypeVisibity = addAdditionalCommissionDetailsPage.roamCommTypeVisibility();

					for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
						boolean even = false;

						if (slabIndex % 2 == 0) {
							even = true;
						}
						String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
						addAdditionalCommissionDetailsPage.enterStartEndRange(slabIndex);
						addAdditionalCommissionDetailsPage.enterCommissionSelectType(slabIndex, even);
						if (roamCommTypeVisibity == true) {
							addAdditionalCommissionDetailsPage.enterRoamCommissionSelectType(slabIndex, even);
						}
						addAdditionalCommissionDetailsPage.enterDifferentialSelectType(slabIndex, even);
						addAdditionalCommissionDetailsPage.enterTax1SelectType(slabIndex, even);
						addAdditionalCommissionDetailsPage.enterTax2SelectType(slabIndex, even);
						if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
							addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
							addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
									_masterVO.getProperty("TimeSlab")); // LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
							for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
								if (detailIndex != 0) {
									addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex);
								}
								addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
										subSlabCount);
							}
						}
					}

					if (subservice == false) {
						i++;
						j = 1; // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
								// for subservice
					}

					// Switch to the main Commission Profile details page
					addAdditionalCommissionDetailsPage.clickAddButton();
					SwitchWindow.backwindow(driver);

				}
			}
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		/*if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
			addCommissionProfilePage.selectOtherCommissionType(ocpData.get("type"));
			addCommissionProfilePage.selectOtherCommissionTypeValue(ocpData.get("typeValue"));
			addCommissionProfilePage.selectOtherCommissionProfile(ocpData.get("profile"));			
		}*/
		

		addCommissionProfilePage.enterApplicableFromDate(currDate);
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		Thread.sleep(2000);
		//addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		result[2] = commissionProfilePage.getActualMsg();
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		if (result[2].equals(Message)) {
			CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		}

		Log.methodExit(methodname);
		return result;
	}

	public String[] addCommissionProfilewithoutAdditionalCommission(String domainName, String categoryName,
			String grade) throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
						addProfileDetailsPage.enterCBCTimeSlab(slabIndex, _masterVO.getProperty("BaseTimeSlab"));
						for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
							if (detailIndex != 0) {
								addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex);
							}
							addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
						}
					} else {
						Log.info("Target based commission is not applicable.");
					}

					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAddButton();

				SwitchWindow.backwindow(driver);
			}
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
				{addProfileDetailsPage.selectTransactionType("ALL");
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
					addProfileDetailsPage.selectPaymentMode("ALL");}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAddButton();

				SwitchWindow.backwindow(driver);

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}

				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
					}

				} else {
					Log.info("Target based commission is not applicable.");
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
			}

		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		result[2] = commissionProfilePage.getActualMsg();
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		if (result[2].equals(Message)) {
			CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		}

		Log.methodExit(methodname);
		return result;
	}

	public String[] addCommissionProfileCBCvalidation(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			result[2] = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			Thread.sleep(2000);
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			if (isProductCodeVisible == true) {
				
			addCommissionProfilePage.clickAssignOtfSlabs();

			
			SwitchWindow.switchwindow(driver);
			if (isProductCodeVisible == true) {

			}
			
			//Enter Other Commission Details
			addOtherCommissionDetails(domainName,categoryName);

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
				for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
					addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
				}

			} else {
				Log.info("Target based commission is not applicable.");
			}

			result[2]=addProfileDetailsPage.clickAddOtfButtonwhenPopup();
			//result[2] = addProfileDetailsPage.getErrorMessage();

			SwitchWindow.backwindow(driver);

		}
			else {
				result[2]="skip";
			}
		}

		Log.methodExit(methodname);
		return result;
	}

	public String[] addCommissionProfileDelete(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[4];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			result[2] = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			Thread.sleep(2000);
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
				{addProfileDetailsPage.selectTransactionType("ALL");
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
					addProfileDetailsPage.selectPaymentMode("ALL");}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAddButton();

				SwitchWindow.backwindow(driver);

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}

				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
					}

				} else {
					Log.info("Target based commission is not applicable.");
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
			}

			modifyCommProfiledetailsPage.ModifyComm();

			// Window handler
			SwitchWindow.switchwindow(driver);

			addProfileDetailsPage.clickDeleteButton();

			result[2] = addProfileDetailsPage.getErrorMessage();
			modifyCommProfiledetailsPage.close();
			SwitchWindow.backwindow(driver);
			
			boolean flag = modifyCommProfiledetailsPage.visibleModifyCommOtf1();
			if (flag) {
			modifyCommProfiledetailsPage.ModifyComm1();

			// Window handler
			SwitchWindow.switchwindow(driver);

			addProfileDetailsPage.clickDeleteButton();

			result[3] = addProfileDetailsPage.getErrorMessage();
			modifyCommProfiledetailsPage.close();
			SwitchWindow.backwindow(driver);
			}

		}
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		Log.methodExit(methodname);
		return result;

	}

	public String[] addCommissionProfilecheckotf(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[4];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			result[2] = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)

			addCommissionProfilePage.clickAssignOtfSlabs();

			//Enter Other Commission Details
			addOtherCommissionDetails(domainName,categoryName);
			
			try {
				driver.switchTo().alert().accept();
				result[2] = "pass";
			} // try
			catch (NoAlertPresentException Ex) {
				result[2] = "fail";
			}

			SwitchWindow.switchwindow(driver);

		}

		Log.methodExit(methodname);
		return result;

	}

	public String[] addCommissionProfilecheckadditional(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[4];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			result[2] = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)

			boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
			if (addCommVisibity == true) {
			addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();

			//Enter Other Commission Details
			addOtherCommissionDetails(domainName,categoryName);
			
			try {
				driver.switchTo().alert().accept();
				result[2] = "pass";
			} // try
			catch (NoAlertPresentException Ex) {
				result[2] = "fail";
			}

			SwitchWindow.switchwindow(driver);

		}
			else {
				result[2] = "skip";
			}
		}

		Log.methodExit(methodname);
		return result;

	}

	public String[] addCommissionProfilecheckNegative(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[4];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName("");
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			result[2] = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

			// Save and Confirm the Commission Profile.
			addCommissionProfilePage.clickSaveButton();
			result[2] = commissionProfilePage.getActualMsg();
		}

		Log.methodExit(methodname);
		return result;

	}

	public String[] addCommissionProfilecheckNegative1(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[4];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode("");
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			result[2] = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

			// Save and Confirm the Commission Profile.
			addCommissionProfilePage.clickSaveButton();
			result[2] = commissionProfilePage.getActualMsg();

		}

		Log.methodExit(methodname);
		return result;

	}

	public String[] addCommissionProfilecheckNegativedate(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[4];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			result[2] = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.enterApplicableFromDate("");
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

			// Save and Confirm the Commission Profile.
			addCommissionProfilePage.clickSaveButton();
			result[2] = commissionProfilePage.getActualMsg();

		}

		Log.methodExit(methodname);
		return result;

	}

	public String[] addCommissionProfilecheckNegativetime(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[4];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			result[2] = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour("");

			// Save and Confirm the Commission Profile.
			addCommissionProfilePage.clickSaveButton();
			result[2] = commissionProfilePage.getActualMsg();

		}

		Log.methodExit(methodname);
		return result;

	}

	public String[] addCommissionProfileCBC(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 1) {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}

			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {

				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				int transactionIndex;
				transactionIndex = addProfileDetailsPage.getTransactionTypeIndex();

				for (int j = 0; j < transactionIndex; j++) {
					// Fill in all the details
					if (isProductCodeVisible == true) {
						addProfileDetailsPage.selectProductCode(i);
					}
					addProfileDetailsPage.selectTransactionType(j);

					int paymentIndex = 1;
					boolean flag = addProfileDetailsPage.getValueFromDropDown("Operator to Channel");
					if (flag) {
						if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
						paymentIndex = addProfileDetailsPage.getPaymentModeIndex();
					}

					for (int k = 0; k < paymentIndex; k++) {

						if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
							addProfileDetailsPage.selectPaymentMode(k);
						addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
						addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
						addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
						addProfileDetailsPage.clickTaxOnFOCFlag();
						addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

						/*
						 * To be removed if not in use by Lokesh
						 */
						int slabCount = addProfileDetailsPage.totalSlabs();
						CONSTANT.COMM_SLAB_COUNT = slabCount;

						for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
							boolean even = false;

							if (slabIndex % 2 == 0) {
								even = true;
							}
							addProfileDetailsPage.enterStartEndRange(slabIndex);
							addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
							addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
							addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
							addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
							Log.info("commission varaible : " + slabIndex);
						}

						addProfileDetailsPage.clickAddButton();

						SwitchWindow.backwindow(driver);

						if (k < (paymentIndex - 1)) {

							addCommissionProfilePage.clickAssignCommissionSlabs();

							// Window handler
							SwitchWindow.switchwindow(driver);

							if (isProductCodeVisible == true) {
								addProfileDetailsPage.selectProductCode(i);
							}
							addProfileDetailsPage.selectTransactionType(j);

						}

					}

					if (j < (transactionIndex - 1)) {

						addCommissionProfilePage.clickAssignCommissionSlabs();

						// Window handler
						SwitchWindow.switchwindow(driver);

					}

				}

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}

				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
					}

				} else {
					Log.info("Target based commission is not applicable.");
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
			}

		}

		// Fill in AddAdditionalCommissionSlabs
		// To Check if Additional Commission slabs exists or not.
		boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
		if (addCommVisibity == true) {
			result[0] = "Y";
			addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();

			SwitchWindow.switchwindow(driver);
			int index1 = addAdditionalCommissionDetailsPage.getServicesIndex();

			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			int i = 1, j = 1;
			while (i <= index1) {
				{
					addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
					SwitchWindow.switchwindow(driver);

					addAdditionalCommissionDetailsPage.selectService(i);

					// Check the existence of Sub Service field. (separate
					// slab for each sub service will be added)
					boolean subservice = addAdditionalCommissionDetailsPage.subServiceVisibility();
					if (subservice == true) {
						int index2 = addAdditionalCommissionDetailsPage.getSubServiceIndex();
						addAdditionalCommissionDetailsPage.selectSubServiceCode(j);
						if (j != index2)
							j++;
						else {
							i++;
							j = 1;
						} // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
							// for subservice
					}

					// Fill in all the details
					if (COMM_PROFILE_CLIENTVER > 0)
						addAdditionalCommissionDetailsPage.selectGatewayCodeAll(_masterVO.getProperty("GatewayCode"));
					addAdditionalCommissionDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
					addAdditionalCommissionDetailsPage
							.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue1"));
					if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
						addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
						String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
						addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
					}

					int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
					boolean roamCommTypeVisibity = addAdditionalCommissionDetailsPage.roamCommTypeVisibility();

					for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
						boolean even = false;

						if (slabIndex % 2 == 0) {
							even = true;
						}
						String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
						addAdditionalCommissionDetailsPage.enterStartEndRange(slabIndex);
						addAdditionalCommissionDetailsPage.enterCommissionSelectType(slabIndex, even);
						if (roamCommTypeVisibity == true) {
							addAdditionalCommissionDetailsPage.enterRoamCommissionSelectType(slabIndex, even);
						}
						addAdditionalCommissionDetailsPage.enterDifferentialSelectType(slabIndex, even);
						addAdditionalCommissionDetailsPage.enterTax1SelectType(slabIndex, even);
						addAdditionalCommissionDetailsPage.enterTax2SelectType(slabIndex, even);
						if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
							addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
							addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
									_masterVO.getProperty("TimeSlab")); // LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
							for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
								if (detailIndex != 0) {
									addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex);
								}
								addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
										subSlabCount);
							}
						}
					}

					if (subservice == false) {
						i++;
						j = 1; // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
								// for subservice
					}

					// Switch to the main Commission Profile details page
					addAdditionalCommissionDetailsPage.clickAddButton();
					SwitchWindow.backwindow(driver);

				}
			}
		}	

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		result[2] = commissionProfilePage.getActualMsg();
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		if (result[2].equals(Message)) {
			CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		}

		Log.methodExit(methodname);
		return result;
	}

	public String[] addCommissionProfileCBC(String domainName, String categoryName, String grade, String Comtype)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String[] message = {};
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(Comtype);
		}
		if (Comtype.equals("PC")) {
			driver.switchTo().alert().accept();
		}
		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 1) {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}

			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {

				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				int transactionIndex;
				transactionIndex = addProfileDetailsPage.getTransactionTypeIndex();

				for (int j = 0; j < transactionIndex; j++) {
					// Fill in all the details
					if (isProductCodeVisible == true) {
						addProfileDetailsPage.selectProductCode(i);
					}
					addProfileDetailsPage.selectTransactionType(j);

					int paymentIndex = 1;
					boolean flag = addProfileDetailsPage.getValueFromDropDown("Operator to Channel");
					if (flag) {
						if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
							paymentIndex = addProfileDetailsPage.getPaymentModeIndex();
					}

					for (int k = 0; k < paymentIndex; k++) {
						if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
						addProfileDetailsPage.selectPaymentMode(k);
						addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
						addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
						addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
						addProfileDetailsPage.clickTaxOnFOCFlag();
						addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

						/*
						 * To be removed if not in use by Lokesh
						 */
						int slabCount = addProfileDetailsPage.totalSlabs();
						CONSTANT.COMM_SLAB_COUNT = slabCount;

						for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
							boolean even = false;

							if (slabIndex % 2 == 0) {
								even = true;
							}
							addProfileDetailsPage.enterStartEndRange(slabIndex);
							addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
							addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
							addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
							addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
							Log.info("commission varaible : " + slabIndex);
						}

						addProfileDetailsPage.clickAddButton();

						SwitchWindow.backwindow(driver);

						if (k < (paymentIndex - 1)) {

							addCommissionProfilePage.clickAssignCommissionSlabs();

							// Window handler
							SwitchWindow.switchwindow(driver);

							if (isProductCodeVisible == true) {
								addProfileDetailsPage.selectProductCode(i);
							}
							addProfileDetailsPage.selectTransactionType(j);

						}

					}

					if (j < (transactionIndex - 1)) {

						addCommissionProfilePage.clickAssignCommissionSlabs();

						// Window handler
						SwitchWindow.switchwindow(driver);

					}

				}

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}

				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
					}

				} else {
					Log.info("Target based commission is not applicable.");
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
			}

			// Fill in AddAdditionalCommissionSlabs
			// To Check if Additional Commission slabs exists or not.
			boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
			if (addCommVisibity == true) {
				result[0] = "Y";
				addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();

				SwitchWindow.switchwindow(driver);
				int index1 = addAdditionalCommissionDetailsPage.getServicesIndex();

				addProfileDetailsPage.clickCloseButton();
				SwitchWindow.backwindow(driver);

				int i = 1, j = 1;
				while (i <= index1) {
					{
						addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
						SwitchWindow.switchwindow(driver);

						addAdditionalCommissionDetailsPage.selectService(i);

						// Check the existence of Sub Service field. (separate
						// slab for each sub service will be added)
						boolean subservice = addAdditionalCommissionDetailsPage.subServiceVisibility();
						if (subservice == true) {
							int index2 = addAdditionalCommissionDetailsPage.getSubServiceIndex();
							addAdditionalCommissionDetailsPage.selectSubServiceCode(j);
							if (j != index2)
								j++;
							else {
								i++;
								j = 1;
							} // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
								// for subservice
						}

						// Fill in all the details
						if (COMM_PROFILE_CLIENTVER > 0)
							addAdditionalCommissionDetailsPage
									.selectGatewayCodeAll(_masterVO.getProperty("GatewayCode"));
						addAdditionalCommissionDetailsPage
								.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
						addAdditionalCommissionDetailsPage
								.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue1"));
						if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
							addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
							String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
							addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
						}

						int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
						boolean roamCommTypeVisibity = addAdditionalCommissionDetailsPage.roamCommTypeVisibility();

						for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
							boolean even = false;

							if (slabIndex % 2 == 0) {
								even = true;
							}
							String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
							addAdditionalCommissionDetailsPage.enterStartEndRange(slabIndex);
							addAdditionalCommissionDetailsPage.enterCommissionSelectType(slabIndex, even);
							if (roamCommTypeVisibity == true) {
								addAdditionalCommissionDetailsPage.enterRoamCommissionSelectType(slabIndex, even);
							}
							addAdditionalCommissionDetailsPage.enterDifferentialSelectType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterTax1SelectType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterTax2SelectType(slabIndex, even);
							if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
								addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
								addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
								addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
										_masterVO.getProperty("TimeSlab")); // LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
								for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
									if (detailIndex != 0) {
										addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex);
									}
									addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
											subSlabCount);
								}
							}
						}

						if (subservice == false) {
							i++;
							j = 1; // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
									// for subservice
						}

						// Switch to the main Commission Profile details page
						addAdditionalCommissionDetailsPage.clickAddButton();
						SwitchWindow.backwindow(driver);

					}
				}
			}

			//Enter Other Commission Details
			addOtherCommissionDetails(domainName,categoryName);

			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

			// Save and Confirm the Commission Profile.
			addCommissionProfilePage.clickSaveButton();
			commissionConfirmPage.clickConfirmButton();
			result[2] = commissionProfilePage.getActualMsg();
			String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage",
					"");
			if (result[2].equals(Message)) {
				CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
				return result;
			}
		} else if (OTF_SLAB_ASSIGN == 0) {
			result[2] = "skip";

		}
		Log.methodExit(methodname);
		return result;

	}

	public String[] addCommissionProfileWithSpecificGeography(String domainName, String categoryName, String grade)
			throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int c = 1;
		for (c = 1; c <= totalRow1; c++)

		{
			if ((ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, c).matches(domainName))
					&& (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, c).matches(categoryName)))

				break;
		}

		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, c));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		String currDate = homePage.getDate();
		if (OTF_SLAB_ASSIGN == 0) {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
						addProfileDetailsPage.enterCBCTimeSlab(slabIndex, _masterVO.getProperty("BaseTimeSlab"));
						for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
							if (detailIndex != 0) {
								addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex);
							}
							addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
						}
					} else {
						Log.info("Target based commission is not applicable.");
					}

					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAddButton();

				SwitchWindow.backwindow(driver);
			}
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
				{addProfileDetailsPage.selectTransactionType("ALL");
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
					addProfileDetailsPage.selectPaymentMode("ALL");}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAddButton();

				SwitchWindow.backwindow(driver);

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}

				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
					}

				} else {
					Log.info("Target based commission is not applicable.");
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
			}

		}

		// Fill in AddAdditionalCommissionSlabs
		// To Check if Additional Commission slabs exists or not.
		boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
		if (addCommVisibity == true) {
			result[0] = "Y";
			addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();

			SwitchWindow.switchwindow(driver);
			int index1 = addAdditionalCommissionDetailsPage.getServicesIndex();
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			int i = 1, j = 1;
			while (i <= index1) {
				{
					addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
					SwitchWindow.switchwindow(driver);

					addAdditionalCommissionDetailsPage.selectService(i);
					if (COMM_PROFILE_CLIENTVER > 0)
						addAdditionalCommissionDetailsPage.selectGatewayCodeAll(_masterVO.getProperty("GatewayCode"));
					addAdditionalCommissionDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
					addAdditionalCommissionDetailsPage
							.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue1"));
					if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
						addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
						String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
						addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
					}
					// Check the existence of Sub Service field. (separate
					// slab for each sub service will be added)
					boolean subservice = addAdditionalCommissionDetailsPage.subServiceVisibility();
					if (subservice == true) {
						int index2 = addAdditionalCommissionDetailsPage.getSubServiceIndex();
						addAdditionalCommissionDetailsPage.selectSubServiceCode(j);
						if (j != index2)
							j++;
						else {
							i++;
							j = 1;
						} // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
							// for subservice
					}

					// Fill in all the details
					int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
					boolean roamCommTypeVisibity = addAdditionalCommissionDetailsPage.roamCommTypeVisibility();
					for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
						boolean even = false;

						if (slabIndex % 2 == 0) {
							even = true;
						}
						String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
						addAdditionalCommissionDetailsPage.enterStartEndRange(slabIndex);
						addAdditionalCommissionDetailsPage.enterCommissionSelectType(slabIndex, even);
						if (roamCommTypeVisibity == true) {
							addAdditionalCommissionDetailsPage.enterRoamCommissionSelectType(slabIndex, even);
						}
						addAdditionalCommissionDetailsPage.enterDifferentialSelectType(slabIndex, even);
						addAdditionalCommissionDetailsPage.enterTax1SelectType(slabIndex, even);
						addAdditionalCommissionDetailsPage.enterTax2SelectType(slabIndex, even);
						if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
							addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
							addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
									_masterVO.getProperty("TimeSlab"));
							for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
								if (detailIndex != 0) {
									addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex);
								}
								addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
										subSlabCount);
							}
						}
					}

					if (subservice == false) {
						i++;
						j = 1; // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
								// for subservice
					}

					// Switch to the main Commission Profile details page
					addAdditionalCommissionDetailsPage.clickAddButton();
					SwitchWindow.backwindow(driver);

				}
			}
		}
		

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		result[2] = commissionProfilePage.getActualMsg();

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return result;
	}

	public String viewCommissionProfile(String domainName, String categoryName, String grade, String commProfile)
			throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickViewButon();
		viewCommissionProfilePage.selectCommissionProfileSet(commProfile);
		viewCommissionProfilePage.selectNoOfDays(_masterVO.getProperty("NoOfDays"));
		viewCommissionProfilePage.clickSubmit();
		ViewCommissionProfilePage2.SelectCommProfileVersion();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			String CommissionType = ViewCommissionProfilePage2.getCommissionType("1");
			Log.info("The Commission Type for CommProfile:" + commProfile + "and version: '1' is :" + CommissionType);
		}
		ViewCommissionProfilePage2.clickSubmit();
		String actual = ViewCommissionProfileDetailsPage.getMessage();
		return actual;

	}

	public String modifyCommissionProfile(String domainName, String categoryName, String grade, String commProfile)
			throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();

		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String currDate = homePage.getDate();

		modifyCommProfiledetailsPage.ModifyComm();

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();

		if (OTF_SLAB_ASSIGN == 0) {
			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
				}
				Log.info("commission varaible : " + slabIndex);
			}
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);
		} else {
			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				addProfileDetailsPage.enterStartEndRange(slabIndex);
			}
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
			}

			addProfileDetailsPage.clickAddOtfButton();

			SwitchWindow.backwindow(driver);
		}

		boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
		if (addCommVisibity == true) {
			modifyCommProfiledetailsPage.ModifyAdditionalComm();
			// Window handler
			SwitchWindow.switchwindow(driver);
			
			if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
				addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
				String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
				addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
			}

			int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
			for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
				String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
				addAdditionalCommissionDetailsPage.enterStartEndRange(slabIndex);
				if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
					addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
				}
			}

			addAdditionalCommissionDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

		}


		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();

		String message = commissionProfilePage.getActualMsg();

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return message;
	}

	public String modifyCommissionProfileForFutureDate(String domainName, String categoryName, String grade,
			String commProfile) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String currDate = homePage.getDate();
		modifyCommProfiledetailsPage.ModifyComm();

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();

		if (OTF_SLAB_ASSIGN == 0) {

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
				}
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

		}

		else {
			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				addProfileDetailsPage.enterStartEndRange(slabIndex);
			}
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 5);
			String newfromDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
			}

			addProfileDetailsPage.clickAddOtfButton();

			SwitchWindow.backwindow(driver);
			boolean flag = modifyCommProfiledetailsPage.visibleModifyCommOtf1();
			if (flag) {
				modifyCommProfiledetailsPage.ModifyCommOtf1();

				// Window handler
				SwitchWindow.switchwindow(driver);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);

			}

		}

		boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
		if (addCommVisibity == true) {
			int size = modifyCommProfiledetailsPage.ModifyAdditionalCommCount();

			int i = 0, j = 1;
			while (i < size - 1) {

				modifyCommProfiledetailsPage.ModifyAdditionalComm(i);
				// Window handler
				SwitchWindow.switchwindow(driver);
				currDate = homePage.addDaysToCurrentDate(currDate, 1);
				if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
					addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
					String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
					addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
				}

				int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
				for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
						
					}
				}

				addAdditionalCommissionDetailsPage.clickAddButton();
				SwitchWindow.backwindow(driver);
				i++;

			}

		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.addDaysToCurrentDate(homePage.getDate(), 1));
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();

		String message = commissionProfilePage.getActualMsg();

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return message;
	}

	public String modifyAdditionalCommissionProfile(String domainName, String categoryName, String grade,
			String commProfile) throws InterruptedException {

		String message = null;

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String version = DBHandler.AccessHandler.getCommProfileVersion(commProfile);
		Log.info("The version is:" + version);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);

		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.selectVersion(version);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String currDate = homePage.getDate();

		int size = modifyCommProfiledetailsPage.ModifyAdditionalCommCount();

		int i = 0, j = 1;
		while (i < size - 1) {

			modifyCommProfiledetailsPage.ModifyAdditionalComm(i);
			// Window handler
			SwitchWindow.switchwindow(driver);
			currDate = homePage.addDaysToCurrentDate(currDate, 1);
			if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
				addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
				String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
				addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
			}

			int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
			for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
				String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
				}
			}

			addAdditionalCommissionDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);
			i++;

		}

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String appTime = homePage.getApplicableFromTime();
		addCommissionProfilePage.enterApplicableFromHour(appTime);
		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		String time2 = appTime+":00";long requiredtime = 0;
		try {
			requiredtime = homePage.getTimeDifferenceInSeconds(time2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		message = commissionProfilePage.getActualMsg();

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		
		Thread.sleep(requiredtime);
		
		return message;
	}

	public String deleteCommProfile(String domainName, String categoryName, String grade, String profileName)
			throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();
		modifyCommissionProfilePage.selectCommissionProfileSet(profileName);
		modifyCommissionProfilePage.clickDeleteButton();
		driver.switchTo().alert().accept();

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);

		String StatusText = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + StatusText);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return StatusText;
	}

	public String deleteAdditionalCommProfile(String domainName, String categoryName, String grade, String profileName)
			throws InterruptedException {

		String message = null;
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String latestversion = DBHandler.AccessHandler.getCommProfileVersion(profileName);
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();
		modifyCommissionProfilePage.selectCommissionProfileSet(profileName);
		modifyCommissionProfilePage.selectVersion(latestversion);
		modifyCommissionProfilePage.clickModifyButton();

		modifyCommProfiledetailsPage.ModifyAdditionalComm();

		SwitchWindow.switchwindow(driver);

		modifyAdditionalCommProfileDetailsPage.clickDeleteSlab();

		SwitchWindow.backwindow(driver);

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		modifyCommProfiledetailsPage.clickSave();
		modifyCommProfileConfirmPage.confirmButton();

		message = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + message);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return message;
	}

	public String addCommissionProfileWithDuplicateName(String domainName, String categoryName, String grade,
			String profileName) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		String shortCode = UniqueChecker.UC_CPName();
		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(shortCode);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		String currDate = homePage.getDate();
		if (OTF_SLAB_ASSIGN == 0) {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
						addProfileDetailsPage.enterCBCTimeSlab(slabIndex, _masterVO.getProperty("BaseTimeSlab"));
						for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
							if (detailIndex != 0) {
								addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex);
							}
							addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
						}
					} else {
						Log.info("Target based commission is not applicable.");
					}

					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAddButton();

				SwitchWindow.backwindow(driver);
			}
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
				{addProfileDetailsPage.selectTransactionType("ALL");
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
					addProfileDetailsPage.selectPaymentMode("ALL");}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAddButton();

				SwitchWindow.backwindow(driver);

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}

				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
					}

				} else {
					Log.info("Target based commission is not applicable.");
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
			}

		}

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();

		String actual = commissionConfirmPage.getMessage();
		return actual;
	}

	public void writeCommissionProfileToExcel(int rowNum, String[] result) {
		// changed on 23rdAugust as recommended by Ayush due to setExcelfile issue
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		ExcelUtility.setCellData(0, ExcelI.COMMISSION_PROFILE, rowNum, result[1]);
		ExcelUtility.setCellData(0, ExcelI.ADDITIONAL_COMMISSION, rowNum, result[0]);
	}

	public String addCommissionProfileWithBlankMandatoryField(String domainName, String categoryName, String grade)
			throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details

		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		String currDate = homePage.getDate();
		// Fill in Commission Slabs. (Separate slab for each Product will be
		// added)
		addCommissionProfilePage.clickAssignCommissionSlabs();

		// Initializing Window Handlers
		SwitchWindow.switchwindow(driver);

		// Getting List size of Product.
		int index = 1;
		boolean isProductCodeVisible;
		try {
			index = addProfileDetailsPage.getProductCodeIndex();
			isProductCodeVisible = true;
		} catch (NoSuchElementException e) {
			isProductCodeVisible = false;
		}
		addProfileDetailsPage.clickCloseButton();
		SwitchWindow.backwindow(driver);

		for (int i = 1; i <= 1; i++) {
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(i);
			}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlab(slabIndex, _masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						if (detailIndex != 0) {
							addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex);
						}
						addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
					}
				}
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);
		}

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate("");
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();

		String actual = addCommissionProfilePage.getMessage();
		return actual;
	}

	public String deleteCommProfileAssociatedWithUser(String domainName, String categoryName, String grade)
			throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int c = 1;
		for (c = 1; c <= totalRow1; c++)

		{
			if ((ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, c).matches(domainName))
					&& (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, c).matches(categoryName))
					&& (ExcelUtility.getCellData(0, ExcelI.GRADE, c).matches(grade)))
				break;
		}

		modifyCommissionProfilePage
				.selectCommissionProfileSet(ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, c));

		modifyCommissionProfilePage.clickDeleteButton();
		driver.switchTo().alert().accept();

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);

		String StatusText = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + StatusText);
		return StatusText;
	}

	public String suspendAdditionalCommProfile(String domainName, String categoryName, String grade, String profileName)
			throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String latestVersion = DBHandler.AccessHandler.getCommProfileVersion(profileName);
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();
		modifyCommissionProfilePage.selectCommissionProfileSet(profileName);
		modifyCommissionProfilePage.selectVersion(latestVersion);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}
		modifyCommProfiledetailsPage.ModifyAdditionalComm();

		SwitchWindow.switchwindow(driver);

		modifyAdditionalCommProfileDetailsPage.clickSuspendAdditionalComm();

		SwitchWindow.backwindow(driver);

		String SlabStatus = modifyCommProfiledetailsPage.getStatus();
		String ExpectedSlabStatus = _masterVO.getProperty("AdditionalCommSlabStatus");
		Assert.assertEquals(SlabStatus, ExpectedSlabStatus);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		
		modifyCommProfiledetailsPage.clickSave();
		modifyCommProfileConfirmPage.confirmButton();

		String statusText = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + statusText);

		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("ACTUAL_MESSAGE", statusText);
		dataMap.put("STATUS", SlabStatus);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return statusText;

	}

	public long suspendAdditionalCommProfileExisting(String domainName, String categoryName, String grade,
			String profileName,String  serviceCode) throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}

		commissionProfilePage.clickModifyButton();
		modifyCommissionProfilePage.selectCommissionProfileSet(profileName);
		modifyCommissionProfilePage.clickModifyButton();
		String currDate = homePage.getDate();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i = 1;
		for (i = 1; i <= totalRow1; i++) {
			if ((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i)
					.matches(_masterVO.getProperty("CustomerRechargeCode"))))
				break;
		}
		String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		Log.info("service is:" + service);
		String SlabStatus = modifyCommProfiledetailsPage.getAdditionalSlabStatusofparticularService(service,serviceCode);
		String ExpectedSlabStatus = _masterVO.getProperty("AdditionalCommSlabStatus");
		Log.info("The Expected Status of Customer Recharge Additional Profile is :" + ExpectedSlabStatus);
		if (!SlabStatus.equalsIgnoreCase(ExpectedSlabStatus)) {
			modifyCommProfiledetailsPage.ModifyAdditionalComSpecificService(service,serviceCode);
			SwitchWindow.switchwindow(driver);
			modifyAdditionalCommProfileDetailsPage.clickSuspendAdditionalComm();

			SwitchWindow.backwindow(driver);

			String SlabStatus1 = modifyCommProfiledetailsPage.getAdditionalSlabStatusofparticularService(service,serviceCode);
			Log.info("The Slab Status after Modification is:" + SlabStatus1);
			Assert.assertEquals(SlabStatus1, ExpectedSlabStatus);
			if (SlabStatus1 == ExpectedSlabStatus) {
				ExtentI.Markup(ExtentColor.TEAL,
						"Additional Commission Profile slab with service" + service + "is suspended successfully");
			}

		} else {
			Log.info("The Slab is already suspended");
		}
		if (OTF_SLAB_ASSIGN == 1) {
			currDate = homePage.getDate();
			boolean flag = modifyCommProfiledetailsPage.visibleModifyCommOtf();
			if (flag) {
				modifyCommProfiledetailsPage.ModifyCommOtf();

				// Window handler
				SwitchWindow.switchwindow(driver);

				String toDate = homePage.addDaysToCurrentDate(currDate, 5);
				String newfromDate = homePage.addDaysToCurrentDate(currDate, 3);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
				boolean flag2 = modifyCommProfiledetailsPage.visibleModifyCommOtf1();
				if (flag2) {
					modifyCommProfiledetailsPage.ModifyCommOtf1();

					// Window handler
					SwitchWindow.switchwindow(driver);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
					}

					addProfileDetailsPage.clickAddOtfButton();

					SwitchWindow.backwindow(driver);

				}
			}
		}

		/*
		 * boolean addCommVisibity =
		 * addCommissionProfilePage.addAddititionalCommissionVisibility(); if
		 * (addCommVisibity == true) { int size =
		 * modifyCommProfiledetailsPage.ModifyAdditionalCommCount();
		 * 
		 * int k = 0; while (k < size - 1) {
		 * 
		 * modifyCommProfiledetailsPage.ModifyAdditionalComm(k); // Window handler
		 * SwitchWindow.switchwindow(driver); currDate =
		 * homePage.addDaysToCurrentDate(currDate, 1); if
		 * (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase(
		 * "true")) {
		 * addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate); String
		 * ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
		 * addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate); }
		 * 
		 * int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs(); for
		 * (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) { String
		 * toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
		 * addProfileDetailsPage.enterStartEndRange(slabIndex); if
		 * (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
		 * addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate); } }
		 * 
		 * if (!addAdditionalCommissionDetailsPage.checkStatusValue().equalsIgnoreCase(
		 * "Suspended")) { addAdditionalCommissionDetailsPage.clickAddButton(); }
		 * 
		 * SwitchWindow.backwindow(driver); k++;
		 * 
		 * }
		 * 
		 * }
		 */
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		modifyCommProfiledetailsPage.clickSave();
		modifyCommProfileConfirmPage.confirmButton();

		String statusText = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + statusText);

		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("ACTUAL_MESSAGE", statusText);
		dataMap.put("STATUS", SlabStatus);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;

	}

	public long resumeAdditionalCommProfileSpecificService(String domainName, String categoryName, String grade,
			String profileName,String serviceCode) throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		String date = homePage.getDate();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (_masterVO.getClientDetail("MULTIPAGECOMMISSIONSELECTION").equalsIgnoreCase("true")) {
			commissionProfilePage.clickAddButton();
		}
		commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
		commissionProfilePage.selectGrade(grade);

		commissionProfilePage.clickModifyButton();
		modifyCommissionProfilePage.selectCommissionProfileSet(profileName);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i = 1;
		for (i = 1; i <= totalRow1; i++) {
			if ((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i)
					.matches(_masterVO.getProperty("CustomerRechargeCode"))))
				break;
		}
		String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);

		String SlabStatus = modifyCommProfiledetailsPage.getAdditionalSlabStatusofparticularService(service,serviceCode);
		String SuspendedSlabStatus = _masterVO.getProperty("AdditionalCommSlabStatus");
		Log.info("The Expected Slab Status before resuming is: " + SuspendedSlabStatus);
		if (SlabStatus.equalsIgnoreCase(SuspendedSlabStatus)) {
			modifyCommProfiledetailsPage.ModifyAdditionalComSpecificService(service,serviceCode);
			SwitchWindow.switchwindow(driver);
			modifyAdditionalCommProfileDetailsPage.enterApplicableFromDate(date);
			modifyAdditionalCommProfileDetailsPage.clickResumeAdditionalComm();

			SwitchWindow.backwindow(driver);

			String SlabStatus1 = modifyCommProfiledetailsPage.getAdditionalSlabStatusofparticularService(service,serviceCode);
			Log.info("The slab status after modification is : " + SlabStatus1);
			String ExpectedSlabStatus2 = _masterVO.getProperty("AdditionalCommResumeStatus");
			Assert.assertEquals(SlabStatus1, ExpectedSlabStatus2);
			if (SlabStatus1.equalsIgnoreCase(ExpectedSlabStatus2)) {
				ExtentI.Markup(ExtentColor.TEAL,
						"Additional Commission Profile slab with service " + service + "  is resumed successfully");
			}

		} else {
			Log.info("The Slab is already active");
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		modifyCommProfiledetailsPage.clickSave();
		modifyCommProfileConfirmPage.confirmButton();

		String statusText = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + statusText);

		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("ACTUAL_MESSAGE", statusText);
		dataMap.put("STATUS", SlabStatus);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;

	}

	public long resumeAdditionalCommProfile(String domainName, String categoryName, String grade, String profileName)
			throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (_masterVO.getClientDetail("MULTIPAGECOMMISSIONSELECTION").equalsIgnoreCase("true")) {
			commissionProfilePage.clickAddButton();
		}
		commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
		commissionProfilePage.selectGrade(grade);

		commissionProfilePage.clickModifyButton();
		modifyCommissionProfilePage.selectCommissionProfileSet(profileName);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String SlabStatus = modifyCommProfiledetailsPage.getStatus();

		if (SlabStatus.contentEquals(_masterVO.getProperty("AdditionalCommSlabStatus"))) {

			modifyCommProfiledetailsPage.ModifyAdditionalComm();

			SwitchWindow.switchwindow(driver);

			modifyAdditionalCommProfileDetailsPage.clickResumeAdditionalComm();
			SwitchWindow.backwindow(driver);

		} else {
			Log.info("The slab is already active");
		}

		String SlabStatus2 = modifyCommProfiledetailsPage.getStatus();
		String ExpectedSlabStatus2 = _masterVO.getProperty("AdditionalCommResumeStatus");
		Assert.assertEquals(SlabStatus2, ExpectedSlabStatus2);
		if (SlabStatus2.equalsIgnoreCase(ExpectedSlabStatus2)) {
			ExtentI.Markup(ExtentColor.TEAL, "Additional Commission Profile slab with service is resumed successfully");
		}

		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);
		modifyCommProfiledetailsPage.clickSave();
		modifyCommProfileConfirmPage.confirmButton();

		String statusText = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + statusText);

		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("ACTUAL_MESSAGE", statusText);
		dataMap.put("STATUS", SlabStatus);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;

	}

	public String CommissionProfileStatusChange(String domainName, String categoryName, String grade,
			String profileName) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Verifying Commission profile Status

		homePage.clickProfileManagement();
		profileMgmntSubCats.clickCommissionProfileStatus();
		commissionProfileStatusPage.selectDomain(domainName);
		commissionProfileStatusPage.selectCategory(categoryName);
		commissionProfileStatusPage.clickSubmitButton();

		String actual = CommissionProfileStatus.CommissionProfileStatus(profileName);

		// Added the default Function, as without a default Profile, The Status was not
		// getting saved.
		CommissionProfileStatus.CommissionProfileDefault(profileName);

		CommissionProfileStatus.ClickOnsave();
		SwitchWindow.switchwindow(driver);
		CommissionProfileStatus.clickOkAlertBtn();
		SwitchWindow.backwindow(driver);

		String Status = CommissionProfileStatus.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return Status;

	}

	public String CommissionProfileDefault(String domainName, String categoryName, String grade, String profileName)
			throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Verifying Commission profile Status

		homePage.clickProfileManagement();
		profileMgmntSubCats.clickCommissionProfileStatus();
		commissionProfileStatusPage.selectDomain(domainName);
		commissionProfileStatusPage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfileStatusPage.clickSubmitButton();

			commissionProfileStatusPage.selectGeoDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfileStatusPage.selectGrade(grade);
		}
		commissionProfileStatusPage.clickSubmitButton();

		String actual = CommissionProfileStatus.CommissionProfileStatus(profileName);

		// Added the default Function, as without a default Profile, The Status was not
		// getting saved.
		CommissionProfileStatus.CommissionProfileDefault(profileName);

		CommissionProfileStatus.ClickOnsave();

		if (_masterVO.getClientDetail("COMM_PROF_STATUS_INTERFACE").equals("0"))
			CommissionProfileStatus.ClickOnconfirm();
		else if (_masterVO.getClientDetail("COMM_PROF_STATUS_INTERFACE").equals("1"))
			CommissionProfileStatus.clickOkAlertBtn();

		String Status = CommissionProfileStatus.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return Status;

	}

	/**
	 * @author lokesh.kontey
	 * @param domainName
	 * @param categoryName
	 * @param grade
	 * @param commProfile
	 * @param product
	 * @param multiple
	 * @return message
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public long modifyCommissionProfileMultipleOf(String domainName, String categoryName, String grade,
			String commProfile, String product, String multiple) throws InterruptedException, ParseException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {

			modifyCommProfiledetailsPage.selectCommissionProductBased(product);
			// Window handler
			SwitchWindow.switchwindow(driver);
			checkCBCDate(currDate);
			modifyCommProfiledetailsPage.modifymultipleof(multiple);
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);
		} else {
			modifyCommProfiledetailsPage.selectCommissionProductBased(product);
			Thread.sleep(3000);
			// Window handler
			SwitchWindow.switchwindow(driver);

			modifyCommProfiledetailsPage.modifymultipleof(multiple);

			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 5);
			String newfromDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
			}

			addProfileDetailsPage.clickAddOtfButton();

			SwitchWindow.backwindow(driver);

			boolean flag = modifyCommProfiledetailsPage.visibleModifyCommOtf1();
			if (flag) {
				modifyCommProfiledetailsPage.ModifyCommOtf1();

				// Window handler
				SwitchWindow.switchwindow(driver);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);

			}
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();

		new AddChannelUserDetailsPage(driver).getActualMessage();
		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;
	}

	/**
	 * Modify minimum transfer value and from range of first slab
	 * 
	 * @param value
	 * @param domainName
	 * @param categoryName
	 * @param grade
	 * @param commProfile
	 * @param product
	 * @param multiple
	 * @return time
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public long modifyMinTrfCommissionSlabfromRange(String[] value, String domainName, String categoryName,
			String grade, String commProfile, String product) throws InterruptedException, ParseException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();
		String currDate = homePage.getDate();
		if (OTF_SLAB_ASSIGN == 0) {
			modifyCommProfiledetailsPage.selectCommissionProductBased(product);
			// Window handler
			SwitchWindow.switchwindow(driver);
			checkCBCDate(currDate);
			addProfileDetailsPage.enterMinTransferValue(value[0]);
			addProfileDetailsPage.enterFromRangeSlab0(value[1]);
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);
		} else {
			modifyCommProfiledetailsPage.selectCommissionProductBased(product);
			Thread.sleep(3000);
			// Window handler
			SwitchWindow.switchwindow(driver);
			addProfileDetailsPage.enterMinTransferValue(value[0]);
			addProfileDetailsPage.enterFromRangeSlab0(value[1]);

			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 5);
			String newfromDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
			}

			addProfileDetailsPage.clickAddOtfButton();

			SwitchWindow.backwindow(driver);

			boolean flag = modifyCommProfiledetailsPage.visibleModifyCommOtf1();
			if (flag) {
				modifyCommProfiledetailsPage.ModifyCommOtf1();

				// Window handler
				SwitchWindow.switchwindow(driver);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);

			}

		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();

		new AddChannelUserDetailsPage(driver).getActualMessage();
		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;
	}

	public void checkCBCDate(String currDate) {
		if (targetbasedCommission.equalsIgnoreCase("true")) {
			String wbStartDate = driver.findElement(By.xpath("//input[@name[contains(.,'[0].otfApplicableFromStr')]]"))
					.getAttribute("value");
			Log.info("CBC StartDate: " + wbStartDate + " | Current Date: " + currDate);
			if (!wbStartDate.matches(currDate)) {
				int slabCount = addProfileDetailsPage.totalSlabs();
				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
				}
			}
		}
	}

	/////////////////////////////////// SIT
	/////////////////////////////////// Methods///////////////////////////////////////////////

	public String[] CommissionProfile_SITValidations(Map<String, String> datamap, String domainName,
			String categoryName, String grade) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = datamap.get("ProfileName");
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(datamap.get("ShortCode"));
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();
		// Fill in Commission Slabs. (Separate slab for each Product will be
		// added)
		if (OTF_SLAB_ASSIGN == 0) {
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				addProfileDetailsPage.enterMultipleOf(datamap.get("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(datamap.get("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(datamap.get("MaxtransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				/*
				 * To be removed if not in use by Lokesh
				 */

				int slabCount1 = addProfileDetailsPage.totalSlabs();
				/*
				 * CONSTANT.COMM_SLAB_COUNT = slabCount1; datamap.put("SlabCount",
				 * String.valueOf(CONSTANT.COMM_SLAB_COUNT));
				 */

				for (int slabIndex = 0; slabIndex < slabCount1; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange1(slabIndex, datamap);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType1(datamap, slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
						addProfileDetailsPage.enterCBCTimeSlab(slabIndex, datamap.get("BaseTimeSlab"));
						for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
							if (detailIndex != 0) {
								addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex);
							}
							addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
						}
					} else {
						Log.info("Target based commission is not applicable.");
					}

					Log.info("commission varaible : " + slabIndex);
				}

				addProfileDetailsPage.clickAdd();
				datamap.put("SlabErrorMessage", CONSTANT.COMM_SLAB_ERR);
				SwitchWindow.backwindow(driver);
			}
		} else {
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
				{addProfileDetailsPage.selectTransactionType("ALL");
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
					addProfileDetailsPage.selectPaymentMode("ALL");}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange1(slabIndex, datamap);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType1(datamap, slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				}

				addProfileDetailsPage.clickAdd();
				datamap.put("SlabErrorMessage", CONSTANT.COMM_SLAB_ERR);
				SwitchWindow.backwindow(driver);

				boolean flag = modifyCommProfiledetailsPage.visibleModifyComm();
				if (flag) {
					addCommissionProfilePage.clickAssignOtfSlabs();

					SwitchWindow.switchwindow(driver);
					if (isProductCodeVisible == true) {
						addProfileDetailsPage.selectProductCode(i);
					}

					String toDate = homePage.addDaysToCurrentDate(currDate, 2);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
						addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
						for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
							addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
						}

					} else {
						Log.info("Target based commission is not applicable.");
					}

					addProfileDetailsPage.clickAddOtfButton();

					SwitchWindow.backwindow(driver);
				}
			}

		}
		String msg = datamap.get("SlabErrorMessage");

		if (msg != null) {
			Log.info("Error Message fetched as " + msg);

			result[2] = msg;

		} else {
			// Fill in AddAdditionalCommissionSlabs
			// To Check if Additional Commission slabs exists or not.
			boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
			if (addCommVisibity == true) {
				result[0] = "Y";
				addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();

				SwitchWindow.switchwindow(driver);
				int index1 = addAdditionalCommissionDetailsPage.getServicesIndex();

				addProfileDetailsPage.clickCloseButton();
				SwitchWindow.backwindow(driver);

				int i = 1, j = 1;
				while (i <= 1) {
					{
						addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
						SwitchWindow.switchwindow(driver);

						addAdditionalCommissionDetailsPage.selectService(i);

						// Check the existence of Sub Service field. (separate
						// slab for each sub service will be added)
						boolean subservice = addAdditionalCommissionDetailsPage.subServiceVisibility();
						if (subservice == true) {
							int index2 = addAdditionalCommissionDetailsPage.getSubServiceIndex();
							addAdditionalCommissionDetailsPage.selectSubServiceCode(j);
							if (j != 1)
								j++;
							else {
								i++;
								j = 1;
							} // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
								// for subservice
						}

						// Fill in all the details
						if (COMM_PROFILE_CLIENTVER > 0)
							try {
								addAdditionalCommissionDetailsPage.selectGatewayCode(datamap.get("GatewayCode"));
							} catch (Exception e) {
								Log.info("Issue in selecting GatewayCode");
								ExtentI.Markup(ExtentColor.RED, "Issue in selecting GatewayCode");

							}
						addAdditionalCommissionDetailsPage
								.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
						addAdditionalCommissionDetailsPage
								.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue1"));
						if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
							addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
							String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
							addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
						}

						int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
						CONSTANT.ADDCOMM_SLAB_COUNT = addCommSlabCount;
						datamap.put("AddCommSlabCount", String.valueOf(CONSTANT.ADDCOMM_SLAB_COUNT));
						boolean roamCommTypeVisibity = addAdditionalCommissionDetailsPage.roamCommTypeVisibility();

						for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
							boolean even = false;

							if (slabIndex % 2 == 0) {
								even = true;
							}
							String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
							addAdditionalCommissionDetailsPage.enterStartEndRangeSIT(slabIndex, datamap);
							addAdditionalCommissionDetailsPage.enterCommissionSelectType(slabIndex, even);
							if (roamCommTypeVisibity == true) {
								addAdditionalCommissionDetailsPage.enterRoamCommissionSelectType(slabIndex, even);
							}
							addAdditionalCommissionDetailsPage.enterDifferentialSelectType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterTax1SelectType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterTax2SelectType(slabIndex, even);
							if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
								addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
								addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
								addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
										_masterVO.getProperty("TimeSlab")); // LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
								for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
									if (detailIndex != 0) {
										addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex);
									}
									addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
											subSlabCount);
								}
							}
						}

						if (subservice == false) {
							i++;
							j = 1; // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
									// for subservice
						}

						// Switch to the main Commission Profile details page
						addAdditionalCommissionDetailsPage.clickAdd();
						SwitchWindow.backwindow(driver);

					}

				}
			}

			
			//Enter Other Commission Details
			addOtherCommissionDetails(domainName,categoryName);
			
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

			// Save and Confirm the Commission Profile.
			addCommissionProfilePage.clickSaveButton();
			commissionConfirmPage.clickConfirmButton();
			result[2] = commissionProfilePage.getActualMsg();

		}

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		return result;
	}

	public long modifyAdditionalCommissionProfile_SIT(Map<String, String> AddCommMap, String domainName,
			String categoryName, String grade, String commProfile) throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String version = DBHandler.AccessHandler.getCommProfileVersion(commProfile);
		Log.info("The version is:" + version);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);

		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.selectVersion(version);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String currDate = homePage.getDate();
		/*
		 * String MasterSheetPath = _masterVO.getProperty("DataProvider");
		 * ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET); int
		 * totalRow1 = ExcelUtility.getRowCount();
		 * 
		 * int i = 1; for (i = 1; i <= totalRow1; i++)
		 * 
		 * { if ((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i)
		 * .matches(_masterVO.getProperty("CustomerRechargeCode"))))
		 * 
		 * break; }
		 * 
		 * System.out.println(i); ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
		 * String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		 * 
		 * // boolean addCommVisibityWithCustomerRecharge = //
		 * modifyCommProfiledetailsPage.isCustomerRechargeAdditionalCommAdded(service);
		 * // if(addCommVisibityWithCustomerRecharge==true){
		 * 
		 * modifyCommProfiledetailsPage.ModifyAdditionalComSpecificService(service); //
		 * Window handler SwitchWindow.switchwindow(driver);
		 * 
		 * To be removed if not in use by Lokesh
		 * 
		 * int slabCount = addProfileDetailsPage.totalSlabs();
		 * AddCommMap.put("slabCountAdditional", String.valueOf(slabCount)); try {
		 * addAdditionalCommissionDetailsPage.enterTimeSlab(""); } catch (Exception e) {
		 * Log.info("Issue in entering TimeSlab"); ExtentI.Markup(ExtentColor.RED,
		 * "Issue in entering TimeSlab");
		 * 
		 * }
		 * 
		 * for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
		 * 
		 * boolean even = false;
		 * 
		 * if (slabIndex % 2 == 0) { even = true; } String fromDate =
		 * homePage.addDaysToCurrentDate(currDate, 0); String toDate =
		 * homePage.addDaysToCurrentDate(currDate, (slabIndex + 1));
		 * 
		 * addAdditionalCommissionDetailsPage.enterStartEndRangeSIT(slabIndex,
		 * AddCommMap);
		 * addAdditionalCommissionDetailsPage.enterCommissionSelectTypeSIT(AddCommMap,
		 * slabIndex, even);
		 * addAdditionalCommissionDetailsPage.enterTax1SelectTypeSIT(AddCommMap,
		 * slabIndex, even);
		 * 
		 * if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
		 * addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, fromDate,
		 * toDate); addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
		 * addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
		 * _masterVO.getProperty("TimeSlab")); //
		 * LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
		 * 
		 * for(int detailIndex = 0; detailIndex < subSlabCount; detailIndex++ ) {
		 * if(detailIndex != 0) {
		 * addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex); }
		 * addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
		 * subSlabCount);
		 * 
		 * }
		 * 
		 * Log.info("commission varaible : " + slabIndex);
		 * 
		 * }
		 * 
		 * }
		 * 
		 * addAdditionalCommissionDetailsPage.clickAdd();
		 * AddCommMap.put("SlabErrorMessage", CONSTANT.ADDCOMM_SLAB_ERR);
		 * SwitchWindow.backwindow(driver);
		 * 
		 */ 
		boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
		if (addCommVisibity == true) {
			int size = modifyCommProfiledetailsPage.ModifyAdditionalCommCount();

			int k = 0;
			while (k < size - 1) {

				modifyCommProfiledetailsPage.ModifyAdditionalComm(k);
				// Window handler
				SwitchWindow.switchwindow(driver);
				currDate = homePage.addDaysToCurrentDate(currDate, 0);
				if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
					addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
					String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
					addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
					addAdditionalCommissionDetailsPage.enterTimeSlab("00:00-23:59");					
				}

				int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
				
				for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
				 
					 boolean even = false;
					 
					if (slabIndex % 2 == 0) 
					{ even = true; 
					} 
					String fromDate = homePage.addDaysToCurrentDate(currDate, 0); 
					String toDate =	 homePage.addDaysToCurrentDate(currDate, (slabIndex + 1));
					 
					addAdditionalCommissionDetailsPage.enterStartEndRangeSIT(slabIndex,AddCommMap);
					addAdditionalCommissionDetailsPage.enterCommissionSelectTypeSIT(AddCommMap,slabIndex, even);
					addAdditionalCommissionDetailsPage.enterTax1SelectTypeSIT(AddCommMap,slabIndex, even);
					if (targetbasedaddtnlcommission.equalsIgnoreCase("true"))
					{
					addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, fromDate,toDate); 
					addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
					addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex, _masterVO.getProperty("TimeSlab"));
					// LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
		
					Log.info("commission varaible : " + slabIndex);
					 
					}
				
				 }		
						
		/*		for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
					}
				}
	
				addAdditionalCommissionDetailsPage.clickAddButton();*/
				
				 
				addAdditionalCommissionDetailsPage.clickAdd();
				AddCommMap.put("SlabErrorMessage", CONSTANT.ADDCOMM_SLAB_ERR);
							
				SwitchWindow.backwindow(driver);
				k++;

			}

		}

		if (OTF_SLAB_ASSIGN == 1) {
			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 5);
			String newfromDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
			}

			addProfileDetailsPage.clickAddOtfButton();

			SwitchWindow.backwindow(driver);
			boolean flag = modifyCommProfiledetailsPage.visibleModifyCommOtf1();
			if (flag) {
				modifyCommProfiledetailsPage.ModifyCommOtf1();

				// Window handler
				SwitchWindow.switchwindow(driver);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);

			}
		}		
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		// Save and Confirm the Commission Profile.

		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		String message = commissionProfilePage.getActualMsg();
		AddCommMap.put("ActualMessage", message);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;
	}

	public long modifyAdditionalCommissionProfile_TimeSlab(Map<String, String> AddCommMap, String domainName,
			String categoryName, String grade, String commProfile,String serviceCode) throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String version = DBHandler.AccessHandler.getCommProfileVersion(commProfile);
		Log.info("The version is:" + version);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);

		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.selectVersion(version);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String currDate = homePage.getDate();
		String t1 = homePage.getApplicableFromTime_min(60);
		String t2 = homePage.getApplicableFromTime_min(120);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i = 1;
		for (i = 1; i <= totalRow1; i++)

		{
			if ((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i)
					.matches(_masterVO.getProperty("CustomerRechargeCode"))))

				break;
		}

		System.out.println(i);

		ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);

		String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		modifyCommProfiledetailsPage.ModifyAdditionalComSpecificService(service,serviceCode);

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		AddCommMap.put("slabCountAdditional", String.valueOf(slabCount));

		String timeSlab = t1.concat("-").concat(t2);
		System.out.println(timeSlab);
		try {
			addAdditionalCommissionDetailsPage.enterTimeSlab(timeSlab);
		} catch (Exception e) {
			Log.info("Issue in entering TimeSlab");
			ExtentI.Markup(ExtentColor.RED, "Issue in entering TimeSlab");
		}

		for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {

			boolean even = false;

			if (slabIndex % 2 == 0) {
				even = true;
			}
			String fromDate = homePage.addDaysToCurrentDate(currDate, 1);
			String toDate = homePage.addDaysToCurrentDate(currDate, (slabIndex + 1));

			addAdditionalCommissionDetailsPage.enterStartEndRangeSIT(slabIndex, AddCommMap);
			addAdditionalCommissionDetailsPage.enterTax1SelectTypeSIT(AddCommMap, slabIndex, even);

			if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
				addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, fromDate, toDate);
				addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
				// addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
				// _masterVO.getProperty("TimeSlab"));
				// //LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
				addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex, timeSlab);
				Log.info("commission varaible : " + slabIndex);

			}

		}

		addAdditionalCommissionDetailsPage.clickAdd();
		AddCommMap.put("SlabErrorMessage", CONSTANT.COMM_SLAB_ERR);
		SwitchWindow.backwindow(driver);
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		// Save and Confirm the Commission Profile.

		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		String message = commissionProfilePage.getActualMsg();
		AddCommMap.put("ActualMessage", message);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;
	}

	public void modifyAdditionalCommissionProfile_CAC_CBCValidations(Map<String, String> AddCommMap, String domainName,
			String categoryName, String grade, String commProfile,String serviceCode) throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String version = DBHandler.AccessHandler.getCommProfileVersion(commProfile);
		Log.info("The version is:" + version);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);

		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.selectVersion(version);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}
		String currDate = homePage.getDate();
		String t1 = homePage.getApplicableFromTime_min(60);
		String t2 = homePage.getApplicableFromTime_min(120);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i = 1;
		for (i = 1; i <= totalRow1; i++)

		{
			if ((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i)
					.matches(_masterVO.getProperty("CustomerRechargeCode"))))

				break;
		}

		System.out.println(i);

		ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);

		String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		modifyCommProfiledetailsPage.ModifyAdditionalComSpecificService(service,serviceCode);

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		String timeSlab = t1.concat("-").concat(t2);

		try {
			addAdditionalCommissionDetailsPage.enterTimeSlab(timeSlab);
		} catch (Exception e) {
			Log.info("Issue in entering TimeSlab");
			ExtentI.Markup(ExtentColor.RED, "Issue in entering TimeSlab");

		}

		for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {

			boolean even = false;

			if (slabIndex % 2 == 0) {
				even = true;
			}
			String fromDate = homePage.addDaysToCurrentDate(currDate, 1);
			String toDate = homePage.addDaysToCurrentDate(currDate, (slabIndex + 1));

			if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
				addAdditionalCommissionDetailsPage.enterApplicableFromDate(fromDate);
				String ApplicableToDate = homePage.addDaysToCurrentDate(fromDate, 90);
				addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
			}

			addAdditionalCommissionDetailsPage.enterStartEndRangeSIT(slabIndex, AddCommMap);
			addAdditionalCommissionDetailsPage.enterTax1SelectTypeSIT(AddCommMap, slabIndex, even);

			if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
				addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, fromDate, toDate);
				addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
				addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex, AddCommMap.get("TimeSlab")); // LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
				// addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex, timeSlab);
				Log.info("commission varaible : " + slabIndex);

			}

		}

		addAdditionalCommissionDetailsPage.clickAdd();
		AddCommMap.put("SlabErrorMessage", CONSTANT.ADDCOMM_SLAB_ERR);
		SwitchWindow.backwindow(driver);
		/*
		 * if(!CONSTANT.ADDCOMM_SLAB_ERR.equals(null)){
		 * addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		 * addCommissionProfilePage.modifyApplicableFromHour(homePage.
		 * getApplicableFromTime_1min()); try{ // Save and Confirm the Commission
		 * Profile. addCommissionProfilePage.clickSaveButton();
		 * 
		 * commissionConfirmPage.clickConfirmButton(); String message =
		 * commissionProfilePage.getActualMsg(); AddCommMap.put("ActualMessage",
		 * message); } catch(Exception e){
		 * Log.info("Error while selecting Confirm button"); }
		 * 
		 * CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE()); }
		 */

	}

	public long modifyCommissionProfile_SIT(Map<String, String> AddCommMap, String domainName, String categoryName,
			String grade, String commProfile) throws InterruptedException, ParseException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			try {
				commissionProfilePage.selectGeographicalDomain(AddCommMap.get("GeographicalDomain"));
			} catch (Exception e) {
				Log.info("Issue in selecting Geography");
			}
			commissionProfilePage.selectGrade(grade);
		}
		try {
			commissionProfilePage.clickModifyButton();
		} catch (Exception e) {
			String ErrorMessage = commissionProfilePage.getActualMsg();
			AddCommMap.put("ErrorMessage", ErrorMessage);
		}

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}
		String currDate = homePage.getDate();
		modifyCommProfiledetailsPage.ModifyComm();

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();

		for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
			String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
			addProfileDetailsPage.enterStartEndRange1(slabIndex, AddCommMap);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
			}
			Log.info("commission varaible : " + slabIndex);
		}

		addProfileDetailsPage.clickAddButton();
		SwitchWindow.backwindow(driver);		
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();

		String message = commissionProfilePage.getActualMsg();

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;
	}

	/*
	 * Method added to add Commission Profile for Particular Service and Gateway
	 */
	public String[] CommissionProfile_SpecificService(Map<String, String> datamap, String domainName,
			String categoryName, String grade) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = datamap.get("ProfileName");
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(datamap.get("ShortCode"));
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {

			String ApplicableFromDate = homePage.getDate();
			String ApplicableFromHour = homePage.getApplicableFromTime();

			addCommissionProfilePage.enterApplicableFromDate(ApplicableFromDate);
			addCommissionProfilePage.enterApplicableFromHour(ApplicableFromHour);
		}
		String currDate = homePage.getDate();
		// Fill in Commission Slabs. (Separate slab for each Product will be
		// added)
		if (OTF_SLAB_ASSIGN == 0) {
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				addProfileDetailsPage.enterMultipleOf(datamap.get("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(datamap.get("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(datamap.get("MaxtransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				int slabCount1 = addProfileDetailsPage.totalSlabs();

				for (int slabIndex = 0; slabIndex < slabCount1; slabIndex++) {
					boolean even = false;
					if (slabIndex % 2 == 0) {
						even = true;
					}
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange1(slabIndex, datamap);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType1(datamap, slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
						addProfileDetailsPage.enterCBCTimeSlab(slabIndex, datamap.get("BaseTimeSlab"));
						for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
							if (detailIndex != 0) {
								addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex);
							}
							addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
						}
					} else {
						Log.info("Target based commission is not applicable.");
					}
					Log.info("commission varaible : " + slabIndex);
				}
				addProfileDetailsPage.clickAdd();
				datamap.put("SlabErrorMessage", CONSTANT.COMM_SLAB_ERR);
				SwitchWindow.backwindow(driver);
			}
		} else {
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				// Fill in all the details
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
				{addProfileDetailsPage.selectTransactionType("ALL");
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
					addProfileDetailsPage.selectPaymentMode("ALL");}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;

				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;

					if (slabIndex % 2 == 0) {
						even = true;
					}
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange1(slabIndex, datamap);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
					addProfileDetailsPage.enterTax1SelectType1(datamap, slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				}

				addProfileDetailsPage.clickAdd();
				datamap.put("SlabErrorMessage", CONSTANT.COMM_SLAB_ERR);
				SwitchWindow.backwindow(driver);

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}

				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
					}

				} else {
					Log.info("Target based commission is not applicable.");
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
			}
		}

		String msg = datamap.get("SlabErrorMessage");
		if (msg != null) {
			Log.info("Error Message fetched as " + msg);
			result[2] = msg;
		} else {
			// Fill in AddAdditionalCommissionSlabs
			// To Check if Additional Commission slabs exists or not.
			boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
			if (addCommVisibity == true) {
				result[0] = "Y";

				addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();

				SwitchWindow.switchwindow(driver);
				int index1 = addAdditionalCommissionDetailsPage.getServicesIndex();

				addProfileDetailsPage.clickCloseButton();
				SwitchWindow.backwindow(driver);

				int i = 1, j = 1;
				while (i <= 1) {
					{
						addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
						SwitchWindow.switchwindow(driver);

						addAdditionalCommissionDetailsPage.selectService(i);

						// Check the existence of Sub Service field. (separate
						// slab for each sub service will be added)
						boolean subservice = addAdditionalCommissionDetailsPage.subServiceVisibility();
						if (subservice == true) {
							int index2 = addAdditionalCommissionDetailsPage.getSubServiceIndex();
							addAdditionalCommissionDetailsPage.selectSubServiceCode(j);
							if (j != 1)
								j++;
							else {
								i++;
								j = 1;
							} // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
								// for subservice
						}

						if (COMM_PROFILE_CLIENTVER > 0)
							try {
								addAdditionalCommissionDetailsPage.selectGatewayCode(datamap.get("GatewayCode"));
							} catch (Exception e) {
								Log.info("Issue in selecting GatewayCode");
								ExtentI.Markup(ExtentColor.RED, "Issue in selecting GatewayCode");

							}

						addAdditionalCommissionDetailsPage
								.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
						addAdditionalCommissionDetailsPage
								.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue1"));

						try {
							addAdditionalCommissionDetailsPage.enterTimeSlab(datamap.get("TimeSlab"));
						} catch (Exception e) {
							Log.info("Issue in entering TimeSlab");
							ExtentI.Markup(ExtentColor.RED, "Issue in entering TimeSlab");

						}

						if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
							addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
							String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
							addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
						}
						int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
						// int addCommSlabCount = 2;
						boolean roamCommTypeVisibity = addAdditionalCommissionDetailsPage.roamCommTypeVisibility();
						for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
							boolean even = false;
							if (slabIndex % 2 == 0) {
								even = true;
							}
							String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
							addAdditionalCommissionDetailsPage.enterStartEndRangeSIT(slabIndex, datamap);
							addAdditionalCommissionDetailsPage.enterCommissionSelectType(slabIndex, even);
							if (roamCommTypeVisibity == true) {
								addAdditionalCommissionDetailsPage.enterRoamCommissionSelectType(slabIndex, even);
							}
							addAdditionalCommissionDetailsPage.enterDifferentialSelectType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterTax1SelectType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterTax2SelectType(slabIndex, even);
							if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
								addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
								addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
								addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
										_masterVO.getProperty("TimeSlab")); // LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
								for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
									if (detailIndex != 0) {
										addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex);
									}
									addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
											subSlabCount);
								}
							}

						}
						if (subservice == false) {
							i++;
							j = 1; // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
									// for subservice
						}

						// Switch to the main Commission Profile details page
						addAdditionalCommissionDetailsPage.clickAdd();
						datamap.put("AddCommSlabErrorMessage", CONSTANT.ADDCOMM_SLAB_ERR);
						SwitchWindow.backwindow(driver);

					}
				}
			}
			
			//Enter Other Commission Details
			addOtherCommissionDetails(domainName,categoryName);

			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

			// Save and Confirm the Commission Profile.
			addCommissionProfilePage.clickSaveButton();
			commissionConfirmPage.clickConfirmButton();
			result[2] = commissionProfilePage.getActualMsg();

		}

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		return result;
	}

	public String getCommissionSlabCount(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "getCommissionSlabCount";
		Log.methodEntry(methodname, domainName, categoryName, grade);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			System.out.println("The curr date is : " + currDate);

			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}

			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				/*
				 * if(targetbasedCommission.equalsIgnoreCase("true")){
				 * addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
				 * addProfileDetailsPage.enterCBCTimeSlab(slabIndex,
				 * _masterVO.getProperty("BaseTimeSlab")); for(int detailIndex = 0; detailIndex
				 * < subSlabCount; detailIndex++ ) { if(detailIndex != 0) {
				 * addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex); }
				 * addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
				 * } }else { Log.info("Target based commission is not applicable."); }
				 */

				Log.info("commission varaible : " + slabIndex);
			}
			addProfileDetailsPage.clickAdd();

			// Adding Commission Profile slab as it is required to access Additional
			// Commission Slab
			SwitchWindow.backwindow(driver);

		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);
		}
		addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
		// Initializing Window Handlers
		SwitchWindow.switchwindow(driver);
		int AddslabCount = addAdditionalCommissionDetailsPage.totalSlabs();
		CONSTANT.ADDCOMM_SLAB_COUNT = AddslabCount;
		addAdditionalCommissionDetailsPage.clickAdd();
		SwitchWindow.backwindow(driver);
		addCommissionProfilePage.clickBackButton();
		Log.methodExit(methodname);
		String actual = null;
		if (CONSTANT.COMM_SLAB_COUNT != 0) {
			actual = ("Slab Count captured");
			Log.info("The Slab count captured as:" + CONSTANT.COMM_SLAB_COUNT);
		} else {
			actual = "Issue in capturing Slab Count:" + CONSTANT.COMM_SLAB_COUNT;
		}

		String actual1 = null;
		if (CONSTANT.ADDCOMM_SLAB_COUNT != 0) {
			actual1 = ("Slab Count captured");
			Log.info("The Slab count captured as:" + CONSTANT.ADDCOMM_SLAB_COUNT);
		} else {
			actual1 = "Issue in capturing Slab Count:" + CONSTANT.ADDCOMM_SLAB_COUNT;
		}
		return actual1;
	}

	public String modifyCommissionProfile_SITNeg(String domainName, String categoryName, String grade)
			throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		// String version= DBHandler.AccessHandler.getCommProfileVersion(commProfile);
		// Log.info("The version is:" +version);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);

		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.clickModifyButton();

		String ErrorMessage = modifyCommissionProfilePage.getErrorMessage();

		return ErrorMessage;
	}

	// Suspend_Resume Additional Commission Profile for any specific service passed
	// as an argument in the method, in order to make it reusable
	// @author : Shallu

	public long suspendAdditionalCommProfileForGivenService(String domainName, String categoryName, String grade,
			String profileName, String service,String serviceCode) throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (_masterVO.getClientDetail("MULTIPAGECOMMISSIONSELECTION").equalsIgnoreCase("true")) {
			commissionProfilePage.clickAddButton();
		}
		commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
		commissionProfilePage.selectGrade(grade);

		commissionProfilePage.clickModifyButton();
		modifyCommissionProfilePage.selectCommissionProfileSet(profileName);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String SlabStatus = modifyCommProfiledetailsPage.getAdditionalSlabStatusofparticularService(service,serviceCode);
		String ExpectedSlabStatus = _masterVO.getProperty("AdditionalCommSlabStatus");
		Log.info("The Expected Status of " + service + " Additional Profile is :" + ExpectedSlabStatus);
		if (!SlabStatus.equalsIgnoreCase(ExpectedSlabStatus)) {
			modifyCommProfiledetailsPage.ModifyAdditionalComSpecificService(service,serviceCode);
			SwitchWindow.switchwindow(driver);
			modifyAdditionalCommProfileDetailsPage.clickSuspendAdditionalComm();

			SwitchWindow.backwindow(driver);

			String SlabStatus1 = modifyCommProfiledetailsPage.getAdditionalSlabStatusofparticularService(service,serviceCode);
			Log.info("The Slab Status after Modification is:" + SlabStatus1);
			Assert.assertEquals(SlabStatus1, ExpectedSlabStatus);
			if (SlabStatus1 == ExpectedSlabStatus) {
				ExtentI.Markup(ExtentColor.TEAL,
						"Additional Commission Profile slab with service" + service + "is suspended successfully");
			}

		} else {
			Log.info("The Slab is already suspended");
		}

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		modifyCommProfiledetailsPage.clickSave();
		modifyCommProfileConfirmPage.confirmButton();

		String statusText = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + statusText);

		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("ACTUAL_MESSAGE", statusText);
		dataMap.put("STATUS", SlabStatus);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;

	}

	public long resumeAdditionalCommProfileForGivenService(String domainName, String categoryName, String grade,
			String profileName, String service,String serviceCode) throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (_masterVO.getClientDetail("MULTIPAGECOMMISSIONSELECTION").equalsIgnoreCase("true")) {
			commissionProfilePage.clickAddButton();
		}
		commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
		commissionProfilePage.selectGrade(grade);

		commissionProfilePage.clickModifyButton();
		modifyCommissionProfilePage.selectCommissionProfileSet(profileName);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}

		String SlabStatus = modifyCommProfiledetailsPage.getAdditionalSlabStatusofparticularService(service,serviceCode);
		String SuspendedSlabStatus = _masterVO.getProperty("AdditionalCommSlabStatus");
		Log.info("The Expected Slab Status before resuming is: " + SuspendedSlabStatus);
		if (SlabStatus.equalsIgnoreCase(SuspendedSlabStatus)) {
			modifyCommProfiledetailsPage.ModifyAdditionalComSpecificService(service,serviceCode);
			SwitchWindow.switchwindow(driver);
			modifyAdditionalCommProfileDetailsPage.clickResumeAdditionalComm();

			SwitchWindow.backwindow(driver);

			String SlabStatus1 = modifyCommProfiledetailsPage.getAdditionalSlabStatusofparticularService(service,serviceCode);
			Log.info("The slab status after modification is : " + SlabStatus1);
			String ExpectedSlabStatus2 = _masterVO.getProperty("AdditionalCommResumeStatus");
			Assert.assertEquals(SlabStatus1, ExpectedSlabStatus2);
			if (SlabStatus1.equalsIgnoreCase(ExpectedSlabStatus2)) {
				ExtentI.Markup(ExtentColor.TEAL,
						"Additional Commission Profile slab with service " + service + "  is resumed successfully");
			}

		} else {
			Log.info("The Slab is already active");
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		modifyCommProfiledetailsPage.clickSave();
		modifyCommProfileConfirmPage.confirmButton();

		String statusText = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + statusText);

		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("ACTUAL_MESSAGE", statusText);
		dataMap.put("STATUS", SlabStatus);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;

	}

	public long modifyAdditionalCommissionProfile_SITService(Map<String, String> AddCommMap, String domainName,
			String categoryName, String grade, String commProfile, String service,String serviceCode)
			throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String version = DBHandler.AccessHandler.getCommProfileVersion(commProfile);
		Log.info("The version is:" + version);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);

		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.selectVersion(version);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}
		String currDate = homePage.getDate();

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);

		modifyCommProfiledetailsPage.ModifyAdditionalComSpecificService(service,serviceCode);

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		AddCommMap.put("slabCountAdditional", String.valueOf(slabCount));

		try {
			addAdditionalCommissionDetailsPage.enterTimeSlab("");
		} catch (Exception e) {
			Log.info("Issue in entering TimeSlab");
			ExtentI.Markup(ExtentColor.RED, "Issue in entering TimeSlab");

		}

		for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {

			boolean even = false;

			if (slabIndex % 2 == 0) {
				even = true;
			}
			String fromDate = homePage.addDaysToCurrentDate(currDate, 1);
			String toDate = homePage.addDaysToCurrentDate(currDate, (slabIndex + 1));

			addAdditionalCommissionDetailsPage.enterStartEndRangeSIT(slabIndex, AddCommMap);
			addAdditionalCommissionDetailsPage.enterCommissionSelectTypeSIT(AddCommMap, slabIndex, even);
			addAdditionalCommissionDetailsPage.enterTax1SelectTypeSIT(AddCommMap, slabIndex, even);

			if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
				addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, fromDate, toDate);
				addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
				addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex, _masterVO.getProperty("TimeSlab")); // LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
				/*
				 * for(int detailIndex = 0; detailIndex < subSlabCount; detailIndex++ ) {
				 * if(detailIndex != 0) {
				 * addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex); }
				 * addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
				 * subSlabCount);
				 * 
				 * }
				 */
				Log.info("commission varaible : " + slabIndex);

			}

		}

		addAdditionalCommissionDetailsPage.clickAdd();
		AddCommMap.put("SlabErrorMessage", CONSTANT.ADDCOMM_SLAB_ERR);
		SwitchWindow.backwindow(driver);
		if (OTF_SLAB_ASSIGN == 1) {
			String currDate1 = homePage.getDate();
			boolean flag = modifyCommProfiledetailsPage.visibleModifyCommOtf();
			if (flag) {
				modifyCommProfiledetailsPage.ModifyCommOtf();

				// Window handler
				SwitchWindow.switchwindow(driver);

				String toDate = homePage.addDaysToCurrentDate(currDate1, 5);
				String newfromDate = homePage.addDaysToCurrentDate(currDate1, 3);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
				boolean flag2 = modifyCommProfiledetailsPage.visibleModifyCommOtf1();
				if (flag2) {
					modifyCommProfiledetailsPage.ModifyCommOtf1();

					// Window handler
					SwitchWindow.switchwindow(driver);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDateOTF(newfromDate, toDate);
					}

					addProfileDetailsPage.clickAddOtfButton();

					SwitchWindow.backwindow(driver);

				}
			}
		}

		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		// Save and Confirm the Commission Profile.

		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		String message = commissionProfilePage.getActualMsg();
		AddCommMap.put("ActualMessage", message);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;
	}

	public long modifyAdditionalCommissionProfile_TimeSlab_ParticularService(Map<String, String> AddCommMap,
			String domainName, String categoryName, String grade, String commProfile, String service,String serviceCode)
			throws InterruptedException, ParseException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String version = DBHandler.AccessHandler.getCommProfileVersion(commProfile);
		Log.info("The version is:" + version);

		// Enter Domain, category, Geographical Domain and Grade.
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);

		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.selectVersion(version);
		modifyCommissionProfilePage.clickModifyButton();
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			boolean CommTypeVisibility = addCommissionProfilePage.CommissionTypeVisibility();
			if (CommTypeVisibility == true) {
				Log.info("Dual Commission Type Select Drop down exists");
			} else {
				Log.info("Dual Commission Type Select Drop down does not exist");
			}

		}
		String currDate = homePage.getDate();
		String t1 = homePage.getApplicableFromTime_min(60);
		String t2 = homePage.getApplicableFromTime_min(120);

		modifyCommProfiledetailsPage.ModifyAdditionalComSpecificService(service,serviceCode);

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		AddCommMap.put("slabCountAdditional", String.valueOf(slabCount));

		String timeSlab = t1.concat("-").concat(t2);
		System.out.println(timeSlab);

		try {
			addAdditionalCommissionDetailsPage.enterTimeSlab(timeSlab);
		} catch (Exception e) {
			Log.info("Issue in entering TimeSlab");
			ExtentI.Markup(ExtentColor.RED, "Issue in entering TimeSlab");

		}

		for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {

			boolean even = false;

			if (slabIndex % 2 == 0) {
				even = true;
			}
			String fromDate = homePage.addDaysToCurrentDate(currDate, 1);
			String toDate = homePage.addDaysToCurrentDate(currDate, (slabIndex + 1));

			addAdditionalCommissionDetailsPage.enterStartEndRangeSIT(slabIndex, AddCommMap);
			addAdditionalCommissionDetailsPage.enterTax1SelectTypeSIT(AddCommMap, slabIndex, even);

			if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
				addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, fromDate, toDate);
				addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
				// addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
				// _masterVO.getProperty("TimeSlab"));
				// //LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
				addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex, timeSlab);
				Log.info("commission varaible : " + slabIndex);

			}

		}

		addAdditionalCommissionDetailsPage.clickAdd();
		AddCommMap.put("SlabErrorMessage", CONSTANT.COMM_SLAB_ERR);
		SwitchWindow.backwindow(driver);
				
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time + ":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		// Save and Confirm the Commission Profile.

		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		String message = commissionProfilePage.getActualMsg();
		AddCommMap.put("ActualMessage", message);

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		return requiredtime;
	}

	public String[] addCommissionProfile_cbcValidationSIT(HashMap<String, String> mapParam)
			throws InterruptedException {
		final String methodname = "addCommissionProfile";
		Log.methodEntry(methodname, mapParam.get("domainName"), mapParam.get("categoryName"), mapParam.get("grade"));

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(mapParam.get("domainName"));
		commissionProfilePage.selectCategory(mapParam.get("categoryName"));
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(mapParam.get("grade"));
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();
		// Fill in Commission Slabs. (Separate slab for each Product will be
		// added)
		addCommissionProfilePage.clickAssignCommissionSlabs();

		// Initializing Window Handlers
		SwitchWindow.switchwindow(driver);

		// Getting List size of Product.
		int index = 1;

		boolean isProductCodeVisible;
		try {
			index = addProfileDetailsPage.getProductCodeIndex();
			isProductCodeVisible = true;
		} catch (NoSuchElementException e) {
			isProductCodeVisible = false;
		}
		addProfileDetailsPage.clickCloseButton();
		SwitchWindow.backwindow(driver);

		for (int i = 1; i <= index; i++) {
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(i);
			}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlab(slabIndex, _masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						if (detailIndex != 0) {
							addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex);
						}
						addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
					}
				} else {
					Log.info("Target based commission is not applicable.");
				}

				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);
		}

		// Fill in AddAdditionalCommissionSlabs
		// To Check if Additional Commission slabs exists or not.
		boolean addCommVisibity = addCommissionProfilePage.addAddititionalCommissionVisibility();
		if (addCommVisibity == true) {
			result[0] = "Y";
			addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();

			SwitchWindow.switchwindow(driver);
			int index1 = addAdditionalCommissionDetailsPage.getServicesIndex();

			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			int i = 1, j = 1;
			while (i <= index1) {
				{
					addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
					SwitchWindow.switchwindow(driver);

					addAdditionalCommissionDetailsPage.selectService(i);

					// Check the existence of Sub Service field. (separate
					// slab for each sub service will be added)
					boolean subservice = addAdditionalCommissionDetailsPage.subServiceVisibility();
					if (subservice == true) {
						int index2 = addAdditionalCommissionDetailsPage.getSubServiceIndex();
						addAdditionalCommissionDetailsPage.selectSubServiceCode(j);
						if (j != index2)
							j++;
						else {
							i++;
							j = 1;
						} // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
							// for subservice
					}

					// Fill in all the details
					if (COMM_PROFILE_CLIENTVER > 0)
						addAdditionalCommissionDetailsPage.selectGatewayCodeAll(_masterVO.getProperty("GatewayCode"));
					addAdditionalCommissionDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
					addAdditionalCommissionDetailsPage
							.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue1"));
					if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
						addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate);
						String ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
						addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate);
					}

					int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
					boolean roamCommTypeVisibity = addAdditionalCommissionDetailsPage.roamCommTypeVisibility();

					for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) {
						boolean even = false;

						if (slabIndex % 2 == 0) {
							even = true;
						}
						String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
						addAdditionalCommissionDetailsPage.enterStartEndRange(slabIndex);
						addAdditionalCommissionDetailsPage.enterCommissionSelectType(slabIndex, even);
						if (roamCommTypeVisibity == true) {
							addAdditionalCommissionDetailsPage.enterRoamCommissionSelectType(slabIndex, even);
						}
						addAdditionalCommissionDetailsPage.enterDifferentialSelectType(slabIndex, even);
						addAdditionalCommissionDetailsPage.enterTax1SelectType(slabIndex, even);
						addAdditionalCommissionDetailsPage.enterTax2SelectType(slabIndex, even);
						if (targetbasedaddtnlcommission.equalsIgnoreCase("true")) {
							addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
							addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
							addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
									_masterVO.getProperty("TimeSlab")); // LoadPropertiesFile.PropertiesMap.get("TimeSlab"));
							for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
								if (detailIndex != 0) {
									addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex);
								}
								addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
										subSlabCount);
							}
						}
					}

					if (subservice == false) {
						i++;
						j = 1; // added by Lokesh 22/11/2017 -> Go to next service and reinitialise counter(j)
								// for subservice
					}

					// Switch to the main Commission Profile details page
					addAdditionalCommissionDetailsPage.clickAddButton();
					SwitchWindow.backwindow(driver);

				}
			}
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(mapParam.get("domainName"), mapParam.get("categoryName"));

		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		result[2] = commissionProfilePage.getActualMsg();

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());

		Log.methodExit(methodname);
		return result;
	}

	public static String domain, category;

	public Object[] suspendcommissionProfileStatus(String domainName, String categoryName, String grade,
			String profileName) throws InterruptedException {
		domain = domainName;
		category = categoryName;
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();

		// Verifying Commission profile Status
		homePage.clickProfileManagement();
		profileMgmntSubCats.clickCommissionProfileStatus();
		commissionProfileStatusPage.selectDomain(domainName);
		commissionProfileStatusPage.selectCategory(categoryName);
		commissionProfileStatusPage.clickSubmitButton();
		Object[] defualtComm = CommissionProfileStatus.suspendCommProfilecheckbox(profileName);
		CommissionProfileStatus.ClickOnsave();
		CommissionProfileStatus.ClickOnconfirm();

		return defualtComm;

	}

	public void resumecommissionProfileStatus(String domainName, String categoryName, String grade, String profileName,
			boolean isdefault) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();

		// Verifying Commission profile Status

		homePage.clickProfileManagement();
		profileMgmntSubCats.clickCommissionProfileStatus();
		commissionProfileStatusPage.selectDomain(domainName);
		commissionProfileStatusPage.selectCategory(categoryName);
		commissionProfileStatusPage.clickSubmitButton();
		CommissionProfileStatus.resumeCommProfilecheckbox(profileName, isdefault);
		CommissionProfileStatus.ClickOnsave();
		CommissionProfileStatus.ClickOnconfirm();
	}

	public WebDriver clickonprofilemgmtandsubmit() {
		Log.info("Visiting profile management to suspend commission profile, as profile was set to default.");

		homePage.clickProfileManagement();
		profileMgmntSubCats.clickCommissionProfileStatus();
		commissionProfileStatusPage.selectDomain(domain);
		commissionProfileStatusPage.selectCategory(category);
		commissionProfileStatusPage.clickSubmitButton();
		return driver;
	}

	public String CommissionProfileDeactivatedNegative(String domainName, String categoryName, String grade,
			String profileName, String lang1Msg, String lang2Msg) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		/*
		 * Fetching Domain and category data from Excel. Generating Profile name using
		 * Random No. Generator
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		// Verifying Commission profile Status

		homePage.clickProfileManagement();
		profileMgmntSubCats.clickCommissionProfileStatus();
		commissionProfileStatusPage.selectDomain(domainName);
		commissionProfileStatusPage.selectCategory(categoryName);
		commissionProfileStatusPage.selectGeoDomain(_masterVO.getProperty("GeographicalDomain"));
		commissionProfileStatusPage.selectGrade(grade);
		commissionProfileStatusPage.clickSubmitButton();

		String actual = CommissionProfileStatus.CommissionProfileDeactivateNegative(profileName, lang1Msg, lang2Msg);

		Log.info(actual);
		CommissionProfileStatus.ClickOnsave();

		String Status = CommissionProfileStatus.getErrorMessage();
		return Status;

	}

	public String addCBCvalidationSlab(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCBCvalidationSlab";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message;
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignOtfSlabs();

			SwitchWindow.switchwindow(driver);
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
				for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
					addProfileDetailsPage.enterCBCDetailsOTFNegative(detailIndex, subSlabCount);
				}

			} else {
				Log.info("Target based commission is not applicable.");
			}

			message=addProfileDetailsPage.clickAddOtfButtonwhenPopup();
			//message = addProfileDetailsPage.getErrorMessage();

			SwitchWindow.backwindow(driver);

		}

		Log.methodExit(methodname);
		return message;
	}

	public String addCBCvalidationFromDate(String domainName, String categoryName, String grade, String caseID)
			throws InterruptedException {
		final String methodname = "addCBCvalidationFromDate";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignOtfSlabs();

			SwitchWindow.switchwindow(driver);
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {

				switch (caseID) {
				case "SITCBCCOMMPROFILE12":
					addProfileDetailsPage.enterStartEndDateOTF("", toDate);
					break;
				case "SITCBCCOMMPROFILE19":
					addProfileDetailsPage.enterStartEndDateOTF(homePage.addDaysToCurrentDate(currDate, -2), toDate);
					break;
				default:
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				}

				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
				for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
					addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
				}

			} else {
				Log.info("Target based commission is not applicable.");
			}

			message=	addProfileDetailsPage.clickAddOtfButtonwhenPopup();
			if (caseID.equals("SITCBCCOMMPROFILE12")) {
				//message = addProfileDetailsPage.getErrorMessage();
				SwitchWindow.backwindow(driver);
			} else if (caseID.equals("SITCBCCOMMPROFILE19")) {
				SwitchWindow.backwindow(driver);
				addCommissionProfilePage.clickSaveButton();
				message = addProfileDetailsPage.getErrorMessage();
			}
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
				
		Log.methodExit(methodname);
		return message;
	}

	public String addCBCvalidationToDate(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCBCvalidationToDate";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignOtfSlabs();

			SwitchWindow.switchwindow(driver);
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(currDate, "");
				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
				for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
					addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
				}

			} else {
				Log.info("Target based commission is not applicable.");
			}

			message=	addProfileDetailsPage.clickAddOtfButtonwhenPopup();
			//message = addProfileDetailsPage.getErrorMessage();

			SwitchWindow.backwindow(driver);

		}

		Log.methodExit(methodname);
		return message;
	}

	public String addCBCvalidationSlabBlank(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCBCvalidationSlabBlank";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignOtfSlabs();

			SwitchWindow.switchwindow(driver);
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
			} else {
				Log.info("Target based commission is not applicable.");
			}

			message=addProfileDetailsPage.clickAddOtfButtonwhenPopup();
		//	message = addProfileDetailsPage.getErrorMessage();

			SwitchWindow.backwindow(driver);

		}

		Log.methodExit(methodname);
		return message;
	}

	public String addCBCvalidationInvalidValue(String domainName, String categoryName, String grade, String CaseID)
			throws InterruptedException {
		final String methodname = "addCBCvalidationInvalidValue";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(1);
				}
				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
				for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
					addProfileDetailsPage.enterCBCDetailsOTFValue(detailIndex, subSlabCount, CaseID);
				}
				message=addProfileDetailsPage.clickAddOtfButtonwhenPopup();
			//	message = addProfileDetailsPage.getErrorMessage();

				SwitchWindow.backwindow(driver);

			} else {
				Log.info("Target based commission is not applicable.");
			}

		}

		Log.methodExit(methodname);
		return message;
	}

	public String addCBCvalidationInvalidRate(String domainName, String categoryName, String grade, String caseID)
			throws InterruptedException {
		final String methodname = "addCBCvalidationInvalidValue";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);

				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(1);
				}

				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
				for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
					addProfileDetailsPage.enterCBCDetailsOTFRate(detailIndex, subSlabCount, caseID);
				}
				message=addProfileDetailsPage.clickAddOtfButtonwhenPopup();
				//message = addProfileDetailsPage.getErrorMessage();

				SwitchWindow.backwindow(driver);

			} else {
				Log.info("Target based commission is not applicable.");
			}

		}

		Log.methodExit(methodname);
		return message;
	}

	public String addCBCvalidationTime(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addCBCvalidationTime";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(1);
				}

				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				addProfileDetailsPage.enterCBCTimeSlabOTF(homePage.getApplicableFromTime_min(-20));

				for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
					addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
				}

				addProfileDetailsPage.clickAddOtfButton();
				message = addProfileDetailsPage.getErrorMessage();

				SwitchWindow.backwindow(driver);
			} else {
				Log.info("Target based commission is not applicable.");
			}

		}

		Log.methodExit(methodname);
		return message;
	}

	public String addCBC(String domainName, String categoryName, String grade, String caseID)
			throws InterruptedException {
		final String methodname = "addCBCvalidationFromDate";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(1);
				}
				switch (caseID) {
				case "SITCBCCOMMPROFILE21":
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					break;
				case "SITCBCCOMMPROFILE22":
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					break;

				}

				for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
					addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
				}

				message=addProfileDetailsPage.clickAddOtfButtonwhenPopup();

				SwitchWindow.backwindow(driver);

			} else {
				Log.info("Target based commission is not applicable.");
			}

		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		message = commissionProfilePage.getActualMsg();
		Log.methodExit(methodname);
		return message;

	}

	public String addcommissonwithoutCBC(String domainName, String categoryName, String grade)
			throws InterruptedException {
		final String methodname = "addcommissonwithoutCBC";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}
			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);
		}
		
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		message = commissionProfilePage.getActualMsg();
		Log.methodExit(methodname);
		return message;

	}

	public String addcommissonwithoutCBC(String domainName, String categoryName, String grade, String comtype)
			throws InterruptedException {
		final String methodname = "addcommissonwithoutCBC";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(comtype);
		}
		if (comtype.equals("PC")) {
			driver.switchTo().alert().accept();
		}
		
		

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}
			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			//Enter Other Commission Details
			addOtherCommissionDetails(domainName,categoryName);
			
			// Save and Confirm the Commission Profile.
			addCommissionProfilePage.clickSaveButton();
			commissionConfirmPage.clickConfirmButton();
			message = commissionProfilePage.getActualMsg();
			// result[2] = commissionProfilePage.getActualMsg();
			String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage",
					"");
			if (message.equals(Message)) {
				CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
			}
		}
		Log.methodExit(methodname);

		return message;

	}

	public String viewCBC(String domainName, String categoryName, String grade, String caseID)
			throws InterruptedException {

		final String methodname = "viewCBC";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);
		}
		

		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 0) {
			message = "skip";
		} else {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)
			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Window handler
			SwitchWindow.switchwindow(driver);

			// Fill in all the details
			if (isProductCodeVisible == true) {
				addProfileDetailsPage.selectProductCode(1);
			}
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
			{addProfileDetailsPage.selectTransactionType("ALL");
			if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
				addProfileDetailsPage.selectPaymentMode("ALL");}
			addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
			addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
			addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
			addProfileDetailsPage.clickTaxOnFOCFlag();
			addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

			/*
			 * To be removed if not in use by Lokesh
			 */
			int slabCount = addProfileDetailsPage.totalSlabs();
			CONSTANT.COMM_SLAB_COUNT = slabCount;

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				boolean even = false;

				if (slabIndex % 2 == 0) {
					even = true;
				}
				addProfileDetailsPage.enterStartEndRange(slabIndex);
				addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
				addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
				addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
				Log.info("commission varaible : " + slabIndex);
			}

			addProfileDetailsPage.clickAddButton();

			SwitchWindow.backwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 2);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(1);
				}

				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
				for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
					addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
				}
				message=addProfileDetailsPage.clickAddOtfButtonwhenPopup();
				SwitchWindow.backwindow(driver);

			} else {
				Log.info("Target based commission is not applicable.");
			}


			//Enter Other Commission Details
			addOtherCommissionDetails(domainName,categoryName);
			
			if (caseID.equals("SITCBCCOMMPROFILE26")) {
				addCommissionProfilePage.clickSaveButton();
			}

		}
		// Save and Confirm the Commission Profile.
		if (message==null)
		message = "pass";
		Log.methodExit(methodname);
		return message;

	}

	public String modifyCBCWrongValueSeq(String domainName, String categoryName, String grade, String commProfile)
			throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();

		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);

		}

		String currDate = homePage.getDate();

		modifyCommProfiledetailsPage.ModifyComm();

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		if (OTF_SLAB_ASSIGN == 0) {
			return "skip";
		} else {

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				addProfileDetailsPage.enterStartEndRange(slabIndex);
			}
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
			}

			addProfileDetailsPage.enterCbcValueSlab0("20");
			addProfileDetailsPage.enterCbcValueSlab1("10");
			addProfileDetailsPage.enterCbcRateSlab0("1");
			addProfileDetailsPage.enterCbcRateSlab1("2");
			String errorMsg=addProfileDetailsPage.clickAddOtfButtonwhenPopup();

		//	String errorMsg = addProfileDetailsPage.getErrorMessage();
			//addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);
			return errorMsg;

		}

	}

	public String modifyCBCDate(String domainName, String categoryName, String grade, String commProfile, String caseID)
			throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String errorMsg = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();

		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);

		}

		String currDate = homePage.getDate();

		modifyCommProfiledetailsPage.ModifyComm();

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		if (OTF_SLAB_ASSIGN == 0) {
			return "skip";
		} else {

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				addProfileDetailsPage.enterStartEndRange(slabIndex);
			}
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				switch (caseID) {
				case "SITCBCCOMMPROFILE28":
					addProfileDetailsPage.enterStartEndDateOTF("", toDate);
					break;
				case "SITCBCCOMMPROFILE29":
					addProfileDetailsPage.enterStartEndDateOTF(currDate, "");
					break;
				case "SITCBCCOMMPROFILE35":
					addProfileDetailsPage.enterStartEndDateOTF(homePage.addDaysToCurrentDate(currDate, -2), toDate);
					break;
				case "SITCBCCOMMPROFILE36": {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
				}

					break;
				}
				addProfileDetailsPage.enterCbcValueSlab0("10");
				addProfileDetailsPage.enterCbcValueSlab1("20");
				addProfileDetailsPage.enterCbcRateSlab0("1");
				addProfileDetailsPage.enterCbcRateSlab1("2");

			}

			errorMsg=addProfileDetailsPage.clickAddOtfButtonwhenPopup();

			if (caseID.equals("SITCBCCOMMPROFILE35") || caseID.equals("SITCBCCOMMPROFILE36")) {
				SwitchWindow.backwindow(driver);
				
				//Enter Other Commission Details
				addOtherCommissionDetails(domainName,categoryName);
				
				addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
				addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
				addCommissionProfilePage.clickSaveButton();
				errorMsg = addProfileDetailsPage.getErrorMessage();
			} else {
				//errorMsg = addProfileDetailsPage.getErrorMessage();
				//addProfileDetailsPage.clickCloseButton();
				SwitchWindow.backwindow(driver);
			}
			return errorMsg;

		}

	}

	public String modifyCBCSlab(String domainName, String categoryName, String grade, String commProfile, String caseID)
			throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();

		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);

		}

		String currDate = homePage.getDate();

		modifyCommProfiledetailsPage.ModifyComm();

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		if (OTF_SLAB_ASSIGN == 0) {
			return "skip";
		} else {

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				addProfileDetailsPage.enterStartEndRange(slabIndex);
			}
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
			}
			
			for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
			
				addProfileDetailsPage.enterCbcValueSlabAll(detailIndex,"");
				addProfileDetailsPage.enterOtfRateSlabAll(detailIndex,"");
				
			/*	addProfileDetailsPage.enterCbcValueSlab0("");
			addProfileDetailsPage.enterCbcValueSlab1("");
			addProfileDetailsPage.enterCbcRateSlab0("");
			addProfileDetailsPage.enterCbcRateSlab1("");*/
			
			}
			String	errorMsg=	addProfileDetailsPage.clickAddOtfButtonwhenPopup();
		//	String errorMsg = addProfileDetailsPage.getErrorMessage();
			//addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);
			return errorMsg;

		}

	}

	public String modifyCBCValueRate(String domainName, String categoryName, String grade, String commProfile,
			String caseID) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();

		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);

		}

		String currDate = homePage.getDate();

		modifyCommProfiledetailsPage.ModifyComm();

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		if (OTF_SLAB_ASSIGN == 0) {
			return "skip";
		} else {

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				addProfileDetailsPage.enterStartEndRange(slabIndex);
			}
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
			}

			switch (caseID) {
			case "SITCBCCOMMPROFILE31": {
				addProfileDetailsPage.enterCbcValueSlab0("ab");
				addProfileDetailsPage.enterCbcRateSlab0("1");
			}
				break;
			case "SITCBCCOMMPROFILE32": {
				addProfileDetailsPage.enterCbcValueSlab0("");
				addProfileDetailsPage.enterCbcRateSlab0("1");
			}
				break;
			case "SITCBCCOMMPROFILE33": {
				addProfileDetailsPage.enterCbcValueSlab0("10");
				addProfileDetailsPage.enterCbcRateSlab0("ss");
			}
				break;
			case "SITCBCCOMMPROFILE34": {
				addProfileDetailsPage.enterCbcValueSlab0("19");
				addProfileDetailsPage.enterCbcRateSlab0("");
			}
				break;

			}
			String errorMsg=addProfileDetailsPage.clickAddOtfButtonwhenPopup();

			//String errorMsg = addProfileDetailsPage.getErrorMessage();
		//	addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);
			return errorMsg;

		}

	}

	public String modifyCBC(String domainName, String categoryName, String grade, String commProfile, String caseID)
			throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		String message = "";
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();

		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);

		}

		String currDate = homePage.getDate();

		modifyCommProfiledetailsPage.ModifyComm();

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		if (OTF_SLAB_ASSIGN == 0) {
			return "skip";
		} else {

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				addProfileDetailsPage.enterStartEndRange(slabIndex);
			}
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				switch (caseID) {
				case "SITCBCCOMMPROFILE37": {
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					addProfileDetailsPage.enterStartEndDateOTF("", "");
				}
					break;
				case "SITCBCCOMMPROFILE38": {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF("");
				}
					break;
				case "SITCBCCOMMPROFILE39": {
					addProfileDetailsPage.enterStartEndDateOTF("", "");
					addProfileDetailsPage.enterCBCTimeSlabOTF("");
				}
					break;
				}
				addProfileDetailsPage.enterCbcValueSlab0("10");
				addProfileDetailsPage.enterCbcValueSlab1("20");
				addProfileDetailsPage.enterCbcRateSlab0("1");
				addProfileDetailsPage.enterCbcRateSlab1("2");

			}

			addProfileDetailsPage.clickAddOtfButton();

			SwitchWindow.backwindow(driver);
			
			//Enter Other Commission Details
			addOtherCommissionDetails(domainName,categoryName);
			
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
			addCommissionProfilePage.clickSaveButton();
			commissionConfirmPage.clickConfirmButton();
			message = commissionProfilePage.getActualMsg();
		}
		// Save and Confirm the Commission Profile.
		return message;
	}

	public String modifyCBCView(String domainName, String categoryName, String grade, String commProfile, String caseID)
			throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();

		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickModifyButton();

		modifyCommissionProfilePage.selectCommissionProfileSet(commProfile);
		modifyCommissionProfilePage.clickModifyButton();

		if (DUAL_COMM_FIELD_AVAILABLE == 1) {

			addCommissionProfilePage.selectCommissionType(PretupsI.Normal_Commission);

		}

		String currDate = homePage.getDate();

		modifyCommProfiledetailsPage.ModifyComm();

		// Window handler
		SwitchWindow.switchwindow(driver);

		/*
		 * To be removed if not in use by Lokesh
		 */
		int slabCount = addProfileDetailsPage.totalSlabs();
		if (OTF_SLAB_ASSIGN == 0) {
			return "skip";
		} else {

			for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
				addProfileDetailsPage.enterStartEndRange(slabIndex);
			}
			addProfileDetailsPage.clickAddButton();
			SwitchWindow.backwindow(driver);

			modifyCommProfiledetailsPage.ModifyCommOtf();

			// Window handler
			SwitchWindow.switchwindow(driver);

			String toDate = homePage.addDaysToCurrentDate(currDate, 3);
			if (targetbasedCommission.equalsIgnoreCase("true")) {
				addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
				addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
				addProfileDetailsPage.enterCbcValueSlab0("10");
				addProfileDetailsPage.enterCbcValueSlab1("20");
				addProfileDetailsPage.enterCbcRateSlab0("1");
				addProfileDetailsPage.enterCbcRateSlab1("2");

			}

			addProfileDetailsPage.clickAddOtfButton();

			SwitchWindow.backwindow(driver);

			if (caseID.equals("SITCBCCOMMPROFILE41")) {
				//Enter Other Commission Details
				addOtherCommissionDetails(domainName,categoryName);
				
				addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
				addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
				addCommissionProfilePage.clickSaveButton();
			}

		}
		// Save and Confirm the Commission Profile.
		String message = "pass";
		return message;
	}

	public String changeCBCPreference(String value) {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickPreferences();
		systemPreferencePage.selectModule("C2S");
		systemPreferencePage.selectPreferenceType(PretupsI.NETWORK_PREFERENCE_TYPE);
		systemPreferencePage.clickSubmitButton();
		String preferenceCode = DBHandler.AccessHandler
				.getNamefromSystemPreference(CONSTANT.TARGET_BASED_BASE_COMMISSION);
		systemPreferencePage.setValueofSystemPreference(preferenceCode, value);
		systemPreferencePage.clickModifyBtn();
		String errorMessage = systemPreferencePage.getErrorMessage();
		if (errorMessage != null)
			return errorMessage;
		else {
			systemPreferencePage.clickConfirmBtn();
			CacheUpdate.updateCache();
			return null;
		}
	}

	public String checkCBCLink(String domainName, String categoryName, String grade) {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();
		if (OTF_SLAB_ASSIGN == 0) {
			return "skip";
		} else {
			boolean isCBCLinkExist = addCommissionProfilePage.CBCVisibility();
			if (isCBCLinkExist)
				return "Pass";
			else
				return "Fail";
		}
	}

	public String[] addCommissionProfilewithoutAdditionalCommission_ZeroBaseCommission(String domainName,
			String categoryName, String grade, HashMap<String, String> modifyCommissionMap)
			throws InterruptedException {
		final String methodname = "addCommissionProfilewithoutAdditionalCommission_ZeroBaseCommission";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1) {
				commissionProfilePage.clickAddButton();
			}
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(modifyCommissionMap.get("COMMISSION_TYPE"));
			if (PretupsI.Postive_Commission.equals(modifyCommissionMap.get("COMMISSION_TYPE"))) {
				driver.switchTo().alert().accept();
			}
		}
		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();
		if (OTF_SLAB_ASSIGN == 0) {
			addCommissionProfilePage.clickAssignCommissionSlabs();
			SwitchWindow.switchwindow(driver);
			int index = 1;

			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();
				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;
				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;
					if (slabIndex % 2 == 0) {
						even = true;
					}
					String toDate = homePage.addDaysToCurrentDate(currDate, slabIndex);
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even, "0");
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					if (targetbasedCommission.equalsIgnoreCase("true")) {
						addProfileDetailsPage.enterStartEndDate(slabIndex, currDate, toDate);
						addProfileDetailsPage.enterCBCTimeSlab(slabIndex, _masterVO.getProperty("BaseTimeSlab"));
						for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
							if (detailIndex != 0) {
								addProfileDetailsPage.clickAssignCBCDetailSlabs(slabIndex);
							}
							if ("TRUE".equals(modifyCommissionMap.get("DECIMAL_CBC")))
								addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount, true,
										modifyCommissionMap.get("TAX_TYPE"));
							else
								addProfileDetailsPage.enterCBCDetails(slabIndex, detailIndex, subSlabCount);
						}
					} else {
						Log.info("Target based commission is not applicable.");
					}
					Log.info("commission varaible : " + slabIndex);
				}
				addProfileDetailsPage.clickAddButton();
				SwitchWindow.backwindow(driver);
			}
		} else {
			addCommissionProfilePage.clickAssignCommissionSlabs();
			SwitchWindow.switchwindow(driver);
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}
			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);
			for (int i = 1; i <= index; i++) {
				addCommissionProfilePage.clickAssignCommissionSlabs();
				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRANSACTION_TYPE).equalsIgnoreCase("TRUE"))
				{addProfileDetailsPage.selectTransactionType("ALL");
				if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
					addProfileDetailsPage.selectPaymentMode("ALL");}
				addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
				addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
				addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
				addProfileDetailsPage.clickTaxOnFOCFlag();
				addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();
				int slabCount = addProfileDetailsPage.totalSlabs();
				CONSTANT.COMM_SLAB_COUNT = slabCount;
				for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
					boolean even = false;
					if (slabIndex % 2 == 0) {
						even = true;
					}
					addProfileDetailsPage.enterStartEndRange(slabIndex);
					addProfileDetailsPage.enterCommissionSelectType(slabIndex, even, "0");
					addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
					addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
					Log.info("commission varaible : " + slabIndex);
				}
				addProfileDetailsPage.clickAddButton();
				SwitchWindow.backwindow(driver);
				addCommissionProfilePage.clickAssignOtfSlabs();
				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}
				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {
						if ("TRUE".equals(modifyCommissionMap.get("DECIMAL_CBC"))) {
							addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount, true,
									modifyCommissionMap.get("TAX_TYPE"));
						} else
							addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount);
					}
				} else {
					Log.info("Target based commission is not applicable.");
				}
				addProfileDetailsPage.clickAddOtfButton();
				SwitchWindow.backwindow(driver);
			}
		}
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		result[2] = commissionProfilePage.getActualMsg();
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		if (result[2].equals(Message)) {
			CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		}
		Log.methodExit(methodname);
		return result;
	}

	public HashMap<String, String> CBCgetAllOTFvalue(String domainName, String categoryName, String grade,
			String Comtype) throws InterruptedException {
		final String methodname = "CBCgetAllOTFvaluea";
		Log.methodEntry(methodname, domainName, categoryName, grade);
		WebElement wbCBCDetailsValue, wbCBCDetailsType;
		HashMap<String, String> OTFValue = new HashMap<String, String>();
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		result = new String[3];
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String profileName = UniqueChecker.UC_CPName();
		result[0] = "N";
		result[1] = profileName;
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(domainName);
		commissionProfilePage.selectCategory(categoryName);
		if (COMM_PROFILE_CLIENTVER > 0) {
			if (COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();

			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(grade);
		}
		commissionProfilePage.clickAddButton();

		// Enter all the details
		addCommissionProfilePage.enterProfileName(profileName);
		addCommissionProfilePage.enterShortCode(profileName);
		if (DUAL_COMM_FIELD_AVAILABLE == 1) {
			addCommissionProfilePage.selectCommissionType(Comtype);
		}
		if (!Comtype.equals("NC")) {
			driver.switchTo().alert().accept();
		}
		if (_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase("true")) {
			addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
			addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());
		}
		String currDate = homePage.getDate();

		if (OTF_SLAB_ASSIGN == 1) {
			// Fill in Commission Slabs. (Separate slab for each Product will be
			// added)

			addCommissionProfilePage.clickAssignCommissionSlabs();

			// Initializing Window Handlers
			SwitchWindow.switchwindow(driver);

			// Getting List size of Product.
			int index = 1;
			boolean isProductCodeVisible;
			try {
				index = addProfileDetailsPage.getProductCodeIndex();
				isProductCodeVisible = true;
			} catch (NoSuchElementException e) {
				isProductCodeVisible = false;
			}

			addProfileDetailsPage.clickCloseButton();
			SwitchWindow.backwindow(driver);

			for (int i = 1; i <= index; i++) {

				addCommissionProfilePage.clickAssignCommissionSlabs();

				// Window handler
				SwitchWindow.switchwindow(driver);

				int transactionIndex;
				transactionIndex = addProfileDetailsPage.getTransactionTypeIndex();

				for (int j = 0; j < transactionIndex; j++) {
					// Fill in all the details
					if (isProductCodeVisible == true) {
						addProfileDetailsPage.selectProductCode(i);
					}
					addProfileDetailsPage.selectTransactionType(j);

					int paymentIndex = 1;
					boolean flag = addProfileDetailsPage.getValueFromDropDown("Operator to Channel");
					if (flag) {
						if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
							paymentIndex = addProfileDetailsPage.getPaymentModeIndex();
					}

					for (int k = 0; k < paymentIndex; k++) {
						if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PAYMENT_MODE_ALWD).equalsIgnoreCase("TRUE"))
						addProfileDetailsPage.selectPaymentMode(k);
						addProfileDetailsPage.enterMultipleOf(_masterVO.getProperty("CommMultipleOf"));
						addProfileDetailsPage.enterMinTransferValue(_masterVO.getProperty("MintransferValue"));
						addProfileDetailsPage.enterMaxTransferValue(_masterVO.getProperty("MaxTransferValue"));
						addProfileDetailsPage.clickTaxOnFOCFlag();
						addProfileDetailsPage.clickTaxCalculatedOnC2CTransfer();

						int slabCount = addProfileDetailsPage.totalSlabs();
						CONSTANT.COMM_SLAB_COUNT = slabCount;

						for (int slabIndex = 0; slabIndex < slabCount; slabIndex++) {
							boolean even = false;

							if (slabIndex % 2 == 0) {
								even = true;
							}
							addProfileDetailsPage.enterStartEndRange(slabIndex);
							addProfileDetailsPage.enterCommissionSelectType(slabIndex, even);
							addProfileDetailsPage.enterTax1SelectType(slabIndex, even);
							addProfileDetailsPage.enterTax2SelectType(slabIndex, even);
							addProfileDetailsPage.enterTax3SelectType(slabIndex, even);
							Log.info("commission varaible : " + slabIndex);
						}

						addProfileDetailsPage.clickAddButton();

						SwitchWindow.backwindow(driver);

						if (k < (paymentIndex - 1)) {

							addCommissionProfilePage.clickAssignCommissionSlabs();

							// Window handler
							SwitchWindow.switchwindow(driver);

							if (isProductCodeVisible == true) {
								addProfileDetailsPage.selectProductCode(i);
							}
							addProfileDetailsPage.selectTransactionType(j);

						}

					}

					if (j < (transactionIndex - 1)) {

						addCommissionProfilePage.clickAssignCommissionSlabs();

						// Window handler
						SwitchWindow.switchwindow(driver);

					}

				}

				addCommissionProfilePage.clickAssignOtfSlabs();

				SwitchWindow.switchwindow(driver);
				if (isProductCodeVisible == true) {
					addProfileDetailsPage.selectProductCode(i);
				}

				String toDate = homePage.addDaysToCurrentDate(currDate, 2);
				if (targetbasedCommission.equalsIgnoreCase("true")) {
					addProfileDetailsPage.enterStartEndDateOTF(currDate, toDate);
					addProfileDetailsPage.enterCBCTimeSlabOTF(_masterVO.getProperty("BaseTimeSlab"));
					for (int detailIndex = 0; detailIndex < subSlabCount; detailIndex++) {

						OTFValue.put("CBCValue" + detailIndex,
								addProfileDetailsPage.enterCBCDetailsOTF(detailIndex, subSlabCount));
					}

				} else {
					Log.info("Target based commission is not applicable.");
				}

				addProfileDetailsPage.clickAddOtfButton();

				SwitchWindow.backwindow(driver);
			}

		}

		// Fill in AddAdditionalCommissionSlabs
		// To Check if Additional Commission slabs exists or not.
		/*
		 * boolean addCommVisibity =
		 * addCommissionProfilePage.addAddititionalCommissionVisibility(); if
		 * (addCommVisibity == true) { result[0] = "Y";
		 * addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
		 * 
		 * SwitchWindow.switchwindow(driver); int
		 * index1=addAdditionalCommissionDetailsPage.getServicesIndex();
		 * 
		 * addProfileDetailsPage.clickCloseButton(); SwitchWindow.backwindow(driver);
		 * 
		 * int i = 1, j = 1; while (i <= index1) { {
		 * addCommissionProfilePage.clickAssignAdditionalCommissionSlabs();
		 * SwitchWindow.switchwindow(driver);
		 * 
		 * addAdditionalCommissionDetailsPage.selectService(i);
		 * 
		 * // Check the existence of Sub Service field. (separate // slab for each sub
		 * service will be added) boolean subservice =
		 * addAdditionalCommissionDetailsPage.subServiceVisibility(); if (subservice ==
		 * true) { int index2 = addAdditionalCommissionDetailsPage.getSubServiceIndex();
		 * addAdditionalCommissionDetailsPage.selectSubServiceCode(j); if (j != index2)
		 * j++; else {i++;j=1;}// added by Lokesh 22/11/2017 -> Go to next service and
		 * reinitialise counter(j) for subservice }
		 * 
		 * // Fill in all the details if (COMM_PROFILE_CLIENTVER > 0)
		 * addAdditionalCommissionDetailsPage.selectGatewayCodeAll(_masterVO.getProperty
		 * ("GatewayCode"));
		 * addAdditionalCommissionDetailsPage.enterMinTransferValue(_masterVO.
		 * getProperty("MintransferValue"));
		 * addAdditionalCommissionDetailsPage.enterMaxTransferValue(_masterVO.
		 * getProperty("MaxTransferValue1"));
		 * if(_masterVO.getClientDetail("ADDCOMMAPPLICABLEDATETIME").equalsIgnoreCase(
		 * "true")){
		 * addAdditionalCommissionDetailsPage.enterApplicableFromDate(currDate); String
		 * ApplicableToDate = homePage.addDaysToCurrentDate(currDate, 90);
		 * addAdditionalCommissionDetailsPage.enterApplicableToDate(ApplicableToDate); }
		 * 
		 * int addCommSlabCount = addAdditionalCommissionDetailsPage.totalSlabs();
		 * boolean roamCommTypeVisibity =
		 * addAdditionalCommissionDetailsPage.roamCommTypeVisibility();
		 * 
		 * for (int slabIndex = 0; slabIndex < addCommSlabCount; slabIndex++) { boolean
		 * even = false;
		 * 
		 * if (slabIndex % 2 == 0) { even = true; } String toDate =
		 * homePage.addDaysToCurrentDate(currDate, slabIndex);
		 * addAdditionalCommissionDetailsPage.enterStartEndRange(slabIndex);
		 * addAdditionalCommissionDetailsPage.enterCommissionSelectType(slabIndex,
		 * even); if(roamCommTypeVisibity==true){
		 * addAdditionalCommissionDetailsPage.enterRoamCommissionSelectType(slabIndex,
		 * even);}
		 * addAdditionalCommissionDetailsPage.enterDifferentialSelectType(slabIndex,
		 * even); addAdditionalCommissionDetailsPage.enterTax1SelectType(slabIndex,
		 * even); addAdditionalCommissionDetailsPage.enterTax2SelectType(slabIndex,
		 * even); if(targetbasedaddtnlcommission.equalsIgnoreCase("true")){
		 * addAdditionalCommissionDetailsPage.enterStartEndDate(slabIndex, currDate,
		 * toDate); addAdditionalCommissionDetailsPage.enterCACType(slabIndex, even);
		 * addAdditionalCommissionDetailsPage.enterCACTimeSlab(slabIndex,
		 * _masterVO.getProperty("TimeSlab"));
		 * //LoadPropertiesFile.PropertiesMap.get("TimeSlab")); for(int detailIndex = 0;
		 * detailIndex < subSlabCount; detailIndex++ ) { if(detailIndex != 0) {
		 * addAdditionalCommissionDetailsPage.clickAssignCACDetailSlabs(slabIndex); }
		 * addAdditionalCommissionDetailsPage.enterCACDetails(slabIndex, detailIndex,
		 * subSlabCount); } } }
		 * 
		 * if (subservice == false) { i++;j=1; // added by Lokesh 22/11/2017 -> Go to
		 * next service and reinitialise counter(j) for subservice }
		 * 
		 * // Switch to the main Commission Profile details page
		 * addAdditionalCommissionDetailsPage.clickAddButton();
		 * SwitchWindow.backwindow(driver);
		 * 
		 * } } }
		 * 
		 */
		//Enter Other Commission Details
		addOtherCommissionDetails(domainName,categoryName);
		
		addCommissionProfilePage.enterApplicableFromDate(homePage.getDate());
		addCommissionProfilePage.enterApplicableFromHour(homePage.getApplicableFromTime());

		// Save and Confirm the Commission Profile.
		addCommissionProfilePage.clickSaveButton();
		commissionConfirmPage.clickConfirmButton();
		result[2] = commissionProfilePage.getActualMsg();
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		if (result[2].equals(Message)) {
			CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		}

		Log.methodExit(methodname);
		return OTFValue;

	}

	public HashMap<String,String> addOtherCommissionProfile(HashMap<String,String> data){
		final String methodName = "addOtherCommissionProfile";
		Log.methodEntry(methodName,data.values());
		int rowCount;
		Date currentDate = new Date();
		
		String profileName = UniqueChecker.UC_Table_Column(PretupsI.TABLE_OTHER_COMM_PRF_SET, PretupsI.COLUMN_OTHER_COMM_PRF_SET_NAME, "AUTOCP_" , 5);
		data.put("profile", profileName);
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		profileMgmntSubCats.clickOtherCommProfile();
		otherCommissionProfilePage.selectType(data.get("type"));
		otherCommissionProfilePage.selectTypeCommissionValue(data.get("typeValue"));
		otherCommissionProfilePage.clickAddOtherCP();
		rowCount = otherCommissionProfilePage.totalSlabs();
		otherCommissionProfilePage.checkO2CBox();
		otherCommissionProfilePage.checkC2CBox();
		otherCommissionProfilePage.enterOtherCommissionName(profileName);
		otherCommissionProfilePage.processOtherCommissionValues(rowCount);
		otherCommissionProfilePage.clickSubmit();
		otherCommissionProfilePage.clickConfirm();
		
		data.put("message", otherCommissionProfilePage.getActualMsg());
	
		return data;
	}
	
	public void writeOtherCommissionDetails(int rowNum,HashMap<String,String> otherCommProfile ) {
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		ExcelUtility.setCellData(0, ExcelI.OTHER_COMMISSION_TYPE, rowNum, otherCommProfile.get("type"));
		ExcelUtility.setCellData(0, ExcelI.OTHER_COMMISSION_VALUE, rowNum, otherCommProfile.get("typeValue"));
		ExcelUtility.setCellData(0, ExcelI.OTHER_COMMISSION_PROFILE, rowNum, otherCommProfile.get("profile"));
	}
	
	public void addOtherCommissionDetails(String domainName, String categoryName) {
		try {
			if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
				/*addCommissionProfilePage.selectOtherCommissionType(_masterVO.getProperty("OCPType"));
				addCommissionProfilePage.selectOtherCommissionTypeValue(_masterVO.getProperty("OCPTypeValue"));
				 */
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
				int totalRow1 = ExcelUtility.getRowCount();
	
				int c = 1;
				for (c = 1; c <= totalRow1; c++){
					if ((ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, c).trim().matches(domainName))
							&& (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, c).trim().matches(categoryName)))
						break;
				}
				addCommissionProfilePage.selectOtherCommissionType(ExcelUtility.getCellData(0,ExcelI.OTHER_COMMISSION_TYPE,c));
				addCommissionProfilePage.selectOtherCommissionTypeValue(ExcelUtility.getCellData(0,ExcelI.OTHER_COMMISSION_VALUE,c));
				addCommissionProfilePage.selectOtherCommissionProfile(ExcelUtility.getCellData(0,ExcelI.OTHER_COMMISSION_PROFILE,c));			
			}
		}catch(Exception e) {
			Log.info(e.getMessage());
		}
		
	}
	
	public void setOCP(HashMap<String,String> data) {
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		data.put("profile",ExcelUtility.getCellData(0,ExcelI.OTHER_COMMISSION_PROFILE,Integer.parseInt(data.get("row"))));	
	}
	public HashMap<String,ArrayList<String>> loadOtherCommissionValuesByType(HashMap<String,String> commTypes){
		HashMap<String,ArrayList<String>> otherCommValues = new HashMap<String,ArrayList<String>>();
		String otherCommType;
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		profileMgmntSubCats.clickOtherCommProfile();
		
		//load values by other commission types
		for(Map.Entry<String, String> entry: commTypes.entrySet()) {
			otherCommType = entry.getValue().trim();
			if(otherCommType.equals(PretupsI.CATEGORY) || otherCommType.equals(PretupsI.GRADE))
			{
				otherCommValues.put(otherCommType, null); // to select default values from dataprovider
			}
			else 
			{
				otherCommissionProfilePage.selectType(otherCommType);			
				otherCommValues.put(otherCommType, otherCommissionProfilePage.getOtherCommValuesOfType());
			}
		}
		
		return otherCommValues;
	}
	
	public HashMap<String,String> modifyOtherCommissionProfile(HashMap<String,String> data){

			final String methodName = "modifyOtherCommissionProfile";
		Log.methodEntry(methodName,data.values());
		int rowCount;
		Date currentDate = new Date();
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		profileMgmntSubCats.clickOtherCommProfile();
		otherCommissionProfilePage.selectType(data.get("type"));
		otherCommissionProfilePage.selectTypeCommissionValue(data.get("typeValue"));
		otherCommissionProfilePage.clickModifyOtherCP();
		setOCP(data);
		otherCommissionProfilePage.selectTypeCommissionName(data.get("profile"));
		otherCommissionProfilePage.clickModifyOtherCP();
		rowCount = otherCommissionProfilePage.totalSlabs();
		otherCommissionProfilePage.checkO2CBox();
		otherCommissionProfilePage.checkC2CBox();
		String profileName = UniqueChecker.UC_Table_Column(PretupsI.TABLE_OTHER_COMM_PRF_SET, PretupsI.COLUMN_OTHER_COMM_PRF_SET_NAME, "AUTOCP_" , 5);
		data.put("profile", profileName);
		otherCommissionProfilePage.enterOtherCommissionName(profileName);
		otherCommissionProfilePage.processOtherCommissionValues(rowCount);
		otherCommissionProfilePage.clickSubmit();
		otherCommissionProfilePage.clickConfirm();
		
		data.put("message", otherCommissionProfilePage.getActualMsg());

		return data;
	}
	
	public boolean viewOtherCommissionProfile(HashMap<String,String> data){
		final String methodName = "viewOtherCommissionProfile";
		Log.methodEntry(methodName,data.values());
		int rowCount;
		Date currentDate = new Date();
		boolean profileFound = false;
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		profileMgmntSubCats.clickOtherCommProfile();
		otherCommissionProfilePage.selectType(data.get("type"));
		otherCommissionProfilePage.selectTypeCommissionValue(data.get("typeValue"));
		otherCommissionProfilePage.clickViewOtherCP();
		setOCP(data);
		otherCommissionProfilePage.selectTypeCommissionName(data.get("profile"));
		otherCommissionProfilePage.clickViewOtherCP();
		profileFound = otherCommissionProfilePage.findOtherCommission(data.get("profile"));
		
		return profileFound;
	}
	
	public String ocpNegativeBlankEntry(){
			final String methodName = "ocpNegativeBlankEntry";
		Log.methodEntry(methodName,"Entered");
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		profileMgmntSubCats.clickOtherCommProfile();
		otherCommissionProfilePage.clickAddOtherCP();
	
		String message = otherCommissionProfilePage.getActualMsg();
		
		return message;
	}
	public HashMap<String,String> ocpNegativeTransferTypeBlank(HashMap<String,String> data){
		final String methodName = "ocpNegativeTransferTypeBlank";
		Log.methodEntry(methodName,data.values());
		int rowCount;
		
		String profileName = UniqueChecker.UC_Table_Column(PretupsI.TABLE_OTHER_COMM_PRF_SET, PretupsI.COLUMN_OTHER_COMM_PRF_SET_NAME, "AUTOCP_" , 5);
		data.put("profile", profileName);
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		profileMgmntSubCats.clickOtherCommProfile();
		otherCommissionProfilePage.selectType(data.get("type"));
		otherCommissionProfilePage.selectTypeCommissionValue(data.get("typeValue"));
		otherCommissionProfilePage.clickAddOtherCP();
		rowCount = otherCommissionProfilePage.totalSlabs();
		otherCommissionProfilePage.enterOtherCommissionName(profileName);
		otherCommissionProfilePage.processOtherCommissionValues(rowCount);
		otherCommissionProfilePage.clickSubmit();
		otherCommissionProfilePage.clickConfirm();
		
		data.put("message", otherCommissionProfilePage.getActualMsg());
	
		return data;
	}
	
	public HashMap<String,String> ocpNegativeStartRangeLesser(HashMap<String,String> data){
	
			final String methodName = "ocpNegativeStartRangeLesser";
		Log.methodEntry(methodName,data.values());
		int rowCount;
		
		String profileName = UniqueChecker.UC_Table_Column(PretupsI.TABLE_OTHER_COMM_PRF_SET, PretupsI.COLUMN_OTHER_COMM_PRF_SET_NAME, "AUTOCP_" , 5);
		data.put("profile", profileName);
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		profileMgmntSubCats.clickOtherCommProfile();
		otherCommissionProfilePage.selectType(data.get("type"));
		otherCommissionProfilePage.selectTypeCommissionValue(data.get("typeValue"));
		otherCommissionProfilePage.clickAddOtherCP();
		rowCount = otherCommissionProfilePage.totalSlabs();
		otherCommissionProfilePage.checkO2CBox();
		otherCommissionProfilePage.checkC2CBox();
		otherCommissionProfilePage.enterOtherCommissionName(profileName);
		if(rowCount<2) {
			data.put("message", "skip");
			return data;
		}
		otherCommissionProfilePage.processOtherCommissionValuesNegative(2);
		otherCommissionProfilePage.clickSubmit();
		otherCommissionProfilePage.clickConfirm();
		
		data.put("message", otherCommissionProfilePage.getActualMsg());
	
		return data;
	}
}