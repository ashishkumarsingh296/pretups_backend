package com.btsl.pretups.cardgroup.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;


/**
 * @author 
 *
 */
public class CardGroupSetDAO {

    private static final Log LOG = LogFactory.getLog(CardGroupSetDAO.class.getName());

    /**
     * Method for inserting Card Group Set.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pGroupSetVO
     *            CardGroupSetVO
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int addCardGroupSet(Connection pCon, CardGroupSetVO pGroupSetVO) throws BTSLBaseException {

         
        int insertCount = 0;
        final String methodName = "addCardGroupSet";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pGroupSetVO= ");
        	loggerValue.append(pGroupSetVO);
            LOG.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO card_group_set (card_group_set_id,");
            strBuff.append("card_group_set_name,network_code,created_on,created_by,");
            strBuff.append("modified_on,modified_by,last_version,module_code,status,sub_service,service_type,set_type,is_default) values ");
            strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                LOG.debug(methodName,loggerValue);
            }

            // commented for DB2 pstInsert =

            try(PreparedStatement psmtInsert =  pCon.prepareStatement(insertQuery);)
            {
            psmtInsert.setString(1, pGroupSetVO.getCardGroupSetID());
            // commented for DB2 psmtInsert.setFormOfUse(2,

            psmtInsert.setString(2, pGroupSetVO.getCardGroupSetName());
            psmtInsert.setString(3, pGroupSetVO.getNetworkCode());
            psmtInsert.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(pGroupSetVO.getCreatedOn()));
            psmtInsert.setString(5, pGroupSetVO.getCreatedBy());
            psmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(pGroupSetVO.getModifiedOn()));
            psmtInsert.setString(7, pGroupSetVO.getModifiedBy());
            psmtInsert.setString(8, pGroupSetVO.getLastVersion());
            psmtInsert.setString(9, pGroupSetVO.getModuleCode());
            psmtInsert.setString(10, pGroupSetVO.getStatus());
            psmtInsert.setString(11, pGroupSetVO.getSubServiceType());
            psmtInsert.setString(12, pGroupSetVO.getServiceType());
            psmtInsert.setString(13, pGroupSetVO.getSetType());
            psmtInsert.setString(14, pGroupSetVO.getDefaultCardGroup());
            insertCount = psmtInsert.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupSet]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupSet]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for inserting Card Group Set Version.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pCardGroupSetVO
     *            CardGroupSetVO
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int addCardGroupSetVersion(Connection pCon, CardGroupSetVersionVO pCardGroupSetVO) throws BTSLBaseException {
        
        int insertCount = 0;
        final String methodName = "addCardGroupSetVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pCardGroupSetVO= ");
        	loggerValue.append(pCardGroupSetVO);
            LOG.debug(methodName,loggerValue);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO card_group_set_versions (card_group_set_id,");
            strBuff.append("version,applicable_from,created_by,created_on,modified_by,modified_on) ");
            strBuff.append(" values (?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                LOG.debug(methodName,loggerValue);
            }

           try(PreparedStatement psmtInsert = pCon.prepareStatement(insertQuery);)
           {
            psmtInsert.setString(1, pCardGroupSetVO.getCardGroupSetID());
            psmtInsert.setString(2, pCardGroupSetVO.getVersion());
            psmtInsert.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(pCardGroupSetVO.getApplicableFrom()));
            psmtInsert.setString(4, pCardGroupSetVO.getCreatedBy());
            psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(pCardGroupSetVO.getCreadtedOn()));
            psmtInsert.setString(6, pCardGroupSetVO.getModifiedBy());
            psmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(pCardGroupSetVO.getModifiedOn()));

            insertCount = psmtInsert.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupSetVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupSetVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for loading Card Group Set Versions.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @param p_currentDate
     *            java.util.Date
     * @param pModuleCode
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList<CardGroupSetVersionVO> loadCardGroupSetVersion(Connection pCon, String pNetworkCode, Date p_currentDate, String pModuleCode) throws BTSLBaseException {

        final String methodName = "loadCardGroupSetVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered pNetworkCode=");
        	loggerValue.append(pNetworkCode);
			loggerValue.append(" currentDate:");
        	loggerValue.append(p_currentDate);
        	loggerValue.append( " pModuleCode=");
			loggerValue.append(pModuleCode);
            LOG.debug(methodName,loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cv.card_group_set_id,cv.version,applicable_from,cg.created_by,cg.created_on,cg.modified_by,cg.modified_on");
        strBuff.append(" FROM card_group_set cg,card_group_set_versions cv WHERE cg.network_code = ? ");
        strBuff.append(" AND cg.card_group_set_id = cv.card_group_set_id AND cg.module_code = ? AND ");
        strBuff.append(" (cv.applicable_from >= ? OR cv.applicable_from =(SELECT MAX(cv2.applicable_from) ");
        strBuff.append(" from card_group_set_versions cv2 WHERE cg.card_group_set_id = cv2.card_group_set_id ))");
        strBuff.append(" AND cg.status <> 'N' ORDER BY version");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

        final ArrayList list = new ArrayList();

        try {
            pstmtSelect = pCon.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, pNetworkCode);
            pstmtSelect.setString(2, pModuleCode);
            pstmtSelect.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_currentDate));

            rs = pstmtSelect.executeQuery();

            CardGroupSetVersionVO cardGroupSetVersionVO = null;

            while (rs.next()) {

                cardGroupSetVersionVO = new CardGroupSetVersionVO();
                cardGroupSetVersionVO.setCardGroupSetID(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_id")));
                cardGroupSetVersionVO.setVersion(SqlParameterEncoder.encodeParams(rs.getString("version")));
                cardGroupSetVersionVO.setApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                cardGroupSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
                cardGroupSetVersionVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                cardGroupSetVersionVO.setCreadtedOn(rs.getTimestamp("created_on"));
                cardGroupSetVersionVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                cardGroupSetVersionVO.setModifiedOn(rs.getTimestamp("modified_on"));
                list.add(cardGroupSetVersionVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe);
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: cardGroupSetVersion size=");
            	loggerValue.append(list.size());
                LOG.debug(methodName,loggerValue);
            }
        }
        return list;
    }

   
    public ArrayList<CardGroupSetVersionVO> loadCardGroupSetVersionNew(Connection pCon, String pNetworkCode, Date p_currentDate, String pModuleCode) throws BTSLBaseException {

        final String methodName = "loadCardGroupSetVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered pNetworkCode=");
        	loggerValue.append(pNetworkCode);
			loggerValue.append(" currentDate:");
        	loggerValue.append(p_currentDate);
        	loggerValue.append( " pModuleCode=");
			loggerValue.append(pModuleCode);
            LOG.debug(methodName,loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cv.card_group_set_id,cv.version,applicable_from,cg.created_by,cg.created_on,cg.modified_by,cg.modified_on");
        strBuff.append(" FROM card_group_set cg,card_group_set_versions cv WHERE cg.network_code = ? ");
        strBuff.append(" AND cg.card_group_set_id = cv.card_group_set_id AND cg.module_code = ? AND ");
        strBuff.append(" cv.version =(SELECT MAX(cv2.version) ");
        strBuff.append(" from card_group_set_versions cv2 WHERE cv2.applicable_from<= ? and cv.card_group_set_id = cv2.card_group_set_id )");
        strBuff.append(" AND cg.status <> 'N' ORDER BY version");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

        final ArrayList list = new ArrayList();

        try {
            pstmtSelect = pCon.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, pNetworkCode);
            pstmtSelect.setString(2, pModuleCode);
            pstmtSelect.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_currentDate));

            rs = pstmtSelect.executeQuery();

            CardGroupSetVersionVO cardGroupSetVersionVO = null;

            while (rs.next()) {

                cardGroupSetVersionVO = new CardGroupSetVersionVO();
                cardGroupSetVersionVO.setCardGroupSetID(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_id")));
                cardGroupSetVersionVO.setVersion(SqlParameterEncoder.encodeParams(rs.getString("version")));
                cardGroupSetVersionVO.setApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                cardGroupSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
                cardGroupSetVersionVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                cardGroupSetVersionVO.setCreadtedOn(rs.getTimestamp("created_on"));
                cardGroupSetVersionVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                cardGroupSetVersionVO.setModifiedOn(rs.getTimestamp("modified_on"));
                list.add(cardGroupSetVersionVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe);
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersion]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: cardGroupSetVersion size=");
            	loggerValue.append(list.size());
                LOG.debug(methodName,loggerValue);
            }
        }
        return list;
    }
    
    
    public ArrayList loadCardGroupSetVersionNumbers(Connection pCon,String cardGroupSetID, Date p_currentDate) throws BTSLBaseException {

        final String methodName = "loadCardGroupSetVersionNumbers";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered cardGroupSetID=");
        	loggerValue.append(cardGroupSetID);
			loggerValue.append(" currentDate:");
        	loggerValue.append(p_currentDate);
            LOG.debug(methodName,loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cv.card_group_set_id,cv.version");
        strBuff.append(" FROM card_group_set_versions cv");
        strBuff.append(" WHERE cv.card_group_set_id = ? ");
        strBuff.append(" AND cv.applicable_from <= ? ");
        strBuff.append(" ORDER BY version");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

        final ArrayList list = new ArrayList();

        try {
            pstmtSelect = pCon.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, cardGroupSetID);
            pstmtSelect.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_currentDate));

            rs = pstmtSelect.executeQuery();


            while (rs.next()) {
                list.add(SqlParameterEncoder.encodeParams(rs.getString("version")));
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe);
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionNumbers]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionNumbers]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: cardGroupSetVersion size=");
            	loggerValue.append(list.size());
                LOG.debug(methodName,loggerValue);
            }
        }
        return list;
    }
    
   
    
    public Date loadCardGroupSetVersionApplicableFromDate(Connection pCon, String cardGroupSetId, String version) throws BTSLBaseException {

        final String methodName = "loadCardGroupSetVersionDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered cardGroupSetId=");
        	loggerValue.append(cardGroupSetId);
			loggerValue.append( " version=");
			loggerValue.append(version);
            LOG.debug(methodName,loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        
        Date applicableFromDate= null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT applicable_from");
        strBuff.append(" FROM card_group_set_versions cv ");
        strBuff.append(" WHERE cv.card_group_set_id = ? AND cv.version = ? ");
       

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

       
        try {
            pstmtSelect = pCon.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, cardGroupSetId);
            pstmtSelect.setString(2, version);
            rs = pstmtSelect.executeQuery();

            CardGroupSetVersionVO cardGroupSetVersionVO = null;
           
            while (rs.next()) {
            	applicableFromDate= BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from"));
                
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe);
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionApplicableFromDate]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionApplicableFromDate]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: loadCardGroupSetVersionDetails applicable from date=");
            	loggerValue.append(applicableFromDate);
                LOG.debug(methodName,loggerValue);
            }
        }
        return applicableFromDate;
    }

    
    /**
     * Method for checking Card Group Set Name is already exist or not.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @param pCardGroupSetName
     *            String
     * @param pCardGroupSetId
     *            String
     * 
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isCardGroupSetNameExist(Connection pCon, String pNetworkCode, String pCardGroupSetName, String pCardGroupSetId) throws BTSLBaseException {
        final String methodName = "isCardGroupSetNameExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pNetworkCode=");
        	loggerValue.append(pNetworkCode);
        	loggerValue.append(" cardGroupSetName=");
        	loggerValue.append(pCardGroupSetName);
        	loggerValue.append(" pCardGroupSetId=");
        	loggerValue.append(pCardGroupSetId);
            LOG.debug(methodName,loggerValue);
        }


         
        
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();

        if (BTSLUtil.isNullString(pCardGroupSetId)) {
            strBuff.append("SELECT card_group_set_name FROM card_group_set WHERE network_code = ?");
            strBuff.append(" AND upper(card_group_set_name) = upper(?)");
        } else {
            strBuff.append("SELECT card_group_set_name FROM card_group_set WHERE network_code = ?");
            strBuff.append(" AND card_group_set_id != ? AND upper(card_group_set_name) = upper(?)");
        }
        final String sqlSelect = strBuff.toString();

        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

        try( PreparedStatement pstmt =  pCon.prepareStatement(sqlSelect);) {
            // commented for DB2 pstmt =

           
            if (BTSLUtil.isNullString(pCardGroupSetId)) {
                pstmt.setString(1, pNetworkCode);
                // commented for DB2 pstmt.setFormOfUse(2,

                pstmt.setString(2, pCardGroupSetName);
            } else {
                pstmt.setString(1, pNetworkCode);
                pstmt.setString(2, pCardGroupSetId);
                // commented for DB2pstmt.setFormOfUse(3,

                pstmt.setString(3, pCardGroupSetName);
            }
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupSetNameExist]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
        	String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupSetNameExist]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: existFlag=");
            	loggerValue.append(existFlag);
                LOG.debug(methodName, loggerValue);
            }
        }
    }

    /**
     * Method for Updating Card Group Set(only update the version).
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pCardGroupSetVO
     *            CardGroupSetVO
     * 
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int updateCardGroupSet(Connection pCon, CardGroupSetVO pCardGroupSetVO) throws BTSLBaseException {


       
        int updateCount = 0;
        StringBuilder loggerValue= new StringBuilder();
        final String methodName = "updateCardGroupSet";
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pCardGroupSetVO=");
        	loggerValue.append(pCardGroupSetVO);
            LOG.debug(methodName,loggerValue);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("UPDATE card_group_set SET card_group_set_name = ? ,last_version = ?, ");
            strBuff.append(" modified_on = ?, modified_by = ?,set_type=? WHERE card_group_set_id = ?");

            final String insertQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                LOG.debug(methodName,loggerValue);
            }

            

            try( PreparedStatement psmtUpdate =  pCon.prepareStatement(insertQuery);)
            {
            psmtUpdate.setString(1, pCardGroupSetVO.getCardGroupSetName());

            psmtUpdate.setString(2, pCardGroupSetVO.getLastVersion());
            psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(pCardGroupSetVO.getModifiedOn()));
            psmtUpdate.setString(4, pCardGroupSetVO.getModifiedBy());

            psmtUpdate.setString(5, pCardGroupSetVO.getSetType());
            psmtUpdate.setString(6, pCardGroupSetVO.getCardGroupSetID());

            final boolean modified = this.recordModified(pCon, pCardGroupSetVO.getCardGroupSetID(), pCardGroupSetVO.getLastModifiedOn());

            // if modified = true mens record modified by another user
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }

            updateCount = psmtUpdate.executeUpdate();

        }
        }// end of try
        catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException:");
        	loggerValue.append(be.toString());
            LOG.error(methodName,loggerValue);
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSet]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSet]", "", "", "",
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
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @param con
     *            Connection
     * @param pCardGroupSetId
     *            String
     * @param oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection con, String pCardGroupSetId, long oldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pCardGroupSetId= ");
        	loggerValue.append(pCardGroupSetId);
			loggerValue.append("oldLastModified= ");
        	loggerValue.append(oldLastModified);
            LOG.debug(methodName,loggerValue);
        }

        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM card_group_set WHERE card_group_set_id = ?";
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
        try ( PreparedStatement pstmt = con.prepareStatement(sqlRecordModified);){
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("QUERY: sqlselect= ");
            	loggerValue.append(sqlRecordModified);
                LOG.debug(methodName,loggerValue);
            }
            // create a prepared statement and execute it
            
            pstmt.setString(1, pCardGroupSetId);
            try( ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(" old=");
            	loggerValue.append(oldLastModified);
                LOG.debug(methodName,loggerValue);
                if (newLastModified != null) {
                	loggerValue.setLength(0);
                	loggerValue.append(" new=" );
                	loggerValue.append(newLastModified.getTime());
                    LOG.debug(methodName,loggerValue);
                } else {
                    LOG.debug(methodName, " new=null");
                }
            }
            if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

            return modified;
        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: " );
        	loggerValue.append(sqle.getMessage() );
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            String logVal=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[recordModified]", "", "", "",
            		logVal);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: " );
        	loggerValue.append(e.getMessage());
            String logVal=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[recordModified]", "", "", "",
            		logVal);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch

        finally {
        	

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exititng: modified=" + modified);
            }
        } // end of finally
    }

    /**
     * Method for Deleting Card Group Set(only update the status set status=N).
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pCardGroupSetVO
     *            CardGroupSetVO
     * 
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int deleteCardGroupSet(Connection pCon, CardGroupSetVO pCardGroupSetVO) throws BTSLBaseException {
        
        int deleteCount = 0;
        final String methodName = "deleteCardGroupSet";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pCardGroupSetVO=");
        	loggerValue.append(pCardGroupSetVO);
            LOG.debug(methodName,loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE card_group_set SET status = ? , modified_on = ?, modified_by = ? ");
            strBuff.append("WHERE card_group_set_id = ?");
            final String insertQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                LOG.debug(methodName,loggerValue);
            }
            try( PreparedStatement psmtDelete = pCon.prepareStatement(insertQuery);)
            {
            psmtDelete.setString(1, pCardGroupSetVO.getStatus());
            psmtDelete.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(pCardGroupSetVO.getModifiedOn()));
            psmtDelete.setString(3, pCardGroupSetVO.getModifiedBy());
            psmtDelete.setString(4, pCardGroupSetVO.getCardGroupSetID());
            deleteCount = psmtDelete.executeUpdate();
            }
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteCardGroupSet]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteCardGroupSet]", "", "", "",
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
     * Method for update Card Group Set Table.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param p_voList
     *            java.util.ArrayList
     * 
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int suspendCardGroupSetList(Connection pCon, List p_voList) throws BTSLBaseException {
        
         
        int updateCount = 0;
        StringBuilder loggerValue= new StringBuilder();
        final String methodName = "suspendCardGroupSetList";
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_voList Size= ");
        	loggerValue.append( p_voList.size());
            LOG.debug(methodName,loggerValue);
        }

        try {
            int listSize = 0;
            boolean modified = false;
            if (p_voList != null) {
                listSize = p_voList.size();
            }

            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("Update card_group_set SET status = ?, modified_by = ?, modified_on = ?,");
            strBuff.append("language_1_message = ?, language_2_message = ?");
            strBuff.append(" WHERE card_group_set_id = ?");

            final String updateQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            // commented for DB2 psmtUpdate = (OraclePreparedStatement)

            try(PreparedStatement psmtUpdate =  pCon.prepareStatement(updateQuery);)
            {
            CardGroupSetVO cardGroupSetVO = null;
            for (int i = 0; i < listSize; i++) {
                cardGroupSetVO = (CardGroupSetVO) p_voList.get(i);

                psmtUpdate.setString(1, cardGroupSetVO.getStatus());
                psmtUpdate.setString(2, cardGroupSetVO.getModifiedBy());
                psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(cardGroupSetVO.getModifiedOn()));
                psmtUpdate.setString(4, cardGroupSetVO.getLanguage1Message());

                // commented for DB2 psmtUpdate.setFormOfUse(5,

                psmtUpdate.setString(5, cardGroupSetVO.getLanguage2Message());

                psmtUpdate.setString(6, cardGroupSetVO.getCardGroupSetID());

                modified = this.recordModified(pCon, cardGroupSetVO.getCardGroupSetID(), cardGroupSetVO.getLastModifiedOn());

                // if modified = true mens record modified by another user
                if (modified) {
                    throw new BTSLBaseException("error.modified");
                }

                updateCount = psmtUpdate.executeUpdate();

                psmtUpdate.clearParameters();

                // check the status of the update
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

        } 
        }// end of try
        catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException:");
        	loggerValue.append(be.toString());
            LOG.error(methodName,loggerValue);
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[suspendCardGroupSetList]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[suspendCardGroupSetList]", "", "", "",
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
     * Method load Card Group Set FOr TransferRule
     * 
     * @author vikas.yadav
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @param pModuleCode
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadCardGroupSetForTransferRule(Connection pCon, String NetworkCode, String ModuleCode, String set_type) throws BTSLBaseException {
        final String methodName = "loadCardGroupSetFOrTransferRule";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered pNetworkCode=");
        	loggerValue.append(NetworkCode);
        	loggerValue.append(" pModuleCode=");
        	loggerValue.append(ModuleCode);
        	loggerValue.append("p_set_type=");
        	loggerValue.append(set_type);
            LOG.debug(methodName,loggerValue);
        }
        String pNetworkCode = SqlParameterEncoder.encodeParams(NetworkCode);
        String pModuleCode = SqlParameterEncoder.encodeParams(ModuleCode);
        String p_set_type = SqlParameterEncoder.encodeParams(set_type);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT card_group_set_id,card_group_set_name,sub_service, service_type, set_type ");
        strBuff.append("FROM card_group_set ");
        strBuff.append("WHERE network_code =? AND module_code = ? AND status <> ? AND set_type = ? ");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmtSelect = pCon.prepareStatement(sqlSelect);

            pstmtSelect.setString(1, pNetworkCode);
            pstmtSelect.setString(2, pModuleCode);
            pstmtSelect.setString(3, PretupsI.STATUS_DELETE);
            pstmtSelect.setString(4, p_set_type);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                list.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_name")),SqlParameterEncoder.encodeParams( rs.getString("sub_service")) + ":" 
            +SqlParameterEncoder.encodeParams(rs.getString("card_group_set_id")) + ":" + SqlParameterEncoder.encodeParams(rs.getString("service_type"))));
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe);
        	String logVal1=loggerValue.toString();
            LOG.error("loadCardGroupSetForTransferRule()",loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetForTransferRule]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, "loadCardGroupSetFOrTransferRule", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
        	String logVal1=loggerValue.toString();
            LOG.error("loadCardGroupSetForTransferRule()",loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetForTransferRule]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, "loadCardGroupSetFOrTransferRule", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: cardGroupSet size=");
            	loggerValue.append(list.size());
            	String logVal1=loggerValue.toString();
                LOG.debug("loadCardGroupSetFOrTransferRule",loggerValue);
            }
        }
        return list;
    }

    /**
     * Method for loading Card Group Set Versions List.
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pCardGroupSetId
     *            String
     * @param pModuleCode
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadCardGroupSetVersionList(Connection pCon, String pCardGroupSetId, String pModuleCode) throws BTSLBaseException {
        final String methodName = "loadCardGroupSetVersionList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered pCardGroupSetId=");
        	loggerValue.append(pCardGroupSetId);
			loggerValue.append("pModuleCode=");
        	loggerValue.append(pModuleCode);
            LOG.debug(methodName,loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cv.card_group_set_id,cv.version,applicable_from");
        strBuff.append(" FROM card_group_set cg,card_group_set_versions cv WHERE cg.card_group_set_id=? ");
        strBuff.append(" AND cg.card_group_set_id = cv.card_group_set_id AND cg.module_code = ? ");
        strBuff.append(" AND cg.status <> 'N' ORDER BY version");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

        final ArrayList list = new ArrayList();

        try {
            pstmtSelect = pCon.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, pCardGroupSetId);
            pstmtSelect.setString(2, pModuleCode);
            rs = pstmtSelect.executeQuery();
            CardGroupSetVersionVO cardGroupSetVersionVO = null;
            while (rs.next()) {

                cardGroupSetVersionVO = new CardGroupSetVersionVO();
                cardGroupSetVersionVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupSetVersionVO.setVersion(rs.getString("version"));
                cardGroupSetVersionVO.setApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                cardGroupSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
                list.add(cardGroupSetVersionVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionList]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: loadCardGroupSetVersionList size=");
            	loggerValue.append(list.size());
                LOG.debug(methodName,loggerValue);
            }
        }
        return list;
    }

    /**
     * Method to update new cardgroup as default and previous as normal
     * 
     * @param pCon
     * @param p_prv_cardGroupSetID
     *            String
     * @param pCurentCardGroupSetID
     *            String
     * 
     * @return isApplicable boolean;
     * @throws BTSLBaseException
     */
    public boolean updateAsDefault(Connection pCon, String p_prv_cardGroupSetID, String pCurentCardGroupSetID, String pUserID, Date p_current) throws BTSLBaseException {

        final String methodName = "updateAsDefault";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_prv_cardGroupSetID:");
        	loggerValue.append(p_prv_cardGroupSetID);
			loggerValue.append(" pCurentCardGroupSetID=");
        	loggerValue.append(pCurentCardGroupSetID);
        	loggerValue.append(", pUserID:");
        	loggerValue.append(pUserID);
        	loggerValue.append(", p_current:");
        	loggerValue.append(p_current);
            LOG.debug(methodName,loggerValue);
        }

         
        int updateCount = 0;
        boolean isApplicable = false;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" UPDATE card_group_set");
        strBuff.append(" SET is_default=?, modified_by=?, modified_on=?");
        strBuff.append(" WHERE card_group_set_id = ?");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

        try(PreparedStatement psmtUpdate = pCon.prepareStatement(sqlSelect);) {
            
            psmtUpdate.clearParameters();
            psmtUpdate.setString(1, PretupsI.YES);
            psmtUpdate.setString(2, pUserID);
            psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_current));
            psmtUpdate.setString(4, pCurentCardGroupSetID);

            updateCount = psmtUpdate.executeUpdate();
            if (updateCount > 0) {
                if (!BTSLUtil.isNullString(p_prv_cardGroupSetID)) {
                    updateCount = 0;
                    psmtUpdate.clearParameters();
                    psmtUpdate.setString(1, PretupsI.NO);
                    psmtUpdate.setString(2, pUserID);
                    psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_current));
                    psmtUpdate.setString(4, p_prv_cardGroupSetID);

                    updateCount = psmtUpdate.executeUpdate();
                    if (updateCount > 0) {
                        isApplicable = true;
                    }
                } else {
                    isApplicable = true;
                }

            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateAsDefault]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isApplicableNow()", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateAsDefault]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }
        return isApplicable;
    }

    
    public boolean updateDefaultAsNo(Connection pCon, String cardGroupSetID,  String pUserID, Date p_current) throws BTSLBaseException {

        final String methodName = "updateDefaultAsNo";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered cardGroupSetID:");
        	loggerValue.append(cardGroupSetID);
        	loggerValue.append(", pUserID:");
        	loggerValue.append(pUserID);
        	loggerValue.append(", p_current:");
        	loggerValue.append(p_current);
            LOG.debug(methodName,loggerValue);
        }

         
        int updateCount = 0;
        boolean isApplicable = false;

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" UPDATE card_group_set");
        strBuff.append(" SET is_default=?, modified_by=?, modified_on=?");
        strBuff.append(" WHERE card_group_set_id = ?");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,loggerValue);
        }

        try(PreparedStatement psmtUpdate = pCon.prepareStatement(sqlSelect);) {
            
            psmtUpdate.clearParameters();
            psmtUpdate.setString(1, PretupsI.NO);
            psmtUpdate.setString(2, pUserID);
            psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_current));
            psmtUpdate.setString(4, cardGroupSetID);

            updateCount = psmtUpdate.executeUpdate();
            if (updateCount > 0) {
                 isApplicable = true;
                
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateDefaultAsNo]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isApplicableNow()", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateDefaultAsNo]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }
        return isApplicable;
    }

    
    
    /**
     * Method for isDefaultCardGroupExist.
     * 
     * @param pCon
     *            java.sql.Connection
     *            return boolean
     * @exception BTSLBaseException
     */
    /**
     * @param pCon
     * @param pTransferVO
     * @return isDefaultExist
     * @throws BTSLBaseException
     */
    public boolean isDefaultCardGroupExist(Connection pCon, TransferVO pTransferVO) throws BTSLBaseException {
        final String methodName = "isDefaultCardGroupExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_serviceType=");
        	loggerValue.append(pTransferVO.getServiceType());
        	loggerValue.append(", p_subService=");
        	loggerValue.append(pTransferVO.getSubService());
        	loggerValue.append(",module_code=");
        	loggerValue.append(pTransferVO.getModule());
        	loggerValue.append(",network_code");
        	loggerValue.append(pTransferVO.getReceiverNetworkCode());
            LOG.debug(methodName,loggerValue);
        }

        boolean isDefaultExist = false;
        StringBuilder queryBuff = null;
       
        try {
            queryBuff = new StringBuilder();
            queryBuff.append(" select CARD_GROUP_SET_ID,STATUS,LANGUAGE_1_MESSAGE,IS_DEFAULT ");
            queryBuff.append(" from CARD_GROUP_SET where service_type=? and SUB_SERVICE=? and is_default='Y' and module_code=?");
            queryBuff.append(" and network_code =? ");
            final String selectQuery = queryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("selectQuery =" );
            	loggerValue.append(selectQuery);
                LOG.debug(methodName,loggerValue);
            }

           try( PreparedStatement psmt = pCon.prepareStatement(selectQuery);)
           {
            psmt.setString(1, pTransferVO.getServiceType());
            psmt.setString(2, pTransferVO.getSubService());
            psmt.setString(3, pTransferVO.getModule());
            psmt.setString(4, pTransferVO.getReceiverNetworkCode());
           try(ResultSet rs = psmt.executeQuery();)
           {
            while (rs.next()) {
                pTransferVO.setCardGroupSetID(rs.getString("CARD_GROUP_SET_ID"));
                pTransferVO.setStatus(rs.getString("status"));
                pTransferVO.setSenderReturnMessage(rs.getString("LANGUAGE_1_MESSAGE"));
                isDefaultExist = true;
            }
        }
           }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
        	String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isDefaultCardGroupExist]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
        	String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isDefaultCardGroupExist]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting isDefaultExist ");
            	loggerValue.append(isDefaultExist);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally
        return isDefaultExist;
    }

    /**
     * This method loads the Card group version details
     * 
     * @param pCon
     * @param p_cardGroupSetAssocVO
     * @return CardGroupDetailsVO
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public ConcurrentMap<String, ArrayList<CardGroupSetVersionVO>> loadCardGroupVersionCache() throws BTSLBaseException {
        final String methodName = "loadCardGroupVersionCache";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered ");
        }
       
        Connection con = null;
        final ConcurrentMap<String, ArrayList<CardGroupSetVersionVO>> cardGroupVersionMap = new ConcurrentHashMap<String, ArrayList<CardGroupSetVersionVO>>();
        ArrayList<CardGroupSetVersionVO> cardGroupVersionList = null;
       
        CardGroupSetVersionVO cardGroupSetVersionVO = null;
        String key = null;
        String oldKey = null;
        try {
        	CardGroupSetQry cardGroupSetQry = (CardGroupSetQry)ObjectProducer.getObject(QueryConstants.CARD_GROUP_QRY, QueryConstants.QUERY_PRODUCER);
			String qry  = cardGroupSetQry.loadCardGroupVersionCacheQry();

            final String selectQuery = qry;
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                LOG.debug(methodName,loggerValue);
            }
            con = OracleUtil.getSingleConnection();
            try( PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
            {
            
            while (rs.next()) {
                cardGroupSetVersionVO = new CardGroupSetVersionVO();
                cardGroupSetVersionVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupSetVersionVO.setVersion(rs.getString("version"));
                cardGroupSetVersionVO.setApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_From")));
                key = cardGroupSetVersionVO.getCardGroupSetID();
                if (oldKey == null) {
                    cardGroupVersionList = new ArrayList<CardGroupSetVersionVO>();
                } else if(!oldKey.equals(key))
				{ 
					if(oldKey!=null)
				    cardGroupVersionMap.put(oldKey, cardGroupVersionList);
					cardGroupVersionList=new ArrayList<CardGroupSetVersionVO>();
				}
                cardGroupVersionList.add(cardGroupSetVersionVO);
                oldKey = key;
            }
            cardGroupVersionMap.put(oldKey, cardGroupVersionList);
            return cardGroupVersionMap;
        } 
        }catch (BTSLBaseException bex) {
        	loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(bex.getMessage());
            LOG.error(methodName,loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupVersionCache]", "", "", "",
                "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupVersionCache]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupVersionCache]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
           
           OracleUtil.closeQuietly(con);
        
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting cardGroupVersionMap.size()=:");
            	loggerValue.append(cardGroupVersionMap.size());
                LOG.debug("loadCardGroupCache",loggerValue);
            }
        }// end of finally
    }
    
    /**
     * @param pCon
     * @param pTransferVO
     * @return transferRulesVO
     * @throws BTSLBaseException
     */
    public TransferRulesVO loadDefaultCardGroup(Connection pCon, TransferVO pTransferVO) throws BTSLBaseException
	{
		String methodName = "loadDefaultCardGroup";
		StringBuilder loggerValue= new StringBuilder();
		if (LOG.isDebugEnabled()){
			loggerValue.setLength(0);
        	loggerValue.append("Entered: p_serviceType=");
        	loggerValue.append(pTransferVO.getServiceType());
			loggerValue.append(", p_subService=");
        	loggerValue.append(pTransferVO.getSubService());
        	loggerValue.append(",module_code=");
        	loggerValue.append(pTransferVO.getModule());
        	loggerValue.append(",network_code");
        	loggerValue.append(pTransferVO.getReceiverNetworkCode());
			LOG.debug(methodName,loggerValue);
		}
		StringBuilder queryBuff=null;
		 
		TransferRulesVO transferRulesVO=null;
		try
		{
			queryBuff=new StringBuilder();
			queryBuff.append(" select CARD_GROUP_SET_ID,STATUS,LANGUAGE_1_MESSAGE,IS_DEFAULT ");
			queryBuff.append(" from CARD_GROUP_SET where service_type=? and SUB_SERVICE=? and is_default='Y' and module_code=? ");
			queryBuff.append(" and network_code =? ");
			String selectQuery = queryBuff.toString();
			if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
            	loggerValue.append("selectQuery =");
            	loggerValue.append(selectQuery);
				LOG.debug(methodName,loggerValue);
			}
			try(PreparedStatement psmt = pCon.prepareStatement(selectQuery);)
			{
			psmt.setString(1, pTransferVO.getServiceType());
			psmt.setString(2, pTransferVO.getSubService());	
			psmt.setString(3, pTransferVO.getModule());
			psmt.setString(4, pTransferVO.getReceiverNetworkCode());	
			try(ResultSet rs=psmt.executeQuery();)
			{
			while(rs.next())
			{
				transferRulesVO=new TransferRulesVO();
				transferRulesVO.setCardGroupSetID(rs.getString("CARD_GROUP_SET_ID"));
				transferRulesVO.setStatus(rs.getString("status"));
			}
		}
			}
		}// end of try
		catch (SQLException sqle)
		{
			loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
			LOG.error(methodName,loggerValue);
			LOG.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupDAO[isDefaultCardGroupExist]","","","",logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e)
		{
			loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
			LOG.error(methodName,loggerValue);
			LOG.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupDAO[isDefaultCardGroupExist]","","","",logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally
		{
			
			if (LOG.isDebugEnabled()){
				loggerValue.setLength(0);
            	loggerValue.append("Exiting transferRulesVO ");
            	loggerValue.append(transferRulesVO);
				LOG.debug(methodName,loggerValue);
			}
		} // end of finally
		return transferRulesVO;
	}
	




public ArrayList validateCardGroupSetId(Connection pCon, String NetworkCode, String ModuleCode, String set_type,String cardGroupSetID ) throws BTSLBaseException {
    final String methodName = "loadCardGroupSetFOrTransferRule";
    StringBuilder loggerValue= new StringBuilder();
    if (LOG.isDebugEnabled()) {
    	loggerValue.setLength(0);
    	loggerValue.append("Entered pNetworkCode=");
    	loggerValue.append(NetworkCode);
    	loggerValue.append(" pModuleCode=");
    	loggerValue.append(ModuleCode);
    	loggerValue.append("p_set_type=");
    	loggerValue.append(set_type);
        LOG.debug(methodName,loggerValue);
    }
    String pNetworkCode = SqlParameterEncoder.encodeParams(NetworkCode);
    String pModuleCode = SqlParameterEncoder.encodeParams(ModuleCode);
    String p_set_type = SqlParameterEncoder.encodeParams(set_type);
    PreparedStatement pstmtSelect = null;
    ResultSet rs = null;

    final StringBuilder strBuff = new StringBuilder();
    strBuff.append("SELECT card_group_set_id,card_group_set_name,sub_service, service_type, set_type ");
    strBuff.append("FROM card_group_set ");
    strBuff.append("WHERE network_code =? AND module_code = ? AND status <> ? AND set_type = ?  AND card_group_set_id= ? ");

    final String sqlSelect = strBuff.toString();
    if (LOG.isDebugEnabled()) {
    	loggerValue.setLength(0);
    	loggerValue.append("QUERY sqlSelect=");
    	loggerValue.append(sqlSelect);
        LOG.debug(methodName,loggerValue);
    }
    final ArrayList list = new ArrayList();
    try {
        pstmtSelect = pCon.prepareStatement(sqlSelect);

        pstmtSelect.setString(1, pNetworkCode);
        pstmtSelect.setString(2, pModuleCode);
        pstmtSelect.setString(3, PretupsI.STATUS_DELETE);
        pstmtSelect.setString(4, p_set_type);
        pstmtSelect.setString(5,cardGroupSetID);
        rs = pstmtSelect.executeQuery();
        while (rs.next()) {
            list.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_name")),SqlParameterEncoder.encodeParams( rs.getString("sub_service")) + ":" 
        +SqlParameterEncoder.encodeParams(rs.getString("card_group_set_id")) + ":" + SqlParameterEncoder.encodeParams(rs.getString("service_type"))));
        }

    } catch (SQLException sqe) {
    	loggerValue.setLength(0);
    	loggerValue.append("SQL Exception : ");
    	loggerValue.append(sqe);
    	String logVal1=loggerValue.toString();
        LOG.error("validateCardGroupSetId()",loggerValue);
        LOG.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[validateCardGroupSetId]", "",
            "", "", logVal1);
        throw new BTSLBaseException(this, "validateCardGroupSetId", "error.general.sql.processing");
    } catch (Exception ex) {
    	loggerValue.setLength(0);
    	loggerValue.append("Exception : ");
    	loggerValue.append(ex.getMessage());
    	String logVal1=loggerValue.toString();
        LOG.error("loadCardGroupSetForTransferRule()",loggerValue);
        LOG.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[validateCardGroupSetId]", "",
            "", "", logVal1);
        throw new BTSLBaseException(this, "validateCardGroupSetId", "error.general.processing");
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelect != null) {
                pstmtSelect.close();
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting: cardGroupSet size=");
        	loggerValue.append(list.size());
        	String logVal1=loggerValue.toString();
            LOG.debug("validateCardGroupSetId",loggerValue);
        }
    }
    return list;
}

/**
 * @param pCon
 * @param serviceType
 * @param subService
 * @param setType
 * @param networkCode
 * @param cardGroupSetName
 * @param version
 * @return cardGroupSetID
 * @throws BTSLBaseException
 */
public String loadCardGroupSetID(Connection pCon,String serviceType, String subService, String setType,String networkCode, String cardGroupSetName) throws BTSLBaseException
{
	String methodName = "loadCardGroupSetID";
	StringBuilder loggerValue= new StringBuilder();
	if (LOG.isDebugEnabled()){
		loggerValue.setLength(0);
    	loggerValue.append("Entered: p_serviceType=");
    	loggerValue.append(serviceType);
		loggerValue.append(", p_subService=");
    	loggerValue.append(subService);
    	loggerValue.append(",setType=");
    	loggerValue.append(setType);
    	loggerValue.append(",network_code");
    	loggerValue.append(networkCode);
    	loggerValue.append(",cardGroupSetName");
    	loggerValue.append(cardGroupSetName);
    	LOG.debug(methodName,loggerValue);
	}
	StringBuilder queryBuff=null;
	String cardGroupSetID= null; 
	try
	{
		queryBuff=new StringBuilder();
		queryBuff.append(" select CARD_GROUP_SET_ID");
		queryBuff.append(" from CARD_GROUP_SET where service_type=? and SUB_SERVICE=? and network_code=? and set_type=? ");
		queryBuff.append(" and card_group_set_name =? ");
		String selectQuery = queryBuff.toString();
		if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
        	loggerValue.append("selectQuery =");
        	loggerValue.append(selectQuery);
			LOG.debug(methodName,loggerValue);
		}
		try(PreparedStatement psmt = pCon.prepareStatement(selectQuery);)
		{
		psmt.setString(1, serviceType);
		psmt.setString(2, subService);	
		psmt.setString(3, networkCode);
		psmt.setString(4, setType);
		psmt.setString(5, cardGroupSetName);
		try(ResultSet rs=psmt.executeQuery();)
		{
		while(rs.next())
		{
			cardGroupSetID=	rs.getString("CARD_GROUP_SET_ID");
		}
	}
		}
	}// end of try
	catch (SQLException sqle)
	{
		loggerValue.setLength(0);
    	loggerValue.append("SQL Exception: ");
    	loggerValue.append(sqle.getMessage());
		String logVal1=loggerValue.toString();
		LOG.error(methodName,loggerValue);
		LOG.errorTrace(methodName,sqle);
		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupDAO[loadCardGroupSetID]","","","",logVal1);
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	} // end of catch
	catch (Exception e)
	{
		loggerValue.setLength(0);
    	loggerValue.append("Exception: ");
    	loggerValue.append(e.getMessage());
		String logVal1=loggerValue.toString();
		LOG.error(methodName,loggerValue);
		LOG.errorTrace(methodName,e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupDAO[loadCardGroupSetID]","","","",logVal1);
		throw new BTSLBaseException(this, methodName, "error.general.processing");
	} // end of catch
	finally
	{
		
		if (LOG.isDebugEnabled()){
			loggerValue.setLength(0);
        	loggerValue.append("Exiting cardGroupSetId ");
        	loggerValue.append(cardGroupSetID);
			LOG.debug(methodName,loggerValue);
		}
	} // end of finally
	return cardGroupSetID;
}



}