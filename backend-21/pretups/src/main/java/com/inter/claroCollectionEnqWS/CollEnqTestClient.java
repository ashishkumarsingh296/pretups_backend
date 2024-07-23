package com.inter.claroCollectionEnqWS;


import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.btsl.common.BTSLBaseException;
import com.inter.claroCollectionEnqWS.scheduler.NodeVO;
import com.inter.claroCollectionEnqWS.stub.ConsultaDeuda;
import com.inter.claroCollectionEnqWS.stub.ConsultaDeudaResponse;
import com.inter.claroCollectionEnqWS.stub.ConsultaPagos_PortType;
import com.inter.claroCollectionEnqWS.stub.DeudaDocumentoType;
import com.inter.claroCollectionEnqWS.stub.DeudaServicioType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;




public class CollEnqTestClient {

	private ConsultaPagos_PortType  _stub=null;
	private String _action=null;	
	private String _msisdn=null;
	private String _amount=null;
	private Stub  _stubSuper=null;

	public CollEnqTestClient(){
		super();
	}

	public static void main(String[] args) 
	{
		CollEnqTestClient CollEnqTestClient = new CollEnqTestClient();
		CollEnqTestClient._action = "1";
		try
		{
			Constants.load(args[0]);
			org.apache.log4j.PropertyConfigurator.configure(args[3]);

			CollEnqTestClient.loadInputs();
			NodeVO nodeVO=new NodeVO();
			nodeVO.setUrl("http://localhost:8080/ClaroWebService/services/EbsRecargaVirtualServiceSoapBindingImpl");
			nodeVO.setUserName("COMTEST");
			nodeVO.setPassword("COMTEST");
			nodeVO.setReadTimeOut(10000);
			ClaroCollEnqWSConnectionManager serviceConnection = new ClaroCollEnqWSConnectionManager(nodeVO,"INTID00005");
			CollEnqTestClient._stub = serviceConnection.getService();
			System.out.println("_stub = "+CollEnqTestClient._stub.toString());
			CollEnqTestClient._stubSuper = (Stub) CollEnqTestClient._stub;
			CollEnqTestClient._stub = (ConsultaPagos_PortType) CollEnqTestClient._stubSuper;

			ConsultaDeuda consultaDeudaRequest= null;

			consultaDeudaRequest=new ConsultaDeuda();
			consultaDeudaRequest.setTxId(getRequestID());
			consultaDeudaRequest.setpCodAgencia("AG01");
			consultaDeudaRequest.setpCodAplicacion("ST");
			consultaDeudaRequest.setpCodBanco("520200");
			consultaDeudaRequest.setpCodCanal("15");
			consultaDeudaRequest.setpCodCiudad("CIU01");
			consultaDeudaRequest.setpCodMoneda("604");
			consultaDeudaRequest.setpCodReenvia("520200");
			consultaDeudaRequest.setpCodTipoServicio(args[2]);//Sub Service Code
			consultaDeudaRequest.setpDatoIdentific(args[1]);//Mobile Numbre
			consultaDeudaRequest.setpNumeroComercio("111111");//User Id
			consultaDeudaRequest.setpNombreComercio("000000000000666");//User Name
			consultaDeudaRequest.setpNroReferencia("77888888");
			consultaDeudaRequest.setpNroTerminal("TERM01");
			consultaDeudaRequest.setpPlaza("PL02");
			consultaDeudaRequest.setpPosUltDocumento(new BigDecimal("0"));
			consultaDeudaRequest.setpTipoIdentific("01");

			ConsultaDeudaResponse recargasResponse=null;

			System.out.println("startTime = "+Time());

			if(CollEnqTestClient._action.equals("1"))
			{
				recargasResponse = CollEnqTestClient._stub.consultaDeuda(consultaDeudaRequest);
				HashMap hashMap=new HashMap();
				hashMap.put("RESPONSE_OBJECT", recargasResponse);
				hashMap.put("SELECTOR_BUNDLE_ID", args[2]);
				parseRechargeAccountDetailsResponse(hashMap);
			}
			System.out.println("EndTime = "+Time());

		}
		catch(SOAPFaultException se)
		{
			System.out.println("CollEnqTestClient:SOAPFaultException getFaultString="+se.getMessage());
			se.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("CollEnqTestClient Exception="+e.getMessage());
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


	private static HashMap parseRechargeAccountDetailsResponse(HashMap _reqMap) throws Exception
	{
		System.out.println("Entered ");
		HashMap responseMap = null;
		ConsultaDeudaResponse recargasResponse=null;
		try
		{
			Object object=_reqMap.get("RESPONSE_OBJECT");
			responseMap = new HashMap();

			if(object!=null)
				recargasResponse=(ConsultaDeudaResponse)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(recargasResponse.getAudit()!=null && !BTSLUtil.isNullString(recargasResponse.getAudit().getErrorCode()))
			{
				System.out.println("Response Code "+recargasResponse.getAudit().getErrorCode());
				responseMap.put("INTERFACE_STATUS",recargasResponse.getAudit().getErrorCode());
	
				if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_SUCCESS)){
				responseMap=parseValidateResponseObject(responseMap,_reqMap,recargasResponse);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_NOBILLPAY_SUCCESS)){

					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_MONTO_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_CLIENT_INVALID_RANGE)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_CUSTOMER_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_MSISDN_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_NUMBER);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_MONTO_AMOUNT_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERRRO)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERROR1)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERROR2)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERROR3)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERROR6)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}

			}else{
				responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				System.out.println("Invalid Error Code::");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
			}

		}
		catch(BTSLBaseException be)
		{
			System.out.println("Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			System.out.println("Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			System.out.println("Exited responseMap::"+responseMap);
		}
		return responseMap;
	}


	/**
	 * @author vipan.kumar
	 * @param responseMap
	 * @param requestMap
	 * @param recargasResponse
	 * @return
	 * @throws Exception
	 */
	private static HashMap parseValidateResponseObject(HashMap responseMap,HashMap requestMap, ConsultaDeudaResponse recargasResponse) throws Exception{
	 System.out.println("Started recargasResponse::"+recargasResponse);
		try{
			
			boolean flag=false;
			DeudaServicioType[] deudaServicioType=recargasResponse.getXDeudaCliente();
			boolean invoicenofound =false; 
			if(deudaServicioType!=null)
			{

				for (int i = 0; i < deudaServicioType.length; i++) {
					System.out.println("deudaServicioType.length::"+deudaServicioType.length);
					if(deudaServicioType[i].getXCodTipoServicio().equalsIgnoreCase((String)requestMap.get("SELECTOR_BUNDLE_ID")))
					{
						System.out.println("deudaServicioType::"+deudaServicioType[i].getXCodTipoServicio());
						responseMap.put("TOTAL_PENDING_BALANCE",String.valueOf(deudaServicioType[i].getXMontoDeudaTotal().doubleValue()));
						responseMap.put("SERVICE_NAME",deudaServicioType[i].getXOpcionRecaudacion());
						responseMap.put("SERVICE_CODE",deudaServicioType[i].getXCodTipoServicio());

						DeudaDocumentoType[] deudaDocumentoType=deudaServicioType[i].getXDeudaDocs();
						if(deudaDocumentoType!=null)
						{
							System.out.println("deudaDocumentoType.length::"+deudaDocumentoType.length);
							responseMap.put("INVOICE_SIZE", String.valueOf(deudaDocumentoType.length));
							for (int j = 0; j < deudaDocumentoType.length; j++) {

								responseMap.put("SERVICE_NAME_"+j,deudaDocumentoType[j].getXDescripServ());
								responseMap.put("SERVICE_CODE_"+j,deudaDocumentoType[j].getXTipoServicio());
								responseMap.put("INVOICE_NUM_"+j,deudaDocumentoType[j].getXNumeroDoc());
								responseMap.put("PERIOD_PENDING_BALANCE_"+j,String.valueOf(deudaDocumentoType[j].getXMontoDebe().doubleValue()));
								responseMap.put("MIN_PENDING_BALANCE_"+j,String.valueOf(deudaDocumentoType[j].getXMontoFact().doubleValue()));
								responseMap.put("INVOICED_PENDING_BALANCE_"+j,String.valueOf(deudaDocumentoType[j].getXImportePagoMin().doubleValue()));
								responseMap.put("BILL_PERIOD_START_"+j,BTSLUtil.getDateStringFromDate(deudaDocumentoType[j].getXFechaEmision().getTime(),"dd/MM/yy"));
								responseMap.put("BILL_PERIOD_END_"+j,BTSLUtil.getDateStringFromDate(deudaDocumentoType[j].getXFechaVenc().getTime(),"dd/MM/yy"));
								flag=true;
								 if (deudaDocumentoType[j].getXNumeroDoc().equals(requestMap.get("INVOICE_NUMBER"))&&!invoicenofound)
										invoicenofound=true;	
								
							}
							if(!BTSLUtil.isNullString((String)requestMap.get("INVOICE_NUMBER"))&&!invoicenofound)
							{
							 throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_INVOICE_NO);	
							}
							else if(BTSLUtil.isNullString((String)requestMap.get("INVOICE_NUMBER")))
								responseMap.put("INVOICE_NUMBER", deudaDocumentoType[0].getXNumeroDoc());
						}else{
							System.out.println("Invalid Response object deudaDocumentoType:");
							flag=false;

						}
					}
				}


			}else{
				System.out.println("Invalid Response object DeudaServicioType:");
				flag=false;

			}

			if(!flag){
				System.out.println("Invalid Response Object ");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
			}

		}catch (Exception e) {
			System.out.println("Exception e::"+e.getMessage());
			if(InterfaceErrorCodesI.ERROR_INVALID_INVOICE_NO.equals(e.getMessage()))
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_INVOICE_NO);	
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
		}
		
		return responseMap;
	}
}
