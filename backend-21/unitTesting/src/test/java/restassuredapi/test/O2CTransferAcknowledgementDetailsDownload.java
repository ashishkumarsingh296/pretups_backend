package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import restassuredapi.api.o2CTransferAcknowledgementDetailsDownload.O2CTransferAcknowledgementDetailsDownloadApi;
import restassuredapi.pojo.o2Ctransferacknowledgementdetailsdownloadrequestpojo.O2CTransferAcknowledgementDetailsDownloadRequestPojo;
import restassuredapi.pojo.o2Ctransferacknowledgementdetailsdownloadresponsepojo.O2CTransferAcknowledgementDetailsDownloadResponsePojo;
import restassuredapi.pojo.o2Ctransferacknowledgementdetailsdownloadrequestpojo.Data;
import restassuredapi.pojo.o2Ctransferacknowledgementdetailsdownloadrequestpojo.DispHeaderColumn;

@ModuleManager(name = Module.O2C_TRANSFER_ACKNOWLEDGEMENT_DETAILS_DOWNLOAD)
public class O2CTransferAcknowledgementDetailsDownload extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     
     String fromDate = df.format(DateUtils.addDays(new Date(), -120));
     String toDate = df.format(DateUtils.addDays(new Date(), -1));
       
	static String moduleCode;
	O2CTransferAcknowledgementDetailsDownloadRequestPojo o2CTransferAcknowledgementDetailsDownloadRequestPojo = new O2CTransferAcknowledgementDetailsDownloadRequestPojo();
	O2CTransferAcknowledgementDetailsDownloadResponsePojo o2CTransferAcknowledgementDetailsDownloadResponsePojo = new O2CTransferAcknowledgementDetailsDownloadResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	Login login = new Login();
	
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][8];
		int j=0;
		for(int i=1;i<=rowCount;i++) {
			Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
			j++;
		}
		return Data;
	}

	Map<String, Object> headerMap = new HashMap<String, Object>();
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

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}

	public void setupData() {
		
		DispHeaderColumn dispHeaderColumn = new DispHeaderColumn();
        dispHeaderColumn.setColumnName("dateTime");
        dispHeaderColumn.setDisplayName("Date & Time");
       
        DispHeaderColumn dispHeaderColumn1 = new DispHeaderColumn();
        dispHeaderColumn1.setColumnName("transactionID");
        dispHeaderColumn1.setDisplayName("Transaction ID");
       
        DispHeaderColumn dispHeaderColumn2 = new DispHeaderColumn();
        dispHeaderColumn2.setColumnName("userName");
        dispHeaderColumn2.setDisplayName("User name");
       
        DispHeaderColumn dispHeaderColumn3 = new DispHeaderColumn();
        dispHeaderColumn3.setColumnName("status");
        dispHeaderColumn3.setDisplayName("Status");
       
        DispHeaderColumn dispHeaderColumn4 = new DispHeaderColumn();
        dispHeaderColumn4.setColumnName("domain");
        dispHeaderColumn4.setDisplayName("Domain");
       
        DispHeaderColumn dispHeaderColumn5 = new DispHeaderColumn();
        dispHeaderColumn5.setColumnName("category");
        dispHeaderColumn5.setDisplayName("Category");
       
        DispHeaderColumn dispHeaderColumn6 = new DispHeaderColumn();
        dispHeaderColumn6.setColumnName("geography");
        dispHeaderColumn6.setDisplayName("GEOGRAPHY");
       
        DispHeaderColumn dispHeaderColumn7 = new DispHeaderColumn();
        dispHeaderColumn7.setColumnName("mobileNumber");
        dispHeaderColumn7.setDisplayName("Mobile number");
       
        DispHeaderColumn dispHeaderColumn8 = new DispHeaderColumn();
        dispHeaderColumn8.setColumnName("networkName");
        dispHeaderColumn8.setDisplayName("Network name");
       
        DispHeaderColumn dispHeaderColumn9 = new DispHeaderColumn();
        dispHeaderColumn9.setColumnName("commissionProfile");
        dispHeaderColumn9.setDisplayName("Commission profile");
       
        DispHeaderColumn dispHeaderColumn10 = new DispHeaderColumn();
        dispHeaderColumn10.setColumnName("transferProfile");
        dispHeaderColumn10.setDisplayName("Transfer profile");
       
        DispHeaderColumn dispHeaderColumn11 = new DispHeaderColumn();
        dispHeaderColumn11.setColumnName("transferType");
        dispHeaderColumn11.setDisplayName("Transfer type");
       
        DispHeaderColumn dispHeaderColumn12 = new DispHeaderColumn();
        dispHeaderColumn12.setColumnName("transferCategory");
        dispHeaderColumn12.setDisplayName("Transfer category");
       
        DispHeaderColumn dispHeaderColumn13 = new DispHeaderColumn();
        dispHeaderColumn13.setColumnName("transNumberExternal");
        dispHeaderColumn13.setDisplayName("Transfer no (External)");
       
        DispHeaderColumn dispHeaderColumn14 = new DispHeaderColumn();
        dispHeaderColumn14.setColumnName("transDateExternal");
        dispHeaderColumn14.setDisplayName("Transfer date (External)");
              
        DispHeaderColumn dispHeaderColumn15 = new DispHeaderColumn();
        dispHeaderColumn15.setColumnName("referenceNumber");
        dispHeaderColumn15.setDisplayName("Reference number");
       
        DispHeaderColumn dispHeaderColumn16 = new DispHeaderColumn();
        dispHeaderColumn16.setColumnName("erpCode");
        dispHeaderColumn16.setDisplayName("ERP code");
       
        DispHeaderColumn dispHeaderColumn17 = new DispHeaderColumn();
        dispHeaderColumn17.setColumnName("address");
        dispHeaderColumn17.setDisplayName("Address");
       
        DispHeaderColumn dispHeaderColumn18 = new DispHeaderColumn();
        dispHeaderColumn18.setColumnName("productShortCode");
        dispHeaderColumn18.setDisplayName("Product short code");
       
        DispHeaderColumn dispHeaderColumn19 = new DispHeaderColumn();
        dispHeaderColumn19.setColumnName("productName");
        dispHeaderColumn19.setDisplayName("Product Name");
        
        DispHeaderColumn dispHeaderColumn20 = new DispHeaderColumn();
        dispHeaderColumn20.setColumnName("denomination");
        dispHeaderColumn20.setDisplayName("Denomination");
        
        DispHeaderColumn dispHeaderColumn21 = new DispHeaderColumn();
        dispHeaderColumn21.setColumnName("quantity");
        dispHeaderColumn21.setDisplayName("Quantity");
        
        DispHeaderColumn dispHeaderColumn22 = new DispHeaderColumn();
        dispHeaderColumn22.setColumnName("approvedQuantity");
        dispHeaderColumn22.setDisplayName("Approved quantity");
        
        DispHeaderColumn dispHeaderColumn23 = new DispHeaderColumn();
        dispHeaderColumn23.setColumnName("level1ApprovedQuantity");
        dispHeaderColumn23.setDisplayName("Level1 approved quantity");
        
        DispHeaderColumn dispHeaderColumn24 = new DispHeaderColumn();
        dispHeaderColumn24.setColumnName("level2ApprovedQuantity");
        dispHeaderColumn24.setDisplayName("Level2 approved quantity");
        
        DispHeaderColumn dispHeaderColumn25 = new DispHeaderColumn();
        dispHeaderColumn25.setColumnName("level3ApprovedQuantity");
        dispHeaderColumn25.setDisplayName("Level3 approved quantity");
        
        DispHeaderColumn dispHeaderColumn26 = new DispHeaderColumn();
        dispHeaderColumn26.setColumnName("tax1Rate");
        dispHeaderColumn26.setDisplayName("Tax1 Rate");
        
        DispHeaderColumn dispHeaderColumn27 = new DispHeaderColumn();
        dispHeaderColumn27.setColumnName("tax1Type");
        dispHeaderColumn27.setDisplayName("Tax1 type");
        
        DispHeaderColumn dispHeaderColumn28 = new DispHeaderColumn();
        dispHeaderColumn28.setColumnName("tax1Amount");
        dispHeaderColumn28.setDisplayName("Tax1 amount");
        
        DispHeaderColumn dispHeaderColumn29 = new DispHeaderColumn();
        dispHeaderColumn29.setColumnName("tax2Rate");
        dispHeaderColumn29.setDisplayName("Tax2 Rate");
        
        DispHeaderColumn dispHeaderColumn30 = new DispHeaderColumn();
        dispHeaderColumn30.setColumnName("tax2Type");
        dispHeaderColumn30.setDisplayName("Tax2 type");
        
        DispHeaderColumn dispHeaderColumn31 = new DispHeaderColumn();
        dispHeaderColumn31.setColumnName("tax2Amount");
        dispHeaderColumn31.setDisplayName("Tax2 amount");
        
        DispHeaderColumn dispHeaderColumn32 = new DispHeaderColumn();
        dispHeaderColumn32.setColumnName("tax3Rate");
        dispHeaderColumn32.setDisplayName("Tax3 rate");
        
        DispHeaderColumn dispHeaderColumn33 = new DispHeaderColumn();
        dispHeaderColumn33.setColumnName("tds");
        dispHeaderColumn33.setDisplayName("TDS");
        
        DispHeaderColumn dispHeaderColumn34 = new DispHeaderColumn();
        dispHeaderColumn34.setColumnName("commisionRate");
        dispHeaderColumn34.setDisplayName("Commission Rate");
        
        DispHeaderColumn dispHeaderColumn35 = new DispHeaderColumn();
        dispHeaderColumn35.setColumnName("commisionType");
        dispHeaderColumn35.setDisplayName("Commission Type");
        
        DispHeaderColumn dispHeaderColumn36 = new DispHeaderColumn();
        dispHeaderColumn36.setColumnName("commisionAmount");
        dispHeaderColumn36.setDisplayName("Commission amount");
        
        DispHeaderColumn dispHeaderColumn37 = new DispHeaderColumn();
        dispHeaderColumn37.setColumnName("receiverCreditQuantity");
        dispHeaderColumn37.setDisplayName("Receiver credit quantity");
        
        DispHeaderColumn dispHeaderColumn38 = new DispHeaderColumn();
        dispHeaderColumn38.setColumnName("cbcRate");
        dispHeaderColumn38.setDisplayName("CBC Rate");
        
        DispHeaderColumn dispHeaderColumn39 = new DispHeaderColumn();
        dispHeaderColumn39.setColumnName("cbcType");
        dispHeaderColumn39.setDisplayName("CBC type");
        
        DispHeaderColumn dispHeaderColumn40 = new DispHeaderColumn();
        dispHeaderColumn40.setColumnName("cbcAmount");
        dispHeaderColumn40.setDisplayName("CBC amount");
        
        DispHeaderColumn dispHeaderColumn41 = new DispHeaderColumn();
        dispHeaderColumn41.setColumnName("denominationAmount");
        dispHeaderColumn41.setDisplayName("Denomination amount");
        
        DispHeaderColumn dispHeaderColumn42 = new DispHeaderColumn();
        dispHeaderColumn42.setColumnName("payableAmount");
        dispHeaderColumn42.setDisplayName("Payable amount");
        
        DispHeaderColumn dispHeaderColumn43 = new DispHeaderColumn();
        dispHeaderColumn43.setColumnName("netAmount");
        dispHeaderColumn43.setDisplayName("Net amount");
        
        DispHeaderColumn dispHeaderColumn44 = new DispHeaderColumn();
        dispHeaderColumn44.setColumnName("paymentInstrumentNumber");
        dispHeaderColumn44.setDisplayName("Payment instrument number");
        
        DispHeaderColumn dispHeaderColumn45 = new DispHeaderColumn();
        dispHeaderColumn45.setColumnName("paymentInstrumentDate");
        dispHeaderColumn45.setDisplayName("Payment instrument date");
        
        DispHeaderColumn dispHeaderColumn46 = new DispHeaderColumn();
        dispHeaderColumn46.setColumnName("paymentInstrumentAmount");
        dispHeaderColumn46.setDisplayName("Payment instrument amount");
        
        DispHeaderColumn dispHeaderColumn47 = new DispHeaderColumn();
        dispHeaderColumn47.setColumnName("paymentMode");
        dispHeaderColumn47.setDisplayName("Payment mode");
        
        DispHeaderColumn dispHeaderColumn48 = new DispHeaderColumn();
        dispHeaderColumn48.setColumnName("firstApprovedRemarks");
        dispHeaderColumn48.setDisplayName("First approved remarks");
        
        DispHeaderColumn dispHeaderColumn49 = new DispHeaderColumn();
        dispHeaderColumn49.setColumnName("secondApprovedRemarks");
        dispHeaderColumn49.setDisplayName("Second approved remarks");
        
        DispHeaderColumn dispHeaderColumn50 = new DispHeaderColumn();
        dispHeaderColumn50.setColumnName("thirdApprovedRemarks");
        dispHeaderColumn50.setDisplayName("Third approved remarks");
        
        DispHeaderColumn dispHeaderColumn51 = new DispHeaderColumn();
        dispHeaderColumn51.setColumnName("voucherBatchNumber");
        dispHeaderColumn51.setDisplayName("Voucher batch number");
        
        DispHeaderColumn dispHeaderColumn52 = new DispHeaderColumn();
        dispHeaderColumn52.setColumnName("vomsProductName");
        dispHeaderColumn52.setDisplayName("Voucher product name");
        
        DispHeaderColumn dispHeaderColumn53 = new DispHeaderColumn();
        dispHeaderColumn53.setColumnName("batchType");
        dispHeaderColumn53.setDisplayName("Batch type");
        
        DispHeaderColumn dispHeaderColumn54 = new DispHeaderColumn();
        dispHeaderColumn54.setColumnName("totalNoofVouchers");
        dispHeaderColumn54.setDisplayName("Total no. of Vouchers");
        
        DispHeaderColumn dispHeaderColumn55 = new DispHeaderColumn();
        dispHeaderColumn55.setColumnName("fromSerialNumber");
        dispHeaderColumn55.setDisplayName("From serial number");
        
        DispHeaderColumn dispHeaderColumn56 = new DispHeaderColumn();
        dispHeaderColumn56.setColumnName("toSerialNumber");
        dispHeaderColumn56.setDisplayName("To serial number");
                           
        List<DispHeaderColumn> dispHeaderColumnList= new ArrayList<DispHeaderColumn>();
  
        dispHeaderColumnList.add(dispHeaderColumn);
        dispHeaderColumnList.add(dispHeaderColumn1);
        dispHeaderColumnList.add(dispHeaderColumn2);
        dispHeaderColumnList.add(dispHeaderColumn3);
        dispHeaderColumnList.add(dispHeaderColumn4);
        dispHeaderColumnList.add(dispHeaderColumn5);
        dispHeaderColumnList.add(dispHeaderColumn6);
        dispHeaderColumnList.add(dispHeaderColumn7);
        dispHeaderColumnList.add(dispHeaderColumn8);
        dispHeaderColumnList.add(dispHeaderColumn9);
        dispHeaderColumnList.add(dispHeaderColumn10);
        dispHeaderColumnList.add(dispHeaderColumn11);
        dispHeaderColumnList.add(dispHeaderColumn12);
        dispHeaderColumnList.add(dispHeaderColumn13);
        dispHeaderColumnList.add(dispHeaderColumn14);
        dispHeaderColumnList.add(dispHeaderColumn15);
        dispHeaderColumnList.add(dispHeaderColumn16);
        dispHeaderColumnList.add(dispHeaderColumn17);
        dispHeaderColumnList.add(dispHeaderColumn18);
        dispHeaderColumnList.add(dispHeaderColumn19);
        dispHeaderColumnList.add(dispHeaderColumn20);
        dispHeaderColumnList.add(dispHeaderColumn21);
        dispHeaderColumnList.add(dispHeaderColumn22);
        dispHeaderColumnList.add(dispHeaderColumn23);
        dispHeaderColumnList.add(dispHeaderColumn24);
        dispHeaderColumnList.add(dispHeaderColumn25);
        dispHeaderColumnList.add(dispHeaderColumn26);
        dispHeaderColumnList.add(dispHeaderColumn27);
        dispHeaderColumnList.add(dispHeaderColumn28);
        dispHeaderColumnList.add(dispHeaderColumn29);
        dispHeaderColumnList.add(dispHeaderColumn30);
        dispHeaderColumnList.add(dispHeaderColumn31);
        dispHeaderColumnList.add(dispHeaderColumn32);
        dispHeaderColumnList.add(dispHeaderColumn33);
        dispHeaderColumnList.add(dispHeaderColumn34);
        dispHeaderColumnList.add(dispHeaderColumn35);
        dispHeaderColumnList.add(dispHeaderColumn36);
        dispHeaderColumnList.add(dispHeaderColumn37);
        dispHeaderColumnList.add(dispHeaderColumn38);
        dispHeaderColumnList.add(dispHeaderColumn39);
        dispHeaderColumnList.add(dispHeaderColumn40);
        dispHeaderColumnList.add(dispHeaderColumn41);
        dispHeaderColumnList.add(dispHeaderColumn42);
        dispHeaderColumnList.add(dispHeaderColumn43);
        dispHeaderColumnList.add(dispHeaderColumn44);
        dispHeaderColumnList.add(dispHeaderColumn45);
        dispHeaderColumnList.add(dispHeaderColumn46);
        dispHeaderColumnList.add(dispHeaderColumn47);
        dispHeaderColumnList.add(dispHeaderColumn48);
        dispHeaderColumnList.add(dispHeaderColumn49);
        dispHeaderColumnList.add(dispHeaderColumn50);
        dispHeaderColumnList.add(dispHeaderColumn51);
        dispHeaderColumnList.add(dispHeaderColumn52);
        dispHeaderColumnList.add(dispHeaderColumn53);
        dispHeaderColumnList.add(dispHeaderColumn54);
        dispHeaderColumnList.add(dispHeaderColumn55);
        dispHeaderColumnList.add(dispHeaderColumn56);

		data.setDispHeaderColumnList(dispHeaderColumnList);
		data.setDistributionType("VOUCHER");
		data.setTransactionID("ALL");
		o2CTransferAcknowledgementDetailsDownloadRequestPojo.setData(data);
	}
	
	// Successful data with valid data.

	protected static String accessToken;

	public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
	{
		//if(accessToken==null) {
		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1,data2);
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

		// Positive Scenario
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-001")
		public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTADDOWNLOAD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferAcknowledgementDetailsDownloadApi o2CTransferAcknowledgementDetailsDownloadApi = new O2CTransferAcknowledgementDetailsDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferAcknowledgementDetailsDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		
		o2CTransferAcknowledgementDetailsDownloadApi.addBodyParam(o2CTransferAcknowledgementDetailsDownloadRequestPojo);
		o2CTransferAcknowledgementDetailsDownloadApi.setExpectedStatusCode(200);
		o2CTransferAcknowledgementDetailsDownloadApi.perform();
		o2CTransferAcknowledgementDetailsDownloadResponsePojo = o2CTransferAcknowledgementDetailsDownloadApi
				.getAPIResponseAsPOJO(O2CTransferAcknowledgementDetailsDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(o2CTransferAcknowledgementDetailsDownloadResponsePojo.getStatus());
		
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// Positive Scenario 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-002")
		public void A_02_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTADDOWNLOAD2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferAcknowledgementDetailsDownloadApi o2CTransferAcknowledgementDetailsDownloadApi = new O2CTransferAcknowledgementDetailsDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferAcknowledgementDetailsDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		
		o2CTransferAcknowledgementDetailsDownloadRequestPojo.getData().setDistributionType("STOCK");

		o2CTransferAcknowledgementDetailsDownloadApi.addBodyParam(o2CTransferAcknowledgementDetailsDownloadRequestPojo);
		o2CTransferAcknowledgementDetailsDownloadApi.setExpectedStatusCode(200);
		o2CTransferAcknowledgementDetailsDownloadApi.perform();
		o2CTransferAcknowledgementDetailsDownloadResponsePojo = o2CTransferAcknowledgementDetailsDownloadApi
				.getAPIResponseAsPOJO(O2CTransferAcknowledgementDetailsDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(o2CTransferAcknowledgementDetailsDownloadResponsePojo.getStatus());
		
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		//TransactionID provided is blank 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-003")
		public void A_03_Test_Negative3_O2CTransferAcknowledgementDetailsDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_Negative3_O2CTransferAcknowledgementDetailsDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTADDOWNLOAD3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferAcknowledgementDetailsDownloadApi o2CTransferAcknowledgementDetailsDownloadApi = new O2CTransferAcknowledgementDetailsDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferAcknowledgementDetailsDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		o2CTransferAcknowledgementDetailsDownloadRequestPojo.getData().setTransactionID("");
				
		o2CTransferAcknowledgementDetailsDownloadApi.addBodyParam(o2CTransferAcknowledgementDetailsDownloadRequestPojo);
		o2CTransferAcknowledgementDetailsDownloadApi.setExpectedStatusCode(400);
		o2CTransferAcknowledgementDetailsDownloadApi.perform();
		o2CTransferAcknowledgementDetailsDownloadResponsePojo = o2CTransferAcknowledgementDetailsDownloadApi
				.getAPIResponseAsPOJO(O2CTransferAcknowledgementDetailsDownloadResponsePojo.class);
		String errorCode = o2CTransferAcknowledgementDetailsDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); // need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
}