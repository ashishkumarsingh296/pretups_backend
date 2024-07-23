package com.inter.righttel.crmSOAP;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.btsl.util.Constants;
import com.inter.righttel.crmSOAP.scheduler.NodeVO;
import com.inter.righttel.crmSOAP.stub.InitTopupRequestType;
import com.inter.righttel.crmSOAP.stub.InitTopupResponseType;
import com.inter.righttel.crmSOAP.stub.MSISDN;
import com.inter.righttel.crmSOAP.stub.SourceType;
import com.inter.righttel.crmSOAP.stub.SubscriberType;
import com.inter.righttel.crmSOAP.stub.TopupServicePortType;




public class RechargeTestClient {

	private TopupServicePortType  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public RechargeTestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		RechargeTestClient RechargeTestClient = new RechargeTestClient();
		RechargeTestClient._action = "1";
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[1]);

			RechargeTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://127.0.0.1:8088/mockTopupServiceSoap11Binding");

			nodeVO.setTopReadTimeOut(10000);
			CRMWebServiceConnectionManager serviceConnection = new CRMWebServiceConnectionManager(nodeVO,"");
			RechargeTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+RechargeTestClient._stub.toString());
			RechargeTestClient._stubSuper = (Stub) RechargeTestClient._stub;
			RechargeTestClient._stub = (TopupServicePortType) RechargeTestClient._stubSuper;


			InitTopupRequestType initTopupRequest= null;
			initTopupRequest=new InitTopupRequestType();
			initTopupRequest.setAmount(Long.parseLong("10"));
			SubscriberType subscriber=new SubscriberType();
			MSISDN msisdn=new MSISDN("98188888888");
			subscriber.setMsisdn(msisdn);
			initTopupRequest.setSubscriber(subscriber);
			SourceType source=new SourceType();
			source.setDistributorId("2");
			initTopupRequest.setSource(source);
			initTopupRequest.setReferenceId("2");
			InitTopupResponseType recargasResponse=null;

			System.out.println("startTime = "+Time());

			if(RechargeTestClient._action.equals("1"))
			{

				recargasResponse = RechargeTestClient._stub.initTopup(initTopupRequest);

			}
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("RechargeTestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("RechargeTestClient Exception="+e.getMessage());
			e.printStackTrace();
		}
	}

	private static String getRequestID() 
	{
		String reqId="";
		String counter="";
		String dateStrReqId = null;
		try{
			java.util.Date mydate = new java.util.Date();
			SimpleDateFormat sdfReqId = new SimpleDateFormat ("yyMMddHHss");
			dateStrReqId = sdfReqId.format(mydate);
			reqId = "003"+dateStrReqId+counter;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return reqId;
	}

	public void loadInputs() throws Exception
	{
		try
		{
			_msisdn = "01199700587";
			_amount="1000";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	public static String Time() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cal.getTime());

	}


}
