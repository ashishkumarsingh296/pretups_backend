package com.inter.claroPINRechargeWS;


import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.claroPINRechargeWS.scheduler.NodeVO;
import com.inter.claroPINRechargeWS.stub.EbsPinVirtual;
import com.inter.claroPINRechargeWS.stub.RecargaPinVirtualRequest;
import com.inter.claroPINRechargeWS.stub.RecargaPinVirtualResponse;
import com.btsl.util.Constants;





public class PinTestClient {

	private EbsPinVirtual  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public PinTestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		PinTestClient PinTestClient = new PinTestClient();
		PinTestClient._action = "1";
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[3]);
			
			PinTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroPINWSConnectionManager serviceConnection = new ClaroPINWSConnectionManager(nodeVO,"INTID00005");
			PinTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+PinTestClient._stub.toString());
			PinTestClient._stubSuper = (Stub) PinTestClient._stub;
			PinTestClient._stub = (EbsPinVirtual) PinTestClient._stubSuper;
			RecargaPinVirtualRequest recargarPINPrepagoRequest= null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
			SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss",Locale.ENGLISH);


			recargarPINPrepagoRequest=new RecargaPinVirtualRequest();
			recargarPINPrepagoRequest.setIdTransaccion(getRequestID());
			recargarPINPrepagoRequest.setMsisdn(args[1]);
			recargarPINPrepagoRequest.setMonto(args[2]);
			recargarPINPrepagoRequest.setNombreAplicacion("EAI");
			recargarPINPrepagoRequest.setIpAplicacion("172.16.7.229");
			recargarPINPrepagoRequest.setFecha(sdf.format(new Date()));
			recargarPINPrepagoRequest.setHora(sdf1.format(new Date()));
			recargarPINPrepagoRequest.setTipo("1");
			recargarPINPrepagoRequest.setBinadquiriente("520200");
			recargarPINPrepagoRequest.setForwardinst("520200");

			RecargaPinVirtualResponse recargasResponse=null;

			System.out.println("startTime = "+Time());

			if(PinTestClient._action.equals("1"))
			{
			
				recargasResponse = PinTestClient._stub.recargaPinVirtual(recargarPINPrepagoRequest);
					
			}
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("PinTestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("PinTestClient Exception="+e.getMessage());
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
