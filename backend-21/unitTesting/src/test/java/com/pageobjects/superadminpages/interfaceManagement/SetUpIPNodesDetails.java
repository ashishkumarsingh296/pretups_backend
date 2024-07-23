package com.pageobjects.superadminpages.interfaceManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.MasterI;
import com.utils.Log;
import com.utils._masterVO;

public class SetUpIPNodesDetails {
	
	@FindBy(name = "nodeSlabsListIndexed[0].ip")
	private WebElement nodeSlabsListIndexed0;
	
	@FindBy(name = "nodeSlabsListIndexed[1].ip")
	private WebElement nodeSlabsListIndexed1;

	
	@FindBy(name = "nodeSlabsListIndexed[0].port")
	private WebElement nodeSlabsListPort_0;
	
	@FindBy(name = "nodeSlabsListIndexed[1].port")
	private WebElement nodeSlabsListPort_1;
	
	
	@FindBy(name = "nodeSlabsListIndexed[0].uri")
	private WebElement nodeSlabsListURI_0;
	
	@FindBy(name = "nodeSlabsListIndexed[1].uri")
	private WebElement nodeSlabsListURI_1;
	
	@FindBy(name = "nodeSlabsListIndexed[0].nodeStatus")
	private WebElement nodeSlabsListStatus_0;
	
	@FindBy(name = "nodeSlabsListIndexed[1].nodeStatus")
	private WebElement nodeSlabsListStatus_1;
	
	
	@FindBy(name = "addNodes")
	private WebElement addNodes;
	
	
WebDriver driver=null;
	
	public SetUpIPNodesDetails(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	
	public void enterNode1IP(){
		String IP = _masterVO.getMasterValue("Putty IP");
		Log.info("Trying to enter IP.");
		nodeSlabsListIndexed0.sendKeys(IP);
		
		Log.info("User entered Node 1 IP as" +IP);
		
	}
	
	public void enterNode2IP(){
		String IP = _masterVO.getMasterValue("Putty IP");
		nodeSlabsListIndexed1.sendKeys(IP);
		
		Log.info("User entered Node 2 IP as" +IP);
		
	}
	
	
	public void enterNode1Port(){
		String Port = null;
		final String regex = "(\\d{4})";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(_masterVO.getMasterValue(MasterI.WEB_URL));
		while (matcher.find()) {
		    for (int i = 1; i <= matcher.groupCount(); i++) {
		    	Port = matcher.group(i);
		    }
		}
		
		nodeSlabsListPort_0.sendKeys(Port);
		
		Log.info("User entered Node 1 IP Port as" + Port);
		
	}
	
	
	public void enterNode2Port(){
		String Port = null;
		final String regex = "(\\d{4})";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(_masterVO.getMasterValue(MasterI.WEB_URL));
		while (matcher.find()) {
		    for (int i = 1; i <= matcher.groupCount(); i++) {
		    	Port = matcher.group(i);
		    }
		}
		nodeSlabsListPort_1.sendKeys(Port);
		
		Log.info("User entered Node 2 IP Port as" + Port);
		
	}
	
	
	public void enterNode1URI(){
		String URI = _masterVO.getMasterValue("WEB URL");
		nodeSlabsListURI_0.sendKeys(URI);
		
		Log.info("User entered Node 1 URI as" +URI);
		
	}
	
	public void enterNode2URI(){
		String URI = _masterVO.getMasterValue("WEB URL");
		nodeSlabsListURI_1.sendKeys(URI);
		
		Log.info("User entered Node 2 URI as" +URI);
		
	}
	
	public void selectNode1Status(String status) {
			Select select = new Select(nodeSlabsListStatus_0);
			select.selectByValue(status);
			
			Log.info("User selected status as  ["+status+"]");
			
			
		}
	
	public void selectNode2Status(String status) {
		Select select = new Select(nodeSlabsListStatus_1);
		select.selectByValue(status);
		
		Log.info("User selected status as  ["+status+"]");
		
		
	}
	
	public void clickAddNodes(){
		
		addNodes.click();	
	}
}
