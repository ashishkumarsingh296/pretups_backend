package com.btsl.pretups.cardgroup.businesslogic;

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

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.SqlParameterEncoder;

/**
 * @author 
 *
 */
public class BonusBundleDAO {
    private static final Log log = LogFactory.getLog(BonusBundleDAO.class.getName());

    /**
     * Method for loading Bonus bundles defined for the Card Group.
     * 
     * @return bonusBundleVOList ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadBonusBundles(Connection pCon) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadBonusBundles", "Entered ");
        }
        final String methodName = "loadBonusBundles";
         
        ArrayList bonusBundleVOList = null;
        BonusBundleDetailVO bonusBundleDetailVO = null;
        StringBuilder selectQueryBuff = null;
        try {
            selectQueryBuff = new StringBuilder();
            selectQueryBuff.append("select bundle_id,bundle_name,bundle_code,bundle_type,status,res_in_status ");
            selectQueryBuff.append("from bonus_bundle_master where status='Y' order by bundle_id ");
            final String selectQuery = selectQueryBuff.toString();
            log.debug("loadBonusBundles", "Query=" + selectQuery);
           try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
           {
            bonusBundleVOList = new ArrayList();
            while (rs.next()) {
                bonusBundleDetailVO = new BonusBundleDetailVO();
                bonusBundleDetailVO.setBundleID(SqlParameterEncoder.encodeParams(rs.getString("bundle_id")));
                bonusBundleDetailVO.setBundleName(SqlParameterEncoder.encodeParams(rs.getString("bundle_name")));
                bonusBundleDetailVO.setBundleCode(SqlParameterEncoder.encodeParams(rs.getString("bundle_code")));
                bonusBundleDetailVO.setBundleType(SqlParameterEncoder.encodeParams(rs.getString("bundle_type")));
                bonusBundleDetailVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                bonusBundleDetailVO.setResINStatus(SqlParameterEncoder.encodeParams(rs.getString("res_in_status")));
                bonusBundleVOList.add(bonusBundleDetailVO);
            }

            return bonusBundleVOList;
        } 
        }catch (SQLException sqle) {
            log.error("loadBonusBundles", "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusBundles]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBonusBundles", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error("loadBonusBundles", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusBundles]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadBonusBundles", "error.general.sql.processing");
        }// end of catch
        finally {
            
        	if (log.isDebugEnabled()) {
                log.debug("loadBonusBundles", "Exited bonusBundleVOList=" + bonusBundleVOList);
            }
        }// end of finally
    }

    /**
     * Method for inserting Card Group Bonus bundle Details.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pCardGroupDetailsVO
     *            CardGroupDetailsVO
     * @exception BTSLBaseException
     */
    public void addBonusBundleDetails(Connection pCon, CardGroupDetailsVO pCardGroupDetailsVO) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtInsert = null;
        final String methodName = "addBonusBundleDetails";
        
        BonusAccountDetailsVO bonusAccVO = null;
        ArrayList bonusAccList = null;
        if (log.isDebugEnabled()) {
            log.debug("addBonusBundleDetails", "Entered: pCardGroupDetailsVO " + pCardGroupDetailsVO);
        }
        try {
            bonusAccList = (ArrayList) pCardGroupDetailsVO.getBonusAccList();
            // Print the bonus account list.
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("bonusAccList.size()=");
            	msg.append(bonusAccList.size());
            	msg.append(" and bonusAccList=");
            	msg.append(bonusAccList);
            	
            	String message=msg.toString();
                log.debug("addBonusBundleDetails", message);
            }

            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("insert into CARD_GROUP_SUB_BON_ACC_DETAILS (card_group_set_id, card_group_id, version,");
            strBuff.append("bundle_id, type, validity, value, mult_factor) values (?,?,?,?,?,?,?,?)");
            final String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("addBonusBundleDetails", "Query sqlInsert:" + insertQuery);
            }

            // commented for DB2 psmtInsert =


            try(PreparedStatement psmtInsert =  pCon.prepareStatement(insertQuery);)
            		{
            final int listSize = bonusAccList.size();
            for (int i = 0; i < listSize; i++) {
                bonusAccVO = (BonusAccountDetailsVO) bonusAccList.get(i);
                psmtInsert.setString(1, pCardGroupDetailsVO.getCardGroupSetID());
                psmtInsert.setString(2, pCardGroupDetailsVO.getCardGroupID());
                psmtInsert.setString(3, pCardGroupDetailsVO.getVersion());
                psmtInsert.setInt(4, Integer.parseInt(bonusAccVO.getBundleID()));
                psmtInsert.setString(5, bonusAccVO.getType());
                psmtInsert.setInt(6, Integer.parseInt(bonusAccVO.getBonusValidity()));
                // psmtInsert.setDouble(7,

                psmtInsert.setDouble(7, Double.parseDouble(bonusAccVO.getBonusValue()));
                psmtInsert.setDouble(8, Double.parseDouble(bonusAccVO.getMultFactor()));

                final int count = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the insert
                if (count <= 0) {
                    throw new BTSLBaseException(this, "addBonusBundleDetails", "error.general.sql.processing");
                }
            }
        }
        }// end of try
        catch (SQLException sqle) {
            log.error("addCardGroupSet", "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[addBonusBundleDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addBonusBundleDetails", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error("addBonusBundleDetails", "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[addBonusBundleDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addBonusBundleDetails", "error.general.processing");
        } // end of catch
        finally {
            
            if (log.isDebugEnabled()) {
                log.debug("addBonusBundleDetails", "Exiting: ");
            }
        } // end of finally
    }

    /**
     * This method loads the Card group details for bonus bundles on the basis
     * of Card_Group_Set_Id,version,
     * and card_group_code.
     * 
     * @param pCon
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
    public ArrayList loadBonusAccDetailsListByID(Connection pCon, String p_cardGroupSetID, String p_version, String p_cardGroupID) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_cardGroupSetID=");
        	msg.append(p_cardGroupSetID);
        	msg.append(" p_version=");
        	msg.append(p_version);       
        	msg.append(" p_cardGroupID=");
        	msg.append(p_cardGroupID);
        	
        	String message=msg.toString();
            log.debug("loadBonusAccDetailsListByID", message);
        }
        
        final String methodName = "loadBonusAccDetailsListByID";
       
        final ArrayList bonusAccDetailsList = new ArrayList();
        BonusAccountDetailsVO bonAccDetailsVO = null;
        StringBuilder selQry = null;
        try {
            selQry = new StringBuilder();
            selQry.append(" select cbad.card_group_set_id, cbad.version, cbad.card_group_id, cbad.bundle_id,");
            selQry.append(" cbad.type,cbad.value,cbad.validity, cbad.mult_factor,");
            selQry.append(" bbm.bundle_name,bbm.bundle_type,bbm.res_in_status,bbm.bundle_code");
            selQry.append(" from card_group_sub_bon_acc_details cbad, bonus_bundle_master bbm");
            selQry.append(" where cbad.card_group_set_id=? and cbad.version=? and cbad.card_group_id=?");
            selQry.append(" and bbm.bundle_id=cbad.bundle_id order by cbad.bundle_id ");


            final String selectQuery = selQry.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadBonusAccDetailsListByID", "select query:" + selectQuery);
            }
           try(PreparedStatement  pstmtSelect = pCon.prepareStatement(selectQuery);)
           {
            pstmtSelect.setString(1, p_cardGroupSetID);
            pstmtSelect.setString(2, p_version);
            pstmtSelect.setString(3, p_cardGroupID);
            try( ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                bonAccDetailsVO = new BonusAccountDetailsVO();
                bonAccDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                bonAccDetailsVO.setVersion(rs.getString("version"));
                bonAccDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                bonAccDetailsVO.setBundleID(String.valueOf(rs.getInt("bundle_id")));
                bonAccDetailsVO.setType(rs.getString("type"));

                // added by harsh
                bonAccDetailsVO.setBonusValue(rs.getString("value"));
                bonAccDetailsVO.setBonusValidity(String.valueOf(rs.getInt("validity")));
                bonAccDetailsVO.setMultFactor(String.valueOf(rs.getDouble("mult_factor")));
                bonAccDetailsVO.setBonusName(rs.getString("bundle_name"));
                bonAccDetailsVO.setBundleType(rs.getString("bundle_type"));
                bonAccDetailsVO.setRestrictedOnIN(rs.getString("res_in_status"));
                bonAccDetailsVO.setBonusCode(rs.getString("bundle_code"));

                bonusAccDetailsList.add(bonAccDetailsVO);
            }
            return bonusAccDetailsList;
        }
           }
        }// end of try
        catch (SQLException sqle) {
            log.error("loadBonusAccDetailsListByID", "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusAccDetailsListByID]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetailsListByID", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            log.error("loadBonusAccDetailsListByID", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusAccDetailsListByID]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadBonusAccDetailsListByID", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
           
            if (log.isDebugEnabled()) {
                log.debug("loadBonusAccDetailsListByID", "Exiting BonusAccDetailsList Size:" + bonusAccDetailsList.size());
            }
        }// end of finally
    }

    /**
     * This method loads the Bonus Account details for the slab in which the
     * requested value lies
     * 
     * @param pCon
     *            Connection
     * @param pstmtSelect1 
     * @param pCardGroupDetailsVO
     *            CardGroupDetailsVO
     * @return bonusAccDetailsList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadBonusAccDetails(Connection pCon, PreparedStatement pstmtSelect, CardGroupDetailsVO pCardGroupDetailsVO) throws BTSLBaseException {
        final String methodName = "loadBonusAccDetails";
        int size=0;
        ArrayList bonusAccDetailsList = null;
        BonusAccountDetailsVO bonusAccDetailVO = null;
        
        
        
        try {
            bonusAccDetailsList = new ArrayList();

            pstmtSelect.clearParameters();
            pstmtSelect.setString(1, pCardGroupDetailsVO.getCardGroupSetID());
            pstmtSelect.setString(2, pCardGroupDetailsVO.getVersion());
            pstmtSelect.setString(3, pCardGroupDetailsVO.getCardGroupID());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                bonusAccDetailVO = new BonusAccountDetailsVO();

                bonusAccDetailVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                bonusAccDetailVO.setVersion(rs.getString("version"));
                bonusAccDetailVO.setCardGroupID(rs.getString("card_group_id"));
                bonusAccDetailVO.setBundleID(String.valueOf(rs.getInt("bundle_id")));
                bonusAccDetailVO.setType(rs.getString("type"));
                bonusAccDetailVO.setBonusValidity(String.valueOf(rs.getString("validity")));

                // added by harsh
                bonusAccDetailVO.setBonusValue(rs.getString("value"));
                bonusAccDetailVO.setMultFactor(String.valueOf(rs.getDouble("mult_factor")));
                bonusAccDetailVO.setBonusName(rs.getString("bundle_name"));
                bonusAccDetailVO.setBundleType(rs.getString("bundle_type"));
                bonusAccDetailVO.setRestrictedOnIN(rs.getString("res_in_status"));
                bonusAccDetailVO.setBonusCode(rs.getString("bundle_code"));

                bonusAccDetailsList.add(bonusAccDetailVO);
            }
               if(!bonusAccDetailsList.isEmpty())
               {
            	   size=bonusAccDetailsList.size();
               }
        }
        }// end of try
        catch (SQLException sqle) {
            log.error("loadBonusAccDetails", "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            throw new BTSLBaseException("BonusBundleDAO", "loadCardGroupDetails", PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            log.error("loadBonusAccDetails", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException("BonusBundleDAO", "loadCardGroupDetails", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	log.debug(methodName, "Exiting..");
        }// end of finally
        return bonusAccDetailsList;
    }

    /**
     * Method for returning active bonus(those which are allowed on IN) in the
     * system.
     * 
     * @return activeBonusCount int
     */
    /**
     * @param pCon
     * @return
     */
    /**
     * @param pCon
     * @return
     */
    public int getActiveBonusCount(Connection pCon) {
        if (log.isDebugEnabled()) {
            log.debug("getActiveBonusCount", "Entered ");
        }
        final String methodName = "getActiveBonusCount";
         
        
        int activeBonusCount = 0;
        StringBuilder countQueryBuff = null;
        try {
            countQueryBuff = new StringBuilder();
            countQueryBuff.append(" select count(1) count from bonus_bundle_master ");
            countQueryBuff.append(" where status='Y' and res_in_status='Y' ");
            final String selectQuery = countQueryBuff.toString();
            log.debug("getActiveBonusCount", "Query=" + selectQuery);
            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);ResultSet  rs = pstmtSelect.executeQuery();)
            {
           
            while (rs.next()) {
                activeBonusCount = Integer.parseInt(rs.getString("count"));
            }
        }
        }catch (Exception e) {
            log.error("getActiveBonusCount", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
        }// end of catch
        finally {
           
            if (log.isDebugEnabled()) {
                log.debug("getActiveBonusCount", "Exited activeBonusCount=" + activeBonusCount);
            }
        }// end of finally
        return activeBonusCount;
    }

    /**
     * Method for loading Bonus bundles defined.
     * 
     * @return bonusBundleVOList ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadBonusBundleList(Connection pCon) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadBonusBundleList", "Entered ");
        }
        final String methodName = "loadBonusBundleList";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList bonusBundleVOList = null;
        BonusBundleDetailVO bonusBundleDetailVO = null;
        StringBuilder selectQueryBuff = null;
        try {
            selectQueryBuff = new StringBuilder();
            selectQueryBuff.append("select bundle_id,bundle_name,bundle_code ");
            selectQueryBuff.append("from bonus_bundle_master order by bundle_id ");
            final String selectQuery = selectQueryBuff.toString();
            log.debug("loadBonusBundleList", "Query=" + selectQuery);
            pstmtSelect = pCon.prepareStatement(selectQuery);
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
            log.error("loadBonusBundleList", "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusBundleList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBonusBundleList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error("loadBonusBundleList", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BonusBundleDAO[loadBonusBundleList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadBonusBundleList", "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug("loadBonusBundleList", "Exited bonusBundleVOList=" + bonusBundleVOList);
            }
        }// end of finally
    }

    /**
     * Method for inserting Card Group Bonus bundle Details.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pCardGroupDetailsVO
     *            CardGroupDetailsVO
     * @exception BTSLBaseException
     */
    /**
     * @param pCon
     * @param cardGroupSetId
     * @param version
     * @return count
     * @throws BTSLBaseException
     */
    /**
     * @param pCon
     * @param cardGroupSetId
     * @param version
     * @return count
     * @throws BTSLBaseException
     */
    /**
     * @param pCon
     * @param cardGroupSetId
     * @param version
     * @return count
     * @throws BTSLBaseException
     */
    public int deletePreviousBonus(Connection pCon, String cardGroupSetId, String version) throws BTSLBaseException {
    	final String methodName = "deletePreviousBonus";
        if (log.isDebugEnabled()) {
            log.debug("deletePreviousBonus", "Entered: cardGroupSetId " + cardGroupSetId);
        }

        
        
        int count = 0;
        // Print the bonus account list.
        final StringBuilder deleteBonusqry = new StringBuilder("DELETE FROM CARD_GROUP_SUB_BON_ACC_DETAILS ");
        deleteBonusqry.append("WHERE card_group_set_id = ? and version = ?");
        try (PreparedStatement psmtDelete = pCon.prepareStatement(deleteBonusqry.toString());){
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query deleteBonusDetQuery:" + deleteBonusqry.toString());
            }
            
            psmtDelete.setString(1, cardGroupSetId);
            psmtDelete.setString(2, version);
            count = psmtDelete.executeUpdate();
        } catch (SQLException e) {

            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            count = 0;
        } finally {
         
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        } // end of finally
        return count;

    }
}
