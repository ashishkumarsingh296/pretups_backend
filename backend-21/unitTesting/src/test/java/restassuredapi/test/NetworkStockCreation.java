package restassuredapi.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.networkstockcreation.NetworkStockCreationAPI;
import restassuredapi.pojo.networkstockcreationrequestpojo.Data;
import restassuredapi.pojo.networkstockcreationrequestpojo.NetworkStockCreationRequestPojo;
import restassuredapi.pojo.networkstockcreationrequestpojo.StockProductList;
import restassuredapi.pojo.networkstockcreationresponsepojo.NetworkStockCreationResponsePojo;
@ModuleManager(name = Module.REST_NETWORK_STOCK_CREATION)
public class NetworkStockCreation extends BaseTest {
	RandomGeneration randomGeneration = new RandomGeneration();
	HashMap<String, String> transfer_Details = new HashMap<String, String>();
	static String moduleCode;
	//HashMap<String, String> transfer_Details = new HashMap<String, String>();
	
	NetworkStockCreationRequestPojo networkStockCreationRequestPojo = new NetworkStockCreationRequestPojo();
	NetworkStockCreationResponsePojo networkStockCreationResponsePojo = new NetworkStockCreationResponsePojo();
	Data data = new Data();
	StockProductList stockProductList = new StockProductList();
	List<StockProductList> stockProductList1 = new ArrayList<StockProductList>();
	
	public HashMap<String,String> getExcelData(){
		HashMap<String,String> tranferDetails=new HashMap<String,String>();    
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
        String ChannelUserLogin = null;
        String ChannelUserPassword = null;
        
        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
                ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                ChannelUserLogin = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                ChannelUserPassword = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                break;
            }
        }

        for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
                Data[k][0] = alist2.get(j);
                Data[k][1] = alist1.get(j);
                Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                Data[k][3] = ChannelUserMSISDN;
                Data[k][4] = ChannelUserPassword;
                Data[k][5] = ChannelUserLogin;
                Data[k][6] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                Data[k][8] = excelCounter;
               
                k++;
                
            }
        }

    }
    
    tranferDetails.put("Login_Id", Data[0][5].toString());
    tranferDetails.put("Password", Data[0][4].toString());
    String Login_Id_NetworkAdmin=ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.LOGIN_ID, new String[]{ExcelI.CATEGORY_NAME}, new String[]{"Network Admin"});
	String Password_NetworkAdmin=ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PASSWORD, new String[]{ExcelI.CATEGORY_NAME}, new String[]{"Network Admin"});
	tranferDetails.put("Login_Id_NA", Login_Id_NetworkAdmin );
	tranferDetails.put("Password_NA", Password_NetworkAdmin);
	
    return tranferDetails;
    
 
	}
	
	
    public void setupData() {
    	transfer_Details = getExcelData();
		networkStockCreationRequestPojo.setIdentifierType(transfer_Details.get("Login_Id_NA"));
		networkStockCreationRequestPojo.setIdentifierValue(transfer_Details.get("Password_NA"));
		data.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setUserId("SYSTEM");
		data.setReferenceNumber("");
		data.setWalletType(_masterVO.getProperty("walletType"));
		data.setRemarks("");
		
		int size = 1;
		for(int i=0;i<size;i++) 
		{			
			stockProductList.setProductCode(_masterVO.getProperty("productCode"));
			stockProductList.setProductName("");
			stockProductList.setRequestedQuantity(randomGeneration.randomNumeric(5));
			stockProductList1.add(stockProductList);
		}
		
		data.setStockProductList(stockProductList1);
		networkStockCreationRequestPojo.setData(data);
		
	}
	
	
	//Happy Flow
	@Test
	public void A_01_Test_networkStockTxn_Positive() throws Exception
	{
		
		final String methodName = "Test_NetworkStockCreationAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
		networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
		networkStockCreationAPI.setExpectedStatusCode(200);
		networkStockCreationAPI.perform();
		networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);
		
		//int statusCode =networkStockCreationResponsePojo.getStatusCode();
	//	String statusCodeString = Integer.toString(statusCode);
		//Assertion.assertEquals(statusCodeString, "400");
		String message =networkStockCreationResponsePojo.getMessageCode();
		Assert.assertEquals(message, "networkstock.createstock.msg.success");
		Assertion.assertEquals(message, "networkstock.createstock.msg.success");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	// To check if Wallet Type is given blank 
	@Test
	public void A_02_Test_networkStockTxnNegativeMissingWalletType() throws Exception
	{
		
		final String methodName = "Test_NetworkStockCreationAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setWalletType("");
		networkStockCreationRequestPojo.setData(data);
		networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
		networkStockCreationAPI.setExpectedStatusCode(200);
		networkStockCreationAPI.perform();
		networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);
		
	
		String message =networkStockCreationResponsePojo.getMessageCode();
		Assert.assertEquals(message, "networkstock.includestocktxn.error.nowallet");
		Assertion.assertEquals(message, "networkstock.includestocktxn.error.nowallet");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	// To check if Requested Quantity is given blank
		@Test
		public void A_03_Test_networkStockTxnNegativeRequestedQuantityblank() throws Exception
		{
			final String methodName = "Test_NetworkStockCreationAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			
			NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
		
			int size = 1;
			for(int i=0;i<size;i++)
			{			
				
				stockProductList.setRequestedQuantity("");
				
			}	
			networkStockCreationRequestPojo.setData(data);
			networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
			networkStockCreationAPI.setExpectedStatusCode(200);
			networkStockCreationAPI.perform();
			networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);

			String message =networkStockCreationResponsePojo.getMessageCode();
			Assert.assertEquals(message, "networkstock.includestocktxn.invalidquantity");
			Assertion.assertEquals(message, "networkstock.includestocktxn.invalidquantity");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		// To check if Requested Quantity is given blank
		@Test
	public void A_04_Test_networkStockTxnNegativeRequestedQuantityLess() throws Exception
	{
			final String methodName = "Test_NetworkStockCreationAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC4");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			
					NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
					networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
					
				
					
					int size = 1;
					for(int i=0;i<size;i++)
					{			
						
						stockProductList.setRequestedQuantity("-1");
						
					}
					networkStockCreationRequestPojo.setData(data);
					
					networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
					networkStockCreationAPI.setExpectedStatusCode(200);
					networkStockCreationAPI.perform();
					networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);

					String message =networkStockCreationResponsePojo.getMessageCode();
					Assert.assertEquals(message, "networkstock.includestocktxn.invalidquantity");
					Assertion.assertEquals(message, "networkstock.includestocktxn.invalidquantity");
					Assertion.completeAssertions();
					Log.endTestCase(methodName);
				}
		@Test
		public void A_05_Test_networkStockTxnNegativeNoproductType() throws Exception
		{
			final String methodName = "Test_NetworkStockCreationAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC5");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			
			NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
			
			
			int size = 1;
			for(int i=0;i<size;i++)
			{			
				stockProductList.setProductCode("");
				stockProductList.setProductName("");
				stockProductList.setRequestedQuantity("");
				
			}
			networkStockCreationRequestPojo.setData(data);
			
			
			networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
			networkStockCreationAPI.setExpectedStatusCode(200);
			networkStockCreationAPI.perform();
			networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);
			
			String message =networkStockCreationResponsePojo.getMessageCode();
			Assert.assertEquals(message, "networkstock.includestocktxn.invalidquantity");
			Assertion.assertEquals(message, "networkstock.includestocktxn.invalidquantity");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			}
		@Test
		public void A_06_Test_networkStockTxnNegativeRequestedQuantityGreaterThanWalletBal() throws Exception
		{
			final String methodName = "Test_NetworkStockCreationAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC6");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			
			NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
			
			int size = 1;
			for(int i=0;i<size;i++)
			{			
				
				stockProductList.setRequestedQuantity("444488888888888888");
			}
			
			networkStockCreationRequestPojo.setData(data);
			networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
			networkStockCreationAPI.setExpectedStatusCode(200);
			networkStockCreationAPI.perform();
			networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);
			String message =networkStockCreationResponsePojo.getMessageCode();
			Assert.assertEquals(message, "networkstock.includestocktxn.error.requestamt.more.than.stock");
			Assertion.assertEquals(message, "networkstock.includestocktxn.error.requestamt.more.than.stock");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		@Test
		public void A_07_Test_BlankNetworkCode() throws Exception
		{
			final String methodName = "Test_NetworkStockCreationAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC7");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			
			NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
			data.setNetworkCode("");
			
			networkStockCreationRequestPojo.setData(data);
			networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
			networkStockCreationAPI.setExpectedStatusCode(200);
			networkStockCreationAPI.perform();
			networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);
			String message =networkStockCreationResponsePojo.getMessageCode();
			Assert.assertEquals(message, "networkstock.includestocktxn.error.ntwcodeidblnkorinv");
			Assertion.assertEquals(message, "networkstock.includestocktxn.error.ntwcodeidblnkorinv");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		@Test
		public void A_08_Test_BlankUserId() throws Exception
		{
			final String methodName = "Test_NetworkStockCreationAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC8");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			
			NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
			data.setUserId("");
			
			networkStockCreationRequestPojo.setData(data);
			networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
			networkStockCreationAPI.setExpectedStatusCode(200);
			networkStockCreationAPI.perform();
			networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);
			String message =networkStockCreationResponsePojo.getMessageCode();
			Assert.assertEquals(message, "networkstock.createstock.msg.unsuccess");
			Assertion.assertEquals(message, "networkstock.createstock.msg.unsuccess");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		@Test
		public void A_09_Test_InvalidWalletType() throws Exception
		{
			final String methodName = "Test_NetworkStockCreationAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC9");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			
			NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
			data.setWalletType("SD");
			
			networkStockCreationRequestPojo.setData(data);
			networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
			networkStockCreationAPI.setExpectedStatusCode(200);
			networkStockCreationAPI.perform();
			networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);
			String message =networkStockCreationResponsePojo.getMessageCode();
			Assert.assertEquals(message, "networkstock.includestocktxn.error.nowallet");
			Assertion.assertEquals(message, "networkstock.includestocktxn.error.nowallet");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		@Test
		public void A_10_Test_BlankIdentifierType() throws Exception
		{
			final String methodName = "Test_NetworkStockCreationAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC10");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			
			NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
			networkStockCreationRequestPojo.setIdentifierType("");
			
			networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
			networkStockCreationAPI.setExpectedStatusCode(200);
			networkStockCreationAPI.perform();
			networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);
			int statusCode =networkStockCreationResponsePojo.getStatusCode();
			Assert.assertEquals(400, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		@Test
		public void A_11_Test_BlankIdentifierValue() throws Exception
		{
			final String methodName = "Test_NetworkStockCreationAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTNWSTOCKC11");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			
			NetworkStockCreationAPI networkStockCreationAPI = new NetworkStockCreationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			networkStockCreationAPI.setContentType(_masterVO.getProperty("contentType"));
			networkStockCreationRequestPojo.setIdentifierValue("");
			
			networkStockCreationAPI.addBodyParam(networkStockCreationRequestPojo);
			networkStockCreationAPI.setExpectedStatusCode(200);
			networkStockCreationAPI.perform();
			networkStockCreationResponsePojo =networkStockCreationAPI.getAPIResponseAsPOJO(NetworkStockCreationResponsePojo.class);
			int statusCode =networkStockCreationResponsePojo.getStatusCode();
			Assert.assertEquals(400, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
}

