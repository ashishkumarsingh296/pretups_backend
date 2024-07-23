package restassuredapi.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.utils.DateUtil;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.commissioncalculatorequest.CommissionCalculatorApi;
import restassuredapi.pojo.commissioncalculatorequestpojo.CommissionCalculatorRequestPojo;
import restassuredapi.pojo.commissioncalculatoresponsepojo.CommissionCalculatorResponsePojo;
import restassuredapi.pojo.commissioncalculatorequestpojo.Data;

@ModuleManager(name = Module.REST_C2S_COMMISSION_CALCULATOR_TEST)
public class CommissionCalculatorTest extends BaseTest {
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
	Date dateobj = new Date();
	Date yesterday = DateUtils.addDays(dateobj, -1);
	String currentDate = df.format(dateobj);
	String yesterdayDate = df.format(yesterday);
	static String moduleCode;
	CommissionCalculatorRequestPojo commissionCalculatorRequestPojo = new CommissionCalculatorRequestPojo();
	CommissionCalculatorResponsePojo commissionCalculatorResponsePojo = new CommissionCalculatorResponsePojo();
	Data data = new Data();
	Login login = new Login();
	// NetworkAdminHomePage homepage = new NetworkAdminHomePage(driver);
	// Product product= null;
	HashMap<String, String> request_Details = new HashMap<String, String>();

	public HashMap<String, String> getExcelData() {
		HashMap<String, String> tranferDetails = new HashMap<String, String>();
		String C2CTransferCode = _masterVO.getProperty("C2CBuyVoucherTransferCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> categorySize = new ArrayList<String>();

		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}
		// int rowCount = ExcelUtility.getRowCount();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

		int totalObjectCounter = 0;
		for (int i = 0; i < alist2.size(); i++) {
			int categorySizeCounter = 0;
			for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(i))) {
					categorySizeCounter++;
				}
			}
			categorySize.add("" + categorySizeCounter);
			totalObjectCounter = totalObjectCounter + categorySizeCounter;
		}

		Object[][] Data = new Object[totalObjectCounter][9];

		for (int j = 0, k = 0; j < alist2.size(); j++) {

			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int excelRowSize = ExcelUtility.getRowCount();
			String ChannelUserMSISDN = null;
			for (int i = 1; i <= excelRowSize; i++) {
				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
					ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
					break;
				}
			}

			for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
					Data[k][0] = alist2.get(j);
					Data[k][1] = alist1.get(j);
					Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
					Data[k][3] = ChannelUserMSISDN;
					Data[k][4] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, excelCounter);
					Data[k][5] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
					Data[k][6] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
					Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
					Data[k][8] = excelCounter;

					k++;

				}
			}

		}

		tranferDetails.put("From_MSISDN", Data[0][2].toString());
		tranferDetails.put("To_MSISDN", Data[0][3].toString());
		tranferDetails.put("From_Pin", Data[0][6].toString());
		return tranferDetails;

	}

	public void setupData() {
		request_Details = getExcelData();
		commissionCalculatorRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		commissionCalculatorRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		commissionCalculatorRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		commissionCalculatorRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		commissionCalculatorRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		commissionCalculatorRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLoginid("");
		data.setPassword("");
		data.setMsisdn(request_Details.get("From_MSISDN"));
		data.setPin(request_Details.get("From_Pin"));
		data.setMsisdn2(request_Details.get("To_MSISDN"));
		data.setExtcode("");
		data.setFromDate(yesterdayDate);
		data.setToDate(yesterdayDate);
		commissionCalculatorRequestPojo.setData(data);

	}

	// Successful data with valid data.
	@Test
	public void A_01_Test_Positive_commissioncalculator() throws Exception {
		final String methodName = "Test_Positive_commissioncalculator";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SINC1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		CommissionCalculatorApi commissionCalculatorApi = new CommissionCalculatorApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		commissionCalculatorApi.setContentType(_masterVO.getProperty("contentType"));
		commissionCalculatorApi.addBodyParam(commissionCalculatorRequestPojo);
		commissionCalculatorApi.setExpectedStatusCode(200);
		commissionCalculatorApi.perform();
		commissionCalculatorResponsePojo = commissionCalculatorApi
				.getAPIResponseAsPOJO(CommissionCalculatorResponsePojo.class);
		int statusCode = Integer.parseInt(commissionCalculatorResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// network code provided is blank
	@Test
	public void A_02_Test_Negative2_commissioncalculator() throws Exception {
		final String methodName = "Test_Negative2_commissioncalculator";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SINC2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		CommissionCalculatorApi commissionCalculatorApi = new CommissionCalculatorApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		commissionCalculatorApi.setContentType(_masterVO.getProperty("contentType"));
		commissionCalculatorRequestPojo.getData().setExtnwcode("");
		commissionCalculatorApi.addBodyParam(commissionCalculatorRequestPojo);
		commissionCalculatorApi.setExpectedStatusCode(200);
		commissionCalculatorApi.perform();
		commissionCalculatorResponsePojo = commissionCalculatorApi
				.getAPIResponseAsPOJO(CommissionCalculatorResponsePojo.class);
		int statusCode = Integer.parseInt(commissionCalculatorResponsePojo.getDataObject().getTxnstatus());
		String message = commissionCalculatorResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	// blank msisdn
	@Test
	public void A_03_Test_Negative3_commissioncalculator() throws Exception {
		final String methodName = "Test_Negative3_commissioncalculator";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SINC3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		CommissionCalculatorApi commissionCalculatorApi = new CommissionCalculatorApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		commissionCalculatorApi.setContentType(_masterVO.getProperty("contentType"));
		commissionCalculatorRequestPojo.getData().setMsisdn("");
		commissionCalculatorApi.addBodyParam(commissionCalculatorRequestPojo);
		commissionCalculatorApi.setExpectedStatusCode(200);
		commissionCalculatorApi.perform();
		commissionCalculatorResponsePojo = commissionCalculatorApi
				.getAPIResponseAsPOJO(CommissionCalculatorResponsePojo.class);
		int statusCode = Integer.parseInt(commissionCalculatorResponsePojo.getDataObject().getTxnstatus());
		String message = commissionCalculatorResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message,
				"Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	// blank from date
	@Test
	public void A_04_Test_Negative4_commissioncalculator() throws Exception {
		final String methodName = "Test_Negative4_commissioncalculator";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SINC4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		CommissionCalculatorApi commissionCalculatorApi = new CommissionCalculatorApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		commissionCalculatorApi.setContentType(_masterVO.getProperty("contentType"));
		commissionCalculatorRequestPojo.getData().setFromDate("");
		commissionCalculatorApi.addBodyParam(commissionCalculatorRequestPojo);
		commissionCalculatorApi.setExpectedStatusCode(200);
		commissionCalculatorApi.perform();
		commissionCalculatorResponsePojo = commissionCalculatorApi
				.getAPIResponseAsPOJO(CommissionCalculatorResponsePojo.class);
		int statusCode = Integer.parseInt(commissionCalculatorResponsePojo.getDataObject().getTxnstatus());
		String message = commissionCalculatorResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "From Date is mandatory,cannot be NULL.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	// blank to date
	@Test
	public void A_05_Test_Negative5_commissioncalculator() throws Exception {
		final String methodName = "Test_Negative5_commissioncalculator";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SINC5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		CommissionCalculatorApi commissionCalculatorApi = new CommissionCalculatorApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		commissionCalculatorApi.setContentType(_masterVO.getProperty("contentType"));
		commissionCalculatorRequestPojo.getData().setToDate("");
		commissionCalculatorApi.addBodyParam(commissionCalculatorRequestPojo);
		commissionCalculatorApi.setExpectedStatusCode(200);
		commissionCalculatorApi.perform();
		commissionCalculatorResponsePojo = commissionCalculatorApi
				.getAPIResponseAsPOJO(CommissionCalculatorResponsePojo.class);
		int statusCode = Integer.parseInt(commissionCalculatorResponsePojo.getDataObject().getTxnstatus());
		String message = commissionCalculatorResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "To Date is mandatory,cannot be NULL.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	// blank to pin but msisdn filled.
	@Test
	public void A_06_Test_Negative6_commissioncalculator() throws Exception {
		final String methodName = "Test_Negative6_commissioncalculator";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SINC6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		CommissionCalculatorApi commissionCalculatorApi = new CommissionCalculatorApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		commissionCalculatorApi.setContentType(_masterVO.getProperty("contentType"));
		commissionCalculatorRequestPojo.getData().setPin("");
		commissionCalculatorApi.addBodyParam(commissionCalculatorRequestPojo);
		commissionCalculatorApi.setExpectedStatusCode(200);
		commissionCalculatorApi.perform();
		commissionCalculatorResponsePojo = commissionCalculatorApi
				.getAPIResponseAsPOJO(CommissionCalculatorResponsePojo.class);
		int statusCode = Integer.parseInt(commissionCalculatorResponsePojo.getDataObject().getTxnstatus());
		String message = commissionCalculatorResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "PIN can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	// msisdn of invalid length.
	@Test
	public void A_07_Test_Negative7_commissioncalculator() throws Exception {
		final String methodName = "Test_Negative7_commissioncalculator";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SINC7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		CommissionCalculatorApi commissionCalculatorApi = new CommissionCalculatorApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		commissionCalculatorApi.setContentType(_masterVO.getProperty("contentType"));
		commissionCalculatorRequestPojo.getData()
				.setMsisdn(commissionCalculatorRequestPojo.getData().getMsisdn() + "23423423234234234");
		commissionCalculatorApi.addBodyParam(commissionCalculatorRequestPojo);
		commissionCalculatorApi.setExpectedStatusCode(200);
		commissionCalculatorApi.perform();
		commissionCalculatorResponsePojo = commissionCalculatorApi
				.getAPIResponseAsPOJO(CommissionCalculatorResponsePojo.class);
		int statusCode = Integer.parseInt(commissionCalculatorResponsePojo.getDataObject().getTxnstatus());
		String message = commissionCalculatorResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "MSISDN length should lie between 6 and 15.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	// msisdn2 is invalid .
	@Test
	public void A_08_Test_Negative8_commissioncalculator() throws Exception {
		final String methodName = "Test_Negative8_commissioncalculator";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SINC8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		CommissionCalculatorApi commissionCalculatorApi = new CommissionCalculatorApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		commissionCalculatorApi.setContentType(_masterVO.getProperty("contentType"));
		commissionCalculatorRequestPojo.getData()
				.setMsisdn2(commissionCalculatorRequestPojo.getData().getMsisdn() + "232");
		commissionCalculatorApi.addBodyParam(commissionCalculatorRequestPojo);
		commissionCalculatorApi.setExpectedStatusCode(200);
		commissionCalculatorApi.perform();
		commissionCalculatorResponsePojo = commissionCalculatorApi
				.getAPIResponseAsPOJO(CommissionCalculatorResponsePojo.class);
		int statusCode = Integer.parseInt(commissionCalculatorResponsePojo.getDataObject().getTxnstatus());
		String message = commissionCalculatorResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "MSISDN2 length should lie between 6 and 15.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

}
