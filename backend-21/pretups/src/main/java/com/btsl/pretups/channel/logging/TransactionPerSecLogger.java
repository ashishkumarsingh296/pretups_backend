package com.btsl.pretups.channel.logging;

import java.sql.Connection;
import java.sql.SQLException;

/*@(#)TransactionPerSecLogger.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Mohd Suhel           21/11/2017         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the balance related Logs for channel user
 */


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class TransactionPerSecLogger implements Runnable{

	
	private static Log log = LogFactory.getFactory().getInstance(TransactionPerSecLogger.class.getName());
	
	private static final Map<Date , Integer> tpsMap = new HashMap<>();
	private static volatile long lastTPSSavedTimeSeconds = 0;
	private static volatile int currentSecondTPS = 0;
	private static volatile long lastTPSChangedTimeSeconds = 0;
	private RequestVO localRequestVO= null;
	private long localMilliSeconds = 0;
	
	
	public TransactionPerSecLogger() { }
	
	public TransactionPerSecLogger(long milliseconds,RequestVO requestVO) { 
				localRequestVO = requestVO;
				localMilliSeconds = milliseconds;
	}
	
	public void run(){
	
	if (log.isDebugEnabled()) {
                log.debug("run", "Run Method Entered of Logger Thread");
            }
			
		log(localMilliSeconds,localRequestVO);
		
	if (log.isDebugEnabled()) {
                log.debug("run", "Run Method Exited of Logger Thread");
            }
	}
	 
	/**
	 * @param milliseconds
	 * @param requestVO
	 */
	public static synchronized void log(long milliseconds,RequestVO requestVO){
	
		StringBuilder strBuff = new StringBuilder();
		String resultDateTime =null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date resultDate =null;
		long timeInSecondsToSave = 3600;
		
		 if (log.isDebugEnabled()) {
                log.debug("log", "Entered");
            }
			
		if(!BTSLUtil.isNullString(Constants.getProperty("TPS_LOG_SAVE_TIME")))
			timeInSecondsToSave = Long.parseLong(Constants.getProperty("TPS_LOG_SAVE_TIME"));
			
		
		try {
		
				long seconds = milliseconds/1000;
				// Code Added by Suhel
				if(lastTPSSavedTimeSeconds == 0 )
					lastTPSSavedTimeSeconds = seconds;

				if(lastTPSChangedTimeSeconds == 0 )
					lastTPSChangedTimeSeconds = seconds;
					
				if(seconds - lastTPSChangedTimeSeconds == 0 )
					{
					currentSecondTPS = currentSecondTPS + 1;
				}
				else
					{
					resultDate = new Date(lastTPSChangedTimeSeconds*1000);
					resultDateTime = sdf.format(resultDate);
					
					tpsMap.put(BTSLUtil.getDateTimeFromDateTimeString(resultDateTime,"dd/MM/yyyy HH:mm:ss"),currentSecondTPS);
					
					
					strBuff.append(BTSLDateUtil.getSystemLocaleDate(resultDateTime, "dd/MM/yyyy HH:mm:ss") + " : "+currentSecondTPS);
					
					log.info("",strBuff.toString());
					
					currentSecondTPS = 1;
					lastTPSChangedTimeSeconds = seconds;

				}
					
								
				if(seconds - lastTPSSavedTimeSeconds >= timeInSecondsToSave )
				{
					lastTPSSavedTimeSeconds = seconds;
					
					if(insertTPSDetails(tpsMap,requestVO)>0)
						tpsMap.clear();
					else{
						log.error("TPS INSERTION "," Not Able to Insert TPS Details");
					}
					
				}
				
				//Ended by Suhel
				
			

		} catch (Exception e) {
			
			log.error("log", milliseconds, " Exception :"
					+ e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransactionPerSecLogger[log]",String.valueOf(milliseconds),"","","Not able to log info for transaction at (in milliseconds ):"+milliseconds+" ,getting Exception="+e.getMessage());
		}
		
		
	}
	
	/**
	 * @param pTpsMap
	 * @param requestVO
	 * @return
	 */
	private static int insertTPSDetails(Map<Date,Integer> pTpsMap , RequestVO requestVO){
		
		String methodName = "insertTPSDetails";
		
		
		
		C2STransferDAO c2sTransferDAO = new C2STransferDAO();
		Connection con = null;
		
		int addCount=-1;
		
		try{
		con = OracleUtil.getSingleConnection();
			if(pTpsMap.size()>0)
				{
				addCount =  c2sTransferDAO.insertTPSDetails(con , pTpsMap , requestVO);
				con.commit();
			}
		}
		catch(BTSLBaseException be){
			try{
				if(con!=null)
					con.rollback();
			}
			catch(Exception e){
				log.error(methodName,  be.getMessage());
			}
			
				
			log.error(methodName, be.getMessage());
		}
		catch(SQLException se){
			log.error(methodName,se.getMessage());
		}
		finally{
			try{
				if(con!=null){
					con.close();
				}
			}
			catch(Exception e)
			{
				log.error(methodName,"Exception : "+e.getMessage());
			}
			
			c2sTransferDAO = null;
		}		
		return addCount;
		
		
	}
}
