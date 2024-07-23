/*
 * Created on June 18, 2009
 * 
 *  To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.client.pretups.userinfo.aup.requesthandler;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType;

/**
 * @(#)ClaroColCUInfoWSRequestFormatter
 *                 Copyright(c) 2016, Comviva TechnoLOGies Ltd.
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
public class ClaroAUPCUInfoWSRequestFormatter {
    private static final Log LOG = LogFactory.getLog(ClaroAUPCUInfoWSRequestFormatter.class);


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
        LogFactory.printLog(methodName, "Entered pAction::" + pAction + " map::" + pMap, LOG);
        Object object = null;
        pMap.put("action", String.valueOf(pAction));
        try {
        	
            if(ClaroAUPCUInfoWSI.ACTION_ACCOUNT_DETAILS==pAction){
                DistribuidorDataRequestType recargasRequest = generateCUInfoRequest(pMap);
                object =  recargasRequest;
        	}
        	
        } catch (Exception e) {
            LogFactory.printError(methodName, "Exception e ::" + e.getMessage(), LOG);
            throw e;
        } finally {
            LogFactory.printLog(methodName, "Exited Request String: object::" + object, LOG);
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
    private DistribuidorDataRequestType generateCUInfoRequest(HashMap pMap) throws Exception {
        final String methodName="generateCUInfoRequest";
        LogFactory.printLog(methodName, "Entered p_requestMap::" + pMap, LOG);
        DistribuidorDataRequestType distribuidorDataRequestType = null;
        try {
            distribuidorDataRequestType = new DistribuidorDataRequestType();
            distribuidorDataRequestType.setCodigo(pMap.get("CODIGO").toString());
        } catch (Exception e) {
            LOG.error("generateRechargeDebitRequest", "Exception e: " + e);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            LogFactory.printLog(methodName, "Exiting Request debitoRequest::" + distribuidorDataRequestType, LOG);
        }
        return distribuidorDataRequestType;
    }

}
