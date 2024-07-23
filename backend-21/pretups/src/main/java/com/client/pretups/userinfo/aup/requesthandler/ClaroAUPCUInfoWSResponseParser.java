package com.client.pretups.userinfo.aup.requesthandler;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.inter.ClaroAUPUserInfo.GetDistributorDataResponseElement;
/**
 * @(#)ClaroColCUInfoWSResponseParser
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
public class ClaroAUPCUInfoWSResponseParser {

    private static final Log LOG = LogFactory.getLog(ClaroAUPCUInfoWSResponseParser.class.getName());

    /**
     * This method is used to parse the response.
     * 
     * @param pAction
     * @param requestMap
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseResponse(int pAction, HashMap requestMap) throws Exception {
        final String methodName="parseResponse";
        LogFactory.printLog(methodName, "Entered pAction::" + pAction + " requestMap:: " + requestMap, LOG);
        HashMap map = null;
        try {
            if(ClaroAUPCUInfoWSI.ACTION_ACCOUNT_DETAILS==pAction){
                map = parseCUInfoResponse(requestMap);
        	}
            
        } catch (Exception e) {
            LogFactory.printError(methodName, "Exception e: :" + e.getMessage(), LOG);
            throw e;
        } finally {
            LogFactory.printLog(methodName, "Exiting map::" + map, LOG);
        }
        return map;
    }

    /**
     * This method is used to parse the credit response.
     * 
     * @param requestMap
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseCUInfoResponse(HashMap requestMap) throws Exception {
        final String methodName ="parseCUInfoResponse";
        LogFactory.printLog(methodName, "Entered ", LOG);
        HashMap responseMap = null;
        GetDistributorDataResponseElement response = null;
        try {
            Object object = requestMap.get("RESPONSE_OBJECT");
            responseMap = new HashMap();

            if (object != null){
                response = (GetDistributorDataResponseElement) object;
            }else{
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
            if (response.getResult().getDistributornameOut()!= null) {

                responseMap.put("INTERFACE_STATUS", String.valueOf(response.getResult().getReturncodeOut()));

                if (String.valueOf(response.getResult().getReturncodeOut()).equalsIgnoreCase(ClaroAUPCUInfoWSI.RESPONSE_SUCCESS)) {
                    if (String.valueOf(response.getResult().getReturncodeOut())!= null) {

                        if (BTSLUtil.isNullString(response.getResult().getDistributornameOut())) {
                            responseMap.put("CHANNELUSER_NAME", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_NAME", response.getResult().getDistributornameOut());
                        }

                        if (BTSLUtil.isNullString(response.getResult().getAddressOut())) {
                            responseMap.put("CHANNELUSER_ADDRESS", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_ADDRESS", response.getResult().getAddressOut());
                        }

                        if (BTSLUtil.isNullString(response.getResult().getEmailOut())) {
                            responseMap.put("CHANNELUSER_EMAIL", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_EMAIL", response.getResult().getEmailOut());
                        }

                        if (BTSLUtil.isNullString(response.getResult().getPhoneOut())) {

                            responseMap.put("CHANNELUSER_TELEPHONE", "NA");
                        } else {
                            if (!BTSLUtil.isNumeric(response.getResult().getPhoneOut())) {
                                responseMap.put("CHANNELUSER_TELEPHONE", "");
                                LogFactory.printLog(methodName, "User Telephone Number is not numeric ", LOG);
                            } else{
                                responseMap.put("CHANNELUSER_TELEPHONE", response.getResult().getPhoneOut());
                            }
                        }

                        if (BTSLUtil.isNullString(response.getResult().getCityOut())) {
                            responseMap.put("CHANNELUSER_CITY", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_CITY", response.getResult().getCityOut());
                        }
                        
                        if (BTSLUtil.isNullString(response.getResult().getStateOut())) {
                            responseMap.put("CHANNELUSER_STATE", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_STATE", response.getResult().getStateOut());
                        }
                        
                        if (BTSLUtil.isNullString(response.getResult().getCountryOut())) {
                            responseMap.put("CHANNELUSER_COUNTRY", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_COUNTRY", response.getResult().getCountryOut());
                        }
                        if (BTSLUtil.isNullString(response.getResult().getFiscalidOut())) {
                            responseMap.put("CHANNELUSER_EMP_CODE", "NA");
                        } else {
                            responseMap.put("CHANNELUSER_EMP_CODE", response.getResult().getFiscalidOut());
                        }
                        responseMap.put("CHANNELUSER_CREDITLIMIT", "NA");
                        responseMap.put("CHANNELUSER_PAYCYCLEPERIOD", "NA");
                        responseMap.put("CHANNELUSER_PAYPERIOD", "NA");
                        
                        
                        

                    } else {
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_RESPONSE_OBJECT);
                    }
                } else if (String.valueOf(response.getResult().getReturncodeOut()).equalsIgnoreCase(ClaroAUPCUInfoWSI.RESPONSE_MONTO_INVALID)||
                		String.valueOf(response.getResult().getReturncodeOut()).equalsIgnoreCase(ClaroAUPCUInfoWSI.RESPONSE_MSISDN_INVALID)||
                		String.valueOf(response.getResult().getReturncodeOut()).equalsIgnoreCase(ClaroAUPCUInfoWSI.RESPONSE_CUSTOMER_INVALID)){
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                }

            } else {
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.ERROR_RESPONSE);
                LogFactory.printError(methodName, "Invalid Error Code::", LOG);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }

        } catch (BTSLBaseException be) {
            LogFactory.printError(methodName, "Exception e::" + be.getMessage(), LOG);
            throw be;
        } catch (Exception e) {
            LogFactory.printError(methodName, "Exception e::" + e.getMessage(), LOG);
            throw e;
        } finally {
            LogFactory.printLog(methodName, "Exited responseMap::" + responseMap, LOG);
        }
        return responseMap;
    }

}
