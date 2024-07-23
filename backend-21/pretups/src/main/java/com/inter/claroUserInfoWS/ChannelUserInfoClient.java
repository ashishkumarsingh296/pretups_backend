package com.inter.claroUserInfoWS;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.claroUserInfoWS.scheduler.NodeVO;
import com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType;
import com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType;
import com.inter.claroUserInfoWS.stub.StreetSellerWSSoap_PortType;
import com.btsl.util.Constants;





public class ChannelUserInfoClient {

	private StreetSellerWSSoap_PortType  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public ChannelUserInfoClient(){
		super();
	}

	public static void main(String[] args) 
	{
		ChannelUserInfoClient ChannelUserValTestClient = new ChannelUserInfoClient();
		ChannelUserValTestClient._action = "1";
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[2]);
			
			ChannelUserValTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroCUInfoWSConnectionManager serviceConnection = new ClaroCUInfoWSConnectionManager(nodeVO,"INTID00005");
			ChannelUserValTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+ChannelUserValTestClient._stub.toString());
			ChannelUserValTestClient._stubSuper = (Stub) ChannelUserValTestClient._stub;
			ChannelUserValTestClient._stub = (StreetSellerWSSoap_PortType) ChannelUserValTestClient._stubSuper;
			
			
			DistribuidorDataRequestType distribuidorDataRequestType=new DistribuidorDataRequestType();
			distribuidorDataRequestType.setCodigo(args[1]);
			
			DistribuidorDataResponseType recargasResponse=null;

			System.out.println("startTime = "+Time());

			if(ChannelUserValTestClient._action.equals("1"))
			{
				recargasResponse = ChannelUserValTestClient._stub.obtenerDatosDistribuidor(distribuidorDataRequestType);
				
				HashMap hashMap=new HashMap();
				hashMap.put("RESPONSE_OBJECT", recargasResponse);
				new ClaroCUInfoWSResponseParser().parseCUInfoResponse(hashMap);
			}
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("ChannelUserInfoClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("ChannelUserInfoClient Exception="+e.getMessage());
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
