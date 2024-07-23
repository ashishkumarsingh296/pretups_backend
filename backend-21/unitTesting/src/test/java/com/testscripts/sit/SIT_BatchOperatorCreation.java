package com.testscripts.sit;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.BatchOperatorUserInitiate;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.entity.OperatorUserVO;
import com.pageobjects.loginpages.ChangePasswordForNewUser;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.NewExcelUtility;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_BatchOperatorCreation extends BaseTest{

	public static boolean TestCaseCounter = false;
	String categorycode = "NWADM";
	static String moduleCode = "Batch Operator User Initiate";
	String assignCategory  = "SIT";
	
	/** Chrome Settings Initializer **/
	public SIT_BatchOperatorCreation() {
		CHROME_OPTIONS = CONSTANT.CHROME_OPTION_BATCH;
	}
	
	@DataProvider(name="exceldata")
	public Object[] initiateOperatorData() throws IOException{

		Object[] dataObjects = null;
		
		String[] optCatcodes = _masterVO.getProperty("OptCategoryCodes").split(",");
		int rowCount = NewExcelUtility.getRowCount(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int sizeOfOperatorUserInitiate = Integer.parseInt(_masterVO.getProperty("NumberOfUsersForEachOptCategory"));
		int existAt = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, "MONTR");
		
		if (existAt==0){
			dataObjects = new Object[(rowCount-1)];
		} else if (existAt>0){ 
			dataObjects = new Object[(rowCount-2)];
		}
		
		if (!optCatcodes[0].equalsIgnoreCase("ALL") && _masterVO.getProperty("allParents").equalsIgnoreCase("true")) {
			int countCodesInSheet = 0;
			for (String categoryCode : optCatcodes) {
				countCodesInSheet = countCodesInSheet
						+ ExtentI.columnbasedfilter(_masterVO.getProperty("DataProvider"),
								ExcelI.OPERATOR_USERS_HIERARCHY_SHEET,
								ExcelI.CATEGORY_CODE, categoryCode,
								ExcelI.CATEGORY_CODE).size();
			}
			dataObjects = new Object[countCodesInSheet];
		}
		else if(!optCatcodes[0].equalsIgnoreCase("ALL") && !_masterVO.getProperty("allParents").equalsIgnoreCase("true")){
			dataObjects = new Object[optCatcodes.length];
		}
		
		ArrayList<String> tracedCategories = new ArrayList<String>();
		int datacounter = 0;
		for(int counter=2;counter<=rowCount;counter++) {
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			String catCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, counter);
			String parentName = ExcelUtility.getCellData(0,ExcelI.PARENT_NAME, counter);
			
			if (catCode.equals("MONTR"))
				continue;
			if (!_masterVO.getProperty("allParents").equalsIgnoreCase("true")
					&& tracedCategories.contains(catCode)) {
				continue;
			}
			
			boolean contains = Arrays.stream(optCatcodes).anyMatch(catCode::equals);	
			if(contains || optCatcodes[0].equalsIgnoreCase("ALL")){
				ArrayList<OperatorUserVO> OperatorUserList = new ArrayList<OperatorUserVO>();
				for (int sizeCounter = 1; sizeCounter <= sizeOfOperatorUserInitiate; sizeCounter++) {
					OperatorUserList.add(new OperatorUserVO(catCode,parentName));
				}
				
				dataObjects[datacounter] = OperatorUserList;
				datacounter++;
			}	
			tracedCategories.add(catCode);
		}
		return dataObjects;

	}
	
	
	
	@Test(dataProvider="exceldata",priority = 1)
	public void Excelfile(ArrayList<OperatorUserVO> OperatorVO) throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION1").getModuleCode();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		OperatorUserVO optData = OperatorVO.get(0);
		
		int row = 0;
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION2").getExtentCase(),optData.getCategoryCode(),optData.getParentName()));
		currentNode.assignCategory(assignCategory);
		
		//btch.loginwithoptusr();
		new Login().UserLogin(driver, "Operator", optData.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optData.getCategoryCode());
		btch.filldetailsinfile(path, optData.getNumberOfOperatorUsers(), optData.getBatchRow(), OperatorVO);
		Object[] udata=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		boolean errors = (boolean) udata[0];
		if(errors){
			currentNode.log(Status.FAIL,"Failures occurred");
			ExtentI.attachCatalinaLogs();

			Log.info("Below are the RoleCodes not present in batch Master Sheet["+path+"]: ");
			String [] roles  = optData.getRoleCode().split(",");
			for(String x:roles){
				int existAt = NewExcelUtility.searchStringRowNum(path, "Master Sheet", x);
				if(existAt==0){
					ExtentI.Markup(ExtentColor.YELLOW, x);
				}
			}
		} else {
			row = ExtentI.combinationExistAtRow(new String[] {ExcelI.CATEGORY_CODE, ExcelI.PARENT_NAME },
					new String[] {optData.getCategoryCode(), optData.getParentName() },
					ExcelI.BATCH_OPERATOR_USERS_HIERARCHY_SHEET);
			btch.writeOperatorUserData(row,	ExcelI.BATCH_OPERATOR_USERS_HIERARCHY_SHEET, optData);
		}
		ExtentI.attachScreenShot();
		
		if(row==0) return;
		
		currentNode = test.createNode("To verify that Web login password can be changed for the batch initiated operator user"
				+ " with category "+optData.getCategoryCode()+" and parent "+optData.getParentName());
		currentNode.assignCategory(assignCategory);
		
		String newPassword = _masterVO.getProperty("NewPassword");
		new Login().LoginAsUser(driver, optData.getLOGINID(), optData.getPASSWORD());
		new ChangePasswordForNewUser(driver).changePassword(optData.getPASSWORD(), newPassword, newPassword);
		if (DBHandler.AccessHandler.fetchUserPassword(optData.getLOGINID()).equals(newPassword)) {
			currentNode.log(Status.PASS, "Password changed successfully");
			ExtentI.insertValueInDataProviderSheet(ExcelI.BATCH_OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PASSWORD, row, newPassword);
		}
		else{
			currentNode.log(Status.FAIL, "Error occurs while changing password");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();
		}
		
	}
	
	
	@Test(priority=2)
	public void blankFirstName() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION1").getModuleCode();
		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setFirstName("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.fnamemissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=3)
	public void blankWebLoginID() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setLOGINID("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.loginidreqforweb");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=4)
	public void blankMobileNumber() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setMSISDN("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("routing.mnp.upload.msisdnrequired");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=5)
	public void blankSubscriberCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setSubscriberCode("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.scodemissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=6)
	public void blankStatusCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setStatusCode("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unamestatusmissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=7)
	public void blankDivisionCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setDivisionCode("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unamedivisionmissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=8)
	public void blankDepartmentCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setDeptCode("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unamedepartmentmissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	
	@Test(priority=9)
	public void blankGeographicalDomainCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setGeodomaincode("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unamegeographymissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=10)
	public void blankProductCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = "BCU";
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setProductType("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unameproductmissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}

	@Test(priority=11)
	public void blankRoleType() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setRoleType("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unamerolestypemissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=12)
	public void blankRoleCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setRoleCode("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION12").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unamerolesmissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=13)
	public void blankDomainCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = "BCU";
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setDomainCode("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.fnamemissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=14)
	public void blankExternalCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setEXTCODE("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.prepareMessageByKey("user.batchoptuser.processuploadedfile.msg.success","");
		Validator.partialmessageCompare(actualMsg, expectedMsg.replaceAll("[.]",""));
	}
	
	@Test(priority=15)
	public void blankEmailID() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setEmail("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.prepareMessageByKey("user.batchoptuser.processuploadedfile.msg.success","");
		Validator.partialmessageCompare(actualMsg, expectedMsg.replaceAll("[.]", ""));
	}
	
	public OperatorUserVO blankWebPassword() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setPASSWORD("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION16").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.prepareMessageByKey("user.batchoptuser.processuploadedfile.msg.success","");
		Validator.partialmessageCompare(actualMsg, expectedMsg.replaceAll("[.]",""));
		return optdata;
	}
	
	@Test(priority=16)
	public void defaultPasswordcheck() throws IOException{
		
		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		OperatorUserVO dataopt = blankWebPassword();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION17").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String actual = DBHandler.AccessHandler.fetchUserPassword(dataopt.getLOGINID());
		String expected = DBHandler.AccessHandler.getSystemPreference(CONSTANT.C2S_DEFAULT_PASSWORD);
		Validator.messageCompare(actual, expected);
	}

	@Test(priority=17)
	public void statusCodeSuspended() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setStatusCode("S");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION18").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.prepareMessageByKey("user.batchoptuser.processuploadedfile.msg.success","");
		Validator.partialmessageCompare(actualMsg, expectedMsg.replaceAll("[.]",""));
		
		String[] actual = DBHandler.AccessHandler.getdetailsfromUsersTable(optdata.getLOGINID(), "STATUS");
		String expected = "S";
		Validator.messageCompare(actual[0], expected);
	}
	
	@Test(priority=18)
	public void mobilenumberiszero() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setMSISDN("0");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION19").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("bulkuser.processuploadedfile.error.msisdnisinvalid");
		Validator.messageCompare(actualMsg, expectedMsg);
		
	}

	@Test(priority=19)
	public void loginidnotunique() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		String login  = optdata.getLOGINID();
		
		OperatorUserVO optdata1 = new OperatorUserVO(catCode,prtName);
		optdata1.setLOGINID(login);
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		OptUserList.add(optdata1);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION20").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.prepareMessageByKey("user.batchoptuser.processuploadedfile.msg.error.loginiduniqueerr",login);
		Validator.messageCompare(actualMsg, expectedMsg);
		
	}
	
	@Test(priority=20)
	public void invalidPassword() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setPASSWORD("1234");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION21").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("operatorutil.validatepassword.error.passwordconsecutive");
		Validator.messageCompare(actualMsg, expectedMsg);
		
	}
	
	@Test(priority=21)
	public void invalidRoleCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setRoleCode("XYZ");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION22").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unamerolesinvalid");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=22)
	public void blankNetworkCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = "SUNADM";
		String prtName = null;
		try{prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		}catch(Exception e){currentNode.log(Status.SKIP,catCode + " not exist in the setup.");return;}
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setNetworkcode("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION23").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unamenetworkmissing");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=23)
	public void invalidNetworkCode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = "SUNADM";
		String prtName = null;
		try{prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		}catch(Exception e){currentNode.log(Status.SKIP,catCode + " not exist in the setup.");return;}
		
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setNetworkcode(UniqueChecker.UC_NetworkCode());
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION24").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.getLabelByKey("user.batchoptuser.processuploadedfile.error.unamenetworkinvalid");
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=24)
	public void lessthanminpasswordlength() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		int minPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_LOGIN_PWD_LENGTH"));
		int maxPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_LOGIN_PWD_LENGTH"));
		String password = CommonUtils.generatePassword(minPaswdLength-2)+"@";
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setPASSWORD(password);
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION25").getExtentCase(),minPaswdLength));
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.prepareMessageByKey("operatorutil.validatepassword.error.passwordlenerr",String.valueOf(minPaswdLength),String.valueOf(maxPaswdLength));
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=25)
	public void invalidsubscribercode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setSubscriberCode("213 432");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION26").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "Subscriber code is not valid.";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=26)
	public void specialcharsinsubscribercode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setSubscriberCode("21@3&4%3#2");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION27").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "Subscriber code is not valid.";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=27)
	public void existingsubscribercode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		String loginID = null, subsCode = null;
		while(true){
		loginID = DBHandler.AccessHandler.existingLoginID();
		subsCode = DBHandler.AccessHandler.getEmpCode(loginID);
		if(subsCode==null||subsCode.equals("")) continue;
		else break;}
		
		optdata.setSubscriberCode(subsCode);
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION28").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = MessagesDAO.prepareMessageByKey("user.batchoptuser.processuploadedfile.msg.success","");
		Validator.partialmessageCompare(actualMsg, expectedMsg.replaceAll("[.]",""));
	}
	
	@Test(priority=28)
	public void invalidstatuscode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setStatusCode("M");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION29").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=29)
	public void specialcharsstatuscode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setStatusCode("&%");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION30").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=30)
	public void invaliddomaincode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setDomainCode("DOMAIN");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION31").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=31)
	public void specialcharsdomaincode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setDomainCode("DO$M&A#IN");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION32").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=32)
	public void invalidgeographicaldomaincode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setGeodomaincode("PQRSTU");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION33").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=33)
	public void specialcharsgeographicaldomaincode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setGeodomaincode("A#R&");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION34").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=34)
	public void invalidproductcode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setProductType("OPUP");;
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION35").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=35)
	public void specialcharsproductcode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setProductType("ETOP#UP&");;
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION36").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=36)
	public void invalidroletype() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setRoleType("R");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION37").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=37)
	public void specialcharsroletype() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		
		optdata.setRoleType("&#$%");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION38").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=38)
	public void existingexternalcode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setEXTCODE(DBHandler.AccessHandler.existingEXTCODE());
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION39").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=39)
	public void invalidexternalcode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setEXTCODE("34556 234 23");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION40").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=40)
	public void specialcharsexternalcode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setEXTCODE("34556$&#");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION41").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=41)
	public void invalidemailid() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setEmail("invalidmailid");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION42").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=42)
	public void passwordwithspace() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setPASSWORD("Com@ 1357");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION43").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=43)
	public void invalidmsisdnprefix() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		String msisdn;
		
		String prefix = new UniqueChecker().UC_PrefixData();
		if( prefix != null ){
		msisdn = prefix + new RandomGeneration().randomNumeric(new GenerateMSISDN().generateMSISDN());}
		else {return;}
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setMSISDN(msisdn);
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION44").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=44)
	public void greaterthanmsisdnlength() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + new RandomGeneration().randomNumeric(new GenerateMSISDN().generateMSISDN()+1);
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setMSISDN(msisdn);
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION45").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=45)
	public void existingloginid() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setLOGINID(DBHandler.AccessHandler.existingLoginID());
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION46").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=46)
	public void invaliddepartmentcode() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setDeptCode("anythingulike");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION47").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	
	@Test(priority=47)
	public void blankusernameprefix() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setUserNamePrefix("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION48").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=48)
	public void invalidusernameprefix() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setUserNamePrefix("MNPR");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION49").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=49)
	public void blankaddress1() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setAddress1("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION50").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=50)
	public void blankaddress2() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setAddress2("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION51").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=51)
	public void blankcity() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setCity("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION52").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=52)
	public void blankstate() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setState("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION53").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=53)
	public void blankcountry() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setCountry("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION54").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
	
	@Test(priority=54)
	public void blankwholeaddress() throws IOException{
		BatchOperatorUserInitiate btch=new BatchOperatorUserInitiate(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		String catCode = categorycode;
		String prtName = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PARENT_NAME, new String[]{ExcelI.CATEGORY_CODE}, new String[]{catCode});
		
		OperatorUserVO optdata = new OperatorUserVO(catCode,prtName);
		optdata.setAddress1(""); optdata.setAddress2(""); optdata.setCity(""); optdata.setState("");
		optdata.setCountry("");
		
		ArrayList<OperatorUserVO> OptUserList = new ArrayList<OperatorUserVO>();
		OptUserList.add(optdata);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBATCHOPTUSERCREATION55").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		new Login().UserLogin(driver, "Operator", optdata.getParentName());
		new SelectNetworkPage(driver).selectNetwork();
		String path=btch.downloadBatchfile(optdata.getCategoryCode());
		btch.filldetailsinfile(path, optdata.getNumberOfOperatorUsers(), optdata.getBatchRow(), OptUserList);
		Object[] data=btch.uploadfile(path, "batchfile"+new SimpleDateFormat("ddMMyyHHmmss").format(new Date()));
		//boolean errors = (boolean) data[0];
		String actualMsg = data[1].toString();
		String expectedMsg = "";
		Validator.messageCompare(actualMsg, expectedMsg);
	}
}
