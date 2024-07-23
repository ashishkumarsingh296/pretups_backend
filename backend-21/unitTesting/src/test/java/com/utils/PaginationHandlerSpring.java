package com.utils;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PaginationHandlerSpring {

	public ArrayList<String> getTxnIDFromEachpage(WebDriver driver){
		List<WebElement> wb = driver.findElements(By.xpath("//*[@id='O2CTransferReports_paginate']/ul/li/a"));
		int pages  = wb.size()-2;
		
		Log.info("Total number of pages: "+pages);
		ArrayList<String> txnList = new ArrayList<String>(); 
		int pagenum=1;
		for(int x=0;x<pages;x++){
			ArrayList<String> txnListperpage = new ArrayList<String>();
			Log.info("Browsing page no. : "+pagenum);
			List<WebElement> txns = driver.findElements(By.xpath("//*[@id='O2CTransferReports']/tbody//td[3]"));
			int txnIDs = txns.size();
			for(int i=1;i<=txnIDs;i++){
			txnListperpage.add(driver.findElement(By.xpath("//*[@id='O2CTransferReports']/tbody/tr["+i+"]/td[3]")).getText());
			}
			
			for(int p=0;p<txnListperpage.size();p++)
			{Log.info("["+pagenum+"]-> "+txnListperpage.get(p));}
			
			if(pages>1&&pagenum<pages){
			driver.findElement(By.xpath("//*[@id='O2CTransferReports_paginate']/ul/li/a[@data-dt-idx='"+(pagenum+1)+"']")).click();
			pagenum++;}
			else if(pages==1){Log.info("Only single page exists.");}
			txnList.addAll(txnListperpage);
		}
		return txnList;
	}
	
	
	public ArrayList<String> getTxnIDFromEachpageforZeroBalCoutnerSumm(WebDriver driver){
		List<WebElement> wb = driver.findElements(By.xpath("//*[@id='zeroBalCounterReportsSumm_paginate']/ul/li/a"));
		int pages  = wb.size()-2;
		
		Log.info("Total number of pages: "+pages);
		ArrayList<String> txnList = new ArrayList<String>(); 
		int pagenum=1;
		for(int x=0;x<pages;x++){
			ArrayList<String> txnListperpage = new ArrayList<String>();
			Log.info("Browsing page no. : "+pagenum);
			List<WebElement> txns = driver.findElements(By.xpath("//*[@id='zeroBalCounterReportsSumm']/tbody//td[2]"));
			int txnIDs = txns.size();
			for(int i=1;i<=txnIDs;i++){
			txnListperpage.add(driver.findElement(By.xpath("//*[@id='zeroBalCounterReportsSumm']/tbody/tr["+i+"]/td[2]")).getText());
			}
			
			for(int p=0;p<txnListperpage.size();p++)
			{Log.info("["+pagenum+"]-> "+txnListperpage.get(p));}
			
			if(pages>1&&pagenum<pages){
			driver.findElement(By.xpath("//*[@id='zeroBalCounterReportsSumm_paginate']/ul/li/a[@data-dt-idx='"+(pagenum+1)+"']")).click();
			pagenum++;}
			else if(pages==1){Log.info("Only single page exists.");}
			txnList.addAll(txnListperpage);
		}
		return txnList;
	}
	public ArrayList<String> getTxnIDFromEachpageforExternaluserRoles(WebDriver driver){
		List<WebElement> wb = driver.findElements(By.xpath("//*[@id='channelUserRolesReports_paginate']/ul/li/a"));
		int pages  = wb.size()-2;
		
		Log.info("Total number of pages: "+pages);
		ArrayList<String> txnList = new ArrayList<String>(); 
		int pagenum=1;
		for(int x=0;x<pages;x++){
			ArrayList<String> txnListperpage = new ArrayList<String>();
			Log.info("Browsing page no. : "+pagenum);
			List<WebElement> txns = driver.findElements(By.xpath("//*[@id='channelUserRolesReports']/tbody//td[5]"));
			int txnIDs = txns.size();
			for(int i=1;i<=txnIDs;i++){
			txnListperpage.add(driver.findElement(By.xpath("//*[@id='channelUserRolesReports']/tbody/tr["+i+"]/td[5]")).getText());
			}
			
			for(int p=0;p<txnListperpage.size();p++)
			{Log.info("["+pagenum+"]-> "+txnListperpage.get(p));}
			
			if(pages>1&&pagenum<pages){
			driver.findElement(By.xpath("//*[@id='channelUserRolesReports_paginate']/ul/li/a[@data-dt-idx='"+(pagenum+1)+"']")).click();
			pagenum++;}
			else if(pages==1){Log.info("Only single page exists.");}
			txnList.addAll(txnListperpage);
		}
		return txnList;
	}
	public ArrayList<String> getTxnIDFromEachpageforstaffSelfC2CReport(WebDriver driver){
		List<WebElement> wb = driver.findElements(By.xpath("//*[@id='channelUserRolesReports_paginate']/ul/li/a"));
		int pages  = wb.size()-2;
		
		Log.info("Total number of pages: "+pages);
		ArrayList<String> txnList = new ArrayList<String>(); 
		int pagenum=1;
		for(int x=0;x<pages;x++){
			ArrayList<String> txnListperpage = new ArrayList<String>();
			Log.info("Browsing page no. : "+pagenum);
			List<WebElement> txns = driver.findElements(By.xpath("//*[@id='channelUserRolesReports']/tbody//td[5]"));
			int txnIDs = txns.size();
			for(int i=1;i<=txnIDs;i++){
			txnListperpage.add(driver.findElement(By.xpath("//*[@id='channelUserRolesReports']/tbody/tr["+i+"]/td[5]")).getText());
			}
			
			for(int p=0;p<txnListperpage.size();p++)
			{Log.info("["+pagenum+"]-> "+txnListperpage.get(p));}
			
			if(pages>1&&pagenum<pages){
			driver.findElement(By.xpath("//*[@id='channelUserRolesReports_paginate']/ul/li/a[@data-dt-idx='"+(pagenum+1)+"']")).click();
			pagenum++;}
			else if(pages==1){Log.info("Only single page exists.");}
			txnList.addAll(txnListperpage);
		}
		return txnList;
	}
	
	public ArrayList<String> getMSISDNFromEachpage(WebDriver driver){
		List<WebElement> wb = driver.findElements(By.xpath("//*[@id='userReports_paginate']/ul/li/a"));
		int pages  = wb.size()-2;
		
		Log.info("Total number of pages: "+pages);
		ArrayList<String> txnList = new ArrayList<String>(); 
		int pagenum=1;
		for(int x=0;x<pages;x++){
			ArrayList<String> txnListperpage = new ArrayList<String>();
			Log.info("Browsing page no. : "+pagenum);
			List<WebElement> txns = driver.findElements(By.xpath("//*[@id='userReports']/tbody//td[3]"));
			int txnIDs = txns.size();
			for(int i=1;i<=txnIDs;i++){
			txnListperpage.add(driver.findElement(By.xpath("//*[@id='userReports']/tbody/tr["+i+"]/td[3]")).getText());
			}
			
			for(int p=0;p<txnListperpage.size();p++)
			{Log.info("["+pagenum+"]-> "+txnListperpage.get(p));}
			
			if(pages>1&&pagenum<pages){
			driver.findElement(By.xpath("//*[@id='userReports_paginate']/ul/li/a[@data-dt-idx='"+(pagenum+1)+"']")).click();
			pagenum++;}
			else if(pages==1){Log.info("Only single page exists.");}
			txnList.addAll(txnListperpage);
		}
		return txnList;
	}
	
	
	
	
	
}
