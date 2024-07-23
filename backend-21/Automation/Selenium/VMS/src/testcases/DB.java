package testcases;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_util_script.DB_Connection;
import common_util_script.Launchdriver;

public class DB {

	
	@Test
	public static void logout() throws Exception{
	
		
		String startserialnoquery="select * from voms_print_batches where printer_batch_id=(select printer_batch_id from voms_print_batches where product_id=(select product_id from voms_products where product_name='AZ25'));";
		common_util_script.DB_Connection.datafromdb(startserialnoquery,"start_serial_no");
		System.out.println("FromSerialNumber value is entered as: " + DB_Connection.dbvalue);
	
		String endserialnoquery="select * from voms_print_batches where printer_batch_id=(select printer_batch_id from voms_print_batches where product_id=(select product_id from voms_products where product_name='AZ25'));";		
		common_util_script.DB_Connection.datafromdb(endserialnoquery,"end_serial_no");
		System.out.println("EndSerialNumber value is entered as: " + DB_Connection.dbvalue);
		
		}		
	
				
}
