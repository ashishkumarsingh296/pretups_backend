package restassuredapi.test;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.commons.MasterI;
import com.utils._masterVO;

import restassuredapi.api.vouchercardgroupAPI.VoucherCardGroupChangeStatusApi;
import restassuredapi.pojo.vouchercardgroupchangestatusrequestpojo.CardGroupSetList;
import restassuredapi.pojo.vouchercardgroupchangestatusrequestpojo.Data;
import restassuredapi.pojo.vouchercardgroupchangestatusrequestpojo.VoucherCardGroupChangeStatusRequestPojo;


public class VoucherCardGroupChangeStatus {	
	VoucherCardGroupChangeStatusRequestPojo voucherCardGroupChangeStatusRequestPojo= new VoucherCardGroupChangeStatusRequestPojo();
	Data data = new Data();
	CardGroupSetList cardGroupSetList = new CardGroupSetList();
	List<CardGroupSetList> cardGroupSetList1 = new ArrayList<CardGroupSetList>();
	
	@BeforeMethod(alwaysRun = true)
    public void setupData() {
		data.setModuleCode("P2P");
		data.setNetworkCode("NG");
		
		voucherCardGroupChangeStatusRequestPojo.setIdentifierType("btnadm");
		voucherCardGroupChangeStatusRequestPojo.setIdentifierValue("1357");
	
		voucherCardGroupChangeStatusRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		voucherCardGroupChangeStatusRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		voucherCardGroupChangeStatusRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		voucherCardGroupChangeStatusRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		voucherCardGroupChangeStatusRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		voucherCardGroupChangeStatusRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));
		
		
		int size = 0;
		for(int i=0;i<size;i++)
		{			
			cardGroupSetList.setCardGroupSetName("AUT2MK2jH");
			cardGroupSetList.setServiceTypeDesc("Voucher Consumption");
			
			cardGroupSetList1.add(cardGroupSetList);
		}
		
		data.setCardGroupSetList(cardGroupSetList1);
		
		
		voucherCardGroupChangeStatusRequestPojo.setData(data);
	}
	
	
	
	
	@Test
	public void A_01_Test_BeforeSuiteview() throws Exception
	{
		VoucherCardGroupChangeStatusApi voucherCardGroupChangeStatusApi=new VoucherCardGroupChangeStatusApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		voucherCardGroupChangeStatusApi.setContentType(_masterVO.getProperty("contentType"));
		voucherCardGroupChangeStatusApi.addBodyParam(voucherCardGroupChangeStatusRequestPojo);
		voucherCardGroupChangeStatusApi.setExpectedStatusCode(200);
		voucherCardGroupChangeStatusApi.perform();
		
		
	}
		
}
