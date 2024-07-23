package com.btsl.pretups.cardgroup.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @author 
 *
 */
public class CardGroupSetVersionDAO {
    private static final Log LOG = LogFactory.getLog(CardGroupSetVersionDAO.class.getName());

    /**
     * Method for checking CardGroup Set is already exist with the same
     * applicable date of the same set id.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param pApplicableDate
     *            Date
     * @param pSetId
     *            String
     * @param pVersion
     *            String
     * 
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isCardGroupAlreadyExist(Connection pCon, Date pApplicableDate, String pSetId, String pVersion) throws BTSLBaseException {
        final String methodName = "isCardGroupAlreadyExist";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: pApplicableDate=");
        	loggerValue.append(pApplicableDate);
    		loggerValue.append(" pSetId=");
        	loggerValue.append(pSetId);
            LOG.debug(methodName,loggerValue);
        }

        
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT card_group_set_id FROM card_group_set_versions ");
        strBuff.append("WHERE applicable_from = ? AND card_group_set_id = ? AND version != ?");

        final String sqlSelect = strBuff.toString();

        if (LOG.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
           

            pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(pApplicableDate));
            pstmt.setString(2, pSetId);
            pstmt.setString(3, pVersion);

            try(ResultSet rs = pstmt.executeQuery();)
            {

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } 
        }catch (SQLException sqe) {
    		loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe);
    		String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupAlreadyExist]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
    		String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupAlreadyExist]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
          
            if (LOG.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Exiting: existFlag=");
            	loggerValue.append(existFlag);
                LOG.debug(methodName,loggerValue);
            }
        }
    }

    /**
     * Method for Updating Card Group Set Version.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pCardGroupSetVersionVO
     *            CardGroupSetVersionVO
     * 
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int updateCardGroupSetVersion(Connection pCon, CardGroupSetVersionVO pCardGroupSetVersionVO) throws BTSLBaseException {

        
        int updateCount = 0;
        StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "updateCardGroupSetVersion";
        if (LOG.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: pCardGroupSetVersionVO=");
        	loggerValue.append(pCardGroupSetVersionVO);
            LOG.debug(methodName,loggerValue);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("UPDATE card_group_set_versions SET modified_on = ?, modified_by = ? ");
            strBuff.append("WHERE card_group_set_id = ? and version = ?");

            final String insertQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                LOG.debug(methodName,loggerValue);
            }

            try(PreparedStatement psmtUpdate = pCon.prepareStatement(insertQuery);)
            {
            psmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(pCardGroupSetVersionVO.getModifiedOn()));
            psmtUpdate.setString(2, pCardGroupSetVersionVO.getModifiedBy());
            psmtUpdate.setString(3, pCardGroupSetVersionVO.getCardGroupSetID());
            psmtUpdate.setString(4, pCardGroupSetVersionVO.getVersion());

            updateCount = psmtUpdate.executeUpdate();

        } 
        }// end of try
        catch (SQLException sqle) {
    		loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSetVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append( e.getMessage());
    		String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSetVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	

            if (LOG.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount=");
            	loggerValue.append(updateCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * Method for Deleting Card Group version
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pCardGroupSetId
     *            String
     * @param pVersion
     *            string
     * 
     * @return deleteCount int
     * @exception BTSLBaseException
     */
    public int deleteVersion(Connection pCon, String pCardGroupSetId, String pVersion) throws BTSLBaseException {
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;
        final String methodName = "deleteVersion";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered: cardGroupSetId=");
        	loggerValue.append(pCardGroupSetId);
    		loggerValue.append(" pVersion= ");
        	loggerValue.append(pVersion);
            LOG.debug(methodName,loggerValue);
        }
        try {
            final int deleteDetails = deleteCardGroupDetails(pCon, pCardGroupSetId, pVersion);

            final BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
            final int deleteBonusCount = bonusBundleDAO.deletePreviousBonus(pCon, pCardGroupSetId, pVersion);

            if (deleteDetails > 0 && deleteBonusCount > 0) {
                final StringBuilder strBuff = new StringBuilder();
                strBuff.append("DELETE FROM card_group_set_versions ");
                strBuff.append("WHERE card_group_set_id = ? and version = ? ");
                final String deleteQuery = strBuff.toString();
                if (LOG.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append("Query sqlDelete:");
                	loggerValue.append(deleteQuery);
                    LOG.debug(methodName,loggerValue);
                }
                psmtDelete = pCon.prepareStatement(deleteQuery);
                psmtDelete.setString(1, pCardGroupSetId);
                psmtDelete.setString(2, pVersion);
                deleteCount = psmtDelete.executeUpdate();
            }
        } // end of try
        catch (SQLException sqle) {
    		loggerValue.setLength(0);
        	loggerValue.append("SQLException: ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            LOG.error("deleteCardGroupDetails",loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteVersion]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	 try{
        	        if (psmtDelete!= null){
        	        	psmtDelete.close();
        	        }
        	      }
        	      catch (SQLException e){
        	    	  LOG.error("An error occurred closing statement.", e);
        	      }
            if (LOG.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount=");
            	loggerValue.append(deleteCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally
        return deleteCount;
    }

    /**
     * Method for Deleting Card Group Details
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pCardGroupSetId
     *            String
     * @param pVersion
     *            string
     * 
     * @return deleteCount int
     * @exception BTSLBaseException
     */
    public int deleteCardGroupDetails(Connection pCon, String pCardGroupSetId, String pVersion) throws BTSLBaseException {
        
        int deleteCount = 0;
        final String methodName = "deleteCardGroupDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
    	loggerValue.append("Entered: cardGroupSetId=");
    	loggerValue.append(pCardGroupSetId);
		loggerValue.append(" pVersion= ");
    	loggerValue.append(pVersion);
            LOG.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM card_group_details ");
            strBuff.append("WHERE card_group_set_id = ? and version = ?");
            final String deleteQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlDelete:");
            	loggerValue.append(deleteQuery);
                LOG.debug(methodName,loggerValue);
            }
            try(PreparedStatement psmtDelete = pCon.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, pCardGroupSetId);
            psmtDelete.setString(2, pVersion);

            deleteCount = psmtDelete.executeUpdate();
            }
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            String logVal1=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteCardGroupDetails]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,loggerValue);
            String logVal1=loggerValue.toString();
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteCardGroupDetails]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount=");
            	loggerValue.append(deleteCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method to get the currently applicable card group set based on the
     * network and current dates
     * 
     * @param pCon
     * @param pCurrentDate
     *            Date
     * @param pCardGrpSetId
     *            String
     * 
     * @return isApplicable boolean;
     * @throws BTSLBaseException
     */
    public boolean isApplicableNow(Connection pCon, Date pCurrentDate, String pCardGrpSetId) throws BTSLBaseException {

        final String methodName = "isApplicableNow";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered currentDate:");
        	loggerValue.append(pCurrentDate);
    		loggerValue.append(" pCardGrpSetId=");
        	loggerValue.append(pCardGrpSetId);
            LOG.debug(methodName,loggerValue);
        }

       
        boolean isApplicable = false;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT card_group_set_id,version,applicable_from");
        strBuff.append(" FROM card_group_set_versions WHERE  ");
        strBuff.append(" card_group_set_id = ? AND applicable_from <= ?");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

        try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, pCardGrpSetId);
            pstmtSelect.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(pCurrentDate));

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isApplicable = true;
            }

        }
        }catch (SQLException sqe) {
    		loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe);
    		String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isApplicableNow]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
    		String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isApplicableNow]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }
        return isApplicable;
    }
}
