package com.btsl.pretups.iccidkeymgmt.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

/**
 * @(#)PosKeyDAO.java
 *                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                    All Rights Reserved
 *                    Data Access Object for POS Keys
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Gurjeet Singh Bedi 04/07/2005 Initial Creation
 * 
 *                    Change 1 for file TelesoftPreTUPsv5.0-test record
 *                    sheet_networkadmin11.xls.Bug fixed-1114, 1117. Fixed on
 *                    27/10/06 by Siddhartha
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 */


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
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.IccFileProcessLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * 
 */
public class PosKeyDAO {
    /**
     * Field _logger.
     */
    private static Log _logger = LogFactory.getLog(PosKeyDAO.class.getName());
    private PosKeyQry posKeyQry;

    /**
     * PosKeyDAO constructor comment.
     */
    public PosKeyDAO() {
        super();
        posKeyQry = (PosKeyQry)ObjectProducer.getObject(QueryConstants.POS_KEY_QRY, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * This method will return the HashMap from the simvender_master_key_mapping
     * table,
     * 
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap masterKeyByVenderCode() throws BTSLBaseException {
        final String METHOD_NAME = "masterKeyByVenderCode";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PosKeyVO posKeyVO = null;
        String qry = "SELECT master_key, registered,network_code,sim_vender_code,sim_profile_id FROM simvender_master_key_mapping";
        Connection con = null;
        HashMap masterKeyMap = new HashMap();
        try {
            con = OracleUtil.getSingleConnection();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Select Query :: " + qry);
            }
            // Get Preapared Statement
            pstmt = con.prepareStatement(qry);
            // Execute Query
            rs = pstmt.executeQuery();
            while (rs.next()) {
                posKeyVO = new PosKeyVO();
                posKeyVO.setKey(rs.getString("master_key"));
                posKeyVO.setNetworkCode(rs.getString("network_code"));
                posKeyVO.setSimVenderCode(rs.getString("sim_vender_code"));
                if ("Y".equals(rs.getString("registered"))) {
                    posKeyVO.setRegistered(true);
                } else {
                    posKeyVO.setRegistered(false);
                }
                posKeyVO.setSimProfile(rs.getString("sim_profile_id"));
                masterKeyMap.put(posKeyVO.getSimVenderCode() + "_" + posKeyVO.getNetworkCode() + "_" + posKeyVO.getSimProfile(), posKeyVO);
            }// end if
        }// end of try
        catch (SQLException sqle) {
            _logger.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _logger.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PoskeyDAO[masterKeyByVenderCode]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");

        }// end of catch
        catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PoskeyDAO[masterKeyByVenderCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, " Exception Closing RS : " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing Prepared Stmt: " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("masterKeyByVenderCode() ", " Exiting masterKeyMap :" + masterKeyMap.size());
            }
        }// end of finally
        return masterKeyMap;
    }// end of masterKeyByVenderCode

    /**
     * This method will return the HashMap from the simvender_master_key_mapping
     * table,
     * 
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap loadEncryptionParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadEncryptionParameters";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SimProfileVO simProfileVO = null;
        HashMap cipherParameterMap = new HashMap();
        String qry = posKeyQry.loadEncryptionParametersQry();
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Select Query :: " + qry);
            }

            // Get Preapared Statement
            pstmt = con.prepareStatement(qry);

            // Execute Query
            rs = pstmt.executeQuery();
            while (rs.next()) {
                simProfileVO = new SimProfileVO();
                simProfileVO.setEncryptALGO(rs.getString("encrypt_algo"));
                simProfileVO.setEncryptMode(rs.getString("encrypt_mode"));
                simProfileVO.setEncryptPad(rs.getString("encrypt_padding"));
                simProfileVO.setNetworkCode(rs.getString("network_code"));
                simProfileVO.setSimID(rs.getString("sim_profile_id"));
                simProfileVO.setSimVenderCode(rs.getString("sim_vender_code"));
                cipherParameterMap.put(simProfileVO.getSimVenderCode() + "_" + simProfileVO.getNetworkCode(), simProfileVO);
            }
        }// end of try
        catch (SQLException sqle) {
            _logger.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _logger.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PoskeyDAO[loadEncryptionParameters]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");

        }// end of catch
        catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PoskeyDAO[masterKeyByVenderCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, " Exception Closing RS : " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing Prepared Stmt: " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadEncryptionParameters() ", " Exiting cipherParameterMap :" + cipherParameterMap.size());
            }
        }// end of finally
        return cipherParameterMap;
    }// end of masterKeyByVenderCode

    /**
     * This method will return the object PosKey from the Pos_Key table,
     * 
     * @param p_con
     *            Connection
     * @param p_usrMsisdn
     * @return object of PosKeyVO
     * @throws BTSLBaseException
     */
    public PosKeyVO loadPosKeyByMsisdn(Connection p_con, String p_usrMsisdn) throws BTSLBaseException {
        final String METHOD_NAME = "loadPosKeyByMsisdn";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered p_usrMsisdn:" + p_usrMsisdn);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PosKeyVO posKeyVO = null;
        String qry = "SELECT icc_id,decrypt_key, registered,sim_profile_id,network_code FROM pos_keys WHERE msisdn=? ";
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Select Query :: " + qry);
            }
            // Get Preapared Statement
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, p_usrMsisdn);
            // Execute Query
            rs = pstmt.executeQuery();
            if (rs.next()) {
                posKeyVO = new PosKeyVO();
                posKeyVO.setMsisdn(p_usrMsisdn);
                posKeyVO.setIccId(rs.getString("icc_id"));
                posKeyVO.setKey(rs.getString("decrypt_key"));
                posKeyVO.setNetworkCode(rs.getString("network_code"));
                if ("Y".equals(rs.getString("registered"))) {
                    posKeyVO.setRegistered(true);
                } else {
                    posKeyVO.setRegistered(false);
                }
                posKeyVO.setSimProfile(rs.getString("sim_profile_id"));
            }// end if
        }// end of try
        catch (SQLException sqle) {
            _logger.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _logger.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PoskeyDAO[loadPosKeyByMsisdn]", "", p_usrMsisdn, "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");

        }// end of catch
        catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PoskeyDAO[loadPosKeyByMsisdn]", "", p_usrMsisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, " Exception Closing RS : " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing Prepared Stmt: " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadPosKeyByMsisdn() ", " Exiting posKeyVO :" + posKeyVO);
            }
        }// end of finally
        return posKeyVO;
    }// end of findPosKey

    /**
     * This method will return the last transaction id from the Pos_Key table,
     * 
     * @param p_con
     *            Connection
     * @param p_usrMsisdn
     *            String
     * @return string
     * @throws BTSLBaseException
     */
    public String getLastTransactionId(Connection p_con, String p_usrMsisdn) throws BTSLBaseException {
        final String METHOD_NAME = "getLastTransactionId";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_usrMsisdn:" + p_usrMsisdn);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String ltId = null;
        String qry = "SELECT last_transaction FROM pos_keys WHERE msisdn=? ";
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug(" getLastTransactionId", " Query :: " + qry);
            }
            // Get Preapared Statement
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, p_usrMsisdn);
            // Execute Query
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ltId = rs.getString("last_transaction");
            }
        }// end of try
        catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[getLastTransactionId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[getLastTransactionId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing RS : " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing Prepared Stmt: " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("getLastTransactionId ", " Exiting bared :" + ltId);
            }
        }// end of finally
        return ltId;
    }// end of findPosKey

    /**
     * This method will update the registered field of the Pos_key table
     * 
     * @param p_con
     *            Connection
     * @param usrMsisdn
     *            String
     * @return the number of records updated
     * @throws BTSLBaseException
     */
    public int registerUser(Connection p_con, String usrMsisdn) throws BTSLBaseException {
        final String METHOD_NAME = "registerUser";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered usrMsisdn:" + usrMsisdn);
        }
        PreparedStatement pstmt = null;
        String qry = "UPDATE pos_keys SET registered='Y' WHERE msisdn=? ";
        int updCount = 0;
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug(" registerUser ", "Query :: " + qry);
            }
            // Get Preapared Statement
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, usrMsisdn);
            // Execute Query
            updCount = pstmt.executeUpdate();
        }// end of try
        catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[registerUser]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[registerUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing Prepared Stmt: " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("registerUser ", " Exiting bared :" + updCount);
            }
        }// end of finally
        return updCount;
    }// end of registerUser

    /**
     * This method will update the last transaction id filed of the pos_keys
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_filename
     *            String
     * @param p_createdBy
     *            String
     * @param p_locationCode
     *            String
     * @param p_simProfileList
     *            ArrayList
     * @param p_file
     *            String
     * @return the number of records updated
     * @throws BTSLBaseException
     */
    /*
     * public int updateTransactionId(Connection p_con,String
     * p_transactionId,String p_usrMsisdn) throws SQLException, Exception
     * {
     * if(_logger.isDebugEnabled())_logger.debug("updateTransactionId() ",
     * " Entered p_usrMsisdn:"+p_usrMsisdn+" p_transactionId:"+p_transactionId);
     * PreparedStatement pstmt = null;
     * String qry = "UPDATE POS_KEYS set LAST_TRANSACTION =? WHERE MSISDN=? ";
     * int updCount=0;
     * try
     * {
     * if(_logger.isDebugEnabled())_logger.debug(" updateTransactionId() ",
     * " Query :: "+qry);
     * // Get Preapared Statement
     * pstmt= p_con.prepareStatement(qry);
     * pstmt.setString(1,p_transactionId);
     * pstmt.setString(2,p_usrMsisdn);
     * // Execute Query
     * updCount= pstmt.executeUpdate();
     * }//end of try
     * catch(SQLException sqe)
     * {
     * _logger.error("updateTransactionId"," Exception : " + sqe.getMessage());
     * throw sqe;
     * }//end of catch
     * catch(Exception ex)
     * {
     * _logger.error("updateTransactionId() "," Exception : " +
     * ex.getMessage());
     * throw ex;
     * }//end of catch
     * finally
     * {
     * try{ if(pstmt!= null) pstmt.close(); } catch(Exception ex){}
     * if(_logger.isDebugEnabled())_logger.debug("updateTransactionId() ",
     * " Exiting bared :"+updCount);
     * }//end of finally
     * return updCount;
     * }//end of registerUser
     * 
     * 
     * 
     * 
     * 
     * //method added by alok jain on 14 apr 2004
     * 
     * /**
     * Method loadPosKey.
     * 
     * @param p_con Connection
     * 
     * @param p_iccid String
     * 
     * @param p_msisdn String
     * 
     * @return PosKeyVO
     * 
     * @throws BTSLBaseException
     */
    public PosKeyVO loadPosKey(Connection p_con, String p_iccid, String p_msisdn) throws BTSLBaseException {
        final String METHOD_NAME = "loadPosKey";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered iccid=" + p_iccid + " msisdn=" + p_msisdn);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rst = null;
        PosKeyVO posKeyVO = null;
        String qryBuf=posKeyQry.loadPosKeyQry();

        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Query :: " + qryBuf);
            }
            pstmtSelect = p_con.prepareStatement(qryBuf);
            pstmtSelect.setString(1, p_iccid);
            pstmtSelect.setString(2, p_msisdn);
            rst = pstmtSelect.executeQuery();

            if (rst.next()) {
                posKeyVO = new PosKeyVO();
                posKeyVO.setIccId(rst.getString("icc_id"));
                posKeyVO.setMsisdn(rst.getString("msisdn"));

                if ("Y".equalsIgnoreCase(rst.getString("registered"))) {
                    posKeyVO.setRegistered(true);
                } else {
                    posKeyVO.setRegistered(false);
                }
                posKeyVO.setCreatedBy(rst.getString("created"));
                posKeyVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(rst.getString("created_on")));
                posKeyVO.setKey(rst.getString("decrypt_key"));
                posKeyVO.setNewIccId(rst.getString("new_icc_id"));
                posKeyVO.setModifiedBy(rst.getString("modified"));
                posKeyVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(rst.getString("modified_on")));
                posKeyVO.setSimProfile(rst.getString("sim_profile_id"));
                posKeyVO.setNetworkCode(rst.getString("network_code"));
            }
        }// end of try
        catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException:" + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[loadPosKey]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[loadPosKey]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing Result set: " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing Prepared Stmt: " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting posKeyVO=" + posKeyVO);
            }
        }// end of finally
        return posKeyVO;

    }// end of loadPosKey

    /**
     * This method will be used to check the whether the incoming transaction is
     * more than the existing transaction id.
     * in the pos_keys table
     * 
     * @param p_fileName
     *            String
     * @param p_file
     *            String
     * @return the true or false, if in transaction id is valid then true, else
     *         false
     */
    /*
     * public boolean validateTransactionId(Connection p_con,String
     * p_transactionId,String p_usrMsisdn) throws SQLException, Exception
     * {
     * if(_logger.isDebugEnabled())
     * _logger.debug("validateTransactionId"," Entered p_usrMsisdn:"+p_usrMsisdn+
     * " p_transactionId:"+p_transactionId);
     * PreparedStatement pstmtSelect = null;
     * ResultSet rs= null;
     * boolean bFlag=true;
     * String txnID="";
     * String qry =
     * "select LAST_TRANSACTION from POS_KEYS where MSISDN=? for update of LAST_TRANSACTION"
     * ;
     * try
     * {
     * if(_logger.isDebugEnabled())_logger.debug(" validateTransactionId",
     * " Query :: "+qry);
     * // Get Preapared Statement
     * pstmtSelect= p_con.prepareStatement(qry);
     * //pstmtSelect.setString(1,p_transactionId);
     * pstmtSelect.setString(1,p_usrMsisdn);
     * // Execute Query
     * rs= pstmtSelect.executeQuery();
     * 
     * if(rs == null)
     * return bFlag;
     * 
     * if(rs.next())
     * {
     * txnID=rs.getString("LAST_TRANSACTION");
     * if(!BTSLUtil.isNullString(txnID))
     * {
     * long dbTxnId=Long.parseLong(txnID);
     * long inTxnId=Long.parseLong(p_transactionId);
     * if(inTxnId <=dbTxnId)
     * {
     * bFlag=false;
     * _logger.error("validateTransactionId"," from DB: " +
     * dbTxnId+" from STK:"+inTxnId);
     * }
     * else
     * bFlag=true;
     * }//end if
     * }
     * else
     * {
     * bFlag=true;
     * }
     * //end if
     * }//end of try
     * catch(SQLException sqe)
     * {
     * _logger.error("validateTransactionId"," Exception : " +
     * sqe.getMessage());
     * bFlag=false;
     * throw sqe;
     * }//end of catch
     * catch(Exception ex)
     * {
     * _logger.error("validateTransactionId"," Exception : " + ex.getMessage());
     * bFlag=false;
     * throw ex;
     * }//end of catch
     * finally
     * {
     * try{ if(rs!= null) rs.close(); } catch(Exception ex){}
     * try{ if(pstmtSelect!= null) pstmtSelect.close(); } catch(Exception ex){}
     * if(_logger.isDebugEnabled())_logger.debug("validateTransactionId",
     * " Exiting bared :"+bFlag);
     * }//end of finally
     * return bFlag;
     * }//end of validateTransactionId
     *//**
     * This method will return true or false, if mobile number is available
     * in the network
     * 
     * @return boolean
     *         Added by gurjeet on 26/08/2003
     *         Modified By Gurjeet on 08/07/2004
     */
    /*
     * private boolean isPhoneInNetwork(Connection p_con,String p_msisdn,String
     * p_locationCode) throws SQLException, Exception
     * {
     * if(_logger.isDebugEnabled())_logger.debug("isPhoneInNetwork",
     * " Entered retMsisdn:"+p_msisdn+"p_locationCode:"+p_locationCode);
     * PreparedStatement pstmtSelect = null;
     * ResultSet rs = null;
     * int diff,diff1;
     * boolean found=false;
     * String msisdn=null;
     * int len1=0;
     * int len=p_msisdn.length();
     * String resMsisdn=null;
     * 
     * //Added By Gurjeet on 08/07/2004 to accomodate the change in Network
     * Prefix number list
     * int prefixLength=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LENGTH_CODE))).intValue();
     * 
     * //Gurjeet: 04/01/2005 Modified to remove the length constraint
     * msisdn=p_msisdn.substring(0,prefixLength);
     * 
     * 
     * if(len>prefixLength)
     * {
     * diff=len-10;
     * msisdn=p_msisdn.substring(diff,diff+prefixLength);
     * }
     * 
     * 
     * //_logger.debug(" Phone from front :: len::"+len+"msisdn :: "+msisdn);
     * String qry =
     * "SELECT MSISDN_PREFIX prefix FROM network_prefix np, locations l WHERE l.location_code=? AND l.external_id=np.network_id "
     * ;
     * try
     * {
     * if(_logger.isDebugEnabled())_logger.debug(" isPhoneInNetwork"," Query :: "
     * +qry);
     * // Get Preapared Statement
     * pstmtSelect= p_con.prepareStatement(qry);
     * pstmtSelect.setString(1,p_locationCode);
     * // Execute Query
     * rs= pstmtSelect.executeQuery();
     * while(rs.next())
     * {
     * resMsisdn=""+rs.getInt("prefix");
     * len1=resMsisdn.length();
     * if(len1>prefixLength)
     * {
     * diff1=len1-prefixLength;
     * resMsisdn=resMsisdn.substring(diff1,len1);
     * //_logger.debug(" Phone in DB :: length::"+len1+"resMsisdn :: "+resMsisdn)
     * ;
     * }
     * if(resMsisdn.equalsIgnoreCase(msisdn))
     * found=true;
     * if(found)
     * break;
     * else
     * continue;
     * }
     * }//end of try
     * catch(SQLException sqe)
     * {
     * _logger.error("isPhoneInNetwork"," Exception : " + sqe.getMessage());
     * found=false;
     * throw new SQLException("master.isphoneinnet.error.exception");
     * }//end of catch
     * catch(Exception ex)
     * {
     * _logger.error("isPhoneInNetwork"," Exception : " + ex.getMessage());
     * found=false;
     * throw new Exception("master.isphoneinnet.error.exception");
     * }//end of catch
     * finally
     * {
     * try{ if(rs!= null) rs.close(); } catch(Exception
     * ex){_logger.error(" isPhoneInNetwork","  Exception Closing RS : " +
     * ex.getMessage());}
     * try{ if(pstmtSelect!= null) pstmtSelect.close(); } catch(Exception
     * ex){_logger
     * .error(" isPhoneInNetwork","  Exception Closing Prepared Stmt: " +
     * ex.getMessage());}
     * if(_logger.isDebugEnabled())_logger.debug("isPhoneInNetwork",
     * " Exiting found :"+found);
     * }//end of finally
     * return found;
     * }//end
     */

    /**
     * This method loads the ICC and MSISDN information for pos_keys table
     * 
     * @param p_con
     * @param p_msisdn
     *            String
     * @param p_iccId
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadICCIDMsisdnDetails(Connection p_con, String p_msisdn, String p_iccId) throws BTSLBaseException {
        final String METHOD_NAME = "loadICCIDMsisdnDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered p_Msisdn:" + p_msisdn + ",p_iccId=" + p_iccId);
        }
        PreparedStatement pstmtSelect = null;
        PosKeyVO posKeyVO = null;
        ArrayList posKeyVOList = null;
        ResultSet rs = null;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT pk.icc_id iccid, pk.msisdn msisdn, pk.registered registered, to_char(pk.created_on,'dd/mm/yy hh24:mi:ss') created_on, ");
            strBuff.append("pk.decrypt_key dekey, pk.new_icc_id new_icc, to_char(pk.modified_on,'dd/mm/yy hh24:mi:ss') modified_on, ");
            strBuff.append("pk.sim_profile_id profile_id,network_code FROM pos_keys pk WHERE (pk.icc_id=? or pk.msisdn=?) ");
            
            // //Added on request of Bedi's by Sanjay

            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query=" + strBuff);
            }
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            pstmtSelect.setString(1, p_iccId);
            pstmtSelect.setString(2, p_msisdn);
            rs = pstmtSelect.executeQuery();
            posKeyVOList = new ArrayList();
            while (rs.next()) {
                posKeyVO = new PosKeyVO();
                posKeyVO.setIccId(rs.getString("iccid"));
                posKeyVO.setMsisdn(rs.getString("msisdn"));

                if ("Y".equalsIgnoreCase(rs.getString("registered"))) {
                    posKeyVO.setRegistered(true);
                } else {
                    posKeyVO.setRegistered(false);
                }
                posKeyVO.setCreatedOnStr(rs.getString("created_on"));
                posKeyVO.setKey(rs.getString("dekey"));
                posKeyVO.setNewIccId(rs.getString("new_icc"));
                posKeyVO.setModifiedOnStr(rs.getString("modified_on"));
                posKeyVO.setSimProfile(rs.getString("profile_id"));
                posKeyVO.setNetworkCode(rs.getString("network_code"));
                posKeyVOList.add(posKeyVO);
            }
        }// end of try
        catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException:" + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[loadICCIDMsisdnDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[loadICCIDMsisdnDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting:list size=" + posKeyVOList.size());
            }
        }
        return posKeyVOList;
    }// end of showICCIDMsisdnDetails

    /**
     * This method assign the mobile number with ICC ID
     * 
     * @param p_con
     *            Connection
     * @param p_firstMsisdn
     *            String
     * @param p_secondIccId
     *            String
     * @param p_modifiedBy
     *            String
     * @return int
     * @throws BTSLBaseException
     */

    public int assignMsisdnWihIccId(Connection p_con, String p_firstMsisdn, String p_secondIccId, String p_modifiedBy) throws BTSLBaseException {
        final String METHOD_NAME = "assignMsisdnWihIccId";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered p_firstMsisdn:" + p_firstMsisdn + ",p_secondIccId=" + p_secondIccId + ",p_modifiedBy=" + p_modifiedBy);
        }
        int updateCount = 0;
        PreparedStatement pstmtUpdate = null;
        try {
            Timestamp currentTime = BTSLUtil.getTimestampFromUtilDate(new java.util.Date());
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE pos_keys SET msisdn=?,modified_by=?,modified_on=?,new_icc_id=?,registered=? ");
            strBuff.append("WHERE icc_id=? ");
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query=" + strBuff.toString());
            }
            pstmtUpdate = p_con.prepareStatement(strBuff.toString());
            pstmtUpdate.setString(1, p_firstMsisdn);
            pstmtUpdate.setString(2, p_modifiedBy);
            pstmtUpdate.setTimestamp(3, currentTime);
            pstmtUpdate.setNull(4, Types.VARCHAR);
            pstmtUpdate.setString(5,PretupsI.YES);
			pstmtUpdate.setString(6,p_secondIccId);
            // is need to check the isRecord is modified
            updateCount = pstmtUpdate.executeUpdate();

            /*
             * Code to check the MSISDN is already existing in User_Phones
             * table.
             * If YES then update TXN_ID.
             */
            if (updateCount > 0) {
                ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                if (channelUserDAO.isPhoneExists(p_con, p_firstMsisdn)) {
                    updateCount = channelUserDAO.updateTransactionId(p_con, PretupsI.UPD_SIM_TXN_ID, p_firstMsisdn);
                    if (updateCount <= 0) {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_USER_MSISDN_NOT_FOUND);
                    }
                }
            }
        } catch (BTSLBaseException e) {
            _logger.error("mapIccidMsisdn", "BTSLBaseException " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw e;
        } catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException:" + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[assignMsisdnWihIccId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[assignMsisdnWihIccId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("assignMsisdnWihIccId ", " Exiting with updateCount=" + updateCount);
            }
        }// end of finally
        return updateCount;
    }// end of assignMsisdnWihIccId

    /**
     * This method will release the ICC ID so that it can be used again
     * 
     * @param p_con
     * 
     * @param p_firstIccId
     *            String
     * @param p_modifiedBy
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int reUtilizeIccId(Connection p_con, String p_firstIccId, String p_modifiedBy) throws BTSLBaseException {
        final String METHOD_NAME = "reUtilizeIccId";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered p_firstIccId:" + p_firstIccId + ",p_modifiedBy=" + p_modifiedBy);
        }
        int updateCount = 0;
        PreparedStatement pstmtUpdate = null;
        try {
            Timestamp currentTime = BTSLUtil.getTimestampFromUtilDate(new java.util.Date());
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE pos_keys SET msisdn=?,modified_by=?,modified_on=?,registered=?, ");
            strBuff.append("new_icc_id=? WHERE icc_id=? ");
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query=" + strBuff.toString());
            }

            pstmtUpdate = p_con.prepareStatement(strBuff.toString());
            pstmtUpdate.setNull(1, Types.VARCHAR);
            pstmtUpdate.setString(2, p_modifiedBy);
            pstmtUpdate.setTimestamp(3, currentTime);
            pstmtUpdate.setString(4, "N");
            pstmtUpdate.setNull(5, Types.VARCHAR);
            pstmtUpdate.setString(6, p_firstIccId);
            // is there is any requirement to check the isRecordModified.
            updateCount = pstmtUpdate.executeUpdate();
        }// end of try
        catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException:" + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[reUtilizeIccId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[reUtilizeIccId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("reUtilizeIccId ", " Exiting with updateCount=" + updateCount);
            }
        }// end of finally
        return updateCount;
    }// end of reUtilizeIccId

    // method added by alok jain on 14 apr 2004

    /**
     * This methos is used to check that the msisdn(mobile number) is exists in
     * Pos_Keys table or not
     * Creation date: (14/12/04)
     * 
     * @author Vinay Kumar
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @param p_locationCode
     *            String
     * @return boolean
     * @throws SQLException
     * @throws Exception
     */
    /*
     * public boolean isMsisdnExistForUpdateSimTxnId(Connection p_con,String
     * p_msisdn,String p_locationCode) throws SQLException,Exception {
     * if(_logger.isDebugEnabled())_logger.debug("isMsisdnExistForUpdateSimTxnId "
     * ," Entered : p_msisdn:"+p_msisdn+", p_locationCode="+p_locationCode);
     * 
     * if(!isPhoneInNetwork(p_con,p_msisdn,p_locationCode)){
     * throw new Exception();
     * }
     * PreparedStatement pstmtSelect = null;
     * ResultSet rs = null;
     * boolean flag=false;
     * String checkSql = "SELECT 1 FROM POS_KEYS WHERE msisdn=? ";
     * try {
     * pstmtSelect=p_con.prepareStatement(checkSql);
     * pstmtSelect.setString(1,p_msisdn);
     * rs = pstmtSelect.executeQuery();
     * if(rs.next())
     * flag=true;
     * }
     * catch (SQLException sqle) {
     * _logger.error("isMsisdnExistForUpdateSimTxnId "," Exception "+sqle.getMessage
     * ());
     * throw sqle;
     * }
     * catch (Exception ex) {
     * _logger.error("isMsisdnExistForUpdateSimTxnId "," Exception "+ex.getMessage
     * ());
     * throw ex;
     * }
     * finally {
     * try{if(rs !=null) rs.close();}catch(Exception e){}
     * try{if(pstmtSelect !=null) pstmtSelect.close();}catch(Exception e){}
     * if(_logger.isDebugEnabled())
     * _logger.debug("isMsisdnExistForUpdateSimTxnId "," Exiting.");
     * }
     * return flag;
     * }
     */
    public int batchCorrectMSISDNwithICCIDMapping(Connection p_con,String p_filename,String p_modifiedBy,String p_locationCode, String p_file) throws BTSLBaseException
	{
		String methodName = "batchCorrectMSISDNwithICCIDMapping";
		if(_logger.isDebugEnabled())
			_logger.debug(methodName," Entered p_filename:"+p_filename);
		String errstr="";
		FileReader fileReader=null; //file reader
		BufferedReader bufferReader = null;
		int recordsTotal = 0;
		boolean fileMoved=false;
		boolean processFile=false;
		String delim=Constants.getProperty("Delimiterforuploadiccid");
	
		if(BTSLUtil.isNullString(delim))
			delim=",";
		String tempStr="";
		NetworkPrefixVO networkPrefixVO;
		String msisdnPrefix,networkCode;

		try
		{
			String IccId = "",msisdn="",tempIccId="";
			String[] lineNumberArray = new String[2];
			try
			{
				fileReader=new FileReader(p_filename);
				bufferReader = new BufferedReader(fileReader);
				bufferReader.readLine();
			}
			catch(Exception e)
			{
				bufferReader=null;
			}
			
			while(!BTSLUtil.isNullString(tempStr=bufferReader.readLine())) // If Line is not Blank Process the Number
			{
				recordsTotal++;
				try
				{
					StringTokenizer parser = new StringTokenizer(tempStr, delim);
					if(_logger.isDebugEnabled())
					{
						_logger.debug(methodName,"Input:"+tempStr+" Record No: "+recordsTotal);
						_logger.debug(methodName,"There are :"+parser.countTokens() + " entries");
					}
					
					while(parser.hasMoreTokens())
		  			{
						IccId=parser.nextToken().trim();
						msisdn=parser.nextToken().trim();
		  			}
					
					if(_logger.isDebugEnabled())
					{
						_logger.debug(methodName,methodName+": IccId:"+IccId);
						_logger.debug(methodName,methodName+": msisdn:"+msisdn);
					}
				
                    IccId=BTSLUtil.calcIccId(IccId,p_locationCode);
                    if(_logger.isDebugEnabled())
						_logger.debug(methodName,methodName+": Final IccId:"+IccId);
                    
                    PosKeyVO iccidVO = null;
                    PosKeyVO msisdnVO = null;
                    
                    StringBuffer checkIccQuery = new StringBuffer("select icc_id,msisdn,registered,decrypt_key,sim_profile_id from pos_keys where icc_id=?");
                    if(_logger.isDebugEnabled())
						_logger.debug(methodName,"selectQuery:"+checkIccQuery.toString());
                    
                    StringBuffer checkMsisdnQuery = new StringBuffer("select icc_id,msisdn,registered,decrypt_key,sim_profile_id from pos_keys where msisdn=?");
                    if(_logger.isDebugEnabled())
						_logger.debug(methodName,"selectQuery:"+checkMsisdnQuery.toString());
                    
                    PreparedStatement checkIccPstmt = p_con.prepareStatement(checkIccQuery.toString());
                    PreparedStatement checkMsisdnPstmt = p_con.prepareStatement(checkMsisdnQuery.toString());
                    checkIccPstmt.setString(1, IccId);
                    ResultSet checkIccRS = checkIccPstmt.executeQuery();
                    if(checkIccRS.next()){
                    	iccidVO = new PosKeyVO();
                    	iccidVO.setIccId(checkIccRS.getString("icc_id"));
                    	iccidVO.setMsisdn(checkIccRS.getString("msisdn"));
                    	iccidVO.setRegistered("Y".equals(checkIccRS.getString("registered")));
                    	iccidVO.setKey(checkIccRS.getString("decrypt_key"));
                    	iccidVO.setSimProfile(checkIccRS.getString("sim_profile_id"));
                    }
                    if(iccidVO==null){
                    	lineNumberArray[0]=IccId;
                    	lineNumberArray[1]=recordsTotal+"";
                    	throw new BTSLBaseException(this,methodName,"iccidkeymgmt.iccidbatchcorrectmapping.error.iccidinvalid",lineNumberArray);
                    }
                    checkMsisdnPstmt.setString(1, msisdn);
                    ResultSet checkMsisdnRS = checkMsisdnPstmt.executeQuery();
                    if(checkMsisdnRS.next()){
                    	msisdnVO = new PosKeyVO();
                    	msisdnVO.setIccId(checkMsisdnRS.getString("icc_id"));
                    	msisdnVO.setMsisdn(checkMsisdnRS.getString("msisdn"));
                    	msisdnVO.setRegistered("Y".equals(checkMsisdnRS.getString("registered")));
                    	msisdnVO.setKey(checkMsisdnRS.getString("decrypt_key"));
                    	msisdnVO.setSimProfile(checkMsisdnRS.getString("sim_profile_id"));
                    }
        
					int count = 0;
                    if(msisdnVO!=null && !IccId.equals(msisdnVO.getIccId())){
                    	count= reUtilizeIccId(p_con,msisdnVO.getIccId(),p_modifiedBy);
        				if(count>0)
        				{
        					count=0;
        					count= assignMsisdnWihIccId(p_con,msisdn,iccidVO.getIccId(),p_modifiedBy);
        				}
                        
                	} else {
                		count= assignMsisdnWihIccId(p_con,msisdn,iccidVO.getIccId(),p_modifiedBy);
                	}
                    if(count != 1) {
                    	lineNumberArray[0]=recordsTotal+"";
                    	throw new BTSLBaseException(this,methodName,"iccidkeymgmt.iccidbatchcorrectmapping.error.updationfailed",lineNumberArray);
                    }
				}//end of try
				catch (BTSLBaseException be)
				{
					throw be;
				} 
				catch (Exception e)
				{
					_logger.error(methodName,"INVALID RECORD : For Icc Id "+tempIccId+" Exception Message ="+e.getMessage());
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"","","","Exception:"+e.getMessage());
					throw new BTSLBaseException(this, methodName,"error.general.processing");
				}
			}

			if(recordsTotal==0)
			{
				errstr=errstr+"master.iccmsisdnupload.error.zerofilesize";
				throw new SQLException("Zero file size");
			}

			if(bufferReader!=null)
				bufferReader.close();
			if(fileReader!=null)
				fileReader.close();

			if(BTSLUtil.isNullString(errstr))
			{
					//Moving File after Processing
					fileMoved = moveFileToArchive(p_filename,p_file);
					if(fileMoved)
						processFile=true;
					else
						throw new BTSLBaseException(this,methodName,"iccidkeymgmt.iccidmsisdnuploadfile.error.filenomove");
			}
		}//end of try
		catch (BTSLBaseException be)
		{
			throw be;
		} 
		catch (SQLException sqe)
		{
			_logger.error(methodName,"SQLException "+sqe.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PosKeyDAO[writeIccMsisdnFileToDatabase]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this,methodName,"error.general.sql.processing");
		}
		catch (Exception e)
		{
			_logger.error("writeIccMsisdnFileToDatabase","Exception "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PosKeyDAO[writeIccMsisdnFileToDatabase]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,"error.general.processing");
		}
		finally
		{
			if(_logger.isDebugEnabled())
				_logger.debug(methodName,"processed till record no:"+recordsTotal);
			//Write in LOGS
			if(!processFile)
				recordsTotal=0;
			if(_logger.isDebugEnabled())
				_logger.debug(methodName,"Processed="+p_file+" ,No of records="+recordsTotal+" ,Status="+processFile+" ,Message="+errstr);
			IccFileProcessLog.log(p_file,p_modifiedBy,p_locationCode," ",recordsTotal,processFile,errstr);
			//Destroying different objects
			try{if(bufferReader!=null)bufferReader.close();}catch(Exception e){}
			try{if(fileReader!=null)fileReader.close();}catch(Exception e){}
			_logger.debug(methodName,"Exiting updateMsisdn="+recordsTotal);
		}//end of finally
	return recordsTotal;
}
    
    /**
	* This method will move the processed file in seperate folder
	* @param p_fileName
	* @param p_file
	* @return boolean
	*/
	public static boolean moveFileToArchive(String p_fileName,String p_file)
	{
		final String methodName = "moveFileToArchive";
		if(_logger.isDebugEnabled())
			_logger.debug(methodName," Entered ");
		File fileRead = new File(p_fileName);
		File fileArchive = new File(""+Constants.getProperty("ICCArchiveFilePath"));
		if(!fileArchive.isDirectory())fileArchive.mkdirs();

		//fileArchive = new File(""+Constants.getProperty("ICCArchiveFilePath")+"/"+p_file);
		fileArchive = new File(""+Constants.getProperty("ICCArchiveFilePath")+p_file+"."+BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to make the new file name
		boolean flag = fileRead.renameTo(fileArchive);
		if(_logger.isDebugEnabled())
			_logger.debug(methodName," Exiting File Moved="+flag);
		return flag;
	}//end of moveFileToArchive


    public List<PosKeyVO> loadPosKey(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadPosKey";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered=" + METHOD_NAME);
        }
        List<PosKeyVO> posKeyVOList = new ArrayList<>();
        PreparedStatement pstmtSelect = null;
        ResultSet rst = null;
        PosKeyVO posKeyVO = null;
        StringBuilder qryBuf = new StringBuilder();
        qryBuf.append("SELECT pk.icc_id, pk.msisdn, pk.registered, pk.created_by, coalesce(to_char(pk.created_on,'dd/mm/yy HH24:MI:SS'),'') ");
        qryBuf.append("created_on,pk.decrypt_key, pk.new_icc_id, pk.modified_by, coalesce(to_char(pk.modified_on,'dd/mm/yy HH24:MI:SS'),'') ");
        qryBuf.append("modified_on, pk.sim_profile_id, us1.user_name created, us2.user_name modified,pk.network_code ");
        qryBuf.append("FROM pos_keys pk left outer join users us2 on pk.modified_by = us2.user_id, users us1 ");
        qryBuf.append("WHERE pk.created_by = us1.user_id");
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Query :: " + qryBuf);
            }
            pstmtSelect = p_con.prepareStatement(qryBuf.toString());
            rst = pstmtSelect.executeQuery();

            while (rst.next()) {
                posKeyVO = new PosKeyVO();
                posKeyVO.setIccId(rst.getString("icc_id"));
                posKeyVO.setMsisdn(rst.getString("msisdn"));

                if ("Y".equalsIgnoreCase(rst.getString("registered"))) {
                    posKeyVO.setRegistered(true);
                } else {
                    posKeyVO.setRegistered(false);
                }
                posKeyVO.setCreatedBy(rst.getString("created"));
                posKeyVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(rst.getString("created_on")));
                posKeyVO.setKey(rst.getString("decrypt_key"));
                posKeyVO.setNewIccId(rst.getString("new_icc_id"));
                posKeyVO.setModifiedBy(rst.getString("modified"));
                posKeyVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(rst.getString("modified_on")));
                posKeyVO.setSimProfile(rst.getString("sim_profile_id"));
                posKeyVO.setNetworkCode(rst.getString("network_code"));
                posKeyVOList.add(posKeyVO);
            }
        }// end of try
        catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException:" + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[loadPosKey]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception:" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PosKeyDAO[loadPosKey]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
        } finally {
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing Result set: " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _logger.error(METHOD_NAME, "  Exception Closing Prepared Stmt: " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting posKeyVO=" + posKeyVO);
            }
        }// end of finally
        return posKeyVOList;

    }

}// end of class