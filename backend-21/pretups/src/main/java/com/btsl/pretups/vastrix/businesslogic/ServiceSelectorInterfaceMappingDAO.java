/*
 * @# ServiceSelectorInterfaceMappingDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Oct 12, 2011 Arvinder Singh Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2011 Comviva Technologies Pvt. Ltd.
 */
package com.btsl.pretups.vastrix.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;


public class ServiceSelectorInterfaceMappingDAO {

    private Log _log = LogFactory.getLog(ServiceSelectorInterfaceMappingDAO.class.getName());
    private ServiceSelectorInterfaceMappingQry serviceSelectorInterfaceMappingQry=(ServiceSelectorInterfaceMappingQry)ObjectProducer.getObject(QueryConstants.SEVICE_SELECTOR_INTERFACE_MAPP_QRY,QueryConstants.QUERY_PRODUCER);
    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @author arvinder.singh
     * @return
     */
    public ArrayList loadServiceTypes(Connection p_con, String p_srvcType) throws BTSLBaseException {
        final String methodName = "loadServiceTypes";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        ArrayList serviceList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        StringBuilder srvcList = new StringBuilder();
        try {
            if (!p_srvcType.contains(",")) {
                p_srvcType = p_srvcType + ",";
            }
            String sevcType[] = p_srvcType.split(",");
            for (int i = 0, j = sevcType.length; i < j; i++) {
                srvcList.append("'");
                srvcList.append(sevcType[i]);
                srvcList.append("',");
            }
            String srvcTypeList = srvcList.substring(0, srvcList.length() - 1);
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT service_type,name ");
            selectQuery.append(" FROM service_type ");
            selectQuery.append(" WHERE external_interface='Y' and status <> 'N' and service_type IN (" + srvcTypeList + ") order by UPPER(name)");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                serviceList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceTypes]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceTypes]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:serviceList size=" + serviceList.size());
            }
        }

        return serviceList;
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @author arvinder.singh
     * @return
     */
    public ArrayList loadServiceProductList(Connection p_con, String p_serviceType) throws BTSLBaseException {
        final String methodName = "loadServiceProductList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        ArrayList productList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        try {
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT selector_code,selector_name ");
            selectQuery.append(" FROM service_type_selector_mapping ");
            selectQuery.append(" WHERE service_type=? and  status <> 'N'  order by UPPER(selector_name)");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_serviceType);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("selector_name"), rs.getString("selector_code"));
                productList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceProductList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceProductList]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("productList", "Exiting:productList size=" + productList.size());
            }
        }

        return productList;
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @author arvinder.singh
     * @return
     */
    public ArrayList loadInterfaceList(Connection p_con, String p_interfaceType) throws BTSLBaseException {
        final String methodName = "loadInterfaceList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_interfaceType:" + p_interfaceType);
        }
        ArrayList interfaceList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInterfaceIdSelect = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String interfaceTypeId = null;
        ListValueVO listValueVO = null;
        try {
            StringBuilder selectInterfaceIdQuery = new StringBuilder();
            StringBuilder selectQuery = new StringBuilder();

            selectInterfaceIdQuery.append("SELECT interface_type_id,interface_name ");
            selectInterfaceIdQuery.append(" FROM interface_types ");
            selectInterfaceIdQuery.append(" WHERE interface_type_id=? ");

            selectQuery.append("SELECT interface_id,interface_description ");
            selectQuery.append(" FROM interfaces ");
            selectQuery.append(" WHERE interface_type_id=? and status='Y' ");

            if (_log.isDebugEnabled()) {
                _log.debug("selectInterfaceIdQuery", "Query=" + selectInterfaceIdQuery);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("selectQuery", "Query=" + selectQuery);
            }

            pstmtInterfaceIdSelect = p_con.prepareStatement(selectInterfaceIdQuery.toString());
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());

            pstmtInterfaceIdSelect.setString(1, p_interfaceType);
            rs = pstmtInterfaceIdSelect.executeQuery();

            while (rs.next()) {
                interfaceTypeId = rs.getString("interface_type_id");
                pstmtSelect.setString(1, interfaceTypeId);
                rs1 = pstmtSelect.executeQuery();
                while (rs1.next()) {
                    listValueVO = new ListValueVO(rs1.getString("interface_description"), rs1.getString("interface_id"));
                    interfaceList.add(listValueVO);
                }
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadInterfaceList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadInterfaceList]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (rs1!= null){
        			rs1.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	try{
        		if (pstmtInterfaceIdSelect!= null){
        			pstmtInterfaceIdSelect.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:interfaceList size=" + interfaceList.size());
            }
        }

        return interfaceList;
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @author arvinder.singh
     * @return
     */
    public ArrayList loadInterfaceForModify(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadInterfaceForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        ArrayList interfaceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ListValueVO listValueVO = null;
        try {
        	
			String selectQuery=serviceSelectorInterfaceMappingQry.loadInterfaceForModifyQry();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("interface_description"), rs.getString("interface_id"));
                interfaceTypeList.add(listValueVO);

            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadInterfaceForModify]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadInterfaceForModify]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {

        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:interfaceTypeList size=" + interfaceTypeList.size());
            }
        }

        return interfaceTypeList;
    }

    /**
     * @param p_con
     * @param p_voList
     * @return
     * @throws BTSLBaseException
     * @author arvinder.singh
     */
    public int addPrdServiceInterfaceMapping(Connection p_con, ArrayList p_voList) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;

        final String methodName = "addPrdServiceInterfaceMapping";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_voList List Size= " + p_voList.size());
        }
        try {

            if (p_voList != null) {
                StringBuilder insertQueryBuff = new StringBuilder();
                insertQueryBuff.append("INSERT INTO svc_setor_intfc_mapping (srv_selector_interface_id,service_type,selector_code,network_code, ");
                insertQueryBuff.append("interface_id,method_type,created_on,created_by,modified_on,modified_by,action,prefix_id ) ");
                insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                String insertQuery = insertQueryBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Insert query:" + insertQuery);
                }

                psmtInsert = p_con.prepareStatement(insertQuery);
                ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;

                for (int i = 0, j = p_voList.size(); i < j; i++) {
                    serviceSelectorInterfaceMappingVO = (ServiceSelectorInterfaceMappingVO) p_voList.get(i);

                    psmtInsert.setString(1, String.valueOf(IDGenerator.getNextID(PretupsI.SERVICE_INTERFACE_MAPPING_ID_TYPE, TypesI.ALL)));
                    psmtInsert.setString(2, serviceSelectorInterfaceMappingVO.getServiceType());
                    psmtInsert.setString(3, serviceSelectorInterfaceMappingVO.getSelectorCode());
                    psmtInsert.setString(4, serviceSelectorInterfaceMappingVO.getNetworkCode());
                    psmtInsert.setString(5, serviceSelectorInterfaceMappingVO.getInterfaceID());
                    psmtInsert.setString(6, serviceSelectorInterfaceMappingVO.getMethodType());
                    psmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(serviceSelectorInterfaceMappingVO.getCreatedOn()));
                    psmtInsert.setString(8, serviceSelectorInterfaceMappingVO.getCreatedBy());
                    psmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(serviceSelectorInterfaceMappingVO.getModifiedOn()));
                    psmtInsert.setString(10, serviceSelectorInterfaceMappingVO.getModifiedBy());
                    psmtInsert.setString(11, serviceSelectorInterfaceMappingVO.getAction());
                    psmtInsert.setLong(12, serviceSelectorInterfaceMappingVO.getPrefixId());
                    insertCount = psmtInsert.executeUpdate();
                    psmtInsert.clearParameters();
                    // check the status of the update
                    if (insertCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }

            }
        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[addPrdServiceInterfaceMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[addPrdServiceInterfaceMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try{
                if (psmtInsert!= null){
                	psmtInsert.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @author arvinder.singh
     * @return
     */
    public ArrayList loadServiceInterfaceMappingRuleList(Connection p_con, String p_networkCode, String p_serviceType) throws BTSLBaseException {
        final String methodName = "loadServiceInterfaceMappingRuleList()";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_networkCode=" + p_networkCode + ",p_serviceType=" + p_serviceType);
        }
        PreparedStatement pstmt = null;
        int size=0;
        ResultSet rs = null;
        ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
        ArrayList serviceInterfaceRulesList = null;
        String sqlSelect = serviceSelectorInterfaceMappingQry.loadServiceInterfaceMappingRuleListQry();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceInterfaceMappingRuleList", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_serviceType);
            rs = pstmt.executeQuery();
           
            serviceInterfaceRulesList = new ArrayList();
            int index = 0;

            while (rs.next()) {
                serviceSelectorInterfaceMappingVO = new ServiceSelectorInterfaceMappingVO();
                serviceSelectorInterfaceMappingVO.setServiceInterfaceMappngID(rs.getString("srv_selector_interface_id"));
                serviceSelectorInterfaceMappingVO.setServiceType(rs.getString("service_type"));
                serviceSelectorInterfaceMappingVO.setSelectorCode(rs.getString("selector_code"));
                serviceSelectorInterfaceMappingVO.setNetworkCode(rs.getString("network_code"));
                serviceSelectorInterfaceMappingVO.setInterfaceID(rs.getString("interface_id"));
                serviceSelectorInterfaceMappingVO.setAction(rs.getString("action"));
                serviceSelectorInterfaceMappingVO.setMethodType(rs.getString("method_type"));
                serviceSelectorInterfaceMappingVO.setPrefixSeries(rs.getString("series"));
                serviceSelectorInterfaceMappingVO.setModifiedOn(rs.getDate("modified_on"));
                serviceSelectorInterfaceMappingVO.setModifiedBy(rs.getString("modified_by"));
                serviceSelectorInterfaceMappingVO.setCreatedOn(rs.getDate("created_on"));
                serviceSelectorInterfaceMappingVO.setCreatedBy(rs.getString("created_by"));
                serviceSelectorInterfaceMappingVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                serviceSelectorInterfaceMappingVO.setRowID("" + ++index);
                serviceInterfaceRulesList.add(serviceSelectorInterfaceMappingVO);
            }
            size=serviceInterfaceRulesList.size();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceInterfaceMappingRuleList]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSrvInterfaceMappingRuleList()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceInterfaceMappingRuleList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: serviceInterfaceRulesList.size=" + size);
            }
        }
        return serviceInterfaceRulesList;
    }

    public int updatePrdServiceInterfaceMapping(Connection p_con, ArrayList p_serviceMappingList) throws BTSLBaseException {
        final String methodName = "updatePrdServiceInterfaceMapping";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_serviceMappingList:" + p_serviceMappingList.size());
        }
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtDelete = null;
        int insertCount = 0;
        int deleteCount = 0;
        ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
        try {
            StringBuilder deleteQueryBuff = new StringBuilder();
            deleteQueryBuff.append("DELETE FROM  svc_setor_intfc_mapping ");
            deleteQueryBuff.append(" WHERE service_type = ? AND network_code=? AND selector_code=? AND interface_id=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "deleteQueryBuff query:" + deleteQueryBuff);
            }

            StringBuilder insertQueryBuff = new StringBuilder();
            insertQueryBuff.append("INSERT INTO svc_setor_intfc_mapping (srv_selector_interface_id,service_type,selector_code,network_code, ");
            insertQueryBuff.append("interface_id,method_type,created_on,created_by,modified_on,modified_by,action,prefix_id ) ");
            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");

            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addPrdServiceInterfaceMapping", "Insert query:" + insertQuery);
            }

            pstmtDelete = p_con.prepareStatement(deleteQueryBuff.toString());

            pstmtInsert = p_con.prepareStatement(insertQuery);
            // // This will delete the new existing VO LIST IN ANY
            for (int i = 0, j = p_serviceMappingList.size(); i < j; i++) {
                serviceSelectorInterfaceMappingVO = (ServiceSelectorInterfaceMappingVO) p_serviceMappingList.get(i);


                pstmtDelete.setString(1, serviceSelectorInterfaceMappingVO.getServiceType());
                pstmtDelete.setString(2, serviceSelectorInterfaceMappingVO.getNetworkCode());
                pstmtDelete.setString(3, serviceSelectorInterfaceMappingVO.getSelectorCode());
                pstmtDelete.setString(4, serviceSelectorInterfaceMappingVO.getInterfaceID());
                deleteCount = pstmtDelete.executeUpdate();
                p_con.commit();

            }
            // //This will insert the new VO LIST

            for (int i = 0, j = p_serviceMappingList.size(); i < j; i++) {
                serviceSelectorInterfaceMappingVO = (ServiceSelectorInterfaceMappingVO) p_serviceMappingList.get(i);

                pstmtInsert.setString(1, String.valueOf(IDGenerator.getNextID(PretupsI.SERVICE_INTERFACE_MAPPING_ID_TYPE, TypesI.ALL)));
                pstmtInsert.setString(2, serviceSelectorInterfaceMappingVO.getServiceType());
                pstmtInsert.setString(3, serviceSelectorInterfaceMappingVO.getSelectorCode());
                pstmtInsert.setString(4, serviceSelectorInterfaceMappingVO.getNetworkCode());
                pstmtInsert.setString(5, serviceSelectorInterfaceMappingVO.getInterfaceID());
                pstmtInsert.setString(6, serviceSelectorInterfaceMappingVO.getMethodType());
                pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(serviceSelectorInterfaceMappingVO.getCreatedOn()));
                pstmtInsert.setString(8, serviceSelectorInterfaceMappingVO.getCreatedBy());
                pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(serviceSelectorInterfaceMappingVO.getModifiedOn()));
                pstmtInsert.setString(10, serviceSelectorInterfaceMappingVO.getModifiedBy());
                pstmtInsert.setString(11, serviceSelectorInterfaceMappingVO.getAction());
                pstmtInsert.setLong(12, serviceSelectorInterfaceMappingVO.getPrefixId());

                insertCount = pstmtInsert.executeUpdate();
                pstmtInsert.clearParameters();
                // check the status of the update
                if (insertCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

            if (insertCount < 0) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[updatePrdServiceInterfaceMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[updatePrdServiceInterfaceMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	try{
        		if (pstmtDelete!= null){
        			pstmtDelete.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting insertCount=" + insertCount);
            }
        }// end of finally
        return insertCount;
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @author arvinder.singh
     * @return
     */

    public int deletePrdServiceInterfaceMapping(Connection p_con, ArrayList p_serviceMappingList) throws BTSLBaseException {
        final String methodName = "deletePrdServiceInterfaceMapping";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_serviceMappingList:" + p_serviceMappingList.size());
        }
        PreparedStatement pstmtDelete = null;
        int[] deleteCount = { 0 };

        ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
        try {
            StringBuilder deleteQueryBuff = new StringBuilder();
            deleteQueryBuff.append("DELETE FROM  svc_setor_intfc_mapping ");
            deleteQueryBuff.append(" WHERE prefix_id=? AND service_type = ? AND network_code=? AND selector_code=? AND interface_id=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "deleteQueryBuff query:" + deleteQueryBuff);
            }
            pstmtDelete = p_con.prepareStatement(deleteQueryBuff.toString());

            for (int i = 0, j = p_serviceMappingList.size(); i < j; i++) {
                serviceSelectorInterfaceMappingVO = (ServiceSelectorInterfaceMappingVO) p_serviceMappingList.get(i);
                pstmtDelete.setString(1, Long.toString(serviceSelectorInterfaceMappingVO.getPrefixId()));
                pstmtDelete.setString(2, serviceSelectorInterfaceMappingVO.getServiceType());
                pstmtDelete.setString(3, serviceSelectorInterfaceMappingVO.getNetworkCode());
                pstmtDelete.setString(4, serviceSelectorInterfaceMappingVO.getSelectorCode());
                pstmtDelete.setString(5, serviceSelectorInterfaceMappingVO.getInterfaceID());

                pstmtDelete.addBatch();// adding the batch
            }
            deleteCount = pstmtDelete.executeBatch();// executing the batch
            if (deleteCount.length <= 0) {

                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[deletePrdServiceInterfaceMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[deletePrdServiceInterfaceMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	try{
        		if (pstmtDelete!= null){
        			pstmtDelete.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting deleteCount=" + deleteCount.length);
            }
        }// end of finally
        return deleteCount.length;
    }

    /**
     * @return
     * @throws BTSLBaseException
     *             ]
     * @author rahul.dutt
     *         this method loads service selector interface mapping from the
     *         database
     */
    public HashMap<String,ServiceSelectorInterfaceMappingVO> loadServSelInterfMappingCache() throws BTSLBaseException {

        final String methodName = "loadServSelInterfMappingCache()";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap servSelInterfMap = new HashMap();
        
        String sqlSelect=serviceSelectorInterfaceMappingQry.loadServSelInterfMappingCacheQry();

        if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMappingCache", "QUERY sqlSelect=" + sqlSelect);
        }
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.ALL);
            rs = pstmt.executeQuery();
            ServiceSelectorInterfaceMappingVO serSelIntMappVO = null;
            while (rs.next()) {
                serSelIntMappVO = new ServiceSelectorInterfaceMappingVO();
                serSelIntMappVO.setServiceType(rs.getString("service_type"));
                serSelIntMappVO.setSelectorCode(rs.getString("selector_code"));
                serSelIntMappVO.setNetworkCode(rs.getString("network_code"));
                serSelIntMappVO.setInterfaceID(rs.getString("interface_id"));
                serSelIntMappVO.setPrefixID(rs.getInt("prefix_id"));
                serSelIntMappVO.setAction(rs.getString("action"));
                serSelIntMappVO.setMethodType(rs.getString("method_type"));
                serSelIntMappVO.setCreatedOn(rs.getDate("created_on"));
                serSelIntMappVO.setCreatedBy(rs.getString("created_by"));
                serSelIntMappVO.setModifiedOn(rs.getDate("modified_on"));
                serSelIntMappVO.setModifiedBy(rs.getString("modified_by"));
                serSelIntMappVO.setServiceInterfaceMappngID(rs.getString("srv_selector_interface_id"));
                serSelIntMappVO.setHandlerClass(rs.getString("handler_class"));
                serSelIntMappVO.setUnderProcessMsgRequired(rs.getString("underprocess_msg_reqd"));
                serSelIntMappVO.setAllServiceClassID(rs.getString("service_class_id"));
                serSelIntMappVO.setExternalID(rs.getString("external_id"));
                serSelIntMappVO.setInterfaceStatus(rs.getString("status"));
                serSelIntMappVO.setStatusType(rs.getString("statustype"));
                serSelIntMappVO.setLanguage1Message(rs.getString("message_language1"));
                serSelIntMappVO.setLanguage2Message(rs.getString("message_language2"));
                serSelIntMappVO.setInterfaceTypeID(rs.getString("interface_type_id"));
                
                serSelIntMappVO.setSingleStep(rs.getString("single_state_transaction"));
                // VAS_VAS01_V_TH_1234
                servSelInterfMap.put(serSelIntMappVO.getServiceType() + "_" + serSelIntMappVO.getSelectorCode() + "_" + serSelIntMappVO.getAction() + "_" + serSelIntMappVO.getNetworkCode() + "_" + serSelIntMappVO.getPrefixId(), serSelIntMappVO);
                

                if (_log.isDebugEnabled()) {
                    _log.debug("loadServSelInterfMappingCache", "QUERY sqlSelect=" + serSelIntMappVO.getServiceType() + "_" + serSelIntMappVO.getSelectorCode() + "_" + serSelIntMappVO.getAction() + "_" + serSelIntMappVO.getNetworkCode() + "_" + serSelIntMappVO.getPrefixId()+"interface_typeID="+serSelIntMappVO.getInterfaceTypeID());
                }
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServSelInterfMappingCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServSelInterfMappingCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
           OracleUtil.closeQuietly(con);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: ServSelInterfMappingCache size=" + servSelInterfMap.size());
            }
        }
        return servSelInterfMap;
    }

}
