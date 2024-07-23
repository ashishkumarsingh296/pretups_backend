package com.btsl.pretups.processes.clientprocesses.businesslogic;

/**
 * @(#)ReverseHirerachyCommisionDAO.java 
 * Copyright(c) 2017, Comviva technologies Ltd. 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * Vishal	 17 Oct 2017			Initial Creation
 * This DAO class will be used fetch the data from configured database
 *  related to generate the report for Reverse Hirerachy Commision.
 * 
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class ReverseHirerachyCommisionDAO
{
	private Log logger = LogFactory.getLog(this.getClass().getName());


	/**
	 * @description : This method will be used to list down the POS details for transaction for O2C & C2C on monthly wise.
	 * @author :Vishal
	 * @param  : p_con - connection with database
	 * @throws BTSLBaseException
	 */
	public ArrayList<ReverseHirerachyCommisionVO> fetchReverseHirerachyCommisionData(Connection pCon,Date pForDate)throws BTSLBaseException
	{
		String  methodName="fetchReverseHirerachyCommisionData"; 
		if(logger.isDebugEnabled())
			logger.debug(methodName,"Entered for Date" +pForDate);


		PreparedStatement prepSelect=null;
		ResultSet rs=null;
		StringBuilder qrySelect = null;
		ArrayList<ReverseHirerachyCommisionVO> hirerachalList= new ArrayList<ReverseHirerachyCommisionVO>();
		try
		{
			java.sql.Date forDate=BTSLUtil.getSQLDateFromUtilDate(pForDate);


			qrySelect = new StringBuilder(" SELECT THROUGH_USER ,BENEFICARY_PARENT_ID,TRANSACTION_DATE," +
					" COMM_PROFILE_SET_ID,ON_AMOUNT,FOC_AMOUNT, FILE_GENERATED," +
					"MSISDN from DUAL_WALLET_AUTO_C2C where TRANSACTION_DATE =TO_DATE('"+forDate+"','YYYY-MM-DD') ");


			if (logger.isDebugEnabled())
				logger.debug(methodName, "Select qrySelect:" + qrySelect.toString());

			prepSelect = pCon.prepareStatement(qrySelect.toString());

			rs = prepSelect.executeQuery();
			ReverseHirerachyCommisionVO reverseHirerachyCommisionVO=null;

			while(rs.next())
			{
				reverseHirerachyCommisionVO = new ReverseHirerachyCommisionVO();
				reverseHirerachyCommisionVO.setThroughUser(rs.getString("THROUGH_USER"));
				reverseHirerachyCommisionVO.setBeneficaryParentId(rs.getString("BENEFICARY_PARENT_ID"));
				reverseHirerachyCommisionVO.setTransactionDate(rs.getString("TRANSACTION_DATE"));
				reverseHirerachyCommisionVO.setCommProfileSetId(rs.getString("COMM_PROFILE_SET_ID"));
				reverseHirerachyCommisionVO.setOnAmount(rs.getDouble("ON_AMOUNT"));
				reverseHirerachyCommisionVO.setFocAmount(rs.getDouble("FOC_AMOUNT"));
				reverseHirerachyCommisionVO.setFileGenerated(rs.getString("FILE_GENERATED"));
				reverseHirerachyCommisionVO.setMsisdn(rs.getString("MSISDN"));
				hirerachalList.add(reverseHirerachyCommisionVO);


			}
		}
		catch(SQLException sql)
		{
			logger.error(methodName,"SQLException:="+sql.getMessage());
			logger.errorTrace(methodName, sql);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyReport4PosDAO[fetchReverseHirerachyCommisionData]", "", "", "", "SQLException:"+sql.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch(Exception e)
		{
			logger.error(methodName,"Exception := "+e.getMessage());
			logger.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyReport4PosDAO[fetchReverseHirerachyCommisionData]", "", "", "", "Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			if(logger.isDebugEnabled())
				logger.debug(methodName,"Exit The total list  "+hirerachalList.size());
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		logger.error("An error occurred closing result set.", e);
        	}
			try{
		        if (prepSelect!= null){
		        	prepSelect.close();
		        }
		    }
		    catch (SQLException e){
		    	logger.error("An error occurred closing statement.", e);
		    }
		
		}
		return hirerachalList;
	}
	
	/**
	 * @description : This method is used to fetch commission amount of user.
	 * @author : Anjali
	 * @param  : pCon - connection with database
	 * @param : pUserId - userId of user whose details are to be fetched.
	 * @param : noOfDays - no. of days details need to be fetched.
	 * @throws BTSLBaseException
	 */
	public long fetchCommissionOfUserThroughUserId(Connection pCon,String pUserId,int noOfDays) throws BTSLBaseException
	{

		String  methodName="fetchReverseHirerachyCommisionData"; 
		if(logger.isDebugEnabled())
			logger.debug(methodName,"Entered for User id: " +pUserId+" for Days: "+noOfDays);
		PreparedStatement prepSelect=null;
		ResultSet rs=null;
		StringBuilder qrySelect = null;
		long amount=0;
		try
		{
			Date currDate=new Date();
			Date toDate=BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(currDate, -noOfDays));
			Date fromDate=BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(currDate, -1));

			qrySelect = new StringBuilder(" SELECT SUM (foc_amount) comm_amount from DUAL_WALLET_AUTO_C2C where TRANSACTION_DATE >= TO_DATE('"+toDate+"','YYYY-MM-DD') and TRANSACTION_DATE <=TO_DATE('"+fromDate+"','YYYY-MM-DD') "
					+ " and BENEFICARY_PARENT_ID = ? ");
			if (logger.isDebugEnabled())
				logger.debug(methodName, "Select qrySelect:" + qrySelect.toString());
			prepSelect = pCon.prepareStatement(qrySelect.toString());
			int i=1;
			prepSelect.setString(i, pUserId);
			rs = prepSelect.executeQuery();
			if(rs.next())
			{
				amount=rs.getLong("comm_amount");
			}
		}
		catch(SQLException sql)
		{
			logger.errorTrace(methodName, sql);
			logger.error(methodName,"SQLException:="+sql);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReverseHirerachyCommisionDAO[fetchCommissionOfUserThroughUserId]", "", "", "", "SQLException:"+sql.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch(Exception e)
		{
			logger.errorTrace(methodName, e);
			logger.error(methodName,"Exception:="+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReverseHirerachyCommisionDAO[fetchCommissionOfUserThroughUserId]", "", "", "", "Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			if(logger.isDebugEnabled())
				logger.debug(methodName,"Exit amount:  "+amount);
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		logger.error("An error occurred closing result set.", e);
        	}
			try{
		        if (prepSelect!= null){
		        	prepSelect.close();
		        }
		    }
		    catch (SQLException e){
		    	logger.error("An error occurred closing statement.", e);
		    }
		}
		return amount;
	
	}

}