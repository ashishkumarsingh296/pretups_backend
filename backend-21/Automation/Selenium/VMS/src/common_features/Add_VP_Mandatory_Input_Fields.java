package common_features;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import common_util_script.Launchdriver;

public class Add_VP_Mandatory_Input_Fields {
	
	public static boolean mandatoryfields (String vouchertype, String servicetype, String subservicetype, String mrp, String profilename, String shortname, String Minq, String Perfq, String tt, String validity, String expiryp, String description ) throws Exception{

	try{
	
	// Create FileInputStream Object  to read Webelement values
	FileInputStream fileInput1 = new FileInputStream(new File("locator.properties"));  
	// Create Properties object    to read Webelement values
	Properties prop1 = new Properties();  
	//load properties file    to read Webelement values
	prop1.load(fileInput1);
	
	//Selecting the Voucher Type.,
	System.out.println("Selecting the voucher type");
	Assert.assertTrue(common_features.Add_VD_VoucherTypeSelection.promotypeselection(vouchertype),"Invalid Voucher type");
	
	
					switch (vouchertype) {
					
					case "electronic":		
						System.out.println("Entering values for the Voucher Type: electronic");
						common_features.Add_VP_Input_Values.commoninputvalues(mrp, profilename, shortname, Minq, Perfq, tt, validity, expiryp);		
						break;
						
					
					case "physical" :
						System.out.println("Entering values for the Voucher Type: physical");
						common_features.Add_VP_Input_Values.physicalvdinputvalue(servicetype, subservicetype);
						common_features.Add_VP_Input_Values.commoninputvalues(mrp, profilename, shortname, Minq, Perfq, tt, validity, expiryp);	
						break;				
					
					case "SERVICE" :
						System.out.println("Entering values for the Voucher Type: SERVICE");
						common_features.Add_VP_Input_Values.servicevdinputvalue(subservicetype);
						common_features.Add_VP_Input_Values.commoninputvalues(mrp, profilename, shortname, Minq, Perfq, tt, validity, expiryp);	
						break;
						
					default:
						System.out.println("VoucherType is selected by default");
						common_features.Add_VP_Input_Values.physicalvdinputvalue(servicetype, subservicetype);
						common_features.Add_VP_Input_Values.commoninputvalues(mrp, profilename, shortname, Minq, Perfq, tt, validity, expiryp);	
						break;
						
						//end of switch statement
									}					
					
		//System.out.println("Closing the Switched window after entering the valid values.");

		}catch(AssertionError ae) {
			System.out.println("Assertion Error: No valid values entered by the superadmin");
			return false;
		}catch(Exception e) {
			System.out.println("Exception: No valid values entered by the superadmin");
			return false;	
		} 
		
		return true;
		}
	
}