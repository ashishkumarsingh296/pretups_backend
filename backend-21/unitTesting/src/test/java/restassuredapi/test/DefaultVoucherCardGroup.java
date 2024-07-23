package restassuredapi.test;

import java.util.HashMap;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.Features.P2PCardGroup;
import com.commons.MasterI;
import com.utils.InitializeBrowser;
import com.utils._masterVO;

import restassuredapi.api.vouchercardgroupAPI.DefaultVoucherCardGroupApi;
import restassuredapi.pojo.defaultvouchercardgrouprequestpojo.Data;
import restassuredapi.pojo.defaultvouchercardgrouprequestpojo.DefaultVoucherCardGroupRequestPojo;
import restassuredapi.pojo.defaultvouchercardgroupresponsepojo.DefaultVoucherCardgroupResponsePojo;


public class DefaultVoucherCardGroup {
	DefaultVoucherCardGroupRequestPojo defaultVoucherCardGroupRequestPojo= new DefaultVoucherCardGroupRequestPojo();
	Data data = new Data();
	DefaultVoucherCardgroupResponsePojo defaultVoucherCardgroupResponsePojo=new DefaultVoucherCardgroupResponsePojo();
	HashMap<String, String> dataMap;
	public static WebDriver driver;
	

	@BeforeMethod(alwaysRun = true)
    public void setupData() throws Exception {
		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);
		P2PCardGroup.P2PCardGroupStatus(dataMap.get("CARDGROUPNAME"));
		defaultVoucherCardGroupRequestPojo.setIdentifierType("btnadm");
		defaultVoucherCardGroupRequestPojo.setIdentifierValue("1357");
		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreation("VCN", "CVG");
		data.setNetworkCode("NG");
		data.setUserId("SYSTEM");
		data.setServiceTypeId("VCN");
		data.setSubServiceTypeId("1");
		data.setCardGroupSetId(dataMap.get("CARDGROUP_SETID"));
		data.setModuleCode("P2P");
		defaultVoucherCardGroupRequestPojo.setData(data);
		
	}
	
	@Test
	public void A_01_Test_defaultVoucherCardGroup_postive() throws Exception
	{
		DefaultVoucherCardGroupApi defaultVoucherCardGroupApi=new DefaultVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		defaultVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		defaultVoucherCardGroupApi.addBodyParam(defaultVoucherCardGroupRequestPojo);
		defaultVoucherCardGroupApi.setExpectedStatusCode(200);
		defaultVoucherCardGroupApi.perform();
		defaultVoucherCardgroupResponsePojo =defaultVoucherCardGroupApi.getAPIResponseAsPOJO(DefaultVoucherCardgroupResponsePojo.class);
		int statusCode =defaultVoucherCardgroupResponsePojo.getStatusCode();
		Assert.assertEquals(200,statusCode);
		
	}
	//ALREDAY DEFAULT CARDGROUP CANT BE SET AS DEFAULT
	@Test
	public void A_02_Test_defaultVoucherCardGroupnegativeAlreadydefault() throws Exception
	{
		DefaultVoucherCardGroupApi defaultVoucherCardGroupApi=new DefaultVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		defaultVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		
		defaultVoucherCardGroupApi.addBodyParam(defaultVoucherCardGroupRequestPojo);
		defaultVoucherCardGroupApi.setExpectedStatusCode(200);
		defaultVoucherCardGroupApi.perform();
		defaultVoucherCardgroupResponsePojo =defaultVoucherCardGroupApi.getAPIResponseAsPOJO(DefaultVoucherCardgroupResponsePojo.class);
		int statusCode =defaultVoucherCardgroupResponsePojo.getStatusCode();
		Assert.assertEquals(400,statusCode);
		
	}
// A CARD GROUP CANT BE SET AS DEFAULT WHEN ITS CURENT VERSION APPLICABLE DATE IS GRETER FROM TODAYS DATE
	@Test
	public void A_03_Test_defaultVoucherCardGroupnegativeApplicablefrom() throws Exception
	{
		DefaultVoucherCardGroupApi defaultVoucherCardGroupApi=new DefaultVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		defaultVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		data.setCardGroupSetId("3083");
		defaultVoucherCardGroupRequestPojo.setData(data);
		
		defaultVoucherCardGroupApi.addBodyParam(defaultVoucherCardGroupRequestPojo);
		defaultVoucherCardGroupApi.setExpectedStatusCode(200);
		defaultVoucherCardGroupApi.perform();
		defaultVoucherCardgroupResponsePojo =defaultVoucherCardGroupApi.getAPIResponseAsPOJO(DefaultVoucherCardgroupResponsePojo.class);
		int statusCode =defaultVoucherCardgroupResponsePojo.getStatusCode();
		Assert.assertEquals(400,statusCode);
		
	}
	// CARD GROUP WITH SUSPENDED SATUS CODE cannot be set as default 
	@Test
	public void A_04_Test_defaultVoucherCardGroupNegativeSuspended() throws Exception
	{
		DefaultVoucherCardGroupApi defaultVoucherCardGroupApi=new DefaultVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		defaultVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		data.setCardGroupSetId(dataMap.get("CARDGROUP_SETID"));
		defaultVoucherCardGroupRequestPojo.setData(data);
		
		
		defaultVoucherCardGroupApi.addBodyParam(defaultVoucherCardGroupRequestPojo);
		defaultVoucherCardGroupApi.setExpectedStatusCode(200);
		defaultVoucherCardGroupApi.perform();
		defaultVoucherCardgroupResponsePojo =defaultVoucherCardGroupApi.getAPIResponseAsPOJO(DefaultVoucherCardgroupResponsePojo.class);
		int statusCode =defaultVoucherCardgroupResponsePojo.getStatusCode();
		Assert.assertEquals(400,statusCode);
		
	}


}

