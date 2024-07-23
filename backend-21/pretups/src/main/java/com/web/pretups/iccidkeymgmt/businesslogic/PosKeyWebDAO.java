package com.web.pretups.iccidkeymgmt.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.logging.IccFileProcessLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class PosKeyWebDAO {

    /**
     * Field _logger.
     */
    private static Log _logger = LogFactory.getLog(PosKeyWebDAO.class.getName());
    private static final String EXCEPTION = "Exception: ";
    private static final String SQL_EXCEPTION = "SQL Exception: ";
    private static PosKeyWebQry posKeyWebQry;
    public static final String classname = "PosKeyWebDAO";

    /**
     * PosKeyDAO constructor comment.
     */
    public PosKeyWebDAO() {
        super();
        posKeyWebQry = (PosKeyWebQry) ObjectProducer.getObject(QueryConstants.POS_KEY_WEB_QRY,QueryConstants.QUERY_PRODUCER);
    }

    /**
     * This method maps the ICC ID with the MSISDN
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_iccId
     *            String
     * @param p_modifiedBy
     *            String
     * @param p_forcefully
     *            boolean
     * @return PosKeyVO
     * @throws BTSLBaseException
     */
    public PosKeyVO mapIccToMsisdn(Connection p_con, String p_msisdn, String p_locationCode, String p_iccId, String p_modifiedBy, boolean p_forcefully) throws BTSLBaseException {

        final String METHOD_NAME = "mapIccToMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (_logger.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(",p_iccId=");
        	loggerValue.append(p_iccId);
        	loggerValue.append(",p_forcefully=");
        	loggerValue.append(p_forcefully);
        	loggerValue.append(",p_locationCode=");
        	loggerValue.append(p_locationCode);
        	_logger.debug(METHOD_NAME, loggerValue);
        }
        PosKeyVO poskeyVOReturn = null;
        PosKeyVO poskeyVO = null;
        final Date currentDate = new Date(System.currentTimeMillis());
        int lenIccStored = 0;
        PreparedStatement insertPstmt = null;
        PreparedStatement updatePstmt = null;
        PreparedStatement updatePrevPstmt = null;
        // Query for Inserting
        final String updateNewSql = "UPDATE pos_keys SET msisdn=?,modified_on=?, modified_by=? where ICC_id=? ";
        final String updateSql = "UPDATE pos_keys SET msisdn=?,modified_on=?, modified_by=? where ICC_id=? ";
        final String updatePrev = "UPDATE pos_keys SET registered='N',modified_on=?, modified_by=? , msisdn=?, new_icc_id=? WHERE msisdn=? ";

        boolean IccAdded = false;
        boolean IccUpdated = false;
        int iccAdded = 0;
        int updatePrevRec = 0;
        int updateMsisdn = 0;
        final String[] errorArray = new String[2];
        IccAdded = false;
        IccUpdated = false;
        int assMsisdn = 0;
        try {
            poskeyVO = isMsisdnExist(p_con, p_msisdn);

            if (poskeyVO == null) {
                assMsisdn = isIccExist(p_con, p_iccId, p_locationCode);
                if (assMsisdn == 0) {
                    try {
                        updatePstmt = p_con.prepareStatement(updateSql);
                        updatePstmt.setString(1, p_msisdn);
                        updatePstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                        updatePstmt.setString(3, p_modifiedBy);
                        updatePstmt.setString(4, p_iccId);
                        if(_logger.isDebugEnabled()){
                			loggerValue.setLength(0);
                			loggerValue.append("Query =");
                			loggerValue.append(updateSql);
                			_logger.debug(METHOD_NAME, loggerValue);
                		}
                        updateMsisdn = updatePstmt.executeUpdate();

                        if (updateMsisdn > 0) {
                            IccUpdated = true;
                        }

                        if (!IccUpdated) {
                            errorArray[0] = p_msisdn;
                            throw new BTSLBaseException(this, "writeFileToDatabase", "iccidkeymgmt.assmsisdniccid.error.msisdnnotupdated", errorArray);
                        }
                        // Clear Paremeters
                        updatePstmt.clearParameters();
                    } catch (SQLException sqe) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("INVALID RECORD For Icc Id ");
                    	loggerValue.append(p_iccId);
            			loggerValue.append(SQL_EXCEPTION);
            			loggerValue.append(sqe.getMessage());
            			_logger.error(METHOD_NAME, loggerValue);
                        _logger.errorTrace(METHOD_NAME, sqe);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[mapIccToMsisdn]", "", "", "",
                            loggerValue.toString());
                        throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                    } catch (Exception e) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("INVALID RECORD For Icc Id ");
                    	loggerValue.append(p_iccId);
            			loggerValue.append(EXCEPTION);
            			loggerValue.append(e.getMessage());
            			_logger.error(METHOD_NAME, loggerValue);
                        _logger.errorTrace(METHOD_NAME, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[mapIccToMsisdn]", "", "", "",
                            loggerValue.toString());
                        throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                    }
                } else if (assMsisdn == 1) {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.assmsisdniccid.error.iccassmsisdnexist");
                } else if (assMsisdn == 7) {
                    // error no icc id found
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.msg.iccidnotsupportnetwork");
                } else {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.assmsisdniccid.error.norecordfound");
                }
            } else {
                if (!p_forcefully) {
                    poskeyVOReturn = poskeyVO;
                } else {
                    final String IccStored = poskeyVO.getIccId();
                    lenIccStored = (IccStored.trim()).length();
                    if(_logger.isDebugEnabled()){
            			loggerValue.setLength(0);
            			loggerValue.append("IccStored =");
            			loggerValue.append(IccStored);
            			loggerValue.append(" lenIccStored =");
            			loggerValue.append(lenIccStored);
            			_logger.debug(METHOD_NAME, loggerValue);
            		}
                    if(_logger.isDebugEnabled()){
            			loggerValue.setLength(0);
            			loggerValue.append("ICC Id before checking =");
            			loggerValue.append(IccStored);
            			_logger.debug(METHOD_NAME, loggerValue);
            		}
                    if ((IccStored.trim()).equalsIgnoreCase(p_iccId.trim())) {
                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.assmsisdniccid.error.alreadyexist", errorArray);
                    }
                    assMsisdn = isIccExist(p_con, p_iccId, p_locationCode);
                    if (assMsisdn == 0) {
                        try {
                        	if(_logger.isDebugEnabled()){
                    			loggerValue.setLength(0);
                    			loggerValue.append("updatePrevPstmt =");
                    			loggerValue.append(updatePrevPstmt);
                    			_logger.debug(METHOD_NAME, loggerValue);
                    		}
                            updatePrevPstmt = p_con.prepareStatement(updatePrev);
                            updatePrevPstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            updatePrevPstmt.setString(2, p_modifiedBy);
                            updatePrevPstmt.setNull(3, Types.VARCHAR);
                            updatePrevPstmt.setString(4, p_iccId);
                            updatePrevPstmt.setString(5, p_msisdn);
                            updatePrevRec = updatePrevPstmt.executeUpdate();
                            if (updatePrevRec <= 0) {
                                errorArray[0] = p_msisdn;
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.assmsisdniccid.error.prevrecnotupdated", errorArray);
                            }
                            // Clear Paremeters
                            updatePrevPstmt.clearParameters();
                        } catch (SQLException sqe) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("INVALID RECORD For Icc Id ");
                        	loggerValue.append(p_iccId);
                			loggerValue.append(SQL_EXCEPTION);
                			loggerValue.append(sqe.getMessage());
                			_logger.error(METHOD_NAME, loggerValue);
                            _logger.errorTrace(METHOD_NAME, sqe);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[mapIccToMsisdn]", "", "",
                                "", loggerValue.toString());
                            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                        } catch (Exception e) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("INVALID RECORD For Icc Id ");
                        	loggerValue.append(p_iccId);
                			loggerValue.append(EXCEPTION);
                			loggerValue.append(e.getMessage());
                			_logger.error(METHOD_NAME, loggerValue);
                            _logger.errorTrace(METHOD_NAME, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[mapIccToMsisdn]", "", "",
                                "", loggerValue.toString());
                            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                        }
                        try {
                            insertPstmt = p_con.prepareStatement(updateNewSql);
                            insertPstmt.setString(1, p_msisdn);
                            insertPstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                            insertPstmt.setString(3, p_modifiedBy);
                            insertPstmt.setString(4, p_iccId);
                            if(_logger.isDebugEnabled()){
                    			loggerValue.setLength(0);
                    			loggerValue.append("updateNewSql =");
                    			loggerValue.append(updateNewSql);
                    			_logger.debug(METHOD_NAME, loggerValue);
                    		}
                            iccAdded = insertPstmt.executeUpdate();

                            if (iccAdded > 0) {
                                IccAdded = true;
                            }

                            if (!IccAdded) {
                                errorArray[0] = p_msisdn;
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.assmsisdniccid.error.notadded", errorArray);
                            }

                            // Clear Paremeters
                            insertPstmt.clearParameters();
                        } catch (SQLException sqe) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("INVALID RECORD For Icc Id ");
                        	loggerValue.append(p_iccId);
                			loggerValue.append(SQL_EXCEPTION);
                			loggerValue.append(sqe.getMessage());
                			_logger.error(METHOD_NAME, loggerValue);
                            _logger.errorTrace(METHOD_NAME, sqe);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[mapIccToMsisdn]", "", "",
                                "", loggerValue.toString());
                            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                        } catch (Exception e) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("INVALID RECORD For Icc Id ");
                        	loggerValue.append(p_iccId);
                			loggerValue.append(EXCEPTION);
                			loggerValue.append(e.getMessage());
                			_logger.error(METHOD_NAME, loggerValue);
                            _logger.errorTrace(METHOD_NAME, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[mapIccToMsisdn]", "", "",
                                "", loggerValue.toString());
                            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                        }
                    }// end of if
                    else if (assMsisdn == 1) {
                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.assmsisdniccid.error.iccassmsisdnexist");
                    } else if (assMsisdn == 7) {
                        // error no icc id found
                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.msg.iccidnotsupportnetwork");
                    } else {
                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.assmsisdniccid.error.norecordfound");
                    }
                }
            }// end of else
            if (IccAdded || IccUpdated) {
                poskeyVOReturn = new PosKeyVO();
                poskeyVOReturn.setSuccessullyUpdated(true);
            }
            return poskeyVOReturn;
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[mapIccToMsisdn]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            // Destroying different objects
            try {
                if (updatePrevPstmt != null) {
                    updatePrevPstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (updatePstmt != null) {
                    updatePstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Exiting");
            }
        }// end of finally

    }

    /**
     * Insert the method's description here.
     * Creation date: (8/14/03 5:46:42 PM)
     * 
     * @param p_con
     *            Connection
     * @param p_Msisdn
     *            String
     * @return PosKeyVO
     * @throws BTSLBaseException
     */
    // private static PosKeyVO isMsisdnExist(Connection p_con,String
    // p_Msisdn)throws BTSLBaseException
    private PosKeyVO isMsisdnExist(Connection p_con, String p_Msisdn) throws BTSLBaseException {
        final String METHOD_NAME = "isMsisdnExist";
        StringBuilder loggerValue= new StringBuilder();
        if (_logger.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_Msisdn=");
        	loggerValue.append(p_Msisdn);
        	_logger.debug(METHOD_NAME, loggerValue);
        }
        PosKeyVO poskeyVO = null;
        final String checkSql = "SELECT icc_id,decrypt_key, created_by  FROM pos_keys WHERE msisdn=? ";
        try (PreparedStatement checkPstmt = p_con.prepareStatement(checkSql);){
            
            checkPstmt.setString(1, p_Msisdn);
           try(ResultSet rs = checkPstmt.executeQuery();)
           {
            if (rs.next()) {
                poskeyVO = new PosKeyVO();
                poskeyVO.setKey(rs.getString("decrypt_key"));
                poskeyVO.setCreatedBy(rs.getString("created_by"));
                poskeyVO.setIccId(rs.getString("icc_id"));
            }
        }
        }// end of try
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            poskeyVO = null;
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[isMsisdnExist]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[isMsisdnExist]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
           
            if (_logger.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: poskeyVO:");
            	loggerValue.append(poskeyVO);
            	_logger.debug(METHOD_NAME, loggerValue);
            }
        }// end of finally
        return poskeyVO;
    }// end of isMsisdnExist

    /**
     * Check if ICC ID exist or not
     * Creation date: (8/14/03 5:46:42 PM)
     * If Icc Id exist but not associated with someone
     * 1: if IccId is associated with MSISDN
     * 2: if IccId does not exist
     * 3: Exception
     * 
     * @param p_con
     *            Connection
     * @param p_IccId
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    // public static int isIccExist(Connection p_con,String p_IccId)throws
    // BTSLBaseException
    public int isIccExist(Connection p_con, String p_IccId, String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "isIccExist";
        StringBuilder loggerValue= new StringBuilder();
        if (_logger.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_IccId=");
        	loggerValue.append(p_IccId);
        	_logger.debug(METHOD_NAME, loggerValue);
        }
        // boolean flag = false;
        int ret = 0;
        String retMsg = "";
        final String checkSql=posKeyWebQry.isIccExistQry();
        try(PreparedStatement checkPstmt = p_con.prepareStatement(checkSql);) {
            
            checkPstmt.setString(1, p_IccId);
            try(ResultSet rs = checkPstmt.executeQuery();)
            {
            if (rs.next()) {
                retMsg = rs.getString("msisdn");
                if (BTSLUtil.isNullString(retMsg)) {
                    ret = 0;
                } else {
                    ret = 1;
                }
                if (!p_networkCode.equals(rs.getString("network_code"))) {
                    ret = 7;
                }
            } else {
                ret = 2;
            }
        }
        }// end of try
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            ret = 3;
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[isIccExist]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            ret = 3;
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[isIccExist]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            
            if (_logger.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: ret:");
            	loggerValue.append(ret);
            	_logger.debug(METHOD_NAME, loggerValue);
            }
        }// end of finally
         // Moved Return from outside the finally block on 28/05/04
        return ret;
    }// end of isIccExist

    /**
     * This method will insert the ICC ID KEY details in the POS KEY table
     * 
     * @return java.lang.String
     * @param p_con
     *            java.sql.Connection
     * @param p_fileName
     *            java.lang.String
     * @param p_createdBy
     *            java.lang.String
     * @param p_locationCode
     *            String
     * @param p_simProfileList
     *            ArrayList
     * @param p_file
     *            String
     * @exception BTSLBaseException
     *                modified by sandeep goel
     * */

    public int writeFileToDatabase(Connection p_con, String p_filename, String p_createdBy, String p_locationCode, ArrayList p_simProfileList, String p_file) throws BTSLBaseException {
        final String METHOD_NAME = "writeFileToDatabase";
        StringBuilder loggerValue= new StringBuilder();
        if (_logger.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_filename=");
        	loggerValue.append(p_filename);
        	loggerValue.append(",p_simProfileList.size=");
        	loggerValue.append(p_simProfileList.size());
        	loggerValue.append(",p_locationCode=");
        	loggerValue.append(p_locationCode);
        	_logger.debug(METHOD_NAME, loggerValue);
        }
        boolean processFile = false;
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        long recordsTotal = 0;
        int totRecords = 0;
        String simId = "";
        String delim = Constants.getProperty("Delimiterforuploadiccid");
        final Date currentDate = new Date(System.currentTimeMillis());
        if (BTSLUtil.isNullString(delim)) {
            delim = " ";
        }

        PreparedStatement insertPstmt = null;
        String tempStr = null;
        int iccAdded = 0;
        // Query for Inserting
        final String insertSql = "INSERT INTO pos_keys(icc_id,decrypt_key,sim_profile_id,created_by,created_on,network_code,registered) VALUES(?,?,?,?,?,?,?) ";
        try {
            // checking that registration of SIM will be at first request or it
            // will registered default.
            String registered = Constants.getProperty("STK_REGISTRATION_REQUIRED");
            if (registered == null || registered.equals(PretupsI.YES)) {
                registered = PretupsI.NO;
            } else {
                registered = PretupsI.YES;
            }

            String IccId = "", DecryptKey = "", tempIccId = "";
            String labelName = null;
            String labelValue = null;
            final String[] lineNumberArray = new String[2];
            boolean simExist = false;
            boolean IccAdded = false;
            boolean fileFormat = false;
            boolean fileMoved = false;
            boolean endDataFound = false;
            StringTokenizer startparser = null;
            insertPstmt = p_con.prepareStatement(insertSql);
            try {
                fileReader = new FileReader("" + p_filename);
                bufferReader = new BufferedReader(fileReader);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (bufferReader.ready()) // If File Not Blank Read line by Line
            {
                tempStr = bufferReader.readLine();
                // Read the First line must be Application Version= ????
                if (BTSLUtil.isNullString(tempStr)) {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.applicationvermissing");
                } else {
                    startparser = new StringTokenizer(tempStr, "=");
                    if(_logger.isDebugEnabled()){
            			loggerValue.setLength(0);
            			loggerValue.append("Input =");
            			loggerValue.append(tempStr);
            			loggerValue.append("There are =");
            			loggerValue.append(startparser.countTokens());
            			loggerValue.append(" entries");
            			_logger.debug(METHOD_NAME, loggerValue);
            		}
                    if (startparser.countTokens() < 3) {
                        try {
                            while (startparser.hasMoreTokens()) {
                                labelName = startparser.nextToken().trim();
                                labelValue = startparser.nextToken().trim();
                                // Get this from Appl props
                                if (!Constants.getProperty("master.poskeyupload.label.applversion").equalsIgnoreCase(labelName)) {
                                    // First line must be Application Version
                                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.applicationverfirstline");
                                }
                                if (BTSLUtil.isNullString(labelValue)) {
                                    // First line must be Application Version
                                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.applicationvervaluefirstline");
                                }
                            }
                        } catch (NoSuchElementException e) {
                            lineNumberArray[0] = "1";
                            _logger.errorTrace(METHOD_NAME, e);
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.notgetrecord", lineNumberArray);
                        }
                    } else {
                        lineNumberArray[0] = "1";
                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.impropersyntax", lineNumberArray);
                    }
                }
                startparser = null;
                tempStr = bufferReader.readLine();
                // Read the second line must be Date= ????
                if (BTSLUtil.isNullString(tempStr)) {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.datemissing");
                } else {
                    startparser = new StringTokenizer(tempStr, "=");
                    if(_logger.isDebugEnabled()){
            			loggerValue.setLength(0);
            			loggerValue.append("Input =");
            			loggerValue.append(tempStr);
            			loggerValue.append("There are =");
            			loggerValue.append(startparser.countTokens());
            			loggerValue.append(" entries");
            			_logger.debug(METHOD_NAME, loggerValue);
            		}
                    if (startparser.countTokens() < 3) {
                        try {
                            while (startparser.hasMoreTokens()) {
                                labelName = startparser.nextToken().trim();
                                labelValue = startparser.nextToken().trim();
                                if (!(Constants.getProperty("master.poskeyupload.label.date")).equalsIgnoreCase(labelName)) {
                                    // Second line must be date
                                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.datesecondline");
                                }
                                if (BTSLUtil.isNullString(labelValue)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.datevaluesecondline");
                                }
                            }
                        } catch (NoSuchElementException e) {
                            lineNumberArray[0] = "2";
                            _logger.errorTrace(METHOD_NAME, e);
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.notgetrecord", lineNumberArray);
                        }
                    } else {
                        lineNumberArray[0] = "2";
                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.impropersyntax", lineNumberArray);
                    }
                }
                tempStr = bufferReader.readLine();
                // Read the third line must be SIM PRofile ID= ???? , verify in
                // Database
                if (BTSLUtil.isNullString(tempStr)) {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.simprofilemissing");
                } else {
                    startparser = new StringTokenizer(tempStr, "=");
                    if(_logger.isDebugEnabled()){
            			loggerValue.setLength(0);
            			loggerValue.append("Input =");
            			loggerValue.append(tempStr);
            			loggerValue.append("There are =");
            			loggerValue.append(startparser.countTokens());
            			loggerValue.append(" entries");
            			_logger.debug(METHOD_NAME, loggerValue);
            		}
                    if (startparser.countTokens() < 3) {
                        try {
                            while (startparser.hasMoreTokens()) {
                                labelName = startparser.nextToken().trim();
                                labelValue = startparser.nextToken().trim();
                                if (!Constants.getProperty("master.poskeyupload.label.simprofile").equalsIgnoreCase(labelName)) {
                                    // Third line must be sim id
                                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.simprothirdline");
                                }
                                if (BTSLUtil.isNullString(labelValue)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.simprovaluethirdline");
                                }
                                int simProfileListSize = p_simProfileList.size();
                                for (int i = 0; i < simProfileListSize; i++) {
                                    final ListValueVO listVal = (ListValueVO) p_simProfileList.get(i);
                                    if (BTSLUtil.NullToString(labelValue).equalsIgnoreCase(listVal.getValue())) {
                                        simExist = true;
                                        simId = listVal.getValue();
                                        break;
                                    } else {
                                        continue;
                                    }
                                }
                                if (!simExist) {
                                    lineNumberArray[0] = labelValue;
                                    lineNumberArray[1] = "3";
                                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.simprofile", lineNumberArray);
                                }
                            }
                        } catch (NoSuchElementException e) {
                            lineNumberArray[0] = "3";
                            _logger.errorTrace(METHOD_NAME, e);
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.notgetrecord", lineNumberArray);
                        }
                    } else {
                        lineNumberArray[0] = "3";
                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.impropersyntax", lineNumberArray);
                    }
                }
                tempStr = bufferReader.readLine();
                // Read the fourth line must be START DATA keyword
                if (BTSLUtil.isNullString(tempStr)) {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.startdatamissing");
                } else if (!Constants.getProperty("master.poskeyupload.label.startdata").equalsIgnoreCase(tempStr)) {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.startdatavalue");
                }

                recordsTotal = 4;
                totRecords = 0;
                DecryptKey = "";
                // change 1 - Bug No 1114 start
                while (bufferReader.ready()) {
                    tempStr = bufferReader.readLine();
                    if (tempStr.trim().length() == 0) {
                        continue;
                    }
                    // change 1 - Bug No 1114 end
                    IccAdded = false;
                    recordsTotal++; // Keeps track of line number
                    // If END DATA keyword is found
                    if (Constants.getProperty("master.poskeyupload.label.enddata").equalsIgnoreCase(tempStr.trim())) {
                        endDataFound = true;
                        // Check for line next line after END DATA
                        tempStr = bufferReader.readLine();
                        if (!BTSLUtil.isNullString(tempStr)) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "Recahred here not null");
                            }
                            // Should be last Line
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.enddatalastline");
                        } else {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "Recahred here next line null");
                            }
                            fileFormat = true; // If next line is NULL then file
                            // format is correct
                            IccAdded = true;
                            break; // Break out of the while loop
                        }
                    }
                    try {
                        totRecords++; // Total records count to be inserted in
                        // database
                        final StringTokenizer parser = new StringTokenizer(tempStr, delim);
                        if(_logger.isDebugEnabled()){
                			loggerValue.setLength(0);
                			loggerValue.append("Input =");
                			loggerValue.append(tempStr);
                			loggerValue.append("There are =");
                			loggerValue.append(startparser.countTokens());
                			loggerValue.append(" entries");
                			_logger.debug(METHOD_NAME, loggerValue);
                		}
                        if (parser.countTokens() < 3) {
                            try {
                                while (parser.hasMoreTokens()) {
                                    IccId = parser.nextToken().trim();
                                    DecryptKey = parser.nextToken().trim();
                                }
                            } catch (NoSuchElementException e) {
                                lineNumberArray[0] = "" + recordsTotal;
                                _logger.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.notgetrecord", lineNumberArray);
                            }
                        } else {
                            lineNumberArray[0] = "" + recordsTotal;
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.impropersyntax", lineNumberArray);
                        }
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, " IccId=" + IccId);
                            _logger.debug(METHOD_NAME, " SimId=" + simId);
                        }
                        tempIccId = BTSLUtil.NullToString(IccId).trim();

                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID))).booleanValue()) {
                            if (BTSLUtil.NullToString(IccId).trim().length() > 20) {
                                lineNumberArray[0] = "" + recordsTotal;
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.icclength", lineNumberArray);

                            }
                        } else {
                            if (BTSLUtil.NullToString(IccId).trim().length() > 15) {
                                lineNumberArray[0] = "" + recordsTotal;
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.imsilength", lineNumberArray);

                            }
                        }
                        // Added on 17/08/2004 by Gurjeet so as to allow key of
                        // only 32 char as told by Gaurav
                        if (BTSLUtil.NullToString(DecryptKey).trim().length() != 32) {
                            lineNumberArray[0] = "" + recordsTotal;
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.keylength", lineNumberArray);
                        }
                        IccId = BTSLUtil.calcIccId(IccId, p_locationCode);
                        if(_logger.isDebugEnabled()){
                			loggerValue.setLength(0);
                			loggerValue.append("Final IccId =");
                			loggerValue.append(IccId);
                			_logger.debug(METHOD_NAME, loggerValue);
                		}

                        // Changed By Gurjeet on 06/07/2004 to check if ICC
                        // Exist or NOT
                        final int exist = isNewIccExist(p_con, IccId);
                        if (exist == 0 || exist == 1) {
                            lineNumberArray[0] = tempIccId;
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.alreadyexist", lineNumberArray);
                        } else if (exist == 3) {
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.alreadyexist");
                        }
                        // Set paremeters
                        insertPstmt.setString(1, IccId);
                        insertPstmt.setString(2, DecryptKey);
                        if (BTSLUtil.isNullString(simId)) {
                            insertPstmt.setString(3, simId);
                        } else {
                            insertPstmt.setString(3, simId.toUpperCase());
                        }
                        insertPstmt.setString(4, p_createdBy);
                        insertPstmt.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(currentDate));
                        insertPstmt.setString(6, p_locationCode);
                        insertPstmt.setString(7, registered);

                        // Execute Query
                        iccAdded = insertPstmt.executeUpdate();
                        if (iccAdded > 0) {
                            IccAdded = true;
                        }
                        if (!IccAdded) {
                            lineNumberArray[0] = tempIccId;
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.notadded", lineNumberArray);
                        }
                        // Clear Paremeters
                        insertPstmt.clearParameters();
                    }// end of try
                    catch (BTSLBaseException be) {
                        throw be;
                    } catch (SQLException sqe) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("INVALID RECORD For Icc Id ");
                    	loggerValue.append(tempIccId);
            			loggerValue.append(SQL_EXCEPTION);
            			loggerValue.append(sqe.getMessage());
            			_logger.error(METHOD_NAME, loggerValue);
                        _logger.errorTrace(METHOD_NAME, sqe);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeFileToDatabase]", "", "",
                            "", loggerValue.toString());
                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.notprocessfile");
                    } catch (Exception e) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("INVALID RECORD For Icc Id ");
                    	loggerValue.append(tempIccId);
            			loggerValue.append(EXCEPTION);
            			loggerValue.append(e.getMessage());
            			_logger.error(METHOD_NAME, loggerValue);
                        _logger.errorTrace(METHOD_NAME, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeFileToDatabase]", "", "",
                            "", loggerValue.toString());
                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.notprocessfile");
                    }
                } // end of While
                if(_logger.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Total lines in File ");
        			loggerValue.append(recordsTotal);
        			loggerValue.append("And Total Records for process:");
        			loggerValue.append(totRecords);
        			_logger.debug(METHOD_NAME, loggerValue);
        		}
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.zerofilesize");
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.norecords");
            }
            if (bufferReader != null) {
                bufferReader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }

            if (!fileFormat) {
                if (!endDataFound) {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.enddatamissing");
                } else {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.improperfileformat");
                }
            }
            // Moving File after Processing
            if (IccAdded && fileFormat) {
                fileMoved = moveFileToArchive(p_filename, p_file);
                if (fileMoved) {
                    processFile = true;
                } else {
                    throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.poskeyuploadfile.error.filenomove");
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeFileToDatabase]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeFileToDatabase]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "processed till record no:" + totRecords);
            }
            // Write in LOGS
            if (!processFile) {
                totRecords = 0;
            }
            if(_logger.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("p_createdBy =");
    			loggerValue.append(p_createdBy);
    			loggerValue.append("p_locationCode =");
    			loggerValue.append(p_locationCode);
    			loggerValue.append("Processed =");
    			loggerValue.append(p_file);
    			loggerValue.append("No of records =");
    			loggerValue.append(totRecords);
    			loggerValue.append("Status=");
    			loggerValue.append(processFile);
    			_logger.debug(METHOD_NAME, loggerValue);
    		}
            IccFileProcessLog.log(p_file, p_createdBy, p_locationCode, simId, totRecords, processFile, "");// errstr
            // Destroying different objects
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: iccAdded:");
            	loggerValue.append(iccAdded);
            	_logger.debug(METHOD_NAME, loggerValue);
            }
        }// end of finally
        return iccAdded;
    }

    /**
     * Check if new ICC ID already exist or not
     * Creation date: 06/07/04
     * 
     * @param p_con
     *            Connection
     * @param p_IccId
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    // public static int isNewIccExist(Connection p_con,String p_IccId)throws
    // BTSLBaseException
    public int isNewIccExist(Connection p_con, String p_IccId) throws BTSLBaseException {
        final String METHOD_NAME = "isNewIccExist";
        StringBuilder loggerValue= new StringBuilder();
        if (_logger.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_IccId=");
        	loggerValue.append(p_IccId);
        	_logger.debug(METHOD_NAME, loggerValue);
        }
        int ret = 0;
        PreparedStatement checkPstmt = null;
        ResultSet rs = null;
        final String checkSql = posKeyWebQry.isNewIccExistQry();
        try {
            checkPstmt = p_con.prepareStatement(checkSql);
            checkPstmt.setString(1, p_IccId);
            rs = checkPstmt.executeQuery();
            if (rs.next()) {
                ret = 1;
            } else {
                ret = 2;
            }
        }// end of try
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[isNewIccExist]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[isNewIccExist]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (checkPstmt != null) {
                    checkPstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: ret:");
            	loggerValue.append(ret);
            	_logger.debug(METHOD_NAME, loggerValue);
            }
        }// end of finally
        return ret;
    }// end of isNewIccExist

    /**
     * This method will move the processed file in seperate folder
     * 
     * @param p_fileName
     * @param p_file
     * @return boolean
     */
    public static boolean moveFileToArchive(String p_fileName, String p_file) {
    	StringBuilder loggerValue= new StringBuilder();
        if (_logger.isDebugEnabled()) {
            _logger.debug("moveFileToArchive", " Entered ");
        }
        final File fileRead = new File(p_fileName);
        File fileArchive = new File("" + Constants.getProperty("ICCArchiveFilePath"));
        if (!fileArchive.isDirectory()) {
            fileArchive.mkdirs();
        }

        // fileArchive = new
        // File(""+Constants.getProperty("ICCArchiveFilePath")+"/"+p_file);
        fileArchive = new File("" + Constants.getProperty("ICCArchiveFilePath") + p_file + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
        // make
        // the
        // new
        // file
        // name
        final boolean flag = fileRead.renameTo(fileArchive);
        if (_logger.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting: Exiting File Moved:");
        	loggerValue.append(flag);
        	_logger.debug("moveFileToArchive", loggerValue);
        }
        return flag;
    }// end of moveFileToArchive

    /**
     * This method updates ICC MSISDN mapping in the POS KEY table in the
     * database
     * Creation date: (8/14/03 5:39:37 PM)
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_filename
     *            java.lang.String
     * @param p_modifiedBy
     *            String
     * @param p_locationCode
     *            String
     * @param p_file
     *            string
     * @return java.lang.String
     * @throws BTSLBaseException
     * @exception java.sql.SQLException
     *                The exception description.
     * @exception java.lang.Exception
     *                The exception description.
     */
    public int writeIccMsisdnFileToDatabase(Connection p_con, String p_filename, String p_modifiedBy, String p_locationCode, String p_file) throws BTSLBaseException {
        final String METHOD_NAME = "writeIccMsisdnFileToDatabase";
        StringBuilder loggerValue= new StringBuilder();
        if (_logger.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_filename=");
        	loggerValue.append(p_filename);
        	loggerValue.append(",p_locationCode=");
        	loggerValue.append(p_locationCode);
        	_logger.debug(METHOD_NAME, loggerValue);
        }
        String errstr = "";
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        long recordsTotal = 0;
        int exist = 0;
        boolean fileMoved = false;
        boolean processFile = false;
        int updateMsisdn = 0;
        PosKeyVO poskeyVO = null;
        String delim = Constants.getProperty("Delimiterforuploadiccid");
        final Date currentDate = new Date(System.currentTimeMillis());
        if (BTSLUtil.isNullString(delim)) {
            delim = " ";
        }
        PreparedStatement updatePstmt = null;
        String tempStr = null;
        NetworkPrefixVO networkPrefixVO;
        String msisdnPrefix, networkCode;
        final String updateSql = "UPDATE pos_keys SET msisdn=?,modified_on=?, modified_by=? where ICC_id=? ";
        try {
            String IccId = "", msisdn = "", tempIccId = "";
            boolean IccAdded = false;
            boolean IccUpdated = false;
            final String[] lineNumberArray = new String[2];
            try {
                fileReader = new FileReader("" + p_filename);
                bufferReader = new BufferedReader(fileReader);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            updatePstmt = p_con.prepareStatement(updateSql);

            if (bufferReader.ready()) // If File Not Blank Read line by Line
            {
                tempStr = bufferReader.readLine();
                recordsTotal = 0;
                msisdn = "";

                while (!com.btsl.util.BTSLUtil.isNullString(tempStr)) // If Line
                // is not
                // Blank
                // Process
                // the
                // Number
                {
                    IccAdded = false;
                    IccUpdated = false;
                    recordsTotal++;
                    try {
                        final StringTokenizer parser = new StringTokenizer(tempStr, delim);
                        if(_logger.isDebugEnabled()){
                			loggerValue.setLength(0);
                			loggerValue.append("Input:");
                			loggerValue.append(tempStr);
                			loggerValue.append("There are:");
                			loggerValue.append(parser.countTokens());
                			loggerValue.append(" entries");
                			_logger.debug(METHOD_NAME, loggerValue);
                		}

                        if (parser.countTokens() < 3) {
                            try {
                                while (parser.hasMoreTokens()) {
                                    IccId = parser.nextToken().trim();
                                    msisdn = parser.nextToken().trim();
                                }
                            } catch (NoSuchElementException e) {
                                lineNumberArray[0] = Long.toString(recordsTotal);
                                _logger.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.notgetrecord", lineNumberArray);
                            }
                        } else {
                            lineNumberArray[0] = Long.toString(recordsTotal);
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.impropersyntax", lineNumberArray);
                        }
                        if (_logger.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("writeIccMsisdnFileToDatabase: IccId:");
                        	loggerValue.append(IccId);
                        	loggerValue.append("msisdn:");
                        	loggerValue.append(msisdn);
                        	_logger.debug(METHOD_NAME, loggerValue);
                        }
                        tempIccId = BTSLUtil.NullToString(IccId).trim();
                        // change 1 -Bug No 1117 start
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID))).booleanValue()) {
                            if (BTSLUtil.NullToString(IccId).trim().length() > 20) {
                                lineNumberArray[0] = Long.toString(recordsTotal);
                                throw new BTSLBaseException(this, "writeFileToDatabase", "iccidkeymgmt.poskeyuploadfile.error.icclength", lineNumberArray);

                            }
                        }

                        else {
                            if (BTSLUtil.NullToString(IccId).trim().length() > 15) {
                                lineNumberArray[0] = Long.toString(recordsTotal);
                                throw new BTSLBaseException(this, "writeFileToDatabase", "iccidkeymgmt.poskeyuploadfile.error.imsilength", lineNumberArray);

                            }
                        }

                        IccId = BTSLUtil.calcIccId(IccId, p_locationCode);
                        if(_logger.isDebugEnabled()){
                			loggerValue.setLength(0);
                			loggerValue.append("Final IccId=");
                			loggerValue.append(IccId);
                			_logger.debug(METHOD_NAME, loggerValue);
                		}
                        if (!BTSLUtil.isValidMSISDN(msisdn)) {
                            lineNumberArray[0] = Long.toString(recordsTotal);
                            throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.invalidmobile", lineNumberArray);
                        } else {
                            msisdn = PretupsBL.getFilteredMSISDN(msisdn);
                        }
                        try {
                            msisdnPrefix = PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(msisdn));
                            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                            if (networkPrefixVO == null) {
                                lineNumberArray[0] = msisdn;
                                lineNumberArray[1] = Long.toString(recordsTotal);
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.mobileunsupportnetwork", lineNumberArray);
                            }
                            networkCode = networkPrefixVO.getNetworkCode();
                            if (networkCode == null || !networkCode.equals(p_locationCode)) {
                                lineNumberArray[0] = msisdn;
                                lineNumberArray[1] = Long.toString(recordsTotal);
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.mobileunsupportnetwork", lineNumberArray);
                            }
                        } catch (BTSLBaseException be) {
                            throw be;
                        } catch (Exception e) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("EXCEPTION WHILE CHCECKING FOR PHONE IN NETWORK for MSISDN ");
                        	loggerValue.append(msisdn);
                			loggerValue.append(EXCEPTION);
                			loggerValue.append(e.getMessage());
                			_logger.error(METHOD_NAME, loggerValue);
                            _logger.errorTrace(METHOD_NAME, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "", loggerValue.toString());
                            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                        }
                        poskeyVO = isMsisdnExist(p_con, msisdn);
                        if (poskeyVO == null) {
                            exist = isIccExist(p_con, IccId, p_locationCode);
                            if (exist == 0) {
                                try {
                                    // Clear Paremeters
                                    updatePstmt.clearParameters();
                                    updatePstmt.setString(1, msisdn);
                                    // updatePstmt.setString(2,registered);
                                    updatePstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                    updatePstmt.setString(3, p_modifiedBy);
                                    updatePstmt.setString(4, IccId);
                                    if(_logger.isDebugEnabled()){
                            			loggerValue.setLength(0);
                            			loggerValue.append("updatePstmt=");
                            			loggerValue.append(updateSql);
                            			_logger.debug(METHOD_NAME, loggerValue);
                            		}
                                    updateMsisdn = updatePstmt.executeUpdate();
                                    if (updateMsisdn > 0) {
                                        IccUpdated = true;
                                    }

                                    if (!IccUpdated) {
                                        lineNumberArray[0] = "" + tempIccId;
                                        throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.msisdnnotupdated", lineNumberArray);
                                    }
                                } catch (BTSLBaseException be) {
                                    throw be;
                                } catch (SQLException sqe) {
                                	loggerValue.setLength(0);
                                	loggerValue.append("INVALID RECORD For Icc Id ");
                                	loggerValue.append(tempIccId);
                        			loggerValue.append(SQL_EXCEPTION);
                        			loggerValue.append(sqe.getMessage());
                        			_logger.error(METHOD_NAME, loggerValue);
                                    _logger.errorTrace(METHOD_NAME, sqe);
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                        "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "", loggerValue.toString());
                                    throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                                } catch (Exception e) {
                                	loggerValue.setLength(0);
                                	loggerValue.append("INVALID RECORD For Icc Id ");
                                	loggerValue.append(tempIccId);
                        			loggerValue.append(EXCEPTION);
                        			loggerValue.append(e.getMessage());
                        			_logger.error(METHOD_NAME, loggerValue);
                                    _logger.errorTrace(METHOD_NAME, e);
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                        "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "", loggerValue.toString());
                                    throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                                }
                            } else if (exist == 1) {
                                // error no icc id found
                                lineNumberArray[0] = "" + tempIccId;
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.iccassmsisdnexist", lineNumberArray);
                            } else if (exist == 7) {
                                // error icc id from unsupported network
                                lineNumberArray[0] = "" + tempIccId;
                                lineNumberArray[1] = "" + recordsTotal;
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.iccidnotfromnetwork", lineNumberArray);
                            } else {
                                // error no icc id found
                                lineNumberArray[0] = "" + tempIccId;
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.norecordfound", lineNumberArray);
                            }
                        } else {
                            if (poskeyVO.getIccId().equalsIgnoreCase(IccId)) {
                                lineNumberArray[0] = "" + msisdn;
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.alreadyexist", lineNumberArray);

                            } else {
                                lineNumberArray[0] = "" + msisdn;
                                throw new BTSLBaseException(this, METHOD_NAME, "iccidkeymgmt.iccidmsisdnuploadfile.error.alreadyasoociatedwithkey", lineNumberArray);
                            }
                        }
                    }// end of try
                    catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("INVALID RECORD For Icc Id ");
                    	loggerValue.append(tempIccId);
            			loggerValue.append(EXCEPTION);
            			loggerValue.append(e.getMessage());
            			_logger.error(METHOD_NAME, loggerValue);
                        _logger.errorTrace(METHOD_NAME, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeIccMsisdnFileToDatabase]",
                            "", "", "", loggerValue.toString());
                        throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                    }
                    tempStr = bufferReader.readLine();
                } // end of While
                _logger.debug(METHOD_NAME, " Total Records " + recordsTotal);
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                errstr = errstr + "master.iccmsisdnupload.error.zerofilesize";
                throw new SQLException("Zero file size");
            }

            if (bufferReader != null) {
                bufferReader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }

            if (BTSLUtil.isNullString(errstr)) {
                // Moving File after Processing
                if (IccAdded || IccUpdated) {
                    fileMoved = moveFileToArchive(p_filename, p_file);
                    if (fileMoved) {
                        processFile = true;
                    } else {
                        throw new BTSLBaseException(this, "writeFileToDatabase", "iccidkeymgmt.iccidmsisdnuploadfile.error.filenomove");
                    }
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
        	if(_logger.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("processed till record no=");
    			loggerValue.append(recordsTotal);
    			_logger.debug(METHOD_NAME, loggerValue);
    		}
            // Write in LOGS
            if (!processFile) {
                recordsTotal = 0;
            }
            if(_logger.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("processed=");
    			loggerValue.append(p_file);
    			loggerValue.append("No of records=");
    			loggerValue.append(recordsTotal);
    			loggerValue.append("Status=");
    			loggerValue.append(processFile);
    			loggerValue.append("Message=");
    			loggerValue.append(errstr);
    			_logger.debug(METHOD_NAME, loggerValue);
    		}
            IccFileProcessLog.log(p_file, p_modifiedBy, p_locationCode, " ", recordsTotal, processFile, errstr);
            // Destroying different objects
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (updatePstmt != null) {
                    updatePstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateMsisdn:");
            	loggerValue.append(updateMsisdn);
            	_logger.debug(METHOD_NAME, loggerValue);
            }
        }// end of finally
        return updateMsisdn;
    }

    /**
     * Method loadPosKeyDetailsForICCIDAndMsisdn.
     * 
     * @param p_con
     *            Connection
     * @param isICCID
     *            boolean
     * @param isHistory
     *            boolean
     * @param p_iccIdOrMsisdn
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadPosKeyDetailsForICCIDAndMsisdn(Connection p_con, boolean isICCID, boolean isHistory, String p_iccIdOrMsisdn) throws BTSLBaseException {
        final String METHOD_NAME = "loadPosKeyDetailsForICCIDAndMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (_logger.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_iccIdOrMsisdn=");
        	loggerValue.append(p_iccIdOrMsisdn);
        	loggerValue.append(",isICCID=");
        	loggerValue.append(isICCID);
        	_logger.debug(METHOD_NAME, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rst = null;
        PosKeyVO posKeyVO = null;
        final ArrayList listOfRecords = new ArrayList();
        final String qry = posKeyWebQry.loadPosKeyDetailsForICCIDAndMsisdnQry(isICCID,isHistory);
        try {
        	 if(_logger.isDebugEnabled()){
     			loggerValue.setLength(0);
     			loggerValue.append("Query =");
     			loggerValue.append(qry);
     			_logger.debug(METHOD_NAME, loggerValue);
     		}
            pstmtSelect = p_con.prepareStatement(qry);
            pstmtSelect.setString(1, p_iccIdOrMsisdn);
            rst = pstmtSelect.executeQuery();

            while (rst.next()) {
                posKeyVO = new PosKeyVO();
                posKeyVO.setIccId(rst.getString("icc_id"));
                posKeyVO.setMsisdn(rst.getString("msisdn"));
                posKeyVO.setModifiedBy(rst.getString("modified"));
                posKeyVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(rst.getString("modified_on")));
                posKeyVO.setNetworkCode(rst.getString("network_code"));

                listOfRecords.add(posKeyVO);
            }
        }// end of try
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[loadPosKeyDetailsForICCIDAndMsisdn]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[loadPosKeyDetailsForICCIDAndMsisdn]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception ex) {
            	loggerValue.setLength(0);
    			loggerValue.append( "Exception Closing Result set: ");
    			loggerValue.append(ex.getMessage());
    			_logger.error(METHOD_NAME, loggerValue);
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
            	loggerValue.setLength(0);
    			loggerValue.append( "Exception Closing Prepared set: ");
    			loggerValue.append(ex.getMessage());
    			_logger.error(METHOD_NAME, loggerValue);
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: listOfRecords.size():");
            	loggerValue.append(listOfRecords.size());
            	_logger.debug(METHOD_NAME, loggerValue);
            }
        }// end of finally
        return listOfRecords;

    }// end of loadPosKey

    /**
     * This method will first check the MSISDN in DB and if it exists then
     * update the last transaction id field of the user_phones table
     * 
     * @param p_con
     *            Connection
     * @param p_transactionId
     *            String
     * @param p_msisdn
     *            String
     * @throws BTSLBaseException
     */

    public void isMsisdnExistAndUpdateSimTxnId(Connection p_con, String p_msisdn, String p_transactionId) throws BTSLBaseException {
        final String METHOD_NAME = "isMsisdnExistAndUpdateSimTxnId";
        StringBuilder loggerValue = new StringBuilder();
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered ");
        }

        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rsSelect = null;
        try {
            final String updateQry = "UPDATE user_phones SET temp_transfer_id=? WHERE user_id=? AND msisdn=?";
            final String selectQry = "SELECT UP.user_id FROM user_phones UP,users U WHERE UP.msisdn=? and U.user_id=UP.user_id and U.status NOT IN ('N','C','W')";
            if(_logger.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(updateQry);
    			_logger.debug(METHOD_NAME, loggerValue);
    		}
            if(_logger.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQry);
    			_logger.debug(METHOD_NAME, loggerValue);
    		}
            pstmtUpdate = p_con.prepareStatement(updateQry);
            pstmtSelect = p_con.prepareStatement(selectQry);
            pstmtSelect.setString(1, p_msisdn);
            rsSelect = pstmtSelect.executeQuery();
            while (rsSelect.next()) {
                pstmtUpdate.setString(1, p_transactionId);
                pstmtUpdate.setString(2, rsSelect.getString("user_id"));
                pstmtUpdate.setString(3, p_msisdn);
                pstmtUpdate.executeUpdate();
            }
        }// end of try
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[isMsisdnExistAndUpdateSimTxnId]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[isMsisdnExistAndUpdateSimTxnId]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            
            try {
                if (rsSelect != null) {
                    rsSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("isMsisdnExistAndUpdateSimTxnId ", " Exiting:");
            }
        }
    }
    public int writeFileToDatabaseICCIDKey(Connection con, String filename, String createdBy, String locationCode, ArrayList simProfileList, String file) throws BTSLBaseException {
        final String METHOD_NAME = "writeFileToDatabaseICCIDKey";
        StringBuilder loggerValue= new StringBuilder();
        boolean processFile = false;
        FileReader fileReader = null;
        BufferedReader bufferReader = null;
        long recordsTotal = 0;
        int totRecords = 0;
        String simId = "";
        String delim = Constants.getProperty(PretupsI.DELIMITER_ICCID);
        final Date currentDate = new Date(System.currentTimeMillis());
        PreparedStatement insertPstmt = null;
        String tempStr = null;
        int iccAdded = 0;
        final String insertSql = "INSERT INTO pos_keys(icc_id,decrypt_key,sim_profile_id,created_by,created_on,network_code,registered) VALUES(?,?,?,?,?,?,?) ";
        try {
            String registered = Constants.getProperty(PretupsI.STK_REGISTRATION_REQUIRED);
            if (registered == null || registered.equals(PretupsI.YES)) {
                registered = PretupsI.NO;
            } else {
                registered = PretupsI.YES;
            }
            String IccId = "", DecryptKey = "", tempIccId = "";
            String labelName = null;
            String labelValue = null;
            final String[] lineNumberArray = new String[2];
            boolean simExist = false;
            boolean IccAdded = false;
            boolean fileFormat = false;
            boolean fileMoved = false;
            boolean endDataFound = false;
            StringTokenizer startparser = null;
            insertPstmt = con.prepareStatement(insertSql);
            try {
                fileReader = new FileReader("" + filename);
                bufferReader = new BufferedReader(fileReader);
            } catch (Exception e) {
            }
            if (bufferReader.ready())
            {
                tempStr = bufferReader.readLine();
                if (BTSLUtil.isNullString(tempStr)) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_APP, 0, null);
                } else {
                    startparser = new StringTokenizer(tempStr, "=");
                    if (startparser.countTokens() < 3) {
                        try {
                            while (startparser.hasMoreTokens()) {
                                labelName = startparser.nextToken().trim();
                                labelValue = startparser.nextToken().trim();
                                if (!Constants.getProperty(PretupsI.APP_VERSION).equalsIgnoreCase(labelName)) {
                                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_APP1, 0, null);
                                }
                                if (BTSLUtil.isNullString(labelValue)) {
                                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_APP2, 0, null);
                                }
                            }
                        } catch (NoSuchElementException e) {
                            lineNumberArray[0] = "1";
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NOT_GET_RECORD,lineNumberArray);
                        }
                    } else {
                        lineNumberArray[0] = "1";
                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NOT_GET_RECORD1, lineNumberArray);
                    }
                }
                startparser = null;
                tempStr = bufferReader.readLine();
                if (BTSLUtil.isNullString(tempStr)) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DATE1, 0, null);
                } else {
                    startparser = new StringTokenizer(tempStr, "=");
                    if (startparser.countTokens() < 3) {
                        try {
                            while (startparser.hasMoreTokens()) {
                                labelName = startparser.nextToken().trim();
                                labelValue = startparser.nextToken().trim();
                                if (!(Constants.getProperty(PretupsI.DATE_ICCID)).equalsIgnoreCase(labelName)) {
                                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DATE2, 0, null);
                                }
                                if (BTSLUtil.isNullString(labelValue)) {
                                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DATE3, 0, null);
                                }
                            }
                        } catch (NoSuchElementException e) {
                            lineNumberArray[0] = "2";
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NOT_GET_RECORD, lineNumberArray);
                        }
                    } else {
                        lineNumberArray[0] = "2";
                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NOT_GET_RECORD1,lineNumberArray);
                    }
                }
                tempStr = bufferReader.readLine();
                if (BTSLUtil.isNullString(tempStr)) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SIM_1, 0, null);
                } else {
                    startparser = new StringTokenizer(tempStr, "=");
                    if (startparser.countTokens() < 3) {
                        try {
                            while (startparser.hasMoreTokens()) {
                                labelName = startparser.nextToken().trim();
                                labelValue = startparser.nextToken().trim();
                                if (!Constants.getProperty(PretupsI.SIM_ICCID).equalsIgnoreCase(labelName)) {
                                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SIM_2, 0, null);
                                }
                                if (BTSLUtil.isNullString(labelValue)) {
                                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SIM_3, 0, null);
                                }
                                int simProfileListSize = simProfileList.size();
                                for (int i = 0; i < simProfileListSize; i++) {
                                    final ListValueVO listVal = (ListValueVO) simProfileList.get(i);
                                    if (BTSLUtil.NullToString(labelValue).equalsIgnoreCase(listVal.getValue())) {
                                        simExist = true;
                                        simId = listVal.getValue();
                                        break;
                                    } else {
                                        continue;
                                    }
                                }
                                if (!simExist) {
                                    lineNumberArray[0] = labelValue;
                                    lineNumberArray[1] = "3";
                                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SIM_4, new String[]{lineNumberArray[0], String.valueOf(lineNumberArray[1])});
                                }
                            }
                        } catch (NoSuchElementException e) {
                            lineNumberArray[0] = "3";
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NOT_GET_RECORD,lineNumberArray );
                        }
                    } else {
                        lineNumberArray[0] = "3";
                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NOT_GET_RECORD1, lineNumberArray);
                    }
                }
                tempStr = bufferReader.readLine();
                if (BTSLUtil.isNullString(tempStr)) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DATES1, 0, null);
                } else if (!Constants.getProperty(PretupsI.START_DATA).equalsIgnoreCase(tempStr)) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DATES2, 0, null);
                }

                recordsTotal = 4;
                totRecords = 0;
                DecryptKey = "";
                while (bufferReader.ready()) {
                    tempStr = bufferReader.readLine();
                    if (tempStr.trim().length() == 0) {
                        continue;
                    }
                    IccAdded = false;
                    recordsTotal++;
                    if (Constants.getProperty(PretupsI.END_DATA).equalsIgnoreCase(tempStr.trim())) {
                        endDataFound = true;
                        tempStr = bufferReader.readLine();
                        if (!BTSLUtil.isNullString(tempStr)) {
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DATES3, 0, null);
                        } else {
                            fileFormat = true;
                            IccAdded = true;
                            break;
                        }
                    }
                    try {
                        totRecords++;
                        final StringTokenizer parser = new StringTokenizer(tempStr, delim);
                        if (parser.countTokens() < 3) {
                            try {
                                while (parser.hasMoreTokens()) {
                                    IccId = parser.nextToken().trim();
                                    DecryptKey = parser.nextToken().trim();
                                }
                            } catch (NoSuchElementException e) {
                                lineNumberArray[0] = "" + recordsTotal;
                                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NOT_GET_RECORD,lineNumberArray);
                            }
                        } else {
                            lineNumberArray[0] = "" + recordsTotal;
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NOT_GET_RECORD1, lineNumberArray);
                        }
                        tempIccId = BTSLUtil.NullToString(IccId).trim();

                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID))).booleanValue()) {
                            if (BTSLUtil.NullToString(IccId).trim().length() > 20) {
                                lineNumberArray[0] = "" + recordsTotal;
                                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ICCID_LENGTH, lineNumberArray);
                            }
                        } else {
                            if (BTSLUtil.NullToString(IccId).trim().length() > 15) {
                                lineNumberArray[0] = "" + recordsTotal;
                                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ICCID_LENGTH1, lineNumberArray);
                            }
                        }
                        if (BTSLUtil.NullToString(DecryptKey).trim().length() != 32) {
                            lineNumberArray[0] = "" + recordsTotal;
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ICCID_KEY, lineNumberArray);
                        }
                        IccId = BTSLUtil.calcIccId(IccId, locationCode);
                        final int exist = isNewIccExist(con, IccId);
                        if (exist == 0 || exist == 1) {
                            lineNumberArray[0] = tempIccId;
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ICCID_ALREADY, lineNumberArray);
                        } else if (exist == 3) {
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ICCID_ALREADY, 0, null);
                        }
                        insertPstmt.setString(1, IccId);
                        insertPstmt.setString(2, DecryptKey);
                        if (BTSLUtil.isNullString(simId)) {
                            insertPstmt.setString(3, simId);
                        } else {
                            insertPstmt.setString(3, simId.toUpperCase());
                        }
                        insertPstmt.setString(4, createdBy);
                        insertPstmt.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(currentDate));
                        insertPstmt.setString(6, locationCode);
                        insertPstmt.setString(7, registered);

                        iccAdded = insertPstmt.executeUpdate();
                        if (iccAdded > 0) {
                            IccAdded = true;
                        }
                        if (!IccAdded) {
                            lineNumberArray[0] = tempIccId;
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ICCID_INFO, lineNumberArray);
                        }
                        insertPstmt.clearParameters();
                    }
                    catch (BTSLBaseException be) {
                        throw be;
                    } catch (SQLException sqe) {
                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ICCID_NO_PROCESS, 0, null);
                    } catch (Exception e) {
                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ICCID_NO_PROCESS, 0, null);
                    }
                }
            }
            if (recordsTotal == 0) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.N0_RECORD, 0, null);
            }
            if (totRecords == 0) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_RECORD1, 0, null);
            }
            if (bufferReader != null) {
                bufferReader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }
            if (!fileFormat) {
                if (!endDataFound) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DATES3, 0, null);
                } else {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.IMPROPER_FILE_FORMAT, 0, null);
                }
            }
            if (IccAdded && fileFormat) {
                fileMoved = moveFileToArchive(filename, file);
                if (fileMoved) {
                    processFile = true;
                } else {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_MOVE, 0, null);
                }
            }
        }
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SQL_ERROR_EXCEPTION, 0, null);
        } catch (Exception e) {
            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR, 0, null);
        } finally {
            if (!processFile) {
                totRecords = 0;
            }
            IccFileProcessLog.log(file, createdBy, locationCode, simId, totRecords, processFile, "");
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e) {
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
            }
            try {
                if (insertPstmt != null) {
                    insertPstmt.close();
                }
            } catch (Exception ex) {
            }
        }
        return iccAdded;
    }
    public int writeIccMsisdnFileToDatabaseWithErrorResponse(Connection con, String filename, String modifiedBy, String locationCode, String file, ArrayList<ListValueVO> errorList, int recordsCount) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "writeIccMsisdnFileToDatabaseWithErrorResponse";
        StringBuilder loggerValue = new StringBuilder();
        if (_logger.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: filename=");
            loggerValue.append(filename);
            loggerValue.append(",locationCode=");
            loggerValue.append(locationCode);
            _logger.debug(METHOD_NAME, loggerValue);
        }
        String errstr = "";
        FileReader fileReader = null; // file reader
        BufferedReader bufferReader = null;
        int recordsTotal = 0;
        int exist = 0;
        boolean fileMoved = false;
        boolean processFile = false;
        int updateMsisdn = 0;
        PosKeyVO poskeyVO = null;
        String delim = Constants.getProperty(PretupsI.DELIMITER_FOR_UPLOAD_ICCID);
        final Date currentDate = new Date(System.currentTimeMillis());
        if (BTSLUtil.isNullString(delim)) {
            delim = " ";
        }
        PreparedStatement updatePstmt = null;
        String tempStr = null;
        NetworkPrefixVO networkPrefixVO;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String msisdnPrefix, networkCode;
        final String updateSql = "UPDATE pos_keys SET msisdn=?,modified_on=?, modified_by=? where ICC_id=? ";
        try {
            String IccId = "", msisdn = "", tempIccId = "";
            boolean IccAdded = false;
            boolean IccUpdated = false;
            final String[] lineNumberArray = new String[2];
            try {
                fileReader = new FileReader("" + filename);
                bufferReader = new BufferedReader(fileReader);
            } catch (Exception e) {
              throw e;
            }
            updatePstmt = con.prepareStatement(updateSql);

            if (bufferReader.ready()) // If File Not Blank Read line by Line
            {
                tempStr = bufferReader.readLine();
                recordsTotal = 0;
                msisdn = "";
                ListValueVO valueVO = new ListValueVO();
                while (!com.btsl.util.BTSLUtil.isNullString(tempStr)) // If Line
                // is not
                // Blank
                // Process
                // the
                // Number
                {
                    IccAdded = false;
                    IccUpdated = false;
                    recordsTotal++;
                    try {
                        final StringTokenizer parser = new StringTokenizer(tempStr, delim);
                        if (_logger.isDebugEnabled()) {
                            loggerValue.setLength(0);
                            loggerValue.append("Input:");
                            loggerValue.append(tempStr);
                            loggerValue.append("There are:");
                            loggerValue.append(parser.countTokens());
                            loggerValue.append(" entries");
                            _logger.debug(METHOD_NAME, loggerValue);
                        }

                        if (parser.countTokens() < 3) {
                            try {
                                while (parser.hasMoreTokens()) {
                                    IccId = parser.nextToken().trim();
                                    msisdn = parser.nextToken().trim();
                                }
                            } catch (NoSuchElementException e) {
                                lineNumberArray[0] = Long.toString(recordsTotal);
                                _logger.errorTrace(METHOD_NAME, e);
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NOT_ABLE_TO_RETRIEVE_RECORDS,
                                        null);
                                errorList.add(new ListValueVO(lineNumberArray[0], errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;

                            }
                        } else {
                            lineNumberArray[0] = Long.toString(recordsTotal);
                            String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RECORD_IN_PROPER_SYNTAX,
                                    null);
                            errorList.add(new ListValueVO(lineNumberArray[0], errorMsg));
                            tempStr = bufferReader.readLine();
                            continue;

                        }
                        if (_logger.isDebugEnabled()) {
                            loggerValue.setLength(0);
                            loggerValue.append("writeIccMsisdnFileToDatabase: IccId:");
                            loggerValue.append(IccId);
                            loggerValue.append("msisdn:");
                            loggerValue.append(msisdn);
                            _logger.debug(METHOD_NAME, loggerValue);
                        }


                        tempIccId = BTSLUtil.NullToString(IccId).trim();
                        
                        // change 1 -Bug No 1117 start
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID))).booleanValue()) {
                            if (BTSLUtil.NullToString(IccId).trim().length() > 20) {
                                lineNumberArray[0] = Long.toString(recordsTotal);
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.IMSI_LENGTH_AT_LINE_NUMBER_CANNOT_BE_GREATER_THAN_CHARACTERS,
                                        new String [] {"20"});
                                errorList.add(new ListValueVO(lineNumberArray[0], errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;

                            }
                        } else {
                            if (BTSLUtil.NullToString(IccId).trim().length() > 15) {
                                lineNumberArray[0] = Long.toString(recordsTotal);
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.IMSI_LENGTH_AT_LINE_NUMBER_CANNOT_BE_GREATER_THAN_CHARACTERS,
                                        new String [] {"15"});
                                errorList.add(new ListValueVO(lineNumberArray[0], errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;

                            }
                        }

                        IccId = BTSLUtil.calcIccId(IccId, locationCode);
                        if (_logger.isDebugEnabled()) {
                            loggerValue.setLength(0);
                            loggerValue.append("Final IccId=");
                            loggerValue.append(IccId);
                            _logger.debug(METHOD_NAME, loggerValue);
                        }
                        if (!BTSLUtil.isValidMSISDN(msisdn)) {
                            lineNumberArray[0] = Long.toString(recordsTotal);
                            String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MOBILE_NUMBER_IS_NOT_VALID,
                                    null);
                            errorList.add(new ListValueVO(lineNumberArray[0], errorMsg));
                            tempStr = bufferReader.readLine();
                            continue;
                        } else {
                            msisdn = PretupsBL.getFilteredMSISDN(msisdn);
                        }
                        try {
                            msisdnPrefix = PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(msisdn));
                            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                            if (networkPrefixVO == null) {
                                lineNumberArray[0] = msisdn;
                                lineNumberArray[1] = Long.toString(recordsTotal);
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_IS_FROM_UNSUPPORTED_NETWORK,
                                        new String[]{msisdn});
                                errorList.add(new ListValueVO(lineNumberArray[1], errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;
                            }
                            networkCode = networkPrefixVO.getNetworkCode();
                            if (networkCode == null || !networkCode.equals(locationCode)) {
                                lineNumberArray[0] = msisdn;
                                lineNumberArray[1] = Long.toString(recordsTotal);
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_IS_FROM_UNSUPPORTED_NETWORK,
                                        null);
                                errorList.add(new ListValueVO(lineNumberArray[1], errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;

                            }
                        } catch (BTSLBaseException be) {
                            String errorMsg = RestAPIStringParser.getMessage(locale, be.getMessageKey(),
                                    new String[]{msisdn});
                            errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                            tempStr = bufferReader.readLine();
                            continue;

                        } catch (Exception e) {
                            loggerValue.setLength(0);
                            loggerValue.append("EXCEPTION WHILE CHCECKING FOR PHONE IN NETWORK for MSISDN ");
                            loggerValue.append(msisdn);
                            loggerValue.append(EXCEPTION);
                            loggerValue.append(e.getMessage());
                            _logger.error(METHOD_NAME, loggerValue);
                            _logger.errorTrace(METHOD_NAME, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                    "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "", loggerValue.toString());
                            String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,
                                    null);
                            errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                            tempStr = bufferReader.readLine();
                            continue;
                        }
                        poskeyVO = isMsisdnExist(con, msisdn);
                        if (poskeyVO == null) {
                            exist = isIccExist(con, IccId, locationCode);
                            if (exist == 0) {
                                try {
                                    // Clear Paremeters
                                    updatePstmt.clearParameters();
                                    updatePstmt.setString(1, msisdn);
                                    // updatePstmt.setString(2,registered);
                                    updatePstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                                    updatePstmt.setString(3, modifiedBy);
                                    updatePstmt.setString(4, IccId);
                                    if (_logger.isDebugEnabled()) {
                                        loggerValue.setLength(0);
                                        loggerValue.append("updatePstmt=");
                                        loggerValue.append(updateSql);
                                        _logger.debug(METHOD_NAME, loggerValue);
                                    }
                                    updateMsisdn = updatePstmt.executeUpdate();
                                    IccUpdated = false;
                                    if (updateMsisdn > 0) {
                                        IccUpdated = true;
                                    }

                                    if (!IccUpdated) {
                                        lineNumberArray[0] = "" + tempIccId;
                                        String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_IS_NOT_UPDATED_FOR_ICCID_IMSI,
                                                new String[]{tempIccId});
                                        errorList.add(new ListValueVO(lineNumberArray[0], errorMsg));
                                        tempStr = bufferReader.readLine();
                                        continue;
                                    }
                                } catch (SQLException sqe) {
                                    loggerValue.setLength(0);
                                    loggerValue.append("INVALID RECORD For Icc Id ");
                                    loggerValue.append(tempIccId);
                                    loggerValue.append(SQL_EXCEPTION);
                                    loggerValue.append(sqe.getMessage());
                                    _logger.error(METHOD_NAME, loggerValue);
                                    _logger.errorTrace(METHOD_NAME, sqe);
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                            "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "", loggerValue.toString());
                                    String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,
                                            null);
                                    errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                                    tempStr = bufferReader.readLine();
                                    continue;
                                } catch (Exception e) {
                                    loggerValue.setLength(0);
                                    loggerValue.append("INVALID RECORD For Icc Id ");
                                    loggerValue.append(tempIccId);
                                    loggerValue.append(EXCEPTION);
                                    loggerValue.append(e.getMessage());
                                    _logger.error(METHOD_NAME, loggerValue);
                                    _logger.errorTrace(METHOD_NAME, e);
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                            "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "", loggerValue.toString());
                                    String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,
                                            null);
                                    errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                                    tempStr = bufferReader.readLine();
                                    continue;

                                }
                            } else if (exist == 1) {
                                // error no icc id found
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_IMSI_ALREADY_HAS_MSISDN_ASSOCIATED_WITH_IT,
                                        new String[]{tempIccId});
                                errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;

                            } else if (exist == 7) {
                                // error icc id from unsupported network
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_IMSI_IS_FROM_UNSUPPORTED_NETWORK_AT_LINE_NUMBER,
                                        new String[]{tempIccId});
                                errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;

                            } else {
                                // error no icc id found
                                lineNumberArray[0] = "" + tempIccId;
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_RECORD_FOUND_FOR_ICCID_IMSI_IN_THE_DATABASE,
                                        new String[]{tempIccId});
                                errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;
                            }
                        } else {
                            if (poskeyVO.getIccId().equalsIgnoreCase(IccId)) {
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RECORD_ALREADY_EXISTS_FOR_MSISDN,
                                        new String[]{msisdn});
                                errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;
                            } else {
                                String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MOBILE_NUMBER_ALREADY_ASSOCIATED_WITH_A_KEY_FOR_REASSOCIATION_USE_ASSOCIATE_MSISDN_WITH_ICCID_IMSI_OPTION,
                                        new String[]{msisdn});
                                errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                                tempStr = bufferReader.readLine();
                                continue;
                            }
                        }
                    }// end of try
                    catch (BTSLBaseException be) {
                        String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,
                                null);
                        errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                        tempStr = bufferReader.readLine();
                        continue;

                    } catch (Exception e) {
                        loggerValue.setLength(0);
                        loggerValue.append("INVALID RECORD For Icc Id ");
                        loggerValue.append(tempIccId);
                        loggerValue.append(EXCEPTION);
                        loggerValue.append(e.getMessage());
                        _logger.error(METHOD_NAME, loggerValue);
                        _logger.errorTrace(METHOD_NAME, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeIccMsisdnFileToDatabase]",
                                "", "", "", loggerValue.toString());
                        String errorMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,
                                null);
                        errorList.add(new ListValueVO(Long.toString(recordsTotal), errorMsg));
                        tempStr = bufferReader.readLine();
                        continue;

                    }
                    tempStr = bufferReader.readLine();
                } // end of While
                _logger.debug(METHOD_NAME, " Total Records " + recordsTotal);
            }// end of bufferedReader !=null
            if (recordsTotal == 0) {
                throw new SQLException(PretupsI.FILE_SIZE_ZERO);
            }

            if (bufferReader != null) {
                bufferReader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }

            if (BTSLUtil.isNullString(errstr)) {
                // Moving File after Processing
                if (IccAdded || IccUpdated) {
                    fileMoved = moveFileToArchive(filename, file);
                    if (fileMoved) {
                        processFile = true;
                    } else {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.FILE_CANNOT_BE_MOVED_FOR_BACKUP_PURPOSE, "");
                    }
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append(SQL_EXCEPTION);
            loggerValue.append(sqe.getMessage());
            _logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, "");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(EXCEPTION);
            loggerValue.append(e.getMessage());
            _logger.error(METHOD_NAME, loggerValue);
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[writeIccMsisdnFileToDatabase]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, "");
        } finally {
            if (_logger.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("processed till record no=");
                loggerValue.append(recordsTotal);
                _logger.debug(METHOD_NAME, loggerValue);
            }
            // Write in LOGS
            recordsCount = recordsTotal;

            if (!processFile) {
                recordsTotal = 0;
            }
            if (_logger.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("processed=");
                loggerValue.append(file);
                loggerValue.append("No of records=");
                loggerValue.append(recordsTotal);
                loggerValue.append("Status=");
                loggerValue.append(processFile);
                loggerValue.append("Message=");
                loggerValue.append(errstr);
                _logger.debug(METHOD_NAME, loggerValue);
            }
            IccFileProcessLog.log(file, modifiedBy, locationCode, " ", recordsTotal, processFile, errstr);
            // Destroying different objects
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (updatePstmt != null) {
                    updatePstmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("Exiting: updateMsisdn:");
                loggerValue.append(updateMsisdn);
                _logger.debug(METHOD_NAME, loggerValue);
            }
        }// end of finally
        return recordsCount - errorList.size();
    }
}
