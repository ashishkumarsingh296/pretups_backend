/*
 * Created on Sep 13, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.umniah.voms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.util.VOMSProductVO;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

/**
 * @author ankit.zindal
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class VOMSVoucherDAO {
	private static Log _log = LogFactory.getLog(VOMSVoucherDAO.class);
	private  VOMSVoucherQry vomsVoucherQry;
	/**
	 * 
	 */
	public VOMSVoucherDAO() {
		super();
		vomsVoucherQry = (VOMSVoucherQry)ObjectProducer.getObject(QueryConstants.VOMS_VOUCHER_UMNIAH_QRY, QueryConstants.QUERY_PRODUCER);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Method LoadActiveProfiles
	 * This method will load the active profiles based von networkcode and
	 * current date
	 * 
	 * @param Connection
	 *            p_con
	 * @param String
	 *            p_networkCode
	 * @param Date
	 *            p_currDate
	 * @return HashMap profileMap
	 * @exception BTSLBaseException
	 * @author Amit Ruwali
	 */

	public HashMap loadActiveProfiles(Connection p_con, String p_networkCode, Date p_currDate, boolean p_isTimeStamp) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("loadActiveProfiles", " Entered.. p_networkCode=" + p_networkCode + "p_currDate=" + p_currDate + "p_isTimeStamp=" + p_isTimeStamp);
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		HashMap profileMap = null;
		VOMSProductVO productVO = null;
		try {
			String sqlSelectBuf = vomsVoucherQry.loadActiveProfilesQry(p_isTimeStamp);
			if (_log.isDebugEnabled())
				_log.debug("loadActiveProfiles", "Select Query=" + sqlSelectBuf);
			dbPs = p_con.prepareStatement(sqlSelectBuf);
			dbPs.setString(1, p_networkCode);
			if (p_isTimeStamp)
				dbPs.setTimestamp(2, InterfaceUtil.getTimestampFromUtilDate(p_currDate));
			else
				dbPs.setDate(2, InterfaceUtil.getSQLDateFromUtilDate(p_currDate));
			dbPs.setString(3, p_networkCode);
			rs = dbPs.executeQuery();
			profileMap = new HashMap();
			while (rs.next()) {
				productVO = new VOMSProductVO();
				productVO.setProductID(rs.getString("product_id"));
				productVO.setProductName(rs.getString("product_name"));
				productVO.setMrpStr(String.valueOf(rs.getLong("mrp")));
				productVO.setStatus(rs.getString("status"));
				productVO.setProductCode(rs.getInt("product_code"));
				productVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
				productVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
				productVO.setTalkTime(rs.getLong("talktime"));
				productVO.setValidity(rs.getInt("validity"));
				productVO.setNetworkCode(p_networkCode);
				profileMap.put(productVO.getMrpStr(), productVO);
			}
			if (_log.isDebugEnabled())
				_log.debug("loadActiveProfiles", "After executing the query loadBatchLogList method profileMap=" + profileMap.size());
			return profileMap;
		} catch (SQLException sqle) {
			_log.error("loadActiveProfiles", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfiles]", "", "", "", "Exception:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "loadActiveProfiles",
			// "error.general.sql.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("loadActiveProfiles", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfiles]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "loadActiveProfiles",
			// "error.general.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("loadActiveProfiles", " Exception while closing rs ex=" + ex);
			}
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("loadActiveProfiles", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				_log.debug("loadActiveProfiles", " Exiting..batchList size=" + profileMap.size());
			} catch (Exception e) {
				_log.error("loadActiveProfiles", " Exception while closing rs ex=" + e);
			}
		}
	}

	/**
	 * This method will Get the PIN & serial number details of voucher. Only
	 * voucher with status enabled will
	 * be picked from VOMS_VOUCHERS table.
	 * 
	 * @param p_con
	 * @param p_productVO
	 * @param p_modifiedBy
	 * @param p_modifiedOn
	 * @param p_source
	 * @param p_newStatus
	 * @param p_transactionIdList
	 * @param p_networkCode
	 * @param p_quantityRequested
	 * @return voucherList
	 * @throws BTSLBaseException
	 *//*
    public static synchronized ArrayList loadPINAndSerialNumber(Connection p_con, VOMSProductVO p_productVO, String p_modifiedBy, Date p_modifiedOn, String p_source, String p_newStatus, ArrayList p_transactionIdList, String p_networkCode, int p_quantityRequested) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadPINAndSerialNumber", " Entered.. p_productVO=" + p_productVO + " p_modifiedBy=" + p_modifiedBy + " p_modifiedOn=" + p_modifiedOn + " p_source=" + p_source + " p_newStatus=" + p_newStatus + " p_transactionIdList size=" + p_transactionIdList + " p_networkCode=" + p_networkCode + " p_quantityRequested=" + p_quantityRequested);
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        VOMSVoucherVO voucherVO = null;
        PreparedStatement dbPsUpdate = null;
        PreparedStatement dbENUpdate = null;
        // PreparedStatement dbSelectUpdate = null;
        ResultSet rst = null;
        int updateCount = 0, updateEnableCount = 0;
        ArrayList voucherList = null;
        // int voucherCount=0;
        int i = 0;
        int listSize = 0;
        try {
            StringBuilder sqlSelectBuf = new StringBuilder("SELECT VV.serial_no,VV.pin_no,VV.expiry_date,VV.previous_status,VV.seq_no,VV.created_date,VV.product_id ");
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
                sqlSelectBuf.append(" FROM VOMS_VOUCHERS VV, ENABLE_SUMMARY_DATES ESD WHERE ");
            else {
                sqlSelectBuf.append(",VP.talktime,VC.PAYABLE_AMOUNT FROM VOMS_VOUCHERS VV, VOMS_CATEGORIES VC,VOMS_PRODUCTS VP, ENABLE_SUMMARY_DATES ESD  ");
                sqlSelectBuf.append(" WHERE VV.PRODUCT_ID=VP.PRODUCT_ID AND VP.CATEGORY_ID=VC.CATEGORY_ID AND ");
            }
            sqlSelectBuf.append("  VV.CURRENT_STATUS=? AND VV.PRODUCT_ID=? AND ESD.PRODUCT_ID = ? ");
            // sqlSelectBuf.append("AND VV.CREATED_DATE = ESD.CRE_DATE AND VV.EXPIRY_DATE = ESD.EXP_DATE ");
            sqlSelectBuf.append("AND VV.CREATED_DATE = ESD.CRE_DATE ");
            // DB220120123for update WITH RS
            // sqlSelectBuf.append("AND ROWNUM<? FOR UPDATE OF VV.CURRENT_STATUS ");

            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
                sqlSelectBuf.append("AND ROWNUM<? FOR UPDATE OF VV.CURRENT_STATUS WITH RS ");
            else
                sqlSelectBuf.append("AND ROWNUM<? FOR UPDATE OF VV.CURRENT_STATUS ");
            if (_log.isDebugEnabled())
                _log.debug("loadPINAndSerialNumber", "Select Query=" + sqlSelectBuf.toString());
            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
            dbPs.setString(1, VOMSI.VOUCHER_ENABLE);
            // dbPs.setString(2,p_productVO.getNetworkCode());
            dbPs.setString(2, p_productVO.getProductID());
            dbPs.setString(3, p_productVO.getProductID());
            dbPs.setString(4, String.valueOf(p_quantityRequested + 1));

            rs = dbPs.executeQuery();
            voucherList = new ArrayList();
            while (rs.next()) {
                voucherVO = new VOMSVoucherVO();
                voucherVO.setSerialNo(rs.getString("serial_no"));
                voucherVO.setPinNo(rs.getString("pin_no"));
                voucherVO.setPreviousStatus(VOMSI.VOUCHER_ENABLE);
                voucherVO.setCurrentStatus(p_newStatus);
                voucherVO.setModifiedBy(p_modifiedBy);
                voucherVO.setModifiedOn(p_modifiedOn);
                voucherVO.setStatusChangeSource(p_source);
                voucherVO.setStatusChangePartnerID(p_modifiedBy);
                voucherVO.setTransactionID((String) p_transactionIdList.get(i));
                voucherVO.setUserLocationCode(p_networkCode);

                voucherVO.setProductID(rs.getString("product_id"));
                voucherVO.setCreatedOn(rs.getDate("created_date"));
                voucherVO.setExpiryDate(rs.getDate("expiry_date"));

                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue()) {
                    voucherVO.setTalkTime(rs.getLong("talktime"));
                    voucherVO.setPayableAmount(rs.getLong("payable_amount"));
                }
                i++;
                voucherList.add(voucherVO);
            }
            listSize = voucherList.size();
            // if required number of vouchers are not available, transaction is
            // rollback
            if (listSize != p_quantityRequested) {
                // p_con.rollback();
            	voucherList=loadPINAndSerialNumberOld(p_con, p_productVO, p_modifiedBy, p_modifiedOn, p_source, p_newStatus, p_transactionIdList, p_networkCode, p_quantityRequested);
            	listSize=voucherList.size();
            	if (listSize != p_quantityRequested) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[loadPINAndSerialNumber]", "", "", "", "Not enough vouchers exist.");
                throw new BTSLBaseException(PretupsErrorCodesI.VOMS_NOT_ENOUGH_VOUCHERS);
            	}
            }

            if (listSize > 0) {
                StringBuilder sqlSelectUpdate = new StringBuilder("UPDATE voms_vouchers SET current_status=?, modified_by=? , modified_on=? , previous_status=?, ");
                sqlSelectUpdate.append("  last_consumed_by=?, last_consumed_on=?,last_transaction_id=?,status=?, user_network_code=?");
                sqlSelectUpdate.append("  WHERE serial_no=?");

                if (_log.isDebugEnabled())
                    _log.debug("loadPINAndSerialNumber", "Update Query=" + sqlSelectUpdate.toString());
                dbPsUpdate = p_con.prepareStatement(sqlSelectUpdate.toString());

                for (i = 0; i < listSize; i++) {
                    voucherVO = (VOMSVoucherVO) voucherList.get(i);
                    dbPsUpdate.setString(1, voucherVO.getCurrentStatus());
                    dbPsUpdate.setString(2, voucherVO.getModifiedBy());
                    dbPsUpdate.setTimestamp(3, InterfaceUtil.getTimestampFromUtilDate(voucherVO.getModifiedOn()));
                    dbPsUpdate.setString(4, voucherVO.getPreviousStatus());
                    dbPsUpdate.setString(5, voucherVO.getModifiedBy());
                    dbPsUpdate.setTimestamp(6, InterfaceUtil.getTimestampFromUtilDate(voucherVO.getModifiedOn()));
                    dbPsUpdate.setString(7, voucherVO.getTransactionID());
                    dbPsUpdate.setString(8, voucherVO.getCurrentStatus());
                    dbPsUpdate.setString(9, voucherVO.getUserLocationCode());
                    // dbPsUpdate.setString(10,voucherVO.getPreviousStatus());
                    dbPsUpdate.setString(10, voucherVO.getSerialNo());
                    updateCount = updateCount + dbPsUpdate.executeUpdate();
                    dbPsUpdate.clearParameters();
                }

                // StringBuilder selectCountEnSmry = new
                // StringBuilder("select VOUCHER_COUNT from VOMS_ENABLE_SUMMARY");
                // selectCountEnSmry.append(" WHERE PRODUCT_ID=? AND CREATED_DATE=? AND EXPIRY_DATE=? FOR UPDATE OF VOUCHER_COUNT ");

                // if (_log.isDebugEnabled())
                // _log.debug("loadPINAndSerialNumber","Select Voucher Enable Summary="+selectCountEnSmry.toString());

                StringBuilder sqlUpdateEnableSmry = new StringBuilder("UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT=VOUCHER_COUNT-1 ");
                sqlUpdateEnableSmry.append(" WHERE PRODUCT_ID=? AND CREATED_DATE=? AND EXPIRY_DATE=? ");

                if (_log.isDebugEnabled())
                    _log.debug("loadPINAndSerialNumber", "Update Voucher Enable Summary=" + sqlUpdateEnableSmry.toString());

                // dbSelectUpdate=p_con.prepareStatement(selectCountEnSmry.toString());
                dbENUpdate = p_con.prepareStatement(sqlUpdateEnableSmry.toString());

                for (i = 0; i < listSize; i++) {
                    // dbSelectUpdate.clearParameters();
                    dbENUpdate.clearParameters();

                    voucherVO = (VOMSVoucherVO) voucherList.get(i);
                    // dbSelectUpdate.setString(1, voucherVO.getProductID());
                    // dbSelectUpdate.setDate(2,BTSLUtil.getSQLDateFromUtilDate(voucherVO.getCreatedOn()));
                    // dbSelectUpdate.setDate(3,BTSLUtil.getSQLDateFromUtilDate(voucherVO.getExpiryDate()));
                    // rst=dbSelectUpdate.executeQuery();
                    // while(rst.next())
                    // {
                    // voucherCount=rst.getInt("VOUCHER_COUNT");
                    // }
                    // voucherCount=voucherCount-1;
                    int count = 1;
                    // dbENUpdate.setInt(1, voucherCount);
                    dbENUpdate.setString(count++, voucherVO.getProductID());
                    dbENUpdate.setDate(count++, BTSLUtil.getSQLDateFromUtilDate(voucherVO.getCreatedOn()));
                    dbENUpdate.setDate(count++, BTSLUtil.getSQLDateFromUtilDate(voucherVO.getExpiryDate()));

                    updateEnableCount = updateEnableCount + dbENUpdate.executeUpdate();

                }

                if (updateCount == p_quantityRequested && updateCount == updateEnableCount) {
                    // Comitting the transaction here only
                    p_con.commit();
                } else {
                    p_con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[loadPINAndSerialNumber]", "", "", p_productVO.getNetworkCode(), "Not able to mark Serial No=" + ((VOMSVoucherVO) voucherList.get(0)).getSerialNo() + "-" + ((VOMSVoucherVO) voucherList.get(listSize - 1)).getSerialNo() + " to status=" + p_newStatus);
                    throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_ERROR_UPDATION);
                }
            } else {
                throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_PRODUCT_REQUESTED);
            }
            if (_log.isDebugEnabled())
                _log.debug("loadPINAndSerialNumber", "After executing the query loadPINAndSerialNumber method voucherList size=" + voucherList.size());
            return voucherList;
        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
            }
            throw be;
        }// end of catch
        catch (SQLException sqle) {
            try {
                p_con.rollback();
            } catch (Exception e) {
            }
            _log.error("loadPINAndSerialNumber", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadPINAndSerialNumber]", "", "", "", "Exception:" + sqle.getMessage());
            // throw new BTSLBaseException(this, "loadPINAndSerialNumber",
            // "error.general.sql.processing");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            try {
                p_con.rollback();
            } catch (Exception ex) {
            }
            _log.error("loadPINAndSerialNumber", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadPINAndSerialNumber]", "", "", "", "Exception:" + e.getMessage());
            // throw new BTSLBaseException(this, "loadPINAndSerialNumber",
            // "error.general.processing");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.error("loadPINAndSerialNumber", " Exception while closing rs ex=" + ex);
            }
            try {
                if (rst != null)
                    rst.close();
            } catch (Exception ex) {
                _log.error("loadPINAndSerialNumber", " Exception while closing rs ex=" + ex);
            }
            try {
                if (dbPs != null)
                    dbPs.close();
            } catch (Exception ex) {
                _log.error("loadPINAndSerialNumber", " Exception while closing prepared statement ex=" + ex);
            }
            try {
                if (dbPsUpdate != null)
                    dbPsUpdate.close();
            } catch (Exception ex) {
                _log.error("loadPINAndSerialNumber", " Exception while closing prepared statement ex=" + ex);
            }
            try {
                if (dbENUpdate != null)
                    dbENUpdate.close();
            } catch (Exception ex) {
                _log.error("loadPINAndSerialNumber", " Exception while closing prepared statement ex=" + ex);
            }
            // try{ if(dbSelectUpdate
            // !=null)dbSelectUpdate.close();}catch(Exception
            // ex){_log.error("loadPINAndSerialNumber"," Exception while closing prepared statement ex="+ex);}
            try {
                _log.debug("loadPINAndSerialNumber", " Exiting voucherList size=" + voucherList.size());
            } catch (Exception e) {
                _log.error("loadPINAndSerialNumber", " Exception while closing rs ex=" + e);
            }
            ;
        }
    }*/

	/**
	 * Method updateVoucherStatus
	 * This method will Get the PIN & serial number details of voucher. Only
	 * voucher with status enabled will
	 * be picked from VOMS_VOUCHERS table.
	 * 
	 * @param Connection
	 *            p_con
	 * @param VomsVoucherVO
	 *            p_voucherVO
	 * @return int updateCount
	 * @exception BTSLBaseException
	 * @author Amit Ruwali
	 */

	public static int updateVoucherStatus(Connection p_con, VOMSVoucherVO p_voucherVO) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("updateVoucherStatus", " Entered p_voucherVO=" + p_voucherVO);
		PreparedStatement dbPs = null;
		int updateCount = -1;
		try {
			StringBuilder sqlSelectBuf = new StringBuilder("UPDATE voms_vouchers SET current_status=?, modified_by=? , modified_on=? , previous_status=?, ");
			sqlSelectBuf.append("  last_consumed_by=?, last_consumed_on=?,last_transaction_id=?,status=?, user_network_code=?,FIRST_CONSUMED_ON=?,SUBSCRIBER_ID=?");
			sqlSelectBuf.append("  WHERE current_status=?  AND serial_no=?");

			if (_log.isDebugEnabled())
				_log.debug("updateVoucherStatus", "Select Query=" + sqlSelectBuf.toString());
			dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
			dbPs.setString(1, p_voucherVO.getCurrentStatus());
			dbPs.setString(2, p_voucherVO.getModifiedBy());
			dbPs.setTimestamp(3, InterfaceUtil.getTimestampFromUtilDate(p_voucherVO.getModifiedOn()));
			dbPs.setString(4, p_voucherVO.getPreviousStatus());
			dbPs.setString(5, p_voucherVO.getModifiedBy());
			dbPs.setTimestamp(6, InterfaceUtil.getTimestampFromUtilDate(p_voucherVO.getModifiedOn()));
			dbPs.setString(7, p_voucherVO.getTransactionID());
			dbPs.setString(8, p_voucherVO.getCurrentStatus());
			dbPs.setString(9, p_voucherVO.getUserLocationCode());
			dbPs.setTimestamp(10, InterfaceUtil.getTimestampFromUtilDate(p_voucherVO.getModifiedOn()));
			dbPs.setString(11, p_voucherVO.getFirstConsumedBy());
			dbPs.setString(12, p_voucherVO.getPreviousStatus());
			dbPs.setString(13, p_voucherVO.getSerialNo());
			updateCount = dbPs.executeUpdate();
			if (updateCount > 0 && VOMSI.VOUCHER_ENABLE.equalsIgnoreCase(p_voucherVO.getCurrentStatus())) {
				updateCount = updateEnableVoucherSummary(p_con, p_voucherVO);
			}
			return updateCount;
		} catch (SQLException sqle) {
			_log.error("updateVoucherStatus", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[updateVoucherStatus]", "", "", "", "Exception:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",
			// "error.general.sql.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("updateVoucherStatus", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[updateVoucherStatus]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",
			// "error.general.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("updateVoucherStatus", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				_log.debug("updateVoucherStatus", " Exiting updateCount=" + updateCount);
			} catch (Exception e) {
				_log.error("updateVoucherStatus", " Exception while closing rs ex=" + e);
			}
			;
		}
	}

	/**
	 * Method insertDetailsInVoucherAudit
	 * This method will add the entry in voms_voucher_audit table.
	 * 
	 * @param Connection
	 *            p_con
	 * @param VomsVoucherVO
	 *            p_voucherVO
	 * @return int updateCount
	 * @exception BTSLBaseException
	 * @author Amit Ruwali
	 */

	public int insertDetailsInVoucherAudit(Connection p_con, VOMSVoucherVO p_voucherVO) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("insertDetailsInVoucherAudit", " Entered p_voucherVO=" + p_voucherVO);
		PreparedStatement dbPs = null;
		int updateCount = -1;
		try {
			StringBuilder sqlInsertBuf = new StringBuilder("INSERT INTO voms_voucher_audit (row_id,serial_no,");
			sqlInsertBuf.append("current_status,previous_status,modified_by,modified_on,status_change_source, ");
			sqlInsertBuf.append("status_change_partner_id,batch_no,message,process_status) VALUES (VOUCHER_AUDIT_ID.nextval,?,?,?,?,?,?,?,?,?,? )");

			if (_log.isDebugEnabled())
				_log.debug("insertDetailsInVoucherAudit", "Insert Query=" + sqlInsertBuf.toString());

			dbPs = p_con.prepareStatement(sqlInsertBuf.toString());
			int i = 1;
			dbPs.setString(i++, p_voucherVO.getSerialNo());
			dbPs.setString(i++, p_voucherVO.getCurrentStatus());
			dbPs.setString(i++, p_voucherVO.getPreviousStatus());
			dbPs.setString(i++, p_voucherVO.getModifiedBy());
			dbPs.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_voucherVO.getModifiedOn()));
			dbPs.setString(i++, p_voucherVO.getStatusChangeSource());
			dbPs.setString(i++, p_voucherVO.getStatusChangePartnerID());
			dbPs.setString(i++, p_voucherVO.getBatchNo());
			dbPs.setString(i++, p_voucherVO.getMessage());
			dbPs.setString(i++, p_voucherVO.getProcessStatus());

			updateCount = dbPs.executeUpdate();
			return updateCount;
		} catch (SQLException sqle) {
			_log.error("insertDetailsInVoucherAudit", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertDetailsInVoucherAudit]", "", "", "", "Exception:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "insertDetailsInVoucherAudit",
			// "error.general.sql.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("insertDetailsInVoucherAudit", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertDetailsInVoucherAudit]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "insertDetailsInVoucherAudit",
			// "error.general.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("insertDetailsInVoucherAudit", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				_log.debug("insertDetailsInVoucherAudit", " Exiting ");
			} catch (Exception e) {
				_log.error("insertDetailsInVoucherAudit", " Exception while closing rs ex=" + e);
			}
			;
		}
	}

	/**
	 * Method insertDetailsInVoucherAudit
	 * This method will add the entry in voms_voucher_audit table.
	 * 
	 * @param Connection
	 *            p_con
	 * @param ArrayList
	 *            p_voucherList
	 * @return int updateCount
	 * @throws BTSLBaseException
	 * @author Ankit Singhal
	 */

	public int insertDetailsInVoucherAudit(Connection p_con, ArrayList p_voucherList) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("insertDetailsInVoucherAudit", " Entered p_voucherList Size=" + p_voucherList.size());
		PreparedStatement dbPs = null;
		int updateCount = 0;
		VomsVoucherVO voucherVO = null;
		try {
			StringBuilder sqlInsertBuf = new StringBuilder("INSERT INTO voms_voucher_audit (row_id,serial_no,");
			sqlInsertBuf.append("current_status,previous_status,modified_by,modified_on,status_change_source, ");
			sqlInsertBuf.append("status_change_partner_id,batch_no,message,process_status) VALUES (VOUCHER_AUDIT_ID.nextval,?,?,?,?,?,?,?,?,?,? )");

			if (_log.isDebugEnabled())
				_log.debug("insertDetailsInVoucherAudit", "Insert Query=" + sqlInsertBuf.toString());

			dbPs = p_con.prepareStatement(sqlInsertBuf.toString());
			for (int j = 0, listSize = p_voucherList.size(); j < listSize; j++) {
				voucherVO = (VomsVoucherVO) p_voucherList.get(j);
				int i = 1;
				dbPs.setString(i++, voucherVO.getSerialNo());
				dbPs.setString(i++, voucherVO.getCurrentStatus());
				dbPs.setString(i++, voucherVO.getPreviousStatus());
				dbPs.setString(i++, voucherVO.getModifiedBy());
				dbPs.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(voucherVO.getModifiedOn()));
				dbPs.setString(i++, voucherVO.getStatusChangeSource());
				dbPs.setString(i++, voucherVO.getStatusChangePartnerID());
				dbPs.setString(i++, voucherVO.getBatchNo());
				dbPs.setString(i++, voucherVO.getMessage());
				dbPs.setString(i++, voucherVO.getProcessStatus());
				updateCount = updateCount + dbPs.executeUpdate();
				dbPs.clearParameters();
			}
			return updateCount;
		} catch (SQLException sqle) {
			_log.error("insertDetailsInVoucherAudit", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSVoucherDAO[insertDetailsInVoucherAudit]", "", "", "", "SQLException:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "insertDetailsInVoucherAudit",
			// "error.general.sql.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("insertDetailsInVoucherAudit", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSVoucherDAO[insertDetailsInVoucherAudit]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "insertDetailsInVoucherAudit",
			// "error.general.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("insertDetailsInVoucherAudit", " Exception while closing prepared statement ex=" + ex);
			}
			_log.debug("insertDetailsInVoucherAudit", " Exiting ");
		}
	}

	/**
	 * Method updateVoucherStatus
	 * This method will Get the PIN & serial number details of voucher. Only
	 * voucher with status enabled will
	 * be picked from VOMS_VOUCHERS table.
	 * 
	 * @param Connection
	 *            p_con
	 * @param Arraylist
	 *            p_voucherList
	 * @return int updateCount
	 * @exception BTSLBaseException
	 */

	public static int updateVoucherStatus(Connection p_con, ArrayList p_voucherList) throws BTSLBaseException {
		VomsVoucherVO voucherVO = null;
		if (_log.isDebugEnabled())
			_log.debug("updateVoucherStatus", " Entered voucher list size=" + p_voucherList.size());
		PreparedStatement dbPs = null;
		int updateCount = 0;
		try {
			StringBuilder sqlUpdateBuf = new StringBuilder("UPDATE voms_vouchers SET current_status=?, modified_by=? , modified_on=? , previous_status=?, ");
			sqlUpdateBuf.append("  last_consumed_by=?, last_consumed_on=?,last_transaction_id=?,status=?, user_network_code=?");
			sqlUpdateBuf.append("  , sale_batch_no=?");
			sqlUpdateBuf.append("  WHERE current_status=?  AND serial_no in (?)");

			if (_log.isDebugEnabled())
				_log.debug("updateVoucherStatus", "Select Query=" + sqlUpdateBuf.toString());
			dbPs = p_con.prepareStatement(sqlUpdateBuf.toString());
			for (int i = 0, size = p_voucherList.size(); i < size; i++) {
				voucherVO = (VomsVoucherVO) p_voucherList.get(i);
				dbPs.setString(1, voucherVO.getCurrentStatus());
				dbPs.setString(2, voucherVO.getModifiedBy());
				dbPs.setTimestamp(3, InterfaceUtil.getTimestampFromUtilDate(voucherVO.getModifiedOn()));
				dbPs.setString(4, voucherVO.getPreviousStatus());
				dbPs.setString(5, voucherVO.getModifiedBy());
				dbPs.setTimestamp(6, InterfaceUtil.getTimestampFromUtilDate(voucherVO.getModifiedOn()));
				dbPs.setString(7, voucherVO.getTransactionID());
				dbPs.setString(8, voucherVO.getCurrentStatus());
				dbPs.setString(9, voucherVO.getUserLocationCode());
				if (!BTSLUtil.isNullString(voucherVO.getSaleBatchNo())) {
					dbPs.setString(10, voucherVO.getSaleBatchNo());
				} else
					dbPs.setString(10, null);
				dbPs.setString(11, voucherVO.getPreviousStatus());
				dbPs.setString(12, voucherVO.getSerialNo());
				updateCount = updateCount + dbPs.executeUpdate();
			}
			return updateCount;
		} catch (SQLException sqle) {
			_log.error("updateVoucherStatus", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSVoucherDAO[updateVoucherStatus]", "", "", "", "SQLException:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",
			// "error.general.sql.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("updateVoucherStatus", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSVoucherDAO[updateVoucherStatus]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",
			// "error.general.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("updateVoucherStatus", " Exception while closing prepared statement ex=" + ex);
			}
			_log.debug("updateVoucherStatus", " Exiting updateCount=" + updateCount);
		}
	}

	/**
	 * This method will Get the PIN & serial number details of voucher. Only
	 * voucher with status consumed will
	 * be picked from VOMS_VOUCHERS table with common Sale_batch_no.
	 * 
	 * @param p_con
	 * @param p_newStatus
	 * @param p_networkCode
	 * @param p_quantityRequested
	 * @return voucherList
	 * @throws BTSLBaseException
	 */
	public static synchronized ArrayList loadPINAndSerialNumberForMVDFileDownload(Connection p_con, String p_saleBatchNumber) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("loadPINAndSerialNumberForMVDFileDownload", " Entered.. p_saleBatchNumber=" + p_saleBatchNumber);
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		VOMSVoucherVO voucherVO = null;
		int updateCount = 0;
		ArrayList voucherList = null;
		CryptoUtil cryptoUtil = null;
		int i = 0;
		int listSize = 0;
		try {
			StringBuilder sqlSelectBuf = new StringBuilder("SELECT vps.mrp, vps.validity, vms.expiry_date, vms.serial_no, vms.generation_batch_no, vms.pin_no, vms.last_transaction_id, vms.current_status ");
			sqlSelectBuf.append("FROM VOMS_VOUCHERS vms, VOMS_PRODUCTS vps WHERE vms.current_status=? ");
			sqlSelectBuf.append("AND vms.sale_batch_no=? ");
			sqlSelectBuf.append("AND vms.product_id = vps.product_id");
			if (_log.isDebugEnabled())
				_log.debug("loadPINAndSerialNumberForMVDFileDownload", "Select Query=" + sqlSelectBuf.toString());
			dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
			dbPs.setString(1, VOMSI.VOUCHER_USED);
			dbPs.setString(2, p_saleBatchNumber);
			rs = dbPs.executeQuery();
			voucherList = new ArrayList();
			cryptoUtil = new CryptoUtil();
			while (rs.next()) {
				voucherVO = new VOMSVoucherVO();
				voucherVO.setSerialNo(rs.getString("serial_no"));
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_ENCRIPTION_ALLOWED))).booleanValue())
					voucherVO.setPinNo(cryptoUtil.decrypt((rs.getString("pin_no")), Constants.KEY));
				else
					voucherVO.setPinNo(BTSLUtil.decryptText(rs.getString("pin_no")));
				voucherVO.setTransactionID(rs.getString("last_transaction_id"));
				voucherVO.setCurrentStatus(rs.getString("current_status"));
				voucherVO.setMRP(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
				voucherVO.setValidity(rs.getInt("validity"));
				voucherVO.setExpiryDateStr(rs.getString("expiry_date"));
				voucherVO.setExpiryDate(rs.getDate("expiry_date"));
				voucherVO.setGenerationBatchNo(rs.getString("generation_batch_no"));
				voucherList.add(voucherVO);
			}
			listSize = voucherList.size();
			return voucherList;
		} catch (SQLException sqle) {
			try {
				p_con.rollback();
			} catch (Exception e) {
			}
			_log.error("loadPINAndSerialNumberForMVDFileDownload", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadPINAndSerialNumberForMVDFileDownload]", "", "", "", "Exception:" + sqle.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			try {
				p_con.rollback();
			} catch (Exception ex) {
			}
			_log.error("loadPINAndSerialNumberForMVDFileDownload", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadPINAndSerialNumberForMVDFileDownload]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("loadPINAndSerialNumberForMVDFileDownload", " Exception while closing rs ex=" + ex);
			}
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("loadPINAndSerialNumberForMVDFileDownload", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				_log.debug("loadPINAndSerialNumberForMVDFileDownload", " Exiting voucherList size=" + voucherList.size());
			} catch (Exception e) {
				_log.error("loadPINAndSerialNumberForMVDFileDownload", " Exception while closing rs ex=" + e);
			}
			;
		}
	}

	/**
	 * This method will Get the list of denominations of voucher active in
	 * PreTUPS system.
	 * 
	 * @return voucherDenomList
	 * @throws BTSLBaseException
	 */
	public static synchronized ArrayList loadDenominationForBulkVoucherDistribution() throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("loadDenominationForBulkVoucherDistribution", " Entered.. ");
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		VOMSVoucherVO voucherVO = null;
		int updateCount = 0;
		ArrayList voucherDenomList = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ListValueVO listVO = null;
		int i = 0;
		int listSize = 0;
		try {
			StringBuilder sqlSelectBuf = new StringBuilder("select PRODUCT_ID,MRP from VOMS_PRODUCTS where status=?");
			if (_log.isDebugEnabled())
				_log.debug("loadDenominationForBulkVoucherDistribution", "Select Query=" + sqlSelectBuf.toString());
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			dbPs = con.prepareStatement(sqlSelectBuf.toString());
			dbPs.setString(1, PretupsI.YES);
			rs = dbPs.executeQuery();
			voucherDenomList = new ArrayList();
			while (rs.next()) {
				listVO = new ListValueVO(rs.getString("PRODUCT_ID"), PretupsBL.getDisplayAmount(rs.getLong("MRP")));
				voucherDenomList.add(listVO);
			}
			return voucherDenomList;
		} catch (SQLException sqle) {
			try {
				con.rollback();
			} catch (Exception e) {
			}
			_log.error("loadDenominationForBulkVoucherDistribution", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadPINAndSerialNumberForMVDFileDownload]", "", "", "", "Exception:" + sqle.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception ex) {
			}
			_log.error("loadDenominationForBulkVoucherDistribution", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadPINAndSerialNumberForMVDFileDownload]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("loadPINAndSerialNumberForMVDFileDownload", " Exception while closing rs ex=" + ex);
			}
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("loadPINAndSerialNumberForMVDFileDownload", " Exception while closing prepared statement ex=" + ex);
			}
			if(mcomCon != null)
			{
				mcomCon.close("VOMSVoucherDAO#loadDenominationForBulkVoucherDistribution");
				mcomCon=null;
			}
			try {
				_log.debug("loadDenominationForBulkVoucherDistribution", " Exiting voucherList size=" + voucherDenomList.size());
			} catch (Exception e) {
				_log.error("loadPINAndSerialNumberForMVDFileDownload", " Exception while closing rs ex=" + e);
			}
			;
		}
	}

	/**
	 * This method will Get the vouchers downloaded by a channel user.
	 * 
	 * @return voucherDenomList
	 * @throws BTSLBaseException
	 */
	public HashMap loadDownloadedVouchersForEnquiry(Connection p_con, String p_userID, boolean p_IsBatchIdEneterd, String p_batchID, String p_denomination, String p_fromdate, String p_toDate) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("loadDownloadedVouchersForEnquiry", " Entered.. p_userID:" + p_userID + " p_IsBatchIdEneterd:" + p_IsBatchIdEneterd + " p_batchID:" + p_batchID + " p_denomination:" + p_denomination + " p_fromdate:" + p_fromdate + " p_toDate:" + p_toDate);
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		VOMSVoucherVO voucherVO = null;
		int updateCount = 0;
		ArrayList voucherDenomList = null;
		HashMap voucherBatchDetailsMap = null;
		// Connection con = null;
		String saleBatchNo = null;
		CryptoUtil cryptoUtil = new CryptoUtil();
		int i = 0;
		int listSize = 0;
		try {
			String sqlSelectBuf =vomsVoucherQry.loadDownloadedVouchersForEnquiryQry(p_IsBatchIdEneterd);

			if (_log.isDebugEnabled())
				_log.debug("loadDownloadedVouchersForEnquiry", "Select Query=" + sqlSelectBuf);
			// con=OracleUtil.getConnection();
			dbPs = p_con.prepareStatement(sqlSelectBuf);
			if (p_IsBatchIdEneterd) {
				dbPs.setString(1, p_batchID);
				dbPs.setString(2, VOMSI.VOUCHER_USED);
				dbPs.setString(3, p_userID);
			} else {
				dbPs.setLong(1, PretupsBL.getSystemAmount(p_denomination));
				dbPs.setDate(2, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_fromdate)));
				dbPs.setDate(3, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_toDate)));
				dbPs.setString(4, VOMSI.VOUCHER_USED);
				dbPs.setString(5, p_userID);
			}
			rs = dbPs.executeQuery();
			voucherDenomList = new ArrayList();
			voucherBatchDetailsMap = new HashMap();
			List tempListOfVO = new ArrayList();
			while (rs.next()) {
				voucherVO = new VOMSVoucherVO();
				voucherVO.setSerialNo(rs.getString("SERIAL_NO"));
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_ENCRIPTION_ALLOWED))).booleanValue())
					voucherVO.setPinNo(cryptoUtil.decrypt((rs.getString("PIN_NO")), Constants.KEY));
				else
					voucherVO.setPinNo(rs.getString("PIN_NO"));
				voucherVO.setTransactionID(rs.getString("LAST_TRANSACTION_ID"));
				voucherVO.setStatus(rs.getString("CURRENT_STATUS"));
				voucherVO.setMRP(rs.getLong("MRP"));
				voucherVO.setGenerationBatchNo(rs.getString("GENERATION_BATCH_NO"));
				voucherVO.setExpiryDateStr(rs.getString("EXPIRY_DATE"));
				voucherVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
				voucherVO.setValidity(rs.getInt("VALIDITY"));
				saleBatchNo = rs.getString("SALE_BATCH_NO");
				if (voucherBatchDetailsMap.containsKey(saleBatchNo)) {
					// List tempListOfVO =
					// (ArrayList)voucherBatchDetailsMap.get(saleBatchNo);
					tempListOfVO.add(voucherVO);
					voucherBatchDetailsMap.put(saleBatchNo, tempListOfVO);
				} else {
					tempListOfVO = new ArrayList();
					tempListOfVO.add(voucherVO);
					voucherBatchDetailsMap.put(saleBatchNo, tempListOfVO);
				}
			}
			return voucherBatchDetailsMap;
		} catch (SQLException sqle) {
			try {
				p_con.rollback();
			} catch (Exception e) {
			}
			_log.error("loadDownloadedVouchersForEnquiry", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadDownloadedVouchersForEnquiry]", "", "", "", "Exception:" + sqle.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			try {
				p_con.rollback();
			} catch (Exception ex) {
			}
			_log.error("loadDownloadedVouchersForEnquiry", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadDownloadedVouchersForEnquiry]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("loadDownloadedVouchersForEnquiry", " Exception while closing rs ex=" + ex);
			}
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("loadDownloadedVouchersForEnquiry", " Exception while closing prepared statement ex=" + ex);
			}
			// try{if(con!=null)con.close();}catch (Exception e) {}
			try {
				_log.debug("loadDownloadedVouchersForEnquiry", " Exiting voucherBatchDetailsMap size=" + voucherBatchDetailsMap.size());
			} catch (Exception e) {
				_log.error("loadDownloadedVouchersForEnquiry", " Exception while closing rs ex=" + e);
			}
			;
		}
	}

	/**
	 * Method to update enable count in ENABLE_VOUCHER_SUMMARY table
	 * 
	 * @param p_con
	 * @param p_VoucherVO
	 * @return
	 * @throws BTSLBaseException
	 */
	private static int updateEnableVoucherSummary(Connection p_con, VOMSVoucherVO p_VoucherVO) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("updateEnableVoucherSummary", "Entered previous_status=" + p_VoucherVO.getPreviousStatus() + ", current status=" + p_VoucherVO.getCurrentStatus());

		PreparedStatement sqlUpdate = null;
		PreparedStatement dbSelectUpdate = null;
		ResultSet rst = null;
		int j = 1, updateCount = -1, voucherCount = 0;
		Date createdDate = null;
		Date expiryDate = null;
		String productId = null;
		try {
			StringBuilder selectCountEnSmry = new StringBuilder("select ES.VOUCHER_COUNT,VV.CREATED_DATE,VV.EXPIRY_DATE,VV.PRODUCT_ID from VOMS_ENABLE_SUMMARY ES,VOMS_VOUCHERS VV");
			selectCountEnSmry.append(" WHERE VV.SERIAL_NO=? AND VV.PRODUCT_ID=ES.PRODUCT_ID AND ES.CREATED_DATE=VV.CREATED_DATE AND VV.EXPIRY_DATE=ES.EXPIRY_DATE ");
			dbSelectUpdate = p_con.prepareStatement(selectCountEnSmry.toString());

			if (_log.isDebugEnabled())
				_log.debug("updateEnableVoucherSummary", "select Voucher Enable Summary=" + selectCountEnSmry.toString());

			StringBuilder sqlUpdateEnableSmry = new StringBuilder();
			sqlUpdateEnableSmry.append("UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT=?");
			sqlUpdateEnableSmry.append(" WHERE PRODUCT_ID=? AND CREATED_DATE=? AND EXPIRY_DATE=? ");
			sqlUpdate = p_con.prepareStatement(sqlUpdateEnableSmry.toString());

			if (_log.isDebugEnabled())
				_log.debug("updateEnableVoucherSummary", "Update Voucher Enable Summary=" + sqlUpdateEnableSmry.toString());

			dbSelectUpdate.setString(1, p_VoucherVO.getSerialNo());

			rst = dbSelectUpdate.executeQuery();
			while (rst.next()) {
				voucherCount = rst.getInt("VOUCHER_COUNT");
				createdDate = rst.getDate("CREATED_DATE");
				expiryDate = rst.getDate("EXPIRY_DATE");
				productId = rst.getString("PRODUCT_ID");
			}
			voucherCount = voucherCount + 1;
			sqlUpdate.setInt(1, voucherCount);
			sqlUpdate.setString(2, productId);
			sqlUpdate.setDate(3, BTSLUtil.getSQLDateFromUtilDate(createdDate));
			sqlUpdate.setDate(4, BTSLUtil.getSQLDateFromUtilDate(expiryDate));

			updateCount = sqlUpdate.executeUpdate();
			sqlUpdate.clearParameters();

		} catch (SQLException sqle) {
			_log.error("updateEnableVoucherSummary", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSVoucherDAO[updateEnableVoucherSummary]", "", "", "", "SQLException:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",
			// "error.general.sql.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("updateEnableVoucherSummary", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSVoucherDAO[updateEnableVoucherSummary]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",
			// "error.general.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (sqlUpdate != null)
					sqlUpdate.close();
			} catch (Exception ex) {
				_log.error("updateEnableVoucherSummary", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				if (dbSelectUpdate != null)
					dbSelectUpdate.close();
			} catch (Exception ex) {
				_log.error("updateEnableVoucherSummary", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				if (rst != null)
					rst.close();
			} catch (Exception ex) {
				_log.error("updateEnableVoucherSummary", " Exception while closing resultset ex=" + ex);
			}
			if (_log.isDebugEnabled())
				_log.debug("updateEnableVoucherSummary", " Exiting updateCount=" + updateCount);
		}
		return updateCount;
	}

	/**
	 * Method LoadActiveProfiles
	 * This method will load the active profiles based von networkcode and
	 * current date
	 * 
	 * @param Connection
	 *            p_con
	 * @param String
	 *            p_networkCode
	 * @param Date
	 *            p_currDate
	 * @return HashMap profileMap
	 * @exception BTSLBaseException
	 * @author Amit Ruwali
	 */

	public HashMap loadActiveProfilesForPrivateRecharge(Connection p_con, String p_networkCode, Date p_currDate, boolean p_isTimeStamp) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("loadActiveProfilesForPrivateRecharge", " Entered.. p_networkCode=" + p_networkCode + "p_currDate=" + p_currDate + "p_isTimeStamp=" + p_isTimeStamp);
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		HashMap profileMap = null;
		VOMSProductVO productVO = null;
		try {
			String sqlSelectBuf = vomsVoucherQry.loadActiveProfilesForPrivateRechargeQry(p_isTimeStamp);
			if (_log.isDebugEnabled())
				_log.debug("loadActiveProfiles", "Select Query=" + sqlSelectBuf);
			dbPs = p_con.prepareStatement(sqlSelectBuf);
			dbPs.setString(1, p_networkCode);
			if (p_isTimeStamp)
				dbPs.setTimestamp(2, InterfaceUtil.getTimestampFromUtilDate(p_currDate));
			else
				dbPs.setDate(2, InterfaceUtil.getSQLDateFromUtilDate(p_currDate));
			dbPs.setString(3, p_networkCode);
			rs = dbPs.executeQuery();
			profileMap = new HashMap();
			while (rs.next()) {
				productVO = new VOMSProductVO();
				productVO.setProductID(rs.getString("product_id"));
				productVO.setProductName(rs.getString("product_name"));
				productVO.setMrpStr(String.valueOf(rs.getLong("mrp")));
				productVO.setStatus(rs.getString("status"));
				productVO.setProductCode(rs.getInt("product_code"));
				productVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
				productVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
				productVO.setTalkTime(rs.getLong("talktime"));
				productVO.setValidity(rs.getInt("validity"));
				productVO.setType(rs.getString("type"));
				productVO.setNetworkCode(p_networkCode);
				profileMap.put(productVO.getMrpStr() + "_" + productVO.getType(), productVO);
			}
			if (_log.isDebugEnabled())
				_log.debug("loadActiveProfilesForPrivateRecharge", "After executing the query loadBatchLogList method profileMap=" + profileMap.size());
			return profileMap;
		} catch (SQLException sqle) {
			_log.error("loadActiveProfilesForPrivateRecharge", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfilesForPrivateRecharge]", "", "", "", "Exception:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "loadActiveProfiles",
			// "error.general.sql.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("loadActiveProfiles", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfiles]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "loadActiveProfiles",
			// "error.general.processing");
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("loadActiveProfiles", " Exception while closing rs ex=" + ex);
			}
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("loadActiveProfiles", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				_log.debug("loadActiveProfiles", " Exiting..batchList size=" + profileMap.size());
			} catch (Exception e) {
				_log.error("loadActiveProfiles", " Exception while closing rs ex=" + e);
			}
		}
	}

	/**
	 * Method updateVoucherStatusWithPIN
	 * This method will Get the PIN & serial number details of voucher. Only
	 * voucher with status enabled will
	 * be picked from VOMS_VOUCHERS table.
	 * 
	 * @param Connection
	 *            p_con
	 * @param VomsVoucherVO
	 *            p_voucherVO
	 * @return int updateCount
	 * @exception BTSLBaseException
	 */

	public static int updateVoucherStatusWithPIN(Connection p_con, VOMSVoucherVO p_voucherVO) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("updateVoucherStatusWithPIN", " Entered p_voucherVO=" + p_voucherVO.toString());
		PreparedStatement dbPs = null;
		int i = 1;
		int updateCount = -1;
		try {
			StringBuilder sqlSelectBuf = new StringBuilder("UPDATE voms_vouchers SET current_status=?, modified_by=? , modified_on=? , previous_status=?, ");
			sqlSelectBuf.append("  last_consumed_by=?, last_consumed_on=?,last_transaction_id=?,status=?, user_network_code=?");
			if (!InterfaceUtil.isNullString(p_voucherVO.getPinNo()))
				sqlSelectBuf.append(", pin_no=?");
			sqlSelectBuf.append("  WHERE current_status=?  AND serial_no=?");

			if (_log.isDebugEnabled())
				_log.debug("updateVoucherStatusWithPIN", "Select Query=" + sqlSelectBuf.toString());
			dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
			dbPs.setString(i++, p_voucherVO.getCurrentStatus());
			dbPs.setString(i++, p_voucherVO.getModifiedBy());
			dbPs.setTimestamp(i++, InterfaceUtil.getTimestampFromUtilDate(p_voucherVO.getModifiedOn()));
			dbPs.setString(i++, p_voucherVO.getPreviousStatus());
			dbPs.setString(i++, p_voucherVO.getModifiedBy());
			dbPs.setTimestamp(i++, InterfaceUtil.getTimestampFromUtilDate(p_voucherVO.getModifiedOn()));
			dbPs.setString(i++, p_voucherVO.getTransactionID());
			dbPs.setString(i++, p_voucherVO.getCurrentStatus());
			dbPs.setString(i++, p_voucherVO.getUserLocationCode());
			if (!InterfaceUtil.isNullString(p_voucherVO.getPinNo()))
				dbPs.setString(i++, p_voucherVO.getPinNo());
			dbPs.setString(i++, p_voucherVO.getPreviousStatus());
			dbPs.setString(i++, p_voucherVO.getSerialNo());
			updateCount = dbPs.executeUpdate();
			return updateCount;
		} catch (SQLException sqle) {
			_log.error("updateVoucherStatusWithPIN", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[updateVoucherStatusWithPIN]", "", "", "", "Exception:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("updateVoucherStatusWithPIN", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[updateVoucherStatusWithPIN]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("updateVoucherStatusWithPIN", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				_log.debug("updateVoucherStatusWithPIN", " Exiting updateCount=" + updateCount);
			} catch (Exception e) {
				_log.error("updateVoucherStatusWithPIN", " Exception while closing rs ex=" + e);
			}
			;
		}
	}

	// Zeeshan Aleem
	public ArrayList validateVoucherCode(Connection p_con, String voucher_code, String serial_number, String p_modifiedBy, Date p_modifiedOn, String p_source, String p_newStatus, ArrayList p_transactionIdList, String p_networkCode) throws BTSLBaseException {
		String methodName = "validateVoucherCode";
		System.out.println(serial_number);
		if (_log.isDebugEnabled())
			_log.debug(methodName, " Entered..");
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		VOMSVoucherVO voucherVO = null;
		PreparedStatement dbPsUpdate = null;
		PreparedStatement dbENUpdate = null;
		// PreparedStatement dbSelectUpdate = null;
		ResultSet rst = null;
		int updateCount = 0, updateEnableCount = 0;
		ArrayList voucherList = null;
		// int voucherCount=0;
		int i = 0;
		int listSize = 0;
		try {
			StringBuilder sqlSelectBuf = new StringBuilder("SELECT VV.serial_no,VV.pin_no,VV.expiry_date,VV.previous_status,VV.seq_no,VV.created_date,VV.product_id ");
			sqlSelectBuf.append(",VP.talktime,VC.PAYABLE_AMOUNT,VC.TYPE FROM VOMS_VOUCHERS VV, VOMS_CATEGORIES VC,VOMS_PRODUCTS VP,ENABLE_SUMMARY_DATES ESD");
			sqlSelectBuf.append(" WHERE VV.PRODUCT_ID=VP.PRODUCT_ID AND VP.CATEGORY_ID=VC.CATEGORY_ID AND ");
			if (serial_number != null)
				sqlSelectBuf.append("  VV.CURRENT_STATUS=? AND VV.SERIAL_NO=? AND VV.PIN_NO like ?");
			else
				sqlSelectBuf.append("  VV.CURRENT_STATUS=? AND VV.PIN_NO=?");

			// sqlSelectBuf.append("AND VV.CREATED_DATE = ESD.CRE_DATE ");
			// DB220120123for update WITH RS


			/*

			 * ("databasetype")))
			 * sqlSelectBuf.append(
			 * "AND ROWNUM<? FOR UPDATE OF VV.CURRENT_STATUS WITH RS ");
			 * else
			 * sqlSelectBuf.append("AND ROWNUM<? FOR UPDATE OF VV.CURRENT_STATUS "
			 * );
			 */
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Select Query=" + sqlSelectBuf.toString());
			dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
			dbPs.setString(1, VOMSI.VOUCHER_ENABLE);
			if (!BTSLUtil.isNullString(serial_number)) {
				dbPs.setString(2, serial_number);
				dbPs.setString(3, "%" + voucher_code + "%");
			} else
				dbPs.setString(2, voucher_code);

			// dbPs.setString(3,p_productVO.getProductID());
			// dbPs.setString(4,String.valueOf(p_quantityRequested+1));

			rs = dbPs.executeQuery();
			voucherList = new ArrayList();
			while (rs.next()) {
				voucherVO = new VOMSVoucherVO();
				voucherVO.setSerialNo(rs.getString("serial_no"));
				voucherVO.setPinNo(rs.getString("pin_no"));
				voucherVO.setPreviousStatus(VOMSI.VOUCHER_ENABLE);
				voucherVO.setCurrentStatus(p_newStatus);
				voucherVO.setModifiedBy(p_modifiedBy);
				voucherVO.setModifiedOn(p_modifiedOn);
				voucherVO.setStatusChangeSource(p_source);
				voucherVO.setStatusChangePartnerID(p_modifiedBy);
				voucherVO.setTransactionID((String) p_transactionIdList.get(i));
				voucherVO.setUserLocationCode(p_networkCode);
				voucherVO.setProductID(rs.getString("product_id"));
				voucherVO.setCreatedOn(rs.getDate("created_date"));
				voucherVO.setExpiryDate(rs.getDate("expiry_date"));
				voucherVO.setPayableAmount(rs.getLong("Payable_Amount"));
				voucherVO.setCategoryType(rs.getString("type"));
				/*

				 * {
				 * voucherVO.setTalkTime(rs.getLong("talktime"));
				 * voucherVO.setPayableAmount(rs.getLong("payable_amount"));
				 * }
				 */i++;
				 voucherList.add(voucherVO);
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		listSize = voucherList.size();
		// if required number of vouchers are not available, transaction is
		// rollback
		/*
		 * if (listSize!=p_quantityRequested)
		 * {
		 * //p_con.rollback();
		 * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
		 * EventStatusI
		 * .RAISED,EventLevelI.MAJOR,"VomsVoucherDAO[loadPINAndSerialNumber]"
		 * ,"","","","Not enough vouchers exist.");
		 * throw new
		 * BTSLBaseException(PretupsErrorCodesI.VOMS_NOT_ENOUGH_VOUCHERS);
		 * }
		 */

		if (listSize > 0) {
			try {
				StringBuilder sqlSelectUpdate = new StringBuilder("UPDATE voms_vouchers SET current_status=?, modified_by=? , modified_on=? , previous_status=?, ");
				sqlSelectUpdate.append("  last_consumed_by=?, last_consumed_on=?,last_transaction_id=?,status=?, user_network_code=?");
				sqlSelectUpdate.append("  WHERE serial_no=?");

				if (_log.isDebugEnabled())
					_log.debug(methodName, "Update Query=" + sqlSelectUpdate.toString());
				dbPsUpdate = p_con.prepareStatement(sqlSelectUpdate.toString());

				for (i = 0; i < listSize; i++) {
					voucherVO = (VOMSVoucherVO) voucherList.get(i);
					dbPsUpdate.setString(1, voucherVO.getCurrentStatus());
					dbPsUpdate.setString(2, voucherVO.getModifiedBy());
					dbPsUpdate.setTimestamp(3, InterfaceUtil.getTimestampFromUtilDate(voucherVO.getModifiedOn()));
					dbPsUpdate.setString(4, voucherVO.getPreviousStatus());
					dbPsUpdate.setString(5, voucherVO.getModifiedBy());
					dbPsUpdate.setTimestamp(6, InterfaceUtil.getTimestampFromUtilDate(voucherVO.getModifiedOn()));
					dbPsUpdate.setString(7, voucherVO.getTransactionID());
					dbPsUpdate.setString(8, voucherVO.getCurrentStatus());
					dbPsUpdate.setString(9, voucherVO.getUserLocationCode());
					// dbPsUpdate.setString(10,voucherVO.getPreviousStatus());
					dbPsUpdate.setString(10, voucherVO.getSerialNo());
					updateCount = updateCount + dbPsUpdate.executeUpdate();
					dbPsUpdate.clearParameters();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			// StringBuilder selectCountEnSmry = new
			// StringBuilder("select VOUCHER_COUNT from VOMS_ENABLE_SUMMARY");
			// selectCountEnSmry.append(" WHERE PRODUCT_ID=? AND CREATED_DATE=? AND EXPIRY_DATE=? FOR UPDATE OF VOUCHER_COUNT ");

			// if (_log.isDebugEnabled())
			// _log.debug("validateVoucherCode","Select Voucher Enable Summary="+selectCountEnSmry.toString());

			try {
				StringBuilder sqlUpdateEnableSmry = new StringBuilder("UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT=VOUCHER_COUNT-1 ");
				sqlUpdateEnableSmry.append(" WHERE PRODUCT_ID=? AND CREATED_DATE=? AND EXPIRY_DATE=? ");

				if (_log.isDebugEnabled())
					_log.debug(methodName, "Update Voucher Enable Summary=" + sqlUpdateEnableSmry.toString());

				// dbSelectUpdate=p_con.prepareStatement(selectCountEnSmry.toString());
				dbENUpdate = p_con.prepareStatement(sqlUpdateEnableSmry.toString());

				for (i = 0; i < listSize; i++) {
					// dbSelectUpdate.clearParameters();
					dbENUpdate.clearParameters();
					voucherVO = (VOMSVoucherVO) voucherList.get(i);
					// dbSelectUpdate.setString(1, voucherVO.getProductID());
					// dbSelectUpdate.setDate(2,BTSLUtil.getSQLDateFromUtilDate(voucherVO.getCreatedOn()));
					// dbSelectUpdate.setDate(3,BTSLUtil.getSQLDateFromUtilDate(voucherVO.getExpiryDate()));
					// rst=dbSelectUpdate.executeQuery();
					// while(rst.next())
					// {
					// voucherCount=rst.getInt("VOUCHER_COUNT");
					// }
					// voucherCount=voucherCount-1;
					int count = 1;
					// dbENUpdate.setInt(1, voucherCount);
					dbENUpdate.setString(count++, voucherVO.getProductID());
					dbENUpdate.setDate(count++, BTSLUtil.getSQLDateFromUtilDate(voucherVO.getCreatedOn()));
					dbENUpdate.setDate(count++, BTSLUtil.getSQLDateFromUtilDate(voucherVO.getExpiryDate()));

					updateEnableCount = updateEnableCount + dbENUpdate.executeUpdate();

				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (true)
				;
			/*
			 * if(updateCount==p_quantityRequested &&
			 * updateCount==updateEnableCount)
			 * {
			 * //Comitting the transaction here only
			 * p_con.commit();
			 * }
			 */
			else {
				try {
					p_con.rollback();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[loadPINAndSerialNumber]", "", "", p_networkCode, "Not able to mark Serial No=" + ((VOMSVoucherVO) voucherList.get(0)).getSerialNo() + "-" + ((VOMSVoucherVO) voucherList.get(listSize - 1)).getSerialNo() + " to status=" + p_newStatus);
				throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_ERROR_UPDATION);
			}
		} else {
			throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_PRODUCT_REQUESTED);
		}
		if (_log.isDebugEnabled())
			_log.debug(methodName, "After executing the query loadPINAndSerialNumber method voucherList size=" + voucherList.size());

		return voucherList;

	}

	/*public static synchronized ArrayList loadPINAndSerialNumberOld(Connection p_con, VOMSProductVO p_productVO,String p_modifiedBy,Date p_modifiedOn,String p_source,String p_newStatus,ArrayList p_transactionIdList, String p_networkCode,int p_quantityRequested) 
    		throws BTSLBaseException
    		{
    			if (_log.isDebugEnabled())
    			    _log.debug("loadPINAndSerialNumber"," Entered.. p_productVO="+p_productVO+" p_modifiedBy="+p_modifiedBy+" p_modifiedOn="+p_modifiedOn+" p_source="+p_source+" p_newStatus="+p_newStatus+" p_transactionIdList size="+p_transactionIdList+" p_networkCode="+p_networkCode+" p_quantityRequested="+p_quantityRequested);
    			PreparedStatement dbPs = null;
    			ResultSet rs = null;
    			VOMSVoucherVO voucherVO=null;
    			PreparedStatement dbPsUpdate = null;
    			int updateCount=0;
    			ArrayList voucherList=null;
    			int i=0;
    			int listSize=0;
    			try
    			{
    				StringBuilder sqlSelectBuf = new StringBuilder("SELECT VV.serial_no,VV.pin_no,VV.expiry_date,VV.previous_status,VV.seq_no");
    	            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
    	            	sqlSelectBuf.append(" FROM voms_vouchers VV WHERE ");
    	            else
    	            {
    	                sqlSelectBuf.append(",VC.payable_amount FROM voms_vouchers VV, voms_categories VC,voms_products VP  ");
    	                sqlSelectBuf.append(" WHERE VV.product_id=VP.product_id AND VP.category_id=VC.category_id AND ");
    	            }
    				sqlSelectBuf.append("  VV.current_status=? AND VV.product_id=? AND rownum<? FOR UPDATE OF VV.current_status ");
    				if (_log.isDebugEnabled())
    				    _log.debug("loadPINAndSerialNumber","Select Query="+sqlSelectBuf.toString());
    				dbPs=p_con.prepareStatement(sqlSelectBuf.toString());
    				dbPs.setString(1,VOMSI.VOUCHER_ENABLE);
    				//dbPs.setString(2,p_productVO.getNetworkCode());
    				dbPs.setString(2,p_productVO.getProductID());
    				dbPs.setString(3,String.valueOf(p_quantityRequested+1));

    				rs=dbPs.executeQuery();
    				voucherList=new ArrayList();
    				while(rs.next())
    				{
    				    voucherVO=new VOMSVoucherVO();
    				    voucherVO.setSerialNo(rs.getString("serial_no"));
    				    voucherVO.setPinNo(rs.getString("pin_no"));
    					voucherVO.setPreviousStatus(VOMSI.VOUCHER_ENABLE);
    					voucherVO.setCurrentStatus(p_newStatus);
    					voucherVO.setModifiedBy(p_modifiedBy);
    					voucherVO.setModifiedOn(p_modifiedOn);
    					voucherVO.setStatusChangeSource(p_source);
    					voucherVO.setStatusChangePartnerID(p_modifiedBy);
    					voucherVO.setTransactionID((String)p_transactionIdList.get(i));
    					voucherVO.setUserLocationCode(p_networkCode);
    	                if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
    	                     voucherVO.setPayableAmount(rs.getLong("payable_amount"));
    					i++;
    				    voucherList.add(voucherVO);
    				}
    				listSize=voucherList.size();

    			}
    			catch (SQLException sqle)
    			{
    			    try{p_con.rollback();}catch(Exception e){}
    			    _log.error("loadPINAndSerialNumber","SQLException "+sqle.getMessage());
    				sqle.printStackTrace();
    				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsVoucherDAO[loadPINAndSerialNumber]","","","","Exception:"+sqle.getMessage());
    				//throw new BTSLBaseException(this, "loadPINAndSerialNumber", "error.general.sql.processing");
    				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    			}//end of catch
    			catch (Exception e)
    			{
    			    try{p_con.rollback();}catch(Exception ex){}
    			    _log.error("loadPINAndSerialNumber","Exception "+e.getMessage());
    				e.printStackTrace();
    				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsVoucherDAO[loadPINAndSerialNumber]","","","","Exception:"+e.getMessage());
    				//throw new BTSLBaseException(this, "loadPINAndSerialNumber", "error.general.processing");
    				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    			}//end of catch
    			finally
    			{
    				try{if(rs!=null) rs.close(); }catch(Exception ex){_log.error("loadPINAndSerialNumber"," Exception while closing rs ex="+ex);}
    				try{ if(dbPs !=null)dbPs.close();}catch(Exception ex){_log.error("loadPINAndSerialNumber"," Exception while closing prepared statement ex="+ex);}
    				try{ if(dbPsUpdate !=null)dbPsUpdate.close();}catch(Exception ex){_log.error("loadPINAndSerialNumber"," Exception while closing prepared statement ex="+ex);}
    				try {_log.debug("loadPINAndSerialNumber"," Exiting voucherList size="+ voucherList.size());}catch(Exception e) {_log.error("loadPINAndSerialNumber"," Exception while closing rs ex="+e);} ;
    			}

    			return voucherList;
    		}*/

	/**
	 * This method will Get the PIN & serial number details of voucher. Only
	 * voucher with status enabled will
	 * be picked from VOMS_VOUCHERS table.
	 * 
	 * @param p_con
	 * @param p_productVO
	 * @param p_modifiedBy
	 * @param p_modifiedOn
	 * @param p_source
	 * @param p_newStatus
	 * @param p_transactionIdList
	 * @param p_networkCode
	 * @param p_quantityRequested
	 * @return voucherList
	 * @throws BTSLBaseException
	 */
	public static synchronized ArrayList loadPINAndSerialNumber(Connection p_con, VOMSProductVO p_productVO, String p_modifiedBy, Date p_modifiedOn, String p_source, String p_newStatus, ArrayList p_transactionIdList, String p_networkCode, int p_quantityRequested) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("loadPINAndSerialNumber", " Entered.. p_productVO=" + p_productVO + " p_modifiedBy=" + p_modifiedBy + " p_modifiedOn=" + p_modifiedOn + " p_source=" + p_source + " p_newStatus=" + p_newStatus + " p_transactionIdList size=" + p_transactionIdList + " p_networkCode=" + p_networkCode + " p_quantityRequested=" + p_quantityRequested);
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		VOMSVoucherVO voucherVO = null;
		PreparedStatement dbPsUpdate = null;
		PreparedStatement dbENUpdate = null;

		ResultSet rst = null;
		int updateCount = 0, updateEnableCount = 0;
		ArrayList voucherList = null;

		int i = 0;
		int listSize = 0;
		try {
			VOMSVoucherQry vomSVoucherQuery=(VOMSVoucherQry)ObjectProducer.getObject(QueryConstants.VOMS_VOUCHER_UMNIAH_QRY, QueryConstants.QUERY_PRODUCER);
			String sqlSelectBuf = vomSVoucherQuery.loadPINAndSerialNumberQry();
			if (_log.isDebugEnabled())
				_log.debug("loadPINAndSerialNumber", "Select Query=" + sqlSelectBuf);
			dbPs = p_con.prepareStatement(sqlSelectBuf);
			dbPs.setString(1, VOMSI.VOUCHER_ENABLE);
			// dbPs.setString(2,p_productVO.getNetworkCode());
			dbPs.setString(2, p_productVO.getProductID());
			dbPs.setString(3, p_productVO.getProductID());
			dbPs.setString(4, String.valueOf(p_quantityRequested + 1));

			rs = dbPs.executeQuery();
			voucherList = new ArrayList();
			while (rs.next()) {
				voucherVO = new VOMSVoucherVO();
				voucherVO.setSerialNo(rs.getString("serial_no"));
				voucherVO.setPinNo(rs.getString("pin_no"));
				voucherVO.setPreviousStatus(VOMSI.VOUCHER_ENABLE);
				voucherVO.setCurrentStatus(p_newStatus);
				voucherVO.setModifiedBy(p_modifiedBy);
				voucherVO.setModifiedOn(p_modifiedOn);
				voucherVO.setStatusChangeSource(p_source);
				voucherVO.setStatusChangePartnerID(p_modifiedBy);
				voucherVO.setTransactionID((String) p_transactionIdList.get(i));
				voucherVO.setUserLocationCode(p_networkCode);

				voucherVO.setProductID(rs.getString("product_id"));
				voucherVO.setCreatedOn(rs.getDate("created_date"));
				voucherVO.setExpiryDate(rs.getDate("expiry_date"));

				if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue()) {
					voucherVO.setTalkTime(rs.getLong("talktime"));
					voucherVO.setPayableAmount(rs.getLong("payable_amount"));
				}
				i++;
				voucherList.add(voucherVO);
			}
			listSize = voucherList.size();
			// if required number of vouchers are not available, transaction is
			// rollback
			if (listSize != p_quantityRequested) {
				// p_con.rollback();
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[loadPINAndSerialNumber]", "", "", "", "Not enough vouchers exist.");
				throw new BTSLBaseException(PretupsErrorCodesI.VOMS_NOT_ENOUGH_VOUCHERS);
			}

			if (listSize > 0) {
				StringBuilder sqlSelectUpdate = new StringBuilder("UPDATE voms_vouchers SET current_status=?, modified_by=? , modified_on=? , previous_status=?, ");
				sqlSelectUpdate.append("  last_consumed_by=?, last_consumed_on=?,last_transaction_id=?,status=?, user_network_code=?");
				sqlSelectUpdate.append("  WHERE serial_no=?");

				if (_log.isDebugEnabled())
					_log.debug("loadPINAndSerialNumber", "Update Query=" + sqlSelectUpdate.toString());
				dbPsUpdate = p_con.prepareStatement(sqlSelectUpdate.toString());

				for (i = 0; i < listSize; i++) {
					voucherVO = (VOMSVoucherVO) voucherList.get(i);
					dbPsUpdate.setString(1, voucherVO.getCurrentStatus());
					dbPsUpdate.setString(2, voucherVO.getModifiedBy());
					dbPsUpdate.setTimestamp(3, InterfaceUtil.getTimestampFromUtilDate(voucherVO.getModifiedOn()));
					dbPsUpdate.setString(4, voucherVO.getPreviousStatus());
					dbPsUpdate.setString(5, voucherVO.getModifiedBy());
					dbPsUpdate.setTimestamp(6, InterfaceUtil.getTimestampFromUtilDate(voucherVO.getModifiedOn()));
					dbPsUpdate.setString(7, voucherVO.getTransactionID());
					dbPsUpdate.setString(8, voucherVO.getCurrentStatus());
					dbPsUpdate.setString(9, voucherVO.getUserLocationCode());
					// dbPsUpdate.setString(10,voucherVO.getPreviousStatus());
					dbPsUpdate.setString(10, voucherVO.getSerialNo());
					updateCount = updateCount + dbPsUpdate.executeUpdate();
					dbPsUpdate.clearParameters();
				}

				// StringBuilder selectCountEnSmry = new

				// selectCountEnSmry.append(" WHERE PRODUCT_ID=? AND CREATED_DATE=? AND EXPIRY_DATE=? FOR UPDATE OF VOUCHER_COUNT ");


				// _log.debug("loadPINAndSerialNumber","Select Voucher Enable Summary="+selectCountEnSmry.toString());

				StringBuilder sqlUpdateEnableSmry = new StringBuilder("UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT=VOUCHER_COUNT-1 ");
				sqlUpdateEnableSmry.append(" WHERE PRODUCT_ID=? AND CREATED_DATE=? AND EXPIRY_DATE=? ");

				if (_log.isDebugEnabled())
					_log.debug("loadPINAndSerialNumber", "Update Voucher Enable Summary=" + sqlUpdateEnableSmry.toString());


				dbENUpdate = p_con.prepareStatement(sqlUpdateEnableSmry.toString());

				for (i = 0; i < listSize; i++) {

					dbENUpdate.clearParameters();

					voucherVO = (VOMSVoucherVO) voucherList.get(i);

					// dbSelectUpdate.setDate(2,BTSLUtil.getSQLDateFromUtilDate(voucherVO.getCreatedOn()));
					// dbSelectUpdate.setDate(3,BTSLUtil.getSQLDateFromUtilDate(voucherVO.getExpiryDate()));
					// rst=dbSelectUpdate.executeQuery();
					// while(rst.next())
					// {
					// voucherCount=rst.getInt("VOUCHER_COUNT");
					// }
					// voucherCount=voucherCount-1;
					int count = 1;

					dbENUpdate.setString(count++, voucherVO.getProductID());
					dbENUpdate.setDate(count++, BTSLUtil.getSQLDateFromUtilDate(voucherVO.getCreatedOn()));
					dbENUpdate.setDate(count++, BTSLUtil.getSQLDateFromUtilDate(voucherVO.getExpiryDate()));

					updateEnableCount = updateEnableCount + dbENUpdate.executeUpdate();

				}

				if (updateCount == p_quantityRequested && updateCount == updateEnableCount) {
					// Comitting the transaction here only
					p_con.commit();
				} else {
					p_con.rollback();
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[loadPINAndSerialNumber]", "", "", p_productVO.getNetworkCode(), "Not able to mark Serial No=" + ((VOMSVoucherVO) voucherList.get(0)).getSerialNo() + "-" + ((VOMSVoucherVO) voucherList.get(listSize - 1)).getSerialNo() + " to status=" + p_newStatus);
					throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_ERROR_UPDATION);
				}
			} else {
				throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_PRODUCT_REQUESTED);
			}
			if (_log.isDebugEnabled())
				_log.debug("loadPINAndSerialNumber", "After executing the query loadPINAndSerialNumber method voucherList size=" + voucherList.size());
			return voucherList;
		} catch (BTSLBaseException be) {
			try {
				p_con.rollback();
			} catch (Exception e) {
			}
			throw be;
		}// end of catch
		catch (SQLException sqle) {
			try {
				p_con.rollback();
			} catch (Exception e) {
			}
			_log.error("loadPINAndSerialNumber", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadPINAndSerialNumber]", "", "", "", "Exception:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "loadPINAndSerialNumber",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			try {
				p_con.rollback();
			} catch (Exception ex) {
			}
			_log.error("loadPINAndSerialNumber", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadPINAndSerialNumber]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "loadPINAndSerialNumber",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("loadPINAndSerialNumber", " Exception while closing rs ex=" + ex);
			}
			try {
				if (rst != null)
					rst.close();
			} catch (Exception ex) {
				_log.error("loadPINAndSerialNumber", " Exception while closing rs ex=" + ex);
			}
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("loadPINAndSerialNumber", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				if (dbPsUpdate != null)
					dbPsUpdate.close();
			} catch (Exception ex) {
				_log.error("loadPINAndSerialNumber", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				if (dbENUpdate != null)
					dbENUpdate.close();
			} catch (Exception ex) {
				_log.error("loadPINAndSerialNumber", " Exception while closing prepared statement ex=" + ex);
			}

			// !=null)dbSelectUpdate.close();}catch(Exception
			// ex){_log.error("loadPINAndSerialNumber"," Exception while closing prepared statement ex="+ex);}
			try {
				_log.debug("loadPINAndSerialNumber", " Exiting voucherList size=" + voucherList.size());
			} catch (Exception e) {
				_log.error("loadPINAndSerialNumber", " Exception while closing rs ex=" + e);
			}
			;
		}
	}

	public HashMap loadActiveProfilesForPrivateRecharge(Connection p_con, String p_networkCode, Date p_currDate, boolean p_isTimeStamp, String p_reqService) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("loadActiveProfilesForPrivateRecharge"," Entered.. p_networkCode="+p_networkCode +"p_currDate="+p_currDate+"p_isTimeStamp="+p_isTimeStamp+",p_reqService = "+p_reqService);
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		HashMap profileMap = null;
		VOMSProductVO productVO = null;
		try {
			StringBuilder sqlSelectBuf = new StringBuilder("SELECT vap.active_product_id,vap.applicable_from, ");
			sqlSelectBuf.append("vi.product_id,vop.category_id,vop.product_name,vop.short_name,vop.mrp, ");
			sqlSelectBuf.append("vop.status,vop.description,vop.product_code,vop.min_req_quantity,vop.max_req_quantity, ");
			sqlSelectBuf.append("vop.multiple_factor,vop.expiry_period,vop.individual_entity,vop.attribute1, ");
			sqlSelectBuf.append("vop.service_code,vop.no_of_arguments,vop.talktime,vop.validity,vc.type,vc.VOUCHER_TYPE FROM ");
			sqlSelectBuf.append("voms_active_products vap,voms_active_product_items vi,voms_products vop,voms_categories vc ,VOMS_VTYPE_SERVICE_MAPPING D ");
			sqlSelectBuf.append(" WHERE vap.active_product_id=vi.active_product_id AND vi.product_id=vop.product_id ");
			sqlSelectBuf.append("AND vop.category_id=vc.category_id AND vc.status<>'N' AND vop.status<>'N' ");
			sqlSelectBuf.append("AND vap.network_code=? AND vap.applicable_from=(SELECT MAX(applicable_from) ");
			sqlSelectBuf.append(" FROM voms_active_products vap2 WHERE ");
			if (p_isTimeStamp)
				sqlSelectBuf.append(" applicable_from <=? ");
			else
				sqlSelectBuf.append(" trunc(applicable_from) <=? ");
			sqlSelectBuf.append(" AND vap2.network_code=? AND vap2.status<>'N') ");
			//Added for Multi product EVD
			sqlSelectBuf.append(" AND D.SERVICE_TYPE= ? AND D.status <> 'N' AND D.VOUCHER_TYPE=VC.VOUCHER_TYPE ");
			if (_log.isDebugEnabled())
				_log.debug("loadActiveProfilesForPrivateRecharge","Select Query="+sqlSelectBuf.toString());
			dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
			dbPs.setString(1, p_networkCode);
			if (p_isTimeStamp)
				dbPs.setTimestamp(2, InterfaceUtil.getTimestampFromUtilDate(p_currDate));
			else
				dbPs.setDate(2, InterfaceUtil.getSQLDateFromUtilDate(p_currDate));
			dbPs.setString(3, p_networkCode);
			dbPs.setString(4,p_reqService);
			rs = dbPs.executeQuery();
			profileMap = new HashMap();
			while (rs.next()) {
				productVO = new VOMSProductVO();
				productVO.setProductID(rs.getString("product_id"));
				productVO.setProductName(rs.getString("product_name"));
				productVO.setMrpStr(String.valueOf(rs.getLong("mrp")));
				productVO.setStatus(rs.getString("status"));
				productVO.setProductCode(rs.getInt("product_code"));
				productVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
				productVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
				productVO.setTalkTime(rs.getLong("talktime"));
				productVO.setValidity(rs.getInt("validity"));
				productVO.setType(rs.getString("type"));
				productVO.setNetworkCode(p_networkCode);
				profileMap.put(productVO.getMrpStr() + "_" + productVO.getType(), productVO);
			}
			if (_log.isDebugEnabled())
				_log.debug("loadActiveProfilesForPrivateRecharge", "After executing the query loadBatchLogList method profileMap=" + profileMap.size());
			return profileMap;
		} catch (SQLException sqle) {
			_log.error("loadActiveProfilesForPrivateRecharge", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfilesForPrivateRecharge]", "", "", "", "Exception:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "loadActiveProfiles",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("loadActiveProfilesForPrivateRecharge","Exception "+e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfiles]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "loadActiveProfiles",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("loadActiveProfiles", " Exception while closing rs ex=" + ex);
			}
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("loadActiveProfiles", " Exception while closing prepared statement ex=" + ex);
			}
			try {_log.debug("loadActiveProfilesForPrivateRecharge"," Exiting..batchList size="+profileMap.size());
			} catch (Exception e) {
				{_log.error("loadActiveProfilesForPrivateRecharge"," Exception while closing rs ex="+e);} 
			}
		}
	}

	/**
	 * Method LoadActiveProfiles
	 * This method will load the active profiles based von networkcode and
	 * current date
	 * 
	 * @param Connection
	 *            p_con
	 * @param String
	 *            p_networkCode
	 * @param Date
	 *            p_currDate
	 * @return HashMap profileMap
	 * @exception BTSLBaseException
	 * @author Amit Ruwali
	 */

	public HashMap loadActiveProfiles(Connection p_con, String p_networkCode, Date p_currDate, boolean p_isTimeStamp,String p_reqService) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("loadActiveProfiles", " Entered.. p_networkCode=" + p_networkCode + "p_currDate=" + p_currDate + "p_isTimeStamp=" + p_isTimeStamp+",p_reqService="+p_reqService);
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		HashMap profileMap = null;
		VOMSProductVO productVO = null;
		try {
			StringBuilder sqlSelectBuf = new StringBuilder("SELECT vap.active_product_id,vap.applicable_from, ");
			sqlSelectBuf.append("vi.product_id,vop.category_id,vop.product_name,vop.short_name,vop.mrp, ");
			sqlSelectBuf.append("vop.status,vop.description,vop.product_code,vop.min_req_quantity,vop.max_req_quantity, ");
			sqlSelectBuf.append("vop.multiple_factor,vop.expiry_period,vop.individual_entity,vop.attribute1, ");
			sqlSelectBuf.append("vop.service_code,vop.no_of_arguments,vop.talktime,vop.validity,vc.VOUCHER_TYPE FROM ");
			sqlSelectBuf.append("voms_active_products vap,voms_active_product_items vi,voms_products vop,voms_categories vc,VOMS_VTYPE_SERVICE_MAPPING D ");
			sqlSelectBuf.append(" WHERE vap.active_product_id=vi.active_product_id AND vi.product_id=vop.product_id ");
			sqlSelectBuf.append("AND vop.category_id=vc.category_id AND vc.status<>'N' AND vop.status<>'N' ");
			sqlSelectBuf.append("AND vap.network_code=? AND vap.applicable_from=(SELECT MAX(applicable_from) ");
			sqlSelectBuf.append(" FROM voms_active_products vap2 WHERE ");
			if (p_isTimeStamp)
				sqlSelectBuf.append(" applicable_from <=? ");
			else
				sqlSelectBuf.append(" trunc(applicable_from) <=? ");
			sqlSelectBuf.append(" AND vap2.network_code=? AND vap2.status<>'N') ");
			sqlSelectBuf.append(" AND D.SERVICE_TYPE= ? AND D.status <> 'N' AND D.VOUCHER_TYPE=VC.VOUCHER_TYPE ");
			if (_log.isDebugEnabled())
				_log.debug("loadActiveProfiles", "Select Query=" + sqlSelectBuf.toString());
			dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
			dbPs.setString(1, p_networkCode);
			if (p_isTimeStamp)
				dbPs.setTimestamp(2, InterfaceUtil.getTimestampFromUtilDate(p_currDate));
			else
				dbPs.setDate(2, InterfaceUtil.getSQLDateFromUtilDate(p_currDate));
			dbPs.setString(3, p_networkCode);
			dbPs.setString(4,p_reqService);
			rs = dbPs.executeQuery();
			profileMap = new HashMap();
			while (rs.next()) {
				productVO = new VOMSProductVO();
				productVO.setProductID(rs.getString("product_id"));
				productVO.setProductName(rs.getString("product_name"));
				productVO.setMrpStr(String.valueOf(rs.getLong("mrp")));
				productVO.setStatus(rs.getString("status"));
				productVO.setProductCode(rs.getInt("product_code"));
				productVO.setMaxReqQuantity(rs.getLong("max_req_quantity"));
				productVO.setMinReqQuantity(rs.getLong("min_req_quantity"));
				productVO.setTalkTime(rs.getLong("talktime"));
				productVO.setValidity(rs.getInt("validity"));
				productVO.setNetworkCode(p_networkCode);
				profileMap.put(productVO.getMrpStr(), productVO);
			}
			if (_log.isDebugEnabled())
				_log.debug("loadActiveProfiles", "After executing the query loadBatchLogList method profileMap=" + profileMap.size());
			return profileMap;
		} catch (SQLException sqle) {
			_log.error("loadActiveProfiles", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfiles]", "", "", "", "Exception:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "loadActiveProfiles",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("loadActiveProfiles", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfiles]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "loadActiveProfiles",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				_log.error("loadActiveProfiles", " Exception while closing rs ex=" + ex);
			}
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("loadActiveProfiles", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				_log.debug("loadActiveProfiles", " Exiting..batchList size=" + profileMap.size());
			} catch (Exception e) {
				_log.error("loadActiveProfiles", " Exception while closing rs ex=" + e);
			}
		}
	}

	/**
	 * Method updateVoucherStatus
	 * This method will Get the PIN & serial number details of voucher. Only
	 * voucher with status enabled will
	 * be picked from VOMS_VOUCHERS table.
	 * 
	 * @param Connection
	 *            p_con
	 * @param VomsVoucherVO
	 *            p_voucherVO
	 * @return int updateCount
	 * @exception BTSLBaseException
	 * @author Amit Ruwali
	 */

	public static int updateVoucherStatusRollback(Connection p_con, VOMSVoucherVO p_voucherVO) throws BTSLBaseException {
		if (_log.isDebugEnabled())
			_log.debug("updateVoucherStatus", " Entered p_voucherVO=" + p_voucherVO);
		PreparedStatement dbPs = null;
		int updateCount = -1;
		try {
			StringBuilder sqlSelectBuf = new StringBuilder("UPDATE voms_vouchers SET current_status=?, modified_by=? , modified_on=? , previous_status=?, ");
			sqlSelectBuf.append("  last_consumed_by=?, last_consumed_on=?,last_transaction_id=?,status=?, user_network_code=?,FIRST_CONSUMED_ON=?,SUBSCRIBER_ID=?");
			sqlSelectBuf.append("  WHERE current_status=?  AND serial_no=?");

			if (_log.isDebugEnabled())
				_log.debug("updateVoucherStatus", "Select Query=" + sqlSelectBuf.toString());
			dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
			dbPs.setString(1, p_voucherVO.getCurrentStatus());
			dbPs.setString(2, p_voucherVO.getModifiedBy());
			dbPs.setTimestamp(3, InterfaceUtil.getTimestampFromUtilDate(p_voucherVO.getModifiedOn()));
			dbPs.setString(4, p_voucherVO.getPreviousStatus());
			dbPs.setString(5, "");
			dbPs.setTimestamp(6, null);
			dbPs.setString(7, "");
			dbPs.setString(8, p_voucherVO.getCurrentStatus());
			dbPs.setString(9, p_voucherVO.getUserLocationCode());
			dbPs.setTimestamp(10, null);
			dbPs.setString(11, "");
			dbPs.setString(12, p_voucherVO.getPreviousStatus());
			dbPs.setString(13, p_voucherVO.getSerialNo());
			updateCount = dbPs.executeUpdate();
			if (updateCount > 0 && VOMSI.VOUCHER_ENABLE.equalsIgnoreCase(p_voucherVO.getCurrentStatus())) {
				updateCount = updateEnableVoucherSummary(p_con, p_voucherVO);
			}

			return updateCount;
		} catch (SQLException sqle) {
			_log.error("updateVoucherStatus", "SQLException " + sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[updateVoucherStatus]", "", "", "", "Exception:" + sqle.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		catch (Exception e) {
			_log.error("updateVoucherStatus", "Exception " + e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[updateVoucherStatus]", "", "", "", "Exception:" + e.getMessage());
			// throw new BTSLBaseException(this, "updateVoucherStatus",

			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}// end of catch
		finally {
			try {
				if (dbPs != null)
					dbPs.close();
			} catch (Exception ex) {
				_log.error("updateVoucherStatus", " Exception while closing prepared statement ex=" + ex);
			}
			try {
				_log.debug("updateVoucherStatus", " Exiting updateCount=" + updateCount);
			} catch (Exception e) {
				_log.error("updateVoucherStatus", " Exception while closing rs ex=" + e);
			}
			;
		}
	}

}