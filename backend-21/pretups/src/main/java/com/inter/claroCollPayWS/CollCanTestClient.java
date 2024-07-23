package com.inter.claroCollPayWS;


import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.claroCollPayWS.scheduler.NodeVO;
import com.inter.claroCollPayWS.stub.CrearAnulacion;
import com.inter.claroCollPayWS.stub.CrearAnulacionResponse;
import com.inter.claroCollPayWS.stub.TransaccionPagos_PortType;
import com.btsl.util.Constants;




public class CollCanTestClient
{

	private TransaccionPagos_PortType  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public CollCanTestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		CollCanTestClient CollCanTestClient = new CollCanTestClient();
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[5]);

			CollCanTestClient._action=args[1];
			CollCanTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroCollPayWSConnectionManager serviceConnection = new ClaroCollPayWSConnectionManager(nodeVO,"INTID00005");
			CollCanTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+CollCanTestClient._stub.toString());
			CollCanTestClient._stubSuper = (Stub) CollCanTestClient._stub;
			CollCanTestClient._stub = (TransaccionPagos_PortType) CollCanTestClient._stubSuper;

			System.out.println("startTime = "+Time());

			if(CollCanTestClient._action.equals("1"))
			{
				CrearAnulacionResponse recargasResponse=null;
				

				CrearAnulacion crearPagoRequest= null;
				Calendar cal = Calendar.getInstance();

				crearPagoRequest=new CrearAnulacion();
				crearPagoRequest.setTxId(getRequestID());
				crearPagoRequest.setpCodAplicacion("ST");
				crearPagoRequest.setpExtorno("0");//Need to add
				crearPagoRequest.setpCodBanco("624300");
				crearPagoRequest.setpCodReenvia("624300");
				crearPagoRequest.setpCodMoneda("604");
				crearPagoRequest.setpTipoIdentific("01");
				crearPagoRequest.setpDatoidentific(args[2]);//Mobile Numbre
				crearPagoRequest.setpFechaHora(cal);
				crearPagoRequest.setpTrace("123456");//Need to add
				crearPagoRequest.setpNroOperacion("000083096669");//Need to add
				crearPagoRequest.setpCodAgencia("AG01");
				crearPagoRequest.setpCodCanal("15");
				crearPagoRequest.setpNombreComercio("");
				crearPagoRequest.setpNroComercio("");
				crearPagoRequest.setpCodCiudad("");
				crearPagoRequest.setpPlaza("");
				crearPagoRequest.setpNroReferencia("");
				crearPagoRequest.setpTraceOrig("");
				crearPagoRequest.setpNroTerminal("TERM01");//Need to add
				crearPagoRequest.setpImportePago(new BigDecimal(args[3]));//Amount
				crearPagoRequest.setpCodBancoOrig("624300");//Need to add
				crearPagoRequest.setpCodReenviaOrig("624300");//Need to add
				crearPagoRequest.setpFechaHoraOrig(cal);
				crearPagoRequest.setpNroOperCobrOrig("000083096669");//Need to add
				crearPagoRequest.setpNroOperAcreOrig("000083096669");//Need to add
				crearPagoRequest.setpCodTipoServicio(args[4]);//Sub Service Code
				recargasResponse = CollCanTestClient._stub.crearAnulacion(crearPagoRequest);
					
			}
			
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("CollCanTestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("CollCanTestClient Exception="+e.getMessage());
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
