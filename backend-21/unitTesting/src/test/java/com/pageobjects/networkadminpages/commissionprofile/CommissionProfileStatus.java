package com.pageobjects.networkadminpages.commissionprofile;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Features.CommissionProfile;
import com.utils.Log;

public class CommissionProfileStatus {

	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement ErrorMessage;
	
	
	@FindBy(xpath ="//*[@id='formId']/div[1]/div/div/div/span")
	private WebElement message1;

	@FindBy(name="saveSuspend")
	private WebElement saveButton;

	@FindBy(name = "confirm")
	private WebElement confirm;
	
	@FindBy(xpath="//button[@id='alertify-ok']")
	private WebElement alertOK;



	WebDriver driver= null;
	public CommissionProfileStatus(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}



	public void ClickOnconfirm() {
		Log.info("Trying to click on button  confirm ");
		confirm.click();
		Log.info("Clicked on  confirm successfully");
	}
	
	public void clickOkAlertBtn() {
		try{
		Log.info("Trying to click OK Alert Button");
		alertOK.submit();
		Log.info("OK Alert Button clicked successfully");
		}
		catch(Exception e){}
		
		}

	public void ClickOnsave() {
		Log.info("Trying to click on button  Save ");
		saveButton.click();
		Log.info("Clicked on  Save successfully");
	}

	public String getMessage(){

		String msg = message.getText();

		Log.info("The Message is :" +msg);
		return msg;
	}

	public String getErrorMessage(){

		String msg = ErrorMessage.getText();

		Log.info("The Message is :" +msg);
		return msg;
	}

	/*public String CommissionProfileStatus(String CommProfile){

		String Status;

		//WebElement chkBox= driver.findElement(By.xpath("//tr/td[contains(text(),'"+ CommProfile +"')]/following-sibling::td/input[@type='checkbox']"));
		WebElement chkBox= driver.findElement(By.xpath("//tr/td[contains(.,'"+ CommProfile +"')]/../td/input[@type='checkbox']"));
		if(!chkBox.isSelected()){

			chkBox.click();
			Log.info("Comm Profile Status checkbox is now activated");

			Status = "Comm Profile status is active";


		}
		else{
			chkBox.click();
			Log.info("Commission Profile is now deactivated");

			Status = "Comm Profile is deactivated";

		}

		return Status;
	}
*/



	public String CommissionProfileStatus(String CommProfile) {

		String Status=null;
		//ArrayList  rowCount= (ArrayList)driver.findElements(By.tagName("tr"));
		List  rowCount=(List) driver.findElements(By.xpath("//*[@id='formId']/div[3]/table/tbody/tr"));
		int commProfiletable = rowCount.size();
		int i;
		
		for( i=1; i<commProfiletable;i++){
			if(driver.findElement(By.xpath("//*[@id='formId']/div[3]/table/tbody/tr["+i+"]/td[2]")).getText().equals(CommProfile)){
				Log.info("Profie Name: "+CommProfile+ "found.");
				System.out.println("The value of i is "+i);
				break;
			}
			
			WebElement  lang1= driver.findElement(By.xpath("//*[@id='formId']/div[3]/table/tbody/tr["+i+"]/td[4]"));
			WebElement  lang2= driver.findElement(By.xpath("//*[@id='formId']/div[3]/table/tbody/tr["+i+"]/td[5]"));
			WebElement chkBox= driver.findElement(By.xpath("//tr/td[contains(.,'"+ CommProfile +"')]/../td/input[@type='checkbox']"));


			if(!chkBox.isSelected()){
				chkBox.click();
				Log.info("Comm Profile Status checkbox is now activated");
				lang1.sendKeys("The Commission Profile" +CommProfile+ "is active");
				lang2.sendKeys("The Commission Profile" +CommProfile+ "is active");
				Status = "Comm Profile status is active";
			} else {
				chkBox.click();
				Log.info("Commission Profile is now deactivated");
				lang1.sendKeys("The Commission Profile" +CommProfile+ "is now deactivated");
				lang2.sendKeys("The Commission Profile" +CommProfile+ "is now deactivated");
				Status = "Comm Profile is deactivated";
			}
		}
		return Status;
	}
		
	





	public String CommissionProfileDefault(String CommProfile){

		String Status;

		WebElement defaultRadioButton= driver.findElement(By.xpath("//tr/td[contains(.,'"+ CommProfile +"')]/../td/input[@type='radio']"));

		if(!defaultRadioButton.isSelected()){

			defaultRadioButton.click();
			Log.info("Comm Profile [" + CommProfile + "] is now default");

			Status = "Comm Profile" + CommProfile + "status is default";


		}
		else{
			defaultRadioButton.click();
			Log.info("Commission Profile is not default");

			Status = "Comm Profile is default";

		}

		return Status;
	}

	public Object[] suspendCommProfilecheckbox(String profileName){
		boolean isprofiledefault = driver.findElement(By.xpath("//input[@type='checkbox']/../../td[text()='"+profileName+"']/following-sibling::td/input[@type='radio']")).isSelected();
		String reason = "Automation testing purpose";

		if(isprofiledefault){
			int x;
			int noOfcommProf = driver.findElements(By.xpath("//input[@type='checkbox']")).size();
			if(noOfcommProf > 1){
				for(x=0;x<noOfcommProf;x++){
					String profileText = driver.findElement(By.xpath("//input[@type='checkbox'][@name='selectCommProfileSetListIndexed["+x+"].status']/../../td[2]")).getText();
					if(profileText.equals(profileName)){
						break;
					}
				}
				if(x>=1&&x!=(noOfcommProf-1)){
					driver.findElement(By.xpath("//input[@type='checkbox'][@name='selectCommProfileSetListIndexed["+(x+1)+"].status']/../following-sibling::td/input[@type='radio']")).click();
					ClickOnsave();
					ClickOnconfirm();
					new CommissionProfile(driver).clickonprofilemgmtandsubmit();
					WebElement profileCheckbox= driver.findElement(By.xpath("//td[text()='"+profileName+"']/following-sibling::td/input[@type='checkbox']"));
					Log.info("1.Trying to click Commission Profile checkBox");
					profileCheckbox.click();
					Log.info("1.Commission Profile checkbox is clicked.");
				}
				else if(x==(noOfcommProf-1)){
					driver.findElement(By.xpath("//input[@type='checkbox'][@name='selectCommProfileSetListIndexed["+(x-1)+"].status']/../following-sibling::td/input[@type='radio']")).click();	
					ClickOnsave();
					ClickOnconfirm();
					new CommissionProfile(driver).clickonprofilemgmtandsubmit();
					WebElement profileCheckbox= driver.findElement(By.xpath("//td[text()='"+profileName+"']/following-sibling::td/input[@type='checkbox']"));
					Log.info("2.Trying to click Commission Profile checkBox");
					profileCheckbox.click();
					Log.info("2.Commission Profile checkbox is clicked.");
				}
				
				setMessage(profileName, reason);
			}else{
				Log.skip("Commission profile status cannot be changed as there is only single commission profile.");
			}}
		else{Log.info("Trying to click Commission Profile checkBox");
		WebElement profileCheckbox= driver.findElement(By.xpath("//td[text()='"+profileName+"']/following-sibling::td/input[@type='checkbox']"));	
		profileCheckbox.click();
			Log.info("Commission Profile checkbox is clicked.");
			
			setMessage(profileName, reason);
		}
		return new Object[]{isprofiledefault,reason};
		}
	
	
	public void resumeCommProfilecheckbox(String profileName, boolean isdefault){
		WebElement profileCheckbox = driver.findElement(By.xpath("//td[text()='"+profileName+"']/following-sibling::td/input[@type='checkbox']"));
		
			int noOfcommProf = driver.findElements(By.xpath("//input[@type='checkbox']")).size();
			if(noOfcommProf > 1){
					Log.info("1.Trying to click Commission Profile checkBox");
					profileCheckbox.click();
					Log.info("1.Commission Profile checkbox is clicked.");
					ClickOnsave();
					ClickOnconfirm();
					new CommissionProfile(driver).clickonprofilemgmtandsubmit();
				}
			else{
				Log.skip("Commission profile status need not be resumed as single commission profile cannot be suspended.");
			}

			if(isdefault){
				CommissionProfileDefault(profileName);
			}
		}
	
	public void setMessage(String profileName,String reason){
		WebElement langmsg1 = driver.findElement(By.xpath("//td[text()='"+profileName+"']/following-sibling::td/input[@type='checkbox']/../following-sibling::td[1]/textarea"));
		WebElement langmsg2 = driver.findElement(By.xpath("//td[text()='"+profileName+"']/following-sibling::td/input[@type='checkbox']/../following-sibling::td[2]/textarea"));
		langmsg1.clear();langmsg1.sendKeys(reason);
		langmsg2.clear();langmsg2.sendKeys(reason);
	}
	
	
	
	public String CommissionProfileDeactivateNegative(String CommProfile,String lang1message, String lang2message) {
		System.out.println("entering method");

		String Status=null;
		//ArrayList  rowCount= (ArrayList)driver.findElements(By.tagName("tr"));
		List  rowCount=(List) driver.findElements(By.xpath("//input[@type='checkbox']"));
		int commProfiletable = rowCount.size();
		int i;
		
		for( i=1; i<commProfiletable;i++){
			if(driver.findElement(By.xpath("//input[@type='checkbox'][@name='selectCommProfileSetListIndexed["+i+"].status']/../../td[2]")).getText().equals(CommProfile)){
				Log.info("Profie Name: "+CommProfile+ "found.");
				System.out.println("The value of i is "+i);
				break;
			}
			
			WebElement  lang1= driver.findElement(By.xpath("//td[text()='"+CommProfile+"']/following-sibling::td/input[@type='checkbox']/../following-sibling::td[1]/textarea"));
			WebElement  lang2= driver.findElement(By.xpath("//td[text()='"+CommProfile+"']/following-sibling::td/input[@type='checkbox']/../following-sibling::td[2]/textarea"));
			WebElement chkBox= driver.findElement(By.xpath("//tr/td[contains(.,'"+ CommProfile +"')]/../td/input[@type='checkbox']"));
			

			if(!chkBox.isSelected()){
				Log.info("Commission Profile is already deactivated");
				//lang1.sendKeys("The Commission Profile" +CommProfile+ "is now deactivated");
				//lang2.sendKeys("The Commission Profile" +CommProfile+ "is now deactivated");
				Status = "Comm Profile is already deactivated";	
				
			} else {
				chkBox.click();
				Log.info("Trying to deactivate Commission Profile without message");
				
				lang1.sendKeys(lang1message);
				lang2.sendKeys(lang2message);
				Status = "Language1 and Langguage2 Message is mandatory";
				
			}
		}
		return Status;
	}

}
