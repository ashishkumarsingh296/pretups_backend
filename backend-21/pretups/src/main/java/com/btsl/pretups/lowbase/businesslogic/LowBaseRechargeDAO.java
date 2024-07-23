package com.btsl.pretups.lowbase.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsRestUtil;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;


/**
 * This class provide method for implementing business rules
 * for Low Base Recharge
 * @author lalit.chattar
 *
 */
public class LowBaseRechargeDAO {

	private static final Log _log = LogFactory.getLog(LowBaseRechargeDAO.class.getName());
	
	private static final String CLASS_NAME = "LowBaseRechargeDAO";
	
	
	/**
	 * This methods load low base recharges details for specific Retailer and subscriber
	 * If  subscriber details not found it loads all the recharges detail of retailer and 
	 * all low base subscriber
	 * 
	 * @param basedRechargeVO
	 * @param connection java.sql.Connection object
	 * @return List<LowBasedRechargeVO> list of low base recharges details
	 */
	public List<LowBasedRechargeVO> loadLowBaseTransactionDetails(LowBasedRechargeVO basedRechargeVO, Connection connection) throws BTSLBaseException{
		//local_index_implemented
		final String methodName = "#loadLowBaseTransactionDetails";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<LowBasedRechargeVO> dataList = new ArrayList<>();
		
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Entered");
			}
			
			StringBuilder builder = new StringBuilder("SELECT SENDER_MSISDN, RECEIVER_MSISDN, TRANSFER_ID, TO_CHAR(TRANSFER_DATE_TIME, 'DD/MM/YYYY HH24:MI:SS') as TRANSFER_DATE_TIME, TRANSFER_VALUE, TRANSFER_STATUS FROM C2S_TRANSFERS ");
			builder.append("WHERE TRANSFER_DATE=TO_DATE(?, 'DD/MM/YY') AND LOW_BASED_RECHARGE = ? AND TRANSFER_STATUS=? AND SENDER_MSISDN = ?");
			if(!BTSLUtil.isEmpty(basedRechargeVO.getSubscriberMobileNo())){
				builder.append(" AND RECEIVER_MSISDN = ?");
			}
			
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, builder.toString());
			}
			
			preparedStatement = connection.prepareStatement(builder.toString());
			preparedStatement.setString(1, basedRechargeVO.getRechargeDate());
			preparedStatement.setString(2, PretupsI.IS_LOW_BASE_RECHARGE);
			preparedStatement.setString(3, PretupsI.TXN_STATUS_SUCCESS);
			preparedStatement.setString(4, basedRechargeVO.getAgentMobileNo());
			
			if(!BTSLUtil.isEmpty(basedRechargeVO.getSubscriberMobileNo())){
				preparedStatement.setString(5, basedRechargeVO.getSubscriberMobileNo());
			}
			
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				LowBasedRechargeVO lowBasedRechargeVO = new LowBasedRechargeVO();
				lowBasedRechargeVO.setAgentMobileNo(resultSet.getString("SENDER_MSISDN"));
				lowBasedRechargeVO.setSubscriberMobileNo(resultSet.getString("RECEIVER_MSISDN"));
				lowBasedRechargeVO.setTransactionNo(resultSet.getString("TRANSFER_ID"));
				lowBasedRechargeVO.setRechargeDate(resultSet.getString("TRANSFER_DATE_TIME"));
				lowBasedRechargeVO.setRechargeAmount(PretupsRestUtil.getActualAmount(resultSet.getString("TRANSFER_VALUE")));
				lowBasedRechargeVO.setMessage(PretupsI.DEFAULT_SUCCESS_MESSAGE);
				dataList.add(lowBasedRechargeVO);
			}
			
		} catch (SQLException e) {
			_log.error(methodName, "SQLException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[addBarredUser]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",e);
		}finally{
			try{
                if (resultSet!= null){
                	resultSet.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
			try{
                if (preparedStatement!= null){
                	preparedStatement.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Exit");
			}
		}
		return dataList;
		
	}
	
	
	
	/**
	 * This methods load low base eligibility details for specific subscriber and recharge amount
	 * 
	 * @param basedRechargeVO
	 * @param connection java.sql.Connection object
	 * @return List<LowBasedRechargeVO> list of low base recharges details
	 */
	public LowBasedRechargeVO loadLowBaseTransactionEligibilityDetails(LowBasedRechargeVO basedRechargeVO, Connection connection) throws BTSLBaseException{
		
		final String methodName = "#loadLowBaseTransactionEligibilityDetails";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LowBasedRechargeVO lowBasedRechargeVO = null;
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Entered");
			}
			
			StringBuilder builder = new StringBuilder("SELECT CUSTOMER_MSISDN, MIN_RECH_AMOUNT, MAX_RECH_AMOUNT FROM LOW_BASE_CUSTOMER WHERE CUSTOMER_MSISDN = ?");
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, builder.toString());
			}
			
			preparedStatement = connection.prepareStatement(builder.toString());
			preparedStatement.setString(1, basedRechargeVO.getSubscriberMobileNo());
			
			resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()){
				lowBasedRechargeVO = new LowBasedRechargeVO();
				lowBasedRechargeVO.setSubscriberMobileNo(resultSet.getString("CUSTOMER_MSISDN"));
				Integer minAmount = resultSet.getInt("MIN_RECH_AMOUNT");
				Integer maxAmount = resultSet.getInt("MAX_RECH_AMOUNT");
				
				lowBasedRechargeVO.setMinTransferValueAsString(PretupsRestUtil.getActualAmount(Integer.toString(minAmount)));
				lowBasedRechargeVO.setMaxTrnasferValueAsString(PretupsRestUtil.getActualAmount(Integer.toString(maxAmount)));

				long rechargeAmoung = Long.parseLong(basedRechargeVO.getRechargeAmount()) * ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
				lowBasedRechargeVO.setMinTransferValue(minAmount);
				lowBasedRechargeVO.setMaxTrnasferValue(maxAmount);
				if(rechargeAmoung >= lowBasedRechargeVO.getMinTransferValue() && rechargeAmoung <= lowBasedRechargeVO.getMaxTrnasferValue()){
					lowBasedRechargeVO.setIsEligible(PretupsI.DEFAULT_YES);
				}else{
					lowBasedRechargeVO.setIsEligible(PretupsI.DEFAULT_NO);
				}
			}
			
		} catch (SQLException e) {
			_log.error(methodName, "SQLException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[addBarredUser]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}finally{
			try{
		        if (resultSet!= null){
		          resultSet.close();
		        }
		      }
		      catch (SQLException e){
		    	  _log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (preparedStatement!= null){
		        	preparedStatement.close();
		        }
		      }
		      catch (SQLException e){
		    	  _log.error("An error occurred closing result set.", e);
		      }
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Exit");
			}
		}
		return lowBasedRechargeVO;
		
	}
	
}
