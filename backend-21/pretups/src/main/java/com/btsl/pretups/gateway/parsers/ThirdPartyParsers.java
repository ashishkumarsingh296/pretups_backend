package com.btsl.pretups.gateway.parsers;

import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.ExtAPIStringParser;
import com.btsl.pretups.gateway.util.ThirdPartyXMLStringParser;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.util.BTSLUtil;


public class ThirdPartyParsers extends PretupsParser {

    public static Log _log = LogFactory.getLog(ExtAPIParsers.class.getName());

    public void loadValidateNetworkDetails(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "loadValidateNetworkDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Request ID = " + p_requestVO.getRequestID());
        }

        try {
            NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(p_requestVO.getExternalNetworkCode());
           NetworkPrefixVO networkPrefixVO = null;
          if (networkVO == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXT_NETWORK_CODE);
            }
             else {
                networkPrefixVO = new NetworkPrefixVO();
                networkPrefixVO.setNetworkCode(networkVO.getNetworkCode());
                networkPrefixVO.setNetworkName(networkVO.getNetworkName());
                networkPrefixVO.setNetworkShortName(networkVO.getNetworkShortName());
                networkPrefixVO.setCompanyName(networkVO.getCompanyName());
                networkPrefixVO.setErpNetworkCode(networkVO.getErpNetworkCode());
                networkPrefixVO.setStatus(networkVO.getStatus());
                networkPrefixVO.setLanguage1Message(networkVO.getLanguage1Message());
                networkPrefixVO.setLanguage2Message(networkVO.getLanguage2Message());
                networkPrefixVO.setModifiedOn(networkVO.getModifiedOn());
                networkPrefixVO.setModifiedTimeStamp(networkVO.getModifiedTimeStamp());
                p_requestVO.setValueObject(networkPrefixVO);
                validateNetwork(p_requestVO, networkPrefixVO);
            
             }
          } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            throw be;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Request ID = " + p_requestVO.getRequestID());
            }
        }
    }


    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
        final String methodName = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Transfer ID = " + p_requestVO.getRequestID() + ", contentType : " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                int action = actionChannelParser(p_requestVO);
                parseChannelParserRequest(action, p_requestVO);
            } else {
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Message = " + p_requestVO.getDecryptedMessage() );
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLException while parsing Request Message : " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "  Exception while parsing Request Message : " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[parseChannelRequestMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ExtAPIParsers", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        }
    }
    /**
     * Method to parse channel requests on basis of keyword
     * 
     * @param action
     * @param p_requestVO
     * @throws Exception
     */
    public void parseChannelParserRequest(int action, RequestVO p_requestVO) throws Exception {
    	final String methodName = "parseChannelParserRequest";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, "Entered Request ID = " + p_requestVO.getRequestID() + ", action = " + action);
    	}

    	switch (action) {
    	   case ACTION_CHNL_CURRENCY_CONVERSION_REQUEST:
           {
           		ThirdPartyXMLStringParser.parseChanenlCurrencyConversionReq(p_requestVO);
           		break;
           }
    	   case ACTION_CHNL_O2CAPRL_REQUEST:
           {
        	   	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
        	   	p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
           		break;
           }
    	   default:
    	     	 if(_log.isDebugEnabled()){
    	     		_log.debug("Default Value " ,action);
    	     	 }

    	}
    }

    /**
     * Method to generate Response of Channel requests
     * @param p_requestVO
     * @throws Exception
     */
    public void generateChannelParserResponse(RequestVO p_requestVO) throws Exception {
    	final String methodName = "generateChannelResponse";
    	
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Request ID=" + p_requestVO.getRequestID());
        }
        int action = p_requestVO.getActionValue();

        String messageCode = p_requestVO.getMessageCode();
        if ((!BTSLUtil.isNullString(messageCode)) && (!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1) {
            messageCode = messageCode.substring(0, messageCode.indexOf("_"));
            p_requestVO.setMessageCode(messageCode);
        }
        switch (action) {
            case ACTION_CHNL_CURRENCY_CONVERSION_REQUEST:
            {
            	ThirdPartyXMLStringParser.generateChanenlCurrencyConversionResponse(p_requestVO);
                break;
            }
            case ACTION_CHNL_O2CAPRL_REQUEST:
            {
            	p_requestVO.getResponseMap().put("TYPE","O2CAPRL");
    			p_requestVO.getResponseMap().put("TXNID",p_requestVO.getTransactionID());
    			p_requestVO.getResponseMap().put("REFNO",p_requestVO.getExternalReferenceNum());
    			ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.ONLINE_O2C_TRANSFER_SUCCESS);
            	ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                break;
            }
            default:
     	     	 if(_log.isDebugEnabled()){
     	     		_log.debug("Default Value " ,action);
     	     	 }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting" );
        }
    }

    public void generateChannelResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateChannelResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "p_requestVO=" + p_requestVO.toString());
        }
        try {
            p_requestVO.setSenderMessageRequired(false);

            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                generateChannelParserResponse(p_requestVO);
            } 
        } catch (Exception e) {
            _log.error(METHOD_NAME, "  Exception while generating Response Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsParser[generateChannelResponseMessage]", "",
                "", "", "Exception getting message :" + e.getMessage());
        }
    }
 
}
