	package restassuredapi.test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.commons.MasterI;
import com.utils._masterVO;

import restassuredapi.api.vouchercardgroupAPI.ViewVoucherCardGroupApi;
import restassuredapi.pojo.viewvouchercardgrouprequestpojo.Data;
import restassuredapi.pojo.viewvouchercardgrouprequestpojo.ViewVoucherCardGroupRequestPojo;
public class ViewVoucherCardGroup {	
	ViewVoucherCardGroupRequestPojo viewVoucherCardGroupRequestPojo= new ViewVoucherCardGroupRequestPojo();
	Data data = new Data();
	
	@BeforeMethod(alwaysRun = true)
    public void setupData() {
		viewVoucherCardGroupRequestPojo.setIdentifierType("ydist");
		viewVoucherCardGroupRequestPojo.setIdentifierValue("1357");
		viewVoucherCardGroupRequestPojo.setIdentifierType("btnadm");
		viewVoucherCardGroupRequestPojo.setIdentifierValue("1357");
		viewVoucherCardGroupRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		viewVoucherCardGroupRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		viewVoucherCardGroupRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		viewVoucherCardGroupRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		viewVoucherCardGroupRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		viewVoucherCardGroupRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));
		data.setServiceTypeDesc("Voucher Consumption");
		data.setSubServiceTypeDescription("CVG");
		data.setCardGroupSetName("AUT17FL3N");
		data.setModuleCode("P2P");
		data.setNetworkCode("NG");
		data.setVersion("1");
		viewVoucherCardGroupRequestPojo.setData(data);

	}
	
	@Test
	public void A_01_Test_BeforeSuiteview() throws Exception
	{
		ViewVoucherCardGroupApi viewVoucherCardGroupApi=new ViewVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		viewVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
			viewVoucherCardGroupApi.addBodyParam(viewVoucherCardGroupRequestPojo);
		viewVoucherCardGroupApi.setExpectedStatusCode(200);
		viewVoucherCardGroupApi.perform();
	}
		
}
