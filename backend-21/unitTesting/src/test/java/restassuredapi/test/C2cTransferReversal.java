package restassuredapi.test;


import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2creversal.C2cReversalApi;
import restassuredapi.api.c2cstock.C2CStockApprovalAPI;
import restassuredapi.api.c2cstock.C2CStockTransferAPI2;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.C2CStockApprovalRequestPojo;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.DataApproval;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.PaymentdetailApproval;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.ProductApproval;
import restassuredapi.pojo.c2cstockapprovalresponsepojo.C2CStockApprovalResponsePojo;
import restassuredapi.pojo.c2cstocktransferrequestpojo.C2CStockTransferRequestPojo2;
import restassuredapi.pojo.c2cstocktransferrequestpojo.DataTransfer2;
import restassuredapi.pojo.c2cstocktransferrequestpojo.PaymentdetailTransfer;
import restassuredapi.pojo.c2cstocktransferrequestpojo.ProductTransfer;
import restassuredapi.pojo.c2cstocktransferresponsepojo.C2CStockTransferResponsePojo;
import restassuredapi.pojo.c2cstocktransferresponsepojo.C2cTransferReversalResponse;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;


@ModuleManager(name = Module.C2C_TNX_REVERSAL)
public class C2cTransferReversal extends BaseTest {
	
	 DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
    
	static String moduleCode;
	C2CStockApprovalRequestPojo c2CStockApprovalRequestPojo = new C2CStockApprovalRequestPojo();
	C2CStockApprovalResponsePojo c2CStockApprovalResponsePojo = new C2CStockApprovalResponsePojo();
	C2CStockTransferRequestPojo2 c2CStockTransferRequestPojo = new C2CStockTransferRequestPojo2();
	C2CStockTransferResponsePojo c2CStockTransferResponsePojo = new C2CStockTransferResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	DataTransfer2 dataTransfer = new DataTransfer2();
	DataApproval dataApproval =new DataApproval();
	
	Login login = new Login();
	NetworkAdminHomePage homepage = new NetworkAdminHomePage(driver);
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String CAdmLoginId=null;
        String CAdmPasswd=null;
        String CAdmCate=null;
		String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> categorySize = new ArrayList<String>();
		int innerTests=0;
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
				innerTests++;
				if(innerTests==1) {
					break;
				}
			}
		}
		 ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
         int OperatorRowCountAdmin = ExcelUtility.getRowCount();
         for (int i = 1; i < OperatorRowCountAdmin; i++) {
                String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                if (CategoryName.equals("BCU") && (!LoginID.equals(null) || !LoginID.equals(""))) {
             	    CAdmLoginId= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
             	    CAdmPasswd= ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);      
                    CAdmCate= ExcelUtility.getCellData(0,  ExcelI.CATEGORY_NAME, i);
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

        	String ChannelUserLoginId = null;
            String ChannelUserMSISDN = null;
            String ChannelUserPIN = null;
            String ChannelUserPASS = null;	
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            int excelRowSize = ExcelUtility.getRowCount();  
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
                    Data[k][8] = CAdmLoginId;
                    Data[k][9] = CAdmPasswd;
                    Data[k][10] = CAdmCate;
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
		headerMap.put("requestGatewayPsecure","1357");
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
		ArrayList<ProductTransfer> products = new ArrayList<ProductTransfer>();
		ArrayList<PaymentdetailTransfer> paymentDetails=new ArrayList<PaymentdetailTransfer>();
		PaymentdetailTransfer paymentdetail= new PaymentdetailTransfer();

		RandomGeneration randomGeneration = new RandomGeneration();
		
		dataTransfer.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		dataTransfer.setMsisdn(fromMsisdn);
		dataTransfer.setPin(fromPin);
		dataTransfer.setLoginid("");
		dataTransfer.setPassword("");
		dataTransfer.setExtcode("");
		dataTransfer.setMsisdn2(toMsisdn);
		dataTransfer.setLoginid2("");
		dataTransfer.setExtcode2("");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
		ProductTransfer product = new ProductTransfer();
		String productShortCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i);
		String productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
		product.setProductcode(productShortCode);
		String userBalance = DBHandler.AccessHandler.getUserBalance(productCode, toMsisdn);
		int prBalance= (int) Double.parseDouble(userBalance);
		int quantity=(int) (prBalance*0.01*0.01);
		product.setQty(String.valueOf(quantity));
		products.add(product);
		}
		dataTransfer.setProducts(products);
		dataTransfer.setRefnumber("12324");
			
		paymentdetail.setPaymentinstnumber(randomGeneration.randomNumeric(5));
		paymentdetail.setPaymentdate(currentDate);
		paymentdetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
		
		// array list of paymentdetails
		paymentDetails.add(paymentdetail);	
		dataTransfer.setLanguage1(_masterVO.getProperty("languageCode0"));
	
		//sending array list
		dataTransfer.setPaymentdetails(paymentDetails);
		dataTransfer.setRemarks("Automation REST API");
		dataTransfer.setDate(currentDate);
		dataTransfer.setFileName("");
		dataTransfer.setFileType("");
		dataTransfer.setFileUploaded(false);
		dataTransfer.setFileAttachment("");
		

		c2CStockTransferRequestPojo.setData(dataTransfer);

	}
	
	public void setupC2CApproval(String fromMsisdn,String fromPin,String txnId,List<ProductTransfer> productTransferList) {
		
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

  
   
    public String transferAndApproval(String fromCategory, String toCategory,
			String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN,
			String fromPassword,String toCatcode) throws Exception {
    	
    	if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromLoginID, fromPassword, fromCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromMSISDN, fromPIN, fromCategory);
		
		//Stock Transfer
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CST1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		setupData(fromMSISDN,fromPIN,toMSISDN);
	
		C2CStockTransferAPI2 c2CStockTransferAPI = new C2CStockTransferAPI2(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CStockTransferAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CStockTransferAPI.addBodyParam(c2CStockTransferRequestPojo);
		c2CStockTransferAPI.setExpectedStatusCode(200);
		c2CStockTransferAPI.perform();
		c2CStockTransferResponsePojo = c2CStockTransferAPI
				.getAPIResponseAsPOJO(C2CStockTransferResponsePojo.class);
		
		String txnId= c2CStockTransferResponsePojo.getDataObject().getTxnid();
		List<ProductTransfer> productTransferList =c2CStockTransferRequestPojo.getData().getProducts();
		int statusCode = Integer.parseInt(c2CStockTransferResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		
		//Stock Approval
		CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory));
		currentNode.assignCategory("REST");
		setupC2CApproval(fromMSISDN, fromPIN, txnId, productTransferList);
		
		String value = DBHandler.AccessHandler.getPreference(toCatcode,_masterVO.getMasterValue(MasterI.NETWORK_CODE),PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		int maxApprovalLevel = Integer.parseInt(value);
		
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, fromCategory, EventsI.C2CTRANSFER_EVENT)) {
			
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
    	return txnId;
    }
   
	    
	 public C2cTransferReversalResponse c2cReversal(String networkCode ,String networkCodeFor,String remark,String tnxid,int expected) throws Exception {
		    C2cReversalApi c2cReversalApi=new  C2cReversalApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			c2cReversalApi.setContentType(_masterVO.getProperty("contentType"));
			c2cReversalApi.setNetworkCode(networkCode);
			c2cReversalApi.setNetworkCodeFor(networkCodeFor);
			c2cReversalApi.setRemark(remark);
			c2cReversalApi.setTxnID(tnxid);
			c2cReversalApi.setExpectedStatusCode(expected);
			C2cTransferReversalResponse respojo = new C2cTransferReversalResponse(); 
			c2cReversalApi.perform();
			respojo = c2cReversalApi.getAPIResponseAsPOJO(C2cTransferReversalResponse.class);
			return respojo;
		}
    
    
	// Successful data with valid data.
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-15642")
	public void A_01_Test_success(String fromCategory, String toCategory,
			String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN,
			String fromPassword,String toCatcode,String cAdminLoginId,String cAdminPassword,String cAdminCat) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
	    String tnxid=transferAndApproval(fromCategory,toCategory,fromMSISDN,toMSISDN,fromLoginID,fromPIN,fromPassword,toCatcode);
	    BeforeMethod(cAdminLoginId, cAdminPassword, cAdminCat);
	    String remark="Automation c2creversal";
	    String networkCode="NG";
	    String networkCodeFor="NG";
	    int expected=200;
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2C_TNX_REVERSAL01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
	    C2cTransferReversalResponse respojo= this.c2cReversal(networkCode, networkCodeFor, remark, tnxid,expected);
	    String status=respojo.getStatus();
	    Assertion.assertEquals(status, "200");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-15643")
	public void A_02_Test_invalidTnxID(String fromCategory, String toCategory,
			String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN,
			String fromPassword,String toCatcode,String cAdminLoginId,String cAdminPassword,String cAdminCat) throws Exception {
		final String methodName = "A_02_Test_invalidTnxID";
		Log.startTestCase(methodName);
	    BeforeMethod(cAdminLoginId, cAdminPassword, cAdminCat);
	    String tnxid="ajbskjasj";
	    String remark="Automation c2creversal";
	    String networkCode="NG";
	    String networkCodeFor="NG";
	    int expected=400;
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2C_TNX_REVERSAL02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
	    C2cTransferReversalResponse respojo= this.c2cReversal(networkCode, networkCodeFor, remark, tnxid,expected);
	    String msg=respojo.getMessage();
	    Assertion.assertEquals(msg, "Transfer ID is invalid.");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
    @Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-15644")
	public void A_03_Test_invalidNetworkCode(String fromCategory, String toCategory,
			String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN,
			String fromPassword,String toCatcode,String cAdminLoginId,String cAdminPassword,String cAdminCat) throws Exception {
		final String methodName = "A_03_Test_invalidNetworkCode";
		Log.startTestCase(methodName);
	    String tnxid=transferAndApproval(fromCategory,toCategory,fromMSISDN,toMSISDN,fromLoginID,fromPIN,fromPassword,toCatcode);
	    BeforeMethod(cAdminLoginId, cAdminPassword, cAdminCat);
	    String remark="Automation c2creversal";
	    String networkCode="NGsdsd";
	    String networkCodeFor="NG";
	    int expected=400;
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2C_TNX_REVERSAL03");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
	    C2cTransferReversalResponse respojo= this.c2cReversal(networkCode, networkCodeFor, remark, tnxid,expected);
	    String msg=respojo.getMessage();
	    Assertion.assertEquals(msg, "Network code is invalid.");
	    Assertion.completeAssertions();
	    Log.endTestCase(methodName);
		
	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-15645")
	public void A_04_Test_invalidNetworkCodeFor(String fromCategory, String toCategory,
			String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN,
			String fromPassword,String toCatcode,String cAdminLoginId,String cAdminPassword,String cAdminCat) throws Exception {
		final String methodName = "A_04_Test_invalidNetworkCodeFor";
		Log.startTestCase(methodName);
	    String tnxid=transferAndApproval(fromCategory,toCategory,fromMSISDN,toMSISDN,fromLoginID,fromPIN,fromPassword,toCatcode);
	    BeforeMethod(cAdminLoginId, cAdminPassword, cAdminCat);
	    String remark="Automation c2creversal";
	    String networkCode="NG";
	    String networkCodeFor="NGsdsa";
	    int expected=400;
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2C_TNX_REVERSAL04");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
	    C2cTransferReversalResponse respojo= this.c2cReversal(networkCode, networkCodeFor, remark, tnxid,expected);
	    String msg=respojo.getMessage();
	    Assertion.assertEquals(msg, "Network code for is invalid.");
	    Assertion.completeAssertions();
	    Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-15646")
	public void A_05_Test_invalidLengthRemarks(String fromCategory, String toCategory,
			String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN,
			String fromPassword,String toCatcode,String cAdminLoginId,String cAdminPassword,String cAdminCat) throws Exception {
		final String methodName = "A_05_Test_invalidLengthRemarks";
		Log.startTestCase(methodName);
	    String tnxid=transferAndApproval(fromCategory,toCategory,fromMSISDN,toMSISDN,fromLoginID,fromPIN,fromPassword,toCatcode);
	    BeforeMethod(cAdminLoginId, cAdminPassword, cAdminCat);
	    String remark="araewredfsggrrertretrtrdfdfdfdfdfdsfdfjhdbdfjdsihfidsbfidfidihdaiuhdiuhdhdaiddis"
	    		+ "dfkjdsnfkjdnsfjdskfjnkdfjdsfjdjfjjdnfjfjfnjfnnjfnjfnjjfnjfnfnnfnjfnjfnjfjnfjfnjfnjfjnnjnj";
	    String networkCode="NG";
	    String networkCodeFor="NG";
	    int expected=400;
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2C_TNX_REVERSAL05");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
	    C2cTransferReversalResponse respojo= this.c2cReversal(networkCode, networkCodeFor, remark, tnxid,expected);
	    String msg=respojo.getMessage();
	    Assertion.assertEquals(msg, "Invalid length for Remarks.");
	    Assertion.completeAssertions();
	    Log.endTestCase(methodName);
		
	}
}