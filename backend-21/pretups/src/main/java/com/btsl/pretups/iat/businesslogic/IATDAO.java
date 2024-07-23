package com.btsl.pretups.iat.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.iat.transfer.businesslogic.IATInterfaceVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATTransferItemVO;
import com.btsl.util.OracleUtil;

public class IATDAO {
    private static final Log _log = LogFactory.getLog(IATDAO.class.getName());

    /**
     * Load the Iat country master cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public ArrayList loadIATCountryMasterList() throws BTSLBaseException {

        final String methodName = "loadIATCountryMasterList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList arr = new ArrayList();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT icm.rec_country_code,icm.rec_country_short_name,icm.rec_country_name,icm.currency, ");
        strBuff.append(" icm.prefix_length,icm.min_msisdn_length ,icm.max_msisdn_length ,icm.country_status,icm.language1_msg,icm.language2_msg,icm.country_serial_id ");
        strBuff.append(" FROM iat_country_master icm where icm.country_status<>?");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadIATCountryMasterList", "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.IAT_COUNTRY_STATUS_DELETED);
            rs = pstmt.executeQuery();
            IATCountryMasterVO countryMasterVO = null;
            while (rs.next()) {
                countryMasterVO = new IATCountryMasterVO();

                countryMasterVO.setRecCountryCode(rs.getInt("rec_country_code"));
                countryMasterVO.setRecCountryName(rs.getString("rec_country_name"));
                countryMasterVO.setRecCountryShortName(rs.getString("rec_country_short_name"));
                countryMasterVO.setCurrency(rs.getString("currency"));
                countryMasterVO.setPrefixLength(rs.getInt("prefix_length"));
                countryMasterVO.setMinMsisdnLength(rs.getInt("min_msisdn_length"));
                countryMasterVO.setMaxMsisdnLength(rs.getInt("max_msisdn_length"));
                countryMasterVO.setCountryStatus(rs.getString("country_status"));
                countryMasterVO.setLanguage1Message(rs.getString("language1_msg"));
                countryMasterVO.setLanguage2Message(rs.getString("language2_msg"));
                countryMasterVO.setCountrySerialID(rs.getString("country_serial_id"));
                arr.add(countryMasterVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATCountryMasterList]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATCountryMasterList]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
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
                _log.debug(methodName, "Exiting: networkMap size=" + arr.size());
            }
        }
        return arr;
    }

    /**
     * Load the Iat network country cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public ArrayList loadIATNetworkCountryList() throws BTSLBaseException {
        final String methodName = "loadIATNetworkCountryList";

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList networkCountryList = new ArrayList();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT incm.rec_country_short_name, incm.rec_nw_code ,incm.rec_nw_name ,incm.rec_nw_prefixes,incm.status  ");
        strBuff.append(" FROM iat_nw_country_mapping incm where incm.status <> ?");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.IAT_NETWORK_STATUS_DELETED);
            rs = pstmt.executeQuery();

            IATNetworkCountryMappingVO iatNetworkCountryMappingVO = null;
            while (rs.next()) {
                iatNetworkCountryMappingVO = new IATNetworkCountryMappingVO();

                iatNetworkCountryMappingVO.setRecCountryShortName(rs.getString("rec_country_short_name"));
                iatNetworkCountryMappingVO.setRecNetworkCode(rs.getString("rec_nw_code"));
                iatNetworkCountryMappingVO.setRecNetworkName(rs.getString("rec_nw_name"));
                iatNetworkCountryMappingVO.setRecNetworkPrefix(rs.getString("rec_nw_prefixes"));
                iatNetworkCountryMappingVO.setStatus(rs.getString("status"));
                networkCountryList.add(iatNetworkCountryMappingVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATNetworkCountryMap]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATNetworkCountryMap]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
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
                _log.debug(methodName, "Exiting: networkMap size=" + networkCountryList.size());
            }
        }
        return networkCountryList;
    }

    /**
     * Load the Iat network service cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap loadIATNetworkServiceMap() throws BTSLBaseException {

        final String methodName = "loadIATNetworkServiceMap";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap map = new HashMap();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" select	insm.rec_country_short_name, insm.rec_nw_code, ");
        strBuff.append(" i.interface_id,i.external_id,i.interface_description,i.interface_type_id,i.status,i.clouser_date,");
        strBuff.append(" i.message_language1,i.message_language2,i.concurrent_connection,i.single_state_transaction,i.status_type,");
        strBuff.append("i.val_expiry_time,i.topup_expiry_time,it.interface_type_id typeid, it.interface_name,it.interface_category,it.handler_class,it.underprocess_msg_reqd,");
        strBuff.append(" insm.iat_name ,insm.iat_code ,insm.service_type,insm.service_status,insm.lang1_message ,insm.lang2_message,insm.iat_ip, insm.iat_port");
        strBuff.append(" FROM  interfaces i,interface_types it,iat_nw_service_mapping insm");
        strBuff.append(" WHERE it.interface_type_id=i.interface_type_id AND insm.iat_code=i.interface_id AND insm.service_status=? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.IAT_SERVICE_STATUS_ACTIVE);
            rs = pstmt.executeQuery();
            IATNetworkServiceMappingVO iatNetworkServiceMappingVO = null;
            while (rs.next()) {
                iatNetworkServiceMappingVO = new IATNetworkServiceMappingVO();

                iatNetworkServiceMappingVO.setIatCode(rs.getString("iat_code"));
                iatNetworkServiceMappingVO.setIatip(rs.getString("iat_ip"));
                iatNetworkServiceMappingVO.setIatPort(rs.getString("iat_port"));
                iatNetworkServiceMappingVO.setIatName(rs.getString("iat_name"));
                iatNetworkServiceMappingVO.setLanguage1Message(rs.getString("lang1_message"));
                iatNetworkServiceMappingVO.setLanguage2Message(rs.getString("lang2_message"));
                iatNetworkServiceMappingVO.setRecCountryShortName(rs.getString("rec_country_short_name"));
                iatNetworkServiceMappingVO.setRecNetworkCode(rs.getString("rec_nw_code"));
                iatNetworkServiceMappingVO.setServiceStatus(rs.getString("service_status"));
                iatNetworkServiceMappingVO.setServiceType(rs.getString("service_type"));
                iatNetworkServiceMappingVO.setHandlerClass(rs.getString("handler_class"));
                iatNetworkServiceMappingVO.setUnderProcessMsgReq(rs.getString("underprocess_msg_reqd"));
                iatNetworkServiceMappingVO.setInterfaceTypeID(rs.getString("typeid"));
                map.put(iatNetworkServiceMappingVO.getRecCountryShortName() + "_" + iatNetworkServiceMappingVO.getRecNetworkCode() + "_" + iatNetworkServiceMappingVO.getServiceType(), iatNetworkServiceMappingVO);

            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATNetworkServiceMap]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATNetworkServiceMap]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
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
                _log.debug(methodName, "Exiting: networkMap size=" + map.size());
            }
        }
        return map;
    }

    /**
     * Load the Iat network service cache key ip port
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap loadIATNetworkServiceMapWithIP() throws BTSLBaseException {

        final String methodName = "loadIATNetworkServiceMapWithIP";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap mapIPPort = new HashMap();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" select	insm.rec_country_short_name, insm.rec_nw_code, ");
        strBuff.append(" i.interface_id,i.external_id,i.interface_description,i.interface_type_id,i.status,i.clouser_date,");
        strBuff.append(" i.message_language1,i.message_language2,i.concurrent_connection,i.single_state_transaction,i.status_type,");
        strBuff.append("i.status,i.val_expiry_time,i.topup_expiry_time,it.interface_type_id, it.interface_name,it.interface_category,it.handler_class,it.underprocess_msg_reqd,");
        strBuff.append(" insm.iat_name ,insm.iat_code ,insm.service_type,insm.service_status,insm.lang1_message ,insm.lang2_message,insm.iat_ip, insm.iat_port");
        strBuff.append(" FROM  interfaces i,interface_types it,iat_nw_service_mapping insm");
        strBuff.append(" WHERE it.interface_type_id=i.interface_type_id AND insm.iat_code=i.interface_id and insm.service_status=? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.IAT_SERVICE_STATUS_ACTIVE);
            rs = pstmt.executeQuery();
            IATNetworkServiceMappingVO iatNetworkServiceMappingVO = null;
            while (rs.next()) {
                iatNetworkServiceMappingVO = new IATNetworkServiceMappingVO();

                iatNetworkServiceMappingVO.setIatCode(rs.getString("iat_code"));
                iatNetworkServiceMappingVO.setIatip(rs.getString("iat_ip"));
                iatNetworkServiceMappingVO.setIatPort(rs.getString("iat_port"));
                iatNetworkServiceMappingVO.setIatName(rs.getString("iat_name"));
                iatNetworkServiceMappingVO.setLanguage1Message(rs.getString("lang1_message"));
                iatNetworkServiceMappingVO.setLanguage2Message(rs.getString("lang2_message"));
                iatNetworkServiceMappingVO.setRecCountryShortName(rs.getString("rec_country_short_name"));
                iatNetworkServiceMappingVO.setRecNetworkCode(rs.getString("rec_nw_code"));
                iatNetworkServiceMappingVO.setServiceStatus(rs.getString("service_status"));
                iatNetworkServiceMappingVO.setServiceType(rs.getString("service_type"));
                iatNetworkServiceMappingVO.setHandlerClass(rs.getString("handler_class"));
                mapIPPort.put(iatNetworkServiceMappingVO.getIatip() + "_" + iatNetworkServiceMappingVO.getIatPort(), iatNetworkServiceMappingVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATNetworkServiceMapWithIP]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATNetworkServiceMapWithIP]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
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
                _log.debug(methodName, "Exiting: networkMap size=" + mapIPPort.size());
            }
        }
        return mapIPPort;
    }

    /**
     * Load the Iat network service cache key interfaceid
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap loadIATNetworkServiceMapWithIATID() throws BTSLBaseException {
        final String methodName = "loadIATNetworkServiceMapWithIATID";
        if (_log.isDebugEnabled()) {

            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap mapIATID = new HashMap();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" select	insm.rec_country_short_name, insm.rec_nw_code, ");
        strBuff.append(" i.interface_id,i.external_id,i.interface_description,i.interface_type_id,i.status,i.clouser_date,");
        strBuff.append(" i.message_language1,i.message_language2,i.concurrent_connection,i.single_state_transaction,i.status_type,");
        strBuff.append("i.status,i.val_expiry_time,i.topup_expiry_time,it.interface_type_id, it.interface_name,it.interface_category,it.handler_class,it.underprocess_msg_reqd,");
        strBuff.append(" insm.iat_name ,insm.iat_code ,insm.service_type,insm.service_status,insm.lang1_message ,insm.lang2_message,insm.iat_ip, insm.iat_port");
        strBuff.append(" FROM  interfaces i,interface_types it,iat_nw_service_mapping insm");
        strBuff.append(" WHERE it.interface_type_id=i.interface_type_id AND insm.iat_code=i.interface_id AND insm.service_status=?");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.IAT_SERVICE_STATUS_ACTIVE);
            rs = pstmt.executeQuery();
            IATNetworkServiceMappingVO iatNetworkServiceMappingVO = null;
            while (rs.next()) {
                iatNetworkServiceMappingVO = new IATNetworkServiceMappingVO();

                iatNetworkServiceMappingVO.setIatCode(rs.getString("iat_code"));
                iatNetworkServiceMappingVO.setIatip(rs.getString("iat_ip"));
                iatNetworkServiceMappingVO.setIatPort(rs.getString("iat_port"));
                iatNetworkServiceMappingVO.setIatName(rs.getString("iat_name"));
                iatNetworkServiceMappingVO.setLanguage1Message(rs.getString("lang1_message"));
                iatNetworkServiceMappingVO.setLanguage2Message(rs.getString("lang2_message"));
                iatNetworkServiceMappingVO.setRecCountryShortName(rs.getString("rec_country_short_name"));
                iatNetworkServiceMappingVO.setRecNetworkCode(rs.getString("rec_nw_code"));
                iatNetworkServiceMappingVO.setServiceStatus(rs.getString("service_status"));
                iatNetworkServiceMappingVO.setServiceType(rs.getString("service_type"));
                iatNetworkServiceMappingVO.setHandlerClass(rs.getString("handler_class"));
                mapIATID.put(iatNetworkServiceMappingVO.getIatCode(), iatNetworkServiceMappingVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATNetworkServiceMapWithIP]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATNetworkServiceMapWithIP]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
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
                _log.debug("loadIATNetworkServiceMapWithIP()", "Exiting: networkMap size=" + mapIATID.size());
            }
        }
        return mapIATID;
    }

    /**
     * Method to load the TransferVO based on transfer ID
     * 
     * @param p_con
     * @param p_transferID
     * @throws BTSLBaseException
     * @return
     */
    public IATTransferItemVO loadIATTransferVO(Connection p_con, String p_transferID) throws BTSLBaseException {
        final String methodName = "loadIATTransferVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  p_transferID " + p_transferID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        IATTransferItemVO iatTransferItemVO = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT iat.transfer_id, iat.rec_country_code, iat.rec_nw_code, iat.rec_msisdn, ");
            selectQueryBuff.append(" iat.notify_msisdn, iat.failed_at, iat.exchange_rate, iat.prov_ratio, iat.rec_bonus,");
            selectQueryBuff.append("iat.iat_timestamp, iat.credit_resp_code, iat.credit_msg, iat.chk_status_resp_code, iat.iat_error_code,  ");
            selectQueryBuff.append("iat.iat_message, iat.rec_nw_error_code, iat.rec_nw_message, iat.fees, iat.rcvd_amt, iat.iat_txn_id, iat.sent_amt, ");
            selectQueryBuff.append("iat.sender_id, iat.service_type, iat.transfer_status, iat.sent_amt_iattorec ");
            selectQueryBuff.append("FROM c2s_iat_transfer_items iat ");
            selectQueryBuff.append("WHERE transfer_id=?   ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, p_transferID);

            rs = pstmtSelect.executeQuery();
            iatTransferItemVO = new IATTransferItemVO();
            if (rs.next()) {
                iatTransferItemVO.setIatSenderTxnId(rs.getString("transfer_id"));
                iatTransferItemVO.setIatRcvrCountryCode(rs.getInt("rec_country_code"));
                iatTransferItemVO.setIatRecNWCode(rs.getString("rec_nw_code"));
                iatTransferItemVO.setIatRecMsisdn(rs.getString("rec_msisdn"));
                iatTransferItemVO.setIatNotifyMsisdn(rs.getString("notify_msisdn"));
                iatTransferItemVO.setIatFailedAt(rs.getString("failed_at"));
                iatTransferItemVO.setIatExchangeRate(rs.getDouble("exchange_rate"));
                iatTransferItemVO.setIatProvRatio(rs.getDouble("prov_ratio"));
                iatTransferItemVO.setIatReceiverSystemBonus(rs.getDouble("rec_bonus"));

                iatTransferItemVO.setIatCreditMessage(rs.getString("credit_resp_code"));
                iatTransferItemVO.setIatCreditRespCode(rs.getString("credit_msg"));
                iatTransferItemVO.setIatErrorCode(rs.getString("chk_status_resp_code"));
                iatTransferItemVO.setIatErrorMessage(rs.getString("iat_error_code"));
                iatTransferItemVO.setIatCheckStatusRespCode(rs.getString("iat_message"));
                iatTransferItemVO.setIatRcvrNWErrorCode(rs.getString("rec_nw_error_code"));
                iatTransferItemVO.setIatRcvrNWErrorMessage(rs.getString("rec_nw_message"));
                iatTransferItemVO.setIatFees(rs.getDouble("fees"));
                iatTransferItemVO.setIatTxnId(rs.getString("iat_txn_id"));
                iatTransferItemVO.setSenderId(rs.getString("sender_id"));
                iatTransferItemVO.setServiceType(rs.getString("service_type"));
                iatTransferItemVO.setTransferStatus(rs.getString("transfer_status"));
                iatTransferItemVO.setIatSentAmtByIAT(rs.getDouble("sent_amt_iattorec"));
                iatTransferItemVO.setIatRcvrRcvdAmt(rs.getDouble("rcvd_amt"));
                iatTransferItemVO.setIatReceivedAmount(rs.getDouble("sent_amt"));
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATTransferVO]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadC2STransferVOList()", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDAO[loadIATTransferVO]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadC2STransferVOList()", "error.general.sql.processing");
        }// end of catch
        finally {
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
                _log.debug(methodName, "Exiting iatTransferItemVO= " + iatTransferItemVO.toString());
            }
        }// end of finally

        return iatTransferItemVO;
    }

    /**
     * Method updateIATTransferItemForAbgTxns.
     * This method update the Iat transfer item vo table after getting the
     * response from IAT hub
     * 
     * @param p_con
     *            Connection
     * @param iatInterfaceVO
     *            IATInterfaceVO (having all the values from IAT hub)
     * @param p_txnStatus
     *            final txn status on IAT hub.
     * @return int
     * @throws BTSLBaseException
     */
    public int updateIATTransferItemForAbgTxns(Connection p_con, IATInterfaceVO iatInterfaceVO, String p_txnStatus) throws BTSLBaseException {
        final String methodName = "updateIATTransferItemForAbgTxns";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_txnStatus=" + p_txnStatus + " Iat txn id :" + iatInterfaceVO.getIatTRXID() + " PreTUPS txn id =" + iatInterfaceVO.getIatSenderNWTRXID());
        }
        PreparedStatement pstmtUpdate = null;
        String updateQuery = null;
        int updateCount = 0;
        try {
            if (PretupsI.TXN_STATUS_SUCCESS.equals(p_txnStatus)) {
                updateQuery = "UPDATE c2s_iat_transfer_items SET  exchange_rate=?, prov_ratio=?, rec_bonus=?, iat_error_code=?, iat_message=?, rec_nw_error_code=?, rec_nw_message=?, fees=?, rcvd_amt=?, iat_txn_id=?, sent_amt=?, transfer_status=?, chk_status_resp_code=? , sent_amt_iattorec=? where transfer_id=? ";
            } else {
                updateQuery = "UPDATE c2s_iat_transfer_items SET  iat_error_code=?, iat_message=?, rec_nw_error_code=?, rec_nw_message=?,transfer_status=? ,chk_status_resp_code=? where transfer_id=? ";
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Update query:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            int i = 1;
            if (PretupsI.TXN_STATUS_SUCCESS.equals(p_txnStatus)) {
                pstmtUpdate.setDouble(i++, iatInterfaceVO.getIatExchangeRate());
                pstmtUpdate.setDouble(i++, iatInterfaceVO.getIatProvRatio());
                pstmtUpdate.setDouble(i++, iatInterfaceVO.getIatReceiverZebraBonus());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatReasonCode());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatReasonMessage());
                pstmtUpdate.setString(i++, iatInterfaceVO.getReceiverNWReasonCode());
                pstmtUpdate.setString(i++, iatInterfaceVO.getReceiverNWReasonMessage());
                pstmtUpdate.setDouble(i++, iatInterfaceVO.getIatFees());
                pstmtUpdate.setDouble(i++, iatInterfaceVO.getIatRcvrRcvdAmount());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatTRXID());
                pstmtUpdate.setDouble(i++, iatInterfaceVO.getIatReceivedAmount());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatINTransactionStatus());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatResponseCodeChkStatus());
                pstmtUpdate.setDouble(i++, iatInterfaceVO.getIatSentAmtByIAT());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatSenderNWTRXID());

            } else {
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatReasonCode());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatReasonMessage());
                pstmtUpdate.setString(i++, iatInterfaceVO.getReceiverNWReasonCode());
                pstmtUpdate.setString(i++, iatInterfaceVO.getReceiverNWReasonMessage());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatINTransactionStatus());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatResponseCodeChkStatus());
                pstmtUpdate.setString(i++, iatInterfaceVO.getIatSenderNWTRXID());

            }
            updateCount = pstmtUpdate.executeUpdate();

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateIATTransferItemForAbgTxns]", "Entered with p_txnStatus=" + p_txnStatus + " Iat txn id :" + iatInterfaceVO.getIatTRXID() + " PreTUPS txn id =" + iatInterfaceVO.getIatSenderNWTRXID(), "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[updateIATTransferItemForAbgTxns]", "Entered with p_txnStatus=" + p_txnStatus + " Iat txn id :" + iatInterfaceVO.getIatTRXID() + " PreTUPS txn id =" + iatInterfaceVO.getIatSenderNWTRXID(), "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting for Zebra id=" + iatInterfaceVO.getIatSenderNWTRXID() + " updateCount:" + updateCount);
            }

        }// end of finally
        return updateCount;
    }
}
