package com.inter.claroCollPayWS;


import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.claroCollPayWS.scheduler.NodeVO;
import com.inter.claroCollPayWS.stub.CrearPago;
import com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoReqType;
import com.inter.claroCollPayWS.stub.CrearPagoDetServicioReqType;
import com.inter.claroCollPayWS.stub.CrearPagoResponse;
import com.inter.claroCollPayWS.stub.TransaccionPagos_PortType;
import com.btsl.util.Constants;




public class CollPayTestClient
{

	private TransaccionPagos_PortType  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public CollPayTestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		CollPayTestClient CollPayTestClient = new CollPayTestClient();
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[6]);

			CollPayTestClient._action=args[1];
			CollPayTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroCollPayWSConnectionManager serviceConnection = new ClaroCollPayWSConnectionManager(nodeVO,"INTID00005");
			CollPayTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+CollPayTestClient._stub.toString());
			CollPayTestClient._stubSuper = (Stub) CollPayTestClient._stub;
			CollPayTestClient._stub = (TransaccionPagos_PortType) CollPayTestClient._stubSuper;

			System.out.println("startTime = "+Time());

			if(CollPayTestClient._action.equals("2"))
			{

				CrearPagoResponse recargasResponse=null;
				
				CrearPago crearPagoRequest1= null;

				Calendar cal = Calendar.getInstance();

				crearPagoRequest1=new CrearPago();
				crearPagoRequest1.setTxId(getRequestID());
				crearPagoRequest1.setpCodAplicacion("ST");
				crearPagoRequest1.setpExtorno("0");//Need to add
				crearPagoRequest1.setpCodBanco("624300");
				crearPagoRequest1.setpCodReenvia("624300");
				crearPagoRequest1.setpCodMoneda("604");
				crearPagoRequest1.setpTipoIdentific("01");
				crearPagoRequest1.setpDatoIdentific(args[2]);//Mobile Numbre
				crearPagoRequest1.setpFechaHora(cal);
				crearPagoRequest1.setpTrace("123457");//Need to add
				crearPagoRequest1.setpNroOperacion("000083096670");//Need to add
				crearPagoRequest1.setpCodAgencia("AG01");
				crearPagoRequest1.setpCodCanal("15");
				crearPagoRequest1.setpCodCiudad("CIU");
				crearPagoRequest1.setpNroTerminal("TERM01");
				crearPagoRequest1.setpPlaza("PL02");
				crearPagoRequest1.setpMedioPago("00");//Need to add
				crearPagoRequest1.setpNroReferencia("77888888");
				crearPagoRequest1.setpPagoEfectivo(new BigDecimal(args[3]));//Amount
				crearPagoRequest1.setpPagoTotal(new BigDecimal(args[3]));//Amount
				crearPagoRequest1.setpDatoTransaccion("");//Need to add
				crearPagoRequest1.setpNombreComercio("");
				crearPagoRequest1.setpNroComercio("");
				crearPagoRequest1.setpNroCheque1("");
				crearPagoRequest1.setpNroCheque2("");
				crearPagoRequest1.setpNroCheque3("");
				crearPagoRequest1.setpPlazaBcoCheque1("");
				crearPagoRequest1.setpPlazaBcoCheque2("");
				crearPagoRequest1.setpPlazaBcoCheque3("");
				crearPagoRequest1.setpBcoGiradCheque1("");
				crearPagoRequest1.setpBcoGiradCheque2("");
				crearPagoRequest1.setpBcoGiradCheque3("");

				CrearPagoDetServicioReqType crearPagoDetServicioReqType=new CrearPagoDetServicioReqType();
	
				CrearPagoDetDocumentoReqType crearPagoDetDocumentoReqType=new CrearPagoDetDocumentoReqType();
				crearPagoDetDocumentoReqType.setpTipoServicio(args[4]);
				crearPagoDetDocumentoReqType.setpNumeroDoc(args[5]);//Invoice Number
				crearPagoDetDocumentoReqType.setpMontoOrigDeuda(new BigDecimal(args[3]));//Need to add
				crearPagoDetDocumentoReqType.setpMontoPagado(new BigDecimal(args[3]));//Need to add
				crearPagoDetDocumentoReqType.setpCodConcepto5("");//Need to add

				crearPagoDetDocumentoReqType.setpPeriodoCotizacion("");
				crearPagoDetDocumentoReqType.setpCodConcepto1("");
				crearPagoDetDocumentoReqType.setpCodConcepto2("");
				crearPagoDetDocumentoReqType.setpCodConcepto3("");
				crearPagoDetDocumentoReqType.setpCodConcepto4("");
				crearPagoDetDocumentoReqType.setpCodConcepto5("");
				crearPagoDetDocumentoReqType.setpDatoDocumento("");
				CrearPagoDetDocumentoReqType[] crearPagoDetDocumentoReqType2={crearPagoDetDocumentoReqType};//Need to add
				crearPagoDetServicioReqType.setpDetalleDocs(crearPagoDetDocumentoReqType2);//Need to add
				crearPagoDetServicioReqType.setpEstadoDeudor("A");//Need to add
				crearPagoDetServicioReqType.setpCodTipoServicio(args[4]);//Service Code
				crearPagoDetServicioReqType.setpNroDocs(new BigDecimal("1"));//Invoice Number
				crearPagoDetServicioReqType.setpMontoPagado(new BigDecimal(args[3]));//Need to add
				crearPagoDetServicioReqType.setpDatoServicio("");
				CrearPagoDetServicioReqType[] crearPagoDetServicioReqType2={crearPagoDetServicioReqType};
				crearPagoRequest1.setpDetDocumentos(crearPagoDetServicioReqType2);
				recargasResponse = CollPayTestClient._stub.crearPago(crearPagoRequest1);
			}
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("CollPayTestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("CollPayTestClient Exception="+e.getMessage());
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
