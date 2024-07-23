package restassuredapi.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import restassuredapi.api.o2cVoucherTransfer.O2cVoucherTransferApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.o2cvouchertransferrequestpojo.Data;
import restassuredapi.pojo.o2cvouchertransferrequestpojo.O2cVoucherTransferRequestPojo;
import restassuredapi.pojo.o2cvouchertransferrequestpojo.PaymentDetail;
import restassuredapi.pojo.o2cvouchertransferrequestpojo.VoucherDetail;
import restassuredapi.pojo.o2cvouchertransferresponsepojo.O2cVoucherTransferResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.O2C_VOUCHER_TRF)
public class O2cVoucherTransfer extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	O2cVoucherTransferRequestPojo o2cVoucherTransferRequestPojo = new O2cVoucherTransferRequestPojo();
	O2cVoucherTransferResponsePojo o2cVoucherTransferResponsePojo = new O2cVoucherTransferResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	 Data data = new Data();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>(); 
	Map<String, Object> headerMap = new HashMap<>();
	public HashMap<String,String> getExcelData(){
		HashMap<String,String> tranferDetails=new HashMap<String,String>();    
		String O2CTransferCode = _masterVO.getProperty("O2cVoucherIniCode");
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
			if (aList.contains(O2CTransferCode)) {
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

    Object[][] Data = new Object[totalObjectCounter][12];

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
                Data[k][8] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
                Data[k][9] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, excelCounter);
                Data[k][10] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, excelCounter);
                Data[k][11] = excelCounter;
               
                k++;
                
            }
        }

    }
    
    tranferDetails.put("MSISDN", Data[0][2].toString());
    tranferDetails.put("To_MSISDN", Data[0][2].toString());
    tranferDetails.put("PIN", Data[0][6].toString());
    tranferDetails.put("LOGIN_ID", Data[0][8].toString());
    tranferDetails.put("PASSWORD", Data[0][9].toString());
    tranferDetails.put("EXTCODE", Data[0][10].toString());

    
    return tranferDetails;
	}
    
	 public void setHeaders() {
			headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
			headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
			headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
			headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
			headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
			headerMap.put("requestGatewayType",_masterVO.getProperty("requestGatewayType") );
			headerMap.put("scope", _masterVO.getProperty("scope"));
			headerMap.put("servicePort", _masterVO.getProperty("servicePort"));

		}
	
	public void setupData() {
		List<VoucherDetail> VoucherDetailsList = new ArrayList<VoucherDetail>();
		List<PaymentDetail> paymentdetailsList  = new ArrayList<PaymentDetail>();
		HashMap<String,String> transferDetails=getExcelData();
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		PaymentDetail paymentDetails = new PaymentDetail();
		paymentDetails.setPaymentinstnumber(randStr.randomNumeric(5));
		paymentDetails.setPaymentdate(currentDate);
		paymentDetails.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
		paymentDetails.setPaymentgatewaytype("NA");
		paymentdetailsList.add(paymentDetails);
		data.setPaymentDetails(paymentdetailsList);
		VoucherDetail voucherDetail = new VoucherDetail();
		voucherDetail.setVoucherType("digital");
		voucherDetail.setVouchersegment("NL");
		voucherDetail.setDenomination("100");
		voucherDetail.setFromSerialNo("");
		voucherDetail.setToSerialNo("");
		VoucherDetailsList.add(voucherDetail);
		data.setVoucherDetails(VoucherDetailsList);
		data.setRefnumber("");
		data.setLanguage(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		data.setRemarks("o2c voucher transfer testing");
		data.setPin(transferDetails.get("PIN"));
		data.setMsisdn2(transferDetails.get("To_MSISDN"));
		o2cVoucherTransferRequestPojo.setData(data);
		oAuthenticationRequestPojo.setIdentifierType("msisdn");
		oAuthenticationRequestPojo.setIdentifierValue(transferDetails.get("MSISDN"));
		oAuthenticationRequestPojo.setPasswordOrSmspin(transferDetails.get("PIN"));
	}
	 protected static String accessToken;
	    
	    @BeforeMethod ()
	    public void BeforeMethod() throws Exception
	    {
	    	if(accessToken==null) {
	    	final String methodName = "Test_OAuthenticationTest";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());

			currentNode.assignCategory("REST");
			setupData();
			setHeaders();
			OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),headerMap);
			oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
			oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
			oAuthenticationAPI.setExpectedStatusCode(200);
			oAuthenticationAPI.perform();
			oAuthenticationResponsePojo = oAuthenticationAPI
					.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
			long statusCode = oAuthenticationResponsePojo.getStatus();
			
			accessToken = oAuthenticationResponsePojo.getToken();
			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
	    	}
	
	    }


	// Successful data with valid data.
	
	    
	    @Test
		public void A_01_Test_success() throws Exception {
			final String methodName = "A_01_Test_success";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERTRF1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			O2cVoucherTransferApi o2CVouchertransferAPI = new O2cVoucherTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CVouchertransferAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CVouchertransferAPI.addBodyParam(o2cVoucherTransferRequestPojo);
			o2CVouchertransferAPI.setExpectedStatusCode(200);
			o2CVouchertransferAPI.perform();
			o2cVoucherTransferResponsePojo = o2CVouchertransferAPI
					.getAPIResponseAsPOJO(O2cVoucherTransferResponsePojo.class);
			int statusCode = Integer.parseInt(o2cVoucherTransferResponsePojo.getStatus());
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}


	    
	@Test
	public void A_02_Test_remarks_blank() throws Exception {
		final String methodName = "A_02_Test_remarks_blank";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERTRF2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		Data o2cDetailsData=o2cVoucherTransferRequestPojo.getData();
		o2cDetailsData.setRemarks("");
		O2cVoucherTransferApi o2CVoucherTransferAPI = new O2cVoucherTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CVoucherTransferAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CVoucherTransferAPI.addBodyParam(o2cVoucherTransferRequestPojo);
		o2CVoucherTransferAPI.setExpectedStatusCode(241132);
		o2CVoucherTransferAPI.perform();
		o2cVoucherTransferResponsePojo = o2CVoucherTransferAPI
				.getAPIResponseAsPOJO(O2cVoucherTransferResponsePojo.class);
		int errorCode = Integer.parseInt(o2cVoucherTransferResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode());
		Assert.assertEquals(241132, errorCode);
		Assertion.assertEquals(Integer.toString(errorCode), "241132");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_03_Test_blank_paymentdate() throws Exception {
		final String methodName = "A_03_Test_blank_paymentdate";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERTRF3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		Data o2cDetailsData=o2cVoucherTransferRequestPojo.getData();
		o2cDetailsData.getPaymentDetails().get(0).setPaymentdate("");
		O2cVoucherTransferApi o2CVoucherTransferAPI = new O2cVoucherTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CVoucherTransferAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CVoucherTransferAPI.addBodyParam(o2cVoucherTransferRequestPojo);
		o2CVoucherTransferAPI.setExpectedStatusCode(1004093);
		o2CVoucherTransferAPI.perform();
		o2cVoucherTransferResponsePojo = o2CVoucherTransferAPI
				.getAPIResponseAsPOJO(O2cVoucherTransferResponsePojo.class);
		int errorCode = Integer.parseInt(o2cVoucherTransferResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode());
		Assert.assertEquals(1004093, errorCode);
		Assertion.assertEquals(Integer.toString(errorCode), "1004093");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_04_Test_blank_paymentinsType() throws Exception {
		final String methodName = "A_04_Test_blank_paymentinsType";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERTRF4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		Data o2cDetailsData=o2cVoucherTransferRequestPojo.getData();
		o2cDetailsData.getPaymentDetails().get(0).setPaymenttype("");
		O2cVoucherTransferApi o2cVoucherTransferApi = new O2cVoucherTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cVoucherTransferApi.setContentType(_masterVO.getProperty("contentType"));
		o2cVoucherTransferApi.addBodyParam(o2cVoucherTransferRequestPojo);
		o2cVoucherTransferApi.setExpectedStatusCode(8148);
		o2cVoucherTransferApi.perform();
		o2cVoucherTransferResponsePojo = o2cVoucherTransferApi
				.getAPIResponseAsPOJO(O2cVoucherTransferResponsePojo.class);
		int errorCode = Integer.parseInt(o2cVoucherTransferResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode());
		Assert.assertEquals(8148, errorCode);
		Assertion.assertEquals(Integer.toString(errorCode), "8148");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_05_Test_blank_paymentinsNum() throws Exception {
		final String methodName = "A_05_Test_blank_paymentinsNum";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERTRF5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		Data o2cDetailsData=o2cVoucherTransferRequestPojo.getData();
		o2cDetailsData.getPaymentDetails().get(0).setPaymentinstnumber("");
		O2cVoucherTransferApi O2cVoucherTransferApi = new O2cVoucherTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		O2cVoucherTransferApi.setContentType(_masterVO.getProperty("contentType"));
		O2cVoucherTransferApi.addBodyParam(o2cVoucherTransferRequestPojo);
		O2cVoucherTransferApi.setExpectedStatusCode(11100);
		O2cVoucherTransferApi.perform();
		o2cVoucherTransferResponsePojo = O2cVoucherTransferApi
				.getAPIResponseAsPOJO(O2cVoucherTransferResponsePojo.class);
		int errorCode = Integer.parseInt(o2cVoucherTransferResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode());
		Assert.assertEquals(11100, errorCode);
		Assertion.assertEquals(Integer.toString(errorCode), "11100");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	public void A_06_Test_multiple_error() throws Exception {
		final String methodName = "A_06_Test_multiple_error";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		Data o2cDetailsData=o2cVoucherTransferRequestPojo.getData();
		o2cDetailsData.getVoucherDetails().get(0).setFromSerialNo("");
		o2cDetailsData.getVoucherDetails().get(0).setVoucherType("");;
		O2cVoucherTransferApi O2cVoucherTransferApi = new O2cVoucherTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		O2cVoucherTransferApi.setContentType(_masterVO.getProperty("contentType"));
		O2cVoucherTransferApi.addBodyParam(o2cVoucherTransferRequestPojo);
		O2cVoucherTransferApi.setExpectedStatusCode(616);
		O2cVoucherTransferApi.perform();
		o2cVoucherTransferResponsePojo = O2cVoucherTransferApi
				.getAPIResponseAsPOJO(O2cVoucherTransferResponsePojo.class);
		int statusCode = Integer.parseInt(o2cVoucherTransferResponsePojo.getStatus());
		Assert.assertEquals(616, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "616");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	public void A_07_Test_multipleRow_error() throws Exception {
		final String methodName = "A_07_Test_multipleRow_error";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		VoucherDetail voucherDetail = new VoucherDetail();
		voucherDetail.setVoucherType("digital");
		voucherDetail.setVouchersegment("");
		voucherDetail.setDenomination("10");
		voucherDetail.setFromSerialNo("");
		voucherDetail.setToSerialNo("");
		Data o2cDetailsData=o2cVoucherTransferRequestPojo.getData();
		o2cDetailsData.getVoucherDetails().get(0).setFromSerialNo("");
		o2cDetailsData.getVoucherDetails().get(0).setVoucherType("");
		o2cDetailsData.getVoucherDetails().add(voucherDetail);
		O2cVoucherTransferApi o2cVoucherTransferApi = new O2cVoucherTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cVoucherTransferApi.setContentType(_masterVO.getProperty("contentType"));
		o2cVoucherTransferApi.addBodyParam(o2cVoucherTransferRequestPojo);
		o2cVoucherTransferApi.setExpectedStatusCode(616);
		o2cVoucherTransferApi.perform();
		o2cVoucherTransferResponsePojo = o2cVoucherTransferApi
				.getAPIResponseAsPOJO(O2cVoucherTransferResponsePojo.class);
		int statusCode = Integer.parseInt(o2cVoucherTransferResponsePojo.getStatus());
		Assert.assertEquals(616, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "616");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	  @Test
	  public void A_08_Test_SerialNo_Pending() throws Exception {
		final String methodName = "A_08_Test_SerialNo_Pending";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERTRF8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		O2cVoucherTransferApi o2CVouchertransferAPI = new O2cVoucherTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CVouchertransferAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CVouchertransferAPI.addBodyParam(o2cVoucherTransferRequestPojo);
		o2CVouchertransferAPI.setExpectedStatusCode(241125);
		o2CVouchertransferAPI.perform();
		o2cVoucherTransferResponsePojo = o2CVouchertransferAPI
				.getAPIResponseAsPOJO(O2cVoucherTransferResponsePojo.class);
		int errorCode = Integer.parseInt(o2cVoucherTransferResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode());
		Assert.assertEquals(241125, errorCode);
		Assertion.assertEquals(Integer.toString(errorCode), "241125");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
