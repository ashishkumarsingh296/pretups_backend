/*
 * Created on June 18, 2009
 * 
 *  To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroColUserInfoWS;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.inter.claroColUserInfoWS.stub.ObtenerDatosDistribuidor;
import com.btsl.util.BTSLUtil;
import com.inter.claroColUserInfoWS.stub.ObtenerDatosDistribuidor;

/**
 * @(#)ClaroColCUInfoWSRequestFormatter
 *                 Copyright(c) 2016, Comviva Technologies Ltd.
 * 				   All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Pankaj Sharma Spt 28,2016 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 */
public class ClaroColCUInfoWSRequestFormatter {
    public static final Log log = LogFactory.getLog(ClaroColCUInfoWSRequestFormatter.class);
    String lineSep = null;
    String soapAction = "";

    public ClaroColCUInfoWSRequestFormatter() {
        lineSep = System.getProperty("line.separator") + "";
    }

    /**
     * This method is used to parse the response string based on the type of
     * Action.
     * 
     * @param int pAction
     * @param HashMap pMap
     * @return String.
     * @throws Exception
     */
    protected Object generateRequest(int pAction, HashMap pMap) throws Exception {
    	final String methodName="generateRequest";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered pAction::" + pAction + " map::" + pMap);
        Object object = null;
        pMap.put("action", String.valueOf(pAction));
        try {
        	
        	if(ClaroColCUInfoWSI.ACTION_ACCOUNT_DETAILS==pAction)
        	{
        		ObtenerDatosDistribuidor recargasRequest = generateCUInfoRequest(pMap);
                object =  recargasRequest;
        	}
        	
        } catch (Exception e) {
            log.error(methodName, "Exception e ::" + e.getMessage());
            throw e;
        } finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited Request String: object::" + object);
        }
        return object;
    }
    
    /**
     * This method is used to generate the service specific request.
     * 
     * @param HashMap pMap
     * @return DistribuidorDataRequestType
     * @throws Exception
     */
    private ObtenerDatosDistribuidor generateCUInfoRequest(HashMap pMap) throws Exception {
    	final String methodName="generateCUInfoRequest";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered p_requestMap::" + pMap);
        ObtenerDatosDistribuidor obtenerDatosDistribuidor = null;
        try {
        	obtenerDatosDistribuidor = new ObtenerDatosDistribuidor();
        	if(pMap.get("CODIGO")!=null)
        		obtenerDatosDistribuidor.setCodigo(pMap.get("CODIGO").toString());
        	if(pMap.get("NIT")!=null)
           	obtenerDatosDistribuidor.setNit(pMap.get("NIT").toString());
        	if(pMap.get("USUARIO")!=null)
           	obtenerDatosDistribuidor.setUsuario(pMap.get("USUARIO").toString());
        	if(pMap.get("COUNTRASENIA")!=null)
           	obtenerDatosDistribuidor.setContrasenia(pMap.get("COUNTRASENIA").toString());
        } catch (Exception e) {
            log.error("generateRechargeDebitRequest", "Exception e: " + e);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting Request debitoRequest::" + obtenerDatosDistribuidor);
        }
        return obtenerDatosDistribuidor;
    }

}
