package com.pageobjects.superadminpages.grouprolemanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class GroupRolesListPage {
	WebDriver driver;
	@ FindBy(name = "add")
	private WebElement add;

	@ FindBy(name = "edit")
	private WebElement edit;

	@ FindBy(name = "delete")
	private WebElement delete;

	@ FindBy(name = "back")
	private WebElement back;

	@ FindBy(name = "code")
	private WebElement groupRolesRadioBtn;

	public GroupRolesListPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickAddButton() throws InterruptedException {
		add.click();
		Log.info("User clicked add button.");
	}

	public void clickEditButton() throws InterruptedException {
		edit.click();
		Log.info("User clicked edit button.");
	}

	public void clickDeleteButton() throws InterruptedException {
		delete.click();
		Log.info("User clicked delete button.");
	}

	public void clickBackButton() throws InterruptedException {
		back.click();
		Log.info("User clicked back button.");
	}

	public void clickGroupRoleRadioButton(String GroupRole) throws InterruptedException {
		String groupRole1= GroupRole.toUpperCase();
		System.out.println(groupRole1);
		WebElement groupRole = driver.findElement(By.xpath("//input[@value='"+groupRole1+"']"));
		groupRole.click();
		System.out.println(groupRole);
		Log.info("User clicked Group Role Code.");
	}

	public boolean groupRoleExistenceCheck(String GroupRole){
		try{
			if(driver.findElement(By.xpath("//input[@value='"+GroupRole+"']")).isDisplayed())
				Log.info("Group Role already exists as: " +GroupRole);
			return true;
		}
		catch(NoSuchElementException e){
			Log.info("Group Role doesn't exist..");
			return false;
		}

	}
}
