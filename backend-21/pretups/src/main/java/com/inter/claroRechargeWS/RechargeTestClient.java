package com.inter.claroRechargeWS;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.claroRechargeWS.scheduler.NodeVO;
import com.inter.claroRechargeWS.stub.AudiTypeRequest;
import com.inter.claroRechargeWS.stub.EbsRecargaVirtualPortType;
import com.inter.claroRechargeWS.stub.EjecutarRecargaRequest;
import com.inter.claroRechargeWS.stub.EjecutarRecargaResponse;
import com.inter.claroRechargeWS.stub.RequestOpcionalComplexType;
import com.btsl.util.Constants;



public class RechargeTestClient {

	private EbsRecargaVirtualPortType  _stub=null;
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
			org.apache.log4j.PropertyConfigurator.configure(args[3]);

			RechargeTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroWebServiceConnectionManager serviceConnection = new ClaroWebServiceConnectionManager(nodeVO,"INTID00005");
			RechargeTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+RechargeTestClient._stub.toString());
			RechargeTestClient._stubSuper = (Stub) RechargeTestClient._stub;
			RechargeTestClient._stub = (EbsRecargaVirtualPortType) RechargeTestClient._stubSuper;

			EjecutarRecargaRequest recargasRequest= null;
			AudiTypeRequest autenticacionType=new AudiTypeRequest();
			autenticacionType.setIpAplicacion("172.16.7.229");
			autenticacionType.setUsuarioAplicacion("EAI");
			autenticacionType.setIdTransaccion(getRequestID());
			autenticacionType.setNombreAplicacion("EAI");
			recargasRequest=new EjecutarRecargaRequest();
			recargasRequest.setMsisdn(args[1]);
			recargasRequest.setMontoRecarga(args[2]);
			recargasRequest.setAuditRequest(autenticacionType);
			RequestOpcionalComplexType complexType=new RequestOpcionalComplexType();
			complexType.setClave("");
			complexType.setValor("");
			RequestOpcionalComplexType[] opcionalComplexTypes={complexType};
			recargasRequest.setListaAdicional(opcionalComplexTypes);
			EjecutarRecargaResponse recargasResponse=null;

			System.out.println("startTime = "+Time());

			if(RechargeTestClient._action.equals("1"))
			{

				recargasResponse = RechargeTestClient._stub.ejecutarRecarga(recargasRequest);

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