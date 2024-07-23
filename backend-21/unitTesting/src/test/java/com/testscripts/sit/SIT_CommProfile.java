package com.testscripts.sit;


import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.AssociateProfile;
import com.Features.C2CTransfer;
import com.Features.C2STransfer;
import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.Map_CommissionProfile;
import com.Features.mapclasses.Channel2ChannelMap;
import com.Features.mapclasses.ChannelUserMap;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;
@ModuleManager(name = Module.SIT_CommProfile)
public class SIT_CommProfile extends BaseTest{

	static boolean TestCaseCounter = false;
	String CommProfile;
	String profileName;
	//Map<String,String> dataMap;
	Map_CommissionProfile Map_CommProfile;
	HashMap<String, String> channelMap=new HashMap<>();
	String assignCategory="SIT";
	static String moduleCode;
	ChannelUserMap chnlUsrMap;
	Channel2ChannelMap c2cMap;
	BusinessValidator BusinessValidator;
	HashMap<String, String> paraMap;
	static String networkCode;
	static Object[][] data;
	HashMap<String, String> userAccessMap;
	static int minPaswdLength;
	static int maxPaswdLength;
	HashMap<String,ArrayList<String>> OCPValues = new HashMap<String,ArrayList<String>>();//Other Commission Types and Values
	ArrayList<String[]> OCPTypeValue = new ArrayList<String[]>();
	int OCSize;
	


	@DataProvider(name = "categoryData1")
	public Object[][] TestDataFeed() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		Object[][] categoryData = new Object[1][3];
		categoryData[0][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.GRADE, 1);

		return categoryData;
	}


	@DataProvider(name = "categoryData")
	public Object[][] getCategoryData() {
		String additionalCommission = null;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] Data = new Object[1][3];

		for (int i = 1; i <= rowCount; i++) {
			additionalCommission = ExcelUtility.getCellData(0, ExcelI.ADDITIONAL_COMMISSION, i);
			if (additionalCommission.equals("Y")&&DBHandler.AccessHandler.webInterface(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i)).equals("Y")) {
				Data[0][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				Data[0][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[0][2] = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
				break;
			}
		}

		return Data;
	}
	
    @DataProvider(name = "categoryDataOCP")
    public Object[][] TestDataFeed2() {

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        Object[][] categoryData = new Object[rowCount][4];
        for (int i = 1, j = 0; i <= rowCount; i++, j++) {
            categoryData[j][0] = i;
            categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
            categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
            categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
        }
        return categoryData;
    }


		/**
	 * Mandatory for "other commission" test cases
	 * Loads Other commission types from lookups
	 * Loads the Other commission values from UI and stores by type
	 */
	@Test 
	public void a__AloadOtherCommisionDetails() {
		String methodName = "a__AloadOtherCommisionDetails";
		Log.info("Entered " + methodName);
		if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
			int OCPTypeCount = Integer.parseInt(_masterVO.getProperty("OCPTCount").trim());
			HashMap<String,String> commTypes = new HashMap<String,String>();
			CommissionProfile CommissionProfile = new CommissionProfile(driver);
			ResultSet rs = null ;
			try {
					rs = DBHandler.AccessHandler.getLookupByType(PretupsI.OTH_COMM_TYPE_LOOKUP);
					while(rs.next()) {
						commTypes.put(rs.getString("LOOKUP_CODE"), rs.getString("LOOKUP_NAME"));
					}
					Log.info("Other Commission Type list size: " + commTypes.size());
				}catch(Exception e) {
					Log.info("Exception in loading commission types");
				}
			OCPValues = CommissionProfile.loadOtherCommissionValuesByType(commTypes);
		
		
			for(Map.Entry<String, ArrayList<String>> type: OCPValues.entrySet()) {
				if(type.getValue() != null)
				{
					for(String value: type.getValue()) 
					{
						OCPTypeValue.add(new String[] {type.getKey() , value}); //Other commission Type, Other commission type Value
					}
				}
				else
				{
					int i = 0;
					do
					{
					OCPTypeValue.add(new String[] {type.getKey() , "select default"}); /*Other commission Type only set,
					 																	Other commission type Value to be selected later from data provider*/
					i++;
					}while(i < OCPTypeCount);
				}
			}
			OCSize = OCPTypeValue.size();
		}
	}
	
	public void loadOCPSet(HashMap<String,String> OCPData, int rowNum, String categoryName, String grade) {
		String methodName = "loadOCPSet";
		Log.methodEntry(methodName, rowNum, categoryName, grade);
		
		OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);		
		if(OCPData.get("type").equals(PretupsI.CATEGORY))
		{
			OCPData.put("typeValue",categoryName);
		}
		else if(OCPData.get("type").equals(PretupsI.GRADE)) 
		{
			OCPData.put("typeValue",grade);
		}
		else
		{
			OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);
		}
        OCPData.put("row", Integer.toString(rowNum));
	}
	
	 @Test(dataProvider = "categoryDataOCP")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void a__BCreateOtherCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "a__BCreateOtherCommissionProfile";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE0").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
	        	/*OCSize = OCPTypeValue.size();
		        OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
		        OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);*/
		        loadOCPSet(OCPData, rowNum, categoryName, grade);
		       
		        Log.info("OCPValues: " + OCPValues.toString());
		        OCPData = CommissionProfile.addOtherCommissionProfile(OCPData);
		        String message = MessagesDAO.prepareMessageByKey("profile.addotheradditionalprofile.message.successaddmessage");
		        CommissionProfile.writeOtherCommissionDetails(rowNum,OCPData);
		        Assertion.assertEquals(OCPData.get("message"),message);
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
			}
			Assertion.completeAssertions();
        Log.endTestCase(methodName);
	 }
	 
	 @Test(dataProvider = "categoryDataOCP")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void a__CModifyOtherCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "a__CModifyOtherCommissionProfile";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE2").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
	        	/*OCSize = OCPTypeValue.size();
		        OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
		        OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);
		        OCPData.put("row", Integer.toString(rowNum));*/
		        loadOCPSet(OCPData, rowNum, categoryName, grade);
		       
		        Log.info("OCPValues: " + OCPValues.toString());
		        OCPData = CommissionProfile.modifyOtherCommissionProfile(OCPData);
		        String message = MessagesDAO.prepareMessageByKey("profile.addadditionalotherprofile.message.successeditmessage");
		        CommissionProfile.writeOtherCommissionDetails(rowNum,OCPData);
		        Assertion.assertEquals(OCPData.get("message"),message);
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
			}
			Assertion.completeAssertions();
        Log.endTestCase(methodName);
	 }
	 
	 @Test(dataProvider = "categoryDataOCP")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void a__DViewOtherCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "a__DViewOtherCommissionProfile";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE3").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
	        	/*OCSize = OCPTypeValue.size();
		        OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
		        OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);
		        OCPData.put("row", Integer.toString(rowNum));*/
	        	loadOCPSet(OCPData, rowNum, categoryName, grade);
		       
		        Log.info("OCPValues: " + OCPValues.toString());
		        boolean viewed = CommissionProfile.viewOtherCommissionProfile(OCPData);
		        if(viewed)
		        	Assertion.assertPass("Other commission viewed successfully");
		        else {
		        	Assertion.assertFail("Not able to view profile");
		        	ExtentI.attachScreenShot();
		        }
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
			}
			Assertion.completeAssertions();
        Log.endTestCase(methodName);
	 }
	 
	 @Test
	 @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void a__EOCP_BlankEntry() throws InterruptedException {
	        final String methodName = "a__EOCP_BlankEntry";
	        Log.startTestCase(methodName, "Entered");
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE28").getExtentCase()).assignCategory(TestCategory.SIT);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
		        String actual = CommissionProfile.ocpNegativeBlankEntry();
		        String message = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.label.commissiontype");
		        Assertion.assertContainsEquals(actual,message);
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
	        }
	 }
	 
	 @Test(dataProvider = "categoryDataOCP")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void a__FOCP_TransferBlank(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "a__FOCP_TransferBlank";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCOMMPROFILE29").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
	        	/*OCSize = OCPTypeValue.size();
		        OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
		        OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);*/
	        	loadOCPSet(OCPData, rowNum, categoryName, grade);
		       
		        Log.info("OCPValues: " + OCPValues.toString());
		        OCPData = CommissionProfile.ocpNegativeTransferTypeBlank(OCPData);
		        String message = MessagesDAO.prepareMessageByKey("profile.addothercommissionprofile.error.transactiontypeempty");
		        Assertion.assertEquals(OCPData.get("message"),message);
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
			}
			Assertion.completeAssertions();
        Log.endTestCase(methodName);
	 }
	 
	 @Test(dataProvider = "categoryDataOCP")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void a__GOCP_StartRangeLesser(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "a__GOCP_StartRangeLesser";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCOMMPROFILE30").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
	        	/*OCSize = OCPTypeValue.size();
		        OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
		        OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);*/
	        	loadOCPSet(OCPData, rowNum, categoryName, grade);
		       
		        Log.info("OCPValues: " + OCPValues.toString());
		        OCPData = CommissionProfile.ocpNegativeStartRangeLesser(OCPData);
		        	if("skip".equals(OCPData.get("message")))
		        		Assertion.assertSkip("Rows lesser than 2");
//		        String message = "From range of slab 2 should be greater than To range of slab 1";
		        	String message = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.invalidcommissionslab", 
		        			MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.label.fromrange"),"2",
		        			MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.label.torange"),"1");
		        Assertion.assertEquals(OCPData.get("message"),message);
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
			}
			Assertion.completeAssertions();
        Log.endTestCase(methodName);
	 }

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-963") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_suspendAdditionalCommProfile(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);

		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		String Transfer_ID = null;
		String Transfer_ID1= null;
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE1").getExtentCase());
		currentNode.assignCategory(assignCategory);

		boolean RC_Through_Web = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

		if (!RC_Through_Web){
			currentNode.log(Status.SKIP, "Customer Recharge through Web is not available");
		}
		else{
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

				break;
			}

			System.out.println(i);
			String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
			String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);

			ExtentI.Markup(ExtentColor.TEAL, "Suspend Additional Commission Profile slab");

			long time2 = CommissionProfile.suspendAdditionalCommProfileExisting(domainName, categoryName, grade, CommProfile,_masterVO.getProperty("CustomerRechargeCode"));
			Thread.sleep(time2);

			try{
				Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			}
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
				ExtentI.Markup(ExtentColor.RED, "C2S Transfer is not successful with  error message" + actualMessage);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();


			}

			String TransferIDExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);
			if(Transfer_ID == null|| Transfer_ID.equals("")){
				Assertion.assertFail("TestCase is not successful as Transfer ID : null or blank ");
			}
			else{
				if (!TransferIDExists.equals("Y")){
					Assertion.assertPass("Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				}		
				else 		
				{
					ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
					Assertion.assertFail("TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				}
			}

			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE2").getExtentCase());
			currentNode.assignCategory(assignCategory);

			long time = CommissionProfile.resumeAdditionalCommProfileSpecificService(domainName, categoryName, grade, CommProfile,_masterVO.getProperty("CustomerRechargeCode"));
			ExtentI.Markup(ExtentColor.TEAL, "Resume Additional Commission Profile slab");
			Thread.sleep(time);
			ExtentI.Markup(ExtentColor.TEAL, "Perform C2S transaction after Resuming Additional Commission Profile slab");

			try{
				Transfer_ID1 = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"110",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
				Log.info("The Transaction ID after resuming Additional Commission is:" +Transfer_ID1);
			}
			catch(Exception e){
				String actualMessage1 = driver.findElement(By.xpath("//ol/li")).getText();
				ExtentI.Markup(ExtentColor.RED, "C2S Transfer is not successful with  error message" + actualMessage1);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}

			String TransferIDExists1 = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID1);
			if(Transfer_ID == null|| Transfer_ID.equals("")){
				Assertion.assertFail("TestCase is not successful as Transfer ID : null or blank ");

			}
			else
			{
				if (!TransferIDExists1.equals("Y")){
					Assertion.assertFail("Transaction ID does not exist as: " + Transfer_ID1 + " in Adjustments Table,Hence TestCase is not Successful");
				}		
				else 
				{
					Assertion.assertPass("TestCase is successful as Transfer ID : "+Transfer_ID1+" exists in Adjustments table ");
				}
			}
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-964") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_CommProfileSlabCount(String domainName, String categoryName, String grade)
			throws InterruptedException{
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String actual = commissionProfile.getCommissionSlabCount(domainName, categoryName, grade);
		String Expected = ("Slab Count captured" );
		Assertion.assertEquals(actual, Expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-966") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void c_CommProfileblankSlabValidation(String domainName, String categoryName, String grade)
			throws InterruptedException{
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);

		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE4").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();

		int slabCount = Integer.parseInt(datamap.get("slabCount"));
		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = datamap;
		for(int i=0;i<slabCount;i++){
			if(i%2==0){slabMap.put("Sstart"+i, "");
			slabMap.put("Send"+i, "");}
			else if(i%2!=0){slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
			slabMap.put("Send"+i, datamap.get("A"+(i+1)));	
			}}

		int  AddSlabCount = Integer.parseInt(datamap.get("AddSlabCount"));
		for(int k=0;k<AddSlabCount;k++){
			if(k%2==0){slabMap.put("AddStart"+k, "");
			slabMap.put("AddSend"+k, "");}
			else if(k%2!=0){slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))+1));
			slabMap.put("AddSend"+k, datamap.get("B"+(k+1)));	
			}}


		String [] result = commissionProfile.CommissionProfile_SITValidations(slabMap,domainName, categoryName, grade);

		String actual = result[2];

		String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");
		Validator.messageCompare(actual, Expected);

		String CommProfileName= slabMap.get("ProfileName");
		Log.info("CommProfile for Deletion is:" +CommProfileName);

		ExtentI.Markup(ExtentColor.TEAL, "Deleting the above created Profile");

		String DeletionMessage = commissionProfile.deleteCommProfile(domainName, categoryName, grade, CommProfileName);

		String DeleteExpected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successdeletemessage");

		Assertion.assertEquals(DeletionMessage, DeleteExpected);
		Assertion.completeAssertions();
		Log.endTestCase(methodname);


	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-968") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void d_CreateAdditionalCommProfileforSpecificGateway(String domainName, String categoryName, String grade) throws InterruptedException {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();
		int CLIENTVER = Integer.parseInt(_masterVO.getClientDetail("ADD_COMM_VER"));

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE5").getExtentCase());
		currentNode.assignCategory(assignCategory);

		if(CLIENTVER ==0) {

			Assertion.assertSkip("GatewayType is not available for Selection for this Client Version");
		}
		else{

			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = datamap;


			boolean RC_Through_Web = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

			if (!RC_Through_Web){

				Log.info("As Customer Recharge through Web is not available, Gateway should be EXTGW");
				HashMap<String, String> gatewayInfo = GatewayI.getGatewayInfo("EXTGW");

				slabMap.put("GatewayCode", gatewayInfo.get("REQUEST_GATEWAY_CODE"));

			}
			else{
				slabMap.put("GatewayCode", PretupsI.GATEWAY_TYPE_WEB);
			}
			int slabCount = Integer.parseInt(datamap.get("slabCount"));
			int AddSlabCount = Integer.parseInt(datamap.get("AddSlabCount"));
			for(int j=0;j<slabCount;j++){


				if(j==0){
					slabMap.put("taxRateAmt",_masterVO.getProperty("TaxRate"));
					Log.info("The new value of tax1Value is " + slabMap.get("taxRateAmt"));
					slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))));
					slabMap.put("Send"+j, String.valueOf(Integer.parseInt(datamap.get("A"+(j+1)))));

				}
				else {slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))+1));
				slabMap.put("Send"+j, datamap.get("A"+(j+1)));	
				}}

			for(int k=0;k<AddSlabCount;k++){


				if(k==0){
					slabMap.put("taxRateAmt",_masterVO.getProperty("TaxRate"));
					Log.info("The new value of tax1Value is " + slabMap.get("taxRateAmt"));
					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(datamap.get("B"+(k+1)))));

				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))+1));
				slabMap.put("AddSend"+k, datamap.get("B"+(k+1)));	
				}}



			String [] result = commissionProfile.CommissionProfile_SITValidations(slabMap,domainName, categoryName, grade);

			String actual = result[2];

			String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");

			Assertion.assertEquals(actual, Expected);

			String CommProfileName= slabMap.get("ProfileName");
			Log.info("CommProfile for Deletion is:" +CommProfileName);

			ExtentI.Markup(ExtentColor.TEAL, "Deleting the above created Profile");

			String DeletionMessage = commissionProfile.deleteCommProfile(domainName, categoryName, grade, CommProfileName);

			String DeleteExpected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successdeletemessage");

			Assertion.assertEquals(DeletionMessage, DeleteExpected);
			Assertion.completeAssertions();
			Log.endTestCase(methodname);
		}
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-969") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void e_GatewayCodeisMandatory(String domainName, String categoryName, String grade) throws InterruptedException {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();
		int CLIENTVER = Integer.parseInt(_masterVO.getClientDetail("ADD_COMM_VER"));


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE6").getExtentCase());
		currentNode.assignCategory(assignCategory);

		if(CLIENTVER ==0){
			Assertion.assertSkip("GatewayType is not available for Selection for this Version");
		}
		else{

			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = datamap;
			slabMap.put("GatewayCode", "");
			int slabCount = Integer.parseInt(datamap.get("slabCount"));
			for(int j=0;j<slabCount;j++){
				if(j==0){
					slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))));
					slabMap.put("Send"+j, String.valueOf(Integer.parseInt(datamap.get("A"+(j+1)))));
				}
				else {slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))+1));
				slabMap.put("Send"+j, datamap.get("A"+(j+1)));	
				}}

			int AddSlabCount = Integer.parseInt(datamap.get("AddSlabCount"));
			for(int k=0;k<AddSlabCount;k++){
				if(k==0){
					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(datamap.get("B"+(k+1)))));
				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))+1));
				slabMap.put("AddSend"+k, datamap.get("B"+(k+1)));	
				}}


			commissionProfile.CommissionProfile_SITValidations(slabMap,domainName, categoryName, grade);

			String actual = CONSTANT.ADDCOMM_SLAB_ERR;

			String Expected = MessagesDAO.prepareMessageByKey("errors.required",MessagesDAO.getLabelByKey("profile.addadditionalprofile.label.gatewaycode"));

			Assertion.assertEquals(actual, Expected);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-971") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void f_CreateAdditionalCommProfileforSpecificServiceAndGatewayALL(String domainName, String categoryName, String grade) throws InterruptedException {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();
		int CLIENTVER = Integer.parseInt(_masterVO.getClientDetail("ADD_COMM_VER"));


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if(CLIENTVER==0){
			Assertion.assertSkip("GatewayType is not available for Selection in this Version"); 
		}
		else
		{
			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = datamap;
			slabMap.put("GatewayCode", PretupsI.GATEWAY_TYPE_ALL);
			int slabCount = Integer.parseInt(datamap.get("slabCount"));
			for(int j=0;j<slabCount;j++){


				if(j==0){

					slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))));
					slabMap.put("Send"+j, String.valueOf(Integer.parseInt(datamap.get("A"+(j+1)))));

				}
				else {slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))+1));
				slabMap.put("Send"+j, datamap.get("A"+(j+1)));	
				}}

			int AddSlabCount = Integer.parseInt(datamap.get("AddSlabCount"));
			for(int k=0;k<AddSlabCount;k++){
				if(k==0){
					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(datamap.get("B"+(k+1)))));
				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))+1));
				slabMap.put("AddSend"+k, datamap.get("B"+(k+1)));	
				}}


			String [] result = commissionProfile.CommissionProfile_SpecificService(slabMap,domainName, categoryName, grade);

			String actual = result[2];

			String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");

			Assertion.assertEquals(actual, Expected);

			String CommProfileName= slabMap.get("ProfileName");
			Log.info("CommProfile for Deletion is:" +CommProfileName);

			ExtentI.Markup(ExtentColor.TEAL, "Deleting the above created Profile");

			String DeletionMessage = commissionProfile.deleteCommProfile(domainName, categoryName, grade, CommProfileName);

			String DeleteExpected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successdeletemessage");

			Assertion.assertEquals(DeletionMessage, DeleteExpected);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-972") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void g_CreateAdditionalCommProfileforSpecificServiceAndSpecificGateway(String domainName, String categoryName, String grade) throws InterruptedException {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();
		int CLIENTVER = Integer.parseInt(_masterVO.getClientDetail("ADD_COMM_VER"));


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE8").getExtentCase());
		currentNode.assignCategory(assignCategory);

		if(CLIENTVER==0){
			Assertion.assertSkip("GatewayType is not available for Selection in this Version"); 
		}
		else
		{

			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = datamap;
			boolean RC_Through_Web = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

			if (!RC_Through_Web){

				Log.info("As Customer Recharge through Web is not available, Gateway should be EXTGW");
				HashMap<String, String> gatewayInfo = GatewayI.getGatewayInfo("EXTGW");

				slabMap.put("GatewayCode", gatewayInfo.get("REQUEST_GATEWAY_CODE"));
			}
			else{
				slabMap.put("GatewayCode", PretupsI.GATEWAY_TYPE_WEB);
			}
			int slabCount = Integer.parseInt(datamap.get("slabCount"));
			for(int j=0;j<slabCount;j++){


				if(j==0){

					slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))));
					slabMap.put("Send"+j, String.valueOf(Integer.parseInt(datamap.get("A"+(j+1)))));

				}
				else {slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))+1));
				slabMap.put("Send"+j, datamap.get("A"+(j+1)));	
				}}

			int AddSlabCount = Integer.parseInt(datamap.get("AddSlabCount"));
			for(int k=0;k<AddSlabCount;k++){
				if(k==0){
					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(datamap.get("B"+(k+1)))));
				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))+1));
				slabMap.put("AddSend"+k, datamap.get("B"+(k+1)));	
				}}


			String [] result = commissionProfile.CommissionProfile_SpecificService(slabMap,domainName, categoryName, grade);

			String actual = result[2];

			String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");

			Assertion.assertEquals(actual, Expected);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-974") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void h_AdditionalCommProfileTimeSlabValidation(String domainName, String categoryName, String grade) throws InterruptedException {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();
		int CLIENTVER = Integer.parseInt(_masterVO.getClientDetail("ADD_COMM_VER"));


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE9").getExtentCase());
		currentNode.assignCategory(assignCategory);

		if(CLIENTVER==0){
			Assertion.assertSkip("TimeSlab is not available for Selection in this Version"); 
		}
		else
		{

			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = datamap;
			slabMap.put("GatewayCode", PretupsI.GATEWAY_TYPE_ALL);
			slabMap.put("TimeSlab", "$$:00-**:&&");
			int slabCount = Integer.parseInt(datamap.get("slabCount"));
			//int slabCount = 5;

			for(int j=0;j<slabCount;j++){


				if(j==0){

					slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))));
					slabMap.put("Send"+j, String.valueOf(Integer.parseInt(datamap.get("A"+(j+1)))));

				}
				else {slabMap.put("Sstart"+j, String.valueOf(Integer.parseInt(datamap.get("A"+j))+1));
				slabMap.put("Send"+j, datamap.get("A"+(j+1)));	
				}}

			int AddSlabCount = Integer.parseInt(datamap.get("AddSlabCount"));
			//int AddSlabCount = 5;
			for(int k=0;k<AddSlabCount;k++){
				if(k==0){
					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(datamap.get("B"+(k+1)))));
				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))+1));
				slabMap.put("AddSend"+k, datamap.get("B"+(k+1)));	
				}}


			commissionProfile.CommissionProfile_SpecificService(slabMap,domainName, categoryName, grade);

			String actual = slabMap.get("AddCommSlabErrorMessage");

			String Expected = MessagesDAO.prepareMessageByKey("error.msg.invalidchars");

			Assertion.assertEquals(actual, Expected);

		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-975") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void i_CommProfileMaxRangeValidation(String domainName, String categoryName, String grade)
			throws InterruptedException{
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE10").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();

		int p = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int z = p+1;

		String value1 = String.valueOf(z);
		int slabCount = Integer.parseInt(datamap.get("slabCount"));
		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = datamap;
		for(int i=0;i<slabCount;i++){

			if(i==0){
				slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))));
				slabMap.put("Send"+i, String.valueOf(Integer.parseInt(datamap.get("A"+(i+1)))));
			}
			else if(i==(slabCount-1)){
				slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
				datamap.put("A"+(i+1),value1);
				slabMap.put("Send"+i, datamap.get("A"+(i+1)));
			}
			else 
			{slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
			slabMap.put("Send"+i, datamap.get("A"+(i+1)));	
			}}


		String[] result1 = commissionProfile.CommissionProfile_SITValidations(datamap,domainName, categoryName, grade);

		String Expected = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.invalidslabsentry",Map_CommProfile.getCommMap("MintransferValue"),Map_CommProfile.getCommMap("MaxtransferValue"));

		Assertion.assertEquals(result1[2], Expected);

		Assertion.completeAssertions();
		Log.endTestCase(methodname);

	}






	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-976") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void j_CommProfileSlabToRangeValidation(String domainName, String categoryName, String grade)
			throws InterruptedException{
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);

		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE11").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();
		datamap.put("A1", "0");

		int slabCount = Integer.parseInt(datamap.get("slabCount"));
		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = datamap;
		for(int i=0;i<slabCount;i++){
			/*if(i==4){
			slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
		slabMap.put("Send"+i, String.valueOf(Integer.parseInt(datamap.get("A"+(i+1)))+1));}*/

			if(i==0){
				slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))));
				slabMap.put("Send"+i, String.valueOf(Integer.parseInt(datamap.get("A"+(i+1)))));

			}
			else {slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
			slabMap.put("Send"+i, datamap.get("A"+(i+1)));	
			}}


		commissionProfile.CommissionProfile_SITValidations(datamap,domainName, categoryName, grade);

		String actual = datamap.get("SlabErrorMessage");

		String Expected = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.torangeinvalid","1");

		Assertion.assertEquals(actual, Expected);

		Assertion.completeAssertions();
		Log.endTestCase(methodname);

	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-977") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void k_CommProfileblankToRangeValidation(String domainName, String categoryName, String grade)
			throws InterruptedException{
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);

		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE12").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();



		/*datamap.put("slabCount", "5");
		datamap.put("A1", "");*/
		int slabCount = Integer.parseInt(datamap.get("slabCount"));
		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = datamap;
		for(int i=0;i<slabCount;i++){
			if(i==0){
				slabMap.put("Sstart"+i,String.valueOf(Integer.parseInt(datamap.get("A"+i))));
				slabMap.put("Send"+i, "");}

			else {slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
			slabMap.put("Send"+i, datamap.get("A"+(i+1)));	
			}}


		String [] result = commissionProfile.CommissionProfile_SITValidations(slabMap,domainName, categoryName, grade);

		String actual = result[2];

		String Expected = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.torangerequired",MessagesDAO.getLabelByKey("profile.commissionprofiledetailview.label.torange"),"1");

		Assertion.assertEquals(actual, Expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodname);

	}




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-978") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void l_CommProfileSameSlabEntryValidation(String domainName, String categoryName, String grade)
			throws InterruptedException{
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE13").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();


		int slabCount = Integer.parseInt(datamap.get("slabCount"));
		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = datamap;
		for(int i=0;i<slabCount;i++){


			if(i==0){
				slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))));
				slabMap.put("Send"+i, String.valueOf(Integer.parseInt(datamap.get("A"+(i+1)))));

			}
			else if(i==1){
				slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+(i-1)))));
				slabMap.put("Send"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i)))); 

			}
			else {slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
			slabMap.put("Send"+i, datamap.get("A"+(i+1)));	
			}}


		commissionProfile.CommissionProfile_SITValidations(datamap,domainName, categoryName, grade);

		String actual = datamap.get("SlabErrorMessage");

		String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.invalidcommissionslab",MessagesDAO.getLabelByKey("profile.addadditionalprofile.label.fromrange"),"2",MessagesDAO.getLabelByKey("profile.addadditionalprofile.label.torange"),"1");

		Assertion.assertEquals(actual, Expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-979") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void m_CommProfileTax1EntryValidation(String domainName, String categoryName, String grade)
			throws InterruptedException{
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);

		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE14").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String,String> datamap=Map_CommProfile.DataMap_CommissionProfile();


		int slabCount = Integer.parseInt(datamap.get("slabCount"));

		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = datamap;
		for(int i=0;i<slabCount;i++){

			if(i==0){
				slabMap.put("taxRateAmt", String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
				slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))));
				slabMap.put("Send"+i, String.valueOf(Integer.parseInt(datamap.get("A"+(i+1)))));

			}
			else if(i==1){

				slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
				slabMap.put("Send"+i, String.valueOf(Integer.parseInt(datamap.get("A"+(i+1)))));
			}


			else {slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
			slabMap.put("Send"+i, datamap.get("A"+(i+1)));	
			}}


		commissionProfile.CommissionProfile_SITValidations(datamap,domainName, categoryName, grade);

		String actual = datamap.get("SlabErrorMessage");

		String Expected = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.invalidtax1rate","1.0 ("+MessagesDAO.getLabelByKey("batchModifyCommProfile.commission.profile.from.range")+")");

		Assertion.assertEquals(actual, Expected);

		Assertion.completeAssertions();
		Log.endTestCase(methodname);


	}


	/*@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-980") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void n_ModifyFromRangeOfAdditionalCommProfile(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		String Transfer_ID = null;
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile(); 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE15").getExtentCase());
		currentNode.assignCategory(assignCategory);

		boolean RC_Through_Web = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

		if (!RC_Through_Web){
			Assertion.assertSkip("Customer Recharge through Web is not available");
		}
		else{
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

				break;
			}

			System.out.println(i);


			//String LoginId = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
			String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);

			//int slabCount = Integer.parseInt(AddCommMap.get("slabCountAdditional"));
			int slabCount =Integer.parseInt(AddCommMap.get("slabCount"));

			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = AddCommMap;
			for(int k=0;k<slabCount;k++){


				if(k==0){
					slabMap.put("Sstart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("A"+k))));
					slabMap.put("Send"+k, String.valueOf(Integer.parseInt(AddCommMap.get("A"+(k+1)))));
				}
				else {slabMap.put("Sstart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("A"+k))+1));
				slabMap.put("Send"+k, AddCommMap.get("A"+(k+1)));	
				}}


			int AddSlabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));

			for(int j=0;j<AddSlabCount;j++){
				if(j==0){
					slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+100));
					slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));
				}
				else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
				slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
				}}


			long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap,domainName, categoryName, grade, CommProfile);
			ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
			Thread.sleep(time2);

			try{


				Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			}
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
				Assertion.assertFail( "C2S Transfer is not successful with  error message" + actualMessage);

			}


			for(int j=0;j<AddSlabCount;j++){
				if(j==0){
					slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
					slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));
				}
				else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
				slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
				}}

			ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
			CommissionProfile.modifyAdditionalCommissionProfile_SIT(slabMap,domainName, categoryName, grade, CommProfile);
			ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

			String TransferIDExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);

			if(Transfer_ID == null|| Transfer_ID.equals("")){
				Assertion.assertFail("TestCase is not successful as Transfer ID : null or blank ");
			}
			else{
				if (!TransferIDExists.equals("Y")){
					Assertion.assertPass("Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				}		
				else 		
				{
					Assertion.assertFail("TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				}
			}
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}*/

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-982") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void o_TaxValidationOfAdditionalCommProfile(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile(); 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE16").getExtentCase());
		currentNode.assignCategory(assignCategory);

		boolean RC_Through_Web = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

		if (!RC_Through_Web){
			Assertion.assertSkip("Customer Recharge through Web is not available");
		}
		else{
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

				break;
			}

			System.out.println(i);

			//String LoginId = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);


			int AddSlabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));

			//int AddSlabCount = 5;
			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = AddCommMap;
			for(int k=0;k<AddSlabCount;k++){


				if(k==0){
					slabMap.put("taxRateAmt", String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
				slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
				}}

			try {
				CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap,domainName, categoryName, grade, CommProfile);
			} catch (Exception ex) {
				String actual = AddCommMap.get("SlabErrorMessage");
				Log.info("actual message is :" +actual);
				String Expected = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.invalidtax1rate","1.0 ("+MessagesDAO.getLabelByKey("batchModifyCommProfile.commission.profile.from.range")+")");

				Assertion.assertEquals(actual, Expected);
			}
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-983") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void p_TaxValidationOnC2S(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		String Transfer_ID = null;
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		_parser parser = new _parser();
		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile(); 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE17").getExtentCase());
		currentNode.assignCategory(assignCategory);

		boolean RC_Through_Web = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

		if (!RC_Through_Web){
			Assertion.assertSkip("Customer Recharge through Web is not available");
		}
		else{
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

				break;
			}

			System.out.println(i);

			String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
			String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);

			int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
			//int slabCount = 5;
			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = AddCommMap;
			for(int k=0;k<slabCount;k++){


				if(k==0){
					slabMap.put("addcommrate1",String.valueOf(Integer.parseInt(AddCommMap.get("A"+k))+4));
					slabMap.put("taxRateAmt", String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+2));
					Log.info("The new value of tax1Value is " + slabMap.get("taxRateAmt"));
					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+10));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
				slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
				}}



			long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap,domainName, categoryName, grade, CommProfile);

			ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
			Thread.sleep(time2);

			try{
				Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			}
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
				Assertion.assertPass("C2S Transfer is not successful with  error message" + actualMessage);
			}

			long Tax1Value = DBHandler.AccessHandler.getAdditionalTax1Value(Transfer_ID);
			String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);
			String tax1Value=String.valueOf(parser.getDisplayAmount(Tax1Value));

			if(Transfer_ID == null|| Transfer_ID.equals("")){
				Assertion.assertFail("TestCase is not successful as Transfer ID : null or blank ");
			}
			else if (!transferIdExists.equalsIgnoreCase("Y")){ 
				Assertion.assertFail("TestCase is not successful");
			}

			else{

				if (tax1Value.equals(slabMap.get("taxRateAmt"))){
					Assertion.assertPass("Transaction ID exist as: " + Transfer_ID + " in Adjustments Table with tax1 value as: " +(slabMap.get("taxRateAmt"))+ ",Hence TestCase is Successful");
				}		
				else 
				{
					Assertion.assertFail("TestCase is not successful as Tax1 Value for TxnId : "+Transfer_ID+" is not equal to " +(slabMap.get("taxRateAmt"))+ " in Adjustments table");
				}

			}

			for(int j=0;j<slabCount;j++){


				if(j==0){
					slabMap.put("addcommrate1",String.valueOf(Integer.parseInt(AddCommMap.get("A"+j))));
					slabMap.put("taxRateAmt", String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
					slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
					slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));

				}
				else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
				slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
				}}
			ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
			CommissionProfile.modifyAdditionalCommissionProfile_SIT(slabMap,domainName, categoryName, grade, CommProfile);
			ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

			String actual = slabMap.get("ActualMessage");

			String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");

			Assertion.assertEquals(actual, Expected);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-984") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void q_TimeSlabValidationOnC2S(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		String Transfer_ID = null;
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		int CLIENTVER = Integer.parseInt(_masterVO.getClientDetail("ADD_COMM_VER"));
		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile(); 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE18").getExtentCase());
		currentNode.assignCategory(assignCategory);

		if(CLIENTVER==0){
			Assertion.assertSkip("GatewayType is not available for Selection in this Version"); 
		}
		else
		{

			boolean RC_Through_Web = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

			if (!RC_Through_Web){
				Assertion.assertSkip("Customer Recharge through Web is not available");
			}
			else{


				String MasterSheetPath = _masterVO.getProperty("DataProvider");
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
				int totalRow1 = ExcelUtility.getRowCount();

				int i=1;
				for( i=1; i<=totalRow1;i++)

				{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

					break;
				}

				System.out.println(i);

				String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
				String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);

				int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
				//int slabCount = 5;
				Map<String,String> slabMap = new HashMap<String, String>();

				slabMap = AddCommMap;
				for(int k=0;k<slabCount;k++){


					if(k==0){

						slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+10));
						slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

					}
					else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
					slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
					}}



				long time2 =CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab(slabMap,domainName, categoryName, grade, CommProfile,_masterVO.getProperty("CustomerRechargeCode"));

				ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
				Thread.sleep(time2);

				try{
					Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
				}
				catch(Exception e){
					String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
					Assertion.assertFail("C2S Transfer is not successful with  error message" + actualMessage);
				}


				String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);


				if(Transfer_ID == null|| Transfer_ID.equals("")){
					Assertion.assertFail("TestCase is not successful as Transfer ID : null or blank ");
				}
				else{
					if (!transferIdExists.equals("Y")){
						Assertion.assertPass("Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
					}		
					else 		
					{
						Assertion.assertFail( "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
					}
				}

				for(int j=0;j<slabCount;j++){


					if(j==0){

						slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
						slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));

					}
					else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
					slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
					}}
				ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
				long time = CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap,domainName, categoryName, grade, CommProfile);
				ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");
				Thread.sleep(time);
				String actual = AddCommMap.get("ActualMessage");

				String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");

				Assertion.assertEquals(actual, Expected);
			}
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}





	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-985") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void r_TimeSlabValidationCAC(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);

		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);


		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile(); 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE19").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String targetbasedCommission = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), "TARGET_BASED_BASE_COMMISSION");

		if(!BTSLUtil.isNullString(targetbasedCommission)&&targetbasedCommission.equalsIgnoreCase("true")){

			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

				break;
			}

			System.out.println(i);

			String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);

			int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = AddCommMap;

			for(int k=0;k<slabCount;k++){


				if(k==0){

					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+10));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
				slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
				}}



			CommissionProfile.modifyAdditionalCommissionProfile_CAC_CBCValidations(AddCommMap,domainName, categoryName, grade, CommProfile,_masterVO.getProperty("CustomerRechargeCode"));



			String actual = AddCommMap.get("SlabErrorMessage");
			Log.info("Actual is:" + actual);

			String Expected = MessagesDAO.prepareMessageByKey("profile.commissionprofile.error.invalid.otfTimeSlab","1","CAC");

			Assertion.assertEquals(actual, Expected);


		}
		else {
			Assertion.assertSkip("CAC CBC slabs are not available");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-987") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void s_TimeSlabFormatValidationCAC(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);

		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile(); 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE20").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String targetbasedCommission = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), "TARGET_BASED_BASE_COMMISSION");

		if(!BTSLUtil.isNullString(targetbasedCommission)&&targetbasedCommission.equalsIgnoreCase("true")){

			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

				break;
			}

			System.out.println(i);

			String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);

			int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = AddCommMap;
			slabMap.put("TimeSlab", "-");

			for(int k=0;k<slabCount;k++){


				if(k==0){

					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+10));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
				slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
				}}

			CommissionProfile.modifyAdditionalCommissionProfile_CAC_CBCValidations(slabMap,domainName, categoryName, grade, CommProfile,_masterVO.getProperty("CustomerRechargeCode"));

			String actual = slabMap.get("SlabErrorMessage");
			String Expected = MessagesDAO.prepareMessageByKey("error.msg.invalidtimeformat");


			Assertion.assertEquals(actual, Expected);

		}
		else {
			Assertion.assertSkip("CAC CBC slabs are not available");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}





	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-988") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void t_CommissionProfileselectionMandatory(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);


		CommissionProfile CommissionProfile = new CommissionProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE21").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String actual = CommissionProfile.modifyCommissionProfile_SITNeg(domainName, categoryName, grade);
		System.out.println(actual);
		String Expected = MessagesDAO.prepareMessageByKey("errors.required",MessagesDAO.getLabelByKey("profile.selectcommissionprofileset.label.commissionprofileset"));	
		Assertion.assertEquals(actual, Expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}









	/*

	@Test(dataProvider = "categoryData")
	public void u_C2SAfterDeletingAddComm(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Commission Profile");
			TestCaseCounter = true;
		}

		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		ChannelUser channelUser= new ChannelUser(driver);

		Map<String,String> datamap = Map_CommProfile.DataMap_CommissionProfile(); 
		currentNode = test.createNode("To verify that C2S will not be successful, if The Recharge is not done in the Time slab mentioned in the additional Commission Profile");
		currentNode.assignCategory("SIT");

		int slabCount = Integer.parseInt(datamap.get("slabCount"));
		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = datamap;
		for(int i=0;i<slabCount;i++){

			if(i==0){
				slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))));
				slabMap.put("Send"+i, String.valueOf(Integer.parseInt(datamap.get("A"+(i+1)))));

			}
			else if(i==1){

				slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
				slabMap.put("Send"+i, String.valueOf(Integer.parseInt(datamap.get("A"+(i+1)))));
			}


			else {slabMap.put("Sstart"+i, String.valueOf(Integer.parseInt(datamap.get("A"+i))+1));
			slabMap.put("Send"+i, datamap.get("A"+(i+1)));	
			}}
		int AddSlabCount = Integer.parseInt(datamap.get("AddSlabCount"));
		for(int k=0;k<AddSlabCount;k++){

			if(k==0){
				slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))));
				slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(datamap.get("B"+(k+1)))));

			}
			else if(k==1){

				slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))+1));
				slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(datamap.get("B"+(k+1)))));
			}


			else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(datamap.get("B"+k))+1));
			slabMap.put("AddSend"+k, datamap.get("B"+(k+1)));	
			}}


		CommissionProfile.CommissionProfile_SITValidations(datamap,domainName, categoryName, grade);

		String actual = datamap.get("ActualMessage");

		String Expected = MessagesDAO.prepareMessageByKey("");

		Validator.messageCompare(actual, Expected);

		String ProfileName = datamap.get("ProfileName");
		Log.info("The created Profile name is " +ProfileName);

		//Modify Channel User to associate the above created Commission Profile 

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

			break;
		}

		System.out.println(i);
		String UserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
		String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
		channelMap=channelUser.channelUserInitiate(RowNum, domain, parentCategory, ToCategory, geoType);
		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("USER_APPROVAL_LEVEL");
		if(APPLEVEL.equals("2"))
		{channelUser.approveLevel1_ChannelUser();
		channelUser.approveLevel2_ChannelUser();
		}
		else if(APPLEVEL.equals("1")){
			channelUser.approveLevel1_ChannelUser();	
		}else{
			Log.info("Approval not required.");	
		}

		 String newToMSISDN =channelMap.get("MSISDN");

		Log.info("Newly created User Name:" +channelMap.get("UserName") );

	}

	 */




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-991") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void u_MinRangeValidationOnC2S(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		String Transfer_ID = null;
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		_parser parser = new _parser();
		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile(); 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE22").getExtentCase());
		currentNode.assignCategory(assignCategory);

		boolean RC_Through_Web = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

		if (!RC_Through_Web){
			Assertion.assertSkip("Customer Recharge through Web is not available");
		}
		else{
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

				break;
			}

			System.out.println(i);

			String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
			String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);

			int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));

			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = AddCommMap;

			slabMap.put("MintransferValue", "110");
			for(int k=0;k<slabCount;k++){


				if(k==0){

					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+200));
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
				slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
				}}



			long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap,domainName, categoryName, grade, CommProfile);

			ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
			Thread.sleep(time2);

			try{
				Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			}
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
				Assertion.assertFail("C2S Transfer is not successful with  error message" + actualMessage);
			}

			String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);


			if(Transfer_ID == null|| Transfer_ID.equals("")){
				Assertion.assertFail("TestCase is not successful as Transfer ID : null or blank ");
			}
			else{
				if (!transferIdExists.equals("Y")){
					Assertion.assertPass("Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				}		
				else 		
				{
					Assertion.assertFail("TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				}
			}

			slabMap.put("MintransferValue", _masterVO.getProperty("MintransferValue"));

			for(int j=0;j<slabCount;j++){


				if(j==0){


					slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
					slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));

				}
				else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
				slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
				}}
			ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
			CommissionProfile.modifyAdditionalCommissionProfile_SIT(slabMap,domainName, categoryName, grade, CommProfile);
			ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

			String actual = slabMap.get("ActualMessage");

			String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");

			Assertion.assertEquals(actual, Expected);


		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-992") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void v_ToRangeValidationOnC2S(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		String Transfer_ID = null;
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		_parser parser = new _parser();
		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile(); 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE23").getExtentCase());
		currentNode.assignCategory(assignCategory);

		boolean RC_Through_Web = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

		if (!RC_Through_Web){
			Assertion.assertSkip("Customer Recharge through Web is not available");
		}
		else{
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

				break;
			}

			System.out.println(i);

			String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
			String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);

			int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
			//int slabCount = 5;
			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = AddCommMap;


			for(int k=0;k<slabCount;k++){


				if(k==0){

					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))));
					slabMap.put("AddSend"+k, "90");

				}
				else if(k==1){

					slabMap.put("AddStart"+k, "200");
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
				slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
				}}



			long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap,domainName, categoryName, grade, CommProfile);

			ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
			Thread.sleep(time2);

			try{
				Transfer_ID = c2STransfer.performC2STransfer(parentCategory,categoryName ,Pin,_masterVO.getProperty("CustomerRechargeCode"),"100",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			}
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
				Assertion.assertFail("C2S Transfer is not successful with  error message" + actualMessage);
			}

			String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);


			if(Transfer_ID == null|| Transfer_ID.equals("")){
				Assertion.assertFail("TestCase is not successful as Transfer ID : null or blank ");
			}
			else{
				if (!transferIdExists.equals("Y")){
					Assertion.assertPass("Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				}		
				else 		
				{
					Assertion.assertFail("TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				}
			}



			for(int j=0;j<slabCount;j++){


				if(j==0){


					slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
					slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));

				}
				else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
				slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
				}}
			ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
			CommissionProfile.modifyAdditionalCommissionProfile_SIT(slabMap,domainName, categoryName, grade, CommProfile);
			ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

			String actual = slabMap.get("ActualMessage");

			String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");

			Assertion.assertEquals(actual, Expected);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);

	}
	


	
	
	//@Test(dataProvider = "categoryData")
	public void w_CBCValidationOnC2C(String domainName, String categoryName, String grade)
			throws InterruptedException, Throwable {
		final String methodname = "Test_CommProfile";
		Log.startTestCase(methodname);
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCOMMPROFILE24").getModuleCode();
		
		String Transfer_ID = null;
		Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		C2CTransfer c2cTransfer = new C2CTransfer(driver);
		chnlUsrMap = new ChannelUserMap();
		c2cMap = new Channel2ChannelMap();
		networkCode = _masterVO.getMasterValue("Network Code");
		String type = "CHANNEL";
		data = DBHandler.AccessHandler.getProductDetails(networkCode, c2cMap.getC2CMap("domainCode"),
				c2cMap.getC2CMap("fromCategoryCode"), c2cMap.getC2CMap("toCategoryCode"), type);
		
		paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);
		BusinessValidator = new BusinessValidator();
		c2cTransfer = new C2CTransfer(driver);
		_parser parser = new _parser();
		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile(); 
		String productCode = null;
		String expMessage = null;
		String expMessage1 = null;
		String productType = null;
		String productName = null;
		String shortName = null;
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE24").getExtentCase());
		currentNode.assignCategory(assignCategory);

		
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int totalRow1 = ExcelUtility.getRowCount();

			int i=1;
			for( i=1; i<=totalRow1;i++)

			{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(categoryName)))

				break;
			}

			System.out.println(i);

			String CommProfile = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
			String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);

			int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
			//int slabCount = 5;
			Map<String,String> slabMap = new HashMap<String, String>();

			slabMap = AddCommMap;


			for(int k=0;k<slabCount;k++){


				if(k==0){

					slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))));
					slabMap.put("AddSend"+k, "90");

				}
				else if(k==1){

					slabMap.put("AddStart"+k, "200");
					slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

				}
				else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
				slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
				}}



			long time2 =CommissionProfile.modifyCommissionProfile_SIT(AddCommMap,domainName, categoryName, grade, CommProfile);

			ExtentI.Markup(ExtentColor.TEAL, "Modified Commission Profile slab");
			Thread.sleep(time2);

			try{
			
				Map<String, String> c2cMapCRDR = null;
				
				for (int productCount = 0; productCount <= data.length; productCount++) {
					if (productCount <= (data.length - 1)) {
						
						businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), c2cMap.getC2CMap("fromMSISDN"), c2cMap.getC2CMap("toMSISDN"));
						HashMap<String, String> initiatedQty = new HashMap<String, String>();
						
						TransactionVO TransactionVO = businessController.preparePreTransactionVO();
						TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
						productCode = data[productCount][0].toString();
						productName = data[productCount][1].toString();
						shortName = data[productCount][2].toString();
						productType = data[productCount][3].toString();

						//currentNode = test.createNode(MessageFormat.format(SITCOMMPROFILE24.getExtentCase(), shortName));
						//currentNode.assignCategory("SIT");

						String balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));
						parser.convertStringToLong(balance).changeDenomation();
						long usrBalance = (long) (parser.getValue() * 0.2);
						String quantity = String.valueOf(usrBalance);
						initiatedQty.put(productCode, quantity);
					
						c2cMapCRDR=new C2CTransfer(driver).channel2channelTransfer(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"),	c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),"false");
						String actualMessage = c2cMapCRDR.get("actualMessage");
						String expectedMessage = c2cMapCRDR.get("expectedMessage");
						Validator.messageCompare(actualMessage, expectedMessage);
					
				//new BusinessValidatorvalidateCRDR(c2cMap.getC2CMap("toMSISDN"), _masterVO.getProperty("C2CTransferCode"), productCode, String.valueOf(usrBalance), c2cMapCRDR.get("TransactionID"));;
				
				TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
				BusinessValidator.validateStocks(TransactionVO);
					}
				}
			}
			
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
				ExtentI.Markup(ExtentColor.RED, "C2S Transfer is not successful with  error message" + actualMessage);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}

			String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);


			if(Transfer_ID == null|| Transfer_ID.equals("")){
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
			}
			else{
				if (!transferIdExists.equals("Y")){
					ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
					currentNode.log(Status.PASS,"Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				}		
				else 		
				{
					ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
					currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				}
			}



			for(int j=0;j<slabCount;j++){


				if(j==0){


					slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
					slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));

				}
				else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
				slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
				}}
			ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
			CommissionProfile.modifyAdditionalCommissionProfile_SIT(slabMap,domainName, categoryName, grade, CommProfile);
			ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

			String actual = slabMap.get("ActualMessage");

			String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");

			Validator.messageCompare(actual, Expected);


		}


    //@Test(dataProvider = "categoryData")
	public void x_commissionProfileStatusChangewithoutLanguage1Message(String domainName, String categoryName, String grade)
			throws InterruptedException {

		Log.startTestCase(this.getClass().getName());
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCOMMPROFILE25").getModuleCode();
		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE25").getExtentCase());
		currentNode.assignCategory(assignCategory);

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		String[] result = commissionProfile.addCommissionProfile(domainName, categoryName, grade);
		System.out.println(result);
		CommProfile=result[1];
		System.out.println("The Created Commission profile name is : " + CommProfile);
		
		
		String actual = commissionProfile.CommissionProfileDeactivatedNegative(domainName, categoryName, grade,CommProfile,"","deactivating Profile");
		
		
		String Message = MessagesDAO.prepareMessageByKey("profile.commissionprofilelist.errors.language1.required",MessagesDAO.getLabelByKey("profile.commissionprofilelistview.label.language1message"),CommProfile);
		Validator.messageCompare(actual, Message);
		
		Log.endTestCase(this.getClass().getName());
	}




    
    
    //@Test(dataProvider = "categoryData")
	public void y_commissionProfileStatusChangewithoutLanguage1Message(String domainName, String categoryName, String grade)
			throws InterruptedException {

		Log.startTestCase(this.getClass().getName());
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCOMMPROFILE26").getModuleCode();
		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE26").getExtentCase());
		currentNode.assignCategory(assignCategory);

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		
		
		
		
		String actual = commissionProfile.CommissionProfileDeactivatedNegative(domainName, categoryName, grade,CommProfile,"deactivating Profile","");

		
		//String Message = MessagesDAO.prepareMessageByKey("profile.commissionprofilelist.errors.language2.required",CommProfile);
		
		String Message = MessagesDAO.prepareMessageByKey("profile.commissionprofilelist.errors.language2.required",MessagesDAO.getLabelByKey("profile.commissionprofilelistview.label.language2message"),CommProfile);
		
		Validator.messageCompare(actual, Message);

		Log.endTestCase(this.getClass().getName());
	} 
	
	
	
	@Test(dataProvider = "Domain&CategoryProvider_validations")
	@TestManager(TestKey = "PRETUPS-1844") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void z_AssociateCommissionProfile(int RowNum, String Domain, String Parent,String Category, String geotype,String grade, HashMap<String, String> mapParam)
			throws InterruptedException {
		final String methodName = "z_changeCommissionProfile";
		Log.startTestCase(methodName);
	/*	moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCOMMPROFILE27").getModuleCode();
		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}*/

		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE27").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> channelresultMap;
	
       
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		ChannelUser channelUserLogic= new ChannelUser(driver);
		AssociateProfile associateProfile = new AssociateProfile(driver);
		
		try
		{
			channelresultMap=channelUserLogic.channelUserInitiate(1, Domain,Parent ,Category , geotype,mapParam);
		    channelresultMap.get("channelInitiateMsg");
		    String APPLEVEL = DBHandler.AccessHandler.getSystemPreference(UserAccess.userapplevelpreference());

			if(APPLEVEL.equals("2"))
			{
				channelUserLogic.approveLevel1_ChannelUser();
				channelUserLogic.approveLevel2_ChannelUser();
			}
			else if(APPLEVEL.equals("1")){
				channelUserLogic.approveLevel1_ChannelUser();
			}else{
				Log.info("Approval not required.");	
			}
				
			 String[] result = commissionProfile.addCommissionProfilewithoutAdditionalCommission(Domain, Category, grade);
     		   System.out.println(result);
     		  String CommProfile=result[1];
     		 channelresultMap.put("commProfile", CommProfile);
     		 channelresultMap.put("grade", grade);
     		 String actual = associateProfile.associateProfile(channelresultMap, "msisdn");
     		  String Message = MessagesDAO.prepareMessageByKey("user.addoperatoruser.updatesuccessmessage",channelresultMap.get("fName"));
     		 Assertion.assertEquals(actual, Message);
     		 // Validator.messageCompare(actual, Message);
		}
		catch(Exception e)
		{
			Log.writeStackTrace(e);
			
		}
		Log.endTestCase(methodName);
	} 
	
	
	@DataProvider(name = "Domain&CategoryProvider_validations")
	public Object[][] DomainCategoryProvider_validations() {
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
		
		ChannelUserMap chnlUserMap = new ChannelUserMap();
		RandomGeneration randGen = new RandomGeneration();
		int rowNum=1;
		String loginID=null;
		String mobileNumber=null;
		
		String[] userDetailsHL = new String[5];
		
		userDetailsHL[0] = ExcelUtility.getCellData(0, "DOMAIN_NAME", 1);
		userDetailsHL[1] = ExcelUtility.getCellData(0, "PARENT_CATEGORY_NAME", 1);
		userDetailsHL[2] = ExcelUtility.getCellData(0, "CATEGORY_NAME", 1);
		userDetailsHL[3] = ExcelUtility.getCellData(0, "GRPH_DOMAIN_TYPE", 1);	
		userDetailsHL[4] = ExcelUtility.getCellData(0, "GRADE", 1);	
		
		mobileNumber=DBHandler.AccessHandler.deletedMSISDN();
		 minPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_LOGIN_PWD_LENGTH"));
		 maxPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_LOGIN_PWD_LENGTH"));
		int minMSISDNLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_MSISDN_LENGTH"));
		int remainingMSISDN = minMSISDNLength-SystemPreferences.MSISDN_PREFIX_LENGTH-1;
		
		String minPaswd = CommonUtils.generatePassword(minPaswdLength-2)+"@";
		
		String prefix = _masterVO.getMasterValue("Prepaid MSISDN Prefix");
		String minMSISDN = prefix + randGen.randomNumberWithoutZero(remainingMSISDN);
		
		Object[][] categoryData;
		
				   categoryData = new Object[][]{{rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],userDetailsHL[4],chnlUserMap.getChannelUserMap("paymentType","ALL","documentType","PAN")}
												 };
		
		return categoryData;
	}
	
	

}
