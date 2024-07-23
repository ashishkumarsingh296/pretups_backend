package common_util_script;
import org.testng.Assert;
public class Verify_URL {
	
	public static boolean success(String url){
		try{
			Thread.sleep(2000);	
			System.out.println("Current URL is : " +  Launchdriver.driver.getCurrentUrl());
			System.out.println("Expected URL is : " +  url);
			Assert.assertEquals(Launchdriver.driver.getCurrentUrl(), url , "Actual URL is not matching with the expected url. Test Case is failed");
			
			} catch(AssertionError ae) {
				System.out.println("Page is not created successfully");			
				
			} 		catch (Exception e){
								
				System.out.println("Page is not created successfully");
				return false;
			}
			return true;
		}
	
}
