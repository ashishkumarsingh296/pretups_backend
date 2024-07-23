package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
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

import restassuredapi.api.c2cstocktransfermult.C2CStockTransferMultApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cmultiplestocktransferresponsepojo.C2CMultipleStockTransferResponsePojo;
import restassuredapi.pojo.c2ctransferstockrequestpojo.C2CDetailsData;
import restassuredapi.pojo.c2ctransferstockrequestpojo.C2CStockDetailsRequestPojo;
import restassuredapi.pojo.c2ctransferstockrequestpojo.Paymentdetail;
import restassuredapi.pojo.c2ctransferstockrequestpojo.Product;

import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
@ModuleManager(name = Module.C2C_TRF_MULT)
public class C2CStockMultiple extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	C2CStockDetailsRequestPojo c2CStockDetailsRequestPojo = new C2CStockDetailsRequestPojo();
	C2CMultipleStockTransferResponsePojo c2CMultipleStockTransferResponsePojo = new C2CMultipleStockTransferResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	C2CDetailsData data = new C2CDetailsData();
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();

	
	@SuppressWarnings("unchecked")
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
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
	
	HashSet<String> fromData = new HashSet<String>();
    Object[][] Data = new Object[totalObjectCounter][8];
    for (int j = 0, k = 0; j < alist2.size(); j++) {
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int excelRowSize = ExcelUtility.getRowCount();
        String ChannelUserLoginId = null;
        String ChannelUserMSISDN = null;
        String ChannelUserPIN = null;
        String ChannelUserPASS = null;
        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist2.get(j))) {
            	fromData.add(alist2.get(j));
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
            	Data[k][1] = ChannelUserMSISDN; //fromMsisdn
            	Data[k][2] = ChannelUserLoginId;
                Data[k][3] = ChannelUserPIN;
                Data[k][4] = ChannelUserPASS;
                Data[k][5] = alist1.get(j); //toCategoryName
                Data[k][6] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                k++;
            }
        }  
    }
    
    int count=fromData.size();
    Object[][] origData = new Object[count][2];
    
    ArrayList<HashMap<String,String>> fromChannelList =  new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> superDist = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> dealer = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> agent = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> retailer = new ArrayList<HashMap<String,String>>();
    
    int ctr=0;
    for(int i=0;i<Data.length;i++)
    {
    	HashMap<String,String> fromChannel = new HashMap<String, String>();
    	boolean find=false;
    		for(int k=0;k<fromChannelList.size();k++) {
    			HashMap<String,String> tempData = fromChannelList.get(k);
    			if(tempData.get("CategoryName").equals(Data[i][0]))
    				find=true;
    		}
    		
    		if(!find) {
    			fromChannel.put("CategoryName",Data[i][0].toString());
    			fromChannel.put("Msisdn",Data[i][1].toString());
    			fromChannel.put("LoginId",Data[i][2].toString());
    			fromChannel.put("Pin",Data[i][3].toString());
    			fromChannel.put("Password",Data[i][4].toString());
    			
    			fromChannelList.add(fromChannel);
    			origData[ctr][0]=fromChannel;
    			ctr++;
    		}
    		
    		if(Data[i][0].toString().equals("Super Distributor")) {
    			HashMap<String,String> temp = new HashMap<String, String>();
    			temp.put("CategoryName",Data[i][5].toString());
    			temp.put("Msisdn",Data[i][6].toString());
    			temp.put("CatCode",Data[i][7].toString());
    	
    			superDist.add(temp);
    		}
    		
    		if(Data[i][0].toString().equals("Dealer")) {
    			HashMap<String,String> temp = new HashMap<String, String>();
    			temp.put("CategoryName",Data[i][5].toString());
    			temp.put("Msisdn",Data[i][6].toString());
    			temp.put("CatCode",Data[i][7].toString());
    	
    			dealer.add(temp);
    		}
    		
    		else if(Data[i][0].toString().equals("Agent")) {
    			HashMap<String,String> temp = new HashMap<String, String>();
    			temp.put("CategoryName",Data[i][5].toString());
    			temp.put("Msisdn",Data[i][6].toString());
    			temp.put("CatCode",Data[i][7].toString());
    	
    			agent.add(temp);
    		}
    		
    		else if(Data[i][0].toString().equals("Retailer")) {
    			HashMap<String,String> temp = new HashMap<String, String>();
    			temp.put("CategoryName",Data[i][5].toString());
    			temp.put("Msisdn",Data[i][6].toString());
    			temp.put("CatCode",Data[i][7].toString());
    	
    			retailer.add(temp);
    		}
    	
    }
    
    for(int i=0;i<origData.length;i++) {
    	
    	if(((HashMap<String,String>)origData[i][0]).get("CategoryName").equals("Super Distributor"))
    		origData[i][1]=superDist;
    	else if(((HashMap<String,String>)origData[i][0]).get("CategoryName").equals("Dealer"))
    		origData[i][1]=dealer;
    	else if(((HashMap<String,String>)origData[i][0]).get("CategoryName").equals("Agent"))
    		origData[i][1]=agent;
    	else if(((HashMap<String,String>)origData[i][0]).get("CategoryName").equals("Retailer"))
    		origData[i][1]=retailer;
    }
    return origData;
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


	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception
	{
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
		org.testng.Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}

	
	public void setupData(String fromMsisdn, ArrayList<HashMap<String,String>> toChannelList) {
		int size = toChannelList.size();
		
		List<C2CDetailsData> dataList = new ArrayList<>();
		for(int j=0;j<size;j++)
		{
			HashMap<String,String> toData = toChannelList.get(j);
			String toMsisdn= toData.get("Msisdn");
			
			data.setExtcode2("");
			data.setLanguage1(_masterVO.getProperty("languageCode0"));
			data.setMsisdn2(toMsisdn);
			data.setLoginid2("");
			
			Paymentdetail paymentdetail= new Paymentdetail();
			ArrayList<Paymentdetail> paymentDetails=new ArrayList<Paymentdetail>();
			paymentdetail.setPaymentinstnumber(new RandomGeneration().randomNumeric(5));
			paymentdetail.setPaymentdate(currentDate);
			paymentdetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
			paymentDetails.add(paymentdetail);
			data.setPaymentdetails(paymentDetails);
			
			ArrayList<Product> products = new ArrayList<Product>();
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			for (int i = 1; i <= rowCount; i++) {
			Product product = new Product();
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
			data.setRemarks("Automation REST API");	
			
			dataList.add(data);
		}
		
		c2CStockDetailsRequestPojo.setData(dataList);
	}

	// Successful data with valid data.
	@Test(dataProvider = "userData")
	public void A_01_Test_success(HashMap<String,String> fromChannel,ArrayList<HashMap<String,String>> toChannelList) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromChannel.get("LoginId"),fromChannel.get("Password"),fromChannel.get("CategoryName"));
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromChannel.get("Msisdn"), fromChannel.get("Pin"), fromChannel.get("CategoryName"));
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CST1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromChannel.get("categoryName")));
		currentNode.assignCategory("REST");
		setupData(fromChannel.get("Msisdn"),toChannelList);
		
		C2CStockTransferMultApi addChannelUserAPI = new C2CStockTransferMultApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(c2CStockDetailsRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(200);
		addChannelUserAPI.perform();
		c2CMultipleStockTransferResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(C2CMultipleStockTransferResponsePojo.class);
		String messageCode = c2CMultipleStockTransferResponsePojo.getStatus();
		Assert.assertEquals(200, Integer.parseInt(messageCode));
		Assertion.assertEquals(messageCode, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	@Test(dataProvider = "userData")
	public void A_03_Test_invalid_msisdn(HashMap<String,String> fromChannel,ArrayList<HashMap<String,String>> toChannelList) throws Exception {
		final String methodName = "A_03_Test_invalid_msisdn";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromChannel.get("LoginId"),fromChannel.get("Password"),fromChannel.get("CategoryName"));
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromChannel.get("Msisdn"), fromChannel.get("Pin"), fromChannel.get("CategoryName"));
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CST3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromChannel.get("categoryName")));
		currentNode.assignCategory("REST");
		setupData(fromChannel.get("Msisdn"),toChannelList);
		List<C2CDetailsData> c2cDetailsDatas=c2CStockDetailsRequestPojo.getData();
		c2cDetailsDatas.get(0).setMsisdn2(String.valueOf(gnMsisdn.generateMSISDN()));
		C2CStockTransferMultApi addChannelUserAPI = new C2CStockTransferMultApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(c2CStockDetailsRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(7901);
		addChannelUserAPI.perform();
		c2CMultipleStockTransferResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(C2CMultipleStockTransferResponsePojo.class);
		String errorcode = c2CMultipleStockTransferResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(7901, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "7901");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	

	@Test(dataProvider = "userData")
	public void A_06_Test_invalid_extcode(HashMap<String,String> fromChannel,ArrayList<HashMap<String,String>> toChannelList) throws Exception {
		final String methodName = "A_06_Test_invalid_extcode";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(fromChannel.get("LoginId"),fromChannel.get("Password"),fromChannel.get("CategoryName"));
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(fromChannel.get("Msisdn"), fromChannel.get("Pin"), fromChannel.get("CategoryName"));
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CST6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromChannel.get("categoryName")));
		currentNode.assignCategory("REST");
		setupData(fromChannel.get("Msisdn"),toChannelList);
		List<C2CDetailsData> c2cDetailsDatas=c2CStockDetailsRequestPojo.getData();
		c2cDetailsDatas.get(0).setExtcode2(String.valueOf(gnMsisdn.generateMSISDN()));
		C2CStockTransferMultApi addChannelUserAPI = new C2CStockTransferMultApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(c2CStockDetailsRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(7894);
		addChannelUserAPI.perform();
		c2CMultipleStockTransferResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(C2CMultipleStockTransferResponsePojo.class);
		String errorcode = c2CMultipleStockTransferResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(7894, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "7894");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	
		}
