package testcases;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TC4_Activate_Profile {

	
	@Test
	public static void add_activate_voucher_profile (String Scenario, String vouchertype, String servicetype, String subservicetype, String denominationname, String shortname, String mrp, String payableamnt, String description, String profilename, String Minq, String Perfq, String tt, String validity, String expiryp, String applicablefrom) throws Exception{
	
		System.out.println("");
		System.out.println("Now adding the active profile details");
		
		
		System.out.println("");
		System.out.println("Now adding the profile for the newly added denomination");
		
		System.out.println("Now clicking on the VOUCHER PROFILE");
		//clicking on Voucher Profile
		common_features.VoucherDenomination_Options.clicklink("Voucher profile");
		
		System.out.println("Now clicking on the ACTIVE PROFILE DETAILS");
		//clicking on Voucher Profile
		common_features.VoucherDenomination_Options.clicklink("Add active profile details");
		
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

		Assert.assertTrue(common_features.Add_Active_Profiles.commoninputvalues(dt, denominationname, profilename), "Profile is not activated successfully");		
		
	}
				
}
