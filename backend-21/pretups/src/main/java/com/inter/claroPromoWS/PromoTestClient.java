package com.inter.claroPromoWS;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.claroPromoWS.scheduler.NodeVO;
import com.inter.claroPromoWS.stub.AudiTypeReq;
import com.inter.claroPromoWS.stub.DataAdicionalReq;
import com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType;
import com.inter.claroPromoWS.stub.EntregaPromocionRequest;
import com.inter.claroPromoWS.stub.EntregaPromocionResponse;
import com.inter.claroPromoWS.stub.PromocionReq;
import com.btsl.util.Constants;



public class PromoTestClient {

	private EbsEntregaPromocionPortType  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public PromoTestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		PromoTestClient PromoTestClient = new PromoTestClient();
		PromoTestClient._action = "1";
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[2]);

			PromoTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroPromoWSConnectionManager serviceConnection = new ClaroPromoWSConnectionManager(nodeVO,"INTID00005");
			PromoTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+PromoTestClient._stub.toString());
			PromoTestClient._stubSuper = (Stub) PromoTestClient._stub;
			PromoTestClient._stub = (EbsEntregaPromocionPortType) PromoTestClient._stubSuper;

			EntregaPromocionRequest crearPagoRequest= null;
			crearPagoRequest=new EntregaPromocionRequest();
			DataAdicionalReq adicionalRequest2=new DataAdicionalReq();
			adicionalRequest2.setClave("");
			adicionalRequest2.setValor("");
			DataAdicionalReq[] adicionalRequest={adicionalRequest2};

			AudiTypeReq audiTypeRequest=new AudiTypeReq();
			audiTypeRequest.setIdTransaccion(getRequestID());
			audiTypeRequest.setIpAplicacion("172.16.7.229");
			audiTypeRequest.setUsuarioAplicacion("T14775");
			audiTypeRequest.setNombreAplicacion("PRETUPS");
			crearPagoRequest.setMsisdn(args[1]);
			crearPagoRequest.setAudit(audiTypeRequest);
			crearPagoRequest.setListaAdicionalReq(adicionalRequest);


			PromocionReq listPromocionesType=null;

			listPromocionesType=new PromocionReq();
			listPromocionesType.setCantidad("1500");
			listPromocionesType.setTipoPromocion("bolsaMinutosGratis");

			PromocionReq listPromocionesType1=null;

			listPromocionesType1=new PromocionReq();
			listPromocionesType1.setCantidad("1800");
			listPromocionesType1.setTipoPromocion("promo1020");

			
			PromocionReq[] listPromocionesType2={listPromocionesType,listPromocionesType1};

			crearPagoRequest.setListaPromocionesReq(listPromocionesType2);

			EntregaPromocionResponse recargasResponse=null;

			System.out.println("startTime = "+Time());

			if(PromoTestClient._action.equals("1"))
			{
				recargasResponse = PromoTestClient._stub.entregarPromocion(crearPagoRequest);
			}
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("PromoTestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("PromoTestClient Exception="+e.getMessage());
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
