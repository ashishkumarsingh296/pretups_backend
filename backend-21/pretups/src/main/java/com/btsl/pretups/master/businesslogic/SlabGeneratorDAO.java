/*
 * #SlabGeneratorDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 4, 2005 Amit Ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

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
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
/**
 * 
 * class  SlabGeneratorDAO
 *
 */
public class SlabGeneratorDAO {
    private Log log = LogFactory.getFactory().getInstance(SlabGeneratorDAO.class.getName());

    /**
     * Method loadSlabDates.
     * This method is used to load the present and future slab dates
     * 
     * @param conn
     *            Connection
     * @param p_userVO
     *            UserVO
     * @return map HashMap
     * @throws BTSLBaseException
     */

    public ArrayList loadSlabDates(Connection conn, String networkCode, int previousMonths, int forwardMonths) throws BTSLBaseException {
        final String methodName = "loadSlabDates";
        LogFactory.printLog(methodName, "Entered params networkCode=" + networkCode , log);
       

        PreparedStatement pstmtSelect = null;
         
        ArrayList slabDateList = null;
        SlabGeneratorQry slabGeneratorQry = (SlabGeneratorQry)ObjectProducer.getObject(QueryConstants.SLAB_GENERATOR_QRY, QueryConstants.QUERY_PRODUCER);
       

        try {
            pstmtSelect = slabGeneratorQry.loadSlabDatesQry(conn, networkCode, previousMonths, forwardMonths);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            slabDateList = new ArrayList();
            ListValueVO listVO = null;
            while (rs.next()) {
                listVO = new ListValueVO(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("slab_date"))), rs.getString("service_type") + ":" + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("slab_date"))));
                slabDateList.add(listVO);
            }
        }
        }

        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[loadSlabDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[loadSlabDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");

        } finally {
        	
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting slabDateList.size=" + slabDateList.size(), log);
           
        }

        return slabDateList;
    }

    /**
     * Method loadSlabDetails.
     * This method is used to load the slab details
     * 
     * @param conn
     *            Connection
     * @param String
     *            pnetworkCode
     * @param pslabVO
     *            SlabGeneratorVO
     * @param pslabDate
     * @return map HashMap
     * @throws BTSLBaseException
     */

    public LinkedHashMap loadSlabDetails(Connection conn, SlabGeneratorVO pslabVO, String pnetworkCode, String pslabDate) throws BTSLBaseException {
        final String methodName = "loadSlabDetails";
        LogFactory.printLog(methodName, "Entered params pslabVO=" + pslabVO + "pnetworkCode=" + pnetworkCode + "pslabDate=" + pslabDate, log);
         
        
        ArrayList slabList = null;
        StringBuilder strBuff = new StringBuilder("SELECT slab_id,from_range,to_range,service_type,slab_date FROM ");
        strBuff.append("SLAB_MASTER WHERE service_type=? AND network_code=? AND slab_date=? ORDER BY slab_date,from_range");
        String sqlSelect = strBuff.toString();
        LinkedHashMap map = null;
        LogFactory.printLog(methodName, "Select Query= " + sqlSelect, log);
        

        try(PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, pslabVO.getServiceType());
            pstmtSelect.setString(2, pnetworkCode);
            pstmtSelect.setDate(3, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pslabDate)));
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            SlabGeneratorVO slabVO = null;
            map = new LinkedHashMap();
            Date currDate = new Date();
            while (rs.next()) {

                slabVO = new SlabGeneratorVO();
                slabVO.setSlabid(rs.getString("slab_id"));
                slabVO.setFromRange(rs.getLong("from_range"));
                slabVO.setToRange(rs.getLong("to_range"));
            
                slabVO.setServiceType(rs.getString("service_type"));
                
               
                // Only Future slabs can be changed,past slabs can't be modified

               
               

               
                slabVO.setSlabDate (BTSLUtil.getDateStringFromDate(rs.getDate("slab_date")));

                
                if (currDate.after(BTSLUtil.getDateFromDateString(slabVO.getSlabDate()))) {
                    slabVO.setRadioRequired("false");
                } else {
                    slabVO.setRadioRequired("true");
                }
                //Added this code for persian implementation of the date
                slabVO.setSlabDate (BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("slab_date"))));

                if (map.containsKey(slabVO.getSlabDate())) {
                    slabList.add(slabVO);
                    map.put(slabVO.getSlabDate(), slabList);
                } else {
                    slabList = new ArrayList();
                    slabList.add(slabVO);
                    map.put(slabVO.getSlabDate(), slabList);
                }

            }
        }
        }
        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[loadSlabDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[loadSlabDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");

        } finally {
        	
           LogFactory.printLog(methodName, "Exiting", log);
            
        }

        return map;
    }

    /**
     * Method addSlabDetails.
     * This method is used to add the slab details
     * 
     * @param conn
     *            Connection
     * @param arrVO
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     */

    public int addSlabDetails(Connection conn, ArrayList arrVO) throws BTSLBaseException {
        final String methodName = "addSlabDetails";
        LogFactory.printLog(methodName, "Entered:p_arrVO=" + arrVO, log);
        

      
        int insertCount = -1;
        int insertListSize;
        try {
            insertListSize = arrVO.size();
            if (insertListSize > 0) {
                int count = 0;
                StringBuilder insertQuery = new StringBuilder("INSERT INTO slab_master(slab_id,from_range,to_range,service_type,");
                insertQuery.append("slab_date,network_code,created_on,created_by,modified_on,modified_by) VALUES(?,?,?,?,?,?,?,?,?,?)");
                LogFactory.printLog(methodName,  "Query insert Query:" + insertQuery.toString(), log);
              
                try(PreparedStatement pstmtInsert = conn.prepareStatement(insertQuery.toString());)
                {
                for (int i = 0; i < insertListSize; i++) {
                    SlabGeneratorVO slabVO = (SlabGeneratorVO) arrVO.get(i);
				pstmtInsert.setString(1,slabVO.getSlabid());
				pstmtInsert.setLong(2, BTSLUtil.parseDoubleToLong(slabVO.getFromRange()));
				pstmtInsert.setLong(3, BTSLUtil.parseDoubleToLong(slabVO.getToRange()));
				pstmtInsert.setString(4,slabVO.getServiceType());
                    pstmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getDateFromDateString(slabVO.getSlabDate())));
                    pstmtInsert.setString(6, slabVO.getNetworkCode());
                    pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(slabVO.getCreatedOn()));
                    pstmtInsert.setString(8, slabVO.getCreatedBy());
                    pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(slabVO.getModifiedOn()));
                    pstmtInsert.setString(10, slabVO.getModifiedBy());
                    insertCount = pstmtInsert.executeUpdate();
                    pstmtInsert.clearParameters();
                    // check the status of the insert
                    if (insertCount > 0) {
                        count++;
                    }
                }
                if (count == arrVO.size()) {
                    insertCount = 1;
                } else {
                    insertCount = 0;
                }
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[addSlabDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[addSlabDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
           	LogFactory.printLog(methodName, "Exiting:updateCount=" + insertCount, log);
           
        }
        return insertCount;
    }

    /**
     * Method isExistsSlabRange.
     * This method is used check wether the range already exists in the database
     * corresponding to the
     * slab date selected
     * 
     * @param conn
     *            Connection
     * @param p_arrVO
     *            ArrayList
     * @param slabVO
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExistsSlabRange(Connection conn, SlabGeneratorVO slabVO) throws BTSLBaseException {
        final String methodName = "isExistsSlabRange";
        LogFactory.printLog(methodName, "Entered:slabVO=" + slabVO, log);
       

         
        boolean found = false;
        ResultSet rs = null;

        try {

            StringBuilder isExistsQuery = new StringBuilder("SELECT 1 FROM slab_master WHERE (from_range<=? AND to_range>=?) AND slab_date=? AND slab_id <>?");
            // toRange>=from_range AND to_range>=fromRange
            LogFactory.printLog(methodName, "Query isExistsQuery Query:" + isExistsQuery.toString(), log);
            try(PreparedStatement pstmtSelect = conn.prepareStatement(isExistsQuery.toString());)
            {
            pstmtSelect.setDouble(1, slabVO.getToRange());
            pstmtSelect.setDouble(2, slabVO.getFromRange());
            pstmtSelect.setDate(3, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(slabVO.getSlabDate())));
            pstmtSelect.setString(4, slabVO.getSlabid());

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }

        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[isExistsSlabRange]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[isExistsSlabRange]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
           LogFactory.printLog(methodName, "Exiting:found=" + found, log);
            
        }
        return found;

    }

    /**
     * Method modifySlabDetails.
     * This method is used to add the slab details
     * 
     * @param conn
     *            Connection
     * @param p_arrVO
     *            ArrayList
     * @param slabVO
     * @return integer
     * @throws BTSLBaseException
     */

    public int modifySlabDetails(Connection conn, SlabGeneratorVO slabVO) throws BTSLBaseException {
        final String methodName = "modifySlabDetails";
        LogFactory.printLog(methodName, "Entered:p_slabVO=" + slabVO, log);
        

         
        int updateCount = -1;

        try {
            StringBuilder updateQuery = new StringBuilder("UPDATE slab_master SET from_range=?,to_range=?,modified_on=?,");
            updateQuery.append("modified_by=? WHERE slab_id=?");
            LogFactory.printLog(methodName, "Query update Query:" + updateQuery.toString(), log);
            try(PreparedStatement pstmtUpdate = conn.prepareStatement(updateQuery.toString());)
            {
            pstmtUpdate.setDouble(1, slabVO.getFromRange());
            pstmtUpdate.setDouble(2, slabVO.getToRange());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(slabVO.getModifiedOn()));
            pstmtUpdate.setString(4, slabVO.getModifiedBy());
            pstmtUpdate.setString(5, slabVO.getSlabid());

            updateCount = pstmtUpdate.executeUpdate();

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[modifySlabDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[modifySlabDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	LogFactory.printLog(methodName, "Exiting:updateCount=" + updateCount, log);
           
        }
        return updateCount;

    }

    /**
     * Method deleteSlabs.
     * This method is used to delete slabs corresponding to slabdate,servicetype
     * and networkcode
     * 
     * @param conn
     *            Connection
     * @param serviceType
     *            String
     * @param slabDate
     *            Date
     * @param networkCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean deleteSlabs(Connection conn, String serviceType, String slabDate, String networkCode) throws BTSLBaseException {
        final String methodName = "deleteSlabs";
        LogFactory.printLog(methodName, "Entered:p_serviceType=" + serviceType + " p_slabDate=" + slabDate + " p_networkCode=" + networkCode, log);
      
         
        int deleteCount = -1;
        try {
            StringBuilder deleteQuery = new StringBuilder("DELETE FROM slab_master WHERE service_type=?");
            deleteQuery.append(" AND slab_date=? AND network_code=?");
            LogFactory.printLog(methodName, "Delete Query:" + deleteQuery.toString(), log);
            try(PreparedStatement pstmtSelect = conn.prepareStatement(deleteQuery.toString());)
            {
            pstmtSelect.setString(1, serviceType);
            pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(slabDate)));
            pstmtSelect.setString(3, networkCode);
            deleteCount = pstmtSelect.executeUpdate();
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[deleteSlabs]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SlabGeneratorDAO[deleteSlabs]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.processing");
        } finally {
        	
        	LogFactory.printLog(methodName, "Exiting:deleteCount=" + deleteCount, log);
            
        }
        if (deleteCount > 0) {
            return true;
        } else {
            return false;
        }
    }

}