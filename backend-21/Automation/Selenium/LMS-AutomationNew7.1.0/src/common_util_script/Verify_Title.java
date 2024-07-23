package common_util_script;

import common_util_script.Launchdriver;

import org.testng.Assert;

public class Verify_Title {

	public static boolean success(String title){
		try{
			Time_Wait.timewait(5);			
			System.out.println("Title of this page is : " + Launchdriver.driver.getTitle() );
			Assert.assertEquals(Launchdriver.driver.getTitle(), title , "Actual Title is not matching the expected title. Test Case is failed");
			System.out.println("Your title is matching. Let's proceed further");
		}catch (Exception e){
				return false;
			}
			return true;
		}
}
