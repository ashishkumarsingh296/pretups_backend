package com.inter.claroca.multicurrency;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;
/**
 * This class was generated by Apache CXF 2.6.1
 * 2016-11-17T13:42:42.475+05:30
 * Generated source version: 2.6.1
 * 
 */
public final class ZWSTIPOCAMBIO_ZWSTIPOCAMBIO_Client {

    private static final QName SERVICE_NAME = new QName("urn:sap-com:document:sap:soap:functions:mc-style", "ZWS_TIPO_CAMBIO");
	    static ZWSTIPOCAMBIO  port=null;
    static ZWSTIPOCAMBIO_Service ss=null;
	static String fromCurrency=null;
	static String toCurrency=null;
	
    private ZWSTIPOCAMBIO_ZWSTIPOCAMBIO_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = ZWSTIPOCAMBIO_Service.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
		fromCurrency=args[1];
		toCurrency=args[2];
        try{ ss = new ZWSTIPOCAMBIO_Service(wsdlURL, SERVICE_NAME);
       port = ss.getZWSTIPOCAMBIO();  

			//INTERFAZ_WS dXZ90g8hyH5$

        
        System.out.println("Invoking ztipoDeCambio...");
    	System.out.println("From Currency : "+fromCurrency+" To Currency : "+toCurrency);
        java.math.BigDecimal _ztipoDeCambio__return = port.ztipoDeCambio(fromCurrency,"","M", toCurrency);
	System.out.println("From Currency : "+fromCurrency+" To Currency : "+toCurrency+" Conversion Rate = "+ _ztipoDeCambio__return);
        
	 }
      	catch (Exception e)
    	{
    	System.out.println("Inside Exception ");
    	e.printStackTrace();
    	}
      	finally{
      		try {System.out.println("Request : "	+((Stub) port)._createCall().getMessageContext().getRequestMessage());}catch (Exception e){}
        	try {System.out.println("Response : "	+((Stub) port)._createCall().getMessageContext().getResponseMessage());}catch (Exception e){}
      	}
    }

}