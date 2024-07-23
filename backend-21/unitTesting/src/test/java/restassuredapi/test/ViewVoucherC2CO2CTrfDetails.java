package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.ViewVoucherC2CO2CTrfDetails.ViewVoucherC2CO2CTrfDetailsAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.viewvoucherC2cO2ctrfdetails.Slablist;
import restassuredapi.pojo.viewvoucherC2cO2ctrfdetails.ViewVoucherC2cO2cTrfDetailsReqPojo;
import restassuredapi.pojo.viewvoucherC2cO2ctrfdetailsresp.ViewVoucherC2cO2cTrfDetailsRespPojo;
@ModuleManager(name = Module.VIEW_VCR_O2C_C2C_TRF_DETAILS)
public class ViewVoucherC2CO2CTrfDetails extends BaseTest {
	static String moduleCode;
	String[] details;
	ViewVoucherC2cO2cTrfDetailsReqPojo viewVoucherC2cO2cTrfDetailsReqPojo = new ViewVoucherC2cO2cTrfDetailsReqPojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	ViewVoucherC2cO2cTrfDetailsRespPojo viewVoucherC2cO2cTrfDetailsRespPojo = new ViewVoucherC2cO2cTrfDetailsRespPojo();

	//Data data = new Data();
	Login login = new Login();
	
	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rCount = ExcelUtility.getRowCount();
		ArrayList<Integer> a = new ArrayList<>();
		for(int i=1;i<=rCount;i++) {
			if(ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("DT")|| ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("D")){
				a.add(i);
			}
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();


		Object[][] Data = new Object[rowCount][11];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i); //from
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
			Data[j][8] = ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, a.get(i%a.size()));
			Data[j][9] = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, a.get(i%a.size()) );
			String prodName = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, a.get(i%a.size()) );
             String productID=DBHandler.AccessHandler.fetchProductID(prodName);
			String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
			Data[j][10] = voucherSegment;
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			j++;
		}

		return Data;

	}


	public void setupData(String data1, String data2, String data3,String vouchertype,String Mrp,String segment) {
		viewVoucherC2cO2cTrfDetailsReqPojo.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		viewVoucherC2cO2cTrfDetailsReqPojo.setCommissionProfileID(Integer.parseInt(DBHandler.AccessHandler.getCommProfileID(data3)));
		viewVoucherC2cO2cTrfDetailsReqPojo.setCommissionProfileVersion(1);
		viewVoucherC2cO2cTrfDetailsReqPojo.setCbcflag(_masterVO.getProperty("status_Y"));
		viewVoucherC2cO2cTrfDetailsReqPojo.setTransferSubType(_masterVO.getProperty("transferTypeVoucher"));
		viewVoucherC2cO2cTrfDetailsReqPojo.setTransferType(_masterVO.getProperty("C2CType"));
		viewVoucherC2cO2cTrfDetailsReqPojo.setRequestType(_masterVO.getProperty("RequestTypeBUY"));
		viewVoucherC2cO2cTrfDetailsReqPojo.setDualCommission(_masterVO.getProperty("DualCommission"));
		viewVoucherC2cO2cTrfDetailsReqPojo.setLanguage1(Integer.parseInt(_masterVO.getProperty("languageCode0")));
		Slablist slablist = new Slablist();
		List<Slablist> list = new ArrayList<Slablist>();
		slablist.setQty(100);
		slablist.setVoucherMrp(Integer.parseInt(Mrp));
		slablist.setSegmentType(segment);
		slablist.setVoucherType(vouchertype);
		list.add(slablist);
		viewVoucherC2cO2cTrfDetailsReqPojo.setSlablist(list);
	}

	Map<String, Object> headerMap = new HashMap<String, Object>();

	public void setHeaders() {
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
		headerMap.put("requestGatewayType", _masterVO.getProperty("requestGatewayType"));
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);


	}

	// Successful data with valid data.

	protected static String accessToken;


	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {
		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));

		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1, data2);
		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
		oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
		oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
		oAuthenticationAPI.setExpectedStatusCode(200);
		oAuthenticationAPI.perform();
		oAuthenticationResponsePojo = oAuthenticationAPI
				.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
		long statusCode = oAuthenticationResponsePojo.getStatus();

		accessToken = oAuthenticationResponsePojo.getToken();
		org.testng.Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}
	
	//API is working successfully for c2c buy txn
	@Test(dataProvider = "userData")
	public void A_01_Test_success_ViewTaxCalculationVoucher(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String comProfile,String vouchertype,String voucherMrp,String segment) throws Exception {
		final String methodName = "Test_Positive_ViewTaxCalculationVoucherStock";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VTXNCALVIEW1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn, PIN, comProfile,vouchertype,voucherMrp,segment);
		ViewVoucherC2CO2CTrfDetailsAPI viewVoucherC2CO2CTrfDetailsAPI = new ViewVoucherC2CO2CTrfDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		viewVoucherC2cO2cTrfDetailsReqPojo.getSlablist().get(0).setVoucherType(vouchertype);
		viewVoucherC2cO2cTrfDetailsReqPojo.getSlablist().get(0).setSegmentType(segment);
		viewVoucherC2cO2cTrfDetailsReqPojo.getSlablist().get(0).setVoucherMrp(Integer.parseInt(voucherMrp));
		viewVoucherC2CO2CTrfDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		viewVoucherC2CO2CTrfDetailsAPI.addBodyParam(viewVoucherC2cO2cTrfDetailsReqPojo);
		viewVoucherC2CO2CTrfDetailsAPI.setExpectedStatusCode(200);
		viewVoucherC2CO2CTrfDetailsAPI.perform();
		viewVoucherC2cO2cTrfDetailsRespPojo = viewVoucherC2CO2CTrfDetailsAPI.getAPIResponseAsPOJO(ViewVoucherC2cO2cTrfDetailsRespPojo.class);
		//int statusCode = Integer.parseInt(viewVoucherC2cO2cTrfDetailsRespPojo.getDataObject().getTxnstatus());
		String msgcode  =viewVoucherC2cO2cTrfDetailsRespPojo.getMessageCode();
		int statusCode = viewVoucherC2cO2cTrfDetailsRespPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(msgcode, "9020");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	//TO check validation message if netwrk code is given or not
	@Test(dataProvider = "userData")
	public void A_02_Test_success_ViewTaxCalculationVoucher(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String comProfile,String vouchertype,String voucherMrp,String segment) throws Exception {
		final String methodName = "A_02_Test_success_ViewTaxCalculationVoucher";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VTXNCALVIEW2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn, PIN, comProfile,vouchertype,voucherMrp,segment);
		viewVoucherC2cO2cTrfDetailsReqPojo.setExtnwcode("");
		ViewVoucherC2CO2CTrfDetailsAPI viewVoucherC2CO2CTrfDetailsAPI = new ViewVoucherC2CO2CTrfDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		viewVoucherC2CO2CTrfDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		viewVoucherC2CO2CTrfDetailsAPI.addBodyParam(viewVoucherC2cO2cTrfDetailsReqPojo);
		viewVoucherC2CO2CTrfDetailsAPI.setExpectedStatusCode(400);
		viewVoucherC2CO2CTrfDetailsAPI.perform();
		viewVoucherC2cO2cTrfDetailsRespPojo = viewVoucherC2CO2CTrfDetailsAPI.getAPIResponseAsPOJO(ViewVoucherC2cO2cTrfDetailsRespPojo.class);
		//int statusCode = Integer.parseInt(viewVoucherC2cO2cTrfDetailsRespPojo.getDataObject().getTxnstatus());
		String msgcode  =viewVoucherC2cO2cTrfDetailsRespPojo.getMessageCode();
		int statusCode = viewVoucherC2cO2cTrfDetailsRespPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(msgcode, "1004004");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	//TO check validation message if voucher type is not given 
		@Test(dataProvider = "userData")
		public void A_03_Test_success_ViewTaxCalculationVoucher(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String comProfile,String vouchertype,String voucherMrp,String segment) throws Exception {
			final String methodName = "A_02_Test_success_ViewTaxCalculationVoucher";
			Log.startTestCase(methodName);
			if (_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password, categoryName);
			else if (_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN, categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VTXNCALVIEW3");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
			currentNode.assignCategory("REST");
			setupData(msisdn, PIN, comProfile,vouchertype,voucherMrp,segment);
			viewVoucherC2cO2cTrfDetailsReqPojo.getSlablist().get(0).setVoucherType("");
			ViewVoucherC2CO2CTrfDetailsAPI viewVoucherC2CO2CTrfDetailsAPI = new ViewVoucherC2CO2CTrfDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			viewVoucherC2CO2CTrfDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
			viewVoucherC2CO2CTrfDetailsAPI.addBodyParam(viewVoucherC2cO2cTrfDetailsReqPojo);
			viewVoucherC2CO2CTrfDetailsAPI.setExpectedStatusCode(400);
			viewVoucherC2CO2CTrfDetailsAPI.perform();
			viewVoucherC2cO2cTrfDetailsRespPojo = viewVoucherC2CO2CTrfDetailsAPI.getAPIResponseAsPOJO(ViewVoucherC2cO2cTrfDetailsRespPojo.class);
			//int statusCode = Integer.parseInt(viewVoucherC2cO2cTrfDetailsRespPojo.getDataObject().getTxnstatus());
			String msgcode  =viewVoucherC2cO2cTrfDetailsRespPojo.getMessageCode();
			int statusCode = viewVoucherC2cO2cTrfDetailsRespPojo.getStatus();
			Assert.assertEquals(statusCode, 400);
			Assertion.assertEquals(msgcode, "5022");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}

}
