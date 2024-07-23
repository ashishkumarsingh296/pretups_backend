package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
/***
 * this Thread is responsible for processing Zero base and FNF request
 * @author 
 *
 */
public class ProcessZBAndFNFThread implements Runnable{
	
private C2STransferVO c2sTransferVO=null;
private Locale receiverLocale=null;
private static Log log = LogFactory.getLog(ProcessZBAndFNFThread.class.getName());

/***
 * constructor for thread
 * @param _c2sTransferVO
 * @param _receiverLocale
 */
public ProcessZBAndFNFThread(C2STransferVO c2sTransferVO,Locale receiverLocale)
{
	this.c2sTransferVO=c2sTransferVO;
	this.receiverLocale=receiverLocale;
}
/*
 * (non-Javadoc)
 * @see java.lang.Runnable#run()
 */
@Override	
public void run()
{
	final String methodName = "run";
	Connection con=null;MComConnectionI mcomCon = null;
	StringBuilder loggerValue= new StringBuilder(); 
	if (log.isDebugEnabled()) {
		loggerValue.setLength(0);
		loggerValue.append("Entered for ZB and FNF   ");
		loggerValue.append("sender MSIDN  ");
		loggerValue.append(c2sTransferVO.getSenderMsisdn());
		loggerValue.append(" receiver MSISDN  ");
		loggerValue.append(c2sTransferVO.getReceiverMsisdn());
        log.debug(methodName,loggerValue );
    }
	
	try
	{
		mcomCon = new MComConnection();con=mcomCon.getConnection();
	PushMessage pushMessages=null;
	PushMessage pushMessagesFnF=null;
	ArrayList<String> fnfList=null;
	String type =null;
	
	
	if(receiverLocale==null)
		receiverLocale=new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
	// Check for ZB and FNFmapping.	
	fnfList=mappingFoundForFNF(con);
	
	//push message for FNF
	if(!fnfList.isEmpty())
	{
		 int fnfLists=fnfList.size();
		for(int i=0;i<fnfLists;i++)
		{
			 type=fnfList.get(i);
        String []array=type.split("_");
        if(array[1].equals(PretupsI.FNF_TYPE)){
        	pushMessages=null;
        	//sending message to Reciever
    		pushMessages = new PushMessage(c2sTransferVO.getReceiverMsisdn(), getMSISDN1MessageForFNF(array[0],PretupsErrorCodesI.MSISDN2_MESSAGE_FOR_FNF), c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(),
    				receiverLocale);
    		pushMessage(pushMessages);
    	//Sending Message to the mapped msisdn
		pushMessagesFnF=new PushMessage(array[0], getMSISDN1MessageForFNF(c2sTransferVO.getReceiverMsisdn(),PretupsErrorCodesI.MSISDN1_MESSAGE_FOR_FNF), c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(),
				receiverLocale);
		pushMessage(pushMessagesFnF);
        }
      //Sending Message to the mapped msisdn
        else if(array[1].equals(PretupsI.ZB_TYPE)){
        	pushMessages=null;
        	pushMessages = new PushMessage(array[0], getMessageForZB(), c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(),
    				receiverLocale);
    		pushMessage(pushMessages);
        }
       
		}
	
	}		
			
	}
	catch (BTSLBaseException be) {
        log.errorTrace(methodName, be);
    } catch (Exception e) {
        log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessZBAndFNFThread[run]", c2sTransferVO.getTransferID(),
        		c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(), "Exception is" + e.getMessage());

    }
	finally
	{
		if(mcomCon != null){mcomCon.close("ProcessZBAndFNFThread#run");mcomCon=null;}
		
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("existing  for ZB and FNF   ");
			loggerValue.append("sender MSIDN= " );
			loggerValue.append(c2sTransferVO.getSenderMsisdn() );
			loggerValue.append(" receiver MSISDN= " );
			loggerValue.append(c2sTransferVO.getReceiverMsisdn());
	        log.debug(methodName,loggerValue);
	    }
}

}

/***
 * 
 * @return
 */
private String getMessageForZB()
{
	   final String[] messageArgArray = {c2sTransferVO.getReceiverMsisdn()  };
        return BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.MESSAGE_FOR_ZB, messageArgArray);
        
}
/***
 * 
 * @return
 */

private String getMSISDN1MessageForFNF(String MSISDN, String messageCode)
{
	   final String[] messageArgArray = { MSISDN };
        return BTSLUtil.getMessage(receiverLocale, messageCode, messageArgArray);
        
}

/***
 * This method is used to find the mapping of subscriber for FNF
 * @param con
 * @param type
 * @return
 * @throws BTSLBaseException
 */
private ArrayList<String> mappingFoundForFNF(Connection con) throws BTSLBaseException
{
	final String methodName = "mappingFoundForFNF";
	StringBuilder loggerValue= new StringBuilder(); 
	if (log.isDebugEnabled()) {
		loggerValue.setLength(0);
		loggerValue.append("Entered for  FNF   ");
		loggerValue.append(" receiver MSISDN is ");
		loggerValue.append(c2sTransferVO.getReceiverMsisdn());
        log.debug(methodName,loggerValue);
    }
	ResultSet rs=null;
	PreparedStatement pstmt=null;
	ArrayList<String> fnfList= new ArrayList<>();
	try
	{
		final StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append("SELECT MSISDN2,RECORD_TYPE from FNF_ZERO_BASE_CUSTOMER where MSISDN1=? ");
		String selectQuery=selectQueryBuff.toString();
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("select query:" );
			loggerValue.append(selectQuery);
			log.debug(methodName, loggerValue);
			
        }
		pstmt = con.prepareStatement(selectQuery);
		pstmt.setString(1,c2sTransferVO.getReceiverMsisdn());
		rs=pstmt.executeQuery();
		while(rs.next())
		{
			fnfList.add(rs.getString("MSISDN2")+"_"+rs.getString("RECORD_TYPE"));	
		}
		
		
	}
	catch (SQLException sqle) {
		loggerValue.setLength(0);
		loggerValue.append("SQLException ");
		loggerValue.append(sqle.getMessage());
		log.error(methodName,  loggerValue);
        
        log.errorTrace(methodName, sqle);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessZBAndFNFThread[mappingFoundForFNF:]",
        		c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(), "SQL Exception:" + sqle.getMessage());
        throw new BTSLBaseException(this, "mappingFoundForFNF", "error.general.sql.processing");
    }
	catch (Exception e) {
		log.error(methodName, "Exception " + e.getMessage());
      
		log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessZBAndFNFThread[mappingFoundForFNF]",
        		c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(), "Exception:" + e.getMessage());
        throw new BTSLBaseException(this, "mappingFoundForFNF;", "error.general.processing");
    }
	finally
	{
		try {
            if (pstmt != null) {
            	pstmt.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
        try {
            if (rs != null) {
            	rs.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
        if (log.isDebugEnabled()) {
            log.debug(
                methodName,
                "exiting  mapping found for FNF list size is "  + fnfList.size() );
        }
		
	}
	return fnfList;
	
}

private void pushMessage(PushMessage pushMessage)
{
	final String  methodname="pushMessage";
	try
	{
	
		pushMessage.push();
	}
	catch (Exception e)
	{
		log.error(methodname, "Exception " + e.getMessage());
		 log.errorTrace(methodname, e);
	}
	
}

}
