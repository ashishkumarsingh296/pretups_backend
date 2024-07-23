package com.inter.claroDTHRechargeWS;


import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.claroDTHRechargeWS.scheduler.NodeVO;
import com.inter.claroDTHRechargeWS.stub.RecargarDTHPrepagoRequest;
import com.inter.claroDTHRechargeWS.stub.RecargarDTHPrepagoResponse;
import com.inter.claroDTHRechargeWS.stub.TransaccionDTHPrepagoPortType;
import com.btsl.util.Constants;




public class TestClient {

	private TransaccionDTHPrepagoPortType  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public TestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		
		TestClient TestClient = new TestClient();
		TestClient._action = "1";
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[3]);
			
			
			TestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroDTHWSConnectionManager serviceConnection = new ClaroDTHWSConnectionManager(nodeVO,"INTID00005");
			TestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+TestClient._stub.toString());
			TestClient._stubSuper = (Stub) TestClient._stub;
			TestClient._stub = (TransaccionDTHPrepagoPortType) TestClient._stubSuper;
			RecargarDTHPrepagoRequest recargarDTHPrepagoRequest= null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
			SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss",Locale.ENGLISH);
			recargarDTHPrepagoRequest=new RecargarDTHPrepagoRequest();
			recargarDTHPrepagoRequest.setIdTransacion( getRequestID());
			recargarDTHPrepagoRequest.setCodigoRecarga(args[1]);
			recargarDTHPrepagoRequest.setMonto(Double.parseDouble(args[2]));
			recargarDTHPrepagoRequest.setCodigoAplicacion("ST");
			recargarDTHPrepagoRequest.setIpAplicacion("172.16.7.229");
			recargarDTHPrepagoRequest.setFechaTransaccion(sdf.format(new Date()));
			recargarDTHPrepagoRequest.setHoraTransaccion(sdf1.format(new Date()));
			recargarDTHPrepagoRequest.setTipoProducto("TVSAT");
			recargarDTHPrepagoRequest.setBinAdquiriente("121000");
			recargarDTHPrepagoRequest.setForwardInstitucion("620700");
			RecargarDTHPrepagoResponse recargasResponse=null;
			
			System.out.println("startTime = "+Time());
			
			if(TestClient._action.equals("1"))
			{
				recargasResponse = TestClient._stub.recargarDTHPrepago(recargarDTHPrepagoRequest);
			
			}
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("TestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("TestClient Exception="+e.getMessage());
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
