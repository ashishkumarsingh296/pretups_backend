package testcases;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_util_script.Launchdriver;

public class TC13_VOMS_Download {
	
	
	@Test
	public static void voucherdownload (String product) throws Exception{
		
				String viewbatchtype = "Non Downloaded Batches";
				
			 	System.out.println("Now clicking on the VOUCHER Download and then on VOMS VoucherDownload");
				//clicking on Voucher Profile
				common_features.VoucherDenomination_Options.clicklink("Voucher Download");
				
				//Selecting the sub option ----Add profile details
				common_features.VoucherDenomination_Options.clicklink("Voms Voucher Download");
				
				String baseWindowHdl = Launchdriver.driver.getWindowHandle();
				System.out.println("Current URL is " + Launchdriver.driver.getCurrentUrl());
		
				System.out.println("Now selecting " +viewbatchtype+ " from the VIEW BATCHES option");
				common_util_script.Selectfromdropdown.select("batchesType", viewbatchtype);
				
				System.out.println("Now entering the fromdate and todate");
				
				System.out.println("");
				Date date = new Date() ;
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy") ;
				String dt = dateFormat.format(date);
				System.out.println("Date is: " + dt);
				Calendar c = Calendar.getInstance();
				c.setTime(dateFormat.parse(dt));
				c.add(Calendar.DATE, 0);  // number of days to add
				dt = dateFormat.format(c.getTime());  // dt is now the new date
				System.out.println("Updated date is: " + dt);

				WebElement applicablef = Launchdriver.driver.findElement(By.name("fromDate"));
				Assert.assertTrue(applicablef.isDisplayed(), "Applicable from input parameter does not exists");
				applicablef.sendKeys(dt);	
				System.out.println ("		You have entered the From Date: "+ dt );
								
				WebElement applicablet = Launchdriver.driver.findElement(By.name("toDate"));
				Assert.assertTrue(applicablet.isDisplayed(), "Applicable from input parameter does not exists");
				applicablet.sendKeys(dt);	
				System.out.println ("		You have entered the To Date: "+ dt );
				
				System.out.println("Now clicking on submit button");
				common_util_script.ClickButton.click("submitVoucherDown");
				
				System.out.println("Now selecting the batch number corresponding to the product: " + product);
				String path1= "//*[contains(text(),'";
				String path2 ="')]/../descendant::*/input";
				Assert.assertTrue(Launchdriver.driver.findElement(By.xpath(path1+product+path2)).isDisplayed());
				Launchdriver.driver.findElement(By.xpath(path1+product+path2)).click();
				common_util_script.ClickButton.click("submitD");				
				
				Launchdriver.driver.findElement(By.linkText("Download Vouchers")).click();
				
				Set<String> windows = Launchdriver.driver.getWindowHandles();
				Iterator iterator =windows.iterator();
				String currentWindowID;
				
				while(iterator.hasNext()){
					currentWindowID = iterator.next().toString();
					
					if(!currentWindowID.equals(baseWindowHdl));
					Launchdriver.driver.switchTo().window(currentWindowID);
				}
				
				Thread.sleep(1000);
				Launchdriver.driver.close();
				Launchdriver.driver.switchTo().window(baseWindowHdl);
				Launchdriver.driver.switchTo().frame(0);
				
	}
	
}
