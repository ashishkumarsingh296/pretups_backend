package com.Features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.restrictedList.ApproveRestList;
import com.pageobjects.channeladminpages.restrictedList.BlacklistSubscribers;
import com.pageobjects.channeladminpages.restrictedList.DeleteRestrictedList;
import com.pageobjects.channeladminpages.restrictedList.UnBlacklistedSubscribers;
import com.pageobjects.channeladminpages.restrictedList.UploadRestrictedListPage;
import com.pageobjects.channeladminpages.restrictedList.restListMngmtSubCategoriesPage;
import com.pageobjects.channeladminpages.restrictedList.selectMSISDNForApproval;
import com.pageobjects.channeladminpages.restrictedList.uploadConfirmPage;
import com.pageobjects.channeladminpages.restrictedList.viewRestrictedList;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;

public class RestrictedListMgmt {

	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	RandomGeneration randmGenrtr;
	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;
	UploadRestrictedListPage UploadRestrictedListPage;
	uploadConfirmPage uploadConfirmPage;
	restListMngmtSubCategoriesPage restListMngmtSubCategoriesPage;
	ApproveRestList ApproveRestList;
	selectMSISDNForApproval selectMSISDNForApproval;
	DeleteRestrictedList DeleteRestrictedList;
	BlacklistSubscribers BlacklistSubscribers;
	UnBlacklistedSubscribers UnBlacklistedSubscribers;
	viewRestrictedList	viewRestrictedList;
	NetworkAdminHomePage homePage;

	public RestrictedListMgmt(WebDriver driver){

		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		UploadRestrictedListPage = new UploadRestrictedListPage(driver);
		uploadConfirmPage = new uploadConfirmPage(driver);
		restListMngmtSubCategoriesPage = new restListMngmtSubCategoriesPage(driver);
		ApproveRestList = new ApproveRestList(driver);
		randmGenrtr = new RandomGeneration();
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
		selectMSISDNForApproval = new selectMSISDNForApproval(driver);
		DeleteRestrictedList = new DeleteRestrictedList(driver);
		BlacklistSubscribers = new BlacklistSubscribers(driver);
		UnBlacklistedSubscribers = new UnBlacklistedSubscribers(driver);
		viewRestrictedList = new viewRestrictedList(driver);
		homePage = new NetworkAdminHomePage(driver);

	}



	/*public List<String> prepareRestrictedList() {
		final int numberOfMSISDNS = 3;

		List<String> fileData = new ArrayList<String>();
		fileData.add("[START]");

		for (int i = 0; i <= numberOfMSISDNS; i++) {
			String generatedMSISDN = GenerateMSISDN.generateRandomMSISDNWithinNetwork(PretupsI.PREPAID_LOOKUP);
			if (!fileData.contains(generatedMSISDN))
				fileData.add(generatedMSISDN);
		}	

		fileData.add("[END]");

		return fileData;
	}*/


	public List<String> prepareRestrictedList(int no,String startTag, String endTag) {
		final int numberOfMSISDNS = no;

		List<String> fileData = new ArrayList<String>();
		fileData.add(startTag);

		for (int i = 1; i <= numberOfMSISDNS; i++) {
			String generatedMSISDN = GenerateMSISDN.generateRandomMSISDNWithinNetwork(PretupsI.PREPAID_LOOKUP);
			if (!fileData.contains(generatedMSISDN))
				fileData.add(generatedMSISDN);
		}	

		fileData.add(endTag);

		return fileData;
	}
	
	
	public List<String> prepareBlacklistMSISDNList(List<String> blacklistList,int no,String startTag, String endTag) {
		final int numberOfMSISDNS = no;

		List<String> fileData = new ArrayList<String>();
		fileData.add(startTag);

		for (int i = 1; i <= numberOfMSISDNS; i++) {
			String generatedMSISDN = blacklistList.get(i);
			if (!fileData.contains(generatedMSISDN))
				fileData.add(generatedMSISDN);
		}	

		fileData.add(endTag);

		return fileData;
	}
	
	

	/*public String uploadMSISDNList( String domain, String category,String geography, String usr,String testCaseID,List<String> msisdnList) throws IOException{

		userInfo= UserAccess.getUserWithAccess(RolesI.RESTRICTED_LIST_UPLOAD);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_UploadListLink();
		//UploadRestrictedListPage.selectGeoDomain(geography);
		UploadRestrictedListPage.selectDomainCode(domain);
		UploadRestrictedListPage.selectCategory(category);
		UploadRestrictedListPage.enterUserName(usr);
		UploadRestrictedListPage.selectSubType("PRE");
		UploadRestrictedListPage.uploadFile(msisdnList,testCaseID);
		UploadRestrictedListPage.enterNoOfRecords("4");
		UploadRestrictedListPage.clickSubmit();
		uploadConfirmPage.clickConfirm();
		String msg = UploadRestrictedListPage.getMessage();


		return msg;

	}*/

	public String uploadMSISDNList( Map<String,String> dataMap, String testCaseID,List<String> msisdnList,int noOfRecords) throws IOException{

		userInfo= UserAccess.getUserWithAccess(RolesI.RESTRICTED_LIST_UPLOAD);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_UploadListLink();
		//UploadRestrictedListPage.selectGeoDomain(geography);
		UploadRestrictedListPage.selectDomainCode(dataMap.get("domainName"));
		UploadRestrictedListPage.selectCategory(dataMap.get("categoryName"));
		UploadRestrictedListPage.enterUserName(dataMap.get("username"));
		UploadRestrictedListPage.selectSubType(dataMap.get("subType"));
		UploadRestrictedListPage.uploadFile(msisdnList,testCaseID);
		String newNo = String.valueOf(noOfRecords);
		UploadRestrictedListPage.enterNoOfRecords(newNo);
		UploadRestrictedListPage.clickSubmit();
		uploadConfirmPage.clickConfirm();
		String msg = null;
		try{

			msg = UploadRestrictedListPage.getMessage();
			Log.info("There is no Error Message on screen,Message fetched as : " +msg);
		}
		catch(Exception e){
			msg = UploadRestrictedListPage.getErrorMessage();
			Log.info("There is Error Message on screen,Error Message fetched as : " +msg);
		}


		return msg;

	}

	public String uploadMSISDNListdiffFormat( String domain, String category,String geography, String usr,String testCaseID,List<String> msisdnList,int noOfRecords) throws IOException{

		userInfo= UserAccess.getUserWithAccess(RolesI.RESTRICTED_LIST_UPLOAD);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_UploadListLink();
		//UploadRestrictedListPage.selectGeoDomain(geography);
		UploadRestrictedListPage.selectDomainCode(domain);
		UploadRestrictedListPage.selectCategory(category);
		UploadRestrictedListPage.enterUserName(usr);
		UploadRestrictedListPage.selectSubType("PRE");
		UploadRestrictedListPage.uploadFileincorrectFormat(msisdnList,testCaseID);
		UploadRestrictedListPage.enterNoOfRecords(String.valueOf(noOfRecords));
		UploadRestrictedListPage.clickSubmit();
		uploadConfirmPage.clickConfirm();
		String msg = UploadRestrictedListPage.getErrorMessage();


		return msg;

	}


	public String approveSingleMSISDNList(String domain,String category, String geography,String user,String MSISDN){

		userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_approveListLink();
		//ApproveRestList.selectGeoDomain(geography);
		ApproveRestList.selectDomainCode(domain);
		ApproveRestList.selectCategory(category);
		ApproveRestList.enterUserName(user);
		ApproveRestList.clickSubmit();
		/*PaginationHandler PaginationHandler = new PaginationHandler();
		PaginationHandler.getToLastPage(driver);*/
		selectMSISDNForApproval.selectMSISDNToApprove(MSISDN);
		selectMSISDNForApproval.clickSubmit();
		selectMSISDNForApproval.clickConfirm();
		String msg = selectMSISDNForApproval.getMessage();

		return msg;

	}


	public String approveRestListMultipleOperation(String domain,String category, String geography,String user,String MSISDN1,String MSISDN2,String MSISDN3){

		userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_approveListLink();
		//ApproveRestList.selectGeoDomain(geography);
		ApproveRestList.selectDomainCode(domain);
		ApproveRestList.selectCategory(category);
		ApproveRestList.enterUserName(user);
		ApproveRestList.clickSubmit();
		/*PaginationHandler PaginationHandler = new PaginationHandler();
		PaginationHandler.getToLastPage(driver);*/
		selectMSISDNForApproval.selectMSISDNToApprove(MSISDN1);
		selectMSISDNForApproval.selectMSISDNForRejection(MSISDN2);
		selectMSISDNForApproval.selectMSISDNToDiscard(MSISDN3);
		selectMSISDNForApproval.clickSubmit();
		selectMSISDNForApproval.clickConfirm();
		String msg = selectMSISDNForApproval.getMessage();

		return msg;

	}



	public String RejectSingleMSISDNList(String domain,String category, String geography,String user,String MSISDN){

		userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_approveListLink();
		//ApproveRestList.selectGeoDomain(geography);
		ApproveRestList.selectDomainCode(domain);
		ApproveRestList.selectCategory(category);
		ApproveRestList.enterUserName(user);
		ApproveRestList.clickSubmit();
		/*PaginationHandler PaginationHandler = new PaginationHandler();
		PaginationHandler.getToLastPage(driver);*/
		selectMSISDNForApproval.selectMSISDNForRejection(MSISDN);
		selectMSISDNForApproval.clickSubmit();
		selectMSISDNForApproval.clickConfirm();
		String msg = selectMSISDNForApproval.getMessage();

		return msg;

	}


	public String DiscardSingleMSISDNList(String domain,String category, String geography,String user,String MSISDN){

		userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_approveListLink();
		//ApproveRestList.selectGeoDomain(geography);
		ApproveRestList.selectDomainCode(domain);
		ApproveRestList.selectCategory(category);
		ApproveRestList.enterUserName(user);
		ApproveRestList.clickSubmit();
		/*PaginationHandler PaginationHandler = new PaginationHandler();
		PaginationHandler.getToLastPage(driver);*/
		selectMSISDNForApproval.selectMSISDNToDiscard(MSISDN);
		selectMSISDNForApproval.clickSubmit();
		selectMSISDNForApproval.clickConfirm();
		String msg = selectMSISDNForApproval.getMessage();

		return msg;

	}


	public String approveMSISDNList( String domain, String category,String geography, String usr){

		userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_approveListLink();
		//ApproveRestList.selectGeoDomain(geography);
		ApproveRestList.selectDomainCode(domain);
		ApproveRestList.selectCategory(category);
		ApproveRestList.enterUserName(usr);
		ApproveRestList.clickSubmit();
		selectMSISDNForApproval.selectApproveAll();
		selectMSISDNForApproval.clickSubmit();
		selectMSISDNForApproval.clickConfirm();
		String msg = selectMSISDNForApproval.getMessage();

		return msg;

	}





	public String rejectMSISDNList(String domain, String category,String geography,String usr){

		userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_approveListLink();
		//ApproveRestList.selectGeoDomain(geography);
		ApproveRestList.selectDomainCode(domain);
		ApproveRestList.selectCategory(category);
		ApproveRestList.enterUserName(usr);
		ApproveRestList.clickSubmit();
		selectMSISDNForApproval.selectRejectAll();
		selectMSISDNForApproval.clickSubmit();
		selectMSISDNForApproval.clickConfirm();
		String msg = selectMSISDNForApproval.getMessage();

		return msg;

	}


	public String discardMSISDNList(String geography, String domain, String category, String usr){

		userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_approveListLink();
		//ApproveRestList.selectGeoDomain(geography);
		ApproveRestList.selectDomainCode(domain);
		ApproveRestList.selectCategory(category);
		ApproveRestList.enterUserName(usr);
		ApproveRestList.clickSubmit();
		selectMSISDNForApproval.selectdiscardAll();
		selectMSISDNForApproval.clickSubmit();
		selectMSISDNForApproval.clickConfirm();
		String msg = selectMSISDNForApproval.getMessage();

		return msg;

	}


	public String deleteRestrictedMSISDN(String domain, String category, String usr, String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_DeleteRestMSISDNLink();
		DeleteRestrictedList.selectDomainCode(domain);
		DeleteRestrictedList.selectCategory(category);
		DeleteRestrictedList.enterUserName(usr);
		DeleteRestrictedList.enterMSISDN(msisdn);
		DeleteRestrictedList.clickSubmit();
		DeleteRestrictedList.selectSingleMSISDNChkBox();
		DeleteRestrictedList.clickSubmitDelete();
		String msg = null;

		DeleteRestrictedList.clickConfirmDelete();
		msg = DeleteRestrictedList.getMessage();

		return msg;

	}

	public String deleteRestrictedMSISDNeg(String domain, String category, String usr, String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_DeleteRestMSISDNLink();
		DeleteRestrictedList.selectDomainCode(domain);
		DeleteRestrictedList.selectCategory(category);
		DeleteRestrictedList.enterUserName(usr);
		DeleteRestrictedList.enterMSISDN(msisdn);
		DeleteRestrictedList.clickSubmit();


		String	msg = DeleteRestrictedList.getMessage();	

		return msg;
	}
	
	
	public String deleteAllRestrictedMSISDN(String domain, String category, String usr){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_DeleteRestMSISDNLink();
		DeleteRestrictedList.selectDomainCode(domain);
		DeleteRestrictedList.selectCategory(category);
		DeleteRestrictedList.enterUserName(usr);
		DeleteRestrictedList.selectAllSubChkBox();
		DeleteRestrictedList.clickSubmit();
		DeleteRestrictedList.clickConfirmAllDelete();
		String msg = DeleteRestrictedList.getMessage();

		return msg;

	}


	public String blacklistSubscriberP2PPayee(String domain,String category,String usr, String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_blackSubsLink();
		BlacklistSubscribers.selectDomainCode(domain);
		BlacklistSubscribers.selectCategory(category);
		BlacklistSubscribers.enterUserName(usr);
		BlacklistSubscribers.selectP2PPayeeType();
		BlacklistSubscribers.deselectC2SPayeeType();
		BlacklistSubscribers.deselectP2PPayerType();
		BlacklistSubscribers.clickSubmit();
		BlacklistSubscribers.enterMSISDN(msisdn);
		BlacklistSubscribers.clickSubmit1();
		BlacklistSubscribers.clickConfirm();
		String msg = BlacklistSubscribers.getMessage();

		return msg; 

	}




	public String blacklistSubscriberP2PPayer(String domain,String category,String usr, String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_blackSubsLink();
		BlacklistSubscribers.selectDomainCode(domain);
		BlacklistSubscribers.selectCategory(category);
		BlacklistSubscribers.enterUserName(usr);
		BlacklistSubscribers.selectP2PPayerType();
		BlacklistSubscribers.deselectP2PPayeeType();
		BlacklistSubscribers.deselectC2SPayeeType();
		BlacklistSubscribers.clickSubmit();
		BlacklistSubscribers.enterMSISDN(msisdn);
		BlacklistSubscribers.clickSubmit1();
		BlacklistSubscribers.clickConfirm();
		String msg = BlacklistSubscribers.getMessage();

		return msg; 

	}


	public String blacklistSubscribersAll(String domain,String category,String usr){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_blackSubsLink();
		BlacklistSubscribers.selectDomainCode(domain);
		BlacklistSubscribers.selectCategory(category);
		BlacklistSubscribers.enterUserName(usr);
		BlacklistSubscribers.selectP2PPayerType();
		BlacklistSubscribers.selectP2PPayeeType();
		BlacklistSubscribers.selectC2SPayeeType();
		BlacklistSubscribers.selectSubscriberAll();
		BlacklistSubscribers.clickSubmit();

		BlacklistSubscribers.clickBlacklistAllConfirm();
		driver.switchTo().alert().accept();
		String msg = BlacklistSubscribers.getMessage();

		return msg; 

	}




	public String blacklistSubscriberC2SPayee(String domain,String category,String usr, String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_blackSubsLink();
		BlacklistSubscribers.selectDomainCode(domain);
		BlacklistSubscribers.selectCategory(category);
		BlacklistSubscribers.enterUserName(usr);
		BlacklistSubscribers.selectC2SPayeeType();
		BlacklistSubscribers.deselectP2PPayeeType();
		BlacklistSubscribers.deselectP2PPayerType();
		BlacklistSubscribers.clickSubmit();
		BlacklistSubscribers.enterMSISDN(msisdn);
		BlacklistSubscribers.clickSubmit1();
		BlacklistSubscribers.clickConfirm();
		String msg = BlacklistSubscribers.getMessage();

		return msg; 

	}





	public String UnblacklistSubscribers(String domain,String category,String usr){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_unBlackListLink();
		UnBlacklistedSubscribers.selectDomainCode(domain);
		UnBlacklistedSubscribers.selectCategory(category);
		UnBlacklistedSubscribers.enterUserName(usr);
		UnBlacklistedSubscribers.selectC2SPayeeType();
		UnBlacklistedSubscribers.selectP2PPayeeType();
		UnBlacklistedSubscribers.selectP2PPayerType();
		UnBlacklistedSubscribers.selectSubscriber();
		UnBlacklistedSubscribers.clickSubmit();
		UnBlacklistedSubscribers.clickConfirm();
		driver.switchTo().alert().accept();
		String msg = UnBlacklistedSubscribers.getMessage();

		return msg; 

	}


	public String UnblacklistSubscriberC2SPayee(String domain,String category,String usr,String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_unBlackListLink();
		UnBlacklistedSubscribers.selectDomainCode(domain);
		UnBlacklistedSubscribers.selectCategory(category);
		UnBlacklistedSubscribers.enterUserName(usr);
		UnBlacklistedSubscribers.selectC2SPayeeType();
		UnBlacklistedSubscribers.deselectP2PPayeeType();
		UnBlacklistedSubscribers.deselectP2PPayerType();
		UnBlacklistedSubscribers.selectSubscriberMultiple();
		UnBlacklistedSubscribers.clickSubmit();
		UnBlacklistedSubscribers.enterMSISDNtoBlacklist(msisdn);
		UnBlacklistedSubscribers.clickSubmitMultipleSub();
		UnBlacklistedSubscribers.selectMSISDN(msisdn);
		UnBlacklistedSubscribers.clickSubmitConfirm();
		UnBlacklistedSubscribers.clickConfirmUnBlack();

		String msg = UnBlacklistedSubscribers.getMessage();

		return msg; 

	}

	public String UnblacklistSubscriberP2PPayee(String domain,String category,String usr,String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_unBlackListLink();
		UnBlacklistedSubscribers.selectDomainCode(domain);
		UnBlacklistedSubscribers.selectCategory(category);
		UnBlacklistedSubscribers.enterUserName(usr);
		UnBlacklistedSubscribers.selectP2PPayeeType();
		UnBlacklistedSubscribers.selectSubscriberMultiple();
		UnBlacklistedSubscribers.clickSubmit();
		UnBlacklistedSubscribers.enterMSISDNtoBlacklist(msisdn);
		UnBlacklistedSubscribers.clickSubmitMultipleSub();
		UnBlacklistedSubscribers.selectMSISDN(msisdn);
		UnBlacklistedSubscribers.clickSubmitConfirm();
		UnBlacklistedSubscribers.clickConfirmUnBlack();
		String msg = UnBlacklistedSubscribers.getMessage();

		return msg; 

	}



	public String UnblacklistSubscriberP2PPayer(String domain,String category,String usr,String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_unBlackListLink();
		UnBlacklistedSubscribers.selectDomainCode(domain);
		UnBlacklistedSubscribers.selectCategory(category);
		UnBlacklistedSubscribers.enterUserName(usr);
		UnBlacklistedSubscribers.selectP2PPayerType();
		UnBlacklistedSubscribers.selectSubscriberMultiple();
		UnBlacklistedSubscribers.clickSubmit();
		UnBlacklistedSubscribers.enterMSISDNtoBlacklist(msisdn);
		UnBlacklistedSubscribers.clickSubmitMultipleSub();
		UnBlacklistedSubscribers.selectMSISDN(msisdn);
		UnBlacklistedSubscribers.clickSubmitConfirm();
		UnBlacklistedSubscribers.clickConfirmUnBlack();
		String msg = UnBlacklistedSubscribers.getMessage();

		return msg; 

	}




	public String viewRestrictedSubscribers(String domain,String category,String usr,String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_viewRestListLink();
		viewRestrictedList.selectDomainCode(domain);
		viewRestrictedList.selectCategory(category);
		viewRestrictedList.enterUserName(usr);
		viewRestrictedList.enterMSISDN(msisdn);
		String Date = homePage.getDate();
		viewRestrictedList.enterFromDate(Date);
		viewRestrictedList.enterToDate(Date);
		viewRestrictedList.clickSubmit();

		String msg = null;
		try{
			msg = viewRestrictedList.getMessage();
		}
		catch(Exception e){
			msg = viewRestrictedList.getErrorMessage();
		}

		return msg; 

	}

	public String viewRestrictedSubscribersNeg(String domain,String category,String usr,String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_viewRestListLink();
		viewRestrictedList.selectDomainCode(domain);
		viewRestrictedList.selectCategory(category);
		viewRestrictedList.enterUserName(usr);
		viewRestrictedList.enterMSISDN(msisdn);
		String Date = homePage.getDate();
		viewRestrictedList.enterFromDate(Date);
		viewRestrictedList.enterToDate(Date);
		viewRestrictedList.clickSubmit();

		String msg = null;

		msg = viewRestrictedList.getMsg();


		return msg; 

	}


	public String viewRestrictedSubscribersInvalidDate(String domain,String category,String usr,String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_viewRestListLink();
		viewRestrictedList.selectDomainCode(domain);
		viewRestrictedList.selectCategory(category);
		viewRestrictedList.enterUserName(usr);
		viewRestrictedList.enterMSISDN(msisdn);
		String Date = homePage.addDaysToCurrentDate(homePage.getDate(), 2);
		viewRestrictedList.enterFromDate(Date);
		viewRestrictedList.enterToDate(Date);
		viewRestrictedList.clickSubmit();

		String	msg = viewRestrictedList.getErrorMessage();


		return msg; 

	}



	public String uploadMSISDNListPathNotSelected( String domain, String category,String geography, String usr) throws IOException{

		userInfo= UserAccess.getUserWithAccess(RolesI.RESTRICTED_LIST_UPLOAD);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_UploadListLink();
		//UploadRestrictedListPage.selectGeoDomain(geography);
		UploadRestrictedListPage.selectDomainCode(domain);
		UploadRestrictedListPage.selectCategory(category);
		UploadRestrictedListPage.enterUserName(usr);
		UploadRestrictedListPage.selectSubType(PretupsI.PREPAID_LOOKUP);
		UploadRestrictedListPage.enterNoOfRecords("4");
		UploadRestrictedListPage.clickSubmit();
		String msg = UploadRestrictedListPage.getErrorMessage();


		return msg;

	}





	public String uploadMSISDNListInvalidSubType( Map<String,String> dataMap, String testCaseID,List<String> msisdnList,int noOfRecords) throws IOException, InterruptedException{

		userInfo= UserAccess.getUserWithAccess(RolesI.RESTRICTED_LIST_UPLOAD);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_UploadListLink();
		//UploadRestrictedListPage.selectGeoDomain(geography);
		UploadRestrictedListPage.selectDomainCode(dataMap.get("domainName"));
		UploadRestrictedListPage.selectCategory(dataMap.get("categoryName"));
		UploadRestrictedListPage.enterUserName(dataMap.get("username"));
		UploadRestrictedListPage.selectSubType(dataMap.get("subType"));
		UploadRestrictedListPage.uploadFile(msisdnList,testCaseID);
		String newNo = String.valueOf(noOfRecords);
		UploadRestrictedListPage.enterNoOfRecords(newNo);
		UploadRestrictedListPage.clickSubmit();
		uploadConfirmPage.clickConfirm();
		uploadConfirmPage.clickErrorLogLink();
		SwitchWindow.switchwindow(driver);
		String msg = uploadConfirmPage.getFailureReason();
		uploadConfirmPage.clickCloseLink();
		SwitchWindow.backwindow(driver);


		return msg;

	}



	public String noDownloadOption( Map<String,String> dataMap) throws IOException, InterruptedException{

		userInfo= UserAccess.getUserWithAccess(RolesI.RESTRICTED_LIST_UPLOAD);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		String msg = null;
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_UploadListLink();
		//UploadRestrictedListPage.selectGeoDomain(geography);
		UploadRestrictedListPage.selectDomainCode(dataMap.get("domainName"));
		UploadRestrictedListPage.selectCategory(dataMap.get("categoryName"));
		UploadRestrictedListPage.enterUserName(dataMap.get("username"));
		UploadRestrictedListPage.selectSubType(dataMap.get("subType"));
		try{

			UploadRestrictedListPage.downloadFile();
			msg = "Download Option found";
			Log.info("Download Option found");
		}
		catch(Exception e){
			msg = "Download Option not available on screen to download the file template";	
			Log.info("Download Option not available on screen to download the file template");	
		}

		return msg;

	}




	public String uploadMSISDNListMandatoryFieldValidation( Map<String,String> dataMap, String testCaseID,List<String> msisdnList,int noOfRecords, boolean i) throws IOException{

		userInfo= UserAccess.getUserWithAccess(RolesI.RESTRICTED_LIST_UPLOAD);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();
		restListMngmtSubCategoriesPage.click_UploadListLink();
		//UploadRestrictedListPage.selectGeoDomain(geography);
		try{
			UploadRestrictedListPage.selectDomainCode(dataMap.get("domainName"));
			UploadRestrictedListPage.selectCategory(dataMap.get("categoryName"));
		}
		catch(Exception e){
			Log.info("Issue while selecting domain name");
		}

		UploadRestrictedListPage.enterUserName(dataMap.get("username"));
		try{
			UploadRestrictedListPage.selectSubType(dataMap.get("subType"));
		}
		catch(Exception e){
			Log.info("Issue while selecting sub type");
		}
		UploadRestrictedListPage.uploadFile(msisdnList,testCaseID);
		String newNo = String.valueOf(noOfRecords);
		if (i==false){
			UploadRestrictedListPage.enterNoOfRecords(newNo);
		}
		else
		{
			UploadRestrictedListPage.enterNoOfRecords("");	
		}
		UploadRestrictedListPage.clickSubmit();

		String msg = UploadRestrictedListPage.getErrorMessage();
		Log.info("There is Error Message on screen,Error Message fetched as : " +msg);



		return msg;

	}	



	
	public String blacklistSubscriberMultipleUploadList(String domain,String category,String usr, String Selecttype, String deselectType1,String deselectType2,int noOfRecords, String testCaseID, List<String> msisdnList) throws IOException{

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_blackSubsLink();
		BlacklistSubscribers.selectDomainCode(domain);
		BlacklistSubscribers.selectCategory(category);
		BlacklistSubscribers.enterUserName(usr);
		BlacklistSubscribers.selectType(Selecttype);
		BlacklistSubscribers.deselectType(deselectType1);
		BlacklistSubscribers.deselectType(deselectType2);
		BlacklistSubscribers.selectSubscriberMultiple();
		BlacklistSubscribers.clickSubmit();
		String newNo = String.valueOf(noOfRecords);
		BlacklistSubscribers.enterNoOfRecords(newNo);
		BlacklistSubscribers.uploadFile(msisdnList,testCaseID);
		BlacklistSubscribers.clickConfirmUpload();
		BlacklistSubscribers.clickSubmitUpload();
		String msg = BlacklistSubscribers.getMessage();

		return msg; 

	}
	
	
	
	public String UnblacklistSubscriberMultiple(String domain,String category,String usr, String Selecttype, String deselectType1,String deselectType2,String msisdn){

		userInfo= UserAccess.getUserWithAccess(RolesI.DELETE_RESTRICTED_MSISDN);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickRestListMgmt();	
		restListMngmtSubCategoriesPage.click_unBlackListLink();
		UnBlacklistedSubscribers.selectDomainCode(domain);
		UnBlacklistedSubscribers.selectCategory(category);
		UnBlacklistedSubscribers.enterUserName(usr);
		UnBlacklistedSubscribers.selectType(Selecttype);
		UnBlacklistedSubscribers.deselectType(deselectType1);
		UnBlacklistedSubscribers.deselectType(deselectType2);
		UnBlacklistedSubscribers.selectSubscriberMultiple();
		UnBlacklistedSubscribers.clickSubmit();
		UnBlacklistedSubscribers.enterMSISDNtoBlacklist(msisdn);
		UnBlacklistedSubscribers.clickSubmitMultipleSub();
		UnBlacklistedSubscribers.selectApproveAll();
		UnBlacklistedSubscribers.clickSubmitConfirm();
		UnBlacklistedSubscribers.clickConfirmUnBlack();
		String msg = UnBlacklistedSubscribers.getMessage();

		return msg; 

	}
	
	





}
