package com.testscripts.sit;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransfer;
import com.Features.Map_CommissionProfile;
import com.Features.RestrictedListMgmt;
import com.apicontrollers.extgw.CreditTransfer.EXTGW_PRC_API;
import com.apicontrollers.extgw.CreditTransfer.PRC_DP;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class SIT_RestrictedListManagement extends BaseTest {


	static boolean TestCaseCounter = false;
	String CommProfile;
	String profileName;
	//Map<String,String> dataMap;
	Map_CommissionProfile Map_CommProfile;
	HashMap<String, String> channelMap=new HashMap<>();
	String assignCategory="SIT";
	static String moduleCode;
	List<String> msisdnList;
	List<String> blacklistMSISDNList;
	List<String> newList;
	


	@DataProvider(name = "categoryData1")
	public Object[][] TestDataFeed() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		Object[][] categoryData = new Object[1][4];
		categoryData[0][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
		categoryData[0][3] = ExcelUtility.getCellData(0, ExcelI.USER_NAME,1);

		return categoryData;
	}

	@Test(dataProvider = "categoryData1")
	public void a_uploadList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST1").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST1").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST1";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");


		String actual = RestrictedListMgmt.uploadMSISDNList(dataMap,testCaseID,msisdnList,noOfMsisdns);

		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.seluserforbulkreg.message.success");

		Validator.messageCompare(actual, expected);

	}




	@Test(dataProvider = "categoryData1")
	public void b_RestrictedListApproveMSISDN(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST2").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST2").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.approveSingleMSISDNList(domainName,categoryName,geography, username,msisdnList.get(1));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.success");

		Validator.messageCompare(actual, expected);

	}


	@Test(dataProvider = "categoryData1")
	public void c_RestrictedListRejectSingleMSISDN(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST3").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST3").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.RejectSingleMSISDNList(domainName,categoryName,geography, username,msisdnList.get(2));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.success");

		Validator.messageCompare(actual, expected);

	}


	@Test(dataProvider = "categoryData1")
	public void d_RestrictedListDiscardSingleMSISDN(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST4").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST4").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.DiscardSingleMSISDNList(domainName,categoryName,geography, username,msisdnList.get(3));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.fail");

		Validator.messageCompare(actual, expected);

	}



	@Test(dataProvider = "categoryData1")
	public void d_rejectRestrictedList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST5").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST5").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.rejectMSISDNList(domainName, categoryName, geography, username);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.success");

		Validator.messageCompare(actual, expected);

	}



	@Test(dataProvider = "categoryData1")
	public void e_discardRestrictedList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST6").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST6").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST6";
		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");


		String actual0 = RestrictedListMgmt.uploadMSISDNList(dataMap,testCaseID,msisdnList,noOfMsisdns);


		String actual = RestrictedListMgmt.discardMSISDNList(geography, domainName, categoryName, username);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.fail");

		Validator.messageCompare(actual, expected);

	}

	@Test(dataProvider = "categoryData1")
	public void f_RestrictedListApproval(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST7").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String testCaseID = "SITRESTLIST7";
		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");


		String actual0 = RestrictedListMgmt.uploadMSISDNList(dataMap,testCaseID,msisdnList,noOfMsisdns);

		String actual = RestrictedListMgmt.approveMSISDNList(domainName,categoryName,geography, username);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.success");

		Validator.messageCompare(actual, expected);

	}

	@Test(dataProvider = "categoryData1")
	public void g_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST8").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST8").getExtentCase());
		currentNode.assignCategory(assignCategory);


		String actual = RestrictedListMgmt.blacklistSubscriberP2PPayer(domainName, categoryName, username,msisdnList.get(1));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(1),username);

		Validator.messageCompare(actual, expected);

	}




	@Test(dataProvider = "categoryData1")
	public void h_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST9").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST9").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.blacklistSubscriberP2PPayee(domainName, categoryName, username,msisdnList.get(2));
		//String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(2),username);

		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(2),username);
		Validator.messageCompare(actual, expected);

	}


	@Test(dataProvider = "categoryData1")
	public void i_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST10").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST10").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.blacklistSubscriberC2SPayee(domainName, categoryName, username,msisdnList.get(3));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(3),username);

		Validator.messageCompare(actual, expected);

	}




	@Test(dataProvider = "categoryData1")
	public void j_unblacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST11").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST11").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.UnblacklistSubscribers(domainName, categoryName, username);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.unblacklistallsubs.message.success",username);

		Validator.messageCompare(actual, expected);

	}


	@Test(dataProvider = "categoryData1")
	public void k_deleteSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST12").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST12").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.deleteRestrictedMSISDN(domainName, categoryName, username,msisdnList.get(2));
		String expected = MessagesDAO.prepareMessageByKey("delete.mapping.activation.profile.msg.subdeleteall");

		Validator.messageCompare(actual, expected);

	}





	@Test(dataProvider = "categoryData1")
	public void l_viewSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST13").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String actual = RestrictedListMgmt.viewRestrictedSubscribers(domainName, categoryName, username,msisdnList.get(3));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.viewrestrictedsubsdetails.label.heading");

		Validator.messageCompare(actual, expected);

	}



	//Negative TestCases


	@Test(dataProvider = "categoryData1")
	public void m_neg_RestrictedListUpload(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST14").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		String testCaseID = "SITRESTLIST14";
		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");

		int newNoOfRecords = noOfMsisdns-1;

		String actual = RestrictedListMgmt.uploadMSISDNList(dataMap,testCaseID,msisdnList,newNoOfRecords);


		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.seluserforbulkreg.error.invalidsize");

		Validator.messageCompare(actual, expected);

	}

	@Test(dataProvider = "categoryData1")
	public void n_neg_RestrictedListUpload(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST15").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST15").getExtentCase());
		currentNode.assignCategory(assignCategory);


		String actual = RestrictedListMgmt.uploadMSISDNListPathNotSelected(domainName,categoryName,geography,username);

		String expected = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("restrictedsubs.seluserforbulkreg.label.uploadedfile"));
		
		Validator.messageCompare(actual, expected);

	}






	@Test(dataProvider = "categoryData1")
	public void o_neg_RestrictedListUpload(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST16").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST16").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST16";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.POSTPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");

		String actual = RestrictedListMgmt.uploadMSISDNListInvalidSubType(dataMap,testCaseID,msisdnList,noOfMsisdns);

		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.seluserforbulkreg.error.prefixnotmatch");

		Validator.messageCompare(actual, expected);

	}



	@Test(dataProvider = "categoryData1")
	public void p_neg_RestrictedListUpload(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST17").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST17").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST17";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");

		String actual = RestrictedListMgmt.uploadMSISDNList(dataMap,testCaseID,msisdnList,noOfMsisdns);

		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.seluserforbulkreg.error.nostart");

		Validator.messageCompare(actual, expected);

	}	




	@Test(dataProvider = "categoryData1")
	public void q_neg_RestrictedListUpload(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST18").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST18").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST18";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");

		String actual = RestrictedListMgmt.uploadMSISDNList(dataMap,testCaseID,msisdnList,noOfMsisdns);

		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklisting.error.noend");

		Validator.messageCompare(actual, expected);

	}	



	@Test(dataProvider = "categoryData1")
	public void r_neg_RestrictedListUpload(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST19").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST19").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST19";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"d$$$","%%77");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");

		String actual = RestrictedListMgmt.uploadMSISDNList(dataMap,testCaseID,msisdnList,noOfMsisdns);

		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.seluserforbulkreg.error.nostart");

		Validator.messageCompare(actual, expected);

	}	



	@Test(dataProvider = "categoryData1")
	public void s_neg_RestrictedListUpload(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST20").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST20").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST20";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"d$$$","%%77");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");

		String actual = RestrictedListMgmt.uploadMSISDNListdiffFormat(domainName,categoryName,geography,username,testCaseID,msisdnList,noOfMsisdns);

		String expected = MessagesDAO.prepareMessageByKey("batch.error.invalidfileformat");

		Validator.messageCompare(actual, expected);

	}	




	@Test(dataProvider = "categoryData1")
	public void t_neg_RestrictedListUpload(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST21").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST21").getExtentCase());
		currentNode.assignCategory(assignCategory);


		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);


		String actual = RestrictedListMgmt.noDownloadOption(dataMap);

		String expected = "Download Option not available on screen to download the file template";

		Validator.messageCompare(actual, expected);

	}


	@Test(dataProvider = "categoryData1")
	public void u_neg_RestrictedListUpload(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST22").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		String Transfer_ID = null;

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST22").getExtentCase());
		currentNode.assignCategory(assignCategory);


		HashMap<String,String> dataMap = new HashMap<String, String>();
		String testCaseID = "SITRESTLIST22";

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");

		String actual = RestrictedListMgmt.uploadMSISDNList(dataMap, testCaseID, msisdnList, noOfMsisdns);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.seluserforbulkreg.message.success");
		if (actual.equals(expected)){
			ExtentI.Markup(ExtentColor.GREEN, "List of MSISDNS to be restricted are uploaded successfully");
		}else {
			Log.info("Issue while uploading list");
		}

		String actual1 = RestrictedListMgmt.approveSingleMSISDNList(domainName, categoryName, geography, username, msisdnList.get(1));

		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.success");

		if (actual1.equalsIgnoreCase(expected1)){

			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is approved successfully");
		}else {
			Log.info("Issue while approving MSISDN");
		}

		String MSISDNStatus = DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(1));
		String MSISDNBlackListStatus = DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(1));

		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Status is : " +MSISDNStatus);
		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Blacklist Status is : " +MSISDNBlackListStatus);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",msisdnList.get(1));
		}
		catch(Exception e){
			String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
			ExtentI.Markup(ExtentColor.RED, "C2S Transfer is not successful with  error message" + actualMessage);
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatus(Transfer_ID);
		 
		String expected2 = "200";
		Validator.messageCompare(trf_status, expected2);

		
	}
	

	

	
	
	@Test(dataProvider = "categoryData1")
	public void v_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST23").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		String Transfer_ID = null;
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST23").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.blacklistSubscriberC2SPayee(domainName, categoryName, username,msisdnList.get(1));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(1),username);

		if (actual.equalsIgnoreCase(expected)){

			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is blacklisted successfully");
		}else {
			Log.info("Issue while approving MSISDN");
		}

		String MSISDNStatus = DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(1));
		String MSISDNBlackListStatus = DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(1));

		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Status is : " +MSISDNStatus);
		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Blacklist Status is : " +MSISDNBlackListStatus);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",msisdnList.get(1));
		}
		catch(Exception e){
			String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
			ExtentI.Markup(ExtentColor.RED, "C2S Transfer is not successful with  error message" + actualMessage);
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatus(Transfer_ID);
		 
		String expected2 = "";
		Validator.messageCompare(trf_status, expected2);
		
		

	}
	

	
	
	@Test(dataProvider = "categoryData1")
	public void w_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST24").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
		HashMap<String, String> dataMap = PRC_DP.getAPIdata();
		String Transfer_ID = null;
		String TXNStatus = null;
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST24").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MSISDNStatus = DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(1));
		String MSISDNBlackListStatus = DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(1));

		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Status is : " +MSISDNStatus);
		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Blacklist Status is : " +MSISDNBlackListStatus);
		
		if(MSISDNBlackListStatus.equalsIgnoreCase("N")){

		String actual = RestrictedListMgmt.blacklistSubscriberC2SPayee(domainName, categoryName, username,msisdnList.get(1));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(1),username);

		if (actual.equalsIgnoreCase(expected)){

			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is blacklisted successfully");
		}else {
			Log.info("Issue while approving MSISDN");
		}

		} else{
			Log.info("The Subcriber is already Blacklisted");
		}

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			
			dataMap.put(CreditTransAPI.MSISDN2,msisdnList.get(1) );
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			//_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			TXNStatus = xmlPath.get(CreditTransAPI.TXNSTATUS);
			Transfer_ID = xmlPath.get(CreditTransAPI.TXNID);
					}
		catch(Exception e){
		
			ExtentI.Markup(ExtentColor.RED, "Issue while executing Credit Transfer API");
			ExtentI.attachCatalinaLogs();
			

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatusforP2P(Transfer_ID);
		 
		String expected2 = "200";
		Validator.messageCompare(trf_status, expected2);
		
		

	}
	

	@Test(dataProvider = "categoryData1")
	public void x_unblacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST25").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		String Transfer_ID = null;
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST25").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MSISDNStatus = DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(1));
		String MSISDNBlackListStatus = DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(1));

		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Status is : " +MSISDNStatus);
		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Blacklist Status is : " +MSISDNBlackListStatus);
		
		if(MSISDNBlackListStatus.equalsIgnoreCase("Y")){

		String actual = RestrictedListMgmt.UnblacklistSubscriberC2SPayee(domainName, categoryName, username,msisdnList.get(1));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(1),username);

		if (actual.equalsIgnoreCase(expected)){

			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is unblacklisted successfully");
		}else {
			Log.info("Issue while unBlacklisting MSISDN");
		}
		}
		else{
			Log.info("Subscriber is already Unblacklisted");
		}

		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",msisdnList.get(1));
		}
		catch(Exception e){
			String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
			ExtentI.Markup(ExtentColor.RED, "C2S Transfer is not successful with  error message" + actualMessage);
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatus(Transfer_ID);
		 
		String expected2 = "200";
		Validator.messageCompare(trf_status, expected2);
		
		

	}

	

	
	@Test(dataProvider = "categoryData1")
	public void y_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST26").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
		HashMap<String, String> dataMap = PRC_DP.getAPIdata();
		String Transfer_ID = null;
		String TXNStatus = null;
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST26").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.blacklistSubscriberP2PPayee(domainName, categoryName, username,msisdnList.get(3));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(3),username);

		if (actual.equalsIgnoreCase(expected)){

			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is blacklisted successfully");
		}else {
			Log.info("Issue while approving MSISDN");
		}

		String MSISDNStatus = DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(3));
		String MSISDNBlackListStatus = DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(3));

		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Status is : " +MSISDNStatus);
		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Blacklist Status is : " +MSISDNBlackListStatus);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			
			dataMap.put(CreditTransAPI.MSISDN2,msisdnList.get(3) );
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			//_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			TXNStatus = xmlPath.get(CreditTransAPI.TXNSTATUS);
			Transfer_ID = xmlPath.get(CreditTransAPI.TXNID);
					}
		catch(Exception e){
		
			ExtentI.Markup(ExtentColor.RED, "Issue while executing Credit Transfer API");
			ExtentI.attachCatalinaLogs();
			

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatusforP2P(Transfer_ID);
		 
		String expected2 = "";
		Validator.messageCompare(trf_status, expected2);
		
		

	}


	
	
	@Test(dataProvider = "categoryData1")
	public void z00_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST27").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
		HashMap<String, String> dataMap = PRC_DP.getAPIdata();
		String Transfer_ID = null;
		String TXNStatus = null;
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST27").getExtentCase());
		currentNode.assignCategory(assignCategory);



		
		String MSISDNStatus = DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(3));
		String MSISDNBlackListStatus = DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(3));

		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Status is : " +MSISDNStatus);
		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Blacklist Status is : " +MSISDNBlackListStatus);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			
			dataMap.put(CreditTransAPI.MSISDN1,msisdnList.get(3) );
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			//_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			TXNStatus = xmlPath.get(CreditTransAPI.TXNSTATUS);
			Transfer_ID = xmlPath.get(CreditTransAPI.TXNID);
					}
		catch(Exception e){
		
			ExtentI.Markup(ExtentColor.RED, "Issue while executing Credit Transfer API");
			ExtentI.attachCatalinaLogs();
			

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatusforP2P(Transfer_ID);
		 
		String expected2 = "200";
		Validator.messageCompare(trf_status, expected2);
		
		

	}
	
	
	
	@Test(dataProvider = "categoryData1")
	public void z01_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST28").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		String Transfer_ID = null;
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST28").getExtentCase());
		currentNode.assignCategory(assignCategory);
		

		String MSISDNStatus = DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(3));
		String MSISDNBlackListStatus = DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(3));

		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Status is : " +MSISDNStatus);
		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Blacklist Status is : " +MSISDNBlackListStatus);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",msisdnList.get(3));
		}
		catch(Exception e){
			String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
			ExtentI.Markup(ExtentColor.RED, "C2S Transfer is not successful with  error message" + actualMessage);
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatus(Transfer_ID);
		 
		String expected2 = "200";
		Validator.messageCompare(trf_status, expected2);
		
		

	}
	

	
	
	
	
	
	
	
	
	
	@Test(dataProvider = "categoryData1")
	public void z02_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST29").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
		HashMap<String, String> dataMap = PRC_DP.getAPIdata();
		String Transfer_ID = null;
		String TXNStatus = null;
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST29").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.blacklistSubscriberP2PPayer(domainName, categoryName, username,msisdnList.get(4));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(4),username);

		if (actual.equalsIgnoreCase(expected)){

			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is blacklisted successfully");
		}else {
			Log.info("Issue while approving MSISDN");
		}

		String MSISDNStatus = DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(4));
		String MSISDNBlackListStatus = DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(4));

		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Status is : " +MSISDNStatus);
		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Blacklist Status is : " +MSISDNBlackListStatus);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			
			dataMap.put(CreditTransAPI.MSISDN1,msisdnList.get(4) );
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			//_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			TXNStatus = xmlPath.get(CreditTransAPI.TXNSTATUS);
			Transfer_ID = xmlPath.get(CreditTransAPI.TXNID);
					}
		catch(Exception e){
		
			ExtentI.Markup(ExtentColor.RED, "Issue while executing Credit Transfer API");
			ExtentI.attachCatalinaLogs();
			

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatusforP2P(Transfer_ID);
		 
		String expected2 = "";
		Validator.messageCompare(trf_status, expected2);
		
		

	}

	
	
	
	@Test(dataProvider = "categoryData1")
	public void z03_unblacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST30").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
		HashMap<String, String> dataMap = PRC_DP.getAPIdata();
		String Transfer_ID = null;
		String TXNStatus = null;
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST31").getExtentCase());
		currentNode.assignCategory(assignCategory);

		

		
		String actual = RestrictedListMgmt.UnblacklistSubscriberP2PPayer(domainName, categoryName, username,msisdnList.get(4));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(4),username);

		if (actual.equalsIgnoreCase(expected)){

			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is blacklisted successfully");
		}else {
			Log.info("Issue while approving MSISDN");
		}

		

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			
			dataMap.put(CreditTransAPI.MSISDN1,msisdnList.get(4) );
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			//_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			TXNStatus = xmlPath.get(CreditTransAPI.TXNSTATUS);
			Transfer_ID = xmlPath.get(CreditTransAPI.TXNID);
					}
		catch(Exception e){
		
			ExtentI.Markup(ExtentColor.RED, "Issue while executing Credit Transfer API");
			ExtentI.attachCatalinaLogs();
			

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatusforP2P(Transfer_ID);
		 
		String expected2 = "200";
		Validator.messageCompare(trf_status, expected2);
		
		

	}
	
	
	
	@Test(dataProvider = "categoryData1")
	public void z04_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST31").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
		HashMap<String, String> dataMap = PRC_DP.getAPIdata();
		String Transfer_ID = null;
		String TXNStatus = null;
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST31").getExtentCase());
		currentNode.assignCategory(assignCategory);



		String actual = RestrictedListMgmt.UnblacklistSubscriberP2PPayee(domainName, categoryName, username,msisdnList.get(3));
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.unblacklistsinglesubs.message.success",msisdnList.get(1),username);

		if (actual.equalsIgnoreCase(expected)){

			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is unblacklisted successfully");
		}else {
			Log.info("Issue while approving MSISDN");
		}

		String MSISDNStatus = DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(3));
		String MSISDNBlackListStatus = DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(3));

		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Status is : " +MSISDNStatus);
		ExtentI.Markup(ExtentColor.TEAL, "The Subscriber Blacklist Status is : " +MSISDNBlackListStatus);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);



		try{
			
			dataMap.put(CreditTransAPI.MSISDN2,msisdnList.get(3) );
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			//_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			TXNStatus = xmlPath.get(CreditTransAPI.TXNSTATUS);
			Transfer_ID = xmlPath.get(CreditTransAPI.TXNID);
					}
		catch(Exception e){
		
			ExtentI.Markup(ExtentColor.RED, "Issue while executing Credit Transfer API");
			ExtentI.attachCatalinaLogs();
			

		}
		String trf_status = DBHandler.AccessHandler.fetchTransferStatusforP2P(Transfer_ID);
		 
		String expected2 = "200";
		Validator.messageCompare(trf_status, expected2);
		
		

	}
	
	

	@Test(dataProvider = "categoryData1")
	public void z08_blacklistSubscriber(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST35").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST35").getExtentCase());
		currentNode.assignCategory(assignCategory);


		String actual = RestrictedListMgmt.blacklistSubscribersAll(domainName, categoryName, username);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistallsubs.message.success",username);

		Validator.messageCompare(actual, expected);

	}

	
	
	@Test(dataProvider = "categoryData1")
	public void z09_viewSubscriberNeg(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST36").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST36").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String msisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
		

		String actual = RestrictedListMgmt.viewRestrictedSubscribersNeg(domainName, categoryName, username,msisdn);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.error.msg.subscriber.notfound");

		Validator.messageCompare(actual, expected);

	}
	

	
	
	@Test(dataProvider = "categoryData1")
	public void z10_viewSubscriberNeg(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST37").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);
		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST37").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		
		
		
		String actual = RestrictedListMgmt.viewRestrictedSubscribersInvalidDate(domainName, categoryName, username,msisdnList.get(2));
		String expected = MessagesDAO.prepareMessageByKey("btsl.error.msg.fromdatebeforecurrentdate");

		Validator.messageCompare(actual, expected);

	}
	

	
	@Test(dataProvider = "categoryData1")
	public void z11_deleteSubscriberNeg(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST41").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST41").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String msisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
		

		String actual = RestrictedListMgmt.deleteRestrictedMSISDNeg(domainName, categoryName, username,msisdn);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.sublistingfordelete.msg.msisdnnotfound");

		Validator.messageCompare(actual, expected);

	}	
	
	
	@Test(dataProvider = "categoryData1")
	public void z12_RestrictedListApproveMSISDN(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST42").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST42").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST42";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");


		String actual = RestrictedListMgmt.uploadMSISDNList(dataMap,testCaseID,msisdnList,noOfMsisdns);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.seluserforbulkreg.message.success");
		if (actual.equals(expected)){
			ExtentI.Markup(ExtentColor.GREEN, "List of MSISDNS to be restricted are uploaded successfully");
		}else {
			Log.info("Issue while uploading list");
		}

		
		
		String actual1 = RestrictedListMgmt.approveRestListMultipleOperation(domainName,categoryName,geography, username,msisdnList.get(1),msisdnList.get(2),msisdnList.get(3));
		String MSISDNStatus1= DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(1));
		if (MSISDNStatus1.equals("A")){
			ExtentI.Markup(ExtentColor.GREEN, "The Status of MSISDN1 is Approved ");
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "The Status of MSISDN1 is not Approved ");
		}
		
		String MSISDNStatus2= DBHandler.AccessHandler.checkForSubscriberExistence(msisdnList.get(2));
		if (MSISDNStatus2.equals("N")){
			ExtentI.Markup(ExtentColor.GREEN, "The Status of MSISDN1 is Rejected ");
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "The Status of MSISDN1 is not Rejected ");
		}
		
		String MSISDNStatus3= DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(3));
		if (MSISDNStatus3.equals("W")){
			ExtentI.Markup(ExtentColor.GREEN, "The Status of MSISDN1 is Discarded ");
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "The Status of MSISDN1 is not Discarded ");
		}
		
		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.success");

		Validator.messageCompare(actual1, expected1);

	}
	
	
	@Test(dataProvider = "categoryData1")
	public void z13_deleteSubscriberNeg(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST43").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST43").getExtentCase());
		currentNode.assignCategory(assignCategory);

			

		String actual = RestrictedListMgmt.deleteAllRestrictedMSISDN(domainName, categoryName, username);
		String expected = MessagesDAO.prepareMessageByKey("delete.mapping.activation.profile.msg.subdeleteall");

		Validator.messageCompare(actual, expected);

	}
	
	
	@Test(dataProvider = "categoryData1")
	public void z14_RestrictedListApproveMSISDNStatusValidation(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST44").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST44").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST44";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");


		String actual = RestrictedListMgmt.uploadMSISDNList(dataMap,testCaseID,msisdnList,noOfMsisdns);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.seluserforbulkreg.message.success");
		if (actual.equals(expected)){
			ExtentI.Markup(ExtentColor.GREEN, "List of MSISDNS to be restricted are uploaded successfully");
		}else {
			Log.info("Issue while uploading list");
		}

		
		
		String actual1 = RestrictedListMgmt.approveSingleMSISDNList(domainName,categoryName,geography, username,msisdnList.get(1));
		String MSISDNStatus1= DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(1));
		if (MSISDNStatus1.equals("A")){
			ExtentI.Markup(ExtentColor.GREEN, "The Status of MSISDN1 is Approved ");
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "The Status of MSISDN1 is not Approved ");
		}
			
		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.success");
		
		if(actual1.equalsIgnoreCase(expected1)){
			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is Approved successfully");
		}
		else{
			currentNode.fail("Issue while approving MSISDN");	
		}

		Validator.messageCompare(MSISDNStatus1, "A");

	}
	
	
	
	
	@Test(dataProvider = "categoryData1")
	public void z15_RestrictedListRejectMSISDNStatusValidation(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST45").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST45").getExtentCase());
		currentNode.assignCategory(assignCategory);

				
		
		String actual1 = RestrictedListMgmt.RejectSingleMSISDNList(domainName,categoryName,geography, username,msisdnList.get(2));
		String MSISDNStatus1= DBHandler.AccessHandler.checkForSubscriberExistence(msisdnList.get(2));
		if (MSISDNStatus1.equals("N")){
			ExtentI.Markup(ExtentColor.GREEN, "The Status of MSISDN1 is Rejected ");
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "The Status of MSISDN1 is not Rejected ");
		}
			
		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.success");
		
		if(actual1.equalsIgnoreCase(expected1)){
			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is rejected successfully");
		}
		else{
			currentNode.fail("Issue while rejecting MSISDN");	
		}

		Validator.messageCompare(MSISDNStatus1, "N");

	}

	
	
	@Test(dataProvider = "categoryData1")
	public void z16_RestrictedListDiscardMSISDNStatusValidation(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST46").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST46").getExtentCase());
		currentNode.assignCategory(assignCategory);

				
		
		String actual1 = RestrictedListMgmt.DiscardSingleMSISDNList(domainName,categoryName,geography, username,msisdnList.get(3));
		String MSISDNStatus1= DBHandler.AccessHandler.SubscriberStatus(msisdnList.get(3));
		if (MSISDNStatus1.equals("W")){
			ExtentI.Markup(ExtentColor.GREEN, "The Status of MSISDN1 is discarded ");
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "The Status of MSISDN1 is not discarded ");
		}
			
		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.fail");
		
		if(actual1.equalsIgnoreCase(expected1)){
			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is discared successfully");
		}
		else{
			currentNode.fail("Issue while discarding MSISDN");	
		}

		Validator.messageCompare(MSISDNStatus1, "W");

	}
	


	
	
	@Test(dataProvider = "categoryData1")
	public void z17_RestrictedListBlacklistMSISDNStatusValidation(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST47").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST47").getExtentCase());
		currentNode.assignCategory(assignCategory);

				
		
		String actual1 = RestrictedListMgmt.blacklistSubscriberC2SPayee(domainName,categoryName,username,msisdnList.get(1));
		String MSISDNStatus1= DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(1));
		if (MSISDNStatus1.equals("Y")){
			ExtentI.Markup(ExtentColor.GREEN, "The Status of MSISDN1 is Blacklisted ");
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "The Status of MSISDN1 is not Blacklisted ");
		}
			
		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(1),username);
		
		if(actual1.equalsIgnoreCase(expected1)){
			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is blacklisted successfully");
		}
		else{
			currentNode.fail("Issue while Blacklisting MSISDN");	
		}

		Validator.messageCompare(MSISDNStatus1, "Y");

	}
	

	
	
	@Test(dataProvider = "categoryData1")
	public void z18_RestrictedListUnBlacklistMSISDNStatusValidation(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST48").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST48").getExtentCase());
		currentNode.assignCategory(assignCategory);

				
		
		String actual1 = RestrictedListMgmt.UnblacklistSubscriberC2SPayee(domainName,categoryName,username,msisdnList.get(1));
		String MSISDNStatus1= DBHandler.AccessHandler.SubscriberBlacklistStatus(msisdnList.get(1));
		if (MSISDNStatus1.equals("N")){
			ExtentI.Markup(ExtentColor.GREEN, "The Status of MSISDN1 is UnBlacklisted ");
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "The Status of MSISDN1 is not UnBlacklisted ");
		}
			
		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklistsinglesubs.message.success",msisdnList.get(1),username);
		
		if(actual1.equalsIgnoreCase(expected1)){
			ExtentI.Markup(ExtentColor.GREEN, "MSISDN is unblacklisted successfully");
		}
		else{
			currentNode.fail("Issue while UnBlacklisting MSISDN");	
		}

		Validator.messageCompare(MSISDNStatus1, "N");

	}
	
	
	
	
	
	
	
	

	
	
	
	
	
	//MandatoryFieldValidations
	
	
	
	
	@Test(dataProvider = "categoryData1")
	public void z19_M1_uploadList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST49").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST49").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST50";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName","");
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");


		String actual = RestrictedListMgmt.uploadMSISDNListMandatoryFieldValidation(dataMap,testCaseID,msisdnList,noOfMsisdns,false);

		String expected = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("restrictedsubs.singlesubsblacklist.label.domain")); 

		Validator.messageCompare(actual, expected);

	}
	
	@Test(dataProvider = "categoryData1")
	public void z20_M2_uploadList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST50").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST50").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST50";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username","");
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");


		String actual = RestrictedListMgmt.uploadMSISDNListMandatoryFieldValidation(dataMap,testCaseID,msisdnList,noOfMsisdns,false);

		String expected = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("restrictedsubs.blacklistallsubs.label.username")); 

		Validator.messageCompare(actual, expected);

	}


	
	@Test(dataProvider = "categoryData1")
	public void z21_M3_uploadList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST51").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST51").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST50";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType","");

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		int size = msisdnList.size();
		System.out.println("No.Of Records: + size");


		String actual = RestrictedListMgmt.uploadMSISDNListMandatoryFieldValidation(dataMap,testCaseID,msisdnList,noOfMsisdns,false);

		String expected = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("iatrestrictedsubs.associatesubscriberdetails.label.subscribertype"));

		Validator.messageCompare(actual, expected);

	}
	

	@Test(dataProvider = "categoryData1")
	public void z22_M4_uploadList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST52").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST52").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST52";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");

		String actual = RestrictedListMgmt.uploadMSISDNListMandatoryFieldValidation(dataMap,testCaseID,msisdnList,noOfMsisdns,true);
		
		String expected = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("restrictedsubs.associatesubscriberdetails.label.numberofrecord"));
		Validator.messageCompare(actual, expected);

	}
	
	
	

	
/*
 * BlacklistListValidations	
 */
	
	
	@Test(dataProvider = "categoryData1")
	public void z23_uploadblacklistMSISDNList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST53").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST53").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String testCaseID = "SITRESTLIST53";

		HashMap<String,String> dataMap = new HashMap<String, String>();		

		dataMap.put("geography", geography);
		dataMap.put("domainName",domainName);
		dataMap.put("categoryName",categoryName);
		dataMap.put("username",username);
		dataMap.put("subType",PretupsI.PREPAID_LOOKUP);

		int  noOfMsisdns = 5;
		msisdnList= RestrictedListMgmt.prepareRestrictedList(5,"[START]","[END]");
		
		

		String actual = RestrictedListMgmt.uploadMSISDNList(dataMap, testCaseID, msisdnList, noOfMsisdns);
		String expected = MessagesDAO.prepareMessageByKey("restrictedsubs.seluserforbulkreg.message.success");
		if (actual.equals(expected)){
			ExtentI.Markup(ExtentColor.GREEN, "List of MSISDNS to be restricted are uploaded successfully");
		}else {
			Log.info("Issue while uploading list");
		}

		String actual1 = RestrictedListMgmt.approveMSISDNList(domainName, categoryName, geography, username);

		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.confirmsubsapproval.success");

		if (actual1.equalsIgnoreCase(expected1)){

			ExtentI.Markup(ExtentColor.GREEN, "MSISDNS are approved successfully");
		}else {
			Log.info("Issue while approving MSISDNS");
		}

		
		
		int noOfRecords = 3;
		newList = RestrictedListMgmt.prepareBlacklistMSISDNList(msisdnList, noOfRecords, "[START]", "[END]");
		
		String actual2 = RestrictedListMgmt.blacklistSubscriberMultipleUploadList(domainName, categoryName,username, "cp2pPayer", "cp2pPayee", "c2sPayee", noOfRecords, testCaseID, newList);
		
		String expected2 = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklisting.message.success");

		Validator.messageCompare(actual2, expected2);

	}
	
	
	@Test(dataProvider = "categoryData1")
	public void z24_RestrictedListUnBlacklistMultipleMSISDNS(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST54").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST54").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String multipleMSISDNS = newList.get(1) + "," + newList.get(2);
		
				
		String actual1 = RestrictedListMgmt.UnblacklistSubscriberMultiple(domainName,categoryName,username,"cp2pPayer", "cp2pPayee", "c2sPayee",multipleMSISDNS);
			
		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.unblacklistselectedsubs.message.success",username);

		Validator.messageCompare(actual1, expected1);

	}
	
	
	
	

	
	
	@Test(dataProvider = "categoryData1")
	public void z25_uploadblacklistMSISDNList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST55").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST55").getExtentCase());
		currentNode.assignCategory(assignCategory);
          String testCaseID = "SITRESTLIST55";
				
		int noOfRecords = 3;
		newList = RestrictedListMgmt.prepareBlacklistMSISDNList(msisdnList, noOfRecords, "[START]", "[END]");
		ExtentI.Markup(ExtentColor.TEAL, "Uploading list of MSISDNS to be blacklisted as cp2pPayee");
		String actual2 = RestrictedListMgmt.blacklistSubscriberMultipleUploadList(domainName, categoryName,username,"cp2pPayee", "cp2pPayer",  "c2sPayee", noOfRecords, testCaseID, newList);
		
		String expected2 = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklisting.message.success");

		Validator.messageCompare(actual2, expected2);

	}
	
	
	@Test(dataProvider = "categoryData1")
	public void z26_RestrictedListUnBlacklistMultipleMSISDNS(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST56").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST56").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String multipleMSISDNS = newList.get(1) + "," + newList.get(2);
	
		ExtentI.Markup(ExtentColor.TEAL, "Multiple MSISDNS to be unblacklisted as cp2pPayee");
		String actual1 = RestrictedListMgmt.UnblacklistSubscriberMultiple(domainName,categoryName,username, "cp2pPayee","cp2pPayer", "c2sPayee",multipleMSISDNS);
			
		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.unblacklistselectedsubs.message.success",username);

		Validator.messageCompare(actual1, expected1);

	}
	

	
	
	
	@Test(dataProvider = "categoryData1")
	public void z27_uploadblacklistMSISDNList(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST57").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST57").getExtentCase());
		currentNode.assignCategory(assignCategory);
          String testCaseID = "SITRESTLIST57";
				
		int noOfRecords = 3;
		newList = RestrictedListMgmt.prepareBlacklistMSISDNList(msisdnList, noOfRecords, "[START]", "[END]");
		ExtentI.Markup(ExtentColor.TEAL, "Uploading list of MSISDNS to be blacklisted as c2sPayee");
		String actual2 = RestrictedListMgmt.blacklistSubscriberMultipleUploadList(domainName, categoryName,username,"c2sPayee","cp2pPayee", "cp2pPayer", noOfRecords, testCaseID, newList);
		
		String expected2 = MessagesDAO.prepareMessageByKey("restrictedsubs.blacklisting.message.success");

		Validator.messageCompare(actual2, expected2);

	}
	
	
	@Test(dataProvider = "categoryData1")
	public void z28_RestrictedListUnBlacklistMultipleMSISDNS(String domainName, String categoryName, String geography, String username)
			throws InterruptedException, Throwable {
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITRESTLIST58").getModuleCode());
			TestCaseCounter = true;
		}

		RestrictedListMgmt RestrictedListMgmt = new RestrictedListMgmt(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITRESTLIST58").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String multipleMSISDNS = newList.get(1) + "," + newList.get(2);
	
		ExtentI.Markup(ExtentColor.TEAL, "Multiple MSISDNS to be unblacklisted as c2sPayee");
		String actual1 = RestrictedListMgmt.UnblacklistSubscriberMultiple(domainName,categoryName,username,"c2sPayee","cp2pPayer", "cp2pPayee" ,multipleMSISDNS);
			
		String expected1 = MessagesDAO.prepareMessageByKey("restrictedsubs.unblacklistselectedsubs.message.success",username);

		Validator.messageCompare(actual1, expected1);

	}
	
	
	
	
	
	
	
	
}
