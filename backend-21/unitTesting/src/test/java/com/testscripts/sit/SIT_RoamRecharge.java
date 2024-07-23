package com.testscripts.sit;

import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.Features.C2STransfer;
import com.Features.ChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.ChannelUserMap;
import com.Features.mapclasses.CustomerRcTransferMap;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_Roam_Recharge)
public class SIT_RoamRecharge extends BaseTest {

	static boolean TestCaseCounter = false;
	C2STransfer c2STransfer;
	RandomGeneration randstr;
	GenerateMSISDN gnMsisdn;
	ChannelUserMap chnlUsrMap;
	ChannelUser chnlUsr;
	HashMap<String, String> paraMap;
	CustomerRcTransferMap c2strfMap1;
	HashMap<String, String> c2strfMap;
	TransferControlProfile trfCntrlProf;
	AddChannelUserDetailsPage getMessage;
	static String networkCode;
	static Object[][] data;
	String type;
	_parser parser;
	@BeforeMethod
	public void dataC2S(){
		c2STransfer = new C2STransfer(driver);
		randstr = new RandomGeneration();
		gnMsisdn = new GenerateMSISDN();
		chnlUsrMap = new ChannelUserMap();
		c2strfMap1 = new CustomerRcTransferMap();
		c2strfMap = c2strfMap1.getC2SMap();
		chnlUsr = new ChannelUser(driver);
		parser = new _parser(); 
		getMessage = new AddChannelUserDetailsPage(driver);
		trfCntrlProf = new TransferControlProfile(driver);
		paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);
		networkCode = _masterVO.getMasterValue("Network Code");
		type = "CHANNEL";
		data = DBHandler.AccessHandler.getProductDetails(networkCode, c2strfMap.get("domainCode"), c2strfMap.get("fromCategoryCode"), c2strfMap.get("toCategoryCode"),type);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-528") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void RoamRecharge()throws Exception {
		final String methodName = "Test_RoamRecharge";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITROAMRECHARGE1");
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "65";
		String prefix = _masterVO.getMasterValue("Roam Recharge Prefix");
		String subsmsisdn=prefix+randstr.randomNumeric(gnMsisdn.generateMSISDN());
		String sysNetworkCode = _masterVO.getMasterValue("Network Code");
		HashMap<String, String> detailMap = DBHandler.AccessHandler.getNetworkPrefixDetails(prefix, "PRE");
		Log.info("<pre><b>Sender Network Code:</b> "+sysNetworkCode+"<br><b>Receiver Netowrk Code:</b> "+detailMap.get("network_code")+"</pre>");
		
		if(!detailMap.get("network_code").equalsIgnoreCase(sysNetworkCode)){
		c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),amount,subsmsisdn);}
		else {
			Assertion.assertSkip("<pre>The provided Roam Recharge Prefix ["+prefix+"] exist in same network ["+sysNetworkCode+"] and hence, roam recharge is not applicable for this prefix.</pre>" );
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}

	
