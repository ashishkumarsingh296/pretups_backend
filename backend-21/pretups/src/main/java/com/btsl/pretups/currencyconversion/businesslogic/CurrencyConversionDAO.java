/**
 * @(#)CurrencyConversionDAO.java
 *                  Copyright(c) 2016, Mahindra Comviva Technology Ltd.
 *                  All Rights Reserved
 * 
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Zeeshan Aleem 31-July-2016 Initial Creation
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  This class is used for Currency Conversion Creation/Modification
 * 
 */

package com.btsl.pretups.currencyconversion.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

public class CurrencyConversionDAO {

	private final Log _log = LogFactory.getLog(this.getClass().getName());
		
	public Map<String, CurrencyConversionVO> loadCurrencyConversionDetails() throws BTSLBaseException {
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CurrencyConversionVO currencyConversionVO = null;
        Connection con = null;
        Map<String, CurrencyConversionVO> map = new HashMap<String, CurrencyConversionVO>();
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT SOURCE_CURRENCY_CODE, TARGET_CURRENCY_CODE, SOURCE_CURRENCY_NAME, TARGET_CURRENCY_NAME, COUNTRY, CONVERSION, ");
        strBuff.append(" MULT_FACTOR, DESCRIPTION, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY FROM CURRENCY_CONVERSION_MAPPING ");
        String sqlSelect = strBuff.toString();
        String methodName = "loadCurrencyConversionDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            con = OracleUtil.getSingleConnection();
            pstmtSelect = con.prepareStatement(sqlSelect);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                currencyConversionVO = new CurrencyConversionVO();
                currencyConversionVO.setSourceCurrencyCode(rs.getString("SOURCE_CURRENCY_CODE"));
                currencyConversionVO.setTargetCurrencyCode(rs.getString("TARGET_CURRENCY_CODE"));
                currencyConversionVO.setSourceCurrencyName(rs.getString("SOURCE_CURRENCY_NAME"));
                currencyConversionVO.setTargetCurrencyName(rs.getString("TARGET_CURRENCY_NAME"));
                currencyConversionVO.setCountry(rs.getString("COUNTRY"));
                currencyConversionVO.setConversion(rs.getLong("CONVERSION"));
                currencyConversionVO.setMultFactor(rs.getLong("MULT_FACTOR"));
                currencyConversionVO.setDescription(rs.getString("DESCRIPTION"));
                currencyConversionVO.setCreatedOn(rs.getDate("CREATED_ON"));
                currencyConversionVO.setCreatedBy(rs.getString("CREATED_BY"));
                currencyConversionVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
                currencyConversionVO.setModifiedBy(rs.getString("MODIFIED_BY"));

                map.put(currencyConversionVO.getSourceCurrencyCode() + "_" + currencyConversionVO.getTargetCurrencyCode()+"_"+currencyConversionVO.getCountry(), currencyConversionVO);

            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[loadCurrencyConversionDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[loadCurrencyConversionDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: currencyConversionVO=" + currencyConversionVO);
            }
        }
        return map;
    }
	
	public List<CurrencyConversionVO> loadCurrencyConversionDetailsList(Connection con) throws BTSLBaseException {

		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		CurrencyConversionVO currencyConversionVO = null;		
		List<CurrencyConversionVO> list = new ArrayList<CurrencyConversionVO>();
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT SOURCE_CURRENCY_CODE, TARGET_CURRENCY_CODE, SOURCE_CURRENCY_NAME, TARGET_CURRENCY_NAME, COUNTRY, CONVERSION, ");
		strBuff.append(" MULT_FACTOR, DESCRIPTION, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY FROM CURRENCY_CONVERSION_MAPPING ");
		String sqlSelect = strBuff.toString();
		String methodName = "loadCurrencyConversionDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		long id=1;
		try {			
			pstmtSelect = con.prepareStatement(sqlSelect);
			rs = pstmtSelect.executeQuery();
			
			while (rs.next()) {
				currencyConversionVO = new CurrencyConversionVO();
				currencyConversionVO.setSourceCurrencyCode(rs.getString("SOURCE_CURRENCY_CODE"));
				currencyConversionVO.setTargetCurrencyCode(rs.getString("TARGET_CURRENCY_CODE"));
				currencyConversionVO.setSourceCurrencyName(rs.getString("SOURCE_CURRENCY_NAME"));
				currencyConversionVO.setTargetCurrencyName(rs.getString("TARGET_CURRENCY_NAME"));
				currencyConversionVO.setCountry(rs.getString("COUNTRY"));
				currencyConversionVO.setConversion(rs.getLong("CONVERSION"));
				currencyConversionVO.setMultFactor(rs.getLong("MULT_FACTOR"));
				currencyConversionVO.setDescription(rs.getString("DESCRIPTION"));
				currencyConversionVO.setCreatedOn(rs.getDate("CREATED_ON"));
				currencyConversionVO.setCreatedBy(rs.getString("CREATED_BY"));
				currencyConversionVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
				currencyConversionVO.setModifiedBy(rs.getString("MODIFIED_BY"));
				currencyConversionVO.setConversion(currencyConversionVO.getDisplayAmount());
				currencyConversionVO.setId(id++);
				list.add(currencyConversionVO);
			}

		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[loadCurrencyConversionDetailsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[loadCurrencyConversionDetailsList]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: currencyConversionVO Size=" + list.size());
			}
		}
		return list;	
	}	
	
	public int updateCurrencyDetails(Connection p_con,CurrencyConversionVO p_currencyVO,String userID) throws BTSLBaseException {
		 final String methodName = "updateCurrencyDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering p_currencyVO Size " + p_currencyVO.getmDataList().size());
        }
        int updateCount = -1;
		List<CurrencyConversionVO> modVOList=p_currencyVO.getmDataList();
        PreparedStatement pstmtUpdate = null;
        StringBuffer updateQueryBuff = new StringBuffer("UPDATE CURRENCY_CONVERSION_MAPPING SET CONVERSION = ?,MODIFIED_ON = ?, MODIFIED_BY = ? WHERE SOURCE_CURRENCY_CODE = ? AND TARGET_CURRENCY_CODE = ?");
        String updateQuery = updateQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("updateCurrencyDetails()", "Update Query= " + updateQuery);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            int modVOLists=modVOList.size();
			for(int i=0;i<modVOLists;i++){
            pstmtUpdate.setLong(1, ((CurrencyConversionVO)modVOList.get(i)).getSystemAmount());
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(new Date()));
            pstmtUpdate.setString(3, userID);
            pstmtUpdate.setString(4, ((CurrencyConversionVO)modVOList.get(i)).getSourceCurrencyCode());
            pstmtUpdate.setString(5, ((CurrencyConversionVO)modVOList.get(i)).getTargetCurrencyCode());           
            updateCount = pstmtUpdate.executeUpdate();
		}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + updateQuery);
            }
        }
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[updateCurrencyDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[updateCurrencyDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting updateCount" + updateCount);
            }
        }
        return updateCount;
	}	
         public double getCovertedAmount(Connection p_con, String currencyCode, String ntwrk) throws BTSLBaseException {

	        final String methodName = "getCovertedAmount";
	        if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Entered currencyCode:" +currencyCode);
	        }	        

                        double currency=1.0;

                currency = ((CurrencyConversionVO)CurrencyConversionCache.getObject("USD", currencyCode, ntwrk)).getDisplayAmount();
            if(_log.isDebugEnabled())_log.debug("getDisplayAmount","Exiting display amount:"+currency);
                  return currency;
	        } 
	 
		/**
		 * CurrencyConversionDAO.java
		 * @param con
		 * @param list
		 * @return
		 * @throws BTSLBaseException
		 * int[]
		 * akanksha.gupta
		 * 01-Sep-2016 4:13:28 pm
		 */
		public int[] updateCurrencyConversionRate(Connection con,List<CurrencyConversionVO> list) throws BTSLBaseException {

			String methodName = "updateCurrencyConversionRate";
				StringBuffer selectQueryBuff = new StringBuffer();
				PreparedStatement pstmtSelect = null;
		        ResultSet rs = null;
	            selectQueryBuff.append("SELECT MULT_FACTOR FROM CURRENCY_CONVERSION_MAPPING WHERE SOURCE_CURRENCY_CODE =? AND TARGET_CURRENCY_CODE = ? AND COUNTRY =? ");
	            final String selectQuery = selectQueryBuff.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "select query:" + selectQuery);
	            }
			StringBuilder strBuff = new StringBuilder();
                        strBuff.append(" UPDATE CURRENCY_CONVERSION_MAPPING SET CONVERSION = ?, REFERENCE_ID = ? ,MODIFIED_ON = ?, MODIFIED_BY = ?  WHERE SOURCE_CURRENCY_CODE = ? AND TARGET_CURRENCY_CODE = ? AND COUNTRY =? ");
			String sqlUpdate = strBuff.toString();
			 int[] updatedRecords = null ;
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
			}
			int updateCount =0;
			try (PreparedStatement pstmtUpdate = con.prepareStatement(sqlUpdate);
					 ){		
				updatedRecords = new int[list.size()];
				
				int j =0;
				for( CurrencyConversionVO currencyConversionVO :list )
				{
					long multFactor=1;
					try  {		
						pstmtSelect = con.prepareStatement(selectQuery);
		            	int k =0;				
		            	pstmtSelect.setString(++k,String.valueOf(currencyConversionVO.getSourceCurrencyCode()));
		            	pstmtSelect.setString(++k,String.valueOf(currencyConversionVO.getTargetCurrencyCode()));
		            	pstmtSelect.setString(++k,String.valueOf(currencyConversionVO.getCountry()));
		            	 rs = pstmtSelect.executeQuery();
		                 while (rs.next()) {
		                    multFactor = rs.getInt("MULT_FACTOR"); 
		                 }
						int i =0;
                                                pstmtUpdate.setDouble(++i,currencyConversionVO.getConversion() * multFactor);
						pstmtUpdate.setString(++i, currencyConversionVO.getExternalRefNumber());
					    pstmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(new Date()));
			            pstmtUpdate.setString(++i, PretupsI.SYSTEM);
						pstmtUpdate.setString(++i,String.valueOf(currencyConversionVO.getSourceCurrencyCode()));
						pstmtUpdate.setString(++i,String.valueOf(currencyConversionVO.getTargetCurrencyCode()));
						pstmtUpdate.setString(++i,String.valueOf(currencyConversionVO.getCountry()));
						int count =	pstmtUpdate.executeUpdate();
						updatedRecords[j] = count;
						if(count>0)
							con.commit();
						else 
						{
							con.rollback();
							
							 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CURRENCY_RECORD_NOT_UPDATED);

						}
						j++;
					
					} catch (SQLException sqe) {
						
						currencyConversionVO.setErrorCode(PretupsErrorCodesI.CURRENCY_RECORD_NOT_UPDATED);
						currencyConversionVO.setErrorMsg("error.general.sql.processing");
						
					_log.error(methodName, "SQLException : " + sqe);
					_log.errorTrace(methodName, sqe);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[updateCurrencyConversionRate]", "", "", "", "SQL Exception:" + sqe.getMessage());
					} 
					catch (Exception ex) {
						currencyConversionVO.setErrorCode(String.valueOf(ex.getMessage()));
						currencyConversionVO.setErrorMsg(ex.getMessage());
				
						_log.error(methodName, "Exception : " + ex);
						_log.errorTrace(methodName, ex);
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[updateCurrencyConversionRate]", "", "", "", "Exception:" + ex.getMessage());
					} 
				
				}	
							
			} catch (SQLException sqe) {
				_log.error(methodName, "SQLException : " + sqe);
				_log.errorTrace(methodName, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[updateCurrencyConversionRate]", "", "", "", "SQL Exception:" + sqe.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			} catch (Exception ex) {
				_log.error(methodName, "Exception : " + ex);
				_log.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionDAO[updateCurrencyConversionRate]", "", "", "", "Exception:" + ex.getMessage());
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CURRENCY_RECORD_NOT_UPDATED);
			} finally {
				try {
	                if (rs != null) {
	                	rs.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
				try {
	                if (pstmtSelect != null) {
	                	pstmtSelect.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "Exiting: updatedRecords="+updatedRecords.length);
					
				}
			}
			return updatedRecords;	
		}	
}
