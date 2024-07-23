package com.btsl.pretups.domain.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.util.OracleUtil;
/**
 * Class CategoryReqGtwTypeDAO
 */
public class CategoryReqGtwTypeDAO {

    private Log log = LogFactory.getFactory().getInstance(CategoryReqGtwTypeDAO.class.getName());

    /**
     * Method for inserting MessageGatewayType with category code in
     * gategory_req_gtw_type table
     * Method:addCategoryReqGtwTypesList
     * 
     * @param conn java.sql.Connection
     * @param pcategoryCode   String
     * @param pmessageGatewayTypeList java.uril.ArrayList
     * @return insertCount int
     * @exception BTSLBaseException
     */
    public int addCategoryReqGtwTypesList(Connection conn, String pcategoryCode, ArrayList pmessageGatewayTypeList) throws BTSLBaseException {
       
        MessageGatewayVO messageGatewayVO = null;
        int insertCount = 0;
        final String methodName = "addCategoryReqGtwTypesList";
        LogFactory.printLog(methodName, "Entered: p_categoryCode= " + pcategoryCode + " ,p_messageGatewayTypeList Size= " + pmessageGatewayTypeList.size(), log);
        
        try {
            int count = 0;
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO category_req_gtw_types (category_code,");
            strBuff.append(" gateway_type) values (UPPER(?),?)");
            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
           try(PreparedStatement psmtInsert = conn.prepareStatement(insertQuery);)
           {
            messageGatewayVO = new MessageGatewayVO();
            int pmessageGatewayTypeLists=pmessageGatewayTypeList.size();
            for (int i = 0; i < pmessageGatewayTypeLists; i++) {
                messageGatewayVO = (MessageGatewayVO) pmessageGatewayTypeList.get(i);
                psmtInsert.setString(1, pcategoryCode);
                psmtInsert.setString(2, messageGatewayVO.getGatewayType());
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the insert
                if (insertCount > 0) {
                    count++;
                }
            }
            if (count == pmessageGatewayTypeList.size()) {
                insertCount = 1;
            } else {
                insertCount = 0;
            }

        } 
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[addCategoryReqGtwTypesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[addCategoryReqGtwTypesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
        	LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, log);
           
            
        } // end of finally
        return insertCount;
    }

    /**
     * Method for inserting MessageGatewayType with category code in
     * gategory_req_gtw_type table
     * Method:deleteCategoryReqGtwTypesList
     * 
     * @param conn
     *            java.sql.Connection
     * @param pcategoryCode
     *            String
     * @param pmessageGatewayTypeList
     *            java.uril.ArrayList
     * @return insertCount int
     * @exception BTSLBaseException
     */
    public int deleteCategoryReqGtwTypesList(Connection conn, String pcategoryCode, ArrayList pmessageGatewayTypeList) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        PreparedStatement psmtSelect = null;
        PreparedStatement psmtDelete = null;
        ResultSet rs = null;
        MessageGatewayVO messageGatewayVO = null;
        int insertCount = 0;
        final String methodName = "deleteCategoryReqGtwTypesList";
        LogFactory.printLog(methodName, "Entered: p_categoryCode= " + pcategoryCode + " ,p_messageGatewayTypeList Size= " + pmessageGatewayTypeList.size(), log);
        try {
            int count = 0;
            StringBuilder selectBuff = new StringBuilder("select 1 FROM category_req_gtw_types ");
            selectBuff.append(" WHERE category_code= ?");
            String selectQuery = selectBuff.toString();

            StringBuilder deleteBuff = new StringBuilder("delete FROM category_req_gtw_types ");
            deleteBuff.append(" WHERE category_code= ?");
            String deleteQuery = deleteBuff.toString();

            StringBuilder strBuff = new StringBuilder("INSERT INTO category_req_gtw_types (category_code,");
            strBuff.append(" gateway_type) values (?,?)");
            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query selectQuery:" + selectQuery);
            }

            psmtSelect = conn.prepareStatement(selectQuery);
            psmtSelect.setString(1, pcategoryCode);
            rs = psmtSelect.executeQuery();
            if (rs.next()) {
                psmtDelete = conn.prepareStatement(deleteQuery);
                psmtDelete.setString(1, pcategoryCode);
                psmtDelete.executeUpdate();
            }
            psmtInsert = conn.prepareStatement(insertQuery);
            messageGatewayVO = new MessageGatewayVO();
            int pmessageGatewayTypeLists=pmessageGatewayTypeList.size();
            for (int i = 0; i <pmessageGatewayTypeLists ; i++) {
                messageGatewayVO = (MessageGatewayVO) pmessageGatewayTypeList.get(i);
                psmtInsert.setString(1, pcategoryCode);
                psmtInsert.setString(2, messageGatewayVO.getGatewayType());
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the insert
                if (insertCount > 0) {
                    count++;
                }
            }
            if (count == pmessageGatewayTypeList.size()) {
                insertCount = 1;
            } else {
                insertCount = 0;
            }

        } // end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[deleteCategoryReqGtwTypesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[deleteCategoryReqGtwTypesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtSelect!= null){
                	psmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtInsert!= null){
                	psmtInsert.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtDelete!= null){
                	psmtDelete.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    }

    /**
     * Method loadMessageGatewayTypeList.
     * this method is to load the list of all the message gateway Type list form
     * message_gateway_types
     * 
     * @author manoj kumar
     * @param conn
     *            Connection
     * @param pcategoryCode
     *            java.lang.String
     * @return messageGatewayTypeList java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadMessageGatewayTypeList(Connection conn, String pcategoryCode) throws BTSLBaseException {
        final String methodName = "loadMessageGatewayTypeList";
        LogFactory.printLog(methodName, "Entered:", log);
        
        
        ArrayList messageGatewayTypeList = null;
        MessageGatewayVO messageGatewayVO = null;
       
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT CRGT.gateway_type,MGT.gateway_type_name,MGT.access_from ");
            selectQuery.append(" FROM category_req_gtw_types CRGT,message_gateway_types MGT ");
            selectQuery.append(" WHERE category_code=? AND CRGT.gateway_type=MGT.gateway_type");
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query=" + selectQuery);
            }

            try(PreparedStatement pstmtSelect = conn.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(1, pcategoryCode);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            messageGatewayTypeList = new ArrayList();
            while (rs.next()) {
                messageGatewayVO = new MessageGatewayVO();
                messageGatewayVO.setGatewayType(rs.getString("gateway_type"));
                messageGatewayVO.setGatewayName(rs.getString("gateway_type_name"));
                messageGatewayVO.setAccessFrom(rs.getString("access_from"));
                messageGatewayTypeList.add(messageGatewayVO);
            }
         
            }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[loadMessageGatewayTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[loadMessageGatewayTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	LogFactory.printLog(methodName, "Exiting:messageGatewayTypeList size=" + messageGatewayTypeList.size(), log);
            
        }
        return messageGatewayTypeList;
    }

    /**
     * This method is same as that of loadMessageGatewayTypeList but only store
     * the Gateway Type in ArrayList
     * 
     * @param conn
     * @param pcategoryCode
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadMessageGatewayTypeListForCategory(Connection conn, String pcategoryCode) throws BTSLBaseException {
        final String methodName = "loadMessageGatewayTypeListForCategory";
        LogFactory.printLog(methodName, "Entered: with p_categoryCode=" + pcategoryCode, log);
       
        
        ArrayList messageGatewayTypeList = null;
        
            StringBuilder selectQuery = new StringBuilder(" SELECT gateway_type  ");
            selectQuery.append(" FROM category_req_gtw_types  ");
            selectQuery.append(" WHERE category_code=? ");
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query=" + selectQuery);
            }

           try (PreparedStatement pstmtSelect = conn.prepareStatement(selectQuery.toString());)
           {
            pstmtSelect.setString(1, pcategoryCode);
           try( ResultSet rs = pstmtSelect.executeQuery();)
           {
            messageGatewayTypeList = new ArrayList();
            while (rs.next()) {
                messageGatewayTypeList.add(rs.getString("gateway_type"));
            }
           }
           }
         catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[loadMessageGatewayTypeListForCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[loadMessageGatewayTypeListForCategory]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	LogFactory.printLog(methodName, "Exiting:messageGatewayTypeList size=" + messageGatewayTypeList.size(), log);
          
        }
        return messageGatewayTypeList;
    }

    /**
     * This method loadMessageGatewayTypeListForCategory return a map having
     * each category associated with its gatewayTypes
     * 
     * @return HashMap<String, ArrayList<String>>
     * @throws BTSLBaseException
     */
    public HashMap<String, ArrayList<String>> loadMessageGatewayTypeListForCategory() throws BTSLBaseException {
        final String methodName = "loadMessageGatewayTypeListForCategory";
        LogFactory.printLog(methodName, "Entered", log);
        
        PreparedStatement pstmtSelect = null;
        Connection con = null;
        ResultSet rs = null;
        String key = null;
        String oldKey = null;
        ArrayList<String> messageGatewayTypeForCategoryList = null;
        HashMap<String, ArrayList<String>> messageGatewayListForCategoryMap = new HashMap<String, ArrayList<String>>();
        try {
            StringBuilder selectQuery = new StringBuilder(" SELECT category_code, gateway_type  ");
            selectQuery.append(" FROM category_req_gtw_types  ");
            selectQuery.append(" ORDER BY category_code");
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query=" + selectQuery);
            }
            con = OracleUtil.getSingleConnection();
            pstmtSelect = con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                key = rs.getString("category_code");
                if (oldKey == null) {
                    messageGatewayTypeForCategoryList = new ArrayList<String>();
                } else if (!oldKey.equals(key)) {
                    messageGatewayListForCategoryMap.put(oldKey, messageGatewayTypeForCategoryList);
                    messageGatewayTypeForCategoryList = new ArrayList<String>();
                }
                messageGatewayTypeForCategoryList.add(rs.getString("gateway_type"));
                oldKey = key;
            }
            if (oldKey != null && oldKey.equals(key)) {
                messageGatewayListForCategoryMap.put(oldKey, messageGatewayTypeForCategoryList);
            }
            return messageGatewayListForCategoryMap;
        }// End of try
        catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[loadMessageGatewayTypeListForCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[loadMessageGatewayTypeListForCategory]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(con);
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	LogFactory.printLog(methodName, "Exiting: messageGatewayListForCategoryMap size= " + messageGatewayListForCategoryMap.size(), log);
            
        }// end of finally
    }

    /**
     * Method loadCategoryRequestGwType.
     * This method is used to load category details according to domain code
     * from CATEGORY_REQ_GTW_TYPES Table
     * 
     * @param conn
     *            Connection
     * @param pcategoryCode
     *            String
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList<String> loadCategoryRequestGwType(Connection conn, String pcategoryCode) throws BTSLBaseException {
        final String methodName = "loadCategoryRequestGwType";
        LogFactory.printLog(methodName, "Entered p_categoryCode=" + pcategoryCode, log);
        

        
        
        ArrayList<String> categoryGWTypeList = new ArrayList<String>();
        StringBuilder strBuff = new StringBuilder(" SELECT distinct C.GATEWAY_TYPE ");
        strBuff.append(" FROM CATEGORY_REQ_GTW_TYPES C ");
        strBuff.append(" WHERE C.CATEGORY_CODE = ? ");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);){
            
            pstmtSelect.setString(1, pcategoryCode);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                categoryGWTypeList.add((String) rs.getString("GATEWAY_TYPE"));
            }
        }
        }

        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[loadCategoryRequestGwType]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[loadCategoryRequestGwType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
        	
        	
        	LogFactory.printLog(methodName,  "Exiting size=" + categoryGWTypeList.size(), log);
           
        }

        return categoryGWTypeList;
    }
    
    
    /**
     * Method for inserting MessageGatewayType with category code in
     * gategory_req_gtw_type table
     * Method:addCategoryReqGtwTypesListFromRest
     * 
     * @param conn java.sql.Connection
     * @param pcategoryCode   String
     * @param pmessageGatewayTypeList java.uril.ArrayList
     * @return insertCount int
     * @exception BTSLBaseException
     */
    public int addCategoryReqGtwTypesListFromRest(Connection conn, String pcategoryCode, ArrayList pmessageGatewayTypeList) throws BTSLBaseException {
       
//        MessageGatwayVORest messageGatewayVO = null;
        int insertCount = 0;
        final String methodName = "addCategoryReqGtwTypesListFromRest";
        LogFactory.printLog(methodName, "Entered: p_categoryCode= " + pcategoryCode + " ,p_messageGatewayTypeList Size= " + pmessageGatewayTypeList.size(), log);
        
        try {
            int count = 0;
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO category_req_gtw_types (category_code,");
            strBuff.append(" gateway_type) values (UPPER(?),?)");
            String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
           try(PreparedStatement psmtInsert = conn.prepareStatement(insertQuery);)
           {
            for (int i = 0; i < pmessageGatewayTypeList.size(); i++) {
            	String gatwayType = (String) pmessageGatewayTypeList.get(i);
                psmtInsert.setString(1, pcategoryCode);
                psmtInsert.setString(2, gatwayType); // use lowercase i here
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the insert
                if (insertCount > 0) {
                    count++;
                }
            }
            if (count == pmessageGatewayTypeList.size()) {
                insertCount = 1;
            } else {
                insertCount = 0;
            }

        } 
        }// end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[addCategoryReqGtwTypesList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryReqGtwTypeDAO[addCategoryReqGtwTypesList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName,  PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        } // end of catch
        finally {
        	
        	LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, log);
           
            
        } // end of finally
        return insertCount;
    }

}
