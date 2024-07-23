package com.selftopup.pretups.cardgroup.businesslogic;

/**
 * @(#)BonusBundleCache.java
 *                           Copyright(c) 2009, Comviva Technologies Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Vinay Kumar Singh July 30, 2009 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
// commented for DB2import oracle.jdbc.OraclePreparedStatement;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;

public class BonusBundleDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for loading Bonus bundles defined for the Card Group.
     * 
     * @return bonusBundleVOList ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadBonusBundles(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadBonusBundles", "Entered ");
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList bonusBundleVOList = null;
        BonusBundleDetailVO bonusBundleDetailVO = null;
        StringBuffer selectQueryBuff = null;
        try {
            selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("select bundle_id,bundle_name,bundle_code,bundle_type,status,res_in_status ");
            selectQueryBuff.append("from bonus_bundle_master where status='Y' order by bundle_id ");
            String selectQuery = selectQueryBuff.toString();
            _log.debug("loadBonusBundles", "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            bonusBundleVOList = new ArrayList();
            while (rs.next()) {
                bonusBundleDetailVO = new BonusBundleDetailVO();
                bonusBundleDetailVO.setBundleID(rs.getString("bundle_id"));
                bonusBundleDetailVO.setBundleName(rs.getString("bundle_name"));
                bonusBundleDetailVO.setBundleCode(rs.getString("bundle_code"));
                bonusBundleDetailVO.setBundleType(rs.getString("bundle_type"));
                bonusBundleDetailVO.setStatus(rs.getString("status"));
                bonusBundleDetailVO.setResINStatus(rs.getString("res_in_status"));
                bonusBundleVOList.add(bonusBundleDetailVO);
            }

            return bonusBundleVOList;
        } catch (SQLException sqle) {
            _log.error("loadBonusBundles", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadBonusBundles: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusBundles]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBonusBundles", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadBonusBundles", "Exception " + e.getMessage());
            _log.errorTrace("loadBonusBundles: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusBundles]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadBonusBundles", "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadBonusBundles", "Exited bonusBundleVOList=" + bonusBundleVOList);
        }// end of finally
    }

    /**
     * Method for inserting Card Group Bonus bundle Details.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupDetailsVO
     *            CardGroupDetailsVO
     * @exception BTSLBaseException
     */
    public void addBonusBundleDetails(Connection p_con, CardGroupDetailsVO p_cardGroupDetailsVO) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtInsert = null;
        PreparedStatement psmtInsert = null;
        BonusAccountDetailsVO bonusAccVO = null;
        ArrayList bonusAccList = null;
        if (_log.isDebugEnabled())
            _log.debug("addBonusBundleDetails", "Entered: p_cardGroupDetailsVO " + p_cardGroupDetailsVO);
        try {
            bonusAccList = (ArrayList) p_cardGroupDetailsVO.getBonusAccList();
            // Print the bonus account list.
            if (_log.isDebugEnabled())
                _log.debug("addBonusBundleDetails", "bonusAccList.size()=" + bonusAccList.size() + " and bonusAccList=" + bonusAccList);

            StringBuffer strBuff = new StringBuffer();
            strBuff.append("insert into CARD_GROUP_SUB_BON_ACC_DETAILS (card_group_set_id, card_group_id, version,");
            strBuff.append("bundle_id, type, validity, value, mult_factor) values (?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addBonusBundleDetails", "Query sqlInsert:" + insertQuery);

            // commented for DB2 psmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);

            psmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            int listSize = bonusAccList.size();
            for (int i = 0; i < listSize; i++) {
                bonusAccVO = (BonusAccountDetailsVO) bonusAccList.get(i);
                psmtInsert.setString(1, p_cardGroupDetailsVO.getCardGroupSetID());
                psmtInsert.setString(2, p_cardGroupDetailsVO.getCardGroupID());
                psmtInsert.setString(3, p_cardGroupDetailsVO.getVersion());
                psmtInsert.setInt(4, Integer.parseInt(bonusAccVO.getBundleID()));
                psmtInsert.setString(5, bonusAccVO.getType());
                psmtInsert.setInt(6, Integer.parseInt(bonusAccVO.getBonusValidity()));
                psmtInsert.setDouble(7, Double.parseDouble(bonusAccVO.getBonusValue()));
                psmtInsert.setDouble(8, Double.parseDouble(bonusAccVO.getMultFactor()));

                int count = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the insert
                if (count <= 0)
                    throw new BTSLBaseException(this, "addBonusBundleDetails", "error.general.sql.processing");
            }
        } // end of try
        catch (SQLException sqle) {
            _log.error("addCardGroupSet", "SQLException: " + sqle.getMessage());
            _log.errorTrace("addCardGroupSet: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[addBonusBundleDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addBonusBundleDetails", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("addBonusBundleDetails", "Exception: " + e.getMessage());
            _log.errorTrace("addCardGroupSet: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[addBonusBundleDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addBonusBundleDetails", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addBonusBundleDetails", "Exiting: ");
            }
        } // end of finally
    }

    /**
     * This method loads the Card group details for bonus bundles on the basis
     * of Card_Group_Set_Id,version,
     * and card_group_code.
     * 
     * @param p_con
     *            Connection
     * @param p_cardGroupSetId
     *            String
     * @param p_version
     *            String
     * @param p_cardGroupID
     *            String
     * @return cardGroupDetailList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadBonusAccDetailsListByID(Connection p_con, String p_cardGroupSetID, String p_version, String p_cardGroupID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadBonusAccDetailsListByID", "Entered p_cardGroupSetID=" + p_cardGroupSetID + " p_version=" + p_version + " p_cardGroupID=" + p_cardGroupID);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList bonusAccDetailsList = new ArrayList();
        BonusAccountDetailsVO bonAccDetailsVO = null;
        StringBuffer selQry = null;
        try {
            selQry = new StringBuffer();
            selQry.append(" select cbad.card_group_set_id, cbad.version, cbad.card_group_id, cbad.bundle_id,");
            selQry.append(" cbad.type,cbad.value,cbad.validity, cbad.mult_factor,");
            selQry.append(" bbm.bundle_name,bbm.bundle_type,bbm.res_in_status,bbm.bundle_code");
            selQry.append(" from card_group_sub_bon_acc_details cbad, bonus_bundle_master bbm");
            selQry.append(" where cbad.card_group_set_id=? and cbad.version=? and cbad.card_group_id=?");
            selQry.append(" and bbm.bundle_id=cbad.bundle_id order by cbad.bundle_id ");
            // selQry.append(" order by seq_no");

            String selectQuery = selQry.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadBonusAccDetailsListByID", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_cardGroupSetID);
            pstmtSelect.setString(2, p_version);
            pstmtSelect.setString(3, p_cardGroupID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                bonAccDetailsVO = new BonusAccountDetailsVO();
                bonAccDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                bonAccDetailsVO.setVersion(rs.getString("version"));
                bonAccDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                bonAccDetailsVO.setBundleID(String.valueOf(rs.getInt("bundle_id")));
                bonAccDetailsVO.setType(rs.getString("type"));
                bonAccDetailsVO.setBonusValue(String.valueOf(rs.getDouble("value")));
                bonAccDetailsVO.setBonusValidity(String.valueOf(rs.getInt("validity")));
                bonAccDetailsVO.setMultFactor(String.valueOf(rs.getDouble("mult_factor")));
                bonAccDetailsVO.setBonusName(rs.getString("bundle_name"));
                bonAccDetailsVO.setBundleType(rs.getString("bundle_type"));
                bonAccDetailsVO.setRestrictedOnIN(rs.getString("res_in_status"));
                bonAccDetailsVO.setBonusCode(rs.getString("bundle_code"));

                bonusAccDetailsList.add(bonAccDetailsVO);
            }
            return bonusAccDetailsList;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadBonusAccDetailsListByID", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadBonusAccDetailsListByID: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusAccDetailsListByID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetailsListByID", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadBonusAccDetailsListByID", "Exception " + e.getMessage());
            _log.errorTrace("loadBonusAccDetailsListByID: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusAccDetailsListByID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadBonusAccDetailsListByID", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadBonusAccDetailsListByID", "Exiting BonusAccDetailsList Size:" + bonusAccDetailsList.size());
        }// end of finally
    }

    /**
     * This method loads the Bonus Account details for the slab in which the
     * requested value lies
     * 
     * @param p_con
     *            Connection
     * @param p_cardGroupDetailsVO
     *            CardGroupDetailsVO
     * @return bonusAccDetailsList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadBonusAccDetails(Connection p_con, CardGroupDetailsVO p_cardGroupDetailsVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadBonusAccDetails", "Entered p_cardGroupDetailsVO=" + p_cardGroupDetailsVO);
        ArrayList bonusAccDetailsList = null;
        BonusAccountDetailsVO bonusAccDetailVO = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer qry = new StringBuffer();
        try {
            bonusAccDetailsList = new ArrayList();

            qry = new StringBuffer();
            qry.append(" select cad.card_group_set_id,cad.version,cad.card_group_id,cad.bundle_id,cad.type,cad.validity,");
            qry.append(" cad.value,cad.mult_factor,bbm.bundle_name,bbm.bundle_type,bbm.res_in_status,bbm.bundle_code");
            qry.append(" from card_group_sub_bon_acc_details cad, bonus_bundle_master bbm");
            qry.append(" where cad.bundle_id=bbm.bundle_id");
            qry.append(" and card_group_set_id=? and version=? and card_group_id=? order by bbm.bundle_id ");

            String selectQuery = qry.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupDetails", "select query:" + selectQuery);

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_cardGroupDetailsVO.getCardGroupSetID());
            pstmtSelect.setString(2, p_cardGroupDetailsVO.getVersion());
            pstmtSelect.setString(3, p_cardGroupDetailsVO.getCardGroupID());
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                bonusAccDetailVO = new BonusAccountDetailsVO();

                bonusAccDetailVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                bonusAccDetailVO.setVersion(rs.getString("version"));
                bonusAccDetailVO.setCardGroupID(rs.getString("card_group_id"));
                bonusAccDetailVO.setBundleID(String.valueOf(rs.getInt("bundle_id")));
                bonusAccDetailVO.setType(rs.getString("type"));
                bonusAccDetailVO.setBonusValidity(String.valueOf(rs.getString("validity")));
                bonusAccDetailVO.setBonusValue(String.valueOf(rs.getDouble("value")));
                bonusAccDetailVO.setMultFactor(String.valueOf(rs.getDouble("mult_factor")));
                bonusAccDetailVO.setBonusName(rs.getString("bundle_name"));
                bonusAccDetailVO.setBundleType(rs.getString("bundle_type"));
                bonusAccDetailVO.setRestrictedOnIN(rs.getString("res_in_status"));
                bonusAccDetailVO.setBonusCode(rs.getString("bundle_code"));

                bonusAccDetailsList.add(bonusAccDetailVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadBonusAccDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadBonusAccDetails: Exception print stack trace:", sqle);
            throw new BTSLBaseException("BonusBundleDAO", "loadCardGroupDetails", SelfTopUpErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadBonusAccDetails", "Exception " + e.getMessage());
            _log.errorTrace("loadBonusAccDetails: Exception print stack trace:", e);
            throw new BTSLBaseException("BonusBundleDAO", "loadCardGroupDetails", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadBonusAccDetails", "Exiting bonusAccDetailsList.size():" + bonusAccDetailsList.size());
        }// end of finally
        return bonusAccDetailsList;
    }

    /**
     * Method for returning active bonus(those which are allowed on IN) in the
     * system.
     * 
     * @return activeBonusCount int
     */
    public int getActiveBonusCount(Connection p_con) {
        if (_log.isDebugEnabled())
            _log.debug("getActiveBonusCount", "Entered ");
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        int activeBonusCount = 0;
        StringBuffer countQueryBuff = null;
        try {
            countQueryBuff = new StringBuffer();
            countQueryBuff.append(" select count(1) count from bonus_bundle_master ");
            countQueryBuff.append(" where status='Y' and res_in_status='Y' ");
            String selectQuery = countQueryBuff.toString();
            _log.debug("getActiveBonusCount", "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                activeBonusCount = Integer.parseInt(rs.getString("count"));
            }
        } catch (Exception e) {
            _log.error("getActiveBonusCount", "Exception " + e.getMessage());
            _log.errorTrace("getActiveBonusCount: Exception print stack trace:", e);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("getActiveBonusCount", "Exited activeBonusCount=" + activeBonusCount);
        }// end of finally
        return activeBonusCount;
    }

    /**
     * Method for loading Bonus bundles defined.
     * 
     * @return bonusBundleVOList ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadBonusBundleList(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadBonusBundleList", "Entered ");
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList bonusBundleVOList = null;
        BonusBundleDetailVO bonusBundleDetailVO = null;
        StringBuffer selectQueryBuff = null;
        try {
            selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("select bundle_id,bundle_name,bundle_code ");
            selectQueryBuff.append("from bonus_bundle_master order by bundle_id ");
            String selectQuery = selectQueryBuff.toString();
            _log.debug("loadBonusBundleList", "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            bonusBundleVOList = new ArrayList();
            while (rs.next()) {
                bonusBundleDetailVO = new BonusBundleDetailVO();
                bonusBundleDetailVO.setBundleID(rs.getString("bundle_id"));
                bonusBundleDetailVO.setBundleName(rs.getString("bundle_name"));
                bonusBundleDetailVO.setBundleCode(rs.getString("bundle_code"));
                bonusBundleVOList.add(bonusBundleDetailVO);
            }

            return bonusBundleVOList;
        } catch (SQLException sqle) {
            _log.error("loadBonusBundleList", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadBonusBundleList: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusBundleList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBonusBundleList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadBonusBundleList", "Exception " + e.getMessage());
            _log.errorTrace("loadBonusBundleList: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusBundleList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadBonusBundleList", "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadBonusBundleList", "Exited bonusBundleVOList=" + bonusBundleVOList);
        }// end of finally
    }

    /**
     * Method for inserting Card Group Bonus bundle Details.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupDetailsVO
     *            CardGroupDetailsVO
     * @exception BTSLBaseException
     */
    public int deletePreviousBonus(Connection p_con, String cardGroupSetId, String version) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deletePreviousBonus", "Entered: cardGroupSetId " + cardGroupSetId);
        // commented for DB2 OraclePreparedStatement psmtInsert = null;
        PreparedStatement psmtDelete = null;
        int count = 0;
        // Print the bonus account list.
        StringBuffer deleteBonusqry = new StringBuffer("DELETE FROM CARD_GROUP_SUB_BON_ACC_DETAILS ");
        deleteBonusqry.append("WHERE card_group_set_id = ? and version = ?");
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("deletePreviousBonus", "Query deleteBonusDetQuery:" + deleteBonusqry.toString());
            }
            psmtDelete = p_con.prepareStatement(deleteBonusqry.toString());
            psmtDelete.setString(1, cardGroupSetId);
            psmtDelete.setString(2, version);
            count = psmtDelete.executeUpdate();
        } catch (SQLException e) {
            _log.error("deletePreviousBonus", "Exception " + e.getMessage());
            _log.errorTrace("deletePreviousBonus: Exception print stack trace:", e);
            count = 0;
        } finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("deletePreviousBonus", "Exiting: ");
            }
            return count;
        } // end of finally

    }
}
