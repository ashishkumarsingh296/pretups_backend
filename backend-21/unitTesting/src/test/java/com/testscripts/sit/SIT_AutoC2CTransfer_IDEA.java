package com.testscripts.sit;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.Features.AutoC2Ccreditlimit;
import com.Features.mapclasses.Channel2ChannelMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_AutoC2CTransfer_IDEA extends BaseTest{

	static boolean testCaseCounter = false;
	Channel2ChannelMap c2cMap;
	AddChannelUserDetailsPage message;
	String actual=null;
	String moduleCode;
	@BeforeMethod()
	public void autoc2cdata(){
		c2cMap = new Channel2ChannelMap();
		message = new AddChannelUserDetailsPage(driver);
		moduleCode = "[SIT]"+_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA1").getModuleCode();
	}
	
	@Test
	public void _001_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA1").getExtentCase());
		currentNode.assignCategory("SIT");
		autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), "0","1","1","2");
		String expected=MessagesDAO.getLabelByKey("autoC2C.associatesubscriberdetailsconfirm.msg.autoc2csuccess");
		actual=message.getActualMessage();
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _002_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA2").getExtentCase());
		currentNode.assignCategory("SIT");
		
		autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), "100","1","1","2");
		String expected=MessagesDAO.getLabelByKey("autoC2C.associatesubscriberdetailsconfirm.msg.autoc2csuccess");
		actual=message.getActualMessage();
		Validator.messageCompare(actual, expected);
	}
	@Test
	public void _003_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA3").getExtentCase());
		currentNode.assignCategory("SIT");
		
		autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), "100","1","1","2");
		String expected=MessagesDAO.getLabelByKey("autoC2C.associatesubscriberdetailsconfirm.msg.autoc2csuccess");
		actual=message.getActualMessage();
		Validator.messageCompare(actual, expected);
		}

	@Test
	public void _004_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		RandomGeneration rndGeneration = new RandomGeneration();
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA4").getExtentCase());
		currentNode.assignCategory("SIT");
		String expected=null;
		String alphanum= rndGeneration.randomAlphaNumeric(6);
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), alphanum,"1","1","2");
		actual=message.getActualMessage();
		}catch(Exception e){expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.maxtrxamtnonnumeric");
		actual=message.getActualMessage();}	
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _005_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA5").getExtentCase());
		currentNode.assignCategory("SIT");
		String expected=null;
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), "","1","1","2");
		actual=message.getActualMessage();}
		catch(Exception e){expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.maxtrxamtnull");
		actual=message.getActualMessage();}
		Validator.messageCompare(actual, expected);
	}	

	@Test
	public void _006_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA6").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = new RandomGeneration().randomNumeric(15);
		autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","1","2");
		actual=message.getActualMessage();ExtentI.attachScreenShot();
		String expected=MessagesDAO.getLabelByKey("autoC2C.associatesubscriberdetailsconfirm.msg.autoc2csuccess");
		Validator.messageCompare(actual, expected);
		}

	@Test
	public void _007_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA7").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "-"+new RandomGeneration().randomNumeric(3);
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","1","2");
		actual=message.getActualMessage();}
		catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.maxtrxamtnonnumeric");}
		Validator.messageCompare(actual, expected);
	}

	@Test
	public void _008_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA8").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","1","2");
		actual=message.getActualMessage();
		String expected=MessagesDAO.getLabelByKey("autoC2C.associatesubscriberdetailsconfirm.msg.autoc2csuccess");
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _009_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA9").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","1","2");
		actual=message.getActualMessage();
		String expected=MessagesDAO.getLabelByKey("autoC2C.associatesubscriberdetailsconfirm.msg.autoc2csuccess");
		Validator.messageCompare(actual, expected);	
	}

	@Test
	public void _010_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA10").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","1","2");
		actual=message.getActualMessage();
		String expected=MessagesDAO.getLabelByKey("autoC2C.associatesubscriberdetailsconfirm.msg.autoc2csuccess");
		Validator.messageCompare(actual, expected);		
	}
	
	@Test
	public void _011_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		RandomGeneration rndgnr = new RandomGeneration();
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA11").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		String dailycount = rndgnr.randomAlphabets(1)+rndgnr.randomNumberWithoutZero(1);
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,dailycount,"1","2");
		actual=message.getActualMessage();}
		catch(Exception e){actual=message.getActualMessage();
		expected = MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.dailycountnonnumeric");}
		Validator.messageCompare(actual, expected);
	}

	@Test
	public void _012_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		RandomGeneration rndgnr = new RandomGeneration();
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA12").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		String weeklycount = rndgnr.randomAlphabets(1)+rndgnr.randomNumberWithoutZero(1);
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1",weeklycount,"2");
		actual=message.getActualMessage();}
		catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.weeklycountnonnumeric");}
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _013_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		RandomGeneration rndgnr = new RandomGeneration();
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA13").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		String monthlycount = rndgnr.randomAlphabets(1)+rndgnr.randomNumberWithoutZero(1);
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","1",monthlycount);
		actual=message.getActualMessage();}
		catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.monthlycountnonnumeric");}
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _014_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		RandomGeneration rndgnr = new RandomGeneration();
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA14").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		String count = rndgnr.randomNumberWithoutZero(15);
		autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,count,count,count);
		actual=message.getActualMessage();
		String expected = MessagesDAO.getLabelByKey("autoC2C.associatesubscriberdetailsconfirm.msg.autoc2csuccess");
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _015_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA15").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"-1","1","2");
		actual=message.getActualMessage();}catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.dailycountnonnumeric");}
		Validator.messageCompare(actual, expected);
	}

	@Test
	public void _016_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA16").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","-1","2");
		actual=message.getActualMessage();}
		catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.weeklycountnonnumeric");}
		Validator.messageCompare(actual, expected);
	}

	@Test
	public void _017_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA17").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","1","-2");
		actual=message.getActualMessage();}catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.monthlycountnonnumeric");}
		Validator.messageCompare(actual, expected);
	}

	@Test
	public void _018_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA18").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"","","2");
		actual=message.getActualMessage();}catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.dailycountnull");}
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _019_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA19").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"","1","");
		actual=message.getActualMessage();}catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.dailycountnull");}
		Validator.messageCompare(actual, expected);	
	}

	@Test
	public void _020_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA20").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","","");
		actual=message.getActualMessage();}catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.weeklycountnull");}
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _021_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA21").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"","1","2");
		actual=message.getActualMessage();}
		catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.dailycountnull");}
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _022_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA22").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","","2");
		actual=message.getActualMessage();}catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.weeklycountnull");}
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void _023_autoC2CTransfer() {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(moduleCode);
			testCaseCounter = true;
		}
		String expected=null;
		AutoC2Ccreditlimit autoCreditlimit= new AutoC2Ccreditlimit(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOC2CTRFIDEA23").getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "100";
		try{autoCreditlimit.performautoc2ccreditlimit(c2cMap.getC2CMap("toMSISDN"), amount,"1","1","");
		actual=message.getActualMessage();}catch(Exception e){actual=message.getActualMessage();
		expected=MessagesDAO.getLabelByKey("autoc2c.associatesubscriberdetails.msg.monthlycountnull");}
		Validator.messageCompare(actual, expected);
		}
	
	}
