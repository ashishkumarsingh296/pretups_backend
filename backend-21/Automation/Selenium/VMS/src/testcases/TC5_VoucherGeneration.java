package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;
import common_util_script.Read_Properties_File;

public class TC5_VoucherGeneration {

	@Test
	public static void generate_physical_voucher (String filename, String sheetname, String totalvouchers, String vouchertype) throws Exception{
	
		System.out.println("");
    	System.out.println("Test Case: Voucher Generation");
    	
    	Map<String, String> cacheMap = Read_Properties_File.getCachemap();
    	String path = cacheMap.get("inputfilepath");
    			    	//login to the  GUI using the valid credentials
		   		
    			//String path = "C:\\Users\\atul.bassi\\LMS_Jordan\\pretupsVDS\\InputCSVFile\\";
				String file  = filename;
				System.out.println("Your excel/csv path is: " + path+file );
				 				
				System.out.println("Now clicking on the Voucher Generation");
				//clicking on Voucher denomination
				common_features.VoucherDenomination_Options.clicklink("Voucher Generation");
				
				System.out.println("Now clicking on Voms order initiate");
				//clicking on Voucher Profile
				common_features.VoucherDenomination_Options.clicklink("Voms order initiate");
				
				System.out.println("Now selecting the voucher type");
				Assert.assertTrue(common_features.Add_VD_VoucherTypeSelection.promotypeselection(vouchertype),"Invalid Voucher type");
				
				System.out.println("Voucher type is selected. Now clicking on submit button to view the denominations");
				common_util_script.ClickButton.click("voucherTypeSubmit");
				
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
					 	
					 	if(type.equals(vouchertype)){					 	
						DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
					    String denom = formatter.formatCellValue(mrp); //Returns the formatted value of a cell as a String regardless of the cell type
					    String denom1 = denom + ".0";
					    System.out.println("Denomination " + i + " = " + denom1 );
					    String denompath = "slabsListIndexed["+j+"].denomination";
					    
					    common_util_script.Selectfromdropdown.select(denompath, denom1);
					    
					    WebElement quanpath = Launchdriver.driver.findElement(By.name("slabsListIndexed["+j+"].quantity"));
					    quanpath.sendKeys(totalvouchers);
					    j++;
					 	} else{
					 		System.out.println("This is not a physical voucher");
					 	}
					    
				}
				 
				System.out.println("Now clicking on submit and then confirm button");
				common_util_script.ClickButton.click("submitOrderInit");
				
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
				

				System.out.println("Profile name is selected. Now clicking on confirm button");
				common_util_script.ClickButton.click("submitOrderInit");
				
	}
				
}