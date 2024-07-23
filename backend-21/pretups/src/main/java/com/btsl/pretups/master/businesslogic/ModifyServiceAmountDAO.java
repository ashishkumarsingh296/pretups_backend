package com.btsl.pretups.master.businesslogic;

/**
 * @(#)ModifyServiceAmountDAO.java
 *                                 Copyright(c) 2011, Comviva technologies Ltd.
 *                                 All Rights Reserved
 * 
 * 
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Jasmine kaur FEB 3, 2011 Initital Creation
 * 
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
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
import com.btsl.pretups.common.PretupsI;
/**
 * 
 * Class ModifyServiceAmountDAO
 *
 */
public class ModifyServiceAmountDAO {
    /**
     * Commons Logging instance.
     */
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadSelectNameList.
     * This method is to Load all the record of the specified service type.
     * 
     * @param conn
     *            Connection
     * @param serviceCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSelectNameList(Connection conn, String serviceCode) throws BTSLBaseException {

        final String methodName = "loadSelectNameList";
        LogFactory.printLog(methodName, "Entered Map" + serviceCode, log);
      

        ArrayList serviceAmountList = new ArrayList();
       
        SelectorAmountMappingVO selectorAmountMappingVO = null;

        StringBuilder strBuff = new StringBuilder("select sp.service_type ,sp.selector_code ");
        strBuff.append(" ,sp.selector_name,sp.amount,sp.modified_allowed from selector_amount_mapping sp,service_type_selector_mapping sv ");
        strBuff.append(" where sp.selector_code = sv.selector_code and sv.service_type=? and sv.status= ? ");

        String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName,"QUERY sqlSelect=" + sqlSelect, log);
        

        try (PreparedStatement  pstmt = conn.prepareStatement(sqlSelect);){
           
            pstmt.setString(1, serviceCode);
            pstmt.setString(2, PretupsI.YES);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            int index = 0;
            while (rs.next()) {
                selectorAmountMappingVO = SelectorAmountMappingVO.getInstance();
                selectorAmountMappingVO.setServiceType(rs.getString("service_type"));
                selectorAmountMappingVO.setSelectorCode(rs.getString("selector_code"));
                selectorAmountMappingVO.setSelectorName(rs.getString("selector_name"));
                selectorAmountMappingVO.setAmount(rs.getLong("amount") / 100);
        
                selectorAmountMappingVO.setModifiedAllowed(rs.getString("modified_allowed"));
                if (selectorAmountMappingVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_YES)) {
                    selectorAmountMappingVO.setDisableAllow(false);
                    selectorAmountMappingVO.setAllowAction("Y");
                } else if (selectorAmountMappingVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_NO)) {
                    selectorAmountMappingVO.setDisableAllow(true);// to be
                                                                  // changed
                    selectorAmountMappingVO.setAllowAction("N");
                }
                selectorAmountMappingVO.setRowID("" + ++index);
                serviceAmountList.add(selectorAmountMappingVO);

            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, " SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyServiceAmountDAO[loadSelectNameList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyServiceAmountDAO[loadSelectNameList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
               	
            	LogFactory.printLog(methodName, "Exited: Map size=", log);
           

        }
        return serviceAmountList;
    }

    /**
     * Method updateSelectorAmount.
     * This method is to update the record of the Service Amount and modified
     * allowed status of the specified Sub service.
     * 
     * @param conn
     *            Connection
     * @param selectorList
     *            ArrayList
     * @return integer
     * @throws BTSLBaseException
     */
    public int updateSelectorAmount(Connection conn, ArrayList selectorList) throws BTSLBaseException {
        final String methodName = "updateSelectorAmount";
        LogFactory.printLog(methodName, "Entered:p_selectorList.size()=" + selectorList.size(), log);
       
        
        SelectorAmountMappingVO selectorAmountMappingVO = null;
        int updateCount = 0;
        try {
            StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE selector_amount_mapping SET modified_allowed=?,amount=?");
            updateQuery.append("WHERE selector_code=?");
            String query = updateQuery.toString();
            LogFactory.printLog(methodName, "Query=" + query, log);
          

           try(PreparedStatement pstmtUpdate = conn.prepareStatement(query);)
           {
              int selectorLists=selectorList.size();
            for (int i = 0; i <selectorLists ; i++) {
                selectorAmountMappingVO = (SelectorAmountMappingVO) selectorList.get(i);
                LogFactory.printLog(methodName,"Query=" + selectorList.get(i), log);
                pstmtUpdate.setString(1, selectorAmountMappingVO.getModifiedAllowed());
                pstmtUpdate.setLong(2, (selectorAmountMappingVO.getAmount()) * 100);
              
                pstmtUpdate.setString(3, selectorAmountMappingVO.getSelectorCode());

                updateCount += pstmtUpdate.executeUpdate();
                pstmtUpdate.clearParameters();
            }

        }
        }

        catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyServiceAmountDAO[updateSelectorAmount]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyServiceAmountDAO[updateSelectorAmount]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
         
          	LogFactory.printLog(methodName, "Exiting:return=" + updateCount, log);
           
        }
        return updateCount;

    }

}