package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
import com.btsl.pretups.channel.transfer.businesslogic.LowBasedAndFNFRechargeVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
/***
 * this thread is responsible for processing low base request
 * @author 
 *
 */
public class ProcessLowBaseThread implements Runnable{
	
	private C2STransferVO c2sTransferVO=null;
	private Locale senderLocale=null;
	private static Log log = LogFactory.getLog(ProcessLowBaseThread.class.getName());
	
	/***
	 * 
	 * @param _c2sTransferVO
	 * @param _senderLocale
	 */

	public ProcessLowBaseThread(C2STransferVO c2sTransferVO,Locale senderLocale )
	{
		this.c2sTransferVO=c2sTransferVO;
		this.senderLocale=senderLocale;
	}
/***
 * 
 */	
	@Override
	public void run()
	{
		final String methodName = "run";
		Connection con=null;MComConnectionI mcomCon = null;
		LowBasedAndFNFRechargeVO lowBasedRechargeVO=null;
		if(senderLocale==null)
			senderLocale=new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		PushMessage pushMessages=null;
		boolean updateRequired=false;
		if (log.isDebugEnabled()) {
	        log.debug(
	            methodName,
	            "Entered for low based   "+ "sender MSIDN is " +c2sTransferVO.getSenderMsisdn() + " receiver MSISDN is " +c2sTransferVO.getReceiverMsisdn() );
	    }
		
		try
		{
			mcomCon = new MComConnection();con=mcomCon.getConnection();
		lowBasedRechargeVO=mappingFoundForLB(con);
		if(lowBasedRechargeVO!=null)
		{
           loadDetailsForLB(con,lowBasedRechargeVO);
			if(c2sTransferVO.getTransferValue()>=lowBasedRechargeVO.getMinTransferValue()||lowBasedRechargeVO.isIsExists())
			{
				
				if(lowBasedRechargeVO.isIsExists())
				{
					if(lowBasedRechargeVO.getAmount()<=lowBasedRechargeVO.getMaxTrnasferValue())
					{
					lowBasedRechargeVO.setCount(lowBasedRechargeVO.getCount()+1);
					lowBasedRechargeVO.setAmount(lowBasedRechargeVO.getAmount()+c2sTransferVO.getTransferValue());
					updateRequired=true;
					}
					else
					{
						updateRequired=false;
					}
				}
				else
				{
					updateRequired=true;
					lowBasedRechargeVO.setCount(0);
					lowBasedRechargeVO.setAmount(0);
				}
				if(updateRequired)
				updateDetailsForLB(con,lowBasedRechargeVO);	
			}
			else
			{
				pushMessages = new PushMessage(c2sTransferVO.getSenderMsisdn(), getMessageForLOWBase(lowBasedRechargeVO), c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(),
						senderLocale);
				pushMessages.push();
				
			}
			
	}		
		}
		catch (BTSLBaseException be) {
	        log.errorTrace(methodName, be);
	    } catch (Exception e) {
	        log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessLowBaseThread[run]", c2sTransferVO.getTransferID(),
	        		c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode()," "  + e.getMessage());

	    }
		finally
		{
			if(mcomCon != null){mcomCon.close("ProcessLowBaseThread#run");mcomCon=null;}
			
			if (log.isDebugEnabled()) {
		        log.debug(
		            methodName,
		            "existing  for lowbased   "+ "sender MSIDN is " +c2sTransferVO.getSenderMsisdn() + " receiver MSISDN is " +c2sTransferVO.getReceiverMsisdn() );
		    }
	}

}
/***
 * 
 * @param con
 * @return
 * @throws BTSLBaseException
 */
	
	private LowBasedAndFNFRechargeVO mappingFoundForLB(Connection con) throws BTSLBaseException
	{
		final String methodName = "mappingFoundForLB";
		if (log.isDebugEnabled()) {
	        log.debug(
	            methodName,
	            "Entered MSISDN"+ c2sTransferVO.getReceiverMsisdn() );
	    }
		ResultSet rs=null;
		PreparedStatement pstmt=null;
		LowBasedAndFNFRechargeVO lowBasedRechargeVO=null;
		
		try
		{
			final StringBuilder selectQueryBuff = new StringBuilder();
			selectQueryBuff.append("SELECT MIN_RECH_AMOUNT,MAX_RECH_AMOUNT from LOW_BASE_CUSTOMER where CUSTOMER_MSISDN=? ");
			String selectQuery=selectQueryBuff.toString();
			if (log.isDebugEnabled()) {
				log.debug(methodName, "select query:" + selectQuery);
				
	        }
			pstmt = con.prepareStatement(selectQuery);
			pstmt.setString(1,c2sTransferVO.getReceiverMsisdn());
			rs=pstmt.executeQuery();
			while(rs.next())
			{
				lowBasedRechargeVO=new LowBasedAndFNFRechargeVO();
				lowBasedRechargeVO.setMinTransferValue(rs.getLong("MIN_RECH_AMOUNT"));
				lowBasedRechargeVO.setMaxTrnasferValue(rs.getLong("MAX_RECH_AMOUNT"));
			}
			
			
		}
		catch (SQLException sqle) {
			log.error(methodName, "SQLException during mappingFoundForLB " + sqle.getMessage());
	        
	        log.errorTrace(methodName, sqle);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessLowBaseThread[mappingFoundForLB]",
	        		c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(), "SQL Exception in :" + sqle.getMessage());
	        throw new BTSLBaseException(this, methodName, "error.general.sql.processing.in");
	    }
		catch (Exception e) {
			log.error(methodName, "Exception in" + methodName + e.getMessage());
	      
			log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessLowBaseThread[mappingFoundForLB]",
	        		c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(), "Exception name:" + e.getMessage());
	        throw new BTSLBaseException(this, "mappingFoundForLB", "error.general.processing in" +methodName);
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
	                "exiting  mapping found for low base " );
	        }
			
		}
		return lowBasedRechargeVO;
		
	}
/***
 * 
 * @param con
 * @param lowBasedRechargeVO
 * @return
 * @throws BTSLBaseException
 */	
	
	
	private LowBasedAndFNFRechargeVO loadDetailsForLB(Connection con,LowBasedAndFNFRechargeVO lowBasedRechargeVO) throws BTSLBaseException
	{
		final String methodName = "loadDetailsForLB";
		if (log.isDebugEnabled()) {
	        log.debug(
	            methodName,
	            "Entered Receiver MSISDN"+ c2sTransferVO.getReceiverMsisdn() );
	    }
		ResultSet rs=null;
		PreparedStatement pstmt=null;
		
		Date date= new Date();
		
		try
		{
			final StringBuilder selectQueryBuff = new StringBuilder();
			selectQueryBuff.append("SELECT count,amount from CUST_RET_COUNT where CUSTOMER_MSISDN=? and RETAILER_MSISDN=? and LAST_TXN_DATE=? ");
			String selectQuery=selectQueryBuff.toString();
			if (log.isDebugEnabled()) {
				log.debug(methodName, "select query:" + selectQuery);
				
	        }
			pstmt = con.prepareStatement(selectQuery);
			pstmt.setString(1,c2sTransferVO.getReceiverMsisdn());
			pstmt.setString(2,c2sTransferVO.getSenderMsisdn());
			pstmt.setDate(3,BTSLUtil.getSQLDateFromUtilDate(date));
			rs=pstmt.executeQuery();
			while(rs.next())
			{
				lowBasedRechargeVO.setCount(rs.getLong("count"));
				lowBasedRechargeVO.setAmount(rs.getLong("amount"));
				lowBasedRechargeVO.setIsExists(true);
			}
			
			
		}
		catch (SQLException sqle) {
			log.error(methodName, "SQLException " + sqle.getMessage());
	        
	        log.errorTrace(methodName, sqle);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessLowBaseThread[loadDetailsForLB]",
	        		c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(), "SQL Exception:" + sqle.getMessage());
	        throw new BTSLBaseException(this, "mappingFound", "error.general.sql.processing");
	    }
		catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
	      
			log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessLowBaseThread[loadDetailsForLB]",
	        		c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(), "Exception:" + e.getMessage());
	        throw new BTSLBaseException(this, "mappingFound", "error.general.processing");
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
	                "exiting  loadDetailsForLB " );
	        }
			
		}
		return lowBasedRechargeVO;
		
	}
/***
 * 
 * @param con
 * @param lowBasedRechargeVO
 * @throws BTSLBaseException
 */
	
	private void  updateDetailsForLB(Connection con,LowBasedAndFNFRechargeVO lowBasedRechargeVO) throws BTSLBaseException
	{
		final String methodName = "updateDetailsForLB";
		if (log.isDebugEnabled()) {
	        log.debug(
	            methodName,
	            "Entered Receiver MSISDN"+ c2sTransferVO.getReceiverMsisdn() );
	    }
		PreparedStatement pstmt=null;
		PreparedStatement pstmt1=null;
		
		Date date= new Date();
		int updatecount=0;
		
		try
		{
			final StringBuilder upadteQueryBuff = new StringBuilder();
			final StringBuilder insertQueryBuff = new StringBuilder();
			final StringBuilder updateC2STransferQueryBuff = new StringBuilder();
			upadteQueryBuff.append("update CUST_RET_COUNT set COUNT=?,AMOUNT=? where CUSTOMER_MSISDN=? and RETAILER_MSISDN=? and LAST_TXN_DATE=?  ");
			
			insertQueryBuff.append(" insert into CUST_RET_COUNT ( CUSTOMER_MSISDN,RETAILER_MSISDN,COUNT,AMOUNT,FIRST_TXN_DATE,LAST_TXN_DATE,STATUS )  ");
			insertQueryBuff.append(" values(?,?,?,?,?,?,?) ");
			//local index implemented
			updateC2STransferQueryBuff.append(" update c2s_transfers set  LOW_BASED_RECHARGE='Y' where transfer_id=? and transfer_date=? ");
			
			String updateQuery=upadteQueryBuff.toString();
			if (log.isDebugEnabled()) {
				log.debug(methodName, "update query:" + updateQuery);
				
	        }
			String insertQuery=insertQueryBuff.toString();
			if (log.isDebugEnabled()) {
				log.debug(methodName, "insert query:" + insertQuery);
				
	        }
			String updateQueryC2S=updateC2STransferQueryBuff.toString();
			
			if (log.isDebugEnabled()) {
				log.debug(methodName, "c2s transfer update query:" + updateQueryC2S);
				
	        }
			if(lowBasedRechargeVO.isIsExists())
			{
				pstmt = con.prepareStatement(updateQuery);
				pstmt.setLong(1,lowBasedRechargeVO.getCount());
				pstmt.setLong(2,lowBasedRechargeVO.getAmount());
				pstmt.setString(3,c2sTransferVO.getReceiverMsisdn());
				pstmt.setString(4,c2sTransferVO.getSenderMsisdn());
				pstmt.setDate(5,BTSLUtil.getSQLDateFromUtilDate(date));
				pstmt1 = con.prepareStatement(updateQueryC2S);
				pstmt1.setString(1,c2sTransferVO.getTransferID());
				pstmt1.setDate(2, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(c2sTransferVO.getTransferID())));
			}
			else
			{
				pstmt = con.prepareStatement(insertQuery);
				pstmt.setString(1,c2sTransferVO.getReceiverMsisdn());
				pstmt.setString(2,c2sTransferVO.getSenderMsisdn());
				pstmt.setLong(3,lowBasedRechargeVO.getCount());
				pstmt.setLong(4,lowBasedRechargeVO.getAmount());
				pstmt.setDate(5,BTSLUtil.getSQLDateFromUtilDate(date));
				pstmt.setDate(6,BTSLUtil.getSQLDateFromUtilDate(date));
				pstmt.setString(7,"Y");
			}
			
			updatecount=pstmt.executeUpdate();
			if(updatecount>0 && pstmt1!=null)
			{
				updatecount=pstmt1.executeUpdate();
			}
			
			if(updatecount>0)
			con.commit();
			else
			con.rollback();	
			
		}
		catch (SQLException sqle) {
			log.error(methodName, "SQLException " + sqle.getMessage());
	        
	        log.errorTrace(methodName, sqle);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessLowBaseThread["+methodName+"]",
	        		c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(), "SQL Exception:" + sqle.getMessage());
	        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	    }
		catch (Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
	      
			log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessLowBaseThread[updateDetailsForLB]",
	        		c2sTransferVO.getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(), "Exception:" + e.getMessage());
	        throw new BTSLBaseException(this, "updateDetailsForLB", "error.general.processing");
	    }
		finally
		{
			
			try {
	            if (pstmt1 != null) {
	            	pstmt1.close();
	            }
	            if (pstmt != null) {
	            	pstmt.close();
	            }
	        } catch (Exception e) {
	        	log.errorTrace(methodName, e);
	        }
			
			
	        
	        if (log.isDebugEnabled()) {
	            log.debug(
	                methodName,
	                "exiting  update count "  +updatecount);
	        }
			
		}
		
		
	}
	
/***
 * 
 * @param lowBasedRechargeVO
 * @return
 */	
	private String getMessageForLOWBase(LowBasedAndFNFRechargeVO lowBasedRechargeVO)
	{
		final String[] messageArgArray=new String[5];
		
		  if(!BTSLUtil.isNullString(c2sTransferVO.getSID()))
		  {
		   messageArgArray[0] =c2sTransferVO.getSID();
		   messageArgArray[1]= String.valueOf(lowBasedRechargeVO.getMinTransferValue()/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()) ;
		 
		  }else
		  {
			 messageArgArray[0] = c2sTransferVO.getReceiverMsisdn();
			 messageArgArray[1]	= String.valueOf(lowBasedRechargeVO.getMinTransferValue()/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue())  ;
		  }   
		  return BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.MSISDN_MESSAGE_FOR_LB, messageArgArray);
	        
	}
	
	

}
