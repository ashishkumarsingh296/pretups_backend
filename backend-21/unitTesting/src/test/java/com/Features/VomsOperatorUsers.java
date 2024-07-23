package com.Features;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;

public class VomsOperatorUsers extends BaseTest{

	HashMap<String, String> operatorMap;
	static boolean TestCaseCounter = false;
	static boolean TestCaseCounter1 = false;
	String assignCategory = "SIT";
	
	public void _01_fetchOperatorUsers_custom(String vouchertype) throws SQLException {
		BuilderLogic OperatorHierarchy = new BuilderLogic();
		OperatorHierarchy.WriteOperatorUserstoExcel(vouchertype);
	}
	
	public void _02_operatorUserCreation(int RowNum, String ParentUser, String LoginUser, String sheetTorefer, String vouchertype) throws InterruptedException {
		final String methodname = "operatorUserCreation";
		Log.startTestCase(methodname, RowNum, ParentUser, LoginUser);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOMSOPTUSRCREATION1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITVOMSOPTUSRCREATION2");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITVOMSOPTUSRCREATION3");
		
		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false && vouchertype.equalsIgnoreCase("Physical")) {
			test=extent.createTest("["+assignCategory+"]"+CaseMaster1.getModuleCode()+"_"+vouchertype);
			TestCaseCounter = true;
		}
		
		if (TestCaseCounter1 == false && vouchertype.equalsIgnoreCase("Electronic")) {
			test=extent.createTest("["+assignCategory+"]"+CaseMaster1.getModuleCode()+"_"+vouchertype);
			TestCaseCounter1 = true;
		}
		
		/*
		 * Test Case - To Create Operator Users as per the Operator Users Hierarchy Sheet
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),ParentUser,LoginUser,vouchertype));
		currentNode.assignCategory(assignCategory);

		OperatorUser OperatorUserLogic = new OperatorUser(driver);

		OperatorUserLogic.customoperatorUserInitiate(ParentUser, LoginUser,sheetTorefer);
		OperatorUserLogic.approveUser(ParentUser);
		OperatorUserLogic.writeOperatorUserDatacustom(RowNum, sheetTorefer);
		
		currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(),LoginUser,vouchertype));
		currentNode.assignCategory(assignCategory);
		OperatorUserLogic.changeUserFirstTimePassword();
		String actual;
		try{actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		if(actual==null){actual="";}
		}catch(Exception e){actual="No message found on screen";}
		String expected = MessagesDAO.getLabelByKey("login.changeCommonLoginPassword.updatesuccessmessage");

		if(actual.equals(expected)){
			OperatorUserLogic.writeOperatorUserDatacustom(RowNum, sheetTorefer);	
		}else{
			String loginid = ExtentI.fetchValuefromDataProviderSheet(sheetTorefer, ExcelI.LOGIN_ID, RowNum);
			if(DBHandler.AccessHandler.fetchUserPassword(loginid).equals(_masterVO.getProperty("NewPassword")))
			{OperatorUserLogic.writeOperatorUserDatacustom(RowNum, sheetTorefer);
			currentNode.log(Status.PASS,MarkupHelper.createLabel("Password get changed but no password change message appears on WEB screen.",ExtentColor.GREEN));}
//			currentNode.log(Status.FAIL,MarkupHelper.createLabel("Password get changed but no password change message appears on WEB screen.",ExtentColor.RED));}
			else
			{ExtentI.insertValueInDataProviderSheet(sheetTorefer, ExcelI.PASSWORD, RowNum, _masterVO.getProperty("Password"));
			currentNode.log(Status.FAIL, MarkupHelper.createLabel(actual,ExtentColor.RED));
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
		}
			
		if(OperatorUserLogic.pinChangeRequired.equals("true")) {
			currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(),LoginUser,vouchertype));
			currentNode.assignCategory(assignCategory);
			operatorMap=OperatorUserLogic.changeUserFirstTimePIN();
			String intChnlChangePINMsg = MessagesDAO.prepareMessageByKey("user.changepin.msg.updatesuccess");
			if(operatorMap.get("changePINMsg").equals(intChnlChangePINMsg)){
				OperatorUserLogic.writeOperatorUserDatacustom(RowNum,sheetTorefer);}
			else{
				currentNode.log(Status.FAIL, MarkupHelper.createLabel(operatorMap.get("changePINMsg"),ExtentColor.RED));
				ExtentI.insertValueInDataProviderSheet(sheetTorefer, ExcelI.PIN, RowNum, _masterVO.getProperty("PIN"));
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
		}
		else
			Log.info("Pin Change is not required.");

		
		
		Log.endTestCase(methodname);
	}

	public Object[][] DomainCategoryProvider(String sheetToRefer, String voucherType) throws IOException {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, sheetToRefer);
		int rowCount = ExcelUtility.getRowCount();
		int counter=rowCount-1;

		Object[][] categoryData = new Object[counter][5];
		int j=0;
		for (int i = 2; i <=rowCount; i++)
		{
				categoryData[j][0] = i;
				categoryData[j][1] = ExcelUtility.getCellData(0,ExcelI.PARENT_NAME, i);
				categoryData[j][2] = ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME, i);
				categoryData[j][3] = sheetToRefer;
				categoryData[j][4] = voucherType;
				j++;
		}
		return categoryData;
	}
	
}
