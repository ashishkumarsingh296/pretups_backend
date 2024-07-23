package common_features;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Login {

	@FindBy(xpath="//*[@id='loginID']")
	private WebElement username;
	
	@FindBy(xpath="//*[@id='password']")
	private WebElement password;
	
	@FindBy(xpath="//*[@type='submit' and @name='submit1']")
	private WebElement login;
	
	public boolean login_page (String uname, String passwd){
		try {
			
			username.clear();
			username.sendKeys(uname);
			
			System.out.println("Enter the password");
			//password.clear();
			password.sendKeys(passwd);
			
			System.out.println("Emailid/Username and Password is entered successfully");
			
			System.out.println("Now clicking on LOGIN button");
			login.click();
			
		} catch (Exception e){
			return false;
		}
		return true;
	}
}
