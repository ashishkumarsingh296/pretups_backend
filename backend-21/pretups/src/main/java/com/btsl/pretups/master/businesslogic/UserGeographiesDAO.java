package com.btsl.pretups.master.businesslogic;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserGeographiesVO;
/**
 * 
 *class UserGeographiesDAO
 *
 */
public class UserGeographiesDAO {

    private static final Log log = LogFactory.getLog(GeographicalDomainDAO.class.getName());

    /**
     * Method isActiveUserAssociatedWithGrphDomain.
     * 
     * @param conn
     *            Connection
     * @param grphDomainCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isActiveUserAssociatedWithGrphDomain(Connection conn, String grphDomainCode) throws BTSLBaseException {
        final String methodName = "isActiveUserAssociatedWithGrphDomain";
        LogFactory.printLog(methodName, "Entered:p_grphDomainCode=" + grphDomainCode, log);
       
        
        boolean isActive = false;
        StringBuilder sqlRecordActive = new StringBuilder();
        sqlRecordActive.append("SELECT 1 FROM  user_geographies UG,users U ");
        sqlRecordActive.append("WHERE UG.grph_domain_code=? AND U.user_id=UG.user_id ");
        sqlRecordActive.append("AND U.status !=?  AND U.status !=? ");
        LogFactory.printLog(methodName, "QUERY=" + sqlRecordActive, log);
        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlRecordActive.toString());){
           
           
           
            pstmtSelect.setString(1, grphDomainCode);
            pstmtSelect.setString(2, PretupsI.USER_STATUS_CANCELED);
            pstmtSelect.setString(3, PretupsI.USER_STATUS_DELETED);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                isActive = true;
            }
        }
        }// end of try
        catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[isActiveUserAssociatedWithGrphDomain]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[isActiveUserAssociatedWithGrphDomain]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");
        } finally {
        	
            LogFactory.printLog(methodName, "Exititng:isActive=" + isActive, log);
            
        }// end of finally
        return isActive;
    }// end isActiveUserAssociatedWithGrphDomain

    /**
     * Method for inserting User Geography Info.
     * 
     * Used in (UserAction, ChannelUserAction)
     * 
     * @author mohit.goel
     * 
     * @param conn
     *            java.sql.Connection
     * @param geographyList
     *            ArrayList
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addUserGeographyList(Connection conn, ArrayList geographyList) throws BTSLBaseException {
        final String methodName = "addUserGeographyList";
        
        int insertCount = 0;
        LogFactory.printLog(methodName, "Entered: p_phoneList= " + geographyList, log);
        
        try {
            int count = 0;
            if (geographyList != null) {
                StringBuilder strBuff = new StringBuilder();
                strBuff.append("INSERT INTO user_geographies (user_id,");
                strBuff.append("grph_domain_code)");
                strBuff.append(" values (?,?)");

                String insertQuery = strBuff.toString();
                if (log.isDebugEnabled()) {
                    log.debug("addUserGeographyList", "Query sqlInsert:" + insertQuery);
                }
                try(PreparedStatement psmtInsert = conn.prepareStatement(insertQuery);)
                {
                for (int i = 0, j = geographyList.size(); i < j; i++) {
                    UserGeographiesVO myVO = (UserGeographiesVO) geographyList.get(i);

                    psmtInsert.setString(1, myVO.getUserId());
                    psmtInsert.setString(2, myVO.getGraphDomainCode());

                    insertCount = psmtInsert.executeUpdate();
                    psmtInsert.clearParameters();
                    // check the status of the update
                    if (insertCount > 0) {
                        count++;
                    }
                }
                if (count == geographyList.size()) {
                    insertCount = 1;
                } else {
                    insertCount = 0;
                }
            }
        } 
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[addUserGeographyList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[addUserGeographyList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, log);
           
        } // end of finally

        return insertCount;
    }

    /**
     * This method validates whether the geography code and msisdn given by user
     * belong to the same parent
     * 
     * @param conn
     * @param geoCode
     * @param msisdn
     * @throws BTSLBaseException
     * @return boolean
     * @author ankur.dhawan
     */
    public boolean validateGeographyOfParent(Connection conn, String geoCode, String msisdn) throws BTSLBaseException {
        final String methodName = "validateGeographyOfParent";
        LogFactory.printLog(methodName, "Entered:geoCode=" + geoCode + " msisdn=" + msisdn, log);
     

        
        boolean isValid = false;

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT (1) FROM users u,user_geographies ug ");
        strBuff.append("WHERE u.user_id=ug.user_id AND u.msisdn=? AND ug.grph_domain_code=? ");
        String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName, "Query : "+ sqlSelect, log);
       

        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);){
           
            int i = 1;
            pstmtSelect.setString(i, msisdn);
            pstmtSelect.setString(++i, geoCode);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs != null) {
                isValid = true;
            }
        }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException:" + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[validateGeographyOfParent]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[validateGeographyOfParent]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");
        } finally {
        	
        	LogFactory.printLog(methodName, "Exiting: isValid=" + isValid, log);
           
        }
        return isValid;
    }
    
    /**
     * Method for deleting User Geographies.
     * 
     * @param con
     *            java.sql.Connection
     * @param userId
     *            String
     * @return deleteCount int
     * @exception BTSLBaseException
     */

    public int deleteUserGeographies(Connection con, String userId) throws BTSLBaseException {
        final String methodName = "deleteUserGeographies";
        
        int deleteCount = 0;
        LogFactory.printLog(methodName, "Entered: p_userId= " + userId, log);
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM user_geographies WHERE user_id = ?");
            String deleteQuery = strBuff.toString();
            LogFactory.printLog(methodName, "Query sqlDelete:" + deleteQuery, log);
            try(PreparedStatement psmtDelete = con.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, userId);
            deleteCount = psmtDelete.executeUpdate();
            psmtDelete.clearParameters();
            }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"UserGeographiesDAO[deleteUserGeographies]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"UserGeographiesDAO[deleteUserGeographies]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }finally {
        
            LogFactory.printLog(methodName, "Exiting: deleteCount=" + deleteCount, log);
        }

        return deleteCount;
    }

}
