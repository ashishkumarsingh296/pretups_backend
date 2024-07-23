/**
 * 
 */
package com.Features;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.Login;
import com.commons.ExcelI;
import com.dbrepository.DBUtil;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.InitializeBrowser;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class SpringLinksAndPageCodes extends BaseTest{
	
	WebDriver driver;
	DBUtil database=new DBUtil();
	int counter=1;
	
	@BeforeClass
	public void setup() {
		driver = InitializeBrowser.Chrome();
	}
	
	
	
	@Test(dataProvider = "Domain&CategoryProvider",priority=1)
	public void links( String ParentUser, String LoginUser, String CategoryCode) throws IOException{
		String LinksSheetPath= _masterVO.getProperty("Links");		
		SelectNetworkPage networkPage = new SelectNetworkPage(driver);
		Login login = new Login();
		SuperAdminHomePage homePage = new SuperAdminHomePage(driver);
		
		login.UserLogin(driver, "Operator", ParentUser,LoginUser);
		networkPage.selectNetwork();
		
		
		String mLink;
		
		int k=0;
		List<WebElement> modules = driver.findElements(By.xpath("//a[@href [contains(.,'moduleCode')and not(contains(.,'pageCode'))]]"));
		int totalModulesOnPage=modules.size();
		for(int i=0;i<totalModulesOnPage;i++){

			List<WebElement> b51 = driver.findElements(By.xpath("//a[@href [contains(.,'moduleCode')and not(contains(.,'pageCode'))]]"));
			b51.get(i).click();
			
			boolean d=driver.findElements(By.xpath("//*[@id='dropdownMenu1']/span[2]")).size()!=0;
			
			if(d==true){
				Log.info("Spring found and now Breaking the loop");
				k=0;
				break;
			}
			else{
				Log.info("Spring not found");
			}
			k++;
		}
		if(k==0){
			Log.info("<-------------Fetching from spring Window------------>");
		for(int i=0;i<modules.size();i++)
		{ 
		List<WebElement> b6=driver.findElements(By.xpath("//a[@href [contains(.,'moduleCode')and not(contains(.,'pageCode'))]]"));

		Log.info("mainLink :" +b6.get(i).getText()+":");
		mLink=b6.get(i).getAttribute("innerHTML").replaceAll("<span></span>","");
		System.out.println("Main Link :: "+mLink);
		
				
				List<WebElement> pageCodesLink=driver.findElements(By.xpath("//a[@href [contains(.,'moduleCode')and not(contains(.,'pageCode'))]][text()='"+mLink+"']/..//a[@href [contains(.,'pageCode')]]"));
				List<WebElement> subModules=driver.findElements(By.xpath("//a[@href [contains(.,'moduleCode')and not(contains(.,'pageCode'))]][text()='"+mLink+"']/..//a[@href [contains(.,'pageCode')]]/span"));
				
				for(int j=0;j<subModules.size();j++)
				{
				WebElement ele =pageCodesLink.get(j);
		        String s=ele.getAttribute("href").replaceAll("\\^*.*\\=","");     // for getting id of each element
		        
		        System.out.println("PageCodes found ::"+s);
		        System.out.println("fetching text for page code.........!!!");
		     
				Log.info("subLink:: " +subModules.get(j).getAttribute("innerHTML"));
				String subLink=subModules.get(j).getAttribute("innerHTML");
				
				Log.info("Fetching links and sublinks.");
				
				ExcelUtility.setExcelFile(LinksSheetPath, "Links and Sublinks");		
				
				ExcelUtility.setCellData(0,"MainLinks", counter, mLink);
				ExcelUtility.setCellData(0,"SubLinks", counter, subLink);
				ExcelUtility.setCellData(0,"PageCodes", counter, s);
				ExcelUtility.setCellData(0,"CategoryCodes", counter, CategoryCode);
				counter++;
				}
				
				}
		try{
		driver.findElement(By.xpath("//a[@href='/pretups/logout.do?method=logout']")).click();}
		catch(Exception e){
			Log.info("Logout not found.");
		}
	}
		else if(k>0){
			Log.info("<-------------Fetching from non-spring Window------------>");
			int i;
			for( i=0;i<modules.size();i++)
			{ 
			List<WebElement> b6=driver.findElements(By.xpath("//a[@href [contains(.,'moduleCode')]]"));

			Log.info("mainLink :" +b6.get(i).getText());
			String mLink1=b6.get(i).getText();
			b6.get(i).click();
			
					List<WebElement> subModules=driver.findElements(By.xpath("//a[@href [contains(.,'pageCode')]]"));			
					for(WebElement ele:subModules)
					{
			
			        String PageCodes =ele.getAttribute("href").replaceAll("\\^*.*\\=","");     // for getting id of each element
			        
			        Log.info("PageCodes found :: "+PageCodes);
			
					Log.info("subLink:: " +ele.getText());
					String subLink=ele.getText();
					
					Log.info("Fetching links and sublinks.");
					
					ExcelUtility.setExcelFile(LinksSheetPath, "Links and Sublinks");		
					
					ExcelUtility.setCellData(0,"MainLinks", counter, mLink1);
					ExcelUtility.setCellData(0,"SubLinks", counter, subLink);
					ExcelUtility.setCellData(0,"PageCodes", counter, PageCodes);
					ExcelUtility.setCellData(0,"CategoryCodes", counter, "MONTR"/*CategoryCode*/);
					counter++;
					}
							
					}
			if(i==totalModulesOnPage){
				homePage.clickLogout();
			}
		}
		
	}	
		
		
	
	@Test(priority=2)
		public void writeRoleCode() throws SQLException{
		String LinksSheetPath= _masterVO.getProperty("Links");
		ExcelUtility.setExcelFile(LinksSheetPath, "Links and Sublinks");
		int rowNUM=ExcelUtility.getRowCount();
		
		database.OpenDBConnection();
		for(int i=1; i<=rowNUM; i++){
			ExcelUtility.setExcelFile(LinksSheetPath, "Links and Sublinks");
			String pageCode=ExcelUtility.getCellData(0, "PageCodes", i);
			String categoryCode=ExcelUtility.getCellData(0, "CategoryCodes", i);
			ResultSet roleCode=database.executeQuery("SELECT role_code  FROM category_roles WHERE role_code IN (SELECT role_code FROM page_roles WHERE page_code = '"+pageCode+"') AND category_code = '"+categoryCode+"'");
			while(roleCode.next()){
				try{
					ExcelUtility.setCellData(0,"RoleCodes", i,roleCode.getString(1).toString());
				}catch(Exception e){
					Log.info("Error while writing RoleCodes in Sheet");
					Log.writeStackTrace(e);
				}
			}
		}database.CloseConnection();
	}

	@DataProvider(name = "Domain&CategoryProvider")
	public Object[][] DomainCategoryProvider() throws IOException {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		
		HashMap<String, ArrayList<String>> catName= new HashMap<String, ArrayList<String>>();
		
		int j=0;
		String keyName = null;
		for (int i = 1; i <=rowCount; i++)
		{		
				ArrayList<String> categoryName=new ArrayList<String>();
				categoryName.add(ExcelUtility.getCellData(0,"PARENT_NAME", i));
				keyName=ExcelUtility.getCellData(0,"CATEGORY_NAME", i);
				categoryName.add(ExcelUtility.getCellData(0,"CATEGORY_CODE", i));		
				catName.put(keyName, categoryName);
				System.out.println("Key = "+catName+" value1= "+catName.get(keyName).get(0)+" value2= "+catName.get(keyName).get(1));		
		}
		
		int listSize=catName.keySet().size();
		Log.info("List Size:: "+listSize);
		Object[][] categoryData = new Object[listSize][3];
		
		for (String name: catName.keySet()){
			
			categoryData[j][0]=catName.get(name).get(0);
            categoryData[j][1]=name.toString();
            categoryData[j][2]=catName.get(name).get(1);
            	
           Log.info("Key = "+categoryData[j][0]+" value1= "+categoryData[j][1]+" value2= "+categoryData[j][2]);	
           j++;
		}
		String LinksSheetPath= _masterVO.getProperty("Links");
		ExcelUtility.setExcelFile(LinksSheetPath, "Links and Sublinks");
		ExcelUtility.createHeader("MainLinks", 0);
		ExcelUtility.createHeader("SubLinks", 1);
		ExcelUtility.createHeader("PageCodes", 2);
		ExcelUtility.createHeader("RoleCodes", 3);
		ExcelUtility.createHeader("CategoryCodes", 4);
		return categoryData;
	}
	
}
