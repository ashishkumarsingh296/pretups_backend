package restassuredapi.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cstock.C2CBuyStockTransferInitiateAPI;
import restassuredapi.api.c2cstock.C2CStockApprovalAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.C2CBuyStockInitiateRequestPojo;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Data;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Paymentdetail;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Product;
import restassuredapi.pojo.c2cbuystockinitiateresponsepojo.C2CBuyStockInitiateResponsePojo;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.C2CStockApprovalRequestPojo;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.DataApproval;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.PaymentdetailApproval;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.ProductApproval;
import restassuredapi.pojo.c2cstockapprovalresponsepojo.C2CStockApprovalResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;


@ModuleManager(name = Module.REST_C2C_STOCK_INITIATE)
public class C2CStockInitiateAndApproval extends BaseTest {
	
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
    
	static String moduleCode;
	C2CStockApprovalRequestPojo c2CStockApprovalRequestPojo = new C2CStockApprovalRequestPojo();
	C2CStockApprovalResponsePojo c2CStockApprovalResponsePojo = new C2CStockApprovalResponsePojo();
	C2CBuyStockInitiateRequestPojo c2CBuyStockInitiateRequestPojo = new C2CBuyStockInitiateRequestPojo();
	C2CBuyStockInitiateResponsePojo c2CBuyStockInitiateResponsePojo = new C2CBuyStockInitiateResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	Data data = new Data();
	DataApproval dataApproval =new DataApproval();
	Login login = new Login();
	NetworkAdminHomePage homepage = new NetworkAdminHomePage(driver);
	Product product= null;


	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String C2CInitiateCode = _masterVO.getProperty("C2CInititateCode");
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
			if (aList.contains(C2CInitiateCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();
		int totalObjectCounter = 0;
		for (int i = 0; i < alist2.size(); i++) {
			int categorySizeCounter = 0;
			for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(i))) {
					categorySizeCounter++;
				}
			}
			categorySize.add("" + categorySizeCounter);
			totalObjectCounter = totalObjectCounter + categorySizeCounter;
		}
		
		Object[][] Data = new Object[totalObjectCounter][11];

        for (int j = 0, k = 0; j < alist1.size(); j++) {

            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            int excelRowSize = ExcelUtility.getRowCount();
            String ChannelUserLoginId = null;
            String ChannelUserMSISDN = null;
            String ChannelUserPIN = null;
            String ChannelUserPASS = null;
            
            for (int i = 1; i <= excelRowSize; i++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist2.get(j))) {
                	ChannelUserMSISDN=ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                    ChannelUserLoginId=ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                    ChannelUserPIN=ExcelUtility.getCellData(0, ExcelI.PIN, i);
                    ChannelUserPASS=ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                    break;
                }
            }

            for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(j))) {
                    Data[k][0] = alist2.get(j); //fromCategoryName
                    Data[k][1] = alist1.get(j); //toCategoryName
                    Data[k][2] = ChannelUserMSISDN; //fromMsisdn
                    Data[k][3] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                    Data[k][4] = ChannelUserLoginId;
                    Data[k][5] = ChannelUserPIN;
                    Data[k][6] = ChannelUserPASS;
                    Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                    Data[k][8] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                    Data[k][9] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
                    Data[k][10] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, excelCounter);
                    k++;
                }
            }

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
	
	public void setupData(String fromMsisdn,String fromPin,String toMsisdn) {
		ArrayList<Product> products = new ArrayList<Product>();
		ArrayList<Paymentdetail> paymentDetails=new ArrayList<Paymentdetail>();
	    Paymentdetail paymentdetail= new Paymentdetail();

		RandomGeneration randomGeneration = new RandomGeneration();
		
		data.setDate("");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setMsisdn(fromMsisdn);
		data.setPin(fromPin);
		data.setLoginid("");
		data.setPassword("");
		data.setExtcode("");
		data.setMsisdn2(toMsisdn);
		data.setLoginid2("");
		data.setExtcode2("");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
		product = new Product();
		String productShortCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i);
		String productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
		product.setProductcode(productShortCode);
		String userBalance = DBHandler.AccessHandler.getUserBalance(productCode, fromMsisdn);
		int prBalance= (int) Double.parseDouble(userBalance);
		int quantity=(int) (prBalance*0.01*0.01);
		product.setQty(String.valueOf(quantity));
		products.add(product);
		}
		data.setProducts(products);
		data.setRefnumber("");
			
		paymentdetail.setPaymentinstnumber(randomGeneration.randomNumeric(5));
		paymentdetail.setPaymentdate(currentDate);
		paymentdetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
		
		// array list of paymentdetails
		paymentDetails.add(paymentdetail);	
		data.setLanguage1(_masterVO.getProperty("languageCode0"));
	
		//sending array list
		data.setPaymentdetails(paymentDetails);
		data.setRemarks("Automation REST API");
		c2CBuyStockInitiateRequestPojo.setData(data);

	}
	
	public void setupC2CApproval(String fromMsisdn,String fromPin,String txnId,List<Product> productTransferList) {
		
		RandomGeneration randomGeneration = new RandomGeneration();
		dataApproval.setDate(currentDate);
		dataApproval.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		dataApproval.setMsisdn(fromMsisdn); //from msisdn
		dataApproval.setPin(fromPin); //from pin
		dataApproval.setLoginid("");
		dataApproval.setPassword("");
		dataApproval.setExtcode("");
		dataApproval.setTxnid(txnId);
		
		ArrayList<ProductApproval> productList = new ArrayList<ProductApproval>();
		for(int i=0;i<productTransferList.size();i++) {
			ProductApproval productApproval = new ProductApproval();
			productApproval.setProductcode(productTransferList.get(i).getProductcode());
			productApproval.setQty(productTransferList.get(i).getQty());
			
			productList.add(productApproval);
		}
	
		dataApproval.setProducts(productList);
		dataApproval.setRefnumber("");
		
		ArrayList<PaymentdetailApproval> paymentDetailList = new ArrayList<PaymentdetailApproval>();
		PaymentdetailApproval paymentDetail = new PaymentdetailApproval();
		paymentDetail.setPaymentinstnumber(randomGeneration.randomNumeric(5));
		paymentDetail.setPaymentdate(currentDate);
		paymentDetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
		paymentDetailList.add(paymentDetail);
		dataApproval.setPaymentdetails(paymentDetailList);
		dataApproval.setCurrentstatus(_masterVO.getProperty("aprvLvl1"));
		dataApproval.setStatus(_masterVO.getProperty("voucherApprove"));
		dataApproval.setRemarks(_masterVO.getProperty("Remarks"));
		dataApproval.setLanguage1(_masterVO.getProperty("languageCode0"));
			
		c2CStockApprovalRequestPojo.setData(dataApproval);
	}
	
	public C2CStockApprovalResponsePojo performC2CApproval() throws IOException {
		
		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
		c2CStockApprovalAPI.setExpectedStatusCode(200);
		c2CStockApprovalAPI.perform();
		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
		
		return c2CStockApprovalResponsePojo;
	}


	// Successful data with valid data.
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(toMSISDN, toPin, txnId, productTransferList);
		
		String value = DBHandler.AccessHandler.getPreference(toCatcode,_masterVO.getMasterValue(MasterI.NETWORK_CODE),PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		int maxApprovalLevel = Integer.parseInt(value);
		
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, fromCategory, EventsI.C2CTRANSFER_EVENT)) {
//            if (fromCategory.equals(toCategory)) {
//                channelMap = channelUser.channelUserInitiate(RowNum, Domain, ParentCategory, ToCategory, geoType);
//                String APPLEVEL = DBHandler.AccessHandler.getSystemPreference(UserAccess.userapplevelpreference());
//                if (APPLEVEL.equals("2")) {
//                    channelUser.approveLevel1_ChannelUser();
//                    channelUser.approveLevel2_ChannelUser();
//                } else if (APPLEVEL.equals("1")) {
//                    channelUser.approveLevel1_ChannelUser();
//                } else {
//                    Log.info("Approval not required.");
//                }
//                toMSISDN = channelMap.get("MSISDN");
//            }
			
			 if(BTSLUtil.isNullString(value)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	            		
	        		}
	            	
	            	if(maxApprovalLevel >= 1)
	        		{
	            		C2CStockApprovalResponsePojo response = performC2CApproval();
	            		String statuscode = response.getDataObject().getTxnstatus();
	            		
	            		Assert.assertEquals(statuscode, "200");
	            		Assertion.assertEquals("200", statuscode);
	            		
	            		Log.info("Level 1 Success !!");
	        		}
	            	if(maxApprovalLevel >= 2)
	        		{
	            		c2CStockApprovalRequestPojo.getData().setCurrentstatus(_masterVO.getProperty("aprvLvl2"));
	            		C2CStockApprovalResponsePojo response = performC2CApproval();
	            		String statuscode = response.getDataObject().getTxnstatus();
	            		
	            		Assert.assertEquals(statuscode, "200");
	            		Assertion.assertEquals("200", statuscode);
	            		
	            		Log.info("Level 2 Success !!");
	            	}
	            	
	            	if(maxApprovalLevel == 3)
	        		{
	            		c2CStockApprovalRequestPojo.getData().setCurrentstatus(_masterVO.getProperty("aprvLvl3"));
	            		C2CStockApprovalResponsePojo response = performC2CApproval();
	            		String statuscode = response.getDataObject().getTxnstatus();
	            		
	            		Assert.assertEquals(statuscode, "200");
	            		Assertion.assertEquals("200", statuscode);
	            	
	            		Log.info("Level 3 Success !!");
	            		
	        		
	        		}	      
	           }
		}  
        else {
            Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + fromCategory + "]");
        }  
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	@Test(dataProvider = "userData")
	public void A_02_Test_BlankMsisdn_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_02_Test_BlankMsisdn_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		data.setMsisdn("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());
		Assert.assertEquals(206, statusCode);
		String msg = c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(msg, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(msg, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	public void A_03_Test_BlankPin_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_03_Test_BlankPin_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		data.setPin("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(206, statusCode);
		String msg = c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(msg, "PIN can not be blank.");
		Assertion.assertEquals(msg, "PIN can not be blank.");
		
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	public void A_04_Test_BlankRefNumber_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_04_Test_BlankRefNumber_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		data.setRefnumber("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	
	@Test(dataProvider = "userData")
	public void A_08_Test_BlankExtnwcode_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_08_Test_BlankExtnwcode_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		data.setExtnwcode("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void A_09_Test_NumericPin_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_09_Test_NumericPin_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		data.setPin(new RandomGeneration().randomAlphabets(4));
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "PIN is not numeric.");
		Assertion.assertEquals(message, "PIN is not numeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void A_10_Test_InvalidLanguage_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_10_Test_InvalidLanguage_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		data.setLanguage1(new RandomGeneration().randomAlphabets(2));
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));		
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "LANGUAGE1 is not numeric.");
		Assertion.assertEquals(message, "LANGUAGE1 is not numeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_11_Test_InvalidMsisdnLength_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_11_Test_InvalidMsisdnLength_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI11");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		data.setMsisdn("72" + new RandomGeneration().randomNumeric(16));
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "MSISDN length should lie between 6 and 15.");
		Assertion.assertEquals(message, "MSISDN length should lie between 6 and 15.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider = "userData")
	public void A_12_Test_BlankInstrumentType_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_12_Test_BlankInstrumentType_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI12");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		c2CBuyStockInitiateRequestPojo.getData().getPaymentdetails().get(0).setPaymenttype("");
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "PAYMENTTYPE can not be blank.");
		Assertion.assertEquals(message, "PAYMENTTYPE can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	
	
	
	@Test(dataProvider = "userData")
	public void A_13_Test_PaymentInstrumentTypeCash_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_13_Test_PaymentInstrumentTypeCash_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI13");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		

		c2CBuyStockInitiateRequestPojo.getData().getPaymentdetails().get(0).setPaymenttype(_masterVO.getProperty("paymentInstrumentTypeCash"));
		c2CBuyStockInitiateRequestPojo.getData().getPaymentdetails().get(0).setPaymentinstnumber("");

		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	public void A_14_Test_InvalidPinLength_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_14_Test_InvalidPinLength_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI14");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);

		data.setPin(new RandomGeneration().randomNumeric(5));
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Invalid PIN length.");
		Assertion.assertEquals(message, "Invalid PIN length.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_15_Test_BlankLanguage_StockInitiate(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "A_15_Test_BlankLanguage_StockInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI15");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);

		data.setLanguage1("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	public void B_02_Test_BlankMsisdn_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_02_Test_BlankMsisdn_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		
		dataApproval.setMsisdn("");
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void B_03_Test_BlankPin_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_03_Test_BlankPin_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		
		dataApproval.setPin("");
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		
		Assert.assertEquals(message, "PIN can not be blank.");
		Assertion.assertEquals(message, "PIN can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void B_04_Test_BlankExtnwCode_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_04_Test_BlankExtnwCode_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		
		dataApproval.setExtnwcode("");
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void B_09_Test_BlankTxnid_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_09_Test_BlankTxnid_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		dataApproval.setTxnid("");
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		Assert.assertEquals(message, "TXNID can not be blank.");
		Assertion.assertEquals(message, "TXNID can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void B_10_Test_BlankCurrentstatus_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_10_Test_BlankCurrentstatus_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		dataApproval.setCurrentstatus("");
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		Assert.assertEquals(message, "Transaction ID does not exist with status .");
		Assertion.assertEquals(message, "Transaction ID does not exist with status .");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void B_11_Test_AlphaNumericMsisdn_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_11_Test_AlphaNumericMsisdn_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA11");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		dataApproval.setMsisdn(new RandomGeneration().randomAlphaNumeric(13));
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();

		Assert.assertEquals(message, "MSISDN is not numeric.");
		Assertion.assertEquals(message, "MSISDN is not numeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void B_12_Test_InvalidMsisdn_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_12_Test_InvalidMsisdn_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA12");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		dataApproval.setMsisdn(new RandomGeneration().randomAlphaNumeric(5));
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();

		Assert.assertEquals(message, "MSISDN is not numeric.");
		Assertion.assertEquals(message, "MSISDN is not numeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void B_13_Test_InvalidLengthPin_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_13_Test_InvalidLengthPin_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA13");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		dataApproval.setPin(new RandomGeneration().randomNumeric(15));
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Invalid PIN length.");
		Assertion.assertEquals(message, "Invalid PIN length.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void B_14_Test_InvalidExtnwCode_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_14_Test_InvalidExtnwCode_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),toCategory,fromCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA14");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		
		String ext=new RandomGeneration().randomAlphabets(4);
		dataApproval.setExtnwcode(ext);
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		Assert.assertEquals(message, "External network code "+ext+" is invalid.");
		Assertion.assertEquals(message, "External network code "+ext+" is invalid.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void B_17_Test_InvalidLanguage1_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_17_Test_InvalidLanguage1_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA17");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		
		dataApproval.setLanguage1(new RandomGeneration().randomNumeric(4));
		c2CStockApprovalRequestPojo.setData(dataApproval);
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		Assert.assertEquals(message, "LANGUAGE1 is not valid.");
		Assertion.assertEquals(message, "LANGUAGE1 is not valid.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider = "userData")
	public void B_18_Test_BlankPaymentInstrumentType_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_18_Test_BlankPaymentInstrumentType_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),toCategory,fromCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA18");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		
		c2CStockApprovalRequestPojo.getData().getPaymentdetails().get(0).setPaymenttype("");		
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		Assert.assertEquals(message, "PAYMENTTYPE can not be blank.");
		Assertion.assertEquals(message, "PAYMENTTYPE can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider = "userData")
	public void B_19_Test__BlankPaymentInstrumentDate_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_19_Test__BlankPaymentInstrumentDate_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA19");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		
		c2CStockApprovalRequestPojo.getData().getPaymentdetails().get(0).setPaymentdate("");	
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		Assert.assertEquals(message, "Payment instrument date is blank.");
		Assertion.assertEquals(message, "Payment instrument date is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void B_20_Test__BlankPaymentInstrumentNumber_StockApproval(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception {
		final String methodName = "B_20_Test__BlankPaymentInstrumentNumber_StockApproval";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String txnId= c2CBuyStockInitiateResponsePojo.getDataObject().getTxnid();
		List<Product> productTransferList =c2CBuyStockInitiateRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//OAuth toChannel User
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA20");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		
		c2CStockApprovalRequestPojo.getData().getPaymentdetails().get(0).setPaymentinstnumber("");		
		
		C2CStockApprovalResponsePojo response = performC2CApproval();
		String message =response.getDataObject().getMessage();
		Assert.assertEquals(message, "PAYMENTINSTNUMBER can not be blank.");
		Assertion.assertEquals(message, "PAYMENTINSTNUMBER can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
}
