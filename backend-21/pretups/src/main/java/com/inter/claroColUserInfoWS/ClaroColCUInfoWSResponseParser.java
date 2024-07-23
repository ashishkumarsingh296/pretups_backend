package com.inter.claroColUserInfoWS;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.inter.claroColUserInfoWS.stub.ObtenerDatosDistribuidorResponse;
import com.inter.claroColUserInfoWS.stub.RespuestaWSDistribuidor;
/**
 * @(#)ClaroColCUInfoWSResponseParser
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
public class ClaroColCUInfoWSResponseParser {

    private static Log log = LogFactory.getLog(ClaroColCUInfoWSResponseParser.class.getName());

    /**
     * This method is used to parse the response.
     * 
     * @param int pAction
     * @param String p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseResponse(int pAction, HashMap requestMap) throws Exception {
    	final String methodName="parseResponse";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered pAction::" + pAction + " requestMap:: " + requestMap);
        HashMap map = null;
        try {
        	if(ClaroColCUInfoWSI.ACTION_ACCOUNT_DETAILS==pAction)
        	{
        		   map = parseCUInfoResponse(requestMap);
        	}
            
        } catch (Exception e) {
            log.error(methodName, "Exception e: :" + e.getMessage());
            throw e;
        } finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting map::" + map);
        }
        return map;
    }

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseCUInfoResponse(HashMap requestMap) throws Exception {
    	final String methodName ="parseCUInfoResponse";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered ");
        HashMap responseMap = null;
        ObtenerDatosDistribuidorResponse response = null;
        try {
            Object object = requestMap.get("RESPONSE_OBJECT");
            responseMap = new HashMap();

            if (object != null)
                response = (ObtenerDatosDistribuidorResponse) object;
            else
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            if (response.getRespuestaWSDistribuidor().getDistribuidor() != null) {
            if (response.getRespuestaWSDistribuidor().getDistribuidor() != null) {

                responseMap.put("INTERFACE_STATUS", String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()));
                if (log.isDebugEnabled())
                    log.debug(methodName, " responseCode::" + response.getRespuestaWSDistribuidor().getCodigoMensaje());
         
                
                if (String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_SUCCESS)) {
                    if (String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje())!= null) {

                        if (BTSLUtil.isNullString(response.getRespuestaWSDistribuidor().getDistribuidor().getNombre())) {
                            responseMap.put("CHANNELUSER_NAME", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_NAME", response.getRespuestaWSDistribuidor().getDistribuidor().getNombre());
                        }

                        if (BTSLUtil.isNullString(response.getRespuestaWSDistribuidor().getDistribuidor().getDireccion())) {
                            responseMap.put("CHANNELUSER_ADDRESS", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_ADDRESS", response.getRespuestaWSDistribuidor().getDistribuidor().getDireccion());
                        }

                        if (BTSLUtil.isNullString(response.getRespuestaWSDistribuidor().getDistribuidor().getEmail())) {
                            responseMap.put("CHANNELUSER_EMAIL", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_EMAIL", response.getRespuestaWSDistribuidor().getDistribuidor().getEmail());
                        }

                        if (BTSLUtil.isNullString(response.getRespuestaWSDistribuidor().getDistribuidor().getTelefono())) {

                            responseMap.put("CHANNELUSER_TELEPHONE", "NA");
                        } else {
                            if (!BTSLUtil.isNumeric(response.getRespuestaWSDistribuidor().getDistribuidor().getTelefono())) {
                                responseMap.put("CHANNELUSER_TELEPHONE", "");
                                log.debug(methodName, "User Telephone Number is not numeric ");
                            } else
                                responseMap.put("CHANNELUSER_TELEPHONE", response.getRespuestaWSDistribuidor().getDistribuidor().getTelefono());
                        }

                        if (BTSLUtil.isNullString(response.getRespuestaWSDistribuidor().getDistribuidor().getCiudad())) {
                            responseMap.put("CHANNELUSER_CITY", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_CITY", response.getRespuestaWSDistribuidor().getDistribuidor().getCiudad());
                        }
                        responseMap.put("CHANNELUSER_STATE", "NA");
                        responseMap.put("CHANNELUSER_CREDITLIMIT", "NA");
                        responseMap.put("CHANNELUSER_PAYCYCLEPERIOD", "NA");
                        responseMap.put("CHANNELUSER_PAYPERIOD", "NA");
                        
                        
                        

                    } else {
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                } else if (String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_MONTO_INVALID)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_CLIENT_INVALID_RANGE)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_CUSTOMER_INVALID)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_MSISDN_INVALID)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_MONTO_AMOUNT_INVALID)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_ERRRO)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_ERROR1)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_ERROR2)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_ERROR3)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_ERROR6)||
                		String.valueOf(response.getRespuestaWSDistribuidor().getCodigoMensaje()).equalsIgnoreCase(ClaroColCUInfoWSI.RESPONSE_CODE_INVALID)){
                
                    if (log.isDebugEnabled())
                        log.debug(methodName, "Exited responseCode::" + response.getRespuestaWSDistribuidor().getCodigoMensaje());
                    responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    
                	throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                }

            }
            } else {
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.ERROR_RESPONSE);
                log.error(methodName, "Invalid Error Code::");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }

        } catch (BTSLBaseException be) {
            log.error(methodName, "Exception e::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

}
