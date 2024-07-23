package com.btsl.pretups.iccidkeymgmt.businesslogic;

/**
 * @(#)ICCIDDeleteDAO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 *                         Data Access Object for Bulk ICCID Delete
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Ashish Kumar Srivastav 11/06/2007 Initial Creation
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.btsl.util.BTSLUtil;

/**
 * 
 */
public class ICCIDDeleteDAO {
    /**
     * Field _logger.
     */
    private Log _logger = LogFactory.getLog(ICCIDDeleteDAO.class.getName());

    /**
     * Date : Jun 13, 2007
     * Discription :This method will return the ArrayList after validating the
     * ICCIDs from pos_keys table,
     * Method : validateICCIDList
     * 
     * @param p_con
     * @param p_iccidList
     * @throws BTSLBaseException
     * @return ArrayList
     * @author Ashish S
     */
    public ArrayList validateICCIDList(Connection p_con, ArrayList p_iccidList) throws BTSLBaseException {
        final String METHOD_NAME = "validateICCIDList";
        if (_logger.isDebugEnabled()) {
            _logger.debug("validateICCIDList", " Entered p_iccidList.size()=" + p_iccidList.size());
        }

        String msdnQuery = "SELECT icc_id, msisdn FROM pos_keys WHERE icc_id=?";
        String userQuery = "SELECT U.user_name, UP.msisdn FROM users U, user_phones UP WHERE U.user_id = UP.user_id AND U.status NOT IN ('N','C') AND UP.msisdn=?";
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelectUser = null;
        ResultSet rsUser = null;
        ResultSet rst = null;
        ArrayList errorList = new ArrayList();
        ListValueVO listValueVO = null;
        String iccid = null;
        String msisdn = null;
        try {
            // Get Preapared Statement for associated msisdn
            pstmtSelect = p_con.prepareStatement(msdnQuery);
            // Get Preapared Statement for associated channel user
            pstmtSelectUser = p_con.prepareStatement(userQuery);
            for (int i = 0, j = p_iccidList.size(); i < j; i++) {
                iccid = (String) p_iccidList.get(i);
                pstmtSelect.setString(1, iccid);
                // execute query
                rst = pstmtSelect.executeQuery();
                if (rst.next()) {
                    msisdn = rst.getString("msisdn");
                    if (BTSLUtil.isNullString(msisdn)) {
                        errorList.add(new ListValueVO(PretupsI.ICCID_DELETEABLE, iccid));
                    } else {
                        pstmtSelectUser.setString(1, msisdn);
                        rsUser = pstmtSelectUser.executeQuery();
                        if (rsUser.next()) {
                            listValueVO = new ListValueVO(PretupsI.ICCID_USER_ASSOCIATED, iccid);
                            listValueVO.setOtherInfo(rsUser.getString("user_name"));
                            listValueVO.setOtherInfo2(rsUser.getString("msisdn"));
                            errorList.add(listValueVO);
                        } else {
                            listValueVO = new ListValueVO(PretupsI.ICCID_MSISDN_ASSOCIATED, iccid);
                            listValueVO.setOtherInfo2(msisdn);
                            errorList.add(listValueVO);
                        }
                        pstmtSelectUser.clearParameters();
                    }
                } else {
                    errorList.add(new ListValueVO(PretupsI.ICCID_NOT_EXISTING, iccid));// end
                }
                // if
                pstmtSelect.clearParameters();
            }// end loop
        }// end try
        catch (SQLException sqle) {
            _logger.error("validateICCIDList", "SQLException " + sqle.getMessage());
            _logger.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ICCIDDeleteDAO[validateICCIDList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "validateICCIDList", "error.general.sql.processing");

        }// end of catch
        catch (Exception e) {
            _logger.error("validateICCIDList", "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ICCIDDeleteDAO[validateICCIDList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validateICCIDList", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rsUser != null) {
                    rsUser.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (rst != null) {
                    rst.close();
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
            try {
                if (pstmtSelectUser != null) {
                    pstmtSelectUser.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("validateICCIDList() ", " Exiting errorList.size=:" + errorList.size());
            }
        }// end of finally
        return errorList;
    }

    /**
     * Date : Jun 13, 2007
     * Discription :This method will return void and delete ICCIDs from the
     * pos_keys table,
     * Method : deleteICCID
     * 
     * @param p_con
     * @param p_iccidList
     * @throws BTSLBaseException
     * @return void
     * @author Ashish S
     */
    public void deleteICCID(Connection p_con, ArrayList p_iccidList) throws BTSLBaseException {
        final String METHOD_NAME = "deleteICCID";
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteICCID", " Entered p_iccidList.size()=" + p_iccidList.size());
        }
        PreparedStatement pstmtDeleteIccid = null;
        String iccidDeleteQuery = "DELETE FROM pos_keys WHERE icc_id =?";
        String iccId = null;
        try {
            // prepare the statement
            pstmtDeleteIccid = p_con.prepareStatement(iccidDeleteQuery);
            for (int i = 0, j = p_iccidList.size(); i < j; i++) {
                iccId = (String) p_iccidList.get(i);
                pstmtDeleteIccid.setString(1, iccId);
                // execute query
                pstmtDeleteIccid.executeUpdate();
                pstmtDeleteIccid.clearParameters();
            }
        }// end of try
        catch (SQLException sqle) {
            _logger.error("deleteICCID", "SQLException " + sqle.getMessage());
            _logger.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ICCIDDeleteDAO[deleteICCID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteICCID", "error.general.sql.processing");

        }// end of catch
        catch (Exception e) {
            _logger.error("deleteICCID", "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ICCIDDeleteDAO[deleteICCID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteICCID", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtDeleteIccid != null) {
                    pstmtDeleteIccid.close();
                }
            } catch (Exception ex) {
                _logger.error(" validateICCIDList()", " Exception Closing pstmtDeleteIccid : " + ex.getMessage());
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("deleteICCID() ", " Exiting");
            }
        }// end of finally
    }
}
