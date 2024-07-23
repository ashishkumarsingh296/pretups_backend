package com.inter.claroDataRechargeWS;


import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.claroDataRechargeWS.scheduler.NodeVO;
import com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatos;
import com.inter.claroDataRechargeWS.stub.EjecutarRecargaDatosRequest;
import com.inter.claroDataRechargeWS.stub.EjecutarRecargaDatosResponse;
import com.btsl.util.Constants;





public class DataTestClient {

	private EbsRecargaPaqueteDatos  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public DataTestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		DataTestClient DataTestClient = new DataTestClient();
		DataTestClient._action = "1";
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[4]);
			DataTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroDataWSConnectionManager serviceConnection = new ClaroDataWSConnectionManager(nodeVO,"INTID00005");
			DataTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+DataTestClient._stub.toString());
			DataTestClient._stubSuper = (Stub) DataTestClient._stub;
			DataTestClient._stub = (EbsRecargaPaqueteDatos) DataTestClient._stubSuper;
			EjecutarRecargaDatosRequest recargarDataPrepagoRequest= null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
			SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss",Locale.ENGLISH);

			recargarDataPrepagoRequest=new EjecutarRecargaDatosRequest();
			recargarDataPrepagoRequest.setIdTransaccion(getRequestID());
			recargarDataPrepagoRequest.setIpAplicacion("localhost");
			recargarDataPrepagoRequest.setNombreAplicacion("ST");
			recargarDataPrepagoRequest.setFechaTX(sdf.format(new Date()));
			recargarDataPrepagoRequest.setHoraTX(sdf1.format(new Date()));
			recargarDataPrepagoRequest.setBinAdquiriente("52000");
			recargarDataPrepagoRequest.setForwardInstitution("52000");
			recargarDataPrepagoRequest.setProducto(args[3]);
			recargarDataPrepagoRequest.setTelefono(args[1]);
			recargarDataPrepagoRequest.setMonto(args[2]);

			EjecutarRecargaDatosResponse recargasResponse=null;

			System.out.println("startTime = "+Time());

			if(DataTestClient._action.equals("1"))
			{
				recargasResponse = DataTestClient._stub.ejecutarRecargaDatos(recargarDataPrepagoRequest);
			}
			System.out.println("End Time = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("DataTestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("DataTestClient Exception="+e.getMessage());
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
