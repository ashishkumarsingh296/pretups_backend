package testcases;

import java.sql.SQLException;
import java.util.List;
import org.testng.Assert;

public class TC14_Change_Other_Status {
	
	public static void change_other_status (String mrp, String productname, String vouchertstatus, String expectedvoucherstatus ) throws Exception{
		System.out.println("Test Case: Addition of the voucher denomination");
	 
		System.out.println("");
		
		System.out.println("Now clicking on the VOUCHER DENOMINATION");
		//clicking on Voucher denomination
		common_features.VoucherDenomination_Options.clicklink("Voucher denomination");
		
		System.out.println("Clicking on Change other status");
		//Select sub option
		common_features.VoucherDenomination_Options.clicklink("Change other status");
		
		System.out.println("Now entering the value in the input fields");
		
		common_util_script.DBQueries_bkp.connecttoDB();
		List<String> results = readfromdb("voms_print_batches.start_serial_no",productname);
		System.out.println("Start Value is: " +  results.get(0));
		Assert.assertTrue(common_util_script.CheckInputFields.click("fromSerial", results.get(0)));
		System.out.println("FromSerialNumber value is entered as: " + results.get(0));
		
		List<String> results1 = readfromdb("voms_print_batches.end_serial_no",productname);
		System.out.println("End Value is: " +  results1.get(0));
		
		Assert.assertTrue(common_util_script.CheckInputFields.click("toSerial", results1.get(0)));
		System.out.println("ToSerialNumber value is entered as: " + results1.get(0));
		
		//totalnumberofvouchers
		long start = Long.valueOf(results.get(0));
		long end = Long.valueOf(results1.get(0));
		long diff = end - start + 1;
		System.out.println("Total number of vouchers are: " + diff);
		
		String totalvouchers = Long.toString(diff);
		
		Assert.assertTrue(common_util_script.CheckInputFields.click("totalNoOfVouchStr",totalvouchers));
		System.out.println("TotalNumberOfVouchers value is entered as: " + diff);
		
		//MRP
		Assert.assertTrue(common_util_script.CheckInputFields.click("mrpStr", mrp));
		System.out.println("MRP value is entered as: " + mrp);
				
		//Selecting the profile from dropdown 
		Assert.assertTrue(common_util_script.Selectfromdropdown.select("productID", productname));
		System.out.println("ProfileID selected is : " + productname);
		
		//Selecting the status from dropdown
		Assert.assertTrue(common_util_script.Selectfromdropdown.select("voucherStatus", vouchertstatus));
		System.out.println("Voucher Status selected is : " + vouchertstatus);
		
		//Clicking on submit button
		Assert.assertTrue(common_util_script.ClickButton.click("changeOtherStatus"));
		System.out.println("Submit button is clicked successfully");
		
		Assert.assertTrue(common_util_script.CheckButton.click("cancelOther"),"Confirm button is not displayed. Issue with the input values provided.");
		Assert.assertTrue(common_util_script.ClickButton.click("changeotherStatus"));
		
		Thread.sleep(500);
		List<String> results3 = readfromdb1(results.get(0),results1.get(0));
		System.out.println("Batch number is : " +  results3.get(0));
		String batchno = results3.get(0);	
		
		String expectedmessage = "Your batch with number " + batchno+" has been generated. Status change is under process. Please check the batch status using the \"View batch list\" option.";
		Assert.assertTrue(common_util_script.Verify_Text.enteryourtext(expectedmessage), "Status is not changed");
		
		for(long i = start;i<=end;i++){
			String vouchernumber = Long.toString(i);
			System.out.println("Checking current status of vouchernumber: " + vouchernumber);
			List<String> voucherresult = checkstatusfromdb(vouchernumber);
			System.out.println("Batch number is : " +  voucherresult.get(0));
			Assert.assertEquals(voucherresult.get(0), expectedvoucherstatus);
			System.out.println("Status of vouchernumber: " + vouchernumber + " is changed successfully to: " + expectedvoucherstatus);
			
		}
		common_util_script.DBQueries_bkp.dbtearDown();
	}


	 public static List<String> readfromdb(String column, String productname) throws SQLException{
			//common_util_script.DBQueries_bkp.connecttoDB();
			String query3 = "SELECT "+column+" from voms_print_batches INNER JOIN voms_products ON voms_print_batches.Product_ID=voms_products.Product_ID where voms_products.product_name='"+productname+"'";
			return common_util_script.DBQueries_bkp.runningDBquery(query3);
	 }
	 
	 public static List<String> readfromdb1(String from, String to) throws SQLException{
			//common_util_script.DBQueries_bkp.connecttoDB();
			String query3 = "SELECT batch_no FROM voms_batches where from_serial_no='"+from+"' and to_serial_no='"+to+"'  order by batch_no DESC";
			return common_util_script.DBQueries_bkp.runningDBquery(query3);
	 }
	 
	 public static List<String> checkstatusfromdb(String vouchernumber) throws SQLException{
			//common_util_script.DBQueries_bkp.connecttoDB();
			String query3 = "select current_status from voms_vouchers where serial_no='"+vouchernumber+"'";
			return common_util_script.DBQueries_bkp.runningDBquery(query3);
	 }
	
}
