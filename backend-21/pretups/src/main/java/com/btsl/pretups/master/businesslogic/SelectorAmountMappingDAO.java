package com.btsl.pretups.master.businesslogic;

/**
 * SelectorAmountMappingDAO.java
 * Copyright(c) 2011, MComviva technologies Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Aman Jain 11/NOV/2013 Initial Creation
 * 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class SelectorAmountMappingDAO {
    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadSelectNameList.
     * This method is to Load all the record of the specified service type.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     * @author aman.jain
     */
    @SuppressWarnings("unchecked")
    public ArrayList loadSelectorAmountList(Connection p_con) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadSelectorAmountList", "Entered ");
        }

        final String METHOD_NAME = "loadSelectorAmountList";
        ArrayList serviceAmountList = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SelectorAmountMappingVO selectorAmountMappingVO = null;
        StringBuffer strBuff = new StringBuffer("select sp.service_type ,sp.selector_code,sp.selector_name,sp.amount,sp.modified_allowed,sp.status,st.name ");
        strBuff.append("from selector_amount_mapping sp ,service_type st,service_type_selector_mapping stsm ");
        strBuff.append(" where sp.service_type=st.service_type and sp.service_type=stsm.service_type ");
        strBuff.append(" and  sp.selector_code=stsm.selector_code and st.status=stsm.status and st.status=sp.status and sp.status=? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadSelectorAmountList", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, TypesI.YES);
            rs = pstmt.executeQuery();
            int radioIndex = 0;
            while (rs.next()) {
                selectorAmountMappingVO = new SelectorAmountMappingVO();
                selectorAmountMappingVO.setServiceType(rs.getString("service_type"));
                selectorAmountMappingVO.setSelectorCode(rs.getString("selector_code"));
                selectorAmountMappingVO.setSelectorName(rs.getString("selector_name"));
                selectorAmountMappingVO.setAmountStr(PretupsBL.getDisplayAmount(rs.getLong("amount")));
                selectorAmountMappingVO.setAmount(rs.getLong("amount"));
                selectorAmountMappingVO.setModifiedAllowed(rs.getString("modified_allowed"));
                if (selectorAmountMappingVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_YES)) {
                    selectorAmountMappingVO.setAllowAction("Yes");
                } else if (selectorAmountMappingVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.NO)) {
                    selectorAmountMappingVO.setAllowAction("No");
                }
                selectorAmountMappingVO.setRadioIndex(radioIndex);
                selectorAmountMappingVO.setServiceName(rs.getString("name"));
                if (rs.getString("status").equalsIgnoreCase(PretupsI.YES)) {
                    selectorAmountMappingVO.setStatus("Yes");
                } else if (rs.getString("status").equalsIgnoreCase(PretupsI.NO)) {
                    selectorAmountMappingVO.setStatus("No");
                }
                radioIndex++;
                serviceAmountList.add(selectorAmountMappingVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadSelectorAmountList", " SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[loadSelectorAmountList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSelectorAmountList", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadSelectorAmountList", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[loadSelectorAmountList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSelectorAmountList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadSelectorAmountList", "Exited: Map size=");
            }

        }
        return serviceAmountList;
    }

    /**
     * Method loadServiceTypeList.
     * This method is to Load all the record of the specified service type.
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author aman.jain
     */
    @SuppressWarnings("unchecked")
    public ArrayList loadServiceTypeList(Connection p_con, String p_networkCode, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceTypeList", "Entered p_networkCode=" + p_networkCode);
        }
        final String METHOD_NAME = "loadServiceTypeList";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList serviceTypeList = new ArrayList();
        try {
            StringBuffer selectQueryBuff = new StringBuffer("SELECT DISTINCT ST.service_type,ST.name FROM service_type ST,network_services NS");
            selectQueryBuff.append(" WHERE ST.service_type=NS.service_type AND NS.status<>'N' AND ST.status<>'N' AND NS.sender_network=? AND ST.module=? ORDER BY ST.name");
            if (_log.isDebugEnabled()) {
                _log.debug("loadServiceTypeList", "Query selectQueryBuff:" + selectQueryBuff.toString());
            }
            pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_module);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                serviceTypeList.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadServiceTypeList", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[loadServiceTypeList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadServiceTypeList", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[loadServiceTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadServiceTypeList", "Exiting serviceTypeList.size:" + serviceTypeList.size());
            }
        }// end of final
        return serviceTypeList;
    }

    /**
     * Method addSelectorAmountDetails.
     * This method is to insert all the record from VO to database
     * 
     * @param p_con
     * @param p_amountMappingVO
     * @return int
     * @throws BTSLBaseException
     * @author aman.jain
     */
    public int addSelectorAmountDetails(Connection p_con, SelectorAmountMappingVO p_amountMappingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addSelectorAmountDetails", "Entered:p_amountMappingVO=" + p_amountMappingVO);
        }

        final String METHOD_NAME = "addSelectorAmountDetails";
        PreparedStatement pstmtInsert = null;
        int insertCount = -1;
        try {
            StringBuffer insertQuery = new StringBuffer("INSERT INTO selector_amount_mapping(service_type,selector_code,selector_name,status,amount,");
            insertQuery.append("modified_allowed,created_on,modified_on,modified_by,created_by) VALUES(?,?,?,?,?,?,?,?,?,?)");
            if (_log.isDebugEnabled()) {
                _log.debug("addSelectorAmountDetails", "Query insert Query:" + insertQuery.toString());
            }
            pstmtInsert = p_con.prepareStatement(insertQuery.toString());
            pstmtInsert.setString(1, p_amountMappingVO.getServiceType());
            pstmtInsert.setString(2, p_amountMappingVO.getSelectorCode());
            pstmtInsert.setString(3, p_amountMappingVO.getSelectorName());
            pstmtInsert.setString(4, p_amountMappingVO.getStatus());
            pstmtInsert.setLong(5, p_amountMappingVO.getAmount());
            pstmtInsert.setString(6, p_amountMappingVO.getModifiedAllowed());
            pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_amountMappingVO.getCreatedOn()));
            pstmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_amountMappingVO.getModifiedOn()));
            pstmtInsert.setString(9, p_amountMappingVO.getModifiedBy());
            pstmtInsert.setString(10, p_amountMappingVO.getCreatedBy());
            insertCount = pstmtInsert.executeUpdate();
            pstmtInsert.clearParameters();
            // check the status of the insert
        } catch (SQLException sqe) {
            _log.error("addSelectorAmountDetails", "SQLException:" + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[addSelectorAmountDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addSelectorAmountDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("addSelectorAmountDetails", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[addSelectorAmountDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addSelectorAmountDetails", "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addSlabDetails", "Exiting:updateCount=" + insertCount);
            }
        }
        return insertCount;
    }

    /**
     * Method isSelectorAmountDetailsExist.
     * This method is to check whether the entry is already present in the
     * database
     * 
     * @param p_con
     * @param p_amountMappingVO
     * @return boolean
     * @throws BTSLBaseException
     * @author aman.jain
     */
    public boolean isSelectorAmountDetailsExist(Connection p_con, SelectorAmountMappingVO p_mappingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("isSelectorAmountDetailsExist", "Entered params p_mappingVO=" + p_mappingVO);
        }

        final String METHOD_NAME = "isSelectorAmountDetailsExist";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer(" select 1 from selector_amount_mapping where service_type=? and  selector_code =? and status<>? ");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("isSelectorAmountDetailsExist", "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_mappingVO.getServiceType());
            pstmtSelect.setString(2, p_mappingVO.getSelectorCode());
            pstmtSelect.setString(3, PretupsI.NO);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.error("isSelectorAmountDetailsExist", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[isSelectorAmountDetailsExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isSelectorAmountDetailsExist", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("isSelectorAmountDetailsExist", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[isSelectorAmountDetailsExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isSelectorAmountDetailsExist", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("isSelectorAmountDetailsExist", "Exiting isExists found=" + found);
            }
        }
        return found;
    }

    /**
     * Method updateSelectorAmountMapping.
     * This method is to update the product details present in the database
     * 
     * @param p_con
     * @param p_amountMappingVO
     * @return boolean
     * @throws BTSLBaseException
     * @author aman.jain
     */
    public int updateSelectorAmountMapping(Connection p_con, SelectorAmountMappingVO p_AmountMappingVO) throws BTSLBaseException {
        final String METHOD_NAME = " updateSelectorAmountMapping";
        if (_log.isDebugEnabled()) {
            _log.debug("updateSelectorAmountMapping", "Entering VO=" + p_AmountMappingVO);
        }
        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            StringBuffer updateQueryBuff = new StringBuffer("UPDATE selector_amount_mapping SET");
            updateQueryBuff.append(" amount=?, modified_allowed=?, ");
            updateQueryBuff.append("modified_on=?,modified_by=? WHERE selector_code=? and service_type=? ");
            String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setLong(1, p_AmountMappingVO.getAmount());
            pstmtUpdate.setString(2, p_AmountMappingVO.getModifiedAllowed());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_AmountMappingVO.getModifiedOn()));
            pstmtUpdate.setString(4, p_AmountMappingVO.getModifiedBy());
            pstmtUpdate.setString(5, p_AmountMappingVO.getSelectorCode());
            pstmtUpdate.setString(6, p_AmountMappingVO.getServiceType());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
            _log.error("updateSelectorAmountMapping", " SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[updateSelectorAmountMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateSelectorAmountMapping", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateSelectorAmountMapping", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[updateSelectorAmountMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSelectorAmountMapping", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateSelectorAmountMapping", "Exiting updateCount " + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method deleteSelectorAmountMapping.
     * This method is to delete the product details present in the database
     * 
     * @param p_con
     * @param p_domainVO
     * @return boolean
     * @throws BTSLBaseException
     * @author aman.jain
     */
    public int deleteSelectorAmountMapping(Connection p_con, SelectorAmountMappingVO p_domainVO) throws BTSLBaseException {
        final String METHOD_NAME = "deleteSelectorAmountMapping";
        if (_log.isDebugEnabled()) {
            _log.debug("deleteSelectorAmountMapping", "Entering VO=" + p_domainVO);
        }
        int deleteCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            StringBuffer updateQueryBuff = new StringBuffer("delete from selector_amount_mapping ");
            updateQueryBuff.append(" WHERE selector_code=? and service_type=? ");
            String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, p_domainVO.getSelectorCode());
            pstmtUpdate.setString(2, p_domainVO.getServiceType());
            deleteCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
            _log.error("deleteSelectorAmountMapping", " SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[deleteSelectorAmountMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteSelectorAmountMapping", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("deleteSelectorAmountMapping", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[deleteSelectorAmountMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteSelectorAmountMapping", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("deleteSelectorAmountMapping", "Exiting deleteCount " + deleteCount);
            }
        }
        return deleteCount;
    }

    /**
     * Method loadSelectorAmountDetails.
     * This method is to load the product amount details present in the database
     * 
     * @param p_con
     * @param p_domainVO
     * @return boolean
     * @throws BTSLBaseException
     * @author aman.jain
     */
    public SelectorAmountMappingVO loadSelectorAmountDetails(Connection p_con, String p_serviceCode, String p_subservice) throws BTSLBaseException {
        final String METHOD_NAME = "loadSelectorAmountDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadSelectorAmountDetails", "Entered p_serviceCode" + p_serviceCode + "p_subservice" + p_subservice);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SelectorAmountMappingVO selectorAmountMappingVO = null;
        StringBuffer strBuff = new StringBuffer("select sp.service_type ,sp.selector_code ");
        strBuff.append(" ,sp.selector_name,sp.amount,sp.modified_allowed,sp.status from selector_amount_mapping sp ");
        strBuff.append(" where sp.service_type=? and sp.status= ? and sp.selector_code= ? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadSelectorAmountDetails", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_serviceCode);
            pstmt.setString(2, TypesI.YES);
            pstmt.setString(3, p_subservice);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                selectorAmountMappingVO = new SelectorAmountMappingVO();
                selectorAmountMappingVO.setServiceType(rs.getString("service_type"));
                selectorAmountMappingVO.setSelectorCode(rs.getString("selector_code"));
                selectorAmountMappingVO.setSelectorName(rs.getString("selector_name"));
                selectorAmountMappingVO.setAmount(rs.getLong("amount"));
                selectorAmountMappingVO.setAmountStr(PretupsBL.getDisplayAmount(rs.getLong("amount")));
            }
        } catch (SQLException sqe) {
            _log.error("loadSelectorAmountDetails", " SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[loadSelectorAmountDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSelectorAmountDetails", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadSelectorAmountDetails", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[loadSelectorAmountDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSelectorAmountDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
            	if(selectorAmountMappingVO!=null)
                _log.debug("loadSelectorAmountDetails", "Exited: selectorAmountMappingVO=" + selectorAmountMappingVO.toString());
            }
        }
        return selectorAmountMappingVO;
    }
    
    
    
    
    /**
     * Method loadSelectorAmountDetails.
     * This method is to load the product amount details present in the database
     * 
     * @param p_con
     * @param p_domainVO
     * @return boolean
     * @throws BTSLBaseException
     * @author aman.jain
     */
    public ArrayList<SelectorAmountMappingVO> loadSelectorAmountDetailsbyServiceType(Connection p_con, String selectorCode, String serviceType) throws BTSLBaseException {
        final String METHOD_NAME = "loadSelectorAmountDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadSelectorAmountDetails", "Entered p_serviceCode" + selectorCode + "p_subservice" + serviceType);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<SelectorAmountMappingVO> listSelector = new ArrayList<SelectorAmountMappingVO>();
        SelectorAmountMappingVO selectorAmountMappingVO = null;
        StringBuffer strBuff = new StringBuffer("select sp.service_type ,sp.selector_code ");
        strBuff.append(" ,sp.selector_name,sp.amount,sp.modified_allowed,sp.status,lk.lookup_name as statusDec  , lm.lookup_name as modifyAllowedDesc  from selector_amount_mapping sp,lookups lk, lookups lm  ");
        strBuff.append(" where   lk.lookup_code =sp.status and lk.lookup_type ='LKTST'  and   lm.lookup_code =sp.modified_allowed and lm.lookup_type ='LKTST'  ");
        strBuff.append("  and    sp.status= ? " );
       	strBuff.append(" AND  sp.service_type=( CASE ?  WHEN 'ALL' THEN sp.service_type  ELSE ?    END)   ");
       	strBuff.append(" AND  sp.selector_code=( CASE ?  WHEN 'ALL' THEN sp.selector_code  ELSE ?    END)   ");
        
        
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadSelectorAmountDetails", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, TypesI.YES);
            pstmt.setString(2, serviceType);
            pstmt.setString(3, serviceType);
            pstmt.setString(4, selectorCode);
            pstmt.setString(5, selectorCode);
            
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                selectorAmountMappingVO = new SelectorAmountMappingVO();
                selectorAmountMappingVO.setServiceType(rs.getString("service_type"));
                selectorAmountMappingVO.setSelectorCode(rs.getString("selector_code"));
                selectorAmountMappingVO.setSelectorName(rs.getString("selector_name"));
                selectorAmountMappingVO.setAmount(rs.getLong("amount"));
                selectorAmountMappingVO.setAmountStr(PretupsBL.getDisplayAmount(rs.getLong("amount")));
                selectorAmountMappingVO.setStatus(rs.getString("statusDec"));
                selectorAmountMappingVO.setModifiedAllowed(rs.getString("modifyAllowedDesc"));
                listSelector.add(selectorAmountMappingVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadSelectorAmountDetails", " SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[loadSelectorAmountDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSelectorAmountDetails", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadSelectorAmountDetails", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelectorAmountMappingDAO[loadSelectorAmountDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSelectorAmountDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
            	if(selectorAmountMappingVO!=null)
                _log.debug("loadSelectorAmountDetails", "Exited: selectorAmountMappingVO=" + selectorAmountMappingVO.toString());
            }
        }
        return listSelector;
    }
}
    
    
