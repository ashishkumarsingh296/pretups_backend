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
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.mchange.v2.c3p0.impl.DbAuth;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testscripts.sit.SIT_VMS;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

import restassuredapi.api.c2cvouchertransfer.C2CVoucherApprovalAPI;
import restassuredapi.api.c2cvouchertransfer.C2CVoucherInitiateAPI;
import restassuredapi.api.c2cvouchertransfer.C2CVoucherTransferAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2CVoucherTransferRequestPojo.C2CVoucherTransferRequestPojo;


import restassuredapi.pojo.c2CVoucherTransferResponsePojo.C2CVoucherTransferResponsePojo;
import restassuredapi.pojo.c2cvoucherapprovalrequestpojo.C2CVoucherApprovalRequestPojo;
import restassuredapi.pojo.c2cvoucherapprovalrequestpojo.DataAp;
import restassuredapi.pojo.c2cvoucherapprovalrequestpojo.VoucherDetailAp;
import restassuredapi.pojo.c2cvoucherapprovalresponsepojo.C2CVoucherApprovalResponsePojo;
import restassuredapi.pojo.c2cvoucherinitiaterequestpojo.C2CVoucherInitiateRequestPojo;
import restassuredapi.pojo.c2cvoucherinitiaterequestpojo.Data;
import restassuredapi.pojo.c2cvoucherinitiaterequestpojo.VoucherDetail;
import restassuredapi.pojo.c2cvoucherinitiateresponsepojo.C2CVoucherInitiateResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
@ModuleManager(name = Module.REST_C2C_VOUCHER_INITIATE)
public class C2CVoucherInitiateAndApproval extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
	    Date dateobj = new Date();
	    String currentDate=df.format(dateobj);
	    static String moduleCode;
	    C2CVoucherInitiateRequestPojo c2cVoucherInitiateRequestPojo= new C2CVoucherInitiateRequestPojo();
	    C2CVoucherInitiateResponsePojo c2cVoucherInitiateResponsePojo = new C2CVoucherInitiateResponsePojo();
	    OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	    OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	    C2CVoucherApprovalRequestPojo c2cVoucherApprovalRequestPojo = new C2CVoucherApprovalRequestPojo();
		C2CVoucherApprovalResponsePojo c2cVoucherApprovalResponsePojo = new C2CVoucherApprovalResponsePojo();
		
	   Data data =new Data();
	    DataAp data1 = new DataAp();
	    VoucherDetailAp voucher = new VoucherDetailAp();
		ArrayList<VoucherDetailAp> voucherDetails = new ArrayList<VoucherDetailAp>();
	    VoucherDetail voucherDetail = new VoucherDetail();

	    @DataProvider(name = "userData")
	    public Object[][] TestDataFeed(){
	        String C2CTransferCode = _masterVO.getProperty("C2CVoucherInitiateCode");
	        String MasterSheetPath = _masterVO.getProperty("DataProvider");
	        HashMap<String,String> UserAp=new HashMap<String,String>();
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	        int rowCount1 = ExcelUtility.getRowCount();
	        for(int i=1;i<=rowCount1;i++) {
	            if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,i).equals("DIST")) {
	                UserAp.put("LoginAp",ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i));
	                UserAp.put("PassAp", ExcelUtility.getCellData(0, ExcelI.PASSWORD,i));
	            }
	        }
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
	        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
	        rowCount = ExcelUtility.getRowCount();
	        ArrayList<ArrayList<String>> voucherData= new ArrayList<ArrayList<String>>();
	        for (int i = 1; i <= rowCount; i++) {
	            ArrayList<String> voucherTempData =new ArrayList<>();
	            if(ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("D")||ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("DT")||ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("PT")||ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("P")){
	            	//if(ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("D"))
	            //	{
	                voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
	                voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
	                voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
	                voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));

	                voucherData.add(voucherTempData);
	            }
	        }

	    	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	        int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();



	        int totalObjectCounter = 0;
	        for (String s : alist2) {
	            int categorySizeCounter = 0;
	            for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
	                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(s)) {
	                    categorySizeCounter++;
	                }
	            }
	            categorySize.add("" + categorySizeCounter);
	            totalObjectCounter = totalObjectCounter + categorySizeCounter;
	        }
	        Object[][] Data = new Object[totalObjectCounter][11];
	        for (int j = 0, k = 0; j < alist2.size(); j++) {
	            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	            int excelRowSize = ExcelUtility.getRowCount();
	            String ChannelUserMSISDN = null,ChannelUserLoginId=null,ChannelUserPIN=null,ChannelUserPASS=null;
	            for (int i = 1; i <= excelRowSize; i++) {
	                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
	                    ChannelUserMSISDN=ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
	                    ChannelUserLoginId=ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
	                    ChannelUserPIN=ExcelUtility.getCellData(0, ExcelI.PIN, i);
	                    ChannelUserPASS=ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
	                  
	                }
	            }

	            for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
	                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
	                    Data[k][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
	                    Data[k][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
	                    Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
	                    Data[k][3] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
	                    Data[k][4] = alist1.get(j);
	                    Data[k][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter);
	                    Data[k][6] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
	                    Data[k][7] = ChannelUserMSISDN;
	                    Data[k][8] = UserAp.get("LoginAp");
	                    Data[k][9] = UserAp.get("PassAp");
	                    Data[k][10] = ExcelUtility.getCellData(0, ExcelI.USER_NAME,excelCounter);
	                    k++;
	                }
	            }
	        }

	        int countTotal = voucherData.size()-1;
	        Object[][] c2cData = new Object[countTotal][15];
	        for (int i = 0; i < countTotal; i++) {

	            int counter_j=0;

	            for(int j=0;j<Data[0].length;j++) {
	                c2cData[i][counter_j++]=Data[i][j];
	            }

	            for(int j=0;j<voucherData.get(i).size();j++) {
	                c2cData[i][counter_j++]=voucherData.get(i).get(j);
	            }

	        }
	        return c2cData;
	    }
	   

	    public void setupData(String data1,String data2,String data3,String voucherType,String mrp,String activeProfile,String type) {
	    	String types[] =SIT_VMS.getAllowedVoucherTypesForScreen(PretupsI.VOUCHER_TYPE_PHYSICAL);
			List<String> al = Arrays.asList(types);
			if(al.contains(type)) {
	    	List<VoucherDetail> voucherDetailList  =new ArrayList<VoucherDetail>();
	    	VoucherDetail voucherDetail=new VoucherDetail();
			voucherDetail.setVoucherType(voucherType);
			voucherDetail.setDenomination(mrp);
			
			
			String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
			
			voucherDetail.setQuantity(_masterVO.getProperty("voucherQty"));
			String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
			voucherDetail.setVouchersegment(voucherSegment);
			voucherDetailList.add(voucherDetail);
			data.setVoucherDetails(voucherDetailList);
			
	        data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
	        data.setMsisdn(data3);
	        data.setPin(data2);
	        data.setLoginid("");
	        data.setPassword("");
	        data.setExtcode("");
	        data.setExtrefnum("");
	        data.setMsisdn2(data1);
	        data.setLoginid2("");
	        data.setExtcode2("");
	        data.setLanguage1(_masterVO.getProperty("languageCode0"));
	       
	        data.setPaymentinstnum(new RandomGeneration().randomNumeric(5));
	        data.setPaymentinstdate(currentDate);
	        data.setPaymentinstcode(_masterVO.getProperty("paymentInstrumentCode"));

	        
	       
	        data.setRemarks(_masterVO.getProperty("Remarks"));
	        c2cVoucherInitiateRequestPojo.setData(data);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
	    }
	    public void setupDataApproval(String pin,String txnId,String activeProfile,String toMSISDN) {
	    	String status="EN";
			
			String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
			String username = DBHandler.AccessHandler.getUsernameFromMsisdn(toMSISDN);
			String userid= DBHandler.AccessHandler.getUserId(username);
			
			String fromSerialNumber = DBHandler.AccessHandler.getMaxSerialNumberWithuserid(productID, status, userid);
			
			if(fromSerialNumber==null)
				Assertion.assertSkip("Voucher Serial Number not Found");
			
			String toSerialNumber = fromSerialNumber;
	    	
			data1.setLoginid("");
			data1.setPassword("");
			data1.setLanguage1(_masterVO.getProperty("languageCode0"));
			data1.setLanguage2(_masterVO.getProperty("languageCode0"));

			
			data1.setPin(pin);
			data1.setRemarks(_masterVO.getProperty("Remarks"));
			data1.setStatus(_masterVO.getProperty("status_Y"));
			data1.setExtcode("");
			data1.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
			data1.setPaymentinstcode(_masterVO.getProperty("paymentInstrumentTypeCash"));
			data1.setPaymentinstdate(currentDate);

			data1.setTransferId(txnId);
			data1.setType(_masterVO.getProperty("voucherapprovaltype"));

			voucher.setFromSerialNum(fromSerialNumber);
			voucher.setToSerialNum(toSerialNumber);
			voucherDetails.add(voucher);
			data1.setVoucherDetails(voucherDetails);
			c2cVoucherApprovalRequestPojo.setData(data1);;

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
		protected static String accessToken;
	    

		
		public void setupAuth(String data1, String data2) {
			oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
			oAuthenticationRequestPojo.setIdentifierValue(data1);
			oAuthenticationRequestPojo.setPasswordOrSmspin(data2);


		}

		 public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
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
				Assert.assertEquals(statusCode, 200);
				Assertion.assertEquals(Long.toString(statusCode), "200");
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}
		  public C2CVoucherApprovalResponsePojo performC2CVoucherApproval() throws IOException {
				
			  C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
						_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
				c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
				c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
				c2CVoucherApprovalAPI.setExpectedStatusCode(200);
				c2CVoucherApprovalAPI.perform();
				c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
						.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
				return c2cVoucherApprovalResponsePojo;
			}
	    @Test(dataProvider = "userData")
	    public void A_01_Test_success(String loginID, String categoryCode, String msisdn, String PIN, String tocategory, String categoryName, String parentName,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) throws Exception {
	        final String methodName = "A_01_Test_success";
	        Log.startTestCase(methodName);
	        String loginid= DBHandler.AccessHandler.getLoginidFromMsisdn(msisdn2);
	        String  passApproval= DBHandler.AccessHandler.fetchUserPassword(loginid);
	        String password= DBHandler.AccessHandler.fetchUserPassword(loginID);
	        if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI1");
	        moduleCode = CaseMaster.getModuleCode();

	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
	        currentNode.assignCategory("REST");
	        setupData(msisdn2,PIN,msisdn,voucherType,mrp,activeProfile,type);
	        C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	        c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
	        c2CVoucherInitiateAPI.addBodyParam(c2cVoucherInitiateRequestPojo);
	        c2CVoucherInitiateAPI.setExpectedStatusCode(200);
	        c2CVoucherInitiateAPI.perform();
	        c2cVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
	                .getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
	        String statusCode = c2cVoucherInitiateResponsePojo.getDataObject().getTxnstatus();
	        if(statusCode.equals("206"))
	        	Assertion.assertSkip("Voucher Already used");
	        else {
	        Assert.assertEquals("200", statusCode);
	        Assertion.assertEquals(statusCode, "200");
	        }
	        
	        //Voucher Approval
	        
	        BeforeMethod(loginid,passApproval,categoryName);
		       
	        CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT1");
			moduleCode = CaseMaster.getModuleCode();
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
			currentNode.assignCategory("REST");
			
			
	        String txnId = c2cVoucherInitiateResponsePojo.getDataObject().getTxnid();
	        setupDataApproval(PIN,txnId,activeProfile,msisdn2);
	     
	        String value = DBHandler.AccessHandler.getPreference(categoryCode,_masterVO.getMasterValue(MasterI.NETWORK_CODE),PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
			int maxApprovalLevel = Integer.parseInt(value);
			
			if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, categoryName, EventsI.C2CTRANSFER_EVENT)) {

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
		            		C2CVoucherApprovalResponsePojo response = performC2CVoucherApproval();
		            		String statuscode = response.getDataObject().getTxnstatus();
		            		if(statuscode=="200") {
		            		Assert.assertEquals(statuscode, "200");
		            		Assertion.assertEquals("200", statuscode);
		            		
		            		Log.info("Level 1 Success !!");}
		            		else 
		            			Assertion.assertSkip("Voucher Already in use!");
		        		}
		            	if(maxApprovalLevel >= 2)
		        		{
		            		
		            		C2CVoucherApprovalResponsePojo response = performC2CVoucherApproval();
		            		String statuscode = response.getDataObject().getTxnstatus();
		            		
		            		Assert.assertEquals(statuscode, "200");
		            		Assertion.assertEquals("200", statuscode);
		            		
		            		Log.info("Level 2 Success !!");
		            	}
		            	
		            	if(maxApprovalLevel == 3)
		        		{
		            		
		            		C2CVoucherApprovalResponsePojo response = performC2CVoucherApproval();
		            		String statuscode = response.getDataObject().getTxnstatus();
		            		
		            		Assert.assertEquals(statuscode, "200");
		            		Assertion.assertEquals("200", statuscode);
		            	
		            		Log.info("Level 3 Success !!");
		            	
		        		}	     
		           }
			}
	        else {
	            Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + categoryName + "]");
	        }
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
	    }
	    
	    @Test(dataProvider = "userData")
		public void A_03_Test_BlankPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userp,String passAp,String voucherType,String type,String activeProfile, String mrp) throws Exception {
			final String methodName = "Test_C2CVoucherInitiateAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(msisdn2,PIN,msisdn,voucherType,mrp,activeProfile,type);
			C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			data.setPin("");
			c2cVoucherInitiateRequestPojo.setData(data);
			c2CVoucherInitiateAPI.addBodyParam(c2cVoucherInitiateRequestPojo);
			c2CVoucherInitiateAPI.setExpectedStatusCode(400);
			c2CVoucherInitiateAPI.perform();
			c2cVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
			String message =c2cVoucherInitiateResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "PIN can not be blank.");
			Assertion.assertEquals(message, "PIN can not be blank.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
		}
	    @Test(dataProvider = "userData")
		public void A_04_Test_BlankExtnwcode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userp,String passAp,String voucherType,String type,String activeProfile, String mrp) throws Exception {
			final String methodName = "Test_C2CVoucherInitiateAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI4");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(msisdn2,PIN,msisdn,voucherType,mrp,activeProfile,type);
			C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			
			data.setExtnwcode("");
			c2cVoucherInitiateRequestPojo.setData(data);
			c2CVoucherInitiateAPI.addBodyParam(c2cVoucherInitiateRequestPojo);
			c2CVoucherInitiateAPI.setExpectedStatusCode(400);
			c2CVoucherInitiateAPI.perform();
			c2cVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
			String message =c2cVoucherInitiateResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "External network code value is blank.");
			Assertion.assertEquals(message, "External network code value is blank.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	    @Test(dataProvider = "userData")
		public void A_09_Test_InvalidLanguage1(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userp,String passAp,String voucherType,String type,String activeProfile, String mrp) throws Exception {
			final String methodName = "Test_C2CVoucherInitiateAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI9");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(msisdn2,PIN,msisdn,voucherType,mrp,activeProfile,type);
			C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			
			
			data.setLanguage1(new RandomGeneration().randomNumberWithoutZero(2));
			c2cVoucherInitiateRequestPojo.setData(data);
			
			c2CVoucherInitiateAPI.addBodyParam(c2cVoucherInitiateRequestPojo);
			
			c2CVoucherInitiateAPI.setExpectedStatusCode(400);
			c2CVoucherInitiateAPI.perform();
			c2cVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(c2cVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
			
			String message =c2cVoucherInitiateResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "LANGUAGE1 is not valid.");
			Assertion.assertEquals(message, "LANGUAGE1 is not valid.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			Assert.assertEquals(206, statusCode);
			Log.endTestCase(methodName);
		}
	 
	    
	
		@Test(dataProvider = "userData")
		public void A_12_Test_BlankMsisdn2(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userp,String passAp,String voucherType,String type,String activeProfile, String mrp) throws Exception {
			final String methodName = "Test_C2CVoucherInitiateAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI12");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(msisdn2,PIN,msisdn,voucherType,mrp,activeProfile,type);
			C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			
			
			data.setMsisdn2("");
			
			c2CVoucherInitiateAPI.addBodyParam(c2cVoucherInitiateRequestPojo);
			
			c2CVoucherInitiateAPI.setExpectedStatusCode(400);
			c2CVoucherInitiateAPI.perform();
			c2cVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(c2cVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
			
			String message =c2cVoucherInitiateResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "Receiver's mobile number is invalid");
			Assertion.assertEquals(message, "Receiver's mobile number is invalid");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			Assert.assertEquals(206, statusCode);
			Log.endTestCase(methodName);
		}
		@Test(dataProvider = "userData")
		public void A_13_Test_BlankRemark(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userp,String passAp,String voucherType,String type,String activeProfile, String mrp) throws Exception {
			final String methodName = "Test_C2CVoucherInitiateAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI13");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(msisdn2,PIN,msisdn,voucherType,mrp,activeProfile,type);
			C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			
			
			data.setRemarks("");
			
			c2CVoucherInitiateAPI.addBodyParam(c2cVoucherInitiateRequestPojo);
			
			c2CVoucherInitiateAPI.setExpectedStatusCode(400);
			c2CVoucherInitiateAPI.perform();
			c2cVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(c2cVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
			
			String message =c2cVoucherInitiateResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "REMARK can not be blank.");
			Assertion.assertEquals(message, "REMARK can not be blank.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			Assert.assertEquals(206, statusCode);
			Log.endTestCase(methodName);
		}
		
		@Test(dataProvider = "userData")
		public void A_14_Test_BlankPaymentInstrumentCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userp,String passAp,String voucherType,String type,String activeProfile, String mrp) throws Exception {
			final String methodName = "Test_C2CVoucherInitiateAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI14");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(msisdn2,PIN,msisdn,voucherType,mrp,activeProfile,type);
			C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			
			
			data.setPaymentinstcode("");
			
			c2CVoucherInitiateAPI.addBodyParam(c2cVoucherInitiateRequestPojo);
			
			c2CVoucherInitiateAPI.setExpectedStatusCode(400);
			c2CVoucherInitiateAPI.perform();
			c2cVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(c2cVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
			
			String message =c2cVoucherInitiateResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "PAYMENTTYPE can not be blank.");
			Assertion.assertEquals(message, "PAYMENTTYPE can not be blank.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			Assert.assertEquals(206, statusCode);
			Log.endTestCase(methodName);
		}

		@Test(dataProvider = "userData")
		public void A_15_Test_BlankPaymentInstrumentDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userp,String passAp,String voucherType,String type,String activeProfile, String mrp) throws Exception {
			final String methodName = "Test_C2CVoucherInitiateAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI15");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(msisdn2,PIN,msisdn,voucherType,mrp,activeProfile,type);
			C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			
			
			data.setPaymentinstdate("");
			
			c2CVoucherInitiateAPI.addBodyParam(c2cVoucherInitiateRequestPojo);
			
			c2CVoucherInitiateAPI.setExpectedStatusCode(400);
			c2CVoucherInitiateAPI.perform();
			c2cVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(c2cVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
			
			String message =c2cVoucherInitiateResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "Payment instrument date is blank.");
			Assertion.assertEquals(message, "Payment instrument date is blank.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			Assert.assertEquals(206, statusCode);
			Log.endTestCase(methodName);
		}

}
