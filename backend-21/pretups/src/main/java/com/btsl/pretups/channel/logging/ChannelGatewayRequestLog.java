package com.btsl.pretups.channel.logging;

import java.util.regex.PatternSyntaxException;

/*
 * @(#)ChannelGatewayRequestLog.java Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd. Class for logging all the channel
 * request
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.receiver.FixedInformationVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class ChannelGatewayRequestLog {
    private static Log _log = LogFactory.getLog(ChannelGatewayRequestLog.class.getName());


    /**
	 * ensures no instantiation
	 */
    private ChannelGatewayRequestLog(){
    	
    }
    /**
     * Method that prepares the the string to be written in log file
     * 
     * @param p_requestVO
     * @return
     */
    private static String generateMessageString(RequestVO p_requestVO) {
        final StringBuilder strBuild = new StringBuilder();
        final String METHOD_NAME = "generateMessageString";
        StringBuilder loggerValue= new StringBuilder(); 
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            strBuild.append("[ReqOut:]");
            if (p_requestVO != null) {
                strBuild.append("[RQID:");
                strBuild.append(p_requestVO.getRequestIDStr());
                strBuild.append("]");
                // strBuild.append("[CRDT:");strBuild.append(p_requestVO.getCreatedOn());
                // strBuild.append("]");
                strBuild.append("[STV:");
                strBuild.append(p_requestVO.getServiceType());
                strBuild.append("]");
                strBuild.append("[S:");
                strBuild.append(p_requestVO.getRequestGatewayType());
                strBuild.append("]");
                strBuild.append("[RQC:");
                strBuild.append(BTSLUtil.NullToString(p_requestVO.getMessageCode()));
                strBuild.append("]");
                if (channelUserVO != null) {
                    strBuild.append("[UN:");
                    strBuild.append(channelUserVO.getUserName());
                    strBuild.append("]");
                    strBuild.append("[CAT:");
                    strBuild.append(channelUserVO.getCategoryCode());
                    strBuild.append("]");
                    strBuild.append("[MSISDN:");
                    strBuild.append(p_requestVO.getRequestMSISDN());
                    strBuild.append("]");
                    strBuild.append("[USt:");
                    strBuild.append(channelUserVO.getStatus());
                    strBuild.append("]");
                    strBuild.append("[UNW:");
                    strBuild.append(channelUserVO.getNetworkID());
                    strBuild.append("]");
                } else {
                    strBuild.append("[UsInf.N.A,UName:null,Cat:null,UStatus:null,U N/W:null ] ");
                }
                // Commented for hiding PIN
                // strBuild.append(" [Incoming
                // SMS:");strBuild.append(p_requestVO.getRequestMessage() );strBuild.append("]");
                if (!BTSLUtil.isNullString(p_requestVO.getIncomingSmsStr())) {
                    strBuild.append("[DSMS:");
                    strBuild.append(p_requestVO.getIncomingSmsStr());
                    strBuild.append("]");
                } else {
                    strBuild.append("[DSMS:N.A]");
                }
                strBuild.append("[TEMPTID:");
                strBuild.append(p_requestVO.getTempTransID());
                strBuild.append("]");
                strBuild.append("[UDH:");
                strBuild.append(BTSLUtil.NullToString(p_requestVO.getUDH()));
                strBuild.append("]");
                strBuild.append("[ST:");
                strBuild.append(p_requestVO.getSourceType());
                strBuild.append("]");
                strBuild.append("[SRVPRT:");
                strBuild.append(p_requestVO.getServicePort());
                strBuild.append("]");
                strBuild.append("[OINFO:");
                strBuild.append(p_requestVO.getRequestGatewayCode());
                strBuild.append(",MsgReq=");
                strBuild.append(p_requestVO.isUnmarkSenderUnderProcess());
                strBuild.append(",FT=");
				if(p_requestVO.getMessageGatewayVO()!=null){
                	strBuild.append(p_requestVO.getMessageGatewayVO().getFlowType());
				}
				strBuild.append(" ResTyp=");
                strBuild.append(p_requestVO.getMsgResponseType());
                strBuild.append("]");
                if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
                    strBuild.append("[RETMSG:");
                    strBuild.append(p_requestVO.getSenderReturnMessage());
                    strBuild.append("]");
                } else {
                    strBuild.append("[RETMSG:");
                    strBuild.append(BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
                    strBuild.append("]");
                }
                final FixedInformationVO fixVO = (FixedInformationVO) p_requestVO.getFixedInformationVO();
                if (fixVO != null) {
                    strBuild.append("[Mcc:");
                    strBuild.append(fixVO.getMcc());
                    strBuild.append(" Mnc:");
                    strBuild.append(fixVO.getMnc());
                    strBuild.append(" Lac:");
                    strBuild.append(fixVO.getLac());
                    strBuild.append(" Cid:");
                    strBuild.append(fixVO.getCid());
                    strBuild.append(" Lang:");
                    strBuild.append(fixVO.getLanguage());
                    strBuild.append(" SrvId:");
                    strBuild.append(fixVO.getServiceId());
                    strBuild.append(" SrvVers:");
                    strBuild.append(fixVO.getServiceVersion());
                    strBuild.append(" Positn:");
                    strBuild.append(fixVO.getPosition());
                    strBuild.append(" AppVers:");
                    strBuild.append(fixVO.getApplicationVersion());
                    strBuild.append("]");
                } else {
                    strBuild.append("[FixdInfNtAvail.]");
                }
                strBuild.append("[TT:");
                strBuild.append(System.currentTimeMillis() - p_requestVO.getCreatedOn().getTime());
                strBuild.append(" ms]");
				
//GNOC INTEGRATION -  Request Gopal
		strBuild.append("[Source IP:");
                strBuild.append(p_requestVO.getRemoteIP());
                strBuild.append("]");
            }
            /*
             * else { strBuild.append(" [Request
             * ID:
             * ");strBuild.append(p_requestVO.getRequestIDStr() );strBuild.append("
             * ]"); strBuild.append("
             * [Creation Date:
             * ");strBuild.append(p_requestVO.getCreatedOn() );strBuild.append("
             * ]");
             * strBuild.append(" [Module:");strBuild.append(p_requestVO.getModule
             * () );strBuild.append("]");
             * strBuild.append(" [Instance ID:");strBuild.append(p_requestVO.
             * getInstanceID());
             * strBuild.append("]"); }
             */
            // strBuild.append("[EndReqOut:]");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append("MSISDN=");
            loggerValue.append(p_requestVO.getFilteredMSISDN());
            loggerValue.append("Exceptn :");
            loggerValue.append(e.getMessage());
            _log.error("generateMsgString", p_requestVO.getRequestIDStr(),loggerValue );
            StringBuilder eventhandle= new StringBuilder(); 
            eventhandle.append("Can'tWrite In Req LogFor Req.ID:");
            eventhandle.append(p_requestVO.getRequestID());
            eventhandle.append("& MSDN:");
            eventhandle.append(p_requestVO.getFilteredMSISDN());
            eventhandle.append(",Exception=");
            eventhandle.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChnlGatewReqLog[generateMsgStrng]", "", p_requestVO
                .getFilteredMSISDN(), "",  eventhandle.toString() );
        }
        return strBuild.toString();
    }// end of generateMessageString

    /**
     * Method to log the details in Request Log
     * 
     * @param p_requestVO
     */
    public static void outLog(RequestVO p_requestVO) {
        /*
         * try {
         */_log.info("", generateMessageString(p_requestVO));
        // }//end of try
        /*
         * catch(Exception e) { e.printStackTrace();
         * _log.error("log",p_requestVO.getRequestIDStr(),"
         * MSISDN="+p_requestVO.getFilteredMSISDN()+" Exception
         * :"+e.getMessage());
         * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
         * EventStatusI
         * .RAISED,EventLevelI.FATAL,"ChannelGatewayRequestLog[log]",
         * "",p_requestVO.getFilteredMSISDN(),"","Not
         * able to write in Request Log for Request
         * ID:"+p_requestVO.getRequestID()+" and
         * MSISDN:"+p_requestVO.getFilteredMSISDN()+" ,getting
         * Exception="+e.getMessage()); }//end of catch
         */}// end of log

    /**
     * Method to log the details of input stage,Request Log
     * 
     * @param p_requestVO
     */
    public static void inLog(RequestVO p_requestVO) {
        final StringBuilder strBuild = new StringBuilder();
        StringBuilder loggerValue= new StringBuilder(); 
        strBuild.append("[ReqIn]");
        strBuild.append("[RQID:").append(p_requestVO.getRequestIDStr()).append("]");
        // strBuild.append("[CRDT:").append(p_requestVO.getCreatedOn()).append("]");
        strBuild.append("[S:").append(p_requestVO.getRequestGatewayType()).append("]");
        strBuild.append("[MD:").append(p_requestVO.getModule()).append("]");

        if (!BTSLUtil.isNullString(p_requestVO.getIncomingSmsStr())) {
            strBuild.append("[DSMS:").append(p_requestVO.getIncomingSmsStr()).append("]");
        } else {
            strBuild.append("[DSMS:N.A]");
        }

        strBuild.append("[SMSISDN:").append(p_requestVO.getRequestMSISDN()).append("]");

        String msg = p_requestVO.getRequestMessage();
        String newmsg = "";
        if (!BTSLUtil.isNullString(msg)) {
            if (p_requestVO.getReqContentType().indexOf("xml") != -1 && (msg.indexOf("<PIN>") != -1 || msg.indexOf("<PASSWORD>") != -1)) {
                if (msg.indexOf("<PIN>") != -1) {
                    final String pin = msg.substring(msg.indexOf("<PIN>") + "<PIN>".length(), msg.indexOf("</PIN>"));
                    String newPin = "";
                    int pin1=pin.length();
                    for (int i = 0; i < pin1; i++) {
                        newPin = newPin + "*";
                    }
                    msg = msg.replace(pin, newPin);
                }
                if (msg.indexOf("<PASSWORD>") != -1) {
                    final String password = msg.substring(msg.indexOf("<PASSWORD>") + "<PASSWORD>".length(), msg.indexOf("</PASSWORD>"));
                    String newPass = "";
                    int passwords=password.length();
                    for (int i = 0; i <passwords ; i++) {
                        newPass = newPass + "*";
                    }
                    msg = msg.replace(password, newPass);
                }
                if (msg.indexOf("<PIN>") != -1) {
                    final String pin = msg.substring(msg.indexOf("<PIN>") + "<PIN>".length(), msg.indexOf("</PIN>"));
                    String newPin = "";
                   int pin2= pin.length();
                    for (int i = 0; i <pin2 ; i++) {
                        newPin = newPin + "*";
                    }
                    msg = msg.replace(pin, newPin);
                } if (msg.indexOf("<NEWPIN>") != -1) {
                    final String pin = msg.substring(msg.indexOf("<NEWPIN>") + "<NEWPIN>".length(), msg.indexOf("</NEWPIN>"));
                    String newPin2 = "";
                    int pin3=pin.length();
                    for (int i = 0; i <pin3 ; i++) {
                        newPin2 = newPin2 + "*";
                    }
                    msg = msg.replace(pin, newPin2);
                } if (msg.indexOf("<CONFIRMPIN>") != -1) {
                    final String pin = msg.substring(msg.indexOf("<CONFIRMPIN>") + "<CONFIRMPIN>".length(), msg.indexOf("</CONFIRMPIN>"));
                    String newPin3 = "";
                    int pin4=pin.length();
                    for (int i = 0; i <pin4 ; i++) {
                        newPin3 = newPin3 + "*";
                    }
                    msg = msg.replace(pin, newPin3);
                }
                newmsg = msg;
            } else {

                final String[] arr = msg.split(" ");

                final int length = arr.length;
                for (int i = 0; i < length; i++) {
                    if (i == length - 1) {
                        newmsg = newmsg + "****";
                    } else {
                        newmsg = newmsg + arr[i] + " ";
                    }
                }
            }

        }
        // strBuild.append("[REQMSG:").append(msg ).append("]");

    	String newmsgRe="";
		try{
			newmsgRe=newmsg.replaceAll("\n|\r", "");
		}catch (PatternSyntaxException e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			_log.debug("inLog", loggerValue);
	   	    _log.errorTrace("inLog", e);
			newmsgRe=newmsg;
		}
		strBuild.append("[REQMSG:" ).append( newmsgRe ).append( "]");

        strBuild.append("[ST:").append(p_requestVO.getSourceType()).append("]");
        strBuild.append("[SERPRT:").append(p_requestVO.getServicePort()).append("]");
        strBuild.append("[OINFO:").append(p_requestVO.getRequestGatewayCode()).append(",MsgReq=").append(p_requestVO.isUnmarkSenderUnderProcess()).append(",FT=").append(
            p_requestVO.getMessageGatewayVO().getFlowType()).append("ResTyp=").append(p_requestVO.getMsgResponseType()).append("]");

        // strBuild.append(" [EndReqInLog:]");
		
		//GNOC INTEGRATION ON REQUEST OF GOPAL
	strBuild.append("[Source IP:");
        strBuild.append(p_requestVO.getRemoteIP());
        strBuild.append("]");
        _log.info("", strBuild.toString());
    }
    
    public static void inLogIntermediate (RequestVO p_requestVO ,String stage) {
    	
    	if(Constants.getProperty("LOG_INTERMEDIATE") == null || "N".equalsIgnoreCase(Constants.getProperty("LOG_INTERMEDIATE"))) {
    		return ;
    	}
       _log.info("", generateMessageIntermediateString(p_requestVO,stage));     
}
    private static String generateMessageIntermediateString(RequestVO p_requestVO,String stage) {
        StringBuilder strBuild = new StringBuilder();
        final String METHOD_NAME = "generateMessageString";
        try {
        
            strBuild.append("[ReqBeforeIn:]");
            if (p_requestVO != null) {
                strBuild.append("[RQID:");                  
                strBuild.append(p_requestVO.getRequestIDStr());               	
                strBuild.append("]");           
                strBuild.append("[STAGE:");
                strBuild.append(stage);
                strBuild.append("]");
                strBuild.append("[S:").append(p_requestVO.getRequestGatewayType()).append("]");
                strBuild.append("[MD:").append(p_requestVO.getModule()).append("]");
                
            }
            strBuild.append("[TIME TAKEN:");
            strBuild.append(System.currentTimeMillis() - p_requestVO.getCreatedOn().getTime());
            strBuild.append(" ms]");
            strBuild.append("[Source IP:");
            strBuild.append(p_requestVO.getRemoteIP());
            strBuild.append("]");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateMsgString", p_requestVO.getRequestIDStr(), "MSISDN=" + p_requestVO.getFilteredMSISDN() + "Exceptn :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChnlGatewReqLog[generateMsgStrng]", "", p_requestVO
                .getFilteredMSISDN(), "", "Can'tWrite In Req LogFor Req.ID:" + p_requestVO.getRequestID() + "& MSDN:" + p_requestVO.getFilteredMSISDN() + ",Exception=" + e
                .getMessage());
        }
        return strBuild.toString();
    }
    
}
