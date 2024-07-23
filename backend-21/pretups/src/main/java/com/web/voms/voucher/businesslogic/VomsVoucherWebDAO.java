package com.web.voms.voucher.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsPrintBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VomsVoucherWebDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private VomsVoucherWebQry vomsVoucherWebQry;

    public VomsVoucherWebDAO() {
        super();
        vomsVoucherWebQry = (VomsVoucherWebQry)ObjectProducer.getObject(QueryConstants.VOMS_VOUCHER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * This method will load the batch log information
     * 
     * @param con
     *            of Connection type
     * @param p_batchNo
     *            of String type
     * @return returns the ArrayList
     * @exception SQLException
     * @exception Exception
     */
    public ArrayList loadBatchLogList(Connection p_con, String p_batchNo) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchLogList", " Entered.. p_batchNo=" + p_batchNo);
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        ArrayList batchList = null;
        VomsVoucherVO voucherVO = null;
        final String methodName = "loadBatchLogList";
        try {
            String sqlSelectBuf = vomsVoucherWebQry.loadBatchLogListQry();
        	if (_log.isDebugEnabled()) {
                _log.debug("loadBatchLogList", "Select Query=" + sqlSelectBuf);
            }
            dbPs = p_con.prepareStatement(sqlSelectBuf);
            dbPs.setString(1, p_batchNo);
            dbPs.setString(2, VOMSI.VA_PROCESS_ERROR_STAT);
            rs = dbPs.executeQuery();
            batchList = new ArrayList();
            while (rs.next()) {
                voucherVO = new VomsVoucherVO();
                voucherVO.setSerialNo(rs.getString("SERIALNO"));
                voucherVO.setPreviousStatus(rs.getString("PREVSTAT"));
                voucherVO.setPrevStatusModifiedBy(rs.getString("MODIFIEDBY"));
                voucherVO.setPrevStatusModifiedOn(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("MODIFIEDON")));
                voucherVO.setVoucherStatus(rs.getString("CURRENTSTAT"));
                voucherVO.setStatusChangeSource(rs.getString("STATCHSRC"));
                voucherVO.setExpiryDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("EXPDATE")));
                voucherVO.setLastErrorMessage(rs.getString("MESSAGE"));
                batchList.add(voucherVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadBatchLogList", "After executing the query loadBatchLogList method batchList=" + batchList.size());
            }
            return batchList;
        } catch (SQLException sqle) {
            _log.error("loadBatchLogList", "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherWebDAO[loadBatchLogList]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBatchLogList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadBatchLogList", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherWebDAO[loadBatchLogList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadBatchLogList", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("loadBatchLogList", " Exception while closing rs ex=" + ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error("loadBatchLogList", " Exception while closing prepared statement ex=" + ex);
            }
            try {
                _log.debug("loadBatchLogList", " Exiting..batchList size=" + batchList.size());
            } catch (Exception e) {
                _log.error("loadBatchLogList", " Exception while closing rs ex=" + e);
            }
            
        }
    }

    /**
     * @param p_con
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     * @author rahul.dutt
     *         this method is used to get voms decrypt key for a channel user
     */
    public String getVomsDecKeyUser(Connection p_con, String p_userID) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getVomsDecKeyUser", " Entered p_userID" + p_userID);
        }
        final String METHOD_NAME = "getVomsDecKeyUser";
        PreparedStatement pselect = null;
        ResultSet rs = null;
        String dec_key = null;
        try {
            final StringBuilder sqlSelectBuf = new StringBuilder(" SELECT voms_decryp_key FROM channel_users WHERE user_id=? ");
            if (_log.isDebugEnabled()) {
                _log.debug("getVomsDecKeyUser", "Select Query=" + sqlSelectBuf.toString());
            }
            pselect = p_con.prepareStatement(sqlSelectBuf.toString());
            pselect.setString(1, p_userID);
            rs = pselect.executeQuery();
            while (rs.next()) {
                dec_key = rs.getString("voms_decryp_key");
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getVomsDecKeyUser", "dec_key" + dec_key);
            }
            return dec_key;
        } catch (SQLException sqle) {
            _log.error("getVomsDecKeyUser", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherWebDAO[getVomsDecKeyUser]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "getVomsDecKeyUser", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("getVomsDecKeyUser", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherWebDAO[getVomsDecKeyUser]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getVomsDecKeyUser", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("getVomsDecKeyUser", " Exception while closing rs ex=" + ex);
            }
            try {
                if (pselect != null) {
                    pselect.close();
                }
            } catch (Exception ex) {
                _log.error("getVomsDecKeyUser", " Exception while closing prepared statement ex=" + ex);
            }
        }
    }

    /**
     * @param p_con
     * @param p_userID
     * @param p_key
     * @return
     * @throws BTSLBaseException
     * @author rahul.dutt
     *         to update voms dec key for a user
     */
    public int updateVomsDecKeyUser(Connection p_con, String p_userID, String pkey) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_userID:");
        	msg.append(p_userID);
        	msg.append("p_key:");
        	msg.append(pkey);
      
        	String message=msg.toString();
            _log.debug("updateVomsDecKeyUser", message);
        }
        final String METHOD_NAME = "updateVomsDecKeyUser";
        PreparedStatement psupdate = null;
        int count = 0;
        try {
            final StringBuilder sqlSelectBuf = new StringBuilder(" UPDATE channel_users set voms_decryp_key=? WHERE user_id=? ");
            if (_log.isDebugEnabled()) {
                _log.debug("updateVomsDecKeyUser", "Select Query=" + sqlSelectBuf.toString());
            }
            psupdate = p_con.prepareStatement(sqlSelectBuf.toString());
            psupdate.setString(1, pkey);
            psupdate.setString(2, p_userID);
            count = psupdate.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug("updateVomsDecKeyUser", "count" + count);
            }
            return count;
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            _log.error("updateVomsDecKeyUser", "SQLException " + sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherWebDAO[updateVomsDecKeyUser]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateVomsDecKeyUser", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateVomsDecKeyUser", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherWebDAO[updateVomsDecKeyUser]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateVomsDecKeyUser", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psupdate != null) {
                    psupdate.close();
                }
            } catch (Exception ex) {
                _log.error("updateVomsDecKeyUser", " Exception while closing prepared statement ex=" + ex);
            }
        }
    }

    /**
     * @param p_con
     * @param p_fromserial
     * @param p_toserial
     * @param p_printbatch
     * @return
     * @throws BTSLBaseException
     * @author rahul.dutt
     *         loads voucher list for a specific print batch
     */
    public ArrayList getVomsVoucherList(Connection pcon, String p_fromserial, String p_toserial, String p_printbatch, VomsPrintBatchVO p_vomsPrintBatchVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_fromserial");
        	msg.append(p_fromserial);
        	msg.append("p_toserial");
        	msg.append(p_toserial);
        	msg.append("p_printbatch");
        	msg.append(p_printbatch);
        	
        	String message=msg.toString();
            _log.debug("getVomsVoucherList", message);
        }
        PreparedStatement pselect = null;
        ResultSet rs = null;
        ArrayList voucherlist = null;
        VomsVoucherVO voucherVO = null;
        String tablename = null;
        final String methodName = "getVomsVoucherList";
        try {
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                final boolean matchFound = BTSLUtil.validateTableName(p_vomsPrintBatchVO.getVoucherType());
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + p_vomsPrintBatchVO.getVoucherType() + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }

            String sqlSelectBuf=vomsVoucherWebQry.getVomsVoucherListQry(tablename);
            if (_log.isDebugEnabled()) {
                _log.debug("getVomsVoucherList", "Select Query=" + sqlSelectBuf);
            }
            pselect = pcon.prepareStatement(sqlSelectBuf);
            pselect.setLong(1, Long.parseLong(p_fromserial));
            pselect.setLong(2, Long.parseLong(p_toserial));
            pselect.setString(3, p_printbatch);
            rs = pselect.executeQuery();
            voucherlist = new ArrayList();
            while (rs.next()) {
                voucherVO = new VomsVoucherVO();
                voucherVO.setSerialNo(rs.getString("serial_no"));
                voucherVO.setProductID(rs.getString("product_id"));
                voucherVO.setPinNo(rs.getString("pin_no"));
                voucherVO.setCurrentStatus(rs.getString("current_status"));
                voucherVO.setExpiryDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("expiry_date")));
                voucherVO.setExpiryDateStr(BTSLUtil.getDateStringFromDate(voucherVO.getExpiryDate()));
                voucherVO.setUserLocationCode(rs.getString("user_network_code"));
                voucherVO.setStatus(rs.getString("user_network_code"));
                voucherVO.setTalkTime(rs.getLong("talktime"));
                voucherVO.setValidity(rs.getInt("validity"));
                voucherVO.setMRP(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                voucherVO.setUserID(rs.getString("user_id"));
                voucherlist.add(voucherVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getVomsVoucherList", "voucherlist" + voucherlist.size());
            }
            return voucherlist;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            _log.error("getVomsVoucherList", "SQLException " + sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherWebDAO[getVomsVoucherList]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "getVomsVoucherList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("getVomsVoucherList", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherWebDAO[getVomsVoucherList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getVomsVoucherList", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("getVomsVoucherList", " Exception while closing rs ex=" + ex);
            }
            try {
                if (pselect != null) {
                    pselect.close();
                }
            } catch (Exception ex) {
                _log.error("getVomsVoucherList", " Exception while closing prepared statement ex=" + ex);
            }
        }
    }
}
