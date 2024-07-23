package com.selftopup.pretups.master.businesslogic;

/*
 * @(#)LocaleMasterDAO.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Gurjeet Singh Nov 04, 2005 Initital Creation
 * Ankit Jindal Nov 16, 2006 Modified for ChangeID=LOCALEMASTER
 * ------------------------------------------------------------------------------
 * -------------------
 * DAO class for locale
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.OracleUtil;

public class LocaleMasterDAO {
    private static Log _log = LogFactory.getLog(LocaleMasterDAO.class);

    public HashMap loadLocaleMasterCache() throws SQLException, Exception {
        final String methodName = "loadLocaleMasterCache";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        PreparedStatement pstmtSelect = null;
        HashMap localeMasterMap = new HashMap();
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT language, country, name, language_code ");
            selectQueryBuff.append(" FROM locale_master where status <> 'N' ORDER BY language_code ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                localeMasterMap.put(rs.getString("language_code"), new Locale(rs.getString("language"), rs.getString("country")));
            }// end while
            return localeMasterMap;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterCache]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", methodName, SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterCache]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", methodName, SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting localeMasterMap.size:" + localeMasterMap.size());
        }// end of finally
    }

    public ArrayList loadLocaleMasterDetails() throws SQLException, Exception {
        final String methodName = "loadLocaleMasterDetails";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        PreparedStatement pstmtSelect = null;
        ArrayList localeList = new ArrayList();
        ListValueVO listValueVO = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getConnection();
            // ChangeID=LOCALEMASTER
            // Query is changed so that only that language are loaded that have
            // status not N and applicable for SMS or BOTH type
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT language, country, name, language_code ");
            selectQueryBuff.append(" FROM locale_master where status <> 'N' AND (type=? OR type=?) ORDER BY language_code ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.SMS_LOCALE);
            pstmtSelect.setString(2, PretupsI.BOTH_LOCALE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("language_code"));
                listValueVO.setType(rs.getString("language"));
                listValueVO.setOtherInfo(rs.getString("country"));

                localeList.add(listValueVO);
            }// end while
            return localeList;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", methodName, SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", methodName, SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting localeList.size:" + localeList.size());
        }// end of finally
    }

    public String loadLocaleMasterCode(Connection p_con, String p_laguage, String p_countryCode) throws SQLException, Exception {
        final String methodName = "loadLocaleMasterCode";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT language, country, name, language_code ");
            selectQueryBuff.append(" FROM locale_master where language=? AND country=? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_laguage);
            pstmtSelect.setString(2, p_countryCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                return rs.getString("language_code");
            return null;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterCode]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", methodName, SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", methodName, SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting");
        }// end of finally
    }

    /**
     * Method:loadLocaleDetailsAtStartUp
     * This method is used to load the details of locale at startup
     * ChangeID=LOCALEMASTER
     * 
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public HashMap loadLocaleDetailsAtStartUp() throws BTSLBaseException, Exception {
        final String methodName = "loadLocaleDetailsAtStartUp";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        PreparedStatement pstmtSelect = null;
        HashMap localeMasterMap = new LinkedHashMap();
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT language, country, name, language_code, ");
            selectQueryBuff.append(" charset, encoding, status, type, message, coding, sequence_no ");
            selectQueryBuff.append(" FROM locale_master where status<>'N' ORDER BY language_code ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            LocaleMasterVO localeVO = null;
            while (rs.next()) {
                localeVO = new LocaleMasterVO();
                localeVO.setName(rs.getString("name"));
                localeVO.setCoding(rs.getString("coding"));
                localeVO.setCharset(rs.getString("charset"));
                localeVO.setEncoding(rs.getString("encoding"));
                localeVO.setLanguage_code(rs.getString("language_code"));
                localeVO.setType(rs.getString("type"));
                localeVO.setMessage(rs.getString("message"));
                localeVO.setLanguage(rs.getString("language"));
                localeVO.setCountry(rs.getString("country"));
                localeVO.setStatus(rs.getString("status"));
                localeVO.setSequenceNo(rs.getInt("sequence_no"));
                localeMasterMap.put(new Locale(rs.getString("language"), rs.getString("country")), localeVO);
            }// end while
            return localeMasterMap;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleDetailsAtStartUp]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", methodName, SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleDetailsAtStartUp]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", methodName, SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting localeMasterMap.size:" + localeMasterMap.size());
        }// end of finally
    }

    /**
     * Method:loadLocaleMasterData
     * This method is used to load the details of locale
     * Added by Deepika aggarwal
     * 
     * @return ArrayList
     * @throws SQLException
     * @throws Exception
     */
    public static ArrayList loadLocaleMasterData() throws SQLException, Exception {
        final String methodName = "loadLocaleMasterData";
        PreparedStatement pstmtSelect = null;
        ArrayList localeList = new ArrayList();
        ListValueVO listValueVO = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getConnection();
            // ChangeID=LOCALEMASTER
            // Query is changed so that only that language are loaded that have
            // status not N and applicable for SMS or BOTH type
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT language, country, name, language_code ");
            selectQueryBuff.append(" FROM locale_master where status <> 'N' AND (type=? OR type=?) ORDER BY language_code ");

            String selectQuery = selectQueryBuff.toString();

            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.SMS_LOCALE);
            pstmtSelect.setString(2, PretupsI.BOTH_LOCALE);
            rs = pstmtSelect.executeQuery();
            String value = null;
            while (rs.next()) {
                value = rs.getString("language") + "_" + rs.getString("country");
                listValueVO = new ListValueVO(rs.getString("name"), value);
                listValueVO.setOtherInfo(rs.getString("language_code"));
                localeList.add(listValueVO);
            }// end while
            return localeList;
        }// end of try
        catch (SQLException sqle) {

            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterData]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", "loadLocaleMasterData", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {

            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", "loadLocaleMasterData", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

        }// end of finally
    }

}
