package com.btsl.voms.voucher.businesslogic;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class VomsPinExpiryDateExtensionDAO {
	 private static Log LOG = LogFactory.getLog(VomsPinExpiryDateExtensionDAO.class.getName());

	
	 public int updateExpiryDate(Connection p_con, VomsVoucherVO vomsVoucherVO) throws BTSLBaseException {
	        final String methodName = "updateExpiryDate";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered vomsVoucherVO:");
	        	loggerValue.append(vomsVoucherVO.toString());
	        	loggerValue.append("p_boolean:" );
	            LOG.debug(methodName,  loggerValue );
	        }
	        PreparedStatement pstmtSelect = null;
	        PreparedStatement pstmtUpdate = null;
	        int updateCount = 0;
	        int selectCount = 0;
	        ResultSet rs = null;
	        try {
	        	int i =1;
	        	final StringBuffer selectQueryBuff = new StringBuffer(" SELECT COUNT(*) as UPDATECOUNT FROM VOMS_VOUCHERS ");
	        	selectQueryBuff.append("WHERE voucher_type = ? ");
	        	selectQueryBuff.append("AND user_network_code = ? ");
	        	selectQueryBuff.append("AND current_status NOT IN ( ? , ? , ? , ?  ) ");
	        	selectQueryBuff.append("AND serial_no >= ? ");
	        	selectQueryBuff.append("AND serial_no <= ?");
	        	
	        	String selectQuery = selectQueryBuff.toString();
	            if (LOG.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Select query:");
	            	loggerValue.append(selectQuery);
	                LOG.debug(methodName,  loggerValue );
	            }
	        	pstmtSelect = p_con.prepareStatement(selectQuery);
	            pstmtSelect.setString(i,vomsVoucherVO.getVoucherType());
	            i++;
	            pstmtSelect.setString(i, vomsVoucherVO.getUserNetworkCode());
	            i++;
	            pstmtSelect.setString(i, PretupsI.VOUCHER_CONSUMED_STATUS);
	            i++;
	            pstmtSelect.setString(i, PretupsI.VOUCHER_STOLEN_STATUS);
	            i++;
	            pstmtSelect.setString(i, PretupsI.VOUCHER_DAMAGED_STATUS);
	            i++;
	            pstmtSelect.setString(i, PretupsI.VOUCHER_HOLD_STATUS);
	            i++;
	            pstmtSelect.setString(i,vomsVoucherVO.get_fromSerialNo());
	            i++;
	            pstmtSelect.setString(i,vomsVoucherVO.getToSerialNo());
	            rs = pstmtSelect.executeQuery();
	            while (rs.next()) {

	               selectCount= rs.getInt("UPDATECOUNT");

	            }
	            if(selectCount==0)
	            	return selectCount;
	            i=1;
	        	final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE VOMS_VOUCHERS ");
	        	updateQueryBuff.append("SET  pre_expiry_date= expiry_date,expiry_date=?,consume_before = ?,info1=?");
	        	updateQueryBuff.append("WHERE voucher_type = ? ");
	        	updateQueryBuff.append("AND user_network_code = ? ");
	        	updateQueryBuff.append("AND current_status NOT IN ( ? , ? , ? , ?  ) ");
	        	updateQueryBuff.append("AND serial_no >= ? ");
	        	updateQueryBuff.append("AND serial_no <= ?");
	           
	            String updateQuery = updateQueryBuff.toString();
	            if (LOG.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Update query:");
	            	loggerValue.append(updateQuery);
	                LOG.debug(methodName,  loggerValue );
	            }
	            
	            pstmtUpdate = p_con.prepareStatement(updateQuery);
	            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getNewExpiryDate()));
	            i++;
	            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getNewExpiryDate()));
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.getExpiryChangeReason());
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.getVoucherType());
	            i++;
	            pstmtUpdate.setString(i, vomsVoucherVO.getUserNetworkCode());
	            i++;
	            pstmtUpdate.setString(i, PretupsI.VOUCHER_CONSUMED_STATUS);
	            i++;
	            pstmtUpdate.setString(i, PretupsI.VOUCHER_STOLEN_STATUS);
	            i++;
	            pstmtUpdate.setString(i, PretupsI.VOUCHER_DAMAGED_STATUS);
	            i++;
	            pstmtUpdate.setString(i, PretupsI.VOUCHER_HOLD_STATUS);
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.get_fromSerialNo());
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.getToSerialNo());
	            updateCount = pstmtUpdate.executeUpdate();
	        	return selectCount;
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException ");
	        	loggerValue.append(sqle.getMessage());
	            LOG.error(methodName,  loggerValue);
	            updateCount = 0;
	            LOG.errorTrace(methodName, sqle);
	            loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:");
	            loggerValue.append(sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
	        }// end of catch
	        catch (Exception e) {
	        	  loggerValue.setLength(0);
	              loggerValue.append("Exception:");
	              loggerValue.append(e.getMessage());
	            LOG.error(methodName, loggerValue );
	            updateCount = 0;
	            LOG.errorTrace(methodName, e);
	            loggerValue.setLength(0);
	            loggerValue.append("Exception:");
	            loggerValue.append(e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	        }// end of catch
	        finally {
	        	try {
	                if (rs != null) {
	                	rs.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            try {
	                if (pstmtUpdate != null) {
	                	pstmtUpdate.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            try {
	                if (pstmtSelect != null) {
	                	pstmtSelect.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            if (LOG.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting updateCount=");
	            	loggerValue.append(updateCount);
	                LOG.debug(methodName,  loggerValue );
	            }
	        }// end of finally
	    	
	 }
	 
	 public ArrayList<VomsVoucherVO> loadBatchPinExpiryList(Connection p_con,
			 String p_status,
			Date p_fromDate, Date p_toDate) throws BTSLBaseException{
		if (LOG.isDebugEnabled()) {
			LOG.debug("loadBatchPinExpiryList", " Entered"
					+ " p_status" + p_status + "p_fromDate" + p_fromDate
					+ "p_toDate=" + p_toDate);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsVoucherVO  batchVO = null;
		ArrayList<VomsVoucherVO> batchList = null;
		final String METHOD_NAME = "loadBatchPinExpiryList";
		StringBuilder strQueryBuff = new StringBuilder("select batch_no,voucher_type,total_vouchers,from_serial_no,to_serial_no,network_code,expiry_date,remarks from voms_pin_exp_ext");
		strQueryBuff.append(" where status =? And date_trunc('day', modified_on :: TIMESTAMP)>=? AND date_trunc('day', modified_on :: TIMESTAMP)<=? order by batch_no desc");
		String strBuff = strQueryBuff.toString();
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug(" loadBatchList", ":: Query :: " + strBuff);
			}
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(1, p_status);
			psmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
			psmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
			rs = psmt.executeQuery();
			batchList = new ArrayList<VomsVoucherVO>();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsVoucherVO();
				batchVO.setVoucherType(rs.getString("voucher_type"));
				batchVO.setBatchNo(rs.getString("batch_no"));
				batchVO.set_totalVouchers(rs.getLong("total_vouchers"));
				batchVO.set_fromSerialNo(rs.getString("from_serial_no"));
				batchVO.setToSerialNo(rs.getString("to_serial_no"));
				batchVO.setUserNetworkCode(rs.getString("network_code"));
				batchVO.setNewExpiryDate(rs.getDate("expiry_date"));
				batchVO.setExpiryChangeReason(rs.getString("remarks"));
				batchVO.setCreatedBy(rs.getString("created_by"));
				batchList.add(batchVO);
			}
			return batchList;
		}catch (SQLException sqle) {

			LOG.error("loadBatchPinExpiryList", "SQLException " + sqle.getMessage());
			LOG.errorTrace(METHOD_NAME, sqle);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
						EventStatusI.RAISED, EventLevelI.FATAL,
						"VomsVoucherDAO[loadBatchPinExpiryList]", "", "", "",
						"SQLException:" + sqle.getMessage());
				throw new BTSLBaseException(this, "loadBatchPinExpiryList",
						"error.general.sql.processing");
			}// end of catch
		catch (Exception e) {

			LOG.error("loadBatchPinExpiryList", "Exception " + e.getMessage());
			LOG.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchPinExpiryList]", "", "", "", "Exception:"
							+ e.getMessage());
			throw new BTSLBaseException(this, "loadBatchPinExpiryList",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				LOG.error(" loadBatchPinExpiryList", " ::  Exception Closing RS : "
						+ ex.getMessage());
				LOG.errorTrace(METHOD_NAME, ex);
			}
			
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception ex) {
				LOG.error(" loadBatchPinExpiryList"," ::  Exception Closing Prepared Stmt: "+ ex.getMessage());
				LOG.errorTrace(METHOD_NAME, ex);
			}
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("loadBatchPinExpiryList() ", ":: Exiting : batchList size = "+ batchList.size());
			}
		}
	}
	 
	 public int updateVomsPinExpExt(Connection p_con, VomsVoucherVO vomsVoucherVO) throws BTSLBaseException {
	        final String methodName = "updateVomsPinExpExt";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered vomsVoucherVO:");
	        	loggerValue.append(vomsVoucherVO.toString());
	        	loggerValue.append("p_boolean:" );
	            LOG.debug(methodName,  loggerValue );
	        }
	        PreparedStatement pstmtUpdate = null;
	        int updateCount = 0;
	        try {
	        	int i =1;
	        	final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE voms_pin_exp_ext ");
	        	updateQueryBuff.append("SET  status = ? , total_failure = ? , total_success = ? , remarks = ? ");
	        	updateQueryBuff.append("WHERE batch_no = ? AND network_code=? ");
	           
	            String updateQuery = updateQueryBuff.toString();
	            if (LOG.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Update query:");
	            	loggerValue.append(updateQuery);
	                LOG.debug(methodName,  loggerValue );
	            }
	            
	            pstmtUpdate = p_con.prepareStatement(updateQuery);
	            pstmtUpdate.setString(i,vomsVoucherVO.getStatus());
	            i++;
	            pstmtUpdate.setLong(i,Long.parseLong(vomsVoucherVO.getTotal_failure()));
	            i++;
	            pstmtUpdate.setLong(i,Long.parseLong(vomsVoucherVO.getTotal_success()));
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.getExpiryChangeReason());
	            
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.getBatchNo());
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.getUserNetworkCode());
	            
	            updateCount = pstmtUpdate.executeUpdate();
	           
	            
	        	return updateCount;
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException ");
	        	loggerValue.append(sqle.getMessage());
	            LOG.error(methodName,  loggerValue);
	            updateCount = 0;
	            LOG.errorTrace(methodName, sqle);
	            loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:");
	            loggerValue.append(sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
	        }// end of catch
	        catch (Exception e) {
	        	  loggerValue.setLength(0);
	              loggerValue.append("Exception:");
	              loggerValue.append(e.getMessage());
	            LOG.error(methodName, loggerValue );
	            updateCount = 0;
	            LOG.errorTrace(methodName, e);
	            loggerValue.setLength(0);
	            loggerValue.append("Exception:");
	            loggerValue.append(e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	        }// end of catch
	        finally {
	            try {
	                if (pstmtUpdate != null) {
	                	pstmtUpdate.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            if (LOG.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting updateCount=");
	            	loggerValue.append(updateCount);
	                LOG.debug(methodName,  loggerValue );
	            }
	        }// end of finally
	        }
	 
	 public int addVomsPinExpExt(Connection p_con, VomsVoucherVO vomsVoucherVO)
				throws BTSLBaseException {
			if (LOG.isDebugEnabled()) {
				StringBuilder loggerValue= new StringBuilder();
				loggerValue.setLength(0);
				loggerValue.append("vomsVoucherVO=");
				loggerValue.append(vomsVoucherVO);
				LOG.debug("addVomsPinExpExt() Entered",
						loggerValue);
			}
			PreparedStatement psmt = null;
			int addCount = 0;
			final String METHOD_NAME = "addVomsPinExpExt";
			try {
				StringBuilder strBuff = new StringBuilder(
						"INSERT INTO voms_pin_exp_ext (batch_no,voucher_type,total_vouchers, from_serial_no, to_serial_no,");
				strBuff.append("total_failure,total_success, network_code,status,created_on,created_by,modified_on,modified_by,remarks,expiry_date,file_batch_id,info1 )");
				strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				if (LOG.isDebugEnabled()) {
					LOG.debug("addVomsPinExpExt()",
							"addVomsPinExpExt()Query=" + strBuff.toString());
				}
				psmt = p_con.prepareStatement(strBuff.toString());
					psmt.setString(1, vomsVoucherVO.getBatchNo());
					psmt.setString(2, vomsVoucherVO.getVoucherType());
					psmt.setLong(3, vomsVoucherVO.get_totalVouchers());
					psmt.setString(4, vomsVoucherVO.get_fromSerialNo());
					psmt.setString(5, vomsVoucherVO.getToSerialNo());
					psmt.setLong(6, 0);
					psmt.setLong(7, 0);
					psmt.setString(8, vomsVoucherVO.getUserNetworkCode());
					psmt.setString(9, vomsVoucherVO.getStatus());
					psmt.setTimestamp(10, BTSLUtil
							.getTimestampFromUtilDate(vomsVoucherVO.getCreatedOn()));
					psmt.setString(11, vomsVoucherVO
							.getCreatedBy());
					psmt.setTimestamp(12,
							BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO
									.getModifiedOn()));
					psmt.setString(13, vomsVoucherVO
							.getModifiedBy());
					psmt.setString(14, vomsVoucherVO.getExpiryChangeReason());
					psmt.setTimestamp(15,BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO 
                                            .getNewExpiryDate()));
					psmt.setString(16,vomsVoucherVO.getFileBatchNo());
					psmt.setString(17,vomsVoucherVO.getExpiryChangeReason());

					addCount += psmt.executeUpdate();
					if (addCount <= 0) {
						LOG.error(" addBatch",
								" Not able to add in Batches table for Batch No="
										+ vomsVoucherVO.getBatchNo());
						EventHandler.handle(EventIDI.SYSTEM_ERROR,
								EventComponentI.SYSTEM, EventStatusI.RAISED,
								EventLevelI.MAJOR, "VomsBatchesDAO[addBatch]", "",
								"", "",
								"Not able to add in Batches table for Batch No="
										+ vomsVoucherVO.getBatchNo());
						throw new BTSLBaseException(
								this,
								"addBatch",
								PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
					}
				
			} catch (BTSLBaseException be) {
				throw be;
			} catch (SQLException sqe) {
				LOG.error(" addBatch() ", "  SQL Exception =" + sqe);
				LOG.errorTrace(METHOD_NAME, sqe);
				EventHandler.handle(
						EventIDI.SYSTEM_ERROR,
						EventComponentI.SYSTEM,
						EventStatusI.RAISED,
						EventLevelI.FATAL,
						"VomsVoucherDAO[addBatch]",
						"",
						"",
						"",
						"SQL Exception: Error in adding batch info"
								+ sqe.getMessage());
				throw new BTSLBaseException(this, "addBatch",
						PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			} catch (Exception ex) {
				LOG.error(" addBatch() ", "  Exception =" + ex);
				LOG.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
						EventStatusI.RAISED, EventLevelI.FATAL,
						"VomsVoucherDAO[addBatch]", "", "", "",
						"Exception: Error in adding batch info" + ex.getMessage());
				throw new BTSLBaseException(this, "addBatch",
						PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			} finally {
				try {
					if (psmt != null) {
						psmt.close();
					}
				} catch (Exception e) {
					LOG.error("addBatch()", " Exception while closing rs ex=" + e);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("addBatch() Successful from serial number "
							+ vomsVoucherVO.get_fromSerialNo()
							+ " to serial number " + vomsVoucherVO.getToSerialNo(),
							"Exiting:  addCount=" + addCount);
				}
			}
			return addCount;
		}
	 
	 public int updateBatchExpiryDate(Connection p_con, VomsVoucherVO vomsVoucherVO) throws BTSLBaseException {
	        final String methodName = "updateBatchExpiryDate";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered vomsVoucherVO:");
	        	loggerValue.append(vomsVoucherVO.toString());
	        	loggerValue.append("p_boolean:" );
	            LOG.debug(methodName,  loggerValue );
	        }
	        PreparedStatement pstmtUpdate = null;
	        int updateCount = 0;
	        try {
	        	int i =1;
	        	final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE VOMS_VOUCHERS ");
	        	updateQueryBuff.append("SET  pre_expiry_date= expiry_date,expiry_date=?,consume_before=?,info1=? ");
	        	updateQueryBuff.append("WHERE serial_no = ? AND user_network_code=? AND voucher_type=? AND current_status NOT IN ( ? , ? , ?  ) ");
	           
	            String updateQuery = updateQueryBuff.toString();
	            if (LOG.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Update query:");
	            	loggerValue.append(updateQuery);
	                LOG.debug(methodName,  loggerValue );
	            }
	            
	            pstmtUpdate = p_con.prepareStatement(updateQuery);
	            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
	            i++;
	            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.getExpiryChangeReason());
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.get_fromSerialNo());
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.getUserNetworkCode());
	            i++;
	            pstmtUpdate.setString(i,vomsVoucherVO.getVoucherType());
	            i++;
	            pstmtUpdate.setString(i, PretupsI.VOUCHER_CONSUMED_STATUS);
	            i++;
	            pstmtUpdate.setString(i, PretupsI.VOUCHER_STOLEN_STATUS);
	            i++;
	            pstmtUpdate.setString(i, PretupsI.VOUCHER_DAMAGED_STATUS);
	            
	            updateCount = pstmtUpdate.executeUpdate();
	        	return updateCount;
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException ");
	        	loggerValue.append(sqle.getMessage());
	            LOG.error(methodName,  loggerValue);
	            updateCount = 0;
	            LOG.errorTrace(methodName, sqle);
	            loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:");
	            loggerValue.append(sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
	        }// end of catch
	        catch (Exception e) {
	        	  loggerValue.setLength(0);
	              loggerValue.append("Exception:");
	              loggerValue.append(e.getMessage());
	            LOG.error(methodName, loggerValue );
	            updateCount = 0;
	            LOG.errorTrace(methodName, e);
	            loggerValue.setLength(0);
	            loggerValue.append("Exception:");
	            loggerValue.append(e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	        }// end of catch
	        finally {
	            try {
	                if (pstmtUpdate != null) {
	                	pstmtUpdate.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            if (LOG.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting updateCount=");
	            	loggerValue.append(updateCount);
	                LOG.debug(methodName,  loggerValue );
	            }
	        }// end of finally
	    	
	 }
	 
	 public int addFileBatches(Connection p_con, VomsVoucherVO vomsVoucherVO)
				throws BTSLBaseException {
			if (LOG.isDebugEnabled()) {
				StringBuilder loggerValue= new StringBuilder();
				loggerValue.setLength(0);
				loggerValue.append("vomsVoucherVO=");
				loggerValue.append(vomsVoucherVO);
				LOG.debug("addFileBatches() Entered",
						loggerValue);
			}
			PreparedStatement psmt = null;
			int addCount = 0;
			final String METHOD_NAME = "addFileBatches";
			try {
				StringBuilder strBuff = new StringBuilder(
						"INSERT INTO FILE_BATCHES (file_batch_id,total_records,batch_name,network_code,");
				strBuff.append("status,created_on,modified_on,file_name )");
				strBuff.append(" VALUES (?,?,?,?,?,?,?,?)");
				if (LOG.isDebugEnabled()) {
					LOG.debug("addFileBatches()",
							"addFileBatches()Query=" + strBuff.toString());
				}
				psmt = p_con.prepareStatement(strBuff.toString());
					psmt.setString(1, vomsVoucherVO.getFileBatchNo());
					psmt.setLong(2, vomsVoucherVO.get_totalVouchers());
					psmt.setString(3, vomsVoucherVO.getBatchName());
					psmt.setString(4, "NG");
					psmt.setString(5, vomsVoucherVO.getStatus());
					psmt.setDate(6, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO
							.getCreatedOn()));
					psmt.setTimestamp(7,
							BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO
									.getModifiedOn()));
					psmt.setString(8, vomsVoucherVO.getFileName());

					addCount += psmt.executeUpdate();
					if (addCount <= 0) {
						LOG.error(" addFileBatches",
								" Not able to add in File Batches table for File Batch No="
										+ vomsVoucherVO.getFileBatchNo());
						EventHandler.handle(EventIDI.SYSTEM_ERROR,
								EventComponentI.SYSTEM, EventStatusI.RAISED,
								EventLevelI.MAJOR, "VomsBatchesDAO[addBatch]", "",
								"", "",
								"Not able to add in File Batches table for File Batch No="
										+ vomsVoucherVO.getFileBatchNo());
						throw new BTSLBaseException(
								this,
								"addFileBatches",
								PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
					}
				
			} catch (BTSLBaseException be) {
				throw be;
			} catch (SQLException sqe) {
				LOG.error(" addFileBatches() ", "  SQL Exception =" + sqe);
				LOG.errorTrace(METHOD_NAME, sqe);
				EventHandler.handle(
						EventIDI.SYSTEM_ERROR,
						EventComponentI.SYSTEM,
						EventStatusI.RAISED,
						EventLevelI.FATAL,
						"VomsVoucherDAO[addFileBatches]",
						"",
						"",
						"",
						"SQL Exception: Error in adding batch info"
								+ sqe.getMessage());
				throw new BTSLBaseException(this, "addFileBatches",
						PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			} catch (Exception ex) {
				LOG.error(" addFileBatches() ", "  Exception =" + ex);
				LOG.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
						EventStatusI.RAISED, EventLevelI.FATAL,
						"VomsVoucherDAO[addFileBatches]", "", "", "",
						"Exception: Error in adding batch info" + ex.getMessage());
				throw new BTSLBaseException(this, "addFileBatches",
						PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			} finally {
				try {
					if (psmt != null) {
						psmt.close();
					}
				} catch (Exception e) {
					LOG.error("addFileBatches()", " Exception while closing rs ex=" + e);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("addFileBatches() Total Successful Batches  "
							+ vomsVoucherVO.get_totalVouchers(),
							"Exiting:  addCount=" + addCount);
				}
			}
			return addCount;
		}
	 
	 /**
	     * Method to load the user balances for the various products
	     * 
	     * @param p_con
	     * @param p_userID
	     * @param p_networkID
	     * @param p_networkFor
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public ArrayList<VomsVoucherVO> loadVomsPinExpExt(Connection p_con, String p_batchID) throws BTSLBaseException {
	        final String METHOD_NAME = "loadVomsPinExpExt";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(METHOD_NAME, "Entered  p_batchID : " + p_batchID);
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        ArrayList<VomsVoucherVO> vomsPinExpExtList = null;
	        try {
	            final StringBuffer selectQueryBuff = new StringBuffer("SELECT from_serial_no, expiry_date, info1, network_code, voucher_type ");
	            selectQueryBuff.append(" FROM VOMS_PIN_EXP_EXT");
	            selectQueryBuff.append(" WHERE file_batch_id=?");
	            final String selectQuery = selectQueryBuff.toString();
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "select query : " + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery);
	            pstmtSelect.setString(1, p_batchID);
	            rs = pstmtSelect.executeQuery();
	            VomsVoucherVO vomsVoucherVO = null;
	            vomsPinExpExtList = new ArrayList<VomsVoucherVO>();
	            while (rs.next()) {
	            	vomsVoucherVO = new VomsVoucherVO();
	            	vomsVoucherVO.set_fromSerialNo(rs.getString("from_serial_no"));
	            	vomsVoucherVO.setExpiryDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("expiry_date")));
	            	vomsVoucherVO.setExpiryChangeReason(rs.getString("info1"));
	            	vomsVoucherVO.setUserNetworkCode(rs.getString("network_code"));
	            	vomsVoucherVO.setVoucherType(rs.getString("voucher_type"));
	            	vomsPinExpExtList.add(vomsVoucherVO);
	            }

	        }// end of try
	        catch (SQLException sqle) {
	            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
	            LOG.errorTrace(METHOD_NAME, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalanceList]", "", "",
	                            null, "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
	        }// end of catch
	        catch (Exception e) {
	            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
	            LOG.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalanceList]", "", "",
	                            null, "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
	        }// end of catch
	        finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (pstmtSelect != null) {
	                    pstmtSelect.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "Exiting vomsPinExpExtList:" + vomsPinExpExtList.size());
	            }
	        }// end of finally

	        return vomsPinExpExtList;
	    }
	    
	    public ArrayList<VomsVoucherVO> loadBatchPinExpiryList(Connection p_con,
				 String p_status) throws BTSLBaseException{
			if (LOG.isDebugEnabled()) {
				LOG.debug("loadBatchPinExpiryList", " Entered"
						+ " p_status" + p_status);
			}
			PreparedStatement psmt = null;
			ResultSet rs = null;
			VomsVoucherVO  batchVO = null;
			ArrayList<VomsVoucherVO> batchList = null;
			final String METHOD_NAME = "loadBatchPinExpiryList";
			StringBuilder strQueryBuff = new StringBuilder("select batch_no,voucher_type,total_vouchers,from_serial_no,to_serial_no,network_code,expiry_date,remarks,created_by from voms_pin_exp_ext");
			strQueryBuff.append(" where status =? order by batch_no");
			String strBuff = strQueryBuff.toString();
			try {
				if (LOG.isDebugEnabled()) {
					LOG.debug(" loadBatchList", ":: Query :: " + strBuff);
				}
				psmt = p_con.prepareStatement(strBuff);
				psmt.setString(1, p_status);
				rs = psmt.executeQuery();
				batchList = new ArrayList<VomsVoucherVO>();
				psmt.clearParameters();
				while (rs.next()) {
					batchVO = new VomsVoucherVO();
					batchVO.setVoucherType(rs.getString("voucher_type"));
					batchVO.setBatchNo(rs.getString("batch_no"));
					batchVO.set_totalVouchers(rs.getLong("total_vouchers"));
					batchVO.set_fromSerialNo(rs.getString("from_serial_no"));
					batchVO.setToSerialNo(rs.getString("to_serial_no"));
					batchVO.setUserNetworkCode(rs.getString("network_code"));
					batchVO.setNewExpiryDate(rs.getDate("expiry_date"));
					batchVO.setExpiryChangeReason(rs.getString("remarks"));
					batchVO.setCreatedBy(rs.getString("created_by"));
					batchList.add(batchVO);
				}
				return batchList;
			}catch (SQLException sqle) {

				LOG.error("loadBatchPinExpiryList", "SQLException " + sqle.getMessage());
				LOG.errorTrace(METHOD_NAME, sqle);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
							EventStatusI.RAISED, EventLevelI.FATAL,
							"VomsVoucherDAO[loadBatchPinExpiryList]", "", "", "",
							"SQLException:" + sqle.getMessage());
					throw new BTSLBaseException(this, "loadBatchPinExpiryList",
							"error.general.sql.processing");
				}// end of catch
			catch (Exception e) {

				LOG.error("loadBatchPinExpiryList", "Exception " + e.getMessage());
				LOG.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
						EventStatusI.RAISED, EventLevelI.FATAL,
						"VomsVoucherDAO[loadBatchPinExpiryList]", "", "", "", "Exception:"
								+ e.getMessage());
				throw new BTSLBaseException(this, "loadBatchPinExpiryList",
						"error.general.processing");
			}// end of catch
			finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception ex) {
					LOG.error(" loadBatchPinExpiryList", " ::  Exception Closing RS : "
							+ ex.getMessage());
					LOG.errorTrace(METHOD_NAME, ex);
				}
				
				try {
					if (psmt != null) {
						psmt.close();
					}
				} catch (Exception ex) {
					LOG.error(" loadBatchPinExpiryList"," ::  Exception Closing Prepared Stmt: "+ ex.getMessage());
					LOG.errorTrace(METHOD_NAME, ex);
				}
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("loadBatchPinExpiryList() ", ":: Exiting : batchList size = "+ batchList.size());
				}
			}
		}
	 
	    /**
	     * Method to load the user balances for the various products
	     * 
	     * @param p_con
	     * @param p_userID
	     * @param p_networkID
	     * @param p_networkFor
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public int loadConsumedVouchersCount(Connection p_con, VomsVoucherVO vomsVoucherVO) throws BTSLBaseException {
	        final String METHOD_NAME = "loadConsumedVouchersCount";
	        StringBuilder loggerValue= new StringBuilder();
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered vomsVoucherVO:");
	        	loggerValue.append(vomsVoucherVO.toString());
	        	loggerValue.append("p_boolean:" );
	            LOG.debug(METHOD_NAME,  loggerValue );
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        int consumedCount = 0;
	        try {
	            final StringBuffer selectQueryBuff = new StringBuffer("SELECT COUNT(*) AS consumed_count FROM voms_vouchers");
	            selectQueryBuff.append(" WHERE serial_no >= ?");
	            selectQueryBuff.append(" AND serial_no <= ?");
	            selectQueryBuff.append(" AND current_status=?");
	            final String selectQuery = selectQueryBuff.toString();
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "select query : " + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery);
	            pstmtSelect.setString(1, vomsVoucherVO.get_fromSerialNo());
	            pstmtSelect.setString(2, vomsVoucherVO.getToSerialNo());
	            pstmtSelect.setString(3, PretupsI.VOUCHER_CONSUMED_STATUS);
	            
	            rs = pstmtSelect.executeQuery();
	            while (rs.next()) {
	            	consumedCount = rs.getInt("consumed_count");
	            }

	        }// end of try
	        catch (SQLException sqle) {
	            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
	            LOG.errorTrace(METHOD_NAME, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsPinExpiryDateExtensionDAO[loadConsumedVouchersCount]", "", "",
	                            null, "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
	        }// end of catch
	        catch (Exception e) {
	            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
	            LOG.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsPinExpiryDateExtensionDAO[loadConsumedVouchersCount]", "", "",
	                            null, "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
	        }// end of catch
	        finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (pstmtSelect != null) {
	                    pstmtSelect.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "Exiting consumedCount:" + consumedCount);
	            }
	        }// end of finally

	        return consumedCount;
	    }
	    
	    /**
	     * Method to load the user balances for the various products
	     * 
	     * @param p_con
	     * @param p_userID
	     * @param p_networkID
	     * @param p_networkFor
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public int loadStolenAndDamagedVouchersCount(Connection p_con, VomsVoucherVO vomsVoucherVO) throws BTSLBaseException {
	        final String METHOD_NAME = "loadStolenAndDamagedVouchersCount";
	        StringBuilder loggerValue= new StringBuilder();
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered vomsVoucherVO:");
	        	loggerValue.append(vomsVoucherVO.toString());
	        	loggerValue.append("p_boolean:" );
	            LOG.debug(METHOD_NAME,  loggerValue );
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        int stolenAndDamagedCount = 0;
	        try {
	            final StringBuffer selectQueryBuff = new StringBuffer("SELECT COUNT(*) AS stolenAndDamagedCount FROM voms_vouchers");
	            selectQueryBuff.append(" WHERE serial_no >= ?");
	            selectQueryBuff.append(" AND serial_no <= ?");
	            selectQueryBuff.append(" AND current_status IN (?, ?)");
	            final String selectQuery = selectQueryBuff.toString();
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "select query : " + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery);
	            pstmtSelect.setString(1, vomsVoucherVO.get_fromSerialNo());
	            pstmtSelect.setString(2, vomsVoucherVO.getToSerialNo());
	            pstmtSelect.setString(3, PretupsI.VOUCHER_STOLEN_STATUS);
	            pstmtSelect.setString(4, PretupsI.VOUCHER_DAMAGED_STATUS);
	            
	            rs = pstmtSelect.executeQuery();
	            while (rs.next()) {
	            	stolenAndDamagedCount = rs.getInt("stolenAndDamagedCount");
	            }

	        }// end of try
	        catch (SQLException sqle) {
	            LOG.error(METHOD_NAME, "SQLException " + sqle.getMessage());
	            LOG.errorTrace(METHOD_NAME, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsPinExpiryDateExtensionDAO[loadStolenAndDamagedVouchersCount]", "", "",
	                            null, "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
	        }// end of catch
	        catch (Exception e) {
	            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
	            LOG.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsPinExpiryDateExtensionDAO[loadStolenAndDamagedVouchersCount]", "", "",
	                            null, "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
	        }// end of catch
	        finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (pstmtSelect != null) {
	                    pstmtSelect.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "Exiting stolenAndDamagedCount:" + stolenAndDamagedCount);
	            }
	        }// end of finally

	        return stolenAndDamagedCount;
	    }
}
