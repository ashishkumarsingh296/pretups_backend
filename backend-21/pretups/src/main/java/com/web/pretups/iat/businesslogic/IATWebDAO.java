package com.web.pretups.iat.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.iat.businesslogic.IATCountryMasterVO;
import com.btsl.pretups.iat.businesslogic.IATNetworkCountryMappingVO;
import com.btsl.pretups.iat.businesslogic.IATNetworkServiceMappingVO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.util.BTSLUtil;

public class IATWebDAO {

    private Log log = LogFactory.getLog(IATWebDAO.class.getName());

    /**
     * @param p_con
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadIATCountryMasterList(Connection p_con) throws BTSLBaseException {

        final String methodName = "loadIATCountryMasterList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final ArrayList arr = new ArrayList();

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT icm.rec_country_code,icm.rec_country_short_name,icm.rec_country_name,icm.currency, ");
        strBuff.append(" icm.prefix_length,icm.min_msisdn_length ,icm.max_msisdn_length ,icm.country_status,icm.language1_msg,icm.language2_msg,icm.country_serial_id ");
        strBuff.append(" FROM iat_country_master icm where icm.country_status<>?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("QUERY sqlSelect : " + sqlSelect);
            log.debug(methodName, msg.toString());
        }

        try {

            pstmt = p_con.prepareStatement(sqlSelect);
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
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATCountryMasterList]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATCountryMasterList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: networkMap size : " + arr.size());
                log.debug(methodName, msg.toString());
            }
        }
        return arr;
    }

    public ArrayList loadIATNetworkCountryList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadIATNetworkCountryList";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final ArrayList networkCountryList = new ArrayList();

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT incm.rec_country_short_name, incm.rec_nw_code ,incm.rec_nw_name ,incm.rec_nw_prefixes,incm.status  ");
        strBuff.append(" FROM iat_nw_country_mapping incm where incm.status <> ?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("QUERY sqlSelect : " + sqlSelect);
            log.debug(methodName, msg.toString());
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
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
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATNetworkCountryMap]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATNetworkCountryMap]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: networkMap size : " + networkCountryList.size());
                log.debug(methodName, msg.toString());
            }
        }
        return networkCountryList;
    }

    /**
     * method is used to add the receiver country for IAT
     * 
     * @return int
     * @throws BTSLBaseException
     * @author chetan.kothari
     */
    public int addIATReceiverCountry(IATCountryMasterVO p_countryMasterVo, Connection p_con) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        int index = 1;
        final String methodName = "addIATReceiverCountry";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: IATCountryMasterVO : " + p_countryMasterVo.toString());
            log.debug(methodName, msg.toString());
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("insert into IAT_COUNTRY_MASTER(COUNTRY_SERIAL_ID,REC_COUNTRY_CODE,REC_COUNTRY_NAME,");
            strBuff.append("REC_COUNTRY_SHORT_NAME,CURRENCY,PREFIX_LENGTH,MIN_MSISDN_LENGTH,");
            strBuff.append("MAX_MSISDN_LENGTH,COUNTRY_STATUS,LANGUAGE1_MSG,LANGUAGE2_MSG) values");
            strBuff.append("(?,?,?,?,?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(index++, p_countryMasterVo.getCountrySerialID());
            psmtInsert.setInt(index++, p_countryMasterVo.getRecCountryCode());
            psmtInsert.setString(index++, p_countryMasterVo.getRecCountryName().toUpperCase());
            psmtInsert.setString(index++, p_countryMasterVo.getRecCountryShortName().toUpperCase());
            psmtInsert.setString(index++, p_countryMasterVo.getCurrency());
            psmtInsert.setInt(index++, p_countryMasterVo.getPrefixLength());
            psmtInsert.setInt(index++, p_countryMasterVo.getMinMsisdnLength());
            psmtInsert.setInt(index++, p_countryMasterVo.getMaxMsisdnLength());
            psmtInsert.setString(index++, p_countryMasterVo.getCountryStatus());
            psmtInsert.setString(index++, p_countryMasterVo.getLanguage1Message());
            psmtInsert.setString(index++, p_countryMasterVo.getLanguage2Message());
            insertCount = psmtInsert.executeUpdate();

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATNetworkServiceMapWithIP]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATNetworkServiceMapWithIP]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: insertCount : " + insertCount);
                log.debug(methodName, msg.toString());
            }
        }
        return insertCount;
    }

    /**
     * Check if iat code already exist or not.
     * 
     * @return boolean
     * @param String
     * @throws BTSLBaseException
     * @author chetan.kothari
     */
    public boolean isIATCodeExist(Connection p_con, String p_iatCode) throws BTSLBaseException {

        final String methodName = "isIATCodeExist";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered IATCode : " + p_iatCode);
            log.debug(methodName, msg.toString());
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isCodeExist = false;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT REC_COUNTRY_CODE from IAT_COUNTRY_MASTER where REC_COUNTRY_CODE=? and COUNTRY_STATUS<>?");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("QUERY sqlSelect : " + sqlSelect);
            log.debug(methodName, msg.toString());
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setLong(1, Long.parseLong(p_iatCode));
            pstmt.setString(2, PretupsI.IAT_COUNTRY_STATUS_DELETED);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                isCodeExist = true;
            } else {
                isCodeExist = false;
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATCountryMasterList]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATCountryMasterList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: isCodeExist : " + isCodeExist);
                log.debug(methodName, msg.toString());
            }
        }
        return isCodeExist;
    }

    /**
     * Method to modify the IAT receiver country based on country code
     * 
     * @param p_con
     * @param p_transferID
     * @return
     */

    public int updateIATReceiverCountry(IATCountryMasterVO p_countryMasterVo, Connection p_con) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int updateCount = 0;
        int index = 1;
        final String methodName = "updateIATReceiverCountry";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: IATCountryMasterVO : " + p_countryMasterVo.toString());
            log.debug(methodName, msg.toString());
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("update IAT_COUNTRY_MASTER ");
            strBuff.append("set CURRENCY=? , PREFIX_LENGTH= ?,MIN_MSISDN_LENGTH =? , MAX_MSISDN_LENGTH = ?,");
            strBuff.append("COUNTRY_STATUS=? , LANGUAGE1_MSG= ?,LANGUAGE2_MSG =? ");
            strBuff.append("WHERE COUNTRY_SERIAL_ID = ?");
            final String insertQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlUpdate:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(index++, p_countryMasterVo.getCurrency());
            psmtInsert.setInt(index++, p_countryMasterVo.getPrefixLength());
            psmtInsert.setInt(index++, p_countryMasterVo.getMinMsisdnLength());
            psmtInsert.setInt(index++, p_countryMasterVo.getMaxMsisdnLength());
            psmtInsert.setString(index++, p_countryMasterVo.getCountryStatus());
            psmtInsert.setString(index++, p_countryMasterVo.getLanguage1Message());
            psmtInsert.setString(index++, p_countryMasterVo.getLanguage2Message());
            psmtInsert.setString(index++, p_countryMasterVo.getCountrySerialID());
            updateCount = psmtInsert.executeUpdate();

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[updateIATReceiverCountry]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[updateIATReceiverCountry]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: insertCount : " + updateCount);
                log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * Check if iat short name already exist or not.
     * 
     * @param p_con
     *            TODO
     * @param String
     * @return boolean
     * @throws BTSLBaseException
     * @author chetan.kothari
     */
    public boolean isIATShortNameExist(Connection p_con, String p_iatShortName) throws BTSLBaseException {

        final String methodName = "isIATShortNameExist";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered IATShortName : " + p_iatShortName);
            log.debug(methodName, msg.toString());
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isShortNameExist = false;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT REC_COUNTRY_CODE from IAT_COUNTRY_MASTER where REC_COUNTRY_SHORT_NAME=? and COUNTRY_STATUS<>?");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_iatShortName);
            pstmt.setString(2, PretupsI.IAT_COUNTRY_STATUS_DELETED);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                isShortNameExist = true;
            } else {
                isShortNameExist = false;
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[isIATShortNameExist]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[isIATShortNameExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: isShortNameExist : " + isShortNameExist);
                log.debug(methodName, msg.toString());
            }
        }
        return isShortNameExist;
    }

    /**
     * Method to delete the IAT receiver country based on country code
     * 
     * @param p_con
     * @param p_transferID
     * @return
     */

    public int deleteIATReceiverCountry(IATCountryMasterVO p_countryMasterVo, Connection p_con) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        PreparedStatement psmtSelect = null;
        ResultSet rs = null;
        int updateCount = 0;
        int index = 1;
        final String methodName = "deleteIATReceiverCountry";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: IATCountryMasterVO : " + p_countryMasterVo.toString());
            log.debug(methodName, msg.toString());
        }
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT * from IAT_COUNTRY_MASTER icm,IAT_NW_COUNTRY_MAPPING incm ");
            strBuff.append("where icm.REC_COUNTRY_SHORT_NAME=incm.REC_COUNTRY_SHORT_NAME and ");

            if (!BTSLUtil.isNullString(String.valueOf(p_countryMasterVo.getRecCountryCode())) && p_countryMasterVo.getRecCountryCode() > 0) {
                strBuff.append(" icm.REC_COUNTRY_CODE=?");
            } else {
                strBuff.append(" icm.REC_COUNTRY_NAME=?");
            }
            strBuff.append(" and icm.COUNTRY_STATUS<>?");
            strBuff.append(" and incm.STATUS<>?");
            final String selectQuery = strBuff.toString();
            psmtSelect = p_con.prepareStatement(selectQuery);
            if (!BTSLUtil.isNullString(String.valueOf(p_countryMasterVo.getRecCountryCode())) && p_countryMasterVo.getRecCountryCode() > 0) {
                psmtSelect.setInt(1, p_countryMasterVo.getRecCountryCode());
            } else {
                psmtSelect.setString(1, p_countryMasterVo.getRecCountryName());
            }
            psmtSelect.setString(2, PretupsI.IAT_COUNTRY_STATUS_DELETED);
            psmtSelect.setString(3, PretupsI.IAT_NETWORK_STATUS_DELETED);
            rs = psmtSelect.executeQuery();
            if (rs.next()) {
                throw new BTSLBaseException("iat.iatcountrymanagement.error.referenceexist");
            }
            p_countryMasterVo = getCountryByCodeOrName(p_con, p_countryMasterVo);
            strBuff = new StringBuilder();
            strBuff.append("update  IAT_COUNTRY_MASTER set COUNTRY_STATUS=? where");
            if (!BTSLUtil.isNullString(String.valueOf(p_countryMasterVo.getRecCountryCode())) && p_countryMasterVo.getRecCountryCode() > 0) {
                strBuff.append(" REC_COUNTRY_CODE=?");
            } else {
                strBuff.append(" REC_COUNTRY_NAME=?");
            }
            strBuff.append(" and COUNTRY_SERIAL_ID=?");
            final String insertQuery = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlDelete:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(index++, PretupsI.IAT_COUNTRY_STATUS_DELETED);
            if (!BTSLUtil.isNullString(String.valueOf(p_countryMasterVo.getRecCountryCode())) && p_countryMasterVo.getRecCountryCode() > 0) {
                psmtInsert.setInt(index++, p_countryMasterVo.getRecCountryCode());
            } else {
                psmtInsert.setString(index++, p_countryMasterVo.getRecCountryName());
            }
            psmtInsert.setString(index++, p_countryMasterVo.getCountrySerialID());
            updateCount = psmtInsert.executeUpdate();

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[deleteIATReceiverCountry]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException : " + be.getMessage());
            throw be;
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[deleteIATReceiverCountry]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtSelect != null) {
                    psmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: deleteCount : " + updateCount);
                log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * Load the Iat country master data by name or code
     * 
     * @param p_con
     *            TODO
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public IATCountryMasterVO getCountryByCodeOrName(Connection p_con, IATCountryMasterVO p_iatVO) throws BTSLBaseException {

        final String methodName = "getCountryByCodeOrName()";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered IATVO : " + p_iatVO);
            log.debug(methodName, msg.toString());
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT * from IAT_COUNTRY_MASTER where  ");
        if (!BTSLUtil.isNullString(String.valueOf(p_iatVO.getRecCountryCode())) && p_iatVO.getRecCountryCode() > 0) {
            strBuff.append(" REC_COUNTRY_CODE=?");
        } else {
            strBuff.append(" REC_COUNTRY_NAME=?");
        }
        strBuff.append(" and COUNTRY_STATUS<>?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            if (!BTSLUtil.isNullString(String.valueOf(p_iatVO.getRecCountryCode())) && p_iatVO.getRecCountryCode() > 0) {
                pstmt.setInt(1, p_iatVO.getRecCountryCode());
            } else {
                pstmt.setString(1, p_iatVO.getRecCountryName().toUpperCase());
            }
            pstmt.setString(2, PretupsI.IAT_COUNTRY_STATUS_DELETED);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                p_iatVO.setRecCountryCode(rs.getInt("REC_COUNTRY_CODE"));
                p_iatVO.setRecCountryName(rs.getString("REC_COUNTRY_NAME"));
                p_iatVO.setRecCountryShortName(rs.getString("REC_COUNTRY_SHORT_NAME"));
                p_iatVO.setCountryStatus(rs.getString("COUNTRY_STATUS"));
                p_iatVO.setCurrency(rs.getString("CURRENCY"));
                p_iatVO.setLanguage1Message(rs.getString("LANGUAGE1_MSG"));
                p_iatVO.setLanguage2Message(rs.getString("LANGUAGE2_MSG"));
                p_iatVO.setPrefixLength(rs.getInt("PREFIX_LENGTH"));
                p_iatVO.setMinMsisdnLength(rs.getInt("MIN_MSISDN_LENGTH"));
                p_iatVO.setMaxMsisdnLength(rs.getInt("MAX_MSISDN_LENGTH"));
                p_iatVO.setCountrySerialID(rs.getString("COUNTRY_SERIAL_ID"));
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATCountryMasterList]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATCountryMasterList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: IATVO : " + p_iatVO);
                log.debug(methodName, msg.toString());
            }
        }
        return p_iatVO;
    }

    /**
     * Load the Iat country master cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public ArrayList getCountryListByCountryName(String p_CountryName, Connection p_con) throws BTSLBaseException {

        final String methodName = "getCountryListByCountryName";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered :ReceiverCountryName : " + p_CountryName);
            log.debug(methodName, msg.toString());
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final ArrayList countryList = new ArrayList();

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT icm.rec_country_code,icm.rec_country_short_name,icm.rec_country_name,icm.currency, ");
        strBuff.append(" icm.prefix_length,icm.min_msisdn_length ,icm.max_msisdn_length ,icm.country_status,icm.language1_msg,icm.language2_msg ");
        strBuff.append(" FROM iat_country_master icm where  icm.rec_country_name like ? and icm.country_status <> ?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_CountryName);
            pstmt.setString(2, PretupsI.IAT_COUNTRY_STATUS_DELETED);
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
                countryList.add(countryMasterVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[getCountryListByCountryName]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[getCountryListByCountryName]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: countryList size : " + countryList.size());
                log.debug(methodName, msg.toString());
            }
        }
        return countryList;
    }

    /**
     * This method will load all IAT interfaces details .
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Chetan Kothari
     */

    public ArrayList loadIATInterfaceDetailsList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadIATInterfaceDetailsList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        StringBuilder strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        InterfaceVO interfaceVO = null;
        final ArrayList loadInterfaceDetailsList = new ArrayList();
        try {
            strBuff = new StringBuilder("SELECT interface_id,external_id,");
            strBuff.append("interface_description,interface_type_id,status,");
            strBuff.append("clouser_date,message_language1,message_language2,");
            strBuff.append("concurrent_connection,single_state_transaction,");
            strBuff
                .append("created_on,created_by,modified_on,modified_by,status_type,val_expiry_time,topup_expiry_time FROM interfaces WHERE status <> 'N' and interface_type_id=?");

            final String sqlLoad = strBuff.toString();
            pstmtSelect = p_con.prepareStatement(sqlLoad);
            pstmtSelect.setString(1, PretupsI.IAT_TRANSACTION_TYPE);
            rs = pstmtSelect.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY Executed= " + sqlLoad);
            }

            while (rs.next()) {
                interfaceVO = new InterfaceVO();
                interfaceVO.setInterfaceId(rs.getString("interface_id"));
                interfaceVO.setExternalId(rs.getString("external_id"));
                interfaceVO.setInterfaceDescription(rs.getString("interface_description"));
                interfaceVO.setInterfaceTypeId(rs.getString("interface_type_id"));
                interfaceVO.setStatusCode(rs.getString("status"));
                interfaceVO.setClosureDate(rs.getDate("clouser_date"));
                interfaceVO.setLanguage1Message(rs.getString("message_language1"));
                interfaceVO.setLanguage2Message(rs.getString("message_language2"));
                interfaceVO.setConcurrentConnection(rs.getInt("concurrent_connection"));
                interfaceVO.setSingleStateTransaction(rs.getString("single_state_transaction"));
                interfaceVO.setCreatedOn(rs.getDate("created_on"));
                interfaceVO.setCreatedBy(rs.getString("created_by"));
                interfaceVO.setModifiedOn(rs.getDate("modified_on"));
                interfaceVO.setModifiedBy(rs.getString("modified_by"));
                interfaceVO.setStatusType(rs.getString("status_type"));
                interfaceVO.setValExpiryTime(rs.getLong("val_expiry_time"));
                interfaceVO.setTopUpExpiryTime(rs.getLong("topup_expiry_time"));
                loadInterfaceDetailsList.add(interfaceVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, " SQL Exception " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadIATInterfaceDetailsList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("IatNetworkCountryMapping", methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceDAO[loadIATInterfaceDetailsList]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("IatNetworkCountryMapping", methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: No. of Interfaces : " + loadInterfaceDetailsList.size());
                log.debug(methodName, msg.toString());
            }
        }
        return loadInterfaceDetailsList;
    }

    /**
     * method is used to add IAT and network country mapping
     * 
     * @return int
     * @throws BTSLBaseException
     * @author chetan.kothari
     */
    public int addIATNetworkCountryMapping(Connection p_con, IATNetworkCountryMappingVO p_iatNWCountryVO) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        int index = 1;
        final String methodName = "addIATNetworkCountryMapping";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_iatNWCountryVOList size : " + p_iatNWCountryVO.toString());
            log.debug(methodName, msg.toString());
        }
        String insertQuery = null;
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("insert into IAT_NW_COUNTRY_MAPPING(");
            strBuff.append("REC_COUNTRY_SHORT_NAME,REC_NW_CODE,REC_NW_NAME,");
            strBuff.append("REC_NW_PREFIXES,STATUS,SERIAL_ID,NW_COUNTRY_ID) values");
            strBuff.append("(?,?,?,?,?,?,?)");
            insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);

            psmtInsert.setString(index++, p_iatNWCountryVO.getRecCountryShortName());
            psmtInsert.setString(index++, p_iatNWCountryVO.getRecNetworkCode());
            psmtInsert.setString(index++, p_iatNWCountryVO.getRecNetworkName());
            psmtInsert.setString(index++, p_iatNWCountryVO.getRecNetworkPrefix());
            psmtInsert.setString(index++, p_iatNWCountryVO.getStatus());
            psmtInsert.setString(index++, p_iatNWCountryVO.getSerialID());
            psmtInsert.setString(index++, p_iatNWCountryVO.getNetworkCountryID());
            insertCount = insertCount + psmtInsert.executeUpdate();
            psmtInsert.clearParameters();

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[addIATNetworkCountryMapping]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[addIATNetworkCountryMapping]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: insertCount : " + insertCount);
                log.debug(methodName, msg.toString());
            }
        }
        return insertCount;
    }

    /**
     * method is used to add IAT and network service mapping
     * 
     * @return int
     * @throws BTSLBaseException
     * @author chetan.kothari
     */
    public int addIATNetworkServiceMapping(Connection p_con, ArrayList p_iatNWServiceVOList) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        int index = 1;
        final String methodName = "addIATNetworkServiceMapping";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_iatNWCountryVOList size : " + p_iatNWServiceVOList.size());
            log.debug(methodName, msg.toString());
        }
        try {
            final Iterator itr = p_iatNWServiceVOList.iterator();
            IATNetworkServiceMappingVO iatNWServiceVO = null;
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("insert into IAT_NW_SERVICE_MAPPING(");
            strBuff.append("SERIAL_NUMBER,REC_COUNTRY_SHORT_NAME,REC_NW_CODE,IAT_NAME,IAT_CODE,IAT_IP,IAT_PORT,");
            strBuff.append("SERVICE_TYPE,SERVICE_STATUS,LANG1_MESSAGE,LANG2_MESSAGE,NW_MAP_SRL_ID) values");
            strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = null;
            insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
            while (itr.hasNext()) {
                iatNWServiceVO = (IATNetworkServiceMappingVO) itr.next();
                index = 1;
                psmtInsert = p_con.prepareStatement(insertQuery);
                psmtInsert.setString(index++, iatNWServiceVO.getSerialNumber());
                psmtInsert.setString(index++, iatNWServiceVO.getRecCountryShortName());
                psmtInsert.setString(index++, iatNWServiceVO.getRecNetworkCode());
                psmtInsert.setString(index++, iatNWServiceVO.getIatName());
                psmtInsert.setString(index++, iatNWServiceVO.getIatCode());
                psmtInsert.setString(index++, iatNWServiceVO.getIatip());
                psmtInsert.setString(index++, iatNWServiceVO.getIatPort());
                psmtInsert.setString(index++, iatNWServiceVO.getServiceType());
                psmtInsert.setString(index++, iatNWServiceVO.getServiceStatus());
                psmtInsert.setString(index++, iatNWServiceVO.getLanguage1Message());
                psmtInsert.setString(index++, iatNWServiceVO.getLanguage2Message());
                psmtInsert.setString(index++, iatNWServiceVO.getNetworkMapSrID());
                insertCount = insertCount + psmtInsert.executeUpdate();
                psmtInsert.clearParameters();

            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[addIATNetworkServiceMapping]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[addIATNetworkServiceMapping]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: insertCount : " + insertCount);
                log.debug(methodName, msg.toString());
            }
        }
        return insertCount;
    }

    /**
     * Get the IAT network mapping vo by country and network code.
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public IATNetworkCountryMappingVO getMappingByCountryAndNetwork(Connection p_con, String p_country, String p_network, boolean nwCode) throws BTSLBaseException {
        final String methodName = "getMappingByCountryAndNetwork";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_country : " + p_country);
        	msg.append(", p_network : " + p_network);
            log.debug(methodName, msg.toString());
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        IATNetworkCountryMappingVO iatVO = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT * from IAT_NW_COUNTRY_MAPPING where  ");
        strBuff.append(" REC_COUNTRY_SHORT_NAME=? and STATUS<> ? ");
        if (nwCode) {
            strBuff.append(" and REC_NW_CODE=?");
        } else {
            strBuff.append(" and REC_NW_NAME=?");
        }
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_country);
            pstmt.setString(2, PretupsI.IAT_NETWORK_STATUS_DELETED);
            pstmt.setString(3, p_network);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                iatVO =  IATNetworkCountryMappingVO.getInstance();
                iatVO.setRecNetworkCode(rs.getString("REC_NW_CODE"));
                iatVO.setRecNetworkName(rs.getString("REC_NW_NAME"));
                iatVO.setRecCountryShortName(rs.getString("REC_COUNTRY_SHORT_NAME"));
                iatVO.setRecNetworkPrefix(rs.getString("REC_NW_PREFIXES"));
                iatVO.setStatus(rs.getString("STATUS"));
                iatVO.setSerialID(rs.getString("SERIAL_ID"));
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[getCountryByCodeOrName]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[getCountryByCodeOrName]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: IATNEtworkCountryVO : " + iatVO);
                log.debug(methodName, msg.toString());
            }
        }
        return iatVO;
    }

    /**
     * method is used to update IAT and network country mapping
     * 
     * @return int
     * @throws BTSLBaseException
     * @author chetan.kothari
     */
    public int updateIATNetworkCountryMapping(Connection p_con, IATNetworkCountryMappingVO p_iatNWCountryVO) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int updateCount = 0;
        int index = 1;
        final String methodName = "updateIATNetworkCountryMapping";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_iatNWCountryVOList size : " + p_iatNWCountryVO.toString());
            log.debug(methodName, msg.toString());
        }
        String insertQuery = null;
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("update IAT_NW_COUNTRY_MAPPING set REC_NW_PREFIXES=?, STATUS=? ");
            strBuff.append("where SERIAL_ID= ?");
            insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("updateIATNetworkCountryMapping", "Query sqlUpdate:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(index++, p_iatNWCountryVO.getRecNetworkPrefix());
            psmtInsert.setString(index++, p_iatNWCountryVO.getStatus());
            psmtInsert.setString(index++, p_iatNWCountryVO.getSerialID());
            updateCount = updateCount + psmtInsert.executeUpdate();
            psmtInsert.clearParameters();

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[updateIATNetworkCountryMapping]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[updateIATNetworkCountryMapping]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: updateCount : " + updateCount);
                log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * Load the Iat network service cache key ip port
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public ArrayList loadIATNetworkServiceListByNwMapID(Connection p_con, String p_serialId) throws BTSLBaseException {
        final String methodName = "loadIATNetworkServiceListByNwMapID";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final ArrayList mappingList = new ArrayList();

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("select * from IAT_NW_SERVICE_MAPPING where ");
        strBuff.append("NW_MAP_SRL_ID=? ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_serialId);
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
                mappingList.add(iatNetworkServiceMappingVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATWebDAO[loadIATNetworkServiceListByCountryAndNetwork]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error("loadIATNetworkServiceListByCountryAndNetwork()", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATWebDAO[loadIATNetworkServiceListByCountryAndNetwork]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: networkMap size : " + mappingList.size());
                log.debug(methodName, msg.toString());
            }
        }
        return mappingList;
    }

    /**
     * method is used delete IAT network and service mapping
     * 
     * @return int
     * @throws BTSLBaseException
     * @author chetan.kothari
     */
    public int deleteIATNetworkServiceMapping(Connection p_con, IATNetworkCountryMappingVO p_iatNWCountryVO) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        int index = 1;
        final String methodName = "deleteIATNetworkServiceMapping";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_iatNWCountryVO : " + p_iatNWCountryVO.toString());
            log.debug(methodName, msg.toString());
        }
        String insertQuery = null;
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("delete from IAT_NW_SERVICE_MAPPING where ");
            strBuff.append("REC_COUNTRY_SHORT_NAME=? and REC_NW_CODE=? and NW_MAP_SRL_ID=?");
            insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("p_iatNWCountryVO", "Query sqlDelete:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(index++, p_iatNWCountryVO.getRecCountryShortName());
            psmtInsert.setString(index++, p_iatNWCountryVO.getRecNetworkCode());
            psmtInsert.setString(index++, p_iatNWCountryVO.getSerialID());
            insertCount = psmtInsert.executeUpdate();
            psmtInsert.clearParameters();

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[deleteIATNetworkServiceMapping]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[deleteIATNetworkServiceMapping]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: deleteCount : " + insertCount);
                log.debug(methodName, msg.toString());
            }
        }
        return insertCount;
    }

    /**
     * Load the Iat country master cache
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadIATCountryList(Connection con) throws BTSLBaseException {

        final String methodName = "loadIATCountryList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final ArrayList arr = new ArrayList();

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT icm.rec_country_code,icm.rec_country_short_name,icm.rec_country_name,icm.currency, ");
        strBuff.append(" icm.prefix_length,icm.min_msisdn_length ,icm.max_msisdn_length ,icm.country_status,icm.language1_msg,icm.language2_msg, ");
        strBuff.append(" icm.COUNTRY_SERIAL_ID FROM iat_country_master icm where icm.country_status IN ('Y','S') ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = con.prepareStatement(sqlSelect);
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
                countryMasterVO.setCountrySerialID(rs.getString("COUNTRY_SERIAL_ID"));
                arr.add(countryMasterVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATCountryList]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[loadIATCountryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: networkMap size : " + arr.size());
                log.debug(methodName, msg.toString());
            }
        }
        return arr;
    }

    /**
     * Method for loading Services List for IAT.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @param p_type
     *            TODO
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesListIAT(Connection p_con, String p_networkCode, String p_module, String p_type) throws BTSLBaseException {
        final String methodName = "loadServicesListIAT";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_networkCode : " + p_networkCode);
        	msg.append(", p_module : " + p_module);
        	msg.append(", p_type : " + p_type);
            log.debug(methodName, msg.toString());
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT st.service_type, st.NAME FROM service_type st,network_services ns ");
        strBuff.append(" WHERE st.external_interface = 'Y' AND st.type= ? ");
        strBuff.append(" AND st.status = 'Y' AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmt.setString(i++, p_type);
            pstmt.setString(i++, p_networkCode);
            pstmt.setString(i++, p_networkCode);
            pstmt.setString(i++, p_module);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListIAT]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadServicesListIAT]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: serviceList size : " + list.size());
                log.debug(methodName, msg.toString());
            }
        }
        return list;
    }

    /**
     * Check if iat short name already exist or not.
     * 
     * @param p_con
     *            TODO
     * @param String
     * @return boolean
     * @throws BTSLBaseException
     * @author chetan.kothari
     */
    public boolean isIATCountryNameExist(Connection p_con, String p_countrtyName) throws BTSLBaseException {

        final String methodName = "isIATCountryNameExist";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered IATShortName : " + p_countrtyName);
            log.debug(methodName, msg.toString());
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isShortNameExist = false;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT REC_COUNTRY_CODE from IAT_COUNTRY_MASTER where REC_COUNTRY_NAME=? and COUNTRY_STATUS<>?");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_countrtyName);
            pstmt.setString(2, PretupsI.IAT_COUNTRY_STATUS_DELETED);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                isShortNameExist = true;
            } else {
                isShortNameExist = false;
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[isIATCountryNameExist]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATWebDAO[isIATCountryNameExist]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: isShortNameExist : " + isShortNameExist);
                log.debug(methodName, msg.toString());
            }
        }
        return isShortNameExist;
    }

}
