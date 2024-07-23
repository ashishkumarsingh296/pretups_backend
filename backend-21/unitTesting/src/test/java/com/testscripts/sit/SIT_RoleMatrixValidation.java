/**
 * 
 */
package com.testscripts.sit;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class SIT_RoleMatrixValidation extends BaseTest{
	
	int counter=1;
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider = "Domain&CategoryProvider", priority=1)
	public void links(String usertype, String LoginUser, String CategoryCode) throws InterruptedException{	
		
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]"+_masterVO.getCaseMasterByID("SITROLEMATRIXVALIDATION1").getModuleCode());
			TestCaseCounter = true;
		}
		ArrayList<Object> s = new ArrayList<Object>();
		ArrayList<Object> num = new ArrayList<Object>();
		SelectNetworkPage networkPage = new SelectNetworkPage(driver);
		Login login = new Login();
		SuperAdminHomePage homePage = new SuperAdminHomePage(driver);
		
		login.UserLogin(driver, usertype, LoginUser);
		networkPage.selectNetwork();
		
		List<WebElement> b5 = driver.findElements(By.xpath("//a[@href [contains(.,'moduleCode')]]"));
		
		for(int i=0;i<b5.size();i++)
		{ 
		List<WebElement> b6=driver.findElements(By.xpath("//a[@href [contains(.,'moduleCode')]]"));

		Log.info("mainLink :" +b6.get(i).getText());
		b6.get(i).click();
		
				List<WebElement> b7=driver.findElements(By.xpath("//a[@href [contains(.,'pageCode')]]"));			
				num.add(b7.size());
				for(int j=0;j<b7.size();j++)
				{
				List<WebElement> b8=driver.findElements(By.xpath("//a[@href [contains(.,'pageCode')]]"));			
				WebElement ele=b8.get(j);
		        s.add(ele.getAttribute("href").replaceAll("\\^*.*\\=",""));
				}
				}
	
	
	ArrayList<String> filtValues=ExtentI.columnbasedfilter(_masterVO.getProperty("RolesSheet"),ExcelI.LINK_SHEET1, ExcelI.CATEGORY_CODES, CategoryCode, ExcelI.ROLE_CODES);
	Log.info("Values after linksheet on filter: "+filtValues);
	ArrayList<String> filtValues1=ExtentI.columnbasedfilter(_masterVO.getProperty("RolesSheet"),ExcelI.LINK_SHEET1, ExcelI.CATEGORY_CODES, CategoryCode, ExcelI.PAGE_CODES);
	
	ArrayList<String> matchedRoleCodes=new ArrayList<String>();
	ArrayList<String> unmatchedRoleCode=new ArrayList<String>();
	ArrayList<String> unmatchedPageCode=new ArrayList<String>();
	String [] roleCodeName;
	for(int j=0;j<s.size();j++){
		roleCodeName = DBHandler.AccessHandler.fetchRoleName(s.get(j).toString());
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITROLEMATRIXVALIDATION1").getExtentCase(), roleCodeName[0],roleCodeName[1],LoginUser));
		currentNode.assignCategory("SIT");

		if(!filtValues.contains(roleCodeName[0])){
			currentNode.log(Status.FAIL,"<pre>Extra link available on WEB application for category ["+LoginUser+"] having roleCode: ["+roleCodeName[0]+"] | Link Name:["+roleCodeName[1]+"]</pre>");
		}
		else{currentNode.log(Status.PASS,"<pre>As per Role Matrix, link is available having roleCode: ["+roleCodeName[0]+"] | Link Name:["+roleCodeName[1]+"]</pre>");
		matchedRoleCodes.add(roleCodeName[0]);}
	}

			for(String k:matchedRoleCodes){
				if(!filtValues.contains(k))
				{unmatchedRoleCode.add(k);
				int m=filtValues.indexOf(k);
				unmatchedPageCode.add(filtValues1.get(m));}
			}
			
	int countList=0;
	for(String value1:unmatchedRoleCode){
		String[] value2 = DBHandler.AccessHandler.fetchRoleName(unmatchedPageCode.get(countList));
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITROLEMATRIXVALIDATION1").getExtentCase(), value1,value2[1],LoginUser));
		currentNode.assignCategory("SIT");
		currentNode.log(Status.FAIL,"<pre>As per Role matrix, WEB not contains roleCode: ["+value1+"] | Link Name: ["+value2[1]+"]</pre>");
		countList++;
	}

		homePage.clickLogout();
	}


	@DataProvider(name = "Domain&CategoryProvider")
	public Object[][] DomainCategoryProvider() throws IOException {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		int rowCount = ExcelUtility.getRowCount(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCount1 = ExcelUtility.getRowCount(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		Object[][] categoryData = new Object[rowCount+rowCount1][3];
		int j=0;
		
		ArrayList<String> catcodelist=new ArrayList<String>();
		ArrayList<String> catnamelist=new ArrayList<String>();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		for (int i = 1; i <=rowCount; i++)
		{		
			if(!catcodelist.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i))){
				categoryData[j][0] = "Operator";
				catnamelist.add(ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME, i));
				categoryData[j][1] = ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME, i);
				catcodelist.add(ExcelUtility.getCellData(0,ExcelI.CATEGORY_CODE, i));
				categoryData[j][2] = ExcelUtility.getCellData(0,ExcelI.CATEGORY_CODE, i);
				System.out.println(" "+categoryData[j][0]+" "+categoryData[j][1]+ " "+ categoryData[j][2]);
				j++;}
		}
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		for (int i = 1; i <=rowCount1; i++)
		{if(!catcodelist.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i))){
				categoryData[j][0] = "ChannelUser";
				catnamelist.add(ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME, i));
				categoryData[j][1] = ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME, i);
				catcodelist.add(ExcelUtility.getCellData(0,ExcelI.CATEGORY_CODE, i));
				categoryData[j][2] = ExcelUtility.getCellData(0,ExcelI.CATEGORY_CODE, i);
				System.out.println(" "+categoryData[j][0]+" "+categoryData[j][1]+ " "+ categoryData[j][2]);
				j++;
		}}
		
		int count=0;
		for(int i=0;i<categoryData.length;i++){
		if((categoryData[i][1])!=null)
			{
			count++;
			}}
		Object[][] data=new Object[count][3];
		for(int itr=0;itr<count;itr++){
			data[itr][0]=categoryData[itr][0];
			data[itr][1]=categoryData[itr][1];
			data[itr][2]=categoryData[itr][2];
		}
		
		
		Log.info(data);
		
		return data;
	}
	
}
