package testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_features.Login;
import common_util_script.Launchdriver;
public class Login_with_validcredentials {

	 		
	
	 	@Test
	 	public static void login_with_valid_credentials () throws Exception {
		
	 	try {
		 System.out.println("Scenario : Login to PreTUPS application with Valid credentials");
	 	//valid input parameters for login
		 
		 String url = "http://172.16.10.43:8597/pretups/";   
		 //valid URL for Jordan
			
		System.out.println("Enter a valid LoginID and Password");
		String loginid = "nadm";     // Valid Email ID
		String password = "com@1356";   //Valid Password
		String language = "English-2";   //English or Arabic specific to Jordan
		
		//launching the browser
		Launchdriver.driver = Launchdriver.browser("chrome");
		
		//launching the URL
		common_util_script.Launch_Browser.launch(url);
		
		//Creating the instance of the LOGIN page
		Login loginpage = PageFactory.initElements(Launchdriver.driver, Login.class);
		
		//Select language
		try{
		System.out.println("Selecting the language: ");
		common_features.Languageselection.arabicorenglish(language);
		System.out.println("You have selected the language: " + language );
		}catch(Exception e){
			
		}
		
		//Enter the valid credentials
		Assert.assertTrue(loginpage.login_page(loginid,password));
	
		Launchdriver.driver.switchTo().frame(0);
		WebElement logout = Launchdriver.driver.findElement(By.cssSelector("a[href*='logout']"));
		Assert.assertTrue(logout.isDisplayed(), "Login is not successfull");

		
		//Assert.assertEquals(Launchdriver.driver.getTitle(), "eRecharge" , "Login is not successfull. Enter the valid credentials");
		System.out.println("Login is successfull. Welcome to PreTUPS");
		common_util_script.Get_screenshot.success("login\\success\\","success");
			
	}catch (Exception e) {
		
		WebElement relogin = Launchdriver.driver.findElement(By.name("relogin"));
		if(relogin.isDisplayed()) {
			   System.out.println("Relogin button is displayed. Clicking on the relogin button");
			   relogin.click();
			   System.out.println("Relogin button is clicked successfully");
			   Thread.sleep(100);
			}
			//Launchdriver.driver.navigate().refresh();
			Launchdriver.driver.switchTo().frame(0);
			Assert.assertTrue(Launchdriver.driver.findElement(By.linkText("Logout")).isDisplayed());
			System.out.println("Login is successfull after the relogin. Welcome to PreTUPS");
			}
	 	
	 	}
}
