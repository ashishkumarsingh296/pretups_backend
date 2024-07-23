package common_features;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class Voucher_Download {

	public static boolean commoninputvalues (String vouchertype, String path, String filename, String sheetname) throws Exception {
		
		try {
			String file  = filename;
			System.out.println("Your excel/csv path is: " + path+file );
			 
			// Specify the file path which you want to create or write
			File excel = new File(path+file);
			  
			// Load the file i.e 
			FileInputStream fis = new FileInputStream(excel);
			// Load the workbook
			XSSFWorkbook wb = new XSSFWorkbook(fis);			
			//get the sheet which you want to modify or create
			XSSFSheet ws = wb.getSheet(sheetname);
			
			int rowNum = ws.getLastRowNum();
			System.out.println("Last row number is: " + rowNum);
			
			int j=0;
			
			for (int i = 1; i <= rowNum; i++) {
				 	
				 	Cell mrp = ws.getRow(i).getCell(6);
				 	String type = ws.getRow(i).getCell(1).getStringCellValue();
				 	System.out.println("Value is: " + type);
				 	DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
				    
				 	if(type.equals(vouchertype)){	
				 	String denom = formatter.formatCellValue(mrp); //Returns the formatted value of a cell as a String regardless of the cell type
				    String denom1 = denom + ".0";
				    System.out.println("Denomination " + i + " = " + denom1 );
				    String denompath = "slabsListIndexed["+j+"].denomination";
				    common_util_script.Selectfromdropdown.select(denompath, denom1);
				    WebElement quanpath = Launchdriver.driver.findElement(By.name("slabsListIndexed["+j+"].quantity"));
				    quanpath.sendKeys("10");
				    
				    j++;
				 	} else{
				 		System.out.println("This is not a physical voucher");
				 	}
				 	
				 }
				 
			System.out.println("Now clicking on submit and then confirm button");
			common_util_script.ClickButton.click("submitOrderInit1");
			
			Assert.assertTrue(Launchdriver.driver.findElement(By.name("back")).isDisplayed(), "Amount or quantity is not mentioned");
			
			System.out.println("Now selecting the profilename corresponding to the denomination name");
			int k=0;
			for (int i = 1; i <= rowNum; i++) {
			 	
			 	Cell prof = ws.getRow(i).getCell(9);
				DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
			    String profname = formatter.formatCellValue(prof); //Returns the formatted value of a cell as a String regardless of the cell type
			    
			    System.out.println("Profilename " + i + " = " + profname );
			    String profpath = "slabsListIndexed["+k+"].productid";			    		
			    common_util_script.Selectfromdropdown.select(profpath, profname);
			    
			    k++;
		   }
	
			System.out.println("Profile name is selected. Now clicking on submit button again");
			common_util_script.ClickButton.click("submitOrderInit");

			
			Assert.assertTrue(Launchdriver.driver.findElement(By.
					
					name("back")).isDisplayed(), "Amount or quantity is not mentioned");
			
			
			
			System.out.println("Now clicking on confirm button");
			common_util_script.ClickButton.click("submitOrderInit");
			
			
			
		}
		catch(AssertionError ae) {
			common_util_script.Switchwindow.windowhandleclose();		
			System.out.println("Assertion: No such element exists");
			return false;	
		}catch(Exception e) {
			System.out.println("Exception: No such element exists");
			return false;
		}
			return true;
		
		}
		
}
