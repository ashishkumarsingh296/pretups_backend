package com.testscripts.sit;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.VMS;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_Voucher_Expiry_Extension)
public class SIT_VoucherExpiryDateChange extends BaseTest{

	static String moduleCode;
	static String assignCategory="SIT";
	String dateFormatGUI = "dd/MM/yy";
	
	@DataProvider(name="positiveData")
	public Object[] pData(){
		
		Object[] data = new Object[]{
				PretupsI.ENABLE,
				PretupsI.GENERATED,
				PretupsI.SUSPENDED,
				PretupsI.ONHOLD,
				PretupsI.UNDER_PROCESS
		};
		
		return data;
		
	}
	
	@Test(dataProvider = "positiveData")
	@TestManager(TestKey = "PRETUPS-2002")
	public void _01_VoucherExpiryDateChange(String status) throws InterruptedException, ParseException {
		final String methodName = "_01_VoucherExpiryDateChange";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYCHANGE01");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),status));
		currentNode.assignCategory(assignCategory);
		NetworkAdminHomePage nahp  = new NetworkAdminHomePage(driver);
		
		String fromSerialNo = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
		if(BTSLUtil.isNullString(fromSerialNo)){
			Assertion.assertSkip("Voucher with Status ["+status+"] not found in the system.");
		}
		else{
		String toSerialNo = fromSerialNo;
		String vouchers = String.valueOf(Long.parseLong(toSerialNo) - Long.parseLong(fromSerialNo) + 1);
		String exp_date = nahp.addDaysToCurrentDate(_APIUtil.getCurrentTimeStamp(), 1);
		String date = new SimpleDateFormat(dateFormatGUI).format(new SimpleDateFormat("dd/MM/yy").parse(exp_date));
		
		vms.changeVoucherExpiryDate(date, fromSerialNo, toSerialNo, vouchers);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(fromSerialNo, "expiry_date","consume_before");
		
		Date edate=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String act_date1 = new SimpleDateFormat("dd/MM/yy").format(edate);
		
		Date edate1=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[1]);  
		String act_date2 = new SimpleDateFormat("dd/MM/yy").format(edate1);
		
		/*Date edate3=new SimpleDateFormat("yyyy/MM/dd").parse(date);
		String exp_date = new SimpleDateFormat("dd/MM/yy").parse(date);*/
		
		Log.info("Expiry Date:");
		Assertion.assertEquals(act_date1, exp_date);
		
		Log.info("Consumed Before Date:");
		Assertion.assertEquals(act_date2, exp_date);
		
		Object[] msg = ExtentI.getMessageOnScreen();
		if(String.valueOf(msg[0]).equalsIgnoreCase("true")){
			Assertion.assertFail(String.valueOf(msg[1]));
			ExtentI.attachScreenShot();
		}
		}
		
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-2003")
	public void _02_VoucherExpiryDateChange() throws InterruptedException, ParseException {
		final String methodName = "_02_VoucherExpiryDateChange";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYCHANGE02");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory(assignCategory);
		NetworkAdminHomePage nahp  = new NetworkAdminHomePage(driver);
		
		long totalVouchers = 4L;
		String fromSerialNo = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.ENABLE);
		String toSerialNo = String.valueOf(Long.parseLong(fromSerialNo)+totalVouchers - 1L);
		String serialNo;
		
		Object[][] vdata = new Object[(int) totalVouchers][2];
		for(int i=0;i<totalVouchers;i++)
		{
			 serialNo= String.valueOf(Long.parseLong(fromSerialNo)+i);
			 
			 vdata[i][0] = serialNo;
			 vdata[i][1] = DBHandler.AccessHandler.getVoucherStatus(serialNo);
		}
		
		
		String vouchers = String.valueOf(Long.parseLong(toSerialNo) - Long.parseLong(fromSerialNo) + 1);
		String exp_date = nahp.addDaysToCurrentDate(_APIUtil.getCurrentTimeStamp(), 1);
		String date = new SimpleDateFormat(dateFormatGUI).format(new SimpleDateFormat("dd/MM/yy").parse(exp_date));
		
		vms.changeVoucherExpiryDate(date, fromSerialNo, toSerialNo, vouchers);
		
		Object[][] vdata1 = new Object[(int) totalVouchers][2];
		for(int i=0;i<totalVouchers;i++)
		{
			 vdata1[i][0] = vdata[i][0];
			 vdata1[i][1] = DBHandler.AccessHandler.getVoucherStatus(vdata1[i][0].toString());
		}
		
		List<String> namesList = Arrays.asList(PretupsI.ENABLE,PretupsI.GENERATED,PretupsI.SUSPENDED,PretupsI.ONHOLD,PretupsI.UNDER_PROCESS);
		ArrayList<String> statusList = new ArrayList<String>();
		statusList.addAll(namesList);
		
		for(int i = 0;i<totalVouchers;i++){
			
				String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(vdata[i][0].toString(), "expiry_date","consume_before");
				if(!BTSLUtil.isNullString(values[0])){
				Date edate=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
				String act_date1 = new SimpleDateFormat("dd/MM/yy").format(edate);
				
				Date edate1=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[1]);  
				String act_date2 = new SimpleDateFormat("dd/MM/yy").format(edate1);
				
				/*Date edate3=new SimpleDateFormat("yyyy/MM/dd").parse(date);
				String exp_date = new SimpleDateFormat("dd/MM/yy").format(edate3);
				*/
				if(statusList.contains(vdata[i][1])){
					Log.info("Expiry Date for SerialNo:"+vdata[i][0].toString()+" | Status:"+vdata[i][1].toString());
					Assertion.assertEquals(act_date1, exp_date);
					
					Log.info("Consumed Before Date:"+vdata[i][0].toString()+" | Status:"+vdata[i][1].toString());
					Assertion.assertEquals(act_date2, exp_date);
			}else if(act_date1.equals(exp_date)||act_date2.equals(exp_date)){
				Assertion.assertFail("Expiry date change is not allowed for vouchers with status other than " + Arrays.toString(namesList.toArray())+ ", still expiry date changed for Voucher ["+vdata[i][0]+"] with status "+vdata[i][1].toString());
			}}
		}
		Object[] msg = ExtentI.getMessageOnScreen();
		if(String.valueOf(msg[0]).equalsIgnoreCase("true")){
			Assertion.assertFail(String.valueOf(msg[1]));
			ExtentI.attachScreenShot();
		}
		
	}

	@Test
	@TestManager(TestKey = "PRETUPS-2004")
	public void _03_VoucherExpiryDateChange() throws InterruptedException, ParseException {
		final String methodName = "_03_VoucherExpiryDateChange";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYCHANGE03");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory(assignCategory);
		NetworkAdminHomePage nahp  = new NetworkAdminHomePage(driver);
		
		String fromSerialNo = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.CONSUMED);
		if(BTSLUtil.isNullString(fromSerialNo)){
			Assertion.assertSkip("Voucher with Consumed status not found in the system.");
		}
		else{
		String toSerialNo = fromSerialNo;
		String vouchers = String.valueOf(Long.parseLong(toSerialNo) - Long.parseLong(fromSerialNo) + 1);
		String exp_date = nahp.addDaysToCurrentDate(_APIUtil.getCurrentTimeStamp(), 1);
		String date = new SimpleDateFormat(dateFormatGUI).format(new SimpleDateFormat("dd/MM/yy").parse(exp_date));
		String values1[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(fromSerialNo, "expiry_date","consume_before");
		
		vms.changeVoucherExpiryDate(date, fromSerialNo, toSerialNo, vouchers);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(fromSerialNo, "expiry_date","consume_before");
		
		Date edate=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String act_date1 = new SimpleDateFormat("dd/MM/yy").format(edate);
		
		Date edate1=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[1]);  
		String act_date2 = new SimpleDateFormat("dd/MM/yy").format(edate1);
		
		/*Date edate3=new SimpleDateFormat("yyyy/MM/dd").parse(date);
		String exp_date = new SimpleDateFormat("dd/MM/yy").format(edate3);*/
		
		Date edate4=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[0]);  
		String exp_ex_date1 = new SimpleDateFormat("dd/MM/yy").format(edate4);
		
		Date edate5=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[1]);  
		String exp_cu_date2 = new SimpleDateFormat("dd/MM/yy").format(edate5);
		
		Log.info("Expiry Date:");
		if(act_date1.equals(exp_date)){
			Assertion.assertFail("Expiry Date get changed for voucher ["+fromSerialNo+"] with status ["+PretupsI.CONSUMED+"]:"+act_date1);
		}else {Assertion.assertEquals(act_date1, exp_ex_date1);}
		
		Log.info("Consumed Before Date:");
		if(act_date2.equals(exp_date)){
			Assertion.assertFail("Consumed Before Date get changed for voucher ["+fromSerialNo+"] with status ["+PretupsI.CONSUMED+"]:"+act_date2);
		}else {Assertion.assertEquals(act_date2, exp_cu_date2);}
	
		Object[] msg = ExtentI.getMessageOnScreen();
		if(String.valueOf(msg[0]).equalsIgnoreCase("true")){
			Assertion.assertFail(String.valueOf(msg[1]));
			ExtentI.attachScreenShot();
		}}
		
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-2005")
	public void _04_VoucherExpiryDateChange() throws InterruptedException, ParseException {
		final String methodName = "_04_VoucherExpiryDateChange";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYCHANGE04");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory(assignCategory);
		NetworkAdminHomePage nahp  = new NetworkAdminHomePage(driver);
		
		String fromSerialNo = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.STOLEN);
		if(BTSLUtil.isNullString(fromSerialNo)){
			Assertion.assertSkip("Voucher with Stolen status not found in the system.");
		}
		else{
		String toSerialNo = fromSerialNo;
		String vouchers = String.valueOf(Long.parseLong(toSerialNo) - Long.parseLong(fromSerialNo) + 1);
		String exp_date = nahp.addDaysToCurrentDate(_APIUtil.getCurrentTimeStamp(), 1);
		String date = new SimpleDateFormat(dateFormatGUI).format(new SimpleDateFormat("dd/MM/yy").parse(exp_date));
		String values1[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(fromSerialNo, "expiry_date","consume_before");
		
		vms.changeVoucherExpiryDate(date, fromSerialNo, toSerialNo, vouchers);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(fromSerialNo, "expiry_date","consume_before");
		
		Date edate=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String act_date1 = new SimpleDateFormat("dd/MM/yy").format(edate);
		
		Date edate1=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[1]);  
		String act_date2 = new SimpleDateFormat("dd/MM/yy").format(edate1);
		
		/*Date edate3=new SimpleDateFormat("yyyy/MM/dd").parse(date);
		String exp_date = new SimpleDateFormat("dd/MM/yy").format(edate3);*/
		
		Date edate4=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[0]);  
		String exp_ex_date1 = new SimpleDateFormat("dd/MM/yy").format(edate4);
		
		Date edate5=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[1]);  
		String exp_cu_date2 = new SimpleDateFormat("dd/MM/yy").format(edate5);
		
		Log.info("Expiry Date:");
		if(act_date1.equals(exp_date)){
			Assertion.assertFail("Expiry Date get changed for voucher ["+fromSerialNo+"] with status ["+PretupsI.STOLEN+"]:"+act_date1);
		}else {Assertion.assertEquals(act_date1, exp_ex_date1);}
		
		Log.info("Consumed Before Date:");
		if(act_date2.equals(exp_date)){
			Assertion.assertFail("Consumed Before Date get changed for voucher ["+fromSerialNo+"] with status ["+PretupsI.STOLEN+"]:"+act_date2);
		}else {Assertion.assertEquals(act_date2, exp_cu_date2);}
		
		Object[] msg = ExtentI.getMessageOnScreen();
		if(String.valueOf(msg[0]).equalsIgnoreCase("true")){
			Assertion.assertFail(String.valueOf(msg[1]));
			ExtentI.attachScreenShot();
		}}
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-2006")
	public void _05_VoucherExpiryDateChange() throws InterruptedException, ParseException {
		final String methodName = "_05_VoucherExpiryDateChange";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYCHANGE05");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory(assignCategory);
		NetworkAdminHomePage nahp  = new NetworkAdminHomePage(driver);
		
		String fromSerialNo = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.ENABLE);
		if(BTSLUtil.isNullString(fromSerialNo)){
			Assertion.assertSkip("Voucher with Enabled status not found in the system.");
		}
		else{
		String toSerialNo = fromSerialNo;
		String vouchers = String.valueOf(Long.parseLong(toSerialNo) - Long.parseLong(fromSerialNo) + 1);
		String values1[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(fromSerialNo, "expiry_date","consume_before");
		Date exdate=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[0]);  
		String expiry_date = new SimpleDateFormat("yyyy/MM/dd").format(exdate);
		
		String exp_date = nahp.addDaysToCurrentDate(expiry_date, 1);
		String date = new SimpleDateFormat(dateFormatGUI).format(new SimpleDateFormat("dd/MM/yy").parse(exp_date));
		
		vms.changeVoucherExpiryDate(date, fromSerialNo, toSerialNo, vouchers);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(fromSerialNo, "expiry_date","consume_before");
		
		Date edate=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String act_date1 = new SimpleDateFormat("dd/MM/yy").format(edate);
		
		Date edate1=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[1]);  
		String act_date2 = new SimpleDateFormat("dd/MM/yy").format(edate1);
		
		/*Date edate3=new SimpleDateFormat("yyyy/MM/dd").parse(date);
		String exp_date = new SimpleDateFormat("dd/MM/yy").format(edate3);*/
		
		Log.info("Expiry Date:");
		Assertion.assertEquals(act_date1, exp_date);
		
		Log.info("Consumed Before Date:");
		Assertion.assertEquals(act_date2, exp_date);
		
		Object[] msg = ExtentI.getMessageOnScreen();
		if(String.valueOf(msg[0]).equalsIgnoreCase("true")){
			Assertion.assertFail(String.valueOf(msg[1]));
			ExtentI.attachScreenShot();
		}}
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-2007")
	public void _06_VoucherExpiryDateChange() throws InterruptedException, ParseException {
		final String methodName = "_06_VoucherExpiryDateChange";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYCHANGE06");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory(assignCategory);
		NetworkAdminHomePage nahp  = new NetworkAdminHomePage(driver);
		
		String fromSerialNo = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.ENABLE);
		if(BTSLUtil.isNullString(fromSerialNo)){
			Assertion.assertSkip("Voucher with Enabled status not found in the system.");
		}
		else{
		String toSerialNo = fromSerialNo;
		String vouchers = String.valueOf(Long.parseLong(toSerialNo) - Long.parseLong(fromSerialNo) + 1);
		String values1[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(fromSerialNo, "expiry_date","consume_before");
		Date exdate=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values1[0]);  
		String expiry_date = new SimpleDateFormat("yyyy/MM/dd").format(exdate);
		
		String exp_date = nahp.addDaysToCurrentDate(expiry_date, -1);
		String date = new SimpleDateFormat(dateFormatGUI).format(new SimpleDateFormat("dd/MM/yy").parse(exp_date));
		
		vms.changeVoucherExpiryDate(date, fromSerialNo, toSerialNo, vouchers);
		String values[] = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(fromSerialNo, "expiry_date","consume_before");
		
		Date edate=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[0]);  
		String act_date1 = new SimpleDateFormat("dd/MM/yy").format(edate);
		
		Date edate1=new SimpleDateFormat("yyyy-MM-dd 00:00:00.0").parse(values[1]);  
		String act_date2 = new SimpleDateFormat("dd/MM/yy").format(edate1);
		
		/*Date edate3=new SimpleDateFormat("yyyy/MM/dd").parse(date);
		String exp_date = new SimpleDateFormat("dd/MM/yy").format(edate3);*/
		
		Log.info("Expiry Date:");
		Assertion.assertEquals(act_date1, exp_date);
		
		Log.info("Consumed Before Date:");
		Assertion.assertEquals(act_date2, exp_date);
		
		Object[] msg = ExtentI.getMessageOnScreen();
		if(String.valueOf(msg[0]).equalsIgnoreCase("true")){
			Assertion.assertFail(String.valueOf(msg[1]));
			ExtentI.attachScreenShot();
		}}
	}
	
	
}
