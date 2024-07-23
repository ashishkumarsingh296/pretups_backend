/* @# NetworkSummaryDAO.java
 *
 *		Created by         Created on 								History
 *	--------------------------------------------------------------------------------
 * 		Vikas Chaudhary		Mar 8, 2016			   Initial creation
 *	--------------------------------------------------------------------------------
 *  Copyright(c) 2016 Mahindra Comviva.
 */
package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;

/**
 * This class file is used for loading Network Summary Data List 
 */

public class NetworkSummaryDAO
{
	/**
	 * Field _log.
	 */
	private static final  Log _log = LogFactory.getLog(NetworkSummaryDAO.class.getName());
	private NetworkSummaryQry networkSummaryQry;
	/**
	 * Constructor for NetworkSummaryDAO.
	 */
	public NetworkSummaryDAO()
	{
		networkSummaryQry = (NetworkSummaryQry)ObjectProducer.getObject(QueryConstants.NETWORK_SUMMARY_QRY, QueryConstants.QUERY_PRODUCER);
	}
/**
 * This funtion  is used for loading data from db and return an array list. 
 */
	 public ArrayList<TransactionSummaryVO> loadNetworkSummaryDataList(String reportType, String networkCode,String from, String to, String currentDate) throws BTSLBaseException,SQLException
	 {
		 final String METHOD_NAME = "loadNetworkSummaryDataList"; 
		if(_log.isDebugEnabled())
			_log.debug("loadNetworkSummaryDataList","Entered " + "reportType : "+reportType +"networkCode : "+networkCode +" from : "+from + " to : "+ to + " currentDate : "+currentDate);
		PreparedStatement pstmtSelect=null;
		ArrayList<TransactionSummaryVO> networkSummaryDataList =new ArrayList<>();
		Connection con=null;
		MComConnectionI mcomCon = null;
		ResultSet rst=null;
		try
		{
			StringBuilder selectQueryBuff=null;
			//PretupsBL.getDisplayAmount(p_amount)
			
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			if(PretupsI.HOURLY_FILTER.equals(reportType)) {
				selectQueryBuff = new StringBuilder("SELECT TS.TRANS_TIME ,N.NETWORK_NAME,TS.GATEWAYCODE,C.CATEGORY_NAME,ST.NAME AS SERVICE_TYPE,SSM.SELECTOR_NAME,TS.INTERFACE_ID, ");
				selectQueryBuff.append(" SUM(TS.SUCCESS_COUNT) AS SUCCESS_COUNT, SUM(TS.ERROR_COUNT) AS ERROR_COUNT,SUM(TS.RECEIVER_TRANSFER_VALUE) AS RECEIVER_TRANSFER_VALUE, SUM(TS.ACCESS_FEE) AS ACCESS_FEE, ");
				selectQueryBuff.append(" SUM(TS.TAX_AMOUNT) AS TAX_AMOUNT, SUM(TS.ERROR_AMT) AS ERROR_AMT, SUM(TS.SUCCESS_AMT) AS SUCCESS_AMT");
				selectQueryBuff.append(" FROM TRANSACTION_SUMMARY TS,NETWORKS N, SERVICE_TYPE ST,CATEGORIES C,SERVICE_TYPE_SELECTOR_MAPPING SSM ");
				selectQueryBuff.append(" WHERE TS.NETWORK_CODE=? AND N.NETWORK_CODE=TS.NETWORK_CODE AND C.CATEGORY_CODE=TS.CATEGORY AND ST.SERVICE_TYPE=TS.SERVICE_TYPE AND TS.SERVICE_TYPE=SSM.SERVICE_TYPE AND TS.SUB_SERVICE=SSM.SELECTOR_CODE AND TS.TRANS_DATE=TO_DATE(?,'dd-mm-yy') ");
				selectQueryBuff.append(" GROUP BY TS.TRANS_TIME,N.NETWORK_NAME,TS.GATEWAYCODE,C.CATEGORY_NAME,ST.NAME,SSM.SELECTOR_NAME,TS.INTERFACE_ID");
				selectQueryBuff.append(" ORDER  BY TS.TRANS_TIME,N.NETWORK_NAME,TS.GATEWAYCODE,C.CATEGORY_NAME,ST.NAME,SSM.SELECTOR_NAME,TS.INTERFACE_ID");
			}
			else if(PretupsI.DAILY_FILTER.equals(reportType)) {
				selectQueryBuff = new StringBuilder("SELECT TS.TRANS_DATE,N.NETWORK_NAME,TS.GATEWAYCODE,C.CATEGORY_NAME,ST.NAME AS SERVICE_TYPE,SSM.SELECTOR_NAME,TS.INTERFACE_ID, SUM(TS.SUCCESS_COUNT) AS SUCCESS_COUNT, SUM(TS.ERROR_COUNT) AS ERROR_COUNT, ");
				selectQueryBuff.append(" SUM(TS.RECEIVER_TRANSFER_VALUE) AS RECEIVER_TRANSFER_VALUE, SUM(TS.ACCESS_FEE) AS ACCESS_FEE,SUM(TS.TAX_AMOUNT) AS TAX_AMOUNT, SUM(TS.ERROR_AMT) AS ERROR_AMT, SUM(TS.SUCCESS_AMT) AS SUCCESS_AMT ");
				selectQueryBuff.append(" FROM TRANSACTION_SUMMARY TS,NETWORKS N, SERVICE_TYPE ST,CATEGORIES C,SERVICE_TYPE_SELECTOR_MAPPING SSM ");
				selectQueryBuff.append(" WHERE TS.NETWORK_CODE=? AND N.NETWORK_CODE=TS.NETWORK_CODE AND C.CATEGORY_CODE=TS.CATEGORY AND ST.SERVICE_TYPE=TS.SERVICE_TYPE AND TS.SERVICE_TYPE=SSM.SERVICE_TYPE AND TS.SUB_SERVICE=SSM.SELECTOR_CODE AND TS.TRANS_DATE>=TO_DATE(?,'dd/mm/yy') AND TRANS_DATE<=TO_DATE(?,'dd-mm-yy')");
				selectQueryBuff.append(" GROUP BY TS.TRANS_DATE,N.NETWORK_NAME,TS.GATEWAYCODE,C.CATEGORY_NAME,ST.NAME,SSM.SELECTOR_NAME,TS.INTERFACE_ID");
				selectQueryBuff.append(" ORDER BY TS.TRANS_DATE,N.NETWORK_NAME,TS.GATEWAYCODE,C.CATEGORY_NAME,ST.NAME,SSM.SELECTOR_NAME,TS.INTERFACE_ID");
			}
			else if(PretupsI.MONTHLY_FILTER.equals(reportType)) {
				selectQueryBuff = networkSummaryQry.loadNetworkSummaryDataListQry();
				}
			
			String selectQuery=selectQueryBuff.toString();
			
			if (_log.isDebugEnabled())
				_log.debug(METHOD_NAME,"Select Query= "+selectQuery);
			
			pstmtSelect = con.prepareStatement(selectQuery);
			if(PretupsI.HOURLY_FILTER.equals(reportType)) {
				pstmtSelect.setString(1, networkCode);
				pstmtSelect.setString(2, currentDate);
			}
			else{
				pstmtSelect.setString(1, networkCode);
				pstmtSelect.setString(2, from);
				pstmtSelect.setString(3, to);
			}
			
			rst = pstmtSelect.executeQuery();
			TransactionSummaryVO sumObj = new TransactionSummaryVO();
            sumObj.setType("SUB TOTAL");
			while(rst.next())
			{
				int size = networkSummaryDataList.size();
				if(size>0) {
					TransactionSummaryVO tmpObj = networkSummaryDataList.get(size - 1);
	          		//Hourly
		            if(PretupsI.HOURLY_FILTER.equals(reportType)) {
		            	if(!(("SUB TOTAL").equals(tmpObj.getType())) && (tmpObj.getTransTime()!=(rst.getInt("TRANS_TIME")))) {
		            		networkSummaryDataList.add(sumObj);
		            		sumObj =  new TransactionSummaryVO();
		            		sumObj.setType("SUB TOTAL");
		            	}
		            }
		            //Monthly
		            else if (PretupsI.MONTHLY_FILTER.equals(reportType)) {
		            	String rstMonthDate=rst.getString("MONTH");
		            	if(!(("SUB TOTAL").equals(tmpObj.getType())) && !(tmpObj.getTransMonth().equals(rst.getString("MONTH")))) {
		            		networkSummaryDataList.add(sumObj);
		            		sumObj =  new TransactionSummaryVO();
		            		sumObj.setType("SUB TOTAL");
		            	}
		            }
		            //Daily
		            else if (PretupsI.DAILY_FILTER.equals(reportType)) {
		            	String rstMonthDate=rst.getString("TRANS_DATE");
		            	if(!(("SUB TOTAL").equals(tmpObj.getType())) && !(tmpObj.getTransDate().equals(rst.getString("TRANS_DATE")))) {
		            		networkSummaryDataList.add(sumObj);
		            		sumObj =  new TransactionSummaryVO();
		            		sumObj.setType("SUB TOTAL");
		            	}
		            }
				}

				TransactionSummaryVO obj = new TransactionSummaryVO();
				if(PretupsI.DAILY_FILTER.equals(reportType)) {
					obj.setTransDate(rst.getString("TRANS_DATE"));
					sumObj.setTransDate(rst.getString("TRANS_DATE"));
					obj.setTimeDateMonth(rst.getString("TRANS_DATE"));
					sumObj.setTimeDateMonth(rst.getString("TRANS_DATE"));
					
				}
				else if (PretupsI.MONTHLY_FILTER.equals(reportType)) {
					obj.setTransMonth(rst.getString("MONTH"));
					sumObj.setTransMonth(rst.getString("MONTH"));
					obj.setTimeDateMonth(rst.getString("MONTH"));
					sumObj.setTimeDateMonth(rst.getString("MONTH"));
					obj.setTransYear(rst.getInt("YEAR"));
					sumObj.setTransYear(rst.getInt("YEAR"));
					
				}
				else {
					obj.setTransTime(rst.getInt("TRANS_TIME"));
					obj.setTimeDateMonth(Integer.toString(rst.getInt("TRANS_TIME")));
					sumObj.setTransTime(rst.getInt("TRANS_TIME"));
					sumObj.setTimeDateMonth(Integer.toString(rst.getInt("TRANS_TIME")));
				}
				obj.setNetworkName(rst.getString("NETWORK_NAME"));
				obj.setGatewayCode(rst.getString("GATEWAYCODE"));
				obj.setCategory(rst.getString("CATEGORY_NAME"));
				obj.setType(rst.getString("SERVICE_TYPE"));
				obj.setSubService(rst.getString("SELECTOR_NAME"));
				obj.setInterfaceId(rst.getString("INTERFACE_ID"));

				obj.setSuccesfulRecharges(rst.getLong("SUCCESS_COUNT"));
				

				obj.setRechargesDenoms(Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("SUCCESS_AMT"))));
				obj.setServiceTax(Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("TAX_AMOUNT"))));
				obj.setAccessFee(Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("ACCESS_FEE"))));
				obj.setTalkTimeAmt(Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("RECEIVER_TRANSFER_VALUE"))));
				obj.setFailCount(rst.getLong("ERROR_COUNT"));
				obj.setFailAmt(Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("ERROR_AMT"))));
			
				obj.setTotalRechargeAmt(Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("SUCCESS_AMT")+rst.getDouble("ERROR_AMT"))));
			
					
				
				sumObj.setSuccesfulRecharges(sumObj.getSuccesfulRecharges()+rst.getLong("SUCCESS_COUNT"));
				sumObj.setRechargesDenoms(sumObj.getRechargesDenoms()+   Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("SUCCESS_AMT")) )     );
				sumObj.setServiceTax(sumObj.getServiceTax()+ Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("TAX_AMOUNT"))));
				sumObj.setAccessFee(sumObj.getAccessFee()+ Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("ACCESS_FEE"))));
				sumObj.setTalkTimeAmt(sumObj.getTalkTimeAmt()+ Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("RECEIVER_TRANSFER_VALUE"))));
				sumObj.setTotalRechargeAmt(sumObj.getTotalRechargeAmt()+ Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("SUCCESS_AMT")))+Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("ERROR_AMT"))));
				sumObj.setFailCount(sumObj.getFailCount()+rst.getLong("ERROR_COUNT"));
				sumObj.setFailAmt(sumObj.getFailAmt()+ Double.parseDouble(PretupsBL.getDisplayAmount(rst.getDouble("ERROR_AMT"))));
				
				networkSummaryDataList.add(obj);
			}
			if(networkSummaryDataList != null && !networkSummaryDataList.isEmpty()) {
				networkSummaryDataList.add(sumObj);
			}
			return networkSummaryDataList;
		}
		catch (Exception e)
		{
			_log.error(METHOD_NAME,"Exception "+e.getMessage());
			_log.errorTrace(METHOD_NAME,e);
			throw new BTSLBaseException(this, "loadNetworkSummaryDataList","error.general.processing");
		}//end of catch
		finally
		{
			if(rst!=null) 
				rst.close();
			if(pstmtSelect!=null) 
				pstmtSelect.close();
			if (mcomCon != null) {
				mcomCon.close("NetworkSummaryDAO#loadNetworkSummaryDataList");
				mcomCon = null;
			}
			if(_log.isDebugEnabled())
				_log.debug(METHOD_NAME,"Exiting loadNetworkSummaryDataList.size:"+networkSummaryDataList.size());
		}//end of finally
	}
}

