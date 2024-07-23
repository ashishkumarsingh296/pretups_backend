package restassuredapi.test;

import static com.utils.GenerateToken.getAcccessToken;
import static com.utils.GenerateToken.getAcccessTokenForChannelUser;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.FileOperations;
import com.utils.FolderPath;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2sBulkReversalApi.C2SBulkReversalAPI;
import restassuredapi.api.c2sBulkReverseDownloadTemplateApi.C2sBulkReverseDownloadTemplateApi;
import restassuredapi.api.fixedrecharge.FixedRechargeApi;
import restassuredapi.api.internetrecharge.InternetRechargeApi;
import restassuredapi.api.prepaidrecharge.PrepaidRechargeApi;
import restassuredapi.pojo.c2sBulkReversalRequestPojo.C2SBulkReversalRequestPojo;
import restassuredapi.pojo.c2sBulkReversalResponsePojo.C2SBulkReversalResponsePojo;
import restassuredapi.pojo.c2sBulkReverseDownloadTemplateResponsePojo.C2sBulkReverseDownloadTemplateResponsePojo;
import restassuredapi.pojo.fixedrechargeresponsepojo.FixedRechargeResponsePojo;
import restassuredapi.pojo.internetrechargeresponsepojo.InternetRechargeResponsePojo;
import restassuredapi.pojo.prepaidrechargeresponsepojo.PrepaidRechargeResponsePojo;

@ModuleManager(name = Module.C2S_BULK_RECHARGE_REVERSAL)
public final class C2SBulkRechargeReversal extends BaseTest {

	private C2SBulkRechargeReversal() {
	}

	Map<String, String> channelUserData = new HashMap<String, String>();

	@BeforeClass
	public void getChannelUserData() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			int rowNum = i + 1;
			String catCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, rowNum);
			String loginId = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, rowNum);
			String password = ExcelUtility.getCellData(0, ExcelI.PASSWORD, rowNum);
			String pin = ExcelUtility.getCellData(0, ExcelI.PIN, rowNum);
			channelUserData.put(catCode.toLowerCase() + "_loginId", loginId);
			channelUserData.put(catCode.toLowerCase() + "_password", password);
			channelUserData.put(catCode.toLowerCase() + "_pin", pin);
		}
	}

	@AfterMethod
	public void clearArrayList() {
		rechargeTxnIds.clear();
	}

	static String moduleCode;
	C2SBulkReversalRequestPojo c2SBulkReversalRequestPojo = new C2SBulkReversalRequestPojo();
	C2SBulkReversalResponsePojo c2SBulkReversalResponsePojo = new C2SBulkReversalResponsePojo();
	C2sBulkReverseDownloadTemplateResponsePojo c2sBulkReverseDownloadTemplateResponsePojo = new C2sBulkReverseDownloadTemplateResponsePojo();
	ArrayList<String> rechargeTxnIds = new ArrayList<String>();
	String base64EncodedString = null;

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);

		Object[][] Data = new Object[1][5];
		int j = 0;
		int i = 11;
		Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
		Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
		Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
		Data[j][3] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		Data[j][4] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
		return Data;
	}

	public void createRequestPayloadForC2SBulkReversal(String attachment, String batchName, String fileName,
			String fileType) {
		c2SBulkReversalRequestPojo.setAttachment(attachment);
		c2SBulkReversalRequestPojo.setBatchName(batchName);
		c2SBulkReversalRequestPojo.setFileName(fileName);
		c2SBulkReversalRequestPojo.setFileType(fileType);
	}

	public void setTestInitialDetails(String methodName, String loginID, String password, String categoryName,
			String caseId) throws Exception {
		Log.startTestCase(methodName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseId);
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
	}

	public void executeC2sBulkReverseDownloadTemplateApi(int statusCode, String username, String password)
			throws IOException {
		Log.info("Entering executeC2sBulkReverseDownloadTemplateApi()");
		C2sBulkReverseDownloadTemplateApi c2sBulkReverseDownloadTemplateApi = new C2sBulkReverseDownloadTemplateApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessToken(username, password));
		c2sBulkReverseDownloadTemplateApi.setExpectedStatusCode(statusCode);
		c2sBulkReverseDownloadTemplateApi.perform();
		c2sBulkReverseDownloadTemplateResponsePojo = c2sBulkReverseDownloadTemplateApi
				.getAPIResponseAsPOJO(C2sBulkReverseDownloadTemplateResponsePojo.class);
		Log.info("Exiting executeC2sBulkReverseDownloadTemplateApi()");
	}

	public void executeC2sBulkRechargeRevarsalAPI(int statusCode, String username, String password) throws IOException {
		Log.info("Entering executeC2sBulkRechargeRevarsalAPI()");
		C2SBulkReversalAPI c2SBulkReversalAPI = new C2SBulkReversalAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessToken(username, password));
		c2SBulkReversalAPI.addBodyParam(c2SBulkReversalRequestPojo);
		c2SBulkReversalAPI.logRequestBody(c2SBulkReversalRequestPojo);
		c2SBulkReversalAPI.setExpectedStatusCode(statusCode);
		c2SBulkReversalAPI.perform();
		c2SBulkReversalResponsePojo = c2SBulkReversalAPI.getAPIResponseAsPOJO(C2SBulkReversalResponsePojo.class);
		Log.info("Exiting executeC2sBulkRechargeRevarsalAPI()");
	}

	public void initateC2SPrepaidRecharge(String channelUserPin, int iterationCount) {

		PrepaidRecharge prepaidRecharge = new PrepaidRecharge();
		prepaidRecharge.setupData(channelUserPin);

		for (int i = 0; i < iterationCount; i++) {
			PrepaidRechargeApi prepaidRechargeAPI = new PrepaidRechargeApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessTokenForChannelUser(
							channelUserData.get("dist_loginId"), channelUserData.get("dist_password")));
			prepaidRechargeAPI.setContentType(_masterVO.getProperty("contentType"));
			prepaidRechargeAPI.addBodyParam(prepaidRecharge.addChannelUserRequestPojo);
			prepaidRechargeAPI.logRequestBody(prepaidRecharge.addChannelUserRequestPojo);
			prepaidRechargeAPI.setExpectedStatusCode(201);
			prepaidRechargeAPI.perform();
			try {
				prepaidRecharge.addChannelUserResponsePojo = prepaidRechargeAPI
						.getAPIResponseAsPOJO(PrepaidRechargeResponsePojo.class);
			} catch (IOException e) {
				throw new RuntimeException("Unable to initiate C2S Prepaid Recharge");
			}
			String txnid = prepaidRecharge.addChannelUserResponsePojo.getDataObject().getTxnid();
			Log.info("The transaction ID of prepaid recharge: " + txnid);
			rechargeTxnIds.add(txnid);
		}
	}

	public void initateFixLineRecharge(String channelUserPin, int iterationCount) {

		FixedRecharge fixedRecharge = new FixedRecharge();
		fixedRecharge.setupData(channelUserPin);

		for (int i = 0; i < iterationCount; i++) {
			FixedRechargeApi fixedRechargeApi = new FixedRechargeApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessTokenForChannelUser(
							channelUserData.get("dist_loginId"), channelUserData.get("dist_password")));
			fixedRechargeApi.setContentType(_masterVO.getProperty("contentType"));
			fixedRecharge.data.setSelector("1");
			fixedRechargeApi.addBodyParam(fixedRecharge.fixedRechargeRequestPojo);
			fixedRechargeApi.logRequestBody(fixedRecharge.fixedRechargeRequestPojo);
			fixedRechargeApi.setExpectedStatusCode(201);
			fixedRechargeApi.perform();
			try {
				fixedRecharge.fixedRechargeResponsePojo = fixedRechargeApi
						.getAPIResponseAsPOJO(FixedRechargeResponsePojo.class);
			} catch (IOException e) {
				throw new RuntimeException("Unable to initiate Fixline Recharge");
			}
			String txnid = fixedRecharge.fixedRechargeResponsePojo.getDataObject().getTxnid();
			System.out.println("The transaction ID of Fixline recharge: " + txnid);
			rechargeTxnIds.add(txnid);
		}
	}

	public void initateInternetRecharge(String channelUserPin, int iterationCount) {

		InternetRecharge internetRecharge = new InternetRecharge();
		internetRecharge.setupData(channelUserPin);

		for (int i = 0; i < iterationCount; i++) {
			InternetRechargeApi internetRechargeApi = new InternetRechargeApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessTokenForChannelUser(
							channelUserData.get("dist_loginId"), channelUserData.get("dist_password")));
			internetRechargeApi.setContentType(_masterVO.getProperty("contentType"));
			internetRechargeApi.addBodyParam(internetRecharge.internetRechargeRequestPojo);
			internetRechargeApi.setExpectedStatusCode(201);
			internetRechargeApi.perform();
			try {
				internetRecharge.internetRechargeResponsePojo = internetRechargeApi
						.getAPIResponseAsPOJO(InternetRechargeResponsePojo.class);
			} catch (IOException e) {
				throw new RuntimeException("Unable to initiate Internet Recharge");
			}
			String txnid = internetRecharge.internetRechargeResponsePojo.getDataObject().getTxnid();
			System.out.println("The transaction ID of Internet recharge: " + txnid);
			rechargeTxnIds.add(txnid);
		}
	}

	public void generateInvalidTransactionId(int count) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
		Date d = new Date();
		for (int i = 0; i < count; i++) {
			RandomGeneration random = new RandomGeneration();
			String invalidTxnId = "T" + sdf.format(d.getTime()).replace("/", "") + "." + random.randomNumeric(4) + "."
					+ random.randomNumeric(6);
			rechargeTxnIds.add(invalidTxnId);
			Log.info("Invalid transaction ID: " + invalidTxnId);
		}
	}

	@Test(dataProvider = "userData", priority = 1)
	@TestManager(TestKey = "PRETUPS-001")
	public void channelAdmin_ReverseBulkPrepaidRecharge(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReverseBulkPrepaidRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV01");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
		String balBeforeReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP",
				channelUserData.get("dist_loginId"));
		Log.info("Balance before reversal: " + balBeforeReversal);
		initateC2SPrepaidRecharge(channelUserData.get("dist_pin"), 4);
		ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
		base64EncodedString = ExcelUtility
				.excelToBase64(FileOperations.file(FolderPath.C2S_BULK_REVERSAL).listFiles()[0].toString());
		createRequestPayloadForC2SBulkReversal(base64EncodedString, "C2SBulkReversal", fileName, "xls");
		executeC2sBulkRechargeRevarsalAPI(200, loginID, password);
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getMessage();
		String balAfterReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP", channelUserData.get("dist_loginId"));
		Log.info("Balance after reversal: " + balAfterReversal);
		Assertion.assertEquals(balBeforeReversal, balAfterReversal);
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is successful.");
		Assert.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is successful.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void channelAdmin_ReverseBulkInternetRecharge(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReverseBulkInternetRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV02");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
		String balBeforeReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP",
				channelUserData.get("dist_loginId"));
		Log.info("Balance before reversal: " + balBeforeReversal);
		initateInternetRecharge(channelUserData.get("dist_pin"), 4);
		ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
		base64EncodedString = ExcelUtility
				.excelToBase64(FileOperations.file(FolderPath.C2S_BULK_REVERSAL).listFiles()[0].toString());
		createRequestPayloadForC2SBulkReversal(base64EncodedString, "C2SBulkReversal", fileName, "xls");
		executeC2sBulkRechargeRevarsalAPI(200, loginID, password);
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getMessage();
		String balAfterReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP", channelUserData.get("dist_loginId"));
		Log.info("Balance after reversal: " + balAfterReversal);
		Assertion.assertEquals(balBeforeReversal, balAfterReversal);
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is successful.");
		Assert.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is successful.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void channelAdmin_ReverseBulkFixlineRecharge(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReverseBulkFixlineRecharge";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV03");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
		String balBeforeReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP",
				channelUserData.get("dist_loginId"));
		Log.info("Balance before reversal: " + balBeforeReversal);
		initateFixLineRecharge(channelUserData.get("dist_pin"), 4);
		ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
		base64EncodedString = ExcelUtility
				.excelToBase64(FileOperations.file(FolderPath.C2S_BULK_REVERSAL).listFiles()[0].toString());
		createRequestPayloadForC2SBulkReversal(base64EncodedString, "C2SBulkReversal", fileName, "xls");
		executeC2sBulkRechargeRevarsalAPI(200, loginID, password);
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getMessage();
		String balAfterReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP", channelUserData.get("dist_loginId"));
		Log.info("Balance after reversal: " + balAfterReversal);
		Assertion.assertEquals(balBeforeReversal, balAfterReversal);
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is successful.");
		Assert.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is successful.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void channelAdmin_ReverseBulkWithValidAndInvalidTxn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReverseBulkWithValidAndInvalidTxn";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV04");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
		String balBeforeReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP",
				channelUserData.get("dist_loginId"));
		Log.info("Balance before reversal: " + balBeforeReversal);
		initateFixLineRecharge(channelUserData.get("dist_pin"), 4);
		generateInvalidTransactionId(4);
		ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
		base64EncodedString = ExcelUtility
				.excelToBase64(FileOperations.file(FolderPath.C2S_BULK_REVERSAL).listFiles()[0].toString());
		createRequestPayloadForC2SBulkReversal(base64EncodedString, "C2SBulkReversal", fileName, "xls");
		executeC2sBulkRechargeRevarsalAPI(200, loginID, password);
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getMessage().trim();
		String balAfterReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP", channelUserData.get("dist_loginId"));
		Log.info("Balance after reversal: " + balAfterReversal);
		Assertion.assertEquals(balBeforeReversal, balAfterReversal);
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is partially successful.");
		Assert.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is partially successful.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-005")
	public void channelAdmin_ReverseBulkWithBlankBatchName(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReverseBulkWithBlankBatchName";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV05");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
		initateFixLineRecharge(channelUserData.get("dist_pin"), 4);
		ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
		base64EncodedString = ExcelUtility
				.excelToBase64(FileOperations.file(FolderPath.C2S_BULK_REVERSAL).listFiles()[0].toString());
		createRequestPayloadForC2SBulkReversal(base64EncodedString, "", fileName, "xls");
		executeC2sBulkRechargeRevarsalAPI(400, loginID, password);
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getMessage();
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.assertEquals(message, "Batch name is empty.");
		Assert.assertEquals(message, "Batch name is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-006")
	public void channelAdmin_ReverseBulkWithInvalidBatchName(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReverseBulkWithInvalidBatchName";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV06");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
		initateFixLineRecharge(channelUserData.get("dist_pin"), 4);
		ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
		base64EncodedString = ExcelUtility
				.excelToBase64(FileOperations.file(FolderPath.C2S_BULK_REVERSAL).listFiles()[0].toString());
		createRequestPayloadForC2SBulkReversal(base64EncodedString, "%^$%@!#!!@", fileName, "xls");
		executeC2sBulkRechargeRevarsalAPI(400, loginID, password);
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getMessage();
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.assertEquals(message, "Special chars not allowed in batch name.");
		Assert.assertEquals(message, "Special chars not allowed in batch name.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-005")
	public void channelAdmin_ReverseBulkWithNoAttachment(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReverseBulkWithBlankBatchName";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV07");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		createRequestPayloadForC2SBulkReversal("", "C2SBulkReversal", fileName, "xls");
		executeC2sBulkRechargeRevarsalAPI(400, loginID, password);
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getMessage();
		Assertion.assertEquals(String.valueOf(status), "400");
		Assertion.assertEquals(message, "No such file exists.");
		Assert.assertEquals(message, "No such file exists.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-008")
	public void channelAdmin_ReversedC2STransactions(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReversedC2STransactions";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV08");
		int statusCode = 200;

		/*
		 * Calling API twice to reverse already reversed C2S transactions.
		 */
		for (int i = 0; i < 2; i++) {
			executeC2sBulkReverseDownloadTemplateApi(statusCode, loginID, password);
			base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
			String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
			FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
			ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
			if (i == 0) {
				initateC2SPrepaidRecharge(channelUserData.get("dist_pin"), 4);
			}
			ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
			base64EncodedString = ExcelUtility
					.excelToBase64(FileOperations.file(FolderPath.C2S_BULK_REVERSAL).listFiles()[0].toString());
			createRequestPayloadForC2SBulkReversal(base64EncodedString, "C2SBulkReversal", fileName, "xls");
			executeC2sBulkRechargeRevarsalAPI(statusCode, loginID, password);
			Thread.sleep(5000);
		}
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getMessage();
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.assertEquals(message, "Recharge reversal for batch C2SBulkReversal has failed.");
		Assert.assertEquals(message, "Recharge reversal for batch C2SBulkReversal has failed.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 9)
	@TestManager(TestKey = "PRETUPS-009")
	public void channelAdmin_ReverseInvalidC2STransactions(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReverseInvalidC2STransactions";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV09");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
		generateInvalidTransactionId(4);
		ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
		base64EncodedString = ExcelUtility
				.excelToBase64(FileOperations.file(FolderPath.C2S_BULK_REVERSAL).listFiles()[0].toString());
		createRequestPayloadForC2SBulkReversal(base64EncodedString, "C2SBulkReversal", fileName, "xls");
		executeC2sBulkRechargeRevarsalAPI(200, loginID, password);
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList()
				.get(0).getErrorMsg();
		Assertion.assertEquals(String.valueOf(status), "400");
		Assertion.assertEquals(message,
				"Either the Entered transaction Id for Recharge Reversal is invalid or the transaction is too old to reverse.");
		Assert.assertEquals(message,
				"Either the Entered transaction Id for Recharge Reversal is invalid or the transaction is too old to reverse.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 10)
	@TestManager(TestKey = "PRETUPS-010")
	public void channelAdmin_ReverseMultipleC2sTypeServicesInBulk(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ReverseInvalidC2STransactions";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV10");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
		String balBeforeReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP",
				channelUserData.get("dist_loginId"));
		Log.info("Balance before reversal: " + balBeforeReversal);
		initateC2SPrepaidRecharge(channelUserData.get("dist_pin"), 2);
		initateFixLineRecharge(channelUserData.get("dist_pin"), 2);
		initateInternetRecharge(channelUserData.get("dist_pin"), 2);
		ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
		base64EncodedString = ExcelUtility
				.excelToBase64(FileOperations.file(FolderPath.C2S_BULK_REVERSAL).listFiles()[0].toString());
		createRequestPayloadForC2SBulkReversal(base64EncodedString, "C2SBulkReversal", fileName, "xls");
		executeC2sBulkRechargeRevarsalAPI(200, loginID, password);
		int status = c2SBulkReversalResponsePojo.getStatus();
		String message = c2SBulkReversalResponsePojo.getMessage();
		String balAfterReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP", channelUserData.get("dist_loginId"));
		Log.info("Balance after reversal: " + balAfterReversal);
		Assertion.assertEquals(balBeforeReversal, balAfterReversal);
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is successful.");
		Assert.assertEquals(message, "Recharge reversal for batch C2SBulkReversal is successful.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 10, enabled = false)
	@TestManager(TestKey = "PRETUPS-010")
	public void Test(String loginID, String password, String parentName, String categoryName, String categorCode)
			throws Exception {
		final String methodName = "channelAdmin_ReverseInvalidC2STransactions";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CHNLADMBTCHRECHRGEREV10");
		executeC2sBulkReverseDownloadTemplateApi(200, loginID, password);
		base64EncodedString = c2sBulkReverseDownloadTemplateResponsePojo.getFileattachment();
		String fileName = c2sBulkReverseDownloadTemplateResponsePojo.getFileName().split("\\.")[0];
		FileOperations.deleteFilesFromFolderIfExists(FolderPath.C2S_BULK_REVERSAL);
		ExcelUtility.base64ToExcel(base64EncodedString, fileName, FolderPath.C2S_BULK_REVERSAL);
		String balBeforeReversal = DBHandler.AccessHandler.getUserBalance("ETOPUP",
				channelUserData.get("dist_loginId"));
		Log.info("Balance before reversal: " + balBeforeReversal);
		initateC2SPrepaidRecharge(channelUserData.get("dist_pin"), 5);
		initateFixLineRecharge(channelUserData.get("dist_pin"), 5);
		initateInternetRecharge(channelUserData.get("dist_pin"), 5);
		ExcelUtility.updateExcelSheet(FolderPath.C2S_BULK_REVERSAL, rechargeTxnIds);
	}

}
