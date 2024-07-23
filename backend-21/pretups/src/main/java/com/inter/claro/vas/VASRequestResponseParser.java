package com.inter.claro.vas;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claro.vas.stub.Input_Paramaters;
import com.inter.claro.vas.stub.WS_Result;
/**
 * @(#)VASRequestResponseParser
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
public class VASRequestResponseParser {
    Log log = LogFactory.getLog("VASRequestResponseParser".getClass().getName());

    /**
     * This method used to generate the Service specific request.
     * 
     * @param int pAction
     * @param HashMap pMap
     * @return Input_Paramaters
     * @throws Exception
     */
    public Input_Paramaters generateVASRequest(int pAction, HashMap pMap) throws Exception {
    	final String methodName="generateRequestObject";
    			

        if (log.isDebugEnabled())
            log.debug(methodName, "Entered pAction=" + pAction + " map: " + pMap);
        Input_Paramaters vasRequest = new Input_Paramaters();
        try {
        	
        	if(VASClaroI.ACTION_RECHARGE_CREDIT==pAction)
        	{
        		   vasRequest = generateCreditReqestObjectInMap(pMap);
        	}
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception e: " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited");
        }// end of finally
        return vasRequest;
    }
    
    /**
     * This method used to parse the response
     * 
     * @param int pAction
     * @param WS_Result responseObject
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseResponseObject(int pAction, WS_Result responseObject) throws Exception {
    	final String methodName="parseResponseObject";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered pAction" + pAction + " responseObject" + responseObject);
        HashMap map = null;
        try {
        	
        	if(VASClaroI.ACTION_RECHARGE_CREDIT==pAction)
        	{
        		 map = parseRechargeCreditResponseObject(responseObject);
        	}

        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting map: " + map);
        }// end of finally
        return map;

    }
    
    /**
     * This method used to generate the credit response
     * 
     * @param HashMap pMap
     * @return Input_Paramaters
     * @throws Exception
     */
    private Input_Paramaters generateCreditReqestObjectInMap(HashMap pMap) throws Exception {
        final String methodName="setPromotionReqestObjectInMap";
    	if (log.isDebugEnabled())
            log.debug(methodName, "Entered pMap: " + pMap);
        Input_Paramaters vasRequest = new Input_Paramaters();
        try {
        	vasRequest.setIP_ORIGEN((String) pMap.get("IP"));
        	String separator =(String) pMap.get("SEPARATOR");
        	StringBuffer trama =new StringBuffer("");
        	trama.append((String) pMap.get("USERNAME_1")+separator);
        	trama.append((String) pMap.get("PASSWORD_1")+separator);
        	trama.append((String) pMap.get("METHOD")+separator);
        	trama.append((String) pMap.get("MSISDN")+separator);
        	trama.append((String) pMap.get("COMMENT")+";");
        	trama.append((String) pMap.get("HOST_ID")+";");
        	trama.append((String) pMap.get("SERIAL_NO")+";");
        	trama.append((String) pMap.get("PACKET_CODE")+";");
        	trama.append((String) pMap.get("COLLECTION_CODE")+separator+"*");
        		
        	if (log.isDebugEnabled())
                log.debug(methodName, " trama: " + trama);
        	
        	//vasRequest.setTRAMA((String)pMap.get("MSISDN"));
        	vasRequest.setTRAMA(trama.toString());
        	
        	
        } catch (Exception e) {
            log.errorTrace("Exception in : "+methodName, e);
            throw e;
        } finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited pMap: " + pMap);
        }
        return vasRequest;
    }

    /**
     *  This method used to parse the response of credit response 
     * 
     * @param WS_Result responseObject
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseRechargeCreditResponseObject(WS_Result responseObject) throws Exception {
    	 final String methodName="parseRechargeCreditResponseObject";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered ");
        HashMap map = null;
        try {

            map = new HashMap();
  	   if (log.isDebugEnabled())
            	log.debug(methodName, "responseCode--"+responseObject.getCODIGO()+"responseMessgage--"+responseObject.getDESCRIPCION());
       
            String responseCode = responseObject.getCODIGO();
            String responseMessgage = responseObject.getDESCRIPCION();
            String wapID = "";
            map.put("RESP_CODE", responseCode);
            map.put("RESP_MSG", responseMessgage);
            map.put("WAP_ID", wapID);
        } catch (Exception e) {
            log.error(methodName, "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exit  map:" + map);
        }
        return map;
    }

}
