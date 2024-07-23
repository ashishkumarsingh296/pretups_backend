package restassuredapi.test;

import static com.utils.GenerateToken.getAcccessToken;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.sshmanager.SSHService;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.DateUtils;
import com.utils.ExcelUtility;
import com.utils.FileOperations;
import com.utils.FolderPath;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.bulkevdapi.BulkEvdApi;
import restassuredapi.api.rechargebulkrestrictedlist.CustomerRechargeRestrictedListApi;
import restassuredapi.api.rechargebulktemplate.CustomerRechargeTemplateApi;
import restassuredapi.pojo.bulkevdrequestpojo.BulkEVDRequestPojo;
import restassuredapi.pojo.bulkevdrequestpojo.BulkEvdVo;
import restassuredapi.pojo.bulkevdresponsepojo.BulkEVDResponsePojo;
import restassuredapi.pojo.rechargerestrictedlistresponsepojo.CustomerRechargeRestrictedListResponsePojo;
import restassuredapi.pojo.rechargetemplateresponsepojo.CustomerRechargeTemplateResponsePojo;

@ModuleManager(name = Module.BULK_EVD_RECHARGE)
public final class BulkEVDRecharge extends BaseTest {

	private BulkEVDRecharge() {
	}

	static String moduleCode;
	Map<String, String> channelUserData = new HashMap<String, String>();
	BulkEVDRequestPojo bulkEVDRequestPojo = new BulkEVDRequestPojo();
	BulkEVDResponsePojo bulkEVDResponsePojo = new BulkEVDResponsePojo();
	CustomerRechargeTemplateResponsePojo customerRechargeTemplateResponsePojo = new CustomerRechargeTemplateResponsePojo();
	CustomerRechargeRestrictedListResponsePojo customerRechargeRestrictedListResponsePojo = new CustomerRechargeRestrictedListResponsePojo();

	String base64EncodedString = null;

	public void downloadEVDRechargeTemplate(String username, String password) throws IOException {

		FileOperations.deleteFilesFromFolderIfExists(FolderPath.BULK_EVD_RECHARGE);
		CustomerRechargeTemplateApi customerRechargeTemplateApi = new CustomerRechargeTemplateApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessToken(username, password));
		customerRechargeTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		customerRechargeTemplateApi.setExpectedStatusCode(200);
		customerRechargeTemplateApi.perform();
		customerRechargeTemplateResponsePojo = customerRechargeTemplateApi
				.getAPIResponseAsPOJO(CustomerRechargeTemplateResponsePojo.class);
	}

	public void downloadRestrictedListTemplate(String username, String password) throws IOException {

		FileOperations.deleteFilesFromFolderIfExists(FolderPath.BULK_EVD_RESTRICTED_LIST_TEMPLATE);
		CustomerRechargeRestrictedListApi customerRechargeRestrictedListApi = new CustomerRechargeRestrictedListApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessToken(username, password));
		customerRechargeRestrictedListApi.setContentType(_masterVO.getProperty("contentType"));
		customerRechargeRestrictedListApi.setExpectedStatusCode(200);
		customerRechargeRestrictedListApi.perform();
		customerRechargeRestrictedListResponsePojo = customerRechargeRestrictedListApi
				.getAPIResponseAsPOJO(CustomerRechargeRestrictedListResponsePojo.class);
	}

	public void createRequestPayloadForBulkEvdRecharge(String file, String noOfDays, String occurence, String pin,
			String scheduleDate, String schedule, String templateType) {

		BulkEvdVo bulkEvdVo = new BulkEvdVo();
		bulkEvdVo.setBatchType("normal");
		bulkEvdVo.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		bulkEvdVo.setFile(file);
		if (templateType.equalsIgnoreCase("Restricted Template")) {
			bulkEvdVo.setFileName(customerRechargeRestrictedListResponsePojo.getFileName().toString().split("\\.")[0]);
		} else {
			bulkEvdVo.setFileName(customerRechargeTemplateResponsePojo.getFileName().toString().split("\\.")[0]);
		}
		if (templateType.equalsIgnoreCase("Restricted Template")) {
			bulkEvdVo.setFileType(customerRechargeRestrictedListResponsePojo.getFileType().toString());
		} else {
			bulkEvdVo.setFileType(customerRechargeTemplateResponsePojo.getFileType().toString());
		}
		bulkEvdVo.setNoOfDays(noOfDays);
		bulkEvdVo.setOccurence(occurence);
		bulkEvdVo.setPin(pin);
		bulkEvdVo.setScheduleDate(scheduleDate);
		bulkEvdVo.setScheduleNow(schedule);
		bulkEVDRequestPojo.setData(bulkEvdVo);

	}

	public void executeBulkEvdRechargeApi(int statusCode, String username, String password) throws IOException {

		Log.info("Entering executeBulkEvdRechargeApi()");
		BulkEvdApi bulkEvdApi = new BulkEvdApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				getAcccessToken(username, password));
		bulkEvdApi.addBodyParam(bulkEVDRequestPojo);
		bulkEvdApi.logRequestBody(bulkEVDRequestPojo);
		bulkEvdApi.setExpectedStatusCode(statusCode);
		bulkEvdApi.perform();
		bulkEVDResponsePojo = bulkEvdApi.getAPIResponseAsPOJO(BulkEVDResponsePojo.class);
		Log.info("Exiting executeBulkEvdRechargeApi()");

	}

	public void writeDataInTemplate(String filepath, String msisdnType, String evdAmount) {
		List<String> templateData = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			String msisdn = UniqueChecker.generate_subscriber_MSISDN(msisdnType);
			String subService = "1";
			String amount = evdAmount;
			String languageCode = "0";
			String excelData = msisdn + "," + subService + "," + amount + "," + languageCode;
			templateData.add(excelData);
		}

		ExcelUtility.setExcelFileXLS(filepath, "Sheet1");
		for (int i = 0; i < templateData.size(); i++) {
			String[] row = templateData.get(i).split(",");
			for (int j = 0; j < row.length; j++) {
				ExcelUtility.setCellDataXLS(row[j], 2 + i, j);

			}
		}
	}

	public void writeDataInRestrictedTemplateList(String filepath, String evdAmount) {
		ExcelUtility.setExcelFileXLS(filepath, "Sheet1");
		int rowCount = ExcelUtility.getRowCountXlsFile();
		Log.info("Total no of row count " + rowCount);
		for (int i = 2; i <= rowCount; i++) {
			ExcelUtility.setCellDataXLS(evdAmount, i, 8);
		}
	}

	public void checkUserBalance(String loginID) {
		DBHandler.AccessHandler.getUserBalanceWithLoginID(loginID);
	}

	public String generateFileAttachment(String loginID, String password, String mobileNoType, String evdDenom)
			throws IOException {
		downloadEVDRechargeTemplate(loginID, password);
		base64EncodedString = customerRechargeTemplateResponsePojo.getFileattachment().toString();
		String fileName = customerRechargeTemplateResponsePojo.getFileName().toString();
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.BULK_EVD_RECHARGE);
		String filePath = FileOperations.file(FolderPath.BULK_EVD_RECHARGE).listFiles()[0].toString();
		writeDataInTemplate(filePath, mobileNoType, evdDenom);
		return base64EncodedString = ExcelUtility.excelToBase64(filePath);
	}

	public String generateRestrictedListFileAttachment(String loginID, String password, String evdDenom)
			throws IOException {
		downloadRestrictedListTemplate(loginID, password);
		base64EncodedString = customerRechargeRestrictedListResponsePojo.getFileattachment().toString();
		String fileName = customerRechargeRestrictedListResponsePojo.getFileName().toString();
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.BULK_EVD_RESTRICTED_LIST_TEMPLATE);
		String filePath = FileOperations.file(FolderPath.BULK_EVD_RESTRICTED_LIST_TEMPLATE).listFiles()[0].toString();
		writeDataInRestrictedTemplateList(filePath, evdDenom);
		return base64EncodedString = ExcelUtility.excelToBase64(filePath);
	}

	public String fetchScheduleBatchId() {
		return bulkEVDResponsePojo.getScheduleBatchId();
	}

	@AfterMethod
	public void executeShellScript(ITestResult result) {

		if (result.isSuccess() && result.getMethod().getMethodName().contains("schedule")) {
			SSHService.executeScript("scheduleTopUP.sh");
			Log.info("Shell script executed successfully");
		}
	}

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount() - 4;

		Object[][] data = new Object[rowCount][6];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			data[j][3] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			data[j][4] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			j++;
		}
		return data;
	}

	public void setTestInitialDetails(String methodName, String loginID, String password, String categoryName,
			String caseId) throws Exception {
		Log.startTestCase(methodName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseId);
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
	}

	@Test(dataProvider = "userData", priority = 1)
	@TestManager(TestKey = "PRETUPS-001")
	public void bulkEVDRecharge(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE01");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.PREPAID_MOB_NUM, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_DAILY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_ON, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(200, loginID, password);
		String message = bulkEVDResponsePojo.getMessage().split(" For")[0].trim();
		Assertion.assertEquals(message, "File is uploaded and processed successfully.");
		Assert.assertEquals(message, "File is uploaded and processed successfully.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void bulkEVDRecharge_scheduleDailyRecharge(String loginID, String password, String pin, String parentName,
			String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE02");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.PREPAID_MOB_NUM, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_DAILY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_OFF, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(202, loginID, password);
		String batchScheduledStatus = DBHandler.AccessHandler.getColumnValueFromTable(PretupsI.COLUMN_STATUS,
				PretupsI.TABLE_SCHEDULED_BATCH_MASTER, PretupsI.COLUMN_BATCH_ID, fetchScheduleBatchId());
		Assertion.assertEquals(batchScheduledStatus, "S");
		Assert.assertEquals(batchScheduledStatus, "S");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void bulkEVDRecharge_scheduleWeeklyRecharge(String loginID, String password, String pin, String parentName,
			String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_scheduleWeeklyRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE03");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.PREPAID_MOB_NUM, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_WEEKLY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_OFF, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(202, loginID, password);
		String batchScheduledStatus = DBHandler.AccessHandler.getColumnValueFromTable(PretupsI.COLUMN_STATUS,
				PretupsI.TABLE_SCHEDULED_BATCH_MASTER, PretupsI.COLUMN_BATCH_ID, fetchScheduleBatchId());
		Assertion.assertEquals(batchScheduledStatus, "S");
		Assert.assertEquals(batchScheduledStatus, "S");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void bulkEVDRecharge_scheduleMonthlyRecharge(String loginID, String password, String pin, String parentName,
			String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_scheduleMonthlyRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE04");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.PREPAID_MOB_NUM, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_MONTHLY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_OFF, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(202, loginID, password);
		String batchScheduledStatus = DBHandler.AccessHandler.getColumnValueFromTable(PretupsI.COLUMN_STATUS,
				PretupsI.TABLE_SCHEDULED_BATCH_MASTER, PretupsI.COLUMN_BATCH_ID, fetchScheduleBatchId());
		Assertion.assertEquals(batchScheduledStatus, "S");
		Assert.assertEquals(batchScheduledStatus, "S");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-005")
	public void bulkEVDRecharge_invalidNoOfDays(String loginID, String password, String pin, String parentName,
			String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_scheduleMonthlyRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE05");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.PREPAID_MOB_NUM, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "-1", PretupsI.OCCURENCE_DAILY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_ON, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(400, loginID, password);
		String message = bulkEVDResponsePojo.getMessage();
		Assertion.assertEquals(message, "Batch list not found.");
		Assert.assertEquals(message, "Batch list not found.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-006")
	public void bulkEVDRecharge_invalidPIN(String loginID, String password, String pin, String parentName,
			String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_invalidPIN";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE06");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.PREPAID_MOB_NUM, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_DAILY, PretupsI.INVALID_PIN,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_ON, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(400, loginID, password);
		String message = bulkEVDResponsePojo.getMessage();
		Assertion.assertEquals(message, "The PIN you have entered is incorrect.");
		Assert.assertEquals(message, "The PIN you have entered is incorrect.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-007")
	public void bulkEVDRecharge_enterFutureDateForScheduleNow(String loginID, String password, String pin,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_enterFutureDateForScheduleNow";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE07");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.PREPAID_MOB_NUM, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_DAILY, pin,
				DateUtils.getFutureDate(5), PretupsI.SCHEDULE_ON, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(400, loginID, password);
		String message = bulkEVDResponsePojo.getMessage();
		Assertion.assertEquals(message, "Please provide current date in case of Schedue now.");
		Assert.assertEquals(message, "Please provide current date in case of Schedue now.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-008")
	public void bulkEVDRecharge_NoOccurence(String loginID, String password, String pin, String parentName,
			String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_NoOccurence";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE08");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.PREPAID_MOB_NUM, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "2", PretupsI.NO_OCCURENCE, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_OFF, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(400, loginID, password);
		String message = bulkEVDResponsePojo.getMessage();
		Assertion.assertEquals(message, "Please enter Schedule occurrence.");
		Assert.assertEquals(message, "Please enter Schedule occurrence.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 9)
	@TestManager(TestKey = "PRETUPS-009")
	public void bulkEVDRecharge_InvalidMobileNumbers(String loginID, String password, String pin, String parentName,
			String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_InvalidMobileNumbers";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE09");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.ALPHANUMERIC_MOB_NUM, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_DAILY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_ON, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(400, loginID, password);
		String message = bulkEVDResponsePojo.getMessage();
		Assertion.assertEquals(message, "Uploaded file does not have any valid records.");
		Assert.assertEquals(message, "Uploaded file does not have any valid records.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 10)
	@TestManager(TestKey = "PRETUPS-010")
	public void bulkEVDRecharge_NonEnabledEvdDenomination(String loginID, String password, String pin,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_NonEnabledEvdDenomination";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE10");
		String base64EncodedString = generateFileAttachment(loginID, password, PretupsI.PREPAID_MOB_NUM, "5");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_DAILY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_ON, PretupsI.NORMAL_TEMPLATE);
		executeBulkEvdRechargeApi(400, loginID, password);
		String message = bulkEVDResponsePojo.getStatus();
		Assertion.assertEquals(message, "400");
		Assert.assertEquals(message, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 11)
	@TestManager(TestKey = "PRETUPS-011")
	public void bulkEVDRecharge_RestrictedMsisdn(String loginID, String password, String pin, String parentName,
			String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_RestrictedMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE11");
		String base64EncodedString = generateRestrictedListFileAttachment(loginID, password, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_DAILY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_ON, PretupsI.RESTRICTED_TEMPLATE);
		executeBulkEvdRechargeApi(200, loginID, password);
		String message = bulkEVDResponsePojo.getMessage().split(" For")[0].trim();
		Assertion.assertEquals(message, "File is uploaded and processed successfully.");
		Assert.assertEquals(message, "File is uploaded and processed successfully.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 12)
	@TestManager(TestKey = "PRETUPS-012")
	public void bulkEVDRecharge_RestrictedMsisdn_scheduleDailyRecharge(String loginID, String password, String pin,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_RestrictedMsisdn_scheduleDailyRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE12");
		String base64EncodedString = generateRestrictedListFileAttachment(loginID, password, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_DAILY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_OFF, PretupsI.RESTRICTED_TEMPLATE);
		executeBulkEvdRechargeApi(202, loginID, password);
		String batchScheduledStatus = DBHandler.AccessHandler.getColumnValueFromTable(PretupsI.COLUMN_STATUS,
				PretupsI.TABLE_SCHEDULED_BATCH_MASTER, PretupsI.COLUMN_BATCH_ID, fetchScheduleBatchId());
		Assertion.assertEquals(batchScheduledStatus, "S");
		Assert.assertEquals(batchScheduledStatus, "S");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 13)
	@TestManager(TestKey = "PRETUPS-013")
	public void bulkEVDRecharge_RestrictedMsisdn_scheduleWeeklyRecharge(String loginID, String password, String pin,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_RestrictedMsisdn_scheduleWeeklyRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE13");
		String base64EncodedString = generateRestrictedListFileAttachment(loginID, password, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_WEEKLY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_OFF, PretupsI.RESTRICTED_TEMPLATE);
		executeBulkEvdRechargeApi(202, loginID, password);
		String batchScheduledStatus = DBHandler.AccessHandler.getColumnValueFromTable(PretupsI.COLUMN_STATUS,
				PretupsI.TABLE_SCHEDULED_BATCH_MASTER, PretupsI.COLUMN_BATCH_ID, fetchScheduleBatchId());
		Assertion.assertEquals(batchScheduledStatus, "S");
		Assert.assertEquals(batchScheduledStatus, "S");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 14)
	@TestManager(TestKey = "PRETUPS-014")
	public void bulkEVDRecharge_RestrictedMsisdn_scheduleMonthlyRecharge(String loginID, String password, String pin,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "bulkEVDRecharge_RestrictedMsisdn_scheduleMonthlyRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "BULKEVDRECHARGE14");
		String base64EncodedString = generateRestrictedListFileAttachment(loginID, password, "50");
		createRequestPayloadForBulkEvdRecharge(base64EncodedString, "1", PretupsI.OCCURENCE_MONTHLY, pin,
				DateUtils.getCurrentDate(), PretupsI.SCHEDULE_OFF, PretupsI.RESTRICTED_TEMPLATE);
		executeBulkEvdRechargeApi(202, loginID, password);
		String batchScheduledStatus = DBHandler.AccessHandler.getColumnValueFromTable(PretupsI.COLUMN_STATUS,
				PretupsI.TABLE_SCHEDULED_BATCH_MASTER, PretupsI.COLUMN_BATCH_ID, fetchScheduleBatchId());
		Assertion.assertEquals(batchScheduledStatus, "S");
		Assert.assertEquals(batchScheduledStatus, "S");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
