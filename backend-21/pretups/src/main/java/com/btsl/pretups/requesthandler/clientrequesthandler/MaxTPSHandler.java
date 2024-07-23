package com.btsl.pretups.requesthandler.clientrequesthandler;

/**
 * * @(#)MaxTPSHandler.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Mohd Suhel Oct 11 , 2017 Initial Creation
 * 
 * Any User Enquire about the TPS .
 * This class handles the enquiry for TPS
 * 
 * 
 */

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.XMLTagValueValidation;


public class MaxTPSHandler implements ServiceKeywordControllerI {
    private Log log = LogFactory.getLog(MaxTPSHandler.class.getName());

    // declaring the tag names
    private static final  String XML_TAG_QDATE = "QDATE";
    private static final String XML_TAG_QHOUR = "QHOUR";
    
    private HashMap requestMap = null;
    private RequestVO requestVO = null;
  
   


    /**
     * This method is the entry point in the class.This methods in turns call
     * the private methods to carry
     * 
     * 
     * @param pRequestVO
     */
    public void process(RequestVO pRequestVO) {
        final String methodName = "process";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered....: pRequestVO= " + pRequestVO);
        }

        requestVO = pRequestVO;
        Connection con = null;
        
       
        
        try {
            con = OracleUtil.getConnection();
            requestMap = requestVO.getRequestMap();
            // short code for the service type
            // this call validates all the passed parameters of the request
           
            String qDate = (String) requestMap.get(XML_TAG_QDATE);
        	String qHour = (String)requestMap.get(XML_TAG_QHOUR);
        	
            
            validate(qDate,qHour);

            Map<String,String> tpsDetail = new HashMap<>();
            
           
            tpsDetail = loadTPSDetails(con,qDate,qHour);
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Entered....: pRequestVO= " + tpsDetail);
            }

            requestVO.setValueObject(tpsDetail);
            requestVO.setSuccessTxn(true);
            
        } catch (BTSLBaseException be) {
            log.error(methodName, be.getMessage());
            requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
           
            log.errorTrace(methodName, be);
        } catch (Exception ex) {
            requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            log.error(methodName, ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MaxTPSHandler[process]", "", "", "",  ex.getMessage());
            requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            requestVO.setRequestMap(requestMap);
            pRequestVO = requestVO;
            // setting the variable to null for efficient garbage collection
            requestMap = null;
            requestVO = null;
           
            // clossing database connection
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }// end of finally
       
    }

    /**
     * This methods validates the input parameters of the request.It performs
     * the checks on these paramteres and throws
     * an error if any condition is not satidfied.Also it makes an entry in the
     * requestMap in case of error
     * 
     * @param pCon
     * @throws BTSLBaseException
     */
    private void validate(String qDate , String qHour) throws BTSLBaseException {
        final String methodName = "validate";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered....");
        }

      
        Pattern pattern;
        Matcher matcher;
        String time24HoursPattern ="^$|^(([01][0-9])|(2[0-3]))";



        try {
        	
        	
        	XMLTagValueValidation.validateTxnDate(qDate,true);

        	pattern = Pattern.compile(time24HoursPattern);
        	if(!BTSLUtil.isNullString(qHour))
        	{	
        		matcher = pattern.matcher(qHour);
        		if(!matcher.matches())
        			{
        				requestMap.put("RES_ERR_KEY", XML_TAG_QHOUR);
        				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MAXTPS_ERROR_INVALID_QHOUR);
        			}
        	}
        	else
        	{
        		requestMap.put("RES_ERR_KEY", XML_TAG_QHOUR);
        		throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.MAXTPS_ERROR_INVALID_QHOUR);
        	}
        	
        	
        

        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error(methodName, "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MaxTPSHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("MaxTPSHandler", methodName, PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        finally {
        	if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting ....");
            }	
        }
        
    }

    /**
     * This method loads the transfer details based on the passed values of
     * sender msisdn, receiver msisdn, service keyword .
     * 
     * @param pCon
     * @return ArrayList
     * @throws BTSLBaseException
     */

    private Map<String,String> loadTPSDetails(Connection pCon,String qDate,String qHour) throws BTSLBaseException {
        final String methodName = "loadTPSDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered....");
        }

        C2STransferDAO c2STransferDAO = new C2STransferDAO();
        Map<String,String> tpsDetailMap = new HashMap<>();
        try {
        	tpsDetailMap = c2STransferDAO.fetchTPSDetails(pCon,qDate,qHour);
        	
            
            
        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error(methodName, "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MaxTPSHandler[loadTransferSummary]", "", "", "", "Exception:" + e.getMessage());
            // throw new BTSLBaseException("MaxTPSHandler",

            throw new BTSLBaseException("MaxTPSHandler", methodName, PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting....");
        }
        return tpsDetailMap;
    }

   

}
