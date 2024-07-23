package com.btsl.pretups.master.businesslogic;

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

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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

/**
 * 
 * class LocaleMasterDAO
 *
 */
public class LocaleMasterDAO {
    private static Log log = LogFactory.getLog(LocaleMasterDAO.class.getName());
    private static final String SQL_EXCEPTION = "SQL Exception : ";
    private static final String EXCEPTION = "Exception :";
    public Connection con;
/**
 * 
 * @return 
 * @return
 * @throws SQLException
 * @throws Exception
 */
    public void localeMaster(Connection con)
       {
            this.con=con;
       }
    
    public HashMap<String,Locale> loadLocaleMasterCache() throws SQLException, Exception {
        final String methodName = "loadLocaleMasterCache";
        LogFactory.printLog(methodName, "Entered**", log);
       
         
        HashMap<String,Locale> localeMasterMap = new HashMap<String,Locale>();
        Connection con = null;
        
        try {
            con = OracleUtil.getSingleConnection();
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT language, country, name, language_code ");
            selectQueryBuff.append(" FROM locale_master where status <> 'N' ORDER BY language_code ");

            String selectQuery = selectQueryBuff.toString();
            LogFactory.printLog(methodName, "select query :" + selectQuery, log);
            
            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                localeMasterMap.put(rs.getString("language_code"), new Locale(rs.getString("language"), rs.getString("country")));
            }// end while
            return localeMasterMap;
        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException1 " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterCache]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception1" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterCache]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
        	OracleUtil.closeQuietly(con);
        	LogFactory.printLog(methodName, "Exiting localeMasterMap.size:" + localeMasterMap.size(), log);
            
        }// end of finally
    }
/**
 * 
 * @return
 * @throws SQLException
 * @throws Exception
 */
    public ArrayList loadLocaleMasterDetails() throws SQLException, Exception {
        final String methodName = "loadLocaleMasterDetails";
        LogFactory.printLog(methodName, "Entered", log);
       
         
        ArrayList localeList = new ArrayList();
        ListValueVO listValueVO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        
         
        try {
        	//added for JUNIT
        	try{
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
               }
            catch (NullPointerException e) {
        	con=this.con;
        	if(BTSLUtil.isNullObject(con))
        	{
        		throw new BTSLBaseException("LocaleMasterDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        	}
		    }
            // ChangeID=LOCALEMASTER
            // Query is changed so that only that language are loaded that have
            // status not N and applicable for SMS or BOTH type
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT language, country, name, language_code ");
            selectQueryBuff.append(" FROM locale_master where status <> 'N' AND (type=? OR type=?) ORDER BY language_code ");

            String selectQuery = selectQueryBuff.toString();
            LogFactory.printLog(methodName, "select query2:" + selectQuery, log);
           
            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, PretupsI.SMS_LOCALE);
            pstmtSelect.setString(2, PretupsI.BOTH_LOCALE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("language_code"));
                listValueVO.setType(rs.getString("language"));
                listValueVO.setOtherInfo(rs.getString("country"));

                localeList.add(listValueVO);
            }// end while
            return localeList;
        }
            }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException2 " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception2" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
			if (mcomCon != null) {
				mcomCon.close("LocaleMasterDAO#loadLocaleMasterDetails");
				mcomCon = null;
			}
           LogFactory.printLog(methodName, "Exiting localeList.size:" + localeList.size(), log);
            
        }// end of finally
    }
/**
 * 
 * @param conn
 * @param plaguage
 * @param pcountryCode
 * @return
 * @throws SQLException
 * @throws Exception
 */
    public String loadLocaleMasterCode(Connection conn, String plaguage, String pcountryCode) throws SQLException, Exception {
        final String methodName = "loadLocaleMasterCode";
        LogFactory.printLog(methodName, "Entered ", log);
      
         
        try {
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT language, country, name, language_code ");
            selectQueryBuff.append(" FROM locale_master where language=? AND country=? ");
            String selectQuery = selectQueryBuff.toString();
            LogFactory.printLog(methodName, "select query:" + selectQuery, log);
            
            try(PreparedStatement pstmtSelect = conn.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, plaguage);
            pstmtSelect.setString(2, pcountryCode);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                return rs.getString("language_code");
            }
            return null;
        }
            }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException3 " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterCode]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception3 " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
        	LogFactory.printLog(methodName, "Exiting", log);
           
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
    public HashMap<Locale,LocaleMasterVO> loadLocaleDetailsAtStartUp() throws BTSLBaseException, Exception {
        final String methodName = "loadLocaleDetailsAtStartUp";
        LogFactory.printLog(methodName, "Entered ", log);
     
        
        HashMap<Locale,LocaleMasterVO> localeMasterMap = new LinkedHashMap<Locale,LocaleMasterVO>();
        Connection con = null;
        
        try {
            con = OracleUtil.getSingleConnection();
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT language, country, name, language_code, ");
            selectQueryBuff.append(" charset, encoding, status, type, message, coding, sequence_no ");
            selectQueryBuff.append(" FROM locale_master where status<>'N' ORDER BY language_code ");

            String selectQuery = selectQueryBuff.toString();
            LogFactory.printLog(methodName, "select query:" + selectQuery, log);
           
            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
            {
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
        }
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException4 " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleDetailsAtStartUp]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception4 " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleDetailsAtStartUp]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
        	OracleUtil.closeQuietly(con);
            LogFactory.printLog(methodName, "Exiting localeMasterMap.size:" + localeMasterMap.size(), log);
          
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
         
        ArrayList localeList = new ArrayList();
        ListValueVO listValueVO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
       
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            // ChangeID=LOCALEMASTER
            // Query is changed so that only that language are loaded that have
            // status not N and applicable for SMS or BOTH type
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT language, country, name, language_code ");
            selectQueryBuff.append(" FROM locale_master where status <> 'N' AND (type=? OR type=?) ORDER BY language_code ");

            String selectQuery = selectQueryBuff.toString();

            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, PretupsI.SMS_LOCALE);
            pstmtSelect.setString(2, PretupsI.BOTH_LOCALE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            String value = null;
            while (rs.next()) {
                value = rs.getString("language") + "_" + rs.getString("country");
                listValueVO = new ListValueVO(rs.getString("name"), value);
                listValueVO.setOtherInfo(rs.getString("language_code"));
                localeList.add(listValueVO);
            }// end while
            return localeList;
        }
            }
        }// end of try
        catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterData]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {

            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
        	
			if (mcomCon != null) {
				mcomCon.close("LocaleMasterDAO#loadLocaleMasterData");
				mcomCon = null;
			}
         

        }// end of finally
    }
    
    /**
     * Validate language code in Database
     * @param p_con
     * @param languageCode
     * @return
     * @throws BTSLBaseException
     */
    public boolean validateLanguageCode(Connection p_con, String languageCode) throws BTSLBaseException {
        final String methodName = "validateLanguageCode";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: languageCode=");
        	loggerValue.append(languageCode);
        	log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isValid = false;

        if (BTSLUtil.isNullString(languageCode)) {
        	return isValid;
        }
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT 1 FROM LOCALE_MASTER WHERE STATUS <> 'N' AND LANGUAGE_CODE = ? ");
        final String sqlSelect = strBuff.toString();
        if(log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			log.debug(methodName, loggerValue);
		}
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setInt(1, Integer.parseInt(languageCode));
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	isValid = true;
            }
            return isValid;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isExternalCodeExist]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[isExternalCodeExist]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                log.errorTrace(methodName, e);
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                log.errorTrace(methodName, e);
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if(log.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: isValid:");
            	loggerValue.append(isValid);
            	log.debug(methodName, loggerValue);
            }
        }
    }
    
    /**
     * 
     * @param conn
     * @param plaguage
     * @param pcountryCode
     * @return
     * @throws SQLException
     * @throws Exception
     */
        public String loadLocaleMasterName(Connection conn, String plaguage, String pcountryCode) throws SQLException, Exception {
            final String methodName = "loadLocaleMasterName";
            LogFactory.printLog(methodName, "Entered ", log);
          
             
            try {
                StringBuilder selectQueryBuff = new StringBuilder(" SELECT language, country, name, language_code ");
                selectQueryBuff.append(" FROM locale_master where language=? AND country=? ");
                String selectQuery = selectQueryBuff.toString();
                LogFactory.printLog(methodName, "select query:" + selectQuery, log);
                
                try(PreparedStatement pstmtSelect = conn.prepareStatement(selectQuery);)
                {
                pstmtSelect.setString(1, plaguage);
                pstmtSelect.setString(2, pcountryCode);
                try(ResultSet rs = pstmtSelect.executeQuery();)
                {
                if (rs.next()) {
                    return rs.getString("name");
                }
                return null;
            }
                }
            }// end of try
            catch (SQLException sqle) {
                log.error(methodName, "SQLException3 " + sqle.getMessage());
                log.errorTrace(methodName, sqle);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterName]", "", "", "", "SQL Exception:" + sqle.getMessage());
                throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }// end of catch
            catch (Exception e) {
                log.error(methodName, "Exception3 " + e.getMessage());
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterDAO[loadLocaleMasterName]", "", "", "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("LocaleMasterDAO",methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }// end of catch
            finally {
            	
            	LogFactory.printLog(methodName, "Exiting", log);
               
            }// end of finally
        }

}
