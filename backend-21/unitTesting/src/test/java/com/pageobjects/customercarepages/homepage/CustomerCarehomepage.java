/**
 * 
 */
package com.pageobjects.customercarepages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class CustomerCarehomepage {

	@FindBy(xpath="//a[@href[contains(.,'moduleCode=PVTREC')]]")
	private WebElement privateRecharge;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=PVRCREG001')]]")
	private WebElement privateRcRegistration;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=PVRCMOD001')]]")
	private WebElement privateRcModification;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=PVRCENQ001')]]")
	private WebElement privateRcEnquiry;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=PVRCDEL001')]]")
	private WebElement privateRcDeactivation;
	
	@FindBy(xpath="//a[@href[contains(.,'moduleCode=P2PSUBS')]]")
	private WebElement p2pSubscribers;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=UPIN001')]]")
	private WebElement p2pPinMgmt;

	@FindBy(xpath="//a[@href[contains(.,'pageCode=DREGSUB001')]]")
	private WebElement deregSubscriber;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=SPNDSVC001')]]")
	private WebElement suspendService;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=RESMSVC001')]]")
	private WebElement resumeService;

	WebDriver driver=null;
	
	public CustomerCarehomepage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickPrivateRecharge(){
		Log.info("Trying to click private recharge link.");
		privateRecharge.click();
		Log.info("Private recharge clicked successfuly.");
	}
	
	public void clickPrivateRcRegistration(){
		Log.info("Trying to click private recharge registration link.");
		privateRcRegistration.click();
		Log.info("Private Recharge Registration link clicked successfuly");
	}
	
	public void clickPrivateRcModification(){
		Log.info("Trying to click private recharge modification.");
		privateRcModification.click();
		Log.info("Private Recharge Modification link clicked successfuly");
	}
	
	public void clickPrivateRcEnquiry(){
		Log.info("Trying to click private recharge enquiry.");
		privateRcEnquiry.click();
		Log.info("Private Recharge enquiry link clicked successfuly");
	}
	
	public void clickPrivateRcDeactivation(){
		Log.info("Trying to click private recharge deactivation.");
		privateRcDeactivation.click();
		Log.info("Private Recharge deactivation link clicked successfuly");
	}
	
	public void clickP2PSubscribersLink(){
		Log.info("Trying to click P2P Subscribers link.");
		p2pSubscribers.click();
		Log.info("P2P subscribers link clicked successfuly");
	}
	
	public void clickP2PPinMgmtLink(){
		Log.info("Trying to click P2P Pin management link.");
		p2pPinMgmt.click();
		Log.info("P2P Pin management link clicked successfuly");
	}
	
	public void clickP2PDeregisterSubsLink(){
		Log.info("Trying to click Deregister subscriber link.");
		deregSubscriber.click();
		Log.info("Deregister subscriber link clicked successfuly");
	}
	
	public void clickP2PSuspendServiceLink(){
		Log.info("Trying to click Suspend service link.");
		suspendService.click();
		Log.info("Suspend service link clicked successfuly");
	}
	
	public void clickP2PResumeServiceLink(){
		Log.info("Trying to click Resume service link.");
		resumeService.click();
		Log.info("Resume service link clicked successfuly");
	}
}
