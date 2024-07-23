package testcases;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import common_features.Login;
import common_util_script.Launchdriver;
public class TC1_Login_with_validcredentials {

		@Test 
	 	public static void login_with_valid_credentials (String username, String password, String url) throws Exception  {
		
	 	try {
	 	
	 	System.out.println("Scenario : Login to PreTUPS application with Valid credentials using object.properties");
			
		System.out.println("Enter a valid LoginID and Password");
	
		//launching the browser
		Launchdriver.driver = Launchdriver.browser("chrome");
		
		System.out.println("URL is: " +  url);
		System.out.println("Username is: " +  username);
		System.out.println("Password is: " +  password);
		
		//launching the URL
		common_util_script.Launch_Browser.launch(url);
		
		//Creating the instance of the LOGIN page
		Login loginpage = PageFactory.initElements(Launchdriver.driver, Login.class);
		
		System.out.println("ID: " + username + " and password is: " + password );
		
		//Enter the valid credentials
		Assert.assertTrue(loginpage.login_page(username,password));
		
		Launchdriver.driver.switchTo().frame(0);
		
		System.out.println("Now selecting the network");
		try{
		WebElement networkcode= Launchdriver.driver.findElement(By.xpath("//*[@type='radio' and @value='NG']"));
		
		Assert.assertTrue(networkcode.isDisplayed(),"No such network code exists");
		System.out.println("Network code exists. Now selecting the checkbox against the network code");
		
		networkcode.click();;
		
		}catch (Exception e) {	
		System.out.println("Network code does not exists");
		}
		
		Launchdriver.driver.findElement(By.name("submit1")).click();

	
		Thread.sleep(1000);
		Assert.assertTrue(Launchdriver.driver.findElement(By.cssSelector("a[href*='logout']")).isDisplayed(), "Login is not successfull");
		System.out.println("Login is successfull");
		
	
			
	}catch(AssertionError ae)  {
			System.out.println("Assertion Error : Login is not successfull.");
			//WebElement actualfailuremessage = Launchdriver.driver.findElement(By.xpath("//tbody/tr[1]/td/div/li"));
			//System.out.println("Failure reason is: " + actualfailuremessage.getText() );
		
			}
	 	catch(Exception e)  {
			System.out.println("Assertion Error : Login is not successfull.");
			//WebElement actualfailuremessage = Launchdriver.driver.findElement(By.xpath("//tbody/tr[1]/td/div/li"));
			//System.out.println("Failure reason is: " + actualfailuremessage.getText() );
		
			}
	 	
		}
	 	
}
