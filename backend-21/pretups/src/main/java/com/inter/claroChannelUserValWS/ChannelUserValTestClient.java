package com.inter.claroChannelUserValWS;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.claroChannelUserValWS.scheduler.NodeVO;
import com.inter.claroChannelUserValWS.stub.AudiTypeRequest;
import com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortType;
import com.inter.claroChannelUserValWS.stub.EvaluaPedidoRequest;
import com.inter.claroChannelUserValWS.stub.EvaluaPedidoResponse;
import com.inter.claroChannelUserValWS.stub.RequestOpcionalComplexType;
import com.btsl.util.Constants;





public class ChannelUserValTestClient {

	private EbsEvaluaPedidoSaldoPortType  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public ChannelUserValTestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		ChannelUserValTestClient ChannelUserValTestClient = new ChannelUserValTestClient();
		ChannelUserValTestClient._action = "1";
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[3]);
			ChannelUserValTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroCUValWSConnectionManager serviceConnection = new ClaroCUValWSConnectionManager(nodeVO,"INTID00005");
			ChannelUserValTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+ChannelUserValTestClient._stub.toString());
			ChannelUserValTestClient._stubSuper = (Stub) ChannelUserValTestClient._stub;
			ChannelUserValTestClient._stub = (EbsEvaluaPedidoSaldoPortType) ChannelUserValTestClient._stubSuper;

			EvaluaPedidoRequest consultarEvaluacionCrediticiaPedidosRequest= null;
			consultarEvaluacionCrediticiaPedidosRequest=new EvaluaPedidoRequest();

			consultarEvaluacionCrediticiaPedidosRequest=new EvaluaPedidoRequest();
			RequestOpcionalComplexType _adicionalRequest=new RequestOpcionalComplexType();
			_adicionalRequest.setClave("");
			_adicionalRequest.setValor("");
			RequestOpcionalComplexType[] _adicionalRequests={_adicionalRequest};
			
			AudiTypeRequest audiTypeRequest=new AudiTypeRequest();
			audiTypeRequest.setIdTransaccion(getRequestID());
			audiTypeRequest.setIpAplicacion("172.16.7.229");
			audiTypeRequest.setNombreAplicacion("PRETUPS");
			audiTypeRequest.setUsuarioAplicacion("USR_PRETUPS");
			consultarEvaluacionCrediticiaPedidosRequest.setCodigoDAC(args[1]);
			consultarEvaluacionCrediticiaPedidosRequest.setMontoPedido(args[2]);
			consultarEvaluacionCrediticiaPedidosRequest.setAudit(audiTypeRequest);
			consultarEvaluacionCrediticiaPedidosRequest.setListaOpcionalRequest(_adicionalRequests);


			EvaluaPedidoResponse recargasResponse=null;

			System.out.println("startTime = "+Time());

			if(ChannelUserValTestClient._action.equals("1"))
			{
				recargasResponse = ChannelUserValTestClient._stub.evaluarPedidoSaldo(consultarEvaluacionCrediticiaPedidosRequest);
			}
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("ChannelUserValTestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("ChannelUserValTestClient Exception="+e.getMessage());
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
