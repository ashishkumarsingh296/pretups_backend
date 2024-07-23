package common.features;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
/**
 * Login PageFactory
 */
public class Login {

	private static Log _log = LogFactory.getFactory().getInstance(Login.class.getName());
	@FindBy(xpath="//*[@id='loginID']")
	private WebElement username;
	
	@FindBy(xpath="//*[@id='password']")
	private WebElement password;
	
	@FindBy(xpath="//*[@type='submit' and @value='Login']")
	private WebElement login;
	
	public boolean login_page (String uname, String passwd){
		try {
			
			username.clear();
			username.sendKeys(uname);
			
			if (_log.isDebugEnabled()) {
		         _log.debug("Enter the password");
		     }
			
			//password.clear();
			password.sendKeys(passwd);
			
			if (_log.isDebugEnabled()) {
		         _log.debug("Emailid/Username and Password is entered successfully");
		     }
			
			if (_log.isDebugEnabled()) {
		         _log.debug("Now clicking on LOGIN button");
		     }
			
			login.click();
			
		} catch (Exception e){
			return false;
		}
		return true;
	}
}
